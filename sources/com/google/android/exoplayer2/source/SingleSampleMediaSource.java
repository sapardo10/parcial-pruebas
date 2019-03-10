package com.google.android.exoplayer2.source;

import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;

public final class SingleSampleMediaSource extends BaseMediaSource {
    private final com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory;
    private final DataSpec dataSpec;
    private final long durationUs;
    private final Format format;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    @Nullable
    private final Object tag;
    private final Timeline timeline;
    @Nullable
    private TransferListener transferListener;
    private final boolean treatLoadErrorsAsEndOfStream;

    @Deprecated
    public interface EventListener {
        void onLoadError(int i, IOException iOException);
    }

    public static final class Factory {
        private final com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory;
        private boolean isCreateCalled;
        private LoadErrorHandlingPolicy loadErrorHandlingPolicy = new DefaultLoadErrorHandlingPolicy();
        @Nullable
        private Object tag;
        private boolean treatLoadErrorsAsEndOfStream;

        public Factory(com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory) {
            this.dataSourceFactory = (com.google.android.exoplayer2.upstream.DataSource.Factory) Assertions.checkNotNull(dataSourceFactory);
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

        public Factory setTreatLoadErrorsAsEndOfStream(boolean treatLoadErrorsAsEndOfStream) {
            Assertions.checkState(this.isCreateCalled ^ 1);
            this.treatLoadErrorsAsEndOfStream = treatLoadErrorsAsEndOfStream;
            return this;
        }

        public SingleSampleMediaSource createMediaSource(Uri uri, Format format, long durationUs) {
            this.isCreateCalled = true;
            return new SingleSampleMediaSource(uri, this.dataSourceFactory, format, durationUs, this.loadErrorHandlingPolicy, this.treatLoadErrorsAsEndOfStream, this.tag);
        }

        @Deprecated
        public SingleSampleMediaSource createMediaSource(Uri uri, Format format, long durationUs, @Nullable Handler eventHandler, @Nullable MediaSourceEventListener eventListener) {
            SingleSampleMediaSource mediaSource = createMediaSource(uri, format, durationUs);
            if (eventHandler != null && eventListener != null) {
                mediaSource.addEventListener(eventHandler, eventListener);
            }
            return mediaSource;
        }
    }

    @Deprecated
    private static final class EventListenerWrapper extends DefaultMediaSourceEventListener {
        private final EventListener eventListener;
        private final int eventSourceId;

        public EventListenerWrapper(EventListener eventListener, int eventSourceId) {
            this.eventListener = (EventListener) Assertions.checkNotNull(eventListener);
            this.eventSourceId = eventSourceId;
        }

        public void onLoadError(int windowIndex, @Nullable MediaPeriodId mediaPeriodId, MediaSourceEventListener$LoadEventInfo loadEventInfo, MediaSourceEventListener$MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
            this.eventListener.onLoadError(this.eventSourceId, error);
        }
    }

    @Deprecated
    public SingleSampleMediaSource(Uri uri, com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory, Format format, long durationUs) {
        this(uri, dataSourceFactory, format, durationUs, 3);
    }

    @Deprecated
    public SingleSampleMediaSource(Uri uri, com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory, Format format, long durationUs, int minLoadableRetryCount) {
        this(uri, dataSourceFactory, format, durationUs, new DefaultLoadErrorHandlingPolicy(minLoadableRetryCount), false, null);
    }

    @Deprecated
    public SingleSampleMediaSource(Uri uri, com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory, Format format, long durationUs, int minLoadableRetryCount, Handler eventHandler, EventListener eventListener, int eventSourceId, boolean treatLoadErrorsAsEndOfStream) {
        Handler handler = eventHandler;
        EventListener eventListener2 = eventListener;
        this(uri, dataSourceFactory, format, durationUs, new DefaultLoadErrorHandlingPolicy(minLoadableRetryCount), treatLoadErrorsAsEndOfStream, null);
        if (handler == null || eventListener2 == null) {
            SingleSampleMediaSource singleSampleMediaSource = this;
            int i = eventSourceId;
            return;
        }
        singleSampleMediaSource = this;
        addEventListener(handler, new EventListenerWrapper(eventListener2, eventSourceId));
    }

    private SingleSampleMediaSource(Uri uri, com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory, Format format, long durationUs, LoadErrorHandlingPolicy loadErrorHandlingPolicy, boolean treatLoadErrorsAsEndOfStream, @Nullable Object tag) {
        this.dataSourceFactory = dataSourceFactory;
        this.format = format;
        this.durationUs = durationUs;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        this.treatLoadErrorsAsEndOfStream = treatLoadErrorsAsEndOfStream;
        this.tag = tag;
        this.dataSpec = new DataSpec(uri, 3);
        this.timeline = new SinglePeriodTimeline(durationUs, true, false, tag);
    }

    @Nullable
    public Object getTag() {
        return this.tag;
    }

    public void prepareSourceInternal(ExoPlayer player, boolean isTopLevelSource, @Nullable TransferListener mediaTransferListener) {
        this.transferListener = mediaTransferListener;
        refreshSourceInfo(this.timeline, null);
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
    }

    public MediaPeriod createPeriod(MediaPeriodId id, Allocator allocator) {
        return new SingleSampleMediaPeriod(this.dataSpec, this.dataSourceFactory, this.transferListener, this.format, this.durationUs, this.loadErrorHandlingPolicy, createEventDispatcher(id), this.treatLoadErrorsAsEndOfStream);
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        ((SingleSampleMediaPeriod) mediaPeriod).release();
    }

    public void releaseSourceInternal() {
    }
}
