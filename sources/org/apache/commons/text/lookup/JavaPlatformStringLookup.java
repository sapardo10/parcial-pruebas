package org.apache.commons.text.lookup;

import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

final class JavaPlatformStringLookup extends AbstractStringLookup {
    static final JavaPlatformStringLookup INSTANCE = new JavaPlatformStringLookup();

    private JavaPlatformStringLookup() {
    }

    String getHardware() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("processors: ");
        stringBuilder.append(Runtime.getRuntime().availableProcessors());
        stringBuilder.append(", architecture: ");
        stringBuilder.append(getSystemProperty("os.arch"));
        stringBuilder.append(getSystemProperty("-", "sun.arch.data.model"));
        stringBuilder.append(getSystemProperty(", instruction sets: ", "sun.cpu.isalist"));
        return stringBuilder.toString();
    }

    String getLocale() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("default locale: ");
        stringBuilder.append(Locale.getDefault());
        stringBuilder.append(", platform encoding: ");
        stringBuilder.append(getSystemProperty("file.encoding"));
        return stringBuilder.toString();
    }

    String getOperatingSystem() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getSystemProperty("os.name"));
        stringBuilder.append(StringUtils.SPACE);
        stringBuilder.append(getSystemProperty("os.version"));
        stringBuilder.append(getSystemProperty(StringUtils.SPACE, "sun.os.patch.level"));
        stringBuilder.append(", architecture: ");
        stringBuilder.append(getSystemProperty("os.arch"));
        stringBuilder.append(getSystemProperty("-", "sun.arch.data.model"));
        return stringBuilder.toString();
    }

    String getRuntime() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getSystemProperty("java.runtime.name"));
        stringBuilder.append(" (build ");
        stringBuilder.append(getSystemProperty("java.runtime.version"));
        stringBuilder.append(") from ");
        stringBuilder.append(getSystemProperty("java.vendor"));
        return stringBuilder.toString();
    }

    private String getSystemProperty(String name) {
        return SystemPropertyStringLookup.INSTANCE.lookup(name);
    }

    private String getSystemProperty(String prefix, String name) {
        String value = getSystemProperty(name);
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(prefix);
        stringBuilder.append(value);
        return stringBuilder.toString();
    }

    String getVirtualMachine() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getSystemProperty("java.vm.name"));
        stringBuilder.append(" (build ");
        stringBuilder.append(getSystemProperty("java.vm.version"));
        stringBuilder.append(", ");
        stringBuilder.append(getSystemProperty("java.vm.info"));
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public String lookup(String key) {
        Object obj;
        StringBuilder stringBuilder;
        int hashCode = key.hashCode();
        if (hashCode != -1097462182) {
            if (hashCode != 3556) {
                if (hashCode != 3767) {
                    if (hashCode != 116909544) {
                        if (hashCode != 351608024) {
                            if (hashCode == 1550962648 && key.equals("runtime")) {
                                obj = 1;
                                switch (obj) {
                                    case null:
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("Java version ");
                                        stringBuilder.append(getSystemProperty("java.version"));
                                        return stringBuilder.toString();
                                    case 1:
                                        return getRuntime();
                                    case 2:
                                        return getVirtualMachine();
                                    case 3:
                                        return getOperatingSystem();
                                    case 4:
                                        return getHardware();
                                    case 5:
                                        return getLocale();
                                    default:
                                        throw new IllegalArgumentException(key);
                                }
                            }
                        } else if (key.equals("version")) {
                            obj = null;
                            switch (obj) {
                                case null:
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("Java version ");
                                    stringBuilder.append(getSystemProperty("java.version"));
                                    return stringBuilder.toString();
                                case 1:
                                    return getRuntime();
                                case 2:
                                    return getVirtualMachine();
                                case 3:
                                    return getOperatingSystem();
                                case 4:
                                    return getHardware();
                                case 5:
                                    return getLocale();
                                default:
                                    throw new IllegalArgumentException(key);
                            }
                        }
                    } else if (key.equals("hardware")) {
                        obj = 4;
                        switch (obj) {
                            case null:
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("Java version ");
                                stringBuilder.append(getSystemProperty("java.version"));
                                return stringBuilder.toString();
                            case 1:
                                return getRuntime();
                            case 2:
                                return getVirtualMachine();
                            case 3:
                                return getOperatingSystem();
                            case 4:
                                return getHardware();
                            case 5:
                                return getLocale();
                            default:
                                throw new IllegalArgumentException(key);
                        }
                    }
                } else if (key.equals("vm")) {
                    obj = 2;
                    switch (obj) {
                        case null:
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Java version ");
                            stringBuilder.append(getSystemProperty("java.version"));
                            return stringBuilder.toString();
                        case 1:
                            return getRuntime();
                        case 2:
                            return getVirtualMachine();
                        case 3:
                            return getOperatingSystem();
                        case 4:
                            return getHardware();
                        case 5:
                            return getLocale();
                        default:
                            throw new IllegalArgumentException(key);
                    }
                }
            } else if (key.equals("os")) {
                obj = 3;
                switch (obj) {
                    case null:
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Java version ");
                        stringBuilder.append(getSystemProperty("java.version"));
                        return stringBuilder.toString();
                    case 1:
                        return getRuntime();
                    case 2:
                        return getVirtualMachine();
                    case 3:
                        return getOperatingSystem();
                    case 4:
                        return getHardware();
                    case 5:
                        return getLocale();
                    default:
                        throw new IllegalArgumentException(key);
                }
            }
        } else if (key.equals("locale")) {
            obj = 5;
            switch (obj) {
                case null:
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Java version ");
                    stringBuilder.append(getSystemProperty("java.version"));
                    return stringBuilder.toString();
                case 1:
                    return getRuntime();
                case 2:
                    return getVirtualMachine();
                case 3:
                    return getOperatingSystem();
                case 4:
                    return getHardware();
                case 5:
                    return getLocale();
                default:
                    throw new IllegalArgumentException(key);
            }
        }
        obj = -1;
        switch (obj) {
            case null:
                stringBuilder = new StringBuilder();
                stringBuilder.append("Java version ");
                stringBuilder.append(getSystemProperty("java.version"));
                return stringBuilder.toString();
            case 1:
                return getRuntime();
            case 2:
                return getVirtualMachine();
            case 3:
                return getOperatingSystem();
            case 4:
                return getHardware();
            case 5:
                return getLocale();
            default:
                throw new IllegalArgumentException(key);
        }
    }
}
