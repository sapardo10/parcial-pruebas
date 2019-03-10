package android.support.design.widget;

import android.content.Context;
import android.graphics.Canvas;
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
import android.support.design.C0035R;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.drawable.DrawableWrapper;

class ShadowDrawableWrapper extends DrawableWrapper {
    static final double COS_45 = Math.cos(Math.toRadians(45.0d));
    static final float SHADOW_BOTTOM_SCALE = 1.0f;
    static final float SHADOW_HORIZ_SCALE = 0.5f;
    static final float SHADOW_MULTIPLIER = 1.5f;
    static final float SHADOW_TOP_SCALE = 0.25f;
    private boolean mAddPaddingForCorners = true;
    final RectF mContentBounds;
    float mCornerRadius;
    final Paint mCornerShadowPaint;
    Path mCornerShadowPath;
    private boolean mDirty = true;
    final Paint mEdgeShadowPaint;
    float mMaxShadowSize;
    private boolean mPrintedShadowClipWarning = false;
    float mRawMaxShadowSize;
    float mRawShadowSize;
    private float mRotation;
    private final int mShadowEndColor;
    private final int mShadowMiddleColor;
    float mShadowSize;
    private final int mShadowStartColor;

    public ShadowDrawableWrapper(Context context, Drawable content, float radius, float shadowSize, float maxShadowSize) {
        super(content);
        this.mShadowStartColor = ContextCompat.getColor(context, C0035R.color.design_fab_shadow_start_color);
        this.mShadowMiddleColor = ContextCompat.getColor(context, C0035R.color.design_fab_shadow_mid_color);
        this.mShadowEndColor = ContextCompat.getColor(context, C0035R.color.design_fab_shadow_end_color);
        this.mCornerShadowPaint = new Paint(5);
        this.mCornerShadowPaint.setStyle(Style.FILL);
        this.mCornerRadius = (float) Math.round(radius);
        this.mContentBounds = new RectF();
        this.mEdgeShadowPaint = new Paint(this.mCornerShadowPaint);
        this.mEdgeShadowPaint.setAntiAlias(false);
        setShadowSize(shadowSize, maxShadowSize);
    }

    private static int toEven(float value) {
        int i = Math.round(value);
        return i % 2 == 1 ? i - 1 : i;
    }

    public void setAddPaddingForCorners(boolean addPaddingForCorners) {
        this.mAddPaddingForCorners = addPaddingForCorners;
        invalidateSelf();
    }

    public void setAlpha(int alpha) {
        super.setAlpha(alpha);
        this.mCornerShadowPaint.setAlpha(alpha);
        this.mEdgeShadowPaint.setAlpha(alpha);
    }

    protected void onBoundsChange(Rect bounds) {
        this.mDirty = true;
    }

    void setShadowSize(float shadowSize, float maxShadowSize) {
        if (shadowSize < 0.0f || maxShadowSize < 0.0f) {
            throw new IllegalArgumentException("invalid shadow size");
        }
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
            this.mShadowSize = (float) Math.round(SHADOW_MULTIPLIER * shadowSize);
            this.mMaxShadowSize = maxShadowSize;
            this.mDirty = true;
            invalidateSelf();
        }
    }

    public boolean getPadding(Rect padding) {
        int vOffset = (int) Math.ceil((double) calculateVerticalPadding(this.mRawMaxShadowSize, this.mCornerRadius, this.mAddPaddingForCorners));
        int hOffset = (int) Math.ceil((double) calculateHorizontalPadding(this.mRawMaxShadowSize, this.mCornerRadius, this.mAddPaddingForCorners));
        padding.set(hOffset, vOffset, hOffset, vOffset);
        return true;
    }

    public static float calculateVerticalPadding(float maxShadowSize, float cornerRadius, boolean addPaddingForCorners) {
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

    public static float calculateHorizontalPadding(float maxShadowSize, float cornerRadius, boolean addPaddingForCorners) {
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

    public int getOpacity() {
        return -3;
    }

    public void setCornerRadius(float radius) {
        radius = (float) Math.round(radius);
        if (this.mCornerRadius != radius) {
            this.mCornerRadius = radius;
            this.mDirty = true;
            invalidateSelf();
        }
    }

    public void draw(Canvas canvas) {
        if (this.mDirty) {
            buildComponents(getBounds());
            this.mDirty = false;
        }
        drawShadow(canvas);
        super.draw(canvas);
    }

    final void setRotation(float rotation) {
        if (this.mRotation != rotation) {
            this.mRotation = rotation;
            invalidateSelf();
        }
    }

    private void drawShadow(Canvas canvas) {
        int saved;
        float shadowScaleBottom;
        float shadowScaleTop;
        float shadowScaleHorizontal;
        Canvas canvas2 = canvas;
        int rotateSaved = canvas.save();
        canvas2.rotate(this.mRotation, this.mContentBounds.centerX(), this.mContentBounds.centerY());
        float edgeShadowTop = (-this.mCornerRadius) - this.mShadowSize;
        float shadowOffset = this.mCornerRadius;
        boolean z = true;
        boolean drawHorizontalEdges = this.mContentBounds.width() - (shadowOffset * 2.0f) > 0.0f;
        if (r0.mContentBounds.height() - (shadowOffset * 2.0f) <= 0.0f) {
            z = false;
        }
        boolean drawVerticalEdges = z;
        float f = r0.mRawShadowSize;
        float shadowOffsetTop = f - (SHADOW_TOP_SCALE * f);
        float shadowOffsetHorizontal = f - (SHADOW_HORIZ_SCALE * f);
        float shadowScaleHorizontal2 = shadowOffset / (shadowOffset + shadowOffsetHorizontal);
        float shadowScaleTop2 = shadowOffset / (shadowOffset + shadowOffsetTop);
        float shadowScaleBottom2 = shadowOffset / (shadowOffset + (f - (f * 1.0f)));
        int saved2 = canvas.save();
        canvas2.translate(r0.mContentBounds.left + shadowOffset, r0.mContentBounds.top + shadowOffset);
        canvas2.scale(shadowScaleHorizontal2, shadowScaleTop2);
        canvas2.drawPath(r0.mCornerShadowPath, r0.mCornerShadowPaint);
        if (drawHorizontalEdges) {
            canvas2.scale(1.0f / shadowScaleHorizontal2, 1.0f);
            saved = saved2;
            shadowScaleBottom = shadowScaleBottom2;
            shadowScaleTop = shadowScaleTop2;
            shadowScaleHorizontal = shadowScaleHorizontal2;
            shadowOffsetHorizontal = 1.0f;
            canvas.drawRect(0.0f, edgeShadowTop, r0.mContentBounds.width() - (shadowOffset * 2.0f), -r0.mCornerRadius, r0.mEdgeShadowPaint);
        } else {
            shadowScaleBottom = shadowScaleBottom2;
            shadowScaleTop = shadowScaleTop2;
            shadowScaleHorizontal = shadowScaleHorizontal2;
            float f2 = shadowOffsetTop;
            float f3 = shadowOffsetHorizontal;
            shadowOffsetHorizontal = 1.0f;
            saved = saved2;
        }
        canvas2.restoreToCount(saved);
        saved = canvas.save();
        canvas2.translate(r0.mContentBounds.right - shadowOffset, r0.mContentBounds.bottom - shadowOffset);
        float shadowScaleHorizontal3 = shadowScaleHorizontal;
        canvas2.scale(shadowScaleHorizontal3, shadowScaleBottom);
        canvas2.rotate(180.0f);
        canvas2.drawPath(r0.mCornerShadowPath, r0.mCornerShadowPaint);
        if (drawHorizontalEdges) {
            canvas2.scale(shadowOffsetHorizontal / shadowScaleHorizontal3, shadowOffsetHorizontal);
            shadowOffsetHorizontal = shadowScaleHorizontal3;
            canvas.drawRect(0.0f, edgeShadowTop, r0.mContentBounds.width() - (shadowOffset * 2.0f), (-r0.mCornerRadius) + r0.mShadowSize, r0.mEdgeShadowPaint);
        } else {
            shadowOffsetHorizontal = shadowScaleHorizontal3;
        }
        canvas2.restoreToCount(saved);
        saved = canvas.save();
        canvas2.translate(r0.mContentBounds.left + shadowOffset, r0.mContentBounds.bottom - shadowOffset);
        canvas2.scale(shadowOffsetHorizontal, shadowScaleBottom);
        canvas2.rotate(270.0f);
        canvas2.drawPath(r0.mCornerShadowPath, r0.mCornerShadowPaint);
        if (drawVerticalEdges) {
            canvas2.scale(1.0f / shadowScaleBottom, 1.0f);
            canvas.drawRect(0.0f, edgeShadowTop, r0.mContentBounds.height() - (shadowOffset * 2.0f), -r0.mCornerRadius, r0.mEdgeShadowPaint);
        }
        canvas2.restoreToCount(saved);
        saved = canvas.save();
        canvas2.translate(r0.mContentBounds.right - shadowOffset, r0.mContentBounds.top + shadowOffset);
        shadowScaleHorizontal3 = shadowScaleTop;
        canvas2.scale(shadowOffsetHorizontal, shadowScaleHorizontal3);
        canvas2.rotate(90.0f);
        canvas2.drawPath(r0.mCornerShadowPath, r0.mCornerShadowPaint);
        if (drawVerticalEdges) {
            canvas2.scale(1.0f / shadowScaleHorizontal3, 1.0f);
            canvas.drawRect(0.0f, edgeShadowTop, r0.mContentBounds.height() - (2.0f * shadowOffset), -r0.mCornerRadius, r0.mEdgeShadowPaint);
        }
        canvas2.restoreToCount(saved);
        canvas2.restoreToCount(rotateSaved);
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
        f2 = -outerBounds.top;
        if (f2 > 0.0f) {
            float startRatio = r0.mCornerRadius / f2;
            float midRatio = startRatio + ((1.0f - startRatio) / 2.0f);
            Shader shader = r8;
            Paint paint = r0.mCornerShadowPaint;
            Shader radialGradient = new RadialGradient(0.0f, 0.0f, f2, new int[]{0, r0.mShadowStartColor, r0.mShadowMiddleColor, r0.mShadowEndColor}, new float[]{0.0f, startRatio, midRatio, 1.0f}, TileMode.CLAMP);
            paint.setShader(shader);
        }
        Paint paint2 = r0.mEdgeShadowPaint;
        float f3 = innerBounds.top;
        paint2.setShader(new LinearGradient(0.0f, f3, 0.0f, outerBounds.top, new int[]{r0.mShadowStartColor, r0.mShadowMiddleColor, r0.mShadowEndColor}, new float[]{0.0f, SHADOW_HORIZ_SCALE, 1.0f}, TileMode.CLAMP));
        r0.mEdgeShadowPaint.setAntiAlias(false);
    }

    private void buildComponents(Rect bounds) {
        float verticalOffset = this.mRawMaxShadowSize * SHADOW_MULTIPLIER;
        this.mContentBounds.set(((float) bounds.left) + this.mRawMaxShadowSize, ((float) bounds.top) + verticalOffset, ((float) bounds.right) - this.mRawMaxShadowSize, ((float) bounds.bottom) - verticalOffset);
        getWrappedDrawable().setBounds((int) this.mContentBounds.left, (int) this.mContentBounds.top, (int) this.mContentBounds.right, (int) this.mContentBounds.bottom);
        buildShadowCorners();
    }

    public float getCornerRadius() {
        return this.mCornerRadius;
    }

    public void setShadowSize(float size) {
        setShadowSize(size, this.mRawMaxShadowSize);
    }

    public void setMaxShadowSize(float size) {
        setShadowSize(this.mRawShadowSize, size);
    }

    public float getShadowSize() {
        return this.mRawShadowSize;
    }

    public float getMaxShadowSize() {
        return this.mRawMaxShadowSize;
    }

    public float getMinWidth() {
        float f = this.mRawMaxShadowSize;
        return (this.mRawMaxShadowSize * 2.0f) + (Math.max(f, this.mCornerRadius + (f / 2.0f)) * 2.0f);
    }

    public float getMinHeight() {
        float f = this.mRawMaxShadowSize;
        return ((this.mRawMaxShadowSize * SHADOW_MULTIPLIER) * 2.0f) + (Math.max(f, this.mCornerRadius + ((f * SHADOW_MULTIPLIER) / 2.0f)) * 2.0f);
    }
}
