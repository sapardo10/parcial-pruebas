package kotlin.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.internal.InlineOnly;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000z\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\u001a\u0012\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004\u001a\u001c\u0010\u0005\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t\u001a!\u0010\n\u001a\u00020\u000b*\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\f\u001a\u00020\rH\b\u001a!\u0010\u000e\u001a\u00020\u000f*\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\f\u001a\u00020\rH\b\u001aB\u0010\u0010\u001a\u00020\u0001*\u00020\u000226\u0010\u0011\u001a2\u0012\u0013\u0012\u00110\u0004¢\u0006\f\b\u0013\u0012\b\b\u0014\u0012\u0004\b\b(\u0015\u0012\u0013\u0012\u00110\r¢\u0006\f\b\u0013\u0012\b\b\u0014\u0012\u0004\b\b(\u0016\u0012\u0004\u0012\u00020\u00010\u0012\u001aJ\u0010\u0010\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0017\u001a\u00020\r26\u0010\u0011\u001a2\u0012\u0013\u0012\u00110\u0004¢\u0006\f\b\u0013\u0012\b\b\u0014\u0012\u0004\b\b(\u0015\u0012\u0013\u0012\u00110\r¢\u0006\f\b\u0013\u0012\b\b\u0014\u0012\u0004\b\b(\u0016\u0012\u0004\u0012\u00020\u00010\u0012\u001a7\u0010\u0018\u001a\u00020\u0001*\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\t2!\u0010\u0011\u001a\u001d\u0012\u0013\u0012\u00110\u0007¢\u0006\f\b\u0013\u0012\b\b\u0014\u0012\u0004\b\b(\u001a\u0012\u0004\u0012\u00020\u00010\u0019\u001a\r\u0010\u001b\u001a\u00020\u001c*\u00020\u0002H\b\u001a\r\u0010\u001d\u001a\u00020\u001e*\u00020\u0002H\b\u001a\u0017\u0010\u001f\u001a\u00020 *\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\tH\b\u001a\n\u0010!\u001a\u00020\u0004*\u00020\u0002\u001a\u001a\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00070#*\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\t\u001a\u0014\u0010$\u001a\u00020\u0007*\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\t\u001a\u0017\u0010%\u001a\u00020&*\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\tH\b\u001a?\u0010'\u001a\u0002H(\"\u0004\b\u0000\u0010(*\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\t2\u0018\u0010)\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070*\u0012\u0004\u0012\u0002H(0\u0019H\bø\u0001\u0000¢\u0006\u0002\u0010,\u001a\u0012\u0010-\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004\u001a\u001c\u0010.\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t\u001a\u0017\u0010/\u001a\u000200*\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\tH\b\u0002\b\n\u0006\b\u0011(+0\u0001¨\u00061"}, d2 = {"appendBytes", "", "Ljava/io/File;", "array", "", "appendText", "text", "", "charset", "Ljava/nio/charset/Charset;", "bufferedReader", "Ljava/io/BufferedReader;", "bufferSize", "", "bufferedWriter", "Ljava/io/BufferedWriter;", "forEachBlock", "action", "Lkotlin/Function2;", "Lkotlin/ParameterName;", "name", "buffer", "bytesRead", "blockSize", "forEachLine", "Lkotlin/Function1;", "line", "inputStream", "Ljava/io/FileInputStream;", "outputStream", "Ljava/io/FileOutputStream;", "printWriter", "Ljava/io/PrintWriter;", "readBytes", "readLines", "", "readText", "reader", "Ljava/io/InputStreamReader;", "useLines", "T", "block", "Lkotlin/sequences/Sequence;", "Requires newer compiler version to be inlined correctly.", "(Ljava/io/File;Ljava/nio/charset/Charset;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "writeBytes", "writeText", "writer", "Ljava/io/OutputStreamWriter;", "kotlin-stdlib"}, k = 5, mv = {1, 1, 10}, xi = 1, xs = "kotlin/io/FilesKt")
/* compiled from: FileReadWrite.kt */
class FilesKt__FileReadWriteKt extends FilesKt__FilePathComponentsKt {
    public static final void forEachBlock(@org.jetbrains.annotations.NotNull java.io.File r7, int r8, @org.jetbrains.annotations.NotNull kotlin.jvm.functions.Function2<? super byte[], ? super java.lang.Integer, kotlin.Unit> r9) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:18:0x003f in {7, 10, 15, 17} preds:[]
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
        r0 = "$receiver";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r7, r0);
        r0 = "action";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r9, r0);
        r0 = 512; // 0x200 float:7.175E-43 double:2.53E-321;
        r0 = kotlin.ranges.RangesKt___RangesKt.coerceAtLeast(r8, r0);
        r0 = new byte[r0];
        r1 = new java.io.FileInputStream;
        r1.<init>(r7);
        r1 = (java.io.Closeable) r1;
        r2 = 0;
        r2 = (java.lang.Throwable) r2;
        r3 = r1;	 Catch:{ Throwable -> 0x0039 }
        r3 = (java.io.FileInputStream) r3;	 Catch:{ Throwable -> 0x0039 }
        r4 = 0;	 Catch:{ Throwable -> 0x0039 }
        r5 = r3.read(r0);	 Catch:{ Throwable -> 0x0039 }
        if (r5 > 0) goto L_0x002e;	 Catch:{ Throwable -> 0x0039 }
        r3 = kotlin.Unit.INSTANCE;	 Catch:{ Throwable -> 0x0039 }
        kotlin.io.CloseableKt.closeFinally(r1, r2);
        return;
    L_0x002e:
        r6 = java.lang.Integer.valueOf(r5);	 Catch:{ Throwable -> 0x0039 }
        r9.invoke(r0, r6);	 Catch:{ Throwable -> 0x0039 }
        goto L_0x0020;
    L_0x0037:
        r3 = move-exception;
        goto L_0x003b;
    L_0x0039:
        r2 = move-exception;
        throw r2;	 Catch:{ all -> 0x0037 }
    L_0x003b:
        kotlin.io.CloseableKt.closeFinally(r1, r2);
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.FilesKt__FileReadWriteKt.forEachBlock(java.io.File, int, kotlin.jvm.functions.Function2):void");
    }

    @org.jetbrains.annotations.NotNull
    public static final byte[] readBytes(@org.jetbrains.annotations.NotNull java.io.File r13) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:26:0x0075 in {8, 9, 11, 13, 15, 18, 23, 25} preds:[]
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
        r0 = "$receiver";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r13, r0);
        r0 = new java.io.FileInputStream;
        r0.<init>(r13);
        r0 = (java.io.Closeable) r0;
        r1 = 0;
        r1 = (java.lang.Throwable) r1;
        r2 = r0;	 Catch:{ Throwable -> 0x006f }
        r2 = (java.io.FileInputStream) r2;	 Catch:{ Throwable -> 0x006f }
        r3 = 0;	 Catch:{ Throwable -> 0x006f }
        r4 = r3;	 Catch:{ Throwable -> 0x006f }
        r5 = 0;	 Catch:{ Throwable -> 0x006f }
        r6 = r13.length();	 Catch:{ Throwable -> 0x006f }
        r8 = r6;	 Catch:{ Throwable -> 0x006f }
        r10 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;	 Catch:{ Throwable -> 0x006f }
        r10 = (long) r10;	 Catch:{ Throwable -> 0x006f }
        r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));	 Catch:{ Throwable -> 0x006f }
        if (r12 > 0) goto L_0x0047;	 Catch:{ Throwable -> 0x006f }
        r3 = (int) r6;	 Catch:{ Throwable -> 0x006f }
        r6 = new byte[r3];	 Catch:{ Throwable -> 0x006f }
    L_0x002a:
        if (r3 <= 0) goto L_0x0036;	 Catch:{ Throwable -> 0x006f }
    L_0x002c:
        r7 = r2.read(r6, r5, r3);	 Catch:{ Throwable -> 0x006f }
        if (r7 >= 0) goto L_0x0033;	 Catch:{ Throwable -> 0x006f }
    L_0x0032:
        goto L_0x0036;	 Catch:{ Throwable -> 0x006f }
    L_0x0033:
        r3 = r3 - r7;	 Catch:{ Throwable -> 0x006f }
        r5 = r5 + r7;	 Catch:{ Throwable -> 0x006f }
        goto L_0x002a;	 Catch:{ Throwable -> 0x006f }
    L_0x0036:
        if (r3 != 0) goto L_0x0039;	 Catch:{ Throwable -> 0x006f }
    L_0x0038:
        goto L_0x0043;	 Catch:{ Throwable -> 0x006f }
    L_0x0039:
        r7 = java.util.Arrays.copyOf(r6, r5);	 Catch:{ Throwable -> 0x006f }
        r8 = "java.util.Arrays.copyOf(this, newSize)";	 Catch:{ Throwable -> 0x006f }
        kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r7, r8);	 Catch:{ Throwable -> 0x006f }
        r6 = r7;
    L_0x0043:
        kotlin.io.CloseableKt.closeFinally(r0, r1);
        return r6;
    L_0x0047:
        r6 = new java.lang.OutOfMemoryError;	 Catch:{ Throwable -> 0x006f }
        r7 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x006f }
        r7.<init>();	 Catch:{ Throwable -> 0x006f }
        r10 = "File ";	 Catch:{ Throwable -> 0x006f }
        r7.append(r10);	 Catch:{ Throwable -> 0x006f }
        r7.append(r13);	 Catch:{ Throwable -> 0x006f }
        r10 = " is too big (";	 Catch:{ Throwable -> 0x006f }
        r7.append(r10);	 Catch:{ Throwable -> 0x006f }
        r7.append(r8);	 Catch:{ Throwable -> 0x006f }
        r10 = " bytes) to fit in memory.";	 Catch:{ Throwable -> 0x006f }
        r7.append(r10);	 Catch:{ Throwable -> 0x006f }
        r7 = r7.toString();	 Catch:{ Throwable -> 0x006f }
        r6.<init>(r7);	 Catch:{ Throwable -> 0x006f }
        r6 = (java.lang.Throwable) r6;	 Catch:{ Throwable -> 0x006f }
        throw r6;	 Catch:{ Throwable -> 0x006f }
    L_0x006d:
        r2 = move-exception;
        goto L_0x0071;
    L_0x006f:
        r1 = move-exception;
        throw r1;	 Catch:{ all -> 0x006d }
    L_0x0071:
        kotlin.io.CloseableKt.closeFinally(r0, r1);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.FilesKt__FileReadWriteKt.readBytes(java.io.File):byte[]");
    }

    public static final <T> T useLines(@org.jetbrains.annotations.NotNull java.io.File r8, @org.jetbrains.annotations.NotNull java.nio.charset.Charset r9, @org.jetbrains.annotations.NotNull kotlin.jvm.functions.Function1<? super kotlin.sequences.Sequence<java.lang.String>, ? extends T> r10) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:30:0x0074 in {2, 3, 9, 10, 12, 17, 21, 23, 24, 26, 27, 29} preds:[]
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
        r0 = 0;
        r1 = "$receiver";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r8, r1);
        r1 = "charset";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r9, r1);
        r1 = "block";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r10, r1);
        r1 = new java.io.FileInputStream;
        r1.<init>(r8);
        r1 = (java.io.InputStream) r1;
        r2 = new java.io.InputStreamReader;
        r2.<init>(r1, r9);
        r2 = (java.io.Reader) r2;
        r1 = r2 instanceof java.io.BufferedReader;
        if (r1 == 0) goto L_0x0026;
    L_0x0022:
        r2 = (java.io.BufferedReader) r2;
        r1 = r2;
        goto L_0x002d;
    L_0x0026:
        r1 = new java.io.BufferedReader;
        r3 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
        r1.<init>(r2, r3);
    L_0x002d:
        r1 = (java.io.Closeable) r1;
        r2 = 0;
        r2 = (java.lang.Throwable) r2;
        r3 = 0;
        r4 = 1;
        r5 = r1;	 Catch:{ Throwable -> 0x0056 }
        r5 = (java.io.BufferedReader) r5;	 Catch:{ Throwable -> 0x0056 }
        r6 = r3;	 Catch:{ Throwable -> 0x0056 }
        r7 = kotlin.io.TextStreamsKt.lineSequence(r5);	 Catch:{ Throwable -> 0x0056 }
        r7 = r10.invoke(r7);	 Catch:{ Throwable -> 0x0056 }
        kotlin.jvm.internal.InlineMarker.finallyStart(r4);
        r3 = kotlin.internal.PlatformImplementationsKt.apiVersionIsAtLeast(r4, r4, r3);
        if (r3 == 0) goto L_0x004d;
    L_0x0049:
        kotlin.io.CloseableKt.closeFinally(r1, r2);
        goto L_0x0050;
    L_0x004d:
        r1.close();
    L_0x0050:
        kotlin.jvm.internal.InlineMarker.finallyEnd(r4);
        return r7;
    L_0x0054:
        r5 = move-exception;
        goto L_0x0058;
    L_0x0056:
        r2 = move-exception;
        throw r2;	 Catch:{ all -> 0x0054 }
    L_0x0058:
        kotlin.jvm.internal.InlineMarker.finallyStart(r4);
        r3 = kotlin.internal.PlatformImplementationsKt.apiVersionIsAtLeast(r4, r4, r3);
        if (r3 != 0) goto L_0x006d;
    L_0x0061:
        if (r2 != 0) goto L_0x0067;
    L_0x0063:
        r1.close();
        goto L_0x0070;
    L_0x0067:
        r1.close();	 Catch:{ Throwable -> 0x006b }
    L_0x006a:
        goto L_0x0070;
    L_0x006b:
        r1 = move-exception;
        goto L_0x006a;
    L_0x006d:
        kotlin.io.CloseableKt.closeFinally(r1, r2);
    L_0x0070:
        kotlin.jvm.internal.InlineMarker.finallyEnd(r4);
        throw r5;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.FilesKt__FileReadWriteKt.useLines(java.io.File, java.nio.charset.Charset, kotlin.jvm.functions.Function1):T");
    }

    public static /* bridge */ /* synthetic */ java.lang.Object useLines$default(java.io.File r6, java.nio.charset.Charset r7, kotlin.jvm.functions.Function1 r8, int r9, java.lang.Object r10) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:33:0x0079 in {2, 5, 6, 12, 13, 15, 20, 24, 26, 27, 29, 30, 32} preds:[]
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
        r10 = r9 & 1;
        if (r10 == 0) goto L_0x0006;
    L_0x0004:
        r7 = kotlin.text.Charsets.UTF_8;
    L_0x0006:
        r10 = "$receiver";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r6, r10);
        r10 = "charset";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r7, r10);
        r10 = "block";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r8, r10);
        r10 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
        r0 = new java.io.FileInputStream;
        r0.<init>(r6);
        r0 = (java.io.InputStream) r0;
        r1 = new java.io.InputStreamReader;
        r1.<init>(r0, r7);
        r1 = (java.io.Reader) r1;
        r0 = r1 instanceof java.io.BufferedReader;
        if (r0 == 0) goto L_0x002d;
    L_0x0029:
        r1 = (java.io.BufferedReader) r1;
        r0 = r1;
        goto L_0x0032;
    L_0x002d:
        r0 = new java.io.BufferedReader;
        r0.<init>(r1, r10);
    L_0x0032:
        r0 = (java.io.Closeable) r0;
        r10 = 0;
        r10 = (java.lang.Throwable) r10;
        r1 = 0;
        r2 = 1;
        r3 = r0;	 Catch:{ Throwable -> 0x005b }
        r3 = (java.io.BufferedReader) r3;	 Catch:{ Throwable -> 0x005b }
        r4 = r1;	 Catch:{ Throwable -> 0x005b }
        r5 = kotlin.io.TextStreamsKt.lineSequence(r3);	 Catch:{ Throwable -> 0x005b }
        r5 = r8.invoke(r5);	 Catch:{ Throwable -> 0x005b }
        kotlin.jvm.internal.InlineMarker.finallyStart(r2);
        r1 = kotlin.internal.PlatformImplementationsKt.apiVersionIsAtLeast(r2, r2, r1);
        if (r1 == 0) goto L_0x0052;
    L_0x004e:
        kotlin.io.CloseableKt.closeFinally(r0, r10);
        goto L_0x0055;
    L_0x0052:
        r0.close();
    L_0x0055:
        kotlin.jvm.internal.InlineMarker.finallyEnd(r2);
        return r5;
    L_0x0059:
        r3 = move-exception;
        goto L_0x005d;
    L_0x005b:
        r10 = move-exception;
        throw r10;	 Catch:{ all -> 0x0059 }
    L_0x005d:
        kotlin.jvm.internal.InlineMarker.finallyStart(r2);
        r1 = kotlin.internal.PlatformImplementationsKt.apiVersionIsAtLeast(r2, r2, r1);
        if (r1 != 0) goto L_0x0072;
    L_0x0066:
        if (r10 != 0) goto L_0x006c;
    L_0x0068:
        r0.close();
        goto L_0x0075;
    L_0x006c:
        r0.close();	 Catch:{ Throwable -> 0x0070 }
    L_0x006f:
        goto L_0x0075;
    L_0x0070:
        r10 = move-exception;
        goto L_0x006f;
    L_0x0072:
        kotlin.io.CloseableKt.closeFinally(r0, r10);
    L_0x0075:
        kotlin.jvm.internal.InlineMarker.finallyEnd(r2);
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.FilesKt__FileReadWriteKt.useLines$default(java.io.File, java.nio.charset.Charset, kotlin.jvm.functions.Function1, int, java.lang.Object):java.lang.Object");
    }

    @InlineOnly
    static /* bridge */ /* synthetic */ InputStreamReader reader$default(File $receiver, Charset charset, int i, Object obj) {
        if ((i & 1) != null) {
            charset = Charsets.UTF_8;
        }
        return new InputStreamReader((InputStream) new FileInputStream($receiver), charset);
    }

    @InlineOnly
    private static final InputStreamReader reader(@NotNull File $receiver, Charset charset) {
        return new InputStreamReader(new FileInputStream($receiver), charset);
    }

    @InlineOnly
    static /* bridge */ /* synthetic */ BufferedReader bufferedReader$default(File $receiver, Charset charset, int bufferSize, int i, Object obj) {
        if ((i & 1) != null) {
            charset = Charsets.UTF_8;
        }
        if ((i & 2) != null) {
            bufferSize = 8192;
        }
        Reader inputStreamReader = new InputStreamReader((InputStream) new FileInputStream($receiver), charset);
        return (inputStreamReader instanceof BufferedReader) != null ? (BufferedReader) inputStreamReader : new BufferedReader(inputStreamReader, bufferSize);
    }

    @InlineOnly
    private static final BufferedReader bufferedReader(@NotNull File $receiver, Charset charset, int bufferSize) {
        Reader inputStreamReader = new InputStreamReader(new FileInputStream($receiver), charset);
        return inputStreamReader instanceof BufferedReader ? (BufferedReader) inputStreamReader : new BufferedReader(inputStreamReader, bufferSize);
    }

    @InlineOnly
    static /* bridge */ /* synthetic */ OutputStreamWriter writer$default(File $receiver, Charset charset, int i, Object obj) {
        if ((i & 1) != null) {
            charset = Charsets.UTF_8;
        }
        return new OutputStreamWriter((OutputStream) new FileOutputStream($receiver), charset);
    }

    @InlineOnly
    private static final OutputStreamWriter writer(@NotNull File $receiver, Charset charset) {
        return new OutputStreamWriter(new FileOutputStream($receiver), charset);
    }

    @InlineOnly
    static /* bridge */ /* synthetic */ BufferedWriter bufferedWriter$default(File $receiver, Charset charset, int bufferSize, int i, Object obj) {
        if ((i & 1) != null) {
            charset = Charsets.UTF_8;
        }
        if ((i & 2) != null) {
            bufferSize = 8192;
        }
        Writer outputStreamWriter = new OutputStreamWriter((OutputStream) new FileOutputStream($receiver), charset);
        return (outputStreamWriter instanceof BufferedWriter) != null ? (BufferedWriter) outputStreamWriter : new BufferedWriter(outputStreamWriter, bufferSize);
    }

    @InlineOnly
    private static final BufferedWriter bufferedWriter(@NotNull File $receiver, Charset charset, int bufferSize) {
        Writer outputStreamWriter = new OutputStreamWriter(new FileOutputStream($receiver), charset);
        return outputStreamWriter instanceof BufferedWriter ? (BufferedWriter) outputStreamWriter : new BufferedWriter(outputStreamWriter, bufferSize);
    }

    @InlineOnly
    static /* bridge */ /* synthetic */ PrintWriter printWriter$default(File $receiver, Charset charset, int i, Object obj) {
        if ((i & 1) != null) {
            charset = Charsets.UTF_8;
        }
        Writer outputStreamWriter = new OutputStreamWriter(new FileOutputStream($receiver), charset);
        return new PrintWriter(outputStreamWriter instanceof BufferedWriter ? (BufferedWriter) outputStreamWriter : new BufferedWriter(outputStreamWriter, 8192));
    }

    @InlineOnly
    private static final PrintWriter printWriter(@NotNull File $receiver, Charset charset) {
        Writer outputStreamWriter = new OutputStreamWriter(new FileOutputStream($receiver), charset);
        return new PrintWriter(outputStreamWriter instanceof BufferedWriter ? (BufferedWriter) outputStreamWriter : new BufferedWriter(outputStreamWriter, 8192));
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final void writeBytes(@org.jetbrains.annotations.NotNull java.io.File r4, @org.jetbrains.annotations.NotNull byte[] r5) {
        /*
        r0 = "$receiver";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r4, r0);
        r0 = "array";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r5, r0);
        r0 = new java.io.FileOutputStream;
        r0.<init>(r4);
        r0 = (java.io.Closeable) r0;
        r1 = 0;
        r1 = (java.lang.Throwable) r1;
        r2 = r0;
        r2 = (java.io.FileOutputStream) r2;	 Catch:{ Throwable -> 0x0023 }
        r3 = 0;
        r2.write(r5);	 Catch:{ Throwable -> 0x0023 }
        r2 = kotlin.Unit.INSTANCE;	 Catch:{ Throwable -> 0x0023 }
        kotlin.io.CloseableKt.closeFinally(r0, r1);
        return;
    L_0x0021:
        r2 = move-exception;
        goto L_0x0025;
    L_0x0023:
        r1 = move-exception;
        throw r1;	 Catch:{ all -> 0x0021 }
    L_0x0025:
        kotlin.io.CloseableKt.closeFinally(r0, r1);
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.FilesKt__FileReadWriteKt.writeBytes(java.io.File, byte[]):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final void appendBytes(@org.jetbrains.annotations.NotNull java.io.File r4, @org.jetbrains.annotations.NotNull byte[] r5) {
        /*
        r0 = "$receiver";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r4, r0);
        r0 = "array";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r5, r0);
        r0 = new java.io.FileOutputStream;
        r1 = 1;
        r0.<init>(r4, r1);
        r0 = (java.io.Closeable) r0;
        r1 = 0;
        r1 = (java.lang.Throwable) r1;
        r2 = r0;
        r2 = (java.io.FileOutputStream) r2;	 Catch:{ Throwable -> 0x0024 }
        r3 = 0;
        r2.write(r5);	 Catch:{ Throwable -> 0x0024 }
        r2 = kotlin.Unit.INSTANCE;	 Catch:{ Throwable -> 0x0024 }
        kotlin.io.CloseableKt.closeFinally(r0, r1);
        return;
    L_0x0022:
        r2 = move-exception;
        goto L_0x0026;
    L_0x0024:
        r1 = move-exception;
        throw r1;	 Catch:{ all -> 0x0022 }
    L_0x0026:
        kotlin.io.CloseableKt.closeFinally(r0, r1);
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.FilesKt__FileReadWriteKt.appendBytes(java.io.File, byte[]):void");
    }

    @NotNull
    public static final String readText(@NotNull File $receiver, @NotNull Charset charset) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(charset, "charset");
        return new String(readBytes($receiver), charset);
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String readText$default(File file, Charset charset, int i, Object obj) {
        if ((i & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        return readText(file, charset);
    }

    public static final void writeText(@NotNull File $receiver, @NotNull String text, @NotNull Charset charset) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(text, "text");
        Intrinsics.checkParameterIsNotNull(charset, "charset");
        Object bytes = text.getBytes(charset);
        Intrinsics.checkExpressionValueIsNotNull(bytes, "(this as java.lang.String).getBytes(charset)");
        writeBytes($receiver, bytes);
    }

    public static /* bridge */ /* synthetic */ void writeText$default(File file, String str, Charset charset, int i, Object obj) {
        if ((i & 2) != 0) {
            charset = Charsets.UTF_8;
        }
        writeText(file, str, charset);
    }

    public static final void appendText(@NotNull File $receiver, @NotNull String text, @NotNull Charset charset) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(text, "text");
        Intrinsics.checkParameterIsNotNull(charset, "charset");
        Object bytes = text.getBytes(charset);
        Intrinsics.checkExpressionValueIsNotNull(bytes, "(this as java.lang.String).getBytes(charset)");
        appendBytes($receiver, bytes);
    }

    public static /* bridge */ /* synthetic */ void appendText$default(File file, String str, Charset charset, int i, Object obj) {
        if ((i & 2) != 0) {
            charset = Charsets.UTF_8;
        }
        appendText(file, str, charset);
    }

    public static final void forEachBlock(@NotNull File $receiver, @NotNull Function2<? super byte[], ? super Integer, Unit> action) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(action, "action");
        forEachBlock($receiver, 4096, action);
    }

    public static /* bridge */ /* synthetic */ void forEachLine$default(File file, Charset charset, Function1 function1, int i, Object obj) {
        if ((i & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        forEachLine(file, charset, function1);
    }

    public static final void forEachLine(@NotNull File $receiver, @NotNull Charset charset, @NotNull Function1<? super String, Unit> action) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(charset, "charset");
        Intrinsics.checkParameterIsNotNull(action, "action");
        TextStreamsKt.forEachLine(new BufferedReader(new InputStreamReader(new FileInputStream($receiver), charset)), action);
    }

    @InlineOnly
    private static final FileInputStream inputStream(@NotNull File $receiver) {
        return new FileInputStream($receiver);
    }

    @InlineOnly
    private static final FileOutputStream outputStream(@NotNull File $receiver) {
        return new FileOutputStream($receiver);
    }

    @NotNull
    public static /* bridge */ /* synthetic */ List readLines$default(File file, Charset charset, int i, Object obj) {
        if ((i & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        return readLines(file, charset);
    }

    @NotNull
    public static final List<String> readLines(@NotNull File $receiver, @NotNull Charset charset) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(charset, "charset");
        ArrayList result = new ArrayList();
        forEachLine($receiver, charset, new FilesKt__FileReadWriteKt$readLines$1(result));
        return result;
    }
}
