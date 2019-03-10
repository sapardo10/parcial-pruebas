package com.google.android.exoplayer2.offline;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import com.google.android.exoplayer2.offline.DownloadManager.Listener;
import com.google.android.exoplayer2.offline.DownloadManager.TaskState;
import com.google.android.exoplayer2.scheduler.Requirements;
import com.google.android.exoplayer2.scheduler.RequirementsWatcher;
import com.google.android.exoplayer2.scheduler.Scheduler;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.NotificationUtil;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.HashMap;

public abstract class DownloadService extends Service {
    public static final String ACTION_ADD = "com.google.android.exoplayer.downloadService.action.ADD";
    public static final String ACTION_INIT = "com.google.android.exoplayer.downloadService.action.INIT";
    public static final String ACTION_RELOAD_REQUIREMENTS = "com.google.android.exoplayer.downloadService.action.RELOAD_REQUIREMENTS";
    private static final String ACTION_RESTART = "com.google.android.exoplayer.downloadService.action.RESTART";
    private static final boolean DEBUG = false;
    public static final long DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL = 1000;
    private static final Requirements DEFAULT_REQUIREMENTS = new Requirements(1, false, false);
    public static final int FOREGROUND_NOTIFICATION_ID_NONE = 0;
    public static final String KEY_DOWNLOAD_ACTION = "download_action";
    public static final String KEY_FOREGROUND = "foreground";
    private static final String TAG = "DownloadService";
    private static final HashMap<Class<? extends DownloadService>, RequirementsHelper> requirementsHelpers = new HashMap();
    @Nullable
    private final String channelId;
    @StringRes
    private final int channelName;
    private DownloadManager downloadManager;
    private DownloadManagerListener downloadManagerListener;
    @Nullable
    private final ForegroundNotificationUpdater foregroundNotificationUpdater;
    private int lastStartId;
    private boolean startedInForeground;
    private boolean taskRemoved;

    private final class ForegroundNotificationUpdater implements Runnable {
        private final Handler handler = new Handler(Looper.getMainLooper());
        private boolean notificationDisplayed;
        private final int notificationId;
        private boolean periodicUpdatesStarted;
        private final long updateInterval;

        public ForegroundNotificationUpdater(int notificationId, long updateInterval) {
            this.notificationId = notificationId;
            this.updateInterval = updateInterval;
        }

        public void startPeriodicUpdates() {
            this.periodicUpdatesStarted = true;
            update();
        }

        public void stopPeriodicUpdates() {
            this.periodicUpdatesStarted = false;
            this.handler.removeCallbacks(this);
        }

        public void update() {
            TaskState[] taskStates = DownloadService.this.downloadManager.getAllTaskStates();
            DownloadService downloadService = DownloadService.this;
            downloadService.startForeground(this.notificationId, downloadService.getForegroundNotification(taskStates));
            this.notificationDisplayed = true;
            if (this.periodicUpdatesStarted) {
                this.handler.removeCallbacks(this);
                this.handler.postDelayed(this, this.updateInterval);
            }
        }

        public void showNotificationIfNotAlready() {
            if (!this.notificationDisplayed) {
                update();
            }
        }

        public void run() {
            update();
        }
    }

    private final class DownloadManagerListener implements Listener {
        private DownloadManagerListener() {
        }

        public void onInitialized(DownloadManager downloadManager) {
            DownloadService downloadService = DownloadService.this;
            downloadService.maybeStartWatchingRequirements(downloadService.getRequirements());
        }

        public void onTaskStateChanged(DownloadManager downloadManager, TaskState taskState) {
            DownloadService.this.onTaskStateChanged(taskState);
            if (DownloadService.this.foregroundNotificationUpdater == null) {
                return;
            }
            if (taskState.state == 1) {
                DownloadService.this.foregroundNotificationUpdater.startPeriodicUpdates();
            } else {
                DownloadService.this.foregroundNotificationUpdater.update();
            }
        }

        public final void onIdle(DownloadManager downloadManager) {
            DownloadService.this.stop();
        }
    }

    private static final class RequirementsHelper implements RequirementsWatcher.Listener {
        private final Context context;
        private final Requirements requirements;
        private final RequirementsWatcher requirementsWatcher;
        @Nullable
        private final Scheduler scheduler;
        private final Class<? extends DownloadService> serviceClass;

        private RequirementsHelper(Context context, Requirements requirements, @Nullable Scheduler scheduler, Class<? extends DownloadService> serviceClass) {
            this.context = context;
            this.requirements = requirements;
            this.scheduler = scheduler;
            this.serviceClass = serviceClass;
            this.requirementsWatcher = new RequirementsWatcher(context, this, requirements);
        }

        public void start() {
            this.requirementsWatcher.start();
        }

        public void stop() {
            this.requirementsWatcher.stop();
            Scheduler scheduler = this.scheduler;
            if (scheduler != null) {
                scheduler.cancel();
            }
        }

        public void requirementsMet(RequirementsWatcher requirementsWatcher) {
            try {
                notifyService();
                Scheduler scheduler = this.scheduler;
                if (scheduler != null) {
                    scheduler.cancel();
                }
            } catch (Exception e) {
            }
        }

        public void requirementsNotMet(RequirementsWatcher requirementsWatcher) {
            try {
                notifyService();
            } catch (Exception e) {
            }
            if (this.scheduler != null) {
                if (!this.scheduler.schedule(this.requirements, this.context.getPackageName(), DownloadService.ACTION_RESTART)) {
                    Log.m6e(DownloadService.TAG, "Scheduling downloads failed.");
                }
            }
        }

        private void notifyService() throws Exception {
            try {
                this.context.startService(DownloadService.getIntent(this.context, this.serviceClass, DownloadService.ACTION_INIT));
            } catch (IllegalStateException e) {
                throw new Exception(e);
            }
        }
    }

    protected abstract DownloadManager getDownloadManager();

    @Nullable
    protected abstract Scheduler getScheduler();

    protected DownloadService(int foregroundNotificationId) {
        this(foregroundNotificationId, 1000);
    }

    protected DownloadService(int foregroundNotificationId, long foregroundNotificationUpdateInterval) {
        this(foregroundNotificationId, foregroundNotificationUpdateInterval, null, 0);
    }

    protected DownloadService(int foregroundNotificationId, long foregroundNotificationUpdateInterval, @Nullable String channelId, @StringRes int channelName) {
        this.foregroundNotificationUpdater = foregroundNotificationId == 0 ? null : new ForegroundNotificationUpdater(foregroundNotificationId, foregroundNotificationUpdateInterval);
        this.channelId = channelId;
        this.channelName = channelName;
    }

    public static Intent buildAddActionIntent(Context context, Class<? extends DownloadService> clazz, DownloadAction downloadAction, boolean foreground) {
        return getIntent(context, clazz, ACTION_ADD).putExtra(KEY_DOWNLOAD_ACTION, downloadAction.toByteArray()).putExtra(KEY_FOREGROUND, foreground);
    }

    public static void startWithAction(Context context, Class<? extends DownloadService> clazz, DownloadAction downloadAction, boolean foreground) {
        Intent intent = buildAddActionIntent(context, clazz, downloadAction, foreground);
        if (foreground) {
            Util.startForegroundService(context, intent);
        } else {
            context.startService(intent);
        }
    }

    public static void start(Context context, Class<? extends DownloadService> clazz) {
        context.startService(getIntent(context, clazz, ACTION_INIT));
    }

    public static void startForeground(Context context, Class<? extends DownloadService> clazz) {
        Util.startForegroundService(context, getIntent(context, clazz, ACTION_INIT).putExtra(KEY_FOREGROUND, true));
    }

    public void onCreate() {
        logd("onCreate");
        String str = this.channelId;
        if (str != null) {
            NotificationUtil.createNotificationChannel(this, str, this.channelName, 2);
        }
        this.downloadManager = getDownloadManager();
        this.downloadManagerListener = new DownloadManagerListener();
        this.downloadManager.addListener(this.downloadManagerListener);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        int i;
        byte[] actionData;
        String str;
        Requirements requirements;
        this.lastStartId = startId;
        boolean z = false;
        this.taskRemoved = false;
        String intentAction = null;
        if (intent != null) {
            intentAction = intent.getAction();
            boolean z2 = this.startedInForeground;
            if (!intent.getBooleanExtra(KEY_FOREGROUND, false)) {
                if (!ACTION_RESTART.equals(intentAction)) {
                    i = 0;
                    this.startedInForeground = z2 | i;
                }
            }
            i = 1;
            this.startedInForeground = z2 | i;
        }
        if (intentAction == null) {
            intentAction = ACTION_INIT;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onStartCommand action: ");
        stringBuilder.append(intentAction);
        stringBuilder.append(" startId: ");
        stringBuilder.append(startId);
        logd(stringBuilder.toString());
        i = intentAction.hashCode();
        if (i != -871181424) {
            if (i != -608867945) {
                if (i != -382886238) {
                    if (i == 1015676687 && intentAction.equals(ACTION_INIT)) {
                        switch (z) {
                            case false:
                            case true:
                                break;
                            case true:
                                actionData = intent.getByteArrayExtra(KEY_DOWNLOAD_ACTION);
                                if (actionData != null) {
                                    try {
                                        this.downloadManager.handleAction(actionData);
                                        break;
                                    } catch (IOException e) {
                                        Log.m7e(TAG, "Failed to handle ADD action", e);
                                        break;
                                    }
                                }
                                Log.m6e(TAG, "Ignoring ADD action with no action data");
                                break;
                            case true:
                                stopWatchingRequirements();
                                break;
                            default:
                                str = TAG;
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("Ignoring unrecognized action: ");
                                stringBuilder.append(intentAction);
                                Log.m6e(str, stringBuilder.toString());
                                break;
                        }
                        requirements = getRequirements();
                        if (requirements.checkRequirements(this)) {
                            this.downloadManager.startDownloads();
                        } else {
                            this.downloadManager.stopDownloads();
                        }
                        maybeStartWatchingRequirements(requirements);
                        if (this.downloadManager.isIdle()) {
                            stop();
                        }
                        return 1;
                    }
                } else if (intentAction.equals(ACTION_ADD)) {
                    z = true;
                    switch (z) {
                        case false:
                        case true:
                            break;
                        case true:
                            actionData = intent.getByteArrayExtra(KEY_DOWNLOAD_ACTION);
                            if (actionData != null) {
                                Log.m6e(TAG, "Ignoring ADD action with no action data");
                                break;
                            }
                            this.downloadManager.handleAction(actionData);
                            break;
                        case true:
                            stopWatchingRequirements();
                            break;
                        default:
                            str = TAG;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Ignoring unrecognized action: ");
                            stringBuilder.append(intentAction);
                            Log.m6e(str, stringBuilder.toString());
                            break;
                    }
                    requirements = getRequirements();
                    if (requirements.checkRequirements(this)) {
                        this.downloadManager.stopDownloads();
                    } else {
                        this.downloadManager.startDownloads();
                    }
                    maybeStartWatchingRequirements(requirements);
                    if (this.downloadManager.isIdle()) {
                        stop();
                    }
                    return 1;
                }
            } else if (intentAction.equals(ACTION_RELOAD_REQUIREMENTS)) {
                z = true;
                switch (z) {
                    case false:
                    case true:
                        break;
                    case true:
                        actionData = intent.getByteArrayExtra(KEY_DOWNLOAD_ACTION);
                        if (actionData != null) {
                            this.downloadManager.handleAction(actionData);
                            break;
                        }
                        Log.m6e(TAG, "Ignoring ADD action with no action data");
                        break;
                    case true:
                        stopWatchingRequirements();
                        break;
                    default:
                        str = TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Ignoring unrecognized action: ");
                        stringBuilder.append(intentAction);
                        Log.m6e(str, stringBuilder.toString());
                        break;
                }
                requirements = getRequirements();
                if (requirements.checkRequirements(this)) {
                    this.downloadManager.startDownloads();
                } else {
                    this.downloadManager.stopDownloads();
                }
                maybeStartWatchingRequirements(requirements);
                if (this.downloadManager.isIdle()) {
                    stop();
                }
                return 1;
            }
        } else if (intentAction.equals(ACTION_RESTART)) {
            z = true;
            switch (z) {
                case false:
                case true:
                    break;
                case true:
                    actionData = intent.getByteArrayExtra(KEY_DOWNLOAD_ACTION);
                    if (actionData != null) {
                        Log.m6e(TAG, "Ignoring ADD action with no action data");
                        break;
                    }
                    this.downloadManager.handleAction(actionData);
                    break;
                case true:
                    stopWatchingRequirements();
                    break;
                default:
                    str = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Ignoring unrecognized action: ");
                    stringBuilder.append(intentAction);
                    Log.m6e(str, stringBuilder.toString());
                    break;
            }
            requirements = getRequirements();
            if (requirements.checkRequirements(this)) {
                this.downloadManager.stopDownloads();
            } else {
                this.downloadManager.startDownloads();
            }
            maybeStartWatchingRequirements(requirements);
            if (this.downloadManager.isIdle()) {
                stop();
            }
            return 1;
        }
        z = true;
        switch (z) {
            case false:
            case true:
                break;
            case true:
                actionData = intent.getByteArrayExtra(KEY_DOWNLOAD_ACTION);
                if (actionData != null) {
                    this.downloadManager.handleAction(actionData);
                    break;
                }
                Log.m6e(TAG, "Ignoring ADD action with no action data");
                break;
            case true:
                stopWatchingRequirements();
                break;
            default:
                str = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append("Ignoring unrecognized action: ");
                stringBuilder.append(intentAction);
                Log.m6e(str, stringBuilder.toString());
                break;
        }
        requirements = getRequirements();
        if (requirements.checkRequirements(this)) {
            this.downloadManager.startDownloads();
        } else {
            this.downloadManager.stopDownloads();
        }
        maybeStartWatchingRequirements(requirements);
        if (this.downloadManager.isIdle()) {
            stop();
        }
        return 1;
    }

    public void onTaskRemoved(Intent rootIntent) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onTaskRemoved rootIntent: ");
        stringBuilder.append(rootIntent);
        logd(stringBuilder.toString());
        this.taskRemoved = true;
    }

    public void onDestroy() {
        logd("onDestroy");
        ForegroundNotificationUpdater foregroundNotificationUpdater = this.foregroundNotificationUpdater;
        if (foregroundNotificationUpdater != null) {
            foregroundNotificationUpdater.stopPeriodicUpdates();
        }
        this.downloadManager.removeListener(this.downloadManagerListener);
        maybeStopWatchingRequirements();
    }

    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected Requirements getRequirements() {
        return DEFAULT_REQUIREMENTS;
    }

    protected Notification getForegroundNotification(TaskState[] taskStates) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getClass().getName());
        stringBuilder.append(" is started in the foreground but getForegroundNotification() is not implemented.");
        throw new IllegalStateException(stringBuilder.toString());
    }

    protected void onTaskStateChanged(TaskState taskState) {
    }

    private void maybeStartWatchingRequirements(Requirements requirements) {
        if (this.downloadManager.getDownloadCount() != 0) {
            Class<? extends DownloadService> clazz = getClass();
            if (((RequirementsHelper) requirementsHelpers.get(clazz)) == null) {
                RequirementsHelper requirementsHelper = new RequirementsHelper(this, requirements, getScheduler(), clazz);
                requirementsHelpers.put(clazz, requirementsHelper);
                requirementsHelper.start();
                logd("started watching requirements");
            }
        }
    }

    private void maybeStopWatchingRequirements() {
        if (this.downloadManager.getDownloadCount() <= 0) {
            stopWatchingRequirements();
        }
    }

    private void stopWatchingRequirements() {
        RequirementsHelper requirementsHelper = (RequirementsHelper) requirementsHelpers.remove(getClass());
        if (requirementsHelper != null) {
            requirementsHelper.stop();
            logd("stopped watching requirements");
        }
    }

    private void stop() {
        ForegroundNotificationUpdater foregroundNotificationUpdater = this.foregroundNotificationUpdater;
        if (foregroundNotificationUpdater != null) {
            foregroundNotificationUpdater.stopPeriodicUpdates();
            if (this.startedInForeground && Util.SDK_INT >= 26) {
                this.foregroundNotificationUpdater.showNotificationIfNotAlready();
            }
        }
        if (Util.SDK_INT >= 28 || !this.taskRemoved) {
            boolean stopSelfResult = stopSelfResult(this.lastStartId);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("stopSelf(");
            stringBuilder.append(this.lastStartId);
            stringBuilder.append(") result: ");
            stringBuilder.append(stopSelfResult);
            logd(stringBuilder.toString());
            return;
        }
        stopSelf();
        logd("stopSelf()");
    }

    private void logd(String message) {
    }

    private static Intent getIntent(Context context, Class<? extends DownloadService> clazz, String action) {
        return new Intent(context, clazz).setAction(action);
    }
}
