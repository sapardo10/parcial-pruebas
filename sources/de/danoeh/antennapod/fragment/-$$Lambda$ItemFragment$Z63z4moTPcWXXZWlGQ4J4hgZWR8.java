package de.danoeh.antennapod.fragment;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ItemFragment$Z63z4moTPcWXXZWlGQ4J4hgZWR8 implements OnTouchListener {
    private final /* synthetic */ ItemFragment f$0;

    public /* synthetic */ -$$Lambda$ItemFragment$Z63z4moTPcWXXZWlGQ4J4hgZWR8(ItemFragment itemFragment) {
        this.f$0 = itemFragment;
    }

    public final boolean onTouch(View view, MotionEvent motionEvent) {
        return this.f$0.webviewGestureDetector.onTouchEvent(motionEvent);
    }
}
