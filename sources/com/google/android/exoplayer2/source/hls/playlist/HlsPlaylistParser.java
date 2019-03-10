package com.google.android.exoplayer2.source.hls.playlist;

import android.support.annotation.Nullable;
import android.util.Base64;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmInitData.SchemeData;
import com.google.android.exoplayer2.extractor.mp4.PsshAtomUtil;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment;
import com.google.android.exoplayer2.upstream.ParsingLoadable.Parser;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HlsPlaylistParser implements Parser<HlsPlaylist> {
    private static final String ATTR_CLOSED_CAPTIONS_NONE = "CLOSED-CAPTIONS=NONE";
    private static final String BOOLEAN_FALSE = "NO";
    private static final String BOOLEAN_TRUE = "YES";
    private static final String KEYFORMAT_IDENTITY = "identity";
    private static final String KEYFORMAT_PLAYREADY = "com.microsoft.playready";
    private static final String KEYFORMAT_WIDEVINE_PSSH_BINARY = "urn:uuid:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed";
    private static final String KEYFORMAT_WIDEVINE_PSSH_JSON = "com.widevine";
    private static final String METHOD_AES_128 = "AES-128";
    private static final String METHOD_NONE = "NONE";
    private static final String METHOD_SAMPLE_AES = "SAMPLE-AES";
    private static final String METHOD_SAMPLE_AES_CENC = "SAMPLE-AES-CENC";
    private static final String METHOD_SAMPLE_AES_CTR = "SAMPLE-AES-CTR";
    private static final String PLAYLIST_HEADER = "#EXTM3U";
    private static final Pattern REGEX_ATTR_BYTERANGE = Pattern.compile("BYTERANGE=\"(\\d+(?:@\\d+)?)\\b\"");
    private static final Pattern REGEX_AUDIO = Pattern.compile("AUDIO=\"(.+?)\"");
    private static final Pattern REGEX_AUTOSELECT = compileBooleanAttrPattern("AUTOSELECT");
    private static final Pattern REGEX_AVERAGE_BANDWIDTH = Pattern.compile("AVERAGE-BANDWIDTH=(\\d+)\\b");
    private static final Pattern REGEX_BANDWIDTH = Pattern.compile("[^-]BANDWIDTH=(\\d+)\\b");
    private static final Pattern REGEX_BYTERANGE = Pattern.compile("#EXT-X-BYTERANGE:(\\d+(?:@\\d+)?)\\b");
    private static final Pattern REGEX_CODECS = Pattern.compile("CODECS=\"(.+?)\"");
    private static final Pattern REGEX_DEFAULT = compileBooleanAttrPattern("DEFAULT");
    private static final Pattern REGEX_FORCED = compileBooleanAttrPattern("FORCED");
    private static final Pattern REGEX_FRAME_RATE = Pattern.compile("FRAME-RATE=([\\d\\.]+)\\b");
    private static final Pattern REGEX_GROUP_ID = Pattern.compile("GROUP-ID=\"(.+?)\"");
    private static final Pattern REGEX_IMPORT = Pattern.compile("IMPORT=\"(.+?)\"");
    private static final Pattern REGEX_INSTREAM_ID = Pattern.compile("INSTREAM-ID=\"((?:CC|SERVICE)\\d+)\"");
    private static final Pattern REGEX_IV = Pattern.compile("IV=([^,.*]+)");
    private static final Pattern REGEX_KEYFORMAT = Pattern.compile("KEYFORMAT=\"(.+?)\"");
    private static final Pattern REGEX_KEYFORMATVERSIONS = Pattern.compile("KEYFORMATVERSIONS=\"(.+?)\"");
    private static final Pattern REGEX_LANGUAGE = Pattern.compile("LANGUAGE=\"(.+?)\"");
    private static final Pattern REGEX_MEDIA_DURATION = Pattern.compile("#EXTINF:([\\d\\.]+)\\b");
    private static final Pattern REGEX_MEDIA_SEQUENCE = Pattern.compile("#EXT-X-MEDIA-SEQUENCE:(\\d+)\\b");
    private static final Pattern REGEX_MEDIA_TITLE = Pattern.compile("#EXTINF:[\\d\\.]+\\b,(.+)");
    private static final Pattern REGEX_METHOD = Pattern.compile("METHOD=(NONE|AES-128|SAMPLE-AES|SAMPLE-AES-CENC|SAMPLE-AES-CTR)\\s*(?:,|$)");
    private static final Pattern REGEX_NAME = Pattern.compile("NAME=\"(.+?)\"");
    private static final Pattern REGEX_PLAYLIST_TYPE = Pattern.compile("#EXT-X-PLAYLIST-TYPE:(.+)\\b");
    private static final Pattern REGEX_RESOLUTION = Pattern.compile("RESOLUTION=(\\d+x\\d+)");
    private static final Pattern REGEX_TARGET_DURATION = Pattern.compile("#EXT-X-TARGETDURATION:(\\d+)\\b");
    private static final Pattern REGEX_TIME_OFFSET = Pattern.compile("TIME-OFFSET=(-?[\\d\\.]+)\\b");
    private static final Pattern REGEX_TYPE = Pattern.compile("TYPE=(AUDIO|VIDEO|SUBTITLES|CLOSED-CAPTIONS)");
    private static final Pattern REGEX_URI = Pattern.compile("URI=\"(.+?)\"");
    private static final Pattern REGEX_VALUE = Pattern.compile("VALUE=\"(.+?)\"");
    private static final Pattern REGEX_VARIABLE_REFERENCE = Pattern.compile("\\{\\$([a-zA-Z0-9\\-_]+)\\}");
    private static final Pattern REGEX_VERSION = Pattern.compile("#EXT-X-VERSION:(\\d+)\\b");
    private static final String TAG_BYTERANGE = "#EXT-X-BYTERANGE";
    private static final String TAG_DEFINE = "#EXT-X-DEFINE";
    private static final String TAG_DISCONTINUITY = "#EXT-X-DISCONTINUITY";
    private static final String TAG_DISCONTINUITY_SEQUENCE = "#EXT-X-DISCONTINUITY-SEQUENCE";
    private static final String TAG_ENDLIST = "#EXT-X-ENDLIST";
    private static final String TAG_GAP = "#EXT-X-GAP";
    private static final String TAG_INDEPENDENT_SEGMENTS = "#EXT-X-INDEPENDENT-SEGMENTS";
    private static final String TAG_INIT_SEGMENT = "#EXT-X-MAP";
    private static final String TAG_KEY = "#EXT-X-KEY";
    private static final String TAG_MEDIA = "#EXT-X-MEDIA";
    private static final String TAG_MEDIA_DURATION = "#EXTINF";
    private static final String TAG_MEDIA_SEQUENCE = "#EXT-X-MEDIA-SEQUENCE";
    private static final String TAG_PLAYLIST_TYPE = "#EXT-X-PLAYLIST-TYPE";
    private static final String TAG_PREFIX = "#EXT";
    private static final String TAG_PROGRAM_DATE_TIME = "#EXT-X-PROGRAM-DATE-TIME";
    private static final String TAG_START = "#EXT-X-START";
    private static final String TAG_STREAM_INF = "#EXT-X-STREAM-INF";
    private static final String TAG_TARGET_DURATION = "#EXT-X-TARGETDURATION";
    private static final String TAG_VERSION = "#EXT-X-VERSION";
    private static final String TYPE_AUDIO = "AUDIO";
    private static final String TYPE_CLOSED_CAPTIONS = "CLOSED-CAPTIONS";
    private static final String TYPE_SUBTITLES = "SUBTITLES";
    private static final String TYPE_VIDEO = "VIDEO";
    private final HlsMasterPlaylist masterPlaylist;

    private static class LineIterator {
        private final Queue<String> extraLines;
        private String next;
        private final BufferedReader reader;

        public LineIterator(Queue<String> extraLines, BufferedReader reader) {
            this.extraLines = extraLines;
            this.reader = reader;
        }

        public boolean hasNext() throws IOException {
            if (this.next != null) {
                return true;
            }
            if (this.extraLines.isEmpty()) {
                while (true) {
                    String readLine = this.reader.readLine();
                    this.next = readLine;
                    if (readLine == null) {
                        return false;
                    }
                    this.next = this.next.trim();
                    if (!this.next.isEmpty()) {
                        return true;
                    }
                }
            } else {
                this.next = (String) this.extraLines.poll();
                return true;
            }
        }

        public String next() throws IOException {
            if (!hasNext()) {
                return null;
            }
            String result = this.next;
            this.next = null;
            return result;
        }
    }

    public com.google.android.exoplayer2.source.hls.playlist.HlsPlaylist parse(android.net.Uri r7, java.io.InputStream r8) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:51:0x00be in {8, 13, 31, 32, 33, 34, 35, 36, 37, 38, 39, 42, 44, 47, 50} preds:[]
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
        r6 = this;
        r0 = new java.io.BufferedReader;
        r1 = new java.io.InputStreamReader;
        r1.<init>(r8);
        r0.<init>(r1);
        r1 = new java.util.ArrayDeque;
        r1.<init>();
        r2 = checkPlaylistHeader(r0);	 Catch:{ all -> 0x00b9 }
        if (r2 == 0) goto L_0x00b1;	 Catch:{ all -> 0x00b9 }
    L_0x0015:
        r2 = r0.readLine();	 Catch:{ all -> 0x00b9 }
        r3 = r2;	 Catch:{ all -> 0x00b9 }
        if (r2 == 0) goto L_0x00a5;	 Catch:{ all -> 0x00b9 }
    L_0x001c:
        r2 = r3.trim();	 Catch:{ all -> 0x00b9 }
        r3 = r2.isEmpty();	 Catch:{ all -> 0x00b9 }
        if (r3 == 0) goto L_0x0027;	 Catch:{ all -> 0x00b9 }
    L_0x0026:
        goto L_0x0015;	 Catch:{ all -> 0x00b9 }
    L_0x0027:
        r3 = "#EXT-X-STREAM-INF";	 Catch:{ all -> 0x00b9 }
        r3 = r2.startsWith(r3);	 Catch:{ all -> 0x00b9 }
        if (r3 == 0) goto L_0x0043;	 Catch:{ all -> 0x00b9 }
    L_0x002f:
        r1.add(r2);	 Catch:{ all -> 0x00b9 }
        r3 = new com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParser$LineIterator;	 Catch:{ all -> 0x00b9 }
        r3.<init>(r1, r0);	 Catch:{ all -> 0x00b9 }
        r4 = r7.toString();	 Catch:{ all -> 0x00b9 }
        r3 = parseMasterPlaylist(r3, r4);	 Catch:{ all -> 0x00b9 }
        com.google.android.exoplayer2.util.Util.closeQuietly(r0);
        return r3;
    L_0x0043:
        r3 = "#EXT-X-TARGETDURATION";	 Catch:{ all -> 0x00b9 }
        r3 = r2.startsWith(r3);	 Catch:{ all -> 0x00b9 }
        if (r3 != 0) goto L_0x008e;	 Catch:{ all -> 0x00b9 }
    L_0x004b:
        r3 = "#EXT-X-MEDIA-SEQUENCE";	 Catch:{ all -> 0x00b9 }
        r3 = r2.startsWith(r3);	 Catch:{ all -> 0x00b9 }
        if (r3 != 0) goto L_0x008d;	 Catch:{ all -> 0x00b9 }
    L_0x0053:
        r3 = "#EXTINF";	 Catch:{ all -> 0x00b9 }
        r3 = r2.startsWith(r3);	 Catch:{ all -> 0x00b9 }
        if (r3 != 0) goto L_0x008c;	 Catch:{ all -> 0x00b9 }
    L_0x005b:
        r3 = "#EXT-X-KEY";	 Catch:{ all -> 0x00b9 }
        r3 = r2.startsWith(r3);	 Catch:{ all -> 0x00b9 }
        if (r3 != 0) goto L_0x008b;	 Catch:{ all -> 0x00b9 }
    L_0x0063:
        r3 = "#EXT-X-BYTERANGE";	 Catch:{ all -> 0x00b9 }
        r3 = r2.startsWith(r3);	 Catch:{ all -> 0x00b9 }
        if (r3 != 0) goto L_0x008a;	 Catch:{ all -> 0x00b9 }
    L_0x006b:
        r3 = "#EXT-X-DISCONTINUITY";	 Catch:{ all -> 0x00b9 }
        r3 = r2.equals(r3);	 Catch:{ all -> 0x00b9 }
        if (r3 != 0) goto L_0x0089;	 Catch:{ all -> 0x00b9 }
    L_0x0073:
        r3 = "#EXT-X-DISCONTINUITY-SEQUENCE";	 Catch:{ all -> 0x00b9 }
        r3 = r2.equals(r3);	 Catch:{ all -> 0x00b9 }
        if (r3 != 0) goto L_0x0088;	 Catch:{ all -> 0x00b9 }
    L_0x007b:
        r3 = "#EXT-X-ENDLIST";	 Catch:{ all -> 0x00b9 }
        r3 = r2.equals(r3);	 Catch:{ all -> 0x00b9 }
        if (r3 == 0) goto L_0x0084;	 Catch:{ all -> 0x00b9 }
    L_0x0083:
        goto L_0x008f;	 Catch:{ all -> 0x00b9 }
    L_0x0084:
        r1.add(r2);	 Catch:{ all -> 0x00b9 }
        goto L_0x0015;	 Catch:{ all -> 0x00b9 }
    L_0x0088:
        goto L_0x008f;	 Catch:{ all -> 0x00b9 }
    L_0x0089:
        goto L_0x008f;	 Catch:{ all -> 0x00b9 }
    L_0x008a:
        goto L_0x008f;	 Catch:{ all -> 0x00b9 }
    L_0x008b:
        goto L_0x008f;	 Catch:{ all -> 0x00b9 }
    L_0x008c:
        goto L_0x008f;	 Catch:{ all -> 0x00b9 }
    L_0x008d:
        goto L_0x008f;	 Catch:{ all -> 0x00b9 }
    L_0x008f:
        r1.add(r2);	 Catch:{ all -> 0x00b9 }
        r3 = r6.masterPlaylist;	 Catch:{ all -> 0x00b9 }
        r4 = new com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParser$LineIterator;	 Catch:{ all -> 0x00b9 }
        r4.<init>(r1, r0);	 Catch:{ all -> 0x00b9 }
        r5 = r7.toString();	 Catch:{ all -> 0x00b9 }
        r3 = parseMediaPlaylist(r3, r4, r5);	 Catch:{ all -> 0x00b9 }
        com.google.android.exoplayer2.util.Util.closeQuietly(r0);
        return r3;
    L_0x00a5:
        com.google.android.exoplayer2.util.Util.closeQuietly(r0);
        r2 = new com.google.android.exoplayer2.ParserException;
        r4 = "Failed to parse the playlist, could not identify any tags.";
        r2.<init>(r4);
        throw r2;
    L_0x00b1:
        r2 = new com.google.android.exoplayer2.source.UnrecognizedInputFormatException;	 Catch:{ all -> 0x00b9 }
        r3 = "Input does not start with the #EXTM3U header.";	 Catch:{ all -> 0x00b9 }
        r2.<init>(r3, r7);	 Catch:{ all -> 0x00b9 }
        throw r2;	 Catch:{ all -> 0x00b9 }
    L_0x00b9:
        r2 = move-exception;
        com.google.android.exoplayer2.util.Util.closeQuietly(r0);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParser.parse(android.net.Uri, java.io.InputStream):com.google.android.exoplayer2.source.hls.playlist.HlsPlaylist");
    }

    public HlsPlaylistParser() {
        this(HlsMasterPlaylist.EMPTY);
    }

    public HlsPlaylistParser(HlsMasterPlaylist masterPlaylist) {
        this.masterPlaylist = masterPlaylist;
    }

    private static boolean checkPlaylistHeader(BufferedReader reader) throws IOException {
        int last = reader.read();
        if (last == 239) {
            if (reader.read() == 187) {
                if (reader.read() == 191) {
                    last = reader.read();
                }
            }
            return false;
        }
        char last2 = skipIgnorableWhitespace(reader, true, last);
        int playlistHeaderLength = PLAYLIST_HEADER.length();
        for (int i = 0; i < playlistHeaderLength; i++) {
            if (last2 != PLAYLIST_HEADER.charAt(i)) {
                return false;
            }
            last2 = reader.read();
        }
        return Util.isLinebreak(skipIgnorableWhitespace(reader, false, last2));
    }

    private static int skipIgnorableWhitespace(BufferedReader reader, boolean skipLinebreaks, int c) throws IOException {
        while (c != -1 && Character.isWhitespace(c) && (skipLinebreaks || !Util.isLinebreak(c))) {
            c = reader.read();
        }
        return c;
    }

    private static HlsMasterPlaylist parseMasterPlaylist(LineIterator iterator, String baseUri) throws IOException {
        Format format;
        List<Format> list;
        String averageBandwidthString;
        String resolutionString;
        String line;
        ArrayList<String> mediaTags;
        ArrayList<HlsUrl> variants;
        ArrayList<String> tags;
        List<Format> muxedCaptionFormats;
        HashSet<String> variantUrls = new HashSet();
        HashMap<String, String> audioGroupIdToCodecs = new HashMap();
        HashMap<String, String> variableDefinitions = new HashMap();
        ArrayList<HlsUrl> variants2 = new ArrayList();
        ArrayList<HlsUrl> audios = new ArrayList();
        ArrayList<HlsUrl> subtitles = new ArrayList();
        ArrayList<String> mediaTags2 = new ArrayList();
        ArrayList<String> tags2 = new ArrayList();
        Format muxedAudioFormat = null;
        List<Format> muxedCaptionFormats2 = null;
        boolean noClosedCaptions = false;
        boolean hasIndependentSegmentsTag = false;
        while (iterator.hasNext()) {
            HashSet<String> hashSet;
            String codecs;
            String frameRateString;
            String line2 = iterator.next();
            if (line2.startsWith(TAG_PREFIX)) {
                tags2.add(line2);
            }
            if (line2.startsWith(TAG_DEFINE)) {
                variableDefinitions.put(parseStringAttr(line2, REGEX_NAME, variableDefinitions), parseStringAttr(line2, REGEX_VALUE, variableDefinitions));
                hashSet = variantUrls;
                format = muxedAudioFormat;
                list = muxedCaptionFormats2;
            } else if (line2.equals(TAG_INDEPENDENT_SEGMENTS)) {
                hasIndependentSegmentsTag = true;
            } else if (line2.startsWith(TAG_MEDIA)) {
                mediaTags2.add(line2);
                hashSet = variantUrls;
                format = muxedAudioFormat;
                list = muxedCaptionFormats2;
            } else if (line2.startsWith(TAG_STREAM_INF)) {
                int width;
                int height;
                float frameRate;
                noClosedCaptions |= line2.contains(ATTR_CLOSED_CAPTIONS_NONE);
                int bitrate = parseIntAttr(line2, REGEX_BANDWIDTH);
                averageBandwidthString = parseOptionalStringAttr(line2, REGEX_AVERAGE_BANDWIDTH, variableDefinitions);
                if (averageBandwidthString != null) {
                    bitrate = Integer.parseInt(averageBandwidthString);
                }
                codecs = parseOptionalStringAttr(line2, REGEX_CODECS, variableDefinitions);
                resolutionString = parseOptionalStringAttr(line2, REGEX_RESOLUTION, variableDefinitions);
                if (resolutionString != null) {
                    String[] widthAndHeight = resolutionString.split("x");
                    int width2 = Integer.parseInt(widthAndHeight[0]);
                    int height2 = Integer.parseInt(widthAndHeight[1]);
                    if (width2 > 0) {
                        if (height2 > 0) {
                            width = width2;
                            height = height2;
                        }
                    }
                    width2 = -1;
                    height2 = -1;
                    width = width2;
                    height = height2;
                } else {
                    width = -1;
                    height = -1;
                }
                format = muxedAudioFormat;
                frameRateString = parseOptionalStringAttr(line2, REGEX_FRAME_RATE, variableDefinitions);
                if (frameRateString != null) {
                    frameRate = Float.parseFloat(frameRateString);
                } else {
                    frameRate = -1.0f;
                }
                frameRateString = parseOptionalStringAttr(line2, REGEX_AUDIO, variableDefinitions);
                if (frameRateString == null || codecs == null) {
                    list = muxedCaptionFormats2;
                } else {
                    list = muxedCaptionFormats2;
                    audioGroupIdToCodecs.put(frameRateString, Util.getCodecsOfType(codecs, 1));
                }
                line = replaceVariableReferences(iterator.next(), variableDefinitions);
                if (variantUrls.add(line)) {
                    hashSet = variantUrls;
                    variants2.add(new HlsUrl(line, Format.createVideoContainerFormat(Integer.toString(variants2.size()), null, MimeTypes.APPLICATION_M3U8, null, codecs, bitrate, width, height, frameRate, null, 0)));
                } else {
                    hashSet = variantUrls;
                }
                variantUrls = hashSet;
                muxedAudioFormat = format;
                muxedCaptionFormats2 = list;
            } else {
                hashSet = variantUrls;
                format = muxedAudioFormat;
                list = muxedCaptionFormats2;
            }
            variantUrls = hashSet;
            muxedAudioFormat = format;
            muxedCaptionFormats2 = list;
        }
        format = muxedAudioFormat;
        list = muxedCaptionFormats2;
        int i = 0;
        while (i < mediaTags2.size()) {
            Format format2;
            String str;
            List<Format> muxedCaptionFormats3;
            String str2;
            frameRateString = (String) mediaTags2.get(i);
            int selectionFlags = parseSelectionFlags(frameRateString);
            String uri = parseOptionalStringAttr(frameRateString, REGEX_URI, variableDefinitions);
            resolutionString = parseStringAttr(frameRateString, REGEX_NAME, variableDefinitions);
            String language = parseOptionalStringAttr(frameRateString, REGEX_LANGUAGE, variableDefinitions);
            averageBandwidthString = parseOptionalStringAttr(frameRateString, REGEX_GROUP_ID, variableDefinitions);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(averageBandwidthString);
            stringBuilder.append(":");
            stringBuilder.append(resolutionString);
            line = stringBuilder.toString();
            codecs = parseStringAttr(frameRateString, REGEX_TYPE, variableDefinitions);
            Object obj = -1;
            mediaTags = mediaTags2;
            int hashCode = codecs.hashCode();
            variants = variants2;
            tags = tags2;
            if (hashCode != -959297733) {
                if (hashCode != -333210994) {
                    if (hashCode == 62628790 && codecs.equals(TYPE_AUDIO)) {
                        obj = null;
                        switch (obj) {
                            case null:
                                frameRateString = (String) audioGroupIdToCodecs.get(averageBandwidthString);
                                format2 = Format.createAudioContainerFormat(line, resolutionString, MimeTypes.APPLICATION_M3U8, frameRateString == null ? MimeTypes.getMediaMimeType(frameRateString) : null, frameRateString, -1, -1, -1, null, selectionFlags, language);
                                if (uri != null) {
                                    audios.add(new HlsUrl(uri, format2));
                                    break;
                                }
                                format = format2;
                                break;
                            case 1:
                                subtitles.add(new HlsUrl(uri, Format.createTextContainerFormat(line, resolutionString, MimeTypes.APPLICATION_M3U8, MimeTypes.TEXT_VTT, null, -1, selectionFlags, language)));
                                break;
                            case 2:
                                codecs = parseStringAttr(frameRateString, REGEX_INSTREAM_ID, variableDefinitions);
                                if (codecs.startsWith("CC")) {
                                    tags2 = MimeTypes.APPLICATION_CEA708;
                                    hashCode = Integer.parseInt(codecs.substring(7));
                                } else {
                                    str = MimeTypes.APPLICATION_CEA608;
                                    hashCode = Integer.parseInt(codecs.substring(2));
                                    tags2 = str;
                                }
                                if (list != null) {
                                    muxedCaptionFormats3 = new ArrayList();
                                } else {
                                    muxedCaptionFormats3 = list;
                                }
                                str2 = frameRateString;
                                muxedCaptionFormats3.add(Format.createTextContainerFormat(line, resolutionString, null, tags2, null, -1, selectionFlags, language, hashCode));
                                list = muxedCaptionFormats3;
                                break;
                            default:
                                break;
                        }
                        i++;
                        mediaTags2 = mediaTags;
                        variants2 = variants;
                        tags2 = tags;
                    }
                } else if (codecs.equals(TYPE_CLOSED_CAPTIONS)) {
                    obj = 2;
                    switch (obj) {
                        case null:
                            frameRateString = (String) audioGroupIdToCodecs.get(averageBandwidthString);
                            if (frameRateString == null) {
                            }
                            format2 = Format.createAudioContainerFormat(line, resolutionString, MimeTypes.APPLICATION_M3U8, frameRateString == null ? MimeTypes.getMediaMimeType(frameRateString) : null, frameRateString, -1, -1, -1, null, selectionFlags, language);
                            if (uri != null) {
                                format = format2;
                                break;
                            }
                            audios.add(new HlsUrl(uri, format2));
                            break;
                        case 1:
                            subtitles.add(new HlsUrl(uri, Format.createTextContainerFormat(line, resolutionString, MimeTypes.APPLICATION_M3U8, MimeTypes.TEXT_VTT, null, -1, selectionFlags, language)));
                            break;
                        case 2:
                            codecs = parseStringAttr(frameRateString, REGEX_INSTREAM_ID, variableDefinitions);
                            if (codecs.startsWith("CC")) {
                                tags2 = MimeTypes.APPLICATION_CEA708;
                                hashCode = Integer.parseInt(codecs.substring(7));
                            } else {
                                str = MimeTypes.APPLICATION_CEA608;
                                hashCode = Integer.parseInt(codecs.substring(2));
                                tags2 = str;
                            }
                            if (list != null) {
                                muxedCaptionFormats3 = list;
                            } else {
                                muxedCaptionFormats3 = new ArrayList();
                            }
                            str2 = frameRateString;
                            muxedCaptionFormats3.add(Format.createTextContainerFormat(line, resolutionString, null, tags2, null, -1, selectionFlags, language, hashCode));
                            list = muxedCaptionFormats3;
                            break;
                        default:
                            break;
                    }
                    i++;
                    mediaTags2 = mediaTags;
                    variants2 = variants;
                    tags2 = tags;
                }
            } else if (codecs.equals(TYPE_SUBTITLES)) {
                obj = 1;
                switch (obj) {
                    case null:
                        frameRateString = (String) audioGroupIdToCodecs.get(averageBandwidthString);
                        if (frameRateString == null) {
                        }
                        format2 = Format.createAudioContainerFormat(line, resolutionString, MimeTypes.APPLICATION_M3U8, frameRateString == null ? MimeTypes.getMediaMimeType(frameRateString) : null, frameRateString, -1, -1, -1, null, selectionFlags, language);
                        if (uri != null) {
                            audios.add(new HlsUrl(uri, format2));
                            break;
                        }
                        format = format2;
                        break;
                    case 1:
                        subtitles.add(new HlsUrl(uri, Format.createTextContainerFormat(line, resolutionString, MimeTypes.APPLICATION_M3U8, MimeTypes.TEXT_VTT, null, -1, selectionFlags, language)));
                        break;
                    case 2:
                        codecs = parseStringAttr(frameRateString, REGEX_INSTREAM_ID, variableDefinitions);
                        if (codecs.startsWith("CC")) {
                            str = MimeTypes.APPLICATION_CEA608;
                            hashCode = Integer.parseInt(codecs.substring(2));
                            tags2 = str;
                        } else {
                            tags2 = MimeTypes.APPLICATION_CEA708;
                            hashCode = Integer.parseInt(codecs.substring(7));
                        }
                        if (list != null) {
                            muxedCaptionFormats3 = new ArrayList();
                        } else {
                            muxedCaptionFormats3 = list;
                        }
                        str2 = frameRateString;
                        muxedCaptionFormats3.add(Format.createTextContainerFormat(line, resolutionString, null, tags2, null, -1, selectionFlags, language, hashCode));
                        list = muxedCaptionFormats3;
                        break;
                    default:
                        break;
                }
                i++;
                mediaTags2 = mediaTags;
                variants2 = variants;
                tags2 = tags;
            }
            switch (obj) {
                case null:
                    frameRateString = (String) audioGroupIdToCodecs.get(averageBandwidthString);
                    if (frameRateString == null) {
                    }
                    format2 = Format.createAudioContainerFormat(line, resolutionString, MimeTypes.APPLICATION_M3U8, frameRateString == null ? MimeTypes.getMediaMimeType(frameRateString) : null, frameRateString, -1, -1, -1, null, selectionFlags, language);
                    if (uri != null) {
                        format = format2;
                        break;
                    }
                    audios.add(new HlsUrl(uri, format2));
                    break;
                case 1:
                    subtitles.add(new HlsUrl(uri, Format.createTextContainerFormat(line, resolutionString, MimeTypes.APPLICATION_M3U8, MimeTypes.TEXT_VTT, null, -1, selectionFlags, language)));
                    break;
                case 2:
                    codecs = parseStringAttr(frameRateString, REGEX_INSTREAM_ID, variableDefinitions);
                    if (codecs.startsWith("CC")) {
                        tags2 = MimeTypes.APPLICATION_CEA708;
                        hashCode = Integer.parseInt(codecs.substring(7));
                    } else {
                        str = MimeTypes.APPLICATION_CEA608;
                        hashCode = Integer.parseInt(codecs.substring(2));
                        tags2 = str;
                    }
                    if (list != null) {
                        muxedCaptionFormats3 = list;
                    } else {
                        muxedCaptionFormats3 = new ArrayList();
                    }
                    str2 = frameRateString;
                    muxedCaptionFormats3.add(Format.createTextContainerFormat(line, resolutionString, null, tags2, null, -1, selectionFlags, language, hashCode));
                    list = muxedCaptionFormats3;
                    break;
                default:
                    break;
            }
            i++;
            mediaTags2 = mediaTags;
            variants2 = variants;
            tags2 = tags;
        }
        tags = tags2;
        mediaTags = mediaTags2;
        variants = variants2;
        if (noClosedCaptions) {
            muxedCaptionFormats = Collections.emptyList();
        } else {
            muxedCaptionFormats = list;
        }
        return new HlsMasterPlaylist(baseUri, tags, variants, audios, subtitles, format, muxedCaptionFormats, hasIndependentSegmentsTag, variableDefinitions);
    }

    private static int parseSelectionFlags(String line) {
        int flags = 0;
        if (parseOptionalBooleanAttribute(line, REGEX_DEFAULT, false)) {
            flags = 0 | 1;
        }
        if (parseOptionalBooleanAttribute(line, REGEX_FORCED, false)) {
            flags |= 2;
        }
        if (parseOptionalBooleanAttribute(line, REGEX_AUTOSELECT, false)) {
            return flags | 4;
        }
        return flags;
    }

    private static HlsMediaPlaylist parseMediaPlaylist(HlsMasterPlaylist masterPlaylist, LineIterator iterator, String baseUri) throws IOException {
        boolean hasIndependentSegmentsTag;
        HlsMasterPlaylist hlsMasterPlaylist = masterPlaylist;
        int playlistType = 0;
        long startOffsetUs = C0555C.TIME_UNSET;
        boolean hasIndependentSegmentsTag2 = hlsMasterPlaylist.hasIndependentSegments;
        HashMap<String, String> variableDefinitions = new HashMap();
        List<Segment> segments = new ArrayList();
        List<String> tags = new ArrayList();
        TreeMap currentSchemeDatas = new TreeMap();
        Segment initializationSegment = null;
        String segmentTitle = "";
        long segmentDurationUs = 0;
        int relativeDiscontinuitySequence = 0;
        long segmentStartTimeUs = 0;
        long segmentByteRangeOffset = 0;
        long segmentByteRangeLength = -1;
        long segmentMediaSequence = 0;
        boolean hasGapTag = false;
        DrmInitData playlistProtectionSchemes = null;
        String encryptionKeyUri = null;
        String encryptionIV = null;
        String encryptionScheme = null;
        DrmInitData cachedDrmInitData = null;
        long targetDurationUs = C0555C.TIME_UNSET;
        boolean hasIndependentSegmentsTag3 = hasIndependentSegmentsTag2;
        boolean hasEndTag = false;
        int playlistDiscontinuitySequence = 0;
        long mediaSequence = 0;
        int version = 1;
        boolean hasDiscontinuitySequence = false;
        long playlistStartTimeUs = 0;
        while (true) {
            hasIndependentSegmentsTag = hasIndependentSegmentsTag3;
            if (!iterator.hasNext()) {
                break;
            }
            String line = iterator.next();
            int version2 = version;
            if (line.startsWith(TAG_PREFIX)) {
                tags.add(line);
            }
            List<String> tags2;
            if (line.startsWith(TAG_PLAYLIST_TYPE)) {
                String playlistTypeString = parseStringAttr(line, REGEX_PLAYLIST_TYPE, variableDefinitions);
                tags2 = tags;
                if ("VOD".equals(playlistTypeString)) {
                    playlistType = 1;
                } else if ("EVENT".equals(playlistTypeString)) {
                    playlistType = 2;
                }
                tags = tags2;
                hasIndependentSegmentsTag3 = hasIndependentSegmentsTag;
                version = version2;
            } else {
                tags2 = tags;
                if (line.startsWith(TAG_START)) {
                    startOffsetUs = (long) (parseDoubleAttr(line, REGEX_TIME_OFFSET) * 4696837146684686336L);
                    tags = tags2;
                    hasIndependentSegmentsTag3 = hasIndependentSegmentsTag;
                    version = version2;
                    mediaSequence = mediaSequence;
                } else {
                    long mediaSequence2 = mediaSequence;
                    String byteRange;
                    if (line.startsWith(TAG_INIT_SEGMENT) != null) {
                        mediaSequence = parseStringAttr(line, REGEX_URI, variableDefinitions);
                        byteRange = parseOptionalStringAttr(line, REGEX_ATTR_BYTERANGE, variableDefinitions);
                        if (byteRange != null) {
                            String[] splitByteRange = byteRange.split("@");
                            segmentByteRangeLength = Long.parseLong(splitByteRange[0]);
                            if (splitByteRange.length > 1) {
                                segmentByteRangeOffset = Long.parseLong(splitByteRange[1]);
                            }
                        }
                        initializationSegment = new Segment(mediaSequence, segmentByteRangeOffset, segmentByteRangeLength);
                        segmentByteRangeOffset = 0;
                        segmentByteRangeLength = -1;
                        tags = tags2;
                        hasIndependentSegmentsTag3 = hasIndependentSegmentsTag;
                        version = version2;
                        mediaSequence = mediaSequence2;
                    } else if (line.startsWith(TAG_TARGET_DURATION) != null) {
                        targetDurationUs = ((long) parseIntAttr(line, REGEX_TARGET_DURATION)) * 1000000;
                        tags = tags2;
                        hasIndependentSegmentsTag3 = hasIndependentSegmentsTag;
                        version = version2;
                        mediaSequence = mediaSequence2;
                    } else if (line.startsWith(TAG_MEDIA_SEQUENCE) != null) {
                        mediaSequence = parseLongAttr(line, REGEX_MEDIA_SEQUENCE);
                        segmentMediaSequence = mediaSequence;
                        tags = tags2;
                        hasIndependentSegmentsTag3 = hasIndependentSegmentsTag;
                        version = version2;
                    } else if (line.startsWith(TAG_VERSION) != null) {
                        version = parseIntAttr(line, REGEX_VERSION);
                        tags = tags2;
                        hasIndependentSegmentsTag3 = hasIndependentSegmentsTag;
                        mediaSequence = mediaSequence2;
                    } else {
                        if (line.startsWith(TAG_DEFINE) != null) {
                            mediaSequence = parseOptionalStringAttr(line, REGEX_IMPORT, variableDefinitions);
                            if (mediaSequence != null) {
                                byteRange = (String) hlsMasterPlaylist.variableDefinitions.get(mediaSequence);
                                if (byteRange != null) {
                                    variableDefinitions.put(mediaSequence, byteRange);
                                }
                            } else {
                                variableDefinitions.put(parseStringAttr(line, REGEX_NAME, variableDefinitions), parseStringAttr(line, REGEX_VALUE, variableDefinitions));
                            }
                        } else if (line.startsWith(TAG_MEDIA_DURATION) != null) {
                            mediaSequence = (long) (parseDoubleAttr(line, REGEX_MEDIA_DURATION) * 4696837146684686336L);
                            segmentTitle = parseOptionalStringAttr(line, REGEX_MEDIA_TITLE, "", variableDefinitions);
                            segmentDurationUs = mediaSequence;
                            tags = tags2;
                            hasIndependentSegmentsTag3 = hasIndependentSegmentsTag;
                            version = version2;
                            mediaSequence = mediaSequence2;
                        } else if (line.startsWith(TAG_KEY) != null) {
                            mediaSequence = parseStringAttr(line, REGEX_METHOD, variableDefinitions);
                            byteRange = parseOptionalStringAttr(line, REGEX_KEYFORMAT, KEYFORMAT_IDENTITY, variableDefinitions);
                            if (METHOD_NONE.equals(mediaSequence)) {
                                currentSchemeDatas.clear();
                                cachedDrmInitData = null;
                                encryptionKeyUri = null;
                                encryptionIV = null;
                                currentSchemeDatas = currentSchemeDatas;
                            } else {
                                String encryptionIV2 = parseOptionalStringAttr(line, REGEX_IV, variableDefinitions);
                                if (!KEYFORMAT_IDENTITY.equals(byteRange)) {
                                    SchemeData schemeData;
                                    if (encryptionScheme == null) {
                                        String str;
                                        if (!METHOD_SAMPLE_AES_CENC.equals(mediaSequence)) {
                                            if (!METHOD_SAMPLE_AES_CTR.equals(mediaSequence)) {
                                                str = C0555C.CENC_TYPE_cbcs;
                                                encryptionScheme = str;
                                            }
                                        }
                                        str = C0555C.CENC_TYPE_cenc;
                                        encryptionScheme = str;
                                    }
                                    if (KEYFORMAT_PLAYREADY.equals(byteRange)) {
                                        schemeData = parsePlayReadySchemeData(line, variableDefinitions);
                                    } else {
                                        schemeData = parseWidevineSchemeData(line, byteRange, variableDefinitions);
                                    }
                                    String encryptionIV3;
                                    if (schemeData != null) {
                                        encryptionIV3 = encryptionIV2;
                                        currentSchemeDatas = currentSchemeDatas;
                                        currentSchemeDatas.put(byteRange, schemeData);
                                        encryptionKeyUri = null;
                                        cachedDrmInitData = null;
                                        encryptionIV = encryptionIV3;
                                    } else {
                                        encryptionIV3 = encryptionIV2;
                                        currentSchemeDatas = currentSchemeDatas;
                                        encryptionKeyUri = null;
                                        encryptionIV = encryptionIV3;
                                    }
                                } else if (METHOD_AES_128.equals(mediaSequence)) {
                                    encryptionIV = encryptionIV2;
                                    encryptionKeyUri = parseStringAttr(line, REGEX_URI, variableDefinitions);
                                    currentSchemeDatas = currentSchemeDatas;
                                } else {
                                    encryptionIV = encryptionIV2;
                                    encryptionKeyUri = null;
                                    currentSchemeDatas = currentSchemeDatas;
                                }
                            }
                            currentSchemeDatas = currentSchemeDatas;
                            tags = tags2;
                            hasIndependentSegmentsTag3 = hasIndependentSegmentsTag;
                            version = version2;
                            mediaSequence = mediaSequence2;
                            hlsMasterPlaylist = masterPlaylist;
                        } else {
                            currentSchemeDatas = currentSchemeDatas;
                            if (line.startsWith(TAG_BYTERANGE) != null) {
                                String[] splitByteRange2 = parseStringAttr(line, REGEX_BYTERANGE, variableDefinitions).split("@");
                                segmentByteRangeLength = Long.parseLong(splitByteRange2[0]);
                                if (splitByteRange2.length > 1) {
                                    segmentByteRangeOffset = Long.parseLong(splitByteRange2[1]);
                                }
                                currentSchemeDatas = currentSchemeDatas;
                                tags = tags2;
                                hasIndependentSegmentsTag3 = hasIndependentSegmentsTag;
                                version = version2;
                                mediaSequence = mediaSequence2;
                                hlsMasterPlaylist = masterPlaylist;
                            } else if (line.startsWith(TAG_DISCONTINUITY_SEQUENCE) != null) {
                                hasDiscontinuitySequence = true;
                                playlistDiscontinuitySequence = Integer.parseInt(line.substring(line.indexOf(58) + 1));
                                currentSchemeDatas = currentSchemeDatas;
                                tags = tags2;
                                hasIndependentSegmentsTag3 = hasIndependentSegmentsTag;
                                version = version2;
                                mediaSequence = mediaSequence2;
                                hlsMasterPlaylist = masterPlaylist;
                            } else if (line.equals(TAG_DISCONTINUITY) != null) {
                                relativeDiscontinuitySequence++;
                                currentSchemeDatas = currentSchemeDatas;
                                tags = tags2;
                                hasIndependentSegmentsTag3 = hasIndependentSegmentsTag;
                                version = version2;
                                mediaSequence = mediaSequence2;
                                hlsMasterPlaylist = masterPlaylist;
                            } else if (line.startsWith(TAG_PROGRAM_DATE_TIME) != null) {
                                if (playlistStartTimeUs == 0) {
                                    playlistStartTimeUs = C0555C.msToUs(Util.parseXsDateTime(line.substring(line.indexOf(58) + 1))) - segmentStartTimeUs;
                                    currentSchemeDatas = currentSchemeDatas;
                                    tags = tags2;
                                    hasIndependentSegmentsTag3 = hasIndependentSegmentsTag;
                                    version = version2;
                                    mediaSequence = mediaSequence2;
                                    hlsMasterPlaylist = masterPlaylist;
                                } else {
                                    currentSchemeDatas = currentSchemeDatas;
                                }
                            } else if (line.equals(TAG_GAP) != null) {
                                hasGapTag = true;
                                currentSchemeDatas = currentSchemeDatas;
                                tags = tags2;
                                hasIndependentSegmentsTag3 = hasIndependentSegmentsTag;
                                version = version2;
                                mediaSequence = mediaSequence2;
                                hlsMasterPlaylist = masterPlaylist;
                            } else if (line.equals(TAG_INDEPENDENT_SEGMENTS) != null) {
                                currentSchemeDatas = currentSchemeDatas;
                                hasIndependentSegmentsTag3 = 1;
                                tags = tags2;
                                version = version2;
                                mediaSequence = mediaSequence2;
                                hlsMasterPlaylist = masterPlaylist;
                            } else if (line.equals(TAG_ENDLIST) != null) {
                                hasEndTag = true;
                                currentSchemeDatas = currentSchemeDatas;
                                tags = tags2;
                                hasIndependentSegmentsTag3 = hasIndependentSegmentsTag;
                                version = version2;
                                mediaSequence = mediaSequence2;
                                hlsMasterPlaylist = masterPlaylist;
                            } else if (line.startsWith("#") == null) {
                                if (encryptionKeyUri == null) {
                                    mediaSequence = null;
                                } else if (encryptionIV != null) {
                                    mediaSequence = encryptionIV;
                                } else {
                                    mediaSequence = Long.toHexString(segmentMediaSequence);
                                }
                                segmentMediaSequence++;
                                if (segmentByteRangeLength == -1) {
                                    segmentByteRangeOffset = 0;
                                }
                                if (cachedDrmInitData != null || currentSchemeDatas.isEmpty()) {
                                    currentSchemeDatas = currentSchemeDatas;
                                } else {
                                    SchemeData[] schemeDatas = (SchemeData[]) currentSchemeDatas.values().toArray(new SchemeData[0]);
                                    DrmInitData cachedDrmInitData2 = new DrmInitData(encryptionScheme, schemeDatas);
                                    SchemeData[] schemeDatas2;
                                    if (playlistProtectionSchemes == null) {
                                        DrmInitData cachedDrmInitData3;
                                        SchemeData[] playlistSchemeDatas = new SchemeData[schemeDatas.length];
                                        currentSchemeDatas = currentSchemeDatas;
                                        currentSchemeDatas = null;
                                        while (true) {
                                            cachedDrmInitData3 = cachedDrmInitData2;
                                            if (currentSchemeDatas >= schemeDatas.length) {
                                                break;
                                            }
                                            schemeDatas2 = schemeDatas;
                                            playlistSchemeDatas[currentSchemeDatas] = schemeDatas[currentSchemeDatas].copyWithData(null);
                                            currentSchemeDatas++;
                                            cachedDrmInitData2 = cachedDrmInitData3;
                                            schemeDatas = schemeDatas2;
                                        }
                                        playlistProtectionSchemes = new DrmInitData(encryptionScheme, playlistSchemeDatas);
                                        cachedDrmInitData = cachedDrmInitData3;
                                    } else {
                                        currentSchemeDatas = currentSchemeDatas;
                                        schemeDatas2 = schemeDatas;
                                        cachedDrmInitData = cachedDrmInitData2;
                                    }
                                }
                                segments.add(new Segment(replaceVariableReferences(line, variableDefinitions), initializationSegment, segmentTitle, segmentDurationUs, relativeDiscontinuitySequence, segmentStartTimeUs, cachedDrmInitData, encryptionKeyUri, mediaSequence, segmentByteRangeOffset, segmentByteRangeLength, hasGapTag));
                                segmentStartTimeUs += segmentDurationUs;
                                segmentDurationUs = 0;
                                segmentTitle = "";
                                if (segmentByteRangeLength != -1) {
                                    segmentByteRangeOffset += segmentByteRangeLength;
                                }
                                segmentByteRangeLength = -1;
                                hasGapTag = false;
                                tags = tags2;
                                hasIndependentSegmentsTag3 = hasIndependentSegmentsTag;
                                version = version2;
                                mediaSequence = mediaSequence2;
                                hlsMasterPlaylist = masterPlaylist;
                            } else {
                                currentSchemeDatas = currentSchemeDatas;
                            }
                        }
                        tags = tags2;
                        hasIndependentSegmentsTag3 = hasIndependentSegmentsTag;
                        version = version2;
                        mediaSequence = mediaSequence2;
                        hlsMasterPlaylist = masterPlaylist;
                    }
                }
            }
        }
        return new HlsMediaPlaylist(playlistType, baseUri, tags, startOffsetUs, playlistStartTimeUs, hasDiscontinuitySequence, playlistDiscontinuitySequence, mediaSequence, version, targetDurationUs, hasIndependentSegmentsTag, hasEndTag, playlistStartTimeUs != 0, playlistProtectionSchemes, segments);
    }

    @Nullable
    private static SchemeData parsePlayReadySchemeData(String line, Map<String, String> variableDefinitions) throws ParserException {
        if (!"1".equals(parseOptionalStringAttr(line, REGEX_KEYFORMATVERSIONS, "1", variableDefinitions))) {
            return null;
        }
        String uriString = parseStringAttr(line, REGEX_URI, variableDefinitions);
        return new SchemeData(C0555C.PLAYREADY_UUID, MimeTypes.VIDEO_MP4, PsshAtomUtil.buildPsshAtom(C0555C.PLAYREADY_UUID, Base64.decode(uriString.substring(uriString.indexOf(44)), 0)));
    }

    @Nullable
    private static SchemeData parseWidevineSchemeData(String line, String keyFormat, Map<String, String> variableDefinitions) throws ParserException {
        if (KEYFORMAT_WIDEVINE_PSSH_BINARY.equals(keyFormat)) {
            String uriString = parseStringAttr(line, REGEX_URI, variableDefinitions);
            return new SchemeData(C0555C.WIDEVINE_UUID, MimeTypes.VIDEO_MP4, Base64.decode(uriString.substring(uriString.indexOf(44)), 0));
        } else if (!KEYFORMAT_WIDEVINE_PSSH_JSON.equals(keyFormat)) {
            return null;
        } else {
            try {
                return new SchemeData(C0555C.WIDEVINE_UUID, "hls", line.getBytes("UTF-8"));
            } catch (Throwable e) {
                throw new ParserException(e);
            }
        }
    }

    private static int parseIntAttr(String line, Pattern pattern) throws ParserException {
        return Integer.parseInt(parseStringAttr(line, pattern, Collections.emptyMap()));
    }

    private static long parseLongAttr(String line, Pattern pattern) throws ParserException {
        return Long.parseLong(parseStringAttr(line, pattern, Collections.emptyMap()));
    }

    private static double parseDoubleAttr(String line, Pattern pattern) throws ParserException {
        return Double.parseDouble(parseStringAttr(line, pattern, Collections.emptyMap()));
    }

    private static String parseStringAttr(String line, Pattern pattern, Map<String, String> variableDefinitions) throws ParserException {
        String value = parseOptionalStringAttr(line, pattern, variableDefinitions);
        if (value != null) {
            return value;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Couldn't match ");
        stringBuilder.append(pattern.pattern());
        stringBuilder.append(" in ");
        stringBuilder.append(line);
        throw new ParserException(stringBuilder.toString());
    }

    @Nullable
    private static String parseOptionalStringAttr(String line, Pattern pattern, Map<String, String> variableDefinitions) {
        return parseOptionalStringAttr(line, pattern, null, variableDefinitions);
    }

    private static String parseOptionalStringAttr(String line, Pattern pattern, String defaultValue, Map<String, String> variableDefinitions) {
        String replaceVariableReferences;
        Matcher matcher = pattern.matcher(line);
        String value = matcher.find() ? matcher.group(1) : defaultValue;
        if (!variableDefinitions.isEmpty()) {
            if (value != null) {
                replaceVariableReferences = replaceVariableReferences(value, variableDefinitions);
                return replaceVariableReferences;
            }
        }
        replaceVariableReferences = value;
        return replaceVariableReferences;
    }

    private static String replaceVariableReferences(String string, Map<String, String> variableDefinitions) {
        Matcher matcher = REGEX_VARIABLE_REFERENCE.matcher(string);
        StringBuffer stringWithReplacements = new StringBuffer();
        while (matcher.find()) {
            String groupName = matcher.group(1);
            if (variableDefinitions.containsKey(groupName)) {
                matcher.appendReplacement(stringWithReplacements, Matcher.quoteReplacement((String) variableDefinitions.get(groupName)));
            }
        }
        matcher.appendTail(stringWithReplacements);
        return stringWithReplacements.toString();
    }

    private static boolean parseOptionalBooleanAttribute(String line, Pattern pattern, boolean defaultValue) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.group(1).equals(BOOLEAN_TRUE);
        }
        return defaultValue;
    }

    private static Pattern compileBooleanAttrPattern(String attribute) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(attribute);
        stringBuilder.append("=(");
        stringBuilder.append(BOOLEAN_FALSE);
        stringBuilder.append("|");
        stringBuilder.append(BOOLEAN_TRUE);
        stringBuilder.append(")");
        return Pattern.compile(stringBuilder.toString());
    }
}
