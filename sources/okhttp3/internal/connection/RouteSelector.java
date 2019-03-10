package okhttp3.internal.connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import okhttp3.Address;
import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.HttpUrl;
import okhttp3.Route;
import okhttp3.internal.Util;

public final class RouteSelector {
    private final Address address;
    private final Call call;
    private final EventListener eventListener;
    private List<InetSocketAddress> inetSocketAddresses = Collections.emptyList();
    private int nextProxyIndex;
    private final List<Route> postponedRoutes = new ArrayList();
    private List<Proxy> proxies = Collections.emptyList();
    private final RouteDatabase routeDatabase;

    public static final class Selection {
        private int nextRouteIndex = 0;
        private final List<Route> routes;

        Selection(List<Route> routes) {
            this.routes = routes;
        }

        public boolean hasNext() {
            return this.nextRouteIndex < this.routes.size();
        }

        public Route next() {
            if (hasNext()) {
                List list = this.routes;
                int i = this.nextRouteIndex;
                this.nextRouteIndex = i + 1;
                return (Route) list.get(i);
            }
            throw new NoSuchElementException();
        }

        public List<Route> getAll() {
            return new ArrayList(this.routes);
        }
    }

    private void resetNextInetSocketAddress(java.net.Proxy r9) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:28:0x00f4 in {4, 7, 9, 10, 17, 22, 23, 25, 27} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r8 = this;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r8.inetSocketAddresses = r0;
        r0 = r9.type();
        r1 = java.net.Proxy.Type.DIRECT;
        if (r0 == r1) goto L_0x0047;
    L_0x000f:
        r0 = r9.type();
        r1 = java.net.Proxy.Type.SOCKS;
        if (r0 != r1) goto L_0x0018;
    L_0x0017:
        goto L_0x0047;
    L_0x0018:
        r0 = r9.address();
        r1 = r0 instanceof java.net.InetSocketAddress;
        if (r1 == 0) goto L_0x002c;
    L_0x0020:
        r1 = r0;
        r1 = (java.net.InetSocketAddress) r1;
        r2 = getHostString(r1);
        r0 = r1.getPort();
        goto L_0x005c;
    L_0x002c:
        r1 = new java.lang.IllegalArgumentException;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Proxy.address() is not an InetSocketAddress: ";
        r2.append(r3);
        r3 = r0.getClass();
        r2.append(r3);
        r2 = r2.toString();
        r1.<init>(r2);
        throw r1;
        r0 = r8.address;
        r0 = r0.url();
        r2 = r0.host();
        r0 = r8.address;
        r0 = r0.url();
        r0 = r0.port();
    L_0x005c:
        r1 = 1;
        if (r0 < r1) goto L_0x00cf;
    L_0x005f:
        r1 = 65535; // 0xffff float:9.1834E-41 double:3.23786E-319;
        if (r0 > r1) goto L_0x00cf;
    L_0x0064:
        r1 = r9.type();
        r3 = java.net.Proxy.Type.SOCKS;
        if (r1 != r3) goto L_0x0076;
    L_0x006c:
        r1 = r8.inetSocketAddresses;
        r3 = java.net.InetSocketAddress.createUnresolved(r2, r0);
        r1.add(r3);
        goto L_0x00ae;
    L_0x0076:
        r1 = r8.eventListener;
        r3 = r8.call;
        r1.dnsStart(r3, r2);
        r1 = r8.address;
        r1 = r1.dns();
        r1 = r1.lookup(r2);
        r3 = r1.isEmpty();
        if (r3 != 0) goto L_0x00af;
    L_0x008d:
        r3 = r8.eventListener;
        r4 = r8.call;
        r3.dnsEnd(r4, r2, r1);
        r3 = 0;
        r4 = r1.size();
    L_0x0099:
        if (r3 >= r4) goto L_0x00ae;
    L_0x009b:
        r5 = r1.get(r3);
        r5 = (java.net.InetAddress) r5;
        r6 = r8.inetSocketAddresses;
        r7 = new java.net.InetSocketAddress;
        r7.<init>(r5, r0);
        r6.add(r7);
        r3 = r3 + 1;
        goto L_0x0099;
    L_0x00ae:
        return;
    L_0x00af:
        r3 = new java.net.UnknownHostException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = r8.address;
        r5 = r5.dns();
        r4.append(r5);
        r5 = " returned no addresses for ";
        r4.append(r5);
        r4.append(r2);
        r4 = r4.toString();
        r3.<init>(r4);
        throw r3;
        r1 = new java.net.SocketException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "No route to ";
        r3.append(r4);
        r3.append(r2);
        r4 = ":";
        r3.append(r4);
        r3.append(r0);
        r4 = "; port is out of range";
        r3.append(r4);
        r3 = r3.toString();
        r1.<init>(r3);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.RouteSelector.resetNextInetSocketAddress(java.net.Proxy):void");
    }

    public okhttp3.internal.connection.RouteSelector.Selection next() throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:25:0x0069 in {9, 10, 11, 14, 15, 16, 19, 20, 22, 24} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r7 = this;
        r0 = r7.hasNext();
        if (r0 == 0) goto L_0x0063;
    L_0x0006:
        r0 = new java.util.ArrayList;
        r0.<init>();
    L_0x000b:
        r1 = r7.hasNextProxy();
        if (r1 == 0) goto L_0x004a;
    L_0x0011:
        r1 = r7.nextProxy();
        r2 = 0;
        r3 = r7.inetSocketAddresses;
        r3 = r3.size();
    L_0x001c:
        if (r2 >= r3) goto L_0x0041;
    L_0x001e:
        r4 = new okhttp3.Route;
        r5 = r7.address;
        r6 = r7.inetSocketAddresses;
        r6 = r6.get(r2);
        r6 = (java.net.InetSocketAddress) r6;
        r4.<init>(r5, r1, r6);
        r5 = r7.routeDatabase;
        r5 = r5.shouldPostpone(r4);
        if (r5 == 0) goto L_0x003b;
    L_0x0035:
        r5 = r7.postponedRoutes;
        r5.add(r4);
        goto L_0x003e;
    L_0x003b:
        r0.add(r4);
    L_0x003e:
        r2 = r2 + 1;
        goto L_0x001c;
    L_0x0041:
        r2 = r0.isEmpty();
        if (r2 != 0) goto L_0x0048;
    L_0x0047:
        goto L_0x004b;
        goto L_0x000b;
    L_0x004b:
        r1 = r0.isEmpty();
        if (r1 == 0) goto L_0x005c;
    L_0x0051:
        r1 = r7.postponedRoutes;
        r0.addAll(r1);
        r1 = r7.postponedRoutes;
        r1.clear();
        goto L_0x005d;
    L_0x005d:
        r1 = new okhttp3.internal.connection.RouteSelector$Selection;
        r1.<init>(r0);
        return r1;
    L_0x0063:
        r0 = new java.util.NoSuchElementException;
        r0.<init>();
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.RouteSelector.next():okhttp3.internal.connection.RouteSelector$Selection");
    }

    public RouteSelector(Address address, RouteDatabase routeDatabase, Call call, EventListener eventListener) {
        this.address = address;
        this.routeDatabase = routeDatabase;
        this.call = call;
        this.eventListener = eventListener;
        resetNextProxy(address.url(), address.proxy());
    }

    public boolean hasNext() {
        if (!hasNextProxy()) {
            if (this.postponedRoutes.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void connectFailed(Route failedRoute, IOException failure) {
        if (failedRoute.proxy().type() != Type.DIRECT && this.address.proxySelector() != null) {
            this.address.proxySelector().connectFailed(this.address.url().uri(), failedRoute.proxy().address(), failure);
        }
        this.routeDatabase.failed(failedRoute);
    }

    private void resetNextProxy(HttpUrl url, Proxy proxy) {
        if (proxy != null) {
            this.proxies = Collections.singletonList(proxy);
        } else {
            List immutableList;
            List<Proxy> proxiesOrNull = this.address.proxySelector().select(url.uri());
            if (proxiesOrNull == null || proxiesOrNull.isEmpty()) {
                immutableList = Util.immutableList(new Proxy[]{Proxy.NO_PROXY});
            } else {
                immutableList = Util.immutableList(proxiesOrNull);
            }
            this.proxies = immutableList;
        }
        this.nextProxyIndex = 0;
    }

    private boolean hasNextProxy() {
        return this.nextProxyIndex < this.proxies.size();
    }

    private Proxy nextProxy() throws IOException {
        if (hasNextProxy()) {
            List list = this.proxies;
            int i = this.nextProxyIndex;
            this.nextProxyIndex = i + 1;
            Proxy result = (Proxy) list.get(i);
            resetNextInetSocketAddress(result);
            return result;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("No route to ");
        stringBuilder.append(this.address.url().host());
        stringBuilder.append("; exhausted proxy configurations: ");
        stringBuilder.append(this.proxies);
        throw new SocketException(stringBuilder.toString());
    }

    static String getHostString(InetSocketAddress socketAddress) {
        InetAddress address = socketAddress.getAddress();
        if (address == null) {
            return socketAddress.getHostName();
        }
        return address.getHostAddress();
    }
}
