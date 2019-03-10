package de.danoeh.antennapod.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

class StorageErrorActivity$2 extends BroadcastReceiver {
    final /* synthetic */ StorageErrorActivity this$0;

    StorageErrorActivity$2(StorageErrorActivity this$0) {
        this.this$0 = this$0;
    }

    public void onReceive(Context context, Intent intent) {
        if (!TextUtils.equals(intent.getAction(), "android.intent.action.MEDIA_MOUNTED")) {
            return;
        }
        if (intent.getBooleanExtra("read-only", true)) {
            Log.d("StorageErrorActivity", "Media was mounted; Finishing activity");
            StorageErrorActivity.access$000(this.this$0);
            return;
        }
        Log.d("StorageErrorActivity", "Media seemed to have been mounted read only");
    }
}
