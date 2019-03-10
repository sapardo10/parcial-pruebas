package com.google.android.exoplayer2.audio;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.ConditionVariable;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.audio.AudioSink.InitializationException;
import com.google.android.exoplayer2.audio.AudioSink.Listener;
import com.google.android.exoplayer2.audio.AudioSink.WriteException;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public final class DefaultAudioSink implements AudioSink {
    private static final int AC3_BUFFER_MULTIPLICATION_FACTOR = 2;
    private static final int BUFFER_MULTIPLICATION_FACTOR = 4;
    private static final int ERROR_BAD_VALUE = -2;
    private static final long MAX_BUFFER_DURATION_US = 750000;
    private static final long MIN_BUFFER_DURATION_US = 250000;
    private static final int MODE_STATIC = 0;
    private static final int MODE_STREAM = 1;
    private static final long PASSTHROUGH_BUFFER_DURATION_US = 250000;
    private static final int START_IN_SYNC = 1;
    private static final int START_NEED_SYNC = 2;
    private static final int START_NOT_SET = 0;
    private static final int STATE_INITIALIZED = 1;
    private static final String TAG = "AudioTrack";
    @SuppressLint({"InlinedApi"})
    private static final int WRITE_NON_BLOCKING = 1;
    public static boolean enablePreV21AudioSessionWorkaround = false;
    public static boolean failOnSpuriousAudioTimestamp = false;
    private AudioProcessor[] activeAudioProcessors;
    @Nullable
    private PlaybackParameters afterDrainPlaybackParameters;
    private AudioAttributes audioAttributes;
    @Nullable
    private final AudioCapabilities audioCapabilities;
    private final AudioProcessorChain audioProcessorChain;
    private int audioSessionId;
    private AudioTrack audioTrack;
    private final AudioTrackPositionTracker audioTrackPositionTracker;
    private AuxEffectInfo auxEffectInfo;
    @Nullable
    private ByteBuffer avSyncHeader;
    private int bufferSize;
    private int bytesUntilNextAvSync;
    private boolean canApplyPlaybackParameters;
    private final ChannelMappingAudioProcessor channelMappingAudioProcessor;
    private int drainingAudioProcessorIndex;
    private final boolean enableConvertHighResIntPcmToFloat;
    private int framesPerEncodedSample;
    private boolean handledEndOfStream;
    @Nullable
    private ByteBuffer inputBuffer;
    private int inputSampleRate;
    private boolean isInputPcm;
    @Nullable
    private AudioTrack keepSessionIdAudioTrack;
    private long lastFeedElapsedRealtimeMs;
    @Nullable
    private Listener listener;
    @Nullable
    private ByteBuffer outputBuffer;
    private ByteBuffer[] outputBuffers;
    private int outputChannelConfig;
    private int outputEncoding;
    private int outputPcmFrameSize;
    private int outputSampleRate;
    private int pcmFrameSize;
    private PlaybackParameters playbackParameters;
    private final ArrayDeque<PlaybackParametersCheckpoint> playbackParametersCheckpoints;
    private long playbackParametersOffsetUs;
    private long playbackParametersPositionUs;
    private boolean playing;
    private byte[] preV21OutputBuffer;
    private int preV21OutputBufferOffset;
    private boolean processingEnabled;
    private final ConditionVariable releasingConditionVariable;
    private boolean shouldConvertHighResIntPcmToFloat;
    private int startMediaTimeState;
    private long startMediaTimeUs;
    private long submittedEncodedFrames;
    private long submittedPcmBytes;
    private final AudioProcessor[] toFloatPcmAvailableAudioProcessors;
    private final AudioProcessor[] toIntPcmAvailableAudioProcessors;
    private final TrimmingAudioProcessor trimmingAudioProcessor;
    private boolean tunneling;
    private float volume;
    private long writtenEncodedFrames;
    private long writtenPcmBytes;

    public interface AudioProcessorChain {
        PlaybackParameters applyPlaybackParameters(PlaybackParameters playbackParameters);

        AudioProcessor[] getAudioProcessors();

        long getMediaDuration(long j);

        long getSkippedOutputFrameCount();
    }

    public static final class InvalidAudioTrackTimestampException extends RuntimeException {
        private InvalidAudioTrackTimestampException(String message) {
            super(message);
        }
    }

    private static final class PlaybackParametersCheckpoint {
        private final long mediaTimeUs;
        private final PlaybackParameters playbackParameters;
        private final long positionUs;

        private PlaybackParametersCheckpoint(PlaybackParameters playbackParameters, long mediaTimeUs, long positionUs) {
            this.playbackParameters = playbackParameters;
            this.mediaTimeUs = mediaTimeUs;
            this.positionUs = positionUs;
        }
    }

    public static class DefaultAudioProcessorChain implements AudioProcessorChain {
        private final AudioProcessor[] audioProcessors;
        private final SilenceSkippingAudioProcessor silenceSkippingAudioProcessor = new SilenceSkippingAudioProcessor();
        private final SonicAudioProcessor sonicAudioProcessor = new SonicAudioProcessor();

        public DefaultAudioProcessorChain(AudioProcessor... audioProcessors) {
            this.audioProcessors = (AudioProcessor[]) Arrays.copyOf(audioProcessors, audioProcessors.length + 2);
            AudioProcessor[] audioProcessorArr = this.audioProcessors;
            audioProcessorArr[audioProcessors.length] = this.silenceSkippingAudioProcessor;
            audioProcessorArr[audioProcessors.length + 1] = this.sonicAudioProcessor;
        }

        public AudioProcessor[] getAudioProcessors() {
            return this.audioProcessors;
        }

        public PlaybackParameters applyPlaybackParameters(PlaybackParameters playbackParameters) {
            this.silenceSkippingAudioProcessor.setEnabled(playbackParameters.skipSilence);
            return new PlaybackParameters(this.sonicAudioProcessor.setSpeed(playbackParameters.speed), this.sonicAudioProcessor.setPitch(playbackParameters.pitch), playbackParameters.skipSilence);
        }

        public long getMediaDuration(long playoutDuration) {
            return this.sonicAudioProcessor.scaleDurationForSpeedup(playoutDuration);
        }

        public long getSkippedOutputFrameCount() {
            return this.silenceSkippingAudioProcessor.getSkippedFrames();
        }
    }

    private final class PositionTrackerListener implements AudioTrackPositionTracker.Listener {
        private PositionTrackerListener() {
        }

        public void onPositionFramesMismatch(long audioTimestampPositionFrames, long audioTimestampSystemTimeUs, long systemTimeUs, long playbackPositionUs) {
            String message = new StringBuilder();
            message.append("Spurious audio timestamp (frame position mismatch): ");
            message.append(audioTimestampPositionFrames);
            message.append(", ");
            message.append(audioTimestampSystemTimeUs);
            message.append(", ");
            message.append(systemTimeUs);
            message.append(", ");
            message.append(playbackPositionUs);
            message.append(", ");
            message.append(DefaultAudioSink.this.getSubmittedFrames());
            message.append(", ");
            message.append(DefaultAudioSink.this.getWrittenFrames());
            message = message.toString();
            if (DefaultAudioSink.failOnSpuriousAudioTimestamp) {
                throw new InvalidAudioTrackTimestampException(message);
            }
            Log.m10w(DefaultAudioSink.TAG, message);
        }

        public void onSystemTimeUsMismatch(long audioTimestampPositionFrames, long audioTimestampSystemTimeUs, long systemTimeUs, long playbackPositionUs) {
            String message = new StringBuilder();
            message.append("Spurious audio timestamp (system clock mismatch): ");
            message.append(audioTimestampPositionFrames);
            message.append(", ");
            message.append(audioTimestampSystemTimeUs);
            message.append(", ");
            message.append(systemTimeUs);
            message.append(", ");
            message.append(playbackPositionUs);
            message.append(", ");
            message.append(DefaultAudioSink.this.getSubmittedFrames());
            message.append(", ");
            message.append(DefaultAudioSink.this.getWrittenFrames());
            message = message.toString();
            if (DefaultAudioSink.failOnSpuriousAudioTimestamp) {
                throw new InvalidAudioTrackTimestampException(message);
            }
            Log.m10w(DefaultAudioSink.TAG, message);
        }

        public void onInvalidLatency(long latencyUs) {
            String str = DefaultAudioSink.TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Ignoring impossibly large audio latency: ");
            stringBuilder.append(latencyUs);
            Log.m10w(str, stringBuilder.toString());
        }

        public void onUnderrun(int bufferSize, long bufferSizeMs) {
            if (DefaultAudioSink.this.listener != null) {
                DefaultAudioSink.this.listener.onUnderrun(bufferSize, bufferSizeMs, SystemClock.elapsedRealtime() - DefaultAudioSink.this.lastFeedElapsedRealtimeMs);
            }
        }
    }

    public void configure(int r17, int r18, int r19, int r20, @android.support.annotation.Nullable int[] r21, int r22, int r23) throws com.google.android.exoplayer2.audio.AudioSink.ConfigurationException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:79:0x0114 in {6, 7, 8, 9, 10, 13, 14, 19, 20, 24, 25, 34, 35, 36, 45, 46, 47, 50, 51, 52, 64, 65, 66, 69, 70, 73, 74, 76, 78} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r16 = this;
        r1 = r16;
        r2 = r17;
        r0 = 0;
        r3 = r19;
        r1.inputSampleRate = r3;
        r4 = r18;
        r5 = r19;
        r6 = com.google.android.exoplayer2.util.Util.isEncodingLinearPcm(r17);
        r1.isInputPcm = r6;
        r6 = r1.enableConvertHighResIntPcmToFloat;
        r7 = 0;
        r8 = 1;
        if (r6 == 0) goto L_0x002b;
    L_0x0019:
        r6 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = r1.supportsOutput(r4, r6);
        if (r6 == 0) goto L_0x002a;
    L_0x0021:
        r6 = com.google.android.exoplayer2.util.Util.isEncodingHighResolutionIntegerPcm(r17);
        if (r6 == 0) goto L_0x0029;
    L_0x0027:
        r6 = 1;
        goto L_0x002d;
    L_0x0029:
        goto L_0x002c;
    L_0x002a:
        goto L_0x002c;
    L_0x002c:
        r6 = 0;
    L_0x002d:
        r1.shouldConvertHighResIntPcmToFloat = r6;
        r6 = r1.isInputPcm;
        if (r6 == 0) goto L_0x003a;
    L_0x0033:
        r6 = com.google.android.exoplayer2.util.Util.getPcmFrameSize(r2, r4);
        r1.pcmFrameSize = r6;
        goto L_0x003b;
    L_0x003b:
        r6 = r17;
        r9 = r1.isInputPcm;
        if (r9 == 0) goto L_0x0046;
    L_0x0041:
        r9 = 4;
        if (r2 == r9) goto L_0x0046;
    L_0x0044:
        r9 = 1;
        goto L_0x0047;
    L_0x0046:
        r9 = 0;
    L_0x0047:
        if (r9 == 0) goto L_0x004e;
    L_0x0049:
        r10 = r1.shouldConvertHighResIntPcmToFloat;
        if (r10 != 0) goto L_0x004e;
    L_0x004d:
        goto L_0x004f;
    L_0x004e:
        r8 = 0;
    L_0x004f:
        r1.canApplyPlaybackParameters = r8;
        r8 = com.google.android.exoplayer2.util.Util.SDK_INT;
        r10 = 21;
        if (r8 >= r10) goto L_0x006a;
    L_0x0057:
        r8 = 8;
        if (r4 != r8) goto L_0x006a;
    L_0x005b:
        if (r21 != 0) goto L_0x006a;
    L_0x005d:
        r8 = 6;
        r8 = new int[r8];
        r10 = 0;
    L_0x0061:
        r11 = r8.length;
        if (r10 >= r11) goto L_0x0069;
    L_0x0064:
        r8[r10] = r10;
        r10 = r10 + 1;
        goto L_0x0061;
    L_0x0069:
        goto L_0x006d;
        r8 = r21;
    L_0x006d:
        if (r9 == 0) goto L_0x00b4;
    L_0x006f:
        r10 = r1.trimmingAudioProcessor;
        r11 = r22;
        r12 = r23;
        r10.setTrimFrameCount(r11, r12);
        r10 = r1.channelMappingAudioProcessor;
        r10.setChannelMap(r8);
        r10 = r16.getAvailableAudioProcessors();
        r13 = r10.length;
        r14 = r6;
        r6 = r5;
        r5 = r4;
        r4 = r0;
    L_0x0086:
        if (r7 >= r13) goto L_0x00b0;
    L_0x0088:
        r15 = r10[r7];
        r0 = r15.configure(r6, r5, r14);	 Catch:{ UnhandledFormatException -> 0x00a7 }
        r4 = r4 | r0;
        r0 = r15.isActive();
        if (r0 == 0) goto L_0x00a3;
    L_0x0096:
        r5 = r15.getOutputChannelCount();
        r6 = r15.getOutputSampleRateHz();
        r14 = r15.getOutputEncoding();
        goto L_0x00a4;
    L_0x00a4:
        r7 = r7 + 1;
        goto L_0x0086;
    L_0x00a7:
        r0 = move-exception;
        r7 = r0;
        r0 = r7;
        r7 = new com.google.android.exoplayer2.audio.AudioSink$ConfigurationException;
        r7.<init>(r0);
        throw r7;
    L_0x00b0:
        r0 = r4;
        r4 = r5;
        r5 = r6;
        goto L_0x00b9;
    L_0x00b4:
        r11 = r22;
        r12 = r23;
        r14 = r6;
    L_0x00b9:
        r6 = r1.isInputPcm;
        r6 = getChannelConfig(r4, r6);
        if (r6 == 0) goto L_0x00fd;
    L_0x00c1:
        if (r0 != 0) goto L_0x00d7;
    L_0x00c3:
        r7 = r16.isInitialized();
        if (r7 == 0) goto L_0x00d6;
    L_0x00c9:
        r7 = r1.outputEncoding;
        if (r7 != r14) goto L_0x00d6;
    L_0x00cd:
        r7 = r1.outputSampleRate;
        if (r7 != r5) goto L_0x00d6;
    L_0x00d1:
        r7 = r1.outputChannelConfig;
        if (r7 != r6) goto L_0x00d6;
    L_0x00d5:
        return;
    L_0x00d6:
        goto L_0x00d8;
    L_0x00d8:
        r16.reset();
        r1.processingEnabled = r9;
        r1.outputSampleRate = r5;
        r1.outputChannelConfig = r6;
        r1.outputEncoding = r14;
        r7 = r1.isInputPcm;
        if (r7 == 0) goto L_0x00ee;
    L_0x00e7:
        r7 = r1.outputEncoding;
        r7 = com.google.android.exoplayer2.util.Util.getPcmFrameSize(r7, r4);
        goto L_0x00ef;
    L_0x00ee:
        r7 = -1;
    L_0x00ef:
        r1.outputPcmFrameSize = r7;
        if (r20 == 0) goto L_0x00f6;
    L_0x00f3:
        r7 = r20;
        goto L_0x00fa;
    L_0x00f6:
        r7 = r16.getDefaultBufferSize();
    L_0x00fa:
        r1.bufferSize = r7;
        return;
    L_0x00fd:
        r7 = new com.google.android.exoplayer2.audio.AudioSink$ConfigurationException;
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r13 = "Unsupported channel count: ";
        r10.append(r13);
        r10.append(r4);
        r10 = r10.toString();
        r7.<init>(r10);
        throw r7;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.audio.DefaultAudioSink.configure(int, int, int, int, int[], int, int):void");
    }

    public DefaultAudioSink(@Nullable AudioCapabilities audioCapabilities, AudioProcessor[] audioProcessors) {
        this(audioCapabilities, audioProcessors, false);
    }

    public DefaultAudioSink(@Nullable AudioCapabilities audioCapabilities, AudioProcessor[] audioProcessors, boolean enableConvertHighResIntPcmToFloat) {
        this(audioCapabilities, new DefaultAudioProcessorChain(audioProcessors), enableConvertHighResIntPcmToFloat);
    }

    public DefaultAudioSink(@Nullable AudioCapabilities audioCapabilities, AudioProcessorChain audioProcessorChain, boolean enableConvertHighResIntPcmToFloat) {
        this.audioCapabilities = audioCapabilities;
        this.audioProcessorChain = (AudioProcessorChain) Assertions.checkNotNull(audioProcessorChain);
        this.enableConvertHighResIntPcmToFloat = enableConvertHighResIntPcmToFloat;
        this.releasingConditionVariable = new ConditionVariable(true);
        this.audioTrackPositionTracker = new AudioTrackPositionTracker(new PositionTrackerListener());
        this.channelMappingAudioProcessor = new ChannelMappingAudioProcessor();
        this.trimmingAudioProcessor = new TrimmingAudioProcessor();
        ArrayList<AudioProcessor> toIntPcmAudioProcessors = new ArrayList();
        Collections.addAll(toIntPcmAudioProcessors, new AudioProcessor[]{new ResamplingAudioProcessor(), this.channelMappingAudioProcessor, this.trimmingAudioProcessor});
        Collections.addAll(toIntPcmAudioProcessors, audioProcessorChain.getAudioProcessors());
        this.toIntPcmAvailableAudioProcessors = (AudioProcessor[]) toIntPcmAudioProcessors.toArray(new AudioProcessor[toIntPcmAudioProcessors.size()]);
        this.toFloatPcmAvailableAudioProcessors = new AudioProcessor[]{new FloatResamplingAudioProcessor()};
        this.volume = 1.0f;
        this.startMediaTimeState = 0;
        this.audioAttributes = AudioAttributes.DEFAULT;
        this.audioSessionId = 0;
        this.auxEffectInfo = new AuxEffectInfo(0, 0.0f);
        this.playbackParameters = PlaybackParameters.DEFAULT;
        this.drainingAudioProcessorIndex = -1;
        this.activeAudioProcessors = new AudioProcessor[0];
        this.outputBuffers = new ByteBuffer[0];
        this.playbackParametersCheckpoints = new ArrayDeque();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public boolean supportsOutput(int channelCount, int encoding) {
        boolean z = true;
        if (Util.isEncodingLinearPcm(encoding)) {
            if (encoding == 4) {
                if (Util.SDK_INT < 21) {
                    z = false;
                }
            }
            return z;
        }
        AudioCapabilities audioCapabilities = this.audioCapabilities;
        if (audioCapabilities != null) {
            if (audioCapabilities.supportsEncoding(encoding)) {
                if (channelCount != -1) {
                    if (channelCount <= this.audioCapabilities.getMaxChannelCount()) {
                    }
                }
                return z;
            }
        }
        z = false;
        return z;
    }

    public long getCurrentPositionUs(boolean sourceEnded) {
        if (isInitialized()) {
            if (this.startMediaTimeState != 0) {
                return this.startMediaTimeUs + applySkipping(applySpeedup(Math.min(this.audioTrackPositionTracker.getCurrentPositionUs(sourceEnded), framesToDurationUs(getWrittenFrames()))));
            }
        }
        return Long.MIN_VALUE;
    }

    private int getDefaultBufferSize() {
        if (this.isInputPcm) {
            int minBufferSize = AudioTrack.getMinBufferSize(this.outputSampleRate, this.outputChannelConfig, this.outputEncoding);
            Assertions.checkState(minBufferSize != -2);
            return Util.constrainValue(minBufferSize * 4, ((int) durationUsToFrames(250000)) * this.outputPcmFrameSize, (int) Math.max((long) minBufferSize, durationUsToFrames(MAX_BUFFER_DURATION_US) * ((long) this.outputPcmFrameSize)));
        }
        minBufferSize = getMaximumEncodedRateBytesPerSecond(this.outputEncoding);
        if (this.outputEncoding == 5) {
            minBufferSize *= 2;
        }
        return (int) ((((long) minBufferSize) * 250000) / 1000000);
    }

    private void setupAudioProcessors() {
        ArrayList<AudioProcessor> newAudioProcessors = new ArrayList();
        for (AudioProcessor audioProcessor : getAvailableAudioProcessors()) {
            if (audioProcessor.isActive()) {
                newAudioProcessors.add(audioProcessor);
            } else {
                audioProcessor.flush();
            }
        }
        int count = newAudioProcessors.size();
        this.activeAudioProcessors = (AudioProcessor[]) newAudioProcessors.toArray(new AudioProcessor[count]);
        this.outputBuffers = new ByteBuffer[count];
        flushAudioProcessors();
    }

    private void flushAudioProcessors() {
        int i = 0;
        while (true) {
            AudioProcessor audioProcessor = this.activeAudioProcessors;
            if (i < audioProcessor.length) {
                audioProcessor = audioProcessor[i];
                audioProcessor.flush();
                this.outputBuffers[i] = audioProcessor.getOutput();
                i++;
            } else {
                return;
            }
        }
    }

    private void initialize() throws InitializationException {
        this.releasingConditionVariable.block();
        this.audioTrack = initializeAudioTrack();
        int audioSessionId = this.audioTrack.getAudioSessionId();
        if (enablePreV21AudioSessionWorkaround) {
            if (Util.SDK_INT < 21) {
                AudioTrack audioTrack = this.keepSessionIdAudioTrack;
                if (audioTrack != null) {
                    if (audioSessionId != audioTrack.getAudioSessionId()) {
                        releaseKeepSessionIdAudioTrack();
                    }
                }
                if (this.keepSessionIdAudioTrack == null) {
                    this.keepSessionIdAudioTrack = initializeKeepSessionIdAudioTrack(audioSessionId);
                }
            }
        }
        if (this.audioSessionId != audioSessionId) {
            this.audioSessionId = audioSessionId;
            Listener listener = this.listener;
            if (listener != null) {
                listener.onAudioSessionId(audioSessionId);
            }
        }
        this.playbackParameters = this.canApplyPlaybackParameters ? this.audioProcessorChain.applyPlaybackParameters(this.playbackParameters) : PlaybackParameters.DEFAULT;
        setupAudioProcessors();
        this.audioTrackPositionTracker.setAudioTrack(this.audioTrack, this.outputEncoding, this.outputPcmFrameSize, this.bufferSize);
        setVolumeInternal();
        if (this.auxEffectInfo.effectId != 0) {
            this.audioTrack.attachAuxEffect(this.auxEffectInfo.effectId);
            this.audioTrack.setAuxEffectSendLevel(this.auxEffectInfo.sendLevel);
        }
    }

    public void play() {
        this.playing = true;
        if (isInitialized()) {
            this.audioTrackPositionTracker.start();
            this.audioTrack.play();
        }
    }

    public void handleDiscontinuity() {
        if (this.startMediaTimeState == 1) {
            this.startMediaTimeState = 2;
        }
    }

    public boolean handleBuffer(ByteBuffer buffer, long presentationTimeUs) throws InitializationException, WriteException {
        boolean z;
        long expectedPresentationTimeUs;
        Listener listener;
        ByteBuffer byteBuffer = buffer;
        long j = presentationTimeUs;
        ByteBuffer byteBuffer2 = this.inputBuffer;
        if (byteBuffer2 != null) {
            if (byteBuffer != byteBuffer2) {
                z = false;
                Assertions.checkArgument(z);
                if (!isInitialized()) {
                    initialize();
                    if (r0.playing) {
                        play();
                    }
                }
                if (!r0.audioTrackPositionTracker.mayHandleBuffer(getWrittenFrames())) {
                    return false;
                }
                if (r0.inputBuffer == null) {
                    if (!buffer.hasRemaining()) {
                        return true;
                    }
                    if (r0.isInputPcm && r0.framesPerEncodedSample == 0) {
                        r0.framesPerEncodedSample = getFramesPerEncodedSample(r0.outputEncoding, byteBuffer);
                        if (r0.framesPerEncodedSample == 0) {
                            return true;
                        }
                    }
                    if (r0.afterDrainPlaybackParameters != null) {
                        if (!drainAudioProcessorsToEndOfStream()) {
                            return false;
                        }
                        PlaybackParameters newPlaybackParameters = r0.afterDrainPlaybackParameters;
                        r0.afterDrainPlaybackParameters = null;
                        newPlaybackParameters = r0.audioProcessorChain.applyPlaybackParameters(newPlaybackParameters);
                        ArrayDeque arrayDeque = r0.playbackParametersCheckpoints;
                        PlaybackParametersCheckpoint playbackParametersCheckpoint = r11;
                        PlaybackParametersCheckpoint playbackParametersCheckpoint2 = new PlaybackParametersCheckpoint(newPlaybackParameters, Math.max(0, j), framesToDurationUs(getWrittenFrames()));
                        arrayDeque.add(playbackParametersCheckpoint);
                        setupAudioProcessors();
                    }
                    if (r0.startMediaTimeState != 0) {
                        r0.startMediaTimeUs = Math.max(0, j);
                        r0.startMediaTimeState = 1;
                    } else {
                        expectedPresentationTimeUs = r0.startMediaTimeUs + inputFramesToDurationUs(getSubmittedFrames() - r0.trimmingAudioProcessor.getTrimmedFrameCount());
                        if (r0.startMediaTimeState == 1) {
                            if (Math.abs(expectedPresentationTimeUs - j) > 200000) {
                                String str = TAG;
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("Discontinuity detected [expected ");
                                stringBuilder.append(expectedPresentationTimeUs);
                                stringBuilder.append(", got ");
                                stringBuilder.append(j);
                                stringBuilder.append("]");
                                Log.m6e(str, stringBuilder.toString());
                                r0.startMediaTimeState = 2;
                            }
                        }
                        if (r0.startMediaTimeState == 2) {
                            long adjustmentUs = j - expectedPresentationTimeUs;
                            r0.startMediaTimeUs += adjustmentUs;
                            r0.startMediaTimeState = 1;
                            listener = r0.listener;
                            if (listener == null && adjustmentUs != 0) {
                                listener.onPositionDiscontinuity();
                            }
                        }
                    }
                    if (r0.isInputPcm) {
                        r0.submittedEncodedFrames += (long) r0.framesPerEncodedSample;
                    } else {
                        r0.submittedPcmBytes += (long) buffer.remaining();
                    }
                    r0.inputBuffer = byteBuffer;
                }
                if (r0.processingEnabled) {
                    writeBuffer(r0.inputBuffer, j);
                } else {
                    processBuffers(j);
                }
                if (!r0.inputBuffer.hasRemaining()) {
                    r0.inputBuffer = null;
                    return true;
                } else if (r0.audioTrackPositionTracker.isStalled(getWrittenFrames())) {
                    return false;
                } else {
                    Log.m10w(TAG, "Resetting stalled audio track");
                    reset();
                    return true;
                }
            }
        }
        z = true;
        Assertions.checkArgument(z);
        if (!isInitialized()) {
            initialize();
            if (r0.playing) {
                play();
            }
        }
        if (!r0.audioTrackPositionTracker.mayHandleBuffer(getWrittenFrames())) {
            return false;
        }
        if (r0.inputBuffer == null) {
            if (!buffer.hasRemaining()) {
                return true;
            }
            if (r0.isInputPcm) {
            }
            if (r0.afterDrainPlaybackParameters != null) {
                if (!drainAudioProcessorsToEndOfStream()) {
                    return false;
                }
                PlaybackParameters newPlaybackParameters2 = r0.afterDrainPlaybackParameters;
                r0.afterDrainPlaybackParameters = null;
                newPlaybackParameters2 = r0.audioProcessorChain.applyPlaybackParameters(newPlaybackParameters2);
                ArrayDeque arrayDeque2 = r0.playbackParametersCheckpoints;
                PlaybackParametersCheckpoint playbackParametersCheckpoint3 = playbackParametersCheckpoint2;
                PlaybackParametersCheckpoint playbackParametersCheckpoint22 = new PlaybackParametersCheckpoint(newPlaybackParameters2, Math.max(0, j), framesToDurationUs(getWrittenFrames()));
                arrayDeque2.add(playbackParametersCheckpoint3);
                setupAudioProcessors();
            }
            if (r0.startMediaTimeState != 0) {
                expectedPresentationTimeUs = r0.startMediaTimeUs + inputFramesToDurationUs(getSubmittedFrames() - r0.trimmingAudioProcessor.getTrimmedFrameCount());
                if (r0.startMediaTimeState == 1) {
                    if (Math.abs(expectedPresentationTimeUs - j) > 200000) {
                        String str2 = TAG;
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Discontinuity detected [expected ");
                        stringBuilder2.append(expectedPresentationTimeUs);
                        stringBuilder2.append(", got ");
                        stringBuilder2.append(j);
                        stringBuilder2.append("]");
                        Log.m6e(str2, stringBuilder2.toString());
                        r0.startMediaTimeState = 2;
                    }
                }
                if (r0.startMediaTimeState == 2) {
                    long adjustmentUs2 = j - expectedPresentationTimeUs;
                    r0.startMediaTimeUs += adjustmentUs2;
                    r0.startMediaTimeState = 1;
                    listener = r0.listener;
                    if (listener == null) {
                    }
                }
            } else {
                r0.startMediaTimeUs = Math.max(0, j);
                r0.startMediaTimeState = 1;
            }
            if (r0.isInputPcm) {
                r0.submittedEncodedFrames += (long) r0.framesPerEncodedSample;
            } else {
                r0.submittedPcmBytes += (long) buffer.remaining();
            }
            r0.inputBuffer = byteBuffer;
        }
        if (r0.processingEnabled) {
            writeBuffer(r0.inputBuffer, j);
        } else {
            processBuffers(j);
        }
        if (!r0.inputBuffer.hasRemaining()) {
            r0.inputBuffer = null;
            return true;
        } else if (r0.audioTrackPositionTracker.isStalled(getWrittenFrames())) {
            return false;
        } else {
            Log.m10w(TAG, "Resetting stalled audio track");
            reset();
            return true;
        }
    }

    private void processBuffers(long avSyncPresentationTimeUs) throws WriteException {
        int count = this.activeAudioProcessors.length;
        int index = count;
        while (index >= 0) {
            ByteBuffer input;
            if (index > 0) {
                input = this.outputBuffers[index - 1];
            } else {
                input = this.inputBuffer;
                if (input == null) {
                    input = AudioProcessor.EMPTY_BUFFER;
                }
            }
            if (index == count) {
                writeBuffer(input, avSyncPresentationTimeUs);
            } else {
                AudioProcessor audioProcessor = this.activeAudioProcessors[index];
                audioProcessor.queueInput(input);
                ByteBuffer output = audioProcessor.getOutput();
                this.outputBuffers[index] = output;
                if (output.hasRemaining()) {
                    index++;
                }
            }
            if (!input.hasRemaining()) {
                index--;
            } else {
                return;
            }
        }
    }

    private void writeBuffer(ByteBuffer buffer, long avSyncPresentationTimeUs) throws WriteException {
        if (buffer.hasRemaining()) {
            int bytesRemaining;
            int originalPosition;
            ByteBuffer byteBuffer = this.outputBuffer;
            boolean z = true;
            if (byteBuffer != null) {
                Assertions.checkArgument(byteBuffer == buffer);
            } else {
                this.outputBuffer = buffer;
                if (Util.SDK_INT < 21) {
                    bytesRemaining = buffer.remaining();
                    byte[] bArr = this.preV21OutputBuffer;
                    if (bArr != null) {
                        if (bArr.length >= bytesRemaining) {
                            originalPosition = buffer.position();
                            buffer.get(this.preV21OutputBuffer, 0, bytesRemaining);
                            buffer.position(originalPosition);
                            this.preV21OutputBufferOffset = 0;
                        }
                    }
                    this.preV21OutputBuffer = new byte[bytesRemaining];
                    originalPosition = buffer.position();
                    buffer.get(this.preV21OutputBuffer, 0, bytesRemaining);
                    buffer.position(originalPosition);
                    this.preV21OutputBufferOffset = 0;
                }
            }
            bytesRemaining = buffer.remaining();
            originalPosition = 0;
            if (Util.SDK_INT < 21) {
                int bytesToWrite = this.audioTrackPositionTracker.getAvailableBufferSize(this.writtenPcmBytes);
                if (bytesToWrite > 0) {
                    originalPosition = this.audioTrack.write(this.preV21OutputBuffer, this.preV21OutputBufferOffset, Math.min(bytesRemaining, bytesToWrite));
                    if (originalPosition > 0) {
                        this.preV21OutputBufferOffset += originalPosition;
                        buffer.position(buffer.position() + originalPosition);
                    }
                }
            } else if (this.tunneling) {
                if (avSyncPresentationTimeUs == C0555C.TIME_UNSET) {
                    z = false;
                }
                Assertions.checkState(z);
                originalPosition = writeNonBlockingWithAvSyncV21(this.audioTrack, buffer, bytesRemaining, avSyncPresentationTimeUs);
            } else {
                originalPosition = writeNonBlockingV21(this.audioTrack, buffer, bytesRemaining);
            }
            this.lastFeedElapsedRealtimeMs = SystemClock.elapsedRealtime();
            if (originalPosition >= 0) {
                if (this.isInputPcm) {
                    this.writtenPcmBytes += (long) originalPosition;
                }
                if (originalPosition == bytesRemaining) {
                    if (!this.isInputPcm) {
                        this.writtenEncodedFrames += (long) this.framesPerEncodedSample;
                    }
                    this.outputBuffer = null;
                }
                return;
            }
            throw new WriteException(originalPosition);
        }
    }

    public void playToEndOfStream() throws WriteException {
        if (!this.handledEndOfStream) {
            if (isInitialized()) {
                if (drainAudioProcessorsToEndOfStream()) {
                    this.audioTrackPositionTracker.handleEndOfStream(getWrittenFrames());
                    this.audioTrack.stop();
                    this.bytesUntilNextAvSync = 0;
                    this.handledEndOfStream = true;
                }
            }
        }
    }

    private boolean drainAudioProcessorsToEndOfStream() throws WriteException {
        boolean audioProcessorNeedsEndOfStream = false;
        if (this.drainingAudioProcessorIndex == -1) {
            this.drainingAudioProcessorIndex = this.processingEnabled ? 0 : this.activeAudioProcessors.length;
            audioProcessorNeedsEndOfStream = true;
        }
        while (true) {
            AudioProcessor audioProcessor = this.drainingAudioProcessorIndex;
            AudioProcessor[] audioProcessorArr = this.activeAudioProcessors;
            if (audioProcessor >= audioProcessorArr.length) {
                break;
            }
            audioProcessor = audioProcessorArr[audioProcessor];
            if (audioProcessorNeedsEndOfStream) {
                audioProcessor.queueEndOfStream();
            }
            processBuffers(C0555C.TIME_UNSET);
            if (!audioProcessor.isEnded()) {
                return false;
            }
            audioProcessorNeedsEndOfStream = true;
            this.drainingAudioProcessorIndex++;
        }
        ByteBuffer byteBuffer = this.outputBuffer;
        if (byteBuffer != null) {
            writeBuffer(byteBuffer, C0555C.TIME_UNSET);
            if (this.outputBuffer != null) {
                return false;
            }
        }
        this.drainingAudioProcessorIndex = -1;
        return true;
    }

    public boolean isEnded() {
        if (isInitialized()) {
            if (!this.handledEndOfStream || hasPendingData()) {
                return false;
            }
        }
        return true;
    }

    public boolean hasPendingData() {
        return isInitialized() && this.audioTrackPositionTracker.hasPendingData(getWrittenFrames());
    }

    public PlaybackParameters setPlaybackParameters(PlaybackParameters playbackParameters) {
        if (!isInitialized() || this.canApplyPlaybackParameters) {
            PlaybackParameters lastSetPlaybackParameters = this.afterDrainPlaybackParameters;
            if (lastSetPlaybackParameters == null) {
                lastSetPlaybackParameters = !this.playbackParametersCheckpoints.isEmpty() ? ((PlaybackParametersCheckpoint) this.playbackParametersCheckpoints.getLast()).playbackParameters : this.playbackParameters;
            }
            if (!playbackParameters.equals(lastSetPlaybackParameters)) {
                if (isInitialized()) {
                    this.afterDrainPlaybackParameters = playbackParameters;
                } else {
                    this.playbackParameters = this.audioProcessorChain.applyPlaybackParameters(playbackParameters);
                }
            }
            return this.playbackParameters;
        }
        this.playbackParameters = PlaybackParameters.DEFAULT;
        return this.playbackParameters;
    }

    public PlaybackParameters getPlaybackParameters() {
        return this.playbackParameters;
    }

    public void setAudioAttributes(AudioAttributes audioAttributes) {
        if (!this.audioAttributes.equals(audioAttributes)) {
            this.audioAttributes = audioAttributes;
            if (!this.tunneling) {
                reset();
                this.audioSessionId = 0;
            }
        }
    }

    public void setAudioSessionId(int audioSessionId) {
        if (this.audioSessionId != audioSessionId) {
            this.audioSessionId = audioSessionId;
            reset();
        }
    }

    public void setAuxEffectInfo(AuxEffectInfo auxEffectInfo) {
        if (!this.auxEffectInfo.equals(auxEffectInfo)) {
            int effectId = auxEffectInfo.effectId;
            float sendLevel = auxEffectInfo.sendLevel;
            if (this.audioTrack != null) {
                if (this.auxEffectInfo.effectId != effectId) {
                    this.audioTrack.attachAuxEffect(effectId);
                }
                if (effectId != 0) {
                    this.audioTrack.setAuxEffectSendLevel(sendLevel);
                }
            }
            this.auxEffectInfo = auxEffectInfo;
        }
    }

    public void enableTunnelingV21(int tunnelingAudioSessionId) {
        Assertions.checkState(Util.SDK_INT >= 21);
        if (this.tunneling) {
            if (this.audioSessionId == tunnelingAudioSessionId) {
                return;
            }
        }
        this.tunneling = true;
        this.audioSessionId = tunnelingAudioSessionId;
        reset();
    }

    public void disableTunneling() {
        if (this.tunneling) {
            this.tunneling = false;
            this.audioSessionId = 0;
            reset();
        }
    }

    public void setVolume(float volume) {
        if (this.volume != volume) {
            this.volume = volume;
            setVolumeInternal();
        }
    }

    private void setVolumeInternal() {
        if (!isInitialized()) {
            return;
        }
        if (Util.SDK_INT >= 21) {
            setVolumeInternalV21(this.audioTrack, this.volume);
        } else {
            setVolumeInternalV3(this.audioTrack, this.volume);
        }
    }

    public void pause() {
        this.playing = false;
        if (isInitialized() && this.audioTrackPositionTracker.pause()) {
            this.audioTrack.pause();
        }
    }

    public void reset() {
        if (isInitialized()) {
            this.submittedPcmBytes = 0;
            this.submittedEncodedFrames = 0;
            this.writtenPcmBytes = 0;
            this.writtenEncodedFrames = 0;
            this.framesPerEncodedSample = 0;
            PlaybackParameters playbackParameters = this.afterDrainPlaybackParameters;
            if (playbackParameters != null) {
                this.playbackParameters = playbackParameters;
                this.afterDrainPlaybackParameters = null;
            } else if (!this.playbackParametersCheckpoints.isEmpty()) {
                this.playbackParameters = ((PlaybackParametersCheckpoint) this.playbackParametersCheckpoints.getLast()).playbackParameters;
            }
            this.playbackParametersCheckpoints.clear();
            this.playbackParametersOffsetUs = 0;
            this.playbackParametersPositionUs = 0;
            this.trimmingAudioProcessor.resetTrimmedFrameCount();
            this.inputBuffer = null;
            this.outputBuffer = null;
            flushAudioProcessors();
            this.handledEndOfStream = false;
            this.drainingAudioProcessorIndex = -1;
            this.avSyncHeader = null;
            this.bytesUntilNextAvSync = 0;
            this.startMediaTimeState = 0;
            if (this.audioTrackPositionTracker.isPlaying()) {
                this.audioTrack.pause();
            }
            final AudioTrack toRelease = this.audioTrack;
            this.audioTrack = null;
            this.audioTrackPositionTracker.reset();
            this.releasingConditionVariable.close();
            new Thread() {
                public void run() {
                    try {
                        toRelease.flush();
                        toRelease.release();
                    } finally {
                        DefaultAudioSink.this.releasingConditionVariable.open();
                    }
                }
            }.start();
        }
    }

    public void release() {
        reset();
        releaseKeepSessionIdAudioTrack();
        for (AudioProcessor audioProcessor : this.toIntPcmAvailableAudioProcessors) {
            audioProcessor.reset();
        }
        for (AudioProcessor audioProcessor2 : this.toFloatPcmAvailableAudioProcessors) {
            audioProcessor2.reset();
        }
        this.audioSessionId = 0;
        this.playing = false;
    }

    private void releaseKeepSessionIdAudioTrack() {
        if (this.keepSessionIdAudioTrack != null) {
            final AudioTrack toRelease = this.keepSessionIdAudioTrack;
            this.keepSessionIdAudioTrack = null;
            new Thread() {
                public void run() {
                    toRelease.release();
                }
            }.start();
        }
    }

    private long applySpeedup(long positionUs) {
        PlaybackParametersCheckpoint checkpoint = null;
        while (!this.playbackParametersCheckpoints.isEmpty()) {
            if (positionUs < ((PlaybackParametersCheckpoint) this.playbackParametersCheckpoints.getFirst()).positionUs) {
                break;
            }
            checkpoint = (PlaybackParametersCheckpoint) this.playbackParametersCheckpoints.remove();
        }
        if (checkpoint != null) {
            this.playbackParameters = checkpoint.playbackParameters;
            this.playbackParametersPositionUs = checkpoint.positionUs;
            this.playbackParametersOffsetUs = checkpoint.mediaTimeUs - this.startMediaTimeUs;
        }
        if (this.playbackParameters.speed == 1.0f) {
            return (this.playbackParametersOffsetUs + positionUs) - this.playbackParametersPositionUs;
        }
        if (this.playbackParametersCheckpoints.isEmpty()) {
            return this.playbackParametersOffsetUs + this.audioProcessorChain.getMediaDuration(positionUs - this.playbackParametersPositionUs);
        }
        return this.playbackParametersOffsetUs + Util.getMediaDurationForPlayoutDuration(positionUs - this.playbackParametersPositionUs, this.playbackParameters.speed);
    }

    private long applySkipping(long positionUs) {
        return framesToDurationUs(this.audioProcessorChain.getSkippedOutputFrameCount()) + positionUs;
    }

    private boolean isInitialized() {
        return this.audioTrack != null;
    }

    private long inputFramesToDurationUs(long frameCount) {
        return (1000000 * frameCount) / ((long) this.inputSampleRate);
    }

    private long framesToDurationUs(long frameCount) {
        return (1000000 * frameCount) / ((long) this.outputSampleRate);
    }

    private long durationUsToFrames(long durationUs) {
        return (((long) this.outputSampleRate) * durationUs) / 1000000;
    }

    private long getSubmittedFrames() {
        return this.isInputPcm ? this.submittedPcmBytes / ((long) this.pcmFrameSize) : this.submittedEncodedFrames;
    }

    private long getWrittenFrames() {
        return this.isInputPcm ? this.writtenPcmBytes / ((long) this.outputPcmFrameSize) : this.writtenEncodedFrames;
    }

    private AudioTrack initializeAudioTrack() throws InitializationException {
        AudioTrack audioTrack;
        if (Util.SDK_INT >= 21) {
            audioTrack = createAudioTrackV21();
        } else {
            int streamType = Util.getStreamTypeForAudioUsage(this.audioAttributes.usage);
            int i = this.audioSessionId;
            if (i == 0) {
                audioTrack = new AudioTrack(streamType, this.outputSampleRate, this.outputChannelConfig, this.outputEncoding, this.bufferSize, 1);
            } else {
                audioTrack = new AudioTrack(streamType, this.outputSampleRate, this.outputChannelConfig, this.outputEncoding, this.bufferSize, 1, i);
            }
        }
        int state = audioTrack.getState();
        if (state == 1) {
            return audioTrack;
        }
        try {
            audioTrack.release();
        } catch (Exception e) {
        }
        throw new InitializationException(state, this.outputSampleRate, this.outputChannelConfig, this.bufferSize);
    }

    @TargetApi(21)
    private AudioTrack createAudioTrackV21() {
        AudioAttributes attributes;
        if (this.tunneling) {
            attributes = new Builder().setContentType(3).setFlags(16).setUsage(1).build();
        } else {
            attributes = this.audioAttributes.getAudioAttributesV21();
        }
        AudioFormat format = new AudioFormat.Builder().setChannelMask(this.outputChannelConfig).setEncoding(this.outputEncoding).setSampleRate(this.outputSampleRate).build();
        int i = this.audioSessionId;
        return new AudioTrack(attributes, format, this.bufferSize, 1, i != 0 ? i : 0);
    }

    private AudioTrack initializeKeepSessionIdAudioTrack(int audioSessionId) {
        return new AudioTrack(3, 4000, 4, 2, 2, 0, audioSessionId);
    }

    private AudioProcessor[] getAvailableAudioProcessors() {
        return this.shouldConvertHighResIntPcmToFloat ? this.toFloatPcmAvailableAudioProcessors : this.toIntPcmAvailableAudioProcessors;
    }

    private static int getChannelConfig(int channelCount, boolean isInputPcm) {
        if (Util.SDK_INT <= 28 && !isInputPcm) {
            if (channelCount == 7) {
                channelCount = 8;
            } else {
                if (!(channelCount == 3 || channelCount == 4)) {
                    if (channelCount == 5) {
                    }
                }
                channelCount = 6;
            }
        }
        if (Util.SDK_INT <= 26 && "fugu".equals(Util.DEVICE) && !isInputPcm && channelCount == 1) {
            channelCount = 2;
        }
        return Util.getAudioTrackChannelConfig(channelCount);
    }

    private static int getMaximumEncodedRateBytesPerSecond(int encoding) {
        if (encoding == 14) {
            return 3062500;
        }
        switch (encoding) {
            case 5:
                return 80000;
            case 6:
                return 768000;
            case 7:
                return 192000;
            case 8:
                return 2250000;
            default:
                throw new IllegalArgumentException();
        }
    }

    private static int getFramesPerEncodedSample(int encoding, ByteBuffer buffer) {
        if (encoding != 7) {
            if (encoding != 8) {
                if (encoding == 5) {
                    return Ac3Util.getAc3SyncframeAudioSampleCount();
                }
                if (encoding == 6) {
                    return Ac3Util.parseEAc3SyncframeAudioSampleCount(buffer);
                }
                if (encoding == 14) {
                    int i;
                    int syncframeOffset = Ac3Util.findTrueHdSyncframeOffset(buffer);
                    if (syncframeOffset == -1) {
                        i = 0;
                    } else {
                        i = Ac3Util.parseTrueHdSyncframeAudioSampleCount(buffer, syncframeOffset) * 16;
                    }
                    return i;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unexpected audio encoding: ");
                stringBuilder.append(encoding);
                throw new IllegalStateException(stringBuilder.toString());
            }
        }
        return DtsUtil.parseDtsAudioSampleCount(buffer);
    }

    @TargetApi(21)
    private static int writeNonBlockingV21(AudioTrack audioTrack, ByteBuffer buffer, int size) {
        return audioTrack.write(buffer, size, 1);
    }

    @TargetApi(21)
    private int writeNonBlockingWithAvSyncV21(AudioTrack audioTrack, ByteBuffer buffer, int size, long presentationTimeUs) {
        int result;
        if (this.avSyncHeader == null) {
            this.avSyncHeader = ByteBuffer.allocate(16);
            this.avSyncHeader.order(ByteOrder.BIG_ENDIAN);
            this.avSyncHeader.putInt(1431633921);
        }
        if (this.bytesUntilNextAvSync == 0) {
            this.avSyncHeader.putInt(4, size);
            this.avSyncHeader.putLong(8, 1000 * presentationTimeUs);
            this.avSyncHeader.position(0);
            this.bytesUntilNextAvSync = size;
        }
        int avSyncHeaderBytesRemaining = this.avSyncHeader.remaining();
        if (avSyncHeaderBytesRemaining > 0) {
            result = audioTrack.write(this.avSyncHeader, avSyncHeaderBytesRemaining, 1);
            if (result < 0) {
                this.bytesUntilNextAvSync = 0;
                return result;
            } else if (result < avSyncHeaderBytesRemaining) {
                return 0;
            }
        }
        result = writeNonBlockingV21(audioTrack, buffer, size);
        if (result < 0) {
            this.bytesUntilNextAvSync = 0;
            return result;
        }
        this.bytesUntilNextAvSync -= result;
        return result;
    }

    @TargetApi(21)
    private static void setVolumeInternalV21(AudioTrack audioTrack, float volume) {
        audioTrack.setVolume(volume);
    }

    private static void setVolumeInternalV3(AudioTrack audioTrack, float volume) {
        audioTrack.setStereoVolume(volume, volume);
    }
}
