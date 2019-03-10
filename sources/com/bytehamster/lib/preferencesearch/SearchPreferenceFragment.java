package com.bytehamster.lib.preferencesearch;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.bytehamster.lib.preferencesearch.SearchConfiguration.SearchIndexItem;
import com.bytehamster.lib.preferencesearch.ui.AnimationUtils;
import com.bytehamster.lib.preferencesearch.ui.RevealAnimationSetting;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SearchPreferenceFragment extends Fragment implements SearchClickListener {
    private static final int MAX_HISTORY = 5;
    static final String NAME = "SearchPreferenceFragment";
    private static final String SHARED_PREFS_FILE = "preferenceSearch";
    private SearchPreferenceAdapter adapter;
    private List<HistoryItem> history;
    private HistoryClickListener historyClickListener;
    private SharedPreferences prefs;
    private List<PreferenceItem> results;
    private SearchConfiguration searchConfiguration;
    private CharSequence searchTermPreset = null;
    private PreferenceParser searcher;
    private TextWatcher textWatcher = new C05484();
    private SearchViewHolder viewHolder;

    /* renamed from: com.bytehamster.lib.preferencesearch.SearchPreferenceFragment$1 */
    class C05451 implements OnClickListener {
        C05451() {
        }

        public void onClick(View view) {
            SearchPreferenceFragment.this.viewHolder.searchView.setText("");
        }
    }

    /* renamed from: com.bytehamster.lib.preferencesearch.SearchPreferenceFragment$2 */
    class C05462 implements OnClickListener {

        /* renamed from: com.bytehamster.lib.preferencesearch.SearchPreferenceFragment$2$1 */
        class C09761 implements OnMenuItemClickListener {
            C09761() {
            }

            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == C0540R.id.clear_history) {
                    SearchPreferenceFragment.this.clearHistory();
                }
                return true;
            }
        }

        C05462() {
        }

        public void onClick(View v) {
            PopupMenu popup = new PopupMenu(SearchPreferenceFragment.this.getContext(), SearchPreferenceFragment.this.viewHolder.moreButton);
            popup.getMenuInflater().inflate(C0540R.menu.searchpreference_more, popup.getMenu());
            popup.setOnMenuItemClickListener(new C09761());
            popup.show();
        }
    }

    /* renamed from: com.bytehamster.lib.preferencesearch.SearchPreferenceFragment$3 */
    class C05473 implements Runnable {
        C05473() {
        }

        public void run() {
            SearchPreferenceFragment.this.viewHolder.searchView.requestFocus();
            InputMethodManager imm = (InputMethodManager) SearchPreferenceFragment.this.getActivity().getSystemService("input_method");
            if (imm != null) {
                imm.showSoftInput(SearchPreferenceFragment.this.viewHolder.searchView, 1);
            }
        }
    }

    /* renamed from: com.bytehamster.lib.preferencesearch.SearchPreferenceFragment$4 */
    class C05484 implements TextWatcher {
        C05484() {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            SearchPreferenceFragment.this.updateSearchResults(editable.toString());
            SearchPreferenceFragment.this.viewHolder.clearButton.setVisibility(editable.toString().isEmpty() ? 8 : 0);
        }
    }

    public interface HistoryClickListener {
        void onHistoryEntryClicked(String str);
    }

    private class SearchViewHolder {
        private CardView cardView;
        private ImageView clearButton;
        private ImageView moreButton;
        private TextView noResults;
        private RecyclerView recyclerView;
        private EditText searchView;

        SearchViewHolder(View root) {
            this.searchView = (EditText) root.findViewById(C0540R.id.search);
            this.clearButton = (ImageView) root.findViewById(C0540R.id.clear);
            this.recyclerView = (RecyclerView) root.findViewById(C0540R.id.list);
            this.moreButton = (ImageView) root.findViewById(C0540R.id.more);
            this.noResults = (TextView) root.findViewById(C0540R.id.no_results);
            this.cardView = (CardView) root.findViewById(C0540R.id.search_card);
        }
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.prefs = getContext().getSharedPreferences(SHARED_PREFS_FILE, 0);
        this.searcher = new PreferenceParser(getContext());
        this.searchConfiguration = SearchConfiguration.fromBundle(getArguments());
        Iterator it = this.searchConfiguration.getFiles().iterator();
        while (it.hasNext()) {
            this.searcher.addResourceFile((SearchIndexItem) it.next());
        }
        loadHistory();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(C0540R.layout.searchpreference_fragment, container, false);
        this.viewHolder = new SearchViewHolder(rootView);
        this.viewHolder.clearButton.setOnClickListener(new C05451());
        if (this.searchConfiguration.isHistoryEnabled()) {
            this.viewHolder.moreButton.setVisibility(0);
        }
        this.viewHolder.moreButton.setOnClickListener(new C05462());
        this.viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.viewHolder.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), 1));
        this.adapter = new SearchPreferenceAdapter();
        this.adapter.setSearchConfiguration(this.searchConfiguration);
        this.adapter.setOnItemClickListener(this);
        this.viewHolder.recyclerView.setAdapter(this.adapter);
        this.viewHolder.searchView.addTextChangedListener(this.textWatcher);
        if (!this.searchConfiguration.isSearchBarEnabled()) {
            this.viewHolder.cardView.setVisibility(8);
        }
        if (this.searchTermPreset != null) {
            this.viewHolder.searchView.setText(this.searchTermPreset);
        }
        RevealAnimationSetting anim = this.searchConfiguration.getRevealAnimationSetting();
        if (anim != null) {
            AnimationUtils.registerCircularRevealAnimation(getContext(), rootView, anim);
        }
        return rootView;
    }

    private void loadHistory() {
        this.history = new ArrayList();
        if (this.searchConfiguration.isHistoryEnabled()) {
            int size = this.prefs.getInt("history_size", 0);
            for (int i = 0; i < size; i++) {
                String title = this.prefs;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("history_");
                stringBuilder.append(i);
                this.history.add(new HistoryItem(title.getString(stringBuilder.toString(), null)));
            }
        }
    }

    private void saveHistory() {
        Editor editor = this.prefs.edit();
        editor.putInt("history_size", this.history.size());
        for (int i = 0; i < this.history.size(); i++) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("history_");
            stringBuilder.append(i);
            editor.putString(stringBuilder.toString(), ((HistoryItem) this.history.get(i)).getTerm());
        }
        editor.apply();
    }

    private void clearHistory() {
        this.viewHolder.searchView.setText("");
        this.history.clear();
        saveHistory();
        updateSearchResults("");
    }

    private void addHistoryEntry(String entry) {
        HistoryItem newItem = new HistoryItem(entry);
        if (!this.history.contains(newItem)) {
            if (this.history.size() >= 5) {
                List list = this.history;
                list.remove(list.size() - 1);
            }
            this.history.add(0, newItem);
            saveHistory();
            updateSearchResults(this.viewHolder.searchView.getText().toString());
        }
    }

    public void onResume() {
        super.onResume();
        updateSearchResults(this.viewHolder.searchView.getText().toString());
        if (this.searchConfiguration.isSearchBarEnabled()) {
            showKeyboard();
        }
    }

    private void showKeyboard() {
        this.viewHolder.searchView.post(new C05473());
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService("input_method");
        if (view != null && imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void setSearchTerm(CharSequence term) {
        SearchViewHolder searchViewHolder = this.viewHolder;
        if (searchViewHolder != null) {
            searchViewHolder.searchView.setText(term);
        } else {
            this.searchTermPreset = term;
        }
    }

    private void updateSearchResults(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            showHistory();
            return;
        }
        this.results = this.searcher.searchFor(keyword, this.searchConfiguration.isFuzzySearchEnabled());
        this.adapter.setContent(new ArrayList(this.results));
        setEmptyViewShown(this.results.isEmpty());
    }

    private void setEmptyViewShown(boolean shown) {
        if (shown) {
            this.viewHolder.noResults.setVisibility(0);
            this.viewHolder.recyclerView.setVisibility(8);
            return;
        }
        this.viewHolder.noResults.setVisibility(8);
        this.viewHolder.recyclerView.setVisibility(0);
    }

    private void showHistory() {
        this.viewHolder.noResults.setVisibility(8);
        this.viewHolder.recyclerView.setVisibility(0);
        this.adapter.setContent(new ArrayList(this.history));
        setEmptyViewShown(this.history.isEmpty());
    }

    public void onItemClicked(ListItem item, int position) {
        if (item.getType() == 1) {
            CharSequence text = ((HistoryItem) item).getTerm();
            this.viewHolder.searchView.setText(text);
            this.viewHolder.searchView.setSelection(text.length());
            HistoryClickListener historyClickListener = this.historyClickListener;
            if (historyClickListener != null) {
                historyClickListener.onHistoryEntryClicked(text.toString());
            }
            return;
        }
        addHistoryEntry(this.viewHolder.searchView.getText().toString());
        hideKeyboard();
        try {
            SearchPreferenceResultListener callback = (SearchPreferenceResultListener) getActivity();
            PreferenceItem r = (PreferenceItem) this.results.get(position);
            String screen = null;
            if (!r.keyBreadcrumbs.isEmpty()) {
                screen = (String) r.keyBreadcrumbs.get(r.keyBreadcrumbs.size() - 1);
            }
            callback.onSearchResultClicked(new SearchPreferenceResult(r.key, r.resId, screen));
        } catch (ClassCastException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getActivity().toString());
            stringBuilder.append(" must implement SearchPreferenceResultListener");
            throw new ClassCastException(stringBuilder.toString());
        }
    }

    public void setHistoryClickListener(HistoryClickListener historyClickListener) {
        this.historyClickListener = historyClickListener;
    }
}
