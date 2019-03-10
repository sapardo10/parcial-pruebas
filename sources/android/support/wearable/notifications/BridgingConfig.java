package android.support.wearable.notifications;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@TargetApi(24)
public class BridgingConfig {
    private static final String EXTRA_BRIDGING_ENABLED = "android.support.wearable.notifications.extra.bridgingEnabled";
    private static final String EXTRA_EXCLUDED_TAGS = "android.support.wearable.notifications.extra.excludedTags";
    private static final String EXTRA_ORIGINAL_PACKAGE = "android.support.wearable.notifications.extra.originalPackage";
    private static final String TAG = "BridgingConfig";
    private final boolean mBridgingEnabled;
    private final Set<String> mExcludedTags;
    private final String mPackageName;

    public static class Builder {
        private final boolean mBridgingEnabled;
        private final Set<String> mExcludedTags = new HashSet();
        private final String mPackageName;

        public Builder(Context context, boolean bridgingEnabled) {
            this.mPackageName = context.getPackageName();
            this.mBridgingEnabled = bridgingEnabled;
        }

        public Builder addExcludedTag(String tag) {
            this.mExcludedTags.add(tag);
            return this;
        }

        public Builder addExcludedTags(Collection<String> tags) {
            this.mExcludedTags.addAll(tags);
            return this;
        }

        public BridgingConfig build() {
            return new BridgingConfig(this.mPackageName, this.mBridgingEnabled, this.mExcludedTags);
        }
    }

    @VisibleForTesting
    public BridgingConfig(String packageName, boolean bridgingEnabled, Set<String> excludedTags) {
        this.mPackageName = packageName;
        this.mBridgingEnabled = bridgingEnabled;
        this.mExcludedTags = excludedTags;
    }

    public boolean isBridgingEnabled() {
        return this.mBridgingEnabled;
    }

    public Set<String> getExcludedTags() {
        return this.mExcludedTags;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public Bundle toBundle(Context context) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_ORIGINAL_PACKAGE, context.getPackageName());
        bundle.putBoolean(EXTRA_BRIDGING_ENABLED, isBridgingEnabled());
        bundle.putStringArrayList(EXTRA_EXCLUDED_TAGS, new ArrayList(getExcludedTags()));
        return bundle;
    }

    public static BridgingConfig fromBundle(Bundle bundle) {
        return new BridgingConfig(bundle.getString(EXTRA_ORIGINAL_PACKAGE), bundle.getBoolean(EXTRA_BRIDGING_ENABLED), new HashSet(bundle.getStringArrayList(EXTRA_EXCLUDED_TAGS)));
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r6) {
        /*
        r5 = this;
        r0 = 1;
        if (r5 != r6) goto L_0x0004;
    L_0x0003:
        return r0;
    L_0x0004:
        r1 = 0;
        if (r6 == 0) goto L_0x0041;
    L_0x0007:
        r2 = r5.getClass();
        r3 = r6.getClass();
        if (r2 == r3) goto L_0x0012;
    L_0x0011:
        goto L_0x0041;
    L_0x0012:
        r2 = r6;
        r2 = (android.support.wearable.notifications.BridgingConfig) r2;
        r3 = r5.mBridgingEnabled;
        r4 = r2.mBridgingEnabled;
        if (r3 == r4) goto L_0x001c;
    L_0x001b:
        return r1;
    L_0x001c:
        r3 = r5.mPackageName;
        if (r3 == 0) goto L_0x0029;
    L_0x0020:
        r4 = r2.mPackageName;
        r3 = r3.equals(r4);
        if (r3 != 0) goto L_0x002e;
    L_0x0028:
        goto L_0x002d;
    L_0x0029:
        r3 = r2.mPackageName;
        if (r3 == 0) goto L_0x002e;
    L_0x002d:
        return r1;
        r3 = r5.mExcludedTags;
        if (r3 == 0) goto L_0x003a;
    L_0x0033:
        r0 = r2.mExcludedTags;
        r0 = r3.equals(r0);
        goto L_0x0040;
    L_0x003a:
        r3 = r2.mExcludedTags;
        if (r3 != 0) goto L_0x003f;
    L_0x003e:
        goto L_0x0040;
    L_0x003f:
        r0 = 0;
    L_0x0040:
        return r0;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.wearable.notifications.BridgingConfig.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        String str = this.mPackageName;
        int i = 0;
        int hashCode = (((str != null ? str.hashCode() : 0) * 31) + this.mBridgingEnabled) * 31;
        Set set = this.mExcludedTags;
        if (set != null) {
            i = set.hashCode();
        }
        return hashCode + i;
    }

    public String toString() {
        String str = this.mPackageName;
        boolean z = this.mBridgingEnabled;
        String valueOf = String.valueOf(this.mExcludedTags);
        StringBuilder stringBuilder = new StringBuilder((String.valueOf(str).length() + 71) + String.valueOf(valueOf).length());
        stringBuilder.append("BridgingConfig{mPackageName='");
        stringBuilder.append(str);
        stringBuilder.append('\'');
        stringBuilder.append(", mBridgingEnabled=");
        stringBuilder.append(z);
        stringBuilder.append(", mExcludedTags=");
        stringBuilder.append(valueOf);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
