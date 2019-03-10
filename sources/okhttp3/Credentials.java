package okhttp3;

import java.nio.charset.Charset;
import okhttp3.internal.Util;
import okio.ByteString;

public final class Credentials {
    private Credentials() {
    }

    public static String basic(String username, String password) {
        return basic(username, password, Util.ISO_8859_1);
    }

    public static String basic(String username, String password, Charset charset) {
        String usernameAndPassword = new StringBuilder();
        usernameAndPassword.append(username);
        usernameAndPassword.append(":");
        usernameAndPassword.append(password);
        String encoded = ByteString.encodeString(usernameAndPassword.toString(), charset).base64();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Basic ");
        stringBuilder.append(encoded);
        return stringBuilder.toString();
    }
}
