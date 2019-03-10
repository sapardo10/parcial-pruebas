package okio;

import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import kotlin.text.Typography;
import org.apache.commons.text.RandomStringGenerator.Builder;

public final class Buffer implements BufferedSource, BufferedSink, Cloneable, ByteChannel {
    private static final byte[] DIGITS = new byte[]{(byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102};
    static final int REPLACEMENT_CHARACTER = 65533;
    @Nullable
    Segment head;
    long size;

    private okio.ByteString digest(java.lang.String r7) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:14:0x0046 in {6, 7, 8, 10, 13} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
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
        r6 = this;
        r0 = java.security.MessageDigest.getInstance(r7);	 Catch:{ NoSuchAlgorithmException -> 0x003f }
        r1 = r6.head;	 Catch:{ NoSuchAlgorithmException -> 0x003f }
        if (r1 == 0) goto L_0x0035;	 Catch:{ NoSuchAlgorithmException -> 0x003f }
    L_0x0008:
        r1 = r6.head;	 Catch:{ NoSuchAlgorithmException -> 0x003f }
        r1 = r1.data;	 Catch:{ NoSuchAlgorithmException -> 0x003f }
        r2 = r6.head;	 Catch:{ NoSuchAlgorithmException -> 0x003f }
        r2 = r2.pos;	 Catch:{ NoSuchAlgorithmException -> 0x003f }
        r3 = r6.head;	 Catch:{ NoSuchAlgorithmException -> 0x003f }
        r3 = r3.limit;	 Catch:{ NoSuchAlgorithmException -> 0x003f }
        r4 = r6.head;	 Catch:{ NoSuchAlgorithmException -> 0x003f }
        r4 = r4.pos;	 Catch:{ NoSuchAlgorithmException -> 0x003f }
        r3 = r3 - r4;	 Catch:{ NoSuchAlgorithmException -> 0x003f }
        r0.update(r1, r2, r3);	 Catch:{ NoSuchAlgorithmException -> 0x003f }
        r1 = r6.head;	 Catch:{ NoSuchAlgorithmException -> 0x003f }
        r1 = r1.next;	 Catch:{ NoSuchAlgorithmException -> 0x003f }
    L_0x0020:
        r2 = r6.head;	 Catch:{ NoSuchAlgorithmException -> 0x003f }
        if (r1 == r2) goto L_0x0034;	 Catch:{ NoSuchAlgorithmException -> 0x003f }
    L_0x0024:
        r2 = r1.data;	 Catch:{ NoSuchAlgorithmException -> 0x003f }
        r3 = r1.pos;	 Catch:{ NoSuchAlgorithmException -> 0x003f }
        r4 = r1.limit;	 Catch:{ NoSuchAlgorithmException -> 0x003f }
        r5 = r1.pos;	 Catch:{ NoSuchAlgorithmException -> 0x003f }
        r4 = r4 - r5;	 Catch:{ NoSuchAlgorithmException -> 0x003f }
        r0.update(r2, r3, r4);	 Catch:{ NoSuchAlgorithmException -> 0x003f }
        r2 = r1.next;	 Catch:{ NoSuchAlgorithmException -> 0x003f }
        r1 = r2;	 Catch:{ NoSuchAlgorithmException -> 0x003f }
        goto L_0x0020;	 Catch:{ NoSuchAlgorithmException -> 0x003f }
    L_0x0034:
        goto L_0x0036;	 Catch:{ NoSuchAlgorithmException -> 0x003f }
    L_0x0036:
        r1 = r0.digest();	 Catch:{ NoSuchAlgorithmException -> 0x003f }
        r1 = okio.ByteString.of(r1);	 Catch:{ NoSuchAlgorithmException -> 0x003f }
        return r1;
    L_0x003f:
        r0 = move-exception;
        r1 = new java.lang.AssertionError;
        r1.<init>();
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.digest(java.lang.String):okio.ByteString");
    }

    private okio.ByteString hmac(java.lang.String r7, okio.ByteString r8) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:0x0059 in {6, 7, 8, 10, 13, 16} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
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
        r6 = this;
        r0 = javax.crypto.Mac.getInstance(r7);	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        r1 = new javax.crypto.spec.SecretKeySpec;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        r2 = r8.toByteArray();	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        r1.<init>(r2, r7);	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        r0.init(r1);	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        r1 = r6.head;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        if (r1 == 0) goto L_0x0041;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
    L_0x0014:
        r1 = r6.head;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        r1 = r1.data;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        r2 = r6.head;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        r2 = r2.pos;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        r3 = r6.head;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        r3 = r3.limit;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        r4 = r6.head;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        r4 = r4.pos;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        r3 = r3 - r4;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        r0.update(r1, r2, r3);	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        r1 = r6.head;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        r1 = r1.next;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
    L_0x002c:
        r2 = r6.head;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        if (r1 == r2) goto L_0x0040;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
    L_0x0030:
        r2 = r1.data;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        r3 = r1.pos;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        r4 = r1.limit;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        r5 = r1.pos;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        r4 = r4 - r5;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        r0.update(r2, r3, r4);	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        r2 = r1.next;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        r1 = r2;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        goto L_0x002c;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
    L_0x0040:
        goto L_0x0042;	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
    L_0x0042:
        r1 = r0.doFinal();	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        r1 = okio.ByteString.of(r1);	 Catch:{ NoSuchAlgorithmException -> 0x0052, InvalidKeyException -> 0x004b }
        return r1;
    L_0x004b:
        r0 = move-exception;
        r1 = new java.lang.IllegalArgumentException;
        r1.<init>(r0);
        throw r1;
    L_0x0052:
        r0 = move-exception;
        r1 = new java.lang.AssertionError;
        r1.<init>();
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.hmac(java.lang.String, okio.ByteString):okio.ByteString");
    }

    private void readFrom(java.io.InputStream r8, long r9, boolean r11) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x0046 in {4, 5, 9, 11, 12, 14} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
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
        r7 = this;
        if (r8 == 0) goto L_0x003e;
    L_0x0002:
        r0 = 0;
        r2 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1));
        if (r2 > 0) goto L_0x000c;
    L_0x0008:
        if (r11 == 0) goto L_0x000b;
    L_0x000a:
        goto L_0x000c;
    L_0x000b:
        return;
        r0 = 1;
        r0 = r7.writableSegment(r0);
        r1 = r0.limit;
        r1 = 8192 - r1;
        r1 = (long) r1;
        r1 = java.lang.Math.min(r9, r1);
        r1 = (int) r1;
        r2 = r0.data;
        r3 = r0.limit;
        r2 = r8.read(r2, r3, r1);
        r3 = -1;
        if (r2 != r3) goto L_0x0030;
    L_0x0027:
        if (r11 == 0) goto L_0x002a;
    L_0x0029:
        return;
    L_0x002a:
        r3 = new java.io.EOFException;
        r3.<init>();
        throw r3;
    L_0x0030:
        r3 = r0.limit;
        r3 = r3 + r2;
        r0.limit = r3;
        r3 = r7.size;
        r5 = (long) r2;
        r3 = r3 + r5;
        r7.size = r3;
        r3 = (long) r2;
        r9 = r9 - r3;
        goto L_0x0002;
    L_0x003e:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "in == null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.readFrom(java.io.InputStream, long, boolean):void");
    }

    public final okio.Buffer copyTo(java.io.OutputStream r8, long r9, long r11) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:14:0x004d in {3, 7, 10, 11, 13} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
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
        r7 = this;
        if (r8 == 0) goto L_0x0045;
    L_0x0002:
        r0 = r7.size;
        r2 = r9;
        r4 = r11;
        okio.Util.checkOffsetAndCount(r0, r2, r4);
        r0 = 0;
        r2 = (r11 > r0 ? 1 : (r11 == r0 ? 0 : -1));
        if (r2 != 0) goto L_0x0010;
    L_0x000f:
        return r7;
    L_0x0010:
        r2 = r7.head;
    L_0x0012:
        r3 = r2.limit;
        r4 = r2.pos;
        r3 = r3 - r4;
        r3 = (long) r3;
        r5 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1));
        if (r5 < 0) goto L_0x0026;
    L_0x001c:
        r3 = r2.limit;
        r4 = r2.pos;
        r3 = r3 - r4;
        r3 = (long) r3;
        r9 = r9 - r3;
        r2 = r2.next;
        goto L_0x0012;
    L_0x0026:
        r3 = (r11 > r0 ? 1 : (r11 == r0 ? 0 : -1));
        if (r3 <= 0) goto L_0x0044;
    L_0x002a:
        r3 = r2.pos;
        r3 = (long) r3;
        r3 = r3 + r9;
        r3 = (int) r3;
        r4 = r2.limit;
        r4 = r4 - r3;
        r4 = (long) r4;
        r4 = java.lang.Math.min(r4, r11);
        r4 = (int) r4;
        r5 = r2.data;
        r8.write(r5, r3, r4);
        r5 = (long) r4;
        r11 = r11 - r5;
        r9 = 0;
        r2 = r2.next;
        goto L_0x0026;
    L_0x0044:
        return r7;
    L_0x0045:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "out == null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.copyTo(java.io.OutputStream, long, long):okio.Buffer");
    }

    public final okio.Buffer copyTo(okio.Buffer r7, long r8, long r10) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:18:0x006b in {3, 7, 12, 13, 14, 15, 17} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
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
        r6 = this;
        if (r7 == 0) goto L_0x0063;
    L_0x0002:
        r0 = r6.size;
        r2 = r8;
        r4 = r10;
        okio.Util.checkOffsetAndCount(r0, r2, r4);
        r0 = 0;
        r2 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1));
        if (r2 != 0) goto L_0x0010;
    L_0x000f:
        return r6;
    L_0x0010:
        r2 = r7.size;
        r2 = r2 + r10;
        r7.size = r2;
        r2 = r6.head;
    L_0x0017:
        r3 = r2.limit;
        r4 = r2.pos;
        r3 = r3 - r4;
        r3 = (long) r3;
        r5 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1));
        if (r5 < 0) goto L_0x002b;
    L_0x0021:
        r3 = r2.limit;
        r4 = r2.pos;
        r3 = r3 - r4;
        r3 = (long) r3;
        r8 = r8 - r3;
        r2 = r2.next;
        goto L_0x0017;
    L_0x002b:
        r3 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1));
        if (r3 <= 0) goto L_0x0062;
    L_0x002f:
        r3 = r2.sharedCopy();
        r4 = r3.pos;
        r4 = (long) r4;
        r4 = r4 + r8;
        r4 = (int) r4;
        r3.pos = r4;
        r4 = r3.pos;
        r5 = (int) r10;
        r4 = r4 + r5;
        r5 = r3.limit;
        r4 = java.lang.Math.min(r4, r5);
        r3.limit = r4;
        r4 = r7.head;
        if (r4 != 0) goto L_0x0051;
    L_0x004a:
        r3.prev = r3;
        r3.next = r3;
        r7.head = r3;
        goto L_0x0056;
    L_0x0051:
        r4 = r4.prev;
        r4.push(r3);
    L_0x0056:
        r4 = r3.limit;
        r5 = r3.pos;
        r4 = r4 - r5;
        r4 = (long) r4;
        r10 = r10 - r4;
        r8 = 0;
        r2 = r2.next;
        goto L_0x002b;
    L_0x0062:
        return r6;
    L_0x0063:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "out == null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.copyTo(okio.Buffer, long, long):okio.Buffer");
    }

    public long indexOf(byte r11, long r12, long r14) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:38:0x00a6 in {6, 9, 12, 18, 19, 23, 24, 32, 33, 34, 35, 37} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
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
        r10 = this;
        r0 = 0;
        r2 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1));
        if (r2 < 0) goto L_0x007f;
    L_0x0006:
        r0 = (r14 > r12 ? 1 : (r14 == r12 ? 0 : -1));
        if (r0 < 0) goto L_0x007f;
    L_0x000a:
        r0 = r10.size;
        r2 = (r14 > r0 ? 1 : (r14 == r0 ? 0 : -1));
        if (r2 <= 0) goto L_0x0013;
    L_0x0010:
        r14 = r10.size;
    L_0x0013:
        r0 = -1;
        r2 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1));
        if (r2 != 0) goto L_0x001a;
    L_0x0019:
        return r0;
    L_0x001a:
        r2 = r10.head;
        if (r2 != 0) goto L_0x001f;
    L_0x001e:
        return r0;
    L_0x001f:
        r3 = r10.size;
        r3 = r3 - r12;
        r5 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1));
        if (r5 >= 0) goto L_0x0037;
    L_0x0026:
        r3 = r10.size;
    L_0x0028:
        r5 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1));
        if (r5 <= 0) goto L_0x0036;
    L_0x002c:
        r2 = r2.prev;
        r5 = r2.limit;
        r6 = r2.pos;
        r5 = r5 - r6;
        r5 = (long) r5;
        r3 = r3 - r5;
        goto L_0x0028;
    L_0x0036:
        goto L_0x004a;
    L_0x0037:
        r3 = 0;
    L_0x0039:
        r5 = r2.limit;
        r6 = r2.pos;
        r5 = r5 - r6;
        r5 = (long) r5;
        r5 = r5 + r3;
        r7 = r5;
        r9 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1));
        if (r9 >= 0) goto L_0x0049;
    L_0x0045:
        r2 = r2.next;
        r3 = r7;
        goto L_0x0039;
    L_0x004a:
        r5 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1));
        if (r5 >= 0) goto L_0x007e;
    L_0x004e:
        r5 = r2.data;
        r6 = r2.limit;
        r6 = (long) r6;
        r8 = r2.pos;
        r8 = (long) r8;
        r8 = r8 + r14;
        r8 = r8 - r3;
        r6 = java.lang.Math.min(r6, r8);
        r6 = (int) r6;
        r7 = r2.pos;
        r7 = (long) r7;
        r7 = r7 + r12;
        r7 = r7 - r3;
        r7 = (int) r7;
    L_0x0063:
        if (r7 >= r6) goto L_0x0073;
    L_0x0065:
        r8 = r5[r7];
        if (r8 != r11) goto L_0x0070;
    L_0x0069:
        r0 = r2.pos;
        r0 = r7 - r0;
        r0 = (long) r0;
        r0 = r0 + r3;
        return r0;
    L_0x0070:
        r7 = r7 + 1;
        goto L_0x0063;
    L_0x0073:
        r8 = r2.limit;
        r9 = r2.pos;
        r8 = r8 - r9;
        r8 = (long) r8;
        r3 = r3 + r8;
        r12 = r3;
        r2 = r2.next;
        goto L_0x004a;
    L_0x007e:
        return r0;
        r0 = new java.lang.IllegalArgumentException;
        r1 = 3;
        r1 = new java.lang.Object[r1];
        r2 = 0;
        r3 = r10.size;
        r3 = java.lang.Long.valueOf(r3);
        r1[r2] = r3;
        r2 = java.lang.Long.valueOf(r12);
        r3 = 1;
        r1[r3] = r2;
        r2 = 2;
        r3 = java.lang.Long.valueOf(r14);
        r1[r2] = r3;
        r2 = "size=%s fromIndex=%s toIndex=%s";
        r1 = java.lang.String.format(r2, r1);
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.indexOf(byte, long, long):long");
    }

    public long indexOf(okio.ByteString r23, long r24) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:39:0x00d8 in {6, 12, 13, 17, 18, 29, 30, 31, 32, 34, 36, 38} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
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
        r22 = this;
        r6 = r22;
        r0 = r23.size();
        if (r0 == 0) goto L_0x00ce;
    L_0x0008:
        r0 = 0;
        r2 = (r24 > r0 ? 1 : (r24 == r0 ? 0 : -1));
        if (r2 < 0) goto L_0x00c4;
    L_0x000e:
        r0 = r6.head;
        r7 = -1;
        if (r0 != 0) goto L_0x0015;
    L_0x0014:
        return r7;
    L_0x0015:
        r1 = r6.size;
        r1 = r1 - r24;
        r3 = (r1 > r24 ? 1 : (r1 == r24 ? 0 : -1));
        if (r3 >= 0) goto L_0x002e;
    L_0x001d:
        r1 = r6.size;
    L_0x001f:
        r3 = (r1 > r24 ? 1 : (r1 == r24 ? 0 : -1));
        if (r3 <= 0) goto L_0x002d;
    L_0x0023:
        r0 = r0.prev;
        r3 = r0.limit;
        r4 = r0.pos;
        r3 = r3 - r4;
        r3 = (long) r3;
        r1 = r1 - r3;
        goto L_0x001f;
    L_0x002d:
        goto L_0x0041;
    L_0x002e:
        r1 = 0;
    L_0x0030:
        r3 = r0.limit;
        r4 = r0.pos;
        r3 = r3 - r4;
        r3 = (long) r3;
        r3 = r3 + r1;
        r9 = r3;
        r5 = (r3 > r24 ? 1 : (r3 == r24 ? 0 : -1));
        if (r5 >= 0) goto L_0x0040;
    L_0x003c:
        r0 = r0.next;
        r1 = r9;
        goto L_0x0030;
    L_0x0041:
        r3 = 0;
        r9 = r23;
        r10 = r9.getByte(r3);
        r11 = r23.size();
        r3 = r6.size;
        r12 = (long) r11;
        r3 = r3 - r12;
        r12 = 1;
        r12 = r12 + r3;
        r16 = r24;
        r5 = r0;
        r14 = r1;
    L_0x0057:
        r0 = (r14 > r12 ? 1 : (r14 == r12 ? 0 : -1));
        if (r0 >= 0) goto L_0x00c0;
    L_0x005b:
        r4 = r5.data;
        r0 = r5.limit;
        r0 = (long) r0;
        r2 = r5.pos;
        r2 = (long) r2;
        r2 = r2 + r12;
        r2 = r2 - r14;
        r0 = java.lang.Math.min(r0, r2);
        r3 = (int) r0;
        r0 = r5.pos;
        r0 = (long) r0;
        r0 = r0 + r16;
        r0 = r0 - r14;
        r0 = (int) r0;
        r2 = r0;
    L_0x0072:
        if (r2 >= r3) goto L_0x00ab;
    L_0x0074:
        r0 = r4[r2];
        if (r0 != r10) goto L_0x009a;
    L_0x0078:
        r18 = r2 + 1;
        r19 = 1;
        r0 = r22;
        r1 = r5;
        r20 = r2;
        r2 = r18;
        r18 = r3;
        r3 = r23;
        r21 = r4;
        r4 = r19;
        r7 = r5;
        r5 = r11;
        r0 = r0.rangeEquals(r1, r2, r3, r4, r5);
        if (r0 == 0) goto L_0x00a1;
    L_0x0093:
        r0 = r7.pos;
        r2 = r20 - r0;
        r0 = (long) r2;
        r0 = r0 + r14;
        return r0;
    L_0x009a:
        r20 = r2;
        r18 = r3;
        r21 = r4;
        r7 = r5;
    L_0x00a1:
        r2 = r20 + 1;
        r5 = r7;
        r3 = r18;
        r4 = r21;
        r7 = -1;
        goto L_0x0072;
    L_0x00ab:
        r20 = r2;
        r18 = r3;
        r21 = r4;
        r7 = r5;
        r0 = r7.limit;
        r1 = r7.pos;
        r0 = r0 - r1;
        r0 = (long) r0;
        r14 = r14 + r0;
        r16 = r14;
        r5 = r7.next;
        r7 = -1;
        goto L_0x0057;
    L_0x00c0:
        r7 = r5;
        r0 = -1;
        return r0;
    L_0x00c4:
        r9 = r23;
        r0 = new java.lang.IllegalArgumentException;
        r1 = "fromIndex < 0";
        r0.<init>(r1);
        throw r0;
    L_0x00ce:
        r9 = r23;
        r0 = new java.lang.IllegalArgumentException;
        r1 = "bytes is empty";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.indexOf(okio.ByteString, long):long");
    }

    public long indexOfElement(okio.ByteString r17, long r18) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:52:0x00db in {4, 10, 11, 15, 16, 27, 28, 30, 31, 32, 43, 44, 45, 46, 47, 49, 51} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
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
        r16 = this;
        r0 = r16;
        r1 = r17;
        r2 = 0;
        r4 = (r18 > r2 ? 1 : (r18 == r2 ? 0 : -1));
        if (r4 < 0) goto L_0x00d3;
    L_0x000a:
        r2 = r0.head;
        r3 = -1;
        if (r2 != 0) goto L_0x0011;
    L_0x0010:
        return r3;
    L_0x0011:
        r5 = r0.size;
        r5 = r5 - r18;
        r7 = (r5 > r18 ? 1 : (r5 == r18 ? 0 : -1));
        if (r7 >= 0) goto L_0x002a;
    L_0x0019:
        r5 = r0.size;
    L_0x001b:
        r7 = (r5 > r18 ? 1 : (r5 == r18 ? 0 : -1));
        if (r7 <= 0) goto L_0x0029;
    L_0x001f:
        r2 = r2.prev;
        r7 = r2.limit;
        r8 = r2.pos;
        r7 = r7 - r8;
        r7 = (long) r7;
        r5 = r5 - r7;
        goto L_0x001b;
    L_0x0029:
        goto L_0x003d;
    L_0x002a:
        r5 = 0;
    L_0x002c:
        r7 = r2.limit;
        r8 = r2.pos;
        r7 = r7 - r8;
        r7 = (long) r7;
        r7 = r7 + r5;
        r9 = r7;
        r11 = (r7 > r18 ? 1 : (r7 == r18 ? 0 : -1));
        if (r11 >= 0) goto L_0x003c;
    L_0x0038:
        r2 = r2.next;
        r5 = r9;
        goto L_0x002c;
    L_0x003d:
        r7 = r17.size();
        r8 = 2;
        r9 = 0;
        if (r7 != r8) goto L_0x0082;
    L_0x0045:
        r7 = r1.getByte(r9);
        r8 = 1;
        r8 = r1.getByte(r8);
        r9 = r18;
    L_0x0050:
        r11 = r0.size;
        r13 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1));
        if (r13 >= 0) goto L_0x0080;
    L_0x0056:
        r11 = r2.data;
        r12 = r2.pos;
        r12 = (long) r12;
        r12 = r12 + r9;
        r12 = r12 - r5;
        r12 = (int) r12;
        r13 = r2.limit;
    L_0x0060:
        if (r12 >= r13) goto L_0x0074;
    L_0x0062:
        r14 = r11[r12];
        if (r14 == r7) goto L_0x006c;
    L_0x0066:
        if (r14 != r8) goto L_0x0069;
    L_0x0068:
        goto L_0x006c;
    L_0x0069:
        r12 = r12 + 1;
        goto L_0x0060;
        r3 = r2.pos;
        r3 = r12 - r3;
        r3 = (long) r3;
        r3 = r3 + r5;
        return r3;
        r12 = r2.limit;
        r13 = r2.pos;
        r12 = r12 - r13;
        r12 = (long) r12;
        r5 = r5 + r12;
        r9 = r5;
        r2 = r2.next;
        goto L_0x0050;
        goto L_0x00d0;
    L_0x0082:
        r7 = r17.internalArray();
        r10 = r18;
    L_0x0088:
        r12 = r0.size;
        r8 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1));
        if (r8 >= 0) goto L_0x00cf;
    L_0x008e:
        r8 = r2.data;
        r12 = r2.pos;
        r12 = (long) r12;
        r12 = r12 + r10;
        r12 = r12 - r5;
        r12 = (int) r12;
        r13 = r2.limit;
    L_0x0098:
        if (r12 >= r13) goto L_0x00bd;
    L_0x009a:
        r14 = r8[r12];
        r15 = r7.length;
    L_0x009d:
        if (r9 >= r15) goto L_0x00b3;
    L_0x009f:
        r3 = r7[r9];
        if (r14 != r3) goto L_0x00aa;
    L_0x00a3:
        r4 = r2.pos;
        r4 = r12 - r4;
        r0 = (long) r4;
        r0 = r0 + r5;
        return r0;
    L_0x00aa:
        r9 = r9 + 1;
        r0 = r16;
        r1 = r17;
        r3 = -1;
        goto L_0x009d;
    L_0x00b3:
        r12 = r12 + 1;
        r0 = r16;
        r1 = r17;
        r3 = -1;
        r9 = 0;
        goto L_0x0098;
    L_0x00bd:
        r0 = r2.limit;
        r1 = r2.pos;
        r0 = r0 - r1;
        r0 = (long) r0;
        r5 = r5 + r0;
        r10 = r5;
        r2 = r2.next;
        r0 = r16;
        r1 = r17;
        r3 = -1;
        r9 = 0;
        goto L_0x0088;
    L_0x00cf:
        r9 = r10;
    L_0x00d0:
        r0 = -1;
        return r0;
    L_0x00d3:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "fromIndex < 0";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.indexOfElement(okio.ByteString, long):long");
    }

    public long readDecimalLong() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:50:0x00ef in {15, 16, 17, 18, 21, 23, 27, 28, 31, 33, 34, 36, 37, 41, 42, 45, 46, 47, 49} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
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
        r0 = r20;
        r1 = r0.size;
        r3 = 0;
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r5 == 0) goto L_0x00e7;
    L_0x000a:
        r1 = 0;
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r6 = -922337203685477580; // 0xf333333333333334 float:4.1723254E-8 double:-8.390303882365713E246;
        r8 = -7;
    L_0x0016:
        r10 = r0.head;
        r11 = r10.data;
        r12 = r10.pos;
        r13 = r10.limit;
    L_0x001e:
        if (r12 >= r13) goto L_0x00bc;
    L_0x0020:
        r14 = r11[r12];
        r15 = 48;
        if (r14 < r15) goto L_0x0081;
    L_0x0026:
        r15 = 57;
        if (r14 > r15) goto L_0x0081;
    L_0x002a:
        r15 = 48;
        r15 = r15 - r14;
        r16 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1));
        if (r16 < 0) goto L_0x004c;
    L_0x0031:
        r16 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1));
        if (r16 != 0) goto L_0x003e;
    L_0x0035:
        r16 = r6;
        r7 = r5;
        r5 = (long) r15;
        r18 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1));
        if (r18 >= 0) goto L_0x0041;
    L_0x003d:
        goto L_0x004f;
    L_0x003e:
        r16 = r6;
        r7 = r5;
    L_0x0041:
        r5 = 10;
        r1 = r1 * r5;
        r5 = (long) r15;
        r1 = r1 + r5;
        r18 = r7;
        r19 = r11;
        goto L_0x0091;
    L_0x004c:
        r16 = r6;
        r7 = r5;
    L_0x004f:
        r5 = new okio.Buffer;
        r5.<init>();
        r5 = r5.writeDecimalLong(r1);
        r5 = r5.writeByte(r14);
        if (r4 != 0) goto L_0x0062;
    L_0x005e:
        r5.readByte();
    L_0x0062:
        r6 = new java.lang.NumberFormatException;
        r18 = r7;
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r19 = r11;
        r11 = "Number too large: ";
        r7.append(r11);
        r11 = r5.readUtf8();
        r7.append(r11);
        r7 = r7.toString();
        r6.<init>(r7);
        throw r6;
    L_0x0081:
        r18 = r5;
        r16 = r6;
        r19 = r11;
        r5 = 45;
        if (r14 != r5) goto L_0x009c;
    L_0x008b:
        if (r3 != 0) goto L_0x009c;
    L_0x008d:
        r4 = 1;
        r5 = 1;
        r8 = r8 - r5;
    L_0x0091:
        r12 = r12 + 1;
        r3 = r3 + 1;
        r6 = r16;
        r5 = r18;
        r11 = r19;
        goto L_0x001e;
        if (r3 == 0) goto L_0x00a1;
    L_0x009f:
        r5 = 1;
        goto L_0x00c2;
    L_0x00a1:
        r5 = new java.lang.NumberFormatException;
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "Expected leading [0-9] or '-' character but was 0x";
        r6.append(r7);
        r7 = java.lang.Integer.toHexString(r14);
        r6.append(r7);
        r6 = r6.toString();
        r5.<init>(r6);
        throw r5;
    L_0x00bc:
        r18 = r5;
        r16 = r6;
        r19 = r11;
    L_0x00c2:
        if (r12 != r13) goto L_0x00ce;
    L_0x00c4:
        r6 = r10.pop();
        r0.head = r6;
        okio.SegmentPool.recycle(r10);
        goto L_0x00d0;
    L_0x00ce:
        r10.pos = r12;
    L_0x00d0:
        if (r5 != 0) goto L_0x00db;
    L_0x00d2:
        r6 = r0.head;
        if (r6 != 0) goto L_0x00d7;
    L_0x00d6:
        goto L_0x00db;
    L_0x00d7:
        r6 = r16;
        goto L_0x0016;
    L_0x00db:
        r6 = r0.size;
        r10 = (long) r3;
        r6 = r6 - r10;
        r0.size = r6;
        if (r4 == 0) goto L_0x00e5;
    L_0x00e3:
        r6 = r1;
        goto L_0x00e6;
    L_0x00e5:
        r6 = -r1;
    L_0x00e6:
        return r6;
    L_0x00e7:
        r1 = new java.lang.IllegalStateException;
        r2 = "size == 0";
        r1.<init>(r2);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.readDecimalLong():long");
    }

    public long readHexadecimalUnsignedLong() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:43:0x00bd in {9, 14, 19, 22, 24, 27, 29, 30, 32, 33, 37, 38, 40, 42} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
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
        r15 = this;
        r0 = r15.size;
        r2 = 0;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 == 0) goto L_0x00b5;
    L_0x0008:
        r0 = 0;
        r4 = 0;
        r5 = 0;
    L_0x000c:
        r6 = r15.head;
        r7 = r6.data;
        r8 = r6.pos;
        r9 = r6.limit;
    L_0x0014:
        if (r8 >= r9) goto L_0x0096;
    L_0x0016:
        r10 = r7[r8];
        r11 = 48;
        if (r10 < r11) goto L_0x0023;
    L_0x001c:
        r11 = 57;
        if (r10 > r11) goto L_0x0023;
    L_0x0020:
        r11 = r10 + -48;
        goto L_0x003e;
        r11 = 97;
        if (r10 < r11) goto L_0x0031;
    L_0x0028:
        r11 = 102; // 0x66 float:1.43E-43 double:5.04E-322;
        if (r10 > r11) goto L_0x0031;
    L_0x002c:
        r11 = r10 + -97;
        r11 = r11 + 10;
        goto L_0x003e;
        r11 = 65;
        if (r10 < r11) goto L_0x0076;
    L_0x0036:
        r11 = 70;
        if (r10 > r11) goto L_0x0076;
    L_0x003a:
        r11 = r10 + -65;
        r11 = r11 + 10;
    L_0x003e:
        r12 = -1152921504606846976; // 0xf000000000000000 float:0.0 double:-3.105036184601418E231;
        r12 = r12 & r0;
        r14 = (r12 > r2 ? 1 : (r12 == r2 ? 0 : -1));
        if (r14 != 0) goto L_0x004e;
    L_0x0045:
        r12 = 4;
        r0 = r0 << r12;
        r12 = (long) r11;
        r0 = r0 | r12;
        r8 = r8 + 1;
        r4 = r4 + 1;
        goto L_0x0014;
    L_0x004e:
        r2 = new okio.Buffer;
        r2.<init>();
        r2 = r2.writeHexadecimalUnsignedLong(r0);
        r2 = r2.writeByte(r10);
        r3 = new java.lang.NumberFormatException;
        r12 = new java.lang.StringBuilder;
        r12.<init>();
        r13 = "Number too large: ";
        r12.append(r13);
        r13 = r2.readUtf8();
        r12.append(r13);
        r12 = r12.toString();
        r3.<init>(r12);
        throw r3;
        if (r4 == 0) goto L_0x007b;
    L_0x0079:
        r5 = 1;
        goto L_0x0097;
    L_0x007b:
        r2 = new java.lang.NumberFormatException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r11 = "Expected leading [0-9a-fA-F] character but was 0x";
        r3.append(r11);
        r11 = java.lang.Integer.toHexString(r10);
        r3.append(r11);
        r3 = r3.toString();
        r2.<init>(r3);
        throw r2;
    L_0x0097:
        if (r8 != r9) goto L_0x00a3;
    L_0x0099:
        r10 = r6.pop();
        r15.head = r10;
        okio.SegmentPool.recycle(r6);
        goto L_0x00a5;
    L_0x00a3:
        r6.pos = r8;
    L_0x00a5:
        if (r5 != 0) goto L_0x00ae;
    L_0x00a7:
        r6 = r15.head;
        if (r6 != 0) goto L_0x00ac;
    L_0x00ab:
        goto L_0x00ae;
    L_0x00ac:
        goto L_0x000c;
    L_0x00ae:
        r2 = r15.size;
        r6 = (long) r4;
        r2 = r2 - r6;
        r15.size = r2;
        return r0;
    L_0x00b5:
        r0 = new java.lang.IllegalStateException;
        r1 = "size == 0";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.readHexadecimalUnsignedLong():long");
    }

    public int readUtf8CodePoint() throws java.io.EOFException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:41:0x00b6 in {4, 7, 10, 13, 20, 22, 25, 30, 33, 34, 36, 38, 40} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
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
        r10 = this;
        r0 = r10.size;
        r2 = 0;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 == 0) goto L_0x00b0;
    L_0x0008:
        r0 = r10.getByte(r2);
        r1 = r0 & 128;
        r2 = 65533; // 0xfffd float:9.1831E-41 double:3.23776E-319;
        if (r1 != 0) goto L_0x0018;
    L_0x0013:
        r1 = r0 & 127;
        r3 = 1;
        r4 = 0;
        goto L_0x003b;
    L_0x0018:
        r1 = r0 & 224;
        r3 = 192; // 0xc0 float:2.69E-43 double:9.5E-322;
        if (r1 != r3) goto L_0x0024;
    L_0x001e:
        r1 = r0 & 31;
        r3 = 2;
        r4 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        goto L_0x003b;
    L_0x0024:
        r1 = r0 & 240;
        r3 = 224; // 0xe0 float:3.14E-43 double:1.107E-321;
        if (r1 != r3) goto L_0x0030;
    L_0x002a:
        r1 = r0 & 15;
        r3 = 3;
        r4 = 2048; // 0x800 float:2.87E-42 double:1.0118E-320;
        goto L_0x003b;
    L_0x0030:
        r1 = r0 & 248;
        r3 = 240; // 0xf0 float:3.36E-43 double:1.186E-321;
        if (r1 != r3) goto L_0x00aa;
    L_0x0036:
        r1 = r0 & 7;
        r3 = 4;
        r4 = 65536; // 0x10000 float:9.18355E-41 double:3.2379E-319;
    L_0x003b:
        r5 = r10.size;
        r7 = (long) r3;
        r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r9 < 0) goto L_0x0078;
    L_0x0042:
        r5 = 1;
    L_0x0043:
        if (r5 >= r3) goto L_0x005d;
    L_0x0045:
        r6 = (long) r5;
        r6 = r10.getByte(r6);
        r7 = r6 & 192;
        r8 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        if (r7 != r8) goto L_0x0058;
    L_0x0050:
        r1 = r1 << 6;
        r7 = r6 & 63;
        r1 = r1 | r7;
        r5 = r5 + 1;
        goto L_0x0043;
    L_0x0058:
        r7 = (long) r5;
        r10.skip(r7);
        return r2;
        r5 = (long) r3;
        r10.skip(r5);
        r5 = 1114111; // 0x10ffff float:1.561202E-39 double:5.50444E-318;
        if (r1 <= r5) goto L_0x0068;
    L_0x0067:
        return r2;
    L_0x0068:
        r5 = 55296; // 0xd800 float:7.7486E-41 double:2.732E-319;
        if (r1 < r5) goto L_0x0073;
    L_0x006d:
        r5 = 57343; // 0xdfff float:8.0355E-41 double:2.8331E-319;
        if (r1 > r5) goto L_0x0073;
    L_0x0072:
        return r2;
        if (r1 >= r4) goto L_0x0077;
    L_0x0076:
        return r2;
    L_0x0077:
        return r1;
    L_0x0078:
        r2 = new java.io.EOFException;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "size < ";
        r5.append(r6);
        r5.append(r3);
        r6 = ": ";
        r5.append(r6);
        r6 = r10.size;
        r5.append(r6);
        r6 = " (to read code point prefixed 0x";
        r5.append(r6);
        r6 = java.lang.Integer.toHexString(r0);
        r5.append(r6);
        r6 = ")";
        r5.append(r6);
        r5 = r5.toString();
        r2.<init>(r5);
        throw r2;
    L_0x00aa:
        r3 = 1;
        r10.skip(r3);
        return r2;
    L_0x00b0:
        r0 = new java.io.EOFException;
        r0.<init>();
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.readUtf8CodePoint():int");
    }

    public int write(java.nio.ByteBuffer r7) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:8:0x0033 in {3, 5, 7} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
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
        r6 = this;
        if (r7 == 0) goto L_0x002b;
    L_0x0002:
        r0 = r7.remaining();
        r1 = r0;
    L_0x0007:
        if (r1 <= 0) goto L_0x0024;
    L_0x0009:
        r2 = 1;
        r2 = r6.writableSegment(r2);
        r3 = r2.limit;
        r3 = 8192 - r3;
        r3 = java.lang.Math.min(r1, r3);
        r4 = r2.data;
        r5 = r2.limit;
        r7.get(r4, r5, r3);
        r1 = r1 - r3;
        r4 = r2.limit;
        r4 = r4 + r3;
        r2.limit = r4;
        goto L_0x0007;
    L_0x0024:
        r2 = r6.size;
        r4 = (long) r0;
        r2 = r2 + r4;
        r6.size = r2;
        return r0;
    L_0x002b:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "source == null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.write(java.nio.ByteBuffer):int");
    }

    public okio.Buffer write(byte[] r8, int r9, int r10) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:8:0x0039 in {3, 5, 7} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
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
        r7 = this;
        if (r8 == 0) goto L_0x0031;
    L_0x0002:
        r0 = r8.length;
        r1 = (long) r0;
        r3 = (long) r9;
        r5 = (long) r10;
        okio.Util.checkOffsetAndCount(r1, r3, r5);
        r0 = r9 + r10;
    L_0x000b:
        if (r9 >= r0) goto L_0x002a;
    L_0x000d:
        r1 = 1;
        r1 = r7.writableSegment(r1);
        r2 = r0 - r9;
        r3 = r1.limit;
        r3 = 8192 - r3;
        r2 = java.lang.Math.min(r2, r3);
        r3 = r1.data;
        r4 = r1.limit;
        java.lang.System.arraycopy(r8, r9, r3, r4, r2);
        r9 = r9 + r2;
        r3 = r1.limit;
        r3 = r3 + r2;
        r1.limit = r3;
        goto L_0x000b;
    L_0x002a:
        r1 = r7.size;
        r3 = (long) r10;
        r1 = r1 + r3;
        r7.size = r1;
        return r7;
    L_0x0031:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "source == null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.write(byte[], int, int):okio.Buffer");
    }

    public void write(okio.Buffer r7, long r8) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:36:0x00a2 in {9, 10, 16, 17, 21, 22, 23, 24, 25, 28, 29, 30, 31, 33, 35} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
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
        r6 = this;
        if (r7 == 0) goto L_0x009a;
    L_0x0002:
        if (r7 == r6) goto L_0x0092;
    L_0x0004:
        r0 = r7.size;
        r2 = 0;
        r4 = r8;
        okio.Util.checkOffsetAndCount(r0, r2, r4);
    L_0x000c:
        r0 = 0;
        r2 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1));
        if (r2 <= 0) goto L_0x0091;
    L_0x0012:
        r0 = r7.head;
        r0 = r0.limit;
        r1 = r7.head;
        r1 = r1.pos;
        r0 = r0 - r1;
        r0 = (long) r0;
        r2 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1));
        if (r2 >= 0) goto L_0x005f;
    L_0x0020:
        r0 = r6.head;
        if (r0 == 0) goto L_0x0027;
    L_0x0024:
        r0 = r0.prev;
        goto L_0x0028;
    L_0x0027:
        r0 = 0;
    L_0x0028:
        if (r0 == 0) goto L_0x0054;
    L_0x002a:
        r1 = r0.owner;
        if (r1 == 0) goto L_0x0054;
    L_0x002e:
        r1 = r0.limit;
        r1 = (long) r1;
        r1 = r1 + r8;
        r3 = r0.shared;
        if (r3 == 0) goto L_0x0038;
    L_0x0036:
        r3 = 0;
        goto L_0x003a;
    L_0x0038:
        r3 = r0.pos;
    L_0x003a:
        r3 = (long) r3;
        r1 = r1 - r3;
        r3 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r5 > 0) goto L_0x0053;
    L_0x0042:
        r1 = r7.head;
        r2 = (int) r8;
        r1.writeTo(r0, r2);
        r1 = r7.size;
        r1 = r1 - r8;
        r7.size = r1;
        r1 = r6.size;
        r1 = r1 + r8;
        r6.size = r1;
        return;
    L_0x0053:
        goto L_0x0055;
    L_0x0055:
        r1 = r7.head;
        r2 = (int) r8;
        r1 = r1.split(r2);
        r7.head = r1;
        goto L_0x0060;
    L_0x0060:
        r0 = r7.head;
        r1 = r0.limit;
        r2 = r0.pos;
        r1 = r1 - r2;
        r1 = (long) r1;
        r3 = r0.pop();
        r7.head = r3;
        r3 = r6.head;
        if (r3 != 0) goto L_0x007b;
    L_0x0072:
        r6.head = r0;
        r3 = r6.head;
        r3.prev = r3;
        r3.next = r3;
        goto L_0x0084;
    L_0x007b:
        r3 = r3.prev;
        r3 = r3.push(r0);
        r3.compact();
    L_0x0084:
        r3 = r7.size;
        r3 = r3 - r1;
        r7.size = r3;
        r3 = r6.size;
        r3 = r3 + r1;
        r6.size = r3;
        r8 = r8 - r1;
        goto L_0x000c;
    L_0x0091:
        return;
    L_0x0092:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "source == this";
        r0.<init>(r1);
        throw r0;
    L_0x009a:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "source == null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.write(okio.Buffer, long):void");
    }

    public long writeAll(okio.Source r10) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:9:0x001d in {4, 6, 8} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
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
        r9 = this;
        if (r10 == 0) goto L_0x0015;
    L_0x0002:
        r0 = 0;
    L_0x0004:
        r2 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
        r2 = r10.read(r9, r2);
        r4 = r2;
        r6 = -1;
        r8 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
        if (r8 == 0) goto L_0x0013;
    L_0x0011:
        r0 = r0 + r4;
        goto L_0x0004;
        return r0;
    L_0x0015:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "source == null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.writeAll(okio.Source):long");
    }

    public final okio.Buffer writeTo(java.io.OutputStream r7, long r8) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:12:0x004e in {6, 7, 8, 9, 11} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
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
        r6 = this;
        if (r7 == 0) goto L_0x0046;
    L_0x0002:
        r0 = r6.size;
        r2 = 0;
        r4 = r8;
        okio.Util.checkOffsetAndCount(r0, r2, r4);
        r0 = r6.head;
    L_0x000c:
        r1 = 0;
        r3 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1));
        if (r3 <= 0) goto L_0x0045;
    L_0x0012:
        r1 = r0.limit;
        r2 = r0.pos;
        r1 = r1 - r2;
        r1 = (long) r1;
        r1 = java.lang.Math.min(r8, r1);
        r1 = (int) r1;
        r2 = r0.data;
        r3 = r0.pos;
        r7.write(r2, r3, r1);
        r2 = r0.pos;
        r2 = r2 + r1;
        r0.pos = r2;
        r2 = r6.size;
        r4 = (long) r1;
        r2 = r2 - r4;
        r6.size = r2;
        r2 = (long) r1;
        r8 = r8 - r2;
        r2 = r0.pos;
        r3 = r0.limit;
        if (r2 != r3) goto L_0x0043;
    L_0x0037:
        r2 = r0;
        r3 = r2.pop();
        r0 = r3;
        r6.head = r3;
        okio.SegmentPool.recycle(r2);
        goto L_0x0044;
    L_0x0044:
        goto L_0x000c;
    L_0x0045:
        return r6;
    L_0x0046:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "out == null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.writeTo(java.io.OutputStream, long):okio.Buffer");
    }

    public okio.Buffer writeUtf8(java.lang.String r13, int r14, int r15) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:49:0x013d in {13, 14, 15, 16, 19, 24, 27, 28, 34, 35, 36, 37, 38, 40, 42, 44, 46, 48} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
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
        r12 = this;
        if (r13 == 0) goto L_0x0135;
    L_0x0002:
        if (r14 < 0) goto L_0x011e;
    L_0x0004:
        if (r15 < r14) goto L_0x00ff;
    L_0x0006:
        r0 = r13.length();
        if (r15 > r0) goto L_0x00dc;
    L_0x000c:
        r0 = r14;
    L_0x000d:
        if (r0 >= r15) goto L_0x00da;
    L_0x000f:
        r1 = r13.charAt(r0);
        r2 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        if (r1 >= r2) goto L_0x0052;
    L_0x0017:
        r3 = 1;
        r3 = r12.writableSegment(r3);
        r4 = r3.data;
        r5 = r3.limit;
        r5 = r5 - r0;
        r6 = 8192 - r5;
        r6 = java.lang.Math.min(r15, r6);
        r7 = r0 + 1;
        r0 = r0 + r5;
        r8 = (byte) r1;
        r4[r0] = r8;
    L_0x002d:
        if (r7 >= r6) goto L_0x003e;
    L_0x002f:
        r1 = r13.charAt(r7);
        if (r1 < r2) goto L_0x0036;
    L_0x0035:
        goto L_0x003f;
    L_0x0036:
        r0 = r7 + 1;
        r7 = r7 + r5;
        r8 = (byte) r1;
        r4[r7] = r8;
        r7 = r0;
        goto L_0x002d;
    L_0x003f:
        r0 = r7 + r5;
        r2 = r3.limit;
        r0 = r0 - r2;
        r2 = r3.limit;
        r2 = r2 + r0;
        r3.limit = r2;
        r8 = r12.size;
        r10 = (long) r0;
        r8 = r8 + r10;
        r12.size = r8;
        r0 = r7;
        goto L_0x00d8;
    L_0x0052:
        r3 = 2048; // 0x800 float:2.87E-42 double:1.0118E-320;
        if (r1 >= r3) goto L_0x0067;
    L_0x0056:
        r3 = r1 >> 6;
        r3 = r3 | 192;
        r12.writeByte(r3);
        r3 = r1 & 63;
        r2 = r2 | r3;
        r12.writeByte(r2);
        r0 = r0 + 1;
        goto L_0x00d8;
    L_0x0067:
        r3 = 55296; // 0xd800 float:7.7486E-41 double:2.732E-319;
        r4 = 63;
        if (r1 < r3) goto L_0x00c1;
    L_0x006e:
        r3 = 57343; // 0xdfff float:8.0355E-41 double:2.8331E-319;
        if (r1 <= r3) goto L_0x0074;
    L_0x0073:
        goto L_0x00c1;
    L_0x0074:
        r5 = r0 + 1;
        if (r5 >= r15) goto L_0x007f;
    L_0x0078:
        r5 = r0 + 1;
        r5 = r13.charAt(r5);
        goto L_0x0080;
    L_0x007f:
        r5 = 0;
    L_0x0080:
        r6 = 56319; // 0xdbff float:7.892E-41 double:2.78253E-319;
        if (r1 > r6) goto L_0x00b9;
    L_0x0085:
        r6 = 56320; // 0xdc00 float:7.8921E-41 double:2.7826E-319;
        if (r5 < r6) goto L_0x00b9;
    L_0x008a:
        if (r5 <= r3) goto L_0x008d;
    L_0x008c:
        goto L_0x00b9;
    L_0x008d:
        r3 = 65536; // 0x10000 float:9.18355E-41 double:3.2379E-319;
        r6 = -55297; // 0xffffffffffff27ff float:NaN double:NaN;
        r6 = r6 & r1;
        r6 = r6 << 10;
        r7 = -56321; // 0xffffffffffff23ff float:NaN double:NaN;
        r7 = r7 & r5;
        r6 = r6 | r7;
        r6 = r6 + r3;
        r3 = r6 >> 18;
        r3 = r3 | 240;
        r12.writeByte(r3);
        r3 = r6 >> 12;
        r3 = r3 & r4;
        r3 = r3 | r2;
        r12.writeByte(r3);
        r3 = r6 >> 6;
        r3 = r3 & r4;
        r3 = r3 | r2;
        r12.writeByte(r3);
        r3 = r6 & 63;
        r2 = r2 | r3;
        r12.writeByte(r2);
        r0 = r0 + 2;
        goto L_0x00d8;
        r12.writeByte(r4);
        r0 = r0 + 1;
        goto L_0x000d;
        r3 = r1 >> 12;
        r3 = r3 | 224;
        r12.writeByte(r3);
        r3 = r1 >> 6;
        r3 = r3 & r4;
        r3 = r3 | r2;
        r12.writeByte(r3);
        r3 = r1 & 63;
        r2 = r2 | r3;
        r12.writeByte(r2);
        r0 = r0 + 1;
    L_0x00d8:
        goto L_0x000d;
        return r12;
    L_0x00dc:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "endIndex > string.length: ";
        r1.append(r2);
        r1.append(r15);
        r2 = " > ";
        r1.append(r2);
        r2 = r13.length();
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x00ff:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "endIndex < beginIndex: ";
        r1.append(r2);
        r1.append(r15);
        r2 = " < ";
        r1.append(r2);
        r1.append(r14);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x011e:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "beginIndex < 0: ";
        r1.append(r2);
        r1.append(r14);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x0135:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "string == null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.writeUtf8(java.lang.String, int, int):okio.Buffer");
    }

    public final long size() {
        return this.size;
    }

    public Buffer buffer() {
        return this;
    }

    public OutputStream outputStream() {
        return new Buffer$1(this);
    }

    public Buffer emitCompleteSegments() {
        return this;
    }

    public BufferedSink emit() {
        return this;
    }

    public boolean exhausted() {
        return this.size == 0;
    }

    public void require(long byteCount) throws EOFException {
        if (this.size < byteCount) {
            throw new EOFException();
        }
    }

    public boolean request(long byteCount) {
        return this.size >= byteCount;
    }

    public InputStream inputStream() {
        return new Buffer$2(this);
    }

    public final Buffer copyTo(OutputStream out) throws IOException {
        return copyTo(out, 0, this.size);
    }

    public final Buffer writeTo(OutputStream out) throws IOException {
        return writeTo(out, this.size);
    }

    public final Buffer readFrom(InputStream in) throws IOException {
        readFrom(in, Long.MAX_VALUE, true);
        return this;
    }

    public final Buffer readFrom(InputStream in, long byteCount) throws IOException {
        if (byteCount >= 0) {
            readFrom(in, byteCount, false);
            return this;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("byteCount < 0: ");
        stringBuilder.append(byteCount);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public final long completeSegmentByteCount() {
        long result = this.size;
        if (result == 0) {
            return 0;
        }
        Segment tail = this.head.prev;
        if (tail.limit < 8192 && tail.owner) {
            result -= (long) (tail.limit - tail.pos);
        }
        return result;
    }

    public byte readByte() {
        if (this.size != 0) {
            Segment segment = this.head;
            byte b = segment.pos;
            int limit = segment.limit;
            int pos = b + 1;
            b = segment.data[b];
            this.size--;
            if (pos == limit) {
                this.head = segment.pop();
                SegmentPool.recycle(segment);
            } else {
                segment.pos = pos;
            }
            return b;
        }
        throw new IllegalStateException("size == 0");
    }

    public final byte getByte(long pos) {
        Util.checkOffsetAndCount(this.size, pos, 1);
        Segment s = this.size;
        if (s - pos > pos) {
            s = this.head;
            while (true) {
                int segmentByteCount = s.limit - s.pos;
                if (pos < ((long) segmentByteCount)) {
                    return s.data[s.pos + ((int) pos)];
                }
                pos -= (long) segmentByteCount;
                s = s.next;
            }
        } else {
            pos -= s;
            s = this.head.prev;
            while (true) {
                pos += (long) (s.limit - s.pos);
                if (pos >= 0) {
                    return s.data[s.pos + ((int) pos)];
                }
                s = s.prev;
            }
        }
    }

    public short readShort() {
        if (this.size >= 2) {
            Segment segment = this.head;
            int pos = segment.pos;
            int limit = segment.limit;
            if (limit - pos < 2) {
                return (short) (((readByte() & 255) << 8) | (readByte() & 255));
            }
            byte[] data = segment.data;
            int pos2 = pos + 1;
            int pos3 = pos2 + 1;
            pos = ((data[pos] & 255) << 8) | (data[pos2] & 255);
            this.size -= 2;
            if (pos3 == limit) {
                this.head = segment.pop();
                SegmentPool.recycle(segment);
            } else {
                segment.pos = pos3;
            }
            return (short) pos;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("size < 2: ");
        stringBuilder.append(this.size);
        throw new IllegalStateException(stringBuilder.toString());
    }

    public int readInt() {
        if (this.size >= 4) {
            Segment segment = this.head;
            int pos = segment.pos;
            int limit = segment.limit;
            if (limit - pos < 4) {
                return ((((readByte() & 255) << 24) | ((readByte() & 255) << 16)) | ((readByte() & 255) << 8)) | (readByte() & 255);
            }
            byte[] data = segment.data;
            int pos2 = pos + 1;
            int pos3 = pos2 + 1;
            pos = ((data[pos] & 255) << 24) | ((data[pos2] & 255) << 16);
            pos2 = pos3 + 1;
            pos |= (data[pos3] & 255) << 8;
            pos3 = pos2 + 1;
            pos |= data[pos2] & 255;
            this.size -= 4;
            if (pos3 == limit) {
                this.head = segment.pop();
                SegmentPool.recycle(segment);
            } else {
                segment.pos = pos3;
            }
            return pos;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("size < 4: ");
        stringBuilder.append(this.size);
        throw new IllegalStateException(stringBuilder.toString());
    }

    public long readLong() {
        if (this.size >= 8) {
            Segment segment = this.head;
            int pos = segment.pos;
            int limit = segment.limit;
            if (limit - pos < 8) {
                return ((((long) readInt()) & 4294967295L) << 32) | (((long) readInt()) & 4294967295L);
            }
            byte[] data = segment.data;
            int pos2 = pos + 1;
            pos = pos2 + 1;
            pos2 = pos + 1;
            pos = pos2 + 1;
            int pos3 = pos + 1;
            pos = pos3 + 1;
            long j = ((((((((long) data[pos]) & 255) << 56) | ((((long) data[pos2]) & 255) << 48)) | ((((long) data[pos]) & 255) << 40)) | ((((long) data[pos2]) & 255) << 32)) | ((((long) data[pos]) & 255) << 24)) | ((((long) data[pos3]) & 255) << 16);
            pos3 = pos + 1;
            pos = pos3 + 1;
            long v = (((((long) data[pos]) & 255) << 8) | j) | (((long) data[pos3]) & 255);
            this.size -= 8;
            if (pos == limit) {
                this.head = segment.pop();
                SegmentPool.recycle(segment);
            } else {
                segment.pos = pos;
            }
            return v;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("size < 8: ");
        stringBuilder.append(this.size);
        throw new IllegalStateException(stringBuilder.toString());
    }

    public short readShortLe() {
        return Util.reverseBytesShort(readShort());
    }

    public int readIntLe() {
        return Util.reverseBytesInt(readInt());
    }

    public long readLongLe() {
        return Util.reverseBytesLong(readLong());
    }

    public ByteString readByteString() {
        return new ByteString(readByteArray());
    }

    public ByteString readByteString(long byteCount) throws EOFException {
        return new ByteString(readByteArray(byteCount));
    }

    public int select(Options options) {
        int index = selectPrefix(options, 0);
        if (index == -1) {
            return -1;
        }
        try {
            skip((long) options.byteStrings[index].size());
            return index;
        } catch (EOFException e) {
            throw new AssertionError();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    int selectPrefix(okio.Options r19, boolean r20) {
        /*
        r18 = this;
        r0 = r19;
        r1 = r18;
        r2 = r1.head;
        r3 = -2;
        if (r2 != 0) goto L_0x0013;
    L_0x0009:
        if (r20 == 0) goto L_0x000c;
    L_0x000b:
        return r3;
    L_0x000c:
        r3 = okio.ByteString.EMPTY;
        r3 = r0.indexOf(r3);
        return r3;
    L_0x0013:
        r4 = r2;
        r5 = r2.data;
        r6 = r2.pos;
        r7 = r2.limit;
        r8 = r0.trie;
        r9 = 0;
        r10 = -1;
    L_0x001e:
        r11 = r9 + 1;
        r9 = r8[r9];
        r12 = r11 + 1;
        r11 = r8[r11];
        r13 = -1;
        if (r11 == r13) goto L_0x002b;
    L_0x0029:
        r10 = r11;
        goto L_0x002c;
    L_0x002c:
        if (r4 != 0) goto L_0x0031;
    L_0x002e:
        r15 = r6;
        r3 = r12;
        goto L_0x0057;
    L_0x0031:
        if (r9 >= 0) goto L_0x0075;
    L_0x0033:
        r13 = r9 * -1;
        r14 = r12 + r13;
    L_0x0037:
        r15 = r6 + 1;
        r6 = r5[r6];
        r6 = r6 & 255;
        r3 = r12 + 1;
        r12 = r8[r12];
        if (r6 == r12) goto L_0x0044;
    L_0x0043:
        return r10;
    L_0x0044:
        if (r3 != r14) goto L_0x0048;
    L_0x0046:
        r12 = 1;
        goto L_0x0049;
    L_0x0048:
        r12 = 0;
    L_0x0049:
        if (r15 != r7) goto L_0x0064;
    L_0x004b:
        r4 = r4.next;
        r15 = r4.pos;
        r5 = r4.data;
        r7 = r4.limit;
        if (r4 != r2) goto L_0x0061;
    L_0x0055:
        if (r12 != 0) goto L_0x005d;
    L_0x0057:
        if (r20 == 0) goto L_0x005c;
    L_0x0059:
        r16 = -2;
        return r16;
    L_0x005c:
        return r10;
    L_0x005d:
        r16 = -2;
        r4 = 0;
        goto L_0x0066;
    L_0x0061:
        r16 = -2;
        goto L_0x0066;
    L_0x0064:
        r16 = -2;
    L_0x0066:
        if (r12 == 0) goto L_0x0070;
    L_0x0068:
        r17 = r8[r3];
        r12 = r3;
        r6 = r15;
        r3 = r17;
        goto L_0x00a4;
        r12 = r3;
        r6 = r15;
        r3 = -2;
        goto L_0x0037;
    L_0x0075:
        r16 = -2;
        r3 = r9;
        r13 = r6 + 1;
        r6 = r5[r6];
        r6 = r6 & 255;
        r14 = r12 + r3;
    L_0x0080:
        if (r12 != r14) goto L_0x0083;
    L_0x0082:
        return r10;
    L_0x0083:
        r15 = r8[r12];
        if (r6 != r15) goto L_0x00ab;
    L_0x0087:
        r15 = r12 + r3;
        r17 = r8[r15];
        if (r13 != r7) goto L_0x00a1;
    L_0x008e:
        r4 = r4.next;
        r13 = r4.pos;
        r5 = r4.data;
        r7 = r4.limit;
        if (r4 != r2) goto L_0x009d;
    L_0x0098:
        r4 = 0;
        r6 = r13;
        r3 = r17;
        goto L_0x00a4;
    L_0x009d:
        r6 = r13;
        r3 = r17;
        goto L_0x00a4;
    L_0x00a1:
        r6 = r13;
        r3 = r17;
    L_0x00a4:
        if (r3 < 0) goto L_0x00a7;
    L_0x00a6:
        return r3;
    L_0x00a7:
        r9 = -r3;
        r3 = -2;
        goto L_0x001e;
    L_0x00ab:
        r12 = r12 + 1;
        goto L_0x0080;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer.selectPrefix(okio.Options, boolean):int");
    }

    public void readFully(Buffer sink, long byteCount) throws EOFException {
        long j = this.size;
        if (j >= byteCount) {
            sink.write(this, byteCount);
        } else {
            sink.write(this, j);
            throw new EOFException();
        }
    }

    public long readAll(Sink sink) throws IOException {
        long byteCount = this.size;
        if (byteCount > 0) {
            sink.write(this, byteCount);
        }
        return byteCount;
    }

    public String readUtf8() {
        try {
            return readString(this.size, Util.UTF_8);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    public String readUtf8(long byteCount) throws EOFException {
        return readString(byteCount, Util.UTF_8);
    }

    public String readString(Charset charset) {
        try {
            return readString(this.size, charset);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    public String readString(long byteCount, Charset charset) throws EOFException {
        Util.checkOffsetAndCount(this.size, 0, byteCount);
        if (charset == null) {
            throw new IllegalArgumentException("charset == null");
        } else if (byteCount > 2147483647L) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("byteCount > Integer.MAX_VALUE: ");
            stringBuilder.append(byteCount);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (byteCount == 0) {
            return "";
        } else {
            Segment s = this.head;
            if (((long) s.pos) + byteCount > ((long) s.limit)) {
                return new String(readByteArray(byteCount), charset);
            }
            String result = new String(s.data, s.pos, (int) byteCount, charset);
            s.pos = (int) (((long) s.pos) + byteCount);
            this.size -= byteCount;
            if (s.pos == s.limit) {
                this.head = s.pop();
                SegmentPool.recycle(s);
            }
            return result;
        }
    }

    @Nullable
    public String readUtf8Line() throws EOFException {
        long newline = indexOf((byte) 10);
        if (newline != -1) {
            return readUtf8Line(newline);
        }
        long j = this.size;
        return j != 0 ? readUtf8(j) : null;
    }

    public String readUtf8LineStrict() throws EOFException {
        return readUtf8LineStrict(Long.MAX_VALUE);
    }

    public String readUtf8LineStrict(long limit) throws EOFException {
        if (limit >= 0) {
            long scanLength = Long.MAX_VALUE;
            if (limit != Long.MAX_VALUE) {
                scanLength = limit + 1;
            }
            long newline = indexOf((byte) 10, 0, scanLength);
            if (newline != -1) {
                return readUtf8Line(newline);
            }
            if (scanLength < size()) {
                if (getByte(scanLength - 1) == (byte) 13 && getByte(scanLength) == (byte) 10) {
                    return readUtf8Line(scanLength);
                }
            }
            Buffer data = new Buffer();
            copyTo(data, 0, Math.min(32, size()));
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\\n not found: limit=");
            stringBuilder.append(Math.min(size(), limit));
            stringBuilder.append(" content=");
            stringBuilder.append(data.readByteString().hex());
            stringBuilder.append(Typography.ellipsis);
            throw new EOFException(stringBuilder.toString());
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append("limit < 0: ");
        stringBuilder.append(limit);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    String readUtf8Line(long newline) throws EOFException {
        if (newline <= 0 || getByte(newline - 1) != (byte) 13) {
            String result = readUtf8(newline);
            skip(1);
            return result;
        }
        String result2 = readUtf8(newline - 1);
        skip(2);
        return result2;
    }

    public byte[] readByteArray() {
        try {
            return readByteArray(this.size);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    public byte[] readByteArray(long byteCount) throws EOFException {
        Util.checkOffsetAndCount(this.size, 0, byteCount);
        if (byteCount <= 2147483647L) {
            byte[] result = new byte[((int) byteCount)];
            readFully(result);
            return result;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("byteCount > Integer.MAX_VALUE: ");
        stringBuilder.append(byteCount);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public int read(byte[] sink) {
        return read(sink, 0, sink.length);
    }

    public void readFully(byte[] sink) throws EOFException {
        int offset = 0;
        while (offset < sink.length) {
            int read = read(sink, offset, sink.length - offset);
            if (read != -1) {
                offset += read;
            } else {
                throw new EOFException();
            }
        }
    }

    public int read(byte[] sink, int offset, int byteCount) {
        Util.checkOffsetAndCount((long) sink.length, (long) offset, (long) byteCount);
        Segment s = this.head;
        if (s == null) {
            return -1;
        }
        int toCopy = Math.min(byteCount, s.limit - s.pos);
        System.arraycopy(s.data, s.pos, sink, offset, toCopy);
        s.pos += toCopy;
        this.size -= (long) toCopy;
        if (s.pos == s.limit) {
            this.head = s.pop();
            SegmentPool.recycle(s);
        }
        return toCopy;
    }

    public int read(ByteBuffer sink) throws IOException {
        Segment s = this.head;
        if (s == null) {
            return -1;
        }
        int toCopy = Math.min(sink.remaining(), s.limit - s.pos);
        sink.put(s.data, s.pos, toCopy);
        s.pos += toCopy;
        this.size -= (long) toCopy;
        if (s.pos == s.limit) {
            this.head = s.pop();
            SegmentPool.recycle(s);
        }
        return toCopy;
    }

    public final void clear() {
        try {
            skip(this.size);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    public void skip(long byteCount) throws EOFException {
        while (byteCount > 0) {
            Segment segment = this.head;
            if (segment != null) {
                int toSkip = (int) Math.min(byteCount, (long) (segment.limit - this.head.pos));
                this.size -= (long) toSkip;
                byteCount -= (long) toSkip;
                Segment segment2 = this.head;
                segment2.pos += toSkip;
                if (this.head.pos == this.head.limit) {
                    segment2 = this.head;
                    this.head = segment2.pop();
                    SegmentPool.recycle(segment2);
                }
            } else {
                throw new EOFException();
            }
        }
    }

    public Buffer write(ByteString byteString) {
        if (byteString != null) {
            byteString.write(this);
            return this;
        }
        throw new IllegalArgumentException("byteString == null");
    }

    public Buffer writeUtf8(String string) {
        return writeUtf8(string, 0, string.length());
    }

    public Buffer writeUtf8CodePoint(int codePoint) {
        if (codePoint < 128) {
            writeByte(codePoint);
        } else if (codePoint < 2048) {
            writeByte((codePoint >> 6) | PsExtractor.AUDIO_STREAM);
            writeByte(128 | (codePoint & 63));
        } else if (codePoint < 65536) {
            if (codePoint < 55296 || codePoint > 57343) {
                writeByte((codePoint >> 12) | 224);
                writeByte(((codePoint >> 6) & 63) | 128);
                writeByte(128 | (codePoint & 63));
            } else {
                writeByte(63);
            }
        } else if (codePoint <= Builder.DEFAULT_MAXIMUM_CODE_POINT) {
            writeByte((codePoint >> 18) | PsExtractor.VIDEO_STREAM_MASK);
            writeByte(((codePoint >> 12) & 63) | 128);
            writeByte(((codePoint >> 6) & 63) | 128);
            writeByte(128 | (codePoint & 63));
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unexpected code point: ");
            stringBuilder.append(Integer.toHexString(codePoint));
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        return this;
    }

    public Buffer writeString(String string, Charset charset) {
        return writeString(string, 0, string.length(), charset);
    }

    public Buffer writeString(String string, int beginIndex, int endIndex, Charset charset) {
        if (string == null) {
            throw new IllegalArgumentException("string == null");
        } else if (beginIndex < 0) {
            r1 = new StringBuilder();
            r1.append("beginIndex < 0: ");
            r1.append(beginIndex);
            throw new IllegalAccessError(r1.toString());
        } else if (endIndex < beginIndex) {
            r1 = new StringBuilder();
            r1.append("endIndex < beginIndex: ");
            r1.append(endIndex);
            r1.append(" < ");
            r1.append(beginIndex);
            throw new IllegalArgumentException(r1.toString());
        } else if (endIndex > string.length()) {
            r1 = new StringBuilder();
            r1.append("endIndex > string.length: ");
            r1.append(endIndex);
            r1.append(" > ");
            r1.append(string.length());
            throw new IllegalArgumentException(r1.toString());
        } else if (charset == null) {
            throw new IllegalArgumentException("charset == null");
        } else if (charset.equals(Util.UTF_8)) {
            return writeUtf8(string, beginIndex, endIndex);
        } else {
            byte[] data = string.substring(beginIndex, endIndex).getBytes(charset);
            return write(data, 0, data.length);
        }
    }

    public Buffer write(byte[] source) {
        if (source != null) {
            return write(source, 0, source.length);
        }
        throw new IllegalArgumentException("source == null");
    }

    public BufferedSink write(Source source, long byteCount) throws IOException {
        while (byteCount > 0) {
            long read = source.read(this, byteCount);
            if (read != -1) {
                byteCount -= read;
            } else {
                throw new EOFException();
            }
        }
        return this;
    }

    public Buffer writeByte(int b) {
        Segment tail = writableSegment(1);
        byte[] bArr = tail.data;
        int i = tail.limit;
        tail.limit = i + 1;
        bArr[i] = (byte) b;
        this.size++;
        return this;
    }

    public Buffer writeShort(int s) {
        Segment tail = writableSegment(2);
        byte[] data = tail.data;
        int i = tail.limit;
        int i2 = i + 1;
        data[i] = (byte) ((s >>> 8) & 255);
        i = i2 + 1;
        data[i2] = (byte) (s & 255);
        tail.limit = i;
        this.size += 2;
        return this;
    }

    public Buffer writeShortLe(int s) {
        return writeShort(Util.reverseBytesShort((short) s));
    }

    public Buffer writeInt(int i) {
        Segment tail = writableSegment(4);
        byte[] data = tail.data;
        int i2 = tail.limit;
        int i3 = i2 + 1;
        data[i2] = (byte) ((i >>> 24) & 255);
        i2 = i3 + 1;
        data[i3] = (byte) ((i >>> 16) & 255);
        i3 = i2 + 1;
        data[i2] = (byte) ((i >>> 8) & 255);
        i2 = i3 + 1;
        data[i3] = (byte) (i & 255);
        tail.limit = i2;
        this.size += 4;
        return this;
    }

    public Buffer writeIntLe(int i) {
        return writeInt(Util.reverseBytesInt(i));
    }

    public Buffer writeLong(long v) {
        Segment tail = writableSegment(8);
        byte[] data = tail.data;
        int i = tail.limit;
        int i2 = i + 1;
        data[i] = (byte) ((int) ((v >>> 56) & 255));
        i = i2 + 1;
        data[i2] = (byte) ((int) ((v >>> 48) & 255));
        i2 = i + 1;
        data[i] = (byte) ((int) ((v >>> 40) & 255));
        i = i2 + 1;
        data[i2] = (byte) ((int) ((v >>> 32) & 255));
        i2 = i + 1;
        data[i] = (byte) ((int) ((v >>> 24) & 255));
        i = i2 + 1;
        data[i2] = (byte) ((int) ((v >>> 16) & 255));
        i2 = i + 1;
        data[i] = (byte) ((int) ((v >>> 8) & 255));
        int limit = i2 + 1;
        data[i2] = (byte) ((int) (v & 255));
        tail.limit = limit;
        this.size += 8;
        return this;
    }

    public Buffer writeLongLe(long v) {
        return writeLong(Util.reverseBytesLong(v));
    }

    public Buffer writeDecimalLong(long v) {
        if (v == 0) {
            return writeByte(48);
        }
        boolean negative = false;
        if (v < 0) {
            v = -v;
            if (v < 0) {
                return writeUtf8("-9223372036854775808");
            }
            negative = true;
        }
        int width = v < 100000000 ? v < 10000 ? v < 100 ? v < 10 ? 1 : 2 : v < 1000 ? 3 : 4 : v < 1000000 ? v < 100000 ? 5 : 6 : v < 10000000 ? 7 : 8 : v < 1000000000000L ? v < 10000000000L ? v < C0555C.NANOS_PER_SECOND ? 9 : 10 : v < 100000000000L ? 11 : 12 : v < 1000000000000000L ? v < 10000000000000L ? 13 : v < 100000000000000L ? 14 : 15 : v < 100000000000000000L ? v < 10000000000000000L ? 16 : 17 : v < 1000000000000000000L ? 18 : 19;
        if (negative) {
            width++;
        }
        Segment tail = writableSegment(width);
        byte[] data = tail.data;
        int pos = tail.limit + width;
        while (v != 0) {
            pos--;
            data[pos] = DIGITS[(int) (v % 10)];
            v /= 10;
        }
        if (negative) {
            data[pos - 1] = (byte) 45;
        }
        tail.limit += width;
        this.size += (long) width;
        return this;
    }

    public Buffer writeHexadecimalUnsignedLong(long v) {
        if (v == 0) {
            return writeByte(48);
        }
        int width = (Long.numberOfTrailingZeros(Long.highestOneBit(v)) / 4) + 1;
        Segment tail = writableSegment(width);
        byte[] data = tail.data;
        int start = tail.limit;
        for (int pos = (tail.limit + width) - 1; pos >= start; pos--) {
            data[pos] = DIGITS[(int) (15 & v)];
            v >>>= 4;
        }
        tail.limit += width;
        this.size += (long) width;
        return this;
    }

    Segment writableSegment(int minimumCapacity) {
        if (minimumCapacity < 1 || minimumCapacity > 8192) {
            throw new IllegalArgumentException();
        }
        Segment tail = this.head;
        if (tail == null) {
            this.head = SegmentPool.take();
            Segment segment = this.head;
            segment.prev = segment;
            segment.next = segment;
            return segment;
        }
        tail = tail.prev;
        if (tail.limit + minimumCapacity <= 8192) {
            if (tail.owner) {
                return tail;
            }
        }
        tail = tail.push(SegmentPool.take());
        return tail;
    }

    public long read(Buffer sink, long byteCount) {
        if (sink == null) {
            throw new IllegalArgumentException("sink == null");
        } else if (byteCount >= 0) {
            long j = this.size;
            if (j == 0) {
                return -1;
            }
            if (byteCount > j) {
                byteCount = this.size;
            }
            sink.write(this, byteCount);
            return byteCount;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("byteCount < 0: ");
            stringBuilder.append(byteCount);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public long indexOf(byte b) {
        return indexOf(b, 0, Long.MAX_VALUE);
    }

    public long indexOf(byte b, long fromIndex) {
        return indexOf(b, fromIndex, Long.MAX_VALUE);
    }

    public long indexOf(ByteString bytes) throws IOException {
        return indexOf(bytes, 0);
    }

    public long indexOfElement(ByteString targetBytes) {
        return indexOfElement(targetBytes, 0);
    }

    public boolean rangeEquals(long offset, ByteString bytes) {
        return rangeEquals(offset, bytes, 0, bytes.size());
    }

    public boolean rangeEquals(long offset, ByteString bytes, int bytesOffset, int byteCount) {
        if (offset >= 0 && bytesOffset >= 0 && byteCount >= 0 && this.size - offset >= ((long) byteCount)) {
            if (bytes.size() - bytesOffset >= byteCount) {
                for (int i = 0; i < byteCount; i++) {
                    if (getByte(((long) i) + offset) != bytes.getByte(bytesOffset + i)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private boolean rangeEquals(Segment segment, int segmentPos, ByteString bytes, int bytesOffset, int bytesLimit) {
        int segmentLimit = segment.limit;
        byte[] data = segment.data;
        for (int i = bytesOffset; i < bytesLimit; i++) {
            if (segmentPos == segmentLimit) {
                segment = segment.next;
                data = segment.data;
                segmentPos = segment.pos;
                segmentLimit = segment.limit;
            }
            if (data[segmentPos] != bytes.getByte(i)) {
                return false;
            }
            segmentPos++;
        }
        return true;
    }

    public void flush() {
    }

    public boolean isOpen() {
        return true;
    }

    public void close() {
    }

    public Timeout timeout() {
        return Timeout.NONE;
    }

    List<Integer> segmentSizes() {
        if (this.head == null) {
            return Collections.emptyList();
        }
        List<Integer> result = new ArrayList();
        result.add(Integer.valueOf(this.head.limit - this.head.pos));
        for (Segment s = this.head.next; s != this.head; s = s.next) {
            result.add(Integer.valueOf(s.limit - s.pos));
        }
        return result;
    }

    public final ByteString md5() {
        return digest("MD5");
    }

    public final ByteString sha1() {
        return digest("SHA-1");
    }

    public final ByteString sha256() {
        return digest("SHA-256");
    }

    public final ByteString sha512() {
        return digest("SHA-512");
    }

    public final ByteString hmacSha1(ByteString key) {
        return hmac("HmacSHA1", key);
    }

    public final ByteString hmacSha256(ByteString key) {
        return hmac("HmacSHA256", key);
    }

    public final ByteString hmacSha512(ByteString key) {
        return hmac("HmacSHA512", key);
    }

    public boolean equals(Object o) {
        Buffer buffer = o;
        if (this == buffer) {
            return true;
        }
        if (!(buffer instanceof Buffer)) {
            return false;
        }
        Buffer that = buffer;
        long j = r0.size;
        if (j != that.size) {
            return false;
        }
        if (j == 0) {
            return true;
        }
        Segment sa = r0.head;
        Segment sb = that.head;
        int posA = sa.pos;
        int posB = sb.pos;
        long pos = 0;
        while (pos < r0.size) {
            long count = (long) Math.min(sa.limit - posA, sb.limit - posB);
            int i = 0;
            while (((long) i) < count) {
                int posA2 = posA + 1;
                int posB2 = posB + 1;
                if (sa.data[posA] != sb.data[posB]) {
                    return false;
                }
                i++;
                posA = posA2;
                posB = posB2;
            }
            if (posA == sa.limit) {
                sa = sa.next;
                posA = sa.pos;
            }
            if (posB == sb.limit) {
                sb = sb.next;
                posB = sb.pos;
            }
            pos += count;
        }
        return true;
    }

    public int hashCode() {
        Segment s = this.head;
        if (s == null) {
            return 0;
        }
        int result = 1;
        while (true) {
            for (int pos = s.pos; pos < s.limit; pos++) {
                result = (result * 31) + s.data[pos];
            }
            s = s.next;
            if (s == this.head) {
                return result;
            }
        }
    }

    public String toString() {
        return snapshot().toString();
    }

    public Buffer clone() {
        Buffer result = new Buffer();
        if (this.size == 0) {
            return result;
        }
        result.head = this.head.sharedCopy();
        Segment segment = result.head;
        segment.prev = segment;
        segment.next = segment;
        for (segment = this.head.next; segment != this.head; segment = segment.next) {
            result.head.prev.push(segment.sharedCopy());
        }
        result.size = this.size;
        return result;
    }

    public final ByteString snapshot() {
        long j = this.size;
        if (j <= 2147483647L) {
            return snapshot((int) j);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("size > Integer.MAX_VALUE: ");
        stringBuilder.append(this.size);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public final ByteString snapshot(int byteCount) {
        if (byteCount == 0) {
            return ByteString.EMPTY;
        }
        return new SegmentedByteString(this, byteCount);
    }

    public final Buffer$UnsafeCursor readUnsafe() {
        return readUnsafe(new Buffer$UnsafeCursor());
    }

    public final Buffer$UnsafeCursor readUnsafe(Buffer$UnsafeCursor unsafeCursor) {
        if (unsafeCursor.buffer == null) {
            unsafeCursor.buffer = this;
            unsafeCursor.readWrite = false;
            return unsafeCursor;
        }
        throw new IllegalStateException("already attached to a buffer");
    }

    public final Buffer$UnsafeCursor readAndWriteUnsafe() {
        return readAndWriteUnsafe(new Buffer$UnsafeCursor());
    }

    public final Buffer$UnsafeCursor readAndWriteUnsafe(Buffer$UnsafeCursor unsafeCursor) {
        if (unsafeCursor.buffer == null) {
            unsafeCursor.buffer = this;
            unsafeCursor.readWrite = true;
            return unsafeCursor;
        }
        throw new IllegalStateException("already attached to a buffer");
    }
}
