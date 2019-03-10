package de.danoeh.antennapod.core.syndication.namespace;

import android.text.TextUtils;
import android.util.Log;
import de.danoeh.antennapod.core.feed.FeedMedia;
import de.danoeh.antennapod.core.syndication.handler.HandlerState;
import de.danoeh.antennapod.core.syndication.namespace.atom.AtomText;
import de.danoeh.antennapod.core.syndication.util.SyndTypeUtils;
import java.util.concurrent.TimeUnit;
import org.xml.sax.Attributes;

public class NSMedia extends Namespace {
    private static final String CONTENT = "content";
    private static final String DEFAULT = "isDefault";
    private static final String DESCRIPTION = "description";
    private static final String DESCRIPTION_TYPE = "type";
    private static final String DOWNLOAD_URL = "url";
    private static final String DURATION = "duration";
    private static final String IMAGE = "thumbnail";
    private static final String IMAGE_URL = "url";
    private static final String MEDIUM = "medium";
    private static final String MEDIUM_AUDIO = "audio";
    private static final String MEDIUM_IMAGE = "image";
    private static final String MEDIUM_VIDEO = "video";
    private static final String MIME_TYPE = "type";
    public static final String NSTAG = "media";
    public static final String NSURI = "http://search.yahoo.com/mrss/";
    private static final String SIZE = "fileSize";
    private static final String TAG = "NSMedia";

    public SyndElement handleElementStart(String localName, HandlerState state, Attributes attributes) {
        String type;
        String sizeStr;
        long size;
        String str;
        StringBuilder stringBuilder;
        Namespace namespace = this;
        String str2 = localName;
        Attributes attributes2 = attributes;
        String type2;
        if ("content".equals(str2)) {
            boolean validTypeMedia;
            boolean validTypeImage;
            String durationStr;
            int durationMs;
            FeedMedia media;
            String url = attributes2.getValue("url");
            type2 = attributes2.getValue("type");
            String defaultStr = attributes2.getValue(DEFAULT);
            String medium = attributes2.getValue(MEDIUM);
            boolean isDefault = "true".equals(defaultStr);
            if (!"audio".equals(medium)) {
                if (!"video".equals(medium)) {
                    if ("image".equals(medium)) {
                        type = type2;
                        validTypeMedia = false;
                        validTypeImage = true;
                    } else {
                        if (type2 == null) {
                            type2 = SyndTypeUtils.getMimeTypeFromUrl(url);
                        }
                        if (SyndTypeUtils.enclosureTypeValid(type2)) {
                            type = type2;
                            validTypeMedia = true;
                            validTypeImage = false;
                        } else if (SyndTypeUtils.imageTypeValid(type2)) {
                            type = type2;
                            validTypeMedia = false;
                            validTypeImage = true;
                        } else {
                            type = type2;
                            validTypeMedia = false;
                            validTypeImage = false;
                        }
                    }
                    if (state.getCurrentItem() != null) {
                        if ((state.getCurrentItem().getMedia() != null || isDefault) && url != null && validTypeMedia) {
                            sizeStr = attributes2.getValue(SIZE);
                            try {
                                size = Long.parseLong(sizeStr);
                            } catch (NumberFormatException e) {
                                String str3 = TAG;
                                StringBuilder stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("Size \"");
                                stringBuilder2.append(sizeStr);
                                stringBuilder2.append("\" could not be parsed.");
                                Log.e(str3, stringBuilder2.toString());
                                size = 0;
                            }
                            durationStr = attributes2.getValue("duration");
                            if (TextUtils.isEmpty(durationStr)) {
                                try {
                                } catch (NumberFormatException e2) {
                                    String str4 = sizeStr;
                                    str = TAG;
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("Duration \"");
                                    stringBuilder.append(durationStr);
                                    stringBuilder.append("\" could not be parsed");
                                    Log.e(str, stringBuilder.toString());
                                    durationMs = 0;
                                    media = new FeedMedia(state.getCurrentItem(), url, size, type);
                                    if (durationMs > 0) {
                                        media.setDuration(durationMs);
                                    }
                                    state.getCurrentItem().setMedia(media);
                                    return new SyndElement(str2, namespace);
                                }
                                try {
                                    durationMs = (int) TimeUnit.MILLISECONDS.convert(Long.parseLong(durationStr), TimeUnit.SECONDS);
                                } catch (NumberFormatException e3) {
                                    str = TAG;
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("Duration \"");
                                    stringBuilder.append(durationStr);
                                    stringBuilder.append("\" could not be parsed");
                                    Log.e(str, stringBuilder.toString());
                                    durationMs = 0;
                                    media = new FeedMedia(state.getCurrentItem(), url, size, type);
                                    if (durationMs > 0) {
                                        media.setDuration(durationMs);
                                    }
                                    state.getCurrentItem().setMedia(media);
                                    return new SyndElement(str2, namespace);
                                }
                            }
                            durationMs = 0;
                            media = new FeedMedia(state.getCurrentItem(), url, size, type);
                            if (durationMs > 0) {
                                media.setDuration(durationMs);
                            }
                            state.getCurrentItem().setMedia(media);
                        }
                    }
                    if (!(state.getCurrentItem() == null || url == null || !validTypeImage)) {
                        state.getCurrentItem().setImageUrl(url);
                    }
                }
            }
            type = type2;
            validTypeMedia = true;
            validTypeImage = false;
            if (state.getCurrentItem() != null) {
                if (state.getCurrentItem().getMedia() != null) {
                }
                sizeStr = attributes2.getValue(SIZE);
                size = Long.parseLong(sizeStr);
                durationStr = attributes2.getValue("duration");
                if (TextUtils.isEmpty(durationStr)) {
                } else {
                    durationMs = (int) TimeUnit.MILLISECONDS.convert(Long.parseLong(durationStr), TimeUnit.SECONDS);
                    media = new FeedMedia(state.getCurrentItem(), url, size, type);
                    if (durationMs > 0) {
                        media.setDuration(durationMs);
                    }
                    state.getCurrentItem().setMedia(media);
                }
                durationMs = 0;
                media = new FeedMedia(state.getCurrentItem(), url, size, type);
                if (durationMs > 0) {
                    media.setDuration(durationMs);
                }
                state.getCurrentItem().setMedia(media);
            }
            state.getCurrentItem().setImageUrl(url);
        } else if (IMAGE.equals(str2)) {
            type2 = attributes2.getValue("url");
            if (type2 != null) {
                if (state.getCurrentItem() != null) {
                    state.getCurrentItem().setImageUrl(type2);
                } else if (state.getFeed().getImageUrl() == null) {
                    state.getFeed().setImageUrl(type2);
                }
            }
        } else if ("description".equals(str2)) {
            return new AtomText(str2, namespace, attributes2.getValue("type"));
        }
        return new SyndElement(str2, namespace);
    }

    public void handleElementEnd(String localName, HandlerState state) {
        if ("description".equals(localName)) {
            String content = state.getContentBuf().toString();
            if (state.getCurrentItem() != null && content != null) {
                if (state.getCurrentItem().getDescription() == null) {
                    state.getCurrentItem().setDescription(content);
                }
            }
        }
    }
}
