package org.objenesis.instantiator.util;

import com.google.android.exoplayer2.DefaultLoadControl;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import org.apache.commons.io.IOUtils;

public final class ClassDefinitionUtils {
    public static final int ACC_ABSTRACT = 1024;
    public static final int ACC_ANNOTATION = 8192;
    public static final int ACC_ENUM = 16384;
    public static final int ACC_FINAL = 16;
    public static final int ACC_INTERFACE = 512;
    public static final int ACC_PUBLIC = 1;
    public static final int ACC_SUPER = 32;
    public static final int ACC_SYNTHETIC = 4096;
    public static final int CONSTANT_Class = 7;
    public static final int CONSTANT_Double = 6;
    public static final int CONSTANT_Fieldref = 9;
    public static final int CONSTANT_Float = 4;
    public static final int CONSTANT_Integer = 3;
    public static final int CONSTANT_InterfaceMethodref = 11;
    public static final int CONSTANT_InvokeDynamic = 18;
    public static final int CONSTANT_Long = 5;
    public static final int CONSTANT_MethodHandle = 15;
    public static final int CONSTANT_MethodType = 16;
    public static final int CONSTANT_Methodref = 10;
    public static final int CONSTANT_NameAndType = 12;
    public static final int CONSTANT_String = 8;
    public static final int CONSTANT_Utf8 = 1;
    public static final byte[] MAGIC = new byte[]{(byte) -54, (byte) -2, (byte) -70, (byte) -66};
    public static final byte OPS_aload_0 = (byte) 42;
    public static final byte OPS_areturn = (byte) -80;
    public static final byte OPS_dup = (byte) 89;
    public static final byte OPS_invokespecial = (byte) -73;
    public static final byte OPS_new = (byte) -69;
    public static final byte OPS_return = (byte) -79;
    private static final ProtectionDomain PROTECTION_DOMAIN = ((ProtectionDomain) AccessController.doPrivileged(new C11871()));
    public static final byte[] VERSION = new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 49};

    /* renamed from: org.objenesis.instantiator.util.ClassDefinitionUtils$1 */
    static class C11871 implements PrivilegedAction<ProtectionDomain> {
        C11871() {
        }

        public ProtectionDomain run() {
            return ClassDefinitionUtils.class.getProtectionDomain();
        }
    }

    private ClassDefinitionUtils() {
    }

    public static <T> Class<T> defineClass(String className, byte[] b, ClassLoader loader) throws Exception {
        Class<T> c = UnsafeUtils.getUnsafe().defineClass(className, b, 0, b.length, loader, PROTECTION_DOMAIN);
        Class.forName(className, true, loader);
        return c;
    }

    public static byte[] readClass(String className) throws IOException {
        byte[] b = new byte[DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS];
        InputStream in = ClassDefinitionUtils.class.getClassLoader().getResourceAsStream(classNameToResource(className));
        try {
            int length = in.read(b);
            if (length < DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS) {
                byte[] copy = new byte[length];
                System.arraycopy(b, 0, copy, 0, length);
                return copy;
            }
            throw new IllegalArgumentException("The class is longer that 2500 bytes which is currently unsupported");
        } finally {
            in.close();
        }
    }

    public static void writeClass(String fileName, byte[] bytes) throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileName));
        try {
            out.write(bytes);
        } finally {
            out.close();
        }
    }

    public static String classNameToInternalClassName(String className) {
        return className.replace('.', IOUtils.DIR_SEPARATOR_UNIX);
    }

    public static String classNameToResource(String className) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(classNameToInternalClassName(className));
        stringBuilder.append(".class");
        return stringBuilder.toString();
    }

    public static <T> Class<T> getExistingClass(ClassLoader classLoader, String className) {
        try {
            return Class.forName(className, true, classLoader);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
