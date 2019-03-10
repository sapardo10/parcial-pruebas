package com.bumptech.glide.request.transition;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import com.bumptech.glide.request.transition.Transition.ViewAdapter;

public class DrawableCrossFadeTransition implements Transition<Drawable> {
    private final int duration;
    private final boolean isCrossFadeEnabled;

    public DrawableCrossFadeTransition(int duration, boolean isCrossFadeEnabled) {
        this.duration = duration;
        this.isCrossFadeEnabled = isCrossFadeEnabled;
    }

    public boolean transition(Drawable current, ViewAdapter adapter) {
        Drawable previous = adapter.getCurrentDrawable();
        if (previous == null) {
            previous = new ColorDrawable(0);
        }
        TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{previous, current});
        transitionDrawable.setCrossFadeEnabled(this.isCrossFadeEnabled);
        transitionDrawable.startTransition(this.duration);
        adapter.setDrawable(transitionDrawable);
        return true;
    }
}