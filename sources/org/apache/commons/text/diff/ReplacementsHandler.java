package org.apache.commons.text.diff;

import java.util.List;

public interface ReplacementsHandler<T> {
    void handleReplacement(int i, List<T> list, List<T> list2);
}
