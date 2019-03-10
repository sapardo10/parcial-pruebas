package com.robotium.solo;

import android.app.Instrumentation;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import java.lang.reflect.Method;

public class SystemUtils {
    private Instrumentation instrumentation;

    public SystemUtils(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    public void setMobileData(Boolean turnedOn) {
        ConnectivityManager dataManager = (ConnectivityManager) this.instrumentation.getTargetContext().getSystemService("connectivity");
        try {
            Method dataClass = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", new Class[]{Boolean.TYPE});
            dataClass.setAccessible(true);
            dataClass.invoke(dataManager, new Object[]{turnedOn});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWiFiData(Boolean turnedOn) {
        try {
            ((WifiManager) this.instrumentation.getTargetContext().getSystemService("wifi")).setWifiEnabled(turnedOn.booleanValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
