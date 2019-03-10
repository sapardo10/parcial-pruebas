package android.support.v7.preference;

import android.os.Bundle;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerViewAccessibilityDelegate;
import android.view.View;

@RestrictTo({Scope.LIBRARY_GROUP})
public class PreferenceRecyclerViewAccessibilityDelegate extends RecyclerViewAccessibilityDelegate {
    final AccessibilityDelegateCompat mDefaultItemDelegate = super.getItemDelegate();
    final AccessibilityDelegateCompat mItemDelegate = new C08701();
    final RecyclerView mRecyclerView;

    /* renamed from: android.support.v7.preference.PreferenceRecyclerViewAccessibilityDelegate$1 */
    class C08701 extends AccessibilityDelegateCompat {
        C08701() {
        }

        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
            PreferenceRecyclerViewAccessibilityDelegate.this.mDefaultItemDelegate.onInitializeAccessibilityNodeInfo(host, info);
            int position = PreferenceRecyclerViewAccessibilityDelegate.this.mRecyclerView.getChildAdapterPosition(host);
            Adapter adapter = PreferenceRecyclerViewAccessibilityDelegate.this.mRecyclerView.getAdapter();
            if (adapter instanceof PreferenceGroupAdapter) {
                Preference preference = ((PreferenceGroupAdapter) adapter).getItem(position);
                if (preference != null) {
                    preference.onInitializeAccessibilityNodeInfo(info);
                }
            }
        }

        public boolean performAccessibilityAction(View host, int action, Bundle args) {
            return PreferenceRecyclerViewAccessibilityDelegate.this.mDefaultItemDelegate.performAccessibilityAction(host, action, args);
        }
    }

    public PreferenceRecyclerViewAccessibilityDelegate(RecyclerView recyclerView) {
        super(recyclerView);
        this.mRecyclerView = recyclerView;
    }

    public AccessibilityDelegateCompat getItemDelegate() {
        return this.mItemDelegate;
    }
}
