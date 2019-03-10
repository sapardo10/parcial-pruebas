package android.support.wearable.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.wearable.C0395R;
import android.util.AttributeSet;
import android.util.Property;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.Iterator;

@TargetApi(20)
@Deprecated
public class ActionChooserView extends View {
    private static final int ANIMATION_STATE_DISABLED = 2;
    private static final int ANIMATION_STATE_DISABLING = 1;
    private static final int ANIMATION_STATE_ENABLED = 0;
    private static final Property<ActionChooserView, Float> OFFSET = new C04346(Float.class, "offset");
    public static final int OPTION_END = 2;
    public static final int OPTION_START = 1;
    private static final Property<ActionChooserView, Float> SELECTED_MULTIPLIER = new C04357(Float.class, "selected_multiplier");
    private final float mAnimMaxOffset;
    private int mAnimationState;
    private final float mBaseRadiusPercentage;
    private final int mBounceAnimationDuration;
    private final int mBounceDelay;
    private final ObjectAnimator mCenterAnimator;
    private final Paint mCirclePaint;
    private final int mConfirmationDelay;
    private final ObjectAnimator mExpandAnimator;
    private final boolean mExpandSelected;
    private final long mExpandToFullMillis;
    private final GestureDetector mGestureDetector;
    private final float mIconHeightPercentage;
    private final float mIdleAnimationSpeed;
    private final AnimatorSet mIdleAnimatorSet;
    private float mLastTouchOffset;
    private float mLastTouchX;
    private ArrayList<ActionChooserListener> mListeners;
    private final float mMaxRadiusPercentage;
    private final float mMinDragSelectPercent;
    private final float mMinSwipeSelectPercent;
    private float mOffset;
    private final SparseArray<Option> mOptions;
    private final ObjectAnimator mReturnAnimator;
    private final Runnable mSelectOptionRunnable;
    private float mSelectedMultiplier;
    private Integer mSelectedOption;
    private float mSelectedPercent;
    private float mSpeed;
    private final boolean mSymmetricalDimens;
    private boolean mTouchedEnabled;

    /* renamed from: android.support.wearable.view.ActionChooserView$1 */
    class C04291 implements Runnable {
        C04291() {
        }

        public void run() {
            if (ActionChooserView.this.mListeners != null) {
                Iterator it = ActionChooserView.this.mListeners.iterator();
                while (it.hasNext()) {
                    ((ActionChooserListener) it.next()).onOptionChosen(ActionChooserView.this.mSelectedOption.intValue());
                }
            }
        }
    }

    /* renamed from: android.support.wearable.view.ActionChooserView$2 */
    class C04302 implements AnimatorListener {
        private boolean mCancelled;

        C04302() {
        }

        public void onAnimationStart(Animator animator) {
            this.mCancelled = false;
        }

        public void onAnimationEnd(Animator animator) {
            if (!this.mCancelled && ActionChooserView.this.mAnimationState == 0) {
                ActionChooserView.this.mIdleAnimatorSet.start();
            }
        }

        public void onAnimationCancel(Animator animator) {
            this.mCancelled = true;
        }

        public void onAnimationRepeat(Animator animator) {
        }
    }

    /* renamed from: android.support.wearable.view.ActionChooserView$3 */
    class C04313 implements AnimatorListener {
        private boolean mCancelled;

        C04313() {
        }

        public void onAnimationStart(Animator animator) {
            this.mCancelled = false;
        }

        public void onAnimationEnd(Animator animator) {
            if (!this.mCancelled && ActionChooserView.this.mAnimationState == 0) {
                ActionChooserView.this.mIdleAnimatorSet.start();
            }
        }

        public void onAnimationCancel(Animator animator) {
            this.mCancelled = true;
        }

        public void onAnimationRepeat(Animator animator) {
        }
    }

    /* renamed from: android.support.wearable.view.ActionChooserView$4 */
    class C04324 extends SimpleOnGestureListener {
        C04324() {
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(e2.getX() - e1.getX()) < ((float) ActionChooserView.this.getMeasuredWidth()) * ActionChooserView.this.mMinSwipeSelectPercent) {
                return false;
            }
            ActionChooserView.this.selectOption(velocityX < 0.0f ? 2 : 1);
            ActionChooserView.this.enableAnimations(true);
            return true;
        }
    }

    /* renamed from: android.support.wearable.view.ActionChooserView$5 */
    class C04335 implements AnimatorListener {
        C04335() {
        }

        public void onAnimationStart(Animator animator) {
        }

        public void onAnimationEnd(Animator animator) {
            ActionChooserView actionChooserView = ActionChooserView.this;
            actionChooserView.removeCallbacks(actionChooserView.mSelectOptionRunnable);
            actionChooserView = ActionChooserView.this;
            actionChooserView.postDelayed(actionChooserView.mSelectOptionRunnable, (long) ActionChooserView.this.mConfirmationDelay);
        }

        public void onAnimationCancel(Animator animator) {
        }

        public void onAnimationRepeat(Animator animator) {
        }
    }

    /* renamed from: android.support.wearable.view.ActionChooserView$6 */
    class C04346 extends Property<ActionChooserView, Float> {
        C04346(Class type, String name) {
            super(type, name);
        }

        public Float get(ActionChooserView view) {
            return Float.valueOf(view.getOffset());
        }

        public void set(ActionChooserView view, Float value) {
            view.setAnimationOffset(value.floatValue());
        }
    }

    /* renamed from: android.support.wearable.view.ActionChooserView$7 */
    class C04357 extends Property<ActionChooserView, Float> {
        C04357(Class type, String name) {
            super(type, name);
        }

        public Float get(ActionChooserView view) {
            return Float.valueOf(view.getSelectedMultiplier());
        }

        public void set(ActionChooserView view, Float value) {
            view.setSelectedMultiplier(value.floatValue());
        }
    }

    @Deprecated
    public interface ActionChooserListener {
        void onOptionChosen(int i);

        void onOptionProgress(float f);
    }

    private static class Option {
        public int color;
        public Drawable icon;

        public Option(int color, Drawable icon) {
            this.color = color;
            this.icon = icon;
        }
    }

    public ActionChooserView(Context context) {
        this(context, null);
    }

    public ActionChooserView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionChooserView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mSelectOptionRunnable = new C04291();
        this.mSelectedMultiplier = 1.0f;
        this.mTouchedEnabled = true;
        this.mAnimationState = 0;
        this.mCirclePaint = new Paint();
        this.mCirclePaint.setAntiAlias(true);
        this.mCirclePaint.setStyle(Style.FILL);
        TypedValue val = new TypedValue();
        getResources().getValue(C0395R.dimen.action_chooser_bounce_in_percent, val, true);
        this.mAnimMaxOffset = val.getFloat();
        getResources().getValue(C0395R.dimen.action_chooser_base_radius_percent, val, true);
        this.mBaseRadiusPercentage = val.getFloat();
        getResources().getValue(C0395R.dimen.action_chooser_max_radius_percent, val, true);
        this.mMaxRadiusPercentage = val.getFloat();
        getResources().getValue(C0395R.dimen.action_chooser_icon_height_percent, val, true);
        this.mIconHeightPercentage = val.getFloat();
        getResources().getValue(C0395R.dimen.action_chooser_min_drag_select_percent, val, true);
        this.mMinDragSelectPercent = val.getFloat();
        getResources().getValue(C0395R.dimen.action_chooser_min_swipe_select_percent, val, true);
        this.mMinSwipeSelectPercent = val.getFloat();
        this.mBounceAnimationDuration = getResources().getInteger(C0395R.integer.action_chooser_anim_duration);
        this.mBounceDelay = getResources().getInteger(C0395R.integer.action_chooser_bounce_delay);
        this.mIdleAnimationSpeed = this.mMaxRadiusPercentage / ((float) this.mBounceAnimationDuration);
        this.mConfirmationDelay = getResources().getInteger(C0395R.integer.action_chooser_confirmation_duration);
        this.mExpandSelected = getResources().getBoolean(C0395R.bool.action_choose_expand_selected);
        this.mSymmetricalDimens = getResources().getBoolean(C0395R.bool.action_choose_symmetrical_dimen);
        this.mExpandToFullMillis = (long) getResources().getInteger(C0395R.integer.action_choose_expand_full_duration);
        this.mOptions = new SparseArray();
        ArrayList<Animator> bounceAnimators = new ArrayList();
        bounceAnimators.addAll(generateOptionAnimation(1));
        bounceAnimators.addAll(generateOptionAnimation(2));
        this.mIdleAnimatorSet = new AnimatorSet();
        this.mIdleAnimatorSet.playSequentially(bounceAnimators);
        this.mIdleAnimatorSet.addListener(new C04302());
        this.mReturnAnimator = ObjectAnimator.ofFloat(this, OFFSET, new float[]{0.0f});
        this.mReturnAnimator.addListener(new C04313());
        this.mCenterAnimator = ObjectAnimator.ofFloat(this, OFFSET, new float[]{0.0f});
        this.mExpandAnimator = ObjectAnimator.ofFloat(this, SELECTED_MULTIPLIER, new float[]{1.0f, (float) Math.sqrt(2.0d)});
        this.mGestureDetector = new GestureDetector(getContext(), new C04324());
    }

    private ArrayList<Animator> generateOptionAnimation(int option) {
        ArrayList<Animator> returnList = new ArrayList();
        int direction = option == 1 ? 1 : -1;
        ObjectAnimator bounceIn = ObjectAnimator.ofFloat(this, OFFSET, new float[]{0.0f, ((float) direction) * this.mAnimMaxOffset});
        bounceIn.setDuration((long) this.mBounceAnimationDuration);
        bounceIn.setStartDelay((long) this.mBounceDelay);
        returnList.add(bounceIn);
        ObjectAnimator bounceout = ObjectAnimator.ofFloat(this, OFFSET, new float[]{((float) direction) * this.mAnimMaxOffset, 0.0f});
        bounceIn.setDuration((long) this.mBounceAnimationDuration);
        bounceIn.setStartDelay((long) this.mBounceDelay);
        returnList.add(bounceout);
        return returnList;
    }

    private void selectOption(int option) {
        this.mSelectedOption = Integer.valueOf(option);
        this.mTouchedEnabled = false;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mIdleAnimatorSet.start();
    }

    protected void onDetachedFromWindow() {
        this.mIdleAnimatorSet.cancel();
        super.onDetachedFromWindow();
    }

    private boolean validateOption(int option) {
        if (option != 1) {
            return option == 2;
        } else {
            return true;
        }
    }

    public void addListener(ActionChooserListener listener) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList();
        }
        if (!this.mListeners.contains(listener)) {
            this.mListeners.add(listener);
        }
    }

    public void removeListener(ActionChooserListener listener) {
        ArrayList arrayList = this.mListeners;
        if (arrayList != null) {
            arrayList.remove(listener);
        }
    }

    public void setOption(int option, Drawable drawable, int color) {
        if (validateOption(option)) {
            this.mOptions.put(option, new Option(color, drawable));
            invalidate();
            return;
        }
        throw new IllegalArgumentException("unrecognized option");
    }

    public void performSelectOption(int option) {
        if (!validateOption(option)) {
            throw new IllegalArgumentException("unrecognized option");
        } else if (option == 1) {
            selectOption(1);
            enableAnimations(true);
        } else if (option == 2) {
            selectOption(2);
            enableAnimations(true);
        }
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        layoutOption((Option) this.mOptions.get(1));
        layoutOption((Option) this.mOptions.get(2));
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = canvas.getWidth();
        int height = this.mSymmetricalDimens ? Math.max(width, canvas.getHeight()) : canvas.getHeight();
        int shift = Math.round(((float) width) * r6.mOffset);
        int baseRadius = Math.round(((float) height) * r6.mBaseRadiusPercentage);
        float maxRadius = ((float) height) * r6.mMaxRadiusPercentage;
        drawOption(canvas, (Option) r6.mOptions.get(1), shift - baseRadius, height / 2, getCircleRadius(baseRadius, r6.mOffset, maxRadius, isSelected(1), r6.mSelectedMultiplier));
        drawOption(canvas, (Option) r6.mOptions.get(2), (shift + width) + baseRadius, height / 2, getCircleRadius(baseRadius, -r6.mOffset, maxRadius, isSelected(2), r6.mSelectedMultiplier));
    }

    private boolean isSelected(int option) {
        Integer num = this.mSelectedOption;
        return num != null && num.intValue() == option;
    }

    private float getCircleRadius(int baseRadius, float offset, float maxRadius, boolean selected, float selectedMultiplier) {
        return (selected ? selectedMultiplier : 1.0f) * (((float) baseRadius) + Math.max(0.0f, ((offset - this.mAnimMaxOffset) / (getMaxOffset() - this.mAnimMaxOffset)) * (maxRadius - ((float) baseRadius))));
    }

    private void layoutOption(Option option) {
        if (option != null) {
            Rect bounds = option.icon.getBounds();
            float scale = (((this.mIconHeightPercentage * 2.0f) * this.mBaseRadiusPercentage) * ((float) getMeasuredHeight())) / ((float) Math.max(option.icon.getIntrinsicHeight(), option.icon.getIntrinsicHeight()));
            bounds.left = 0;
            bounds.top = 0;
            bounds.right = Math.round(((float) option.icon.getIntrinsicWidth()) * scale);
            bounds.bottom = Math.round(((float) option.icon.getIntrinsicHeight()) * scale);
        }
    }

    private void drawOption(Canvas canvas, Option option, int cX, int cY, float radius) {
        if (option != null) {
            this.mCirclePaint.setColor(option.color);
            canvas.drawCircle((float) cX, (float) cY, radius, this.mCirclePaint);
            if (option.icon != null) {
                Rect bounds = option.icon.getBounds();
                bounds.offsetTo(cX - (bounds.width() / 2), cY - (bounds.height() / 2));
                option.icon.setBounds(bounds);
                option.icon.draw(canvas);
            }
        }
    }

    public boolean canScrollHorizontally(int direction) {
        return true;
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent event) {
        if (!this.mTouchedEnabled) {
            return false;
        }
        if (this.mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        switch (event.getAction() & 255) {
            case 0:
                enableAnimations(false);
                this.mLastTouchX = event.getX();
                this.mLastTouchOffset = getOffset();
                break;
            case 1:
            case 3:
                if (Math.abs(event.getX() - this.mLastTouchX) >= ((float) getMeasuredWidth()) * this.mMinDragSelectPercent) {
                    selectOption(event.getX() < this.mLastTouchX ? 2 : 1);
                }
                enableAnimations(true);
                break;
            case 2:
                float delta = event.getX() - this.mLastTouchX;
                this.mSpeed = Math.abs(((delta / ((float) getWidth())) - this.mLastTouchOffset) / ((float) (event.getEventTime() - event.getDownTime())));
                setOffset(this.mLastTouchOffset + (delta / ((float) getWidth())));
                break;
            default:
                break;
        }
        return true;
    }

    private float getOffset() {
        return this.mOffset;
    }

    private float getSelectedMultiplier() {
        return this.mSelectedMultiplier;
    }

    private void setSelectedMultiplier(float percent) {
        this.mSelectedMultiplier = percent;
        invalidate();
    }

    private float getMaxOffset() {
        return this.mBaseRadiusPercentage + 0.5f;
    }

    private void setAnimationOffset(float offset) {
        if (this.mAnimationState != 2) {
            setOffset(offset);
        }
    }

    private void setOffset(float offset) {
        int direction = offset < 0.0f ? -1 : 1;
        if (this.mAnimationState == 1 && Math.abs(offset) == 0.0f) {
            enableAnimations(false, true);
            setOffsetAndNotify(0.0f);
            invalidate();
            return;
        }
        setOffsetAndNotify(((float) direction) * Math.min(Math.abs(offset), getMaxOffset()));
        if (Math.abs(this.mOffset) >= getMaxOffset()) {
            this.mSelectedOption = Integer.valueOf(direction < 0 ? 2 : 1);
            if (this.mOptions.indexOfKey(this.mSelectedOption.intValue()) > -1) {
                this.mTouchedEnabled = false;
                enableAnimations(false, true);
                if (this.mExpandSelected) {
                    this.mExpandAnimator.setDuration(this.mExpandToFullMillis);
                    this.mExpandAnimator.addListener(new C04335());
                    this.mExpandAnimator.start();
                } else {
                    removeCallbacks(this.mSelectOptionRunnable);
                    postDelayed(this.mSelectOptionRunnable, (long) this.mConfirmationDelay);
                }
            }
        }
        invalidate();
    }

    private void setOffsetAndNotify(float newOffset) {
        if (newOffset != this.mOffset) {
            this.mOffset = newOffset;
            float percent = Math.max(0.0f, (Math.abs(newOffset) - this.mAnimMaxOffset) / (getMaxOffset() - this.mAnimMaxOffset));
            if (this.mSelectedPercent != percent) {
                this.mSelectedPercent = percent;
                Iterator it = this.mListeners.iterator();
                while (it.hasNext()) {
                    ((ActionChooserListener) it.next()).onOptionProgress(this.mSelectedPercent);
                }
            }
        }
    }

    public void setEnabled(boolean enabled) {
        boolean oldEnabled = isEnabled();
        super.setEnabled(enabled);
        if (oldEnabled != enabled) {
            this.mTouchedEnabled = enabled;
            enableAnimations(enabled, enabled);
        }
    }

    private void enableAnimations(boolean enabled) {
        enableAnimations(enabled, true);
    }

    private void enableAnimations(boolean enabled, boolean immediate) {
        if (enabled) {
            this.mAnimationState = 0;
            if (this.mSelectedOption != null) {
                this.mIdleAnimatorSet.cancel();
                this.mCenterAnimator.cancel();
                this.mReturnAnimator.cancel();
                ObjectAnimator objectAnimator = this.mCenterAnimator;
                float[] fArr = new float[2];
                fArr[0] = getOffset();
                fArr[1] = getMaxOffset() * ((float) (this.mSelectedOption.intValue() == 2 ? -1 : 1));
                objectAnimator.setFloatValues(fArr);
                this.mCenterAnimator.setDuration((long) Math.round((Math.abs(getMaxOffset()) - Math.abs(getOffset())) / Math.max(this.mIdleAnimationSpeed, this.mSpeed)));
                this.mCenterAnimator.start();
            } else if (this.mOffset == 0.0f) {
                this.mIdleAnimatorSet.start();
            } else {
                this.mReturnAnimator.setFloatValues(new float[]{getOffset(), 0.0f});
                this.mReturnAnimator.setDuration((long) Math.round(Math.abs(currentOffset / this.mIdleAnimationSpeed)));
                this.mReturnAnimator.start();
            }
        } else if (immediate) {
            this.mAnimationState = 2;
            this.mIdleAnimatorSet.cancel();
            this.mCenterAnimator.cancel();
            this.mReturnAnimator.cancel();
        } else {
            this.mAnimationState = 1;
        }
    }
}
