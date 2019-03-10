package com.google.android.exoplayer2.audio;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.audio.AudioSink.InitializationException;
import com.google.android.exoplayer2.audio.AudioSink.Listener;
import com.google.android.exoplayer2.audio.AudioSink.WriteException;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.mediacodec.MediaCodecInfo;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException;
import com.google.android.exoplayer2.mediacodec.MediaFormatUtil;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MediaClock;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

@TargetApi(16)
public class MediaCodecAudioRenderer extends MediaCodecRenderer implements MediaClock {
    private static final int MAX_PENDING_STREAM_CHANGE_COUNT = 10;
    private static final String TAG = "MediaCodecAudioRenderer";
    private boolean allowFirstBufferPositionDiscontinuity;
    private boolean allowPositionDiscontinuity;
    private final AudioSink audioSink;
    private int channelCount;
    private int codecMaxInputSize;
    private boolean codecNeedsDiscardChannelsWorkaround;
    private boolean codecNeedsEosBufferTimestampWorkaround;
    private final Context context;
    private long currentPositionUs;
    private int encoderDelay;
    private int encoderPadding;
    private final AudioRendererEventListener$EventDispatcher eventDispatcher;
    private long lastInputTimeUs;
    private boolean passthroughEnabled;
    private MediaFormat passthroughMediaFormat;
    private int pcmEncoding;
    private int pendingStreamChangeCount;
    private final long[] pendingStreamChangeTimesUs;

    private final class AudioSinkListener implements Listener {
        private AudioSinkListener() {
        }

        public void onAudioSessionId(int audioSessionId) {
            MediaCodecAudioRenderer.this.eventDispatcher.audioSessionId(audioSessionId);
            MediaCodecAudioRenderer.this.onAudioSessionId(audioSessionId);
        }

        public void onPositionDiscontinuity() {
            MediaCodecAudioRenderer.this.onAudioTrackPositionDiscontinuity();
            MediaCodecAudioRenderer.this.allowPositionDiscontinuity = true;
        }

        public void onUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
            MediaCodecAudioRenderer.this.eventDispatcher.audioTrackUnderrun(bufferSize, bufferSizeMs, elapsedSinceLastFeedMs);
            MediaCodecAudioRenderer.this.onAudioTrackUnderrun(bufferSize, bufferSizeMs, elapsedSinceLastFeedMs);
        }
    }

    protected void onOutputFormatChanged(android.media.MediaCodec r14, android.media.MediaFormat r15) throws com.google.android.exoplayer2.ExoPlaybackException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:23:0x0057 in {2, 3, 13, 14, 15, 19, 22} preds:[]
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
        r0 = r13.passthroughMediaFormat;
        if (r0 == 0) goto L_0x0011;
    L_0x0004:
        r1 = "mime";
        r0 = r0.getString(r1);
        r0 = com.google.android.exoplayer2.util.MimeTypes.getEncoding(r0);
        r1 = r13.passthroughMediaFormat;
        goto L_0x0014;
    L_0x0011:
        r0 = r13.pcmEncoding;
        r1 = r15;
    L_0x0014:
        r2 = "channel-count";
        r10 = r1.getInteger(r2);
        r2 = "sample-rate";
        r11 = r1.getInteger(r2);
        r2 = r13.codecNeedsDiscardChannelsWorkaround;
        if (r2 == 0) goto L_0x003a;
    L_0x0025:
        r2 = 6;
        if (r10 != r2) goto L_0x003a;
    L_0x0028:
        r3 = r13.channelCount;
        if (r3 >= r2) goto L_0x003a;
    L_0x002c:
        r2 = new int[r3];
        r3 = 0;
    L_0x002f:
        r4 = r13.channelCount;
        if (r3 >= r4) goto L_0x0038;
    L_0x0033:
        r2[r3] = r3;
        r3 = r3 + 1;
        goto L_0x002f;
    L_0x0038:
        r12 = r2;
        goto L_0x003d;
        r2 = 0;
        r12 = r2;
    L_0x003d:
        r2 = r13.audioSink;	 Catch:{ ConfigurationException -> 0x004d }
        r6 = 0;	 Catch:{ ConfigurationException -> 0x004d }
        r8 = r13.encoderDelay;	 Catch:{ ConfigurationException -> 0x004d }
        r9 = r13.encoderPadding;	 Catch:{ ConfigurationException -> 0x004d }
        r3 = r0;	 Catch:{ ConfigurationException -> 0x004d }
        r4 = r10;	 Catch:{ ConfigurationException -> 0x004d }
        r5 = r11;	 Catch:{ ConfigurationException -> 0x004d }
        r7 = r12;	 Catch:{ ConfigurationException -> 0x004d }
        r2.configure(r3, r4, r5, r6, r7, r8, r9);	 Catch:{ ConfigurationException -> 0x004d }
        return;
    L_0x004d:
        r2 = move-exception;
        r3 = r13.getIndex();
        r3 = com.google.android.exoplayer2.ExoPlaybackException.createForRenderer(r2, r3);
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.audio.MediaCodecAudioRenderer.onOutputFormatChanged(android.media.MediaCodec, android.media.MediaFormat):void");
    }

    public MediaCodecAudioRenderer(Context context, MediaCodecSelector mediaCodecSelector) {
        this(context, mediaCodecSelector, null, false);
    }

    public MediaCodecAudioRenderer(Context context, MediaCodecSelector mediaCodecSelector, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys) {
        this(context, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, null, null);
    }

    public MediaCodecAudioRenderer(Context context, MediaCodecSelector mediaCodecSelector, @Nullable Handler eventHandler, @Nullable AudioRendererEventListener eventListener) {
        this(context, mediaCodecSelector, null, false, eventHandler, eventListener);
    }

    public MediaCodecAudioRenderer(Context context, MediaCodecSelector mediaCodecSelector, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, @Nullable Handler eventHandler, @Nullable AudioRendererEventListener eventListener) {
        this(context, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, eventHandler, eventListener, (AudioCapabilities) null, new AudioProcessor[0]);
    }

    public MediaCodecAudioRenderer(Context context, MediaCodecSelector mediaCodecSelector, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, @Nullable Handler eventHandler, @Nullable AudioRendererEventListener eventListener, @Nullable AudioCapabilities audioCapabilities, AudioProcessor... audioProcessors) {
        this(context, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, eventHandler, eventListener, new DefaultAudioSink(audioCapabilities, audioProcessors));
    }

    public MediaCodecAudioRenderer(Context context, MediaCodecSelector mediaCodecSelector, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, @Nullable Handler eventHandler, @Nullable AudioRendererEventListener eventListener, AudioSink audioSink) {
        super(1, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, 44100.0f);
        this.context = context.getApplicationContext();
        this.audioSink = audioSink;
        this.lastInputTimeUs = C0555C.TIME_UNSET;
        this.pendingStreamChangeTimesUs = new long[10];
        this.eventDispatcher = new AudioRendererEventListener$EventDispatcher(eventHandler, eventListener);
        audioSink.setListener(new AudioSinkListener());
    }

    protected int supportsFormat(MediaCodecSelector mediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, Format format) throws DecoderQueryException {
        String mimeType = format.sampleMimeType;
        if (!MimeTypes.isAudio(mimeType)) {
            return 0;
        }
        int tunnelingSupport = Util.SDK_INT >= 21 ? 32 : 0;
        boolean supportsFormatDrm = BaseRenderer.supportsFormatDrm(drmSessionManager, format.drmInitData);
        int formatSupport = 4;
        if (supportsFormatDrm) {
            if (allowPassthrough(format.channelCount, mimeType)) {
                if (mediaCodecSelector.getPassthroughDecoderInfo() != null) {
                    return (tunnelingSupport | 8) | 4;
                }
            }
        }
        int i = 1;
        if (MimeTypes.AUDIO_RAW.equals(mimeType)) {
            if (!this.audioSink.supportsOutput(format.channelCount, format.pcmEncoding)) {
                return 1;
            }
        }
        if (this.audioSink.supportsOutput(format.channelCount, 2)) {
            boolean requiresSecureDecryption = false;
            DrmInitData drmInitData = format.drmInitData;
            if (drmInitData != null) {
                for (int i2 = 0; i2 < drmInitData.schemeDataCount; i2++) {
                    requiresSecureDecryption |= drmInitData.get(i2).requiresSecureDecryption;
                }
            }
            List<MediaCodecInfo> decoderInfos = mediaCodecSelector.getDecoderInfos(format.sampleMimeType, requiresSecureDecryption);
            if (decoderInfos.isEmpty()) {
                if (requiresSecureDecryption) {
                    if (!mediaCodecSelector.getDecoderInfos(format.sampleMimeType, false).isEmpty()) {
                        i = 2;
                        return i;
                    }
                }
                return i;
            } else if (!supportsFormatDrm) {
                return 2;
            } else {
                int adaptiveSupport;
                MediaCodecInfo decoderInfo = (MediaCodecInfo) decoderInfos.get(0);
                boolean isFormatSupported = decoderInfo.isFormatSupported(format);
                if (isFormatSupported) {
                    if (decoderInfo.isSeamlessAdaptationSupported(format)) {
                        adaptiveSupport = 16;
                        if (isFormatSupported) {
                            formatSupport = 3;
                        }
                        return (adaptiveSupport | tunnelingSupport) | formatSupport;
                    }
                }
                adaptiveSupport = 8;
                if (isFormatSupported) {
                    formatSupport = 3;
                }
                return (adaptiveSupport | tunnelingSupport) | formatSupport;
            }
        }
        return 1;
    }

    protected List<MediaCodecInfo> getDecoderInfos(MediaCodecSelector mediaCodecSelector, Format format, boolean requiresSecureDecoder) throws DecoderQueryException {
        if (allowPassthrough(format.channelCount, format.sampleMimeType)) {
            MediaCodecInfo passthroughDecoderInfo = mediaCodecSelector.getPassthroughDecoderInfo();
            if (passthroughDecoderInfo != null) {
                return Collections.singletonList(passthroughDecoderInfo);
            }
        }
        return super.getDecoderInfos(mediaCodecSelector, format, requiresSecureDecoder);
    }

    protected boolean allowPassthrough(int channelCount, String mimeType) {
        return this.audioSink.supportsOutput(channelCount, MimeTypes.getEncoding(mimeType));
    }

    protected void configureCodec(MediaCodecInfo codecInfo, MediaCodec codec, Format format, MediaCrypto crypto, float codecOperatingRate) {
        this.codecMaxInputSize = getCodecMaxInputSize(codecInfo, format, getStreamFormats());
        this.codecNeedsDiscardChannelsWorkaround = codecNeedsDiscardChannelsWorkaround(codecInfo.name);
        this.codecNeedsEosBufferTimestampWorkaround = codecNeedsEosBufferTimestampWorkaround(codecInfo.name);
        this.passthroughEnabled = codecInfo.passthrough;
        MediaFormat mediaFormat = getMediaFormat(format, codecInfo.mimeType == null ? MimeTypes.AUDIO_RAW : codecInfo.mimeType, this.codecMaxInputSize, codecOperatingRate);
        codec.configure(mediaFormat, null, crypto, 0);
        if (this.passthroughEnabled) {
            this.passthroughMediaFormat = mediaFormat;
            this.passthroughMediaFormat.setString("mime", format.sampleMimeType);
            return;
        }
        this.passthroughMediaFormat = null;
    }

    protected int canKeepCodec(MediaCodec codec, MediaCodecInfo codecInfo, Format oldFormat, Format newFormat) {
        if (getCodecMaxInputSize(codecInfo, newFormat) <= this.codecMaxInputSize) {
            if (codecInfo.isSeamlessAdaptationSupported(oldFormat, newFormat, true) && oldFormat.encoderDelay == 0 && oldFormat.encoderPadding == 0 && newFormat.encoderDelay == 0 && newFormat.encoderPadding == 0) {
                return 1;
            }
        }
        return 0;
    }

    public MediaClock getMediaClock() {
        return this;
    }

    protected float getCodecOperatingRate(float operatingRate, Format format, Format[] streamFormats) {
        int maxSampleRate = -1;
        for (Format streamFormat : streamFormats) {
            int streamSampleRate = streamFormat.sampleRate;
            if (streamSampleRate != -1) {
                maxSampleRate = Math.max(maxSampleRate, streamSampleRate);
            }
        }
        return maxSampleRate == -1 ? -1.0f : ((float) maxSampleRate) * operatingRate;
    }

    protected void onCodecInitialized(String name, long initializedTimestampMs, long initializationDurationMs) {
        this.eventDispatcher.decoderInitialized(name, initializedTimestampMs, initializationDurationMs);
    }

    protected void onInputFormatChanged(Format newFormat) throws ExoPlaybackException {
        super.onInputFormatChanged(newFormat);
        this.eventDispatcher.inputFormatChanged(newFormat);
        this.pcmEncoding = MimeTypes.AUDIO_RAW.equals(newFormat.sampleMimeType) ? newFormat.pcmEncoding : 2;
        this.channelCount = newFormat.channelCount;
        this.encoderDelay = newFormat.encoderDelay;
        this.encoderPadding = newFormat.encoderPadding;
    }

    protected void onAudioSessionId(int audioSessionId) {
    }

    protected void onAudioTrackPositionDiscontinuity() {
    }

    protected void onAudioTrackUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
    }

    protected void onEnabled(boolean joining) throws ExoPlaybackException {
        super.onEnabled(joining);
        this.eventDispatcher.enabled(this.decoderCounters);
        int tunnelingAudioSessionId = getConfiguration().tunnelingAudioSessionId;
        if (tunnelingAudioSessionId != 0) {
            this.audioSink.enableTunnelingV21(tunnelingAudioSessionId);
        } else {
            this.audioSink.disableTunneling();
        }
    }

    protected void onStreamChanged(Format[] formats, long offsetUs) throws ExoPlaybackException {
        super.onStreamChanged(formats, offsetUs);
        if (this.lastInputTimeUs != C0555C.TIME_UNSET) {
            int i = this.pendingStreamChangeCount;
            if (i == this.pendingStreamChangeTimesUs.length) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Too many stream changes, so dropping change at ");
                stringBuilder.append(this.pendingStreamChangeTimesUs[this.pendingStreamChangeCount - 1]);
                Log.m10w(str, stringBuilder.toString());
            } else {
                this.pendingStreamChangeCount = i + 1;
            }
            this.pendingStreamChangeTimesUs[this.pendingStreamChangeCount - 1] = this.lastInputTimeUs;
        }
    }

    protected void onPositionReset(long positionUs, boolean joining) throws ExoPlaybackException {
        super.onPositionReset(positionUs, joining);
        this.audioSink.reset();
        this.currentPositionUs = positionUs;
        this.allowFirstBufferPositionDiscontinuity = true;
        this.allowPositionDiscontinuity = true;
        this.lastInputTimeUs = C0555C.TIME_UNSET;
        this.pendingStreamChangeCount = 0;
    }

    protected void onStarted() {
        super.onStarted();
        this.audioSink.play();
    }

    protected void onStopped() {
        updateCurrentPosition();
        this.audioSink.pause();
        super.onStopped();
    }

    protected void onDisabled() {
        try {
            this.lastInputTimeUs = C0555C.TIME_UNSET;
            this.pendingStreamChangeCount = 0;
            this.audioSink.release();
            try {
                super.onDisabled();
            } finally {
                this.decoderCounters.ensureUpdated();
                this.eventDispatcher.disabled(this.decoderCounters);
            }
        } catch (Throwable th) {
            super.onDisabled();
        } finally {
            this.decoderCounters.ensureUpdated();
            this.eventDispatcher.disabled(this.decoderCounters);
        }
    }

    public boolean isEnded() {
        return super.isEnded() && this.audioSink.isEnded();
    }

    public boolean isReady() {
        if (!this.audioSink.hasPendingData()) {
            if (!super.isReady()) {
                return false;
            }
        }
        return true;
    }

    public long getPositionUs() {
        if (getState() == 2) {
            updateCurrentPosition();
        }
        return this.currentPositionUs;
    }

    public PlaybackParameters setPlaybackParameters(PlaybackParameters playbackParameters) {
        return this.audioSink.setPlaybackParameters(playbackParameters);
    }

    public PlaybackParameters getPlaybackParameters() {
        return this.audioSink.getPlaybackParameters();
    }

    protected void onQueueInputBuffer(DecoderInputBuffer buffer) {
        if (this.allowFirstBufferPositionDiscontinuity && !buffer.isDecodeOnly()) {
            if (Math.abs(buffer.timeUs - this.currentPositionUs) > 500000) {
                this.currentPositionUs = buffer.timeUs;
            }
            this.allowFirstBufferPositionDiscontinuity = false;
        }
        this.lastInputTimeUs = Math.max(buffer.timeUs, this.lastInputTimeUs);
    }

    @CallSuper
    protected void onProcessedOutputBuffer(long presentationTimeUs) {
        while (this.pendingStreamChangeCount != 0 && presentationTimeUs >= this.pendingStreamChangeTimesUs[0]) {
            this.audioSink.handleDiscontinuity();
            this.pendingStreamChangeCount--;
            Object obj = this.pendingStreamChangeTimesUs;
            System.arraycopy(obj, 1, obj, 0, this.pendingStreamChangeCount);
        }
    }

    protected boolean processOutputBuffer(long positionUs, long elapsedRealtimeUs, MediaCodec codec, ByteBuffer buffer, int bufferIndex, int bufferFlags, long bufferPresentationTimeUs, boolean shouldSkip, Format format) throws ExoPlaybackException {
        long bufferPresentationTimeUs2;
        ByteBuffer byteBuffer;
        Exception e;
        MediaCodec mediaCodec = codec;
        int i = bufferIndex;
        if (!this.codecNeedsEosBufferTimestampWorkaround || bufferPresentationTimeUs != 0 || (bufferFlags & 4) == 0 || r1.lastInputTimeUs == C0555C.TIME_UNSET) {
            bufferPresentationTimeUs2 = bufferPresentationTimeUs;
        } else {
            bufferPresentationTimeUs2 = r1.lastInputTimeUs;
        }
        if (r1.passthroughEnabled && (bufferFlags & 2) != 0) {
            codec.releaseOutputBuffer(i, false);
            return true;
        } else if (shouldSkip) {
            codec.releaseOutputBuffer(i, false);
            r0 = r1.decoderCounters;
            r0.skippedOutputBufferCount++;
            r1.audioSink.handleDiscontinuity();
            return true;
        } else {
            try {
                byteBuffer = buffer;
                try {
                    if (!r1.audioSink.handleBuffer(buffer, bufferPresentationTimeUs2)) {
                        return false;
                    }
                    codec.releaseOutputBuffer(i, false);
                    r0 = r1.decoderCounters;
                    r0.renderedOutputBufferCount++;
                    return true;
                } catch (InitializationException e2) {
                    e = e2;
                    throw ExoPlaybackException.createForRenderer(e, getIndex());
                }
            } catch (InitializationException e3) {
                e = e3;
                byteBuffer = buffer;
                throw ExoPlaybackException.createForRenderer(e, getIndex());
            }
        }
    }

    protected void renderToEndOfStream() throws ExoPlaybackException {
        try {
            this.audioSink.playToEndOfStream();
        } catch (WriteException e) {
            throw ExoPlaybackException.createForRenderer(e, getIndex());
        }
    }

    public void handleMessage(int messageType, @Nullable Object message) throws ExoPlaybackException {
        if (messageType != 5) {
            switch (messageType) {
                case 2:
                    this.audioSink.setVolume(((Float) message).floatValue());
                    return;
                case 3:
                    this.audioSink.setAudioAttributes((AudioAttributes) message);
                    return;
                default:
                    super.handleMessage(messageType, message);
                    return;
            }
        }
        this.audioSink.setAuxEffectInfo((AuxEffectInfo) message);
    }

    protected int getCodecMaxInputSize(MediaCodecInfo codecInfo, Format format, Format[] streamFormats) {
        int maxInputSize = getCodecMaxInputSize(codecInfo, format);
        if (streamFormats.length == 1) {
            return maxInputSize;
        }
        int maxInputSize2 = maxInputSize;
        for (Format streamFormat : streamFormats) {
            if (codecInfo.isSeamlessAdaptationSupported(format, streamFormat, false)) {
                maxInputSize2 = Math.max(maxInputSize2, getCodecMaxInputSize(codecInfo, streamFormat));
            }
        }
        return maxInputSize2;
    }

    private int getCodecMaxInputSize(MediaCodecInfo codecInfo, Format format) {
        if (Util.SDK_INT < 24 && "OMX.google.raw.decoder".equals(codecInfo.name)) {
            boolean needsRawDecoderWorkaround = true;
            if (Util.SDK_INT == 23) {
                PackageManager packageManager = this.context.getPackageManager();
                if (packageManager != null) {
                    if (packageManager.hasSystemFeature("android.software.leanback")) {
                        needsRawDecoderWorkaround = false;
                    }
                }
            }
            if (needsRawDecoderWorkaround) {
                return -1;
            }
        }
        return format.maxInputSize;
    }

    @SuppressLint({"InlinedApi"})
    protected MediaFormat getMediaFormat(Format format, String codecMimeType, int codecMaxInputSize, float codecOperatingRate) {
        MediaFormat mediaFormat = new MediaFormat();
        mediaFormat.setString("mime", codecMimeType);
        mediaFormat.setInteger("channel-count", format.channelCount);
        mediaFormat.setInteger("sample-rate", format.sampleRate);
        MediaFormatUtil.setCsdBuffers(mediaFormat, format.initializationData);
        MediaFormatUtil.maybeSetInteger(mediaFormat, "max-input-size", codecMaxInputSize);
        if (Util.SDK_INT >= 23) {
            mediaFormat.setInteger("priority", 0);
            if (codecOperatingRate != -1.0f) {
                mediaFormat.setFloat("operating-rate", codecOperatingRate);
            }
        }
        return mediaFormat;
    }

    private void updateCurrentPosition() {
        long newCurrentPositionUs = this.audioSink.getCurrentPositionUs(isEnded());
        if (newCurrentPositionUs != Long.MIN_VALUE) {
            long j;
            if (this.allowPositionDiscontinuity) {
                j = newCurrentPositionUs;
            } else {
                j = Math.max(this.currentPositionUs, newCurrentPositionUs);
            }
            this.currentPositionUs = j;
            this.allowPositionDiscontinuity = false;
        }
    }

    private static boolean codecNeedsDiscardChannelsWorkaround(String codecName) {
        if (Util.SDK_INT < 24 && "OMX.SEC.aac.dec".equals(codecName)) {
            if ("samsung".equals(Util.MANUFACTURER)) {
                if (!Util.DEVICE.startsWith("zeroflte") && !Util.DEVICE.startsWith("herolte")) {
                    if (Util.DEVICE.startsWith("heroqlte")) {
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static boolean codecNeedsEosBufferTimestampWorkaround(String codecName) {
        if (Util.SDK_INT < 21) {
            if ("OMX.SEC.mp3.dec".equals(codecName)) {
                if ("samsung".equals(Util.MANUFACTURER)) {
                    if (!Util.DEVICE.startsWith("baffin")) {
                        if (!Util.DEVICE.startsWith("grand")) {
                            if (!Util.DEVICE.startsWith("fortuna")) {
                                if (!Util.DEVICE.startsWith("gprimelte")) {
                                    if (!Util.DEVICE.startsWith("j2y18lte")) {
                                        if (Util.DEVICE.startsWith("ms01")) {
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
