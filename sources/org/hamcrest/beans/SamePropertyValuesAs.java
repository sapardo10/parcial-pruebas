package org.hamcrest.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.core.IsEqual;

public class SamePropertyValuesAs<T> extends TypeSafeDiagnosingMatcher<T> {
    private final T expectedBean;
    private final List<PropertyMatcher> propertyMatchers;
    private final Set<String> propertyNames;

    public static class PropertyMatcher extends DiagnosingMatcher<Object> {
        private final Matcher<Object> matcher;
        private final String propertyName;
        private final Method readMethod;

        public PropertyMatcher(PropertyDescriptor descriptor, Object expectedObject) {
            this.propertyName = descriptor.getDisplayName();
            this.readMethod = descriptor.getReadMethod();
            this.matcher = IsEqual.equalTo(SamePropertyValuesAs.readProperty(this.readMethod, expectedObject));
        }

        public boolean matches(Object actual, Description mismatch) {
            Object actualValue = SamePropertyValuesAs.readProperty(this.readMethod, actual);
            if (this.matcher.matches(actualValue)) {
                return true;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.propertyName);
            stringBuilder.append(StringUtils.SPACE);
            mismatch.appendText(stringBuilder.toString());
            this.matcher.describeMismatch(actualValue, mismatch);
            return false;
        }

        public void describeTo(Description description) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.propertyName);
            stringBuilder.append(": ");
            description.appendText(stringBuilder.toString()).appendDescriptionOf(this.matcher);
        }
    }

    public SamePropertyValuesAs(T expectedBean) {
        PropertyDescriptor[] descriptors = PropertyUtil.propertyDescriptorsFor(expectedBean, Object.class);
        this.expectedBean = expectedBean;
        this.propertyNames = propertyNamesFrom(descriptors);
        this.propertyMatchers = propertyMatchersFor(expectedBean, descriptors);
    }

    public boolean matchesSafely(T bean, Description mismatch) {
        return isCompatibleType(bean, mismatch) && hasNoExtraProperties(bean, mismatch) && hasMatchingValues(bean, mismatch);
    }

    public void describeTo(Description description) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("same property values as ");
        stringBuilder.append(this.expectedBean.getClass().getSimpleName());
        description.appendText(stringBuilder.toString()).appendList(" [", ", ", "]", this.propertyMatchers);
    }

    private boolean isCompatibleType(T item, Description mismatchDescription) {
        if (this.expectedBean.getClass().isAssignableFrom(item.getClass())) {
            return true;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("is incompatible type: ");
        stringBuilder.append(item.getClass().getSimpleName());
        mismatchDescription.appendText(stringBuilder.toString());
        return false;
    }

    private boolean hasNoExtraProperties(T item, Description mismatchDescription) {
        Set<String> actualPropertyNames = propertyNamesFrom(PropertyUtil.propertyDescriptorsFor(item, Object.class));
        actualPropertyNames.removeAll(this.propertyNames);
        if (actualPropertyNames.isEmpty()) {
            return true;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("has extra properties called ");
        stringBuilder.append(actualPropertyNames);
        mismatchDescription.appendText(stringBuilder.toString());
        return false;
    }

    private boolean hasMatchingValues(T item, Description mismatchDescription) {
        for (PropertyMatcher propertyMatcher : this.propertyMatchers) {
            if (!propertyMatcher.matches(item)) {
                propertyMatcher.describeMismatch(item, mismatchDescription);
                return false;
            }
        }
        return true;
    }

    private static <T> List<PropertyMatcher> propertyMatchersFor(T bean, PropertyDescriptor[] descriptors) {
        List<PropertyMatcher> result = new ArrayList(descriptors.length);
        for (PropertyDescriptor propertyDescriptor : descriptors) {
            result.add(new PropertyMatcher(propertyDescriptor, bean));
        }
        return result;
    }

    private static Set<String> propertyNamesFrom(PropertyDescriptor[] descriptors) {
        HashSet<String> result = new HashSet();
        for (PropertyDescriptor propertyDescriptor : descriptors) {
            result.add(propertyDescriptor.getDisplayName());
        }
        return result;
    }

    private static Object readProperty(Method method, Object target) {
        try {
            return method.invoke(target, PropertyUtil.NO_ARGUMENTS);
        } catch (Exception e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Could not invoke ");
            stringBuilder.append(method);
            stringBuilder.append(" on ");
            stringBuilder.append(target);
            throw new IllegalArgumentException(stringBuilder.toString(), e);
        }
    }

    @Factory
    public static <T> Matcher<T> samePropertyValuesAs(T expectedBean) {
        return new SamePropertyValuesAs(expectedBean);
    }
}
