package android.support.v7.media;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.app.ActivityManagerCompat;
import android.support.v4.hardware.display.DisplayManagerCompat;
import android.support.v4.media.VolumeProviderCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.MediaSessionCompat.OnActiveChangeListener;
import android.support.v4.media.session.MediaSessionCompat.Token;
import android.support.v4.util.Pair;
import android.support.v7.media.MediaRouteProvider.ProviderMetadata;
import android.support.v7.media.MediaRouteProvider.RouteController;
import android.support.v7.media.MediaRouteSelector.Builder;
import android.support.v7.media.RemoteControlClientCompat.PlaybackInfo;
import android.support.v7.media.RemoteControlClientCompat.VolumeCallback;
import android.support.v7.media.SystemMediaRouteProvider.SyncCallback;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class MediaRouter {
    public static final int AVAILABILITY_FLAG_IGNORE_DEFAULT_ROUTE = 1;
    public static final int AVAILABILITY_FLAG_REQUIRE_MATCH = 2;
    public static final int CALLBACK_FLAG_FORCE_DISCOVERY = 8;
    public static final int CALLBACK_FLAG_PERFORM_ACTIVE_SCAN = 1;
    public static final int CALLBACK_FLAG_REQUEST_DISCOVERY = 4;
    public static final int CALLBACK_FLAG_UNFILTERED_EVENTS = 2;
    static final boolean DEBUG = Log.isLoggable(TAG, 3);
    static final String TAG = "MediaRouter";
    public static final int UNSELECT_REASON_DISCONNECTED = 1;
    public static final int UNSELECT_REASON_ROUTE_CHANGED = 3;
    public static final int UNSELECT_REASON_STOPPED = 2;
    public static final int UNSELECT_REASON_UNKNOWN = 0;
    static GlobalMediaRouter sGlobal;
    final ArrayList<CallbackRecord> mCallbackRecords;
    final Context mContext;

    public static abstract class Callback {
        public void onRouteSelected(MediaRouter router, RouteInfo route) {
        }

        public void onRouteUnselected(MediaRouter router, RouteInfo route) {
        }

        public void onRouteUnselected(MediaRouter router, RouteInfo route, int reason) {
            onRouteUnselected(router, route);
        }

        public void onRouteAdded(MediaRouter router, RouteInfo route) {
        }

        public void onRouteRemoved(MediaRouter router, RouteInfo route) {
        }

        public void onRouteChanged(MediaRouter router, RouteInfo route) {
        }

        public void onRouteVolumeChanged(MediaRouter router, RouteInfo route) {
        }

        public void onRoutePresentationDisplayChanged(MediaRouter router, RouteInfo route) {
        }

        public void onProviderAdded(MediaRouter router, ProviderInfo provider) {
        }

        public void onProviderRemoved(MediaRouter router, ProviderInfo provider) {
        }

        public void onProviderChanged(MediaRouter router, ProviderInfo provider) {
        }
    }

    private static final class CallbackRecord {
        public final Callback mCallback;
        public int mFlags;
        public final MediaRouter mRouter;
        public MediaRouteSelector mSelector = MediaRouteSelector.EMPTY;

        public CallbackRecord(MediaRouter router, Callback callback) {
            this.mRouter = router;
            this.mCallback = callback;
        }

        public boolean filterRouteEvent(RouteInfo route) {
            if ((this.mFlags & 2) == 0) {
                if (!route.matchesSelector(this.mSelector)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static abstract class ControlRequestCallback {
        public void onResult(Bundle data) {
        }

        public void onError(String error, Bundle data) {
        }
    }

    public static final class ProviderInfo {
        private MediaRouteProviderDescriptor mDescriptor;
        private final ProviderMetadata mMetadata;
        private final MediaRouteProvider mProviderInstance;
        private Resources mResources;
        private boolean mResourcesNotAvailable;
        private final List<RouteInfo> mRoutes = new ArrayList();

        ProviderInfo(MediaRouteProvider provider) {
            this.mProviderInstance = provider;
            this.mMetadata = provider.getMetadata();
        }

        public MediaRouteProvider getProviderInstance() {
            MediaRouter.checkCallingThread();
            return this.mProviderInstance;
        }

        public String getPackageName() {
            return this.mMetadata.getPackageName();
        }

        public ComponentName getComponentName() {
            return this.mMetadata.getComponentName();
        }

        public List<RouteInfo> getRoutes() {
            MediaRouter.checkCallingThread();
            return this.mRoutes;
        }

        Resources getResources() {
            if (this.mResources == null && !this.mResourcesNotAvailable) {
                String packageName = getPackageName();
                Context context = MediaRouter.sGlobal.getProviderContext(packageName);
                if (context != null) {
                    this.mResources = context.getResources();
                } else {
                    String str = MediaRouter.TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Unable to obtain resources for route provider package: ");
                    stringBuilder.append(packageName);
                    Log.w(str, stringBuilder.toString());
                    this.mResourcesNotAvailable = true;
                }
            }
            return this.mResources;
        }

        boolean updateDescriptor(MediaRouteProviderDescriptor descriptor) {
            if (this.mDescriptor == descriptor) {
                return false;
            }
            this.mDescriptor = descriptor;
            return true;
        }

        int findRouteByDescriptorId(String id) {
            int count = this.mRoutes.size();
            for (int i = 0; i < count; i++) {
                if (((RouteInfo) this.mRoutes.get(i)).mDescriptorId.equals(id)) {
                    return i;
                }
            }
            return -1;
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("MediaRouter.RouteProviderInfo{ packageName=");
            stringBuilder.append(getPackageName());
            stringBuilder.append(" }");
            return stringBuilder.toString();
        }
    }

    public static class RouteInfo {
        static final int CHANGE_GENERAL = 1;
        static final int CHANGE_PRESENTATION_DISPLAY = 4;
        static final int CHANGE_VOLUME = 2;
        public static final int CONNECTION_STATE_CONNECTED = 2;
        public static final int CONNECTION_STATE_CONNECTING = 1;
        public static final int CONNECTION_STATE_DISCONNECTED = 0;
        @RestrictTo({Scope.LIBRARY_GROUP})
        public static final int DEVICE_TYPE_BLUETOOTH = 3;
        public static final int DEVICE_TYPE_SPEAKER = 2;
        public static final int DEVICE_TYPE_TV = 1;
        @RestrictTo({Scope.LIBRARY_GROUP})
        public static final int DEVICE_TYPE_UNKNOWN = 0;
        public static final int PLAYBACK_TYPE_LOCAL = 0;
        public static final int PLAYBACK_TYPE_REMOTE = 1;
        public static final int PLAYBACK_VOLUME_FIXED = 0;
        public static final int PLAYBACK_VOLUME_VARIABLE = 1;
        @RestrictTo({Scope.LIBRARY_GROUP})
        public static final int PRESENTATION_DISPLAY_ID_NONE = -1;
        static final String SYSTEM_MEDIA_ROUTE_PROVIDER_PACKAGE_NAME = "android";
        private boolean mCanDisconnect;
        private boolean mConnecting;
        private int mConnectionState;
        private final ArrayList<IntentFilter> mControlFilters = new ArrayList();
        private String mDescription;
        MediaRouteDescriptor mDescriptor;
        private final String mDescriptorId;
        private int mDeviceType;
        private boolean mEnabled;
        private Bundle mExtras;
        private Uri mIconUri;
        private String mName;
        private int mPlaybackStream;
        private int mPlaybackType;
        private Display mPresentationDisplay;
        private int mPresentationDisplayId = -1;
        private final ProviderInfo mProvider;
        private IntentSender mSettingsIntent;
        private final String mUniqueId;
        private int mVolume;
        private int mVolumeHandling;
        private int mVolumeMax;

        public boolean supportsControlAction(@android.support.annotation.NonNull java.lang.String r5, @android.support.annotation.NonNull java.lang.String r6) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:0x003c in {9, 10, 12, 14, 16} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r4 = this;
            if (r5 == 0) goto L_0x0034;
        L_0x0002:
            if (r6 == 0) goto L_0x002c;
        L_0x0004:
            android.support.v7.media.MediaRouter.checkCallingThread();
            r0 = r4.mControlFilters;
            r0 = r0.size();
            r1 = 0;
        L_0x000e:
            if (r1 >= r0) goto L_0x002a;
        L_0x0010:
            r2 = r4.mControlFilters;
            r2 = r2.get(r1);
            r2 = (android.content.IntentFilter) r2;
            r3 = r2.hasCategory(r5);
            if (r3 == 0) goto L_0x0026;
        L_0x001e:
            r3 = r2.hasAction(r6);
            if (r3 == 0) goto L_0x0026;
        L_0x0024:
            r3 = 1;
            return r3;
            r1 = r1 + 1;
            goto L_0x000e;
        L_0x002a:
            r1 = 0;
            return r1;
        L_0x002c:
            r0 = new java.lang.IllegalArgumentException;
            r1 = "action must not be null";
            r0.<init>(r1);
            throw r0;
        L_0x0034:
            r0 = new java.lang.IllegalArgumentException;
            r1 = "category must not be null";
            r0.<init>(r1);
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.media.MediaRouter.RouteInfo.supportsControlAction(java.lang.String, java.lang.String):boolean");
        }

        public boolean supportsControlCategory(@android.support.annotation.NonNull java.lang.String r4) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:12:0x002b in {6, 7, 9, 11} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r3 = this;
            if (r4 == 0) goto L_0x0023;
        L_0x0002:
            android.support.v7.media.MediaRouter.checkCallingThread();
            r0 = r3.mControlFilters;
            r0 = r0.size();
            r1 = 0;
        L_0x000c:
            if (r1 >= r0) goto L_0x0021;
        L_0x000e:
            r2 = r3.mControlFilters;
            r2 = r2.get(r1);
            r2 = (android.content.IntentFilter) r2;
            r2 = r2.hasCategory(r4);
            if (r2 == 0) goto L_0x001e;
        L_0x001c:
            r2 = 1;
            return r2;
        L_0x001e:
            r1 = r1 + 1;
            goto L_0x000c;
        L_0x0021:
            r1 = 0;
            return r1;
        L_0x0023:
            r0 = new java.lang.IllegalArgumentException;
            r1 = "category must not be null";
            r0.<init>(r1);
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.media.MediaRouter.RouteInfo.supportsControlCategory(java.lang.String):boolean");
        }

        public boolean supportsControlRequest(@android.support.annotation.NonNull android.content.Intent r7) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:11:0x0033 in {5, 6, 8, 10} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r6 = this;
            if (r7 == 0) goto L_0x002b;
        L_0x0002:
            android.support.v7.media.MediaRouter.checkCallingThread();
            r0 = android.support.v7.media.MediaRouter.sGlobal;
            r0 = r0.getContentResolver();
            r1 = r6.mControlFilters;
            r1 = r1.size();
            r2 = 0;
        L_0x0012:
            if (r2 >= r1) goto L_0x0029;
        L_0x0014:
            r3 = r6.mControlFilters;
            r3 = r3.get(r2);
            r3 = (android.content.IntentFilter) r3;
            r4 = "MediaRouter";
            r5 = 1;
            r3 = r3.match(r0, r7, r5, r4);
            if (r3 < 0) goto L_0x0026;
        L_0x0025:
            return r5;
        L_0x0026:
            r2 = r2 + 1;
            goto L_0x0012;
        L_0x0029:
            r2 = 0;
            return r2;
        L_0x002b:
            r0 = new java.lang.IllegalArgumentException;
            r1 = "intent must not be null";
            r0.<init>(r1);
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.media.MediaRouter.RouteInfo.supportsControlRequest(android.content.Intent):boolean");
        }

        RouteInfo(ProviderInfo provider, String descriptorId, String uniqueId) {
            this.mProvider = provider;
            this.mDescriptorId = descriptorId;
            this.mUniqueId = uniqueId;
        }

        public ProviderInfo getProvider() {
            return this.mProvider;
        }

        @NonNull
        public String getId() {
            return this.mUniqueId;
        }

        public String getName() {
            return this.mName;
        }

        @Nullable
        public String getDescription() {
            return this.mDescription;
        }

        public Uri getIconUri() {
            return this.mIconUri;
        }

        public boolean isEnabled() {
            return this.mEnabled;
        }

        public boolean isConnecting() {
            return this.mConnecting;
        }

        public int getConnectionState() {
            return this.mConnectionState;
        }

        public boolean isSelected() {
            MediaRouter.checkCallingThread();
            return MediaRouter.sGlobal.getSelectedRoute() == this;
        }

        public boolean isDefault() {
            MediaRouter.checkCallingThread();
            return MediaRouter.sGlobal.getDefaultRoute() == this;
        }

        public boolean isBluetooth() {
            MediaRouter.checkCallingThread();
            return MediaRouter.sGlobal.getBluetoothRoute() == this;
        }

        public boolean isDeviceSpeaker() {
            int defaultAudioRouteNameResourceId = Resources.getSystem().getIdentifier("default_audio_route_name", "string", "android");
            if (isDefault()) {
                if (Resources.getSystem().getText(defaultAudioRouteNameResourceId).equals(this.mName)) {
                    return true;
                }
            }
            return false;
        }

        public List<IntentFilter> getControlFilters() {
            return this.mControlFilters;
        }

        public boolean matchesSelector(@NonNull MediaRouteSelector selector) {
            if (selector != null) {
                MediaRouter.checkCallingThread();
                return selector.matchesControlFilters(this.mControlFilters);
            }
            throw new IllegalArgumentException("selector must not be null");
        }

        public void sendControlRequest(@NonNull Intent intent, @Nullable ControlRequestCallback callback) {
            if (intent != null) {
                MediaRouter.checkCallingThread();
                MediaRouter.sGlobal.sendControlRequest(this, intent, callback);
                return;
            }
            throw new IllegalArgumentException("intent must not be null");
        }

        public int getPlaybackType() {
            return this.mPlaybackType;
        }

        public int getPlaybackStream() {
            return this.mPlaybackStream;
        }

        public int getDeviceType() {
            return this.mDeviceType;
        }

        @RestrictTo({Scope.LIBRARY_GROUP})
        public boolean isDefaultOrBluetooth() {
            boolean z = true;
            if (!isDefault()) {
                if (this.mDeviceType != 3) {
                    if (isSystemMediaRouteProvider(this)) {
                        if (supportsControlCategory(MediaControlIntent.CATEGORY_LIVE_AUDIO)) {
                            if (!supportsControlCategory(MediaControlIntent.CATEGORY_LIVE_VIDEO)) {
                                return z;
                            }
                        }
                    }
                    z = false;
                    return z;
                }
            }
            return true;
        }

        boolean isSelectable() {
            return this.mDescriptor != null && this.mEnabled;
        }

        private static boolean isSystemMediaRouteProvider(RouteInfo route) {
            return TextUtils.equals(route.getProviderInstance().getMetadata().getPackageName(), "android");
        }

        public int getVolumeHandling() {
            return this.mVolumeHandling;
        }

        public int getVolume() {
            return this.mVolume;
        }

        public int getVolumeMax() {
            return this.mVolumeMax;
        }

        public boolean canDisconnect() {
            return this.mCanDisconnect;
        }

        public void requestSetVolume(int volume) {
            MediaRouter.checkCallingThread();
            MediaRouter.sGlobal.requestSetVolume(this, Math.min(this.mVolumeMax, Math.max(0, volume)));
        }

        public void requestUpdateVolume(int delta) {
            MediaRouter.checkCallingThread();
            if (delta != 0) {
                MediaRouter.sGlobal.requestUpdateVolume(this, delta);
            }
        }

        @Nullable
        public Display getPresentationDisplay() {
            MediaRouter.checkCallingThread();
            if (this.mPresentationDisplayId >= 0 && this.mPresentationDisplay == null) {
                this.mPresentationDisplay = MediaRouter.sGlobal.getDisplay(this.mPresentationDisplayId);
            }
            return this.mPresentationDisplay;
        }

        @RestrictTo({Scope.LIBRARY_GROUP})
        public int getPresentationDisplayId() {
            return this.mPresentationDisplayId;
        }

        @Nullable
        public Bundle getExtras() {
            return this.mExtras;
        }

        @Nullable
        public IntentSender getSettingsIntent() {
            return this.mSettingsIntent;
        }

        public void select() {
            MediaRouter.checkCallingThread();
            MediaRouter.sGlobal.selectRoute(this);
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("MediaRouter.RouteInfo{ uniqueId=");
            stringBuilder.append(this.mUniqueId);
            stringBuilder.append(", name=");
            stringBuilder.append(this.mName);
            stringBuilder.append(", description=");
            stringBuilder.append(this.mDescription);
            stringBuilder.append(", iconUri=");
            stringBuilder.append(this.mIconUri);
            stringBuilder.append(", enabled=");
            stringBuilder.append(this.mEnabled);
            stringBuilder.append(", connecting=");
            stringBuilder.append(this.mConnecting);
            stringBuilder.append(", connectionState=");
            stringBuilder.append(this.mConnectionState);
            stringBuilder.append(", canDisconnect=");
            stringBuilder.append(this.mCanDisconnect);
            stringBuilder.append(", playbackType=");
            stringBuilder.append(this.mPlaybackType);
            stringBuilder.append(", playbackStream=");
            stringBuilder.append(this.mPlaybackStream);
            stringBuilder.append(", deviceType=");
            stringBuilder.append(this.mDeviceType);
            stringBuilder.append(", volumeHandling=");
            stringBuilder.append(this.mVolumeHandling);
            stringBuilder.append(", volume=");
            stringBuilder.append(this.mVolume);
            stringBuilder.append(", volumeMax=");
            stringBuilder.append(this.mVolumeMax);
            stringBuilder.append(", presentationDisplayId=");
            stringBuilder.append(this.mPresentationDisplayId);
            stringBuilder.append(", extras=");
            stringBuilder.append(this.mExtras);
            stringBuilder.append(", settingsIntent=");
            stringBuilder.append(this.mSettingsIntent);
            stringBuilder.append(", providerPackageName=");
            stringBuilder.append(this.mProvider.getPackageName());
            stringBuilder.append(" }");
            return stringBuilder.toString();
        }

        int maybeUpdateDescriptor(MediaRouteDescriptor descriptor) {
            if (this.mDescriptor != descriptor) {
                return updateDescriptor(descriptor);
            }
            return 0;
        }

        int updateDescriptor(MediaRouteDescriptor descriptor) {
            int changes = 0;
            this.mDescriptor = descriptor;
            if (descriptor == null) {
                return 0;
            }
            if (!MediaRouter.equal(this.mName, descriptor.getName())) {
                this.mName = descriptor.getName();
                changes = 0 | 1;
            }
            if (!MediaRouter.equal(this.mDescription, descriptor.getDescription())) {
                this.mDescription = descriptor.getDescription();
                changes |= 1;
            }
            if (!MediaRouter.equal(this.mIconUri, descriptor.getIconUri())) {
                this.mIconUri = descriptor.getIconUri();
                changes |= 1;
            }
            if (this.mEnabled != descriptor.isEnabled()) {
                this.mEnabled = descriptor.isEnabled();
                changes |= 1;
            }
            if (this.mConnecting != descriptor.isConnecting()) {
                this.mConnecting = descriptor.isConnecting();
                changes |= 1;
            }
            if (this.mConnectionState != descriptor.getConnectionState()) {
                this.mConnectionState = descriptor.getConnectionState();
                changes |= 1;
            }
            if (!this.mControlFilters.equals(descriptor.getControlFilters())) {
                this.mControlFilters.clear();
                this.mControlFilters.addAll(descriptor.getControlFilters());
                changes |= 1;
            }
            if (this.mPlaybackType != descriptor.getPlaybackType()) {
                this.mPlaybackType = descriptor.getPlaybackType();
                changes |= 1;
            }
            if (this.mPlaybackStream != descriptor.getPlaybackStream()) {
                this.mPlaybackStream = descriptor.getPlaybackStream();
                changes |= 1;
            }
            if (this.mDeviceType != descriptor.getDeviceType()) {
                this.mDeviceType = descriptor.getDeviceType();
                changes |= 1;
            }
            if (this.mVolumeHandling != descriptor.getVolumeHandling()) {
                this.mVolumeHandling = descriptor.getVolumeHandling();
                changes |= 3;
            }
            if (this.mVolume != descriptor.getVolume()) {
                this.mVolume = descriptor.getVolume();
                changes |= 3;
            }
            if (this.mVolumeMax != descriptor.getVolumeMax()) {
                this.mVolumeMax = descriptor.getVolumeMax();
                changes |= 3;
            }
            if (this.mPresentationDisplayId != descriptor.getPresentationDisplayId()) {
                this.mPresentationDisplayId = descriptor.getPresentationDisplayId();
                this.mPresentationDisplay = null;
                changes |= 5;
            }
            if (!MediaRouter.equal(this.mExtras, descriptor.getExtras())) {
                this.mExtras = descriptor.getExtras();
                changes |= 1;
            }
            if (!MediaRouter.equal(this.mSettingsIntent, descriptor.getSettingsActivity())) {
                this.mSettingsIntent = descriptor.getSettingsActivity();
                changes |= 1;
            }
            if (this.mCanDisconnect == descriptor.canDisconnectAndKeepPlaying()) {
                return changes;
            }
            this.mCanDisconnect = descriptor.canDisconnectAndKeepPlaying();
            return changes | 5;
        }

        String getDescriptorId() {
            return this.mDescriptorId;
        }

        @RestrictTo({Scope.LIBRARY_GROUP})
        public MediaRouteProvider getProviderInstance() {
            return this.mProvider.getProviderInstance();
        }
    }

    private static final class GlobalMediaRouter implements SyncCallback, android.support.v7.media.RegisteredMediaRouteProviderWatcher.Callback {
        final Context mApplicationContext;
        private RouteInfo mBluetoothRoute;
        final CallbackHandler mCallbackHandler = new CallbackHandler();
        private MediaSessionCompat mCompatSession;
        private RouteInfo mDefaultRoute;
        private MediaRouteDiscoveryRequest mDiscoveryRequest;
        private final DisplayManagerCompat mDisplayManager;
        private final boolean mLowRam;
        private MediaSessionRecord mMediaSession;
        final PlaybackInfo mPlaybackInfo = new PlaybackInfo();
        private final ProviderCallback mProviderCallback = new ProviderCallback();
        private final ArrayList<ProviderInfo> mProviders = new ArrayList();
        MediaSessionCompat mRccMediaSession;
        private RegisteredMediaRouteProviderWatcher mRegisteredProviderWatcher;
        private final ArrayList<RemoteControlClientRecord> mRemoteControlClients = new ArrayList();
        private final Map<String, RouteController> mRouteControllerMap = new HashMap();
        final ArrayList<WeakReference<MediaRouter>> mRouters = new ArrayList();
        private final ArrayList<RouteInfo> mRoutes = new ArrayList();
        RouteInfo mSelectedRoute;
        private RouteController mSelectedRouteController;
        private OnActiveChangeListener mSessionActiveListener = new C08641();
        final SystemMediaRouteProvider mSystemProvider;
        private final Map<Pair<String, String>, String> mUniqueIdMap = new HashMap();

        private final class CallbackHandler extends Handler {
            public static final int MSG_PROVIDER_ADDED = 513;
            public static final int MSG_PROVIDER_CHANGED = 515;
            public static final int MSG_PROVIDER_REMOVED = 514;
            public static final int MSG_ROUTE_ADDED = 257;
            public static final int MSG_ROUTE_CHANGED = 259;
            public static final int MSG_ROUTE_PRESENTATION_DISPLAY_CHANGED = 261;
            public static final int MSG_ROUTE_REMOVED = 258;
            public static final int MSG_ROUTE_SELECTED = 262;
            public static final int MSG_ROUTE_UNSELECTED = 263;
            public static final int MSG_ROUTE_VOLUME_CHANGED = 260;
            private static final int MSG_TYPE_MASK = 65280;
            private static final int MSG_TYPE_PROVIDER = 512;
            private static final int MSG_TYPE_ROUTE = 256;
            private final ArrayList<CallbackRecord> mTempCallbackRecords = new ArrayList();

            public void handleMessage(android.os.Message r8) {
                /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:26:0x0081 in {4, 5, 6, 14, 15, 16, 20, 22, 25} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
                /*
                r7 = this;
                r0 = r8.what;
                r1 = r8.obj;
                r2 = r8.arg1;
                r3 = 259; // 0x103 float:3.63E-43 double:1.28E-321;
                if (r0 != r3) goto L_0x0029;
            L_0x000a:
                r3 = android.support.v7.media.MediaRouter.GlobalMediaRouter.this;
                r3 = r3.getSelectedRoute();
                r3 = r3.getId();
                r4 = r1;
                r4 = (android.support.v7.media.MediaRouter.RouteInfo) r4;
                r4 = r4.getId();
                r3 = r3.equals(r4);
                if (r3 == 0) goto L_0x0028;
            L_0x0021:
                r3 = android.support.v7.media.MediaRouter.GlobalMediaRouter.this;
                r4 = 1;
                r3.updateSelectedRouteIfNeeded(r4);
                goto L_0x002a;
            L_0x0028:
                goto L_0x002a;
            L_0x002a:
                r7.syncWithSystemProvider(r0, r1);
                r3 = android.support.v7.media.MediaRouter.GlobalMediaRouter.this;	 Catch:{ all -> 0x007a }
                r3 = r3.mRouters;	 Catch:{ all -> 0x007a }
                r3 = r3.size();	 Catch:{ all -> 0x007a }
            L_0x0035:
                r3 = r3 + -1;	 Catch:{ all -> 0x007a }
                if (r3 < 0) goto L_0x005b;	 Catch:{ all -> 0x007a }
            L_0x0039:
                r4 = android.support.v7.media.MediaRouter.GlobalMediaRouter.this;	 Catch:{ all -> 0x007a }
                r4 = r4.mRouters;	 Catch:{ all -> 0x007a }
                r4 = r4.get(r3);	 Catch:{ all -> 0x007a }
                r4 = (java.lang.ref.WeakReference) r4;	 Catch:{ all -> 0x007a }
                r4 = r4.get();	 Catch:{ all -> 0x007a }
                r4 = (android.support.v7.media.MediaRouter) r4;	 Catch:{ all -> 0x007a }
                if (r4 != 0) goto L_0x0053;	 Catch:{ all -> 0x007a }
            L_0x004b:
                r5 = android.support.v7.media.MediaRouter.GlobalMediaRouter.this;	 Catch:{ all -> 0x007a }
                r5 = r5.mRouters;	 Catch:{ all -> 0x007a }
                r5.remove(r3);	 Catch:{ all -> 0x007a }
                goto L_0x005a;	 Catch:{ all -> 0x007a }
            L_0x0053:
                r5 = r7.mTempCallbackRecords;	 Catch:{ all -> 0x007a }
                r6 = r4.mCallbackRecords;	 Catch:{ all -> 0x007a }
                r5.addAll(r6);	 Catch:{ all -> 0x007a }
            L_0x005a:
                goto L_0x0035;	 Catch:{ all -> 0x007a }
                r3 = r7.mTempCallbackRecords;	 Catch:{ all -> 0x007a }
                r3 = r3.size();	 Catch:{ all -> 0x007a }
                r4 = 0;	 Catch:{ all -> 0x007a }
            L_0x0063:
                if (r4 >= r3) goto L_0x0073;	 Catch:{ all -> 0x007a }
            L_0x0065:
                r5 = r7.mTempCallbackRecords;	 Catch:{ all -> 0x007a }
                r5 = r5.get(r4);	 Catch:{ all -> 0x007a }
                r5 = (android.support.v7.media.MediaRouter.CallbackRecord) r5;	 Catch:{ all -> 0x007a }
                r7.invokeCallback(r5, r0, r1, r2);	 Catch:{ all -> 0x007a }
                r4 = r4 + 1;
                goto L_0x0063;
            L_0x0073:
                r3 = r7.mTempCallbackRecords;
                r3.clear();
                return;
            L_0x007a:
                r3 = move-exception;
                r4 = r7.mTempCallbackRecords;
                r4.clear();
                throw r3;
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: android.support.v7.media.MediaRouter.GlobalMediaRouter.CallbackHandler.handleMessage(android.os.Message):void");
            }

            CallbackHandler() {
            }

            public void post(int msg, Object obj) {
                obtainMessage(msg, obj).sendToTarget();
            }

            public void post(int msg, Object obj, int arg) {
                Message message = obtainMessage(msg, obj);
                message.arg1 = arg;
                message.sendToTarget();
            }

            private void syncWithSystemProvider(int what, Object obj) {
                if (what != MSG_ROUTE_SELECTED) {
                    switch (what) {
                        case 257:
                            GlobalMediaRouter.this.mSystemProvider.onSyncRouteAdded((RouteInfo) obj);
                            return;
                        case MSG_ROUTE_REMOVED /*258*/:
                            GlobalMediaRouter.this.mSystemProvider.onSyncRouteRemoved((RouteInfo) obj);
                            return;
                        case MSG_ROUTE_CHANGED /*259*/:
                            GlobalMediaRouter.this.mSystemProvider.onSyncRouteChanged((RouteInfo) obj);
                            return;
                        default:
                            return;
                    }
                }
                GlobalMediaRouter.this.mSystemProvider.onSyncRouteSelected((RouteInfo) obj);
            }

            private void invokeCallback(CallbackRecord record, int what, Object obj, int arg) {
                MediaRouter router = record.mRouter;
                Callback callback = record.mCallback;
                int i = 65280 & what;
                if (i == 256) {
                    RouteInfo route = (RouteInfo) obj;
                    if (record.filterRouteEvent(route)) {
                        switch (what) {
                            case 257:
                                callback.onRouteAdded(router, route);
                                break;
                            case MSG_ROUTE_REMOVED /*258*/:
                                callback.onRouteRemoved(router, route);
                                break;
                            case MSG_ROUTE_CHANGED /*259*/:
                                callback.onRouteChanged(router, route);
                                break;
                            case MSG_ROUTE_VOLUME_CHANGED /*260*/:
                                callback.onRouteVolumeChanged(router, route);
                                break;
                            case MSG_ROUTE_PRESENTATION_DISPLAY_CHANGED /*261*/:
                                callback.onRoutePresentationDisplayChanged(router, route);
                                break;
                            case MSG_ROUTE_SELECTED /*262*/:
                                callback.onRouteSelected(router, route);
                                break;
                            case MSG_ROUTE_UNSELECTED /*263*/:
                                callback.onRouteUnselected(router, route, arg);
                                break;
                            default:
                                break;
                        }
                    }
                } else if (i == 512) {
                    ProviderInfo provider = (ProviderInfo) obj;
                    switch (what) {
                        case 513:
                            callback.onProviderAdded(router, provider);
                            return;
                        case MSG_PROVIDER_REMOVED /*514*/:
                            callback.onProviderRemoved(router, provider);
                            return;
                        case MSG_PROVIDER_CHANGED /*515*/:
                            callback.onProviderChanged(router, provider);
                            return;
                        default:
                            return;
                    }
                }
            }
        }

        private final class MediaSessionRecord {
            private int mControlType;
            private int mMaxVolume;
            private final MediaSessionCompat mMsCompat;
            private VolumeProviderCompat mVpCompat;

            public MediaSessionRecord(Object mediaSession) {
                this.mMsCompat = MediaSessionCompat.fromMediaSession(GlobalMediaRouter.this.mApplicationContext, mediaSession);
            }

            public MediaSessionRecord(MediaSessionCompat mediaSessionCompat) {
                this.mMsCompat = mediaSessionCompat;
            }

            public void configureVolume(int controlType, int max, int current) {
                VolumeProviderCompat volumeProviderCompat = this.mVpCompat;
                if (volumeProviderCompat != null && controlType == this.mControlType && max == this.mMaxVolume) {
                    volumeProviderCompat.setCurrentVolume(current);
                    return;
                }
                this.mVpCompat = new VolumeProviderCompat(controlType, max, current) {
                    public void onSetVolumeTo(final int volume) {
                        GlobalMediaRouter.this.mCallbackHandler.post(new Runnable() {
                            public void run() {
                                if (GlobalMediaRouter.this.mSelectedRoute != null) {
                                    GlobalMediaRouter.this.mSelectedRoute.requestSetVolume(volume);
                                }
                            }
                        });
                    }

                    public void onAdjustVolume(final int direction) {
                        GlobalMediaRouter.this.mCallbackHandler.post(new Runnable() {
                            public void run() {
                                if (GlobalMediaRouter.this.mSelectedRoute != null) {
                                    GlobalMediaRouter.this.mSelectedRoute.requestUpdateVolume(direction);
                                }
                            }
                        });
                    }
                };
                this.mMsCompat.setPlaybackToRemote(this.mVpCompat);
            }

            public void clearVolumeHandling() {
                this.mMsCompat.setPlaybackToLocal(GlobalMediaRouter.this.mPlaybackInfo.playbackStream);
                this.mVpCompat = null;
            }

            public Token getToken() {
                return this.mMsCompat.getSessionToken();
            }
        }

        /* renamed from: android.support.v7.media.MediaRouter$GlobalMediaRouter$1 */
        class C08641 implements OnActiveChangeListener {
            C08641() {
            }

            public void onActiveChanged() {
                if (GlobalMediaRouter.this.mRccMediaSession == null) {
                    return;
                }
                if (GlobalMediaRouter.this.mRccMediaSession.isActive()) {
                    GlobalMediaRouter globalMediaRouter = GlobalMediaRouter.this;
                    globalMediaRouter.addRemoteControlClient(globalMediaRouter.mRccMediaSession.getRemoteControlClient());
                    return;
                }
                globalMediaRouter = GlobalMediaRouter.this;
                globalMediaRouter.removeRemoteControlClient(globalMediaRouter.mRccMediaSession.getRemoteControlClient());
            }
        }

        private final class ProviderCallback extends android.support.v7.media.MediaRouteProvider.Callback {
            ProviderCallback() {
            }

            public void onDescriptorChanged(MediaRouteProvider provider, MediaRouteProviderDescriptor descriptor) {
                GlobalMediaRouter.this.updateProviderDescriptor(provider, descriptor);
            }
        }

        private final class RemoteControlClientRecord implements VolumeCallback {
            private boolean mDisconnected;
            private final RemoteControlClientCompat mRccCompat;

            public RemoteControlClientRecord(Object rcc) {
                this.mRccCompat = RemoteControlClientCompat.obtain(GlobalMediaRouter.this.mApplicationContext, rcc);
                this.mRccCompat.setVolumeCallback(this);
                updatePlaybackInfo();
            }

            public Object getRemoteControlClient() {
                return this.mRccCompat.getRemoteControlClient();
            }

            public void disconnect() {
                this.mDisconnected = true;
                this.mRccCompat.setVolumeCallback(null);
            }

            public void updatePlaybackInfo() {
                this.mRccCompat.setPlaybackInfo(GlobalMediaRouter.this.mPlaybackInfo);
            }

            public void onVolumeSetRequest(int volume) {
                if (!this.mDisconnected && GlobalMediaRouter.this.mSelectedRoute != null) {
                    GlobalMediaRouter.this.mSelectedRoute.requestSetVolume(volume);
                }
            }

            public void onVolumeUpdateRequest(int direction) {
                if (!this.mDisconnected && GlobalMediaRouter.this.mSelectedRoute != null) {
                    GlobalMediaRouter.this.mSelectedRoute.requestUpdateVolume(direction);
                }
            }
        }

        GlobalMediaRouter(Context applicationContext) {
            this.mApplicationContext = applicationContext;
            this.mDisplayManager = DisplayManagerCompat.getInstance(applicationContext);
            this.mLowRam = ActivityManagerCompat.isLowRamDevice((ActivityManager) applicationContext.getSystemService("activity"));
            this.mSystemProvider = SystemMediaRouteProvider.obtain(applicationContext, this);
        }

        public void start() {
            addProvider(this.mSystemProvider);
            this.mRegisteredProviderWatcher = new RegisteredMediaRouteProviderWatcher(this.mApplicationContext, this);
            this.mRegisteredProviderWatcher.start();
        }

        public MediaRouter getRouter(Context context) {
            int i = this.mRouters.size();
            while (true) {
                i--;
                if (i >= 0) {
                    MediaRouter router = (MediaRouter) ((WeakReference) this.mRouters.get(i)).get();
                    if (router == null) {
                        this.mRouters.remove(i);
                    } else if (router.mContext == context) {
                        return router;
                    }
                } else {
                    MediaRouter router2 = new MediaRouter(context);
                    this.mRouters.add(new WeakReference(router2));
                    return router2;
                }
            }
        }

        public ContentResolver getContentResolver() {
            return this.mApplicationContext.getContentResolver();
        }

        public Context getProviderContext(String packageName) {
            if (packageName.equals(SystemMediaRouteProvider.PACKAGE_NAME)) {
                return this.mApplicationContext;
            }
            try {
                return this.mApplicationContext.createPackageContext(packageName, 4);
            } catch (NameNotFoundException e) {
                return null;
            }
        }

        public Display getDisplay(int displayId) {
            return this.mDisplayManager.getDisplay(displayId);
        }

        public void sendControlRequest(RouteInfo route, Intent intent, ControlRequestCallback callback) {
            if (route == this.mSelectedRoute) {
                RouteController routeController = this.mSelectedRouteController;
                if (routeController != null) {
                    if (!routeController.onControlRequest(intent, callback)) {
                        if (callback != null) {
                            callback.onError(null, null);
                        }
                    }
                    return;
                }
            }
            if (callback != null) {
                callback.onError(null, null);
            }
        }

        public void requestSetVolume(RouteInfo route, int volume) {
            RouteController routeController;
            if (route == this.mSelectedRoute) {
                routeController = this.mSelectedRouteController;
                if (routeController != null) {
                    routeController.onSetVolume(volume);
                    return;
                }
            }
            if (!this.mRouteControllerMap.isEmpty()) {
                routeController = (RouteController) this.mRouteControllerMap.get(route.mDescriptorId);
                if (routeController != null) {
                    routeController.onSetVolume(volume);
                }
            }
        }

        public void requestUpdateVolume(RouteInfo route, int delta) {
            if (route == this.mSelectedRoute) {
                RouteController routeController = this.mSelectedRouteController;
                if (routeController != null) {
                    routeController.onUpdateVolume(delta);
                }
            }
        }

        public RouteInfo getRoute(String uniqueId) {
            Iterator it = this.mRoutes.iterator();
            while (it.hasNext()) {
                RouteInfo info = (RouteInfo) it.next();
                if (info.mUniqueId.equals(uniqueId)) {
                    return info;
                }
            }
            return null;
        }

        public List<RouteInfo> getRoutes() {
            return this.mRoutes;
        }

        List<ProviderInfo> getProviders() {
            return this.mProviders;
        }

        @NonNull
        RouteInfo getDefaultRoute() {
            RouteInfo routeInfo = this.mDefaultRoute;
            if (routeInfo != null) {
                return routeInfo;
            }
            throw new IllegalStateException("There is no default route.  The media router has not yet been fully initialized.");
        }

        RouteInfo getBluetoothRoute() {
            return this.mBluetoothRoute;
        }

        @NonNull
        RouteInfo getSelectedRoute() {
            RouteInfo routeInfo = this.mSelectedRoute;
            if (routeInfo != null) {
                return routeInfo;
            }
            throw new IllegalStateException("There is no currently selected route.  The media router has not yet been fully initialized.");
        }

        void selectRoute(@NonNull RouteInfo route) {
            selectRoute(route, 3);
        }

        void selectRoute(@NonNull RouteInfo route, int unselectReason) {
            String str;
            StringBuilder stringBuilder;
            if (!this.mRoutes.contains(route)) {
                str = MediaRouter.TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append("Ignoring attempt to select removed route: ");
                stringBuilder.append(route);
                Log.w(str, stringBuilder.toString());
            } else if (route.mEnabled) {
                setSelectedRouteInternal(route, unselectReason);
            } else {
                str = MediaRouter.TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append("Ignoring attempt to select disabled route: ");
                stringBuilder.append(route);
                Log.w(str, stringBuilder.toString());
            }
        }

        public boolean isRouteAvailable(MediaRouteSelector selector, int flags) {
            if (selector.isEmpty()) {
                return false;
            }
            if ((flags & 2) == 0 && this.mLowRam) {
                return true;
            }
            int routeCount = this.mRoutes.size();
            for (int i = 0; i < routeCount; i++) {
                RouteInfo route = (RouteInfo) this.mRoutes.get(i);
                if ((flags & 1) != 0) {
                    if (route.isDefaultOrBluetooth()) {
                    }
                }
                if (route.matchesSelector(selector)) {
                    return true;
                }
            }
            return false;
        }

        public void updateDiscoveryRequest() {
            int count;
            boolean discover = false;
            boolean activeScan = false;
            Builder builder = new Builder();
            int i = this.mRouters.size();
            while (true) {
                i--;
                if (i < 0) {
                    break;
                }
                MediaRouter router = (MediaRouter) ((WeakReference) this.mRouters.get(i)).get();
                if (router == null) {
                    this.mRouters.remove(i);
                } else {
                    count = router.mCallbackRecords.size();
                    for (int j = 0; j < count; j++) {
                        CallbackRecord callback = (CallbackRecord) router.mCallbackRecords.get(j);
                        builder.addSelector(callback.mSelector);
                        if ((callback.mFlags & 1) != 0) {
                            activeScan = true;
                            discover = true;
                        }
                        if ((callback.mFlags & 4) != 0) {
                            if (!this.mLowRam) {
                                discover = true;
                            }
                        }
                        if ((callback.mFlags & 8) != 0) {
                            discover = true;
                        }
                    }
                }
            }
            MediaRouteSelector selector = discover ? builder.build() : MediaRouteSelector.EMPTY;
            MediaRouteDiscoveryRequest mediaRouteDiscoveryRequest = this.mDiscoveryRequest;
            if (mediaRouteDiscoveryRequest != null) {
                if (mediaRouteDiscoveryRequest.getSelector().equals(selector)) {
                    if (this.mDiscoveryRequest.isActiveScan() == activeScan) {
                        return;
                    }
                }
            }
            if (!selector.isEmpty() || activeScan) {
                this.mDiscoveryRequest = new MediaRouteDiscoveryRequest(selector, activeScan);
            } else if (this.mDiscoveryRequest != null) {
                this.mDiscoveryRequest = null;
            } else {
                return;
            }
            if (MediaRouter.DEBUG) {
                String str = MediaRouter.TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Updated discovery request: ");
                stringBuilder.append(this.mDiscoveryRequest);
                Log.d(str, stringBuilder.toString());
            }
            if (discover && !activeScan && this.mLowRam) {
                Log.i(MediaRouter.TAG, "Forcing passive route discovery on a low-RAM device, system performance may be affected.  Please consider using CALLBACK_FLAG_REQUEST_DISCOVERY instead of CALLBACK_FLAG_FORCE_DISCOVERY.");
            }
            int providerCount = this.mProviders.size();
            for (count = 0; count < providerCount; count++) {
                ((ProviderInfo) this.mProviders.get(count)).mProviderInstance.setDiscoveryRequest(this.mDiscoveryRequest);
            }
        }

        public void addProvider(MediaRouteProvider providerInstance) {
            if (findProviderInfo(providerInstance) < 0) {
                ProviderInfo provider = new ProviderInfo(providerInstance);
                this.mProviders.add(provider);
                if (MediaRouter.DEBUG) {
                    String str = MediaRouter.TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Provider added: ");
                    stringBuilder.append(provider);
                    Log.d(str, stringBuilder.toString());
                }
                this.mCallbackHandler.post(513, provider);
                updateProviderContents(provider, providerInstance.getDescriptor());
                providerInstance.setCallback(this.mProviderCallback);
                providerInstance.setDiscoveryRequest(this.mDiscoveryRequest);
            }
        }

        public void removeProvider(MediaRouteProvider providerInstance) {
            int index = findProviderInfo(providerInstance);
            if (index >= 0) {
                providerInstance.setCallback(null);
                providerInstance.setDiscoveryRequest(null);
                ProviderInfo provider = (ProviderInfo) this.mProviders.get(index);
                updateProviderContents(provider, null);
                if (MediaRouter.DEBUG) {
                    String str = MediaRouter.TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Provider removed: ");
                    stringBuilder.append(provider);
                    Log.d(str, stringBuilder.toString());
                }
                this.mCallbackHandler.post(CallbackHandler.MSG_PROVIDER_REMOVED, provider);
                this.mProviders.remove(index);
            }
        }

        void updateProviderDescriptor(MediaRouteProvider providerInstance, MediaRouteProviderDescriptor descriptor) {
            int index = findProviderInfo(providerInstance);
            if (index >= 0) {
                updateProviderContents((ProviderInfo) this.mProviders.get(index), descriptor);
            }
        }

        private int findProviderInfo(MediaRouteProvider providerInstance) {
            int count = this.mProviders.size();
            for (int i = 0; i < count; i++) {
                if (((ProviderInfo) this.mProviders.get(i)).mProviderInstance == providerInstance) {
                    return i;
                }
            }
            return -1;
        }

        private void updateProviderContents(ProviderInfo provider, MediaRouteProviderDescriptor providerDescriptor) {
            GlobalMediaRouter globalMediaRouter = this;
            ProviderInfo providerInfo = provider;
            MediaRouteProviderDescriptor mediaRouteProviderDescriptor = providerDescriptor;
            if (provider.updateDescriptor(providerDescriptor)) {
                String str;
                StringBuilder stringBuilder;
                int i;
                RouteInfo route;
                int targetIndex = 0;
                boolean selectedRouteDescriptorChanged = false;
                if (mediaRouteProviderDescriptor != null) {
                    if (providerDescriptor.isValid()) {
                        boolean selectedRouteDescriptorChanged2;
                        RouteInfo route2;
                        List<MediaRouteDescriptor> routeDescriptors = providerDescriptor.getRoutes();
                        int routeCount = routeDescriptors.size();
                        List<Pair<RouteInfo, MediaRouteDescriptor>> addedGroups = new ArrayList();
                        List<Pair<RouteInfo, MediaRouteDescriptor>> updatedGroups = new ArrayList();
                        for (int i2 = 0; i2 < routeCount; i2++) {
                            MediaRouteDescriptor routeDescriptor = (MediaRouteDescriptor) routeDescriptors.get(i2);
                            String id = routeDescriptor.getId();
                            int sourceIndex = providerInfo.findRouteByDescriptorId(id);
                            if (sourceIndex < 0) {
                                String uniqueId = assignRouteUniqueId(providerInfo, id);
                                boolean isGroup = routeDescriptor.getGroupMemberIds() != null;
                                RouteInfo route3 = isGroup ? new RouteGroup(providerInfo, id, uniqueId) : new RouteInfo(providerInfo, id, uniqueId);
                                int targetIndex2 = targetIndex + 1;
                                provider.mRoutes.add(targetIndex, route3);
                                globalMediaRouter.mRoutes.add(route3);
                                if (isGroup) {
                                    addedGroups.add(new Pair(route3, routeDescriptor));
                                    selectedRouteDescriptorChanged2 = selectedRouteDescriptorChanged;
                                } else {
                                    route3.maybeUpdateDescriptor(routeDescriptor);
                                    if (MediaRouter.DEBUG) {
                                        String str2 = MediaRouter.TAG;
                                        StringBuilder stringBuilder2 = new StringBuilder();
                                        selectedRouteDescriptorChanged2 = selectedRouteDescriptorChanged;
                                        stringBuilder2.append("Route added: ");
                                        stringBuilder2.append(route3);
                                        Log.d(str2, stringBuilder2.toString());
                                    } else {
                                        selectedRouteDescriptorChanged2 = selectedRouteDescriptorChanged;
                                    }
                                    globalMediaRouter.mCallbackHandler.post(257, route3);
                                }
                                targetIndex = targetIndex2;
                                selectedRouteDescriptorChanged = selectedRouteDescriptorChanged2;
                            } else {
                                selectedRouteDescriptorChanged2 = selectedRouteDescriptorChanged;
                                if (sourceIndex < targetIndex) {
                                    String str3 = MediaRouter.TAG;
                                    StringBuilder stringBuilder3 = new StringBuilder();
                                    stringBuilder3.append("Ignoring route descriptor with duplicate id: ");
                                    stringBuilder3.append(routeDescriptor);
                                    Log.w(str3, stringBuilder3.toString());
                                    selectedRouteDescriptorChanged = selectedRouteDescriptorChanged2;
                                } else {
                                    RouteInfo route4 = (RouteInfo) provider.mRoutes.get(sourceIndex);
                                    int targetIndex3 = targetIndex + 1;
                                    Collections.swap(provider.mRoutes, sourceIndex, targetIndex);
                                    if (route4 instanceof RouteGroup) {
                                        updatedGroups.add(new Pair(route4, routeDescriptor));
                                    } else if (updateRouteDescriptorAndNotify(route4, routeDescriptor) != 0) {
                                        if (route4 == globalMediaRouter.mSelectedRoute) {
                                            selectedRouteDescriptorChanged = true;
                                            targetIndex = targetIndex3;
                                        }
                                    }
                                    targetIndex = targetIndex3;
                                    selectedRouteDescriptorChanged = selectedRouteDescriptorChanged2;
                                }
                            }
                        }
                        selectedRouteDescriptorChanged2 = selectedRouteDescriptorChanged;
                        for (Pair<RouteInfo, MediaRouteDescriptor> pair : addedGroups) {
                            route2 = pair.first;
                            route2.maybeUpdateDescriptor((MediaRouteDescriptor) pair.second);
                            if (MediaRouter.DEBUG) {
                                String str4 = MediaRouter.TAG;
                                StringBuilder stringBuilder4 = new StringBuilder();
                                stringBuilder4.append("Route added: ");
                                stringBuilder4.append(route2);
                                Log.d(str4, stringBuilder4.toString());
                            }
                            globalMediaRouter.mCallbackHandler.post(257, route2);
                        }
                        for (Pair<RouteInfo, MediaRouteDescriptor> pair2 : updatedGroups) {
                            route2 = (RouteInfo) pair2.first;
                            if (updateRouteDescriptorAndNotify(route2, (MediaRouteDescriptor) pair2.second) != 0) {
                                if (route2 == globalMediaRouter.mSelectedRoute) {
                                    selectedRouteDescriptorChanged2 = true;
                                }
                            }
                        }
                        selectedRouteDescriptorChanged = selectedRouteDescriptorChanged2;
                    } else {
                        str = MediaRouter.TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Ignoring invalid provider descriptor: ");
                        stringBuilder.append(mediaRouteProviderDescriptor);
                        Log.w(str, stringBuilder.toString());
                    }
                }
                for (i = provider.mRoutes.size() - 1; i >= targetIndex; i--) {
                    route = (RouteInfo) provider.mRoutes.get(i);
                    route.maybeUpdateDescriptor(null);
                    globalMediaRouter.mRoutes.remove(route);
                }
                updateSelectedRouteIfNeeded(selectedRouteDescriptorChanged);
                for (i = provider.mRoutes.size() - 1; i >= targetIndex; i--) {
                    route = (RouteInfo) provider.mRoutes.remove(i);
                    if (MediaRouter.DEBUG) {
                        String str5 = MediaRouter.TAG;
                        StringBuilder stringBuilder5 = new StringBuilder();
                        stringBuilder5.append("Route removed: ");
                        stringBuilder5.append(route);
                        Log.d(str5, stringBuilder5.toString());
                    }
                    globalMediaRouter.mCallbackHandler.post(CallbackHandler.MSG_ROUTE_REMOVED, route);
                }
                if (MediaRouter.DEBUG) {
                    str = MediaRouter.TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Provider changed: ");
                    stringBuilder.append(providerInfo);
                    Log.d(str, stringBuilder.toString());
                }
                globalMediaRouter.mCallbackHandler.post(CallbackHandler.MSG_PROVIDER_CHANGED, providerInfo);
            }
        }

        private int updateRouteDescriptorAndNotify(RouteInfo route, MediaRouteDescriptor routeDescriptor) {
            int changes = route.maybeUpdateDescriptor(routeDescriptor);
            if (changes != 0) {
                String str;
                StringBuilder stringBuilder;
                if ((changes & 1) != 0) {
                    if (MediaRouter.DEBUG) {
                        str = MediaRouter.TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Route changed: ");
                        stringBuilder.append(route);
                        Log.d(str, stringBuilder.toString());
                    }
                    this.mCallbackHandler.post(CallbackHandler.MSG_ROUTE_CHANGED, route);
                }
                if ((changes & 2) != 0) {
                    if (MediaRouter.DEBUG) {
                        str = MediaRouter.TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Route volume changed: ");
                        stringBuilder.append(route);
                        Log.d(str, stringBuilder.toString());
                    }
                    this.mCallbackHandler.post(CallbackHandler.MSG_ROUTE_VOLUME_CHANGED, route);
                }
                if ((changes & 4) != 0) {
                    if (MediaRouter.DEBUG) {
                        str = MediaRouter.TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Route presentation display changed: ");
                        stringBuilder.append(route);
                        Log.d(str, stringBuilder.toString());
                    }
                    this.mCallbackHandler.post(CallbackHandler.MSG_ROUTE_PRESENTATION_DISPLAY_CHANGED, route);
                }
            }
            return changes;
        }

        private String assignRouteUniqueId(ProviderInfo provider, String routeDescriptorId) {
            String componentName = provider.getComponentName().flattenToShortString();
            String uniqueId = new StringBuilder();
            uniqueId.append(componentName);
            uniqueId.append(":");
            uniqueId.append(routeDescriptorId);
            uniqueId = uniqueId.toString();
            if (findRouteByUniqueId(uniqueId) < 0) {
                this.mUniqueIdMap.put(new Pair(componentName, routeDescriptorId), uniqueId);
                return uniqueId;
            }
            String str = MediaRouter.TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Either ");
            stringBuilder.append(routeDescriptorId);
            stringBuilder.append(" isn't unique in ");
            stringBuilder.append(componentName);
            stringBuilder.append(" or we're trying to assign a unique ID for an already added route");
            Log.w(str, stringBuilder.toString());
            int i = 2;
            while (true) {
                String newUniqueId = String.format(Locale.US, "%s_%d", new Object[]{uniqueId, Integer.valueOf(i)});
                if (findRouteByUniqueId(newUniqueId) < 0) {
                    this.mUniqueIdMap.put(new Pair(componentName, routeDescriptorId), newUniqueId);
                    return newUniqueId;
                }
                i++;
            }
        }

        private int findRouteByUniqueId(String uniqueId) {
            int count = this.mRoutes.size();
            for (int i = 0; i < count; i++) {
                if (((RouteInfo) this.mRoutes.get(i)).mUniqueId.equals(uniqueId)) {
                    return i;
                }
            }
            return -1;
        }

        private String getUniqueId(ProviderInfo provider, String routeDescriptorId) {
            return (String) this.mUniqueIdMap.get(new Pair(provider.getComponentName().flattenToShortString(), routeDescriptorId));
        }

        private void updateSelectedRouteIfNeeded(boolean selectedRouteDescriptorChanged) {
            String str;
            Iterator it;
            RouteInfo routeInfo = this.mDefaultRoute;
            if (routeInfo != null && !routeInfo.isSelectable()) {
                str = MediaRouter.TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Clearing the default route because it is no longer selectable: ");
                stringBuilder.append(this.mDefaultRoute);
                Log.i(str, stringBuilder.toString());
                this.mDefaultRoute = null;
            }
            if (this.mDefaultRoute == null && !this.mRoutes.isEmpty()) {
                it = this.mRoutes.iterator();
                while (it.hasNext()) {
                    RouteInfo route = (RouteInfo) it.next();
                    if (isSystemDefaultRoute(route) && route.isSelectable()) {
                        this.mDefaultRoute = route;
                        str = MediaRouter.TAG;
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Found default route: ");
                        stringBuilder2.append(this.mDefaultRoute);
                        Log.i(str, stringBuilder2.toString());
                        break;
                    }
                }
            }
            routeInfo = this.mBluetoothRoute;
            if (routeInfo != null && !routeInfo.isSelectable()) {
                str = MediaRouter.TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append("Clearing the bluetooth route because it is no longer selectable: ");
                stringBuilder.append(this.mBluetoothRoute);
                Log.i(str, stringBuilder.toString());
                this.mBluetoothRoute = null;
            }
            if (this.mBluetoothRoute == null && !this.mRoutes.isEmpty()) {
                it = this.mRoutes.iterator();
                while (it.hasNext()) {
                    RouteInfo route2 = (RouteInfo) it.next();
                    if (isSystemLiveAudioOnlyRoute(route2) && route2.isSelectable()) {
                        this.mBluetoothRoute = route2;
                        str = MediaRouter.TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Found bluetooth route: ");
                        stringBuilder.append(this.mBluetoothRoute);
                        Log.i(str, stringBuilder.toString());
                        break;
                    }
                }
            }
            routeInfo = this.mSelectedRoute;
            if (routeInfo != null) {
                if (routeInfo.isSelectable()) {
                    if (selectedRouteDescriptorChanged) {
                        routeInfo = this.mSelectedRoute;
                        if (routeInfo instanceof RouteGroup) {
                            List<RouteInfo> routes = ((RouteGroup) routeInfo).getRoutes();
                            Set<String> idSet = new HashSet();
                            for (RouteInfo route3 : routes) {
                                idSet.add(route3.mDescriptorId);
                            }
                            Iterator<Entry<String, RouteController>> iter = this.mRouteControllerMap.entrySet().iterator();
                            while (iter.hasNext()) {
                                Entry<String, RouteController> entry = (Entry) iter.next();
                                if (!idSet.contains(entry.getKey())) {
                                    RouteController controller = (RouteController) entry.getValue();
                                    controller.onUnselect();
                                    controller.onRelease();
                                    iter.remove();
                                }
                            }
                            for (RouteInfo route4 : routes) {
                                if (!this.mRouteControllerMap.containsKey(route4.mDescriptorId)) {
                                    RouteController controller2 = route4.getProviderInstance().onCreateRouteController(route4.mDescriptorId, this.mSelectedRoute.mDescriptorId);
                                    controller2.onSelect();
                                    this.mRouteControllerMap.put(route4.mDescriptorId, controller2);
                                }
                            }
                        }
                        updatePlaybackInfoFromSelectedRoute();
                        return;
                    }
                    return;
                }
            }
            str = MediaRouter.TAG;
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("Unselecting the current route because it is no longer selectable: ");
            stringBuilder3.append(this.mSelectedRoute);
            Log.i(str, stringBuilder3.toString());
            setSelectedRouteInternal(chooseFallbackRoute(), 0);
        }

        RouteInfo chooseFallbackRoute() {
            Iterator it = this.mRoutes.iterator();
            while (it.hasNext()) {
                RouteInfo route = (RouteInfo) it.next();
                if (route != this.mDefaultRoute) {
                    if (isSystemLiveAudioOnlyRoute(route)) {
                        if (route.isSelectable()) {
                            return route;
                        }
                    }
                }
            }
            return this.mDefaultRoute;
        }

        private boolean isSystemLiveAudioOnlyRoute(RouteInfo route) {
            if (route.getProviderInstance() == this.mSystemProvider) {
                if (route.supportsControlCategory(MediaControlIntent.CATEGORY_LIVE_AUDIO)) {
                    if (!route.supportsControlCategory(MediaControlIntent.CATEGORY_LIVE_VIDEO)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean isSystemDefaultRoute(RouteInfo route) {
            if (route.getProviderInstance() == this.mSystemProvider) {
                if (route.mDescriptorId.equals(SystemMediaRouteProvider.DEFAULT_ROUTE_ID)) {
                    return true;
                }
            }
            return false;
        }

        private void setSelectedRouteInternal(@NonNull RouteInfo route, int unselectReason) {
            RouteInfo routeInfo;
            String str;
            StringBuilder stringBuilder;
            RouteController routeController;
            if (MediaRouter.sGlobal != null) {
                if (this.mBluetoothRoute == null || !route.isDefault()) {
                    routeInfo = this.mSelectedRoute;
                    if (routeInfo != route) {
                        if (routeInfo != null) {
                            if (MediaRouter.DEBUG) {
                                str = MediaRouter.TAG;
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("Route unselected: ");
                                stringBuilder.append(this.mSelectedRoute);
                                stringBuilder.append(" reason: ");
                                stringBuilder.append(unselectReason);
                                Log.d(str, stringBuilder.toString());
                            }
                            this.mCallbackHandler.post(CallbackHandler.MSG_ROUTE_UNSELECTED, this.mSelectedRoute, unselectReason);
                            routeController = this.mSelectedRouteController;
                            if (routeController != null) {
                                routeController.onUnselect(unselectReason);
                                this.mSelectedRouteController.onRelease();
                                this.mSelectedRouteController = null;
                            }
                            if (!this.mRouteControllerMap.isEmpty()) {
                                for (RouteController controller : this.mRouteControllerMap.values()) {
                                    controller.onUnselect(unselectReason);
                                    controller.onRelease();
                                }
                                this.mRouteControllerMap.clear();
                            }
                        }
                        this.mSelectedRoute = route;
                        this.mSelectedRouteController = route.getProviderInstance().onCreateRouteController(route.mDescriptorId);
                        routeController = this.mSelectedRouteController;
                        if (routeController != null) {
                            routeController.onSelect();
                        }
                        if (MediaRouter.DEBUG) {
                            str = MediaRouter.TAG;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Route selected: ");
                            stringBuilder.append(this.mSelectedRoute);
                            Log.d(str, stringBuilder.toString());
                        }
                        this.mCallbackHandler.post(CallbackHandler.MSG_ROUTE_SELECTED, this.mSelectedRoute);
                        routeInfo = this.mSelectedRoute;
                        if (routeInfo instanceof RouteGroup) {
                            List<RouteInfo> routes = ((RouteGroup) routeInfo).getRoutes();
                            this.mRouteControllerMap.clear();
                            for (RouteInfo r : routes) {
                                RouteController controller2 = r.getProviderInstance().onCreateRouteController(r.mDescriptorId, this.mSelectedRoute.mDescriptorId);
                                controller2.onSelect();
                                this.mRouteControllerMap.put(r.mDescriptorId, controller2);
                            }
                        }
                        updatePlaybackInfoFromSelectedRoute();
                    }
                }
            }
            StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
            stringBuilder = new StringBuilder();
            for (int i = 3; i < callStack.length; i++) {
                StackTraceElement caller = callStack[i];
                stringBuilder.append(caller.getClassName());
                stringBuilder.append(".");
                stringBuilder.append(caller.getMethodName());
                stringBuilder.append(":");
                stringBuilder.append(caller.getLineNumber());
                stringBuilder.append("  ");
            }
            String str2;
            StringBuilder stringBuilder2;
            if (MediaRouter.sGlobal == null) {
                str2 = MediaRouter.TAG;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("setSelectedRouteInternal is called while sGlobal is null: pkgName=");
                stringBuilder2.append(this.mApplicationContext.getPackageName());
                stringBuilder2.append(", callers=");
                stringBuilder2.append(stringBuilder.toString());
                Log.w(str2, stringBuilder2.toString());
            } else {
                str2 = MediaRouter.TAG;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Default route is selected while a BT route is available: pkgName=");
                stringBuilder2.append(this.mApplicationContext.getPackageName());
                stringBuilder2.append(", callers=");
                stringBuilder2.append(stringBuilder.toString());
                Log.w(str2, stringBuilder2.toString());
            }
            routeInfo = this.mSelectedRoute;
            if (routeInfo != route) {
                if (routeInfo != null) {
                    if (MediaRouter.DEBUG) {
                        str = MediaRouter.TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Route unselected: ");
                        stringBuilder.append(this.mSelectedRoute);
                        stringBuilder.append(" reason: ");
                        stringBuilder.append(unselectReason);
                        Log.d(str, stringBuilder.toString());
                    }
                    this.mCallbackHandler.post(CallbackHandler.MSG_ROUTE_UNSELECTED, this.mSelectedRoute, unselectReason);
                    routeController = this.mSelectedRouteController;
                    if (routeController != null) {
                        routeController.onUnselect(unselectReason);
                        this.mSelectedRouteController.onRelease();
                        this.mSelectedRouteController = null;
                    }
                    if (!this.mRouteControllerMap.isEmpty()) {
                        for (RouteController controller3 : this.mRouteControllerMap.values()) {
                            controller3.onUnselect(unselectReason);
                            controller3.onRelease();
                        }
                        this.mRouteControllerMap.clear();
                    }
                }
                this.mSelectedRoute = route;
                this.mSelectedRouteController = route.getProviderInstance().onCreateRouteController(route.mDescriptorId);
                routeController = this.mSelectedRouteController;
                if (routeController != null) {
                    routeController.onSelect();
                }
                if (MediaRouter.DEBUG) {
                    str = MediaRouter.TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Route selected: ");
                    stringBuilder.append(this.mSelectedRoute);
                    Log.d(str, stringBuilder.toString());
                }
                this.mCallbackHandler.post(CallbackHandler.MSG_ROUTE_SELECTED, this.mSelectedRoute);
                routeInfo = this.mSelectedRoute;
                if (routeInfo instanceof RouteGroup) {
                    List<RouteInfo> routes2 = ((RouteGroup) routeInfo).getRoutes();
                    this.mRouteControllerMap.clear();
                    for (RouteInfo r2 : routes2) {
                        RouteController controller22 = r2.getProviderInstance().onCreateRouteController(r2.mDescriptorId, this.mSelectedRoute.mDescriptorId);
                        controller22.onSelect();
                        this.mRouteControllerMap.put(r2.mDescriptorId, controller22);
                    }
                }
                updatePlaybackInfoFromSelectedRoute();
            }
        }

        public void onSystemRouteSelectedByDescriptorId(String id) {
            this.mCallbackHandler.removeMessages(CallbackHandler.MSG_ROUTE_SELECTED);
            int providerIndex = findProviderInfo(this.mSystemProvider);
            if (providerIndex >= 0) {
                ProviderInfo provider = (ProviderInfo) this.mProviders.get(providerIndex);
                int routeIndex = provider.findRouteByDescriptorId(id);
                if (routeIndex >= 0) {
                    ((RouteInfo) provider.mRoutes.get(routeIndex)).select();
                }
            }
        }

        public void addRemoteControlClient(Object rcc) {
            if (findRemoteControlClientRecord(rcc) < 0) {
                this.mRemoteControlClients.add(new RemoteControlClientRecord(rcc));
            }
        }

        public void removeRemoteControlClient(Object rcc) {
            int index = findRemoteControlClientRecord(rcc);
            if (index >= 0) {
                ((RemoteControlClientRecord) this.mRemoteControlClients.remove(index)).disconnect();
            }
        }

        public void setMediaSession(Object session) {
            setMediaSessionRecord(session != null ? new MediaSessionRecord(session) : null);
        }

        public void setMediaSessionCompat(MediaSessionCompat session) {
            this.mCompatSession = session;
            if (VERSION.SDK_INT >= 21) {
                setMediaSessionRecord(session != null ? new MediaSessionRecord(session) : null);
            } else if (VERSION.SDK_INT >= 14) {
                MediaSessionCompat mediaSessionCompat = this.mRccMediaSession;
                if (mediaSessionCompat != null) {
                    removeRemoteControlClient(mediaSessionCompat.getRemoteControlClient());
                    this.mRccMediaSession.removeOnActiveChangeListener(this.mSessionActiveListener);
                }
                this.mRccMediaSession = session;
                if (session != null) {
                    session.addOnActiveChangeListener(this.mSessionActiveListener);
                    if (session.isActive()) {
                        addRemoteControlClient(session.getRemoteControlClient());
                    }
                }
            }
        }

        private void setMediaSessionRecord(MediaSessionRecord mediaSessionRecord) {
            MediaSessionRecord mediaSessionRecord2 = this.mMediaSession;
            if (mediaSessionRecord2 != null) {
                mediaSessionRecord2.clearVolumeHandling();
            }
            this.mMediaSession = mediaSessionRecord;
            if (mediaSessionRecord != null) {
                updatePlaybackInfoFromSelectedRoute();
            }
        }

        public Token getMediaSessionToken() {
            MediaSessionRecord mediaSessionRecord = this.mMediaSession;
            if (mediaSessionRecord != null) {
                return mediaSessionRecord.getToken();
            }
            MediaSessionCompat mediaSessionCompat = this.mCompatSession;
            if (mediaSessionCompat != null) {
                return mediaSessionCompat.getSessionToken();
            }
            return null;
        }

        private int findRemoteControlClientRecord(Object rcc) {
            int count = this.mRemoteControlClients.size();
            for (int i = 0; i < count; i++) {
                if (((RemoteControlClientRecord) this.mRemoteControlClients.get(i)).getRemoteControlClient() == rcc) {
                    return i;
                }
            }
            return -1;
        }

        private void updatePlaybackInfoFromSelectedRoute() {
            RouteInfo routeInfo = this.mSelectedRoute;
            if (routeInfo != null) {
                int i;
                this.mPlaybackInfo.volume = routeInfo.getVolume();
                this.mPlaybackInfo.volumeMax = this.mSelectedRoute.getVolumeMax();
                this.mPlaybackInfo.volumeHandling = this.mSelectedRoute.getVolumeHandling();
                this.mPlaybackInfo.playbackStream = this.mSelectedRoute.getPlaybackStream();
                this.mPlaybackInfo.playbackType = this.mSelectedRoute.getPlaybackType();
                int count = this.mRemoteControlClients.size();
                for (i = 0; i < count; i++) {
                    ((RemoteControlClientRecord) this.mRemoteControlClients.get(i)).updatePlaybackInfo();
                }
                if (this.mMediaSession != null) {
                    if (this.mSelectedRoute != getDefaultRoute()) {
                        if (this.mSelectedRoute != getBluetoothRoute()) {
                            i = 0;
                            if (this.mPlaybackInfo.volumeHandling == 1) {
                                i = 2;
                            }
                            this.mMediaSession.configureVolume(i, this.mPlaybackInfo.volumeMax, this.mPlaybackInfo.volume);
                        }
                    }
                    this.mMediaSession.clearVolumeHandling();
                }
                return;
            }
            MediaSessionRecord mediaSessionRecord = this.mMediaSession;
            if (mediaSessionRecord != null) {
                mediaSessionRecord.clearVolumeHandling();
            }
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public static class RouteGroup extends RouteInfo {
        private List<RouteInfo> mRoutes = new ArrayList();

        RouteGroup(ProviderInfo provider, String descriptorId, String uniqueId) {
            super(provider, descriptorId, uniqueId);
        }

        public int getRouteCount() {
            return this.mRoutes.size();
        }

        public RouteInfo getRouteAt(int index) {
            return (RouteInfo) this.mRoutes.get(index);
        }

        public List<RouteInfo> getRoutes() {
            return this.mRoutes;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(super.toString());
            sb.append('[');
            int count = this.mRoutes.size();
            for (int i = 0; i < count; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(this.mRoutes.get(i));
            }
            sb.append(']');
            return sb.toString();
        }

        int maybeUpdateDescriptor(MediaRouteDescriptor descriptor) {
            boolean changed = false;
            int i = 1;
            if (this.mDescriptor != descriptor) {
                this.mDescriptor = descriptor;
                if (descriptor != null) {
                    List<String> groupMemberIds = descriptor.getGroupMemberIds();
                    List<RouteInfo> routes = new ArrayList();
                    changed = groupMemberIds.size() != this.mRoutes.size();
                    for (String groupMemberId : groupMemberIds) {
                        RouteInfo groupMember = MediaRouter.sGlobal.getRoute(MediaRouter.sGlobal.getUniqueId(getProvider(), groupMemberId));
                        if (groupMember != null) {
                            routes.add(groupMember);
                            if (!changed && !this.mRoutes.contains(groupMember)) {
                                changed = true;
                            }
                        }
                    }
                    if (changed) {
                        this.mRoutes = routes;
                    }
                }
            }
            if (!changed) {
                i = 0;
            }
            return super.updateDescriptor(descriptor) | i;
        }
    }

    private MediaRouter(Context context) {
        this.mCallbackRecords = new ArrayList();
        this.mContext = context;
    }

    public static MediaRouter getInstance(@NonNull Context context) {
        if (context != null) {
            checkCallingThread();
            if (sGlobal == null) {
                sGlobal = new GlobalMediaRouter(context.getApplicationContext());
                sGlobal.start();
            }
            return sGlobal.getRouter(context);
        }
        throw new IllegalArgumentException("context must not be null");
    }

    public List<RouteInfo> getRoutes() {
        checkCallingThread();
        return sGlobal.getRoutes();
    }

    public List<ProviderInfo> getProviders() {
        checkCallingThread();
        return sGlobal.getProviders();
    }

    @NonNull
    public RouteInfo getDefaultRoute() {
        checkCallingThread();
        return sGlobal.getDefaultRoute();
    }

    public RouteInfo getBluetoothRoute() {
        checkCallingThread();
        return sGlobal.getBluetoothRoute();
    }

    @NonNull
    public RouteInfo getSelectedRoute() {
        checkCallingThread();
        return sGlobal.getSelectedRoute();
    }

    @NonNull
    public RouteInfo updateSelectedRoute(@NonNull MediaRouteSelector selector) {
        if (selector != null) {
            checkCallingThread();
            if (DEBUG) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("updateSelectedRoute: ");
                stringBuilder.append(selector);
                Log.d(str, stringBuilder.toString());
            }
            RouteInfo route = sGlobal.getSelectedRoute();
            if (route.isDefaultOrBluetooth() || route.matchesSelector(selector)) {
                return route;
            }
            route = sGlobal.chooseFallbackRoute();
            sGlobal.selectRoute(route);
            return route;
        }
        throw new IllegalArgumentException("selector must not be null");
    }

    public void selectRoute(@NonNull RouteInfo route) {
        if (route != null) {
            checkCallingThread();
            if (DEBUG) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("selectRoute: ");
                stringBuilder.append(route);
                Log.d(str, stringBuilder.toString());
            }
            sGlobal.selectRoute(route);
            return;
        }
        throw new IllegalArgumentException("route must not be null");
    }

    public void unselect(int reason) {
        if (reason < 0 || reason > 3) {
            throw new IllegalArgumentException("Unsupported reason to unselect route");
        }
        checkCallingThread();
        RouteInfo fallbackRoute = sGlobal.chooseFallbackRoute();
        if (sGlobal.getSelectedRoute() != fallbackRoute) {
            sGlobal.selectRoute(fallbackRoute, reason);
            return;
        }
        GlobalMediaRouter globalMediaRouter = sGlobal;
        globalMediaRouter.selectRoute(globalMediaRouter.getDefaultRoute(), reason);
    }

    public boolean isRouteAvailable(@NonNull MediaRouteSelector selector, int flags) {
        if (selector != null) {
            checkCallingThread();
            return sGlobal.isRouteAvailable(selector, flags);
        }
        throw new IllegalArgumentException("selector must not be null");
    }

    public void addCallback(MediaRouteSelector selector, Callback callback) {
        addCallback(selector, callback, 0);
    }

    public void addCallback(@NonNull MediaRouteSelector selector, @NonNull Callback callback, int flags) {
        if (selector == null) {
            throw new IllegalArgumentException("selector must not be null");
        } else if (callback != null) {
            CallbackRecord record;
            checkCallingThread();
            if (DEBUG) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("addCallback: selector=");
                stringBuilder.append(selector);
                stringBuilder.append(", callback=");
                stringBuilder.append(callback);
                stringBuilder.append(", flags=");
                stringBuilder.append(Integer.toHexString(flags));
                Log.d(str, stringBuilder.toString());
            }
            int index = findCallbackRecord(callback);
            if (index < 0) {
                record = new CallbackRecord(this, callback);
                this.mCallbackRecords.add(record);
            } else {
                record = (CallbackRecord) this.mCallbackRecords.get(index);
            }
            boolean updateNeeded = false;
            if (((record.mFlags ^ -1) & flags) != 0) {
                record.mFlags |= flags;
                updateNeeded = true;
            }
            if (!record.mSelector.contains(selector)) {
                record.mSelector = new Builder(record.mSelector).addSelector(selector).build();
                updateNeeded = true;
            }
            if (updateNeeded) {
                sGlobal.updateDiscoveryRequest();
            }
        } else {
            throw new IllegalArgumentException("callback must not be null");
        }
    }

    public void removeCallback(@NonNull Callback callback) {
        if (callback != null) {
            checkCallingThread();
            if (DEBUG) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("removeCallback: callback=");
                stringBuilder.append(callback);
                Log.d(str, stringBuilder.toString());
            }
            int index = findCallbackRecord(callback);
            if (index >= 0) {
                this.mCallbackRecords.remove(index);
                sGlobal.updateDiscoveryRequest();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("callback must not be null");
    }

    private int findCallbackRecord(Callback callback) {
        int count = this.mCallbackRecords.size();
        for (int i = 0; i < count; i++) {
            if (((CallbackRecord) this.mCallbackRecords.get(i)).mCallback == callback) {
                return i;
            }
        }
        return -1;
    }

    public void addProvider(@NonNull MediaRouteProvider providerInstance) {
        if (providerInstance != null) {
            checkCallingThread();
            if (DEBUG) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("addProvider: ");
                stringBuilder.append(providerInstance);
                Log.d(str, stringBuilder.toString());
            }
            sGlobal.addProvider(providerInstance);
            return;
        }
        throw new IllegalArgumentException("providerInstance must not be null");
    }

    public void removeProvider(@NonNull MediaRouteProvider providerInstance) {
        if (providerInstance != null) {
            checkCallingThread();
            if (DEBUG) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("removeProvider: ");
                stringBuilder.append(providerInstance);
                Log.d(str, stringBuilder.toString());
            }
            sGlobal.removeProvider(providerInstance);
            return;
        }
        throw new IllegalArgumentException("providerInstance must not be null");
    }

    public void addRemoteControlClient(@NonNull Object remoteControlClient) {
        if (remoteControlClient != null) {
            checkCallingThread();
            if (DEBUG) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("addRemoteControlClient: ");
                stringBuilder.append(remoteControlClient);
                Log.d(str, stringBuilder.toString());
            }
            sGlobal.addRemoteControlClient(remoteControlClient);
            return;
        }
        throw new IllegalArgumentException("remoteControlClient must not be null");
    }

    public void removeRemoteControlClient(@NonNull Object remoteControlClient) {
        if (remoteControlClient != null) {
            if (DEBUG) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("removeRemoteControlClient: ");
                stringBuilder.append(remoteControlClient);
                Log.d(str, stringBuilder.toString());
            }
            sGlobal.removeRemoteControlClient(remoteControlClient);
            return;
        }
        throw new IllegalArgumentException("remoteControlClient must not be null");
    }

    public void setMediaSession(Object mediaSession) {
        if (DEBUG) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("addMediaSession: ");
            stringBuilder.append(mediaSession);
            Log.d(str, stringBuilder.toString());
        }
        sGlobal.setMediaSession(mediaSession);
    }

    public void setMediaSessionCompat(MediaSessionCompat mediaSession) {
        if (DEBUG) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("addMediaSessionCompat: ");
            stringBuilder.append(mediaSession);
            Log.d(str, stringBuilder.toString());
        }
        sGlobal.setMediaSessionCompat(mediaSession);
    }

    public Token getMediaSessionToken() {
        return sGlobal.getMediaSessionToken();
    }

    static void checkCallingThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("The media router service must only be accessed on the application's main thread.");
        }
    }

    static <T> boolean equal(T a, T b) {
        if (a != b) {
            if (a == null || b == null || !a.equals(b)) {
                return false;
            }
        }
        return true;
    }
}
