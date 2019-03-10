package io.reactivex.internal.operators.flowable;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.subscribers.BlockingSubscriber;
import io.reactivex.internal.subscribers.BoundedSubscriber;
import io.reactivex.internal.subscribers.LambdaSubscriber;
import io.reactivex.internal.util.BlockingHelper;
import io.reactivex.internal.util.BlockingIgnoringReceiver;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.internal.util.NotificationLite;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public final class FlowableBlockingSubscribe {
    private FlowableBlockingSubscribe() {
        throw new IllegalStateException("No instances!");
    }

    public static <T> void subscribe(Publisher<? extends T> o, Subscriber<? super T> subscriber) {
        BlockingQueue<Object> queue = new LinkedBlockingQueue();
        BlockingSubscriber<T> bs = new BlockingSubscriber(queue);
        o.subscribe(bs);
        while (!bs.isCancelled()) {
            try {
                Object v = queue.poll();
                if (v == null) {
                    if (bs.isCancelled()) {
                        break;
                    }
                    BlockingHelper.verifyNonBlocking();
                    v = queue.take();
                }
                if (!bs.isCancelled()) {
                    if (v == BlockingSubscriber.TERMINATED) {
                        break;
                    } else if (NotificationLite.acceptFull(v, (Subscriber) subscriber)) {
                        break;
                    }
                } else {
                    break;
                }
            } catch (InterruptedException e) {
                bs.cancel();
                subscriber.onError(e);
                return;
            }
        }
    }

    public static <T> void subscribe(Publisher<? extends T> o) {
        BlockingIgnoringReceiver callback = new BlockingIgnoringReceiver();
        LambdaSubscriber<T> ls = new LambdaSubscriber(Functions.emptyConsumer(), callback, callback, Functions.REQUEST_MAX);
        o.subscribe(ls);
        BlockingHelper.awaitForComplete(callback, ls);
        Throwable e = callback.error;
        if (e != null) {
            throw ExceptionHelper.wrapOrThrow(e);
        }
    }

    public static <T> void subscribe(Publisher<? extends T> o, Consumer<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete) {
        ObjectHelper.requireNonNull((Object) onNext, "onNext is null");
        ObjectHelper.requireNonNull((Object) onError, "onError is null");
        ObjectHelper.requireNonNull((Object) onComplete, "onComplete is null");
        subscribe(o, new LambdaSubscriber(onNext, onError, onComplete, Functions.REQUEST_MAX));
    }

    public static <T> void subscribe(Publisher<? extends T> o, Consumer<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete, int bufferSize) {
        ObjectHelper.requireNonNull((Object) onNext, "onNext is null");
        ObjectHelper.requireNonNull((Object) onError, "onError is null");
        ObjectHelper.requireNonNull((Object) onComplete, "onComplete is null");
        ObjectHelper.verifyPositive(bufferSize, "number > 0 required");
        subscribe(o, new BoundedSubscriber(onNext, onError, onComplete, Functions.boundedConsumer(bufferSize), bufferSize));
    }
}
