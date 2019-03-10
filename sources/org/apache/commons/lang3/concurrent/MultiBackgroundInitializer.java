package org.apache.commons.lang3.concurrent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import org.apache.commons.lang3.Validate;

public class MultiBackgroundInitializer extends BackgroundInitializer<MultiBackgroundInitializerResults> {
    private final Map<String, BackgroundInitializer<?>> childInitializers = new HashMap();

    public static class MultiBackgroundInitializerResults {
        private final Map<String, ConcurrentException> exceptions;
        private final Map<String, BackgroundInitializer<?>> initializers;
        private final Map<String, Object> resultObjects;

        private MultiBackgroundInitializerResults(Map<String, BackgroundInitializer<?>> inits, Map<String, Object> results, Map<String, ConcurrentException> excepts) {
            this.initializers = inits;
            this.resultObjects = results;
            this.exceptions = excepts;
        }

        public BackgroundInitializer<?> getInitializer(String name) {
            return checkName(name);
        }

        public Object getResultObject(String name) {
            checkName(name);
            return this.resultObjects.get(name);
        }

        public boolean isException(String name) {
            checkName(name);
            return this.exceptions.containsKey(name);
        }

        public ConcurrentException getException(String name) {
            checkName(name);
            return (ConcurrentException) this.exceptions.get(name);
        }

        public Set<String> initializerNames() {
            return Collections.unmodifiableSet(this.initializers.keySet());
        }

        public boolean isSuccessful() {
            return this.exceptions.isEmpty();
        }

        private BackgroundInitializer<?> checkName(String name) {
            BackgroundInitializer<?> init = (BackgroundInitializer) this.initializers.get(name);
            if (init != null) {
                return init;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("No child initializer with name ");
            stringBuilder.append(name);
            throw new NoSuchElementException(stringBuilder.toString());
        }
    }

    protected org.apache.commons.lang3.concurrent.MultiBackgroundInitializer.MultiBackgroundInitializerResults initialize() throws java.lang.Exception {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:28:0x0075 in {9, 10, 11, 18, 20, 21, 23, 27} preds:[]
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
        r8 = this;
        monitor-enter(r8);
        r0 = new java.util.HashMap;	 Catch:{ all -> 0x0072 }
        r1 = r8.childInitializers;	 Catch:{ all -> 0x0072 }
        r0.<init>(r1);	 Catch:{ all -> 0x0072 }
        monitor-exit(r8);	 Catch:{ all -> 0x0072 }
        r1 = r8.getActiveExecutor();
        r2 = r0.values();
        r2 = r2.iterator();
    L_0x0015:
        r3 = r2.hasNext();
        if (r3 == 0) goto L_0x0030;
    L_0x001b:
        r3 = r2.next();
        r3 = (org.apache.commons.lang3.concurrent.BackgroundInitializer) r3;
        r4 = r3.getExternalExecutor();
        if (r4 != 0) goto L_0x002b;
    L_0x0027:
        r3.setExternalExecutor(r1);
        goto L_0x002c;
    L_0x002c:
        r3.start();
        goto L_0x0015;
        r2 = new java.util.HashMap;
        r2.<init>();
        r3 = new java.util.HashMap;
        r3.<init>();
        r4 = r0.entrySet();
        r4 = r4.iterator();
    L_0x0043:
        r5 = r4.hasNext();
        if (r5 == 0) goto L_0x006a;
    L_0x0049:
        r5 = r4.next();
        r5 = (java.util.Map.Entry) r5;
        r6 = r5.getKey();	 Catch:{ ConcurrentException -> 0x0061 }
        r7 = r5.getValue();	 Catch:{ ConcurrentException -> 0x0061 }
        r7 = (org.apache.commons.lang3.concurrent.BackgroundInitializer) r7;	 Catch:{ ConcurrentException -> 0x0061 }
        r7 = r7.get();	 Catch:{ ConcurrentException -> 0x0061 }
        r2.put(r6, r7);	 Catch:{ ConcurrentException -> 0x0061 }
        goto L_0x0069;
    L_0x0061:
        r6 = move-exception;
        r7 = r5.getKey();
        r3.put(r7, r6);
    L_0x0069:
        goto L_0x0043;
        r4 = new org.apache.commons.lang3.concurrent.MultiBackgroundInitializer$MultiBackgroundInitializerResults;
        r5 = 0;
        r4.<init>(r0, r2, r3);
        return r4;
    L_0x0072:
        r0 = move-exception;
        monitor-exit(r8);	 Catch:{ all -> 0x0072 }
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.concurrent.MultiBackgroundInitializer.initialize():org.apache.commons.lang3.concurrent.MultiBackgroundInitializer$MultiBackgroundInitializerResults");
    }

    public MultiBackgroundInitializer(ExecutorService exec) {
        super(exec);
    }

    public void addInitializer(String name, BackgroundInitializer<?> init) {
        boolean z = true;
        Validate.isTrue(name != null, "Name of child initializer must not be null!", new Object[0]);
        if (init == null) {
            z = false;
        }
        Validate.isTrue(z, "Child initializer must not be null!", new Object[0]);
        synchronized (this) {
            if (isStarted()) {
                throw new IllegalStateException("addInitializer() must not be called after start()!");
            }
            this.childInitializers.put(name, init);
        }
    }

    protected int getTaskCount() {
        int result = 1;
        for (BackgroundInitializer<?> bi : this.childInitializers.values()) {
            result += bi.getTaskCount();
        }
        return result;
    }
}
