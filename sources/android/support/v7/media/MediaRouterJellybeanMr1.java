package android.support.v7.media;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.media.MediaRouter;
import android.os.Build.VERSION;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Display;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiresApi(17)
final class MediaRouterJellybeanMr1 {
    private static final String TAG = "MediaRouterJellybeanMr1";

    public static final class ActiveScanWorkaround implements Runnable {
        private static final int WIFI_DISPLAY_SCAN_INTERVAL = 15000;
        private boolean mActivelyScanningWifiDisplays;
        private final DisplayManager mDisplayManager;
        private final Handler mHandler;
        private Method mScanWifiDisplaysMethod;

        public ActiveScanWorkaround(Context context, Handler handler) {
            if (VERSION.SDK_INT == 17) {
                this.mDisplayManager = (DisplayManager) context.getSystemService("display");
                this.mHandler = handler;
                try {
                    this.mScanWifiDisplaysMethod = DisplayManager.class.getMethod("scanWifiDisplays", new Class[0]);
                    return;
                } catch (NoSuchMethodException e) {
                    return;
                }
            }
            throw new UnsupportedOperationException();
        }

        public void setActiveScanRouteTypes(int routeTypes) {
            if ((routeTypes & 2) != 0) {
                if (!this.mActivelyScanningWifiDisplays) {
                    if (this.mScanWifiDisplaysMethod != null) {
                        this.mActivelyScanningWifiDisplays = true;
                        this.mHandler.post(this);
                        return;
                    }
                    Log.w(MediaRouterJellybeanMr1.TAG, "Cannot scan for wifi displays because the DisplayManager.scanWifiDisplays() method is not available on this device.");
                }
            } else if (this.mActivelyScanningWifiDisplays) {
                this.mActivelyScanningWifiDisplays = false;
                this.mHandler.removeCallbacks(this);
            }
        }

        public void run() {
            if (this.mActivelyScanningWifiDisplays) {
                try {
                    this.mScanWifiDisplaysMethod.invoke(this.mDisplayManager, new Object[0]);
                } catch (IllegalAccessException ex) {
                    Log.w(MediaRouterJellybeanMr1.TAG, "Cannot scan for wifi displays.", ex);
                } catch (InvocationTargetException ex2) {
                    Log.w(MediaRouterJellybeanMr1.TAG, "Cannot scan for wifi displays.", ex2);
                }
                this.mHandler.postDelayed(this, 15000);
            }
        }
    }

    public static final class IsConnectingWorkaround {
        private Method mGetStatusCodeMethod;
        private int mStatusConnecting;

        public IsConnectingWorkaround() {
            if (VERSION.SDK_INT == 17) {
                try {
                    this.mStatusConnecting = android.media.MediaRouter.RouteInfo.class.getField("STATUS_CONNECTING").getInt(null);
                    this.mGetStatusCodeMethod = android.media.MediaRouter.RouteInfo.class.getMethod("getStatusCode", new Class[0]);
                } catch (NoSuchFieldException e) {
                } catch (NoSuchMethodException e2) {
                } catch (IllegalAccessException e3) {
                    return;
                }
                return;
            }
            throw new UnsupportedOperationException();
        }

        public boolean isConnecting(Object routeObj) {
            android.media.MediaRouter.RouteInfo route = (android.media.MediaRouter.RouteInfo) routeObj;
            Method method = this.mGetStatusCodeMethod;
            boolean z = false;
            if (method == null) {
                return false;
            }
            try {
                if (((Integer) method.invoke(route, new Object[0])).intValue() == this.mStatusConnecting) {
                    z = true;
                }
                return z;
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e2) {
            }
        }
    }

    public static final class RouteInfo {
        public static boolean isEnabled(Object routeObj) {
            return ((android.media.MediaRouter.RouteInfo) routeObj).isEnabled();
        }

        public static Display getPresentationDisplay(Object routeObj) {
            try {
                return ((android.media.MediaRouter.RouteInfo) routeObj).getPresentationDisplay();
            } catch (NoSuchMethodError ex) {
                Log.w(MediaRouterJellybeanMr1.TAG, "Cannot get presentation display for the route.", ex);
                return null;
            }
        }
    }

    public interface Callback extends android.support.v7.media.MediaRouterJellybean.Callback {
        void onRoutePresentationDisplayChanged(Object obj);
    }

    static class CallbackProxy<T extends Callback> extends CallbackProxy<T> {
        public CallbackProxy(T callback) {
            super(callback);
        }

        public void onRoutePresentationDisplayChanged(MediaRouter router, android.media.MediaRouter.RouteInfo route) {
            ((Callback) this.mCallback).onRoutePresentationDisplayChanged(route);
        }
    }

    MediaRouterJellybeanMr1() {
    }

    public static Object createCallback(Callback callback) {
        return new CallbackProxy(callback);
    }
}