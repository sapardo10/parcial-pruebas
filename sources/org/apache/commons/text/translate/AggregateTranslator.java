package org.apache.commons.text.translate;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class AggregateTranslator extends CharSequenceTranslator {
    private final List<CharSequenceTranslator> translators = new ArrayList();

    public AggregateTranslator(CharSequenceTranslator... translators) {
        if (translators != null) {
            for (CharSequenceTranslator translator : translators) {
                if (translator != null) {
                    this.translators.add(translator);
                }
            }
        }
    }

    public int translate(CharSequence input, int index, Writer out) throws IOException {
        for (CharSequenceTranslator translator : this.translators) {
            int consumed = translator.translate(input, index, out);
            if (consumed != 0) {
                return consumed;
            }
        }
        return 0;
    }
}
