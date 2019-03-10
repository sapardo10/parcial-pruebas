package android.support.wearable.internal.view.drawer;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.wearable.view.drawer.WearableNavigationDrawer.WearableNavigationDrawerAdapter;

public class SinglePagePresenter implements WearableNavigationDrawerPresenter {
    private static final long DRAWER_CLOSE_DELAY_MS = 500;
    @Nullable
    private WearableNavigationDrawerAdapter mAdapter;
    private int mCount = 0;
    private final boolean mIsAccessibilityEnabled;
    private int mSelected = 0;
    private final Ui mUi;

    public interface Ui {
        void closeDrawerDelayed(long j);

        void deselectItem(int i);

        void initialize(int i);

        void peekDrawer();

        void selectItem(int i);

        void setIcon(int i, Drawable drawable, String str);

        void setPresenter(WearableNavigationDrawerPresenter wearableNavigationDrawerPresenter);

        void setText(String str, boolean z);
    }

    public SinglePagePresenter(Ui ui, boolean isAccessibilityEnabled) {
        if (ui != null) {
            this.mIsAccessibilityEnabled = isAccessibilityEnabled;
            this.mUi = ui;
            this.mUi.setPresenter(this);
            onDataSetChanged();
            return;
        }
        throw new IllegalArgumentException("Received null ui.");
    }

    public void onDataSetChanged() {
        int count = this.mAdapter;
        if (count != 0) {
            count = count.getCount();
            if (this.mCount != count) {
                this.mCount = count;
                this.mSelected = Math.min(this.mSelected, count - 1);
                this.mUi.initialize(count);
            }
            for (int i = 0; i < count; i++) {
                this.mUi.setIcon(i, this.mAdapter.getItemDrawable(i), this.mAdapter.getItemText(i));
            }
            this.mUi.setText(this.mAdapter.getItemText(this.mSelected), false);
            this.mUi.selectItem(this.mSelected);
        }
    }

    public void onNewAdapter(WearableNavigationDrawerAdapter adapter) {
        if (adapter != null) {
            this.mAdapter = adapter;
            this.mAdapter.setPresenter(this);
            onDataSetChanged();
            return;
        }
        throw new IllegalArgumentException("Received null adapter.");
    }

    public void onSelected(int index) {
        this.mUi.deselectItem(this.mSelected);
        this.mUi.selectItem(index);
        this.mSelected = index;
        if (this.mIsAccessibilityEnabled) {
            this.mUi.peekDrawer();
        } else {
            this.mUi.closeDrawerDelayed(DRAWER_CLOSE_DELAY_MS);
        }
        WearableNavigationDrawerAdapter wearableNavigationDrawerAdapter = this.mAdapter;
        if (wearableNavigationDrawerAdapter != null) {
            this.mUi.setText(wearableNavigationDrawerAdapter.getItemText(index), true);
            this.mAdapter.onItemSelected(index);
        }
    }

    public void onSetCurrentItemRequested(int index, boolean smoothScrollTo) {
        this.mUi.deselectItem(this.mSelected);
        this.mUi.selectItem(index);
        this.mSelected = index;
        WearableNavigationDrawerAdapter wearableNavigationDrawerAdapter = this.mAdapter;
        if (wearableNavigationDrawerAdapter != null) {
            this.mUi.setText(wearableNavigationDrawerAdapter.getItemText(index), false);
            this.mAdapter.onItemSelected(index);
        }
    }

    public boolean onDrawerTapped() {
        return false;
    }
}
