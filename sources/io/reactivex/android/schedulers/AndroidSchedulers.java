package io.reactivex.android.schedulers;

import android.annotation.SuppressLint;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import java.util.concurrent.Callable;

public final class AndroidSchedulers {
    private static final Scheduler MAIN_THREAD = RxAndroidPlugins.initMainThreadScheduler(new C07961());

    /* renamed from: io.reactivex.android.schedulers.AndroidSchedulers$1 */
    static class C07961 implements Callable<Scheduler> {
        C07961() {
        }

        public Scheduler call() throws Exception {
            return MainHolder.DEFAULT;
        }
    }

    private static final class MainHolder {
        static final Scheduler DEFAULT = new HandlerScheduler(new Handler(Looper.getMainLooper()), false);

        private MainHolder() {
        }
    }

    public static Scheduler mainThread() {
        return RxAndroidPlugins.onMainThreadScheduler(MAIN_THREAD);
    }

    public static Scheduler from(Looper looper) {
        return from(looper, false);
    }

    @SuppressLint({"NewApi"})
    public static Scheduler from(Looper looper, boolean async) {
        if (looper != null) {
            if (VERSION.SDK_INT < 16) {
                async = false;
            } else if (async && VERSION.SDK_INT < 22) {
                Message message = Message.obtain();
                try {
                    message.setAsynchronous(true);
                } catch (NoSuchMethodError e) {
                    async = false;
                }
                message.recycle();
            }
            return new HandlerScheduler(new Handler(looper), async);
        }
        throw new NullPointerException("looper == null");
    }

    private AndroidSchedulers() {
        throw new AssertionError("No instances.");
    }
}
