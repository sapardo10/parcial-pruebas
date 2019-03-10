package com.google.android.exoplayer2.scheduler;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import com.google.android.exoplayer2.util.Util;

public final class Requirements {
    private static final int DEVICE_CHARGING = 16;
    private static final int DEVICE_IDLE = 8;
    public static final int NETWORK_TYPE_ANY = 1;
    private static final int NETWORK_TYPE_MASK = 7;
    public static final int NETWORK_TYPE_METERED = 4;
    public static final int NETWORK_TYPE_NONE = 0;
    public static final int NETWORK_TYPE_NOT_ROAMING = 3;
    private static final String[] NETWORK_TYPE_STRINGS = null;
    public static final int NETWORK_TYPE_UNMETERED = 2;
    private static final String TAG = "Requirements";
    private final int requirements;

    public Requirements(int networkType, boolean charging, boolean idle) {
        int i = 0;
        int i2 = (charging ? 16 : 0) | networkType;
        if (idle) {
            i = 8;
        }
        this(i | i2);
    }

    public Requirements(int requirementsData) {
        this.requirements = requirementsData;
    }

    public int getRequiredNetworkType() {
        return this.requirements & 7;
    }

    public boolean isChargingRequired() {
        return (this.requirements & 16) != 0;
    }

    public boolean isIdleRequired() {
        return (this.requirements & 8) != 0;
    }

    public boolean checkRequirements(Context context) {
        if (checkNetworkRequirements(context)) {
            if (checkChargingRequirement(context)) {
                if (checkIdleRequirement(context)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getRequirementsData() {
        return this.requirements;
    }

    private boolean checkNetworkRequirements(Context context) {
        int networkRequirement = getRequiredNetworkType();
        if (networkRequirement == 0) {
            return true;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.isConnected()) {
                if (!checkInternetConnectivity(connectivityManager)) {
                    return false;
                }
                if (networkRequirement == 1) {
                    return true;
                }
                boolean roaming;
                StringBuilder stringBuilder;
                if (networkRequirement == 3) {
                    roaming = networkInfo.isRoaming();
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Roaming: ");
                    stringBuilder.append(roaming);
                    logd(stringBuilder.toString());
                    return roaming ^ 1;
                }
                roaming = isActiveNetworkMetered(connectivityManager, networkInfo);
                stringBuilder = new StringBuilder();
                stringBuilder.append("Metered network: ");
                stringBuilder.append(roaming);
                logd(stringBuilder.toString());
                if (networkRequirement == 2) {
                    return roaming ^ 1;
                }
                if (networkRequirement == 4) {
                    return roaming;
                }
                throw new IllegalStateException();
            }
        }
        logd("No network info or no connection.");
        return false;
    }

    private boolean checkChargingRequirement(Context context) {
        boolean z = true;
        if (!isChargingRequired()) {
            return true;
        }
        Intent batteryStatus = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        if (batteryStatus == null) {
            return false;
        }
        int status = batteryStatus.getIntExtra(NotificationCompat.CATEGORY_STATUS, -1);
        if (status != 2) {
            if (status != 5) {
                z = false;
            }
        }
        return z;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkIdleRequirement(android.content.Context r6) {
        /*
        r5 = this;
        r0 = r5.isIdleRequired();
        r1 = 1;
        if (r0 != 0) goto L_0x0008;
    L_0x0007:
        return r1;
    L_0x0008:
        r0 = "power";
        r0 = r6.getSystemService(r0);
        r0 = (android.os.PowerManager) r0;
        r2 = com.google.android.exoplayer2.util.Util.SDK_INT;
        r3 = 23;
        r4 = 0;
        if (r2 < r3) goto L_0x001d;
    L_0x0018:
        r1 = r0.isDeviceIdleMode();
        goto L_0x0032;
    L_0x001d:
        r2 = com.google.android.exoplayer2.util.Util.SDK_INT;
        r3 = 20;
        if (r2 < r3) goto L_0x002a;
    L_0x0023:
        r2 = r0.isInteractive();
        if (r2 != 0) goto L_0x0031;
    L_0x0029:
        goto L_0x0030;
    L_0x002a:
        r2 = r0.isScreenOn();
        if (r2 != 0) goto L_0x0031;
    L_0x0030:
        goto L_0x0032;
    L_0x0031:
        r1 = 0;
    L_0x0032:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.scheduler.Requirements.checkIdleRequirement(android.content.Context):boolean");
    }

    private static boolean checkInternetConnectivity(ConnectivityManager connectivityManager) {
        boolean z = true;
        if (Util.SDK_INT < 23) {
            return true;
        }
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null) {
            logd("No active network.");
            return false;
        }
        boolean validated;
        StringBuilder stringBuilder;
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
        if (networkCapabilities != null) {
            if (networkCapabilities.hasCapability(16)) {
                validated = false;
                stringBuilder = new StringBuilder();
                stringBuilder.append("Network capability validated: ");
                stringBuilder.append(validated);
                logd(stringBuilder.toString());
                if (!validated) {
                    z = false;
                }
                return z;
            }
        }
        validated = true;
        stringBuilder = new StringBuilder();
        stringBuilder.append("Network capability validated: ");
        stringBuilder.append(validated);
        logd(stringBuilder.toString());
        if (!validated) {
            z = false;
        }
        return z;
    }

    private static boolean isActiveNetworkMetered(ConnectivityManager connectivityManager, NetworkInfo networkInfo) {
        if (Util.SDK_INT >= 16) {
            return connectivityManager.isActiveNetworkMetered();
        }
        int type = networkInfo.getType();
        boolean z = true;
        if (type == 1 || type == 7 || type == 9) {
            z = false;
        }
        return z;
    }

    private static void logd(String message) {
    }

    public String toString() {
        return super.toString();
    }
}
