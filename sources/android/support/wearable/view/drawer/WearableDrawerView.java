package android.support.wearable.view.drawer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.wearable.C0395R;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

@TargetApi(23)
@Deprecated
public class WearableDrawerView extends FrameLayout {
    private static final int BACKGROUND_COLOR_INDEX = 0;
    private static final int[] COLOR_ATTRS = new int[]{16844002};
    private boolean mCanAutoPeek;
    private View mContent;
    @IdRes
    private int mContentResId;
    @WearableDrawerView$DrawerState
    private int mDrawerState;
    private boolean mIsLocked;
    private boolean mIsPeeking;
    private boolean mOnlyOpenWhenAtTop;
    private float mOpenedPercent;
    private WearableDrawerLayout mParent;
    private final ViewGroup mPeekContainer;
    private final ImageView mPeekIcon;
    @IdRes
    private int mPeekResId;
    private boolean mShouldLockWhenNotOpenOrPeeking;
    private boolean mShouldPeekOnScrollDown;

    /* renamed from: android.support.wearable.view.drawer.WearableDrawerView$1 */
    class C04681 implements OnClickListener {
        C04681() {
        }

        public void onClick(View v) {
            WearableDrawerView.this.onPeekContainerClicked(v);
        }
    }

    public WearableDrawerView(Context context) {
        this(context, null);
    }

    public WearableDrawerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WearableDrawerView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public WearableDrawerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mIsLocked = false;
        this.mCanAutoPeek = true;
        this.mShouldLockWhenNotOpenOrPeeking = false;
        this.mOnlyOpenWhenAtTop = false;
        this.mShouldPeekOnScrollDown = false;
        this.mPeekResId = 0;
        this.mContentResId = 0;
        LayoutInflater.from(context).inflate(C0395R.layout.wearable_drawer_view, this, true);
        setClickable(true);
        setElevation(context.getResources().getDimension(C0395R.dimen.wearable_drawer_view_elevation));
        this.mPeekContainer = (ViewGroup) findViewById(C0395R.id.wearable_support_drawer_view_peek_container);
        this.mPeekIcon = (ImageView) findViewById(C0395R.id.wearable_support_drawer_view_peek_icon);
        this.mPeekContainer.setOnClickListener(new C04681());
        parseAttributes(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mPeekContainer.bringToFront();
    }

    public void onPeekContainerClicked(View v) {
        openDrawer();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        LayoutParams peekParams = (LayoutParams) this.mPeekContainer.getLayoutParams();
        if (!Gravity.isVertical(peekParams.gravity)) {
            if ((((LayoutParams) getLayoutParams()).gravity & 112) == 48) {
                peekParams.gravity = 80;
                this.mPeekIcon.setImageResource(C0395R.drawable.ic_more_horiz_24dp_wht);
            } else {
                peekParams.gravity = 48;
                this.mPeekIcon.setImageResource(C0395R.drawable.ic_more_vert_24dp_wht);
            }
            this.mPeekContainer.setLayoutParams(peekParams);
        }
    }

    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        int childId = child.getId();
        if (childId != 0) {
            if (childId == this.mPeekResId) {
                setPeekContent(child, index, params);
                return;
            } else if (childId == this.mContentResId && !setDrawerContentWithoutAdding(child)) {
                return;
            }
        }
        super.addView(child, index, params);
    }

    int preferGravity() {
        return 0;
    }

    ViewGroup getPeekContainer() {
        return this.mPeekContainer;
    }

    public void setDrawerContent(@Nullable View content) {
        if (setDrawerContentWithoutAdding(content)) {
            addView(content);
        }
    }

    public boolean hasDrawerContent() {
        return this.mContent != null;
    }

    @Nullable
    public View getDrawerContent() {
        return this.mContent;
    }

    public void setPeekContent(View content) {
        ViewGroup.LayoutParams layoutParams = content.getLayoutParams();
        setPeekContent(content, -1, layoutParams != null ? layoutParams : generateDefaultLayoutParams());
    }

    public void openDrawer() {
        getWearableDrawerLayout().openDrawer((View) this);
    }

    public void closeDrawer() {
        getWearableDrawerLayout().closeDrawer((View) this);
    }

    public void peekDrawer() {
        getWearableDrawerLayout().peekDrawer(this);
    }

    public void onDrawerOpened() {
    }

    public void onDrawerClosed() {
    }

    public void onDrawerStateChanged(@WearableDrawerView$DrawerState int state) {
    }

    public void setShouldOnlyOpenWhenAtTop(boolean onlyOpenWhenAtTop) {
        this.mOnlyOpenWhenAtTop = onlyOpenWhenAtTop;
    }

    public boolean shouldOnlyOpenWhenAtTop() {
        return this.mOnlyOpenWhenAtTop;
    }

    public void setShouldPeekOnScrollDown(boolean shouldPeekOnScrollDown) {
        this.mShouldPeekOnScrollDown = shouldPeekOnScrollDown;
    }

    public boolean shouldPeekOnScrollDown() {
        return this.mShouldPeekOnScrollDown;
    }

    public void setShouldLockWhenNotOpenOrPeeking(boolean shouldLockWhenNotOpenOrPeeking) {
        this.mShouldLockWhenNotOpenOrPeeking = shouldLockWhenNotOpenOrPeeking;
    }

    public boolean shouldLockWhenNotOpenOrPeeking() {
        return this.mShouldLockWhenNotOpenOrPeeking;
    }

    @WearableDrawerView$DrawerState
    public int getDrawerState() {
        return this.mDrawerState;
    }

    public boolean isPeeking() {
        return this.mIsPeeking;
    }

    public boolean canAutoPeek() {
        return this.mCanAutoPeek && !this.mIsLocked;
    }

    public void setCanAutoPeek(boolean canAutoPeek) {
        this.mCanAutoPeek = canAutoPeek;
    }

    public boolean isLocked() {
        if (!this.mIsLocked) {
            if (!shouldLockWhenNotOpenOrPeeking() || this.mOpenedPercent > 0.0f) {
                return false;
            }
        }
        return true;
    }

    public void lockDrawerClosed() {
        closeDrawer();
        this.mIsLocked = true;
    }

    public void lockDrawerOpened() {
        openDrawer();
        this.mIsLocked = true;
    }

    public void unlockDrawer() {
        this.mIsLocked = false;
    }

    public boolean isOpened() {
        return this.mOpenedPercent == 1.0f;
    }

    public boolean isClosed() {
        return this.mOpenedPercent == 0.0f;
    }

    void setDrawerState(@WearableDrawerView$DrawerState int drawerState) {
        this.mDrawerState = drawerState;
    }

    void setIsPeeking(boolean isPeeking) {
        this.mIsPeeking = isPeeking;
    }

    float getOpenedPercent() {
        return this.mOpenedPercent;
    }

    void setOpenedPercent(float openedPercent) {
        this.mOpenedPercent = openedPercent;
    }

    private void parseAttributes(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setDefaultBackgroundIfNonePresent(context);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, C0395R.styleable.WearableDrawerView, defStyleAttr, defStyleRes);
            this.mContentResId = typedArray.getResourceId(C0395R.styleable.WearableDrawerView_drawer_content, 0);
            this.mPeekResId = typedArray.getResourceId(C0395R.styleable.WearableDrawerView_peek_view, 0);
            typedArray.recycle();
        }
    }

    private void setDefaultBackgroundIfNonePresent(Context context) {
        if (getBackground() == null) {
            setBackgroundColor(getDefaultBackgroundColor(context));
        }
    }

    private int getDefaultBackgroundColor(Context context) {
        return context.obtainStyledAttributes(COLOR_ATTRS).getColor(0, 0);
    }

    private void setPeekContent(View content, int index, ViewGroup.LayoutParams params) {
        if (content != null) {
            if (this.mPeekContainer.getChildCount() > 0) {
                this.mPeekContainer.removeAllViews();
            }
            this.mPeekContainer.addView(content, index, params);
        }
    }

    private boolean setDrawerContentWithoutAdding(View content) {
        View view = this.mContent;
        boolean z = false;
        if (content == view) {
            return false;
        }
        if (view != null) {
            removeView(view);
        }
        this.mContent = content;
        if (this.mContent != null) {
            z = true;
        }
        return z;
    }

    private WearableDrawerLayout getWearableDrawerLayout() {
        if (this.mParent == null) {
            this.mParent = (WearableDrawerLayout) getParent();
        }
        return this.mParent;
    }
}
