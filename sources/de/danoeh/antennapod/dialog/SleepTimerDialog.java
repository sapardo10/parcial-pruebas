package de.danoeh.antennapod.dialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import de.danoeh.antennapod.core.event.MessageEvent;
import de.danoeh.antennapod.core.preferences.SleepTimerPreferences;
import de.danoeh.antennapod.debug.R;
import de.greenrobot.event.EventBus;

public abstract class SleepTimerDialog {
    private static final String TAG = SleepTimerDialog.class.getSimpleName();
    private CheckBox cbShakeToReset;
    private CheckBox cbVibrate;
    private CheckBox chAutoEnable;
    private final Context context;
    private MaterialDialog dialog;
    private EditText etxtTime;
    private Spinner spTimeUnit;

    /* renamed from: de.danoeh.antennapod.dialog.SleepTimerDialog$1 */
    class C07831 implements TextWatcher {
        C07831() {
        }

        public void afterTextChanged(Editable s) {
            SleepTimerDialog.this.checkInputLength(s.length());
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }

    public abstract void onTimerSet(long j, boolean z, boolean z2);

    protected SleepTimerDialog(Context context) {
        this.context = context;
    }

    public MaterialDialog createNewDialog() {
        Builder builder = new Builder(this.context);
        builder.title((int) R.string.set_sleeptimer_label);
        builder.customView((int) R.layout.time_dialog, false);
        builder.positiveText((int) R.string.set_sleeptimer_label);
        builder.negativeText((int) R.string.cancel_label);
        builder.onNegative(-$$Lambda$SleepTimerDialog$l7KYSPUXerrKBJlUTwaUuEt2gj8.INSTANCE);
        builder.onPositive(new -$$Lambda$SleepTimerDialog$XsjWyT2mrBjGBlHZO3YIH8KrYTo());
        this.dialog = builder.build();
        View view = this.dialog.getView();
        this.etxtTime = (EditText) view.findViewById(R.id.etxtTime);
        this.spTimeUnit = (Spinner) view.findViewById(R.id.spTimeUnit);
        this.cbShakeToReset = (CheckBox) view.findViewById(R.id.cbShakeToReset);
        this.cbVibrate = (CheckBox) view.findViewById(R.id.cbVibrate);
        this.chAutoEnable = (CheckBox) view.findViewById(R.id.chAutoEnable);
        this.etxtTime.setText(SleepTimerPreferences.lastTimerValue());
        this.etxtTime.addTextChangedListener(new C07831());
        this.etxtTime.postDelayed(new -$$Lambda$SleepTimerDialog$B8UcYP-mR1V0K-N1nYGtdPdiRms(), 100);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter(this.context, 17367048, new String[]{this.context.getString(R.string.time_seconds), this.context.getString(R.string.time_minutes), this.context.getString(R.string.time_hours)});
        spinnerAdapter.setDropDownViewResource(17367049);
        this.spTimeUnit.setAdapter(spinnerAdapter);
        this.spTimeUnit.setSelection(SleepTimerPreferences.lastTimerTimeUnit());
        this.cbShakeToReset.setChecked(SleepTimerPreferences.shakeToReset());
        this.cbVibrate.setChecked(SleepTimerPreferences.vibrate());
        this.chAutoEnable.setChecked(SleepTimerPreferences.autoEnable());
        this.chAutoEnable.setOnCheckedChangeListener(new -$$Lambda$SleepTimerDialog$KKUPWdeznpO_dkPPfqjboud8Q04());
        return this.dialog;
    }

    public static /* synthetic */ void lambda$createNewDialog$1(SleepTimerDialog sleepTimerDialog, MaterialDialog dialog, DialogAction which) {
        try {
            sleepTimerDialog.savePreferences();
            sleepTimerDialog.onTimerSet(SleepTimerPreferences.timerMillis(), sleepTimerDialog.cbShakeToReset.isChecked(), sleepTimerDialog.cbVibrate.isChecked());
            dialog.dismiss();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(sleepTimerDialog.context, R.string.time_dialog_invalid_input, 1).show();
        }
    }

    public static /* synthetic */ void lambda$createNewDialog$3(SleepTimerDialog sleepTimerDialog, CompoundButton compoundButton, boolean isChecked) {
        SleepTimerPreferences.setAutoEnable(isChecked);
        EventBus.getDefault().post(new MessageEvent(sleepTimerDialog.context.getString(isChecked ? R.string.sleep_timer_enabled_label : R.string.sleep_timer_disabled_label)));
    }

    private void checkInputLength(int length) {
        if (length > 0) {
            Log.d(TAG, "Length is larger than 0, enabling confirm button");
            this.dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
            return;
        }
        Log.d(TAG, "Length is smaller than 0, disabling confirm button");
        this.dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
    }

    private void savePreferences() {
        SleepTimerPreferences.setLastTimer(this.etxtTime.getText().toString(), this.spTimeUnit.getSelectedItemPosition());
        SleepTimerPreferences.setShakeToReset(this.cbShakeToReset.isChecked());
        SleepTimerPreferences.setVibrate(this.cbVibrate.isChecked());
        SleepTimerPreferences.setAutoEnable(this.chAutoEnable.isChecked());
    }
}
