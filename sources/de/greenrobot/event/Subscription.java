package de.greenrobot.event;

final class Subscription {
    volatile boolean active = true;
    final int priority;
    final Object subscriber;
    final SubscriberMethod subscriberMethod;

    Subscription(Object subscriber, SubscriberMethod subscriberMethod, int priority) {
        this.subscriber = subscriber;
        this.subscriberMethod = subscriberMethod;
        this.priority = priority;
    }

    public boolean equals(Object other) {
        boolean z = false;
        if (!(other instanceof Subscription)) {
            return false;
        }
        Subscription otherSubscription = (Subscription) other;
        if (this.subscriber == otherSubscription.subscriber && this.subscriberMethod.equals(otherSubscription.subscriberMethod)) {
            z = true;
        }
        return z;
    }

    public int hashCode() {
        return this.subscriber.hashCode() + this.subscriberMethod.methodString.hashCode();
    }
}
