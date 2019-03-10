package de.danoeh.antennapod.core.util.id3reader;

import de.danoeh.antennapod.core.util.id3reader.model.FrameHeader;
import de.danoeh.antennapod.core.util.id3reader.model.TagHeader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;

public class ID3Reader {
    static final int ACTION_DONT_SKIP = 2;
    private static final int ACTION_SKIP = 1;
    private static final byte ENCODING_UTF16_WITHOUT_BOM = (byte) 2;
    private static final byte ENCODING_UTF16_WITH_BOM = (byte) 1;
    private static final byte ENCODING_UTF8 = (byte) 3;
    private static final int FRAME_ID_LENGTH = 4;
    private static final int HEADER_LENGTH = 10;
    private static final int ID3_LENGTH = 3;
    private int readerPosition;
    private TagHeader tagHeader;

    ID3Reader() {
    }

    public final void readInputStream(InputStream input) throws IOException, ID3ReaderException {
        this.readerPosition = 0;
        this.tagHeader = createTagHeader(readBytes(input, 10));
        int rc = this.tagHeader;
        if (rc == 0) {
            onNoTagHeaderFound();
        } else if (onStartTagHeader(rc) == 1) {
            onEndTag();
        } else {
            while (this.readerPosition < this.tagHeader.getSize()) {
                FrameHeader frameHeader = createFrameHeader(readBytes(input, 10));
                if (checkForNullString(frameHeader.getId())) {
                    break;
                } else if (onStartFrameHeader(frameHeader, input) == 1) {
                    if (frameHeader.getSize() + this.readerPosition > this.tagHeader.getSize()) {
                        break;
                    }
                    skipBytes(input, frameHeader.getSize());
                }
            }
            onEndTag();
        }
    }

    private boolean checkForNullString(String s) {
        if (s.isEmpty()) {
            return true;
        }
        if (s.charAt(0) != '\u0000') {
            return false;
        }
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) != '\u0000') {
                return false;
            }
        }
        return true;
    }

    char[] readBytes(InputStream input, int number) throws IOException, ID3ReaderException {
        char[] header = new char[number];
        int i = 0;
        while (i < number) {
            int b = input.read();
            this.readerPosition++;
            if (b != -1) {
                header[i] = (char) b;
                i++;
            } else {
                throw new ID3ReaderException("Unexpected end of stream");
            }
        }
        return header;
    }

    void skipBytes(InputStream input, int number) throws IOException {
        if (number <= 0) {
            number = 1;
        }
        IOUtils.skipFully(input, (long) number);
        this.readerPosition += number;
    }

    private TagHeader createTagHeader(char[] source) throws ID3ReaderException {
        boolean z = true;
        if (source[0] != 'I' || source[1] != 'D' || source[2] != '3') {
            z = false;
        }
        boolean hasTag = z;
        if (source.length != 10) {
            throw new ID3ReaderException("Length of header must be 10");
        } else if (!hasTag) {
            return null;
        } else {
            return new TagHeader(new String(source, 0, 3), unsynchsafe(((source[8] << 8) | ((source[6] << 24) | (source[7] << 16))) | source[9]), (char) ((source[3] << 8) | source[4]), (byte) source[5]);
        }
    }

    private FrameHeader createFrameHeader(char[] source) throws ID3ReaderException {
        if (source.length == 10) {
            String id = new String(source, 0, 4);
            int size = (((source[4] << 24) | (source[5] << 16)) | (source[6] << 8)) | source[7];
            TagHeader tagHeader = this.tagHeader;
            if (tagHeader != null && tagHeader.getVersion() >= 'Ѐ') {
                size = unsynchsafe(size);
            }
            return new FrameHeader(id, size, (char) ((source[8] << 8) | source[9]));
        }
        throw new ID3ReaderException("Length of header must be 10");
    }

    private int unsynchsafe(int in) {
        int out = 0;
        for (int mask = 2130706432; mask != 0; mask >>= 8) {
            out = (out >> 1) | (in & mask);
        }
        return out;
    }

    protected int readString(StringBuilder buffer, InputStream input, int max) throws IOException, ID3ReaderException {
        if (max > 0) {
            char[] encoding = readBytes(input, 1);
            max--;
            if (encoding[0] != '\u0001') {
                if (encoding[0] != '\u0002') {
                    if (encoding[0] == '\u0003') {
                        return readUnicodeString(buffer, input, max, Charset.forName("UTF-8")) + 1;
                    }
                    return readISOString(buffer, input, max) + 1;
                }
            }
            return readUnicodeString(buffer, input, max, Charset.forName("UTF-16")) + 1;
        }
        if (buffer != null) {
            buffer.append("");
        }
        return 0;
    }

    protected int readISOString(StringBuilder buffer, InputStream input, int max) throws IOException, ID3ReaderException {
        int bytesRead = 0;
        while (true) {
            bytesRead++;
            if (bytesRead > max) {
                break;
            }
            char read = (char) input.read();
            char c = read;
            if (read <= '\u0000') {
                break;
            } else if (buffer != null) {
                buffer.append(c);
            }
        }
        return bytesRead;
    }

    private int readUnicodeString(StringBuilder strBuffer, InputStream input, int max, Charset charset) throws IOException, ID3ReaderException {
        byte[] buffer = new byte[max];
        int cZero = -1;
        int i = 0;
        while (i < max) {
            int c = input.read();
            if (c == -1) {
                break;
            }
            if (c != 0) {
                buffer[i] = (byte) c;
                cZero = -1;
            } else if (cZero == 0) {
                break;
            } else {
                cZero = 0;
            }
            i++;
        }
        if (strBuffer != null) {
            strBuffer.append(charset.newDecoder().decode(ByteBuffer.wrap(buffer)).toString());
        }
        return i;
    }

    int onStartTagHeader(TagHeader header) {
        return 1;
    }

    int onStartFrameHeader(FrameHeader header, InputStream input) throws IOException, ID3ReaderException {
        return 1;
    }

    void onEndTag() {
    }

    void onNoTagHeaderFound() {
    }
}
