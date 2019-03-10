package de.danoeh.antennapod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.service.download.DownloadRequest;
import de.danoeh.antennapod.core.storage.DownloadRequester;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import de.danoeh.antennapod.debug.R;
import org.apache.commons.lang3.Validate;

public class DownloadAuthenticationActivity extends AppCompatActivity {
    public static final String ARG_DOWNLOAD_REQUEST = "request";
    public static final String ARG_SEND_TO_DOWNLOAD_REQUESTER_BOOL = "send_to_downloadrequester";
    private static final String RESULT_REQUEST = "request";
    private EditText etxtPassword;
    private EditText etxtUsername;

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(UserPreferences.getTheme());
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView((int) R.layout.download_authentication_activity);
        TextView txtvDescription = (TextView) findViewById(R.id.txtvDescription);
        this.etxtUsername = (EditText) findViewById(R.id.etxtUsername);
        this.etxtPassword = (EditText) findViewById(R.id.etxtPassword);
        Button butConfirm = (Button) findViewById(R.id.butConfirm);
        Button butCancel = (Button) findViewById(R.id.butCancel);
        Validate.isTrue(getIntent().hasExtra("request"), "Download request missing", new Object[0]);
        DownloadRequest request = (DownloadRequest) getIntent().getParcelableExtra("request");
        boolean sendToDownloadRequester = getIntent().getBooleanExtra(ARG_SEND_TO_DOWNLOAD_REQUESTER_BOOL, false);
        String newDescription = new StringBuilder();
        newDescription.append(txtvDescription.getText());
        newDescription.append(":\n\n");
        newDescription.append(request.getTitle());
        txtvDescription.setText(newDescription.toString());
        if (savedInstanceState != null) {
            this.etxtUsername.setText(savedInstanceState.getString(PodDBAdapter.KEY_USERNAME));
            this.etxtPassword.setText(savedInstanceState.getString(PodDBAdapter.KEY_PASSWORD));
        }
        butConfirm.setOnClickListener(new C0001x9c180720(this, request, sendToDownloadRequester));
        butCancel.setOnClickListener(new C0002x918519a5());
    }

    public static /* synthetic */ void lambda$onCreate$0(DownloadAuthenticationActivity downloadAuthenticationActivity, DownloadRequest request, boolean sendToDownloadRequester, View v) {
        String username = downloadAuthenticationActivity.etxtUsername.getText().toString();
        String password = downloadAuthenticationActivity.etxtPassword.getText().toString();
        request.setUsername(username);
        request.setPassword(password);
        Intent result = new Intent();
        result.putExtra("request", request);
        downloadAuthenticationActivity.setResult(-1, result);
        if (sendToDownloadRequester) {
            DownloadRequester.getInstance().download(downloadAuthenticationActivity, request);
        }
        downloadAuthenticationActivity.finish();
    }

    public static /* synthetic */ void lambda$onCreate$1(DownloadAuthenticationActivity downloadAuthenticationActivity, View v) {
        downloadAuthenticationActivity.setResult(0);
        downloadAuthenticationActivity.finish();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PodDBAdapter.KEY_USERNAME, this.etxtUsername.getText().toString());
        outState.putString(PodDBAdapter.KEY_PASSWORD, this.etxtPassword.getText().toString());
    }
}
