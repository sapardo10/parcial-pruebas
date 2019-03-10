package android.support.wearable.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.wearable.C0395R;
import android.util.AttributeSet;
import android.util.Property;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ProgressBar;

@TargetApi(20)
@Deprecated
public class ProgressSpinner extends ProgressBar {
    private static final Property<ProgressSpinner, Float> SHOWINGNESS = new C04511(Float.class, "showingness");
    private static final int SHOWINGNESS_ANIMATION_MS = 460;
    private ObjectAnimator mAnimator;
    private int[] mColors = null;
    private final ArgbEvaluator mEvaluator = new ArgbEvaluator();
    private Interpolator mInterpolator;
    private float mShowingness;
    private int mStartingLevel;

    /* renamed from: android.support.wearable.view.ProgressSpinner$1 */
    class C04511 extends Property<ProgressSpinner, Float> {
        C04511(Class type, String name) {
            super(type, name);
        }

        public void set(ProgressSpinner object, Float value) {
            object.setShowingness(value.floatValue());
        }

        public Float get(ProgressSpinner object) {
            return Float.valueOf(object.getShowingness());
        }
    }

    private class ProgressDrawable extends Drawable {
        static final float GROW_SHRINK_RATIO = 0.5f;
        static final float INNER_CIRCLE_MAX_SIZE = 0.64285713f;
        static final float INNER_CIRCLE_SHOW_END = 1.0f;
        static final float INNER_CIRCLE_SHOW_START = 0.4f;
        static final int INNER_RING_DEVISOR = 7;
        static final int MAX_LEVEL = 10000;
        static final float MIDDLE_CIRCLE_MAX_SIZE = 0.78571427f;
        static final float MIDDLE_CIRCLE_SHOW_END = 0.8f;
        static final float MIDDLE_CIRCLE_SHOW_START = 0.2f;
        static final int MIN_SEGMENTS = 4;
        static final float SHOW_STEP_VALUE = 0.2f;
        static final float STARTING_ANGLE = -90.0f;
        final Paint mForegroundPaint = new Paint();
        final RectF mInnerCircleBounds = new RectF();

        ProgressDrawable() {
            this.mForegroundPaint.setAntiAlias(true);
            this.mForegroundPaint.setColor(-1);
            this.mForegroundPaint.setStyle(Style.STROKE);
            this.mForegroundPaint.setStrokeCap(Cap.ROUND);
        }

        private void drawEditModeSample(Canvas c) {
            RectF bounds = new RectF(getBounds());
            bounds.inset(10.0f, 10.0f);
            this.mForegroundPaint.setColor(ProgressSpinner.this.mColors[0]);
            this.mForegroundPaint.setStrokeWidth(7.0f);
            c.drawArc(bounds, 0.0f, 270.0f, false, this.mForegroundPaint);
        }

        public void draw(Canvas c) {
            float baseRadius = (float) (getBounds().width() / 2);
            if (ProgressSpinner.this.isInEditMode()) {
                drawEditModeSample(c);
                return;
            }
            float strokeWidth;
            float innerRadius;
            float inset;
            if (ProgressSpinner.this.mShowingness < 1.0f) {
                strokeWidth = (MIDDLE_CIRCLE_MAX_SIZE * ProgressSpinner.this.mInterpolator.getInterpolation(ProgressSpinner.lerpInvSat(0.2f, MIDDLE_CIRCLE_SHOW_END, ProgressSpinner.this.mShowingness))) * baseRadius;
                innerRadius = (INNER_CIRCLE_MAX_SIZE * ProgressSpinner.this.mInterpolator.getInterpolation(ProgressSpinner.lerpInvSat(INNER_CIRCLE_SHOW_START, 1.0f, ProgressSpinner.this.mShowingness))) * baseRadius;
                inset = (baseRadius - strokeWidth) + ((strokeWidth - innerRadius) / 2.0f);
                strokeWidth -= innerRadius;
            } else {
                inset = (float) (getBounds().width() / 7);
                strokeWidth = (float) (getBounds().width() / 14);
            }
            r0.mInnerCircleBounds.set(getBounds());
            r0.mInnerCircleBounds.inset(inset, inset);
            r0.mForegroundPaint.setStrokeWidth(strokeWidth);
            int level = ((getLevel() + 10000) - ProgressSpinner.this.mStartingLevel) % 10000;
            innerRadius = 360.0f;
            int color = ProgressSpinner.this.mColors[0];
            boolean growing = false;
            float correctionAngle = 0.0f;
            float maxCorrectionAngle = 360.0f - 306.0f;
            if (ProgressSpinner.this.mShowingness < 1.0f) {
                innerRadius = 360.0f;
                float f = baseRadius;
            } else {
                float sweepAngle;
                int i;
                int mNumberOfSegments = ProgressSpinner.this.mColors.length;
                int mLevelsPerSegment = 10000 / Math.max(4, mNumberOfSegments);
                int i2 = 0;
                while (i2 < Math.max(4, mNumberOfSegments)) {
                    if (level <= (i2 + 1) * mLevelsPerSegment) {
                        int offset = i2 * mLevelsPerSegment;
                        float progress = ((float) (level - offset)) / ((float) mLevelsPerSegment);
                        growing = progress < GROW_SHRINK_RATIO;
                        correctionAngle = maxCorrectionAngle * progress;
                        if (growing) {
                            ProgressSpinner progressSpinner = ProgressSpinner.this;
                            sweepAngle = 360.0f;
                            color = progressSpinner.getColor(progress, GROW_SHRINK_RATIO, progressSpinner.mColors[i2 % ProgressSpinner.this.mColors.length], ProgressSpinner.this.mColors[(i2 + 1) % ProgressSpinner.this.mColors.length]);
                            innerRadius = 306.0f * ProgressSpinner.this.mInterpolator.getInterpolation(ProgressSpinner.lerpInv(0.0f, GROW_SHRINK_RATIO, progress));
                        } else {
                            i = mLevelsPerSegment;
                            sweepAngle = 360.0f;
                            color = ProgressSpinner.this.mColors[(i2 + 1) % ProgressSpinner.this.mColors.length];
                            innerRadius = 306.0f * (1.0f - ProgressSpinner.this.mInterpolator.getInterpolation(ProgressSpinner.lerpInv(GROW_SHRINK_RATIO, 1.0f, progress)));
                        }
                    } else {
                        sweepAngle = 360.0f;
                        i2++;
                        mLevelsPerSegment = mLevelsPerSegment;
                    }
                }
                i = mLevelsPerSegment;
                sweepAngle = 360.0f;
            }
            r0.mForegroundPaint.setColor(color);
            if (innerRadius < 1.0f) {
                innerRadius = 1.0f;
            }
            if (((double) strokeWidth) > 0.1d) {
                c.rotate(((((((float) level) * 1.0E-4f) * 2.0f) * 360.0f) + STARTING_ANGLE) + correctionAngle, r0.mInnerCircleBounds.centerX(), r0.mInnerCircleBounds.centerY());
                c.drawArc(r0.mInnerCircleBounds, growing ? 0.0f : 306.0f - innerRadius, innerRadius, false, r0.mForegroundPaint);
            } else {
                Canvas canvas = c;
            }
        }

        public int getOpacity() {
            return -3;
        }

        public void setAlpha(int alpha) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }
    }

    public ProgressSpinner(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ProgressSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ProgressSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyle) {
        if (!isInEditMode()) {
            this.mInterpolator = AnimationUtils.loadInterpolator(context, 17563654);
        }
        setIndeterminateDrawable(new ProgressDrawable());
        if (getVisibility() == 0) {
            this.mShowingness = 1.0f;
        }
        int[] colors = this.mColors;
        if (attrs != null) {
            colors = getColorsFromAttributes(context, attrs, defStyle);
        }
        if (colors == null) {
            if (isInEditMode()) {
                colors = new int[]{context.getResources().getColor(17170456)};
            } else {
                TypedArray typedArray = getResources().obtainTypedArray(C0395R.array.progress_spinner_sequence);
                colors = new int[typedArray.length()];
                for (int i = 0; i < typedArray.length(); i++) {
                    colors[i] = typedArray.getColor(i, 0);
                }
                typedArray.recycle();
            }
        }
        setColors(colors);
    }

    public void setVisibility(int visibility) {
        if (getVisibility() != visibility) {
            super.setVisibility(visibility);
            if (visibility != 0) {
                if (visibility != 4) {
                    if (visibility != 8) {
                        throw new IllegalArgumentException("Visibility only supports View.VISIBLE, View.INVISIBLE, or View.GONE");
                    }
                }
                setShowingness(0.0f);
                return;
            }
            setShowingness(1.0f);
        }
    }

    public void showWithAnimation() {
        showWithAnimation(0);
    }

    public void showWithAnimation(long delayMs) {
        showWithAnimation(delayMs, null);
    }

    public void showWithAnimation(long delayMs, @Nullable final AnimatorListenerAdapter listener) {
        ObjectAnimator objectAnimator = this.mAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.mAnimator = null;
        }
        if (getVisibility() != 0) {
            this.mAnimator = ObjectAnimator.ofFloat(this, SHOWINGNESS, new float[]{0.0f, 1.0f});
            this.mAnimator.setDuration(460);
            if (delayMs > 0) {
                this.mAnimator.setStartDelay(delayMs);
            }
            this.mAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animation) {
                    AnimatorListenerAdapter animatorListenerAdapter = listener;
                    if (animatorListenerAdapter != null) {
                        animatorListenerAdapter.onAnimationStart(animation);
                    }
                    super.setVisibility(0);
                }

                public void onAnimationEnd(Animator animation) {
                    ProgressSpinner progressSpinner = ProgressSpinner.this;
                    progressSpinner.mStartingLevel = progressSpinner.getIndeterminateDrawable().getLevel();
                    ProgressSpinner.this.mAnimator = null;
                    AnimatorListenerAdapter animatorListenerAdapter = listener;
                    if (animatorListenerAdapter != null) {
                        animatorListenerAdapter.onAnimationEnd(animation);
                    }
                }
            });
            this.mAnimator.start();
        } else if (listener != null) {
            listener.onAnimationStart(null);
            listener.onAnimationEnd(null);
        }
    }

    public void hide() {
        ObjectAnimator objectAnimator = this.mAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.mAnimator = null;
        }
        setVisibility(8);
    }

    public void hideWithAnimation() {
        hideWithAnimation(null);
    }

    public void hideWithAnimation(final AnimatorListenerAdapter listener) {
        ObjectAnimator objectAnimator = this.mAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.mAnimator = null;
        }
        if (getVisibility() == 0) {
            this.mAnimator = ObjectAnimator.ofFloat(this, SHOWINGNESS, new float[]{getShowingness(), 0.0f});
            this.mAnimator.setDuration((long) (getShowingness() * 460.0f));
            this.mAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    ProgressSpinner.this.setVisibility(8);
                    AnimatorListenerAdapter animatorListenerAdapter = listener;
                    if (animatorListenerAdapter != null) {
                        animatorListenerAdapter.onAnimationEnd(animation);
                    }
                }
            });
            this.mAnimator.start();
        } else if (listener != null) {
            listener.onAnimationEnd(null);
        }
    }

    private float getShowingness() {
        return this.mShowingness;
    }

    private void setShowingness(float showingness) {
        this.mShowingness = showingness;
        invalidate();
    }

    @Nullable
    private int[] getColorsFromAttributes(Context context, AttributeSet attrs, int defStyle) {
        int[] colors = null;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, C0395R.styleable.ProgressSpinner, defStyle, 0);
        if (typedArray.hasValue(C0395R.styleable.ProgressSpinner_color_sequence)) {
            try {
                colors = getResources().getIntArray(typedArray.getResourceId(C0395R.styleable.ProgressSpinner_color_sequence, 0));
            } catch (NotFoundException e) {
            }
            if (colors != null) {
                if (colors.length <= 0) {
                }
            }
            colors = new int[]{Integer.valueOf(typedArray.getColor(C0395R.styleable.ProgressSpinner_color_sequence, getResources().getColor(17170445))).intValue()};
        }
        typedArray.recycle();
        return colors;
    }

    public void setColors(int[] colors) {
        if (colors != null && colors.length > 0) {
            this.mColors = colors;
        }
    }

    private int getColor(float progress, float max, int color1, int color2) {
        return ((Integer) this.mEvaluator.evaluate(lerpInv(0.0f, max, progress), Integer.valueOf(color1), Integer.valueOf(color2))).intValue();
    }

    private static float lerpInvSat(float a, float b, float value) {
        return saturate(lerpInv(a, b, value));
    }

    private static float lerpInv(float a, float b, float value) {
        return a != b ? (value - a) / (b - a) : 0.0f;
    }

    private static float saturate(float value) {
        return clamp(value, 0.0f, 1.0f);
    }

    private static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}
