package org.apache.commons.text.similarity;

import org.apache.commons.lang3.Validate;

public class SimilarityScoreFrom<R> {
    private final CharSequence left;
    private final SimilarityScore<R> similarityScore;

    public SimilarityScoreFrom(SimilarityScore<R> similarityScore, CharSequence left) {
        Validate.isTrue(similarityScore != null, "The edit distance may not be null.", new Object[0]);
        this.similarityScore = similarityScore;
        this.left = left;
    }

    public R apply(CharSequence right) {
        return this.similarityScore.apply(this.left, right);
    }

    public CharSequence getLeft() {
        return this.left;
    }

    public SimilarityScore<R> getSimilarityScore() {
        return this.similarityScore;
    }
}
