package okio;

import android.support.v4.media.session.PlaybackStateCompat;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import javax.annotation.Nullable;
import kotlin.text.Typography;

final class RealBufferedSource implements BufferedSource {
    public final Buffer buffer = new Buffer();
    boolean closed;
    public final Source source;

    public long indexOf(byte r14, long r15, long r17) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:26:0x006e in {11, 16, 17, 19, 21, 23, 25} preds:[]
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
        r0 = r13;
        r1 = r0.closed;
        if (r1 != 0) goto L_0x0065;
    L_0x0005:
        r1 = 0;
        r3 = (r15 > r1 ? 1 : (r15 == r1 ? 0 : -1));
        if (r3 < 0) goto L_0x0045;
    L_0x000b:
        r1 = (r17 > r15 ? 1 : (r17 == r15 ? 0 : -1));
        if (r1 < 0) goto L_0x0045;
    L_0x000f:
        r7 = r15;
    L_0x0010:
        r9 = -1;
        r1 = (r7 > r17 ? 1 : (r7 == r17 ? 0 : -1));
        if (r1 >= 0) goto L_0x0043;
    L_0x0016:
        r1 = r0.buffer;
        r2 = r14;
        r3 = r7;
        r5 = r17;
        r1 = r1.indexOf(r2, r3, r5);
        r3 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1));
        if (r3 == 0) goto L_0x0025;
    L_0x0024:
        return r1;
    L_0x0025:
        r3 = r0.buffer;
        r3 = r3.size;
        r5 = (r3 > r17 ? 1 : (r3 == r17 ? 0 : -1));
        if (r5 >= 0) goto L_0x0041;
    L_0x002d:
        r5 = r0.source;
        r6 = r0.buffer;
        r11 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
        r5 = r5.read(r6, r11);
        r11 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1));
        if (r11 != 0) goto L_0x003c;
    L_0x003b:
        goto L_0x0041;
    L_0x003c:
        r7 = java.lang.Math.max(r7, r3);
        goto L_0x0010;
        return r9;
        return r9;
        r1 = new java.lang.IllegalArgumentException;
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r3 = 0;
        r4 = java.lang.Long.valueOf(r15);
        r2[r3] = r4;
        r3 = 1;
        r4 = java.lang.Long.valueOf(r17);
        r2[r3] = r4;
        r3 = "fromIndex=%s toIndex=%s";
        r2 = java.lang.String.format(r3, r2);
        r1.<init>(r2);
        throw r1;
        r1 = new java.lang.IllegalStateException;
        r2 = "closed";
        r1.<init>(r2);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.RealBufferedSource.indexOf(byte, long, long):long");
    }

    public long indexOf(okio.ByteString r11, long r12) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:11:0x003b in {4, 7, 8, 10} preds:[]
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
        r0 = r10.closed;
        if (r0 != 0) goto L_0x0033;
    L_0x0004:
        r0 = r10.buffer;
        r0 = r0.indexOf(r11, r12);
        r2 = -1;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 == 0) goto L_0x0011;
    L_0x0010:
        return r0;
    L_0x0011:
        r4 = r10.buffer;
        r4 = r4.size;
        r6 = r10.source;
        r7 = r10.buffer;
        r8 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
        r6 = r6.read(r7, r8);
        r8 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1));
        if (r8 != 0) goto L_0x0024;
    L_0x0023:
        return r2;
    L_0x0024:
        r2 = r11.size();
        r2 = (long) r2;
        r2 = r4 - r2;
        r6 = 1;
        r2 = r2 + r6;
        r12 = java.lang.Math.max(r12, r2);
        goto L_0x0004;
    L_0x0033:
        r0 = new java.lang.IllegalStateException;
        r1 = "closed";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.RealBufferedSource.indexOf(okio.ByteString, long):long");
    }

    public long indexOfElement(okio.ByteString r11, long r12) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:11:0x0031 in {4, 7, 8, 10} preds:[]
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
        r0 = r10.closed;
        if (r0 != 0) goto L_0x0029;
    L_0x0004:
        r0 = r10.buffer;
        r0 = r0.indexOfElement(r11, r12);
        r2 = -1;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 == 0) goto L_0x0011;
    L_0x0010:
        return r0;
    L_0x0011:
        r4 = r10.buffer;
        r4 = r4.size;
        r6 = r10.source;
        r7 = r10.buffer;
        r8 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
        r6 = r6.read(r7, r8);
        r8 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1));
        if (r8 != 0) goto L_0x0024;
    L_0x0023:
        return r2;
    L_0x0024:
        r12 = java.lang.Math.max(r12, r4);
        goto L_0x0004;
    L_0x0029:
        r0 = new java.lang.IllegalStateException;
        r1 = "closed";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.RealBufferedSource.indexOfElement(okio.ByteString, long):long");
    }

    public boolean rangeEquals(long r8, okio.ByteString r10, int r11, int r12) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:24:0x0044 in {8, 13, 16, 17, 19, 20, 21, 23} preds:[]
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
        r7 = this;
        r0 = r7.closed;
        if (r0 != 0) goto L_0x003c;
    L_0x0004:
        r0 = 0;
        r2 = 0;
        r3 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1));
        if (r3 < 0) goto L_0x003a;
    L_0x000b:
        if (r11 < 0) goto L_0x003a;
    L_0x000d:
        if (r12 < 0) goto L_0x003a;
    L_0x000f:
        r0 = r10.size();
        r0 = r0 - r11;
        if (r0 >= r12) goto L_0x0017;
    L_0x0016:
        goto L_0x003b;
    L_0x0017:
        r0 = 0;
    L_0x0018:
        if (r0 >= r12) goto L_0x0038;
    L_0x001a:
        r3 = (long) r0;
        r3 = r3 + r8;
        r5 = 1;
        r5 = r5 + r3;
        r1 = r7.request(r5);
        if (r1 != 0) goto L_0x0026;
    L_0x0025:
        return r2;
    L_0x0026:
        r1 = r7.buffer;
        r1 = r1.getByte(r3);
        r5 = r11 + r0;
        r5 = r10.getByte(r5);
        if (r1 == r5) goto L_0x0035;
    L_0x0034:
        return r2;
    L_0x0035:
        r0 = r0 + 1;
        goto L_0x0018;
    L_0x0038:
        r0 = 1;
        return r0;
    L_0x003b:
        return r2;
    L_0x003c:
        r0 = new java.lang.IllegalStateException;
        r1 = "closed";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.RealBufferedSource.rangeEquals(long, okio.ByteString, int, int):boolean");
    }

    public long readAll(okio.Sink r10) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:0x004e in {6, 7, 8, 11, 12, 13, 15} preds:[]
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
        if (r10 == 0) goto L_0x0046;
    L_0x0002:
        r0 = 0;
    L_0x0004:
        r2 = r9.source;
        r3 = r9.buffer;
        r4 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
        r2 = r2.read(r3, r4);
        r4 = -1;
        r6 = 0;
        r8 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r8 == 0) goto L_0x0029;
    L_0x0016:
        r2 = r9.buffer;
        r2 = r2.completeSegmentByteCount();
        r4 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
        if (r4 <= 0) goto L_0x0027;
    L_0x0020:
        r0 = r0 + r2;
        r4 = r9.buffer;
        r10.write(r4, r2);
        goto L_0x0028;
    L_0x0028:
        goto L_0x0004;
    L_0x0029:
        r2 = r9.buffer;
        r2 = r2.size();
        r4 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
        if (r4 <= 0) goto L_0x0044;
    L_0x0033:
        r2 = r9.buffer;
        r2 = r2.size();
        r0 = r0 + r2;
        r2 = r9.buffer;
        r3 = r2.size();
        r10.write(r2, r3);
        goto L_0x0045;
    L_0x0045:
        return r0;
    L_0x0046:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "sink == null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.RealBufferedSource.readAll(okio.Sink):long");
    }

    public void readFully(byte[] r8) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:14:0x002d in {3, 10, 12, 13} preds:[]
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
        r7 = this;
        r0 = r8.length;	 Catch:{ EOFException -> 0x000c }
        r0 = (long) r0;	 Catch:{ EOFException -> 0x000c }
        r7.require(r0);	 Catch:{ EOFException -> 0x000c }
        r0 = r7.buffer;
        r0.readFully(r8);
        return;
    L_0x000c:
        r0 = move-exception;
        r1 = 0;
    L_0x000e:
        r2 = r7.buffer;
        r2 = r2.size;
        r4 = 0;
        r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r6 <= 0) goto L_0x002c;
    L_0x0018:
        r2 = r7.buffer;
        r3 = r2.size;
        r3 = (int) r3;
        r2 = r2.read(r8, r1, r3);
        r3 = -1;
        if (r2 == r3) goto L_0x0026;
    L_0x0024:
        r1 = r1 + r2;
        goto L_0x000e;
    L_0x0026:
        r3 = new java.lang.AssertionError;
        r3.<init>();
        throw r3;
    L_0x002c:
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.RealBufferedSource.readFully(byte[]):void");
    }

    public boolean request(long r6) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:0x0046 in {9, 10, 12, 14, 16} preds:[]
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
        r0 = 0;
        r2 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1));
        if (r2 < 0) goto L_0x002f;
    L_0x0006:
        r0 = r5.closed;
        if (r0 != 0) goto L_0x0027;
    L_0x000a:
        r0 = r5.buffer;
        r0 = r0.size;
        r2 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1));
        if (r2 >= 0) goto L_0x0025;
    L_0x0012:
        r0 = r5.source;
        r1 = r5.buffer;
        r2 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
        r0 = r0.read(r1, r2);
        r2 = -1;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 != 0) goto L_0x0024;
    L_0x0022:
        r0 = 0;
        return r0;
    L_0x0024:
        goto L_0x000a;
    L_0x0025:
        r0 = 1;
        return r0;
    L_0x0027:
        r0 = new java.lang.IllegalStateException;
        r1 = "closed";
        r0.<init>(r1);
        throw r0;
    L_0x002f:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "byteCount < 0: ";
        r1.append(r2);
        r1.append(r6);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.RealBufferedSource.request(long):boolean");
    }

    public int select(okio.Options r8) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x003b in {4, 9, 10, 12, 14} preds:[]
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
        r7 = this;
        r0 = r7.closed;
        if (r0 != 0) goto L_0x0033;
    L_0x0004:
        r0 = r7.buffer;
        r1 = 1;
        r0 = r0.selectPrefix(r8, r1);
        r1 = -1;
        if (r0 != r1) goto L_0x000f;
    L_0x000e:
        return r1;
    L_0x000f:
        r2 = -2;
        if (r0 != r2) goto L_0x0024;
    L_0x0012:
        r2 = r7.source;
        r3 = r7.buffer;
        r4 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
        r2 = r2.read(r3, r4);
        r4 = -1;
        r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r6 != 0) goto L_0x0023;
    L_0x0022:
        return r1;
    L_0x0023:
        goto L_0x0004;
    L_0x0024:
        r1 = r8.byteStrings;
        r1 = r1[r0];
        r1 = r1.size();
        r2 = r7.buffer;
        r3 = (long) r1;
        r2.skip(r3);
        return r0;
    L_0x0033:
        r0 = new java.lang.IllegalStateException;
        r1 = "closed";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.RealBufferedSource.select(okio.Options):int");
    }

    public void skip(long r6) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x0044 in {8, 10, 11, 12, 14} preds:[]
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
        r0 = r5.closed;
        if (r0 != 0) goto L_0x003c;
    L_0x0004:
        r0 = 0;
        r2 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1));
        if (r2 <= 0) goto L_0x003b;
    L_0x000a:
        r2 = r5.buffer;
        r2 = r2.size;
        r4 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1));
        if (r4 != 0) goto L_0x0029;
    L_0x0012:
        r0 = r5.source;
        r1 = r5.buffer;
        r2 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
        r0 = r0.read(r1, r2);
        r2 = -1;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 == 0) goto L_0x0023;
    L_0x0022:
        goto L_0x0029;
    L_0x0023:
        r0 = new java.io.EOFException;
        r0.<init>();
        throw r0;
        r0 = r5.buffer;
        r0 = r0.size();
        r0 = java.lang.Math.min(r6, r0);
        r2 = r5.buffer;
        r2.skip(r0);
        r6 = r6 - r0;
        goto L_0x0004;
    L_0x003b:
        return;
    L_0x003c:
        r0 = new java.lang.IllegalStateException;
        r1 = "closed";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.RealBufferedSource.skip(long):void");
    }

    RealBufferedSource(Source source) {
        if (source != null) {
            this.source = source;
            return;
        }
        throw new NullPointerException("source == null");
    }

    public Buffer buffer() {
        return this.buffer;
    }

    public long read(Buffer sink, long byteCount) throws IOException {
        if (sink == null) {
            throw new IllegalArgumentException("sink == null");
        } else if (byteCount < 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("byteCount < 0: ");
            stringBuilder.append(byteCount);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (this.closed) {
            throw new IllegalStateException("closed");
        } else {
            if (this.buffer.size == 0) {
                if (this.source.read(this.buffer, PlaybackStateCompat.ACTION_PLAY_FROM_URI) == -1) {
                    return -1;
                }
            }
            return this.buffer.read(sink, Math.min(byteCount, this.buffer.size));
        }
    }

    public boolean exhausted() throws IOException {
        if (!this.closed) {
            return this.buffer.exhausted() && this.source.read(this.buffer, PlaybackStateCompat.ACTION_PLAY_FROM_URI) == -1;
        } else {
            throw new IllegalStateException("closed");
        }
    }

    public void require(long byteCount) throws IOException {
        if (!request(byteCount)) {
            throw new EOFException();
        }
    }

    public byte readByte() throws IOException {
        require(1);
        return this.buffer.readByte();
    }

    public ByteString readByteString() throws IOException {
        this.buffer.writeAll(this.source);
        return this.buffer.readByteString();
    }

    public ByteString readByteString(long byteCount) throws IOException {
        require(byteCount);
        return this.buffer.readByteString(byteCount);
    }

    public byte[] readByteArray() throws IOException {
        this.buffer.writeAll(this.source);
        return this.buffer.readByteArray();
    }

    public byte[] readByteArray(long byteCount) throws IOException {
        require(byteCount);
        return this.buffer.readByteArray(byteCount);
    }

    public int read(byte[] sink) throws IOException {
        return read(sink, 0, sink.length);
    }

    public int read(byte[] sink, int offset, int byteCount) throws IOException {
        Util.checkOffsetAndCount((long) sink.length, (long) offset, (long) byteCount);
        if (this.buffer.size == 0) {
            if (this.source.read(this.buffer, PlaybackStateCompat.ACTION_PLAY_FROM_URI) == -1) {
                return -1;
            }
        }
        return this.buffer.read(sink, offset, (int) Math.min((long) byteCount, this.buffer.size));
    }

    public int read(ByteBuffer sink) throws IOException {
        if (this.buffer.size == 0) {
            if (this.source.read(this.buffer, PlaybackStateCompat.ACTION_PLAY_FROM_URI) == -1) {
                return -1;
            }
        }
        return this.buffer.read(sink);
    }

    public void readFully(Buffer sink, long byteCount) throws IOException {
        try {
            require(byteCount);
            this.buffer.readFully(sink, byteCount);
        } catch (EOFException e) {
            sink.writeAll(this.buffer);
            throw e;
        }
    }

    public String readUtf8() throws IOException {
        this.buffer.writeAll(this.source);
        return this.buffer.readUtf8();
    }

    public String readUtf8(long byteCount) throws IOException {
        require(byteCount);
        return this.buffer.readUtf8(byteCount);
    }

    public String readString(Charset charset) throws IOException {
        if (charset != null) {
            this.buffer.writeAll(this.source);
            return this.buffer.readString(charset);
        }
        throw new IllegalArgumentException("charset == null");
    }

    public String readString(long byteCount, Charset charset) throws IOException {
        require(byteCount);
        if (charset != null) {
            return this.buffer.readString(byteCount, charset);
        }
        throw new IllegalArgumentException("charset == null");
    }

    @Nullable
    public String readUtf8Line() throws IOException {
        long newline = indexOf((byte) 10);
        if (newline != -1) {
            return this.buffer.readUtf8Line(newline);
        }
        return this.buffer.size != 0 ? readUtf8(this.buffer.size) : null;
    }

    public String readUtf8LineStrict() throws IOException {
        return readUtf8LineStrict(Long.MAX_VALUE);
    }

    public String readUtf8LineStrict(long limit) throws IOException {
        RealBufferedSource realBufferedSource = this;
        long j = limit;
        if (j >= 0) {
            long scanLength = j == Long.MAX_VALUE ? Long.MAX_VALUE : j + 1;
            long newline = indexOf((byte) 10, 0, scanLength);
            if (newline != -1) {
                return realBufferedSource.buffer.readUtf8Line(newline);
            }
            if (scanLength < Long.MAX_VALUE) {
                if (request(scanLength) && realBufferedSource.buffer.getByte(scanLength - 1) == (byte) 13) {
                    if (request(1 + scanLength) && realBufferedSource.buffer.getByte(scanLength) == (byte) 10) {
                        return realBufferedSource.buffer.readUtf8Line(scanLength);
                    }
                }
            }
            Buffer data = new Buffer();
            Buffer buffer = realBufferedSource.buffer;
            buffer.copyTo(data, 0, Math.min(32, buffer.size()));
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\\n not found: limit=");
            stringBuilder.append(Math.min(realBufferedSource.buffer.size(), j));
            stringBuilder.append(" content=");
            stringBuilder.append(data.readByteString().hex());
            stringBuilder.append(Typography.ellipsis);
            throw new EOFException(stringBuilder.toString());
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("limit < 0: ");
        stringBuilder2.append(j);
        throw new IllegalArgumentException(stringBuilder2.toString());
    }

    public int readUtf8CodePoint() throws IOException {
        require(1);
        byte b0 = this.buffer.getByte(0);
        if ((b0 & 224) == PsExtractor.AUDIO_STREAM) {
            require(2);
        } else if ((b0 & PsExtractor.VIDEO_STREAM_MASK) == 224) {
            require(3);
        } else if ((b0 & 248) == PsExtractor.VIDEO_STREAM_MASK) {
            require(4);
        }
        return this.buffer.readUtf8CodePoint();
    }

    public short readShort() throws IOException {
        require(2);
        return this.buffer.readShort();
    }

    public short readShortLe() throws IOException {
        require(2);
        return this.buffer.readShortLe();
    }

    public int readInt() throws IOException {
        require(4);
        return this.buffer.readInt();
    }

    public int readIntLe() throws IOException {
        require(4);
        return this.buffer.readIntLe();
    }

    public long readLong() throws IOException {
        require(8);
        return this.buffer.readLong();
    }

    public long readLongLe() throws IOException {
        require(8);
        return this.buffer.readLongLe();
    }

    public long readDecimalLong() throws IOException {
        require(1);
        int pos = 0;
        while (request((long) (pos + 1))) {
            byte b = this.buffer.getByte((long) pos);
            if (b >= (byte) 48) {
                if (b > (byte) 57) {
                }
                pos++;
            }
            if (pos == 0) {
                if (b != (byte) 45) {
                }
                pos++;
            }
            if (pos != 0) {
                return this.buffer.readDecimalLong();
            }
            throw new NumberFormatException(String.format("Expected leading [0-9] or '-' character but was %#x", new Object[]{Byte.valueOf(b)}));
        }
        return this.buffer.readDecimalLong();
    }

    public long readHexadecimalUnsignedLong() throws IOException {
        require(1);
        for (int pos = 0; request((long) (pos + 1)); pos++) {
            byte b = this.buffer.getByte((long) pos);
            if (b >= (byte) 48) {
                if (b > (byte) 57) {
                }
            }
            if (b < (byte) 97 || b > (byte) 102) {
                if (b >= (byte) 65) {
                    if (b > (byte) 70) {
                    }
                }
                if (pos != 0) {
                    return this.buffer.readHexadecimalUnsignedLong();
                }
                throw new NumberFormatException(String.format("Expected leading [0-9a-fA-F] character but was %#x", new Object[]{Byte.valueOf(b)}));
            }
        }
        return this.buffer.readHexadecimalUnsignedLong();
    }

    public long indexOf(byte b) throws IOException {
        return indexOf(b, 0, Long.MAX_VALUE);
    }

    public long indexOf(byte b, long fromIndex) throws IOException {
        return indexOf(b, fromIndex, Long.MAX_VALUE);
    }

    public long indexOf(ByteString bytes) throws IOException {
        return indexOf(bytes, 0);
    }

    public long indexOfElement(ByteString targetBytes) throws IOException {
        return indexOfElement(targetBytes, 0);
    }

    public boolean rangeEquals(long offset, ByteString bytes) throws IOException {
        return rangeEquals(offset, bytes, 0, bytes.size());
    }

    public InputStream inputStream() {
        return new RealBufferedSource$1(this);
    }

    public boolean isOpen() {
        return this.closed ^ 1;
    }

    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            this.source.close();
            this.buffer.clear();
        }
    }

    public Timeout timeout() {
        return this.source.timeout();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("buffer(");
        stringBuilder.append(this.source);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
