package de.danoeh.antennapod.core.util.playback;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import de.danoeh.antennapod.core.C0734R;
import de.danoeh.antennapod.core.util.Converter;
import de.danoeh.antennapod.core.util.ShownotesProvider;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Timeline {
    private static final Pattern LINE_BREAK_REGEX = Pattern.compile("<br */?>");
    private static final String TAG = "Timeline";
    private static final String TIMECODE_LINK = "<a class=\"timecode\" href=\"antennapod://timecode/%d\">%s</a>";
    private static final Pattern TIMECODE_LINK_REGEX = Pattern.compile("antennapod://timecode/((\\d+))");
    private static final Pattern TIMECODE_REGEX = Pattern.compile("\\b((\\d+):)?(\\d+):(\\d{2})\\b");
    private static final String WEBVIEW_STYLE = "@font-face { font-family: 'Roboto-Light'; src: url('file:///android_asset/Roboto-Light.ttf'); } * { color: %s; font-family: roboto-Light; font-size: 13pt; } a { font-style: normal; text-decoration: none; font-weight: normal; color: #00A8DF; } a.timecode { color: #669900; } img { display: block; margin: 10 auto; max-width: %s; height: auto; } body { margin: %dpx %dpx %dpx %dpx; }";
    private final String colorPrimaryString;
    private final String colorSecondaryString;
    private final String noShownotesLabel;
    private final int pageMargin;
    private ShownotesProvider shownotesProvider;

    public Timeline(Context context, ShownotesProvider shownotesProvider) {
        if (shownotesProvider != null) {
            this.shownotesProvider = shownotesProvider;
            this.noShownotesLabel = context.getString(C0734R.string.no_shownotes_label);
            TypedArray res = context.getTheme().obtainStyledAttributes(new int[]{16842806});
            int col = res.getColor(0, 0);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("rgba(");
            stringBuilder.append(Color.red(col));
            stringBuilder.append(",");
            stringBuilder.append(Color.green(col));
            stringBuilder.append(",");
            stringBuilder.append(Color.blue(col));
            stringBuilder.append(",");
            double alpha = (double) Color.alpha(col);
            Double.isNaN(alpha);
            stringBuilder.append(alpha / 255.0d);
            stringBuilder.append(")");
            this.colorPrimaryString = stringBuilder.toString();
            res.recycle();
            res = context.getTheme().obtainStyledAttributes(new int[]{16842808});
            col = res.getColor(0, 0);
            stringBuilder = new StringBuilder();
            stringBuilder.append("rgba(");
            stringBuilder.append(Color.red(col));
            stringBuilder.append(",");
            stringBuilder.append(Color.green(col));
            stringBuilder.append(",");
            stringBuilder.append(Color.blue(col));
            stringBuilder.append(",");
            double alpha2 = (double) Color.alpha(col);
            Double.isNaN(alpha2);
            stringBuilder.append(alpha2 / 255.0d);
            stringBuilder.append(")");
            this.colorSecondaryString = stringBuilder.toString();
            res.recycle();
            this.pageMargin = (int) TypedValue.applyDimension(1, 8.0f, context.getResources().getDisplayMetrics());
            return;
        }
        throw new IllegalArgumentException("shownotesProvider = null");
    }

    public String processShownotes(boolean addTimecodes) {
        ShownotesProvider shownotesProvider = this.shownotesProvider;
        Playable playable = shownotesProvider instanceof Playable ? (Playable) shownotesProvider : null;
        try {
            String shownotes = (String) this.shownotesProvider.loadShownotes().call();
            if (TextUtils.isEmpty(shownotes)) {
                Log.d(TAG, "shownotesProvider contained no shownotes. Returning 'no shownotes' message");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("<html><head><style type='text/css'>html, body { margin: 0; padding: 0; width: 100%; height: 100%; } html { display: table; }body { display: table-cell; vertical-align: middle; text-align:center;-webkit-text-size-adjust: none; font-size: 87%; color: ");
                stringBuilder.append(this.colorSecondaryString);
                stringBuilder.append(";} </style></head><body><p>");
                stringBuilder.append(this.noShownotesLabel);
                stringBuilder.append("</p></body></html>");
                shownotes = stringBuilder.toString();
                String str = TAG;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("shownotes: ");
                stringBuilder2.append(shownotes);
                Log.d(str, stringBuilder2.toString());
                return shownotes;
            }
            if (!LINE_BREAK_REGEX.matcher(shownotes).find() && !shownotes.contains("<p>")) {
                shownotes = shownotes.replace("\n", "<br />");
            }
            Document document = Jsoup.parse(shownotes);
            document.head().appendElement(TtmlNode.TAG_STYLE).attr("type", "text/css").text(String.format(Locale.getDefault(), WEBVIEW_STYLE, new Object[]{this.colorPrimaryString, "100%", Integer.valueOf(this.pageMargin), Integer.valueOf(this.pageMargin), Integer.valueOf(this.pageMargin), Integer.valueOf(this.pageMargin)}));
            if (addTimecodes) {
                addTimecodes(document, playable);
            }
            return document.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isTimecodeLink(String link) {
        return link != null && link.matches(TIMECODE_LINK_REGEX.pattern());
    }

    public static int getTimecodeLinkTime(String link) {
        if (isTimecodeLink(link)) {
            Matcher m = TIMECODE_LINK_REGEX.matcher(link);
            try {
                if (m.find()) {
                    return Integer.parseInt(m.group(1));
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public void setShownotesProvider(@NonNull ShownotesProvider shownotesProvider) {
        this.shownotesProvider = shownotesProvider;
    }

    private void addTimecodes(Document document, Playable playable) {
        Elements elementsWithTimeCodes = document.body().getElementsMatchingOwnText(TIMECODE_REGEX);
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Recognized ");
        stringBuilder.append(elementsWithTimeCodes.size());
        stringBuilder.append(" timecodes");
        Log.d(str, stringBuilder.toString());
        if (elementsWithTimeCodes.size() != 0) {
            Iterator it;
            Matcher matcherForElement;
            int playableDuration = playable == null ? Integer.MAX_VALUE : playable.getDuration();
            boolean useHourFormat = true;
            if (playableDuration != Integer.MAX_VALUE) {
                it = elementsWithTimeCodes.iterator();
                while (it.hasNext()) {
                    matcherForElement = TIMECODE_REGEX.matcher(((Element) it.next()).html());
                    while (matcherForElement.find()) {
                        if (matcherForElement.group(1) == null) {
                            if (Converter.durationStringShortToMs(matcherForElement.group(0), true) > playableDuration) {
                                useHourFormat = false;
                                break;
                            }
                        }
                    }
                    if (!useHourFormat) {
                        break;
                    }
                }
            }
            it = elementsWithTimeCodes.iterator();
            while (it.hasNext()) {
                Element element = (Element) it.next();
                matcherForElement = TIMECODE_REGEX.matcher(element.html());
                StringBuffer buffer = new StringBuffer();
                while (matcherForElement.find()) {
                    int time;
                    String group = matcherForElement.group(0);
                    if (matcherForElement.group(1) != null) {
                        time = Converter.durationStringLongToMs(group);
                    } else {
                        time = Converter.durationStringShortToMs(group, useHourFormat);
                    }
                    String replacementText = group;
                    if (time < playableDuration) {
                        replacementText = String.format(Locale.getDefault(), TIMECODE_LINK, new Object[]{Integer.valueOf(time), group});
                    }
                    matcherForElement.appendReplacement(buffer, replacementText);
                }
                matcherForElement.appendTail(buffer);
                element.html(buffer.toString());
            }
        }
    }
}
