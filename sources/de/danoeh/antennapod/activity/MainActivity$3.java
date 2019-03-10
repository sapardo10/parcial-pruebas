package de.danoeh.antennapod.activity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

class MainActivity$3 implements OnItemLongClickListener {
    final /* synthetic */ MainActivity this$0;

    MainActivity$3(MainActivity this$0) {
        this.this$0 = this$0;
    }

    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (position < MainActivity.access$500(this.this$0).getTags().size()) {
            MainActivity.access$600(this.this$0);
            return true;
        }
        MainActivity.access$702(this.this$0, position);
        return false;
    }
}
