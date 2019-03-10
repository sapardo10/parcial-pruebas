package android.support.v7.media;

import android.content.IntentFilter;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class MediaRouteDescriptor {
    static final String KEY_CAN_DISCONNECT = "canDisconnect";
    static final String KEY_CONNECTING = "connecting";
    static final String KEY_CONNECTION_STATE = "connectionState";
    static final String KEY_CONTROL_FILTERS = "controlFilters";
    static final String KEY_DESCRIPTION = "status";
    static final String KEY_DEVICE_TYPE = "deviceType";
    static final String KEY_ENABLED = "enabled";
    static final String KEY_EXTRAS = "extras";
    static final String KEY_GROUP_MEMBER_IDS = "groupMemberIds";
    static final String KEY_ICON_URI = "iconUri";
    static final String KEY_ID = "id";
    static final String KEY_MAX_CLIENT_VERSION = "maxClientVersion";
    static final String KEY_MIN_CLIENT_VERSION = "minClientVersion";
    static final String KEY_NAME = "name";
    static final String KEY_PLAYBACK_STREAM = "playbackStream";
    static final String KEY_PLAYBACK_TYPE = "playbackType";
    static final String KEY_PRESENTATION_DISPLAY_ID = "presentationDisplayId";
    static final String KEY_SETTINGS_INTENT = "settingsIntent";
    static final String KEY_VOLUME = "volume";
    static final String KEY_VOLUME_HANDLING = "volumeHandling";
    static final String KEY_VOLUME_MAX = "volumeMax";
    final Bundle mBundle;
    List<IntentFilter> mControlFilters;

    public static final class Builder {
        private final Bundle mBundle;
        private ArrayList<IntentFilter> mControlFilters;
        private ArrayList<String> mGroupMemberIds;

        public android.support.v7.media.MediaRouteDescriptor.Builder addControlFilters(java.util.Collection<android.content.IntentFilter> r3) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:12:0x0027 in {6, 7, 8, 9, 11} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r2 = this;
            if (r3 == 0) goto L_0x001f;
        L_0x0002:
            r0 = r3.isEmpty();
            if (r0 != 0) goto L_0x001d;
        L_0x0008:
            r0 = r3.iterator();
        L_0x000c:
            r1 = r0.hasNext();
            if (r1 == 0) goto L_0x001c;
        L_0x0012:
            r1 = r0.next();
            r1 = (android.content.IntentFilter) r1;
            r2.addControlFilter(r1);
            goto L_0x000c;
        L_0x001c:
            goto L_0x001e;
        L_0x001e:
            return r2;
        L_0x001f:
            r0 = new java.lang.IllegalArgumentException;
            r1 = "filters must not be null";
            r0.<init>(r1);
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.media.MediaRouteDescriptor.Builder.addControlFilters(java.util.Collection):android.support.v7.media.MediaRouteDescriptor$Builder");
        }

        @android.support.annotation.RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
        public android.support.v7.media.MediaRouteDescriptor.Builder addGroupMemberIds(java.util.Collection<java.lang.String> r3) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:12:0x0027 in {6, 7, 8, 9, 11} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r2 = this;
            if (r3 == 0) goto L_0x001f;
        L_0x0002:
            r0 = r3.isEmpty();
            if (r0 != 0) goto L_0x001d;
        L_0x0008:
            r0 = r3.iterator();
        L_0x000c:
            r1 = r0.hasNext();
            if (r1 == 0) goto L_0x001c;
        L_0x0012:
            r1 = r0.next();
            r1 = (java.lang.String) r1;
            r2.addGroupMemberId(r1);
            goto L_0x000c;
        L_0x001c:
            goto L_0x001e;
        L_0x001e:
            return r2;
        L_0x001f:
            r0 = new java.lang.IllegalArgumentException;
            r1 = "groupMemberIds must not be null";
            r0.<init>(r1);
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.media.MediaRouteDescriptor.Builder.addGroupMemberIds(java.util.Collection):android.support.v7.media.MediaRouteDescriptor$Builder");
        }

        public Builder(String id, String name) {
            this.mBundle = new Bundle();
            setId(id);
            setName(name);
        }

        public Builder(MediaRouteDescriptor descriptor) {
            if (descriptor != null) {
                this.mBundle = new Bundle(descriptor.mBundle);
                descriptor.ensureControlFilters();
                if (!descriptor.mControlFilters.isEmpty()) {
                    this.mControlFilters = new ArrayList(descriptor.mControlFilters);
                    return;
                }
                return;
            }
            throw new IllegalArgumentException("descriptor must not be null");
        }

        public Builder setId(String id) {
            this.mBundle.putString("id", id);
            return this;
        }

        @RestrictTo({Scope.LIBRARY_GROUP})
        public Builder addGroupMemberId(String groupMemberId) {
            if (TextUtils.isEmpty(groupMemberId)) {
                throw new IllegalArgumentException("groupMemberId must not be empty");
            }
            if (this.mGroupMemberIds == null) {
                this.mGroupMemberIds = new ArrayList();
            }
            if (!this.mGroupMemberIds.contains(groupMemberId)) {
                this.mGroupMemberIds.add(groupMemberId);
            }
            return this;
        }

        public Builder setName(String name) {
            this.mBundle.putString("name", name);
            return this;
        }

        public Builder setDescription(String description) {
            this.mBundle.putString("status", description);
            return this;
        }

        public Builder setIconUri(Uri iconUri) {
            if (iconUri != null) {
                this.mBundle.putString(MediaRouteDescriptor.KEY_ICON_URI, iconUri.toString());
                return this;
            }
            throw new IllegalArgumentException("iconUri must not be null");
        }

        public Builder setEnabled(boolean enabled) {
            this.mBundle.putBoolean(MediaRouteDescriptor.KEY_ENABLED, enabled);
            return this;
        }

        @Deprecated
        public Builder setConnecting(boolean connecting) {
            this.mBundle.putBoolean(MediaRouteDescriptor.KEY_CONNECTING, connecting);
            return this;
        }

        public Builder setConnectionState(int connectionState) {
            this.mBundle.putInt(MediaRouteDescriptor.KEY_CONNECTION_STATE, connectionState);
            return this;
        }

        public Builder setCanDisconnect(boolean canDisconnect) {
            this.mBundle.putBoolean(MediaRouteDescriptor.KEY_CAN_DISCONNECT, canDisconnect);
            return this;
        }

        public Builder setSettingsActivity(IntentSender is) {
            this.mBundle.putParcelable(MediaRouteDescriptor.KEY_SETTINGS_INTENT, is);
            return this;
        }

        public Builder addControlFilter(IntentFilter filter) {
            if (filter != null) {
                if (this.mControlFilters == null) {
                    this.mControlFilters = new ArrayList();
                }
                if (!this.mControlFilters.contains(filter)) {
                    this.mControlFilters.add(filter);
                }
                return this;
            }
            throw new IllegalArgumentException("filter must not be null");
        }

        public Builder setPlaybackType(int playbackType) {
            this.mBundle.putInt(MediaRouteDescriptor.KEY_PLAYBACK_TYPE, playbackType);
            return this;
        }

        public Builder setPlaybackStream(int playbackStream) {
            this.mBundle.putInt(MediaRouteDescriptor.KEY_PLAYBACK_STREAM, playbackStream);
            return this;
        }

        public Builder setDeviceType(int deviceType) {
            this.mBundle.putInt(MediaRouteDescriptor.KEY_DEVICE_TYPE, deviceType);
            return this;
        }

        public Builder setVolume(int volume) {
            this.mBundle.putInt("volume", volume);
            return this;
        }

        public Builder setVolumeMax(int volumeMax) {
            this.mBundle.putInt(MediaRouteDescriptor.KEY_VOLUME_MAX, volumeMax);
            return this;
        }

        public Builder setVolumeHandling(int volumeHandling) {
            this.mBundle.putInt(MediaRouteDescriptor.KEY_VOLUME_HANDLING, volumeHandling);
            return this;
        }

        public Builder setPresentationDisplayId(int presentationDisplayId) {
            this.mBundle.putInt(MediaRouteDescriptor.KEY_PRESENTATION_DISPLAY_ID, presentationDisplayId);
            return this;
        }

        public Builder setExtras(Bundle extras) {
            this.mBundle.putBundle(MediaRouteDescriptor.KEY_EXTRAS, extras);
            return this;
        }

        @RestrictTo({Scope.LIBRARY_GROUP})
        public Builder setMinClientVersion(int minVersion) {
            this.mBundle.putInt(MediaRouteDescriptor.KEY_MIN_CLIENT_VERSION, minVersion);
            return this;
        }

        @RestrictTo({Scope.LIBRARY_GROUP})
        public Builder setMaxClientVersion(int maxVersion) {
            this.mBundle.putInt(MediaRouteDescriptor.KEY_MAX_CLIENT_VERSION, maxVersion);
            return this;
        }

        public MediaRouteDescriptor build() {
            ArrayList arrayList = this.mControlFilters;
            if (arrayList != null) {
                this.mBundle.putParcelableArrayList(MediaRouteDescriptor.KEY_CONTROL_FILTERS, arrayList);
            }
            arrayList = this.mGroupMemberIds;
            if (arrayList != null) {
                this.mBundle.putStringArrayList(MediaRouteDescriptor.KEY_GROUP_MEMBER_IDS, arrayList);
            }
            return new MediaRouteDescriptor(this.mBundle, this.mControlFilters);
        }
    }

    MediaRouteDescriptor(Bundle bundle, List<IntentFilter> controlFilters) {
        this.mBundle = bundle;
        this.mControlFilters = controlFilters;
    }

    public String getId() {
        return this.mBundle.getString("id");
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public List<String> getGroupMemberIds() {
        return this.mBundle.getStringArrayList(KEY_GROUP_MEMBER_IDS);
    }

    public String getName() {
        return this.mBundle.getString("name");
    }

    public String getDescription() {
        return this.mBundle.getString("status");
    }

    public Uri getIconUri() {
        String iconUri = this.mBundle.getString(KEY_ICON_URI);
        return iconUri == null ? null : Uri.parse(iconUri);
    }

    public boolean isEnabled() {
        return this.mBundle.getBoolean(KEY_ENABLED, true);
    }

    @Deprecated
    public boolean isConnecting() {
        return this.mBundle.getBoolean(KEY_CONNECTING, false);
    }

    public int getConnectionState() {
        return this.mBundle.getInt(KEY_CONNECTION_STATE, 0);
    }

    public boolean canDisconnectAndKeepPlaying() {
        return this.mBundle.getBoolean(KEY_CAN_DISCONNECT, false);
    }

    public IntentSender getSettingsActivity() {
        return (IntentSender) this.mBundle.getParcelable(KEY_SETTINGS_INTENT);
    }

    public List<IntentFilter> getControlFilters() {
        ensureControlFilters();
        return this.mControlFilters;
    }

    void ensureControlFilters() {
        if (this.mControlFilters == null) {
            this.mControlFilters = this.mBundle.getParcelableArrayList(KEY_CONTROL_FILTERS);
            if (this.mControlFilters == null) {
                this.mControlFilters = Collections.emptyList();
            }
        }
    }

    public int getPlaybackType() {
        return this.mBundle.getInt(KEY_PLAYBACK_TYPE, 1);
    }

    public int getPlaybackStream() {
        return this.mBundle.getInt(KEY_PLAYBACK_STREAM, -1);
    }

    public int getDeviceType() {
        return this.mBundle.getInt(KEY_DEVICE_TYPE);
    }

    public int getVolume() {
        return this.mBundle.getInt("volume");
    }

    public int getVolumeMax() {
        return this.mBundle.getInt(KEY_VOLUME_MAX);
    }

    public int getVolumeHandling() {
        return this.mBundle.getInt(KEY_VOLUME_HANDLING, 0);
    }

    public int getPresentationDisplayId() {
        return this.mBundle.getInt(KEY_PRESENTATION_DISPLAY_ID, -1);
    }

    public Bundle getExtras() {
        return this.mBundle.getBundle(KEY_EXTRAS);
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public int getMinClientVersion() {
        return this.mBundle.getInt(KEY_MIN_CLIENT_VERSION, 1);
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public int getMaxClientVersion() {
        return this.mBundle.getInt(KEY_MAX_CLIENT_VERSION, Integer.MAX_VALUE);
    }

    public boolean isValid() {
        ensureControlFilters();
        if (!TextUtils.isEmpty(getId())) {
            if (!TextUtils.isEmpty(getName())) {
                if (!this.mControlFilters.contains(null)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("MediaRouteDescriptor{ ");
        result.append("id=");
        result.append(getId());
        result.append(", groupMemberIds=");
        result.append(getGroupMemberIds());
        result.append(", name=");
        result.append(getName());
        result.append(", description=");
        result.append(getDescription());
        result.append(", iconUri=");
        result.append(getIconUri());
        result.append(", isEnabled=");
        result.append(isEnabled());
        result.append(", isConnecting=");
        result.append(isConnecting());
        result.append(", connectionState=");
        result.append(getConnectionState());
        result.append(", controlFilters=");
        result.append(Arrays.toString(getControlFilters().toArray()));
        result.append(", playbackType=");
        result.append(getPlaybackType());
        result.append(", playbackStream=");
        result.append(getPlaybackStream());
        result.append(", deviceType=");
        result.append(getDeviceType());
        result.append(", volume=");
        result.append(getVolume());
        result.append(", volumeMax=");
        result.append(getVolumeMax());
        result.append(", volumeHandling=");
        result.append(getVolumeHandling());
        result.append(", presentationDisplayId=");
        result.append(getPresentationDisplayId());
        result.append(", extras=");
        result.append(getExtras());
        result.append(", isValid=");
        result.append(isValid());
        result.append(", minClientVersion=");
        result.append(getMinClientVersion());
        result.append(", maxClientVersion=");
        result.append(getMaxClientVersion());
        result.append(" }");
        return result.toString();
    }

    public Bundle asBundle() {
        return this.mBundle;
    }

    public static MediaRouteDescriptor fromBundle(Bundle bundle) {
        return bundle != null ? new MediaRouteDescriptor(bundle, null) : null;
    }
}
