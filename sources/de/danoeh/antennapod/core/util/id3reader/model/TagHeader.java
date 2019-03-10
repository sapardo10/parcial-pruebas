package de.danoeh.antennapod.core.util.id3reader.model;

public class TagHeader extends Header {
    private final byte flags;
    private final char version;

    public TagHeader(String id, int size, char version, byte flags) {
        super(id, size);
        this.version = version;
        this.flags = flags;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("TagHeader [version=");
        stringBuilder.append(this.version);
        stringBuilder.append(", flags=");
        stringBuilder.append(this.flags);
        stringBuilder.append(", id=");
        stringBuilder.append(this.id);
        stringBuilder.append(", size=");
        stringBuilder.append(this.size);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public char getVersion() {
        return this.version;
    }
}
