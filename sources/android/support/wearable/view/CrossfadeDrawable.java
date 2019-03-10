package android.support.wearable.view;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;

@TargetApi(20)
@Deprecated
public class CrossfadeDrawable extends Drawable implements Callback {
    private int mAlpha;
    private Drawable mBase;
    private int mChangingConfigs;
    private ColorFilter mColorFilter;
    private int mColorFilterColor;
    private Mode mColorFilterMode;
    private boolean mDither;
    private Drawable mFading;
    private boolean mFilterBitmap;
    private float mProgress;

    public void setFading(Drawable d) {
        Drawable drawable = this.mFading;
        if (drawable != d) {
            if (drawable != null) {
                drawable.setCallback(null);
            }
            this.mFading = d;
            if (d != null) {
                initDrawable(d);
            }
            invalidateSelf();
        }
    }

    public void setBase(Drawable d) {
        Drawable drawable = this.mBase;
        if (drawable != d) {
            if (drawable != null) {
                drawable.setCallback(null);
            }
            this.mBase = d;
            initDrawable(d);
            invalidateSelf();
        }
    }

    public void setProgress(float progress) {
        float updated = Func.clamp(progress, (int) 0.0f, 1);
        if (updated != this.mProgress) {
            this.mProgress = updated;
            invalidateSelf();
        }
    }

    private void initDrawable(Drawable d) {
        d.setCallback(this);
        d.setState(getState());
        ColorFilter colorFilter = this.mColorFilter;
        if (colorFilter != null) {
            d.setColorFilter(colorFilter);
        }
        Mode mode = this.mColorFilterMode;
        if (mode != null) {
            d.setColorFilter(this.mColorFilterColor, mode);
        }
        d.setDither(this.mDither);
        d.setFilterBitmap(this.mFilterBitmap);
        d.setBounds(getBounds());
    }

    public void draw(Canvas canvas) {
        if (this.mBase != null && (this.mProgress < 1.0f || this.mFading == null)) {
            this.mBase.setAlpha(255);
            this.mBase.draw(canvas);
        }
        Drawable drawable = this.mFading;
        if (drawable != null) {
            float f = this.mProgress;
            if (f > 0.0f) {
                drawable.setAlpha((int) (f * 255.0f));
                this.mFading.draw(canvas);
            }
        }
    }

    public int getIntrinsicWidth() {
        int fading = this.mFading;
        int base = -1;
        fading = fading == 0 ? -1 : fading.getIntrinsicWidth();
        Drawable drawable = this.mBase;
        if (drawable != null) {
            base = drawable.getIntrinsicHeight();
        }
        return Math.max(fading, base);
    }

    public int getIntrinsicHeight() {
        int fading = this.mFading;
        int base = -1;
        fading = fading == 0 ? -1 : fading.getIntrinsicHeight();
        Drawable drawable = this.mBase;
        if (drawable != null) {
            base = drawable.getIntrinsicHeight();
        }
        return Math.max(fading, base);
    }

    protected void onBoundsChange(Rect bounds) {
        Drawable drawable = this.mBase;
        if (drawable != null) {
            drawable.setBounds(bounds);
        }
        drawable = this.mFading;
        if (drawable != null) {
            drawable.setBounds(bounds);
        }
        invalidateSelf();
    }

    public void jumpToCurrentState() {
        Drawable drawable = this.mFading;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
        drawable = this.mBase;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    public void setChangingConfigurations(int configs) {
        if (this.mChangingConfigs != configs) {
            this.mChangingConfigs = configs;
            Drawable drawable = this.mFading;
            if (drawable != null) {
                drawable.setChangingConfigurations(configs);
            }
            drawable = this.mBase;
            if (drawable != null) {
                drawable.setChangingConfigurations(configs);
            }
        }
    }

    public void setFilterBitmap(boolean filter) {
        if (this.mFilterBitmap != filter) {
            this.mFilterBitmap = filter;
            Drawable drawable = this.mFading;
            if (drawable != null) {
                drawable.setFilterBitmap(filter);
            }
            drawable = this.mBase;
            if (drawable != null) {
                drawable.setFilterBitmap(filter);
            }
        }
    }

    public void setDither(boolean dither) {
        if (this.mDither != dither) {
            this.mDither = dither;
            Drawable drawable = this.mFading;
            if (drawable != null) {
                drawable.setDither(dither);
            }
            drawable = this.mBase;
            if (drawable != null) {
                drawable.setDither(dither);
            }
        }
    }

    public void setColorFilter(ColorFilter cf) {
        if (this.mColorFilter != cf) {
            this.mColorFilter = cf;
            Drawable drawable = this.mFading;
            if (drawable != null) {
                drawable.setColorFilter(cf);
            }
            drawable = this.mBase;
            if (drawable != null) {
                drawable.setColorFilter(cf);
            }
        }
    }

    public void setColorFilter(int color, Mode mode) {
        if (this.mColorFilterColor == color) {
            if (this.mColorFilterMode == mode) {
                return;
            }
        }
        this.mColorFilterColor = color;
        this.mColorFilterMode = mode;
        Drawable drawable = this.mFading;
        if (drawable != null) {
            drawable.setColorFilter(color, mode);
        }
        drawable = this.mBase;
        if (drawable != null) {
            drawable.setColorFilter(color, mode);
        }
    }

    public void clearColorFilter() {
        if (this.mColorFilterMode != null) {
            this.mColorFilterMode = null;
            Drawable drawable = this.mFading;
            if (drawable != null) {
                drawable.clearColorFilter();
            }
            drawable = this.mBase;
            if (drawable != null) {
                drawable.clearColorFilter();
            }
        }
    }

    public int getChangingConfigurations() {
        return this.mChangingConfigs;
    }

    protected boolean onStateChange(int[] state) {
        boolean changed = false;
        Drawable drawable = this.mFading;
        if (drawable != null) {
            changed = false | drawable.setState(state);
        }
        drawable = this.mBase;
        if (drawable != null) {
            return changed | drawable.setState(state);
        }
        return changed;
    }

    protected boolean onLevelChange(int level) {
        boolean changed = false;
        Drawable drawable = this.mFading;
        if (drawable != null) {
            changed = false | drawable.setLevel(level);
        }
        drawable = this.mBase;
        if (drawable != null) {
            return changed | drawable.setLevel(level);
        }
        return changed;
    }

    public boolean isStateful() {
        Drawable drawable = this.mFading;
        if (drawable != null) {
            if (!drawable.isStateful()) {
            }
        }
        drawable = this.mBase;
        return drawable != null && drawable.isStateful();
    }

    public int getAlpha() {
        return this.mAlpha;
    }

    public void setAlpha(int alpha) {
        if (alpha != this.mAlpha) {
            this.mAlpha = alpha;
            invalidateSelf();
        }
    }

    public Drawable getBase() {
        return this.mBase;
    }

    public Drawable getFading() {
        return this.mFading;
    }

    public int getOpacity() {
        Drawable drawable = this.mFading;
        int i = 0;
        int opacity = drawable == null ? 0 : drawable.getOpacity();
        Drawable drawable2 = this.mBase;
        if (drawable2 != null) {
            i = drawable2.getOpacity();
        }
        return resolveOpacity(opacity, i);
    }

    public void invalidateDrawable(Drawable who) {
        if ((who == this.mFading || who == this.mBase) && getCallback() != null) {
            getCallback().invalidateDrawable(this);
        }
    }

    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        if ((who == this.mFading || who == this.mBase) && getCallback() != null) {
            getCallback().scheduleDrawable(this, what, when);
        }
    }

    public void unscheduleDrawable(Drawable who, Runnable what) {
        if ((who == this.mFading || who == this.mBase) && getCallback() != null) {
            getCallback().unscheduleDrawable(this, what);
        }
    }
}
