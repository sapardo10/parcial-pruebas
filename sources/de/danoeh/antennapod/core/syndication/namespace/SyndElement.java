package de.danoeh.antennapod.core.syndication.namespace;

public class SyndElement {
    private final String name;
    private final Namespace namespace;

    public SyndElement(String name, Namespace namespace) {
        this.name = name;
        this.namespace = namespace;
    }

    public Namespace getNamespace() {
        return this.namespace;
    }

    public String getName() {
        return this.name;
    }
}
