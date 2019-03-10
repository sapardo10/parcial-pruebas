package de.danoeh.antennapod.core.util.vorbiscommentreader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.commons.io.IOUtils;

class OggInputStream extends InputStream {
    private long bytesLeft;
    private final InputStream input;
    private boolean isInPage = false;

    public OggInputStream(InputStream input) {
        this.input = input;
    }

    public int read() throws IOException {
        if (!this.isInPage) {
            readOggPage();
        }
        if (!this.isInPage || this.bytesLeft <= 0) {
            return -1;
        }
        int result = this.input.read();
        this.bytesLeft--;
        if (this.bytesLeft == 0) {
            this.isInPage = false;
        }
        return result;
    }

    private void readOggPage() throws IOException {
        int read;
        int[] buffer = new int[4];
        boolean isInOggS = false;
        while (true) {
            int i;
            read = this.input.read();
            int c = read;
            if (read == -1) {
                break;
            }
            if (c == 79) {
                isInOggS = true;
                buffer[0] = c;
            } else if (c == 83) {
                buffer[3] = c;
            } else if (c != 103) {
                if (isInOggS) {
                    Arrays.fill(buffer, 0);
                    isInOggS = false;
                }
            } else if (buffer[1] != c) {
                buffer[1] = c;
            } else {
                buffer[2] = c;
            }
            if (buffer[0] == 79 && buffer[1] == 103 && buffer[2] == 103 && buffer[3] == 83) {
                break;
            }
            IOUtils.skipFully(this.input, 22);
            this.bytesLeft = 0;
            read = this.input.read();
            for (i = 0; i < read; i++) {
                this.bytesLeft += (long) this.input.read();
            }
            this.isInPage = true;
        }
        IOUtils.skipFully(this.input, 22);
        this.bytesLeft = 0;
        read = this.input.read();
        for (i = 0; i < read; i++) {
            this.bytesLeft += (long) this.input.read();
        }
        this.isInPage = true;
    }
}
