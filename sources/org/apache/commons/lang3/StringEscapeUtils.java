package org.apache.commons.lang3;

import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import java.io.IOException;
import java.io.Writer;
import org.apache.commons.lang3.text.translate.AggregateTranslator;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.EntityArrays;
import org.apache.commons.lang3.text.translate.JavaUnicodeEscaper;
import org.apache.commons.lang3.text.translate.LookupTranslator;
import org.apache.commons.lang3.text.translate.NumericEntityEscaper;
import org.apache.commons.lang3.text.translate.NumericEntityUnescaper;
import org.apache.commons.lang3.text.translate.NumericEntityUnescaper.OPTION;
import org.apache.commons.lang3.text.translate.OctalUnescaper;
import org.apache.commons.lang3.text.translate.UnicodeUnescaper;
import org.apache.commons.lang3.text.translate.UnicodeUnpairedSurrogateRemover;

@Deprecated
public class StringEscapeUtils {
    public static final CharSequenceTranslator ESCAPE_CSV = new CsvEscaper();
    public static final CharSequenceTranslator ESCAPE_ECMASCRIPT;
    public static final CharSequenceTranslator ESCAPE_HTML3 = new AggregateTranslator(new LookupTranslator(EntityArrays.BASIC_ESCAPE()), new LookupTranslator(EntityArrays.ISO8859_1_ESCAPE()));
    public static final CharSequenceTranslator ESCAPE_HTML4 = new AggregateTranslator(new LookupTranslator(EntityArrays.BASIC_ESCAPE()), new LookupTranslator(EntityArrays.ISO8859_1_ESCAPE()), new LookupTranslator(EntityArrays.HTML40_EXTENDED_ESCAPE()));
    public static final CharSequenceTranslator ESCAPE_JAVA;
    public static final CharSequenceTranslator ESCAPE_JSON;
    @Deprecated
    public static final CharSequenceTranslator ESCAPE_XML = new AggregateTranslator(new LookupTranslator(EntityArrays.BASIC_ESCAPE()), new LookupTranslator(EntityArrays.APOS_ESCAPE()));
    public static final CharSequenceTranslator ESCAPE_XML10;
    public static final CharSequenceTranslator ESCAPE_XML11;
    public static final CharSequenceTranslator UNESCAPE_CSV = new CsvUnescaper();
    public static final CharSequenceTranslator UNESCAPE_ECMASCRIPT;
    public static final CharSequenceTranslator UNESCAPE_HTML3 = new AggregateTranslator(new LookupTranslator(EntityArrays.BASIC_UNESCAPE()), new LookupTranslator(EntityArrays.ISO8859_1_UNESCAPE()), new NumericEntityUnescaper(new OPTION[0]));
    public static final CharSequenceTranslator UNESCAPE_HTML4 = new AggregateTranslator(new LookupTranslator(EntityArrays.BASIC_UNESCAPE()), new LookupTranslator(EntityArrays.ISO8859_1_UNESCAPE()), new LookupTranslator(EntityArrays.HTML40_EXTENDED_UNESCAPE()), new NumericEntityUnescaper(new OPTION[0]));
    public static final CharSequenceTranslator UNESCAPE_JAVA;
    public static final CharSequenceTranslator UNESCAPE_JSON;
    public static final CharSequenceTranslator UNESCAPE_XML = new AggregateTranslator(new LookupTranslator(EntityArrays.BASIC_UNESCAPE()), new LookupTranslator(EntityArrays.APOS_UNESCAPE()), new NumericEntityUnescaper(new OPTION[0]));

    static class CsvEscaper extends CharSequenceTranslator {
        private static final char CSV_DELIMITER = ',';
        private static final char CSV_QUOTE = '\"';
        private static final String CSV_QUOTE_STR = String.valueOf('\"');
        private static final char[] CSV_SEARCH_CHARS = new char[]{CSV_DELIMITER, '\"', CharUtils.CR, '\n'};

        CsvEscaper() {
        }

        public int translate(CharSequence input, int index, Writer out) throws IOException {
            if (index == 0) {
                if (StringUtils.containsNone(input.toString(), CSV_SEARCH_CHARS)) {
                    out.write(input.toString());
                } else {
                    out.write(34);
                    String charSequence = input.toString();
                    String str = CSV_QUOTE_STR;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(CSV_QUOTE_STR);
                    stringBuilder.append(CSV_QUOTE_STR);
                    out.write(StringUtils.replace(charSequence, str, stringBuilder.toString()));
                    out.write(34);
                }
                return Character.codePointCount(input, 0, input.length());
            }
            throw new IllegalStateException("CsvEscaper should never reach the [1] index");
        }
    }

    static class CsvUnescaper extends CharSequenceTranslator {
        private static final char CSV_DELIMITER = ',';
        private static final char CSV_QUOTE = '\"';
        private static final String CSV_QUOTE_STR = String.valueOf('\"');
        private static final char[] CSV_SEARCH_CHARS = new char[]{CSV_DELIMITER, '\"', CharUtils.CR, '\n'};

        CsvUnescaper() {
        }

        public int translate(CharSequence input, int index, Writer out) throws IOException {
            if (index == 0) {
                if (input.charAt(0) == '\"') {
                    if (input.charAt(input.length() - 1) == '\"') {
                        CharSequence quoteless = input.subSequence(1, input.length() - 1).toString();
                        if (StringUtils.containsAny(quoteless, CSV_SEARCH_CHARS)) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(CSV_QUOTE_STR);
                            stringBuilder.append(CSV_QUOTE_STR);
                            out.write(StringUtils.replace(quoteless, stringBuilder.toString(), CSV_QUOTE_STR));
                        } else {
                            out.write(input.toString());
                        }
                        return Character.codePointCount(input, 0, input.length());
                    }
                }
                out.write(input.toString());
                return Character.codePointCount(input, 0, input.length());
            }
            throw new IllegalStateException("CsvUnescaper should never reach the [1] index");
        }
    }

    static {
        r2 = new String[2][];
        r2[0] = new String[]{"\"", "\\\""};
        r2[1] = new String[]{"\\", "\\\\"};
        ESCAPE_JAVA = new LookupTranslator(r2).with(new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE())).with(JavaUnicodeEscaper.outsideOf(32, 127));
        r7 = new CharSequenceTranslator[3];
        r10 = new String[4][];
        r10[0] = new String[]{"'", "\\'"};
        r10[1] = new String[]{"\"", "\\\""};
        r10[2] = new String[]{"\\", "\\\\"};
        r10[3] = new String[]{"/", "\\/"};
        r7[0] = new LookupTranslator(r10);
        r7[1] = new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE());
        r7[2] = JavaUnicodeEscaper.outsideOf(32, 127);
        ESCAPE_ECMASCRIPT = new AggregateTranslator(r7);
        r7 = new CharSequenceTranslator[3];
        r10 = new String[3][];
        r10[0] = new String[]{"\"", "\\\""};
        r10[1] = new String[]{"\\", "\\\\"};
        r10[2] = new String[]{"/", "\\/"};
        r7[0] = new LookupTranslator(r10);
        r7[1] = new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE());
        r7[2] = JavaUnicodeEscaper.outsideOf(32, 127);
        ESCAPE_JSON = new AggregateTranslator(r7);
        r7 = new CharSequenceTranslator[6];
        r11 = new String[31][];
        r11[0] = new String[]{"\u0000", ""};
        r11[1] = new String[]{"\u0001", ""};
        r11[2] = new String[]{"\u0002", ""};
        r11[3] = new String[]{"\u0003", ""};
        r11[4] = new String[]{"\u0004", ""};
        r11[5] = new String[]{"\u0005", ""};
        r11[6] = new String[]{"\u0006", ""};
        r11[7] = new String[]{"\u0007", ""};
        r11[8] = new String[]{"\b", ""};
        r11[9] = new String[]{"\u000b", ""};
        r11[10] = new String[]{"\f", ""};
        r11[11] = new String[]{"\u000e", ""};
        r11[12] = new String[]{"\u000f", ""};
        r11[13] = new String[]{"\u0010", ""};
        r11[14] = new String[]{"\u0011", ""};
        r11[15] = new String[]{"\u0012", ""};
        r11[16] = new String[]{"\u0013", ""};
        r11[17] = new String[]{"\u0014", ""};
        r11[18] = new String[]{"\u0015", ""};
        r11[19] = new String[]{"\u0016", ""};
        r11[20] = new String[]{"\u0017", ""};
        r11[21] = new String[]{"\u0018", ""};
        r11[22] = new String[]{"\u0019", ""};
        r11[23] = new String[]{"\u001a", ""};
        r11[24] = new String[]{"\u001b", ""};
        r11[25] = new String[]{"\u001c", ""};
        r11[26] = new String[]{"\u001d", ""};
        r11[27] = new String[]{"\u001e", ""};
        r11[28] = new String[]{"\u001f", ""};
        r11[29] = new String[]{"￾", ""};
        r11[30] = new String[]{"￿", ""};
        r7[2] = new LookupTranslator(r11);
        r7[3] = NumericEntityEscaper.between(127, 132);
        r7[4] = NumericEntityEscaper.between(TsExtractor.TS_STREAM_TYPE_SPLICE_INFO, 159);
        r7[5] = new UnicodeUnpairedSurrogateRemover();
        ESCAPE_XML10 = new AggregateTranslator(r7);
        r7 = new CharSequenceTranslator[8];
        r7[0] = new LookupTranslator(EntityArrays.BASIC_ESCAPE());
        r7[1] = new LookupTranslator(EntityArrays.APOS_ESCAPE());
        CharSequence[][] charSequenceArr = new String[5][];
        charSequenceArr[0] = new String[]{"\u0000", ""};
        charSequenceArr[1] = new String[]{"\u000b", "&#11;"};
        charSequenceArr[2] = new String[]{"\f", "&#12;"};
        charSequenceArr[3] = new String[]{"￾", ""};
        charSequenceArr[4] = new String[]{"￿", ""};
        r7[2] = new LookupTranslator(charSequenceArr);
        r7[3] = NumericEntityEscaper.between(1, 8);
        r7[4] = NumericEntityEscaper.between(14, 31);
        r7[5] = NumericEntityEscaper.between(127, 132);
        r7[6] = NumericEntityEscaper.between(TsExtractor.TS_STREAM_TYPE_SPLICE_INFO, 159);
        r7[7] = new UnicodeUnpairedSurrogateRemover();
        ESCAPE_XML11 = new AggregateTranslator(r7);
        CharSequenceTranslator[] charSequenceTranslatorArr = new CharSequenceTranslator[4];
        charSequenceTranslatorArr[0] = new OctalUnescaper();
        charSequenceTranslatorArr[1] = new UnicodeUnescaper();
        charSequenceTranslatorArr[2] = new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_UNESCAPE());
        r7 = new String[4][];
        r7[0] = new String[]{"\\\\", "\\"};
        r7[1] = new String[]{"\\\"", "\""};
        r7[2] = new String[]{"\\'", "'"};
        r7[3] = new String[]{"\\", ""};
        charSequenceTranslatorArr[3] = new LookupTranslator(r7);
        UNESCAPE_JAVA = new AggregateTranslator(charSequenceTranslatorArr);
        CharSequenceTranslator charSequenceTranslator = UNESCAPE_JAVA;
        UNESCAPE_ECMASCRIPT = charSequenceTranslator;
        UNESCAPE_JSON = charSequenceTranslator;
    }

    public static final String escapeJava(String input) {
        return ESCAPE_JAVA.translate(input);
    }

    public static final String escapeEcmaScript(String input) {
        return ESCAPE_ECMASCRIPT.translate(input);
    }

    public static final String escapeJson(String input) {
        return ESCAPE_JSON.translate(input);
    }

    public static final String unescapeJava(String input) {
        return UNESCAPE_JAVA.translate(input);
    }

    public static final String unescapeEcmaScript(String input) {
        return UNESCAPE_ECMASCRIPT.translate(input);
    }

    public static final String unescapeJson(String input) {
        return UNESCAPE_JSON.translate(input);
    }

    public static final String escapeHtml4(String input) {
        return ESCAPE_HTML4.translate(input);
    }

    public static final String escapeHtml3(String input) {
        return ESCAPE_HTML3.translate(input);
    }

    public static final String unescapeHtml4(String input) {
        return UNESCAPE_HTML4.translate(input);
    }

    public static final String unescapeHtml3(String input) {
        return UNESCAPE_HTML3.translate(input);
    }

    @Deprecated
    public static final String escapeXml(String input) {
        return ESCAPE_XML.translate(input);
    }

    public static String escapeXml10(String input) {
        return ESCAPE_XML10.translate(input);
    }

    public static String escapeXml11(String input) {
        return ESCAPE_XML11.translate(input);
    }

    public static final String unescapeXml(String input) {
        return UNESCAPE_XML.translate(input);
    }

    public static final String escapeCsv(String input) {
        return ESCAPE_CSV.translate(input);
    }

    public static final String unescapeCsv(String input) {
        return UNESCAPE_CSV.translate(input);
    }
}
