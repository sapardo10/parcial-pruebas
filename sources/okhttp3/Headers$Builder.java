package okhttp3;

import java.util.ArrayList;
import java.util.List;

public final class Headers$Builder {
    final List<String> namesAndValues = new ArrayList(20);

    private void checkNameAndValue(java.lang.String r11, java.lang.String r12) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:32:0x00a8 in {9, 11, 19, 21, 23, 25, 27, 29, 31} preds:[]
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
        r10 = this;
        if (r11 == 0) goto L_0x00a0;
    L_0x0002:
        r0 = r11.isEmpty();
        if (r0 != 0) goto L_0x0098;
    L_0x0008:
        r0 = 0;
        r1 = r11.length();
    L_0x000d:
        r2 = 127; // 0x7f float:1.78E-43 double:6.27E-322;
        r3 = 2;
        r4 = 0;
        r5 = 3;
        r6 = 1;
        if (r0 >= r1) goto L_0x003f;
    L_0x0015:
        r7 = r11.charAt(r0);
        r8 = 32;
        if (r7 <= r8) goto L_0x0022;
    L_0x001d:
        if (r7 >= r2) goto L_0x0022;
    L_0x001f:
        r0 = r0 + 1;
        goto L_0x000d;
        r2 = new java.lang.IllegalArgumentException;
        r5 = new java.lang.Object[r5];
        r8 = java.lang.Integer.valueOf(r7);
        r5[r4] = r8;
        r4 = java.lang.Integer.valueOf(r0);
        r5[r6] = r4;
        r5[r3] = r11;
        r3 = "Unexpected char %#04x at %d in header name: %s";
        r3 = okhttp3.internal.Util.format(r3, r5);
        r2.<init>(r3);
        throw r2;
        if (r12 == 0) goto L_0x007c;
    L_0x0042:
        r0 = 0;
        r1 = r12.length();
    L_0x0047:
        if (r0 >= r1) goto L_0x007a;
    L_0x0049:
        r7 = r12.charAt(r0);
        r8 = 31;
        if (r7 > r8) goto L_0x0055;
    L_0x0051:
        r8 = 9;
        if (r7 != r8) goto L_0x005a;
    L_0x0055:
        if (r7 >= r2) goto L_0x005a;
    L_0x0057:
        r0 = r0 + 1;
        goto L_0x0047;
        r2 = new java.lang.IllegalArgumentException;
        r8 = 4;
        r8 = new java.lang.Object[r8];
        r9 = java.lang.Integer.valueOf(r7);
        r8[r4] = r9;
        r4 = java.lang.Integer.valueOf(r0);
        r8[r6] = r4;
        r8[r3] = r11;
        r8[r5] = r12;
        r3 = "Unexpected char %#04x at %d in %s value: %s";
        r3 = okhttp3.internal.Util.format(r3, r8);
        r2.<init>(r3);
        throw r2;
        return;
    L_0x007c:
        r0 = new java.lang.NullPointerException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "value for name ";
        r1.append(r2);
        r1.append(r11);
        r2 = " == null";
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x0098:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "name is empty";
        r0.<init>(r1);
        throw r0;
    L_0x00a0:
        r0 = new java.lang.NullPointerException;
        r1 = "name == null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.Headers$Builder.checkNameAndValue(java.lang.String, java.lang.String):void");
    }

    Headers$Builder addLenient(String line) {
        int index = line.indexOf(":", 1);
        if (index != -1) {
            return addLenient(line.substring(0, index), line.substring(index + 1));
        }
        if (line.startsWith(":")) {
            return addLenient("", line.substring(1));
        }
        return addLenient("", line);
    }

    public Headers$Builder add(String line) {
        int index = line.indexOf(":");
        if (index != -1) {
            return add(line.substring(0, index).trim(), line.substring(index + 1));
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unexpected header: ");
        stringBuilder.append(line);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public Headers$Builder add(String name, String value) {
        checkNameAndValue(name, value);
        return addLenient(name, value);
    }

    Headers$Builder addLenient(String name, String value) {
        this.namesAndValues.add(name);
        this.namesAndValues.add(value.trim());
        return this;
    }

    public Headers$Builder removeAll(String name) {
        int i = 0;
        while (i < this.namesAndValues.size()) {
            if (name.equalsIgnoreCase((String) this.namesAndValues.get(i))) {
                this.namesAndValues.remove(i);
                this.namesAndValues.remove(i);
                i -= 2;
            }
            i += 2;
        }
        return this;
    }

    public Headers$Builder set(String name, String value) {
        checkNameAndValue(name, value);
        removeAll(name);
        addLenient(name, value);
        return this;
    }

    public String get(String name) {
        for (int i = this.namesAndValues.size() - 2; i >= 0; i -= 2) {
            if (name.equalsIgnoreCase((String) this.namesAndValues.get(i))) {
                return (String) this.namesAndValues.get(i + 1);
            }
        }
        return null;
    }

    public Headers build() {
        return new Headers(this);
    }
}
