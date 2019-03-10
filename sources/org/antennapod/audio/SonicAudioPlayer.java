package org.antennapod.audio;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import org.vinuxproject.sonic.Sonic;

@TargetApi(16)
public class SonicAudioPlayer extends AbstractAudioPlayer {
    private static final String TAG = SonicAudioPlayer.class.getSimpleName();
    private static final String TAG_TRACK = "SonicTrack";
    private int mBufferSize;
    private MediaCodec mCodec;
    private final Context mContext;
    private boolean mContinue = false;
    private float mCurrentPitch = 1.0f;
    private float mCurrentSpeed = 1.0f;
    private final Object mDecoderLock;
    private Thread mDecoderThread;
    private boolean mDownMix;
    private long mDuration;
    private MediaExtractor mExtractor;
    private AtomicInteger mInitiatingCount = new AtomicInteger(0);
    private boolean mIsDecoding = false;
    private final ReentrantLock mLock;
    private String mPath;
    private AtomicInteger mSeekingCount = new AtomicInteger(0);
    private Sonic mSonic;
    private AudioTrack mTrack;
    private Uri mUri;
    private WakeLock mWakeLock = null;
    private final SonicAudioPlayerState state = new SonicAudioPlayerState();

    /* renamed from: org.antennapod.audio.SonicAudioPlayer$1 */
    class C11441 implements Runnable {
        C11441() {
        }

        public void run() {
            SonicAudioPlayer.this.doPrepare();
        }
    }

    /* renamed from: org.antennapod.audio.SonicAudioPlayer$3 */
    class C11473 implements Runnable {
        private int currHeadPos;

        /* renamed from: org.antennapod.audio.SonicAudioPlayer$3$1 */
        class C11461 implements Runnable {
            C11461() {
            }

            public void run() {
                SonicAudioPlayer.this.owningMediaPlayer.onCompletionListener.onCompletion(SonicAudioPlayer.this.owningMediaPlayer);
            }
        }

        C11473() {
        }

        public void run() {
            SonicAudioPlayer.this.mIsDecoding = true;
            SonicAudioPlayer.this.mCodec.start();
            ByteBuffer[] inputBuffers = SonicAudioPlayer.this.mCodec.getInputBuffers();
            boolean sawInputEOS = false;
            boolean sawOutputEOS = false;
            ByteBuffer[] outputBuffers = SonicAudioPlayer.this.mCodec.getOutputBuffers();
            while (!sawInputEOS && !sawOutputEOS && SonicAudioPlayer.this.mContinue) {
                r1.currHeadPos = SonicAudioPlayer.this.mTrack.getPlaybackHeadPosition();
                if (SonicAudioPlayer.this.state.is(5)) {
                    System.out.println("Decoder changed to PAUSED");
                    try {
                        synchronized (SonicAudioPlayer.this.mDecoderLock) {
                            SonicAudioPlayer.this.mDecoderLock.wait();
                            System.out.println("Done with wait");
                        }
                    } catch (InterruptedException e) {
                    }
                } else {
                    boolean z;
                    if (SonicAudioPlayer.this.mSonic != null) {
                        SonicAudioPlayer.this.mSonic.setSpeed(SonicAudioPlayer.this.mCurrentSpeed);
                        SonicAudioPlayer.this.mSonic.setPitch(SonicAudioPlayer.this.mCurrentPitch);
                    }
                    long j = 200;
                    int inputBufIndex = SonicAudioPlayer.this.mCodec.dequeueInputBuffer(200);
                    if (inputBufIndex >= 0) {
                        long presentationTimeUs;
                        ByteBuffer dstBuf = inputBuffers[inputBufIndex];
                        boolean sampleSize = SonicAudioPlayer.this.mExtractor.readSampleData(dstBuf, 0);
                        if (sampleSize >= false) {
                            z = true;
                            sawInputEOS = false;
                            presentationTimeUs = 0;
                        } else {
                            z = sawInputEOS;
                            sawInputEOS = sampleSize;
                            presentationTimeUs = SonicAudioPlayer.this.mExtractor.getSampleTime();
                        }
                        SonicAudioPlayer.this.mCodec.queueInputBuffer(inputBufIndex, 0, sawInputEOS, presentationTimeUs, z ? 4 : 0);
                        if (!z) {
                            SonicAudioPlayer.this.mExtractor.advance();
                        }
                    } else {
                        z = sawInputEOS;
                    }
                    BufferInfo info = new BufferInfo();
                    byte[] modifiedSamples = new byte[info.size];
                    while (true) {
                        int res = SonicAudioPlayer.this.mCodec.dequeueOutputBuffer(info, j);
                        int available;
                        if (res >= 0) {
                            int outputBufIndex = res;
                            byte[] chunk = new byte[info.size];
                            outputBuffers[res].get(chunk);
                            outputBuffers[res].clear();
                            if (chunk.length > 0) {
                                SonicAudioPlayer.this.mSonic.writeBytesToStream(chunk, chunk.length);
                            } else {
                                SonicAudioPlayer.this.mSonic.flushStream();
                            }
                            available = SonicAudioPlayer.this.mSonic.samplesAvailable();
                            if (available > 0) {
                                if (modifiedSamples.length < available) {
                                    modifiedSamples = new byte[available];
                                }
                                if (SonicAudioPlayer.this.mDownMix && SonicAudioPlayer.this.mSonic.getNumChannels() == 2) {
                                    int maxBytes = (available / 4) * 4;
                                    SonicAudioPlayer.this.mSonic.readBytesFromStream(modifiedSamples, maxBytes);
                                    DownMixer.downMix(modifiedSamples);
                                    SonicAudioPlayer.this.mTrack.write(modifiedSamples, 0, maxBytes);
                                } else {
                                    SonicAudioPlayer.this.mSonic.readBytesFromStream(modifiedSamples, available);
                                    SonicAudioPlayer.this.mTrack.write(modifiedSamples, 0, available);
                                }
                            }
                            SonicAudioPlayer.this.mCodec.releaseOutputBuffer(outputBufIndex, false);
                            if ((info.flags & 4) != 0) {
                                sawOutputEOS = true;
                            }
                        } else if (res == -3) {
                            ByteBuffer[] outputBuffers2 = SonicAudioPlayer.this.mCodec.getOutputBuffers();
                            Log.d("PCM", "Output buffers changed");
                            outputBuffers = outputBuffers2;
                        } else if (res == -2) {
                            MediaFormat oFormat = SonicAudioPlayer.this.mCodec.getOutputFormat();
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Output format has changed to ");
                            stringBuilder.append(oFormat);
                            Log.d("PCM", stringBuilder.toString());
                            available = oFormat.getInteger("sample-rate");
                            int channelCount = oFormat.getInteger("channel-count");
                            if (available == SonicAudioPlayer.this.mSonic.getSampleRate()) {
                                if (channelCount != SonicAudioPlayer.this.mSonic.getNumChannels()) {
                                }
                            }
                            SonicAudioPlayer.this.mTrack.stop();
                            SonicAudioPlayer.this.mLock.lock();
                            SonicAudioPlayer.this.mTrack.release();
                            SonicAudioPlayer.this.initDevice(available, channelCount);
                            outputBuffers = SonicAudioPlayer.this.mCodec.getOutputBuffers();
                            SonicAudioPlayer.this.mTrack.play();
                            SonicAudioPlayer.this.mLock.unlock();
                        }
                        if (res != -3 && res != -2) {
                            break;
                        }
                        j = 200;
                    }
                    sawInputEOS = z;
                }
            }
            Log.d(SonicAudioPlayer.TAG_TRACK, "Decoding loop exited. Stopping codec and track");
            String str = SonicAudioPlayer.TAG_TRACK;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Duration: ");
            stringBuilder2.append((int) (SonicAudioPlayer.this.mDuration / 1000));
            Log.d(str, stringBuilder2.toString());
            if (SonicAudioPlayer.this.mInitiatingCount.get() <= 0 && SonicAudioPlayer.this.mSeekingCount.get() <= 0) {
                str = SonicAudioPlayer.TAG_TRACK;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Current position: ");
                stringBuilder2.append(SonicAudioPlayer.this.getCurrentPosition());
                Log.d(str, stringBuilder2.toString());
            }
            SonicAudioPlayer.this.mCodec.stop();
            while (true) {
                maxBytes = r1.currHeadPos;
                try {
                    Thread.sleep(100);
                    r1.currHeadPos = SonicAudioPlayer.this.mTrack.getPlaybackHeadPosition();
                } catch (InterruptedException e2) {
                }
                if (r1.currHeadPos == maxBytes) {
                    break;
                }
            }
            SonicAudioPlayer.this.mTrack.stop();
            Log.d(SonicAudioPlayer.TAG_TRACK, "Stopped codec and track");
            if (SonicAudioPlayer.this.mInitiatingCount.get() <= 0 && SonicAudioPlayer.this.mSeekingCount.get() <= 0) {
                str = SonicAudioPlayer.TAG_TRACK;
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append("Current position: ");
                stringBuilder3.append(SonicAudioPlayer.this.getCurrentPosition());
                Log.d(str, stringBuilder3.toString());
            }
            SonicAudioPlayer.this.mIsDecoding = false;
            if (SonicAudioPlayer.this.mContinue && (sawInputEOS || sawOutputEOS)) {
                SonicAudioPlayer.this.state.changeTo(7);
                if (SonicAudioPlayer.this.owningMediaPlayer.onCompletionListener != null) {
                    Thread t = new Thread(new C11461());
                    t.setDaemon(true);
                    t.start();
                }
            } else {
                Log.d(SonicAudioPlayer.TAG_TRACK, "Loop ended before saw input eos or output eos");
                str = SonicAudioPlayer.TAG_TRACK;
                StringBuilder stringBuilder4 = new StringBuilder();
                stringBuilder4.append("sawInputEOS: ");
                stringBuilder4.append(sawInputEOS);
                Log.d(str, stringBuilder4.toString());
                str = SonicAudioPlayer.TAG_TRACK;
                stringBuilder4 = new StringBuilder();
                stringBuilder4.append("sawOutputEOS: ");
                stringBuilder4.append(sawOutputEOS);
                Log.d(str, stringBuilder4.toString());
            }
            synchronized (SonicAudioPlayer.this.mDecoderLock) {
                SonicAudioPlayer.this.mDecoderLock.notifyAll();
            }
        }
    }

    /* renamed from: org.antennapod.audio.SonicAudioPlayer$4 */
    class C11484 implements UncaughtExceptionHandler {
        C11484() {
        }

        public void uncaughtException(Thread thread, Throwable ex) {
            Log.e(SonicAudioPlayer.TAG_TRACK, Log.getStackTraceString(ex));
            SonicAudioPlayer.this.error();
        }
    }

    private android.media.AudioTrack createAudioTrack(int r13, int r14, int r15) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:18:0x0034 in {8, 10, 13, 14, 15, 17} preds:[]
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
        r12 = this;
        r0 = 4;
    L_0x0001:
        r1 = 1;
        if (r0 < r1) goto L_0x002c;
    L_0x0004:
        r9 = r15 * r0;
        r10 = 0;
        r11 = new android.media.AudioTrack;	 Catch:{ IllegalArgumentException -> 0x0021 }
        r3 = 3;	 Catch:{ IllegalArgumentException -> 0x0021 }
        r6 = 2;	 Catch:{ IllegalArgumentException -> 0x0021 }
        r8 = 1;	 Catch:{ IllegalArgumentException -> 0x0021 }
        r2 = r11;	 Catch:{ IllegalArgumentException -> 0x0021 }
        r4 = r13;	 Catch:{ IllegalArgumentException -> 0x0021 }
        r5 = r14;	 Catch:{ IllegalArgumentException -> 0x0021 }
        r7 = r9;	 Catch:{ IllegalArgumentException -> 0x0021 }
        r2.<init>(r3, r4, r5, r6, r7, r8);	 Catch:{ IllegalArgumentException -> 0x0021 }
        r10 = r11;	 Catch:{ IllegalArgumentException -> 0x0021 }
        r2 = r10.getState();	 Catch:{ IllegalArgumentException -> 0x0021 }
        if (r2 != r1) goto L_0x001d;	 Catch:{ IllegalArgumentException -> 0x0021 }
    L_0x001a:
        r12.mBufferSize = r9;	 Catch:{ IllegalArgumentException -> 0x0021 }
        return r10;	 Catch:{ IllegalArgumentException -> 0x0021 }
    L_0x001d:
        r10.release();	 Catch:{ IllegalArgumentException -> 0x0021 }
        goto L_0x0029;
    L_0x0021:
        r1 = move-exception;
        if (r10 == 0) goto L_0x0028;
    L_0x0024:
        r10.release();
        goto L_0x0029;
    L_0x0029:
        r0 = r0 + -1;
        goto L_0x0001;
    L_0x002c:
        r0 = new java.lang.IllegalStateException;
        r1 = "Could not create buffer for AudioTrack";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.antennapod.audio.SonicAudioPlayer.createAudioTrack(int, int, int):android.media.AudioTrack");
    }

    private boolean initStream() throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:48:0x014c in {4, 7, 14, 24, 25, 26, 31, 33, 35, 37, 39, 41, 44, 47} preds:[]
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
        r13 = this;
        r0 = r13.currentPath();
        r1 = r13.mInitiatingCount;
        r1.incrementAndGet();
        r1 = new android.media.MediaExtractor;	 Catch:{ all -> 0x0145 }
        r1.<init>();	 Catch:{ all -> 0x0145 }
        r13.mExtractor = r1;	 Catch:{ all -> 0x0145 }
        r1 = r13.mPath;	 Catch:{ all -> 0x0145 }
        r2 = 0;	 Catch:{ all -> 0x0145 }
        if (r1 == 0) goto L_0x001d;	 Catch:{ all -> 0x0145 }
    L_0x0015:
        r1 = r13.mExtractor;	 Catch:{ all -> 0x0145 }
        r3 = r13.mPath;	 Catch:{ all -> 0x0145 }
        r1.setDataSource(r3);	 Catch:{ all -> 0x0145 }
        goto L_0x002a;	 Catch:{ all -> 0x0145 }
    L_0x001d:
        r1 = r13.mUri;	 Catch:{ all -> 0x0145 }
        if (r1 == 0) goto L_0x013d;	 Catch:{ all -> 0x0145 }
    L_0x0021:
        r1 = r13.mExtractor;	 Catch:{ all -> 0x0145 }
        r3 = r13.mContext;	 Catch:{ all -> 0x0145 }
        r4 = r13.mUri;	 Catch:{ all -> 0x0145 }
        r1.setDataSource(r3, r4, r2);	 Catch:{ all -> 0x0145 }
    L_0x002a:
        r1 = r13.mInitiatingCount;
        r1.decrementAndGet();
        r1 = r13.currentPath();
        r3 = 0;
        if (r1 == 0) goto L_0x013b;
    L_0x0037:
        r4 = r1.equals(r0);
        if (r4 == 0) goto L_0x013b;
    L_0x003d:
        r4 = r13.state;
        r5 = 9;
        r4 = r4.is(r5);
        if (r4 == 0) goto L_0x0049;
    L_0x0047:
        goto L_0x013b;
    L_0x0049:
        r4 = r13.mLock;
        r4.lock();
        r4 = r13.mExtractor;
        if (r4 == 0) goto L_0x012e;
    L_0x0052:
        r4 = -1;
        r5 = 0;
    L_0x0054:
        r6 = r13.mExtractor;
        r6 = r6.getTrackCount();
        if (r5 >= r6) goto L_0x007d;
    L_0x005c:
        r6 = r13.mExtractor;
        r6 = r6.getTrackFormat(r5);
        r7 = "mime";
        r7 = r6.getString(r7);
        if (r4 >= 0) goto L_0x0074;
    L_0x006a:
        r8 = "audio/";
        r8 = r7.startsWith(r8);
        if (r8 == 0) goto L_0x0074;
    L_0x0072:
        r4 = r5;
        goto L_0x007a;
        r8 = r13.mExtractor;
        r8.unselectTrack(r5);
    L_0x007a:
        r5 = r5 + 1;
        goto L_0x0054;
    L_0x007d:
        if (r4 < 0) goto L_0x0121;
    L_0x007f:
        r5 = r13.mExtractor;
        r5 = r5.getTrackFormat(r4);
        r6 = "sample-rate";	 Catch:{ Throwable -> 0x010d }
        r6 = r5.getInteger(r6);	 Catch:{ Throwable -> 0x010d }
        r7 = "channel-count";	 Catch:{ Throwable -> 0x010d }
        r7 = r5.getInteger(r7);	 Catch:{ Throwable -> 0x010d }
        r8 = "mime";	 Catch:{ Throwable -> 0x010d }
        r8 = r5.getString(r8);	 Catch:{ Throwable -> 0x010d }
        r9 = "durationUs";	 Catch:{ Throwable -> 0x010d }
        r9 = r5.getLong(r9);	 Catch:{ Throwable -> 0x010d }
        r13.mDuration = r9;	 Catch:{ Throwable -> 0x010d }
        r9 = "SonicTrack";	 Catch:{ Throwable -> 0x010d }
        r10 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x010d }
        r10.<init>();	 Catch:{ Throwable -> 0x010d }
        r11 = "Sample rate: ";	 Catch:{ Throwable -> 0x010d }
        r10.append(r11);	 Catch:{ Throwable -> 0x010d }
        r10.append(r6);	 Catch:{ Throwable -> 0x010d }
        r10 = r10.toString();	 Catch:{ Throwable -> 0x010d }
        android.util.Log.v(r9, r10);	 Catch:{ Throwable -> 0x010d }
        r9 = "SonicTrack";	 Catch:{ Throwable -> 0x010d }
        r10 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x010d }
        r10.<init>();	 Catch:{ Throwable -> 0x010d }
        r11 = "Channel count: ";	 Catch:{ Throwable -> 0x010d }
        r10.append(r11);	 Catch:{ Throwable -> 0x010d }
        r10.append(r7);	 Catch:{ Throwable -> 0x010d }
        r10 = r10.toString();	 Catch:{ Throwable -> 0x010d }
        android.util.Log.v(r9, r10);	 Catch:{ Throwable -> 0x010d }
        r9 = "SonicTrack";	 Catch:{ Throwable -> 0x010d }
        r10 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x010d }
        r10.<init>();	 Catch:{ Throwable -> 0x010d }
        r11 = "Mime type: ";	 Catch:{ Throwable -> 0x010d }
        r10.append(r11);	 Catch:{ Throwable -> 0x010d }
        r10.append(r8);	 Catch:{ Throwable -> 0x010d }
        r10 = r10.toString();	 Catch:{ Throwable -> 0x010d }
        android.util.Log.v(r9, r10);	 Catch:{ Throwable -> 0x010d }
        r9 = "SonicTrack";	 Catch:{ Throwable -> 0x010d }
        r10 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x010d }
        r10.<init>();	 Catch:{ Throwable -> 0x010d }
        r11 = "Duration: ";	 Catch:{ Throwable -> 0x010d }
        r10.append(r11);	 Catch:{ Throwable -> 0x010d }
        r11 = r13.mDuration;	 Catch:{ Throwable -> 0x010d }
        r10.append(r11);	 Catch:{ Throwable -> 0x010d }
        r10 = r10.toString();	 Catch:{ Throwable -> 0x010d }
        android.util.Log.v(r9, r10);	 Catch:{ Throwable -> 0x010d }
        r13.initDevice(r6, r7);	 Catch:{ Throwable -> 0x010d }
        r9 = r13.mExtractor;	 Catch:{ Throwable -> 0x010d }
        r9.selectTrack(r4);	 Catch:{ Throwable -> 0x010d }
        r9 = android.media.MediaCodec.createDecoderByType(r8);	 Catch:{ Throwable -> 0x010d }
        r13.mCodec = r9;	 Catch:{ Throwable -> 0x010d }
        r9 = r13.mCodec;	 Catch:{ Throwable -> 0x010d }
        r9.configure(r5, r2, r2, r3);	 Catch:{ Throwable -> 0x010d }
        goto L_0x011a;
    L_0x010d:
        r2 = move-exception;
        r3 = TAG;
        r6 = android.util.Log.getStackTraceString(r2);
        android.util.Log.e(r3, r6);
        r13.error();
    L_0x011a:
        r2 = r13.mLock;
        r2.unlock();
        r2 = 1;
        return r2;
    L_0x0121:
        r2 = r13.mLock;
        r2.unlock();
        r2 = new java.io.IOException;
        r3 = "No audio track found";
        r2.<init>(r3);
        throw r2;
    L_0x012e:
        r2 = r13.mLock;
        r2.unlock();
        r2 = new java.io.IOException;
        r3 = "Extractor is null";
        r2.<init>(r3);
        throw r2;
        return r3;
    L_0x013d:
        r1 = new java.io.IOException;	 Catch:{ all -> 0x0145 }
        r2 = "Neither path nor uri set";	 Catch:{ all -> 0x0145 }
        r1.<init>(r2);	 Catch:{ all -> 0x0145 }
        throw r1;	 Catch:{ all -> 0x0145 }
    L_0x0145:
        r1 = move-exception;
        r2 = r13.mInitiatingCount;
        r2.decrementAndGet();
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.antennapod.audio.SonicAudioPlayer.initStream():boolean");
    }

    SonicAudioPlayer(MediaPlayer owningMediaPlayer, Context context) {
        super(owningMediaPlayer, context);
        this.mContext = context;
        this.mPath = null;
        this.mUri = null;
        this.mLock = new ReentrantLock();
        this.mDecoderLock = new Object();
        this.mDownMix = false;
    }

    public int getAudioSessionId() {
        AudioTrack audioTrack = this.mTrack;
        if (audioTrack == null) {
            return 0;
        }
        return audioTrack.getAudioSessionId();
    }

    public boolean canSetPitch() {
        return true;
    }

    public boolean canSetSpeed() {
        return true;
    }

    public float getCurrentPitchStepsAdjustment() {
        return this.mCurrentPitch;
    }

    public int getCurrentPosition() {
        if (!(this.state.is(1) || this.state.is(0))) {
            if (!this.state.is(9)) {
                return (int) (this.mExtractor.getSampleTime() / 1000);
            }
        }
        return 0;
    }

    public float getCurrentSpeedMultiplier() {
        return this.mCurrentSpeed;
    }

    public boolean canDownmix() {
        return true;
    }

    public void setDownmix(boolean enable) {
        this.mDownMix = enable;
    }

    public int getDuration() {
        if (!(this.state.is(1) || this.state.is(0))) {
            if (!this.state.is(9)) {
                return (int) (this.mDuration / 1000);
            }
        }
        error();
        return 0;
    }

    public float getMaxSpeedMultiplier() {
        return 4.0f;
    }

    public float getMinSpeedMultiplier() {
        return 0.5f;
    }

    public boolean isLooping() {
        return false;
    }

    public boolean isPlaying() {
        if (!this.state.is(9)) {
            return this.state.is(4);
        }
        error();
        return false;
    }

    public void pause() {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("pause(), current state: ");
        stringBuilder.append(this.state);
        Log.d(str, stringBuilder.toString());
        if (this.state.is(3)) {
            Log.d(TAG_TRACK, "PREPARED, ignore pause()");
        } else if (this.state.is(4) || this.state.is(5)) {
            this.mTrack.pause();
            this.state.changeTo(5);
        } else {
            error();
        }
    }

    public void prepare() {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("prepare(), current state: ");
        stringBuilder.append(this.state);
        Log.d(str, stringBuilder.toString());
        if (this.state.is(1) || this.state.is(6)) {
            doPrepare();
        } else {
            error();
        }
    }

    public void prepareAsync() {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("prepareAsync(), current state: ");
        stringBuilder.append(this.state);
        Log.d(str, stringBuilder.toString());
        if (this.state.is(1) || this.state.is(6)) {
            Thread t = new Thread(new C11441());
            t.setDaemon(true);
            t.start();
            return;
        }
        error();
    }

    private void doPrepare() {
        String lastPath = currentPath();
        this.state.changeTo(2);
        try {
            if (initStream()) {
                if (!this.state.is(9)) {
                    this.state.changeTo(3);
                }
                this.owningMediaPlayer.onPreparedListener.onPrepared(this.owningMediaPlayer);
            }
        } catch (IOException e) {
            String currentPath = currentPath();
            if (currentPath != null) {
                if (!currentPath.equals(lastPath)) {
                }
            }
            Log.e(TAG_TRACK, "Failed setting data source!", e);
            error();
        }
    }

    public void stop() {
        if (this.state.stoppingAllowed()) {
            this.state.changeTo(6);
            this.mContinue = false;
            this.mTrack.pause();
            this.mTrack.flush();
            return;
        }
        error();
        String str = TAG_TRACK;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Stopping in current state ");
        stringBuilder.append(this.state);
        stringBuilder.append(" not allowed");
        Log.d(str, stringBuilder.toString());
    }

    public void start() {
        if (!this.state.is(4)) {
            if (!this.state.is(7)) {
                if (!this.state.is(3)) {
                    if (this.state.is(5)) {
                        this.state.changeTo(4);
                        synchronized (this.mDecoderLock) {
                            this.mDecoderLock.notify();
                        }
                        this.mTrack.play();
                    } else {
                        this.state.changeTo(9);
                        if (this.mTrack != null) {
                            error();
                        } else {
                            Log.d("start", "Attempting to start while in idle after construction. Not allowed by no callbacks called");
                        }
                    }
                }
            }
            if (this.state.is(7)) {
                try {
                    initStream();
                } catch (IOException e) {
                    Log.e(TAG, "initStream() failed");
                    error();
                    return;
                }
            }
            this.state.changeTo(4);
            this.mContinue = true;
            this.mTrack.play();
            decode();
        }
    }

    public void release() {
        reset();
        this.state.changeTo(8);
    }

    public void reset() {
        this.mLock.lock();
        this.mContinue = false;
        try {
            if (this.mDecoderThread != null && !this.state.is(7)) {
                while (this.mIsDecoding) {
                    synchronized (this.mDecoderLock) {
                        this.mDecoderLock.notify();
                        this.mDecoderLock.wait();
                    }
                }
            }
        } catch (InterruptedException e) {
            Log.e(TAG_TRACK, "Interrupted in reset while waiting for decoder thread to stop.", e);
        }
        MediaCodec mediaCodec = this.mCodec;
        if (mediaCodec != null) {
            mediaCodec.release();
            this.mCodec = null;
        }
        MediaExtractor mediaExtractor = this.mExtractor;
        if (mediaExtractor != null) {
            mediaExtractor.release();
            this.mExtractor = null;
        }
        AudioTrack audioTrack = this.mTrack;
        if (audioTrack != null) {
            audioTrack.release();
            this.mTrack = null;
        }
        this.mPath = null;
        this.mUri = null;
        this.mBufferSize = 0;
        this.state.changeTo(0);
        this.mLock.unlock();
    }

    public void seekTo(final int msec) {
        boolean playing = false;
        if (this.state.seekingAllowed()) {
            if (this.state.is(4)) {
                playing = true;
                pause();
            }
            AudioTrack audioTrack = this.mTrack;
            if (audioTrack != null) {
                audioTrack.flush();
                final boolean wasPlaying = playing;
                Runnable seekRunnable = new Runnable() {
                    public void run() {
                        String lastPath = SonicAudioPlayer.this.currentPath();
                        SonicAudioPlayer.this.mSeekingCount.incrementAndGet();
                        try {
                            SonicAudioPlayer.this.mExtractor.seekTo(((long) msec) * 1000, 0);
                            if (SonicAudioPlayer.this.mExtractor != null && lastPath != null && lastPath.equals(SonicAudioPlayer.this.currentPath()) && !SonicAudioPlayer.this.state.is(9)) {
                                String access$600 = SonicAudioPlayer.TAG;
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("seek completed, position: ");
                                stringBuilder.append(SonicAudioPlayer.this.getCurrentPosition());
                                Log.d(access$600, stringBuilder.toString());
                                if (SonicAudioPlayer.this.owningMediaPlayer.onSeekCompleteListener != null) {
                                    SonicAudioPlayer.this.owningMediaPlayer.onSeekCompleteListener.onSeekComplete(SonicAudioPlayer.this.owningMediaPlayer);
                                }
                                if (wasPlaying) {
                                    SonicAudioPlayer.this.start();
                                }
                            }
                        } catch (Exception e) {
                            SonicAudioPlayer.this.error();
                        } finally {
                            SonicAudioPlayer.this.mSeekingCount.decrementAndGet();
                        }
                    }
                };
                if (this.mUri != null) {
                    Thread t = new Thread(seekRunnable);
                    t.setDaemon(true);
                    t.start();
                } else {
                    seekRunnable.run();
                }
                return;
            }
            return;
        }
        error();
        String str = TAG_TRACK;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Seeking in current state ");
        stringBuilder.append(this.state);
        stringBuilder.append(" is not seekable");
        Log.d(str, stringBuilder.toString());
    }

    public void setAudioStreamType(int streamtype) {
    }

    public void setEnableSpeedAdjustment(boolean enableSpeedAdjustment) {
    }

    public void setLooping(boolean loop) {
    }

    public void setPitchStepsAdjustment(float pitchSteps) {
        this.mCurrentPitch += pitchSteps;
    }

    public void setPlaybackPitch(float f) {
        this.mCurrentSpeed = f;
    }

    public void setPlaybackSpeed(float f) {
        this.mCurrentSpeed = f;
    }

    public void setDataSource(String path) {
        if (this.state.settingDataSourceAllowed()) {
            this.mPath = path;
            this.state.changeTo(1);
            return;
        }
        error();
    }

    public void setDataSource(Context context, Uri uri) {
        if (this.state.settingDataSourceAllowed()) {
            this.mUri = uri;
            this.state.changeTo(1);
            return;
        }
        error();
    }

    void setDownMix(boolean downmix) {
        this.mDownMix = downmix;
    }

    public void setVolume(float leftVolume, float rightVolume) {
        AudioTrack audioTrack = this.mTrack;
        if (audioTrack != null) {
            audioTrack.setStereoVolume(leftVolume, rightVolume);
        }
    }

    public void setWakeMode(Context context, int mode) {
        boolean wasHeld = false;
        WakeLock wakeLock = this.mWakeLock;
        if (wakeLock != null) {
            if (wakeLock.isHeld()) {
                wasHeld = true;
                this.mWakeLock.release();
            }
            this.mWakeLock = null;
        }
        if (mode > 0) {
            this.mWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(mode, getClass().getName());
            this.mWakeLock.setReferenceCounted(false);
            if (wasHeld) {
                this.mWakeLock.acquire();
            }
        }
    }

    private void error() {
        error(0);
    }

    private void error(int extra) {
        if (!this.state.is(9)) {
            this.state.changeTo(9);
            if (this.owningMediaPlayer.onErrorListener != null) {
                if (!this.owningMediaPlayer.onErrorListener.onError(this.owningMediaPlayer, 0, extra) && this.owningMediaPlayer.onCompletionListener != null) {
                    this.owningMediaPlayer.onCompletionListener.onCompletion(this.owningMediaPlayer);
                }
            }
        }
    }

    private String currentPath() {
        String str = this.mPath;
        if (str != null) {
            return str;
        }
        Uri uri = this.mUri;
        if (uri != null) {
            return uri.toString();
        }
        return null;
    }

    private void initDevice(int sampleRate, int numChannels) {
        this.mLock.lock();
        int format = findFormatFromChannels(numChannels);
        int oldBufferSize = this.mBufferSize;
        this.mBufferSize = AudioTrack.getMinBufferSize(sampleRate, format, 2);
        if (this.mBufferSize != oldBufferSize) {
            AudioTrack audioTrack = this.mTrack;
            if (audioTrack != null) {
                audioTrack.release();
            }
            this.mTrack = createAudioTrack(sampleRate, format, this.mBufferSize);
        }
        this.mSonic = new Sonic(sampleRate, numChannels);
        this.mLock.unlock();
    }

    private static int findFormatFromChannels(int numChannels) {
        switch (numChannels) {
            case 1:
                return 4;
            case 2:
                return 12;
            case 3:
                return 28;
            case 4:
                return 204;
            case 5:
                return 220;
            case 6:
                return 252;
            case 7:
                return 1276;
            case 8:
                if (VERSION.SDK_INT >= 23) {
                    return 6396;
                }
                return -1;
            default:
                return -1;
        }
    }

    private void decode() {
        this.mDecoderThread = new Thread(new C11473());
        this.mDecoderThread.setUncaughtExceptionHandler(new C11484());
        this.mDecoderThread.setDaemon(true);
        this.mDecoderThread.start();
    }
}
