package io.reactivex.android.schedulers;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import io.reactivex.Scheduler;
import io.reactivex.Scheduler.Worker;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.TimeUnit;

final class HandlerScheduler extends Scheduler {
    private final boolean async;
    private final Handler handler;

    private static final class ScheduledRunnable implements Runnable, Disposable {
        private final Runnable delegate;
        private volatile boolean disposed;
        private final Handler handler;

        ScheduledRunnable(Handler handler, Runnable delegate) {
            this.handler = handler;
            this.delegate = delegate;
        }

        public void run() {
            try {
                this.delegate.run();
            } catch (Throwable t) {
                RxJavaPlugins.onError(t);
            }
        }

        public void dispose() {
            this.handler.removeCallbacks(this);
            this.disposed = true;
        }

        public boolean isDisposed() {
            return this.disposed;
        }
    }

    private static final class HandlerWorker extends Worker {
        private final boolean async;
        private volatile boolean disposed;
        private final Handler handler;

        HandlerWorker(Handler handler, boolean async) {
            this.handler = handler;
            this.async = async;
        }

        @SuppressLint({"NewApi"})
        public Disposable schedule(Runnable run, long delay, TimeUnit unit) {
            if (run == null) {
                throw new NullPointerException("run == null");
            } else if (unit == null) {
                throw new NullPointerException("unit == null");
            } else if (this.disposed) {
                return Disposables.disposed();
            } else {
                ScheduledRunnable scheduled = new ScheduledRunnable(this.handler, RxJavaPlugins.onSchedule(run));
                Message message = Message.obtain(this.handler, scheduled);
                message.obj = this;
                if (this.async) {
                    message.setAsynchronous(true);
                }
                this.handler.sendMessageDelayed(message, unit.toMillis(delay));
                if (!this.disposed) {
                    return scheduled;
                }
                this.handler.removeCallbacks(scheduled);
                return Disposables.disposed();
            }
        }

        public void dispose() {
            this.disposed = true;
            this.handler.removeCallbacksAndMessages(this);
        }

        public boolean isDisposed() {
            return this.disposed;
        }
    }

    HandlerScheduler(Handler handler, boolean async) {
        this.handler = handler;
        this.async = async;
    }

    public Disposable scheduleDirect(Runnable run, long delay, TimeUnit unit) {
        if (run == null) {
            throw new NullPointerException("run == null");
        } else if (unit != null) {
            ScheduledRunnable scheduled = new ScheduledRunnable(this.handler, RxJavaPlugins.onSchedule(run));
            this.handler.postDelayed(scheduled, unit.toMillis(delay));
            return scheduled;
        } else {
            throw new NullPointerException("unit == null");
        }
    }

    public Worker createWorker() {
        return new HandlerWorker(this.handler, this.async);
    }
}
