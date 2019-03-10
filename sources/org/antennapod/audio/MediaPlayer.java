package org.antennapod.audio;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class MediaPlayer {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;
    public static final int MEDIA_ERROR_SERVER_DIED = 100;
    public static final int MEDIA_ERROR_UNKNOWN = 1;
    private static final String MP_TAG = "ReplacementMediaPlayer";
    private static final double PITCH_STEP_CONSTANT = 1.0594630943593d;
    private static final Uri SPEED_ADJUSTMENT_MARKET_URI = Uri.parse("market://details?id=com.aocate.presto");
    public static final String TAG = "MediaPlayer";
    private static Intent prestoMarketIntent = null;
    private AndroidAudioPlayer amp;
    private boolean enableSpeedAdjustment;
    private int lastKnownPosition;
    final ReentrantLock lock;
    private int mAudioStreamType;
    private final Context mContext;
    private boolean mIsLooping;
    private float mLeftVolume;
    private float mPitchStepsAdjustment;
    private float mRightVolume;
    private Handler mServiceDisconnectedHandler;
    private float mSpeedMultiplier;
    private int mWakeMode;
    AbstractAudioPlayer mpi;
    OnBufferingUpdateListener onBufferingUpdateListener;
    OnCompletionListener onCompletionListener;
    OnErrorListener onErrorListener;
    OnInfoListener onInfoListener;
    OnPitchAdjustmentAvailableChangedListener onPitchAdjustmentAvailableChangedListener;
    final OnPreparedListener onPreparedListener;
    OnSeekCompleteListener onSeekCompleteListener;
    OnSpeedAdjustmentAvailableChangedListener onSpeedAdjustmentAvailableChangedListener;
    private boolean pitchAdjustmentAvailable;
    private OnPitchAdjustmentAvailableChangedListener pitchAdjustmentAvailableChangedListener;
    private OnPreparedListener preparedListener;
    private ServiceBackedAudioPlayer sbmp;
    private SonicAudioPlayer smp;
    private boolean speedAdjustmentAvailable;
    private OnSpeedAdjustmentAvailableChangedListener speedAdjustmentAvailableChangedListener;
    State state;
    private String stringDataSource;
    private Uri uriDataSource;
    private boolean useService;

    /* renamed from: org.antennapod.audio.MediaPlayer$4 */
    class C11414 implements ServiceConnection {

        /* renamed from: org.antennapod.audio.MediaPlayer$4$1 */
        class C11391 implements Runnable {
            C11391() {
            }

            public void run() {
                MediaPlayer.this.lock.lock();
                Log.d(MediaPlayer.MP_TAG, "onServiceConnected");
                try {
                    MediaPlayer.this.switchMediaPlayerImpl(MediaPlayer.this.mpi, MediaPlayer.this.sbmp);
                    Log.d(MediaPlayer.MP_TAG, "End onServiceConnected");
                } finally {
                    MediaPlayer.this.lock.unlock();
                }
            }
        }

        /* renamed from: org.antennapod.audio.MediaPlayer$4$2 */
        class C11402 implements Callback {
            C11402() {
            }

            public boolean handleMessage(Message msg) {
                MediaPlayer.this.lock.lock();
                try {
                    if (MediaPlayer.this.amp == null) {
                        MediaPlayer.this.amp = new AndroidAudioPlayer(MediaPlayer.this, MediaPlayer.this.mContext);
                    }
                    MediaPlayer.this.switchMediaPlayerImpl(MediaPlayer.this.mpi, MediaPlayer.this.amp);
                    return true;
                } finally {
                    MediaPlayer.this.lock.unlock();
                }
            }
        }

        C11414() {
        }

        public void onServiceConnected(ComponentName className, IBinder service) {
            new Thread(new C11391()).start();
        }

        public void onServiceDisconnected(ComponentName className) {
            MediaPlayer.this.lock.lock();
            try {
                if (MediaPlayer.this.sbmp != null) {
                    MediaPlayer.this.sbmp.release();
                }
                MediaPlayer.this.sbmp = null;
                if (MediaPlayer.this.mServiceDisconnectedHandler == null) {
                    MediaPlayer.this.mServiceDisconnectedHandler = new Handler(new C11402());
                }
                MediaPlayer.this.mServiceDisconnectedHandler.sendMessage(MediaPlayer.this.mServiceDisconnectedHandler.obtainMessage());
            } finally {
                MediaPlayer.this.lock.unlock();
            }
        }
    }

    public interface OnBufferingUpdateListener {
        void onBufferingUpdate(MediaPlayer mediaPlayer, int i);
    }

    public interface OnCompletionListener {
        void onCompletion(MediaPlayer mediaPlayer);
    }

    public interface OnErrorListener {
        boolean onError(MediaPlayer mediaPlayer, int i, int i2);
    }

    public interface OnInfoListener {
        boolean onInfo(MediaPlayer mediaPlayer, int i, int i2);
    }

    public interface OnPitchAdjustmentAvailableChangedListener {
        void onPitchAdjustmentAvailableChanged(MediaPlayer mediaPlayer, boolean z);
    }

    public interface OnPreparedListener {
        void onPrepared(MediaPlayer mediaPlayer);
    }

    public interface OnSeekCompleteListener {
        void onSeekComplete(MediaPlayer mediaPlayer);
    }

    public interface OnSpeedAdjustmentAvailableChangedListener {
        void onSpeedAdjustmentAvailableChanged(MediaPlayer mediaPlayer, boolean z);
    }

    public enum State {
        IDLE,
        INITIALIZED,
        PREPARED,
        STARTED,
        PAUSED,
        STOPPED,
        PREPARING,
        PLAYBACK_COMPLETED,
        END,
        ERROR
    }

    /* renamed from: org.antennapod.audio.MediaPlayer$1 */
    class C12081 implements OnPitchAdjustmentAvailableChangedListener {
        C12081() {
        }

        public void onPitchAdjustmentAvailableChanged(MediaPlayer arg0, boolean pitchAdjustmentAvailable) {
            MediaPlayer.this.lock.lock();
            try {
                Log.d(MediaPlayer.MP_TAG, "onPitchAdjustmentAvailableChangedListener.onPitchAdjustmentAvailableChanged being called");
                if (MediaPlayer.this.pitchAdjustmentAvailable != pitchAdjustmentAvailable) {
                    String str = MediaPlayer.MP_TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Pitch adjustment state has changed from ");
                    stringBuilder.append(MediaPlayer.this.pitchAdjustmentAvailable);
                    stringBuilder.append(" to ");
                    stringBuilder.append(pitchAdjustmentAvailable);
                    Log.d(str, stringBuilder.toString());
                    MediaPlayer.this.pitchAdjustmentAvailable = pitchAdjustmentAvailable;
                    if (MediaPlayer.this.pitchAdjustmentAvailableChangedListener != null) {
                        MediaPlayer.this.pitchAdjustmentAvailableChangedListener.onPitchAdjustmentAvailableChanged(arg0, pitchAdjustmentAvailable);
                    }
                }
                MediaPlayer.this.lock.unlock();
            } catch (Throwable th) {
                MediaPlayer.this.lock.unlock();
            }
        }
    }

    /* renamed from: org.antennapod.audio.MediaPlayer$2 */
    class C12092 implements OnPreparedListener {
        C12092() {
        }

        public void onPrepared(MediaPlayer arg0) {
            Log.d(MediaPlayer.MP_TAG, "onPreparedListener 242 setting state to PREPARED");
            MediaPlayer.this.state = State.PREPARED;
            if (MediaPlayer.this.preparedListener != null) {
                Log.d(MediaPlayer.MP_TAG, "Calling preparedListener");
                MediaPlayer.this.preparedListener.onPrepared(arg0);
            }
            Log.d(MediaPlayer.MP_TAG, "Wrap up onPreparedListener");
        }
    }

    /* renamed from: org.antennapod.audio.MediaPlayer$3 */
    class C12103 implements OnSpeedAdjustmentAvailableChangedListener {
        C12103() {
        }

        public void onSpeedAdjustmentAvailableChanged(MediaPlayer arg0, boolean speedAdjustmentAvailable) {
            MediaPlayer.this.lock.lock();
            try {
                Log.d(MediaPlayer.MP_TAG, "onSpeedAdjustmentAvailableChangedListener.onSpeedAdjustmentAvailableChanged being called");
                if (MediaPlayer.this.speedAdjustmentAvailable != speedAdjustmentAvailable) {
                    String str = MediaPlayer.MP_TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Speed adjustment state has changed from ");
                    stringBuilder.append(MediaPlayer.this.speedAdjustmentAvailable);
                    stringBuilder.append(" to ");
                    stringBuilder.append(speedAdjustmentAvailable);
                    Log.d(str, stringBuilder.toString());
                    MediaPlayer.this.speedAdjustmentAvailable = speedAdjustmentAvailable;
                    if (MediaPlayer.this.speedAdjustmentAvailableChangedListener != null) {
                        MediaPlayer.this.speedAdjustmentAvailableChangedListener.onSpeedAdjustmentAvailableChanged(arg0, speedAdjustmentAvailable);
                    }
                }
                MediaPlayer.this.lock.unlock();
            } catch (Throwable th) {
                MediaPlayer.this.lock.unlock();
            }
        }
    }

    public void prepare() throws java.lang.IllegalStateException, java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:21:0x009a in {4, 6, 9, 10, 13, 14, 17, 20} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
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
        r0 = r3.lock;
        r0.lock();
        r0 = "ReplacementMediaPlayer";	 Catch:{ all -> 0x0093 }
        r1 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0093 }
        r1.<init>();	 Catch:{ all -> 0x0093 }
        r2 = "prepare() using ";	 Catch:{ all -> 0x0093 }
        r1.append(r2);	 Catch:{ all -> 0x0093 }
        r2 = r3.mpi;	 Catch:{ all -> 0x0093 }
        if (r2 != 0) goto L_0x0018;	 Catch:{ all -> 0x0093 }
    L_0x0015:
        r2 = "null (this shouldn't happen)";	 Catch:{ all -> 0x0093 }
    L_0x0017:
        goto L_0x0023;	 Catch:{ all -> 0x0093 }
    L_0x0018:
        r2 = r3.mpi;	 Catch:{ all -> 0x0093 }
        r2 = r2.getClass();	 Catch:{ all -> 0x0093 }
        r2 = r2.toString();	 Catch:{ all -> 0x0093 }
        goto L_0x0017;	 Catch:{ all -> 0x0093 }
    L_0x0023:
        r1.append(r2);	 Catch:{ all -> 0x0093 }
        r2 = " state ";	 Catch:{ all -> 0x0093 }
        r1.append(r2);	 Catch:{ all -> 0x0093 }
        r2 = r3.state;	 Catch:{ all -> 0x0093 }
        r2 = r2.toString();	 Catch:{ all -> 0x0093 }
        r1.append(r2);	 Catch:{ all -> 0x0093 }
        r1 = r1.toString();	 Catch:{ all -> 0x0093 }
        android.util.Log.d(r0, r1);	 Catch:{ all -> 0x0093 }
        r0 = "ReplacementMediaPlayer";	 Catch:{ all -> 0x0093 }
        r1 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0093 }
        r1.<init>();	 Catch:{ all -> 0x0093 }
        r2 = "onPreparedListener is: ";	 Catch:{ all -> 0x0093 }
        r1.append(r2);	 Catch:{ all -> 0x0093 }
        r2 = r3.onPreparedListener;	 Catch:{ all -> 0x0093 }
        if (r2 != 0) goto L_0x004e;	 Catch:{ all -> 0x0093 }
    L_0x004b:
        r2 = "null";	 Catch:{ all -> 0x0093 }
        goto L_0x0050;	 Catch:{ all -> 0x0093 }
    L_0x004e:
        r2 = "non-null";	 Catch:{ all -> 0x0093 }
    L_0x0050:
        r1.append(r2);	 Catch:{ all -> 0x0093 }
        r1 = r1.toString();	 Catch:{ all -> 0x0093 }
        android.util.Log.d(r0, r1);	 Catch:{ all -> 0x0093 }
        r0 = "ReplacementMediaPlayer";	 Catch:{ all -> 0x0093 }
        r1 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0093 }
        r1.<init>();	 Catch:{ all -> 0x0093 }
        r2 = "preparedListener is: ";	 Catch:{ all -> 0x0093 }
        r1.append(r2);	 Catch:{ all -> 0x0093 }
        r2 = r3.preparedListener;	 Catch:{ all -> 0x0093 }
        if (r2 != 0) goto L_0x006d;	 Catch:{ all -> 0x0093 }
    L_0x006a:
        r2 = "null";	 Catch:{ all -> 0x0093 }
        goto L_0x006f;	 Catch:{ all -> 0x0093 }
    L_0x006d:
        r2 = "non-null";	 Catch:{ all -> 0x0093 }
    L_0x006f:
        r1.append(r2);	 Catch:{ all -> 0x0093 }
        r1 = r1.toString();	 Catch:{ all -> 0x0093 }
        android.util.Log.d(r0, r1);	 Catch:{ all -> 0x0093 }
        r3.checkMpi();	 Catch:{ all -> 0x0093 }
        r0 = r3.mpi;	 Catch:{ all -> 0x0093 }
        r0.prepare();	 Catch:{ all -> 0x0093 }
        r0 = org.antennapod.audio.MediaPlayer.State.PREPARED;	 Catch:{ all -> 0x0093 }
        r3.state = r0;	 Catch:{ all -> 0x0093 }
        r0 = "ReplacementMediaPlayer";	 Catch:{ all -> 0x0093 }
        r1 = "prepare() finished";	 Catch:{ all -> 0x0093 }
        android.util.Log.d(r0, r1);	 Catch:{ all -> 0x0093 }
        r0 = r3.lock;
        r0.unlock();
        return;
    L_0x0093:
        r0 = move-exception;
        r1 = r3.lock;
        r1.unlock();
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.antennapod.audio.MediaPlayer.prepare():void");
    }

    public static boolean isIntentAvailable(Context context, String action) {
        return context.getPackageManager().queryIntentServices(new Intent(action), 65536).size() > 0;
    }

    public static Intent getPrestoServiceIntent(Context context, String action) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentServices(new Intent(action), 65536);
        if (list.size() <= 0) {
            return null;
        }
        ResolveInfo first = (ResolveInfo) list.get(0);
        if (first.serviceInfo != null) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(first.serviceInfo.packageName, first.serviceInfo.name));
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Returning intent:");
            stringBuilder.append(intent.toString());
            Log.i(str, stringBuilder.toString());
            return intent;
        }
        str = TAG;
        stringBuilder = new StringBuilder();
        stringBuilder.append("Found service that accepts ");
        stringBuilder.append(action);
        stringBuilder.append(", but serviceInfo was null");
        Log.e(str, stringBuilder.toString());
        return null;
    }

    public static boolean isPrestoLibraryInstalled(Context context) {
        return isIntentAvailable(context, "com.aocate.intent.PLAY_AUDIO_ADJUST_SPEED_0_8");
    }

    public static Intent getPrestoMarketIntent() {
        if (prestoMarketIntent == null) {
            prestoMarketIntent = new Intent("android.intent.action.VIEW", SPEED_ADJUSTMENT_MARKET_URI);
        }
        return prestoMarketIntent;
    }

    public static void openPrestoMarketIntent(Context context) {
        context.startActivity(getPrestoMarketIntent());
    }

    public MediaPlayer(Context context) {
        this(context, true);
    }

    public MediaPlayer(Context context, boolean useService) {
        this.amp = null;
        this.sbmp = null;
        this.smp = null;
        this.enableSpeedAdjustment = true;
        this.lastKnownPosition = 0;
        this.lock = new ReentrantLock();
        this.mAudioStreamType = 3;
        this.mIsLooping = false;
        this.mLeftVolume = 1.0f;
        this.mPitchStepsAdjustment = 0.0f;
        this.mRightVolume = 1.0f;
        this.mSpeedMultiplier = 1.0f;
        this.mWakeMode = 0;
        this.mpi = null;
        this.pitchAdjustmentAvailable = false;
        this.speedAdjustmentAvailable = false;
        this.mServiceDisconnectedHandler = null;
        this.state = State.INITIALIZED;
        this.stringDataSource = null;
        this.uriDataSource = null;
        this.useService = false;
        this.onBufferingUpdateListener = null;
        this.onCompletionListener = null;
        this.onErrorListener = null;
        this.onInfoListener = null;
        this.onPitchAdjustmentAvailableChangedListener = new C12081();
        this.pitchAdjustmentAvailableChangedListener = null;
        this.onPreparedListener = new C12092();
        this.preparedListener = null;
        this.onSeekCompleteListener = null;
        this.onSpeedAdjustmentAvailableChangedListener = new C12103();
        this.speedAdjustmentAvailableChangedListener = null;
        this.mContext = context;
        this.useService = useService;
        AbstractAudioPlayer androidAudioPlayer = new AndroidAudioPlayer(this, context);
        this.amp = androidAudioPlayer;
        this.mpi = androidAudioPlayer;
        if (VERSION.SDK_INT >= 16) {
            this.smp = new SonicAudioPlayer(this, context);
            this.smp.setDownMix(downmix());
        }
        Log.d(MP_TAG, "setupMpi");
        setupMpi(context);
    }

    protected boolean useSonic() {
        return false;
    }

    protected boolean downmix() {
        return false;
    }

    private boolean invalidServiceConnectionConfiguration() {
        if (this.smp != null) {
            boolean usingSonic = this.mpi instanceof SonicAudioPlayer;
            if (usingSonic) {
                if (useSonic()) {
                }
                return true;
            }
            if (!usingSonic && useSonic()) {
                return true;
            }
        }
        if (this.mpi instanceof ServiceBackedAudioPlayer) {
            if (this.useService && isPrestoLibraryInstalled()) {
                Log.d(MP_TAG, "We could be using a ServiceBackedMediaPlayer and we are");
                return false;
            }
            Log.d(MP_TAG, "We're trying to use a ServiceBackedMediaPlayer but we shouldn't be");
            return true;
        } else if (this.useService && isPrestoLibraryInstalled()) {
            Log.d(MP_TAG, "We could be using the service, but we're not");
            return true;
        } else {
            Log.d(MP_TAG, "this.mpi is not a ServiceBackedMediaPlayer, but we couldn't use it anyway");
            return false;
        }
    }

    public boolean canFallback() {
        AbstractAudioPlayer abstractAudioPlayer = this.mpi;
        if (abstractAudioPlayer != null) {
            if (abstractAudioPlayer instanceof AndroidAudioPlayer) {
                return false;
            }
        }
        return true;
    }

    public void fallback() {
        this.smp = null;
        setupMpi(this.mpi.mContext);
    }

    protected void checkMpi() {
        if (invalidServiceConnectionConfiguration()) {
            setupMpi(this.mpi.mContext);
        }
    }

    private void setupMpi(Context context) {
        this.lock.lock();
        try {
            Log.d(MP_TAG, "setupMpi");
            if (!useSonic() || this.smp == null) {
                if (this.useService && isPrestoLibraryInstalled()) {
                    if (this.mpi == null || !(this.mpi instanceof ServiceBackedAudioPlayer)) {
                        if (this.sbmp == null) {
                            Log.d(MP_TAG, "Instantiating new ServiceBackedMediaPlayer");
                            this.sbmp = new ServiceBackedAudioPlayer(this, context, new C11414());
                        }
                        Log.d(MP_TAG, "Switching to ServiceBackedMediaPlayer");
                        switchMediaPlayerImpl(this.mpi, this.sbmp);
                    } else {
                        Log.d(MP_TAG, "Already using ServiceBackedMediaPlayer");
                        this.lock.unlock();
                        return;
                    }
                } else if (this.mpi == null || !(this.mpi instanceof AndroidAudioPlayer)) {
                    if (this.amp == null) {
                        Log.d(MP_TAG, "Instantiating new AndroidMediaPlayer (this should be impossible)");
                        this.amp = new AndroidAudioPlayer(this, context);
                    }
                    switchMediaPlayerImpl(this.mpi, this.amp);
                } else {
                    Log.d(MP_TAG, "Already using AndroidMediaPlayer");
                    this.lock.unlock();
                    return;
                }
                this.lock.unlock();
            } else if (this.mpi == null || !(this.mpi instanceof SonicAudioPlayer)) {
                Log.d(MP_TAG, "Switching to SonicMediaPlayer");
                switchMediaPlayerImpl(this.mpi, this.smp);
                this.lock.unlock();
            } else {
                Log.d(MP_TAG, "Already using SonicMediaPlayer");
            }
        } finally {
            this.lock.unlock();
        }
    }

    private void switchMediaPlayerImpl(AbstractAudioPlayer from, AbstractAudioPlayer to) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("switchMediaPlayerImpl() called with: from = [");
        stringBuilder.append(from);
        stringBuilder.append("], to = [");
        stringBuilder.append(to);
        stringBuilder.append("]");
        Log.d(str, stringBuilder.toString());
        this.lock.lock();
        try {
            Log.d(MP_TAG, "switchMediaPlayerImpl");
            if (from != to && to != null) {
                if (to instanceof ServiceBackedAudioPlayer) {
                    if (((ServiceBackedAudioPlayer) to).isConnected()) {
                    }
                }
                if (this.state != State.END) {
                    str = MP_TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("switchMediaPlayerImpl(), current state is ");
                    stringBuilder.append(this.state.toString());
                    Log.d(str, stringBuilder.toString());
                    to.reset();
                    to.setEnableSpeedAdjustment(this.enableSpeedAdjustment);
                    to.setAudioStreamType(this.mAudioStreamType);
                    to.setLooping(this.mIsLooping);
                    str = MP_TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Setting playback speed to ");
                    stringBuilder.append(this.mSpeedMultiplier);
                    Log.d(str, stringBuilder.toString());
                    to.setVolume(this.mLeftVolume, this.mRightVolume);
                    to.setWakeMode(this.mContext, this.mWakeMode);
                    Log.d(MP_TAG, "asserting at least one data source is null");
                    if (this.uriDataSource != null) {
                        Log.d(MP_TAG, "switchMediaPlayerImpl(): uriDataSource != null");
                        to.setDataSource(this.mContext, this.uriDataSource);
                    }
                    if (this.stringDataSource != null) {
                        Log.d(MP_TAG, "switchMediaPlayerImpl(): stringDataSource != null");
                        try {
                            to.setDataSource(this.stringDataSource);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    to.setPitchStepsAdjustment(this.mPitchStepsAdjustment);
                    to.setPlaybackSpeed(this.mSpeedMultiplier);
                    if (!(this.state == State.PREPARED || this.state == State.PREPARING || this.state == State.PAUSED || this.state == State.STOPPED || this.state == State.STARTED)) {
                        if (this.state != State.PLAYBACK_COMPLETED) {
                            if (from == null && from.isPlaying()) {
                                from.pause();
                            }
                            if (!(this.state == State.STARTED || this.state == State.PAUSED)) {
                                if (this.state == State.STOPPED) {
                                    if (this.state != State.PAUSED) {
                                        Log.d(MP_TAG, "switchMediaPlayerImpl(): paused");
                                        to.pause();
                                    } else if (this.state != State.STOPPED) {
                                        Log.d(MP_TAG, "switchMediaPlayerImpl(): stopped");
                                        to.stop();
                                    }
                                    this.mpi = to;
                                    str = TAG;
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("Switched to ");
                                    stringBuilder.append(to.getClass().toString());
                                    Log.d(str, stringBuilder.toString());
                                    if (to.canSetPitch() == this.pitchAdjustmentAvailable && this.onPitchAdjustmentAvailableChangedListener != null) {
                                        this.onPitchAdjustmentAvailableChangedListener.onPitchAdjustmentAvailableChanged(this, to.canSetPitch());
                                    }
                                    if (to.canSetSpeed() == this.speedAdjustmentAvailable && this.onSpeedAdjustmentAvailableChangedListener != null) {
                                        this.onSpeedAdjustmentAvailableChangedListener.onSpeedAdjustmentAvailableChanged(this, to.canSetSpeed());
                                    }
                                    str = MP_TAG;
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("switchMediaPlayerImpl() ");
                                    stringBuilder.append(this.state.toString());
                                    Log.d(str, stringBuilder.toString());
                                    this.lock.unlock();
                                    return;
                                }
                            }
                            Log.d(MP_TAG, "switchMediaPlayerImpl(): start");
                            to.start();
                            if (this.state != State.PAUSED) {
                                Log.d(MP_TAG, "switchMediaPlayerImpl(): paused");
                                to.pause();
                            } else if (this.state != State.STOPPED) {
                                Log.d(MP_TAG, "switchMediaPlayerImpl(): stopped");
                                to.stop();
                            }
                            this.mpi = to;
                            str = TAG;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Switched to ");
                            stringBuilder.append(to.getClass().toString());
                            Log.d(str, stringBuilder.toString());
                            if (to.canSetPitch() == this.pitchAdjustmentAvailable) {
                            }
                            if (to.canSetSpeed() == this.speedAdjustmentAvailable) {
                            }
                            str = MP_TAG;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("switchMediaPlayerImpl() ");
                            stringBuilder.append(this.state.toString());
                            Log.d(str, stringBuilder.toString());
                            this.lock.unlock();
                            return;
                        }
                    }
                    Log.d(MP_TAG, "switchMediaPlayerImpl(): prepare and seek");
                    try {
                        to.muteNextOnPrepare();
                        to.prepare();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    int seekPos = 0;
                    if (from != null) {
                        seekPos = from.getCurrentPosition();
                    } else if (this.lastKnownPosition < to.getDuration()) {
                        seekPos = this.lastKnownPosition;
                    }
                    if (seekPos > 0) {
                        to.muteNextSeek();
                        to.seekTo(seekPos);
                    }
                    if (from == null) {
                    }
                    if (this.state == State.STOPPED) {
                        if (this.state != State.PAUSED) {
                            Log.d(MP_TAG, "switchMediaPlayerImpl(): paused");
                            to.pause();
                        } else if (this.state != State.STOPPED) {
                            Log.d(MP_TAG, "switchMediaPlayerImpl(): stopped");
                            to.stop();
                        }
                        this.mpi = to;
                        str = TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Switched to ");
                        stringBuilder.append(to.getClass().toString());
                        Log.d(str, stringBuilder.toString());
                        if (to.canSetPitch() == this.pitchAdjustmentAvailable) {
                        }
                        if (to.canSetSpeed() == this.speedAdjustmentAvailable) {
                        }
                        str = MP_TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("switchMediaPlayerImpl() ");
                        stringBuilder.append(this.state.toString());
                        Log.d(str, stringBuilder.toString());
                        this.lock.unlock();
                        return;
                    }
                    Log.d(MP_TAG, "switchMediaPlayerImpl(): start");
                    to.start();
                    if (this.state != State.PAUSED) {
                        Log.d(MP_TAG, "switchMediaPlayerImpl(): paused");
                        to.pause();
                    } else if (this.state != State.STOPPED) {
                        Log.d(MP_TAG, "switchMediaPlayerImpl(): stopped");
                        to.stop();
                    }
                    this.mpi = to;
                    str = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Switched to ");
                    stringBuilder.append(to.getClass().toString());
                    Log.d(str, stringBuilder.toString());
                    if (to.canSetPitch() == this.pitchAdjustmentAvailable) {
                    }
                    if (to.canSetSpeed() == this.speedAdjustmentAvailable) {
                    }
                    str = MP_TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("switchMediaPlayerImpl() ");
                    stringBuilder.append(this.state.toString());
                    Log.d(str, stringBuilder.toString());
                    this.lock.unlock();
                    return;
                }
            }
            this.lock.unlock();
        } catch (IOException e3) {
            e3.printStackTrace();
        } catch (Throwable th) {
            this.lock.unlock();
        }
    }

    public boolean canSetPitch() {
        this.lock.lock();
        try {
            boolean canSetPitch = this.mpi.canSetPitch();
            return canSetPitch;
        } finally {
            this.lock.unlock();
        }
    }

    public boolean canSetSpeed() {
        this.lock.lock();
        try {
            boolean canSetSpeed = this.mpi.canSetSpeed();
            return canSetSpeed;
        } finally {
            this.lock.unlock();
        }
    }

    public boolean canDownmix() {
        this.lock.lock();
        try {
            boolean canDownmix = this.mpi.canDownmix();
            return canDownmix;
        } finally {
            this.lock.unlock();
        }
    }

    protected void finalize() throws Throwable {
        this.lock.lock();
        try {
            Log.d(MP_TAG, "finalize()");
            release();
        } finally {
            this.lock.unlock();
        }
    }

    public float getCurrentPitchStepsAdjustment() {
        this.lock.lock();
        try {
            float currentPitchStepsAdjustment = this.mpi.getCurrentPitchStepsAdjustment();
            return currentPitchStepsAdjustment;
        } finally {
            this.lock.unlock();
        }
    }

    public int getCurrentPosition() {
        this.lock.lock();
        try {
            int currentPosition = this.mpi.getCurrentPosition();
            this.lastKnownPosition = currentPosition;
            return currentPosition;
        } finally {
            this.lock.unlock();
        }
    }

    public float getCurrentSpeedMultiplier() {
        this.lock.lock();
        try {
            float currentSpeedMultiplier = this.mpi.getCurrentSpeedMultiplier();
            return currentSpeedMultiplier;
        } finally {
            this.lock.unlock();
        }
    }

    public int getDuration() {
        this.lock.lock();
        try {
            int duration = this.mpi.getDuration();
            return duration;
        } finally {
            this.lock.unlock();
        }
    }

    public float getMaxSpeedMultiplier() {
        this.lock.lock();
        try {
            float maxSpeedMultiplier = this.mpi.getMaxSpeedMultiplier();
            return maxSpeedMultiplier;
        } finally {
            this.lock.unlock();
        }
    }

    public float getMinSpeedMultiplier() {
        this.lock.lock();
        try {
            float minSpeedMultiplier = this.mpi.getMinSpeedMultiplier();
            return minSpeedMultiplier;
        } finally {
            this.lock.unlock();
        }
    }

    public int getServiceVersionCode() {
        this.lock.lock();
        try {
            if (this.mpi instanceof ServiceBackedAudioPlayer) {
                int serviceVersionCode = ((ServiceBackedAudioPlayer) this.mpi).getServiceVersionCode();
                return serviceVersionCode;
            }
            this.lock.unlock();
            return -1;
        } finally {
            this.lock.unlock();
        }
    }

    public String getServiceVersionName() {
        this.lock.lock();
        try {
            if (this.mpi instanceof ServiceBackedAudioPlayer) {
                String serviceVersionName = ((ServiceBackedAudioPlayer) this.mpi).getServiceVersionName();
                return serviceVersionName;
            }
            this.lock.unlock();
            return null;
        } finally {
            this.lock.unlock();
        }
    }

    public boolean isLooping() {
        this.lock.lock();
        try {
            boolean isLooping = this.mpi.isLooping();
            return isLooping;
        } finally {
            this.lock.unlock();
        }
    }

    public boolean isPlaying() {
        this.lock.lock();
        try {
            boolean isPlaying = this.mpi.isPlaying();
            return isPlaying;
        } finally {
            this.lock.unlock();
        }
    }

    public boolean isPrestoLibraryInstalled() {
        AbstractAudioPlayer abstractAudioPlayer = this.mpi;
        if (abstractAudioPlayer != null) {
            if (abstractAudioPlayer.mContext != null) {
                return isPrestoLibraryInstalled(this.mpi.mContext);
            }
        }
        return false;
    }

    public void openPrestoMarketIntent() {
        AbstractAudioPlayer abstractAudioPlayer = this.mpi;
        if (abstractAudioPlayer != null && abstractAudioPlayer.mContext != null) {
            openPrestoMarketIntent(this.mpi.mContext);
        }
    }

    public void pause() {
        this.lock.lock();
        try {
            checkMpi();
            this.state = State.PAUSED;
            this.mpi.pause();
        } finally {
            this.lock.unlock();
        }
    }

    public void prepareAsync() {
        this.lock.lock();
        try {
            Log.d(MP_TAG, "prepareAsync()");
            checkMpi();
            this.state = State.PREPARING;
            this.mpi.prepareAsync();
        } finally {
            this.lock.unlock();
        }
    }

    public void release() {
        this.lock.lock();
        try {
            Log.d(MP_TAG, "Releasing MediaPlayer");
            this.state = State.END;
            if (this.amp != null) {
                this.amp.release();
            }
            if (this.sbmp != null) {
                this.sbmp.release();
            }
            this.onBufferingUpdateListener = null;
            this.onCompletionListener = null;
            this.onErrorListener = null;
            this.onInfoListener = null;
            this.preparedListener = null;
            this.onPitchAdjustmentAvailableChangedListener = null;
            this.pitchAdjustmentAvailableChangedListener = null;
            Log.d(MP_TAG, "Setting onSeekCompleteListener to null 871");
            this.onSeekCompleteListener = null;
            this.onSpeedAdjustmentAvailableChangedListener = null;
            this.speedAdjustmentAvailableChangedListener = null;
        } finally {
            this.lock.unlock();
        }
    }

    public void reset() {
        this.lock.lock();
        try {
            this.state = State.IDLE;
            this.stringDataSource = null;
            this.uriDataSource = null;
            this.mpi.reset();
        } finally {
            this.lock.unlock();
        }
    }

    public void seekTo(int msec) throws IllegalStateException {
        this.lock.lock();
        try {
            this.mpi.seekTo(msec);
        } finally {
            this.lock.unlock();
        }
    }

    public void setAudioStreamType(int streamtype) {
        this.lock.lock();
        try {
            this.mAudioStreamType = streamtype;
            this.mpi.setAudioStreamType(streamtype);
        } finally {
            this.lock.unlock();
        }
    }

    public void setDataSource(Context context, Uri uri) throws IllegalArgumentException, IllegalStateException, IOException {
        this.lock.lock();
        try {
            String str = MP_TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("In setDataSource(context, ");
            stringBuilder.append(uri.toString());
            stringBuilder.append("), using ");
            stringBuilder.append(this.mpi.getClass().toString());
            Log.d(str, stringBuilder.toString());
            checkMpi();
            this.state = State.INITIALIZED;
            this.stringDataSource = null;
            this.uriDataSource = uri;
            this.mpi.setDataSource(context, uri);
        } finally {
            this.lock.unlock();
        }
    }

    public void setDataSource(String path) throws IllegalArgumentException, IllegalStateException, IOException {
        this.lock.lock();
        try {
            String str = MP_TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("In setDataSource(context, ");
            stringBuilder.append(path);
            stringBuilder.append(")");
            Log.d(str, stringBuilder.toString());
            checkMpi();
            this.state = State.INITIALIZED;
            this.stringDataSource = path;
            this.uriDataSource = null;
            this.mpi.setDataSource(path);
        } finally {
            this.lock.unlock();
        }
    }

    public void setEnableSpeedAdjustment(boolean enableSpeedAdjustment) {
        this.lock.lock();
        try {
            this.enableSpeedAdjustment = enableSpeedAdjustment;
            this.mpi.setEnableSpeedAdjustment(enableSpeedAdjustment);
        } finally {
            this.lock.unlock();
        }
    }

    public void setLooping(boolean loop) {
        this.lock.lock();
        try {
            this.mIsLooping = loop;
            this.mpi.setLooping(loop);
        } finally {
            this.lock.unlock();
        }
    }

    public void setPitchStepsAdjustment(float pitchSteps) {
        this.lock.lock();
        try {
            this.mPitchStepsAdjustment = pitchSteps;
            this.mpi.setPitchStepsAdjustment(pitchSteps);
        } finally {
            this.lock.unlock();
        }
    }

    private static float getPitchStepsAdjustment(float pitch) {
        return (float) (Math.log((double) pitch) / (Math.log(PITCH_STEP_CONSTANT) * 2.0d));
    }

    public void setPlaybackPitch(float pitch) {
        this.lock.lock();
        try {
            this.mPitchStepsAdjustment = getPitchStepsAdjustment(pitch);
            this.mpi.setPlaybackPitch(pitch);
        } finally {
            this.lock.unlock();
        }
    }

    public void setPlaybackSpeed(float f) {
        this.lock.lock();
        try {
            this.mSpeedMultiplier = f;
            this.mpi.setPlaybackSpeed(f);
        } finally {
            this.lock.unlock();
        }
    }

    public void setDownmix(boolean enable) {
        this.lock.lock();
        try {
            this.mpi.setDownmix(enable);
        } finally {
            this.lock.unlock();
        }
    }

    public void setVolume(float leftVolume, float rightVolume) {
        this.lock.lock();
        try {
            this.mLeftVolume = leftVolume;
            this.mRightVolume = rightVolume;
            this.mpi.setVolume(leftVolume, rightVolume);
        } finally {
            this.lock.unlock();
        }
    }

    public void setWakeMode(Context context, int mode) {
        this.lock.lock();
        try {
            this.mWakeMode = mode;
            this.mpi.setWakeMode(context, mode);
        } finally {
            this.lock.unlock();
        }
    }

    public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {
        this.lock.lock();
        try {
            this.onBufferingUpdateListener = listener;
        } finally {
            this.lock.unlock();
        }
    }

    public void setOnCompletionListener(OnCompletionListener listener) {
        this.lock.lock();
        try {
            this.onCompletionListener = listener;
        } finally {
            this.lock.unlock();
        }
    }

    public void setOnErrorListener(OnErrorListener listener) {
        this.lock.lock();
        try {
            this.onErrorListener = listener;
        } finally {
            this.lock.unlock();
        }
    }

    public void setOnInfoListener(OnInfoListener listener) {
        this.lock.lock();
        try {
            this.onInfoListener = listener;
        } finally {
            this.lock.unlock();
        }
    }

    public void setOnPitchAdjustmentAvailableChangedListener(OnPitchAdjustmentAvailableChangedListener listener) {
        this.lock.lock();
        try {
            this.pitchAdjustmentAvailableChangedListener = listener;
        } finally {
            this.lock.unlock();
        }
    }

    public void setOnPreparedListener(OnPreparedListener listener) {
        this.lock.lock();
        Log.d(MP_TAG, " ++++++++++++++++++++++++++++++++++++++++++++ setOnPreparedListener");
        try {
            this.preparedListener = listener;
        } finally {
            this.lock.unlock();
        }
    }

    public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
        this.lock.lock();
        try {
            this.onSeekCompleteListener = listener;
        } finally {
            this.lock.unlock();
        }
    }

    public void setOnSpeedAdjustmentAvailableChangedListener(OnSpeedAdjustmentAvailableChangedListener listener) {
        this.lock.lock();
        try {
            this.speedAdjustmentAvailableChangedListener = listener;
        } finally {
            this.lock.unlock();
        }
    }

    public void start() {
        this.lock.lock();
        try {
            Log.d(MP_TAG, "start()");
            checkMpi();
            this.state = State.STARTED;
            this.mpi.start();
        } finally {
            this.lock.unlock();
        }
    }

    public void stop() {
        this.lock.lock();
        try {
            checkMpi();
            this.state = State.STOPPED;
            this.mpi.stop();
        } finally {
            this.lock.unlock();
        }
    }
}
