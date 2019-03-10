package de.greenrobot.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class SubscriberMethodFinder {
    private static final int BRIDGE = 64;
    private static final int MODIFIERS_IGNORE = 5192;
    private static final String ON_EVENT_METHOD_NAME = "onEvent";
    private static final int SYNTHETIC = 4096;
    private static final Map<String, List<SubscriberMethod>> methodCache = new HashMap();
    private final Map<Class<?>, Class<?>> skipMethodVerificationForClasses = new ConcurrentHashMap();

    java.util.List<de.greenrobot.event.SubscriberMethod> findSubscriberMethods(java.lang.Class<?> r21) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.fixSplitterBlock(BlockFinish.java:63)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:34)
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
        r20 = this;
        r1 = r20;
        r2 = r21.getName();
        r3 = methodCache;
        monitor-enter(r3);
        r4 = 0;
        r0 = methodCache;	 Catch:{ all -> 0x017e }
        r0 = r0.get(r2);	 Catch:{ all -> 0x017e }
        r0 = (java.util.List) r0;	 Catch:{ all -> 0x017e }
        r4 = r0;	 Catch:{ all -> 0x017e }
        monitor-exit(r3);	 Catch:{ all -> 0x017e }
        if (r4 == 0) goto L_0x0017;
    L_0x0016:
        return r4;
    L_0x0017:
        r0 = new java.util.ArrayList;
        r0.<init>();
        r3 = r0;
        r0 = r21;
        r4 = new java.util.HashSet;
        r4.<init>();
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = r0;
    L_0x002a:
        if (r6 == 0) goto L_0x0147;
    L_0x002c:
        r0 = r6.getName();
        r7 = "java.";
        r7 = r0.startsWith(r7);
        if (r7 != 0) goto L_0x0144;
    L_0x0038:
        r7 = "javax.";
        r7 = r0.startsWith(r7);
        if (r7 != 0) goto L_0x0144;
    L_0x0040:
        r7 = "android.";
        r7 = r0.startsWith(r7);
        if (r7 == 0) goto L_0x004a;
    L_0x0048:
        goto L_0x0148;
    L_0x004a:
        r7 = r6.getDeclaredMethods();
        r8 = r7;
        r9 = r8.length;
        r10 = 0;
    L_0x0051:
        if (r10 >= r9) goto L_0x013a;
    L_0x0053:
        r11 = r8[r10];
        r12 = r11.getName();
        r13 = "onEvent";
        r13 = r12.startsWith(r13);
        if (r13 == 0) goto L_0x012e;
    L_0x0061:
        r13 = r11.getModifiers();
        r14 = r13 & 1;
        if (r14 == 0) goto L_0x0102;
    L_0x0069:
        r14 = r13 & 5192;
        if (r14 != 0) goto L_0x0102;
    L_0x006d:
        r14 = r11.getParameterTypes();
        r15 = r14.length;
        r16 = r0;
        r0 = 1;
        if (r15 != r0) goto L_0x00ff;
    L_0x0077:
        r0 = "onEvent";
        r0 = r0.length();
        r0 = r12.substring(r0);
        r15 = r0.length();
        if (r15 != 0) goto L_0x008a;
    L_0x0087:
        r15 = de.greenrobot.event.ThreadMode.PostThread;
        goto L_0x00aa;
    L_0x008a:
        r15 = "MainThread";
        r15 = r0.equals(r15);
        if (r15 == 0) goto L_0x0095;
    L_0x0092:
        r15 = de.greenrobot.event.ThreadMode.MainThread;
        goto L_0x00aa;
    L_0x0095:
        r15 = "BackgroundThread";
        r15 = r0.equals(r15);
        if (r15 == 0) goto L_0x00a0;
    L_0x009d:
        r15 = de.greenrobot.event.ThreadMode.BackgroundThread;
        goto L_0x00aa;
    L_0x00a0:
        r15 = "Async";
        r15 = r0.equals(r15);
        if (r15 == 0) goto L_0x00db;
    L_0x00a8:
        r15 = de.greenrobot.event.ThreadMode.Async;
    L_0x00aa:
        r17 = r0;
        r0 = 0;
        r18 = r7;
        r7 = r14[r0];
        r5.setLength(r0);
        r5.append(r12);
        r0 = 62;
        r5.append(r0);
        r0 = r7.getName();
        r5.append(r0);
        r0 = r5.toString();
        r19 = r4.add(r0);
        if (r19 == 0) goto L_0x00d8;
    L_0x00cd:
        r19 = r0;
        r0 = new de.greenrobot.event.SubscriberMethod;
        r0.<init>(r11, r15, r7);
        r3.add(r0);
        goto L_0x012d;
    L_0x00d8:
        r19 = r0;
        goto L_0x012d;
    L_0x00db:
        r17 = r0;
        r18 = r7;
        r0 = r1.skipMethodVerificationForClasses;
        r0 = r0.containsKey(r6);
        if (r0 == 0) goto L_0x00e8;
    L_0x00e7:
        goto L_0x0132;
    L_0x00e8:
        r0 = new de.greenrobot.event.EventBusException;
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r15 = "Illegal onEvent method, check for typos: ";
        r7.append(r15);
        r7.append(r11);
        r7 = r7.toString();
        r0.<init>(r7);
        throw r0;
    L_0x00ff:
        r18 = r7;
        goto L_0x012d;
    L_0x0102:
        r16 = r0;
        r18 = r7;
        r0 = r1.skipMethodVerificationForClasses;
        r0 = r0.containsKey(r6);
        if (r0 != 0) goto L_0x012d;
    L_0x010e:
        r0 = de.greenrobot.event.EventBus.TAG;
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r14 = "Skipping method (not public, static or abstract): ";
        r7.append(r14);
        r7.append(r6);
        r14 = ".";
        r7.append(r14);
        r7.append(r12);
        r7 = r7.toString();
        android.util.Log.d(r0, r7);
        goto L_0x0132;
    L_0x012d:
        goto L_0x0132;
    L_0x012e:
        r16 = r0;
        r18 = r7;
    L_0x0132:
        r10 = r10 + 1;
        r0 = r16;
        r7 = r18;
        goto L_0x0051;
    L_0x013a:
        r16 = r0;
        r18 = r7;
        r6 = r6.getSuperclass();
        goto L_0x002a;
    L_0x0144:
        r16 = r0;
        goto L_0x0148;
    L_0x0148:
        r0 = r3.isEmpty();
        if (r0 != 0) goto L_0x015b;
    L_0x014e:
        r7 = methodCache;
        monitor-enter(r7);
        r0 = methodCache;
        r0.put(r2, r3);
        monitor-exit(r7);
        return r3;
    L_0x0158:
        r0 = move-exception;
        monitor-exit(r7);
        throw r0;
    L_0x015b:
        r0 = new de.greenrobot.event.EventBusException;
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "Subscriber ";
        r7.append(r8);
        r8 = r21;
        r7.append(r8);
        r9 = " has no public methods called ";
        r7.append(r9);
        r9 = "onEvent";
        r7.append(r9);
        r7 = r7.toString();
        r0.<init>(r7);
        throw r0;
    L_0x017e:
        r0 = move-exception;
        r8 = r21;
    L_0x0181:
        monitor-exit(r3);	 Catch:{ all -> 0x0183 }
        throw r0;
    L_0x0183:
        r0 = move-exception;
        goto L_0x0181;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.greenrobot.event.SubscriberMethodFinder.findSubscriberMethods(java.lang.Class):java.util.List<de.greenrobot.event.SubscriberMethod>");
    }

    SubscriberMethodFinder(List<Class<?>> skipMethodVerificationForClassesList) {
        if (skipMethodVerificationForClassesList != null) {
            for (Class<?> clazz : skipMethodVerificationForClassesList) {
                this.skipMethodVerificationForClasses.put(clazz, clazz);
            }
        }
    }

    static void clearCaches() {
        synchronized (methodCache) {
            methodCache.clear();
        }
    }
}
