package io.reactivex.internal.operators.observable;

import io.reactivex.ObservableOperator;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.plugins.RxJavaPlugins;

public final class ObservableLift<R, T> extends AbstractObservableWithUpstream<T, R> {
    final ObservableOperator<? extends R, ? super T> operator;

    public ObservableLift(ObservableSource<T> source, ObservableOperator<? extends R, ? super T> operator) {
        super(source);
        this.operator = operator;
    }

    public void subscribeActual(Observer<? super R> observer) {
        try {
            Object apply = this.operator.apply(observer);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Operator ");
            stringBuilder.append(this.operator);
            stringBuilder.append(" returned a null Observer");
            this.source.subscribe((Observer) ObjectHelper.requireNonNull(apply, stringBuilder.toString()));
        } catch (NullPointerException e) {
            throw e;
        } catch (Throwable e2) {
            Exceptions.throwIfFatal(e2);
            RxJavaPlugins.onError(e2);
            new NullPointerException("Actually not, but can't throw other exceptions due to RS").initCause(e2);
        }
    }
}
