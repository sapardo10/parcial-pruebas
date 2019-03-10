package io.reactivex.internal.schedulers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class SchedulerPoolFactory {
    static final Map<ScheduledThreadPoolExecutor, Object> POOLS = new ConcurrentHashMap();
    public static final boolean PURGE_ENABLED;
    static final String PURGE_ENABLED_KEY = "rx2.purge-enabled";
    public static final int PURGE_PERIOD_SECONDS;
    static final String PURGE_PERIOD_SECONDS_KEY = "rx2.purge-period-seconds";
    static final AtomicReference<ScheduledExecutorService> PURGE_THREAD = new AtomicReference();

    static final class PurgeProperties {
        boolean purgeEnable;
        int purgePeriod;

        PurgeProperties() {
        }

        void load(Properties properties) {
            if (properties.containsKey(SchedulerPoolFactory.PURGE_ENABLED_KEY)) {
                this.purgeEnable = Boolean.parseBoolean(properties.getProperty(SchedulerPoolFactory.PURGE_ENABLED_KEY));
            } else {
                this.purgeEnable = true;
            }
            if (this.purgeEnable && properties.containsKey(SchedulerPoolFactory.PURGE_PERIOD_SECONDS_KEY)) {
                try {
                    this.purgePeriod = Integer.parseInt(properties.getProperty(SchedulerPoolFactory.PURGE_PERIOD_SECONDS_KEY));
                } catch (NumberFormatException e) {
                    this.purgePeriod = 1;
                }
                return;
            }
            this.purgePeriod = 1;
        }
    }

    static final class ScheduledTask implements Runnable {
        ScheduledTask() {
        }

        public void run() {
            Iterator it = new ArrayList(SchedulerPoolFactory.POOLS.keySet()).iterator();
            while (it.hasNext()) {
                ScheduledThreadPoolExecutor e = (ScheduledThreadPoolExecutor) it.next();
                if (e.isShutdown()) {
                    SchedulerPoolFactory.POOLS.remove(e);
                } else {
                    e.purge();
                }
            }
        }
    }

    private SchedulerPoolFactory() {
        throw new IllegalStateException("No instances!");
    }

    static {
        Properties properties = System.getProperties();
        PurgeProperties pp = new PurgeProperties();
        pp.load(properties);
        PURGE_ENABLED = pp.purgeEnable;
        PURGE_PERIOD_SECONDS = pp.purgePeriod;
        start();
    }

    public static void start() {
        tryStart(PURGE_ENABLED);
    }

    static void tryStart(boolean purgeEnabled) {
        if (purgeEnabled) {
            while (true) {
                ScheduledExecutorService curr = (ScheduledExecutorService) PURGE_THREAD.get();
                if (curr == null) {
                    ScheduledExecutorService next = Executors.newScheduledThreadPool(1, new RxThreadFactory("RxSchedulerPurge"));
                    if (PURGE_THREAD.compareAndSet(curr, next)) {
                        Runnable scheduledTask = new ScheduledTask();
                        int i = PURGE_PERIOD_SECONDS;
                        next.scheduleAtFixedRate(scheduledTask, (long) i, (long) i, TimeUnit.SECONDS);
                        return;
                    }
                    next.shutdownNow();
                } else {
                    return;
                }
            }
        }
    }

    public static void shutdown() {
        ScheduledExecutorService exec = (ScheduledExecutorService) PURGE_THREAD.getAndSet(null);
        if (exec != null) {
            exec.shutdownNow();
        }
        POOLS.clear();
    }

    public static ScheduledExecutorService create(ThreadFactory factory) {
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1, factory);
        tryPutIntoPool(PURGE_ENABLED, exec);
        return exec;
    }

    static void tryPutIntoPool(boolean purgeEnabled, ScheduledExecutorService exec) {
        if (purgeEnabled && (exec instanceof ScheduledThreadPoolExecutor)) {
            POOLS.put((ScheduledThreadPoolExecutor) exec, exec);
        }
    }
}
