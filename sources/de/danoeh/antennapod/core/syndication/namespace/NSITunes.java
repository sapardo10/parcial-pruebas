package de.danoeh.antennapod.core.syndication.namespace;

import android.text.TextUtils;
import android.util.Log;
import de.danoeh.antennapod.core.syndication.handler.HandlerState;
import java.util.concurrent.TimeUnit;
import org.xml.sax.Attributes;

public class NSITunes extends Namespace {
    private static final String AUTHOR = "author";
    public static final String DURATION = "duration";
    private static final String IMAGE = "image";
    private static final String IMAGE_HREF = "href";
    public static final String NSTAG = "itunes";
    public static final String NSURI = "http://www.itunes.com/dtds/podcast-1.0.dtd";
    private static final String SUBTITLE = "subtitle";
    private static final String SUMMARY = "summary";

    public SyndElement handleElementStart(String localName, HandlerState state, Attributes attributes) {
        if ("image".equals(localName)) {
            String url = attributes.getValue(IMAGE_HREF);
            if (state.getCurrentItem() != null) {
                state.getCurrentItem().setImageUrl(url);
            } else if (!TextUtils.isEmpty(url)) {
                state.getFeed().setImageUrl(url);
            }
        }
        return new SyndElement(localName, this);
    }

    public void handleElementEnd(String localName, HandlerState state) {
        if (state.getContentBuf() != null) {
            String second = state.getSecondTag().getName();
            if ("author".equals(localName)) {
                if (state.getFeed() != null) {
                    state.getFeed().setAuthor(state.getContentBuf().toString());
                }
            } else if ("duration".equals(localName)) {
                durationStr = state.getContentBuf().toString();
                if (!TextUtils.isEmpty(durationStr)) {
                    String[] parts = durationStr.trim().split(":");
                    try {
                        int durationMs;
                        if (parts.length == 2) {
                            durationMs = (int) (((long) null) + (TimeUnit.MINUTES.toMillis(Long.parseLong(parts[0])) + TimeUnit.SECONDS.toMillis((long) Float.parseFloat(parts[1]))));
                        } else if (parts.length >= 3) {
                            durationMs = (int) (((long) null) + ((TimeUnit.HOURS.toMillis(Long.parseLong(parts[0])) + TimeUnit.MINUTES.toMillis(Long.parseLong(parts[1]))) + TimeUnit.SECONDS.toMillis((long) Float.parseFloat(parts[2]))));
                        } else {
                            return;
                        }
                        state.getTempObjects().put("duration", Integer.valueOf(durationMs));
                    } catch (NumberFormatException e) {
                        String str = NSTAG;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Duration \"");
                        stringBuilder.append(durationStr);
                        stringBuilder.append("\" could not be parsed");
                        Log.e(str, stringBuilder.toString());
                    }
                }
            } else if (SUBTITLE.equals(localName)) {
                durationStr = state.getContentBuf().toString();
                if (!TextUtils.isEmpty(durationStr)) {
                    if (state.getCurrentItem() != null) {
                        if (TextUtils.isEmpty(state.getCurrentItem().getDescription())) {
                            state.getCurrentItem().setDescription(durationStr);
                        }
                    } else if (state.getFeed() != null && TextUtils.isEmpty(state.getFeed().getDescription())) {
                        state.getFeed().setDescription(durationStr);
                    }
                }
            } else if (SUMMARY.equals(localName)) {
                durationStr = state.getContentBuf().toString();
                if (!TextUtils.isEmpty(durationStr)) {
                    if (state.getCurrentItem() != null) {
                        if (!TextUtils.isEmpty(state.getCurrentItem().getDescription())) {
                            double length = (double) state.getCurrentItem().getDescription().length();
                            Double.isNaN(length);
                            if (length * 1.25d < ((double) durationStr.length())) {
                            }
                        }
                        state.getCurrentItem().setDescription(durationStr);
                    }
                    if (NSRSS20.CHANNEL.equals(second) && state.getFeed() != null) {
                        state.getFeed().setDescription(durationStr);
                    }
                }
            }
        }
    }
}
