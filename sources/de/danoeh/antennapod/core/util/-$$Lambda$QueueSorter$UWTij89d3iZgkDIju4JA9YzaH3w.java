package de.danoeh.antennapod.core.util;

import java.util.List;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$QueueSorter$UWTij89d3iZgkDIju4JA9YzaH3w implements Permutor {
    public static final /* synthetic */ -$$Lambda$QueueSorter$UWTij89d3iZgkDIju4JA9YzaH3w INSTANCE = new -$$Lambda$QueueSorter$UWTij89d3iZgkDIju4JA9YzaH3w();

    private /* synthetic */ -$$Lambda$QueueSorter$UWTij89d3iZgkDIju4JA9YzaH3w() {
    }

    public final void reorder(List list) {
        QueueSorter.smartShuffle(list, true);
    }
}
