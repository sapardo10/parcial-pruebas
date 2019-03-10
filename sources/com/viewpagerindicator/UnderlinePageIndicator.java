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
import android.view.ViewConfiguration;

public class UnderlinePageIndicator extends View implements PageIndicator {
    private static final int FADE_FRAME_MS = 30;
    private static final int INVALID_POINTER = -1;
    private int mActivePointerId;
    private int mCurrentPage;
    private int mFadeBy;
    private int mFadeDelay;
    private int mFadeLength;
    private final Runnable mFadeRunnable;
    private boolean mFades;
    private boolean mIsDragging;
    private float mLastMotionX;
    private OnPageChangeListener mListener;
    private final Paint mPaint;
    private float mPositionOffset;
    private int mScrollState;
    private int mTouchSlop;
    private ViewPager mViewPager;

    /* renamed from: com.viewpagerindicator.UnderlinePageIndicator$1 */
    class C07031 implements Runnable {
        C07031() {
        }

        public void run() {
            if (UnderlinePageIndicator.this.mFades) {
                int alpha = Math.max(UnderlinePageIndicator.this.mPaint.getAlpha() - UnderlinePageIndicator.this.mFadeBy, 0);
                UnderlinePageIndicator.this.mPaint.setAlpha(alpha);
                UnderlinePageIndicator.this.invalidate();
                if (alpha > 0) {
                    UnderlinePageIndicator.this.postDelayed(this, 30);
                }
            }
        }
    }

    /* renamed from: com.viewpagerindicator.UnderlinePageIndicator$2 */
    class C07042 implements Runnable {
        C07042() {
        }

        public void run() {
            if (UnderlinePageIndicator.this.mFades) {
                UnderlinePageIndicator underlinePageIndicator = UnderlinePageIndicator.this;
                underlinePageIndicator.post(underlinePageIndicator.mFadeRunnable);
            }
        }
    }

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new C07051();
        int currentPage;

        /* renamed from: com.viewpagerindicator.UnderlinePageIndicator$SavedState$1 */
        static class C07051 implements Creator<SavedState> {
            C07051() {
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

    public UnderlinePageIndicator(Context context) {
        this(context, null);
    }

    public UnderlinePageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, C0695R.attr.vpiUnderlinePageIndicatorStyle);
    }

    public UnderlinePageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mPaint = new Paint(1);
        this.mLastMotionX = -1.0f;
        this.mActivePointerId = -1;
        this.mFadeRunnable = new C07031();
        if (!isInEditMode()) {
            Resources res = getResources();
            boolean defaultFades = res.getBoolean(C0695R.bool.default_underline_indicator_fades);
            int defaultFadeDelay = res.getInteger(C0695R.integer.default_underline_indicator_fade_delay);
            int defaultFadeLength = res.getInteger(C0695R.integer.default_underline_indicator_fade_length);
            int defaultSelectedColor = res.getColor(C0695R.color.default_underline_indicator_selected_color);
            TypedArray a = context.obtainStyledAttributes(attrs, C0695R.styleable.UnderlinePageIndicator, defStyle, 0);
            setFades(a.getBoolean(C0695R.styleable.UnderlinePageIndicator_fades, defaultFades));
            setSelectedColor(a.getColor(C0695R.styleable.UnderlinePageIndicator_selectedColor, defaultSelectedColor));
            setFadeDelay(a.getInteger(C0695R.styleable.UnderlinePageIndicator_fadeDelay, defaultFadeDelay));
            setFadeLength(a.getInteger(C0695R.styleable.UnderlinePageIndicator_fadeLength, defaultFadeLength));
            Drawable background = a.getDrawable(C0695R.styleable.UnderlinePageIndicator_android_background);
            if (background != null) {
                setBackgroundDrawable(background);
            }
            a.recycle();
            this.mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(ViewConfiguration.get(context));
        }
    }

    public boolean getFades() {
        return this.mFades;
    }

    public void setFades(boolean fades) {
        if (fades != this.mFades) {
            this.mFades = fades;
            if (fades) {
                post(this.mFadeRunnable);
                return;
            }
            removeCallbacks(this.mFadeRunnable);
            this.mPaint.setAlpha(255);
            invalidate();
        }
    }

    public int getFadeDelay() {
        return this.mFadeDelay;
    }

    public void setFadeDelay(int fadeDelay) {
        this.mFadeDelay = fadeDelay;
    }

    public int getFadeLength() {
        return this.mFadeLength;
    }

    public void setFadeLength(int fadeLength) {
        this.mFadeLength = fadeLength;
        this.mFadeBy = 255 / (this.mFadeLength / 30);
    }

    public int getSelectedColor() {
        return this.mPaint.getColor();
    }

    public void setSelectedColor(int selectedColor) {
        this.mPaint.setColor(selectedColor);
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ViewPager viewPager = this.mViewPager;
        if (viewPager != null) {
            int count = viewPager.getAdapter().getCount();
            if (count != 0) {
                if (this.mCurrentPage >= count) {
                    setCurrentItem(count - 1);
                    return;
                }
                int paddingLeft = getPaddingLeft();
                float pageWidth = ((float) ((getWidth() - paddingLeft) - getPaddingRight())) / (((float) count) * 1.0f);
                float left = ((float) paddingLeft) + ((((float) this.mCurrentPage) + this.mPositionOffset) * pageWidth);
                canvas.drawRect(left, (float) getPaddingTop(), left + pageWidth, (float) (getHeight() - getPaddingBottom()), this.mPaint);
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
                post(new C07042());
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
        this.mPositionOffset = positionOffset;
        if (this.mFades) {
            if (positionOffsetPixels > 0) {
                removeCallbacks(this.mFadeRunnable);
                this.mPaint.setAlpha(255);
            } else if (this.mScrollState != 1) {
                postDelayed(this.mFadeRunnable, (long) this.mFadeDelay);
            }
        }
        invalidate();
        OnPageChangeListener onPageChangeListener = this.mListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    public void onPageSelected(int position) {
        if (this.mScrollState == 0) {
            this.mCurrentPage = position;
            this.mPositionOffset = 0.0f;
            invalidate();
            this.mFadeRunnable.run();
        }
        OnPageChangeListener onPageChangeListener = this.mListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageSelected(position);
        }
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mListener = listener;
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
