package android.support.wearable.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.support.v4.view.ViewCompat;
import android.support.wearable.C0395R;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

@TargetApi(20)
@Deprecated
public class CardFrame extends ViewGroup {
    private static final float BOX_FACTOR = 0.146467f;
    private static final boolean DEBUG = false;
    private static final int DEFAULT_CONTENT_PADDING_DP = 12;
    private static final int DEFAULT_CONTENT_PADDING_TOP_DP = 8;
    private static final int EDGE_FADE_DISTANCE_DP = 40;
    public static final int EXPAND_DOWN = 1;
    public static final int EXPAND_UP = -1;
    public static final float NO_EXPANSION = 1.0f;
    private static final String TAG = "CardFrame";
    private int mBoxInset;
    private boolean mCanExpand;
    private int mCardBaseHeight;
    private final Rect mChildClipBounds;
    private final Rect mContentPadding;
    private final EdgeFade mEdgeFade;
    private final int mEdgeFadeDistance;
    private int mExpansionDirection;
    private boolean mExpansionEnabled;
    private float mExpansionFactor;
    private boolean mHasBottomInset;
    private final Rect mInsetPadding;
    private boolean mRoundDisplay;

    private static class EdgeFade {
        private final Matrix matrix = new Matrix();
        private final Paint paint = new Paint();
        private final Shader shader = new LinearGradient(0.0f, 0.0f, 0.0f, 1.0f, ViewCompat.MEASURED_STATE_MASK, 0, TileMode.CLAMP);

        public EdgeFade() {
            this.paint.setShader(this.shader);
            this.paint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
        }
    }

    public CardFrame(Context context) {
        this(context, null, 0);
    }

    public CardFrame(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardFrame(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mExpansionEnabled = true;
        this.mExpansionFactor = 1.0f;
        this.mExpansionDirection = 1;
        this.mChildClipBounds = new Rect();
        this.mInsetPadding = new Rect();
        this.mContentPadding = new Rect();
        this.mEdgeFade = new EdgeFade();
        float density = context.getResources().getDisplayMetrics().density;
        this.mEdgeFadeDistance = (int) (40.0f * density);
        setBackgroundResource(C0395R.drawable.card_background);
        int defaultContentPadding = (int) (1094713344 * density);
        setContentPadding(defaultContentPadding, (int) (1090519040 * density), defaultContentPadding, defaultContentPadding);
    }

    public void setContentPadding(int left, int top, int right, int bottom) {
        this.mContentPadding.set(left, top, right, bottom);
        requestLayout();
    }

    public int getContentPaddingLeft() {
        return this.mContentPadding.left;
    }

    public int getContentPaddingRight() {
        return this.mContentPadding.right;
    }

    public int getContentPaddingTop() {
        return this.mContentPadding.top;
    }

    public int getContentPaddingBottom() {
        return this.mContentPadding.bottom;
    }

    public void setExpansionEnabled(boolean enabled) {
        this.mExpansionEnabled = enabled;
        requestLayout();
        invalidate();
    }

    public void setExpansionDirection(int direction) {
        this.mExpansionDirection = direction;
        requestLayout();
        invalidate();
    }

    public void setExpansionFactor(float expansionFactor) {
        this.mExpansionFactor = expansionFactor;
        requestLayout();
        invalidate();
    }

    public int getExpansionDirection() {
        return this.mExpansionDirection;
    }

    public boolean isExpansionEnabled() {
        return this.mExpansionEnabled;
    }

    public float getExpansionFactor() {
        return this.mExpansionFactor;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        requestApplyInsets();
    }

    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        boolean round = insets.isRound();
        if (round != this.mRoundDisplay) {
            this.mRoundDisplay = round;
            requestLayout();
        }
        boolean inset = insets.getSystemWindowInsetBottom() > 0;
        if (inset != this.mHasBottomInset) {
            this.mHasBottomInset = inset;
            requestLayout();
        }
        return insets.consumeSystemWindowInsets();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int outsetLeft;
        int outsetRight;
        int logicalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int logicalHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (this.mRoundDisplay) {
            MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
            r0.mInsetPadding.setEmpty();
            outsetLeft = 0;
            int outsetBottom = 0;
            outsetRight = 0;
            if (lp.leftMargin < 0) {
                outsetLeft = -lp.leftMargin;
                logicalWidth -= outsetLeft;
            }
            if (lp.rightMargin < 0) {
                outsetRight = -lp.rightMargin;
                logicalWidth -= outsetRight;
            }
            if (lp.bottomMargin < 0) {
                outsetBottom = -lp.bottomMargin;
                logicalHeight -= outsetBottom;
            }
            r0.mBoxInset = (int) (((float) Math.max(logicalWidth, logicalHeight)) * BOX_FACTOR);
            r0.mInsetPadding.left = r0.mBoxInset - (getPaddingLeft() - outsetLeft);
            r0.mInsetPadding.right = r0.mBoxInset - (getPaddingRight() - outsetRight);
            if (!r0.mHasBottomInset) {
                r0.mInsetPadding.bottom = r0.mBoxInset - (getPaddingBottom() - outsetBottom);
            }
        }
        int cardMeasuredWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec, true);
        outsetRight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec, false);
        if (getChildCount() == 0) {
            setMeasuredDimension(cardMeasuredWidth, outsetRight);
            return;
        }
        int childHeightMeasureSpecSize;
        int paddingHeight;
        LayoutParams lp2;
        View content = getChildAt(0);
        int parentHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        int parentHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int childWidthMeasureSpecSize = cardMeasuredWidth;
        boolean cardHeightMatchContent = false;
        r0.mCanExpand = r0.mExpansionEnabled;
        if (parentHeightMode != 0) {
            if (parentHeightSize != 0) {
                if (parentHeightMode == 1073741824) {
                    Log.w(TAG, "height measure spec passed with mode EXACT");
                    r0.mCanExpand = false;
                    r0.mCardBaseHeight = parentHeightSize;
                    outsetLeft = r0.mCardBaseHeight;
                    outsetRight = 1073741824;
                    childHeightMeasureSpecSize = outsetLeft;
                } else {
                    r0.mCardBaseHeight = parentHeightSize;
                    outsetLeft = r0.mCardBaseHeight;
                    if (r0.mCanExpand) {
                        outsetLeft = (int) (((float) outsetLeft) * r0.mExpansionFactor);
                    }
                    if (r0.mExpansionDirection == -1) {
                        outsetRight = 0;
                        childHeightMeasureSpecSize = 0;
                    } else {
                        outsetRight = Integer.MIN_VALUE;
                        childHeightMeasureSpecSize = outsetLeft + getPaddingBottom();
                    }
                }
                paddingHeight = ((((getPaddingTop() + getPaddingBottom()) + r0.mContentPadding.top) + r0.mContentPadding.bottom) + r0.mInsetPadding.top) + r0.mInsetPadding.bottom;
                logicalWidth = MeasureSpec.makeMeasureSpec(childWidthMeasureSpecSize - (((((getPaddingLeft() + getPaddingRight()) + r0.mContentPadding.left) + r0.mContentPadding.right) + r0.mInsetPadding.left) + r0.mInsetPadding.right), 1073741824);
                logicalHeight = MeasureSpec.makeMeasureSpec(childHeightMeasureSpecSize - paddingHeight, outsetRight);
                lp2 = content.getLayoutParams();
                logicalWidth = getChildMeasureSpec(logicalWidth, null, lp2.width);
                content.measure(logicalWidth, logicalHeight);
                if (cardHeightMatchContent) {
                    outsetLeft = Math.min(outsetLeft, content.getMeasuredHeight() + paddingHeight);
                    r0.mCanExpand &= content.getMeasuredHeight() <= outsetLeft - paddingHeight ? 1 : 0;
                } else {
                    int i = logicalWidth;
                    outsetLeft = content.getMeasuredHeight() + paddingHeight;
                }
                setMeasuredDimension(cardMeasuredWidth, outsetLeft);
            }
        }
        Log.w(TAG, "height measure spec passed with mode UNSPECIFIED, or zero height.");
        r0.mCanExpand = false;
        r0.mCardBaseHeight = 0;
        outsetLeft = 0;
        cardHeightMatchContent = true;
        outsetRight = 0;
        childHeightMeasureSpecSize = 0;
        paddingHeight = ((((getPaddingTop() + getPaddingBottom()) + r0.mContentPadding.top) + r0.mContentPadding.bottom) + r0.mInsetPadding.top) + r0.mInsetPadding.bottom;
        logicalWidth = MeasureSpec.makeMeasureSpec(childWidthMeasureSpecSize - (((((getPaddingLeft() + getPaddingRight()) + r0.mContentPadding.left) + r0.mContentPadding.right) + r0.mInsetPadding.left) + r0.mInsetPadding.right), 1073741824);
        logicalHeight = MeasureSpec.makeMeasureSpec(childHeightMeasureSpecSize - paddingHeight, outsetRight);
        lp2 = content.getLayoutParams();
        logicalWidth = getChildMeasureSpec(logicalWidth, null, lp2.width);
        content.measure(logicalWidth, logicalHeight);
        if (cardHeightMatchContent) {
            outsetLeft = Math.min(outsetLeft, content.getMeasuredHeight() + paddingHeight);
            if (content.getMeasuredHeight() <= outsetLeft - paddingHeight) {
            }
            r0.mCanExpand &= content.getMeasuredHeight() <= outsetLeft - paddingHeight ? 1 : 0;
        } else {
            int i2 = logicalWidth;
            outsetLeft = content.getMeasuredHeight() + paddingHeight;
        }
        setMeasuredDimension(cardMeasuredWidth, outsetLeft);
    }

    public static int getDefaultSize(int size, int measureSpec, boolean greedy) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == Integer.MIN_VALUE) {
            return greedy ? specSize : size;
        } else if (specMode == 0) {
            return size;
        } else {
            if (specMode != 1073741824) {
                return result;
            }
            return specSize;
        }
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (getChildCount() != 0) {
            int b;
            int t;
            View content = getChildAt(null);
            int parentHeight = bottom - top;
            int l = (getPaddingLeft() + this.mInsetPadding.left) + this.mContentPadding.left;
            int r = content.getMeasuredWidth() + l;
            if (this.mExpansionDirection == -1) {
                b = parentHeight;
                t = b - (((content.getMeasuredHeight() + getPaddingBottom()) + this.mInsetPadding.bottom) + this.mContentPadding.bottom);
            } else {
                t = this.mContentPadding.top + (getPaddingTop() + this.mInsetPadding.top);
                b = content.getMeasuredHeight() + t;
            }
            content.layout(l, t, r, b);
        }
    }

    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean bottomFade;
        boolean topFade;
        int saveCount;
        boolean more;
        Canvas canvas2 = canvas;
        int fadeDistance = this.mEdgeFadeDistance;
        this.mChildClipBounds.set(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
        int paddingHeight = getPaddingTop() + getPaddingBottom();
        int contentHeight = child.getHeight();
        if (this.mCanExpand) {
            if (r0.mExpansionDirection == -1 && contentHeight + paddingHeight > getHeight()) {
                r0.mChildClipBounds.top = getPaddingTop();
                bottomFade = false;
                topFade = true;
                saveCount = canvas.getSaveCount();
                canvas2.clipRect(r0.mChildClipBounds);
                if (!topFade) {
                    canvas.saveLayer((float) r0.mChildClipBounds.left, (float) r0.mChildClipBounds.top, (float) r0.mChildClipBounds.right, (float) (r0.mChildClipBounds.top + fadeDistance), null, 4);
                }
                if (!bottomFade) {
                    canvas.saveLayer((float) r0.mChildClipBounds.left, (float) (r0.mChildClipBounds.bottom - fadeDistance), (float) r0.mChildClipBounds.right, (float) r0.mChildClipBounds.bottom, null, 4);
                }
                more = super.drawChild(canvas, child, drawingTime);
                if (!topFade) {
                    r0.mEdgeFade.matrix.reset();
                    r0.mEdgeFade.matrix.setScale(1.0f, (float) fadeDistance);
                    r0.mEdgeFade.matrix.postTranslate((float) r0.mChildClipBounds.left, (float) r0.mChildClipBounds.top);
                    r0.mEdgeFade.shader.setLocalMatrix(r0.mEdgeFade.matrix);
                    r0.mEdgeFade.paint.setShader(r0.mEdgeFade.shader);
                    canvas.drawRect((float) r0.mChildClipBounds.left, (float) r0.mChildClipBounds.top, (float) r0.mChildClipBounds.right, (float) (r0.mChildClipBounds.top + fadeDistance), r0.mEdgeFade.paint);
                }
                if (!bottomFade) {
                    r0.mEdgeFade.matrix.reset();
                    r0.mEdgeFade.matrix.setScale(1.0f, (float) fadeDistance);
                    r0.mEdgeFade.matrix.postRotate(180.0f);
                    r0.mEdgeFade.matrix.postTranslate((float) r0.mChildClipBounds.left, (float) r0.mChildClipBounds.bottom);
                    r0.mEdgeFade.shader.setLocalMatrix(r0.mEdgeFade.matrix);
                    r0.mEdgeFade.paint.setShader(r0.mEdgeFade.shader);
                    canvas.drawRect((float) r0.mChildClipBounds.left, (float) (r0.mChildClipBounds.bottom - fadeDistance), (float) r0.mChildClipBounds.right, (float) r0.mChildClipBounds.bottom, r0.mEdgeFade.paint);
                }
                canvas2.restoreToCount(saveCount);
                return more;
            } else if (r0.mExpansionDirection == 1) {
                if (contentHeight + paddingHeight > getHeight()) {
                    r0.mChildClipBounds.bottom = getHeight() - getPaddingBottom();
                    bottomFade = true;
                    topFade = false;
                    saveCount = canvas.getSaveCount();
                    canvas2.clipRect(r0.mChildClipBounds);
                    if (!topFade) {
                        canvas.saveLayer((float) r0.mChildClipBounds.left, (float) r0.mChildClipBounds.top, (float) r0.mChildClipBounds.right, (float) (r0.mChildClipBounds.top + fadeDistance), null, 4);
                    }
                    if (!bottomFade) {
                        canvas.saveLayer((float) r0.mChildClipBounds.left, (float) (r0.mChildClipBounds.bottom - fadeDistance), (float) r0.mChildClipBounds.right, (float) r0.mChildClipBounds.bottom, null, 4);
                    }
                    more = super.drawChild(canvas, child, drawingTime);
                    if (!topFade) {
                        r0.mEdgeFade.matrix.reset();
                        r0.mEdgeFade.matrix.setScale(1.0f, (float) fadeDistance);
                        r0.mEdgeFade.matrix.postTranslate((float) r0.mChildClipBounds.left, (float) r0.mChildClipBounds.top);
                        r0.mEdgeFade.shader.setLocalMatrix(r0.mEdgeFade.matrix);
                        r0.mEdgeFade.paint.setShader(r0.mEdgeFade.shader);
                        canvas.drawRect((float) r0.mChildClipBounds.left, (float) r0.mChildClipBounds.top, (float) r0.mChildClipBounds.right, (float) (r0.mChildClipBounds.top + fadeDistance), r0.mEdgeFade.paint);
                    }
                    if (!bottomFade) {
                        r0.mEdgeFade.matrix.reset();
                        r0.mEdgeFade.matrix.setScale(1.0f, (float) fadeDistance);
                        r0.mEdgeFade.matrix.postRotate(180.0f);
                        r0.mEdgeFade.matrix.postTranslate((float) r0.mChildClipBounds.left, (float) r0.mChildClipBounds.bottom);
                        r0.mEdgeFade.shader.setLocalMatrix(r0.mEdgeFade.matrix);
                        r0.mEdgeFade.paint.setShader(r0.mEdgeFade.shader);
                        canvas.drawRect((float) r0.mChildClipBounds.left, (float) (r0.mChildClipBounds.bottom - fadeDistance), (float) r0.mChildClipBounds.right, (float) r0.mChildClipBounds.bottom, r0.mEdgeFade.paint);
                    }
                    canvas2.restoreToCount(saveCount);
                    return more;
                }
            }
        }
        bottomFade = false;
        topFade = false;
        saveCount = canvas.getSaveCount();
        canvas2.clipRect(r0.mChildClipBounds);
        if (!topFade) {
            canvas.saveLayer((float) r0.mChildClipBounds.left, (float) r0.mChildClipBounds.top, (float) r0.mChildClipBounds.right, (float) (r0.mChildClipBounds.top + fadeDistance), null, 4);
        }
        if (!bottomFade) {
            canvas.saveLayer((float) r0.mChildClipBounds.left, (float) (r0.mChildClipBounds.bottom - fadeDistance), (float) r0.mChildClipBounds.right, (float) r0.mChildClipBounds.bottom, null, 4);
        }
        more = super.drawChild(canvas, child, drawingTime);
        if (!topFade) {
            r0.mEdgeFade.matrix.reset();
            r0.mEdgeFade.matrix.setScale(1.0f, (float) fadeDistance);
            r0.mEdgeFade.matrix.postTranslate((float) r0.mChildClipBounds.left, (float) r0.mChildClipBounds.top);
            r0.mEdgeFade.shader.setLocalMatrix(r0.mEdgeFade.matrix);
            r0.mEdgeFade.paint.setShader(r0.mEdgeFade.shader);
            canvas.drawRect((float) r0.mChildClipBounds.left, (float) r0.mChildClipBounds.top, (float) r0.mChildClipBounds.right, (float) (r0.mChildClipBounds.top + fadeDistance), r0.mEdgeFade.paint);
        }
        if (!bottomFade) {
            r0.mEdgeFade.matrix.reset();
            r0.mEdgeFade.matrix.setScale(1.0f, (float) fadeDistance);
            r0.mEdgeFade.matrix.postRotate(180.0f);
            r0.mEdgeFade.matrix.postTranslate((float) r0.mChildClipBounds.left, (float) r0.mChildClipBounds.bottom);
            r0.mEdgeFade.shader.setLocalMatrix(r0.mEdgeFade.matrix);
            r0.mEdgeFade.paint.setShader(r0.mEdgeFade.shader);
            canvas.drawRect((float) r0.mChildClipBounds.left, (float) (r0.mChildClipBounds.bottom - fadeDistance), (float) r0.mChildClipBounds.right, (float) r0.mChildClipBounds.bottom, r0.mEdgeFade.paint);
        }
        canvas2.restoreToCount(saveCount);
        return more;
    }

    public void addView(View child) {
        if (getChildCount() <= 0) {
            super.addView(child);
            return;
        }
        throw new IllegalStateException("CardFrame can host only one direct child");
    }

    public void addView(View child, int index) {
        if (getChildCount() <= 0) {
            super.addView(child, index);
            return;
        }
        throw new IllegalStateException("CardFrame can host only one direct child");
    }

    public void addView(View child, LayoutParams params) {
        if (getChildCount() <= 0) {
            super.addView(child, params);
            return;
        }
        throw new IllegalStateException("CardFrame can host only one direct child");
    }

    public void addView(View child, int index, LayoutParams params) {
        if (getChildCount() <= 0) {
            super.addView(child, index, params);
            return;
        }
        throw new IllegalStateException("CardFrame can host only one direct child");
    }

    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(CardFrame.class.getName());
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(CardFrame.class.getName());
    }
}
