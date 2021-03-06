package android.support.design.widget;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.support.annotation.ColorInt;
import android.support.v4.math.MathUtils;
import android.support.v4.text.TextDirectionHeuristicsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.C0286R;
import android.support.v7.widget.TintTypedArray;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.animation.Interpolator;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;

final class CollapsingTextHelper {
    private static final boolean DEBUG_DRAW = false;
    private static final Paint DEBUG_DRAW_PAINT = null;
    private static final boolean USE_SCALING_TEXTURE = (VERSION.SDK_INT < 18);
    private boolean mBoundsChanged;
    private final Rect mCollapsedBounds;
    private float mCollapsedDrawX;
    private float mCollapsedDrawY;
    private int mCollapsedShadowColor;
    private float mCollapsedShadowDx;
    private float mCollapsedShadowDy;
    private float mCollapsedShadowRadius;
    private ColorStateList mCollapsedTextColor;
    private int mCollapsedTextGravity = 16;
    private float mCollapsedTextSize = 15.0f;
    private Typeface mCollapsedTypeface;
    private final RectF mCurrentBounds;
    private float mCurrentDrawX;
    private float mCurrentDrawY;
    private float mCurrentTextSize;
    private Typeface mCurrentTypeface;
    private boolean mDrawTitle;
    private final Rect mExpandedBounds;
    private float mExpandedDrawX;
    private float mExpandedDrawY;
    private float mExpandedFraction;
    private int mExpandedShadowColor;
    private float mExpandedShadowDx;
    private float mExpandedShadowDy;
    private float mExpandedShadowRadius;
    private ColorStateList mExpandedTextColor;
    private int mExpandedTextGravity = 16;
    private float mExpandedTextSize = 15.0f;
    private Bitmap mExpandedTitleTexture;
    private Typeface mExpandedTypeface;
    private boolean mIsRtl;
    private Interpolator mPositionInterpolator;
    private float mScale;
    private int[] mState;
    private CharSequence mText;
    private final TextPaint mTextPaint;
    private Interpolator mTextSizeInterpolator;
    private CharSequence mTextToDraw;
    private float mTextureAscent;
    private float mTextureDescent;
    private Paint mTexturePaint;
    private boolean mUseTexture;
    private final View mView;

    static {
        Paint paint = DEBUG_DRAW_PAINT;
        if (paint != null) {
            paint.setAntiAlias(true);
            DEBUG_DRAW_PAINT.setColor(-65281);
        }
    }

    public CollapsingTextHelper(View view) {
        this.mView = view;
        this.mTextPaint = new TextPaint(TsExtractor.TS_STREAM_TYPE_AC3);
        this.mCollapsedBounds = new Rect();
        this.mExpandedBounds = new Rect();
        this.mCurrentBounds = new RectF();
    }

    void setTextSizeInterpolator(Interpolator interpolator) {
        this.mTextSizeInterpolator = interpolator;
        recalculate();
    }

    void setPositionInterpolator(Interpolator interpolator) {
        this.mPositionInterpolator = interpolator;
        recalculate();
    }

    void setExpandedTextSize(float textSize) {
        if (this.mExpandedTextSize != textSize) {
            this.mExpandedTextSize = textSize;
            recalculate();
        }
    }

    void setCollapsedTextSize(float textSize) {
        if (this.mCollapsedTextSize != textSize) {
            this.mCollapsedTextSize = textSize;
            recalculate();
        }
    }

    void setCollapsedTextColor(ColorStateList textColor) {
        if (this.mCollapsedTextColor != textColor) {
            this.mCollapsedTextColor = textColor;
            recalculate();
        }
    }

    void setExpandedTextColor(ColorStateList textColor) {
        if (this.mExpandedTextColor != textColor) {
            this.mExpandedTextColor = textColor;
            recalculate();
        }
    }

    void setExpandedBounds(int left, int top, int right, int bottom) {
        if (!rectEquals(this.mExpandedBounds, left, top, right, bottom)) {
            this.mExpandedBounds.set(left, top, right, bottom);
            this.mBoundsChanged = true;
            onBoundsChanged();
        }
    }

    void setCollapsedBounds(int left, int top, int right, int bottom) {
        if (!rectEquals(this.mCollapsedBounds, left, top, right, bottom)) {
            this.mCollapsedBounds.set(left, top, right, bottom);
            this.mBoundsChanged = true;
            onBoundsChanged();
        }
    }

    void onBoundsChanged() {
        boolean z;
        if (this.mCollapsedBounds.width() > 0 && this.mCollapsedBounds.height() > 0) {
            if (this.mExpandedBounds.width() > 0 && this.mExpandedBounds.height() > 0) {
                z = true;
                this.mDrawTitle = z;
            }
        }
        z = false;
        this.mDrawTitle = z;
    }

    void setExpandedTextGravity(int gravity) {
        if (this.mExpandedTextGravity != gravity) {
            this.mExpandedTextGravity = gravity;
            recalculate();
        }
    }

    int getExpandedTextGravity() {
        return this.mExpandedTextGravity;
    }

    void setCollapsedTextGravity(int gravity) {
        if (this.mCollapsedTextGravity != gravity) {
            this.mCollapsedTextGravity = gravity;
            recalculate();
        }
    }

    int getCollapsedTextGravity() {
        return this.mCollapsedTextGravity;
    }

    void setCollapsedTextAppearance(int resId) {
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(this.mView.getContext(), resId, C0286R.styleable.TextAppearance);
        if (a.hasValue(C0286R.styleable.TextAppearance_android_textColor)) {
            this.mCollapsedTextColor = a.getColorStateList(C0286R.styleable.TextAppearance_android_textColor);
        }
        if (a.hasValue(C0286R.styleable.TextAppearance_android_textSize)) {
            this.mCollapsedTextSize = (float) a.getDimensionPixelSize(C0286R.styleable.TextAppearance_android_textSize, (int) this.mCollapsedTextSize);
        }
        this.mCollapsedShadowColor = a.getInt(C0286R.styleable.TextAppearance_android_shadowColor, 0);
        this.mCollapsedShadowDx = a.getFloat(C0286R.styleable.TextAppearance_android_shadowDx, 0.0f);
        this.mCollapsedShadowDy = a.getFloat(C0286R.styleable.TextAppearance_android_shadowDy, 0.0f);
        this.mCollapsedShadowRadius = a.getFloat(C0286R.styleable.TextAppearance_android_shadowRadius, 0.0f);
        a.recycle();
        if (VERSION.SDK_INT >= 16) {
            this.mCollapsedTypeface = readFontFamilyTypeface(resId);
        }
        recalculate();
    }

    void setExpandedTextAppearance(int resId) {
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(this.mView.getContext(), resId, C0286R.styleable.TextAppearance);
        if (a.hasValue(C0286R.styleable.TextAppearance_android_textColor)) {
            this.mExpandedTextColor = a.getColorStateList(C0286R.styleable.TextAppearance_android_textColor);
        }
        if (a.hasValue(C0286R.styleable.TextAppearance_android_textSize)) {
            this.mExpandedTextSize = (float) a.getDimensionPixelSize(C0286R.styleable.TextAppearance_android_textSize, (int) this.mExpandedTextSize);
        }
        this.mExpandedShadowColor = a.getInt(C0286R.styleable.TextAppearance_android_shadowColor, 0);
        this.mExpandedShadowDx = a.getFloat(C0286R.styleable.TextAppearance_android_shadowDx, 0.0f);
        this.mExpandedShadowDy = a.getFloat(C0286R.styleable.TextAppearance_android_shadowDy, 0.0f);
        this.mExpandedShadowRadius = a.getFloat(C0286R.styleable.TextAppearance_android_shadowRadius, 0.0f);
        a.recycle();
        if (VERSION.SDK_INT >= 16) {
            this.mExpandedTypeface = readFontFamilyTypeface(resId);
        }
        recalculate();
    }

    private Typeface readFontFamilyTypeface(int resId) {
        TypedArray a = this.mView.getContext();
        int[] iArr = new int[1];
        Typeface typeface = null;
        iArr[0] = 16843692;
        a = a.obtainStyledAttributes(resId, iArr);
        try {
            String family = a.getString(0);
            if (family != null) {
                typeface = Typeface.create(family, 0);
                return typeface;
            }
            a.recycle();
            return null;
        } finally {
            a.recycle();
        }
    }

    void setCollapsedTypeface(Typeface typeface) {
        if (areTypefacesDifferent(this.mCollapsedTypeface, typeface)) {
            this.mCollapsedTypeface = typeface;
            recalculate();
        }
    }

    void setExpandedTypeface(Typeface typeface) {
        if (areTypefacesDifferent(this.mExpandedTypeface, typeface)) {
            this.mExpandedTypeface = typeface;
            recalculate();
        }
    }

    void setTypefaces(Typeface typeface) {
        this.mExpandedTypeface = typeface;
        this.mCollapsedTypeface = typeface;
        recalculate();
    }

    Typeface getCollapsedTypeface() {
        Typeface typeface = this.mCollapsedTypeface;
        return typeface != null ? typeface : Typeface.DEFAULT;
    }

    Typeface getExpandedTypeface() {
        Typeface typeface = this.mExpandedTypeface;
        return typeface != null ? typeface : Typeface.DEFAULT;
    }

    void setExpansionFraction(float fraction) {
        fraction = MathUtils.clamp(fraction, 0.0f, 1.0f);
        if (fraction != this.mExpandedFraction) {
            this.mExpandedFraction = fraction;
            calculateCurrentOffsets();
        }
    }

    final boolean setState(int[] state) {
        this.mState = state;
        if (!isStateful()) {
            return false;
        }
        recalculate();
        return true;
    }

    final boolean isStateful() {
        ColorStateList colorStateList = this.mCollapsedTextColor;
        if (colorStateList != null) {
            if (colorStateList.isStateful()) {
                return true;
            }
        }
        colorStateList = this.mExpandedTextColor;
        if (colorStateList != null) {
            if (!colorStateList.isStateful()) {
            }
            return true;
        }
        return false;
    }

    float getExpansionFraction() {
        return this.mExpandedFraction;
    }

    float getCollapsedTextSize() {
        return this.mCollapsedTextSize;
    }

    float getExpandedTextSize() {
        return this.mExpandedTextSize;
    }

    private void calculateCurrentOffsets() {
        calculateOffsets(this.mExpandedFraction);
    }

    private void calculateOffsets(float fraction) {
        interpolateBounds(fraction);
        this.mCurrentDrawX = lerp(this.mExpandedDrawX, this.mCollapsedDrawX, fraction, this.mPositionInterpolator);
        this.mCurrentDrawY = lerp(this.mExpandedDrawY, this.mCollapsedDrawY, fraction, this.mPositionInterpolator);
        setInterpolatedTextSize(lerp(this.mExpandedTextSize, this.mCollapsedTextSize, fraction, this.mTextSizeInterpolator));
        if (this.mCollapsedTextColor != this.mExpandedTextColor) {
            this.mTextPaint.setColor(blendColors(getCurrentExpandedTextColor(), getCurrentCollapsedTextColor(), fraction));
        } else {
            this.mTextPaint.setColor(getCurrentCollapsedTextColor());
        }
        this.mTextPaint.setShadowLayer(lerp(this.mExpandedShadowRadius, this.mCollapsedShadowRadius, fraction, null), lerp(this.mExpandedShadowDx, this.mCollapsedShadowDx, fraction, null), lerp(this.mExpandedShadowDy, this.mCollapsedShadowDy, fraction, null), blendColors(this.mExpandedShadowColor, this.mCollapsedShadowColor, fraction));
        ViewCompat.postInvalidateOnAnimation(this.mView);
    }

    @ColorInt
    private int getCurrentExpandedTextColor() {
        int[] iArr = this.mState;
        if (iArr != null) {
            return this.mExpandedTextColor.getColorForState(iArr, 0);
        }
        return this.mExpandedTextColor.getDefaultColor();
    }

    @ColorInt
    private int getCurrentCollapsedTextColor() {
        int[] iArr = this.mState;
        if (iArr != null) {
            return this.mCollapsedTextColor.getColorForState(iArr, 0);
        }
        return this.mCollapsedTextColor.getDefaultColor();
    }

    private void calculateBaseOffsets() {
        float currentTextSize = this.mCurrentTextSize;
        calculateUsingTextSize(this.mCollapsedTextSize);
        CharSequence charSequence = this.mTextToDraw;
        float f = 0.0f;
        float width = charSequence != null ? this.mTextPaint.measureText(charSequence, 0, charSequence.length()) : 0.0f;
        int collapsedAbsGravity = GravityCompat.getAbsoluteGravity(this.mCollapsedTextGravity, this.mIsRtl);
        int i = collapsedAbsGravity & 112;
        if (i == 48) {
            this.mCollapsedDrawY = ((float) this.mCollapsedBounds.top) - this.mTextPaint.ascent();
        } else if (i != 80) {
            this.mCollapsedDrawY = ((float) this.mCollapsedBounds.centerY()) + (((this.mTextPaint.descent() - this.mTextPaint.ascent()) / 2.0f) - this.mTextPaint.descent());
        } else {
            this.mCollapsedDrawY = (float) this.mCollapsedBounds.bottom;
        }
        int i2 = collapsedAbsGravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        if (i2 == 1) {
            this.mCollapsedDrawX = ((float) this.mCollapsedBounds.centerX()) - (width / 2.0f);
        } else if (i2 != 5) {
            this.mCollapsedDrawX = (float) this.mCollapsedBounds.left;
        } else {
            this.mCollapsedDrawX = ((float) this.mCollapsedBounds.right) - width;
        }
        calculateUsingTextSize(this.mExpandedTextSize);
        CharSequence charSequence2 = this.mTextToDraw;
        if (charSequence2 != null) {
            f = this.mTextPaint.measureText(charSequence2, 0, charSequence2.length());
        }
        width = f;
        int expandedAbsGravity = GravityCompat.getAbsoluteGravity(this.mExpandedTextGravity, this.mIsRtl);
        int i3 = expandedAbsGravity & 112;
        if (i3 == 48) {
            this.mExpandedDrawY = ((float) this.mExpandedBounds.top) - this.mTextPaint.ascent();
        } else if (i3 != 80) {
            this.mExpandedDrawY = ((float) this.mExpandedBounds.centerY()) + (((this.mTextPaint.descent() - this.mTextPaint.ascent()) / 2.0f) - this.mTextPaint.descent());
        } else {
            this.mExpandedDrawY = (float) this.mExpandedBounds.bottom;
        }
        i3 = expandedAbsGravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        if (i3 == 1) {
            this.mExpandedDrawX = ((float) this.mExpandedBounds.centerX()) - (width / 2.0f);
        } else if (i3 != 5) {
            this.mExpandedDrawX = (float) this.mExpandedBounds.left;
        } else {
            this.mExpandedDrawX = ((float) this.mExpandedBounds.right) - width;
        }
        clearTexture();
        setInterpolatedTextSize(currentTextSize);
    }

    private void interpolateBounds(float fraction) {
        this.mCurrentBounds.left = lerp((float) this.mExpandedBounds.left, (float) this.mCollapsedBounds.left, fraction, this.mPositionInterpolator);
        this.mCurrentBounds.top = lerp(this.mExpandedDrawY, this.mCollapsedDrawY, fraction, this.mPositionInterpolator);
        this.mCurrentBounds.right = lerp((float) this.mExpandedBounds.right, (float) this.mCollapsedBounds.right, fraction, this.mPositionInterpolator);
        this.mCurrentBounds.bottom = lerp((float) this.mExpandedBounds.bottom, (float) this.mCollapsedBounds.bottom, fraction, this.mPositionInterpolator);
    }

    public void draw(Canvas canvas) {
        int saveCount = canvas.save();
        if (this.mTextToDraw != null && this.mDrawTitle) {
            float ascent;
            float y;
            float x = this.mCurrentDrawX;
            float y2 = this.mCurrentDrawY;
            boolean z = this.mUseTexture && this.mExpandedTitleTexture != null;
            boolean drawTexture = z;
            float f;
            if (drawTexture) {
                float ascent2 = this.mTextureAscent;
                float f2 = this.mScale;
                ascent = ascent2 * f2;
                f = this.mTextureDescent * f2;
            } else {
                ascent = this.mTextPaint.ascent() * this.mScale;
                f = this.mScale * this.mTextPaint.descent();
            }
            if (drawTexture) {
                y = y2 + ascent;
            } else {
                y = y2;
            }
            y2 = this.mScale;
            if (y2 != 1.0f) {
                canvas.scale(y2, y2, x, y);
            }
            if (drawTexture) {
                canvas.drawBitmap(this.mExpandedTitleTexture, x, y, this.mTexturePaint);
            } else {
                CharSequence charSequence = this.mTextToDraw;
                canvas.drawText(charSequence, 0, charSequence.length(), x, y, this.mTextPaint);
            }
        }
        canvas.restoreToCount(saveCount);
    }

    private boolean calculateIsRtl(CharSequence text) {
        boolean z = true;
        if (ViewCompat.getLayoutDirection(this.mView) != 1) {
            z = false;
        }
        return (z ? TextDirectionHeuristicsCompat.FIRSTSTRONG_RTL : TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR).isRtl(text, 0, text.length());
    }

    private void setInterpolatedTextSize(float textSize) {
        calculateUsingTextSize(textSize);
        boolean z = USE_SCALING_TEXTURE && this.mScale != 1.0f;
        this.mUseTexture = z;
        if (this.mUseTexture) {
            ensureExpandedTexture();
        }
        ViewCompat.postInvalidateOnAnimation(this.mView);
    }

    private boolean areTypefacesDifferent(Typeface first, Typeface second) {
        if (first != null) {
            if (first.equals(second)) {
            }
        }
        return first == null && second != null;
    }

    private void calculateUsingTextSize(float textSize) {
        if (this.mText != null) {
            float newTextSize;
            float availableWidth;
            float collapsedWidth = (float) this.mCollapsedBounds.width();
            float expandedWidth = (float) this.mExpandedBounds.width();
            boolean updateDrawText = false;
            if (isClose(textSize, this.mCollapsedTextSize)) {
                newTextSize = this.mCollapsedTextSize;
                this.mScale = 1.0f;
                if (areTypefacesDifferent(this.mCurrentTypeface, this.mCollapsedTypeface)) {
                    this.mCurrentTypeface = this.mCollapsedTypeface;
                    updateDrawText = true;
                }
                availableWidth = collapsedWidth;
            } else {
                newTextSize = this.mExpandedTextSize;
                if (areTypefacesDifferent(this.mCurrentTypeface, this.mExpandedTypeface)) {
                    this.mCurrentTypeface = this.mExpandedTypeface;
                    updateDrawText = true;
                }
                if (isClose(textSize, this.mExpandedTextSize)) {
                    this.mScale = 1.0f;
                } else {
                    this.mScale = textSize / this.mExpandedTextSize;
                }
                availableWidth = this.mCollapsedTextSize / this.mExpandedTextSize;
                if (expandedWidth * availableWidth > collapsedWidth) {
                    availableWidth = Math.min(collapsedWidth / availableWidth, expandedWidth);
                } else {
                    availableWidth = expandedWidth;
                }
            }
            boolean z = true;
            if (availableWidth > 0.0f) {
                boolean z2;
                if (this.mCurrentTextSize == newTextSize && !this.mBoundsChanged) {
                    if (!updateDrawText) {
                        z2 = false;
                        updateDrawText = z2;
                        this.mCurrentTextSize = newTextSize;
                        this.mBoundsChanged = false;
                    }
                }
                z2 = true;
                updateDrawText = z2;
                this.mCurrentTextSize = newTextSize;
                this.mBoundsChanged = false;
            }
            if (this.mTextToDraw != null) {
                if (!updateDrawText) {
                }
            }
            this.mTextPaint.setTextSize(this.mCurrentTextSize);
            this.mTextPaint.setTypeface(this.mCurrentTypeface);
            TextPaint textPaint = this.mTextPaint;
            if (this.mScale == 1.0f) {
                z = false;
            }
            textPaint.setLinearText(z);
            CharSequence title = TextUtils.ellipsize(this.mText, this.mTextPaint, availableWidth, TruncateAt.END);
            if (!TextUtils.equals(title, this.mTextToDraw)) {
                this.mTextToDraw = title;
                this.mIsRtl = calculateIsRtl(this.mTextToDraw);
            }
        }
    }

    private void ensureExpandedTexture() {
        if (this.mExpandedTitleTexture == null && !this.mExpandedBounds.isEmpty()) {
            if (!TextUtils.isEmpty(this.mTextToDraw)) {
                calculateOffsets(0.0f);
                this.mTextureAscent = this.mTextPaint.ascent();
                this.mTextureDescent = this.mTextPaint.descent();
                TextPaint textPaint = this.mTextPaint;
                CharSequence charSequence = this.mTextToDraw;
                int w = Math.round(textPaint.measureText(charSequence, 0, charSequence.length()));
                int h = Math.round(this.mTextureDescent - this.mTextureAscent);
                if (w > 0) {
                    if (h > 0) {
                        this.mExpandedTitleTexture = Bitmap.createBitmap(w, h, Config.ARGB_8888);
                        Canvas c = new Canvas(this.mExpandedTitleTexture);
                        CharSequence charSequence2 = this.mTextToDraw;
                        c.drawText(charSequence2, 0, charSequence2.length(), 0.0f, ((float) h) - this.mTextPaint.descent(), this.mTextPaint);
                        if (this.mTexturePaint == null) {
                            this.mTexturePaint = new Paint(3);
                        }
                    }
                }
            }
        }
    }

    public void recalculate() {
        if (this.mView.getHeight() > 0 && this.mView.getWidth() > 0) {
            calculateBaseOffsets();
            calculateCurrentOffsets();
        }
    }

    void setText(CharSequence text) {
        if (text != null) {
            if (text.equals(this.mText)) {
                return;
            }
        }
        this.mText = text;
        this.mTextToDraw = null;
        clearTexture();
        recalculate();
    }

    CharSequence getText() {
        return this.mText;
    }

    private void clearTexture() {
        Bitmap bitmap = this.mExpandedTitleTexture;
        if (bitmap != null) {
            bitmap.recycle();
            this.mExpandedTitleTexture = null;
        }
    }

    private static boolean isClose(float value, float targetValue) {
        return Math.abs(value - targetValue) < 0.001f;
    }

    ColorStateList getExpandedTextColor() {
        return this.mExpandedTextColor;
    }

    ColorStateList getCollapsedTextColor() {
        return this.mCollapsedTextColor;
    }

    private static int blendColors(int color1, int color2, float ratio) {
        float inverseRatio = 1.0f - ratio;
        return Color.argb((int) ((((float) Color.alpha(color1)) * inverseRatio) + (((float) Color.alpha(color2)) * ratio)), (int) ((((float) Color.red(color1)) * inverseRatio) + (((float) Color.red(color2)) * ratio)), (int) ((((float) Color.green(color1)) * inverseRatio) + (((float) Color.green(color2)) * ratio)), (int) ((((float) Color.blue(color1)) * inverseRatio) + (((float) Color.blue(color2)) * ratio)));
    }

    private static float lerp(float startValue, float endValue, float fraction, Interpolator interpolator) {
        if (interpolator != null) {
            fraction = interpolator.getInterpolation(fraction);
        }
        return AnimationUtils.lerp(startValue, endValue, fraction);
    }

    private static boolean rectEquals(Rect r, int left, int top, int right, int bottom) {
        return r.left == left && r.top == top && r.right == right && r.bottom == bottom;
    }
}
