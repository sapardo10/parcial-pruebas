package android.support.wearable.watchface;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.service.wallpaper.WallpaperService;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.watchface.IWatchFaceService.Stub;
import android.support.wearable.watchface.WatchFaceStyle.Builder;
import android.support.wearable.watchface.accessibility.ContentDescriptionLabel;
import android.support.wearable.watchface.decomposition.WatchFaceDecomposition;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import java.util.Arrays;
import java.util.Objects;

@TargetApi(21)
public abstract class WatchFaceService extends WallpaperService {
    public static final String ACTION_REQUEST_STATE = "com.google.android.wearable.watchfaces.action.REQUEST_STATE";
    public static final String COMMAND_AMBIENT_UPDATE = "com.google.android.wearable.action.AMBIENT_UPDATE";
    public static final String COMMAND_BACKGROUND_ACTION = "com.google.android.wearable.action.BACKGROUND_ACTION";
    public static final String COMMAND_COMPLICATION_DATA = "com.google.android.wearable.action.COMPLICATION_DATA";
    public static final String COMMAND_KEY_EVENT = "com.google.android.wearable.action.KEY_EVENT";
    public static final String COMMAND_REQUEST_STYLE = "com.google.android.wearable.action.REQUEST_STYLE";
    public static final String COMMAND_SET_BINDER = "com.google.android.wearable.action.SET_BINDER";
    public static final String COMMAND_SET_PROPERTIES = "com.google.android.wearable.action.SET_PROPERTIES";
    public static final String COMMAND_TAP = "android.wallpaper.tap";
    public static final String COMMAND_TOUCH = "android.wallpaper.touch";
    public static final String COMMAND_TOUCH_CANCEL = "android.wallpaper.touch_cancel";
    public static final String EXTRA_AMBIENT_MODE = "ambient_mode";
    public static final String EXTRA_BINDER = "binder";
    public static final String EXTRA_CARD_LOCATION = "card_location";
    public static final String EXTRA_COMPLICATION_DATA = "complication_data";
    public static final String EXTRA_COMPLICATION_ID = "complication_id";
    public static final String EXTRA_INDICATOR_STATUS = "indicator_status";
    public static final String EXTRA_INTERRUPTION_FILTER = "interruption_filter";
    public static final String EXTRA_NOTIFICATION_COUNT = "notification_count";
    public static final String EXTRA_TAP_TIME = "tap_time";
    public static final String EXTRA_UNREAD_COUNT = "unread_count";
    public static final String EXTRA_WATCH_FACE_VISIBLE = "watch_face_visible";
    public static final int INTERRUPTION_FILTER_ALARMS = 4;
    public static final int INTERRUPTION_FILTER_ALL = 1;
    public static final int INTERRUPTION_FILTER_NONE = 3;
    public static final int INTERRUPTION_FILTER_PRIORITY = 2;
    public static final int INTERRUPTION_FILTER_UNKNOWN = 0;
    public static final String PROPERTY_BURN_IN_PROTECTION = "burn_in_protection";
    public static final String PROPERTY_IN_RETAIL_MODE = "in_retail_mode";
    public static final String PROPERTY_LOW_BIT_AMBIENT = "low_bit_ambient";
    public static final String STATUS_AIRPLANE_MODE = "airplane_mode";
    public static final String STATUS_CHARGING = "charging";
    public static final String STATUS_CONNECTED = "connected";
    public static final String STATUS_GPS_ACTIVE = "gps_active";
    public static final String STATUS_INTERRUPTION_FILTER = "interruption_filter";
    public static final String STATUS_KEYGUARD_LOCKED = "keyguard_locked";
    private static final String[] STATUS_KEYS = new String[]{STATUS_CHARGING, STATUS_AIRPLANE_MODE, STATUS_CONNECTED, STATUS_THEATER_MODE, STATUS_GPS_ACTIVE, STATUS_KEYGUARD_LOCKED, "interruption_filter"};
    public static final String STATUS_THEATER_MODE = "theater_mode";
    private static final long SURFACE_DRAW_TIMEOUT_MS = 100;
    private static final String TAG = "WatchFaceService";
    public static final int TAP_TYPE_TAP = 2;
    public static final int TAP_TYPE_TOUCH = 0;
    public static final int TAP_TYPE_TOUCH_CANCEL = 1;

    public abstract class Engine extends android.service.wallpaper.WallpaperService.Engine {
        private ContentDescriptionLabel[] mA11yLabelsPending;
        private int[] mActiveComplicationsPending;
        private final IntentFilter mAmbientTimeTickFilter = new IntentFilter();
        private WakeLock mAmbientUpdateWakelock;
        private boolean mComplicationsActivated = false;
        private WatchFaceDecomposition mDecomposition;
        private boolean mDecompositionPending;
        private final SparseArray<ProviderConfig> mDefaultProviderConfigsPending = new SparseArray();
        private boolean mInAmbientMode;
        private final IntentFilter mInteractiveTimeTickFilter;
        private int mInterruptionFilter;
        private ContentDescriptionLabel[] mLastA11yLabelsPending;
        private int[] mLastActiveComplicationsPending;
        private Bundle mLastStatusBundle;
        private WatchFaceStyle mLastWatchFaceStyle;
        private int mNotificationCount;
        private final Rect mPeekCardPosition = new Rect(0, 0, 0, 0);
        private final BroadcastReceiver mTimeTickReceiver = new C04751();
        private boolean mTimeTickRegistered = false;
        private int mUnreadCount;
        private IWatchFaceService mWatchFaceService;
        private WatchFaceStyle mWatchFaceStyle;

        /* renamed from: android.support.wearable.watchface.WatchFaceService$Engine$1 */
        class C04751 extends BroadcastReceiver {
            C04751() {
            }

            public void onReceive(Context context, Intent intent) {
                if (Log.isLoggable(WatchFaceService.TAG, 3)) {
                    String str = WatchFaceService.TAG;
                    String valueOf = String.valueOf(intent);
                    StringBuilder stringBuilder = new StringBuilder(String.valueOf(valueOf).length() + 46);
                    stringBuilder.append("Received intent that triggers onTimeTick for: ");
                    stringBuilder.append(valueOf);
                    Log.d(str, stringBuilder.toString());
                }
                Engine.this.onTimeTick();
            }
        }

        @RequiresPermission("android.permission.WAKE_LOCK")
        public Engine() {
            super(WatchFaceService.this);
            this.mAmbientTimeTickFilter.addAction("android.intent.action.DATE_CHANGED");
            this.mAmbientTimeTickFilter.addAction("android.intent.action.TIME_SET");
            this.mAmbientTimeTickFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
            this.mInteractiveTimeTickFilter = new IntentFilter(this.mAmbientTimeTickFilter);
            this.mInteractiveTimeTickFilter.addAction("android.intent.action.TIME_TICK");
        }

        @CallSuper
        public Bundle onCommand(String action, int x, int y, int z, Bundle extras, boolean resultRequested) {
            if (Log.isLoggable(WatchFaceService.TAG, 3)) {
                String str = WatchFaceService.TAG;
                String str2 = "received command: ";
                String valueOf = String.valueOf(action);
                Log.d(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
            }
            if (WatchFaceService.COMMAND_BACKGROUND_ACTION.equals(action)) {
                maybeUpdateAmbientMode(extras);
                maybeUpdateInterruptionFilter(extras);
                maybeUpdatePeekCardPosition(extras);
                maybeUpdateUnreadCount(extras);
                maybeUpdateNotificationCount(extras);
                maybeUpdateStatus(extras);
            } else if (WatchFaceService.COMMAND_AMBIENT_UPDATE.equals(action)) {
                if (this.mInAmbientMode) {
                    if (Log.isLoggable(WatchFaceService.TAG, 3)) {
                        Log.d(WatchFaceService.TAG, "ambient mode update");
                    }
                    this.mAmbientUpdateWakelock.acquire();
                    onTimeTick();
                    this.mAmbientUpdateWakelock.acquire(WatchFaceService.SURFACE_DRAW_TIMEOUT_MS);
                }
            } else if (WatchFaceService.COMMAND_SET_PROPERTIES.equals(action)) {
                onPropertiesChanged(extras);
            } else if (WatchFaceService.COMMAND_SET_BINDER.equals(action)) {
                onSetBinder(extras);
            } else if (WatchFaceService.COMMAND_REQUEST_STYLE.equals(action)) {
                WatchFaceStyle watchFaceStyle = this.mLastWatchFaceStyle;
                if (watchFaceStyle != null) {
                    setWatchFaceStyle(watchFaceStyle);
                } else if (Log.isLoggable(WatchFaceService.TAG, 3)) {
                    Log.d(WatchFaceService.TAG, "Last watch face style is null.");
                }
                int[] iArr = this.mLastActiveComplicationsPending;
                if (iArr != null) {
                    setActiveComplications(iArr);
                }
                ContentDescriptionLabel[] contentDescriptionLabelArr = this.mLastA11yLabelsPending;
                if (contentDescriptionLabelArr != null) {
                    setContentDescriptionLabels(contentDescriptionLabelArr);
                }
            } else {
                if (!WatchFaceService.COMMAND_TOUCH.equals(action)) {
                    if (!WatchFaceService.COMMAND_TOUCH_CANCEL.equals(action)) {
                        if (!WatchFaceService.COMMAND_TAP.equals(action)) {
                            if (WatchFaceService.COMMAND_COMPLICATION_DATA.equals(action)) {
                                extras.setClassLoader(ComplicationData.class.getClassLoader());
                                onComplicationDataUpdate(extras.getInt(WatchFaceService.EXTRA_COMPLICATION_ID), (ComplicationData) extras.getParcelable(WatchFaceService.EXTRA_COMPLICATION_DATA));
                            }
                        }
                    }
                }
                long tapTime = extras.getLong(WatchFaceService.EXTRA_TAP_TIME);
                int tapType = 0;
                if (WatchFaceService.COMMAND_TOUCH_CANCEL.equals(action)) {
                    tapType = 1;
                } else if (WatchFaceService.COMMAND_TAP.equals(action)) {
                    tapType = 2;
                }
                onTapCommand(tapType, x, y, tapTime);
            }
            return null;
        }

        private void onSetBinder(Bundle extras) {
            IBinder binder = extras.getBinder(WatchFaceService.EXTRA_BINDER);
            if (binder != null) {
                String str;
                String str2;
                this.mWatchFaceService = Stub.asInterface(binder);
                WatchFaceStyle watchFaceStyle = this.mWatchFaceStyle;
                if (watchFaceStyle != null) {
                    try {
                        this.mWatchFaceService.setStyle(watchFaceStyle);
                        this.mWatchFaceStyle = null;
                    } catch (RemoteException e) {
                        Log.w(WatchFaceService.TAG, "Failed to set WatchFaceStyle", e);
                    }
                }
                int[] iArr = this.mActiveComplicationsPending;
                if (iArr != null) {
                    doSetActiveComplications(iArr);
                    if (Log.isLoggable(WatchFaceService.TAG, 3)) {
                        str = WatchFaceService.TAG;
                        str2 = "onSetBinder set active complications to ";
                        String valueOf = String.valueOf(Arrays.toString(this.mActiveComplicationsPending));
                        Log.d(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
                    }
                    this.mActiveComplicationsPending = null;
                }
                ContentDescriptionLabel[] contentDescriptionLabelArr = this.mA11yLabelsPending;
                if (contentDescriptionLabelArr != null) {
                    doSetContentDescriptionLabels(contentDescriptionLabelArr);
                    if (Log.isLoggable(WatchFaceService.TAG, 3)) {
                        str = WatchFaceService.TAG;
                        String str3 = "onSetBinder set a11y labels to ";
                        str2 = String.valueOf(Arrays.toString(this.mA11yLabelsPending));
                        Log.d(str, str2.length() != 0 ? str3.concat(str2) : new String(str3));
                    }
                    this.mA11yLabelsPending = null;
                }
                if (this.mDecompositionPending) {
                    doUpdateDecomposition();
                    this.mDecompositionPending = false;
                }
                doSetPendingDefaultComplicationProviders();
                return;
            }
            Log.w(WatchFaceService.TAG, "Binder is null.");
        }

        public void setWatchFaceStyle(WatchFaceStyle watchFaceStyle) {
            if (Log.isLoggable(WatchFaceService.TAG, 3)) {
                String str = WatchFaceService.TAG;
                String valueOf = String.valueOf(watchFaceStyle);
                StringBuilder stringBuilder = new StringBuilder(String.valueOf(valueOf).length() + 18);
                stringBuilder.append("setWatchFaceStyle ");
                stringBuilder.append(valueOf);
                Log.d(str, stringBuilder.toString());
            }
            this.mWatchFaceStyle = watchFaceStyle;
            this.mLastWatchFaceStyle = watchFaceStyle;
            IWatchFaceService iWatchFaceService = this.mWatchFaceService;
            if (iWatchFaceService != null) {
                try {
                    iWatchFaceService.setStyle(watchFaceStyle);
                    this.mWatchFaceStyle = null;
                } catch (RemoteException e) {
                    Log.e(WatchFaceService.TAG, "Failed to set WatchFaceStyle: ", e);
                }
            }
        }

        public void setActiveComplications(int... watchFaceComplicationIds) {
            if (Log.isLoggable(WatchFaceService.TAG, 3)) {
                String str = WatchFaceService.TAG;
                String str2 = "setActiveComplications ";
                String valueOf = String.valueOf(Arrays.toString(watchFaceComplicationIds));
                Log.d(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
            }
            this.mActiveComplicationsPending = watchFaceComplicationIds;
            this.mLastActiveComplicationsPending = watchFaceComplicationIds;
            if (this.mWatchFaceService != null) {
                doSetActiveComplications(watchFaceComplicationIds);
                this.mActiveComplicationsPending = null;
            } else if (Log.isLoggable(WatchFaceService.TAG, 3)) {
                Log.d(WatchFaceService.TAG, "Could not set active complications as mWatchFaceService is null.");
            }
        }

        public void setContentDescriptionLabels(ContentDescriptionLabel[] labels) {
            if (Log.isLoggable(WatchFaceService.TAG, 3)) {
                String str = WatchFaceService.TAG;
                String str2 = "setContentDescriptionLabels ";
                String valueOf = String.valueOf(Arrays.toString(labels));
                Log.d(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
            }
            this.mA11yLabelsPending = labels;
            this.mLastA11yLabelsPending = labels;
            if (this.mWatchFaceService != null) {
                doSetContentDescriptionLabels(labels);
                this.mA11yLabelsPending = null;
            } else if (Log.isLoggable(WatchFaceService.TAG, 3)) {
                Log.d(WatchFaceService.TAG, "Could not set accessibility labels as mWatchFaceService is null.");
            }
        }

        public void updateDecomposition(@Nullable WatchFaceDecomposition decomposition) {
            this.mDecomposition = decomposition;
            if (this.mWatchFaceService != null) {
                doUpdateDecomposition();
            } else {
                this.mDecompositionPending = true;
            }
        }

        public void setDefaultSystemComplicationProvider(int watchFaceComplicationId, int systemProvider, int type) {
            if (Log.isLoggable(WatchFaceService.TAG, 3)) {
                String str = WatchFaceService.TAG;
                StringBuilder stringBuilder = new StringBuilder(72);
                stringBuilder.append("setDefaultSystemComplicationProvider ");
                stringBuilder.append(watchFaceComplicationId);
                stringBuilder.append(",");
                stringBuilder.append(systemProvider);
                stringBuilder.append(",");
                stringBuilder.append(type);
                Log.d(str, stringBuilder.toString());
            }
            this.mDefaultProviderConfigsPending.put(watchFaceComplicationId, new ProviderConfig(systemProvider, type));
            if (this.mWatchFaceService != null) {
                doSetPendingDefaultComplicationProviders();
            } else if (Log.isLoggable(WatchFaceService.TAG, 3)) {
                Log.d(WatchFaceService.TAG, "Could not set default provider as mWatchFaceService is null.");
            }
        }

        public void setDefaultComplicationProvider(int watchFaceComplicationId, ComponentName provider, int type) {
            if (Log.isLoggable(WatchFaceService.TAG, 3)) {
                String str = WatchFaceService.TAG;
                String valueOf = String.valueOf(provider);
                StringBuilder stringBuilder = new StringBuilder(String.valueOf(valueOf).length() + 55);
                stringBuilder.append("setDefaultComplicationProvider ");
                stringBuilder.append(watchFaceComplicationId);
                stringBuilder.append(",");
                stringBuilder.append(valueOf);
                stringBuilder.append(",");
                stringBuilder.append(type);
                Log.d(str, stringBuilder.toString());
            }
            this.mDefaultProviderConfigsPending.put(watchFaceComplicationId, new ProviderConfig(provider, type));
            if (this.mWatchFaceService != null) {
                doSetPendingDefaultComplicationProviders();
            } else if (Log.isLoggable(WatchFaceService.TAG, 3)) {
                Log.d(WatchFaceService.TAG, "Could not set default provider as mWatchFaceService is null.");
            }
        }

        public void onAmbientModeChanged(boolean inAmbientMode) {
        }

        public void onInterruptionFilterChanged(int interruptionFilter) {
        }

        @Deprecated
        public void onPeekCardPositionUpdate(Rect rect) {
        }

        public void onUnreadCountChanged(int count) {
        }

        public void onNotificationCountChanged(int count) {
        }

        public void onPropertiesChanged(Bundle properties) {
        }

        public void onStatusChanged(Bundle status) {
        }

        public void onTimeTick() {
        }

        public void onTapCommand(int tapType, int x, int y, long eventTime) {
        }

        public void onComplicationDataUpdate(int watchFaceComplicationId, ComplicationData data) {
        }

        @CallSuper
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            this.mWatchFaceStyle = new Builder(WatchFaceService.this).build();
            this.mAmbientUpdateWakelock = ((PowerManager) WatchFaceService.this.getSystemService("power")).newWakeLock(1, "WatchFaceService[AmbientUpdate]");
            this.mAmbientUpdateWakelock.setReferenceCounted(false);
        }

        @CallSuper
        public void onDestroy() {
            if (this.mTimeTickRegistered) {
                this.mTimeTickRegistered = false;
                WatchFaceService.this.unregisterReceiver(this.mTimeTickReceiver);
            }
            super.onDestroy();
        }

        @CallSuper
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (Log.isLoggable(WatchFaceService.TAG, 3)) {
                String str = WatchFaceService.TAG;
                StringBuilder stringBuilder = new StringBuilder(26);
                stringBuilder.append("onVisibilityChanged: ");
                stringBuilder.append(visible);
                Log.d(str, stringBuilder.toString());
            }
            Intent intent = new Intent(WatchFaceService.ACTION_REQUEST_STATE);
            intent.putExtra(WatchFaceService.EXTRA_WATCH_FACE_VISIBLE, visible);
            WatchFaceService.this.sendBroadcast(intent);
            updateTimeTickReceiver();
        }

        public final boolean isInAmbientMode() {
            return this.mInAmbientMode;
        }

        public final int getInterruptionFilter() {
            return this.mInterruptionFilter;
        }

        public final int getUnreadCount() {
            return this.mUnreadCount;
        }

        public final int getNotificationCount() {
            return this.mNotificationCount;
        }

        @Deprecated
        public final Rect getPeekCardPosition() {
            return this.mPeekCardPosition;
        }

        @Deprecated
        public void setTouchEventsEnabled(boolean enabled) {
            super.setTouchEventsEnabled(enabled);
        }

        @Deprecated
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
        }

        private void maybeUpdateInterruptionFilter(Bundle bundle) {
            if (bundle.containsKey("interruption_filter")) {
                int interruptionFilter = bundle.getInt("interruption_filter", 1);
                if (interruptionFilter != this.mInterruptionFilter) {
                    this.mInterruptionFilter = interruptionFilter;
                    onInterruptionFilterChanged(interruptionFilter);
                }
            }
        }

        private void maybeUpdatePeekCardPosition(Bundle bundle) {
            if (bundle.containsKey(WatchFaceService.EXTRA_CARD_LOCATION)) {
                Rect rect = Rect.unflattenFromString(bundle.getString(WatchFaceService.EXTRA_CARD_LOCATION));
                if (!rect.equals(this.mPeekCardPosition)) {
                    this.mPeekCardPosition.set(rect);
                    onPeekCardPositionUpdate(rect);
                }
            }
        }

        private void maybeUpdateAmbientMode(Bundle bundle) {
            if (bundle.containsKey(WatchFaceService.EXTRA_AMBIENT_MODE)) {
                boolean inAmbientMode = bundle.getBoolean(WatchFaceService.EXTRA_AMBIENT_MODE, false);
                if (this.mInAmbientMode != inAmbientMode) {
                    this.mInAmbientMode = inAmbientMode;
                    dispatchAmbientModeChanged();
                }
            }
        }

        private void dispatchAmbientModeChanged() {
            if (Log.isLoggable(WatchFaceService.TAG, 3)) {
                String str = WatchFaceService.TAG;
                boolean z = this.mInAmbientMode;
                StringBuilder stringBuilder = new StringBuilder(33);
                stringBuilder.append("dispatchAmbientModeChanged: ");
                stringBuilder.append(z);
                Log.d(str, stringBuilder.toString());
            }
            onAmbientModeChanged(this.mInAmbientMode);
            updateTimeTickReceiver();
        }

        private void maybeUpdateUnreadCount(Bundle bundle) {
            if (bundle.containsKey(WatchFaceService.EXTRA_UNREAD_COUNT)) {
                int unreadCount = bundle.getInt(WatchFaceService.EXTRA_UNREAD_COUNT, 0);
                if (unreadCount != this.mUnreadCount) {
                    this.mUnreadCount = unreadCount;
                    onUnreadCountChanged(this.mUnreadCount);
                }
            }
        }

        private void maybeUpdateNotificationCount(Bundle bundle) {
            if (bundle.containsKey(WatchFaceService.EXTRA_NOTIFICATION_COUNT)) {
                int notificationCount = bundle.getInt(WatchFaceService.EXTRA_NOTIFICATION_COUNT, 0);
                if (notificationCount != this.mNotificationCount) {
                    this.mNotificationCount = notificationCount;
                    onNotificationCountChanged(this.mNotificationCount);
                }
            }
        }

        private void maybeUpdateStatus(Bundle bundle) {
            Bundle statusBundle = bundle.getBundle(WatchFaceService.EXTRA_INDICATOR_STATUS);
            if (statusBundle != null) {
                Bundle bundle2 = this.mLastStatusBundle;
                if (bundle2 != null) {
                    if (sameStatus(statusBundle, bundle2)) {
                        return;
                    }
                }
                this.mLastStatusBundle = new Bundle(statusBundle);
                onStatusChanged(statusBundle);
            }
        }

        private void updateTimeTickReceiver() {
            if (Log.isLoggable(WatchFaceService.TAG, 3)) {
                String str = WatchFaceService.TAG;
                boolean z = this.mTimeTickRegistered;
                boolean isVisible = isVisible();
                boolean z2 = this.mInAmbientMode;
                StringBuilder stringBuilder = new StringBuilder(47);
                stringBuilder.append("updateTimeTickReceiver: ");
                stringBuilder.append(z);
                stringBuilder.append(" -> (");
                stringBuilder.append(isVisible);
                stringBuilder.append(", ");
                stringBuilder.append(z2);
                stringBuilder.append(")");
                Log.d(str, stringBuilder.toString());
            }
            if (this.mTimeTickRegistered) {
                WatchFaceService.this.unregisterReceiver(this.mTimeTickReceiver);
                this.mTimeTickRegistered = false;
            }
            if (isVisible()) {
                if (this.mInAmbientMode) {
                    WatchFaceService.this.registerReceiver(this.mTimeTickReceiver, this.mAmbientTimeTickFilter);
                } else {
                    WatchFaceService.this.registerReceiver(this.mTimeTickReceiver, this.mInteractiveTimeTickFilter);
                }
                this.mTimeTickRegistered = true;
                onTimeTick();
            }
        }

        private boolean sameStatus(Bundle bundle0, Bundle bundle1) {
            for (String key : WatchFaceService.STATUS_KEYS) {
                if (!Objects.equals(bundle0.get(key), bundle1.get(key))) {
                    return false;
                }
            }
            return true;
        }

        private void doSetActiveComplications(int[] ids) {
            try {
                this.mWatchFaceService.setActiveComplications(ids, !this.mComplicationsActivated);
                this.mComplicationsActivated = true;
            } catch (RemoteException e) {
                Log.e(WatchFaceService.TAG, "Failed to set active complications: ", e);
            }
        }

        private void doSetContentDescriptionLabels(ContentDescriptionLabel[] labels) {
            try {
                this.mWatchFaceService.setContentDescriptionLabels(labels);
            } catch (RemoteException e) {
                Log.e(WatchFaceService.TAG, "Failed to set accessibility labels: ", e);
            }
        }

        private void doSetPendingDefaultComplicationProviders() {
            if (Log.isLoggable(WatchFaceService.TAG, 3)) {
                Log.d(WatchFaceService.TAG, "doSetPendingDefaultComplicationProviders");
            }
            int i = 0;
            while (i < this.mDefaultProviderConfigsPending.size()) {
                try {
                    int watchFaceComplicationId = this.mDefaultProviderConfigsPending.keyAt(i);
                    ProviderConfig config = (ProviderConfig) this.mDefaultProviderConfigsPending.valueAt(i);
                    if (config.systemProvider == -1) {
                        this.mWatchFaceService.setDefaultComplicationProvider(watchFaceComplicationId, config.provider, config.type);
                    } else {
                        this.mWatchFaceService.setDefaultSystemComplicationProvider(watchFaceComplicationId, config.systemProvider, config.type);
                    }
                    i++;
                } catch (RemoteException e) {
                    Log.e(WatchFaceService.TAG, "Failed to set default complication providers: ", e);
                    return;
                }
            }
            this.mDefaultProviderConfigsPending.clear();
        }

        private void doUpdateDecomposition() {
            if (Log.isLoggable(WatchFaceService.TAG, 3)) {
                Log.d(WatchFaceService.TAG, "doUpdateDecomposition");
            }
            try {
                this.mWatchFaceService.updateDecomposition(this.mDecomposition);
                this.mDecomposition = null;
            } catch (RemoteException e) {
                Log.e(WatchFaceService.TAG, "Failed to update decomposition: ", e);
            }
        }
    }

    private static class ProviderConfig {
        private static final int NONE = -1;
        public final ComponentName provider;
        public final int systemProvider;
        public final int type;

        public ProviderConfig(ComponentName provider, int type) {
            this.provider = provider;
            this.systemProvider = -1;
            this.type = type;
        }

        public ProviderConfig(int systemProvider, int type) {
            this.systemProvider = systemProvider;
            this.provider = null;
            this.type = type;
        }
    }

    public abstract Engine onCreateEngine();
}
