package com.google.android.exoplayer2.drm;

public interface DefaultDrmSessionEventListener {

    public final /* synthetic */ class -CC {
        public static void $default$onDrmSessionAcquired(DefaultDrmSessionEventListener -this) {
        }

        public static void $default$onDrmSessionReleased(DefaultDrmSessionEventListener -this) {
        }
    }

    void onDrmKeysLoaded();

    void onDrmKeysRemoved();

    void onDrmKeysRestored();

    void onDrmSessionAcquired();

    void onDrmSessionManagerError(Exception exception);

    void onDrmSessionReleased();
}
