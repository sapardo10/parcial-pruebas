package io.reactivex.internal.queue;

import io.reactivex.annotations.Nullable;
import io.reactivex.internal.fuseable.SimplePlainQueue;
import io.reactivex.internal.util.Pow2;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class SpscLinkedArrayQueue<T> implements SimplePlainQueue<T> {
    private static final Object HAS_NEXT = new Object();
    static final int MAX_LOOK_AHEAD_STEP = Integer.getInteger("jctools.spsc.max.lookahead.step", 4096).intValue();
    AtomicReferenceArray<Object> consumerBuffer;
    final AtomicLong consumerIndex = new AtomicLong();
    final int consumerMask;
    AtomicReferenceArray<Object> producerBuffer;
    final AtomicLong producerIndex = new AtomicLong();
    long producerLookAhead;
    int producerLookAheadStep;
    final int producerMask;

    public SpscLinkedArrayQueue(int bufferSize) {
        int p2capacity = Pow2.roundToPowerOfTwo(Math.max(8, bufferSize));
        int mask = p2capacity - 1;
        AtomicReferenceArray<Object> buffer = new AtomicReferenceArray(p2capacity + 1);
        this.producerBuffer = buffer;
        this.producerMask = mask;
        adjustLookAheadStep(p2capacity);
        this.consumerBuffer = buffer;
        this.consumerMask = mask;
        this.producerLookAhead = (long) (mask - 1);
        soProducerIndex(0);
    }

    public boolean offer(T e) {
        SpscLinkedArrayQueue spscLinkedArrayQueue = this;
        if (e != null) {
            AtomicReferenceArray<Object> buffer = spscLinkedArrayQueue.producerBuffer;
            long index = lpProducerIndex();
            int mask = spscLinkedArrayQueue.producerMask;
            int offset = calcWrappedOffset(index, mask);
            if (index < spscLinkedArrayQueue.producerLookAhead) {
                return writeToQueue(buffer, e, index, offset);
            }
            int lookAheadStep = spscLinkedArrayQueue.producerLookAheadStep;
            if (lvElement(buffer, calcWrappedOffset(((long) lookAheadStep) + index, mask)) == null) {
                spscLinkedArrayQueue.producerLookAhead = (((long) lookAheadStep) + index) - 1;
                return writeToQueue(buffer, e, index, offset);
            } else if (lvElement(buffer, calcWrappedOffset(1 + index, mask)) == null) {
                return writeToQueue(buffer, e, index, offset);
            } else {
                resize(buffer, index, offset, e, (long) mask);
                return true;
            }
        }
        throw new NullPointerException("Null is not a valid element");
    }

    private boolean writeToQueue(AtomicReferenceArray<Object> buffer, T e, long index, int offset) {
        soElement(buffer, offset, e);
        soProducerIndex(1 + index);
        return true;
    }

    private void resize(AtomicReferenceArray<Object> oldBuffer, long currIndex, int offset, T e, long mask) {
        AtomicReferenceArray<Object> newBuffer = new AtomicReferenceArray(oldBuffer.length());
        this.producerBuffer = newBuffer;
        this.producerLookAhead = (currIndex + mask) - 1;
        soElement(newBuffer, offset, e);
        soNext(oldBuffer, newBuffer);
        soElement(oldBuffer, offset, HAS_NEXT);
        soProducerIndex(1 + currIndex);
    }

    private void soNext(AtomicReferenceArray<Object> curr, AtomicReferenceArray<Object> next) {
        soElement(curr, calcDirectOffset(curr.length() - 1), next);
    }

    private AtomicReferenceArray<Object> lvNextBufferAndUnlink(AtomicReferenceArray<Object> curr, int nextIndex) {
        int nextOffset = calcDirectOffset(nextIndex);
        AtomicReferenceArray<Object> nextBuffer = (AtomicReferenceArray) lvElement(curr, nextOffset);
        soElement(curr, nextOffset, null);
        return nextBuffer;
    }

    @Nullable
    public T poll() {
        AtomicReferenceArray<Object> buffer = this.consumerBuffer;
        long index = lpConsumerIndex();
        int mask = this.consumerMask;
        int offset = calcWrappedOffset(index, mask);
        Object e = lvElement(buffer, offset);
        boolean isNextBuffer = e == HAS_NEXT;
        if (e != null && !isNextBuffer) {
            soElement(buffer, offset, null);
            soConsumerIndex(1 + index);
            return e;
        } else if (isNextBuffer) {
            return newBufferPoll(lvNextBufferAndUnlink(buffer, mask + 1), index, mask);
        } else {
            return null;
        }
    }

    private T newBufferPoll(AtomicReferenceArray<Object> nextBuffer, long index, int mask) {
        this.consumerBuffer = nextBuffer;
        int offsetInNew = calcWrappedOffset(index, mask);
        T n = lvElement(nextBuffer, offsetInNew);
        if (n != null) {
            soElement(nextBuffer, offsetInNew, null);
            soConsumerIndex(1 + index);
        }
        return n;
    }

    public T peek() {
        AtomicReferenceArray<Object> buffer = this.consumerBuffer;
        long index = lpConsumerIndex();
        int mask = this.consumerMask;
        Object e = lvElement(buffer, calcWrappedOffset(index, mask));
        if (e == HAS_NEXT) {
            return newBufferPeek(lvNextBufferAndUnlink(buffer, mask + 1), index, mask);
        }
        return e;
    }

    private T newBufferPeek(AtomicReferenceArray<Object> nextBuffer, long index, int mask) {
        this.consumerBuffer = nextBuffer;
        return lvElement(nextBuffer, calcWrappedOffset(index, mask));
    }

    public void clear() {
        while (true) {
            if (poll() == null) {
                if (isEmpty()) {
                    return;
                }
            }
        }
    }

    public int size() {
        long after = lvConsumerIndex();
        while (true) {
            long before = after;
            long currentProducerIndex = lvProducerIndex();
            after = lvConsumerIndex();
            if (before == after) {
                return (int) (currentProducerIndex - after);
            }
        }
    }

    public boolean isEmpty() {
        return lvProducerIndex() == lvConsumerIndex();
    }

    private void adjustLookAheadStep(int capacity) {
        this.producerLookAheadStep = Math.min(capacity / 4, MAX_LOOK_AHEAD_STEP);
    }

    private long lvProducerIndex() {
        return this.producerIndex.get();
    }

    private long lvConsumerIndex() {
        return this.consumerIndex.get();
    }

    private long lpProducerIndex() {
        return this.producerIndex.get();
    }

    private long lpConsumerIndex() {
        return this.consumerIndex.get();
    }

    private void soProducerIndex(long v) {
        this.producerIndex.lazySet(v);
    }

    private void soConsumerIndex(long v) {
        this.consumerIndex.lazySet(v);
    }

    private static int calcWrappedOffset(long index, int mask) {
        return calcDirectOffset(((int) index) & mask);
    }

    private static int calcDirectOffset(int index) {
        return index;
    }

    private static void soElement(AtomicReferenceArray<Object> buffer, int offset, Object e) {
        buffer.lazySet(offset, e);
    }

    private static <E> Object lvElement(AtomicReferenceArray<Object> buffer, int offset) {
        return buffer.get(offset);
    }

    public boolean offer(T first, T second) {
        AtomicReferenceArray<Object> buffer = this.producerBuffer;
        long p = lvProducerIndex();
        int m = this.producerMask;
        int pi;
        if (lvElement(buffer, calcWrappedOffset(p + 2, m)) == null) {
            pi = calcWrappedOffset(p, m);
            soElement(buffer, pi + 1, second);
            soElement(buffer, pi, first);
            soProducerIndex(2 + p);
        } else {
            AtomicReferenceArray<Object> newBuffer = new AtomicReferenceArray(buffer.length());
            this.producerBuffer = newBuffer;
            pi = calcWrappedOffset(p, m);
            soElement(newBuffer, pi + 1, second);
            soElement(newBuffer, pi, first);
            soNext(buffer, newBuffer);
            soElement(buffer, pi, HAS_NEXT);
            soProducerIndex(2 + p);
        }
        return true;
    }
}
