package io.reactivex.exceptions;

import io.reactivex.annotations.NonNull;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CompositeException extends RuntimeException {
    private static final long serialVersionUID = 3026362227162912146L;
    private Throwable cause;
    private final List<Throwable> exceptions;
    private final String message;

    static final class CompositeExceptionCausalChain extends RuntimeException {
        static final String MESSAGE = "Chain of Causes for CompositeException In Order Received =>";
        private static final long serialVersionUID = 3875212506787802066L;

        CompositeExceptionCausalChain() {
        }

        public String getMessage() {
            return MESSAGE;
        }
    }

    static abstract class PrintStreamOrWriter {
        abstract void println(Object obj);

        PrintStreamOrWriter() {
        }
    }

    static final class WrappedPrintStream extends PrintStreamOrWriter {
        private final PrintStream printStream;

        WrappedPrintStream(PrintStream printStream) {
            this.printStream = printStream;
        }

        void println(Object o) {
            this.printStream.println(o);
        }
    }

    static final class WrappedPrintWriter extends PrintStreamOrWriter {
        private final PrintWriter printWriter;

        WrappedPrintWriter(PrintWriter printWriter) {
            this.printWriter = printWriter;
        }

        void println(Object o) {
            this.printWriter.println(o);
        }
    }

    public CompositeException(@io.reactivex.annotations.NonNull java.lang.Iterable<? extends java.lang.Throwable> r7) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:20:0x007b in {7, 9, 10, 11, 12, 13, 17, 19} preds:[]
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
        r6.<init>();
        r0 = new java.util.LinkedHashSet;
        r0.<init>();
        r1 = new java.util.ArrayList;
        r1.<init>();
        if (r7 == 0) goto L_0x0040;
    L_0x000f:
        r2 = r7.iterator();
    L_0x0013:
        r3 = r2.hasNext();
        if (r3 == 0) goto L_0x003f;
    L_0x0019:
        r3 = r2.next();
        r3 = (java.lang.Throwable) r3;
        r4 = r3 instanceof io.reactivex.exceptions.CompositeException;
        if (r4 == 0) goto L_0x002e;
    L_0x0023:
        r4 = r3;
        r4 = (io.reactivex.exceptions.CompositeException) r4;
        r4 = r4.getExceptions();
        r0.addAll(r4);
        goto L_0x003e;
    L_0x002e:
        if (r3 == 0) goto L_0x0034;
    L_0x0030:
        r0.add(r3);
        goto L_0x003e;
    L_0x0034:
        r4 = new java.lang.NullPointerException;
        r5 = "Throwable was null!";
        r4.<init>(r5);
        r0.add(r4);
    L_0x003e:
        goto L_0x0013;
    L_0x003f:
        goto L_0x004a;
    L_0x0040:
        r2 = new java.lang.NullPointerException;
        r3 = "errors was null";
        r2.<init>(r3);
        r0.add(r2);
    L_0x004a:
        r2 = r0.isEmpty();
        if (r2 != 0) goto L_0x0073;
    L_0x0050:
        r1.addAll(r0);
        r2 = java.util.Collections.unmodifiableList(r1);
        r6.exceptions = r2;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = r6.exceptions;
        r3 = r3.size();
        r2.append(r3);
        r3 = " exceptions occurred. ";
        r2.append(r3);
        r2 = r2.toString();
        r6.message = r2;
        return;
    L_0x0073:
        r2 = new java.lang.IllegalArgumentException;
        r3 = "errors is empty";
        r2.<init>(r3);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.exceptions.CompositeException.<init>(java.lang.Iterable):void");
    }

    @io.reactivex.annotations.NonNull
    public synchronized java.lang.Throwable getCause() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:32:0x0069 in {9, 15, 17, 20, 21, 23, 24, 25, 28, 31} preds:[]
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
        monitor-enter(r10);
        r0 = r10.cause;	 Catch:{ all -> 0x0066 }
        if (r0 != 0) goto L_0x0061;	 Catch:{ all -> 0x0066 }
    L_0x0005:
        r0 = new io.reactivex.exceptions.CompositeException$CompositeExceptionCausalChain;	 Catch:{ all -> 0x0066 }
        r0.<init>();	 Catch:{ all -> 0x0066 }
        r1 = new java.util.HashSet;	 Catch:{ all -> 0x0066 }
        r1.<init>();	 Catch:{ all -> 0x0066 }
        r2 = r0;	 Catch:{ all -> 0x0066 }
        r3 = r10.exceptions;	 Catch:{ all -> 0x0066 }
        r3 = r3.iterator();	 Catch:{ all -> 0x0066 }
    L_0x0016:
        r4 = r3.hasNext();	 Catch:{ all -> 0x0066 }
        if (r4 == 0) goto L_0x005e;	 Catch:{ all -> 0x0066 }
    L_0x001c:
        r4 = r3.next();	 Catch:{ all -> 0x0066 }
        r4 = (java.lang.Throwable) r4;	 Catch:{ all -> 0x0066 }
        r5 = r1.contains(r4);	 Catch:{ all -> 0x0066 }
        if (r5 == 0) goto L_0x0029;	 Catch:{ all -> 0x0066 }
    L_0x0028:
        goto L_0x0016;	 Catch:{ all -> 0x0066 }
    L_0x0029:
        r1.add(r4);	 Catch:{ all -> 0x0066 }
        r5 = r10.getListOfCauses(r4);	 Catch:{ all -> 0x0066 }
        r6 = r5.iterator();	 Catch:{ all -> 0x0066 }
    L_0x0034:
        r7 = r6.hasNext();	 Catch:{ all -> 0x0066 }
        if (r7 == 0) goto L_0x0053;	 Catch:{ all -> 0x0066 }
    L_0x003a:
        r7 = r6.next();	 Catch:{ all -> 0x0066 }
        r7 = (java.lang.Throwable) r7;	 Catch:{ all -> 0x0066 }
        r8 = r1.contains(r7);	 Catch:{ all -> 0x0066 }
        if (r8 == 0) goto L_0x004f;	 Catch:{ all -> 0x0066 }
    L_0x0046:
        r8 = new java.lang.RuntimeException;	 Catch:{ all -> 0x0066 }
        r9 = "Duplicate found in causal chain so cropping to prevent loop ...";	 Catch:{ all -> 0x0066 }
        r8.<init>(r9);	 Catch:{ all -> 0x0066 }
        r4 = r8;	 Catch:{ all -> 0x0066 }
        goto L_0x0034;	 Catch:{ all -> 0x0066 }
    L_0x004f:
        r1.add(r7);	 Catch:{ all -> 0x0066 }
        goto L_0x0034;
    L_0x0053:
        r2.initCause(r4);	 Catch:{ Throwable -> 0x0057 }
        goto L_0x0058;
    L_0x0057:
        r6 = move-exception;
    L_0x0058:
        r6 = r10.getRootCause(r2);	 Catch:{ all -> 0x0066 }
        r2 = r6;	 Catch:{ all -> 0x0066 }
        goto L_0x0016;	 Catch:{ all -> 0x0066 }
    L_0x005e:
        r10.cause = r0;	 Catch:{ all -> 0x0066 }
        goto L_0x0062;	 Catch:{ all -> 0x0066 }
    L_0x0062:
        r0 = r10.cause;	 Catch:{ all -> 0x0066 }
        monitor-exit(r10);
        return r0;
    L_0x0066:
        r0 = move-exception;
        monitor-exit(r10);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.reactivex.exceptions.CompositeException.getCause():java.lang.Throwable");
    }

    public CompositeException(@NonNull Throwable... exceptions) {
        this(exceptions == null ? Collections.singletonList(new NullPointerException("exceptions was null")) : Arrays.asList(exceptions));
    }

    @NonNull
    public List<Throwable> getExceptions() {
        return this.exceptions;
    }

    @NonNull
    public String getMessage() {
        return this.message;
    }

    public void printStackTrace() {
        printStackTrace(System.err);
    }

    public void printStackTrace(PrintStream s) {
        printStackTrace(new WrappedPrintStream(s));
    }

    public void printStackTrace(PrintWriter s) {
        printStackTrace(new WrappedPrintWriter(s));
    }

    private void printStackTrace(PrintStreamOrWriter s) {
        StringBuilder b = new StringBuilder(128);
        b.append(this);
        b.append('\n');
        for (StackTraceElement myStackElement : getStackTrace()) {
            b.append("\tat ");
            b.append(myStackElement);
            b.append('\n');
        }
        int i = 1;
        for (Throwable ex : this.exceptions) {
            b.append("  ComposedException ");
            b.append(i);
            b.append(" :\n");
            appendStackTrace(b, ex, "\t");
            i++;
        }
        s.println(b.toString());
    }

    private void appendStackTrace(StringBuilder b, Throwable ex, String prefix) {
        b.append(prefix);
        b.append(ex);
        b.append('\n');
        for (StackTraceElement stackElement : ex.getStackTrace()) {
            b.append("\t\tat ");
            b.append(stackElement);
            b.append('\n');
        }
        if (ex.getCause() != null) {
            b.append("\tCaused by: ");
            appendStackTrace(b, ex.getCause(), "");
        }
    }

    private List<Throwable> getListOfCauses(Throwable ex) {
        List<Throwable> list = new ArrayList();
        Throwable root = ex.getCause();
        if (root != null) {
            if (root != ex) {
                while (true) {
                    list.add(root);
                    Throwable cause = root.getCause();
                    if (cause == null) {
                        break;
                    } else if (cause == root) {
                        break;
                    } else {
                        root = cause;
                    }
                }
                return list;
            }
        }
        return list;
    }

    public int size() {
        return this.exceptions.size();
    }

    Throwable getRootCause(Throwable e) {
        Throwable root = e.getCause();
        if (root != null) {
            if (this.cause != root) {
                while (true) {
                    Throwable cause = root.getCause();
                    if (cause == null) {
                        break;
                    } else if (cause == root) {
                        break;
                    } else {
                        root = cause;
                    }
                }
                return root;
            }
        }
        return e;
    }
}
