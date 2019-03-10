package org.hamcrest.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import org.hamcrest.Condition;
import org.hamcrest.Condition.Step;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class HasPropertyWithValue<T> extends TypeSafeDiagnosingMatcher<T> {
    private static final Step<PropertyDescriptor, Method> WITH_READ_METHOD = withReadMethod();
    private final String propertyName;
    private final Matcher<Object> valueMatcher;

    /* renamed from: org.hamcrest.beans.HasPropertyWithValue$2 */
    static class C12442 implements Step<PropertyDescriptor, Method> {
        C12442() {
        }

        public Condition<Method> apply(PropertyDescriptor property, Description mismatch) {
            Method readMethod = property.getReadMethod();
            if (readMethod != null) {
                return Condition.matched(readMethod, mismatch);
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("property \"");
            stringBuilder.append(property.getName());
            stringBuilder.append("\" is not readable");
            mismatch.appendText(stringBuilder.toString());
            return Condition.notMatched();
        }
    }

    public HasPropertyWithValue(String propertyName, Matcher<?> valueMatcher) {
        this.propertyName = propertyName;
        this.valueMatcher = nastyGenericsWorkaround(valueMatcher);
    }

    public boolean matchesSafely(T bean, Description mismatch) {
        Condition and = propertyOn(bean, mismatch).and(WITH_READ_METHOD).and(withPropertyValue(bean));
        Matcher matcher = this.valueMatcher;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("property '");
        stringBuilder.append(this.propertyName);
        stringBuilder.append("' ");
        return and.matching(matcher, stringBuilder.toString());
    }

    public void describeTo(Description description) {
        description.appendText("hasProperty(").appendValue(this.propertyName).appendText(", ").appendDescriptionOf(this.valueMatcher).appendText(")");
    }

    private Condition<PropertyDescriptor> propertyOn(T bean, Description mismatch) {
        PropertyDescriptor property = PropertyUtil.getPropertyDescriptor(this.propertyName, bean);
        if (property != null) {
            return Condition.matched(property, mismatch);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("No property \"");
        stringBuilder.append(this.propertyName);
        stringBuilder.append("\"");
        mismatch.appendText(stringBuilder.toString());
        return Condition.notMatched();
    }

    private Step<Method, Object> withPropertyValue(final T bean) {
        return new Step<Method, Object>() {
            public Condition<Object> apply(Method readMethod, Description mismatch) {
                try {
                    return Condition.matched(readMethod.invoke(bean, PropertyUtil.NO_ARGUMENTS), mismatch);
                } catch (Exception e) {
                    mismatch.appendText(e.getMessage());
                    return Condition.notMatched();
                }
            }
        };
    }

    private static Matcher<Object> nastyGenericsWorkaround(Matcher<?> valueMatcher) {
        return valueMatcher;
    }

    private static Step<PropertyDescriptor, Method> withReadMethod() {
        return new C12442();
    }

    @Factory
    public static <T> Matcher<T> hasProperty(String propertyName, Matcher<?> valueMatcher) {
        return new HasPropertyWithValue(propertyName, valueMatcher);
    }
}
