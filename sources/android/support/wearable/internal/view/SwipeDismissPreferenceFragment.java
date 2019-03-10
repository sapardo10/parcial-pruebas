package android.support.wearable.internal.view;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.wearable.view.SwipeDismissFrameLayout;
import android.support.wearable.view.SwipeDismissFrameLayout.Callback;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@TargetApi(11)
public class SwipeDismissPreferenceFragment extends PreferenceFragment {
    private final Callback mCallback = new C09121();
    private SwipeDismissFrameLayout mSwipeLayout;

    /* renamed from: android.support.wearable.internal.view.SwipeDismissPreferenceFragment$1 */
    class C09121 extends Callback {
        C09121() {
        }

        public void onSwipeStart() {
            SwipeDismissPreferenceFragment.this.onSwipeStart();
        }

        public void onSwipeCancelled() {
            SwipeDismissPreferenceFragment.this.onSwipeCancelled();
        }

        public void onDismissed(SwipeDismissFrameLayout layout) {
            SwipeDismissPreferenceFragment.this.onDismiss();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mSwipeLayout = new SwipeDismissFrameLayout(getActivity());
        this.mSwipeLayout.addCallback(this.mCallback);
        View contents = super.onCreateView(inflater, this.mSwipeLayout, savedInstanceState);
        this.mSwipeLayout.setBackgroundColor(getBackgroundColor());
        this.mSwipeLayout.addView(contents);
        return this.mSwipeLayout;
    }

    public void onDismiss() {
    }

    public void onSwipeStart() {
    }

    public void onSwipeCancelled() {
    }

    public void setFocusable(boolean focusable) {
        if (focusable) {
            this.mSwipeLayout.setDescendantFocusability(131072);
            this.mSwipeLayout.setFocusable(true);
            return;
        }
        this.mSwipeLayout.setDescendantFocusability(393216);
        this.mSwipeLayout.setFocusable(false);
        this.mSwipeLayout.clearFocus();
    }

    private int getBackgroundColor() {
        TypedValue value = new TypedValue();
        getActivity().getTheme().resolveAttribute(16842801, value, true);
        return value.data;
    }
}
