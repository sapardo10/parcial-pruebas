package com.google.android.exoplayer2.source;

import android.os.Handler;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource.-CC;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.source.MediaSource.SourceInfoRefreshListener;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class BaseMediaSource implements MediaSource {
    private final MediaSourceEventListener$EventDispatcher eventDispatcher = new MediaSourceEventListener$EventDispatcher();
    @Nullable
    private Object manifest;
    @Nullable
    private ExoPlayer player;
    private final ArrayList<SourceInfoRefreshListener> sourceInfoListeners = new ArrayList(1);
    @Nullable
    private Timeline timeline;

    @Nullable
    public /* synthetic */ Object getTag() {
        return -CC.$default$getTag(this);
    }

    protected abstract void prepareSourceInternal(ExoPlayer exoPlayer, boolean z, @Nullable TransferListener transferListener);

    protected abstract void releaseSourceInternal();

    protected final void refreshSourceInfo(Timeline timeline, @Nullable Object manifest) {
        this.timeline = timeline;
        this.manifest = manifest;
        Iterator it = this.sourceInfoListeners.iterator();
        while (it.hasNext()) {
            ((SourceInfoRefreshListener) it.next()).onSourceInfoRefreshed(this, timeline, manifest);
        }
    }

    protected final MediaSourceEventListener$EventDispatcher createEventDispatcher(@Nullable MediaPeriodId mediaPeriodId) {
        return this.eventDispatcher.withParameters(0, mediaPeriodId, 0);
    }

    protected final MediaSourceEventListener$EventDispatcher createEventDispatcher(MediaPeriodId mediaPeriodId, long mediaTimeOffsetMs) {
        Assertions.checkArgument(mediaPeriodId != null);
        return this.eventDispatcher.withParameters(0, mediaPeriodId, mediaTimeOffsetMs);
    }

    protected final MediaSourceEventListener$EventDispatcher createEventDispatcher(int windowIndex, @Nullable MediaPeriodId mediaPeriodId, long mediaTimeOffsetMs) {
        return this.eventDispatcher.withParameters(windowIndex, mediaPeriodId, mediaTimeOffsetMs);
    }

    public final void addEventListener(Handler handler, MediaSourceEventListener eventListener) {
        this.eventDispatcher.addEventListener(handler, eventListener);
    }

    public final void removeEventListener(MediaSourceEventListener eventListener) {
        this.eventDispatcher.removeEventListener(eventListener);
    }

    public final void prepareSource(ExoPlayer player, boolean isTopLevelSource, SourceInfoRefreshListener listener) {
        prepareSource(player, isTopLevelSource, listener, null);
    }

    public final void prepareSource(ExoPlayer player, boolean isTopLevelSource, SourceInfoRefreshListener listener, @Nullable TransferListener mediaTransferListener) {
        boolean z;
        Timeline timeline;
        ExoPlayer exoPlayer = this.player;
        if (exoPlayer != null) {
            if (exoPlayer != player) {
                z = false;
                Assertions.checkArgument(z);
                this.sourceInfoListeners.add(listener);
                if (this.player != null) {
                    this.player = player;
                    prepareSourceInternal(player, isTopLevelSource, mediaTransferListener);
                }
                timeline = this.timeline;
                if (timeline != null) {
                    listener.onSourceInfoRefreshed(this, timeline, this.manifest);
                    return;
                }
                return;
            }
        }
        z = true;
        Assertions.checkArgument(z);
        this.sourceInfoListeners.add(listener);
        if (this.player != null) {
            timeline = this.timeline;
            if (timeline != null) {
                listener.onSourceInfoRefreshed(this, timeline, this.manifest);
                return;
            }
            return;
        }
        this.player = player;
        prepareSourceInternal(player, isTopLevelSource, mediaTransferListener);
    }

    public final void releaseSource(SourceInfoRefreshListener listener) {
        this.sourceInfoListeners.remove(listener);
        if (this.sourceInfoListeners.isEmpty()) {
            this.player = null;
            this.timeline = null;
            this.manifest = null;
            releaseSourceInternal();
        }
    }
}
