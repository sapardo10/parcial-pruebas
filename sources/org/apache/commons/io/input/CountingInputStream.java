package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;

public class CountingInputStream extends ProxyInputStream {
    private long count;

    public CountingInputStream(InputStream in) {
        super(in);
    }

    public synchronized long skip(long length) throws IOException {
        long skip;
        skip = super.skip(length);
        this.count += skip;
        return skip;
    }

    protected synchronized void afterRead(int n) {
        if (n != -1) {
            this.count += (long) n;
        }
    }

    public int getCount() {
        long result = getByteCount();
        if (result <= 2147483647L) {
            return (int) result;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("The byte count ");
        stringBuilder.append(result);
        stringBuilder.append(" is too large to be converted to an int");
        throw new ArithmeticException(stringBuilder.toString());
    }

    public int resetCount() {
        long result = resetByteCount();
        if (result <= 2147483647L) {
            return (int) result;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("The byte count ");
        stringBuilder.append(result);
        stringBuilder.append(" is too large to be converted to an int");
        throw new ArithmeticException(stringBuilder.toString());
    }

    public synchronized long getByteCount() {
        return this.count;
    }

    public synchronized long resetByteCount() {
        long tmp;
        tmp = this.count;
        this.count = 0;
        return tmp;
    }
}
