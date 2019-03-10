package com.robotium.solo;

import android.app.Instrumentation;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ScrollView;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class ViewFetcher {
    private static Class<?> windowManager;
    private Instrumentation instrumentation;
    private Sleeper sleeper;
    private String windowManagerString;

    public ViewFetcher(Instrumentation instrumentation, Sleeper sleeper) {
        this.instrumentation = instrumentation;
        this.sleeper = sleeper;
        setWindowManagerString();
    }

    public View getTopParent(View view) {
        ViewParent viewParent = view.getParent();
        if (viewParent == null || !(viewParent instanceof View)) {
            return view;
        }
        return getTopParent((View) viewParent);
    }

    public View getScrollOrListParent(View view) {
        if ((view instanceof AbsListView) || (view instanceof ScrollView) || (view instanceof WebView)) {
            return view;
        }
        try {
            return getScrollOrListParent((View) view.getParent());
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<View> getAllViews(boolean onlySufficientlyVisible) {
        View view;
        View[] views = getWindowDecorViews();
        ArrayList<View> allViews = new ArrayList();
        View[] nonDecorViews = getNonDecorViews(views);
        if (nonDecorViews != null) {
            for (View view2 : nonDecorViews) {
                try {
                    addChildren(allViews, (ViewGroup) view2, onlySufficientlyVisible);
                } catch (Exception e) {
                }
                if (view2 != null) {
                    allViews.add(view2);
                }
            }
        }
        if (views != null && views.length > 0) {
            view2 = getRecentDecorView(views);
            try {
                addChildren(allViews, (ViewGroup) view2, onlySufficientlyVisible);
            } catch (Exception e2) {
            }
            if (view2 != null) {
                allViews.add(view2);
            }
        }
        return allViews;
    }

    public final View getRecentDecorView(View[] views) {
        if (views == null) {
            return null;
        }
        View[] decorViews = new View[views.length];
        int i = 0;
        for (View view : views) {
            if (isDecorView(view)) {
                decorViews[i] = view;
                i++;
            }
        }
        return getRecentContainer(decorViews);
    }

    private final View getRecentContainer(View[] views) {
        View container = null;
        long drawingTime = 0;
        for (View view : views) {
            if (view != null && view.isShown() && view.hasWindowFocus() && view.getDrawingTime() > drawingTime) {
                container = view;
                drawingTime = view.getDrawingTime();
            }
        }
        return container;
    }

    private final View[] getNonDecorViews(View[] views) {
        View[] decorViews = null;
        if (views != null) {
            decorViews = new View[views.length];
            int i = 0;
            for (View view : views) {
                if (!isDecorView(view)) {
                    decorViews[i] = view;
                    i++;
                }
            }
        }
        return decorViews;
    }

    private boolean isDecorView(View view) {
        boolean z = false;
        if (view == null) {
            return false;
        }
        String nameOfClass = view.getClass().getName();
        if (!(nameOfClass.equals("com.android.internal.policy.impl.PhoneWindow$DecorView") || nameOfClass.equals("com.android.internal.policy.impl.MultiPhoneWindow$MultiPhoneDecorView"))) {
            if (!nameOfClass.equals("com.android.internal.policy.PhoneWindow$DecorView")) {
                return z;
            }
        }
        z = true;
        return z;
    }

    public ArrayList<View> getViews(View parent, boolean onlySufficientlyVisible) {
        ArrayList<View> views = new ArrayList();
        if (parent == null) {
            return getAllViews(onlySufficientlyVisible);
        }
        View parentToUse = parent;
        views.add(parentToUse);
        if (parentToUse instanceof ViewGroup) {
            addChildren(views, (ViewGroup) parentToUse, onlySufficientlyVisible);
        }
        return views;
    }

    private void addChildren(ArrayList<View> views, ViewGroup viewGroup, boolean onlySufficientlyVisible) {
        if (viewGroup != null) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                if (onlySufficientlyVisible && isViewSufficientlyShown(child)) {
                    views.add(child);
                } else if (!onlySufficientlyVisible && child != null) {
                    views.add(child);
                }
                if (child instanceof ViewGroup) {
                    addChildren(views, (ViewGroup) child, onlySufficientlyVisible);
                }
            }
        }
    }

    public final boolean isViewSufficientlyShown(View view) {
        int[] xyView = new int[2];
        int[] xyParent = new int[2];
        if (view == null) {
            return false;
        }
        float viewHeight = (float) view.getHeight();
        View parent = getScrollOrListParent(view);
        view.getLocationOnScreen(xyView);
        if (parent == null) {
            xyParent[1] = 0;
        } else {
            parent.getLocationOnScreen(xyParent);
        }
        if (((float) xyView[1]) + (viewHeight / 2.0f) <= getScrollListWindowHeight(view) && ((float) xyView[1]) + (viewHeight / 2.0f) >= ((float) xyParent[1])) {
            return true;
        }
        return false;
    }

    public float getScrollListWindowHeight(View view) {
        float windowHeight;
        int[] xyParent = new int[2];
        View parent = getScrollOrListParent(view);
        if (parent == null) {
            windowHeight = (float) ((WindowManager) this.instrumentation.getTargetContext().getSystemService("window")).getDefaultDisplay().getHeight();
        } else {
            parent.getLocationOnScreen(xyParent);
            windowHeight = (float) (xyParent[1] + parent.getHeight());
        }
        return windowHeight;
    }

    public <T extends View> ArrayList<T> getCurrentViews(Class<T> classToFilterBy, boolean includeSubclasses) {
        return getCurrentViews(classToFilterBy, includeSubclasses, null);
    }

    public <T extends View> ArrayList<T> getCurrentViews(Class<T> classToFilterBy, boolean includeSubclasses, View parent) {
        ArrayList<T> filteredViews = new ArrayList();
        for (View view : getViews(parent, true)) {
            if (view != null) {
                Class<? extends View> classOfView = view.getClass();
                if (includeSubclasses) {
                    if (!classToFilterBy.isAssignableFrom(classOfView)) {
                    }
                    filteredViews.add(classToFilterBy.cast(view));
                }
                if (!includeSubclasses && classToFilterBy == classOfView) {
                    filteredViews.add(classToFilterBy.cast(view));
                }
            }
        }
        return filteredViews;
    }

    public final <T extends View> T getFreshestView(ArrayList<T> views) {
        int[] locationOnScreen = new int[2];
        T viewToReturn = null;
        long drawingTime = 0;
        if (views == null) {
            return null;
        }
        Iterator i$ = views.iterator();
        while (i$.hasNext()) {
            T view = (View) i$.next();
            if (view != null) {
                view.getLocationOnScreen(locationOnScreen);
                if (locationOnScreen[0] >= 0) {
                    if (view.getHeight() > 0) {
                        if (view.getDrawingTime() > drawingTime) {
                            drawingTime = view.getDrawingTime();
                            viewToReturn = view;
                        } else if (view.getDrawingTime() == drawingTime) {
                            if (view.isFocused()) {
                                viewToReturn = view;
                            }
                        }
                    }
                }
            }
        }
        return viewToReturn;
    }

    public <T extends View> ViewGroup getRecyclerView(int recyclerViewIndex, int timeOut) {
        long endTime = SystemClock.uptimeMillis() + ((long) timeOut);
        while (SystemClock.uptimeMillis() < endTime) {
            View recyclerView = getRecyclerView(true, recyclerViewIndex);
            if (recyclerView != null) {
                return (ViewGroup) recyclerView;
            }
        }
        return null;
    }

    public View getRecyclerView(boolean shouldSleep, int recyclerViewIndex) {
        Set<View> uniqueViews = new HashSet();
        if (shouldSleep) {
            this.sleeper.sleep();
        }
        Iterator i$ = RobotiumUtils.removeInvisibleViews(RobotiumUtils.filterViewsToSet(new Class[]{ViewGroup.class}, getAllViews(false))).iterator();
        while (i$.hasNext()) {
            View view = (View) i$.next();
            if (isViewType(view.getClass(), "widget.RecyclerView")) {
                uniqueViews.add(view);
            }
            if (uniqueViews.size() > recyclerViewIndex) {
                return (ViewGroup) view;
            }
        }
        return null;
    }

    public List<View> getScrollableSupportPackageViews(boolean shouldSleep) {
        List<View> viewsToReturn = new ArrayList();
        if (shouldSleep) {
            this.sleeper.sleep();
        }
        Iterator i$ = RobotiumUtils.removeInvisibleViews(RobotiumUtils.filterViewsToSet(new Class[]{ViewGroup.class}, getAllViews(true))).iterator();
        while (i$.hasNext()) {
            View view = (View) i$.next();
            if (!isViewType(view.getClass(), "widget.RecyclerView")) {
                if (isViewType(view.getClass(), "widget.NestedScrollView")) {
                }
            }
            viewsToReturn.add(view);
        }
        return viewsToReturn;
    }

    private boolean isViewType(Class<?> aClass, String typeName) {
        if (aClass.getName().contains(typeName)) {
            return true;
        }
        if (aClass.getSuperclass() != null) {
            return isViewType(aClass.getSuperclass(), typeName);
        }
        return false;
    }

    public View getIdenticalView(View view) {
        if (view == null) {
            return null;
        }
        View viewToReturn = null;
        for (View v : RobotiumUtils.removeInvisibleViews(getCurrentViews(view.getClass(), true))) {
            if (areViewsIdentical(v, view)) {
                viewToReturn = v;
                break;
            }
        }
        return viewToReturn;
    }

    private boolean areViewsIdentical(View firstView, View secondView) {
        if (firstView.getId() == secondView.getId()) {
            if (firstView.getClass().isAssignableFrom(secondView.getClass())) {
                if (firstView.getParent() == null || !(firstView.getParent() instanceof View) || secondView.getParent() == null || !(secondView.getParent() instanceof View)) {
                    return true;
                }
                return areViewsIdentical((View) firstView.getParent(), (View) secondView.getParent());
            }
        }
        return false;
    }

    static {
        try {
            String windowManagerClassName;
            if (VERSION.SDK_INT >= 17) {
                windowManagerClassName = "android.view.WindowManagerGlobal";
            } else {
                windowManagerClassName = "android.view.WindowManagerImpl";
            }
            windowManager = Class.forName(windowManagerClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SecurityException e2) {
            e2.printStackTrace();
        }
    }

    public View[] getWindowDecorViews() {
        Exception e;
        Object obj;
        try {
            Field viewsField = windowManager.getDeclaredField("mViews");
            try {
                Field instanceField = windowManager.getDeclaredField(this.windowManagerString);
                try {
                    View[] result;
                    viewsField.setAccessible(true);
                    instanceField.setAccessible(true);
                    Object instance = instanceField.get(null);
                    if (VERSION.SDK_INT >= 19) {
                        result = (View[]) ((ArrayList) viewsField.get(instance)).toArray(new View[0]);
                    } else {
                        result = (View[]) viewsField.get(instance);
                    }
                    return result;
                } catch (Exception e2) {
                    e = e2;
                    e.printStackTrace();
                    return null;
                }
            } catch (Exception e3) {
                e = e3;
                obj = null;
                e.printStackTrace();
                return null;
            }
        } catch (Exception e4) {
            e = e4;
            Object obj2 = null;
            obj = null;
            e.printStackTrace();
            return null;
        }
    }

    private void setWindowManagerString() {
        if (VERSION.SDK_INT >= 17) {
            this.windowManagerString = "sDefaultWindowManager";
        } else if (VERSION.SDK_INT >= 13) {
            this.windowManagerString = "sWindowManager";
        } else {
            this.windowManagerString = "mWindowManager";
        }
    }
}
