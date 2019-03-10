package com.google.android.exoplayer2.drm;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.NotProvisionedException;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.drm.DrmInitData.SchemeData;
import com.google.android.exoplayer2.drm.DrmSession.DrmSessionException;
import com.google.android.exoplayer2.drm.ExoMediaDrm.KeyRequest;
import com.google.android.exoplayer2.drm.ExoMediaDrm.ProvisionRequest;
import com.google.android.exoplayer2.util.EventDispatcher;
import com.google.android.exoplayer2.util.Log;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@TargetApi(18)
class DefaultDrmSession<T extends ExoMediaCrypto> implements DrmSession<T> {
    private static final int MAX_LICENSE_DURATION_TO_RENEW = 60;
    private static final int MSG_KEYS = 1;
    private static final int MSG_PROVISION = 0;
    private static final String TAG = "DefaultDrmSession";
    final MediaDrmCallback callback;
    private KeyRequest currentKeyRequest;
    private ProvisionRequest currentProvisionRequest;
    private final EventDispatcher<DefaultDrmSessionEventListener> eventDispatcher;
    private final int initialDrmRequestRetryCount;
    private DrmSessionException lastException;
    private T mediaCrypto;
    private final ExoMediaDrm<T> mediaDrm;
    private final int mode;
    @Nullable
    private byte[] offlineLicenseKeySetId;
    private int openCount;
    private final HashMap<String, String> optionalKeyRequestParameters;
    private PostRequestHandler postRequestHandler;
    final PostResponseHandler postResponseHandler;
    private final ProvisioningManager<T> provisioningManager;
    private HandlerThread requestHandlerThread;
    @Nullable
    public final List<SchemeData> schemeDatas;
    private byte[] sessionId;
    private int state;
    final UUID uuid;

    @SuppressLint({"HandlerLeak"})
    private class PostRequestHandler extends Handler {
        public PostRequestHandler(Looper backgroundLooper) {
            super(backgroundLooper);
        }

        void post(int what, Object request, boolean allowRetry) {
            obtainMessage(what, allowRetry, 0, request).sendToTarget();
        }

        public void handleMessage(Message msg) {
            Object executeProvisionRequest;
            Object request = msg.obj;
            try {
                switch (msg.what) {
                    case 0:
                        executeProvisionRequest = DefaultDrmSession.this.callback.executeProvisionRequest(DefaultDrmSession.this.uuid, (ProvisionRequest) request);
                        break;
                    case 1:
                        executeProvisionRequest = DefaultDrmSession.this.callback.executeKeyRequest(DefaultDrmSession.this.uuid, (KeyRequest) request);
                        break;
                    default:
                        throw new RuntimeException();
                }
            } catch (Exception e) {
                executeProvisionRequest = e;
                if (maybeRetryRequest(msg)) {
                    return;
                }
            }
            DefaultDrmSession.this.postResponseHandler.obtainMessage(msg.what, Pair.create(request, executeProvisionRequest)).sendToTarget();
        }

        private boolean maybeRetryRequest(Message originalMsg) {
            if (!(originalMsg.arg1 == 1)) {
                return false;
            }
            int errorCount = originalMsg.arg2 + 1;
            if (errorCount > DefaultDrmSession.this.initialDrmRequestRetryCount) {
                return false;
            }
            Message retryMsg = Message.obtain(originalMsg);
            retryMsg.arg2 = errorCount;
            sendMessageDelayed(retryMsg, getRetryDelayMillis(errorCount));
            return true;
        }

        private long getRetryDelayMillis(int errorCount) {
            return (long) Math.min((errorCount - 1) * 1000, 5000);
        }
    }

    @SuppressLint({"HandlerLeak"})
    private class PostResponseHandler extends Handler {
        public PostResponseHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            Pair<?, ?> requestAndResponse = msg.obj;
            Object request = requestAndResponse.first;
            Object response = requestAndResponse.second;
            switch (msg.what) {
                case 0:
                    DefaultDrmSession.this.onProvisionResponse(request, response);
                    return;
                case 1:
                    DefaultDrmSession.this.onKeyResponse(request, response);
                    return;
                default:
                    return;
            }
        }
    }

    public interface ProvisioningManager<T extends ExoMediaCrypto> {
        void onProvisionCompleted();

        void onProvisionError(Exception exception);

        void provisionRequired(DefaultDrmSession<T> defaultDrmSession);
    }

    public DefaultDrmSession(UUID uuid, ExoMediaDrm<T> mediaDrm, ProvisioningManager<T> provisioningManager, @Nullable List<SchemeData> schemeDatas, int mode, @Nullable byte[] offlineLicenseKeySetId, HashMap<String, String> optionalKeyRequestParameters, MediaDrmCallback callback, Looper playbackLooper, EventDispatcher<DefaultDrmSessionEventListener> eventDispatcher, int initialDrmRequestRetryCount) {
        this.uuid = uuid;
        this.provisioningManager = provisioningManager;
        this.mediaDrm = mediaDrm;
        this.mode = mode;
        this.offlineLicenseKeySetId = offlineLicenseKeySetId;
        this.schemeDatas = offlineLicenseKeySetId == null ? Collections.unmodifiableList(schemeDatas) : null;
        this.optionalKeyRequestParameters = optionalKeyRequestParameters;
        this.callback = callback;
        this.initialDrmRequestRetryCount = initialDrmRequestRetryCount;
        this.eventDispatcher = eventDispatcher;
        this.state = 2;
        this.postResponseHandler = new PostResponseHandler(playbackLooper);
        this.requestHandlerThread = new HandlerThread("DrmRequestHandler");
        this.requestHandlerThread.start();
        this.postRequestHandler = new PostRequestHandler(this.requestHandlerThread.getLooper());
    }

    public void acquire() {
        int i = this.openCount + 1;
        this.openCount = i;
        if (i == 1) {
            if (this.state != 1) {
                if (openInternal(true)) {
                    doLicense(true);
                }
            }
        }
    }

    public boolean release() {
        int i = this.openCount - 1;
        this.openCount = i;
        if (i != 0) {
            return false;
        }
        this.state = 0;
        this.postResponseHandler.removeCallbacksAndMessages(null);
        this.postRequestHandler.removeCallbacksAndMessages(null);
        this.postRequestHandler = null;
        this.requestHandlerThread.quit();
        this.requestHandlerThread = null;
        this.mediaCrypto = null;
        this.lastException = null;
        this.currentKeyRequest = null;
        this.currentProvisionRequest = null;
        byte[] bArr = this.sessionId;
        if (bArr != null) {
            this.mediaDrm.closeSession(bArr);
            this.sessionId = null;
            this.eventDispatcher.dispatch(-$$Lambda$1U2yJBSMBm8ESUSz9LUzNXtoVus.INSTANCE);
        }
        return true;
    }

    public boolean hasSessionId(byte[] sessionId) {
        return Arrays.equals(this.sessionId, sessionId);
    }

    public void onMediaDrmEvent(int what) {
        if (isOpen()) {
            switch (what) {
                case 1:
                    this.state = 3;
                    this.provisioningManager.provisionRequired(this);
                    break;
                case 2:
                    doLicense(false);
                    break;
                case 3:
                    onKeysExpired();
                    break;
                default:
                    break;
            }
        }
    }

    public void provision() {
        this.currentProvisionRequest = this.mediaDrm.getProvisionRequest();
        this.postRequestHandler.post(0, this.currentProvisionRequest, true);
    }

    public void onProvisionCompleted() {
        if (openInternal(false)) {
            doLicense(true);
        }
    }

    public void onProvisionError(Exception error) {
        onError(error);
    }

    public final int getState() {
        return this.state;
    }

    public final DrmSessionException getError() {
        return this.state == 1 ? this.lastException : null;
    }

    public final T getMediaCrypto() {
        return this.mediaCrypto;
    }

    public Map<String, String> queryKeyStatus() {
        byte[] bArr = this.sessionId;
        return bArr == null ? null : this.mediaDrm.queryKeyStatus(bArr);
    }

    public byte[] getOfflineLicenseKeySetId() {
        return this.offlineLicenseKeySetId;
    }

    private boolean openInternal(boolean allowProvisioning) {
        if (isOpen()) {
            return true;
        }
        try {
            this.sessionId = this.mediaDrm.openSession();
            this.eventDispatcher.dispatch(-$$Lambda$jFcVU4qXZB2nhSZWHXCB9S7MtRI.INSTANCE);
            this.mediaCrypto = this.mediaDrm.createMediaCrypto(this.sessionId);
            this.state = 3;
            return true;
        } catch (NotProvisionedException e) {
            if (allowProvisioning) {
                this.provisioningManager.provisionRequired(this);
            } else {
                onError(e);
            }
            return false;
        } catch (Exception e2) {
            onError(e2);
            return false;
        }
    }

    private void onProvisionResponse(Object request, Object response) {
        if (request == this.currentProvisionRequest) {
            if (this.state == 2 || isOpen()) {
                this.currentProvisionRequest = null;
                if (response instanceof Exception) {
                    this.provisioningManager.onProvisionError((Exception) response);
                    return;
                }
                try {
                    this.mediaDrm.provideProvisionResponse((byte[]) response);
                    this.provisioningManager.onProvisionCompleted();
                } catch (Exception e) {
                    this.provisioningManager.onProvisionError(e);
                }
            }
        }
    }

    private void doLicense(boolean allowRetry) {
        switch (this.mode) {
            case 0:
            case 1:
                if (this.offlineLicenseKeySetId == null) {
                    postKeyRequest(1, allowRetry);
                    return;
                }
                if (this.state != 4) {
                    if (!restoreKeys()) {
                        return;
                    }
                }
                long licenseDurationRemainingSec = getLicenseDurationRemainingSec();
                if (this.mode == 0 && licenseDurationRemainingSec <= 60) {
                    String str = TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Offline license has expired or will expire soon. Remaining seconds: ");
                    stringBuilder.append(licenseDurationRemainingSec);
                    Log.m4d(str, stringBuilder.toString());
                    postKeyRequest(2, allowRetry);
                } else if (licenseDurationRemainingSec <= 0) {
                    onError(new KeysExpiredException());
                } else {
                    this.state = 4;
                    this.eventDispatcher.dispatch(-$$Lambda$tzysvANfjWo6mXRxYD2fQMdks_4.INSTANCE);
                }
                return;
            case 2:
                if (this.offlineLicenseKeySetId == null) {
                    postKeyRequest(2, allowRetry);
                    return;
                } else if (restoreKeys()) {
                    postKeyRequest(2, allowRetry);
                    return;
                } else {
                    return;
                }
            case 3:
                if (restoreKeys()) {
                    postKeyRequest(3, allowRetry);
                    return;
                }
                return;
            default:
                return;
        }
    }

    private boolean restoreKeys() {
        try {
            this.mediaDrm.restoreKeys(this.sessionId, this.offlineLicenseKeySetId);
            return true;
        } catch (Exception e) {
            Log.m7e(TAG, "Error trying to restore Widevine keys.", e);
            onError(e);
            return false;
        }
    }

    private long getLicenseDurationRemainingSec() {
        if (!C0555C.WIDEVINE_UUID.equals(this.uuid)) {
            return Long.MAX_VALUE;
        }
        Pair<Long, Long> pair = WidevineUtil.getLicenseDurationRemainingSec(this);
        return Math.min(((Long) pair.first).longValue(), ((Long) pair.second).longValue());
    }

    private void postKeyRequest(int type, boolean allowRetry) {
        try {
            this.currentKeyRequest = this.mediaDrm.getKeyRequest(type == 3 ? this.offlineLicenseKeySetId : this.sessionId, this.schemeDatas, type, this.optionalKeyRequestParameters);
            this.postRequestHandler.post(1, this.currentKeyRequest, allowRetry);
        } catch (Exception e) {
            onKeysError(e);
        }
    }

    private void onKeyResponse(Object request, Object response) {
        if (request == this.currentKeyRequest) {
            if (isOpen()) {
                this.currentKeyRequest = null;
                if (response instanceof Exception) {
                    onKeysError((Exception) response);
                    return;
                }
                try {
                    byte[] responseData = (byte[]) response;
                    if (this.mode == 3) {
                        this.mediaDrm.provideKeyResponse(this.offlineLicenseKeySetId, responseData);
                        this.eventDispatcher.dispatch(-$$Lambda$tzysvANfjWo6mXRxYD2fQMdks_4.INSTANCE);
                    } else {
                        byte[] keySetId = this.mediaDrm.provideKeyResponse(this.sessionId, responseData);
                        if ((this.mode == 2 || (this.mode == 0 && this.offlineLicenseKeySetId != null)) && keySetId != null && keySetId.length != 0) {
                            this.offlineLicenseKeySetId = keySetId;
                        }
                        this.state = 4;
                        this.eventDispatcher.dispatch(-$$Lambda$wyKVEWJALn1OyjwryLo2GUxlQ2M.INSTANCE);
                    }
                } catch (Exception e) {
                    onKeysError(e);
                }
            }
        }
    }

    private void onKeysExpired() {
        if (this.state == 4) {
            this.state = 3;
            onError(new KeysExpiredException());
        }
    }

    private void onKeysError(Exception e) {
        if (e instanceof NotProvisionedException) {
            this.provisioningManager.provisionRequired(this);
        } else {
            onError(e);
        }
    }

    private void onError(Exception e) {
        this.lastException = new DrmSessionException(e);
        this.eventDispatcher.dispatch(new -$$Lambda$DefaultDrmSession$-nKOJC1w2998gRg4Cg4l2mjlp30(e));
        if (this.state != 4) {
            this.state = 1;
        }
    }

    private boolean isOpen() {
        int i = this.state;
        if (i != 3) {
            if (i != 4) {
                return false;
            }
        }
        return true;
    }
}
