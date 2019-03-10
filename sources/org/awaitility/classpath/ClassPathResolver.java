package org.awaitility.classpath;

public class ClassPathResolver {
    public static boolean existInCP(String className) {
        if (!existsInCP(className, ClassPathResolver.class.getClassLoader())) {
            if (!existsInCP(className, Thread.currentThread().getContextClassLoader())) {
                return false;
            }
        }
        return true;
    }

    private static boolean existsInCP(String className, ClassLoader classLoader) {
        try {
            Class.forName(className, false, classLoader);
            return true;
        } catch (Throwable th) {
            return false;
        }
    }
}
