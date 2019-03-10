package android.support.wearable.complications.rendering.utils;

import android.graphics.Rect;

public class IconLayoutHelper extends LayoutHelper {
    public void getIconBounds(Rect outRect) {
        getBounds(outRect);
        LayoutUtils.getCentralSquare(outRect, outRect);
    }
}
