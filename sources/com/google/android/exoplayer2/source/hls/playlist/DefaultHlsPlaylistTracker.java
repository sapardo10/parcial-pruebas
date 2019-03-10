package com.google.android.exoplayer2.source.hls.playlist;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.source.MediaSourceEventListener$EventDispatcher;
import com.google.android.exoplayer2.source.hls.HlsDataSourceFactory;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker.Factory;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker.PlaylistEventListener;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker.PlaylistResetException;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker.PlaylistStuckException;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker.PrimaryPlaylistListener;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.upstream.Loader.Callback;
import com.google.android.exoplayer2.upstream.Loader.LoadErrorAction;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.upstream.ParsingLoadable.Parser;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.UriUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

public final class DefaultHlsPlaylistTracker implements HlsPlaylistTracker, Callback<ParsingLoadable<HlsPlaylist>> {
    public static final Factory FACTORY = -$$Lambda$lKTLOVxne0MoBOOliKH0gO2KDMM.INSTANCE;
    private static final double PLAYLIST_STUCK_TARGET_DURATION_COEFFICIENT = 3.5d;
    private final HlsDataSourceFactory dataSourceFactory;
    @Nullable
    private MediaSourceEventListener$EventDispatcher eventDispatcher;
    @Nullable
    private Loader initialPlaylistLoader;
    private long initialStartTimeUs;
    private boolean isLive;
    private final List<PlaylistEventListener> listeners;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    @Nullable
    private HlsMasterPlaylist masterPlaylist;
    @Nullable
    private Parser<HlsPlaylist> mediaPlaylistParser;
    private final IdentityHashMap<HlsUrl, MediaPlaylistBundle> playlistBundles;
    private final HlsPlaylistParserFactory playlistParserFactory;
    @Nullable
    private Handler playlistRefreshHandler;
    @Nullable
    private HlsUrl primaryHlsUrl;
    @Nullable
    private PrimaryPlaylistListener primaryPlaylistListener;
    @Nullable
    private HlsMediaPlaylist primaryUrlSnapshot;

    private final class MediaPlaylistBundle implements Callback<ParsingLoadable<HlsPlaylist>>, Runnable {
        private long blacklistUntilMs;
        private long earliestNextLoadTimeMs;
        private long lastSnapshotChangeMs;
        private long lastSnapshotLoadMs;
        private boolean loadPending;
        private final ParsingLoadable<HlsPlaylist> mediaPlaylistLoadable;
        private final Loader mediaPlaylistLoader = new Loader("DefaultHlsPlaylistTracker:MediaPlaylist");
        private IOException playlistError;
        private HlsMediaPlaylist playlistSnapshot;
        private final HlsUrl playlistUrl;

        public MediaPlaylistBundle(HlsUrl playlistUrl) {
            this.playlistUrl = playlistUrl;
            this.mediaPlaylistLoadable = new ParsingLoadable(DefaultHlsPlaylistTracker.this.dataSourceFactory.createDataSource(4), UriUtil.resolveToUri(DefaultHlsPlaylistTracker.this.masterPlaylist.baseUri, playlistUrl.url), 4, DefaultHlsPlaylistTracker.this.mediaPlaylistParser);
        }

        public HlsMediaPlaylist getPlaylistSnapshot() {
            return this.playlistSnapshot;
        }

        public boolean isSnapshotValid() {
            boolean z = false;
            if (this.playlistSnapshot == null) {
                return false;
            }
            long currentTimeMs = SystemClock.elapsedRealtime();
            long snapshotValidityDurationMs = Math.max(30000, C0555C.usToMs(this.playlistSnapshot.durationUs));
            if (!(this.playlistSnapshot.hasEndTag || this.playlistSnapshot.playlistType == 2 || this.playlistSnapshot.playlistType == 1)) {
                if (this.lastSnapshotLoadMs + snapshotValidityDurationMs <= currentTimeMs) {
                    return z;
                }
            }
            z = true;
            return z;
        }

        public void release() {
            this.mediaPlaylistLoader.release();
        }

        public void loadPlaylist() {
            this.blacklistUntilMs = 0;
            if (!this.loadPending) {
                if (!this.mediaPlaylistLoader.isLoading()) {
                    long currentTimeMs = SystemClock.elapsedRealtime();
                    if (currentTimeMs < this.earliestNextLoadTimeMs) {
                        this.loadPending = true;
                        DefaultHlsPlaylistTracker.this.playlistRefreshHandler.postDelayed(this, this.earliestNextLoadTimeMs - currentTimeMs);
                    } else {
                        loadPlaylistImmediately();
                    }
                }
            }
        }

        public void maybeThrowPlaylistRefreshError() throws IOException {
            this.mediaPlaylistLoader.maybeThrowError();
            IOException iOException = this.playlistError;
            if (iOException != null) {
                throw iOException;
            }
        }

        public void onLoadCompleted(ParsingLoadable<HlsPlaylist> loadable, long elapsedRealtimeMs, long loadDurationMs) {
            MediaPlaylistBundle mediaPlaylistBundle = this;
            HlsPlaylist result = (HlsPlaylist) loadable.getResult();
            if (result instanceof HlsMediaPlaylist) {
                processLoadedPlaylist((HlsMediaPlaylist) result, loadDurationMs);
                DefaultHlsPlaylistTracker.this.eventDispatcher.loadCompleted(loadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), 4, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
                return;
            }
            ParsingLoadable<HlsPlaylist> parsingLoadable = loadable;
            long j = loadDurationMs;
            mediaPlaylistBundle.playlistError = new ParserException("Loaded playlist has unexpected type.");
        }

        public void onLoadCanceled(ParsingLoadable<HlsPlaylist> loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
            DefaultHlsPlaylistTracker.this.eventDispatcher.loadCanceled(loadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), 4, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        }

        public LoadErrorAction onLoadError(ParsingLoadable<HlsPlaylist> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error, int errorCount) {
            boolean blacklistingFailed;
            LoadErrorAction loadErrorAction;
            ParsingLoadable<HlsPlaylist> parsingLoadable = loadable;
            long blacklistDurationMs = DefaultHlsPlaylistTracker.this.loadErrorHandlingPolicy.getBlacklistDurationMsFor(parsingLoadable.type, loadDurationMs, error, errorCount);
            boolean shouldBlacklist = blacklistDurationMs != C0555C.TIME_UNSET;
            if (!DefaultHlsPlaylistTracker.this.notifyPlaylistError(r0.playlistUrl, blacklistDurationMs)) {
                if (shouldBlacklist) {
                    blacklistingFailed = false;
                    if (shouldBlacklist) {
                        blacklistingFailed |= blacklistPlaylist(blacklistDurationMs);
                    }
                    if (blacklistingFailed) {
                        loadErrorAction = Loader.DONT_RETRY;
                    } else {
                        long retryDelay = DefaultHlsPlaylistTracker.this.loadErrorHandlingPolicy.getRetryDelayMsFor(parsingLoadable.type, loadDurationMs, error, errorCount);
                        loadErrorAction = retryDelay == C0555C.TIME_UNSET ? Loader.createRetryAction(false, retryDelay) : Loader.DONT_RETRY_FATAL;
                    }
                    DefaultHlsPlaylistTracker.this.eventDispatcher.loadError(parsingLoadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), 4, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded(), error, loadErrorAction.isRetry() ^ 1);
                    return loadErrorAction;
                }
            }
            blacklistingFailed = true;
            if (shouldBlacklist) {
                blacklistingFailed |= blacklistPlaylist(blacklistDurationMs);
            }
            if (blacklistingFailed) {
                loadErrorAction = Loader.DONT_RETRY;
            } else {
                long retryDelay2 = DefaultHlsPlaylistTracker.this.loadErrorHandlingPolicy.getRetryDelayMsFor(parsingLoadable.type, loadDurationMs, error, errorCount);
                if (retryDelay2 == C0555C.TIME_UNSET) {
                }
            }
            DefaultHlsPlaylistTracker.this.eventDispatcher.loadError(parsingLoadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), 4, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded(), error, loadErrorAction.isRetry() ^ 1);
            return loadErrorAction;
        }

        public void run() {
            this.loadPending = false;
            loadPlaylistImmediately();
        }

        private void loadPlaylistImmediately() {
            DefaultHlsPlaylistTracker.this.eventDispatcher.loadStarted(this.mediaPlaylistLoadable.dataSpec, this.mediaPlaylistLoadable.type, this.mediaPlaylistLoader.startLoading(this.mediaPlaylistLoadable, this, DefaultHlsPlaylistTracker.this.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(this.mediaPlaylistLoadable.type)));
        }

        private void processLoadedPlaylist(HlsMediaPlaylist loadedPlaylist, long loadDurationMs) {
            HlsMediaPlaylist hlsMediaPlaylist = loadedPlaylist;
            HlsMediaPlaylist oldPlaylist = this.playlistSnapshot;
            long currentTimeMs = SystemClock.elapsedRealtime();
            this.lastSnapshotLoadMs = currentTimeMs;
            this.playlistSnapshot = DefaultHlsPlaylistTracker.this.getLatestPlaylistSnapshot(oldPlaylist, hlsMediaPlaylist);
            HlsMediaPlaylist hlsMediaPlaylist2 = this.playlistSnapshot;
            if (hlsMediaPlaylist2 != oldPlaylist) {
                r0.playlistError = null;
                r0.lastSnapshotChangeMs = currentTimeMs;
                DefaultHlsPlaylistTracker.this.onPlaylistUpdated(r0.playlistUrl, hlsMediaPlaylist2);
            } else if (!hlsMediaPlaylist2.hasEndTag) {
                if (hlsMediaPlaylist.mediaSequence + ((long) hlsMediaPlaylist.segments.size()) < r0.playlistSnapshot.mediaSequence) {
                    r0.playlistError = new PlaylistResetException(r0.playlistUrl.url);
                    DefaultHlsPlaylistTracker.this.notifyPlaylistError(r0.playlistUrl, C0555C.TIME_UNSET);
                } else {
                    double d = (double) (currentTimeMs - r0.lastSnapshotChangeMs);
                    double usToMs = (double) C0555C.usToMs(r0.playlistSnapshot.targetDurationUs);
                    Double.isNaN(usToMs);
                    if (d > usToMs * DefaultHlsPlaylistTracker.PLAYLIST_STUCK_TARGET_DURATION_COEFFICIENT) {
                        r0.playlistError = new PlaylistStuckException(r0.playlistUrl.url);
                        long blacklistDurationMs = DefaultHlsPlaylistTracker.this.loadErrorHandlingPolicy.getBlacklistDurationMsFor(4, loadDurationMs, r0.playlistError, 1);
                        DefaultHlsPlaylistTracker.this.notifyPlaylistError(r0.playlistUrl, blacklistDurationMs);
                        if (blacklistDurationMs != C0555C.TIME_UNSET) {
                            blacklistPlaylist(blacklistDurationMs);
                        }
                    }
                }
            }
            hlsMediaPlaylist2 = r0.playlistSnapshot;
            r0.earliestNextLoadTimeMs = C0555C.usToMs(hlsMediaPlaylist2 != oldPlaylist ? hlsMediaPlaylist2.targetDurationUs : hlsMediaPlaylist2.targetDurationUs / 2) + currentTimeMs;
            if (r0.playlistUrl == DefaultHlsPlaylistTracker.this.primaryHlsUrl && !r0.playlistSnapshot.hasEndTag) {
                loadPlaylist();
            }
        }

        private boolean blacklistPlaylist(long blacklistDurationMs) {
            this.blacklistUntilMs = SystemClock.elapsedRealtime() + blacklistDurationMs;
            return DefaultHlsPlaylistTracker.this.primaryHlsUrl == this.playlistUrl && !DefaultHlsPlaylistTracker.this.maybeSelectNewPrimaryUrl();
        }
    }

    @Deprecated
    public DefaultHlsPlaylistTracker(HlsDataSourceFactory dataSourceFactory, LoadErrorHandlingPolicy loadErrorHandlingPolicy, Parser<HlsPlaylist> playlistParser) {
        this(dataSourceFactory, loadErrorHandlingPolicy, createFixedFactory(playlistParser));
    }

    public DefaultHlsPlaylistTracker(HlsDataSourceFactory dataSourceFactory, LoadErrorHandlingPolicy loadErrorHandlingPolicy, HlsPlaylistParserFactory playlistParserFactory) {
        this.dataSourceFactory = dataSourceFactory;
        this.playlistParserFactory = playlistParserFactory;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        this.listeners = new ArrayList();
        this.playlistBundles = new IdentityHashMap();
        this.initialStartTimeUs = C0555C.TIME_UNSET;
    }

    public void start(Uri initialPlaylistUri, MediaSourceEventListener$EventDispatcher eventDispatcher, PrimaryPlaylistListener primaryPlaylistListener) {
        this.playlistRefreshHandler = new Handler();
        this.eventDispatcher = eventDispatcher;
        this.primaryPlaylistListener = primaryPlaylistListener;
        ParsingLoadable<HlsPlaylist> masterPlaylistLoadable = new ParsingLoadable(this.dataSourceFactory.createDataSource(4), initialPlaylistUri, 4, this.playlistParserFactory.createPlaylistParser());
        Assertions.checkState(this.initialPlaylistLoader == null);
        this.initialPlaylistLoader = new Loader("DefaultHlsPlaylistTracker:MasterPlaylist");
        eventDispatcher.loadStarted(masterPlaylistLoadable.dataSpec, masterPlaylistLoadable.type, this.initialPlaylistLoader.startLoading(masterPlaylistLoadable, this, this.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(masterPlaylistLoadable.type)));
    }

    public void stop() {
        this.primaryHlsUrl = null;
        this.primaryUrlSnapshot = null;
        this.masterPlaylist = null;
        this.initialStartTimeUs = C0555C.TIME_UNSET;
        this.initialPlaylistLoader.release();
        this.initialPlaylistLoader = null;
        for (MediaPlaylistBundle bundle : this.playlistBundles.values()) {
            bundle.release();
        }
        this.playlistRefreshHandler.removeCallbacksAndMessages(null);
        this.playlistRefreshHandler = null;
        this.playlistBundles.clear();
    }

    public void addListener(PlaylistEventListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(PlaylistEventListener listener) {
        this.listeners.remove(listener);
    }

    @Nullable
    public HlsMasterPlaylist getMasterPlaylist() {
        return this.masterPlaylist;
    }

    public HlsMediaPlaylist getPlaylistSnapshot(HlsUrl url, boolean isForPlayback) {
        HlsMediaPlaylist snapshot = ((MediaPlaylistBundle) this.playlistBundles.get(url)).getPlaylistSnapshot();
        if (snapshot != null && isForPlayback) {
            maybeSetPrimaryUrl(url);
        }
        return snapshot;
    }

    public long getInitialStartTimeUs() {
        return this.initialStartTimeUs;
    }

    public boolean isSnapshotValid(HlsUrl url) {
        return ((MediaPlaylistBundle) this.playlistBundles.get(url)).isSnapshotValid();
    }

    public void maybeThrowPrimaryPlaylistRefreshError() throws IOException {
        Loader loader = this.initialPlaylistLoader;
        if (loader != null) {
            loader.maybeThrowError();
        }
        HlsUrl hlsUrl = this.primaryHlsUrl;
        if (hlsUrl != null) {
            maybeThrowPlaylistRefreshError(hlsUrl);
        }
    }

    public void maybeThrowPlaylistRefreshError(HlsUrl url) throws IOException {
        ((MediaPlaylistBundle) this.playlistBundles.get(url)).maybeThrowPlaylistRefreshError();
    }

    public void refreshPlaylist(HlsUrl url) {
        ((MediaPlaylistBundle) this.playlistBundles.get(url)).loadPlaylist();
    }

    public boolean isLive() {
        return this.isLive;
    }

    public void onLoadCompleted(ParsingLoadable<HlsPlaylist> loadable, long elapsedRealtimeMs, long loadDurationMs) {
        HlsMasterPlaylist masterPlaylist;
        DefaultHlsPlaylistTracker defaultHlsPlaylistTracker = this;
        HlsPlaylist result = (HlsPlaylist) loadable.getResult();
        boolean isMediaPlaylist = result instanceof HlsMediaPlaylist;
        if (isMediaPlaylist) {
            masterPlaylist = HlsMasterPlaylist.createSingleVariantMasterPlaylist(result.baseUri);
        } else {
            masterPlaylist = (HlsMasterPlaylist) result;
        }
        defaultHlsPlaylistTracker.masterPlaylist = masterPlaylist;
        defaultHlsPlaylistTracker.mediaPlaylistParser = defaultHlsPlaylistTracker.playlistParserFactory.createPlaylistParser(masterPlaylist);
        defaultHlsPlaylistTracker.primaryHlsUrl = (HlsUrl) masterPlaylist.variants.get(0);
        ArrayList<HlsUrl> urls = new ArrayList();
        urls.addAll(masterPlaylist.variants);
        urls.addAll(masterPlaylist.audios);
        urls.addAll(masterPlaylist.subtitles);
        createBundles(urls);
        MediaPlaylistBundle primaryBundle = (MediaPlaylistBundle) defaultHlsPlaylistTracker.playlistBundles.get(defaultHlsPlaylistTracker.primaryHlsUrl);
        if (isMediaPlaylist) {
            primaryBundle.processLoadedPlaylist((HlsMediaPlaylist) result, loadDurationMs);
        } else {
            long j = loadDurationMs;
            primaryBundle.loadPlaylist();
        }
        defaultHlsPlaylistTracker.eventDispatcher.loadCompleted(loadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), 4, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
    }

    public void onLoadCanceled(ParsingLoadable<HlsPlaylist> loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
        this.eventDispatcher.loadCanceled(loadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), 4, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
    }

    public LoadErrorAction onLoadError(ParsingLoadable<HlsPlaylist> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error, int errorCount) {
        ParsingLoadable<HlsPlaylist> parsingLoadable = loadable;
        long retryDelayMs = this.loadErrorHandlingPolicy.getRetryDelayMsFor(parsingLoadable.type, loadDurationMs, error, errorCount);
        boolean isFatal = retryDelayMs == C0555C.TIME_UNSET;
        r0.eventDispatcher.loadError(parsingLoadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), 4, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded(), error, isFatal);
        if (isFatal) {
            return Loader.DONT_RETRY_FATAL;
        }
        return Loader.createRetryAction(false, retryDelayMs);
    }

    private boolean maybeSelectNewPrimaryUrl() {
        List<HlsUrl> variants = this.masterPlaylist.variants;
        int variantsSize = variants.size();
        long currentTimeMs = SystemClock.elapsedRealtime();
        for (int i = 0; i < variantsSize; i++) {
            MediaPlaylistBundle bundle = (MediaPlaylistBundle) this.playlistBundles.get(variants.get(i));
            if (currentTimeMs > bundle.blacklistUntilMs) {
                this.primaryHlsUrl = bundle.playlistUrl;
                bundle.loadPlaylist();
                return true;
            }
        }
        return false;
    }

    private void maybeSetPrimaryUrl(HlsUrl url) {
        if (url != this.primaryHlsUrl) {
            if (this.masterPlaylist.variants.contains(url)) {
                HlsMediaPlaylist hlsMediaPlaylist = this.primaryUrlSnapshot;
                if (hlsMediaPlaylist == null || !hlsMediaPlaylist.hasEndTag) {
                    this.primaryHlsUrl = url;
                    ((MediaPlaylistBundle) this.playlistBundles.get(this.primaryHlsUrl)).loadPlaylist();
                }
            }
        }
    }

    private void createBundles(List<HlsUrl> urls) {
        int listSize = urls.size();
        for (int i = 0; i < listSize; i++) {
            HlsUrl url = (HlsUrl) urls.get(i);
            this.playlistBundles.put(url, new MediaPlaylistBundle(url));
        }
    }

    private void onPlaylistUpdated(HlsUrl url, HlsMediaPlaylist newSnapshot) {
        if (url == this.primaryHlsUrl) {
            if (this.primaryUrlSnapshot == null) {
                this.isLive = newSnapshot.hasEndTag ^ 1;
                this.initialStartTimeUs = newSnapshot.startTimeUs;
            }
            this.primaryUrlSnapshot = newSnapshot;
            this.primaryPlaylistListener.onPrimaryPlaylistRefreshed(newSnapshot);
        }
        int listenersSize = this.listeners.size();
        for (int i = 0; i < listenersSize; i++) {
            ((PlaylistEventListener) this.listeners.get(i)).onPlaylistChanged();
        }
    }

    private boolean notifyPlaylistError(HlsUrl playlistUrl, long blacklistDurationMs) {
        boolean anyBlacklistingFailed = false;
        for (int i = 0; i < this.listeners.size(); i++) {
            anyBlacklistingFailed |= ((PlaylistEventListener) this.listeners.get(i)).onPlaylistError(playlistUrl, blacklistDurationMs) ^ 1;
        }
        return anyBlacklistingFailed;
    }

    private HlsMediaPlaylist getLatestPlaylistSnapshot(HlsMediaPlaylist oldPlaylist, HlsMediaPlaylist loadedPlaylist) {
        if (loadedPlaylist.isNewerThan(oldPlaylist)) {
            return loadedPlaylist.copyWith(getLoadedPlaylistStartTimeUs(oldPlaylist, loadedPlaylist), getLoadedPlaylistDiscontinuitySequence(oldPlaylist, loadedPlaylist));
        }
        if (loadedPlaylist.hasEndTag) {
            return oldPlaylist.copyWithEndTag();
        }
        return oldPlaylist;
    }

    private long getLoadedPlaylistStartTimeUs(HlsMediaPlaylist oldPlaylist, HlsMediaPlaylist loadedPlaylist) {
        if (loadedPlaylist.hasProgramDateTime) {
            return loadedPlaylist.startTimeUs;
        }
        HlsMediaPlaylist hlsMediaPlaylist = this.primaryUrlSnapshot;
        long primarySnapshotStartTimeUs = hlsMediaPlaylist != null ? hlsMediaPlaylist.startTimeUs : 0;
        if (oldPlaylist == null) {
            return primarySnapshotStartTimeUs;
        }
        int oldPlaylistSize = oldPlaylist.segments.size();
        Segment firstOldOverlappingSegment = getFirstOldOverlappingSegment(oldPlaylist, loadedPlaylist);
        if (firstOldOverlappingSegment != null) {
            return oldPlaylist.startTimeUs + firstOldOverlappingSegment.relativeStartTimeUs;
        }
        if (((long) oldPlaylistSize) == loadedPlaylist.mediaSequence - oldPlaylist.mediaSequence) {
            return oldPlaylist.getEndTimeUs();
        }
        return primarySnapshotStartTimeUs;
    }

    private int getLoadedPlaylistDiscontinuitySequence(HlsMediaPlaylist oldPlaylist, HlsMediaPlaylist loadedPlaylist) {
        if (loadedPlaylist.hasDiscontinuitySequence) {
            return loadedPlaylist.discontinuitySequence;
        }
        HlsMediaPlaylist hlsMediaPlaylist = this.primaryUrlSnapshot;
        int primaryUrlDiscontinuitySequence = hlsMediaPlaylist != null ? hlsMediaPlaylist.discontinuitySequence : 0;
        if (oldPlaylist == null) {
            return primaryUrlDiscontinuitySequence;
        }
        Segment firstOldOverlappingSegment = getFirstOldOverlappingSegment(oldPlaylist, loadedPlaylist);
        if (firstOldOverlappingSegment != null) {
            return (oldPlaylist.discontinuitySequence + firstOldOverlappingSegment.relativeDiscontinuitySequence) - ((Segment) loadedPlaylist.segments.get(0)).relativeDiscontinuitySequence;
        }
        return primaryUrlDiscontinuitySequence;
    }

    private static Segment getFirstOldOverlappingSegment(HlsMediaPlaylist oldPlaylist, HlsMediaPlaylist loadedPlaylist) {
        int mediaSequenceOffset = (int) (loadedPlaylist.mediaSequence - oldPlaylist.mediaSequence);
        List<Segment> oldSegments = oldPlaylist.segments;
        return mediaSequenceOffset < oldSegments.size() ? (Segment) oldSegments.get(mediaSequenceOffset) : null;
    }

    private static HlsPlaylistParserFactory createFixedFactory(final Parser<HlsPlaylist> playlistParser) {
        return new HlsPlaylistParserFactory() {
            public Parser<HlsPlaylist> createPlaylistParser() {
                return playlistParser;
            }

            public Parser<HlsPlaylist> createPlaylistParser(HlsMasterPlaylist masterPlaylist) {
                return playlistParser;
            }
        };
    }
}
