package com.bytehamster.lib.preferencesearch;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import com.bytehamster.lib.preferencesearch.SearchPreferenceFragment.HistoryClickListener;

public class SearchPreferenceActionView extends SearchView {
    private AppCompatActivity activity;
    private SearchConfiguration searchConfiguration = new SearchConfiguration();
    private SearchPreferenceFragment searchFragment;

    /* renamed from: com.bytehamster.lib.preferencesearch.SearchPreferenceActionView$2 */
    class C05432 implements OnFocusChangeListener {

        /* renamed from: com.bytehamster.lib.preferencesearch.SearchPreferenceActionView$2$1 */
        class C09751 implements HistoryClickListener {
            C09751() {
            }

            public void onHistoryEntryClicked(String entry) {
                SearchPreferenceActionView.this.setQuery(entry, false);
            }
        }

        C05432() {
        }

        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus && (SearchPreferenceActionView.this.searchFragment == null || !SearchPreferenceActionView.this.searchFragment.isVisible())) {
                SearchPreferenceActionView searchPreferenceActionView = SearchPreferenceActionView.this;
                searchPreferenceActionView.searchFragment = searchPreferenceActionView.searchConfiguration.showSearchFragment();
                SearchPreferenceActionView.this.searchFragment.setHistoryClickListener(new C09751());
            }
        }
    }

    /* renamed from: com.bytehamster.lib.preferencesearch.SearchPreferenceActionView$1 */
    class C09741 implements OnQueryTextListener {
        C09741() {
        }

        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        public boolean onQueryTextChange(String newText) {
            if (SearchPreferenceActionView.this.searchFragment != null) {
                SearchPreferenceActionView.this.searchFragment.setSearchTerm(newText);
            }
            return true;
        }
    }

    public SearchPreferenceActionView(Context context) {
        super(context);
        initView();
    }

    public SearchPreferenceActionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SearchPreferenceActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        this.searchConfiguration.setSearchBarEnabled(false);
        setOnQueryTextListener(new C09741());
        setOnQueryTextFocusChangeListener(new C05432());
    }

    public SearchConfiguration getSearchConfiguration() {
        return this.searchConfiguration;
    }

    public boolean cancelSearch() {
        setQuery("", false);
        boolean didSomething = false;
        if (!isIconified()) {
            setIconified(true);
            didSomething = true;
        }
        SearchPreferenceFragment searchPreferenceFragment = this.searchFragment;
        if (searchPreferenceFragment == null || !searchPreferenceFragment.isVisible()) {
            return didSomething;
        }
        removeFragment();
        return true;
    }

    private void removeFragment() {
        if (this.searchFragment.isVisible()) {
            FragmentManager fm = this.activity.getSupportFragmentManager();
            fm.beginTransaction().remove(this.searchFragment).commit();
            fm.popBackStack("SearchPreferenceFragment", 1);
        }
    }

    public void setActivity(AppCompatActivity activity) {
        this.searchConfiguration.setActivity(activity);
        this.activity = activity;
    }
}
