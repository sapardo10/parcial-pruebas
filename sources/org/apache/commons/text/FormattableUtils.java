package org.apache.commons.text;

import java.util.Formattable;
import java.util.Formatter;

public class FormattableUtils {
    private static final String SIMPLEST_FORMAT = "%s";

    public static String toString(Formattable formattable) {
        return String.format(SIMPLEST_FORMAT, new Object[]{formattable});
    }

    public static Formatter append(CharSequence seq, Formatter formatter, int flags, int width, int precision) {
        return append(seq, formatter, flags, width, precision, ' ', null);
    }

    public static Formatter append(CharSequence seq, Formatter formatter, int flags, int width, int precision, char padChar) {
        return append(seq, formatter, flags, width, precision, padChar, null);
    }

    public static Formatter append(CharSequence seq, Formatter formatter, int flags, int width, int precision, CharSequence ellipsis) {
        return append(seq, formatter, flags, width, precision, ' ', ellipsis);
    }

    public static Formatter append(CharSequence seq, Formatter formatter, int flags, int width, int precision, char padChar, CharSequence ellipsis) {
        boolean leftJustify = true;
        if (ellipsis != null && precision >= 0) {
            if (ellipsis.length() > precision) {
                throw new IllegalArgumentException(String.format("Specified ellipsis '%s' exceeds precision of %s", new Object[]{ellipsis, Integer.valueOf(precision)}));
            }
        }
        StringBuilder buf = new StringBuilder(seq);
        if (precision >= 0 && precision < seq.length()) {
            CharSequence _ellipsis;
            if (ellipsis == null) {
                _ellipsis = "";
            } else {
                _ellipsis = ellipsis;
            }
            buf.replace(precision - _ellipsis.length(), seq.length(), _ellipsis.toString());
        }
        if ((flags & 1) != 1) {
            leftJustify = false;
        }
        for (int i = buf.length(); i < width; i++) {
            buf.insert(leftJustify ? i : 0, padChar);
        }
        formatter.format(buf.toString(), new Object[0]);
        return formatter;
    }
}
