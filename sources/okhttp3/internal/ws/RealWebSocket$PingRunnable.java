package okhttp3.internal.ws;

final class RealWebSocket$PingRunnable implements Runnable {
    final /* synthetic */ RealWebSocket this$0;

    RealWebSocket$PingRunnable(RealWebSocket realWebSocket) {
        this.this$0 = realWebSocket;
    }

    public void run() {
        this.this$0.writePingFrame();
    }
}
