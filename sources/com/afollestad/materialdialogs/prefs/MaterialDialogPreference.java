package com.afollestad.materialdialogs.prefs;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.preference.DialogPreference;
import android.preference.Preference.BaseSavedState;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;

public class MaterialDialogPreference extends DialogPreference {
    private Context context;
    private MaterialDialog mDialog;

    private static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new C05071();
        Bundle dialogBundle;
        boolean isDialogShowing;

        /* renamed from: com.afollestad.materialdialogs.prefs.MaterialDialogPreference$SavedState$1 */
        static class C05071 implements Creator<SavedState> {
            C05071() {
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

    /* renamed from: com.afollestad.materialdialogs.prefs.MaterialDialogPreference$1 */
    class C09341 implements SingleButtonCallback {
        C09341() {
        }

        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            switch (which) {
                case NEUTRAL:
                    MaterialDialogPreference.this.onClick(dialog, -3);
                    return;
                case NEGATIVE:
                    MaterialDialogPreference.this.onClick(dialog, -2);
                    return;
                default:
                    MaterialDialogPreference.this.onClick(dialog, -1);
                    return;
            }
        }
    }

    @TargetApi(21)
    public MaterialDialogPreference(Context context) {
        super(context);
        init(context, null);
    }

    public MaterialDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MaterialDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(21)
    public MaterialDialogPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        PrefUtil.setLayoutResource(context, this, attrs);
    }

    public Dialog getDialog() {
        return this.mDialog;
    }

    protected void showDialog(Bundle state) {
        Builder builder = new Builder(this.context).title(getDialogTitle()).icon(getDialogIcon()).dismissListener(this).onAny(new C09341()).positiveText(getPositiveButtonText()).negativeText(getNegativeButtonText()).autoDismiss(true);
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
