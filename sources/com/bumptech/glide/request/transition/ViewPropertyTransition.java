package com.bumptech.glide.request.transition;

import android.view.View;
import com.bumptech.glide.request.transition.Transition.ViewAdapter;

public class ViewPropertyTransition<R> implements Transition<R> {
    private final Animator animator;

    public interface Animator {
        void animate(View view);
    }

    public ViewPropertyTransition(Animator animator) {
        this.animator = animator;
    }

    public boolean transition(R r, ViewAdapter adapter) {
        if (adapter.getView() != null) {
            this.animator.animate(adapter.getView());
        }
        return false;
    }
}
