package android.support.wearable.complications.rendering;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.support.annotation.VisibleForTesting;
import java.util.Objects;

@TargetApi(24)
class RoundedDrawable extends Drawable {
    private Drawable mDrawable;
    @VisibleForTesting
    final Paint mPaint = new Paint();
    private int mRadius;
    private final RectF mTmpBounds = new RectF();

    public RoundedDrawable() {
        this.mPaint.setAntiAlias(true);
    }

    public void setDrawable(Drawable drawable) {
        if (!Objects.equals(this.mDrawable, drawable)) {
            this.mDrawable = drawable;
            updateBitmapShader();
        }
    }

    protected void onBoundsChange(Rect bounds) {
        this.mTmpBounds.right = (float) bounds.width();
        this.mTmpBounds.bottom = (float) bounds.height();
        updateBitmapShader();
    }

    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (this.mDrawable != null) {
            if (!bounds.isEmpty()) {
                canvas.save();
                canvas.translate((float) bounds.left, (float) bounds.top);
                RectF rectF = this.mTmpBounds;
                int i = this.mRadius;
                canvas.drawRoundRect(rectF, (float) i, (float) i, this.mPaint);
                canvas.restore();
            }
        }
    }

    public int getOpacity() {
        return -3;
    }

    public void setAlpha(int alpha) {
        this.mPaint.setAlpha(alpha);
    }

    public void setColorFilter(ColorFilter cf) {
        this.mPaint.setColorFilter(cf);
    }

    public void setRadius(int radius) {
        this.mRadius = radius;
    }

    private void updateBitmapShader() {
        if (this.mDrawable != null) {
            Rect bounds = getBounds();
            if (!bounds.isEmpty()) {
                this.mPaint.setShader(new BitmapShader(drawableToBitmap(this.mDrawable, bounds.width(), bounds.height()), TileMode.CLAMP, TileMode.CLAMP));
            }
        }
    }

    private Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    @VisibleForTesting
    Drawable getDrawable() {
        return this.mDrawable;
    }
}
