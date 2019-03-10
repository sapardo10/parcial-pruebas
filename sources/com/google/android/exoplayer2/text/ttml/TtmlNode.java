package com.google.android.exoplayer2.text.ttml;

import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.util.Base64;
import android.util.Pair;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.util.Assertions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

final class TtmlNode {
    public static final String ANONYMOUS_REGION_ID = "";
    public static final String ATTR_ID = "id";
    public static final String ATTR_TTS_BACKGROUND_COLOR = "backgroundColor";
    public static final String ATTR_TTS_COLOR = "color";
    public static final String ATTR_TTS_DISPLAY_ALIGN = "displayAlign";
    public static final String ATTR_TTS_EXTENT = "extent";
    public static final String ATTR_TTS_FONT_FAMILY = "fontFamily";
    public static final String ATTR_TTS_FONT_SIZE = "fontSize";
    public static final String ATTR_TTS_FONT_STYLE = "fontStyle";
    public static final String ATTR_TTS_FONT_WEIGHT = "fontWeight";
    public static final String ATTR_TTS_ORIGIN = "origin";
    public static final String ATTR_TTS_TEXT_ALIGN = "textAlign";
    public static final String ATTR_TTS_TEXT_DECORATION = "textDecoration";
    public static final String BOLD = "bold";
    public static final String CENTER = "center";
    public static final String END = "end";
    public static final String ITALIC = "italic";
    public static final String LEFT = "left";
    public static final String LINETHROUGH = "linethrough";
    public static final String NO_LINETHROUGH = "nolinethrough";
    public static final String NO_UNDERLINE = "nounderline";
    public static final String RIGHT = "right";
    public static final String START = "start";
    public static final String TAG_BODY = "body";
    public static final String TAG_BR = "br";
    public static final String TAG_DATA = "data";
    public static final String TAG_DIV = "div";
    public static final String TAG_HEAD = "head";
    public static final String TAG_IMAGE = "image";
    public static final String TAG_INFORMATION = "information";
    public static final String TAG_LAYOUT = "layout";
    public static final String TAG_METADATA = "metadata";
    public static final String TAG_P = "p";
    public static final String TAG_REGION = "region";
    public static final String TAG_SPAN = "span";
    public static final String TAG_STYLE = "style";
    public static final String TAG_STYLING = "styling";
    public static final String TAG_TT = "tt";
    public static final String UNDERLINE = "underline";
    private List<TtmlNode> children;
    public final long endTimeUs;
    @Nullable
    public final String imageId;
    public final boolean isTextNode;
    private final HashMap<String, Integer> nodeEndsByRegion;
    private final HashMap<String, Integer> nodeStartsByRegion;
    public final String regionId;
    public final long startTimeUs;
    @Nullable
    public final TtmlStyle style;
    @Nullable
    private final String[] styleIds;
    @Nullable
    public final String tag;
    @Nullable
    public final String text;

    public static TtmlNode buildTextNode(String text) {
        return new TtmlNode(null, TtmlRenderUtil.applyTextElementSpacePolicy(text), C0555C.TIME_UNSET, C0555C.TIME_UNSET, null, null, "", null);
    }

    public static TtmlNode buildNode(@Nullable String tag, long startTimeUs, long endTimeUs, @Nullable TtmlStyle style, @Nullable String[] styleIds, String regionId, @Nullable String imageId) {
        return new TtmlNode(tag, null, startTimeUs, endTimeUs, style, styleIds, regionId, imageId);
    }

    private TtmlNode(@Nullable String tag, @Nullable String text, long startTimeUs, long endTimeUs, @Nullable TtmlStyle style, @Nullable String[] styleIds, String regionId, @Nullable String imageId) {
        this.tag = tag;
        this.text = text;
        this.imageId = imageId;
        this.style = style;
        this.styleIds = styleIds;
        this.isTextNode = text != null;
        this.startTimeUs = startTimeUs;
        this.endTimeUs = endTimeUs;
        this.regionId = (String) Assertions.checkNotNull(regionId);
        this.nodeStartsByRegion = new HashMap();
        this.nodeEndsByRegion = new HashMap();
    }

    public boolean isActive(long timeUs) {
        if (this.startTimeUs == C0555C.TIME_UNSET) {
            if (this.endTimeUs != C0555C.TIME_UNSET) {
            }
            return true;
        }
        if ((this.startTimeUs > timeUs || this.endTimeUs != C0555C.TIME_UNSET) && (this.startTimeUs != C0555C.TIME_UNSET || timeUs >= this.endTimeUs)) {
            if (this.startTimeUs > timeUs || timeUs >= this.endTimeUs) {
                return false;
            }
        }
        return true;
    }

    public void addChild(TtmlNode child) {
        if (this.children == null) {
            this.children = new ArrayList();
        }
        this.children.add(child);
    }

    public TtmlNode getChild(int index) {
        List list = this.children;
        if (list != null) {
            return (TtmlNode) list.get(index);
        }
        throw new IndexOutOfBoundsException();
    }

    public int getChildCount() {
        List list = this.children;
        return list == null ? 0 : list.size();
    }

    public long[] getEventTimesUs() {
        TreeSet<Long> eventTimeSet = new TreeSet();
        getEventTimes(eventTimeSet, false);
        long[] eventTimes = new long[eventTimeSet.size()];
        int i = 0;
        Iterator it = eventTimeSet.iterator();
        while (it.hasNext()) {
            int i2 = i + 1;
            eventTimes[i] = ((Long) it.next()).longValue();
            i = i2;
        }
        return eventTimes;
    }

    private void getEventTimes(TreeSet<Long> out, boolean descendsPNode) {
        int i;
        TtmlNode ttmlNode;
        boolean z;
        boolean isPNode = TAG_P.equals(this.tag);
        boolean isDivNode = TAG_DIV.equals(this.tag);
        if (!(descendsPNode || isPNode)) {
            if (!isDivNode || this.imageId == null) {
                if (this.children == null) {
                    for (i = 0; i < this.children.size(); i++) {
                        ttmlNode = (TtmlNode) this.children.get(i);
                        if (!descendsPNode) {
                            if (isPNode) {
                                z = false;
                                ttmlNode.getEventTimes(out, z);
                            }
                        }
                        z = true;
                        ttmlNode.getEventTimes(out, z);
                    }
                }
            }
        }
        long j = this.startTimeUs;
        if (j != C0555C.TIME_UNSET) {
            out.add(Long.valueOf(j));
        }
        j = this.endTimeUs;
        if (j != C0555C.TIME_UNSET) {
            out.add(Long.valueOf(j));
        }
        if (this.children == null) {
            for (i = 0; i < this.children.size(); i++) {
                ttmlNode = (TtmlNode) this.children.get(i);
                if (descendsPNode) {
                    if (isPNode) {
                        z = false;
                        ttmlNode.getEventTimes(out, z);
                    }
                }
                z = true;
                ttmlNode.getEventTimes(out, z);
            }
        }
    }

    public String[] getStyleIds() {
        return this.styleIds;
    }

    public List<Cue> getCues(long timeUs, Map<String, TtmlStyle> globalStyles, Map<String, TtmlRegion> regionMap, Map<String, String> imageMap) {
        long j = timeUs;
        Map<String, TtmlRegion> map = regionMap;
        ArrayList regionImageOutputs = new ArrayList();
        traverseForImage(j, this.regionId, regionImageOutputs);
        TreeMap<String, SpannableStringBuilder> regionTextOutputs = new TreeMap();
        traverseForText(timeUs, false, this.regionId, regionTextOutputs);
        traverseForStyle(j, globalStyles, regionTextOutputs);
        List<Cue> cues = new ArrayList();
        Iterator it = regionImageOutputs.iterator();
        while (it.hasNext()) {
            Pair<String, String> regionImagePair = (Pair) it.next();
            String encodedBitmapData = (String) imageMap.get(regionImagePair.second);
            if (encodedBitmapData != null) {
                byte[] bitmapData = Base64.decode(encodedBitmapData, 0);
                TtmlRegion region = (TtmlRegion) map.get(regionImagePair.first);
                Iterator it2 = it;
                Cue cue = r15;
                Cue cue2 = new Cue(BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length), region.position, 1, region.line, region.lineAnchor, region.width, Float.MIN_VALUE);
                cues.add(cue);
                it = it2;
                Map<String, TtmlStyle> map2 = globalStyles;
            }
        }
        Map<String, String> map3 = imageMap;
        for (Entry<String, SpannableStringBuilder> entry : regionTextOutputs.entrySet()) {
            TtmlRegion region2 = (TtmlRegion) map.get(entry.getKey());
            cues.add(new Cue(cleanUpText((SpannableStringBuilder) entry.getValue()), null, region2.line, region2.lineType, region2.lineAnchor, region2.position, Integer.MIN_VALUE, region2.width, region2.textSizeType, region2.textSize));
        }
        return cues;
    }

    private void traverseForImage(long timeUs, String inheritedRegion, List<Pair<String, String>> regionImageList) {
        String resolvedRegionId = "".equals(this.regionId) ? inheritedRegion : this.regionId;
        if (isActive(timeUs) && TAG_DIV.equals(this.tag)) {
            String str = this.imageId;
            if (str != null) {
                regionImageList.add(new Pair(resolvedRegionId, str));
                return;
            }
        }
        for (int i = 0; i < getChildCount(); i++) {
            getChild(i).traverseForImage(timeUs, resolvedRegionId, regionImageList);
        }
    }

    private void traverseForText(long timeUs, boolean descendsPNode, String inheritedRegion, Map<String, SpannableStringBuilder> regionOutputs) {
        this.nodeStartsByRegion.clear();
        this.nodeEndsByRegion.clear();
        if (!TAG_METADATA.equals(this.tag)) {
            String resolvedRegionId = "".equals(this.regionId) ? inheritedRegion : this.regionId;
            if (this.isTextNode && descendsPNode) {
                getRegionOutput(resolvedRegionId, regionOutputs).append(this.text);
            } else if (TAG_BR.equals(this.tag) && descendsPNode) {
                getRegionOutput(resolvedRegionId, regionOutputs).append('\n');
            } else if (isActive(timeUs)) {
                for (Entry<String, SpannableStringBuilder> entry : regionOutputs.entrySet()) {
                    this.nodeStartsByRegion.put(entry.getKey(), Integer.valueOf(((SpannableStringBuilder) entry.getValue()).length()));
                }
                boolean isPNode = TAG_P.equals(this.tag);
                for (int i = 0; i < getChildCount(); i++) {
                    boolean z;
                    TtmlNode child = getChild(i);
                    if (!descendsPNode) {
                        if (!isPNode) {
                            z = false;
                            child.traverseForText(timeUs, z, resolvedRegionId, regionOutputs);
                        }
                    }
                    z = true;
                    child.traverseForText(timeUs, z, resolvedRegionId, regionOutputs);
                }
                if (isPNode) {
                    TtmlRenderUtil.endParagraph(getRegionOutput(resolvedRegionId, regionOutputs));
                }
                for (Entry<String, SpannableStringBuilder> entry2 : regionOutputs.entrySet()) {
                    this.nodeEndsByRegion.put(entry2.getKey(), Integer.valueOf(((SpannableStringBuilder) entry2.getValue()).length()));
                }
            }
        }
    }

    private static SpannableStringBuilder getRegionOutput(String resolvedRegionId, Map<String, SpannableStringBuilder> regionOutputs) {
        if (!regionOutputs.containsKey(resolvedRegionId)) {
            regionOutputs.put(resolvedRegionId, new SpannableStringBuilder());
        }
        return (SpannableStringBuilder) regionOutputs.get(resolvedRegionId);
    }

    private void traverseForStyle(long timeUs, Map<String, TtmlStyle> globalStyles, Map<String, SpannableStringBuilder> regionOutputs) {
        if (isActive(timeUs)) {
            for (Entry<String, Integer> entry : this.nodeEndsByRegion.entrySet()) {
                String regionId = (String) entry.getKey();
                int start = this.nodeStartsByRegion.containsKey(regionId) ? ((Integer) this.nodeStartsByRegion.get(regionId)).intValue() : 0;
                int end = ((Integer) entry.getValue()).intValue();
                if (start != end) {
                    applyStyleToOutput(globalStyles, (SpannableStringBuilder) regionOutputs.get(regionId), start, end);
                }
            }
            for (int i = 0; i < getChildCount(); i++) {
                getChild(i).traverseForStyle(timeUs, globalStyles, regionOutputs);
            }
        }
    }

    private void applyStyleToOutput(Map<String, TtmlStyle> globalStyles, SpannableStringBuilder regionOutput, int start, int end) {
        TtmlStyle resolvedStyle = TtmlRenderUtil.resolveStyle(this.style, this.styleIds, globalStyles);
        if (resolvedStyle != null) {
            TtmlRenderUtil.applyStylesToSpan(regionOutput, start, end, resolvedStyle);
        }
    }

    private SpannableStringBuilder cleanUpText(SpannableStringBuilder builder) {
        int i;
        int builderLength = builder.length();
        for (i = 0; i < builderLength; i++) {
            if (builder.charAt(i) == ' ') {
                int j = i + 1;
                while (j < builder.length() && builder.charAt(j) == ' ') {
                    j++;
                }
                int spacesToDelete = j - (i + 1);
                if (spacesToDelete > 0) {
                    builder.delete(i, i + spacesToDelete);
                    builderLength -= spacesToDelete;
                }
            }
        }
        if (builderLength > 0 && builder.charAt(0) == ' ') {
            builder.delete(0, 1);
            builderLength--;
        }
        i = 0;
        while (i < builderLength - 1) {
            if (builder.charAt(i) == '\n' && builder.charAt(i + 1) == ' ') {
                builder.delete(i + 1, i + 2);
                builderLength--;
            }
            i++;
        }
        if (builderLength > 0 && builder.charAt(builderLength - 1) == ' ') {
            builder.delete(builderLength - 1, builderLength);
            builderLength--;
        }
        i = 0;
        while (i < builderLength - 1) {
            if (builder.charAt(i) == ' ' && builder.charAt(i + 1) == '\n') {
                builder.delete(i, i + 1);
                builderLength--;
            }
            i++;
        }
        if (builderLength > 0 && builder.charAt(builderLength - 1) == '\n') {
            builder.delete(builderLength - 1, builderLength);
        }
        return builder;
    }
}
