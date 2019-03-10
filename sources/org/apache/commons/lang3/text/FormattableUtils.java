package org.apache.commons.lang3.text;

import java.util.Formattable;
import java.util.Formatter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;

@Deprecated
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
        boolean z;
        StringBuilder buf;
        int i;
        boolean leftJustify = true;
        if (ellipsis != null && precision >= 0) {
            if (ellipsis.length() > precision) {
                z = false;
                Validate.isTrue(z, "Specified ellipsis '%1$s' exceeds precision of %2$s", ellipsis, Integer.valueOf(precision));
                buf = new StringBuilder(seq);
                if (precision < 0 && precision < seq.length()) {
                    CharSequence _ellipsis = (CharSequence) ObjectUtils.defaultIfNull(ellipsis, "");
                    buf.replace(precision - _ellipsis.length(), seq.length(), _ellipsis.toString());
                }
                if ((flags & 1) == 1) {
                    leftJustify = false;
                }
                for (i = buf.length(); i < width; i++) {
                    buf.insert(leftJustify ? i : 0, padChar);
                }
                formatter.format(buf.toString(), new Object[0]);
                return formatter;
            }
        }
        z = true;
        Validate.isTrue(z, "Specified ellipsis '%1$s' exceeds precision of %2$s", ellipsis, Integer.valueOf(precision));
        buf = new StringBuilder(seq);
        if (precision < 0) {
        }
        if ((flags & 1) == 1) {
            leftJustify = false;
        }
        for (i = buf.length(); i < width; i++) {
            if (leftJustify) {
            }
            buf.insert(leftJustify ? i : 0, padChar);
        }
        formatter.format(buf.toString(), new Object[0]);
        return formatter;
    }
}
