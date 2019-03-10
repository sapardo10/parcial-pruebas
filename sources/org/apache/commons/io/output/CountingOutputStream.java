package org.apache.commons.io.output;

import java.io.OutputStream;

public class CountingOutputStream extends ProxyOutputStream {
    private long count = 0;

    public CountingOutputStream(OutputStream out) {
        super(out);
    }

    protected synchronized void beforeWrite(int n) {
        this.count += (long) n;
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
