package io.reactivex.internal.operators.observable;

import io.reactivex.CompletableObserver;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.HalfSerializer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class ObservableMergeWithCompletable<T> extends AbstractObservableWithUpstream<T, T> {
    final CompletableSource other;

    static final class MergeWithObserver<T> extends AtomicInteger implements Observer<T>, Disposable {
        private static final long serialVersionUID = -4592979584110982903L;
        final Observer<? super T> downstream;
        final AtomicThrowable error = new AtomicThrowable();
        final AtomicReference<Disposable> mainDisposable = new AtomicReference();
        volatile boolean mainDone;
        volatile boolean otherDone;
        final OtherObserver otherObserver = new OtherObserver(this);

        static final class OtherObserver extends AtomicReference<Disposable> implements CompletableObserver {
            private static final long serialVersionUID = -2935427570954647017L;
            final MergeWithObserver<?> parent;

            OtherObserver(MergeWithObserver<?> parent) {
                this.parent = parent;
            }

            public void onSubscribe(Disposable d) {
                DisposableHelper.setOnce(this, d);
            }

            public void onError(Throwable e) {
                this.parent.otherError(e);
            }

            public void onComplete() {
                this.parent.otherComplete();
            }
        }

        MergeWithObserver(Observer<? super T> downstream) {
            this.downstream = downstream;
        }

        public void onSubscribe(Disposable d) {
            DisposableHelper.setOnce(this.mainDisposable, d);
        }

        public void onNext(T t) {
            HalfSerializer.onNext(this.downstream, (Object) t, (AtomicInteger) this, this.error);
        }

        public void onError(Throwable ex) {
            DisposableHelper.dispose(this.mainDisposable);
            HalfSerializer.onError(this.downstream, ex, (AtomicInteger) this, this.error);
        }

        public void onComplete() {
            this.mainDone = true;
            if (this.otherDone) {
                HalfSerializer.onComplete(this.downstream, (AtomicInteger) this, this.error);
            }
        }

        public boolean isDisposed() {
            return DisposableHelper.isDisposed((Disposable) this.mainDisposable.get());
        }

        public void dispose() {
            DisposableHelper.dispose(this.mainDisposable);
            DisposableHelper.dispose(this.otherObserver);
        }

        void otherError(Throwable ex) {
            DisposableHelper.dispose(this.mainDisposable);
            HalfSerializer.onError(this.downstream, ex, (AtomicInteger) this, this.error);
        }

        void otherComplete() {
            this.otherDone = true;
            if (this.mainDone) {
                HalfSerializer.onComplete(this.downstream, (AtomicInteger) this, this.error);
            }
        }
    }

    public ObservableMergeWithCompletable(Observable<T> source, CompletableSource other) {
        super(source);
        this.other = other;
    }

    protected void subscribeActual(Observer<? super T> observer) {
        MergeWithObserver<T> parent = new MergeWithObserver(observer);
        observer.onSubscribe(parent);
        this.source.subscribe(parent);
        this.other.subscribe(parent.otherObserver);
    }
}
