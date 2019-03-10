package android.support.wearable.internal.view.drawer;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.wearable.C0395R;
import android.support.wearable.internal.view.drawer.SinglePagePresenter.Ui;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.drawer.WearableNavigationDrawer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class SinglePageUi implements Ui {
    @IdRes
    private static final int[] SINGLE_PAGE_BUTTON_IDS = new int[]{C0395R.id.wearable_support_nav_drawer_icon_0, C0395R.id.wearable_support_nav_drawer_icon_1, C0395R.id.wearable_support_nav_drawer_icon_2, C0395R.id.wearable_support_nav_drawer_icon_3, C0395R.id.wearable_support_nav_drawer_icon_4, C0395R.id.wearable_support_nav_drawer_icon_5, C0395R.id.wearable_support_nav_drawer_icon_6};
    @LayoutRes
    private static final int[] SINGLE_PAGE_LAYOUT_RES = new int[]{0, C0395R.layout.single_page_nav_drawer_1_item, C0395R.layout.single_page_nav_drawer_2_item, C0395R.layout.single_page_nav_drawer_3_item, C0395R.layout.single_page_nav_drawer_4_item, C0395R.layout.single_page_nav_drawer_5_item, C0395R.layout.single_page_nav_drawer_6_item, C0395R.layout.single_page_nav_drawer_7_item};
    private final Runnable mCloseDrawerRunnable = new C04231();
    private final WearableNavigationDrawer mDrawer;
    private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
    private WearableNavigationDrawerPresenter mPresenter;
    private CircledImageView[] mSinglePageImageViews;
    @Nullable
    private TextView mTextView;

    /* renamed from: android.support.wearable.internal.view.drawer.SinglePageUi$1 */
    class C04231 implements Runnable {
        C04231() {
        }

        public void run() {
            SinglePageUi.this.mDrawer.closeDrawer();
        }
    }

    private static class OnSelectedClickHandler implements OnClickListener {
        private final int mIndex;
        private final WearableNavigationDrawerPresenter mPresenter;

        private OnSelectedClickHandler(int index, WearableNavigationDrawerPresenter presenter) {
            this.mIndex = index;
            this.mPresenter = presenter;
        }

        public void onClick(View v) {
            this.mPresenter.onSelected(this.mIndex);
        }
    }

    public SinglePageUi(WearableNavigationDrawer navigationDrawer) {
        if (navigationDrawer != null) {
            this.mDrawer = navigationDrawer;
            return;
        }
        throw new IllegalArgumentException("Received null navigationDrawer.");
    }

    public void setPresenter(WearableNavigationDrawerPresenter presenter) {
        this.mPresenter = presenter;
    }

    public void initialize(int count) {
        if (count >= 0) {
            int layoutRes = SINGLE_PAGE_LAYOUT_RES;
            if (count < layoutRes.length) {
                if (layoutRes[count] != null) {
                    layoutRes = layoutRes[count];
                    LayoutInflater inflater = LayoutInflater.from(this.mDrawer.getContext());
                    View content = inflater.inflate(layoutRes, this.mDrawer, false);
                    View peek = inflater.inflate(C0395R.layout.single_page_nav_drawer_peek_view, this.mDrawer, false);
                    this.mTextView = (TextView) content.findViewById(C0395R.id.wearable_support_nav_drawer_text);
                    this.mSinglePageImageViews = new CircledImageView[count];
                    for (int i = 0; i < count; i++) {
                        this.mSinglePageImageViews[i] = (CircledImageView) content.findViewById(SINGLE_PAGE_BUTTON_IDS[i]);
                        this.mSinglePageImageViews[i].setOnClickListener(new OnSelectedClickHandler(i, this.mPresenter));
                        this.mSinglePageImageViews[i].setCircleHidden(true);
                    }
                    this.mDrawer.setDrawerContent(content);
                    this.mDrawer.setPeekContent(peek);
                    return;
                }
            }
        }
        this.mDrawer.setDrawerContent(null);
    }

    public void setIcon(int index, Drawable drawable, String contentDescription) {
        this.mSinglePageImageViews[index].setImageDrawable(drawable);
        this.mSinglePageImageViews[index].setContentDescription(contentDescription);
    }

    public void setText(String itemText, boolean showToastIfNoTextView) {
        TextView textView = this.mTextView;
        if (textView != null) {
            textView.setText(itemText);
        } else if (showToastIfNoTextView) {
            Toast toast = Toast.makeText(this.mDrawer.getContext(), itemText, 0);
            toast.setGravity(17, 0, 0);
            toast.show();
        }
    }

    public void selectItem(int index) {
        this.mSinglePageImageViews[index].setCircleHidden(false);
    }

    public void deselectItem(int index) {
        this.mSinglePageImageViews[index].setCircleHidden(true);
    }

    public void closeDrawerDelayed(long delayMs) {
        this.mMainThreadHandler.removeCallbacks(this.mCloseDrawerRunnable);
        this.mMainThreadHandler.postDelayed(this.mCloseDrawerRunnable, delayMs);
    }

    public void peekDrawer() {
        this.mDrawer.peekDrawer();
    }
}
