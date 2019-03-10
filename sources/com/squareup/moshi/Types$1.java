package com.squareup.moshi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class Types$1 implements InvocationHandler {
    final /* synthetic */ Class val$annotationType;

    Types$1(Class cls) {
        this.val$annotationType = cls;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object obj;
        StringBuilder stringBuilder;
        String methodName = method.getName();
        int hashCode = methodName.hashCode();
        if (hashCode != -1776922004) {
            if (hashCode != -1295482945) {
                if (hashCode != 147696667) {
                    if (hashCode == 1444986633 && methodName.equals("annotationType")) {
                        obj = null;
                        switch (obj) {
                            case null:
                                return this.val$annotationType;
                            case 1:
                                return Boolean.valueOf(this.val$annotationType.isInstance(args[0]));
                            case 2:
                                return Integer.valueOf(0);
                            case 3:
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("@");
                                stringBuilder.append(this.val$annotationType.getName());
                                stringBuilder.append("()");
                                return stringBuilder.toString();
                            default:
                                return method.invoke(proxy, args);
                        }
                    }
                } else if (methodName.equals("hashCode")) {
                    obj = 2;
                    switch (obj) {
                        case null:
                            return this.val$annotationType;
                        case 1:
                            return Boolean.valueOf(this.val$annotationType.isInstance(args[0]));
                        case 2:
                            return Integer.valueOf(0);
                        case 3:
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("@");
                            stringBuilder.append(this.val$annotationType.getName());
                            stringBuilder.append("()");
                            return stringBuilder.toString();
                        default:
                            return method.invoke(proxy, args);
                    }
                }
            } else if (methodName.equals("equals")) {
                obj = 1;
                switch (obj) {
                    case null:
                        return this.val$annotationType;
                    case 1:
                        return Boolean.valueOf(this.val$annotationType.isInstance(args[0]));
                    case 2:
                        return Integer.valueOf(0);
                    case 3:
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("@");
                        stringBuilder.append(this.val$annotationType.getName());
                        stringBuilder.append("()");
                        return stringBuilder.toString();
                    default:
                        return method.invoke(proxy, args);
                }
            }
        } else if (methodName.equals("toString")) {
            obj = 3;
            switch (obj) {
                case null:
                    return this.val$annotationType;
                case 1:
                    return Boolean.valueOf(this.val$annotationType.isInstance(args[0]));
                case 2:
                    return Integer.valueOf(0);
                case 3:
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("@");
                    stringBuilder.append(this.val$annotationType.getName());
                    stringBuilder.append("()");
                    return stringBuilder.toString();
                default:
                    return method.invoke(proxy, args);
            }
        }
        obj = -1;
        switch (obj) {
            case null:
                return this.val$annotationType;
            case 1:
                return Boolean.valueOf(this.val$annotationType.isInstance(args[0]));
            case 2:
                return Integer.valueOf(0);
            case 3:
                stringBuilder = new StringBuilder();
                stringBuilder.append("@");
                stringBuilder.append(this.val$annotationType.getName());
                stringBuilder.append("()");
                return stringBuilder.toString();
            default:
                return method.invoke(proxy, args);
        }
    }
}
