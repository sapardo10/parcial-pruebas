package android.support.wearable.complications.rendering;

import android.graphics.ColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;

class ComplicationStyle {
    private static final int BACKGROUND_COLOR_DEFAULT = -16777216;
    private static final int BORDER_COLOR_DEFAULT = -1;
    private static final int BORDER_RADIUS_DEFAULT = Integer.MAX_VALUE;
    static final int BORDER_STYLE_DASHED = 2;
    static final int BORDER_STYLE_NONE = 0;
    static final int BORDER_STYLE_SOLID = 1;
    private static final int BORDER_WIDTH_DEFAULT = 1;
    private static final int DASH_GAP_DEFAULT = 3;
    private static final int DASH_WIDTH_DEFAULT = 3;
    private static final int HIGHLIGHT_COLOR_DEFAULT = -3355444;
    private static final int PRIMARY_COLOR_DEFAULT = -1;
    private static final int RING_WIDTH_DEFAULT = 2;
    private static final int SECONDARY_COLOR_DEFAULT = -3355444;
    private static final int TEXT_SIZE_DEFAULT = Integer.MAX_VALUE;
    private static final Typeface TYPEFACE_DEFAULT = Typeface.create("sans-serif-condensed", 0);
    private final int mBackgroundColor;
    private final Drawable mBackgroundDrawable;
    private final int mBorderColor;
    private final int mBorderDashGap;
    private final int mBorderDashWidth;
    private final int mBorderRadius;
    private final int mBorderStyle;
    private final int mBorderWidth;
    private final ColorFilter mColorFilter;
    private final int mHighlightColor;
    private final int mIconColor;
    private final int mRangedValuePrimaryColor;
    private final int mRangedValueRingWidth;
    private final int mRangedValueSecondaryColor;
    private final int mTextColor;
    private final int mTextSize;
    private final Typeface mTextTypeface;
    private final int mTitleColor;
    private final int mTitleSize;
    private final Typeface mTitleTypeface;

    public static class Builder implements Parcelable {
        public static final Creator<Builder> CREATOR = new C04221();
        private static final String FIELD_BACKGROUND_COLOR = "background_color";
        private static final String FIELD_BORDER_COLOR = "border_color";
        private static final String FIELD_BORDER_DASH_GAP = "border_dash_gap";
        private static final String FIELD_BORDER_DASH_WIDTH = "border_dash_width";
        private static final String FIELD_BORDER_RADIUS = "border_radius";
        private static final String FIELD_BORDER_STYLE = "border_style";
        private static final String FIELD_BORDER_WIDTH = "border_width";
        private static final String FIELD_HIGHLIGHT_COLOR = "highlight_color";
        private static final String FIELD_ICON_COLOR = "icon_color";
        private static final String FIELD_RANGED_VALUE_PRIMARY_COLOR = "ranged_value_primary_color";
        private static final String FIELD_RANGED_VALUE_RING_WIDTH = "ranged_value_ring_width";
        private static final String FIELD_RANGED_VALUE_SECONDARY_COLOR = "ranged_value_secondary_color";
        private static final String FIELD_TEXT_COLOR = "text_color";
        private static final String FIELD_TEXT_SIZE = "text_size";
        private static final String FIELD_TEXT_STYLE = "text_style";
        private static final String FIELD_TITLE_COLOR = "title_color";
        private static final String FIELD_TITLE_SIZE = "title_size";
        private static final String FIELD_TITLE_STYLE = "title_style";
        private int backgroundColor;
        private Drawable backgroundDrawable;
        private int borderColor;
        private int borderDashGap;
        private int borderDashWidth;
        private int borderRadius;
        private int borderStyle;
        private int borderWidth;
        private ColorFilter colorFilter;
        private int highlightColor;
        private int iconColor;
        private int rangedValuePrimaryColor;
        private int rangedValueRingWidth;
        private int rangedValueSecondaryColor;
        private int textColor;
        private int textSize;
        private Typeface textTypeface;
        private int titleColor;
        private int titleSize;
        private Typeface titleTypeface;

        /* renamed from: android.support.wearable.complications.rendering.ComplicationStyle$Builder$1 */
        class C04221 implements Creator<Builder> {
            C04221() {
            }

            public Builder createFromParcel(Parcel source) {
                return new Builder(source);
            }

            public Builder[] newArray(int size) {
                return new Builder[size];
            }
        }

        public Builder() {
            this.backgroundColor = -16777216;
            this.backgroundDrawable = null;
            this.textColor = -1;
            this.titleColor = -3355444;
            this.textTypeface = ComplicationStyle.TYPEFACE_DEFAULT;
            this.titleTypeface = ComplicationStyle.TYPEFACE_DEFAULT;
            this.textSize = Integer.MAX_VALUE;
            this.titleSize = Integer.MAX_VALUE;
            this.colorFilter = null;
            this.iconColor = -1;
            this.borderColor = -1;
            this.borderStyle = 1;
            this.borderDashWidth = 3;
            this.borderDashGap = 3;
            this.borderRadius = Integer.MAX_VALUE;
            this.borderWidth = 1;
            this.rangedValueRingWidth = 2;
            this.rangedValuePrimaryColor = -1;
            this.rangedValueSecondaryColor = -3355444;
            this.highlightColor = -3355444;
        }

        public Builder(Builder builder) {
            this.backgroundColor = -16777216;
            this.backgroundDrawable = null;
            this.textColor = -1;
            this.titleColor = -3355444;
            this.textTypeface = ComplicationStyle.TYPEFACE_DEFAULT;
            this.titleTypeface = ComplicationStyle.TYPEFACE_DEFAULT;
            this.textSize = Integer.MAX_VALUE;
            this.titleSize = Integer.MAX_VALUE;
            this.colorFilter = null;
            this.iconColor = -1;
            this.borderColor = -1;
            this.borderStyle = 1;
            this.borderDashWidth = 3;
            this.borderDashGap = 3;
            this.borderRadius = Integer.MAX_VALUE;
            this.borderWidth = 1;
            this.rangedValueRingWidth = 2;
            this.rangedValuePrimaryColor = -1;
            this.rangedValueSecondaryColor = -3355444;
            this.highlightColor = -3355444;
            this.backgroundColor = builder.backgroundColor;
            this.backgroundDrawable = builder.backgroundDrawable;
            this.textColor = builder.textColor;
            this.titleColor = builder.titleColor;
            this.textTypeface = builder.textTypeface;
            this.titleTypeface = builder.titleTypeface;
            this.textSize = builder.textSize;
            this.titleSize = builder.titleSize;
            this.colorFilter = builder.colorFilter;
            this.iconColor = builder.iconColor;
            this.borderColor = builder.borderColor;
            this.borderStyle = builder.borderStyle;
            this.borderDashWidth = builder.borderDashWidth;
            this.borderDashGap = builder.borderDashGap;
            this.borderRadius = builder.borderRadius;
            this.borderWidth = builder.borderWidth;
            this.rangedValueRingWidth = builder.rangedValueRingWidth;
            this.rangedValuePrimaryColor = builder.rangedValuePrimaryColor;
            this.rangedValueSecondaryColor = builder.rangedValueSecondaryColor;
            this.highlightColor = builder.highlightColor;
        }

        private Builder(Parcel in) {
            this.backgroundColor = -16777216;
            this.backgroundDrawable = null;
            this.textColor = -1;
            this.titleColor = -3355444;
            this.textTypeface = ComplicationStyle.TYPEFACE_DEFAULT;
            this.titleTypeface = ComplicationStyle.TYPEFACE_DEFAULT;
            this.textSize = Integer.MAX_VALUE;
            this.titleSize = Integer.MAX_VALUE;
            this.colorFilter = null;
            this.iconColor = -1;
            this.borderColor = -1;
            this.borderStyle = 1;
            this.borderDashWidth = 3;
            this.borderDashGap = 3;
            this.borderRadius = Integer.MAX_VALUE;
            this.borderWidth = 1;
            this.rangedValueRingWidth = 2;
            this.rangedValuePrimaryColor = -1;
            this.rangedValueSecondaryColor = -3355444;
            this.highlightColor = -3355444;
            Bundle bundle = in.readBundle(getClass().getClassLoader());
            this.backgroundColor = bundle.getInt(FIELD_BACKGROUND_COLOR);
            this.textColor = bundle.getInt(FIELD_TEXT_COLOR);
            this.titleColor = bundle.getInt(FIELD_TITLE_COLOR);
            this.textTypeface = Typeface.defaultFromStyle(bundle.getInt(FIELD_TEXT_STYLE, 0));
            this.titleTypeface = Typeface.defaultFromStyle(bundle.getInt(FIELD_TITLE_STYLE, 0));
            this.textSize = bundle.getInt(FIELD_TEXT_SIZE);
            this.titleSize = bundle.getInt(FIELD_TITLE_SIZE);
            this.iconColor = bundle.getInt(FIELD_ICON_COLOR);
            this.borderColor = bundle.getInt(FIELD_BORDER_COLOR);
            this.borderStyle = bundle.getInt(FIELD_BORDER_STYLE);
            this.borderDashWidth = bundle.getInt(FIELD_BORDER_DASH_WIDTH);
            this.borderDashGap = bundle.getInt(FIELD_BORDER_DASH_GAP);
            this.borderRadius = bundle.getInt(FIELD_BORDER_RADIUS);
            this.borderWidth = bundle.getInt(FIELD_BORDER_WIDTH);
            this.rangedValueRingWidth = bundle.getInt(FIELD_RANGED_VALUE_RING_WIDTH);
            this.rangedValuePrimaryColor = bundle.getInt(FIELD_RANGED_VALUE_PRIMARY_COLOR);
            this.rangedValueSecondaryColor = bundle.getInt(FIELD_RANGED_VALUE_SECONDARY_COLOR);
            this.highlightColor = bundle.getInt(FIELD_HIGHLIGHT_COLOR);
        }

        public void writeToParcel(Parcel dest, int flags) {
            Bundle bundle = new Bundle();
            bundle.putInt(FIELD_BACKGROUND_COLOR, this.backgroundColor);
            bundle.putInt(FIELD_TEXT_COLOR, this.textColor);
            bundle.putInt(FIELD_TITLE_COLOR, this.titleColor);
            bundle.putInt(FIELD_TEXT_STYLE, this.textTypeface.getStyle());
            bundle.putInt(FIELD_TITLE_STYLE, this.titleTypeface.getStyle());
            bundle.putInt(FIELD_TEXT_SIZE, this.textSize);
            bundle.putInt(FIELD_TITLE_SIZE, this.titleSize);
            bundle.putInt(FIELD_ICON_COLOR, this.iconColor);
            bundle.putInt(FIELD_BORDER_COLOR, this.borderColor);
            bundle.putInt(FIELD_BORDER_STYLE, this.borderStyle);
            bundle.putInt(FIELD_BORDER_DASH_WIDTH, this.borderDashWidth);
            bundle.putInt(FIELD_BORDER_DASH_GAP, this.borderDashGap);
            bundle.putInt(FIELD_BORDER_RADIUS, this.borderRadius);
            bundle.putInt(FIELD_BORDER_WIDTH, this.borderWidth);
            bundle.putInt(FIELD_RANGED_VALUE_RING_WIDTH, this.rangedValueRingWidth);
            bundle.putInt(FIELD_RANGED_VALUE_PRIMARY_COLOR, this.rangedValuePrimaryColor);
            bundle.putInt(FIELD_RANGED_VALUE_SECONDARY_COLOR, this.rangedValueSecondaryColor);
            bundle.putInt(FIELD_HIGHLIGHT_COLOR, this.highlightColor);
            dest.writeBundle(bundle);
        }

        public int describeContents() {
            return 0;
        }

        public Builder setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder setBackgroundDrawable(Drawable backgroundDrawable) {
            this.backgroundDrawable = backgroundDrawable;
            return this;
        }

        public Builder setTextColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder setTitleColor(int titleColor) {
            this.titleColor = titleColor;
            return this;
        }

        public Builder setColorFilter(ColorFilter colorFilter) {
            this.colorFilter = colorFilter;
            return this;
        }

        public Builder setIconColor(int iconColor) {
            this.iconColor = iconColor;
            return this;
        }

        public Builder setTextTypeface(Typeface textTypeface) {
            this.textTypeface = textTypeface;
            return this;
        }

        public Builder setTitleTypeface(Typeface titleTypeface) {
            this.titleTypeface = titleTypeface;
            return this;
        }

        public Builder setTextSize(int textSize) {
            this.textSize = textSize;
            return this;
        }

        public Builder setTitleSize(int titleSize) {
            this.titleSize = titleSize;
            return this;
        }

        public Builder setBorderColor(int borderColor) {
            this.borderColor = borderColor;
            return this;
        }

        public Builder setBorderStyle(int borderStyle) {
            if (borderStyle == 1) {
                this.borderStyle = 1;
            } else if (borderStyle == 2) {
                this.borderStyle = 2;
            } else {
                this.borderStyle = 0;
            }
            return this;
        }

        public Builder setBorderDashWidth(int borderDashWidth) {
            this.borderDashWidth = borderDashWidth;
            return this;
        }

        public Builder setBorderDashGap(int borderDashGap) {
            this.borderDashGap = borderDashGap;
            return this;
        }

        public Builder setBorderRadius(int borderRadius) {
            this.borderRadius = borderRadius;
            return this;
        }

        public Builder setBorderWidth(int borderWidth) {
            this.borderWidth = borderWidth;
            return this;
        }

        public Builder setRangedValueRingWidth(int rangedValueRingWidth) {
            this.rangedValueRingWidth = rangedValueRingWidth;
            return this;
        }

        public Builder setRangedValuePrimaryColor(int rangedValuePrimaryColor) {
            this.rangedValuePrimaryColor = rangedValuePrimaryColor;
            return this;
        }

        public Builder setRangedValueSecondaryColor(int rangedValueSecondaryColor) {
            this.rangedValueSecondaryColor = rangedValueSecondaryColor;
            return this;
        }

        public Builder setHighlightColor(int highlightColor) {
            this.highlightColor = highlightColor;
            return this;
        }

        public ComplicationStyle build() {
            return new ComplicationStyle(this.backgroundColor, this.backgroundDrawable, this.textColor, this.titleColor, this.textTypeface, this.titleTypeface, this.textSize, this.titleSize, this.colorFilter, this.iconColor, this.borderColor, this.borderStyle, this.borderRadius, this.borderWidth, this.borderDashWidth, this.borderDashGap, this.rangedValueRingWidth, this.rangedValuePrimaryColor, this.rangedValueSecondaryColor, this.highlightColor);
        }
    }

    private ComplicationStyle(int backgroundColor, Drawable backgroundDrawable, int textColor, int titleColor, Typeface textTypeface, Typeface titleTypeface, int textSize, int titleSize, ColorFilter colorFilter, int iconColor, int borderColor, int borderStyle, int borderRadius, int borderWidth, int dashWidth, int dashGap, int ringWidth, int rangedPrimaryColor, int rangedSecondaryColor, int highlightColor) {
        this.mBackgroundColor = backgroundColor;
        this.mBackgroundDrawable = backgroundDrawable;
        this.mTextColor = textColor;
        this.mTitleColor = titleColor;
        this.mTextTypeface = textTypeface;
        this.mTitleTypeface = titleTypeface;
        this.mTextSize = textSize;
        this.mTitleSize = titleSize;
        this.mColorFilter = colorFilter;
        this.mIconColor = iconColor;
        this.mBorderColor = borderColor;
        this.mBorderStyle = borderStyle;
        this.mBorderDashWidth = dashWidth;
        this.mBorderDashGap = dashGap;
        this.mBorderRadius = borderRadius;
        this.mBorderWidth = borderWidth;
        this.mRangedValueRingWidth = ringWidth;
        this.mRangedValuePrimaryColor = rangedPrimaryColor;
        this.mRangedValueSecondaryColor = rangedSecondaryColor;
        this.mHighlightColor = highlightColor;
    }

    public int getBackgroundColor() {
        return this.mBackgroundColor;
    }

    @Nullable
    public Drawable getBackgroundDrawable() {
        return this.mBackgroundDrawable;
    }

    public int getTextColor() {
        return this.mTextColor;
    }

    public int getTitleColor() {
        return this.mTitleColor;
    }

    @Nullable
    public ColorFilter getColorFilter() {
        return this.mColorFilter;
    }

    public int getIconColor() {
        return this.mIconColor;
    }

    public Typeface getTextTypeface() {
        return this.mTextTypeface;
    }

    public Typeface getTitleTypeface() {
        return this.mTitleTypeface;
    }

    public int getTextSize() {
        return this.mTextSize;
    }

    public int getTitleSize() {
        return this.mTitleSize;
    }

    public int getBorderColor() {
        return this.mBorderColor;
    }

    public int getBorderStyle() {
        return this.mBorderStyle;
    }

    public int getBorderDashWidth() {
        return this.mBorderDashWidth;
    }

    public int getBorderDashGap() {
        return this.mBorderDashGap;
    }

    public int getBorderRadius() {
        return this.mBorderRadius;
    }

    public int getBorderWidth() {
        return this.mBorderWidth;
    }

    public int getRangedValueRingWidth() {
        return this.mRangedValueRingWidth;
    }

    public int getRangedValuePrimaryColor() {
        return this.mRangedValuePrimaryColor;
    }

    public int getRangedValueSecondaryColor() {
        return this.mRangedValueSecondaryColor;
    }

    public int getHighlightColor() {
        return this.mHighlightColor;
    }
}
