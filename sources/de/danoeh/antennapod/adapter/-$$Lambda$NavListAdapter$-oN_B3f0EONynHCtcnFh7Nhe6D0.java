package de.danoeh.antennapod.adapter;

import android.app.Activity;
import android.support.v7.app.AlertDialog.Builder;
import android.view.View;
import android.view.View.OnClickListener;
import de.danoeh.antennapod.debug.R;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$NavListAdapter$-oN_B3f0EONynHCtcnFh7Nhe6D0 implements OnClickListener {
    private final /* synthetic */ Activity f$0;

    public /* synthetic */ -$$Lambda$NavListAdapter$-oN_B3f0EONynHCtcnFh7Nhe6D0(Activity activity) {
        this.f$0 = activity;
    }

    public final void onClick(View view) {
        new Builder(this.f$0).setTitle((int) R.string.episode_cache_full_title).setMessage((int) R.string.episode_cache_full_message).setPositiveButton(17039370, -$$Lambda$NavListAdapter$aSMwjSB-aKqf-k-YaR5pKfi1eS0.INSTANCE).show();
    }
}
