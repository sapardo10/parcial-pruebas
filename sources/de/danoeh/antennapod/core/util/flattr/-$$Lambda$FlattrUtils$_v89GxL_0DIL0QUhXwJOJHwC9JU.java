package de.danoeh.antennapod.core.util.flattr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FlattrUtils$_v89GxL_0DIL0QUhXwJOJHwC9JU implements OnClickListener {
    private final /* synthetic */ String f$0;
    private final /* synthetic */ Context f$1;

    public /* synthetic */ -$$Lambda$FlattrUtils$_v89GxL_0DIL0QUhXwJOJHwC9JU(String str, Context context) {
        this.f$0 = str;
        this.f$1 = context;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$1.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(this.f$0)));
    }
}
