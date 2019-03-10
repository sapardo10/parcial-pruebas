package com.bytehamster.lib.preferencesearch;

import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

class SearchPreferenceAdapter extends Adapter<ViewHolder> {
    private List<ListItem> dataset = new ArrayList();
    private SearchClickListener onItemClickListener;
    private SearchConfiguration searchConfiguration;

    interface SearchClickListener {
        void onItemClicked(ListItem listItem, int i);
    }

    static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        View root;

        ViewHolder(View v) {
            super(v);
            this.root = v;
        }
    }

    static class HistoryViewHolder extends ViewHolder {
        TextView term;

        HistoryViewHolder(View v) {
            super(v);
            this.term = (TextView) v.findViewById(C0540R.id.term);
        }
    }

    static class PreferenceViewHolder extends ViewHolder {
        TextView breadcrumbs;
        TextView summary;
        TextView title;

        PreferenceViewHolder(View v) {
            super(v);
            this.title = (TextView) v.findViewById(C0540R.id.title);
            this.summary = (TextView) v.findViewById(C0540R.id.summary);
            this.breadcrumbs = (TextView) v.findViewById(C0540R.id.breadcrumbs);
        }
    }

    SearchPreferenceAdapter() {
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                return new HistoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(C0540R.layout.searchpreference_list_item_history, parent, false));
            case 2:
                return new PreferenceViewHolder(LayoutInflater.from(parent.getContext()).inflate(C0540R.layout.searchpreference_list_item_result, parent, false));
            default:
                return null;
        }
    }

    public void onBindViewHolder(final ViewHolder h, int position) {
        final ListItem listItem = (ListItem) this.dataset.get(position);
        if (getItemViewType(position) == 1) {
            ((HistoryViewHolder) h).term.setText(((HistoryItem) listItem).getTerm());
        } else if (getItemViewType(position) == 2) {
            PreferenceViewHolder holder = (PreferenceViewHolder) h;
            PreferenceItem item = (PreferenceItem) listItem;
            holder.title.setText(item.title);
            if (TextUtils.isEmpty(item.summary)) {
                holder.summary.setVisibility(8);
            } else {
                holder.summary.setVisibility(0);
                holder.summary.setText(item.summary);
            }
            if (this.searchConfiguration.isBreadcrumbsEnabled()) {
                holder.breadcrumbs.setText(item.breadcrumbs);
                holder.breadcrumbs.setAlpha(0.6f);
                holder.summary.setAlpha(1.0f);
            } else {
                holder.breadcrumbs.setVisibility(8);
                holder.summary.setAlpha(0.6f);
            }
            h.root.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (SearchPreferenceAdapter.this.onItemClickListener != null) {
                        SearchPreferenceAdapter.this.onItemClickListener.onItemClicked(listItem, h.getAdapterPosition());
                    }
                }
            });
        }
        h.root.setOnClickListener(/* anonymous class already generated */);
    }

    void setContent(List<ListItem> items) {
        this.dataset = new ArrayList(items);
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return this.dataset.size();
    }

    public int getItemViewType(int position) {
        return ((ListItem) this.dataset.get(position)).getType();
    }

    void setSearchConfiguration(SearchConfiguration searchConfiguration) {
        this.searchConfiguration = searchConfiguration;
    }

    void setOnItemClickListener(SearchClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
