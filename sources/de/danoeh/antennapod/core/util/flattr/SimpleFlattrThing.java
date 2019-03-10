package de.danoeh.antennapod.core.util.flattr;

public class SimpleFlattrThing implements FlattrThing {
    private final FlattrStatus status;
    private final String title;
    private final String url;

    public SimpleFlattrThing(String title, String url, FlattrStatus status) {
        this.title = title;
        this.url = url;
        this.status = status;
    }

    public String getTitle() {
        return this.title;
    }

    public String getPaymentLink() {
        return this.url;
    }

    public FlattrStatus getFlattrStatus() {
        return this.status;
    }
}
