package android.arch.lifecycle;

import android.arch.core.internal.FastSafeIterableMap;
import android.arch.lifecycle.Lifecycle.State;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

public class LifecycleRegistry extends Lifecycle {
    private static final String LOG_TAG = "LifecycleRegistry";
    private int mAddingObserverCounter = 0;
    private boolean mHandlingEvent = false;
    private final WeakReference<LifecycleOwner> mLifecycleOwner;
    private boolean mNewEventOccurred = false;
    private FastSafeIterableMap<LifecycleObserver, ObserverWithState> mObserverMap = new FastSafeIterableMap();
    private ArrayList<State> mParentStates = new ArrayList();
    private State mState;

    /* renamed from: android.arch.lifecycle.LifecycleRegistry$1 */
    static /* synthetic */ class C00271 {
        static final /* synthetic */ int[] $SwitchMap$android$arch$lifecycle$Lifecycle$Event = new int[Lifecycle$Event.values().length];

        static {
            $SwitchMap$android$arch$lifecycle$Lifecycle$State = new int[State.values().length];
            try {
                $SwitchMap$android$arch$lifecycle$Lifecycle$State[State.INITIALIZED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$arch$lifecycle$Lifecycle$State[State.CREATED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$arch$lifecycle$Lifecycle$State[State.STARTED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$arch$lifecycle$Lifecycle$State[State.RESUMED.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$arch$lifecycle$Lifecycle$State[State.DESTROYED.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$android$arch$lifecycle$Lifecycle$Event[Lifecycle$Event.ON_CREATE.ordinal()] = 1;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$android$arch$lifecycle$Lifecycle$Event[Lifecycle$Event.ON_STOP.ordinal()] = 2;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$android$arch$lifecycle$Lifecycle$Event[Lifecycle$Event.ON_START.ordinal()] = 3;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$android$arch$lifecycle$Lifecycle$Event[Lifecycle$Event.ON_PAUSE.ordinal()] = 4;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$android$arch$lifecycle$Lifecycle$Event[Lifecycle$Event.ON_RESUME.ordinal()] = 5;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$android$arch$lifecycle$Lifecycle$Event[Lifecycle$Event.ON_DESTROY.ordinal()] = 6;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$android$arch$lifecycle$Lifecycle$Event[Lifecycle$Event.ON_ANY.ordinal()] = 7;
            } catch (NoSuchFieldError e12) {
            }
        }
    }

    static class ObserverWithState {
        GenericLifecycleObserver mLifecycleObserver;
        State mState;

        ObserverWithState(LifecycleObserver observer, State initialState) {
            this.mLifecycleObserver = Lifecycling.getCallback(observer);
            this.mState = initialState;
        }

        void dispatchEvent(LifecycleOwner owner, Lifecycle$Event event) {
            State newState = LifecycleRegistry.getStateAfter(event);
            this.mState = LifecycleRegistry.min(this.mState, newState);
            this.mLifecycleObserver.onStateChanged(owner, event);
            this.mState = newState;
        }
    }

    public LifecycleRegistry(@NonNull LifecycleOwner provider) {
        this.mLifecycleOwner = new WeakReference(provider);
        this.mState = State.INITIALIZED;
    }

    @MainThread
    public void markState(@NonNull State state) {
        moveToState(state);
    }

    public void handleLifecycleEvent(@NonNull Lifecycle$Event event) {
        moveToState(getStateAfter(event));
    }

    private void moveToState(State next) {
        if (this.mState != next) {
            this.mState = next;
            if (!this.mHandlingEvent) {
                if (this.mAddingObserverCounter == 0) {
                    this.mHandlingEvent = true;
                    sync();
                    this.mHandlingEvent = false;
                    return;
                }
            }
            this.mNewEventOccurred = true;
        }
    }

    private boolean isSynced() {
        boolean z = true;
        if (this.mObserverMap.size() == 0) {
            return true;
        }
        State eldestObserverState = ((ObserverWithState) this.mObserverMap.eldest().getValue()).mState;
        State newestObserverState = ((ObserverWithState) this.mObserverMap.newest().getValue()).mState;
        if (eldestObserverState != newestObserverState || this.mState != newestObserverState) {
            z = false;
        }
        return z;
    }

    private State calculateTargetState(LifecycleObserver observer) {
        Entry<LifecycleObserver, ObserverWithState> previous = this.mObserverMap.ceil(observer);
        State parentState = null;
        State siblingState = previous != null ? ((ObserverWithState) previous.getValue()).mState : null;
        if (!this.mParentStates.isEmpty()) {
            ArrayList arrayList = this.mParentStates;
            parentState = (State) arrayList.get(arrayList.size() - 1);
        }
        return min(min(this.mState, siblingState), parentState);
    }

    public void addObserver(@NonNull LifecycleObserver observer) {
        ObserverWithState statefulObserver = new ObserverWithState(observer, this.mState == State.DESTROYED ? State.DESTROYED : State.INITIALIZED);
        if (((ObserverWithState) this.mObserverMap.putIfAbsent(observer, statefulObserver)) == null) {
            LifecycleOwner lifecycleOwner = (LifecycleOwner) this.mLifecycleOwner.get();
            if (lifecycleOwner != null) {
                boolean isReentrance;
                State targetState;
                if (this.mAddingObserverCounter == 0) {
                    if (!this.mHandlingEvent) {
                        isReentrance = false;
                        targetState = calculateTargetState(observer);
                        this.mAddingObserverCounter++;
                        while (statefulObserver.mState.compareTo(targetState) < 0) {
                            if (this.mObserverMap.contains(observer)) {
                                break;
                            }
                            pushParentState(statefulObserver.mState);
                            statefulObserver.dispatchEvent(lifecycleOwner, upEvent(statefulObserver.mState));
                            popParentState();
                            targetState = calculateTargetState(observer);
                        }
                        if (!isReentrance) {
                            sync();
                        }
                        this.mAddingObserverCounter--;
                    }
                }
                isReentrance = true;
                targetState = calculateTargetState(observer);
                this.mAddingObserverCounter++;
                while (statefulObserver.mState.compareTo(targetState) < 0) {
                    if (this.mObserverMap.contains(observer)) {
                        break;
                    }
                    pushParentState(statefulObserver.mState);
                    statefulObserver.dispatchEvent(lifecycleOwner, upEvent(statefulObserver.mState));
                    popParentState();
                    targetState = calculateTargetState(observer);
                }
                if (!isReentrance) {
                    sync();
                }
                this.mAddingObserverCounter--;
            }
        }
    }

    private void popParentState() {
        ArrayList arrayList = this.mParentStates;
        arrayList.remove(arrayList.size() - 1);
    }

    private void pushParentState(State state) {
        this.mParentStates.add(state);
    }

    public void removeObserver(@NonNull LifecycleObserver observer) {
        this.mObserverMap.remove(observer);
    }

    public int getObserverCount() {
        return this.mObserverMap.size();
    }

    @NonNull
    public State getCurrentState() {
        return this.mState;
    }

    static State getStateAfter(Lifecycle$Event event) {
        switch (C00271.$SwitchMap$android$arch$lifecycle$Lifecycle$Event[event.ordinal()]) {
            case 1:
            case 2:
                return State.CREATED;
            case 3:
            case 4:
                return State.STARTED;
            case 5:
                return State.RESUMED;
            case 6:
                return State.DESTROYED;
            default:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unexpected event value ");
                stringBuilder.append(event);
                throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    private static Lifecycle$Event downEvent(State state) {
        switch (state) {
            case INITIALIZED:
                throw new IllegalArgumentException();
            case CREATED:
                return Lifecycle$Event.ON_DESTROY;
            case STARTED:
                return Lifecycle$Event.ON_STOP;
            case RESUMED:
                return Lifecycle$Event.ON_PAUSE;
            case DESTROYED:
                throw new IllegalArgumentException();
            default:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unexpected state value ");
                stringBuilder.append(state);
                throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    private static Lifecycle$Event upEvent(State state) {
        switch (state) {
            case INITIALIZED:
            case DESTROYED:
                return Lifecycle$Event.ON_CREATE;
            case CREATED:
                return Lifecycle$Event.ON_START;
            case STARTED:
                return Lifecycle$Event.ON_RESUME;
            case RESUMED:
                throw new IllegalArgumentException();
            default:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unexpected state value ");
                stringBuilder.append(state);
                throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    private void forwardPass(LifecycleOwner lifecycleOwner) {
        Iterator<Entry<LifecycleObserver, ObserverWithState>> ascendingIterator = this.mObserverMap.iteratorWithAdditions();
        while (ascendingIterator.hasNext() && !this.mNewEventOccurred) {
            Entry<LifecycleObserver, ObserverWithState> entry = (Entry) ascendingIterator.next();
            ObserverWithState observer = (ObserverWithState) entry.getValue();
            while (observer.mState.compareTo(this.mState) < 0 && !this.mNewEventOccurred) {
                if (!this.mObserverMap.contains(entry.getKey())) {
                    break;
                }
                pushParentState(observer.mState);
                observer.dispatchEvent(lifecycleOwner, upEvent(observer.mState));
                popParentState();
            }
        }
    }

    private void backwardPass(LifecycleOwner lifecycleOwner) {
        Iterator<Entry<LifecycleObserver, ObserverWithState>> descendingIterator = this.mObserverMap.descendingIterator();
        while (descendingIterator.hasNext() && !this.mNewEventOccurred) {
            Entry<LifecycleObserver, ObserverWithState> entry = (Entry) descendingIterator.next();
            ObserverWithState observer = (ObserverWithState) entry.getValue();
            while (observer.mState.compareTo(this.mState) > 0 && !this.mNewEventOccurred) {
                if (!this.mObserverMap.contains(entry.getKey())) {
                    break;
                }
                Lifecycle$Event event = downEvent(observer.mState);
                pushParentState(getStateAfter(event));
                observer.dispatchEvent(lifecycleOwner, event);
                popParentState();
            }
        }
    }

    private void sync() {
        LifecycleOwner lifecycleOwner = (LifecycleOwner) this.mLifecycleOwner.get();
        if (lifecycleOwner == null) {
            Log.w(LOG_TAG, "LifecycleOwner is garbage collected, you shouldn't try dispatch new events from it.");
            return;
        }
        while (!isSynced()) {
            this.mNewEventOccurred = false;
            if (this.mState.compareTo(((ObserverWithState) this.mObserverMap.eldest().getValue()).mState) < 0) {
                backwardPass(lifecycleOwner);
            }
            Entry<LifecycleObserver, ObserverWithState> newest = this.mObserverMap.newest();
            if (!this.mNewEventOccurred && newest != null) {
                if (this.mState.compareTo(((ObserverWithState) newest.getValue()).mState) > 0) {
                    forwardPass(lifecycleOwner);
                }
            }
        }
        this.mNewEventOccurred = false;
    }

    static State min(@NonNull State state1, @Nullable State state2) {
        return (state2 == null || state2.compareTo(state1) >= 0) ? state1 : state2;
    }
}
