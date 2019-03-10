package kotlin.jvm.internal;

import java.util.ArrayList;

public class SpreadBuilder {
    private final ArrayList<Object> list;

    public void addSpread(java.lang.Object r7) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:32:0x0089 in {1, 8, 9, 10, 11, 14, 20, 21, 27, 28, 29, 31} preds:[]
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
        r6 = this;
        if (r7 != 0) goto L_0x0003;
    L_0x0002:
        return;
    L_0x0003:
        r0 = r7 instanceof java.lang.Object[];
        if (r0 == 0) goto L_0x002a;
    L_0x0007:
        r0 = r7;
        r0 = (java.lang.Object[]) r0;
        r1 = r0.length;
        if (r1 <= 0) goto L_0x0028;
    L_0x000d:
        r1 = r6.list;
        r2 = r1.size();
        r3 = r0.length;
        r2 = r2 + r3;
        r1.ensureCapacity(r2);
        r1 = r0;
        r2 = r1.length;
        r3 = 0;
    L_0x001b:
        if (r3 >= r2) goto L_0x0027;
    L_0x001d:
        r4 = r1[r3];
        r5 = r6.list;
        r5.add(r4);
        r3 = r3 + 1;
        goto L_0x001b;
    L_0x0027:
        goto L_0x0029;
    L_0x0029:
        goto L_0x006d;
    L_0x002a:
        r0 = r7 instanceof java.util.Collection;
        if (r0 == 0) goto L_0x0037;
    L_0x002e:
        r0 = r6.list;
        r1 = r7;
        r1 = (java.util.Collection) r1;
        r0.addAll(r1);
        goto L_0x006d;
    L_0x0037:
        r0 = r7 instanceof java.lang.Iterable;
        if (r0 == 0) goto L_0x0054;
    L_0x003b:
        r0 = r7;
        r0 = (java.lang.Iterable) r0;
        r0 = r0.iterator();
    L_0x0042:
        r1 = r0.hasNext();
        if (r1 == 0) goto L_0x0052;
    L_0x0048:
        r1 = r0.next();
        r2 = r6.list;
        r2.add(r1);
        goto L_0x0042;
        goto L_0x006d;
    L_0x0054:
        r0 = r7 instanceof java.util.Iterator;
        if (r0 == 0) goto L_0x006e;
    L_0x0058:
        r0 = r7;
        r0 = (java.util.Iterator) r0;
    L_0x005b:
        r1 = r0.hasNext();
        if (r1 == 0) goto L_0x006b;
    L_0x0061:
        r1 = r6.list;
        r2 = r0.next();
        r1.add(r2);
        goto L_0x005b;
    L_0x006d:
        return;
    L_0x006e:
        r0 = new java.lang.UnsupportedOperationException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Don't know how to spread ";
        r1.append(r2);
        r2 = r7.getClass();
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.jvm.internal.SpreadBuilder.addSpread(java.lang.Object):void");
    }

    public SpreadBuilder(int size) {
        this.list = new ArrayList(size);
    }

    public int size() {
        return this.list.size();
    }

    public void add(Object element) {
        this.list.add(element);
    }

    public Object[] toArray(Object[] a) {
        return this.list.toArray(a);
    }
}
