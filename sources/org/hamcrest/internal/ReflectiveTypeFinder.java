package org.hamcrest.internal;

import java.lang.reflect.Method;

public class ReflectiveTypeFinder {
    private final int expectedNumberOfParameters;
    private final String methodName;
    private final int typedParameter;

    public java.lang.Class<?> findExpectedType(java.lang.Class<?> r7) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:13:0x0041 in {8, 9, 10, 12} preds:[]
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
        r0 = r7;
    L_0x0001:
        r1 = java.lang.Object.class;
        if (r0 == r1) goto L_0x0023;
    L_0x0005:
        r1 = r0.getDeclaredMethods();
        r2 = r1.length;
        r3 = 0;
    L_0x000b:
        if (r3 >= r2) goto L_0x001e;
    L_0x000d:
        r4 = r1[r3];
        r5 = r6.canObtainExpectedTypeFrom(r4);
        if (r5 == 0) goto L_0x001a;
    L_0x0015:
        r5 = r6.expectedTypeFrom(r4);
        return r5;
        r3 = r3 + 1;
        goto L_0x000b;
    L_0x001e:
        r0 = r0.getSuperclass();
        goto L_0x0001;
    L_0x0023:
        r0 = new java.lang.Error;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Cannot determine correct type for ";
        r1.append(r2);
        r2 = r6.methodName;
        r1.append(r2);
        r2 = "() method.";
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.hamcrest.internal.ReflectiveTypeFinder.findExpectedType(java.lang.Class):java.lang.Class<?>");
    }

    public ReflectiveTypeFinder(String methodName, int expectedNumberOfParameters, int typedParameter) {
        this.methodName = methodName;
        this.expectedNumberOfParameters = expectedNumberOfParameters;
        this.typedParameter = typedParameter;
    }

    protected boolean canObtainExpectedTypeFrom(Method method) {
        return method.getName().equals(this.methodName) && method.getParameterTypes().length == this.expectedNumberOfParameters && !method.isSynthetic();
    }

    protected Class<?> expectedTypeFrom(Method method) {
        return method.getParameterTypes()[this.typedParameter];
    }
}
