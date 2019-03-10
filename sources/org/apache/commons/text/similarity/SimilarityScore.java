package org.apache.commons.text.similarity;

public interface SimilarityScore<R> {
    R apply(CharSequence charSequence, CharSequence charSequence2);
}
