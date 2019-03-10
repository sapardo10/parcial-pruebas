package android.support.wearable.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.DrawableRes;
import android.support.wearable.C0395R;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewOutlineProvider;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

@SuppressLint({"ClickableViewAccessibility"})
@TargetApi(21)
@Deprecated
public class CircularButton extends View {
    private static final float DEFAULT_ICON_SIZE_DP = 48.0f;
    public static final int SCALE_MODE_CENTER = 1;
    public static final int SCALE_MODE_FIT = 0;
    private static final double SQRT_2 = Math.sqrt(2.0d);
    private ColorStateList mColors;
    private int mDiameter;
    private Drawable mImage;
    private final Interpolator mInterpolator;
    private int mRippleColor;
    private RippleDrawable mRippleDrawable;
    private int mScaleMode;
    private final ShapeDrawable mShapeDrawable;

    private class CircleOutlineProvider extends ViewOutlineProvider {
        private CircleOutlineProvider() {
        }

        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, CircularButton.this.mDiameter, CircularButton.this.mDiameter);
        }
    }

    private static int inscribedSize(int r) {
        double d = (double) r;
        double d2 = SQRT_2;
        Double.isNaN(d);
        return (int) Math.floor(d * d2);
    }

    private static int encircledRadius(int l) {
        double d = (double) l;
        double d2 = SQRT_2;
        Double.isNaN(d);
        return (int) Math.floor(d / d2);
    }

    public CircularButton(Context context) {
        this(context, null);
    }

    public CircularButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularButton(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CircularButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mRippleColor = -1;
        this.mShapeDrawable = new ShapeDrawable(new OvalShape());
        this.mShapeDrawable.getPaint().setColor(-3355444);
        super.setBackgroundDrawable(this.mShapeDrawable);
        setOutlineProvider(new CircleOutlineProvider());
        this.mInterpolator = new AccelerateInterpolator(2.0f);
        this.mScaleMode = 0;
        boolean clickable = true;
        TypedArray a = context.obtainStyledAttributes(attrs, C0395R.styleable.CircularButton, defStyleAttr, defStyleRes);
        for (int i = 0; i < a.getIndexCount(); i++) {
            int attr = a.getIndex(i);
            if (attr == C0395R.styleable.CircularButton_android_color) {
                this.mColors = a.getColorStateList(attr);
                this.mShapeDrawable.getPaint().setColor(this.mColors.getDefaultColor());
            } else if (attr == C0395R.styleable.CircularButton_android_src) {
                this.mImage = a.getDrawable(attr);
            } else if (attr == C0395R.styleable.CircularButton_buttonRippleColor) {
                setRippleColor(a.getColor(attr, -1));
            } else if (attr == C0395R.styleable.CircularButton_pressedButtonTranslationZ) {
                setPressedTranslationZ(a.getDimension(attr, 0.0f));
            } else if (attr == C0395R.styleable.CircularButton_imageScaleMode) {
                this.mScaleMode = a.getInt(attr, this.mScaleMode);
            } else if (attr == C0395R.styleable.CircularButton_android_clickable) {
                clickable = a.getBoolean(C0395R.styleable.CircularButton_android_clickable, clickable);
            }
        }
        a.recycle();
        setClickable(clickable);
    }

    public void setColor(int color) {
        this.mColors = ColorStateList.valueOf(color);
        this.mShapeDrawable.getPaint().setColor(this.mColors.getDefaultColor());
    }

    public void setColor(ColorStateList colorStateList) {
        this.mColors = colorStateList;
        this.mShapeDrawable.getPaint().setColor(this.mColors.getDefaultColor());
    }

    public void setRippleColor(int rippleColor) {
        this.mRippleColor = rippleColor;
        RippleDrawable rippleDrawable = this.mRippleDrawable;
        if (rippleDrawable != null) {
            rippleDrawable.setColor(ColorStateList.valueOf(rippleColor));
        } else if (this.mRippleColor == -1 || isInEditMode()) {
            this.mRippleDrawable = null;
            super.setBackgroundDrawable(this.mShapeDrawable);
        } else {
            ColorStateList valueOf = ColorStateList.valueOf(rippleColor);
            Drawable drawable = this.mShapeDrawable;
            this.mRippleDrawable = new RippleDrawable(valueOf, drawable, drawable);
            super.setBackgroundDrawable(this.mRippleDrawable);
        }
    }

    public void setImageResource(@DrawableRes int drawableRes) {
        setImageDrawable(getResources().getDrawable(drawableRes, null));
    }

    public Drawable getImageDrawable() {
        return this.mImage;
    }

    public void setImageDrawable(Drawable drawable) {
        Drawable drawable2 = this.mImage;
        if (drawable2 != null) {
            drawable2.setCallback(null);
        }
        if (this.mImage != drawable) {
            this.mImage = drawable;
            requestLayout();
            invalidate();
        }
        drawable2 = this.mImage;
        if (drawable2 != null) {
            drawable2.setCallback(this);
        }
    }

    public int getImageScaleMode() {
        return this.mScaleMode;
    }

    public void setImageScaleMode(int scaleMode) {
        this.mScaleMode = scaleMode;
        if (this.mImage != null) {
            invalidate();
            requestLayout();
        }
    }

    protected boolean verifyDrawable(Drawable who) {
        if (this.mImage != who) {
            if (!super.verifyDrawable(who)) {
                return false;
            }
        }
        return true;
    }

    public void setBackgroundDrawable(Drawable background) {
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        ColorStateList colorStateList = this.mColors;
        if (colorStateList != null && colorStateList.isStateful()) {
            this.mShapeDrawable.getPaint().setColor(this.mColors.getColorForState(getDrawableState(), this.mColors.getDefaultColor()));
            this.mShapeDrawable.invalidateSelf();
        }
    }

    private int dpToPx(float dp) {
        return (int) Math.ceil((double) TypedValue.applyDimension(1, dp, getResources().getDisplayMetrics()));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int imageSize;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == 1073741824 && heightMode == 1073741824) {
            this.mDiameter = Math.min(widthSize, heightSize);
        } else if (widthMode == 1073741824) {
            this.mDiameter = widthSize;
        } else if (heightMode == 1073741824) {
            this.mDiameter = heightSize;
        } else {
            int atMost;
            if (hasIntrinsicSize(this.mImage)) {
                imageSize = Math.max(this.mImage.getIntrinsicHeight(), this.mImage.getIntrinsicWidth());
            } else {
                imageSize = dpToPx(1111490560);
            }
            if (widthMode != Integer.MIN_VALUE) {
                if (heightMode != Integer.MIN_VALUE) {
                    this.mDiameter = imageSize;
                }
            }
            if (widthMode != Integer.MIN_VALUE) {
                atMost = heightSize;
            } else if (heightMode != Integer.MIN_VALUE) {
                atMost = widthSize;
            } else {
                atMost = Math.min(widthSize, heightSize);
            }
            this.mDiameter = Math.min(atMost, encircledRadius(imageSize) * 2);
        }
        imageSize = this.mDiameter;
        setMeasuredDimension(imageSize, imageSize);
    }

    private static boolean hasIntrinsicSize(Drawable d) {
        return d != null && d.getIntrinsicHeight() > 0 && d.getIntrinsicWidth() > 0;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int w = r - l;
        int h = b - t;
        int iw = this.mImage;
        if (iw != 0) {
            int hpad;
            iw = iw.getIntrinsicWidth();
            int ih = this.mImage.getIntrinsicHeight();
            if (this.mScaleMode != 0) {
                if (hasIntrinsicSize(this.mImage)) {
                    hpad = (int) (((float) (w - iw)) / 1073741824);
                    int vpad = (int) (((float) (h - ih)) / 2.0f);
                    this.mImage.setBounds(hpad, vpad, hpad + iw, vpad + ih);
                    return;
                }
            }
            hpad = inscribedSize(this.mDiameter / 2);
            int vpad2 = (this.mDiameter - hpad) / 2;
            int hpad2 = vpad2;
            if (hasIntrinsicSize(this.mImage)) {
                if (iw == ih) {
                    ih = hpad;
                    iw = hpad;
                } else {
                    float aspect = ((float) iw) / ((float) ih);
                    if (iw > ih) {
                        iw = hpad;
                        ih = (int) (((float) iw) / aspect);
                        vpad2 = (int) (((float) (hpad - ih)) / 2.0f);
                    } else {
                        ih = hpad;
                        iw = (int) (((float) ih) * aspect);
                        hpad2 = (int) (((float) (hpad - iw)) / 2.0f);
                    }
                }
                this.mImage.setBounds(hpad2, vpad2, hpad2 + iw, vpad2 + ih);
            } else {
                this.mImage.setBounds(hpad2, vpad2, hpad2 + hpad, vpad2 + hpad);
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Drawable drawable = this.mImage;
        if (drawable != null) {
            drawable.draw(canvas);
        }
    }

    public void setPressedTranslationZ(float translationZ) {
        StateListAnimator stateListAnimator = new StateListAnimator();
        stateListAnimator.addState(PRESSED_ENABLED_STATE_SET, setupAnimator(ObjectAnimator.ofFloat(this, "translationZ", new float[]{translationZ})));
        stateListAnimator.addState(ENABLED_FOCUSED_STATE_SET, setupAnimator(ObjectAnimator.ofFloat(this, "translationZ", new float[]{translationZ})));
        stateListAnimator.addState(EMPTY_STATE_SET, setupAnimator(ObjectAnimator.ofFloat(this, "translationZ", new float[]{getElevation()})));
        setStateListAnimator(stateListAnimator);
    }

    private Animator setupAnimator(Animator animator) {
        animator.setInterpolator(this.mInterpolator);
        return animator;
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = super.onTouchEvent(event);
        if (handled) {
            if ((event.getAction() & 255) == 0) {
                getBackground().setHotspot(event.getX(), event.getY());
            }
        }
        return handled;
    }
}
