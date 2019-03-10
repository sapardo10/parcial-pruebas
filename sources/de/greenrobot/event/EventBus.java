package de.greenrobot.event;

import android.os.Looper;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

public class EventBus {
    private static final EventBusBuilder DEFAULT_BUILDER = new EventBusBuilder();
    public static String TAG = "Event";
    static volatile EventBus defaultInstance;
    private static final Map<Class<?>, List<Class<?>>> eventTypesCache = new HashMap();
    private final AsyncPoster asyncPoster;
    private final BackgroundPoster backgroundPoster;
    private final ThreadLocal<PostingThreadState> currentPostingThreadState;
    private final boolean eventInheritance;
    private final ExecutorService executorService;
    private final boolean logNoSubscriberMessages;
    private final boolean logSubscriberExceptions;
    private final HandlerPoster mainThreadPoster;
    private final boolean sendNoSubscriberEvent;
    private final boolean sendSubscriberExceptionEvent;
    private final Map<Class<?>, Object> stickyEvents;
    private final SubscriberMethodFinder subscriberMethodFinder;
    private final Map<Class<?>, CopyOnWriteArrayList<Subscription>> subscriptionsByEventType;
    private final boolean throwSubscriberException;
    private final Map<Object, List<Class<?>>> typesBySubscriber;

    /* renamed from: de.greenrobot.event.EventBus$1 */
    class C07911 extends ThreadLocal<PostingThreadState> {
        C07911() {
        }

        protected PostingThreadState initialValue() {
            return new PostingThreadState();
        }
    }

    interface PostCallback {
        void onPostCompleted(List<SubscriberExceptionEvent> list);
    }

    static final class PostingThreadState {
        boolean canceled;
        Object event;
        final List<Object> eventQueue = new ArrayList();
        boolean isMainThread;
        boolean isPosting;
        Subscription subscription;

        PostingThreadState() {
        }
    }

    private java.util.List<java.lang.Class<?>> lookupAllEventTypes(java.lang.Class<?> r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x0032 in {7, 8, 9, 11, 14} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r4 = this;
        r0 = eventTypesCache;
        monitor-enter(r0);
        r1 = eventTypesCache;	 Catch:{ all -> 0x002f }
        r1 = r1.get(r5);	 Catch:{ all -> 0x002f }
        r1 = (java.util.List) r1;	 Catch:{ all -> 0x002f }
        if (r1 != 0) goto L_0x002c;	 Catch:{ all -> 0x002f }
    L_0x000d:
        r2 = new java.util.ArrayList;	 Catch:{ all -> 0x002f }
        r2.<init>();	 Catch:{ all -> 0x002f }
        r1 = r2;	 Catch:{ all -> 0x002f }
        r2 = r5;	 Catch:{ all -> 0x002f }
    L_0x0014:
        if (r2 == 0) goto L_0x0026;	 Catch:{ all -> 0x002f }
    L_0x0016:
        r1.add(r2);	 Catch:{ all -> 0x002f }
        r3 = r2.getInterfaces();	 Catch:{ all -> 0x002f }
        addInterfaces(r1, r3);	 Catch:{ all -> 0x002f }
        r3 = r2.getSuperclass();	 Catch:{ all -> 0x002f }
        r2 = r3;	 Catch:{ all -> 0x002f }
        goto L_0x0014;	 Catch:{ all -> 0x002f }
    L_0x0026:
        r3 = eventTypesCache;	 Catch:{ all -> 0x002f }
        r3.put(r5, r1);	 Catch:{ all -> 0x002f }
        goto L_0x002d;	 Catch:{ all -> 0x002f }
    L_0x002d:
        monitor-exit(r0);	 Catch:{ all -> 0x002f }
        return r1;	 Catch:{ all -> 0x002f }
    L_0x002f:
        r1 = move-exception;	 Catch:{ all -> 0x002f }
        monitor-exit(r0);	 Catch:{ all -> 0x002f }
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.greenrobot.event.EventBus.lookupAllEventTypes(java.lang.Class):java.util.List<java.lang.Class<?>>");
    }

    private boolean postSingleEventForEventType(java.lang.Object r9, de.greenrobot.event.EventBus.PostingThreadState r10, java.lang.Class<?> r11) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:34:0x0052 in {18, 19, 22, 23, 25, 27, 29, 30, 33} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r8 = this;
        monitor-enter(r8);
        r0 = 0;
        r1 = r8.subscriptionsByEventType;	 Catch:{ all -> 0x004f }
        r1 = r1.get(r11);	 Catch:{ all -> 0x004f }
        r1 = (java.util.concurrent.CopyOnWriteArrayList) r1;	 Catch:{ all -> 0x004f }
        monitor-exit(r8);	 Catch:{ all -> 0x004a }
        r2 = 0;
        if (r1 == 0) goto L_0x0048;
    L_0x000e:
        r3 = r1.isEmpty();
        if (r3 != 0) goto L_0x0048;
    L_0x0014:
        r3 = r1.iterator();
    L_0x0018:
        r4 = r3.hasNext();
        if (r4 == 0) goto L_0x0045;
    L_0x001e:
        r4 = r3.next();
        r4 = (de.greenrobot.event.Subscription) r4;
        r10.event = r9;
        r10.subscription = r4;
        r5 = 0;
        r6 = r10.isMainThread;	 Catch:{ all -> 0x003d }
        r8.postToSubscription(r4, r9, r6);	 Catch:{ all -> 0x003d }
        r6 = r10.canceled;	 Catch:{ all -> 0x003d }
        r5 = r6;
        r10.event = r0;
        r10.subscription = r0;
        r10.canceled = r2;
        if (r5 == 0) goto L_0x003b;
    L_0x003a:
        goto L_0x0046;
        goto L_0x0018;
    L_0x003d:
        r6 = move-exception;
        r10.event = r0;
        r10.subscription = r0;
        r10.canceled = r2;
        throw r6;
    L_0x0046:
        r0 = 1;
        return r0;
        return r2;
    L_0x004a:
        r0 = move-exception;
        r7 = r1;
        r1 = r0;
        r0 = r7;
        goto L_0x0050;
    L_0x004f:
        r1 = move-exception;
    L_0x0050:
        monitor-exit(r8);	 Catch:{ all -> 0x004f }
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.greenrobot.event.EventBus.postSingleEventForEventType(java.lang.Object, de.greenrobot.event.EventBus$PostingThreadState, java.lang.Class):boolean");
    }

    private synchronized void register(java.lang.Object r4, boolean r5, int r6) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:13:0x0025 in {6, 9, 12} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r3 = this;
        monitor-enter(r3);
        r0 = r3.subscriberMethodFinder;	 Catch:{ all -> 0x0022 }
        r1 = r4.getClass();	 Catch:{ all -> 0x0022 }
        r0 = r0.findSubscriberMethods(r1);	 Catch:{ all -> 0x0022 }
        r1 = r0.iterator();	 Catch:{ all -> 0x0022 }
    L_0x000f:
        r2 = r1.hasNext();	 Catch:{ all -> 0x0022 }
        if (r2 == 0) goto L_0x001f;	 Catch:{ all -> 0x0022 }
    L_0x0015:
        r2 = r1.next();	 Catch:{ all -> 0x0022 }
        r2 = (de.greenrobot.event.SubscriberMethod) r2;	 Catch:{ all -> 0x0022 }
        r3.subscribe(r4, r2, r5, r6);	 Catch:{ all -> 0x0022 }
        goto L_0x000f;
        monitor-exit(r3);
        return;
    L_0x0022:
        r4 = move-exception;
        monitor-exit(r3);
        throw r4;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.greenrobot.event.EventBus.register(java.lang.Object, boolean, int):void");
    }

    private void subscribe(java.lang.Object r9, de.greenrobot.event.SubscriberMethod r10, boolean r11, int r12) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:41:0x00a5 in {2, 10, 11, 12, 13, 16, 17, 29, 30, 31, 32, 36, 37, 38, 40} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r8 = this;
        r0 = r10.eventType;
        r1 = r8.subscriptionsByEventType;
        r1 = r1.get(r0);
        r1 = (java.util.concurrent.CopyOnWriteArrayList) r1;
        r2 = new de.greenrobot.event.Subscription;
        r2.<init>(r9, r10, r12);
        if (r1 != 0) goto L_0x001d;
    L_0x0011:
        r3 = new java.util.concurrent.CopyOnWriteArrayList;
        r3.<init>();
        r1 = r3;
        r3 = r8.subscriptionsByEventType;
        r3.put(r0, r1);
        goto L_0x0023;
    L_0x001d:
        r3 = r1.contains(r2);
        if (r3 != 0) goto L_0x0082;
    L_0x0023:
        r3 = r1.size();
        r4 = 0;
    L_0x0028:
        if (r4 > r3) goto L_0x0041;
    L_0x002a:
        if (r4 == r3) goto L_0x003c;
    L_0x002c:
        r5 = r2.priority;
        r6 = r1.get(r4);
        r6 = (de.greenrobot.event.Subscription) r6;
        r6 = r6.priority;
        if (r5 <= r6) goto L_0x0039;
    L_0x0038:
        goto L_0x003c;
    L_0x0039:
        r4 = r4 + 1;
        goto L_0x0028;
        r1.add(r4, r2);
        goto L_0x0042;
    L_0x0042:
        r4 = r8.typesBySubscriber;
        r4 = r4.get(r9);
        r4 = (java.util.List) r4;
        if (r4 != 0) goto L_0x0058;
    L_0x004c:
        r5 = new java.util.ArrayList;
        r5.<init>();
        r4 = r5;
        r5 = r8.typesBySubscriber;
        r5.put(r9, r4);
        goto L_0x0059;
    L_0x0059:
        r4.add(r0);
        if (r11 == 0) goto L_0x0080;
    L_0x005e:
        r5 = r8.stickyEvents;
        monitor-enter(r5);
        r6 = 0;
        r7 = r8.stickyEvents;	 Catch:{ all -> 0x007d }
        r6 = r7.get(r0);	 Catch:{ all -> 0x007d }
        monitor-exit(r5);	 Catch:{ all -> 0x007d }
        if (r6 == 0) goto L_0x007c;
    L_0x006b:
        r5 = android.os.Looper.getMainLooper();
        r7 = android.os.Looper.myLooper();
        if (r5 != r7) goto L_0x0077;
    L_0x0075:
        r5 = 1;
        goto L_0x0078;
    L_0x0077:
        r5 = 0;
    L_0x0078:
        r8.postToSubscription(r2, r6, r5);
        goto L_0x0081;
    L_0x007c:
        goto L_0x0081;
    L_0x007d:
        r7 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x007d }
        throw r7;
    L_0x0081:
        return;
    L_0x0082:
        r3 = new de.greenrobot.event.EventBusException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Subscriber ";
        r4.append(r5);
        r5 = r9.getClass();
        r4.append(r5);
        r5 = " already registered to event ";
        r4.append(r5);
        r4.append(r0);
        r4 = r4.toString();
        r3.<init>(r4);
        throw r3;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.greenrobot.event.EventBus.subscribe(java.lang.Object, de.greenrobot.event.SubscriberMethod, boolean, int):void");
    }

    public synchronized void unregister(java.lang.Object r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x0045 in {7, 8, 9, 11, 14} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r4 = this;
        monitor-enter(r4);
        r0 = r4.typesBySubscriber;	 Catch:{ all -> 0x0042 }
        r0 = r0.get(r5);	 Catch:{ all -> 0x0042 }
        r0 = (java.util.List) r0;	 Catch:{ all -> 0x0042 }
        if (r0 == 0) goto L_0x0026;	 Catch:{ all -> 0x0042 }
    L_0x000b:
        r1 = r0.iterator();	 Catch:{ all -> 0x0042 }
    L_0x000f:
        r2 = r1.hasNext();	 Catch:{ all -> 0x0042 }
        if (r2 == 0) goto L_0x001f;	 Catch:{ all -> 0x0042 }
    L_0x0015:
        r2 = r1.next();	 Catch:{ all -> 0x0042 }
        r2 = (java.lang.Class) r2;	 Catch:{ all -> 0x0042 }
        r4.unubscribeByEventType(r5, r2);	 Catch:{ all -> 0x0042 }
        goto L_0x000f;	 Catch:{ all -> 0x0042 }
        r1 = r4.typesBySubscriber;	 Catch:{ all -> 0x0042 }
        r1.remove(r5);	 Catch:{ all -> 0x0042 }
        goto L_0x0040;	 Catch:{ all -> 0x0042 }
    L_0x0026:
        r1 = TAG;	 Catch:{ all -> 0x0042 }
        r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0042 }
        r2.<init>();	 Catch:{ all -> 0x0042 }
        r3 = "Subscriber to unregister was not registered before: ";	 Catch:{ all -> 0x0042 }
        r2.append(r3);	 Catch:{ all -> 0x0042 }
        r3 = r5.getClass();	 Catch:{ all -> 0x0042 }
        r2.append(r3);	 Catch:{ all -> 0x0042 }
        r2 = r2.toString();	 Catch:{ all -> 0x0042 }
        android.util.Log.w(r1, r2);	 Catch:{ all -> 0x0042 }
    L_0x0040:
        monitor-exit(r4);
        return;
    L_0x0042:
        r5 = move-exception;
        monitor-exit(r4);
        throw r5;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.greenrobot.event.EventBus.unregister(java.lang.Object):void");
    }

    public static EventBus getDefault() {
        if (defaultInstance == null) {
            synchronized (EventBus.class) {
                if (defaultInstance == null) {
                    defaultInstance = new EventBus();
                }
            }
        }
        return defaultInstance;
    }

    public static EventBusBuilder builder() {
        return new EventBusBuilder();
    }

    public static void clearCaches() {
        SubscriberMethodFinder.clearCaches();
        eventTypesCache.clear();
    }

    public EventBus() {
        this(DEFAULT_BUILDER);
    }

    EventBus(EventBusBuilder builder) {
        this.currentPostingThreadState = new C07911();
        this.subscriptionsByEventType = new HashMap();
        this.typesBySubscriber = new HashMap();
        this.stickyEvents = new ConcurrentHashMap();
        this.mainThreadPoster = new HandlerPoster(this, Looper.getMainLooper(), 10);
        this.backgroundPoster = new BackgroundPoster(this);
        this.asyncPoster = new AsyncPoster(this);
        this.subscriberMethodFinder = new SubscriberMethodFinder(builder.skipMethodVerificationForClasses);
        this.logSubscriberExceptions = builder.logSubscriberExceptions;
        this.logNoSubscriberMessages = builder.logNoSubscriberMessages;
        this.sendSubscriberExceptionEvent = builder.sendSubscriberExceptionEvent;
        this.sendNoSubscriberEvent = builder.sendNoSubscriberEvent;
        this.throwSubscriberException = builder.throwSubscriberException;
        this.eventInheritance = builder.eventInheritance;
        this.executorService = builder.executorService;
    }

    public void register(Object subscriber) {
        register(subscriber, false, 0);
    }

    public void register(Object subscriber, int priority) {
        register(subscriber, false, priority);
    }

    public void registerSticky(Object subscriber) {
        register(subscriber, true, 0);
    }

    public void registerSticky(Object subscriber, int priority) {
        register(subscriber, true, priority);
    }

    public synchronized boolean isRegistered(Object subscriber) {
        return this.typesBySubscriber.containsKey(subscriber);
    }

    private void unubscribeByEventType(Object subscriber, Class<?> eventType) {
        List<Subscription> subscriptions = (List) this.subscriptionsByEventType.get(eventType);
        if (subscriptions != null) {
            int size = subscriptions.size();
            int i = 0;
            while (i < size) {
                Subscription subscription = (Subscription) subscriptions.get(i);
                if (subscription.subscriber == subscriber) {
                    subscription.active = false;
                    subscriptions.remove(i);
                    i--;
                    size--;
                }
                i++;
            }
        }
    }

    public void post(Object event) {
        PostingThreadState postingState = (PostingThreadState) this.currentPostingThreadState.get();
        List<Object> eventQueue = postingState.eventQueue;
        eventQueue.add(event);
        if (!postingState.isPosting) {
            postingState.isMainThread = Looper.getMainLooper() == Looper.myLooper();
            postingState.isPosting = true;
            if (postingState.canceled) {
                throw new EventBusException("Internal error. Abort state was not reset");
            }
            while (!eventQueue.isEmpty()) {
                try {
                    postSingleEvent(eventQueue.remove(0), postingState);
                } finally {
                    postingState.isPosting = false;
                    postingState.isMainThread = false;
                }
            }
        }
    }

    public void cancelEventDelivery(Object event) {
        PostingThreadState postingState = (PostingThreadState) this.currentPostingThreadState.get();
        if (!postingState.isPosting) {
            throw new EventBusException("This method may only be called from inside event handling methods on the posting thread");
        } else if (event == null) {
            throw new EventBusException("Event may not be null");
        } else if (postingState.event != event) {
            throw new EventBusException("Only the currently handled event may be aborted");
        } else if (postingState.subscription.subscriberMethod.threadMode == ThreadMode.PostThread) {
            postingState.canceled = true;
        } else {
            throw new EventBusException(" event handlers may only abort the incoming event");
        }
    }

    public void postSticky(Object event) {
        synchronized (this.stickyEvents) {
            this.stickyEvents.put(event.getClass(), event);
        }
        post(event);
    }

    public <T> T getStickyEvent(Class<T> eventType) {
        T cast;
        synchronized (this.stickyEvents) {
            cast = eventType.cast(this.stickyEvents.get(eventType));
        }
        return cast;
    }

    public <T> T removeStickyEvent(Class<T> eventType) {
        T cast;
        synchronized (this.stickyEvents) {
            cast = eventType.cast(this.stickyEvents.remove(eventType));
        }
        return cast;
    }

    public boolean removeStickyEvent(Object event) {
        synchronized (this.stickyEvents) {
            Class<?> eventType = event.getClass();
            if (event.equals(this.stickyEvents.get(eventType))) {
                this.stickyEvents.remove(eventType);
                return true;
            }
            return false;
        }
    }

    public void removeAllStickyEvents() {
        synchronized (this.stickyEvents) {
            this.stickyEvents.clear();
        }
    }

    public boolean hasSubscriberForEvent(Class<?> eventClass) {
        List<Class<?>> eventTypes = lookupAllEventTypes(eventClass);
        if (eventTypes != null) {
            int countTypes = eventTypes.size();
            for (int h = 0; h < countTypes; h++) {
                CopyOnWriteArrayList<Subscription> subscriptions;
                Class<?> clazz = (Class) eventTypes.get(h);
                synchronized (this) {
                    subscriptions = (CopyOnWriteArrayList) this.subscriptionsByEventType.get(clazz);
                }
                if (subscriptions != null && !subscriptions.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void postSingleEvent(Object event, PostingThreadState postingState) throws Error {
        Class<?> eventClass = event.getClass();
        boolean subscriptionFound = false;
        if (this.eventInheritance) {
            List<Class<?>> eventTypes = lookupAllEventTypes(eventClass);
            for (int h = 0; h < eventTypes.size(); h++) {
                subscriptionFound |= postSingleEventForEventType(event, postingState, (Class) eventTypes.get(h));
            }
        } else {
            subscriptionFound = postSingleEventForEventType(event, postingState, eventClass);
        }
        if (!subscriptionFound) {
            if (this.logNoSubscriberMessages) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("No subscribers registered for event ");
                stringBuilder.append(eventClass);
                Log.d(str, stringBuilder.toString());
            }
            if (this.sendNoSubscriberEvent && eventClass != NoSubscriberEvent.class && eventClass != SubscriberExceptionEvent.class) {
                post(new NoSubscriberEvent(this, event));
            }
        }
    }

    private void postToSubscription(Subscription subscription, Object event, boolean isMainThread) {
        switch (subscription.subscriberMethod.threadMode) {
            case PostThread:
                invokeSubscriber(subscription, event);
                return;
            case MainThread:
                if (isMainThread) {
                    invokeSubscriber(subscription, event);
                    return;
                } else {
                    this.mainThreadPoster.enqueue(subscription, event);
                    return;
                }
            case BackgroundThread:
                if (isMainThread) {
                    this.backgroundPoster.enqueue(subscription, event);
                    return;
                } else {
                    invokeSubscriber(subscription, event);
                    return;
                }
            case Async:
                this.asyncPoster.enqueue(subscription, event);
                return;
            default:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unknown thread mode: ");
                stringBuilder.append(subscription.subscriberMethod.threadMode);
                throw new IllegalStateException(stringBuilder.toString());
        }
    }

    static void addInterfaces(List<Class<?>> eventTypes, Class<?>[] interfaces) {
        for (Class<?> interfaceClass : interfaces) {
            if (!eventTypes.contains(interfaceClass)) {
                eventTypes.add(interfaceClass);
                addInterfaces(eventTypes, interfaceClass.getInterfaces());
            }
        }
    }

    void invokeSubscriber(PendingPost pendingPost) {
        Object event = pendingPost.event;
        Subscription subscription = pendingPost.subscription;
        PendingPost.releasePendingPost(pendingPost);
        if (subscription.active) {
            invokeSubscriber(subscription, event);
        }
    }

    void invokeSubscriber(Subscription subscription, Object event) {
        try {
            subscription.subscriberMethod.method.invoke(subscription.subscriber, new Object[]{event});
        } catch (InvocationTargetException e) {
            handleSubscriberException(subscription, event, e.getCause());
        } catch (IllegalAccessException e2) {
            throw new IllegalStateException("Unexpected exception", e2);
        }
    }

    private void handleSubscriberException(Subscription subscription, Object event, Throwable cause) {
        String str;
        StringBuilder stringBuilder;
        if (event instanceof SubscriberExceptionEvent) {
            if (this.logSubscriberExceptions) {
                str = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append("SubscriberExceptionEvent subscriber ");
                stringBuilder.append(subscription.subscriber.getClass());
                stringBuilder.append(" threw an exception");
                Log.e(str, stringBuilder.toString(), cause);
                SubscriberExceptionEvent exEvent = (SubscriberExceptionEvent) event;
                String str2 = TAG;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Initial event ");
                stringBuilder2.append(exEvent.causingEvent);
                stringBuilder2.append(" caused exception in ");
                stringBuilder2.append(exEvent.causingSubscriber);
                Log.e(str2, stringBuilder2.toString(), exEvent.throwable);
            }
        } else if (this.throwSubscriberException) {
            throw new EventBusException("Invoking subscriber failed", cause);
        } else {
            if (this.logSubscriberExceptions) {
                str = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append("Could not dispatch event: ");
                stringBuilder.append(event.getClass());
                stringBuilder.append(" to subscribing class ");
                stringBuilder.append(subscription.subscriber.getClass());
                Log.e(str, stringBuilder.toString(), cause);
            }
            if (this.sendSubscriberExceptionEvent) {
                post(new SubscriberExceptionEvent(this, cause, event, subscription.subscriber));
            }
        }
    }

    ExecutorService getExecutorService() {
        return this.executorService;
    }
}
