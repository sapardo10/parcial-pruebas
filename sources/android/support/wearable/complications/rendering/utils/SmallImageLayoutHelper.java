package android.support.wearable.complications.rendering.utils;

import android.graphics.Rect;

public class SmallImageLayoutHelper extends LayoutHelper {
    public void getSmallImageBounds(Rect outRect) {
        getBounds(outRect);
        LayoutUtils.getCentralSquare(outRect, outRect);
    }
}
