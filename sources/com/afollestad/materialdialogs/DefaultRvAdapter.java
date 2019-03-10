package com.afollestad.materialdialogs;

import android.annotation.TargetApi;
import android.os.Build.VERSION;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.util.DialogUtils;

class DefaultRvAdapter extends Adapter<DefaultVH> {
    private InternalListCallback callback;
    private final MaterialDialog dialog;
    private final GravityEnum itemGravity;
    @LayoutRes
    private final int layout;

    public interface InternalListCallback {
        boolean onItemSelected(MaterialDialog materialDialog, View view, int i, CharSequence charSequence, boolean z);
    }

    public static class DefaultVH extends ViewHolder implements OnClickListener, OnLongClickListener {
        final DefaultRvAdapter adapter;
        final CompoundButton control;
        final TextView title;

        public DefaultVH(View itemView, DefaultRvAdapter adapter) {
            super(itemView);
            this.control = (CompoundButton) itemView.findViewById(C0498R.id.md_control);
            this.title = (TextView) itemView.findViewById(C0498R.id.md_title);
            this.adapter = adapter;
            itemView.setOnClickListener(this);
            if (adapter.dialog.mBuilder.listLongCallback != null) {
                itemView.setOnLongClickListener(this);
            }
        }

        public void onClick(View view) {
            if (this.adapter.callback != null) {
                CharSequence text = null;
                if (this.adapter.dialog.mBuilder.items != null && getAdapterPosition() < this.adapter.dialog.mBuilder.items.size()) {
                    text = (CharSequence) this.adapter.dialog.mBuilder.items.get(getAdapterPosition());
                }
                this.adapter.callback.onItemSelected(this.adapter.dialog, view, getAdapterPosition(), text, false);
            }
        }

        public boolean onLongClick(View view) {
            if (this.adapter.callback == null) {
                return false;
            }
            CharSequence text = null;
            if (this.adapter.dialog.mBuilder.items != null && getAdapterPosition() < this.adapter.dialog.mBuilder.items.size()) {
                text = (CharSequence) this.adapter.dialog.mBuilder.items.get(getAdapterPosition());
            }
            return this.adapter.callback.onItemSelected(this.adapter.dialog, view, getAdapterPosition(), text, true);
        }
    }

    public DefaultRvAdapter(MaterialDialog dialog, @LayoutRes int layout) {
        this.dialog = dialog;
        this.layout = layout;
        this.itemGravity = dialog.mBuilder.itemsGravity;
    }

    public void setCallback(InternalListCallback callback) {
        this.callback = callback;
    }

    public DefaultVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(this.layout, parent, false);
        DialogUtils.setBackgroundCompat(view, this.dialog.getListSelector());
        return new DefaultVH(view, this);
    }

    public void onBindViewHolder(DefaultVH holder, int index) {
        View view = holder.itemView;
        boolean disabled = DialogUtils.isIn(Integer.valueOf(index), this.dialog.mBuilder.disabledIndices);
        boolean selected;
        switch (this.dialog.listType) {
            case SINGLE:
                RadioButton radio = holder.control;
                selected = this.dialog.mBuilder.selectedIndex == index;
                MDTintHelper.setTint(radio, this.dialog.mBuilder.widgetColor);
                radio.setChecked(selected);
                radio.setEnabled(disabled ^ 1);
                break;
            case MULTI:
                CheckBox checkbox = holder.control;
                selected = this.dialog.selectedIndicesList.contains(Integer.valueOf(index));
                MDTintHelper.setTint(checkbox, this.dialog.mBuilder.widgetColor);
                checkbox.setChecked(selected);
                checkbox.setEnabled(disabled ^ 1);
                break;
            default:
                break;
        }
        holder.title.setText((CharSequence) this.dialog.mBuilder.items.get(index));
        holder.title.setTextColor(this.dialog.mBuilder.itemColor);
        this.dialog.setTypeface(holder.title, this.dialog.mBuilder.regularFont);
        setupGravity((ViewGroup) view);
        if (this.dialog.mBuilder.itemIds != null) {
            if (index < this.dialog.mBuilder.itemIds.length) {
                view.setId(this.dialog.mBuilder.itemIds[index]);
            } else {
                view.setId(-1);
            }
        }
        if (VERSION.SDK_INT >= 21) {
            ViewGroup group = (ViewGroup) view;
            if (group.getChildCount() == 2) {
                if (group.getChildAt(0) instanceof CompoundButton) {
                    group.getChildAt(0).setBackground(null);
                } else if (group.getChildAt(1) instanceof CompoundButton) {
                    group.getChildAt(1).setBackground(null);
                }
            }
        }
    }

    public int getItemCount() {
        return this.dialog.mBuilder.items != null ? this.dialog.mBuilder.items.size() : 0;
    }

    @TargetApi(17)
    private void setupGravity(ViewGroup view) {
        ((LinearLayout) view).setGravity(this.itemGravity.getGravityInt() | 16);
        if (view.getChildCount() == 2) {
            CompoundButton first;
            TextView second;
            if (this.itemGravity == GravityEnum.END && !isRTL() && (view.getChildAt(0) instanceof CompoundButton)) {
                first = (CompoundButton) view.getChildAt(0);
                view.removeView(first);
                second = (TextView) view.getChildAt(0);
                view.removeView(second);
                second.setPadding(second.getPaddingRight(), second.getPaddingTop(), second.getPaddingLeft(), second.getPaddingBottom());
                view.addView(second);
                view.addView(first);
            } else if (this.itemGravity == GravityEnum.START && isRTL() && (view.getChildAt(1) instanceof CompoundButton)) {
                first = (CompoundButton) view.getChildAt(1);
                view.removeView(first);
                second = (TextView) view.getChildAt(0);
                view.removeView(second);
                second.setPadding(second.getPaddingRight(), second.getPaddingTop(), second.getPaddingRight(), second.getPaddingBottom());
                view.addView(first);
                view.addView(second);
            }
        }
    }

    @TargetApi(17)
    private boolean isRTL() {
        boolean z = false;
        if (VERSION.SDK_INT < 17) {
            return false;
        }
        if (this.dialog.getBuilder().getContext().getResources().getConfiguration().getLayoutDirection() == 1) {
            z = true;
        }
        return z;
    }
}
