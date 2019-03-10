package org.apache.commons.text.translate;

import java.io.IOException;
import java.io.Writer;

abstract class SinglePassTranslator extends CharSequenceTranslator {
    abstract void translateWhole(CharSequence charSequence, Writer writer) throws IOException;

    SinglePassTranslator() {
    }

    public int translate(CharSequence input, int index, Writer out) throws IOException {
        if (index == 0) {
            translateWhole(input, out);
            return Character.codePointCount(input, index, input.length());
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getClassName());
        stringBuilder.append(".translate(final CharSequence input, final int index, final Writer out) can not handle a non-zero index.");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    private String getClassName() {
        Class<? extends SinglePassTranslator> clazz = getClass();
        return clazz.isAnonymousClass() ? clazz.getName() : clazz.getSimpleName();
    }
}
