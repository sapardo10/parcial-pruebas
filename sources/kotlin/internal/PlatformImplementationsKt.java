package kotlin.internal;

import kotlin.KotlinVersion;
import kotlin.Metadata;
import kotlin.PublishedApi;
import kotlin.SinceKotlin;
import kotlin.TypeCastException;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u001e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0000\n\u0002\b\u0004\u001a \u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\u0005H\u0001\u001a\"\u0010\b\u001a\u0002H\t\"\n\b\u0000\u0010\t\u0018\u0001*\u00020\n2\u0006\u0010\u000b\u001a\u00020\nH\b¢\u0006\u0002\u0010\f\u001a\b\u0010\r\u001a\u00020\u0005H\u0002\"\u0010\u0010\u0000\u001a\u00020\u00018\u0000X\u0004¢\u0006\u0002\n\u0000¨\u0006\u000e"}, d2 = {"IMPLEMENTATIONS", "Lkotlin/internal/PlatformImplementations;", "apiVersionIsAtLeast", "", "major", "", "minor", "patch", "castToBaseType", "T", "", "instance", "(Ljava/lang/Object;)Ljava/lang/Object;", "getJavaVersion", "kotlin-stdlib"}, k = 2, mv = {1, 1, 10})
/* compiled from: PlatformImplementations.kt */
public final class PlatformImplementationsKt {
    @NotNull
    @JvmField
    public static final PlatformImplementations IMPLEMENTATIONS;

    static {
        PlatformImplementations platformImplementations;
        ClassLoader classLoader;
        ClassLoader classLoader2;
        StringBuilder stringBuilder;
        Throwable initCause;
        int version = getJavaVersion();
        Object newInstance;
        if (version >= 65544) {
            try {
                newInstance = Class.forName("kotlin.internal.jdk8.JDK8PlatformImplementations").newInstance();
                Intrinsics.checkExpressionValueIsNotNull(newInstance, "Class.forName(\"kotlin.in…entations\").newInstance()");
                if (newInstance != null) {
                    platformImplementations = (PlatformImplementations) newInstance;
                } else {
                    throw new TypeCastException("null cannot be cast to non-null type kotlin.internal.PlatformImplementations");
                }
            } catch (ClassCastException e) {
                classLoader = newInstance.getClass().getClassLoader();
                classLoader2 = PlatformImplementations.class.getClassLoader();
                stringBuilder = new StringBuilder();
                stringBuilder.append("Instance classloader: ");
                stringBuilder.append(classLoader);
                stringBuilder.append(", base type classloader: ");
                stringBuilder.append(classLoader2);
                initCause = new ClassCastException(stringBuilder.toString()).initCause(e);
                Intrinsics.checkExpressionValueIsNotNull(initCause, "ClassCastException(\"Inst…baseTypeCL\").initCause(e)");
                throw initCause;
            } catch (ClassNotFoundException e2) {
                try {
                    newInstance = Class.forName("kotlin.internal.JRE8PlatformImplementations").newInstance();
                    Intrinsics.checkExpressionValueIsNotNull(newInstance, "Class.forName(\"kotlin.in…entations\").newInstance()");
                    if (newInstance != null) {
                        try {
                            platformImplementations = (PlatformImplementations) newInstance;
                        } catch (ClassCastException e3) {
                            classLoader = newInstance.getClass().getClassLoader();
                            classLoader2 = PlatformImplementations.class.getClassLoader();
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Instance classloader: ");
                            stringBuilder.append(classLoader);
                            stringBuilder.append(", base type classloader: ");
                            stringBuilder.append(classLoader2);
                            initCause = new ClassCastException(stringBuilder.toString()).initCause(e3);
                            Intrinsics.checkExpressionValueIsNotNull(initCause, "ClassCastException(\"Inst…baseTypeCL\").initCause(e)");
                            throw initCause;
                        }
                    }
                    throw new TypeCastException("null cannot be cast to non-null type kotlin.internal.PlatformImplementations");
                } catch (ClassNotFoundException e4) {
                }
            }
        } else if (version >= 65543) {
            try {
                newInstance = Class.forName("kotlin.internal.jdk7.JDK7PlatformImplementations").newInstance();
                Intrinsics.checkExpressionValueIsNotNull(newInstance, "Class.forName(\"kotlin.in…entations\").newInstance()");
                if (newInstance != null) {
                    platformImplementations = (PlatformImplementations) newInstance;
                } else {
                    throw new TypeCastException("null cannot be cast to non-null type kotlin.internal.PlatformImplementations");
                }
            } catch (ClassCastException e32) {
                classLoader = newInstance.getClass().getClassLoader();
                classLoader2 = PlatformImplementations.class.getClassLoader();
                stringBuilder = new StringBuilder();
                stringBuilder.append("Instance classloader: ");
                stringBuilder.append(classLoader);
                stringBuilder.append(", base type classloader: ");
                stringBuilder.append(classLoader2);
                initCause = new ClassCastException(stringBuilder.toString()).initCause(e32);
                Intrinsics.checkExpressionValueIsNotNull(initCause, "ClassCastException(\"Inst…baseTypeCL\").initCause(e)");
                throw initCause;
            } catch (ClassNotFoundException e5) {
                try {
                    newInstance = Class.forName("kotlin.internal.JRE7PlatformImplementations").newInstance();
                    Intrinsics.checkExpressionValueIsNotNull(newInstance, "Class.forName(\"kotlin.in…entations\").newInstance()");
                    if (newInstance != null) {
                        try {
                            platformImplementations = (PlatformImplementations) newInstance;
                        } catch (ClassCastException e322) {
                            classLoader = newInstance.getClass().getClassLoader();
                            classLoader2 = PlatformImplementations.class.getClassLoader();
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Instance classloader: ");
                            stringBuilder.append(classLoader);
                            stringBuilder.append(", base type classloader: ");
                            stringBuilder.append(classLoader2);
                            initCause = new ClassCastException(stringBuilder.toString()).initCause(e322);
                            Intrinsics.checkExpressionValueIsNotNull(initCause, "ClassCastException(\"Inst…baseTypeCL\").initCause(e)");
                            throw initCause;
                        }
                    }
                    throw new TypeCastException("null cannot be cast to non-null type kotlin.internal.PlatformImplementations");
                } catch (ClassNotFoundException e6) {
                }
            }
        } else {
            platformImplementations = new PlatformImplementations();
        }
        IMPLEMENTATIONS = platformImplementations;
    }

    @InlineOnly
    private static final <T> T castToBaseType(Object instance) {
        try {
            Intrinsics.reifiedOperationMarker(1, "T");
            return instance;
        } catch (ClassCastException e) {
            ClassLoader instanceCL = instance.getClass().getClassLoader();
            Intrinsics.reifiedOperationMarker(4, "T");
            ClassLoader baseTypeCL = Object.class.getClassLoader();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Instance classloader: ");
            stringBuilder.append(instanceCL);
            stringBuilder.append(", base type classloader: ");
            stringBuilder.append(baseTypeCL);
            Throwable initCause = new ClassCastException(stringBuilder.toString()).initCause(e);
            Intrinsics.checkExpressionValueIsNotNull(initCause, "ClassCastException(\"Inst…baseTypeCL\").initCause(e)");
            throw initCause;
        }
    }

    private static final int getJavaVersion() {
        String version = System.getProperty("java.specification.version");
        if (version == null) {
            return 65542;
        }
        int firstDot = StringsKt.indexOf$default(version, '.', 0, false, 6, null);
        if (firstDot < 0) {
            int parseInt;
            try {
                parseInt = 65536 * Integer.parseInt(version);
            } catch (NumberFormatException e) {
                parseInt = 65542;
            }
            return parseInt;
        }
        int secondDot = StringsKt.indexOf$default(version, '.', firstDot + 1, false, 4, null);
        if (secondDot < 0) {
            secondDot = version.length();
        }
        if (version != null) {
            String firstPart = version.substring(null, firstDot);
            Intrinsics.checkExpressionValueIsNotNull(firstPart, "(this as java.lang.Strin…ing(startIndex, endIndex)");
            String secondPart = firstDot + 1;
            if (version != null) {
                NumberFormatException e2;
                secondPart = version.substring(secondPart, secondDot);
                Intrinsics.checkExpressionValueIsNotNull(secondPart, "(this as java.lang.Strin…ing(startIndex, endIndex)");
                try {
                    e2 = Integer.parseInt(secondPart) + (Integer.parseInt(firstPart) * 65536);
                } catch (NumberFormatException e3) {
                    e2 = 65542;
                }
                return e2;
            }
            throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    @SinceKotlin(version = "1.2")
    @PublishedApi
    public static final boolean apiVersionIsAtLeast(int major, int minor, int patch) {
        return KotlinVersion.CURRENT.isAtLeast(major, minor, patch);
    }
}
