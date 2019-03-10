package org.jsoup.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Locale;
import kotlin.text.Typography;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharUtils;
import org.jsoup.UncheckedIOException;
import org.jsoup.helper.Validate;

public final class CharacterReader {
    static final char EOF = '￿';
    static final int maxBufferLen = 32768;
    private static final int maxStringCacheLen = 12;
    private static final int readAheadLimit = 24576;
    private int bufLength;
    private int bufMark;
    private int bufPos;
    private int bufSplitPoint;
    private final char[] charBuf;
    private final Reader reader;
    private int readerPos;
    private final String[] stringCache;

    public CharacterReader(Reader input, int sz) {
        this.stringCache = new String[512];
        Validate.notNull(input);
        Validate.isTrue(input.markSupported());
        this.reader = input;
        int i = 32768;
        if (sz <= 32768) {
            i = sz;
        }
        this.charBuf = new char[i];
        bufferUp();
    }

    public CharacterReader(Reader input) {
        this(input, 32768);
    }

    public CharacterReader(String input) {
        this(new StringReader(input), input.length());
    }

    private void bufferUp() {
        int i = this.bufPos;
        if (i >= this.bufSplitPoint) {
            try {
                this.readerPos += i;
                this.reader.skip((long) i);
                this.reader.mark(32768);
                this.bufLength = this.reader.read(this.charBuf);
                this.reader.reset();
                this.bufPos = 0;
                this.bufMark = 0;
                i = this.bufLength;
                int i2 = readAheadLimit;
                if (i <= readAheadLimit) {
                    i2 = this.bufLength;
                }
                this.bufSplitPoint = i2;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    public int pos() {
        return this.readerPos + this.bufPos;
    }

    public boolean isEmpty() {
        return this.bufPos >= this.bufLength;
    }

    public char current() {
        bufferUp();
        return isEmpty() ? EOF : this.charBuf[this.bufPos];
    }

    char consume() {
        bufferUp();
        char val = isEmpty() ? EOF : this.charBuf[this.bufPos];
        this.bufPos++;
        return val;
    }

    void unconsume() {
        this.bufPos--;
    }

    public void advance() {
        this.bufPos++;
    }

    void mark() {
        this.bufMark = this.bufPos;
    }

    void rewindToMark() {
        this.bufPos = this.bufMark;
    }

    int nextIndexOf(char c) {
        bufferUp();
        for (int i = this.bufPos; i < this.bufLength; i++) {
            if (c == this.charBuf[i]) {
                return i - this.bufPos;
            }
        }
        return -1;
    }

    int nextIndexOf(CharSequence seq) {
        bufferUp();
        char startChar = seq.charAt('\u0000');
        int offset = this.bufPos;
        while (offset < this.bufLength) {
            if (startChar != this.charBuf[offset]) {
                while (true) {
                    offset++;
                    if (offset >= this.bufLength || startChar == this.charBuf[offset]) {
                    }
                }
            }
            int i = offset + 1;
            int last = (seq.length() + i) - 1;
            int i2 = this.bufLength;
            if (offset < i2 && last <= i2) {
                i2 = 1;
                while (i < last && seq.charAt(i2) == this.charBuf[i]) {
                    i++;
                    i2++;
                }
                if (i == last) {
                    return offset - this.bufPos;
                }
            }
            offset++;
        }
        return -1;
    }

    public String consumeTo(char c) {
        int offset = nextIndexOf(c);
        if (offset == -1) {
            return consumeToEnd();
        }
        String consumed = cacheString(this.charBuf, this.stringCache, this.bufPos, offset);
        this.bufPos += offset;
        return consumed;
    }

    String consumeTo(String seq) {
        int offset = nextIndexOf((CharSequence) seq);
        if (offset == -1) {
            return consumeToEnd();
        }
        String consumed = cacheString(this.charBuf, this.stringCache, this.bufPos, offset);
        this.bufPos += offset;
        return consumed;
    }

    public String consumeToAny(char... chars) {
        bufferUp();
        int start = this.bufPos;
        int remaining = this.bufLength;
        char[] val = this.charBuf;
        loop0:
        while (this.bufPos < remaining) {
            for (char c : chars) {
                if (val[this.bufPos] == c) {
                    break loop0;
                }
            }
            this.bufPos++;
        }
        int i = this.bufPos;
        return i > start ? cacheString(this.charBuf, this.stringCache, start, i - start) : "";
    }

    String consumeToAnySorted(char... chars) {
        int i;
        bufferUp();
        int start = this.bufPos;
        int remaining = this.bufLength;
        char[] val = this.charBuf;
        while (true) {
            i = this.bufPos;
            if (i >= remaining) {
                break;
            } else if (Arrays.binarySearch(chars, val[i]) >= 0) {
                break;
            } else {
                this.bufPos++;
            }
            i = this.bufPos;
            return i <= start ? cacheString(this.charBuf, this.stringCache, start, i - start) : "";
        }
        i = this.bufPos;
        if (i <= start) {
        }
    }

    String consumeData() {
        int i;
        bufferUp();
        int start = this.bufPos;
        int remaining = this.bufLength;
        char[] val = this.charBuf;
        while (true) {
            i = this.bufPos;
            if (i >= remaining) {
                break;
            }
            char c = val[i];
            if (c != Typography.amp && c != Typography.less) {
                if (c == '\u0000') {
                    break;
                }
                this.bufPos = i + 1;
            }
            i = this.bufPos;
            return i <= start ? cacheString(this.charBuf, this.stringCache, start, i - start) : "";
        }
        i = this.bufPos;
        if (i <= start) {
        }
    }

    String consumeTagName() {
        int i;
        bufferUp();
        int start = this.bufPos;
        int remaining = this.bufLength;
        char[] val = this.charBuf;
        while (true) {
            i = this.bufPos;
            if (i >= remaining) {
                break;
            }
            char c = val[i];
            if (c != '\t' && c != '\n' && c != CharUtils.CR && c != '\f' && c != ' ' && c != IOUtils.DIR_SEPARATOR_UNIX && c != Typography.greater) {
                if (c == '\u0000') {
                    break;
                }
                this.bufPos = i + 1;
            }
            i = this.bufPos;
            return i <= start ? cacheString(this.charBuf, this.stringCache, start, i - start) : "";
        }
        i = this.bufPos;
        if (i <= start) {
        }
    }

    String consumeToEnd() {
        bufferUp();
        String data = this.charBuf;
        String[] strArr = this.stringCache;
        int i = this.bufPos;
        data = cacheString(data, strArr, i, this.bufLength - i);
        this.bufPos = this.bufLength;
        return data;
    }

    String consumeLetterSequence() {
        bufferUp();
        int start = this.bufPos;
        while (true) {
            char c = this.bufPos;
            if (c >= this.bufLength) {
                break;
            }
            c = this.charBuf[c];
            if (c >= 'A') {
                if (c > 'Z') {
                }
                this.bufPos++;
            }
            if (c < 'a' || c > 'z') {
                if (!Character.isLetter(c)) {
                    break;
                }
            }
            this.bufPos++;
            return cacheString(this.charBuf, this.stringCache, start, this.bufPos - start);
        }
        return cacheString(this.charBuf, this.stringCache, start, this.bufPos - start);
    }

    String consumeLetterThenDigitSequence() {
        bufferUp();
        int start = this.bufPos;
        while (true) {
            char c = this.bufPos;
            if (c >= this.bufLength) {
                break;
            }
            c = this.charBuf[c];
            if (c >= 'A') {
                if (c > 'Z') {
                }
                this.bufPos++;
            }
            if (c < 'a' || c > 'z') {
                if (!Character.isLetter(c)) {
                    break;
                }
            }
            this.bufPos++;
            while (!isEmpty()) {
                char[] cArr = this.charBuf;
                int i = this.bufPos;
                c = cArr[i];
                if (c >= '0' || c > '9') {
                    break;
                }
                this.bufPos = i + 1;
            }
            return cacheString(this.charBuf, this.stringCache, start, this.bufPos - start);
        }
        while (!isEmpty()) {
            char[] cArr2 = this.charBuf;
            int i2 = this.bufPos;
            c = cArr2[i2];
            if (c >= '0') {
            }
        }
        return cacheString(this.charBuf, this.stringCache, start, this.bufPos - start);
    }

    String consumeHexSequence() {
        bufferUp();
        int start = this.bufPos;
        while (true) {
            char c = this.bufPos;
            if (c >= this.bufLength) {
                break;
            }
            c = this.charBuf[c];
            if (c >= '0') {
                if (c > '9') {
                }
                this.bufPos++;
            }
            if (c < 'A' || c > 'F') {
                if (c < 'a' || c > 'f') {
                }
            }
            this.bufPos++;
            return cacheString(this.charBuf, this.stringCache, start, this.bufPos - start);
        }
        return cacheString(this.charBuf, this.stringCache, start, this.bufPos - start);
    }

    String consumeDigitSequence() {
        bufferUp();
        int start = this.bufPos;
        while (true) {
            int i = this.bufPos;
            if (i >= this.bufLength) {
                break;
            }
            char c = this.charBuf[i];
            if (c >= '0' && c <= '9') {
                this.bufPos = i + 1;
            }
            return cacheString(this.charBuf, this.stringCache, start, this.bufPos - start);
        }
        return cacheString(this.charBuf, this.stringCache, start, this.bufPos - start);
    }

    boolean matches(char c) {
        return !isEmpty() && this.charBuf[this.bufPos] == c;
    }

    boolean matches(String seq) {
        bufferUp();
        int scanLength = seq.length();
        if (scanLength > this.bufLength - this.bufPos) {
            return false;
        }
        for (int offset = 0; offset < scanLength; offset++) {
            if (seq.charAt(offset) != this.charBuf[this.bufPos + offset]) {
                return false;
            }
        }
        return true;
    }

    boolean matchesIgnoreCase(String seq) {
        bufferUp();
        int scanLength = seq.length();
        if (scanLength > this.bufLength - this.bufPos) {
            return false;
        }
        for (int offset = 0; offset < scanLength; offset++) {
            if (Character.toUpperCase(seq.charAt(offset)) != Character.toUpperCase(this.charBuf[this.bufPos + offset])) {
                return false;
            }
        }
        return true;
    }

    boolean matchesAny(char... seq) {
        if (isEmpty()) {
            return false;
        }
        bufferUp();
        char c = this.charBuf[this.bufPos];
        for (char seek : seq) {
            if (seek == c) {
                return true;
            }
        }
        return false;
    }

    boolean matchesAnySorted(char[] seq) {
        bufferUp();
        return !isEmpty() && Arrays.binarySearch(seq, this.charBuf[this.bufPos]) >= 0;
    }

    boolean matchesLetter() {
        boolean z = false;
        if (isEmpty()) {
            return false;
        }
        char c = this.charBuf[this.bufPos];
        if (c >= 'A') {
            if (c > 'Z') {
            }
            z = true;
            return z;
        }
        if (c < 'a' || c > 'z') {
            if (Character.isLetter(c)) {
            }
            return z;
        }
        z = true;
        return z;
    }

    boolean matchesDigit() {
        boolean z = false;
        if (isEmpty()) {
            return false;
        }
        char c = this.charBuf[this.bufPos];
        if (c >= '0' && c <= '9') {
            z = true;
        }
        return z;
    }

    boolean matchConsume(String seq) {
        bufferUp();
        if (!matches(seq)) {
            return false;
        }
        this.bufPos += seq.length();
        return true;
    }

    boolean matchConsumeIgnoreCase(String seq) {
        if (!matchesIgnoreCase(seq)) {
            return false;
        }
        this.bufPos += seq.length();
        return true;
    }

    boolean containsIgnoreCase(String seq) {
        CharSequence loScan = seq.toLowerCase(Locale.ENGLISH);
        CharSequence hiScan = seq.toUpperCase(Locale.ENGLISH);
        if (nextIndexOf(loScan) <= -1) {
            if (nextIndexOf(hiScan) <= -1) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        char[] cArr = this.charBuf;
        int i = this.bufPos;
        return new String(cArr, i, this.bufLength - i);
    }

    private static String cacheString(char[] charBuf, String[] stringCache, int start, int count) {
        if (count > 12) {
            return new String(charBuf, start, count);
        }
        if (count < 1) {
            return "";
        }
        int hash = 0;
        int offset = start;
        int i = 0;
        while (i < count) {
            hash = (hash * 31) + charBuf[offset];
            i++;
            offset++;
        }
        int index = hash & (stringCache.length - 1);
        String cached = stringCache[index];
        if (cached == null) {
            cached = new String(charBuf, start, count);
            stringCache[index] = cached;
        } else if (rangeEquals(charBuf, start, count, cached)) {
            return cached;
        } else {
            cached = new String(charBuf, start, count);
            stringCache[index] = cached;
        }
        return cached;
    }

    static boolean rangeEquals(char[] charBuf, int start, int count, String cached) {
        if (count != cached.length()) {
            return false;
        }
        int i = start;
        int j = 0;
        while (true) {
            int count2 = count - 1;
            if (count == 0) {
                return true;
            }
            count = i + 1;
            int j2 = j + 1;
            if (charBuf[i] != cached.charAt(j)) {
                return false;
            }
            i = count;
            count = count2;
            j = j2;
        }
    }

    boolean rangeEquals(int start, int count, String cached) {
        return rangeEquals(this.charBuf, start, count, cached);
    }
}
