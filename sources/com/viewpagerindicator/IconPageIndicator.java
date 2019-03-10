package com.viewpagerindicator;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

public class IconPageIndicator extends HorizontalScrollView implements PageIndicator {
    private Runnable mIconSelector;
    private final IcsLinearLayout mIconsLayout;
    private OnPageChangeListener mListener;
    private int mSelectedIndex;
    private ViewPager mViewPager;

    public void setCurrentItem(int r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:0x0033 in {6, 7, 10, 11, 12, 13, 15} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r4 = this;
        r0 = r4.mViewPager;
        if (r0 == 0) goto L_0x002b;
    L_0x0004:
        r4.mSelectedIndex = r5;
        r0.setCurrentItem(r5);
        r0 = r4.mIconsLayout;
        r0 = r0.getChildCount();
        r1 = 0;
    L_0x0010:
        if (r1 >= r0) goto L_0x002a;
    L_0x0012:
        r2 = r4.mIconsLayout;
        r2 = r2.getChildAt(r1);
        if (r1 != r5) goto L_0x001c;
    L_0x001a:
        r3 = 1;
        goto L_0x001d;
    L_0x001c:
        r3 = 0;
    L_0x001d:
        r2.setSelected(r3);
        if (r3 == 0) goto L_0x0026;
    L_0x0022:
        r4.animateToIcon(r5);
        goto L_0x0027;
    L_0x0027:
        r1 = r1 + 1;
        goto L_0x0010;
    L_0x002a:
        return;
    L_0x002b:
        r0 = new java.lang.IllegalStateException;
        r1 = "ViewPager has not been bound.";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.viewpagerindicator.IconPageIndicator.setCurrentItem(int):void");
    }

    public IconPageIndicator(Context context) {
        this(context, null);
    }

    public IconPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);
        this.mIconsLayout = new IcsLinearLayout(context, C0695R.attr.vpiIconPageIndicatorStyle);
        addView(this.mIconsLayout, new LayoutParams(-2, -1, 17));
    }

    private void animateToIcon(int position) {
        final View iconView = this.mIconsLayout.getChildAt(position);
        Runnable runnable = this.mIconSelector;
        if (runnable != null) {
            removeCallbacks(runnable);
        }
        this.mIconSelector = new Runnable() {
            public void run() {
                IconPageIndicator.this.smoothScrollTo(iconView.getLeft() - ((IconPageIndicator.this.getWidth() - iconView.getWidth()) / 2), 0);
                IconPageIndicator.this.mIconSelector = null;
            }
        };
        post(this.mIconSelector);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Runnable runnable = this.mIconSelector;
        if (runnable != null) {
            post(runnable);
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Runnable runnable = this.mIconSelector;
        if (runnable != null) {
            removeCallbacks(runnable);
        }
    }

    public void onPageScrollStateChanged(int arg0) {
        OnPageChangeListener onPageChangeListener = this.mListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrollStateChanged(arg0);
        }
    }

    public void onPageScrolled(int arg0, float arg1, int arg2) {
        OnPageChangeListener onPageChangeListener = this.mListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrolled(arg0, arg1, arg2);
        }
    }

    public void onPageSelected(int arg0) {
        setCurrentItem(arg0);
        OnPageChangeListener onPageChangeListener = this.mListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageSelected(arg0);
        }
    }

    public void setViewPager(ViewPager view) {
        ViewPager viewPager = this.mViewPager;
        if (viewPager != view) {
            if (viewPager != null) {
                viewPager.setOnPageChangeListener(null);
            }
            if (view.getAdapter() != null) {
                this.mViewPager = view;
                view.setOnPageChangeListener(this);
                notifyDataSetChanged();
                return;
            }
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
    }

    public void notifyDataSetChanged() {
        this.mIconsLayout.removeAllViews();
        IconPagerAdapter iconAdapter = (IconPagerAdapter) this.mViewPager.getAdapter();
        int count = iconAdapter.getCount();
        for (int i = 0; i < count; i++) {
            ImageView view = new ImageView(getContext(), null, C0695R.attr.vpiIconPageIndicatorStyle);
            view.setImageResource(iconAdapter.getIconResId(i));
            this.mIconsLayout.addView(view);
        }
        if (this.mSelectedIndex > count) {
            this.mSelectedIndex = count - 1;
        }
        setCurrentItem(this.mSelectedIndex);
        requestLayout();
    }

    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mListener = listener;
    }
}
