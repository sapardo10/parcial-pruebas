package de.danoeh.antennapod.activity.gpoddernet;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;
import de.danoeh.antennapod.BuildConfig;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.core.gpoddernet.GpodnetService;
import de.danoeh.antennapod.core.gpoddernet.GpodnetServiceException;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetDevice;
import de.danoeh.antennapod.core.gpoddernet.model.GpodnetDevice.DeviceType;
import de.danoeh.antennapod.core.preferences.GpodnetPreferences;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.service.GpodnetSyncService;
import de.danoeh.antennapod.debug.R;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class GpodnetAuthenticationActivity extends AppCompatActivity {
    private static final int STEP_DEFAULT = -1;
    private static final int STEP_DEVICE = 1;
    private static final int STEP_FINISH = 2;
    private static final int STEP_LOGIN = 0;
    private static final String TAG = "GpodnetAuthActivity";
    private int currentStep = -1;
    private volatile String password;
    private volatile GpodnetDevice selectedDevice;
    private GpodnetService service;
    private volatile String username;
    private ViewFlipper viewFlipper;
    private View[] views;

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(UserPreferences.getTheme());
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.gpodnetauth_activity);
        this.service = new GpodnetService();
        this.viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);
        LayoutInflater inflater = (LayoutInflater) getSystemService("layout_inflater");
        r2 = new View[3];
        int i = 0;
        r2[0] = inflater.inflate(R.layout.gpodnetauth_credentials, this.viewFlipper, false);
        r2[1] = inflater.inflate(R.layout.gpodnetauth_device, this.viewFlipper, false);
        r2[2] = inflater.inflate(R.layout.gpodnetauth_finish, this.viewFlipper, false);
        this.views = r2;
        View[] viewArr = this.views;
        int length = viewArr.length;
        while (i < length) {
            this.viewFlipper.addView(viewArr[i]);
            i++;
        }
        advance();
    }

    protected void onDestroy() {
        super.onDestroy();
        GpodnetService gpodnetService = this.service;
        if (gpodnetService != null) {
            gpodnetService.shutdown();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return super.onOptionsItemSelected(item);
        }
        finish();
        return true;
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    private void setupLoginView(View view) {
        EditText username = (EditText) view.findViewById(R.id.etxtUsername);
        EditText password = (EditText) view.findViewById(R.id.etxtPassword);
        Button login = (Button) view.findViewById(R.id.butLogin);
        TextView txtvError = (TextView) view.findViewById(R.id.txtvError);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progBarLogin);
        password.setOnEditorActionListener(new C0721x81accfb3(login));
        final EditText editText = username;
        final EditText editText2 = password;
        final Button button = login;
        final ProgressBar progressBar2 = progressBar;
        final TextView textView = txtvError;
        login.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                final String usernameStr = editText.getText().toString();
                final String passwordStr = editText2.getText().toString();
                if (BuildConfig.DEBUG) {
                    Log.d(GpodnetAuthenticationActivity.TAG, "Checking login credentials");
                }
                new AsyncTask<GpodnetService, Void, Void>() {
                    volatile Exception exception;

                    protected void onPreExecute() {
                        super.onPreExecute();
                        button.setEnabled(false);
                        progressBar2.setVisibility(0);
                        textView.setVisibility(8);
                        ((InputMethodManager) GpodnetAuthenticationActivity.this.getSystemService("input_method")).hideSoftInputFromWindow(button.getWindowToken(), 2);
                    }

                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        button.setEnabled(true);
                        progressBar2.setVisibility(8);
                        if (this.exception == null) {
                            GpodnetAuthenticationActivity.this.advance();
                            return;
                        }
                        textView.setText(this.exception.getCause().getMessage());
                        textView.setVisibility(0);
                    }

                    protected Void doInBackground(GpodnetService... params) {
                        try {
                            params[0].authenticate(usernameStr, passwordStr);
                            GpodnetAuthenticationActivity.this.username = usernameStr;
                            GpodnetAuthenticationActivity.this.password = passwordStr;
                        } catch (GpodnetServiceException e) {
                            e.printStackTrace();
                            this.exception = e;
                        }
                        return null;
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new GpodnetService[]{GpodnetAuthenticationActivity.this.service});
            }
        });
    }

    static /* synthetic */ boolean lambda$setupLoginView$0(Button login, TextView v, int actionID, KeyEvent event) {
        return actionID == 2 && login.performClick();
    }

    private void setupDeviceView(View view) {
        View view2 = view;
        EditText deviceID = (EditText) view2.findViewById(R.id.etxtDeviceID);
        EditText caption = (EditText) view2.findViewById(R.id.etxtCaption);
        Button createNewDevice = (Button) view2.findViewById(R.id.butCreateNewDevice);
        Button chooseDevice = (Button) view2.findViewById(R.id.butChooseExistingDevice);
        TextView txtvError = (TextView) view2.findViewById(R.id.txtvError);
        ProgressBar progBarCreateDevice = (ProgressBar) view2.findViewById(R.id.progbarCreateDevice);
        Spinner spinnerDevices = (Spinner) view2.findViewById(R.id.spinnerChooseDevice);
        AtomicReference<List<GpodnetDevice>> devices = new AtomicReference();
        final Button button = chooseDevice;
        final Spinner spinner = spinnerDevices;
        final Button button2 = createNewDevice;
        final AtomicReference<List<GpodnetDevice>> atomicReference = devices;
        Spinner spinnerDevices2 = spinnerDevices;
        C07242 c07242 = r0;
        final EditText editText = deviceID;
        C07242 c072422 = new AsyncTask<GpodnetService, Void, List<GpodnetDevice>>() {
            private volatile Exception exception;

            protected void onPreExecute() {
                super.onPreExecute();
                button.setEnabled(false);
                spinner.setEnabled(false);
                button2.setEnabled(false);
            }

            protected void onPostExecute(List<GpodnetDevice> gpodnetDevices) {
                super.onPostExecute(gpodnetDevices);
                if (gpodnetDevices != null) {
                    List<String> deviceNames = new ArrayList();
                    for (GpodnetDevice device : gpodnetDevices) {
                        deviceNames.add(device.getCaption());
                    }
                    spinner.setAdapter(new ArrayAdapter(GpodnetAuthenticationActivity.this, 17367049, deviceNames));
                    spinner.setEnabled(true);
                    if (!deviceNames.isEmpty()) {
                        button.setEnabled(true);
                    }
                    atomicReference.set(gpodnetDevices);
                    editText.setText(GpodnetAuthenticationActivity.this.generateDeviceID(gpodnetDevices));
                    button2.setEnabled(true);
                }
            }

            protected List<GpodnetDevice> doInBackground(GpodnetService... params) {
                try {
                    return params[0].getDevices(GpodnetAuthenticationActivity.this.username);
                } catch (GpodnetServiceException e) {
                    e.printStackTrace();
                    this.exception = e;
                    return null;
                }
            }
        };
        c07242.execute(new GpodnetService[]{this.service});
        final EditText editText2 = deviceID;
        final EditText editText3 = caption;
        final TextView textView = txtvError;
        final Button button3 = createNewDevice;
        AtomicReference<List<GpodnetDevice>> devices2 = devices;
        final Button button4 = chooseDevice;
        Spinner spinnerDevices3 = spinnerDevices2;
        C07263 c07263 = r0;
        final ProgressBar progressBar = progBarCreateDevice;
        C07263 c072632 = new OnClickListener() {
            public void onClick(View v) {
                if (GpodnetAuthenticationActivity.this.checkDeviceIDText(editText2, editText3, textView, (List) atomicReference.get())) {
                    final String deviceStr = editText2.getText().toString();
                    final String captionStr = editText3.getText().toString();
                    new AsyncTask<GpodnetService, Void, GpodnetDevice>() {
                        private volatile Exception exception;

                        protected void onPreExecute() {
                            super.onPreExecute();
                            button3.setEnabled(false);
                            button4.setEnabled(false);
                            progressBar.setVisibility(0);
                            textView.setVisibility(8);
                        }

                        protected void onPostExecute(GpodnetDevice result) {
                            super.onPostExecute(result);
                            button3.setEnabled(true);
                            button4.setEnabled(true);
                            progressBar.setVisibility(8);
                            if (this.exception == null) {
                                GpodnetAuthenticationActivity.this.selectedDevice = result;
                                GpodnetAuthenticationActivity.this.advance();
                                return;
                            }
                            textView.setText(this.exception.getMessage());
                            textView.setVisibility(0);
                        }

                        protected GpodnetDevice doInBackground(GpodnetService... params) {
                            try {
                                params[0].configureDevice(GpodnetAuthenticationActivity.this.username, deviceStr, captionStr, DeviceType.MOBILE);
                                return new GpodnetDevice(deviceStr, captionStr, DeviceType.MOBILE.toString(), 0);
                            } catch (GpodnetServiceException e) {
                                e.printStackTrace();
                                this.exception = e;
                                return null;
                            }
                        }
                    }.execute(new GpodnetService[]{GpodnetAuthenticationActivity.this.service});
                }
            }
        };
        createNewDevice.setOnClickListener(c07263);
        chooseDevice.setOnClickListener(new C0720x9b42b696(this, spinnerDevices3, devices2));
    }

    public static /* synthetic */ void lambda$setupDeviceView$1(GpodnetAuthenticationActivity gpodnetAuthenticationActivity, Spinner spinnerDevices, AtomicReference devices, View v) {
        int position = spinnerDevices.getSelectedItemPosition();
        if (position != -1) {
            gpodnetAuthenticationActivity.selectedDevice = (GpodnetDevice) ((List) devices.get()).get(position);
            gpodnetAuthenticationActivity.advance();
        }
    }

    private String generateDeviceID(List<GpodnetDevice> gpodnetDevices) {
        String baseId = Build.MODEL.replaceAll("\\W", "");
        String id = baseId;
        int num = 0;
        while (isDeviceWithIdInList(id, gpodnetDevices)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(baseId);
            stringBuilder.append("_");
            stringBuilder.append(num);
            id = stringBuilder.toString();
            num++;
        }
        return id;
    }

    private boolean isDeviceWithIdInList(String id, List<GpodnetDevice> gpodnetDevices) {
        if (gpodnetDevices == null) {
            return false;
        }
        for (GpodnetDevice device : gpodnetDevices) {
            if (device.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDeviceIDText(EditText deviceID, EditText caption, TextView txtvError, List<GpodnetDevice> devices) {
        String text = deviceID.getText().toString();
        if (text.length() == 0) {
            txtvError.setText(R.string.gpodnetauth_device_errorEmpty);
            txtvError.setVisibility(0);
            return false;
        } else if (caption.length() == 0) {
            txtvError.setText(R.string.gpodnetauth_device_caption_errorEmpty);
            txtvError.setVisibility(0);
            return false;
        } else if (devices == null) {
            return true;
        } else {
            if (isDeviceWithIdInList(text, devices)) {
                txtvError.setText(R.string.gpodnetauth_device_errorAlreadyUsed);
                txtvError.setVisibility(0);
                return false;
            }
            txtvError.setVisibility(8);
            return true;
        }
    }

    private void setupFinishView(View view) {
        Button back = (Button) view.findViewById(R.id.butGoMainscreen);
        ((Button) view.findViewById(R.id.butSyncNow)).setOnClickListener(new C0718xfa921cff());
        back.setOnClickListener(new C0719x6c43e3d6());
    }

    public static /* synthetic */ void lambda$setupFinishView$2(GpodnetAuthenticationActivity gpodnetAuthenticationActivity, View v) {
        GpodnetSyncService.sendSyncIntent(gpodnetAuthenticationActivity);
        gpodnetAuthenticationActivity.finish();
    }

    public static /* synthetic */ void lambda$setupFinishView$3(GpodnetAuthenticationActivity gpodnetAuthenticationActivity, View v) {
        Intent intent = new Intent(gpodnetAuthenticationActivity, MainActivity.class);
        intent.setFlags(67108864);
        gpodnetAuthenticationActivity.startActivity(intent);
    }

    private void writeLoginCredentials() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Writing login credentials");
        }
        GpodnetPreferences.setUsername(this.username);
        GpodnetPreferences.setPassword(this.password);
        GpodnetPreferences.setDeviceID(this.selectedDevice.getId());
    }

    private void advance() {
        int i = this.currentStep;
        if (i < 2) {
            View view = this.views[i + 1];
            if (i == -1) {
                setupLoginView(view);
            } else if (i == 0) {
                if (this.username == null || this.password == null) {
                    throw new IllegalStateException("Username and password must not be null here");
                }
                setupDeviceView(view);
            } else if (i == 1) {
                if (this.selectedDevice != null) {
                    writeLoginCredentials();
                    setupFinishView(view);
                } else {
                    throw new IllegalStateException("Device must not be null here");
                }
            }
            if (this.currentStep != -1) {
                this.viewFlipper.showNext();
            }
            this.currentStep++;
            return;
        }
        finish();
    }
}
