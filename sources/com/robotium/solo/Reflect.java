package com.robotium.solo;

import java.lang.reflect.Field;

class Reflect {
    private Object object;

    public class FieldRf {
        private Class<?> clazz;
        private String name;
        private Object object;

        public FieldRf(Object object, String name) {
            this.object = object;
            this.name = name;
        }

        public <T> T out(Class<T> outclazz) {
            return outclazz.cast(getValue(getField()));
        }

        public void in(Object value) {
            try {
                getField().set(this.object, value);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            }
        }

        public FieldRf type(Class<?> clazz) {
            this.clazz = clazz;
            return this;
        }

        private Field getField() {
            if (this.clazz == null) {
                this.clazz = this.object.getClass();
            }
            Field field = null;
            try {
                field = this.clazz.getDeclaredField(this.name);
                field.setAccessible(true);
            } catch (NoSuchFieldException e) {
            }
            return field;
        }

        private Object getValue(Field field) {
            if (field == null) {
                return null;
            }
            Object obj = null;
            try {
                obj = field.get(this.object);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            }
            return obj;
        }
    }

    public Reflect(Object object) {
        if (object != null) {
            this.object = object;
            return;
        }
        throw new IllegalArgumentException("Object can not be null.");
    }

    public FieldRf field(String name) {
        return new FieldRf(this.object, name);
    }
}
