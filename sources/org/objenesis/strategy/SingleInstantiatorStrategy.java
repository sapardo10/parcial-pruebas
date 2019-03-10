package org.objenesis.strategy;

import java.lang.reflect.Constructor;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

public class SingleInstantiatorStrategy implements InstantiatorStrategy {
    private Constructor<?> constructor;

    public <T extends ObjectInstantiator<?>> SingleInstantiatorStrategy(Class<T> instantiator) {
        try {
            this.constructor = instantiator.getConstructor(new Class[]{Class.class});
        } catch (Throwable e) {
            throw new ObjenesisException(e);
        }
    }

    public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
        try {
            return (ObjectInstantiator) this.constructor.newInstance(new Object[]{type});
        } catch (Throwable e) {
            throw new ObjenesisException(e);
        } catch (Throwable e2) {
            throw new ObjenesisException(e2);
        } catch (Throwable e22) {
            throw new ObjenesisException(e22);
        }
    }
}
