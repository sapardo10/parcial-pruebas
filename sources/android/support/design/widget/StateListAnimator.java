package android.support.design.widget;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.util.StateSet;
import java.util.ArrayList;

final class StateListAnimator {
    private final AnimatorListener mAnimationListener = new C00611();
    private Tuple mLastMatch = null;
    ValueAnimator mRunningAnimator = null;
    private final ArrayList<Tuple> mTuples = new ArrayList();

    /* renamed from: android.support.design.widget.StateListAnimator$1 */
    class C00611 extends AnimatorListenerAdapter {
        C00611() {
        }

        public void onAnimationEnd(Animator animator) {
            if (StateListAnimator.this.mRunningAnimator == animator) {
                StateListAnimator.this.mRunningAnimator = null;
            }
        }
    }

    static class Tuple {
        final ValueAnimator mAnimator;
        final int[] mSpecs;

        Tuple(int[] specs, ValueAnimator animator) {
            this.mSpecs = specs;
            this.mAnimator = animator;
        }
    }

    StateListAnimator() {
    }

    public void addState(int[] specs, ValueAnimator animator) {
        Tuple tuple = new Tuple(specs, animator);
        animator.addListener(this.mAnimationListener);
        this.mTuples.add(tuple);
    }

    void setState(int[] state) {
        Tuple match = null;
        int count = this.mTuples.size();
        for (int i = 0; i < count; i++) {
            Tuple tuple = (Tuple) this.mTuples.get(i);
            if (StateSet.stateSetMatches(tuple.mSpecs, state)) {
                match = tuple;
                break;
            }
        }
        Tuple tuple2 = this.mLastMatch;
        if (match != tuple2) {
            if (tuple2 != null) {
                cancel();
            }
            this.mLastMatch = match;
            if (match != null) {
                start(match);
            }
        }
    }

    private void start(Tuple match) {
        this.mRunningAnimator = match.mAnimator;
        this.mRunningAnimator.start();
    }

    private void cancel() {
        ValueAnimator valueAnimator = this.mRunningAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.mRunningAnimator = null;
        }
    }

    public void jumpToCurrentState() {
        ValueAnimator valueAnimator = this.mRunningAnimator;
        if (valueAnimator != null) {
            valueAnimator.end();
            this.mRunningAnimator = null;
        }
    }
}
