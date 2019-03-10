package com.google.android.exoplayer2.text;

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
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.util.Collections;
import java.util.List;

public final class TextRenderer extends BaseRenderer implements Callback {
    private static final int MSG_UPDATE_OUTPUT = 0;
    private static final int REPLACEMENT_STATE_NONE = 0;
    private static final int REPLACEMENT_STATE_SIGNAL_END_OF_STREAM = 1;
    private static final int REPLACEMENT_STATE_WAIT_END_OF_STREAM = 2;
    private SubtitleDecoder decoder;
    private final SubtitleDecoderFactory decoderFactory;
    private int decoderReplacementState;
    private final FormatHolder formatHolder;
    private boolean inputStreamEnded;
    private SubtitleInputBuffer nextInputBuffer;
    private SubtitleOutputBuffer nextSubtitle;
    private int nextSubtitleEventIndex;
    private final TextOutput output;
    @Nullable
    private final Handler outputHandler;
    private boolean outputStreamEnded;
    private Format streamFormat;
    private SubtitleOutputBuffer subtitle;

    @Deprecated
    public interface Output extends TextOutput {
    }

    public void render(long r10, long r12) throws com.google.android.exoplayer2.ExoPlaybackException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:84:0x0117 in {2, 8, 11, 12, 15, 21, 22, 23, 33, 34, 35, 40, 41, 42, 43, 44, 46, 47, 50, 59, 60, 61, 65, 70, 71, 73, 76, 77, 78, 80, 83} preds:[]
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
        r9 = this;
        r0 = r9.outputStreamEnded;
        if (r0 == 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r0 = r9.nextSubtitle;
        if (r0 != 0) goto L_0x0023;
    L_0x0009:
        r0 = r9.decoder;
        r0.setPositionUs(r10);
        r0 = r9.decoder;	 Catch:{ SubtitleDecoderException -> 0x0019 }
        r0 = r0.dequeueOutputBuffer();	 Catch:{ SubtitleDecoderException -> 0x0019 }
        r0 = (com.google.android.exoplayer2.text.SubtitleOutputBuffer) r0;	 Catch:{ SubtitleDecoderException -> 0x0019 }
        r9.nextSubtitle = r0;	 Catch:{ SubtitleDecoderException -> 0x0019 }
        goto L_0x0024;
    L_0x0019:
        r0 = move-exception;
        r1 = r9.getIndex();
        r1 = com.google.android.exoplayer2.ExoPlaybackException.createForRenderer(r0, r1);
        throw r1;
    L_0x0024:
        r0 = r9.getState();
        r1 = 2;
        if (r0 == r1) goto L_0x002c;
    L_0x002b:
        return;
    L_0x002c:
        r0 = 0;
        r2 = r9.subtitle;
        r3 = 1;
        if (r2 == 0) goto L_0x0046;
    L_0x0032:
        r4 = r9.getNextEventTime();
    L_0x0036:
        r2 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1));
        if (r2 > 0) goto L_0x0045;
    L_0x003a:
        r2 = r9.nextSubtitleEventIndex;
        r2 = r2 + r3;
        r9.nextSubtitleEventIndex = r2;
        r4 = r9.getNextEventTime();
        r0 = 1;
        goto L_0x0036;
    L_0x0045:
        goto L_0x0047;
    L_0x0047:
        r2 = r9.nextSubtitle;
        r4 = 0;
        if (r2 == 0) goto L_0x0092;
    L_0x004c:
        r2 = r2.isEndOfStream();
        if (r2 == 0) goto L_0x0070;
    L_0x0052:
        if (r0 != 0) goto L_0x006f;
    L_0x0054:
        r5 = r9.getNextEventTime();
        r7 = 9223372036854775807; // 0x7fffffffffffffff float:NaN double:NaN;
        r2 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r2 != 0) goto L_0x006f;
    L_0x0061:
        r2 = r9.decoderReplacementState;
        if (r2 != r1) goto L_0x0069;
    L_0x0065:
        r9.replaceDecoder();
        goto L_0x0093;
    L_0x0069:
        r9.releaseBuffers();
        r9.outputStreamEnded = r3;
        goto L_0x0093;
    L_0x006f:
        goto L_0x0093;
    L_0x0070:
        r2 = r9.nextSubtitle;
        r5 = r2.timeUs;
        r2 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1));
        if (r2 > 0) goto L_0x0091;
    L_0x0078:
        r2 = r9.subtitle;
        if (r2 == 0) goto L_0x0080;
    L_0x007c:
        r2.release();
        goto L_0x0081;
    L_0x0081:
        r2 = r9.nextSubtitle;
        r9.subtitle = r2;
        r9.nextSubtitle = r4;
        r2 = r9.subtitle;
        r2 = r2.getNextEventTimeIndex(r10);
        r9.nextSubtitleEventIndex = r2;
        r0 = 1;
        goto L_0x0093;
    L_0x0091:
        goto L_0x0093;
    L_0x0093:
        if (r0 == 0) goto L_0x009f;
    L_0x0095:
        r2 = r9.subtitle;
        r2 = r2.getCues(r10);
        r9.updateOutput(r2);
        goto L_0x00a0;
    L_0x00a0:
        r2 = r9.decoderReplacementState;
        if (r2 != r1) goto L_0x00a5;
    L_0x00a4:
        return;
    L_0x00a6:
        r2 = r9.inputStreamEnded;	 Catch:{ SubtitleDecoderException -> 0x010d }
        if (r2 != 0) goto L_0x010b;	 Catch:{ SubtitleDecoderException -> 0x010d }
    L_0x00aa:
        r2 = r9.nextInputBuffer;	 Catch:{ SubtitleDecoderException -> 0x010d }
        if (r2 != 0) goto L_0x00be;	 Catch:{ SubtitleDecoderException -> 0x010d }
    L_0x00ae:
        r2 = r9.decoder;	 Catch:{ SubtitleDecoderException -> 0x010d }
        r2 = r2.dequeueInputBuffer();	 Catch:{ SubtitleDecoderException -> 0x010d }
        r2 = (com.google.android.exoplayer2.text.SubtitleInputBuffer) r2;	 Catch:{ SubtitleDecoderException -> 0x010d }
        r9.nextInputBuffer = r2;	 Catch:{ SubtitleDecoderException -> 0x010d }
        r2 = r9.nextInputBuffer;	 Catch:{ SubtitleDecoderException -> 0x010d }
        if (r2 != 0) goto L_0x00bd;	 Catch:{ SubtitleDecoderException -> 0x010d }
    L_0x00bc:
        return;	 Catch:{ SubtitleDecoderException -> 0x010d }
    L_0x00bd:
        goto L_0x00bf;	 Catch:{ SubtitleDecoderException -> 0x010d }
    L_0x00bf:
        r2 = r9.decoderReplacementState;	 Catch:{ SubtitleDecoderException -> 0x010d }
        if (r2 != r3) goto L_0x00d5;	 Catch:{ SubtitleDecoderException -> 0x010d }
    L_0x00c3:
        r2 = r9.nextInputBuffer;	 Catch:{ SubtitleDecoderException -> 0x010d }
        r3 = 4;	 Catch:{ SubtitleDecoderException -> 0x010d }
        r2.setFlags(r3);	 Catch:{ SubtitleDecoderException -> 0x010d }
        r2 = r9.decoder;	 Catch:{ SubtitleDecoderException -> 0x010d }
        r3 = r9.nextInputBuffer;	 Catch:{ SubtitleDecoderException -> 0x010d }
        r2.queueInputBuffer(r3);	 Catch:{ SubtitleDecoderException -> 0x010d }
        r9.nextInputBuffer = r4;	 Catch:{ SubtitleDecoderException -> 0x010d }
        r9.decoderReplacementState = r1;	 Catch:{ SubtitleDecoderException -> 0x010d }
        return;	 Catch:{ SubtitleDecoderException -> 0x010d }
    L_0x00d5:
        r2 = r9.formatHolder;	 Catch:{ SubtitleDecoderException -> 0x010d }
        r5 = r9.nextInputBuffer;	 Catch:{ SubtitleDecoderException -> 0x010d }
        r6 = 0;	 Catch:{ SubtitleDecoderException -> 0x010d }
        r2 = r9.readSource(r2, r5, r6);	 Catch:{ SubtitleDecoderException -> 0x010d }
        r5 = -4;	 Catch:{ SubtitleDecoderException -> 0x010d }
        if (r2 != r5) goto L_0x0105;	 Catch:{ SubtitleDecoderException -> 0x010d }
    L_0x00e1:
        r5 = r9.nextInputBuffer;	 Catch:{ SubtitleDecoderException -> 0x010d }
        r5 = r5.isEndOfStream();	 Catch:{ SubtitleDecoderException -> 0x010d }
        if (r5 == 0) goto L_0x00ec;	 Catch:{ SubtitleDecoderException -> 0x010d }
    L_0x00e9:
        r9.inputStreamEnded = r3;	 Catch:{ SubtitleDecoderException -> 0x010d }
        goto L_0x00fb;	 Catch:{ SubtitleDecoderException -> 0x010d }
    L_0x00ec:
        r5 = r9.nextInputBuffer;	 Catch:{ SubtitleDecoderException -> 0x010d }
        r6 = r9.formatHolder;	 Catch:{ SubtitleDecoderException -> 0x010d }
        r6 = r6.format;	 Catch:{ SubtitleDecoderException -> 0x010d }
        r6 = r6.subsampleOffsetUs;	 Catch:{ SubtitleDecoderException -> 0x010d }
        r5.subsampleOffsetUs = r6;	 Catch:{ SubtitleDecoderException -> 0x010d }
        r5 = r9.nextInputBuffer;	 Catch:{ SubtitleDecoderException -> 0x010d }
        r5.flip();	 Catch:{ SubtitleDecoderException -> 0x010d }
    L_0x00fb:
        r5 = r9.decoder;	 Catch:{ SubtitleDecoderException -> 0x010d }
        r6 = r9.nextInputBuffer;	 Catch:{ SubtitleDecoderException -> 0x010d }
        r5.queueInputBuffer(r6);	 Catch:{ SubtitleDecoderException -> 0x010d }
        r9.nextInputBuffer = r4;	 Catch:{ SubtitleDecoderException -> 0x010d }
        goto L_0x010a;
    L_0x0105:
        r5 = -3;
        if (r2 != r5) goto L_0x0109;
    L_0x0108:
        return;
    L_0x010a:
        goto L_0x00a6;
        return;
    L_0x010d:
        r1 = move-exception;
        r2 = r9.getIndex();
        r2 = com.google.android.exoplayer2.ExoPlaybackException.createForRenderer(r1, r2);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.TextRenderer.render(long, long):void");
    }

    public TextRenderer(TextOutput output, @Nullable Looper outputLooper) {
        this(output, outputLooper, SubtitleDecoderFactory.DEFAULT);
    }

    public TextRenderer(TextOutput output, @Nullable Looper outputLooper, SubtitleDecoderFactory decoderFactory) {
        Handler handler;
        super(3);
        this.output = (TextOutput) Assertions.checkNotNull(output);
        if (outputLooper == null) {
            handler = null;
        } else {
            handler = Util.createHandler(outputLooper, this);
        }
        this.outputHandler = handler;
        this.decoderFactory = decoderFactory;
        this.formatHolder = new FormatHolder();
    }

    public int supportsFormat(Format format) {
        if (this.decoderFactory.supportsFormat(format)) {
            return BaseRenderer.supportsFormatDrm(null, format.drmInitData) ? 4 : 2;
        } else if (MimeTypes.isText(format.sampleMimeType)) {
            return 1;
        } else {
            return 0;
        }
    }

    protected void onStreamChanged(Format[] formats, long offsetUs) throws ExoPlaybackException {
        this.streamFormat = formats[0];
        if (this.decoder != null) {
            this.decoderReplacementState = 1;
        } else {
            this.decoder = this.decoderFactory.createDecoder(this.streamFormat);
        }
    }

    protected void onPositionReset(long positionUs, boolean joining) {
        clearOutput();
        this.inputStreamEnded = false;
        this.outputStreamEnded = false;
        if (this.decoderReplacementState != 0) {
            replaceDecoder();
            return;
        }
        releaseBuffers();
        this.decoder.flush();
    }

    protected void onDisabled() {
        this.streamFormat = null;
        clearOutput();
        releaseDecoder();
    }

    public boolean isEnded() {
        return this.outputStreamEnded;
    }

    public boolean isReady() {
        return true;
    }

    private void releaseBuffers() {
        this.nextInputBuffer = null;
        this.nextSubtitleEventIndex = -1;
        SubtitleOutputBuffer subtitleOutputBuffer = this.subtitle;
        if (subtitleOutputBuffer != null) {
            subtitleOutputBuffer.release();
            this.subtitle = null;
        }
        subtitleOutputBuffer = this.nextSubtitle;
        if (subtitleOutputBuffer != null) {
            subtitleOutputBuffer.release();
            this.nextSubtitle = null;
        }
    }

    private void releaseDecoder() {
        releaseBuffers();
        this.decoder.release();
        this.decoder = null;
        this.decoderReplacementState = 0;
    }

    private void replaceDecoder() {
        releaseDecoder();
        this.decoder = this.decoderFactory.createDecoder(this.streamFormat);
    }

    private long getNextEventTime() {
        int i = this.nextSubtitleEventIndex;
        if (i != -1) {
            if (i < this.subtitle.getEventTimeCount()) {
                return this.subtitle.getEventTime(this.nextSubtitleEventIndex);
            }
        }
        return Long.MAX_VALUE;
    }

    private void updateOutput(List<Cue> cues) {
        Handler handler = this.outputHandler;
        if (handler != null) {
            handler.obtainMessage(0, cues).sendToTarget();
        } else {
            invokeUpdateOutputInternal(cues);
        }
    }

    private void clearOutput() {
        updateOutput(Collections.emptyList());
    }

    public boolean handleMessage(Message msg) {
        if (msg.what == 0) {
            invokeUpdateOutputInternal((List) msg.obj);
            return true;
        }
        throw new IllegalStateException();
    }

    private void invokeUpdateOutputInternal(List<Cue> cues) {
        this.output.onCues(cues);
    }
}
