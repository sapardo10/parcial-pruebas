package okio;

import java.io.InputStream;

class Buffer$2 extends InputStream {
    final /* synthetic */ Buffer this$0;

    Buffer$2(Buffer this$0) {
        this.this$0 = this$0;
    }

    public int read() {
        if (this.this$0.size > 0) {
            return this.this$0.readByte() & 255;
        }
        return -1;
    }

    public int read(byte[] sink, int offset, int byteCount) {
        return this.this$0.read(sink, offset, byteCount);
    }

    public int available() {
        return (int) Math.min(this.this$0.size, 2147483647L);
    }

    public void close() {
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.this$0);
        stringBuilder.append(".inputStream()");
        return stringBuilder.toString();
    }
}
