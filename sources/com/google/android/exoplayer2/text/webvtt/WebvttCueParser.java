package com.google.android.exoplayer2.text.webvtt;

import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan.Standard;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import com.google.android.exoplayer2.text.webvtt.WebvttCue.Builder;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public final class WebvttCueParser {
    private static final char CHAR_AMPERSAND = '&';
    private static final char CHAR_GREATER_THAN = '>';
    private static final char CHAR_LESS_THAN = '<';
    private static final char CHAR_SEMI_COLON = ';';
    private static final char CHAR_SLASH = '/';
    private static final char CHAR_SPACE = ' ';
    public static final Pattern CUE_HEADER_PATTERN = Pattern.compile("^(\\S+)\\s+-->\\s+(\\S+)(.*)?$");
    private static final Pattern CUE_SETTING_PATTERN = Pattern.compile("(\\S+?):(\\S+)");
    private static final String ENTITY_AMPERSAND = "amp";
    private static final String ENTITY_GREATER_THAN = "gt";
    private static final String ENTITY_LESS_THAN = "lt";
    private static final String ENTITY_NON_BREAK_SPACE = "nbsp";
    private static final int STYLE_BOLD = 1;
    private static final int STYLE_ITALIC = 2;
    private static final String TAG = "WebvttCueParser";
    private static final String TAG_BOLD = "b";
    private static final String TAG_CLASS = "c";
    private static final String TAG_ITALIC = "i";
    private static final String TAG_LANG = "lang";
    private static final String TAG_UNDERLINE = "u";
    private static final String TAG_VOICE = "v";
    private final StringBuilder textBuilder = new StringBuilder();

    private static final class StartTag {
        private static final String[] NO_CLASSES = new String[0];
        public final String[] classes;
        public final String name;
        public final int position;
        public final String voice;

        private StartTag(String name, int position, String voice, String[] classes) {
            this.position = position;
            this.name = name;
            this.voice = voice;
            this.classes = classes;
        }

        public static StartTag buildStartTag(String fullTagExpression, int position) {
            fullTagExpression = fullTagExpression.trim();
            if (fullTagExpression.isEmpty()) {
                return null;
            }
            String voice;
            String[] classes;
            int voiceStartIndex = fullTagExpression.indexOf(StringUtils.SPACE);
            if (voiceStartIndex == -1) {
                voice = "";
            } else {
                voice = fullTagExpression.substring(voiceStartIndex).trim();
                fullTagExpression = fullTagExpression.substring(0, voiceStartIndex);
            }
            String[] nameAndClasses = Util.split(fullTagExpression, "\\.");
            String name = nameAndClasses[0];
            if (nameAndClasses.length > 1) {
                classes = (String[]) Arrays.copyOfRange(nameAndClasses, 1, nameAndClasses.length);
            } else {
                classes = NO_CLASSES;
            }
            return new StartTag(name, position, voice, classes);
        }

        public static StartTag buildWholeCueVirtualTag() {
            return new StartTag("", 0, "", new String[0]);
        }
    }

    private static final class StyleMatch implements Comparable<StyleMatch> {
        public final int score;
        public final WebvttCssStyle style;

        public StyleMatch(int score, WebvttCssStyle style) {
            this.score = score;
            this.style = style;
        }

        public int compareTo(@NonNull StyleMatch another) {
            return this.score - another.score;
        }
    }

    public boolean parseCue(ParsableByteArray webvttData, Builder builder, List<WebvttCssStyle> styles) {
        String firstLine = webvttData.readLine();
        if (firstLine == null) {
            return false;
        }
        Matcher cueHeaderMatcher = CUE_HEADER_PATTERN.matcher(firstLine);
        if (cueHeaderMatcher.matches()) {
            return parseCue(null, cueHeaderMatcher, webvttData, builder, this.textBuilder, styles);
        }
        String secondLine = webvttData.readLine();
        if (secondLine == null) {
            return false;
        }
        cueHeaderMatcher = CUE_HEADER_PATTERN.matcher(secondLine);
        if (!cueHeaderMatcher.matches()) {
            return false;
        }
        return parseCue(firstLine.trim(), cueHeaderMatcher, webvttData, builder, this.textBuilder, styles);
    }

    static void parseCueSettingsList(String cueSettingsList, Builder builder) {
        Matcher cueSettingMatcher = CUE_SETTING_PATTERN.matcher(cueSettingsList);
        while (cueSettingMatcher.find()) {
            String name = cueSettingMatcher.group(1);
            String value = cueSettingMatcher.group(2);
            try {
                if ("line".equals(name)) {
                    parseLineAttribute(value, builder);
                } else if ("align".equals(name)) {
                    builder.setTextAlignment(parseTextAlignment(value));
                } else if (PodDBAdapter.KEY_POSITION.equals(name)) {
                    parsePositionAttribute(value, builder);
                } else if ("size".equals(name)) {
                    builder.setWidth(WebvttParserUtil.parsePercentage(value));
                } else {
                    String str = TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Unknown cue setting ");
                    stringBuilder.append(name);
                    stringBuilder.append(":");
                    stringBuilder.append(value);
                    Log.m10w(str, stringBuilder.toString());
                }
            } catch (NumberFormatException e) {
                String str2 = TAG;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Skipping bad cue setting: ");
                stringBuilder2.append(cueSettingMatcher.group());
                Log.m10w(str2, stringBuilder2.toString());
            }
        }
    }

    static void parseCueText(String id, String markup, Builder builder, List<WebvttCssStyle> styles) {
        SpannableStringBuilder spannedText = new SpannableStringBuilder();
        ArrayDeque<StartTag> startTagStack = new ArrayDeque();
        List<StyleMatch> scratchStyleMatches = new ArrayList();
        int pos = 0;
        while (pos < markup.length()) {
            char curr = markup.charAt(pos);
            int semiColonEndIndex;
            if (curr == '&') {
                semiColonEndIndex = markup.indexOf(59, pos + 1);
                int spaceEndIndex = markup.indexOf(32, pos + 1);
                int entityEndIndex = semiColonEndIndex == -1 ? spaceEndIndex : spaceEndIndex == -1 ? semiColonEndIndex : Math.min(semiColonEndIndex, spaceEndIndex);
                if (entityEndIndex != -1) {
                    applyEntity(markup.substring(pos + 1, entityEndIndex), spannedText);
                    if (entityEndIndex == spaceEndIndex) {
                        spannedText.append(StringUtils.SPACE);
                    }
                    pos = entityEndIndex + 1;
                } else {
                    spannedText.append(curr);
                    pos++;
                }
            } else if (curr != '<') {
                spannedText.append(curr);
                pos++;
            } else if (pos + 1 >= markup.length()) {
                pos++;
            } else {
                semiColonEndIndex = pos;
                boolean isVoidTag = false;
                int i = 1;
                boolean isClosingTag = markup.charAt(semiColonEndIndex + 1) == '/';
                pos = findEndOfTag(markup, semiColonEndIndex + 1);
                if (markup.charAt(pos - 2) == '/') {
                    isVoidTag = true;
                }
                if (isClosingTag) {
                    i = 2;
                }
                String fullTagExpression = markup.substring(i + semiColonEndIndex, isVoidTag ? pos - 2 : pos - 1);
                String tagName = getTagName(fullTagExpression);
                if (tagName != null) {
                    if (isSupportedTag(tagName)) {
                        if (isClosingTag) {
                            while (!startTagStack.isEmpty()) {
                                StartTag startTag = (StartTag) startTagStack.pop();
                                applySpansForTag(id, startTag, spannedText, styles, scratchStyleMatches);
                                if (startTag.name.equals(tagName)) {
                                    break;
                                }
                            }
                        } else if (!isVoidTag) {
                            startTagStack.push(StartTag.buildStartTag(fullTagExpression, spannedText.length()));
                        }
                    }
                }
            }
        }
        while (!startTagStack.isEmpty()) {
            applySpansForTag(id, (StartTag) startTagStack.pop(), spannedText, styles, scratchStyleMatches);
        }
        applySpansForTag(id, StartTag.buildWholeCueVirtualTag(), spannedText, styles, scratchStyleMatches);
        builder.setText(spannedText);
    }

    private static boolean parseCue(String id, Matcher cueHeaderMatcher, ParsableByteArray webvttData, Builder builder, StringBuilder textBuilder, List<WebvttCssStyle> styles) {
        try {
            builder.setStartTime(WebvttParserUtil.parseTimestampUs(cueHeaderMatcher.group(1))).setEndTime(WebvttParserUtil.parseTimestampUs(cueHeaderMatcher.group(2)));
            parseCueSettingsList(cueHeaderMatcher.group(3), builder);
            textBuilder.setLength(0);
            while (true) {
                CharSequence readLine = webvttData.readLine();
                CharSequence line = readLine;
                if (TextUtils.isEmpty(readLine)) {
                    parseCueText(id, textBuilder.toString(), builder, styles);
                    return true;
                }
                if (textBuilder.length() > 0) {
                    textBuilder.append("\n");
                }
                textBuilder.append(line.trim());
            }
        } catch (NumberFormatException e) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Skipping cue with bad header: ");
            stringBuilder.append(cueHeaderMatcher.group());
            Log.m10w(str, stringBuilder.toString());
            return false;
        }
    }

    private static void parseLineAttribute(String s, Builder builder) throws NumberFormatException {
        int commaIndex = s.indexOf(44);
        if (commaIndex != -1) {
            builder.setLineAnchor(parsePositionAnchor(s.substring(commaIndex + 1)));
            s = s.substring(0, commaIndex);
        } else {
            builder.setLineAnchor(Integer.MIN_VALUE);
        }
        if (s.endsWith("%")) {
            builder.setLine(WebvttParserUtil.parsePercentage(s)).setLineType(0);
            return;
        }
        int lineNumber = Integer.parseInt(s);
        if (lineNumber < 0) {
            lineNumber--;
        }
        builder.setLine((float) lineNumber).setLineType(1);
    }

    private static void parsePositionAttribute(String s, Builder builder) throws NumberFormatException {
        int commaIndex = s.indexOf(44);
        if (commaIndex != -1) {
            builder.setPositionAnchor(parsePositionAnchor(s.substring(commaIndex + 1)));
            s = s.substring(0, commaIndex);
        } else {
            builder.setPositionAnchor(Integer.MIN_VALUE);
        }
        builder.setPosition(WebvttParserUtil.parsePercentage(s));
    }

    private static int parsePositionAnchor(String s) {
        Object obj;
        String str;
        StringBuilder stringBuilder;
        int hashCode = s.hashCode();
        if (hashCode != -1364013995) {
            if (hashCode != -1074341483) {
                if (hashCode != 100571) {
                    if (hashCode == 109757538 && s.equals("start")) {
                        obj = null;
                        switch (obj) {
                            case null:
                                return 0;
                            case 1:
                            case 2:
                                return 1;
                            case 3:
                                return 2;
                            default:
                                str = TAG;
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("Invalid anchor value: ");
                                stringBuilder.append(s);
                                Log.m10w(str, stringBuilder.toString());
                                return Integer.MIN_VALUE;
                        }
                    }
                } else if (s.equals(TtmlNode.END)) {
                    obj = 3;
                    switch (obj) {
                        case null:
                            return 0;
                        case 1:
                        case 2:
                            return 1;
                        case 3:
                            return 2;
                        default:
                            str = TAG;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Invalid anchor value: ");
                            stringBuilder.append(s);
                            Log.m10w(str, stringBuilder.toString());
                            return Integer.MIN_VALUE;
                    }
                }
            } else if (s.equals("middle")) {
                obj = 2;
                switch (obj) {
                    case null:
                        return 0;
                    case 1:
                    case 2:
                        return 1;
                    case 3:
                        return 2;
                    default:
                        str = TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Invalid anchor value: ");
                        stringBuilder.append(s);
                        Log.m10w(str, stringBuilder.toString());
                        return Integer.MIN_VALUE;
                }
            }
        } else if (s.equals(TtmlNode.CENTER)) {
            obj = 1;
            switch (obj) {
                case null:
                    return 0;
                case 1:
                case 2:
                    return 1;
                case 3:
                    return 2;
                default:
                    str = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Invalid anchor value: ");
                    stringBuilder.append(s);
                    Log.m10w(str, stringBuilder.toString());
                    return Integer.MIN_VALUE;
            }
        }
        obj = -1;
        switch (obj) {
            case null:
                return 0;
            case 1:
            case 2:
                return 1;
            case 3:
                return 2;
            default:
                str = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append("Invalid anchor value: ");
                stringBuilder.append(s);
                Log.m10w(str, stringBuilder.toString());
                return Integer.MIN_VALUE;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.text.Layout.Alignment parseTextAlignment(java.lang.String r3) {
        /*
        r0 = r3.hashCode();
        switch(r0) {
            case -1364013995: goto L_0x003c;
            case -1074341483: goto L_0x0032;
            case 100571: goto L_0x0028;
            case 3317767: goto L_0x001e;
            case 108511772: goto L_0x0013;
            case 109757538: goto L_0x0008;
            default: goto L_0x0007;
        };
    L_0x0007:
        goto L_0x0046;
    L_0x0008:
        r0 = "start";
        r0 = r3.equals(r0);
        if (r0 == 0) goto L_0x0007;
    L_0x0011:
        r0 = 0;
        goto L_0x0047;
    L_0x0013:
        r0 = "right";
        r0 = r3.equals(r0);
        if (r0 == 0) goto L_0x0007;
    L_0x001c:
        r0 = 5;
        goto L_0x0047;
    L_0x001e:
        r0 = "left";
        r0 = r3.equals(r0);
        if (r0 == 0) goto L_0x0007;
    L_0x0026:
        r0 = 1;
        goto L_0x0047;
    L_0x0028:
        r0 = "end";
        r0 = r3.equals(r0);
        if (r0 == 0) goto L_0x0007;
    L_0x0030:
        r0 = 4;
        goto L_0x0047;
    L_0x0032:
        r0 = "middle";
        r0 = r3.equals(r0);
        if (r0 == 0) goto L_0x0007;
    L_0x003a:
        r0 = 3;
        goto L_0x0047;
    L_0x003c:
        r0 = "center";
        r0 = r3.equals(r0);
        if (r0 == 0) goto L_0x0007;
    L_0x0044:
        r0 = 2;
        goto L_0x0047;
    L_0x0046:
        r0 = -1;
    L_0x0047:
        switch(r0) {
            case 0: goto L_0x0068;
            case 1: goto L_0x0068;
            case 2: goto L_0x0065;
            case 3: goto L_0x0065;
            case 4: goto L_0x0062;
            case 5: goto L_0x0062;
            default: goto L_0x004a;
        };
    L_0x004a:
        r0 = "WebvttCueParser";
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Invalid alignment value: ";
        r1.append(r2);
        r1.append(r3);
        r1 = r1.toString();
        com.google.android.exoplayer2.util.Log.m10w(r0, r1);
        r0 = 0;
        return r0;
    L_0x0062:
        r0 = android.text.Layout.Alignment.ALIGN_OPPOSITE;
        return r0;
    L_0x0065:
        r0 = android.text.Layout.Alignment.ALIGN_CENTER;
        return r0;
    L_0x0068:
        r0 = android.text.Layout.Alignment.ALIGN_NORMAL;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.webvtt.WebvttCueParser.parseTextAlignment(java.lang.String):android.text.Layout$Alignment");
    }

    private static int findEndOfTag(String markup, int startPos) {
        int index = markup.indexOf(62, startPos);
        return index == -1 ? markup.length() : index + 1;
    }

    private static void applyEntity(String entity, SpannableStringBuilder spannedText) {
        Object obj;
        String str;
        StringBuilder stringBuilder;
        int hashCode = entity.hashCode();
        if (hashCode != 3309) {
            if (hashCode != 3464) {
                if (hashCode != 96708) {
                    if (hashCode == 3374865 && entity.equals(ENTITY_NON_BREAK_SPACE)) {
                        obj = 2;
                        switch (obj) {
                            case null:
                                spannedText.append('<');
                                return;
                            case 1:
                                spannedText.append('>');
                                return;
                            case 2:
                                spannedText.append(CHAR_SPACE);
                                return;
                            case 3:
                                spannedText.append('&');
                                return;
                            default:
                                str = TAG;
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("ignoring unsupported entity: '&");
                                stringBuilder.append(entity);
                                stringBuilder.append(";'");
                                Log.m10w(str, stringBuilder.toString());
                                return;
                        }
                    }
                } else if (entity.equals(ENTITY_AMPERSAND)) {
                    obj = 3;
                    switch (obj) {
                        case null:
                            spannedText.append('<');
                            return;
                        case 1:
                            spannedText.append('>');
                            return;
                        case 2:
                            spannedText.append(CHAR_SPACE);
                            return;
                        case 3:
                            spannedText.append('&');
                            return;
                        default:
                            str = TAG;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("ignoring unsupported entity: '&");
                            stringBuilder.append(entity);
                            stringBuilder.append(";'");
                            Log.m10w(str, stringBuilder.toString());
                            return;
                    }
                }
            } else if (entity.equals(ENTITY_LESS_THAN)) {
                obj = null;
                switch (obj) {
                    case null:
                        spannedText.append('<');
                        return;
                    case 1:
                        spannedText.append('>');
                        return;
                    case 2:
                        spannedText.append(CHAR_SPACE);
                        return;
                    case 3:
                        spannedText.append('&');
                        return;
                    default:
                        str = TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("ignoring unsupported entity: '&");
                        stringBuilder.append(entity);
                        stringBuilder.append(";'");
                        Log.m10w(str, stringBuilder.toString());
                        return;
                }
            }
        } else if (entity.equals(ENTITY_GREATER_THAN)) {
            obj = 1;
            switch (obj) {
                case null:
                    spannedText.append('<');
                    return;
                case 1:
                    spannedText.append('>');
                    return;
                case 2:
                    spannedText.append(CHAR_SPACE);
                    return;
                case 3:
                    spannedText.append('&');
                    return;
                default:
                    str = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("ignoring unsupported entity: '&");
                    stringBuilder.append(entity);
                    stringBuilder.append(";'");
                    Log.m10w(str, stringBuilder.toString());
                    return;
            }
        }
        obj = -1;
        switch (obj) {
            case null:
                spannedText.append('<');
                return;
            case 1:
                spannedText.append('>');
                return;
            case 2:
                spannedText.append(CHAR_SPACE);
                return;
            case 3:
                spannedText.append('&');
                return;
            default:
                str = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append("ignoring unsupported entity: '&");
                stringBuilder.append(entity);
                stringBuilder.append(";'");
                Log.m10w(str, stringBuilder.toString());
                return;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean isSupportedTag(java.lang.String r3) {
        /*
        r0 = r3.hashCode();
        r1 = 1;
        r2 = 0;
        switch(r0) {
            case 98: goto L_0x003e;
            case 99: goto L_0x0034;
            case 105: goto L_0x002a;
            case 117: goto L_0x001f;
            case 118: goto L_0x0014;
            case 3314158: goto L_0x000a;
            default: goto L_0x0009;
        };
    L_0x0009:
        goto L_0x0048;
    L_0x000a:
        r0 = "lang";
        r0 = r3.equals(r0);
        if (r0 == 0) goto L_0x0009;
    L_0x0012:
        r0 = 3;
        goto L_0x0049;
    L_0x0014:
        r0 = "v";
        r0 = r3.equals(r0);
        if (r0 == 0) goto L_0x0009;
    L_0x001d:
        r0 = 5;
        goto L_0x0049;
    L_0x001f:
        r0 = "u";
        r0 = r3.equals(r0);
        if (r0 == 0) goto L_0x0009;
    L_0x0028:
        r0 = 4;
        goto L_0x0049;
    L_0x002a:
        r0 = "i";
        r0 = r3.equals(r0);
        if (r0 == 0) goto L_0x0009;
    L_0x0032:
        r0 = 2;
        goto L_0x0049;
    L_0x0034:
        r0 = "c";
        r0 = r3.equals(r0);
        if (r0 == 0) goto L_0x0009;
    L_0x003c:
        r0 = 1;
        goto L_0x0049;
    L_0x003e:
        r0 = "b";
        r0 = r3.equals(r0);
        if (r0 == 0) goto L_0x0009;
    L_0x0046:
        r0 = 0;
        goto L_0x0049;
    L_0x0048:
        r0 = -1;
    L_0x0049:
        switch(r0) {
            case 0: goto L_0x004d;
            case 1: goto L_0x004d;
            case 2: goto L_0x004d;
            case 3: goto L_0x004d;
            case 4: goto L_0x004d;
            case 5: goto L_0x004d;
            default: goto L_0x004c;
        };
    L_0x004c:
        return r2;
    L_0x004d:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.webvtt.WebvttCueParser.isSupportedTag(java.lang.String):boolean");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void applySpansForTag(java.lang.String r6, com.google.android.exoplayer2.text.webvtt.WebvttCueParser.StartTag r7, android.text.SpannableStringBuilder r8, java.util.List<com.google.android.exoplayer2.text.webvtt.WebvttCssStyle> r9, java.util.List<com.google.android.exoplayer2.text.webvtt.WebvttCueParser.StyleMatch> r10) {
        /*
        r0 = r7.position;
        r1 = r8.length();
        r2 = r7.name;
        r3 = r2.hashCode();
        r4 = 2;
        r5 = 1;
        switch(r3) {
            case 0: goto L_0x0050;
            case 98: goto L_0x0046;
            case 99: goto L_0x003c;
            case 105: goto L_0x0032;
            case 117: goto L_0x0027;
            case 118: goto L_0x001c;
            case 3314158: goto L_0x0012;
            default: goto L_0x0011;
        };
    L_0x0011:
        goto L_0x005a;
    L_0x0012:
        r3 = "lang";
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x0011;
    L_0x001a:
        r2 = 4;
        goto L_0x005b;
    L_0x001c:
        r3 = "v";
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x0011;
    L_0x0025:
        r2 = 5;
        goto L_0x005b;
    L_0x0027:
        r3 = "u";
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x0011;
    L_0x0030:
        r2 = 2;
        goto L_0x005b;
    L_0x0032:
        r3 = "i";
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x0011;
    L_0x003a:
        r2 = 1;
        goto L_0x005b;
    L_0x003c:
        r3 = "c";
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x0011;
    L_0x0044:
        r2 = 3;
        goto L_0x005b;
    L_0x0046:
        r3 = "b";
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x0011;
    L_0x004e:
        r2 = 0;
        goto L_0x005b;
    L_0x0050:
        r3 = "";
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x0011;
    L_0x0058:
        r2 = 6;
        goto L_0x005b;
    L_0x005a:
        r2 = -1;
    L_0x005b:
        r3 = 33;
        switch(r2) {
            case 0: goto L_0x0074;
            case 1: goto L_0x006b;
            case 2: goto L_0x0062;
            case 3: goto L_0x0061;
            case 4: goto L_0x0061;
            case 5: goto L_0x0061;
            case 6: goto L_0x0061;
            default: goto L_0x0060;
        };
    L_0x0060:
        return;
    L_0x0061:
        goto L_0x007d;
    L_0x0062:
        r2 = new android.text.style.UnderlineSpan;
        r2.<init>();
        r8.setSpan(r2, r0, r1, r3);
        goto L_0x007d;
    L_0x006b:
        r2 = new android.text.style.StyleSpan;
        r2.<init>(r4);
        r8.setSpan(r2, r0, r1, r3);
        goto L_0x007d;
    L_0x0074:
        r2 = new android.text.style.StyleSpan;
        r2.<init>(r5);
        r8.setSpan(r2, r0, r1, r3);
    L_0x007d:
        r10.clear();
        getApplicableStyles(r9, r6, r7, r10);
        r2 = r10.size();
        r3 = 0;
    L_0x0088:
        if (r3 >= r2) goto L_0x0098;
    L_0x008a:
        r4 = r10.get(r3);
        r4 = (com.google.android.exoplayer2.text.webvtt.WebvttCueParser.StyleMatch) r4;
        r4 = r4.style;
        applyStyleToText(r8, r4, r0, r1);
        r3 = r3 + 1;
        goto L_0x0088;
    L_0x0098:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.webvtt.WebvttCueParser.applySpansForTag(java.lang.String, com.google.android.exoplayer2.text.webvtt.WebvttCueParser$StartTag, android.text.SpannableStringBuilder, java.util.List, java.util.List):void");
    }

    private static void applyStyleToText(SpannableStringBuilder spannedText, WebvttCssStyle style, int start, int end) {
        if (style != null) {
            if (style.getStyle() != -1) {
                spannedText.setSpan(new StyleSpan(style.getStyle()), start, end, 33);
            }
            if (style.isLinethrough()) {
                spannedText.setSpan(new StrikethroughSpan(), start, end, 33);
            }
            if (style.isUnderline()) {
                spannedText.setSpan(new UnderlineSpan(), start, end, 33);
            }
            if (style.hasFontColor()) {
                spannedText.setSpan(new ForegroundColorSpan(style.getFontColor()), start, end, 33);
            }
            if (style.hasBackgroundColor()) {
                spannedText.setSpan(new BackgroundColorSpan(style.getBackgroundColor()), start, end, 33);
            }
            if (style.getFontFamily() != null) {
                spannedText.setSpan(new TypefaceSpan(style.getFontFamily()), start, end, 33);
            }
            if (style.getTextAlign() != null) {
                spannedText.setSpan(new Standard(style.getTextAlign()), start, end, 33);
            }
            switch (style.getFontSizeUnit()) {
                case 1:
                    spannedText.setSpan(new AbsoluteSizeSpan((int) style.getFontSize(), true), start, end, 33);
                    break;
                case 2:
                    spannedText.setSpan(new RelativeSizeSpan(style.getFontSize()), start, end, 33);
                    break;
                case 3:
                    spannedText.setSpan(new RelativeSizeSpan(style.getFontSize() / 100.0f), start, end, 33);
                    break;
                default:
                    break;
            }
        }
    }

    private static String getTagName(String tagExpression) {
        tagExpression = tagExpression.trim();
        if (tagExpression.isEmpty()) {
            return null;
        }
        return Util.splitAtFirst(tagExpression, "[ \\.]")[0];
    }

    private static void getApplicableStyles(List<WebvttCssStyle> declaredStyles, String id, StartTag tag, List<StyleMatch> output) {
        int styleCount = declaredStyles.size();
        for (int i = 0; i < styleCount; i++) {
            WebvttCssStyle style = (WebvttCssStyle) declaredStyles.get(i);
            int score = style.getSpecificityScore(id, tag.name, tag.classes, tag.voice);
            if (score > 0) {
                output.add(new StyleMatch(score, style));
            }
        }
        Collections.sort(output);
    }
}
