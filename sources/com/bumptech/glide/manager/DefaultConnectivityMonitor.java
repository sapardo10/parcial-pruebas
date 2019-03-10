package com.bumptech.glide.manager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;
import com.bumptech.glide.manager.ConnectivityMonitor.ConnectivityListener;
import com.bumptech.glide.util.Preconditions;

final class DefaultConnectivityMonitor implements ConnectivityMonitor {
    private static final String TAG = "ConnectivityMonitor";
    private final BroadcastReceiver connectivityReceiver = new C05331();
    private final Context context;
    boolean isConnected;
    private boolean isRegistered;
    final ConnectivityListener listener;

    /* renamed from: com.bumptech.glide.manager.DefaultConnectivityMonitor$1 */
    class C05331 extends BroadcastReceiver {
        C05331() {
        }

        public void onReceive(@NonNull Context context, Intent intent) {
            boolean wasConnected = DefaultConnectivityMonitor.this.isConnected;
            DefaultConnectivityMonitor defaultConnectivityMonitor = DefaultConnectivityMonitor.this;
            defaultConnectivityMonitor.isConnected = defaultConnectivityMonitor.isConnected(context);
            if (wasConnected != DefaultConnectivityMonitor.this.isConnected) {
                if (Log.isLoggable(DefaultConnectivityMonitor.TAG, 3)) {
                    String str = DefaultConnectivityMonitor.TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("connectivity changed, isConnected: ");
                    stringBuilder.append(DefaultConnectivityMonitor.this.isConnected);
                    Log.d(str, stringBuilder.toString());
                }
                DefaultConnectivityMonitor.this.listener.onConnectivityChanged(DefaultConnectivityMonitor.this.isConnected);
            }
        }
    }

    DefaultConnectivityMonitor(@NonNull Context context, @NonNull ConnectivityListener listener) {
        this.context = context.getApplicationContext();
        this.listener = listener;
    }

    private void register() {
        if (!this.isRegistered) {
            this.isConnected = isConnected(this.context);
            try {
                this.context.registerReceiver(this.connectivityReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
                this.isRegistered = true;
            } catch (SecurityException e) {
                if (Log.isLoggable(TAG, 5)) {
                    Log.w(TAG, "Failed to register", e);
                }
            }
        }
    }

    private void unregister() {
        if (this.isRegistered) {
            this.context.unregisterReceiver(this.connectivityReceiver);
            this.isRegistered = false;
        }
    }

    @SuppressLint({"MissingPermission"})
    boolean isConnected(@NonNull Context context) {
        boolean z = true;
        try {
            NetworkInfo networkInfo = ((ConnectivityManager) Preconditions.checkNotNull((ConnectivityManager) context.getSystemService("connectivity"))).getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {
                z = false;
            }
            return z;
        } catch (RuntimeException e) {
            if (Log.isLoggable(TAG, 5)) {
                Log.w(TAG, "Failed to determine connectivity status when connectivity changed", e);
            }
            return true;
        }
    }

    public void onStart() {
        register();
    }

    public void onStop() {
        unregister();
    }

    public void onDestroy() {
    }
}
