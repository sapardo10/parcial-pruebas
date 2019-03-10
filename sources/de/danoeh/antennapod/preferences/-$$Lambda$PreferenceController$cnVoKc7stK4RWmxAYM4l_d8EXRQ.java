package de.danoeh.antennapod.preferences;

import android.net.wifi.WifiConfiguration;
import java.util.Comparator;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PreferenceController$cnVoKc7stK4RWmxAYM4l_d8EXRQ implements Comparator {
    public static final /* synthetic */ -$$Lambda$PreferenceController$cnVoKc7stK4RWmxAYM4l_d8EXRQ INSTANCE = new -$$Lambda$PreferenceController$cnVoKc7stK4RWmxAYM4l_d8EXRQ();

    private /* synthetic */ -$$Lambda$PreferenceController$cnVoKc7stK4RWmxAYM4l_d8EXRQ() {
    }

    public final int compare(Object obj, Object obj2) {
        return PreferenceController.blankIfNull(((WifiConfiguration) obj).SSID).compareTo(PreferenceController.blankIfNull(((WifiConfiguration) obj2).SSID));
    }
}
