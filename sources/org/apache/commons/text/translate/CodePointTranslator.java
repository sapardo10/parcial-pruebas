package org.apache.commons.text.translate;

import java.io.IOException;
import java.io.Writer;

public abstract class CodePointTranslator extends CharSequenceTranslator {
    public abstract boolean translate(int i, Writer writer) throws IOException;

    public final int translate(CharSequence input, int index, Writer out) throws IOException {
        return translate(Character.codePointAt(input, index), out);
    }
}
