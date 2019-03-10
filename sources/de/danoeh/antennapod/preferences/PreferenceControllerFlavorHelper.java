package de.danoeh.antennapod.preferences;

import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.preferences.PreferenceController.PreferenceUI;

class PreferenceControllerFlavorHelper {
    PreferenceControllerFlavorHelper() {
    }

    static void setupFlavoredUI(PreferenceUI ui) {
        ui.findPreference(UserPreferences.PREF_CAST_ENABLED).setEnabled(false);
    }
}
