package me.zhanghai.android.materialprogressbar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ProgressBar;
import me.zhanghai.android.materialprogressbar.internal.DrawableCompat;

public class MaterialProgressBar extends ProgressBar {
    public static final int PROGRESS_STYLE_CIRCULAR = 0;
    public static final int PROGRESS_STYLE_HORIZONTAL = 1;
    private static final String TAG = MaterialProgressBar.class.getSimpleName();
    private int mProgressStyle;
    private TintInfo mProgressTint = new TintInfo();

    private static class TintInfo {
        boolean mHasTintList;
        boolean mHasTintMode;
        ColorStateList mTintList;
        Mode mTintMode;

        private TintInfo() {
        }
    }

    public MaterialProgressBar(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public MaterialProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public MaterialProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public MaterialProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, C1125R.styleable.MaterialProgressBar, defStyleAttr, defStyleRes);
        boolean showTrack = false;
        this.mProgressStyle = a.getInt(C1125R.styleable.MaterialProgressBar_mpb_progressStyle, 0);
        boolean setBothDrawables = a.getBoolean(C1125R.styleable.MaterialProgressBar_mpb_setBothDrawables, false);
        boolean useIntrinsicPadding = a.getBoolean(C1125R.styleable.MaterialProgressBar_mpb_useIntrinsicPadding, true);
        int i = C1125R.styleable.MaterialProgressBar_mpb_showTrack;
        if (this.mProgressStyle == 1) {
            showTrack = true;
        }
        showTrack = a.getBoolean(i, showTrack);
        if (a.hasValue(C1125R.styleable.MaterialProgressBar_android_tint)) {
            this.mProgressTint.mTintList = a.getColorStateList(C1125R.styleable.MaterialProgressBar_android_tint);
            this.mProgressTint.mHasTintList = true;
        }
        if (a.hasValue(C1125R.styleable.MaterialProgressBar_mpb_tintMode)) {
            this.mProgressTint.mTintMode = DrawableCompat.parseTintMode(a.getInt(C1125R.styleable.MaterialProgressBar_mpb_tintMode, -1), null);
            this.mProgressTint.mHasTintMode = true;
        }
        a.recycle();
        switch (this.mProgressStyle) {
            case 0:
                if (isIndeterminate() && !setBothDrawables) {
                    if (!isInEditMode()) {
                        setIndeterminateDrawable(new IndeterminateProgressDrawable(context));
                        break;
                    }
                    break;
                }
                throw new UnsupportedOperationException("Determinate circular drawable is not yet supported");
                break;
            case 1:
                if (!isIndeterminate()) {
                    if (!setBothDrawables) {
                        if (isIndeterminate()) {
                            if (setBothDrawables) {
                                break;
                            }
                        }
                        setProgressDrawable(new HorizontalProgressDrawable(context));
                        break;
                    }
                }
                if (!isInEditMode()) {
                    setIndeterminateDrawable(new IndeterminateHorizontalProgressDrawable(context));
                }
                if (isIndeterminate()) {
                    if (setBothDrawables) {
                    }
                }
                setProgressDrawable(new HorizontalProgressDrawable(context));
            default:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unknown progress style: ");
                stringBuilder.append(this.mProgressStyle);
                throw new IllegalArgumentException(stringBuilder.toString());
        }
        setUseIntrinsicPadding(useIntrinsicPadding);
        setShowTrack(showTrack);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        fixCanvasScalingWhenHardwareAccelerated();
    }

    private void fixCanvasScalingWhenHardwareAccelerated() {
        if (VERSION.SDK_INT >= 18) {
            return;
        }
        if (isHardwareAccelerated() && getLayerType() != 1) {
            setLayerType(1, null);
        }
    }

    public int getProgressStyle() {
        return this.mProgressStyle;
    }

    public Drawable getDrawable() {
        return isIndeterminate() ? getIndeterminateDrawable() : getProgressDrawable();
    }

    public boolean getUseIntrinsicPadding() {
        Drawable drawable = getDrawable();
        if (drawable instanceof IntrinsicPaddingDrawable) {
            return ((IntrinsicPaddingDrawable) drawable).getUseIntrinsicPadding();
        }
        throw new IllegalStateException("Drawable does not implement IntrinsicPaddingDrawable");
    }

    public void setUseIntrinsicPadding(boolean useIntrinsicPadding) {
        Drawable drawable = getDrawable();
        if (drawable instanceof IntrinsicPaddingDrawable) {
            ((IntrinsicPaddingDrawable) drawable).setUseIntrinsicPadding(useIntrinsicPadding);
        }
        Drawable indeterminateDrawable = getIndeterminateDrawable();
        if (indeterminateDrawable instanceof IntrinsicPaddingDrawable) {
            ((IntrinsicPaddingDrawable) indeterminateDrawable).setUseIntrinsicPadding(useIntrinsicPadding);
        }
    }

    public boolean getShowTrack() {
        Drawable drawable = getDrawable();
        if (drawable instanceof ShowTrackDrawable) {
            return ((ShowTrackDrawable) drawable).getShowTrack();
        }
        return false;
    }

    public void setShowTrack(boolean showTrack) {
        Drawable drawable = getDrawable();
        if (drawable instanceof ShowTrackDrawable) {
            ((ShowTrackDrawable) drawable).setShowTrack(showTrack);
        }
        Drawable indeterminateDrawable = getIndeterminateDrawable();
        if (indeterminateDrawable instanceof ShowTrackDrawable) {
            ((ShowTrackDrawable) indeterminateDrawable).setShowTrack(showTrack);
        }
    }

    public void setProgressDrawable(Drawable d) {
        super.setProgressDrawable(d);
        if (this.mProgressTint != null) {
            applyDeterminateProgressTint();
        }
    }

    public void setIndeterminateDrawable(Drawable d) {
        super.setIndeterminateDrawable(d);
        if (this.mProgressTint != null) {
            applyIndeterminateProgressTint();
        }
    }

    @Nullable
    public ColorStateList getProgressTintList() {
        return this.mProgressTint.mTintList;
    }

    public void setProgressTintList(@Nullable ColorStateList tint) {
        TintInfo tintInfo = this.mProgressTint;
        tintInfo.mTintList = tint;
        tintInfo.mHasTintList = true;
        applyProgressTint();
    }

    @Nullable
    public Mode getProgressTintMode() {
        return this.mProgressTint.mTintMode;
    }

    public void setProgressTintMode(@Nullable Mode tintMode) {
        TintInfo tintInfo = this.mProgressTint;
        tintInfo.mTintMode = tintMode;
        tintInfo.mHasTintMode = true;
        applyProgressTint();
    }

    private void applyProgressTint() {
        applyDeterminateProgressTint();
        applyIndeterminateProgressTint();
    }

    private void applyDeterminateProgressTint() {
        if (!this.mProgressTint.mHasTintList) {
            if (!this.mProgressTint.mHasTintMode) {
                return;
            }
        }
        Drawable drawable = getProgressDrawable();
        if (drawable != null) {
            applyTintForDrawable(drawable, this.mProgressTint);
        }
    }

    private void applyIndeterminateProgressTint() {
        if (!this.mProgressTint.mHasTintList) {
            if (!this.mProgressTint.mHasTintMode) {
                return;
            }
        }
        Drawable drawable = getIndeterminateDrawable();
        if (drawable != null) {
            applyTintForDrawable(drawable, this.mProgressTint);
        }
    }

    @SuppressLint({"NewApi"})
    private void applyTintForDrawable(Drawable drawable, TintInfo tint) {
        if (!tint.mHasTintList) {
            if (!tint.mHasTintMode) {
                return;
            }
        }
        if (tint.mHasTintList) {
            if (drawable instanceof TintableDrawable) {
                ((TintableDrawable) drawable).setTintList(tint.mTintList);
            } else {
                Log.w(TAG, "Drawable did not implement TintableDrawable, it won't be tinted below Lollipop");
                if (VERSION.SDK_INT >= 21) {
                    drawable.setTintList(tint.mTintList);
                }
            }
        }
        if (tint.mHasTintMode) {
            if (drawable instanceof TintableDrawable) {
                ((TintableDrawable) drawable).setTintMode(tint.mTintMode);
            } else {
                Log.w(TAG, "Drawable did not implement TintableDrawable, it won't be tinted below Lollipop");
                if (VERSION.SDK_INT >= 21) {
                    drawable.setTintMode(tint.mTintMode);
                }
            }
        }
        if (drawable.isStateful()) {
            drawable.setState(getDrawableState());
        }
    }
}
