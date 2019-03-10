package org.apache.commons.lang3;

import org.apache.commons.io.IOUtils;

public class ClassPathUtils {
    public static String toFullyQualifiedName(Class<?> context, String resourceName) {
        Validate.notNull(context, "Parameter '%s' must not be null!", "context");
        Validate.notNull(resourceName, "Parameter '%s' must not be null!", "resourceName");
        return toFullyQualifiedName(context.getPackage(), resourceName);
    }

    public static String toFullyQualifiedName(Package context, String resourceName) {
        Validate.notNull(context, "Parameter '%s' must not be null!", "context");
        Validate.notNull(resourceName, "Parameter '%s' must not be null!", "resourceName");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(context.getName());
        stringBuilder.append(".");
        stringBuilder.append(resourceName);
        return stringBuilder.toString();
    }

    public static String toFullyQualifiedPath(Class<?> context, String resourceName) {
        Validate.notNull(context, "Parameter '%s' must not be null!", "context");
        Validate.notNull(resourceName, "Parameter '%s' must not be null!", "resourceName");
        return toFullyQualifiedPath(context.getPackage(), resourceName);
    }

    public static String toFullyQualifiedPath(Package context, String resourceName) {
        Validate.notNull(context, "Parameter '%s' must not be null!", "context");
        Validate.notNull(resourceName, "Parameter '%s' must not be null!", "resourceName");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(context.getName().replace('.', IOUtils.DIR_SEPARATOR_UNIX));
        stringBuilder.append("/");
        stringBuilder.append(resourceName);
        return stringBuilder.toString();
    }
}
