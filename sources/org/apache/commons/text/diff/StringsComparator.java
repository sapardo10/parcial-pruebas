package org.apache.commons.text.diff;

public class StringsComparator {
    private final String left;
    private final String right;
    private final int[] vDown;
    private final int[] vUp;

    private static class Snake {
        private final int diag;
        private final int end;
        private final int start;

        Snake(int start, int end, int diag) {
            this.start = start;
            this.end = end;
            this.diag = diag;
        }

        public int getStart() {
            return this.start;
        }

        public int getEnd() {
            return this.end;
        }

        public int getDiag() {
            return this.diag;
        }
    }

    public StringsComparator(String left, String right) {
        this.left = left;
        this.right = right;
        int size = (left.length() + right.length()) + 2;
        this.vDown = new int[size];
        this.vUp = new int[size];
    }

    public EditScript<Character> getScript() {
        EditScript<Character> script = new EditScript();
        buildScript(0, this.left.length(), 0, this.right.length(), script);
        return script;
    }

    private void buildScript(int start1, int end1, int start2, int end2, EditScript<Character> script) {
        int i;
        Snake middle = getMiddleSnake(start1, end1, start2, end2);
        if (middle != null) {
            if (middle.getStart() == end1) {
                if (middle.getDiag() != end1 - end2) {
                }
            }
            if (middle.getEnd() != start1 || middle.getDiag() != start1 - start2) {
                buildScript(start1, middle.getStart(), start2, middle.getStart() - middle.getDiag(), script);
                for (i = middle.getStart(); i < middle.getEnd(); i++) {
                    script.append(new KeepCommand(Character.valueOf(this.left.charAt(i))));
                }
                buildScript(middle.getEnd(), end1, middle.getEnd() - middle.getDiag(), end2, script);
                return;
            }
        }
        i = start1;
        int j = start2;
        while (true) {
            if (i >= end1) {
                if (j >= end2) {
                    return;
                }
            }
            if (i < end1 && j < end2 && this.left.charAt(i) == this.right.charAt(j)) {
                script.append(new KeepCommand(Character.valueOf(this.left.charAt(i))));
                i++;
                j++;
            } else if (end1 - start1 > end2 - start2) {
                script.append(new DeleteCommand(Character.valueOf(this.left.charAt(i))));
                i++;
            } else {
                script.append(new InsertCommand(Character.valueOf(this.right.charAt(j))));
                j++;
            }
        }
    }

    private Snake getMiddleSnake(int start1, int end1, int start2, int end2) {
        StringsComparator stringsComparator = this;
        int i = start1;
        int i2 = end1;
        int i3 = start2;
        int i4 = end2;
        int m = i2 - i;
        int n = i4 - i3;
        int i5;
        if (m == 0) {
            i5 = n;
        } else if (n == 0) {
            r16 = m;
            i5 = n;
        } else {
            int delta = m - n;
            int sum = n + m;
            int offset = (sum % 2 == 0 ? sum : sum + 1) / 2;
            stringsComparator.vDown[offset + 1] = i;
            stringsComparator.vUp[offset + 1] = i2 + 1;
            int d = 0;
            while (d <= offset) {
                int i6;
                int[] iArr;
                int y;
                int k = -d;
                while (k <= d) {
                    int x;
                    i6 = k + offset;
                    if (k != (-d)) {
                        if (k != d) {
                            iArr = stringsComparator.vDown;
                            if (iArr[i6 - 1] < iArr[i6 + 1]) {
                            }
                        }
                        iArr = stringsComparator.vDown;
                        iArr[i6] = iArr[i6 - 1] + 1;
                        x = stringsComparator.vDown[i6];
                        y = ((x - i) + i3) - k;
                        while (x < i2 && y < i4) {
                            r16 = m;
                            if (stringsComparator.left.charAt(x) == stringsComparator.right.charAt(y)) {
                                break;
                            }
                            x++;
                            stringsComparator.vDown[i6] = x;
                            y++;
                            m = r16;
                        }
                        r16 = m;
                        if (delta % 2 != 0 || delta - d > k || k > delta + d) {
                            i5 = n;
                        } else {
                            m = stringsComparator.vUp;
                            i5 = n;
                            if (m[i6 - delta] <= stringsComparator.vDown[i6]) {
                                return buildSnake(m[i6 - delta], (k + i) - i3, i2, i4);
                            }
                        }
                        k += 2;
                        m = r16;
                        n = i5;
                    }
                    iArr = stringsComparator.vDown;
                    iArr[i6] = iArr[i6 + 1];
                    x = stringsComparator.vDown[i6];
                    y = ((x - i) + i3) - k;
                    while (x < i2) {
                        r16 = m;
                        if (stringsComparator.left.charAt(x) == stringsComparator.right.charAt(y)) {
                            break;
                        }
                        x++;
                        stringsComparator.vDown[i6] = x;
                        y++;
                        m = r16;
                    }
                    r16 = m;
                    if (delta % 2 != 0) {
                    }
                    i5 = n;
                    k += 2;
                    m = r16;
                    n = i5;
                }
                r16 = m;
                i5 = n;
                m = delta - d;
                while (m <= delta + d) {
                    int[] iArr2;
                    n = (m + offset) - delta;
                    if (m != delta - d) {
                        if (m != delta + d) {
                            iArr2 = stringsComparator.vUp;
                            if (iArr2[n + 1] <= iArr2[n - 1]) {
                            }
                        }
                        iArr2 = stringsComparator.vUp;
                        iArr2[n] = iArr2[n - 1];
                        k = stringsComparator.vUp[n] - 1;
                        i6 = ((k - i) + i3) - m;
                        while (k >= i && i6 >= i3) {
                            if (stringsComparator.left.charAt(k) == stringsComparator.right.charAt(i6)) {
                                break;
                            }
                            y = k - 1;
                            stringsComparator.vUp[n] = k;
                            i6--;
                            k = y;
                        }
                        if (delta % 2 != 0 && (-d) <= m && m <= d) {
                            iArr = stringsComparator.vUp;
                            if (iArr[n] <= stringsComparator.vDown[n + delta]) {
                                return buildSnake(iArr[n], (m + i) - i3, i2, i4);
                            }
                        }
                        m += 2;
                    }
                    iArr2 = stringsComparator.vUp;
                    iArr2[n] = iArr2[n + 1] - 1;
                    k = stringsComparator.vUp[n] - 1;
                    i6 = ((k - i) + i3) - m;
                    while (k >= i) {
                        if (stringsComparator.left.charAt(k) == stringsComparator.right.charAt(i6)) {
                            break;
                        }
                        y = k - 1;
                        stringsComparator.vUp[n] = k;
                        i6--;
                        k = y;
                    }
                    if (delta % 2 != 0) {
                    }
                    m += 2;
                }
                d++;
                m = r16;
                n = i5;
            }
            i5 = n;
            throw new RuntimeException("Internal Error");
        }
        return null;
    }

    private Snake buildSnake(int start, int diag, int end1, int end2) {
        int end = start;
        while (end - diag < end2 && end < end1) {
            if (this.left.charAt(end) != this.right.charAt(end - diag)) {
                break;
            }
            end++;
        }
        return new Snake(start, end, diag);
    }
}
