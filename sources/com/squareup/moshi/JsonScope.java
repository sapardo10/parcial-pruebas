package com.squareup.moshi;

final class JsonScope {
    static final int CLOSED = 8;
    static final int DANGLING_NAME = 4;
    static final int EMPTY_ARRAY = 1;
    static final int EMPTY_DOCUMENT = 6;
    static final int EMPTY_OBJECT = 3;
    static final int NONEMPTY_ARRAY = 2;
    static final int NONEMPTY_DOCUMENT = 7;
    static final int NONEMPTY_OBJECT = 5;

    private JsonScope() {
    }

    static String getPath(int stackSize, int[] stack, String[] pathNames, int[] pathIndices) {
        StringBuilder result = new StringBuilder().append('$');
        for (int i = 0; i < stackSize; i++) {
            switch (stack[i]) {
                case 1:
                case 2:
                    result.append('[');
                    result.append(pathIndices[i]);
                    result.append(']');
                    break;
                case 3:
                case 4:
                case 5:
                    result.append('.');
                    if (pathNames[i] == null) {
                        break;
                    }
                    result.append(pathNames[i]);
                    break;
                default:
                    break;
            }
        }
        return result.toString();
    }
}
