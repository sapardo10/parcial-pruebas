package com.google.android.exoplayer2.drm;

import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Pair;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.drm.DefaultDrmSessionEventListener.-CC;
import com.google.android.exoplayer2.drm.DrmSession.DrmSessionException;
import com.google.android.exoplayer2.upstream.HttpDataSource.Factory;
import com.google.android.exoplayer2.util.Assertions;
import java.util.HashMap;
import java.util.UUID;

public final class OfflineLicenseHelper<T extends ExoMediaCrypto> {
    private final ConditionVariable conditionVariable;
    private final DefaultDrmSessionManager<T> drmSessionManager;
    private final HandlerThread handlerThread = new HandlerThread("OfflineLicenseHelper");

    /* renamed from: com.google.android.exoplayer2.drm.OfflineLicenseHelper$1 */
    class C09781 implements DefaultDrmSessionEventListener {
        public /* synthetic */ void onDrmSessionAcquired() {
            -CC.$default$onDrmSessionAcquired(this);
        }

        public /* synthetic */ void onDrmSessionReleased() {
            -CC.$default$onDrmSessionReleased(this);
        }

        C09781() {
        }

        public void onDrmKeysLoaded() {
            OfflineLicenseHelper.this.conditionVariable.open();
        }

        public void onDrmSessionManagerError(Exception e) {
            OfflineLicenseHelper.this.conditionVariable.open();
        }

        public void onDrmKeysRestored() {
            OfflineLicenseHelper.this.conditionVariable.open();
        }

        public void onDrmKeysRemoved() {
            OfflineLicenseHelper.this.conditionVariable.open();
        }
    }

    public static OfflineLicenseHelper<FrameworkMediaCrypto> newWidevineInstance(String defaultLicenseUrl, Factory httpDataSourceFactory) throws UnsupportedDrmException {
        return newWidevineInstance(defaultLicenseUrl, false, httpDataSourceFactory, null);
    }

    public static OfflineLicenseHelper<FrameworkMediaCrypto> newWidevineInstance(String defaultLicenseUrl, boolean forceDefaultLicenseUrl, Factory httpDataSourceFactory) throws UnsupportedDrmException {
        return newWidevineInstance(defaultLicenseUrl, forceDefaultLicenseUrl, httpDataSourceFactory, null);
    }

    public static OfflineLicenseHelper<FrameworkMediaCrypto> newWidevineInstance(String defaultLicenseUrl, boolean forceDefaultLicenseUrl, Factory httpDataSourceFactory, HashMap<String, String> optionalKeyRequestParameters) throws UnsupportedDrmException {
        return new OfflineLicenseHelper(C0555C.WIDEVINE_UUID, FrameworkMediaDrm.newInstance(C0555C.WIDEVINE_UUID), new HttpMediaDrmCallback(defaultLicenseUrl, forceDefaultLicenseUrl, httpDataSourceFactory), optionalKeyRequestParameters);
    }

    public OfflineLicenseHelper(UUID uuid, ExoMediaDrm<T> mediaDrm, MediaDrmCallback callback, HashMap<String, String> optionalKeyRequestParameters) {
        this.handlerThread.start();
        this.conditionVariable = new ConditionVariable();
        DefaultDrmSessionEventListener eventListener = new C09781();
        this.drmSessionManager = new DefaultDrmSessionManager(uuid, mediaDrm, callback, optionalKeyRequestParameters);
        this.drmSessionManager.addListener(new Handler(this.handlerThread.getLooper()), eventListener);
    }

    public synchronized byte[] getPropertyByteArray(String key) {
        return this.drmSessionManager.getPropertyByteArray(key);
    }

    public synchronized void setPropertyByteArray(String key, byte[] value) {
        this.drmSessionManager.setPropertyByteArray(key, value);
    }

    public synchronized String getPropertyString(String key) {
        return this.drmSessionManager.getPropertyString(key);
    }

    public synchronized void setPropertyString(String key, String value) {
        this.drmSessionManager.setPropertyString(key, value);
    }

    public synchronized byte[] downloadLicense(DrmInitData drmInitData) throws DrmSessionException {
        Assertions.checkArgument(drmInitData != null);
        return blockingKeyRequest(2, null, drmInitData);
    }

    public synchronized byte[] renewLicense(byte[] offlineLicenseKeySetId) throws DrmSessionException {
        Assertions.checkNotNull(offlineLicenseKeySetId);
        return blockingKeyRequest(2, offlineLicenseKeySetId, null);
    }

    public synchronized void releaseLicense(byte[] offlineLicenseKeySetId) throws DrmSessionException {
        Assertions.checkNotNull(offlineLicenseKeySetId);
        blockingKeyRequest(3, offlineLicenseKeySetId, null);
    }

    public synchronized Pair<Long, Long> getLicenseDurationRemainingSec(byte[] offlineLicenseKeySetId) throws DrmSessionException {
        Assertions.checkNotNull(offlineLicenseKeySetId);
        DrmSession<T> drmSession = openBlockingKeyRequest(1, offlineLicenseKeySetId, null);
        DrmSessionException error = drmSession.getError();
        Pair<Long, Long> licenseDurationRemainingSec = WidevineUtil.getLicenseDurationRemainingSec(drmSession);
        this.drmSessionManager.releaseSession(drmSession);
        if (error == null) {
            return licenseDurationRemainingSec;
        }
        if (error.getCause() instanceof KeysExpiredException) {
            return Pair.create(Long.valueOf(0), Long.valueOf(0));
        }
        throw error;
    }

    public void release() {
        this.handlerThread.quit();
    }

    private byte[] blockingKeyRequest(int licenseMode, byte[] offlineLicenseKeySetId, DrmInitData drmInitData) throws DrmSessionException {
        DrmSession<T> drmSession = openBlockingKeyRequest(licenseMode, offlineLicenseKeySetId, drmInitData);
        DrmSessionException error = drmSession.getError();
        byte[] keySetId = drmSession.getOfflineLicenseKeySetId();
        this.drmSessionManager.releaseSession(drmSession);
        if (error == null) {
            return keySetId;
        }
        throw error;
    }

    private DrmSession<T> openBlockingKeyRequest(int licenseMode, byte[] offlineLicenseKeySetId, DrmInitData drmInitData) {
        this.drmSessionManager.setMode(licenseMode, offlineLicenseKeySetId);
        this.conditionVariable.close();
        DrmSession<T> drmSession = this.drmSessionManager.acquireSession(this.handlerThread.getLooper(), drmInitData);
        this.conditionVariable.block();
        return drmSession;
    }
}
