package de.danoeh.antennapod.core.util.id3reader.model;

public abstract class Header {
    final String id;
    final int size;

    Header(String id, int size) {
        this.id = id;
        this.size = size;
    }

    public String getId() {
        return this.id;
    }

    public int getSize() {
        return this.size;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Header [id=");
        stringBuilder.append(this.id);
        stringBuilder.append(", size=");
        stringBuilder.append(this.size);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
