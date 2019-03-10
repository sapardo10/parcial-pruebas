package com.google.android.exoplayer2.metadata;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;

public final class MetadataRenderer extends BaseRenderer implements Callback {
    private static final int MAX_PENDING_METADATA_COUNT = 5;
    private static final int MSG_INVOKE_RENDERER = 0;
    private final MetadataInputBuffer buffer;
    private MetadataDecoder decoder;
    private final MetadataDecoderFactory decoderFactory;
    private final FormatHolder formatHolder;
    private boolean inputStreamEnded;
    private final MetadataOutput output;
    @Nullable
    private final Handler outputHandler;
    private final Metadata[] pendingMetadata;
    private int pendingMetadataCount;
    private int pendingMetadataIndex;
    private final long[] pendingMetadataTimestamps;

    @Deprecated
    public interface Output extends MetadataOutput {
    }

    public MetadataRenderer(MetadataOutput output, @Nullable Looper outputLooper) {
        this(output, outputLooper, MetadataDecoderFactory.DEFAULT);
    }

    public MetadataRenderer(MetadataOutput output, @Nullable Looper outputLooper, MetadataDecoderFactory decoderFactory) {
        Handler handler;
        super(4);
        this.output = (MetadataOutput) Assertions.checkNotNull(output);
        if (outputLooper == null) {
            handler = null;
        } else {
            handler = Util.createHandler(outputLooper, this);
        }
        this.outputHandler = handler;
        this.decoderFactory = (MetadataDecoderFactory) Assertions.checkNotNull(decoderFactory);
        this.formatHolder = new FormatHolder();
        this.buffer = new MetadataInputBuffer();
        this.pendingMetadata = new Metadata[5];
        this.pendingMetadataTimestamps = new long[5];
    }

    public int supportsFormat(Format format) {
        if (!this.decoderFactory.supportsFormat(format)) {
            return 0;
        }
        return BaseRenderer.supportsFormatDrm(null, format.drmInitData) ? 4 : 2;
    }

    protected void onStreamChanged(Format[] formats, long offsetUs) throws ExoPlaybackException {
        this.decoder = this.decoderFactory.createDecoder(formats[0]);
    }

    protected void onPositionReset(long positionUs, boolean joining) {
        flushPendingMetadata();
        this.inputStreamEnded = false;
    }

    public void render(long positionUs, long elapsedRealtimeUs) throws ExoPlaybackException {
        int index;
        if (!this.inputStreamEnded && this.pendingMetadataCount < 5) {
            this.buffer.clear();
            if (readSource(this.formatHolder, this.buffer, false) == -4) {
                if (this.buffer.isEndOfStream()) {
                    this.inputStreamEnded = true;
                } else if (!this.buffer.isDecodeOnly()) {
                    this.buffer.subsampleOffsetUs = this.formatHolder.format.subsampleOffsetUs;
                    this.buffer.flip();
                    index = (this.pendingMetadataIndex + this.pendingMetadataCount) % 5;
                    Metadata metadata = this.decoder.decode(this.buffer);
                    if (metadata != null) {
                        this.pendingMetadata[index] = metadata;
                        this.pendingMetadataTimestamps[index] = this.buffer.timeUs;
                        this.pendingMetadataCount++;
                    }
                }
            }
        }
        if (this.pendingMetadataCount > 0) {
            long[] jArr = this.pendingMetadataTimestamps;
            index = this.pendingMetadataIndex;
            if (jArr[index] <= positionUs) {
                invokeRenderer(this.pendingMetadata[index]);
                Metadata[] metadataArr = this.pendingMetadata;
                index = this.pendingMetadataIndex;
                metadataArr[index] = null;
                this.pendingMetadataIndex = (index + 1) % 5;
                this.pendingMetadataCount--;
            }
        }
    }

    protected void onDisabled() {
        flushPendingMetadata();
        this.decoder = null;
    }

    public boolean isEnded() {
        return this.inputStreamEnded;
    }

    public boolean isReady() {
        return true;
    }

    private void invokeRenderer(Metadata metadata) {
        Handler handler = this.outputHandler;
        if (handler != null) {
            handler.obtainMessage(0, metadata).sendToTarget();
        } else {
            invokeRendererInternal(metadata);
        }
    }

    private void flushPendingMetadata() {
        Arrays.fill(this.pendingMetadata, null);
        this.pendingMetadataIndex = 0;
        this.pendingMetadataCount = 0;
    }

    public boolean handleMessage(Message msg) {
        if (msg.what == 0) {
            invokeRendererInternal((Metadata) msg.obj);
            return true;
        }
        throw new IllegalStateException();
    }

    private void invokeRendererInternal(Metadata metadata) {
        this.output.onMetadata(metadata);
    }
}
