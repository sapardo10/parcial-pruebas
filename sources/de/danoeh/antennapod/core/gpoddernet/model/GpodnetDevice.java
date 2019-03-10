package de.danoeh.antennapod.core.gpoddernet.model;

import android.support.annotation.NonNull;

public class GpodnetDevice {
    private final String caption;
    private final String id;
    private final int subscriptions;
    private final DeviceType type;

    public enum DeviceType {
        DESKTOP,
        LAPTOP,
        MOBILE,
        SERVER,
        OTHER;

        static DeviceType fromString(String s) {
            if (s == null) {
                return OTHER;
            }
            Object obj = -1;
            int hashCode = s.hashCode();
            if (hashCode != -1109985830) {
                if (hashCode != -1068855134) {
                    if (hashCode != -905826493) {
                        if (hashCode == 1557106716 && s.equals("desktop")) {
                            obj = null;
                            switch (obj) {
                                case null:
                                    return DESKTOP;
                                case 1:
                                    return LAPTOP;
                                case 2:
                                    return MOBILE;
                                case 3:
                                    return SERVER;
                                default:
                                    return OTHER;
                            }
                        }
                    } else if (s.equals("server")) {
                        obj = 3;
                        switch (obj) {
                            case null:
                                return DESKTOP;
                            case 1:
                                return LAPTOP;
                            case 2:
                                return MOBILE;
                            case 3:
                                return SERVER;
                            default:
                                return OTHER;
                        }
                    }
                } else if (s.equals("mobile")) {
                    obj = 2;
                    switch (obj) {
                        case null:
                            return DESKTOP;
                        case 1:
                            return LAPTOP;
                        case 2:
                            return MOBILE;
                        case 3:
                            return SERVER;
                        default:
                            return OTHER;
                    }
                }
            } else if (s.equals("laptop")) {
                obj = 1;
                switch (obj) {
                    case null:
                        return DESKTOP;
                    case 1:
                        return LAPTOP;
                    case 2:
                        return MOBILE;
                    case 3:
                        return SERVER;
                    default:
                        return OTHER;
                }
            }
            switch (obj) {
                case null:
                    return DESKTOP;
                case 1:
                    return LAPTOP;
                case 2:
                    return MOBILE;
                case 3:
                    return SERVER;
                default:
                    return OTHER;
            }
        }

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public GpodnetDevice(@NonNull String id, String caption, String type, int subscriptions) {
        this.id = id;
        this.caption = caption;
        this.type = DeviceType.fromString(type);
        this.subscriptions = subscriptions;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("GpodnetDevice [id=");
        stringBuilder.append(this.id);
        stringBuilder.append(", caption=");
        stringBuilder.append(this.caption);
        stringBuilder.append(", type=");
        stringBuilder.append(this.type);
        stringBuilder.append(", subscriptions=");
        stringBuilder.append(this.subscriptions);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public String getId() {
        return this.id;
    }

    public String getCaption() {
        return this.caption;
    }

    public DeviceType getType() {
        return this.type;
    }

    public int getSubscriptions() {
        return this.subscriptions;
    }
}
