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
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.debug.R;

public class EpisodesFragment extends Fragment {
    private static final int POS_ALL_EPISODES = 1;
    private static final int POS_FAV_EPISODES = 2;
    private static final int POS_NEW_EPISODES = 0;
    private static final String PREF_LAST_TAB_POSITION = "tab_position";
    public static final String TAG = "EpisodesFragment";
    private static final int TOTAL_COUNT = 3;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static class EpisodesPagerAdapter extends FragmentPagerAdapter {
        private final AllEpisodesFragment[] fragments = new AllEpisodesFragment[]{new NewEpisodesFragment(), new AllEpisodesFragment(), new FavoriteEpisodesFragment()};
        private final Resources resources;

        public EpisodesPagerAdapter(FragmentManager fm, Resources resources) {
            super(fm);
            this.resources = resources;
        }

        public Fragment getItem(int position) {
            return this.fragments[position];
        }

        public int getCount() {
            return 3;
        }

        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return this.resources.getString(R.string.new_episodes_label);
                case 1:
                    return this.resources.getString(R.string.all_episodes_short_label);
                case 2:
                    return this.resources.getString(R.string.favorite_episodes_label);
                default:
                    return super.getPageTitle(position);
            }
        }

        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            int i = 0;
            while (i < 3) {
                this.fragments[i].isMenuInvalidationAllowed = i == position;
                i++;
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle((int) R.string.episodes_label);
        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);
        this.viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        this.viewPager.setAdapter(new EpisodesPagerAdapter(getChildFragmentManager(), getResources()));
        this.tabLayout = (TabLayout) rootView.findViewById(R.id.sliding_tabs);
        this.tabLayout.setupWithViewPager(this.viewPager);
        return rootView;
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
