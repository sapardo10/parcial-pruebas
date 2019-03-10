package android.support.wearable.view;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

@TargetApi(21)
public class AcceptDenyDialogFragment extends DialogFragment implements android.content.DialogInterface.OnClickListener {
    private static final String EXTRA_DIALOG_BUILDER = "extra_dialog_builder";

    public static class Builder implements Parcelable {
        public static final Creator<Builder> CREATOR = new C04281();
        private int mIconRes;
        private String mMessage;
        private boolean mShowNegativeButton;
        private boolean mShowPositiveButton;
        private String mTitle;

        /* renamed from: android.support.wearable.view.AcceptDenyDialogFragment$Builder$1 */
        class C04281 implements Creator<Builder> {
            C04281() {
            }

            public Builder createFromParcel(Parcel in) {
                return new Builder(in);
            }

            public Builder[] newArray(int size) {
                return new Builder[size];
            }
        }

        @NonNull
        public Builder setTitle(String title) {
            this.mTitle = title;
            return this;
        }

        @NonNull
        public Builder setMessage(String message) {
            this.mMessage = message;
            return this;
        }

        @NonNull
        public Builder setIconRes(@DrawableRes int iconRes) {
            this.mIconRes = iconRes;
            return this;
        }

        @NonNull
        public Builder setShowPositiveButton(boolean show) {
            this.mShowPositiveButton = show;
            return this;
        }

        @NonNull
        public Builder setShowNegativeButton(boolean show) {
            this.mShowNegativeButton = show;
            return this;
        }

        public <T extends AcceptDenyDialogFragment> T apply(T f) {
            Bundle args = f.getArguments();
            if (args == null) {
                args = new Bundle();
                f.setArguments(args);
            }
            args.putParcelable(AcceptDenyDialogFragment.EXTRA_DIALOG_BUILDER, this);
            return f;
        }

        @NonNull
        public AcceptDenyDialogFragment build() {
            return apply(new AcceptDenyDialogFragment());
        }

        protected void createDialog(@NonNull AcceptDenyDialog dialog, android.content.DialogInterface.OnClickListener buttonListener) {
            dialog.setTitle(this.mTitle);
            dialog.setMessage(this.mMessage);
            if (this.mIconRes != 0) {
                dialog.setIcon(dialog.getContext().getDrawable(this.mIconRes));
            }
            if (this.mShowPositiveButton) {
                if (buttonListener != null) {
                    dialog.setPositiveButton(buttonListener);
                } else {
                    throw new IllegalArgumentException("buttonListener must not be null when used with buttons");
                }
            }
            if (!this.mShowNegativeButton) {
                return;
            }
            if (buttonListener != null) {
                dialog.setNegativeButton(buttonListener);
                return;
            }
            throw new IllegalArgumentException("buttonListener must not be null when used with buttons");
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeString(this.mTitle);
            out.writeString(this.mMessage);
            out.writeInt(this.mIconRes);
            out.writeValue(Boolean.valueOf(this.mShowPositiveButton));
            out.writeValue(Boolean.valueOf(this.mShowNegativeButton));
        }

        private Builder(Parcel in) {
            this.mTitle = in.readString();
            this.mMessage = in.readString();
            this.mIconRes = in.readInt();
            this.mShowPositiveButton = ((Boolean) in.readValue(null)).booleanValue();
            this.mShowNegativeButton = ((Boolean) in.readValue(null)).booleanValue();
        }
    }

    public interface OnCancelListener {
        void onCancel(@NonNull AcceptDenyDialogFragment acceptDenyDialogFragment);
    }

    public interface OnClickListener {
        void onClick(@NonNull AcceptDenyDialogFragment acceptDenyDialogFragment, int i);
    }

    public interface OnDismissListener {
        void onDismiss(@NonNull AcceptDenyDialogFragment acceptDenyDialogFragment);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AcceptDenyDialog dialog = new AcceptDenyDialog(getActivity());
        Builder builder = (Builder) getArguments().getParcelable(EXTRA_DIALOG_BUILDER);
        if (builder != null) {
            builder.createDialog(dialog, this);
        }
        onPrepareDialog(dialog);
        return dialog;
    }

    protected void onPrepareDialog(@NonNull AcceptDenyDialog dialog) {
    }

    public void onClick(DialogInterface dialog, int which) {
        if (getActivity() instanceof OnClickListener) {
            ((OnClickListener) getActivity()).onClick(this, which);
        }
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (getActivity() instanceof OnCancelListener) {
            ((OnCancelListener) getActivity()).onCancel(this);
        }
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getActivity() instanceof OnDismissListener) {
            ((OnDismissListener) getActivity()).onDismiss(this);
        }
    }
}
