package android.support.wearable.authentication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.VisibleForTesting;
import android.support.wearable.authentication.IAuthenticationRequestService.Stub;
import android.util.Log;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;

public class OAuthClient {
    @VisibleForTesting
    static final String ACTION_OAUTH = "android.support.wearable.authentication.action.OAUTH";
    private static final String ANDROID_WEAR_PACKAGE_NAME = "com.google.android.wearable.app";
    public static final int ERROR_PHONE_UNAVAILABLE = 1;
    public static final int ERROR_UNSUPPORTED = 0;
    public static final String KEY_ERROR_CODE = "errorCode";
    public static final String KEY_PACKAGE_NAME = "packageName";
    public static final String KEY_REQUEST_URL = "requestUrl";
    public static final String KEY_RESPONSE_URL = "responseUrl";
    @Deprecated
    public static final String LEGACY_WEAR_REDIRECT_URL_PREFIX = "https://www.android.com/wear/3p_auth/";
    private static final int STATE_CONNECTED = 2;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_DISCONNECTED = 0;
    private static final String TAG = "OAuth";
    public static final String WEAR_REDIRECT_URL_PREFIX = "https://wear.googleapis.com/3p_auth/";
    @Nullable
    private Throwable mAllocationSite = new Throwable("Explicit termination method 'destroy' not called");
    private final ServiceConnection mConnection = new OAuthConnection();
    private int mConnectionState = 0;
    private final Set<RequestCallback> mOutstandingRequests = new HashSet();
    private final String mPackageName;
    private final Queue<Runnable> mQueuedRunnables = new ArrayDeque();
    @Nullable
    private IAuthenticationRequestService mService;
    private final ServiceBinder mServiceBinder;
    private final Executor mUiThreadExecutor;

    /* renamed from: android.support.wearable.authentication.OAuthClient$2 */
    class C03972 implements Executor {
        final /* synthetic */ Context val$appContext;

        C03972(Context context) {
            this.val$appContext = context;
        }

        public void execute(Runnable command) {
            new Handler(this.val$appContext.getMainLooper()).post(command);
        }
    }

    public static abstract class Callback {
        @UiThread
        public abstract void onAuthorizationError(int i);

        @UiThread
        public abstract void onAuthorizationResponse(Uri uri, Uri uri2);
    }

    private final class OAuthConnection implements ServiceConnection {
        private OAuthConnection() {
        }

        @UiThread
        public void onServiceConnected(ComponentName name, IBinder boundService) {
            if (Log.isLoggable(OAuthClient.TAG, 3)) {
                Log.d(OAuthClient.TAG, "Connected to OAuth service");
            }
            OAuthClient.this.mService = Stub.asInterface(boundService);
            OAuthClient.this.mConnectionState = 2;
            while (!OAuthClient.this.mQueuedRunnables.isEmpty()) {
                ((Runnable) OAuthClient.this.mQueuedRunnables.poll()).run();
            }
        }

        @UiThread
        public void onServiceDisconnected(ComponentName name) {
            if (Log.isLoggable(OAuthClient.TAG, 3)) {
                Log.d(OAuthClient.TAG, "Disconnected from OAuth service");
            }
            OAuthClient.this.mService = null;
        }
    }

    interface ServiceBinder {
        boolean bindService(Intent intent, ServiceConnection serviceConnection, int i);

        void unbindService(ServiceConnection serviceConnection);
    }

    /* renamed from: android.support.wearable.authentication.OAuthClient$1 */
    class C09101 implements ServiceBinder {
        final /* synthetic */ Context val$appContext;

        C09101(Context context) {
            this.val$appContext = context;
        }

        public boolean bindService(Intent intent, ServiceConnection connection, int flags) {
            return this.val$appContext.bindService(intent, connection, flags);
        }

        public void unbindService(ServiceConnection connection) {
            this.val$appContext.unbindService(connection);
        }
    }

    private final class RequestCallback extends IAuthenticationRequestCallback.Stub {
        private final Callback mClientCallback;
        private final Uri mRequestUrl;

        private RequestCallback(Uri requestUrl, Callback clientCallback) {
            this.mRequestUrl = (Uri) OAuthClient.checkNotNull(requestUrl);
            this.mClientCallback = (Callback) OAuthClient.checkNotNull(clientCallback);
        }

        public void onResult(Bundle result) {
            final Uri responseUrl = (Uri) result.getParcelable(OAuthClient.KEY_RESPONSE_URL);
            final int error = result.getInt(OAuthClient.KEY_ERROR_CODE, -1);
            OAuthClient.this.mUiThreadExecutor.execute(new Runnable() {
                public void run() {
                    OAuthClient.this.removePendingCallback(RequestCallback.this);
                    if (error == -1) {
                        RequestCallback.this.mClientCallback.onAuthorizationResponse(RequestCallback.this.mRequestUrl, responseUrl);
                    } else {
                        RequestCallback.this.mClientCallback.onAuthorizationError(error);
                    }
                }
            });
        }
    }

    public static OAuthClient create(Context context) {
        Context appContext = context.getApplicationContext();
        return new OAuthClient(new C09101(appContext), new C03972(appContext), context.getPackageName());
    }

    @VisibleForTesting
    protected OAuthClient(ServiceBinder serviceBinder, Executor uiThreadExecutor, String packageName) {
        this.mPackageName = (String) checkNotNull(packageName);
        this.mServiceBinder = (ServiceBinder) checkNotNull(serviceBinder);
        this.mUiThreadExecutor = (Executor) checkNotNull(uiThreadExecutor);
    }

    @UiThread
    public void sendAuthorizationRequest(final Uri requestUrl, final Callback clientCallback) {
        if (this.mConnectionState == 0) {
            connect();
        }
        whenConnected(new Runnable() {
            public void run() {
                Bundle request = new Bundle();
                request.putParcelable(OAuthClient.KEY_REQUEST_URL, requestUrl);
                request.putString(OAuthClient.KEY_PACKAGE_NAME, OAuthClient.this.mPackageName);
                RequestCallback callback = new RequestCallback(requestUrl, clientCallback);
                OAuthClient.this.mOutstandingRequests.add(callback);
                try {
                    OAuthClient.this.mService.openUrl(request, callback);
                } catch (RemoteException e) {
                    OAuthClient.this.removePendingCallback(callback);
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @UiThread
    public void destroy() {
        this.mAllocationSite = null;
        this.mQueuedRunnables.clear();
        this.mOutstandingRequests.clear();
        disconnect();
    }

    protected void finalize() throws Throwable {
        Throwable th = this.mAllocationSite;
        if (th != null) {
            Log.w(TAG, "An OAuthClient was acquired at the attached stack trace but never released.\nCall OAuthClient.destroy()", th);
        }
        super.finalize();
    }

    private void whenConnected(Runnable runnable) {
        if (this.mConnectionState == 2) {
            runnable.run();
        } else {
            this.mQueuedRunnables.add(runnable);
        }
    }

    private void removePendingCallback(RequestCallback requestCallback) {
        this.mOutstandingRequests.remove(requestCallback);
        if (this.mOutstandingRequests.isEmpty() && this.mService != null) {
            disconnect();
        }
    }

    private void connect() {
        int i = this.mConnectionState;
        if (i == 0) {
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "Binding to OAuth service");
            }
            if (this.mServiceBinder.bindService(new Intent(ACTION_OAUTH).setPackage(ANDROID_WEAR_PACKAGE_NAME), this.mConnection, 1)) {
                if (Log.isLoggable(TAG, 3)) {
                    Log.d(TAG, "Bound to OAuth service. Connecting...");
                }
                this.mConnectionState = 1;
                return;
            }
            throw new RuntimeException("Failed to bind to OAuth service");
        }
        StringBuilder stringBuilder = new StringBuilder(20);
        stringBuilder.append("State is ");
        stringBuilder.append(i);
        throw new IllegalStateException(stringBuilder.toString());
    }

    private void disconnect() {
        if (this.mConnectionState != 0) {
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "Disconnecting...");
            }
            this.mServiceBinder.unbindService(this.mConnection);
            this.mService = null;
            this.mConnectionState = 0;
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "Disconnected.");
            }
        }
    }

    private static <T> T checkNotNull(T t) {
        if (t != null) {
            return t;
        }
        throw new NullPointerException();
    }
}
