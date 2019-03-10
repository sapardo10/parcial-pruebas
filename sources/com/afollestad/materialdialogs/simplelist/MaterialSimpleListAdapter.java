package com.afollestad.materialdialogs.simplelist;

import android.graphics.PorterDuff.Mode;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.commons.C0502R;
import com.afollestad.materialdialogs.internal.MDAdapter;
import java.util.ArrayList;
import java.util.List;

public class MaterialSimpleListAdapter extends Adapter<SimpleListVH> implements MDAdapter {
    private MaterialDialog dialog;
    private Callback mCallback;
    private List<MaterialSimpleListItem> mItems = new ArrayList(4);

    public interface Callback {
        void onMaterialListItemSelected(MaterialDialog materialDialog, int i, MaterialSimpleListItem materialSimpleListItem);
    }

    public static class SimpleListVH extends ViewHolder implements OnClickListener {
        final MaterialSimpleListAdapter adapter;
        final ImageView icon;
        final TextView title;

        public SimpleListVH(View itemView, MaterialSimpleListAdapter adapter) {
            super(itemView);
            this.icon = (ImageView) itemView.findViewById(16908294);
            this.title = (TextView) itemView.findViewById(16908310);
            this.adapter = adapter;
            itemView.setOnClickListener(this);
        }

        public void onClick(View view) {
            if (this.adapter.mCallback != null) {
                this.adapter.mCallback.onMaterialListItemSelected(this.adapter.dialog, getAdapterPosition(), this.adapter.getItem(getAdapterPosition()));
            }
        }
    }

    public MaterialSimpleListAdapter(Callback callback) {
        this.mCallback = callback;
    }

    public void add(MaterialSimpleListItem item) {
        this.mItems.add(item);
        notifyItemInserted(this.mItems.size() - 1);
    }

    public void clear() {
        this.mItems.clear();
        notifyDataSetChanged();
    }

    public MaterialSimpleListItem getItem(int index) {
        return (MaterialSimpleListItem) this.mItems.get(index);
    }

    public void setDialog(MaterialDialog dialog) {
        this.dialog = dialog;
    }

    public SimpleListVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SimpleListVH(LayoutInflater.from(parent.getContext()).inflate(C0502R.layout.md_simplelist_item, parent, false), this);
    }

    public void onBindViewHolder(SimpleListVH holder, int position) {
        if (this.dialog != null) {
            MaterialSimpleListItem item = (MaterialSimpleListItem) this.mItems.get(position);
            if (item.getIcon() != null) {
                holder.icon.setImageDrawable(item.getIcon());
                holder.icon.setPadding(item.getIconPadding(), item.getIconPadding(), item.getIconPadding(), item.getIconPadding());
                holder.icon.getBackground().setColorFilter(item.getBackgroundColor(), Mode.SRC_ATOP);
            } else {
                holder.icon.setVisibility(8);
            }
            holder.title.setTextColor(this.dialog.getBuilder().getItemColor());
            holder.title.setText(item.getContent());
            this.dialog.setTypeface(holder.title, this.dialog.getBuilder().getRegularFont());
        }
    }

    public int getItemCount() {
        return this.mItems.size();
    }
}
