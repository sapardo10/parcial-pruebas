package de.danoeh.antennapod.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.danoeh.antennapod.BuildConfig;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.debug.R;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class DirectoryChooserActivity extends AppCompatActivity {
    private static final String CREATE_DIRECTORY_NAME = "AntennaPod";
    public static final int RESULT_CODE_DIR_SELECTED = 1;
    public static final String RESULT_SELECTED_DIR = "selected_dir";
    private static final String TAG = "DirectoryChooserActivit";
    private Button butCancel;
    private Button butConfirm;
    private ImageButton butNavUp;
    private FileObserver fileObserver;
    private ArrayList<String> filenames;
    private File[] filesInDir;
    private ListView listDirectories;
    private ArrayAdapter<String> listDirectoriesAdapter;
    private File selectedDir;
    private TextView txtvSelectedFolder;

    /* renamed from: de.danoeh.antennapod.activity.DirectoryChooserActivity$1 */
    class C07111 implements OnClickListener {
        C07111() {
        }

        public void onClick(View v) {
            DirectoryChooserActivity directoryChooserActivity = DirectoryChooserActivity.this;
            if (!directoryChooserActivity.isValidFile(directoryChooserActivity.selectedDir)) {
                return;
            }
            if (DirectoryChooserActivity.this.selectedDir.list().length == 0) {
                DirectoryChooserActivity.this.returnSelectedFolder();
            } else {
                showNonEmptyDirectoryWarning();
            }
        }

        private void showNonEmptyDirectoryWarning() {
            Builder adb = new Builder(DirectoryChooserActivity.this);
            adb.setTitle((int) R.string.folder_not_empty_dialog_title);
            adb.setMessage((int) R.string.folder_not_empty_dialog_msg);
            adb.setNegativeButton((int) R.string.cancel_label, -$$Lambda$DirectoryChooserActivity$1$eNmC8tSe_z9PXMs5DnL2XoocUGU.INSTANCE);
            adb.setPositiveButton((int) R.string.confirm_label, new -$$Lambda$DirectoryChooserActivity$1$F_bEBQNwY6JSD_Fe89AruJWPfdE());
            adb.create().show();
        }

        public static /* synthetic */ void lambda$showNonEmptyDirectoryWarning$1(C07111 c07111, DialogInterface dialog, int which) {
            dialog.dismiss();
            DirectoryChooserActivity.this.returnSelectedFolder();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(UserPreferences.getTheme());
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.directory_chooser);
        this.butConfirm = (Button) findViewById(R.id.butConfirm);
        this.butCancel = (Button) findViewById(R.id.butCancel);
        this.butNavUp = (ImageButton) findViewById(R.id.butNavUp);
        this.txtvSelectedFolder = (TextView) findViewById(R.id.txtvSelectedFolder);
        this.listDirectories = (ListView) findViewById(R.id.directory_list);
        this.butConfirm.setOnClickListener(new C07111());
        this.butCancel.setOnClickListener(new -$$Lambda$DirectoryChooserActivity$l8cN56OGzIihSY0qtXfBRFP0kSw());
        this.listDirectories.setOnItemClickListener(new -$$Lambda$DirectoryChooserActivity$7sUS5j7DTcwW4PbwZBsCR1fTMH8());
        this.butNavUp.setOnClickListener(new -$$Lambda$DirectoryChooserActivity$apvMVeGG-nRqUzYMlXH5ji7nZOM());
        this.filenames = new ArrayList();
        this.listDirectoriesAdapter = new ArrayAdapter(this, 17367043, this.filenames);
        this.listDirectories.setAdapter(this.listDirectoriesAdapter);
        changeDirectory(Environment.getExternalStorageDirectory());
    }

    public static /* synthetic */ void lambda$onCreate$0(DirectoryChooserActivity directoryChooserActivity, View v) {
        directoryChooserActivity.setResult(0);
        directoryChooserActivity.finish();
    }

    public static /* synthetic */ void lambda$onCreate$1(DirectoryChooserActivity directoryChooserActivity, AdapterView adapter, View view, int position, long id) {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Selected index: ");
        stringBuilder.append(position);
        Log.d(str, stringBuilder.toString());
        File[] fileArr = directoryChooserActivity.filesInDir;
        if (fileArr != null && position >= 0 && position < fileArr.length) {
            directoryChooserActivity.changeDirectory(fileArr[position]);
        }
    }

    public static /* synthetic */ void lambda$onCreate$2(DirectoryChooserActivity directoryChooserActivity, View v) {
        File file = directoryChooserActivity.selectedDir;
        if (file != null) {
            file = file.getParentFile();
            File parent = file;
            if (file != null) {
                directoryChooserActivity.changeDirectory(parent);
            }
        }
    }

    private void returnSelectedFolder() {
        if (this.selectedDir != null && BuildConfig.DEBUG) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Returning ");
            stringBuilder.append(this.selectedDir.getAbsolutePath());
            stringBuilder.append(" as result");
            Log.d(str, stringBuilder.toString());
        }
        Intent resultData = new Intent();
        File file = this.selectedDir;
        if (file != null) {
            resultData.putExtra(RESULT_SELECTED_DIR, file.getAbsolutePath());
        }
        setResult(-1, resultData);
        finish();
    }

    protected void onPause() {
        super.onPause();
        FileObserver fileObserver = this.fileObserver;
        if (fileObserver != null) {
            fileObserver.stopWatching();
        }
    }

    protected void onResume() {
        super.onResume();
        FileObserver fileObserver = this.fileObserver;
        if (fileObserver != null) {
            fileObserver.startWatching();
        }
    }

    public void onStop() {
        super.onStop();
        this.listDirectoriesAdapter = null;
        this.fileObserver = null;
    }

    private void changeDirectory(File dir) {
        if (dir != null && dir.isDirectory()) {
            File[] contents = dir.listFiles();
            if (contents != null) {
                int numDirectories = 0;
                for (File f : contents) {
                    if (f.isDirectory()) {
                        numDirectories++;
                    }
                }
                this.filesInDir = new File[numDirectories];
                this.filenames.clear();
                int i = 0;
                int i2 = 0;
                while (i < numDirectories) {
                    if (contents[i2].isDirectory()) {
                        this.filesInDir[i] = contents[i2];
                        this.filenames.add(contents[i2].getName());
                        i++;
                    }
                    i2++;
                }
                Arrays.sort(this.filesInDir);
                Collections.sort(this.filenames);
                this.selectedDir = dir;
                this.txtvSelectedFolder.setText(dir.getAbsolutePath());
                this.listDirectoriesAdapter.notifyDataSetChanged();
                this.fileObserver = createFileObserver(dir.getAbsolutePath());
                this.fileObserver.startWatching();
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Changed directory to ");
                stringBuilder.append(dir.getAbsolutePath());
                Log.d(str, stringBuilder.toString());
            } else {
                Log.d(TAG, "Could not change folder: contents of dir were null");
            }
        } else if (dir == null) {
            Log.d(TAG, "Could not change folder: dir was null");
        } else {
            Log.d(TAG, "Could not change folder: dir is no directory");
        }
        refreshButtonState();
    }

    private void refreshButtonState() {
        File file = this.selectedDir;
        if (file != null) {
            this.butConfirm.setEnabled(isValidFile(file));
            supportInvalidateOptionsMenu();
        }
    }

    private void refreshDirectory() {
        File file = this.selectedDir;
        if (file != null) {
            changeDirectory(file);
        }
    }

    private FileObserver createFileObserver(String path) {
        return new FileObserver(path, 960) {
            public void onEvent(int event, String path) {
                String str = DirectoryChooserActivity.TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("FileObserver received event ");
                stringBuilder.append(event);
                Log.d(str, stringBuilder.toString());
                DirectoryChooserActivity directoryChooserActivity = DirectoryChooserActivity.this;
                directoryChooserActivity.runOnUiThread(new -$$Lambda$DirectoryChooserActivity$2$FEk3IEtvWf-bxupmVnG-oy-qNUc(directoryChooserActivity));
            }
        };
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.new_folder_item).setVisible(isValidFile(this.selectedDir));
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.directory_chooser, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == 16908332) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else if (itemId == R.id.new_folder_item) {
            openNewFolderDialog();
            return true;
        } else if (itemId != R.id.set_to_default_folder_item) {
            return false;
        } else {
            this.selectedDir = null;
            returnSelectedFolder();
            return true;
        }
    }

    private void openNewFolderDialog() {
        Builder builder = new Builder(this);
        builder.setTitle((int) R.string.create_folder_label);
        builder.setMessage(String.format(getString(R.string.create_folder_msg), new Object[]{CREATE_DIRECTORY_NAME}));
        builder.setNegativeButton((int) R.string.cancel_label, -$$Lambda$DirectoryChooserActivity$adIqsZYGjfRSnLaLQXm_LPVdRg8.INSTANCE);
        builder.setPositiveButton((int) R.string.confirm_label, new -$$Lambda$DirectoryChooserActivity$U0cF3tZz_WuqBQmjQyUxcEA_N8I());
        builder.create().show();
    }

    public static /* synthetic */ void lambda$openNewFolderDialog$4(DirectoryChooserActivity directoryChooserActivity, DialogInterface dialog, int which) {
        dialog.dismiss();
        Toast.makeText(directoryChooserActivity, directoryChooserActivity.createFolder(), null).show();
    }

    private int createFolder() {
        File file = this.selectedDir;
        if (file == null) {
            return R.string.create_folder_error;
        }
        if (!file.canWrite()) {
            return R.string.create_folder_error_no_write_access;
        }
        file = new File(this.selectedDir, CREATE_DIRECTORY_NAME);
        if (file.exists()) {
            return R.string.create_folder_error_already_exists;
        }
        if (file.mkdir()) {
            return R.string.create_folder_success;
        }
        return R.string.create_folder_error;
    }

    private boolean isValidFile(File file) {
        return file != null && file.isDirectory() && file.canRead() && file.canWrite();
    }
}
