package org.apache.commons.text.similarity;

public interface EditDistance<R> extends SimilarityScore<R> {
    R apply(CharSequence charSequence, CharSequence charSequence2);
}
