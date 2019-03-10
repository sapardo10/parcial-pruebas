package org.apache.commons.io.output;

import java.io.IOException;
import java.io.OutputStream;

public class ClosedOutputStream extends OutputStream {
    public static final ClosedOutputStream CLOSED_OUTPUT_STREAM = new ClosedOutputStream();

    public void write(int b) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("write(");
        stringBuilder.append(b);
        stringBuilder.append(") failed: stream is closed");
        throw new IOException(stringBuilder.toString());
    }
}
