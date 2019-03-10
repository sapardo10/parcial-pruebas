package android.support.wearable.preference;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.wearable.C0395R;
import android.support.wearable.view.WearableDialogHelper;
import android.util.AttributeSet;

@TargetApi(23)
public class WearableDialogPreference extends DialogPreference {
    private WearableDialogHelper mHelper;
    private CharSequence mNeutralButtonText;
    private OnDialogClosedListener mOnDialogClosedListener;
    private int mWhichButtonClicked;

    public interface OnDialogClosedListener {
        void onDialogClosed(int i);
    }

    public WearableDialogPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    public WearableDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public WearableDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public WearableDialogPreference(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this.mHelper = new WearableDialogHelper(context.getResources(), context.getTheme());
        TypedArray a = context.obtainStyledAttributes(attrs, C0395R.styleable.WearableDialogPreference, defStyleAttr, defStyleRes);
        this.mHelper.setPositiveIcon(a.getDrawable(C0395R.styleable.WearableDialogPreference_positiveButtonIcon));
        this.mHelper.setNeutralIcon(a.getDrawable(C0395R.styleable.WearableDialogPreference_neutralButtonIcon));
        this.mHelper.setNegativeIcon(a.getDrawable(C0395R.styleable.WearableDialogPreference_negativeButtonIcon));
        this.mNeutralButtonText = a.getString(C0395R.styleable.WearableDialogPreference_neutralButtonText);
        a.recycle();
    }

    @CallSuper
    protected void onPrepareDialogBuilder(Builder builder) {
        super.onPrepareDialogBuilder(builder.setNeutralButton(this.mNeutralButtonText, this));
    }

    protected void showDialog(Bundle state) {
        super.showDialog(state);
        this.mHelper.apply((AlertDialog) getDialog());
        this.mWhichButtonClicked = 0;
    }

    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
        this.mWhichButtonClicked = which;
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        OnDialogClosedListener onDialogClosedListener = this.mOnDialogClosedListener;
        if (onDialogClosedListener != null) {
            onDialogClosedListener.onDialogClosed(this.mWhichButtonClicked);
        }
    }

    public void setOnDialogClosedListener(OnDialogClosedListener listener) {
        this.mOnDialogClosedListener = listener;
    }

    public OnDialogClosedListener getOnDialogClosedListener() {
        return this.mOnDialogClosedListener;
    }

    public Drawable getPositiveIcon() {
        return this.mHelper.getPositiveIcon();
    }

    public void setPositiveIcon(@DrawableRes int dialogIconRes) {
        this.mHelper.setPositiveIcon(getContext().getDrawable(dialogIconRes));
    }

    public void setPositiveIcon(Drawable icon) {
        this.mHelper.setPositiveIcon(icon);
    }

    public Drawable getNeutralIcon() {
        return this.mHelper.getNeutralIcon();
    }

    public void setNeutralIcon(@DrawableRes int dialogIconRes) {
        this.mHelper.setNeutralIcon(getContext().getDrawable(dialogIconRes));
    }

    public void setNeutralIcon(Drawable icon) {
        this.mHelper.setNeutralIcon(icon);
    }

    public Drawable getNegativeIcon() {
        return this.mHelper.getNegativeIcon();
    }

    public void setNegativeIcon(@DrawableRes int dialogIconRes) {
        this.mHelper.setNegativeIcon(getContext().getDrawable(dialogIconRes));
    }

    public void setNegativeIcon(Drawable icon) {
        this.mHelper.setNegativeIcon(icon);
    }

    public void setNeutralButtonText(CharSequence neutralButtonText) {
        this.mNeutralButtonText = neutralButtonText;
    }

    public void setNeutralButtonText(@StringRes int neutralButtonTextResId) {
        setNeutralButtonText(getContext().getString(neutralButtonTextResId));
    }

    public CharSequence getNeutralButtonText() {
        return this.mNeutralButtonText;
    }
}
