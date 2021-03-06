package android.support.v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.cardview.C0287R;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;

public class CardView extends FrameLayout {
    private static final int[] COLOR_BACKGROUND_ATTR = new int[]{16842801};
    private static final CardViewImpl IMPL;
    private final CardViewDelegate mCardViewDelegate = new C08821();
    private boolean mCompatPadding;
    final Rect mContentPadding = new Rect();
    private boolean mPreventCornerOverlap;
    final Rect mShadowBounds = new Rect();
    int mUserSetMinHeight;
    int mUserSetMinWidth;

    /* renamed from: android.support.v7.widget.CardView$1 */
    class C08821 implements CardViewDelegate {
        private Drawable mCardBackground;

        C08821() {
        }

        public void setCardBackground(Drawable drawable) {
            this.mCardBackground = drawable;
            CardView.this.setBackgroundDrawable(drawable);
        }

        public boolean getUseCompatPadding() {
            return CardView.this.getUseCompatPadding();
        }

        public boolean getPreventCornerOverlap() {
            return CardView.this.getPreventCornerOverlap();
        }

        public void setShadowPadding(int left, int top, int right, int bottom) {
            CardView.this.mShadowBounds.set(left, top, right, bottom);
            CardView cardView = CardView.this;
            super.setPadding(cardView.mContentPadding.left + left, CardView.this.mContentPadding.top + top, CardView.this.mContentPadding.right + right, CardView.this.mContentPadding.bottom + bottom);
        }

        public void setMinWidthHeightInternal(int width, int height) {
            if (width > CardView.this.mUserSetMinWidth) {
                super.setMinimumWidth(width);
            }
            if (height > CardView.this.mUserSetMinHeight) {
                super.setMinimumHeight(height);
            }
        }

        public Drawable getCardBackground() {
            return this.mCardBackground;
        }

        public View getCardView() {
            return CardView.this;
        }
    }

    static {
        if (VERSION.SDK_INT >= 21) {
            IMPL = new CardViewApi21Impl();
        } else if (VERSION.SDK_INT >= 17) {
            IMPL = new CardViewApi17Impl();
        } else {
            IMPL = new CardViewBaseImpl();
        }
        IMPL.initStatic();
    }

    public CardView(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public CardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    public void setPadding(int left, int top, int right, int bottom) {
    }

    public void setPaddingRelative(int start, int top, int end, int bottom) {
    }

    public boolean getUseCompatPadding() {
        return this.mCompatPadding;
    }

    public void setUseCompatPadding(boolean useCompatPadding) {
        if (this.mCompatPadding != useCompatPadding) {
            this.mCompatPadding = useCompatPadding;
            IMPL.onCompatPaddingChanged(this.mCardViewDelegate);
        }
    }

    public void setContentPadding(int left, int top, int right, int bottom) {
        this.mContentPadding.set(left, top, right, bottom);
        IMPL.updatePadding(this.mCardViewDelegate);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (IMPL instanceof CardViewApi21Impl) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode == Integer.MIN_VALUE || widthMode == 1073741824) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max((int) Math.ceil((double) IMPL.getMinWidth(this.mCardViewDelegate)), MeasureSpec.getSize(widthMeasureSpec)), widthMode);
        }
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == Integer.MIN_VALUE || heightMode == 1073741824) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max((int) Math.ceil((double) IMPL.getMinHeight(this.mCardViewDelegate)), MeasureSpec.getSize(heightMeasureSpec)), heightMode);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        ColorStateList backgroundColor;
        float maxElevation;
        CardView cardView = this;
        TypedArray a = context.obtainStyledAttributes(attrs, C0287R.styleable.CardView, defStyleAttr, C0287R.style.CardView);
        if (a.hasValue(C0287R.styleable.CardView_cardBackgroundColor)) {
            backgroundColor = a.getColorStateList(C0287R.styleable.CardView_cardBackgroundColor);
        } else {
            int color;
            TypedArray aa = getContext().obtainStyledAttributes(COLOR_BACKGROUND_ATTR);
            int themeColorBackground = aa.getColor(0, 0);
            aa.recycle();
            float[] hsv = new float[3];
            Color.colorToHSV(themeColorBackground, hsv);
            if (hsv[2] > 0.5f) {
                color = getResources().getColor(C0287R.color.cardview_light_background);
            } else {
                color = getResources().getColor(C0287R.color.cardview_dark_background);
            }
            backgroundColor = ColorStateList.valueOf(color);
        }
        float radius = a.getDimension(C0287R.styleable.CardView_cardCornerRadius, 0.0f);
        float elevation = a.getDimension(C0287R.styleable.CardView_cardElevation, 0.0f);
        float maxElevation2 = a.getDimension(C0287R.styleable.CardView_cardMaxElevation, 0.0f);
        cardView.mCompatPadding = a.getBoolean(C0287R.styleable.CardView_cardUseCompatPadding, false);
        cardView.mPreventCornerOverlap = a.getBoolean(C0287R.styleable.CardView_cardPreventCornerOverlap, true);
        int defaultPadding = a.getDimensionPixelSize(C0287R.styleable.CardView_contentPadding, 0);
        cardView.mContentPadding.left = a.getDimensionPixelSize(C0287R.styleable.CardView_contentPaddingLeft, defaultPadding);
        cardView.mContentPadding.top = a.getDimensionPixelSize(C0287R.styleable.CardView_contentPaddingTop, defaultPadding);
        cardView.mContentPadding.right = a.getDimensionPixelSize(C0287R.styleable.CardView_contentPaddingRight, defaultPadding);
        cardView.mContentPadding.bottom = a.getDimensionPixelSize(C0287R.styleable.CardView_contentPaddingBottom, defaultPadding);
        if (elevation > maxElevation2) {
            maxElevation = elevation;
        } else {
            maxElevation = maxElevation2;
        }
        cardView.mUserSetMinWidth = a.getDimensionPixelSize(C0287R.styleable.CardView_android_minWidth, 0);
        cardView.mUserSetMinHeight = a.getDimensionPixelSize(C0287R.styleable.CardView_android_minHeight, 0);
        a.recycle();
        IMPL.initialize(cardView.mCardViewDelegate, context, backgroundColor, radius, elevation, maxElevation);
    }

    public void setMinimumWidth(int minWidth) {
        this.mUserSetMinWidth = minWidth;
        super.setMinimumWidth(minWidth);
    }

    public void setMinimumHeight(int minHeight) {
        this.mUserSetMinHeight = minHeight;
        super.setMinimumHeight(minHeight);
    }

    public void setCardBackgroundColor(@ColorInt int color) {
        IMPL.setBackgroundColor(this.mCardViewDelegate, ColorStateList.valueOf(color));
    }

    public void setCardBackgroundColor(@Nullable ColorStateList color) {
        IMPL.setBackgroundColor(this.mCardViewDelegate, color);
    }

    public ColorStateList getCardBackgroundColor() {
        return IMPL.getBackgroundColor(this.mCardViewDelegate);
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

    public void setRadius(float radius) {
        IMPL.setRadius(this.mCardViewDelegate, radius);
    }

    public float getRadius() {
        return IMPL.getRadius(this.mCardViewDelegate);
    }

    public void setCardElevation(float elevation) {
        IMPL.setElevation(this.mCardViewDelegate, elevation);
    }

    public float getCardElevation() {
        return IMPL.getElevation(this.mCardViewDelegate);
    }

    public void setMaxCardElevation(float maxElevation) {
        IMPL.setMaxElevation(this.mCardViewDelegate, maxElevation);
    }

    public float getMaxCardElevation() {
        return IMPL.getMaxElevation(this.mCardViewDelegate);
    }

    public boolean getPreventCornerOverlap() {
        return this.mPreventCornerOverlap;
    }

    public void setPreventCornerOverlap(boolean preventCornerOverlap) {
        if (preventCornerOverlap != this.mPreventCornerOverlap) {
            this.mPreventCornerOverlap = preventCornerOverlap;
            IMPL.onPreventCornerOverlapChanged(this.mCardViewDelegate);
        }
    }
}
