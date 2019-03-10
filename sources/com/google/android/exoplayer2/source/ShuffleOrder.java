package com.google.android.exoplayer2.source;

import java.util.Arrays;
import java.util.Random;

public interface ShuffleOrder {

    public static class DefaultShuffleOrder implements ShuffleOrder {
        private final int[] indexInShuffled;
        private final Random random;
        private final int[] shuffled;

        public DefaultShuffleOrder(int length) {
            this(length, new Random());
        }

        public DefaultShuffleOrder(int length, long randomSeed) {
            this(length, new Random(randomSeed));
        }

        public DefaultShuffleOrder(int[] shuffledIndices, long randomSeed) {
            this(Arrays.copyOf(shuffledIndices, shuffledIndices.length), new Random(randomSeed));
        }

        private DefaultShuffleOrder(int length, Random random) {
            this(createShuffledList(length, random), random);
        }

        private DefaultShuffleOrder(int[] shuffled, Random random) {
            this.shuffled = shuffled;
            this.random = random;
            this.indexInShuffled = new int[shuffled.length];
            for (int i = 0; i < shuffled.length; i++) {
                this.indexInShuffled[shuffled[i]] = i;
            }
        }

        public int getLength() {
            return this.shuffled.length;
        }

        public int getNextIndex(int index) {
            int shuffledIndex = this.indexInShuffled[index] + 1;
            int[] iArr = this.shuffled;
            return shuffledIndex < iArr.length ? iArr[shuffledIndex] : -1;
        }

        public int getPreviousIndex(int index) {
            int shuffledIndex = this.indexInShuffled[index] - 1;
            return shuffledIndex >= 0 ? this.shuffled[shuffledIndex] : -1;
        }

        public int getLastIndex() {
            int[] iArr = this.shuffled;
            return iArr.length > 0 ? iArr[iArr.length - 1] : -1;
        }

        public int getFirstIndex() {
            int[] iArr = this.shuffled;
            return iArr.length > 0 ? iArr[0] : -1;
        }

        public ShuffleOrder cloneAndInsert(int insertionIndex, int insertionCount) {
            int swapIndex;
            int[] insertionPoints = new int[insertionCount];
            int[] insertionValues = new int[insertionCount];
            for (int i = 0; i < insertionCount; i++) {
                insertionPoints[i] = this.random.nextInt(this.shuffled.length + 1);
                swapIndex = this.random.nextInt(i + 1);
                insertionValues[i] = insertionValues[swapIndex];
                insertionValues[swapIndex] = i + insertionIndex;
            }
            Arrays.sort(insertionPoints);
            int[] newShuffled = new int[(this.shuffled.length + insertionCount)];
            swapIndex = 0;
            int indexInInsertionList = 0;
            for (int i2 = 0; i2 < this.shuffled.length + insertionCount; i2++) {
                if (indexInInsertionList >= insertionCount || swapIndex != insertionPoints[indexInInsertionList]) {
                    int indexInOldShuffled = swapIndex + 1;
                    newShuffled[i2] = this.shuffled[swapIndex];
                    if (newShuffled[i2] >= insertionIndex) {
                        newShuffled[i2] = newShuffled[i2] + insertionCount;
                    }
                    swapIndex = indexInOldShuffled;
                } else {
                    int indexInInsertionList2 = indexInInsertionList + 1;
                    newShuffled[i2] = insertionValues[indexInInsertionList];
                    indexInInsertionList = indexInInsertionList2;
                }
            }
            return new DefaultShuffleOrder(newShuffled, new Random(this.random.nextLong()));
        }

        public ShuffleOrder cloneAndRemove(int indexFrom, int indexToExclusive) {
            int numberOfElementsToRemove = indexToExclusive - indexFrom;
            int[] newShuffled = new int[(this.shuffled.length - numberOfElementsToRemove)];
            int foundElementsCount = 0;
            int i = 0;
            while (true) {
                int[] iArr = this.shuffled;
                if (i >= iArr.length) {
                    return new DefaultShuffleOrder(newShuffled, new Random(this.random.nextLong()));
                }
                if (iArr[i] < indexFrom || iArr[i] >= indexToExclusive) {
                    int i2 = i - foundElementsCount;
                    int[] iArr2 = this.shuffled;
                    newShuffled[i2] = iArr2[i] >= indexFrom ? iArr2[i] - numberOfElementsToRemove : iArr2[i];
                } else {
                    foundElementsCount++;
                }
                i++;
            }
        }

        public ShuffleOrder cloneAndClear() {
            return new DefaultShuffleOrder(0, new Random(this.random.nextLong()));
        }

        private static int[] createShuffledList(int length, Random random) {
            int[] shuffled = new int[length];
            for (int i = 0; i < length; i++) {
                int swapIndex = random.nextInt(i + 1);
                shuffled[i] = shuffled[swapIndex];
                shuffled[swapIndex] = i;
            }
            return shuffled;
        }
    }

    public static final class UnshuffledShuffleOrder implements ShuffleOrder {
        private final int length;

        public UnshuffledShuffleOrder(int length) {
            this.length = length;
        }

        public int getLength() {
            return this.length;
        }

        public int getNextIndex(int index) {
            index++;
            return index < this.length ? index : -1;
        }

        public int getPreviousIndex(int index) {
            index--;
            return index >= 0 ? index : -1;
        }

        public int getLastIndex() {
            int i = this.length;
            return i > 0 ? i - 1 : -1;
        }

        public int getFirstIndex() {
            return this.length > 0 ? 0 : -1;
        }

        public ShuffleOrder cloneAndInsert(int insertionIndex, int insertionCount) {
            return new UnshuffledShuffleOrder(this.length + insertionCount);
        }

        public ShuffleOrder cloneAndRemove(int indexFrom, int indexToExclusive) {
            return new UnshuffledShuffleOrder((this.length - indexToExclusive) + indexFrom);
        }

        public ShuffleOrder cloneAndClear() {
            return new UnshuffledShuffleOrder(0);
        }
    }

    ShuffleOrder cloneAndClear();

    ShuffleOrder cloneAndInsert(int i, int i2);

    ShuffleOrder cloneAndRemove(int i, int i2);

    int getFirstIndex();

    int getLastIndex();

    int getLength();

    int getNextIndex(int i);

    int getPreviousIndex(int i);
}
