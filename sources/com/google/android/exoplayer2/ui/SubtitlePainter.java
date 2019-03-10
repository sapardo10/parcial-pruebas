package com.google.android.exoplayer2.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.RelativeSizeSpan;
import com.google.android.exoplayer2.text.CaptionStyleCompat;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;

final class SubtitlePainter {
    private static final float INNER_PADDING_RATIO = 0.125f;
    private static final String TAG = "SubtitlePainter";
    private boolean applyEmbeddedFontSizes;
    private boolean applyEmbeddedStyles;
    private int backgroundColor;
    private Rect bitmapRect;
    private float bottomPaddingFraction;
    private Bitmap cueBitmap;
    private float cueBitmapHeight;
    private float cueLine;
    private int cueLineAnchor;
    private int cueLineType;
    private float cuePosition;
    private int cuePositionAnchor;
    private float cueSize;
    private CharSequence cueText;
    private Alignment cueTextAlignment;
    private float cueTextSizePx;
    private float defaultTextSizePx;
    private int edgeColor;
    private int edgeType;
    private int foregroundColor;
    private final float outlineWidth;
    private final Paint paint;
    private int parentBottom;
    private int parentLeft;
    private int parentRight;
    private int parentTop;
    private final float shadowOffset;
    private final float shadowRadius;
    private final float spacingAdd;
    private final float spacingMult;
    private StaticLayout textLayout;
    private int textLeft;
    private int textPaddingX;
    private final TextPaint textPaint = new TextPaint();
    private int textTop;
    private int windowColor;

    public SubtitlePainter(Context context) {
        TypedArray styledAttributes = context.obtainStyledAttributes(null, new int[]{16843287, 16843288}, 0, 0);
        this.spacingAdd = (float) styledAttributes.getDimensionPixelSize(0, 0);
        this.spacingMult = styledAttributes.getFloat(1, 1.0f);
        styledAttributes.recycle();
        int twoDpInPx = Math.round((((float) context.getResources().getDisplayMetrics().densityDpi) * 2.0f) / 1126170624);
        this.outlineWidth = (float) twoDpInPx;
        this.shadowRadius = (float) twoDpInPx;
        this.shadowOffset = (float) twoDpInPx;
        this.textPaint.setAntiAlias(true);
        this.textPaint.setSubpixelText(true);
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Style.FILL);
    }

    public void draw(Cue cue, boolean applyEmbeddedStyles, boolean applyEmbeddedFontSizes, CaptionStyleCompat style, float defaultTextSizePx, float cueTextSizePx, float bottomPaddingFraction, Canvas canvas, int cueBoxLeft, int cueBoxTop, int cueBoxRight, int cueBoxBottom) {
        SubtitlePainter subtitlePainter = this;
        Cue cue2 = cue;
        boolean z = applyEmbeddedStyles;
        boolean z2 = applyEmbeddedFontSizes;
        CaptionStyleCompat captionStyleCompat = style;
        float f = defaultTextSizePx;
        float f2 = cueTextSizePx;
        float f3 = bottomPaddingFraction;
        Canvas canvas2 = canvas;
        int i = cueBoxLeft;
        int i2 = cueBoxTop;
        int i3 = cueBoxRight;
        int i4 = cueBoxBottom;
        boolean isTextCue = cue2.bitmap == null;
        int windowColor = ViewCompat.MEASURED_STATE_MASK;
        if (isTextCue) {
            if (!TextUtils.isEmpty(cue2.text)) {
                int i5 = (cue2.windowColorSet && z) ? cue2.windowColor : captionStyleCompat.windowColor;
                windowColor = i5;
            } else {
                return;
            }
        }
        if (!areCharSequencesEqual(subtitlePainter.cueText, cue2.text)) {
            canvas2 = canvas;
        } else if (!Util.areEqual(subtitlePainter.cueTextAlignment, cue2.textAlignment) || subtitlePainter.cueBitmap != cue2.bitmap || subtitlePainter.cueLine != cue2.line || subtitlePainter.cueLineType != cue2.lineType) {
            canvas2 = canvas;
        } else if (!Util.areEqual(Integer.valueOf(subtitlePainter.cueLineAnchor), Integer.valueOf(cue2.lineAnchor)) || subtitlePainter.cuePosition != cue2.position) {
            canvas2 = canvas;
        } else if (!Util.areEqual(Integer.valueOf(subtitlePainter.cuePositionAnchor), Integer.valueOf(cue2.positionAnchor)) || subtitlePainter.cueSize != cue2.size || subtitlePainter.cueBitmapHeight != cue2.bitmapHeight || subtitlePainter.applyEmbeddedStyles != z || subtitlePainter.applyEmbeddedFontSizes != z2 || subtitlePainter.foregroundColor != captionStyleCompat.foregroundColor || subtitlePainter.backgroundColor != captionStyleCompat.backgroundColor || subtitlePainter.windowColor != windowColor || subtitlePainter.edgeType != captionStyleCompat.edgeType || subtitlePainter.edgeColor != captionStyleCompat.edgeColor) {
            canvas2 = canvas;
        } else if (Util.areEqual(subtitlePainter.textPaint.getTypeface(), captionStyleCompat.typeface) && subtitlePainter.defaultTextSizePx == f && subtitlePainter.cueTextSizePx == f2 && subtitlePainter.bottomPaddingFraction == f3 && subtitlePainter.parentLeft == i && subtitlePainter.parentTop == i2 && subtitlePainter.parentRight == i3 && subtitlePainter.parentBottom == i4) {
            drawLayout(canvas, isTextCue);
            return;
        } else {
            canvas2 = canvas;
        }
        subtitlePainter.cueText = cue2.text;
        subtitlePainter.cueTextAlignment = cue2.textAlignment;
        subtitlePainter.cueBitmap = cue2.bitmap;
        subtitlePainter.cueLine = cue2.line;
        subtitlePainter.cueLineType = cue2.lineType;
        subtitlePainter.cueLineAnchor = cue2.lineAnchor;
        subtitlePainter.cuePosition = cue2.position;
        subtitlePainter.cuePositionAnchor = cue2.positionAnchor;
        subtitlePainter.cueSize = cue2.size;
        subtitlePainter.cueBitmapHeight = cue2.bitmapHeight;
        subtitlePainter.applyEmbeddedStyles = z;
        subtitlePainter.applyEmbeddedFontSizes = z2;
        subtitlePainter.foregroundColor = captionStyleCompat.foregroundColor;
        subtitlePainter.backgroundColor = captionStyleCompat.backgroundColor;
        subtitlePainter.windowColor = windowColor;
        subtitlePainter.edgeType = captionStyleCompat.edgeType;
        subtitlePainter.edgeColor = captionStyleCompat.edgeColor;
        subtitlePainter.textPaint.setTypeface(captionStyleCompat.typeface);
        subtitlePainter.defaultTextSizePx = f;
        subtitlePainter.cueTextSizePx = f2;
        subtitlePainter.bottomPaddingFraction = f3;
        subtitlePainter.parentLeft = i;
        subtitlePainter.parentTop = i2;
        subtitlePainter.parentRight = i3;
        subtitlePainter.parentBottom = i4;
        if (isTextCue) {
            setupTextLayout();
        } else {
            setupBitmapLayout();
        }
        drawLayout(canvas2, isTextCue);
    }

    private void setupTextLayout() {
        int parentWidth = this.parentRight - this.parentLeft;
        int parentHeight = this.parentBottom - this.parentTop;
        this.textPaint.setTextSize(this.defaultTextSizePx);
        int textPaddingX = (int) ((this.defaultTextSizePx * INNER_PADDING_RATIO) + 1056964608);
        int availableWidth = parentWidth - (textPaddingX * 2);
        float f = this.cueSize;
        if (f != Float.MIN_VALUE) {
            availableWidth = (int) (((float) availableWidth) * f);
        }
        if (availableWidth <= 0) {
            Log.m10w(TAG, "Skipped drawing subtitle cue (insufficient space)");
            return;
        }
        int cueLength;
        int i;
        int i2;
        CharSequence cueText = r0.cueText;
        if (!r0.applyEmbeddedStyles) {
            cueText = cueText.toString();
        } else if (!r0.applyEmbeddedFontSizes) {
            SpannableStringBuilder newCueText = new SpannableStringBuilder(cueText);
            cueLength = newCueText.length();
            RelativeSizeSpan[] relSpans = (RelativeSizeSpan[]) newCueText.getSpans(0, cueLength, RelativeSizeSpan.class);
            for (AbsoluteSizeSpan absSpan : (AbsoluteSizeSpan[]) newCueText.getSpans(0, cueLength, AbsoluteSizeSpan.class)) {
                newCueText.removeSpan(absSpan);
            }
            for (RelativeSizeSpan relSpan : relSpans) {
                newCueText.removeSpan(relSpan);
            }
            cueText = newCueText;
        } else if (r0.cueTextSizePx > 0.0f) {
            SpannableStringBuilder newCueText2 = new SpannableStringBuilder(cueText);
            newCueText2.setSpan(new AbsoluteSizeSpan((int) r0.cueTextSizePx), 0, newCueText2.length(), 16711680);
            cueText = newCueText2;
        }
        if (Color.alpha(r0.backgroundColor) > 0) {
            newCueText2 = new SpannableStringBuilder(cueText);
            newCueText2.setSpan(new BackgroundColorSpan(r0.backgroundColor), 0, newCueText2.length(), 16711680);
            cueText = newCueText2;
        }
        Alignment alignment = r0.cueTextAlignment;
        if (alignment == null) {
            alignment = Alignment.ALIGN_CENTER;
        }
        Alignment textAlignment = alignment;
        r0.textLayout = new StaticLayout(cueText, r0.textPaint, availableWidth, textAlignment, r0.spacingMult, r0.spacingAdd, true);
        int height = r0.textLayout.getHeight();
        int textWidth = 0;
        int lineCount = r0.textLayout.getLineCount();
        for (cueLength = 0; cueLength < lineCount; cueLength++) {
            textWidth = Math.max((int) Math.ceil((double) r0.textLayout.getLineWidth(cueLength)), textWidth);
        }
        if (r0.cueSize != Float.MIN_VALUE && textWidth < availableWidth) {
            textWidth = availableWidth;
        }
        textWidth += textPaddingX * 2;
        float f2 = r0.cuePosition;
        if (f2 != Float.MIN_VALUE) {
            cueLength = Math.round(((float) parentWidth) * f2) + r0.parentLeft;
            i = r0.cuePositionAnchor;
            i = i == 2 ? cueLength - textWidth : i == 1 ? ((cueLength * 2) - textWidth) / 2 : cueLength;
            i = Math.max(i, r0.parentLeft);
            cueLength = Math.min(i + textWidth, r0.parentRight);
        } else {
            i = ((parentWidth - textWidth) / 2) + r0.parentLeft;
            cueLength = i + textWidth;
        }
        textWidth = cueLength - i;
        if (textWidth <= 0) {
            Log.m10w(TAG, "Skipped drawing subtitle cue (invalid horizontal positioning)");
            return;
        }
        float f3 = r0.cueLine;
        if (f3 != Float.MIN_VALUE) {
            int anchorPosition;
            if (r0.cueLineType == 0) {
                anchorPosition = Math.round(((float) parentHeight) * f3) + r0.parentTop;
            } else {
                anchorPosition = r0.textLayout.getLineBottom(0) - r0.textLayout.getLineTop(0);
                float f4 = r0.cueLine;
                if (f4 >= 0.0f) {
                    anchorPosition = Math.round(f4 * ((float) anchorPosition)) + r0.parentTop;
                } else {
                    anchorPosition = Math.round((f4 + 1.0f) * ((float) anchorPosition)) + r0.parentBottom;
                }
            }
            int i3 = r0.cueLineAnchor;
            i2 = i3 == 2 ? anchorPosition - height : i3 == 1 ? ((anchorPosition * 2) - height) / 2 : anchorPosition;
            i3 = i2 + height;
            int i4 = r0.parentBottom;
            if (i3 > i4) {
                i2 = i4 - height;
            } else if (i2 < r0.parentTop) {
                i2 = r0.parentTop;
            }
        } else {
            i2 = (r0.parentBottom - height) - ((int) (((float) parentHeight) * r0.bottomPaddingFraction));
        }
        r0.textLayout = new StaticLayout(cueText, r0.textPaint, textWidth, textAlignment, r0.spacingMult, r0.spacingAdd, true);
        r0.textLeft = i;
        r0.textTop = i2;
        r0.textPaddingX = textPaddingX;
    }

    private void setupBitmapLayout() {
        int height;
        float f;
        int i;
        float f2;
        int y;
        int parentWidth = this.parentRight;
        int i2 = this.parentLeft;
        parentWidth -= i2;
        int parentHeight = this.parentBottom;
        int i3 = this.parentTop;
        parentHeight -= i3;
        i2 = ((float) i2) + (((float) parentWidth) * this.cuePosition);
        i3 = ((float) i3) + (((float) parentHeight) * this.cueLine);
        int width = Math.round(((float) parentWidth) * this.cueSize);
        float f3 = this.cueBitmapHeight;
        if (f3 != Float.MIN_VALUE) {
            height = Math.round(((float) parentHeight) * f3);
        } else {
            height = Math.round(((float) width) * (((float) this.cueBitmap.getHeight()) / ((float) this.cueBitmap.getWidth())));
        }
        int i4 = this.cueLineAnchor;
        if (i4 == 2) {
            f = (float) width;
        } else if (i4 == 1) {
            f = (float) (width / 2);
        } else {
            i4 = i2;
            i4 = Math.round(i4);
            i = this.cuePositionAnchor;
            if (i == 2) {
                f2 = (float) height;
            } else if (i != 1) {
                f2 = (float) (height / 2);
            } else {
                y = i3;
                y = Math.round(y);
                this.bitmapRect = new Rect(i4, y, i4 + width, y + height);
            }
            y = i3 - f2;
            y = Math.round(y);
            this.bitmapRect = new Rect(i4, y, i4 + width, y + height);
        }
        i4 = i2 - f;
        i4 = Math.round(i4);
        i = this.cuePositionAnchor;
        if (i == 2) {
            f2 = (float) height;
        } else if (i != 1) {
            y = i3;
            y = Math.round(y);
            this.bitmapRect = new Rect(i4, y, i4 + width, y + height);
        } else {
            f2 = (float) (height / 2);
        }
        y = i3 - f2;
        y = Math.round(y);
        this.bitmapRect = new Rect(i4, y, i4 + width, y + height);
    }

    private void drawLayout(Canvas canvas, boolean isTextCue) {
        if (isTextCue) {
            drawTextLayout(canvas);
        } else {
            drawBitmapLayout(canvas);
        }
    }

    private void drawTextLayout(Canvas canvas) {
        StaticLayout layout = this.textLayout;
        if (layout != null) {
            int saveCount = canvas.save();
            canvas.translate((float) this.textLeft, (float) this.textTop);
            if (Color.alpha(this.windowColor) > 0) {
                this.paint.setColor(this.windowColor);
                canvas.drawRect((float) (-this.textPaddingX), 0.0f, (float) (layout.getWidth() + this.textPaddingX), (float) layout.getHeight(), this.paint);
            }
            int i = this.edgeType;
            boolean z = true;
            if (i == 1) {
                this.textPaint.setStrokeJoin(Join.ROUND);
                this.textPaint.setStrokeWidth(this.outlineWidth);
                this.textPaint.setColor(this.edgeColor);
                this.textPaint.setStyle(Style.FILL_AND_STROKE);
                layout.draw(canvas);
            } else if (i == 2) {
                TextPaint textPaint = this.textPaint;
                float f = this.shadowRadius;
                float f2 = this.shadowOffset;
                textPaint.setShadowLayer(f, f2, f2, this.edgeColor);
            } else {
                if (i != 3) {
                    if (i == 4) {
                    }
                }
                if (this.edgeType != 3) {
                    z = false;
                }
                boolean raised = z;
                int colorDown = -1;
                int colorUp = raised ? -1 : this.edgeColor;
                if (raised) {
                    colorDown = this.edgeColor;
                }
                float offset = this.shadowRadius / 2.0f;
                this.textPaint.setColor(this.foregroundColor);
                this.textPaint.setStyle(Style.FILL);
                this.textPaint.setShadowLayer(this.shadowRadius, -offset, -offset, colorUp);
                layout.draw(canvas);
                this.textPaint.setShadowLayer(this.shadowRadius, offset, offset, colorDown);
            }
            this.textPaint.setColor(this.foregroundColor);
            this.textPaint.setStyle(Style.FILL);
            layout.draw(canvas);
            this.textPaint.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
            canvas.restoreToCount(saveCount);
        }
    }

    private void drawBitmapLayout(Canvas canvas) {
        canvas.drawBitmap(this.cueBitmap, null, this.bitmapRect, null);
    }

    private static boolean areCharSequencesEqual(CharSequence first, CharSequence second) {
        if (first != second) {
            if (first == null || !first.equals(second)) {
                return false;
            }
        }
        return true;
    }
}
