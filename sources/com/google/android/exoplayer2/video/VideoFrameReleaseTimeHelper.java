package com.google.android.exoplayer2.video;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;
import android.view.Display;
import android.view.WindowManager;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.util.Util;

@TargetApi(16)
public final class VideoFrameReleaseTimeHelper {
    private static final long CHOREOGRAPHER_SAMPLE_DELAY_MILLIS = 500;
    private static final long MAX_ALLOWED_DRIFT_NS = 20000000;
    private static final int MIN_FRAMES_FOR_ADJUSTMENT = 6;
    private static final long VSYNC_OFFSET_PERCENTAGE = 80;
    private long adjustedLastFrameTimeNs;
    private final DefaultDisplayListener displayListener;
    private long frameCount;
    private boolean haveSync;
    private long lastFramePresentationTimeUs;
    private long pendingAdjustedFrameTimeNs;
    private long syncFramePresentationTimeNs;
    private long syncUnadjustedReleaseTimeNs;
    private long vsyncDurationNs;
    private long vsyncOffsetNs;
    private final VSyncSampler vsyncSampler;
    private final WindowManager windowManager;

    @TargetApi(17)
    private final class DefaultDisplayListener implements DisplayListener {
        private final DisplayManager displayManager;

        public DefaultDisplayListener(DisplayManager displayManager) {
            this.displayManager = displayManager;
        }

        public void register() {
            this.displayManager.registerDisplayListener(this, null);
        }

        public void unregister() {
            this.displayManager.unregisterDisplayListener(this);
        }

        public void onDisplayAdded(int displayId) {
        }

        public void onDisplayRemoved(int displayId) {
        }

        public void onDisplayChanged(int displayId) {
            if (displayId == 0) {
                VideoFrameReleaseTimeHelper.this.updateDefaultDisplayRefreshRateParams();
            }
        }
    }

    private static final class VSyncSampler implements FrameCallback, Callback {
        private static final int CREATE_CHOREOGRAPHER = 0;
        private static final VSyncSampler INSTANCE = new VSyncSampler();
        private static final int MSG_ADD_OBSERVER = 1;
        private static final int MSG_REMOVE_OBSERVER = 2;
        private Choreographer choreographer;
        private final HandlerThread choreographerOwnerThread = new HandlerThread("ChoreographerOwner:Handler");
        private final Handler handler;
        private int observerCount;
        public volatile long sampledVsyncTimeNs = C0555C.TIME_UNSET;

        public static VSyncSampler getInstance() {
            return INSTANCE;
        }

        private VSyncSampler() {
            this.choreographerOwnerThread.start();
            this.handler = Util.createHandler(this.choreographerOwnerThread.getLooper(), this);
            this.handler.sendEmptyMessage(0);
        }

        public void addObserver() {
            this.handler.sendEmptyMessage(1);
        }

        public void removeObserver() {
            this.handler.sendEmptyMessage(2);
        }

        public void doFrame(long vsyncTimeNs) {
            this.sampledVsyncTimeNs = vsyncTimeNs;
            this.choreographer.postFrameCallbackDelayed(this, VideoFrameReleaseTimeHelper.CHOREOGRAPHER_SAMPLE_DELAY_MILLIS);
        }

        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    createChoreographerInstanceInternal();
                    return true;
                case 1:
                    addObserverInternal();
                    return true;
                case 2:
                    removeObserverInternal();
                    return true;
                default:
                    return false;
            }
        }

        private void createChoreographerInstanceInternal() {
            this.choreographer = Choreographer.getInstance();
        }

        private void addObserverInternal() {
            this.observerCount++;
            if (this.observerCount == 1) {
                this.choreographer.postFrameCallback(this);
            }
        }

        private void removeObserverInternal() {
            this.observerCount--;
            if (this.observerCount == 0) {
                this.choreographer.removeFrameCallback(this);
                this.sampledVsyncTimeNs = C0555C.TIME_UNSET;
            }
        }
    }

    public VideoFrameReleaseTimeHelper() {
        this(null);
    }

    public VideoFrameReleaseTimeHelper(@Nullable Context context) {
        DefaultDisplayListener defaultDisplayListener = null;
        if (context != null) {
            context = context.getApplicationContext();
            this.windowManager = (WindowManager) context.getSystemService("window");
        } else {
            this.windowManager = null;
        }
        if (this.windowManager != null) {
            if (Util.SDK_INT >= 17) {
                defaultDisplayListener = maybeBuildDefaultDisplayListenerV17(context);
            }
            this.displayListener = defaultDisplayListener;
            this.vsyncSampler = VSyncSampler.getInstance();
        } else {
            this.displayListener = null;
            this.vsyncSampler = null;
        }
        this.vsyncDurationNs = C0555C.TIME_UNSET;
        this.vsyncOffsetNs = C0555C.TIME_UNSET;
    }

    public void enable() {
        this.haveSync = false;
        if (this.windowManager != null) {
            this.vsyncSampler.addObserver();
            DefaultDisplayListener defaultDisplayListener = this.displayListener;
            if (defaultDisplayListener != null) {
                defaultDisplayListener.register();
            }
            updateDefaultDisplayRefreshRateParams();
        }
    }

    public void disable() {
        if (this.windowManager != null) {
            DefaultDisplayListener defaultDisplayListener = this.displayListener;
            if (defaultDisplayListener != null) {
                defaultDisplayListener.unregister();
            }
            this.vsyncSampler.removeObserver();
        }
    }

    public long adjustReleaseTime(long framePresentationTimeUs, long unadjustedReleaseTimeNs) {
        long adjustedReleaseTimeNs;
        long j = framePresentationTimeUs;
        long j2 = unadjustedReleaseTimeNs;
        long framePresentationTimeNs = 1000 * j;
        long adjustedFrameTimeNs = framePresentationTimeNs;
        long adjustedReleaseTimeNs2 = unadjustedReleaseTimeNs;
        if (this.haveSync) {
            if (j != r0.lastFramePresentationTimeUs) {
                r0.frameCount++;
                r0.adjustedLastFrameTimeNs = r0.pendingAdjustedFrameTimeNs;
            }
            long j3 = r0.frameCount;
            if (j3 >= 6) {
                j3 = r0.adjustedLastFrameTimeNs + ((framePresentationTimeNs - r0.syncFramePresentationTimeNs) / j3);
                if (isDriftTooLarge(j3, j2)) {
                    r0.haveSync = false;
                } else {
                    adjustedFrameTimeNs = j3;
                    adjustedReleaseTimeNs2 = (r0.syncUnadjustedReleaseTimeNs + adjustedFrameTimeNs) - r0.syncFramePresentationTimeNs;
                    adjustedFrameTimeNs = adjustedFrameTimeNs;
                }
                adjustedReleaseTimeNs = adjustedReleaseTimeNs2;
            } else {
                adjustedReleaseTimeNs = adjustedReleaseTimeNs2;
                if (isDriftTooLarge(framePresentationTimeNs, j2)) {
                    r0.haveSync = false;
                }
            }
        } else {
            adjustedReleaseTimeNs = adjustedReleaseTimeNs2;
        }
        if (!r0.haveSync) {
            r0.syncFramePresentationTimeNs = framePresentationTimeNs;
            r0.syncUnadjustedReleaseTimeNs = j2;
            r0.frameCount = 0;
            r0.haveSync = true;
        }
        r0.lastFramePresentationTimeUs = j;
        r0.pendingAdjustedFrameTimeNs = adjustedFrameTimeNs;
        VSyncSampler vSyncSampler = r0.vsyncSampler;
        if (vSyncSampler != null) {
            if (r0.vsyncDurationNs != C0555C.TIME_UNSET) {
                long sampledVsyncTimeNs = vSyncSampler.sampledVsyncTimeNs;
                if (sampledVsyncTimeNs == C0555C.TIME_UNSET) {
                    return adjustedReleaseTimeNs;
                }
                return closestVsync(adjustedReleaseTimeNs, sampledVsyncTimeNs, r0.vsyncDurationNs) - r0.vsyncOffsetNs;
            }
        }
        return adjustedReleaseTimeNs;
    }

    @TargetApi(17)
    private DefaultDisplayListener maybeBuildDefaultDisplayListenerV17(Context context) {
        DisplayManager manager = (DisplayManager) context.getSystemService("display");
        return manager == null ? null : new DefaultDisplayListener(manager);
    }

    private void updateDefaultDisplayRefreshRateParams() {
        Display defaultDisplay = this.windowManager.getDefaultDisplay();
        if (defaultDisplay != null) {
            double defaultDisplayRefreshRate = (double) defaultDisplay.getRefreshRate();
            Double.isNaN(defaultDisplayRefreshRate);
            this.vsyncDurationNs = (long) (1.0E9d / defaultDisplayRefreshRate);
            this.vsyncOffsetNs = (this.vsyncDurationNs * VSYNC_OFFSET_PERCENTAGE) / 100;
        }
    }

    private boolean isDriftTooLarge(long frameTimeNs, long releaseTimeNs) {
        return Math.abs((releaseTimeNs - this.syncUnadjustedReleaseTimeNs) - (frameTimeNs - this.syncFramePresentationTimeNs)) > MAX_ALLOWED_DRIFT_NS;
    }

    private static long closestVsync(long releaseTime, long sampledVsyncTime, long vsyncDuration) {
        long snappedBeforeNs;
        long snappedAfterNs;
        long snappedTimeNs = sampledVsyncTime + (vsyncDuration * ((releaseTime - sampledVsyncTime) / vsyncDuration));
        if (releaseTime <= snappedTimeNs) {
            snappedBeforeNs = snappedTimeNs - vsyncDuration;
            snappedAfterNs = snappedTimeNs;
        } else {
            snappedBeforeNs = snappedTimeNs;
            snappedAfterNs = snappedTimeNs + vsyncDuration;
        }
        return snappedAfterNs - releaseTime < releaseTime - snappedBeforeNs ? snappedAfterNs : snappedBeforeNs;
    }
}
