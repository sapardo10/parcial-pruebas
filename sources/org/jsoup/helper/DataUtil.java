package org.jsoup.helper;

import de.danoeh.antennapod.core.syndication.namespace.NSContent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.internal.ConstrainableInputStream;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.XmlDeclaration;
import org.jsoup.parser.Parser;
import org.objenesis.instantiator.util.ClassDefinitionUtils;

public final class DataUtil {
    static final int boundaryLength = 32;
    static final int bufferSize = 32768;
    private static final Pattern charsetPattern = Pattern.compile("(?i)\\bcharset=\\s*(?:[\"'])?([^\\s,;\"']*)");
    static final String defaultCharset = "UTF-8";
    private static final int firstReadBufferSize = 5120;
    private static final char[] mimeBoundaryChars = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private static class BomCharset {
        private final String charset;
        private final int offset;

        public BomCharset(String charset, int offset) {
            this.charset = charset;
            this.offset = offset;
        }
    }

    private DataUtil() {
    }

    public static Document load(File in, String charsetName, String baseUri) throws IOException {
        return parseInputStream(new FileInputStream(in), charsetName, baseUri, Parser.htmlParser());
    }

    public static Document load(InputStream in, String charsetName, String baseUri) throws IOException {
        return parseInputStream(in, charsetName, baseUri, Parser.htmlParser());
    }

    public static Document load(InputStream in, String charsetName, String baseUri, Parser parser) throws IOException {
        return parseInputStream(in, charsetName, baseUri, parser);
    }

    static void crossStreams(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[32768];
        while (true) {
            int read = in.read(buffer);
            int len = read;
            if (read != -1) {
                out.write(buffer, 0, len);
            } else {
                return;
            }
        }
    }

    static Document parseInputStream(InputStream input, String charsetName, String baseUri, Parser parser) throws IOException {
        if (input == null) {
            return new Document(baseUri);
        }
        input = ConstrainableInputStream.wrap(input, 32768, 0);
        Document doc = null;
        input.mark(firstReadBufferSize);
        ByteBuffer firstBytes = readToByteBuffer(input, 5119);
        boolean fullyRead = input.read() == -1;
        input.reset();
        BomCharset bomCharset = detectCharsetFromBom(firstBytes);
        if (bomCharset != null) {
            charsetName = bomCharset.charset;
            input.skip((long) bomCharset.offset);
        }
        if (charsetName == null) {
            doc = parser.parseInput(Charset.forName("UTF-8").decode(firstBytes).toString(), baseUri);
            String foundCharset = null;
            Iterator it = doc.select("meta[http-equiv=content-type], meta[charset]").iterator();
            while (it.hasNext()) {
                Element meta = (Element) it.next();
                if (meta.hasAttr("http-equiv")) {
                    foundCharset = getCharsetFromContentType(meta.attr(NSContent.NSTAG));
                }
                if (foundCharset == null && meta.hasAttr("charset")) {
                    foundCharset = meta.attr("charset");
                }
                if (foundCharset != null) {
                    break;
                }
            }
            if (foundCharset == null && doc.childNodeSize() > 0 && (doc.childNode(0) instanceof XmlDeclaration)) {
                XmlDeclaration prolog = (XmlDeclaration) doc.childNode(0);
                if (prolog.name().equals("xml")) {
                    foundCharset = prolog.attr("encoding");
                }
            }
            String foundCharset2 = validateCharset(foundCharset);
            if (foundCharset2 != null && !foundCharset2.equalsIgnoreCase("UTF-8")) {
                charsetName = foundCharset2.trim().replaceAll("[\"']", "");
                doc = null;
            } else if (!fullyRead) {
                doc = null;
            }
        } else {
            Validate.notEmpty(charsetName, "Must set charset arg to character set of file to parse. Set to null to attempt to detect from HTML");
        }
        if (doc == null) {
            if (charsetName == null) {
                charsetName = "UTF-8";
            }
            doc = parser.parseInput(new BufferedReader(new InputStreamReader(input, charsetName), 32768), baseUri);
            doc.outputSettings().charset(charsetName);
        }
        input.close();
        return doc;
    }

    public static ByteBuffer readToByteBuffer(InputStream inStream, int maxSize) throws IOException {
        Validate.isTrue(maxSize >= 0, "maxSize must be 0 (unlimited) or larger");
        return ConstrainableInputStream.wrap(inStream, 32768, maxSize).readToByteBuffer(maxSize);
    }

    static ByteBuffer readToByteBuffer(InputStream inStream) throws IOException {
        return readToByteBuffer(inStream, 0);
    }

    static ByteBuffer readFileToByteBuffer(File file) throws IOException {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
            byte[] bytes = new byte[((int) randomAccessFile.length())];
            randomAccessFile.readFully(bytes);
            ByteBuffer wrap = ByteBuffer.wrap(bytes);
            randomAccessFile.close();
            return wrap;
        } catch (Throwable th) {
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        }
    }

    static ByteBuffer emptyByteBuffer() {
        return ByteBuffer.allocate(0);
    }

    static String getCharsetFromContentType(String contentType) {
        if (contentType == null) {
            return null;
        }
        Matcher m = charsetPattern.matcher(contentType);
        if (m.find()) {
            return validateCharset(m.group(1).trim().replace("charset=", ""));
        }
        return null;
    }

    private static String validateCharset(String cs) {
        if (cs != null) {
            if (cs.length() != 0) {
                cs = cs.trim().replaceAll("[\"']", "");
                try {
                    if (Charset.isSupported(cs)) {
                        return cs;
                    }
                    cs = cs.toUpperCase(Locale.ENGLISH);
                    if (Charset.isSupported(cs)) {
                        return cs;
                    }
                    return null;
                } catch (IllegalCharsetNameException e) {
                }
            }
        }
        return null;
    }

    static String mimeBoundary() {
        StringBuilder mime = new StringBuilder(32);
        Random rand = new Random();
        for (int i = 0; i < 32; i++) {
            char[] cArr = mimeBoundaryChars;
            mime.append(cArr[rand.nextInt(cArr.length)]);
        }
        return mime.toString();
    }

    private static BomCharset detectCharsetFromBom(ByteBuffer byteData) {
        Buffer buffer = byteData;
        buffer.mark();
        byte[] bom = new byte[4];
        if (byteData.remaining() >= bom.length) {
            byteData.get(bom);
            buffer.rewind();
        }
        if (bom[0] == (byte) 0 && bom[1] == (byte) 0 && bom[2] == (byte) -2) {
            if (bom[3] != (byte) -1) {
            }
            return new BomCharset("UTF-32", 0);
        }
        if (bom[0] == (byte) -1 && bom[1] == (byte) -2 && bom[2] == (byte) 0 && bom[3] == (byte) 0) {
            return new BomCharset("UTF-32", 0);
        }
        if (bom[0] == (byte) -2) {
            if (bom[1] != (byte) -1) {
            }
            return new BomCharset("UTF-16", 0);
        }
        if (bom[0] == (byte) -1 && bom[1] == (byte) -2) {
            return new BomCharset("UTF-16", 0);
        }
        if (bom[0] == (byte) -17 && bom[1] == ClassDefinitionUtils.OPS_new && bom[2] == (byte) -65) {
            return new BomCharset("UTF-8", 3);
        }
        return null;
    }
}
