package io.reactivex.internal.operators.single;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicInteger;

public final class SingleEquals<T> extends Single<Boolean> {
    final SingleSource<? extends T> first;
    final SingleSource<? extends T> second;

    static class InnerObserver<T> implements SingleObserver<T> {
        final AtomicInteger count;
        final SingleObserver<? super Boolean> downstream;
        final int index;
        final CompositeDisposable set;
        final Object[] values;

        InnerObserver(int index, CompositeDisposable set, Object[] values, SingleObserver<? super Boolean> observer, AtomicInteger count) {
            this.index = index;
            this.set = set;
            this.values = values;
            this.downstream = observer;
            this.count = count;
        }

        public void onSubscribe(Disposable d) {
            this.set.add(d);
        }

        public void onSuccess(T value) {
            this.values[this.index] = value;
            if (this.count.incrementAndGet() == 2) {
                SingleObserver singleObserver = this.downstream;
                Object[] objArr = this.values;
                singleObserver.onSuccess(Boolean.valueOf(ObjectHelper.equals(objArr[0], objArr[1])));
            }
        }

        public void onError(Throwable e) {
            while (true) {
                int state = this.count.get();
                if (state >= 2) {
                    RxJavaPlugins.onError(e);
                    return;
                } else if (this.count.compareAndSet(state, 2)) {
                    this.set.dispose();
                    this.downstream.onError(e);
                    return;
                }
            }
        }
    }

    public SingleEquals(SingleSource<? extends T> first, SingleSource<? extends T> second) {
        this.first = first;
        this.second = second;
    }

    protected void subscribeActual(SingleObserver<? super Boolean> observer) {
        AtomicInteger count = new AtomicInteger();
        Object[] values = new Object[]{null, null};
        CompositeDisposable set = new CompositeDisposable();
        observer.onSubscribe(set);
        this.first.subscribe(new InnerObserver(0, set, values, observer, count));
        this.second.subscribe(new InnerObserver(1, set, values, observer, count));
    }
}
