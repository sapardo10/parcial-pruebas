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
class ViewportDrawable extends Drawable implements Callback {
    private static final boolean DEBUG = false;
    private static final float STEP_SIZE_PCT = 0.2f;
    private static final String TAG = "ViewportDrawable";
    private int mAlpha;
    private int mCenterOffsetX;
    private int mCenterOffsetY;
    private int mChangingConfigs;
    private ColorFilter mColorFilter;
    private int mColorFilterColor;
    private Mode mColorFilterMode;
    private boolean mDither;
    private Drawable mDrawable;
    private final Rect mDrawableBounds;
    private boolean mFilterBitmap;
    private float mHeightStepSize;
    private int mMaxPosX;
    private int mMaxPosY;
    private float mPositionX;
    private float mPositionY;
    private float mScale;
    private int mSrcHeight;
    private int mSrcWidth;
    private float mWidthStepSize;

    public ViewportDrawable() {
        this(null);
    }

    public ViewportDrawable(Drawable drawable) {
        this.mAlpha = 255;
        this.mDither = true;
        this.mFilterBitmap = true;
        this.mMaxPosX = 2;
        this.mMaxPosY = 2;
        this.mPositionX = 1.0f;
        this.mPositionY = 1.0f;
        this.mDrawableBounds = new Rect();
        this.mScale = 1.0f;
        setDrawable(drawable);
    }

    public void setDrawable(Drawable drawable) {
        Drawable drawable2 = this.mDrawable;
        if (drawable2 != drawable) {
            if (drawable2 != null) {
                drawable2.setCallback(null);
            }
            this.mDrawable = drawable;
            drawable2 = this.mDrawable;
            if (drawable2 != null) {
                drawable2.setAlpha(getAlpha());
                updateDrawableBounds(getBounds());
                this.mDrawable.setCallback(this);
                ColorFilter colorFilter = this.mColorFilter;
                if (colorFilter != null) {
                    this.mDrawable.setColorFilter(colorFilter);
                }
                Mode mode = this.mColorFilterMode;
                if (mode != null) {
                    this.mDrawable.setColorFilter(this.mColorFilterColor, mode);
                }
                this.mDrawable.setDither(this.mDither);
                this.mDrawable.setFilterBitmap(this.mFilterBitmap);
                this.mDrawable.setState(getState());
                recomputeScale();
                invalidateSelf();
            }
        }
    }

    private void updateDrawableBounds(Rect bounds) {
        int w = this.mDrawable;
        if (w != 0) {
            w = w.getIntrinsicWidth();
            int h = this.mDrawable.getIntrinsicHeight();
            if (w == -1 || h == -1) {
                this.mDrawable.setBounds(bounds);
            } else {
                this.mDrawable.setBounds(bounds.left, bounds.top, bounds.left + w, bounds.top + h);
            }
        }
    }

    public void setPosition(float x, float y) {
        if (this.mPositionX == x) {
            if (this.mPositionY == y) {
                return;
            }
        }
        this.mPositionX = limit(x, 0, this.mMaxPosX);
        this.mPositionY = limit(y, 0, this.mMaxPosY);
        invalidateSelf();
    }

    public void setVerticalPosition(float y) {
        setPosition(this.mPositionX, y);
    }

    public void setHorizontalPosition(float x) {
        setPosition(x, this.mPositionY);
    }

    public void setStops(int xStops, int yStops) {
        int maxX = Math.max(0, xStops - 1);
        int maxY = Math.max(0, yStops - 1);
        if (maxX == this.mMaxPosX) {
            if (maxY == this.mMaxPosY) {
                return;
            }
        }
        this.mMaxPosX = maxX;
        this.mMaxPosY = maxY;
        this.mPositionX = limit(this.mPositionX, 0, this.mMaxPosX);
        this.mPositionY = limit(this.mPositionY, 0, this.mMaxPosY);
        recomputeScale();
        invalidateSelf();
    }

    public void setHorizontalStops(int stops) {
        setStops(stops, this.mMaxPosY + 1);
    }

    public void setVerticalStops(int stops) {
        setStops(this.mMaxPosX + 1, stops);
    }

    protected void onBoundsChange(Rect bounds) {
        this.mDrawableBounds.set(bounds);
        updateDrawableBounds(bounds);
        recomputeScale();
        invalidateSelf();
    }

    private void recomputeScale() {
        if (this.mDrawable != null) {
            if (this.mDrawableBounds.width() != 0) {
                if (this.mDrawableBounds.height() != 0) {
                    this.mSrcWidth = this.mDrawable.getIntrinsicWidth();
                    this.mSrcHeight = this.mDrawable.getIntrinsicHeight();
                    if (this.mSrcWidth != -1) {
                        if (this.mSrcHeight != -1) {
                            this.mWidthStepSize = ((float) this.mDrawableBounds.width()) * STEP_SIZE_PCT;
                            this.mHeightStepSize = ((float) this.mDrawableBounds.height()) * STEP_SIZE_PCT;
                            float minWidth = ((float) this.mDrawableBounds.width()) + (((float) this.mMaxPosX) * this.mWidthStepSize);
                            float minHeight = ((float) this.mDrawableBounds.height()) + (((float) this.mMaxPosY) * this.mHeightStepSize);
                            this.mScale = Math.max(minWidth / ((float) this.mSrcWidth), minHeight / ((float) this.mSrcHeight));
                            float scaledWidth = (float) this.mSrcWidth;
                            float f = this.mScale;
                            scaledWidth *= f;
                            float scaledHeight = ((float) this.mSrcHeight) * f;
                            if (scaledWidth > minWidth) {
                                this.mCenterOffsetX = (int) ((scaledWidth - minWidth) / 2.0f);
                                this.mCenterOffsetY = 0;
                            } else {
                                this.mCenterOffsetY = (int) ((scaledHeight - minHeight) / 2.0f);
                                this.mCenterOffsetX = 0;
                            }
                            return;
                        }
                    }
                    this.mSrcWidth = this.mDrawableBounds.width();
                    this.mSrcHeight = this.mDrawableBounds.height();
                    this.mScale = 1.0f;
                    this.mWidthStepSize = 0.0f;
                    this.mHeightStepSize = 0.0f;
                    this.mCenterOffsetX = 0;
                    this.mCenterOffsetY = 0;
                }
            }
        }
    }

    public void draw(Canvas canvas) {
        if (this.mDrawable != null) {
            canvas.save();
            canvas.clipRect(getBounds());
            canvas.translate(-(((float) this.mCenterOffsetX) + (this.mPositionX * this.mWidthStepSize)), -(((float) this.mCenterOffsetY) + (this.mPositionY * this.mHeightStepSize)));
            float f = this.mScale;
            canvas.scale(f, f);
            this.mDrawable.draw(canvas);
            canvas.restore();
        }
    }

    private static float limit(float value, int min, int max) {
        if (value < ((float) min)) {
            return (float) min;
        }
        if (value > ((float) max)) {
            return (float) max;
        }
        return value;
    }

    public void setFilterBitmap(boolean filter) {
        if (this.mFilterBitmap != filter) {
            this.mFilterBitmap = filter;
            Drawable drawable = this.mDrawable;
            if (drawable != null) {
                drawable.setFilterBitmap(filter);
            }
        }
    }

    public void setDither(boolean dither) {
        if (this.mDither != dither) {
            this.mDither = dither;
            Drawable drawable = this.mDrawable;
            if (drawable != null) {
                drawable.setDither(dither);
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
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            drawable.setColorFilter(color, mode);
        }
    }

    public void clearColorFilter() {
        if (this.mColorFilterMode != null) {
            this.mColorFilterMode = null;
            Drawable drawable = this.mDrawable;
            if (drawable != null) {
                drawable.clearColorFilter();
            }
        }
    }

    public void jumpToCurrentState() {
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    public void setChangingConfigurations(int configs) {
        if (this.mChangingConfigs != configs) {
            this.mChangingConfigs = configs;
            Drawable drawable = this.mDrawable;
            if (drawable != null) {
                drawable.setChangingConfigurations(configs);
            }
        }
    }

    public int getChangingConfigurations() {
        return this.mChangingConfigs;
    }

    protected boolean onStateChange(int[] state) {
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            return drawable.setState(state);
        }
        return false;
    }

    protected boolean onLevelChange(int level) {
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            return drawable.setLevel(level);
        }
        return false;
    }

    public boolean isStateful() {
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            return drawable.isStateful();
        }
        return false;
    }

    public int getAlpha() {
        return this.mAlpha;
    }

    public void setAlpha(int alpha) {
        if (this.mAlpha != alpha) {
            this.mAlpha = alpha;
            Drawable drawable = this.mDrawable;
            if (drawable != null) {
                drawable.setAlpha(alpha);
            }
        }
    }

    public void setColorFilter(ColorFilter cf) {
        if (this.mColorFilter != cf) {
            this.mColorFilter = cf;
            Drawable drawable = this.mDrawable;
            if (drawable != null) {
                drawable.setColorFilter(cf);
            }
        }
    }

    public int getOpacity() {
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            return drawable.getOpacity();
        }
        return 0;
    }

    public void invalidateDrawable(Drawable who) {
        if (who == this.mDrawable && getCallback() != null) {
            getCallback().invalidateDrawable(this);
        }
    }

    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        if (who == this.mDrawable && getCallback() != null) {
            getCallback().scheduleDrawable(this, what, when);
        }
    }

    public void unscheduleDrawable(Drawable who, Runnable what) {
        if (who == this.mDrawable && getCallback() != null) {
            getCallback().unscheduleDrawable(this, what);
        }
    }
}
