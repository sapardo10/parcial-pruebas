package android.support.v7.preference;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog.Builder;
import java.util.ArrayList;

public class ListPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {
    private static final String SAVE_STATE_ENTRIES = "ListPreferenceDialogFragment.entries";
    private static final String SAVE_STATE_ENTRY_VALUES = "ListPreferenceDialogFragment.entryValues";
    private static final String SAVE_STATE_INDEX = "ListPreferenceDialogFragment.index";
    private int mClickedDialogEntryIndex;
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;

    /* renamed from: android.support.v7.preference.ListPreferenceDialogFragmentCompat$1 */
    class C03061 implements OnClickListener {
        C03061() {
        }

        public void onClick(DialogInterface dialog, int which) {
            ListPreferenceDialogFragmentCompat.this.mClickedDialogEntryIndex = which;
            ListPreferenceDialogFragmentCompat.this.onClick(dialog, -1);
            dialog.dismiss();
        }
    }

    public static ListPreferenceDialogFragmentCompat newInstance(String key) {
        ListPreferenceDialogFragmentCompat fragment = new ListPreferenceDialogFragmentCompat();
        Bundle b = new Bundle(1);
        b.putString("key", key);
        fragment.setArguments(b);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            ListPreference preference = getListPreference();
            if (preference.getEntries() == null || preference.getEntryValues() == null) {
                throw new IllegalStateException("ListPreference requires an entries array and an entryValues array.");
            }
            this.mClickedDialogEntryIndex = preference.findIndexOfValue(preference.getValue());
            this.mEntries = preference.getEntries();
            this.mEntryValues = preference.getEntryValues();
            return;
        }
        this.mClickedDialogEntryIndex = savedInstanceState.getInt(SAVE_STATE_INDEX, 0);
        this.mEntries = getCharSequenceArray(savedInstanceState, SAVE_STATE_ENTRIES);
        this.mEntryValues = getCharSequenceArray(savedInstanceState, SAVE_STATE_ENTRY_VALUES);
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_STATE_INDEX, this.mClickedDialogEntryIndex);
        putCharSequenceArray(outState, SAVE_STATE_ENTRIES, this.mEntries);
        putCharSequenceArray(outState, SAVE_STATE_ENTRY_VALUES, this.mEntryValues);
    }

    private static void putCharSequenceArray(Bundle out, String key, CharSequence[] entries) {
        ArrayList<String> stored = new ArrayList(entries.length);
        for (CharSequence cs : entries) {
            stored.add(cs.toString());
        }
        out.putStringArrayList(key, stored);
    }

    private static CharSequence[] getCharSequenceArray(Bundle in, String key) {
        ArrayList<String> stored = in.getStringArrayList(key);
        return stored == null ? null : (CharSequence[]) stored.toArray(new CharSequence[stored.size()]);
    }

    private ListPreference getListPreference() {
        return (ListPreference) getPreference();
    }

    protected void onPrepareDialogBuilder(Builder builder) {
        super.onPrepareDialogBuilder(builder);
        builder.setSingleChoiceItems(this.mEntries, this.mClickedDialogEntryIndex, new C03061());
        builder.setPositiveButton(null, null);
    }

    public void onDialogClosed(boolean positiveResult) {
        ListPreference preference = getListPreference();
        if (positiveResult) {
            int i = this.mClickedDialogEntryIndex;
            if (i >= 0) {
                String value = this.mEntryValues[i].toString();
                if (preference.callChangeListener(value)) {
                    preference.setValue(value);
                }
            }
        }
    }
}
