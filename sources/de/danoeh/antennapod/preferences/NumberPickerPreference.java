package de.danoeh.antennapod.preferences;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v7.preference.Preference;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import de.danoeh.antennapod.debug.R;

public class NumberPickerPreference extends Preference {
    private Context context;
    private int defaultValue = 0;
    private int maxValue = Integer.MAX_VALUE;
    private int minValue = 0;

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public NumberPickerPreference(Context context) {
        super(context);
        this.context = context;
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            String name = attrs.getAttributeName(i);
            String value = attrs.getAttributeValue(i);
            Object obj = -1;
            int hashCode = name.hashCode();
            if (hashCode != -1376969153) {
                if (hashCode != -659125328) {
                    if (hashCode == 399227501 && name.equals("maxValue")) {
                        obj = 2;
                        switch (obj) {
                            case null:
                                this.defaultValue = Integer.parseInt(value);
                                break;
                            case 1:
                                this.minValue = Integer.parseInt(value);
                                break;
                            case 2:
                                this.maxValue = Integer.parseInt(value);
                                break;
                            default:
                                break;
                        }
                    }
                } else if (name.equals("defaultValue")) {
                    obj = null;
                    switch (obj) {
                        case null:
                            this.defaultValue = Integer.parseInt(value);
                            break;
                        case 1:
                            this.minValue = Integer.parseInt(value);
                            break;
                        case 2:
                            this.maxValue = Integer.parseInt(value);
                            break;
                        default:
                            break;
                    }
                }
            } else if (name.equals("minValue")) {
                obj = 1;
                switch (obj) {
                    case null:
                        this.defaultValue = Integer.parseInt(value);
                        break;
                    case 1:
                        this.minValue = Integer.parseInt(value);
                        break;
                    case 2:
                        this.maxValue = Integer.parseInt(value);
                        break;
                    default:
                        break;
                }
            }
            switch (obj) {
                case null:
                    this.defaultValue = Integer.parseInt(value);
                    break;
                case 1:
                    this.minValue = Integer.parseInt(value);
                    break;
                case 2:
                    this.maxValue = Integer.parseInt(value);
                    break;
                default:
                    break;
            }
        }
    }

    protected void onClick() {
        super.onClick();
        View view = View.inflate(this.context, R.layout.numberpicker, null);
        EditText number = (EditText) view.findViewById(R.id.number);
        SharedPreferences sharedPreferences = getSharedPreferences();
        String key = getKey();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(this.defaultValue);
        number.setText(sharedPreferences.getString(key, stringBuilder.toString()));
        number.setFilters(new InputFilter[]{new -$$Lambda$NumberPickerPreference$H02MuUBWtIRF7qrCn8yg9CFYDWE()});
        AlertDialog dialog = new Builder(this.context).setTitle(getTitle()).setView(view).setNegativeButton(17039360, null).setPositiveButton(17039370, new -$$Lambda$NumberPickerPreference$rOanAep0PMT7MyptR8H82-pky8U(this, number)).create();
        dialog.show();
        dialog.getWindow().setSoftInputMode(5);
    }

    public static /* synthetic */ CharSequence lambda$onClick$0(NumberPickerPreference numberPickerPreference, CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            String newVal = new StringBuilder();
            newVal.append(dest.toString().substring(0, dstart));
            newVal.append(dest.toString().substring(dend));
            newVal = newVal.toString();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(newVal.substring(0, dstart));
            stringBuilder.append(source.toString());
            stringBuilder.append(newVal.substring(dstart));
            int input = Integer.parseInt(stringBuilder.toString());
            if (input < numberPickerPreference.minValue || input > numberPickerPreference.maxValue) {
                return "";
            }
            return null;
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
    }

    public static /* synthetic */ void lambda$onClick$1(NumberPickerPreference numberPickerPreference, EditText number, DialogInterface dialogInterface, int i) {
        try {
            int value = Integer.parseInt(number.getText().toString());
            if (value >= numberPickerPreference.minValue) {
                if (value <= numberPickerPreference.maxValue) {
                    Editor edit = numberPickerPreference.getSharedPreferences().edit();
                    String key = numberPickerPreference.getKey();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("");
                    stringBuilder.append(value);
                    edit.putString(key, stringBuilder.toString()).apply();
                    if (numberPickerPreference.getOnPreferenceChangeListener() != null) {
                        numberPickerPreference.getOnPreferenceChangeListener().onPreferenceChange(numberPickerPreference, Integer.valueOf(value));
                    }
                }
            }
        } catch (NumberFormatException e) {
        }
    }
}
