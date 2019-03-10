package org.objenesis.instantiator.basic;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.instantiator.annotations.Instantiator;
import org.objenesis.instantiator.annotations.Typology;

@Instantiator(Typology.SERIALIZATION)
public class ObjectInputStreamInstantiator<T> implements ObjectInstantiator<T> {
    private ObjectInputStream inputStream;

    public ObjectInputStreamInstantiator(Class<T> clazz) {
        if (Serializable.class.isAssignableFrom(clazz)) {
            try {
                this.inputStream = new ObjectInputStream(new ObjectInputStreamInstantiator$MockStream(clazz));
                return;
            } catch (IOException e) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("IOException: ");
                stringBuilder.append(e.getMessage());
                throw new Error(stringBuilder.toString());
            }
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append(clazz);
        stringBuilder.append(" not serializable");
        throw new ObjenesisException(new NotSerializableException(stringBuilder.toString()));
    }

    public T newInstance() {
        try {
            return this.inputStream.readObject();
        } catch (ClassNotFoundException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("ClassNotFoundException: ");
            stringBuilder.append(e.getMessage());
            throw new Error(stringBuilder.toString());
        } catch (Exception e2) {
            throw new ObjenesisException(e2);
        }
    }
}
