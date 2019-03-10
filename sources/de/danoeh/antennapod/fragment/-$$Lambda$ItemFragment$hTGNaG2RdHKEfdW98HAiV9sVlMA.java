package de.danoeh.antennapod.fragment;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ItemFragment$hTGNaG2RdHKEfdW98HAiV9sVlMA implements OnTouchListener {
    private final /* synthetic */ ItemFragment f$0;

    public /* synthetic */ -$$Lambda$ItemFragment$hTGNaG2RdHKEfdW98HAiV9sVlMA(ItemFragment itemFragment) {
        this.f$0 = itemFragment;
    }

    public final boolean onTouch(View view, MotionEvent motionEvent) {
        return this.f$0.headerGestureDetector.onTouchEvent(motionEvent);
    }
}
