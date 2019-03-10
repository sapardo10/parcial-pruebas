package com.google.android.exoplayer2.source.smoothstreaming;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.BaseMediaSource;
import com.google.android.exoplayer2.source.CompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.DefaultCompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.MediaSourceEventListener$EventDispatcher;
import com.google.android.exoplayer2.source.SinglePeriodTimeline;
import com.google.android.exoplayer2.source.ads.AdsMediaSource.MediaSourceFactory;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest.StreamElement;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsUtil;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.upstream.Loader.Callback;
import com.google.android.exoplayer2.upstream.Loader.LoadErrorAction;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower.Dummy;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.upstream.ParsingLoadable.Parser;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.util.ArrayList;

public final class SsMediaSource extends BaseMediaSource implements Callback<ParsingLoadable<SsManifest>> {
    public static final long DEFAULT_LIVE_PRESENTATION_DELAY_MS = 30000;
    private static final int MINIMUM_MANIFEST_REFRESH_PERIOD_MS = 5000;
    private static final long MIN_LIVE_DEFAULT_START_POSITION_US = 5000000;
    private final com.google.android.exoplayer2.source.smoothstreaming.SsChunkSource.Factory chunkSourceFactory;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private final long livePresentationDelayMs;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    private SsManifest manifest;
    private DataSource manifestDataSource;
    private final com.google.android.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory;
    private final MediaSourceEventListener$EventDispatcher manifestEventDispatcher;
    private long manifestLoadStartTimestamp;
    private Loader manifestLoader;
    private LoaderErrorThrower manifestLoaderErrorThrower;
    private final Parser<? extends SsManifest> manifestParser;
    private Handler manifestRefreshHandler;
    private final Uri manifestUri;
    private final ArrayList<SsMediaPeriod> mediaPeriods;
    @Nullable
    private TransferListener mediaTransferListener;
    private final boolean sideloadedManifest;
    @Nullable
    private final Object tag;

    public static final class Factory implements MediaSourceFactory {
        private final com.google.android.exoplayer2.source.smoothstreaming.SsChunkSource.Factory chunkSourceFactory;
        private CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
        private boolean isCreateCalled;
        private long livePresentationDelayMs;
        private LoadErrorHandlingPolicy loadErrorHandlingPolicy;
        @Nullable
        private final com.google.android.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory;
        @Nullable
        private Parser<? extends SsManifest> manifestParser;
        @Nullable
        private Object tag;

        public Factory(com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory) {
            this(new com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource.Factory(dataSourceFactory), dataSourceFactory);
        }

        public Factory(com.google.android.exoplayer2.source.smoothstreaming.SsChunkSource.Factory chunkSourceFactory, @Nullable com.google.android.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory) {
            this.chunkSourceFactory = (com.google.android.exoplayer2.source.smoothstreaming.SsChunkSource.Factory) Assertions.checkNotNull(chunkSourceFactory);
            this.manifestDataSourceFactory = manifestDataSourceFactory;
            this.loadErrorHandlingPolicy = new DefaultLoadErrorHandlingPolicy();
            this.livePresentationDelayMs = 30000;
            this.compositeSequenceableLoaderFactory = new DefaultCompositeSequenceableLoaderFactory();
        }

        public Factory setTag(Object tag) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.tag = tag;
            return this;
        }

        @Deprecated
        public Factory setMinLoadableRetryCount(int minLoadableRetryCount) {
            return setLoadErrorHandlingPolicy(new DefaultLoadErrorHandlingPolicy(minLoadableRetryCount));
        }

        public Factory setLoadErrorHandlingPolicy(LoadErrorHandlingPolicy loadErrorHandlingPolicy) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
            return this;
        }

        public Factory setLivePresentationDelayMs(long livePresentationDelayMs) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.livePresentationDelayMs = livePresentationDelayMs;
            return this;
        }

        public Factory setManifestParser(Parser<? extends SsManifest> manifestParser) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.manifestParser = (Parser) Assertions.checkNotNull(manifestParser);
            return this;
        }

        public Factory setCompositeSequenceableLoaderFactory(CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.compositeSequenceableLoaderFactory = (CompositeSequenceableLoaderFactory) Assertions.checkNotNull(compositeSequenceableLoaderFactory);
            return this;
        }

        public SsMediaSource createMediaSource(SsManifest manifest) {
            Assertions.checkArgument(manifest.isLive ^ true);
            this.isCreateCalled = true;
            return new SsMediaSource(manifest, null, null, null, this.chunkSourceFactory, this.compositeSequenceableLoaderFactory, this.loadErrorHandlingPolicy, this.livePresentationDelayMs, this.tag);
        }

        @Deprecated
        public SsMediaSource createMediaSource(SsManifest manifest, @Nullable Handler eventHandler, @Nullable MediaSourceEventListener eventListener) {
            SsMediaSource mediaSource = createMediaSource(manifest);
            if (eventHandler != null && eventListener != null) {
                mediaSource.addEventListener(eventHandler, eventListener);
            }
            return mediaSource;
        }

        public SsMediaSource createMediaSource(Uri manifestUri) {
            this.isCreateCalled = true;
            if (this.manifestParser == null) {
                this.manifestParser = new SsManifestParser();
            }
            return new SsMediaSource(null, (Uri) Assertions.checkNotNull(manifestUri), this.manifestDataSourceFactory, this.manifestParser, this.chunkSourceFactory, this.compositeSequenceableLoaderFactory, this.loadErrorHandlingPolicy, this.livePresentationDelayMs, this.tag);
        }

        @Deprecated
        public SsMediaSource createMediaSource(Uri manifestUri, @Nullable Handler eventHandler, @Nullable MediaSourceEventListener eventListener) {
            SsMediaSource mediaSource = createMediaSource(manifestUri);
            if (eventHandler != null && eventListener != null) {
                mediaSource.addEventListener(eventHandler, eventListener);
            }
            return mediaSource;
        }

        public int[] getSupportedTypes() {
            return new int[]{1};
        }
    }

    static {
        ExoPlayerLibraryInfo.registerModule("goog.exo.smoothstreaming");
    }

    @Deprecated
    public SsMediaSource(SsManifest manifest, com.google.android.exoplayer2.source.smoothstreaming.SsChunkSource.Factory chunkSourceFactory, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifest, chunkSourceFactory, 3, eventHandler, eventListener);
    }

    @Deprecated
    public SsMediaSource(SsManifest manifest, com.google.android.exoplayer2.source.smoothstreaming.SsChunkSource.Factory chunkSourceFactory, int minLoadableRetryCount, Handler eventHandler, MediaSourceEventListener eventListener) {
        Handler handler = eventHandler;
        MediaSourceEventListener mediaSourceEventListener = eventListener;
        this(manifest, null, null, null, chunkSourceFactory, new DefaultCompositeSequenceableLoaderFactory(), new DefaultLoadErrorHandlingPolicy(minLoadableRetryCount), 30000, null);
        if (handler == null || mediaSourceEventListener == null) {
            SsMediaSource ssMediaSource = this;
            return;
        }
        ssMediaSource = this;
        addEventListener(handler, mediaSourceEventListener);
    }

    @Deprecated
    public SsMediaSource(Uri manifestUri, com.google.android.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory, com.google.android.exoplayer2.source.smoothstreaming.SsChunkSource.Factory chunkSourceFactory, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifestUri, manifestDataSourceFactory, chunkSourceFactory, 3, 30000, eventHandler, eventListener);
    }

    @Deprecated
    public SsMediaSource(Uri manifestUri, com.google.android.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory, com.google.android.exoplayer2.source.smoothstreaming.SsChunkSource.Factory chunkSourceFactory, int minLoadableRetryCount, long livePresentationDelayMs, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifestUri, manifestDataSourceFactory, new SsManifestParser(), chunkSourceFactory, minLoadableRetryCount, livePresentationDelayMs, eventHandler, eventListener);
    }

    @Deprecated
    public SsMediaSource(Uri manifestUri, com.google.android.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory, Parser<? extends SsManifest> manifestParser, com.google.android.exoplayer2.source.smoothstreaming.SsChunkSource.Factory chunkSourceFactory, int minLoadableRetryCount, long livePresentationDelayMs, Handler eventHandler, MediaSourceEventListener eventListener) {
        Handler handler = eventHandler;
        MediaSourceEventListener mediaSourceEventListener = eventListener;
        this(null, manifestUri, manifestDataSourceFactory, manifestParser, chunkSourceFactory, new DefaultCompositeSequenceableLoaderFactory(), new DefaultLoadErrorHandlingPolicy(minLoadableRetryCount), livePresentationDelayMs, null);
        if (handler == null || mediaSourceEventListener == null) {
            SsMediaSource ssMediaSource = this;
            return;
        }
        ssMediaSource = this;
        addEventListener(handler, mediaSourceEventListener);
    }

    private SsMediaSource(SsManifest manifest, Uri manifestUri, com.google.android.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory, Parser<? extends SsManifest> manifestParser, com.google.android.exoplayer2.source.smoothstreaming.SsChunkSource.Factory chunkSourceFactory, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, LoadErrorHandlingPolicy loadErrorHandlingPolicy, long livePresentationDelayMs, @Nullable Object tag) {
        boolean z;
        boolean z2 = false;
        if (manifest != null) {
            if (manifest.isLive) {
                z = false;
                Assertions.checkState(z);
                this.manifest = manifest;
                this.manifestUri = manifestUri != null ? null : SsUtil.fixManifestUri(manifestUri);
                this.manifestDataSourceFactory = manifestDataSourceFactory;
                this.manifestParser = manifestParser;
                this.chunkSourceFactory = chunkSourceFactory;
                this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
                this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
                this.livePresentationDelayMs = livePresentationDelayMs;
                this.manifestEventDispatcher = createEventDispatcher(null);
                this.tag = tag;
                if (manifest != null) {
                    z2 = true;
                }
                this.sideloadedManifest = z2;
                this.mediaPeriods = new ArrayList();
            }
        }
        z = true;
        Assertions.checkState(z);
        this.manifest = manifest;
        if (manifestUri != null) {
        }
        this.manifestUri = manifestUri != null ? null : SsUtil.fixManifestUri(manifestUri);
        this.manifestDataSourceFactory = manifestDataSourceFactory;
        this.manifestParser = manifestParser;
        this.chunkSourceFactory = chunkSourceFactory;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        this.livePresentationDelayMs = livePresentationDelayMs;
        this.manifestEventDispatcher = createEventDispatcher(null);
        this.tag = tag;
        if (manifest != null) {
            z2 = true;
        }
        this.sideloadedManifest = z2;
        this.mediaPeriods = new ArrayList();
    }

    @Nullable
    public Object getTag() {
        return this.tag;
    }

    public void prepareSourceInternal(ExoPlayer player, boolean isTopLevelSource, @Nullable TransferListener mediaTransferListener) {
        this.mediaTransferListener = mediaTransferListener;
        if (this.sideloadedManifest) {
            this.manifestLoaderErrorThrower = new Dummy();
            processManifest();
            return;
        }
        this.manifestDataSource = this.manifestDataSourceFactory.createDataSource();
        this.manifestLoader = new Loader("Loader:Manifest");
        this.manifestLoaderErrorThrower = this.manifestLoader;
        this.manifestRefreshHandler = new Handler();
        startLoadingManifest();
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
        this.manifestLoaderErrorThrower.maybeThrowError();
    }

    public MediaPeriod createPeriod(MediaPeriodId id, Allocator allocator) {
        SsMediaPeriod period = new SsMediaPeriod(this.manifest, this.chunkSourceFactory, this.mediaTransferListener, this.compositeSequenceableLoaderFactory, this.loadErrorHandlingPolicy, createEventDispatcher(id), this.manifestLoaderErrorThrower, allocator);
        this.mediaPeriods.add(period);
        return period;
    }

    public void releasePeriod(MediaPeriod period) {
        ((SsMediaPeriod) period).release();
        this.mediaPeriods.remove(period);
    }

    public void releaseSourceInternal() {
        this.manifest = this.sideloadedManifest ? this.manifest : null;
        this.manifestDataSource = null;
        this.manifestLoadStartTimestamp = 0;
        Loader loader = this.manifestLoader;
        if (loader != null) {
            loader.release();
            this.manifestLoader = null;
        }
        Handler handler = this.manifestRefreshHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            this.manifestRefreshHandler = null;
        }
    }

    public void onLoadCompleted(ParsingLoadable<SsManifest> loadable, long elapsedRealtimeMs, long loadDurationMs) {
        ParsingLoadable<SsManifest> parsingLoadable = loadable;
        this.manifestEventDispatcher.loadCompleted(parsingLoadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), parsingLoadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        this.manifest = (SsManifest) loadable.getResult();
        this.manifestLoadStartTimestamp = elapsedRealtimeMs - loadDurationMs;
        processManifest();
        scheduleManifestRefresh();
    }

    public void onLoadCanceled(ParsingLoadable<SsManifest> loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
        ParsingLoadable<SsManifest> parsingLoadable = loadable;
        this.manifestEventDispatcher.loadCanceled(parsingLoadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), parsingLoadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
    }

    public LoadErrorAction onLoadError(ParsingLoadable<SsManifest> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error, int errorCount) {
        ParsingLoadable<SsManifest> parsingLoadable = loadable;
        boolean isFatal = error instanceof ParserException;
        this.manifestEventDispatcher.loadError(parsingLoadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), parsingLoadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded(), error, isFatal);
        return isFatal ? Loader.DONT_RETRY_FATAL : Loader.RETRY;
    }

    private void processManifest() {
        SsMediaSource ssMediaSource = this;
        for (int i = 0; i < ssMediaSource.mediaPeriods.size(); i++) {
            ((SsMediaPeriod) ssMediaSource.mediaPeriods.get(i)).updateManifest(ssMediaSource.manifest);
        }
        long endTimeUs = Long.MIN_VALUE;
        long startTimeUs = Long.MAX_VALUE;
        for (StreamElement element : ssMediaSource.manifest.streamElements) {
            if (element.chunkCount > 0) {
                startTimeUs = Math.min(startTimeUs, element.getStartTimeUs(0));
                endTimeUs = Math.max(endTimeUs, element.getStartTimeUs(element.chunkCount - 1) + element.getChunkDurationUs(element.chunkCount - 1));
            }
        }
        Timeline singlePeriodTimeline;
        long j;
        if (startTimeUs == Long.MAX_VALUE) {
            singlePeriodTimeline = new SinglePeriodTimeline(ssMediaSource.manifest.isLive ? C0555C.TIME_UNSET : 0, 0, 0, 0, true, ssMediaSource.manifest.isLive, ssMediaSource.tag);
            j = endTimeUs;
        } else if (ssMediaSource.manifest.isLive) {
            if (ssMediaSource.manifest.dvrWindowLengthUs != C0555C.TIME_UNSET && ssMediaSource.manifest.dvrWindowLengthUs > 0) {
                startTimeUs = Math.max(startTimeUs, endTimeUs - ssMediaSource.manifest.dvrWindowLengthUs);
            }
            durationUs = endTimeUs - startTimeUs;
            long defaultStartPositionUs = durationUs - C0555C.msToUs(ssMediaSource.livePresentationDelayMs);
            if (defaultStartPositionUs < MIN_LIVE_DEFAULT_START_POSITION_US) {
                defaultStartPositionUs = Math.min(MIN_LIVE_DEFAULT_START_POSITION_US, durationUs / 2);
            }
            singlePeriodTimeline = new SinglePeriodTimeline(C0555C.TIME_UNSET, durationUs, startTimeUs, defaultStartPositionUs, true, true, ssMediaSource.tag);
            j = endTimeUs;
        } else {
            durationUs = ssMediaSource.manifest.durationUs != C0555C.TIME_UNSET ? ssMediaSource.manifest.durationUs : endTimeUs - startTimeUs;
            Timeline singlePeriodTimeline2 = new SinglePeriodTimeline(startTimeUs + durationUs, durationUs, startTimeUs, 0, true, false, ssMediaSource.tag);
        }
        refreshSourceInfo(timeline, ssMediaSource.manifest);
    }

    private void scheduleManifestRefresh() {
        if (this.manifest.isLive) {
            this.manifestRefreshHandler.postDelayed(new -$$Lambda$SsMediaSource$tFjHmMdOxDkhvkY7QhPdfdPmbtI(), Math.max(0, (this.manifestLoadStartTimestamp + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) - SystemClock.elapsedRealtime()));
        }
    }

    private void startLoadingManifest() {
        ParsingLoadable<SsManifest> loadable = new ParsingLoadable(this.manifestDataSource, this.manifestUri, 4, this.manifestParser);
        this.manifestEventDispatcher.loadStarted(loadable.dataSpec, loadable.type, this.manifestLoader.startLoading(loadable, this, this.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(loadable.type)));
    }
}
