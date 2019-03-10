package org.apache.commons.text.similarity;

public class JaccardDistance implements EditDistance<Double> {
    private final JaccardSimilarity jaccardSimilarity = new JaccardSimilarity();

    public Double apply(CharSequence left, CharSequence right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        double round = (double) Math.round((1.0d - this.jaccardSimilarity.apply(left, right).doubleValue()) * 100.0d);
        Double.isNaN(round);
        return Double.valueOf(round / 100.0d);
    }
}
