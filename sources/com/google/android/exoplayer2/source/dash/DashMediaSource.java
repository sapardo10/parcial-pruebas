package com.google.android.exoplayer2.source.dash;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.source.BaseMediaSource;
import com.google.android.exoplayer2.source.CompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.DefaultCompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.MediaSourceEventListener$EventDispatcher;
import com.google.android.exoplayer2.source.ads.AdsMediaSource.MediaSourceFactory;
import com.google.android.exoplayer2.source.dash.PlayerEmsgHandler.PlayerEmsgCallback;
import com.google.android.exoplayer2.source.dash.manifest.AdaptationSet;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.DashManifestParser;
import com.google.android.exoplayer2.source.dash.manifest.Period;
import com.google.android.exoplayer2.source.dash.manifest.Representation;
import com.google.android.exoplayer2.source.dash.manifest.UtcTimingElement;
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
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DashMediaSource extends BaseMediaSource {
    @Deprecated
    public static final long DEFAULT_LIVE_PRESENTATION_DELAY_FIXED_MS = 30000;
    public static final long DEFAULT_LIVE_PRESENTATION_DELAY_MS = 30000;
    @Deprecated
    public static final long DEFAULT_LIVE_PRESENTATION_DELAY_PREFER_MANIFEST_MS = -1;
    private static final long MIN_LIVE_DEFAULT_START_POSITION_US = 5000000;
    private static final int NOTIFY_MANIFEST_INTERVAL_MS = 5000;
    private static final String TAG = "DashMediaSource";
    private final com.google.android.exoplayer2.source.dash.DashChunkSource.Factory chunkSourceFactory;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private DataSource dataSource;
    private long elapsedRealtimeOffsetMs;
    private long expiredManifestPublishTimeUs;
    private int firstPeriodId;
    private Handler handler;
    private Uri initialManifestUri;
    private final long livePresentationDelayMs;
    private final boolean livePresentationDelayOverridesManifest;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    private Loader loader;
    private DashManifest manifest;
    private final ManifestCallback manifestCallback;
    private final com.google.android.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory;
    private final MediaSourceEventListener$EventDispatcher manifestEventDispatcher;
    private IOException manifestFatalError;
    private long manifestLoadEndTimestampMs;
    private final LoaderErrorThrower manifestLoadErrorThrower;
    private boolean manifestLoadPending;
    private long manifestLoadStartTimestampMs;
    private final Parser<? extends DashManifest> manifestParser;
    private Uri manifestUri;
    private final Object manifestUriLock;
    @Nullable
    private TransferListener mediaTransferListener;
    private final SparseArray<DashMediaPeriod> periodsById;
    private final PlayerEmsgCallback playerEmsgCallback;
    private final Runnable refreshManifestRunnable;
    private final boolean sideloadedManifest;
    private final Runnable simulateManifestRefreshRunnable;
    private int staleManifestReloadAttempt;
    @Nullable
    private final Object tag;

    private static final class PeriodSeekInfo {
        public final long availableEndTimeUs;
        public final long availableStartTimeUs;
        public final boolean isIndexExplicit;

        public static PeriodSeekInfo createPeriodSeekInfo(Period period, long durationUs) {
            boolean haveAudioVideoAdaptationSets;
            Period period2 = period;
            long j = durationUs;
            int adaptationSetCount = period2.adaptationSets.size();
            boolean haveAudioVideoAdaptationSets2 = false;
            int i = 0;
            while (i < adaptationSetCount) {
                int type = ((AdaptationSet) period2.adaptationSets.get(i)).type;
                if (type != 1) {
                    if (type != 2) {
                        i++;
                    }
                }
                haveAudioVideoAdaptationSets2 = true;
                break;
            }
            i = 0;
            long availableStartTimeUs = 0;
            boolean isIndexExplicit = false;
            boolean seenEmptyIndex = false;
            long availableEndTimeUs = Long.MAX_VALUE;
            while (i < adaptationSetCount) {
                int adaptationSetCount2;
                AdaptationSet adaptationSet = (AdaptationSet) period2.adaptationSets.get(i);
                if (haveAudioVideoAdaptationSets2 && adaptationSet.type == 3) {
                    adaptationSetCount2 = adaptationSetCount;
                    haveAudioVideoAdaptationSets = haveAudioVideoAdaptationSets2;
                } else {
                    DashSegmentIndex index = ((Representation) adaptationSet.representations.get(0)).getIndex();
                    if (index == null) {
                        adaptationSetCount = availableEndTimeUs;
                        return new PeriodSeekInfo(true, 0, durationUs);
                    }
                    DashSegmentIndex index2 = index;
                    adaptationSetCount2 = adaptationSetCount;
                    haveAudioVideoAdaptationSets = haveAudioVideoAdaptationSets2;
                    adaptationSetCount = availableEndTimeUs;
                    boolean isIndexExplicit2 = index2.isExplicit() | isIndexExplicit;
                    DashSegmentIndex index3 = index2;
                    int segmentCount = index3.getSegmentCount(j);
                    if (segmentCount == 0) {
                        isIndexExplicit = isIndexExplicit2;
                        seenEmptyIndex = true;
                        availableStartTimeUs = 0;
                        availableEndTimeUs = 0;
                    } else if (seenEmptyIndex) {
                        isIndexExplicit = isIndexExplicit2;
                        availableEndTimeUs = adaptationSetCount;
                    } else {
                        long firstSegmentNum = index3.getFirstSegmentNum();
                        boolean isIndexExplicit3 = isIndexExplicit2;
                        long adaptationSetAvailableStartTimeUs = index3.getTimeUs(firstSegmentNum);
                        long availableStartTimeUs2 = Math.max(availableStartTimeUs, adaptationSetAvailableStartTimeUs);
                        if (segmentCount != -1) {
                            adaptationSetAvailableStartTimeUs = (((long) segmentCount) + firstSegmentNum) - 1;
                            long lastSegmentNum = adaptationSetAvailableStartTimeUs;
                            availableStartTimeUs = availableStartTimeUs2;
                            isIndexExplicit = isIndexExplicit3;
                            availableEndTimeUs = Math.min(adaptationSetCount, index3.getTimeUs(adaptationSetAvailableStartTimeUs) + index3.getDurationUs(adaptationSetAvailableStartTimeUs, j));
                        } else {
                            availableStartTimeUs = availableStartTimeUs2;
                            isIndexExplicit = isIndexExplicit3;
                            availableEndTimeUs = adaptationSetCount;
                        }
                    }
                }
                i++;
                adaptationSetCount = adaptationSetCount2;
                haveAudioVideoAdaptationSets2 = haveAudioVideoAdaptationSets;
                period2 = period;
            }
            haveAudioVideoAdaptationSets = haveAudioVideoAdaptationSets2;
            long availableEndTimeUs2 = availableEndTimeUs;
            return new PeriodSeekInfo(isIndexExplicit, availableStartTimeUs, availableEndTimeUs);
        }

        private PeriodSeekInfo(boolean isIndexExplicit, long availableStartTimeUs, long availableEndTimeUs) {
            this.isIndexExplicit = isIndexExplicit;
            this.availableStartTimeUs = availableStartTimeUs;
            this.availableEndTimeUs = availableEndTimeUs;
        }
    }

    private static final class DashTimeline extends Timeline {
        private final int firstPeriodId;
        private final DashManifest manifest;
        private final long offsetInFirstPeriodUs;
        private final long presentationStartTimeMs;
        private final long windowDefaultStartPositionUs;
        private final long windowDurationUs;
        private final long windowStartTimeMs;
        @Nullable
        private final Object windowTag;

        public DashTimeline(long presentationStartTimeMs, long windowStartTimeMs, int firstPeriodId, long offsetInFirstPeriodUs, long windowDurationUs, long windowDefaultStartPositionUs, DashManifest manifest, @Nullable Object windowTag) {
            this.presentationStartTimeMs = presentationStartTimeMs;
            this.windowStartTimeMs = windowStartTimeMs;
            this.firstPeriodId = firstPeriodId;
            this.offsetInFirstPeriodUs = offsetInFirstPeriodUs;
            this.windowDurationUs = windowDurationUs;
            this.windowDefaultStartPositionUs = windowDefaultStartPositionUs;
            this.manifest = manifest;
            this.windowTag = windowTag;
        }

        public int getPeriodCount() {
            return this.manifest.getPeriodCount();
        }

        public Timeline.Period getPeriod(int periodIndex, Timeline.Period period, boolean setIdentifiers) {
            Assertions.checkIndex(periodIndex, 0, getPeriodCount());
            Integer num = null;
            Object id = setIdentifiers ? this.manifest.getPeriod(periodIndex).id : null;
            if (setIdentifiers) {
                num = Integer.valueOf(this.firstPeriodId + periodIndex);
            }
            return period.set(id, num, 0, this.manifest.getPeriodDurationUs(periodIndex), C0555C.msToUs(this.manifest.getPeriod(periodIndex).startMs - this.manifest.getPeriod(0).startMs) - this.offsetInFirstPeriodUs);
        }

        public int getWindowCount() {
            return 1;
        }

        public Window getWindow(int windowIndex, Window window, boolean setTag, long defaultPositionProjectionUs) {
            Assertions.checkIndex(windowIndex, 0, 1);
            long windowDefaultStartPositionUs = getAdjustedWindowDefaultStartPositionUs(defaultPositionProjectionUs);
            Object tag = setTag ? r0.windowTag : null;
            boolean isDynamic = r0.manifest.dynamic && r0.manifest.minUpdatePeriodMs != C0555C.TIME_UNSET && r0.manifest.durationMs == C0555C.TIME_UNSET;
            return window.set(tag, r0.presentationStartTimeMs, r0.windowStartTimeMs, true, isDynamic, windowDefaultStartPositionUs, r0.windowDurationUs, 0, getPeriodCount() - 1, r0.offsetInFirstPeriodUs);
        }

        public int getIndexOfPeriod(Object uid) {
            int i = -1;
            if (!(uid instanceof Integer)) {
                return -1;
            }
            int periodIndex = ((Integer) uid).intValue() - this.firstPeriodId;
            if (periodIndex >= 0) {
                if (periodIndex < getPeriodCount()) {
                    i = periodIndex;
                }
            }
            return i;
        }

        private long getAdjustedWindowDefaultStartPositionUs(long defaultPositionProjectionUs) {
            long windowDefaultStartPositionUs = this.windowDefaultStartPositionUs;
            if (!this.manifest.dynamic) {
                return windowDefaultStartPositionUs;
            }
            if (defaultPositionProjectionUs > 0) {
                windowDefaultStartPositionUs += defaultPositionProjectionUs;
                if (windowDefaultStartPositionUs > r0.windowDurationUs) {
                    return C0555C.TIME_UNSET;
                }
            }
            int periodIndex = 0;
            long defaultStartPositionInPeriodUs = r0.offsetInFirstPeriodUs + windowDefaultStartPositionUs;
            long periodDurationUs = r0.manifest.getPeriodDurationUs(0);
            while (periodIndex < r0.manifest.getPeriodCount() - 1 && defaultStartPositionInPeriodUs >= periodDurationUs) {
                defaultStartPositionInPeriodUs -= periodDurationUs;
                periodIndex++;
                periodDurationUs = r0.manifest.getPeriodDurationUs(periodIndex);
            }
            Period period = r0.manifest.getPeriod(periodIndex);
            int videoAdaptationSetIndex = period.getAdaptationSetIndex(2);
            if (videoAdaptationSetIndex == -1) {
                return windowDefaultStartPositionUs;
            }
            DashSegmentIndex snapIndex = ((Representation) ((AdaptationSet) period.adaptationSets.get(videoAdaptationSetIndex)).representations.get(0)).getIndex();
            if (snapIndex != null) {
                if (snapIndex.getSegmentCount(periodDurationUs) != 0) {
                    return (snapIndex.getTimeUs(snapIndex.getSegmentNum(defaultStartPositionInPeriodUs, periodDurationUs)) + windowDefaultStartPositionUs) - defaultStartPositionInPeriodUs;
                }
            }
            return windowDefaultStartPositionUs;
        }

        public Object getUidOfPeriod(int periodIndex) {
            Assertions.checkIndex(periodIndex, 0, getPeriodCount());
            return Integer.valueOf(this.firstPeriodId + periodIndex);
        }
    }

    private final class DefaultPlayerEmsgCallback implements PlayerEmsgCallback {
        private DefaultPlayerEmsgCallback() {
        }

        public void onDashManifestRefreshRequested() {
            DashMediaSource.this.onDashManifestRefreshRequested();
        }

        public void onDashManifestPublishTimeExpired(long expiredManifestPublishTimeUs) {
            DashMediaSource.this.onDashManifestPublishTimeExpired(expiredManifestPublishTimeUs);
        }
    }

    public static final class Factory implements MediaSourceFactory {
        private final com.google.android.exoplayer2.source.dash.DashChunkSource.Factory chunkSourceFactory;
        private CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
        private boolean isCreateCalled;
        private long livePresentationDelayMs;
        private boolean livePresentationDelayOverridesManifest;
        private LoadErrorHandlingPolicy loadErrorHandlingPolicy;
        @Nullable
        private final com.google.android.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory;
        @Nullable
        private Parser<? extends DashManifest> manifestParser;
        @Nullable
        private Object tag;

        public Factory(com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory) {
            this(new com.google.android.exoplayer2.source.dash.DefaultDashChunkSource.Factory(dataSourceFactory), dataSourceFactory);
        }

        public Factory(com.google.android.exoplayer2.source.dash.DashChunkSource.Factory chunkSourceFactory, @Nullable com.google.android.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory) {
            this.chunkSourceFactory = (com.google.android.exoplayer2.source.dash.DashChunkSource.Factory) Assertions.checkNotNull(chunkSourceFactory);
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

        @Deprecated
        public Factory setLivePresentationDelayMs(long livePresentationDelayMs) {
            if (livePresentationDelayMs == -1) {
                return setLivePresentationDelayMs(30000, false);
            }
            return setLivePresentationDelayMs(livePresentationDelayMs, true);
        }

        public Factory setLivePresentationDelayMs(long livePresentationDelayMs, boolean overridesManifest) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.livePresentationDelayMs = livePresentationDelayMs;
            this.livePresentationDelayOverridesManifest = overridesManifest;
            return this;
        }

        public Factory setManifestParser(Parser<? extends DashManifest> manifestParser) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.manifestParser = (Parser) Assertions.checkNotNull(manifestParser);
            return this;
        }

        public Factory setCompositeSequenceableLoaderFactory(CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.compositeSequenceableLoaderFactory = (CompositeSequenceableLoaderFactory) Assertions.checkNotNull(compositeSequenceableLoaderFactory);
            return this;
        }

        public DashMediaSource createMediaSource(DashManifest manifest) {
            Assertions.checkArgument(manifest.dynamic ^ true);
            this.isCreateCalled = true;
            return new DashMediaSource(manifest, null, null, null, this.chunkSourceFactory, this.compositeSequenceableLoaderFactory, this.loadErrorHandlingPolicy, this.livePresentationDelayMs, this.livePresentationDelayOverridesManifest, this.tag);
        }

        @Deprecated
        public DashMediaSource createMediaSource(DashManifest manifest, @Nullable Handler eventHandler, @Nullable MediaSourceEventListener eventListener) {
            DashMediaSource mediaSource = createMediaSource(manifest);
            if (eventHandler != null && eventListener != null) {
                mediaSource.addEventListener(eventHandler, eventListener);
            }
            return mediaSource;
        }

        public DashMediaSource createMediaSource(Uri manifestUri) {
            this.isCreateCalled = true;
            if (this.manifestParser == null) {
                this.manifestParser = new DashManifestParser();
            }
            return new DashMediaSource(null, (Uri) Assertions.checkNotNull(manifestUri), this.manifestDataSourceFactory, this.manifestParser, this.chunkSourceFactory, this.compositeSequenceableLoaderFactory, this.loadErrorHandlingPolicy, this.livePresentationDelayMs, this.livePresentationDelayOverridesManifest, this.tag);
        }

        @Deprecated
        public DashMediaSource createMediaSource(Uri manifestUri, @Nullable Handler eventHandler, @Nullable MediaSourceEventListener eventListener) {
            DashMediaSource mediaSource = createMediaSource(manifestUri);
            if (eventHandler != null && eventListener != null) {
                mediaSource.addEventListener(eventHandler, eventListener);
            }
            return mediaSource;
        }

        public int[] getSupportedTypes() {
            return new int[]{0};
        }
    }

    static final class Iso8601Parser implements Parser<Long> {
        private static final Pattern TIMESTAMP_WITH_TIMEZONE_PATTERN = Pattern.compile("(.+?)(Z|((\\+|-|−)(\\d\\d)(:?(\\d\\d))?))");

        Iso8601Parser() {
        }

        public Long parse(Uri uri, InputStream inputStream) throws IOException {
            String firstLine = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8"))).readLine();
            try {
                Matcher matcher = TIMESTAMP_WITH_TIMEZONE_PATTERN.matcher(firstLine);
                if (matcher.matches()) {
                    String timestampWithoutTimezone = matcher.group(1);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                    format.setTimeZone(TimeZone.getTimeZone("UTC"));
                    long timestampMs = format.parse(timestampWithoutTimezone).getTime();
                    if (!"Z".equals(matcher.group(2))) {
                        long sign = "+".equals(matcher.group(4)) ? 1 : -1;
                        long hours = Long.parseLong(matcher.group(5));
                        String minutesString = matcher.group(7);
                        timestampMs -= ((((hours * 60) + (TextUtils.isEmpty(minutesString) ? 0 : Long.parseLong(minutesString))) * 60) * 1000) * sign;
                    }
                    return Long.valueOf(timestampMs);
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Couldn't parse timestamp: ");
                stringBuilder.append(firstLine);
                throw new ParserException(stringBuilder.toString());
            } catch (Throwable e) {
                throw new ParserException(e);
            }
        }
    }

    private final class ManifestCallback implements Callback<ParsingLoadable<DashManifest>> {
        private ManifestCallback() {
        }

        public void onLoadCompleted(ParsingLoadable<DashManifest> loadable, long elapsedRealtimeMs, long loadDurationMs) {
            DashMediaSource.this.onManifestLoadCompleted(loadable, elapsedRealtimeMs, loadDurationMs);
        }

        public void onLoadCanceled(ParsingLoadable<DashManifest> loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
            DashMediaSource.this.onLoadCanceled(loadable, elapsedRealtimeMs, loadDurationMs);
        }

        public LoadErrorAction onLoadError(ParsingLoadable<DashManifest> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error, int errorCount) {
            return DashMediaSource.this.onManifestLoadError(loadable, elapsedRealtimeMs, loadDurationMs, error);
        }
    }

    final class ManifestLoadErrorThrower implements LoaderErrorThrower {
        ManifestLoadErrorThrower() {
        }

        public void maybeThrowError() throws IOException {
            DashMediaSource.this.loader.maybeThrowError();
            maybeThrowManifestError();
        }

        public void maybeThrowError(int minRetryCount) throws IOException {
            DashMediaSource.this.loader.maybeThrowError(minRetryCount);
            maybeThrowManifestError();
        }

        private void maybeThrowManifestError() throws IOException {
            if (DashMediaSource.this.manifestFatalError != null) {
                throw DashMediaSource.this.manifestFatalError;
            }
        }
    }

    private final class UtcTimestampCallback implements Callback<ParsingLoadable<Long>> {
        private UtcTimestampCallback() {
        }

        public void onLoadCompleted(ParsingLoadable<Long> loadable, long elapsedRealtimeMs, long loadDurationMs) {
            DashMediaSource.this.onUtcTimestampLoadCompleted(loadable, elapsedRealtimeMs, loadDurationMs);
        }

        public void onLoadCanceled(ParsingLoadable<Long> loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
            DashMediaSource.this.onLoadCanceled(loadable, elapsedRealtimeMs, loadDurationMs);
        }

        public LoadErrorAction onLoadError(ParsingLoadable<Long> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error, int errorCount) {
            return DashMediaSource.this.onUtcTimestampLoadError(loadable, elapsedRealtimeMs, loadDurationMs, error);
        }
    }

    private static final class XsDateTimeParser implements Parser<Long> {
        private XsDateTimeParser() {
        }

        public Long parse(Uri uri, InputStream inputStream) throws IOException {
            return Long.valueOf(Util.parseXsDateTime(new BufferedReader(new InputStreamReader(inputStream)).readLine()));
        }
    }

    static {
        ExoPlayerLibraryInfo.registerModule("goog.exo.dash");
    }

    @Deprecated
    public DashMediaSource(DashManifest manifest, com.google.android.exoplayer2.source.dash.DashChunkSource.Factory chunkSourceFactory, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifest, chunkSourceFactory, 3, eventHandler, eventListener);
    }

    @Deprecated
    public DashMediaSource(DashManifest manifest, com.google.android.exoplayer2.source.dash.DashChunkSource.Factory chunkSourceFactory, int minLoadableRetryCount, Handler eventHandler, MediaSourceEventListener eventListener) {
        Handler handler = eventHandler;
        MediaSourceEventListener mediaSourceEventListener = eventListener;
        this(manifest, null, null, null, chunkSourceFactory, new DefaultCompositeSequenceableLoaderFactory(), new DefaultLoadErrorHandlingPolicy(minLoadableRetryCount), 30000, false, null);
        if (handler == null || mediaSourceEventListener == null) {
            DashMediaSource dashMediaSource = this;
            return;
        }
        dashMediaSource = this;
        addEventListener(handler, mediaSourceEventListener);
    }

    @Deprecated
    public DashMediaSource(Uri manifestUri, com.google.android.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory, com.google.android.exoplayer2.source.dash.DashChunkSource.Factory chunkSourceFactory, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifestUri, manifestDataSourceFactory, chunkSourceFactory, 3, -1, eventHandler, eventListener);
    }

    @Deprecated
    public DashMediaSource(Uri manifestUri, com.google.android.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory, com.google.android.exoplayer2.source.dash.DashChunkSource.Factory chunkSourceFactory, int minLoadableRetryCount, long livePresentationDelayMs, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifestUri, manifestDataSourceFactory, new DashManifestParser(), chunkSourceFactory, minLoadableRetryCount, livePresentationDelayMs, eventHandler, eventListener);
    }

    @Deprecated
    public DashMediaSource(Uri manifestUri, com.google.android.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory, Parser<? extends DashManifest> manifestParser, com.google.android.exoplayer2.source.dash.DashChunkSource.Factory chunkSourceFactory, int minLoadableRetryCount, long livePresentationDelayMs, Handler eventHandler, MediaSourceEventListener eventListener) {
        Handler handler = eventHandler;
        MediaSourceEventListener mediaSourceEventListener = eventListener;
        this(null, manifestUri, manifestDataSourceFactory, manifestParser, chunkSourceFactory, new DefaultCompositeSequenceableLoaderFactory(), new DefaultLoadErrorHandlingPolicy(minLoadableRetryCount), livePresentationDelayMs == -1 ? 30000 : livePresentationDelayMs, livePresentationDelayMs != -1, null);
        if (handler == null || mediaSourceEventListener == null) {
            DashMediaSource dashMediaSource = this;
            return;
        }
        dashMediaSource = this;
        addEventListener(handler, mediaSourceEventListener);
    }

    private DashMediaSource(DashManifest manifest, Uri manifestUri, com.google.android.exoplayer2.upstream.DataSource.Factory manifestDataSourceFactory, Parser<? extends DashManifest> manifestParser, com.google.android.exoplayer2.source.dash.DashChunkSource.Factory chunkSourceFactory, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, LoadErrorHandlingPolicy loadErrorHandlingPolicy, long livePresentationDelayMs, boolean livePresentationDelayOverridesManifest, @Nullable Object tag) {
        this.initialManifestUri = manifestUri;
        this.manifest = manifest;
        this.manifestUri = manifestUri;
        this.manifestDataSourceFactory = manifestDataSourceFactory;
        this.manifestParser = manifestParser;
        this.chunkSourceFactory = chunkSourceFactory;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        this.livePresentationDelayMs = livePresentationDelayMs;
        this.livePresentationDelayOverridesManifest = livePresentationDelayOverridesManifest;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        this.tag = tag;
        this.sideloadedManifest = manifest != null;
        this.manifestEventDispatcher = createEventDispatcher(null);
        this.manifestUriLock = new Object();
        this.periodsById = new SparseArray();
        this.playerEmsgCallback = new DefaultPlayerEmsgCallback();
        this.expiredManifestPublishTimeUs = C0555C.TIME_UNSET;
        if (this.sideloadedManifest) {
            Assertions.checkState(true ^ manifest.dynamic);
            this.manifestCallback = null;
            this.refreshManifestRunnable = null;
            this.simulateManifestRefreshRunnable = null;
            this.manifestLoadErrorThrower = new Dummy();
            return;
        }
        this.manifestCallback = new ManifestCallback();
        this.manifestLoadErrorThrower = new ManifestLoadErrorThrower();
        this.refreshManifestRunnable = new -$$Lambda$DashMediaSource$QbzYvqCY1TT8f0KClkalovG-Oxc();
        this.simulateManifestRefreshRunnable = new -$$Lambda$DashMediaSource$e1nzB-O4m3YSG1BkxQDKPaNvDa8();
    }

    public void replaceManifestUri(Uri manifestUri) {
        synchronized (this.manifestUriLock) {
            this.manifestUri = manifestUri;
            this.initialManifestUri = manifestUri;
        }
    }

    @Nullable
    public Object getTag() {
        return this.tag;
    }

    public void prepareSourceInternal(ExoPlayer player, boolean isTopLevelSource, @Nullable TransferListener mediaTransferListener) {
        this.mediaTransferListener = mediaTransferListener;
        if (this.sideloadedManifest) {
            processManifest(false);
            return;
        }
        this.dataSource = this.manifestDataSourceFactory.createDataSource();
        this.loader = new Loader("Loader:DashMediaSource");
        this.handler = new Handler();
        startLoadingManifest();
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
        this.manifestLoadErrorThrower.maybeThrowError();
    }

    public MediaPeriod createPeriod(MediaPeriodId periodId, Allocator allocator) {
        MediaPeriodId mediaPeriodId = periodId;
        int periodIndex = ((Integer) mediaPeriodId.periodUid).intValue() - this.firstPeriodId;
        MediaSourceEventListener$EventDispatcher periodEventDispatcher = createEventDispatcher(mediaPeriodId, this.manifest.getPeriod(periodIndex).startMs);
        int i = this.firstPeriodId + periodIndex;
        DashManifest dashManifest = this.manifest;
        com.google.android.exoplayer2.source.dash.DashChunkSource.Factory factory = this.chunkSourceFactory;
        TransferListener transferListener = this.mediaTransferListener;
        LoadErrorHandlingPolicy loadErrorHandlingPolicy = this.loadErrorHandlingPolicy;
        long j = this.elapsedRealtimeOffsetMs;
        LoaderErrorThrower loaderErrorThrower = this.manifestLoadErrorThrower;
        CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory = this.compositeSequenceableLoaderFactory;
        CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory2 = compositeSequenceableLoaderFactory;
        DashMediaPeriod mediaPeriod = new DashMediaPeriod(i, dashManifest, periodIndex, factory, transferListener, loadErrorHandlingPolicy, periodEventDispatcher, j, loaderErrorThrower, allocator, compositeSequenceableLoaderFactory2, this.playerEmsgCallback);
        this.periodsById.put(mediaPeriod.id, mediaPeriod);
        return mediaPeriod;
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        DashMediaPeriod dashMediaPeriod = (DashMediaPeriod) mediaPeriod;
        dashMediaPeriod.release();
        this.periodsById.remove(dashMediaPeriod.id);
    }

    public void releaseSourceInternal() {
        this.manifestLoadPending = false;
        this.dataSource = null;
        Loader loader = this.loader;
        if (loader != null) {
            loader.release();
            this.loader = null;
        }
        this.manifestLoadStartTimestampMs = 0;
        this.manifestLoadEndTimestampMs = 0;
        this.manifest = this.sideloadedManifest ? this.manifest : null;
        this.manifestUri = this.initialManifestUri;
        this.manifestFatalError = null;
        Handler handler = this.handler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            this.handler = null;
        }
        this.elapsedRealtimeOffsetMs = 0;
        this.staleManifestReloadAttempt = 0;
        this.expiredManifestPublishTimeUs = C0555C.TIME_UNSET;
        this.firstPeriodId = 0;
        this.periodsById.clear();
    }

    void onDashManifestRefreshRequested() {
        this.handler.removeCallbacks(this.simulateManifestRefreshRunnable);
        startLoadingManifest();
    }

    void onDashManifestPublishTimeExpired(long expiredManifestPublishTimeUs) {
        long j = this.expiredManifestPublishTimeUs;
        if (j != C0555C.TIME_UNSET) {
            if (j >= expiredManifestPublishTimeUs) {
                return;
            }
        }
        this.expiredManifestPublishTimeUs = expiredManifestPublishTimeUs;
    }

    void onManifestLoadCompleted(ParsingLoadable<DashManifest> loadable, long elapsedRealtimeMs, long loadDurationMs) {
        ParsingLoadable<DashManifest> parsingLoadable = loadable;
        long j = elapsedRealtimeMs;
        this.manifestEventDispatcher.loadCompleted(parsingLoadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), parsingLoadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        DashManifest newManifest = (DashManifest) loadable.getResult();
        DashManifest dashManifest = this.manifest;
        boolean z = false;
        int periodCount = dashManifest == null ? 0 : dashManifest.getPeriodCount();
        long newFirstPeriodStartTimeMs = newManifest.getPeriod(0).startMs;
        int removedPeriodCount = 0;
        while (removedPeriodCount < periodCount) {
            if (r1.manifest.getPeriod(removedPeriodCount).startMs >= newFirstPeriodStartTimeMs) {
                break;
            }
            removedPeriodCount++;
        }
        if (newManifest.dynamic) {
            boolean isManifestStale = false;
            if (periodCount - removedPeriodCount > newManifest.getPeriodCount()) {
                Log.m10w(TAG, "Loaded out of sync manifest");
                isManifestStale = true;
            } else if (r1.expiredManifestPublishTimeUs != C0555C.TIME_UNSET && newManifest.publishTimeMs * 1000 <= r1.expiredManifestPublishTimeUs) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Loaded stale dynamic manifest: ");
                stringBuilder.append(newManifest.publishTimeMs);
                stringBuilder.append(", ");
                stringBuilder.append(r1.expiredManifestPublishTimeUs);
                Log.m10w(str, stringBuilder.toString());
                isManifestStale = true;
            }
            if (isManifestStale) {
                int i = r1.staleManifestReloadAttempt;
                r1.staleManifestReloadAttempt = i + 1;
                if (i < r1.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(parsingLoadable.type)) {
                    scheduleManifestRefresh(getManifestLoadRetryDelayMillis());
                } else {
                    r1.manifestFatalError = new DashManifestStaleException();
                }
                return;
            }
            r1.staleManifestReloadAttempt = 0;
        }
        r1.manifest = newManifest;
        r1.manifestLoadPending &= r1.manifest.dynamic;
        r1.manifestLoadStartTimestampMs = j - loadDurationMs;
        r1.manifestLoadEndTimestampMs = j;
        if (r1.manifest.location != null) {
            synchronized (r1.manifestUriLock) {
                if (parsingLoadable.dataSpec.uri == r1.manifestUri) {
                    z = true;
                }
                if (z) {
                    r1.manifestUri = r1.manifest.location;
                }
            }
        }
        if (periodCount != 0) {
            r1.firstPeriodId += removedPeriodCount;
            processManifest(true);
        } else if (r1.manifest.utcTiming != null) {
            resolveUtcTimingElement(r1.manifest.utcTiming);
        } else {
            processManifest(true);
        }
    }

    LoadErrorAction onManifestLoadError(ParsingLoadable<DashManifest> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error) {
        ParsingLoadable<DashManifest> parsingLoadable = loadable;
        boolean isFatal = error instanceof ParserException;
        this.manifestEventDispatcher.loadError(parsingLoadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), parsingLoadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded(), error, isFatal);
        return isFatal ? Loader.DONT_RETRY_FATAL : Loader.RETRY;
    }

    void onUtcTimestampLoadCompleted(ParsingLoadable<Long> loadable, long elapsedRealtimeMs, long loadDurationMs) {
        ParsingLoadable<Long> parsingLoadable = loadable;
        this.manifestEventDispatcher.loadCompleted(parsingLoadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), parsingLoadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        onUtcTimestampResolved(((Long) loadable.getResult()).longValue() - elapsedRealtimeMs);
    }

    LoadErrorAction onUtcTimestampLoadError(ParsingLoadable<Long> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error) {
        ParsingLoadable<Long> parsingLoadable = loadable;
        this.manifestEventDispatcher.loadError(parsingLoadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), parsingLoadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded(), error, true);
        onUtcTimestampResolutionError(error);
        return Loader.DONT_RETRY;
    }

    void onLoadCanceled(ParsingLoadable<?> loadable, long elapsedRealtimeMs, long loadDurationMs) {
        ParsingLoadable<?> parsingLoadable = loadable;
        this.manifestEventDispatcher.loadCanceled(parsingLoadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), parsingLoadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
    }

    private void resolveUtcTimingElement(UtcTimingElement timingElement) {
        String scheme = timingElement.schemeIdUri;
        if (!Util.areEqual(scheme, "urn:mpeg:dash:utc:direct:2014")) {
            if (!Util.areEqual(scheme, "urn:mpeg:dash:utc:direct:2012")) {
                if (!Util.areEqual(scheme, "urn:mpeg:dash:utc:http-iso:2014")) {
                    if (!Util.areEqual(scheme, "urn:mpeg:dash:utc:http-iso:2012")) {
                        if (!Util.areEqual(scheme, "urn:mpeg:dash:utc:http-xsdate:2014")) {
                            if (!Util.areEqual(scheme, "urn:mpeg:dash:utc:http-xsdate:2012")) {
                                onUtcTimestampResolutionError(new IOException("Unsupported UTC timing scheme"));
                                return;
                            }
                        }
                        resolveUtcTimingElementHttp(timingElement, new XsDateTimeParser());
                        return;
                    }
                }
                resolveUtcTimingElementHttp(timingElement, new Iso8601Parser());
                return;
            }
        }
        resolveUtcTimingElementDirect(timingElement);
    }

    private void resolveUtcTimingElementDirect(UtcTimingElement timingElement) {
        try {
            onUtcTimestampResolved(Util.parseXsDateTime(timingElement.value) - this.manifestLoadEndTimestampMs);
        } catch (ParserException e) {
            onUtcTimestampResolutionError(e);
        }
    }

    private void resolveUtcTimingElementHttp(UtcTimingElement timingElement, Parser<Long> parser) {
        startLoading(new ParsingLoadable(this.dataSource, Uri.parse(timingElement.value), 5, (Parser) parser), new UtcTimestampCallback(), 1);
    }

    private void onUtcTimestampResolved(long elapsedRealtimeOffsetMs) {
        this.elapsedRealtimeOffsetMs = elapsedRealtimeOffsetMs;
        processManifest(true);
    }

    private void onUtcTimestampResolutionError(IOException error) {
        Log.m7e(TAG, "Failed to resolve UtcTiming element.", error);
        processManifest(true);
    }

    private void processManifest(boolean scheduleRefresh) {
        int id;
        long presentationDelayForManifestMs;
        DashMediaSource dashMediaSource = this;
        for (int i = 0; i < dashMediaSource.periodsById.size(); i++) {
            id = dashMediaSource.periodsById.keyAt(i);
            if (id >= dashMediaSource.firstPeriodId) {
                ((DashMediaPeriod) dashMediaSource.periodsById.valueAt(i)).updateManifest(dashMediaSource.manifest, id - dashMediaSource.firstPeriodId);
            }
        }
        boolean windowChangingImplicitly = false;
        id = dashMediaSource.manifest.getPeriodCount() - 1;
        PeriodSeekInfo firstPeriodSeekInfo = PeriodSeekInfo.createPeriodSeekInfo(dashMediaSource.manifest.getPeriod(0), dashMediaSource.manifest.getPeriodDurationUs(0));
        PeriodSeekInfo lastPeriodSeekInfo = PeriodSeekInfo.createPeriodSeekInfo(dashMediaSource.manifest.getPeriod(id), dashMediaSource.manifest.getPeriodDurationUs(id));
        long currentStartTimeUs = firstPeriodSeekInfo.availableStartTimeUs;
        long currentEndTimeUs = lastPeriodSeekInfo.availableEndTimeUs;
        int i2;
        if (!dashMediaSource.manifest.dynamic || lastPeriodSeekInfo.isIndexExplicit) {
            i2 = id;
            PeriodSeekInfo periodSeekInfo = lastPeriodSeekInfo;
            windowChangingImplicitly = false;
        } else {
            currentEndTimeUs = Math.min((getNowUnixTimeUs() - C0555C.msToUs(dashMediaSource.manifest.availabilityStartTimeMs)) - C0555C.msToUs(dashMediaSource.manifest.getPeriod(id).startMs), currentEndTimeUs);
            if (dashMediaSource.manifest.timeShiftBufferDepthMs != C0555C.TIME_UNSET) {
                int periodIndex = id;
                long offsetInPeriodUs = currentEndTimeUs - C0555C.msToUs(dashMediaSource.manifest.timeShiftBufferDepthMs);
                while (offsetInPeriodUs < 0 && periodIndex > 0) {
                    periodIndex--;
                    offsetInPeriodUs += dashMediaSource.manifest.getPeriodDurationUs(periodIndex);
                    windowChangingImplicitly = windowChangingImplicitly;
                }
                if (periodIndex == 0) {
                    currentStartTimeUs = Math.max(currentStartTimeUs, offsetInPeriodUs);
                    i2 = id;
                } else {
                    currentStartTimeUs = dashMediaSource.manifest.getPeriodDurationUs(0);
                }
            } else {
                boolean windowChangingImplicitly2 = false;
                i2 = id;
            }
            windowChangingImplicitly = true;
        }
        long windowDurationUs = currentEndTimeUs - currentStartTimeUs;
        for (id = 0; id < dashMediaSource.manifest.getPeriodCount() - 1; id++) {
            windowDurationUs += dashMediaSource.manifest.getPeriodDurationUs(id);
        }
        long windowDefaultStartPositionUs = 0;
        if (dashMediaSource.manifest.dynamic) {
            presentationDelayForManifestMs = dashMediaSource.livePresentationDelayMs;
            if (!dashMediaSource.livePresentationDelayOverridesManifest && dashMediaSource.manifest.suggestedPresentationDelayMs != C0555C.TIME_UNSET) {
                presentationDelayForManifestMs = dashMediaSource.manifest.suggestedPresentationDelayMs;
            }
            windowDefaultStartPositionUs = windowDurationUs - C0555C.msToUs(presentationDelayForManifestMs);
            if (windowDefaultStartPositionUs < MIN_LIVE_DEFAULT_START_POSITION_US) {
                windowDefaultStartPositionUs = Math.min(MIN_LIVE_DEFAULT_START_POSITION_US, windowDurationUs / 2);
            }
        }
        long windowStartTimeMs = (dashMediaSource.manifest.availabilityStartTimeMs + dashMediaSource.manifest.getPeriod(0).startMs) + C0555C.usToMs(currentStartTimeUs);
        refreshSourceInfo(new DashTimeline(dashMediaSource.manifest.availabilityStartTimeMs, windowStartTimeMs, dashMediaSource.firstPeriodId, currentStartTimeUs, windowDurationUs, windowDefaultStartPositionUs, dashMediaSource.manifest, dashMediaSource.tag), dashMediaSource.manifest);
        if (dashMediaSource.sideloadedManifest) {
            long j = windowStartTimeMs;
            long j2 = windowDurationUs;
            return;
        }
        dashMediaSource.handler.removeCallbacks(dashMediaSource.simulateManifestRefreshRunnable);
        if (windowChangingImplicitly) {
            dashMediaSource.handler.postDelayed(dashMediaSource.simulateManifestRefreshRunnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        }
        if (dashMediaSource.manifestLoadPending) {
            startLoadingManifest();
            boolean z = windowChangingImplicitly;
            j = windowStartTimeMs;
            j2 = windowDurationUs;
        } else if (scheduleRefresh && dashMediaSource.manifest.dynamic && dashMediaSource.manifest.minUpdatePeriodMs != C0555C.TIME_UNSET) {
            presentationDelayForManifestMs = dashMediaSource.manifest.minUpdatePeriodMs;
            if (presentationDelayForManifestMs == 0) {
                presentationDelayForManifestMs = DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
            }
            scheduleManifestRefresh(Math.max(0, (dashMediaSource.manifestLoadStartTimestampMs + presentationDelayForManifestMs) - SystemClock.elapsedRealtime()));
        } else {
            j = windowStartTimeMs;
            j2 = windowDurationUs;
        }
    }

    private void scheduleManifestRefresh(long delayUntilNextLoadMs) {
        this.handler.postDelayed(this.refreshManifestRunnable, delayUntilNextLoadMs);
    }

    private void startLoadingManifest() {
        this.handler.removeCallbacks(this.refreshManifestRunnable);
        if (this.loader.isLoading()) {
            this.manifestLoadPending = true;
            return;
        }
        Uri manifestUri;
        synchronized (this.manifestUriLock) {
            manifestUri = this.manifestUri;
        }
        this.manifestLoadPending = false;
        startLoading(new ParsingLoadable(this.dataSource, manifestUri, 4, this.manifestParser), this.manifestCallback, this.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(4));
    }

    private long getManifestLoadRetryDelayMillis() {
        return (long) Math.min((this.staleManifestReloadAttempt - 1) * 1000, 5000);
    }

    private <T> void startLoading(ParsingLoadable<T> loadable, Callback<ParsingLoadable<T>> callback, int minRetryCount) {
        this.manifestEventDispatcher.loadStarted(loadable.dataSpec, loadable.type, this.loader.startLoading(loadable, callback, minRetryCount));
    }

    private long getNowUnixTimeUs() {
        if (this.elapsedRealtimeOffsetMs != 0) {
            return C0555C.msToUs(SystemClock.elapsedRealtime() + this.elapsedRealtimeOffsetMs);
        }
        return C0555C.msToUs(System.currentTimeMillis());
    }
}
