package com.viewpagerindicator;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
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

public class LinePageIndicator extends View implements PageIndicator {
    private static final int INVALID_POINTER = -1;
    private int mActivePointerId;
    private boolean mCentered;
    private int mCurrentPage;
    private float mGapWidth;
    private boolean mIsDragging;
    private float mLastMotionX;
    private float mLineWidth;
    private OnPageChangeListener mListener;
    private final Paint mPaintSelected;
    private final Paint mPaintUnselected;
    private int mTouchSlop;
    private ViewPager mViewPager;

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new C06941();
        int currentPage;

        /* renamed from: com.viewpagerindicator.LinePageIndicator$SavedState$1 */
        static class C06941 implements Creator<SavedState> {
            C06941() {
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

    public LinePageIndicator(Context context) {
        this(context, null);
    }

    public LinePageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, C0695R.attr.vpiLinePageIndicatorStyle);
    }

    public LinePageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mPaintUnselected = new Paint(1);
        this.mPaintSelected = new Paint(1);
        this.mLastMotionX = -1.0f;
        this.mActivePointerId = -1;
        if (!isInEditMode()) {
            Resources res = getResources();
            int defaultSelectedColor = res.getColor(C0695R.color.default_line_indicator_selected_color);
            int defaultUnselectedColor = res.getColor(C0695R.color.default_line_indicator_unselected_color);
            float defaultLineWidth = res.getDimension(C0695R.dimen.default_line_indicator_line_width);
            float defaultGapWidth = res.getDimension(C0695R.dimen.default_line_indicator_gap_width);
            float defaultStrokeWidth = res.getDimension(C0695R.dimen.default_line_indicator_stroke_width);
            boolean defaultCentered = res.getBoolean(C0695R.bool.default_line_indicator_centered);
            TypedArray a = context.obtainStyledAttributes(attrs, C0695R.styleable.LinePageIndicator, defStyle, 0);
            this.mCentered = a.getBoolean(C0695R.styleable.LinePageIndicator_centered, defaultCentered);
            this.mLineWidth = a.getDimension(C0695R.styleable.LinePageIndicator_lineWidth, defaultLineWidth);
            this.mGapWidth = a.getDimension(C0695R.styleable.LinePageIndicator_gapWidth, defaultGapWidth);
            setStrokeWidth(a.getDimension(C0695R.styleable.LinePageIndicator_strokeWidth, defaultStrokeWidth));
            this.mPaintUnselected.setColor(a.getColor(C0695R.styleable.LinePageIndicator_unselectedColor, defaultUnselectedColor));
            this.mPaintSelected.setColor(a.getColor(C0695R.styleable.LinePageIndicator_selectedColor, defaultSelectedColor));
            Drawable background = a.getDrawable(C0695R.styleable.LinePageIndicator_android_background);
            if (background != null) {
                setBackgroundDrawable(background);
            }
            a.recycle();
            this.mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(ViewConfiguration.get(context));
        }
    }

    public void setCentered(boolean centered) {
        this.mCentered = centered;
        invalidate();
    }

    public boolean isCentered() {
        return this.mCentered;
    }

    public void setUnselectedColor(int unselectedColor) {
        this.mPaintUnselected.setColor(unselectedColor);
        invalidate();
    }

    public int getUnselectedColor() {
        return this.mPaintUnselected.getColor();
    }

    public void setSelectedColor(int selectedColor) {
        this.mPaintSelected.setColor(selectedColor);
        invalidate();
    }

    public int getSelectedColor() {
        return this.mPaintSelected.getColor();
    }

    public void setLineWidth(float lineWidth) {
        this.mLineWidth = lineWidth;
        invalidate();
    }

    public float getLineWidth() {
        return this.mLineWidth;
    }

    public void setStrokeWidth(float lineHeight) {
        this.mPaintSelected.setStrokeWidth(lineHeight);
        this.mPaintUnselected.setStrokeWidth(lineHeight);
        invalidate();
    }

    public float getStrokeWidth() {
        return this.mPaintSelected.getStrokeWidth();
    }

    public void setGapWidth(float gapWidth) {
        this.mGapWidth = gapWidth;
        invalidate();
    }

    public float getGapWidth() {
        return this.mGapWidth;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ViewPager viewPager = this.mViewPager;
        if (viewPager != null) {
            int count = viewPager.getAdapter().getCount();
            if (count != 0) {
                if (r0.mCurrentPage >= count) {
                    setCurrentItem(count - 1);
                    return;
                }
                float horizontalOffset;
                float lineWidthAndGap = r0.mLineWidth;
                float f = r0.mGapWidth;
                lineWidthAndGap += f;
                float indicatorWidth = (((float) count) * lineWidthAndGap) - f;
                f = (float) getPaddingTop();
                float paddingLeft = (float) getPaddingLeft();
                float paddingRight = (float) getPaddingRight();
                float verticalOffset = (((((float) getHeight()) - f) - ((float) getPaddingBottom())) / 2.0f) + f;
                float horizontalOffset2 = paddingLeft;
                if (r0.mCentered) {
                    horizontalOffset = horizontalOffset2 + ((((((float) getWidth()) - paddingLeft) - paddingRight) / 2.0f) - (indicatorWidth / 2.0f));
                } else {
                    horizontalOffset = horizontalOffset2;
                }
                int i = 0;
                while (i < count) {
                    float dx1 = horizontalOffset + (((float) i) * lineWidthAndGap);
                    canvas.drawLine(dx1, verticalOffset, dx1 + r0.mLineWidth, verticalOffset, i == r0.mCurrentPage ? r0.mPaintSelected : r0.mPaintUnselected);
                    i++;
                }
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

    public void setViewPager(ViewPager viewPager) {
        ViewPager viewPager2 = this.mViewPager;
        if (viewPager2 != viewPager) {
            if (viewPager2 != null) {
                viewPager2.setOnPageChangeListener(null);
            }
            if (viewPager.getAdapter() != null) {
                this.mViewPager = viewPager;
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
        OnPageChangeListener onPageChangeListener = this.mListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        OnPageChangeListener onPageChangeListener = this.mListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    public void onPageSelected(int position) {
        this.mCurrentPage = position;
        invalidate();
        OnPageChangeListener onPageChangeListener = this.mListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageSelected(position);
        }
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mListener = listener;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        float result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode != 1073741824) {
            ViewPager viewPager = this.mViewPager;
            if (viewPager != null) {
                int count = viewPager.getAdapter().getCount();
                result = (((float) (getPaddingLeft() + getPaddingRight())) + (((float) count) * this.mLineWidth)) + (((float) (count - 1)) * this.mGapWidth);
                if (specMode == Integer.MIN_VALUE) {
                    result = Math.min(result, (float) specSize);
                }
                return (int) Math.ceil((double) result);
            }
        }
        result = (float) specSize;
        return (int) Math.ceil((double) result);
    }

    private int measureHeight(int measureSpec) {
        float result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == 1073741824) {
            result = (float) specSize;
        } else {
            result = (this.mPaintSelected.getStrokeWidth() + ((float) getPaddingTop())) + ((float) getPaddingBottom());
            if (specMode == Integer.MIN_VALUE) {
                result = Math.min(result, (float) specSize);
            }
        }
        return (int) Math.ceil((double) result);
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
