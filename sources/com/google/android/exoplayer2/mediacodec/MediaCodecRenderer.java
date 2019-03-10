package com.google.android.exoplayer2.mediacodec;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodec.CodecException;
import android.media.MediaCodec.CryptoException;
import android.media.MediaCodec.CryptoInfo;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.drm.DrmSession;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.TimedValueQueue;
import com.google.android.exoplayer2.util.TraceUtil;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

@TargetApi(16)
public abstract class MediaCodecRenderer extends BaseRenderer {
    private static final byte[] ADAPTATION_WORKAROUND_BUFFER = Util.getBytesFromHexString("0000016742C00BDA259000000168CE0F13200000016588840DCE7118A0002FBF1C31C3275D78");
    private static final int ADAPTATION_WORKAROUND_MODE_ALWAYS = 2;
    private static final int ADAPTATION_WORKAROUND_MODE_NEVER = 0;
    private static final int ADAPTATION_WORKAROUND_MODE_SAME_RESOLUTION = 1;
    private static final int ADAPTATION_WORKAROUND_SLICE_WIDTH_HEIGHT = 32;
    protected static final float CODEC_OPERATING_RATE_UNSET = -1.0f;
    protected static final int KEEP_CODEC_RESULT_NO = 0;
    protected static final int KEEP_CODEC_RESULT_YES_WITHOUT_RECONFIGURATION = 1;
    protected static final int KEEP_CODEC_RESULT_YES_WITH_RECONFIGURATION = 3;
    private static final long MAX_CODEC_HOTSWAP_TIME_MS = 1000;
    private static final int RECONFIGURATION_STATE_NONE = 0;
    private static final int RECONFIGURATION_STATE_QUEUE_PENDING = 2;
    private static final int RECONFIGURATION_STATE_WRITE_PENDING = 1;
    private static final int REINITIALIZATION_STATE_NONE = 0;
    private static final int REINITIALIZATION_STATE_SIGNAL_END_OF_STREAM = 1;
    private static final int REINITIALIZATION_STATE_WAIT_END_OF_STREAM = 2;
    private static final String TAG = "MediaCodecRenderer";
    private final float assumedMinimumCodecOperatingRate;
    @Nullable
    private ArrayDeque<MediaCodecInfo> availableCodecInfos;
    private final DecoderInputBuffer buffer;
    private MediaCodec codec;
    private int codecAdaptationWorkaroundMode;
    private boolean codecConfiguredWithOperatingRate;
    private long codecHotswapDeadlineMs;
    @Nullable
    private MediaCodecInfo codecInfo;
    private boolean codecNeedsAdaptationWorkaroundBuffer;
    private boolean codecNeedsDiscardToSpsWorkaround;
    private boolean codecNeedsEosFlushWorkaround;
    private boolean codecNeedsEosOutputExceptionWorkaround;
    private boolean codecNeedsEosPropagation;
    private boolean codecNeedsFlushWorkaround;
    private boolean codecNeedsMonoChannelCountWorkaround;
    private boolean codecNeedsReconfigureWorkaround;
    private float codecOperatingRate;
    private boolean codecReceivedBuffers;
    private boolean codecReceivedEos;
    private int codecReconfigurationState;
    private boolean codecReconfigured;
    private int codecReinitializationState;
    private final List<Long> decodeOnlyPresentationTimestamps;
    protected DecoderCounters decoderCounters;
    private DrmSession<FrameworkMediaCrypto> drmSession;
    @Nullable
    private final DrmSessionManager<FrameworkMediaCrypto> drmSessionManager;
    private final DecoderInputBuffer flagsOnlyBuffer;
    private Format format;
    private final FormatHolder formatHolder;
    private final TimedValueQueue<Format> formatQueue;
    private ByteBuffer[] inputBuffers;
    private int inputIndex;
    private boolean inputStreamEnded;
    private final MediaCodecSelector mediaCodecSelector;
    private ByteBuffer outputBuffer;
    private final BufferInfo outputBufferInfo;
    private ByteBuffer[] outputBuffers;
    private Format outputFormat;
    private int outputIndex;
    private boolean outputStreamEnded;
    private DrmSession<FrameworkMediaCrypto> pendingDrmSession;
    private Format pendingFormat;
    private final boolean playClearSamplesWithoutKeys;
    @Nullable
    private DecoderInitializationException preferredDecoderInitializationException;
    private float rendererOperatingRate;
    private boolean shouldSkipAdaptationWorkaroundOutputBuffer;
    private boolean shouldSkipOutputBuffer;
    private boolean waitingForFirstSyncFrame;
    private boolean waitingForKeys;

    public static class DecoderInitializationException extends Exception {
        private static final int CUSTOM_ERROR_CODE_BASE = -50000;
        private static final int DECODER_QUERY_ERROR = -49998;
        private static final int NO_SUITABLE_DECODER_ERROR = -49999;
        public final String decoderName;
        public final String diagnosticInfo;
        @Nullable
        public final DecoderInitializationException fallbackDecoderInitializationException;
        public final String mimeType;
        public final boolean secureDecoderRequired;

        public DecoderInitializationException(Format format, Throwable cause, boolean secureDecoderRequired, int errorCode) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Decoder init failed: [");
            stringBuilder.append(errorCode);
            stringBuilder.append("], ");
            stringBuilder.append(format);
            this(stringBuilder.toString(), cause, format.sampleMimeType, secureDecoderRequired, null, buildCustomDiagnosticInfo(errorCode), null);
        }

        public DecoderInitializationException(Format format, Throwable cause, boolean secureDecoderRequired, String decoderName) {
            String diagnosticInfoV21;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Decoder init failed: ");
            stringBuilder.append(decoderName);
            stringBuilder.append(", ");
            stringBuilder.append(format);
            String stringBuilder2 = stringBuilder.toString();
            String str = format.sampleMimeType;
            if (Util.SDK_INT >= 21) {
                diagnosticInfoV21 = getDiagnosticInfoV21(cause);
            } else {
                diagnosticInfoV21 = null;
            }
            this(stringBuilder2, cause, str, secureDecoderRequired, decoderName, diagnosticInfoV21, null);
        }

        private DecoderInitializationException(String message, Throwable cause, String mimeType, boolean secureDecoderRequired, @Nullable String decoderName, @Nullable String diagnosticInfo, @Nullable DecoderInitializationException fallbackDecoderInitializationException) {
            super(message, cause);
            this.mimeType = mimeType;
            this.secureDecoderRequired = secureDecoderRequired;
            this.decoderName = decoderName;
            this.diagnosticInfo = diagnosticInfo;
            this.fallbackDecoderInitializationException = fallbackDecoderInitializationException;
        }

        @CheckResult
        private DecoderInitializationException copyWithFallbackException(DecoderInitializationException fallbackException) {
            return new DecoderInitializationException(getMessage(), getCause(), this.mimeType, this.secureDecoderRequired, this.decoderName, this.diagnosticInfo, fallbackException);
        }

        @TargetApi(21)
        private static String getDiagnosticInfoV21(Throwable cause) {
            if (cause instanceof CodecException) {
                return ((CodecException) cause).getDiagnosticInfo();
            }
            return null;
        }

        private static String buildCustomDiagnosticInfo(int errorCode) {
            String sign = errorCode < 0 ? "neg_" : "";
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("com.google.android.exoplayer.MediaCodecTrackRenderer_");
            stringBuilder.append(sign);
            stringBuilder.append(Math.abs(errorCode));
            return stringBuilder.toString();
        }
    }

    private boolean initCodecWithFallback(android.media.MediaCrypto r6, boolean r7) throws com.google.android.exoplayer2.mediacodec.MediaCodecRenderer.DecoderInitializationException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:31:0x0087 in {4, 7, 8, 14, 18, 22, 23, 26, 28, 30} preds:[]
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
        r5 = this;
        r0 = r5.availableCodecInfos;
        r1 = 0;
        if (r0 != 0) goto L_0x001f;
    L_0x0005:
        r0 = new java.util.ArrayDeque;	 Catch:{ DecoderQueryException -> 0x0013 }
        r2 = r5.getAvailableCodecInfos(r7);	 Catch:{ DecoderQueryException -> 0x0013 }
        r0.<init>(r2);	 Catch:{ DecoderQueryException -> 0x0013 }
        r5.availableCodecInfos = r0;	 Catch:{ DecoderQueryException -> 0x0013 }
        r5.preferredDecoderInitializationException = r1;	 Catch:{ DecoderQueryException -> 0x0013 }
        goto L_0x0020;
    L_0x0013:
        r0 = move-exception;
        r1 = new com.google.android.exoplayer2.mediacodec.MediaCodecRenderer$DecoderInitializationException;
        r2 = r5.format;
        r3 = -49998; // 0xffffffffffff3cb2 float:NaN double:NaN;
        r1.<init>(r2, r0, r7, r3);
        throw r1;
    L_0x0020:
        r0 = r5.availableCodecInfos;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x007c;
    L_0x0028:
        r0 = r5.availableCodecInfos;
        r0 = r0.peekFirst();
        r0 = (com.google.android.exoplayer2.mediacodec.MediaCodecInfo) r0;
        r1 = r5.shouldInitCodec(r0);
        if (r1 != 0) goto L_0x0038;
    L_0x0036:
        r1 = 0;
        return r1;
    L_0x0038:
        r5.initCodec(r0, r6);	 Catch:{ Exception -> 0x003d }
        r1 = 1;
        return r1;
    L_0x003d:
        r1 = move-exception;
        r2 = "MediaCodecRenderer";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Failed to initialize decoder: ";
        r3.append(r4);
        r3.append(r0);
        r3 = r3.toString();
        com.google.android.exoplayer2.util.Log.m11w(r2, r3, r1);
        r2 = r5.availableCodecInfos;
        r2.removeFirst();
        r2 = new com.google.android.exoplayer2.mediacodec.MediaCodecRenderer$DecoderInitializationException;
        r3 = r5.format;
        r4 = r0.name;
        r2.<init>(r3, r1, r7, r4);
        r3 = r5.preferredDecoderInitializationException;
        if (r3 != 0) goto L_0x0069;
    L_0x0066:
        r5.preferredDecoderInitializationException = r2;
        goto L_0x0070;
        r3 = r3.copyWithFallbackException(r2);
        r5.preferredDecoderInitializationException = r3;
    L_0x0070:
        r3 = r5.availableCodecInfos;
        r3 = r3.isEmpty();
        if (r3 != 0) goto L_0x0079;
    L_0x0078:
        goto L_0x0028;
    L_0x0079:
        r3 = r5.preferredDecoderInitializationException;
        throw r3;
    L_0x007c:
        r0 = new com.google.android.exoplayer2.mediacodec.MediaCodecRenderer$DecoderInitializationException;
        r2 = r5.format;
        r3 = -49999; // 0xffffffffffff3cb1 float:NaN double:NaN;
        r0.<init>(r2, r1, r7, r3);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.mediacodec.MediaCodecRenderer.initCodecWithFallback(android.media.MediaCrypto, boolean):boolean");
    }

    protected abstract void configureCodec(MediaCodecInfo mediaCodecInfo, MediaCodec mediaCodec, Format format, MediaCrypto mediaCrypto, float f) throws DecoderQueryException;

    protected abstract boolean processOutputBuffer(long j, long j2, MediaCodec mediaCodec, ByteBuffer byteBuffer, int i, int i2, long j3, boolean z, Format format) throws ExoPlaybackException;

    protected abstract int supportsFormat(MediaCodecSelector mediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, Format format) throws DecoderQueryException;

    public MediaCodecRenderer(int trackType, MediaCodecSelector mediaCodecSelector, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, float assumedMinimumCodecOperatingRate) {
        super(trackType);
        Assertions.checkState(Util.SDK_INT >= 16);
        this.mediaCodecSelector = (MediaCodecSelector) Assertions.checkNotNull(mediaCodecSelector);
        this.drmSessionManager = drmSessionManager;
        this.playClearSamplesWithoutKeys = playClearSamplesWithoutKeys;
        this.assumedMinimumCodecOperatingRate = assumedMinimumCodecOperatingRate;
        this.buffer = new DecoderInputBuffer(0);
        this.flagsOnlyBuffer = DecoderInputBuffer.newFlagsOnlyInstance();
        this.formatHolder = new FormatHolder();
        this.formatQueue = new TimedValueQueue();
        this.decodeOnlyPresentationTimestamps = new ArrayList();
        this.outputBufferInfo = new BufferInfo();
        this.codecReconfigurationState = 0;
        this.codecReinitializationState = 0;
        this.codecOperatingRate = CODEC_OPERATING_RATE_UNSET;
        this.rendererOperatingRate = 1.0f;
    }

    public final int supportsMixedMimeTypeAdaptation() {
        return 8;
    }

    public final int supportsFormat(Format format) throws ExoPlaybackException {
        try {
            return supportsFormat(this.mediaCodecSelector, this.drmSessionManager, format);
        } catch (DecoderQueryException e) {
            throw ExoPlaybackException.createForRenderer(e, getIndex());
        }
    }

    protected List<MediaCodecInfo> getDecoderInfos(MediaCodecSelector mediaCodecSelector, Format format, boolean requiresSecureDecoder) throws DecoderQueryException {
        return mediaCodecSelector.getDecoderInfos(format.sampleMimeType, requiresSecureDecoder);
    }

    protected final void maybeInitCodec() throws ExoPlaybackException {
        if (this.codec == null) {
            String mimeType = this.format;
            if (mimeType != null) {
                this.drmSession = this.pendingDrmSession;
                mimeType = mimeType.sampleMimeType;
                MediaCrypto wrappedMediaCrypto = null;
                boolean drmSessionRequiresSecureDecoder = false;
                DrmSession drmSession = this.drmSession;
                if (drmSession != null) {
                    FrameworkMediaCrypto mediaCrypto = (FrameworkMediaCrypto) drmSession.getMediaCrypto();
                    if (mediaCrypto != null) {
                        wrappedMediaCrypto = mediaCrypto.getWrappedMediaCrypto();
                        drmSessionRequiresSecureDecoder = mediaCrypto.requiresSecureDecoderComponent(mimeType);
                    } else if (this.drmSession.getError() == null) {
                        return;
                    }
                    if (deviceNeedsDrmKeysToConfigureCodecWorkaround()) {
                        int drmSessionState = this.drmSession.getState();
                        if (drmSessionState == 1) {
                            throw ExoPlaybackException.createForRenderer(this.drmSession.getError(), getIndex());
                        } else if (drmSessionState != 4) {
                            return;
                        }
                    }
                }
                try {
                    if (initCodecWithFallback(wrappedMediaCrypto, drmSessionRequiresSecureDecoder)) {
                        boolean z;
                        DecoderCounters decoderCounters;
                        String codecName = this.codecInfo.name;
                        this.codecAdaptationWorkaroundMode = codecAdaptationWorkaroundMode(codecName);
                        this.codecNeedsReconfigureWorkaround = codecNeedsReconfigureWorkaround(codecName);
                        this.codecNeedsDiscardToSpsWorkaround = codecNeedsDiscardToSpsWorkaround(codecName, this.format);
                        this.codecNeedsFlushWorkaround = codecNeedsFlushWorkaround(codecName);
                        this.codecNeedsEosFlushWorkaround = codecNeedsEosFlushWorkaround(codecName);
                        this.codecNeedsEosOutputExceptionWorkaround = codecNeedsEosOutputExceptionWorkaround(codecName);
                        this.codecNeedsMonoChannelCountWorkaround = codecNeedsMonoChannelCountWorkaround(codecName, this.format);
                        if (!codecNeedsEosPropagationWorkaround(this.codecInfo)) {
                            if (!getCodecNeedsEosPropagation()) {
                                z = false;
                                this.codecNeedsEosPropagation = z;
                                this.codecHotswapDeadlineMs = getState() != 2 ? SystemClock.elapsedRealtime() + 1000 : C0555C.TIME_UNSET;
                                resetInputBuffer();
                                resetOutputBuffer();
                                this.waitingForFirstSyncFrame = true;
                                decoderCounters = this.decoderCounters;
                                decoderCounters.decoderInitCount++;
                            }
                        }
                        z = true;
                        this.codecNeedsEosPropagation = z;
                        if (getState() != 2) {
                        }
                        this.codecHotswapDeadlineMs = getState() != 2 ? SystemClock.elapsedRealtime() + 1000 : C0555C.TIME_UNSET;
                        resetInputBuffer();
                        resetOutputBuffer();
                        this.waitingForFirstSyncFrame = true;
                        decoderCounters = this.decoderCounters;
                        decoderCounters.decoderInitCount++;
                    }
                } catch (DecoderInitializationException e) {
                    throw ExoPlaybackException.createForRenderer(e, getIndex());
                }
            }
        }
    }

    protected boolean shouldInitCodec(MediaCodecInfo codecInfo) {
        return true;
    }

    protected boolean getCodecNeedsEosPropagation() {
        return false;
    }

    @Nullable
    protected final Format updateOutputFormatForTime(long presentationTimeUs) {
        Format format = (Format) this.formatQueue.pollFloor(presentationTimeUs);
        if (format != null) {
            this.outputFormat = format;
        }
        return format;
    }

    protected final MediaCodec getCodec() {
        return this.codec;
    }

    @Nullable
    protected final MediaCodecInfo getCodecInfo() {
        return this.codecInfo;
    }

    protected void onEnabled(boolean joining) throws ExoPlaybackException {
        this.decoderCounters = new DecoderCounters();
    }

    protected void onPositionReset(long positionUs, boolean joining) throws ExoPlaybackException {
        this.inputStreamEnded = false;
        this.outputStreamEnded = false;
        if (this.codec != null) {
            flushCodec();
        }
        this.formatQueue.clear();
    }

    public final void setOperatingRate(float operatingRate) throws ExoPlaybackException {
        this.rendererOperatingRate = operatingRate;
        updateCodecOperatingRate();
    }

    protected void onDisabled() {
        this.format = null;
        this.availableCodecInfos = null;
        try {
            releaseCodec();
            try {
                if (this.drmSession != null) {
                    this.drmSessionManager.releaseSession(this.drmSession);
                }
                try {
                    if (this.pendingDrmSession != null && this.pendingDrmSession != this.drmSession) {
                        this.drmSessionManager.releaseSession(this.pendingDrmSession);
                    }
                    this.drmSession = null;
                    this.pendingDrmSession = null;
                } catch (Throwable th) {
                    this.drmSession = null;
                    this.pendingDrmSession = null;
                }
            } catch (Throwable th2) {
                this.drmSession = null;
                this.pendingDrmSession = null;
            }
        } catch (Throwable th3) {
            this.drmSession = null;
            this.pendingDrmSession = null;
        }
    }

    protected void releaseCodec() {
        DrmSession drmSession;
        this.codecHotswapDeadlineMs = C0555C.TIME_UNSET;
        resetInputBuffer();
        resetOutputBuffer();
        this.waitingForKeys = false;
        this.shouldSkipOutputBuffer = false;
        this.decodeOnlyPresentationTimestamps.clear();
        resetCodecBuffers();
        this.codecInfo = null;
        this.codecReconfigured = false;
        this.codecReceivedBuffers = false;
        this.codecNeedsDiscardToSpsWorkaround = false;
        this.codecNeedsFlushWorkaround = false;
        this.codecAdaptationWorkaroundMode = 0;
        this.codecNeedsReconfigureWorkaround = false;
        this.codecNeedsEosFlushWorkaround = false;
        this.codecNeedsMonoChannelCountWorkaround = false;
        this.codecNeedsAdaptationWorkaroundBuffer = false;
        this.shouldSkipAdaptationWorkaroundOutputBuffer = false;
        this.codecNeedsEosPropagation = false;
        this.codecReceivedEos = false;
        this.codecReconfigurationState = 0;
        this.codecReinitializationState = 0;
        this.codecConfiguredWithOperatingRate = false;
        if (this.codec != null) {
            DecoderCounters decoderCounters = this.decoderCounters;
            decoderCounters.decoderReleaseCount++;
            try {
                this.codec.stop();
                try {
                    this.codec.release();
                    this.codec = null;
                    DrmSession drmSession2 = this.drmSession;
                    if (drmSession2 != null && this.pendingDrmSession != drmSession2) {
                        try {
                            this.drmSessionManager.releaseSession(drmSession2);
                        } finally {
                            this.drmSession = null;
                        }
                    }
                } catch (Throwable th) {
                    this.codec = null;
                    drmSession = this.drmSession;
                    if (drmSession != null && this.pendingDrmSession != drmSession) {
                        this.drmSessionManager.releaseSession(drmSession);
                    }
                } finally {
                    this.drmSession = null;
                }
            } catch (Throwable th2) {
                this.codec = null;
                drmSession = this.drmSession;
                if (drmSession != null && this.pendingDrmSession != drmSession) {
                    try {
                        this.drmSessionManager.releaseSession(drmSession);
                    } finally {
                        this.drmSession = null;
                    }
                }
            } finally {
                this.drmSession = null;
            }
        }
    }

    protected void onStarted() {
    }

    protected void onStopped() {
    }

    public void render(long positionUs, long elapsedRealtimeUs) throws ExoPlaybackException {
        if (this.outputStreamEnded) {
            renderToEndOfStream();
            return;
        }
        int result;
        if (this.format == null) {
            this.flagsOnlyBuffer.clear();
            result = readSource(this.formatHolder, this.flagsOnlyBuffer, true);
            if (result == -5) {
                onInputFormatChanged(this.formatHolder.format);
            } else if (result == -4) {
                Assertions.checkState(this.flagsOnlyBuffer.isEndOfStream());
                this.inputStreamEnded = true;
                processEndOfStream();
                return;
            } else {
                return;
            }
        }
        maybeInitCodec();
        if (this.codec != null) {
            TraceUtil.beginSection("drainAndFeed");
            while (drainOutputBuffer(positionUs, elapsedRealtimeUs)) {
            }
            while (feedInputBuffer()) {
            }
            TraceUtil.endSection();
        } else {
            DecoderCounters decoderCounters = this.decoderCounters;
            decoderCounters.skippedInputBufferCount += skipSource(positionUs);
            this.flagsOnlyBuffer.clear();
            result = readSource(this.formatHolder, this.flagsOnlyBuffer, false);
            if (result == -5) {
                onInputFormatChanged(this.formatHolder.format);
            } else if (result == -4) {
                Assertions.checkState(this.flagsOnlyBuffer.isEndOfStream());
                this.inputStreamEnded = true;
                processEndOfStream();
            }
        }
        this.decoderCounters.ensureUpdated();
    }

    protected void flushCodec() throws ExoPlaybackException {
        this.codecHotswapDeadlineMs = C0555C.TIME_UNSET;
        resetInputBuffer();
        resetOutputBuffer();
        this.waitingForFirstSyncFrame = true;
        this.waitingForKeys = false;
        this.shouldSkipOutputBuffer = false;
        this.decodeOnlyPresentationTimestamps.clear();
        this.codecNeedsAdaptationWorkaroundBuffer = false;
        this.shouldSkipAdaptationWorkaroundOutputBuffer = false;
        if (!this.codecNeedsFlushWorkaround) {
            if (!this.codecNeedsEosFlushWorkaround || !this.codecReceivedEos) {
                if (this.codecReinitializationState != 0) {
                    releaseCodec();
                    maybeInitCodec();
                } else {
                    this.codec.flush();
                    this.codecReceivedBuffers = false;
                }
                if (!this.codecReconfigured && this.format != null) {
                    this.codecReconfigurationState = 1;
                    return;
                }
            }
        }
        releaseCodec();
        maybeInitCodec();
        if (!this.codecReconfigured) {
        }
    }

    private List<MediaCodecInfo> getAvailableCodecInfos(boolean drmSessionRequiresSecureDecoder) throws DecoderQueryException {
        List<MediaCodecInfo> codecInfos = getDecoderInfos(this.mediaCodecSelector, this.format, drmSessionRequiresSecureDecoder);
        if (codecInfos.isEmpty() && drmSessionRequiresSecureDecoder) {
            codecInfos = getDecoderInfos(this.mediaCodecSelector, this.format, false);
            if (!codecInfos.isEmpty()) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Drm session requires secure decoder for ");
                stringBuilder.append(this.format.sampleMimeType);
                stringBuilder.append(", but no secure decoder available. Trying to proceed with ");
                stringBuilder.append(codecInfos);
                stringBuilder.append(".");
                Log.m10w(str, stringBuilder.toString());
            }
        }
        return codecInfos;
    }

    private void initCodec(MediaCodecInfo codecInfo, MediaCrypto crypto) throws Exception {
        MediaCodec codec;
        Exception e;
        MediaCodecInfo mediaCodecInfo = codecInfo;
        String name = mediaCodecInfo.name;
        updateCodecOperatingRate();
        boolean configureWithOperatingRate = this.codecOperatingRate > this.assumedMinimumCodecOperatingRate;
        try {
            long codecInitializingTimestamp = SystemClock.elapsedRealtime();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("createCodec:");
            stringBuilder.append(name);
            TraceUtil.beginSection(stringBuilder.toString());
            codec = MediaCodec.createByCodecName(name);
            try {
                TraceUtil.endSection();
                TraceUtil.beginSection("configureCodec");
                configureCodec(codecInfo, codec, r7.format, crypto, configureWithOperatingRate ? r7.codecOperatingRate : CODEC_OPERATING_RATE_UNSET);
                r7.codecConfiguredWithOperatingRate = configureWithOperatingRate;
                TraceUtil.endSection();
                TraceUtil.beginSection("startCodec");
                codec.start();
                TraceUtil.endSection();
                long codecInitializedTimestamp = SystemClock.elapsedRealtime();
                getCodecBuffers(codec);
                r7.codec = codec;
                r7.codecInfo = mediaCodecInfo;
                onCodecInitialized(name, codecInitializedTimestamp, codecInitializedTimestamp - codecInitializingTimestamp);
            } catch (Exception e2) {
                e = e2;
                if (codec != null) {
                    resetCodecBuffers();
                    codec.release();
                }
                throw e;
            }
        } catch (Exception e3) {
            e = e3;
            codec = null;
            if (codec != null) {
                resetCodecBuffers();
                codec.release();
            }
            throw e;
        }
    }

    private void getCodecBuffers(MediaCodec codec) {
        if (Util.SDK_INT < 21) {
            this.inputBuffers = codec.getInputBuffers();
            this.outputBuffers = codec.getOutputBuffers();
        }
    }

    private void resetCodecBuffers() {
        if (Util.SDK_INT < 21) {
            this.inputBuffers = null;
            this.outputBuffers = null;
        }
    }

    private ByteBuffer getInputBuffer(int inputIndex) {
        if (Util.SDK_INT >= 21) {
            return this.codec.getInputBuffer(inputIndex);
        }
        return this.inputBuffers[inputIndex];
    }

    private ByteBuffer getOutputBuffer(int outputIndex) {
        if (Util.SDK_INT >= 21) {
            return this.codec.getOutputBuffer(outputIndex);
        }
        return this.outputBuffers[outputIndex];
    }

    private boolean hasOutputBuffer() {
        return this.outputIndex >= 0;
    }

    private void resetInputBuffer() {
        this.inputIndex = -1;
        this.buffer.data = null;
    }

    private void resetOutputBuffer() {
        this.outputIndex = -1;
        this.outputBuffer = null;
    }

    private boolean feedInputBuffer() throws ExoPlaybackException {
        MediaCodec mediaCodec = this.codec;
        if (!(mediaCodec == null || this.codecReinitializationState == 2)) {
            if (!this.inputStreamEnded) {
                int i;
                if (this.inputIndex < 0) {
                    this.inputIndex = mediaCodec.dequeueInputBuffer(0);
                    i = this.inputIndex;
                    if (i < 0) {
                        return false;
                    }
                    this.buffer.data = getInputBuffer(i);
                    this.buffer.clear();
                }
                if (this.codecReinitializationState == 1) {
                    if (!this.codecNeedsEosPropagation) {
                        this.codecReceivedEos = true;
                        this.codec.queueInputBuffer(this.inputIndex, 0, 0, 0, 4);
                        resetInputBuffer();
                    }
                    this.codecReinitializationState = 2;
                    return false;
                } else if (this.codecNeedsAdaptationWorkaroundBuffer) {
                    this.codecNeedsAdaptationWorkaroundBuffer = false;
                    this.buffer.data.put(ADAPTATION_WORKAROUND_BUFFER);
                    this.codec.queueInputBuffer(this.inputIndex, 0, ADAPTATION_WORKAROUND_BUFFER.length, 0, 0);
                    resetInputBuffer();
                    this.codecReceivedBuffers = true;
                    return true;
                } else {
                    int result;
                    i = 0;
                    if (this.waitingForKeys) {
                        result = -4;
                    } else {
                        if (this.codecReconfigurationState == 1) {
                            for (result = 0; result < this.format.initializationData.size(); result++) {
                                this.buffer.data.put((byte[]) this.format.initializationData.get(result));
                            }
                            this.codecReconfigurationState = 2;
                        }
                        i = this.buffer.data.position();
                        result = readSource(this.formatHolder, this.buffer, false);
                    }
                    if (result == -3) {
                        return false;
                    }
                    if (result == -5) {
                        if (this.codecReconfigurationState == 2) {
                            this.buffer.clear();
                            this.codecReconfigurationState = 1;
                        }
                        onInputFormatChanged(this.formatHolder.format);
                        return true;
                    } else if (this.buffer.isEndOfStream()) {
                        if (this.codecReconfigurationState == 2) {
                            this.buffer.clear();
                            this.codecReconfigurationState = 1;
                        }
                        this.inputStreamEnded = true;
                        if (this.codecReceivedBuffers) {
                            try {
                                if (!this.codecNeedsEosPropagation) {
                                    this.codecReceivedEos = true;
                                    this.codec.queueInputBuffer(this.inputIndex, 0, 0, 0, 4);
                                    resetInputBuffer();
                                }
                                return false;
                            } catch (CryptoException e) {
                                throw ExoPlaybackException.createForRenderer(e, getIndex());
                            }
                        }
                        processEndOfStream();
                        return false;
                    } else if (!this.waitingForFirstSyncFrame || this.buffer.isKeyFrame()) {
                        this.waitingForFirstSyncFrame = false;
                        boolean bufferEncrypted = this.buffer.isEncrypted();
                        this.waitingForKeys = shouldWaitForKeys(bufferEncrypted);
                        if (this.waitingForKeys) {
                            return false;
                        }
                        if (this.codecNeedsDiscardToSpsWorkaround && !bufferEncrypted) {
                            NalUnitUtil.discardToSps(this.buffer.data);
                            if (this.buffer.data.position() == 0) {
                                return true;
                            }
                            this.codecNeedsDiscardToSpsWorkaround = false;
                        }
                        try {
                            long presentationTimeUs = this.buffer.timeUs;
                            if (this.buffer.isDecodeOnly()) {
                                this.decodeOnlyPresentationTimestamps.add(Long.valueOf(presentationTimeUs));
                            }
                            if (this.pendingFormat != null) {
                                this.formatQueue.add(presentationTimeUs, this.pendingFormat);
                                this.pendingFormat = null;
                            }
                            this.buffer.flip();
                            onQueueInputBuffer(this.buffer);
                            if (bufferEncrypted) {
                                this.codec.queueSecureInputBuffer(this.inputIndex, 0, getFrameworkCryptoInfo(this.buffer, i), presentationTimeUs, 0);
                            } else {
                                this.codec.queueInputBuffer(this.inputIndex, 0, this.buffer.data.limit(), presentationTimeUs, 0);
                            }
                            resetInputBuffer();
                            this.codecReceivedBuffers = true;
                            this.codecReconfigurationState = 0;
                            DecoderCounters decoderCounters = this.decoderCounters;
                            decoderCounters.inputBufferCount++;
                            return true;
                        } catch (CryptoException e2) {
                            throw ExoPlaybackException.createForRenderer(e2, getIndex());
                        }
                    } else {
                        this.buffer.clear();
                        if (this.codecReconfigurationState == 2) {
                            this.codecReconfigurationState = 1;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean shouldWaitForKeys(boolean bufferEncrypted) throws ExoPlaybackException {
        boolean z = false;
        if (this.drmSession != null) {
            if (bufferEncrypted || !this.playClearSamplesWithoutKeys) {
                int drmSessionState = this.drmSession.getState();
                if (drmSessionState != 1) {
                    if (drmSessionState != 4) {
                        z = true;
                    }
                    return z;
                }
                throw ExoPlaybackException.createForRenderer(this.drmSession.getError(), getIndex());
            }
        }
        return false;
    }

    protected void onCodecInitialized(String name, long initializedTimestampMs, long initializationDurationMs) {
    }

    protected void onInputFormatChanged(Format newFormat) throws ExoPlaybackException {
        Format oldFormat = this.format;
        this.format = newFormat;
        this.pendingFormat = newFormat;
        boolean z = true;
        if (Util.areEqual(this.format.drmInitData, oldFormat == null ? null : oldFormat.drmInitData) ^ true) {
            if (this.format.drmInitData != null) {
                DrmSessionManager drmSessionManager = this.drmSessionManager;
                if (drmSessionManager != null) {
                    this.pendingDrmSession = drmSessionManager.acquireSession(Looper.myLooper(), this.format.drmInitData);
                    DrmSession drmSession = this.pendingDrmSession;
                    if (drmSession == this.drmSession) {
                        this.drmSessionManager.releaseSession(drmSession);
                    }
                } else {
                    throw ExoPlaybackException.createForRenderer(new IllegalStateException("Media requires a DrmSessionManager"), getIndex());
                }
            }
            this.pendingDrmSession = null;
        }
        boolean keepingCodec = false;
        if (this.pendingDrmSession == this.drmSession) {
            MediaCodec mediaCodec = this.codec;
            if (mediaCodec != null) {
                int canKeepCodec = canKeepCodec(mediaCodec, this.codecInfo, oldFormat, this.format);
                if (canKeepCodec != 3) {
                    switch (canKeepCodec) {
                        case 0:
                            break;
                        case 1:
                            keepingCodec = true;
                            break;
                        default:
                            throw new IllegalStateException();
                    }
                } else if (!this.codecNeedsReconfigureWorkaround) {
                    keepingCodec = true;
                    this.codecReconfigured = true;
                    this.codecReconfigurationState = 1;
                    canKeepCodec = this.codecAdaptationWorkaroundMode;
                    if (canKeepCodec != 2) {
                        if (canKeepCodec != 1 || this.format.width != oldFormat.width || this.format.height != oldFormat.height) {
                            z = false;
                        }
                    }
                    this.codecNeedsAdaptationWorkaroundBuffer = z;
                }
                if (keepingCodec) {
                    reinitializeCodec();
                } else {
                    updateCodecOperatingRate();
                }
            }
        }
        if (keepingCodec) {
            updateCodecOperatingRate();
        } else {
            reinitializeCodec();
        }
    }

    protected void onOutputFormatChanged(MediaCodec codec, MediaFormat outputFormat) throws ExoPlaybackException {
    }

    protected void onQueueInputBuffer(DecoderInputBuffer buffer) {
    }

    protected void onProcessedOutputBuffer(long presentationTimeUs) {
    }

    protected int canKeepCodec(MediaCodec codec, MediaCodecInfo codecInfo, Format oldFormat, Format newFormat) {
        return 0;
    }

    public boolean isEnded() {
        return this.outputStreamEnded;
    }

    public boolean isReady() {
        if (this.format != null && !this.waitingForKeys) {
            if (!isSourceReady()) {
                if (!hasOutputBuffer()) {
                    if (this.codecHotswapDeadlineMs != C0555C.TIME_UNSET) {
                        if (SystemClock.elapsedRealtime() < this.codecHotswapDeadlineMs) {
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    protected long getDequeueOutputBufferTimeoutUs() {
        return 0;
    }

    protected float getCodecOperatingRate(float operatingRate, Format format, Format[] streamFormats) {
        return CODEC_OPERATING_RATE_UNSET;
    }

    private void updateCodecOperatingRate() throws ExoPlaybackException {
        if (this.format != null) {
            if (Util.SDK_INT >= 23) {
                float codecOperatingRate = getCodecOperatingRate(this.rendererOperatingRate, this.format, getStreamFormats());
                if (this.codecOperatingRate != codecOperatingRate) {
                    this.codecOperatingRate = codecOperatingRate;
                    if (this.codec != null) {
                        if (this.codecReinitializationState == 0) {
                            if (codecOperatingRate == CODEC_OPERATING_RATE_UNSET && this.codecConfiguredWithOperatingRate) {
                                reinitializeCodec();
                            }
                            if (codecOperatingRate != CODEC_OPERATING_RATE_UNSET && (this.codecConfiguredWithOperatingRate || codecOperatingRate > this.assumedMinimumCodecOperatingRate)) {
                                Bundle codecParameters = new Bundle();
                                codecParameters.putFloat("operating-rate", codecOperatingRate);
                                this.codec.setParameters(codecParameters);
                                this.codecConfiguredWithOperatingRate = true;
                            }
                        }
                    }
                }
            }
        }
    }

    private void reinitializeCodec() throws ExoPlaybackException {
        this.availableCodecInfos = null;
        if (this.codecReceivedBuffers) {
            this.codecReinitializationState = 1;
            return;
        }
        releaseCodec();
        maybeInitCodec();
    }

    private boolean drainOutputBuffer(long positionUs, long elapsedRealtimeUs) throws ExoPlaybackException {
        boolean z;
        MediaCodecRenderer mediaCodecRenderer = this;
        if (!hasOutputBuffer()) {
            int outputIndex;
            if (mediaCodecRenderer.codecNeedsEosOutputExceptionWorkaround && mediaCodecRenderer.codecReceivedEos) {
                try {
                    outputIndex = mediaCodecRenderer.codec.dequeueOutputBuffer(mediaCodecRenderer.outputBufferInfo, getDequeueOutputBufferTimeoutUs());
                } catch (IllegalStateException e) {
                    processEndOfStream();
                    if (mediaCodecRenderer.outputStreamEnded) {
                        releaseCodec();
                    }
                    return false;
                }
            }
            outputIndex = mediaCodecRenderer.codec.dequeueOutputBuffer(mediaCodecRenderer.outputBufferInfo, getDequeueOutputBufferTimeoutUs());
            if (outputIndex < 0) {
                if (outputIndex == -2) {
                    processOutputFormat();
                    return true;
                } else if (outputIndex == -3) {
                    processOutputBuffersChanged();
                    return true;
                } else {
                    if (mediaCodecRenderer.codecNeedsEosPropagation && (mediaCodecRenderer.inputStreamEnded || mediaCodecRenderer.codecReinitializationState == 2)) {
                        processEndOfStream();
                    }
                    return false;
                }
            } else if (mediaCodecRenderer.shouldSkipAdaptationWorkaroundOutputBuffer) {
                mediaCodecRenderer.shouldSkipAdaptationWorkaroundOutputBuffer = false;
                mediaCodecRenderer.codec.releaseOutputBuffer(outputIndex, false);
                return true;
            } else if (mediaCodecRenderer.outputBufferInfo.size != 0 || (mediaCodecRenderer.outputBufferInfo.flags & 4) == 0) {
                mediaCodecRenderer.outputIndex = outputIndex;
                mediaCodecRenderer.outputBuffer = getOutputBuffer(outputIndex);
                ByteBuffer byteBuffer = mediaCodecRenderer.outputBuffer;
                if (byteBuffer != null) {
                    byteBuffer.position(mediaCodecRenderer.outputBufferInfo.offset);
                    mediaCodecRenderer.outputBuffer.limit(mediaCodecRenderer.outputBufferInfo.offset + mediaCodecRenderer.outputBufferInfo.size);
                }
                mediaCodecRenderer.shouldSkipOutputBuffer = shouldSkipOutputBuffer(mediaCodecRenderer.outputBufferInfo.presentationTimeUs);
                updateOutputFormatForTime(mediaCodecRenderer.outputBufferInfo.presentationTimeUs);
            } else {
                processEndOfStream();
                return false;
            }
        }
        if (mediaCodecRenderer.codecNeedsEosOutputExceptionWorkaround && mediaCodecRenderer.codecReceivedEos) {
            try {
                z = false;
                try {
                    boolean processedOutputBuffer = processOutputBuffer(positionUs, elapsedRealtimeUs, mediaCodecRenderer.codec, mediaCodecRenderer.outputBuffer, mediaCodecRenderer.outputIndex, mediaCodecRenderer.outputBufferInfo.flags, mediaCodecRenderer.outputBufferInfo.presentationTimeUs, mediaCodecRenderer.shouldSkipOutputBuffer, mediaCodecRenderer.outputFormat);
                } catch (IllegalStateException e2) {
                    processEndOfStream();
                    if (mediaCodecRenderer.outputStreamEnded) {
                        releaseCodec();
                    }
                    return z;
                }
            } catch (IllegalStateException e3) {
                z = false;
                processEndOfStream();
                if (mediaCodecRenderer.outputStreamEnded) {
                    releaseCodec();
                }
                return z;
            }
        }
        z = false;
        processedOutputBuffer = processOutputBuffer(positionUs, elapsedRealtimeUs, mediaCodecRenderer.codec, mediaCodecRenderer.outputBuffer, mediaCodecRenderer.outputIndex, mediaCodecRenderer.outputBufferInfo.flags, mediaCodecRenderer.outputBufferInfo.presentationTimeUs, mediaCodecRenderer.shouldSkipOutputBuffer, mediaCodecRenderer.outputFormat);
        if (processedOutputBuffer) {
            onProcessedOutputBuffer(mediaCodecRenderer.outputBufferInfo.presentationTimeUs);
            boolean isEndOfStream = (mediaCodecRenderer.outputBufferInfo.flags & 4) != 0;
            resetOutputBuffer();
            if (!isEndOfStream) {
                return true;
            }
            processEndOfStream();
        }
        return z;
    }

    private void processOutputFormat() throws ExoPlaybackException {
        MediaFormat format = this.codec.getOutputFormat();
        if (this.codecAdaptationWorkaroundMode != 0) {
            if (format.getInteger("width") == 32) {
                if (format.getInteger("height") == 32) {
                    this.shouldSkipAdaptationWorkaroundOutputBuffer = true;
                    return;
                }
            }
        }
        if (this.codecNeedsMonoChannelCountWorkaround) {
            format.setInteger("channel-count", 1);
        }
        onOutputFormatChanged(this.codec, format);
    }

    private void processOutputBuffersChanged() {
        if (Util.SDK_INT < 21) {
            this.outputBuffers = this.codec.getOutputBuffers();
        }
    }

    protected void renderToEndOfStream() throws ExoPlaybackException {
    }

    private void processEndOfStream() throws ExoPlaybackException {
        if (this.codecReinitializationState == 2) {
            releaseCodec();
            maybeInitCodec();
            return;
        }
        this.outputStreamEnded = true;
        renderToEndOfStream();
    }

    private boolean shouldSkipOutputBuffer(long presentationTimeUs) {
        int size = this.decodeOnlyPresentationTimestamps.size();
        for (int i = 0; i < size; i++) {
            if (((Long) this.decodeOnlyPresentationTimestamps.get(i)).longValue() == presentationTimeUs) {
                this.decodeOnlyPresentationTimestamps.remove(i);
                return true;
            }
        }
        return false;
    }

    private static CryptoInfo getFrameworkCryptoInfo(DecoderInputBuffer buffer, int adaptiveReconfigurationBytes) {
        CryptoInfo cryptoInfo = buffer.cryptoInfo.getFrameworkCryptoInfoV16();
        if (adaptiveReconfigurationBytes == 0) {
            return cryptoInfo;
        }
        if (cryptoInfo.numBytesOfClearData == null) {
            cryptoInfo.numBytesOfClearData = new int[1];
        }
        int[] iArr = cryptoInfo.numBytesOfClearData;
        iArr[0] = iArr[0] + adaptiveReconfigurationBytes;
        return cryptoInfo;
    }

    private boolean deviceNeedsDrmKeysToConfigureCodecWorkaround() {
        if ("Amazon".equals(Util.MANUFACTURER)) {
            if (!"AFTM".equals(Util.MODEL)) {
                if ("AFTB".equals(Util.MODEL)) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean codecNeedsFlushWorkaround(String name) {
        if (Util.SDK_INT >= 18) {
            if (Util.SDK_INT == 18) {
                if ("OMX.SEC.avc.dec".equals(name) || "OMX.SEC.avc.dec.secure".equals(name)) {
                }
            }
            if (Util.SDK_INT == 19) {
                if (Util.MODEL.startsWith("SM-G800")) {
                    if (!"OMX.Exynos.avc.dec".equals(name)) {
                        if ("OMX.Exynos.avc.dec.secure".equals(name)) {
                        }
                    }
                }
            }
            return false;
        }
        return true;
    }

    private int codecAdaptationWorkaroundMode(String name) {
        if (Util.SDK_INT <= 25 && "OMX.Exynos.avc.dec.secure".equals(name)) {
            if (!Util.MODEL.startsWith("SM-T585") && !Util.MODEL.startsWith("SM-A510")) {
                if (!Util.MODEL.startsWith("SM-A520")) {
                    if (Util.MODEL.startsWith("SM-J700")) {
                    }
                }
            }
            return 2;
        }
        if (Util.SDK_INT < 24) {
            if (!"OMX.Nvidia.h264.decode".equals(name)) {
                if ("OMX.Nvidia.h264.decode.secure".equals(name)) {
                }
            }
            if (!"flounder".equals(Util.DEVICE) && !"flounder_lte".equals(Util.DEVICE)) {
                if (!"grouper".equals(Util.DEVICE)) {
                    if ("tilapia".equals(Util.DEVICE)) {
                    }
                }
            }
            return 1;
        }
        return 0;
    }

    private static boolean codecNeedsReconfigureWorkaround(String name) {
        return Util.MODEL.startsWith("SM-T230") && "OMX.MARVELL.VIDEO.HW.CODA7542DECODER".equals(name);
    }

    private static boolean codecNeedsDiscardToSpsWorkaround(String name, Format format) {
        if (Util.SDK_INT < 21 && format.initializationData.isEmpty()) {
            if ("OMX.MTK.VIDEO.DECODER.AVC".equals(name)) {
                return true;
            }
        }
        return false;
    }

    private static boolean codecNeedsEosPropagationWorkaround(MediaCodecInfo codecInfo) {
        String name = codecInfo.name;
        if (Util.SDK_INT <= 17) {
            if (!"OMX.rk.video_decoder.avc".equals(name)) {
                if ("OMX.allwinner.video.decoder.avc".equals(name)) {
                }
            }
            return true;
        }
        if (!("Amazon".equals(Util.MANUFACTURER) && "AFTS".equals(Util.MODEL) && codecInfo.secure)) {
            return false;
        }
        return true;
    }

    private static boolean codecNeedsEosFlushWorkaround(String name) {
        if (Util.SDK_INT <= 23) {
            if ("OMX.google.vorbis.decoder".equals(name)) {
                return true;
            }
        }
        if (Util.SDK_INT <= 19) {
            if (!"hb2000".equals(Util.DEVICE)) {
                if ("stvm8".equals(Util.DEVICE)) {
                }
            }
            if (!"OMX.amlogic.avc.decoder.awesome".equals(name)) {
                if ("OMX.amlogic.avc.decoder.awesome.secure".equals(name)) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean codecNeedsEosOutputExceptionWorkaround(String name) {
        return Util.SDK_INT == 21 && "OMX.google.aac.decoder".equals(name);
    }

    private static boolean codecNeedsMonoChannelCountWorkaround(String name, Format format) {
        if (Util.SDK_INT <= 18 && format.channelCount == 1) {
            if ("OMX.MTK.AUDIO.DECODER.MP3".equals(name)) {
                return true;
            }
        }
        return false;
    }
}
