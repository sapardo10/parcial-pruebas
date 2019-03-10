package org.apache.commons.text;

import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.CsvTranslators.CsvEscaper;
import org.apache.commons.text.translate.CsvTranslators.CsvUnescaper;
import org.apache.commons.text.translate.EntityArrays;
import org.apache.commons.text.translate.JavaUnicodeEscaper;
import org.apache.commons.text.translate.LookupTranslator;
import org.apache.commons.text.translate.NumericEntityEscaper;
import org.apache.commons.text.translate.NumericEntityUnescaper;
import org.apache.commons.text.translate.NumericEntityUnescaper.OPTION;
import org.apache.commons.text.translate.OctalUnescaper;
import org.apache.commons.text.translate.UnicodeUnescaper;
import org.apache.commons.text.translate.UnicodeUnpairedSurrogateRemover;

public class StringEscapeUtils {
    public static final CharSequenceTranslator ESCAPE_CSV = new CsvEscaper();
    public static final CharSequenceTranslator ESCAPE_ECMASCRIPT;
    public static final CharSequenceTranslator ESCAPE_HTML3 = new AggregateTranslator(new LookupTranslator(EntityArrays.BASIC_ESCAPE), new LookupTranslator(EntityArrays.ISO8859_1_ESCAPE));
    public static final CharSequenceTranslator ESCAPE_HTML4 = new AggregateTranslator(new LookupTranslator(EntityArrays.BASIC_ESCAPE), new LookupTranslator(EntityArrays.ISO8859_1_ESCAPE), new LookupTranslator(EntityArrays.HTML40_EXTENDED_ESCAPE));
    public static final CharSequenceTranslator ESCAPE_JAVA;
    public static final CharSequenceTranslator ESCAPE_JSON;
    public static final CharSequenceTranslator ESCAPE_XML10;
    public static final CharSequenceTranslator ESCAPE_XML11;
    public static final CharSequenceTranslator ESCAPE_XSI;
    public static final CharSequenceTranslator UNESCAPE_CSV = new CsvUnescaper();
    public static final CharSequenceTranslator UNESCAPE_ECMASCRIPT = UNESCAPE_JAVA;
    public static final CharSequenceTranslator UNESCAPE_HTML3 = new AggregateTranslator(new LookupTranslator(EntityArrays.BASIC_UNESCAPE), new LookupTranslator(EntityArrays.ISO8859_1_UNESCAPE), new NumericEntityUnescaper(new OPTION[0]));
    public static final CharSequenceTranslator UNESCAPE_HTML4 = new AggregateTranslator(new LookupTranslator(EntityArrays.BASIC_UNESCAPE), new LookupTranslator(EntityArrays.ISO8859_1_UNESCAPE), new LookupTranslator(EntityArrays.HTML40_EXTENDED_UNESCAPE), new NumericEntityUnescaper(new OPTION[0]));
    public static final CharSequenceTranslator UNESCAPE_JAVA;
    public static final CharSequenceTranslator UNESCAPE_JSON = new AggregateTranslator(new OctalUnescaper(), new UnicodeUnescaper(), new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_UNESCAPE));
    public static final CharSequenceTranslator UNESCAPE_XML = new AggregateTranslator(new LookupTranslator(EntityArrays.BASIC_UNESCAPE), new LookupTranslator(EntityArrays.APOS_UNESCAPE), new NumericEntityUnescaper(new OPTION[0]));
    public static final CharSequenceTranslator UNESCAPE_XSI = new XsiUnescaper();

    public static final class Builder {
        private final StringBuilder sb;
        private final CharSequenceTranslator translator;

        private Builder(CharSequenceTranslator translator) {
            this.sb = new StringBuilder();
            this.translator = translator;
        }

        public Builder escape(String input) {
            this.sb.append(this.translator.translate(input));
            return this;
        }

        public Builder append(String input) {
            this.sb.append(input);
            return this;
        }

        public String toString() {
            return this.sb.toString();
        }
    }

    static class XsiUnescaper extends CharSequenceTranslator {
        private static final char BACKSLASH = '\\';

        public int translate(java.lang.CharSequence r6, int r7, java.io.Writer r8) throws java.io.IOException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:0x0042 in {6, 7, 9, 11, 12, 13, 15} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r5 = this;
            if (r7 != 0) goto L_0x003a;
        L_0x0002:
            r0 = r6.toString();
            r1 = 0;
            r2 = 0;
        L_0x0008:
            r3 = 92;
            r3 = r0.indexOf(r3, r2);
            r4 = -1;
            if (r3 != r4) goto L_0x002a;
        L_0x0011:
            r4 = r0.length();
            if (r1 >= r4) goto L_0x001f;
        L_0x0017:
            r4 = r0.substring(r1);
            r8.write(r4);
            goto L_0x0020;
        L_0x0020:
            r3 = 0;
            r4 = r6.length();
            r3 = java.lang.Character.codePointCount(r6, r3, r4);
            return r3;
        L_0x002a:
            if (r3 <= r1) goto L_0x0034;
        L_0x002c:
            r4 = r0.substring(r1, r3);
            r8.write(r4);
            goto L_0x0035;
        L_0x0035:
            r1 = r3 + 1;
            r2 = r3 + 2;
            goto L_0x0008;
        L_0x003a:
            r0 = new java.lang.IllegalStateException;
            r1 = "XsiUnescaper should never reach the [1] index";
            r0.<init>(r1);
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.text.StringEscapeUtils.XsiUnescaper.translate(java.lang.CharSequence, int, java.io.Writer):int");
        }

        XsiUnescaper() {
        }
    }

    static {
        Map<CharSequence, CharSequence> escapeJavaMap = new HashMap();
        escapeJavaMap.put("\"", "\\\"");
        escapeJavaMap.put("\\", "\\\\");
        ESCAPE_JAVA = new AggregateTranslator(new LookupTranslator(Collections.unmodifiableMap(escapeJavaMap)), new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE), JavaUnicodeEscaper.outsideOf(32, 127));
        escapeJavaMap = new HashMap();
        escapeJavaMap.put("'", "\\'");
        escapeJavaMap.put("\"", "\\\"");
        escapeJavaMap.put("\\", "\\\\");
        escapeJavaMap.put("/", "\\/");
        ESCAPE_ECMASCRIPT = new AggregateTranslator(new LookupTranslator(Collections.unmodifiableMap(escapeJavaMap)), new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE), JavaUnicodeEscaper.outsideOf(32, 127));
        escapeJavaMap = new HashMap();
        escapeJavaMap.put("\"", "\\\"");
        escapeJavaMap.put("\\", "\\\\");
        escapeJavaMap.put("/", "\\/");
        ESCAPE_JSON = new AggregateTranslator(new LookupTranslator(Collections.unmodifiableMap(escapeJavaMap)), new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE), JavaUnicodeEscaper.outsideOf(32, 127));
        escapeJavaMap = new HashMap();
        escapeJavaMap.put("\u0000", "");
        escapeJavaMap.put("\u0001", "");
        escapeJavaMap.put("\u0002", "");
        escapeJavaMap.put("\u0003", "");
        escapeJavaMap.put("\u0004", "");
        escapeJavaMap.put("\u0005", "");
        escapeJavaMap.put("\u0006", "");
        escapeJavaMap.put("\u0007", "");
        escapeJavaMap.put("\b", "");
        escapeJavaMap.put("\u000b", "");
        escapeJavaMap.put("\f", "");
        escapeJavaMap.put("\u000e", "");
        escapeJavaMap.put("\u000f", "");
        escapeJavaMap.put("\u0010", "");
        escapeJavaMap.put("\u0011", "");
        escapeJavaMap.put("\u0012", "");
        escapeJavaMap.put("\u0013", "");
        escapeJavaMap.put("\u0014", "");
        escapeJavaMap.put("\u0015", "");
        escapeJavaMap.put("\u0016", "");
        escapeJavaMap.put("\u0017", "");
        escapeJavaMap.put("\u0018", "");
        escapeJavaMap.put("\u0019", "");
        escapeJavaMap.put("\u001a", "");
        escapeJavaMap.put("\u001b", "");
        escapeJavaMap.put("\u001c", "");
        escapeJavaMap.put("\u001d", "");
        escapeJavaMap.put("\u001e", "");
        escapeJavaMap.put("\u001f", "");
        escapeJavaMap.put("￾", "");
        escapeJavaMap.put("￿", "");
        ESCAPE_XML10 = new AggregateTranslator(new LookupTranslator(EntityArrays.BASIC_ESCAPE), new LookupTranslator(EntityArrays.APOS_ESCAPE), new LookupTranslator(Collections.unmodifiableMap(escapeJavaMap)), NumericEntityEscaper.between(127, 132), NumericEntityEscaper.between(TsExtractor.TS_STREAM_TYPE_SPLICE_INFO, 159), new UnicodeUnpairedSurrogateRemover());
        escapeJavaMap = new HashMap();
        escapeJavaMap.put("\u0000", "");
        escapeJavaMap.put("\u000b", "&#11;");
        escapeJavaMap.put("\f", "&#12;");
        escapeJavaMap.put("￾", "");
        escapeJavaMap.put("￿", "");
        ESCAPE_XML11 = new AggregateTranslator(new LookupTranslator(EntityArrays.BASIC_ESCAPE), new LookupTranslator(EntityArrays.APOS_ESCAPE), new LookupTranslator(Collections.unmodifiableMap(escapeJavaMap)), NumericEntityEscaper.between(1, 8), NumericEntityEscaper.between(14, 31), NumericEntityEscaper.between(127, 132), NumericEntityEscaper.between(TsExtractor.TS_STREAM_TYPE_SPLICE_INFO, 159), new UnicodeUnpairedSurrogateRemover());
        escapeJavaMap = new HashMap();
        escapeJavaMap.put("|", "\\|");
        escapeJavaMap.put("&", "\\&");
        escapeJavaMap.put(";", "\\;");
        escapeJavaMap.put("<", "\\<");
        escapeJavaMap.put(">", "\\>");
        escapeJavaMap.put("(", "\\(");
        escapeJavaMap.put(")", "\\)");
        escapeJavaMap.put("$", "\\$");
        escapeJavaMap.put("`", "\\`");
        escapeJavaMap.put("\\", "\\\\");
        escapeJavaMap.put("\"", "\\\"");
        escapeJavaMap.put("'", "\\'");
        escapeJavaMap.put(StringUtils.SPACE, "\\ ");
        escapeJavaMap.put("\t", "\\\t");
        escapeJavaMap.put(IOUtils.LINE_SEPARATOR_WINDOWS, "");
        escapeJavaMap.put("\n", "");
        escapeJavaMap.put("*", "\\*");
        escapeJavaMap.put("?", "\\?");
        escapeJavaMap.put("[", "\\[");
        escapeJavaMap.put("#", "\\#");
        escapeJavaMap.put("~", "\\~");
        escapeJavaMap.put("=", "\\=");
        escapeJavaMap.put("%", "\\%");
        ESCAPE_XSI = new LookupTranslator(Collections.unmodifiableMap(escapeJavaMap));
        escapeJavaMap = new HashMap();
        escapeJavaMap.put("\\\\", "\\");
        escapeJavaMap.put("\\\"", "\"");
        escapeJavaMap.put("\\'", "'");
        escapeJavaMap.put("\\", "");
        UNESCAPE_JAVA = new AggregateTranslator(new OctalUnescaper(), new UnicodeUnescaper(), new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_UNESCAPE), new LookupTranslator(Collections.unmodifiableMap(escapeJavaMap)));
    }

    public static Builder builder(CharSequenceTranslator translator) {
        return new Builder(translator);
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

    public static final String escapeXSI(String input) {
        return ESCAPE_XSI.translate(input);
    }

    public static final String unescapeXSI(String input) {
        return UNESCAPE_XSI.translate(input);
    }
}
