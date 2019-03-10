package org.objenesis;

import java.util.concurrent.ConcurrentHashMap;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.strategy.InstantiatorStrategy;

public class ObjenesisBase implements Objenesis {
    protected ConcurrentHashMap<String, ObjectInstantiator<?>> cache;
    protected final InstantiatorStrategy strategy;

    public ObjenesisBase(InstantiatorStrategy strategy) {
        this(strategy, true);
    }

    public ObjenesisBase(InstantiatorStrategy strategy, boolean useCache) {
        if (strategy != null) {
            this.strategy = strategy;
            this.cache = useCache ? new ConcurrentHashMap() : null;
            return;
        }
        throw new IllegalArgumentException("A strategy can't be null");
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getClass().getName());
        stringBuilder.append(" using ");
        stringBuilder.append(this.strategy.getClass().getName());
        stringBuilder.append(this.cache == null ? " without" : " with");
        stringBuilder.append(" caching");
        return stringBuilder.toString();
    }

    public <T> T newInstance(Class<T> clazz) {
        return getInstantiatorOf(clazz).newInstance();
    }

    public <T> ObjectInstantiator<T> getInstantiatorOf(Class<T> clazz) {
        if (clazz.isPrimitive()) {
            throw new IllegalArgumentException("Primitive types can't be instantiated in Java");
        }
        ConcurrentHashMap concurrentHashMap = this.cache;
        if (concurrentHashMap == null) {
            return this.strategy.newInstantiatorOf(clazz);
        }
        ObjectInstantiator<?> instantiator = (ObjectInstantiator) concurrentHashMap.get(clazz.getName());
        if (instantiator == null) {
            ObjectInstantiator<?> newInstantiator = this.strategy.newInstantiatorOf(clazz);
            instantiator = (ObjectInstantiator) this.cache.putIfAbsent(clazz.getName(), newInstantiator);
            if (instantiator == null) {
                instantiator = newInstantiator;
            }
        }
        return instantiator;
    }
}
