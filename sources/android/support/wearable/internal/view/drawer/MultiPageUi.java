package android.support.wearable.internal.view.drawer;

import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.support.wearable.C0395R;
import android.support.wearable.internal.view.drawer.MultiPagePresenter.Ui;
import android.support.wearable.view.drawer.PageIndicatorView;
import android.support.wearable.view.drawer.WearableNavigationDrawer;
import android.support.wearable.view.drawer.WearableNavigationDrawer.WearableNavigationDrawerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MultiPageUi implements Ui {
    private static final String TAG = "MultiPageUi";
    @Nullable
    private ViewPager mNavigationPager;
    @Nullable
    private PageIndicatorView mPageIndicatorView;
    private WearableNavigationDrawerPresenter mPresenter;

    private static final class NavigationPagerAdapter extends PagerAdapter {
        private final WearableNavigationDrawerAdapter mAdapter;

        NavigationPagerAdapter(WearableNavigationDrawerAdapter adapter) {
            this.mAdapter = adapter;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(container.getContext()).inflate(C0395R.layout.navigation_drawer_item_view, container, false);
            container.addView(view);
            TextView textView = (TextView) view.findViewById(C0395R.id.wearable_support_navigation_drawer_item_text);
            ((ImageView) view.findViewById(C0395R.id.wearable_support_navigation_drawer_item_icon)).setImageDrawable(this.mAdapter.getItemDrawable(position));
            textView.setText(this.mAdapter.getItemText(position));
            return view;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public int getCount() {
            return this.mAdapter.getCount();
        }

        public int getItemPosition(Object object) {
            return -2;
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    /* renamed from: android.support.wearable.internal.view.drawer.MultiPageUi$1 */
    class C11131 extends SimpleOnPageChangeListener {
        C11131() {
        }

        public void onPageSelected(int position) {
            MultiPageUi.this.mPresenter.onSelected(position);
        }
    }

    public void initialize(WearableNavigationDrawer drawer, WearableNavigationDrawerPresenter presenter) {
        if (drawer == null) {
            throw new IllegalArgumentException("Received null drawer.");
        } else if (presenter != null) {
            this.mPresenter = presenter;
            View content = LayoutInflater.from(drawer.getContext()).inflate(C0395R.layout.navigation_drawer_view, drawer, false);
            this.mNavigationPager = (ViewPager) content.findViewById(C0395R.id.wearable_support_navigation_drawer_view_pager);
            this.mPageIndicatorView = (PageIndicatorView) content.findViewById(C0395R.id.wearable_support_navigation_drawer_page_indicator);
            drawer.setDrawerContent(content);
        } else {
            throw new IllegalArgumentException("Received null presenter.");
        }
    }

    public void setNavigationPagerAdapter(WearableNavigationDrawerAdapter adapter) {
        if (this.mNavigationPager != null) {
            if (this.mPageIndicatorView != null) {
                this.mNavigationPager.setAdapter(new NavigationPagerAdapter(adapter));
                this.mNavigationPager.clearOnPageChangeListeners();
                this.mNavigationPager.addOnPageChangeListener(new C11131());
                this.mPageIndicatorView.setPager(this.mNavigationPager);
                return;
            }
        }
        Log.w(TAG, "setNavigationPagerAdapter was called before initialize.");
    }

    public void notifyPageIndicatorDataChanged() {
        PageIndicatorView pageIndicatorView = this.mPageIndicatorView;
        if (pageIndicatorView != null) {
            pageIndicatorView.notifyDataSetChanged();
        }
    }

    public void notifyNavigationPagerAdapterDataChanged() {
        PagerAdapter adapter = this.mNavigationPager;
        if (adapter != null) {
            adapter = adapter.getAdapter();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void setNavigationPagerSelectedItem(int index, boolean smoothScrollTo) {
        ViewPager viewPager = this.mNavigationPager;
        if (viewPager != null) {
            viewPager.setCurrentItem(index, smoothScrollTo);
        }
    }
}
