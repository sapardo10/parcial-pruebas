package de.danoeh.antennapod.activity.gpoddernet;

import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/* compiled from: lambda */
/* renamed from: de.danoeh.antennapod.activity.gpoddernet.-$$Lambda$GpodnetAuthenticationActivity$fwUYhWsgPdGJSje37HpbH0XgntU */
public final /* synthetic */ class C0721x81accfb3 implements OnEditorActionListener {
    private final /* synthetic */ Button f$0;

    public /* synthetic */ C0721x81accfb3(Button button) {
        this.f$0 = button;
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return GpodnetAuthenticationActivity.lambda$setupLoginView$0(this.f$0, textView, i, keyEvent);
    }
}
