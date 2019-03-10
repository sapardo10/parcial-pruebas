package com.bytehamster.lib.preferencesearch.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewAnimationUtils;

public class AnimationUtils {

    public interface OnDismissedListener {
        void onDismissed();
    }

    public static void registerCircularRevealAnimation(Context context, View view, RevealAnimationSetting revealSettings) {
        int startColor = revealSettings.getColorAccent();
        int endColor = getBackgroundColor(view);
        if (VERSION.SDK_INT >= 21) {
            final View view2 = view;
            final RevealAnimationSetting revealAnimationSetting = revealSettings;
            final Context context2 = context;
            final int i = startColor;
            final int i2 = endColor;
            view.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                @TargetApi(21)
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    View view = v;
                    v.removeOnLayoutChangeListener(this);
                    view2.setVisibility(0);
                    int cx = revealAnimationSetting.getCenterX();
                    int cy = revealAnimationSetting.getCenterY();
                    int width = revealAnimationSetting.getWidth();
                    int height = revealAnimationSetting.getHeight();
                    int duration = context2.getResources().getInteger(17694722);
                    Animator anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0.0f, (float) Math.sqrt((double) ((width * width) + (height * height)))).setDuration((long) duration);
                    anim.setInterpolator(new FastOutSlowInInterpolator());
                    anim.start();
                    AnimationUtils.startColorAnimation(view2, i, i2, duration);
                }
            });
        }
    }

    private static void startColorAnimation(final View view, int startColor, int endColor, int duration) {
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(new int[]{startColor, endColor});
        anim.setEvaluator(new ArgbEvaluator());
        anim.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setBackgroundColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
            }
        });
        anim.setDuration((long) duration);
        anim.start();
    }

    public static void startCircularExitAnimation(Context context, final View view, RevealAnimationSetting revealSettings, final OnDismissedListener listener) {
        int startColor = getBackgroundColor(view);
        int endColor = revealSettings.getColorAccent();
        if (VERSION.SDK_INT >= 21) {
            int cx = revealSettings.getCenterX();
            int cy = revealSettings.getCenterY();
            int width = revealSettings.getWidth();
            int height = revealSettings.getHeight();
            int duration = context.getResources().getInteger(17694722);
            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, (float) Math.sqrt((double) ((width * width) + (height * height))), null);
            anim.setDuration((long) duration);
            anim.setInterpolator(new FastOutSlowInInterpolator());
            anim.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(4);
                    listener.onDismissed();
                }
            });
            anim.start();
            startColorAnimation(view, startColor, endColor, duration);
            return;
        }
        listener.onDismissed();
    }

    private static int getBackgroundColor(View view) {
        Drawable background = view.getBackground();
        if (background instanceof ColorDrawable) {
            return ((ColorDrawable) background).getColor();
        }
        return 0;
    }
}
