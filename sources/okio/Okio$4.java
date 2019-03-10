package okio;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

class Okio$4 extends AsyncTimeout {
    final /* synthetic */ Socket val$socket;

    Okio$4(Socket socket) {
        this.val$socket = socket;
    }

    protected IOException newTimeoutException(@Nullable IOException cause) {
        InterruptedIOException ioe = new SocketTimeoutException("timeout");
        if (cause != null) {
            ioe.initCause(cause);
        }
        return ioe;
    }

    protected void timedOut() {
        Logger logger;
        Level level;
        StringBuilder stringBuilder;
        try {
            this.val$socket.close();
        } catch (Exception e) {
            logger = Okio.logger;
            level = Level.WARNING;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to close timed out socket ");
            stringBuilder.append(this.val$socket);
            logger.log(level, stringBuilder.toString(), e);
        } catch (AssertionError e2) {
            if (Okio.isAndroidGetsocknameError(e2)) {
                logger = Okio.logger;
                level = Level.WARNING;
                stringBuilder = new StringBuilder();
                stringBuilder.append("Failed to close timed out socket ");
                stringBuilder.append(this.val$socket);
                logger.log(level, stringBuilder.toString(), e2);
                return;
            }
            throw e2;
        }
    }
}
