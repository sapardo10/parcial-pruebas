package android.support.v7.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.content.ContextCompat;

public class PreferenceManager {
    public static final String KEY_HAS_SET_DEFAULT_VALUES = "_has_set_default_values";
    private static final int STORAGE_DEFAULT = 0;
    private static final int STORAGE_DEVICE_PROTECTED = 1;
    private Context mContext;
    @Nullable
    private Editor mEditor;
    private long mNextId = 0;
    private boolean mNoCommit;
    private OnDisplayPreferenceDialogListener mOnDisplayPreferenceDialogListener;
    private OnNavigateToScreenListener mOnNavigateToScreenListener;
    private OnPreferenceTreeClickListener mOnPreferenceTreeClickListener;
    private PreferenceComparisonCallback mPreferenceComparisonCallback;
    @Nullable
    private PreferenceDataStore mPreferenceDataStore;
    private PreferenceScreen mPreferenceScreen;
    @Nullable
    private SharedPreferences mSharedPreferences;
    private int mSharedPreferencesMode;
    private String mSharedPreferencesName;
    private int mStorage = 0;

    public interface OnDisplayPreferenceDialogListener {
        void onDisplayPreferenceDialog(Preference preference);
    }

    public interface OnNavigateToScreenListener {
        void onNavigateToScreen(PreferenceScreen preferenceScreen);
    }

    public interface OnPreferenceTreeClickListener {
        boolean onPreferenceTreeClick(Preference preference);
    }

    public static abstract class PreferenceComparisonCallback {
        public abstract boolean arePreferenceContentsTheSame(Preference preference, Preference preference2);

        public abstract boolean arePreferenceItemsTheSame(Preference preference, Preference preference2);
    }

    public static class SimplePreferenceComparisonCallback extends PreferenceComparisonCallback {
        public boolean arePreferenceItemsTheSame(Preference p1, Preference p2) {
            return p1.getId() == p2.getId();
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean arePreferenceContentsTheSame(android.support.v7.preference.Preference r6, android.support.v7.preference.Preference r7) {
            /*
            r5 = this;
            r0 = r6.getClass();
            r1 = r7.getClass();
            r2 = 0;
            if (r0 == r1) goto L_0x000c;
        L_0x000b:
            return r2;
        L_0x000c:
            if (r6 != r7) goto L_0x0015;
        L_0x000e:
            r0 = r6.wasDetached();
            if (r0 == 0) goto L_0x0015;
        L_0x0014:
            return r2;
            r0 = r6.getTitle();
            r1 = r7.getTitle();
            r0 = android.text.TextUtils.equals(r0, r1);
            if (r0 != 0) goto L_0x0025;
        L_0x0024:
            return r2;
        L_0x0025:
            r0 = r6.getSummary();
            r1 = r7.getSummary();
            r0 = android.text.TextUtils.equals(r0, r1);
            if (r0 != 0) goto L_0x0034;
        L_0x0033:
            return r2;
        L_0x0034:
            r0 = r6.getIcon();
            r1 = r7.getIcon();
            if (r0 == r1) goto L_0x0047;
        L_0x003e:
            if (r0 == 0) goto L_0x0046;
        L_0x0040:
            r3 = r0.equals(r1);
            if (r3 != 0) goto L_0x0047;
        L_0x0046:
            return r2;
            r3 = r6.isEnabled();
            r4 = r7.isEnabled();
            if (r3 == r4) goto L_0x0053;
        L_0x0052:
            return r2;
        L_0x0053:
            r3 = r6.isSelectable();
            r4 = r7.isSelectable();
            if (r3 == r4) goto L_0x005e;
        L_0x005d:
            return r2;
        L_0x005e:
            r3 = r6 instanceof android.support.v7.preference.TwoStatePreference;
            if (r3 == 0) goto L_0x0074;
        L_0x0062:
            r3 = r6;
            r3 = (android.support.v7.preference.TwoStatePreference) r3;
            r3 = r3.isChecked();
            r4 = r7;
            r4 = (android.support.v7.preference.TwoStatePreference) r4;
            r4 = r4.isChecked();
            if (r3 == r4) goto L_0x0073;
        L_0x0072:
            return r2;
        L_0x0073:
            goto L_0x0075;
        L_0x0075:
            r3 = r6 instanceof android.support.v7.preference.DropDownPreference;
            if (r3 == 0) goto L_0x007c;
        L_0x0079:
            if (r6 == r7) goto L_0x007c;
        L_0x007b:
            return r2;
            r2 = 1;
            return r2;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.preference.PreferenceManager.SimplePreferenceComparisonCallback.arePreferenceContentsTheSame(android.support.v7.preference.Preference, android.support.v7.preference.Preference):boolean");
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public PreferenceManager(Context context) {
        this.mContext = context;
        setSharedPreferencesName(getDefaultSharedPreferencesName(context));
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public PreferenceScreen inflateFromResource(Context context, int resId, PreferenceScreen rootPreferences) {
        setNoCommit(true);
        rootPreferences = (PreferenceScreen) new PreferenceInflater(context, this).inflate(resId, (PreferenceGroup) rootPreferences);
        rootPreferences.onAttachedToHierarchy(this);
        setNoCommit(false);
        return rootPreferences;
    }

    public PreferenceScreen createPreferenceScreen(Context context) {
        PreferenceScreen preferenceScreen = new PreferenceScreen(context, null);
        preferenceScreen.onAttachedToHierarchy(this);
        return preferenceScreen;
    }

    long getNextId() {
        long j;
        synchronized (this) {
            j = this.mNextId;
            this.mNextId = 1 + j;
        }
        return j;
    }

    public String getSharedPreferencesName() {
        return this.mSharedPreferencesName;
    }

    public void setSharedPreferencesName(String sharedPreferencesName) {
        this.mSharedPreferencesName = sharedPreferencesName;
        this.mSharedPreferences = null;
    }

    public int getSharedPreferencesMode() {
        return this.mSharedPreferencesMode;
    }

    public void setSharedPreferencesMode(int sharedPreferencesMode) {
        this.mSharedPreferencesMode = sharedPreferencesMode;
        this.mSharedPreferences = null;
    }

    public void setStorageDefault() {
        if (VERSION.SDK_INT >= 24) {
            this.mStorage = 0;
            this.mSharedPreferences = null;
        }
    }

    public void setStorageDeviceProtected() {
        if (VERSION.SDK_INT >= 24) {
            this.mStorage = 1;
            this.mSharedPreferences = null;
        }
    }

    public boolean isStorageDefault() {
        boolean z = true;
        if (VERSION.SDK_INT < 24) {
            return true;
        }
        if (this.mStorage != 0) {
            z = false;
        }
        return z;
    }

    public boolean isStorageDeviceProtected() {
        boolean z = false;
        if (VERSION.SDK_INT < 24) {
            return false;
        }
        if (this.mStorage == 1) {
            z = true;
        }
        return z;
    }

    public void setPreferenceDataStore(PreferenceDataStore dataStore) {
        this.mPreferenceDataStore = dataStore;
    }

    @Nullable
    public PreferenceDataStore getPreferenceDataStore() {
        return this.mPreferenceDataStore;
    }

    public SharedPreferences getSharedPreferences() {
        if (getPreferenceDataStore() != null) {
            return null;
        }
        if (this.mSharedPreferences == null) {
            Context storageContext;
            if (this.mStorage != 1) {
                storageContext = this.mContext;
            } else {
                storageContext = ContextCompat.createDeviceProtectedStorageContext(this.mContext);
            }
            this.mSharedPreferences = storageContext.getSharedPreferences(this.mSharedPreferencesName, this.mSharedPreferencesMode);
        }
        return this.mSharedPreferences;
    }

    public static SharedPreferences getDefaultSharedPreferences(Context context) {
        return context.getSharedPreferences(getDefaultSharedPreferencesName(context), getDefaultSharedPreferencesMode());
    }

    private static String getDefaultSharedPreferencesName(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(context.getPackageName());
        stringBuilder.append("_preferences");
        return stringBuilder.toString();
    }

    private static int getDefaultSharedPreferencesMode() {
        return 0;
    }

    public PreferenceScreen getPreferenceScreen() {
        return this.mPreferenceScreen;
    }

    public boolean setPreferences(PreferenceScreen preferenceScreen) {
        PreferenceScreen preferenceScreen2 = this.mPreferenceScreen;
        if (preferenceScreen == preferenceScreen2) {
            return false;
        }
        if (preferenceScreen2 != null) {
            preferenceScreen2.onDetached();
        }
        this.mPreferenceScreen = preferenceScreen;
        return true;
    }

    public Preference findPreference(CharSequence key) {
        PreferenceScreen preferenceScreen = this.mPreferenceScreen;
        if (preferenceScreen == null) {
            return null;
        }
        return preferenceScreen.findPreference(key);
    }

    public static void setDefaultValues(Context context, int resId, boolean readAgain) {
        setDefaultValues(context, getDefaultSharedPreferencesName(context), getDefaultSharedPreferencesMode(), resId, readAgain);
    }

    public static void setDefaultValues(Context context, String sharedPreferencesName, int sharedPreferencesMode, int resId, boolean readAgain) {
        SharedPreferences defaultValueSp = context.getSharedPreferences(KEY_HAS_SET_DEFAULT_VALUES, 0);
        if (!readAgain) {
            if (defaultValueSp.getBoolean(KEY_HAS_SET_DEFAULT_VALUES, false)) {
                return;
            }
        }
        PreferenceManager pm = new PreferenceManager(context);
        pm.setSharedPreferencesName(sharedPreferencesName);
        pm.setSharedPreferencesMode(sharedPreferencesMode);
        pm.inflateFromResource(context, resId, null);
        defaultValueSp.edit().putBoolean(KEY_HAS_SET_DEFAULT_VALUES, true).apply();
    }

    Editor getEditor() {
        if (this.mPreferenceDataStore != null) {
            return null;
        }
        if (!this.mNoCommit) {
            return getSharedPreferences().edit();
        }
        if (this.mEditor == null) {
            this.mEditor = getSharedPreferences().edit();
        }
        return this.mEditor;
    }

    boolean shouldCommit() {
        return this.mNoCommit ^ 1;
    }

    private void setNoCommit(boolean noCommit) {
        if (!noCommit) {
            Editor editor = this.mEditor;
            if (editor != null) {
                editor.apply();
                this.mNoCommit = noCommit;
            }
        }
        this.mNoCommit = noCommit;
    }

    public Context getContext() {
        return this.mContext;
    }

    public PreferenceComparisonCallback getPreferenceComparisonCallback() {
        return this.mPreferenceComparisonCallback;
    }

    public void setPreferenceComparisonCallback(PreferenceComparisonCallback preferenceComparisonCallback) {
        this.mPreferenceComparisonCallback = preferenceComparisonCallback;
    }

    public OnDisplayPreferenceDialogListener getOnDisplayPreferenceDialogListener() {
        return this.mOnDisplayPreferenceDialogListener;
    }

    public void setOnDisplayPreferenceDialogListener(OnDisplayPreferenceDialogListener onDisplayPreferenceDialogListener) {
        this.mOnDisplayPreferenceDialogListener = onDisplayPreferenceDialogListener;
    }

    public void showDialog(Preference preference) {
        OnDisplayPreferenceDialogListener onDisplayPreferenceDialogListener = this.mOnDisplayPreferenceDialogListener;
        if (onDisplayPreferenceDialogListener != null) {
            onDisplayPreferenceDialogListener.onDisplayPreferenceDialog(preference);
        }
    }

    public void setOnPreferenceTreeClickListener(OnPreferenceTreeClickListener listener) {
        this.mOnPreferenceTreeClickListener = listener;
    }

    public OnPreferenceTreeClickListener getOnPreferenceTreeClickListener() {
        return this.mOnPreferenceTreeClickListener;
    }

    public void setOnNavigateToScreenListener(OnNavigateToScreenListener listener) {
        this.mOnNavigateToScreenListener = listener;
    }

    public OnNavigateToScreenListener getOnNavigateToScreenListener() {
        return this.mOnNavigateToScreenListener;
    }
}
