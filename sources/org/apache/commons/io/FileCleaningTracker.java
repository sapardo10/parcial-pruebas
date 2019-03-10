package org.apache.commons.io;

import java.io.File;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class FileCleaningTracker {
    final List<String> deleteFailures = Collections.synchronizedList(new ArrayList());
    volatile boolean exitWhenFinished = false;
    /* renamed from: q */
    ReferenceQueue<Object> f71q = new ReferenceQueue();
    Thread reaper;
    final Collection<Tracker> trackers = Collections.synchronizedSet(new HashSet());

    private final class Reaper extends Thread {
        Reaper() {
            super("File Reaper");
            setPriority(10);
            setDaemon(true);
        }

        public void run() {
            while (true) {
                if (FileCleaningTracker.this.exitWhenFinished) {
                    if (FileCleaningTracker.this.trackers.size() <= 0) {
                        return;
                    }
                }
                try {
                    Tracker tracker = (Tracker) FileCleaningTracker.this.f71q.remove();
                    FileCleaningTracker.this.trackers.remove(tracker);
                    if (!tracker.delete()) {
                        FileCleaningTracker.this.deleteFailures.add(tracker.getPath());
                    }
                    tracker.clear();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private static final class Tracker extends PhantomReference<Object> {
        private final FileDeleteStrategy deleteStrategy;
        private final String path;

        Tracker(String path, FileDeleteStrategy deleteStrategy, Object marker, ReferenceQueue<? super Object> queue) {
            super(marker, queue);
            this.path = path;
            this.deleteStrategy = deleteStrategy == null ? FileDeleteStrategy.NORMAL : deleteStrategy;
        }

        public String getPath() {
            return this.path;
        }

        public boolean delete() {
            return this.deleteStrategy.deleteQuietly(new File(this.path));
        }
    }

    public void track(File file, Object marker) {
        track(file, marker, null);
    }

    public void track(File file, Object marker, FileDeleteStrategy deleteStrategy) {
        if (file != null) {
            addTracker(file.getPath(), marker, deleteStrategy);
            return;
        }
        throw new NullPointerException("The file must not be null");
    }

    public void track(String path, Object marker) {
        track(path, marker, null);
    }

    public void track(String path, Object marker, FileDeleteStrategy deleteStrategy) {
        if (path != null) {
            addTracker(path, marker, deleteStrategy);
            return;
        }
        throw new NullPointerException("The path must not be null");
    }

    private synchronized void addTracker(String path, Object marker, FileDeleteStrategy deleteStrategy) {
        if (this.exitWhenFinished) {
            throw new IllegalStateException("No new trackers can be added once exitWhenFinished() is called");
        }
        if (this.reaper == null) {
            this.reaper = new Reaper();
            this.reaper.start();
        }
        this.trackers.add(new Tracker(path, deleteStrategy, marker, this.f71q));
    }

    public int getTrackCount() {
        return this.trackers.size();
    }

    public List<String> getDeleteFailures() {
        return this.deleteFailures;
    }

    public synchronized void exitWhenFinished() {
        this.exitWhenFinished = true;
        if (this.reaper != null) {
            synchronized (this.reaper) {
                this.reaper.interrupt();
            }
        }
    }
}
