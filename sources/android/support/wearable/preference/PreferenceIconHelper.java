package android.support.wearable.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.support.wearable.C0395R;

@TargetApi(23)
public final class PreferenceIconHelper {
    public static void wrapAllIconsInGroup(PreferenceGroup group) {
        for (int i = 0; i < group.getPreferenceCount(); i++) {
            Preference p = group.getPreference(i);
            wrapIcon(p);
            if (p instanceof PreferenceGroup) {
                wrapAllIconsInGroup((PreferenceGroup) p);
            }
        }
    }

    public static void wrapIcon(Preference p) {
        Drawable icon = p.getIcon();
        if (icon != null) {
            p.setIcon(wrapIcon(p.getContext(), icon));
        }
    }

    public static Drawable wrapIcon(Context context, Drawable icon) {
        if (icon instanceof LayerDrawable) {
            if (((LayerDrawable) icon).findDrawableByLayerId(C0395R.id.nested_icon) != null) {
                return icon;
            }
        }
        LayerDrawable wrappedDrawable = (LayerDrawable) context.getDrawable(C0395R.drawable.preference_wrapped_icon);
        wrappedDrawable.setDrawableByLayerId(C0395R.id.nested_icon, icon);
        return wrappedDrawable;
    }

    private PreferenceIconHelper() {
        throw new IllegalStateException("cannot instantiate utility class");
    }
}
