package com.bytehamster.lib.preferencesearch;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class SearchPreference extends Preference implements OnClickListener {
    private SearchConfiguration searchConfiguration = new SearchConfiguration();

    public SearchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(C0540R.layout.searchpreference_preference);
    }

    public SearchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(C0540R.layout.searchpreference_preference);
    }

    public SearchPreference(Context context) {
        super(context);
        setLayoutResource(C0540R.layout.searchpreference_preference);
    }

    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        EditText searchText = (EditText) holder.findViewById(C0540R.id.search);
        searchText.setFocusable(false);
        searchText.setInputType(0);
        searchText.setOnClickListener(this);
        holder.findViewById(C0540R.id.search_card).setOnClickListener(this);
    }

    public void onClick(View view) {
        getSearchConfiguration().showSearchFragment();
    }

    public SearchConfiguration getSearchConfiguration() {
        return this.searchConfiguration;
    }
}
