package de.danoeh.antennapod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.bytehamster.lib.preferencesearch.SearchPreferenceResult;
import com.bytehamster.lib.preferencesearch.SearchPreferenceResultListener;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.preferences.PreferenceController;
import de.danoeh.antennapod.preferences.PreferenceController.PreferenceUI;
import java.lang.ref.WeakReference;

public class PreferenceActivity extends AppCompatActivity implements SearchPreferenceResultListener {
    public static final String PARAM_RESOURCE = "resource";
    private static WeakReference<PreferenceActivity> instance;
    private PreferenceController preferenceController;
    private final PreferenceUI preferenceUI = new C10201();

    /* renamed from: de.danoeh.antennapod.activity.PreferenceActivity$1 */
    class C10201 implements PreferenceUI {
        private PreferenceFragmentCompat fragment;

        C10201() {
        }

        public void setFragment(PreferenceFragmentCompat fragment) {
            this.fragment = fragment;
        }

        public PreferenceFragmentCompat getFragment() {
            return this.fragment;
        }

        public Preference findPreference(CharSequence key) {
            return this.fragment.findPreference(key);
        }

        public PreferenceScreen getPreferenceScreen() {
            return this.fragment.getPreferenceScreen();
        }

        public AppCompatActivity getActivity() {
            return PreferenceActivity.this;
        }
    }

    public static class MainFragment extends PreferenceFragmentCompat {
        private int screen;

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            this.screen = getArguments().getInt(PreferenceActivity.PARAM_RESOURCE);
            addPreferencesFromResource(this.screen);
            PreferenceActivity activity = (PreferenceActivity) PreferenceActivity.instance.get();
            if (activity != null && activity.preferenceController != null) {
                activity.preferenceUI.setFragment(this);
                activity.preferenceController.onCreate(this.screen);
            }
        }

        public void onResume() {
            super.onResume();
            PreferenceActivity activity = (PreferenceActivity) PreferenceActivity.instance.get();
            if (activity != null && activity.preferenceController != null) {
                activity.setTitle(PreferenceController.getTitleOfPage(this.screen));
                activity.preferenceUI.setFragment(this);
                activity.preferenceController.onResume(this.screen);
            }
        }

        public void onPause() {
            PreferenceActivity activity = (PreferenceActivity) PreferenceActivity.instance.get();
            if (this.screen == R.xml.preferences_gpodder) {
                activity.preferenceController.unregisterGpodnet();
            }
            super.onPause();
        }

        public void onStop() {
            PreferenceActivity activity = (PreferenceActivity) PreferenceActivity.instance.get();
            if (this.screen == R.xml.preferences_storage) {
                activity.preferenceController.unsubscribeExportSubscription();
            }
            super.onStop();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        instance = new WeakReference(this);
        setTheme(UserPreferences.getTheme());
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        FrameLayout root = new FrameLayout(this);
        root.setId(R.id.content);
        root.setLayoutParams(new LayoutParams(-1, -1));
        setContentView(root);
        this.preferenceController = new PreferenceController(this.preferenceUI);
        showPreferenceScreen(R.xml.preferences, false);
    }

    private void showPreferenceScreen(int screen, boolean addHistory) {
        PreferenceFragmentCompat prefFragment = new MainFragment();
        this.preferenceUI.setFragment(prefFragment);
        Bundle args = new Bundle();
        args.putInt(PARAM_RESOURCE, screen);
        prefFragment.setArguments(args);
        if (addHistory) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content, prefFragment).addToBackStack(getString(PreferenceController.getTitleOfPage(screen))).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.content, prefFragment).commit();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.preferenceController.onActivityResult(requestCode, resultCode, data);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return false;
        }
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        } else {
            getSupportFragmentManager().popBackStack();
        }
        return true;
    }

    public void onSearchResultClicked(SearchPreferenceResult result) {
        showPreferenceScreen(result.getResourceFile(), true);
        result.highlight(this.preferenceUI.getFragment());
    }
}
