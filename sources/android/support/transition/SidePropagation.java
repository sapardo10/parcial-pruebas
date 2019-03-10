package android.support.transition;

import android.graphics.Rect;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;

public class SidePropagation extends VisibilityPropagation {
    private float mPropagationSpeed = 3.0f;
    private int mSide = 80;

    public void setSide(int side) {
        this.mSide = side;
    }

    public void setPropagationSpeed(float propagationSpeed) {
        if (propagationSpeed != 0.0f) {
            this.mPropagationSpeed = propagationSpeed;
            return;
        }
        throw new IllegalArgumentException("propagationSpeed may not be 0");
    }

    public long getStartDelay(ViewGroup sceneRoot, Transition transition, TransitionValues startValues, TransitionValues endValues) {
        SidePropagation sidePropagation = this;
        TransitionValues transitionValues = startValues;
        if (transitionValues == null && endValues == null) {
            return 0;
        }
        int directionMultiplier;
        TransitionValues positionValues;
        int viewCenterX;
        int viewCenterY;
        int[] loc;
        int left;
        int top;
        int right;
        int bottom;
        int epicenterX;
        int epicenterY;
        int directionMultiplier2;
        float distanceFraction;
        long duration;
        Rect epicenter = transition.getEpicenter();
        if (endValues != null) {
            if (getViewVisibility(transitionValues) != 0) {
                directionMultiplier = 1;
                positionValues = endValues;
                viewCenterX = getViewX(positionValues);
                viewCenterY = getViewY(positionValues);
                loc = new int[2];
                sceneRoot.getLocationOnScreen(loc);
                left = loc[0] + Math.round(sceneRoot.getTranslationX());
                top = loc[1] + Math.round(sceneRoot.getTranslationY());
                right = left + sceneRoot.getWidth();
                bottom = top + sceneRoot.getHeight();
                if (epicenter == null) {
                    epicenterX = epicenter.centerX();
                    epicenterY = epicenter.centerY();
                } else {
                    epicenterX = (left + right) / 2;
                    epicenterY = (top + bottom) / 2;
                }
                directionMultiplier2 = directionMultiplier;
                distanceFraction = ((float) distance(sceneRoot, viewCenterX, viewCenterY, epicenterX, epicenterY, left, top, right, bottom)) / ((float) getMaxDistance(sceneRoot));
                duration = transition.getDuration();
                if (duration < 0) {
                    duration = 300;
                }
                return (long) Math.round((((float) (((long) directionMultiplier2) * duration)) / sidePropagation.mPropagationSpeed) * distanceFraction);
            }
        }
        directionMultiplier = -1;
        positionValues = startValues;
        viewCenterX = getViewX(positionValues);
        viewCenterY = getViewY(positionValues);
        loc = new int[2];
        sceneRoot.getLocationOnScreen(loc);
        left = loc[0] + Math.round(sceneRoot.getTranslationX());
        top = loc[1] + Math.round(sceneRoot.getTranslationY());
        right = left + sceneRoot.getWidth();
        bottom = top + sceneRoot.getHeight();
        if (epicenter == null) {
            epicenterX = (left + right) / 2;
            epicenterY = (top + bottom) / 2;
        } else {
            epicenterX = epicenter.centerX();
            epicenterY = epicenter.centerY();
        }
        directionMultiplier2 = directionMultiplier;
        distanceFraction = ((float) distance(sceneRoot, viewCenterX, viewCenterY, epicenterX, epicenterY, left, top, right, bottom)) / ((float) getMaxDistance(sceneRoot));
        duration = transition.getDuration();
        if (duration < 0) {
            duration = 300;
        }
        return (long) Math.round((((float) (((long) directionMultiplier2) * duration)) / sidePropagation.mPropagationSpeed) * distanceFraction);
    }

    private int distance(View sceneRoot, int viewX, int viewY, int epicenterX, int epicenterY, int left, int top, int right, int bottom) {
        int i = this.mSide;
        boolean z = false;
        if (i == 8388611) {
            if (ViewCompat.getLayoutDirection(sceneRoot) == 1) {
                z = true;
            }
            i = z ? 5 : 3;
        } else if (i == GravityCompat.END) {
            if (ViewCompat.getLayoutDirection(sceneRoot) == 1) {
                z = true;
            }
            i = z ? 3 : 5;
        } else {
            i = this.mSide;
        }
        if (i == 3) {
            return (right - viewX) + Math.abs(epicenterY - viewY);
        }
        if (i == 5) {
            return (viewX - left) + Math.abs(epicenterY - viewY);
        }
        if (i == 48) {
            return (bottom - viewY) + Math.abs(epicenterX - viewX);
        }
        if (i != 80) {
            return 0;
        }
        return (viewY - top) + Math.abs(epicenterX - viewX);
    }

    private int getMaxDistance(ViewGroup sceneRoot) {
        int i = this.mSide;
        if (i == 3 || i == 5 || i == 8388611 || i == GravityCompat.END) {
            return sceneRoot.getWidth();
        }
        return sceneRoot.getHeight();
    }
}
