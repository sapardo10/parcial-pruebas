package android.support.wearable.complications;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.wearable.complications.IProviderInfoService.Stub;
import android.util.Log;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@TargetApi(24)
public class ProviderInfoRetriever {
    public static final String ACTION_GET_COMPLICATION_CONFIG = "android.support.wearable.complications.ACTION_GET_COMPLICATION_CONFIG";
    private static final String PROVIDER_INFO_SERVICE_CLASS = "com.google.android.clockwork.home.complications.ProviderInfoService";
    private static final String PROVIDER_INFO_SERVICE_PACKAGE = "com.google.android.wearable.app";
    private static final String TAG = "ProviderInfoRetriever";
    private static final long TIMEOUT_MILLIS = 5000;
    private final ServiceConnection mConn = new ProviderInfoServiceConnection();
    private final Context mContext;
    private final Executor mExecutor;
    private final CountDownLatch mLatch = new CountDownLatch(1);
    private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
    private IProviderInfoService mService;
    private final Object mServiceLock = new Object();

    public static abstract class OnProviderInfoReceivedCallback {
        public abstract void onProviderInfoReceived(int i, @Nullable ComplicationProviderInfo complicationProviderInfo);

        public void onRetrievalFailed() {
        }
    }

    private final class ProviderInfoServiceConnection implements ServiceConnection {
        private ProviderInfoServiceConnection() {
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            synchronized (ProviderInfoRetriever.this.mServiceLock) {
                ProviderInfoRetriever.this.mService = Stub.asInterface(service);
            }
            ProviderInfoRetriever.this.mLatch.countDown();
        }

        public void onServiceDisconnected(ComponentName name) {
            synchronized (ProviderInfoRetriever.this.mServiceLock) {
                ProviderInfoRetriever.this.mService = null;
            }
        }
    }

    public ProviderInfoRetriever(Context context, Executor executor) {
        this.mContext = context;
        this.mExecutor = executor;
    }

    public void init() {
        Intent intent = new Intent(ACTION_GET_COMPLICATION_CONFIG);
        intent.setClassName(PROVIDER_INFO_SERVICE_PACKAGE, PROVIDER_INFO_SERVICE_CLASS);
        this.mContext.bindService(intent, this.mConn, 1);
    }

    public void retrieveProviderInfo(final OnProviderInfoReceivedCallback callback, final ComponentName watchFaceComponent, final int... watchFaceComplicationIds) {
        this.mExecutor.execute(new Runnable() {

            /* renamed from: android.support.wearable.complications.ProviderInfoRetriever$1$2 */
            class C04092 implements Runnable {
                C04092() {
                }

                public void run() {
                    callback.onRetrievalFailed();
                }
            }

            public void run() {
                ComplicationProviderInfo[] infos = ProviderInfoRetriever.this.doRetrieveInfo(watchFaceComponent, watchFaceComplicationIds);
                if (infos != null) {
                    for (int i = 0; i < infos.length; i++) {
                        final int watchFaceComplicationId = watchFaceComplicationIds[i];
                        final ComplicationProviderInfo info = infos[i];
                        ProviderInfoRetriever.this.mMainThreadHandler.post(new Runnable() {
                            public void run() {
                                callback.onProviderInfoReceived(watchFaceComplicationId, info);
                            }
                        });
                    }
                    return;
                }
                ProviderInfoRetriever.this.mMainThreadHandler.post(new C04092());
            }
        });
    }

    public void release() {
        this.mContext.unbindService(this.mConn);
        synchronized (this.mServiceLock) {
            this.mService = null;
        }
        this.mLatch.countDown();
    }

    @Nullable
    @WorkerThread
    private ComplicationProviderInfo[] doRetrieveInfo(ComponentName watchFaceComponent, int... ids) {
        ComplicationProviderInfo[] complicationProviderInfoArr = null;
        try {
            if (this.mLatch.await(5000, TimeUnit.MILLISECONDS)) {
                synchronized (this.mServiceLock) {
                    if (this.mService != null) {
                        try {
                            complicationProviderInfoArr = this.mService.getProviderInfos(watchFaceComponent, ids);
                            return complicationProviderInfoArr;
                        } catch (RemoteException e) {
                            Log.w(TAG, "RemoteException from ProviderInfoService.", e);
                            return complicationProviderInfoArr;
                        }
                    }
                }
            } else {
                Log.w(TAG, "Timeout while waiting for service binding.");
                return null;
            }
        } catch (InterruptedException e2) {
            Log.w(TAG, "Interrupted while waiting for service binding.", e2);
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
