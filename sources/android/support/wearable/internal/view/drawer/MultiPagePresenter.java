package android.support.wearable.internal.view.drawer;

import android.support.annotation.Nullable;
import android.support.wearable.view.drawer.WearableNavigationDrawer;
import android.support.wearable.view.drawer.WearableNavigationDrawer.WearableNavigationDrawerAdapter;

public class MultiPagePresenter implements WearableNavigationDrawerPresenter {
    @Nullable
    private WearableNavigationDrawerAdapter mAdapter;
    private final WearableNavigationDrawer mDrawer;
    private final boolean mIsAccessibilityEnabled;
    private final Ui mUi;

    public interface Ui {
        void initialize(WearableNavigationDrawer wearableNavigationDrawer, WearableNavigationDrawerPresenter wearableNavigationDrawerPresenter);

        void notifyNavigationPagerAdapterDataChanged();

        void notifyPageIndicatorDataChanged();

        void setNavigationPagerAdapter(WearableNavigationDrawerAdapter wearableNavigationDrawerAdapter);

        void setNavigationPagerSelectedItem(int i, boolean z);
    }

    public MultiPagePresenter(WearableNavigationDrawer drawer, Ui ui, boolean isAccessibilityEnabled) {
        if (drawer == null) {
            throw new IllegalArgumentException("Received null drawer.");
        } else if (ui != null) {
            this.mDrawer = drawer;
            this.mUi = ui;
            this.mUi.initialize(drawer, this);
            this.mIsAccessibilityEnabled = isAccessibilityEnabled;
        } else {
            throw new IllegalArgumentException("Received null ui.");
        }
    }

    public void onDataSetChanged() {
        this.mUi.notifyNavigationPagerAdapterDataChanged();
        this.mUi.notifyPageIndicatorDataChanged();
    }

    public void onNewAdapter(WearableNavigationDrawerAdapter adapter) {
        if (adapter != null) {
            this.mAdapter = adapter;
            this.mAdapter.setPresenter(this);
            this.mUi.setNavigationPagerAdapter(adapter);
            return;
        }
        throw new IllegalArgumentException("Received null adapter.");
    }

    public void onSelected(int index) {
        WearableNavigationDrawerAdapter wearableNavigationDrawerAdapter = this.mAdapter;
        if (wearableNavigationDrawerAdapter != null) {
            wearableNavigationDrawerAdapter.onItemSelected(index);
        }
    }

    public void onSetCurrentItemRequested(int index, boolean smoothScrollTo) {
        this.mUi.setNavigationPagerSelectedItem(index, smoothScrollTo);
    }

    public boolean onDrawerTapped() {
        if (!this.mDrawer.isOpened()) {
            return false;
        }
        if (this.mIsAccessibilityEnabled) {
            this.mDrawer.peekDrawer();
        } else {
            this.mDrawer.closeDrawer();
        }
        return true;
    }
}
