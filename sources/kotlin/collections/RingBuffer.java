package kotlin.collections;

import java.util.Iterator;
import java.util.RandomAccess;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u0000\n\u0002\b\t\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010(\n\u0002\b\f\b\u0002\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u00022\u00060\u0003j\u0002`\u0004B\r\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\u0013\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00028\u0000¢\u0006\u0002\u0010\u0016J\u0016\u0010\u0017\u001a\u00028\u00002\u0006\u0010\u0018\u001a\u00020\u0006H\u0002¢\u0006\u0002\u0010\u0019J\u0006\u0010\u001a\u001a\u00020\u001bJ\u000f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00028\u00000\u001dH\u0002J\u000e\u0010\u001e\u001a\u00020\u00142\u0006\u0010\u001f\u001a\u00020\u0006J\u0015\u0010 \u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\tH\u0014¢\u0006\u0002\u0010!J'\u0010 \u001a\b\u0012\u0004\u0012\u0002H\u00010\t\"\u0004\b\u0001\u0010\u00012\f\u0010\"\u001a\b\u0012\u0004\u0012\u0002H\u00010\tH\u0014¢\u0006\u0002\u0010#J9\u0010$\u001a\u00020\u0014\"\u0004\b\u0001\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00010\t2\u0006\u0010\u0015\u001a\u0002H\u00012\b\b\u0002\u0010%\u001a\u00020\u00062\b\b\u0002\u0010&\u001a\u00020\u0006H\u0002¢\u0006\u0002\u0010'J\u0015\u0010(\u001a\u00020\u0006*\u00020\u00062\u0006\u0010\u001f\u001a\u00020\u0006H\bR\u0018\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\tX\u0004¢\u0006\u0004\n\u0002\u0010\u000bR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR$\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u0006@RX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\r\"\u0004\b\u0011\u0010\u0007R\u000e\u0010\u0012\u001a\u00020\u0006X\u000e¢\u0006\u0002\n\u0000¨\u0006)"}, d2 = {"Lkotlin/collections/RingBuffer;", "T", "Lkotlin/collections/AbstractList;", "Ljava/util/RandomAccess;", "Lkotlin/collections/RandomAccess;", "capacity", "", "(I)V", "buffer", "", "", "[Ljava/lang/Object;", "getCapacity", "()I", "<set-?>", "size", "getSize", "setSize", "startIndex", "add", "", "element", "(Ljava/lang/Object;)V", "get", "index", "(I)Ljava/lang/Object;", "isFull", "", "iterator", "", "removeFirst", "n", "toArray", "()[Ljava/lang/Object;", "array", "([Ljava/lang/Object;)[Ljava/lang/Object;", "fill", "fromIndex", "toIndex", "([Ljava/lang/Object;Ljava/lang/Object;II)V", "forward", "kotlin-stdlib"}, k = 1, mv = {1, 1, 10})
/* compiled from: SlidingWindow.kt */
final class RingBuffer<T> extends AbstractList<T> implements RandomAccess {
    private final Object[] buffer;
    private final int capacity;
    private int size;
    private int startIndex;

    @org.jetbrains.annotations.NotNull
    public <T> T[] toArray(@org.jetbrains.annotations.NotNull T[] r7) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:19:0x005d in {2, 3, 8, 11, 14, 16, 18} preds:[]
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
        r0 = "array";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r7, r0);
        r0 = r7.length;
        r1 = r6.size();
        if (r0 >= r1) goto L_0x001b;
    L_0x000d:
        r0 = r6.size();
        r0 = java.util.Arrays.copyOf(r7, r0);
        r1 = "java.util.Arrays.copyOf(this, newSize)";
        kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r0, r1);
        goto L_0x001c;
    L_0x001b:
        r0 = r7;
        r1 = r6.size();
        r2 = 0;
        r3 = r6.startIndex;
    L_0x0024:
        if (r2 >= r1) goto L_0x0035;
    L_0x0026:
        r4 = r6.capacity;
        if (r3 >= r4) goto L_0x0035;
    L_0x002a:
        r4 = r6.buffer;
        r4 = r4[r3];
        r0[r2] = r4;
        r2 = r2 + 1;
        r3 = r3 + 1;
        goto L_0x0024;
    L_0x0035:
        r3 = 0;
    L_0x0036:
        if (r2 >= r1) goto L_0x0043;
    L_0x0038:
        r4 = r6.buffer;
        r4 = r4[r3];
        r0[r2] = r4;
        r2 = r2 + 1;
        r3 = r3 + 1;
        goto L_0x0036;
    L_0x0043:
        r4 = r0.length;
        r5 = r6.size();
        if (r4 <= r5) goto L_0x0052;
    L_0x004a:
        r4 = r6.size();
        r5 = 0;
        r0[r4] = r5;
    L_0x0052:
        if (r0 == 0) goto L_0x0055;
    L_0x0054:
        return r0;
    L_0x0055:
        r4 = new kotlin.TypeCastException;
        r5 = "null cannot be cast to non-null type kotlin.Array<T>";
        r4.<init>(r5);
        throw r4;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.collections.RingBuffer.toArray(java.lang.Object[]):T[]");
    }

    public RingBuffer(int capacity) {
        this.capacity = capacity;
        if ((this.capacity >= 0 ? 1 : null) != null) {
            this.buffer = new Object[this.capacity];
            return;
        }
        int $i$a$1$require = 0;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ring buffer capacity should not be negative but it is ");
        stringBuilder.append(this.capacity);
        throw new IllegalArgumentException(stringBuilder.toString().toString());
    }

    public final int getCapacity() {
        return this.capacity;
    }

    private void setSize(int <set-?>) {
        this.size = <set-?>;
    }

    public int getSize() {
        return this.size;
    }

    public T get(int index) {
        AbstractList.Companion.checkElementIndex$kotlin_stdlib(index, size());
        return this.buffer[(this.startIndex + index) % getCapacity()];
    }

    public final boolean isFull() {
        return size() == this.capacity;
    }

    @NotNull
    public Iterator<T> iterator() {
        return new RingBuffer$iterator$1(this);
    }

    @NotNull
    public Object[] toArray() {
        return toArray(new Object[size()]);
    }

    public final void add(T element) {
        if (isFull()) {
            throw new IllegalStateException("ring buffer is full");
        }
        this.buffer[(this.startIndex + size()) % getCapacity()] = element;
        setSize(size() + 1);
    }

    public final void removeFirst(int n) {
        Object obj = 1;
        int $i$a$2$require;
        if ((n >= 0 ? 1 : null) != null) {
            if (n > size()) {
                obj = null;
            }
            if (obj == null) {
                $i$a$2$require = 0;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("n shouldn't be greater than the buffer size: n = ");
                stringBuilder.append(n);
                stringBuilder.append(", size = ");
                stringBuilder.append(size());
                throw new IllegalArgumentException(stringBuilder.toString().toString());
            } else if (n > 0) {
                $i$a$2$require = this.startIndex;
                int $i$f$forward = 0;
                int end = ($i$a$2$require + n) % getCapacity();
                if ($i$a$2$require > end) {
                    fill(this.buffer, null, $i$a$2$require, this.capacity);
                    fill(this.buffer, null, 0, end);
                } else {
                    fill(this.buffer, null, $i$a$2$require, end);
                }
                this.startIndex = end;
                setSize(size() - n);
                return;
            } else {
                return;
            }
        }
        $i$a$2$require = 0;
        stringBuilder = new StringBuilder();
        stringBuilder.append("n shouldn't be negative but it is ");
        stringBuilder.append(n);
        throw new IllegalArgumentException(stringBuilder.toString().toString());
    }

    private final int forward(int $receiver, int n) {
        return ($receiver + n) % getCapacity();
    }

    static /* bridge */ /* synthetic */ void fill$default(RingBuffer ringBuffer, Object[] objArr, Object obj, int i, int i2, int i3, Object obj2) {
        if ((i3 & 2) != null) {
            i = 0;
        }
        if ((i3 & 4) != 0) {
            i2 = objArr.length;
        }
        ringBuffer.fill(objArr, obj, i, i2);
    }

    private final <T> void fill(@NotNull T[] $receiver, T element, int fromIndex, int toIndex) {
        for (int idx = fromIndex; idx < toIndex; idx++) {
            $receiver[idx] = element;
        }
    }
}
