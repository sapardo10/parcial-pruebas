package com.google.android.exoplayer2.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.accessibility.CaptioningManager;
import com.google.android.exoplayer2.text.CaptionStyleCompat;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.List;

public final class SubtitleView extends View implements TextOutput {
    public static final float DEFAULT_BOTTOM_PADDING_FRACTION = 0.08f;
    public static final float DEFAULT_TEXT_SIZE_FRACTION = 0.0533f;
    private boolean applyEmbeddedFontSizes;
    private boolean applyEmbeddedStyles;
    private float bottomPaddingFraction;
    private List<Cue> cues;
    private final List<SubtitlePainter> painters;
    private CaptionStyleCompat style;
    private float textSize;
    private int textSizeType;

    public SubtitleView(Context context) {
        this(context, null);
    }

    public SubtitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.painters = new ArrayList();
        this.textSizeType = 0;
        this.textSize = 0.0533f;
        this.applyEmbeddedStyles = true;
        this.applyEmbeddedFontSizes = true;
        this.style = CaptionStyleCompat.DEFAULT;
        this.bottomPaddingFraction = 0.08f;
    }

    public void onCues(List<Cue> cues) {
        setCues(cues);
    }

    public void setCues(@Nullable List<Cue> cues) {
        if (this.cues != cues) {
            this.cues = cues;
            int cueCount = cues == null ? 0 : cues.size();
            while (this.painters.size() < cueCount) {
                this.painters.add(new SubtitlePainter(getContext()));
            }
            invalidate();
        }
    }

    public void setFixedTextSize(int unit, float size) {
        Resources resources;
        Context context = getContext();
        if (context == null) {
            resources = Resources.getSystem();
        } else {
            resources = context.getResources();
        }
        setTextSize(2, TypedValue.applyDimension(unit, size, resources.getDisplayMetrics()));
    }

    public void setUserDefaultTextSize() {
        float fontScale = (Util.SDK_INT < 19 || isInEditMode()) ? 1.0f : getUserCaptionFontScaleV19();
        setFractionalTextSize(0.0533f * fontScale);
    }

    public void setFractionalTextSize(float fractionOfHeight) {
        setFractionalTextSize(fractionOfHeight, false);
    }

    public void setFractionalTextSize(float fractionOfHeight, boolean ignorePadding) {
        setTextSize(ignorePadding, fractionOfHeight);
    }

    private void setTextSize(int textSizeType, float textSize) {
        if (this.textSizeType != textSizeType || this.textSize != textSize) {
            this.textSizeType = textSizeType;
            this.textSize = textSize;
            invalidate();
        }
    }

    public void setApplyEmbeddedStyles(boolean applyEmbeddedStyles) {
        if (this.applyEmbeddedStyles != applyEmbeddedStyles || this.applyEmbeddedFontSizes != applyEmbeddedStyles) {
            this.applyEmbeddedStyles = applyEmbeddedStyles;
            this.applyEmbeddedFontSizes = applyEmbeddedStyles;
            invalidate();
        }
    }

    public void setApplyEmbeddedFontSizes(boolean applyEmbeddedFontSizes) {
        if (this.applyEmbeddedFontSizes != applyEmbeddedFontSizes) {
            this.applyEmbeddedFontSizes = applyEmbeddedFontSizes;
            invalidate();
        }
    }

    public void setUserDefaultStyle() {
        CaptionStyleCompat userCaptionStyleV19;
        if (Util.SDK_INT >= 19) {
            if (isCaptionManagerEnabled() && !isInEditMode()) {
                userCaptionStyleV19 = getUserCaptionStyleV19();
                setStyle(userCaptionStyleV19);
            }
        }
        userCaptionStyleV19 = CaptionStyleCompat.DEFAULT;
        setStyle(userCaptionStyleV19);
    }

    public void setStyle(CaptionStyleCompat style) {
        if (this.style != style) {
            this.style = style;
            invalidate();
        }
    }

    public void setBottomPaddingFraction(float bottomPaddingFraction) {
        if (this.bottomPaddingFraction != bottomPaddingFraction) {
            this.bottomPaddingFraction = bottomPaddingFraction;
            invalidate();
        }
    }

    public void dispatchDraw(Canvas canvas) {
        int cueCount = this.cues;
        cueCount = cueCount == 0 ? 0 : cueCount.size();
        int rawViewHeight = getHeight();
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getWidth() - getPaddingRight();
        int bottom = rawViewHeight - getPaddingBottom();
        int i;
        int i2;
        int i3;
        if (bottom <= top) {
            i = right;
            i2 = top;
            i3 = left;
        } else if (right <= left) {
            r22 = bottom;
            i = right;
            i2 = top;
            i3 = left;
        } else {
            int viewHeightMinusPadding = bottom - top;
            float defaultViewTextSizePx = resolveTextSize(r0.textSizeType, r0.textSize, rawViewHeight, viewHeightMinusPadding);
            if (defaultViewTextSizePx > 0.0f) {
                int viewHeightMinusPadding2;
                int i4 = 0;
                while (i4 < cueCount) {
                    Cue cue = (Cue) r0.cues.get(i4);
                    float cueTextSizePx = resolveCueTextSize(cue, rawViewHeight, viewHeightMinusPadding);
                    int i5 = i4;
                    viewHeightMinusPadding2 = viewHeightMinusPadding;
                    r22 = bottom;
                    i = right;
                    i2 = top;
                    i3 = left;
                    ((SubtitlePainter) r0.painters.get(i4)).draw(cue, r0.applyEmbeddedStyles, r0.applyEmbeddedFontSizes, r0.style, defaultViewTextSizePx, cueTextSizePx, r0.bottomPaddingFraction, canvas, left, top, i, r22);
                    i4 = i5 + 1;
                    viewHeightMinusPadding = viewHeightMinusPadding2;
                    bottom = r22;
                    right = i;
                    top = i2;
                    left = i3;
                }
                viewHeightMinusPadding2 = viewHeightMinusPadding;
                r22 = bottom;
                i = right;
                i2 = top;
                i3 = left;
            }
        }
    }

    private float resolveCueTextSize(Cue cue, int rawViewHeight, int viewHeightMinusPadding) {
        if (cue.textSizeType != Integer.MIN_VALUE) {
            if (cue.textSize != Float.MIN_VALUE) {
                return Math.max(resolveTextSize(cue.textSizeType, cue.textSize, rawViewHeight, viewHeightMinusPadding), 0.0f);
            }
        }
        return 0.0f;
    }

    private float resolveTextSize(int textSizeType, float textSize, int rawViewHeight, int viewHeightMinusPadding) {
        switch (textSizeType) {
            case 0:
                return ((float) viewHeightMinusPadding) * textSize;
            case 1:
                return ((float) rawViewHeight) * textSize;
            case 2:
                return textSize;
            default:
                return Float.MIN_VALUE;
        }
    }

    @TargetApi(19)
    private boolean isCaptionManagerEnabled() {
        return ((CaptioningManager) getContext().getSystemService("captioning")).isEnabled();
    }

    @TargetApi(19)
    private float getUserCaptionFontScaleV19() {
        return ((CaptioningManager) getContext().getSystemService("captioning")).getFontScale();
    }

    @TargetApi(19)
    private CaptionStyleCompat getUserCaptionStyleV19() {
        return CaptionStyleCompat.createFromCaptionStyle(((CaptioningManager) getContext().getSystemService("captioning")).getUserStyle());
    }
}
