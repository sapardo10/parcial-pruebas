package com.joanzapata.iconify;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.text.TextPaint;
import android.util.TypedValue;
import com.joanzapata.iconify.internal.IconFontDescriptorWrapper;

public class IconDrawable extends Drawable {
    public static final int ANDROID_ACTIONBAR_ICON_SIZE_DP = 24;
    private int alpha = 255;
    private Context context;
    private Icon icon;
    private TextPaint paint;
    private int size = -1;

    public IconDrawable(Context context, String iconKey) {
        Icon icon = Iconify.findIconForKey(iconKey);
        if (icon != null) {
            init(context, icon);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("No icon with that key \"");
        stringBuilder.append(iconKey);
        stringBuilder.append("\".");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public IconDrawable(Context context, Icon icon) {
        init(context, icon);
    }

    private void init(Context context, Icon icon) {
        this.context = context;
        this.icon = icon;
        this.paint = new TextPaint();
        IconFontDescriptorWrapper descriptor = Iconify.findTypefaceOf(icon);
        if (descriptor != null) {
            this.paint.setTypeface(descriptor.getTypeface(context));
            this.paint.setStyle(Style.FILL);
            this.paint.setTextAlign(Align.CENTER);
            this.paint.setUnderlineText(false);
            this.paint.setColor(ViewCompat.MEASURED_STATE_MASK);
            this.paint.setAntiAlias(true);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unable to find the module associated with icon ");
        stringBuilder.append(icon.key());
        stringBuilder.append(", have you registered the module ");
        stringBuilder.append("you are trying to use with Iconify.with(...) in your Application?");
        throw new IllegalStateException(stringBuilder.toString());
    }

    public IconDrawable actionBarSize() {
        return sizeDp(24);
    }

    public IconDrawable sizeRes(int dimenRes) {
        return sizePx(this.context.getResources().getDimensionPixelSize(dimenRes));
    }

    public IconDrawable sizeDp(int size) {
        return sizePx(convertDpToPx(this.context, (float) size));
    }

    public IconDrawable sizePx(int size) {
        this.size = size;
        setBounds(0, 0, size, size);
        invalidateSelf();
        return this;
    }

    public IconDrawable color(int color) {
        this.paint.setColor(color);
        invalidateSelf();
        return this;
    }

    public IconDrawable colorRes(int colorRes) {
        this.paint.setColor(this.context.getResources().getColor(colorRes));
        invalidateSelf();
        return this;
    }

    public IconDrawable alpha(int alpha) {
        setAlpha(alpha);
        invalidateSelf();
        return this;
    }

    public int getIntrinsicHeight() {
        return this.size;
    }

    public int getIntrinsicWidth() {
        return this.size;
    }

    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        int height = bounds.height();
        this.paint.setTextSize((float) height);
        Rect textBounds = new Rect();
        String textValue = String.valueOf(this.icon.character());
        this.paint.getTextBounds(textValue, 0, 1, textBounds);
        int textHeight = textBounds.height();
        canvas.drawText(textValue, bounds.exactCenterX(), ((((float) bounds.top) + (((float) (height - textHeight)) / 2.0f)) + ((float) textHeight)) - ((float) textBounds.bottom), this.paint);
    }

    public boolean isStateful() {
        return true;
    }

    public boolean setState(int[] stateSet) {
        int oldValue = this.paint.getAlpha();
        int newValue = isEnabled(stateSet) ? this.alpha : this.alpha / 2;
        this.paint.setAlpha(newValue);
        return oldValue != newValue;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
        this.paint.setAlpha(alpha);
    }

    public void setColorFilter(ColorFilter cf) {
        this.paint.setColorFilter(cf);
    }

    public void clearColorFilter() {
        this.paint.setColorFilter(null);
    }

    public int getOpacity() {
        return this.alpha;
    }

    public void setStyle(Style style) {
        this.paint.setStyle(style);
    }

    private boolean isEnabled(int[] stateSet) {
        for (int state : stateSet) {
            if (state == 16842910) {
                return true;
            }
        }
        return false;
    }

    private int convertDpToPx(Context context, float dp) {
        return (int) TypedValue.applyDimension(1, dp, context.getResources().getDisplayMetrics());
    }
}
