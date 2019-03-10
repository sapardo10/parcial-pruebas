package retrofit2.adapter.rxjava2;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

final class CallEnqueueObservable<T> extends Observable<Response<T>> {
    private final Call<T> originalCall;

    private static final class CallCallback<T> implements Disposable, Callback<T> {
        private final Call<?> call;
        private volatile boolean disposed;
        private final Observer<? super Response<T>> observer;
        boolean terminated = false;

        CallCallback(Call<?> call, Observer<? super Response<T>> observer) {
            this.call = call;
            this.observer = observer;
        }

        public void onResponse(Call<T> call, Response<T> response) {
            if (!this.disposed) {
                try {
                    this.observer.onNext(response);
                    if (!this.disposed) {
                        this.terminated = true;
                        this.observer.onComplete();
                    }
                } catch (Throwable inner) {
                    Exceptions.throwIfFatal(inner);
                    RxJavaPlugins.onError(new CompositeException(new Throwable[]{t, inner}));
                }
            }
        }

        public void onFailure(Call<T> call, Throwable t) {
            if (!call.isCanceled()) {
                try {
                    this.observer.onError(t);
                } catch (Throwable inner) {
                    Exceptions.throwIfFatal(inner);
                    RxJavaPlugins.onError(new CompositeException(new Throwable[]{t, inner}));
                }
            }
        }

        public void dispose() {
            this.disposed = true;
            this.call.cancel();
        }

        public boolean isDisposed() {
            return this.disposed;
        }
    }

    CallEnqueueObservable(Call<T> originalCall) {
        this.originalCall = originalCall;
    }

    protected void subscribeActual(Observer<? super Response<T>> observer) {
        Call<T> call = this.originalCall.clone();
        CallCallback<T> callback = new CallCallback(call, observer);
        observer.onSubscribe(callback);
        call.enqueue(callback);
    }
}
