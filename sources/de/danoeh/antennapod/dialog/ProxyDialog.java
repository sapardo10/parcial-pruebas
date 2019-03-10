package de.danoeh.antennapod.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v4.internal.view.SupportMenu;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.afollestad.materialdialogs.internal.MDButton;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.service.download.AntennapodHttpClient;
import de.danoeh.antennapod.core.service.download.ProxyConfig;
import de.danoeh.antennapod.debug.R;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.concurrent.TimeUnit;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProxyDialog {
    private static final String TAG = "ProxyDialog";
    private final Context context;
    private MaterialDialog dialog;
    private Disposable disposable;
    private EditText etHost;
    private EditText etPassword;
    private EditText etPort;
    private EditText etUsername;
    private final TextWatcher requireTestOnChange = new C07822();
    private Spinner spType;
    private boolean testSuccessful = false;
    private TextView txtvMessage;

    /* renamed from: de.danoeh.antennapod.dialog.ProxyDialog$1 */
    class C07811 implements OnItemSelectedListener {
        C07811() {
        }

        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            boolean z = true;
            ProxyDialog.this.enableSettings(position > 0);
            ProxyDialog proxyDialog = ProxyDialog.this;
            if (position <= 0) {
                z = false;
            }
            proxyDialog.setTestRequired(z);
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
            ProxyDialog.this.enableSettings(false);
        }
    }

    /* renamed from: de.danoeh.antennapod.dialog.ProxyDialog$2 */
    class C07822 implements TextWatcher {
        C07822() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            ProxyDialog.this.setTestRequired(true);
        }
    }

    public ProxyDialog(Context context) {
        this.context = context;
    }

    public Dialog createDialog() {
        this.dialog = new Builder(this.context).title((int) R.string.pref_proxy_title).customView((int) R.layout.proxy_settings, true).positiveText((int) R.string.proxy_test_label).negativeText((int) R.string.cancel_label).onPositive(new -$$Lambda$ProxyDialog$4SQyzmZJi312IqKx9G14tOtMHcM()).onNegative(-$$Lambda$ProxyDialog$tiFutZ6ZUhbFhB14FkzFkKmhBd8.INSTANCE).autoDismiss(false).build();
        View view = this.dialog.getCustomView();
        this.spType = (Spinner) view.findViewById(R.id.spType);
        ArrayAdapter<String> adapter = new ArrayAdapter(this.context, 17367048, new String[]{Type.DIRECT.name(), Type.HTTP.name()});
        adapter.setDropDownViewResource(17367049);
        this.spType.setAdapter(adapter);
        ProxyConfig proxyConfig = UserPreferences.getProxyConfig();
        this.spType.setSelection(adapter.getPosition(proxyConfig.type.name()));
        this.etHost = (EditText) view.findViewById(R.id.etHost);
        if (!TextUtils.isEmpty(proxyConfig.host)) {
            this.etHost.setText(proxyConfig.host);
        }
        this.etHost.addTextChangedListener(this.requireTestOnChange);
        this.etPort = (EditText) view.findViewById(R.id.etPort);
        if (proxyConfig.port > 0) {
            this.etPort.setText(String.valueOf(proxyConfig.port));
        }
        this.etPort.addTextChangedListener(this.requireTestOnChange);
        this.etUsername = (EditText) view.findViewById(R.id.etUsername);
        if (!TextUtils.isEmpty(proxyConfig.username)) {
            this.etUsername.setText(proxyConfig.username);
        }
        this.etUsername.addTextChangedListener(this.requireTestOnChange);
        this.etPassword = (EditText) view.findViewById(R.id.etPassword);
        if (!TextUtils.isEmpty(proxyConfig.password)) {
            this.etPassword.setText(proxyConfig.username);
        }
        this.etPassword.addTextChangedListener(this.requireTestOnChange);
        if (proxyConfig.type == Type.DIRECT) {
            enableSettings(false);
            setTestRequired(false);
        }
        this.spType.setOnItemSelectedListener(new C07811());
        this.txtvMessage = (TextView) view.findViewById(R.id.txtvMessage);
        checkValidity();
        return this.dialog;
    }

    public static /* synthetic */ void lambda$createDialog$0(ProxyDialog proxyDialog, MaterialDialog dialog1, DialogAction which) {
        if (proxyDialog.testSuccessful) {
            ProxyConfig proxy;
            if (Type.valueOf((String) ((Spinner) dialog1.findViewById(R.id.spType)).getSelectedItem()) == Type.DIRECT) {
                proxy = ProxyConfig.direct();
            } else {
                String host = proxyDialog.etHost.getText().toString();
                String port = proxyDialog.etPort.getText().toString();
                String username = proxyDialog.etUsername.getText().toString();
                if (TextUtils.isEmpty(username)) {
                    username = null;
                }
                String password = proxyDialog.etPassword.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    password = null;
                }
                int portValue = 0;
                if (!TextUtils.isEmpty(port)) {
                    portValue = Integer.valueOf(port).intValue();
                }
                proxy = ProxyConfig.http(host, portValue, username, password);
            }
            UserPreferences.setProxyConfig(proxy);
            AntennapodHttpClient.reinit();
            proxyDialog.dialog.dismiss();
            return;
        }
        proxyDialog.dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
        proxyDialog.test();
    }

    private void enableSettings(boolean enable) {
        this.etHost.setEnabled(enable);
        this.etPort.setEnabled(enable);
        this.etUsername.setEnabled(enable);
        this.etPassword.setEnabled(enable);
    }

    private boolean checkValidity() {
        boolean valid = true;
        if (this.spType.getSelectedItemPosition() > 0) {
            valid = true & checkHost();
        }
        return valid & checkPort();
    }

    private boolean checkHost() {
        String host = this.etHost.getText().toString();
        if (host.length() == 0) {
            this.etHost.setError(this.context.getString(R.string.proxy_host_empty_error));
            return false;
        } else if ("localhost".equals(host) || Patterns.DOMAIN_NAME.matcher(host).matches()) {
            return true;
        } else {
            this.etHost.setError(this.context.getString(R.string.proxy_host_invalid_error));
            return false;
        }
    }

    private boolean checkPort() {
        int port = getPort();
        if (port >= 0 || port <= SupportMenu.USER_MASK) {
            return true;
        }
        this.etPort.setError(this.context.getString(R.string.proxy_port_invalid_error));
        return false;
    }

    private int getPort() {
        String port = this.etPort.getText().toString();
        if (port.length() <= 0) {
            return 0;
        }
        try {
            return Integer.parseInt(port);
        } catch (NumberFormatException e) {
        }
    }

    private void setTestRequired(boolean required) {
        if (required) {
            this.testSuccessful = false;
            MDButton button = this.dialog.getActionButton(DialogAction.POSITIVE);
            button.setText(this.context.getText(R.string.proxy_test_label));
            button.setEnabled(true);
            return;
        }
        this.testSuccessful = true;
        button = this.dialog.getActionButton(DialogAction.POSITIVE);
        button.setText(this.context.getText(17039370));
        button.setEnabled(true);
    }

    private void test() {
        Disposable disposable = this.disposable;
        if (disposable != null) {
            disposable.dispose();
        }
        if (checkValidity()) {
            TypedArray res = this.context.getTheme().obtainStyledAttributes(new int[]{16842806});
            int textColorPrimary = res.getColor(0, 0);
            res.recycle();
            String checking = this.context.getString(R.string.proxy_checking);
            this.txtvMessage.setTextColor(textColorPrimary);
            TextView textView = this.txtvMessage;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("{fa-circle-o-notch spin} ");
            stringBuilder.append(checking);
            textView.setText(stringBuilder.toString());
            this.txtvMessage.setVisibility(0);
            this.disposable = Single.create(new -$$Lambda$ProxyDialog$NCPcjTyzUrqDuhNnm8mblkpMpNg()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new -$$Lambda$ProxyDialog$bB74oIEadoytBfYgdEldiUWZnDs(), new -$$Lambda$ProxyDialog$it_QT-dYpiQfhkOY-GbTi-TgZMk());
            return;
        }
        setTestRequired(true);
    }

    public static /* synthetic */ void lambda$test$3(ProxyDialog proxyDialog, SingleEmitter emitter) throws Exception {
        String type = (String) proxyDialog.spType.getSelectedItem();
        String host = proxyDialog.etHost.getText().toString();
        String port = proxyDialog.etPort.getText().toString();
        String username = proxyDialog.etUsername.getText().toString();
        String password = proxyDialog.etPassword.getText().toString();
        int portValue = ProxyConfig.DEFAULT_PORT;
        if (!TextUtils.isEmpty(port)) {
            portValue = Integer.valueOf(port).intValue();
        }
        OkHttpClient.Builder builder = AntennapodHttpClient.newBuilder().connectTimeout(10, TimeUnit.SECONDS).proxy(new Proxy(Type.valueOf(type.toUpperCase()), InetSocketAddress.createUnresolved(host, portValue)));
        builder.interceptors().clear();
        OkHttpClient client = builder.build();
        if (!TextUtils.isEmpty(username)) {
            client.interceptors().add(new -$$Lambda$ProxyDialog$_yPJpxkRgUap-TNBLPde37qrwI8(Credentials.basic(username, password)));
        }
        try {
            emitter.onSuccess(client.newCall(new Request.Builder().url("http://www.google.com").head().build()).execute());
        } catch (IOException e) {
            emitter.onError(e);
        }
    }

    public static /* synthetic */ void lambda$test$4(ProxyDialog proxyDialog, Response response) throws Exception {
        int colorId;
        String icon;
        String result;
        if (response.isSuccessful()) {
            colorId = R.color.download_success_green;
            icon = "{fa-check}";
            result = proxyDialog.context.getString(R.string.proxy_test_successful);
        } else {
            colorId = R.color.download_failed_red;
            icon = "{fa-close}";
            result = proxyDialog.context.getString(R.string.proxy_test_failed);
        }
        proxyDialog.txtvMessage.setTextColor(ContextCompat.getColor(proxyDialog.context, colorId));
        proxyDialog.txtvMessage.setText(String.format("%s %s: %s", new Object[]{icon, result, response.message()}));
        proxyDialog.setTestRequired(response.isSuccessful() ^ true);
    }

    public static /* synthetic */ void lambda$test$5(ProxyDialog proxyDialog, Throwable error) throws Exception {
        String result = proxyDialog.context.getString(R.string.proxy_test_failed);
        proxyDialog.txtvMessage.setTextColor(ContextCompat.getColor(proxyDialog.context, R.color.download_failed_red));
        proxyDialog.txtvMessage.setText(String.format("%s %s: %s", new Object[]{"{fa-close}", result, error.getMessage()}));
        proxyDialog.setTestRequired(true);
    }
}
