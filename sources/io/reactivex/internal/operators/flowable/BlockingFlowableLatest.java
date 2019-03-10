package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.Notification;
import io.reactivex.internal.util.BlockingHelper;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subscribers.DisposableSubscriber;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;

public final class BlockingFlowableLatest<T> implements Iterable<T> {
    final Publisher<? extends T> source;

    static final class LatestSubscriberIterator<T> extends DisposableSubscriber<Notification<T>> implements Iterator<T> {
        Notification<T> iteratorNotification;
        final Semaphore notify = new Semaphore(0);
        final AtomicReference<Notification<T>> value = new AtomicReference();

        LatestSubscriberIterator() {
        }

        public void onNext(Notification<T> args) {
            if (this.value.getAndSet(args) == null) {
                this.notify.release();
            }
        }

        public void onError(Throwable e) {
            RxJavaPlugins.onError(e);
        }

        public void onComplete() {
        }

        public boolean hasNext() {
            Notification notification = this.iteratorNotification;
            if (notification != null) {
                if (notification.isOnError()) {
                    throw ExceptionHelper.wrapOrThrow(this.iteratorNotification.getError());
                }
            }
            notification = this.iteratorNotification;
            if (notification != null) {
                if (!notification.isOnNext()) {
                    return this.iteratorNotification.isOnNext();
                }
            }
            if (this.iteratorNotification == null) {
                try {
                    BlockingHelper.verifyNonBlocking();
                    this.notify.acquire();
                    Notification<T> n = (Notification) this.value.getAndSet(null);
                    this.iteratorNotification = n;
                    if (n.isOnError()) {
                        throw ExceptionHelper.wrapOrThrow(n.getError());
                    }
                } catch (InterruptedException ex) {
                    dispose();
                    this.iteratorNotification = Notification.createOnError(ex);
                    throw ExceptionHelper.wrapOrThrow(ex);
                }
            }
            return this.iteratorNotification.isOnNext();
        }

        public T next() {
            if (hasNext()) {
                if (this.iteratorNotification.isOnNext()) {
                    T v = this.iteratorNotification.getValue();
                    this.iteratorNotification = null;
                    return v;
                }
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            throw new UnsupportedOperationException("Read-only iterator.");
        }
    }

    public BlockingFlowableLatest(Publisher<? extends T> source) {
        this.source = source;
    }

    public Iterator<T> iterator() {
        LatestSubscriberIterator<T> lio = new LatestSubscriberIterator();
        Flowable.fromPublisher(this.source).materialize().subscribe(lio);
        return lio;
    }
}