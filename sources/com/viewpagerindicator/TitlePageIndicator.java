package com.viewpagerindicator;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import java.util.ArrayList;

public class TitlePageIndicator extends View implements PageIndicator {
    private static final float BOLD_FADE_PERCENTAGE = 0.05f;
    private static final String EMPTY_TITLE = "";
    private static final int INVALID_POINTER = -1;
    private static final float SELECTION_FADE_PERCENTAGE = 0.25f;
    private int mActivePointerId;
    private boolean mBoldText;
    private final Rect mBounds;
    private OnCenterItemClickListener mCenterItemClickListener;
    private float mClipPadding;
    private int mColorSelected;
    private int mColorText;
    private int mCurrentPage;
    private float mFooterIndicatorHeight;
    private IndicatorStyle mFooterIndicatorStyle;
    private float mFooterIndicatorUnderlinePadding;
    private float mFooterLineHeight;
    private float mFooterPadding;
    private boolean mIsDragging;
    private float mLastMotionX;
    private LinePosition mLinePosition;
    private OnPageChangeListener mListener;
    private float mPageOffset;
    private final Paint mPaintFooterIndicator;
    private final Paint mPaintFooterLine;
    private final Paint mPaintText;
    private Path mPath;
    private int mScrollState;
    private float mTitlePadding;
    private float mTopPadding;
    private int mTouchSlop;
    private ViewPager mViewPager;

    public enum IndicatorStyle {
        None(0),
        Triangle(1),
        Underline(2);
        
        public final int value;

        private IndicatorStyle(int value) {
            this.value = value;
        }

        public static IndicatorStyle fromValue(int value) {
            for (IndicatorStyle style : values()) {
                if (style.value == value) {
                    return style;
                }
            }
            return null;
        }
    }

    public enum LinePosition {
        Bottom(0),
        Top(1);
        
        public final int value;

        private LinePosition(int value) {
            this.value = value;
        }

        public static LinePosition fromValue(int value) {
            for (LinePosition position : values()) {
                if (position.value == value) {
                    return position;
                }
            }
            return null;
        }
    }

    public interface OnCenterItemClickListener {
        void onCenterItemClick(int i);
    }

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new C07021();
        int currentPage;

        /* renamed from: com.viewpagerindicator.TitlePageIndicator$SavedState$1 */
        static class C07021 implements Creator<SavedState> {
            C07021() {
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

    public TitlePageIndicator(Context context) {
        this(context, null);
    }

    public TitlePageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, C0695R.attr.vpiTitlePageIndicatorStyle);
    }

    public TitlePageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mCurrentPage = -1;
        this.mPaintText = new Paint();
        this.mPath = new Path();
        this.mBounds = new Rect();
        this.mPaintFooterLine = new Paint();
        this.mPaintFooterIndicator = new Paint();
        this.mLastMotionX = -1.0f;
        this.mActivePointerId = -1;
        if (!isInEditMode()) {
            Resources res = getResources();
            int defaultFooterColor = res.getColor(C0695R.color.default_title_indicator_footer_color);
            float defaultFooterLineHeight = res.getDimension(C0695R.dimen.default_title_indicator_footer_line_height);
            int defaultFooterIndicatorStyle = res.getInteger(C0695R.integer.default_title_indicator_footer_indicator_style);
            float defaultFooterIndicatorHeight = res.getDimension(C0695R.dimen.default_title_indicator_footer_indicator_height);
            float defaultFooterIndicatorUnderlinePadding = res.getDimension(C0695R.dimen.default_title_indicator_footer_indicator_underline_padding);
            float defaultFooterPadding = res.getDimension(C0695R.dimen.default_title_indicator_footer_padding);
            int defaultLinePosition = res.getInteger(C0695R.integer.default_title_indicator_line_position);
            int defaultSelectedColor = res.getColor(C0695R.color.default_title_indicator_selected_color);
            boolean defaultSelectedBold = res.getBoolean(C0695R.bool.default_title_indicator_selected_bold);
            int defaultTextColor = res.getColor(C0695R.color.default_title_indicator_text_color);
            float defaultTextSize = res.getDimension(C0695R.dimen.default_title_indicator_text_size);
            float defaultTitlePadding = res.getDimension(C0695R.dimen.default_title_indicator_title_padding);
            float defaultClipPadding = res.getDimension(C0695R.dimen.default_title_indicator_clip_padding);
            float defaultTopPadding = res.getDimension(C0695R.dimen.default_title_indicator_top_padding);
            int defaultFooterColor2 = defaultFooterColor;
            boolean defaultSelectedBold2 = defaultSelectedBold;
            int defaultTextColor2 = defaultTextColor;
            float defaultTextSize2 = defaultTextSize;
            TypedArray a = context.obtainStyledAttributes(attrs, C0695R.styleable.TitlePageIndicator, defStyle, 0);
            r0.mFooterLineHeight = a.getDimension(C0695R.styleable.TitlePageIndicator_footerLineHeight, defaultFooterLineHeight);
            r0.mFooterIndicatorStyle = IndicatorStyle.fromValue(a.getInteger(C0695R.styleable.TitlePageIndicator_footerIndicatorStyle, defaultFooterIndicatorStyle));
            r0.mFooterIndicatorHeight = a.getDimension(C0695R.styleable.TitlePageIndicator_footerIndicatorHeight, defaultFooterIndicatorHeight);
            r0.mFooterIndicatorUnderlinePadding = a.getDimension(C0695R.styleable.TitlePageIndicator_footerIndicatorUnderlinePadding, defaultFooterIndicatorUnderlinePadding);
            r0.mFooterPadding = a.getDimension(C0695R.styleable.TitlePageIndicator_footerPadding, defaultFooterPadding);
            r0.mLinePosition = LinePosition.fromValue(a.getInteger(C0695R.styleable.TitlePageIndicator_linePosition, defaultLinePosition));
            r0.mTopPadding = a.getDimension(C0695R.styleable.TitlePageIndicator_topPadding, defaultTopPadding);
            r0.mTitlePadding = a.getDimension(C0695R.styleable.TitlePageIndicator_titlePadding, defaultTitlePadding);
            r0.mClipPadding = a.getDimension(C0695R.styleable.TitlePageIndicator_clipPadding, defaultClipPadding);
            r0.mColorSelected = a.getColor(C0695R.styleable.TitlePageIndicator_selectedColor, defaultSelectedColor);
            r0.mColorText = a.getColor(C0695R.styleable.TitlePageIndicator_android_textColor, defaultTextColor2);
            r0.mBoldText = a.getBoolean(C0695R.styleable.TitlePageIndicator_selectedBold, defaultSelectedBold2);
            float textSize = a.getDimension(C0695R.styleable.TitlePageIndicator_android_textSize, defaultTextSize2);
            int footerColor = a.getColor(C0695R.styleable.TitlePageIndicator_footerColor, defaultFooterColor2);
            r0.mPaintText.setTextSize(textSize);
            r0.mPaintText.setAntiAlias(true);
            r0.mPaintFooterLine.setStyle(Style.FILL_AND_STROKE);
            r0.mPaintFooterLine.setStrokeWidth(r0.mFooterLineHeight);
            r0.mPaintFooterLine.setColor(footerColor);
            r0.mPaintFooterIndicator.setStyle(Style.FILL_AND_STROKE);
            r0.mPaintFooterIndicator.setColor(footerColor);
            Drawable background = a.getDrawable(C0695R.styleable.TitlePageIndicator_android_background);
            if (background != null) {
                setBackgroundDrawable(background);
            }
            a.recycle();
            r0.mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(ViewConfiguration.get(context));
        }
    }

    public int getFooterColor() {
        return this.mPaintFooterLine.getColor();
    }

    public void setFooterColor(int footerColor) {
        this.mPaintFooterLine.setColor(footerColor);
        this.mPaintFooterIndicator.setColor(footerColor);
        invalidate();
    }

    public float getFooterLineHeight() {
        return this.mFooterLineHeight;
    }

    public void setFooterLineHeight(float footerLineHeight) {
        this.mFooterLineHeight = footerLineHeight;
        this.mPaintFooterLine.setStrokeWidth(this.mFooterLineHeight);
        invalidate();
    }

    public float getFooterIndicatorHeight() {
        return this.mFooterIndicatorHeight;
    }

    public void setFooterIndicatorHeight(float footerTriangleHeight) {
        this.mFooterIndicatorHeight = footerTriangleHeight;
        invalidate();
    }

    public float getFooterIndicatorPadding() {
        return this.mFooterPadding;
    }

    public void setFooterIndicatorPadding(float footerIndicatorPadding) {
        this.mFooterPadding = footerIndicatorPadding;
        invalidate();
    }

    public IndicatorStyle getFooterIndicatorStyle() {
        return this.mFooterIndicatorStyle;
    }

    public void setFooterIndicatorStyle(IndicatorStyle indicatorStyle) {
        this.mFooterIndicatorStyle = indicatorStyle;
        invalidate();
    }

    public LinePosition getLinePosition() {
        return this.mLinePosition;
    }

    public void setLinePosition(LinePosition linePosition) {
        this.mLinePosition = linePosition;
        invalidate();
    }

    public int getSelectedColor() {
        return this.mColorSelected;
    }

    public void setSelectedColor(int selectedColor) {
        this.mColorSelected = selectedColor;
        invalidate();
    }

    public boolean isSelectedBold() {
        return this.mBoldText;
    }

    public void setSelectedBold(boolean selectedBold) {
        this.mBoldText = selectedBold;
        invalidate();
    }

    public int getTextColor() {
        return this.mColorText;
    }

    public void setTextColor(int textColor) {
        this.mPaintText.setColor(textColor);
        this.mColorText = textColor;
        invalidate();
    }

    public float getTextSize() {
        return this.mPaintText.getTextSize();
    }

    public void setTextSize(float textSize) {
        this.mPaintText.setTextSize(textSize);
        invalidate();
    }

    public float getTitlePadding() {
        return this.mTitlePadding;
    }

    public void setTitlePadding(float titlePadding) {
        this.mTitlePadding = titlePadding;
        invalidate();
    }

    public float getTopPadding() {
        return this.mTopPadding;
    }

    public void setTopPadding(float topPadding) {
        this.mTopPadding = topPadding;
        invalidate();
    }

    public float getClipPadding() {
        return this.mClipPadding;
    }

    public void setClipPadding(float clipPadding) {
        this.mClipPadding = clipPadding;
        invalidate();
    }

    public void setTypeface(Typeface typeface) {
        this.mPaintText.setTypeface(typeface);
        invalidate();
    }

    public Typeface getTypeface() {
        return this.mPaintText.getTypeface();
    }

    protected void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        super.onDraw(canvas);
        ViewPager viewPager = this.mViewPager;
        if (viewPager != null) {
            int count = viewPager.getAdapter().getCount();
            if (count != 0) {
                ArrayList<Rect> bounds;
                int boundsSize;
                int countMinusOne;
                float halfWidth;
                int left;
                float leftClip;
                int width;
                int height;
                int right;
                float rightClip;
                int page;
                float f;
                int i;
                float offsetPercent;
                boolean currentSelected;
                boolean currentBold;
                float selectedPercent;
                Rect curPageBound;
                float curPageWidth;
                Rect bound;
                float curPageWidth2;
                int w;
                Rect curPageWidth3;
                Rect curPageBound2;
                int width2;
                int w2;
                boolean currentPage;
                CharSequence pageTitle;
                Paint paint;
                boolean z;
                Rect rightBound;
                int w3;
                Rect bound2;
                int page2;
                int right2;
                int count2;
                float footerLineHeight;
                float heightMinusLine;
                float f2;
                Rect underlineBounds;
                float rightPlusPadding;
                int leftMinusPadding;
                if (r0.mCurrentPage == -1) {
                    viewPager = r0.mViewPager;
                    if (viewPager != null) {
                        r0.mCurrentPage = viewPager.getCurrentItem();
                        bounds = calculateAllBounds(r0.mPaintText);
                        boundsSize = bounds.size();
                        if (r0.mCurrentPage < boundsSize) {
                            setCurrentItem(boundsSize - 1);
                        }
                        countMinusOne = count - 1;
                        halfWidth = ((float) getWidth()) / 2.0f;
                        left = getLeft();
                        leftClip = ((float) left) + r0.mClipPadding;
                        width = getWidth();
                        height = getHeight();
                        right = left + width;
                        rightClip = ((float) right) - r0.mClipPadding;
                        page = r0.mCurrentPage;
                        f = r0.mPageOffset;
                        if (((double) f) > 0.5d) {
                            i = page;
                            offsetPercent = r0.mPageOffset;
                        } else {
                            i = page + 1;
                            offsetPercent = 1.0f - f;
                        }
                        currentSelected = offsetPercent > SELECTION_FADE_PERCENTAGE;
                        currentBold = offsetPercent > BOLD_FADE_PERCENTAGE;
                        selectedPercent = (SELECTION_FADE_PERCENTAGE - offsetPercent) / SELECTION_FADE_PERCENTAGE;
                        curPageBound = (Rect) bounds.get(r0.mCurrentPage);
                        curPageWidth = (float) (curPageBound.right - curPageBound.left);
                        if (((float) curPageBound.left) < leftClip) {
                            clipViewOnTheLeft(curPageBound, curPageWidth, left);
                        }
                        if (((float) curPageBound.right) > rightClip) {
                            clipViewOnTheRight(curPageBound, curPageWidth, right);
                        }
                        page = r0.mCurrentPage;
                        if (page <= 0) {
                            page--;
                            while (page >= 0) {
                                bound = (Rect) bounds.get(page);
                                if (((float) bound.left) >= leftClip) {
                                    curPageWidth2 = curPageWidth;
                                    w = bound.right - bound.left;
                                    clipViewOnTheLeft(bound, (float) w, left);
                                    curPageWidth3 = (Rect) bounds.get(page + 1);
                                    curPageBound2 = curPageBound;
                                    width2 = width;
                                    if (((float) bound.right) + r0.mTitlePadding > ((float) curPageWidth3.left)) {
                                        bound.left = (int) (((float) (curPageWidth3.left - w)) - r0.mTitlePadding);
                                        bound.right = bound.left + w;
                                    }
                                } else {
                                    curPageWidth2 = curPageWidth;
                                    curPageBound2 = curPageBound;
                                    width2 = width;
                                }
                                page--;
                                curPageWidth = curPageWidth2;
                                curPageBound = curPageBound2;
                                width = width2;
                            }
                            curPageBound2 = curPageBound;
                            width2 = width;
                        } else {
                            curPageBound2 = curPageBound;
                            width2 = width;
                        }
                        page = r0.mCurrentPage;
                        if (page < countMinusOne) {
                            for (page++; page < count; page++) {
                                bound = (Rect) bounds.get(page);
                                if (((float) bound.right) > rightClip) {
                                    w2 = bound.right - bound.left;
                                    clipViewOnTheRight(bound, (float) w2, right);
                                    curPageBound = (Rect) bounds.get(page - 1);
                                    if (((float) bound.left) - r0.mTitlePadding < ((float) curPageBound.right)) {
                                        bound.left = (int) (((float) curPageBound.right) + r0.mTitlePadding);
                                        bound.right = bound.left + w2;
                                    }
                                }
                            }
                        }
                        w = r0.mColorText >>> 24;
                        width = 0;
                        while (width < count) {
                            curPageBound = (Rect) bounds.get(width);
                            if (curPageBound.left > left) {
                                if (curPageBound.left >= right) {
                                }
                                currentPage = width == i;
                                pageTitle = getTitle(width);
                                paint = r0.mPaintText;
                                z = currentPage && currentBold && r0.mBoldText;
                                paint.setFakeBoldText(z);
                                r0.mPaintText.setColor(r0.mColorText);
                                if (!currentPage && currentSelected) {
                                    r0.mPaintText.setAlpha(w - ((int) (((float) w) * selectedPercent)));
                                }
                                if (width >= boundsSize - 1) {
                                    rightBound = (Rect) bounds.get(width + 1);
                                    if (((float) curPageBound.right) + r0.mTitlePadding > ((float) rightBound.left)) {
                                        w3 = curPageBound.right - curPageBound.left;
                                        curPageBound.left = (int) (((float) (rightBound.left - w3)) - r0.mTitlePadding);
                                        curPageBound.right = curPageBound.left + w3;
                                    }
                                }
                                bound2 = curPageBound;
                                page2 = i;
                                right2 = right;
                                count2 = count;
                                count = width2;
                                width2 = width;
                                canvas.drawText(pageTitle, 0, pageTitle.length(), (float) curPageBound.left, ((float) curPageBound.bottom) + r0.mTopPadding, r0.mPaintText);
                                if (currentPage || !currentSelected) {
                                    width = width2 + 1;
                                    width2 = count;
                                    right = right2;
                                    count = count2;
                                    i = page2;
                                } else {
                                    r0.mPaintText.setColor(r0.mColorSelected);
                                    r0.mPaintText.setAlpha((int) (((float) (r0.mColorSelected >>> 24)) * selectedPercent));
                                    Rect bound3 = bound2;
                                    canvas.drawText(pageTitle, 0, pageTitle.length(), (float) bound3.left, ((float) bound3.bottom) + r0.mTopPadding, r0.mPaintText);
                                    width = width2 + 1;
                                    width2 = count;
                                    right = right2;
                                    count = count2;
                                    i = page2;
                                }
                            }
                            if (curPageBound.right > left || curPageBound.right >= right) {
                                page2 = i;
                                right2 = right;
                                count2 = count;
                                count = width2;
                                width2 = width;
                                width = width2 + 1;
                                width2 = count;
                                right = right2;
                                count = count2;
                                i = page2;
                            } else {
                                if (width == i) {
                                }
                                currentPage = width == i;
                                pageTitle = getTitle(width);
                                paint = r0.mPaintText;
                                if (!currentPage) {
                                }
                                paint.setFakeBoldText(z);
                                r0.mPaintText.setColor(r0.mColorText);
                                if (!currentPage) {
                                }
                                if (width >= boundsSize - 1) {
                                    rightBound = (Rect) bounds.get(width + 1);
                                    if (((float) curPageBound.right) + r0.mTitlePadding > ((float) rightBound.left)) {
                                    } else {
                                        w3 = curPageBound.right - curPageBound.left;
                                        curPageBound.left = (int) (((float) (rightBound.left - w3)) - r0.mTitlePadding);
                                        curPageBound.right = curPageBound.left + w3;
                                    }
                                }
                                bound2 = curPageBound;
                                page2 = i;
                                right2 = right;
                                count2 = count;
                                count = width2;
                                width2 = width;
                                canvas.drawText(pageTitle, 0, pageTitle.length(), (float) curPageBound.left, ((float) curPageBound.bottom) + r0.mTopPadding, r0.mPaintText);
                                if (currentPage) {
                                }
                                width = width2 + 1;
                                width2 = count;
                                right = right2;
                                count = count2;
                                i = page2;
                            }
                        }
                        page2 = i;
                        right2 = right;
                        count2 = count;
                        count = width2;
                        width2 = width;
                        footerLineHeight = r0.mFooterLineHeight;
                        f = r0.mFooterIndicatorHeight;
                        if (r0.mLinePosition != LinePosition.Top) {
                            curPageWidth = -f;
                            f = -footerLineHeight;
                            page = 0;
                        } else {
                            curPageWidth = f;
                            f = footerLineHeight;
                            page = height;
                        }
                        r0.mPath.reset();
                        r0.mPath.moveTo(0.0f, ((float) page) - (f / 2.0f));
                        r0.mPath.lineTo((float) count, ((float) page) - (f / 2.0f));
                        r0.mPath.close();
                        canvas2.drawPath(r0.mPath, r0.mPaintFooterLine);
                        heightMinusLine = ((float) page) - f;
                        switch (r0.mFooterIndicatorStyle) {
                            case Triangle:
                                f2 = f;
                                r0.mPath.reset();
                                r0.mPath.moveTo(halfWidth, heightMinusLine - curPageWidth);
                                r0.mPath.lineTo(halfWidth + curPageWidth, heightMinusLine);
                                r0.mPath.lineTo(halfWidth - curPageWidth, heightMinusLine);
                                r0.mPath.close();
                                canvas2.drawPath(r0.mPath, r0.mPaintFooterIndicator);
                                break;
                            case Underline:
                                if (!currentSelected) {
                                    i = page2;
                                    if (i < boundsSize) {
                                        underlineBounds = (Rect) bounds.get(i);
                                        height = page;
                                        rightPlusPadding = ((float) underlineBounds.right) + r0.mFooterIndicatorUnderlinePadding;
                                        f2 = f;
                                        page = ((float) underlineBounds.left) - r0.mFooterIndicatorUnderlinePadding;
                                        f = heightMinusLine - curPageWidth;
                                        page2 = i;
                                        r0.mPath.reset();
                                        r0.mPath.moveTo(page, heightMinusLine);
                                        r0.mPath.lineTo(rightPlusPadding, heightMinusLine);
                                        r0.mPath.lineTo(rightPlusPadding, f);
                                        r0.mPath.lineTo(page, f);
                                        r0.mPath.close();
                                        leftMinusPadding = page;
                                        r0.mPaintFooterIndicator.setAlpha((int) (selectedPercent * 1132396544));
                                        canvas2.drawPath(r0.mPath, r0.mPaintFooterIndicator);
                                        r0.mPaintFooterIndicator.setAlpha(255);
                                        break;
                                    }
                                    height = page;
                                    f2 = f;
                                    page2 = i;
                                    break;
                                }
                                f2 = f;
                                break;
                            default:
                                height = page;
                                break;
                        }
                        return;
                    }
                }
                bounds = calculateAllBounds(r0.mPaintText);
                boundsSize = bounds.size();
                if (r0.mCurrentPage < boundsSize) {
                    countMinusOne = count - 1;
                    halfWidth = ((float) getWidth()) / 2.0f;
                    left = getLeft();
                    leftClip = ((float) left) + r0.mClipPadding;
                    width = getWidth();
                    height = getHeight();
                    right = left + width;
                    rightClip = ((float) right) - r0.mClipPadding;
                    page = r0.mCurrentPage;
                    f = r0.mPageOffset;
                    if (((double) f) > 0.5d) {
                        i = page + 1;
                        offsetPercent = 1.0f - f;
                    } else {
                        i = page;
                        offsetPercent = r0.mPageOffset;
                    }
                    if (offsetPercent > SELECTION_FADE_PERCENTAGE) {
                    }
                    currentSelected = offsetPercent > SELECTION_FADE_PERCENTAGE;
                    if (offsetPercent > BOLD_FADE_PERCENTAGE) {
                    }
                    currentBold = offsetPercent > BOLD_FADE_PERCENTAGE;
                    selectedPercent = (SELECTION_FADE_PERCENTAGE - offsetPercent) / SELECTION_FADE_PERCENTAGE;
                    curPageBound = (Rect) bounds.get(r0.mCurrentPage);
                    curPageWidth = (float) (curPageBound.right - curPageBound.left);
                    if (((float) curPageBound.left) < leftClip) {
                        clipViewOnTheLeft(curPageBound, curPageWidth, left);
                    }
                    if (((float) curPageBound.right) > rightClip) {
                        clipViewOnTheRight(curPageBound, curPageWidth, right);
                    }
                    page = r0.mCurrentPage;
                    if (page <= 0) {
                        curPageBound2 = curPageBound;
                        width2 = width;
                    } else {
                        page--;
                        while (page >= 0) {
                            bound = (Rect) bounds.get(page);
                            if (((float) bound.left) >= leftClip) {
                                curPageWidth2 = curPageWidth;
                                curPageBound2 = curPageBound;
                                width2 = width;
                            } else {
                                curPageWidth2 = curPageWidth;
                                w = bound.right - bound.left;
                                clipViewOnTheLeft(bound, (float) w, left);
                                curPageWidth3 = (Rect) bounds.get(page + 1);
                                curPageBound2 = curPageBound;
                                width2 = width;
                                if (((float) bound.right) + r0.mTitlePadding > ((float) curPageWidth3.left)) {
                                    bound.left = (int) (((float) (curPageWidth3.left - w)) - r0.mTitlePadding);
                                    bound.right = bound.left + w;
                                }
                            }
                            page--;
                            curPageWidth = curPageWidth2;
                            curPageBound = curPageBound2;
                            width = width2;
                        }
                        curPageBound2 = curPageBound;
                        width2 = width;
                    }
                    page = r0.mCurrentPage;
                    if (page < countMinusOne) {
                        for (page++; page < count; page++) {
                            bound = (Rect) bounds.get(page);
                            if (((float) bound.right) > rightClip) {
                                w2 = bound.right - bound.left;
                                clipViewOnTheRight(bound, (float) w2, right);
                                curPageBound = (Rect) bounds.get(page - 1);
                                if (((float) bound.left) - r0.mTitlePadding < ((float) curPageBound.right)) {
                                    bound.left = (int) (((float) curPageBound.right) + r0.mTitlePadding);
                                    bound.right = bound.left + w2;
                                }
                            }
                        }
                    }
                    w = r0.mColorText >>> 24;
                    width = 0;
                    while (width < count) {
                        curPageBound = (Rect) bounds.get(width);
                        if (curPageBound.left > left) {
                            if (curPageBound.left >= right) {
                            }
                            if (width == i) {
                            }
                            currentPage = width == i;
                            pageTitle = getTitle(width);
                            paint = r0.mPaintText;
                            if (currentPage) {
                            }
                            paint.setFakeBoldText(z);
                            r0.mPaintText.setColor(r0.mColorText);
                            if (!currentPage) {
                            }
                            if (width >= boundsSize - 1) {
                                rightBound = (Rect) bounds.get(width + 1);
                                if (((float) curPageBound.right) + r0.mTitlePadding > ((float) rightBound.left)) {
                                    w3 = curPageBound.right - curPageBound.left;
                                    curPageBound.left = (int) (((float) (rightBound.left - w3)) - r0.mTitlePadding);
                                    curPageBound.right = curPageBound.left + w3;
                                }
                            }
                            bound2 = curPageBound;
                            page2 = i;
                            right2 = right;
                            count2 = count;
                            count = width2;
                            width2 = width;
                            canvas.drawText(pageTitle, 0, pageTitle.length(), (float) curPageBound.left, ((float) curPageBound.bottom) + r0.mTopPadding, r0.mPaintText);
                            if (currentPage) {
                            }
                            width = width2 + 1;
                            width2 = count;
                            right = right2;
                            count = count2;
                            i = page2;
                        }
                        if (curPageBound.right > left) {
                        }
                        page2 = i;
                        right2 = right;
                        count2 = count;
                        count = width2;
                        width2 = width;
                        width = width2 + 1;
                        width2 = count;
                        right = right2;
                        count = count2;
                        i = page2;
                    }
                    page2 = i;
                    right2 = right;
                    count2 = count;
                    count = width2;
                    width2 = width;
                    footerLineHeight = r0.mFooterLineHeight;
                    f = r0.mFooterIndicatorHeight;
                    if (r0.mLinePosition != LinePosition.Top) {
                        curPageWidth = f;
                        f = footerLineHeight;
                        page = height;
                    } else {
                        curPageWidth = -f;
                        f = -footerLineHeight;
                        page = 0;
                    }
                    r0.mPath.reset();
                    r0.mPath.moveTo(0.0f, ((float) page) - (f / 2.0f));
                    r0.mPath.lineTo((float) count, ((float) page) - (f / 2.0f));
                    r0.mPath.close();
                    canvas2.drawPath(r0.mPath, r0.mPaintFooterLine);
                    heightMinusLine = ((float) page) - f;
                    switch (r0.mFooterIndicatorStyle) {
                        case Triangle:
                            f2 = f;
                            r0.mPath.reset();
                            r0.mPath.moveTo(halfWidth, heightMinusLine - curPageWidth);
                            r0.mPath.lineTo(halfWidth + curPageWidth, heightMinusLine);
                            r0.mPath.lineTo(halfWidth - curPageWidth, heightMinusLine);
                            r0.mPath.close();
                            canvas2.drawPath(r0.mPath, r0.mPaintFooterIndicator);
                            break;
                        case Underline:
                            if (!currentSelected) {
                                f2 = f;
                                break;
                            }
                            i = page2;
                            if (i < boundsSize) {
                                height = page;
                                f2 = f;
                                page2 = i;
                                break;
                            }
                            underlineBounds = (Rect) bounds.get(i);
                            height = page;
                            rightPlusPadding = ((float) underlineBounds.right) + r0.mFooterIndicatorUnderlinePadding;
                            f2 = f;
                            page = ((float) underlineBounds.left) - r0.mFooterIndicatorUnderlinePadding;
                            f = heightMinusLine - curPageWidth;
                            page2 = i;
                            r0.mPath.reset();
                            r0.mPath.moveTo(page, heightMinusLine);
                            r0.mPath.lineTo(rightPlusPadding, heightMinusLine);
                            r0.mPath.lineTo(rightPlusPadding, f);
                            r0.mPath.lineTo(page, f);
                            r0.mPath.close();
                            leftMinusPadding = page;
                            r0.mPaintFooterIndicator.setAlpha((int) (selectedPercent * 1132396544));
                            canvas2.drawPath(r0.mPath, r0.mPaintFooterIndicator);
                            r0.mPaintFooterIndicator.setAlpha(255);
                            break;
                        default:
                            height = page;
                            break;
                    }
                    return;
                }
                setCurrentItem(boundsSize - 1);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (super.onTouchEvent(ev)) {
            return true;
        }
        ViewPager viewPager = this.mViewPager;
        int newPointerIndex = 0;
        if (viewPager != null) {
            if (viewPager.getAdapter().getCount() != 0) {
                int action = ev.getAction() & 255;
                int count;
                switch (action) {
                    case 0:
                        this.mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                        this.mLastMotionX = ev.getX();
                        break;
                    case 1:
                    case 3:
                        if (!this.mIsDragging) {
                            count = this.mViewPager.getAdapter().getCount();
                            int width = getWidth();
                            float halfWidth = ((float) width) / 2.0f;
                            float sixthWidth = ((float) width) / 6.0f;
                            float leftThird = halfWidth - sixthWidth;
                            float rightThird = halfWidth + sixthWidth;
                            float eventX = ev.getX();
                            int i;
                            if (eventX < leftThird) {
                                i = this.mCurrentPage;
                                if (i > 0) {
                                    if (action != 3) {
                                        this.mViewPager.setCurrentItem(i - 1);
                                    }
                                    return true;
                                }
                            } else if (eventX > rightThird) {
                                i = this.mCurrentPage;
                                if (i < count - 1) {
                                    if (action != 3) {
                                        this.mViewPager.setCurrentItem(i + 1);
                                    }
                                    return true;
                                }
                            } else {
                                OnCenterItemClickListener onCenterItemClickListener = this.mCenterItemClickListener;
                                if (onCenterItemClickListener != null && action != 3) {
                                    onCenterItemClickListener.onCenterItemClick(this.mCurrentPage);
                                }
                            }
                        }
                        this.mIsDragging = false;
                        this.mActivePointerId = -1;
                        if (!this.mViewPager.isFakeDragging()) {
                            break;
                        }
                        this.mViewPager.endFakeDrag();
                        break;
                    case 2:
                        float x = MotionEventCompat.getX(ev, MotionEventCompat.findPointerIndex(ev, this.mActivePointerId));
                        float deltaX = x - this.mLastMotionX;
                        if (!this.mIsDragging) {
                            if (Math.abs(deltaX) > ((float) this.mTouchSlop)) {
                                this.mIsDragging = true;
                            }
                        }
                        if (!this.mIsDragging) {
                            break;
                        }
                        this.mLastMotionX = x;
                        if (!this.mViewPager.isFakeDragging()) {
                            if (!this.mViewPager.beginFakeDrag()) {
                                break;
                            }
                        }
                        this.mViewPager.fakeDragBy(deltaX);
                        break;
                    case 5:
                        newPointerIndex = MotionEventCompat.getActionIndex(ev);
                        this.mLastMotionX = MotionEventCompat.getX(ev, newPointerIndex);
                        this.mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                        break;
                    case 6:
                        count = MotionEventCompat.getActionIndex(ev);
                        if (MotionEventCompat.getPointerId(ev, count) == this.mActivePointerId) {
                            if (count == 0) {
                                newPointerIndex = 1;
                            }
                            this.mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                        }
                        this.mLastMotionX = MotionEventCompat.getX(ev, MotionEventCompat.findPointerIndex(ev, this.mActivePointerId));
                        break;
                    default:
                        break;
                }
                return true;
            }
        }
        return false;
    }

    private void clipViewOnTheRight(Rect curViewBound, float curViewWidth, int right) {
        curViewBound.right = (int) (((float) right) - this.mClipPadding);
        curViewBound.left = (int) (((float) curViewBound.right) - curViewWidth);
    }

    private void clipViewOnTheLeft(Rect curViewBound, float curViewWidth, int left) {
        float f = (float) left;
        float f2 = this.mClipPadding;
        curViewBound.left = (int) (f + f2);
        curViewBound.right = (int) (f2 + curViewWidth);
    }

    private ArrayList<Rect> calculateAllBounds(Paint paint) {
        ArrayList<Rect> list = new ArrayList();
        int count = this.mViewPager.getAdapter().getCount();
        int width = getWidth();
        int halfWidth = width / 2;
        for (int i = 0; i < count; i++) {
            Rect bounds = calcBounds(i, paint);
            int w = bounds.right - bounds.left;
            int h = bounds.bottom - bounds.top;
            bounds.left = (int) ((((float) halfWidth) - (((float) w) / 2.0f)) + ((((float) (i - this.mCurrentPage)) - this.mPageOffset) * ((float) width)));
            bounds.right = bounds.left + w;
            bounds.top = 0;
            bounds.bottom = h;
            list.add(bounds);
        }
        return list;
    }

    private Rect calcBounds(int index, Paint paint) {
        Rect bounds = new Rect();
        CharSequence title = getTitle(index);
        bounds.right = (int) paint.measureText(title, 0, title.length());
        bounds.bottom = (int) (paint.descent() - paint.ascent());
        return bounds;
    }

    public void setViewPager(ViewPager view) {
        ViewPager viewPager = this.mViewPager;
        if (viewPager != view) {
            if (viewPager != null) {
                viewPager.setOnPageChangeListener(null);
            }
            if (view.getAdapter() != null) {
                this.mViewPager = view;
                this.mViewPager.setOnPageChangeListener(this);
                invalidate();
                return;
            }
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
    }

    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    public void notifyDataSetChanged() {
        invalidate();
    }

    public void setOnCenterItemClickListener(OnCenterItemClickListener listener) {
        this.mCenterItemClickListener = listener;
    }

    public void setCurrentItem(int item) {
        ViewPager viewPager = this.mViewPager;
        if (viewPager != null) {
            viewPager.setCurrentItem(item);
            this.mCurrentPage = item;
            invalidate();
            return;
        }
        throw new IllegalStateException("ViewPager has not been bound.");
    }

    public void onPageScrollStateChanged(int state) {
        this.mScrollState = state;
        OnPageChangeListener onPageChangeListener = this.mListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.mCurrentPage = position;
        this.mPageOffset = positionOffset;
        invalidate();
        OnPageChangeListener onPageChangeListener = this.mListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    public void onPageSelected(int position) {
        if (this.mScrollState == 0) {
            this.mCurrentPage = position;
            invalidate();
        }
        OnPageChangeListener onPageChangeListener = this.mListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageSelected(position);
        }
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mListener = listener;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float height;
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        if (MeasureSpec.getMode(heightMeasureSpec) == 1073741824) {
            height = (float) MeasureSpec.getSize(heightMeasureSpec);
        } else {
            this.mBounds.setEmpty();
            this.mBounds.bottom = (int) (this.mPaintText.descent() - this.mPaintText.ascent());
            height = ((((float) (this.mBounds.bottom - this.mBounds.top)) + this.mFooterLineHeight) + this.mFooterPadding) + this.mTopPadding;
            if (this.mFooterIndicatorStyle != IndicatorStyle.None) {
                height += this.mFooterIndicatorHeight;
            }
        }
        setMeasuredDimension(measuredWidth, (int) height);
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

    private CharSequence getTitle(int i) {
        CharSequence title = this.mViewPager.getAdapter().getPageTitle(i);
        if (title == null) {
            return "";
        }
        return title;
    }
}
