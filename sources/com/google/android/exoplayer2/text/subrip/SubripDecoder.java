package com.google.android.exoplayer2.text.subrip;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.SimpleSubtitleDecoder;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.LongArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SubripDecoder extends SimpleSubtitleDecoder {
    private static final String ALIGN_BOTTOM_LEFT = "{\\an1}";
    private static final String ALIGN_BOTTOM_MID = "{\\an2}";
    private static final String ALIGN_BOTTOM_RIGHT = "{\\an3}";
    private static final String ALIGN_MID_LEFT = "{\\an4}";
    private static final String ALIGN_MID_MID = "{\\an5}";
    private static final String ALIGN_MID_RIGHT = "{\\an6}";
    private static final String ALIGN_TOP_LEFT = "{\\an7}";
    private static final String ALIGN_TOP_MID = "{\\an8}";
    private static final String ALIGN_TOP_RIGHT = "{\\an9}";
    static final float END_FRACTION = 0.92f;
    static final float MID_FRACTION = 0.5f;
    static final float START_FRACTION = 0.08f;
    private static final String SUBRIP_ALIGNMENT_TAG = "\\{\\\\an[1-9]\\}";
    private static final Pattern SUBRIP_TAG_PATTERN = Pattern.compile("\\{\\\\.*?\\}");
    private static final String SUBRIP_TIMECODE = "(?:(\\d+):)?(\\d+):(\\d+),(\\d+)";
    private static final Pattern SUBRIP_TIMING_LINE = Pattern.compile("\\s*((?:(\\d+):)?(\\d+):(\\d+),(\\d+))\\s*-->\\s*((?:(\\d+):)?(\\d+):(\\d+),(\\d+))?\\s*");
    private static final String TAG = "SubripDecoder";
    private final ArrayList<String> tags = new ArrayList();
    private final StringBuilder textBuilder = new StringBuilder();

    public SubripDecoder() {
        super(TAG);
    }

    protected SubripSubtitle decode(byte[] bytes, int length, boolean reset) {
        Cue[] cuesArray;
        ArrayList<Cue> cues = new ArrayList();
        LongArray cueTimesUs = new LongArray();
        ParsableByteArray subripData = new ParsableByteArray(bytes, length);
        while (true) {
            String readLine = subripData.readLine();
            String currentLine = readLine;
            if (readLine == null) {
                break;
            } else if (currentLine.length() != 0) {
                try {
                    Integer.parseInt(currentLine);
                    boolean haveEndTimecode = false;
                    currentLine = subripData.readLine();
                    if (currentLine == null) {
                        break;
                    }
                    Matcher matcher = SUBRIP_TIMING_LINE.matcher(currentLine);
                    if (matcher.matches()) {
                        cueTimesUs.add(parseTimecode(matcher, 1));
                        if (!TextUtils.isEmpty(matcher.group(6))) {
                            haveEndTimecode = true;
                            cueTimesUs.add(parseTimecode(matcher, 6));
                        }
                        this.textBuilder.setLength(0);
                        this.tags.clear();
                        while (true) {
                            CharSequence readLine2 = subripData.readLine();
                            CharSequence currentLine2 = readLine2;
                            if (TextUtils.isEmpty(readLine2)) {
                                break;
                            }
                            if (this.textBuilder.length() > 0) {
                                this.textBuilder.append("<br>");
                            }
                            this.textBuilder.append(processLine(currentLine2, this.tags));
                        }
                        Spanned text = Html.fromHtml(this.textBuilder.toString());
                        String alignmentTag = null;
                        for (int i = 0; i < this.tags.size(); i++) {
                            String tag = (String) this.tags.get(i);
                            if (tag.matches(SUBRIP_ALIGNMENT_TAG)) {
                                alignmentTag = tag;
                                break;
                            }
                        }
                        cues.add(buildCue(text, alignmentTag));
                        if (haveEndTimecode) {
                            cues.add(null);
                        }
                    } else {
                        String str = TAG;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Skipping invalid timing: ");
                        stringBuilder.append(currentLine);
                        Log.m10w(str, stringBuilder.toString());
                    }
                } catch (NumberFormatException e) {
                    String str2 = TAG;
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("Skipping invalid index: ");
                    stringBuilder2.append(currentLine);
                    Log.m10w(str2, stringBuilder2.toString());
                }
            }
            cuesArray = new Cue[cues.size()];
            cues.toArray(cuesArray);
            return new SubripSubtitle(cuesArray, cueTimesUs.toArray());
        }
        Log.m10w(TAG, "Unexpected end");
        cuesArray = new Cue[cues.size()];
        cues.toArray(cuesArray);
        return new SubripSubtitle(cuesArray, cueTimesUs.toArray());
    }

    private String processLine(String line, ArrayList<String> tags) {
        line = line.trim();
        int removedCharacterCount = 0;
        StringBuilder processedLine = new StringBuilder(line);
        Matcher matcher = SUBRIP_TAG_PATTERN.matcher(line);
        while (matcher.find()) {
            String tag = matcher.group();
            tags.add(tag);
            int start = matcher.start() - removedCharacterCount;
            int tagLength = tag.length();
            processedLine.replace(start, start + tagLength, "");
            removedCharacterCount += tagLength;
        }
        return processedLine.toString();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.google.android.exoplayer2.text.Cue buildCue(android.text.Spanned r15, @android.support.annotation.Nullable java.lang.String r16) {
        /*
        r14 = this;
        r0 = r16;
        if (r0 != 0) goto L_0x000b;
    L_0x0004:
        r1 = new com.google.android.exoplayer2.text.Cue;
        r11 = r15;
        r1.<init>(r15);
        return r1;
    L_0x000b:
        r11 = r15;
        r1 = r16.hashCode();
        r2 = 5;
        r3 = 8;
        r4 = 2;
        r5 = 4;
        r6 = 7;
        r7 = 1;
        r8 = 3;
        r9 = 6;
        r10 = 0;
        r12 = -1;
        switch(r1) {
            case -685620710: goto L_0x0078;
            case -685620679: goto L_0x006d;
            case -685620648: goto L_0x0062;
            case -685620617: goto L_0x0057;
            case -685620586: goto L_0x004c;
            case -685620555: goto L_0x0041;
            case -685620524: goto L_0x0036;
            case -685620493: goto L_0x002a;
            case -685620462: goto L_0x001f;
            default: goto L_0x001e;
        };
    L_0x001e:
        goto L_0x0083;
    L_0x001f:
        r1 = "{\\an9}";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x001e;
    L_0x0028:
        r1 = 5;
        goto L_0x0084;
    L_0x002a:
        r1 = "{\\an8}";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x001e;
    L_0x0033:
        r1 = 8;
        goto L_0x0084;
    L_0x0036:
        r1 = "{\\an7}";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x001e;
    L_0x003f:
        r1 = 2;
        goto L_0x0084;
    L_0x0041:
        r1 = "{\\an6}";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x001e;
    L_0x004a:
        r1 = 4;
        goto L_0x0084;
    L_0x004c:
        r1 = "{\\an5}";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x001e;
    L_0x0055:
        r1 = 7;
        goto L_0x0084;
    L_0x0057:
        r1 = "{\\an4}";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x001e;
    L_0x0060:
        r1 = 1;
        goto L_0x0084;
    L_0x0062:
        r1 = "{\\an3}";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x001e;
    L_0x006b:
        r1 = 3;
        goto L_0x0084;
    L_0x006d:
        r1 = "{\\an2}";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x001e;
    L_0x0076:
        r1 = 6;
        goto L_0x0084;
    L_0x0078:
        r1 = "{\\an1}";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x001e;
    L_0x0081:
        r1 = 0;
        goto L_0x0084;
    L_0x0083:
        r1 = -1;
    L_0x0084:
        switch(r1) {
            case 0: goto L_0x008b;
            case 1: goto L_0x008b;
            case 2: goto L_0x008b;
            case 3: goto L_0x0089;
            case 4: goto L_0x0089;
            case 5: goto L_0x0089;
            default: goto L_0x0087;
        };
    L_0x0087:
        r1 = 1;
        goto L_0x008d;
    L_0x0089:
        r1 = 2;
        goto L_0x008d;
    L_0x008b:
        r1 = 0;
    L_0x008d:
        r13 = r16.hashCode();
        switch(r13) {
            case -685620710: goto L_0x00ed;
            case -685620679: goto L_0x00e2;
            case -685620648: goto L_0x00d7;
            case -685620617: goto L_0x00cc;
            case -685620586: goto L_0x00c1;
            case -685620555: goto L_0x00b5;
            case -685620524: goto L_0x00aa;
            case -685620493: goto L_0x009f;
            case -685620462: goto L_0x0095;
            default: goto L_0x0094;
        };
    L_0x0094:
        goto L_0x00f8;
    L_0x0095:
        r3 = "{\\an9}";
        r3 = r0.equals(r3);
        if (r3 == 0) goto L_0x0094;
    L_0x009e:
        goto L_0x00f9;
    L_0x009f:
        r2 = "{\\an8}";
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x0094;
    L_0x00a8:
        r2 = 4;
        goto L_0x00f9;
    L_0x00aa:
        r2 = "{\\an7}";
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x0094;
    L_0x00b3:
        r2 = 3;
        goto L_0x00f9;
    L_0x00b5:
        r2 = "{\\an6}";
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x0094;
    L_0x00be:
        r2 = 8;
        goto L_0x00f9;
    L_0x00c1:
        r2 = "{\\an5}";
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x0094;
    L_0x00ca:
        r2 = 7;
        goto L_0x00f9;
    L_0x00cc:
        r2 = "{\\an4}";
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x0094;
    L_0x00d5:
        r2 = 6;
        goto L_0x00f9;
    L_0x00d7:
        r2 = "{\\an3}";
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x0094;
    L_0x00e0:
        r2 = 2;
        goto L_0x00f9;
    L_0x00e2:
        r2 = "{\\an2}";
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x0094;
    L_0x00eb:
        r2 = 1;
        goto L_0x00f9;
    L_0x00ed:
        r2 = "{\\an1}";
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x0094;
    L_0x00f6:
        r2 = 0;
        goto L_0x00f9;
    L_0x00f8:
        r2 = -1;
    L_0x00f9:
        switch(r2) {
            case 0: goto L_0x0102;
            case 1: goto L_0x0102;
            case 2: goto L_0x0102;
            case 3: goto L_0x00ff;
            case 4: goto L_0x00ff;
            case 5: goto L_0x00ff;
            default: goto L_0x00fc;
        };
    L_0x00fc:
        r2 = 1;
        r12 = r2;
        goto L_0x0104;
    L_0x00ff:
        r2 = 0;
        r12 = r2;
        goto L_0x0104;
    L_0x0102:
        r2 = 2;
        r12 = r2;
    L_0x0104:
        r13 = new com.google.android.exoplayer2.text.Cue;
        r4 = 0;
        r5 = getFractionalPositionForAnchorType(r12);
        r6 = 0;
        r8 = getFractionalPositionForAnchorType(r1);
        r10 = 1;
        r2 = r13;
        r3 = r15;
        r7 = r12;
        r9 = r1;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10);
        return r13;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.subrip.SubripDecoder.buildCue(android.text.Spanned, java.lang.String):com.google.android.exoplayer2.text.Cue");
    }

    private static long parseTimecode(Matcher matcher, int groupOffset) {
        return 1000 * ((((((Long.parseLong(matcher.group(groupOffset + 1)) * 60) * 60) * 1000) + ((Long.parseLong(matcher.group(groupOffset + 2)) * 60) * 1000)) + (Long.parseLong(matcher.group(groupOffset + 3)) * 1000)) + Long.parseLong(matcher.group(groupOffset + 4)));
    }

    static float getFractionalPositionForAnchorType(int anchorType) {
        switch (anchorType) {
            case 0:
                return 0.08f;
            case 1:
                return MID_FRACTION;
            default:
                return END_FRACTION;
        }
    }
}
