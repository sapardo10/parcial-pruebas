package org.objenesis.instantiator.basic;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.annotations.Instantiator;
import org.objenesis.instantiator.annotations.Typology;
import org.objenesis.instantiator.util.ClassDefinitionUtils;

@Instantiator(Typology.STANDARD)
public class ProxyingInstantiator<T> implements ObjectInstantiator<T> {
    private static final byte[] CODE = new byte[]{ClassDefinitionUtils.OPS_aload_0, ClassDefinitionUtils.OPS_return};
    private static final int CODE_ATTRIBUTE_LENGTH = (CODE.length + 12);
    private static int CONSTANT_POOL_COUNT = 9;
    private static final String CONSTRUCTOR_DESC = "()V";
    private static final String CONSTRUCTOR_NAME = "<init>";
    private static final int INDEX_CLASS_SUPERCLASS = 2;
    private static final int INDEX_CLASS_THIS = 1;
    private static final int INDEX_UTF8_CLASS = 7;
    private static final int INDEX_UTF8_CODE_ATTRIBUTE = 5;
    private static final int INDEX_UTF8_CONSTRUCTOR_DESC = 4;
    private static final int INDEX_UTF8_CONSTRUCTOR_NAME = 3;
    private static final int INDEX_UTF8_SUPERCLASS = 8;
    private static final String SUFFIX = "$$$Objenesis";
    private final Class<?> newType;

    public ProxyingInstantiator(Class<T> type) {
        byte[] classBytes = writeExtendingClass(type, SUFFIX);
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(type.getName());
            stringBuilder.append(SUFFIX);
            this.newType = ClassDefinitionUtils.defineClass(stringBuilder.toString(), classBytes, type.getClassLoader());
        } catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }

    public T newInstance() {
        try {
            return this.newType.newInstance();
        } catch (InstantiationException e) {
            throw new ObjenesisException(e);
        } catch (IllegalAccessException e2) {
            throw new ObjenesisException(e2);
        }
    }

    private static byte[] writeExtendingClass(Class<?> type, String suffix) {
        String parentClazz = ClassDefinitionUtils.classNameToInternalClassName(type.getName());
        String clazz = new StringBuilder();
        clazz.append(parentClazz);
        clazz.append(suffix);
        clazz = clazz.toString();
        DataOutputStream in = null;
        ByteArrayOutputStream bIn = new ByteArrayOutputStream(1000);
        try {
            in = new DataOutputStream(bIn);
            in.write(ClassDefinitionUtils.MAGIC);
            in.write(ClassDefinitionUtils.VERSION);
            in.writeShort(CONSTANT_POOL_COUNT);
            in.writeByte(7);
            in.writeShort(7);
            in.writeByte(7);
            in.writeShort(8);
            in.writeByte(1);
            in.writeUTF(CONSTRUCTOR_NAME);
            in.writeByte(1);
            in.writeUTF(CONSTRUCTOR_DESC);
            in.writeByte(1);
            in.writeUTF("Code");
            in.writeByte(1);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("L");
            stringBuilder.append(clazz);
            stringBuilder.append(";");
            in.writeUTF(stringBuilder.toString());
            in.writeByte(1);
            in.writeUTF(clazz);
            in.writeByte(1);
            in.writeUTF(parentClazz);
            in.writeShort(33);
            in.writeShort(1);
            in.writeShort(2);
            in.writeShort(0);
            in.writeShort(0);
            in.writeShort(1);
            in.writeShort(1);
            in.writeShort(3);
            in.writeShort(4);
            in.writeShort(1);
            in.writeShort(5);
            in.writeInt(CODE_ATTRIBUTE_LENGTH);
            in.writeShort(1);
            in.writeShort(1);
            in.writeInt(CODE.length);
            in.write(CODE);
            in.writeShort(0);
            in.writeShort(0);
            in.writeShort(0);
            try {
                in.close();
                return bIn.toByteArray();
            } catch (IOException e) {
                throw new ObjenesisException(e);
            }
        } catch (IOException e2) {
            throw new ObjenesisException(e2);
        } catch (Throwable th) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e22) {
                    throw new ObjenesisException(e22);
                }
            }
        }
    }
}
