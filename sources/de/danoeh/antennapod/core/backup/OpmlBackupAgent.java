package de.danoeh.antennapod.core.backup;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupHelper;
import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import de.danoeh.antennapod.core.BuildConfig;
import java.io.FileOutputStream;
import java.io.IOException;

public class OpmlBackupAgent extends BackupAgentHelper {
    private static final String OPML_BACKUP_KEY = "opml";

    private static class OpmlBackupHelper implements BackupHelper {
        private static final String OPML_ENTITY_KEY = "antennapod-feeds.opml";
        private static final String TAG = "OpmlBackupHelper";
        private byte[] mChecksum;
        private final Context mContext;

        public void performBackup(android.os.ParcelFileDescriptor r13, android.app.backup.BackupDataOutput r14, android.os.ParcelFileDescriptor r15) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:51:0x00e3 in {3, 5, 19, 20, 21, 22, 23, 24, 26, 27, 32, 40, 43, 44, 48, 49, 50} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r12 = this;
            r0 = "OpmlBackupHelper";
            r1 = "Performing backup";
            android.util.Log.d(r0, r1);
            r0 = new java.io.ByteArrayOutputStream;
            r0.<init>();
            r1 = 0;
            r2 = "MD5";	 Catch:{ NoSuchAlgorithmException -> 0x0021 }
            r2 = java.security.MessageDigest.getInstance(r2);	 Catch:{ NoSuchAlgorithmException -> 0x0021 }
            r1 = r2;	 Catch:{ NoSuchAlgorithmException -> 0x0021 }
            r2 = new java.io.OutputStreamWriter;	 Catch:{ NoSuchAlgorithmException -> 0x0021 }
            r3 = new java.security.DigestOutputStream;	 Catch:{ NoSuchAlgorithmException -> 0x0021 }
            r3.<init>(r0, r1);	 Catch:{ NoSuchAlgorithmException -> 0x0021 }
            r4 = de.danoeh.antennapod.core.util.LangUtils.UTF_8;	 Catch:{ NoSuchAlgorithmException -> 0x0021 }
            r2.<init>(r3, r4);	 Catch:{ NoSuchAlgorithmException -> 0x0021 }
            goto L_0x002a;
        L_0x0021:
            r2 = move-exception;
            r3 = new java.io.OutputStreamWriter;
            r4 = de.danoeh.antennapod.core.util.LangUtils.UTF_8;
            r3.<init>(r0, r4);
            r2 = r3;
        L_0x002a:
            r3 = new de.danoeh.antennapod.core.export.opml.OpmlWriter;	 Catch:{ IOException -> 0x00cc }
            r3.<init>();	 Catch:{ IOException -> 0x00cc }
            r4 = de.danoeh.antennapod.core.storage.DBReader.getFeedList();	 Catch:{ IOException -> 0x00cc }
            r3.writeDocument(r4, r2);	 Catch:{ IOException -> 0x00cc }
            if (r1 == 0) goto L_0x00af;	 Catch:{ IOException -> 0x00cc }
        L_0x0038:
            r3 = r1.digest();	 Catch:{ IOException -> 0x00cc }
            r4 = "OpmlBackupHelper";	 Catch:{ IOException -> 0x00cc }
            r5 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x00cc }
            r5.<init>();	 Catch:{ IOException -> 0x00cc }
            r6 = "New checksum: ";	 Catch:{ IOException -> 0x00cc }
            r5.append(r6);	 Catch:{ IOException -> 0x00cc }
            r6 = new java.math.BigInteger;	 Catch:{ IOException -> 0x00cc }
            r7 = 1;	 Catch:{ IOException -> 0x00cc }
            r6.<init>(r7, r3);	 Catch:{ IOException -> 0x00cc }
            r8 = 16;	 Catch:{ IOException -> 0x00cc }
            r6 = r6.toString(r8);	 Catch:{ IOException -> 0x00cc }
            r5.append(r6);	 Catch:{ IOException -> 0x00cc }
            r5 = r5.toString();	 Catch:{ IOException -> 0x00cc }
            de.danoeh.antennapod.core.backup.OpmlBackupAgent.LOGD(r4, r5);	 Catch:{ IOException -> 0x00cc }
            if (r13 == 0) goto L_0x00aa;	 Catch:{ IOException -> 0x00cc }
        L_0x0060:
            r4 = new java.io.FileInputStream;	 Catch:{ IOException -> 0x00cc }
            r5 = r13.getFileDescriptor();	 Catch:{ IOException -> 0x00cc }
            r4.<init>(r5);	 Catch:{ IOException -> 0x00cc }
            r5 = r4.read();	 Catch:{ IOException -> 0x00cc }
            r6 = -1;	 Catch:{ IOException -> 0x00cc }
            if (r5 == r6) goto L_0x00a9;	 Catch:{ IOException -> 0x00cc }
        L_0x0070:
            r6 = new byte[r5];	 Catch:{ IOException -> 0x00cc }
            r4.read(r6);	 Catch:{ IOException -> 0x00cc }
            r9 = "OpmlBackupHelper";	 Catch:{ IOException -> 0x00cc }
            r10 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x00cc }
            r10.<init>();	 Catch:{ IOException -> 0x00cc }
            r11 = "Old checksum: ";	 Catch:{ IOException -> 0x00cc }
            r10.append(r11);	 Catch:{ IOException -> 0x00cc }
            r11 = new java.math.BigInteger;	 Catch:{ IOException -> 0x00cc }
            r11.<init>(r7, r6);	 Catch:{ IOException -> 0x00cc }
            r7 = r11.toString(r8);	 Catch:{ IOException -> 0x00cc }
            r10.append(r7);	 Catch:{ IOException -> 0x00cc }
            r7 = r10.toString();	 Catch:{ IOException -> 0x00cc }
            de.danoeh.antennapod.core.backup.OpmlBackupAgent.LOGD(r9, r7);	 Catch:{ IOException -> 0x00cc }
            r7 = java.util.Arrays.equals(r6, r3);	 Catch:{ IOException -> 0x00cc }
            if (r7 == 0) goto L_0x00a8;	 Catch:{ IOException -> 0x00cc }
        L_0x009a:
            r7 = "OpmlBackupHelper";	 Catch:{ IOException -> 0x00cc }
            r8 = "Checksums are the same; won't backup";	 Catch:{ IOException -> 0x00cc }
            de.danoeh.antennapod.core.backup.OpmlBackupAgent.LOGD(r7, r8);	 Catch:{ IOException -> 0x00cc }
            r2.close();	 Catch:{ IOException -> 0x00a6 }
            goto L_0x00a7;
        L_0x00a6:
            r7 = move-exception;
        L_0x00a7:
            return;
        L_0x00a8:
            goto L_0x00ab;
        L_0x00a9:
            goto L_0x00ab;
        L_0x00ab:
            r12.writeNewStateDescription(r15, r3);	 Catch:{ IOException -> 0x00cc }
            goto L_0x00b0;	 Catch:{ IOException -> 0x00cc }
        L_0x00b0:
            r3 = "OpmlBackupHelper";	 Catch:{ IOException -> 0x00cc }
            r4 = "Backing up OPML";	 Catch:{ IOException -> 0x00cc }
            de.danoeh.antennapod.core.backup.OpmlBackupAgent.LOGD(r3, r4);	 Catch:{ IOException -> 0x00cc }
            r3 = r0.toByteArray();	 Catch:{ IOException -> 0x00cc }
            r4 = "antennapod-feeds.opml";	 Catch:{ IOException -> 0x00cc }
            r5 = r3.length;	 Catch:{ IOException -> 0x00cc }
            r14.writeEntityHeader(r4, r5);	 Catch:{ IOException -> 0x00cc }
            r4 = r3.length;	 Catch:{ IOException -> 0x00cc }
            r14.writeEntityData(r3, r4);	 Catch:{ IOException -> 0x00cc }
            r2.close();	 Catch:{ IOException -> 0x00d9 }
            goto L_0x00d8;
        L_0x00ca:
            r3 = move-exception;
            goto L_0x00dc;
        L_0x00cc:
            r3 = move-exception;
            r4 = "OpmlBackupHelper";	 Catch:{ all -> 0x00ca }
            r5 = "Error during backup";	 Catch:{ all -> 0x00ca }
            android.util.Log.e(r4, r5, r3);	 Catch:{ all -> 0x00ca }
            r2.close();	 Catch:{ IOException -> 0x00d9 }
        L_0x00d8:
            goto L_0x00db;
        L_0x00d9:
            r3 = move-exception;
            goto L_0x00d8;
        L_0x00db:
            return;
            r2.close();	 Catch:{ IOException -> 0x00e1 }
            goto L_0x00e2;
        L_0x00e1:
            r4 = move-exception;
        L_0x00e2:
            throw r3;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.backup.OpmlBackupAgent.OpmlBackupHelper.performBackup(android.os.ParcelFileDescriptor, android.app.backup.BackupDataOutput, android.os.ParcelFileDescriptor):void");
        }

        public void restoreEntity(android.app.backup.BackupDataInputStream r12) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:39:0x00b0 in {3, 7, 9, 13, 14, 21, 24, 25, 26, 31, 34, 36, 38} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r11 = this;
            r0 = "OpmlBackupHelper";
            r1 = "Backup restore";
            de.danoeh.antennapod.core.backup.OpmlBackupAgent.LOGD(r0, r1);
            r0 = "antennapod-feeds.opml";
            r1 = r12.getKey();
            r0 = r0.equals(r1);
            if (r0 != 0) goto L_0x002e;
        L_0x0013:
            r0 = "OpmlBackupHelper";
            r1 = new java.lang.StringBuilder;
            r1.<init>();
            r2 = "Unknown entity key: ";
            r1.append(r2);
            r2 = r12.getKey();
            r1.append(r2);
            r1 = r1.toString();
            de.danoeh.antennapod.core.backup.OpmlBackupAgent.LOGD(r0, r1);
            return;
        L_0x002e:
            r0 = 0;
            r1 = "MD5";	 Catch:{ NoSuchAlgorithmException -> 0x0043 }
            r1 = java.security.MessageDigest.getInstance(r1);	 Catch:{ NoSuchAlgorithmException -> 0x0043 }
            r0 = r1;	 Catch:{ NoSuchAlgorithmException -> 0x0043 }
            r1 = new java.io.InputStreamReader;	 Catch:{ NoSuchAlgorithmException -> 0x0043 }
            r2 = new java.security.DigestInputStream;	 Catch:{ NoSuchAlgorithmException -> 0x0043 }
            r2.<init>(r12, r0);	 Catch:{ NoSuchAlgorithmException -> 0x0043 }
            r3 = de.danoeh.antennapod.core.util.LangUtils.UTF_8;	 Catch:{ NoSuchAlgorithmException -> 0x0043 }
            r1.<init>(r2, r3);	 Catch:{ NoSuchAlgorithmException -> 0x0043 }
            goto L_0x004c;
        L_0x0043:
            r1 = move-exception;
            r2 = new java.io.InputStreamReader;
            r3 = de.danoeh.antennapod.core.util.LangUtils.UTF_8;
            r2.<init>(r12, r3);
            r1 = r2;
        L_0x004c:
            r2 = new de.danoeh.antennapod.core.export.opml.OpmlReader;	 Catch:{ XmlPullParserException -> 0x009e, IOException -> 0x0094 }
            r2.<init>();	 Catch:{ XmlPullParserException -> 0x009e, IOException -> 0x0094 }
            r2 = r2.readDocument(r1);	 Catch:{ XmlPullParserException -> 0x009e, IOException -> 0x0094 }
            r3 = 0;	 Catch:{ XmlPullParserException -> 0x009e, IOException -> 0x0094 }
            if (r0 != 0) goto L_0x005a;	 Catch:{ XmlPullParserException -> 0x009e, IOException -> 0x0094 }
        L_0x0058:
            r4 = r3;	 Catch:{ XmlPullParserException -> 0x009e, IOException -> 0x0094 }
            goto L_0x005e;	 Catch:{ XmlPullParserException -> 0x009e, IOException -> 0x0094 }
        L_0x005a:
            r4 = r0.digest();	 Catch:{ XmlPullParserException -> 0x009e, IOException -> 0x0094 }
        L_0x005e:
            r11.mChecksum = r4;	 Catch:{ XmlPullParserException -> 0x009e, IOException -> 0x0094 }
            r4 = de.danoeh.antennapod.core.storage.DownloadRequester.getInstance();	 Catch:{ XmlPullParserException -> 0x009e, IOException -> 0x0094 }
            r5 = r2.iterator();	 Catch:{ XmlPullParserException -> 0x009e, IOException -> 0x0094 }
        L_0x0068:
            r6 = r5.hasNext();	 Catch:{ XmlPullParserException -> 0x009e, IOException -> 0x0094 }
            if (r6 == 0) goto L_0x0090;	 Catch:{ XmlPullParserException -> 0x009e, IOException -> 0x0094 }
        L_0x006e:
            r6 = r5.next();	 Catch:{ XmlPullParserException -> 0x009e, IOException -> 0x0094 }
            r6 = (de.danoeh.antennapod.core.export.opml.OpmlElement) r6;	 Catch:{ XmlPullParserException -> 0x009e, IOException -> 0x0094 }
            r7 = new de.danoeh.antennapod.core.feed.Feed;	 Catch:{ XmlPullParserException -> 0x009e, IOException -> 0x0094 }
            r8 = r6.getXmlUrl();	 Catch:{ XmlPullParserException -> 0x009e, IOException -> 0x0094 }
            r9 = r6.getText();	 Catch:{ XmlPullParserException -> 0x009e, IOException -> 0x0094 }
            r7.<init>(r8, r3, r9);	 Catch:{ XmlPullParserException -> 0x009e, IOException -> 0x0094 }
            r8 = r11.mContext;	 Catch:{ DownloadRequestException -> 0x0087 }
            r4.downloadFeed(r8, r7);	 Catch:{ DownloadRequestException -> 0x0087 }
            goto L_0x008f;
        L_0x0087:
            r8 = move-exception;
            r9 = "OpmlBackupHelper";	 Catch:{ XmlPullParserException -> 0x009e, IOException -> 0x0094 }
            r10 = "Error while restoring/downloading feed";	 Catch:{ XmlPullParserException -> 0x009e, IOException -> 0x0094 }
            de.danoeh.antennapod.core.backup.OpmlBackupAgent.LOGD(r9, r10, r8);	 Catch:{ XmlPullParserException -> 0x009e, IOException -> 0x0094 }
        L_0x008f:
            goto L_0x0068;
            goto L_0x00a7;
        L_0x0092:
            r2 = move-exception;
            goto L_0x00ac;
        L_0x0094:
            r2 = move-exception;
            r3 = "OpmlBackupHelper";	 Catch:{ all -> 0x0092 }
            r4 = "Failed to restore OPML backup";	 Catch:{ all -> 0x0092 }
            android.util.Log.e(r3, r4, r2);	 Catch:{ all -> 0x0092 }
            goto L_0x00a7;	 Catch:{ all -> 0x0092 }
        L_0x009e:
            r2 = move-exception;	 Catch:{ all -> 0x0092 }
            r3 = "OpmlBackupHelper";	 Catch:{ all -> 0x0092 }
            r4 = "Error while parsing the OPML file";	 Catch:{ all -> 0x0092 }
            android.util.Log.e(r3, r4, r2);	 Catch:{ all -> 0x0092 }
        L_0x00a7:
            org.apache.commons.io.IOUtils.closeQuietly(r1);
            return;
        L_0x00ac:
            org.apache.commons.io.IOUtils.closeQuietly(r1);
            throw r2;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.core.backup.OpmlBackupAgent.OpmlBackupHelper.restoreEntity(android.app.backup.BackupDataInputStream):void");
        }

        public OpmlBackupHelper(Context context) {
            this.mContext = context;
        }

        public void writeNewStateDescription(ParcelFileDescriptor newState) {
            writeNewStateDescription(newState, this.mChecksum);
        }

        private void writeNewStateDescription(ParcelFileDescriptor newState, byte[] checksum) {
            if (checksum != null) {
                try {
                    FileOutputStream outState = new FileOutputStream(newState.getFileDescriptor());
                    outState.write(checksum.length);
                    outState.write(checksum);
                    outState.flush();
                    outState.close();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to write new state description", e);
                }
            }
        }
    }

    public void onCreate() {
        addHelper("opml", new OpmlBackupHelper(this));
    }

    private static void LOGD(String tag, String msg) {
        if (BuildConfig.DEBUG && Log.isLoggable(tag, 3)) {
            Log.d(tag, msg);
        }
    }

    private static void LOGD(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG && Log.isLoggable(tag, 3)) {
            Log.d(tag, msg, tr);
        }
    }
}
