package android.support.v4.app;

import android.app.Service;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;

public final class ServiceCompat {
    public static final int START_STICKY = 1;
    public static final int STOP_FOREGROUND_DETACH = 2;
    public static final int STOP_FOREGROUND_REMOVE = 1;

    private ServiceCompat() {
    }

    public static void stopForeground(@NonNull Service service, int flags) {
        if (VERSION.SDK_INT >= 24) {
            service.stopForeground(flags);
        } else {
            service.stopForeground((flags & 1) != 0);
        }
    }
}