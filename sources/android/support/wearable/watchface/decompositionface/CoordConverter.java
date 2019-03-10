package android.support.wearable.watchface.decompositionface;

import android.graphics.Rect;
import android.graphics.RectF;

public class CoordConverter {
    private final Rect mPixelBounds = new Rect();

    public void setPixelBounds(Rect pixelBounds) {
        this.mPixelBounds.set(pixelBounds);
    }

    public void setPixelBounds(int left, int top, int right, int bottom) {
        this.mPixelBounds.set(left, top, right, bottom);
    }

    public void getPixelRectFromProportional(RectF proportionRect, Rect output) {
        output.set(getPixelX(proportionRect.left), getPixelY(proportionRect.top), getPixelX(proportionRect.right), getPixelY(proportionRect.bottom));
    }

    public int getPixelX(float proportionalX) {
        return Math.round((((float) this.mPixelBounds.width()) * proportionalX) + ((float) this.mPixelBounds.left));
    }

    public int getPixelY(float proportionalY) {
        return Math.round((((float) this.mPixelBounds.height()) * proportionalY) + ((float) this.mPixelBounds.top));
    }
}
