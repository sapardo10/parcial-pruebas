package com.bytehamster.lib.preferencesearch;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.XmlRes;
import android.support.v7.app.AppCompatActivity;
import com.bytehamster.lib.preferencesearch.ui.RevealAnimationSetting;
import java.util.ArrayList;

public class SearchConfiguration {
    private static final String ARGUMENT_BREADCRUMBS_ENABLED = "breadcrumbs_enabled";
    private static final String ARGUMENT_FUZZY_ENABLED = "fuzzy";
    private static final String ARGUMENT_HISTORY_ENABLED = "history_enabled";
    private static final String ARGUMENT_INDEX_ITEMS = "items";
    private static final String ARGUMENT_REVEAL_ANIMATION_SETTING = "reveal_anim_setting";
    private static final String ARGUMENT_SEARCH_BAR_ENABLED = "search_bar_enabled";
    private AppCompatActivity activity;
    private boolean breadcrumbsEnabled = false;
    private int containerResId = 16908290;
    private boolean fuzzySearchEnabled = true;
    private boolean historyEnabled = true;
    private ArrayList<SearchIndexItem> itemsToIndex = new ArrayList();
    private RevealAnimationSetting revealAnimationSetting = null;
    private boolean searchBarEnabled = true;

    public static class SearchIndexItem implements Parcelable {
        public static final Creator<SearchIndexItem> CREATOR = new C05421();
        private String breadcrumb;
        @XmlRes
        private final int resId;
        private final SearchConfiguration searchConfiguration;

        /* renamed from: com.bytehamster.lib.preferencesearch.SearchConfiguration$SearchIndexItem$1 */
        static class C05421 implements Creator<SearchIndexItem> {
            C05421() {
            }

            public SearchIndexItem createFromParcel(Parcel in) {
                return new SearchIndexItem(in);
            }

            public SearchIndexItem[] newArray(int size) {
                return new SearchIndexItem[size];
            }
        }

        private SearchIndexItem(@XmlRes int resId, SearchConfiguration searchConfiguration) {
            this.breadcrumb = "";
            this.resId = resId;
            this.searchConfiguration = searchConfiguration;
        }

        public SearchIndexItem addBreadcrumb(@StringRes int breadcrumb) {
            assertNotParcel();
            return addBreadcrumb(this.searchConfiguration.activity.getString(breadcrumb));
        }

        public SearchIndexItem addBreadcrumb(String breadcrumb) {
            assertNotParcel();
            this.breadcrumb = Breadcrumb.concat(this.breadcrumb, breadcrumb);
            return this;
        }

        private void assertNotParcel() {
            if (this.searchConfiguration == null) {
                throw new IllegalStateException("SearchIndexItems that are restored from parcel can not be modified.");
            }
        }

        @XmlRes
        int getResId() {
            return this.resId;
        }

        String getBreadcrumb() {
            return this.breadcrumb;
        }

        private SearchIndexItem(Parcel parcel) {
            this.breadcrumb = "";
            this.breadcrumb = parcel.readString();
            this.resId = parcel.readInt();
            this.searchConfiguration = null;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.breadcrumb);
            dest.writeInt(this.resId);
        }

        public int describeContents() {
            return 0;
        }
    }

    SearchConfiguration() {
    }

    public SearchConfiguration(AppCompatActivity activity) {
        setActivity(activity);
    }

    public SearchPreferenceFragment showSearchFragment() {
        if (this.activity != null) {
            Bundle arguments = toBundle();
            SearchPreferenceFragment fragment = new SearchPreferenceFragment();
            fragment.setArguments(arguments);
            this.activity.getSupportFragmentManager().beginTransaction().add(this.containerResId, fragment, "SearchPreferenceFragment").addToBackStack("SearchPreferenceFragment").commit();
            return fragment;
        }
        throw new IllegalStateException("setActivity() not called");
    }

    private Bundle toBundle() {
        Bundle arguments = new Bundle();
        arguments.putParcelableArrayList(ARGUMENT_INDEX_ITEMS, this.itemsToIndex);
        arguments.putBoolean(ARGUMENT_HISTORY_ENABLED, this.historyEnabled);
        arguments.putParcelable(ARGUMENT_REVEAL_ANIMATION_SETTING, this.revealAnimationSetting);
        arguments.putBoolean(ARGUMENT_FUZZY_ENABLED, this.fuzzySearchEnabled);
        arguments.putBoolean(ARGUMENT_BREADCRUMBS_ENABLED, this.breadcrumbsEnabled);
        arguments.putBoolean(ARGUMENT_SEARCH_BAR_ENABLED, this.searchBarEnabled);
        return arguments;
    }

    static SearchConfiguration fromBundle(Bundle bundle) {
        SearchConfiguration config = new SearchConfiguration();
        config.itemsToIndex = bundle.getParcelableArrayList(ARGUMENT_INDEX_ITEMS);
        config.historyEnabled = bundle.getBoolean(ARGUMENT_HISTORY_ENABLED);
        config.revealAnimationSetting = (RevealAnimationSetting) bundle.getParcelable(ARGUMENT_REVEAL_ANIMATION_SETTING);
        config.fuzzySearchEnabled = bundle.getBoolean(ARGUMENT_FUZZY_ENABLED);
        config.breadcrumbsEnabled = bundle.getBoolean(ARGUMENT_BREADCRUMBS_ENABLED);
        config.searchBarEnabled = bundle.getBoolean(ARGUMENT_SEARCH_BAR_ENABLED);
        return config;
    }

    public void setActivity(@NonNull AppCompatActivity activity) {
        this.activity = activity;
        if (!(activity instanceof SearchPreferenceResultListener)) {
            throw new IllegalArgumentException("Activity must implement SearchPreferenceResultListener");
        }
    }

    public void setHistoryEnabled(boolean historyEnabled) {
        this.historyEnabled = historyEnabled;
    }

    public void setFuzzySearchEnabled(boolean fuzzySearchEnabled) {
        this.fuzzySearchEnabled = fuzzySearchEnabled;
    }

    public void setBreadcrumbsEnabled(boolean breadcrumbsEnabled) {
        this.breadcrumbsEnabled = breadcrumbsEnabled;
    }

    public void setSearchBarEnabled(boolean searchBarEnabled) {
        this.searchBarEnabled = searchBarEnabled;
    }

    public void setFragmentContainerViewId(@IdRes int containerResId) {
        this.containerResId = containerResId;
    }

    public void useAnimation(int centerX, int centerY, int width, int height, @ColorInt int colorAccent) {
        this.revealAnimationSetting = new RevealAnimationSetting(centerX, centerY, width, height, colorAccent);
    }

    public SearchIndexItem index(@XmlRes int resId) {
        SearchIndexItem item = new SearchIndexItem(resId, this);
        this.itemsToIndex.add(item);
        return item;
    }

    ArrayList<SearchIndexItem> getFiles() {
        return this.itemsToIndex;
    }

    boolean isHistoryEnabled() {
        return this.historyEnabled;
    }

    boolean isBreadcrumbsEnabled() {
        return this.breadcrumbsEnabled;
    }

    boolean isFuzzySearchEnabled() {
        return this.fuzzySearchEnabled;
    }

    boolean isSearchBarEnabled() {
        return this.searchBarEnabled;
    }

    RevealAnimationSetting getRevealAnimationSetting() {
        return this.revealAnimationSetting;
    }
}
