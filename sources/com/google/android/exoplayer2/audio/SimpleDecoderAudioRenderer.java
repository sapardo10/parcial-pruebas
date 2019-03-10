package com.google.android.exoplayer2.audio;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.audio.AudioSink.ConfigurationException;
import com.google.android.exoplayer2.audio.AudioSink.InitializationException;
import com.google.android.exoplayer2.audio.AudioSink.Listener;
import com.google.android.exoplayer2.audio.AudioSink.WriteException;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.decoder.SimpleDecoder;
import com.google.android.exoplayer2.decoder.SimpleOutputBuffer;
import com.google.android.exoplayer2.drm.DrmSession;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MediaClock;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.TraceUtil;
import com.google.android.exoplayer2.util.Util;

public abstract class SimpleDecoderAudioRenderer extends BaseRenderer implements MediaClock {
    private static final int REINITIALIZATION_STATE_NONE = 0;
    private static final int REINITIALIZATION_STATE_SIGNAL_END_OF_STREAM = 1;
    private static final int REINITIALIZATION_STATE_WAIT_END_OF_STREAM = 2;
    private boolean allowFirstBufferPositionDiscontinuity;
    private boolean allowPositionDiscontinuity;
    private final AudioSink audioSink;
    private boolean audioTrackNeedsConfigure;
    private long currentPositionUs;
    private SimpleDecoder<DecoderInputBuffer, ? extends SimpleOutputBuffer, ? extends AudioDecoderException> decoder;
    private DecoderCounters decoderCounters;
    private boolean decoderReceivedBuffers;
    private int decoderReinitializationState;
    private DrmSession<ExoMediaCrypto> drmSession;
    private final DrmSessionManager<ExoMediaCrypto> drmSessionManager;
    private int encoderDelay;
    private int encoderPadding;
    private final AudioRendererEventListener$EventDispatcher eventDispatcher;
    private final DecoderInputBuffer flagsOnlyBuffer;
    private final FormatHolder formatHolder;
    private DecoderInputBuffer inputBuffer;
    private Format inputFormat;
    private boolean inputStreamEnded;
    private SimpleOutputBuffer outputBuffer;
    private boolean outputStreamEnded;
    private DrmSession<ExoMediaCrypto> pendingDrmSession;
    private final boolean playClearSamplesWithoutKeys;
    private boolean waitingForKeys;

    private final class AudioSinkListener implements Listener {
        private AudioSinkListener() {
        }

        public void onAudioSessionId(int audioSessionId) {
            SimpleDecoderAudioRenderer.this.eventDispatcher.audioSessionId(audioSessionId);
            SimpleDecoderAudioRenderer.this.onAudioSessionId(audioSessionId);
        }

        public void onPositionDiscontinuity() {
            SimpleDecoderAudioRenderer.this.onAudioTrackPositionDiscontinuity();
            SimpleDecoderAudioRenderer.this.allowPositionDiscontinuity = true;
        }

        public void onUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
            SimpleDecoderAudioRenderer.this.eventDispatcher.audioTrackUnderrun(bufferSize, bufferSizeMs, elapsedSinceLastFeedMs);
            SimpleDecoderAudioRenderer.this.onAudioTrackUnderrun(bufferSize, bufferSizeMs, elapsedSinceLastFeedMs);
        }
    }

    protected abstract SimpleDecoder<DecoderInputBuffer, ? extends SimpleOutputBuffer, ? extends AudioDecoderException> createDecoder(Format format, ExoMediaCrypto exoMediaCrypto) throws AudioDecoderException;

    protected abstract int supportsFormatInternal(DrmSessionManager<ExoMediaCrypto> drmSessionManager, Format format);

    public SimpleDecoderAudioRenderer() {
        this(null, null, new AudioProcessor[0]);
    }

    public SimpleDecoderAudioRenderer(@Nullable Handler eventHandler, @Nullable AudioRendererEventListener eventListener, AudioProcessor... audioProcessors) {
        this(eventHandler, eventListener, null, null, false, audioProcessors);
    }

    public SimpleDecoderAudioRenderer(@Nullable Handler eventHandler, @Nullable AudioRendererEventListener eventListener, @Nullable AudioCapabilities audioCapabilities) {
        this(eventHandler, eventListener, audioCapabilities, null, false, new AudioProcessor[0]);
    }

    public SimpleDecoderAudioRenderer(@Nullable Handler eventHandler, @Nullable AudioRendererEventListener eventListener, @Nullable AudioCapabilities audioCapabilities, @Nullable DrmSessionManager<ExoMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, AudioProcessor... audioProcessors) {
        this(eventHandler, eventListener, drmSessionManager, playClearSamplesWithoutKeys, new DefaultAudioSink(audioCapabilities, audioProcessors));
    }

    public SimpleDecoderAudioRenderer(@Nullable Handler eventHandler, @Nullable AudioRendererEventListener eventListener, @Nullable DrmSessionManager<ExoMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, AudioSink audioSink) {
        super(1);
        this.drmSessionManager = drmSessionManager;
        this.playClearSamplesWithoutKeys = playClearSamplesWithoutKeys;
        this.eventDispatcher = new AudioRendererEventListener$EventDispatcher(eventHandler, eventListener);
        this.audioSink = audioSink;
        audioSink.setListener(new AudioSinkListener());
        this.formatHolder = new FormatHolder();
        this.flagsOnlyBuffer = DecoderInputBuffer.newFlagsOnlyInstance();
        this.decoderReinitializationState = 0;
        this.audioTrackNeedsConfigure = true;
    }

    public MediaClock getMediaClock() {
        return this;
    }

    public final int supportsFormat(Format format) {
        int tunnelingSupport = 0;
        if (!MimeTypes.isAudio(format.sampleMimeType)) {
            return 0;
        }
        int formatSupport = supportsFormatInternal(this.drmSessionManager, format);
        if (formatSupport <= 2) {
            return formatSupport;
        }
        if (Util.SDK_INT >= 21) {
            tunnelingSupport = 32;
        }
        return (tunnelingSupport | 8) | formatSupport;
    }

    protected final boolean supportsOutput(int channelCount, int encoding) {
        return this.audioSink.supportsOutput(channelCount, encoding);
    }

    public void render(long positionUs, long elapsedRealtimeUs) throws ExoPlaybackException {
        if (this.outputStreamEnded) {
            try {
                this.audioSink.playToEndOfStream();
                return;
            } catch (WriteException e) {
                throw ExoPlaybackException.createForRenderer(e, getIndex());
            }
        }
        if (this.inputFormat == null) {
            this.flagsOnlyBuffer.clear();
            int result = readSource(this.formatHolder, this.flagsOnlyBuffer, true);
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
        maybeInitDecoder();
        if (this.decoder != null) {
            try {
                TraceUtil.beginSection("drainAndFeed");
                while (drainOutputBuffer()) {
                }
                while (feedInputBuffer()) {
                }
                TraceUtil.endSection();
                this.decoderCounters.ensureUpdated();
            } catch (Exception e2) {
                throw ExoPlaybackException.createForRenderer(e2, getIndex());
            }
        }
    }

    protected void onAudioSessionId(int audioSessionId) {
    }

    protected void onAudioTrackPositionDiscontinuity() {
    }

    protected void onAudioTrackUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
    }

    protected Format getOutputFormat() {
        return Format.createAudioSampleFormat(null, MimeTypes.AUDIO_RAW, null, -1, -1, this.inputFormat.channelCount, this.inputFormat.sampleRate, 2, null, null, 0, null);
    }

    private boolean drainOutputBuffer() throws ExoPlaybackException, AudioDecoderException, ConfigurationException, InitializationException, WriteException {
        if (this.outputBuffer == null) {
            this.outputBuffer = (SimpleOutputBuffer) this.decoder.dequeueOutputBuffer();
            if (this.outputBuffer == null) {
                return false;
            }
            DecoderCounters decoderCounters = this.decoderCounters;
            decoderCounters.skippedOutputBufferCount += this.outputBuffer.skippedOutputBufferCount;
        }
        if (this.outputBuffer.isEndOfStream()) {
            if (this.decoderReinitializationState == 2) {
                releaseDecoder();
                maybeInitDecoder();
                this.audioTrackNeedsConfigure = true;
            } else {
                this.outputBuffer.release();
                this.outputBuffer = null;
                processEndOfStream();
            }
            return false;
        }
        if (this.audioTrackNeedsConfigure) {
            Format outputFormat = getOutputFormat();
            this.audioSink.configure(outputFormat.pcmEncoding, outputFormat.channelCount, outputFormat.sampleRate, 0, null, this.encoderDelay, this.encoderPadding);
            this.audioTrackNeedsConfigure = false;
        }
        if (!this.audioSink.handleBuffer(this.outputBuffer.data, this.outputBuffer.timeUs)) {
            return false;
        }
        decoderCounters = this.decoderCounters;
        decoderCounters.renderedOutputBufferCount++;
        this.outputBuffer.release();
        this.outputBuffer = null;
        return true;
    }

    private boolean feedInputBuffer() throws AudioDecoderException, ExoPlaybackException {
        SimpleDecoder simpleDecoder = this.decoder;
        if (!(simpleDecoder == null || this.decoderReinitializationState == 2)) {
            if (!this.inputStreamEnded) {
                if (this.inputBuffer == null) {
                    this.inputBuffer = simpleDecoder.dequeueInputBuffer();
                    if (this.inputBuffer == null) {
                        return false;
                    }
                }
                if (this.decoderReinitializationState == 1) {
                    this.inputBuffer.setFlags(4);
                    this.decoder.queueInputBuffer(this.inputBuffer);
                    this.inputBuffer = null;
                    this.decoderReinitializationState = 2;
                    return false;
                }
                int result;
                if (this.waitingForKeys) {
                    result = -4;
                } else {
                    result = readSource(this.formatHolder, this.inputBuffer, false);
                }
                if (result == -3) {
                    return false;
                }
                if (result == -5) {
                    onInputFormatChanged(this.formatHolder.format);
                    return true;
                } else if (this.inputBuffer.isEndOfStream()) {
                    this.inputStreamEnded = true;
                    this.decoder.queueInputBuffer(this.inputBuffer);
                    this.inputBuffer = null;
                    return false;
                } else {
                    this.waitingForKeys = shouldWaitForKeys(this.inputBuffer.isEncrypted());
                    if (this.waitingForKeys) {
                        return false;
                    }
                    this.inputBuffer.flip();
                    onQueueInputBuffer(this.inputBuffer);
                    this.decoder.queueInputBuffer(this.inputBuffer);
                    this.decoderReceivedBuffers = true;
                    DecoderCounters decoderCounters = this.decoderCounters;
                    decoderCounters.inputBufferCount++;
                    this.inputBuffer = null;
                    return true;
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

    private void processEndOfStream() throws ExoPlaybackException {
        this.outputStreamEnded = true;
        try {
            this.audioSink.playToEndOfStream();
        } catch (WriteException e) {
            throw ExoPlaybackException.createForRenderer(e, getIndex());
        }
    }

    private void flushDecoder() throws ExoPlaybackException {
        this.waitingForKeys = false;
        if (this.decoderReinitializationState != 0) {
            releaseDecoder();
            maybeInitDecoder();
            return;
        }
        this.inputBuffer = null;
        SimpleOutputBuffer simpleOutputBuffer = this.outputBuffer;
        if (simpleOutputBuffer != null) {
            simpleOutputBuffer.release();
            this.outputBuffer = null;
        }
        this.decoder.flush();
        this.decoderReceivedBuffers = false;
    }

    public boolean isEnded() {
        return this.outputStreamEnded && this.audioSink.isEnded();
    }

    public boolean isReady() {
        if (!this.audioSink.hasPendingData()) {
            if (this.inputFormat != null && !this.waitingForKeys) {
                if (!isSourceReady()) {
                    if (this.outputBuffer != null) {
                    }
                }
            }
            return false;
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

    protected void onEnabled(boolean joining) throws ExoPlaybackException {
        this.decoderCounters = new DecoderCounters();
        this.eventDispatcher.enabled(this.decoderCounters);
        int tunnelingAudioSessionId = getConfiguration().tunnelingAudioSessionId;
        if (tunnelingAudioSessionId != 0) {
            this.audioSink.enableTunnelingV21(tunnelingAudioSessionId);
        } else {
            this.audioSink.disableTunneling();
        }
    }

    protected void onPositionReset(long positionUs, boolean joining) throws ExoPlaybackException {
        this.audioSink.reset();
        this.currentPositionUs = positionUs;
        this.allowFirstBufferPositionDiscontinuity = true;
        this.allowPositionDiscontinuity = true;
        this.inputStreamEnded = false;
        this.outputStreamEnded = false;
        if (this.decoder != null) {
            flushDecoder();
        }
    }

    protected void onStarted() {
        this.audioSink.play();
    }

    protected void onStopped() {
        updateCurrentPosition();
        this.audioSink.pause();
    }

    protected void onDisabled() {
        this.inputFormat = null;
        this.audioTrackNeedsConfigure = true;
        this.waitingForKeys = false;
        try {
            releaseDecoder();
            this.audioSink.release();
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
                    this.decoderCounters.ensureUpdated();
                    this.eventDispatcher.disabled(this.decoderCounters);
                } catch (Throwable th) {
                    this.drmSession = null;
                    this.pendingDrmSession = null;
                    this.decoderCounters.ensureUpdated();
                    this.eventDispatcher.disabled(this.decoderCounters);
                }
            } catch (Throwable th2) {
                this.drmSession = null;
                this.pendingDrmSession = null;
                this.decoderCounters.ensureUpdated();
                this.eventDispatcher.disabled(this.decoderCounters);
            }
        } catch (Throwable th3) {
            this.drmSession = null;
            this.pendingDrmSession = null;
            this.decoderCounters.ensureUpdated();
            this.eventDispatcher.disabled(this.decoderCounters);
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

    private void maybeInitDecoder() throws ExoPlaybackException {
        if (this.decoder == null) {
            this.drmSession = this.pendingDrmSession;
            ExoMediaCrypto mediaCrypto = null;
            DrmSession drmSession = this.drmSession;
            if (drmSession != null) {
                mediaCrypto = drmSession.getMediaCrypto();
                if (mediaCrypto == null) {
                    if (this.drmSession.getError() == null) {
                        return;
                    }
                }
            }
            try {
                long codecInitializingTimestamp = SystemClock.elapsedRealtime();
                TraceUtil.beginSection("createAudioDecoder");
                this.decoder = createDecoder(this.inputFormat, mediaCrypto);
                TraceUtil.endSection();
                long codecInitializedTimestamp = SystemClock.elapsedRealtime();
                this.eventDispatcher.decoderInitialized(this.decoder.getName(), codecInitializedTimestamp, codecInitializedTimestamp - codecInitializingTimestamp);
                DecoderCounters decoderCounters = this.decoderCounters;
                decoderCounters.decoderInitCount++;
            } catch (AudioDecoderException e) {
                throw ExoPlaybackException.createForRenderer(e, getIndex());
            }
        }
    }

    private void releaseDecoder() {
        SimpleDecoder simpleDecoder = this.decoder;
        if (simpleDecoder != null) {
            this.inputBuffer = null;
            this.outputBuffer = null;
            simpleDecoder.release();
            this.decoder = null;
            DecoderCounters decoderCounters = this.decoderCounters;
            decoderCounters.decoderReleaseCount++;
            this.decoderReinitializationState = 0;
            this.decoderReceivedBuffers = false;
        }
    }

    private void onInputFormatChanged(Format newFormat) throws ExoPlaybackException {
        Format oldFormat = this.inputFormat;
        this.inputFormat = newFormat;
        if (Util.areEqual(this.inputFormat.drmInitData, oldFormat == null ? null : oldFormat.drmInitData) ^ true) {
            if (this.inputFormat.drmInitData != null) {
                DrmSessionManager drmSessionManager = this.drmSessionManager;
                if (drmSessionManager != null) {
                    this.pendingDrmSession = drmSessionManager.acquireSession(Looper.myLooper(), this.inputFormat.drmInitData);
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
        if (this.decoderReceivedBuffers) {
            this.decoderReinitializationState = 1;
        } else {
            releaseDecoder();
            maybeInitDecoder();
            this.audioTrackNeedsConfigure = true;
        }
        this.encoderDelay = newFormat.encoderDelay;
        this.encoderPadding = newFormat.encoderPadding;
        this.eventDispatcher.inputFormatChanged(newFormat);
    }

    private void onQueueInputBuffer(DecoderInputBuffer buffer) {
        if (this.allowFirstBufferPositionDiscontinuity && !buffer.isDecodeOnly()) {
            if (Math.abs(buffer.timeUs - this.currentPositionUs) > 500000) {
                this.currentPositionUs = buffer.timeUs;
            }
            this.allowFirstBufferPositionDiscontinuity = false;
        }
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
}
