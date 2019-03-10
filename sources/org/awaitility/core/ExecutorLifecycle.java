package org.awaitility.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorLifecycle {
    private final EvaluationCleanup evaluationCleanup;
    private final Supplier<ExecutorService> executorServiceSupplier;

    /* renamed from: org.awaitility.core.ExecutorLifecycle$2 */
    static class C12392 implements Consumer<ExecutorService> {
        C12392() {
        }

        public void accept(ExecutorService executorService) {
        }
    }

    /* renamed from: org.awaitility.core.ExecutorLifecycle$3 */
    static class C12403 implements Consumer<ExecutorService> {
        C12403() {
        }

        public void accept(ExecutorService executor) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                    executor.awaitTermination(1, TimeUnit.SECONDS);
                }
            } catch (InterruptedException e) {
                CheckedExceptionRethrower.safeRethrow(e);
            }
        }
    }

    /* renamed from: org.awaitility.core.ExecutorLifecycle$4 */
    static class C12414 implements Consumer<ExecutorService> {
        C12414() {
        }

        public void accept(ExecutorService executorService) {
            executorService.shutdownNow();
        }
    }

    private ExecutorLifecycle(Supplier<ExecutorService> executorServiceSupplier, EvaluationCleanup evaluationCleanup) {
        this.executorServiceSupplier = executorServiceSupplier;
        this.evaluationCleanup = evaluationCleanup;
    }

    public static ExecutorLifecycle withoutCleanup(final ExecutorService executorService) {
        return withoutCleanup(new Supplier<ExecutorService>() {
            public ExecutorService get() {
                return executorService;
            }
        });
    }

    public static ExecutorLifecycle withoutCleanup(Supplier<ExecutorService> executorServiceSupplier) {
        return new ExecutorLifecycle(executorServiceSupplier, noCleanup());
    }

    public static ExecutorLifecycle withNormalCleanupBehavior(Supplier<ExecutorService> executorServiceSupplier) {
        return new ExecutorLifecycle(executorServiceSupplier, normalCleanupBehavior());
    }

    public ExecutorService supplyExecutorService() {
        return (ExecutorService) this.executorServiceSupplier.get();
    }

    void executeNormalCleanupBehavior(ExecutorService executorService) {
        this.evaluationCleanup.executeNormalCleanupBehavior(executorService);
    }

    void executeUnexpectedCleanupBehavior(ExecutorService executorService) {
        this.evaluationCleanup.executeUnexpectedCleanupBehavior(executorService);
    }

    private static EvaluationCleanup noCleanup() {
        Consumer<ExecutorService> noop = new C12392();
        return new EvaluationCleanup(noop, noop);
    }

    private static EvaluationCleanup normalCleanupBehavior() {
        return new EvaluationCleanup(new C12403(), new C12414());
    }
}
