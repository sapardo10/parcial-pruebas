package de.danoeh.antennapod.core.service.playback;

import android.content.Context;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import de.danoeh.antennapod.core.event.QueueEvent;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.util.playback.Playable;
import de.greenrobot.event.EventBus;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PlaybackServiceTaskManager {
    public static final int POSITION_SAVER_WAITING_INTERVAL = 5000;
    private static final int SCHED_EX_POOL_SIZE = 2;
    private static final String TAG = "PlaybackServiceTaskMgr";
    public static final int WIDGET_UPDATER_NOTIFICATION_INTERVAL = 1000;
    private final PSTMCallback callback;
    private volatile Future<?> chapterLoaderFuture;
    private final Context context;
    private ScheduledFuture<?> positionSaverFuture;
    private volatile Future<List<FeedItem>> queueFuture;
    private final ScheduledThreadPoolExecutor schedExecutor = new ScheduledThreadPoolExecutor(2, -$$Lambda$PlaybackServiceTaskManager$0OpYYZ3FjoTNYJd9OeaH28t2IWk.INSTANCE);
    private SleepTimer sleepTimer;
    private ScheduledFuture<?> sleepTimerFuture;
    private ScheduledFuture<?> widgetUpdaterFuture;

    public interface PSTMCallback {
        void onChapterLoaded(Playable playable);

        void onSleepTimerAlmostExpired();

        void onSleepTimerExpired();

        void onSleepTimerReset();

        void onWidgetUpdaterTick();

        void positionSaverTick();
    }

    class SleepTimer implements Runnable {
        private static final long NOTIFICATION_THRESHOLD = 10000;
        private static final String TAG = "SleepTimer";
        private static final long UPDATE_INTERVAL = 1000;
        private ShakeListener shakeListener;
        private final boolean shakeToReset;
        private long timeLeft;
        private final boolean vibrate;
        private final long waitingTime;

        public SleepTimer(long waitingTime, boolean shakeToReset, boolean vibrate) {
            this.waitingTime = waitingTime;
            this.timeLeft = waitingTime;
            this.shakeToReset = shakeToReset;
            this.vibrate = vibrate;
        }

        public void run() {
            Log.d(TAG, "Starting");
            boolean notifiedAlmostExpired = false;
            long lastTick = System.currentTimeMillis();
            while (this.timeLeft > 0) {
                try {
                    Thread.sleep(1000);
                    long now = System.currentTimeMillis();
                    this.timeLeft -= now - lastTick;
                    lastTick = now;
                    if (this.timeLeft < NOTIFICATION_THRESHOLD && !notifiedAlmostExpired) {
                        Log.d(TAG, "Sleep timer is about to expire");
                        if (this.vibrate) {
                            Vibrator v = (Vibrator) PlaybackServiceTaskManager.this.context.getSystemService("vibrator");
                            if (v != null) {
                                v.vibrate(500);
                            }
                        }
                        if (this.shakeListener == null && this.shakeToReset) {
                            this.shakeListener = new ShakeListener(PlaybackServiceTaskManager.this.context, this);
                        }
                        PlaybackServiceTaskManager.this.callback.onSleepTimerAlmostExpired();
                        notifiedAlmostExpired = true;
                    }
                    if (this.timeLeft <= 0) {
                        Log.d(TAG, "Sleep timer expired");
                        if (this.shakeListener != null) {
                            this.shakeListener.pause();
                            this.shakeListener = null;
                        }
                        if (Thread.currentThread().isInterrupted()) {
                            Log.d(TAG, "Sleep timer interrupted");
                        } else {
                            PlaybackServiceTaskManager.this.callback.onSleepTimerExpired();
                        }
                    }
                } catch (InterruptedException e) {
                    Log.d(TAG, "Thread was interrupted while waiting");
                    e.printStackTrace();
                    return;
                }
            }
        }

        public long getWaitingTime() {
            return this.timeLeft;
        }

        public void onShake() {
            PlaybackServiceTaskManager.this.setSleepTimer(this.waitingTime, this.shakeToReset, this.vibrate);
            PlaybackServiceTaskManager.this.callback.onSleepTimerReset();
            this.shakeListener.pause();
            this.shakeListener = null;
        }
    }

    public PlaybackServiceTaskManager(@NonNull Context context, @NonNull PSTMCallback callback) {
        this.context = context;
        this.callback = callback;
        loadQueue();
        EventBus.getDefault().register(this);
    }

    static /* synthetic */ Thread lambda$new$0(Runnable r) {
        Thread t = new Thread(r);
        t.setPriority(1);
        return t;
    }

    public void onEvent(QueueEvent event) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onEvent(QueueEvent ");
        stringBuilder.append(event);
        stringBuilder.append(")");
        Log.d(str, stringBuilder.toString());
        cancelQueueLoader();
        loadQueue();
    }

    private synchronized boolean isQueueLoaderActive() {
        boolean z;
        z = (this.queueFuture == null || this.queueFuture.isDone()) ? false : true;
        return z;
    }

    private synchronized void cancelQueueLoader() {
        if (isQueueLoaderActive()) {
            this.queueFuture.cancel(true);
        }
    }

    private synchronized void loadQueue() {
        if (!isQueueLoaderActive()) {
            this.queueFuture = this.schedExecutor.submit(-$$Lambda$W41vErsRVBKwiIn04LXxvD3pKVM.INSTANCE);
        }
    }

    public synchronized List<FeedItem> getQueueIfLoaded() {
        if (!this.queueFuture.isDone()) {
            return null;
        }
        try {
            return (List) this.queueFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized List<FeedItem> getQueue() throws InterruptedException {
        try {
        } catch (ExecutionException e) {
            throw new IllegalArgumentException(e);
        }
        return (List) this.queueFuture.get();
    }

    public synchronized void startPositionSaver() {
        if (isPositionSaverActive()) {
            Log.d(TAG, "Call to startPositionSaver was ignored.");
        } else {
            PSTMCallback pSTMCallback = this.callback;
            pSTMCallback.getClass();
            this.positionSaverFuture = this.schedExecutor.scheduleWithFixedDelay(new -$$Lambda$LFpxw6tKmHOSr0_Ya_VKL1YcXdY(pSTMCallback), DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS, TimeUnit.MILLISECONDS);
            Log.d(TAG, "Started PositionSaver");
        }
    }

    public synchronized boolean isPositionSaverActive() {
        boolean z;
        z = (this.positionSaverFuture == null || this.positionSaverFuture.isCancelled() || this.positionSaverFuture.isDone()) ? false : true;
        return z;
    }

    public synchronized void cancelPositionSaver() {
        if (isPositionSaverActive()) {
            this.positionSaverFuture.cancel(false);
            Log.d(TAG, "Cancelled PositionSaver");
        }
    }

    public synchronized void startWidgetUpdater() {
        if (isWidgetUpdaterActive()) {
            Log.d(TAG, "Call to startWidgetUpdater was ignored.");
        } else {
            PSTMCallback pSTMCallback = this.callback;
            pSTMCallback.getClass();
            this.widgetUpdaterFuture = this.schedExecutor.scheduleWithFixedDelay(new -$$Lambda$IEUbFz6W-YIKKsBf0btNELyYNdc(pSTMCallback), 1000, 1000, TimeUnit.MILLISECONDS);
            Log.d(TAG, "Started WidgetUpdater");
        }
    }

    public synchronized void setSleepTimer(long waitingTime, boolean shakeToReset, boolean vibrate) {
        if (waitingTime > 0) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Setting sleep timer to ");
            stringBuilder.append(Long.toString(waitingTime));
            stringBuilder.append(" milliseconds");
            Log.d(str, stringBuilder.toString());
            if (isSleepTimerActive()) {
                this.sleepTimerFuture.cancel(true);
            }
            this.sleepTimer = new SleepTimer(waitingTime, shakeToReset, vibrate);
            this.sleepTimerFuture = this.schedExecutor.schedule(this.sleepTimer, 0, TimeUnit.MILLISECONDS);
        } else {
            throw new IllegalArgumentException("Waiting time <= 0");
        }
    }

    public synchronized boolean isSleepTimerActive() {
        boolean z;
        if (this.sleepTimer != null && this.sleepTimerFuture != null) {
            if (!this.sleepTimerFuture.isCancelled()) {
                if (!this.sleepTimerFuture.isDone()) {
                    if (this.sleepTimer.getWaitingTime() > 0) {
                        z = true;
                    }
                }
            }
        }
        z = false;
        return z;
    }

    public synchronized void disableSleepTimer() {
        if (isSleepTimerActive()) {
            Log.d(TAG, "Disabling sleep timer");
            this.sleepTimerFuture.cancel(true);
        }
    }

    public synchronized long getSleepTimerTimeLeft() {
        if (!isSleepTimerActive()) {
            return 0;
        }
        return this.sleepTimer.getWaitingTime();
    }

    public synchronized boolean isWidgetUpdaterActive() {
        boolean z;
        z = (this.widgetUpdaterFuture == null || this.widgetUpdaterFuture.isCancelled() || this.widgetUpdaterFuture.isDone()) ? false : true;
        return z;
    }

    public synchronized void cancelWidgetUpdater() {
        if (isWidgetUpdaterActive()) {
            this.widgetUpdaterFuture.cancel(false);
            Log.d(TAG, "Cancelled WidgetUpdater");
        }
    }

    private synchronized void cancelChapterLoader() {
        if (isChapterLoaderActive()) {
            this.chapterLoaderFuture.cancel(true);
        }
    }

    private synchronized boolean isChapterLoaderActive() {
        boolean z;
        z = (this.chapterLoaderFuture == null || this.chapterLoaderFuture.isDone()) ? false : true;
        return z;
    }

    public synchronized void startChapterLoader(@NonNull Playable media) {
        if (isChapterLoaderActive()) {
            cancelChapterLoader();
        }
        this.chapterLoaderFuture = this.schedExecutor.submit(new -$$Lambda$PlaybackServiceTaskManager$c09cOz04xoeyeRCAarj9igH4RA4(this, media));
    }

    public static /* synthetic */ void lambda$startChapterLoader$1(@NonNull PlaybackServiceTaskManager playbackServiceTaskManager, Playable media) {
        Log.d(TAG, "Chapter loader started");
        if (media.getChapters() == null) {
            media.loadChapterMarks();
            if (!Thread.currentThread().isInterrupted() && media.getChapters() != null) {
                playbackServiceTaskManager.callback.onChapterLoaded(media);
            }
        }
        Log.d(TAG, "Chapter loader stopped");
    }

    public synchronized void cancelAllTasks() {
        cancelPositionSaver();
        cancelWidgetUpdater();
        disableSleepTimer();
        cancelQueueLoader();
        cancelChapterLoader();
    }

    public synchronized void shutdown() {
        EventBus.getDefault().unregister(this);
        cancelAllTasks();
        this.schedExecutor.shutdown();
    }
}
