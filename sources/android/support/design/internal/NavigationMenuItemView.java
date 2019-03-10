package android.support.design.internal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.design.C0035R;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.appcompat.C0286R;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuView.ItemView;
import android.support.v7.widget.LinearLayoutCompat.LayoutParams;
import android.support.v7.widget.TooltipCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;

@RestrictTo({Scope.LIBRARY_GROUP})
public class NavigationMenuItemView extends ForegroundLinearLayout implements ItemView {
    private static final int[] CHECKED_STATE_SET = new int[]{16842912};
    private final AccessibilityDelegateCompat mAccessibilityDelegate;
    private FrameLayout mActionArea;
    boolean mCheckable;
    private Drawable mEmptyDrawable;
    private boolean mHasIconTintList;
    private final int mIconSize;
    private ColorStateList mIconTintList;
    private MenuItemImpl mItemData;
    private boolean mNeedsEmptyIcon;
    private final CheckedTextView mTextView;

    /* renamed from: android.support.design.internal.NavigationMenuItemView$1 */
    class C08061 extends AccessibilityDelegateCompat {
        C08061() {
        }

        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            info.setCheckable(NavigationMenuItemView.this.mCheckable);
        }
    }

    public NavigationMenuItemView(Context context) {
        this(context, null);
    }

    public NavigationMenuItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationMenuItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mAccessibilityDelegate = new C08061();
        setOrientation(0);
        LayoutInflater.from(context).inflate(C0035R.layout.design_navigation_menu_item, this, true);
        this.mIconSize = context.getResources().getDimensionPixelSize(C0035R.dimen.design_navigation_icon_size);
        this.mTextView = (CheckedTextView) findViewById(C0035R.id.design_menu_item_text);
        this.mTextView.setDuplicateParentStateEnabled(true);
        ViewCompat.setAccessibilityDelegate(this.mTextView, this.mAccessibilityDelegate);
    }

    public void initialize(MenuItemImpl itemData, int menuType) {
        this.mItemData = itemData;
        setVisibility(itemData.isVisible() ? 0 : 8);
        if (getBackground() == null) {
            ViewCompat.setBackground(this, createDefaultBackground());
        }
        setCheckable(itemData.isCheckable());
        setChecked(itemData.isChecked());
        setEnabled(itemData.isEnabled());
        setTitle(itemData.getTitle());
        setIcon(itemData.getIcon());
        setActionView(itemData.getActionView());
        setContentDescription(itemData.getContentDescription());
        TooltipCompat.setTooltipText(this, itemData.getTooltipText());
        adjustAppearance();
    }

    private boolean shouldExpandActionArea() {
        if (this.mItemData.getTitle() == null) {
            if (this.mItemData.getIcon() == null) {
                if (this.mItemData.getActionView() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private void adjustAppearance() {
        FrameLayout frameLayout;
        if (shouldExpandActionArea()) {
            this.mTextView.setVisibility(8);
            frameLayout = this.mActionArea;
            if (frameLayout != null) {
                LayoutParams params = (LayoutParams) frameLayout.getLayoutParams();
                params.width = -1;
                this.mActionArea.setLayoutParams(params);
                return;
            }
            return;
        }
        this.mTextView.setVisibility(0);
        frameLayout = this.mActionArea;
        if (frameLayout != null) {
            params = (LayoutParams) frameLayout.getLayoutParams();
            params.width = -2;
            this.mActionArea.setLayoutParams(params);
        }
    }

    public void recycle() {
        FrameLayout frameLayout = this.mActionArea;
        if (frameLayout != null) {
            frameLayout.removeAllViews();
        }
        this.mTextView.setCompoundDrawables(null, null, null, null);
    }

    private void setActionView(View actionView) {
        if (actionView != null) {
            if (this.mActionArea == null) {
                this.mActionArea = (FrameLayout) ((ViewStub) findViewById(C0035R.id.design_menu_item_action_area_stub)).inflate();
            }
            this.mActionArea.removeAllViews();
            this.mActionArea.addView(actionView);
        }
    }

    private StateListDrawable createDefaultBackground() {
        TypedValue value = new TypedValue();
        if (!getContext().getTheme().resolveAttribute(C0286R.attr.colorControlHighlight, value, true)) {
            return null;
        }
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(CHECKED_STATE_SET, new ColorDrawable(value.data));
        drawable.addState(EMPTY_STATE_SET, new ColorDrawable(0));
        return drawable;
    }

    public MenuItemImpl getItemData() {
        return this.mItemData;
    }

    public void setTitle(CharSequence title) {
        this.mTextView.setText(title);
    }

    public void setCheckable(boolean checkable) {
        refreshDrawableState();
        if (this.mCheckable != checkable) {
            this.mCheckable = checkable;
            this.mAccessibilityDelegate.sendAccessibilityEvent(this.mTextView, 2048);
        }
    }

    public void setChecked(boolean checked) {
        refreshDrawableState();
        this.mTextView.setChecked(checked);
    }

    public void setShortcut(boolean showShortcut, char shortcutKey) {
    }

    public void setIcon(Drawable icon) {
        if (icon != null) {
            if (this.mHasIconTintList) {
                ConstantState state = icon.getConstantState();
                icon = DrawableCompat.wrap(state == null ? icon : state.newDrawable()).mutate();
                DrawableCompat.setTintList(icon, this.mIconTintList);
            }
            int i = this.mIconSize;
            icon.setBounds(0, 0, i, i);
        } else if (this.mNeedsEmptyIcon) {
            if (this.mEmptyDrawable == null) {
                this.mEmptyDrawable = ResourcesCompat.getDrawable(getResources(), C0035R.drawable.navigation_empty_icon, getContext().getTheme());
                Drawable drawable = this.mEmptyDrawable;
                if (drawable != null) {
                    int i2 = this.mIconSize;
                    drawable.setBounds(0, 0, i2, i2);
                }
            }
            icon = this.mEmptyDrawable;
        }
        TextViewCompat.setCompoundDrawablesRelative(this.mTextView, icon, null, null, null);
    }

    public boolean prefersCondensedTitle() {
        return false;
    }

    public boolean showsIcon() {
        return true;
    }

    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        MenuItemImpl menuItemImpl = this.mItemData;
        if (menuItemImpl != null && menuItemImpl.isCheckable() && this.mItemData.isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    void setIconTintList(ColorStateList tintList) {
        this.mIconTintList = tintList;
        this.mHasIconTintList = this.mIconTintList != null;
        MenuItemImpl menuItemImpl = this.mItemData;
        if (menuItemImpl != null) {
            setIcon(menuItemImpl.getIcon());
        }
    }

    public void setTextAppearance(int textAppearance) {
        TextViewCompat.setTextAppearance(this.mTextView, textAppearance);
    }

    public void setTextColor(ColorStateList colors) {
        this.mTextView.setTextColor(colors);
    }

    public void setNeedsEmptyIcon(boolean needsEmptyIcon) {
        this.mNeedsEmptyIcon = needsEmptyIcon;
    }
}
