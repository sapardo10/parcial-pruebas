package android.support.v7.media;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class MediaRouteProviderDescriptor {
    private static final String KEY_ROUTES = "routes";
    private final Bundle mBundle;
    private List<MediaRouteDescriptor> mRoutes;

    public static final class Builder {
        private final Bundle mBundle;
        private ArrayList<MediaRouteDescriptor> mRoutes;

        public android.support.v7.media.MediaRouteProviderDescriptor.Builder addRoutes(java.util.Collection<android.support.v7.media.MediaRouteDescriptor> r3) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:12:0x0028 in {6, 7, 8, 9, 11} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r2 = this;
            if (r3 == 0) goto L_0x001f;
        L_0x0002:
            r0 = r3.isEmpty();
            if (r0 != 0) goto L_0x001d;
        L_0x0008:
            r0 = r3.iterator();
        L_0x000c:
            r1 = r0.hasNext();
            if (r1 == 0) goto L_0x001c;
        L_0x0012:
            r1 = r0.next();
            r1 = (android.support.v7.media.MediaRouteDescriptor) r1;
            r2.addRoute(r1);
            goto L_0x000c;
        L_0x001c:
            goto L_0x001e;
        L_0x001e:
            return r2;
        L_0x001f:
            r0 = new java.lang.IllegalArgumentException;
            r1 = "routes must not be null";
            r0.<init>(r1);
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.media.MediaRouteProviderDescriptor.Builder.addRoutes(java.util.Collection):android.support.v7.media.MediaRouteProviderDescriptor$Builder");
        }

        public Builder() {
            this.mBundle = new Bundle();
        }

        public Builder(MediaRouteProviderDescriptor descriptor) {
            if (descriptor != null) {
                this.mBundle = new Bundle(descriptor.mBundle);
                descriptor.ensureRoutes();
                if (!descriptor.mRoutes.isEmpty()) {
                    this.mRoutes = new ArrayList(descriptor.mRoutes);
                    return;
                }
                return;
            }
            throw new IllegalArgumentException("descriptor must not be null");
        }

        public Builder addRoute(MediaRouteDescriptor route) {
            if (route != null) {
                ArrayList arrayList = this.mRoutes;
                if (arrayList == null) {
                    this.mRoutes = new ArrayList();
                } else if (arrayList.contains(route)) {
                    throw new IllegalArgumentException("route descriptor already added");
                }
                this.mRoutes.add(route);
                return this;
            }
            throw new IllegalArgumentException("route must not be null");
        }

        Builder setRoutes(Collection<MediaRouteDescriptor> routes) {
            if (routes != null) {
                if (!routes.isEmpty()) {
                    this.mRoutes = new ArrayList(routes);
                    return this;
                }
            }
            this.mRoutes = null;
            this.mBundle.remove(MediaRouteProviderDescriptor.KEY_ROUTES);
            return this;
        }

        public MediaRouteProviderDescriptor build() {
            int count = this.mRoutes;
            if (count != 0) {
                count = count.size();
                ArrayList<Bundle> routeBundles = new ArrayList(count);
                for (int i = 0; i < count; i++) {
                    routeBundles.add(((MediaRouteDescriptor) this.mRoutes.get(i)).asBundle());
                }
                this.mBundle.putParcelableArrayList(MediaRouteProviderDescriptor.KEY_ROUTES, routeBundles);
            }
            return new MediaRouteProviderDescriptor(this.mBundle, this.mRoutes);
        }
    }

    private MediaRouteProviderDescriptor(Bundle bundle, List<MediaRouteDescriptor> routes) {
        this.mBundle = bundle;
        this.mRoutes = routes;
    }

    public List<MediaRouteDescriptor> getRoutes() {
        ensureRoutes();
        return this.mRoutes;
    }

    private void ensureRoutes() {
        if (this.mRoutes == null) {
            ArrayList<Bundle> routeBundles = this.mBundle.getParcelableArrayList(KEY_ROUTES);
            if (routeBundles != null) {
                if (!routeBundles.isEmpty()) {
                    int count = routeBundles.size();
                    this.mRoutes = new ArrayList(count);
                    for (int i = 0; i < count; i++) {
                        this.mRoutes.add(MediaRouteDescriptor.fromBundle((Bundle) routeBundles.get(i)));
                    }
                    return;
                }
            }
            this.mRoutes = Collections.emptyList();
        }
    }

    public boolean isValid() {
        ensureRoutes();
        int routeCount = this.mRoutes.size();
        int i = 0;
        while (i < routeCount) {
            MediaRouteDescriptor route = (MediaRouteDescriptor) this.mRoutes.get(i);
            if (route != null) {
                if (route.isValid()) {
                    i++;
                }
            }
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("MediaRouteProviderDescriptor{ ");
        result.append("routes=");
        result.append(Arrays.toString(getRoutes().toArray()));
        result.append(", isValid=");
        result.append(isValid());
        result.append(" }");
        return result.toString();
    }

    public Bundle asBundle() {
        return this.mBundle;
    }

    public static MediaRouteProviderDescriptor fromBundle(Bundle bundle) {
        return bundle != null ? new MediaRouteProviderDescriptor(bundle, null) : null;
    }
}
