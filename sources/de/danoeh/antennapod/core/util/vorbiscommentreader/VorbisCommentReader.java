package de.danoeh.antennapod.core.util.vorbiscommentreader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import org.apache.commons.io.EndianUtils;
import org.apache.commons.io.IOUtils;

public abstract class VorbisCommentReader {
    private static final int FIRST_PAGE_LENGTH = 58;
    private static final int PACKET_TYPE_COMMENT = 3;
    private static final int PACKET_TYPE_IDENTIFICATION = 1;
    private static final int SECOND_PAGE_MAX_LENGTH = 67108864;

    protected abstract boolean onContentVectorKey(String str);

    protected abstract void onContentVectorValue(String str, String str2) throws VorbisCommentReaderException;

    protected abstract void onEndOfComment();

    protected abstract void onError(VorbisCommentReaderException vorbisCommentReaderException);

    protected abstract void onNoVorbisCommentFound();

    protected abstract void onVorbisCommentFound();

    protected abstract void onVorbisCommentHeaderFound(VorbisCommentHeader vorbisCommentHeader);

    public void readInputStream(InputStream input) throws VorbisCommentReaderException {
        try {
            if (findIdentificationHeader(input)) {
                onVorbisCommentFound();
                input = new OggInputStream(input);
                if (findCommentHeader(input)) {
                    VorbisCommentHeader commentHeader = readCommentHeader(input);
                    if (commentHeader != null) {
                        onVorbisCommentHeaderFound(commentHeader);
                        for (int i = 0; ((long) i) < commentHeader.getUserCommentLength(); i++) {
                            try {
                                long vectorLength = EndianUtils.readSwappedUnsignedInteger(input);
                                String key = readContentVectorKey(input, vectorLength).toLowerCase();
                                if (onContentVectorKey(key)) {
                                    onContentVectorValue(key, readUTF8String(input, (long) ((int) ((vectorLength - ((long) key.length())) - 1))));
                                } else {
                                    IOUtils.skipFully(input, (vectorLength - ((long) key.length())) - 1);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        onEndOfComment();
                    }
                } else {
                    onError(new VorbisCommentReaderException("No comment header found"));
                }
            } else {
                onNoVorbisCommentFound();
            }
        } catch (Throwable e2) {
            onError(new VorbisCommentReaderException(e2));
        }
    }

    private String readUTF8String(InputStream input, long length) throws IOException {
        byte[] buffer = new byte[((int) length)];
        IOUtils.readFully(input, buffer);
        return Charset.forName("UTF-8").newDecoder().decode(ByteBuffer.wrap(buffer)).toString();
    }

    private boolean findIdentificationHeader(InputStream input) throws IOException {
        byte[] buffer = new byte[58];
        IOUtils.readFully(input, buffer);
        int i = 6;
        while (i < buffer.length) {
            if (buffer[i - 5] == (byte) 118 && buffer[i - 4] == (byte) 111 && buffer[i - 3] == (byte) 114 && buffer[i - 2] == (byte) 98 && buffer[i - 1] == (byte) 105 && buffer[i] == (byte) 115 && buffer[i - 6] == (byte) 1) {
                return true;
            }
            i++;
        }
        return false;
    }

    private boolean findCommentHeader(InputStream input) throws IOException {
        char[] buffer = new char[("vorbis".length() + 1)];
        for (int bytesRead = 0; bytesRead < SECOND_PAGE_MAX_LENGTH; bytesRead++) {
            char c = (char) input.read();
            int dest = -1;
            if (c == '\u0003') {
                dest = 0;
            } else if (c == 'b') {
                dest = 4;
            } else if (c == 'i') {
                dest = 5;
            } else if (c == 'o') {
                dest = 2;
            } else if (c != 'v') {
                switch (c) {
                    case 'r':
                        dest = 3;
                        break;
                    case 's':
                        dest = 6;
                        break;
                    default:
                        break;
                }
            } else {
                dest = 1;
            }
            if (dest >= 0) {
                buffer[dest] = c;
                if (buffer[1] == 'v' && buffer[2] == 'o' && buffer[3] == 'r' && buffer[4] == 'b' && buffer[5] == 'i' && buffer[6] == 's' && buffer[0] == '\u0003') {
                    return true;
                }
            } else {
                Arrays.fill(buffer, '\u0000');
            }
        }
        return false;
    }

    private VorbisCommentHeader readCommentHeader(InputStream input) throws IOException, VorbisCommentReaderException {
        try {
            return new VorbisCommentHeader(readUTF8String(input, EndianUtils.readSwappedUnsignedInteger(input)), EndianUtils.readSwappedUnsignedInteger(input));
        } catch (Throwable e) {
            throw new VorbisCommentReaderException(e);
        }
    }

    private String readContentVectorKey(InputStream input, long vectorLength) throws IOException {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; ((long) i) < vectorLength; i++) {
            char c = (char) input.read();
            if (c == '=') {
                return builder.toString();
            }
            builder.append(c);
        }
        return null;
    }
}
