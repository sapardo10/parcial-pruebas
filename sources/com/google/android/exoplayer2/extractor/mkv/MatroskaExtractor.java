package com.google.android.exoplayer2.extractor.mkv;

import android.support.annotation.Nullable;
import android.util.SparseArray;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.audio.Ac3Util;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmInitData.SchemeData;
import com.google.android.exoplayer2.extractor.ChunkIndex;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekMap.Unseekable;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.TrackOutput.CryptoData;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.LongArray;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

public final class MatroskaExtractor implements Extractor {
    private static final int BLOCK_STATE_DATA = 2;
    private static final int BLOCK_STATE_HEADER = 1;
    private static final int BLOCK_STATE_START = 0;
    private static final String CODEC_ID_AAC = "A_AAC";
    private static final String CODEC_ID_AC3 = "A_AC3";
    private static final String CODEC_ID_ACM = "A_MS/ACM";
    private static final String CODEC_ID_ASS = "S_TEXT/ASS";
    private static final String CODEC_ID_DTS = "A_DTS";
    private static final String CODEC_ID_DTS_EXPRESS = "A_DTS/EXPRESS";
    private static final String CODEC_ID_DTS_LOSSLESS = "A_DTS/LOSSLESS";
    private static final String CODEC_ID_DVBSUB = "S_DVBSUB";
    private static final String CODEC_ID_E_AC3 = "A_EAC3";
    private static final String CODEC_ID_FLAC = "A_FLAC";
    private static final String CODEC_ID_FOURCC = "V_MS/VFW/FOURCC";
    private static final String CODEC_ID_H264 = "V_MPEG4/ISO/AVC";
    private static final String CODEC_ID_H265 = "V_MPEGH/ISO/HEVC";
    private static final String CODEC_ID_MP2 = "A_MPEG/L2";
    private static final String CODEC_ID_MP3 = "A_MPEG/L3";
    private static final String CODEC_ID_MPEG2 = "V_MPEG2";
    private static final String CODEC_ID_MPEG4_AP = "V_MPEG4/ISO/AP";
    private static final String CODEC_ID_MPEG4_ASP = "V_MPEG4/ISO/ASP";
    private static final String CODEC_ID_MPEG4_SP = "V_MPEG4/ISO/SP";
    private static final String CODEC_ID_OPUS = "A_OPUS";
    private static final String CODEC_ID_PCM_INT_LIT = "A_PCM/INT/LIT";
    private static final String CODEC_ID_PGS = "S_HDMV/PGS";
    private static final String CODEC_ID_SUBRIP = "S_TEXT/UTF8";
    private static final String CODEC_ID_THEORA = "V_THEORA";
    private static final String CODEC_ID_TRUEHD = "A_TRUEHD";
    private static final String CODEC_ID_VOBSUB = "S_VOBSUB";
    private static final String CODEC_ID_VORBIS = "A_VORBIS";
    private static final String CODEC_ID_VP8 = "V_VP8";
    private static final String CODEC_ID_VP9 = "V_VP9";
    private static final String DOC_TYPE_MATROSKA = "matroska";
    private static final String DOC_TYPE_WEBM = "webm";
    private static final int ENCRYPTION_IV_SIZE = 8;
    public static final ExtractorsFactory FACTORY = -$$Lambda$MatroskaExtractor$jNXW0tyYIOPE6N2jicocV6rRvBs.INSTANCE;
    public static final int FLAG_DISABLE_SEEK_FOR_CUES = 1;
    private static final int FOURCC_COMPRESSION_DIVX = 1482049860;
    private static final int FOURCC_COMPRESSION_VC1 = 826496599;
    private static final int ID_AUDIO = 225;
    private static final int ID_AUDIO_BIT_DEPTH = 25188;
    private static final int ID_BLOCK = 161;
    private static final int ID_BLOCK_DURATION = 155;
    private static final int ID_BLOCK_GROUP = 160;
    private static final int ID_CHANNELS = 159;
    private static final int ID_CLUSTER = 524531317;
    private static final int ID_CODEC_DELAY = 22186;
    private static final int ID_CODEC_ID = 134;
    private static final int ID_CODEC_PRIVATE = 25506;
    private static final int ID_COLOUR = 21936;
    private static final int ID_COLOUR_PRIMARIES = 21947;
    private static final int ID_COLOUR_RANGE = 21945;
    private static final int ID_COLOUR_TRANSFER = 21946;
    private static final int ID_CONTENT_COMPRESSION = 20532;
    private static final int ID_CONTENT_COMPRESSION_ALGORITHM = 16980;
    private static final int ID_CONTENT_COMPRESSION_SETTINGS = 16981;
    private static final int ID_CONTENT_ENCODING = 25152;
    private static final int ID_CONTENT_ENCODINGS = 28032;
    private static final int ID_CONTENT_ENCODING_ORDER = 20529;
    private static final int ID_CONTENT_ENCODING_SCOPE = 20530;
    private static final int ID_CONTENT_ENCRYPTION = 20533;
    private static final int ID_CONTENT_ENCRYPTION_AES_SETTINGS = 18407;
    private static final int ID_CONTENT_ENCRYPTION_AES_SETTINGS_CIPHER_MODE = 18408;
    private static final int ID_CONTENT_ENCRYPTION_ALGORITHM = 18401;
    private static final int ID_CONTENT_ENCRYPTION_KEY_ID = 18402;
    private static final int ID_CUES = 475249515;
    private static final int ID_CUE_CLUSTER_POSITION = 241;
    private static final int ID_CUE_POINT = 187;
    private static final int ID_CUE_TIME = 179;
    private static final int ID_CUE_TRACK_POSITIONS = 183;
    private static final int ID_DEFAULT_DURATION = 2352003;
    private static final int ID_DISPLAY_HEIGHT = 21690;
    private static final int ID_DISPLAY_UNIT = 21682;
    private static final int ID_DISPLAY_WIDTH = 21680;
    private static final int ID_DOC_TYPE = 17026;
    private static final int ID_DOC_TYPE_READ_VERSION = 17029;
    private static final int ID_DURATION = 17545;
    private static final int ID_EBML = 440786851;
    private static final int ID_EBML_READ_VERSION = 17143;
    private static final int ID_FLAG_DEFAULT = 136;
    private static final int ID_FLAG_FORCED = 21930;
    private static final int ID_INFO = 357149030;
    private static final int ID_LANGUAGE = 2274716;
    private static final int ID_LUMNINANCE_MAX = 21977;
    private static final int ID_LUMNINANCE_MIN = 21978;
    private static final int ID_MASTERING_METADATA = 21968;
    private static final int ID_MAX_CLL = 21948;
    private static final int ID_MAX_FALL = 21949;
    private static final int ID_NAME = 21358;
    private static final int ID_PIXEL_HEIGHT = 186;
    private static final int ID_PIXEL_WIDTH = 176;
    private static final int ID_PRIMARY_B_CHROMATICITY_X = 21973;
    private static final int ID_PRIMARY_B_CHROMATICITY_Y = 21974;
    private static final int ID_PRIMARY_G_CHROMATICITY_X = 21971;
    private static final int ID_PRIMARY_G_CHROMATICITY_Y = 21972;
    private static final int ID_PRIMARY_R_CHROMATICITY_X = 21969;
    private static final int ID_PRIMARY_R_CHROMATICITY_Y = 21970;
    private static final int ID_PROJECTION = 30320;
    private static final int ID_PROJECTION_PRIVATE = 30322;
    private static final int ID_REFERENCE_BLOCK = 251;
    private static final int ID_SAMPLING_FREQUENCY = 181;
    private static final int ID_SEEK = 19899;
    private static final int ID_SEEK_HEAD = 290298740;
    private static final int ID_SEEK_ID = 21419;
    private static final int ID_SEEK_POSITION = 21420;
    private static final int ID_SEEK_PRE_ROLL = 22203;
    private static final int ID_SEGMENT = 408125543;
    private static final int ID_SEGMENT_INFO = 357149030;
    private static final int ID_SIMPLE_BLOCK = 163;
    private static final int ID_STEREO_MODE = 21432;
    private static final int ID_TIMECODE_SCALE = 2807729;
    private static final int ID_TIME_CODE = 231;
    private static final int ID_TRACKS = 374648427;
    private static final int ID_TRACK_ENTRY = 174;
    private static final int ID_TRACK_NUMBER = 215;
    private static final int ID_TRACK_TYPE = 131;
    private static final int ID_VIDEO = 224;
    private static final int ID_WHITE_POINT_CHROMATICITY_X = 21975;
    private static final int ID_WHITE_POINT_CHROMATICITY_Y = 21976;
    private static final int LACING_EBML = 3;
    private static final int LACING_FIXED_SIZE = 2;
    private static final int LACING_NONE = 0;
    private static final int LACING_XIPH = 1;
    private static final int OPUS_MAX_INPUT_SIZE = 5760;
    private static final byte[] SSA_DIALOGUE_FORMAT = Util.getUtf8Bytes("Format: Start, End, ReadOrder, Layer, Style, Name, MarginL, MarginR, MarginV, Effect, Text");
    private static final byte[] SSA_PREFIX = new byte[]{(byte) 68, (byte) 105, (byte) 97, (byte) 108, (byte) 111, (byte) 103, (byte) 117, (byte) 101, (byte) 58, (byte) 32, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 44, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 44};
    private static final int SSA_PREFIX_END_TIMECODE_OFFSET = 21;
    private static final byte[] SSA_TIMECODE_EMPTY = new byte[]{(byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32};
    private static final String SSA_TIMECODE_FORMAT = "%01d:%02d:%02d:%02d";
    private static final long SSA_TIMECODE_LAST_VALUE_SCALING_FACTOR = 10000;
    private static final byte[] SUBRIP_PREFIX = new byte[]{(byte) 49, (byte) 10, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 44, (byte) 48, (byte) 48, (byte) 48, (byte) 32, (byte) 45, (byte) 45, (byte) 62, (byte) 32, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 58, (byte) 48, (byte) 48, (byte) 44, (byte) 48, (byte) 48, (byte) 48, (byte) 10};
    private static final int SUBRIP_PREFIX_END_TIMECODE_OFFSET = 19;
    private static final byte[] SUBRIP_TIMECODE_EMPTY = new byte[]{(byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32};
    private static final String SUBRIP_TIMECODE_FORMAT = "%02d:%02d:%02d,%03d";
    private static final long SUBRIP_TIMECODE_LAST_VALUE_SCALING_FACTOR = 1000;
    private static final String TAG = "MatroskaExtractor";
    private static final int TRACK_TYPE_AUDIO = 2;
    private static final int UNSET_ENTRY_ID = -1;
    private static final int VORBIS_MAX_INPUT_SIZE = 8192;
    private static final int WAVE_FORMAT_EXTENSIBLE = 65534;
    private static final int WAVE_FORMAT_PCM = 1;
    private static final int WAVE_FORMAT_SIZE = 18;
    private static final UUID WAVE_SUBFORMAT_PCM = new UUID(72057594037932032L, -9223371306706625679L);
    private long blockDurationUs;
    private int blockFlags;
    private int blockLacingSampleCount;
    private int blockLacingSampleIndex;
    private int[] blockLacingSampleSizes;
    private int blockState;
    private long blockTimeUs;
    private int blockTrackNumber;
    private int blockTrackNumberLength;
    private long clusterTimecodeUs;
    private LongArray cueClusterPositions;
    private LongArray cueTimesUs;
    private long cuesContentPosition;
    private Track currentTrack;
    private long durationTimecode;
    private long durationUs;
    private final ParsableByteArray encryptionInitializationVector;
    private final ParsableByteArray encryptionSubsampleData;
    private ByteBuffer encryptionSubsampleDataBuffer;
    private ExtractorOutput extractorOutput;
    private final ParsableByteArray nalLength;
    private final ParsableByteArray nalStartCode;
    private final EbmlReader reader;
    private int sampleBytesRead;
    private int sampleBytesWritten;
    private int sampleCurrentNalBytesRemaining;
    private boolean sampleEncodingHandled;
    private boolean sampleInitializationVectorRead;
    private int samplePartitionCount;
    private boolean samplePartitionCountRead;
    private boolean sampleRead;
    private boolean sampleSeenReferenceBlock;
    private byte sampleSignalByte;
    private boolean sampleSignalByteRead;
    private final ParsableByteArray sampleStrippedBytes;
    private final ParsableByteArray scratch;
    private int seekEntryId;
    private final ParsableByteArray seekEntryIdBytes;
    private long seekEntryPosition;
    private boolean seekForCues;
    private final boolean seekForCuesEnabled;
    private long seekPositionAfterBuildingCues;
    private boolean seenClusterPositionForCurrentCuePoint;
    private long segmentContentPosition;
    private long segmentContentSize;
    private boolean sentSeekMap;
    private final ParsableByteArray subtitleSample;
    private long timecodeScale;
    private final SparseArray<Track> tracks;
    private final VarintReader varintReader;
    private final ParsableByteArray vorbisNumPageSamples;

    private static final class Track {
        private static final int DEFAULT_MAX_CLL = 1000;
        private static final int DEFAULT_MAX_FALL = 200;
        private static final int DISPLAY_UNIT_PIXELS = 0;
        private static final int MAX_CHROMATICITY = 50000;
        public int audioBitDepth;
        public int channelCount;
        public long codecDelayNs;
        public String codecId;
        public byte[] codecPrivate;
        public int colorRange;
        public int colorSpace;
        public int colorTransfer;
        public CryptoData cryptoData;
        public int defaultSampleDurationNs;
        public int displayHeight;
        public int displayUnit;
        public int displayWidth;
        public DrmInitData drmInitData;
        public boolean flagDefault;
        public boolean flagForced;
        public boolean hasColorInfo;
        public boolean hasContentEncryption;
        public int height;
        private String language;
        public int maxContentLuminance;
        public int maxFrameAverageLuminance;
        public float maxMasteringLuminance;
        public float minMasteringLuminance;
        public int nalUnitLengthFieldLength;
        public String name;
        public int number;
        public TrackOutput output;
        public float primaryBChromaticityX;
        public float primaryBChromaticityY;
        public float primaryGChromaticityX;
        public float primaryGChromaticityY;
        public float primaryRChromaticityX;
        public float primaryRChromaticityY;
        public byte[] projectionData;
        public int sampleRate;
        public byte[] sampleStrippedBytes;
        public long seekPreRollNs;
        public int stereoMode;
        @Nullable
        public TrueHdSampleRechunker trueHdSampleRechunker;
        public int type;
        public float whitePointChromaticityX;
        public float whitePointChromaticityY;
        public int width;

        private static android.util.Pair<java.lang.String, java.util.List<byte[]>> parseFourCcPrivate(com.google.android.exoplayer2.util.ParsableByteArray r9) throws com.google.android.exoplayer2.ParserException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:29:0x009e in {5, 20, 21, 23, 25, 28} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r0 = 16;
            r9.skipBytes(r0);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r0 = r9.readLittleEndianUnsignedInt();	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r2 = 1482049860; // 0x58564944 float:9.4244065E14 double:7.322299212E-315;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r4 = 0;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            if (r5 != 0) goto L_0x001a;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
        L_0x0011:
            r2 = new android.util.Pair;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r3 = "video/3gpp";	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r2.<init>(r3, r4);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            return r2;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
        L_0x001a:
            r2 = 826496599; // 0x31435657 float:2.8425313E-9 double:4.08343576E-315;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            if (r5 != 0) goto L_0x007f;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
        L_0x0021:
            r2 = r9.getPosition();	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r2 = r2 + 20;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r3 = r9.data;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r4 = r2;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r5 = r3.length;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r5 = r5 + -4;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            if (r4 >= r5) goto L_0x0072;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r5 = r3[r4];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            if (r5 != 0) goto L_0x006d;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r5 = r4 + 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r5 = r3[r5];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            if (r5 != 0) goto L_0x006d;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r5 = r4 + 2;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r5 = r3[r5];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r6 = 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            if (r5 != r6) goto L_0x006d;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r5 = r4 + 3;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r5 = r3[r5];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r6 = 15;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            if (r5 != r6) goto L_0x006d;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r5 = r3.length;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r5 = java.util.Arrays.copyOfRange(r3, r4, r5);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r6 = new android.util.Pair;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r7 = "video/wvc1";	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r8 = java.util.Collections.singletonList(r5);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r6.<init>(r7, r8);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            return r6;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r4 = r4 + 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            goto L_0x002b;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r4 = new com.google.android.exoplayer2.ParserException;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r5 = "Failed to find FourCC VC1 initialization data";	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r4.<init>(r5);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            throw r4;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x0093 }
            r0 = "MatroskaExtractor";
            r1 = "Unknown FourCC. Setting mimeType to video/x-unknown";
            com.google.android.exoplayer2.util.Log.m10w(r0, r1);
            r0 = new android.util.Pair;
            r1 = "video/x-unknown";
            r0.<init>(r1, r4);
            return r0;
        L_0x0093:
            r0 = move-exception;
            r1 = new com.google.android.exoplayer2.ParserException;
            r2 = "Error parsing FourCC private data";
            r1.<init>(r2);
            throw r1;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.mkv.MatroskaExtractor.Track.parseFourCcPrivate(com.google.android.exoplayer2.util.ParsableByteArray):android.util.Pair<java.lang.String, java.util.List<byte[]>>");
        }

        private static java.util.List<byte[]> parseVorbisCodecPrivate(byte[] r8) throws com.google.android.exoplayer2.ParserException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:31:0x00b1 in {7, 11, 19, 21, 23, 25, 27, 30} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r0 = 0;
            r1 = r8[r0];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r2 = 2;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            if (r1 != r2) goto L_0x009a;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
        L_0x0006:
            r1 = 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r3 = r0;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r4 = r8[r1];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r5 = -1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            if (r4 != r5) goto L_0x0017;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r3 = r3 + 255;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r1 = r1 + 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            goto L_0x0009;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r4 = r1 + 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r1 = r8[r1];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r3 = r3 + r1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r1 = r0;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r6 = r8[r4];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            if (r6 != r5) goto L_0x002e;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r1 = r1 + 255;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r4 = r4 + 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            goto L_0x0021;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r5 = r4 + 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r4 = r8[r4];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r1 = r1 + r4;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r4 = r8[r5];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r6 = 1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            if (r4 != r6) goto L_0x008e;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r4 = new byte[r3];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            java.lang.System.arraycopy(r8, r5, r4, r0, r3);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r5 = r5 + r3;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r6 = r8[r5];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r7 = 3;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            if (r6 != r7) goto L_0x0082;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r5 = r5 + r1;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r6 = r8[r5];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r7 = 5;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            if (r6 != r7) goto L_0x0076;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r6 = r8.length;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r6 = r6 - r5;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r6 = new byte[r6];	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r7 = r8.length;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r7 = r7 - r5;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            java.lang.System.arraycopy(r8, r5, r6, r0, r7);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r0 = new java.util.ArrayList;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r0.<init>(r2);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r0.add(r4);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r0.add(r6);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            return r0;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r0 = new com.google.android.exoplayer2.ParserException;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r2 = "Error parsing vorbis codec private";	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r0.<init>(r2);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            throw r0;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r0 = new com.google.android.exoplayer2.ParserException;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r2 = "Error parsing vorbis codec private";	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r0.<init>(r2);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            throw r0;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r0 = new com.google.android.exoplayer2.ParserException;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r2 = "Error parsing vorbis codec private";	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r0.<init>(r2);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            throw r0;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r0 = new com.google.android.exoplayer2.ParserException;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r1 = "Error parsing vorbis codec private";	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            r0.<init>(r1);	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
            throw r0;	 Catch:{ ArrayIndexOutOfBoundsException -> 0x00a6 }
        L_0x00a6:
            r0 = move-exception;
            r1 = new com.google.android.exoplayer2.ParserException;
            r2 = "Error parsing vorbis codec private";
            r1.<init>(r2);
            throw r1;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.mkv.MatroskaExtractor.Track.parseVorbisCodecPrivate(byte[]):java.util.List<byte[]>");
        }

        private Track() {
            this.width = -1;
            this.height = -1;
            this.displayWidth = -1;
            this.displayHeight = -1;
            this.displayUnit = 0;
            this.projectionData = null;
            this.stereoMode = -1;
            this.hasColorInfo = false;
            this.colorSpace = -1;
            this.colorTransfer = -1;
            this.colorRange = -1;
            this.maxContentLuminance = 1000;
            this.maxFrameAverageLuminance = 200;
            this.primaryRChromaticityX = -1.0f;
            this.primaryRChromaticityY = -1.0f;
            this.primaryGChromaticityX = -1.0f;
            this.primaryGChromaticityY = -1.0f;
            this.primaryBChromaticityX = -1.0f;
            this.primaryBChromaticityY = -1.0f;
            this.whitePointChromaticityX = -1.0f;
            this.whitePointChromaticityY = -1.0f;
            this.maxMasteringLuminance = -1.0f;
            this.minMasteringLuminance = -1.0f;
            this.channelCount = 1;
            this.audioBitDepth = -1;
            this.sampleRate = 8000;
            this.codecDelayNs = 0;
            this.seekPreRollNs = 0;
            this.flagDefault = true;
            this.language = "eng";
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void initializeOutput(com.google.android.exoplayer2.extractor.ExtractorOutput r29, int r30) throws com.google.android.exoplayer2.ParserException {
            /*
            r28 = this;
            r0 = r28;
            r1 = -1;
            r2 = -1;
            r3 = 0;
            r4 = r0.codecId;
            r5 = r4.hashCode();
            r6 = 4;
            r7 = 1;
            r8 = 8;
            r9 = 0;
            r10 = 3;
            r11 = 2;
            r12 = -1;
            switch(r5) {
                case -2095576542: goto L_0x0159;
                case -2095575984: goto L_0x014f;
                case -1985379776: goto L_0x0144;
                case -1784763192: goto L_0x0139;
                case -1730367663: goto L_0x012e;
                case -1482641358: goto L_0x0123;
                case -1482641357: goto L_0x0118;
                case -1373388978: goto L_0x010d;
                case -933872740: goto L_0x0102;
                case -538363189: goto L_0x00f7;
                case -538363109: goto L_0x00ec;
                case -425012669: goto L_0x00e0;
                case -356037306: goto L_0x00d4;
                case 62923557: goto L_0x00c8;
                case 62923603: goto L_0x00bc;
                case 62927045: goto L_0x00b0;
                case 82338133: goto L_0x00a5;
                case 82338134: goto L_0x009a;
                case 99146302: goto L_0x008e;
                case 444813526: goto L_0x0082;
                case 542569478: goto L_0x0076;
                case 725957860: goto L_0x006a;
                case 738597099: goto L_0x005e;
                case 855502857: goto L_0x0053;
                case 1422270023: goto L_0x0047;
                case 1809237540: goto L_0x003c;
                case 1950749482: goto L_0x0030;
                case 1950789798: goto L_0x0024;
                case 1951062397: goto L_0x0018;
                default: goto L_0x0016;
            };
        L_0x0016:
            goto L_0x0163;
        L_0x0018:
            r5 = "A_OPUS";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x0020:
            r4 = 11;
            goto L_0x0164;
        L_0x0024:
            r5 = "A_FLAC";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x002c:
            r4 = 21;
            goto L_0x0164;
        L_0x0030:
            r5 = "A_EAC3";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x0038:
            r4 = 16;
            goto L_0x0164;
        L_0x003c:
            r5 = "V_MPEG2";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x0044:
            r4 = 2;
            goto L_0x0164;
        L_0x0047:
            r5 = "S_TEXT/UTF8";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x004f:
            r4 = 24;
            goto L_0x0164;
        L_0x0053:
            r5 = "V_MPEGH/ISO/HEVC";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x005b:
            r4 = 7;
            goto L_0x0164;
        L_0x005e:
            r5 = "S_TEXT/ASS";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x0066:
            r4 = 25;
            goto L_0x0164;
        L_0x006a:
            r5 = "A_PCM/INT/LIT";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x0072:
            r4 = 23;
            goto L_0x0164;
        L_0x0076:
            r5 = "A_DTS/EXPRESS";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x007e:
            r4 = 19;
            goto L_0x0164;
        L_0x0082:
            r5 = "V_THEORA";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x008a:
            r4 = 9;
            goto L_0x0164;
        L_0x008e:
            r5 = "S_HDMV/PGS";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x0096:
            r4 = 27;
            goto L_0x0164;
        L_0x009a:
            r5 = "V_VP9";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x00a2:
            r4 = 1;
            goto L_0x0164;
        L_0x00a5:
            r5 = "V_VP8";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x00ad:
            r4 = 0;
            goto L_0x0164;
        L_0x00b0:
            r5 = "A_DTS";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x00b8:
            r4 = 18;
            goto L_0x0164;
        L_0x00bc:
            r5 = "A_AC3";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x00c4:
            r4 = 15;
            goto L_0x0164;
        L_0x00c8:
            r5 = "A_AAC";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x00d0:
            r4 = 12;
            goto L_0x0164;
        L_0x00d4:
            r5 = "A_DTS/LOSSLESS";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x00dc:
            r4 = 20;
            goto L_0x0164;
        L_0x00e0:
            r5 = "S_VOBSUB";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x00e8:
            r4 = 26;
            goto L_0x0164;
        L_0x00ec:
            r5 = "V_MPEG4/ISO/AVC";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x00f4:
            r4 = 6;
            goto L_0x0164;
        L_0x00f7:
            r5 = "V_MPEG4/ISO/ASP";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x00ff:
            r4 = 4;
            goto L_0x0164;
        L_0x0102:
            r5 = "S_DVBSUB";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x010a:
            r4 = 28;
            goto L_0x0164;
        L_0x010d:
            r5 = "V_MS/VFW/FOURCC";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x0115:
            r4 = 8;
            goto L_0x0164;
        L_0x0118:
            r5 = "A_MPEG/L3";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x0120:
            r4 = 14;
            goto L_0x0164;
        L_0x0123:
            r5 = "A_MPEG/L2";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x012b:
            r4 = 13;
            goto L_0x0164;
        L_0x012e:
            r5 = "A_VORBIS";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x0136:
            r4 = 10;
            goto L_0x0164;
        L_0x0139:
            r5 = "A_TRUEHD";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x0141:
            r4 = 17;
            goto L_0x0164;
        L_0x0144:
            r5 = "A_MS/ACM";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x014c:
            r4 = 22;
            goto L_0x0164;
        L_0x014f:
            r5 = "V_MPEG4/ISO/SP";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x0157:
            r4 = 3;
            goto L_0x0164;
        L_0x0159:
            r5 = "V_MPEG4/ISO/AP";
            r4 = r4.equals(r5);
            if (r4 == 0) goto L_0x0016;
        L_0x0161:
            r4 = 5;
            goto L_0x0164;
        L_0x0163:
            r4 = -1;
        L_0x0164:
            switch(r4) {
                case 0: goto L_0x0312;
                case 1: goto L_0x030e;
                case 2: goto L_0x030a;
                case 3: goto L_0x02fb;
                case 4: goto L_0x02fb;
                case 5: goto L_0x02fb;
                case 6: goto L_0x02e6;
                case 7: goto L_0x02d1;
                case 8: goto L_0x02bb;
                case 9: goto L_0x02b7;
                case 10: goto L_0x02ac;
                case 11: goto L_0x0268;
                case 12: goto L_0x025e;
                case 13: goto L_0x0258;
                case 14: goto L_0x0252;
                case 15: goto L_0x024e;
                case 16: goto L_0x024a;
                case 17: goto L_0x023f;
                case 18: goto L_0x023b;
                case 19: goto L_0x023b;
                case 20: goto L_0x0237;
                case 21: goto L_0x022d;
                case 22: goto L_0x01d5;
                case 23: goto L_0x01a4;
                case 24: goto L_0x01a0;
                case 25: goto L_0x019b;
                case 26: goto L_0x0191;
                case 27: goto L_0x018d;
                case 28: goto L_0x0171;
                default: goto L_0x0167;
            };
        L_0x0167:
            r9 = r29;
            r4 = new com.google.android.exoplayer2.ParserException;
            r5 = "Unrecognized codec identifier.";
            r4.<init>(r5);
            throw r4;
        L_0x0171:
            r4 = "application/dvbsubs";
            r5 = new byte[r6];
            r6 = r0.codecPrivate;
            r8 = r6[r9];
            r5[r9] = r8;
            r8 = r6[r7];
            r5[r7] = r8;
            r7 = r6[r11];
            r5[r11] = r7;
            r6 = r6[r10];
            r5[r10] = r6;
            r3 = java.util.Collections.singletonList(r5);
            goto L_0x0316;
        L_0x018d:
            r4 = "application/pgs";
            goto L_0x0316;
        L_0x0191:
            r4 = "application/vobsub";
            r5 = r0.codecPrivate;
            r3 = java.util.Collections.singletonList(r5);
            goto L_0x0316;
        L_0x019b:
            r4 = "text/x-ssa";
            goto L_0x0316;
        L_0x01a0:
            r4 = "application/x-subrip";
            goto L_0x0316;
        L_0x01a4:
            r4 = "audio/raw";
            r5 = r0.audioBitDepth;
            r2 = com.google.android.exoplayer2.util.Util.getPcmEncoding(r5);
            if (r2 != 0) goto L_0x01d3;
        L_0x01ae:
            r2 = -1;
            r4 = "audio/x-unknown";
            r5 = "MatroskaExtractor";
            r6 = new java.lang.StringBuilder;
            r6.<init>();
            r7 = "Unsupported PCM bit depth: ";
            r6.append(r7);
            r7 = r0.audioBitDepth;
            r6.append(r7);
            r7 = ". Setting mimeType to ";
            r6.append(r7);
            r6.append(r4);
            r6 = r6.toString();
            com.google.android.exoplayer2.util.Log.m10w(r5, r6);
            goto L_0x0316;
        L_0x01d3:
            goto L_0x0316;
        L_0x01d5:
            r4 = "audio/raw";
            r5 = new com.google.android.exoplayer2.util.ParsableByteArray;
            r6 = r0.codecPrivate;
            r5.<init>(r6);
            r5 = parseMsAcmCodecPrivate(r5);
            if (r5 == 0) goto L_0x0213;
        L_0x01e4:
            r5 = r0.audioBitDepth;
            r2 = com.google.android.exoplayer2.util.Util.getPcmEncoding(r5);
            if (r2 != 0) goto L_0x0211;
        L_0x01ec:
            r2 = -1;
            r4 = "audio/x-unknown";
            r5 = "MatroskaExtractor";
            r6 = new java.lang.StringBuilder;
            r6.<init>();
            r7 = "Unsupported PCM bit depth: ";
            r6.append(r7);
            r7 = r0.audioBitDepth;
            r6.append(r7);
            r7 = ". Setting mimeType to ";
            r6.append(r7);
            r6.append(r4);
            r6 = r6.toString();
            com.google.android.exoplayer2.util.Log.m10w(r5, r6);
            goto L_0x0316;
        L_0x0211:
            goto L_0x0316;
        L_0x0213:
            r4 = "audio/x-unknown";
            r5 = "MatroskaExtractor";
            r6 = new java.lang.StringBuilder;
            r6.<init>();
            r7 = "Non-PCM MS/ACM is unsupported. Setting mimeType to ";
            r6.append(r7);
            r6.append(r4);
            r6 = r6.toString();
            com.google.android.exoplayer2.util.Log.m10w(r5, r6);
            goto L_0x0316;
        L_0x022d:
            r4 = "audio/flac";
            r5 = r0.codecPrivate;
            r3 = java.util.Collections.singletonList(r5);
            goto L_0x0316;
        L_0x0237:
            r4 = "audio/vnd.dts.hd";
            goto L_0x0316;
        L_0x023b:
            r4 = "audio/vnd.dts";
            goto L_0x0316;
        L_0x023f:
            r4 = "audio/true-hd";
            r5 = new com.google.android.exoplayer2.extractor.mkv.MatroskaExtractor$TrueHdSampleRechunker;
            r5.<init>();
            r0.trueHdSampleRechunker = r5;
            goto L_0x0316;
        L_0x024a:
            r4 = "audio/eac3";
            goto L_0x0316;
        L_0x024e:
            r4 = "audio/ac3";
            goto L_0x0316;
        L_0x0252:
            r4 = "audio/mpeg";
            r1 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
            goto L_0x0316;
        L_0x0258:
            r4 = "audio/mpeg-L2";
            r1 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
            goto L_0x0316;
        L_0x025e:
            r4 = "audio/mp4a-latm";
            r5 = r0.codecPrivate;
            r3 = java.util.Collections.singletonList(r5);
            goto L_0x0316;
        L_0x0268:
            r4 = "audio/opus";
            r1 = 5760; // 0x1680 float:8.071E-42 double:2.846E-320;
            r5 = new java.util.ArrayList;
            r5.<init>(r10);
            r3 = r5;
            r5 = r0.codecPrivate;
            r3.add(r5);
            r5 = java.nio.ByteBuffer.allocate(r8);
            r6 = java.nio.ByteOrder.nativeOrder();
            r5 = r5.order(r6);
            r6 = r0.codecDelayNs;
            r5 = r5.putLong(r6);
            r5 = r5.array();
            r3.add(r5);
            r5 = java.nio.ByteBuffer.allocate(r8);
            r6 = java.nio.ByteOrder.nativeOrder();
            r5 = r5.order(r6);
            r6 = r0.seekPreRollNs;
            r5 = r5.putLong(r6);
            r5 = r5.array();
            r3.add(r5);
            goto L_0x0316;
        L_0x02ac:
            r4 = "audio/vorbis";
            r1 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
            r5 = r0.codecPrivate;
            r3 = parseVorbisCodecPrivate(r5);
            goto L_0x0316;
        L_0x02b7:
            r4 = "video/x-unknown";
            goto L_0x0316;
        L_0x02bb:
            r4 = new com.google.android.exoplayer2.util.ParsableByteArray;
            r5 = r0.codecPrivate;
            r4.<init>(r5);
            r4 = parseFourCcPrivate(r4);
            r5 = r4.first;
            r5 = (java.lang.String) r5;
            r6 = r4.second;
            r3 = r6;
            r3 = (java.util.List) r3;
            r4 = r5;
            goto L_0x0316;
        L_0x02d1:
            r4 = "video/hevc";
            r5 = new com.google.android.exoplayer2.util.ParsableByteArray;
            r6 = r0.codecPrivate;
            r5.<init>(r6);
            r5 = com.google.android.exoplayer2.video.HevcConfig.parse(r5);
            r3 = r5.initializationData;
            r6 = r5.nalUnitLengthFieldLength;
            r0.nalUnitLengthFieldLength = r6;
            goto L_0x0316;
        L_0x02e6:
            r4 = "video/avc";
            r5 = new com.google.android.exoplayer2.util.ParsableByteArray;
            r6 = r0.codecPrivate;
            r5.<init>(r6);
            r5 = com.google.android.exoplayer2.video.AvcConfig.parse(r5);
            r3 = r5.initializationData;
            r6 = r5.nalUnitLengthFieldLength;
            r0.nalUnitLengthFieldLength = r6;
            goto L_0x0316;
        L_0x02fb:
            r4 = "video/mp4v-es";
            r5 = r0.codecPrivate;
            if (r5 != 0) goto L_0x0304;
        L_0x0302:
            r5 = 0;
            goto L_0x0308;
        L_0x0304:
            r5 = java.util.Collections.singletonList(r5);
        L_0x0308:
            r3 = r5;
            goto L_0x0316;
        L_0x030a:
            r4 = "video/mpeg2";
            goto L_0x0316;
        L_0x030e:
            r4 = "video/x-vnd.on2.vp9";
            goto L_0x0316;
        L_0x0312:
            r4 = "video/x-vnd.on2.vp8";
        L_0x0316:
            r5 = 0;
            r6 = r0.flagDefault;
            r5 = r5 | r6;
            r6 = r0.flagForced;
            if (r6 == 0) goto L_0x0320;
        L_0x031e:
            r9 = 2;
        L_0x0320:
            r5 = r5 | r9;
            r6 = com.google.android.exoplayer2.util.MimeTypes.isAudio(r4);
            if (r6 == 0) goto L_0x034e;
        L_0x0327:
            r6 = 1;
            r13 = java.lang.Integer.toString(r30);
            r15 = 0;
            r16 = -1;
            r7 = r0.channelCount;
            r8 = r0.sampleRate;
            r9 = r0.drmInitData;
            r10 = r0.language;
            r14 = r4;
            r17 = r1;
            r18 = r7;
            r19 = r8;
            r20 = r2;
            r21 = r3;
            r22 = r9;
            r23 = r5;
            r24 = r10;
            r7 = com.google.android.exoplayer2.Format.createAudioSampleFormat(r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24);
            goto L_0x0490;
        L_0x034e:
            r6 = com.google.android.exoplayer2.util.MimeTypes.isVideo(r4);
            if (r6 == 0) goto L_0x0400;
        L_0x0354:
            r6 = 2;
            r7 = r0.displayUnit;
            if (r7 != 0) goto L_0x036c;
        L_0x0359:
            r7 = r0.displayWidth;
            if (r7 != r12) goto L_0x0360;
        L_0x035d:
            r7 = r0.width;
        L_0x0360:
            r0.displayWidth = r7;
            r7 = r0.displayHeight;
            if (r7 != r12) goto L_0x0369;
        L_0x0366:
            r7 = r0.height;
        L_0x0369:
            r0.displayHeight = r7;
            goto L_0x036d;
        L_0x036d:
            r7 = -1082130432; // 0xffffffffbf800000 float:-1.0 double:NaN;
            r8 = r0.displayWidth;
            if (r8 == r12) goto L_0x0384;
        L_0x0373:
            r9 = r0.displayHeight;
            if (r9 == r12) goto L_0x0384;
        L_0x0377:
            r10 = r0.height;
            r10 = r10 * r8;
            r8 = (float) r10;
            r10 = r0.width;
            r10 = r10 * r9;
            r9 = (float) r10;
            r8 = r8 / r9;
            r7 = r8;
            goto L_0x0385;
        L_0x0385:
            r8 = 0;
            r9 = r0.hasColorInfo;
            if (r9 == 0) goto L_0x039b;
        L_0x038a:
            r9 = r28.getHdrStaticInfo();
            r10 = new com.google.android.exoplayer2.video.ColorInfo;
            r11 = r0.colorSpace;
            r12 = r0.colorRange;
            r13 = r0.colorTransfer;
            r10.<init>(r11, r12, r13, r9);
            r8 = r10;
            goto L_0x039c;
        L_0x039c:
            r9 = -1;
            r10 = "htc_video_rotA-000";
            r11 = r0.name;
            r10 = r10.equals(r11);
            if (r10 == 0) goto L_0x03a9;
        L_0x03a7:
            r9 = 0;
            goto L_0x03d1;
        L_0x03a9:
            r10 = "htc_video_rotA-090";
            r11 = r0.name;
            r10 = r10.equals(r11);
            if (r10 == 0) goto L_0x03b6;
        L_0x03b3:
            r9 = 90;
            goto L_0x03d1;
        L_0x03b6:
            r10 = "htc_video_rotA-180";
            r11 = r0.name;
            r10 = r10.equals(r11);
            if (r10 == 0) goto L_0x03c3;
        L_0x03c0:
            r9 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
            goto L_0x03d1;
        L_0x03c3:
            r10 = "htc_video_rotA-270";
            r11 = r0.name;
            r10 = r10.equals(r11);
            if (r10 == 0) goto L_0x03d0;
        L_0x03cd:
            r9 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
            goto L_0x03d1;
            r13 = java.lang.Integer.toString(r30);
            r16 = -1;
            r10 = r0.width;
            r11 = r0.height;
            r20 = -1082130432; // 0xffffffffbf800000 float:-1.0 double:NaN;
            r12 = r0.projectionData;
            r14 = r0.stereoMode;
            r15 = r0.drmInitData;
            r25 = r14;
            r14 = r4;
            r27 = r15;
            r15 = 0;
            r17 = r1;
            r18 = r10;
            r19 = r11;
            r21 = r3;
            r22 = r9;
            r23 = r7;
            r24 = r12;
            r26 = r8;
            r7 = com.google.android.exoplayer2.Format.createVideoSampleFormat(r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27);
            goto L_0x0490;
        L_0x0400:
            r6 = "application/x-subrip";
            r6 = r6.equals(r4);
            if (r6 == 0) goto L_0x0417;
        L_0x0408:
            r6 = 3;
            r7 = java.lang.Integer.toString(r30);
            r8 = r0.language;
            r9 = r0.drmInitData;
            r7 = com.google.android.exoplayer2.Format.createTextSampleFormat(r7, r4, r5, r8, r9);
            goto L_0x0490;
        L_0x0417:
            r6 = "text/x-ssa";
            r6 = r6.equals(r4);
            if (r6 == 0) goto L_0x0453;
        L_0x0420:
            r6 = 3;
            r7 = new java.util.ArrayList;
            r7.<init>(r11);
            r3 = r7;
            r7 = com.google.android.exoplayer2.extractor.mkv.MatroskaExtractor.SSA_DIALOGUE_FORMAT;
            r3.add(r7);
            r7 = r0.codecPrivate;
            r3.add(r7);
            r13 = java.lang.Integer.toString(r30);
            r15 = 0;
            r16 = -1;
            r7 = r0.language;
            r19 = -1;
            r8 = r0.drmInitData;
            r21 = 9223372036854775807; // 0x7fffffffffffffff float:NaN double:NaN;
            r14 = r4;
            r17 = r5;
            r18 = r7;
            r20 = r8;
            r23 = r3;
            r7 = com.google.android.exoplayer2.Format.createTextSampleFormat(r13, r14, r15, r16, r17, r18, r19, r20, r21, r23);
            goto L_0x0490;
        L_0x0453:
            r6 = "application/vobsub";
            r6 = r6.equals(r4);
            if (r6 != 0) goto L_0x0475;
        L_0x045b:
            r6 = "application/pgs";
            r6 = r6.equals(r4);
            if (r6 != 0) goto L_0x0474;
        L_0x0463:
            r6 = "application/dvbsubs";
            r6 = r6.equals(r4);
            if (r6 == 0) goto L_0x046c;
        L_0x046b:
            goto L_0x0476;
        L_0x046c:
            r6 = new com.google.android.exoplayer2.ParserException;
            r7 = "Unexpected MIME type.";
            r6.<init>(r7);
            throw r6;
        L_0x0474:
            goto L_0x0476;
        L_0x0476:
            r6 = 3;
            r13 = java.lang.Integer.toString(r30);
            r15 = 0;
            r16 = -1;
            r7 = r0.language;
            r8 = r0.drmInitData;
            r14 = r4;
            r17 = r5;
            r18 = r3;
            r19 = r7;
            r20 = r8;
            r7 = com.google.android.exoplayer2.Format.createImageSampleFormat(r13, r14, r15, r16, r17, r18, r19, r20);
        L_0x0490:
            r8 = r0.number;
            r9 = r29;
            r8 = r9.track(r8, r6);
            r0.output = r8;
            r8 = r0.output;
            r8.format(r7);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.mkv.MatroskaExtractor.Track.initializeOutput(com.google.android.exoplayer2.extractor.ExtractorOutput, int):void");
        }

        public void outputPendingSampleMetadata() {
            TrueHdSampleRechunker trueHdSampleRechunker = this.trueHdSampleRechunker;
            if (trueHdSampleRechunker != null) {
                trueHdSampleRechunker.outputPendingSampleMetadata(this);
            }
        }

        public void reset() {
            TrueHdSampleRechunker trueHdSampleRechunker = this.trueHdSampleRechunker;
            if (trueHdSampleRechunker != null) {
                trueHdSampleRechunker.reset();
            }
        }

        private byte[] getHdrStaticInfo() {
            if (!(this.primaryRChromaticityX == -1.0f || this.primaryRChromaticityY == -1.0f || this.primaryGChromaticityX == -1.0f || this.primaryGChromaticityY == -1.0f || this.primaryBChromaticityX == -1.0f || this.primaryBChromaticityY == -1.0f || this.whitePointChromaticityX == -1.0f || this.whitePointChromaticityY == -1.0f || this.maxMasteringLuminance == -1.0f)) {
                if (this.minMasteringLuminance != -1.0f) {
                    byte[] hdrStaticInfoData = new byte[25];
                    ByteBuffer hdrStaticInfo = ByteBuffer.wrap(hdrStaticInfoData);
                    hdrStaticInfo.put((byte) 0);
                    hdrStaticInfo.putShort((short) ((int) ((this.primaryRChromaticityX * 50000.0f) + 0.5f)));
                    hdrStaticInfo.putShort((short) ((int) ((this.primaryRChromaticityY * 50000.0f) + 0.5f)));
                    hdrStaticInfo.putShort((short) ((int) ((this.primaryGChromaticityX * 50000.0f) + 0.5f)));
                    hdrStaticInfo.putShort((short) ((int) ((this.primaryGChromaticityY * 50000.0f) + 0.5f)));
                    hdrStaticInfo.putShort((short) ((int) ((this.primaryBChromaticityX * 50000.0f) + 0.5f)));
                    hdrStaticInfo.putShort((short) ((int) ((this.primaryBChromaticityY * 50000.0f) + 0.5f)));
                    hdrStaticInfo.putShort((short) ((int) ((this.whitePointChromaticityX * 50000.0f) + 0.5f)));
                    hdrStaticInfo.putShort((short) ((int) ((this.whitePointChromaticityY * 50000.0f) + 0.5f)));
                    hdrStaticInfo.putShort((short) ((int) (this.maxMasteringLuminance + 0.5f)));
                    hdrStaticInfo.putShort((short) ((int) (this.minMasteringLuminance + 0.5f)));
                    hdrStaticInfo.putShort((short) this.maxContentLuminance);
                    hdrStaticInfo.putShort((short) this.maxFrameAverageLuminance);
                    return hdrStaticInfoData;
                }
            }
            return null;
        }

        private static boolean parseMsAcmCodecPrivate(ParsableByteArray buffer) throws ParserException {
            try {
                int formatTag = buffer.readLittleEndianUnsignedShort();
                boolean z = true;
                if (formatTag == 1) {
                    return true;
                }
                if (formatTag != MatroskaExtractor.WAVE_FORMAT_EXTENSIBLE) {
                    return false;
                }
                buffer.setPosition(24);
                if (buffer.readLong() == MatroskaExtractor.WAVE_SUBFORMAT_PCM.getMostSignificantBits()) {
                    if (buffer.readLong() == MatroskaExtractor.WAVE_SUBFORMAT_PCM.getLeastSignificantBits()) {
                        return z;
                    }
                }
                z = false;
                return z;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ParserException("Error parsing MS/ACM codec private");
            }
        }
    }

    private static final class TrueHdSampleRechunker {
        private int blockFlags;
        private int chunkSize;
        private boolean foundSyncframe;
        private int sampleCount;
        private final byte[] syncframePrefix = new byte[10];
        private long timeUs;

        public void reset() {
            this.foundSyncframe = false;
        }

        public void startSample(ExtractorInput input, int blockFlags, int size) throws IOException, InterruptedException {
            if (!this.foundSyncframe) {
                input.peekFully(this.syncframePrefix, 0, 10);
                input.resetPeekPosition();
                if (Ac3Util.parseTrueHdSyncframeAudioSampleCount(this.syncframePrefix) != 0) {
                    this.foundSyncframe = true;
                    this.sampleCount = 0;
                } else {
                    return;
                }
            }
            if (this.sampleCount == 0) {
                this.blockFlags = blockFlags;
                this.chunkSize = 0;
            }
            this.chunkSize += size;
        }

        public void sampleMetadata(Track track, long timeUs) {
            if (this.foundSyncframe) {
                int i = this.sampleCount;
                this.sampleCount = i + 1;
                if (i == 0) {
                    this.timeUs = timeUs;
                }
                if (this.sampleCount >= 16) {
                    track.output.sampleMetadata(this.timeUs, this.blockFlags, this.chunkSize, 0, track.cryptoData);
                    this.sampleCount = 0;
                }
            }
        }

        public void outputPendingSampleMetadata(Track track) {
            if (this.foundSyncframe && this.sampleCount > 0) {
                track.output.sampleMetadata(this.timeUs, this.blockFlags, this.chunkSize, 0, track.cryptoData);
                this.sampleCount = 0;
            }
        }
    }

    private final class InnerEbmlReaderOutput implements EbmlReaderOutput {
        private InnerEbmlReaderOutput() {
        }

        public int getElementType(int id) {
            switch (id) {
                case MatroskaExtractor.ID_TRACK_TYPE /*131*/:
                case MatroskaExtractor.ID_FLAG_DEFAULT /*136*/:
                case MatroskaExtractor.ID_BLOCK_DURATION /*155*/:
                case MatroskaExtractor.ID_CHANNELS /*159*/:
                case MatroskaExtractor.ID_PIXEL_WIDTH /*176*/:
                case MatroskaExtractor.ID_CUE_TIME /*179*/:
                case MatroskaExtractor.ID_PIXEL_HEIGHT /*186*/:
                case MatroskaExtractor.ID_TRACK_NUMBER /*215*/:
                case MatroskaExtractor.ID_TIME_CODE /*231*/:
                case MatroskaExtractor.ID_CUE_CLUSTER_POSITION /*241*/:
                case MatroskaExtractor.ID_REFERENCE_BLOCK /*251*/:
                case MatroskaExtractor.ID_CONTENT_COMPRESSION_ALGORITHM /*16980*/:
                case MatroskaExtractor.ID_DOC_TYPE_READ_VERSION /*17029*/:
                case MatroskaExtractor.ID_EBML_READ_VERSION /*17143*/:
                case MatroskaExtractor.ID_CONTENT_ENCRYPTION_ALGORITHM /*18401*/:
                case MatroskaExtractor.ID_CONTENT_ENCRYPTION_AES_SETTINGS_CIPHER_MODE /*18408*/:
                case MatroskaExtractor.ID_CONTENT_ENCODING_ORDER /*20529*/:
                case MatroskaExtractor.ID_CONTENT_ENCODING_SCOPE /*20530*/:
                case MatroskaExtractor.ID_SEEK_POSITION /*21420*/:
                case MatroskaExtractor.ID_STEREO_MODE /*21432*/:
                case MatroskaExtractor.ID_DISPLAY_WIDTH /*21680*/:
                case MatroskaExtractor.ID_DISPLAY_UNIT /*21682*/:
                case MatroskaExtractor.ID_DISPLAY_HEIGHT /*21690*/:
                case MatroskaExtractor.ID_FLAG_FORCED /*21930*/:
                case MatroskaExtractor.ID_COLOUR_RANGE /*21945*/:
                case MatroskaExtractor.ID_COLOUR_TRANSFER /*21946*/:
                case MatroskaExtractor.ID_COLOUR_PRIMARIES /*21947*/:
                case MatroskaExtractor.ID_MAX_CLL /*21948*/:
                case MatroskaExtractor.ID_MAX_FALL /*21949*/:
                case MatroskaExtractor.ID_CODEC_DELAY /*22186*/:
                case MatroskaExtractor.ID_SEEK_PRE_ROLL /*22203*/:
                case MatroskaExtractor.ID_AUDIO_BIT_DEPTH /*25188*/:
                case MatroskaExtractor.ID_DEFAULT_DURATION /*2352003*/:
                case MatroskaExtractor.ID_TIMECODE_SCALE /*2807729*/:
                    return 2;
                case 134:
                case MatroskaExtractor.ID_DOC_TYPE /*17026*/:
                case MatroskaExtractor.ID_NAME /*21358*/:
                case MatroskaExtractor.ID_LANGUAGE /*2274716*/:
                    return 3;
                case MatroskaExtractor.ID_BLOCK_GROUP /*160*/:
                case MatroskaExtractor.ID_TRACK_ENTRY /*174*/:
                case MatroskaExtractor.ID_CUE_TRACK_POSITIONS /*183*/:
                case MatroskaExtractor.ID_CUE_POINT /*187*/:
                case 224:
                case MatroskaExtractor.ID_AUDIO /*225*/:
                case MatroskaExtractor.ID_CONTENT_ENCRYPTION_AES_SETTINGS /*18407*/:
                case MatroskaExtractor.ID_SEEK /*19899*/:
                case MatroskaExtractor.ID_CONTENT_COMPRESSION /*20532*/:
                case MatroskaExtractor.ID_CONTENT_ENCRYPTION /*20533*/:
                case MatroskaExtractor.ID_COLOUR /*21936*/:
                case MatroskaExtractor.ID_MASTERING_METADATA /*21968*/:
                case MatroskaExtractor.ID_CONTENT_ENCODING /*25152*/:
                case MatroskaExtractor.ID_CONTENT_ENCODINGS /*28032*/:
                case MatroskaExtractor.ID_PROJECTION /*30320*/:
                case MatroskaExtractor.ID_SEEK_HEAD /*290298740*/:
                case 357149030:
                case MatroskaExtractor.ID_TRACKS /*374648427*/:
                case MatroskaExtractor.ID_SEGMENT /*408125543*/:
                case MatroskaExtractor.ID_EBML /*440786851*/:
                case MatroskaExtractor.ID_CUES /*475249515*/:
                case MatroskaExtractor.ID_CLUSTER /*524531317*/:
                    return 1;
                case MatroskaExtractor.ID_BLOCK /*161*/:
                case MatroskaExtractor.ID_SIMPLE_BLOCK /*163*/:
                case MatroskaExtractor.ID_CONTENT_COMPRESSION_SETTINGS /*16981*/:
                case MatroskaExtractor.ID_CONTENT_ENCRYPTION_KEY_ID /*18402*/:
                case MatroskaExtractor.ID_SEEK_ID /*21419*/:
                case MatroskaExtractor.ID_CODEC_PRIVATE /*25506*/:
                case MatroskaExtractor.ID_PROJECTION_PRIVATE /*30322*/:
                    return 4;
                case MatroskaExtractor.ID_SAMPLING_FREQUENCY /*181*/:
                case MatroskaExtractor.ID_DURATION /*17545*/:
                case MatroskaExtractor.ID_PRIMARY_R_CHROMATICITY_X /*21969*/:
                case MatroskaExtractor.ID_PRIMARY_R_CHROMATICITY_Y /*21970*/:
                case MatroskaExtractor.ID_PRIMARY_G_CHROMATICITY_X /*21971*/:
                case MatroskaExtractor.ID_PRIMARY_G_CHROMATICITY_Y /*21972*/:
                case MatroskaExtractor.ID_PRIMARY_B_CHROMATICITY_X /*21973*/:
                case MatroskaExtractor.ID_PRIMARY_B_CHROMATICITY_Y /*21974*/:
                case MatroskaExtractor.ID_WHITE_POINT_CHROMATICITY_X /*21975*/:
                case MatroskaExtractor.ID_WHITE_POINT_CHROMATICITY_Y /*21976*/:
                case MatroskaExtractor.ID_LUMNINANCE_MAX /*21977*/:
                case MatroskaExtractor.ID_LUMNINANCE_MIN /*21978*/:
                    return 5;
                default:
                    return 0;
            }
        }

        public boolean isLevel1Element(int id) {
            if (!(id == 357149030 || id == MatroskaExtractor.ID_CLUSTER || id == MatroskaExtractor.ID_CUES)) {
                if (id != MatroskaExtractor.ID_TRACKS) {
                    return false;
                }
            }
            return true;
        }

        public void startMasterElement(int id, long contentPosition, long contentSize) throws ParserException {
            MatroskaExtractor.this.startMasterElement(id, contentPosition, contentSize);
        }

        public void endMasterElement(int id) throws ParserException {
            MatroskaExtractor.this.endMasterElement(id);
        }

        public void integerElement(int id, long value) throws ParserException {
            MatroskaExtractor.this.integerElement(id, value);
        }

        public void floatElement(int id, double value) throws ParserException {
            MatroskaExtractor.this.floatElement(id, value);
        }

        public void stringElement(int id, String value) throws ParserException {
            MatroskaExtractor.this.stringElement(id, value);
        }

        public void binaryElement(int id, int contentsSize, ExtractorInput input) throws IOException, InterruptedException {
            MatroskaExtractor.this.binaryElement(id, contentsSize, input);
        }
    }

    public MatroskaExtractor() {
        this(0);
    }

    public MatroskaExtractor(int flags) {
        this(new DefaultEbmlReader(), flags);
    }

    MatroskaExtractor(EbmlReader reader, int flags) {
        this.segmentContentPosition = -1;
        this.timecodeScale = C0555C.TIME_UNSET;
        this.durationTimecode = C0555C.TIME_UNSET;
        this.durationUs = C0555C.TIME_UNSET;
        this.cuesContentPosition = -1;
        this.seekPositionAfterBuildingCues = -1;
        this.clusterTimecodeUs = C0555C.TIME_UNSET;
        this.reader = reader;
        this.reader.init(new InnerEbmlReaderOutput());
        this.seekForCuesEnabled = (flags & 1) == 0;
        this.varintReader = new VarintReader();
        this.tracks = new SparseArray();
        this.scratch = new ParsableByteArray(4);
        this.vorbisNumPageSamples = new ParsableByteArray(ByteBuffer.allocate(4).putInt(-1).array());
        this.seekEntryIdBytes = new ParsableByteArray(4);
        this.nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
        this.nalLength = new ParsableByteArray(4);
        this.sampleStrippedBytes = new ParsableByteArray();
        this.subtitleSample = new ParsableByteArray();
        this.encryptionInitializationVector = new ParsableByteArray(8);
        this.encryptionSubsampleData = new ParsableByteArray();
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return new Sniffer().sniff(input);
    }

    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
    }

    public void seek(long position, long timeUs) {
        this.clusterTimecodeUs = C0555C.TIME_UNSET;
        this.blockState = 0;
        this.reader.reset();
        this.varintReader.reset();
        resetSample();
        for (int i = 0; i < this.tracks.size(); i++) {
            ((Track) this.tracks.valueAt(i)).reset();
        }
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        this.sampleRead = false;
        boolean continueReading = true;
        while (continueReading && !this.sampleRead) {
            continueReading = this.reader.read(input);
            if (continueReading && maybeSeekForCues(seekPosition, input.getPosition())) {
                return 1;
            }
        }
        if (continueReading) {
            return 0;
        }
        for (int i = 0; i < this.tracks.size(); i++) {
            ((Track) this.tracks.valueAt(i)).outputPendingSampleMetadata();
        }
        return -1;
    }

    void startMasterElement(int id, long contentPosition, long contentSize) throws ParserException {
        if (id == ID_BLOCK_GROUP) {
            this.sampleSeenReferenceBlock = false;
        } else if (id == ID_TRACK_ENTRY) {
            this.currentTrack = new Track();
        } else if (id == ID_CUE_POINT) {
            this.seenClusterPositionForCurrentCuePoint = false;
        } else if (id == ID_SEEK) {
            this.seekEntryId = -1;
            this.seekEntryPosition = -1;
        } else if (id == ID_CONTENT_ENCRYPTION) {
            this.currentTrack.hasContentEncryption = true;
        } else if (id == ID_MASTERING_METADATA) {
            this.currentTrack.hasColorInfo = true;
        } else if (id == ID_CONTENT_ENCODING) {
        } else {
            if (id == ID_SEGMENT) {
                long j = this.segmentContentPosition;
                if (j != -1) {
                    if (j != contentPosition) {
                        throw new ParserException("Multiple Segment elements not supported");
                    }
                }
                this.segmentContentPosition = contentPosition;
                this.segmentContentSize = contentSize;
            } else if (id == ID_CUES) {
                this.cueTimesUs = new LongArray();
                this.cueClusterPositions = new LongArray();
            } else if (id == ID_CLUSTER) {
                if (!this.sentSeekMap) {
                    if (!this.seekForCuesEnabled || this.cuesContentPosition == -1) {
                        this.extractorOutput.seekMap(new Unseekable(this.durationUs));
                        this.sentSeekMap = true;
                        return;
                    }
                    this.seekForCues = true;
                }
            }
        }
    }

    void endMasterElement(int id) throws ParserException {
        if (id != ID_BLOCK_GROUP) {
            if (id == ID_TRACK_ENTRY) {
                if (isCodecSupported(this.currentTrack.codecId)) {
                    Track track = this.currentTrack;
                    track.initializeOutput(this.extractorOutput, track.number);
                    this.tracks.put(this.currentTrack.number, this.currentTrack);
                }
                this.currentTrack = null;
            } else if (id == ID_SEEK) {
                int i = this.seekEntryId;
                if (i != -1) {
                    long j = this.seekEntryPosition;
                    if (j != -1) {
                        if (i == ID_CUES) {
                            this.cuesContentPosition = j;
                        }
                    }
                }
                throw new ParserException("Mandatory element SeekID or SeekPosition not found");
            } else if (id != ID_CONTENT_ENCODING) {
                if (id != ID_CONTENT_ENCODINGS) {
                    if (id == 357149030) {
                        if (this.timecodeScale == C0555C.TIME_UNSET) {
                            this.timecodeScale = 1000000;
                        }
                        long j2 = this.durationTimecode;
                        if (j2 != C0555C.TIME_UNSET) {
                            this.durationUs = scaleTimecodeToUs(j2);
                        }
                    } else if (id != ID_TRACKS) {
                        if (id == ID_CUES) {
                            if (!this.sentSeekMap) {
                                this.extractorOutput.seekMap(buildSeekMap());
                                this.sentSeekMap = true;
                            }
                        }
                    } else if (this.tracks.size() != 0) {
                        this.extractorOutput.endTracks();
                    } else {
                        throw new ParserException("No valid tracks were found");
                    }
                } else if (this.currentTrack.hasContentEncryption) {
                    if (this.currentTrack.sampleStrippedBytes != null) {
                        throw new ParserException("Combining encryption and compression is not supported");
                    }
                }
            } else if (this.currentTrack.hasContentEncryption) {
                if (this.currentTrack.cryptoData != null) {
                    this.currentTrack.drmInitData = new DrmInitData(new SchemeData(C0555C.UUID_NIL, MimeTypes.VIDEO_WEBM, this.currentTrack.cryptoData.encryptionKey));
                } else {
                    throw new ParserException("Encrypted Track found but ContentEncKeyID was not found");
                }
            }
        } else if (this.blockState == 2) {
            if (!this.sampleSeenReferenceBlock) {
                this.blockFlags |= 1;
            }
            commitSampleToOutput((Track) this.tracks.get(this.blockTrackNumber), this.blockTimeUs);
            this.blockState = 0;
        }
    }

    void integerElement(int id, long value) throws ParserException {
        boolean z = false;
        Track track;
        StringBuilder stringBuilder;
        switch (id) {
            case ID_TRACK_TYPE /*131*/:
                this.currentTrack.type = (int) value;
                return;
            case ID_FLAG_DEFAULT /*136*/:
                track = this.currentTrack;
                if (value == 1) {
                    z = true;
                }
                track.flagDefault = z;
                return;
            case ID_BLOCK_DURATION /*155*/:
                this.blockDurationUs = scaleTimecodeToUs(value);
                return;
            case ID_CHANNELS /*159*/:
                this.currentTrack.channelCount = (int) value;
                return;
            case ID_PIXEL_WIDTH /*176*/:
                this.currentTrack.width = (int) value;
                return;
            case ID_CUE_TIME /*179*/:
                this.cueTimesUs.add(scaleTimecodeToUs(value));
                return;
            case ID_PIXEL_HEIGHT /*186*/:
                this.currentTrack.height = (int) value;
                return;
            case ID_TRACK_NUMBER /*215*/:
                this.currentTrack.number = (int) value;
                return;
            case ID_TIME_CODE /*231*/:
                this.clusterTimecodeUs = scaleTimecodeToUs(value);
                return;
            case ID_CUE_CLUSTER_POSITION /*241*/:
                if (!this.seenClusterPositionForCurrentCuePoint) {
                    this.cueClusterPositions.add(value);
                    this.seenClusterPositionForCurrentCuePoint = true;
                    return;
                }
                return;
            case ID_REFERENCE_BLOCK /*251*/:
                this.sampleSeenReferenceBlock = true;
                return;
            case ID_CONTENT_COMPRESSION_ALGORITHM /*16980*/:
                if (value != 3) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("ContentCompAlgo ");
                    stringBuilder.append(value);
                    stringBuilder.append(" not supported");
                    throw new ParserException(stringBuilder.toString());
                }
                return;
            case ID_DOC_TYPE_READ_VERSION /*17029*/:
                if (value < 1 || value > 2) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("DocTypeReadVersion ");
                    stringBuilder.append(value);
                    stringBuilder.append(" not supported");
                    throw new ParserException(stringBuilder.toString());
                }
                return;
            case ID_EBML_READ_VERSION /*17143*/:
                if (value != 1) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("EBMLReadVersion ");
                    stringBuilder.append(value);
                    stringBuilder.append(" not supported");
                    throw new ParserException(stringBuilder.toString());
                }
                return;
            case ID_CONTENT_ENCRYPTION_ALGORITHM /*18401*/:
                if (value != 5) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("ContentEncAlgo ");
                    stringBuilder.append(value);
                    stringBuilder.append(" not supported");
                    throw new ParserException(stringBuilder.toString());
                }
                return;
            case ID_CONTENT_ENCRYPTION_AES_SETTINGS_CIPHER_MODE /*18408*/:
                if (value != 1) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("AESSettingsCipherMode ");
                    stringBuilder.append(value);
                    stringBuilder.append(" not supported");
                    throw new ParserException(stringBuilder.toString());
                }
                return;
            case ID_CONTENT_ENCODING_ORDER /*20529*/:
                if (value != 0) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("ContentEncodingOrder ");
                    stringBuilder.append(value);
                    stringBuilder.append(" not supported");
                    throw new ParserException(stringBuilder.toString());
                }
                return;
            case ID_CONTENT_ENCODING_SCOPE /*20530*/:
                if (value != 1) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("ContentEncodingScope ");
                    stringBuilder.append(value);
                    stringBuilder.append(" not supported");
                    throw new ParserException(stringBuilder.toString());
                }
                return;
            case ID_SEEK_POSITION /*21420*/:
                this.seekEntryPosition = this.segmentContentPosition + value;
                return;
            case ID_STEREO_MODE /*21432*/:
                int layout = (int) value;
                if (layout == 3) {
                    this.currentTrack.stereoMode = 1;
                    return;
                } else if (layout != 15) {
                    switch (layout) {
                        case 0:
                            this.currentTrack.stereoMode = 0;
                            return;
                        case 1:
                            this.currentTrack.stereoMode = 2;
                            return;
                        default:
                            return;
                    }
                } else {
                    this.currentTrack.stereoMode = 3;
                    return;
                }
            case ID_DISPLAY_WIDTH /*21680*/:
                this.currentTrack.displayWidth = (int) value;
                return;
            case ID_DISPLAY_UNIT /*21682*/:
                this.currentTrack.displayUnit = (int) value;
                return;
            case ID_DISPLAY_HEIGHT /*21690*/:
                this.currentTrack.displayHeight = (int) value;
                return;
            case ID_FLAG_FORCED /*21930*/:
                track = this.currentTrack;
                if (value == 1) {
                    z = true;
                }
                track.flagForced = z;
                return;
            case ID_COLOUR_RANGE /*21945*/:
                switch ((int) value) {
                    case 1:
                        this.currentTrack.colorRange = 2;
                        return;
                    case 2:
                        this.currentTrack.colorRange = 1;
                        return;
                    default:
                        return;
                }
            case ID_COLOUR_TRANSFER /*21946*/:
                int i = (int) value;
                if (i != 1) {
                    if (i == 16) {
                        this.currentTrack.colorTransfer = 6;
                        return;
                    } else if (i != 18) {
                        switch (i) {
                            case 6:
                            case 7:
                                break;
                            default:
                                return;
                        }
                    } else {
                        this.currentTrack.colorTransfer = 7;
                        return;
                    }
                }
                this.currentTrack.colorTransfer = 3;
                return;
            case ID_COLOUR_PRIMARIES /*21947*/:
                Track track2 = this.currentTrack;
                track2.hasColorInfo = true;
                int i2 = (int) value;
                if (i2 == 1) {
                    track2.colorSpace = 1;
                    return;
                } else if (i2 != 9) {
                    switch (i2) {
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                            track2.colorSpace = 2;
                            return;
                        default:
                            return;
                    }
                } else {
                    track2.colorSpace = 6;
                    return;
                }
            case ID_MAX_CLL /*21948*/:
                this.currentTrack.maxContentLuminance = (int) value;
                return;
            case ID_MAX_FALL /*21949*/:
                this.currentTrack.maxFrameAverageLuminance = (int) value;
                return;
            case ID_CODEC_DELAY /*22186*/:
                this.currentTrack.codecDelayNs = value;
                return;
            case ID_SEEK_PRE_ROLL /*22203*/:
                this.currentTrack.seekPreRollNs = value;
                return;
            case ID_AUDIO_BIT_DEPTH /*25188*/:
                this.currentTrack.audioBitDepth = (int) value;
                return;
            case ID_DEFAULT_DURATION /*2352003*/:
                this.currentTrack.defaultSampleDurationNs = (int) value;
                return;
            case ID_TIMECODE_SCALE /*2807729*/:
                this.timecodeScale = value;
                return;
            default:
                return;
        }
    }

    void floatElement(int id, double value) {
        if (id == ID_SAMPLING_FREQUENCY) {
            this.currentTrack.sampleRate = (int) value;
        } else if (id != ID_DURATION) {
            switch (id) {
                case ID_PRIMARY_R_CHROMATICITY_X /*21969*/:
                    this.currentTrack.primaryRChromaticityX = (float) value;
                    return;
                case ID_PRIMARY_R_CHROMATICITY_Y /*21970*/:
                    this.currentTrack.primaryRChromaticityY = (float) value;
                    return;
                case ID_PRIMARY_G_CHROMATICITY_X /*21971*/:
                    this.currentTrack.primaryGChromaticityX = (float) value;
                    return;
                case ID_PRIMARY_G_CHROMATICITY_Y /*21972*/:
                    this.currentTrack.primaryGChromaticityY = (float) value;
                    return;
                case ID_PRIMARY_B_CHROMATICITY_X /*21973*/:
                    this.currentTrack.primaryBChromaticityX = (float) value;
                    return;
                case ID_PRIMARY_B_CHROMATICITY_Y /*21974*/:
                    this.currentTrack.primaryBChromaticityY = (float) value;
                    return;
                case ID_WHITE_POINT_CHROMATICITY_X /*21975*/:
                    this.currentTrack.whitePointChromaticityX = (float) value;
                    return;
                case ID_WHITE_POINT_CHROMATICITY_Y /*21976*/:
                    this.currentTrack.whitePointChromaticityY = (float) value;
                    return;
                case ID_LUMNINANCE_MAX /*21977*/:
                    this.currentTrack.maxMasteringLuminance = (float) value;
                    return;
                case ID_LUMNINANCE_MIN /*21978*/:
                    this.currentTrack.minMasteringLuminance = (float) value;
                    return;
                default:
                    return;
            }
        } else {
            this.durationTimecode = (long) value;
        }
    }

    void stringElement(int id, String value) throws ParserException {
        if (id == 134) {
            this.currentTrack.codecId = value;
        } else if (id == ID_DOC_TYPE) {
            if (!DOC_TYPE_WEBM.equals(value)) {
                if (!DOC_TYPE_MATROSKA.equals(value)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("DocType ");
                    stringBuilder.append(value);
                    stringBuilder.append(" not supported");
                    throw new ParserException(stringBuilder.toString());
                }
            }
        } else if (id == ID_NAME) {
            this.currentTrack.name = value;
        } else if (id == ID_LANGUAGE) {
            this.currentTrack.language = value;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void binaryElement(int r23, int r24, com.google.android.exoplayer2.extractor.ExtractorInput r25) throws java.io.IOException, java.lang.InterruptedException {
        /*
        r22 = this;
        r0 = r22;
        r1 = r23;
        r2 = r24;
        r3 = r25;
        r4 = 161; // 0xa1 float:2.26E-43 double:7.95E-322;
        r5 = 4;
        r6 = 163; // 0xa3 float:2.28E-43 double:8.05E-322;
        r7 = 0;
        r8 = 1;
        if (r1 == r4) goto L_0x0094;
    L_0x0011:
        if (r1 == r6) goto L_0x0094;
    L_0x0013:
        r4 = 16981; // 0x4255 float:2.3795E-41 double:8.3897E-320;
        if (r1 == r4) goto L_0x0087;
    L_0x0017:
        r4 = 18402; // 0x47e2 float:2.5787E-41 double:9.092E-320;
        if (r1 == r4) goto L_0x0077;
    L_0x001b:
        r4 = 21419; // 0x53ab float:3.0014E-41 double:1.05824E-319;
        if (r1 == r4) goto L_0x0058;
    L_0x001f:
        r4 = 25506; // 0x63a2 float:3.5742E-41 double:1.26016E-319;
        if (r1 == r4) goto L_0x004b;
    L_0x0023:
        r4 = 30322; // 0x7672 float:4.249E-41 double:1.4981E-319;
        if (r1 != r4) goto L_0x0034;
    L_0x0027:
        r4 = r0.currentTrack;
        r5 = new byte[r2];
        r4.projectionData = r5;
        r4 = r4.projectionData;
        r3.readFully(r4, r7, r2);
        goto L_0x02c5;
    L_0x0034:
        r4 = new com.google.android.exoplayer2.ParserException;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Unexpected id: ";
        r5.append(r6);
        r5.append(r1);
        r5 = r5.toString();
        r4.<init>(r5);
        throw r4;
    L_0x004b:
        r4 = r0.currentTrack;
        r5 = new byte[r2];
        r4.codecPrivate = r5;
        r4 = r4.codecPrivate;
        r3.readFully(r4, r7, r2);
        goto L_0x02c5;
    L_0x0058:
        r4 = r0.seekEntryIdBytes;
        r4 = r4.data;
        java.util.Arrays.fill(r4, r7);
        r4 = r0.seekEntryIdBytes;
        r4 = r4.data;
        r5 = r5 - r2;
        r3.readFully(r4, r5, r2);
        r4 = r0.seekEntryIdBytes;
        r4.setPosition(r7);
        r4 = r0.seekEntryIdBytes;
        r4 = r4.readUnsignedInt();
        r4 = (int) r4;
        r0.seekEntryId = r4;
        goto L_0x02c5;
    L_0x0077:
        r4 = new byte[r2];
        r3.readFully(r4, r7, r2);
        r5 = r0.currentTrack;
        r6 = new com.google.android.exoplayer2.extractor.TrackOutput$CryptoData;
        r6.<init>(r8, r4, r7, r7);
        r5.cryptoData = r6;
        goto L_0x02c5;
    L_0x0087:
        r4 = r0.currentTrack;
        r5 = new byte[r2];
        r4.sampleStrippedBytes = r5;
        r4 = r4.sampleStrippedBytes;
        r3.readFully(r4, r7, r2);
        goto L_0x02c5;
    L_0x0094:
        r4 = r0.blockState;
        r9 = 8;
        if (r4 != 0) goto L_0x00ba;
    L_0x009a:
        r4 = r0.varintReader;
        r10 = r4.readUnsignedVarint(r3, r7, r8, r9);
        r4 = (int) r10;
        r0.blockTrackNumber = r4;
        r4 = r0.varintReader;
        r4 = r4.getLastLength();
        r0.blockTrackNumberLength = r4;
        r10 = -9223372036854775807; // 0x8000000000000001 float:1.4E-45 double:-4.9E-324;
        r0.blockDurationUs = r10;
        r0.blockState = r8;
        r4 = r0.scratch;
        r4.reset();
        goto L_0x00bb;
    L_0x00bb:
        r4 = r0.tracks;
        r10 = r0.blockTrackNumber;
        r4 = r4.get(r10);
        r4 = (com.google.android.exoplayer2.extractor.mkv.MatroskaExtractor.Track) r4;
        if (r4 != 0) goto L_0x00d1;
    L_0x00c7:
        r5 = r0.blockTrackNumberLength;
        r5 = r2 - r5;
        r3.skipFully(r5);
        r0.blockState = r7;
        return;
    L_0x00d1:
        r10 = r0.blockState;
        if (r10 != r8) goto L_0x0290;
    L_0x00d5:
        r10 = 3;
        r0.readScratch(r3, r10);
        r11 = r0.scratch;
        r11 = r11.data;
        r12 = 2;
        r11 = r11[r12];
        r11 = r11 & 6;
        r11 = r11 >> r8;
        r13 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        if (r11 != 0) goto L_0x00fc;
    L_0x00e7:
        r0.blockLacingSampleCount = r8;
        r5 = r0.blockLacingSampleSizes;
        r5 = ensureArrayCapacity(r5, r8);
        r0.blockLacingSampleSizes = r5;
        r5 = r0.blockLacingSampleSizes;
        r14 = r0.blockTrackNumberLength;
        r14 = r2 - r14;
        r14 = r14 - r10;
        r5[r7] = r14;
        goto L_0x021d;
    L_0x00fc:
        if (r1 != r6) goto L_0x0288;
    L_0x00fe:
        r0.readScratch(r3, r5);
        r14 = r0.scratch;
        r14 = r14.data;
        r14 = r14[r10];
        r14 = r14 & r13;
        r14 = r14 + r8;
        r0.blockLacingSampleCount = r14;
        r14 = r0.blockLacingSampleSizes;
        r15 = r0.blockLacingSampleCount;
        r14 = ensureArrayCapacity(r14, r15);
        r0.blockLacingSampleSizes = r14;
        if (r11 != r12) goto L_0x0126;
    L_0x0117:
        r10 = r0.blockTrackNumberLength;
        r10 = r2 - r10;
        r10 = r10 - r5;
        r5 = r0.blockLacingSampleCount;
        r10 = r10 / r5;
        r14 = r0.blockLacingSampleSizes;
        java.util.Arrays.fill(r14, r7, r5, r10);
        goto L_0x021d;
    L_0x0126:
        if (r11 != r8) goto L_0x0163;
    L_0x0128:
        r5 = 0;
        r10 = 4;
        r14 = 0;
    L_0x012b:
        r15 = r0.blockLacingSampleCount;
        r6 = r15 + -1;
        if (r14 >= r6) goto L_0x0155;
    L_0x0131:
        r6 = r0.blockLacingSampleSizes;
        r6[r14] = r7;
    L_0x0135:
        r10 = r10 + r8;
        r0.readScratch(r3, r10);
        r6 = r0.scratch;
        r6 = r6.data;
        r15 = r10 + -1;
        r6 = r6[r15];
        r6 = r6 & r13;
        r15 = r0.blockLacingSampleSizes;
        r16 = r15[r14];
        r16 = r16 + r6;
        r15[r14] = r16;
        if (r6 == r13) goto L_0x0154;
    L_0x014c:
        r15 = r15[r14];
        r5 = r5 + r15;
        r14 = r14 + 1;
        r6 = 163; // 0xa3 float:2.28E-43 double:8.05E-322;
        goto L_0x012b;
    L_0x0154:
        goto L_0x0135;
        r6 = r0.blockLacingSampleSizes;
        r15 = r15 - r8;
        r14 = r0.blockTrackNumberLength;
        r14 = r2 - r14;
        r14 = r14 - r10;
        r14 = r14 - r5;
        r6[r15] = r14;
        goto L_0x021d;
    L_0x0163:
        if (r11 != r10) goto L_0x0271;
    L_0x0165:
        r5 = 0;
        r6 = 4;
        r10 = 0;
    L_0x0168:
        r14 = r0.blockLacingSampleCount;
        r15 = r14 + -1;
        if (r10 >= r15) goto L_0x020f;
    L_0x016e:
        r14 = r0.blockLacingSampleSizes;
        r14[r10] = r7;
        r6 = r6 + 1;
        r0.readScratch(r3, r6);
        r14 = r0.scratch;
        r14 = r14.data;
        r15 = r6 + -1;
        r14 = r14[r15];
        if (r14 == 0) goto L_0x0207;
    L_0x0181:
        r14 = 0;
        r16 = 0;
        r12 = r16;
    L_0x0187:
        if (r12 >= r9) goto L_0x01d6;
    L_0x0189:
        r16 = 7 - r12;
        r16 = r8 << r16;
        r7 = r0.scratch;
        r7 = r7.data;
        r17 = r6 + -1;
        r7 = r7[r17];
        r7 = r7 & r16;
        if (r7 == 0) goto L_0x01d0;
    L_0x0199:
        r7 = r6 + -1;
        r6 = r6 + r12;
        r0.readScratch(r3, r6);
        r8 = r0.scratch;
        r8 = r8.data;
        r18 = r7 + 1;
        r7 = r8[r7];
        r7 = r7 & r13;
        r8 = r16 ^ -1;
        r7 = r7 & r8;
        r7 = (long) r7;
        r14 = r7;
        r7 = r18;
    L_0x01af:
        if (r7 >= r6) goto L_0x01c0;
    L_0x01b1:
        r14 = r14 << r9;
        r8 = r0.scratch;
        r8 = r8.data;
        r18 = r7 + 1;
        r7 = r8[r7];
        r7 = r7 & r13;
        r7 = (long) r7;
        r14 = r14 | r7;
        r7 = r18;
        goto L_0x01af;
    L_0x01c0:
        if (r10 <= 0) goto L_0x01cf;
    L_0x01c2:
        r8 = r12 * 7;
        r8 = r8 + 6;
        r18 = 1;
        r20 = r18 << r8;
        r20 = r20 - r18;
        r14 = r14 - r20;
        goto L_0x01d6;
    L_0x01cf:
        goto L_0x01d6;
        r12 = r12 + 1;
        r7 = 0;
        r8 = 1;
        goto L_0x0187;
    L_0x01d6:
        r7 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r12 = (r14 > r7 ? 1 : (r14 == r7 ? 0 : -1));
        if (r12 < 0) goto L_0x01fe;
    L_0x01dd:
        r7 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r12 = (r14 > r7 ? 1 : (r14 == r7 ? 0 : -1));
        if (r12 > 0) goto L_0x01fe;
    L_0x01e4:
        r7 = (int) r14;
        r8 = r0.blockLacingSampleSizes;
        if (r10 != 0) goto L_0x01eb;
    L_0x01e9:
        r12 = r7;
        goto L_0x01f0;
    L_0x01eb:
        r12 = r10 + -1;
        r12 = r8[r12];
        r12 = r12 + r7;
    L_0x01f0:
        r8[r10] = r12;
        r8 = r0.blockLacingSampleSizes;
        r8 = r8[r10];
        r5 = r5 + r8;
        r10 = r10 + 1;
        r7 = 0;
        r8 = 1;
        r12 = 2;
        goto L_0x0168;
        r7 = new com.google.android.exoplayer2.ParserException;
        r8 = "EBML lacing sample size out of range.";
        r7.<init>(r8);
        throw r7;
    L_0x0207:
        r7 = new com.google.android.exoplayer2.ParserException;
        r8 = "No valid varint length mask found";
        r7.<init>(r8);
        throw r7;
        r7 = r0.blockLacingSampleSizes;
        r8 = 1;
        r14 = r14 - r8;
        r8 = r0.blockTrackNumberLength;
        r8 = r2 - r8;
        r8 = r8 - r6;
        r8 = r8 - r5;
        r7[r14] = r8;
    L_0x021d:
        r5 = r0.scratch;
        r5 = r5.data;
        r6 = 0;
        r5 = r5[r6];
        r5 = r5 << r9;
        r6 = r0.scratch;
        r6 = r6.data;
        r7 = 1;
        r6 = r6[r7];
        r6 = r6 & r13;
        r5 = r5 | r6;
        r6 = r0.clusterTimecodeUs;
        r12 = (long) r5;
        r12 = r0.scaleTimecodeToUs(r12);
        r6 = r6 + r12;
        r0.blockTimeUs = r6;
        r6 = r0.scratch;
        r6 = r6.data;
        r7 = 2;
        r6 = r6[r7];
        r6 = r6 & r9;
        if (r6 != r9) goto L_0x0244;
    L_0x0242:
        r6 = 1;
        goto L_0x0245;
    L_0x0244:
        r6 = 0;
    L_0x0245:
        r8 = r4.type;
        if (r8 == r7) goto L_0x025b;
    L_0x0249:
        r8 = 163; // 0xa3 float:2.28E-43 double:8.05E-322;
        if (r1 != r8) goto L_0x0259;
    L_0x024d:
        r8 = r0.scratch;
        r8 = r8.data;
        r8 = r8[r7];
        r7 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        r8 = r8 & r7;
        if (r8 != r7) goto L_0x0259;
    L_0x0258:
        goto L_0x025b;
    L_0x0259:
        r7 = 0;
        goto L_0x025c;
    L_0x025b:
        r7 = 1;
    L_0x025c:
        if (r7 == 0) goto L_0x0260;
    L_0x025e:
        r8 = 1;
        goto L_0x0261;
    L_0x0260:
        r8 = 0;
    L_0x0261:
        if (r6 == 0) goto L_0x0266;
    L_0x0263:
        r9 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        goto L_0x0267;
    L_0x0266:
        r9 = 0;
    L_0x0267:
        r8 = r8 | r9;
        r0.blockFlags = r8;
        r8 = 2;
        r0.blockState = r8;
        r8 = 0;
        r0.blockLacingSampleIndex = r8;
        goto L_0x0291;
    L_0x0271:
        r5 = new com.google.android.exoplayer2.ParserException;
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "Unexpected lacing value: ";
        r6.append(r7);
        r6.append(r11);
        r6 = r6.toString();
        r5.<init>(r6);
        throw r5;
    L_0x0288:
        r5 = new com.google.android.exoplayer2.ParserException;
        r6 = "Lacing only supported in SimpleBlocks.";
        r5.<init>(r6);
        throw r5;
    L_0x0291:
        r5 = 163; // 0xa3 float:2.28E-43 double:8.05E-322;
        if (r1 != r5) goto L_0x02bc;
    L_0x0295:
        r5 = r0.blockLacingSampleIndex;
        r6 = r0.blockLacingSampleCount;
        if (r5 >= r6) goto L_0x02b8;
    L_0x029b:
        r6 = r0.blockLacingSampleSizes;
        r5 = r6[r5];
        r0.writeSampleData(r3, r4, r5);
        r5 = r0.blockTimeUs;
        r7 = r0.blockLacingSampleIndex;
        r8 = r4.defaultSampleDurationNs;
        r7 = r7 * r8;
        r7 = r7 / 1000;
        r7 = (long) r7;
        r5 = r5 + r7;
        r0.commitSampleToOutput(r4, r5);
        r7 = r0.blockLacingSampleIndex;
        r8 = 1;
        r7 = r7 + r8;
        r0.blockLacingSampleIndex = r7;
        goto L_0x0295;
    L_0x02b8:
        r5 = 0;
        r0.blockState = r5;
        goto L_0x02c5;
    L_0x02bc:
        r5 = 0;
        r6 = r0.blockLacingSampleSizes;
        r5 = r6[r5];
        r0.writeSampleData(r3, r4, r5);
    L_0x02c5:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.mkv.MatroskaExtractor.binaryElement(int, int, com.google.android.exoplayer2.extractor.ExtractorInput):void");
    }

    private void commitSampleToOutput(Track track, long timeUs) {
        MatroskaExtractor matroskaExtractor = this;
        Track track2 = track;
        if (track2.trueHdSampleRechunker != null) {
            track2.trueHdSampleRechunker.sampleMetadata(track2, timeUs);
        } else {
            long j = timeUs;
            if (CODEC_ID_SUBRIP.equals(track2.codecId)) {
                commitSubtitleSample(track, SUBRIP_TIMECODE_FORMAT, 19, 1000, SUBRIP_TIMECODE_EMPTY);
            } else if (CODEC_ID_ASS.equals(track2.codecId)) {
                commitSubtitleSample(track, SSA_TIMECODE_FORMAT, 21, SSA_TIMECODE_LAST_VALUE_SCALING_FACTOR, SSA_TIMECODE_EMPTY);
            }
            track2.output.sampleMetadata(timeUs, matroskaExtractor.blockFlags, matroskaExtractor.sampleBytesWritten, 0, track2.cryptoData);
        }
        matroskaExtractor.sampleRead = true;
        resetSample();
    }

    private void resetSample() {
        this.sampleBytesRead = 0;
        this.sampleBytesWritten = 0;
        this.sampleCurrentNalBytesRemaining = 0;
        this.sampleEncodingHandled = false;
        this.sampleSignalByteRead = false;
        this.samplePartitionCountRead = false;
        this.samplePartitionCount = 0;
        this.sampleSignalByte = (byte) 0;
        this.sampleInitializationVectorRead = false;
        this.sampleStrippedBytes.reset();
    }

    private void readScratch(ExtractorInput input, int requiredLength) throws IOException, InterruptedException {
        if (this.scratch.limit() < requiredLength) {
            if (this.scratch.capacity() < requiredLength) {
                ParsableByteArray parsableByteArray = this.scratch;
                parsableByteArray.reset(Arrays.copyOf(parsableByteArray.data, Math.max(this.scratch.data.length * 2, requiredLength)), this.scratch.limit());
            }
            input.readFully(this.scratch.data, this.scratch.limit(), requiredLength - this.scratch.limit());
            this.scratch.setLimit(requiredLength);
        }
    }

    private void writeSampleData(ExtractorInput input, Track track, int size) throws IOException, InterruptedException {
        MatroskaExtractor matroskaExtractor = this;
        ExtractorInput extractorInput = input;
        Track track2 = track;
        int size2 = size;
        if (CODEC_ID_SUBRIP.equals(track2.codecId)) {
            writeSubtitleSampleData(extractorInput, SUBRIP_PREFIX, size2);
        } else if (CODEC_ID_ASS.equals(track2.codecId)) {
            writeSubtitleSampleData(extractorInput, SSA_PREFIX, size2);
        } else {
            int i;
            int finalPartitionSize;
            TrackOutput output = track2.output;
            if (!matroskaExtractor.sampleEncodingHandled) {
                if (track2.hasContentEncryption) {
                    matroskaExtractor.blockFlags &= -1073741825;
                    int i2 = 128;
                    if (!matroskaExtractor.sampleSignalByteRead) {
                        extractorInput.readFully(matroskaExtractor.scratch.data, 0, 1);
                        matroskaExtractor.sampleBytesRead++;
                        if ((matroskaExtractor.scratch.data[0] & 128) != 128) {
                            matroskaExtractor.sampleSignalByte = matroskaExtractor.scratch.data[0];
                            matroskaExtractor.sampleSignalByteRead = true;
                        } else {
                            throw new ParserException("Extension bit is set in signal byte");
                        }
                    }
                    if ((matroskaExtractor.sampleSignalByte & 1) == 1) {
                        boolean hasSubsampleEncryption = (matroskaExtractor.sampleSignalByte & 2) == 2;
                        matroskaExtractor.blockFlags |= 1073741824;
                        if (!matroskaExtractor.sampleInitializationVectorRead) {
                            extractorInput.readFully(matroskaExtractor.encryptionInitializationVector.data, 0, 8);
                            matroskaExtractor.sampleBytesRead += 8;
                            matroskaExtractor.sampleInitializationVectorRead = true;
                            byte[] bArr = matroskaExtractor.scratch.data;
                            if (!hasSubsampleEncryption) {
                                i2 = 0;
                            }
                            bArr[0] = (byte) (i2 | 8);
                            matroskaExtractor.scratch.setPosition(0);
                            output.sampleData(matroskaExtractor.scratch, 1);
                            matroskaExtractor.sampleBytesWritten++;
                            matroskaExtractor.encryptionInitializationVector.setPosition(0);
                            output.sampleData(matroskaExtractor.encryptionInitializationVector, 8);
                            matroskaExtractor.sampleBytesWritten += 8;
                        }
                        if (hasSubsampleEncryption) {
                            int partitionOffset;
                            int i3;
                            if (!matroskaExtractor.samplePartitionCountRead) {
                                extractorInput.readFully(matroskaExtractor.scratch.data, 0, 1);
                                matroskaExtractor.sampleBytesRead++;
                                matroskaExtractor.scratch.setPosition(0);
                                matroskaExtractor.samplePartitionCount = matroskaExtractor.scratch.readUnsignedByte();
                                matroskaExtractor.samplePartitionCountRead = true;
                            }
                            i2 = matroskaExtractor.samplePartitionCount * 4;
                            matroskaExtractor.scratch.reset(i2);
                            extractorInput.readFully(matroskaExtractor.scratch.data, 0, i2);
                            matroskaExtractor.sampleBytesRead += i2;
                            short subsampleCount = (short) ((matroskaExtractor.samplePartitionCount / 2) + (short) 1);
                            int subsampleDataSize = (subsampleCount * 6) + 2;
                            ByteBuffer byteBuffer = matroskaExtractor.encryptionSubsampleDataBuffer;
                            if (byteBuffer != null) {
                                if (byteBuffer.capacity() >= subsampleDataSize) {
                                    matroskaExtractor.encryptionSubsampleDataBuffer.position(0);
                                    matroskaExtractor.encryptionSubsampleDataBuffer.putShort(subsampleCount);
                                    partitionOffset = 0;
                                    i3 = 0;
                                    while (true) {
                                        i = matroskaExtractor.samplePartitionCount;
                                        if (i3 < i) {
                                            break;
                                        }
                                        i = partitionOffset;
                                        partitionOffset = matroskaExtractor.scratch.readUnsignedIntToInt();
                                        if (i3 % 2 != 0) {
                                            matroskaExtractor.encryptionSubsampleDataBuffer.putShort((short) (partitionOffset - i));
                                        } else {
                                            matroskaExtractor.encryptionSubsampleDataBuffer.putInt(partitionOffset - i);
                                        }
                                        i3++;
                                    }
                                    finalPartitionSize = (size2 - matroskaExtractor.sampleBytesRead) - partitionOffset;
                                    if (i % 2 != 1) {
                                        matroskaExtractor.encryptionSubsampleDataBuffer.putInt(finalPartitionSize);
                                    } else {
                                        matroskaExtractor.encryptionSubsampleDataBuffer.putShort((short) finalPartitionSize);
                                        matroskaExtractor.encryptionSubsampleDataBuffer.putInt(0);
                                    }
                                    matroskaExtractor.encryptionSubsampleData.reset(matroskaExtractor.encryptionSubsampleDataBuffer.array(), subsampleDataSize);
                                    output.sampleData(matroskaExtractor.encryptionSubsampleData, subsampleDataSize);
                                    matroskaExtractor.sampleBytesWritten += subsampleDataSize;
                                }
                            }
                            matroskaExtractor.encryptionSubsampleDataBuffer = ByteBuffer.allocate(subsampleDataSize);
                            matroskaExtractor.encryptionSubsampleDataBuffer.position(0);
                            matroskaExtractor.encryptionSubsampleDataBuffer.putShort(subsampleCount);
                            partitionOffset = 0;
                            i3 = 0;
                            while (true) {
                                i = matroskaExtractor.samplePartitionCount;
                                if (i3 < i) {
                                    break;
                                }
                                i = partitionOffset;
                                partitionOffset = matroskaExtractor.scratch.readUnsignedIntToInt();
                                if (i3 % 2 != 0) {
                                    matroskaExtractor.encryptionSubsampleDataBuffer.putInt(partitionOffset - i);
                                } else {
                                    matroskaExtractor.encryptionSubsampleDataBuffer.putShort((short) (partitionOffset - i));
                                }
                                i3++;
                            }
                            finalPartitionSize = (size2 - matroskaExtractor.sampleBytesRead) - partitionOffset;
                            if (i % 2 != 1) {
                                matroskaExtractor.encryptionSubsampleDataBuffer.putShort((short) finalPartitionSize);
                                matroskaExtractor.encryptionSubsampleDataBuffer.putInt(0);
                            } else {
                                matroskaExtractor.encryptionSubsampleDataBuffer.putInt(finalPartitionSize);
                            }
                            matroskaExtractor.encryptionSubsampleData.reset(matroskaExtractor.encryptionSubsampleDataBuffer.array(), subsampleDataSize);
                            output.sampleData(matroskaExtractor.encryptionSubsampleData, subsampleDataSize);
                            matroskaExtractor.sampleBytesWritten += subsampleDataSize;
                        }
                    }
                } else if (track2.sampleStrippedBytes != null) {
                    matroskaExtractor.sampleStrippedBytes.reset(track2.sampleStrippedBytes, track2.sampleStrippedBytes.length);
                    matroskaExtractor.sampleEncodingHandled = true;
                }
                matroskaExtractor.sampleEncodingHandled = true;
            }
            size2 += matroskaExtractor.sampleStrippedBytes.limit();
            if (!CODEC_ID_H264.equals(track2.codecId)) {
                if (!CODEC_ID_H265.equals(track2.codecId)) {
                    if (track2.trueHdSampleRechunker != null) {
                        Assertions.checkState(matroskaExtractor.sampleStrippedBytes.limit() == 0);
                        track2.trueHdSampleRechunker.startSample(extractorInput, matroskaExtractor.blockFlags, size2);
                    }
                    while (true) {
                        int i4 = matroskaExtractor.sampleBytesRead;
                        if (i4 >= size2) {
                            break;
                        }
                        readToOutput(extractorInput, output, size2 - i4);
                    }
                    if (CODEC_ID_VORBIS.equals(track2.codecId)) {
                        matroskaExtractor.vorbisNumPageSamples.setPosition(0);
                        output.sampleData(matroskaExtractor.vorbisNumPageSamples, 4);
                        matroskaExtractor.sampleBytesWritten += 4;
                    }
                }
            }
            byte[] nalLengthData = matroskaExtractor.nalLength.data;
            nalLengthData[0] = (byte) 0;
            nalLengthData[1] = (byte) 0;
            nalLengthData[2] = (byte) 0;
            i = track2.nalUnitLengthFieldLength;
            int nalUnitLengthFieldLengthDiff = 4 - track2.nalUnitLengthFieldLength;
            while (matroskaExtractor.sampleBytesRead < size2) {
                finalPartitionSize = matroskaExtractor.sampleCurrentNalBytesRemaining;
                if (finalPartitionSize == 0) {
                    readToTarget(extractorInput, nalLengthData, nalUnitLengthFieldLengthDiff, i);
                    matroskaExtractor.nalLength.setPosition(0);
                    matroskaExtractor.sampleCurrentNalBytesRemaining = matroskaExtractor.nalLength.readUnsignedIntToInt();
                    matroskaExtractor.nalStartCode.setPosition(0);
                    output.sampleData(matroskaExtractor.nalStartCode, 4);
                    matroskaExtractor.sampleBytesWritten += 4;
                } else {
                    matroskaExtractor.sampleCurrentNalBytesRemaining = finalPartitionSize - readToOutput(extractorInput, output, finalPartitionSize);
                }
            }
            if (CODEC_ID_VORBIS.equals(track2.codecId)) {
                matroskaExtractor.vorbisNumPageSamples.setPosition(0);
                output.sampleData(matroskaExtractor.vorbisNumPageSamples, 4);
                matroskaExtractor.sampleBytesWritten += 4;
            }
        }
    }

    private void writeSubtitleSampleData(ExtractorInput input, byte[] samplePrefix, int size) throws IOException, InterruptedException {
        int sizeWithPrefix = samplePrefix.length + size;
        if (this.subtitleSample.capacity() < sizeWithPrefix) {
            this.subtitleSample.data = Arrays.copyOf(samplePrefix, sizeWithPrefix + size);
        } else {
            System.arraycopy(samplePrefix, 0, this.subtitleSample.data, 0, samplePrefix.length);
        }
        input.readFully(this.subtitleSample.data, samplePrefix.length, size);
        this.subtitleSample.reset(sizeWithPrefix);
    }

    private void commitSubtitleSample(Track track, String timecodeFormat, int endTimecodeOffset, long lastTimecodeValueScalingFactor, byte[] emptyTimecode) {
        setSampleDuration(this.subtitleSample.data, this.blockDurationUs, timecodeFormat, endTimecodeOffset, lastTimecodeValueScalingFactor, emptyTimecode);
        TrackOutput trackOutput = track.output;
        ParsableByteArray parsableByteArray = this.subtitleSample;
        trackOutput.sampleData(parsableByteArray, parsableByteArray.limit());
        this.sampleBytesWritten += this.subtitleSample.limit();
    }

    private static void setSampleDuration(byte[] subripSampleData, long durationUs, String timecodeFormat, int endTimecodeOffset, long lastTimecodeValueScalingFactor, byte[] emptyTimecode) {
        byte[] timeCodeData;
        long j;
        String str;
        if (durationUs == C0555C.TIME_UNSET) {
            timeCodeData = emptyTimecode;
            j = durationUs;
            str = timecodeFormat;
        } else {
            j = durationUs - (((long) (((int) (durationUs / 3600000000L)) * 3600)) * 1000000);
            j -= ((long) (((int) (j / 60000000)) * 60)) * 1000000;
            int lastValue = (int) ((j - (((long) ((int) (j / 1000000))) * 1000000)) / lastTimecodeValueScalingFactor);
            str = timecodeFormat;
            timeCodeData = Util.getUtf8Bytes(String.format(Locale.US, timecodeFormat, new Object[]{Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(seconds), Integer.valueOf(lastValue)}));
        }
        byte[] bArr = subripSampleData;
        int seconds = endTimecodeOffset;
        System.arraycopy(timeCodeData, 0, subripSampleData, endTimecodeOffset, emptyTimecode.length);
    }

    private void readToTarget(ExtractorInput input, byte[] target, int offset, int length) throws IOException, InterruptedException {
        int pendingStrippedBytes = Math.min(length, this.sampleStrippedBytes.bytesLeft());
        input.readFully(target, offset + pendingStrippedBytes, length - pendingStrippedBytes);
        if (pendingStrippedBytes > 0) {
            this.sampleStrippedBytes.readBytes(target, offset, pendingStrippedBytes);
        }
        this.sampleBytesRead += length;
    }

    private int readToOutput(ExtractorInput input, TrackOutput output, int length) throws IOException, InterruptedException {
        int bytesRead;
        int strippedBytesLeft = this.sampleStrippedBytes.bytesLeft();
        if (strippedBytesLeft > 0) {
            bytesRead = Math.min(length, strippedBytesLeft);
            output.sampleData(this.sampleStrippedBytes, bytesRead);
        } else {
            bytesRead = output.sampleData(input, length, false);
        }
        this.sampleBytesRead += bytesRead;
        this.sampleBytesWritten += bytesRead;
        return bytesRead;
    }

    private SeekMap buildSeekMap() {
        if (!(this.segmentContentPosition == -1 || this.durationUs == C0555C.TIME_UNSET)) {
            LongArray longArray = this.cueTimesUs;
            if (longArray != null) {
                if (longArray.size() != 0) {
                    longArray = this.cueClusterPositions;
                    if (longArray != null) {
                        if (longArray.size() != this.cueTimesUs.size()) {
                            this.cueTimesUs = null;
                            this.cueClusterPositions = null;
                            return new Unseekable(this.durationUs);
                        }
                        int i;
                        int cuePointsSize = this.cueTimesUs.size();
                        int[] sizes = new int[cuePointsSize];
                        long[] offsets = new long[cuePointsSize];
                        long[] durationsUs = new long[cuePointsSize];
                        long[] timesUs = new long[cuePointsSize];
                        for (i = 0; i < cuePointsSize; i++) {
                            timesUs[i] = this.cueTimesUs.get(i);
                            offsets[i] = this.segmentContentPosition + this.cueClusterPositions.get(i);
                        }
                        for (i = 0; i < cuePointsSize - 1; i++) {
                            sizes[i] = (int) (offsets[i + 1] - offsets[i]);
                            durationsUs[i] = timesUs[i + 1] - timesUs[i];
                        }
                        sizes[cuePointsSize - 1] = (int) ((this.segmentContentPosition + this.segmentContentSize) - offsets[cuePointsSize - 1]);
                        durationsUs[cuePointsSize - 1] = this.durationUs - timesUs[cuePointsSize - 1];
                        this.cueTimesUs = null;
                        this.cueClusterPositions = null;
                        return new ChunkIndex(sizes, offsets, durationsUs, timesUs);
                    }
                }
                this.cueTimesUs = null;
                this.cueClusterPositions = null;
                return new Unseekable(this.durationUs);
            }
        }
        this.cueTimesUs = null;
        this.cueClusterPositions = null;
        return new Unseekable(this.durationUs);
    }

    private boolean maybeSeekForCues(PositionHolder seekPosition, long currentPosition) {
        if (this.seekForCues) {
            this.seekPositionAfterBuildingCues = currentPosition;
            seekPosition.position = this.cuesContentPosition;
            this.seekForCues = false;
            return true;
        }
        if (this.sentSeekMap) {
            long j = this.seekPositionAfterBuildingCues;
            if (j != -1) {
                seekPosition.position = j;
                this.seekPositionAfterBuildingCues = -1;
                return true;
            }
        }
        return false;
    }

    private long scaleTimecodeToUs(long unscaledTimecode) throws ParserException {
        long j = this.timecodeScale;
        if (j != C0555C.TIME_UNSET) {
            return Util.scaleLargeTimestamp(unscaledTimecode, j, 1000);
        }
        throw new ParserException("Can't scale timecode prior to timecodeScale being set.");
    }

    private static boolean isCodecSupported(String codecId) {
        if (!CODEC_ID_VP8.equals(codecId)) {
            if (!CODEC_ID_VP9.equals(codecId)) {
                if (!CODEC_ID_MPEG2.equals(codecId)) {
                    if (!CODEC_ID_MPEG4_SP.equals(codecId)) {
                        if (!CODEC_ID_MPEG4_ASP.equals(codecId)) {
                            if (!CODEC_ID_MPEG4_AP.equals(codecId)) {
                                if (!CODEC_ID_H264.equals(codecId)) {
                                    if (!CODEC_ID_H265.equals(codecId)) {
                                        if (!CODEC_ID_FOURCC.equals(codecId)) {
                                            if (!CODEC_ID_THEORA.equals(codecId)) {
                                                if (!CODEC_ID_OPUS.equals(codecId)) {
                                                    if (!CODEC_ID_VORBIS.equals(codecId)) {
                                                        if (!CODEC_ID_AAC.equals(codecId)) {
                                                            if (!CODEC_ID_MP2.equals(codecId)) {
                                                                if (!CODEC_ID_MP3.equals(codecId)) {
                                                                    if (!CODEC_ID_AC3.equals(codecId)) {
                                                                        if (!CODEC_ID_E_AC3.equals(codecId)) {
                                                                            if (!CODEC_ID_TRUEHD.equals(codecId)) {
                                                                                if (!CODEC_ID_DTS.equals(codecId)) {
                                                                                    if (!CODEC_ID_DTS_EXPRESS.equals(codecId)) {
                                                                                        if (!CODEC_ID_DTS_LOSSLESS.equals(codecId)) {
                                                                                            if (!CODEC_ID_FLAC.equals(codecId)) {
                                                                                                if (!CODEC_ID_ACM.equals(codecId)) {
                                                                                                    if (!CODEC_ID_PCM_INT_LIT.equals(codecId)) {
                                                                                                        if (!CODEC_ID_SUBRIP.equals(codecId)) {
                                                                                                            if (!CODEC_ID_ASS.equals(codecId)) {
                                                                                                                if (!CODEC_ID_VOBSUB.equals(codecId)) {
                                                                                                                    if (!CODEC_ID_PGS.equals(codecId)) {
                                                                                                                        if (!CODEC_ID_DVBSUB.equals(codecId)) {
                                                                                                                            return false;
                                                                                                                        }
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private static int[] ensureArrayCapacity(int[] array, int length) {
        if (array == null) {
            return new int[length];
        }
        if (array.length >= length) {
            return array;
        }
        return new int[Math.max(array.length * 2, length)];
    }
}
