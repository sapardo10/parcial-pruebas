package de.danoeh.antennapod.activity;

import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.util.Consumer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PUDwvmQNaFubEt5TwXSlHHv6TUs implements Consumer {
    public static final /* synthetic */ -$$Lambda$PUDwvmQNaFubEt5TwXSlHHv6TUs INSTANCE = new -$$Lambda$PUDwvmQNaFubEt5TwXSlHHv6TUs();

    private /* synthetic */ -$$Lambda$PUDwvmQNaFubEt5TwXSlHHv6TUs() {
    }

    public final void accept(Object obj) {
        UserPreferences.setRewindSecs(((Integer) obj).intValue());
    }
}
