package android.support.wearable.view.drawer;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RadialGradient;
import android.graphics.Shader.TileMode;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.wearable.C0395R;
import android.support.wearable.view.SimpleAnimatorListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import java.util.concurrent.TimeUnit;

@TargetApi(23)
@Deprecated
public class PageIndicatorView extends View implements OnPageChangeListener {
    private static final String TAG = "Dots";
    private PagerAdapter mAdapter;
    private int mCurrentViewPagerState;
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
    private int mNumberOfPositions;
    private int mSelectedPosition;
    private boolean mVisible;

    /* renamed from: android.support.wearable.view.drawer.PageIndicatorView$1 */
    class C09201 extends SimpleAnimatorListener {
        C09201() {
        }

        public void onAnimationComplete(Animator animator) {
            PageIndicatorView.this.mVisible = false;
            PageIndicatorView.this.animate().alpha(0.0f).setListener(null).setStartDelay((long) PageIndicatorView.this.mDotFadeOutDelay).setDuration((long) PageIndicatorView.this.mDotFadeOutDuration).start();
        }
    }

    public PageIndicatorView(Context context) {
        this(context, null);
    }

    public PageIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = getContext().obtainStyledAttributes(attrs, C0395R.styleable.PageIndicatorView, defStyleAttr, C0395R.style.PageIndicatorViewStyle);
        this.mDotSpacing = a.getDimensionPixelOffset(C0395R.styleable.PageIndicatorView_pageIndicatorDotSpacing, 0);
        this.mDotRadius = a.getDimension(C0395R.styleable.PageIndicatorView_pageIndicatorDotRadius, 0.0f);
        this.mDotRadiusSelected = a.getDimension(C0395R.styleable.PageIndicatorView_pageIndicatorDotRadiusSelected, 0.0f);
        this.mDotColor = a.getColor(C0395R.styleable.PageIndicatorView_pageIndicatorDotColor, 0);
        this.mDotColorSelected = a.getColor(C0395R.styleable.PageIndicatorView_pageIndicatorDotColorSelected, 0);
        this.mDotFadeOutDelay = a.getInt(C0395R.styleable.PageIndicatorView_pageIndicatorDotFadeOutDelay, 0);
        this.mDotFadeOutDuration = a.getInt(C0395R.styleable.PageIndicatorView_pageIndicatorDotFadeOutDuration, 0);
        this.mDotFadeInDuration = a.getInt(C0395R.styleable.PageIndicatorView_pageIndicatorDotFadeInDuration, 0);
        this.mDotFadeWhenIdle = a.getBoolean(C0395R.styleable.PageIndicatorView_pageIndicatorDotFadeWhenIdle, false);
        this.mDotShadowDx = a.getDimension(C0395R.styleable.PageIndicatorView_pageIndicatorDotShadowDx, 0.0f);
        this.mDotShadowDy = a.getDimension(C0395R.styleable.PageIndicatorView_pageIndicatorDotShadowDy, 0.0f);
        this.mDotShadowRadius = a.getDimension(C0395R.styleable.PageIndicatorView_pageIndicatorDotShadowRadius, 0.0f);
        this.mDotShadowColor = a.getColor(C0395R.styleable.PageIndicatorView_pageIndicatorDotShadowColor, 0);
        a.recycle();
        this.mDotPaint = new Paint(1);
        this.mDotPaint.setColor(this.mDotColor);
        this.mDotPaint.setStyle(Style.FILL);
        this.mDotPaintSelected = new Paint(1);
        this.mDotPaintSelected.setColor(this.mDotColorSelected);
        this.mDotPaintSelected.setStyle(Style.FILL);
        this.mDotPaintShadow = new Paint(1);
        this.mDotPaintShadowSelected = new Paint(1);
        this.mCurrentViewPagerState = 0;
        if (isInEditMode()) {
            this.mNumberOfPositions = 5;
            this.mSelectedPosition = 2;
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

    public void setPager(ViewPager pager) {
        pager.addOnPageChangeListener(this);
        setPagerAdapter(pager.getAdapter());
        this.mAdapter = pager.getAdapter();
        PagerAdapter pagerAdapter = this.mAdapter;
        if (pagerAdapter != null && pagerAdapter.getCount() > 0) {
            positionChanged(0);
        }
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

    private void positionChanged(int position) {
        this.mSelectedPosition = position;
        invalidate();
    }

    private void updateNumberOfPositions() {
        int count = this.mAdapter.getCount();
        if (count != this.mNumberOfPositions) {
            this.mNumberOfPositions = count;
            requestLayout();
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
        animate().alpha(1.0f).setStartDelay(0).setDuration((long) this.mDotFadeInDuration).setListener(new C09201()).start();
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (!this.mDotFadeWhenIdle) {
            return;
        }
        if (this.mCurrentViewPagerState != 1) {
            return;
        }
        if (positionOffset != 0.0f) {
            if (!this.mVisible) {
                fadeIn();
            }
        } else if (this.mVisible) {
            fadeOut(0);
        }
    }

    public void onPageSelected(int position) {
        if (position != this.mSelectedPosition) {
            positionChanged(position);
        }
    }

    public void onPageScrollStateChanged(int state) {
        if (this.mCurrentViewPagerState != state) {
            this.mCurrentViewPagerState = state;
            if (!this.mDotFadeWhenIdle) {
                return;
            }
            if (state != 0) {
                return;
            }
            if (this.mVisible) {
                fadeOut((long) this.mDotFadeOutDelay);
            } else {
                fadeInOut();
            }
        }
    }

    public void setPagerAdapter(PagerAdapter adapter) {
        this.mAdapter = adapter;
        if (this.mAdapter != null) {
            updateNumberOfPositions();
            if (this.mDotFadeWhenIdle) {
                fadeInOut();
            }
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int totalWidth;
        int totalHeight;
        if (MeasureSpec.getMode(widthMeasureSpec) == 1073741824) {
            totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        } else {
            totalWidth = (getPaddingLeft() + (this.mNumberOfPositions * this.mDotSpacing)) + getPaddingRight();
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
        if (this.mNumberOfPositions > 1) {
            float dotCenterLeft = ((float) getPaddingLeft()) + (((float) this.mDotSpacing) / 2.0f);
            float dotCenterTop = ((float) getHeight()) / 2.0f;
            canvas.save();
            canvas.translate(dotCenterLeft, dotCenterTop);
            for (int i = 0; i < this.mNumberOfPositions; i++) {
                if (i == this.mSelectedPosition) {
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

    public void notifyDataSetChanged() {
        PagerAdapter pagerAdapter = this.mAdapter;
        if (pagerAdapter != null && pagerAdapter.getCount() > 0) {
            updateNumberOfPositions();
        }
    }
}
