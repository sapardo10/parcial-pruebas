package de.danoeh.antennapod.activity;

import android.database.DataSetObserver;

class MainActivity$1 extends DataSetObserver {
    final /* synthetic */ MainActivity this$0;

    MainActivity$1(MainActivity this$0) {
        this.this$0 = this$0;
    }

    public void onChanged() {
        MainActivity mainActivity = this.this$0;
        MainActivity.access$002(mainActivity, MainActivity.access$100(mainActivity));
    }
}
