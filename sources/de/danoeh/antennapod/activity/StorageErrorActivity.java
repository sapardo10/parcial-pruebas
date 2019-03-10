package de.danoeh.antennapod.activity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.util.StorageUtils;
import de.danoeh.antennapod.debug.R;
import de.danoeh.antennapod.dialog.ChooseDataFolderDialog;
import java.io.File;

public class StorageErrorActivity extends AppCompatActivity {
    private static final String[] EXTERNAL_STORAGE_PERMISSIONS = new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 42;
    private static final String TAG = "StorageErrorActivity";
    private final BroadcastReceiver mediaUpdate = new StorageErrorActivity$2(this);

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(UserPreferences.getTheme());
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.storage_error);
        ((Button) findViewById(R.id.btnChooseDataFolder)).setOnClickListener(new -$$Lambda$StorageErrorActivity$XPknmAuqeqG9D73c41BJh3MKo10());
        if (VERSION.SDK_INT >= 23) {
            int readPermission = ActivityCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE");
            int writePermission = ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
            if (readPermission == 0) {
                if (writePermission == 0) {
                    return;
                }
            }
            requestPermission();
        }
    }

    public static /* synthetic */ void lambda$onCreate$0(StorageErrorActivity storageErrorActivity, View v) {
        if (19 > VERSION.SDK_INT || VERSION.SDK_INT > 22) {
            storageErrorActivity.openDirectoryChooser();
        } else {
            storageErrorActivity.showChooseDataFolderDialog();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, EXTERNAL_STORAGE_PERMISSIONS, 42);
    }

    private void openDirectoryChooser() {
        startActivityForResult(new Intent(this, DirectoryChooserActivity.class), 1);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 42) {
            if (grantResults.length == 2) {
                if (grantResults[0] == 0) {
                    if (grantResults[1] == 0) {
                    }
                }
                new Builder(this).content(R.string.choose_data_directory_permission_rationale).positiveText(17039370).onPositive(new -$$Lambda$StorageErrorActivity$J3T72D4w9TR8XeBCKkvMCx5xA4I()).onNegative(new -$$Lambda$StorageErrorActivity$6gsxFiY-7c2oG7l2FUlza8-yH1w()).show();
            }
        }
    }

    protected void onResume() {
        super.onResume();
        if (StorageUtils.storageAvailable()) {
            leaveErrorState();
        } else {
            registerReceiver(this.mediaUpdate, new IntentFilter("android.intent.action.MEDIA_MOUNTED"));
        }
    }

    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(this.mediaUpdate);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private void showChooseDataFolderDialog() {
        ChooseDataFolderDialog.showDialog(this, new StorageErrorActivity$1(this));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1 && requestCode == 1) {
            File path;
            String dir = data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR);
            if (dir != null) {
                path = new File(dir);
            } else {
                path = getExternalFilesDir(null);
            }
            if (path != null) {
                String message = null;
                if (!path.exists()) {
                    message = String.format(getString(R.string.folder_does_not_exist_error), new Object[]{dir});
                } else if (!path.canRead()) {
                    message = String.format(getString(R.string.folder_not_readable_error), new Object[]{dir});
                } else if (!path.canWrite()) {
                    message = String.format(getString(R.string.folder_not_writable_error), new Object[]{dir});
                }
                if (message == null) {
                    String str = TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Setting data folder: ");
                    stringBuilder.append(dir);
                    Log.d(str, stringBuilder.toString());
                    UserPreferences.setDataFolder(dir);
                    leaveErrorState();
                } else {
                    AlertDialog.Builder ab = new AlertDialog.Builder(this);
                    ab.setMessage(message);
                    ab.setPositiveButton(17039370, null);
                    ab.show();
                }
            }
        }
    }

    private void leaveErrorState() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}
