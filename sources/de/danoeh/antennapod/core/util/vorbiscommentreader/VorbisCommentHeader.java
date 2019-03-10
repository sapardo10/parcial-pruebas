package de.danoeh.antennapod.core.util.vorbiscommentreader;

class VorbisCommentHeader {
    private final long userCommentLength;
    private final String vendorString;

    public VorbisCommentHeader(String vendorString, long userCommentLength) {
        this.vendorString = vendorString;
        this.userCommentLength = userCommentLength;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("VorbisCommentHeader [vendorString=");
        stringBuilder.append(this.vendorString);
        stringBuilder.append(", userCommentLength=");
        stringBuilder.append(this.userCommentLength);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public String getVendorString() {
        return this.vendorString;
    }

    public long getUserCommentLength() {
        return this.userCommentLength;
    }
}
