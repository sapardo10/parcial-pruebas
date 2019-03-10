package de.danoeh.antennapod.core.util;

import java.util.List;

public interface Permutor<E> {
    void reorder(List<E> list);
}
