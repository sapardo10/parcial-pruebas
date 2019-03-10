package org.apache.commons.io.filefilter;

import java.io.Serializable;
import java.nio.charset.Charset;

public class MagicNumberFileFilter extends AbstractFileFilter implements Serializable {
    private static final long serialVersionUID = -547733176983104172L;
    private final long byteOffset;
    private final byte[] magicNumbers;

    public MagicNumberFileFilter(byte[] magicNumber) {
        this(magicNumber, 0);
    }

    public MagicNumberFileFilter(String magicNumber) {
        this(magicNumber, 0);
    }

    public MagicNumberFileFilter(String magicNumber, long offset) {
        if (magicNumber == null) {
            throw new IllegalArgumentException("The magic number cannot be null");
        } else if (magicNumber.isEmpty()) {
            throw new IllegalArgumentException("The magic number must contain at least one byte");
        } else if (offset >= 0) {
            this.magicNumbers = magicNumber.getBytes(Charset.defaultCharset());
            this.byteOffset = offset;
        } else {
            throw new IllegalArgumentException("The offset cannot be negative");
        }
    }

    public MagicNumberFileFilter(byte[] magicNumber, long offset) {
        if (magicNumber == null) {
            throw new IllegalArgumentException("The magic number cannot be null");
        } else if (magicNumber.length == 0) {
            throw new IllegalArgumentException("The magic number must contain at least one byte");
        } else if (offset >= 0) {
            this.magicNumbers = new byte[magicNumber.length];
            System.arraycopy(magicNumber, 0, this.magicNumbers, 0, magicNumber.length);
            this.byteOffset = offset;
        } else {
            throw new IllegalArgumentException("The offset cannot be negative");
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean accept(java.io.File r6) {
        /*
        r5 = this;
        r0 = 0;
        if (r6 == 0) goto L_0x0041;
    L_0x0003:
        r1 = r6.isFile();
        if (r1 == 0) goto L_0x0041;
    L_0x0009:
        r1 = r6.canRead();
        if (r1 == 0) goto L_0x0041;
    L_0x000f:
        r1 = 0;
        r2 = r5.magicNumbers;	 Catch:{ IOException -> 0x003c, all -> 0x0037 }
        r2 = r2.length;	 Catch:{ IOException -> 0x003c, all -> 0x0037 }
        r2 = new byte[r2];	 Catch:{ IOException -> 0x003c, all -> 0x0037 }
        r3 = new java.io.RandomAccessFile;	 Catch:{ IOException -> 0x003c, all -> 0x0037 }
        r4 = "r";
        r3.<init>(r6, r4);	 Catch:{ IOException -> 0x003c, all -> 0x0037 }
        r1 = r3;
        r3 = r5.byteOffset;	 Catch:{ IOException -> 0x003c, all -> 0x0037 }
        r1.seek(r3);	 Catch:{ IOException -> 0x003c, all -> 0x0037 }
        r3 = r1.read(r2);	 Catch:{ IOException -> 0x003c, all -> 0x0037 }
        r4 = r5.magicNumbers;	 Catch:{ IOException -> 0x003c, all -> 0x0037 }
        r4 = r4.length;	 Catch:{ IOException -> 0x003c, all -> 0x0037 }
        if (r3 == r4) goto L_0x0030;
    L_0x002c:
        org.apache.commons.io.IOUtils.closeQuietly(r1);
        return r0;
    L_0x0030:
        r4 = r5.magicNumbers;	 Catch:{ IOException -> 0x003c, all -> 0x0037 }
        r0 = java.util.Arrays.equals(r4, r2);	 Catch:{ IOException -> 0x003c, all -> 0x0037 }
        goto L_0x002c;
    L_0x0037:
        r0 = move-exception;
        org.apache.commons.io.IOUtils.closeQuietly(r1);
        throw r0;
    L_0x003c:
        r2 = move-exception;
        org.apache.commons.io.IOUtils.closeQuietly(r1);
        goto L_0x0042;
    L_0x0042:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.filefilter.MagicNumberFileFilter.accept(java.io.File):boolean");
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append("(");
        builder.append(new String(this.magicNumbers, Charset.defaultCharset()));
        builder.append(",");
        builder.append(this.byteOffset);
        builder.append(")");
        return builder.toString();
    }
}
