package de.danoeh.antennapod.fragment.gpodnet;

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

public class GpodnetMainFragment extends Fragment {
    private static final String PREF_LAST_TAB_POSITION = "tab_position";
    private static final String TAG = "GpodnetMainFragment";
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public class GpodnetPagerAdapter extends FragmentPagerAdapter {
        private static final int NUM_PAGES = 2;
        private static final int POS_SUGGESTIONS = 2;
        private static final int POS_TAGS = 1;
        private static final int POS_TOPLIST = 0;
        final Resources resources;

        public GpodnetPagerAdapter(FragmentManager fm, Resources resources) {
            super(fm);
            this.resources = resources;
        }

        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new PodcastTopListFragment();
                case 1:
                    return new TagListFragment();
                case 2:
                    return new SuggestionListFragment();
                default:
                    return null;
            }
        }

        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return GpodnetMainFragment.this.getString(R.string.gpodnet_toplist_header);
                case 1:
                    return GpodnetMainFragment.this.getString(R.string.gpodnet_taglist_header);
                case 2:
                    return GpodnetMainFragment.this.getString(R.string.gpodnet_suggestions_header);
                default:
                    return super.getPageTitle(position);
            }
        }

        public int getCount() {
            return 2;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.pager_fragment, container, false);
        this.viewPager = (ViewPager) root.findViewById(R.id.viewpager);
        this.viewPager.setAdapter(new GpodnetPagerAdapter(getChildFragmentManager(), getResources()));
        this.tabLayout = (TabLayout) root.findViewById(R.id.sliding_tabs);
        this.tabLayout.setupWithViewPager(this.viewPager);
        return root;
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
