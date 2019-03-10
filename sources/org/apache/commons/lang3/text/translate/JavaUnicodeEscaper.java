package org.apache.commons.lang3.text.translate;

@Deprecated
public class JavaUnicodeEscaper extends UnicodeEscaper {
    public static JavaUnicodeEscaper above(int codepoint) {
        return outsideOf(0, codepoint);
    }

    public static JavaUnicodeEscaper below(int codepoint) {
        return outsideOf(codepoint, Integer.MAX_VALUE);
    }

    public static JavaUnicodeEscaper between(int codepointLow, int codepointHigh) {
        return new JavaUnicodeEscaper(codepointLow, codepointHigh, true);
    }

    public static JavaUnicodeEscaper outsideOf(int codepointLow, int codepointHigh) {
        return new JavaUnicodeEscaper(codepointLow, codepointHigh, false);
    }

    public JavaUnicodeEscaper(int below, int above, boolean between) {
        super(below, above, between);
    }

    protected String toUtf16Escape(int codepoint) {
        char[] surrogatePair = Character.toChars(codepoint);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\\u");
        stringBuilder.append(CharSequenceTranslator.hex(surrogatePair[0]));
        stringBuilder.append("\\u");
        stringBuilder.append(CharSequenceTranslator.hex(surrogatePair[1]));
        return stringBuilder.toString();
    }
}
