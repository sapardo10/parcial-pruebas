package de.danoeh.antennapod.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.debug.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImportExportActivity extends AppCompatActivity {
    private static final String EXPORT_FILENAME = "AntennaPodBackup.db";
    private static final int REQUEST_CODE_BACKUP_DOCUMENT = 44;
    private static final int REQUEST_CODE_RESTORE = 43;
    private static final String TAG = ImportExportActivity.class.getSimpleName();

    private void backupToDocument(android.net.Uri r8) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:31:0x0079 in {6, 7, 9, 10, 20, 21, 26, 28, 29, 30} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r7 = this;
        r0 = 0;
        r1 = 0;
        r2 = -1;
        r3 = 2131362032; // 0x7f0a00f0 float:1.8343833E38 double:1.053032759E-314;
        r4 = r7.getContentResolver();	 Catch:{ IOException -> 0x0042 }
        r5 = "w";	 Catch:{ IOException -> 0x0042 }
        r4 = r4.openFileDescriptor(r8, r5);	 Catch:{ IOException -> 0x0042 }
        r0 = r4;	 Catch:{ IOException -> 0x0042 }
        r4 = new java.io.FileOutputStream;	 Catch:{ IOException -> 0x0042 }
        r5 = r0.getFileDescriptor();	 Catch:{ IOException -> 0x0042 }
        r4.<init>(r5);	 Catch:{ IOException -> 0x0042 }
        r1 = r4;	 Catch:{ IOException -> 0x0042 }
        r7.writeBackupTo(r1);	 Catch:{ IOException -> 0x0042 }
        r4 = r7.findViewById(r3);	 Catch:{ IOException -> 0x0042 }
        r5 = 2131820827; // 0x7f11011b float:1.927438E38 double:1.0532594337E-314;	 Catch:{ IOException -> 0x0042 }
        r4 = android.support.design.widget.Snackbar.make(r4, r5, r2);	 Catch:{ IOException -> 0x0042 }
        r4.show();	 Catch:{ IOException -> 0x0042 }
        org.apache.commons.io.IOUtils.closeQuietly(r1);
        if (r0 == 0) goto L_0x003f;
    L_0x0032:
        r0.close();	 Catch:{ IOException -> 0x0036 }
    L_0x0035:
        goto L_0x0064;
    L_0x0036:
        r2 = move-exception;
        r3 = TAG;
        r4 = "Unable to close ParcelFileDescriptor";
        android.util.Log.d(r3, r4);
        goto L_0x0035;
    L_0x003f:
        goto L_0x0064;
    L_0x0040:
        r2 = move-exception;
        goto L_0x0065;
    L_0x0042:
        r4 = move-exception;
        r5 = TAG;	 Catch:{ all -> 0x0040 }
        r6 = android.util.Log.getStackTraceString(r4);	 Catch:{ all -> 0x0040 }
        android.util.Log.e(r5, r6);	 Catch:{ all -> 0x0040 }
        r3 = r7.findViewById(r3);	 Catch:{ all -> 0x0040 }
        r5 = r4.getLocalizedMessage();	 Catch:{ all -> 0x0040 }
        r2 = android.support.design.widget.Snackbar.make(r3, r5, r2);	 Catch:{ all -> 0x0040 }
        r2.show();	 Catch:{ all -> 0x0040 }
        org.apache.commons.io.IOUtils.closeQuietly(r1);
        if (r0 == 0) goto L_0x003f;
    L_0x0060:
        r0.close();	 Catch:{ IOException -> 0x0036 }
        goto L_0x0035;
    L_0x0064:
        return;
    L_0x0065:
        org.apache.commons.io.IOUtils.closeQuietly(r1);
        if (r0 == 0) goto L_0x0077;
    L_0x006a:
        r0.close();	 Catch:{ IOException -> 0x006e }
        goto L_0x0078;
    L_0x006e:
        r3 = move-exception;
        r4 = TAG;
        r5 = "Unable to close ParcelFileDescriptor";
        android.util.Log.d(r4, r5);
        goto L_0x0078;
    L_0x0078:
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.activity.ImportExportActivity.backupToDocument(android.net.Uri):void");
    }

    private void restoreFrom(android.net.Uri r7) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:13:0x003f in {2, 9, 10, 12} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r6 = this;
        r0 = "Antennapod.db";
        r0 = r6.getDatabasePath(r0);
        r1 = 0;
        r2 = r6.getContentResolver();	 Catch:{ IOException -> 0x001c }
        r2 = r2.openInputStream(r7);	 Catch:{ IOException -> 0x001c }
        r1 = r2;	 Catch:{ IOException -> 0x001c }
        org.apache.commons.io.FileUtils.copyInputStreamToFile(r1, r0);	 Catch:{ IOException -> 0x001c }
        r6.displayImportSuccessDialog();	 Catch:{ IOException -> 0x001c }
    L_0x0016:
        org.apache.commons.io.IOUtils.closeQuietly(r1);
        goto L_0x003a;
    L_0x001a:
        r2 = move-exception;
        goto L_0x003b;
    L_0x001c:
        r2 = move-exception;
        r3 = TAG;	 Catch:{ all -> 0x001a }
        r4 = android.util.Log.getStackTraceString(r2);	 Catch:{ all -> 0x001a }
        android.util.Log.e(r3, r4);	 Catch:{ all -> 0x001a }
        r3 = 2131362032; // 0x7f0a00f0 float:1.8343833E38 double:1.053032759E-314;	 Catch:{ all -> 0x001a }
        r3 = r6.findViewById(r3);	 Catch:{ all -> 0x001a }
        r4 = r2.getLocalizedMessage();	 Catch:{ all -> 0x001a }
        r5 = -1;	 Catch:{ all -> 0x001a }
        r3 = android.support.design.widget.Snackbar.make(r3, r4, r5);	 Catch:{ all -> 0x001a }
        r3.show();	 Catch:{ all -> 0x001a }
        goto L_0x0016;
    L_0x003a:
        return;
    L_0x003b:
        org.apache.commons.io.IOUtils.closeQuietly(r1);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.activity.ImportExportActivity.restoreFrom(android.net.Uri):void");
    }

    private void writeBackupTo(java.io.FileOutputStream r13) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:31:0x0083 in {10, 12, 14, 16, 18, 20, 23, 24, 27, 28, 30} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r12 = this;
        r0 = 0;
        r1 = 0;
        r2 = -1;
        r3 = 2131362032; // 0x7f0a00f0 float:1.8343833E38 double:1.053032759E-314;
        r4 = "Antennapod.db";	 Catch:{ IOException -> 0x0061 }
        r4 = r12.getDatabasePath(r4);	 Catch:{ IOException -> 0x0061 }
        r5 = r4.exists();	 Catch:{ IOException -> 0x0061 }
        if (r5 == 0) goto L_0x004b;	 Catch:{ IOException -> 0x0061 }
    L_0x0012:
        r5 = new java.io.FileInputStream;	 Catch:{ IOException -> 0x0061 }
        r5.<init>(r4);	 Catch:{ IOException -> 0x0061 }
        r7 = r5.getChannel();	 Catch:{ IOException -> 0x0061 }
        r6 = r13.getChannel();	 Catch:{ IOException -> 0x0047, all -> 0x0043 }
        r8 = 0;
        r10 = r7.size();	 Catch:{ IOException -> 0x003e, all -> 0x0039 }
        r6.transferFrom(r7, r8, r10);	 Catch:{ IOException -> 0x003e, all -> 0x0039 }
        r0 = r12.findViewById(r3);	 Catch:{ IOException -> 0x003e, all -> 0x0039 }
        r1 = 2131820827; // 0x7f11011b float:1.927438E38 double:1.0532594337E-314;	 Catch:{ IOException -> 0x003e, all -> 0x0039 }
        r0 = android.support.design.widget.Snackbar.make(r0, r1, r2);	 Catch:{ IOException -> 0x003e, all -> 0x0039 }
        r0.show();	 Catch:{ IOException -> 0x003e, all -> 0x0039 }
        r1 = r6;
        r0 = r7;
        goto L_0x0058;
    L_0x0039:
        r0 = move-exception;
        r2 = r0;
        r1 = r6;
        r0 = r7;
        goto L_0x007c;
    L_0x003e:
        r0 = move-exception;
        r4 = r0;
        r1 = r6;
        r0 = r7;
        goto L_0x0062;
    L_0x0043:
        r0 = move-exception;
        r2 = r0;
        r0 = r7;
        goto L_0x007c;
    L_0x0047:
        r0 = move-exception;
        r4 = r0;
        r0 = r7;
        goto L_0x0062;
    L_0x004b:
        r5 = r12.findViewById(r3);	 Catch:{ IOException -> 0x0061 }
        r6 = "Can not access current database";	 Catch:{ IOException -> 0x0061 }
        r5 = android.support.design.widget.Snackbar.make(r5, r6, r2);	 Catch:{ IOException -> 0x0061 }
        r5.show();	 Catch:{ IOException -> 0x0061 }
    L_0x0058:
        org.apache.commons.io.IOUtils.closeQuietly(r0);
        org.apache.commons.io.IOUtils.closeQuietly(r1);
        goto L_0x007b;
    L_0x005f:
        r2 = move-exception;
        goto L_0x007c;
    L_0x0061:
        r4 = move-exception;
    L_0x0062:
        r5 = TAG;	 Catch:{ all -> 0x005f }
        r6 = android.util.Log.getStackTraceString(r4);	 Catch:{ all -> 0x005f }
        android.util.Log.e(r5, r6);	 Catch:{ all -> 0x005f }
        r3 = r12.findViewById(r3);	 Catch:{ all -> 0x005f }
        r5 = r4.getLocalizedMessage();	 Catch:{ all -> 0x005f }
        r2 = android.support.design.widget.Snackbar.make(r3, r5, r2);	 Catch:{ all -> 0x005f }
        r2.show();	 Catch:{ all -> 0x005f }
        goto L_0x0058;
    L_0x007b:
        return;
    L_0x007c:
        org.apache.commons.io.IOUtils.closeQuietly(r0);
        org.apache.commons.io.IOUtils.closeQuietly(r1);
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.danoeh.antennapod.activity.ImportExportActivity.writeBackupTo(java.io.FileOutputStream):void");
    }

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(UserPreferences.getTheme());
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
        }
        setContentView(R.layout.import_export_activity);
        findViewById(R.id.button_export).setOnClickListener(new -$$Lambda$ImportExportActivity$qX5B6529Rzl6sSnNshw3yRiagaQ());
        findViewById(R.id.button_import).setOnClickListener(new -$$Lambda$ImportExportActivity$SsIgvy-rxhdvJNCdeV85gmSBIMg());
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return super.onOptionsItemSelected(item);
        }
        finish();
        return true;
    }

    private void backup() {
        if (VERSION.SDK_INT >= 19) {
            startActivityForResult(new Intent("android.intent.action.CREATE_DOCUMENT").addCategory("android.intent.category.OPENABLE").setType("application/x-sqlite3").putExtra("android.intent.extra.TITLE", EXPORT_FILENAME), 44);
            return;
        }
        try {
            writeBackupTo(new FileOutputStream(new File(Environment.getExternalStorageDirectory(), EXPORT_FILENAME)));
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            Snackbar.make(findViewById(R.id.import_export_layout), e.getLocalizedMessage(), -1).show();
        }
    }

    private void restore() {
        if (VERSION.SDK_INT >= 19) {
            Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT");
            intent.setType("*/*");
            startActivityForResult(intent, 43);
            return;
        }
        intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.import_select_file)), 43);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == -1) {
            if (resultData != null) {
                Uri uri = resultData.getData();
                if (requestCode == 43) {
                    restoreFrom(uri);
                } else if (requestCode == 44) {
                    backupToDocument(uri);
                }
            }
        }
    }

    private void displayImportSuccessDialog() {
        Builder d = new Builder(this);
        d.setMessage((int) R.string.import_ok);
        d.setCancelable(false);
        d.setPositiveButton(17039370, new -$$Lambda$ImportExportActivity$OkqirT9YSAh1Eki1EkvDaO4JfOc());
        d.show();
    }
}
