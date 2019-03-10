package org.apache.commons.text.translate;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Map;

public class LookupTranslator extends CharSequenceTranslator {
    private final int longest;
    private final Map<String, String> lookupMap;
    private final HashSet<Character> prefixSet;
    private final int shortest;

    public LookupTranslator(java.util.Map<java.lang.CharSequence, java.lang.CharSequence> r8) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:0x007a in {7, 8, 10, 11, 12, 14, 16} preds:[]
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
        r7 = this;
        r7.<init>();
        if (r8 == 0) goto L_0x0072;
    L_0x0005:
        r0 = new java.util.HashMap;
        r0.<init>();
        r7.lookupMap = r0;
        r0 = new java.util.HashSet;
        r0.<init>();
        r7.prefixSet = r0;
        r0 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r1 = 0;
        r2 = r8.entrySet();
        r2 = r2.iterator();
    L_0x001f:
        r3 = r2.hasNext();
        if (r3 == 0) goto L_0x006d;
    L_0x0025:
        r3 = r2.next();
        r3 = (java.util.Map.Entry) r3;
        r4 = r7.lookupMap;
        r5 = r3.getKey();
        r5 = (java.lang.CharSequence) r5;
        r5 = r5.toString();
        r6 = r3.getValue();
        r6 = (java.lang.CharSequence) r6;
        r6 = r6.toString();
        r4.put(r5, r6);
        r4 = r7.prefixSet;
        r5 = r3.getKey();
        r5 = (java.lang.CharSequence) r5;
        r6 = 0;
        r5 = r5.charAt(r6);
        r5 = java.lang.Character.valueOf(r5);
        r4.add(r5);
        r4 = r3.getKey();
        r4 = (java.lang.CharSequence) r4;
        r4 = r4.length();
        if (r4 >= r0) goto L_0x0066;
    L_0x0064:
        r0 = r4;
        goto L_0x0067;
    L_0x0067:
        if (r4 <= r1) goto L_0x006b;
    L_0x0069:
        r1 = r4;
        goto L_0x006c;
    L_0x006c:
        goto L_0x001f;
    L_0x006d:
        r7.shortest = r0;
        r7.longest = r1;
        return;
    L_0x0072:
        r0 = new java.security.InvalidParameterException;
        r1 = "lookupMap cannot be null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.text.translate.LookupTranslator.<init>(java.util.Map):void");
    }

    public int translate(CharSequence input, int index, Writer out) throws IOException {
        if (this.prefixSet.contains(Character.valueOf(input.charAt(index)))) {
            int max = this.longest;
            if (this.longest + index > input.length()) {
                max = input.length() - index;
            }
            for (int i = max; i >= this.shortest; i--) {
                String result = (String) this.lookupMap.get(input.subSequence(index, index + i).toString());
                if (result != null) {
                    out.write(result);
                    return i;
                }
            }
        }
        return 0;
    }
}
