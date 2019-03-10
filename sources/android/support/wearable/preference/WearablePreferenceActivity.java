package android.support.wearable.preference;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceFragment.OnPreferenceStartFragmentCallback;
import android.support.annotation.CallSuper;
import android.support.wearable.activity.WearableActivity;
import android.text.TextUtils;

@TargetApi(23)
public class WearablePreferenceActivity extends WearableActivity implements OnPreferenceStartFragmentCallback {
    @CallSuper
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            String initialFragment = intent.getStringExtra(":android:show_fragment");
            Bundle initialArguments = intent.getBundleExtra(":android:show_fragment_args");
            if (initialFragment != null) {
                startPreferenceFragment(Fragment.instantiate(this, initialFragment, initialArguments), false);
            }
        }
    }

    public boolean onPreferenceStartFragment(PreferenceFragment caller, Preference pref) {
        startActivity(onBuildStartFragmentIntent(pref.getFragment(), pref.getExtras(), 0));
        return true;
    }

    protected void onStart() {
        super.onStart();
        Fragment fragment = getFragmentManager().findFragmentById(16908290);
        if (fragment instanceof PreferenceFragment) {
            CharSequence title = ((PreferenceFragment) fragment).getPreferenceScreen().getTitle();
            if (!TextUtils.isEmpty(title) && !TextUtils.equals(title, getTitle())) {
                setTitle(title);
            }
        }
    }

    public void startPreferenceFragment(Fragment fragment, boolean push) {
        if (push) {
            startActivity(onBuildStartFragmentIntent(fragment.getClass().getName(), fragment.getArguments(), 0));
            return;
        }
        getFragmentManager().popBackStack(null, 1);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(16908290, fragment);
        transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commitAllowingStateLoss();
    }

    public Intent onBuildStartFragmentIntent(String fragmentName, Bundle args, int titleRes) {
        return new Intent("android.intent.action.MAIN").setClass(this, WearablePreferenceActivity.class).putExtra(":android:show_fragment", fragmentName).putExtra(":android:show_fragment_args", args).putExtra(":android:show_fragment_title", titleRes).putExtra(":android:no_headers", true);
    }
}
