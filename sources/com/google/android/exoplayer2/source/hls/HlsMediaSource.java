package com.google.android.exoplayer2.source.hls;

import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.source.BaseMediaSource;
import com.google.android.exoplayer2.source.CompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.DefaultCompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.SinglePeriodTimeline;
import com.google.android.exoplayer2.source.ads.AdsMediaSource.MediaSourceFactory;
import com.google.android.exoplayer2.source.hls.playlist.DefaultHlsPlaylistParserFactory;
import com.google.android.exoplayer2.source.hls.playlist.DefaultHlsPlaylistTracker;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParser;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParserFactory;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker.PrimaryPlaylistListener;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.ParsingLoadable.Parser;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.util.List;

public final class HlsMediaSource extends BaseMediaSource implements PrimaryPlaylistListener {
    private final boolean allowChunklessPreparation;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private final HlsDataSourceFactory dataSourceFactory;
    private final HlsExtractorFactory extractorFactory;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    private final Uri manifestUri;
    @Nullable
    private TransferListener mediaTransferListener;
    private final HlsPlaylistTracker playlistTracker;
    @Nullable
    private final Object tag;

    public static final class Factory implements MediaSourceFactory {
        private boolean allowChunklessPreparation;
        private CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
        private HlsExtractorFactory extractorFactory;
        private final HlsDataSourceFactory hlsDataSourceFactory;
        private boolean isCreateCalled;
        private LoadErrorHandlingPolicy loadErrorHandlingPolicy;
        private HlsPlaylistParserFactory playlistParserFactory;
        private com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker.Factory playlistTrackerFactory;
        @Nullable
        private Object tag;

        public Factory(com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory) {
            this(new DefaultHlsDataSourceFactory(dataSourceFactory));
        }

        public Factory(HlsDataSourceFactory hlsDataSourceFactory) {
            this.hlsDataSourceFactory = (HlsDataSourceFactory) Assertions.checkNotNull(hlsDataSourceFactory);
            this.playlistParserFactory = new DefaultHlsPlaylistParserFactory();
            this.playlistTrackerFactory = DefaultHlsPlaylistTracker.FACTORY;
            this.extractorFactory = HlsExtractorFactory.DEFAULT;
            this.loadErrorHandlingPolicy = new DefaultLoadErrorHandlingPolicy();
            this.compositeSequenceableLoaderFactory = new DefaultCompositeSequenceableLoaderFactory();
        }

        public Factory setTag(Object tag) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.tag = tag;
            return this;
        }

        public Factory setExtractorFactory(HlsExtractorFactory extractorFactory) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.extractorFactory = (HlsExtractorFactory) Assertions.checkNotNull(extractorFactory);
            return this;
        }

        public Factory setLoadErrorHandlingPolicy(LoadErrorHandlingPolicy loadErrorHandlingPolicy) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
            return this;
        }

        @Deprecated
        public Factory setMinLoadableRetryCount(int minLoadableRetryCount) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.loadErrorHandlingPolicy = new DefaultLoadErrorHandlingPolicy(minLoadableRetryCount);
            return this;
        }

        public Factory setPlaylistParserFactory(HlsPlaylistParserFactory playlistParserFactory) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.playlistParserFactory = (HlsPlaylistParserFactory) Assertions.checkNotNull(playlistParserFactory);
            return this;
        }

        public Factory setPlaylistTrackerFactory(com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker.Factory playlistTrackerFactory) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.playlistTrackerFactory = (com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker.Factory) Assertions.checkNotNull(playlistTrackerFactory);
            return this;
        }

        public Factory setCompositeSequenceableLoaderFactory(CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.compositeSequenceableLoaderFactory = (CompositeSequenceableLoaderFactory) Assertions.checkNotNull(compositeSequenceableLoaderFactory);
            return this;
        }

        public Factory setAllowChunklessPreparation(boolean allowChunklessPreparation) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.allowChunklessPreparation = allowChunklessPreparation;
            return this;
        }

        public HlsMediaSource createMediaSource(Uri playlistUri) {
            this.isCreateCalled = true;
            HlsDataSourceFactory hlsDataSourceFactory = this.hlsDataSourceFactory;
            HlsExtractorFactory hlsExtractorFactory = this.extractorFactory;
            CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory = this.compositeSequenceableLoaderFactory;
            LoadErrorHandlingPolicy loadErrorHandlingPolicy = this.loadErrorHandlingPolicy;
            return new HlsMediaSource(playlistUri, hlsDataSourceFactory, hlsExtractorFactory, compositeSequenceableLoaderFactory, loadErrorHandlingPolicy, this.playlistTrackerFactory.createTracker(hlsDataSourceFactory, loadErrorHandlingPolicy, this.playlistParserFactory), this.allowChunklessPreparation, this.tag);
        }

        @Deprecated
        public HlsMediaSource createMediaSource(Uri playlistUri, @Nullable Handler eventHandler, @Nullable MediaSourceEventListener eventListener) {
            HlsMediaSource mediaSource = createMediaSource(playlistUri);
            if (eventHandler != null && eventListener != null) {
                mediaSource.addEventListener(eventHandler, eventListener);
            }
            return mediaSource;
        }

        public int[] getSupportedTypes() {
            return new int[]{2};
        }
    }

    static {
        ExoPlayerLibraryInfo.registerModule("goog.exo.hls");
    }

    @Deprecated
    public HlsMediaSource(Uri manifestUri, com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifestUri, dataSourceFactory, 3, eventHandler, eventListener);
    }

    @Deprecated
    public HlsMediaSource(Uri manifestUri, com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory, int minLoadableRetryCount, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifestUri, new DefaultHlsDataSourceFactory(dataSourceFactory), HlsExtractorFactory.DEFAULT, minLoadableRetryCount, eventHandler, eventListener, new HlsPlaylistParser());
    }

    @Deprecated
    public HlsMediaSource(Uri manifestUri, HlsDataSourceFactory dataSourceFactory, HlsExtractorFactory extractorFactory, int minLoadableRetryCount, Handler eventHandler, MediaSourceEventListener eventListener, Parser<HlsPlaylist> playlistParser) {
        int i = minLoadableRetryCount;
        Handler handler = eventHandler;
        MediaSourceEventListener mediaSourceEventListener = eventListener;
        this(manifestUri, dataSourceFactory, extractorFactory, new DefaultCompositeSequenceableLoaderFactory(), new DefaultLoadErrorHandlingPolicy(i), new DefaultHlsPlaylistTracker(dataSourceFactory, new DefaultLoadErrorHandlingPolicy(i), (Parser) playlistParser), false, null);
        if (handler == null || mediaSourceEventListener == null) {
            HlsMediaSource hlsMediaSource = this;
            return;
        }
        hlsMediaSource = this;
        addEventListener(handler, mediaSourceEventListener);
    }

    private HlsMediaSource(Uri manifestUri, HlsDataSourceFactory dataSourceFactory, HlsExtractorFactory extractorFactory, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, LoadErrorHandlingPolicy loadErrorHandlingPolicy, HlsPlaylistTracker playlistTracker, boolean allowChunklessPreparation, @Nullable Object tag) {
        this.manifestUri = manifestUri;
        this.dataSourceFactory = dataSourceFactory;
        this.extractorFactory = extractorFactory;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        this.playlistTracker = playlistTracker;
        this.allowChunklessPreparation = allowChunklessPreparation;
        this.tag = tag;
    }

    @Nullable
    public Object getTag() {
        return this.tag;
    }

    public void prepareSourceInternal(ExoPlayer player, boolean isTopLevelSource, @Nullable TransferListener mediaTransferListener) {
        this.mediaTransferListener = mediaTransferListener;
        this.playlistTracker.start(this.manifestUri, createEventDispatcher(null), this);
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
        this.playlistTracker.maybeThrowPrimaryPlaylistRefreshError();
    }

    public MediaPeriod createPeriod(MediaPeriodId id, Allocator allocator) {
        return new HlsMediaPeriod(this.extractorFactory, this.playlistTracker, this.dataSourceFactory, this.mediaTransferListener, this.loadErrorHandlingPolicy, createEventDispatcher(id), allocator, this.compositeSequenceableLoaderFactory, this.allowChunklessPreparation);
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        ((HlsMediaPeriod) mediaPeriod).release();
    }

    public void releaseSourceInternal() {
        this.playlistTracker.stop();
    }

    public void onPrimaryPlaylistRefreshed(HlsMediaPlaylist playlist) {
        long presentationStartTimeMs;
        long windowDefaultStartPositionUs;
        List<Segment> segments;
        long windowDefaultStartPositionUs2;
        SinglePeriodTimeline timeline;
        HlsMediaSource hlsMediaSource = this;
        HlsMediaPlaylist hlsMediaPlaylist = playlist;
        long windowStartTimeMs = hlsMediaPlaylist.hasProgramDateTime ? C0555C.usToMs(hlsMediaPlaylist.startTimeUs) : C0555C.TIME_UNSET;
        if (hlsMediaPlaylist.playlistType != 2) {
            if (hlsMediaPlaylist.playlistType != 1) {
                presentationStartTimeMs = C0555C.TIME_UNSET;
                windowDefaultStartPositionUs = hlsMediaPlaylist.startOffsetUs;
                if (hlsMediaSource.playlistTracker.isLive()) {
                    if (windowDefaultStartPositionUs == C0555C.TIME_UNSET) {
                        windowDefaultStartPositionUs = 0;
                    }
                    SinglePeriodTimeline singlePeriodTimeline = new SinglePeriodTimeline(presentationStartTimeMs, windowStartTimeMs, hlsMediaPlaylist.durationUs, hlsMediaPlaylist.durationUs, 0, windowDefaultStartPositionUs, true, false, hlsMediaSource.tag);
                } else {
                    long offsetFromInitialStartTimeUs = hlsMediaPlaylist.startTimeUs - hlsMediaSource.playlistTracker.getInitialStartTimeUs();
                    long periodDurationUs = hlsMediaPlaylist.hasEndTag ? offsetFromInitialStartTimeUs + hlsMediaPlaylist.durationUs : C0555C.TIME_UNSET;
                    segments = hlsMediaPlaylist.segments;
                    if (windowDefaultStartPositionUs == C0555C.TIME_UNSET) {
                        windowDefaultStartPositionUs2 = windowDefaultStartPositionUs;
                    } else if (segments.isEmpty()) {
                        windowDefaultStartPositionUs2 = ((Segment) segments.get(Math.max(0, segments.size() - 3))).relativeStartTimeUs;
                    } else {
                        windowDefaultStartPositionUs2 = 0;
                    }
                    timeline = new SinglePeriodTimeline(presentationStartTimeMs, windowStartTimeMs, periodDurationUs, hlsMediaPlaylist.durationUs, offsetFromInitialStartTimeUs, windowDefaultStartPositionUs2, true, hlsMediaPlaylist.hasEndTag ^ 1, hlsMediaSource.tag);
                    windowDefaultStartPositionUs = windowDefaultStartPositionUs2;
                }
                refreshSourceInfo(timeline, new HlsManifest(hlsMediaSource.playlistTracker.getMasterPlaylist(), hlsMediaPlaylist));
            }
        }
        presentationStartTimeMs = windowStartTimeMs;
        windowDefaultStartPositionUs = hlsMediaPlaylist.startOffsetUs;
        if (hlsMediaSource.playlistTracker.isLive()) {
            if (windowDefaultStartPositionUs == C0555C.TIME_UNSET) {
                windowDefaultStartPositionUs = 0;
            }
            SinglePeriodTimeline singlePeriodTimeline2 = new SinglePeriodTimeline(presentationStartTimeMs, windowStartTimeMs, hlsMediaPlaylist.durationUs, hlsMediaPlaylist.durationUs, 0, windowDefaultStartPositionUs, true, false, hlsMediaSource.tag);
        } else {
            long offsetFromInitialStartTimeUs2 = hlsMediaPlaylist.startTimeUs - hlsMediaSource.playlistTracker.getInitialStartTimeUs();
            if (hlsMediaPlaylist.hasEndTag) {
            }
            segments = hlsMediaPlaylist.segments;
            if (windowDefaultStartPositionUs == C0555C.TIME_UNSET) {
                windowDefaultStartPositionUs2 = windowDefaultStartPositionUs;
            } else if (segments.isEmpty()) {
                windowDefaultStartPositionUs2 = ((Segment) segments.get(Math.max(0, segments.size() - 3))).relativeStartTimeUs;
            } else {
                windowDefaultStartPositionUs2 = 0;
            }
            timeline = new SinglePeriodTimeline(presentationStartTimeMs, windowStartTimeMs, periodDurationUs, hlsMediaPlaylist.durationUs, offsetFromInitialStartTimeUs2, windowDefaultStartPositionUs2, true, hlsMediaPlaylist.hasEndTag ^ 1, hlsMediaSource.tag);
            windowDefaultStartPositionUs = windowDefaultStartPositionUs2;
        }
        refreshSourceInfo(timeline, new HlsManifest(hlsMediaSource.playlistTracker.getMasterPlaylist(), hlsMediaPlaylist));
    }
}
