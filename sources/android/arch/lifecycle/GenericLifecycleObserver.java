package android.arch.lifecycle;

public interface GenericLifecycleObserver extends LifecycleObserver {
    void onStateChanged(LifecycleOwner lifecycleOwner, Lifecycle$Event lifecycle$Event);
}
