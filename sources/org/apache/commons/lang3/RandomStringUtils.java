package org.apache.commons.lang3;

import java.util.Random;

public class RandomStringUtils {
    private static final Random RANDOM = new Random();

    public static java.lang.String random(int r7, int r8, int r9, boolean r10, boolean r11, char[] r12, java.util.Random r13) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:72:0x0118 in {2, 7, 9, 14, 17, 18, 26, 30, 32, 40, 41, 42, 47, 52, 56, 59, 62, 63, 64, 65, 67, 69, 71} preds:[]
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
        if (r7 != 0) goto L_0x0005;
    L_0x0002:
        r0 = "";
        return r0;
    L_0x0005:
        if (r7 < 0) goto L_0x00fc;
    L_0x0007:
        if (r12 == 0) goto L_0x0015;
    L_0x0009:
        r0 = r12.length;
        if (r0 == 0) goto L_0x000d;
    L_0x000c:
        goto L_0x0015;
    L_0x000d:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "The chars array must not be empty";
        r0.<init>(r1);
        throw r0;
        if (r8 != 0) goto L_0x002c;
    L_0x0018:
        if (r9 != 0) goto L_0x002c;
    L_0x001a:
        if (r12 == 0) goto L_0x001e;
    L_0x001c:
        r9 = r12.length;
        goto L_0x002f;
    L_0x001e:
        if (r10 != 0) goto L_0x0026;
    L_0x0020:
        if (r11 != 0) goto L_0x0026;
    L_0x0022:
        r9 = 1114111; // 0x10ffff float:1.561202E-39 double:5.50444E-318;
        goto L_0x002f;
        r9 = 123; // 0x7b float:1.72E-43 double:6.1E-322;
        r8 = 32;
        goto L_0x002f;
        if (r9 <= r8) goto L_0x00d8;
    L_0x002f:
        r0 = 48;
        r1 = 65;
        if (r12 != 0) goto L_0x0075;
    L_0x0035:
        r2 = 65;
        r3 = 48;
        if (r11 == 0) goto L_0x003f;
    L_0x003b:
        if (r9 <= r3) goto L_0x003e;
    L_0x003d:
        goto L_0x003f;
    L_0x003e:
        goto L_0x0044;
    L_0x003f:
        if (r10 == 0) goto L_0x0075;
    L_0x0041:
        if (r9 <= r2) goto L_0x003e;
    L_0x0043:
        goto L_0x0075;
    L_0x0044:
        r4 = new java.lang.IllegalArgumentException;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Parameter end (";
        r5.append(r6);
        r5.append(r9);
        r6 = ") must be greater then (";
        r5.append(r6);
        r5.append(r3);
        r3 = ") for generating digits ";
        r5.append(r3);
        r3 = "or greater then (";
        r5.append(r3);
        r5.append(r2);
        r2 = ") for generating letters.";
        r5.append(r2);
        r2 = r5.toString();
        r4.<init>(r2);
        throw r4;
        r2 = new java.lang.StringBuilder;
        r2.<init>(r7);
        r3 = r9 - r8;
    L_0x007d:
        r4 = r7 + -1;
        if (r7 == 0) goto L_0x00d3;
    L_0x0081:
        if (r12 != 0) goto L_0x0096;
    L_0x0083:
        r7 = r13.nextInt(r3);
        r7 = r7 + r8;
        r5 = java.lang.Character.getType(r7);
        if (r5 == 0) goto L_0x0092;
    L_0x008e:
        switch(r5) {
            case 18: goto L_0x0092;
            case 19: goto L_0x0092;
            default: goto L_0x0091;
        };
    L_0x0091:
        goto L_0x009d;
    L_0x0092:
        r4 = r4 + 1;
        r7 = r4;
        goto L_0x007d;
    L_0x0096:
        r7 = r13.nextInt(r3);
        r7 = r7 + r8;
        r7 = r12[r7];
    L_0x009d:
        r5 = java.lang.Character.charCount(r7);
        if (r4 != 0) goto L_0x00aa;
    L_0x00a3:
        r6 = 1;
        if (r5 <= r6) goto L_0x00aa;
    L_0x00a6:
        r4 = r4 + 1;
        r7 = r4;
        goto L_0x007d;
        if (r10 == 0) goto L_0x00b5;
    L_0x00ad:
        r6 = java.lang.Character.isLetter(r7);
        if (r6 != 0) goto L_0x00b4;
    L_0x00b3:
        goto L_0x00b5;
    L_0x00b4:
        goto L_0x00c2;
    L_0x00b5:
        if (r11 == 0) goto L_0x00bd;
    L_0x00b7:
        r6 = java.lang.Character.isDigit(r7);
        if (r6 != 0) goto L_0x00b4;
    L_0x00bd:
        if (r10 != 0) goto L_0x00ce;
    L_0x00bf:
        if (r11 != 0) goto L_0x00ce;
    L_0x00c1:
        goto L_0x00b4;
    L_0x00c2:
        r2.appendCodePoint(r7);
        r6 = 2;
        if (r5 != r6) goto L_0x00cc;
    L_0x00c8:
        r4 = r4 + -1;
        r7 = r4;
        goto L_0x00d2;
    L_0x00cc:
        r7 = r4;
        goto L_0x00d2;
        r4 = r4 + 1;
        r7 = r4;
    L_0x00d2:
        goto L_0x007d;
    L_0x00d3:
        r7 = r2.toString();
        return r7;
    L_0x00d8:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Parameter end (";
        r1.append(r2);
        r1.append(r9);
        r2 = ") must be greater than start (";
        r1.append(r2);
        r1.append(r8);
        r2 = ")";
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x00fc:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Requested random string length ";
        r1.append(r2);
        r1.append(r7);
        r2 = " is less than 0.";
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.RandomStringUtils.random(int, int, int, boolean, boolean, char[], java.util.Random):java.lang.String");
    }

    public static String random(int count) {
        return random(count, false, false);
    }

    public static String randomAscii(int count) {
        return random(count, 32, 127, false, false);
    }

    public static String randomAscii(int minLengthInclusive, int maxLengthExclusive) {
        return randomAscii(RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    public static String randomAlphabetic(int count) {
        return random(count, true, false);
    }

    public static String randomAlphabetic(int minLengthInclusive, int maxLengthExclusive) {
        return randomAlphabetic(RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    public static String randomAlphanumeric(int count) {
        return random(count, true, true);
    }

    public static String randomAlphanumeric(int minLengthInclusive, int maxLengthExclusive) {
        return randomAlphanumeric(RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    public static String randomGraph(int count) {
        return random(count, 33, 126, false, false);
    }

    public static String randomGraph(int minLengthInclusive, int maxLengthExclusive) {
        return randomGraph(RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    public static String randomNumeric(int count) {
        return random(count, false, true);
    }

    public static String randomNumeric(int minLengthInclusive, int maxLengthExclusive) {
        return randomNumeric(RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    public static String randomPrint(int count) {
        return random(count, 32, 126, false, false);
    }

    public static String randomPrint(int minLengthInclusive, int maxLengthExclusive) {
        return randomPrint(RandomUtils.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    public static String random(int count, boolean letters, boolean numbers) {
        return random(count, 0, 0, letters, numbers);
    }

    public static String random(int count, int start, int end, boolean letters, boolean numbers) {
        return random(count, start, end, letters, numbers, null, RANDOM);
    }

    public static String random(int count, int start, int end, boolean letters, boolean numbers, char... chars) {
        return random(count, start, end, letters, numbers, chars, RANDOM);
    }

    public static String random(int count, String chars) {
        if (chars != null) {
            return random(count, chars.toCharArray());
        }
        return random(count, 0, 0, false, false, null, RANDOM);
    }

    public static String random(int count, char... chars) {
        if (chars == null) {
            return random(count, 0, 0, false, false, null, RANDOM);
        }
        return random(count, 0, chars.length, false, false, chars, RANDOM);
    }
}
