package org.awaitility.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class InternalExecutorServiceFactory {
    public static ExecutorService sameThreadExecutorService() {
        return new SameThreadExecutorService();
    }

    public static ExecutorService create(final Function<Runnable, Thread> threadSupplier) {
        if (threadSupplier != null) {
            return create(new BiFunction<Runnable, String, Thread>() {
                public Thread apply(Runnable runnable, String __) {
                    return (Thread) threadSupplier.apply(runnable);
                }
            }, null);
        }
        throw new IllegalArgumentException("Condition evaluation thread supplier cannot be null");
    }

    public static ExecutorService create(final BiFunction<Runnable, String, Thread> threadSupplier, final String alias) {
        if (threadSupplier != null) {
            return Executors.newSingleThreadExecutor(new ThreadFactory() {
                public Thread newThread(Runnable r) {
                    return (Thread) threadSupplier.apply(r, InternalExecutorServiceFactory.generateDefaultThreadName(alias));
                }
            });
        }
        throw new IllegalArgumentException("Condition evaluation thread supplier cannot be null");
    }

    private static String generateDefaultThreadName(String alias) {
        if (alias == null) {
            return "awaitility-thread";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("awaitility[");
        stringBuilder.append(alias);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
