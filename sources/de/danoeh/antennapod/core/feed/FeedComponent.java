package de.danoeh.antennapod.core.feed;

public abstract class FeedComponent {
    long id;

    public abstract String getHumanReadableIdentifier();

    FeedComponent() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    void updateFromOther(FeedComponent other) {
    }

    boolean compareWithOther(FeedComponent other) {
        return false;
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (this == o) {
            return true;
        }
        if (o != null) {
            if (o instanceof FeedComponent) {
                if (this.id != ((FeedComponent) o).id) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        long j = this.id;
        return (int) (j ^ (j >>> 32));
    }
}
