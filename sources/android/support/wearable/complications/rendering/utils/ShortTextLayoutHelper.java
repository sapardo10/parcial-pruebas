package android.support.wearable.complications.rendering.utils;

import android.graphics.Rect;
import android.support.wearable.complications.ComplicationData;
import android.text.Layout.Alignment;

public class ShortTextLayoutHelper extends LayoutHelper {
    private final Rect mBounds = new Rect();

    public void getIconBounds(Rect outRect) {
        if (getComplicationData().getIcon() == null) {
            outRect.setEmpty();
            return;
        }
        getBounds(outRect);
        if (LayoutUtils.isWideRectangle(outRect)) {
            LayoutUtils.getLeftPart(outRect, outRect);
            return;
        }
        LayoutUtils.getCentralSquare(outRect, outRect);
        LayoutUtils.getTopHalf(outRect, outRect);
        LayoutUtils.getCentralSquare(outRect, outRect);
    }

    public Alignment getShortTextAlignment() {
        ComplicationData data = getComplicationData();
        getBounds(this.mBounds);
        if (!LayoutUtils.isWideRectangle(this.mBounds) || data.getIcon() == null) {
            return Alignment.ALIGN_CENTER;
        }
        return Alignment.ALIGN_NORMAL;
    }

    public int getShortTextGravity() {
        ComplicationData data = getComplicationData();
        if (data.getShortTitle() == null || data.getIcon() != null) {
            return 16;
        }
        return 80;
    }

    public void getShortTextBounds(Rect outRect) {
        ComplicationData data = getComplicationData();
        getBounds(outRect);
        if (data.getIcon() != null) {
            if (LayoutUtils.isWideRectangle(outRect)) {
                LayoutUtils.getRightPart(outRect, outRect);
                return;
            }
            LayoutUtils.getCentralSquare(outRect, outRect);
            LayoutUtils.getBottomHalf(outRect, outRect);
        } else if (data.getShortTitle() != null) {
            LayoutUtils.getTopHalf(outRect, outRect);
        }
    }

    public Alignment getShortTitleAlignment() {
        return getShortTextAlignment();
    }

    public int getShortTitleGravity() {
        return 48;
    }

    public void getShortTitleBounds(Rect outRect) {
        ComplicationData data = getComplicationData();
        if (data.getIcon() == null) {
            if (data.getShortTitle() != null) {
                getBounds(outRect);
                LayoutUtils.getBottomHalf(outRect, outRect);
                return;
            }
        }
        outRect.setEmpty();
    }
}
