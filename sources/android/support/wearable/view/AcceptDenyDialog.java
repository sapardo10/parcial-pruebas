package android.support.wearable.view;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.annotation.StyleRes;
import android.support.wearable.C0395R;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

@TargetApi(21)
public class AcceptDenyDialog extends Dialog {
    private final OnClickListener mButtonHandler;
    protected View mButtonPanel;
    protected ImageView mIcon;
    protected TextView mMessage;
    protected ImageButton mNegativeButton;
    protected DialogInterface.OnClickListener mNegativeButtonListener;
    protected ImageButton mPositiveButton;
    protected DialogInterface.OnClickListener mPositiveButtonListener;
    protected View mSpacer;
    protected TextView mTitle;

    /* renamed from: android.support.wearable.view.AcceptDenyDialog$1 */
    class C04261 implements OnClickListener {
        C04261() {
        }

        public void onClick(View v) {
            if (v == AcceptDenyDialog.this.mPositiveButton && AcceptDenyDialog.this.mPositiveButtonListener != null) {
                AcceptDenyDialog.this.mPositiveButtonListener.onClick(AcceptDenyDialog.this, -1);
            } else if (v == AcceptDenyDialog.this.mNegativeButton && AcceptDenyDialog.this.mNegativeButtonListener != null) {
                AcceptDenyDialog.this.mNegativeButtonListener.onClick(AcceptDenyDialog.this, -2);
            }
            AcceptDenyDialog.this.dismiss();
        }
    }

    public AcceptDenyDialog(Context context) {
        this(context, 0);
    }

    public AcceptDenyDialog(Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.mButtonHandler = new C04261();
        setContentView(C0395R.layout.accept_deny_dialog);
        this.mTitle = (TextView) findViewById(16908310);
        this.mMessage = (TextView) findViewById(16908299);
        this.mIcon = (ImageView) findViewById(16908294);
        this.mPositiveButton = (ImageButton) findViewById(16908313);
        this.mPositiveButton.setOnClickListener(this.mButtonHandler);
        this.mNegativeButton = (ImageButton) findViewById(16908314);
        this.mNegativeButton.setOnClickListener(this.mButtonHandler);
        this.mSpacer = findViewById(C0395R.id.spacer);
        this.mButtonPanel = findViewById(C0395R.id.buttonPanel);
    }

    public ImageButton getButton(int whichButton) {
        switch (whichButton) {
            case -2:
                return this.mNegativeButton;
            case -1:
                return this.mPositiveButton;
            default:
                return null;
        }
    }

    public void setIcon(Drawable icon) {
        this.mIcon.setVisibility(icon == null ? 8 : 0);
        this.mIcon.setImageDrawable(icon);
    }

    public void setIcon(int resId) {
        this.mIcon.setVisibility(resId == 0 ? 8 : 0);
        this.mIcon.setImageResource(resId);
    }

    public void setMessage(CharSequence message) {
        this.mMessage.setText(message);
        this.mMessage.setVisibility(message == null ? 8 : 0);
    }

    public void setTitle(CharSequence title) {
        this.mTitle.setText(title);
    }

    public void setButton(int whichButton, DialogInterface.OnClickListener listener) {
        int i;
        switch (whichButton) {
            case -2:
                this.mNegativeButtonListener = listener;
                break;
            case -1:
                this.mPositiveButtonListener = listener;
                break;
            default:
                return;
        }
        View view = this.mSpacer;
        int i2 = 8;
        if (this.mPositiveButtonListener != null) {
            if (this.mNegativeButtonListener != null) {
                i = 4;
                view.setVisibility(i);
                this.mPositiveButton.setVisibility(this.mPositiveButtonListener != null ? 8 : 0);
                this.mNegativeButton.setVisibility(this.mNegativeButtonListener != null ? 8 : 0);
                view = this.mButtonPanel;
                if (this.mPositiveButtonListener == null || this.mNegativeButtonListener != null) {
                    i2 = 0;
                }
                view.setVisibility(i2);
            }
        }
        i = 8;
        view.setVisibility(i);
        if (this.mPositiveButtonListener != null) {
        }
        this.mPositiveButton.setVisibility(this.mPositiveButtonListener != null ? 8 : 0);
        if (this.mNegativeButtonListener != null) {
        }
        this.mNegativeButton.setVisibility(this.mNegativeButtonListener != null ? 8 : 0);
        view = this.mButtonPanel;
        if (this.mPositiveButtonListener == null) {
        }
        i2 = 0;
        view.setVisibility(i2);
    }

    public void setPositiveButton(DialogInterface.OnClickListener listener) {
        setButton(-1, listener);
    }

    public void setNegativeButton(DialogInterface.OnClickListener listener) {
        setButton(-2, listener);
    }
}
