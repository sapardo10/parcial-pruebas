package android.support.v7.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SwitchPreferenceCompat extends TwoStatePreference {
    private final Listener mListener;
    private CharSequence mSwitchOff;
    private CharSequence mSwitchOn;

    private class Listener implements OnCheckedChangeListener {
        private Listener() {
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (SwitchPreferenceCompat.this.callChangeListener(Boolean.valueOf(isChecked))) {
                SwitchPreferenceCompat.this.setChecked(isChecked);
            } else {
                buttonView.setChecked(isChecked ^ 1);
            }
        }
    }

    public SwitchPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mListener = new Listener();
        TypedArray a = context.obtainStyledAttributes(attrs, C0315R.styleable.SwitchPreferenceCompat, defStyleAttr, defStyleRes);
        setSummaryOn(TypedArrayUtils.getString(a, C0315R.styleable.SwitchPreferenceCompat_summaryOn, C0315R.styleable.SwitchPreferenceCompat_android_summaryOn));
        setSummaryOff(TypedArrayUtils.getString(a, C0315R.styleable.SwitchPreferenceCompat_summaryOff, C0315R.styleable.SwitchPreferenceCompat_android_summaryOff));
        setSwitchTextOn(TypedArrayUtils.getString(a, C0315R.styleable.SwitchPreferenceCompat_switchTextOn, C0315R.styleable.SwitchPreferenceCompat_android_switchTextOn));
        setSwitchTextOff(TypedArrayUtils.getString(a, C0315R.styleable.SwitchPreferenceCompat_switchTextOff, C0315R.styleable.SwitchPreferenceCompat_android_switchTextOff));
        setDisableDependentsState(TypedArrayUtils.getBoolean(a, C0315R.styleable.SwitchPreferenceCompat_disableDependentsState, C0315R.styleable.SwitchPreferenceCompat_android_disableDependentsState, false));
        a.recycle();
    }

    public SwitchPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SwitchPreferenceCompat(Context context, AttributeSet attrs) {
        this(context, attrs, C0315R.attr.switchPreferenceCompatStyle);
    }

    public SwitchPreferenceCompat(Context context) {
        this(context, null);
    }

    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        syncSwitchView(holder.findViewById(C0315R.id.switchWidget));
        syncSummaryView(holder);
    }

    public void setSwitchTextOn(CharSequence onText) {
        this.mSwitchOn = onText;
        notifyChanged();
    }

    public void setSwitchTextOff(CharSequence offText) {
        this.mSwitchOff = offText;
        notifyChanged();
    }

    public void setSwitchTextOn(int resId) {
        setSwitchTextOn(getContext().getString(resId));
    }

    public void setSwitchTextOff(int resId) {
        setSwitchTextOff(getContext().getString(resId));
    }

    public CharSequence getSwitchTextOn() {
        return this.mSwitchOn;
    }

    public CharSequence getSwitchTextOff() {
        return this.mSwitchOff;
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    protected void performClick(View view) {
        super.performClick(view);
        syncViewIfAccessibilityEnabled(view);
    }

    private void syncViewIfAccessibilityEnabled(View view) {
        if (((AccessibilityManager) getContext().getSystemService("accessibility")).isEnabled()) {
            syncSwitchView(view.findViewById(C0315R.id.switchWidget));
            syncSummaryView(view.findViewById(16908304));
        }
    }

    private void syncSwitchView(View view) {
        if (view instanceof SwitchCompat) {
            ((SwitchCompat) view).setOnCheckedChangeListener(null);
        }
        if (view instanceof Checkable) {
            ((Checkable) view).setChecked(this.mChecked);
        }
        if (view instanceof SwitchCompat) {
            SwitchCompat switchView = (SwitchCompat) view;
            switchView.setTextOn(this.mSwitchOn);
            switchView.setTextOff(this.mSwitchOff);
            switchView.setOnCheckedChangeListener(this.mListener);
        }
    }
}
