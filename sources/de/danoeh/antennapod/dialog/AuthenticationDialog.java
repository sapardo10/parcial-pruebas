package de.danoeh.antennapod.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import de.danoeh.antennapod.debug.R;

public abstract class AuthenticationDialog extends Dialog {
    private final boolean enableUsernameField;
    private final String passwordInitialValue;
    private final boolean showSaveCredentialsCheckbox;
    private final int titleRes;
    private final String usernameInitialValue;

    protected abstract void onConfirmed(String str, String str2, boolean z);

    public AuthenticationDialog(Context context, int titleRes, boolean enableUsernameField, boolean showSaveCredentialsCheckbox, String usernameInitialValue, String passwordInitialValue) {
        super(context);
        this.titleRes = titleRes;
        this.enableUsernameField = enableUsernameField;
        this.showSaveCredentialsCheckbox = showSaveCredentialsCheckbox;
        this.usernameInitialValue = usernameInitialValue;
        this.passwordInitialValue = passwordInitialValue;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_dialog);
        EditText etxtUsername = (EditText) findViewById(R.id.etxtUsername);
        EditText etxtPassword = (EditText) findViewById(R.id.etxtPassword);
        CheckBox saveUsernamePassword = (CheckBox) findViewById(R.id.chkSaveUsernamePassword);
        Button butConfirm = (Button) findViewById(R.id.butConfirm);
        Button butCancel = (Button) findViewById(R.id.butCancel);
        int i = this.titleRes;
        if (i != 0) {
            setTitle(i);
        } else {
            requestWindowFeature(1);
        }
        etxtUsername.setEnabled(this.enableUsernameField);
        if (this.showSaveCredentialsCheckbox) {
            saveUsernamePassword.setVisibility(0);
        } else {
            saveUsernamePassword.setVisibility(8);
        }
        CharSequence charSequence = this.usernameInitialValue;
        if (charSequence != null) {
            etxtUsername.setText(charSequence);
        }
        charSequence = this.passwordInitialValue;
        if (charSequence != null) {
            etxtPassword.setText(charSequence);
        }
        setOnCancelListener(new -$$Lambda$AuthenticationDialog$W7evMJbvxnht8ZYZa2lzV2nORhA());
        butCancel.setOnClickListener(new -$$Lambda$AuthenticationDialog$4nzVds_YlIvaR1iz7nrgssxAbjQ());
        butConfirm.setOnClickListener(new -$$Lambda$AuthenticationDialog$_XTKAFUktE03tjgyt4HuZ8f03aI(this, etxtUsername, etxtPassword, saveUsernamePassword));
    }

    public static /* synthetic */ void lambda$onCreate$2(AuthenticationDialog authenticationDialog, EditText etxtUsername, EditText etxtPassword, CheckBox saveUsernamePassword, View v) {
        boolean z;
        String obj = etxtUsername.getText().toString();
        String obj2 = etxtPassword.getText().toString();
        if (authenticationDialog.showSaveCredentialsCheckbox) {
            if (saveUsernamePassword.isChecked()) {
                z = true;
                authenticationDialog.onConfirmed(obj, obj2, z);
                authenticationDialog.dismiss();
            }
        }
        z = false;
        authenticationDialog.onConfirmed(obj, obj2, z);
        authenticationDialog.dismiss();
    }

    protected void onCancelled() {
    }
}
