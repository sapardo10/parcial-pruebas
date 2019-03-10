package org.apache.commons.lang3.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class ExceptionUtils {
    private static final String[] CAUSE_METHOD_NAMES = new String[]{"getCause", "getNextException", "getTargetException", "getException", "getSourceException", "getRootCause", "getCausedByException", "getNested", "getLinkedException", "getNestedException", "getLinkedCause", "getThrowable"};
    static final String WRAPPED_MARKER = " [wrapped] ";

    public static void removeCommonFrames(java.util.List<java.lang.String> r5, java.util.List<java.lang.String> r6) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:14:0x003c in {7, 8, 9, 11, 13} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        if (r5 == 0) goto L_0x0033;
    L_0x0002:
        if (r6 == 0) goto L_0x0033;
    L_0x0004:
        r0 = r5.size();
        r0 = r0 + -1;
        r1 = r6.size();
        r1 = r1 + -1;
    L_0x0010:
        if (r0 < 0) goto L_0x0031;
    L_0x0012:
        if (r1 < 0) goto L_0x0031;
    L_0x0014:
        r2 = r5.get(r0);
        r2 = (java.lang.String) r2;
        r3 = r6.get(r1);
        r3 = (java.lang.String) r3;
        r4 = r2.equals(r3);
        if (r4 == 0) goto L_0x002a;
    L_0x0026:
        r5.remove(r0);
        goto L_0x002b;
    L_0x002b:
        r0 = r0 + -1;
        r1 = r1 + -1;
        goto L_0x0010;
        return;
        r0 = new java.lang.IllegalArgumentException;
        r1 = "The List must not be null";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.exception.ExceptionUtils.removeCommonFrames(java.util.List, java.util.List):void");
    }

    @Deprecated
    public static String[] getDefaultCauseMethodNames() {
        return (String[]) ArrayUtils.clone(CAUSE_METHOD_NAMES);
    }

    @Deprecated
    public static Throwable getCause(Throwable throwable) {
        return getCause(throwable, null);
    }

    @Deprecated
    public static Throwable getCause(Throwable throwable, String[] methodNames) {
        if (throwable == null) {
            return null;
        }
        if (methodNames == null) {
            Throwable cause = throwable.getCause();
            if (cause != null) {
                return cause;
            }
            methodNames = CAUSE_METHOD_NAMES;
        }
        for (String methodName : methodNames) {
            if (methodName != null) {
                Throwable legacyCause = getCauseUsingMethodName(throwable, methodName);
                if (legacyCause != null) {
                    return legacyCause;
                }
            }
        }
        return null;
    }

    public static Throwable getRootCause(Throwable throwable) {
        List<Throwable> list = getThrowableList(throwable);
        return list.size() < 2 ? null : (Throwable) list.get(list.size() - 1);
    }

    private static Throwable getCauseUsingMethodName(Throwable throwable, String methodName) {
        Method method = null;
        try {
            method = throwable.getClass().getMethod(methodName, new Class[0]);
        } catch (NoSuchMethodException e) {
        }
        if (method != null && Throwable.class.isAssignableFrom(method.getReturnType())) {
            try {
                return (Throwable) method.invoke(throwable, new Object[0]);
            } catch (IllegalAccessException e2) {
            }
        }
        return null;
    }

    public static int getThrowableCount(Throwable throwable) {
        return getThrowableList(throwable).size();
    }

    public static Throwable[] getThrowables(Throwable throwable) {
        List<Throwable> list = getThrowableList(throwable);
        return (Throwable[]) list.toArray(new Throwable[list.size()]);
    }

    public static List<Throwable> getThrowableList(Throwable throwable) {
        List<Throwable> list = new ArrayList();
        while (throwable != null && !list.contains(throwable)) {
            list.add(throwable);
            throwable = throwable.getCause();
        }
        return list;
    }

    public static int indexOfThrowable(Throwable throwable, Class<?> clazz) {
        return indexOf(throwable, clazz, 0, false);
    }

    public static int indexOfThrowable(Throwable throwable, Class<?> clazz, int fromIndex) {
        return indexOf(throwable, clazz, fromIndex, false);
    }

    public static int indexOfType(Throwable throwable, Class<?> type) {
        return indexOf(throwable, type, 0, true);
    }

    public static int indexOfType(Throwable throwable, Class<?> type, int fromIndex) {
        return indexOf(throwable, type, fromIndex, true);
    }

    private static int indexOf(Throwable throwable, Class<?> type, int fromIndex, boolean subclass) {
        if (throwable != null) {
            if (type != null) {
                if (fromIndex < 0) {
                    fromIndex = 0;
                }
                Throwable[] throwables = getThrowables(throwable);
                if (fromIndex >= throwables.length) {
                    return -1;
                }
                int i;
                if (subclass) {
                    for (i = fromIndex; i < throwables.length; i++) {
                        if (type.isAssignableFrom(throwables[i].getClass())) {
                            return i;
                        }
                    }
                } else {
                    for (i = fromIndex; i < throwables.length; i++) {
                        if (type.equals(throwables[i].getClass())) {
                            return i;
                        }
                    }
                }
                return -1;
            }
        }
        return -1;
    }

    public static void printRootCauseStackTrace(Throwable throwable) {
        printRootCauseStackTrace(throwable, System.err);
    }

    public static void printRootCauseStackTrace(Throwable throwable, PrintStream stream) {
        if (throwable != null) {
            Validate.isTrue(stream != null, "The PrintStream must not be null", new Object[0]);
            for (String element : getRootCauseStackTrace(throwable)) {
                stream.println(element);
            }
            stream.flush();
        }
    }

    public static void printRootCauseStackTrace(Throwable throwable, PrintWriter writer) {
        if (throwable != null) {
            Validate.isTrue(writer != null, "The PrintWriter must not be null", new Object[0]);
            for (String element : getRootCauseStackTrace(throwable)) {
                writer.println(element);
            }
            writer.flush();
        }
    }

    public static String[] getRootCauseStackTrace(Throwable throwable) {
        if (throwable == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        Throwable[] throwables = getThrowables(throwable);
        int count = throwables.length;
        List<String> frames = new ArrayList();
        List<String> nextTrace = getStackFrameList(throwables[count - 1]);
        int i = count;
        while (true) {
            i--;
            if (i < 0) {
                return (String[]) frames.toArray(new String[frames.size()]);
            }
            List<String> trace = nextTrace;
            if (i != 0) {
                nextTrace = getStackFrameList(throwables[i - 1]);
                removeCommonFrames(trace, nextTrace);
            }
            if (i == count - 1) {
                frames.add(throwables[i].toString());
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(WRAPPED_MARKER);
                stringBuilder.append(throwables[i].toString());
                frames.add(stringBuilder.toString());
            }
            frames.addAll(trace);
        }
    }

    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw, true));
        return sw.getBuffer().toString();
    }

    public static String[] getStackFrames(Throwable throwable) {
        if (throwable == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return getStackFrames(getStackTrace(throwable));
    }

    static String[] getStackFrames(String stackTrace) {
        StringTokenizer frames = new StringTokenizer(stackTrace, System.lineSeparator());
        List<String> list = new ArrayList();
        while (frames.hasMoreTokens()) {
            list.add(frames.nextToken());
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    static List<String> getStackFrameList(Throwable t) {
        StringTokenizer frames = new StringTokenizer(getStackTrace(t), System.lineSeparator());
        List<String> list = new ArrayList();
        boolean traceStarted = false;
        while (frames.hasMoreTokens()) {
            String token = frames.nextToken();
            int at = token.indexOf("at");
            if (at != -1 && token.substring(0, at).trim().isEmpty()) {
                traceStarted = true;
                list.add(token);
            } else if (traceStarted) {
                break;
            }
        }
        return list;
    }

    public static String getMessage(Throwable th) {
        if (th == null) {
            return "";
        }
        String clsName = ClassUtils.getShortClassName(th, null);
        String msg = th.getMessage();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(clsName);
        stringBuilder.append(": ");
        stringBuilder.append(StringUtils.defaultString(msg));
        return stringBuilder.toString();
    }

    public static String getRootCauseMessage(Throwable th) {
        Throwable root = getRootCause(th);
        return getMessage(root == null ? th : root);
    }

    public static <R> R rethrow(Throwable throwable) {
        return typeErasure(throwable);
    }

    private static <R, T extends Throwable> R typeErasure(Throwable throwable) throws Throwable {
        throw throwable;
    }

    public static <R> R wrapAndThrow(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            throw ((RuntimeException) throwable);
        } else if (throwable instanceof Error) {
            throw ((Error) throwable);
        } else {
            throw new UndeclaredThrowableException(throwable);
        }
    }

    public static boolean hasCause(Throwable chain, Class<? extends Throwable> type) {
        if (chain instanceof UndeclaredThrowableException) {
            chain = chain.getCause();
        }
        return type.isInstance(chain);
    }
}
