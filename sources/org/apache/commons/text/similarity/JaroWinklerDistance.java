package org.apache.commons.text.similarity;

import java.util.Arrays;

public class JaroWinklerDistance implements SimilarityScore<Double> {
    public static final int INDEX_NOT_FOUND = -1;

    public Double apply(CharSequence left, CharSequence right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }
        int[] mtp = matches(left, right);
        double m = (double) mtp[0];
        if (m == 0.0d) {
            return Double.valueOf(0.0d);
        }
        double length = (double) left.length();
        Double.isNaN(m);
        Double.isNaN(length);
        length = m / length;
        double length2 = (double) right.length();
        Double.isNaN(m);
        Double.isNaN(length2);
        length += m / length2;
        length2 = (double) mtp[1];
        Double.isNaN(m);
        Double.isNaN(length2);
        length2 = m - length2;
        Double.isNaN(m);
        length = (length + (length2 / m)) / 3.0d;
        if (length < 0.7d) {
            length2 = length;
        } else {
            double d = (double) mtp[3];
            Double.isNaN(d);
            length2 = Math.min(0.1d, 1.0d / d);
            d = (double) mtp[2];
            Double.isNaN(d);
            length2 = ((length2 * d) * (1.0d - length)) + length;
        }
        return Double.valueOf(length2);
    }

    protected static int[] matches(CharSequence first, CharSequence second) {
        CharSequence max;
        CharSequence min;
        int xi;
        int xn;
        int i;
        if (first.length() > second.length()) {
            max = first;
            min = second;
        } else {
            max = second;
            min = first;
        }
        int range = Math.max((max.length() / 2) - 1, 0);
        int[] matchIndexes = new int[min.length()];
        Arrays.fill(matchIndexes, -1);
        boolean[] matchFlags = new boolean[max.length()];
        int matches = 0;
        for (int mi = 0; mi < min.length(); mi++) {
            char c1 = min.charAt(mi);
            xi = Math.max(mi - range, 0);
            xn = Math.min((mi + range) + 1, max.length());
            while (xi < xn) {
                if (!matchFlags[xi] && c1 == max.charAt(xi)) {
                    matchIndexes[mi] = xi;
                    matchFlags[xi] = true;
                    matches++;
                    break;
                }
                xi++;
            }
        }
        char[] ms1 = new char[matches];
        char[] ms2 = new char[matches];
        xn = 0;
        for (xi = 0; xi < min.length(); xi++) {
            if (matchIndexes[xi] != -1) {
                ms1[xn] = min.charAt(xi);
                xn++;
            }
        }
        xi = 0;
        for (i = 0; i < max.length(); i++) {
            if (matchFlags[i]) {
                ms2[xi] = max.charAt(i);
                xi++;
            }
        }
        i = 0;
        for (xi = 0; xi < ms1.length; xi++) {
            if (ms1[xi] != ms2[xi]) {
                i++;
            }
        }
        xi = 0;
        for (xn = 0; xn < min.length(); xn++) {
            if (first.charAt(xn) != second.charAt(xn)) {
                break;
            }
            xi++;
        }
        CharSequence charSequence = first;
        CharSequence charSequence2 = second;
        return new int[]{matches, i / 2, xi, max.length()};
    }
}
