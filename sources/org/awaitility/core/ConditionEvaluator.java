package org.awaitility.core;

import org.awaitility.Duration;

interface ConditionEvaluator {
    ConditionEvaluationResult eval(Duration duration) throws Exception;
}
