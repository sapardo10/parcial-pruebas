package android.support.wearable.preference;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.preference.Preference.BaseSavedState;
import android.preference.SwitchPreference;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.wearable.C0395R;
import android.support.wearable.view.AcceptDenyDialog;
import android.util.AttributeSet;

@TargetApi(21)
public class AcceptDenySwitchPreference extends SwitchPreference implements OnClickListener, OnDismissListener {
    private AcceptDenyDialog mDialog;
    private Drawable mDialogIcon;
    private CharSequence mDialogMessage;
    private CharSequence mDialogTitle;
    private boolean mShowDialogWhenTurningOff;
    private boolean mShowDialogWhenTurningOn;
    private boolean mShowNegativeButton;
    private boolean mShowPositiveButton;
    private int mWhichButtonClicked;

    private static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new C04251();
        Bundle dialogBundle;
        boolean isDialogShowing;

        /* renamed from: android.support.wearable.preference.AcceptDenySwitchPreference$SavedState$1 */
        class C04251 implements Creator<SavedState> {
            C04251() {
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

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.isDialogShowing);
            dest.writeBundle(this.dialogBundle);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }
    }

    public AcceptDenySwitchPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    public AcceptDenySwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public AcceptDenySwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public AcceptDenySwitchPreference(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, C0395R.styleable.AcceptDenySwitchPreference, defStyleAttr, defStyleRes);
        this.mDialogTitle = a.getString(C0395R.styleable.AcceptDenySwitchPreference_dialogTitle);
        if (this.mDialogTitle == null) {
            this.mDialogTitle = getTitle();
        }
        this.mDialogMessage = a.getString(C0395R.styleable.AcceptDenySwitchPreference_dialogMessage);
        this.mDialogIcon = a.getDrawable(C0395R.styleable.AcceptDenySwitchPreference_dialogIcon);
        this.mShowPositiveButton = a.getBoolean(C0395R.styleable.AcceptDenySwitchPreference_showPositiveDialogButton, true);
        this.mShowNegativeButton = a.getBoolean(C0395R.styleable.AcceptDenySwitchPreference_showNegativeDialogButton, true);
        this.mShowDialogWhenTurningOn = a.getBoolean(C0395R.styleable.AcceptDenySwitchPreference_showDialogWhenTurningOn, true);
        this.mShowDialogWhenTurningOff = a.getBoolean(C0395R.styleable.AcceptDenySwitchPreference_showDialogWhenTurningOff, false);
        a.recycle();
    }

    public void setDialogTitle(CharSequence dialogTitle) {
        this.mDialogTitle = dialogTitle;
    }

    public void setDialogTitle(@StringRes int dialogTitleResId) {
        setDialogTitle(getContext().getString(dialogTitleResId));
    }

    public CharSequence getDialogTitle() {
        return this.mDialogTitle;
    }

    public void setDialogMessage(CharSequence dialogMessage) {
        this.mDialogMessage = dialogMessage;
    }

    public void setDialogMessage(@StringRes int dialogMessageResId) {
        setDialogMessage(getContext().getString(dialogMessageResId));
    }

    public CharSequence getDialogMessage() {
        return this.mDialogMessage;
    }

    public void setDialogIcon(Drawable dialogIcon) {
        this.mDialogIcon = dialogIcon;
    }

    public void setDialogIcon(@DrawableRes int dialogIconRes) {
        this.mDialogIcon = getContext().getDrawable(dialogIconRes);
    }

    public Drawable getDialogIcon() {
        return this.mDialogIcon;
    }

    protected void onPrepareDialog(@NonNull AcceptDenyDialog dialog) {
    }

    protected void onClick() {
        AcceptDenyDialog acceptDenyDialog = this.mDialog;
        if (acceptDenyDialog == null || !acceptDenyDialog.isShowing()) {
            boolean newValue = isChecked() ^ 1;
            if (this.mShowDialogWhenTurningOn) {
                if (!newValue) {
                }
                showDialog(null);
            }
            if (!this.mShowDialogWhenTurningOff || newValue) {
                if (callChangeListener(Boolean.valueOf(newValue))) {
                    setChecked(newValue);
                }
            }
            showDialog(null);
        }
    }

    protected void showDialog(@Nullable Bundle state) {
        Context context = getContext();
        this.mWhichButtonClicked = -2;
        this.mDialog = new AcceptDenyDialog(context);
        this.mDialog.setTitle(this.mDialogTitle);
        this.mDialog.setIcon(this.mDialogIcon);
        this.mDialog.setMessage(this.mDialogMessage);
        if (this.mShowPositiveButton) {
            this.mDialog.setPositiveButton(this);
        }
        if (this.mShowNegativeButton) {
            this.mDialog.setNegativeButton(this);
        }
        onPrepareDialog(this.mDialog);
        if (state != null) {
            this.mDialog.onRestoreInstanceState(state);
        }
        this.mDialog.setOnDismissListener(this);
        this.mDialog.show();
    }

    public void onClick(DialogInterface dialog, int which) {
        this.mWhichButtonClicked = which;
    }

    public void onDismiss(@NonNull DialogInterface dialog) {
        this.mDialog = null;
        onDialogClosed(this.mWhichButtonClicked == -1);
    }

    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            boolean newValue = isChecked() ^ 1;
            if (callChangeListener(Boolean.valueOf(newValue))) {
                setChecked(newValue);
            }
        }
    }

    public Dialog getDialog() {
        return this.mDialog;
    }

    public void onPrepareForRemoval() {
        AcceptDenyDialog acceptDenyDialog = this.mDialog;
        if (acceptDenyDialog != null) {
            if (acceptDenyDialog.isShowing()) {
                this.mDialog.dismiss();
            }
        }
    }

    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        AcceptDenyDialog acceptDenyDialog = this.mDialog;
        if (acceptDenyDialog != null) {
            if (acceptDenyDialog.isShowing()) {
                SavedState myState = new SavedState(superState);
                myState.isDialogShowing = true;
                myState.dialogBundle = this.mDialog.onSaveInstanceState();
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
