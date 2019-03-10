package com.viewpagerindicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TabPageIndicator extends HorizontalScrollView implements PageIndicator {
    private static final CharSequence EMPTY_TITLE = "";
    private static final int ICON_BOTTOM = 3;
    private static final int ICON_LEFT = 0;
    private static final int ICON_RIGHT = 1;
    private static final int ICON_TOP = 2;
    private int mIconDirection;
    private int mIconHeight;
    private int mIconWidth;
    private OnPageChangeListener mListener;
    private int mMaxTabWidth;
    private int mSelectedTabIndex;
    private final OnClickListener mTabClickListener;
    private final IcsLinearLayout mTabLayout;
    private OnTabReselectedListener mTabReselectedListener;
    private Runnable mTabSelector;
    private ViewPager mViewPager;

    /* renamed from: com.viewpagerindicator.TabPageIndicator$1 */
    class C06991 implements OnClickListener {
        C06991() {
        }

        public void onClick(View view) {
            TabView tabView = (TabView) view;
            int oldSelected = TabPageIndicator.this.mViewPager.getCurrentItem();
            int newSelected = tabView.getIndex();
            TabPageIndicator.this.mViewPager.setCurrentItem(newSelected);
            if (oldSelected == newSelected && TabPageIndicator.this.mTabReselectedListener != null) {
                TabPageIndicator.this.mTabReselectedListener.onTabReselected(newSelected);
            }
        }
    }

    public interface OnTabReselectedListener {
        void onTabReselected(int i);
    }

    private class TabView extends TextView {
        private int mIndex;

        public TabView(Context context) {
            super(context, null, C0695R.attr.vpiTabPageIndicatorStyle);
        }

        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (TabPageIndicator.this.mMaxTabWidth > 0 && getMeasuredWidth() > TabPageIndicator.this.mMaxTabWidth) {
                super.onMeasure(MeasureSpec.makeMeasureSpec(TabPageIndicator.this.mMaxTabWidth, 1073741824), heightMeasureSpec);
            }
        }

        public int getIndex() {
            return this.mIndex;
        }
    }

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
        r4.mSelectedTabIndex = r5;
        r0.setCurrentItem(r5);
        r0 = r4.mTabLayout;
        r0 = r0.getChildCount();
        r1 = 0;
    L_0x0010:
        if (r1 >= r0) goto L_0x002a;
    L_0x0012:
        r2 = r4.mTabLayout;
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
        r4.animateToTab(r5);
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
        throw new UnsupportedOperationException("Method not decompiled: com.viewpagerindicator.TabPageIndicator.setCurrentItem(int):void");
    }

    public TabPageIndicator(Context context) {
        this(context, null);
    }

    public TabPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTabClickListener = new C06991();
        setHorizontalScrollBarEnabled(false);
        TypedArray a = context.obtainStyledAttributes(attrs, C0695R.styleable.TabPageIndicator);
        this.mIconDirection = a.getInteger(C0695R.styleable.TabPageIndicator_vpi_iconDirection, 0);
        this.mIconWidth = a.getDimensionPixelSize(C0695R.styleable.TabPageIndicator_vpi_iconWidth, 0);
        this.mIconHeight = a.getDimensionPixelSize(C0695R.styleable.TabPageIndicator_vpi_iconHeight, 0);
        a.recycle();
        this.mTabLayout = new IcsLinearLayout(context, C0695R.attr.vpiTabPageIndicatorStyle);
        addView(this.mTabLayout, new LayoutParams(-2, -1));
    }

    public void setOnTabReselectedListener(OnTabReselectedListener listener) {
        this.mTabReselectedListener = listener;
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        boolean lockedExpanded = widthMode == 1073741824;
        setFillViewport(lockedExpanded);
        int childCount = this.mTabLayout.getChildCount();
        if (childCount <= 1 || !(widthMode == 1073741824 || widthMode == Integer.MIN_VALUE)) {
            this.mMaxTabWidth = -1;
        } else if (childCount > 2) {
            this.mMaxTabWidth = (int) (((float) MeasureSpec.getSize(widthMeasureSpec)) * 0.4f);
        } else {
            this.mMaxTabWidth = MeasureSpec.getSize(widthMeasureSpec) / 2;
        }
        int oldWidth = getMeasuredWidth();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int newWidth = getMeasuredWidth();
        if (lockedExpanded && oldWidth != newWidth) {
            setCurrentItem(this.mSelectedTabIndex);
        }
    }

    private void animateToTab(int position) {
        final View tabView = this.mTabLayout.getChildAt(position);
        Runnable runnable = this.mTabSelector;
        if (runnable != null) {
            removeCallbacks(runnable);
        }
        this.mTabSelector = new Runnable() {
            public void run() {
                TabPageIndicator.this.smoothScrollTo(tabView.getLeft() - ((TabPageIndicator.this.getWidth() - tabView.getWidth()) / 2), 0);
                TabPageIndicator.this.mTabSelector = null;
            }
        };
        post(this.mTabSelector);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Runnable runnable = this.mTabSelector;
        if (runnable != null) {
            post(runnable);
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Runnable runnable = this.mTabSelector;
        if (runnable != null) {
            removeCallbacks(runnable);
        }
    }

    private void addTab(int index, CharSequence text, int iconResId) {
        TabView tabView = new TabView(getContext());
        tabView.mIndex = index;
        tabView.setFocusable(true);
        tabView.setOnClickListener(this.mTabClickListener);
        tabView.setText(text);
        if (iconResId != 0) {
            Drawable icon = getResources().getDrawable(iconResId);
            int i = this.mIconWidth;
            if (i != 0) {
                int i2 = this.mIconHeight;
                if (i2 != 0) {
                    icon.setBounds(0, 0, i, i2);
                } else {
                    icon.setBounds(0, 0, i, (icon.getIntrinsicHeight() * i) / icon.getIntrinsicWidth());
                }
            } else {
                i = this.mIconHeight;
                if (i != 0) {
                    icon.setBounds(0, 0, (i * icon.getIntrinsicWidth()) / icon.getIntrinsicHeight(), this.mIconHeight);
                } else {
                    icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
                }
            }
            switch (this.mIconDirection) {
                case 0:
                    tabView.setCompoundDrawables(icon, null, null, null);
                    break;
                case 1:
                    tabView.setCompoundDrawables(null, null, icon, null);
                    break;
                case 2:
                    tabView.setCompoundDrawables(null, icon, null, null);
                    break;
                default:
                    tabView.setCompoundDrawables(null, null, null, icon);
                    break;
            }
        }
        this.mTabLayout.addView(tabView, new LinearLayout.LayoutParams(0, -1, 1.0f));
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
        this.mTabLayout.removeAllViews();
        PagerAdapter adapter = this.mViewPager.getAdapter();
        IconPagerAdapter iconAdapter = null;
        if (adapter instanceof IconPagerAdapter) {
            iconAdapter = (IconPagerAdapter) adapter;
        }
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            CharSequence title = adapter.getPageTitle(i);
            if (title == null) {
                title = EMPTY_TITLE;
            }
            int iconResId = 0;
            if (iconAdapter != null) {
                iconResId = iconAdapter.getIconResId(i);
            }
            addTab(i, title, iconResId);
        }
        if (this.mSelectedTabIndex > count) {
            this.mSelectedTabIndex = count - 1;
        }
        setCurrentItem(this.mSelectedTabIndex);
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
