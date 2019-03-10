package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;

public class UnixLineEndingInputStream extends InputStream {
    private final boolean ensureLineFeedAtEndOfFile;
    private boolean eofSeen = false;
    private boolean slashNSeen = false;
    private boolean slashRSeen = false;
    private final InputStream target;

    public UnixLineEndingInputStream(InputStream in, boolean ensureLineFeedAtEndOfFile) {
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
        this.slashNSeen = target == 10;
        if (target != 13) {
            z = false;
        }
        this.slashRSeen = z;
        return target;
    }

    public int read() throws IOException {
        boolean previousWasSlashR = this.slashRSeen;
        if (this.eofSeen) {
            return eofGame(previousWasSlashR);
        }
        int target = readWithUpdate();
        if (this.eofSeen) {
            return eofGame(previousWasSlashR);
        }
        if (this.slashRSeen) {
            return 10;
        }
        if (previousWasSlashR && this.slashNSeen) {
            return read();
        }
        return target;
    }

    private int eofGame(boolean previousWasSlashR) {
        if (!previousWasSlashR) {
            if (this.ensureLineFeedAtEndOfFile) {
                if (this.slashNSeen) {
                    return -1;
                }
                this.slashNSeen = true;
                return 10;
            }
        }
        return -1;
    }

    public void close() throws IOException {
        super.close();
        this.target.close();
    }

    public synchronized void mark(int readlimit) {
        throw new UnsupportedOperationException("Mark notsupported");
    }
}
