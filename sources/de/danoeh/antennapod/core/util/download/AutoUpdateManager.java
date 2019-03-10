package de.danoeh.antennapod.core.util.download;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobInfo.Builder;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.receiver.FeedUpdateReceiver;
import de.danoeh.antennapod.core.service.FeedUpdateJobService;
import de.danoeh.antennapod.core.storage.DBTasks;
import de.danoeh.antennapod.core.util.Converter;
import de.danoeh.antennapod.core.util.FeedUpdateUtils;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class AutoUpdateManager {
    private static final int JOB_ID_FEED_UPDATE = 42;
    private static final String TAG = "AutoUpdateManager";

    private AutoUpdateManager() {
    }

    public static void restartUpdateIntervalAlarm(Context context, long triggerAtMillis, long intervalMillis) {
        Log.d(TAG, "Restarting update alarm.");
        if (VERSION.SDK_INT >= 24) {
            restartJobServiceInterval(context, intervalMillis);
        } else {
            restartAlarmManagerInterval(context, triggerAtMillis, intervalMillis);
        }
    }

    public static void restartUpdateTimeOfDayAlarm(Context context, int hoursOfDay, int minute) {
        Log.d(TAG, "Restarting update alarm.");
        Calendar now = Calendar.getInstance();
        Calendar alarm = (Calendar) now.clone();
        alarm.set(11, hoursOfDay);
        alarm.set(12, minute);
        if (!alarm.before(now)) {
            if (!alarm.equals(now)) {
                if (VERSION.SDK_INT < 24) {
                    restartJobServiceTriggerAt(context, alarm.getTimeInMillis() - now.getTimeInMillis());
                } else {
                    restartAlarmManagerTimeOfDay(context, alarm);
                }
            }
        }
        alarm.add(5, 1);
        if (VERSION.SDK_INT < 24) {
            restartAlarmManagerTimeOfDay(context, alarm);
        } else {
            restartJobServiceTriggerAt(context, alarm.getTimeInMillis() - now.getTimeInMillis());
        }
    }

    @RequiresApi(api = 21)
    private static Builder getFeedUpdateJobBuilder(Context context) {
        Builder builder = new Builder(42, new ComponentName(context, FeedUpdateJobService.class));
        builder.setRequiredNetworkType(1);
        builder.setPersisted(true);
        return builder;
    }

    @RequiresApi(api = 24)
    private static void restartJobServiceInterval(Context context, long intervalMillis) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JobScheduler.class);
        if (jobScheduler == null) {
            Log.d(TAG, "JobScheduler was null.");
            return;
        }
        JobInfo oldJob = jobScheduler.getPendingJob(42);
        if (oldJob == null || oldJob.getIntervalMillis() != intervalMillis) {
            Builder builder = getFeedUpdateJobBuilder(context);
            builder.setPeriodic(intervalMillis);
            jobScheduler.cancel(42);
            if (intervalMillis <= 0) {
                Log.d(TAG, "Automatic update was deactivated");
                return;
            }
            jobScheduler.schedule(builder.build());
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("JobScheduler was set at interval ");
            stringBuilder.append(intervalMillis);
            Log.d(str, stringBuilder.toString());
            return;
        }
        str = TAG;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("JobScheduler was already set at interval ");
        stringBuilder2.append(intervalMillis);
        stringBuilder2.append(", ignoring.");
        Log.d(str, stringBuilder2.toString());
    }

    private static void restartAlarmManagerInterval(Context context, long triggerAtMillis, long intervalMillis) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM);
        if (alarmManager == null) {
            Log.d(TAG, "AlarmManager was null");
            return;
        }
        PendingIntent updateIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, FeedUpdateReceiver.class), 0);
        alarmManager.cancel(updateIntent);
        if (intervalMillis <= 0) {
            Log.d(TAG, "Automatic update was deactivated");
            return;
        }
        alarmManager.set(2, SystemClock.elapsedRealtime() + triggerAtMillis, updateIntent);
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Changed alarm to new interval ");
        stringBuilder.append(TimeUnit.MILLISECONDS.toHours(intervalMillis));
        stringBuilder.append(" h");
        Log.d(str, stringBuilder.toString());
    }

    @RequiresApi(api = 24)
    private static void restartJobServiceTriggerAt(Context context, long triggerAtMillis) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JobScheduler.class);
        if (jobScheduler == null) {
            Log.d(TAG, "JobScheduler was null.");
            return;
        }
        Builder builder = getFeedUpdateJobBuilder(context);
        builder.setMinimumLatency(triggerAtMillis);
        jobScheduler.cancel(42);
        jobScheduler.schedule(builder.build());
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("JobScheduler was set for ");
        stringBuilder.append(triggerAtMillis);
        Log.d(str, stringBuilder.toString());
    }

    private static void restartAlarmManagerTimeOfDay(Context context, Calendar alarm) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM);
        if (alarmManager == null) {
            Log.d(TAG, "AlarmManager was null");
            return;
        }
        PendingIntent updateIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, FeedUpdateReceiver.class), 0);
        alarmManager.cancel(updateIntent);
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Alarm set for: ");
        stringBuilder.append(alarm.toString());
        stringBuilder.append(" : ");
        stringBuilder.append(alarm.getTimeInMillis());
        Log.d(str, stringBuilder.toString());
        alarmManager.set(0, alarm.getTimeInMillis(), updateIntent);
        String str2 = TAG;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Changed alarm to new time of day ");
        stringBuilder2.append(alarm.get(11));
        stringBuilder2.append(":");
        stringBuilder2.append(alarm.get(12));
        Log.d(str2, stringBuilder2.toString());
    }

    public static void checkShouldRefreshFeeds(Context context) {
        long interval = 0;
        if (UserPreferences.getUpdateInterval() > 0) {
            interval = UserPreferences.getUpdateInterval();
        } else if (UserPreferences.getUpdateTimeOfDay().length > 0) {
            interval = TimeUnit.DAYS.toMillis(1);
        }
        if (interval != 0) {
            long lastRefresh = DBTasks.getLastRefreshAllFeedsTimeMillis(context);
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("last refresh: ");
            stringBuilder.append(Converter.getDurationStringLocalized(context, System.currentTimeMillis() - lastRefresh));
            stringBuilder.append(" ago");
            Log.d(str, stringBuilder.toString());
            if (lastRefresh <= System.currentTimeMillis() - interval) {
                FeedUpdateUtils.startAutoUpdate(context, null);
            }
        }
    }
}
