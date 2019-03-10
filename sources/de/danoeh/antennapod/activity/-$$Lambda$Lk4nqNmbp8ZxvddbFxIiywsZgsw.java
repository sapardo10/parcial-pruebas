package de.danoeh.antennapod.activity;

import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.util.Supplier;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$Lk4nqNmbp8ZxvddbFxIiywsZgsw implements Supplier {
    public static final /* synthetic */ -$$Lambda$Lk4nqNmbp8ZxvddbFxIiywsZgsw INSTANCE = new -$$Lambda$Lk4nqNmbp8ZxvddbFxIiywsZgsw();

    private /* synthetic */ -$$Lambda$Lk4nqNmbp8ZxvddbFxIiywsZgsw() {
    }

    public final Object get() {
        return Integer.valueOf(UserPreferences.getRewindSecs());
    }
}
