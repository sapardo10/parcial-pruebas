package org.apache.commons.text.similarity;

public class LongestCommonSubsequenceDistance implements EditDistance<Integer> {
    private final LongestCommonSubsequence longestCommonSubsequence = new LongestCommonSubsequence();

    public Integer apply(CharSequence left, CharSequence right) {
        if (left != null && right != null) {
            return Integer.valueOf((left.length() + right.length()) - (this.longestCommonSubsequence.apply(left, right).intValue() * 2));
        }
        throw new IllegalArgumentException("Inputs must not be null");
    }
}
