package com.bumptech.glide.request.transition;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.request.transition.ViewPropertyTransition.Animator;

public class ViewPropertyAnimationFactory<R> implements TransitionFactory<R> {
    private ViewPropertyTransition<R> animation;
    private final Animator animator;

    public ViewPropertyAnimationFactory(Animator animator) {
        this.animator = animator;
    }

    public Transition<R> build(DataSource dataSource, boolean isFirstResource) {
        if (dataSource != DataSource.MEMORY_CACHE) {
            if (isFirstResource) {
                if (this.animation == null) {
                    this.animation = new ViewPropertyTransition(this.animator);
                }
                return this.animation;
            }
        }
        return NoTransition.get();
    }
}
