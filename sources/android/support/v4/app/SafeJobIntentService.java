package android.support.v4.app;

import android.app.job.JobParameters;
import android.app.job.JobServiceEngine;
import android.app.job.JobWorkItem;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

public abstract class SafeJobIntentService extends JobIntentService {

    @RequiresApi(26)
    static final class SafeJobServiceEngineImpl extends JobServiceEngine implements CompatJobEngine {
        static final boolean DEBUG = false;
        static final String TAG = "JobServiceEngineImpl";
        final Object mLock = new Object();
        JobParameters mParams;
        final JobIntentService mService;

        final class WrapperWorkItem implements GenericWorkItem {
            final JobWorkItem mJobWork;

            WrapperWorkItem(JobWorkItem jobWork) {
                this.mJobWork = jobWork;
            }

            public Intent getIntent() {
                return this.mJobWork.getIntent();
            }

            public void complete() {
                synchronized (SafeJobServiceEngineImpl.this.mLock) {
                    if (SafeJobServiceEngineImpl.this.mParams != null) {
                        try {
                            SafeJobServiceEngineImpl.this.mParams.completeWork(this.mJobWork);
                        } catch (SecurityException e) {
                            Log.e(SafeJobServiceEngineImpl.TAG, Log.getStackTraceString(e));
                        }
                    }
                }
            }
        }

        SafeJobServiceEngineImpl(JobIntentService service) {
            super(service);
            this.mService = service;
        }

        public IBinder compatGetBinder() {
            return getBinder();
        }

        public boolean onStartJob(JobParameters params) {
            this.mParams = params;
            this.mService.ensureProcessorRunningLocked(false);
            return true;
        }

        public boolean onStopJob(JobParameters params) {
            boolean result = this.mService.doStopCurrentWork();
            synchronized (this.mLock) {
                this.mParams = null;
            }
            return result;
        }

        public GenericWorkItem dequeueWork() {
            JobWorkItem work = null;
            synchronized (this.mLock) {
                if (this.mParams == null) {
                    return null;
                }
                try {
                    work = this.mParams.dequeueWork();
                } catch (SecurityException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
            if (work == null) {
                return null;
            }
            work.getIntent().setExtrasClassLoader(this.mService.getClassLoader());
            return new WrapperWorkItem(work);
        }
    }

    public void onCreate() {
        super.onCreate();
        if (VERSION.SDK_INT >= 26) {
            this.mJobImpl = new SafeJobServiceEngineImpl(this);
        }
    }
}
