package android.support.wearable.view;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.wearable.C0395R;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowInsets;

@TargetApi(21)
@Deprecated
public class ActionPage extends ViewGroup {
    private static final float CIRCLE_SIZE_RATIO = 0.45f;
    private static final float CIRCLE_VERT_POSITION_SQUARE = 0.43f;
    private static final boolean DEBUG = false;
    private static final float LABEL_BOTTOM_MARGIN_RATIO_ROUND = 0.09375f;
    private static final float LABEL_WIDTH_RATIO = 0.892f;
    private static final float LABEL_WIDTH_RATIO_ROUND = 0.625f;
    public static final int SCALE_MODE_CENTER = 1;
    public static final int SCALE_MODE_FIT = 0;
    private static final String TAG = "ActionPage";
    private int mBottomInset;
    private final Point mButtonCenter;
    private float mButtonRadius;
    private int mButtonSize;
    private CircularButton mCircularButton;
    private boolean mInsetsApplied;
    private boolean mIsRound;
    private final ActionLabel mLabel;
    private int mTextHeight;
    private int mTextWidth;

    public ActionPage(Context context) {
        this(context, null);
    }

    public ActionPage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionPage(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, C0395R.style.Widget_ActionPage);
    }

    public ActionPage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        Context context2 = context;
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mButtonCenter = new Point();
        this.mCircularButton = new CircularButton(context2);
        this.mLabel = new ActionLabel(context2);
        this.mLabel.setGravity(17);
        this.mLabel.setMaxLines(2);
        float lineSpacingMult = 1.0f;
        float lineSpacingExtra = 0.0f;
        String fontFamily = null;
        int typefaceIndex = 1;
        int styleIndex = 0;
        TypedArray a = context2.obtainStyledAttributes(attrs, C0395R.styleable.ActionPage, defStyleAttr, defStyleRes);
        for (int i = 0; i < a.getIndexCount(); i++) {
            int attr = a.getIndex(i);
            if (attr == C0395R.styleable.ActionPage_android_color) {
                r0.mCircularButton.setColor(a.getColorStateList(attr));
            } else if (attr == C0395R.styleable.ActionPage_android_src) {
                r0.mCircularButton.setImageDrawable(a.getDrawable(attr));
            } else if (attr == C0395R.styleable.ActionPage_imageScaleMode) {
                r0.mCircularButton.setImageScaleMode(a.getInt(attr, 0));
            } else if (attr == C0395R.styleable.ActionPage_buttonRippleColor) {
                r0.mCircularButton.setRippleColor(a.getColor(attr, -1));
            } else if (attr == C0395R.styleable.ActionPage_pressedButtonTranslationZ) {
                r0.mCircularButton.setPressedTranslationZ(a.getDimension(attr, 0.0f));
            } else if (attr == C0395R.styleable.ActionPage_android_text) {
                r0.mLabel.setText(a.getText(attr));
            } else if (attr == C0395R.styleable.ActionPage_minTextSize) {
                r0.mLabel.setMinTextSize(0, a.getDimension(attr, 10.0f));
            } else if (attr == C0395R.styleable.ActionPage_maxTextSize) {
                r0.mLabel.setMaxTextSize(0, a.getDimension(attr, 60.0f));
            } else if (attr == C0395R.styleable.ActionPage_android_textColor) {
                r0.mLabel.setTextColor(a.getColorStateList(attr));
            } else if (attr == C0395R.styleable.ActionPage_android_maxLines) {
                r0.mLabel.setMaxLines(a.getInt(attr, 2));
            } else if (attr == C0395R.styleable.ActionPage_android_fontFamily) {
                fontFamily = a.getString(attr);
            } else if (attr == C0395R.styleable.ActionPage_android_typeface) {
                typefaceIndex = a.getInt(attr, typefaceIndex);
            } else if (attr == C0395R.styleable.ActionPage_android_textStyle) {
                styleIndex = a.getInt(attr, styleIndex);
            } else if (attr == C0395R.styleable.ActionPage_android_gravity) {
                r0.mLabel.setGravity(a.getInt(attr, 17));
            } else if (attr == C0395R.styleable.ActionPage_android_lineSpacingExtra) {
                lineSpacingExtra = a.getDimension(attr, lineSpacingExtra);
            } else if (attr == C0395R.styleable.ActionPage_android_lineSpacingMultiplier) {
                lineSpacingMult = a.getDimension(attr, lineSpacingMult);
            } else if (attr == C0395R.styleable.ActionPage_android_stateListAnimator) {
                r0.mCircularButton.setStateListAnimator(AnimatorInflater.loadStateListAnimator(context2, a.getResourceId(attr, 0)));
            }
        }
        a.recycle();
        r0.mLabel.setLineSpacing(lineSpacingExtra, lineSpacingMult);
        r0.mLabel.setTypefaceFromAttrs(fontFamily, typefaceIndex, styleIndex);
        addView(r0.mLabel);
        addView(r0.mCircularButton);
    }

    public ActionLabel getLabel() {
        return this.mLabel;
    }

    public CircularButton getButton() {
        return this.mCircularButton;
    }

    public void setText(CharSequence text) {
        this.mLabel.setText(text);
    }

    public void setColor(int color) {
        this.mCircularButton.setColor(color);
    }

    public void setColor(ColorStateList color) {
        this.mCircularButton.setColor(color);
    }

    public void setImageDrawable(Drawable drawable) {
        this.mCircularButton.setImageDrawable(drawable);
    }

    public void setImageResource(@DrawableRes int drawableRes) {
        this.mCircularButton.setImageResource(drawableRes);
    }

    public void setImageScaleMode(int scaleMode) {
        this.mCircularButton.setImageScaleMode(scaleMode);
    }

    public void setStateListAnimator(StateListAnimator stateListAnimator) {
        CircularButton circularButton = this.mCircularButton;
        if (circularButton != null) {
            circularButton.setStateListAnimator(stateListAnimator);
        }
    }

    public void setOnClickListener(OnClickListener l) {
        CircularButton circularButton = this.mCircularButton;
        if (circularButton != null) {
            circularButton.setOnClickListener(l);
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        CircularButton circularButton = this.mCircularButton;
        if (circularButton != null) {
            circularButton.setEnabled(enabled);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        if (this.mCircularButton.getImageScaleMode() == 1) {
            if (this.mCircularButton.getImageDrawable() != null) {
                this.mCircularButton.measure(0, 0);
                this.mButtonSize = Math.min(this.mCircularButton.getMeasuredWidth(), this.mCircularButton.getMeasuredHeight());
                this.mButtonRadius = ((float) this.mButtonSize) / 2.0f;
                if (this.mIsRound) {
                    this.mButtonCenter.set(width / 2, (int) (((float) height) * CIRCLE_VERT_POSITION_SQUARE));
                    this.mTextWidth = (int) (((float) width) * LABEL_WIDTH_RATIO);
                } else {
                    this.mButtonCenter.set(width / 2, height / 2);
                    this.mTextWidth = (int) (((float) width) * LABEL_WIDTH_RATIO_ROUND);
                    this.mBottomInset = (int) (((float) height) * LABEL_BOTTOM_MARGIN_RATIO_ROUND);
                }
                this.mTextHeight = (int) ((((float) height) - (((float) this.mButtonCenter.y) + this.mButtonRadius)) - ((float) this.mBottomInset));
                this.mLabel.measure(MeasureSpec.makeMeasureSpec(this.mTextWidth, 1073741824), MeasureSpec.makeMeasureSpec(this.mTextHeight, 1073741824));
            }
        }
        this.mButtonSize = (int) (((float) Math.min(width, height)) * CIRCLE_SIZE_RATIO);
        int i = this.mButtonSize;
        this.mButtonRadius = ((float) i) / 2.0f;
        this.mCircularButton.measure(MeasureSpec.makeMeasureSpec(i, 1073741824), MeasureSpec.makeMeasureSpec(this.mButtonSize, 1073741824));
        if (this.mIsRound) {
            this.mButtonCenter.set(width / 2, (int) (((float) height) * CIRCLE_VERT_POSITION_SQUARE));
            this.mTextWidth = (int) (((float) width) * LABEL_WIDTH_RATIO);
        } else {
            this.mButtonCenter.set(width / 2, height / 2);
            this.mTextWidth = (int) (((float) width) * LABEL_WIDTH_RATIO_ROUND);
            this.mBottomInset = (int) (((float) height) * LABEL_BOTTOM_MARGIN_RATIO_ROUND);
        }
        this.mTextHeight = (int) ((((float) height) - (((float) this.mButtonCenter.y) + this.mButtonRadius)) - ((float) this.mBottomInset));
        this.mLabel.measure(MeasureSpec.makeMeasureSpec(this.mTextWidth, 1073741824), MeasureSpec.makeMeasureSpec(this.mTextHeight, 1073741824));
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.mInsetsApplied) {
            requestApplyInsets();
        }
    }

    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        this.mInsetsApplied = true;
        if (this.mIsRound != insets.isRound()) {
            this.mIsRound = insets.isRound();
            requestLayout();
        }
        int insetBottom = insets.getSystemWindowInsetBottom();
        if (this.mBottomInset != insetBottom) {
            this.mBottomInset = insetBottom;
            requestLayout();
        }
        if (this.mIsRound) {
            this.mBottomInset = (int) Math.max((float) this.mBottomInset, ((float) getMeasuredHeight()) * LABEL_BOTTOM_MARGIN_RATIO_ROUND);
        }
        return insets;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int w = r - l;
        this.mCircularButton.layout((int) (((float) this.mButtonCenter.x) - this.mButtonRadius), (int) (((float) this.mButtonCenter.y) - this.mButtonRadius), (int) (((float) this.mButtonCenter.x) + this.mButtonRadius), (int) (((float) this.mButtonCenter.y) + this.mButtonRadius));
        int textHorizPadding = (int) (((float) (w - this.mTextWidth)) / 1073741824);
        this.mLabel.layout(textHorizPadding, this.mCircularButton.getBottom(), this.mTextWidth + textHorizPadding, this.mCircularButton.getBottom() + this.mTextHeight);
    }
}
