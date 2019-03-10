package de.danoeh.antennapod.core.feed;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$EventDistributor$zJEKGjLQn8YZdh7p5eZsxgjv2FY implements Runnable {
    private final /* synthetic */ EventDistributor f$0;

    public /* synthetic */ -$$Lambda$EventDistributor$zJEKGjLQn8YZdh7p5eZsxgjv2FY(EventDistributor eventDistributor) {
        this.f$0 = eventDistributor;
    }

    public final void run() {
        this.f$0.processEventQueue();
    }
}
