package com.google.android.exoplayer2.text.webvtt;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WebvttParserUtil {
    private static final Pattern COMMENT = Pattern.compile("^NOTE(( |\t).*)?$");
    private static final String WEBVTT_HEADER = "WEBVTT";

    private WebvttParserUtil() {
    }

    public static void validateWebvttHeaderLine(ParsableByteArray input) throws ParserException {
        int startPosition = input.getPosition();
        if (!isWebvttHeaderLine(input)) {
            input.setPosition(startPosition);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Expected WEBVTT. Got ");
            stringBuilder.append(input.readLine());
            throw new ParserException(stringBuilder.toString());
        }
    }

    public static boolean isWebvttHeaderLine(ParsableByteArray input) {
        String line = input.readLine();
        return line != null && line.startsWith(WEBVTT_HEADER);
    }

    public static long parseTimestampUs(String timestamp) throws NumberFormatException {
        long value = 0;
        String[] parts = Util.splitAtFirst(timestamp, "\\.");
        int i = 0;
        String[] subparts = Util.split(parts[0], ":");
        while (i < subparts.length) {
            value = (60 * value) + Long.parseLong(subparts[i]);
            i++;
        }
        value *= 1000;
        if (parts.length == 2) {
            value += Long.parseLong(parts[1]);
        }
        return 1000 * value;
    }

    public static float parsePercentage(String s) throws NumberFormatException {
        if (s.endsWith("%")) {
            return Float.parseFloat(s.substring(0, s.length() - 1)) / 100.0f;
        }
        throw new NumberFormatException("Percentages must end with %");
    }

    public static Matcher findNextCueHeader(ParsableByteArray input) {
        while (true) {
            String readLine = input.readLine();
            String line = readLine;
            if (readLine == null) {
                return null;
            }
            if (COMMENT.matcher(line).matches()) {
                while (true) {
                    readLine = input.readLine();
                    line = readLine;
                    if (readLine == null || line.isEmpty()) {
                    }
                }
            } else {
                Matcher cueHeaderMatcher = WebvttCueParser.CUE_HEADER_PATTERN.matcher(line);
                if (cueHeaderMatcher.matches()) {
                    return cueHeaderMatcher;
                }
            }
        }
    }
}
