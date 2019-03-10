package android.support.transition;

import android.animation.LayoutTransition;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.ViewGroup;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiresApi(14)
class ViewGroupUtilsApi14 implements ViewGroupUtilsImpl {
    private static final int LAYOUT_TRANSITION_CHANGING = 4;
    private static final String TAG = "ViewGroupUtilsApi14";
    private static Method sCancelMethod;
    private static boolean sCancelMethodFetched;
    private static LayoutTransition sEmptyLayoutTransition;
    private static Field sLayoutSuppressedField;
    private static boolean sLayoutSuppressedFieldFetched;

    /* renamed from: android.support.transition.ViewGroupUtilsApi14$1 */
    class C01041 extends LayoutTransition {
        C01041() {
        }

        public boolean isChangingLayout() {
            return true;
        }
    }

    ViewGroupUtilsApi14() {
    }

    public ViewGroupOverlayImpl getOverlay(@NonNull ViewGroup group) {
        return ViewGroupOverlayApi14.createFrom(group);
    }

    public void suppressLayout(@NonNull ViewGroup group, boolean suppress) {
        if (sEmptyLayoutTransition == null) {
            sEmptyLayoutTransition = new C01041();
            sEmptyLayoutTransition.setAnimator(2, null);
            sEmptyLayoutTransition.setAnimator(0, null);
            sEmptyLayoutTransition.setAnimator(1, null);
            sEmptyLayoutTransition.setAnimator(3, null);
            sEmptyLayoutTransition.setAnimator(4, null);
        }
        if (suppress) {
            LayoutTransition layoutTransition = group.getLayoutTransition();
            if (layoutTransition != null) {
                if (layoutTransition.isRunning()) {
                    cancelLayoutTransition(layoutTransition);
                }
                if (layoutTransition != sEmptyLayoutTransition) {
                    group.setTag(C0100R.id.transition_layout_save, layoutTransition);
                }
            }
            group.setLayoutTransition(sEmptyLayoutTransition);
        } else {
            group.setLayoutTransition(null);
            if (!sLayoutSuppressedFieldFetched) {
                try {
                    sLayoutSuppressedField = ViewGroup.class.getDeclaredField("mLayoutSuppressed");
                    sLayoutSuppressedField.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    Log.i(TAG, "Failed to access mLayoutSuppressed field by reflection");
                }
                sLayoutSuppressedFieldFetched = true;
            }
            boolean layoutSuppressed = false;
            Field field = sLayoutSuppressedField;
            if (field != null) {
                try {
                    layoutSuppressed = field.getBoolean(group);
                    if (layoutSuppressed) {
                        sLayoutSuppressedField.setBoolean(group, false);
                    }
                } catch (IllegalAccessException e2) {
                    Log.i(TAG, "Failed to get mLayoutSuppressed field by reflection");
                }
            }
            if (layoutSuppressed) {
                group.requestLayout();
            }
            LayoutTransition layoutTransition2 = (LayoutTransition) group.getTag(C0100R.id.transition_layout_save);
            if (layoutTransition2 != null) {
                group.setTag(C0100R.id.transition_layout_save, null);
                group.setLayoutTransition(layoutTransition2);
            }
        }
    }

    private static void cancelLayoutTransition(LayoutTransition t) {
        if (!sCancelMethodFetched) {
            try {
                sCancelMethod = LayoutTransition.class.getDeclaredMethod("cancel", new Class[0]);
                sCancelMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                Log.i(TAG, "Failed to access cancel method by reflection");
            }
            sCancelMethodFetched = true;
        }
        Method method = sCancelMethod;
        if (method != null) {
            try {
                method.invoke(t, new Object[0]);
            } catch (IllegalAccessException e2) {
                Log.i(TAG, "Failed to access cancel method by reflection");
            } catch (InvocationTargetException e3) {
                Log.i(TAG, "Failed to invoke cancel method by reflection");
            }
        }
    }
}
