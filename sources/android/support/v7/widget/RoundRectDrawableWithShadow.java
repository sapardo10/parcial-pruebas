package android.support.v7.widget;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.cardview.C0287R;

class RoundRectDrawableWithShadow extends Drawable {
    private static final double COS_45 = Math.cos(Math.toRadians(45.0d));
    private static final float SHADOW_MULTIPLIER = 1.5f;
    static RoundRectHelper sRoundRectHelper;
    private boolean mAddPaddingForCorners = true;
    private ColorStateList mBackground;
    private final RectF mCardBounds;
    private float mCornerRadius;
    private Paint mCornerShadowPaint;
    private Path mCornerShadowPath;
    private boolean mDirty = true;
    private Paint mEdgeShadowPaint;
    private final int mInsetShadow;
    private Paint mPaint;
    private boolean mPrintedShadowClipWarning = false;
    private float mRawMaxShadowSize;
    private float mRawShadowSize;
    private final int mShadowEndColor;
    private float mShadowSize;
    private final int mShadowStartColor;

    interface RoundRectHelper {
        void drawRoundRect(Canvas canvas, RectF rectF, float f, Paint paint);
    }

    RoundRectDrawableWithShadow(Resources resources, ColorStateList backgroundColor, float radius, float shadowSize, float maxShadowSize) {
        this.mShadowStartColor = resources.getColor(C0287R.color.cardview_shadow_start_color);
        this.mShadowEndColor = resources.getColor(C0287R.color.cardview_shadow_end_color);
        this.mInsetShadow = resources.getDimensionPixelSize(C0287R.dimen.cardview_compat_inset_shadow);
        this.mPaint = new Paint(5);
        setBackground(backgroundColor);
        this.mCornerShadowPaint = new Paint(5);
        this.mCornerShadowPaint.setStyle(Style.FILL);
        this.mCornerRadius = (float) ((int) (0.5f + radius));
        this.mCardBounds = new RectF();
        this.mEdgeShadowPaint = new Paint(this.mCornerShadowPaint);
        this.mEdgeShadowPaint.setAntiAlias(false);
        setShadowSize(shadowSize, maxShadowSize);
    }

    private void setBackground(ColorStateList color) {
        this.mBackground = color == null ? ColorStateList.valueOf(0) : color;
        this.mPaint.setColor(this.mBackground.getColorForState(getState(), this.mBackground.getDefaultColor()));
    }

    private int toEven(float value) {
        int i = (int) (1056964608 + value);
        if (i % 2 == 1) {
            return i - 1;
        }
        return i;
    }

    void setAddPaddingForCorners(boolean addPaddingForCorners) {
        this.mAddPaddingForCorners = addPaddingForCorners;
        invalidateSelf();
    }

    public void setAlpha(int alpha) {
        this.mPaint.setAlpha(alpha);
        this.mCornerShadowPaint.setAlpha(alpha);
        this.mEdgeShadowPaint.setAlpha(alpha);
    }

    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.mDirty = true;
    }

    private void setShadowSize(float shadowSize, float maxShadowSize) {
        StringBuilder stringBuilder;
        if (shadowSize < 0.0f) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Invalid shadow size ");
            stringBuilder.append(shadowSize);
            stringBuilder.append(". Must be >= 0");
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (maxShadowSize >= 0.0f) {
            shadowSize = (float) toEven(shadowSize);
            maxShadowSize = (float) toEven(maxShadowSize);
            if (shadowSize > maxShadowSize) {
                shadowSize = maxShadowSize;
                if (!this.mPrintedShadowClipWarning) {
                    this.mPrintedShadowClipWarning = true;
                }
            }
            if (this.mRawShadowSize != shadowSize || this.mRawMaxShadowSize != maxShadowSize) {
                this.mRawShadowSize = shadowSize;
                this.mRawMaxShadowSize = maxShadowSize;
                this.mShadowSize = (float) ((int) (((SHADOW_MULTIPLIER * shadowSize) + ((float) this.mInsetShadow)) + 0.5f));
                this.mDirty = true;
                invalidateSelf();
            }
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Invalid max shadow size ");
            stringBuilder.append(maxShadowSize);
            stringBuilder.append(". Must be >= 0");
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public boolean getPadding(Rect padding) {
        int vOffset = (int) Math.ceil((double) calculateVerticalPadding(this.mRawMaxShadowSize, this.mCornerRadius, this.mAddPaddingForCorners));
        int hOffset = (int) Math.ceil((double) calculateHorizontalPadding(this.mRawMaxShadowSize, this.mCornerRadius, this.mAddPaddingForCorners));
        padding.set(hOffset, vOffset, hOffset, vOffset);
        return true;
    }

    static float calculateVerticalPadding(float maxShadowSize, float cornerRadius, boolean addPaddingForCorners) {
        if (!addPaddingForCorners) {
            return SHADOW_MULTIPLIER * maxShadowSize;
        }
        double d = (double) (SHADOW_MULTIPLIER * maxShadowSize);
        double d2 = 1.0d - COS_45;
        double d3 = (double) cornerRadius;
        Double.isNaN(d3);
        d2 *= d3;
        Double.isNaN(d);
        return (float) (d + d2);
    }

    static float calculateHorizontalPadding(float maxShadowSize, float cornerRadius, boolean addPaddingForCorners) {
        if (!addPaddingForCorners) {
            return maxShadowSize;
        }
        double d = (double) maxShadowSize;
        double d2 = 1.0d - COS_45;
        double d3 = (double) cornerRadius;
        Double.isNaN(d3);
        d2 *= d3;
        Double.isNaN(d);
        return (float) (d + d2);
    }

    protected boolean onStateChange(int[] stateSet) {
        int newColor = this.mBackground;
        newColor = newColor.getColorForState(stateSet, newColor.getDefaultColor());
        if (this.mPaint.getColor() == newColor) {
            return false;
        }
        this.mPaint.setColor(newColor);
        this.mDirty = true;
        invalidateSelf();
        return true;
    }

    public boolean isStateful() {
        ColorStateList colorStateList = this.mBackground;
        return (colorStateList != null && colorStateList.isStateful()) || super.isStateful();
    }

    public void setColorFilter(ColorFilter cf) {
        this.mPaint.setColorFilter(cf);
    }

    public int getOpacity() {
        return -3;
    }

    void setCornerRadius(float radius) {
        if (radius >= 0.0f) {
            radius = (float) ((int) (0.5f + radius));
            if (this.mCornerRadius != radius) {
                this.mCornerRadius = radius;
                this.mDirty = true;
                invalidateSelf();
                return;
            }
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Invalid radius ");
        stringBuilder.append(radius);
        stringBuilder.append(". Must be >= 0");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public void draw(Canvas canvas) {
        if (this.mDirty) {
            buildComponents(getBounds());
            this.mDirty = false;
        }
        canvas.translate(0.0f, this.mRawShadowSize / 2.0f);
        drawShadow(canvas);
        canvas.translate(0.0f, (-this.mRawShadowSize) / 2.0f);
        sRoundRectHelper.drawRoundRect(canvas, this.mCardBounds, this.mCornerRadius, this.mPaint);
    }

    private void drawShadow(Canvas canvas) {
        float f = this.mCornerRadius;
        float edgeShadowTop = (-f) - this.mShadowSize;
        f = (f + ((float) this.mInsetShadow)) + (this.mRawShadowSize / 2.0f);
        boolean z = true;
        boolean drawHorizontalEdges = this.mCardBounds.width() - (f * 2.0f) > 0.0f;
        if (this.mCardBounds.height() - (f * 2.0f) <= 0.0f) {
            z = false;
        }
        boolean drawVerticalEdges = z;
        int saved = canvas.save();
        canvas.translate(this.mCardBounds.left + f, this.mCardBounds.top + f);
        canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
        if (drawHorizontalEdges) {
            canvas.drawRect(0.0f, edgeShadowTop, this.mCardBounds.width() - (f * 2.0f), -this.mCornerRadius, this.mEdgeShadowPaint);
        }
        canvas.restoreToCount(saved);
        saved = canvas.save();
        canvas.translate(this.mCardBounds.right - f, this.mCardBounds.bottom - f);
        canvas.rotate(180.0f);
        canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
        if (drawHorizontalEdges) {
            canvas.drawRect(0.0f, edgeShadowTop, this.mCardBounds.width() - (f * 2.0f), (-this.mCornerRadius) + this.mShadowSize, this.mEdgeShadowPaint);
        }
        canvas.restoreToCount(saved);
        saved = canvas.save();
        canvas.translate(this.mCardBounds.left + f, this.mCardBounds.bottom - f);
        canvas.rotate(270.0f);
        canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
        if (drawVerticalEdges) {
            canvas.drawRect(0.0f, edgeShadowTop, this.mCardBounds.height() - (f * 2.0f), -this.mCornerRadius, this.mEdgeShadowPaint);
        }
        canvas.restoreToCount(saved);
        saved = canvas.save();
        canvas.translate(this.mCardBounds.right - f, this.mCardBounds.top + f);
        canvas.rotate(90.0f);
        canvas.drawPath(this.mCornerShadowPath, this.mCornerShadowPaint);
        if (drawVerticalEdges) {
            canvas.drawRect(0.0f, edgeShadowTop, this.mCardBounds.height() - (2.0f * f), -this.mCornerRadius, this.mEdgeShadowPaint);
        }
        canvas.restoreToCount(saved);
    }

    private void buildShadowCorners() {
        float f = this.mCornerRadius;
        RectF innerBounds = new RectF(-f, -f, f, f);
        RectF outerBounds = new RectF(innerBounds);
        float f2 = this.mShadowSize;
        outerBounds.inset(-f2, -f2);
        Path path = this.mCornerShadowPath;
        if (path == null) {
            r0.mCornerShadowPath = new Path();
        } else {
            path.reset();
        }
        r0.mCornerShadowPath.setFillType(FillType.EVEN_ODD);
        r0.mCornerShadowPath.moveTo(-r0.mCornerRadius, 0.0f);
        r0.mCornerShadowPath.rLineTo(-r0.mShadowSize, 0.0f);
        r0.mCornerShadowPath.arcTo(outerBounds, 180.0f, 90.0f, false);
        r0.mCornerShadowPath.arcTo(innerBounds, 270.0f, -90.0f, false);
        r0.mCornerShadowPath.close();
        f2 = r0.mCornerRadius;
        float f3 = r0.mShadowSize;
        float startRatio = f2 / (f2 + f3);
        Paint paint = r0.mCornerShadowPaint;
        float f4 = f2 + f3;
        r13 = new int[3];
        int i = r0.mShadowStartColor;
        r13[0] = i;
        r13[1] = i;
        r13[2] = r0.mShadowEndColor;
        Shader shader = r9;
        Shader radialGradient = new RadialGradient(0.0f, 0.0f, f4, r13, new float[]{0.0f, startRatio, 1.0f}, TileMode.CLAMP);
        paint.setShader(shader);
        Paint paint2 = r0.mEdgeShadowPaint;
        float f5 = r0.mCornerRadius;
        float f6 = -f5;
        float f7 = r0.mShadowSize;
        f6 += f7;
        float f8 = (-f5) - f7;
        r14 = new int[3];
        int i2 = r0.mShadowStartColor;
        r14[0] = i2;
        r14[1] = i2;
        r14[2] = r0.mShadowEndColor;
        paint2.setShader(new LinearGradient(0.0f, f6, 0.0f, f8, r14, new float[]{0.0f, 0.5f, 1.0f}, TileMode.CLAMP));
        r0.mEdgeShadowPaint.setAntiAlias(false);
    }

    private void buildComponents(Rect bounds) {
        float verticalOffset = this.mRawMaxShadowSize * SHADOW_MULTIPLIER;
        this.mCardBounds.set(((float) bounds.left) + this.mRawMaxShadowSize, ((float) bounds.top) + verticalOffset, ((float) bounds.right) - this.mRawMaxShadowSize, ((float) bounds.bottom) - verticalOffset);
        buildShadowCorners();
    }

    float getCornerRadius() {
        return this.mCornerRadius;
    }

    void getMaxShadowAndCornerPadding(Rect into) {
        getPadding(into);
    }

    void setShadowSize(float size) {
        setShadowSize(size, this.mRawMaxShadowSize);
    }

    void setMaxShadowSize(float size) {
        setShadowSize(this.mRawShadowSize, size);
    }

    float getShadowSize() {
        return this.mRawShadowSize;
    }

    float getMaxShadowSize() {
        return this.mRawMaxShadowSize;
    }

    float getMinWidth() {
        float f = this.mRawMaxShadowSize;
        return ((this.mRawMaxShadowSize + ((float) this.mInsetShadow)) * 2.0f) + (Math.max(f, (this.mCornerRadius + ((float) this.mInsetShadow)) + (f / 2.0f)) * 2.0f);
    }

    float getMinHeight() {
        float f = this.mRawMaxShadowSize;
        return (((this.mRawMaxShadowSize * SHADOW_MULTIPLIER) + ((float) this.mInsetShadow)) * 2.0f) + (Math.max(f, (this.mCornerRadius + ((float) this.mInsetShadow)) + ((f * SHADOW_MULTIPLIER) / 2.0f)) * 2.0f);
    }

    void setColor(@Nullable ColorStateList color) {
        setBackground(color);
        invalidateSelf();
    }

    ColorStateList getColor() {
        return this.mBackground;
    }
}
