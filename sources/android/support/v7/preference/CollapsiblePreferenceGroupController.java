package android.support.v7.preference;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v7.preference.Preference.BaseSavedState;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

final class CollapsiblePreferenceGroupController implements PreferenceInstanceStateCallback {
    private final Context mContext;
    private int mMaxPreferenceToShow;
    private final PreferenceGroupAdapter mPreferenceGroupAdapter;

    /* renamed from: android.support.v7.preference.CollapsiblePreferenceGroupController$1 */
    class C08681 implements OnPreferenceClickListener {
        C08681() {
        }

        public boolean onPreferenceClick(Preference preference) {
            CollapsiblePreferenceGroupController.this.mMaxPreferenceToShow = Integer.MAX_VALUE;
            CollapsiblePreferenceGroupController.this.mPreferenceGroupAdapter.onPreferenceHierarchyChange(preference);
            return true;
        }
    }

    static class ExpandButton extends Preference {
        ExpandButton(Context context, List<Preference> visiblePreferenceList, List<Preference> flattenedPreferenceList) {
            super(context);
            initLayout();
            setSummary(visiblePreferenceList, flattenedPreferenceList);
        }

        private void initLayout() {
            setLayoutResource(C0315R.layout.expand_button);
            setIcon(C0315R.drawable.ic_arrow_down_24dp);
            setTitle(C0315R.string.expand_button_title);
            setOrder(999);
        }

        private void setSummary(List<Preference> visiblePreferenceList, List<Preference> flattenedPreferenceList) {
            CharSequence summary = null;
            for (int i = flattenedPreferenceList.indexOf((Preference) visiblePreferenceList.get(visiblePreferenceList.size() - 1)) + 1; i < flattenedPreferenceList.size(); i++) {
                Preference preference = (Preference) flattenedPreferenceList.get(i);
                if (!(preference instanceof PreferenceGroup)) {
                    if (preference.isVisible()) {
                        CharSequence title = preference.getTitle();
                        if (!TextUtils.isEmpty(title)) {
                            if (summary == null) {
                                summary = title;
                            } else {
                                summary = getContext().getString(C0315R.string.summary_collapsed_preference_list, new Object[]{summary, title});
                            }
                        }
                    }
                }
            }
            setSummary(summary);
        }

        public void onBindViewHolder(PreferenceViewHolder holder) {
            super.onBindViewHolder(holder);
            holder.setDividerAllowedAbove(false);
        }
    }

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new C03021();
        int mMaxPreferenceToShow;

        /* renamed from: android.support.v7.preference.CollapsiblePreferenceGroupController$SavedState$1 */
        static class C03021 implements Creator<SavedState> {
            C03021() {
            }

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        }

        SavedState(Parcel source) {
            super(source);
            this.mMaxPreferenceToShow = source.readInt();
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.mMaxPreferenceToShow);
        }

        SavedState(Parcelable superState) {
            super(superState);
        }
    }

    CollapsiblePreferenceGroupController(PreferenceGroup preferenceGroup, PreferenceGroupAdapter preferenceGroupAdapter) {
        this.mPreferenceGroupAdapter = preferenceGroupAdapter;
        this.mMaxPreferenceToShow = preferenceGroup.getInitialExpandedChildrenCount();
        this.mContext = preferenceGroup.getContext();
        preferenceGroup.setPreferenceInstanceStateCallback(this);
    }

    public List<Preference> createVisiblePreferencesList(List<Preference> flattenedPreferenceList) {
        int visiblePreferenceCount = 0;
        List<Preference> visiblePreferenceList = new ArrayList(flattenedPreferenceList.size());
        for (Preference preference : flattenedPreferenceList) {
            if (preference.isVisible()) {
                if (visiblePreferenceCount < this.mMaxPreferenceToShow) {
                    visiblePreferenceList.add(preference);
                }
                if (!(preference instanceof PreferenceGroup)) {
                    visiblePreferenceCount++;
                }
            }
        }
        if (showLimitedChildren() && visiblePreferenceCount > this.mMaxPreferenceToShow) {
            visiblePreferenceList.add(createExpandButton(visiblePreferenceList, flattenedPreferenceList));
        }
        return visiblePreferenceList;
    }

    public boolean onPreferenceVisibilityChange(Preference preference) {
        if (!showLimitedChildren()) {
            return false;
        }
        this.mPreferenceGroupAdapter.onPreferenceHierarchyChange(preference);
        return true;
    }

    public Parcelable saveInstanceState(Parcelable state) {
        SavedState myState = new SavedState(state);
        myState.mMaxPreferenceToShow = this.mMaxPreferenceToShow;
        return myState;
    }

    public Parcelable restoreInstanceState(Parcelable state) {
        if (state != null) {
            if (state.getClass().equals(SavedState.class)) {
                SavedState myState = (SavedState) state;
                int restoredMaxToShow = myState.mMaxPreferenceToShow;
                if (this.mMaxPreferenceToShow != restoredMaxToShow) {
                    this.mMaxPreferenceToShow = restoredMaxToShow;
                    this.mPreferenceGroupAdapter.onPreferenceHierarchyChange(null);
                }
                return myState.getSuperState();
            }
        }
        return state;
    }

    private ExpandButton createExpandButton(List<Preference> visiblePreferenceList, List<Preference> flattenedPreferenceList) {
        ExpandButton preference = new ExpandButton(this.mContext, visiblePreferenceList, flattenedPreferenceList);
        preference.setOnPreferenceClickListener(new C08681());
        return preference;
    }

    private boolean showLimitedChildren() {
        return this.mMaxPreferenceToShow != Integer.MAX_VALUE;
    }
}
