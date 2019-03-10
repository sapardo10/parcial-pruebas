package okhttp3.internal.ws;

final class RealWebSocket$CancelRunnable implements Runnable {
    final /* synthetic */ RealWebSocket this$0;

    RealWebSocket$CancelRunnable(RealWebSocket this$0) {
        this.this$0 = this$0;
    }

    public void run() {
        this.this$0.cancel();
    }
}
