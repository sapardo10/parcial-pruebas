package android.support.v7.preference;

import android.support.annotation.IdRes;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.SparseArray;
import android.view.View;

public class PreferenceViewHolder extends ViewHolder {
    private final SparseArray<View> mCachedViews = new SparseArray(4);
    private boolean mDividerAllowedAbove;
    private boolean mDividerAllowedBelow;

    PreferenceViewHolder(View itemView) {
        super(itemView);
        this.mCachedViews.put(16908310, itemView.findViewById(16908310));
        this.mCachedViews.put(16908304, itemView.findViewById(16908304));
        this.mCachedViews.put(16908294, itemView.findViewById(16908294));
        this.mCachedViews.put(C0315R.id.icon_frame, itemView.findViewById(C0315R.id.icon_frame));
        this.mCachedViews.put(AndroidResources.ANDROID_R_ICON_FRAME, itemView.findViewById(AndroidResources.ANDROID_R_ICON_FRAME));
    }

    @RestrictTo({Scope.TESTS})
    public static PreferenceViewHolder createInstanceForTests(View itemView) {
        return new PreferenceViewHolder(itemView);
    }

    public View findViewById(@IdRes int id) {
        View cachedView = (View) this.mCachedViews.get(id);
        if (cachedView != null) {
            return cachedView;
        }
        View v = this.itemView.findViewById(id);
        if (v != null) {
            this.mCachedViews.put(id, v);
        }
        return v;
    }

    public boolean isDividerAllowedAbove() {
        return this.mDividerAllowedAbove;
    }

    public void setDividerAllowedAbove(boolean allowed) {
        this.mDividerAllowedAbove = allowed;
    }

    public boolean isDividerAllowedBelow() {
        return this.mDividerAllowedBelow;
    }

    public void setDividerAllowedBelow(boolean allowed) {
        this.mDividerAllowedBelow = allowed;
    }
}
