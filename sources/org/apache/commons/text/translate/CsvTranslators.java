package org.apache.commons.text.translate;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

public final class CsvTranslators {
    private static final char CSV_DELIMITER = ',';
    private static final String CSV_ESCAPED_QUOTE_STR;
    private static final char CSV_QUOTE = '\"';
    private static final String CSV_QUOTE_STR = String.valueOf('\"');
    private static final char[] CSV_SEARCH_CHARS = new char[]{CSV_DELIMITER, '\"', CharUtils.CR, '\n'};

    public static class CsvEscaper extends SinglePassTranslator {
        public /* bridge */ /* synthetic */ int translate(CharSequence charSequence, int i, Writer writer) throws IOException {
            return super.translate(charSequence, i, writer);
        }

        void translateWhole(CharSequence input, Writer out) throws IOException {
            CharSequence inputSting = input.toString();
            if (StringUtils.containsNone(inputSting, CsvTranslators.CSV_SEARCH_CHARS)) {
                out.write(inputSting);
                return;
            }
            out.write(34);
            out.write(StringUtils.replace(inputSting, CsvTranslators.CSV_QUOTE_STR, CsvTranslators.CSV_ESCAPED_QUOTE_STR));
            out.write(34);
        }
    }

    public static class CsvUnescaper extends SinglePassTranslator {
        public /* bridge */ /* synthetic */ int translate(CharSequence charSequence, int i, Writer writer) throws IOException {
            return super.translate(charSequence, i, writer);
        }

        void translateWhole(CharSequence input, Writer out) throws IOException {
            if (input.charAt(0) == '\"') {
                if (input.charAt(input.length() - 1) == '\"') {
                    CharSequence quoteless = input.subSequence(1, input.length() - 1).toString();
                    if (StringUtils.containsAny(quoteless, CsvTranslators.CSV_SEARCH_CHARS)) {
                        out.write(StringUtils.replace(quoteless, CsvTranslators.CSV_ESCAPED_QUOTE_STR, CsvTranslators.CSV_QUOTE_STR));
                    } else {
                        out.write(input.toString());
                    }
                    return;
                }
            }
            out.write(input.toString());
        }
    }

    static {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(CSV_QUOTE_STR);
        stringBuilder.append(CSV_QUOTE_STR);
        CSV_ESCAPED_QUOTE_STR = stringBuilder.toString();
    }

    private CsvTranslators() {
    }
}
