package com.google.android.exoplayer2.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SlidingPercentile {
    private static final Comparator<Sample> INDEX_COMPARATOR = -$$Lambda$SlidingPercentile$IHMSNRVWSvKImU2XQD2j4ISb4-U.INSTANCE;
    private static final int MAX_RECYCLED_SAMPLES = 5;
    private static final int SORT_ORDER_BY_INDEX = 1;
    private static final int SORT_ORDER_BY_VALUE = 0;
    private static final int SORT_ORDER_NONE = -1;
    private static final Comparator<Sample> VALUE_COMPARATOR = -$$Lambda$SlidingPercentile$UufTq1Ma5g1qQu0Vqc6f2CE68bE.INSTANCE;
    private int currentSortOrder = -1;
    private final int maxWeight;
    private int nextSampleIndex;
    private int recycledSampleCount;
    private final Sample[] recycledSamples = new Sample[5];
    private final ArrayList<Sample> samples = new ArrayList();
    private int totalWeight;

    private static class Sample {
        public int index;
        public float value;
        public int weight;

        private Sample() {
        }
    }

    public SlidingPercentile(int maxWeight) {
        this.maxWeight = maxWeight;
    }

    public void addSample(int weight, float value) {
        Sample newSample;
        ensureSortedByIndex();
        int i = this.recycledSampleCount;
        if (i > 0) {
            Sample[] sampleArr = this.recycledSamples;
            i--;
            this.recycledSampleCount = i;
            newSample = sampleArr[i];
        } else {
            newSample = new Sample();
        }
        int i2 = this.nextSampleIndex;
        this.nextSampleIndex = i2 + 1;
        newSample.index = i2;
        newSample.weight = weight;
        newSample.value = value;
        this.samples.add(newSample);
        this.totalWeight += weight;
        while (true) {
            i2 = this.totalWeight;
            int i3 = this.maxWeight;
            if (i2 > i3) {
                i2 -= i3;
                Sample oldestSample = (Sample) this.samples.get(0);
                if (oldestSample.weight <= i2) {
                    this.totalWeight -= oldestSample.weight;
                    this.samples.remove(0);
                    int i4 = this.recycledSampleCount;
                    if (i4 < 5) {
                        Sample[] sampleArr2 = this.recycledSamples;
                        this.recycledSampleCount = i4 + 1;
                        sampleArr2[i4] = oldestSample;
                    }
                } else {
                    oldestSample.weight -= i2;
                    this.totalWeight -= i2;
                }
            } else {
                return;
            }
        }
    }

    public float getPercentile(float percentile) {
        float f;
        ensureSortedByValue();
        float desiredWeight = ((float) this.totalWeight) * percentile;
        int accumulatedWeight = 0;
        for (int i = 0; i < this.samples.size(); i++) {
            Sample currentSample = (Sample) this.samples.get(i);
            accumulatedWeight += currentSample.weight;
            if (((float) accumulatedWeight) >= desiredWeight) {
                return currentSample.value;
            }
        }
        if (this.samples.isEmpty()) {
            f = Float.NaN;
        } else {
            ArrayList arrayList = this.samples;
            f = ((Sample) arrayList.get(arrayList.size() - 1)).value;
        }
        return f;
    }

    private void ensureSortedByIndex() {
        if (this.currentSortOrder != 1) {
            Collections.sort(this.samples, INDEX_COMPARATOR);
            this.currentSortOrder = 1;
        }
    }

    private void ensureSortedByValue() {
        if (this.currentSortOrder != 0) {
            Collections.sort(this.samples, VALUE_COMPARATOR);
            this.currentSortOrder = 0;
        }
    }
}
