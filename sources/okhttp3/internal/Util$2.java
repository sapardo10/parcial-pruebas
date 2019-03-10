package okhttp3.internal;

import java.util.concurrent.ThreadFactory;

class Util$2 implements ThreadFactory {
    final /* synthetic */ boolean val$daemon;
    final /* synthetic */ String val$name;

    Util$2(String str, boolean z) {
        this.val$name = str;
        this.val$daemon = z;
    }

    public Thread newThread(Runnable runnable) {
        Thread result = new Thread(runnable, this.val$name);
        result.setDaemon(this.val$daemon);
        return result;
    }
}
