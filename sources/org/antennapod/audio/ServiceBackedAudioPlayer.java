package org.antennapod.audio;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.util.Log;
import com.aocate.presto.service.IDeathCallback_0_8;
import com.aocate.presto.service.IOnBufferingUpdateListenerCallback_0_8.Stub;
import com.aocate.presto.service.IOnCompletionListenerCallback_0_8;
import com.aocate.presto.service.IOnErrorListenerCallback_0_8;
import com.aocate.presto.service.IOnInfoListenerCallback_0_8;
import com.aocate.presto.service.IOnPitchAdjustmentAvailableChangedListenerCallback_0_8;
import com.aocate.presto.service.IOnPreparedListenerCallback_0_8;
import com.aocate.presto.service.IOnSeekCompleteListenerCallback_0_8;
import com.aocate.presto.service.IOnSpeedAdjustmentAvailableChangedListenerCallback_0_8;
import com.aocate.presto.service.IPlayMedia_0_8;
import java.io.IOException;
import org.antennapod.audio.MediaPlayer.State;

public class ServiceBackedAudioPlayer extends AbstractAudioPlayer {
    static final String INTENT_NAME = "com.aocate.intent.PLAY_AUDIO_ADJUST_SPEED_0_8";
    private static final String SBMP_TAG = "ServiceBackedMediaPlaye";
    private boolean isErroring = false;
    private int mAudioStreamType = 3;
    private Stub mOnBufferingUpdateCallback = null;
    private IOnCompletionListenerCallback_0_8.Stub mOnCompletionCallback = null;
    private IOnErrorListenerCallback_0_8.Stub mOnErrorCallback = null;
    private IOnInfoListenerCallback_0_8.Stub mOnInfoCallback = null;
    private IOnPitchAdjustmentAvailableChangedListenerCallback_0_8.Stub mOnPitchAdjustmentAvailableChangedCallback = null;
    private IOnPreparedListenerCallback_0_8.Stub mOnPreparedCallback = null;
    private IOnSeekCompleteListenerCallback_0_8.Stub mOnSeekCompleteCallback = null;
    private IOnSpeedAdjustmentAvailableChangedListenerCallback_0_8.Stub mOnSpeedAdjustmentAvailableChangedCallback = null;
    private ServiceConnection mPlayMediaServiceConnection = null;
    private WakeLock mWakeLock = null;
    private Intent playMediaServiceIntent = null;
    protected IPlayMedia_0_8 pmInterface = null;
    private int sessionId = 0;

    /* renamed from: org.antennapod.audio.ServiceBackedAudioPlayer$2 */
    class C12752 extends Stub {
        C12752() {
        }

        public void onBufferingUpdate(int percent) throws RemoteException {
            ServiceBackedAudioPlayer.this.owningMediaPlayer.lock.lock();
            try {
                if (ServiceBackedAudioPlayer.this.owningMediaPlayer.onBufferingUpdateListener != null && ServiceBackedAudioPlayer.this.owningMediaPlayer.mpi == ServiceBackedAudioPlayer.this) {
                    ServiceBackedAudioPlayer.this.owningMediaPlayer.onBufferingUpdateListener.onBufferingUpdate(ServiceBackedAudioPlayer.this.owningMediaPlayer, percent);
                }
                ServiceBackedAudioPlayer.this.owningMediaPlayer.lock.unlock();
            } catch (Throwable th) {
                ServiceBackedAudioPlayer.this.owningMediaPlayer.lock.unlock();
            }
        }
    }

    /* renamed from: org.antennapod.audio.ServiceBackedAudioPlayer$3 */
    class C12763 extends IOnCompletionListenerCallback_0_8.Stub {
        C12763() {
        }

        public void onCompletion() throws RemoteException {
            ServiceBackedAudioPlayer.this.owningMediaPlayer.lock.lock();
            Log.d(ServiceBackedAudioPlayer.SBMP_TAG, "onCompletionListener being called");
            ServiceBackedAudioPlayer.this.stayAwake(false);
            try {
                if (ServiceBackedAudioPlayer.this.owningMediaPlayer.onCompletionListener != null) {
                    ServiceBackedAudioPlayer.this.owningMediaPlayer.onCompletionListener.onCompletion(ServiceBackedAudioPlayer.this.owningMediaPlayer);
                }
                ServiceBackedAudioPlayer.this.owningMediaPlayer.lock.unlock();
            } catch (Throwable th) {
                ServiceBackedAudioPlayer.this.owningMediaPlayer.lock.unlock();
            }
        }
    }

    /* renamed from: org.antennapod.audio.ServiceBackedAudioPlayer$4 */
    class C12774 extends IOnErrorListenerCallback_0_8.Stub {
        C12774() {
        }

        public boolean onError(int what, int extra) throws RemoteException {
            ServiceBackedAudioPlayer.this.owningMediaPlayer.lock.lock();
            ServiceBackedAudioPlayer.this.stayAwake(false);
            try {
                if (ServiceBackedAudioPlayer.this.owningMediaPlayer.onErrorListener != null) {
                    boolean onError = ServiceBackedAudioPlayer.this.owningMediaPlayer.onErrorListener.onError(ServiceBackedAudioPlayer.this.owningMediaPlayer, what, extra);
                    return onError;
                }
                ServiceBackedAudioPlayer.this.owningMediaPlayer.lock.unlock();
                return false;
            } finally {
                ServiceBackedAudioPlayer.this.owningMediaPlayer.lock.unlock();
            }
        }
    }

    /* renamed from: org.antennapod.audio.ServiceBackedAudioPlayer$5 */
    class C12785 extends IOnInfoListenerCallback_0_8.Stub {
        C12785() {
        }

        public boolean onInfo(int what, int extra) throws RemoteException {
            ServiceBackedAudioPlayer.this.owningMediaPlayer.lock.lock();
            try {
                if (ServiceBackedAudioPlayer.this.owningMediaPlayer.onInfoListener == null || ServiceBackedAudioPlayer.this.owningMediaPlayer.mpi != ServiceBackedAudioPlayer.this) {
                    ServiceBackedAudioPlayer.this.owningMediaPlayer.lock.unlock();
                    return false;
                }
                boolean onInfo = ServiceBackedAudioPlayer.this.owningMediaPlayer.onInfoListener.onInfo(ServiceBackedAudioPlayer.this.owningMediaPlayer, what, extra);
                return onInfo;
            } finally {
                ServiceBackedAudioPlayer.this.owningMediaPlayer.lock.unlock();
            }
        }
    }

    /* renamed from: org.antennapod.audio.ServiceBackedAudioPlayer$6 */
    class C12796 extends IOnPitchAdjustmentAvailableChangedListenerCallback_0_8.Stub {
        C12796() {
        }

        public void onPitchAdjustmentAvailableChanged(boolean pitchAdjustmentAvailable) throws RemoteException {
            ServiceBackedAudioPlayer.this.owningMediaPlayer.lock.lock();
            try {
                if (ServiceBackedAudioPlayer.this.owningMediaPlayer.onPitchAdjustmentAvailableChangedListener != null) {
                    ServiceBackedAudioPlayer.this.owningMediaPlayer.onPitchAdjustmentAvailableChangedListener.onPitchAdjustmentAvailableChanged(ServiceBackedAudioPlayer.this.owningMediaPlayer, pitchAdjustmentAvailable);
                }
                ServiceBackedAudioPlayer.this.owningMediaPlayer.lock.unlock();
            } catch (Throwable th) {
                ServiceBackedAudioPlayer.this.owningMediaPlayer.lock.unlock();
            }
        }
    }

    /* renamed from: org.antennapod.audio.ServiceBackedAudioPlayer$7 */
    class C12807 extends IOnPreparedListenerCallback_0_8.Stub {
        C12807() {
        }

        public void onPrepared() throws RemoteException {
            ServiceBackedAudioPlayer.this.owningMediaPlayer.lock.lock();
            Log.d(ServiceBackedAudioPlayer.SBMP_TAG, "setOnPreparedCallback.mOnPreparedCallback.onPrepared 1050");
            try {
                String str = ServiceBackedAudioPlayer.SBMP_TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("owningMediaPlayer.onPreparedListener is ");
                stringBuilder.append(ServiceBackedAudioPlayer.this.owningMediaPlayer.onPreparedListener == null ? "null" : "non-null");
                Log.d(str, stringBuilder.toString());
                str = ServiceBackedAudioPlayer.SBMP_TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append("owningMediaPlayer.mpi is ");
                stringBuilder.append(ServiceBackedAudioPlayer.this.owningMediaPlayer.mpi == ServiceBackedAudioPlayer.this ? "this" : "not this");
                Log.d(str, stringBuilder.toString());
                ServiceBackedAudioPlayer.this.lockMuteOnPreparedCount.lock();
                if (ServiceBackedAudioPlayer.this.muteOnPreparedCount > 0) {
                    ServiceBackedAudioPlayer serviceBackedAudioPlayer = ServiceBackedAudioPlayer.this;
                    serviceBackedAudioPlayer.muteOnPreparedCount--;
                } else {
                    ServiceBackedAudioPlayer.this.muteOnPreparedCount = 0;
                    if (ServiceBackedAudioPlayer.this.owningMediaPlayer.onPreparedListener != null) {
                        ServiceBackedAudioPlayer.this.owningMediaPlayer.onPreparedListener.onPrepared(ServiceBackedAudioPlayer.this.owningMediaPlayer);
                    }
                }
                ServiceBackedAudioPlayer.this.lockMuteOnPreparedCount.unlock();
                ServiceBackedAudioPlayer.this.owningMediaPlayer.lock.unlock();
            } catch (Throwable th) {
                ServiceBackedAudioPlayer.this.owningMediaPlayer.lock.unlock();
            }
        }
    }

    /* renamed from: org.antennapod.audio.ServiceBackedAudioPlayer$8 */
    class C12818 extends IOnSeekCompleteListenerCallback_0_8.Stub {
        C12818() {
        }

        public void onSeekComplete() throws RemoteException {
            Log.d(ServiceBackedAudioPlayer.SBMP_TAG, "onSeekComplete() 941");
            ServiceBackedAudioPlayer.this.owningMediaPlayer.lock.lock();
            try {
                if (ServiceBackedAudioPlayer.this.muteOnSeekCount > 0) {
                    String str = ServiceBackedAudioPlayer.SBMP_TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("The next ");
                    stringBuilder.append(ServiceBackedAudioPlayer.this.muteOnSeekCount);
                    stringBuilder.append(" seek events are muted (counting this one)");
                    Log.d(str, stringBuilder.toString());
                    ServiceBackedAudioPlayer serviceBackedAudioPlayer = ServiceBackedAudioPlayer.this;
                    serviceBackedAudioPlayer.muteOnSeekCount--;
                } else {
                    ServiceBackedAudioPlayer.this.muteOnSeekCount = 0;
                    Log.d(ServiceBackedAudioPlayer.SBMP_TAG, "Attempting to invoke next seek event");
                    if (ServiceBackedAudioPlayer.this.owningMediaPlayer.onSeekCompleteListener != null) {
                        Log.d(ServiceBackedAudioPlayer.SBMP_TAG, "Invoking onSeekComplete");
                        ServiceBackedAudioPlayer.this.owningMediaPlayer.onSeekCompleteListener.onSeekComplete(ServiceBackedAudioPlayer.this.owningMediaPlayer);
                    }
                }
                ServiceBackedAudioPlayer.this.owningMediaPlayer.lock.unlock();
            } catch (Throwable th) {
                ServiceBackedAudioPlayer.this.owningMediaPlayer.lock.unlock();
            }
        }
    }

    /* renamed from: org.antennapod.audio.ServiceBackedAudioPlayer$9 */
    class C12829 extends IOnSpeedAdjustmentAvailableChangedListenerCallback_0_8.Stub {
        C12829() {
        }

        public void onSpeedAdjustmentAvailableChanged(boolean speedAdjustmentAvailable) throws RemoteException {
            ServiceBackedAudioPlayer.this.owningMediaPlayer.lock.lock();
            try {
                if (ServiceBackedAudioPlayer.this.owningMediaPlayer.onSpeedAdjustmentAvailableChangedListener != null) {
                    ServiceBackedAudioPlayer.this.owningMediaPlayer.onSpeedAdjustmentAvailableChangedListener.onSpeedAdjustmentAvailableChanged(ServiceBackedAudioPlayer.this.owningMediaPlayer, speedAdjustmentAvailable);
                }
                ServiceBackedAudioPlayer.this.owningMediaPlayer.lock.unlock();
            } catch (Throwable th) {
                ServiceBackedAudioPlayer.this.owningMediaPlayer.lock.unlock();
            }
        }
    }

    public ServiceBackedAudioPlayer(MediaPlayer owningMediaPlayer, Context context, final ServiceConnection serviceConnection) {
        super(owningMediaPlayer, context);
        Log.d(SBMP_TAG, "Instantiating ServiceBackedMediaPlayer 87");
        this.playMediaServiceIntent = MediaPlayer.getPrestoServiceIntent(context, INTENT_NAME);
        this.mPlayMediaServiceConnection = new ServiceConnection() {

            /* renamed from: org.antennapod.audio.ServiceBackedAudioPlayer$1$1 */
            class C12741 extends IDeathCallback_0_8.Stub {
                C12741() {
                }
            }

            public void onServiceConnected(ComponentName name, IBinder service) {
                IPlayMedia_0_8 tmpPlayMediaInterface = IPlayMedia_0_8.Stub.asInterface(service);
                Log.d(ServiceBackedAudioPlayer.SBMP_TAG, "Setting up pmInterface 94");
                if (ServiceBackedAudioPlayer.this.sessionId == 0) {
                    try {
                        ServiceBackedAudioPlayer.this.sessionId = (int) tmpPlayMediaInterface.startSession(new C12741());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        ServiceBackedAudioPlayer.this.error(1, 0);
                    }
                }
                Log.d(ServiceBackedAudioPlayer.SBMP_TAG, "Assigning pmInterface");
                ServiceBackedAudioPlayer.this.setOnBufferingUpdateCallback(tmpPlayMediaInterface);
                ServiceBackedAudioPlayer.this.setOnCompletionCallback(tmpPlayMediaInterface);
                ServiceBackedAudioPlayer.this.setOnErrorCallback(tmpPlayMediaInterface);
                ServiceBackedAudioPlayer.this.setOnInfoCallback(tmpPlayMediaInterface);
                ServiceBackedAudioPlayer.this.setOnPitchAdjustmentAvailableChangedListener(tmpPlayMediaInterface);
                ServiceBackedAudioPlayer.this.setOnPreparedCallback(tmpPlayMediaInterface);
                ServiceBackedAudioPlayer.this.setOnSeekCompleteCallback(tmpPlayMediaInterface);
                ServiceBackedAudioPlayer.this.setOnSpeedAdjustmentAvailableChangedCallback(tmpPlayMediaInterface);
                ServiceBackedAudioPlayer.this.pmInterface = tmpPlayMediaInterface;
                Log.d(ServiceBackedAudioPlayer.SBMP_TAG, "Invoking onServiceConnected");
                serviceConnection.onServiceConnected(name, service);
            }

            public void onServiceDisconnected(ComponentName name) {
                Log.d(ServiceBackedAudioPlayer.SBMP_TAG, "onServiceDisconnected 114");
                ServiceBackedAudioPlayer serviceBackedAudioPlayer = ServiceBackedAudioPlayer.this;
                serviceBackedAudioPlayer.pmInterface = null;
                serviceBackedAudioPlayer.sessionId = 0;
                serviceConnection.onServiceDisconnected(name);
            }
        };
        Log.d(SBMP_TAG, "Connecting PlayMediaService 124");
        if (!ConnectPlayMediaService()) {
            Log.e(SBMP_TAG, "bindService failed");
            error(1, 0);
        }
    }

    private boolean ConnectPlayMediaService() {
        Log.d(SBMP_TAG, "ConnectPlayMediaService()");
        if (MediaPlayer.isIntentAvailable(this.mContext, INTENT_NAME)) {
            Log.d(SBMP_TAG, "com.aocate.intent.PLAY_AUDIO_ADJUST_SPEED_0_8 is available");
            if (this.pmInterface == null) {
                try {
                    Log.d(SBMP_TAG, "Binding service");
                    return this.mContext.bindService(this.playMediaServiceIntent, this.mPlayMediaServiceConnection, 1);
                } catch (Exception e) {
                    Log.e(SBMP_TAG, "Could not bind with service", e);
                    return false;
                }
            }
            Log.d(SBMP_TAG, "Service already bound");
            return true;
        }
        Log.d(SBMP_TAG, "com.aocate.intent.PLAY_AUDIO_ADJUST_SPEED_0_8 is not available");
        return false;
    }

    public int getAudioSessionId() {
        return this.sessionId;
    }

    public boolean canSetPitch() {
        Log.d(SBMP_TAG, "canSetPitch() 155");
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        IPlayMedia_0_8 iPlayMedia_0_8 = this.pmInterface;
        if (iPlayMedia_0_8 == null) {
            return false;
        }
        try {
            return iPlayMedia_0_8.canSetPitch((long) this.sessionId);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
    }

    public boolean canSetSpeed() {
        Log.d(SBMP_TAG, "canSetSpeed() 180");
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        IPlayMedia_0_8 iPlayMedia_0_8 = this.pmInterface;
        if (iPlayMedia_0_8 == null) {
            return false;
        }
        try {
            return iPlayMedia_0_8.canSetSpeed((long) this.sessionId);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
    }

    public boolean canDownmix() {
        return false;
    }

    public void setDownmix(boolean enable) {
    }

    void error(int what, int extra) {
        this.owningMediaPlayer.lock.lock();
        String str = SBMP_TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("error(");
        stringBuilder.append(what);
        stringBuilder.append(", ");
        stringBuilder.append(extra);
        stringBuilder.append(")");
        Log.e(str, stringBuilder.toString());
        stayAwake(false);
        try {
            if (!this.isErroring) {
                this.isErroring = true;
                this.owningMediaPlayer.state = State.ERROR;
                if (this.owningMediaPlayer.onErrorListener != null) {
                    if (this.owningMediaPlayer.onErrorListener.onError(this.owningMediaPlayer, what, extra)) {
                        return;
                    }
                }
                if (this.owningMediaPlayer.onCompletionListener != null) {
                    this.owningMediaPlayer.onCompletionListener.onCompletion(this.owningMediaPlayer);
                }
            }
            this.isErroring = false;
            this.owningMediaPlayer.lock.unlock();
        } finally {
            this.isErroring = false;
            this.owningMediaPlayer.lock.unlock();
        }
    }

    protected void finalize() throws Throwable {
        this.owningMediaPlayer.lock.lock();
        try {
            Log.d(SBMP_TAG, "finalize() 224");
            release();
        } finally {
            this.owningMediaPlayer.lock.unlock();
        }
    }

    public float getCurrentPitchStepsAdjustment() {
        Log.d(SBMP_TAG, "getCurrentPitchStepsAdjustment() 240");
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        IPlayMedia_0_8 iPlayMedia_0_8 = this.pmInterface;
        if (iPlayMedia_0_8 == null) {
            return 0.0f;
        }
        try {
            return iPlayMedia_0_8.getCurrentPitchStepsAdjustment((long) this.sessionId);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
    }

    public int getCurrentPosition() {
        IPlayMedia_0_8 iPlayMedia_0_8 = this.pmInterface;
        if (iPlayMedia_0_8 == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
            return 0;
        }
        try {
            return iPlayMedia_0_8.getCurrentPosition((long) this.sessionId);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
            return 0;
        }
    }

    public float getCurrentSpeedMultiplier() {
        Log.d(SBMP_TAG, "getCurrentSpeedMultiplier() 286");
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        IPlayMedia_0_8 iPlayMedia_0_8 = this.pmInterface;
        if (iPlayMedia_0_8 == null) {
            return 1.0f;
        }
        try {
            return iPlayMedia_0_8.getCurrentSpeedMultiplier((long) this.sessionId);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
    }

    public int getDuration() {
        Log.d(SBMP_TAG, "getDuration() 311");
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        try {
            return this.pmInterface.getDuration((long) this.sessionId);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
            return 0;
        }
    }

    public float getMaxSpeedMultiplier() {
        Log.d(SBMP_TAG, "getMaxSpeedMultiplier() 332");
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        IPlayMedia_0_8 iPlayMedia_0_8 = this.pmInterface;
        if (iPlayMedia_0_8 == null) {
            return 1.0f;
        }
        try {
            return iPlayMedia_0_8.getMaxSpeedMultiplier((long) this.sessionId);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
    }

    public float getMinSpeedMultiplier() {
        Log.d(SBMP_TAG, "getMinSpeedMultiplier() 357");
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        IPlayMedia_0_8 iPlayMedia_0_8 = this.pmInterface;
        if (iPlayMedia_0_8 == null) {
            return 1.0f;
        }
        try {
            return iPlayMedia_0_8.getMinSpeedMultiplier((long) this.sessionId);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
    }

    public int getServiceVersionCode() {
        Log.d(SBMP_TAG, "getVersionCode");
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        try {
            return this.pmInterface.getVersionCode();
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
            return 0;
        }
    }

    public String getServiceVersionName() {
        Log.d(SBMP_TAG, "getVersionName");
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        try {
            return this.pmInterface.getVersionName();
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
            return "";
        }
    }

    public boolean isConnected() {
        return this.pmInterface != null;
    }

    public boolean isLooping() {
        Log.d(SBMP_TAG, "isLooping() 382");
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        try {
            return this.pmInterface.isLooping((long) this.sessionId);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
            return false;
        }
    }

    public boolean isPlaying() {
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        IPlayMedia_0_8 iPlayMedia_0_8 = this.pmInterface;
        if (iPlayMedia_0_8 == null) {
            return false;
        }
        try {
            return iPlayMedia_0_8.isPlaying((long) this.sessionId);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
    }

    public void pause() {
        Log.d(SBMP_TAG, "pause() 424");
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        try {
            this.pmInterface.pause((long) this.sessionId);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
        stayAwake(false);
    }

    public void prepare() throws IllegalStateException, IOException {
        Log.d(SBMP_TAG, "prepare() 444");
        String str = SBMP_TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onPreparedCallback is: ");
        stringBuilder.append(this.mOnPreparedCallback == null ? "null" : "non-null");
        Log.d(str, stringBuilder.toString());
        if (this.pmInterface == null) {
            Log.d(SBMP_TAG, "prepare: pmInterface is null");
            if (!ConnectPlayMediaService()) {
                Log.d(SBMP_TAG, "prepare: Failed to connect play media service");
                error(1, 0);
            }
        }
        if (this.pmInterface != null) {
            Log.d(SBMP_TAG, "prepare: pmInterface isn't null");
            try {
                str = SBMP_TAG;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("prepare: Remote invoke pmInterface.prepare(");
                stringBuilder2.append(this.sessionId);
                stringBuilder2.append(")");
                Log.d(str, stringBuilder2.toString());
                this.pmInterface.prepare((long) this.sessionId);
                Log.d(SBMP_TAG, "prepare: prepared");
            } catch (RemoteException e) {
                Log.d(SBMP_TAG, "prepare: RemoteException");
                e.printStackTrace();
                error(1, 0);
            }
        }
        Log.d(SBMP_TAG, "Done with prepare()");
    }

    public void prepareAsync() {
        Log.d(SBMP_TAG, "prepareAsync() 469");
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        try {
            this.pmInterface.prepareAsync((long) this.sessionId);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
    }

    public void release() {
        Log.d(SBMP_TAG, "release() 492");
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        if (this.pmInterface != null) {
            Log.d(SBMP_TAG, "release() 500");
            try {
                this.pmInterface.release((long) this.sessionId);
            } catch (RemoteException e) {
                e.printStackTrace();
                error(1, 0);
            }
            this.mContext.unbindService(this.mPlayMediaServiceConnection);
            setWakeMode(this.mContext, 0);
            this.pmInterface = null;
            this.sessionId = 0;
        }
        WakeLock wakeLock = this.mWakeLock;
        if (wakeLock != null && wakeLock.isHeld()) {
            Log.d(SBMP_TAG, "Releasing wakelock");
            this.mWakeLock.release();
        }
    }

    public void reset() {
        Log.d(SBMP_TAG, "reset() 523");
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        try {
            this.pmInterface.reset((long) this.sessionId);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
        stayAwake(false);
    }

    public void seekTo(int msec) throws IllegalStateException {
        String str = SBMP_TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("seekTo(");
        stringBuilder.append(msec);
        stringBuilder.append(")");
        Log.d(str, stringBuilder.toString());
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        try {
            this.pmInterface.seekTo((long) this.sessionId, msec);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
    }

    public void setAudioStreamType(int streamtype) {
        String str = SBMP_TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("setAudioStreamType(");
        stringBuilder.append(streamtype);
        stringBuilder.append(")");
        Log.d(str, stringBuilder.toString());
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        try {
            this.pmInterface.setAudioStreamType((long) this.sessionId, this.mAudioStreamType);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
    }

    public void setDataSource(Context context, Uri uri) throws IllegalArgumentException, IllegalStateException, IOException {
        Log.d(SBMP_TAG, "setDataSource(context, uri)");
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        try {
            this.pmInterface.setDataSourceUri((long) this.sessionId, uri);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
    }

    public void setDataSource(String path) throws IllegalArgumentException, IllegalStateException, IOException {
        Log.d(SBMP_TAG, "setDataSource(path)");
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        IPlayMedia_0_8 iPlayMedia_0_8 = this.pmInterface;
        if (iPlayMedia_0_8 == null) {
            error(1, 0);
        } else {
            try {
                iPlayMedia_0_8.setDataSourceString((long) this.sessionId, path);
            } catch (RemoteException e) {
                e.printStackTrace();
                error(1, 0);
            }
        }
    }

    public void setEnableSpeedAdjustment(boolean enableSpeedAdjustment) {
        this.owningMediaPlayer.lock.lock();
        Log.d(SBMP_TAG, "setEnableSpeedAdjustment(enableSpeedAdjustment)");
        try {
            if (this.pmInterface == null) {
                if (!ConnectPlayMediaService()) {
                    error(1, 0);
                }
            }
            if (this.pmInterface != null) {
                this.pmInterface.setEnableSpeedAdjustment((long) this.sessionId, enableSpeedAdjustment);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        } catch (Throwable th) {
            this.owningMediaPlayer.lock.unlock();
        }
        this.owningMediaPlayer.lock.unlock();
    }

    public void setLooping(boolean loop) {
        String str = SBMP_TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("setLooping(");
        stringBuilder.append(loop);
        stringBuilder.append(")");
        Log.d(str, stringBuilder.toString());
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        try {
            this.pmInterface.setLooping((long) this.sessionId, loop);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
    }

    public void setPitchStepsAdjustment(float pitchSteps) {
        String str = SBMP_TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("setPitchStepsAdjustment(");
        stringBuilder.append(pitchSteps);
        stringBuilder.append(")");
        Log.d(str, stringBuilder.toString());
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        IPlayMedia_0_8 iPlayMedia_0_8 = this.pmInterface;
        if (iPlayMedia_0_8 != null) {
            try {
                iPlayMedia_0_8.setPitchStepsAdjustment((long) this.sessionId, pitchSteps);
            } catch (RemoteException e) {
                e.printStackTrace();
                error(1, 0);
            }
        }
    }

    public void setPlaybackPitch(float f) {
        String str = SBMP_TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("setPlaybackPitch(");
        stringBuilder.append(f);
        stringBuilder.append(")");
        Log.d(str, stringBuilder.toString());
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        IPlayMedia_0_8 iPlayMedia_0_8 = this.pmInterface;
        if (iPlayMedia_0_8 != null) {
            try {
                iPlayMedia_0_8.setPlaybackPitch((long) this.sessionId, f);
            } catch (RemoteException e) {
                e.printStackTrace();
                error(1, 0);
            }
        }
    }

    public void setPlaybackSpeed(float f) {
        String str = SBMP_TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("setPlaybackSpeed(");
        stringBuilder.append(f);
        stringBuilder.append(")");
        Log.d(str, stringBuilder.toString());
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        IPlayMedia_0_8 iPlayMedia_0_8 = this.pmInterface;
        if (iPlayMedia_0_8 != null) {
            try {
                iPlayMedia_0_8.setPlaybackSpeed((long) this.sessionId, f);
            } catch (RemoteException e) {
                e.printStackTrace();
                error(1, 0);
            }
        }
    }

    public void setVolume(float leftVolume, float rightVolume) {
        String str = SBMP_TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("setVolume(");
        stringBuilder.append(leftVolume);
        stringBuilder.append(", ");
        stringBuilder.append(rightVolume);
        stringBuilder.append(")");
        Log.d(str, stringBuilder.toString());
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        try {
            this.pmInterface.setVolume((long) this.sessionId, leftVolume, rightVolume);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
    }

    public void setWakeMode(Context context, int mode) {
        String str = SBMP_TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("setWakeMode(context, ");
        stringBuilder.append(mode);
        stringBuilder.append(")");
        Log.d(str, stringBuilder.toString());
        boolean wasHeld = false;
        WakeLock wakeLock = this.mWakeLock;
        if (wakeLock != null) {
            if (wakeLock.isHeld()) {
                wasHeld = true;
                Log.d(SBMP_TAG, "Releasing wakelock");
                this.mWakeLock.release();
            }
            this.mWakeLock = null;
        }
        if (mode != 0) {
            this.mWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(mode, getClass().getName());
            this.mWakeLock.setReferenceCounted(false);
            if (wasHeld) {
                Log.d(SBMP_TAG, "Acquiring wakelock");
                this.mWakeLock.acquire();
            }
        }
    }

    private void stayAwake(boolean awake) {
        WakeLock wakeLock = this.mWakeLock;
        if (wakeLock == null) {
            return;
        }
        if (awake && !wakeLock.isHeld()) {
            Log.d(SBMP_TAG, "Acquiring wakelock");
            this.mWakeLock.acquire();
        } else if (!awake && this.mWakeLock.isHeld()) {
            Log.d(SBMP_TAG, "Releasing wakelock");
            this.mWakeLock.release();
        }
    }

    private void setOnBufferingUpdateCallback(IPlayMedia_0_8 iface) {
        try {
            if (this.mOnBufferingUpdateCallback == null) {
                this.mOnBufferingUpdateCallback = new C12752();
            }
            iface.registerOnBufferingUpdateCallback((long) this.sessionId, this.mOnBufferingUpdateCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
    }

    private void setOnCompletionCallback(IPlayMedia_0_8 iface) {
        try {
            if (this.mOnCompletionCallback == null) {
                this.mOnCompletionCallback = new C12763();
            }
            iface.registerOnCompletionCallback((long) this.sessionId, this.mOnCompletionCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
    }

    private void setOnErrorCallback(IPlayMedia_0_8 iface) {
        try {
            if (this.mOnErrorCallback == null) {
                this.mOnErrorCallback = new C12774();
            }
            iface.registerOnErrorCallback((long) this.sessionId, this.mOnErrorCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
    }

    private void setOnInfoCallback(IPlayMedia_0_8 iface) {
        try {
            if (this.mOnInfoCallback == null) {
                this.mOnInfoCallback = new C12785();
            }
            iface.registerOnInfoCallback((long) this.sessionId, this.mOnInfoCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
    }

    private void setOnPitchAdjustmentAvailableChangedListener(IPlayMedia_0_8 iface) {
        try {
            if (this.mOnPitchAdjustmentAvailableChangedCallback == null) {
                this.mOnPitchAdjustmentAvailableChangedCallback = new C12796();
            }
            iface.registerOnPitchAdjustmentAvailableChangedCallback((long) this.sessionId, this.mOnPitchAdjustmentAvailableChangedCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
    }

    private void setOnPreparedCallback(IPlayMedia_0_8 iface) {
        try {
            if (this.mOnPreparedCallback == null) {
                this.mOnPreparedCallback = new C12807();
            }
            iface.registerOnPreparedCallback((long) this.sessionId, this.mOnPreparedCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
    }

    private void setOnSeekCompleteCallback(IPlayMedia_0_8 iface) {
        try {
            if (this.mOnSeekCompleteCallback == null) {
                this.mOnSeekCompleteCallback = new C12818();
            }
            iface.registerOnSeekCompleteCallback((long) this.sessionId, this.mOnSeekCompleteCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
    }

    private void setOnSpeedAdjustmentAvailableChangedCallback(IPlayMedia_0_8 iface) {
        try {
            Log.d(SBMP_TAG, "Setting the service of on speed adjustment available changed");
            if (this.mOnSpeedAdjustmentAvailableChangedCallback == null) {
                this.mOnSpeedAdjustmentAvailableChangedCallback = new C12829();
            }
            iface.registerOnSpeedAdjustmentAvailableChangedCallback((long) this.sessionId, this.mOnSpeedAdjustmentAvailableChangedCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
    }

    public void start() {
        Log.d(SBMP_TAG, "start()");
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        try {
            this.pmInterface.start((long) this.sessionId);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
        stayAwake(true);
    }

    public void stop() {
        Log.d(SBMP_TAG, "stop()");
        if (this.pmInterface == null) {
            if (!ConnectPlayMediaService()) {
                error(1, 0);
            }
        }
        try {
            this.pmInterface.stop((long) this.sessionId);
        } catch (RemoteException e) {
            e.printStackTrace();
            error(1, 0);
        }
        stayAwake(false);
    }
}
