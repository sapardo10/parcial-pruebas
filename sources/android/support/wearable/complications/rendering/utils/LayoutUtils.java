package android.support.wearable.complications.rendering.utils;

import android.graphics.Rect;

public class LayoutUtils {
    private static final float WIDE_RECTANGLE_MINIMUM_ASPECT_RATIO = 2.0f;

    public static boolean isWideRectangle(Rect bounds) {
        return ((float) bounds.width()) > ((float) bounds.height()) * WIDE_RECTANGLE_MINIMUM_ASPECT_RATIO;
    }

    public static void getLeftPart(Rect outRect, Rect inRect) {
        if (inRect.width() < inRect.height()) {
            outRect.setEmpty();
        } else {
            outRect.set(inRect.left, inRect.top, inRect.left + inRect.height(), inRect.bottom);
        }
    }

    public static void getRightPart(Rect outRect, Rect inRect) {
        if (inRect.width() < inRect.height()) {
            outRect.set(inRect);
        } else {
            outRect.set(inRect.left + inRect.height(), inRect.top, inRect.right, inRect.bottom);
        }
    }

    public static void getTopHalf(Rect outRect, Rect inRect) {
        outRect.set(inRect.left, inRect.top, inRect.right, (inRect.top + inRect.bottom) / 2);
    }

    public static void getBottomHalf(Rect outRect, Rect inRect) {
        outRect.set(inRect.left, (inRect.top + inRect.bottom) / 2, inRect.right, inRect.bottom);
    }

    public static void getCentralSquare(Rect outRect, Rect inRect) {
        int edge = Math.min(inRect.width(), inRect.height());
        outRect.set(inRect.centerX() - (edge / 2), inRect.centerY() - (edge / 2), inRect.centerX() + (edge / 2), inRect.centerY() + (edge / 2));
    }

    public static void scaledAroundCenter(Rect outRect, Rect inRect, float sizeFraction) {
        outRect.set(inRect);
        float paddingFraction = 0.5f - (sizeFraction / WIDE_RECTANGLE_MINIMUM_ASPECT_RATIO);
        outRect.inset((int) (((float) outRect.width()) * paddingFraction), (int) (((float) outRect.height()) * paddingFraction));
    }

    public static void fitSquareToBounds(Rect squareBounds, Rect container) {
        if (!squareBounds.isEmpty()) {
            int originalCenterX = squareBounds.centerX();
            int originalCenterY = squareBounds.centerY();
            if (squareBounds.intersect(container)) {
                getCentralSquare(squareBounds, squareBounds);
                int dx = originalCenterX - squareBounds.centerX();
                int dy = originalCenterY - squareBounds.centerY();
                squareBounds.offset(dx, dy);
                if (!container.contains(squareBounds)) {
                    squareBounds.offset(-dx, -dy);
                }
                return;
            }
            squareBounds.setEmpty();
        }
    }

    public static void getInnerBounds(Rect outRect, Rect inRect, float radius) {
        outRect.set(inRect);
        double sqrt = Math.sqrt(2.0d) - 1.0d;
        double d = (double) radius;
        Double.isNaN(d);
        int padding = (int) Math.ceil(sqrt * d);
        outRect.inset(padding, padding);
    }
}
