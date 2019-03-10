package com.google.android.exoplayer2.drm;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.DeniedByServerException;
import android.media.MediaCrypto;
import android.media.MediaCryptoException;
import android.media.MediaDrm;
import android.media.MediaDrmException;
import android.media.NotProvisionedException;
import android.media.UnsupportedSchemeException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.drm.DrmInitData.SchemeData;
import com.google.android.exoplayer2.drm.ExoMediaDrm.KeyRequest;
import com.google.android.exoplayer2.drm.ExoMediaDrm.KeyStatus;
import com.google.android.exoplayer2.drm.ExoMediaDrm.OnEventListener;
import com.google.android.exoplayer2.drm.ExoMediaDrm.OnKeyStatusChangeListener;
import com.google.android.exoplayer2.drm.ExoMediaDrm.ProvisionRequest;
import com.google.android.exoplayer2.extractor.mp4.PsshAtomUtil;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@TargetApi(23)
public final class FrameworkMediaDrm implements ExoMediaDrm<FrameworkMediaCrypto> {
    private static final String CENC_SCHEME_MIME_TYPE = "cenc";
    private final MediaDrm mediaDrm;
    private final UUID uuid;

    public static FrameworkMediaDrm newInstance(UUID uuid) throws UnsupportedDrmException {
        try {
            return new FrameworkMediaDrm(uuid);
        } catch (UnsupportedSchemeException e) {
            throw new UnsupportedDrmException(1, e);
        } catch (Exception e2) {
            throw new UnsupportedDrmException(2, e2);
        }
    }

    private FrameworkMediaDrm(UUID uuid) throws UnsupportedSchemeException {
        Assertions.checkNotNull(uuid);
        Assertions.checkArgument(C0555C.COMMON_PSSH_UUID.equals(uuid) ^ 1, "Use C.CLEARKEY_UUID instead");
        this.uuid = uuid;
        this.mediaDrm = new MediaDrm(adjustUuid(uuid));
        if (C0555C.WIDEVINE_UUID.equals(uuid) && needsForceWidevineL3Workaround()) {
            forceWidevineL3(this.mediaDrm);
        }
    }

    public void setOnEventListener(OnEventListener<? super FrameworkMediaCrypto> listener) {
        this.mediaDrm.setOnEventListener(listener == null ? null : new -$$Lambda$FrameworkMediaDrm$zJ3h9UKP9ayPF2iQATh7r7bKJes(this, listener));
    }

    public void setOnKeyStatusChangeListener(OnKeyStatusChangeListener<? super FrameworkMediaCrypto> listener) {
        if (Util.SDK_INT >= 23) {
            this.mediaDrm.setOnKeyStatusChangeListener(listener == null ? null : new -$$Lambda$FrameworkMediaDrm$WcqXRf-ZlBuRYiaqpRgpL0-wRvg(this, listener), null);
            return;
        }
        throw new UnsupportedOperationException();
    }

    public static /* synthetic */ void lambda$setOnKeyStatusChangeListener$1(FrameworkMediaDrm frameworkMediaDrm, OnKeyStatusChangeListener listener, MediaDrm mediaDrm, byte[] sessionId, List keyInfo, boolean hasNewUsableKey) {
        List<KeyStatus> exoKeyInfo = new ArrayList();
        for (MediaDrm.KeyStatus keyStatus : keyInfo) {
            exoKeyInfo.add(new KeyStatus(keyStatus.getStatusCode(), keyStatus.getKeyId()));
        }
        listener.onKeyStatusChange(frameworkMediaDrm, sessionId, exoKeyInfo, hasNewUsableKey);
    }

    public byte[] openSession() throws MediaDrmException {
        return this.mediaDrm.openSession();
    }

    public void closeSession(byte[] sessionId) {
        this.mediaDrm.closeSession(sessionId);
    }

    public KeyRequest getKeyRequest(byte[] scope, @Nullable List<SchemeData> schemeDatas, int keyType, @Nullable HashMap<String, String> optionalParameters) throws NotProvisionedException {
        SchemeData schemeData = null;
        byte[] initData = null;
        String mimeType = null;
        if (schemeDatas != null) {
            schemeData = getSchemeData(this.uuid, schemeDatas);
            initData = adjustRequestInitData(this.uuid, schemeData.data);
            mimeType = adjustRequestMimeType(this.uuid, schemeData.mimeType);
        }
        MediaDrm.KeyRequest request = this.mediaDrm.getKeyRequest(scope, initData, mimeType, keyType, optionalParameters);
        byte[] requestData = adjustRequestData(this.uuid, request.getData());
        String licenseServerUrl = request.getDefaultUrl();
        if (TextUtils.isEmpty(licenseServerUrl) && schemeData != null) {
            if (!TextUtils.isEmpty(schemeData.licenseServerUrl)) {
                licenseServerUrl = schemeData.licenseServerUrl;
            }
        }
        return new KeyRequest(requestData, licenseServerUrl);
    }

    public byte[] provideKeyResponse(byte[] scope, byte[] response) throws NotProvisionedException, DeniedByServerException {
        if (C0555C.CLEARKEY_UUID.equals(this.uuid)) {
            response = ClearKeyUtil.adjustResponseData(response);
        }
        return this.mediaDrm.provideKeyResponse(scope, response);
    }

    public ProvisionRequest getProvisionRequest() {
        MediaDrm.ProvisionRequest request = this.mediaDrm.getProvisionRequest();
        return new ProvisionRequest(request.getData(), request.getDefaultUrl());
    }

    public void provideProvisionResponse(byte[] response) throws DeniedByServerException {
        this.mediaDrm.provideProvisionResponse(response);
    }

    public Map<String, String> queryKeyStatus(byte[] sessionId) {
        return this.mediaDrm.queryKeyStatus(sessionId);
    }

    public void release() {
        this.mediaDrm.release();
    }

    public void restoreKeys(byte[] sessionId, byte[] keySetId) {
        this.mediaDrm.restoreKeys(sessionId, keySetId);
    }

    public String getPropertyString(String propertyName) {
        return this.mediaDrm.getPropertyString(propertyName);
    }

    public byte[] getPropertyByteArray(String propertyName) {
        return this.mediaDrm.getPropertyByteArray(propertyName);
    }

    public void setPropertyString(String propertyName, String value) {
        this.mediaDrm.setPropertyString(propertyName, value);
    }

    public void setPropertyByteArray(String propertyName, byte[] value) {
        this.mediaDrm.setPropertyByteArray(propertyName, value);
    }

    public FrameworkMediaCrypto createMediaCrypto(byte[] initData) throws MediaCryptoException {
        boolean forceAllowInsecureDecoderComponents;
        if (Util.SDK_INT < 21) {
            if (C0555C.WIDEVINE_UUID.equals(this.uuid) && "L3".equals(getPropertyString("securityLevel"))) {
                forceAllowInsecureDecoderComponents = true;
                return new FrameworkMediaCrypto(new MediaCrypto(adjustUuid(this.uuid), initData), forceAllowInsecureDecoderComponents);
            }
        }
        forceAllowInsecureDecoderComponents = false;
        return new FrameworkMediaCrypto(new MediaCrypto(adjustUuid(this.uuid), initData), forceAllowInsecureDecoderComponents);
    }

    private static SchemeData getSchemeData(UUID uuid, List<SchemeData> schemeDatas) {
        if (!C0555C.WIDEVINE_UUID.equals(uuid)) {
            return (SchemeData) schemeDatas.get(0);
        }
        if (Util.SDK_INT >= 28 && schemeDatas.size() > 1) {
            SchemeData firstSchemeData = (SchemeData) schemeDatas.get(0);
            int concatenatedDataLength = 0;
            boolean canConcatenateData = true;
            int i = 0;
            while (i < schemeDatas.size()) {
                SchemeData schemeData = (SchemeData) schemeDatas.get(i);
                if (schemeData.requiresSecureDecryption == firstSchemeData.requiresSecureDecryption) {
                    if (Util.areEqual(schemeData.mimeType, firstSchemeData.mimeType)) {
                        if (Util.areEqual(schemeData.licenseServerUrl, firstSchemeData.licenseServerUrl)) {
                            if (PsshAtomUtil.isPsshAtom(schemeData.data)) {
                                concatenatedDataLength += schemeData.data.length;
                                i++;
                            }
                        }
                    }
                }
                canConcatenateData = false;
            }
            if (canConcatenateData) {
                byte[] concatenatedData = new byte[concatenatedDataLength];
                i = 0;
                for (int i2 = 0; i2 < schemeDatas.size(); i2++) {
                    SchemeData schemeData2 = (SchemeData) schemeDatas.get(i2);
                    int schemeDataLength = schemeData2.data.length;
                    System.arraycopy(schemeData2.data, 0, concatenatedData, i, schemeDataLength);
                    i += schemeDataLength;
                }
                return firstSchemeData.copyWithData(concatenatedData);
            }
        }
        for (int i3 = 0; i3 < schemeDatas.size(); i3++) {
            SchemeData schemeData3 = (SchemeData) schemeDatas.get(i3);
            int version = PsshAtomUtil.parseVersion(schemeData3.data);
            if (Util.SDK_INT < 23 && version == 0) {
                return schemeData3;
            }
            if (Util.SDK_INT >= 23 && version == 1) {
                return schemeData3;
            }
        }
        return (SchemeData) schemeDatas.get(0);
    }

    private static UUID adjustUuid(UUID uuid) {
        return (Util.SDK_INT >= 27 || !C0555C.CLEARKEY_UUID.equals(uuid)) ? uuid : C0555C.COMMON_PSSH_UUID;
    }

    private static byte[] adjustRequestInitData(UUID uuid, byte[] initData) {
        byte[] psshData;
        if (Util.SDK_INT < 21) {
            if (C0555C.WIDEVINE_UUID.equals(uuid)) {
                psshData = PsshAtomUtil.parseSchemeSpecificData(initData, uuid);
                if (psshData != null) {
                    return psshData;
                }
                return initData;
            }
        }
        if (C0555C.PLAYREADY_UUID.equals(uuid)) {
            if ("Amazon".equals(Util.MANUFACTURER)) {
                if (!"AFTB".equals(Util.MODEL)) {
                    if (!"AFTS".equals(Util.MODEL)) {
                        if ("AFTM".equals(Util.MODEL)) {
                        }
                    }
                }
                psshData = PsshAtomUtil.parseSchemeSpecificData(initData, uuid);
                if (psshData != null) {
                    return psshData;
                }
            }
        }
        return initData;
    }

    private static String adjustRequestMimeType(UUID uuid, String mimeType) {
        if (Util.SDK_INT < 26) {
            if (C0555C.CLEARKEY_UUID.equals(uuid)) {
                if (!MimeTypes.VIDEO_MP4.equals(mimeType)) {
                    if (MimeTypes.AUDIO_MP4.equals(mimeType)) {
                    }
                }
                return "cenc";
            }
        }
        return mimeType;
    }

    private static byte[] adjustRequestData(UUID uuid, byte[] requestData) {
        if (C0555C.CLEARKEY_UUID.equals(uuid)) {
            return ClearKeyUtil.adjustRequestData(requestData);
        }
        return requestData;
    }

    @SuppressLint({"WrongConstant"})
    private static void forceWidevineL3(MediaDrm mediaDrm) {
        mediaDrm.setPropertyString("securityLevel", "L3");
    }

    private static boolean needsForceWidevineL3Workaround() {
        return "ASUS_Z00AD".equals(Util.MODEL);
    }
}
