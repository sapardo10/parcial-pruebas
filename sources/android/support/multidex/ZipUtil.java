package android.support.multidex;

import android.support.v4.media.session.PlaybackStateCompat;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.CRC32;

final class ZipUtil {
    private static final int BUFFER_SIZE = 16384;
    private static final int ENDHDR = 22;
    private static final int ENDSIG = 101010256;

    static class CentralDirectory {
        long offset;
        long size;

        CentralDirectory() {
        }
    }

    static android.support.multidex.ZipUtil.CentralDirectory findCentralDirectory(java.io.RandomAccessFile r10) throws java.io.IOException, java.util.zip.ZipException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:18:0x0086 in {4, 5, 10, 13, 15, 17} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r0 = r10.length();
        r2 = 22;
        r0 = r0 - r2;
        r2 = 0;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 < 0) goto L_0x006b;
    L_0x000d:
        r4 = 65536; // 0x10000 float:9.18355E-41 double:3.2379E-319;
        r4 = r0 - r4;
        r6 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));
        if (r6 >= 0) goto L_0x0019;
    L_0x0016:
        r4 = 0;
        goto L_0x001a;
    L_0x001a:
        r2 = 101010256; // 0x6054b50 float:2.506985E-35 double:4.99056974E-316;
        r2 = java.lang.Integer.reverseBytes(r2);
    L_0x0021:
        r10.seek(r0);
        r3 = r10.readInt();
        if (r3 != r2) goto L_0x005b;
        r3 = 2;
        r10.skipBytes(r3);
        r10.skipBytes(r3);
        r10.skipBytes(r3);
        r10.skipBytes(r3);
        r3 = new android.support.multidex.ZipUtil$CentralDirectory;
        r3.<init>();
        r6 = r10.readInt();
        r6 = java.lang.Integer.reverseBytes(r6);
        r6 = (long) r6;
        r8 = 4294967295; // 0xffffffff float:NaN double:2.1219957905E-314;
        r6 = r6 & r8;
        r3.size = r6;
        r6 = r10.readInt();
        r6 = java.lang.Integer.reverseBytes(r6);
        r6 = (long) r6;
        r6 = r6 & r8;
        r3.offset = r6;
        return r3;
    L_0x005b:
        r6 = 1;
        r0 = r0 - r6;
        r3 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r3 < 0) goto L_0x0063;
    L_0x0062:
        goto L_0x0021;
    L_0x0063:
        r3 = new java.util.zip.ZipException;
        r6 = "End Of Central Directory signature not found";
        r3.<init>(r6);
        throw r3;
    L_0x006b:
        r2 = new java.util.zip.ZipException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "File too short to be a zip file: ";
        r3.append(r4);
        r4 = r10.length();
        r3.append(r4);
        r3 = r3.toString();
        r2.<init>(r3);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.multidex.ZipUtil.findCentralDirectory(java.io.RandomAccessFile):android.support.multidex.ZipUtil$CentralDirectory");
    }

    ZipUtil() {
    }

    static long getZipCrc(File apk) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(apk, "r");
        try {
            long computeCrcOfCentralDir = computeCrcOfCentralDir(raf, findCentralDirectory(raf));
            return computeCrcOfCentralDir;
        } finally {
            raf.close();
        }
    }

    static long computeCrcOfCentralDir(RandomAccessFile raf, CentralDirectory dir) throws IOException {
        CRC32 crc = new CRC32();
        long stillToRead = dir.size;
        raf.seek(dir.offset);
        byte[] buffer = new byte[16384];
        int length = raf.read(buffer, 0, (int) Math.min(PlaybackStateCompat.ACTION_PREPARE, stillToRead));
        while (length != -1) {
            crc.update(buffer, 0, length);
            stillToRead -= (long) length;
            if (stillToRead == 0) {
                break;
            }
            length = raf.read(buffer, 0, (int) Math.min(PlaybackStateCompat.ACTION_PREPARE, stillToRead));
        }
        return crc.getValue();
    }
}
