package android.support.v7.media;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class MediaRouteSelector {
    public static final MediaRouteSelector EMPTY = new MediaRouteSelector(new Bundle(), null);
    static final String KEY_CONTROL_CATEGORIES = "controlCategories";
    private final Bundle mBundle;
    List<String> mControlCategories;

    public static final class Builder {
        private ArrayList<String> mControlCategories;

        @android.support.annotation.NonNull
        public android.support.v7.media.MediaRouteSelector.Builder addControlCategories(@android.support.annotation.NonNull java.util.Collection<java.lang.String> r3) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:12:0x0027 in {6, 7, 8, 9, 11} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r2 = this;
            if (r3 == 0) goto L_0x001f;
        L_0x0002:
            r0 = r3.isEmpty();
            if (r0 != 0) goto L_0x001d;
        L_0x0008:
            r0 = r3.iterator();
        L_0x000c:
            r1 = r0.hasNext();
            if (r1 == 0) goto L_0x001c;
        L_0x0012:
            r1 = r0.next();
            r1 = (java.lang.String) r1;
            r2.addControlCategory(r1);
            goto L_0x000c;
        L_0x001c:
            goto L_0x001e;
        L_0x001e:
            return r2;
        L_0x001f:
            r0 = new java.lang.IllegalArgumentException;
            r1 = "categories must not be null";
            r0.<init>(r1);
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.media.MediaRouteSelector.Builder.addControlCategories(java.util.Collection):android.support.v7.media.MediaRouteSelector$Builder");
        }

        public Builder(@NonNull MediaRouteSelector selector) {
            if (selector != null) {
                selector.ensureControlCategories();
                if (!selector.mControlCategories.isEmpty()) {
                    this.mControlCategories = new ArrayList(selector.mControlCategories);
                    return;
                }
                return;
            }
            throw new IllegalArgumentException("selector must not be null");
        }

        @NonNull
        public Builder addControlCategory(@NonNull String category) {
            if (category != null) {
                if (this.mControlCategories == null) {
                    this.mControlCategories = new ArrayList();
                }
                if (!this.mControlCategories.contains(category)) {
                    this.mControlCategories.add(category);
                }
                return this;
            }
            throw new IllegalArgumentException("category must not be null");
        }

        @NonNull
        public Builder addSelector(@NonNull MediaRouteSelector selector) {
            if (selector != null) {
                addControlCategories(selector.getControlCategories());
                return this;
            }
            throw new IllegalArgumentException("selector must not be null");
        }

        @NonNull
        public MediaRouteSelector build() {
            if (this.mControlCategories == null) {
                return MediaRouteSelector.EMPTY;
            }
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(MediaRouteSelector.KEY_CONTROL_CATEGORIES, this.mControlCategories);
            return new MediaRouteSelector(bundle, this.mControlCategories);
        }
    }

    MediaRouteSelector(Bundle bundle, List<String> controlCategories) {
        this.mBundle = bundle;
        this.mControlCategories = controlCategories;
    }

    public List<String> getControlCategories() {
        ensureControlCategories();
        return this.mControlCategories;
    }

    void ensureControlCategories() {
        if (this.mControlCategories == null) {
            this.mControlCategories = this.mBundle.getStringArrayList(KEY_CONTROL_CATEGORIES);
            List list = this.mControlCategories;
            if (list != null) {
                if (!list.isEmpty()) {
                    return;
                }
            }
            this.mControlCategories = Collections.emptyList();
        }
    }

    public boolean hasControlCategory(String category) {
        if (category != null) {
            ensureControlCategories();
            int categoryCount = this.mControlCategories.size();
            for (int i = 0; i < categoryCount; i++) {
                if (((String) this.mControlCategories.get(i)).equals(category)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean matchesControlFilters(List<IntentFilter> filters) {
        if (filters != null) {
            ensureControlCategories();
            int categoryCount = this.mControlCategories.size();
            if (categoryCount != 0) {
                int filterCount = filters.size();
                for (int i = 0; i < filterCount; i++) {
                    IntentFilter filter = (IntentFilter) filters.get(i);
                    if (filter != null) {
                        for (int j = 0; j < categoryCount; j++) {
                            if (filter.hasCategory((String) this.mControlCategories.get(j))) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean contains(MediaRouteSelector selector) {
        if (selector == null) {
            return false;
        }
        ensureControlCategories();
        selector.ensureControlCategories();
        return this.mControlCategories.containsAll(selector.mControlCategories);
    }

    public boolean isEmpty() {
        ensureControlCategories();
        return this.mControlCategories.isEmpty();
    }

    public boolean isValid() {
        ensureControlCategories();
        if (this.mControlCategories.contains(null)) {
            return false;
        }
        return true;
    }

    public boolean equals(Object o) {
        if (!(o instanceof MediaRouteSelector)) {
            return false;
        }
        MediaRouteSelector other = (MediaRouteSelector) o;
        ensureControlCategories();
        other.ensureControlCategories();
        return this.mControlCategories.equals(other.mControlCategories);
    }

    public int hashCode() {
        ensureControlCategories();
        return this.mControlCategories.hashCode();
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("MediaRouteSelector{ ");
        result.append("controlCategories=");
        result.append(Arrays.toString(getControlCategories().toArray()));
        result.append(" }");
        return result.toString();
    }

    public Bundle asBundle() {
        return this.mBundle;
    }

    public static MediaRouteSelector fromBundle(@Nullable Bundle bundle) {
        return bundle != null ? new MediaRouteSelector(bundle, null) : null;
    }
}
