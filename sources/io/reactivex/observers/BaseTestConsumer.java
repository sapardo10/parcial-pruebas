package io.reactivex.observers;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import io.reactivex.Notification;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.internal.util.VolatileSizeArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class BaseTestConsumer<T, U extends BaseTestConsumer<T, U>> implements Disposable {
    protected boolean checkSubscriptionOnce;
    protected long completions;
    protected final CountDownLatch done = new CountDownLatch(1);
    protected final List<Throwable> errors = new VolatileSizeArrayList();
    protected int establishedFusionMode;
    protected int initialFusionMode;
    protected Thread lastThread;
    protected CharSequence tag;
    protected boolean timeout;
    protected final List<T> values = new VolatileSizeArrayList();

    public enum TestWaitStrategy implements Runnable {
        SPIN {
            public void run() {
            }
        },
        YIELD {
            public void run() {
                Thread.yield();
            }
        },
        SLEEP_1MS {
            public void run() {
                TestWaitStrategy.sleep(1);
            }
        },
        SLEEP_10MS {
            public void run() {
                TestWaitStrategy.sleep(10);
            }
        },
        SLEEP_100MS {
            public void run() {
                TestWaitStrategy.sleep(100);
            }
        },
        SLEEP_1000MS {
            public void run() {
                TestWaitStrategy.sleep(1000);
            }
        };

        public abstract void run();

        static void sleep(int millis) {
            try {
                Thread.sleep((long) millis);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public final U assertError(io.reactivex.functions.Predicate<java.lang.Throwable> r6) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:25:0x0047 in {9, 10, 13, 14, 18, 20, 22, 24} preds:[]
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
        r5 = this;
        r0 = r5.errors;
        r0 = r0.size();
        if (r0 == 0) goto L_0x0040;
    L_0x0008:
        r1 = 0;
        r2 = r5.errors;
        r2 = r2.iterator();
    L_0x000f:
        r3 = r2.hasNext();
        if (r3 == 0) goto L_0x002b;
    L_0x0015:
        r3 = r2.next();
        r3 = (java.lang.Throwable) r3;
        r4 = r6.test(r3);	 Catch:{ Exception -> 0x0025 }
        if (r4 == 0) goto L_0x0023;
    L_0x0021:
        r1 = 1;
        goto L_0x002c;
        goto L_0x000f;
    L_0x0025:
        r2 = move-exception;
        r4 = io.reactivex.internal.util.ExceptionHelper.wrapOrThrow(r2);
        throw r4;
    L_0x002c:
        if (r1 == 0) goto L_0x0039;
    L_0x002e:
        r2 = 1;
        if (r0 != r2) goto L_0x0032;
    L_0x0031:
        return r5;
    L_0x0032:
        r2 = "Error present but other errors as well";
        r2 = r5.fail(r2);
        throw r2;
    L_0x0039:
        r2 = "Error not present";
        r2 = r5.fail(r2);
        throw r2;
    L_0x0040:
        r1 = "No errors";
        r1 = r5.fail(r1);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.observers.BaseTestConsumer.assertError(io.reactivex.functions.Predicate):U");
    }

    public abstract U assertNotSubscribed();

    public abstract U assertSubscribed();

    public final U assertValueSequence(java.lang.Iterable<? extends T> r10) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:18:0x0094 in {4, 7, 9, 10, 13, 15, 17} preds:[]
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
        r9 = this;
        r0 = 0;
        r1 = r9.values;
        r1 = r1.iterator();
        r2 = r10.iterator();
    L_0x000b:
        r3 = r2.hasNext();
        r4 = r1.hasNext();
        if (r4 == 0) goto L_0x0058;
    L_0x0015:
        if (r3 != 0) goto L_0x0018;
    L_0x0017:
        goto L_0x0059;
    L_0x0018:
        r5 = r2.next();
        r6 = r1.next();
        r7 = io.reactivex.internal.functions.ObjectHelper.equals(r5, r6);
        if (r7 == 0) goto L_0x002a;
        r0 = r0 + 1;
        goto L_0x000b;
    L_0x002a:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "Values at position ";
        r7.append(r8);
        r7.append(r0);
        r8 = " differ; Expected: ";
        r7.append(r8);
        r8 = valueAndClass(r5);
        r7.append(r8);
        r8 = ", Actual: ";
        r7.append(r8);
        r8 = valueAndClass(r6);
        r7.append(r8);
        r7 = r7.toString();
        r7 = r9.fail(r7);
        throw r7;
    L_0x0059:
        if (r4 != 0) goto L_0x0079;
    L_0x005b:
        if (r3 != 0) goto L_0x005e;
    L_0x005d:
        return r9;
    L_0x005e:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Fewer values received than expected (";
        r5.append(r6);
        r5.append(r0);
        r6 = ")";
        r5.append(r6);
        r5 = r5.toString();
        r5 = r9.fail(r5);
        throw r5;
    L_0x0079:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "More values received than expected (";
        r5.append(r6);
        r5.append(r0);
        r6 = ")";
        r5.append(r6);
        r5 = r5.toString();
        r5 = r9.fail(r5);
        throw r5;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.observers.BaseTestConsumer.assertValueSequence(java.lang.Iterable):U");
    }

    public final U assertValues(T... r7) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:13:0x0082 in {6, 8, 10, 12} preds:[]
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
        r0 = r6.values;
        r0 = r0.size();
        r1 = r7.length;
        if (r0 != r1) goto L_0x004d;
    L_0x0009:
        r1 = 0;
    L_0x000a:
        if (r1 >= r0) goto L_0x004b;
    L_0x000c:
        r2 = r6.values;
        r2 = r2.get(r1);
        r3 = r7[r1];
        r4 = io.reactivex.internal.functions.ObjectHelper.equals(r3, r2);
        if (r4 == 0) goto L_0x001d;
    L_0x001a:
        r1 = r1 + 1;
        goto L_0x000a;
    L_0x001d:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Values at position ";
        r4.append(r5);
        r4.append(r1);
        r5 = " differ; Expected: ";
        r4.append(r5);
        r5 = valueAndClass(r3);
        r4.append(r5);
        r5 = ", Actual: ";
        r4.append(r5);
        r5 = valueAndClass(r2);
        r4.append(r5);
        r4 = r4.toString();
        r4 = r6.fail(r4);
        throw r4;
        return r6;
    L_0x004d:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Value count differs; Expected: ";
        r1.append(r2);
        r2 = r7.length;
        r1.append(r2);
        r2 = " ";
        r1.append(r2);
        r2 = java.util.Arrays.toString(r7);
        r1.append(r2);
        r2 = ", Actual: ";
        r1.append(r2);
        r1.append(r0);
        r2 = " ";
        r1.append(r2);
        r2 = r6.values;
        r1.append(r2);
        r1 = r1.toString();
        r1 = r6.fail(r1);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.observers.BaseTestConsumer.assertValues(java.lang.Object[]):U");
    }

    public final Thread lastThread() {
        return this.lastThread;
    }

    public final List<T> values() {
        return this.values;
    }

    public final List<Throwable> errors() {
        return this.errors;
    }

    public final long completions() {
        return this.completions;
    }

    public final boolean isTerminated() {
        return this.done.getCount() == 0;
    }

    public final int valueCount() {
        return this.values.size();
    }

    public final int errorCount() {
        return this.errors.size();
    }

    protected final AssertionError fail(String message) {
        StringBuilder b = new StringBuilder(message.length() + 64);
        b.append(message);
        b.append(" (");
        b.append("latch = ");
        b.append(this.done.getCount());
        b.append(", ");
        b.append("values = ");
        b.append(this.values.size());
        b.append(", ");
        b.append("errors = ");
        b.append(this.errors.size());
        b.append(", ");
        b.append("completions = ");
        b.append(this.completions);
        if (this.timeout) {
            b.append(", timeout!");
        }
        if (isDisposed()) {
            b.append(", disposed!");
        }
        CharSequence tag = this.tag;
        if (tag != null) {
            b.append(", tag = ");
            b.append(tag);
        }
        b.append(')');
        AssertionError ae = new AssertionError(b.toString());
        if (!this.errors.isEmpty()) {
            if (this.errors.size() == 1) {
                ae.initCause((Throwable) this.errors.get(0));
            } else {
                ae.initCause(new CompositeException(this.errors));
            }
        }
        return ae;
    }

    public final U await() throws InterruptedException {
        if (this.done.getCount() == 0) {
            return this;
        }
        this.done.await();
        return this;
    }

    public final boolean await(long time, TimeUnit unit) throws InterruptedException {
        boolean d;
        boolean z = false;
        if (this.done.getCount() != 0) {
            if (!this.done.await(time, unit)) {
                d = false;
                if (!d) {
                    z = true;
                }
                this.timeout = z;
                return d;
            }
        }
        d = true;
        if (d) {
            z = true;
        }
        this.timeout = z;
        return d;
    }

    public final U assertComplete() {
        long c = this.completions;
        if (c == 0) {
            throw fail("Not completed");
        } else if (c <= 1) {
            return this;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Multiple completions: ");
            stringBuilder.append(c);
            throw fail(stringBuilder.toString());
        }
    }

    public final U assertNotComplete() {
        long c = this.completions;
        if (c == 1) {
            throw fail("Completed!");
        } else if (c <= 1) {
            return this;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Multiple completions: ");
            stringBuilder.append(c);
            throw fail(stringBuilder.toString());
        }
    }

    public final U assertNoErrors() {
        if (this.errors.size() == 0) {
            return this;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Error(s) present: ");
        stringBuilder.append(this.errors);
        throw fail(stringBuilder.toString());
    }

    public final U assertError(Throwable error) {
        return assertError(Functions.equalsWith(error));
    }

    public final U assertError(Class<? extends Throwable> errorClass) {
        return assertError(Functions.isInstanceOf(errorClass));
    }

    public final U assertValue(T value) {
        if (this.values.size() == 1) {
            T v = this.values.get(0);
            if (ObjectHelper.equals(value, v)) {
                return this;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Expected: ");
            stringBuilder.append(valueAndClass(value));
            stringBuilder.append(", Actual: ");
            stringBuilder.append(valueAndClass(v));
            throw fail(stringBuilder.toString());
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Expected: ");
        stringBuilder2.append(valueAndClass(value));
        stringBuilder2.append(", Actual: ");
        stringBuilder2.append(this.values);
        throw fail(stringBuilder2.toString());
    }

    public final U assertNever(T value) {
        int s = this.values.size();
        for (int i = 0; i < s; i++) {
            if (ObjectHelper.equals(this.values.get(i), value)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Value at position ");
                stringBuilder.append(i);
                stringBuilder.append(" is equal to ");
                stringBuilder.append(valueAndClass(value));
                stringBuilder.append("; Expected them to be different");
                throw fail(stringBuilder.toString());
            }
        }
        return this;
    }

    public final U assertValue(Predicate<T> valuePredicate) {
        assertValueAt(0, (Predicate) valuePredicate);
        if (this.values.size() <= 1) {
            return this;
        }
        throw fail("Value present but other values as well");
    }

    public final U assertNever(Predicate<? super T> valuePredicate) {
        int s = this.values.size();
        int i = 0;
        while (i < s) {
            try {
                if (valuePredicate.test(this.values.get(i))) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Value at position ");
                    stringBuilder.append(i);
                    stringBuilder.append(" matches predicate ");
                    stringBuilder.append(valuePredicate.toString());
                    stringBuilder.append(", which was not expected.");
                    throw fail(stringBuilder.toString());
                }
                i++;
            } catch (Exception ex) {
                throw ExceptionHelper.wrapOrThrow(ex);
            }
        }
        return this;
    }

    public final U assertValueAt(int index, T value) {
        int s = this.values.size();
        if (s == 0) {
            throw fail("No values");
        } else if (index < s) {
            T v = this.values.get(index);
            if (ObjectHelper.equals(value, v)) {
                return this;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Expected: ");
            stringBuilder.append(valueAndClass(value));
            stringBuilder.append(", Actual: ");
            stringBuilder.append(valueAndClass(v));
            throw fail(stringBuilder.toString());
        } else {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Invalid index: ");
            stringBuilder2.append(index);
            throw fail(stringBuilder2.toString());
        }
    }

    public final U assertValueAt(int index, Predicate<T> valuePredicate) {
        if (this.values.size() == 0) {
            throw fail("No values");
        } else if (index < this.values.size()) {
            boolean found = false;
            try {
                if (valuePredicate.test(this.values.get(index))) {
                    found = true;
                }
                if (found) {
                    return this;
                }
                throw fail("Value not present");
            } catch (Exception ex) {
                throw ExceptionHelper.wrapOrThrow(ex);
            }
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Invalid index: ");
            stringBuilder.append(index);
            throw fail(stringBuilder.toString());
        }
    }

    public static String valueAndClass(Object o) {
        if (o == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(o);
        stringBuilder.append(" (class: ");
        stringBuilder.append(o.getClass().getSimpleName());
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public final U assertValueCount(int count) {
        int s = this.values.size();
        if (s == count) {
            return this;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Value counts differ; Expected: ");
        stringBuilder.append(count);
        stringBuilder.append(", Actual: ");
        stringBuilder.append(s);
        throw fail(stringBuilder.toString());
    }

    public final U assertNoValues() {
        return assertValueCount(0);
    }

    public final U assertValuesOnly(T... values) {
        return assertSubscribed().assertValues(values).assertNoErrors().assertNotComplete();
    }

    public final U assertValueSet(Collection<? extends T> expected) {
        if (expected.isEmpty()) {
            assertNoValues();
            return this;
        }
        for (T v : this.values) {
            if (!expected.contains(v)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Value not in the expected collection: ");
                stringBuilder.append(valueAndClass(v));
                throw fail(stringBuilder.toString());
            }
        }
        return this;
    }

    public final U assertValueSetOnly(Collection<? extends T> expected) {
        return assertSubscribed().assertValueSet(expected).assertNoErrors().assertNotComplete();
    }

    public final U assertValueSequenceOnly(Iterable<? extends T> sequence) {
        return assertSubscribed().assertValueSequence(sequence).assertNoErrors().assertNotComplete();
    }

    public final U assertTerminated() {
        if (this.done.getCount() == 0) {
            long c = this.completions;
            StringBuilder stringBuilder;
            if (c <= 1) {
                int s = this.errors.size();
                if (s <= 1) {
                    if (c != 0) {
                        if (s != 0) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Terminated with multiple completions and errors: ");
                            stringBuilder.append(c);
                            throw fail(stringBuilder.toString());
                        }
                    }
                    return this;
                }
                stringBuilder = new StringBuilder();
                stringBuilder.append("Terminated with multiple errors: ");
                stringBuilder.append(s);
                throw fail(stringBuilder.toString());
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append("Terminated with multiple completions: ");
            stringBuilder.append(c);
            throw fail(stringBuilder.toString());
        }
        throw fail("Subscriber still running!");
    }

    public final U assertNotTerminated() {
        if (this.done.getCount() != 0) {
            return this;
        }
        throw fail("Subscriber terminated!");
    }

    public final boolean awaitTerminalEvent() {
        try {
            await();
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public final boolean awaitTerminalEvent(long duration, TimeUnit unit) {
        try {
            return await(duration, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public final U assertErrorMessage(String message) {
        int s = this.errors.size();
        if (s == 0) {
            throw fail("No errors");
        } else if (s == 1) {
            String errorMessage = ((Throwable) this.errors.get(0)).getMessage();
            if (ObjectHelper.equals(message, errorMessage)) {
                return this;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Error message differs; Expected: ");
            stringBuilder.append(message);
            stringBuilder.append(", Actual: ");
            stringBuilder.append(errorMessage);
            throw fail(stringBuilder.toString());
        } else {
            throw fail("Multiple errors");
        }
    }

    public final List<List<Object>> getEvents() {
        List<List<Object>> result = new ArrayList();
        result.add(values());
        result.add(errors());
        List<Object> completeList = new ArrayList();
        for (long i = 0; i < this.completions; i++) {
            completeList.add(Notification.createOnComplete());
        }
        result.add(completeList);
        return result;
    }

    public final U assertResult(T... values) {
        return assertSubscribed().assertValues(values).assertNoErrors().assertComplete();
    }

    public final U assertFailure(Class<? extends Throwable> error, T... values) {
        return assertSubscribed().assertValues(values).assertError((Class) error).assertNotComplete();
    }

    public final U assertFailure(Predicate<Throwable> errorPredicate, T... values) {
        return assertSubscribed().assertValues(values).assertError((Predicate) errorPredicate).assertNotComplete();
    }

    public final U assertFailureAndMessage(Class<? extends Throwable> error, String message, T... values) {
        return assertSubscribed().assertValues(values).assertError((Class) error).assertErrorMessage(message).assertNotComplete();
    }

    public final U awaitDone(long time, TimeUnit unit) {
        try {
            if (!this.done.await(time, unit)) {
                this.timeout = true;
                dispose();
            }
            return this;
        } catch (InterruptedException ex) {
            dispose();
            throw ExceptionHelper.wrapOrThrow(ex);
        }
    }

    public final U assertEmpty() {
        return assertSubscribed().assertNoValues().assertNoErrors().assertNotComplete();
    }

    public final U withTag(CharSequence tag) {
        this.tag = tag;
        return this;
    }

    public final U awaitCount(int atLeast) {
        return awaitCount(atLeast, TestWaitStrategy.SLEEP_10MS, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    public final U awaitCount(int atLeast, Runnable waitStrategy) {
        return awaitCount(atLeast, waitStrategy, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final U awaitCount(int r8, java.lang.Runnable r9, long r10) {
        /*
        r7 = this;
        r0 = java.lang.System.currentTimeMillis();
    L_0x0004:
        r2 = 0;
        r4 = (r10 > r2 ? 1 : (r10 == r2 ? 0 : -1));
        if (r4 <= 0) goto L_0x0017;
    L_0x000a:
        r4 = java.lang.System.currentTimeMillis();
        r4 = r4 - r0;
        r6 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1));
        if (r6 < 0) goto L_0x0017;
    L_0x0013:
        r2 = 1;
        r7.timeout = r2;
        goto L_0x002c;
        r4 = r7.done;
        r4 = r4.getCount();
        r6 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));
        if (r6 != 0) goto L_0x0023;
    L_0x0022:
        goto L_0x002c;
    L_0x0023:
        r2 = r7.values;
        r2 = r2.size();
        if (r2 < r8) goto L_0x002d;
    L_0x002c:
        return r7;
    L_0x002d:
        r9.run();
        goto L_0x0004;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.observers.BaseTestConsumer.awaitCount(int, java.lang.Runnable, long):U");
    }

    public final boolean isTimeout() {
        return this.timeout;
    }

    public final U clearTimeout() {
        this.timeout = false;
        return this;
    }

    public final U assertTimeout() {
        if (this.timeout) {
            return this;
        }
        throw fail("No timeout?!");
    }

    public final U assertNoTimeout() {
        if (!this.timeout) {
            return this;
        }
        throw fail("Timeout?!");
    }
}
