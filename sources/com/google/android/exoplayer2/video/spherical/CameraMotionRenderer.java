package com.google.android.exoplayer2.video.spherical;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;

public class CameraMotionRenderer extends BaseRenderer {
    private static final int SAMPLE_WINDOW_DURATION_US = 100000;
    private final DecoderInputBuffer buffer = new DecoderInputBuffer(1);
    private final FormatHolder formatHolder = new FormatHolder();
    private long lastTimestampUs;
    @Nullable
    private CameraMotionListener listener;
    private long offsetUs;
    private final ParsableByteArray scratch = new ParsableByteArray();

    public CameraMotionRenderer() {
        super(5);
    }

    public int supportsFormat(Format format) {
        return MimeTypes.APPLICATION_CAMERA_MOTION.equals(format.sampleMimeType) ? 4 : 0;
    }

    public void handleMessage(int messageType, @Nullable Object message) throws ExoPlaybackException {
        if (messageType == 7) {
            this.listener = (CameraMotionListener) message;
        } else {
            super.handleMessage(messageType, message);
        }
    }

    protected void onStreamChanged(Format[] formats, long offsetUs) throws ExoPlaybackException {
        this.offsetUs = offsetUs;
    }

    protected void onPositionReset(long positionUs, boolean joining) throws ExoPlaybackException {
        reset();
    }

    protected void onDisabled() {
        reset();
    }

    public void render(long positionUs, long elapsedRealtimeUs) throws ExoPlaybackException {
        while (!hasReadStreamToEnd() && this.lastTimestampUs < 100000 + positionUs) {
            this.buffer.clear();
            if (readSource(this.formatHolder, this.buffer, false) == -4) {
                if (!this.buffer.isEndOfStream()) {
                    this.buffer.flip();
                    this.lastTimestampUs = this.buffer.timeUs;
                    if (this.listener != null) {
                        float[] rotation = parseMetadata(this.buffer.data);
                        if (rotation != null) {
                            ((CameraMotionListener) Util.castNonNull(this.listener)).onCameraMotion(this.lastTimestampUs - this.offsetUs, rotation);
                        }
                    }
                }
            }
            return;
        }
    }

    public boolean isEnded() {
        return hasReadStreamToEnd();
    }

    public boolean isReady() {
        return true;
    }

    @Nullable
    private float[] parseMetadata(ByteBuffer data) {
        if (data.remaining() != 16) {
            return null;
        }
        this.scratch.reset(data.array(), data.limit());
        this.scratch.setPosition(data.arrayOffset() + 4);
        float[] result = new float[3];
        for (int i = 0; i < 3; i++) {
            result[i] = Float.intBitsToFloat(this.scratch.readLittleEndianInt());
        }
        return result;
    }

    private void reset() {
        this.lastTimestampUs = 0;
        CameraMotionListener cameraMotionListener = this.listener;
        if (cameraMotionListener != null) {
            cameraMotionListener.onCameraMotionReset();
        }
    }
}
