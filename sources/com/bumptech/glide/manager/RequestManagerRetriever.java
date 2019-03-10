package com.bumptech.glide.manager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RequestManagerRetriever implements Callback {
    private static final RequestManagerFactory DEFAULT_FACTORY = new C09691();
    private static final String FRAGMENT_INDEX_KEY = "key";
    @VisibleForTesting
    static final String FRAGMENT_TAG = "com.bumptech.glide.manager";
    private static final int ID_REMOVE_FRAGMENT_MANAGER = 1;
    private static final int ID_REMOVE_SUPPORT_FRAGMENT_MANAGER = 2;
    private static final String TAG = "RMRetriever";
    private volatile RequestManager applicationManager;
    private final RequestManagerFactory factory;
    private final Handler handler;
    @VisibleForTesting
    final Map<FragmentManager, RequestManagerFragment> pendingRequestManagerFragments = new HashMap();
    @VisibleForTesting
    final Map<android.support.v4.app.FragmentManager, SupportRequestManagerFragment> pendingSupportRequestManagerFragments = new HashMap();
    private final Bundle tempBundle = new Bundle();
    private final ArrayMap<View, Fragment> tempViewToFragment = new ArrayMap();
    private final ArrayMap<View, android.support.v4.app.Fragment> tempViewToSupportFragment = new ArrayMap();

    public interface RequestManagerFactory {
        @NonNull
        RequestManager build(@NonNull Glide glide, @NonNull Lifecycle lifecycle, @NonNull RequestManagerTreeNode requestManagerTreeNode, @NonNull Context context);
    }

    /* renamed from: com.bumptech.glide.manager.RequestManagerRetriever$1 */
    class C09691 implements RequestManagerFactory {
        C09691() {
        }

        @NonNull
        public RequestManager build(@NonNull Glide glide, @NonNull Lifecycle lifecycle, @NonNull RequestManagerTreeNode requestManagerTreeNode, @NonNull Context context) {
            return new RequestManager(glide, lifecycle, requestManagerTreeNode, context);
        }
    }

    public RequestManagerRetriever(@Nullable RequestManagerFactory factory) {
        this.factory = factory != null ? factory : DEFAULT_FACTORY;
        this.handler = new Handler(Looper.getMainLooper(), this);
    }

    @NonNull
    private RequestManager getApplicationManager(@NonNull Context context) {
        if (this.applicationManager == null) {
            synchronized (this) {
                if (this.applicationManager == null) {
                    this.applicationManager = this.factory.build(Glide.get(context.getApplicationContext()), new ApplicationLifecycle(), new EmptyRequestManagerTreeNode(), context.getApplicationContext());
                }
            }
        }
        return this.applicationManager;
    }

    @NonNull
    public RequestManager get(@NonNull Context context) {
        if (context != null) {
            if (Util.isOnMainThread() && !(context instanceof Application)) {
                if (context instanceof FragmentActivity) {
                    return get((FragmentActivity) context);
                }
                if (context instanceof Activity) {
                    return get((Activity) context);
                }
                if (context instanceof ContextWrapper) {
                    return get(((ContextWrapper) context).getBaseContext());
                }
            }
            return getApplicationManager(context);
        }
        throw new IllegalArgumentException("You cannot start a load on a null Context");
    }

    @NonNull
    public RequestManager get(@NonNull FragmentActivity activity) {
        if (Util.isOnBackgroundThread()) {
            return get(activity.getApplicationContext());
        }
        assertNotDestroyed(activity);
        return supportFragmentGet(activity, activity.getSupportFragmentManager(), null, isActivityVisible(activity));
    }

    @NonNull
    public RequestManager get(@NonNull android.support.v4.app.Fragment fragment) {
        Preconditions.checkNotNull(fragment.getActivity(), "You cannot start a load on a fragment before it is attached or after it is destroyed");
        if (Util.isOnBackgroundThread()) {
            return get(fragment.getActivity().getApplicationContext());
        }
        return supportFragmentGet(fragment.getActivity(), fragment.getChildFragmentManager(), fragment, fragment.isVisible());
    }

    @NonNull
    public RequestManager get(@NonNull Activity activity) {
        if (Util.isOnBackgroundThread()) {
            return get(activity.getApplicationContext());
        }
        assertNotDestroyed(activity);
        return fragmentGet(activity, activity.getFragmentManager(), null, isActivityVisible(activity));
    }

    @NonNull
    public RequestManager get(@NonNull View view) {
        if (Util.isOnBackgroundThread()) {
            return get(view.getContext().getApplicationContext());
        }
        Preconditions.checkNotNull(view);
        Preconditions.checkNotNull(view.getContext(), "Unable to obtain a request manager for a view without a Context");
        Activity activity = findActivity(view.getContext());
        if (activity == null) {
            return get(view.getContext().getApplicationContext());
        }
        if (activity instanceof FragmentActivity) {
            android.support.v4.app.Fragment fragment = findSupportFragment(view, (FragmentActivity) activity);
            return fragment != null ? get(fragment) : get(activity);
        }
        Fragment fragment2 = findFragment(view, activity);
        if (fragment2 == null) {
            return get(activity);
        }
        return get(fragment2);
    }

    private static void findAllSupportFragmentsWithViews(@Nullable Collection<android.support.v4.app.Fragment> topLevelFragments, @NonNull Map<View, android.support.v4.app.Fragment> result) {
        if (topLevelFragments != null) {
            for (android.support.v4.app.Fragment fragment : topLevelFragments) {
                if (fragment != null) {
                    if (fragment.getView() != null) {
                        result.put(fragment.getView(), fragment);
                        findAllSupportFragmentsWithViews(fragment.getChildFragmentManager().getFragments(), result);
                    }
                }
            }
        }
    }

    @Nullable
    private android.support.v4.app.Fragment findSupportFragment(@NonNull View target, @NonNull FragmentActivity activity) {
        this.tempViewToSupportFragment.clear();
        findAllSupportFragmentsWithViews(activity.getSupportFragmentManager().getFragments(), this.tempViewToSupportFragment);
        android.support.v4.app.Fragment result = null;
        View activityRoot = activity.findViewById(16908290);
        View current = target;
        while (!current.equals(activityRoot)) {
            result = (android.support.v4.app.Fragment) this.tempViewToSupportFragment.get(current);
            if (result == null) {
                if (!(current.getParent() instanceof View)) {
                    break;
                }
                current = (View) current.getParent();
            } else {
                break;
            }
        }
        this.tempViewToSupportFragment.clear();
        return result;
    }

    @Nullable
    @Deprecated
    private Fragment findFragment(@NonNull View target, @NonNull Activity activity) {
        this.tempViewToFragment.clear();
        findAllFragmentsWithViews(activity.getFragmentManager(), this.tempViewToFragment);
        Fragment result = null;
        View activityRoot = activity.findViewById(16908290);
        View current = target;
        while (!current.equals(activityRoot)) {
            result = (Fragment) this.tempViewToFragment.get(current);
            if (result == null) {
                if (!(current.getParent() instanceof View)) {
                    break;
                }
                current = (View) current.getParent();
            } else {
                break;
            }
        }
        this.tempViewToFragment.clear();
        return result;
    }

    @TargetApi(26)
    @Deprecated
    private void findAllFragmentsWithViews(@NonNull FragmentManager fragmentManager, @NonNull ArrayMap<View, Fragment> result) {
        if (VERSION.SDK_INT >= 26) {
            for (Fragment fragment : fragmentManager.getFragments()) {
                if (fragment.getView() != null) {
                    result.put(fragment.getView(), fragment);
                    findAllFragmentsWithViews(fragment.getChildFragmentManager(), result);
                }
            }
            return;
        }
        findAllFragmentsWithViewsPreO(fragmentManager, result);
    }

    @Deprecated
    private void findAllFragmentsWithViewsPreO(@NonNull FragmentManager fragmentManager, @NonNull ArrayMap<View, Fragment> result) {
        int index = 0;
        while (true) {
            int index2 = index + 1;
            this.tempBundle.putInt(FRAGMENT_INDEX_KEY, index);
            Fragment fragment = null;
            try {
                fragment = fragmentManager.getFragment(this.tempBundle, FRAGMENT_INDEX_KEY);
            } catch (Exception e) {
            }
            if (fragment != null) {
                if (fragment.getView() != null) {
                    result.put(fragment.getView(), fragment);
                    if (VERSION.SDK_INT >= 17) {
                        findAllFragmentsWithViews(fragment.getChildFragmentManager(), result);
                    }
                }
                index = index2;
            } else {
                return;
            }
        }
    }

    @Nullable
    private Activity findActivity(@NonNull Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            return findActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    @TargetApi(17)
    private static void assertNotDestroyed(@NonNull Activity activity) {
        if (VERSION.SDK_INT >= 17) {
            if (activity.isDestroyed()) {
                throw new IllegalArgumentException("You cannot start a load for a destroyed activity");
            }
        }
    }

    @TargetApi(17)
    @Deprecated
    @NonNull
    public RequestManager get(@NonNull Fragment fragment) {
        if (fragment.getActivity() != null) {
            if (!Util.isOnBackgroundThread()) {
                if (VERSION.SDK_INT >= 17) {
                    return fragmentGet(fragment.getActivity(), fragment.getChildFragmentManager(), fragment, fragment.isVisible());
                }
            }
            return get(fragment.getActivity().getApplicationContext());
        }
        throw new IllegalArgumentException("You cannot start a load on a fragment before it is attached");
    }

    @Deprecated
    @NonNull
    RequestManagerFragment getRequestManagerFragment(Activity activity) {
        return getRequestManagerFragment(activity.getFragmentManager(), null, isActivityVisible(activity));
    }

    @NonNull
    private RequestManagerFragment getRequestManagerFragment(@NonNull FragmentManager fm, @Nullable Fragment parentHint, boolean isParentVisible) {
        RequestManagerFragment current = (RequestManagerFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        if (current == null) {
            current = (RequestManagerFragment) this.pendingRequestManagerFragments.get(fm);
            if (current == null) {
                current = new RequestManagerFragment();
                current.setParentFragmentHint(parentHint);
                if (isParentVisible) {
                    current.getGlideLifecycle().onStart();
                }
                this.pendingRequestManagerFragments.put(fm, current);
                fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss();
                this.handler.obtainMessage(1, fm).sendToTarget();
            }
        }
        return current;
    }

    @Deprecated
    @NonNull
    private RequestManager fragmentGet(@NonNull Context context, @NonNull FragmentManager fm, @Nullable Fragment parentHint, boolean isParentVisible) {
        RequestManagerFragment current = getRequestManagerFragment(fm, parentHint, isParentVisible);
        RequestManager requestManager = current.getRequestManager();
        if (requestManager != null) {
            return requestManager;
        }
        requestManager = this.factory.build(Glide.get(context), current.getGlideLifecycle(), current.getRequestManagerTreeNode(), context);
        current.setRequestManager(requestManager);
        return requestManager;
    }

    @NonNull
    SupportRequestManagerFragment getSupportRequestManagerFragment(FragmentActivity activity) {
        return getSupportRequestManagerFragment(activity.getSupportFragmentManager(), null, isActivityVisible(activity));
    }

    private static boolean isActivityVisible(Activity activity) {
        return activity.isFinishing() ^ 1;
    }

    @NonNull
    private SupportRequestManagerFragment getSupportRequestManagerFragment(@NonNull android.support.v4.app.FragmentManager fm, @Nullable android.support.v4.app.Fragment parentHint, boolean isParentVisible) {
        SupportRequestManagerFragment current = (SupportRequestManagerFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        if (current == null) {
            current = (SupportRequestManagerFragment) this.pendingSupportRequestManagerFragments.get(fm);
            if (current == null) {
                current = new SupportRequestManagerFragment();
                current.setParentFragmentHint(parentHint);
                if (isParentVisible) {
                    current.getGlideLifecycle().onStart();
                }
                this.pendingSupportRequestManagerFragments.put(fm, current);
                fm.beginTransaction().add((android.support.v4.app.Fragment) current, FRAGMENT_TAG).commitAllowingStateLoss();
                this.handler.obtainMessage(2, fm).sendToTarget();
            }
        }
        return current;
    }

    @NonNull
    private RequestManager supportFragmentGet(@NonNull Context context, @NonNull android.support.v4.app.FragmentManager fm, @Nullable android.support.v4.app.Fragment parentHint, boolean isParentVisible) {
        SupportRequestManagerFragment current = getSupportRequestManagerFragment(fm, parentHint, isParentVisible);
        RequestManager requestManager = current.getRequestManager();
        if (requestManager != null) {
            return requestManager;
        }
        requestManager = this.factory.build(Glide.get(context), current.getGlideLifecycle(), current.getRequestManagerTreeNode(), context);
        current.setRequestManager(requestManager);
        return requestManager;
    }

    public boolean handleMessage(Message message) {
        boolean handled = true;
        Object removed = null;
        Object key = null;
        switch (message.what) {
            case 1:
                FragmentManager fm = message.obj;
                key = fm;
                removed = this.pendingRequestManagerFragments.remove(fm);
                break;
            case 2:
                android.support.v4.app.FragmentManager supportFm = message.obj;
                key = supportFm;
                removed = this.pendingSupportRequestManagerFragments.remove(supportFm);
                break;
            default:
                handled = false;
                break;
        }
        if (handled && removed == null && Log.isLoggable(TAG, 5)) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to remove expected request manager fragment, manager: ");
            stringBuilder.append(key);
            Log.w(str, stringBuilder.toString());
        }
        return handled;
    }
}
