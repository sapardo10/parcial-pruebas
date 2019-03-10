package org.awaitility.core;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.awaitility.Duration;

abstract class ConditionAwaiter implements UncaughtExceptionHandler {
    private final ConditionEvaluator conditionEvaluator;
    private final ConditionSettings conditionSettings;
    private final ExecutorService executor;
    private final AtomicReference<Throwable> uncaughtThrowable;

    private class ConditionPoller implements Callable<ConditionEvaluationResult> {
        private final Duration delayed;

        ConditionPoller(Duration delayed) {
            this.delayed = delayed;
        }

        public ConditionEvaluationResult call() throws Exception {
            try {
                return ConditionAwaiter.this.conditionEvaluator.eval(this.delayed);
            } catch (Throwable e) {
                if (ConditionAwaiter.this.conditionSettings.shouldExceptionBeIgnored(e)) {
                    return new ConditionEvaluationResult(false);
                }
                return new ConditionEvaluationResult(false, e, null);
            }
        }
    }

    public <T> void await(org.awaitility.core.ConditionEvaluationHandler<T> r21) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:198:0x0334 in {12, 14, 16, 18, 20, 21, 34, 35, 36, 37, 41, 42, 45, 46, 47, 51, 53, 55, 57, 59, 61, 63, 69, 70, 71, 73, 75, 83, 84, 85, 87, 89, 91, 97, 109, 111, 113, 115, 126, 130, 138, 139, 155, 157, 158, 159, 161, 162, 163, 167, 170, 172, 174, 176, 178, 180, 184, 186, 187, 189, 191, 194, 195, 197} preds:[]
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
        r20 = this;
        r1 = r20;
        r0 = r1.conditionSettings;
        r2 = r0.getPollDelay();
        r0 = r1.conditionSettings;
        r3 = r0.getMaxWaitTime();
        r0 = r1.conditionSettings;
        r4 = r0.getMinWaitTime();
        r5 = r3.getValue();
        r7 = java.lang.System.nanoTime();
        r9 = r2.getValueInNS();
        r7 = r7 - r9;
        r9 = 0;
        r10 = 0;
        r11 = 0;
        r0 = new org.awaitility.Duration;
        r12 = java.util.concurrent.TimeUnit.MILLISECONDS;
        r13 = 0;
        r0.<init>(r13, r12);
        r12 = r0;
        r13 = 0;
        r0 = r1.executor;	 Catch:{ TimeoutException -> 0x017c, ExecutionException -> 0x014f, Throwable -> 0x012c, all -> 0x0123 }
        r0 = r0.isShutdown();	 Catch:{ TimeoutException -> 0x011c, ExecutionException -> 0x014f, Throwable -> 0x012c, all -> 0x0123 }
        if (r0 != 0) goto L_0x0104;	 Catch:{ TimeoutException -> 0x011c, ExecutionException -> 0x014f, Throwable -> 0x012c, all -> 0x0123 }
    L_0x0037:
        r0 = r1.executor;	 Catch:{ TimeoutException -> 0x011c, ExecutionException -> 0x014f, Throwable -> 0x012c, all -> 0x0123 }
        r0 = r0.isTerminated();	 Catch:{ TimeoutException -> 0x011c, ExecutionException -> 0x014f, Throwable -> 0x012c, all -> 0x0123 }
        if (r0 != 0) goto L_0x0104;	 Catch:{ TimeoutException -> 0x011c, ExecutionException -> 0x014f, Throwable -> 0x012c, all -> 0x0123 }
    L_0x003f:
        r21.start();	 Catch:{ TimeoutException -> 0x011c, ExecutionException -> 0x014f, Throwable -> 0x012c, all -> 0x0123 }
        r0 = r2.isZero();	 Catch:{ TimeoutException -> 0x011c, ExecutionException -> 0x014f, Throwable -> 0x012c, all -> 0x0123 }
        if (r0 != 0) goto L_0x006a;
    L_0x0048:
        r16 = r2.getValueInMS();	 Catch:{ TimeoutException -> 0x0063, ExecutionException -> 0x005e, Throwable -> 0x0059, all -> 0x0050 }
        java.lang.Thread.sleep(r16);	 Catch:{ TimeoutException -> 0x0063, ExecutionException -> 0x005e, Throwable -> 0x0059, all -> 0x0050 }
        goto L_0x006b;
    L_0x0050:
        r0 = move-exception;
        r16 = r2;
        r19 = r3;
        r18 = r10;
        goto L_0x0328;
    L_0x0059:
        r0 = move-exception;
        r18 = r10;
        goto L_0x012f;
    L_0x005e:
        r0 = move-exception;
        r18 = r10;
        goto L_0x0152;
    L_0x0063:
        r0 = move-exception;
        r16 = r2;
        r18 = r10;
        goto L_0x0181;
        r0 = r2;
        r16 = r3.compareTo(r12);	 Catch:{ TimeoutException -> 0x011c, ExecutionException -> 0x014f, Throwable -> 0x012c, all -> 0x0123 }
        if (r16 <= 0) goto L_0x00dc;	 Catch:{ TimeoutException -> 0x011c, ExecutionException -> 0x014f, Throwable -> 0x012c, all -> 0x0123 }
        r9 = r9 + 1;	 Catch:{ TimeoutException -> 0x011c, ExecutionException -> 0x014f, Throwable -> 0x012c, all -> 0x0123 }
        r16 = r3.minus(r12);	 Catch:{ TimeoutException -> 0x011c, ExecutionException -> 0x014f, Throwable -> 0x012c, all -> 0x0123 }
        r15 = r1.executor;	 Catch:{ TimeoutException -> 0x011c, ExecutionException -> 0x014f, Throwable -> 0x012c, all -> 0x0123 }
        r14 = new org.awaitility.core.ConditionAwaiter$ConditionPoller;	 Catch:{ TimeoutException -> 0x011c, ExecutionException -> 0x014f, Throwable -> 0x012c, all -> 0x0123 }
        r14.<init>(r0);	 Catch:{ TimeoutException -> 0x011c, ExecutionException -> 0x014f, Throwable -> 0x012c, all -> 0x0123 }
        r14 = r15.submit(r14);	 Catch:{ TimeoutException -> 0x011c, ExecutionException -> 0x014f, Throwable -> 0x012c, all -> 0x0123 }
        r13 = r14;	 Catch:{ TimeoutException -> 0x011c, ExecutionException -> 0x014f, Throwable -> 0x012c, all -> 0x0123 }
        r14 = r16.getValue();	 Catch:{ TimeoutException -> 0x011c, ExecutionException -> 0x014f, Throwable -> 0x012c, all -> 0x0123 }
        r18 = r10;
        r10 = r16.getTimeUnit();	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        r10 = r13.get(r14, r10);	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        r10 = (org.awaitility.core.ConditionEvaluationResult) r10;	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        r11 = r10;	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        r10 = r11.isSuccessful();	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        if (r10 != 0) goto L_0x00da;	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        r10 = r11.hasThrowable();	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        if (r10 == 0) goto L_0x00b7;	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        goto L_0x00df;	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        r10 = r1.conditionSettings;	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        r10 = r10.getPollInterval();	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        r10 = r10.next(r9, r0);	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        r0 = r10;	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        r14 = r0.getValueInMS();	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        java.lang.Thread.sleep(r14);	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        r10 = calculateConditionEvaluationDuration(r2, r7);	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        r12 = r10;	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        r10 = r18;	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        goto L_0x006d;	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        goto L_0x00df;	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
    L_0x00dc:
        r18 = r10;	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        r10 = calculateConditionEvaluationDuration(r2, r7);	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        r12 = r10;	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        r10 = r3.compareTo(r12);	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        if (r10 <= 0) goto L_0x00f1;
        r10 = 1;
        goto L_0x00f3;
        r10 = 0;
        if (r13 == 0) goto L_0x00fd;
        r14 = 1;
        r13.cancel(r14);
        goto L_0x00fe;
    L_0x00fe:
        r16 = r2;
        r18 = r10;
        goto L_0x0191;
    L_0x0104:
        r18 = r10;
        r0 = new java.lang.IllegalStateException;	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        r10 = "The executor service that Awaitility is instructed to use has been shutdown so condition evaluation cannot be performed. Is there something wrong the thread or executor configuration?";	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        r0.<init>(r10);	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
        throw r0;	 Catch:{ TimeoutException -> 0x0117, ExecutionException -> 0x0115, Throwable -> 0x0113 }
    L_0x0113:
        r0 = move-exception;
        goto L_0x012f;
    L_0x0115:
        r0 = move-exception;
        goto L_0x0152;
    L_0x0117:
        r0 = move-exception;
        r16 = r2;
        goto L_0x0181;
    L_0x011c:
        r0 = move-exception;
        r18 = r10;
        r16 = r2;
        goto L_0x0181;
    L_0x0123:
        r0 = move-exception;
        r18 = r10;
        r16 = r2;
        r19 = r3;
        goto L_0x0328;
    L_0x012c:
        r0 = move-exception;
        r18 = r10;
        r10 = new org.awaitility.core.ConditionEvaluationResult;	 Catch:{ all -> 0x0148 }
        r14 = 0;	 Catch:{ all -> 0x0148 }
        r15 = 0;	 Catch:{ all -> 0x0148 }
        r10.<init>(r14, r0, r15);	 Catch:{ all -> 0x0148 }
        r11 = r10;
        if (r13 == 0) goto L_0x0144;
        r10 = 1;
        r13.cancel(r10);
        goto L_0x0145;
    L_0x0145:
        r16 = r2;
        goto L_0x0191;
    L_0x0148:
        r0 = move-exception;
        r16 = r2;
        r19 = r3;
        goto L_0x0328;
    L_0x014f:
        r0 = move-exception;
        r18 = r10;
        r10 = new org.awaitility.core.ConditionEvaluationResult;	 Catch:{ all -> 0x0175 }
        r14 = r0.getCause();	 Catch:{ all -> 0x0175 }
        r16 = r2;
        r2 = 0;
        r15 = 0;
        r10.<init>(r15, r14, r2);	 Catch:{ all -> 0x0170 }
        r11 = r10;
        if (r13 == 0) goto L_0x016e;
        r2 = 1;
        r13.cancel(r2);
        goto L_0x0191;
        goto L_0x0191;
    L_0x0170:
        r0 = move-exception;
        r19 = r3;
        goto L_0x0328;
    L_0x0175:
        r0 = move-exception;
        r16 = r2;
        r19 = r3;
        goto L_0x0328;
    L_0x017c:
        r0 = move-exception;
        r16 = r2;
        r18 = r10;
        r2 = new org.awaitility.core.ConditionEvaluationResult;	 Catch:{ all -> 0x0325 }
        r10 = 0;	 Catch:{ all -> 0x0325 }
        r14 = 0;	 Catch:{ all -> 0x0325 }
        r2.<init>(r10, r14, r0);	 Catch:{ all -> 0x0325 }
        r11 = r2;
        if (r13 == 0) goto L_0x016e;
    L_0x0190:
        goto L_0x0168;
        r0 = r1.uncaughtThrowable;	 Catch:{ Throwable -> 0x02f2, all -> 0x02ee }
        r0 = r0.get();	 Catch:{ Throwable -> 0x02f2, all -> 0x02ee }
        if (r0 != 0) goto L_0x02dd;
        if (r11 == 0) goto L_0x01b9;
        r0 = r11.hasThrowable();	 Catch:{ Throwable -> 0x01b4, all -> 0x01af }
        if (r0 != 0) goto L_0x01a8;	 Catch:{ Throwable -> 0x01b4, all -> 0x01af }
    L_0x01a7:
        goto L_0x01b9;	 Catch:{ Throwable -> 0x01b4, all -> 0x01af }
        r0 = r11.getThrowable();	 Catch:{ Throwable -> 0x01b4, all -> 0x01af }
        throw r0;	 Catch:{ Throwable -> 0x01b4, all -> 0x01af }
    L_0x01af:
        r0 = move-exception;
        r19 = r3;
        goto L_0x0311;
    L_0x01b4:
        r0 = move-exception;
        r19 = r3;
        goto L_0x02f5;
        r0 = 4;
        r2 = 2;
        if (r18 != 0) goto L_0x028d;
        r14 = r3.getTimeUnitAsString();	 Catch:{ Throwable -> 0x02f2, all -> 0x02ee }
        r15 = r1.conditionSettings;	 Catch:{ Throwable -> 0x02f2, all -> 0x02ee }
        r15 = r15.hasAlias();	 Catch:{ Throwable -> 0x02f2, all -> 0x02ee }
        if (r15 == 0) goto L_0x0206;
        r15 = "Condition with alias '%s' didn't complete within %s %s because %s.";	 Catch:{ Throwable -> 0x01b4, all -> 0x01af }
        r0 = new java.lang.Object[r0];	 Catch:{ Throwable -> 0x01b4, all -> 0x01af }
        r10 = r1.conditionSettings;	 Catch:{ Throwable -> 0x01b4, all -> 0x01af }
        r10 = r10.getAlias();	 Catch:{ Throwable -> 0x01b4, all -> 0x01af }
        r17 = 0;	 Catch:{ Throwable -> 0x01b4, all -> 0x01af }
        r0[r17] = r10;	 Catch:{ Throwable -> 0x01b4, all -> 0x01af }
        r10 = java.lang.Long.valueOf(r5);	 Catch:{ Throwable -> 0x01b4, all -> 0x01af }
        r17 = 1;	 Catch:{ Throwable -> 0x01b4, all -> 0x01af }
        r0[r17] = r10;	 Catch:{ Throwable -> 0x01b4, all -> 0x01af }
        r0[r2] = r14;	 Catch:{ Throwable -> 0x01b4, all -> 0x01af }
        r2 = r20.getTimeoutMessage();	 Catch:{ Throwable -> 0x01b4, all -> 0x01af }
        r2 = java.beans.Introspector.decapitalize(r2);	 Catch:{ Throwable -> 0x01b4, all -> 0x01af }
        r10 = 3;	 Catch:{ Throwable -> 0x01b4, all -> 0x01af }
        r0[r10] = r2;	 Catch:{ Throwable -> 0x01b4, all -> 0x01af }
        r0 = java.lang.String.format(r15, r0);	 Catch:{ Throwable -> 0x01b4, all -> 0x01af }
        r2 = r0;
        goto L_0x022b;
        r0 = "%s within %s %s.";	 Catch:{ Throwable -> 0x02f2, all -> 0x02ee }
        r10 = 3;	 Catch:{ Throwable -> 0x02f2, all -> 0x02ee }
        r10 = new java.lang.Object[r10];	 Catch:{ Throwable -> 0x02f2, all -> 0x02ee }
        r15 = r20.getTimeoutMessage();	 Catch:{ Throwable -> 0x02f2, all -> 0x02ee }
        r17 = 0;	 Catch:{ Throwable -> 0x02f2, all -> 0x02ee }
        r10[r17] = r15;	 Catch:{ Throwable -> 0x02f2, all -> 0x02ee }
        r15 = java.lang.Long.valueOf(r5);	 Catch:{ Throwable -> 0x02f2, all -> 0x02ee }
        r17 = 1;	 Catch:{ Throwable -> 0x02f2, all -> 0x02ee }
        r10[r17] = r15;	 Catch:{ Throwable -> 0x02f2, all -> 0x02ee }
        r10[r2] = r14;	 Catch:{ Throwable -> 0x02f2, all -> 0x02ee }
        r0 = java.lang.String.format(r0, r10);	 Catch:{ Throwable -> 0x02f2, all -> 0x02ee }
        r2 = r0;
        if (r11 == 0) goto L_0x023d;
        r0 = r11.hasTrace();	 Catch:{ Throwable -> 0x01b4, all -> 0x01af }
        if (r0 == 0) goto L_0x023d;	 Catch:{ Throwable -> 0x01b4, all -> 0x01af }
        r0 = r11.getTrace();	 Catch:{ Throwable -> 0x01b4, all -> 0x01af }
        goto L_0x0241;
        r0 = 0;
        r10 = r0;
        r0 = "java.lang.management.ThreadMXBean";	 Catch:{ Throwable -> 0x02f2, all -> 0x02ee }
        r0 = org.awaitility.classpath.ClassPathResolver.existInCP(r0);	 Catch:{ Throwable -> 0x02f2, all -> 0x02ee }
        if (r0 == 0) goto L_0x0281;	 Catch:{ Throwable -> 0x02f2, all -> 0x02ee }
        r0 = "java.lang.management.ManagementFactory";	 Catch:{ Throwable -> 0x02f2, all -> 0x02ee }
        r0 = org.awaitility.classpath.ClassPathResolver.existInCP(r0);	 Catch:{ Throwable -> 0x02f2, all -> 0x02ee }
        if (r0 == 0) goto L_0x0281;	 Catch:{ Throwable -> 0x02f2, all -> 0x02ee }
        r0 = java.lang.management.ManagementFactory.getThreadMXBean();	 Catch:{ Throwable -> 0x02f2, all -> 0x02ee }
        r15 = r0;
        r0 = r15.findDeadlockedThreads();	 Catch:{ UnsupportedOperationException -> 0x027c }
        if (r0 == 0) goto L_0x0277;
        r19 = r3;
        r3 = new org.awaitility.core.DeadlockException;	 Catch:{ UnsupportedOperationException -> 0x0275 }
        r3.<init>(r0);	 Catch:{ UnsupportedOperationException -> 0x0275 }
        r10 = r3;
        goto L_0x027a;
    L_0x0275:
        r0 = move-exception;
        goto L_0x027f;
    L_0x0277:
        r19 = r3;
        goto L_0x0284;
    L_0x027c:
        r0 = move-exception;
        r19 = r3;
        goto L_0x0284;
    L_0x0281:
        r19 = r3;
        r0 = new org.awaitility.core.ConditionTimeoutException;	 Catch:{ Throwable -> 0x02ec }
        r0.<init>(r2, r10);	 Catch:{ Throwable -> 0x02ec }
        throw r0;	 Catch:{ Throwable -> 0x02ec }
    L_0x028d:
        r19 = r3;	 Catch:{ Throwable -> 0x02ec }
        r3 = r12.compareTo(r4);	 Catch:{ Throwable -> 0x02ec }
        if (r3 < 0) goto L_0x0299;	 Catch:{ Throwable -> 0x02ec }
        goto L_0x02fb;	 Catch:{ Throwable -> 0x02ec }
        r3 = "Condition was evaluated in %s %s which is earlier than expected minimum timeout %s %s";	 Catch:{ Throwable -> 0x02ec }
        r0 = new java.lang.Object[r0];	 Catch:{ Throwable -> 0x02ec }
        r14 = r12.getValue();	 Catch:{ Throwable -> 0x02ec }
        r10 = java.lang.Long.valueOf(r14);	 Catch:{ Throwable -> 0x02ec }
        r14 = 0;	 Catch:{ Throwable -> 0x02ec }
        r0[r14] = r10;	 Catch:{ Throwable -> 0x02ec }
        r10 = r12.getTimeUnit();	 Catch:{ Throwable -> 0x02ec }
        r14 = 1;	 Catch:{ Throwable -> 0x02ec }
        r0[r14] = r10;	 Catch:{ Throwable -> 0x02ec }
        r14 = r4.getValue();	 Catch:{ Throwable -> 0x02ec }
        r10 = java.lang.Long.valueOf(r14);	 Catch:{ Throwable -> 0x02ec }
        r0[r2] = r10;	 Catch:{ Throwable -> 0x02ec }
        r2 = r4.getTimeUnit();	 Catch:{ Throwable -> 0x02ec }
        r10 = 3;	 Catch:{ Throwable -> 0x02ec }
        r0[r10] = r2;	 Catch:{ Throwable -> 0x02ec }
        r0 = java.lang.String.format(r3, r0);	 Catch:{ Throwable -> 0x02ec }
        r2 = new org.awaitility.core.ConditionTimeoutException;	 Catch:{ Throwable -> 0x02ec }
        r2.<init>(r0);	 Catch:{ Throwable -> 0x02ec }
        throw r2;	 Catch:{ Throwable -> 0x02ec }
    L_0x02dd:
        r19 = r3;	 Catch:{ Throwable -> 0x02ec }
        r0 = r1.uncaughtThrowable;	 Catch:{ Throwable -> 0x02ec }
        r0 = r0.get();	 Catch:{ Throwable -> 0x02ec }
        r0 = (java.lang.Throwable) r0;	 Catch:{ Throwable -> 0x02ec }
        throw r0;	 Catch:{ Throwable -> 0x02ec }
    L_0x02ec:
        r0 = move-exception;
        goto L_0x02f5;
    L_0x02ee:
        r0 = move-exception;
        r19 = r3;
        goto L_0x0311;
    L_0x02f2:
        r0 = move-exception;
        r19 = r3;
        org.awaitility.core.CheckedExceptionRethrower.safeRethrow(r0);	 Catch:{ all -> 0x0310 }
    L_0x02fb:
        r0 = r1.uncaughtThrowable;
        r2 = 0;
        r0.set(r2);
        r0 = r1.conditionSettings;
        r0 = r0.getExecutorLifecycle();
        r2 = r1.executor;
        r0.executeNormalCleanupBehavior(r2);
        return;
    L_0x0310:
        r0 = move-exception;
        r2 = r1.uncaughtThrowable;
        r3 = 0;
        r2.set(r3);
        r2 = r1.conditionSettings;
        r2 = r2.getExecutorLifecycle();
        r3 = r1.executor;
        r2.executeNormalCleanupBehavior(r3);
        throw r0;
    L_0x0325:
        r0 = move-exception;
        r19 = r3;
        if (r13 == 0) goto L_0x0331;
        r2 = 1;
        r13.cancel(r2);
        goto L_0x0332;
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.awaitility.core.ConditionAwaiter.await(org.awaitility.core.ConditionEvaluationHandler):void");
    }

    protected abstract String getTimeoutMessage();

    ConditionAwaiter(ConditionEvaluator conditionEvaluator, ConditionSettings conditionSettings) {
        if (conditionEvaluator == null) {
            throw new IllegalArgumentException("You must specify a condition (was null).");
        } else if (conditionSettings != null) {
            if (conditionSettings.shouldCatchUncaughtExceptions()) {
                Thread.setDefaultUncaughtExceptionHandler(this);
            }
            this.conditionSettings = conditionSettings;
            this.conditionEvaluator = conditionEvaluator;
            this.executor = conditionSettings.getExecutorLifecycle().supplyExecutorService();
            this.uncaughtThrowable = new AtomicReference();
        } else {
            throw new IllegalArgumentException("You must specify the condition settings (was null).");
        }
    }

    public void uncaughtException(Thread thread, Throwable throwable) {
        if (!this.conditionSettings.shouldExceptionBeIgnored(throwable)) {
            this.uncaughtThrowable.set(throwable);
            this.conditionSettings.getExecutorLifecycle().executeUnexpectedCleanupBehavior(this.executor);
        }
    }

    static Duration calculateConditionEvaluationDuration(Duration pollDelay, long pollingStarted) {
        return new Duration((System.nanoTime() - pollingStarted) - pollDelay.getValueInNS(), TimeUnit.NANOSECONDS);
    }
}
