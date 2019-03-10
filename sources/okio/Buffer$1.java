package okio;

import java.io.OutputStream;

class Buffer$1 extends OutputStream {
    final /* synthetic */ Buffer this$0;

    Buffer$1(Buffer this$0) {
        this.this$0 = this$0;
    }

    public void write(int b) {
        this.this$0.writeByte((byte) b);
    }

    public void write(byte[] data, int offset, int byteCount) {
        this.this$0.write(data, offset, byteCount);
    }

    public void flush() {
    }

    public void close() {
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.this$0);
        stringBuilder.append(".outputStream()");
        return stringBuilder.toString();
    }
}
