package org.objenesis.instantiator.perc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.annotations.Instantiator;
import org.objenesis.instantiator.annotations.Typology;

@Instantiator(Typology.SERIALIZATION)
public class PercSerializationInstantiator<T> implements ObjectInstantiator<T> {
    private final Method newInstanceMethod;
    private Object[] typeArgs;

    public PercSerializationInstantiator(java.lang.Class<T> r14) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:20:0x0093 in {3, 7, 10, 13, 16, 19} preds:[]
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
        r13.<init>();
        r0 = r14;
    L_0x0004:
        r1 = java.io.Serializable.class;
        r1 = r1.isAssignableFrom(r0);
        if (r1 == 0) goto L_0x0011;
    L_0x000c:
        r0 = r0.getSuperclass();
        goto L_0x0004;
    L_0x0011:
        r1 = "COM.newmonics.PercClassLoader.Method";	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r1 = java.lang.Class.forName(r1);	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r2 = java.io.ObjectInputStream.class;	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r3 = "noArgConstruct";	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r4 = 3;	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r5 = new java.lang.Class[r4];	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r6 = java.lang.Class.class;	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r7 = 0;	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r5[r7] = r6;	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r6 = java.lang.Object.class;	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r8 = 1;	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r5[r8] = r6;	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r6 = 2;	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r5[r6] = r1;	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r2 = r2.getDeclaredMethod(r3, r5);	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r13.newInstanceMethod = r2;	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r2 = r13.newInstanceMethod;	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r2.setAccessible(r8);	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r2 = "COM.newmonics.PercClassLoader.PercClass";	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r2 = java.lang.Class.forName(r2);	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r3 = "getPercClass";	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r5 = new java.lang.Class[r8];	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r9 = java.lang.Class.class;	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r5[r7] = r9;	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r3 = r2.getDeclaredMethod(r3, r5);	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r5 = 0;	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r9 = new java.lang.Object[r8];	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r9[r7] = r0;	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r5 = r3.invoke(r5, r9);	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r9 = r5.getClass();	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r10 = "findMethod";	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r11 = new java.lang.Class[r8];	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r12 = java.lang.String.class;	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r11[r7] = r12;	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r9 = r9.getDeclaredMethod(r10, r11);	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r10 = new java.lang.Object[r8];	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r11 = "<init>()V";	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r10[r7] = r11;	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r10 = r9.invoke(r5, r10);	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r4 = new java.lang.Object[r4];	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r4[r7] = r0;	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r4[r8] = r14;	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r4[r6] = r10;	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        r13.typeArgs = r4;	 Catch:{ ClassNotFoundException -> 0x008c, NoSuchMethodException -> 0x0085, InvocationTargetException -> 0x007e, IllegalAccessException -> 0x0077 }
        return;
    L_0x0077:
        r1 = move-exception;
        r2 = new org.objenesis.ObjenesisException;
        r2.<init>(r1);
        throw r2;
    L_0x007e:
        r1 = move-exception;
        r2 = new org.objenesis.ObjenesisException;
        r2.<init>(r1);
        throw r2;
    L_0x0085:
        r1 = move-exception;
        r2 = new org.objenesis.ObjenesisException;
        r2.<init>(r1);
        throw r2;
    L_0x008c:
        r1 = move-exception;
        r2 = new org.objenesis.ObjenesisException;
        r2.<init>(r1);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.objenesis.instantiator.perc.PercSerializationInstantiator.<init>(java.lang.Class):void");
    }

    public T newInstance() {
        try {
            return this.newInstanceMethod.invoke(null, this.typeArgs);
        } catch (IllegalAccessException e) {
            throw new ObjenesisException(e);
        } catch (InvocationTargetException e2) {
            throw new ObjenesisException(e2);
        }
    }
}
