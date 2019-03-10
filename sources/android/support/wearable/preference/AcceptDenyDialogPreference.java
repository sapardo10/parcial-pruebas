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
import android.preference.Preference;
import android.preference.Preference.BaseSavedState;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.wearable.C0395R;
import android.support.wearable.view.AcceptDenyDialog;
import android.util.AttributeSet;

@TargetApi(21)
public class AcceptDenyDialogPreference extends Preference implements OnClickListener, OnDismissListener {
    private AcceptDenyDialog mDialog;
    private Drawable mDialogIcon;
    private CharSequence mDialogMessage;
    private CharSequence mDialogTitle;
    private OnDialogClosedListener mOnDialogClosedListener;
    private boolean mShowNegativeButton;
    private boolean mShowPositiveButton;
    private int mWhichButtonClicked;

    public interface OnDialogClosedListener {
        void onDialogClosed(boolean z);
    }

    private static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new C04241();
        Bundle dialogBundle;
        boolean isDialogShowing;

        /* renamed from: android.support.wearable.preference.AcceptDenyDialogPreference$SavedState$1 */
        class C04241 implements Creator<SavedState> {
            C04241() {
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

    public AcceptDenyDialogPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    public AcceptDenyDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public AcceptDenyDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public AcceptDenyDialogPreference(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, C0395R.styleable.AcceptDenyDialogPreference, defStyleAttr, defStyleRes);
        this.mDialogTitle = a.getString(C0395R.styleable.AcceptDenyDialogPreference_dialogTitle);
        if (this.mDialogTitle == null) {
            this.mDialogTitle = getTitle();
        }
        this.mDialogMessage = a.getString(C0395R.styleable.AcceptDenyDialogPreference_dialogMessage);
        this.mDialogIcon = a.getDrawable(C0395R.styleable.AcceptDenyDialogPreference_dialogIcon);
        this.mShowPositiveButton = a.getBoolean(C0395R.styleable.AcceptDenyDialogPreference_showPositiveDialogButton, true);
        this.mShowNegativeButton = a.getBoolean(C0395R.styleable.AcceptDenyDialogPreference_showNegativeDialogButton, true);
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

    public void setOnDialogClosedListener(OnDialogClosedListener listener) {
        this.mOnDialogClosedListener = listener;
    }

    public OnDialogClosedListener getOnDialogClosedListener() {
        return this.mOnDialogClosedListener;
    }

    protected void onPrepareDialog(@NonNull AcceptDenyDialog dialog) {
    }

    protected void onClick() {
        AcceptDenyDialog acceptDenyDialog = this.mDialog;
        if (acceptDenyDialog == null || !acceptDenyDialog.isShowing()) {
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
        OnDialogClosedListener onDialogClosedListener = this.mOnDialogClosedListener;
        if (onDialogClosedListener != null) {
            onDialogClosedListener.onDialogClosed(this.mWhichButtonClicked == -1);
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
