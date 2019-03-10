package de.danoeh.antennapod.fragment;

import android.view.View;
import android.view.View.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FyydSearchFragment$_Hdy2WmdSm1XhO2q2rdKVxpAbMg implements OnClickListener {
    private final /* synthetic */ FyydSearchFragment f$0;
    private final /* synthetic */ String f$1;

    public /* synthetic */ -$$Lambda$FyydSearchFragment$_Hdy2WmdSm1XhO2q2rdKVxpAbMg(FyydSearchFragment fyydSearchFragment, String str) {
        this.f$0 = fyydSearchFragment;
        this.f$1 = str;
    }

    public final void onClick(View view) {
        this.f$0.search(this.f$1);
    }
}
