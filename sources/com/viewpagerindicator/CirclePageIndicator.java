package com.viewpagerindicator;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
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

public class CirclePageIndicator extends View implements PageIndicator {
    private static final int INVALID_POINTER = -1;
    private int mActivePointerId;
    private boolean mCentered;
    private int mCurrentPage;
    private float mGap;
    private boolean mIsDragging;
    private float mLastMotionX;
    private OnPageChangeListener mListener;
    private int mOrientation;
    private float mPageOffset;
    private final Paint mPaintFill;
    private final Paint mPaintPageFill;
    private final Paint mPaintStroke;
    private float mRadius;
    private int mScrollState;
    private boolean mSnap;
    private int mSnapPage;
    private int mTouchSlop;
    private ViewPager mViewPager;

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new C06911();
        int currentPage;

        /* renamed from: com.viewpagerindicator.CirclePageIndicator$SavedState$1 */
        static class C06911 implements Creator<SavedState> {
            C06911() {
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

    public CirclePageIndicator(Context context) {
        this(context, null);
    }

    public CirclePageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, C0695R.attr.vpiCirclePageIndicatorStyle);
    }

    public CirclePageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mPaintPageFill = new Paint(1);
        this.mPaintStroke = new Paint(1);
        this.mPaintFill = new Paint(1);
        this.mLastMotionX = -1.0f;
        this.mActivePointerId = -1;
        if (!isInEditMode()) {
            Resources res = getResources();
            int defaultPageColor = res.getColor(C0695R.color.default_circle_indicator_page_color);
            int defaultFillColor = res.getColor(C0695R.color.default_circle_indicator_fill_color);
            int defaultOrientation = res.getInteger(C0695R.integer.default_circle_indicator_orientation);
            int defaultStrokeColor = res.getColor(C0695R.color.default_circle_indicator_stroke_color);
            float defaultStrokeWidth = res.getDimension(C0695R.dimen.default_circle_indicator_stroke_width);
            float defaultRadius = res.getDimension(C0695R.dimen.default_circle_indicator_radius);
            float defaultGap = res.getDimension(C0695R.dimen.default_circle_indicator_gap);
            boolean defaultCentered = res.getBoolean(C0695R.bool.default_circle_indicator_centered);
            boolean defaultSnap = res.getBoolean(C0695R.bool.default_circle_indicator_snap);
            TypedArray a = context.obtainStyledAttributes(attrs, C0695R.styleable.CirclePageIndicator, defStyle, 0);
            r0.mCentered = a.getBoolean(C0695R.styleable.CirclePageIndicator_centered, defaultCentered);
            r0.mOrientation = a.getInt(C0695R.styleable.CirclePageIndicator_android_orientation, defaultOrientation);
            r0.mPaintPageFill.setStyle(Style.FILL);
            r0.mPaintPageFill.setColor(a.getColor(C0695R.styleable.CirclePageIndicator_pageColor, defaultPageColor));
            r0.mPaintStroke.setStyle(Style.STROKE);
            r0.mPaintStroke.setColor(a.getColor(C0695R.styleable.CirclePageIndicator_strokeColor, defaultStrokeColor));
            r0.mPaintStroke.setStrokeWidth(a.getDimension(C0695R.styleable.CirclePageIndicator_strokeWidth, defaultStrokeWidth));
            r0.mPaintFill.setStyle(Style.FILL);
            r0.mPaintFill.setColor(a.getColor(C0695R.styleable.CirclePageIndicator_fillColor, defaultFillColor));
            r0.mRadius = a.getDimension(C0695R.styleable.CirclePageIndicator_radius, defaultRadius);
            r0.mGap = a.getDimension(C0695R.styleable.CirclePageIndicator_gap, defaultGap);
            r0.mSnap = a.getBoolean(C0695R.styleable.CirclePageIndicator_snap, defaultSnap);
            Drawable background = a.getDrawable(C0695R.styleable.CirclePageIndicator_android_background);
            if (background != null) {
                setBackgroundDrawable(background);
            }
            a.recycle();
            r0.mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(ViewConfiguration.get(context));
        }
    }

    public void setCentered(boolean centered) {
        this.mCentered = centered;
        invalidate();
    }

    public boolean isCentered() {
        return this.mCentered;
    }

    public void setPageColor(int pageColor) {
        this.mPaintPageFill.setColor(pageColor);
        invalidate();
    }

    public int getPageColor() {
        return this.mPaintPageFill.getColor();
    }

    public void setFillColor(int fillColor) {
        this.mPaintFill.setColor(fillColor);
        invalidate();
    }

    public int getFillColor() {
        return this.mPaintFill.getColor();
    }

    public void setOrientation(int orientation) {
        switch (orientation) {
            case 0:
            case 1:
                this.mOrientation = orientation;
                requestLayout();
                return;
            default:
                throw new IllegalArgumentException("Orientation must be either HORIZONTAL or VERTICAL.");
        }
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public void setStrokeColor(int strokeColor) {
        this.mPaintStroke.setColor(strokeColor);
        invalidate();
    }

    public int getStrokeColor() {
        return this.mPaintStroke.getColor();
    }

    public void setStrokeWidth(float strokeWidth) {
        this.mPaintStroke.setStrokeWidth(strokeWidth);
        invalidate();
    }

    public float getStrokeWidth() {
        return this.mPaintStroke.getStrokeWidth();
    }

    public void setRadius(float radius) {
        this.mRadius = radius;
        invalidate();
    }

    public float getRadius() {
        return this.mRadius;
    }

    public void setGap(float gap) {
        this.mGap = gap;
        invalidate();
    }

    public float getGapSize() {
        return this.mGap;
    }

    public void setSnap(boolean snap) {
        this.mSnap = snap;
        invalidate();
    }

    public boolean isSnap() {
        return this.mSnap;
    }

    protected void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        super.onDraw(canvas);
        ViewPager viewPager = this.mViewPager;
        if (viewPager != null) {
            int count = viewPager.getAdapter().getCount();
            if (count != 0) {
                if (r0.mCurrentPage >= count) {
                    setCurrentItem(count - 1);
                    return;
                }
                int longSize;
                int longPaddingBefore;
                int longPaddingAfter;
                int shortPaddingBefore;
                float drawLong;
                float dX;
                if (r0.mOrientation == 0) {
                    longSize = getWidth();
                    longPaddingBefore = getPaddingLeft();
                    longPaddingAfter = getPaddingRight();
                    shortPaddingBefore = getPaddingTop();
                } else {
                    longSize = getHeight();
                    longPaddingBefore = getPaddingTop();
                    longPaddingAfter = getPaddingBottom();
                    shortPaddingBefore = getPaddingLeft();
                }
                float f = r0.mRadius;
                float threeRadius = (3.0f * f) + r0.mGap;
                float shortOffset = (((float) shortPaddingBefore) + f) + (r0.mPaintStroke.getStrokeWidth() / 2.0f);
                f = (((float) longPaddingBefore) + r0.mRadius) + (r0.mPaintStroke.getStrokeWidth() / 2.0f);
                if (r0.mCentered) {
                    f += (((float) ((longSize - longPaddingBefore) - longPaddingAfter)) - (((((float) count) * threeRadius) - r0.mRadius) - r0.mGap)) / 2.0f;
                }
                float pageFillRadius = r0.mRadius;
                if (r0.mPaintStroke.getStrokeWidth() > 0.0f) {
                    pageFillRadius -= r0.mPaintStroke.getStrokeWidth() / 2.0f;
                }
                int iLoop = 0;
                while (iLoop < count) {
                    float dX2;
                    float dY;
                    int count2;
                    drawLong = (((float) iLoop) * threeRadius) + f;
                    if (r0.mOrientation == 0) {
                        dX2 = drawLong;
                        dY = shortOffset;
                    } else {
                        dX2 = shortOffset;
                        dY = drawLong;
                    }
                    if (r0.mPaintPageFill.getAlpha() > 0) {
                        canvas2.drawCircle(dX2, dY, pageFillRadius, r0.mPaintPageFill);
                    }
                    float f2 = r0.mRadius;
                    if (pageFillRadius != f2) {
                        count2 = count;
                        canvas2.drawCircle(dX2, dY, f2, r0.mPaintStroke);
                    } else {
                        count2 = count;
                    }
                    iLoop++;
                    count = count2;
                }
                float cx = ((float) (r0.mSnap ? r0.mSnapPage : r0.mCurrentPage)) * threeRadius;
                if (!r0.mSnap) {
                    cx += r0.mPageOffset * threeRadius;
                }
                if (r0.mOrientation == 0) {
                    dX = f + cx;
                    drawLong = shortOffset;
                } else {
                    dX = shortOffset;
                    drawLong = f + cx;
                }
                canvas2.drawCircle(dX, drawLong, r0.mRadius, r0.mPaintFill);
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
                            if (this.mCurrentPage > 0 && ev.getX() < halfWidth - sixthWidth) {
                                if (action != 3) {
                                    this.mViewPager.setCurrentItem(this.mCurrentPage - 1);
                                }
                                return true;
                            } else if (this.mCurrentPage < count - 1 && ev.getX() > halfWidth + sixthWidth) {
                                if (action != 3) {
                                    this.mViewPager.setCurrentItem(this.mCurrentPage + 1);
                                }
                                return true;
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

    public void notifyDataSetChanged() {
        invalidate();
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
        OnPageChangeListener onPageChangeListener;
        if (!this.mSnap) {
            if (this.mScrollState != 0) {
                onPageChangeListener = this.mListener;
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageSelected(position);
                }
            }
        }
        this.mCurrentPage = position;
        this.mSnapPage = position;
        invalidate();
        onPageChangeListener = this.mListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageSelected(position);
        }
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mListener = listener;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mOrientation == 0) {
            setMeasuredDimension(measureLong(widthMeasureSpec), measureShort(heightMeasureSpec));
        } else {
            setMeasuredDimension(measureShort(widthMeasureSpec), measureLong(heightMeasureSpec));
        }
    }

    private int measureLong(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode != 1073741824) {
            ViewPager viewPager = this.mViewPager;
            if (viewPager != null) {
                int count = viewPager.getAdapter().getCount();
                float paddingLeft = (float) (getPaddingLeft() + getPaddingRight());
                float f = (float) count;
                float f2 = this.mRadius;
                int result = (int) (((paddingLeft + (f * ((2.0f * f2) + 1.0f))) + (((float) (count - 1)) * (f2 + this.mGap))) + this.mPaintStroke.getStrokeWidth());
                if (specMode == Integer.MIN_VALUE) {
                    return Math.min(result, specSize);
                }
                return result;
            }
        }
        return specSize;
    }

    private int measureShort(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == 1073741824) {
            return specSize;
        }
        int result = (int) (((((this.mRadius * 2.0f) + ((float) getPaddingTop())) + ((float) getPaddingBottom())) + 1.0f) + this.mPaintStroke.getStrokeWidth());
        if (specMode == Integer.MIN_VALUE) {
            return Math.min(result, specSize);
        }
        return result;
    }

    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mCurrentPage = savedState.currentPage;
        this.mSnapPage = savedState.currentPage;
        requestLayout();
    }

    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.currentPage = this.mCurrentPage;
        return savedState;
    }
}
