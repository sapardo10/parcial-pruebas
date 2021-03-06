package android.support.v4.app;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.Lifecycle.State;
import android.arch.lifecycle.ViewModelStore;
import android.arch.lifecycle.ViewModelStoreOwner;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.app.ActivityCompat.PermissionCompatDelegate;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.util.SparseArrayCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class FragmentActivity extends BaseFragmentActivityApi16 implements ViewModelStoreOwner, ActivityCompat$OnRequestPermissionsResultCallback, ActivityCompat$RequestPermissionsRequestCodeValidator {
    static final String ALLOCATED_REQUEST_INDICIES_TAG = "android:support:request_indicies";
    static final String FRAGMENTS_TAG = "android:support:fragments";
    static final int MAX_NUM_PENDING_FRAGMENT_ACTIVITY_RESULTS = 65534;
    static final int MSG_REALLY_STOPPED = 1;
    static final int MSG_RESUME_PENDING = 2;
    static final String NEXT_CANDIDATE_REQUEST_INDEX_TAG = "android:support:next_request_index";
    static final String REQUEST_FRAGMENT_WHO_TAG = "android:support:request_fragment_who";
    private static final String TAG = "FragmentActivity";
    boolean mCreated;
    final FragmentController mFragments = FragmentController.createController(new FragmentActivity$HostCallbacks(this));
    final Handler mHandler = new FragmentActivity$1(this);
    LoaderManager mLoaderManager;
    int mNextCandidateRequestIndex;
    SparseArrayCompat<String> mPendingFragmentActivityResults;
    boolean mReallyStopped = true;
    boolean mRequestedPermissionsFromFragment;
    boolean mResumed;
    boolean mRetaining;
    boolean mStopped = true;
    private ViewModelStore mViewModelStore;

    private int allocateRequestIndex(android.support.v4.app.Fragment r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:9:0x0036 in {4, 6, 8} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r4 = this;
        r0 = r4.mPendingFragmentActivityResults;
        r0 = r0.size();
        r1 = 65534; // 0xfffe float:9.1833E-41 double:3.2378E-319;
        if (r0 >= r1) goto L_0x002e;
    L_0x000b:
        r0 = r4.mPendingFragmentActivityResults;
        r2 = r4.mNextCandidateRequestIndex;
        r0 = r0.indexOfKey(r2);
        if (r0 < 0) goto L_0x001d;
    L_0x0015:
        r0 = r4.mNextCandidateRequestIndex;
        r0 = r0 + 1;
        r0 = r0 % r1;
        r4.mNextCandidateRequestIndex = r0;
        goto L_0x000b;
    L_0x001d:
        r0 = r4.mNextCandidateRequestIndex;
        r2 = r4.mPendingFragmentActivityResults;
        r3 = r5.mWho;
        r2.put(r0, r3);
        r2 = r4.mNextCandidateRequestIndex;
        r2 = r2 + 1;
        r2 = r2 % r1;
        r4.mNextCandidateRequestIndex = r2;
        return r0;
    L_0x002e:
        r0 = new java.lang.IllegalStateException;
        r1 = "Too many pending Fragment activity results.";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.app.FragmentActivity.allocateRequestIndex(android.support.v4.app.Fragment):int");
    }

    public /* bridge */ /* synthetic */ View onCreateView(View view, String str, Context context, AttributeSet attributeSet) {
        return super.onCreateView(view, str, context, attributeSet);
    }

    public /* bridge */ /* synthetic */ View onCreateView(String str, Context context, AttributeSet attributeSet) {
        return super.onCreateView(str, context, attributeSet);
    }

    @RequiresApi(16)
    public /* bridge */ /* synthetic */ void startActivityForResult(Intent intent, int i, @Nullable Bundle bundle) {
        super.startActivityForResult(intent, i, bundle);
    }

    public /* bridge */ /* synthetic */ void startIntentSenderForResult(IntentSender intentSender, int i, @Nullable Intent intent, int i2, int i3, int i4) throws SendIntentException {
        super.startIntentSenderForResult(intentSender, i, intent, i2, i3, i4);
    }

    @RequiresApi(16)
    public /* bridge */ /* synthetic */ void startIntentSenderForResult(IntentSender intentSender, int i, @Nullable Intent intent, int i2, int i3, int i4, Bundle bundle) throws SendIntentException {
        super.startIntentSenderForResult(intentSender, i, intent, i2, i3, i4, bundle);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.mFragments.noteStateNotSaved();
        int requestIndex = requestCode >> 16;
        if (requestIndex != 0) {
            requestIndex--;
            String who = (String) this.mPendingFragmentActivityResults.get(requestIndex);
            this.mPendingFragmentActivityResults.remove(requestIndex);
            if (who == null) {
                Log.w(TAG, "Activity result delivered for unknown Fragment.");
                return;
            }
            Fragment targetFragment = this.mFragments.findFragmentByWho(who);
            if (targetFragment == null) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Activity result no fragment exists for who: ");
                stringBuilder.append(who);
                Log.w(str, stringBuilder.toString());
            } else {
                targetFragment.onActivityResult(SupportMenu.USER_MASK & requestCode, resultCode, data);
            }
            return;
        }
        PermissionCompatDelegate delegate = ActivityCompat.getPermissionCompatDelegate();
        if (delegate == null || !delegate.onActivityResult(this, requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onBackPressed() {
        FragmentManager fragmentManager = this.mFragments.getSupportFragmentManager();
        boolean isStateSaved = fragmentManager.isStateSaved();
        if (!isStateSaved || VERSION.SDK_INT > 25) {
            if (!isStateSaved) {
                if (fragmentManager.popBackStackImmediate()) {
                }
            }
            super.onBackPressed();
        }
    }

    public void supportFinishAfterTransition() {
        ActivityCompat.finishAfterTransition(this);
    }

    public void setEnterSharedElementCallback(SharedElementCallback callback) {
        ActivityCompat.setEnterSharedElementCallback(this, callback);
    }

    public void setExitSharedElementCallback(SharedElementCallback listener) {
        ActivityCompat.setExitSharedElementCallback(this, listener);
    }

    public void supportPostponeEnterTransition() {
        ActivityCompat.postponeEnterTransition(this);
    }

    public void supportStartPostponedEnterTransition() {
        ActivityCompat.startPostponedEnterTransition(this);
    }

    @CallSuper
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        this.mFragments.dispatchMultiWindowModeChanged(isInMultiWindowMode);
    }

    @CallSuper
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        this.mFragments.dispatchPictureInPictureModeChanged(isInPictureInPictureMode);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mFragments.noteStateNotSaved();
        this.mFragments.dispatchConfigurationChanged(newConfig);
    }

    @NonNull
    public ViewModelStore getViewModelStore() {
        if (getApplication() != null) {
            if (this.mViewModelStore == null) {
                this.mViewModelStore = new ViewModelStore();
            }
            return this.mViewModelStore;
        }
        throw new IllegalStateException("Your activity is not yet attached to the Application instance. You can't request ViewModel before onCreate call.");
    }

    public Lifecycle getLifecycle() {
        return super.getLifecycle();
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        FragmentManagerNonConfig fragmentManagerNonConfig = null;
        this.mFragments.attachHost(null);
        super.onCreate(savedInstanceState);
        FragmentActivity$NonConfigurationInstances nc = (FragmentActivity$NonConfigurationInstances) getLastNonConfigurationInstance();
        if (nc != null) {
            this.mViewModelStore = nc.viewModelStore;
        }
        if (savedInstanceState != null) {
            Parcelable p = savedInstanceState.getParcelable(FRAGMENTS_TAG);
            FragmentController fragmentController = this.mFragments;
            if (nc != null) {
                fragmentManagerNonConfig = nc.fragments;
            }
            fragmentController.restoreAllState(p, fragmentManagerNonConfig);
            if (savedInstanceState.containsKey(NEXT_CANDIDATE_REQUEST_INDEX_TAG)) {
                this.mNextCandidateRequestIndex = savedInstanceState.getInt(NEXT_CANDIDATE_REQUEST_INDEX_TAG);
                int[] requestCodes = savedInstanceState.getIntArray(ALLOCATED_REQUEST_INDICIES_TAG);
                String[] fragmentWhos = savedInstanceState.getStringArray(REQUEST_FRAGMENT_WHO_TAG);
                if (!(requestCodes == null || fragmentWhos == null)) {
                    if (requestCodes.length == fragmentWhos.length) {
                        this.mPendingFragmentActivityResults = new SparseArrayCompat(requestCodes.length);
                        for (int i = 0; i < requestCodes.length; i++) {
                            this.mPendingFragmentActivityResults.put(requestCodes[i], fragmentWhos[i]);
                        }
                    }
                }
                Log.w(TAG, "Invalid requestCode mapping in savedInstanceState.");
            }
        }
        if (this.mPendingFragmentActivityResults == null) {
            this.mPendingFragmentActivityResults = new SparseArrayCompat();
            this.mNextCandidateRequestIndex = 0;
        }
        this.mFragments.dispatchCreate();
    }

    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == 0) {
            return super.onCreatePanelMenu(featureId, menu) | this.mFragments.dispatchCreateOptionsMenu(menu, getMenuInflater());
        }
        return super.onCreatePanelMenu(featureId, menu);
    }

    final View dispatchFragmentsOnCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return this.mFragments.onCreateView(parent, name, context, attrs);
    }

    protected void onDestroy() {
        super.onDestroy();
        doReallyStop(false);
        ViewModelStore viewModelStore = this.mViewModelStore;
        if (viewModelStore != null && !this.mRetaining) {
            viewModelStore.clear();
        }
        this.mFragments.dispatchDestroy();
    }

    public void onLowMemory() {
        super.onLowMemory();
        this.mFragments.dispatchLowMemory();
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (super.onMenuItemSelected(featureId, item)) {
            return true;
        }
        if (featureId == 0) {
            return this.mFragments.dispatchOptionsItemSelected(item);
        }
        if (featureId != 6) {
            return false;
        }
        return this.mFragments.dispatchContextItemSelected(item);
    }

    public void onPanelClosed(int featureId, Menu menu) {
        if (featureId == 0) {
            this.mFragments.dispatchOptionsMenuClosed(menu);
        }
        super.onPanelClosed(featureId, menu);
    }

    protected void onPause() {
        super.onPause();
        this.mResumed = false;
        if (this.mHandler.hasMessages(2)) {
            this.mHandler.removeMessages(2);
            onResumeFragments();
        }
        this.mFragments.dispatchPause();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.mFragments.noteStateNotSaved();
    }

    public void onStateNotSaved() {
        this.mFragments.noteStateNotSaved();
    }

    protected void onResume() {
        super.onResume();
        this.mHandler.sendEmptyMessage(2);
        this.mResumed = true;
        this.mFragments.execPendingActions();
    }

    protected void onPostResume() {
        super.onPostResume();
        this.mHandler.removeMessages(2);
        onResumeFragments();
        this.mFragments.execPendingActions();
    }

    protected void onResumeFragments() {
        this.mFragments.dispatchResume();
    }

    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        if (featureId != 0 || menu == null) {
            return super.onPreparePanel(featureId, view, menu);
        }
        return onPrepareOptionsPanel(view, menu) | this.mFragments.dispatchPrepareOptionsMenu(menu);
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        return super.onPreparePanel(0, view, menu);
    }

    public final Object onRetainNonConfigurationInstance() {
        if (this.mStopped) {
            doReallyStop(true);
        }
        Object custom = onRetainCustomNonConfigurationInstance();
        FragmentManagerNonConfig fragments = this.mFragments.retainNestedNonConfig();
        if (fragments == null && this.mViewModelStore == null && custom == null) {
            return null;
        }
        FragmentActivity$NonConfigurationInstances nci = new FragmentActivity$NonConfigurationInstances();
        nci.custom = custom;
        nci.viewModelStore = this.mViewModelStore;
        nci.fragments = fragments;
        return nci;
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        markFragmentsCreated();
        Parcelable p = this.mFragments.saveAllState();
        if (p != null) {
            outState.putParcelable(FRAGMENTS_TAG, p);
        }
        if (this.mPendingFragmentActivityResults.size() > 0) {
            outState.putInt(NEXT_CANDIDATE_REQUEST_INDEX_TAG, this.mNextCandidateRequestIndex);
            int[] requestCodes = new int[this.mPendingFragmentActivityResults.size()];
            String[] fragmentWhos = new String[this.mPendingFragmentActivityResults.size()];
            for (int i = 0; i < this.mPendingFragmentActivityResults.size(); i++) {
                requestCodes[i] = this.mPendingFragmentActivityResults.keyAt(i);
                fragmentWhos[i] = (String) this.mPendingFragmentActivityResults.valueAt(i);
            }
            outState.putIntArray(ALLOCATED_REQUEST_INDICIES_TAG, requestCodes);
            outState.putStringArray(REQUEST_FRAGMENT_WHO_TAG, fragmentWhos);
        }
    }

    protected void onStart() {
        super.onStart();
        this.mStopped = false;
        this.mReallyStopped = false;
        this.mHandler.removeMessages(1);
        if (!this.mCreated) {
            this.mCreated = true;
            this.mFragments.dispatchActivityCreated();
        }
        this.mFragments.noteStateNotSaved();
        this.mFragments.execPendingActions();
        this.mFragments.dispatchStart();
    }

    protected void onStop() {
        super.onStop();
        this.mStopped = true;
        markFragmentsCreated();
        this.mHandler.sendEmptyMessage(1);
        this.mFragments.dispatchStop();
    }

    public Object onRetainCustomNonConfigurationInstance() {
        return null;
    }

    public Object getLastCustomNonConfigurationInstance() {
        FragmentActivity$NonConfigurationInstances nc = (FragmentActivity$NonConfigurationInstances) getLastNonConfigurationInstance();
        return nc != null ? nc.custom : null;
    }

    @Deprecated
    public void supportInvalidateOptionsMenu() {
        invalidateOptionsMenu();
    }

    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        writer.print(prefix);
        writer.print("Local FragmentActivity ");
        writer.print(Integer.toHexString(System.identityHashCode(this)));
        writer.println(" State:");
        String innerPrefix = new StringBuilder();
        innerPrefix.append(prefix);
        innerPrefix.append("  ");
        innerPrefix = innerPrefix.toString();
        writer.print(innerPrefix);
        writer.print("mCreated=");
        writer.print(this.mCreated);
        writer.print("mResumed=");
        writer.print(this.mResumed);
        writer.print(" mStopped=");
        writer.print(this.mStopped);
        writer.print(" mReallyStopped=");
        writer.println(this.mReallyStopped);
        LoaderManager loaderManager = this.mLoaderManager;
        if (loaderManager != null) {
            loaderManager.dump(innerPrefix, fd, writer, args);
        }
        this.mFragments.getSupportFragmentManager().dump(prefix, fd, writer, args);
    }

    void doReallyStop(boolean retaining) {
        if (!this.mReallyStopped) {
            this.mReallyStopped = true;
            this.mRetaining = retaining;
            this.mHandler.removeMessages(1);
            onReallyStop();
        }
    }

    void onReallyStop() {
        this.mFragments.dispatchReallyStop();
    }

    public void onAttachFragment(Fragment fragment) {
    }

    public FragmentManager getSupportFragmentManager() {
        return this.mFragments.getSupportFragmentManager();
    }

    public LoaderManager getSupportLoaderManager() {
        LoaderManager loaderManager = this.mLoaderManager;
        if (loaderManager != null) {
            return loaderManager;
        }
        this.mLoaderManager = new LoaderManagerImpl(this, getViewModelStore());
        return this.mLoaderManager;
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        if (!this.mStartedActivityFromFragment) {
            if (requestCode != -1) {
                BaseFragmentActivityApi14.checkForValidRequestCode(requestCode);
            }
        }
        super.startActivityForResult(intent, requestCode);
    }

    public final void validateRequestPermissionsRequestCode(int requestCode) {
        if (!this.mRequestedPermissionsFromFragment && requestCode != -1) {
            BaseFragmentActivityApi14.checkForValidRequestCode(requestCode);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        this.mFragments.noteStateNotSaved();
        int index = (requestCode >> 16) & SupportMenu.USER_MASK;
        if (index != 0) {
            index--;
            String who = (String) this.mPendingFragmentActivityResults.get(index);
            this.mPendingFragmentActivityResults.remove(index);
            if (who == null) {
                Log.w(TAG, "Activity result delivered for unknown Fragment.");
                return;
            }
            Fragment frag = this.mFragments.findFragmentByWho(who);
            if (frag == null) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Activity result no fragment exists for who: ");
                stringBuilder.append(who);
                Log.w(str, stringBuilder.toString());
            } else {
                frag.onRequestPermissionsResult(SupportMenu.USER_MASK & requestCode, permissions, grantResults);
            }
        }
    }

    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
        startActivityFromFragment(fragment, intent, requestCode, null);
    }

    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode, @Nullable Bundle options) {
        this.mStartedActivityFromFragment = true;
        if (requestCode == -1) {
            try {
                ActivityCompat.startActivityForResult(this, intent, -1, options);
            } finally {
                this.mStartedActivityFromFragment = false;
            }
        } else {
            BaseFragmentActivityApi14.checkForValidRequestCode(requestCode);
            ActivityCompat.startActivityForResult(this, intent, ((allocateRequestIndex(fragment) + 1) << 16) + (SupportMenu.USER_MASK & requestCode), options);
            this.mStartedActivityFromFragment = false;
        }
    }

    public void startIntentSenderFromFragment(Fragment fragment, IntentSender intent, int requestCode, @Nullable Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws SendIntentException {
        int i = requestCode;
        this.mStartedIntentSenderFromFragment = true;
        if (i == -1) {
            try {
                ActivityCompat.startIntentSenderForResult(this, intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
            } finally {
                r9.mStartedIntentSenderFromFragment = false;
            }
        } else {
            BaseFragmentActivityApi14.checkForValidRequestCode(requestCode);
            ActivityCompat.startIntentSenderForResult(this, intent, ((allocateRequestIndex(fragment) + 1) << 16) + (SupportMenu.USER_MASK & i), fillInIntent, flagsMask, flagsValues, extraFlags, options);
            r9.mStartedIntentSenderFromFragment = false;
        }
    }

    void requestPermissionsFromFragment(Fragment fragment, String[] permissions, int requestCode) {
        if (requestCode == -1) {
            ActivityCompat.requestPermissions(this, permissions, requestCode);
            return;
        }
        BaseFragmentActivityApi14.checkForValidRequestCode(requestCode);
        try {
            this.mRequestedPermissionsFromFragment = true;
            ActivityCompat.requestPermissions(this, permissions, ((allocateRequestIndex(fragment) + 1) << 16) + (SupportMenu.USER_MASK & requestCode));
        } finally {
            this.mRequestedPermissionsFromFragment = false;
        }
    }

    private void markFragmentsCreated() {
        while (markState(getSupportFragmentManager(), State.CREATED)) {
        }
    }

    private static boolean markState(FragmentManager manager, State state) {
        boolean hadNotMarked = false;
        for (Fragment fragment : manager.getFragments()) {
            if (fragment != null) {
                if (fragment.getLifecycle().getCurrentState().isAtLeast(State.STARTED)) {
                    fragment.mLifecycleRegistry.markState(state);
                    hadNotMarked = true;
                }
                FragmentManager childFragmentManager = fragment.peekChildFragmentManager();
                if (childFragmentManager != null) {
                    hadNotMarked |= markState(childFragmentManager, state);
                }
            }
        }
        return hadNotMarked;
    }
}
