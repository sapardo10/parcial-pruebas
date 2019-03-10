package de.danoeh.antennapod.fragment;

import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.danoeh.antennapod.debug.R;

public class DownloadsFragment extends Fragment {
    public static final String ARG_SELECTED_TAB = "selected_tab";
    private static final int POS_COMPLETED = 1;
    public static final int POS_LOG = 2;
    public static final int POS_RUNNING = 0;
    private static final String PREF_LAST_TAB_POSITION = "tab_position";
    public static final String TAG = "DownloadsFragment";
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static class DownloadsPagerAdapter extends FragmentPagerAdapter {
        final Resources resources;

        public DownloadsPagerAdapter(FragmentManager fm, Resources resources) {
            super(fm);
            this.resources = resources;
        }

        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new RunningDownloadsFragment();
                case 1:
                    return new CompletedDownloadsFragment();
                case 2:
                    return new DownloadLogFragment();
                default:
                    return null;
            }
        }

        public int getCount() {
            return 3;
        }

        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return this.resources.getString(R.string.downloads_running_label);
                case 1:
                    return this.resources.getString(R.string.downloads_completed_label);
                case 2:
                    return this.resources.getString(R.string.downloads_log_label);
                default:
                    return super.getPageTitle(position);
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.pager_fragment, container, false);
        this.viewPager = (ViewPager) root.findViewById(R.id.viewpager);
        this.viewPager.setAdapter(new DownloadsPagerAdapter(getChildFragmentManager(), getResources()));
        this.tabLayout = (TabLayout) root.findViewById(R.id.sliding_tabs);
        this.tabLayout.setupWithViewPager(this.viewPager);
        return root;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            this.viewPager.setCurrentItem(getArguments().getInt(ARG_SELECTED_TAB), false);
        }
    }

    public void onPause() {
        super.onPause();
        Editor editor = getActivity().getSharedPreferences(TAG, 0).edit();
        editor.putInt(PREF_LAST_TAB_POSITION, this.tabLayout.getSelectedTabPosition());
        editor.apply();
    }

    public void onStart() {
        super.onStart();
        this.viewPager.setCurrentItem(getActivity().getSharedPreferences(TAG, 0).getInt(PREF_LAST_TAB_POSITION, 0));
    }
}
