package de.danoeh.antennapod.activity;

import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.util.Supplier;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$4cqA4kZ7HqFsP6DrP7kwbpB7jRM implements Supplier {
    public static final /* synthetic */ -$$Lambda$4cqA4kZ7HqFsP6DrP7kwbpB7jRM INSTANCE = new -$$Lambda$4cqA4kZ7HqFsP6DrP7kwbpB7jRM();

    private /* synthetic */ -$$Lambda$4cqA4kZ7HqFsP6DrP7kwbpB7jRM() {
    }

    public final Object get() {
        return Integer.valueOf(UserPreferences.getFastForwardSecs());
    }
}
