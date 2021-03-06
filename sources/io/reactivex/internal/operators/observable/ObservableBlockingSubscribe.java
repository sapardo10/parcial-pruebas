package io.reactivex.internal.operators.observable;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.observers.BlockingObserver;
import io.reactivex.internal.observers.LambdaObserver;
import io.reactivex.internal.util.BlockingHelper;
import io.reactivex.internal.util.BlockingIgnoringReceiver;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.internal.util.NotificationLite;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class ObservableBlockingSubscribe {
    private ObservableBlockingSubscribe() {
        throw new IllegalStateException("No instances!");
    }

    public static <T> void subscribe(ObservableSource<? extends T> o, Observer<? super T> observer) {
        BlockingQueue<Object> queue = new LinkedBlockingQueue();
        BlockingObserver<T> bs = new BlockingObserver(queue);
        observer.onSubscribe(bs);
        o.subscribe(bs);
        while (!bs.isDisposed()) {
            Object v = queue.poll();
            if (v == null) {
                try {
                    v = queue.take();
                } catch (InterruptedException ex) {
                    bs.dispose();
                    observer.onError(ex);
                    return;
                }
            }
            if (bs.isDisposed() || o == BlockingObserver.TERMINATED) {
                break;
            } else if (NotificationLite.acceptFull(v, (Observer) observer)) {
                break;
            }
        }
    }

    public static <T> void subscribe(ObservableSource<? extends T> o) {
        BlockingIgnoringReceiver callback = new BlockingIgnoringReceiver();
        LambdaObserver<T> ls = new LambdaObserver(Functions.emptyConsumer(), callback, callback, Functions.emptyConsumer());
        o.subscribe(ls);
        BlockingHelper.awaitForComplete(callback, ls);
        Throwable e = callback.error;
        if (e != null) {
            throw ExceptionHelper.wrapOrThrow(e);
        }
    }

    public static <T> void subscribe(ObservableSource<? extends T> o, Consumer<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete) {
        ObjectHelper.requireNonNull((Object) onNext, "onNext is null");
        ObjectHelper.requireNonNull((Object) onError, "onError is null");
        ObjectHelper.requireNonNull((Object) onComplete, "onComplete is null");
        subscribe(o, new LambdaObserver(onNext, onError, onComplete, Functions.emptyConsumer()));
    }
}
