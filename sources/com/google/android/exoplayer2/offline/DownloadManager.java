package com.google.android.exoplayer2.offline;

import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.offline.DownloadAction.Deserializer;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

public final class DownloadManager {
    private static final boolean DEBUG = false;
    public static final int DEFAULT_MAX_SIMULTANEOUS_DOWNLOADS = 1;
    public static final int DEFAULT_MIN_RETRY_COUNT = 5;
    private static final String TAG = "DownloadManager";
    private final ActionFile actionFile;
    private final ArrayList<Task> activeDownloadTasks;
    private final Deserializer[] deserializers;
    private final DownloaderConstructorHelper downloaderConstructorHelper;
    private boolean downloadsStopped;
    private final Handler fileIOHandler;
    private final HandlerThread fileIOThread;
    private final Handler handler;
    private boolean initialized;
    private final CopyOnWriteArraySet<Listener> listeners;
    private final int maxActiveDownloadTasks;
    private final int minRetryCount;
    private int nextTaskId;
    private boolean released;
    private final ArrayList<Task> tasks;

    public interface Listener {
        void onIdle(DownloadManager downloadManager);

        void onInitialized(DownloadManager downloadManager);

        void onTaskStateChanged(DownloadManager downloadManager, TaskState taskState);
    }

    private static final class Task implements Runnable {
        public static final int STATE_QUEUED_CANCELING = 5;
        public static final int STATE_STARTED_CANCELING = 6;
        public static final int STATE_STARTED_STOPPING = 7;
        private final DownloadAction action;
        private volatile int currentState;
        private final DownloadManager downloadManager;
        private volatile Downloader downloader;
        private Throwable error;
        private final int id;
        private final int minRetryCount;
        private Thread thread;

        private Task(int id, DownloadManager downloadManager, DownloadAction action, int minRetryCount) {
            this.id = id;
            this.downloadManager = downloadManager;
            this.action = action;
            this.currentState = 0;
            this.minRetryCount = minRetryCount;
        }

        public TaskState getDownloadState() {
            return new TaskState(this.id, this.action, getExternalState(), getDownloadPercentage(), getDownloadedBytes(), this.error);
        }

        public boolean isFinished() {
            if (!(this.currentState == 4 || this.currentState == 2)) {
                if (this.currentState != 3) {
                    return false;
                }
            }
            return true;
        }

        public boolean isActive() {
            if (this.currentState == 5 || this.currentState == 1 || this.currentState == 7) {
                return true;
            }
            return this.currentState == 6;
        }

        public float getDownloadPercentage() {
            return this.downloader != null ? this.downloader.getDownloadPercentage() : -1.0f;
        }

        public long getDownloadedBytes() {
            return this.downloader != null ? this.downloader.getDownloadedBytes() : 0;
        }

        public String toString() {
            return super.toString();
        }

        private static String toString(byte[] data) {
            if (data.length > 100) {
                return "<data is too long>";
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append('\'');
            stringBuilder.append(Util.fromUtf8Bytes(data));
            stringBuilder.append('\'');
            return stringBuilder.toString();
        }

        private String getStateString() {
            switch (this.currentState) {
                case 5:
                case 6:
                    return "CANCELING";
                case 7:
                    return "STOPPING";
                default:
                    return TaskState.getStateString(this.currentState);
            }
        }

        private int getExternalState() {
            switch (this.currentState) {
                case 5:
                    return 0;
                case 6:
                case 7:
                    return 1;
                default:
                    return this.currentState;
            }
        }

        private void start() {
            if (changeStateAndNotify(0, 1)) {
                this.thread = new Thread(this);
                this.thread.start();
            }
        }

        private boolean canStart() {
            return this.currentState == 0;
        }

        private void cancel() {
            if (changeStateAndNotify(0, 5)) {
                this.downloadManager.handler.post(new -$$Lambda$DownloadManager$Task$BscZ_DsnJwLao_N7rZjz7bnzplk());
            } else if (changeStateAndNotify(1, 6)) {
                cancelDownload();
            }
        }

        private void stop() {
            if (changeStateAndNotify(1, 7)) {
                DownloadManager.logd("Stopping", this);
                cancelDownload();
            }
        }

        private boolean changeStateAndNotify(int oldState, int newState) {
            return changeStateAndNotify(oldState, newState, null);
        }

        private boolean changeStateAndNotify(int oldState, int newState, Throwable error) {
            boolean z = false;
            if (this.currentState != oldState) {
                return false;
            }
            this.currentState = newState;
            this.error = error;
            if (this.currentState != getExternalState()) {
                z = true;
            }
            if (!z) {
                this.downloadManager.onTaskStateChange(this);
            }
            return true;
        }

        private void cancelDownload() {
            if (this.downloader != null) {
                this.downloader.cancel();
            }
            this.thread.interrupt();
        }

        public void run() {
            int errorCount;
            long errorPosition;
            StringBuilder stringBuilder;
            DownloadManager.logd("Task is started", this);
            Throwable error = null;
            try {
                this.downloader = this.action.createDownloader(this.downloadManager.downloaderConstructorHelper);
                if (this.action.isRemoveAction) {
                    this.downloader.remove();
                } else {
                    errorCount = 0;
                    errorPosition = -1;
                    while (!Thread.interrupted()) {
                        this.downloader.download();
                    }
                }
            } catch (IOException e) {
                long downloadedBytes = this.downloader.getDownloadedBytes();
                if (downloadedBytes != errorPosition) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Reset error count. downloadedBytes = ");
                    stringBuilder.append(downloadedBytes);
                    DownloadManager.logd(stringBuilder.toString(), this);
                    errorPosition = downloadedBytes;
                    errorCount = 0;
                }
                if (this.currentState == 1) {
                    errorCount++;
                    if (errorCount <= this.minRetryCount) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Download error. Retry ");
                        stringBuilder.append(errorCount);
                        DownloadManager.logd(stringBuilder.toString(), this);
                        Thread.sleep((long) getRetryDelayMillis(errorCount));
                    }
                }
                throw e;
            } catch (Throwable e2) {
                error = e2;
            }
            this.downloadManager.handler.post(new -$$Lambda$DownloadManager$Task$tMCSa8vI5Qy5JY5aoxlLoYvc2xQ(this, error));
        }

        public static /* synthetic */ void lambda$run$1(Task task, Throwable finalError) {
            if (!task.changeStateAndNotify(1, finalError != null ? 4 : 2, finalError)) {
                if (!task.changeStateAndNotify(6, 3)) {
                    if (!task.changeStateAndNotify(7, 0)) {
                        throw new IllegalStateException();
                    }
                }
            }
        }

        private int getRetryDelayMillis(int errorCount) {
            return Math.min((errorCount - 1) * 1000, 5000);
        }
    }

    public static final class TaskState {
        public static final int STATE_CANCELED = 3;
        public static final int STATE_COMPLETED = 2;
        public static final int STATE_FAILED = 4;
        public static final int STATE_QUEUED = 0;
        public static final int STATE_STARTED = 1;
        public final DownloadAction action;
        public final float downloadPercentage;
        public final long downloadedBytes;
        public final Throwable error;
        public final int state;
        public final int taskId;

        public static String getStateString(int state) {
            switch (state) {
                case 0:
                    return "QUEUED";
                case 1:
                    return "STARTED";
                case 2:
                    return "COMPLETED";
                case 3:
                    return "CANCELED";
                case 4:
                    return "FAILED";
                default:
                    throw new IllegalStateException();
            }
        }

        private TaskState(int taskId, DownloadAction action, int state, float downloadPercentage, long downloadedBytes, Throwable error) {
            this.taskId = taskId;
            this.action = action;
            this.state = state;
            this.downloadPercentage = downloadPercentage;
            this.downloadedBytes = downloadedBytes;
            this.error = error;
        }
    }

    public DownloadManager(Cache cache, Factory upstreamDataSourceFactory, File actionSaveFile, Deserializer... deserializers) {
        this(new DownloaderConstructorHelper(cache, upstreamDataSourceFactory), actionSaveFile, deserializers);
    }

    public DownloadManager(DownloaderConstructorHelper constructorHelper, File actionFile, Deserializer... deserializers) {
        this(constructorHelper, 1, 5, actionFile, deserializers);
    }

    public DownloadManager(DownloaderConstructorHelper constructorHelper, int maxSimultaneousDownloads, int minRetryCount, File actionFile, Deserializer... deserializers) {
        Deserializer[] deserializerArr;
        this.downloaderConstructorHelper = constructorHelper;
        this.maxActiveDownloadTasks = maxSimultaneousDownloads;
        this.minRetryCount = minRetryCount;
        this.actionFile = new ActionFile(actionFile);
        if (deserializers.length > 0) {
            deserializerArr = deserializers;
        } else {
            deserializerArr = DownloadAction.getDefaultDeserializers();
        }
        this.deserializers = deserializerArr;
        this.downloadsStopped = true;
        this.tasks = new ArrayList();
        this.activeDownloadTasks = new ArrayList();
        Looper looper = Looper.myLooper();
        if (looper == null) {
            looper = Looper.getMainLooper();
        }
        this.handler = new Handler(looper);
        this.fileIOThread = new HandlerThread("DownloadManager file i/o");
        this.fileIOThread.start();
        this.fileIOHandler = new Handler(this.fileIOThread.getLooper());
        this.listeners = new CopyOnWriteArraySet();
        loadActions();
        logd("Created");
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    public void startDownloads() {
        Assertions.checkState(this.released ^ 1);
        if (this.downloadsStopped) {
            this.downloadsStopped = false;
            maybeStartTasks();
            logd("Downloads are started");
        }
    }

    public void stopDownloads() {
        Assertions.checkState(this.released ^ true);
        if (!this.downloadsStopped) {
            this.downloadsStopped = true;
            for (int i = 0; i < this.activeDownloadTasks.size(); i++) {
                ((Task) this.activeDownloadTasks.get(i)).stop();
            }
            logd("Downloads are stopping");
        }
    }

    public int handleAction(byte[] actionData) throws IOException {
        Assertions.checkState(this.released ^ 1);
        return handleAction(DownloadAction.deserializeFromStream(this.deserializers, new ByteArrayInputStream(actionData)));
    }

    public int handleAction(DownloadAction action) {
        Assertions.checkState(this.released ^ 1);
        Task task = addTaskForAction(action);
        if (this.initialized) {
            saveActions();
            maybeStartTasks();
            if (task.currentState == 0) {
                notifyListenersTaskStateChange(task);
            }
        }
        return task.id;
    }

    public int getTaskCount() {
        Assertions.checkState(this.released ^ 1);
        return this.tasks.size();
    }

    public int getDownloadCount() {
        int count = 0;
        for (int i = 0; i < this.tasks.size(); i++) {
            if (!((Task) this.tasks.get(i)).action.isRemoveAction) {
                count++;
            }
        }
        return count;
    }

    @Nullable
    public TaskState getTaskState(int taskId) {
        Assertions.checkState(this.released ^ 1);
        for (int i = 0; i < this.tasks.size(); i++) {
            Task task = (Task) this.tasks.get(i);
            if (task.id == taskId) {
                return task.getDownloadState();
            }
        }
        return null;
    }

    public TaskState[] getAllTaskStates() {
        Assertions.checkState(this.released ^ 1);
        TaskState[] states = new TaskState[this.tasks.size()];
        for (int i = 0; i < states.length; i++) {
            states[i] = ((Task) this.tasks.get(i)).getDownloadState();
        }
        return states;
    }

    public boolean isInitialized() {
        Assertions.checkState(this.released ^ 1);
        return this.initialized;
    }

    public boolean isIdle() {
        Assertions.checkState(this.released ^ true);
        if (!this.initialized) {
            return false;
        }
        for (int i = 0; i < this.tasks.size(); i++) {
            if (((Task) this.tasks.get(i)).isActive()) {
                return false;
            }
        }
        return true;
    }

    public void release() {
        if (!this.released) {
            this.released = true;
            for (int i = 0; i < this.tasks.size(); i++) {
                ((Task) this.tasks.get(i)).stop();
            }
            ConditionVariable fileIOFinishedCondition = new ConditionVariable();
            Handler handler = this.fileIOHandler;
            fileIOFinishedCondition.getClass();
            handler.post(new -$$Lambda$xEDVsWySjOhZCU-CTVGu6ziJ2xc(fileIOFinishedCondition));
            fileIOFinishedCondition.block();
            this.fileIOThread.quit();
            logd("Released");
        }
    }

    private Task addTaskForAction(DownloadAction action) {
        int i = this.nextTaskId;
        this.nextTaskId = i + 1;
        Task task = new Task(i, this, action, this.minRetryCount);
        this.tasks.add(task);
        logd("Task is added", task);
        return task;
    }

    private void maybeStartTasks() {
        if (this.initialized) {
            if (!this.released) {
                boolean z;
                int i;
                Task task;
                DownloadAction action;
                boolean isRemoveAction;
                boolean canStartTask;
                int j;
                Task otherTask;
                StringBuilder stringBuilder;
                if (!this.downloadsStopped) {
                    if (this.activeDownloadTasks.size() != this.maxActiveDownloadTasks) {
                        z = false;
                        for (i = 0; i < this.tasks.size(); i++) {
                            task = (Task) this.tasks.get(i);
                            if (!task.canStart()) {
                                action = task.action;
                                isRemoveAction = action.isRemoveAction;
                                if (isRemoveAction || !skipDownloadActions) {
                                    canStartTask = true;
                                    for (j = 0; j < i; j++) {
                                        otherTask = (Task) this.tasks.get(j);
                                        if (otherTask.action.isSameMedia(action)) {
                                            if (!isRemoveAction) {
                                                canStartTask = false;
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append(task);
                                                stringBuilder.append(" clashes with ");
                                                stringBuilder.append(otherTask);
                                                logd(stringBuilder.toString());
                                                otherTask.cancel();
                                            } else if (otherTask.action.isRemoveAction) {
                                                canStartTask = false;
                                                z = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (canStartTask) {
                                        task.start();
                                        if (!isRemoveAction) {
                                            this.activeDownloadTasks.add(task);
                                            z = this.activeDownloadTasks.size() != this.maxActiveDownloadTasks;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                z = true;
                for (i = 0; i < this.tasks.size(); i++) {
                    task = (Task) this.tasks.get(i);
                    if (!task.canStart()) {
                        action = task.action;
                        isRemoveAction = action.isRemoveAction;
                        if (!isRemoveAction) {
                        }
                        canStartTask = true;
                        for (j = 0; j < i; j++) {
                            otherTask = (Task) this.tasks.get(j);
                            if (otherTask.action.isSameMedia(action)) {
                                if (!isRemoveAction) {
                                    if (otherTask.action.isRemoveAction) {
                                        canStartTask = false;
                                        z = true;
                                        break;
                                    }
                                } else {
                                    canStartTask = false;
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append(task);
                                    stringBuilder.append(" clashes with ");
                                    stringBuilder.append(otherTask);
                                    logd(stringBuilder.toString());
                                    otherTask.cancel();
                                }
                            }
                        }
                        if (canStartTask) {
                            task.start();
                            if (!isRemoveAction) {
                                this.activeDownloadTasks.add(task);
                                if (this.activeDownloadTasks.size() != this.maxActiveDownloadTasks) {
                                }
                                z = this.activeDownloadTasks.size() != this.maxActiveDownloadTasks;
                            }
                        }
                    }
                }
            }
        }
    }

    private void maybeNotifyListenersIdle() {
        if (isIdle()) {
            logd("Notify idle state");
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                ((Listener) it.next()).onIdle(this);
            }
        }
    }

    private void onTaskStateChange(Task task) {
        if (!this.released) {
            boolean stopped = task.isActive() ^ 1;
            if (stopped) {
                this.activeDownloadTasks.remove(task);
            }
            notifyListenersTaskStateChange(task);
            if (task.isFinished()) {
                this.tasks.remove(task);
                saveActions();
            }
            if (stopped) {
                maybeStartTasks();
                maybeNotifyListenersIdle();
            }
        }
    }

    private void notifyListenersTaskStateChange(Task task) {
        logd("Task state is changed", task);
        TaskState taskState = task.getDownloadState();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((Listener) it.next()).onTaskStateChanged(this, taskState);
        }
    }

    private void loadActions() {
        this.fileIOHandler.post(new -$$Lambda$DownloadManager$0LJSbWXADhROJkmo8hGQn9eqfcs());
    }

    public static /* synthetic */ void lambda$loadActions$1(DownloadManager downloadManager) {
        DownloadAction[] loadedActions;
        try {
            loadedActions = downloadManager.actionFile.load(downloadManager.deserializers);
            logd("Action file is loaded.");
        } catch (Throwable e) {
            Log.m7e(TAG, "Action file loading failed.", e);
            loadedActions = new DownloadAction[0];
        }
        downloadManager.handler.post(new -$$Lambda$DownloadManager$tqhW7d1-gBwDY6S8i1JfVeIyzSI(downloadManager, loadedActions));
    }

    public static /* synthetic */ void lambda$null$0(DownloadManager downloadManager, DownloadAction[] actions) {
        if (!downloadManager.released) {
            int i;
            List<Task> pendingTasks = new ArrayList(downloadManager.tasks);
            downloadManager.tasks.clear();
            for (DownloadAction action : actions) {
                downloadManager.addTaskForAction(action);
            }
            logd("Tasks are created.");
            downloadManager.initialized = true;
            Iterator it = downloadManager.listeners.iterator();
            while (it.hasNext()) {
                ((Listener) it.next()).onInitialized(downloadManager);
            }
            if (!pendingTasks.isEmpty()) {
                downloadManager.tasks.addAll(pendingTasks);
                downloadManager.saveActions();
            }
            downloadManager.maybeStartTasks();
            for (i = 0; i < downloadManager.tasks.size(); i++) {
                Task task = (Task) downloadManager.tasks.get(i);
                if (task.currentState == 0) {
                    downloadManager.notifyListenersTaskStateChange(task);
                }
            }
        }
    }

    private void saveActions() {
        if (!this.released) {
            DownloadAction[] actions = new DownloadAction[this.tasks.size()];
            for (int i = 0; i < this.tasks.size(); i++) {
                actions[i] = ((Task) this.tasks.get(i)).action;
            }
            this.fileIOHandler.post(new -$$Lambda$DownloadManager$SgHHqKrgOJ8vvRnakgUybwmDe2w(this, actions));
        }
    }

    public static /* synthetic */ void lambda$saveActions$2(DownloadManager downloadManager, DownloadAction[] actions) {
        try {
            downloadManager.actionFile.store(actions);
            logd("Actions persisted.");
        } catch (IOException e) {
            Log.m7e(TAG, "Persisting actions failed.", e);
        }
    }

    private static void logd(String message) {
    }

    private static void logd(String message, Task task) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(message);
        stringBuilder.append(": ");
        stringBuilder.append(task);
        logd(stringBuilder.toString());
    }
}
