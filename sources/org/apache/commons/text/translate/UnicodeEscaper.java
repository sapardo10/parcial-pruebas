package org.apache.commons.text.translate;

import android.support.v4.internal.view.SupportMenu;
import java.io.IOException;
import java.io.Writer;

public class UnicodeEscaper extends CodePointTranslator {
    private final int above;
    private final int below;
    private final boolean between;

    public UnicodeEscaper() {
        this(0, Integer.MAX_VALUE, true);
    }

    protected UnicodeEscaper(int below, int above, boolean between) {
        this.below = below;
        this.above = above;
        this.between = between;
    }

    public static UnicodeEscaper below(int codepoint) {
        return outsideOf(codepoint, Integer.MAX_VALUE);
    }

    public static UnicodeEscaper above(int codepoint) {
        return outsideOf(0, codepoint);
    }

    public static UnicodeEscaper outsideOf(int codepointLow, int codepointHigh) {
        return new UnicodeEscaper(codepointLow, codepointHigh, false);
    }

    public static UnicodeEscaper between(int codepointLow, int codepointHigh) {
        return new UnicodeEscaper(codepointLow, codepointHigh, true);
    }

    public boolean translate(int codepoint, Writer out) throws IOException {
        if (this.between) {
            if (codepoint >= this.below) {
                if (codepoint > this.above) {
                }
            }
            return false;
        } else if (codepoint >= this.below && codepoint <= this.above) {
            return false;
        }
        if (codepoint > SupportMenu.USER_MASK) {
            out.write(toUtf16Escape(codepoint));
        } else {
            out.write("\\u");
            out.write(HEX_DIGITS[(codepoint >> 12) & 15]);
            out.write(HEX_DIGITS[(codepoint >> 8) & 15]);
            out.write(HEX_DIGITS[(codepoint >> 4) & 15]);
            out.write(HEX_DIGITS[codepoint & 15]);
        }
        return true;
    }

    protected String toUtf16Escape(int codepoint) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\\u");
        stringBuilder.append(CharSequenceTranslator.hex(codepoint));
        return stringBuilder.toString();
    }
}
