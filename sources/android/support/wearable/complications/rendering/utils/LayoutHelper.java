package android.support.wearable.complications.rendering.utils;

import android.graphics.Rect;
import android.support.wearable.complications.ComplicationData;
import android.text.Layout.Alignment;

public class LayoutHelper {
    private final Rect mBounds = new Rect();
    private ComplicationData mComplicationData;

    public void getBounds(Rect outRect) {
        outRect.set(this.mBounds);
    }

    public void setWidth(int width) {
        this.mBounds.right = width;
    }

    public void setHeight(int height) {
        this.mBounds.bottom = height;
    }

    public void setComplicationData(ComplicationData data) {
        this.mComplicationData = data;
    }

    public void update(int width, int height, ComplicationData data) {
        setWidth(width);
        setHeight(height);
        setComplicationData(data);
    }

    public ComplicationData getComplicationData() {
        return this.mComplicationData;
    }

    public void getIconBounds(Rect outRect) {
        outRect.setEmpty();
    }

    public void getSmallImageBounds(Rect outRect) {
        outRect.setEmpty();
    }

    public void getLargeImageBounds(Rect outRect) {
        outRect.setEmpty();
    }

    public void getRangedValueBounds(Rect outRect) {
        outRect.setEmpty();
    }

    public void getShortTextBounds(Rect outRect) {
        outRect.setEmpty();
    }

    public Alignment getShortTextAlignment() {
        return Alignment.ALIGN_CENTER;
    }

    public int getShortTextGravity() {
        return 17;
    }

    public void getShortTitleBounds(Rect outRect) {
        outRect.setEmpty();
    }

    public Alignment getShortTitleAlignment() {
        return Alignment.ALIGN_CENTER;
    }

    public int getShortTitleGravity() {
        return 17;
    }

    public void getLongTextBounds(Rect outRect) {
        outRect.setEmpty();
    }

    public Alignment getLongTextAlignment() {
        return Alignment.ALIGN_CENTER;
    }

    public int getLongTextGravity() {
        return 17;
    }

    public void getLongTitleBounds(Rect outRect) {
        outRect.setEmpty();
    }

    public Alignment getLongTitleAlignment() {
        return Alignment.ALIGN_CENTER;
    }

    public int getLongTitleGravity() {
        return 17;
    }
}
