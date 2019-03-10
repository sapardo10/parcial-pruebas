package android.support.v4.app;

import android.os.Handler;
import android.os.Message;

class FragmentActivity$1 extends Handler {
    final /* synthetic */ FragmentActivity this$0;

    FragmentActivity$1(FragmentActivity this$0) {
        this.this$0 = this$0;
    }

    public void handleMessage(Message msg) {
        switch (msg.what) {
            case 1:
                if (this.this$0.mStopped) {
                    this.this$0.doReallyStop(false);
                    return;
                }
                return;
            case 2:
                this.this$0.onResumeFragments();
                this.this$0.mFragments.execPendingActions();
                return;
            default:
                super.handleMessage(msg);
                return;
        }
    }
}
