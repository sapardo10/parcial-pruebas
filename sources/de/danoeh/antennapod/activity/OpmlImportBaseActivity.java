package de.danoeh.antennapod.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import de.danoeh.antennapod.asynctask.OpmlFeedQueuer;
import de.danoeh.antennapod.asynctask.OpmlImportWorker;
import de.danoeh.antennapod.core.export.opml.OpmlElement;
import de.danoeh.antennapod.core.util.LangUtils;
import de.danoeh.antennapod.debug.R;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class OpmlImportBaseActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 5;
    private static final String TAG = "OpmlImportBaseActivity";
    private OpmlImportWorker importWorker;
    @Nullable
    private Uri uri;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Received result");
        if (resultCode == 0) {
            Log.d(TAG, "Activity was cancelled");
            if (finishWhenCanceled()) {
                finish();
                return;
            }
            return;
        }
        int[] selected = data.getIntArrayExtra(OpmlFeedChooserActivity.EXTRA_SELECTED_ITEMS);
        if (selected == null || selected.length <= 0) {
            Log.d(TAG, "No items were selected");
        } else {
            new OpmlFeedQueuer(this, selected) {
                protected void onPostExecute(Void result) {
                    super.onPostExecute(result);
                    Intent intent = new Intent(OpmlImportBaseActivity.this, MainActivity.class);
                    intent.addFlags(335544320);
                    OpmlImportBaseActivity.this.startActivity(intent);
                }
            }.executeAsync();
        }
    }

    void importUri(@Nullable Uri uri) {
        if (uri == null) {
            new Builder(this).content((int) R.string.opml_import_error_no_file).positiveText(17039370).show();
            return;
        }
        this.uri = uri;
        if (VERSION.SDK_INT >= 23) {
            if (uri.toString().contains(Environment.getExternalStorageDirectory().toString())) {
                if (ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") != 0) {
                    requestPermission();
                    return;
                }
            }
        }
        startImport();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 5);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 5) {
            if (grantResults.length <= 0 || !ArrayUtils.contains(grantResults, 0)) {
                new Builder(this).content((int) R.string.opml_import_ask_read_permission).positiveText(17039370).negativeText((int) R.string.cancel_label).onPositive(new -$$Lambda$OpmlImportBaseActivity$xR1-Oy9k22CPx73hEflPhnObSig()).onNegative(new -$$Lambda$OpmlImportBaseActivity$qEPA6EBHc1RJBA2cNvOM6bB6caM()).show();
            } else {
                startImport();
            }
        }
    }

    private void startImport() {
        try {
            this.importWorker = new OpmlImportWorker(this, new InputStreamReader(getContentResolver().openInputStream(this.uri), LangUtils.UTF_8)) {
                protected void onPostExecute(ArrayList<OpmlElement> result) {
                    super.onPostExecute((ArrayList) result);
                    if (result != null) {
                        Log.d(OpmlImportBaseActivity.TAG, "Parsing was successful");
                        OpmlImportHolder.setReadElements(result);
                        Context context = OpmlImportBaseActivity.this;
                        context.startActivityForResult(new Intent(context, OpmlFeedChooserActivity.class), 0);
                        return;
                    }
                    Log.d(OpmlImportBaseActivity.TAG, "Parser error occurred");
                }
            };
            this.importWorker.executeAsync();
        } catch (Exception e) {
            Log.d(TAG, Log.getStackTraceString(e));
            String message = getString(R.string.opml_reader_error);
            Builder builder = new Builder(this);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(message);
            stringBuilder.append(StringUtils.SPACE);
            stringBuilder.append(e.getMessage());
            builder.content(stringBuilder.toString()).positiveText(17039370).show();
        }
    }

    boolean finishWhenCanceled() {
        return false;
    }
}
