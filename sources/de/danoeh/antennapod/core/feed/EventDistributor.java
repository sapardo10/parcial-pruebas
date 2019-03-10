package de.danoeh.antennapod.core.feed;

import android.os.Handler;
import android.util.Log;
import java.util.AbstractQueue;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventDistributor extends Observable {
    public static final int DOWNLOADLOG_UPDATE = 8;
    public static final int DOWNLOAD_HANDLED = 64;
    public static final int FEED_LIST_UPDATE = 1;
    public static final int PLAYBACK_HISTORY_UPDATE = 16;
    public static final int PLAYER_STATUS_UPDATE = 128;
    private static final String TAG = "EventDistributor";
    public static final int UNREAD_ITEMS_UPDATE = 2;
    private static EventDistributor instance;
    private final AbstractQueue<Integer> events = new ConcurrentLinkedQueue();
    private final Handler handler = new Handler();

    private EventDistributor() {
    }

    public static synchronized EventDistributor getInstance() {
        EventDistributor eventDistributor;
        synchronized (EventDistributor.class) {
            if (instance == null) {
                instance = new EventDistributor();
            }
            eventDistributor = instance;
        }
        return eventDistributor;
    }

    public void register(EventDistributor$EventListener el) {
        addObserver(el);
    }

    public void unregister(EventDistributor$EventListener el) {
        deleteObserver(el);
    }

    private void addEvent(Integer i) {
        this.events.offer(i);
        this.handler.post(new -$$Lambda$EventDistributor$zJEKGjLQn8YZdh7p5eZsxgjv2FY());
    }

    private void processEventQueue() {
        Integer result = Integer.valueOf(null);
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Processing event queue. Number of events: ");
        stringBuilder.append(this.events.size());
        Log.d(str, stringBuilder.toString());
        Integer current = (Integer) this.events.poll();
        while (current != null) {
            result = Integer.valueOf(result.intValue() | current.intValue());
            current = (Integer) this.events.poll();
        }
        if (result.intValue() != 0) {
            str = TAG;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Notifying observers. Data: ");
            stringBuilder.append(result);
            Log.d(str, stringBuilder.toString());
            setChanged();
            notifyObservers(result);
            return;
        }
        Log.d(TAG, "Event queue didn't contain any new events. Observers will not be notified.");
    }

    public void addObserver(Observer observer) {
        super.addObserver(observer);
    }

    public void sendUnreadItemsUpdateBroadcast() {
        addEvent(Integer.valueOf(2));
    }

    public void sendFeedUpdateBroadcast() {
        addEvent(Integer.valueOf(1));
    }

    public void sendPlaybackHistoryUpdateBroadcast() {
        addEvent(Integer.valueOf(16));
    }

    public void sendDownloadLogUpdateBroadcast() {
        addEvent(Integer.valueOf(8));
    }

    public void sendPlayerStatusUpdateBroadcast() {
        addEvent(Integer.valueOf(128));
    }
}
