package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;

public class WindowsLineEndingInputStream extends InputStream {
    private final boolean ensureLineFeedAtEndOfFile;
    private boolean eofSeen = false;
    private boolean injectSlashN = false;
    private boolean slashNSeen = false;
    private boolean slashRSeen = false;
    private final InputStream target;

    public WindowsLineEndingInputStream(InputStream in, boolean ensureLineFeedAtEndOfFile) {
        this.target = in;
        this.ensureLineFeedAtEndOfFile = ensureLineFeedAtEndOfFile;
    }

    private int readWithUpdate() throws IOException {
        int target = this.target.read();
        boolean z = true;
        this.eofSeen = target == -1;
        if (this.eofSeen) {
            return target;
        }
        this.slashRSeen = target == 13;
        if (target != 10) {
            z = false;
        }
        this.slashNSeen = z;
        return target;
    }

    public int read() throws IOException {
        if (this.eofSeen) {
            return eofGame();
        }
        if (this.injectSlashN) {
            this.injectSlashN = false;
            return 10;
        }
        boolean prevWasSlashR = this.slashRSeen;
        int target = readWithUpdate();
        if (this.eofSeen) {
            return eofGame();
        }
        if (target == 10) {
            if (!prevWasSlashR) {
                this.injectSlashN = true;
                return 13;
            }
        }
        return target;
    }

    private int eofGame() {
        if (!this.ensureLineFeedAtEndOfFile) {
            return -1;
        }
        if (!this.slashNSeen && !this.slashRSeen) {
            this.slashRSeen = true;
            return 13;
        } else if (this.slashNSeen) {
            return -1;
        } else {
            this.slashRSeen = false;
            this.slashNSeen = true;
            return 10;
        }
    }

    public void close() throws IOException {
        super.close();
        this.target.close();
    }

    public synchronized void mark(int readlimit) {
        throw new UnsupportedOperationException("Mark not supported");
    }
}
