package okhttp3.internal.ws;

import java.io.IOException;

class RealWebSocket$1 implements Runnable {
    final /* synthetic */ RealWebSocket this$0;

    RealWebSocket$1(RealWebSocket this$0) {
        this.this$0 = this$0;
    }

    public void run() {
        while (this.this$0.writeOneFrame()) {
            try {
            } catch (IOException e) {
                this.this$0.failWebSocket(e, null);
                return;
            }
        }
    }
}
