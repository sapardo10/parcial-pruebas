package android.support.wearable.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.support.v4.view.GravityCompat;
import android.support.wearable.C0395R;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;
import java.util.Objects;

@TargetApi(21)
@Deprecated
public class ActionLabel extends View {
    private static final boolean DEBUG = false;
    static final int MAX_TEXT_SIZE = 60;
    static final int MIN_TEXT_SIZE = 10;
    private static final int MONOSPACE = 3;
    private static final int SANS = 1;
    private static final int SERIF = 2;
    private static final String TAG = "ActionLabel";
    private int mCurTextColor;
    private float mCurrentTextSize;
    private int mDrawMaxLines;
    private int mGravity;
    private Layout mLayout;
    private float mLineSpacingAdd;
    private float mLineSpacingMult;
    private int mMaxLines;
    private float mMaxTextSize;
    private float mMinTextSize;
    private float mSpacingAdd;
    private float mSpacingMult;
    private CharSequence mText;
    private ColorStateList mTextColor;
    private final TextPaint mTextPaint;

    public ActionLabel(Context context) {
        this(context, null);
    }

    public ActionLabel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionLabel(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ActionLabel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mGravity = 8388659;
        this.mSpacingMult = 1.0f;
        this.mSpacingAdd = 0.0f;
        this.mMaxLines = Integer.MAX_VALUE;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float density = dm.density;
        float scaledDensity = dm.scaledDensity;
        this.mMinTextSize = 10.0f * scaledDensity;
        this.mMaxTextSize = 60.0f * scaledDensity;
        this.mTextPaint = new TextPaint(1);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, C0395R.styleable.ActionLabel, defStyleAttr, defStyleRes);
        this.mText = a.getText(C0395R.styleable.ActionLabel_android_text);
        this.mMinTextSize = a.getDimension(C0395R.styleable.ActionLabel_minTextSize, this.mMinTextSize);
        this.mMaxTextSize = a.getDimension(C0395R.styleable.ActionLabel_maxTextSize, this.mMaxTextSize);
        this.mTextColor = a.getColorStateList(C0395R.styleable.ActionLabel_android_textColor);
        this.mMaxLines = a.getInt(C0395R.styleable.ActionLabel_android_maxLines, 2);
        if (this.mTextColor != null) {
            updateTextColors();
        }
        this.mTextPaint.setTextSize(this.mMaxTextSize);
        setTypefaceFromAttrs(a.getString(C0395R.styleable.ActionLabel_android_fontFamily), a.getInt(C0395R.styleable.ActionLabel_android_typeface, -1), a.getInt(C0395R.styleable.ActionLabel_android_textStyle, -1));
        this.mGravity = a.getInt(C0395R.styleable.ActionLabel_android_gravity, this.mGravity);
        this.mLineSpacingAdd = (float) a.getDimensionPixelSize(C0395R.styleable.ActionLabel_android_lineSpacingExtra, (int) this.mLineSpacingAdd);
        this.mLineSpacingMult = a.getFloat(C0395R.styleable.ActionLabel_android_lineSpacingMultiplier, this.mLineSpacingMult);
        a.recycle();
        if (this.mText == null) {
            this.mText = "";
        }
    }

    public void setText(CharSequence text) {
        if (text == null) {
            throw new RuntimeException("Can not set ActionLabel text to null");
        } else if (!Objects.equals(this.mText, text)) {
            this.mLayout = null;
            this.mText = text;
            requestLayout();
            invalidate();
        }
    }

    public void setMinTextSize(float size) {
        setMinTextSize(2, size);
    }

    public void setMinTextSize(int unit, float size) {
        float sizePx = TypedValue.applyDimension(unit, size, getContext().getResources().getDisplayMetrics());
        if (sizePx != this.mMinTextSize) {
            this.mLayout = null;
            this.mMinTextSize = sizePx;
            requestLayout();
            invalidate();
        }
    }

    public void setMaxTextSize(float size) {
        setMaxTextSize(2, size);
    }

    public void setMaxTextSize(int unit, float size) {
        float sizePx = TypedValue.applyDimension(unit, size, getContext().getResources().getDisplayMetrics());
        if (sizePx != this.mMaxTextSize) {
            this.mLayout = null;
            this.mMaxTextSize = sizePx;
            requestLayout();
            invalidate();
        }
    }

    public void setTypeface(Typeface tf) {
        if (!Objects.equals(this.mTextPaint.getTypeface(), tf)) {
            this.mTextPaint.setTypeface(tf);
            if (this.mLayout != null) {
                requestLayout();
                invalidate();
            }
        }
    }

    public void setTypeface(Typeface tf, int style) {
        float f = 0.0f;
        boolean z = false;
        if (style > 0) {
            if (tf == null) {
                tf = Typeface.defaultFromStyle(style);
            } else {
                tf = Typeface.create(tf, style);
            }
            setTypeface(tf);
            int need = ((tf != null ? tf.getStyle() : 0) ^ -1) & style;
            TextPaint textPaint = this.mTextPaint;
            if ((need & 1) != 0) {
                z = true;
            }
            textPaint.setFakeBoldText(z);
            TextPaint textPaint2 = this.mTextPaint;
            if ((need & 2) != 0) {
                f = -0.25f;
            }
            textPaint2.setTextSkewX(f);
            return;
        }
        this.mTextPaint.setFakeBoldText(false);
        this.mTextPaint.setTextSkewX(0.0f);
        setTypeface(tf);
    }

    public Typeface getTypeface() {
        return this.mTextPaint.getTypeface();
    }

    void setTypefaceFromAttrs(String familyName, int typefaceIndex, int styleIndex) {
        Typeface tf = null;
        if (familyName != null) {
            tf = Typeface.create(familyName, styleIndex);
            if (tf != null) {
                setTypeface(tf);
                return;
            }
        }
        switch (typefaceIndex) {
            case 1:
                tf = Typeface.SANS_SERIF;
                break;
            case 2:
                tf = Typeface.SERIF;
                break;
            case 3:
                tf = Typeface.MONOSPACE;
                break;
            default:
                break;
        }
        setTypeface(tf, styleIndex);
    }

    public void setLineSpacing(float add, float mult) {
        if (this.mSpacingAdd == add) {
            if (this.mSpacingMult == mult) {
                return;
            }
        }
        this.mSpacingAdd = add;
        this.mSpacingMult = mult;
        if (this.mLayout != null) {
            this.mLayout = null;
            requestLayout();
            invalidate();
        }
    }

    public float getLineSpacingMultiplier() {
        return this.mSpacingMult;
    }

    public float getLineSpacingExtra() {
        return this.mSpacingAdd;
    }

    public void setTextColor(int color) {
        this.mTextColor = ColorStateList.valueOf(color);
        updateTextColors();
    }

    public void setTextColor(ColorStateList colors) {
        if (colors != null) {
            this.mTextColor = colors;
            updateTextColors();
            return;
        }
        throw new NullPointerException();
    }

    public final ColorStateList getTextColors() {
        return this.mTextColor;
    }

    public final int getCurrentTextColor() {
        return this.mCurTextColor;
    }

    public int getMaxLines() {
        return this.mMaxLines;
    }

    public void setMaxLines(int lines) {
        if (this.mMaxLines != lines) {
            this.mMaxLines = lines;
            this.mLayout = null;
            requestLayout();
            invalidate();
        }
    }

    public void setGravity(int gravity) {
        if (this.mGravity != gravity) {
            this.mGravity = gravity;
            invalidate();
        }
    }

    public int getGravity() {
        return this.mGravity;
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        ColorStateList colorStateList = this.mTextColor;
        if (colorStateList != null && colorStateList.isStateful()) {
            updateTextColors();
        }
    }

    private void updateTextColors() {
        int color = this.mTextColor.getColorForState(getDrawableState(), 0);
        if (color != this.mCurTextColor) {
            this.mCurTextColor = color;
            invalidate();
        }
    }

    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);
        this.mLayout = null;
        requestLayout();
        invalidate();
    }

    @SuppressLint({"RtlHardcoded"})
    private Alignment getLayoutAlignment() {
        switch (getTextAlignment()) {
            case 1:
                int i = this.mGravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
                if (i == 1) {
                    return Alignment.ALIGN_CENTER;
                }
                if (i == 3) {
                    return Alignment.ALIGN_NORMAL;
                }
                if (i == 5) {
                    return Alignment.ALIGN_OPPOSITE;
                }
                if (i == 8388611) {
                    return Alignment.ALIGN_NORMAL;
                }
                if (i != GravityCompat.END) {
                    return Alignment.ALIGN_NORMAL;
                }
                return Alignment.ALIGN_OPPOSITE;
            case 2:
                return Alignment.ALIGN_NORMAL;
            case 3:
                return Alignment.ALIGN_OPPOSITE;
            case 4:
                return Alignment.ALIGN_CENTER;
            default:
                return Alignment.ALIGN_NORMAL;
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = -1;
        int height = -1;
        if (widthMode == 1073741824) {
            width = widthSize;
        }
        if (heightMode == 1073741824) {
            height = heightSize;
        }
        if (width == -1) {
            this.mTextPaint.setTextSize(this.mMaxTextSize);
            width = (int) Math.ceil((double) Layout.getDesiredWidth(this.mText, this.mTextPaint));
            this.mTextPaint.setTextSize(this.mCurrentTextSize);
        }
        if (widthMode == Integer.MIN_VALUE) {
            width = Math.min(width, widthSize);
        }
        Alignment alignment = getLayoutAlignment();
        if (height == -1) {
            height = heightMode == Integer.MIN_VALUE ? heightSize : Integer.MAX_VALUE;
        }
        Layout layout = this.mLayout;
        if (layout == null) {
            this.mLayout = makeNewLayout(width, height, alignment);
        } else {
            boolean heightChanged = true;
            boolean widthChanged = layout.getWidth() != width;
            if (this.mLayout.getHeight() == height) {
                heightChanged = false;
            }
            if (!widthChanged) {
                if (heightChanged) {
                }
            }
            this.mLayout = makeNewLayout(width, height, alignment);
        }
        layout = this.mLayout;
        if (layout == null) {
            setMeasuredDimension(0, 0);
            return;
        }
        if (heightMode != 1073741824) {
            height = layout.getLineTop(layout.getLineCount());
        }
        if (heightMode == Integer.MIN_VALUE) {
            height = Math.min(height, heightSize);
        }
        setMeasuredDimension(width, height);
    }

    private Layout makeNewLayout(int width, int height, Alignment alignment) {
        ActionLabel actionLabel = this;
        if (height > 0) {
            if (width > 0) {
                Layout layout;
                boolean z;
                boolean z2;
                int availableHeight = height - (getPaddingTop() + getPaddingBottom());
                int availableWidth = width - (getPaddingLeft() + getPaddingRight());
                float f = actionLabel.mMaxTextSize;
                actionLabel.mCurrentTextSize = f;
                actionLabel.mTextPaint.setTextSize(f);
                Layout layout2 = new StaticLayout(actionLabel.mText, actionLabel.mTextPaint, availableWidth, alignment, actionLabel.mSpacingMult, actionLabel.mSpacingAdd, true);
                boolean tooManyLines = layout2.getLineCount() > actionLabel.mMaxLines;
                boolean tooTall = layout2.getLineTop(layout2.getLineCount()) > availableHeight;
                boolean textCanShrink = actionLabel.mTextPaint.getTextSize() > actionLabel.mMinTextSize;
                if (!tooManyLines) {
                    if (!tooTall) {
                        layout = layout2;
                        z = tooTall;
                        z2 = textCanShrink;
                        actionLabel.mDrawMaxLines = Math.min(actionLabel.mMaxLines, layout.getLineCount());
                        return layout;
                    }
                }
                layout = layout2;
                z = tooTall;
                z2 = textCanShrink;
                int tries = 1;
                boolean tooManyLines2 = tooManyLines;
                while (true) {
                    if ((tooManyLines2 || tooTall) && textCanShrink) {
                        actionLabel.mCurrentTextSize -= 1.0f;
                        actionLabel.mTextPaint.setTextSize(actionLabel.mCurrentTextSize);
                        layout2 = new StaticLayout(actionLabel.mText, actionLabel.mTextPaint, availableWidth, alignment, actionLabel.mSpacingMult, actionLabel.mSpacingAdd, true);
                        z = layout2.getLineTop(layout2.getLineCount()) > availableHeight;
                        tooManyLines2 = layout2.getLineCount() > actionLabel.mMaxLines;
                        z2 = actionLabel.mTextPaint.getTextSize() > actionLabel.mMinTextSize;
                        tries++;
                        layout = layout2;
                    }
                }
                tooManyLines = tooManyLines2;
                actionLabel.mDrawMaxLines = Math.min(actionLabel.mMaxLines, layout.getLineCount());
                return layout;
            }
        }
        return null;
    }

    private int getAvailableHeight() {
        return getHeight() - (getPaddingTop() + getPaddingBottom());
    }

    int getVerticalOffset() {
        int availHeight = getAvailableHeight();
        int textHeight = this.mLayout.getLineTop(this.mDrawMaxLines);
        int gravity = this.mGravity & 112;
        if (gravity == 16) {
            return (availHeight - textHeight) / 2;
        }
        if (gravity == 48) {
            return 0;
        }
        if (gravity != 80) {
            return 0;
        }
        return availHeight - textHeight;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mLayout != null) {
            canvas.save();
            this.mTextPaint.setColor(this.mCurTextColor);
            this.mTextPaint.drawableState = getDrawableState();
            canvas.translate((float) getPaddingLeft(), (float) (getPaddingTop() + getVerticalOffset()));
            canvas.clipRect(0, 0, getWidth() - getPaddingRight(), this.mLayout.getLineTop(this.mDrawMaxLines));
            this.mLayout.draw(canvas);
            canvas.restore();
        }
    }
}
