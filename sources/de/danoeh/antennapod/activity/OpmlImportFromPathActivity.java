package de.danoeh.antennapod.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.util.IntentUtils;
import de.danoeh.antennapod.core.util.StorageUtils;
import de.danoeh.antennapod.debug.R;

public class OpmlImportFromPathActivity extends OpmlImportBaseActivity {
    private static final int CHOOSE_OPML_FILE = 1;
    private static final String TAG = "OpmlImportFromPathAct";
    private Intent intentGetContentAction;
    private Intent intentPickAction;

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(UserPreferences.getTheme());
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.opml_import);
        TextView txtvHeaderExplanation1 = (TextView) findViewById(R.id.txtvHeadingExplanation1);
        TextView txtvExplanation1 = (TextView) findViewById(R.id.txtvExplanation1);
        TextView txtvHeaderExplanation2 = (TextView) findViewById(R.id.txtvHeadingExplanation2);
        TextView txtvExplanation2 = (TextView) findViewById(R.id.txtvExplanation2);
        TextView txtvHeaderExplanation3 = (TextView) findViewById(R.id.txtvHeadingExplanation3);
        Button butChooseFilesystem = (Button) findViewById(R.id.butChooseFileFromFilesystem);
        butChooseFilesystem.setOnClickListener(new -$$Lambda$OpmlImportFromPathActivity$k6YAEr6DjG4E4H3Qcyn_lMUf5Rw());
        Button butChooseExternal = (Button) findViewById(R.id.butChooseFileFromExternal);
        butChooseExternal.setOnClickListener(new -$$Lambda$OpmlImportFromPathActivity$eU2s7KM97cjbEgJgWCb83DALBlg());
        int nextOption = 1;
        String optionLabel = getString(R.string.opml_import_option);
        this.intentPickAction = new Intent("android.intent.action.PICK");
        if (!IntentUtils.isCallable(getApplicationContext(), this.intentPickAction)) {
            this.intentPickAction.setData(null);
            if (!IntentUtils.isCallable(getApplicationContext(), this.intentPickAction)) {
                txtvHeaderExplanation1.setVisibility(8);
                txtvExplanation1.setVisibility(8);
                findViewById(R.id.divider1).setVisibility(8);
                butChooseFilesystem.setVisibility(8);
            }
        }
        if (txtvExplanation1.getVisibility() == 0) {
            txtvHeaderExplanation1.setText(String.format(optionLabel, new Object[]{Integer.valueOf(1)}));
            nextOption = 1 + 1;
        }
        this.intentGetContentAction = new Intent("android.intent.action.GET_CONTENT");
        this.intentGetContentAction.addCategory("android.intent.category.OPENABLE");
        this.intentGetContentAction.setType("*/*");
        if (IntentUtils.isCallable(getApplicationContext(), this.intentGetContentAction)) {
            txtvHeaderExplanation2.setText(String.format(optionLabel, new Object[]{Integer.valueOf(nextOption)}));
            nextOption++;
        } else {
            txtvHeaderExplanation2.setVisibility(8);
            txtvExplanation2.setVisibility(8);
            findViewById(R.id.divider2).setVisibility(8);
            butChooseExternal.setVisibility(8);
        }
        txtvHeaderExplanation3.setText(String.format(optionLabel, new Object[]{Integer.valueOf(nextOption)}));
    }

    protected void onResume() {
        super.onResume();
        StorageUtils.checkStorageAvailability(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return false;
        }
        finish();
        return true;
    }

    private void chooseFileFromFilesystem() {
        try {
            startActivityForResult(this.intentPickAction, 1);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "No activity found. Should never happen...");
        }
    }

    private void chooseFileFromExternal() {
        try {
            startActivityForResult(this.intentGetContentAction, 1);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "No activity found. Should never happen...");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 1) {
            Uri uri = data.getData();
            if (uri != null && uri.toString().startsWith("/")) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("file://");
                stringBuilder.append(uri.toString());
                uri = Uri.parse(stringBuilder.toString());
            }
            importUri(uri);
        }
    }
}
