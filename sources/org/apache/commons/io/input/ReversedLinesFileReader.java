package org.apache.commons.io.input;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class ReversedLinesFileReader implements Closeable {
    private final int avoidNewlineSplitBufferSize;
    private final int blockSize;
    private final int byteDecrement;
    private FilePart currentFilePart;
    private final Charset encoding;
    private final byte[][] newLineSequences;
    private final RandomAccessFile randomAccessFile;
    private final long totalBlockCount;
    private final long totalByteLength;
    private boolean trailingNewlineOfFileSkipped;

    private class FilePart {
        private int currentLastBytePos;
        private final byte[] data;
        private byte[] leftOver;
        private final long no;

        private FilePart(long no, int length, byte[] leftOverOfLastFilePart) throws IOException {
            this.no = no;
            this.data = new byte[((leftOverOfLastFilePart != null ? leftOverOfLastFilePart.length : 0) + length)];
            long off = (no - 1) * ((long) ReversedLinesFileReader.this.blockSize);
            if (no > 0) {
                ReversedLinesFileReader.this.randomAccessFile.seek(off);
                if (ReversedLinesFileReader.this.randomAccessFile.read(this.data, 0, length) != length) {
                    throw new IllegalStateException("Count of requested bytes and actually read bytes don't match");
                }
            }
            if (leftOverOfLastFilePart != null) {
                System.arraycopy(leftOverOfLastFilePart, 0, this.data, length, leftOverOfLastFilePart.length);
            }
            this.currentLastBytePos = this.data.length - 1;
            this.leftOver = null;
        }

        private FilePart rollOver() throws IOException {
            if (this.currentLastBytePos <= -1) {
                long j = this.no;
                if (j > 1) {
                    ReversedLinesFileReader reversedLinesFileReader = ReversedLinesFileReader.this;
                    return new FilePart(j - 1, reversedLinesFileReader.blockSize, this.leftOver);
                } else if (this.leftOver == null) {
                    return null;
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Unexpected leftover of the last block: leftOverOfThisFilePart=");
                    stringBuilder.append(new String(this.leftOver, ReversedLinesFileReader.this.encoding));
                    throw new IllegalStateException(stringBuilder.toString());
                }
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append("Current currentLastCharPos unexpectedly positive... last readLine() should have returned something! currentLastCharPos=");
            stringBuilder.append(this.currentLastBytePos);
            throw new IllegalStateException(stringBuilder.toString());
        }

        private String readLine() throws IOException {
            String line = null;
            boolean isLastFilePart = this.no == 1;
            int i = this.currentLastBytePos;
            while (i > -1) {
                if (!isLastFilePart && i < ReversedLinesFileReader.this.avoidNewlineSplitBufferSize) {
                    createLeftOver();
                    break;
                }
                int newLineMatchByteCount = getNewLineMatchByteCount(this.data, i);
                int newLineMatchByteCount2 = newLineMatchByteCount;
                if (newLineMatchByteCount > 0) {
                    newLineMatchByteCount = i + 1;
                    int lineLengthBytes = (this.currentLastBytePos - newLineMatchByteCount) + 1;
                    if (lineLengthBytes >= 0) {
                        byte[] lineData = new byte[lineLengthBytes];
                        System.arraycopy(this.data, newLineMatchByteCount, lineData, 0, lineLengthBytes);
                        line = new String(lineData, ReversedLinesFileReader.this.encoding);
                        this.currentLastBytePos = i - newLineMatchByteCount2;
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Unexpected negative line length=");
                        stringBuilder.append(lineLengthBytes);
                        throw new IllegalStateException(stringBuilder.toString());
                    }
                }
                i -= ReversedLinesFileReader.this.byteDecrement;
                if (i < 0) {
                    createLeftOver();
                    break;
                }
            }
            if (isLastFilePart) {
                byte[] bArr = this.leftOver;
                if (bArr != null) {
                    line = new String(bArr, ReversedLinesFileReader.this.encoding);
                    this.leftOver = null;
                    return line;
                }
            }
            return line;
        }

        private void createLeftOver() {
            int lineLengthBytes = this.currentLastBytePos + 1;
            if (lineLengthBytes > 0) {
                this.leftOver = new byte[lineLengthBytes];
                System.arraycopy(this.data, 0, this.leftOver, 0, lineLengthBytes);
            } else {
                this.leftOver = null;
            }
            this.currentLastBytePos = -1;
        }

        private int getNewLineMatchByteCount(byte[] data, int i) {
            for (byte[] newLineSequence : ReversedLinesFileReader.this.newLineSequences) {
                boolean match = true;
                for (int j = newLineSequence.length - 1; j >= 0; j--) {
                    int k = (i + j) - (newLineSequence.length - 1);
                    int i2 = (k < 0 || data[k] != newLineSequence[j]) ? 0 : 1;
                    match &= i2;
                }
                if (match) {
                    return newLineSequence.length;
                }
            }
            return 0;
        }
    }

    @Deprecated
    public ReversedLinesFileReader(File file) throws IOException {
        this(file, 4096, Charset.defaultCharset());
    }

    public ReversedLinesFileReader(File file, Charset charset) throws IOException {
        this(file, 4096, charset);
    }

    public ReversedLinesFileReader(File file, int blockSize, Charset encoding) throws IOException {
        this.trailingNewlineOfFileSkipped = false;
        this.blockSize = blockSize;
        this.encoding = encoding;
        Charset charset = Charsets.toCharset(encoding);
        if (charset.newEncoder().maxBytesPerChar() == 1.0f) {
            this.byteDecrement = 1;
        } else if (charset == Charsets.UTF_8) {
            this.byteDecrement = 1;
        } else {
            if (!(charset == Charset.forName("Shift_JIS") || charset == Charset.forName("windows-31j") || charset == Charset.forName("x-windows-949") || charset == Charset.forName("gbk"))) {
                if (charset != Charset.forName("x-windows-950")) {
                    if (charset != Charsets.UTF_16BE) {
                        if (charset != Charsets.UTF_16LE) {
                            if (charset == Charsets.UTF_16) {
                                throw new UnsupportedEncodingException("For UTF-16, you need to specify the byte order (use UTF-16BE or UTF-16LE)");
                            }
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Encoding ");
                            stringBuilder.append(encoding);
                            stringBuilder.append(" is not supported yet (feel free to ");
                            stringBuilder.append("submit a patch)");
                            throw new UnsupportedEncodingException(stringBuilder.toString());
                        }
                    }
                    this.byteDecrement = 2;
                }
            }
            this.byteDecrement = 1;
        }
        this.newLineSequences = new byte[][]{IOUtils.LINE_SEPARATOR_WINDOWS.getBytes(encoding), "\n".getBytes(encoding), StringUtils.CR.getBytes(encoding)};
        this.avoidNewlineSplitBufferSize = this.newLineSequences[0].length;
        this.randomAccessFile = new RandomAccessFile(file, "r");
        this.totalByteLength = this.randomAccessFile.length();
        long j = this.totalByteLength;
        int lastBlockLength = (int) (j % ((long) blockSize));
        if (lastBlockLength > 0) {
            this.totalBlockCount = (j / ((long) blockSize)) + 1;
        } else {
            this.totalBlockCount = j / ((long) blockSize);
            if (j > 0) {
                lastBlockLength = blockSize;
            }
        }
        this.currentFilePart = new FilePart(this.totalBlockCount, lastBlockLength, null);
    }

    public ReversedLinesFileReader(File file, int blockSize, String encoding) throws IOException {
        this(file, blockSize, Charsets.toCharset(encoding));
    }

    public String readLine() throws IOException {
        String line = this.currentFilePart.readLine();
        while (line == null) {
            this.currentFilePart = this.currentFilePart.rollOver();
            FilePart filePart = this.currentFilePart;
            if (filePart == null) {
                break;
            }
            line = filePart.readLine();
        }
        if (!"".equals(line) || this.trailingNewlineOfFileSkipped) {
            return line;
        }
        this.trailingNewlineOfFileSkipped = true;
        return readLine();
    }

    public void close() throws IOException {
        this.randomAccessFile.close();
    }
}
