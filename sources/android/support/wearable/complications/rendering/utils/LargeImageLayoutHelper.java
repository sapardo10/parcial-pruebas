package android.support.wearable.complications.rendering.utils;

import android.graphics.Rect;

public class LargeImageLayoutHelper extends LayoutHelper {
    public void getLargeImageBounds(Rect outRect) {
        getBounds(outRect);
        LayoutUtils.getCentralSquare(outRect, outRect);
    }
}
