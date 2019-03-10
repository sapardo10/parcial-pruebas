package kotlin.io;

import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import kotlin.Lazy;
import kotlin.Metadata;
import kotlin.internal.InlineOnly;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference0Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000d\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\u0010\u000b\n\u0002\u0010\u0005\n\u0002\u0010\f\n\u0002\u0010\u0019\n\u0002\u0010\u0006\n\u0002\u0010\u0007\n\u0002\u0010\t\n\u0002\u0010\n\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\b\u001a\u0011\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\rH\b\u001a\u0011\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u000eH\b\u001a\u0011\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u000fH\b\u001a\u0011\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0010H\b\u001a\u0011\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0011H\b\u001a\u0011\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0012H\b\u001a\u0011\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0001H\b\u001a\u0011\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0013H\b\u001a\u0011\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0014H\b\u001a\t\u0010\u0015\u001a\u00020\nH\b\u001a\u0013\u0010\u0015\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\b\u001a\u0011\u0010\u0015\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\rH\b\u001a\u0011\u0010\u0015\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u000eH\b\u001a\u0011\u0010\u0015\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u000fH\b\u001a\u0011\u0010\u0015\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0010H\b\u001a\u0011\u0010\u0015\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0011H\b\u001a\u0011\u0010\u0015\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0012H\b\u001a\u0011\u0010\u0015\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0001H\b\u001a\u0011\u0010\u0015\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0013H\b\u001a\u0011\u0010\u0015\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0014H\b\u001a\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017\u001a\u001a\u0010\u0016\u001a\u0004\u0018\u00010\u00172\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u0003\u001a\u00020\u0004H\u0000\u001a\f\u0010\u001a\u001a\u00020\r*\u00020\u001bH\u0002\u001a\f\u0010\u001c\u001a\u00020\u000f*\u00020\u001bH\u0002\u001a\f\u0010\u001d\u001a\u00020\n*\u00020\u001eH\u0002\u001a$\u0010\u001f\u001a\u00020\r*\u00020\u00042\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\u001b2\u0006\u0010#\u001a\u00020\rH\u0002\"\u000e\u0010\u0000\u001a\u00020\u0001XT¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001XT¢\u0006\u0002\n\u0000\"\u001b\u0010\u0003\u001a\u00020\u00048BX\u0002¢\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006¨\u0006$"}, d2 = {"BUFFER_SIZE", "", "LINE_SEPARATOR_MAX_LENGTH", "decoder", "Ljava/nio/charset/CharsetDecoder;", "getDecoder", "()Ljava/nio/charset/CharsetDecoder;", "decoder$delegate", "Lkotlin/Lazy;", "print", "", "message", "", "", "", "", "", "", "", "", "", "println", "readLine", "", "inputStream", "Ljava/io/InputStream;", "containsLineSeparator", "Ljava/nio/CharBuffer;", "dequeue", "flipBack", "Ljava/nio/Buffer;", "tryDecode", "byteBuffer", "Ljava/nio/ByteBuffer;", "charBuffer", "isEndOfStream", "kotlin-stdlib"}, k = 2, mv = {1, 1, 10})
@JvmName(name = "ConsoleKt")
/* compiled from: Console.kt */
public final class ConsoleKt {
    static final /* synthetic */ KProperty[] $$delegatedProperties = new KProperty[]{Reflection.property0(new PropertyReference0Impl(Reflection.getOrCreateKotlinPackage(ConsoleKt.class, "kotlin-stdlib"), "decoder", "getDecoder()Ljava/nio/charset/CharsetDecoder;"))};
    private static final int BUFFER_SIZE = 32;
    private static final int LINE_SEPARATOR_MAX_LENGTH = 2;
    private static final Lazy decoder$delegate = LazyKt__LazyJVMKt.lazy(ConsoleKt$decoder$2.INSTANCE);

    private static final CharsetDecoder getDecoder() {
        Lazy lazy = decoder$delegate;
        KProperty kProperty = $$delegatedProperties[0];
        return (CharsetDecoder) lazy.getValue();
    }

    @org.jetbrains.annotations.Nullable
    public static final java.lang.String readLine(@org.jetbrains.annotations.NotNull java.io.InputStream r11, @org.jetbrains.annotations.NotNull java.nio.charset.CharsetDecoder r12) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:40:0x00b0 in {2, 3, 8, 13, 16, 17, 18, 23, 27, 28, 29, 31, 32, 34, 36, 37, 39} preds:[]
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
        r0 = "inputStream";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r11, r0);
        r0 = "decoder";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r12, r0);
        r0 = r12.maxCharsPerByte();
        r1 = 1;
        r2 = (float) r1;
        r3 = 0;
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 > 0) goto L_0x0017;
    L_0x0015:
        r0 = 1;
        goto L_0x0018;
    L_0x0017:
        r0 = 0;
    L_0x0018:
        if (r0 == 0) goto L_0x00a1;
    L_0x001a:
        r0 = 32;
        r0 = java.nio.ByteBuffer.allocate(r0);
        r2 = 2;
        r2 = java.nio.CharBuffer.allocate(r2);
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = r11.read();
        r6 = -1;
        if (r5 != r6) goto L_0x0033;
    L_0x0031:
        r1 = 0;
        return r1;
        r7 = (byte) r5;
        r0.put(r7);
        r7 = "byteBuffer";
        kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r0, r7);
        r7 = "charBuffer";
        kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r2, r7);
        r7 = tryDecode(r12, r0, r2, r3);
        if (r7 == 0) goto L_0x005e;
    L_0x0048:
        r7 = containsLineSeparator(r2);
        if (r7 == 0) goto L_0x004f;
    L_0x004e:
        goto L_0x0065;
    L_0x004f:
        r7 = r2.hasRemaining();
        if (r7 != 0) goto L_0x005d;
    L_0x0055:
        r7 = dequeue(r2);
        r4.append(r7);
        goto L_0x005f;
    L_0x005d:
        goto L_0x005f;
    L_0x005f:
        r5 = r11.read();
        if (r5 != r6) goto L_0x00a0;
    L_0x0065:
        r6 = r12;
        r7 = r3;
        tryDecode(r6, r0, r2, r1);
        r6.reset();
        r6 = r2;
        r8 = r6.position();
        r3 = r6.get(r3);
        r1 = r6.get(r1);
        r9 = 10;
        switch(r8) {
            case 1: goto L_0x0093;
            case 2: goto L_0x0081;
            default: goto L_0x0080;
        };
    L_0x0080:
        goto L_0x0099;
    L_0x0081:
        r10 = 13;
        if (r3 != r10) goto L_0x0089;
    L_0x0085:
        if (r1 == r9) goto L_0x0088;
    L_0x0087:
        goto L_0x0089;
    L_0x0088:
        goto L_0x008c;
    L_0x0089:
        r4.append(r3);
    L_0x008c:
        if (r1 == r9) goto L_0x0092;
    L_0x008e:
        r4.append(r1);
        goto L_0x0099;
    L_0x0092:
        goto L_0x0099;
    L_0x0093:
        if (r3 == r9) goto L_0x0099;
    L_0x0095:
        r4.append(r3);
        r1 = r4.toString();
        return r1;
    L_0x00a0:
        goto L_0x0033;
    L_0x00a1:
        r0 = r3;
        r1 = new java.lang.IllegalArgumentException;
        r0 = "Encodings with multiple chars per byte are not supported";
        r0 = r0.toString();
        r1.<init>(r0);
        r1 = (java.lang.Throwable) r1;
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.ConsoleKt.readLine(java.io.InputStream, java.nio.charset.CharsetDecoder):java.lang.String");
    }

    @InlineOnly
    private static final void print(Object message) {
        System.out.print(message);
    }

    @InlineOnly
    private static final void print(int message) {
        System.out.print(message);
    }

    @InlineOnly
    private static final void print(long message) {
        System.out.print(message);
    }

    @InlineOnly
    private static final void print(byte message) {
        System.out.print(Byte.valueOf(message));
    }

    @InlineOnly
    private static final void print(short message) {
        System.out.print(Short.valueOf(message));
    }

    @InlineOnly
    private static final void print(char message) {
        System.out.print(message);
    }

    @InlineOnly
    private static final void print(boolean message) {
        System.out.print(message);
    }

    @InlineOnly
    private static final void print(float message) {
        System.out.print(message);
    }

    @InlineOnly
    private static final void print(double message) {
        System.out.print(message);
    }

    @InlineOnly
    private static final void print(char[] message) {
        System.out.print(message);
    }

    @InlineOnly
    private static final void println(Object message) {
        System.out.println(message);
    }

    @InlineOnly
    private static final void println(int message) {
        System.out.println(message);
    }

    @InlineOnly
    private static final void println(long message) {
        System.out.println(message);
    }

    @InlineOnly
    private static final void println(byte message) {
        System.out.println(Byte.valueOf(message));
    }

    @InlineOnly
    private static final void println(short message) {
        System.out.println(Short.valueOf(message));
    }

    @InlineOnly
    private static final void println(char message) {
        System.out.println(message);
    }

    @InlineOnly
    private static final void println(boolean message) {
        System.out.println(message);
    }

    @InlineOnly
    private static final void println(float message) {
        System.out.println(message);
    }

    @InlineOnly
    private static final void println(double message) {
        System.out.println(message);
    }

    @InlineOnly
    private static final void println(char[] message) {
        System.out.println(message);
    }

    @InlineOnly
    private static final void println() {
        System.out.println();
    }

    @Nullable
    public static final String readLine() {
        InputStream inputStream = System.in;
        Intrinsics.checkExpressionValueIsNotNull(inputStream, "System.`in`");
        return readLine(inputStream, getDecoder());
    }

    private static final boolean tryDecode(@NotNull CharsetDecoder $receiver, ByteBuffer byteBuffer, CharBuffer charBuffer, boolean isEndOfStream) {
        int positionBefore = charBuffer.position();
        byteBuffer.flip();
        CoderResult $receiver2 = $receiver.decode(byteBuffer, charBuffer, isEndOfStream);
        boolean z = false;
        int $i$a$2$also = 0;
        if ($receiver2.isError()) {
            $receiver2.throwException();
        }
        if (charBuffer.position() > positionBefore) {
            z = true;
        }
        if (z) {
            byteBuffer.clear();
        } else {
            flipBack(byteBuffer);
        }
        return z;
    }

    private static final boolean containsLineSeparator(@NotNull CharBuffer $receiver) {
        if ($receiver.get(1) != '\n') {
            return $receiver.get(0) == '\n';
        } else {
            return true;
        }
    }

    private static final void flipBack(@NotNull Buffer $receiver) {
        $receiver.position($receiver.limit());
        $receiver.limit($receiver.capacity());
    }

    private static final char dequeue(@NotNull CharBuffer $receiver) {
        $receiver.flip();
        char c = $receiver.get();
        char it = c;
        $receiver.compact();
        return c;
    }
}
