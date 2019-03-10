package com.google.android.exoplayer2.drm;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.drm.DefaultDrmSession.ProvisioningManager;
import com.google.android.exoplayer2.drm.DrmInitData.SchemeData;
import com.google.android.exoplayer2.drm.DrmSession.DrmSessionException;
import com.google.android.exoplayer2.drm.ExoMediaDrm.OnEventListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.EventDispatcher;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@TargetApi(18)
public class DefaultDrmSessionManager<T extends ExoMediaCrypto> implements DrmSessionManager<T>, ProvisioningManager<T> {
    public static final int INITIAL_DRM_REQUEST_RETRY_COUNT = 3;
    public static final int MODE_DOWNLOAD = 2;
    public static final int MODE_PLAYBACK = 0;
    public static final int MODE_QUERY = 1;
    public static final int MODE_RELEASE = 3;
    public static final String PLAYREADY_CUSTOM_DATA_KEY = "PRCustomData";
    private static final String TAG = "DefaultDrmSessionMgr";
    private final MediaDrmCallback callback;
    private final EventDispatcher<DefaultDrmSessionEventListener> eventDispatcher;
    private final int initialDrmRequestRetryCount;
    private final ExoMediaDrm<T> mediaDrm;
    volatile MediaDrmHandler mediaDrmHandler;
    private int mode;
    private final boolean multiSession;
    private byte[] offlineLicenseKeySetId;
    private final HashMap<String, String> optionalKeyRequestParameters;
    private Looper playbackLooper;
    private final List<DefaultDrmSession<T>> provisioningSessions;
    private final List<DefaultDrmSession<T>> sessions;
    private final UUID uuid;

    @SuppressLint({"HandlerLeak"})
    private class MediaDrmHandler extends Handler {
        public MediaDrmHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            byte[] sessionId = msg.obj;
            for (DefaultDrmSession<T> session : DefaultDrmSessionManager.this.sessions) {
                if (session.hasSessionId(sessionId)) {
                    session.onMediaDrmEvent(msg.what);
                    return;
                }
            }
        }
    }

    public static final class MissingSchemeDataException extends Exception {
        private MissingSchemeDataException(UUID uuid) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Media does not support uuid: ");
            stringBuilder.append(uuid);
            super(stringBuilder.toString());
        }
    }

    @Deprecated
    public interface EventListener extends DefaultDrmSessionEventListener {
    }

    private class MediaDrmEventListener implements OnEventListener<T> {
        private MediaDrmEventListener() {
        }

        public void onEvent(ExoMediaDrm<? extends T> exoMediaDrm, byte[] sessionId, int event, int extra, byte[] data) {
            if (DefaultDrmSessionManager.this.mode == 0) {
                DefaultDrmSessionManager.this.mediaDrmHandler.obtainMessage(event, sessionId).sendToTarget();
            }
        }
    }

    @Deprecated
    public static DefaultDrmSessionManager<FrameworkMediaCrypto> newWidevineInstance(MediaDrmCallback callback, HashMap<String, String> optionalKeyRequestParameters, Handler eventHandler, DefaultDrmSessionEventListener eventListener) throws UnsupportedDrmException {
        DefaultDrmSessionManager<FrameworkMediaCrypto> drmSessionManager = newWidevineInstance(callback, optionalKeyRequestParameters);
        if (eventHandler != null && eventListener != null) {
            drmSessionManager.addListener(eventHandler, eventListener);
        }
        return drmSessionManager;
    }

    public static DefaultDrmSessionManager<FrameworkMediaCrypto> newWidevineInstance(MediaDrmCallback callback, HashMap<String, String> optionalKeyRequestParameters) throws UnsupportedDrmException {
        return newFrameworkInstance(C0555C.WIDEVINE_UUID, callback, optionalKeyRequestParameters);
    }

    @Deprecated
    public static DefaultDrmSessionManager<FrameworkMediaCrypto> newPlayReadyInstance(MediaDrmCallback callback, String customData, Handler eventHandler, DefaultDrmSessionEventListener eventListener) throws UnsupportedDrmException {
        DefaultDrmSessionManager<FrameworkMediaCrypto> drmSessionManager = newPlayReadyInstance(callback, customData);
        if (eventHandler != null && eventListener != null) {
            drmSessionManager.addListener(eventHandler, eventListener);
        }
        return drmSessionManager;
    }

    public static DefaultDrmSessionManager<FrameworkMediaCrypto> newPlayReadyInstance(MediaDrmCallback callback, String customData) throws UnsupportedDrmException {
        HashMap<String, String> optionalKeyRequestParameters;
        if (TextUtils.isEmpty(customData)) {
            optionalKeyRequestParameters = null;
        } else {
            optionalKeyRequestParameters = new HashMap();
            optionalKeyRequestParameters.put(PLAYREADY_CUSTOM_DATA_KEY, customData);
        }
        return newFrameworkInstance(C0555C.PLAYREADY_UUID, callback, optionalKeyRequestParameters);
    }

    @Deprecated
    public static DefaultDrmSessionManager<FrameworkMediaCrypto> newFrameworkInstance(UUID uuid, MediaDrmCallback callback, HashMap<String, String> optionalKeyRequestParameters, Handler eventHandler, DefaultDrmSessionEventListener eventListener) throws UnsupportedDrmException {
        DefaultDrmSessionManager<FrameworkMediaCrypto> drmSessionManager = newFrameworkInstance(uuid, callback, optionalKeyRequestParameters);
        if (eventHandler != null && eventListener != null) {
            drmSessionManager.addListener(eventHandler, eventListener);
        }
        return drmSessionManager;
    }

    public static DefaultDrmSessionManager<FrameworkMediaCrypto> newFrameworkInstance(UUID uuid, MediaDrmCallback callback, HashMap<String, String> optionalKeyRequestParameters) throws UnsupportedDrmException {
        return new DefaultDrmSessionManager(uuid, FrameworkMediaDrm.newInstance(uuid), callback, (HashMap) optionalKeyRequestParameters, false, 3);
    }

    @Deprecated
    public DefaultDrmSessionManager(UUID uuid, ExoMediaDrm<T> mediaDrm, MediaDrmCallback callback, HashMap<String, String> optionalKeyRequestParameters, Handler eventHandler, DefaultDrmSessionEventListener eventListener) {
        this(uuid, mediaDrm, callback, optionalKeyRequestParameters);
        if (eventHandler != null && eventListener != null) {
            addListener(eventHandler, eventListener);
        }
    }

    public DefaultDrmSessionManager(UUID uuid, ExoMediaDrm<T> mediaDrm, MediaDrmCallback callback, HashMap<String, String> optionalKeyRequestParameters) {
        this(uuid, (ExoMediaDrm) mediaDrm, callback, (HashMap) optionalKeyRequestParameters, false, 3);
    }

    @Deprecated
    public DefaultDrmSessionManager(UUID uuid, ExoMediaDrm<T> mediaDrm, MediaDrmCallback callback, HashMap<String, String> optionalKeyRequestParameters, Handler eventHandler, DefaultDrmSessionEventListener eventListener, boolean multiSession) {
        this(uuid, mediaDrm, callback, optionalKeyRequestParameters, multiSession);
        if (eventHandler != null && eventListener != null) {
            addListener(eventHandler, eventListener);
        }
    }

    public DefaultDrmSessionManager(UUID uuid, ExoMediaDrm<T> mediaDrm, MediaDrmCallback callback, HashMap<String, String> optionalKeyRequestParameters, boolean multiSession) {
        this(uuid, (ExoMediaDrm) mediaDrm, callback, (HashMap) optionalKeyRequestParameters, multiSession, 3);
    }

    @Deprecated
    public DefaultDrmSessionManager(UUID uuid, ExoMediaDrm<T> mediaDrm, MediaDrmCallback callback, HashMap<String, String> optionalKeyRequestParameters, Handler eventHandler, DefaultDrmSessionEventListener eventListener, boolean multiSession, int initialDrmRequestRetryCount) {
        this(uuid, (ExoMediaDrm) mediaDrm, callback, (HashMap) optionalKeyRequestParameters, multiSession, initialDrmRequestRetryCount);
        if (eventHandler != null && eventListener != null) {
            addListener(eventHandler, eventListener);
        }
    }

    public DefaultDrmSessionManager(UUID uuid, ExoMediaDrm<T> mediaDrm, MediaDrmCallback callback, HashMap<String, String> optionalKeyRequestParameters, boolean multiSession, int initialDrmRequestRetryCount) {
        Assertions.checkNotNull(uuid);
        Assertions.checkNotNull(mediaDrm);
        Assertions.checkArgument(C0555C.COMMON_PSSH_UUID.equals(uuid) ^ 1, "Use C.CLEARKEY_UUID instead");
        this.uuid = uuid;
        this.mediaDrm = mediaDrm;
        this.callback = callback;
        this.optionalKeyRequestParameters = optionalKeyRequestParameters;
        this.eventDispatcher = new EventDispatcher();
        this.multiSession = multiSession;
        this.initialDrmRequestRetryCount = initialDrmRequestRetryCount;
        this.mode = 0;
        this.sessions = new ArrayList();
        this.provisioningSessions = new ArrayList();
        if (multiSession && C0555C.WIDEVINE_UUID.equals(uuid) && Util.SDK_INT >= 19) {
            mediaDrm.setPropertyString("sessionSharing", "enable");
        }
        mediaDrm.setOnEventListener(new MediaDrmEventListener());
    }

    public final void addListener(Handler handler, DefaultDrmSessionEventListener eventListener) {
        this.eventDispatcher.addListener(handler, eventListener);
    }

    public final void removeListener(DefaultDrmSessionEventListener eventListener) {
        this.eventDispatcher.removeListener(eventListener);
    }

    public final String getPropertyString(String key) {
        return this.mediaDrm.getPropertyString(key);
    }

    public final void setPropertyString(String key, String value) {
        this.mediaDrm.setPropertyString(key, value);
    }

    public final byte[] getPropertyByteArray(String key) {
        return this.mediaDrm.getPropertyByteArray(key);
    }

    public final void setPropertyByteArray(String key, byte[] value) {
        this.mediaDrm.setPropertyByteArray(key, value);
    }

    public void setMode(int mode, byte[] offlineLicenseKeySetId) {
        Assertions.checkState(this.sessions.isEmpty());
        if (mode != 1) {
            if (mode != 3) {
                this.mode = mode;
                this.offlineLicenseKeySetId = offlineLicenseKeySetId;
            }
        }
        Assertions.checkNotNull(offlineLicenseKeySetId);
        this.mode = mode;
        this.offlineLicenseKeySetId = offlineLicenseKeySetId;
    }

    public boolean canAcquireSession(@NonNull DrmInitData drmInitData) {
        boolean z = true;
        if (this.offlineLicenseKeySetId != null) {
            return true;
        }
        String str;
        if (getSchemeDatas(drmInitData, this.uuid, true).isEmpty()) {
            if (drmInitData.schemeDataCount != 1 || !drmInitData.get(0).matches(C0555C.COMMON_PSSH_UUID)) {
                return false;
            }
            str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DrmInitData only contains common PSSH SchemeData. Assuming support for: ");
            stringBuilder.append(this.uuid);
            Log.m10w(str, stringBuilder.toString());
        }
        str = drmInitData.schemeType;
        if (str != null) {
            if (!C0555C.CENC_TYPE_cenc.equals(str)) {
                if (!C0555C.CENC_TYPE_cbc1.equals(str) && !C0555C.CENC_TYPE_cbcs.equals(str)) {
                    if (!C0555C.CENC_TYPE_cens.equals(str)) {
                        return true;
                    }
                }
                if (Util.SDK_INT < 25) {
                    z = false;
                }
                return z;
            }
        }
        return true;
    }

    public DrmSession<T> acquireSession(Looper playbackLooper, DrmInitData drmInitData) {
        boolean z;
        C05781 c05781;
        List<SchemeData> schemeDatas;
        List<SchemeData> schemeDatas2;
        DefaultDrmSession<T> defaultDrmSession;
        Looper looper = playbackLooper;
        Looper looper2 = this.playbackLooper;
        if (looper2 != null) {
            if (looper2 != looper) {
                z = false;
                Assertions.checkState(z);
                if (r12.sessions.isEmpty()) {
                    r12.playbackLooper = looper;
                    if (r12.mediaDrmHandler == null) {
                        r12.mediaDrmHandler = new MediaDrmHandler(looper);
                    }
                }
                c05781 = null;
                if (r12.offlineLicenseKeySetId != null) {
                    schemeDatas = getSchemeDatas(drmInitData, r12.uuid, false);
                    if (schemeDatas.isEmpty()) {
                        schemeDatas2 = schemeDatas;
                    } else {
                        MissingSchemeDataException error = new MissingSchemeDataException(r12.uuid);
                        r12.eventDispatcher.dispatch(new -$$Lambda$DefaultDrmSessionManager$lsU4S5fVqixyNsHyDBIvI3jEzVc(error));
                        return new ErrorStateDrmSession(new DrmSessionException(error));
                    }
                }
                DrmInitData drmInitData2 = drmInitData;
                schemeDatas2 = null;
                if (r12.multiSession) {
                    if (r12.sessions.isEmpty()) {
                        c05781 = (DefaultDrmSession) r12.sessions.get(0);
                    }
                    defaultDrmSession = c05781;
                } else {
                    for (DefaultDrmSession<T> existingSession : r12.sessions) {
                        if (Util.areEqual(existingSession.schemeDatas, schemeDatas2)) {
                            defaultDrmSession = existingSession;
                            break;
                        }
                    }
                    defaultDrmSession = null;
                }
                if (defaultDrmSession == null) {
                    DefaultDrmSession<T> session = new DefaultDrmSession(r12.uuid, r12.mediaDrm, this, schemeDatas2, r12.mode, r12.offlineLicenseKeySetId, r12.optionalKeyRequestParameters, r12.callback, playbackLooper, r12.eventDispatcher, r12.initialDrmRequestRetryCount);
                    r12.sessions.add(session);
                    defaultDrmSession = session;
                }
                defaultDrmSession.acquire();
                return defaultDrmSession;
            }
        }
        z = true;
        Assertions.checkState(z);
        if (r12.sessions.isEmpty()) {
            r12.playbackLooper = looper;
            if (r12.mediaDrmHandler == null) {
                r12.mediaDrmHandler = new MediaDrmHandler(looper);
            }
        }
        c05781 = null;
        if (r12.offlineLicenseKeySetId != null) {
            DrmInitData drmInitData22 = drmInitData;
            schemeDatas2 = null;
        } else {
            schemeDatas = getSchemeDatas(drmInitData, r12.uuid, false);
            if (schemeDatas.isEmpty()) {
                schemeDatas2 = schemeDatas;
            } else {
                MissingSchemeDataException error2 = new MissingSchemeDataException(r12.uuid);
                r12.eventDispatcher.dispatch(new -$$Lambda$DefaultDrmSessionManager$lsU4S5fVqixyNsHyDBIvI3jEzVc(error2));
                return new ErrorStateDrmSession(new DrmSessionException(error2));
            }
        }
        if (r12.multiSession) {
            for (DefaultDrmSession<T> existingSession2 : r12.sessions) {
                if (Util.areEqual(existingSession2.schemeDatas, schemeDatas2)) {
                    defaultDrmSession = existingSession2;
                    break;
                }
            }
            defaultDrmSession = null;
        } else {
            if (r12.sessions.isEmpty()) {
                c05781 = (DefaultDrmSession) r12.sessions.get(0);
            }
            defaultDrmSession = c05781;
        }
        if (defaultDrmSession == null) {
            DefaultDrmSession<T> session2 = new DefaultDrmSession(r12.uuid, r12.mediaDrm, this, schemeDatas2, r12.mode, r12.offlineLicenseKeySetId, r12.optionalKeyRequestParameters, r12.callback, playbackLooper, r12.eventDispatcher, r12.initialDrmRequestRetryCount);
            r12.sessions.add(session2);
            defaultDrmSession = session2;
        }
        defaultDrmSession.acquire();
        return defaultDrmSession;
    }

    public void releaseSession(DrmSession<T> session) {
        if (!(session instanceof ErrorStateDrmSession)) {
            DefaultDrmSession<T> drmSession = (DefaultDrmSession) session;
            if (drmSession.release()) {
                this.sessions.remove(drmSession);
                if (this.provisioningSessions.size() > 1 && this.provisioningSessions.get(0) == drmSession) {
                    ((DefaultDrmSession) this.provisioningSessions.get(1)).provision();
                }
                this.provisioningSessions.remove(drmSession);
            }
        }
    }

    public void provisionRequired(DefaultDrmSession<T> session) {
        this.provisioningSessions.add(session);
        if (this.provisioningSessions.size() == 1) {
            session.provision();
        }
    }

    public void onProvisionCompleted() {
        for (DefaultDrmSession<T> session : this.provisioningSessions) {
            session.onProvisionCompleted();
        }
        this.provisioningSessions.clear();
    }

    public void onProvisionError(Exception error) {
        for (DefaultDrmSession<T> session : this.provisioningSessions) {
            session.onProvisionError(error);
        }
        this.provisioningSessions.clear();
    }

    private static List<SchemeData> getSchemeDatas(DrmInitData drmInitData, UUID uuid, boolean allowMissingData) {
        List<SchemeData> matchingSchemeDatas = new ArrayList(drmInitData.schemeDataCount);
        for (int i = 0; i < drmInitData.schemeDataCount; i++) {
            boolean uuidMatches;
            SchemeData schemeData = drmInitData.get(i);
            if (!schemeData.matches(uuid)) {
                if (!C0555C.CLEARKEY_UUID.equals(uuid) || !schemeData.matches(C0555C.COMMON_PSSH_UUID)) {
                    uuidMatches = false;
                    if (!uuidMatches && (schemeData.data != null || allowMissingData)) {
                        matchingSchemeDatas.add(schemeData);
                    }
                }
            }
            uuidMatches = true;
            if (!uuidMatches) {
            }
        }
        return matchingSchemeDatas;
    }
}
