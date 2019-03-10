package okhttp3.internal.cache;

import java.io.FileNotFoundException;
import java.io.IOException;
import okio.Okio;
import okio.Sink;
import okio.Source;

public final class DiskLruCache$Editor {
    private boolean done;
    final DiskLruCache$Entry entry;
    final /* synthetic */ DiskLruCache this$0;
    final boolean[] written;

    DiskLruCache$Editor(DiskLruCache this$0, DiskLruCache$Entry entry) {
        this.this$0 = this$0;
        this.entry = entry;
        this.written = entry.readable ? null : new boolean[this$0.valueCount];
    }

    void detach() {
        if (this.entry.currentEditor == this) {
            for (int i = 0; i < this.this$0.valueCount; i++) {
                try {
                    this.this$0.fileSystem.delete(this.entry.dirtyFiles[i]);
                } catch (IOException e) {
                }
            }
            this.entry.currentEditor = null;
        }
    }

    public Source newSource(int index) {
        synchronized (this.this$0) {
            if (this.done) {
                throw new IllegalStateException();
            }
            if (this.entry.readable) {
                if (this.entry.currentEditor == this) {
                    try {
                        Source source = this.this$0.fileSystem.source(this.entry.cleanFiles[index]);
                        return source;
                    } catch (FileNotFoundException e) {
                        return null;
                    }
                }
            }
            return null;
        }
    }

    public Sink newSink(int index) {
        synchronized (this.this$0) {
            if (this.done) {
                throw new IllegalStateException();
            } else if (this.entry.currentEditor != this) {
                Sink blackhole = Okio.blackhole();
                return blackhole;
            } else {
                if (!this.entry.readable) {
                    this.written[index] = true;
                }
                try {
                    Sink c13051 = new FaultHidingSink(this.this$0.fileSystem.sink(this.entry.dirtyFiles[index])) {
                        protected void onException(IOException e) {
                            synchronized (DiskLruCache$Editor.this.this$0) {
                                DiskLruCache$Editor.this.detach();
                            }
                        }
                    };
                    return c13051;
                } catch (FileNotFoundException e) {
                    return Okio.blackhole();
                }
            }
        }
    }

    public void commit() throws IOException {
        synchronized (this.this$0) {
            if (this.done) {
                throw new IllegalStateException();
            }
            if (this.entry.currentEditor == this) {
                this.this$0.completeEdit(this, true);
            }
            this.done = true;
        }
    }

    public void abort() throws IOException {
        synchronized (this.this$0) {
            if (this.done) {
                throw new IllegalStateException();
            }
            if (this.entry.currentEditor == this) {
                this.this$0.completeEdit(this, false);
            }
            this.done = true;
        }
    }

    public void abortUnlessCommitted() {
        synchronized (this.this$0) {
            if (!this.done && this.entry.currentEditor == this) {
                try {
                    this.this$0.completeEdit(this, false);
                } catch (IOException e) {
                }
            }
        }
    }
}
