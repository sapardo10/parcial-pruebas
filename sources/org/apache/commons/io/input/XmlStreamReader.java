package org.apache.commons.io.input;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.ByteOrderMark;

public class XmlStreamReader extends Reader {
    private static final ByteOrderMark[] BOMS = new ByteOrderMark[]{ByteOrderMark.UTF_8, ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_32BE, ByteOrderMark.UTF_32LE};
    private static final int BUFFER_SIZE = 4096;
    private static final Pattern CHARSET_PATTERN = Pattern.compile("charset=[\"']?([.[^; \"']]*)[\"']?");
    private static final String EBCDIC = "CP1047";
    public static final Pattern ENCODING_PATTERN = Pattern.compile("<\\?xml.*encoding[\\s]*=[\\s]*((?:\".[^\"]*\")|(?:'.[^']*'))", 8);
    private static final String HTTP_EX_1 = "Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], BOM must be NULL";
    private static final String HTTP_EX_2 = "Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], encoding mismatch";
    private static final String HTTP_EX_3 = "Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], Invalid MIME";
    private static final String RAW_EX_1 = "Invalid encoding, BOM [{0}] XML guess [{1}] XML prolog [{2}] encoding mismatch";
    private static final String RAW_EX_2 = "Invalid encoding, BOM [{0}] XML guess [{1}] XML prolog [{2}] unknown BOM";
    private static final String US_ASCII = "US-ASCII";
    private static final String UTF_16 = "UTF-16";
    private static final String UTF_16BE = "UTF-16BE";
    private static final String UTF_16LE = "UTF-16LE";
    private static final String UTF_32 = "UTF-32";
    private static final String UTF_32BE = "UTF-32BE";
    private static final String UTF_32LE = "UTF-32LE";
    private static final String UTF_8 = "UTF-8";
    private static final ByteOrderMark[] XML_GUESS_BYTES = new ByteOrderMark[]{new ByteOrderMark("UTF-8", 60, 63, 120, 109), new ByteOrderMark("UTF-16BE", 0, 60, 0, 63), new ByteOrderMark("UTF-16LE", 60, 0, 63, 0), new ByteOrderMark(UTF_32BE, 0, 0, 0, 60, 0, 0, 0, 63, 0, 0, 0, 120, 0, 0, 0, 109), new ByteOrderMark(UTF_32LE, 60, 0, 0, 0, 63, 0, 0, 0, 120, 0, 0, 0, 109, 0, 0, 0), new ByteOrderMark(EBCDIC, 76, 111, 167, 148)};
    private final String defaultEncoding;
    private final String encoding;
    private final Reader reader;

    public String getDefaultEncoding() {
        return this.defaultEncoding;
    }

    public XmlStreamReader(File file) throws IOException {
        this(new FileInputStream(file));
    }

    public XmlStreamReader(InputStream is) throws IOException {
        this(is, true);
    }

    public XmlStreamReader(InputStream is, boolean lenient) throws IOException {
        this(is, lenient, null);
    }

    public XmlStreamReader(InputStream is, boolean lenient, String defaultEncoding) throws IOException {
        this.defaultEncoding = defaultEncoding;
        BOMInputStream bom = new BOMInputStream(new BufferedInputStream(is, 4096), false, BOMS);
        BOMInputStream pis = new BOMInputStream(bom, true, XML_GUESS_BYTES);
        this.encoding = doRawStream(bom, pis, lenient);
        this.reader = new InputStreamReader(pis, this.encoding);
    }

    public XmlStreamReader(URL url) throws IOException {
        this(url.openConnection(), null);
    }

    public XmlStreamReader(URLConnection conn, String defaultEncoding) throws IOException {
        this.defaultEncoding = defaultEncoding;
        String contentType = conn.getContentType();
        BOMInputStream bom = new BOMInputStream(new BufferedInputStream(conn.getInputStream(), 4096), false, BOMS);
        BOMInputStream pis = new BOMInputStream(bom, true, XML_GUESS_BYTES);
        if (!(conn instanceof HttpURLConnection)) {
            if (contentType == null) {
                this.encoding = doRawStream(bom, pis, true);
                this.reader = new InputStreamReader(pis, this.encoding);
            }
        }
        this.encoding = doHttpStream(bom, pis, contentType, true);
        this.reader = new InputStreamReader(pis, this.encoding);
    }

    public XmlStreamReader(InputStream is, String httpContentType) throws IOException {
        this(is, httpContentType, true);
    }

    public XmlStreamReader(InputStream is, String httpContentType, boolean lenient, String defaultEncoding) throws IOException {
        this.defaultEncoding = defaultEncoding;
        BOMInputStream bom = new BOMInputStream(new BufferedInputStream(is, 4096), false, BOMS);
        BOMInputStream pis = new BOMInputStream(bom, true, XML_GUESS_BYTES);
        this.encoding = doHttpStream(bom, pis, httpContentType, lenient);
        this.reader = new InputStreamReader(pis, this.encoding);
    }

    public XmlStreamReader(InputStream is, String httpContentType, boolean lenient) throws IOException {
        this(is, httpContentType, lenient, null);
    }

    public String getEncoding() {
        return this.encoding;
    }

    public int read(char[] buf, int offset, int len) throws IOException {
        return this.reader.read(buf, offset, len);
    }

    public void close() throws IOException {
        this.reader.close();
    }

    private String doRawStream(BOMInputStream bom, BOMInputStream pis, boolean lenient) throws IOException {
        String bomEnc = bom.getBOMCharsetName();
        String xmlGuessEnc = pis.getBOMCharsetName();
        try {
            return calculateRawEncoding(bomEnc, xmlGuessEnc, getXmlProlog(pis, xmlGuessEnc));
        } catch (XmlStreamReaderException ex) {
            if (lenient) {
                return doLenientDetection(null, ex);
            }
            throw ex;
        }
    }

    private String doHttpStream(BOMInputStream bom, BOMInputStream pis, String httpContentType, boolean lenient) throws IOException {
        String bomEnc = bom.getBOMCharsetName();
        String xmlGuessEnc = pis.getBOMCharsetName();
        try {
            return calculateHttpEncoding(httpContentType, bomEnc, xmlGuessEnc, getXmlProlog(pis, xmlGuessEnc), lenient);
        } catch (XmlStreamReaderException ex) {
            if (lenient) {
                return doLenientDetection(httpContentType, ex);
            }
            throw ex;
        }
    }

    private String doLenientDetection(String httpContentType, XmlStreamReaderException ex) throws IOException {
        if (httpContentType != null && httpContentType.startsWith("text/html")) {
            httpContentType = httpContentType.substring("text/html".length());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("text/xml");
            stringBuilder.append(httpContentType);
            try {
                return calculateHttpEncoding(stringBuilder.toString(), ex.getBomEncoding(), ex.getXmlGuessEncoding(), ex.getXmlEncoding(), true);
            } catch (XmlStreamReaderException ex2) {
                ex = ex2;
            }
        }
        String encoding = ex.getXmlEncoding();
        if (encoding == null) {
            encoding = ex.getContentTypeEncoding();
        }
        if (encoding == null) {
            String str = this.defaultEncoding;
            if (str == null) {
                str = "UTF-8";
            }
            encoding = str;
        }
        return encoding;
    }

    String calculateRawEncoding(String bomEnc, String xmlGuessEnc, String xmlEnc) throws IOException {
        if (bomEnc == null) {
            if (xmlGuessEnc != null) {
                if (xmlEnc != null) {
                    if (xmlEnc.equals("UTF-16") && (xmlGuessEnc.equals("UTF-16BE") || xmlGuessEnc.equals("UTF-16LE"))) {
                        return xmlGuessEnc;
                    }
                    return xmlEnc;
                }
            }
            String str = this.defaultEncoding;
            if (str == null) {
                str = "UTF-8";
            }
            return str;
        } else if (bomEnc.equals("UTF-8")) {
            if (xmlGuessEnc != null) {
                if (!xmlGuessEnc.equals("UTF-8")) {
                    throw new XmlStreamReaderException(MessageFormat.format(RAW_EX_1, new Object[]{bomEnc, xmlGuessEnc, xmlEnc}), bomEnc, xmlGuessEnc, xmlEnc);
                }
            }
            if (xmlEnc != null) {
                if (!xmlEnc.equals("UTF-8")) {
                    throw new XmlStreamReaderException(MessageFormat.format(RAW_EX_1, new Object[]{bomEnc, xmlGuessEnc, xmlEnc}), bomEnc, xmlGuessEnc, xmlEnc);
                }
            }
            return bomEnc;
        } else {
            if (!bomEnc.equals("UTF-16BE")) {
                if (!bomEnc.equals("UTF-16LE")) {
                    if (!bomEnc.equals(UTF_32BE)) {
                        if (!bomEnc.equals(UTF_32LE)) {
                            throw new XmlStreamReaderException(MessageFormat.format(RAW_EX_2, new Object[]{bomEnc, xmlGuessEnc, xmlEnc}), bomEnc, xmlGuessEnc, xmlEnc);
                        }
                    }
                    if (xmlGuessEnc != null) {
                        if (!xmlGuessEnc.equals(bomEnc)) {
                            throw new XmlStreamReaderException(MessageFormat.format(RAW_EX_1, new Object[]{bomEnc, xmlGuessEnc, xmlEnc}), bomEnc, xmlGuessEnc, xmlEnc);
                        }
                    }
                    if (!(xmlEnc == null || xmlEnc.equals(UTF_32))) {
                        if (!xmlEnc.equals(bomEnc)) {
                            throw new XmlStreamReaderException(MessageFormat.format(RAW_EX_1, new Object[]{bomEnc, xmlGuessEnc, xmlEnc}), bomEnc, xmlGuessEnc, xmlEnc);
                        }
                    }
                    return bomEnc;
                }
            }
            if (xmlGuessEnc != null) {
                if (!xmlGuessEnc.equals(bomEnc)) {
                    throw new XmlStreamReaderException(MessageFormat.format(RAW_EX_1, new Object[]{bomEnc, xmlGuessEnc, xmlEnc}), bomEnc, xmlGuessEnc, xmlEnc);
                }
            }
            if (!(xmlEnc == null || xmlEnc.equals("UTF-16"))) {
                if (!xmlEnc.equals(bomEnc)) {
                    throw new XmlStreamReaderException(MessageFormat.format(RAW_EX_1, new Object[]{bomEnc, xmlGuessEnc, xmlEnc}), bomEnc, xmlGuessEnc, xmlEnc);
                }
            }
            return bomEnc;
        }
    }

    String calculateHttpEncoding(String httpContentType, String bomEnc, String xmlGuessEnc, String xmlEnc, boolean lenient) throws IOException {
        XmlStreamReader xmlStreamReader = this;
        String str = bomEnc;
        String str2 = xmlGuessEnc;
        String str3 = xmlEnc;
        if (lenient && str3 != null) {
            return str3;
        }
        String cTMime = getContentTypeMime(httpContentType);
        String cTEnc = getContentTypeEncoding(httpContentType);
        boolean appXml = isAppXml(cTMime);
        boolean textXml = isTextXml(cTMime);
        if (!appXml) {
            if (!textXml) {
                throw new XmlStreamReaderException(MessageFormat.format(HTTP_EX_3, new Object[]{cTMime, cTEnc, str, str2, str3}), cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc);
            }
        }
        if (cTEnc != null) {
            if (!cTEnc.equals("UTF-16BE")) {
                if (!cTEnc.equals("UTF-16LE")) {
                    if (!cTEnc.equals("UTF-16")) {
                        if (!cTEnc.equals(UTF_32BE)) {
                            if (!cTEnc.equals(UTF_32LE)) {
                                if (!cTEnc.equals(UTF_32)) {
                                    return cTEnc;
                                }
                                if (str != null && str.startsWith(UTF_32)) {
                                    return str;
                                }
                                throw new XmlStreamReaderException(MessageFormat.format(HTTP_EX_2, new Object[]{cTMime, cTEnc, str, str2, str3}), cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc);
                            }
                        }
                        if (str == null) {
                            return cTEnc;
                        }
                        throw new XmlStreamReaderException(MessageFormat.format(HTTP_EX_1, new Object[]{cTMime, cTEnc, str, str2, str3}), cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc);
                    } else if (str != null && str.startsWith("UTF-16")) {
                        return str;
                    } else {
                        throw new XmlStreamReaderException(MessageFormat.format(HTTP_EX_2, new Object[]{cTMime, cTEnc, str, str2, str3}), cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc);
                    }
                }
            }
            if (str == null) {
                return cTEnc;
            }
            throw new XmlStreamReaderException(MessageFormat.format(HTTP_EX_1, new Object[]{cTMime, cTEnc, str, str2, str3}), cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc);
        } else if (appXml) {
            return calculateRawEncoding(str, str2, str3);
        } else {
            String str4 = xmlStreamReader.defaultEncoding;
            if (str4 == null) {
                str4 = "US-ASCII";
            }
            return str4;
        }
    }

    static String getContentTypeMime(String httpContentType) {
        if (httpContentType == null) {
            return null;
        }
        String mime;
        int i = httpContentType.indexOf(";");
        if (i >= 0) {
            mime = httpContentType.substring(0, i);
        } else {
            mime = httpContentType;
        }
        return mime.trim();
    }

    static String getContentTypeEncoding(String httpContentType) {
        if (httpContentType == null) {
            return null;
        }
        int i = httpContentType.indexOf(";");
        if (i <= -1) {
            return null;
        }
        Matcher m = CHARSET_PATTERN.matcher(httpContentType.substring(i + 1));
        String str = null;
        String encoding = m.find() ? m.group(1) : null;
        if (encoding != null) {
            str = encoding.toUpperCase(Locale.US);
        }
        return str;
    }

    private static String getXmlProlog(InputStream is, String guessedEnc) throws IOException {
        if (guessedEnc == null) {
            return null;
        }
        byte[] bytes = new byte[4096];
        is.mark(4096);
        int offset = 0;
        int max = 4096;
        int c = is.read(bytes, 0, 4096);
        int firstGT = -1;
        String xmlProlog = "";
        while (c != -1 && firstGT == -1 && offset < 4096) {
            offset += c;
            max -= c;
            c = is.read(bytes, offset, max);
            xmlProlog = new String(bytes, 0, offset, guessedEnc);
            firstGT = xmlProlog.indexOf(62);
        }
        if (firstGT == -1) {
            if (c == -1) {
                throw new IOException("Unexpected end of XML stream");
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("XML prolog or ROOT element not found on first ");
            stringBuilder.append(offset);
            stringBuilder.append(" bytes");
            throw new IOException(stringBuilder.toString());
        } else if (offset <= 0) {
            return null;
        } else {
            is.reset();
            BufferedReader bReader = new BufferedReader(new StringReader(xmlProlog.substring(0, firstGT + 1)));
            StringBuffer prolog = new StringBuffer();
            for (String line = bReader.readLine(); line != null; line = bReader.readLine()) {
                prolog.append(line);
            }
            Matcher m = ENCODING_PATTERN.matcher(prolog);
            if (!m.find()) {
                return null;
            }
            String encoding = m.group(1).toUpperCase();
            return encoding.substring(1, encoding.length() - 1);
        }
    }

    static boolean isAppXml(String mime) {
        return mime != null && (mime.equals("application/xml") || mime.equals("application/xml-dtd") || mime.equals("application/xml-external-parsed-entity") || (mime.startsWith("application/") && mime.endsWith("+xml")));
    }

    static boolean isTextXml(String mime) {
        return mime != null && (mime.equals("text/xml") || mime.equals("text/xml-external-parsed-entity") || (mime.startsWith("text/") && mime.endsWith("+xml")));
    }
}
