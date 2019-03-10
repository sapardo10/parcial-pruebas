package okhttp3.internal.http;

public final class HttpMethod {
    public static boolean invalidatesCache(String method) {
        if (!method.equals("POST")) {
            if (!method.equals("PATCH")) {
                if (!method.equals("PUT")) {
                    if (!method.equals("DELETE")) {
                        if (!method.equals("MOVE")) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public static boolean requiresRequestBody(String method) {
        if (!method.equals("POST")) {
            if (!method.equals("PUT")) {
                if (!method.equals("PATCH")) {
                    if (!method.equals("PROPPATCH")) {
                        if (!method.equals("REPORT")) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public static boolean permitsRequestBody(String method) {
        return (method.equals("GET") || method.equals("HEAD")) ? false : true;
    }

    public static boolean redirectsWithBody(String method) {
        return method.equals("PROPFIND");
    }

    public static boolean redirectsToGet(String method) {
        return method.equals("PROPFIND") ^ 1;
    }

    private HttpMethod() {
    }
}
