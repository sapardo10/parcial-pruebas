package de.danoeh.antennapod.activity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

class MainActivity$2 implements OnItemClickListener {
    final /* synthetic */ MainActivity this$0;

    MainActivity$2(MainActivity this$0) {
        this.this$0 = this$0;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getAdapter().getItemViewType(position) != 1 && position != MainActivity.access$000(this.this$0)) {
            MainActivity.access$200(this.this$0, position, null);
        }
        MainActivity.access$400(this.this$0).closeDrawer(MainActivity.access$300(this.this$0));
    }
}
