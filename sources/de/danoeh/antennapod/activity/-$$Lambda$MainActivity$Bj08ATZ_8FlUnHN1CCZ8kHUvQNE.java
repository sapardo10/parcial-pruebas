package de.danoeh.antennapod.activity;

import android.view.View;
import android.view.View.OnClickListener;
import de.danoeh.antennapod.core.event.MessageEvent;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MainActivity$Bj08ATZ_8FlUnHN1CCZ8kHUvQNE implements OnClickListener {
    private final /* synthetic */ MessageEvent f$0;

    public /* synthetic */ -$$Lambda$MainActivity$Bj08ATZ_8FlUnHN1CCZ8kHUvQNE(MessageEvent messageEvent) {
        this.f$0 = messageEvent;
    }

    public final void onClick(View view) {
        this.f$0.action.run();
    }
}
