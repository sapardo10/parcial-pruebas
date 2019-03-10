package android.support.wearable.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.support.wearable.C0395R;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowInsets;
import android.widget.FrameLayout;

@TargetApi(20)
@Deprecated
public class WatchViewStub extends FrameLayout {
    private static final String TAG = "WatchViewStub";
    private boolean mInflateNeeded;
    private boolean mLastKnownRound;
    private OnLayoutInflatedListener mListener;
    private int mRectLayout;
    private int mRoundLayout;
    private boolean mWindowInsetsApplied;
    private boolean mWindowOverscan;

    public interface OnLayoutInflatedListener {
        void onLayoutInflated(WatchViewStub watchViewStub);
    }

    public WatchViewStub(Context context) {
        this(context, null);
    }

    public WatchViewStub(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WatchViewStub(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, C0395R.styleable.WatchViewStub, 0, 0);
        this.mRectLayout = a.getResourceId(C0395R.styleable.WatchViewStub_rectLayout, 0);
        this.mRoundLayout = a.getResourceId(C0395R.styleable.WatchViewStub_roundLayout, 0);
        this.mInflateNeeded = true;
        a.recycle();
    }

    public void setOnLayoutInflatedListener(OnLayoutInflatedListener listener) {
        this.mListener = listener;
    }

    public void setRectLayout(@LayoutRes int resId) {
        if (this.mInflateNeeded) {
            this.mRectLayout = resId;
        } else {
            Log.w(TAG, "Views have already been inflated. setRectLayout will have no effect.");
        }
    }

    public void setRoundLayout(@LayoutRes int resId) {
        if (this.mInflateNeeded) {
            this.mRoundLayout = resId;
        } else {
            Log.w(TAG, "Views have already been inflated. setRoundLayout will have no effect.");
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mWindowOverscan = Func.getWindowOverscan(this);
        this.mWindowInsetsApplied = false;
        requestApplyInsets();
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mInflateNeeded && !this.mWindowOverscan) {
            inflate();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        this.mWindowInsetsApplied = true;
        boolean round = insets.isRound();
        if (round != this.mLastKnownRound) {
            this.mLastKnownRound = round;
            this.mInflateNeeded = true;
        }
        if (this.mInflateNeeded) {
            inflate();
        }
        return insets;
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.mWindowOverscan && !this.mWindowInsetsApplied) {
            Log.w(TAG, "onApplyWindowInsets was not called. WatchViewStub should be the the root of your layout. If an OnApplyWindowInsetsListener was attached to this view, it must forward the insets on by calling view.onApplyWindowInsets.");
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    private void inflate() {
        removeAllViews();
        if (this.mRoundLayout == 0) {
            if (!isInEditMode()) {
                throw new IllegalStateException("You must supply a roundLayout resource");
            }
        }
        if (this.mRectLayout == 0) {
            if (!isInEditMode()) {
                throw new IllegalStateException("You must supply a rectLayout resource");
            }
        }
        LayoutInflater.from(getContext()).inflate(this.mLastKnownRound ? this.mRoundLayout : this.mRectLayout, this);
        this.mInflateNeeded = false;
        OnLayoutInflatedListener onLayoutInflatedListener = this.mListener;
        if (onLayoutInflatedListener != null) {
            onLayoutInflatedListener.onLayoutInflated(this);
        }
    }
}
