package org.awaitility.core;

import java.util.concurrent.ExecutorService;

class EvaluationCleanup {
    private final Consumer<ExecutorService> normalShutdownBehavior;
    private final Consumer<ExecutorService> unexpectedShutdownBehavior;

    public EvaluationCleanup(Consumer<ExecutorService> normalShutdownBehavior, Consumer<ExecutorService> unexpectedShutdownBehavior) {
        this.normalShutdownBehavior = normalShutdownBehavior;
        this.unexpectedShutdownBehavior = unexpectedShutdownBehavior;
    }

    Consumer<ExecutorService> getNormalShutdownBehavior() {
        return this.normalShutdownBehavior;
    }

    Consumer<ExecutorService> getUnexpectedShutdownBehavior() {
        return this.unexpectedShutdownBehavior;
    }

    void executeNormalCleanupBehavior(ExecutorService executorService) {
        this.normalShutdownBehavior.accept(executorService);
    }

    void executeUnexpectedCleanupBehavior(ExecutorService executorService) {
        this.unexpectedShutdownBehavior.accept(executorService);
    }
}
