package okhttp3.internal.platform;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import okhttp3.internal.Util;

class JdkWithJettyBootPlatform$JettyNegoProvider implements InvocationHandler {
    private final List<String> protocols;
    String selected;
    boolean unsupported;

    JdkWithJettyBootPlatform$JettyNegoProvider(List<String> protocols) {
        this.protocols = protocols;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Class<?> returnType = method.getReturnType();
        if (args == null) {
            args = Util.EMPTY_STRING_ARRAY;
        }
        if (methodName.equals("supports") && Boolean.TYPE == returnType) {
            return Boolean.valueOf(true);
        }
        if (methodName.equals("unsupported") && Void.TYPE == returnType) {
            this.unsupported = true;
            return null;
        } else if (methodName.equals("protocols") && args.length == 0) {
            return this.protocols;
        } else {
            if ((methodName.equals("selectProtocol") || methodName.equals("select")) && String.class == returnType && args.length == 1 && (args[0] instanceof List)) {
                List<String> peerProtocols = args[0];
                int size = peerProtocols.size();
                for (int i = 0; i < size; i++) {
                    if (this.protocols.contains(peerProtocols.get(i))) {
                        String str = (String) peerProtocols.get(i);
                        this.selected = str;
                        return str;
                    }
                }
                String str2 = (String) this.protocols.get(0);
                this.selected = str2;
                return str2;
            } else if ((!methodName.equals("protocolSelected") && !methodName.equals("selected")) || args.length != 1) {
                return method.invoke(this, args);
            } else {
                this.selected = (String) args[0];
                return null;
            }
        }
    }
}
