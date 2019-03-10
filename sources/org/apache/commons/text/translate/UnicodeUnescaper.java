package org.apache.commons.text.translate;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.io.IOUtils;

public class UnicodeUnescaper extends CharSequenceTranslator {
    public int translate(CharSequence input, int index, Writer out) throws IOException {
        if (input.charAt(index) != IOUtils.DIR_SEPARATOR_WINDOWS || index + 1 >= input.length() || input.charAt(index + 1) != 'u') {
            return 0;
        }
        int i = 2;
        while (index + i < input.length() && input.charAt(index + i) == 'u') {
            i++;
        }
        if (index + i < input.length() && input.charAt(index + i) == '+') {
            i++;
        }
        if ((index + i) + 4 <= input.length()) {
            CharSequence unicode = input.subSequence(index + i, (index + i) + 4);
            try {
                out.write((char) Integer.parseInt(unicode.toString(), 16));
                return i + 4;
            } catch (NumberFormatException nfe) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unable to parse unicode value: ");
                stringBuilder.append(unicode);
                throw new IllegalArgumentException(stringBuilder.toString(), nfe);
            }
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Less than 4 hex digits in unicode value: '");
        stringBuilder2.append(input.subSequence(index, input.length()));
        stringBuilder2.append("' due to end of CharSequence");
        throw new IllegalArgumentException(stringBuilder2.toString());
    }
}
