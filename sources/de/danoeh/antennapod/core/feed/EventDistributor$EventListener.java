package de.danoeh.antennapod.core.feed;

import java.util.Observable;
import java.util.Observer;

public abstract class EventDistributor$EventListener implements Observer {
    public abstract void update(EventDistributor eventDistributor, Integer num);

    public void update(Observable observable, Object data) {
        if ((observable instanceof EventDistributor) && (data instanceof Integer)) {
            update((EventDistributor) observable, (Integer) data);
        }
    }
}
