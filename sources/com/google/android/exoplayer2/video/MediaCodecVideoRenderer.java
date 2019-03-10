package com.google.android.exoplayer2.video;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.media.MediaCodec;
import android.media.MediaCodec.OnFrameRenderedListener;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Surface;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.mediacodec.MediaCodecInfo;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException;
import com.google.android.exoplayer2.mediacodec.MediaFormatUtil;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.TraceUtil;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.util.List;

@TargetApi(16)
public class MediaCodecVideoRenderer extends MediaCodecRenderer {
    private static final float INITIAL_FORMAT_MAX_INPUT_SIZE_SCALE_FACTOR = 1.5f;
    private static final String KEY_CROP_BOTTOM = "crop-bottom";
    private static final String KEY_CROP_LEFT = "crop-left";
    private static final String KEY_CROP_RIGHT = "crop-right";
    private static final String KEY_CROP_TOP = "crop-top";
    private static final int MAX_PENDING_OUTPUT_STREAM_OFFSET_COUNT = 10;
    private static final int[] STANDARD_LONG_EDGE_VIDEO_PX = new int[]{1920, 1600, 1440, 1280, 960, 854, 640, 540, 480};
    private static final String TAG = "MediaCodecVideoRenderer";
    private static boolean deviceNeedsSetOutputSurfaceWorkaround;
    private static boolean evaluatedDeviceNeedsSetOutputSurfaceWorkaround;
    private final long allowedJoiningTimeMs;
    private int buffersInCodecCount;
    private CodecMaxValues codecMaxValues;
    private boolean codecNeedsSetOutputSurfaceWorkaround;
    private int consecutiveDroppedFrameCount;
    private final Context context;
    private int currentHeight;
    private float currentPixelWidthHeightRatio;
    private int currentUnappliedRotationDegrees;
    private int currentWidth;
    private final boolean deviceNeedsNoPostProcessWorkaround;
    private long droppedFrameAccumulationStartTimeMs;
    private int droppedFrames;
    private Surface dummySurface;
    private final VideoRendererEventListener$EventDispatcher eventDispatcher;
    @Nullable
    private VideoFrameMetadataListener frameMetadataListener;
    private final VideoFrameReleaseTimeHelper frameReleaseTimeHelper;
    private long initialPositionUs;
    private long joiningDeadlineMs;
    private long lastInputTimeUs;
    private long lastRenderTimeUs;
    private final int maxDroppedFramesToNotify;
    private long outputStreamOffsetUs;
    private int pendingOutputStreamOffsetCount;
    private final long[] pendingOutputStreamOffsetsUs;
    private final long[] pendingOutputStreamSwitchTimesUs;
    private float pendingPixelWidthHeightRatio;
    private int pendingRotationDegrees;
    private boolean renderedFirstFrame;
    private int reportedHeight;
    private float reportedPixelWidthHeightRatio;
    private int reportedUnappliedRotationDegrees;
    private int reportedWidth;
    private int scalingMode;
    private Surface surface;
    private boolean tunneling;
    private int tunnelingAudioSessionId;
    OnFrameRenderedListenerV23 tunnelingOnFrameRenderedListener;

    protected static final class CodecMaxValues {
        public final int height;
        public final int inputSize;
        public final int width;

        public CodecMaxValues(int width, int height, int inputSize) {
            this.width = width;
            this.height = height;
            this.inputSize = inputSize;
        }
    }

    @TargetApi(23)
    private final class OnFrameRenderedListenerV23 implements OnFrameRenderedListener {
        private OnFrameRenderedListenerV23(MediaCodec codec) {
            codec.setOnFrameRenderedListener(this, new Handler());
        }

        public void onFrameRendered(@NonNull MediaCodec codec, long presentationTimeUs, long nanoTime) {
            if (this == MediaCodecVideoRenderer.this.tunnelingOnFrameRenderedListener) {
                MediaCodecVideoRenderer.this.onProcessedTunneledBuffer(presentationTimeUs);
            }
        }
    }

    protected boolean codecNeedsSetOutputSurfaceWorkaround(java.lang.String r8) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:416:0x0634 in {2, 12, 15, 21, 24, 27, 30, 33, 36, 39, 42, 45, 48, 51, 54, 57, 60, 63, 66, 69, 72, 75, 78, 81, 84, 87, 90, 93, 96, 99, 102, 105, 108, 111, 114, 117, 120, 123, 126, 129, 132, 135, 138, 141, 144, 147, 150, 153, 156, 159, 162, 165, 168, 171, 174, 177, 180, 183, 186, 189, 192, 195, 198, 201, 204, 207, 210, 213, 216, 219, 222, 225, 228, 231, 234, 237, 240, 243, 246, 249, 252, 255, 258, 261, 264, 267, 270, 273, 276, 279, 282, 285, 288, 291, 294, 297, 300, 303, 306, 309, 312, 315, 318, 321, 324, 327, 330, 333, 336, 339, 342, 345, 348, 351, 354, 357, 360, 363, 366, 369, 372, 375, 378, 381, 384, 387, 388, 390, 391, 399, 402, 403, 405, 406, 407, 408, 411, 415} preds:[]
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
        r7 = this;
        r0 = "OMX.google";
        r0 = r8.startsWith(r0);
        r1 = 0;
        if (r0 == 0) goto L_0x000a;
    L_0x0009:
        return r1;
    L_0x000a:
        r0 = com.google.android.exoplayer2.video.MediaCodecVideoRenderer.class;
        monitor-enter(r0);
        r2 = evaluatedDeviceNeedsSetOutputSurfaceWorkaround;	 Catch:{ all -> 0x0631 }
        if (r2 != 0) goto L_0x062c;	 Catch:{ all -> 0x0631 }
    L_0x0011:
        r2 = com.google.android.exoplayer2.util.Util.SDK_INT;	 Catch:{ all -> 0x0631 }
        r3 = 27;	 Catch:{ all -> 0x0631 }
        r4 = 1;	 Catch:{ all -> 0x0631 }
        if (r2 > r3) goto L_0x0026;	 Catch:{ all -> 0x0631 }
    L_0x0018:
        r2 = "dangal";	 Catch:{ all -> 0x0631 }
        r5 = com.google.android.exoplayer2.util.Util.DEVICE;	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r5);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0026;	 Catch:{ all -> 0x0631 }
    L_0x0022:
        deviceNeedsSetOutputSurfaceWorkaround = r4;	 Catch:{ all -> 0x0631 }
        goto L_0x0629;	 Catch:{ all -> 0x0631 }
        r2 = com.google.android.exoplayer2.util.Util.SDK_INT;	 Catch:{ all -> 0x0631 }
        if (r2 < r3) goto L_0x002d;	 Catch:{ all -> 0x0631 }
    L_0x002b:
        goto L_0x0629;	 Catch:{ all -> 0x0631 }
    L_0x002d:
        r2 = com.google.android.exoplayer2.util.Util.DEVICE;	 Catch:{ all -> 0x0631 }
        r5 = r2.hashCode();	 Catch:{ all -> 0x0631 }
        r6 = -1;	 Catch:{ all -> 0x0631 }
        switch(r5) {
            case -2144781245: goto L_0x05ea;
            case -2144781185: goto L_0x05df;
            case -2144781160: goto L_0x05d4;
            case -2097309513: goto L_0x05c9;
            case -2022874474: goto L_0x05be;
            case -1978993182: goto L_0x05b3;
            case -1978990237: goto L_0x05a8;
            case -1936688988: goto L_0x059d;
            case -1936688066: goto L_0x0592;
            case -1936688065: goto L_0x0586;
            case -1931988508: goto L_0x057a;
            case -1696512866: goto L_0x056e;
            case -1680025915: goto L_0x0562;
            case -1615810839: goto L_0x0556;
            case -1554255044: goto L_0x0549;
            case -1481772737: goto L_0x053d;
            case -1481772730: goto L_0x0531;
            case -1481772729: goto L_0x0525;
            case -1320080169: goto L_0x0519;
            case -1217592143: goto L_0x050d;
            case -1180384755: goto L_0x0501;
            case -1139198265: goto L_0x04f5;
            case -1052835013: goto L_0x04e9;
            case -993250464: goto L_0x04de;
            case -965403638: goto L_0x04d1;
            case -958336948: goto L_0x04c5;
            case -879245230: goto L_0x04b8;
            case -842500323: goto L_0x04ac;
            case -821392978: goto L_0x04a1;
            case -797483286: goto L_0x0495;
            case -794946968: goto L_0x0488;
            case -788334647: goto L_0x047b;
            case -782144577: goto L_0x046f;
            case -575125681: goto L_0x0463;
            case -521118391: goto L_0x0457;
            case -430914369: goto L_0x044b;
            case -290434366: goto L_0x043e;
            case -282781963: goto L_0x0432;
            case -277133239: goto L_0x0426;
            case -173639913: goto L_0x041a;
            case -56598463: goto L_0x040d;
            case 2126: goto L_0x0401;
            case 2564: goto L_0x03f5;
            case 2715: goto L_0x03e9;
            case 2719: goto L_0x03dd;
            case 3483: goto L_0x03d1;
            case 73405: goto L_0x03c5;
            case 75739: goto L_0x03b9;
            case 76779: goto L_0x03ad;
            case 78669: goto L_0x03a1;
            case 79305: goto L_0x0395;
            case 80618: goto L_0x0389;
            case 88274: goto L_0x037d;
            case 98846: goto L_0x0371;
            case 98848: goto L_0x0365;
            case 99329: goto L_0x0359;
            case 101481: goto L_0x034d;
            case 1513190: goto L_0x0342;
            case 1514184: goto L_0x0337;
            case 1514185: goto L_0x032c;
            case 2436959: goto L_0x0320;
            case 2463773: goto L_0x0314;
            case 2464648: goto L_0x0308;
            case 2689555: goto L_0x02fc;
            case 3154429: goto L_0x02f0;
            case 3284551: goto L_0x02e4;
            case 3351335: goto L_0x02d8;
            case 3386211: goto L_0x02cc;
            case 41325051: goto L_0x02c0;
            case 55178625: goto L_0x02b4;
            case 61542055: goto L_0x02a9;
            case 65355429: goto L_0x029d;
            case 66214468: goto L_0x0291;
            case 66214470: goto L_0x0285;
            case 66214473: goto L_0x0279;
            case 66215429: goto L_0x026d;
            case 66215431: goto L_0x0261;
            case 66215433: goto L_0x0255;
            case 66216390: goto L_0x0249;
            case 76402249: goto L_0x023d;
            case 76404105: goto L_0x0231;
            case 76404911: goto L_0x0225;
            case 80963634: goto L_0x0219;
            case 82882791: goto L_0x020d;
            case 98715550: goto L_0x0201;
            case 102844228: goto L_0x01f5;
            case 165221241: goto L_0x01ea;
            case 182191441: goto L_0x01de;
            case 245388979: goto L_0x01d2;
            case 287431619: goto L_0x01c6;
            case 307593612: goto L_0x01ba;
            case 308517133: goto L_0x01ae;
            case 316215098: goto L_0x01a2;
            case 316215116: goto L_0x0196;
            case 316246811: goto L_0x018a;
            case 316246818: goto L_0x017e;
            case 407160593: goto L_0x0172;
            case 507412548: goto L_0x0166;
            case 793982701: goto L_0x015a;
            case 794038622: goto L_0x014e;
            case 794040393: goto L_0x0142;
            case 835649806: goto L_0x0136;
            case 917340916: goto L_0x012b;
            case 958008161: goto L_0x011f;
            case 1060579533: goto L_0x0113;
            case 1150207623: goto L_0x0107;
            case 1176899427: goto L_0x00fb;
            case 1280332038: goto L_0x00ef;
            case 1306947716: goto L_0x00e3;
            case 1349174697: goto L_0x00d7;
            case 1522194893: goto L_0x00ca;
            case 1691543273: goto L_0x00be;
            case 1709443163: goto L_0x00b2;
            case 1865889110: goto L_0x00a5;
            case 1906253259: goto L_0x0099;
            case 1977196784: goto L_0x008d;
            case 2006372676: goto L_0x0081;
            case 2029784656: goto L_0x0075;
            case 2030379515: goto L_0x0069;
            case 2033393791: goto L_0x005d;
            case 2047190025: goto L_0x0051;
            case 2047252157: goto L_0x0045;
            case 2048319463: goto L_0x0039;
            default: goto L_0x0037;
        };	 Catch:{ all -> 0x0631 }
    L_0x0037:
        goto L_0x05f5;	 Catch:{ all -> 0x0631 }
    L_0x0039:
        r3 = "HWVNS-H";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0041:
        r2 = 53;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0045:
        r5 = "ELUGA_Prim";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r5);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x004d:
        r2 = 27;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0051:
        r3 = "ELUGA_Note";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0059:
        r2 = 26;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x005d:
        r3 = "ASUS_X00AD_2";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0065:
        r2 = 11;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0069:
        r3 = "HWCAM-H";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0071:
        r2 = 52;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0075:
        r3 = "HWBLN-H";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x007d:
        r2 = 51;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0081:
        r3 = "BRAVIA_ATV3_4K";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0089:
        r2 = 15;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x008d:
        r3 = "Infinix-X572";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0095:
        r2 = 56;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0099:
        r3 = "PB2-670M";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x00a1:
        r2 = 84;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x00a5:
        r3 = "santoni";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x00ae:
        r2 = 100;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x00b2:
        r3 = "iball8735_9806";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x00ba:
        r2 = 55;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x00be:
        r3 = "CPH1609";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x00c6:
        r2 = 19;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x00ca:
        r3 = "woods_f";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x00d3:
        r2 = 116; // 0x74 float:1.63E-43 double:5.73E-322;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x00d7:
        r3 = "htc_e56ml_dtul";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x00df:
        r2 = 49;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x00e3:
        r3 = "EverStar_S";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x00eb:
        r2 = 29;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x00ef:
        r3 = "hwALE-H";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x00f7:
        r2 = 50;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x00fb:
        r3 = "itel_S41";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0103:
        r2 = 58;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0107:
        r3 = "LS-5017";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x010f:
        r2 = 64;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0113:
        r3 = "panell_d";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x011b:
        r2 = 80;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x011f:
        r3 = "j2xlteins";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0127:
        r2 = 59;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x012b:
        r3 = "A7000plus";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0133:
        r2 = 7;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0136:
        r3 = "manning";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x013e:
        r2 = 66;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0142:
        r3 = "GIONEE_WBL7519";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x014a:
        r2 = 47;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x014e:
        r3 = "GIONEE_WBL7365";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0156:
        r2 = 46;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x015a:
        r3 = "GIONEE_WBL5708";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0162:
        r2 = 45;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0166:
        r3 = "QM16XE_U";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x016e:
        r2 = 98;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0172:
        r3 = "Pixi5-10_4G";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x017a:
        r2 = 90;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x017e:
        r3 = "TB3-850M";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0186:
        r2 = 108; // 0x6c float:1.51E-43 double:5.34E-322;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x018a:
        r3 = "TB3-850F";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0192:
        r2 = 107; // 0x6b float:1.5E-43 double:5.3E-322;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0196:
        r3 = "TB3-730X";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x019e:
        r2 = 106; // 0x6a float:1.49E-43 double:5.24E-322;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x01a2:
        r3 = "TB3-730F";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x01aa:
        r2 = 105; // 0x69 float:1.47E-43 double:5.2E-322;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x01ae:
        r3 = "A7020a48";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x01b6:
        r2 = 9;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x01ba:
        r3 = "A7010a48";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x01c2:
        r2 = 8;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x01c6:
        r3 = "griffin";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x01ce:
        r2 = 48;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x01d2:
        r3 = "marino_f";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x01da:
        r2 = 67;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x01de:
        r3 = "CPY83_I00";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x01e6:
        r2 = 20;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x01ea:
        r3 = "A2016a40";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x01f2:
        r2 = 5;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x01f5:
        r3 = "le_x6";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x01fd:
        r2 = 63;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0201:
        r3 = "i9031";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0209:
        r2 = 54;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x020d:
        r3 = "X3_HK";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0215:
        r2 = 118; // 0x76 float:1.65E-43 double:5.83E-322;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0219:
        r3 = "V23GB";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0221:
        r2 = 111; // 0x6f float:1.56E-43 double:5.5E-322;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0225:
        r3 = "Q4310";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x022d:
        r2 = 96;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0231:
        r3 = "Q4260";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0239:
        r2 = 94;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x023d:
        r3 = "PRO7S";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0245:
        r2 = 92;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0249:
        r3 = "F3311";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0251:
        r2 = 36;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0255:
        r3 = "F3215";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x025d:
        r2 = 35;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0261:
        r3 = "F3213";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0269:
        r2 = 34;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x026d:
        r3 = "F3211";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0275:
        r2 = 33;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0279:
        r3 = "F3116";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0281:
        r2 = 32;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0285:
        r3 = "F3113";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x028d:
        r2 = 31;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0291:
        r3 = "F3111";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0299:
        r2 = 30;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x029d:
        r3 = "E5643";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x02a5:
        r2 = 24;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x02a9:
        r3 = "A1601";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x02b1:
        r2 = 4;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x02b4:
        r3 = "Aura_Note_2";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x02bc:
        r2 = 12;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x02c0:
        r3 = "MEIZU_M5";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x02c8:
        r2 = 68;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x02cc:
        r3 = "p212";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x02d4:
        r2 = 77;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x02d8:
        r3 = "mido";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x02e0:
        r2 = 70;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x02e4:
        r3 = "kate";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x02ec:
        r2 = 62;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x02f0:
        r3 = "fugu";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x02f8:
        r2 = 38;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x02fc:
        r3 = "XE2X";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0304:
        r2 = 119; // 0x77 float:1.67E-43 double:5.9E-322;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0308:
        r3 = "Q427";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0310:
        r2 = 95;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0314:
        r3 = "Q350";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x031c:
        r2 = 93;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0320:
        r3 = "P681";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0328:
        r2 = 78;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x032c:
        r3 = "1714";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0334:
        r2 = 2;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0337:
        r3 = "1713";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x033f:
        r2 = 1;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0342:
        r3 = "1601";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x034a:
        r2 = 0;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x034d:
        r3 = "flo";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0355:
        r2 = 37;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0359:
        r3 = "deb";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0361:
        r2 = 23;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0365:
        r3 = "cv3";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x036d:
        r2 = 22;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0371:
        r3 = "cv1";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0379:
        r2 = 21;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x037d:
        r3 = "Z80";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0385:
        r2 = 122; // 0x7a float:1.71E-43 double:6.03E-322;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0389:
        r3 = "QX1";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0391:
        r2 = 99;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0395:
        r3 = "PLE";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x039d:
        r2 = 91;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x03a1:
        r3 = "P85";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x03a9:
        r2 = 79;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x03ad:
        r3 = "MX6";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x03b5:
        r2 = 71;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x03b9:
        r3 = "M5c";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x03c1:
        r2 = 65;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x03c5:
        r3 = "JGZ";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x03cd:
        r2 = 60;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x03d1:
        r3 = "mh";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x03d9:
        r2 = 69;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x03dd:
        r3 = "V5";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x03e5:
        r2 = 112; // 0x70 float:1.57E-43 double:5.53E-322;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x03e9:
        r3 = "V1";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x03f1:
        r2 = 110; // 0x6e float:1.54E-43 double:5.43E-322;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x03f5:
        r3 = "Q5";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x03fd:
        r2 = 97;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0401:
        r3 = "C1";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0409:
        r2 = 16;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x040d:
        r3 = "woods_fn";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0416:
        r2 = 117; // 0x75 float:1.64E-43 double:5.8E-322;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x041a:
        r3 = "ELUGA_A3_Pro";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0422:
        r2 = 25;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0426:
        r3 = "Z12_PRO";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x042e:
        r2 = 121; // 0x79 float:1.7E-43 double:6.0E-322;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0432:
        r3 = "BLACK-1X";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x043a:
        r2 = 13;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x043e:
        r3 = "taido_row";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0447:
        r2 = 104; // 0x68 float:1.46E-43 double:5.14E-322;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x044b:
        r3 = "Pixi4-7_3G";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0453:
        r2 = 89;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0457:
        r3 = "GIONEE_GBL7360";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x045f:
        r2 = 41;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0463:
        r3 = "GiONEE_CBL7513";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x046b:
        r2 = 39;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x046f:
        r3 = "OnePlus5T";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0477:
        r2 = 76;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x047b:
        r3 = "whyred";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0484:
        r2 = 115; // 0x73 float:1.61E-43 double:5.7E-322;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0488:
        r3 = "watson";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0491:
        r2 = 114; // 0x72 float:1.6E-43 double:5.63E-322;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0495:
        r3 = "SVP-DTV15";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x049d:
        r2 = 102; // 0x66 float:1.43E-43 double:5.04E-322;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x04a1:
        r3 = "A7000-a";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x04a9:
        r2 = 6;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x04ac:
        r3 = "nicklaus_f";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x04b4:
        r2 = 73;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x04b8:
        r3 = "tcl_eu";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x04c1:
        r2 = 109; // 0x6d float:1.53E-43 double:5.4E-322;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x04c5:
        r3 = "ELUGA_Ray_X";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x04cd:
        r2 = 28;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x04d1:
        r3 = "s905x018";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x04da:
        r2 = 103; // 0x67 float:1.44E-43 double:5.1E-322;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x04de:
        r3 = "A10-70F";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x04e6:
        r2 = 3;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x04e9:
        r3 = "namath";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x04f1:
        r2 = 72;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x04f5:
        r3 = "Slate_Pro";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x04fd:
        r2 = 101; // 0x65 float:1.42E-43 double:5.0E-322;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0501:
        r3 = "iris60";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0509:
        r2 = 57;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x050d:
        r3 = "BRAVIA_ATV2";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0515:
        r2 = 14;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0519:
        r3 = "GiONEE_GBL7319";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0521:
        r2 = 40;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0525:
        r3 = "panell_dt";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x052d:
        r2 = 83;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0531:
        r3 = "panell_ds";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0539:
        r2 = 82;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x053d:
        r3 = "panell_dl";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0545:
        r2 = 81;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0549:
        r3 = "vernee_M5";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0552:
        r2 = 113; // 0x71 float:1.58E-43 double:5.6E-322;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0556:
        r3 = "Phantom6";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x055e:
        r2 = 88;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0562:
        r3 = "ComioS1";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x056a:
        r2 = 17;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x056e:
        r3 = "XT1663";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0576:
        r2 = 120; // 0x78 float:1.68E-43 double:5.93E-322;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x057a:
        r3 = "AquaPowerM";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x0582:
        r2 = 10;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0586:
        r3 = "PGN611";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x058e:
        r2 = 87;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x0592:
        r3 = "PGN610";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x059a:
        r2 = 86;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x059d:
        r3 = "PGN528";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x05a5:
        r2 = 85;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x05a8:
        r3 = "NX573J";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x05b0:
        r2 = 75;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x05b3:
        r3 = "NX541J";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x05bb:
        r2 = 74;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x05be:
        r3 = "CP8676_I02";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x05c6:
        r2 = 18;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x05c9:
        r3 = "K50a40";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x05d1:
        r2 = 61;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x05d4:
        r3 = "GIONEE_SWW1631";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x05dc:
        r2 = 44;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x05df:
        r3 = "GIONEE_SWW1627";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x05e7:
        r2 = 43;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x05ea:
        r3 = "GIONEE_SWW1609";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0631 }
    L_0x05f2:
        r2 = 42;	 Catch:{ all -> 0x0631 }
        goto L_0x05f6;	 Catch:{ all -> 0x0631 }
    L_0x05f5:
        r2 = -1;	 Catch:{ all -> 0x0631 }
    L_0x05f6:
        switch(r2) {
            case 0: goto L_0x05fa;
            case 1: goto L_0x05fa;
            case 2: goto L_0x05fa;
            case 3: goto L_0x05fa;
            case 4: goto L_0x05fa;
            case 5: goto L_0x05fa;
            case 6: goto L_0x05fa;
            case 7: goto L_0x05fa;
            case 8: goto L_0x05fa;
            case 9: goto L_0x05fa;
            case 10: goto L_0x05fa;
            case 11: goto L_0x05fa;
            case 12: goto L_0x05fa;
            case 13: goto L_0x05fa;
            case 14: goto L_0x05fa;
            case 15: goto L_0x05fa;
            case 16: goto L_0x05fa;
            case 17: goto L_0x05fa;
            case 18: goto L_0x05fa;
            case 19: goto L_0x05fa;
            case 20: goto L_0x05fa;
            case 21: goto L_0x05fa;
            case 22: goto L_0x05fa;
            case 23: goto L_0x05fa;
            case 24: goto L_0x05fa;
            case 25: goto L_0x05fa;
            case 26: goto L_0x05fa;
            case 27: goto L_0x05fa;
            case 28: goto L_0x05fa;
            case 29: goto L_0x05fa;
            case 30: goto L_0x05fa;
            case 31: goto L_0x05fa;
            case 32: goto L_0x05fa;
            case 33: goto L_0x05fa;
            case 34: goto L_0x05fa;
            case 35: goto L_0x05fa;
            case 36: goto L_0x05fa;
            case 37: goto L_0x05fa;
            case 38: goto L_0x05fa;
            case 39: goto L_0x05fa;
            case 40: goto L_0x05fa;
            case 41: goto L_0x05fa;
            case 42: goto L_0x05fa;
            case 43: goto L_0x05fa;
            case 44: goto L_0x05fa;
            case 45: goto L_0x05fa;
            case 46: goto L_0x05fa;
            case 47: goto L_0x05fa;
            case 48: goto L_0x05fa;
            case 49: goto L_0x05fa;
            case 50: goto L_0x05fa;
            case 51: goto L_0x05fa;
            case 52: goto L_0x05fa;
            case 53: goto L_0x05fa;
            case 54: goto L_0x05fa;
            case 55: goto L_0x05fa;
            case 56: goto L_0x05fa;
            case 57: goto L_0x05fa;
            case 58: goto L_0x05fa;
            case 59: goto L_0x05fa;
            case 60: goto L_0x05fa;
            case 61: goto L_0x05fa;
            case 62: goto L_0x05fa;
            case 63: goto L_0x05fa;
            case 64: goto L_0x05fa;
            case 65: goto L_0x05fa;
            case 66: goto L_0x05fa;
            case 67: goto L_0x05fa;
            case 68: goto L_0x05fa;
            case 69: goto L_0x05fa;
            case 70: goto L_0x05fa;
            case 71: goto L_0x05fa;
            case 72: goto L_0x05fa;
            case 73: goto L_0x05fa;
            case 74: goto L_0x05fa;
            case 75: goto L_0x05fa;
            case 76: goto L_0x05fa;
            case 77: goto L_0x05fa;
            case 78: goto L_0x05fa;
            case 79: goto L_0x05fa;
            case 80: goto L_0x05fa;
            case 81: goto L_0x05fa;
            case 82: goto L_0x05fa;
            case 83: goto L_0x05fa;
            case 84: goto L_0x05fa;
            case 85: goto L_0x05fa;
            case 86: goto L_0x05fa;
            case 87: goto L_0x05fa;
            case 88: goto L_0x05fa;
            case 89: goto L_0x05fa;
            case 90: goto L_0x05fa;
            case 91: goto L_0x05fa;
            case 92: goto L_0x05fa;
            case 93: goto L_0x05fa;
            case 94: goto L_0x05fa;
            case 95: goto L_0x05fa;
            case 96: goto L_0x05fa;
            case 97: goto L_0x05fa;
            case 98: goto L_0x05fa;
            case 99: goto L_0x05fa;
            case 100: goto L_0x05fa;
            case 101: goto L_0x05fa;
            case 102: goto L_0x05fa;
            case 103: goto L_0x05fa;
            case 104: goto L_0x05fa;
            case 105: goto L_0x05fa;
            case 106: goto L_0x05fa;
            case 107: goto L_0x05fa;
            case 108: goto L_0x05fa;
            case 109: goto L_0x05fa;
            case 110: goto L_0x05fa;
            case 111: goto L_0x05fa;
            case 112: goto L_0x05fa;
            case 113: goto L_0x05fa;
            case 114: goto L_0x05fa;
            case 115: goto L_0x05fa;
            case 116: goto L_0x05fa;
            case 117: goto L_0x05fa;
            case 118: goto L_0x05fa;
            case 119: goto L_0x05fa;
            case 120: goto L_0x05fa;
            case 121: goto L_0x05fa;
            case 122: goto L_0x05fa;
            default: goto L_0x05f9;
        };	 Catch:{ all -> 0x0631 }
    L_0x05f9:
        goto L_0x05fd;	 Catch:{ all -> 0x0631 }
    L_0x05fa:
        deviceNeedsSetOutputSurfaceWorkaround = r4;	 Catch:{ all -> 0x0631 }
    L_0x05fd:
        r2 = com.google.android.exoplayer2.util.Util.MODEL;	 Catch:{ all -> 0x0631 }
        r3 = r2.hashCode();	 Catch:{ all -> 0x0631 }
        r5 = 2006354; // 0x1e9d52 float:2.811501E-39 double:9.912706E-318;	 Catch:{ all -> 0x0631 }
        if (r3 == r5) goto L_0x0618;	 Catch:{ all -> 0x0631 }
    L_0x0608:
        r1 = 2006367; // 0x1e9d5f float:2.811519E-39 double:9.91277E-318;	 Catch:{ all -> 0x0631 }
        if (r3 == r1) goto L_0x060e;	 Catch:{ all -> 0x0631 }
    L_0x060d:
        goto L_0x0621;	 Catch:{ all -> 0x0631 }
    L_0x060e:
        r1 = "AFTN";	 Catch:{ all -> 0x0631 }
        r1 = r2.equals(r1);	 Catch:{ all -> 0x0631 }
        if (r1 == 0) goto L_0x060d;	 Catch:{ all -> 0x0631 }
    L_0x0616:
        r1 = 1;	 Catch:{ all -> 0x0631 }
        goto L_0x0622;	 Catch:{ all -> 0x0631 }
    L_0x0618:
        r3 = "AFTA";	 Catch:{ all -> 0x0631 }
        r2 = r2.equals(r3);	 Catch:{ all -> 0x0631 }
        if (r2 == 0) goto L_0x060d;	 Catch:{ all -> 0x0631 }
    L_0x0620:
        goto L_0x0622;	 Catch:{ all -> 0x0631 }
    L_0x0621:
        r1 = -1;	 Catch:{ all -> 0x0631 }
    L_0x0622:
        switch(r1) {
            case 0: goto L_0x0626;
            case 1: goto L_0x0626;
            default: goto L_0x0625;
        };	 Catch:{ all -> 0x0631 }
    L_0x0625:
        goto L_0x0629;	 Catch:{ all -> 0x0631 }
    L_0x0626:
        deviceNeedsSetOutputSurfaceWorkaround = r4;	 Catch:{ all -> 0x0631 }
    L_0x0629:
        evaluatedDeviceNeedsSetOutputSurfaceWorkaround = r4;	 Catch:{ all -> 0x0631 }
        goto L_0x062d;	 Catch:{ all -> 0x0631 }
    L_0x062d:
        monitor-exit(r0);	 Catch:{ all -> 0x0631 }
        r0 = deviceNeedsSetOutputSurfaceWorkaround;
        return r0;
    L_0x0631:
        r1 = move-exception;
        monitor-exit(r0);	 Catch:{ all -> 0x0631 }
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.video.MediaCodecVideoRenderer.codecNeedsSetOutputSurfaceWorkaround(java.lang.String):boolean");
    }

    public MediaCodecVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector) {
        this(context, mediaCodecSelector, 0);
    }

    public MediaCodecVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector, long allowedJoiningTimeMs) {
        this(context, mediaCodecSelector, allowedJoiningTimeMs, null, null, -1);
    }

    public MediaCodecVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector, long allowedJoiningTimeMs, @Nullable Handler eventHandler, @Nullable VideoRendererEventListener eventListener, int maxDroppedFramesToNotify) {
        this(context, mediaCodecSelector, allowedJoiningTimeMs, null, false, eventHandler, eventListener, maxDroppedFramesToNotify);
    }

    public MediaCodecVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector, long allowedJoiningTimeMs, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, @Nullable Handler eventHandler, @Nullable VideoRendererEventListener eventListener, int maxDroppedFramesToNotify) {
        super(2, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, 30.0f);
        this.allowedJoiningTimeMs = allowedJoiningTimeMs;
        this.maxDroppedFramesToNotify = maxDroppedFramesToNotify;
        this.context = context.getApplicationContext();
        this.frameReleaseTimeHelper = new VideoFrameReleaseTimeHelper(this.context);
        this.eventDispatcher = new VideoRendererEventListener$EventDispatcher(eventHandler, eventListener);
        this.deviceNeedsNoPostProcessWorkaround = deviceNeedsNoPostProcessWorkaround();
        this.pendingOutputStreamOffsetsUs = new long[10];
        this.pendingOutputStreamSwitchTimesUs = new long[10];
        this.outputStreamOffsetUs = C0555C.TIME_UNSET;
        this.lastInputTimeUs = C0555C.TIME_UNSET;
        this.joiningDeadlineMs = C0555C.TIME_UNSET;
        this.currentWidth = -1;
        this.currentHeight = -1;
        this.currentPixelWidthHeightRatio = -1.0f;
        this.pendingPixelWidthHeightRatio = -1.0f;
        this.scalingMode = 1;
        clearReportedVideoSize();
    }

    protected int supportsFormat(MediaCodecSelector mediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, Format format) throws DecoderQueryException {
        int tunnelingSupport = 0;
        if (!MimeTypes.isVideo(format.sampleMimeType)) {
            return 0;
        }
        boolean requiresSecureDecryption = false;
        DrmInitData drmInitData = format.drmInitData;
        if (drmInitData != null) {
            for (int i = 0; i < drmInitData.schemeDataCount; i++) {
                requiresSecureDecryption |= drmInitData.get(i).requiresSecureDecryption;
            }
        }
        List<MediaCodecInfo> decoderInfos = mediaCodecSelector.getDecoderInfos(format.sampleMimeType, requiresSecureDecryption);
        int i2 = 2;
        if (decoderInfos.isEmpty()) {
            if (requiresSecureDecryption) {
                if (!mediaCodecSelector.getDecoderInfos(format.sampleMimeType, false).isEmpty()) {
                    return i2;
                }
            }
            i2 = 1;
            return i2;
        } else if (!BaseRenderer.supportsFormatDrm(drmSessionManager, drmInitData)) {
            return 2;
        } else {
            MediaCodecInfo decoderInfo = (MediaCodecInfo) decoderInfos.get(0);
            boolean isFormatSupported = decoderInfo.isFormatSupported(format);
            int adaptiveSupport = decoderInfo.isSeamlessAdaptationSupported(format) ? 16 : 8;
            if (decoderInfo.tunneling) {
                tunnelingSupport = 32;
            }
            return (adaptiveSupport | tunnelingSupport) | (isFormatSupported ? 4 : 3);
        }
    }

    protected void onEnabled(boolean joining) throws ExoPlaybackException {
        super.onEnabled(joining);
        this.tunnelingAudioSessionId = getConfiguration().tunnelingAudioSessionId;
        this.tunneling = this.tunnelingAudioSessionId != 0;
        this.eventDispatcher.enabled(this.decoderCounters);
        this.frameReleaseTimeHelper.enable();
    }

    protected void onStreamChanged(Format[] formats, long offsetUs) throws ExoPlaybackException {
        if (this.outputStreamOffsetUs == C0555C.TIME_UNSET) {
            this.outputStreamOffsetUs = offsetUs;
        } else {
            int i = this.pendingOutputStreamOffsetCount;
            if (i == this.pendingOutputStreamOffsetsUs.length) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Too many stream changes, so dropping offset: ");
                stringBuilder.append(this.pendingOutputStreamOffsetsUs[this.pendingOutputStreamOffsetCount - 1]);
                Log.m10w(str, stringBuilder.toString());
            } else {
                this.pendingOutputStreamOffsetCount = i + 1;
            }
            long[] jArr = this.pendingOutputStreamOffsetsUs;
            int i2 = this.pendingOutputStreamOffsetCount;
            jArr[i2 - 1] = offsetUs;
            this.pendingOutputStreamSwitchTimesUs[i2 - 1] = this.lastInputTimeUs;
        }
        super.onStreamChanged(formats, offsetUs);
    }

    protected void onPositionReset(long positionUs, boolean joining) throws ExoPlaybackException {
        super.onPositionReset(positionUs, joining);
        clearRenderedFirstFrame();
        this.initialPositionUs = C0555C.TIME_UNSET;
        this.consecutiveDroppedFrameCount = 0;
        this.lastInputTimeUs = C0555C.TIME_UNSET;
        int i = this.pendingOutputStreamOffsetCount;
        if (i != 0) {
            this.outputStreamOffsetUs = this.pendingOutputStreamOffsetsUs[i - 1];
            this.pendingOutputStreamOffsetCount = 0;
        }
        if (joining) {
            setJoiningDeadlineMs();
        } else {
            this.joiningDeadlineMs = C0555C.TIME_UNSET;
        }
    }

    public boolean isReady() {
        if (super.isReady()) {
            if (!this.renderedFirstFrame) {
                Surface surface = this.dummySurface;
                if (surface == null || this.surface != surface) {
                    if (getCodec() != null) {
                        if (this.tunneling) {
                        }
                    }
                    this.joiningDeadlineMs = C0555C.TIME_UNSET;
                    return true;
                }
            }
            this.joiningDeadlineMs = C0555C.TIME_UNSET;
            return true;
        }
        if (this.joiningDeadlineMs == C0555C.TIME_UNSET) {
            return false;
        }
        if (SystemClock.elapsedRealtime() < this.joiningDeadlineMs) {
            return true;
        }
        this.joiningDeadlineMs = C0555C.TIME_UNSET;
        return false;
    }

    protected void onStarted() {
        super.onStarted();
        this.droppedFrames = 0;
        this.droppedFrameAccumulationStartTimeMs = SystemClock.elapsedRealtime();
        this.lastRenderTimeUs = SystemClock.elapsedRealtime() * 1000;
    }

    protected void onStopped() {
        this.joiningDeadlineMs = C0555C.TIME_UNSET;
        maybeNotifyDroppedFrames();
        super.onStopped();
    }

    protected void onDisabled() {
        this.currentWidth = -1;
        this.currentHeight = -1;
        this.currentPixelWidthHeightRatio = -1.0f;
        this.pendingPixelWidthHeightRatio = -1.0f;
        this.outputStreamOffsetUs = C0555C.TIME_UNSET;
        this.lastInputTimeUs = C0555C.TIME_UNSET;
        this.pendingOutputStreamOffsetCount = 0;
        clearReportedVideoSize();
        clearRenderedFirstFrame();
        this.frameReleaseTimeHelper.disable();
        this.tunnelingOnFrameRenderedListener = null;
        this.tunneling = false;
        try {
            super.onDisabled();
        } finally {
            this.decoderCounters.ensureUpdated();
            this.eventDispatcher.disabled(this.decoderCounters);
        }
    }

    public void handleMessage(int messageType, @Nullable Object message) throws ExoPlaybackException {
        if (messageType == 1) {
            setSurface((Surface) message);
        } else if (messageType == 4) {
            this.scalingMode = ((Integer) message).intValue();
            MediaCodec codec = getCodec();
            if (codec != null) {
                codec.setVideoScalingMode(this.scalingMode);
            }
        } else if (messageType == 6) {
            this.frameMetadataListener = (VideoFrameMetadataListener) message;
        } else {
            super.handleMessage(messageType, message);
        }
    }

    private void setSurface(Surface surface) throws ExoPlaybackException {
        if (surface == null) {
            if (this.dummySurface != null) {
                surface = this.dummySurface;
            } else {
                MediaCodecInfo codecInfo = getCodecInfo();
                if (codecInfo != null && shouldUseDummySurface(codecInfo)) {
                    this.dummySurface = DummySurface.newInstanceV17(this.context, codecInfo.secure);
                    surface = this.dummySurface;
                }
            }
        }
        if (this.surface != surface) {
            this.surface = surface;
            int state = getState();
            if (state != 1) {
                if (state != 2) {
                    if (surface != null || surface == this.dummySurface) {
                        clearReportedVideoSize();
                        clearRenderedFirstFrame();
                    } else {
                        maybeRenotifyVideoSizeChanged();
                        clearRenderedFirstFrame();
                        if (state == 2) {
                            setJoiningDeadlineMs();
                        }
                    }
                }
            }
            MediaCodec codec = getCodec();
            if (Util.SDK_INT < 23 || codec == null || surface == null || this.codecNeedsSetOutputSurfaceWorkaround) {
                releaseCodec();
                maybeInitCodec();
            } else {
                setOutputSurfaceV23(codec, surface);
            }
            if (surface != null) {
            }
            clearReportedVideoSize();
            clearRenderedFirstFrame();
        } else if (!(surface == null || surface == this.dummySurface)) {
            maybeRenotifyVideoSizeChanged();
            maybeRenotifyRenderedFirstFrame();
        }
    }

    protected boolean shouldInitCodec(MediaCodecInfo codecInfo) {
        if (this.surface == null) {
            if (!shouldUseDummySurface(codecInfo)) {
                return false;
            }
        }
        return true;
    }

    protected boolean getCodecNeedsEosPropagation() {
        return this.tunneling;
    }

    protected void configureCodec(MediaCodecInfo codecInfo, MediaCodec codec, Format format, MediaCrypto crypto, float codecOperatingRate) throws DecoderQueryException {
        this.codecMaxValues = getCodecMaxValues(codecInfo, format, getStreamFormats());
        MediaFormat mediaFormat = getMediaFormat(format, this.codecMaxValues, codecOperatingRate, this.deviceNeedsNoPostProcessWorkaround, this.tunnelingAudioSessionId);
        if (this.surface == null) {
            Assertions.checkState(shouldUseDummySurface(codecInfo));
            if (this.dummySurface == null) {
                this.dummySurface = DummySurface.newInstanceV17(this.context, codecInfo.secure);
            }
            this.surface = this.dummySurface;
        }
        codec.configure(mediaFormat, this.surface, crypto, 0);
        if (Util.SDK_INT >= 23 && this.tunneling) {
            this.tunnelingOnFrameRenderedListener = new OnFrameRenderedListenerV23(codec);
        }
    }

    protected int canKeepCodec(MediaCodec codec, MediaCodecInfo codecInfo, Format oldFormat, Format newFormat) {
        int i = 1;
        if (codecInfo.isSeamlessAdaptationSupported(oldFormat, newFormat, true) && newFormat.width <= this.codecMaxValues.width && newFormat.height <= this.codecMaxValues.height) {
            if (getMaxInputSize(codecInfo, newFormat) <= this.codecMaxValues.inputSize) {
                if (!oldFormat.initializationDataEquals(newFormat)) {
                    i = 3;
                }
                return i;
            }
        }
        return 0;
    }

    @CallSuper
    protected void releaseCodec() {
        Surface surface;
        try {
            super.releaseCodec();
            this.buffersInCodecCount = 0;
            surface = this.dummySurface;
            if (surface != null) {
                if (this.surface == surface) {
                    this.surface = null;
                }
                this.dummySurface.release();
                this.dummySurface = null;
            }
        } catch (Throwable th) {
            this.buffersInCodecCount = 0;
            surface = this.dummySurface;
            if (surface != null) {
                if (this.surface == surface) {
                    this.surface = null;
                }
                this.dummySurface.release();
                this.dummySurface = null;
            }
        }
    }

    @CallSuper
    protected void flushCodec() throws ExoPlaybackException {
        super.flushCodec();
        this.buffersInCodecCount = 0;
    }

    protected float getCodecOperatingRate(float operatingRate, Format format, Format[] streamFormats) {
        float maxFrameRate = -1.0f;
        for (Format streamFormat : streamFormats) {
            float streamFrameRate = streamFormat.frameRate;
            if (streamFrameRate != -1.0f) {
                maxFrameRate = Math.max(maxFrameRate, streamFrameRate);
            }
        }
        if (maxFrameRate == -1.0f) {
            return -1.0f;
        }
        return maxFrameRate * operatingRate;
    }

    protected void onCodecInitialized(String name, long initializedTimestampMs, long initializationDurationMs) {
        this.eventDispatcher.decoderInitialized(name, initializedTimestampMs, initializationDurationMs);
        this.codecNeedsSetOutputSurfaceWorkaround = codecNeedsSetOutputSurfaceWorkaround(name);
    }

    protected void onInputFormatChanged(Format newFormat) throws ExoPlaybackException {
        super.onInputFormatChanged(newFormat);
        this.eventDispatcher.inputFormatChanged(newFormat);
        this.pendingPixelWidthHeightRatio = newFormat.pixelWidthHeightRatio;
        this.pendingRotationDegrees = newFormat.rotationDegrees;
    }

    @CallSuper
    protected void onQueueInputBuffer(DecoderInputBuffer buffer) {
        this.buffersInCodecCount++;
        this.lastInputTimeUs = Math.max(buffer.timeUs, this.lastInputTimeUs);
        if (Util.SDK_INT < 23 && this.tunneling) {
            onProcessedTunneledBuffer(buffer.timeUs);
        }
    }

    protected void onOutputFormatChanged(MediaCodec codec, MediaFormat outputFormat) {
        boolean hasCrop;
        int width;
        int integer;
        if (outputFormat.containsKey(KEY_CROP_RIGHT)) {
            if (outputFormat.containsKey(KEY_CROP_LEFT) && outputFormat.containsKey(KEY_CROP_BOTTOM)) {
                if (outputFormat.containsKey(KEY_CROP_TOP)) {
                    hasCrop = true;
                    if (hasCrop) {
                        width = outputFormat.getInteger("width");
                    } else {
                        width = (outputFormat.getInteger(KEY_CROP_RIGHT) - outputFormat.getInteger(KEY_CROP_LEFT)) + 1;
                    }
                    if (hasCrop) {
                        integer = outputFormat.getInteger("height");
                    } else {
                        integer = (outputFormat.getInteger(KEY_CROP_BOTTOM) - outputFormat.getInteger(KEY_CROP_TOP)) + 1;
                    }
                    processOutputFormat(codec, width, integer);
                }
            }
        }
        hasCrop = false;
        if (hasCrop) {
            width = outputFormat.getInteger("width");
        } else {
            width = (outputFormat.getInteger(KEY_CROP_RIGHT) - outputFormat.getInteger(KEY_CROP_LEFT)) + 1;
        }
        if (hasCrop) {
            integer = outputFormat.getInteger("height");
        } else {
            integer = (outputFormat.getInteger(KEY_CROP_BOTTOM) - outputFormat.getInteger(KEY_CROP_TOP)) + 1;
        }
        processOutputFormat(codec, width, integer);
    }

    protected boolean processOutputBuffer(long positionUs, long elapsedRealtimeUs, MediaCodec codec, ByteBuffer buffer, int bufferIndex, int bufferFlags, long bufferPresentationTimeUs, boolean shouldSkip, Format format) throws ExoPlaybackException {
        long j = positionUs;
        long j2 = elapsedRealtimeUs;
        MediaCodec mediaCodec = codec;
        int i = bufferIndex;
        long j3 = bufferPresentationTimeUs;
        if (this.initialPositionUs == C0555C.TIME_UNSET) {
            r8.initialPositionUs = j;
        }
        long presentationTimeUs = j3 - r8.outputStreamOffsetUs;
        if (shouldSkip) {
            skipOutputBuffer(mediaCodec, i, presentationTimeUs);
            return true;
        }
        long earlyUs = j3 - j;
        if (r8.surface != r8.dummySurface) {
            long presentationTimeUs2;
            long elapsedRealtimeNowUs = SystemClock.elapsedRealtime() * 1000;
            boolean isStarted = getState() == 2;
            if (r8.renderedFirstFrame) {
                if (isStarted) {
                    if (shouldForceRenderOutputBuffer(earlyUs, elapsedRealtimeNowUs - r8.lastRenderTimeUs)) {
                        j3 = presentationTimeUs;
                    }
                }
                if (!isStarted) {
                } else if (j == r8.initialPositionUs) {
                    j3 = presentationTimeUs;
                } else {
                    earlyUs -= elapsedRealtimeNowUs - j2;
                    long systemTimeNs = System.nanoTime();
                    j = systemTimeNs + (earlyUs * 1000);
                    long adjustedReleaseTimeNs = r8.frameReleaseTimeHelper.adjustReleaseTime(j3, j);
                    earlyUs = (adjustedReleaseTimeNs - systemTimeNs) / 1000;
                    if (shouldDropBuffersToKeyframe(earlyUs, j2)) {
                        j = earlyUs;
                        presentationTimeUs2 = presentationTimeUs;
                        if (maybeDropBuffersToKeyframe(codec, bufferIndex, presentationTimeUs, positionUs)) {
                            return false;
                        }
                    } else {
                        presentationTimeUs2 = presentationTimeUs;
                        long j4 = j;
                        j = earlyUs;
                    }
                    if (shouldDropOutputBuffer(j, j2)) {
                        dropOutputBuffer(mediaCodec, i, presentationTimeUs2);
                        return true;
                    }
                    j3 = presentationTimeUs2;
                    if (Util.SDK_INT < 21) {
                        presentationTimeUs2 = j3;
                        if (j < 30000) {
                            if (j > 11000) {
                                try {
                                    Thread.sleep((j - 10000) / 1000);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    return false;
                                }
                            }
                            notifyFrameMetadataListener(presentationTimeUs2, adjustedReleaseTimeNs, format);
                            renderOutputBuffer(mediaCodec, i, presentationTimeUs2);
                            return true;
                        }
                    } else if (j < 50000) {
                        presentationTimeUs2 = j3;
                        notifyFrameMetadataListener(j3, adjustedReleaseTimeNs, format);
                        renderOutputBufferV21(codec, bufferIndex, presentationTimeUs2, adjustedReleaseTimeNs);
                        return true;
                    }
                    return false;
                }
                return false;
            }
            j3 = presentationTimeUs;
            j = System.nanoTime();
            presentationTimeUs2 = j3;
            notifyFrameMetadataListener(j3, j, format);
            if (Util.SDK_INT >= 21) {
                renderOutputBufferV21(codec, bufferIndex, presentationTimeUs2, j);
                long j5 = presentationTimeUs2;
            } else {
                renderOutputBuffer(mediaCodec, i, presentationTimeUs2);
            }
            return true;
        } else if (!isBufferLate(earlyUs)) {
            return false;
        } else {
            skipOutputBuffer(mediaCodec, i, presentationTimeUs);
            return true;
        }
    }

    private void processOutputFormat(MediaCodec codec, int width, int height) {
        this.currentWidth = width;
        this.currentHeight = height;
        this.currentPixelWidthHeightRatio = this.pendingPixelWidthHeightRatio;
        if (Util.SDK_INT >= 21) {
            int i = this.pendingRotationDegrees;
            if (i != 90) {
                if (i == 270) {
                }
            }
            i = this.currentWidth;
            this.currentWidth = this.currentHeight;
            this.currentHeight = i;
            this.currentPixelWidthHeightRatio = 1.0f / this.currentPixelWidthHeightRatio;
        } else {
            this.currentUnappliedRotationDegrees = this.pendingRotationDegrees;
        }
        codec.setVideoScalingMode(this.scalingMode);
    }

    private void notifyFrameMetadataListener(long presentationTimeUs, long releaseTimeNs, Format format) {
        VideoFrameMetadataListener videoFrameMetadataListener = this.frameMetadataListener;
        if (videoFrameMetadataListener != null) {
            videoFrameMetadataListener.onVideoFrameAboutToBeRendered(presentationTimeUs, releaseTimeNs, format);
        }
    }

    protected long getOutputStreamOffsetUs() {
        return this.outputStreamOffsetUs;
    }

    protected void onProcessedTunneledBuffer(long presentationTimeUs) {
        Format format = updateOutputFormatForTime(presentationTimeUs);
        if (format != null) {
            processOutputFormat(getCodec(), format.width, format.height);
        }
        maybeNotifyVideoSizeChanged();
        maybeNotifyRenderedFirstFrame();
        onProcessedOutputBuffer(presentationTimeUs);
    }

    @CallSuper
    protected void onProcessedOutputBuffer(long presentationTimeUs) {
        this.buffersInCodecCount--;
        while (true) {
            int i = this.pendingOutputStreamOffsetCount;
            if (i != 0 && presentationTimeUs >= this.pendingOutputStreamSwitchTimesUs[0]) {
                Object obj = this.pendingOutputStreamOffsetsUs;
                this.outputStreamOffsetUs = obj[0];
                this.pendingOutputStreamOffsetCount = i - 1;
                System.arraycopy(obj, 1, obj, 0, this.pendingOutputStreamOffsetCount);
                Object obj2 = this.pendingOutputStreamSwitchTimesUs;
                System.arraycopy(obj2, 1, obj2, 0, this.pendingOutputStreamOffsetCount);
            }
        }
    }

    protected boolean shouldDropOutputBuffer(long earlyUs, long elapsedRealtimeUs) {
        return isBufferLate(earlyUs);
    }

    protected boolean shouldDropBuffersToKeyframe(long earlyUs, long elapsedRealtimeUs) {
        return isBufferVeryLate(earlyUs);
    }

    protected boolean shouldForceRenderOutputBuffer(long earlyUs, long elapsedSinceLastRenderUs) {
        return isBufferLate(earlyUs) && elapsedSinceLastRenderUs > 100000;
    }

    protected void skipOutputBuffer(MediaCodec codec, int index, long presentationTimeUs) {
        TraceUtil.beginSection("skipVideoBuffer");
        codec.releaseOutputBuffer(index, false);
        TraceUtil.endSection();
        DecoderCounters decoderCounters = this.decoderCounters;
        decoderCounters.skippedOutputBufferCount++;
    }

    protected void dropOutputBuffer(MediaCodec codec, int index, long presentationTimeUs) {
        TraceUtil.beginSection("dropVideoBuffer");
        codec.releaseOutputBuffer(index, false);
        TraceUtil.endSection();
        updateDroppedBufferCounters(1);
    }

    protected boolean maybeDropBuffersToKeyframe(MediaCodec codec, int index, long presentationTimeUs, long positionUs) throws ExoPlaybackException {
        int droppedSourceBufferCount = skipSource(positionUs);
        if (droppedSourceBufferCount == 0) {
            return false;
        }
        DecoderCounters decoderCounters = this.decoderCounters;
        decoderCounters.droppedToKeyframeCount++;
        updateDroppedBufferCounters(this.buffersInCodecCount + droppedSourceBufferCount);
        flushCodec();
        return true;
    }

    protected void updateDroppedBufferCounters(int droppedBufferCount) {
        DecoderCounters decoderCounters = this.decoderCounters;
        decoderCounters.droppedBufferCount += droppedBufferCount;
        this.droppedFrames += droppedBufferCount;
        this.consecutiveDroppedFrameCount += droppedBufferCount;
        this.decoderCounters.maxConsecutiveDroppedBufferCount = Math.max(this.consecutiveDroppedFrameCount, this.decoderCounters.maxConsecutiveDroppedBufferCount);
        int i = this.maxDroppedFramesToNotify;
        if (i > 0 && this.droppedFrames >= i) {
            maybeNotifyDroppedFrames();
        }
    }

    protected void renderOutputBuffer(MediaCodec codec, int index, long presentationTimeUs) {
        maybeNotifyVideoSizeChanged();
        TraceUtil.beginSection("releaseOutputBuffer");
        codec.releaseOutputBuffer(index, true);
        TraceUtil.endSection();
        this.lastRenderTimeUs = SystemClock.elapsedRealtime() * 1000;
        DecoderCounters decoderCounters = this.decoderCounters;
        decoderCounters.renderedOutputBufferCount++;
        this.consecutiveDroppedFrameCount = 0;
        maybeNotifyRenderedFirstFrame();
    }

    @TargetApi(21)
    protected void renderOutputBufferV21(MediaCodec codec, int index, long presentationTimeUs, long releaseTimeNs) {
        maybeNotifyVideoSizeChanged();
        TraceUtil.beginSection("releaseOutputBuffer");
        codec.releaseOutputBuffer(index, releaseTimeNs);
        TraceUtil.endSection();
        this.lastRenderTimeUs = SystemClock.elapsedRealtime() * 1000;
        DecoderCounters decoderCounters = this.decoderCounters;
        decoderCounters.renderedOutputBufferCount++;
        this.consecutiveDroppedFrameCount = 0;
        maybeNotifyRenderedFirstFrame();
    }

    private boolean shouldUseDummySurface(MediaCodecInfo codecInfo) {
        if (Util.SDK_INT >= 23 && !this.tunneling) {
            if (!codecNeedsSetOutputSurfaceWorkaround(codecInfo.name)) {
                if (codecInfo.secure) {
                    if (DummySurface.isSecureSupported(this.context)) {
                    }
                }
                return true;
            }
        }
        return false;
    }

    private void setJoiningDeadlineMs() {
        this.joiningDeadlineMs = this.allowedJoiningTimeMs > 0 ? SystemClock.elapsedRealtime() + this.allowedJoiningTimeMs : C0555C.TIME_UNSET;
    }

    private void clearRenderedFirstFrame() {
        this.renderedFirstFrame = false;
        if (Util.SDK_INT >= 23 && this.tunneling) {
            MediaCodec codec = getCodec();
            if (codec != null) {
                this.tunnelingOnFrameRenderedListener = new OnFrameRenderedListenerV23(codec);
            }
        }
    }

    void maybeNotifyRenderedFirstFrame() {
        if (!this.renderedFirstFrame) {
            this.renderedFirstFrame = true;
            this.eventDispatcher.renderedFirstFrame(this.surface);
        }
    }

    private void maybeRenotifyRenderedFirstFrame() {
        if (this.renderedFirstFrame) {
            this.eventDispatcher.renderedFirstFrame(this.surface);
        }
    }

    private void clearReportedVideoSize() {
        this.reportedWidth = -1;
        this.reportedHeight = -1;
        this.reportedPixelWidthHeightRatio = -1.0f;
        this.reportedUnappliedRotationDegrees = -1;
    }

    private void maybeNotifyVideoSizeChanged() {
        if (this.currentWidth == -1) {
            if (this.currentHeight != -1) {
            }
        }
        if (!(this.reportedWidth == this.currentWidth && this.reportedHeight == this.currentHeight && this.reportedUnappliedRotationDegrees == this.currentUnappliedRotationDegrees && this.reportedPixelWidthHeightRatio == this.currentPixelWidthHeightRatio)) {
            this.eventDispatcher.videoSizeChanged(this.currentWidth, this.currentHeight, this.currentUnappliedRotationDegrees, this.currentPixelWidthHeightRatio);
            this.reportedWidth = this.currentWidth;
            this.reportedHeight = this.currentHeight;
            this.reportedUnappliedRotationDegrees = this.currentUnappliedRotationDegrees;
            this.reportedPixelWidthHeightRatio = this.currentPixelWidthHeightRatio;
        }
    }

    private void maybeRenotifyVideoSizeChanged() {
        if (this.reportedWidth == -1) {
            if (this.reportedHeight == -1) {
                return;
            }
        }
        this.eventDispatcher.videoSizeChanged(this.reportedWidth, this.reportedHeight, this.reportedUnappliedRotationDegrees, this.reportedPixelWidthHeightRatio);
    }

    private void maybeNotifyDroppedFrames() {
        if (this.droppedFrames > 0) {
            long now = SystemClock.elapsedRealtime();
            this.eventDispatcher.droppedFrames(this.droppedFrames, now - this.droppedFrameAccumulationStartTimeMs);
            this.droppedFrames = 0;
            this.droppedFrameAccumulationStartTimeMs = now;
        }
    }

    private static boolean isBufferLate(long earlyUs) {
        return earlyUs < -30000;
    }

    private static boolean isBufferVeryLate(long earlyUs) {
        return earlyUs < -500000;
    }

    @TargetApi(23)
    private static void setOutputSurfaceV23(MediaCodec codec, Surface surface) {
        codec.setOutputSurface(surface);
    }

    @TargetApi(21)
    private static void configureTunnelingV21(MediaFormat mediaFormat, int tunnelingAudioSessionId) {
        mediaFormat.setFeatureEnabled("tunneled-playback", true);
        mediaFormat.setInteger("audio-session-id", tunnelingAudioSessionId);
    }

    @SuppressLint({"InlinedApi"})
    protected MediaFormat getMediaFormat(Format format, CodecMaxValues codecMaxValues, float codecOperatingRate, boolean deviceNeedsNoPostProcessWorkaround, int tunnelingAudioSessionId) {
        MediaFormat mediaFormat = new MediaFormat();
        mediaFormat.setString("mime", format.sampleMimeType);
        mediaFormat.setInteger("width", format.width);
        mediaFormat.setInteger("height", format.height);
        MediaFormatUtil.setCsdBuffers(mediaFormat, format.initializationData);
        MediaFormatUtil.maybeSetFloat(mediaFormat, "frame-rate", format.frameRate);
        MediaFormatUtil.maybeSetInteger(mediaFormat, "rotation-degrees", format.rotationDegrees);
        MediaFormatUtil.maybeSetColorInfo(mediaFormat, format.colorInfo);
        mediaFormat.setInteger("max-width", codecMaxValues.width);
        mediaFormat.setInteger("max-height", codecMaxValues.height);
        MediaFormatUtil.maybeSetInteger(mediaFormat, "max-input-size", codecMaxValues.inputSize);
        if (Util.SDK_INT >= 23) {
            mediaFormat.setInteger("priority", 0);
            if (codecOperatingRate != -1.0f) {
                mediaFormat.setFloat("operating-rate", codecOperatingRate);
            }
        }
        if (deviceNeedsNoPostProcessWorkaround) {
            mediaFormat.setInteger("no-post-process", 1);
            mediaFormat.setInteger("auto-frc", 0);
        }
        if (tunnelingAudioSessionId != 0) {
            configureTunnelingV21(mediaFormat, tunnelingAudioSessionId);
        }
        return mediaFormat;
    }

    protected CodecMaxValues getCodecMaxValues(MediaCodecInfo codecInfo, Format format, Format[] streamFormats) throws DecoderQueryException {
        int maxWidth = format.width;
        int maxHeight = format.height;
        int maxInputSize = getMaxInputSize(codecInfo, format);
        if (streamFormats.length == 1) {
            if (maxInputSize != -1) {
                int codecMaxInputSize = getCodecMaxInputSize(codecInfo, format.sampleMimeType, format.width, format.height);
                if (codecMaxInputSize != -1) {
                    maxInputSize = Math.min((int) (((float) maxInputSize) * 1069547520), codecMaxInputSize);
                }
            }
            return new CodecMaxValues(maxWidth, maxHeight, maxInputSize);
        }
        boolean haveUnknownDimensions = false;
        int maxInputSize2 = maxInputSize;
        maxInputSize = maxHeight;
        maxHeight = maxWidth;
        for (Format streamFormat : streamFormats) {
            if (codecInfo.isSeamlessAdaptationSupported(format, streamFormat, false)) {
                int i;
                if (streamFormat.width != -1) {
                    if (streamFormat.height != -1) {
                        i = 0;
                        haveUnknownDimensions |= i;
                        maxHeight = Math.max(maxHeight, streamFormat.width);
                        maxInputSize = Math.max(maxInputSize, streamFormat.height);
                        maxInputSize2 = Math.max(maxInputSize2, getMaxInputSize(codecInfo, streamFormat));
                    }
                }
                i = 1;
                haveUnknownDimensions |= i;
                maxHeight = Math.max(maxHeight, streamFormat.width);
                maxInputSize = Math.max(maxInputSize, streamFormat.height);
                maxInputSize2 = Math.max(maxInputSize2, getMaxInputSize(codecInfo, streamFormat));
            }
        }
        if (haveUnknownDimensions) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Resolutions unknown. Codec max resolution: ");
            stringBuilder.append(maxHeight);
            stringBuilder.append("x");
            stringBuilder.append(maxInputSize);
            Log.m10w(str, stringBuilder.toString());
            Point codecMaxSize = getCodecMaxSize(codecInfo, format);
            if (codecMaxSize != null) {
                maxHeight = Math.max(maxHeight, codecMaxSize.x);
                maxInputSize = Math.max(maxInputSize, codecMaxSize.y);
                maxInputSize2 = Math.max(maxInputSize2, getCodecMaxInputSize(codecInfo, format.sampleMimeType, maxHeight, maxInputSize));
                String str2 = TAG;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Codec max resolution adjusted to: ");
                stringBuilder2.append(maxHeight);
                stringBuilder2.append("x");
                stringBuilder2.append(maxInputSize);
                Log.m10w(str2, stringBuilder2.toString());
            }
        }
        return new CodecMaxValues(maxHeight, maxInputSize, maxInputSize2);
    }

    private static Point getCodecMaxSize(MediaCodecInfo codecInfo, Format format) throws DecoderQueryException {
        float f;
        MediaCodecInfo mediaCodecInfo = codecInfo;
        Format format2 = format;
        int i = 0;
        boolean isVerticalVideo = format2.height > format2.width;
        int formatLongEdgePx = isVerticalVideo ? format2.height : format2.width;
        int formatShortEdgePx = isVerticalVideo ? format2.width : format2.height;
        float aspectRatio = ((float) formatShortEdgePx) / ((float) formatLongEdgePx);
        int[] iArr = STANDARD_LONG_EDGE_VIDEO_PX;
        int length = iArr.length;
        while (i < length) {
            int longEdgePx = iArr[i];
            int shortEdgePx = (int) (((float) longEdgePx) * aspectRatio);
            if (longEdgePx <= formatLongEdgePx) {
                f = aspectRatio;
            } else if (shortEdgePx <= formatShortEdgePx) {
                r15 = formatShortEdgePx;
                f = aspectRatio;
            } else {
                if (Util.SDK_INT >= 21) {
                    Point alignedSize = mediaCodecInfo.alignVideoSizeV21(isVerticalVideo ? shortEdgePx : longEdgePx, isVerticalVideo ? longEdgePx : shortEdgePx);
                    r15 = formatShortEdgePx;
                    f = aspectRatio;
                    if (mediaCodecInfo.isVideoSizeAndRateSupportedV21(alignedSize.x, alignedSize.y, (double) format2.frameRate) != 0) {
                        return alignedSize;
                    }
                } else {
                    r15 = formatShortEdgePx;
                    f = aspectRatio;
                    int longEdgePx2 = Util.ceilDivide(longEdgePx, 16) * 16;
                    formatShortEdgePx = Util.ceilDivide(shortEdgePx, 16) * 16;
                    if (longEdgePx2 * formatShortEdgePx <= MediaCodecUtil.maxH264DecodableFrameSize()) {
                        return new Point(isVerticalVideo ? formatShortEdgePx : longEdgePx2, isVerticalVideo ? longEdgePx2 : formatShortEdgePx);
                    }
                }
                i++;
                formatShortEdgePx = r15;
                aspectRatio = f;
            }
            return null;
        }
        f = aspectRatio;
        return null;
    }

    private static int getMaxInputSize(MediaCodecInfo codecInfo, Format format) {
        if (format.maxInputSize == -1) {
            return getCodecMaxInputSize(codecInfo, format.sampleMimeType, format.width, format.height);
        }
        int totalInitializationDataSize = 0;
        for (int i = 0; i < format.initializationData.size(); i++) {
            totalInitializationDataSize += ((byte[]) format.initializationData.get(i)).length;
        }
        return format.maxInputSize + totalInitializationDataSize;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int getCodecMaxInputSize(com.google.android.exoplayer2.mediacodec.MediaCodecInfo r4, java.lang.String r5, int r6, int r7) {
        /*
        r0 = -1;
        if (r6 == r0) goto L_0x00ac;
    L_0x0003:
        if (r7 != r0) goto L_0x0007;
    L_0x0005:
        goto L_0x00ac;
    L_0x0007:
        r1 = r5.hashCode();
        switch(r1) {
            case -1664118616: goto L_0x0046;
            case -1662541442: goto L_0x003b;
            case 1187890754: goto L_0x0030;
            case 1331836730: goto L_0x0025;
            case 1599127256: goto L_0x001a;
            case 1599127257: goto L_0x000f;
            default: goto L_0x000e;
        };
    L_0x000e:
        goto L_0x0051;
    L_0x000f:
        r1 = "video/x-vnd.on2.vp9";
        r1 = r5.equals(r1);
        if (r1 == 0) goto L_0x000e;
    L_0x0018:
        r1 = 5;
        goto L_0x0052;
    L_0x001a:
        r1 = "video/x-vnd.on2.vp8";
        r1 = r5.equals(r1);
        if (r1 == 0) goto L_0x000e;
    L_0x0023:
        r1 = 3;
        goto L_0x0052;
    L_0x0025:
        r1 = "video/avc";
        r1 = r5.equals(r1);
        if (r1 == 0) goto L_0x000e;
    L_0x002e:
        r1 = 2;
        goto L_0x0052;
    L_0x0030:
        r1 = "video/mp4v-es";
        r1 = r5.equals(r1);
        if (r1 == 0) goto L_0x000e;
    L_0x0039:
        r1 = 1;
        goto L_0x0052;
    L_0x003b:
        r1 = "video/hevc";
        r1 = r5.equals(r1);
        if (r1 == 0) goto L_0x000e;
    L_0x0044:
        r1 = 4;
        goto L_0x0052;
    L_0x0046:
        r1 = "video/3gpp";
        r1 = r5.equals(r1);
        if (r1 == 0) goto L_0x000e;
    L_0x004f:
        r1 = 0;
        goto L_0x0052;
    L_0x0051:
        r1 = -1;
    L_0x0052:
        switch(r1) {
            case 0: goto L_0x00a2;
            case 1: goto L_0x00a2;
            case 2: goto L_0x005e;
            case 3: goto L_0x005a;
            case 4: goto L_0x0056;
            case 5: goto L_0x0056;
            default: goto L_0x0055;
        };
    L_0x0055:
        return r0;
    L_0x0056:
        r0 = r6 * r7;
        r1 = 4;
        goto L_0x00a6;
    L_0x005a:
        r0 = r6 * r7;
        r1 = 2;
        goto L_0x00a6;
    L_0x005e:
        r1 = "BRAVIA 4K 2015";
        r2 = com.google.android.exoplayer2.util.Util.MODEL;
        r1 = r1.equals(r2);
        if (r1 != 0) goto L_0x00a0;
    L_0x0068:
        r1 = "Amazon";
        r2 = com.google.android.exoplayer2.util.Util.MANUFACTURER;
        r1 = r1.equals(r2);
        if (r1 == 0) goto L_0x008d;
    L_0x0072:
        r1 = "KFSOWI";
        r2 = com.google.android.exoplayer2.util.Util.MODEL;
        r1 = r1.equals(r2);
        if (r1 != 0) goto L_0x008c;
    L_0x007c:
        r1 = "AFTS";
        r2 = com.google.android.exoplayer2.util.Util.MODEL;
        r1 = r1.equals(r2);
        if (r1 == 0) goto L_0x008b;
    L_0x0086:
        r1 = r4.secure;
        if (r1 == 0) goto L_0x008b;
    L_0x008a:
        goto L_0x00a1;
    L_0x008b:
        goto L_0x008e;
    L_0x008c:
        goto L_0x00a1;
    L_0x008e:
        r0 = 16;
        r1 = com.google.android.exoplayer2.util.Util.ceilDivide(r6, r0);
        r2 = com.google.android.exoplayer2.util.Util.ceilDivide(r7, r0);
        r1 = r1 * r2;
        r1 = r1 * 16;
        r0 = r1 * 16;
        r1 = 2;
        goto L_0x00a6;
    L_0x00a1:
        return r0;
    L_0x00a2:
        r0 = r6 * r7;
        r1 = 2;
    L_0x00a6:
        r2 = r0 * 3;
        r3 = r1 * 2;
        r2 = r2 / r3;
        return r2;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.video.MediaCodecVideoRenderer.getCodecMaxInputSize(com.google.android.exoplayer2.mediacodec.MediaCodecInfo, java.lang.String, int, int):int");
    }

    private static boolean deviceNeedsNoPostProcessWorkaround() {
        return "NVIDIA".equals(Util.MANUFACTURER);
    }
}
