package okio;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharUtils;
import org.objenesis.instantiator.util.ClassDefinitionUtils;

final class Base64 {
    private static final byte[] MAP = new byte[]{(byte) 65, (byte) 66, (byte) 67, (byte) 68, (byte) 69, (byte) 70, (byte) 71, (byte) 72, (byte) 73, (byte) 74, (byte) 75, (byte) 76, (byte) 77, (byte) 78, (byte) 79, (byte) 80, (byte) 81, (byte) 82, (byte) 83, (byte) 84, (byte) 85, (byte) 86, (byte) 87, (byte) 88, ClassDefinitionUtils.OPS_dup, (byte) 90, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 106, (byte) 107, (byte) 108, (byte) 109, (byte) 110, (byte) 111, (byte) 112, (byte) 113, (byte) 114, (byte) 115, (byte) 116, (byte) 117, (byte) 118, (byte) 119, (byte) 120, (byte) 121, (byte) 122, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 43, (byte) 47};
    private static final byte[] URL_MAP = new byte[]{(byte) 65, (byte) 66, (byte) 67, (byte) 68, (byte) 69, (byte) 70, (byte) 71, (byte) 72, (byte) 73, (byte) 74, (byte) 75, (byte) 76, (byte) 77, (byte) 78, (byte) 79, (byte) 80, (byte) 81, (byte) 82, (byte) 83, (byte) 84, (byte) 85, (byte) 86, (byte) 87, (byte) 88, ClassDefinitionUtils.OPS_dup, (byte) 90, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 106, (byte) 107, (byte) 108, (byte) 109, (byte) 110, (byte) 111, (byte) 112, (byte) 113, (byte) 114, (byte) 115, (byte) 116, (byte) 117, (byte) 118, (byte) 119, (byte) 120, (byte) 121, (byte) 122, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 45, (byte) 95};

    private static java.lang.String encode(byte[] r8, byte[] r9) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:14:0x00c6 in {2, 5, 6, 7, 10, 13} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r0 = r8.length;
        r0 = r0 + 2;
        r0 = r0 / 3;
        r0 = r0 * 4;
        r1 = new byte[r0];
        r2 = 0;
        r3 = r8.length;
        r4 = r8.length;
        r4 = r4 % 3;
        r3 = r3 - r4;
        r4 = 0;
    L_0x0010:
        if (r4 >= r3) goto L_0x0059;
    L_0x0012:
        r5 = r2 + 1;
        r6 = r8[r4];
        r6 = r6 & 255;
        r6 = r6 >> 2;
        r6 = r9[r6];
        r1[r2] = r6;
        r2 = r5 + 1;
        r6 = r8[r4];
        r6 = r6 & 3;
        r6 = r6 << 4;
        r7 = r4 + 1;
        r7 = r8[r7];
        r7 = r7 & 255;
        r7 = r7 >> 4;
        r6 = r6 | r7;
        r6 = r9[r6];
        r1[r5] = r6;
        r5 = r2 + 1;
        r6 = r4 + 1;
        r6 = r8[r6];
        r6 = r6 & 15;
        r6 = r6 << 2;
        r7 = r4 + 2;
        r7 = r8[r7];
        r7 = r7 & 255;
        r7 = r7 >> 6;
        r6 = r6 | r7;
        r6 = r9[r6];
        r1[r2] = r6;
        r2 = r5 + 1;
        r6 = r4 + 2;
        r6 = r8[r6];
        r6 = r6 & 63;
        r6 = r9[r6];
        r1[r5] = r6;
        r4 = r4 + 3;
        goto L_0x0010;
    L_0x0059:
        r4 = r8.length;
        r4 = r4 % 3;
        r5 = 61;
        switch(r4) {
            case 1: goto L_0x0096;
            case 2: goto L_0x0062;
            default: goto L_0x0061;
        };
    L_0x0061:
        goto L_0x00b7;
    L_0x0062:
        r4 = r2 + 1;
        r6 = r8[r3];
        r6 = r6 & 255;
        r6 = r6 >> 2;
        r6 = r9[r6];
        r1[r2] = r6;
        r2 = r4 + 1;
        r6 = r8[r3];
        r6 = r6 & 3;
        r6 = r6 << 4;
        r7 = r3 + 1;
        r7 = r8[r7];
        r7 = r7 & 255;
        r7 = r7 >> 4;
        r6 = r6 | r7;
        r6 = r9[r6];
        r1[r4] = r6;
        r4 = r2 + 1;
        r6 = r3 + 1;
        r6 = r8[r6];
        r6 = r6 & 15;
        r6 = r6 << 2;
        r6 = r9[r6];
        r1[r2] = r6;
        r2 = r4 + 1;
        r1[r4] = r5;
        goto L_0x00b7;
    L_0x0096:
        r4 = r2 + 1;
        r6 = r8[r3];
        r6 = r6 & 255;
        r6 = r6 >> 2;
        r6 = r9[r6];
        r1[r2] = r6;
        r2 = r4 + 1;
        r6 = r8[r3];
        r6 = r6 & 3;
        r6 = r6 << 4;
        r6 = r9[r6];
        r1[r4] = r6;
        r4 = r2 + 1;
        r1[r2] = r5;
        r2 = r4 + 1;
        r1[r4] = r5;
    L_0x00b7:
        r4 = new java.lang.String;	 Catch:{ UnsupportedEncodingException -> 0x00bf }
        r5 = "US-ASCII";	 Catch:{ UnsupportedEncodingException -> 0x00bf }
        r4.<init>(r1, r5);	 Catch:{ UnsupportedEncodingException -> 0x00bf }
        return r4;
    L_0x00bf:
        r4 = move-exception;
        r5 = new java.lang.AssertionError;
        r5.<init>(r4);
        throw r5;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Base64.encode(byte[], byte[]):java.lang.String");
    }

    private Base64() {
    }

    public static byte[] decode(String in) {
        int limit = in.length();
        while (limit > 0) {
            char c = in.charAt(limit - 1);
            if (c != '=' && c != '\n' && c != CharUtils.CR && c != ' ' && c != '\t') {
                break;
            }
            limit--;
        }
        byte[] out = new byte[((int) ((((long) limit) * 6) / 8))];
        int outCount = 0;
        int inCount = 0;
        int word = 0;
        for (int pos = 0; pos < limit; pos++) {
            int bits;
            char c2 = in.charAt(pos);
            if (c2 >= 'A' && c2 <= 'Z') {
                bits = c2 - 65;
            } else if (c2 >= 'a' && c2 <= 'z') {
                bits = c2 - 71;
            } else if (c2 < '0' || c2 > '9') {
                if (c2 != '+') {
                    if (c2 != '-') {
                        if (c2 != IOUtils.DIR_SEPARATOR_UNIX) {
                            if (c2 != '_') {
                                if (c2 == '\n' || c2 == CharUtils.CR || c2 == ' ') {
                                } else if (c2 != '\t') {
                                    return null;
                                } else {
                                }
                            }
                        }
                        bits = 63;
                    }
                }
                bits = 62;
            } else {
                bits = c2 + 4;
            }
            word = (word << 6) | ((byte) bits);
            inCount++;
            if (inCount % 4 == 0) {
                int i = outCount + 1;
                out[outCount] = (byte) (word >> 16);
                outCount = i + 1;
                out[i] = (byte) (word >> 8);
                i = outCount + 1;
                out[outCount] = (byte) word;
                outCount = i;
            }
        }
        int lastWordChars = inCount % 4;
        if (lastWordChars == 1) {
            return null;
        }
        int outCount2;
        if (lastWordChars == 2) {
            outCount2 = outCount + 1;
            out[outCount] = (byte) ((word << 12) >> 16);
            outCount = outCount2;
        } else if (lastWordChars == 3) {
            word <<= 6;
            outCount2 = outCount + 1;
            out[outCount] = (byte) (word >> 16);
            outCount = outCount2 + 1;
            out[outCount2] = (byte) (word >> 8);
        }
        if (outCount == out.length) {
            return out;
        }
        byte[] prefix = new byte[outCount];
        System.arraycopy(out, 0, prefix, 0, outCount);
        return prefix;
    }

    public static String encode(byte[] in) {
        return encode(in, MAP);
    }

    public static String encodeUrl(byte[] in) {
        return encode(in, URL_MAP);
    }
}
