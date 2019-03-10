package de.danoeh.antennapod.core.util;

import java.util.List;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$QueueSorter$7aMMJCreyl5FVcgionW7s0S-Xyk implements Permutor {
    public static final /* synthetic */ -$$Lambda$QueueSorter$7aMMJCreyl5FVcgionW7s0S-Xyk INSTANCE = new -$$Lambda$QueueSorter$7aMMJCreyl5FVcgionW7s0S-Xyk();

    private /* synthetic */ -$$Lambda$QueueSorter$7aMMJCreyl5FVcgionW7s0S-Xyk() {
    }

    public final void reorder(List list) {
        QueueSorter.smartShuffle(list, false);
    }
}
