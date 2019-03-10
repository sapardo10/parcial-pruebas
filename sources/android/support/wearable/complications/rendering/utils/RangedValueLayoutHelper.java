package android.support.wearable.complications.rendering.utils;

import android.graphics.Rect;
import android.support.wearable.complications.ComplicationData;
import android.text.Layout.Alignment;

public class RangedValueLayoutHelper extends LayoutHelper {
    private static final float ICON_SIZE_FRACTION = 0.8f;
    private static final float INNER_SQUARE_SIZE_FRACTION = ((float) (1.0d / Math.sqrt(2.0d)));
    private static final float RANGED_VALUE_SIZE_FRACTION = 0.95f;
    private final Rect mBounds = new Rect();
    private final Rect mRangedValueInnerSquare = new Rect();
    private final ShortTextLayoutHelper mShortTextLayoutHelper = new ShortTextLayoutHelper();

    private void updateShortTextLayoutHelper() {
        if (getComplicationData() != null) {
            getRangedValueBounds(this.mRangedValueInnerSquare);
            Rect rect = this.mRangedValueInnerSquare;
            LayoutUtils.scaledAroundCenter(rect, rect, INNER_SQUARE_SIZE_FRACTION);
            this.mShortTextLayoutHelper.update(this.mRangedValueInnerSquare.width(), this.mRangedValueInnerSquare.height(), getComplicationData());
        }
    }

    public void setWidth(int width) {
        super.setWidth(width);
        updateShortTextLayoutHelper();
    }

    public void setHeight(int height) {
        super.setHeight(height);
        updateShortTextLayoutHelper();
    }

    public void setComplicationData(ComplicationData data) {
        super.setComplicationData(data);
        updateShortTextLayoutHelper();
    }

    public void getRangedValueBounds(Rect outRect) {
        getBounds(outRect);
        if (getComplicationData().getShortText() != null) {
            if (LayoutUtils.isWideRectangle(outRect)) {
                LayoutUtils.getLeftPart(outRect, outRect);
                LayoutUtils.scaledAroundCenter(outRect, outRect, RANGED_VALUE_SIZE_FRACTION);
                return;
            }
        }
        LayoutUtils.getCentralSquare(outRect, outRect);
        LayoutUtils.scaledAroundCenter(outRect, outRect, RANGED_VALUE_SIZE_FRACTION);
    }

    public void getIconBounds(Rect outRect) {
        ComplicationData data = getComplicationData();
        if (data.getIcon() == null) {
            outRect.setEmpty();
            return;
        }
        getBounds(outRect);
        if (data.getShortText() != null) {
            if (!LayoutUtils.isWideRectangle(outRect)) {
                this.mShortTextLayoutHelper.getIconBounds(outRect);
                outRect.offset(this.mRangedValueInnerSquare.left, this.mRangedValueInnerSquare.top);
                return;
            }
        }
        LayoutUtils.scaledAroundCenter(outRect, this.mRangedValueInnerSquare, ICON_SIZE_FRACTION);
    }

    public Alignment getShortTextAlignment() {
        getBounds(this.mBounds);
        if (LayoutUtils.isWideRectangle(this.mBounds)) {
            return Alignment.ALIGN_NORMAL;
        }
        return this.mShortTextLayoutHelper.getShortTextAlignment();
    }

    public int getShortTextGravity() {
        ComplicationData data = getComplicationData();
        getBounds(this.mBounds);
        if (!LayoutUtils.isWideRectangle(this.mBounds)) {
            return this.mShortTextLayoutHelper.getShortTextGravity();
        }
        if (data.getShortTitle() != null) {
            return 80;
        }
        return 16;
    }

    public void getShortTextBounds(Rect outRect) {
        ComplicationData data = getComplicationData();
        if (data.getShortText() == null) {
            outRect.setEmpty();
            return;
        }
        getBounds(outRect);
        if (LayoutUtils.isWideRectangle(outRect)) {
            if (data.getShortTitle() != null) {
                if (data.getIcon() == null) {
                    LayoutUtils.getRightPart(outRect, outRect);
                    LayoutUtils.getTopHalf(outRect, outRect);
                    return;
                }
            }
            LayoutUtils.getRightPart(outRect, outRect);
            return;
        }
        this.mShortTextLayoutHelper.getShortTextBounds(outRect);
        outRect.offset(this.mRangedValueInnerSquare.left, this.mRangedValueInnerSquare.top);
    }

    public Alignment getShortTitleAlignment() {
        return getShortTextAlignment();
    }

    public int getShortTitleGravity() {
        return 48;
    }

    public void getShortTitleBounds(Rect outRect) {
        ComplicationData data = getComplicationData();
        if (data.getShortTitle() != null) {
            if (data.getShortText() != null) {
                getBounds(outRect);
                if (LayoutUtils.isWideRectangle(outRect)) {
                    LayoutUtils.getRightPart(outRect, outRect);
                    LayoutUtils.getBottomHalf(outRect, outRect);
                    return;
                }
                this.mShortTextLayoutHelper.getShortTitleBounds(outRect);
                outRect.offset(this.mRangedValueInnerSquare.left, this.mRangedValueInnerSquare.top);
                return;
            }
        }
        outRect.setEmpty();
    }
}
