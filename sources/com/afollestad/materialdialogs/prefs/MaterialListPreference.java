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
import android.preference.ListPreference;
import android.preference.Preference.BaseSavedState;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.afollestad.materialdialogs.MaterialDialog.ListCallbackSingleChoice;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import java.lang.reflect.Field;

public class MaterialListPreference extends ListPreference {
    private Context context;
    private MaterialDialog mDialog;

    private static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new C05111();
        Bundle dialogBundle;
        boolean isDialogShowing;

        /* renamed from: com.afollestad.materialdialogs.prefs.MaterialListPreference$SavedState$1 */
        static class C05111 implements Creator<SavedState> {
            C05111() {
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

    /* renamed from: com.afollestad.materialdialogs.prefs.MaterialListPreference$1 */
    class C09361 implements ListCallbackSingleChoice {
        C09361() {
        }

        public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
            MaterialListPreference.this.onClick(null, -1);
            if (which >= 0 && MaterialListPreference.this.getEntryValues() != null) {
                try {
                    Field clickedIndex = ListPreference.class.getDeclaredField("mClickedDialogEntryIndex");
                    clickedIndex.setAccessible(true);
                    clickedIndex.set(MaterialListPreference.this, Integer.valueOf(which));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
    }

    /* renamed from: com.afollestad.materialdialogs.prefs.MaterialListPreference$2 */
    class C09372 implements SingleButtonCallback {
        C09372() {
        }

        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            switch (which) {
                case NEUTRAL:
                    MaterialListPreference.this.onClick(dialog, -3);
                    return;
                case NEGATIVE:
                    MaterialListPreference.this.onClick(dialog, -2);
                    return;
                default:
                    MaterialListPreference.this.onClick(dialog, -1);
                    return;
            }
        }
    }

    public MaterialListPreference(Context context) {
        super(context);
        init(context, null);
    }

    public MaterialListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @TargetApi(21)
    public MaterialListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(21)
    public MaterialListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        PrefUtil.setLayoutResource(context, this, attrs);
        if (VERSION.SDK_INT <= 10) {
            setWidgetLayoutResource(0);
        }
    }

    public void setEntries(CharSequence[] entries) {
        super.setEntries(entries);
        MaterialDialog materialDialog = this.mDialog;
        if (materialDialog != null) {
            materialDialog.setItems(entries);
        }
    }

    public Dialog getDialog() {
        return this.mDialog;
    }

    public RecyclerView getRecyclerView() {
        if (getDialog() == null) {
            return null;
        }
        return ((MaterialDialog) getDialog()).getRecyclerView();
    }

    protected void showDialog(Bundle state) {
        if (getEntries() == null || getEntryValues() == null) {
            throw new IllegalStateException("ListPreference requires an entries array and an entryValues array.");
        }
        Builder builder = new Builder(this.context).title(getDialogTitle()).icon(getDialogIcon()).dismissListener(this).onAny(new C09372()).negativeText(getNegativeButtonText()).items(getEntries()).autoDismiss(true).itemsCallbackSingleChoice(findIndexOfValue(getValue()), new C09361());
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
        onClick(this.mDialog, -2);
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
