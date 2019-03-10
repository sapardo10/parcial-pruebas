package android.support.wearable.complications.rendering;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.StaticLayout.Builder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import java.util.Objects;

@TargetApi(24)
public class TextRenderer {
    private static final int DEFAULT_MINIMUM_CHARACTERS_SHOWN = 7;
    private static final int SPACE_CHARACTER = 32;
    private static final int TEXT_SIZE_STEP_SIZE = 1;
    private Alignment mAlignment = Alignment.ALIGN_CENTER;
    @Nullable
    private String mAmbientModeText;
    private final Rect mBounds = new Rect();
    private TruncateAt mEllipsize = TruncateAt.END;
    private int mGravity = 17;
    private boolean mInAmbientMode = false;
    private int mMaxLines = 1;
    private int mMinCharactersShown = 7;
    private boolean mNeedCalculateBounds;
    private boolean mNeedUpdateLayout;
    private final Rect mOutputRect = new Rect();
    private TextPaint mPaint;
    private float mRelativePaddingBottom;
    private float mRelativePaddingEnd;
    private float mRelativePaddingStart;
    private float mRelativePaddingTop;
    private StaticLayout mStaticLayout;
    @Nullable
    private CharSequence mText;
    private final Rect mWorkingRect = new Rect();

    public void draw(Canvas canvas, Rect bounds) {
        if (!TextUtils.isEmpty(this.mText)) {
            if (!this.mNeedUpdateLayout) {
                if (this.mBounds.width() == bounds.width()) {
                    if (this.mBounds.height() == bounds.height()) {
                        if (!this.mNeedCalculateBounds) {
                            if (!this.mBounds.equals(bounds)) {
                                canvas.save();
                                canvas.translate((float) this.mOutputRect.left, (float) this.mOutputRect.top);
                                this.mStaticLayout.draw(canvas);
                                canvas.restore();
                            }
                        }
                        this.mBounds.set(bounds);
                        calculateBounds();
                        this.mNeedCalculateBounds = false;
                        canvas.save();
                        canvas.translate((float) this.mOutputRect.left, (float) this.mOutputRect.top);
                        this.mStaticLayout.draw(canvas);
                        canvas.restore();
                    }
                }
            }
            updateLayout(bounds.width(), bounds.height());
            this.mNeedUpdateLayout = false;
            this.mNeedCalculateBounds = true;
            if (this.mNeedCalculateBounds) {
                if (!this.mBounds.equals(bounds)) {
                    canvas.save();
                    canvas.translate((float) this.mOutputRect.left, (float) this.mOutputRect.top);
                    this.mStaticLayout.draw(canvas);
                    canvas.restore();
                }
            }
            this.mBounds.set(bounds);
            calculateBounds();
            this.mNeedCalculateBounds = false;
            canvas.save();
            canvas.translate((float) this.mOutputRect.left, (float) this.mOutputRect.top);
            this.mStaticLayout.draw(canvas);
            canvas.restore();
        }
    }

    public void requestUpdateLayout() {
        this.mNeedUpdateLayout = true;
    }

    public void setText(@Nullable CharSequence text) {
        if (!Objects.equals(this.mText, text)) {
            this.mText = text;
            this.mNeedUpdateLayout = true;
        }
    }

    public void setPaint(TextPaint paint) {
        this.mPaint = paint;
        this.mNeedUpdateLayout = true;
    }

    public void setRelativePadding(float start, float top, float end, float bottom) {
        if (this.mRelativePaddingStart != start || this.mRelativePaddingTop != top || this.mRelativePaddingEnd != end || this.mRelativePaddingBottom != bottom) {
            this.mRelativePaddingStart = start;
            this.mRelativePaddingTop = top;
            this.mRelativePaddingEnd = end;
            this.mRelativePaddingBottom = bottom;
            this.mNeedUpdateLayout = true;
        }
    }

    public void setGravity(int gravity) {
        if (this.mGravity != gravity) {
            this.mGravity = gravity;
            this.mNeedCalculateBounds = true;
        }
    }

    public void setMaxLines(int maxLines) {
        if (this.mMaxLines != maxLines) {
            if (maxLines > 0) {
                this.mMaxLines = maxLines;
                this.mNeedUpdateLayout = true;
            }
        }
    }

    public void setMinimumCharactersShown(int minCharactersShown) {
        if (this.mMinCharactersShown != minCharactersShown) {
            this.mMinCharactersShown = minCharactersShown;
            this.mNeedUpdateLayout = true;
        }
    }

    public void setEllipsize(@Nullable TruncateAt ellipsize) {
        if (this.mEllipsize != ellipsize) {
            this.mEllipsize = ellipsize;
            this.mNeedUpdateLayout = true;
        }
    }

    public void setAlignment(Alignment alignment) {
        if (this.mAlignment != alignment) {
            this.mAlignment = alignment;
            this.mNeedUpdateLayout = true;
        }
    }

    public boolean hasText() {
        return TextUtils.isEmpty(this.mText) ^ 1;
    }

    public boolean isLtr() {
        return this.mStaticLayout.getParagraphDirection(0) == 1;
    }

    public void setInAmbientMode(boolean inAmbientMode) {
        if (this.mInAmbientMode != inAmbientMode) {
            this.mInAmbientMode = inAmbientMode;
            if (!TextUtils.equals(this.mAmbientModeText, this.mText)) {
                this.mNeedUpdateLayout = true;
            }
        }
    }

    private void updateLayout(int width, int height) {
        if (this.mPaint == null) {
            setPaint(new TextPaint());
        }
        int availableWidth = (int) (((float) width) * ((1.0f - this.mRelativePaddingStart) - this.mRelativePaddingEnd));
        TextPaint paint = new TextPaint(this.mPaint);
        paint.setTextSize(Math.min((float) (height / this.mMaxLines), paint.getTextSize()));
        float textWidth = this.mText;
        if (paint.measureText(textWidth, 0, textWidth.length()) > ((float) availableWidth)) {
            int charactersShown = this.mMinCharactersShown;
            TruncateAt truncateAt = this.mEllipsize;
            if (truncateAt != null && truncateAt != TruncateAt.MARQUEE) {
                charactersShown++;
            }
            CharSequence textToFit = this.mText.subSequence(0, Math.min(charactersShown, this.mText.length()));
            for (textWidth = paint.measureText(textToFit, 0, textToFit.length()); textWidth > ((float) availableWidth); textWidth = paint.measureText(textToFit, 0, textToFit.length())) {
                paint.setTextSize(paint.getTextSize() - 1.0f);
            }
        }
        CharSequence text = this.mText;
        if (this.mInAmbientMode) {
            this.mAmbientModeText = EmojiHelper.replaceEmoji(this.mText, 32);
            text = this.mAmbientModeText;
        }
        Builder builder = Builder.obtain(text, 0, text.length(), paint, availableWidth);
        builder.setBreakStrategy(1);
        builder.setEllipsize(this.mEllipsize);
        builder.setHyphenationFrequency(2);
        builder.setMaxLines(this.mMaxLines);
        builder.setAlignment(this.mAlignment);
        this.mStaticLayout = builder.build();
    }

    private void calculateBounds() {
        int layoutDirection = isLtr() ^ 1;
        this.mWorkingRect.set(this.mBounds.left + ((int) (((float) this.mBounds.width()) * (isLtr() ? this.mRelativePaddingStart : this.mRelativePaddingEnd))), this.mBounds.top + ((int) (((float) this.mBounds.height()) * this.mRelativePaddingTop)), this.mBounds.right - ((int) (((float) this.mBounds.width()) * (isLtr() ? this.mRelativePaddingEnd : this.mRelativePaddingStart))), this.mBounds.bottom - ((int) (((float) this.mBounds.height()) * this.mRelativePaddingBottom)));
        Gravity.apply(this.mGravity, this.mStaticLayout.getWidth(), this.mStaticLayout.getHeight(), this.mWorkingRect, this.mOutputRect, layoutDirection);
    }
}
