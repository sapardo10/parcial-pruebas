package org.apache.commons.text.similarity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CosineSimilarity {
    public java.lang.Double cosineSimilarity(java.util.Map<java.lang.CharSequence, java.lang.Integer> r14, java.util.Map<java.lang.CharSequence, java.lang.Integer> r15) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:21:0x0079 in {5, 9, 14, 15, 16, 18, 20} preds:[]
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
        r13 = this;
        if (r14 == 0) goto L_0x0070;
    L_0x0002:
        if (r15 == 0) goto L_0x0070;
    L_0x0004:
        r0 = r13.getIntersection(r14, r15);
        r1 = r13.dot(r14, r15, r0);
        r3 = 0;
        r5 = r14.values();
        r5 = r5.iterator();
    L_0x0016:
        r6 = r5.hasNext();
        r7 = 4611686018427387904; // 0x4000000000000000 float:0.0 double:2.0;
        if (r6 == 0) goto L_0x002f;
    L_0x001e:
        r6 = r5.next();
        r6 = (java.lang.Integer) r6;
        r9 = r6.intValue();
        r9 = (double) r9;
        r7 = java.lang.Math.pow(r9, r7);
        r3 = r3 + r7;
        goto L_0x0016;
    L_0x002f:
        r5 = 0;
        r9 = r15.values();
        r9 = r9.iterator();
    L_0x0039:
        r10 = r9.hasNext();
        if (r10 == 0) goto L_0x0050;
    L_0x003f:
        r10 = r9.next();
        r10 = (java.lang.Integer) r10;
        r11 = r10.intValue();
        r11 = (double) r11;
        r11 = java.lang.Math.pow(r11, r7);
        r5 = r5 + r11;
        goto L_0x0039;
    L_0x0050:
        r7 = 0;
        r9 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1));
        if (r9 <= 0) goto L_0x0068;
    L_0x0056:
        r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r9 > 0) goto L_0x005b;
    L_0x005a:
        goto L_0x0068;
    L_0x005b:
        r7 = java.lang.Math.sqrt(r3);
        r9 = java.lang.Math.sqrt(r5);
        r7 = r7 * r9;
        r7 = r1 / r7;
        goto L_0x006b;
        r7 = 0;
    L_0x006b:
        r9 = java.lang.Double.valueOf(r7);
        return r9;
        r0 = new java.lang.IllegalArgumentException;
        r1 = "Vectors must not be null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.text.similarity.CosineSimilarity.cosineSimilarity(java.util.Map, java.util.Map):java.lang.Double");
    }

    private Set<CharSequence> getIntersection(Map<CharSequence, Integer> leftVector, Map<CharSequence, Integer> rightVector) {
        Set<CharSequence> intersection = new HashSet(leftVector.keySet());
        intersection.retainAll(rightVector.keySet());
        return intersection;
    }

    private double dot(Map<CharSequence, Integer> leftVector, Map<CharSequence, Integer> rightVector, Set<CharSequence> intersection) {
        long dotProduct = 0;
        for (CharSequence key : intersection) {
            dotProduct += (long) (((Integer) leftVector.get(key)).intValue() * ((Integer) rightVector.get(key)).intValue());
        }
        return (double) dotProduct;
    }
}
