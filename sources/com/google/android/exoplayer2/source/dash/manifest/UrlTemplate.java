package com.google.android.exoplayer2.source.dash.manifest;

import java.util.Locale;

public final class UrlTemplate {
    private static final String BANDWIDTH = "Bandwidth";
    private static final int BANDWIDTH_ID = 3;
    private static final String DEFAULT_FORMAT_TAG = "%01d";
    private static final String ESCAPED_DOLLAR = "$$";
    private static final String NUMBER = "Number";
    private static final int NUMBER_ID = 2;
    private static final String REPRESENTATION = "RepresentationID";
    private static final int REPRESENTATION_ID = 1;
    private static final String TIME = "Time";
    private static final int TIME_ID = 4;
    private final int identifierCount;
    private final String[] identifierFormatTags;
    private final int[] identifiers;
    private final String[] urlPieces;

    public static UrlTemplate compile(String template) {
        String[] urlPieces = new String[5];
        int[] identifiers = new int[4];
        String[] identifierFormatTags = new String[4];
        return new UrlTemplate(urlPieces, identifiers, identifierFormatTags, parseTemplate(template, urlPieces, identifiers, identifierFormatTags));
    }

    private UrlTemplate(String[] urlPieces, int[] identifiers, String[] identifierFormatTags, int identifierCount) {
        this.urlPieces = urlPieces;
        this.identifiers = identifiers;
        this.identifierFormatTags = identifierFormatTags;
        this.identifierCount = identifierCount;
    }

    public String buildUri(String representationId, long segmentNumber, int bandwidth, long time) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (true) {
            int i2 = this.identifierCount;
            if (i < i2) {
                builder.append(this.urlPieces[i]);
                int[] iArr = this.identifiers;
                if (iArr[i] == 1) {
                    builder.append(representationId);
                } else if (iArr[i] == 2) {
                    builder.append(String.format(Locale.US, this.identifierFormatTags[i], new Object[]{Long.valueOf(segmentNumber)}));
                } else if (iArr[i] == 3) {
                    builder.append(String.format(Locale.US, this.identifierFormatTags[i], new Object[]{Integer.valueOf(bandwidth)}));
                } else if (iArr[i] == 4) {
                    builder.append(String.format(Locale.US, this.identifierFormatTags[i], new Object[]{Long.valueOf(time)}));
                }
                i++;
            } else {
                builder.append(this.urlPieces[i2]);
                return builder.toString();
            }
        }
    }

    private static int parseTemplate(String template, String[] urlPieces, int[] identifiers, String[] identifierFormatTags) {
        String str = template;
        urlPieces[0] = "";
        int templateIndex = 0;
        int identifierCount = 0;
        while (templateIndex < template.length()) {
            int dollarIndex = template.indexOf("$", templateIndex);
            Object obj = -1;
            StringBuilder stringBuilder;
            if (dollarIndex == -1) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(urlPieces[identifierCount]);
                stringBuilder.append(template.substring(templateIndex));
                urlPieces[identifierCount] = stringBuilder.toString();
                templateIndex = template.length();
            } else if (dollarIndex != templateIndex) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(urlPieces[identifierCount]);
                stringBuilder.append(template.substring(templateIndex, dollarIndex));
                urlPieces[identifierCount] = stringBuilder.toString();
                templateIndex = dollarIndex;
            } else if (template.startsWith(ESCAPED_DOLLAR, templateIndex)) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(urlPieces[identifierCount]);
                stringBuilder.append("$");
                urlPieces[identifierCount] = stringBuilder.toString();
                templateIndex += 2;
            } else {
                int secondIndex = template.indexOf("$", templateIndex + 1);
                String identifier = template.substring(templateIndex + 1, secondIndex);
                if (identifier.equals(REPRESENTATION)) {
                    identifiers[identifierCount] = 1;
                } else {
                    int formatTagIndex = identifier.indexOf("%0");
                    String formatTag = DEFAULT_FORMAT_TAG;
                    if (formatTagIndex != -1) {
                        formatTag = identifier.substring(formatTagIndex);
                        if (!formatTag.endsWith("d")) {
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(formatTag);
                            stringBuilder2.append("d");
                            formatTag = stringBuilder2.toString();
                        }
                        identifier = identifier.substring(0, formatTagIndex);
                    }
                    int hashCode = identifier.hashCode();
                    if (hashCode != -1950496919) {
                        if (hashCode != 2606829) {
                            if (hashCode == 38199441 && identifier.equals(BANDWIDTH)) {
                                obj = 1;
                                switch (obj) {
                                    case null:
                                        identifiers[identifierCount] = 2;
                                        break;
                                    case 1:
                                        identifiers[identifierCount] = 3;
                                        break;
                                    case 2:
                                        identifiers[identifierCount] = 4;
                                        break;
                                    default:
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("Invalid template: ");
                                        stringBuilder.append(template);
                                        throw new IllegalArgumentException(stringBuilder.toString());
                                }
                                identifierFormatTags[identifierCount] = formatTag;
                            }
                        } else if (identifier.equals(TIME)) {
                            obj = 2;
                            switch (obj) {
                                case null:
                                    identifiers[identifierCount] = 2;
                                    break;
                                case 1:
                                    identifiers[identifierCount] = 3;
                                    break;
                                case 2:
                                    identifiers[identifierCount] = 4;
                                    break;
                                default:
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("Invalid template: ");
                                    stringBuilder.append(template);
                                    throw new IllegalArgumentException(stringBuilder.toString());
                            }
                            identifierFormatTags[identifierCount] = formatTag;
                        }
                    } else if (identifier.equals(NUMBER)) {
                        obj = null;
                        switch (obj) {
                            case null:
                                identifiers[identifierCount] = 2;
                                break;
                            case 1:
                                identifiers[identifierCount] = 3;
                                break;
                            case 2:
                                identifiers[identifierCount] = 4;
                                break;
                            default:
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("Invalid template: ");
                                stringBuilder.append(template);
                                throw new IllegalArgumentException(stringBuilder.toString());
                        }
                        identifierFormatTags[identifierCount] = formatTag;
                    }
                    switch (obj) {
                        case null:
                            identifiers[identifierCount] = 2;
                            break;
                        case 1:
                            identifiers[identifierCount] = 3;
                            break;
                        case 2:
                            identifiers[identifierCount] = 4;
                            break;
                        default:
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Invalid template: ");
                            stringBuilder.append(template);
                            throw new IllegalArgumentException(stringBuilder.toString());
                    }
                    identifierFormatTags[identifierCount] = formatTag;
                }
                identifierCount++;
                urlPieces[identifierCount] = "";
                templateIndex = secondIndex + 1;
            }
        }
        return identifierCount;
    }
}
