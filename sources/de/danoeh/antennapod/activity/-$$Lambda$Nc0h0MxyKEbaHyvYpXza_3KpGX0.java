package de.danoeh.antennapod.activity;

import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.util.Consumer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$Nc0h0MxyKEbaHyvYpXza_3KpGX0 implements Consumer {
    public static final /* synthetic */ -$$Lambda$Nc0h0MxyKEbaHyvYpXza_3KpGX0 INSTANCE = new -$$Lambda$Nc0h0MxyKEbaHyvYpXza_3KpGX0();

    private /* synthetic */ -$$Lambda$Nc0h0MxyKEbaHyvYpXza_3KpGX0() {
    }

    public final void accept(Object obj) {
        UserPreferences.setFastForwardSecs(((Integer) obj).intValue());
    }
}
