package android.support.v7.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.AttributeSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PreferenceGroup extends Preference {
    private boolean mAttachedToHierarchy;
    private final Runnable mClearRecycleCacheRunnable;
    private int mCurrentPreferenceOrder;
    private final Handler mHandler;
    private final SimpleArrayMap<String, Long> mIdRecycleCache;
    private int mInitialExpandedChildrenCount;
    private boolean mOrderingAsAdded;
    private PreferenceInstanceStateCallback mPreferenceInstanceStateCallback;
    private List<Preference> mPreferenceList;

    /* renamed from: android.support.v7.preference.PreferenceGroup$1 */
    class C03131 implements Runnable {
        C03131() {
        }

        public void run() {
            synchronized (this) {
                PreferenceGroup.this.mIdRecycleCache.clear();
            }
        }
    }

    interface PreferenceInstanceStateCallback {
        Parcelable restoreInstanceState(Parcelable parcelable);

        Parcelable saveInstanceState(Parcelable parcelable);
    }

    public interface PreferencePositionCallback {
        int getPreferenceAdapterPosition(Preference preference);

        int getPreferenceAdapterPosition(String str);
    }

    public void removeAll() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:12:0x0020 in {4, 7, 11} preds:[]
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
        r3 = this;
        monitor-enter(r3);
        r0 = r3.mPreferenceList;	 Catch:{ all -> 0x001d }
        r1 = r0.size();	 Catch:{ all -> 0x001d }
        r1 = r1 + -1;	 Catch:{ all -> 0x001d }
    L_0x0009:
        if (r1 < 0) goto L_0x0018;	 Catch:{ all -> 0x001d }
    L_0x000b:
        r2 = 0;	 Catch:{ all -> 0x001d }
        r2 = r0.get(r2);	 Catch:{ all -> 0x001d }
        r2 = (android.support.v7.preference.Preference) r2;	 Catch:{ all -> 0x001d }
        r3.removePreferenceInt(r2);	 Catch:{ all -> 0x001d }
        r1 = r1 + -1;	 Catch:{ all -> 0x001d }
        goto L_0x0009;	 Catch:{ all -> 0x001d }
    L_0x0018:
        monitor-exit(r3);	 Catch:{ all -> 0x001d }
        r3.notifyHierarchyChanged();
        return;
    L_0x001d:
        r0 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x001d }
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.preference.PreferenceGroup.removeAll():void");
    }

    public PreferenceGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mOrderingAsAdded = true;
        this.mCurrentPreferenceOrder = 0;
        this.mAttachedToHierarchy = false;
        this.mInitialExpandedChildrenCount = Integer.MAX_VALUE;
        this.mIdRecycleCache = new SimpleArrayMap();
        this.mHandler = new Handler();
        this.mClearRecycleCacheRunnable = new C03131();
        this.mPreferenceList = new ArrayList();
        TypedArray a = context.obtainStyledAttributes(attrs, C0315R.styleable.PreferenceGroup, defStyleAttr, defStyleRes);
        this.mOrderingAsAdded = TypedArrayUtils.getBoolean(a, C0315R.styleable.PreferenceGroup_orderingFromXml, C0315R.styleable.PreferenceGroup_orderingFromXml, true);
        if (a.hasValue(C0315R.styleable.PreferenceGroup_initialExpandedChildrenCount)) {
            this.mInitialExpandedChildrenCount = TypedArrayUtils.getInt(a, C0315R.styleable.PreferenceGroup_initialExpandedChildrenCount, C0315R.styleable.PreferenceGroup_initialExpandedChildrenCount, -1);
        }
        a.recycle();
    }

    public PreferenceGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PreferenceGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setOrderingAsAdded(boolean orderingAsAdded) {
        this.mOrderingAsAdded = orderingAsAdded;
    }

    public boolean isOrderingAsAdded() {
        return this.mOrderingAsAdded;
    }

    public void setInitialExpandedChildrenCount(int expandedCount) {
        this.mInitialExpandedChildrenCount = expandedCount;
    }

    public int getInitialExpandedChildrenCount() {
        return this.mInitialExpandedChildrenCount;
    }

    public void addItemFromInflater(Preference preference) {
        addPreference(preference);
    }

    public int getPreferenceCount() {
        return this.mPreferenceList.size();
    }

    public Preference getPreference(int index) {
        return (Preference) this.mPreferenceList.get(index);
    }

    public boolean addPreference(Preference preference) {
        if (this.mPreferenceList.contains(preference)) {
            return true;
        }
        int i;
        if (preference.getOrder() == Integer.MAX_VALUE) {
            if (this.mOrderingAsAdded) {
                i = this.mCurrentPreferenceOrder;
                this.mCurrentPreferenceOrder = i + 1;
                preference.setOrder(i);
            }
            if (preference instanceof PreferenceGroup) {
                ((PreferenceGroup) preference).setOrderingAsAdded(this.mOrderingAsAdded);
            }
        }
        i = Collections.binarySearch(this.mPreferenceList, preference);
        if (i < 0) {
            i = (i * -1) - 1;
        }
        if (!onPrepareAddPreference(preference)) {
            return false;
        }
        long id;
        synchronized (this) {
            this.mPreferenceList.add(i, preference);
        }
        PreferenceManager preferenceManager = getPreferenceManager();
        String key = preference.getKey();
        if (key == null || !this.mIdRecycleCache.containsKey(key)) {
            id = preferenceManager.getNextId();
        } else {
            id = ((Long) this.mIdRecycleCache.get(key)).longValue();
            this.mIdRecycleCache.remove(key);
        }
        preference.onAttachedToHierarchy(preferenceManager, id);
        preference.assignParent(this);
        if (this.mAttachedToHierarchy) {
            preference.onAttached();
        }
        notifyHierarchyChanged();
        return true;
    }

    public boolean removePreference(Preference preference) {
        boolean returnValue = removePreferenceInt(preference);
        notifyHierarchyChanged();
        return returnValue;
    }

    private boolean removePreferenceInt(Preference preference) {
        boolean success;
        synchronized (this) {
            preference.onPrepareForRemoval();
            if (preference.getParent() == this) {
                preference.assignParent(null);
            }
            success = this.mPreferenceList.remove(preference);
            if (success) {
                String key = preference.getKey();
                if (key != null) {
                    this.mIdRecycleCache.put(key, Long.valueOf(preference.getId()));
                    this.mHandler.removeCallbacks(this.mClearRecycleCacheRunnable);
                    this.mHandler.post(this.mClearRecycleCacheRunnable);
                }
                if (this.mAttachedToHierarchy) {
                    preference.onDetached();
                }
            }
        }
        return success;
    }

    protected boolean onPrepareAddPreference(Preference preference) {
        preference.onParentChanged(this, shouldDisableDependents());
        return true;
    }

    public Preference findPreference(CharSequence key) {
        if (TextUtils.equals(getKey(), key)) {
            return this;
        }
        int preferenceCount = getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = getPreference(i);
            String curKey = preference.getKey();
            if (curKey != null && curKey.equals(key)) {
                return preference;
            }
            if (preference instanceof PreferenceGroup) {
                Preference returnedPreference = ((PreferenceGroup) preference).findPreference(key);
                if (returnedPreference != null) {
                    return returnedPreference;
                }
            }
        }
        return null;
    }

    protected boolean isOnSameScreenAsChildren() {
        return true;
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public boolean isAttached() {
        return this.mAttachedToHierarchy;
    }

    public void onAttached() {
        super.onAttached();
        this.mAttachedToHierarchy = true;
        int preferenceCount = getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            getPreference(i).onAttached();
        }
    }

    public void onDetached() {
        super.onDetached();
        this.mAttachedToHierarchy = false;
        int preferenceCount = getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            getPreference(i).onDetached();
        }
    }

    public void notifyDependencyChange(boolean disableDependents) {
        super.notifyDependencyChange(disableDependents);
        int preferenceCount = getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            getPreference(i).onParentChanged(this, disableDependents);
        }
    }

    void sortPreferences() {
        synchronized (this) {
            Collections.sort(this.mPreferenceList);
        }
    }

    protected void dispatchSaveInstanceState(Bundle container) {
        super.dispatchSaveInstanceState(container);
        int preferenceCount = getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            getPreference(i).dispatchSaveInstanceState(container);
        }
    }

    protected void dispatchRestoreInstanceState(Bundle container) {
        super.dispatchRestoreInstanceState(container);
        int preferenceCount = getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            getPreference(i).dispatchRestoreInstanceState(container);
        }
    }

    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        PreferenceInstanceStateCallback preferenceInstanceStateCallback = this.mPreferenceInstanceStateCallback;
        if (preferenceInstanceStateCallback != null) {
            return preferenceInstanceStateCallback.saveInstanceState(superState);
        }
        return superState;
    }

    protected void onRestoreInstanceState(Parcelable state) {
        PreferenceInstanceStateCallback preferenceInstanceStateCallback = this.mPreferenceInstanceStateCallback;
        if (preferenceInstanceStateCallback != null) {
            state = preferenceInstanceStateCallback.restoreInstanceState(state);
        }
        super.onRestoreInstanceState(state);
    }

    final void setPreferenceInstanceStateCallback(PreferenceInstanceStateCallback callback) {
        this.mPreferenceInstanceStateCallback = callback;
    }

    @VisibleForTesting
    final PreferenceInstanceStateCallback getPreferenceInstanceStateCallback() {
        return this.mPreferenceInstanceStateCallback;
    }
}
