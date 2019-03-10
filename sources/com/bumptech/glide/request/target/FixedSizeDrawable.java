package com.bumptech.glide.request.target;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.Drawable.ConstantState;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import com.bumptech.glide.util.Preconditions;

public class FixedSizeDrawable extends Drawable {
    private final RectF bounds;
    private final Matrix matrix;
    private boolean mutated;
    private State state;
    private Drawable wrapped;
    private final RectF wrappedRect;

    static final class State extends ConstantState {
        final int height;
        final int width;
        private final ConstantState wrapped;

        State(State other) {
            this(other.wrapped, other.width, other.height);
        }

        State(ConstantState wrapped, int width, int height) {
            this.wrapped = wrapped;
            this.width = width;
            this.height = height;
        }

        @NonNull
        public Drawable newDrawable() {
            return new FixedSizeDrawable(this, this.wrapped.newDrawable());
        }

        @NonNull
        public Drawable newDrawable(Resources res) {
            return new FixedSizeDrawable(this, this.wrapped.newDrawable(res));
        }

        public int getChangingConfigurations() {
            return 0;
        }
    }

    public FixedSizeDrawable(Drawable wrapped, int width, int height) {
        this(new State(wrapped.getConstantState(), width, height), wrapped);
    }

    FixedSizeDrawable(State state, Drawable wrapped) {
        this.state = (State) Preconditions.checkNotNull(state);
        this.wrapped = (Drawable) Preconditions.checkNotNull(wrapped);
        wrapped.setBounds(0, 0, wrapped.getIntrinsicWidth(), wrapped.getIntrinsicHeight());
        this.matrix = new Matrix();
        this.wrappedRect = new RectF(0.0f, 0.0f, (float) wrapped.getIntrinsicWidth(), (float) wrapped.getIntrinsicHeight());
        this.bounds = new RectF();
    }

    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        this.bounds.set((float) left, (float) top, (float) right, (float) bottom);
        updateMatrix();
    }

    public void setBounds(@NonNull Rect bounds) {
        super.setBounds(bounds);
        this.bounds.set(bounds);
        updateMatrix();
    }

    private void updateMatrix() {
        this.matrix.setRectToRect(this.wrappedRect, this.bounds, ScaleToFit.CENTER);
    }

    public void setChangingConfigurations(int configs) {
        this.wrapped.setChangingConfigurations(configs);
    }

    public int getChangingConfigurations() {
        return this.wrapped.getChangingConfigurations();
    }

    @Deprecated
    public void setDither(boolean dither) {
        this.wrapped.setDither(dither);
    }

    public void setFilterBitmap(boolean filter) {
        this.wrapped.setFilterBitmap(filter);
    }

    public Callback getCallback() {
        return this.wrapped.getCallback();
    }

    @RequiresApi(19)
    public int getAlpha() {
        return this.wrapped.getAlpha();
    }

    public void setColorFilter(int color, @NonNull Mode mode) {
        this.wrapped.setColorFilter(color, mode);
    }

    public void clearColorFilter() {
        this.wrapped.clearColorFilter();
    }

    @NonNull
    public Drawable getCurrent() {
        return this.wrapped.getCurrent();
    }

    public boolean setVisible(boolean visible, boolean restart) {
        return this.wrapped.setVisible(visible, restart);
    }

    public int getIntrinsicWidth() {
        return this.state.width;
    }

    public int getIntrinsicHeight() {
        return this.state.height;
    }

    public int getMinimumWidth() {
        return this.wrapped.getMinimumWidth();
    }

    public int getMinimumHeight() {
        return this.wrapped.getMinimumHeight();
    }

    public boolean getPadding(@NonNull Rect padding) {
        return this.wrapped.getPadding(padding);
    }

    public void invalidateSelf() {
        super.invalidateSelf();
        this.wrapped.invalidateSelf();
    }

    public void unscheduleSelf(@NonNull Runnable what) {
        super.unscheduleSelf(what);
        this.wrapped.unscheduleSelf(what);
    }

    public void scheduleSelf(@NonNull Runnable what, long when) {
        super.scheduleSelf(what, when);
        this.wrapped.scheduleSelf(what, when);
    }

    public void draw(@NonNull Canvas canvas) {
        canvas.save();
        canvas.concat(this.matrix);
        this.wrapped.draw(canvas);
        canvas.restore();
    }

    public void setAlpha(int i) {
        this.wrapped.setAlpha(i);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.wrapped.setColorFilter(colorFilter);
    }

    public int getOpacity() {
        return this.wrapped.getOpacity();
    }

    @NonNull
    public Drawable mutate() {
        if (!this.mutated && super.mutate() == this) {
            this.wrapped = this.wrapped.mutate();
            this.state = new State(this.state);
            this.mutated = true;
        }
        return this;
    }

    public ConstantState getConstantState() {
        return this.state;
    }
}
