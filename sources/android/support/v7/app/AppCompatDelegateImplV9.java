package android.support.v7.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.PointerIconCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.appcompat.C0286R;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.view.StandaloneActionMode;
import android.support.v7.view.menu.ListMenuPresenter;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuBuilder.Callback;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.ActionBarContextView;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.ContentFrameLayout.OnAttachListener;
import android.support.v7.widget.DecorContentParent;
import android.support.v7.widget.FitWindowsViewGroup.OnFitSystemWindowsListener;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.VectorEnabledTintResources;
import android.support.v7.widget.ViewStubCompat;
import android.support.v7.widget.ViewUtils;
import android.text.TextUtils;
import android.util.AndroidRuntimeException;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.LayoutInflater.Factory2;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.google.android.exoplayer2.util.MimeTypes;
import org.xmlpull.v1.XmlPullParser;

@RequiresApi(14)
class AppCompatDelegateImplV9 extends AppCompatDelegateImplBase implements Callback, Factory2 {
    private static final boolean IS_PRE_LOLLIPOP = (VERSION.SDK_INT < 21);
    private ActionMenuPresenterCallback mActionMenuPresenterCallback;
    ActionMode mActionMode;
    PopupWindow mActionModePopup;
    ActionBarContextView mActionModeView;
    private AppCompatViewInflater mAppCompatViewInflater;
    private boolean mClosingActionMenu;
    private DecorContentParent mDecorContentParent;
    private boolean mEnableDefaultActionBarUp;
    ViewPropertyAnimatorCompat mFadeAnim = null;
    private boolean mFeatureIndeterminateProgress;
    private boolean mFeatureProgress;
    int mInvalidatePanelMenuFeatures;
    boolean mInvalidatePanelMenuPosted;
    private final Runnable mInvalidatePanelMenuRunnable = new C02701();
    private boolean mLongPressBackDown;
    private PanelMenuPresenterCallback mPanelMenuPresenterCallback;
    private PanelFeatureState[] mPanels;
    private PanelFeatureState mPreparedPanel;
    Runnable mShowActionModePopup;
    private View mStatusGuard;
    private ViewGroup mSubDecor;
    private boolean mSubDecorInstalled;
    private Rect mTempRect1;
    private Rect mTempRect2;
    private TextView mTitleView;

    /* renamed from: android.support.v7.app.AppCompatDelegateImplV9$1 */
    class C02701 implements Runnable {
        C02701() {
        }

        public void run() {
            if ((AppCompatDelegateImplV9.this.mInvalidatePanelMenuFeatures & 1) != 0) {
                AppCompatDelegateImplV9.this.doInvalidatePanelMenu(0);
            }
            if ((AppCompatDelegateImplV9.this.mInvalidatePanelMenuFeatures & 4096) != 0) {
                AppCompatDelegateImplV9.this.doInvalidatePanelMenu(108);
            }
            AppCompatDelegateImplV9 appCompatDelegateImplV9 = AppCompatDelegateImplV9.this;
            appCompatDelegateImplV9.mInvalidatePanelMenuPosted = false;
            appCompatDelegateImplV9.mInvalidatePanelMenuFeatures = 0;
        }
    }

    /* renamed from: android.support.v7.app.AppCompatDelegateImplV9$5 */
    class C02715 implements Runnable {

        /* renamed from: android.support.v7.app.AppCompatDelegateImplV9$5$1 */
        class C11041 extends ViewPropertyAnimatorListenerAdapter {
            C11041() {
            }

            public void onAnimationStart(View view) {
                AppCompatDelegateImplV9.this.mActionModeView.setVisibility(0);
            }

            public void onAnimationEnd(View view) {
                AppCompatDelegateImplV9.this.mActionModeView.setAlpha(1.0f);
                AppCompatDelegateImplV9.this.mFadeAnim.setListener(null);
                AppCompatDelegateImplV9.this.mFadeAnim = null;
            }
        }

        C02715() {
        }

        public void run() {
            AppCompatDelegateImplV9.this.mActionModePopup.showAtLocation(AppCompatDelegateImplV9.this.mActionModeView, 55, 0, 0);
            AppCompatDelegateImplV9.this.endOnGoingFadeAnimation();
            if (AppCompatDelegateImplV9.this.shouldAnimateActionModeView()) {
                AppCompatDelegateImplV9.this.mActionModeView.setAlpha(0.0f);
                AppCompatDelegateImplV9 appCompatDelegateImplV9 = AppCompatDelegateImplV9.this;
                appCompatDelegateImplV9.mFadeAnim = ViewCompat.animate(appCompatDelegateImplV9.mActionModeView).alpha(1.0f);
                AppCompatDelegateImplV9.this.mFadeAnim.setListener(new C11041());
                return;
            }
            AppCompatDelegateImplV9.this.mActionModeView.setAlpha(1.0f);
            AppCompatDelegateImplV9.this.mActionModeView.setVisibility(0);
        }
    }

    protected static final class PanelFeatureState {
        int background;
        View createdPanelView;
        ViewGroup decorView;
        int featureId;
        Bundle frozenActionViewState;
        Bundle frozenMenuState;
        int gravity;
        boolean isHandled;
        boolean isOpen;
        boolean isPrepared;
        ListMenuPresenter listMenuPresenter;
        Context listPresenterContext;
        MenuBuilder menu;
        public boolean qwertyMode;
        boolean refreshDecorView = false;
        boolean refreshMenuContent;
        View shownPanelView;
        boolean wasLastOpen;
        int windowAnimations;
        /* renamed from: x */
        int f1x;
        /* renamed from: y */
        int f2y;

        private static class SavedState implements Parcelable {
            public static final Creator<SavedState> CREATOR = new C02721();
            int featureId;
            boolean isOpen;
            Bundle menuState;

            /* renamed from: android.support.v7.app.AppCompatDelegateImplV9$PanelFeatureState$SavedState$1 */
            static class C02721 implements ClassLoaderCreator<SavedState> {
                C02721() {
                }

                public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                    return SavedState.readFromParcel(in, loader);
                }

                public SavedState createFromParcel(Parcel in) {
                    return SavedState.readFromParcel(in, null);
                }

                public SavedState[] newArray(int size) {
                    return new SavedState[size];
                }
            }

            SavedState() {
            }

            public int describeContents() {
                return 0;
            }

            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.featureId);
                dest.writeInt(this.isOpen);
                if (this.isOpen) {
                    dest.writeBundle(this.menuState);
                }
            }

            static SavedState readFromParcel(Parcel source, ClassLoader loader) {
                SavedState savedState = new SavedState();
                savedState.featureId = source.readInt();
                boolean z = true;
                if (source.readInt() != 1) {
                    z = false;
                }
                savedState.isOpen = z;
                if (savedState.isOpen) {
                    savedState.menuState = source.readBundle(loader);
                }
                return savedState;
            }
        }

        PanelFeatureState(int featureId) {
            this.featureId = featureId;
        }

        public boolean hasPanelItems() {
            boolean z = false;
            if (this.shownPanelView == null) {
                return false;
            }
            if (this.createdPanelView != null) {
                return true;
            }
            if (this.listMenuPresenter.getAdapter().getCount() > 0) {
                z = true;
            }
            return z;
        }

        public void clearMenuPresenters() {
            MenuBuilder menuBuilder = this.menu;
            if (menuBuilder != null) {
                menuBuilder.removeMenuPresenter(this.listMenuPresenter);
            }
            this.listMenuPresenter = null;
        }

        void setStyle(Context context) {
            TypedValue outValue = new TypedValue();
            Theme widgetTheme = context.getResources().newTheme();
            widgetTheme.setTo(context.getTheme());
            widgetTheme.resolveAttribute(C0286R.attr.actionBarPopupTheme, outValue, true);
            if (outValue.resourceId != 0) {
                widgetTheme.applyStyle(outValue.resourceId, true);
            }
            widgetTheme.resolveAttribute(C0286R.attr.panelMenuListTheme, outValue, true);
            if (outValue.resourceId != 0) {
                widgetTheme.applyStyle(outValue.resourceId, true);
            } else {
                widgetTheme.applyStyle(C0286R.style.Theme_AppCompat_CompactMenu, true);
            }
            context = new ContextThemeWrapper(context, 0);
            context.getTheme().setTo(widgetTheme);
            this.listPresenterContext = context;
            TypedArray a = context.obtainStyledAttributes(C0286R.styleable.AppCompatTheme);
            this.background = a.getResourceId(C0286R.styleable.AppCompatTheme_panelBackground, 0);
            this.windowAnimations = a.getResourceId(C0286R.styleable.AppCompatTheme_android_windowAnimationStyle, 0);
            a.recycle();
        }

        void setMenu(MenuBuilder menu) {
            MenuBuilder menuBuilder = this.menu;
            if (menu != menuBuilder) {
                if (menuBuilder != null) {
                    menuBuilder.removeMenuPresenter(this.listMenuPresenter);
                }
                this.menu = menu;
                if (menu != null) {
                    MenuPresenter menuPresenter = this.listMenuPresenter;
                    if (menuPresenter != null) {
                        menu.addMenuPresenter(menuPresenter);
                    }
                }
            }
        }

        MenuView getListMenuView(MenuPresenter.Callback cb) {
            if (this.menu == null) {
                return null;
            }
            if (this.listMenuPresenter == null) {
                this.listMenuPresenter = new ListMenuPresenter(this.listPresenterContext, C0286R.layout.abc_list_menu_item_layout);
                this.listMenuPresenter.setCallback(cb);
                this.menu.addMenuPresenter(this.listMenuPresenter);
            }
            return this.listMenuPresenter.getMenuView(this.decorView);
        }

        Parcelable onSaveInstanceState() {
            SavedState savedState = new SavedState();
            savedState.featureId = this.featureId;
            savedState.isOpen = this.isOpen;
            if (this.menu != null) {
                savedState.menuState = new Bundle();
                this.menu.savePresenterStates(savedState.menuState);
            }
            return savedState;
        }

        void onRestoreInstanceState(Parcelable state) {
            SavedState savedState = (SavedState) state;
            this.featureId = savedState.featureId;
            this.wasLastOpen = savedState.isOpen;
            this.frozenMenuState = savedState.menuState;
            this.shownPanelView = null;
            this.decorView = null;
        }

        void applyFrozenState() {
            MenuBuilder menuBuilder = this.menu;
            if (menuBuilder != null) {
                Bundle bundle = this.frozenMenuState;
                if (bundle != null) {
                    menuBuilder.restorePresenterStates(bundle);
                    this.frozenMenuState = null;
                }
            }
        }
    }

    /* renamed from: android.support.v7.app.AppCompatDelegateImplV9$2 */
    class C08562 implements OnApplyWindowInsetsListener {
        C08562() {
        }

        public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
            int top = insets.getSystemWindowInsetTop();
            int newTop = AppCompatDelegateImplV9.this.updateStatusGuard(top);
            if (top != newTop) {
                insets = insets.replaceSystemWindowInsets(insets.getSystemWindowInsetLeft(), newTop, insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            }
            return ViewCompat.onApplyWindowInsets(v, insets);
        }
    }

    /* renamed from: android.support.v7.app.AppCompatDelegateImplV9$3 */
    class C08573 implements OnFitSystemWindowsListener {
        C08573() {
        }

        public void onFitSystemWindows(Rect insets) {
            insets.top = AppCompatDelegateImplV9.this.updateStatusGuard(insets.top);
        }
    }

    /* renamed from: android.support.v7.app.AppCompatDelegateImplV9$4 */
    class C08584 implements OnAttachListener {
        C08584() {
        }

        public void onAttachedFromWindow() {
        }

        public void onDetachedFromWindow() {
            AppCompatDelegateImplV9.this.dismissPopups();
        }
    }

    private final class ActionMenuPresenterCallback implements MenuPresenter.Callback {
        ActionMenuPresenterCallback() {
        }

        public boolean onOpenSubMenu(MenuBuilder subMenu) {
            Window.Callback cb = AppCompatDelegateImplV9.this.getWindowCallback();
            if (cb != null) {
                cb.onMenuOpened(108, subMenu);
            }
            return true;
        }

        public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
            AppCompatDelegateImplV9.this.checkCloseActionMenu(menu);
        }
    }

    class ActionModeCallbackWrapperV9 implements ActionMode.Callback {
        private ActionMode.Callback mWrapped;

        /* renamed from: android.support.v7.app.AppCompatDelegateImplV9$ActionModeCallbackWrapperV9$1 */
        class C11061 extends ViewPropertyAnimatorListenerAdapter {
            C11061() {
            }

            public void onAnimationEnd(View view) {
                AppCompatDelegateImplV9.this.mActionModeView.setVisibility(8);
                if (AppCompatDelegateImplV9.this.mActionModePopup != null) {
                    AppCompatDelegateImplV9.this.mActionModePopup.dismiss();
                } else if (AppCompatDelegateImplV9.this.mActionModeView.getParent() instanceof View) {
                    ViewCompat.requestApplyInsets((View) AppCompatDelegateImplV9.this.mActionModeView.getParent());
                }
                AppCompatDelegateImplV9.this.mActionModeView.removeAllViews();
                AppCompatDelegateImplV9.this.mFadeAnim.setListener(null);
                AppCompatDelegateImplV9.this.mFadeAnim = null;
            }
        }

        public ActionModeCallbackWrapperV9(ActionMode.Callback wrapped) {
            this.mWrapped = wrapped;
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return this.mWrapped.onCreateActionMode(mode, menu);
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return this.mWrapped.onPrepareActionMode(mode, menu);
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return this.mWrapped.onActionItemClicked(mode, item);
        }

        public void onDestroyActionMode(ActionMode mode) {
            this.mWrapped.onDestroyActionMode(mode);
            if (AppCompatDelegateImplV9.this.mActionModePopup != null) {
                AppCompatDelegateImplV9.this.mWindow.getDecorView().removeCallbacks(AppCompatDelegateImplV9.this.mShowActionModePopup);
            }
            if (AppCompatDelegateImplV9.this.mActionModeView != null) {
                AppCompatDelegateImplV9.this.endOnGoingFadeAnimation();
                AppCompatDelegateImplV9 appCompatDelegateImplV9 = AppCompatDelegateImplV9.this;
                appCompatDelegateImplV9.mFadeAnim = ViewCompat.animate(appCompatDelegateImplV9.mActionModeView).alpha(0.0f);
                AppCompatDelegateImplV9.this.mFadeAnim.setListener(new C11061());
            }
            if (AppCompatDelegateImplV9.this.mAppCompatCallback != null) {
                AppCompatDelegateImplV9.this.mAppCompatCallback.onSupportActionModeFinished(AppCompatDelegateImplV9.this.mActionMode);
            }
            AppCompatDelegateImplV9.this.mActionMode = null;
        }
    }

    private class ListMenuDecorView extends ContentFrameLayout {
        public ListMenuDecorView(Context context) {
            super(context);
        }

        public boolean dispatchKeyEvent(KeyEvent event) {
            if (!AppCompatDelegateImplV9.this.dispatchKeyEvent(event)) {
                if (!super.dispatchKeyEvent(event)) {
                    return false;
                }
            }
            return true;
        }

        public boolean onInterceptTouchEvent(MotionEvent event) {
            if (event.getAction() == 0) {
                if (isOutOfBounds((int) event.getX(), (int) event.getY())) {
                    AppCompatDelegateImplV9.this.closePanel(0);
                    return true;
                }
            }
            return super.onInterceptTouchEvent(event);
        }

        public void setBackgroundResource(int resid) {
            setBackgroundDrawable(AppCompatResources.getDrawable(getContext(), resid));
        }

        private boolean isOutOfBounds(int x, int y) {
            if (x >= -5 && y >= -5 && x <= getWidth() + 5) {
                if (y <= getHeight() + 5) {
                    return false;
                }
            }
            return true;
        }
    }

    private final class PanelMenuPresenterCallback implements MenuPresenter.Callback {
        PanelMenuPresenterCallback() {
        }

        public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
            Menu parentMenu = menu.getRootMenu();
            boolean isSubMenu = parentMenu != menu;
            PanelFeatureState panel = AppCompatDelegateImplV9.this.findMenuPanel(isSubMenu ? parentMenu : menu);
            if (panel == null) {
                return;
            }
            if (isSubMenu) {
                AppCompatDelegateImplV9.this.callOnPanelClosed(panel.featureId, panel, parentMenu);
                AppCompatDelegateImplV9.this.closePanel(panel, true);
                return;
            }
            AppCompatDelegateImplV9.this.closePanel(panel, allMenusAreClosing);
        }

        public boolean onOpenSubMenu(MenuBuilder subMenu) {
            if (subMenu == null && AppCompatDelegateImplV9.this.mHasActionBar) {
                Window.Callback cb = AppCompatDelegateImplV9.this.getWindowCallback();
                if (cb != null && !AppCompatDelegateImplV9.this.isDestroyed()) {
                    cb.onMenuOpened(108, subMenu);
                }
            }
            return true;
        }
    }

    /* renamed from: android.support.v7.app.AppCompatDelegateImplV9$6 */
    class C11056 extends ViewPropertyAnimatorListenerAdapter {
        C11056() {
        }

        public void onAnimationStart(View view) {
            AppCompatDelegateImplV9.this.mActionModeView.setVisibility(0);
            AppCompatDelegateImplV9.this.mActionModeView.sendAccessibilityEvent(32);
            if (AppCompatDelegateImplV9.this.mActionModeView.getParent() instanceof View) {
                ViewCompat.requestApplyInsets((View) AppCompatDelegateImplV9.this.mActionModeView.getParent());
            }
        }

        public void onAnimationEnd(View view) {
            AppCompatDelegateImplV9.this.mActionModeView.setAlpha(1.0f);
            AppCompatDelegateImplV9.this.mFadeAnim.setListener(null);
            AppCompatDelegateImplV9.this.mFadeAnim = null;
        }
    }

    private android.view.ViewGroup createSubDecor() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:71:0x01c2 in {4, 7, 8, 11, 12, 15, 16, 21, 26, 27, 30, 31, 34, 35, 38, 39, 40, 41, 44, 45, 48, 49, 53, 54, 59, 62, 63, 64, 66, 68, 70} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r10 = this;
        r0 = r10.mContext;
        r1 = android.support.v7.appcompat.C0286R.styleable.AppCompatTheme;
        r0 = r0.obtainStyledAttributes(r1);
        r1 = android.support.v7.appcompat.C0286R.styleable.AppCompatTheme_windowActionBar;
        r1 = r0.hasValue(r1);
        if (r1 == 0) goto L_0x01b7;
    L_0x0010:
        r1 = android.support.v7.appcompat.C0286R.styleable.AppCompatTheme_windowNoTitle;
        r2 = 0;
        r1 = r0.getBoolean(r1, r2);
        r3 = 1;
        if (r1 == 0) goto L_0x001e;
    L_0x001a:
        r10.requestWindowFeature(r3);
        goto L_0x002d;
    L_0x001e:
        r1 = android.support.v7.appcompat.C0286R.styleable.AppCompatTheme_windowActionBar;
        r1 = r0.getBoolean(r1, r2);
        if (r1 == 0) goto L_0x002c;
    L_0x0026:
        r1 = 108; // 0x6c float:1.51E-43 double:5.34E-322;
        r10.requestWindowFeature(r1);
        goto L_0x002d;
    L_0x002d:
        r1 = android.support.v7.appcompat.C0286R.styleable.AppCompatTheme_windowActionBarOverlay;
        r1 = r0.getBoolean(r1, r2);
        r4 = 109; // 0x6d float:1.53E-43 double:5.4E-322;
        if (r1 == 0) goto L_0x003b;
    L_0x0037:
        r10.requestWindowFeature(r4);
        goto L_0x003c;
    L_0x003c:
        r1 = android.support.v7.appcompat.C0286R.styleable.AppCompatTheme_windowActionModeOverlay;
        r1 = r0.getBoolean(r1, r2);
        if (r1 == 0) goto L_0x004a;
    L_0x0044:
        r1 = 10;
        r10.requestWindowFeature(r1);
        goto L_0x004b;
    L_0x004b:
        r1 = android.support.v7.appcompat.C0286R.styleable.AppCompatTheme_android_windowIsFloating;
        r1 = r0.getBoolean(r1, r2);
        r10.mIsFloating = r1;
        r0.recycle();
        r1 = r10.mWindow;
        r1.getDecorView();
        r1 = r10.mContext;
        r1 = android.view.LayoutInflater.from(r1);
        r5 = 0;
        r6 = r10.mWindowNoTitle;
        r7 = 0;
        if (r6 != 0) goto L_0x00e3;
    L_0x0067:
        r6 = r10.mIsFloating;
        if (r6 == 0) goto L_0x007a;
    L_0x006b:
        r3 = android.support.v7.appcompat.C0286R.layout.abc_dialog_title_material;
        r3 = r1.inflate(r3, r7);
        r5 = r3;
        r5 = (android.view.ViewGroup) r5;
        r10.mOverlayActionBar = r2;
        r10.mHasActionBar = r2;
        goto L_0x0114;
    L_0x007a:
        r6 = r10.mHasActionBar;
        if (r6 == 0) goto L_0x00e2;
    L_0x007e:
        r6 = new android.util.TypedValue;
        r6.<init>();
        r8 = r10.mContext;
        r8 = r8.getTheme();
        r9 = android.support.v7.appcompat.C0286R.attr.actionBarTheme;
        r8.resolveAttribute(r9, r6, r3);
        r3 = r6.resourceId;
        if (r3 == 0) goto L_0x009c;
    L_0x0092:
        r3 = new android.support.v7.view.ContextThemeWrapper;
        r8 = r10.mContext;
        r9 = r6.resourceId;
        r3.<init>(r8, r9);
        goto L_0x009e;
    L_0x009c:
        r3 = r10.mContext;
    L_0x009e:
        r8 = android.view.LayoutInflater.from(r3);
        r9 = android.support.v7.appcompat.C0286R.layout.abc_screen_toolbar;
        r8 = r8.inflate(r9, r7);
        r5 = r8;
        r5 = (android.view.ViewGroup) r5;
        r8 = android.support.v7.appcompat.C0286R.id.decor_content_parent;
        r8 = r5.findViewById(r8);
        r8 = (android.support.v7.widget.DecorContentParent) r8;
        r10.mDecorContentParent = r8;
        r8 = r10.mDecorContentParent;
        r9 = r10.getWindowCallback();
        r8.setWindowCallback(r9);
        r8 = r10.mOverlayActionBar;
        if (r8 == 0) goto L_0x00c8;
    L_0x00c2:
        r8 = r10.mDecorContentParent;
        r8.initFeature(r4);
        goto L_0x00c9;
    L_0x00c9:
        r4 = r10.mFeatureProgress;
        if (r4 == 0) goto L_0x00d4;
    L_0x00cd:
        r4 = r10.mDecorContentParent;
        r8 = 2;
        r4.initFeature(r8);
        goto L_0x00d5;
    L_0x00d5:
        r4 = r10.mFeatureIndeterminateProgress;
        if (r4 == 0) goto L_0x00e0;
    L_0x00d9:
        r4 = r10.mDecorContentParent;
        r8 = 5;
        r4.initFeature(r8);
        goto L_0x00e1;
    L_0x00e1:
        goto L_0x0114;
    L_0x00e2:
        goto L_0x0114;
    L_0x00e3:
        r3 = r10.mOverlayActionMode;
        if (r3 == 0) goto L_0x00f1;
    L_0x00e7:
        r3 = android.support.v7.appcompat.C0286R.layout.abc_screen_simple_overlay_action_mode;
        r3 = r1.inflate(r3, r7);
        r3 = (android.view.ViewGroup) r3;
        r5 = r3;
        goto L_0x00fa;
    L_0x00f1:
        r3 = android.support.v7.appcompat.C0286R.layout.abc_screen_simple;
        r3 = r1.inflate(r3, r7);
        r3 = (android.view.ViewGroup) r3;
        r5 = r3;
    L_0x00fa:
        r3 = android.os.Build.VERSION.SDK_INT;
        r4 = 21;
        if (r3 < r4) goto L_0x0109;
    L_0x0100:
        r3 = new android.support.v7.app.AppCompatDelegateImplV9$2;
        r3.<init>();
        android.support.v4.view.ViewCompat.setOnApplyWindowInsetsListener(r5, r3);
        goto L_0x0114;
    L_0x0109:
        r3 = r5;
        r3 = (android.support.v7.widget.FitWindowsViewGroup) r3;
        r4 = new android.support.v7.app.AppCompatDelegateImplV9$3;
        r4.<init>();
        r3.setOnFitSystemWindowsListener(r4);
    L_0x0114:
        if (r5 == 0) goto L_0x0171;
    L_0x0116:
        r3 = r10.mDecorContentParent;
        if (r3 != 0) goto L_0x0125;
    L_0x011a:
        r3 = android.support.v7.appcompat.C0286R.id.title;
        r3 = r5.findViewById(r3);
        r3 = (android.widget.TextView) r3;
        r10.mTitleView = r3;
        goto L_0x0126;
    L_0x0126:
        android.support.v7.widget.ViewUtils.makeOptionalFitsSystemWindows(r5);
        r3 = android.support.v7.appcompat.C0286R.id.action_bar_activity_content;
        r3 = r5.findViewById(r3);
        r3 = (android.support.v7.widget.ContentFrameLayout) r3;
        r4 = r10.mWindow;
        r6 = 16908290; // 0x1020002 float:2.3877235E-38 double:8.353805E-317;
        r4 = r4.findViewById(r6);
        r4 = (android.view.ViewGroup) r4;
        if (r4 == 0) goto L_0x0162;
    L_0x013e:
        r8 = r4.getChildCount();
        if (r8 <= 0) goto L_0x014f;
    L_0x0144:
        r8 = r4.getChildAt(r2);
        r4.removeViewAt(r2);
        r3.addView(r8);
        goto L_0x013e;
    L_0x014f:
        r2 = -1;
        r4.setId(r2);
        r3.setId(r6);
        r2 = r4 instanceof android.widget.FrameLayout;
        if (r2 == 0) goto L_0x0161;
    L_0x015a:
        r2 = r4;
        r2 = (android.widget.FrameLayout) r2;
        r2.setForeground(r7);
        goto L_0x0163;
    L_0x0161:
        goto L_0x0163;
    L_0x0163:
        r2 = r10.mWindow;
        r2.setContentView(r5);
        r2 = new android.support.v7.app.AppCompatDelegateImplV9$4;
        r2.<init>();
        r3.setAttachListener(r2);
        return r5;
    L_0x0171:
        r2 = new java.lang.IllegalArgumentException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "AppCompat does not support the current theme features: { windowActionBar: ";
        r3.append(r4);
        r4 = r10.mHasActionBar;
        r3.append(r4);
        r4 = ", windowActionBarOverlay: ";
        r3.append(r4);
        r4 = r10.mOverlayActionBar;
        r3.append(r4);
        r4 = ", android:windowIsFloating: ";
        r3.append(r4);
        r4 = r10.mIsFloating;
        r3.append(r4);
        r4 = ", windowActionModeOverlay: ";
        r3.append(r4);
        r4 = r10.mOverlayActionMode;
        r3.append(r4);
        r4 = ", windowNoTitle: ";
        r3.append(r4);
        r4 = r10.mWindowNoTitle;
        r3.append(r4);
        r4 = " }";
        r3.append(r4);
        r3 = r3.toString();
        r2.<init>(r3);
        throw r2;
    L_0x01b7:
        r0.recycle();
        r1 = new java.lang.IllegalStateException;
        r2 = "You need to use a Theme.AppCompat theme (or descendant) with this activity.";
        r1.<init>(r2);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.app.AppCompatDelegateImplV9.createSubDecor():android.view.ViewGroup");
    }

    AppCompatDelegateImplV9(Context context, Window window, AppCompatCallback callback) {
        super(context, window, callback);
    }

    public void onCreate(Bundle savedInstanceState) {
        if (!(this.mOriginalWindowCallback instanceof Activity)) {
            return;
        }
        if (NavUtils.getParentActivityName((Activity) this.mOriginalWindowCallback) != null) {
            ActionBar ab = peekSupportActionBar();
            if (ab == null) {
                this.mEnableDefaultActionBarUp = true;
            } else {
                ab.setDefaultDisplayHomeAsUpEnabled(true);
            }
        }
    }

    public void onPostCreate(Bundle savedInstanceState) {
        ensureSubDecor();
    }

    public void initWindowDecorActionBar() {
        ensureSubDecor();
        if (this.mHasActionBar) {
            if (this.mActionBar == null) {
                if (this.mOriginalWindowCallback instanceof Activity) {
                    this.mActionBar = new WindowDecorActionBar((Activity) this.mOriginalWindowCallback, this.mOverlayActionBar);
                } else if (this.mOriginalWindowCallback instanceof Dialog) {
                    this.mActionBar = new WindowDecorActionBar((Dialog) this.mOriginalWindowCallback);
                }
                if (this.mActionBar != null) {
                    this.mActionBar.setDefaultDisplayHomeAsUpEnabled(this.mEnableDefaultActionBarUp);
                }
            }
        }
    }

    public void setSupportActionBar(Toolbar toolbar) {
        if (this.mOriginalWindowCallback instanceof Activity) {
            ActionBar ab = getSupportActionBar();
            if (ab instanceof WindowDecorActionBar) {
                throw new IllegalStateException("This Activity already has an action bar supplied by the window decor. Do not request Window.FEATURE_SUPPORT_ACTION_BAR and set windowActionBar to false in your theme to use a Toolbar instead.");
            }
            this.mMenuInflater = null;
            if (ab != null) {
                ab.onDestroy();
            }
            if (toolbar != null) {
                ToolbarActionBar tbab = new ToolbarActionBar(toolbar, ((Activity) this.mOriginalWindowCallback).getTitle(), this.mAppCompatWindowCallback);
                this.mActionBar = tbab;
                this.mWindow.setCallback(tbab.getWrappedWindowCallback());
            } else {
                this.mActionBar = null;
                this.mWindow.setCallback(this.mAppCompatWindowCallback);
            }
            invalidateOptionsMenu();
        }
    }

    @Nullable
    public <T extends View> T findViewById(@IdRes int id) {
        ensureSubDecor();
        return this.mWindow.findViewById(id);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (this.mHasActionBar && this.mSubDecorInstalled) {
            ActionBar ab = getSupportActionBar();
            if (ab != null) {
                ab.onConfigurationChanged(newConfig);
            }
        }
        AppCompatDrawableManager.get().onConfigurationChanged(this.mContext);
        applyDayNight();
    }

    public void onStop() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setShowHideAnimationEnabled(false);
        }
    }

    public void onPostResume() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setShowHideAnimationEnabled(true);
        }
    }

    public void setContentView(View v) {
        ensureSubDecor();
        ViewGroup contentParent = (ViewGroup) this.mSubDecor.findViewById(16908290);
        contentParent.removeAllViews();
        contentParent.addView(v);
        this.mOriginalWindowCallback.onContentChanged();
    }

    public void setContentView(int resId) {
        ensureSubDecor();
        ViewGroup contentParent = (ViewGroup) this.mSubDecor.findViewById(16908290);
        contentParent.removeAllViews();
        LayoutInflater.from(this.mContext).inflate(resId, contentParent);
        this.mOriginalWindowCallback.onContentChanged();
    }

    public void setContentView(View v, LayoutParams lp) {
        ensureSubDecor();
        ViewGroup contentParent = (ViewGroup) this.mSubDecor.findViewById(16908290);
        contentParent.removeAllViews();
        contentParent.addView(v, lp);
        this.mOriginalWindowCallback.onContentChanged();
    }

    public void addContentView(View v, LayoutParams lp) {
        ensureSubDecor();
        ((ViewGroup) this.mSubDecor.findViewById(16908290)).addView(v, lp);
        this.mOriginalWindowCallback.onContentChanged();
    }

    public void onDestroy() {
        if (this.mInvalidatePanelMenuPosted) {
            this.mWindow.getDecorView().removeCallbacks(this.mInvalidatePanelMenuRunnable);
        }
        super.onDestroy();
        if (this.mActionBar != null) {
            this.mActionBar.onDestroy();
        }
    }

    private void ensureSubDecor() {
        if (!this.mSubDecorInstalled) {
            this.mSubDecor = createSubDecor();
            CharSequence title = getTitle();
            if (!TextUtils.isEmpty(title)) {
                onTitleChanged(title);
            }
            applyFixedSizeWindow();
            onSubDecorInstalled(this.mSubDecor);
            this.mSubDecorInstalled = true;
            PanelFeatureState st = getPanelState(0, false);
            if (!isDestroyed() && (st == null || st.menu == null)) {
                invalidatePanelMenu(108);
            }
        }
    }

    void onSubDecorInstalled(ViewGroup subDecor) {
    }

    private void applyFixedSizeWindow() {
        ContentFrameLayout cfl = (ContentFrameLayout) this.mSubDecor.findViewById(16908290);
        View windowDecor = this.mWindow.getDecorView();
        cfl.setDecorPadding(windowDecor.getPaddingLeft(), windowDecor.getPaddingTop(), windowDecor.getPaddingRight(), windowDecor.getPaddingBottom());
        TypedArray a = this.mContext.obtainStyledAttributes(C0286R.styleable.AppCompatTheme);
        a.getValue(C0286R.styleable.AppCompatTheme_windowMinWidthMajor, cfl.getMinWidthMajor());
        a.getValue(C0286R.styleable.AppCompatTheme_windowMinWidthMinor, cfl.getMinWidthMinor());
        if (a.hasValue(C0286R.styleable.AppCompatTheme_windowFixedWidthMajor)) {
            a.getValue(C0286R.styleable.AppCompatTheme_windowFixedWidthMajor, cfl.getFixedWidthMajor());
        }
        if (a.hasValue(C0286R.styleable.AppCompatTheme_windowFixedWidthMinor)) {
            a.getValue(C0286R.styleable.AppCompatTheme_windowFixedWidthMinor, cfl.getFixedWidthMinor());
        }
        if (a.hasValue(C0286R.styleable.AppCompatTheme_windowFixedHeightMajor)) {
            a.getValue(C0286R.styleable.AppCompatTheme_windowFixedHeightMajor, cfl.getFixedHeightMajor());
        }
        if (a.hasValue(C0286R.styleable.AppCompatTheme_windowFixedHeightMinor)) {
            a.getValue(C0286R.styleable.AppCompatTheme_windowFixedHeightMinor, cfl.getFixedHeightMinor());
        }
        a.recycle();
        cfl.requestLayout();
    }

    public boolean requestWindowFeature(int featureId) {
        featureId = sanitizeWindowFeatureId(featureId);
        if (this.mWindowNoTitle && featureId == 108) {
            return false;
        }
        if (this.mHasActionBar && featureId == 1) {
            this.mHasActionBar = false;
        }
        switch (featureId) {
            case 1:
                throwFeatureRequestIfSubDecorInstalled();
                this.mWindowNoTitle = true;
                return true;
            case 2:
                throwFeatureRequestIfSubDecorInstalled();
                this.mFeatureProgress = true;
                return true;
            case 5:
                throwFeatureRequestIfSubDecorInstalled();
                this.mFeatureIndeterminateProgress = true;
                return true;
            case 10:
                throwFeatureRequestIfSubDecorInstalled();
                this.mOverlayActionMode = true;
                return true;
            case 108:
                throwFeatureRequestIfSubDecorInstalled();
                this.mHasActionBar = true;
                return true;
            case 109:
                throwFeatureRequestIfSubDecorInstalled();
                this.mOverlayActionBar = true;
                return true;
            default:
                return this.mWindow.requestFeature(featureId);
        }
    }

    public boolean hasWindowFeature(int featureId) {
        switch (sanitizeWindowFeatureId(featureId)) {
            case 1:
                return this.mWindowNoTitle;
            case 2:
                return this.mFeatureProgress;
            case 5:
                return this.mFeatureIndeterminateProgress;
            case 10:
                return this.mOverlayActionMode;
            case 108:
                return this.mHasActionBar;
            case 109:
                return this.mOverlayActionBar;
            default:
                return false;
        }
    }

    void onTitleChanged(CharSequence title) {
        DecorContentParent decorContentParent = this.mDecorContentParent;
        if (decorContentParent != null) {
            decorContentParent.setWindowTitle(title);
        } else if (peekSupportActionBar() != null) {
            peekSupportActionBar().setWindowTitle(title);
        } else {
            TextView textView = this.mTitleView;
            if (textView != null) {
                textView.setText(title);
            }
        }
    }

    void onPanelClosed(int featureId, Menu menu) {
        if (featureId == 108) {
            ActionBar ab = getSupportActionBar();
            if (ab != null) {
                ab.dispatchMenuVisibilityChanged(false);
            }
        } else if (featureId == 0) {
            PanelFeatureState st = getPanelState(featureId, true);
            if (st.isOpen) {
                closePanel(st, false);
            }
        }
    }

    boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId != 108) {
            return false;
        }
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.dispatchMenuVisibilityChanged(true);
        }
        return true;
    }

    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
        Window.Callback cb = getWindowCallback();
        if (cb != null && !isDestroyed()) {
            PanelFeatureState panel = findMenuPanel(menu.getRootMenu());
            if (panel != null) {
                return cb.onMenuItemSelected(panel.featureId, item);
            }
        }
        return false;
    }

    public void onMenuModeChange(MenuBuilder menu) {
        reopenMenu(menu, true);
    }

    public ActionMode startSupportActionMode(@NonNull ActionMode.Callback callback) {
        if (callback != null) {
            ActionMode actionMode = this.mActionMode;
            if (actionMode != null) {
                actionMode.finish();
            }
            ActionMode.Callback wrappedCallback = new ActionModeCallbackWrapperV9(callback);
            ActionBar ab = getSupportActionBar();
            if (ab != null) {
                this.mActionMode = ab.startActionMode(wrappedCallback);
                if (this.mActionMode != null && this.mAppCompatCallback != null) {
                    this.mAppCompatCallback.onSupportActionModeStarted(this.mActionMode);
                }
            }
            if (this.mActionMode == null) {
                this.mActionMode = startSupportActionModeFromWindow(wrappedCallback);
            }
            return this.mActionMode;
        }
        throw new IllegalArgumentException("ActionMode callback can not be null.");
    }

    public void invalidateOptionsMenu() {
        ActionBar ab = getSupportActionBar();
        if (ab == null || !ab.invalidateOptionsMenu()) {
            invalidatePanelMenu(0);
        }
    }

    ActionMode startSupportActionModeFromWindow(@NonNull ActionMode.Callback callback) {
        endOnGoingFadeAnimation();
        ActionMode actionMode = this.mActionMode;
        if (actionMode != null) {
            actionMode.finish();
        }
        if (!(callback instanceof ActionModeCallbackWrapperV9)) {
            callback = new ActionModeCallbackWrapperV9(callback);
        }
        actionMode = null;
        if (this.mAppCompatCallback != null && !isDestroyed()) {
            try {
                actionMode = this.mAppCompatCallback.onWindowStartingSupportActionMode(callback);
            } catch (AbstractMethodError e) {
            }
        }
        if (actionMode != null) {
            this.mActionMode = actionMode;
        } else {
            boolean z = true;
            if (this.mActionModeView == null) {
                if (this.mIsFloating) {
                    Context actionBarContext;
                    TypedValue outValue = new TypedValue();
                    Theme baseTheme = this.mContext.getTheme();
                    baseTheme.resolveAttribute(C0286R.attr.actionBarTheme, outValue, true);
                    if (outValue.resourceId != 0) {
                        Theme actionBarTheme = this.mContext.getResources().newTheme();
                        actionBarTheme.setTo(baseTheme);
                        actionBarTheme.applyStyle(outValue.resourceId, true);
                        actionBarContext = new ContextThemeWrapper(this.mContext, 0);
                        actionBarContext.getTheme().setTo(actionBarTheme);
                    } else {
                        actionBarContext = this.mContext;
                    }
                    this.mActionModeView = new ActionBarContextView(actionBarContext);
                    this.mActionModePopup = new PopupWindow(actionBarContext, null, C0286R.attr.actionModePopupWindowStyle);
                    PopupWindowCompat.setWindowLayoutType(this.mActionModePopup, 2);
                    this.mActionModePopup.setContentView(this.mActionModeView);
                    this.mActionModePopup.setWidth(-1);
                    actionBarContext.getTheme().resolveAttribute(C0286R.attr.actionBarSize, outValue, true);
                    this.mActionModeView.setContentHeight(TypedValue.complexToDimensionPixelSize(outValue.data, actionBarContext.getResources().getDisplayMetrics()));
                    this.mActionModePopup.setHeight(-2);
                    this.mShowActionModePopup = new C02715();
                } else {
                    ViewStubCompat stub = (ViewStubCompat) this.mSubDecor.findViewById(C0286R.id.action_mode_bar_stub);
                    if (stub != null) {
                        stub.setLayoutInflater(LayoutInflater.from(getActionBarThemedContext()));
                        this.mActionModeView = (ActionBarContextView) stub.inflate();
                    }
                }
            }
            if (this.mActionModeView != null) {
                endOnGoingFadeAnimation();
                this.mActionModeView.killMode();
                Context context = this.mActionModeView.getContext();
                ActionBarContextView actionBarContextView = this.mActionModeView;
                if (this.mActionModePopup != null) {
                    z = false;
                }
                actionMode = new StandaloneActionMode(context, actionBarContextView, callback, z);
                if (callback.onCreateActionMode(actionMode, actionMode.getMenu())) {
                    actionMode.invalidate();
                    this.mActionModeView.initForMode(actionMode);
                    this.mActionMode = actionMode;
                    if (shouldAnimateActionModeView()) {
                        this.mActionModeView.setAlpha(0.0f);
                        this.mFadeAnim = ViewCompat.animate(this.mActionModeView).alpha(1.0f);
                        this.mFadeAnim.setListener(new C11056());
                    } else {
                        this.mActionModeView.setAlpha(1.0f);
                        this.mActionModeView.setVisibility(0);
                        this.mActionModeView.sendAccessibilityEvent(32);
                        if (this.mActionModeView.getParent() instanceof View) {
                            ViewCompat.requestApplyInsets((View) this.mActionModeView.getParent());
                        }
                    }
                    if (this.mActionModePopup != null) {
                        this.mWindow.getDecorView().post(this.mShowActionModePopup);
                    }
                } else {
                    this.mActionMode = null;
                }
            }
        }
        if (this.mActionMode != null && this.mAppCompatCallback != null) {
            this.mAppCompatCallback.onSupportActionModeStarted(this.mActionMode);
        }
        return this.mActionMode;
    }

    final boolean shouldAnimateActionModeView() {
        if (this.mSubDecorInstalled) {
            View view = this.mSubDecor;
            if (view != null && ViewCompat.isLaidOut(view)) {
                return true;
            }
        }
        return false;
    }

    void endOnGoingFadeAnimation() {
        ViewPropertyAnimatorCompat viewPropertyAnimatorCompat = this.mFadeAnim;
        if (viewPropertyAnimatorCompat != null) {
            viewPropertyAnimatorCompat.cancel();
        }
    }

    boolean onBackPressed() {
        ActionMode actionMode = this.mActionMode;
        if (actionMode != null) {
            actionMode.finish();
            return true;
        }
        ActionBar ab = getSupportActionBar();
        if (ab == null || !ab.collapseActionView()) {
            return false;
        }
        return true;
    }

    boolean onKeyShortcut(int keyCode, KeyEvent ev) {
        ActionBar ab = getSupportActionBar();
        if (ab != null && ab.onKeyShortcut(keyCode, ev)) {
            return true;
        }
        boolean handled = this.mPreparedPanel;
        if (handled) {
            if (performPanelShortcut(handled, ev.getKeyCode(), ev, 1)) {
                PanelFeatureState panelFeatureState = this.mPreparedPanel;
                if (panelFeatureState != null) {
                    panelFeatureState.isHandled = true;
                }
                return true;
            }
        }
        if (this.mPreparedPanel == null) {
            PanelFeatureState st = getPanelState(0, true);
            preparePanel(st, ev);
            boolean handled2 = performPanelShortcut(st, ev.getKeyCode(), ev, 1);
            st.isPrepared = false;
            if (handled2) {
                return true;
            }
        }
        return false;
    }

    boolean dispatchKeyEvent(KeyEvent event) {
        boolean isDown = true;
        if (event.getKeyCode() == 82) {
            if (this.mOriginalWindowCallback.dispatchKeyEvent(event)) {
                return true;
            }
        }
        int keyCode = event.getKeyCode();
        if (event.getAction() != 0) {
            isDown = false;
        }
        return isDown ? onKeyDown(keyCode, event) : onKeyUp(keyCode, event);
    }

    boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            boolean wasLongPressBackDown = this.mLongPressBackDown;
            this.mLongPressBackDown = false;
            PanelFeatureState st = getPanelState(0, false);
            if (st != null && st.isOpen) {
                if (!wasLongPressBackDown) {
                    closePanel(st, true);
                }
                return true;
            } else if (onBackPressed()) {
                return true;
            }
        } else if (keyCode == 82) {
            onKeyUpPanel(0, event);
            return true;
        }
        return false;
    }

    boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean z = true;
        if (keyCode == 4) {
            if ((event.getFlags() & 128) == 0) {
                z = false;
            }
            this.mLongPressBackDown = z;
        } else if (keyCode == 82) {
            onKeyDownPanel(0, event);
            return true;
        }
        return false;
    }

    public View createView(View parent, String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        boolean z = false;
        if (this.mAppCompatViewInflater == null) {
            String viewInflaterClassName = this.mContext.obtainStyledAttributes(C0286R.styleable.AppCompatTheme).getString(C0286R.styleable.AppCompatTheme_viewInflaterClass);
            if (viewInflaterClassName != null) {
                if (!AppCompatViewInflater.class.getName().equals(viewInflaterClassName)) {
                    try {
                        this.mAppCompatViewInflater = (AppCompatViewInflater) Class.forName(viewInflaterClassName).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                    } catch (Throwable t) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Failed to instantiate custom view inflater ");
                        stringBuilder.append(viewInflaterClassName);
                        stringBuilder.append(". Falling back to default.");
                        Log.i("AppCompatDelegate", stringBuilder.toString(), t);
                        this.mAppCompatViewInflater = new AppCompatViewInflater();
                    }
                }
            }
            this.mAppCompatViewInflater = new AppCompatViewInflater();
        }
        boolean inheritContext = false;
        if (IS_PRE_LOLLIPOP) {
            if (!(attrs instanceof XmlPullParser)) {
                z = shouldInheritContext((ViewParent) parent);
            } else if (((XmlPullParser) attrs).getDepth() > 1) {
                z = true;
            }
            inheritContext = z;
        }
        return this.mAppCompatViewInflater.createView(parent, name, context, attrs, inheritContext, IS_PRE_LOLLIPOP, true, VectorEnabledTintResources.shouldBeUsed());
    }

    private boolean shouldInheritContext(ViewParent parent) {
        if (parent == null) {
            return false;
        }
        ViewParent windowDecor = this.mWindow.getDecorView();
        while (parent != null) {
            if (parent != windowDecor && (parent instanceof View)) {
                if (!ViewCompat.isAttachedToWindow((View) parent)) {
                    parent = parent.getParent();
                }
            }
            return false;
        }
        return true;
    }

    public void installViewFactory() {
        LayoutInflater layoutInflater = LayoutInflater.from(this.mContext);
        if (layoutInflater.getFactory() == null) {
            LayoutInflaterCompat.setFactory2(layoutInflater, this);
        } else if (!(layoutInflater.getFactory2() instanceof AppCompatDelegateImplV9)) {
            Log.i("AppCompatDelegate", "The Activity's LayoutInflater already has a Factory installed so we can not install AppCompat's");
        }
    }

    public final View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View view = callActivityOnCreateView(parent, name, context, attrs);
        if (view != null) {
            return view;
        }
        return createView(parent, name, context, attrs);
    }

    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return onCreateView(null, name, context, attrs);
    }

    View callActivityOnCreateView(View parent, String name, Context context, AttributeSet attrs) {
        if (this.mOriginalWindowCallback instanceof Factory) {
            View result = ((Factory) this.mOriginalWindowCallback).onCreateView(name, context, attrs);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private void openPanel(PanelFeatureState st, KeyEvent event) {
        AppCompatDelegateImplV9 appCompatDelegateImplV9 = this;
        PanelFeatureState panelFeatureState = st;
        if (!panelFeatureState.isOpen) {
            if (!isDestroyed()) {
                if (panelFeatureState.featureId == 0) {
                    if ((appCompatDelegateImplV9.mContext.getResources().getConfiguration().screenLayout & 15) == 4) {
                        return;
                    }
                }
                Window.Callback cb = getWindowCallback();
                if (cb == null || cb.onMenuOpened(panelFeatureState.featureId, panelFeatureState.menu)) {
                    WindowManager wm = (WindowManager) appCompatDelegateImplV9.mContext.getSystemService("window");
                    if (wm != null && preparePanel(st, event)) {
                        LayoutParams lp;
                        WindowManager.LayoutParams layoutParams;
                        int width = -2;
                        if (panelFeatureState.decorView != null) {
                            if (!panelFeatureState.refreshDecorView) {
                                if (panelFeatureState.createdPanelView != null) {
                                    lp = panelFeatureState.createdPanelView.getLayoutParams();
                                    if (lp != null && lp.width == -1) {
                                        width = -1;
                                    }
                                    panelFeatureState.isHandled = false;
                                    layoutParams = new WindowManager.LayoutParams(width, -2, panelFeatureState.f1x, panelFeatureState.f2y, PointerIconCompat.TYPE_HAND, 8519680, -3);
                                    layoutParams.gravity = panelFeatureState.gravity;
                                    layoutParams.windowAnimations = panelFeatureState.windowAnimations;
                                    wm.addView(panelFeatureState.decorView, layoutParams);
                                    panelFeatureState.isOpen = true;
                                    return;
                                }
                                panelFeatureState.isHandled = false;
                                layoutParams = new WindowManager.LayoutParams(width, -2, panelFeatureState.f1x, panelFeatureState.f2y, PointerIconCompat.TYPE_HAND, 8519680, -3);
                                layoutParams.gravity = panelFeatureState.gravity;
                                layoutParams.windowAnimations = panelFeatureState.windowAnimations;
                                wm.addView(panelFeatureState.decorView, layoutParams);
                                panelFeatureState.isOpen = true;
                                return;
                            }
                        }
                        if (panelFeatureState.decorView == null) {
                            if (initializePanelDecor(st)) {
                                if (panelFeatureState.decorView == null) {
                                }
                            }
                            return;
                        } else if (panelFeatureState.refreshDecorView && panelFeatureState.decorView.getChildCount() > 0) {
                            panelFeatureState.decorView.removeAllViews();
                        }
                        if (initializePanelContent(st)) {
                            if (st.hasPanelItems()) {
                                lp = panelFeatureState.shownPanelView.getLayoutParams();
                                if (lp == null) {
                                    lp = new LayoutParams(-2, -2);
                                }
                                panelFeatureState.decorView.setBackgroundResource(panelFeatureState.background);
                                ViewParent shownPanelParent = panelFeatureState.shownPanelView.getParent();
                                if (shownPanelParent != null && (shownPanelParent instanceof ViewGroup)) {
                                    ((ViewGroup) shownPanelParent).removeView(panelFeatureState.shownPanelView);
                                }
                                panelFeatureState.decorView.addView(panelFeatureState.shownPanelView, lp);
                                if (!panelFeatureState.shownPanelView.hasFocus()) {
                                    panelFeatureState.shownPanelView.requestFocus();
                                }
                                panelFeatureState.isHandled = false;
                                layoutParams = new WindowManager.LayoutParams(width, -2, panelFeatureState.f1x, panelFeatureState.f2y, PointerIconCompat.TYPE_HAND, 8519680, -3);
                                layoutParams.gravity = panelFeatureState.gravity;
                                layoutParams.windowAnimations = panelFeatureState.windowAnimations;
                                wm.addView(panelFeatureState.decorView, layoutParams);
                                panelFeatureState.isOpen = true;
                                return;
                            }
                        }
                        return;
                    }
                    return;
                }
                closePanel(panelFeatureState, true);
            }
        }
    }

    private boolean initializePanelDecor(PanelFeatureState st) {
        st.setStyle(getActionBarThemedContext());
        st.decorView = new ListMenuDecorView(st.listPresenterContext);
        st.gravity = 81;
        return true;
    }

    private void reopenMenu(MenuBuilder menu, boolean toggleMenuMode) {
        DecorContentParent decorContentParent = this.mDecorContentParent;
        if (decorContentParent != null && decorContentParent.canShowOverflowMenu()) {
            if (ViewConfiguration.get(this.mContext).hasPermanentMenuKey()) {
                if (this.mDecorContentParent.isOverflowMenuShowPending()) {
                }
            }
            Window.Callback cb = getWindowCallback();
            if (this.mDecorContentParent.isOverflowMenuShowing()) {
                if (toggleMenuMode) {
                    this.mDecorContentParent.hideOverflowMenu();
                    if (!isDestroyed()) {
                        cb.onPanelClosed(108, getPanelState(0, true).menu);
                    }
                    return;
                }
            }
            if (cb != null && !isDestroyed()) {
                if (this.mInvalidatePanelMenuPosted && (this.mInvalidatePanelMenuFeatures & 1) != 0) {
                    this.mWindow.getDecorView().removeCallbacks(this.mInvalidatePanelMenuRunnable);
                    this.mInvalidatePanelMenuRunnable.run();
                }
                PanelFeatureState st = getPanelState(0, true);
                if (st.menu != null && !st.refreshMenuContent) {
                    if (cb.onPreparePanel(0, st.createdPanelView, st.menu)) {
                        cb.onMenuOpened(108, st.menu);
                        this.mDecorContentParent.showOverflowMenu();
                    }
                }
                return;
            }
            return;
        }
        PanelFeatureState st2 = getPanelState(0, true);
        st2.refreshDecorView = true;
        closePanel(st2, false);
        openPanel(st2, null);
    }

    private boolean initializePanelMenu(PanelFeatureState st) {
        Context context = this.mContext;
        if ((st.featureId == 0 || st.featureId == 108) && this.mDecorContentParent != null) {
            TypedValue outValue = new TypedValue();
            Theme baseTheme = context.getTheme();
            baseTheme.resolveAttribute(C0286R.attr.actionBarTheme, outValue, true);
            Theme widgetTheme = null;
            if (outValue.resourceId != 0) {
                widgetTheme = context.getResources().newTheme();
                widgetTheme.setTo(baseTheme);
                widgetTheme.applyStyle(outValue.resourceId, true);
                widgetTheme.resolveAttribute(C0286R.attr.actionBarWidgetTheme, outValue, true);
            } else {
                baseTheme.resolveAttribute(C0286R.attr.actionBarWidgetTheme, outValue, true);
            }
            if (outValue.resourceId != 0) {
                if (widgetTheme == null) {
                    widgetTheme = context.getResources().newTheme();
                    widgetTheme.setTo(baseTheme);
                }
                widgetTheme.applyStyle(outValue.resourceId, true);
            }
            if (widgetTheme != null) {
                context = new ContextThemeWrapper(context, 0);
                context.getTheme().setTo(widgetTheme);
            }
        }
        MenuBuilder menu = new MenuBuilder(context);
        menu.setCallback(this);
        st.setMenu(menu);
        return true;
    }

    private boolean initializePanelContent(PanelFeatureState st) {
        boolean z = true;
        if (st.createdPanelView != null) {
            st.shownPanelView = st.createdPanelView;
            return true;
        } else if (st.menu == null) {
            return false;
        } else {
            if (this.mPanelMenuPresenterCallback == null) {
                this.mPanelMenuPresenterCallback = new PanelMenuPresenterCallback();
            }
            st.shownPanelView = (View) st.getListMenuView(this.mPanelMenuPresenterCallback);
            if (st.shownPanelView == null) {
                z = false;
            }
            return z;
        }
    }

    private boolean preparePanel(PanelFeatureState st, KeyEvent event) {
        if (isDestroyed()) {
            return false;
        }
        if (st.isPrepared) {
            return true;
        }
        boolean isActionBarMenu;
        DecorContentParent decorContentParent;
        DecorContentParent decorContentParent2;
        PanelFeatureState panelFeatureState = this.mPreparedPanel;
        if (panelFeatureState != null && panelFeatureState != st) {
            closePanel(panelFeatureState, false);
        }
        Window.Callback cb = getWindowCallback();
        if (cb != null) {
            st.createdPanelView = cb.onCreatePanelView(st.featureId);
        }
        if (st.featureId != 0) {
            if (st.featureId != 108) {
                isActionBarMenu = false;
                if (isActionBarMenu) {
                    decorContentParent = this.mDecorContentParent;
                    if (decorContentParent != null) {
                        decorContentParent.setMenuPrepared();
                        if (st.createdPanelView != null) {
                            if (!isActionBarMenu) {
                                if (peekSupportActionBar() instanceof ToolbarActionBar) {
                                }
                            }
                            if (st.menu != null) {
                                if (!st.refreshMenuContent) {
                                    st.menu.stopDispatchingItemsChanged();
                                    if (st.frozenActionViewState == null) {
                                        st.menu.restoreActionViewStates(st.frozenActionViewState);
                                        st.frozenActionViewState = null;
                                    }
                                    if (cb.onPreparePanel(0, st.createdPanelView, st.menu)) {
                                        if (isActionBarMenu) {
                                            decorContentParent2 = this.mDecorContentParent;
                                            if (decorContentParent2 != null) {
                                                decorContentParent2.setMenu(null, this.mActionMenuPresenterCallback);
                                                st.menu.startDispatchingItemsChanged();
                                                return false;
                                            }
                                        }
                                        st.menu.startDispatchingItemsChanged();
                                        return false;
                                    }
                                    st.qwertyMode = KeyCharacterMap.load(event == null ? event.getDeviceId() : -1).getKeyboardType() == 1;
                                    st.menu.setQwertyMode(st.qwertyMode);
                                    st.menu.startDispatchingItemsChanged();
                                }
                            }
                            if (st.menu != null) {
                                if (initializePanelMenu(st)) {
                                    if (st.menu != null) {
                                    }
                                }
                                return false;
                            }
                            if (!isActionBarMenu && this.mDecorContentParent != null) {
                                if (this.mActionMenuPresenterCallback == null) {
                                    this.mActionMenuPresenterCallback = new ActionMenuPresenterCallback();
                                }
                                this.mDecorContentParent.setMenu(st.menu, this.mActionMenuPresenterCallback);
                            }
                            st.menu.stopDispatchingItemsChanged();
                            if (cb.onCreatePanelMenu(st.featureId, st.menu)) {
                                st.setMenu(null);
                                if (isActionBarMenu) {
                                    decorContentParent2 = this.mDecorContentParent;
                                    if (decorContentParent2 != null) {
                                        decorContentParent2.setMenu(null, this.mActionMenuPresenterCallback);
                                        return false;
                                    }
                                }
                                return false;
                            }
                            st.refreshMenuContent = false;
                            st.menu.stopDispatchingItemsChanged();
                            if (st.frozenActionViewState == null) {
                                st.menu.restoreActionViewStates(st.frozenActionViewState);
                                st.frozenActionViewState = null;
                            }
                            if (cb.onPreparePanel(0, st.createdPanelView, st.menu)) {
                                if (event == null) {
                                }
                                if (KeyCharacterMap.load(event == null ? event.getDeviceId() : -1).getKeyboardType() == 1) {
                                }
                                st.qwertyMode = KeyCharacterMap.load(event == null ? event.getDeviceId() : -1).getKeyboardType() == 1;
                                st.menu.setQwertyMode(st.qwertyMode);
                                st.menu.startDispatchingItemsChanged();
                            } else {
                                if (isActionBarMenu) {
                                    decorContentParent2 = this.mDecorContentParent;
                                    if (decorContentParent2 != null) {
                                        decorContentParent2.setMenu(null, this.mActionMenuPresenterCallback);
                                        st.menu.startDispatchingItemsChanged();
                                        return false;
                                    }
                                }
                                st.menu.startDispatchingItemsChanged();
                                return false;
                            }
                        }
                        st.isPrepared = true;
                        st.isHandled = false;
                        this.mPreparedPanel = st;
                        return true;
                    }
                }
                if (st.createdPanelView != null) {
                    if (!isActionBarMenu) {
                        if (peekSupportActionBar() instanceof ToolbarActionBar) {
                        }
                    }
                    if (st.menu != null) {
                        if (!st.refreshMenuContent) {
                            st.menu.stopDispatchingItemsChanged();
                            if (st.frozenActionViewState == null) {
                                st.menu.restoreActionViewStates(st.frozenActionViewState);
                                st.frozenActionViewState = null;
                            }
                            if (cb.onPreparePanel(0, st.createdPanelView, st.menu)) {
                                if (isActionBarMenu) {
                                    decorContentParent2 = this.mDecorContentParent;
                                    if (decorContentParent2 != null) {
                                        decorContentParent2.setMenu(null, this.mActionMenuPresenterCallback);
                                        st.menu.startDispatchingItemsChanged();
                                        return false;
                                    }
                                }
                                st.menu.startDispatchingItemsChanged();
                                return false;
                            }
                            if (event == null) {
                            }
                            if (KeyCharacterMap.load(event == null ? event.getDeviceId() : -1).getKeyboardType() == 1) {
                            }
                            st.qwertyMode = KeyCharacterMap.load(event == null ? event.getDeviceId() : -1).getKeyboardType() == 1;
                            st.menu.setQwertyMode(st.qwertyMode);
                            st.menu.startDispatchingItemsChanged();
                        }
                    }
                    if (st.menu != null) {
                        if (initializePanelMenu(st)) {
                            if (st.menu != null) {
                            }
                        }
                        return false;
                    }
                    if (!isActionBarMenu) {
                    }
                    st.menu.stopDispatchingItemsChanged();
                    if (cb.onCreatePanelMenu(st.featureId, st.menu)) {
                        st.refreshMenuContent = false;
                        st.menu.stopDispatchingItemsChanged();
                        if (st.frozenActionViewState == null) {
                            st.menu.restoreActionViewStates(st.frozenActionViewState);
                            st.frozenActionViewState = null;
                        }
                        if (cb.onPreparePanel(0, st.createdPanelView, st.menu)) {
                            if (event == null) {
                            }
                            if (KeyCharacterMap.load(event == null ? event.getDeviceId() : -1).getKeyboardType() == 1) {
                            }
                            st.qwertyMode = KeyCharacterMap.load(event == null ? event.getDeviceId() : -1).getKeyboardType() == 1;
                            st.menu.setQwertyMode(st.qwertyMode);
                            st.menu.startDispatchingItemsChanged();
                        } else {
                            if (isActionBarMenu) {
                                decorContentParent2 = this.mDecorContentParent;
                                if (decorContentParent2 != null) {
                                    decorContentParent2.setMenu(null, this.mActionMenuPresenterCallback);
                                    st.menu.startDispatchingItemsChanged();
                                    return false;
                                }
                            }
                            st.menu.startDispatchingItemsChanged();
                            return false;
                        }
                    }
                    st.setMenu(null);
                    if (isActionBarMenu) {
                        decorContentParent2 = this.mDecorContentParent;
                        if (decorContentParent2 != null) {
                            decorContentParent2.setMenu(null, this.mActionMenuPresenterCallback);
                            return false;
                        }
                    }
                    return false;
                }
                st.isPrepared = true;
                st.isHandled = false;
                this.mPreparedPanel = st;
                return true;
            }
        }
        isActionBarMenu = true;
        if (isActionBarMenu) {
            decorContentParent = this.mDecorContentParent;
            if (decorContentParent != null) {
                decorContentParent.setMenuPrepared();
                if (st.createdPanelView != null) {
                    if (!isActionBarMenu) {
                        if (peekSupportActionBar() instanceof ToolbarActionBar) {
                        }
                    }
                    if (st.menu != null) {
                        if (!st.refreshMenuContent) {
                            st.menu.stopDispatchingItemsChanged();
                            if (st.frozenActionViewState == null) {
                                st.menu.restoreActionViewStates(st.frozenActionViewState);
                                st.frozenActionViewState = null;
                            }
                            if (cb.onPreparePanel(0, st.createdPanelView, st.menu)) {
                                if (isActionBarMenu) {
                                    decorContentParent2 = this.mDecorContentParent;
                                    if (decorContentParent2 != null) {
                                        decorContentParent2.setMenu(null, this.mActionMenuPresenterCallback);
                                        st.menu.startDispatchingItemsChanged();
                                        return false;
                                    }
                                }
                                st.menu.startDispatchingItemsChanged();
                                return false;
                            }
                            if (event == null) {
                            }
                            if (KeyCharacterMap.load(event == null ? event.getDeviceId() : -1).getKeyboardType() == 1) {
                            }
                            st.qwertyMode = KeyCharacterMap.load(event == null ? event.getDeviceId() : -1).getKeyboardType() == 1;
                            st.menu.setQwertyMode(st.qwertyMode);
                            st.menu.startDispatchingItemsChanged();
                        }
                    }
                    if (st.menu != null) {
                        if (initializePanelMenu(st)) {
                            if (st.menu != null) {
                            }
                        }
                        return false;
                    }
                    if (!isActionBarMenu) {
                    }
                    st.menu.stopDispatchingItemsChanged();
                    if (cb.onCreatePanelMenu(st.featureId, st.menu)) {
                        st.setMenu(null);
                        if (isActionBarMenu) {
                            decorContentParent2 = this.mDecorContentParent;
                            if (decorContentParent2 != null) {
                                decorContentParent2.setMenu(null, this.mActionMenuPresenterCallback);
                                return false;
                            }
                        }
                        return false;
                    }
                    st.refreshMenuContent = false;
                    st.menu.stopDispatchingItemsChanged();
                    if (st.frozenActionViewState == null) {
                        st.menu.restoreActionViewStates(st.frozenActionViewState);
                        st.frozenActionViewState = null;
                    }
                    if (cb.onPreparePanel(0, st.createdPanelView, st.menu)) {
                        if (event == null) {
                        }
                        if (KeyCharacterMap.load(event == null ? event.getDeviceId() : -1).getKeyboardType() == 1) {
                        }
                        st.qwertyMode = KeyCharacterMap.load(event == null ? event.getDeviceId() : -1).getKeyboardType() == 1;
                        st.menu.setQwertyMode(st.qwertyMode);
                        st.menu.startDispatchingItemsChanged();
                    } else {
                        if (isActionBarMenu) {
                            decorContentParent2 = this.mDecorContentParent;
                            if (decorContentParent2 != null) {
                                decorContentParent2.setMenu(null, this.mActionMenuPresenterCallback);
                                st.menu.startDispatchingItemsChanged();
                                return false;
                            }
                        }
                        st.menu.startDispatchingItemsChanged();
                        return false;
                    }
                }
                st.isPrepared = true;
                st.isHandled = false;
                this.mPreparedPanel = st;
                return true;
            }
        }
        if (st.createdPanelView != null) {
            if (!isActionBarMenu) {
                if (peekSupportActionBar() instanceof ToolbarActionBar) {
                }
            }
            if (st.menu != null) {
                if (!st.refreshMenuContent) {
                    st.menu.stopDispatchingItemsChanged();
                    if (st.frozenActionViewState == null) {
                        st.menu.restoreActionViewStates(st.frozenActionViewState);
                        st.frozenActionViewState = null;
                    }
                    if (cb.onPreparePanel(0, st.createdPanelView, st.menu)) {
                        if (isActionBarMenu) {
                            decorContentParent2 = this.mDecorContentParent;
                            if (decorContentParent2 != null) {
                                decorContentParent2.setMenu(null, this.mActionMenuPresenterCallback);
                                st.menu.startDispatchingItemsChanged();
                                return false;
                            }
                        }
                        st.menu.startDispatchingItemsChanged();
                        return false;
                    }
                    if (event == null) {
                    }
                    if (KeyCharacterMap.load(event == null ? event.getDeviceId() : -1).getKeyboardType() == 1) {
                    }
                    st.qwertyMode = KeyCharacterMap.load(event == null ? event.getDeviceId() : -1).getKeyboardType() == 1;
                    st.menu.setQwertyMode(st.qwertyMode);
                    st.menu.startDispatchingItemsChanged();
                }
            }
            if (st.menu != null) {
                if (initializePanelMenu(st)) {
                    if (st.menu != null) {
                    }
                }
                return false;
            }
            if (!isActionBarMenu) {
            }
            st.menu.stopDispatchingItemsChanged();
            if (cb.onCreatePanelMenu(st.featureId, st.menu)) {
                st.refreshMenuContent = false;
                st.menu.stopDispatchingItemsChanged();
                if (st.frozenActionViewState == null) {
                    st.menu.restoreActionViewStates(st.frozenActionViewState);
                    st.frozenActionViewState = null;
                }
                if (cb.onPreparePanel(0, st.createdPanelView, st.menu)) {
                    if (event == null) {
                    }
                    if (KeyCharacterMap.load(event == null ? event.getDeviceId() : -1).getKeyboardType() == 1) {
                    }
                    st.qwertyMode = KeyCharacterMap.load(event == null ? event.getDeviceId() : -1).getKeyboardType() == 1;
                    st.menu.setQwertyMode(st.qwertyMode);
                    st.menu.startDispatchingItemsChanged();
                } else {
                    if (isActionBarMenu) {
                        decorContentParent2 = this.mDecorContentParent;
                        if (decorContentParent2 != null) {
                            decorContentParent2.setMenu(null, this.mActionMenuPresenterCallback);
                            st.menu.startDispatchingItemsChanged();
                            return false;
                        }
                    }
                    st.menu.startDispatchingItemsChanged();
                    return false;
                }
            }
            st.setMenu(null);
            if (isActionBarMenu) {
                decorContentParent2 = this.mDecorContentParent;
                if (decorContentParent2 != null) {
                    decorContentParent2.setMenu(null, this.mActionMenuPresenterCallback);
                    return false;
                }
            }
            return false;
        }
        st.isPrepared = true;
        st.isHandled = false;
        this.mPreparedPanel = st;
        return true;
    }

    void checkCloseActionMenu(MenuBuilder menu) {
        if (!this.mClosingActionMenu) {
            this.mClosingActionMenu = true;
            this.mDecorContentParent.dismissPopups();
            Window.Callback cb = getWindowCallback();
            if (cb != null && !isDestroyed()) {
                cb.onPanelClosed(108, menu);
            }
            this.mClosingActionMenu = false;
        }
    }

    void closePanel(int featureId) {
        closePanel(getPanelState(featureId, true), true);
    }

    void closePanel(PanelFeatureState st, boolean doCallback) {
        WindowManager wm;
        if (doCallback && st.featureId == 0) {
            DecorContentParent decorContentParent = this.mDecorContentParent;
            if (decorContentParent != null) {
                if (decorContentParent.isOverflowMenuShowing()) {
                    checkCloseActionMenu(st.menu);
                    return;
                }
                wm = (WindowManager) this.mContext.getSystemService("window");
                if (wm == null && st.isOpen && st.decorView != null) {
                    wm.removeView(st.decorView);
                    if (doCallback) {
                        callOnPanelClosed(st.featureId, st, null);
                    }
                }
                st.isPrepared = false;
                st.isHandled = false;
                st.isOpen = false;
                st.shownPanelView = null;
                st.refreshDecorView = true;
                if (this.mPreparedPanel == st) {
                    this.mPreparedPanel = null;
                }
            }
        }
        wm = (WindowManager) this.mContext.getSystemService("window");
        if (wm == null) {
        }
        st.isPrepared = false;
        st.isHandled = false;
        st.isOpen = false;
        st.shownPanelView = null;
        st.refreshDecorView = true;
        if (this.mPreparedPanel == st) {
            this.mPreparedPanel = null;
        }
    }

    private boolean onKeyDownPanel(int featureId, KeyEvent event) {
        if (event.getRepeatCount() == 0) {
            PanelFeatureState st = getPanelState(featureId, true);
            if (!st.isOpen) {
                return preparePanel(st, event);
            }
        }
        return false;
    }

    private boolean onKeyUpPanel(int featureId, KeyEvent event) {
        if (this.mActionMode != null) {
            return false;
        }
        AudioManager audioManager;
        boolean show;
        boolean handled = false;
        PanelFeatureState st = getPanelState(featureId, true);
        if (featureId == 0) {
            DecorContentParent decorContentParent = this.mDecorContentParent;
            if (decorContentParent != null) {
                if (decorContentParent.canShowOverflowMenu()) {
                    if (!ViewConfiguration.get(this.mContext).hasPermanentMenuKey()) {
                        if (this.mDecorContentParent.isOverflowMenuShowing()) {
                            handled = this.mDecorContentParent.hideOverflowMenu();
                        } else if (!isDestroyed() && preparePanel(st, event)) {
                            handled = this.mDecorContentParent.showOverflowMenu();
                        }
                        if (handled) {
                            audioManager = (AudioManager) this.mContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
                            if (audioManager != null) {
                                audioManager.playSoundEffect(0);
                            } else {
                                Log.w("AppCompatDelegate", "Couldn't get audio manager");
                            }
                        }
                        return handled;
                    }
                }
                if (!st.isOpen) {
                    if (st.isHandled) {
                        if (st.isPrepared) {
                            show = true;
                            if (st.refreshMenuContent) {
                                st.isPrepared = false;
                                show = preparePanel(st, event);
                            }
                            if (show) {
                                openPanel(st, event);
                                handled = true;
                            }
                        }
                        if (handled) {
                            audioManager = (AudioManager) this.mContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
                            if (audioManager != null) {
                                Log.w("AppCompatDelegate", "Couldn't get audio manager");
                            } else {
                                audioManager.playSoundEffect(0);
                            }
                        }
                        return handled;
                    }
                }
                handled = st.isOpen;
                closePanel(st, true);
                if (handled) {
                    audioManager = (AudioManager) this.mContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
                    if (audioManager != null) {
                        audioManager.playSoundEffect(0);
                    } else {
                        Log.w("AppCompatDelegate", "Couldn't get audio manager");
                    }
                }
                return handled;
            }
        }
        if (st.isOpen) {
            if (st.isHandled) {
                if (st.isPrepared) {
                    show = true;
                    if (st.refreshMenuContent) {
                        st.isPrepared = false;
                        show = preparePanel(st, event);
                    }
                    if (show) {
                        openPanel(st, event);
                        handled = true;
                    }
                }
                if (handled) {
                    audioManager = (AudioManager) this.mContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
                    if (audioManager != null) {
                        Log.w("AppCompatDelegate", "Couldn't get audio manager");
                    } else {
                        audioManager.playSoundEffect(0);
                    }
                }
                return handled;
            }
        }
        handled = st.isOpen;
        closePanel(st, true);
        if (handled) {
            audioManager = (AudioManager) this.mContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
            if (audioManager != null) {
                audioManager.playSoundEffect(0);
            } else {
                Log.w("AppCompatDelegate", "Couldn't get audio manager");
            }
        }
        return handled;
    }

    void callOnPanelClosed(int featureId, PanelFeatureState panel, Menu menu) {
        if (menu == null) {
            if (panel == null) {
                if (featureId >= 0) {
                    PanelFeatureState[] panelFeatureStateArr = this.mPanels;
                    if (featureId < panelFeatureStateArr.length) {
                        panel = panelFeatureStateArr[featureId];
                    }
                }
            }
            if (panel != null) {
                menu = panel.menu;
            }
        }
        if (panel == null || panel.isOpen) {
            if (!isDestroyed()) {
                this.mOriginalWindowCallback.onPanelClosed(featureId, menu);
            }
        }
    }

    PanelFeatureState findMenuPanel(Menu menu) {
        PanelFeatureState[] panels = this.mPanels;
        int N = panels != null ? panels.length : 0;
        for (int i = 0; i < N; i++) {
            PanelFeatureState panel = panels[i];
            if (panel != null && panel.menu == menu) {
                return panel;
            }
        }
        return null;
    }

    protected PanelFeatureState getPanelState(int featureId, boolean required) {
        PanelFeatureState st;
        PanelFeatureState[] panelFeatureStateArr = this.mPanels;
        PanelFeatureState[] ar = panelFeatureStateArr;
        if (panelFeatureStateArr != null) {
            if (ar.length > featureId) {
                st = ar[featureId];
                if (st == null) {
                    return st;
                }
                PanelFeatureState panelFeatureState = new PanelFeatureState(featureId);
                st = panelFeatureState;
                ar[featureId] = panelFeatureState;
                return st;
            }
        }
        panelFeatureStateArr = new PanelFeatureState[(featureId + 1)];
        if (ar != null) {
            System.arraycopy(ar, 0, panelFeatureStateArr, 0, ar.length);
        }
        ar = panelFeatureStateArr;
        this.mPanels = panelFeatureStateArr;
        st = ar[featureId];
        if (st == null) {
            return st;
        }
        PanelFeatureState panelFeatureState2 = new PanelFeatureState(featureId);
        st = panelFeatureState2;
        ar[featureId] = panelFeatureState2;
        return st;
    }

    private boolean performPanelShortcut(PanelFeatureState st, int keyCode, KeyEvent event, int flags) {
        if (event.isSystem()) {
            return false;
        }
        boolean handled = false;
        if ((st.isPrepared || preparePanel(st, event)) && st.menu != null) {
            handled = st.menu.performShortcut(keyCode, event, flags);
        }
        if (handled) {
            if ((flags & 1) == 0 && this.mDecorContentParent == null) {
                closePanel(st, true);
            }
        }
        return handled;
    }

    private void invalidatePanelMenu(int featureId) {
        this.mInvalidatePanelMenuFeatures |= 1 << featureId;
        if (!this.mInvalidatePanelMenuPosted) {
            ViewCompat.postOnAnimation(this.mWindow.getDecorView(), this.mInvalidatePanelMenuRunnable);
            this.mInvalidatePanelMenuPosted = true;
        }
    }

    void doInvalidatePanelMenu(int featureId) {
        PanelFeatureState st = getPanelState(featureId, true);
        if (st.menu != null) {
            Bundle savedActionViewStates = new Bundle();
            st.menu.saveActionViewStates(savedActionViewStates);
            if (savedActionViewStates.size() > 0) {
                st.frozenActionViewState = savedActionViewStates;
            }
            st.menu.stopDispatchingItemsChanged();
            st.menu.clear();
        }
        st.refreshMenuContent = true;
        st.refreshDecorView = true;
        if ((featureId == 108 || featureId == 0) && this.mDecorContentParent != null) {
            st = getPanelState(0, false);
            if (st != null) {
                st.isPrepared = false;
                preparePanel(st, null);
            }
        }
    }

    int updateStatusGuard(int insetTop) {
        boolean showStatusGuard = false;
        ActionBarContextView actionBarContextView = this.mActionModeView;
        int i = 0;
        if (actionBarContextView != null) {
            if (actionBarContextView.getLayoutParams() instanceof MarginLayoutParams) {
                MarginLayoutParams mlp = (MarginLayoutParams) this.mActionModeView.getLayoutParams();
                boolean mlpChanged = false;
                if (this.mActionModeView.isShown()) {
                    if (this.mTempRect1 == null) {
                        this.mTempRect1 = new Rect();
                        this.mTempRect2 = new Rect();
                    }
                    Rect insets = this.mTempRect1;
                    Rect localInsets = this.mTempRect2;
                    insets.set(0, insetTop, 0, 0);
                    ViewUtils.computeFitSystemWindows(this.mSubDecor, insets, localInsets);
                    if (mlp.topMargin != (localInsets.top == 0 ? insetTop : 0)) {
                        mlpChanged = true;
                        mlp.topMargin = insetTop;
                        LayoutParams lp = this.mStatusGuard;
                        if (lp == null) {
                            this.mStatusGuard = new View(this.mContext);
                            this.mStatusGuard.setBackgroundColor(this.mContext.getResources().getColor(C0286R.color.abc_input_method_navigation_guard));
                            this.mSubDecor.addView(this.mStatusGuard, -1, new LayoutParams(-1, insetTop));
                        } else {
                            lp = lp.getLayoutParams();
                            if (lp.height != insetTop) {
                                lp.height = insetTop;
                                this.mStatusGuard.setLayoutParams(lp);
                            }
                        }
                    }
                    showStatusGuard = this.mStatusGuard != null;
                    if (!this.mOverlayActionMode && showStatusGuard) {
                        insetTop = 0;
                    }
                } else if (mlp.topMargin != 0) {
                    mlpChanged = true;
                    mlp.topMargin = 0;
                }
                if (mlpChanged) {
                    this.mActionModeView.setLayoutParams(mlp);
                }
            }
        }
        View view = this.mStatusGuard;
        if (view != null) {
            if (!showStatusGuard) {
                i = 8;
            }
            view.setVisibility(i);
        }
        return insetTop;
    }

    private void throwFeatureRequestIfSubDecorInstalled() {
        if (this.mSubDecorInstalled) {
            throw new AndroidRuntimeException("Window feature must be requested before adding content");
        }
    }

    private int sanitizeWindowFeatureId(int featureId) {
        if (featureId == 8) {
            Log.i("AppCompatDelegate", "You should now use the AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR id when requesting this feature.");
            return 108;
        } else if (featureId != 9) {
            return featureId;
        } else {
            Log.i("AppCompatDelegate", "You should now use the AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY id when requesting this feature.");
            return 109;
        }
    }

    ViewGroup getSubDecor() {
        return this.mSubDecor;
    }

    void dismissPopups() {
        DecorContentParent decorContentParent = this.mDecorContentParent;
        if (decorContentParent != null) {
            decorContentParent.dismissPopups();
        }
        if (this.mActionModePopup != null) {
            this.mWindow.getDecorView().removeCallbacks(this.mShowActionModePopup);
            if (this.mActionModePopup.isShowing()) {
                try {
                    this.mActionModePopup.dismiss();
                } catch (IllegalArgumentException e) {
                }
            }
            this.mActionModePopup = null;
        }
        endOnGoingFadeAnimation();
        PanelFeatureState st = getPanelState(0, false);
        if (st != null && st.menu != null) {
            st.menu.close();
        }
    }
}
