package de.danoeh.antennapod.core.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.support.annotation.RequiresApi;
import android.util.Log;
import de.danoeh.antennapod.core.ClientConfig;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.util.FeedUpdateUtils;

@RequiresApi(api = 21)
public class FeedUpdateJobService extends JobService {
    private static final String TAG = "FeedUpdateJobService";

    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        ClientConfig.initialize(getApplicationContext());
        FeedUpdateUtils.startAutoUpdate(getApplicationContext(), new -$$Lambda$FeedUpdateJobService$ciCKi_0-_o7Q4BMAJh4VTw2qV44(this, params));
        return true;
    }

    public static /* synthetic */ void lambda$onStartJob$0(FeedUpdateJobService feedUpdateJobService, JobParameters params) {
        UserPreferences.restartUpdateAlarm(false);
        feedUpdateJobService.jobFinished(params, false);
    }

    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
