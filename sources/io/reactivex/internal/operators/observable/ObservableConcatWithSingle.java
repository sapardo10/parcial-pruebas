package io.reactivex.internal.operators.observable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import java.util.concurrent.atomic.AtomicReference;

public final class ObservableConcatWithSingle<T> extends AbstractObservableWithUpstream<T, T> {
    final SingleSource<? extends T> other;

    static final class ConcatWithObserver<T> extends AtomicReference<Disposable> implements Observer<T>, SingleObserver<T>, Disposable {
        private static final long serialVersionUID = -1953724749712440952L;
        final Observer<? super T> downstream;
        boolean inSingle;
        SingleSource<? extends T> other;

        ConcatWithObserver(Observer<? super T> actual, SingleSource<? extends T> other) {
            this.downstream = actual;
            this.other = other;
        }

        public void onSubscribe(Disposable d) {
            if (DisposableHelper.setOnce(this, d) && !this.inSingle) {
                this.downstream.onSubscribe(this);
            }
        }

        public void onNext(T t) {
            this.downstream.onNext(t);
        }

        public void onSuccess(T t) {
            this.downstream.onNext(t);
            this.downstream.onComplete();
        }

        public void onError(Throwable e) {
            this.downstream.onError(e);
        }

        public void onComplete() {
            this.inSingle = true;
            DisposableHelper.replace(this, null);
            SingleSource<? extends T> ss = this.other;
            this.other = null;
            ss.subscribe(this);
        }

        public void dispose() {
            DisposableHelper.dispose(this);
        }

        public boolean isDisposed() {
            return DisposableHelper.isDisposed((Disposable) get());
        }
    }

    public ObservableConcatWithSingle(Observable<T> source, SingleSource<? extends T> other) {
        super(source);
        this.other = other;
    }

    protected void subscribeActual(Observer<? super T> observer) {
        this.source.subscribe(new ConcatWithObserver(observer, this.other));
    }
}
