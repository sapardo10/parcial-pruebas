package android.support.wearable.view;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RadialGradient;
import android.graphics.Shader.TileMode;
import android.support.wearable.C0395R;
import android.support.wearable.view.GridViewPager.OnAdapterChangeListener;
import android.support.wearable.view.GridViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import java.util.concurrent.TimeUnit;

@TargetApi(20)
@Deprecated
public class DotsPageIndicator extends View implements OnPageChangeListener, OnAdapterChangeListener {
    private static final String TAG = "Dots";
    private GridPagerAdapter mAdapter;
    private OnAdapterChangeListener mAdapterChangeListener;
    private int mColumnCount;
    private int mCurrentState;
    private int mDotColor;
    private int mDotColorSelected;
    private int mDotFadeInDuration;
    private int mDotFadeOutDelay;
    private int mDotFadeOutDuration;
    private boolean mDotFadeWhenIdle;
    private final Paint mDotPaint;
    private final Paint mDotPaintSelected;
    private final Paint mDotPaintShadow;
    private final Paint mDotPaintShadowSelected;
    private float mDotRadius;
    private float mDotRadiusSelected;
    private int mDotShadowColor;
    private float mDotShadowDx;
    private float mDotShadowDy;
    private float mDotShadowRadius;
    private int mDotSpacing;
    private OnPageChangeListener mPageChangeListener;
    private GridViewPager mPager;
    private int mSelectedColumn;
    private int mSelectedRow;
    private boolean mVisible;

    /* renamed from: android.support.wearable.view.DotsPageIndicator$1 */
    class C09151 extends SimpleAnimatorListener {
        C09151() {
        }

        public void onAnimationComplete(Animator animator) {
            DotsPageIndicator.this.mVisible = false;
            DotsPageIndicator.this.animate().alpha(0.0f).setListener(null).setStartDelay((long) DotsPageIndicator.this.mDotFadeOutDelay).setDuration((long) DotsPageIndicator.this.mDotFadeOutDuration).start();
        }
    }

    public DotsPageIndicator(Context context) {
        this(context, null);
    }

    public DotsPageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DotsPageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = getContext().obtainStyledAttributes(attrs, C0395R.styleable.DotsPageIndicator, 0, C0395R.style.DotsPageIndicatorStyle);
        this.mDotSpacing = a.getDimensionPixelOffset(C0395R.styleable.DotsPageIndicator_dotSpacing, 0);
        this.mDotRadius = a.getDimension(C0395R.styleable.DotsPageIndicator_dotRadius, 0.0f);
        this.mDotRadiusSelected = a.getDimension(C0395R.styleable.DotsPageIndicator_dotRadiusSelected, 0.0f);
        this.mDotColor = a.getColor(C0395R.styleable.DotsPageIndicator_dotColor, 0);
        this.mDotColorSelected = a.getColor(C0395R.styleable.DotsPageIndicator_dotColorSelected, 0);
        this.mDotFadeOutDelay = a.getInt(C0395R.styleable.DotsPageIndicator_dotFadeOutDelay, 0);
        this.mDotFadeOutDuration = a.getInt(C0395R.styleable.DotsPageIndicator_dotFadeOutDuration, 0);
        this.mDotFadeInDuration = a.getInt(C0395R.styleable.DotsPageIndicator_dotFadeInDuration, 0);
        this.mDotFadeWhenIdle = a.getBoolean(C0395R.styleable.DotsPageIndicator_dotFadeWhenIdle, false);
        this.mDotShadowDx = a.getDimension(C0395R.styleable.DotsPageIndicator_dotShadowDx, 0.0f);
        this.mDotShadowDy = a.getDimension(C0395R.styleable.DotsPageIndicator_dotShadowDy, 0.0f);
        this.mDotShadowRadius = a.getDimension(C0395R.styleable.DotsPageIndicator_dotShadowRadius, 0.0f);
        this.mDotShadowColor = a.getColor(C0395R.styleable.DotsPageIndicator_dotShadowColor, 0);
        a.recycle();
        this.mDotPaint = new Paint(1);
        this.mDotPaint.setColor(this.mDotColor);
        this.mDotPaint.setStyle(Style.FILL);
        this.mDotPaintSelected = new Paint(1);
        this.mDotPaintSelected.setColor(this.mDotColorSelected);
        this.mDotPaintSelected.setStyle(Style.FILL);
        this.mDotPaintShadow = new Paint(1);
        this.mDotPaintShadowSelected = new Paint(1);
        this.mCurrentState = 0;
        if (isInEditMode()) {
            this.mColumnCount = 5;
            this.mSelectedColumn = 2;
            this.mDotFadeWhenIdle = false;
        }
        if (this.mDotFadeWhenIdle) {
            this.mVisible = false;
            animate().alpha(0.0f).setStartDelay(AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS).setDuration((long) this.mDotFadeOutDuration).start();
        } else {
            animate().cancel();
            setAlpha(1.0f);
        }
        updateShadows();
    }

    private void updateShadows() {
        updateDotPaint(this.mDotPaint, this.mDotPaintShadow, this.mDotRadius, this.mDotShadowRadius, this.mDotColor, this.mDotShadowColor);
        updateDotPaint(this.mDotPaintSelected, this.mDotPaintShadowSelected, this.mDotRadiusSelected, this.mDotShadowRadius, this.mDotColorSelected, this.mDotShadowColor);
    }

    private void updateDotPaint(Paint dotPaint, Paint shadowPaint, float baseRadius, float shadowRadius, int color, int shadowColor) {
        Paint paint = dotPaint;
        float radius = baseRadius + shadowRadius;
        float shadowStart = baseRadius / radius;
        float f = radius;
        Paint paint2 = shadowPaint;
        shadowPaint.setShader(new RadialGradient(0.0f, 0.0f, f, new int[]{shadowColor, shadowColor, 0}, new float[]{0.0f, shadowStart, 1.0f}, TileMode.CLAMP));
        dotPaint.setColor(color);
        dotPaint.setStyle(Style.FILL);
    }

    public void setPager(GridViewPager pager) {
        GridViewPager gridViewPager = this.mPager;
        if (gridViewPager != pager) {
            if (gridViewPager != null) {
                gridViewPager.setOnPageChangeListener(null);
                this.mPager.setOnAdapterChangeListener(null);
                this.mPager = null;
            }
            this.mPager = pager;
            gridViewPager = this.mPager;
            if (gridViewPager != null) {
                gridViewPager.setOnPageChangeListener(this);
                this.mPager.setOnAdapterChangeListener(this);
                this.mAdapter = this.mPager.getAdapter();
            }
        }
        GridPagerAdapter gridPagerAdapter = this.mAdapter;
        if (gridPagerAdapter != null && gridPagerAdapter.getRowCount() > 0) {
            rowChanged(0, 0);
        }
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mPageChangeListener = listener;
    }

    public void setOnAdapterChangeListener(OnAdapterChangeListener listener) {
        this.mAdapterChangeListener = listener;
    }

    public float getDotSpacing() {
        return (float) this.mDotSpacing;
    }

    public void setDotSpacing(int spacing) {
        if (this.mDotSpacing != spacing) {
            this.mDotSpacing = spacing;
            requestLayout();
        }
    }

    public float getDotRadius() {
        return this.mDotRadius;
    }

    public void setDotRadius(int radius) {
        if (this.mDotRadius != ((float) radius)) {
            this.mDotRadius = (float) radius;
            updateShadows();
            invalidate();
        }
    }

    public float getDotRadiusSelected() {
        return this.mDotRadiusSelected;
    }

    public void setDotRadiusSelected(int radius) {
        if (this.mDotRadiusSelected != ((float) radius)) {
            this.mDotRadiusSelected = (float) radius;
            updateShadows();
            invalidate();
        }
    }

    public int getDotColor() {
        return this.mDotColor;
    }

    public void setDotColor(int color) {
        if (this.mDotColor != color) {
            this.mDotColor = color;
            invalidate();
        }
    }

    public int getDotColorSelected() {
        return this.mDotColorSelected;
    }

    public void setDotColorSelected(int color) {
        if (this.mDotColorSelected != color) {
            this.mDotColorSelected = color;
            invalidate();
        }
    }

    public boolean getDotFadeWhenIdle() {
        return this.mDotFadeWhenIdle;
    }

    public void setDotFadeWhenIdle(boolean fade) {
        this.mDotFadeWhenIdle = fade;
        if (!fade) {
            fadeIn();
        }
    }

    public int getDotFadeOutDuration() {
        return this.mDotFadeOutDuration;
    }

    public void setDotFadeOutDuration(int duration, TimeUnit unit) {
        this.mDotFadeOutDuration = (int) TimeUnit.MILLISECONDS.convert((long) duration, unit);
    }

    public int getDotFadeInDuration() {
        return this.mDotFadeInDuration;
    }

    public void setDotFadeInDuration(int duration, TimeUnit unit) {
        this.mDotFadeInDuration = (int) TimeUnit.MILLISECONDS.convert((long) duration, unit);
    }

    public int getDotFadeOutDelay() {
        return this.mDotFadeOutDelay;
    }

    public void setDotFadeOutDelay(int delay) {
        this.mDotFadeOutDelay = delay;
    }

    public float getDotShadowRadius() {
        return this.mDotShadowRadius;
    }

    public void setDotShadowRadius(float radius) {
        if (this.mDotShadowRadius != radius) {
            this.mDotShadowRadius = radius;
            updateShadows();
            invalidate();
        }
    }

    public float getDotShadowDx() {
        return this.mDotShadowDx;
    }

    public void setDotShadowDx(float dx) {
        this.mDotShadowDx = dx;
        invalidate();
    }

    public float getDotShadowDy() {
        return this.mDotShadowDy;
    }

    public void setDotShadowDy(float dy) {
        this.mDotShadowDy = dy;
        invalidate();
    }

    public int getDotShadowColor() {
        return this.mDotShadowColor;
    }

    public void setDotShadowColor(int color) {
        this.mDotShadowColor = color;
        updateShadows();
        invalidate();
    }

    private void columnChanged(int column) {
        this.mSelectedColumn = column;
        invalidate();
    }

    private void rowChanged(int row, int column) {
        this.mSelectedRow = row;
        int count = this.mAdapter.getColumnCount(row);
        if (count != this.mColumnCount) {
            this.mColumnCount = count;
            this.mSelectedColumn = column;
            requestLayout();
        } else if (column != this.mSelectedColumn) {
            this.mSelectedColumn = column;
            invalidate();
        }
    }

    private void fadeIn() {
        this.mVisible = true;
        animate().cancel();
        animate().alpha(1.0f).setStartDelay(0).setDuration((long) this.mDotFadeInDuration).start();
    }

    private void fadeOut(long delayMillis) {
        this.mVisible = false;
        animate().cancel();
        animate().alpha(0.0f).setStartDelay(delayMillis).setDuration((long) this.mDotFadeOutDuration).start();
    }

    private void fadeInOut() {
        this.mVisible = true;
        animate().cancel();
        animate().alpha(1.0f).setStartDelay(0).setDuration((long) this.mDotFadeInDuration).setListener(new C09151()).start();
    }

    public void onPageScrollStateChanged(int state) {
        if (this.mCurrentState != state) {
            this.mCurrentState = state;
            if (this.mDotFadeWhenIdle) {
                if (state == 0) {
                    if (this.mVisible) {
                        fadeOut((long) this.mDotFadeOutDelay);
                    } else {
                        fadeInOut();
                    }
                }
            }
        }
        OnPageChangeListener onPageChangeListener = this.mPageChangeListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    public void onPageScrolled(int row, int column, float rowOffset, float columnOffset, int rowOffsetPixels, int columnOffsetPixels) {
        if (this.mDotFadeWhenIdle) {
            if (this.mCurrentState == 1) {
                if (columnOffset != 0.0f) {
                    if (!this.mVisible) {
                        fadeIn();
                    }
                } else if (this.mVisible) {
                    fadeOut(0);
                }
            }
        }
        OnPageChangeListener onPageChangeListener = this.mPageChangeListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrolled(row, column, rowOffset, columnOffset, rowOffsetPixels, columnOffsetPixels);
        }
    }

    public void onPageSelected(int row, int column) {
        if (row != this.mSelectedRow) {
            rowChanged(row, column);
        } else if (column != this.mSelectedColumn) {
            columnChanged(column);
        }
        OnPageChangeListener onPageChangeListener = this.mPageChangeListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageSelected(row, column);
        }
    }

    public void onAdapterChanged(GridPagerAdapter oldAdapter, GridPagerAdapter newAdapter) {
        this.mAdapter = newAdapter;
        if (this.mAdapter != null) {
            rowChanged(0, 0);
            if (this.mDotFadeWhenIdle) {
                fadeInOut();
            }
        }
        OnAdapterChangeListener onAdapterChangeListener = this.mAdapterChangeListener;
        if (onAdapterChangeListener != null) {
            onAdapterChangeListener.onAdapterChanged(oldAdapter, newAdapter);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int totalWidth;
        int totalHeight;
        if (MeasureSpec.getMode(widthMeasureSpec) == 1073741824) {
            totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        } else {
            totalWidth = (getPaddingLeft() + (this.mColumnCount * this.mDotSpacing)) + getPaddingRight();
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == 1073741824) {
            totalHeight = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            float f = this.mDotRadius;
            float f2 = this.mDotShadowRadius;
            totalHeight = (getPaddingTop() + ((int) (((float) ((int) Math.ceil((double) (2.0f * Math.max(f + f2, this.mDotRadiusSelected + f2))))) + this.mDotShadowDy))) + getPaddingBottom();
        }
        setMeasuredDimension(resolveSizeAndState(totalWidth, widthMeasureSpec, 0), resolveSizeAndState(totalHeight, heightMeasureSpec, 0));
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mColumnCount > 1) {
            float dotCenterLeft = ((float) getPaddingLeft()) + (((float) this.mDotSpacing) / 2.0f);
            float dotCenterTop = ((float) getHeight()) / 2.0f;
            canvas.save();
            canvas.translate(dotCenterLeft, dotCenterTop);
            for (int i = 0; i < this.mColumnCount; i++) {
                if (i == this.mSelectedColumn) {
                    canvas.drawCircle(this.mDotShadowDx, this.mDotShadowDy, this.mDotRadiusSelected + this.mDotShadowRadius, this.mDotPaintShadowSelected);
                    canvas.drawCircle(0.0f, 0.0f, this.mDotRadiusSelected, this.mDotPaintSelected);
                } else {
                    canvas.drawCircle(this.mDotShadowDx, this.mDotShadowDy, this.mDotRadius + this.mDotShadowRadius, this.mDotPaintShadow);
                    canvas.drawCircle(0.0f, 0.0f, this.mDotRadius, this.mDotPaint);
                }
                canvas.translate((float) this.mDotSpacing, 0.0f);
            }
            canvas.restore();
        }
    }

    public void onDataSetChanged() {
        GridPagerAdapter gridPagerAdapter = this.mAdapter;
        if (gridPagerAdapter != null && gridPagerAdapter.getRowCount() > 0) {
            rowChanged(0, 0);
        }
        OnAdapterChangeListener onAdapterChangeListener = this.mAdapterChangeListener;
        if (onAdapterChangeListener != null) {
            onAdapterChangeListener.onDataSetChanged();
        }
    }
}
