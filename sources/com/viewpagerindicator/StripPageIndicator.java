package com.viewpagerindicator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import java.util.Locale;

public class StripPageIndicator extends HorizontalScrollView implements PageIndicator {
    private static final int[] ATTRS = new int[]{16842901, 16842904};
    private int mBackgroundResId;
    private int mCurrentPage;
    private float mCurrentPositionOffset;
    private LayoutParams mDefaultTabLayoutParams;
    private int mDividerColor;
    private int mDividerPadding;
    private Paint mDividerPaint;
    private int mDividerWidth;
    private LayoutParams mExpandedTabLayoutParams;
    private int mIndicatorColor;
    private int mIndicatorHeight;
    private int mLastScrollX;
    private int mLinePosition;
    public OnPageChangeListener mListener;
    private Locale mLocale;
    private Paint mRectPaint;
    private int mScrollOffset;
    private boolean mShouldExpand;
    private int mTabCount;
    private int mTabPadding;
    private ColorStateList mTabTextColor;
    private int mTabTextSize;
    private Typeface mTabTypeface;
    private int mTabTypefaceStyle;
    private LinearLayout mTabsContainer;
    private boolean mTextAllCaps;
    private int mUnderlineColor;
    private int mUnderlineHeight;
    private ViewPager mViewPager;

    /* renamed from: com.viewpagerindicator.StripPageIndicator$1 */
    class C06961 implements OnGlobalLayoutListener {
        C06961() {
        }

        @SuppressLint({"NewApi"})
        public void onGlobalLayout() {
            if (VERSION.SDK_INT < 16) {
                StripPageIndicator.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            } else {
                StripPageIndicator.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
            StripPageIndicator stripPageIndicator = StripPageIndicator.this;
            stripPageIndicator.mCurrentPage = stripPageIndicator.mViewPager.getCurrentItem();
            stripPageIndicator = StripPageIndicator.this;
            stripPageIndicator.scrollToChild(stripPageIndicator.mCurrentPage, 0);
            StripPageIndicator.this.setTabSelected();
        }
    }

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new C06981();
        int currentPage;

        /* renamed from: com.viewpagerindicator.StripPageIndicator$SavedState$1 */
        static class C06981 implements Creator<SavedState> {
            C06981() {
            }

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.currentPage = in.readInt();
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.currentPage);
        }
    }

    public StripPageIndicator(Context context) {
        this(context, null);
    }

    public StripPageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StripPageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mTabTypefaceStyle = 1;
        this.mLastScrollX = 0;
        this.mBackgroundResId = C0695R.drawable.vpi__tab_background;
        setFillViewport(true);
        setWillNotDraw(false);
        this.mTabsContainer = new LinearLayout(context);
        this.mTabsContainer.setOrientation(0);
        this.mTabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(this.mTabsContainer);
        Resources res = getResources();
        this.mIndicatorColor = res.getColor(C0695R.color.default_strip_indicator_indicator_color);
        this.mUnderlineColor = res.getColor(C0695R.color.default_strip_indicator_divider_color);
        this.mDividerColor = res.getColor(C0695R.color.default_strip_indicator_divider_color);
        this.mShouldExpand = res.getBoolean(C0695R.bool.default_strip_indicator_should_expand);
        this.mTextAllCaps = res.getBoolean(C0695R.bool.default_strip_indicator_text_all_caps);
        this.mScrollOffset = res.getDimensionPixelSize(C0695R.dimen.default_strip_indicator_scroll_offset);
        this.mIndicatorHeight = res.getDimensionPixelSize(C0695R.dimen.default_strip_indicator_indicator_height);
        this.mUnderlineHeight = res.getDimensionPixelSize(C0695R.dimen.default_strip_indicator_underline_height);
        this.mDividerPadding = res.getDimensionPixelSize(C0695R.dimen.default_strip_indicator_divider_padding);
        this.mTabPadding = res.getDimensionPixelSize(C0695R.dimen.default_strip_indicator_tab_padding);
        this.mDividerWidth = res.getDimensionPixelSize(C0695R.dimen.default_strip_indicator_divider_width);
        this.mTabTextSize = res.getDimensionPixelSize(C0695R.dimen.default_strip_indicator_text_size);
        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS, defStyle, 0);
        this.mTabTextSize = a.getDimensionPixelSize(0, this.mTabTextSize);
        this.mTabTextColor = a.getColorStateList(1);
        a.recycle();
        if (this.mTabTextColor == null) {
            this.mTabTextColor = res.getColorStateList(C0695R.color.default_strip_indicator_text_color);
        }
        a = context.obtainStyledAttributes(attrs, C0695R.styleable.StripPageIndicator);
        this.mIndicatorColor = a.getColor(C0695R.styleable.StripPageIndicator_vpiIndicatorColor, this.mIndicatorColor);
        this.mUnderlineColor = a.getColor(C0695R.styleable.StripPageIndicator_vpiUnderlineColor, this.mUnderlineColor);
        this.mDividerColor = a.getColor(C0695R.styleable.StripPageIndicator_vpiDividerColor, this.mDividerColor);
        this.mIndicatorHeight = a.getDimensionPixelSize(C0695R.styleable.StripPageIndicator_vpiIndicatorHeight, this.mIndicatorHeight);
        this.mUnderlineHeight = a.getDimensionPixelSize(C0695R.styleable.StripPageIndicator_vpiUnderlineHeight, this.mUnderlineHeight);
        this.mDividerPadding = a.getDimensionPixelSize(C0695R.styleable.StripPageIndicator_vpiDividerPadding, this.mDividerPadding);
        this.mTabPadding = a.getDimensionPixelSize(C0695R.styleable.StripPageIndicator_vpiTabPaddingLeftRight, this.mTabPadding);
        this.mBackgroundResId = a.getResourceId(C0695R.styleable.StripPageIndicator_vpiTabBackground, this.mBackgroundResId);
        this.mShouldExpand = a.getBoolean(C0695R.styleable.StripPageIndicator_vpiShouldExpand, this.mShouldExpand);
        this.mScrollOffset = a.getDimensionPixelSize(C0695R.styleable.StripPageIndicator_vpiScrollOffset, this.mScrollOffset);
        this.mTextAllCaps = a.getBoolean(C0695R.styleable.StripPageIndicator_vpiTextAllCaps, this.mTextAllCaps);
        this.mTabTextSize = a.getDimensionPixelSize(C0695R.styleable.StripPageIndicator_android_textSize, this.mTabTextSize);
        ColorStateList textColor = a.getColorStateList(C0695R.styleable.StripPageIndicator_android_textColor);
        if (textColor != null) {
            this.mTabTextColor = textColor;
        }
        a.recycle();
        this.mRectPaint = new Paint();
        this.mRectPaint.setAntiAlias(true);
        this.mRectPaint.setStyle(Style.FILL);
        this.mDividerPaint = new Paint();
        this.mDividerPaint.setAntiAlias(true);
        this.mDividerPaint.setStrokeWidth((float) this.mDividerWidth);
        this.mDefaultTabLayoutParams = new LayoutParams(-2, -1);
        this.mExpandedTabLayoutParams = new LayoutParams(0, -1, 1.0f);
        if (this.mLocale == null) {
            this.mLocale = getResources().getConfiguration().locale;
        }
    }

    public void setViewPager(ViewPager view) {
        if (this.mViewPager != view) {
            if (view.getAdapter() != null) {
                this.mViewPager = view;
                view.setOnPageChangeListener(this);
                notifyDataSetChanged();
                return;
            }
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
    }

    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    public void setCurrentItem(int item) {
        ViewPager viewPager = this.mViewPager;
        if (viewPager != null) {
            viewPager.setCurrentItem(item);
            this.mCurrentPage = item;
            setTabSelected();
            invalidate();
            return;
        }
        throw new IllegalStateException("ViewPager has not been bound.");
    }

    private void setTabSelected() {
        int i = 0;
        while (i < this.mTabCount) {
            this.mTabsContainer.getChildAt(i).setSelected(i == this.mCurrentPage);
            i++;
        }
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mListener = listener;
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(position);
        stringBuilder.append(":");
        stringBuilder.append(positionOffset);
        stringBuilder.append(":");
        stringBuilder.append(positionOffsetPixels);
        Log.d("OnPageScrolled", stringBuilder.toString());
        this.mLinePosition = position;
        this.mCurrentPositionOffset = positionOffset;
        scrollToChild(position, (int) (((float) this.mTabsContainer.getChildAt(position).getWidth()) * positionOffset));
        invalidate();
        OnPageChangeListener onPageChangeListener = this.mListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    public void onPageScrollStateChanged(int state) {
        if (state == 0) {
            scrollToChild(this.mViewPager.getCurrentItem(), 0);
        }
        OnPageChangeListener onPageChangeListener = this.mListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    public void onPageSelected(int position) {
        this.mCurrentPage = position;
        setTabSelected();
        OnPageChangeListener onPageChangeListener = this.mListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageSelected(position);
        }
    }

    public void notifyDataSetChanged() {
        this.mTabsContainer.removeAllViews();
        PagerAdapter adapter = this.mViewPager.getAdapter();
        this.mTabCount = adapter.getCount();
        IconPagerAdapter iconAdapter = adapter instanceof IconPagerAdapter ? (IconPagerAdapter) adapter : null;
        for (int i = 0; i < this.mTabCount; i++) {
            if (iconAdapter != null) {
                addIconTab(i, iconAdapter.getIconResId(i));
            } else {
                addTextTab(i, adapter.getPageTitle(i));
            }
        }
        updateTabStyles();
        getViewTreeObserver().addOnGlobalLayoutListener(new C06961());
    }

    private void addTextTab(int position, CharSequence title) {
        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setGravity(17);
        tab.setSingleLine();
        addTab(position, tab);
    }

    private void addIconTab(int position, int resId) {
        ImageButton tab = new ImageButton(getContext());
        tab.setImageResource(resId);
        addTab(position, tab);
    }

    private void addTab(final int position, View tab) {
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                StripPageIndicator.this.mViewPager.setCurrentItem(position);
            }
        });
        int i = this.mTabPadding;
        tab.setPadding(i, 0, i, 0);
        this.mTabsContainer.addView(tab, position, this.mShouldExpand ? this.mExpandedTabLayoutParams : this.mDefaultTabLayoutParams);
    }

    private void updateTabStyles() {
        for (int i = 0; i < this.mTabCount; i++) {
            View v = this.mTabsContainer.getChildAt(i);
            v.setBackgroundResource(this.mBackgroundResId);
            if (v instanceof TextView) {
                TextView tab = (TextView) v;
                tab.setTextSize(0, (float) this.mTabTextSize);
                tab.setTypeface(this.mTabTypeface, this.mTabTypefaceStyle);
                tab.setTextColor(this.mTabTextColor);
                if (this.mTextAllCaps) {
                    if (VERSION.SDK_INT >= 14) {
                        tab.setAllCaps(true);
                    } else {
                        tab.setText(tab.getText().toString().toUpperCase(this.mLocale));
                    }
                }
            }
        }
    }

    private void scrollToChild(int position, int offset) {
        if (this.mTabCount != 0) {
            int newScrollX = this.mTabsContainer.getChildAt(position).getLeft() + offset;
            if (position <= 0) {
                if (offset <= 0) {
                    if (newScrollX != this.mLastScrollX) {
                        this.mLastScrollX = newScrollX;
                        scrollTo(newScrollX, 0);
                    }
                }
            }
            newScrollX -= this.mScrollOffset;
            if (newScrollX != this.mLastScrollX) {
                this.mLastScrollX = newScrollX;
                scrollTo(newScrollX, 0);
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInEditMode()) {
            if (this.mTabCount != 0) {
                int i;
                View tab;
                int height = getHeight();
                this.mRectPaint.setColor(this.mIndicatorColor);
                View currentTab = this.mTabsContainer.getChildAt(this.mLinePosition);
                float lineLeft = (float) currentTab.getLeft();
                float lineRight = (float) currentTab.getRight();
                if (this.mCurrentPositionOffset > 0.0f) {
                    i = this.mLinePosition;
                    if (i < this.mTabCount - 1) {
                        View nextTab = this.mTabsContainer.getChildAt(i + 1);
                        float nextTabRight = (float) nextTab.getRight();
                        float left = ((float) nextTab.getLeft()) - lineLeft;
                        float f = this.mCurrentPositionOffset;
                        lineLeft += left * f;
                        lineRight += (nextTabRight - lineRight) * f;
                        canvas.drawRect(lineLeft, (float) (height - this.mIndicatorHeight), lineRight, (float) height, this.mRectPaint);
                        this.mRectPaint.setColor(this.mUnderlineColor);
                        canvas.drawRect(0.0f, (float) (height - this.mUnderlineHeight), (float) this.mTabsContainer.getWidth(), (float) height, this.mRectPaint);
                        this.mDividerPaint.setColor(this.mDividerColor);
                        for (i = 0; i < this.mTabCount - 1; i++) {
                            tab = this.mTabsContainer.getChildAt(i);
                            canvas.drawLine((float) tab.getRight(), (float) this.mDividerPadding, (float) tab.getRight(), (float) (height - this.mDividerPadding), this.mDividerPaint);
                        }
                    }
                }
                canvas.drawRect(lineLeft, (float) (height - this.mIndicatorHeight), lineRight, (float) height, this.mRectPaint);
                this.mRectPaint.setColor(this.mUnderlineColor);
                canvas.drawRect(0.0f, (float) (height - this.mUnderlineHeight), (float) this.mTabsContainer.getWidth(), (float) height, this.mRectPaint);
                this.mDividerPaint.setColor(this.mDividerColor);
                for (i = 0; i < this.mTabCount - 1; i++) {
                    tab = this.mTabsContainer.getChildAt(i);
                    canvas.drawLine((float) tab.getRight(), (float) this.mDividerPadding, (float) tab.getRight(), (float) (height - this.mDividerPadding), this.mDividerPaint);
                }
            }
        }
    }

    public void setIndicatorColor(int indicatorColor) {
        this.mIndicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorColorResource(int resId) {
        this.mIndicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public int getIndicatorColor() {
        return this.mIndicatorColor;
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.mIndicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {
        return this.mIndicatorHeight;
    }

    public void setUnderlineColor(int underlineColor) {
        this.mUnderlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.mUnderlineColor = getResources().getColor(resId);
        invalidate();
    }

    public int getUnderlineColor() {
        return this.mUnderlineColor;
    }

    public void setDividerColor(int dividerColor) {
        this.mDividerColor = dividerColor;
        invalidate();
    }

    public void setDividerColorResource(int resId) {
        this.mDividerColor = getResources().getColor(resId);
        invalidate();
    }

    public int getDividerColor() {
        return this.mDividerColor;
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        this.mUnderlineHeight = underlineHeightPx;
        invalidate();
    }

    public int getUnderlineHeight() {
        return this.mUnderlineHeight;
    }

    public void setDividerPadding(int dividerPaddingPx) {
        this.mDividerPadding = dividerPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {
        return this.mDividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.mScrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return this.mScrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand) {
        this.mShouldExpand = shouldExpand;
        requestLayout();
    }

    public boolean getShouldExpand() {
        return this.mShouldExpand;
    }

    public boolean isTextAllCaps() {
        return this.mTextAllCaps;
    }

    public void setAllCaps(boolean textAllCaps) {
        this.mTextAllCaps = textAllCaps;
    }

    public void setTextSize(int textSizePx) {
        this.mTabTextSize = textSizePx;
        updateTabStyles();
    }

    public int getTextSize() {
        return this.mTabTextSize;
    }

    public void setTextColor(int textColor) {
        this.mTabTextColor = ColorStateList.valueOf(textColor);
        updateTabStyles();
    }

    public void setTextColorResource(int resId) {
        this.mTabTextColor = getResources().getColorStateList(resId);
        updateTabStyles();
    }

    public ColorStateList getTextColor() {
        return this.mTabTextColor;
    }

    public void setTypeface(Typeface typeface, int style) {
        this.mTabTypeface = typeface;
        this.mTabTypefaceStyle = style;
        updateTabStyles();
    }

    public void setTabBackground(int resId) {
        this.mBackgroundResId = resId;
    }

    public int getTabBackground() {
        return this.mBackgroundResId;
    }

    public void setTabPaddingLeftRight(int paddingPx) {
        this.mTabPadding = paddingPx;
        updateTabStyles();
    }

    public int getTabPaddingLeftRight() {
        return this.mTabPadding;
    }

    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mCurrentPage = savedState.currentPage;
        requestLayout();
    }

    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.currentPage = this.mCurrentPage;
        return savedState;
    }
}
