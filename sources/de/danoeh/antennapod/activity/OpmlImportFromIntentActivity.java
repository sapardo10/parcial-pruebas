package de.danoeh.antennapod.activity;

import android.net.Uri;
import android.os.Bundle;
import de.danoeh.antennapod.core.preferences.UserPreferences;

public class OpmlImportFromIntentActivity extends OpmlImportBaseActivity {
    private static final String TAG = "OpmlImportFromIntentAct";

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(UserPreferences.getTheme());
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Uri uri = getIntent().getData();
        if (uri == null || !uri.toString().startsWith("/")) {
            String extraText = getIntent().getStringExtra("android.intent.extra.TEXT");
            if (extraText != null) {
                uri = Uri.parse(extraText);
            }
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("file://");
            stringBuilder.append(uri.toString());
            uri = Uri.parse(stringBuilder.toString());
        }
        importUri(uri);
    }

    protected boolean finishWhenCanceled() {
        return true;
    }
}
