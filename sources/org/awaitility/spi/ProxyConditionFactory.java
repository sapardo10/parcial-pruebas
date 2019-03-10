package org.awaitility.spi;

import org.awaitility.core.Condition;
import org.awaitility.core.ConditionSettings;
import org.hamcrest.Matcher;

public interface ProxyConditionFactory<T> {
    Condition<T> createProxyCondition(T t, Matcher<? super T> matcher, ConditionSettings conditionSettings);
}
