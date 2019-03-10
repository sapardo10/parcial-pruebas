package com.google.android.exoplayer2.source;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public final class MediaSourceEventListener$EventDispatcher {
    private final CopyOnWriteArrayList<ListenerAndHandler> listenerAndHandlers;
    @Nullable
    public final MediaPeriodId mediaPeriodId;
    private final long mediaTimeOffsetMs;
    public final int windowIndex;

    /* renamed from: com.google.android.exoplayer2.source.MediaSourceEventListener$EventDispatcher$ListenerAndHandler */
    private static final class ListenerAndHandler {
        public final Handler handler;
        public final MediaSourceEventListener listener;

        public ListenerAndHandler(Handler handler, MediaSourceEventListener listener) {
            this.handler = handler;
            this.listener = listener;
        }
    }

    public MediaSourceEventListener$EventDispatcher() {
        this(new CopyOnWriteArrayList(), 0, null, 0);
    }

    private MediaSourceEventListener$EventDispatcher(CopyOnWriteArrayList<ListenerAndHandler> listenerAndHandlers, int windowIndex, @Nullable MediaPeriodId mediaPeriodId, long mediaTimeOffsetMs) {
        this.listenerAndHandlers = listenerAndHandlers;
        this.windowIndex = windowIndex;
        this.mediaPeriodId = mediaPeriodId;
        this.mediaTimeOffsetMs = mediaTimeOffsetMs;
    }

    @CheckResult
    public MediaSourceEventListener$EventDispatcher withParameters(int windowIndex, @Nullable MediaPeriodId mediaPeriodId, long mediaTimeOffsetMs) {
        return new MediaSourceEventListener$EventDispatcher(this.listenerAndHandlers, windowIndex, mediaPeriodId, mediaTimeOffsetMs);
    }

    public void addEventListener(Handler handler, MediaSourceEventListener eventListener) {
        boolean z = (handler == null || eventListener == null) ? false : true;
        Assertions.checkArgument(z);
        this.listenerAndHandlers.add(new ListenerAndHandler(handler, eventListener));
    }

    public void removeEventListener(MediaSourceEventListener eventListener) {
        Iterator it = this.listenerAndHandlers.iterator();
        while (it.hasNext()) {
            ListenerAndHandler listenerAndHandler = (ListenerAndHandler) it.next();
            if (listenerAndHandler.listener == eventListener) {
                this.listenerAndHandlers.remove(listenerAndHandler);
            }
        }
    }

    public void mediaPeriodCreated() {
        MediaPeriodId mediaPeriodId = (MediaPeriodId) Assertions.checkNotNull(this.mediaPeriodId);
        Iterator it = this.listenerAndHandlers.iterator();
        while (it.hasNext()) {
            ListenerAndHandler listenerAndHandler = (ListenerAndHandler) it.next();
            postOrRun(listenerAndHandler.handler, new C0616xba05d90e(this, listenerAndHandler.listener, mediaPeriodId));
        }
    }

    public void mediaPeriodReleased() {
        MediaPeriodId mediaPeriodId = (MediaPeriodId) Assertions.checkNotNull(this.mediaPeriodId);
        Iterator it = this.listenerAndHandlers.iterator();
        while (it.hasNext()) {
            ListenerAndHandler listenerAndHandler = (ListenerAndHandler) it.next();
            postOrRun(listenerAndHandler.handler, new C0619x6f51cd56(this, listenerAndHandler.listener, mediaPeriodId));
        }
    }

    public void loadStarted(DataSpec dataSpec, int dataType, long elapsedRealtimeMs) {
        loadStarted(dataSpec, dataType, -1, null, 0, null, C0555C.TIME_UNSET, C0555C.TIME_UNSET, elapsedRealtimeMs);
    }

    public void loadStarted(DataSpec dataSpec, int dataType, int trackType, @Nullable Format trackFormat, int trackSelectionReason, @Nullable Object trackSelectionData, long mediaStartTimeUs, long mediaEndTimeUs, long elapsedRealtimeMs) {
        loadStarted(new MediaSourceEventListener$LoadEventInfo(dataSpec, dataSpec.uri, Collections.emptyMap(), elapsedRealtimeMs, 0, 0), new MediaSourceEventListener$MediaLoadData(dataType, trackType, trackFormat, trackSelectionReason, trackSelectionData, adjustMediaTime(mediaStartTimeUs), adjustMediaTime(mediaEndTimeUs)));
    }

    public void loadStarted(MediaSourceEventListener$LoadEventInfo loadEventInfo, MediaSourceEventListener$MediaLoadData mediaLoadData) {
        Iterator it = this.listenerAndHandlers.iterator();
        while (it.hasNext()) {
            ListenerAndHandler listenerAndHandler = (ListenerAndHandler) it.next();
            postOrRun(listenerAndHandler.handler, new C0618xa1fa9f85(this, listenerAndHandler.listener, loadEventInfo, mediaLoadData));
        }
    }

    public void loadCompleted(DataSpec dataSpec, Uri uri, Map<String, List<String>> responseHeaders, int dataType, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
        loadCompleted(dataSpec, uri, responseHeaders, dataType, -1, null, 0, null, C0555C.TIME_UNSET, C0555C.TIME_UNSET, elapsedRealtimeMs, loadDurationMs, bytesLoaded);
    }

    public void loadCompleted(DataSpec dataSpec, Uri uri, Map<String, List<String>> responseHeaders, int dataType, int trackType, @Nullable Format trackFormat, int trackSelectionReason, @Nullable Object trackSelectionData, long mediaStartTimeUs, long mediaEndTimeUs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
        loadCompleted(new MediaSourceEventListener$LoadEventInfo(dataSpec, uri, responseHeaders, elapsedRealtimeMs, loadDurationMs, bytesLoaded), new MediaSourceEventListener$MediaLoadData(dataType, trackType, trackFormat, trackSelectionReason, trackSelectionData, adjustMediaTime(mediaStartTimeUs), adjustMediaTime(mediaEndTimeUs)));
    }

    public void loadCompleted(MediaSourceEventListener$LoadEventInfo loadEventInfo, MediaSourceEventListener$MediaLoadData mediaLoadData) {
        Iterator it = this.listenerAndHandlers.iterator();
        while (it.hasNext()) {
            ListenerAndHandler listenerAndHandler = (ListenerAndHandler) it.next();
            postOrRun(listenerAndHandler.handler, new C0615xa944c178(this, listenerAndHandler.listener, loadEventInfo, mediaLoadData));
        }
    }

    public void loadCanceled(DataSpec dataSpec, Uri uri, Map<String, List<String>> responseHeaders, int dataType, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
        loadCanceled(dataSpec, uri, responseHeaders, dataType, -1, null, 0, null, C0555C.TIME_UNSET, C0555C.TIME_UNSET, elapsedRealtimeMs, loadDurationMs, bytesLoaded);
    }

    public void loadCanceled(DataSpec dataSpec, Uri uri, Map<String, List<String>> responseHeaders, int dataType, int trackType, @Nullable Format trackFormat, int trackSelectionReason, @Nullable Object trackSelectionData, long mediaStartTimeUs, long mediaEndTimeUs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
        loadCanceled(new MediaSourceEventListener$LoadEventInfo(dataSpec, uri, responseHeaders, elapsedRealtimeMs, loadDurationMs, bytesLoaded), new MediaSourceEventListener$MediaLoadData(dataType, trackType, trackFormat, trackSelectionReason, trackSelectionData, adjustMediaTime(mediaStartTimeUs), adjustMediaTime(mediaEndTimeUs)));
    }

    public void loadCanceled(MediaSourceEventListener$LoadEventInfo loadEventInfo, MediaSourceEventListener$MediaLoadData mediaLoadData) {
        Iterator it = this.listenerAndHandlers.iterator();
        while (it.hasNext()) {
            ListenerAndHandler listenerAndHandler = (ListenerAndHandler) it.next();
            postOrRun(listenerAndHandler.handler, new C0612x9d2b2b7d(this, listenerAndHandler.listener, loadEventInfo, mediaLoadData));
        }
    }

    public void loadError(DataSpec dataSpec, Uri uri, Map<String, List<String>> responseHeaders, int dataType, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded, IOException error, boolean wasCanceled) {
        loadError(dataSpec, uri, responseHeaders, dataType, -1, null, 0, null, C0555C.TIME_UNSET, C0555C.TIME_UNSET, elapsedRealtimeMs, loadDurationMs, bytesLoaded, error, wasCanceled);
    }

    public void loadError(DataSpec dataSpec, Uri uri, Map<String, List<String>> responseHeaders, int dataType, int trackType, @Nullable Format trackFormat, int trackSelectionReason, @Nullable Object trackSelectionData, long mediaStartTimeUs, long mediaEndTimeUs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded, IOException error, boolean wasCanceled) {
        loadError(new MediaSourceEventListener$LoadEventInfo(dataSpec, uri, responseHeaders, elapsedRealtimeMs, loadDurationMs, bytesLoaded), new MediaSourceEventListener$MediaLoadData(dataType, trackType, trackFormat, trackSelectionReason, trackSelectionData, adjustMediaTime(mediaStartTimeUs), adjustMediaTime(mediaEndTimeUs)), error, wasCanceled);
    }

    public void loadError(MediaSourceEventListener$LoadEventInfo loadEventInfo, MediaSourceEventListener$MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
        Iterator it = this.listenerAndHandlers.iterator();
        while (it.hasNext()) {
            ListenerAndHandler listenerAndHandler = (ListenerAndHandler) it.next();
            postOrRun(listenerAndHandler.handler, new C0611x15e4e00a(this, listenerAndHandler.listener, loadEventInfo, mediaLoadData, error, wasCanceled));
        }
    }

    public void readingStarted() {
        MediaPeriodId mediaPeriodId = (MediaPeriodId) Assertions.checkNotNull(this.mediaPeriodId);
        Iterator it = this.listenerAndHandlers.iterator();
        while (it.hasNext()) {
            ListenerAndHandler listenerAndHandler = (ListenerAndHandler) it.next();
            postOrRun(listenerAndHandler.handler, new C0617x9b820e1e(this, listenerAndHandler.listener, mediaPeriodId));
        }
    }

    public void upstreamDiscarded(int trackType, long mediaStartTimeUs, long mediaEndTimeUs) {
        upstreamDiscarded(new MediaSourceEventListener$MediaLoadData(1, trackType, null, 3, null, adjustMediaTime(mediaStartTimeUs), adjustMediaTime(mediaEndTimeUs)));
    }

    public void upstreamDiscarded(MediaSourceEventListener$MediaLoadData mediaLoadData) {
        MediaPeriodId mediaPeriodId = (MediaPeriodId) Assertions.checkNotNull(this.mediaPeriodId);
        Iterator it = this.listenerAndHandlers.iterator();
        while (it.hasNext()) {
            ListenerAndHandler listenerAndHandler = (ListenerAndHandler) it.next();
            postOrRun(listenerAndHandler.handler, new C0613x46f001ef(this, listenerAndHandler.listener, mediaPeriodId, mediaLoadData));
        }
    }

    public void downstreamFormatChanged(int trackType, @Nullable Format trackFormat, int trackSelectionReason, @Nullable Object trackSelectionData, long mediaTimeUs) {
        MediaSourceEventListener$EventDispatcher mediaSourceEventListener$EventDispatcher = this;
        downstreamFormatChanged(new MediaSourceEventListener$MediaLoadData(1, trackType, trackFormat, trackSelectionReason, trackSelectionData, adjustMediaTime(mediaTimeUs), C0555C.TIME_UNSET));
    }

    public void downstreamFormatChanged(MediaSourceEventListener$MediaLoadData mediaLoadData) {
        Iterator it = this.listenerAndHandlers.iterator();
        while (it.hasNext()) {
            ListenerAndHandler listenerAndHandler = (ListenerAndHandler) it.next();
            postOrRun(listenerAndHandler.handler, new C0614x6798e946(this, listenerAndHandler.listener, mediaLoadData));
        }
    }

    private long adjustMediaTime(long mediaTimeUs) {
        long mediaTimeMs = C0555C.usToMs(mediaTimeUs);
        return mediaTimeMs == C0555C.TIME_UNSET ? C0555C.TIME_UNSET : this.mediaTimeOffsetMs + mediaTimeMs;
    }

    private void postOrRun(Handler handler, Runnable runnable) {
        if (handler.getLooper() == Looper.myLooper()) {
            runnable.run();
        } else {
            handler.post(runnable);
        }
    }
}
