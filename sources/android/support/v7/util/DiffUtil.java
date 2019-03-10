package android.support.v7.util;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.RecyclerView.Adapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class DiffUtil {
    private static final Comparator<Snake> SNAKE_COMPARATOR = new C03251();

    /* renamed from: android.support.v7.util.DiffUtil$1 */
    static class C03251 implements Comparator<Snake> {
        C03251() {
        }

        public int compare(Snake o1, Snake o2) {
            int cmpX = o1.f3x - o2.f3x;
            return cmpX == 0 ? o1.f4y - o2.f4y : cmpX;
        }
    }

    public static abstract class Callback {
        public abstract boolean areContentsTheSame(int i, int i2);

        public abstract boolean areItemsTheSame(int i, int i2);

        public abstract int getNewListSize();

        public abstract int getOldListSize();

        @Nullable
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            return null;
        }
    }

    public static class DiffResult {
        private static final int FLAG_CHANGED = 2;
        private static final int FLAG_IGNORE = 16;
        private static final int FLAG_MASK = 31;
        private static final int FLAG_MOVED_CHANGED = 4;
        private static final int FLAG_MOVED_NOT_CHANGED = 8;
        private static final int FLAG_NOT_CHANGED = 1;
        private static final int FLAG_OFFSET = 5;
        private final Callback mCallback;
        private final boolean mDetectMoves;
        private final int[] mNewItemStatuses;
        private final int mNewListSize;
        private final int[] mOldItemStatuses;
        private final int mOldListSize;
        private final List<Snake> mSnakes;

        DiffResult(Callback callback, List<Snake> snakes, int[] oldItemStatuses, int[] newItemStatuses, boolean detectMoves) {
            this.mSnakes = snakes;
            this.mOldItemStatuses = oldItemStatuses;
            this.mNewItemStatuses = newItemStatuses;
            Arrays.fill(this.mOldItemStatuses, 0);
            Arrays.fill(this.mNewItemStatuses, 0);
            this.mCallback = callback;
            this.mOldListSize = callback.getOldListSize();
            this.mNewListSize = callback.getNewListSize();
            this.mDetectMoves = detectMoves;
            addRootSnake();
            findMatchingItems();
        }

        private void addRootSnake() {
            Snake firstSnake = this.mSnakes.isEmpty() ? null : (Snake) this.mSnakes.get(0);
            if (firstSnake != null && firstSnake.f3x == 0) {
                if (firstSnake.f4y == 0) {
                    return;
                }
            }
            Snake root = new Snake();
            root.f3x = 0;
            root.f4y = 0;
            root.removal = false;
            root.size = 0;
            root.reverse = false;
            this.mSnakes.add(0, root);
        }

        private void findMatchingItems() {
            int posOld = this.mOldListSize;
            int posNew = this.mNewListSize;
            for (int i = this.mSnakes.size() - 1; i >= 0; i--) {
                Snake snake = (Snake) this.mSnakes.get(i);
                int endX = snake.f3x + snake.size;
                int endY = snake.f4y + snake.size;
                if (this.mDetectMoves) {
                    while (posOld > endX) {
                        findAddition(posOld, posNew, i);
                        posOld--;
                    }
                    while (posNew > endY) {
                        findRemoval(posOld, posNew, i);
                        posNew--;
                    }
                }
                for (int j = 0; j < snake.size; j++) {
                    int oldItemPos = snake.f3x + j;
                    int newItemPos = snake.f4y + j;
                    int changeFlag = this.mCallback.areContentsTheSame(oldItemPos, newItemPos) ? 1 : 2;
                    this.mOldItemStatuses[oldItemPos] = (newItemPos << 5) | changeFlag;
                    this.mNewItemStatuses[newItemPos] = (oldItemPos << 5) | changeFlag;
                }
                posOld = snake.f3x;
                posNew = snake.f4y;
            }
        }

        private void findAddition(int x, int y, int snakeIndex) {
            if (this.mOldItemStatuses[x - 1] == 0) {
                findMatchingItem(x, y, snakeIndex, false);
            }
        }

        private void findRemoval(int x, int y, int snakeIndex) {
            if (this.mNewItemStatuses[y - 1] == 0) {
                findMatchingItem(x, y, snakeIndex, true);
            }
        }

        private boolean findMatchingItem(int x, int y, int snakeIndex, boolean removal) {
            int myItemPos;
            int i;
            int i2;
            DiffResult diffResult = this;
            if (removal) {
                myItemPos = y - 1;
                i = x;
                i2 = y - 1;
            } else {
                myItemPos = x - 1;
                i = x - 1;
                i2 = y;
            }
            for (int i3 = snakeIndex; i3 >= 0; i3--) {
                Snake snake = (Snake) diffResult.mSnakes.get(i3);
                int endX = snake.f3x + snake.size;
                int endY = snake.f4y + snake.size;
                int changeFlag = 8;
                int pos;
                if (removal) {
                    for (pos = i - 1; pos >= endX; pos--) {
                        if (diffResult.mCallback.areItemsTheSame(pos, myItemPos)) {
                            if (!diffResult.mCallback.areContentsTheSame(pos, myItemPos)) {
                                changeFlag = 4;
                            }
                            diffResult.mNewItemStatuses[myItemPos] = (pos << 5) | 16;
                            diffResult.mOldItemStatuses[pos] = (myItemPos << 5) | changeFlag;
                            return true;
                        }
                    }
                } else {
                    for (pos = i2 - 1; pos >= endY; pos--) {
                        if (diffResult.mCallback.areItemsTheSame(myItemPos, pos)) {
                            if (!diffResult.mCallback.areContentsTheSame(myItemPos, pos)) {
                                changeFlag = 4;
                            }
                            diffResult.mOldItemStatuses[x - 1] = (pos << 5) | 16;
                            diffResult.mNewItemStatuses[pos] = ((x - 1) << 5) | changeFlag;
                            return true;
                        }
                    }
                    continue;
                }
                i = snake.f3x;
                i2 = snake.f4y;
            }
            return false;
        }

        public void dispatchUpdatesTo(Adapter adapter) {
            dispatchUpdatesTo(new AdapterListUpdateCallback(adapter));
        }

        public void dispatchUpdatesTo(ListUpdateCallback updateCallback) {
            BatchingListUpdateCallback batchingCallback;
            DiffResult diffResult = this;
            ListUpdateCallback listUpdateCallback = updateCallback;
            if (listUpdateCallback instanceof BatchingListUpdateCallback) {
                ListUpdateCallback listUpdateCallback2 = listUpdateCallback;
                batchingCallback = (BatchingListUpdateCallback) listUpdateCallback;
            } else {
                BatchingListUpdateCallback batchingCallback2 = new BatchingListUpdateCallback(listUpdateCallback);
                BatchingListUpdateCallback updateCallback2 = batchingCallback2;
                batchingCallback = batchingCallback2;
            }
            List<PostponedUpdate> postponedUpdates = new ArrayList();
            int posOld = diffResult.mOldListSize;
            int posOld2 = posOld;
            int posNew = diffResult.mNewListSize;
            for (int snakeIndex = diffResult.mSnakes.size() - 1; snakeIndex >= 0; snakeIndex--) {
                int endY;
                Snake snake = (Snake) diffResult.mSnakes.get(snakeIndex);
                int snakeSize = snake.size;
                int endX = snake.f3x + snakeSize;
                int endY2 = snake.f4y + snakeSize;
                if (endX < posOld2) {
                    endY = endY2;
                    dispatchRemovals(postponedUpdates, batchingCallback, endX, posOld2 - endX, endX);
                } else {
                    endY = endY2;
                }
                if (endY < posNew) {
                    posOld = snakeSize;
                    dispatchAdditions(postponedUpdates, batchingCallback, endX, posNew - endY, endY);
                } else {
                    posOld = snakeSize;
                }
                for (snakeSize = posOld - 1; snakeSize >= 0; snakeSize--) {
                    if ((diffResult.mOldItemStatuses[snake.f3x + snakeSize] & 31) == 2) {
                        batchingCallback.onChanged(snake.f3x + snakeSize, 1, diffResult.mCallback.getChangePayload(snake.f3x + snakeSize, snake.f4y + snakeSize));
                    }
                }
                posOld2 = snake.f3x;
                posNew = snake.f4y;
            }
            batchingCallback.dispatchLastEvent();
        }

        private static PostponedUpdate removePostponedUpdate(List<PostponedUpdate> updates, int pos, boolean removal) {
            for (int i = updates.size() - 1; i >= 0; i--) {
                PostponedUpdate update = (PostponedUpdate) updates.get(i);
                if (update.posInOwnerList == pos && update.removal == removal) {
                    updates.remove(i);
                    for (int j = i; j < updates.size(); j++) {
                        PostponedUpdate postponedUpdate = (PostponedUpdate) updates.get(j);
                        postponedUpdate.currentPos += removal ? 1 : -1;
                    }
                    return update;
                }
            }
            return null;
        }

        private void dispatchAdditions(List<PostponedUpdate> postponedUpdates, ListUpdateCallback updateCallback, int start, int count, int globalIndex) {
            if (this.mDetectMoves) {
                for (int i = count - 1; i >= 0; i--) {
                    int status = this.mNewItemStatuses[globalIndex + i] & 31;
                    if (status == 0) {
                        updateCallback.onInserted(start, 1);
                        for (PostponedUpdate update : postponedUpdates) {
                            update.currentPos++;
                        }
                    } else if (status == 4 || status == 8) {
                        int pos = this.mNewItemStatuses[globalIndex + i] >> 5;
                        updateCallback.onMoved(removePostponedUpdate(postponedUpdates, pos, true).currentPos, start);
                        if (status == 4) {
                            updateCallback.onChanged(start, 1, this.mCallback.getChangePayload(pos, globalIndex + i));
                        }
                    } else if (status == 16) {
                        postponedUpdates.add(new PostponedUpdate(globalIndex + i, start, false));
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("unknown flag for pos ");
                        stringBuilder.append(globalIndex + i);
                        stringBuilder.append(StringUtils.SPACE);
                        stringBuilder.append(Long.toBinaryString((long) status));
                        throw new IllegalStateException(stringBuilder.toString());
                    }
                }
                return;
            }
            updateCallback.onInserted(start, count);
        }

        private void dispatchRemovals(List<PostponedUpdate> postponedUpdates, ListUpdateCallback updateCallback, int start, int count, int globalIndex) {
            if (this.mDetectMoves) {
                for (int i = count - 1; i >= 0; i--) {
                    int status = this.mOldItemStatuses[globalIndex + i] & 31;
                    if (status == 0) {
                        updateCallback.onRemoved(start + i, 1);
                        for (PostponedUpdate update : postponedUpdates) {
                            update.currentPos--;
                        }
                    } else if (status == 4 || status == 8) {
                        int pos = this.mOldItemStatuses[globalIndex + i] >> 5;
                        PostponedUpdate update2 = removePostponedUpdate(postponedUpdates, pos, null);
                        updateCallback.onMoved(start + i, update2.currentPos - 1);
                        if (status == 4) {
                            updateCallback.onChanged(update2.currentPos - 1, 1, this.mCallback.getChangePayload(globalIndex + i, pos));
                        }
                    } else if (status == 16) {
                        postponedUpdates.add(new PostponedUpdate(globalIndex + i, start + i, true));
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("unknown flag for pos ");
                        stringBuilder.append(globalIndex + i);
                        stringBuilder.append(StringUtils.SPACE);
                        stringBuilder.append(Long.toBinaryString((long) status));
                        throw new IllegalStateException(stringBuilder.toString());
                    }
                }
                return;
            }
            updateCallback.onRemoved(start, count);
        }

        @VisibleForTesting
        List<Snake> getSnakes() {
            return this.mSnakes;
        }
    }

    public static abstract class ItemCallback<T> {
        public abstract boolean areContentsTheSame(T t, T t2);

        public abstract boolean areItemsTheSame(T t, T t2);

        public Object getChangePayload(T t, T t2) {
            return null;
        }
    }

    private static class PostponedUpdate {
        int currentPos;
        int posInOwnerList;
        boolean removal;

        public PostponedUpdate(int posInOwnerList, int currentPos, boolean removal) {
            this.posInOwnerList = posInOwnerList;
            this.currentPos = currentPos;
            this.removal = removal;
        }
    }

    static class Range {
        int newListEnd;
        int newListStart;
        int oldListEnd;
        int oldListStart;

        public Range(int oldListStart, int oldListEnd, int newListStart, int newListEnd) {
            this.oldListStart = oldListStart;
            this.oldListEnd = oldListEnd;
            this.newListStart = newListStart;
            this.newListEnd = newListEnd;
        }
    }

    static class Snake {
        boolean removal;
        boolean reverse;
        int size;
        /* renamed from: x */
        int f3x;
        /* renamed from: y */
        int f4y;

        Snake() {
        }
    }

    private DiffUtil() {
    }

    public static DiffResult calculateDiff(Callback cb) {
        return calculateDiff(cb, true);
    }

    public static DiffResult calculateDiff(Callback cb, boolean detectMoves) {
        int oldSize = cb.getOldListSize();
        int newSize = cb.getNewListSize();
        List<Snake> snakes = new ArrayList();
        ArrayList stack = new ArrayList();
        stack.add(new Range(0, oldSize, 0, newSize));
        int max = (oldSize + newSize) + Math.abs(oldSize - newSize);
        int[] forward = new int[(max * 2)];
        int[] iArr = new int[(max * 2)];
        List<Range> rangePool = new ArrayList();
        while (!stack.isEmpty()) {
            Range range = (Range) stack.remove(stack.size() - 1);
            Snake snake = diffPartial(cb, range.oldListStart, range.oldListEnd, range.newListStart, range.newListEnd, forward, iArr, max);
            if (snake != null) {
                if (snake.size > 0) {
                    snakes.add(snake);
                }
                snake.f3x += range.oldListStart;
                snake.f4y += range.newListStart;
                Range left = rangePool.isEmpty() ? new Range() : (Range) rangePool.remove(rangePool.size() - 1);
                left.oldListStart = range.oldListStart;
                left.newListStart = range.newListStart;
                if (snake.reverse) {
                    left.oldListEnd = snake.f3x;
                    left.newListEnd = snake.f4y;
                } else if (snake.removal) {
                    left.oldListEnd = snake.f3x - 1;
                    left.newListEnd = snake.f4y;
                } else {
                    left.oldListEnd = snake.f3x;
                    left.newListEnd = snake.f4y - 1;
                }
                stack.add(left);
                Range right = range;
                if (!snake.reverse) {
                    right.oldListStart = snake.f3x + snake.size;
                    right.newListStart = snake.f4y + snake.size;
                } else if (snake.removal) {
                    right.oldListStart = (snake.f3x + snake.size) + 1;
                    right.newListStart = snake.f4y + snake.size;
                } else {
                    right.oldListStart = snake.f3x + snake.size;
                    right.newListStart = (snake.f4y + snake.size) + 1;
                }
                stack.add(right);
            } else {
                rangePool.add(range);
            }
        }
        Collections.sort(snakes, SNAKE_COMPARATOR);
        int[] backward = iArr;
        return new DiffResult(cb, snakes, forward, iArr, detectMoves);
    }

    private static Snake diffPartial(Callback cb, int startOld, int endOld, int startNew, int endNew, int[] forward, int[] backward, int kOffset) {
        Callback callback = cb;
        int[] iArr = forward;
        int[] iArr2 = backward;
        int oldSize = endOld - startOld;
        int newSize = endNew - startNew;
        if (endOld - startOld < 1) {
        } else if (endNew - startNew < 1) {
            r17 = oldSize;
        } else {
            int delta = oldSize - newSize;
            int dLimit = ((oldSize + newSize) + 1) / 2;
            Arrays.fill(iArr, (kOffset - dLimit) - 1, (kOffset + dLimit) + 1, 0);
            Arrays.fill(iArr2, ((kOffset - dLimit) - 1) + delta, ((kOffset + dLimit) + 1) + delta, oldSize);
            boolean checkInFwd = delta % 2 != 0;
            int d = 0;
            while (d <= dLimit) {
                int x;
                int k = -d;
                while (k <= d) {
                    boolean removal;
                    int y;
                    if (k != (-d)) {
                        if (k == d || iArr[(kOffset + k) - 1] >= iArr[(kOffset + k) + 1]) {
                            x = iArr[(kOffset + k) - 1] + 1;
                            removal = true;
                            y = x - k;
                            while (x < oldSize && y < newSize) {
                                if (callback.areItemsTheSame(startOld + x, startNew + y)) {
                                    break;
                                }
                                x++;
                                y++;
                            }
                            iArr[kOffset + k] = x;
                            if (!checkInFwd && k >= (delta - d) + 1 && k <= (delta + d) - 1) {
                                if (iArr[kOffset + k] >= iArr2[kOffset + k]) {
                                    Snake outSnake = new Snake();
                                    outSnake.f3x = iArr2[kOffset + k];
                                    outSnake.f4y = outSnake.f3x - k;
                                    outSnake.size = iArr[kOffset + k] - iArr2[kOffset + k];
                                    outSnake.removal = removal;
                                    outSnake.reverse = false;
                                    return outSnake;
                                }
                            }
                            k += 2;
                        }
                    }
                    x = iArr[(kOffset + k) + 1];
                    removal = false;
                    y = x - k;
                    while (x < oldSize) {
                        if (callback.areItemsTheSame(startOld + x, startNew + y)) {
                            break;
                        }
                        x++;
                        y++;
                    }
                    iArr[kOffset + k] = x;
                    if (!checkInFwd) {
                    }
                    k += 2;
                }
                k = -d;
                while (k <= d) {
                    int x2;
                    boolean removal2;
                    int y2;
                    x = k + delta;
                    if (x != d + delta) {
                        if (x == (-d) + delta || iArr2[(kOffset + x) - 1] >= iArr2[(kOffset + x) + 1]) {
                            x2 = iArr2[(kOffset + x) + 1] - 1;
                            removal2 = true;
                            y2 = x2 - x;
                            while (x2 > 0 && y2 > 0) {
                                r17 = oldSize;
                                if (callback.areItemsTheSame((startOld + x2) - 1, (startNew + y2) - 1) != 0) {
                                    break;
                                }
                                x2--;
                                y2--;
                                oldSize = r17;
                            }
                            r17 = oldSize;
                            iArr2[kOffset + x] = x2;
                            if (checkInFwd && k + delta >= (-d) && k + delta <= d) {
                                if (iArr[kOffset + x] >= iArr2[kOffset + x]) {
                                    oldSize = new Snake();
                                    oldSize.f3x = iArr2[kOffset + x];
                                    oldSize.f4y = oldSize.f3x - x;
                                    oldSize.size = iArr[kOffset + x] - iArr2[kOffset + x];
                                    oldSize.removal = removal2;
                                    oldSize.reverse = true;
                                    return oldSize;
                                }
                            }
                            k += 2;
                            oldSize = r17;
                        }
                    }
                    x2 = iArr2[(kOffset + x) - 1];
                    removal2 = false;
                    y2 = x2 - x;
                    while (x2 > 0) {
                        r17 = oldSize;
                        if (callback.areItemsTheSame((startOld + x2) - 1, (startNew + y2) - 1) != 0) {
                            break;
                        }
                        x2--;
                        y2--;
                        oldSize = r17;
                    }
                    r17 = oldSize;
                    iArr2[kOffset + x] = x2;
                    if (checkInFwd) {
                    }
                    k += 2;
                    oldSize = r17;
                }
                d++;
            }
            throw new IllegalStateException("DiffUtil hit an unexpected case while trying to calculate the optimal path. Please make sure your data is not changing during the diff calculation.");
        }
        return null;
    }
}
