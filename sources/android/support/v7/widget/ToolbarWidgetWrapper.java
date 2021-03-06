package android.support.v7.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v7.appcompat.C0286R;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.menu.ActionMenuItem;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPresenter;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.Window.Callback;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

@RestrictTo({Scope.LIBRARY_GROUP})
public class ToolbarWidgetWrapper implements DecorToolbar {
    private static final int AFFECTS_LOGO_MASK = 3;
    private static final long DEFAULT_FADE_DURATION_MS = 200;
    private static final String TAG = "ToolbarWidgetWrapper";
    private ActionMenuPresenter mActionMenuPresenter;
    private View mCustomView;
    private int mDefaultNavigationContentDescription;
    private Drawable mDefaultNavigationIcon;
    private int mDisplayOpts;
    private CharSequence mHomeDescription;
    private Drawable mIcon;
    private Drawable mLogo;
    boolean mMenuPrepared;
    private Drawable mNavIcon;
    private int mNavigationMode;
    private Spinner mSpinner;
    private CharSequence mSubtitle;
    private View mTabView;
    CharSequence mTitle;
    private boolean mTitleSet;
    Toolbar mToolbar;
    Callback mWindowCallback;

    /* renamed from: android.support.v7.widget.ToolbarWidgetWrapper$1 */
    class C03871 implements OnClickListener {
        final ActionMenuItem mNavItem = new ActionMenuItem(ToolbarWidgetWrapper.this.mToolbar.getContext(), 0, 16908332, 0, 0, ToolbarWidgetWrapper.this.mTitle);

        C03871() {
        }

        public void onClick(View v) {
            if (ToolbarWidgetWrapper.this.mWindowCallback != null && ToolbarWidgetWrapper.this.mMenuPrepared) {
                ToolbarWidgetWrapper.this.mWindowCallback.onMenuItemSelected(0, this.mNavItem);
            }
        }
    }

    public ToolbarWidgetWrapper(Toolbar toolbar, boolean style) {
        this(toolbar, style, C0286R.string.abc_action_bar_up_description, C0286R.drawable.abc_ic_ab_back_material);
    }

    public ToolbarWidgetWrapper(Toolbar toolbar, boolean style, int defaultNavigationContentDescription, int defaultNavigationIcon) {
        this.mNavigationMode = 0;
        this.mDefaultNavigationContentDescription = 0;
        this.mToolbar = toolbar;
        this.mTitle = toolbar.getTitle();
        this.mSubtitle = toolbar.getSubtitle();
        r0.mTitleSet = this.mTitle != null;
        r0.mNavIcon = toolbar.getNavigationIcon();
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(toolbar.getContext(), null, C0286R.styleable.ActionBar, C0286R.attr.actionBarStyle, 0);
        r0.mDefaultNavigationIcon = a.getDrawable(C0286R.styleable.ActionBar_homeAsUpIndicator);
        if (style) {
            int customNavId;
            int height;
            LayoutParams lp;
            int contentInsetStart;
            int contentInsetEnd;
            int titleTextStyle;
            Toolbar toolbar2;
            int subtitleTextStyle;
            Toolbar toolbar3;
            int popupTheme;
            CharSequence title = a.getText(C0286R.styleable.ActionBar_title);
            if (!TextUtils.isEmpty(title)) {
                setTitle(title);
            }
            CharSequence subtitle = a.getText(C0286R.styleable.ActionBar_subtitle);
            if (!TextUtils.isEmpty(subtitle)) {
                setSubtitle(subtitle);
            }
            Drawable logo = a.getDrawable(C0286R.styleable.ActionBar_logo);
            if (logo != null) {
                setLogo(logo);
            }
            Drawable icon = a.getDrawable(C0286R.styleable.ActionBar_icon);
            if (icon != null) {
                setIcon(icon);
            }
            if (r0.mNavIcon == null) {
                Drawable drawable = r0.mDefaultNavigationIcon;
                if (drawable != null) {
                    setNavigationIcon(drawable);
                    setDisplayOptions(a.getInt(C0286R.styleable.ActionBar_displayOptions, 0));
                    customNavId = a.getResourceId(C0286R.styleable.ActionBar_customNavigationLayout, 0);
                    if (customNavId != 0) {
                        setCustomView(LayoutInflater.from(r0.mToolbar.getContext()).inflate(customNavId, r0.mToolbar, false));
                        setDisplayOptions(r0.mDisplayOpts | 16);
                    }
                    height = a.getLayoutDimension(C0286R.styleable.ActionBar_height, 0);
                    if (height > 0) {
                        lp = r0.mToolbar.getLayoutParams();
                        lp.height = height;
                        r0.mToolbar.setLayoutParams(lp);
                    }
                    contentInsetStart = a.getDimensionPixelOffset(C0286R.styleable.ActionBar_contentInsetStart, -1);
                    contentInsetEnd = a.getDimensionPixelOffset(C0286R.styleable.ActionBar_contentInsetEnd, -1);
                    if (contentInsetStart < 0) {
                        if (contentInsetEnd >= 0) {
                            titleTextStyle = a.getResourceId(C0286R.styleable.ActionBar_titleTextStyle, 0);
                            if (titleTextStyle == 0) {
                                toolbar2 = r0.mToolbar;
                                toolbar2.setTitleTextAppearance(toolbar2.getContext(), titleTextStyle);
                            }
                            subtitleTextStyle = a.getResourceId(C0286R.styleable.ActionBar_subtitleTextStyle, 0);
                            if (subtitleTextStyle == 0) {
                                toolbar3 = r0.mToolbar;
                                toolbar3.setSubtitleTextAppearance(toolbar3.getContext(), subtitleTextStyle);
                            }
                            popupTheme = a.getResourceId(C0286R.styleable.ActionBar_popupTheme, 0);
                            if (popupTheme == 0) {
                                r0.mToolbar.setPopupTheme(popupTheme);
                            }
                        }
                    }
                    r0.mToolbar.setContentInsetsRelative(Math.max(contentInsetStart, 0), Math.max(contentInsetEnd, 0));
                    titleTextStyle = a.getResourceId(C0286R.styleable.ActionBar_titleTextStyle, 0);
                    if (titleTextStyle == 0) {
                        toolbar2 = r0.mToolbar;
                        toolbar2.setTitleTextAppearance(toolbar2.getContext(), titleTextStyle);
                    }
                    subtitleTextStyle = a.getResourceId(C0286R.styleable.ActionBar_subtitleTextStyle, 0);
                    if (subtitleTextStyle == 0) {
                        toolbar3 = r0.mToolbar;
                        toolbar3.setSubtitleTextAppearance(toolbar3.getContext(), subtitleTextStyle);
                    }
                    popupTheme = a.getResourceId(C0286R.styleable.ActionBar_popupTheme, 0);
                    if (popupTheme == 0) {
                        r0.mToolbar.setPopupTheme(popupTheme);
                    }
                }
            }
            setDisplayOptions(a.getInt(C0286R.styleable.ActionBar_displayOptions, 0));
            customNavId = a.getResourceId(C0286R.styleable.ActionBar_customNavigationLayout, 0);
            if (customNavId != 0) {
                setCustomView(LayoutInflater.from(r0.mToolbar.getContext()).inflate(customNavId, r0.mToolbar, false));
                setDisplayOptions(r0.mDisplayOpts | 16);
            }
            height = a.getLayoutDimension(C0286R.styleable.ActionBar_height, 0);
            if (height > 0) {
                lp = r0.mToolbar.getLayoutParams();
                lp.height = height;
                r0.mToolbar.setLayoutParams(lp);
            }
            contentInsetStart = a.getDimensionPixelOffset(C0286R.styleable.ActionBar_contentInsetStart, -1);
            contentInsetEnd = a.getDimensionPixelOffset(C0286R.styleable.ActionBar_contentInsetEnd, -1);
            if (contentInsetStart < 0) {
                if (contentInsetEnd >= 0) {
                    titleTextStyle = a.getResourceId(C0286R.styleable.ActionBar_titleTextStyle, 0);
                    if (titleTextStyle == 0) {
                        toolbar2 = r0.mToolbar;
                        toolbar2.setTitleTextAppearance(toolbar2.getContext(), titleTextStyle);
                    }
                    subtitleTextStyle = a.getResourceId(C0286R.styleable.ActionBar_subtitleTextStyle, 0);
                    if (subtitleTextStyle == 0) {
                        toolbar3 = r0.mToolbar;
                        toolbar3.setSubtitleTextAppearance(toolbar3.getContext(), subtitleTextStyle);
                    }
                    popupTheme = a.getResourceId(C0286R.styleable.ActionBar_popupTheme, 0);
                    if (popupTheme == 0) {
                        r0.mToolbar.setPopupTheme(popupTheme);
                    }
                }
            }
            r0.mToolbar.setContentInsetsRelative(Math.max(contentInsetStart, 0), Math.max(contentInsetEnd, 0));
            titleTextStyle = a.getResourceId(C0286R.styleable.ActionBar_titleTextStyle, 0);
            if (titleTextStyle == 0) {
                toolbar2 = r0.mToolbar;
                toolbar2.setTitleTextAppearance(toolbar2.getContext(), titleTextStyle);
            }
            subtitleTextStyle = a.getResourceId(C0286R.styleable.ActionBar_subtitleTextStyle, 0);
            if (subtitleTextStyle == 0) {
                toolbar3 = r0.mToolbar;
                toolbar3.setSubtitleTextAppearance(toolbar3.getContext(), subtitleTextStyle);
            }
            popupTheme = a.getResourceId(C0286R.styleable.ActionBar_popupTheme, 0);
            if (popupTheme == 0) {
                r0.mToolbar.setPopupTheme(popupTheme);
            }
        } else {
            r0.mDisplayOpts = detectDisplayOptions();
        }
        a.recycle();
        setDefaultNavigationContentDescription(defaultNavigationContentDescription);
        r0.mHomeDescription = r0.mToolbar.getNavigationContentDescription();
        r0.mToolbar.setNavigationOnClickListener(new C03871());
    }

    public void setDefaultNavigationContentDescription(int defaultNavigationContentDescription) {
        if (defaultNavigationContentDescription != this.mDefaultNavigationContentDescription) {
            this.mDefaultNavigationContentDescription = defaultNavigationContentDescription;
            if (TextUtils.isEmpty(this.mToolbar.getNavigationContentDescription())) {
                setNavigationContentDescription(this.mDefaultNavigationContentDescription);
            }
        }
    }

    private int detectDisplayOptions() {
        if (this.mToolbar.getNavigationIcon() == null) {
            return 11;
        }
        int opts = 11 | 4;
        this.mDefaultNavigationIcon = this.mToolbar.getNavigationIcon();
        return opts;
    }

    public ViewGroup getViewGroup() {
        return this.mToolbar;
    }

    public Context getContext() {
        return this.mToolbar.getContext();
    }

    public boolean hasExpandedActionView() {
        return this.mToolbar.hasExpandedActionView();
    }

    public void collapseActionView() {
        this.mToolbar.collapseActionView();
    }

    public void setWindowCallback(Callback cb) {
        this.mWindowCallback = cb;
    }

    public void setWindowTitle(CharSequence title) {
        if (!this.mTitleSet) {
            setTitleInt(title);
        }
    }

    public CharSequence getTitle() {
        return this.mToolbar.getTitle();
    }

    public void setTitle(CharSequence title) {
        this.mTitleSet = true;
        setTitleInt(title);
    }

    private void setTitleInt(CharSequence title) {
        this.mTitle = title;
        if ((this.mDisplayOpts & 8) != 0) {
            this.mToolbar.setTitle(title);
        }
    }

    public CharSequence getSubtitle() {
        return this.mToolbar.getSubtitle();
    }

    public void setSubtitle(CharSequence subtitle) {
        this.mSubtitle = subtitle;
        if ((this.mDisplayOpts & 8) != 0) {
            this.mToolbar.setSubtitle(subtitle);
        }
    }

    public void initProgress() {
        Log.i(TAG, "Progress display unsupported");
    }

    public void initIndeterminateProgress() {
        Log.i(TAG, "Progress display unsupported");
    }

    public boolean hasIcon() {
        return this.mIcon != null;
    }

    public boolean hasLogo() {
        return this.mLogo != null;
    }

    public void setIcon(int resId) {
        setIcon(resId != 0 ? AppCompatResources.getDrawable(getContext(), resId) : null);
    }

    public void setIcon(Drawable d) {
        this.mIcon = d;
        updateToolbarLogo();
    }

    public void setLogo(int resId) {
        setLogo(resId != 0 ? AppCompatResources.getDrawable(getContext(), resId) : null);
    }

    public void setLogo(Drawable d) {
        this.mLogo = d;
        updateToolbarLogo();
    }

    private void updateToolbarLogo() {
        Drawable logo = null;
        int i = this.mDisplayOpts;
        if ((i & 2) != 0) {
            if ((i & 1) != 0) {
                Drawable drawable = this.mLogo;
                if (drawable == null) {
                    drawable = this.mIcon;
                }
                logo = drawable;
            } else {
                logo = this.mIcon;
            }
        }
        this.mToolbar.setLogo(logo);
    }

    public boolean canShowOverflowMenu() {
        return this.mToolbar.canShowOverflowMenu();
    }

    public boolean isOverflowMenuShowing() {
        return this.mToolbar.isOverflowMenuShowing();
    }

    public boolean isOverflowMenuShowPending() {
        return this.mToolbar.isOverflowMenuShowPending();
    }

    public boolean showOverflowMenu() {
        return this.mToolbar.showOverflowMenu();
    }

    public boolean hideOverflowMenu() {
        return this.mToolbar.hideOverflowMenu();
    }

    public void setMenuPrepared() {
        this.mMenuPrepared = true;
    }

    public void setMenu(Menu menu, MenuPresenter.Callback cb) {
        if (this.mActionMenuPresenter == null) {
            this.mActionMenuPresenter = new ActionMenuPresenter(this.mToolbar.getContext());
            this.mActionMenuPresenter.setId(C0286R.id.action_menu_presenter);
        }
        this.mActionMenuPresenter.setCallback(cb);
        this.mToolbar.setMenu((MenuBuilder) menu, this.mActionMenuPresenter);
    }

    public void dismissPopupMenus() {
        this.mToolbar.dismissPopupMenus();
    }

    public int getDisplayOptions() {
        return this.mDisplayOpts;
    }

    public void setDisplayOptions(int newOpts) {
        int changed = this.mDisplayOpts ^ newOpts;
        this.mDisplayOpts = newOpts;
        if (changed != 0) {
            if ((changed & 4) != 0) {
                if ((newOpts & 4) != 0) {
                    updateHomeAccessibility();
                }
                updateNavigationIcon();
            }
            if ((changed & 3) != 0) {
                updateToolbarLogo();
            }
            if ((changed & 8) != 0) {
                if ((newOpts & 8) != 0) {
                    this.mToolbar.setTitle(this.mTitle);
                    this.mToolbar.setSubtitle(this.mSubtitle);
                } else {
                    this.mToolbar.setTitle(null);
                    this.mToolbar.setSubtitle(null);
                }
            }
            if ((changed & 16) != 0) {
                View view = this.mCustomView;
                if (view != null) {
                    if ((newOpts & 16) != 0) {
                        this.mToolbar.addView(view);
                    } else {
                        this.mToolbar.removeView(view);
                    }
                }
            }
        }
    }

    public void setEmbeddedTabView(ScrollingTabContainerView tabView) {
        View view = this.mTabView;
        if (view != null) {
            ViewParent parent = view.getParent();
            ViewParent viewParent = this.mToolbar;
            if (parent == viewParent) {
                viewParent.removeView(this.mTabView);
                this.mTabView = tabView;
                if (tabView == null && this.mNavigationMode == 2) {
                    this.mToolbar.addView(this.mTabView, 0);
                    Toolbar.LayoutParams lp = (Toolbar.LayoutParams) this.mTabView.getLayoutParams();
                    lp.width = -2;
                    lp.height = -2;
                    lp.gravity = 8388691;
                    tabView.setAllowCollapse(true);
                    return;
                }
            }
        }
        this.mTabView = tabView;
        if (tabView == null) {
        }
    }

    public boolean hasEmbeddedTabs() {
        return this.mTabView != null;
    }

    public boolean isTitleTruncated() {
        return this.mToolbar.isTitleTruncated();
    }

    public void setCollapsible(boolean collapsible) {
        this.mToolbar.setCollapsible(collapsible);
    }

    public void setHomeButtonEnabled(boolean enable) {
    }

    public int getNavigationMode() {
        return this.mNavigationMode;
    }

    public void setNavigationMode(int mode) {
        int oldMode = this.mNavigationMode;
        if (mode != oldMode) {
            ViewParent parent;
            ViewParent viewParent;
            switch (oldMode) {
                case 1:
                    Spinner spinner = this.mSpinner;
                    if (spinner != null) {
                        parent = spinner.getParent();
                        viewParent = this.mToolbar;
                        if (parent == viewParent) {
                            viewParent.removeView(this.mSpinner);
                            break;
                        }
                    }
                    break;
                case 2:
                    View view = this.mTabView;
                    if (view != null) {
                        parent = view.getParent();
                        viewParent = this.mToolbar;
                        if (parent == viewParent) {
                            viewParent.removeView(this.mTabView);
                            break;
                        }
                    }
                    break;
                default:
                    break;
            }
            this.mNavigationMode = mode;
            switch (mode) {
                case 0:
                    return;
                case 1:
                    ensureSpinner();
                    this.mToolbar.addView(this.mSpinner, 0);
                    return;
                case 2:
                    View view2 = this.mTabView;
                    if (view2 != null) {
                        this.mToolbar.addView(view2, 0);
                        Toolbar.LayoutParams lp = (Toolbar.LayoutParams) this.mTabView.getLayoutParams();
                        lp.width = -2;
                        lp.height = -2;
                        lp.gravity = 8388691;
                        return;
                    }
                    return;
                default:
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Invalid navigation mode ");
                    stringBuilder.append(mode);
                    throw new IllegalArgumentException(stringBuilder.toString());
            }
        }
    }

    private void ensureSpinner() {
        if (this.mSpinner == null) {
            this.mSpinner = new AppCompatSpinner(getContext(), null, C0286R.attr.actionDropDownStyle);
            this.mSpinner.setLayoutParams(new Toolbar.LayoutParams(-2, -2, 8388627));
        }
    }

    public void setDropdownParams(SpinnerAdapter adapter, OnItemSelectedListener listener) {
        ensureSpinner();
        this.mSpinner.setAdapter(adapter);
        this.mSpinner.setOnItemSelectedListener(listener);
    }

    public void setDropdownSelectedPosition(int position) {
        Spinner spinner = this.mSpinner;
        if (spinner != null) {
            spinner.setSelection(position);
            return;
        }
        throw new IllegalStateException("Can't set dropdown selected position without an adapter");
    }

    public int getDropdownSelectedPosition() {
        Spinner spinner = this.mSpinner;
        return spinner != null ? spinner.getSelectedItemPosition() : 0;
    }

    public int getDropdownItemCount() {
        Spinner spinner = this.mSpinner;
        return spinner != null ? spinner.getCount() : 0;
    }

    public void setCustomView(View view) {
        View view2 = this.mCustomView;
        if (view2 != null && (this.mDisplayOpts & 16) != 0) {
            this.mToolbar.removeView(view2);
        }
        this.mCustomView = view;
        if (view != null && (this.mDisplayOpts & 16) != 0) {
            this.mToolbar.addView(this.mCustomView);
        }
    }

    public View getCustomView() {
        return this.mCustomView;
    }

    public void animateToVisibility(int visibility) {
        ViewPropertyAnimatorCompat anim = setupAnimatorToVisibility(visibility, 200);
        if (anim != null) {
            anim.start();
        }
    }

    public ViewPropertyAnimatorCompat setupAnimatorToVisibility(final int visibility, long duration) {
        return ViewCompat.animate(this.mToolbar).alpha(visibility == 0 ? 1.0f : 0.0f).setDuration(duration).setListener(new ViewPropertyAnimatorListenerAdapter() {
            private boolean mCanceled = false;

            public void onAnimationStart(View view) {
                ToolbarWidgetWrapper.this.mToolbar.setVisibility(0);
            }

            public void onAnimationEnd(View view) {
                if (!this.mCanceled) {
                    ToolbarWidgetWrapper.this.mToolbar.setVisibility(visibility);
                }
            }

            public void onAnimationCancel(View view) {
                this.mCanceled = true;
            }
        });
    }

    public void setNavigationIcon(Drawable icon) {
        this.mNavIcon = icon;
        updateNavigationIcon();
    }

    public void setNavigationIcon(int resId) {
        setNavigationIcon(resId != 0 ? AppCompatResources.getDrawable(getContext(), resId) : null);
    }

    public void setDefaultNavigationIcon(Drawable defaultNavigationIcon) {
        if (this.mDefaultNavigationIcon != defaultNavigationIcon) {
            this.mDefaultNavigationIcon = defaultNavigationIcon;
            updateNavigationIcon();
        }
    }

    private void updateNavigationIcon() {
        if ((this.mDisplayOpts & 4) != 0) {
            Toolbar toolbar = this.mToolbar;
            Drawable drawable = this.mNavIcon;
            if (drawable == null) {
                drawable = this.mDefaultNavigationIcon;
            }
            toolbar.setNavigationIcon(drawable);
            return;
        }
        this.mToolbar.setNavigationIcon(null);
    }

    public void setNavigationContentDescription(CharSequence description) {
        this.mHomeDescription = description;
        updateHomeAccessibility();
    }

    public void setNavigationContentDescription(int resId) {
        setNavigationContentDescription(resId == 0 ? null : getContext().getString(resId));
    }

    private void updateHomeAccessibility() {
        if ((this.mDisplayOpts & 4) == 0) {
            return;
        }
        if (TextUtils.isEmpty(this.mHomeDescription)) {
            this.mToolbar.setNavigationContentDescription(this.mDefaultNavigationContentDescription);
        } else {
            this.mToolbar.setNavigationContentDescription(this.mHomeDescription);
        }
    }

    public void saveHierarchyState(SparseArray<Parcelable> toolbarStates) {
        this.mToolbar.saveHierarchyState(toolbarStates);
    }

    public void restoreHierarchyState(SparseArray<Parcelable> toolbarStates) {
        this.mToolbar.restoreHierarchyState(toolbarStates);
    }

    public void setBackgroundDrawable(Drawable d) {
        ViewCompat.setBackground(this.mToolbar, d);
    }

    public int getHeight() {
        return this.mToolbar.getHeight();
    }

    public void setVisibility(int visible) {
        this.mToolbar.setVisibility(visible);
    }

    public int getVisibility() {
        return this.mToolbar.getVisibility();
    }

    public void setMenuCallbacks(MenuPresenter.Callback actionMenuPresenterCallback, MenuBuilder.Callback menuBuilderCallback) {
        this.mToolbar.setMenuCallbacks(actionMenuPresenterCallback, menuBuilderCallback);
    }

    public Menu getMenu() {
        return this.mToolbar.getMenu();
    }
}
