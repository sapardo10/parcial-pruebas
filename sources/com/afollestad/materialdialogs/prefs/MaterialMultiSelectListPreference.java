package com.afollestad.materialdialogs.prefs;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.preference.MultiSelectListPreference;
import android.preference.Preference.BaseSavedState;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.afollestad.materialdialogs.MaterialDialog.ListCallbackMultiChoice;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@TargetApi(11)
public class MaterialMultiSelectListPreference extends MultiSelectListPreference {
    private Context context;
    private MaterialDialog mDialog;

    private static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new C05131();
        Bundle dialogBundle;
        boolean isDialogShowing;

        /* renamed from: com.afollestad.materialdialogs.prefs.MaterialMultiSelectListPreference$SavedState$1 */
        static class C05131 implements Creator<SavedState> {
            C05131() {
            }

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        }

        public SavedState(Parcel source) {
            super(source);
            boolean z = true;
            if (source.readInt() != 1) {
                z = false;
            }
            this.isDialogShowing = z;
            this.dialogBundle = source.readBundle();
        }

        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.isDialogShowing);
            dest.writeBundle(this.dialogBundle);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }
    }

    /* renamed from: com.afollestad.materialdialogs.prefs.MaterialMultiSelectListPreference$1 */
    class C09381 implements ListCallbackMultiChoice {
        C09381() {
        }

        public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
            MaterialMultiSelectListPreference.this.onClick(null, -1);
            dialog.dismiss();
            Set<String> values = new HashSet();
            for (int i : which) {
                values.add(MaterialMultiSelectListPreference.this.getEntryValues()[i.intValue()].toString());
            }
            if (MaterialMultiSelectListPreference.this.callChangeListener(values)) {
                MaterialMultiSelectListPreference.this.setValues(values);
            }
            return true;
        }
    }

    /* renamed from: com.afollestad.materialdialogs.prefs.MaterialMultiSelectListPreference$2 */
    class C09392 implements SingleButtonCallback {
        C09392() {
        }

        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            switch (which) {
                case NEUTRAL:
                    MaterialMultiSelectListPreference.this.onClick(dialog, -3);
                    return;
                case NEGATIVE:
                    MaterialMultiSelectListPreference.this.onClick(dialog, -2);
                    return;
                default:
                    MaterialMultiSelectListPreference.this.onClick(dialog, -1);
                    return;
            }
        }
    }

    public MaterialMultiSelectListPreference(Context context) {
        super(context);
        init(context, null);
    }

    public MaterialMultiSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @TargetApi(21)
    public MaterialMultiSelectListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(21)
    public MaterialMultiSelectListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public void setEntries(CharSequence[] entries) {
        super.setEntries(entries);
        MaterialDialog materialDialog = this.mDialog;
        if (materialDialog != null) {
            materialDialog.setItems(entries);
        }
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        PrefUtil.setLayoutResource(context, this, attrs);
        if (VERSION.SDK_INT <= 10) {
            setWidgetLayoutResource(0);
        }
    }

    public Dialog getDialog() {
        return this.mDialog;
    }

    protected void showDialog(Bundle state) {
        List<Integer> indices = new ArrayList();
        for (String s : getValues()) {
            if (findIndexOfValue(s) >= 0) {
                indices.add(Integer.valueOf(findIndexOfValue(s)));
            }
        }
        Builder builder = new Builder(this.context).title(getDialogTitle()).icon(getDialogIcon()).negativeText(getNegativeButtonText()).positiveText(getPositiveButtonText()).onAny(new C09392()).items(getEntries()).itemsCallbackMultiChoice((Integer[]) indices.toArray(new Integer[indices.size()]), new C09381()).dismissListener(this);
        View contentView = onCreateDialogView();
        if (contentView != null) {
            onBindDialogView(contentView);
            builder.customView(contentView, false);
        } else {
            builder.content(getDialogMessage());
        }
        PrefUtil.registerOnActivityDestroyListener(this, this);
        this.mDialog = builder.build();
        if (state != null) {
            this.mDialog.onRestoreInstanceState(state);
        }
        this.mDialog.show();
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        PrefUtil.unregisterOnActivityDestroyListener(this, this);
    }

    public void onActivityDestroy() {
        super.onActivityDestroy();
        MaterialDialog materialDialog = this.mDialog;
        if (materialDialog != null && materialDialog.isShowing()) {
            this.mDialog.dismiss();
        }
    }

    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        Dialog dialog = getDialog();
        if (dialog != null) {
            if (dialog.isShowing()) {
                SavedState myState = new SavedState(superState);
                myState.isDialogShowing = true;
                myState.dialogBundle = dialog.onSaveInstanceState();
                return myState;
            }
        }
        return superState;
    }

    protected void onRestoreInstanceState(Parcelable state) {
        if (state != null) {
            if (state.getClass().equals(SavedState.class)) {
                SavedState myState = (SavedState) state;
                super.onRestoreInstanceState(myState.getSuperState());
                if (myState.isDialogShowing) {
                    showDialog(myState.dialogBundle);
                }
                return;
            }
        }
        super.onRestoreInstanceState(state);
    }
}
