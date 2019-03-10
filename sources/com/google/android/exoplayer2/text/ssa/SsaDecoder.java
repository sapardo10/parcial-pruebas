package com.google.android.exoplayer2.text.ssa;

import android.text.TextUtils;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.SimpleSubtitleDecoder;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.LongArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SsaDecoder extends SimpleSubtitleDecoder {
    private static final String DIALOGUE_LINE_PREFIX = "Dialogue: ";
    private static final String FORMAT_LINE_PREFIX = "Format: ";
    private static final Pattern SSA_TIMECODE_PATTERN = Pattern.compile("(?:(\\d+):)?(\\d+):(\\d+)(?::|\\.)(\\d+)");
    private static final String TAG = "SsaDecoder";
    private int formatEndIndex;
    private int formatKeyCount;
    private int formatStartIndex;
    private int formatTextIndex;
    private final boolean haveInitializationData;

    public SsaDecoder() {
        this(null);
    }

    public SsaDecoder(List<byte[]> initializationData) {
        super(TAG);
        if (initializationData == null || initializationData.isEmpty()) {
            this.haveInitializationData = false;
            return;
        }
        this.haveInitializationData = true;
        String formatLine = Util.fromUtf8Bytes((byte[]) initializationData.get(0));
        Assertions.checkArgument(formatLine.startsWith(FORMAT_LINE_PREFIX));
        parseFormatLine(formatLine);
        parseHeader(new ParsableByteArray((byte[]) initializationData.get(1)));
    }

    protected SsaSubtitle decode(byte[] bytes, int length, boolean reset) {
        ArrayList<Cue> cues = new ArrayList();
        LongArray cueTimesUs = new LongArray();
        ParsableByteArray data = new ParsableByteArray(bytes, length);
        if (!this.haveInitializationData) {
            parseHeader(data);
        }
        parseEventBody(data, cues, cueTimesUs);
        Cue[] cuesArray = new Cue[cues.size()];
        cues.toArray(cuesArray);
        return new SsaSubtitle(cuesArray, cueTimesUs.toArray());
    }

    private void parseHeader(ParsableByteArray data) {
        while (true) {
            String readLine = data.readLine();
            String currentLine = readLine;
            if (readLine == null || currentLine.startsWith("[Events]")) {
                return;
            }
        }
    }

    private void parseEventBody(ParsableByteArray data, List<Cue> cues, LongArray cueTimesUs) {
        while (true) {
            String readLine = data.readLine();
            String currentLine = readLine;
            if (readLine == null) {
                return;
            }
            if (!this.haveInitializationData && currentLine.startsWith(FORMAT_LINE_PREFIX)) {
                parseFormatLine(currentLine);
            } else if (currentLine.startsWith(DIALOGUE_LINE_PREFIX)) {
                parseDialogueLine(currentLine, cues, cueTimesUs);
            }
        }
    }

    private void parseFormatLine(String formatLine) {
        String[] values = TextUtils.split(formatLine.substring(FORMAT_LINE_PREFIX.length()), ",");
        this.formatKeyCount = values.length;
        this.formatStartIndex = -1;
        this.formatEndIndex = -1;
        this.formatTextIndex = -1;
        int i = 0;
        while (true) {
            int i2 = 0;
            if (i < this.formatKeyCount) {
                String key = Util.toLowerInvariant(values[i].trim());
                int hashCode = key.hashCode();
                if (hashCode != 100571) {
                    if (hashCode != 3556653) {
                        if (hashCode == 109757538 && key.equals("start")) {
                            switch (i2) {
                                case 0:
                                    this.formatStartIndex = i;
                                    break;
                                case 1:
                                    this.formatEndIndex = i;
                                    break;
                                case 2:
                                    this.formatTextIndex = i;
                                    break;
                                default:
                                    break;
                            }
                            i++;
                        }
                    } else if (key.equals("text")) {
                        i2 = 2;
                        switch (i2) {
                            case 0:
                                this.formatStartIndex = i;
                                break;
                            case 1:
                                this.formatEndIndex = i;
                                break;
                            case 2:
                                this.formatTextIndex = i;
                                break;
                            default:
                                break;
                        }
                        i++;
                    }
                } else if (key.equals(TtmlNode.END)) {
                    i2 = 1;
                    switch (i2) {
                        case 0:
                            this.formatStartIndex = i;
                            break;
                        case 1:
                            this.formatEndIndex = i;
                            break;
                        case 2:
                            this.formatTextIndex = i;
                            break;
                        default:
                            break;
                    }
                    i++;
                }
                i2 = -1;
                switch (i2) {
                    case 0:
                        this.formatStartIndex = i;
                        break;
                    case 1:
                        this.formatEndIndex = i;
                        break;
                    case 2:
                        this.formatTextIndex = i;
                        break;
                    default:
                        break;
                }
                i++;
            } else {
                if (!(this.formatStartIndex == -1 || this.formatEndIndex == -1)) {
                    if (this.formatTextIndex != -1) {
                        return;
                    }
                }
                this.formatKeyCount = 0;
                return;
            }
        }
    }

    private void parseDialogueLine(String dialogueLine, List<Cue> cues, LongArray cueTimesUs) {
        if (this.formatKeyCount == 0) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Skipping dialogue line before complete format: ");
            stringBuilder.append(dialogueLine);
            Log.m10w(str, stringBuilder.toString());
            return;
        }
        String[] lineValues = dialogueLine.substring(DIALOGUE_LINE_PREFIX.length()).split(",", this.formatKeyCount);
        if (lineValues.length != this.formatKeyCount) {
            String str2 = TAG;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Skipping dialogue line with fewer columns than format: ");
            stringBuilder2.append(dialogueLine);
            Log.m10w(str2, stringBuilder2.toString());
            return;
        }
        long startTimeUs = parseTimecodeUs(lineValues[this.formatStartIndex]);
        if (startTimeUs == C0555C.TIME_UNSET) {
            String str3 = TAG;
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("Skipping invalid timing: ");
            stringBuilder3.append(dialogueLine);
            Log.m10w(str3, stringBuilder3.toString());
            return;
        }
        long endTimeUs = C0555C.TIME_UNSET;
        String endTimeString = lineValues[this.formatEndIndex];
        if (!endTimeString.trim().isEmpty()) {
            endTimeUs = parseTimecodeUs(endTimeString);
            if (endTimeUs == C0555C.TIME_UNSET) {
                str3 = TAG;
                stringBuilder3 = new StringBuilder();
                stringBuilder3.append("Skipping invalid timing: ");
                stringBuilder3.append(dialogueLine);
                Log.m10w(str3, stringBuilder3.toString());
                return;
            }
        }
        cues.add(new Cue(lineValues[this.formatTextIndex].replaceAll("\\{.*?\\}", "").replaceAll("\\\\N", "\n").replaceAll("\\\\n", "\n")));
        cueTimesUs.add(startTimeUs);
        if (endTimeUs != C0555C.TIME_UNSET) {
            cues.add(null);
            cueTimesUs.add(endTimeUs);
        }
    }

    public static long parseTimecodeUs(String timeString) {
        Matcher matcher = SSA_TIMECODE_PATTERN.matcher(timeString);
        if (matcher.matches()) {
            return (((((Long.parseLong(matcher.group(1)) * 60) * 60) * 1000000) + ((Long.parseLong(matcher.group(2)) * 60) * 1000000)) + (Long.parseLong(matcher.group(3)) * 1000000)) + (Long.parseLong(matcher.group(4)) * 10000);
        }
        return C0555C.TIME_UNSET;
    }
}
