package com.bytehamster.lib.preferencesearch;

import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

public class SearchPreferenceResult {
    private final int file;
    private final String key;
    private final String screen;

    SearchPreferenceResult(String key, int file, String screen) {
        this.key = key;
        this.file = file;
        this.screen = screen;
    }

    public String getKey() {
        return this.key;
    }

    public int getResourceFile() {
        return this.file;
    }

    public String getScreen() {
        return this.screen;
    }

    public void highlight(PreferenceFragmentCompat prefsFragment) {
        highlight(prefsFragment, -12627531);
    }

    public void highlight(final PreferenceFragmentCompat prefsFragment, @ColorInt final int color) {
        new Handler().post(new Runnable() {
            public void run() {
                SearchPreferenceResult.this.doHighlight(prefsFragment, color);
            }
        });
    }

    private void doHighlight(PreferenceFragmentCompat prefsFragment, @ColorInt int color) {
        final Preference prefResult = prefsFragment.findPreference(getKey());
        if (prefResult == null) {
            Log.e("doHighlight", "Preference not found on given screen");
            return;
        }
        Drawable arrow;
        if ((ViewCompat.MEASURED_STATE_MASK & color) == 0) {
            color -= 16777216;
        }
        prefsFragment.scrollToPreference(prefResult);
        final Drawable oldIcon = prefResult.getIcon();
        final boolean oldSpaceReserved = prefResult.isIconSpaceReserved();
        if (VERSION.SDK_INT >= 21) {
            arrow = prefsFragment.getContext().getDrawable(C0540R.drawable.searchpreference_ic_arrow_right);
        } else {
            arrow = prefsFragment.getResources().getDrawable(C0540R.drawable.searchpreference_ic_arrow_right);
        }
        arrow.setColorFilter(color, Mode.SRC_IN);
        prefResult.setIcon(arrow);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                prefResult.setIcon(oldIcon);
                prefResult.setIconSpaceReserved(oldSpaceReserved);
            }
        }, 1000);
    }

    public void closeSearchPage(AppCompatActivity activity) {
        FragmentManager fm = activity.getSupportFragmentManager();
        fm.beginTransaction().remove(fm.findFragmentByTag("SearchPreferenceFragment")).commit();
    }
}
