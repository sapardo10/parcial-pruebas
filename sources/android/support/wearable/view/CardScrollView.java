package android.support.wearable.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

@TargetApi(20)
@Deprecated
public class CardScrollView extends FrameLayout {
    private static final int CARD_SHADOW_WIDTH_DP = 8;
    private static final boolean DEBUG = false;
    private static final String TAG = "CardScrollView";
    private CardFrame mCardFrame;
    private final int mCardShadowWidth;
    private boolean mRoundDisplay;

    public CardScrollView(Context context) {
        this(context, null);
    }

    public CardScrollView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.mCardShadowWidth = (int) (getResources().getDisplayMetrics().density * 8.0f);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        requestApplyInsets();
    }

    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        boolean round = insets.isRound();
        if (this.mRoundDisplay != round) {
            this.mRoundDisplay = round;
            LayoutParams lp = (LayoutParams) this.mCardFrame.getLayoutParams();
            int i = this.mCardShadowWidth;
            lp.leftMargin = -i;
            lp.rightMargin = -i;
            lp.bottomMargin = -i;
            this.mCardFrame.setLayoutParams(lp);
        }
        if (insets.getSystemWindowInsetBottom() > 0) {
            int bottomInset = insets.getSystemWindowInsetBottom();
            ViewGroup.LayoutParams lp2 = getLayoutParams();
            if (lp2 instanceof MarginLayoutParams) {
                ((MarginLayoutParams) lp2).bottomMargin = bottomInset;
            }
        }
        if (this.mRoundDisplay) {
            CardFrame cardFrame = this.mCardFrame;
            if (cardFrame != null) {
                cardFrame.onApplyWindowInsets(insets);
                requestLayout();
                return insets;
            }
        }
        requestLayout();
        return insets;
    }

    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0 || !(child instanceof CardFrame)) {
            throw new IllegalStateException("CardScrollView may contain only a single CardFrame.");
        }
        super.addView(child, index, params);
        this.mCardFrame = (CardFrame) child;
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 0) {
            if (getChildAt(0) instanceof CardFrame) {
                return;
            }
        }
        Log.w(TAG, "No CardFrame has been added!");
    }

    private boolean hasCardFrame() {
        if (this.mCardFrame != null) {
            return true;
        }
        Log.w(TAG, "No CardFrame has been added.");
        return false;
    }

    public void setExpansionEnabled(boolean enableExpansion) {
        if (Log.isLoggable(TAG, 3)) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder(26);
            stringBuilder.append("setExpansionEnabled: ");
            stringBuilder.append(enableExpansion);
            Log.d(str, stringBuilder.toString());
        }
        if (!hasCardFrame()) {
            return;
        }
        if (enableExpansion != this.mCardFrame.isExpansionEnabled()) {
            this.mCardFrame.setExpansionEnabled(enableExpansion);
            if (!enableExpansion) {
                scrollTo(0, 0);
            }
        }
    }

    public boolean isExpansionEnabled() {
        if (hasCardFrame()) {
            return this.mCardFrame.isExpansionEnabled();
        }
        return false;
    }

    public void setExpansionDirection(int direction) {
        if (Log.isLoggable(TAG, 3)) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder(34);
            stringBuilder.append("setExpansionDirection: ");
            stringBuilder.append(direction);
            Log.d(str, stringBuilder.toString());
        }
        if (!hasCardFrame()) {
            return;
        }
        if (direction != this.mCardFrame.getExpansionDirection()) {
            this.mCardFrame.setExpansionDirection(direction);
            if (direction == 1 && getScrollY() < 0) {
                scrollTo(0, 0);
            } else if (direction == -1 && getScrollY() > 0) {
                scrollTo(0, 0);
            }
            requestLayout();
        }
    }

    public float getExpansionFactor() {
        if (hasCardFrame()) {
            return this.mCardFrame.getExpansionFactor();
        }
        return 0.0f;
    }

    public void setExpansionFactor(float expansionFactor) {
        if (Log.isLoggable(TAG, 3)) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder(35);
            stringBuilder.append("setExpansionFactor: ");
            stringBuilder.append(expansionFactor);
            Log.d(str, stringBuilder.toString());
        }
        if (hasCardFrame()) {
            this.mCardFrame.setExpansionFactor(expansionFactor);
        }
    }

    public int getExpansionDirection() {
        if (hasCardFrame()) {
            return this.mCardFrame.getExpansionDirection();
        }
        return 0;
    }

    public void setCardGravity(int gravity) {
        if (Log.isLoggable(TAG, 3)) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder(27);
            stringBuilder.append("setCardGravity: ");
            stringBuilder.append(gravity);
            Log.d(str, stringBuilder.toString());
        }
        if (hasCardFrame()) {
            gravity &= 112;
            if (((LayoutParams) this.mCardFrame.getLayoutParams()).gravity != gravity) {
                this.mCardFrame.setLayoutParams(new LayoutParams(-1, -2, gravity));
                requestLayout();
            }
        }
    }

    public int getCardGravity() {
        if (hasCardFrame()) {
            return ((LayoutParams) this.mCardFrame.getLayoutParams()).gravity;
        }
        return 0;
    }

    public boolean canScrollHorizontally(int direction) {
        return false;
    }

    public int getAvailableScrollDelta(int direction) {
        if (!hasCardFrame()) {
            return 0;
        }
        LayoutParams lp = (LayoutParams) this.mCardFrame.getLayoutParams();
        int marginHeight = lp.topMargin + lp.bottomMargin;
        int cardVerticalSpan = (this.mCardFrame.getMeasuredHeight() + (getPaddingTop() + getPaddingBottom())) + marginHeight;
        if (cardVerticalSpan <= getMeasuredHeight()) {
            return 0;
        }
        int extra = cardVerticalSpan - getMeasuredHeight();
        int avail = 0;
        int sy = getScrollY();
        if (this.mCardFrame.getExpansionDirection() == 1) {
            if (sy >= 0) {
                if (direction < 0) {
                    avail = -sy;
                } else if (direction > 0) {
                    avail = Math.max(0, extra - sy);
                }
            }
        } else if (this.mCardFrame.getExpansionDirection() == -1) {
            if (sy <= 0) {
                if (direction > 0) {
                    avail = -sy;
                } else if (direction < 0) {
                    avail = -(extra + sy);
                }
            }
        }
        if (Log.isLoggable(TAG, 3)) {
            String str = TAG;
            int max = Math.max(0, avail);
            StringBuilder stringBuilder = new StringBuilder(42);
            stringBuilder.append("getVerticalScrollableDistance: ");
            stringBuilder.append(max);
            Log.d(str, stringBuilder.toString());
        }
        return avail;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        CardFrame cardFrame = this.mCardFrame;
        if (cardFrame != null) {
            MarginLayoutParams lp = (MarginLayoutParams) cardFrame.getLayoutParams();
            int availableHeight = (MeasureSpec.getSize(heightMeasureSpec) - (getPaddingTop() + getPaddingBottom())) - (lp.topMargin + lp.bottomMargin);
            this.mCardFrame.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(widthMeasureSpec) - (getPaddingLeft() + getPaddingRight())) - (lp.leftMargin + lp.rightMargin), 1073741824), MeasureSpec.makeMeasureSpec(availableHeight, Integer.MIN_VALUE));
        }
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), getDefaultSize(getSuggestedMinimumWidth(), heightMeasureSpec));
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        CardFrame cardFrame = this.mCardFrame;
        if (cardFrame != null) {
            boolean alignBottom;
            LayoutParams lp = (LayoutParams) cardFrame.getLayoutParams();
            int cardHeight = r0.mCardFrame.getMeasuredHeight();
            int cardWidth = r0.mCardFrame.getMeasuredWidth();
            int parentHeight = bottom - top;
            boolean z = true;
            if ((getPaddingTop() + cardHeight) + lp.topMargin <= parentHeight) {
                if ((lp.gravity & 112) != 80) {
                    z = false;
                }
                alignBottom = z;
            } else {
                if (r0.mCardFrame.getExpansionDirection() != -1) {
                    z = false;
                }
                alignBottom = z;
            }
            int l = getPaddingLeft() + lp.leftMargin;
            int t = getPaddingTop() + lp.topMargin;
            int r = l + cardWidth;
            int b = t + cardHeight;
            if (alignBottom) {
                b = parentHeight - (getPaddingBottom() + lp.bottomMargin);
                t = b - cardHeight;
            }
            r0.mCardFrame.layout(l, t, r, b);
        }
    }

    int roundAwayFromZero(float v) {
        return (int) (v < 0.0f ? Math.floor((double) v) : Math.ceil((double) v));
    }

    int roundTowardZero(float v) {
        return (int) (v > 0.0f ? Math.floor((double) v) : Math.ceil((double) v));
    }
}
