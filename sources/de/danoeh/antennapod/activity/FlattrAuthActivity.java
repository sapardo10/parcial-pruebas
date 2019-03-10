package de.danoeh.antennapod.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import de.danoeh.antennapod.BuildConfig;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.util.flattr.FlattrUtils;
import de.danoeh.antennapod.debug.R;
import org.shredzone.flattr4j.exception.FlattrException;

public class FlattrAuthActivity extends AppCompatActivity {
    private static final String TAG = "FlattrAuthActivity";
    private static FlattrAuthActivity singleton;
    private boolean authSuccessful;
    private Button butAuthenticate;
    private Button butReturn;
    private TextView txtvExplanation;

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(UserPreferences.getTheme());
        super.onCreate(savedInstanceState);
        singleton = this;
        this.authSuccessful = false;
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Activity created");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView((int) R.layout.flattr_auth);
        this.txtvExplanation = (TextView) findViewById(R.id.txtvExplanation);
        this.butAuthenticate = (Button) findViewById(R.id.but_authenticate);
        this.butReturn = (Button) findViewById(R.id.but_return_home);
        this.butReturn.setOnClickListener(new -$$Lambda$FlattrAuthActivity$L20_Dk5JtoEIcuh3_PSCAmS43Vg());
        this.butAuthenticate.setOnClickListener(new -$$Lambda$FlattrAuthActivity$dEeKWfuKpdbh9DY4bc3h3H0qAi0());
    }

    public static /* synthetic */ void lambda$onCreate$0(FlattrAuthActivity flattrAuthActivity, View v) {
        Intent intent = new Intent(flattrAuthActivity, MainActivity.class);
        intent.addFlags(67108864);
        flattrAuthActivity.startActivity(intent);
    }

    public static /* synthetic */ void lambda$onCreate$1(FlattrAuthActivity flattrAuthActivity, View v) {
        try {
            FlattrUtils.startAuthProcess(flattrAuthActivity);
        } catch (FlattrException e) {
            e.printStackTrace();
        }
    }

    public static FlattrAuthActivity getInstance() {
        return singleton;
    }

    protected void onResume() {
        super.onResume();
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Activity resumed");
        }
        Uri uri = getIntent().getData();
        if (uri != null) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Received uri");
            }
            FlattrUtils.handleCallback(this, uri);
        }
    }

    public void handleAuthenticationSuccess() {
        this.authSuccessful = true;
        this.txtvExplanation.setText(R.string.flattr_auth_success);
        this.butAuthenticate.setEnabled(false);
        this.butReturn.setVisibility(0);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    protected void onPause() {
        super.onPause();
        if (this.authSuccessful) {
            finish();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return false;
        }
        if (this.authSuccessful) {
            Intent intent = new Intent(this, PreferenceActivity.class);
            intent.addFlags(67108864);
            startActivity(intent);
        } else {
            finish();
        }
        return true;
    }
}
