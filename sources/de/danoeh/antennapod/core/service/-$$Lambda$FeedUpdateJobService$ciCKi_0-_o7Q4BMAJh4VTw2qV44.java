package de.danoeh.antennapod.core.service;

import android.app.job.JobParameters;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FeedUpdateJobService$ciCKi_0-_o7Q4BMAJh4VTw2qV44 implements Runnable {
    private final /* synthetic */ FeedUpdateJobService f$0;
    private final /* synthetic */ JobParameters f$1;

    public /* synthetic */ -$$Lambda$FeedUpdateJobService$ciCKi_0-_o7Q4BMAJh4VTw2qV44(FeedUpdateJobService feedUpdateJobService, JobParameters jobParameters) {
        this.f$0 = feedUpdateJobService;
        this.f$1 = jobParameters;
    }

    public final void run() {
        FeedUpdateJobService.lambda$onStartJob$0(this.f$0, this.f$1);
    }
}
