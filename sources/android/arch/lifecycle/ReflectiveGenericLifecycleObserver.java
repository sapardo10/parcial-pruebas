package android.arch.lifecycle;

class ReflectiveGenericLifecycleObserver implements GenericLifecycleObserver {
    private final CallbackInfo mInfo = ClassesInfoCache.sInstance.getInfo(this.mWrapped.getClass());
    private final Object mWrapped;

    ReflectiveGenericLifecycleObserver(Object wrapped) {
        this.mWrapped = wrapped;
    }

    public void onStateChanged(LifecycleOwner source, Lifecycle$Event event) {
        this.mInfo.invokeCallbacks(source, event, this.mWrapped);
    }
}
