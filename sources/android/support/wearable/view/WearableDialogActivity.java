package android.support.wearable.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.annotation.CallSuper;
import android.support.wearable.C0395R;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.ObservableScrollView.OnScrollListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.TextView;

@TargetApi(21)
public class WearableDialogActivity extends WearableActivity implements Callback, OnLayoutChangeListener, OnScrollListener, OnClickListener, OnApplyWindowInsetsListener {
    private static final long ANIM_DURATION = 500;
    private static final long HIDE_ANIM_DELAY = 1500;
    private static final int MSG_HIDE_BUTTON_BAR = 1001;
    private ViewGroup mAnimatedWrapperContainer;
    private Button mButtonNegative;
    private Button mButtonNeutral;
    private ViewGroup mButtonPanel;
    private ObjectAnimator mButtonPanelAnimator;
    private float mButtonPanelFloatHeight;
    private int mButtonPanelShadeHeight;
    private Button mButtonPositive;
    private Handler mHandler;
    private boolean mHiddenBefore;
    private Interpolator mInterpolator;
    private boolean mIsLowBitAmbient;
    private TextView mMessageView;
    private ObservableScrollView mParentPanel;
    private TextView mTitleView;
    private PropertyValuesHolder mTranslationValuesHolder;

    /* renamed from: android.support.wearable.view.WearableDialogActivity$1 */
    class C04571 extends AnimatorListenerAdapter {
        C04571() {
        }

        public void onAnimationEnd(Animator animation) {
            WearableDialogActivity.this.mParentPanel.setOnScrollListener(null);
            WearableDialogActivity.this.mButtonPanel.setTranslationY(0.0f);
            WearableDialogActivity.this.mButtonPanel.setTranslationZ(0.0f);
        }
    }

    @CallSuper
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(C0395R.style.Theme_WearDiag);
        setContentView(C0395R.layout.alert_dialog_wearable);
        this.mAnimatedWrapperContainer = (ViewGroup) findViewById(C0395R.id.animatedWrapperContainer);
        this.mTitleView = (TextView) this.mAnimatedWrapperContainer.findViewById(C0395R.id.alertTitle);
        this.mMessageView = (TextView) this.mAnimatedWrapperContainer.findViewById(16908299);
        this.mButtonPanel = (ViewGroup) this.mAnimatedWrapperContainer.findViewById(C0395R.id.buttonPanel);
        this.mButtonPositive = (Button) this.mButtonPanel.findViewById(16908313);
        this.mButtonPositive.setOnClickListener(this);
        this.mButtonNegative = (Button) this.mButtonPanel.findViewById(16908314);
        this.mButtonNegative.setOnClickListener(this);
        this.mButtonNeutral = (Button) this.mButtonPanel.findViewById(16908315);
        this.mButtonNeutral.setOnClickListener(this);
        setupLayout();
        this.mHandler = new Handler(this);
        this.mInterpolator = AnimationUtils.loadInterpolator(this, AndroidResources.FAST_OUT_SLOW_IN);
        this.mButtonPanelFloatHeight = getResources().getDimension(C0395R.dimen.diag_floating_height);
        this.mParentPanel = (ObservableScrollView) findViewById(C0395R.id.parentPanel);
        this.mParentPanel.addOnLayoutChangeListener(this);
        this.mParentPanel.setOnScrollListener(this);
        this.mParentPanel.setOnApplyWindowInsetsListener(this);
    }

    public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
        Resources res = getResources();
        if (insets.isRound()) {
            this.mButtonPanelShadeHeight = res.getDimensionPixelSize(C0395R.dimen.diag_shade_height_round);
            this.mTitleView.setPadding(res.getDimensionPixelSize(C0395R.dimen.diag_content_side_padding_round), res.getDimensionPixelSize(C0395R.dimen.diag_content_top_padding_round), res.getDimensionPixelSize(C0395R.dimen.diag_content_side_padding_round), 0);
            this.mTitleView.setGravity(17);
            this.mMessageView.setPadding(res.getDimensionPixelSize(C0395R.dimen.diag_content_side_padding_round), 0, res.getDimensionPixelSize(C0395R.dimen.diag_content_side_padding_round), res.getDimensionPixelSize(C0395R.dimen.diag_content_bottom_padding));
            this.mMessageView.setGravity(17);
            this.mButtonPanel.setPadding(res.getDimensionPixelSize(C0395R.dimen.diag_content_side_padding_round), 0, res.getDimensionPixelSize(C0395R.dimen.diag_button_side_padding_right_round), res.getDimensionPixelSize(C0395R.dimen.diag_button_bottom_padding_round));
        } else {
            this.mButtonPanelShadeHeight = getResources().getDimensionPixelSize(C0395R.dimen.diag_shade_height_rect);
        }
        return v.onApplyWindowInsets(insets);
    }

    protected void setupLayout() {
        boolean z;
        ViewGroup viewGroup;
        CharSequence title = getAlertTitle();
        int i = 8;
        if (TextUtils.isEmpty(title)) {
            this.mTitleView.setVisibility(8);
        } else {
            this.mMessageView.setVisibility(0);
            this.mTitleView.setText(title);
        }
        CharSequence message = getMessage();
        if (TextUtils.isEmpty(message)) {
            this.mMessageView.setVisibility(8);
        } else {
            this.mMessageView.setVisibility(0);
            this.mMessageView.setText(message);
        }
        boolean hasButtons = setButton(this.mButtonPositive, getPositiveButtonText(), getPositiveButtonDrawable());
        boolean z2 = true;
        if (!setButton(this.mButtonNegative, getNegativeButtonText(), getNegativeButtonDrawable())) {
            if (!hasButtons) {
                z = false;
                hasButtons = z;
                if (!setButton(this.mButtonNeutral, getNeutralButtonText(), getNeutralButtonDrawable())) {
                    if (hasButtons) {
                        z2 = false;
                    }
                }
                hasButtons = z2;
                viewGroup = this.mButtonPanel;
                if (hasButtons) {
                    i = 0;
                }
                viewGroup.setVisibility(i);
            }
        }
        z = true;
        hasButtons = z;
        if (setButton(this.mButtonNeutral, getNeutralButtonText(), getNeutralButtonDrawable())) {
            if (hasButtons) {
                z2 = false;
            }
        }
        hasButtons = z2;
        viewGroup = this.mButtonPanel;
        if (hasButtons) {
            i = 0;
        }
        viewGroup.setVisibility(i);
    }

    private boolean setButton(Button button, CharSequence text, Drawable drawable) {
        if (TextUtils.isEmpty(text)) {
            button.setVisibility(8);
            return false;
        }
        button.setText(text);
        if (drawable != null) {
            button.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }
        button.setVisibility(0);
        return true;
    }

    public CharSequence getAlertTitle() {
        return null;
    }

    public CharSequence getMessage() {
        return null;
    }

    public CharSequence getPositiveButtonText() {
        return null;
    }

    public Drawable getPositiveButtonDrawable() {
        return null;
    }

    public CharSequence getNegativeButtonText() {
        return null;
    }

    public Drawable getNegativeButtonDrawable() {
        return null;
    }

    public CharSequence getNeutralButtonText() {
        return null;
    }

    public Drawable getNeutralButtonDrawable() {
        return null;
    }

    public void onPositiveButtonClick() {
        finish();
    }

    public void onNeutralButtonClick() {
        finish();
    }

    public void onNegativeButtonClick() {
        finish();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case 16908313:
                onPositiveButtonClick();
                return;
            case 16908314:
                onNegativeButtonClick();
                return;
            case 16908315:
                onNeutralButtonClick();
                return;
            default:
                return;
        }
    }

    public void onScroll(float deltaY) {
        this.mHandler.removeMessages(1001);
        hideButtonBar();
    }

    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        ObjectAnimator objectAnimator = this.mButtonPanelAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        r0.mHandler.removeMessages(1001);
        r0.mHiddenBefore = false;
        if (r0.mAnimatedWrapperContainer.getHeight() > r0.mParentPanel.getHeight()) {
            r0.mButtonPanel.setTranslationZ(r0.mButtonPanelFloatHeight);
            r0.mHandler.sendEmptyMessageDelayed(1001, HIDE_ANIM_DELAY);
            ViewGroup viewGroup = r0.mButtonPanel;
            PropertyValuesHolder[] propertyValuesHolderArr = new PropertyValuesHolder[2];
            propertyValuesHolderArr[0] = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, new float[]{(float) getButtonBarFloatingBottomTranslation(), (float) getButtonBarFloatingTopTranslation()});
            propertyValuesHolderArr[1] = PropertyValuesHolder.ofFloat(View.TRANSLATION_Z, new float[]{0.0f, r0.mButtonPanelFloatHeight});
            r0.mButtonPanelAnimator = ObjectAnimator.ofPropertyValuesHolder(viewGroup, propertyValuesHolderArr);
            r0.mButtonPanelAnimator.setDuration(ANIM_DURATION);
            r0.mButtonPanelAnimator.setInterpolator(r0.mInterpolator);
            r0.mButtonPanelAnimator.start();
            return;
        }
        r0.mButtonPanel.setTranslationY(0.0f);
        r0.mButtonPanel.setTranslationZ(0.0f);
        r0.mButtonPanel.offsetTopAndBottom(r0.mParentPanel.getHeight() - r0.mAnimatedWrapperContainer.getHeight());
        r0.mAnimatedWrapperContainer.setBottom(r0.mParentPanel.getHeight());
    }

    public boolean handleMessage(Message msg) {
        if (msg.what != 1001) {
            return false;
        }
        hideButtonBar();
        return true;
    }

    private int getButtonBarFloatingTopTranslation() {
        return getButtonBarOffsetFromBottom() - Math.min(this.mButtonPanel.getHeight(), this.mButtonPanelShadeHeight);
    }

    private int getButtonBarFloatingBottomTranslation() {
        return Math.min(getButtonBarOffsetFromBottom(), 0);
    }

    private int getButtonBarOffsetFromBottom() {
        return ((-this.mButtonPanel.getTop()) + Math.max(this.mParentPanel.getScrollY(), 0)) + this.mParentPanel.getHeight();
    }

    private void hideButtonBar() {
        ObjectAnimator objectAnimator;
        if (this.mHiddenBefore) {
            objectAnimator = this.mButtonPanelAnimator;
            if (objectAnimator != null) {
                if (objectAnimator.isRunning()) {
                    int start = getButtonBarFloatingTopTranslation();
                    if (start < getButtonBarFloatingBottomTranslation()) {
                        this.mTranslationValuesHolder.setFloatValues(new float[]{(float) start, (float) end});
                        if (this.mButtonPanel.getTranslationY() < ((float) start)) {
                            this.mButtonPanel.setTranslationY((float) start);
                        }
                    } else {
                        this.mButtonPanelAnimator.cancel();
                        this.mButtonPanel.setTranslationY(0.0f);
                        this.mButtonPanel.setTranslationZ(0.0f);
                    }
                } else {
                    this.mButtonPanel.setTranslationY(0.0f);
                    this.mButtonPanel.setTranslationZ(0.0f);
                }
                this.mHiddenBefore = true;
            }
        }
        objectAnimator = this.mButtonPanelAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        this.mTranslationValuesHolder = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, new float[]{(float) getButtonBarFloatingTopTranslation(), (float) getButtonBarFloatingBottomTranslation()});
        ViewGroup viewGroup = this.mButtonPanel;
        PropertyValuesHolder[] propertyValuesHolderArr = new PropertyValuesHolder[2];
        propertyValuesHolderArr[0] = this.mTranslationValuesHolder;
        propertyValuesHolderArr[1] = PropertyValuesHolder.ofFloat(View.TRANSLATION_Z, new float[]{this.mButtonPanelFloatHeight, 0.0f});
        this.mButtonPanelAnimator = ObjectAnimator.ofPropertyValuesHolder(viewGroup, propertyValuesHolderArr);
        this.mButtonPanelAnimator.addListener(new C04571());
        this.mButtonPanelAnimator.setDuration(ANIM_DURATION);
        this.mButtonPanelAnimator.setInterpolator(this.mInterpolator);
        this.mButtonPanelAnimator.start();
        this.mHiddenBefore = true;
    }

    @CallSuper
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        this.mIsLowBitAmbient = ambientDetails.getBoolean(WearableActivity.EXTRA_LOWBIT_AMBIENT);
        this.mButtonPanel.setVisibility(8);
        if (this.mIsLowBitAmbient) {
            setAntiAlias(this.mTitleView, false);
            setAntiAlias(this.mMessageView, false);
        }
    }

    @CallSuper
    public void onExitAmbient() {
        super.onExitAmbient();
        this.mButtonPanel.setVisibility(0);
        if (this.mIsLowBitAmbient) {
            setAntiAlias(this.mTitleView, true);
            setAntiAlias(this.mMessageView, true);
        }
    }

    private void setAntiAlias(TextView textView, boolean antiAlias) {
        textView.getPaint().setAntiAlias(antiAlias);
        textView.invalidate();
    }
}
