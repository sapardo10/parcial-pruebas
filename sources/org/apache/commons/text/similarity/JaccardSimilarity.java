package org.apache.commons.text.similarity;

import java.util.HashSet;
import java.util.Set;

public class JaccardSimilarity implements SimilarityScore<Double> {
    public Double apply(CharSequence left, CharSequence right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        double round = (double) Math.round(calculateJaccardSimilarity(left, right).doubleValue() * 100.0d);
        Double.isNaN(round);
        return Double.valueOf(round / 100.0d);
    }

    private Double calculateJaccardSimilarity(CharSequence left, CharSequence right) {
        Set<String> intersectionSet = new HashSet();
        Set<String> unionSet = new HashSet();
        boolean unionFilled = false;
        int leftLength = left.length();
        int rightLength = right.length();
        if (leftLength != 0) {
            if (rightLength != 0) {
                for (int leftIndex = 0; leftIndex < leftLength; leftIndex++) {
                    unionSet.add(String.valueOf(left.charAt(leftIndex)));
                    for (int rightIndex = 0; rightIndex < rightLength; rightIndex++) {
                        if (!unionFilled) {
                            unionSet.add(String.valueOf(right.charAt(rightIndex)));
                        }
                        if (left.charAt(leftIndex) == right.charAt(rightIndex)) {
                            intersectionSet.add(String.valueOf(left.charAt(leftIndex)));
                        }
                    }
                    unionFilled = true;
                }
                return Double.valueOf(Double.valueOf((double) intersectionSet.size()).doubleValue() / Double.valueOf((double) unionSet.size()).doubleValue());
            }
        }
        return Double.valueOf(0.0d);
    }
}
