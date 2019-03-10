package org.apache.commons.text.lookup;

import de.danoeh.antennapod.core.storage.PodDBAdapter;
import java.net.InetAddress;
import java.net.UnknownHostException;

final class LocalHostStringLookup extends AbstractStringLookup {
    static final LocalHostStringLookup INSTANCE = new LocalHostStringLookup();

    private LocalHostStringLookup() {
    }

    public String lookup(String key) {
        Object obj;
        int hashCode = key.hashCode();
        if (hashCode != -1147692044) {
            if (hashCode != 3373707) {
                if (hashCode == 1339224004 && key.equals("canonical-name")) {
                    obj = 1;
                    switch (obj) {
                        case null:
                            try {
                                return InetAddress.getLocalHost().getHostName();
                            } catch (UnknownHostException e) {
                                return null;
                            }
                        case 1:
                            try {
                                return InetAddress.getLocalHost().getCanonicalHostName();
                            } catch (UnknownHostException e2) {
                                return null;
                            }
                        case 2:
                            try {
                                return InetAddress.getLocalHost().getHostAddress();
                            } catch (UnknownHostException e3) {
                                return null;
                            }
                        default:
                            throw new IllegalArgumentException(key);
                    }
                }
            } else if (key.equals(PodDBAdapter.KEY_NAME)) {
                obj = null;
                switch (obj) {
                    case null:
                        return InetAddress.getLocalHost().getHostName();
                    case 1:
                        return InetAddress.getLocalHost().getCanonicalHostName();
                    case 2:
                        return InetAddress.getLocalHost().getHostAddress();
                    default:
                        throw new IllegalArgumentException(key);
                }
            }
        } else if (key.equals("address")) {
            obj = 2;
            switch (obj) {
                case null:
                    return InetAddress.getLocalHost().getHostName();
                case 1:
                    return InetAddress.getLocalHost().getCanonicalHostName();
                case 2:
                    return InetAddress.getLocalHost().getHostAddress();
                default:
                    throw new IllegalArgumentException(key);
            }
        }
        obj = -1;
        switch (obj) {
            case null:
                return InetAddress.getLocalHost().getHostName();
            case 1:
                return InetAddress.getLocalHost().getCanonicalHostName();
            case 2:
                return InetAddress.getLocalHost().getHostAddress();
            default:
                throw new IllegalArgumentException(key);
        }
    }
}
