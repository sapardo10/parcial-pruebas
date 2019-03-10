package okhttp3.internal.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import okio.Okio;
import okio.Sink;
import okio.Source;

public interface FileSystem {
    public static final FileSystem SYSTEM = new C12041();

    /* renamed from: okhttp3.internal.io.FileSystem$1 */
    class C12041 implements FileSystem {
        public void deleteContents(java.io.File r6) throws java.io.IOException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:0x004f in {6, 7, 10, 12, 13, 15} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r5 = this;
            r0 = r6.listFiles();
            if (r0 == 0) goto L_0x0038;
        L_0x0006:
            r1 = r0.length;
            r2 = 0;
        L_0x0008:
            if (r2 >= r1) goto L_0x0037;
        L_0x000a:
            r3 = r0[r2];
            r4 = r3.isDirectory();
            if (r4 == 0) goto L_0x0016;
        L_0x0012:
            r5.deleteContents(r3);
            goto L_0x0017;
        L_0x0017:
            r4 = r3.delete();
            if (r4 == 0) goto L_0x0020;
        L_0x001d:
            r2 = r2 + 1;
            goto L_0x0008;
        L_0x0020:
            r1 = new java.io.IOException;
            r2 = new java.lang.StringBuilder;
            r2.<init>();
            r4 = "failed to delete ";
            r2.append(r4);
            r2.append(r3);
            r2 = r2.toString();
            r1.<init>(r2);
            throw r1;
        L_0x0037:
            return;
        L_0x0038:
            r1 = new java.io.IOException;
            r2 = new java.lang.StringBuilder;
            r2.<init>();
            r3 = "not a readable directory: ";
            r2.append(r3);
            r2.append(r6);
            r2 = r2.toString();
            r1.<init>(r2);
            throw r1;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.io.FileSystem.1.deleteContents(java.io.File):void");
        }

        C12041() {
        }

        public Source source(File file) throws FileNotFoundException {
            return Okio.source(file);
        }

        public Sink sink(File file) throws FileNotFoundException {
            try {
                return Okio.sink(file);
            } catch (FileNotFoundException e) {
                file.getParentFile().mkdirs();
                return Okio.sink(file);
            }
        }

        public Sink appendingSink(File file) throws FileNotFoundException {
            try {
                return Okio.appendingSink(file);
            } catch (FileNotFoundException e) {
                file.getParentFile().mkdirs();
                return Okio.appendingSink(file);
            }
        }

        public void delete(File file) throws IOException {
            if (!file.delete()) {
                if (file.exists()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("failed to delete ");
                    stringBuilder.append(file);
                    throw new IOException(stringBuilder.toString());
                }
            }
        }

        public boolean exists(File file) {
            return file.exists();
        }

        public long size(File file) {
            return file.length();
        }

        public void rename(File from, File to) throws IOException {
            delete(to);
            if (!from.renameTo(to)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("failed to rename ");
                stringBuilder.append(from);
                stringBuilder.append(" to ");
                stringBuilder.append(to);
                throw new IOException(stringBuilder.toString());
            }
        }
    }

    Sink appendingSink(File file) throws FileNotFoundException;

    void delete(File file) throws IOException;

    void deleteContents(File file) throws IOException;

    boolean exists(File file);

    void rename(File file, File file2) throws IOException;

    Sink sink(File file) throws FileNotFoundException;

    long size(File file);

    Source source(File file) throws FileNotFoundException;
}
