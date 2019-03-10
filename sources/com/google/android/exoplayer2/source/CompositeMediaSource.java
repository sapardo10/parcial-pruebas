package com.google.android.exoplayer2.source;

import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.source.MediaSource.SourceInfoRefreshListener;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.HashMap;

public abstract class CompositeMediaSource<T> extends BaseMediaSource {
    private final HashMap<T, MediaSourceAndListener> childSources = new HashMap();
    @Nullable
    private Handler eventHandler;
    @Nullable
    private TransferListener mediaTransferListener;
    @Nullable
    private ExoPlayer player;

    private static final class MediaSourceAndListener {
        public final MediaSourceEventListener eventListener;
        public final SourceInfoRefreshListener listener;
        public final MediaSource mediaSource;

        public MediaSourceAndListener(MediaSource mediaSource, SourceInfoRefreshListener listener, MediaSourceEventListener eventListener) {
            this.mediaSource = mediaSource;
            this.listener = listener;
            this.eventListener = eventListener;
        }
    }

    private final class ForwardingEventListener implements MediaSourceEventListener {
        private MediaSourceEventListener$EventDispatcher eventDispatcher;
        private final T id;

        public ForwardingEventListener(T id) {
            this.eventDispatcher = CompositeMediaSource.this.createEventDispatcher(null);
            this.id = id;
        }

        public void onMediaPeriodCreated(int windowIndex, MediaPeriodId mediaPeriodId) {
            if (maybeUpdateEventDispatcher(windowIndex, mediaPeriodId)) {
                this.eventDispatcher.mediaPeriodCreated();
            }
        }

        public void onMediaPeriodReleased(int windowIndex, MediaPeriodId mediaPeriodId) {
            if (maybeUpdateEventDispatcher(windowIndex, mediaPeriodId)) {
                this.eventDispatcher.mediaPeriodReleased();
            }
        }

        public void onLoadStarted(int windowIndex, @Nullable MediaPeriodId mediaPeriodId, MediaSourceEventListener$LoadEventInfo loadEventData, MediaSourceEventListener$MediaLoadData mediaLoadData) {
            if (maybeUpdateEventDispatcher(windowIndex, mediaPeriodId)) {
                this.eventDispatcher.loadStarted(loadEventData, maybeUpdateMediaLoadData(mediaLoadData));
            }
        }

        public void onLoadCompleted(int windowIndex, @Nullable MediaPeriodId mediaPeriodId, MediaSourceEventListener$LoadEventInfo loadEventData, MediaSourceEventListener$MediaLoadData mediaLoadData) {
            if (maybeUpdateEventDispatcher(windowIndex, mediaPeriodId)) {
                this.eventDispatcher.loadCompleted(loadEventData, maybeUpdateMediaLoadData(mediaLoadData));
            }
        }

        public void onLoadCanceled(int windowIndex, @Nullable MediaPeriodId mediaPeriodId, MediaSourceEventListener$LoadEventInfo loadEventData, MediaSourceEventListener$MediaLoadData mediaLoadData) {
            if (maybeUpdateEventDispatcher(windowIndex, mediaPeriodId)) {
                this.eventDispatcher.loadCanceled(loadEventData, maybeUpdateMediaLoadData(mediaLoadData));
            }
        }

        public void onLoadError(int windowIndex, @Nullable MediaPeriodId mediaPeriodId, MediaSourceEventListener$LoadEventInfo loadEventData, MediaSourceEventListener$MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
            if (maybeUpdateEventDispatcher(windowIndex, mediaPeriodId)) {
                this.eventDispatcher.loadError(loadEventData, maybeUpdateMediaLoadData(mediaLoadData), error, wasCanceled);
            }
        }

        public void onReadingStarted(int windowIndex, MediaPeriodId mediaPeriodId) {
            if (maybeUpdateEventDispatcher(windowIndex, mediaPeriodId)) {
                this.eventDispatcher.readingStarted();
            }
        }

        public void onUpstreamDiscarded(int windowIndex, @Nullable MediaPeriodId mediaPeriodId, MediaSourceEventListener$MediaLoadData mediaLoadData) {
            if (maybeUpdateEventDispatcher(windowIndex, mediaPeriodId)) {
                this.eventDispatcher.upstreamDiscarded(maybeUpdateMediaLoadData(mediaLoadData));
            }
        }

        public void onDownstreamFormatChanged(int windowIndex, @Nullable MediaPeriodId mediaPeriodId, MediaSourceEventListener$MediaLoadData mediaLoadData) {
            if (maybeUpdateEventDispatcher(windowIndex, mediaPeriodId)) {
                this.eventDispatcher.downstreamFormatChanged(maybeUpdateMediaLoadData(mediaLoadData));
            }
        }

        private boolean maybeUpdateEventDispatcher(int childWindowIndex, @Nullable MediaPeriodId childMediaPeriodId) {
            MediaPeriodId mediaPeriodId = null;
            if (childMediaPeriodId != null) {
                mediaPeriodId = CompositeMediaSource.this.getMediaPeriodIdForChildMediaPeriodId(this.id, childMediaPeriodId);
                if (mediaPeriodId == null) {
                    return false;
                }
            }
            int windowIndex = CompositeMediaSource.this.getWindowIndexForChildWindowIndex(this.id, childWindowIndex);
            if (this.eventDispatcher.windowIndex == windowIndex) {
                if (Util.areEqual(this.eventDispatcher.mediaPeriodId, mediaPeriodId)) {
                    return true;
                }
            }
            this.eventDispatcher = CompositeMediaSource.this.createEventDispatcher(windowIndex, mediaPeriodId, 0);
            return true;
        }

        private MediaSourceEventListener$MediaLoadData maybeUpdateMediaLoadData(MediaSourceEventListener$MediaLoadData mediaLoadData) {
            MediaSourceEventListener$MediaLoadData mediaSourceEventListener$MediaLoadData = mediaLoadData;
            long mediaStartTimeMs = CompositeMediaSource.this.getMediaTimeForChildMediaTime(this.id, mediaSourceEventListener$MediaLoadData.mediaStartTimeMs);
            long mediaEndTimeMs = CompositeMediaSource.this.getMediaTimeForChildMediaTime(this.id, mediaSourceEventListener$MediaLoadData.mediaEndTimeMs);
            if (mediaStartTimeMs == mediaSourceEventListener$MediaLoadData.mediaStartTimeMs && mediaEndTimeMs == mediaSourceEventListener$MediaLoadData.mediaEndTimeMs) {
                return mediaSourceEventListener$MediaLoadData;
            }
            return new MediaSourceEventListener$MediaLoadData(mediaSourceEventListener$MediaLoadData.dataType, mediaSourceEventListener$MediaLoadData.trackType, mediaSourceEventListener$MediaLoadData.trackFormat, mediaSourceEventListener$MediaLoadData.trackSelectionReason, mediaSourceEventListener$MediaLoadData.trackSelectionData, mediaStartTimeMs, mediaEndTimeMs);
        }
    }

    protected abstract void onChildSourceInfoRefreshed(T t, MediaSource mediaSource, Timeline timeline, @Nullable Object obj);

    protected CompositeMediaSource() {
    }

    @CallSuper
    public void prepareSourceInternal(ExoPlayer player, boolean isTopLevelSource, @Nullable TransferListener mediaTransferListener) {
        this.player = player;
        this.mediaTransferListener = mediaTransferListener;
        this.eventHandler = new Handler();
    }

    @CallSuper
    public void maybeThrowSourceInfoRefreshError() throws IOException {
        for (MediaSourceAndListener childSource : this.childSources.values()) {
            childSource.mediaSource.maybeThrowSourceInfoRefreshError();
        }
    }

    @CallSuper
    public void releaseSourceInternal() {
        for (MediaSourceAndListener childSource : this.childSources.values()) {
            childSource.mediaSource.releaseSource(childSource.listener);
            childSource.mediaSource.removeEventListener(childSource.eventListener);
        }
        this.childSources.clear();
        this.player = null;
    }

    protected final void prepareChildSource(T id, MediaSource mediaSource) {
        Assertions.checkArgument(this.childSources.containsKey(id) ^ 1);
        SourceInfoRefreshListener sourceListener = new -$$Lambda$CompositeMediaSource$ahAPO18YbnzL6kKRAWdp4FR_Vco(this, id);
        MediaSourceEventListener eventListener = new ForwardingEventListener(id);
        this.childSources.put(id, new MediaSourceAndListener(mediaSource, sourceListener, eventListener));
        mediaSource.addEventListener((Handler) Assertions.checkNotNull(this.eventHandler), eventListener);
        mediaSource.prepareSource((ExoPlayer) Assertions.checkNotNull(this.player), false, sourceListener, this.mediaTransferListener);
    }

    protected final void releaseChildSource(T id) {
        MediaSourceAndListener removedChild = (MediaSourceAndListener) Assertions.checkNotNull(this.childSources.remove(id));
        removedChild.mediaSource.releaseSource(removedChild.listener);
        removedChild.mediaSource.removeEventListener(removedChild.eventListener);
    }

    protected int getWindowIndexForChildWindowIndex(T t, int windowIndex) {
        return windowIndex;
    }

    @Nullable
    protected MediaPeriodId getMediaPeriodIdForChildMediaPeriodId(T t, MediaPeriodId mediaPeriodId) {
        return mediaPeriodId;
    }

    protected long getMediaTimeForChildMediaTime(@Nullable T t, long mediaTimeMs) {
        return mediaTimeMs;
    }
}
