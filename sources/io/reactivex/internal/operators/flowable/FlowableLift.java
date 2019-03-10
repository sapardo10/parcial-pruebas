package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableOperator;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;
import org.reactivestreams.Subscriber;

public final class FlowableLift<R, T> extends AbstractFlowableWithUpstream<T, R> {
    final FlowableOperator<? extends R, ? super T> operator;

    public FlowableLift(Flowable<T> source, FlowableOperator<? extends R, ? super T> operator) {
        super(source);
        this.operator = operator;
    }

    public void subscribeActual(Subscriber<? super R> s) {
        try {
            Subscriber<? super T> st = this.operator.apply(s);
            if (st != null) {
                this.source.subscribe(st);
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Operator ");
            stringBuilder.append(this.operator);
            stringBuilder.append(" returned a null Subscriber");
            throw new NullPointerException(stringBuilder.toString());
        } catch (NullPointerException e) {
            throw e;
        } catch (Throwable e2) {
            Exceptions.throwIfFatal(e2);
            RxJavaPlugins.onError(e2);
            new NullPointerException("Actually not, but can't throw other exceptions due to RS").initCause(e2);
        }
    }
}
