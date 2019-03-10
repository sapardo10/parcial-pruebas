package android.support.wearable.watchface.decompositionface;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

@TargetApi(23)
class DigitDrawable extends Drawable {
    private int currentDigit;
    private int digitCount;
    private final Rect fontBounds = new Rect();
    private Drawable fontDrawable;

    DigitDrawable() {
    }

    public void draw(Canvas canvas) {
        if (this.fontDrawable != null) {
            updateFontImageBounds();
            canvas.save();
            canvas.clipRect(getBounds());
            this.fontDrawable.draw(canvas);
            canvas.restore();
        }
    }

    public void setAlpha(int alpha) {
        Drawable drawable = this.fontDrawable;
        if (drawable != null) {
            drawable.setAlpha(alpha);
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
        Drawable drawable = this.fontDrawable;
        if (drawable != null) {
            drawable.setColorFilter(colorFilter);
        }
    }

    public int getOpacity() {
        return -3;
    }

    public void setFontDrawable(Drawable drawable) {
        this.fontDrawable = drawable;
        this.fontDrawable.setAlpha(getAlpha());
        this.fontDrawable.setColorFilter(getColorFilter());
    }

    public void setDigitCount(int digitCount) {
        this.digitCount = digitCount;
    }

    public void setCurrentDigit(int currentDigit) {
        this.currentDigit = currentDigit;
    }

    public int getIntrinsicWidth() {
        Drawable drawable = this.fontDrawable;
        if (drawable == null) {
            return 0;
        }
        return drawable.getIntrinsicWidth();
    }

    public int getIntrinsicHeight() {
        Drawable drawable = this.fontDrawable;
        if (drawable != null) {
            if (this.digitCount != 0) {
                return drawable.getIntrinsicHeight() / this.digitCount;
            }
        }
        return 0;
    }

    private void updateFontImageBounds() {
        if (this.fontDrawable != null) {
            this.fontBounds.set(getBounds().left, getBounds().top - (this.currentDigit * getBounds().height()), getBounds().right, getBounds().bottom + (((this.digitCount - this.currentDigit) - 1) * getBounds().height()));
            this.fontDrawable.setBounds(this.fontBounds);
        }
    }
}
