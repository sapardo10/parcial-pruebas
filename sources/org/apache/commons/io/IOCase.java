package org.apache.commons.io;

import java.io.Serializable;

public enum IOCase implements Serializable {
    SENSITIVE("Sensitive", true),
    INSENSITIVE("Insensitive", false),
    SYSTEM("System", FilenameUtils.isSystemWindows() ^ true);
    
    private static final long serialVersionUID = -6343169151696340687L;
    private final String name;
    private final transient boolean sensitive;

    public static org.apache.commons.io.IOCase forName(java.lang.String r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:8:0x0030 in {4, 5, 7} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r0 = values();
        r1 = r0.length;
        r2 = 0;
    L_0x0006:
        if (r2 >= r1) goto L_0x0019;
    L_0x0008:
        r3 = r0[r2];
        r4 = r3.getName();
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x0015;
    L_0x0014:
        return r3;
        r2 = r2 + 1;
        goto L_0x0006;
    L_0x0019:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Invalid IOCase name: ";
        r1.append(r2);
        r1.append(r5);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.IOCase.forName(java.lang.String):org.apache.commons.io.IOCase");
    }

    private IOCase(String name, boolean sensitive) {
        this.name = name;
        this.sensitive = sensitive;
    }

    private Object readResolve() {
        return forName(this.name);
    }

    public String getName() {
        return this.name;
    }

    public boolean isCaseSensitive() {
        return this.sensitive;
    }

    public int checkCompareTo(String str1, String str2) {
        if (str1 != null && str2 != null) {
            return this.sensitive ? str1.compareTo(str2) : str1.compareToIgnoreCase(str2);
        } else {
            throw new NullPointerException("The strings must not be null");
        }
    }

    public boolean checkEquals(String str1, String str2) {
        if (str1 != null && str2 != null) {
            return this.sensitive ? str1.equals(str2) : str1.equalsIgnoreCase(str2);
        } else {
            throw new NullPointerException("The strings must not be null");
        }
    }

    public boolean checkStartsWith(String str, String start) {
        return str.regionMatches(this.sensitive ^ 1, 0, start, 0, start.length());
    }

    public boolean checkEndsWith(String str, String end) {
        int endLen = end.length();
        return str.regionMatches(this.sensitive ^ 1, str.length() - endLen, end, 0, endLen);
    }

    public int checkIndexOf(String str, int strStartIndex, String search) {
        int endIndex = str.length() - search.length();
        if (endIndex >= strStartIndex) {
            for (int i = strStartIndex; i <= endIndex; i++) {
                if (checkRegionMatches(str, i, search)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public boolean checkRegionMatches(String str, int strStartIndex, String search) {
        return str.regionMatches(this.sensitive ^ 1, strStartIndex, search, 0, search.length());
    }

    public String toString() {
        return this.name;
    }
}
