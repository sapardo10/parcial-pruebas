package android.support.wearable.view;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.view.animation.LinearInterpolator;

@TargetApi(20)
class ProgressDrawable extends Drawable {
    private static final long ANIMATION_DURATION = 6000;
    private static final int CORRECTION_ANGLE = 54;
    private static final int FULL_CIRCLE = 360;
    private static final float GROW_SHRINK_RATIO = 0.5f;
    private static final Property<ProgressDrawable, Integer> LEVEL = new C04501(Integer.class, "level");
    private static final int LEVELS_PER_SEGMENT = 2000;
    private static final int MAX_LEVEL = 10000;
    private static final int MAX_SWEEP = 306;
    private static final int NUMBER_OF_SEGMENTS = 5;
    private static final float STARTING_ANGLE = -90.0f;
    private static final TimeInterpolator mInterpolator = Gusterpolator.INSTANCE;
    private final ObjectAnimator mAnimator;
    private int mCircleBorderColor;
    private float mCircleBorderWidth;
    private final RectF mInnerCircleBounds = new RectF();
    private final Paint mPaint = new Paint();

    /* renamed from: android.support.wearable.view.ProgressDrawable$1 */
    class C04501 extends Property<ProgressDrawable, Integer> {
        C04501(Class type, String name) {
            super(type, name);
        }

        public Integer get(ProgressDrawable drawable) {
            return Integer.valueOf(drawable.getLevel());
        }

        public void set(ProgressDrawable drawable, Integer value) {
            drawable.setLevel(value.intValue());
            drawable.invalidateSelf();
        }
    }

    public ProgressDrawable() {
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStyle(Style.STROKE);
        this.mAnimator = ObjectAnimator.ofInt(this, LEVEL, new int[]{0, 10000});
        this.mAnimator.setRepeatCount(-1);
        this.mAnimator.setRepeatMode(1);
        this.mAnimator.setDuration(ANIMATION_DURATION);
        this.mAnimator.setInterpolator(new LinearInterpolator());
    }

    public void setRingColor(int color) {
        this.mCircleBorderColor = color;
    }

    public void setRingWidth(float width) {
        this.mCircleBorderWidth = width;
    }

    public void startAnimation() {
        if (!this.mAnimator.isStarted()) {
            this.mAnimator.start();
        }
    }

    public void stopAnimation() {
        this.mAnimator.cancel();
    }

    public void draw(Canvas canvas) {
        float sweepAngle;
        canvas.save();
        this.mInnerCircleBounds.set(getBounds());
        RectF rectF = this.mInnerCircleBounds;
        float f = this.mCircleBorderWidth;
        rectF.inset(f / 2.0f, f / 2.0f);
        this.mPaint.setStrokeWidth(this.mCircleBorderWidth);
        this.mPaint.setColor(this.mCircleBorderColor);
        int level = getLevel();
        float progress = ((float) (level - ((level / 2000) * 2000))) / 2000.0f;
        boolean growing = progress < GROW_SHRINK_RATIO;
        float correctionAngle = 54.0f * progress;
        if (growing) {
            sweepAngle = mInterpolator.getInterpolation(lerpInv(0.0f, GROW_SHRINK_RATIO, progress)) * 306.0f;
        } else {
            sweepAngle = (1.0f - mInterpolator.getInterpolation(lerpInv(GROW_SHRINK_RATIO, 1.0f, progress))) * 306.0f;
        }
        sweepAngle = Math.max(1.0f, sweepAngle);
        canvas.rotate(((((((float) level) * 1.0E-4f) * 2.0f) * 360.0f) + STARTING_ANGLE) + correctionAngle, r0.mInnerCircleBounds.centerX(), r0.mInnerCircleBounds.centerY());
        canvas.drawArc(r0.mInnerCircleBounds, growing ? 0.0f : 306.0f - sweepAngle, sweepAngle, false, r0.mPaint);
        canvas.restore();
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public int getOpacity() {
        return -1;
    }

    protected boolean onLevelChange(int level) {
        return true;
    }

    private static float lerpInv(float a, float b, float value) {
        return a != b ? (value - a) / (b - a) : 0.0f;
    }
}
