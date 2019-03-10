package android.support.wearable.complications.rendering.utils;

import android.graphics.Rect;
import android.support.wearable.complications.ComplicationData;
import android.text.Layout.Alignment;

public class LongTextLayoutHelper extends LayoutHelper {
    private final Rect mBounds = new Rect();

    private boolean shouldShowTextOnly(Rect bounds) {
        ComplicationData data = getComplicationData();
        return (data.getIcon() == null && data.getSmallImage() == null) || !LayoutUtils.isWideRectangle(bounds);
    }

    public void getIconBounds(Rect outRect) {
        ComplicationData data = getComplicationData();
        getBounds(outRect);
        if (data.getIcon() != null && data.getSmallImage() == null) {
            if (!shouldShowTextOnly(outRect)) {
                LayoutUtils.getLeftPart(outRect, outRect);
                return;
            }
        }
        outRect.setEmpty();
    }

    public void getSmallImageBounds(Rect outRect) {
        ComplicationData data = getComplicationData();
        getBounds(outRect);
        if (data.getSmallImage() != null) {
            if (!shouldShowTextOnly(outRect)) {
                LayoutUtils.getLeftPart(outRect, outRect);
                return;
            }
        }
        outRect.setEmpty();
    }

    public void getLongTextBounds(Rect outRect) {
        ComplicationData data = getComplicationData();
        getBounds(outRect);
        if (shouldShowTextOnly(outRect)) {
            if (data.getLongTitle() != null) {
                LayoutUtils.getTopHalf(outRect, outRect);
            }
        } else if (data.getLongTitle() == null) {
            LayoutUtils.getRightPart(outRect, outRect);
        } else {
            LayoutUtils.getRightPart(outRect, outRect);
            LayoutUtils.getTopHalf(outRect, outRect);
        }
    }

    public Alignment getLongTextAlignment() {
        getBounds(this.mBounds);
        if (shouldShowTextOnly(this.mBounds)) {
            return Alignment.ALIGN_CENTER;
        }
        return Alignment.ALIGN_NORMAL;
    }

    public int getLongTextGravity() {
        if (getComplicationData().getLongTitle() == null) {
            return 16;
        }
        return 80;
    }

    public void getLongTitleBounds(Rect outRect) {
        ComplicationData data = getComplicationData();
        getBounds(outRect);
        if (data.getLongTitle() == null) {
            outRect.setEmpty();
        } else if (shouldShowTextOnly(outRect)) {
            LayoutUtils.getBottomHalf(outRect, outRect);
        } else {
            LayoutUtils.getRightPart(outRect, outRect);
            LayoutUtils.getBottomHalf(outRect, outRect);
        }
    }

    public Alignment getLongTitleAlignment() {
        return getLongTextAlignment();
    }

    public int getLongTitleGravity() {
        return 48;
    }
}
