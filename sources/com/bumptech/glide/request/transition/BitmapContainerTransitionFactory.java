package com.bumptech.glide.request.transition;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.request.transition.Transition.ViewAdapter;

public abstract class BitmapContainerTransitionFactory<R> implements TransitionFactory<R> {
    private final TransitionFactory<Drawable> realFactory;

    private final class BitmapGlideAnimation implements Transition<R> {
        private final Transition<Drawable> transition;

        BitmapGlideAnimation(Transition<Drawable> transition) {
            this.transition = transition;
        }

        public boolean transition(R current, ViewAdapter adapter) {
            return this.transition.transition(new BitmapDrawable(adapter.getView().getResources(), BitmapContainerTransitionFactory.this.getBitmap(current)), adapter);
        }
    }

    protected abstract Bitmap getBitmap(R r);

    public BitmapContainerTransitionFactory(TransitionFactory<Drawable> realFactory) {
        this.realFactory = realFactory;
    }

    public Transition<R> build(DataSource dataSource, boolean isFirstResource) {
        return new BitmapGlideAnimation(this.realFactory.build(dataSource, isFirstResource));
    }
}
