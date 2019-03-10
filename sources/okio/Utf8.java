package okio;

public final class Utf8 {
    public static long size(java.lang.String r9, int r10, int r11) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:42:0x00c2 in {9, 12, 17, 20, 21, 27, 28, 29, 30, 31, 33, 35, 37, 39, 41} preds:[]
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
        if (r9 == 0) goto L_0x00ba;
    L_0x0002:
        if (r10 < 0) goto L_0x00a3;
    L_0x0004:
        if (r11 < r10) goto L_0x0084;
    L_0x0006:
        r0 = r9.length();
        if (r11 > r0) goto L_0x0061;
    L_0x000c:
        r0 = 0;
        r2 = r10;
    L_0x000f:
        if (r2 >= r11) goto L_0x005f;
    L_0x0011:
        r3 = r9.charAt(r2);
        r4 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        r5 = 1;
        if (r3 >= r4) goto L_0x001f;
    L_0x001b:
        r0 = r0 + r5;
        r2 = r2 + 1;
        goto L_0x005e;
    L_0x001f:
        r4 = 2048; // 0x800 float:2.87E-42 double:1.0118E-320;
        if (r3 >= r4) goto L_0x0029;
    L_0x0023:
        r4 = 2;
        r0 = r0 + r4;
        r2 = r2 + 1;
        goto L_0x005e;
    L_0x0029:
        r4 = 55296; // 0xd800 float:7.7486E-41 double:2.732E-319;
        if (r3 < r4) goto L_0x0058;
    L_0x002e:
        r4 = 57343; // 0xdfff float:8.0355E-41 double:2.8331E-319;
        if (r3 <= r4) goto L_0x0034;
    L_0x0033:
        goto L_0x0058;
    L_0x0034:
        r7 = r2 + 1;
        if (r7 >= r11) goto L_0x003f;
    L_0x0038:
        r7 = r2 + 1;
        r7 = r9.charAt(r7);
        goto L_0x0040;
    L_0x003f:
        r7 = 0;
    L_0x0040:
        r8 = 56319; // 0xdbff float:7.892E-41 double:2.78253E-319;
        if (r3 > r8) goto L_0x0053;
    L_0x0045:
        r8 = 56320; // 0xdc00 float:7.8921E-41 double:2.7826E-319;
        if (r7 < r8) goto L_0x0053;
    L_0x004a:
        if (r7 <= r4) goto L_0x004d;
    L_0x004c:
        goto L_0x0053;
    L_0x004d:
        r4 = 4;
        r0 = r0 + r4;
        r2 = r2 + 2;
        goto L_0x005e;
        r0 = r0 + r5;
        r2 = r2 + 1;
        goto L_0x005e;
        r4 = 3;
        r0 = r0 + r4;
        r2 = r2 + 1;
    L_0x005e:
        goto L_0x000f;
        return r0;
    L_0x0061:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "endIndex > string.length: ";
        r1.append(r2);
        r1.append(r11);
        r2 = " > ";
        r1.append(r2);
        r2 = r9.length();
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x0084:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "endIndex < beginIndex: ";
        r1.append(r2);
        r1.append(r11);
        r2 = " < ";
        r1.append(r2);
        r1.append(r10);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x00a3:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "beginIndex < 0: ";
        r1.append(r2);
        r1.append(r10);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x00ba:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "string == null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Utf8.size(java.lang.String, int, int):long");
    }

    private Utf8() {
    }

    public static long size(String string) {
        return size(string, 0, string.length());
    }
}
