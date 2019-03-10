package android.support.wearable.notifications;

import android.annotation.TargetApi;

@TargetApi(24)
@Deprecated
public class BridgeModeConstants {
    public static final String NOTIFICATION_BRIDGE_MODE_BRIDGING = "BRIDGING";
    public static final String NOTIFICATION_BRIDGE_MODE_METADATA_NAME = "com.google.android.wearable.notificationBridgeMode";
    public static final String NOTIFICATION_BRIDGE_MODE_NO_BRIDGING = "NO_BRIDGING";

    private BridgeModeConstants() {
    }
}
