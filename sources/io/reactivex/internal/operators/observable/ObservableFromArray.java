package io.reactivex.internal.operators.observable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.Nullable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.observers.BasicQueueDisposable;

public final class ObservableFromArray<T> extends Observable<T> {
    final T[] array;

    static final class FromArrayDisposable<T> extends BasicQueueDisposable<T> {
        final T[] array;
        volatile boolean disposed;
        final Observer<? super T> downstream;
        boolean fusionMode;
        int index;

        FromArrayDisposable(Observer<? super T> actual, T[] array) {
            this.downstream = actual;
            this.array = array;
        }

        public int requestFusion(int mode) {
            if ((mode & 1) == 0) {
                return 0;
            }
            this.fusionMode = true;
            return 1;
        }

        @Nullable
        public T poll() {
            int i = this.index;
            T[] a = this.array;
            if (i == a.length) {
                return null;
            }
            this.index = i + 1;
            return ObjectHelper.requireNonNull(a[i], "The array element is null");
        }

        public boolean isEmpty() {
            return this.index == this.array.length;
        }

        public void clear() {
            this.index = this.array.length;
        }

        public void dispose() {
            this.disposed = true;
        }

        public boolean isDisposed() {
            return this.disposed;
        }

        void run() {
            T[] a = this.array;
            int n = a.length;
            for (int i = 0; i < n && !isDisposed(); i++) {
                T value = a[i];
                if (value == null) {
                    Observer observer = this.downstream;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("The ");
                    stringBuilder.append(i);
                    stringBuilder.append("th element is null");
                    observer.onError(new NullPointerException(stringBuilder.toString()));
                    return;
                }
                this.downstream.onNext(value);
            }
            if (!isDisposed()) {
                this.downstream.onComplete();
            }
        }
    }

    public ObservableFromArray(T[] array) {
        this.array = array;
    }

    public void subscribeActual(Observer<? super T> observer) {
        FromArrayDisposable<T> d = new FromArrayDisposable(observer, this.array);
        observer.onSubscribe(d);
        if (!d.fusionMode) {
            d.run();
        }
    }
}
