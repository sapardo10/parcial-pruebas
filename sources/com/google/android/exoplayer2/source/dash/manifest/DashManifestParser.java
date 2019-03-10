package com.google.android.exoplayer2.source.dash.manifest;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Pair;
import android.util.Xml;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmInitData.SchemeData;
import com.google.android.exoplayer2.extractor.mp4.PsshAtomUtil;
import com.google.android.exoplayer2.metadata.emsg.EventMessage;
import com.google.android.exoplayer2.source.dash.manifest.SegmentBase.SegmentList;
import com.google.android.exoplayer2.source.dash.manifest.SegmentBase.SegmentTemplate;
import com.google.android.exoplayer2.source.dash.manifest.SegmentBase.SegmentTimelineElement;
import com.google.android.exoplayer2.source.dash.manifest.SegmentBase.SingleSegmentBase;
import com.google.android.exoplayer2.upstream.ParsingLoadable.Parser;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.UriUtil;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.util.XmlPullParserUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

public class DashManifestParser extends DefaultHandler implements Parser<DashManifest> {
    private static final Pattern CEA_608_ACCESSIBILITY_PATTERN = Pattern.compile("CC([1-4])=.*");
    private static final Pattern CEA_708_ACCESSIBILITY_PATTERN = Pattern.compile("([1-9]|[1-5][0-9]|6[0-3])=.*");
    private static final Pattern FRAME_RATE_PATTERN = Pattern.compile("(\\d+)(?:/(\\d+))?");
    private static final String TAG = "MpdParser";
    private final String contentId;
    private final XmlPullParserFactory xmlParserFactory;

    protected static final class RepresentationInfo {
        public final String baseUrl;
        public final ArrayList<SchemeData> drmSchemeDatas;
        public final String drmSchemeType;
        public final Format format;
        public final ArrayList<Descriptor> inbandEventStreams;
        public final long revisionId;
        public final SegmentBase segmentBase;

        public RepresentationInfo(Format format, String baseUrl, SegmentBase segmentBase, String drmSchemeType, ArrayList<SchemeData> drmSchemeDatas, ArrayList<Descriptor> inbandEventStreams, long revisionId) {
            this.format = format;
            this.baseUrl = baseUrl;
            this.segmentBase = segmentBase;
            this.drmSchemeType = drmSchemeType;
            this.drmSchemeDatas = drmSchemeDatas;
            this.inbandEventStreams = inbandEventStreams;
            this.revisionId = revisionId;
        }
    }

    public DashManifestParser() {
        this(null);
    }

    public DashManifestParser(String contentId) {
        this.contentId = contentId;
        try {
            this.xmlParserFactory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            throw new RuntimeException("Couldn't create XmlPullParserFactory instance", e);
        }
    }

    public DashManifest parse(Uri uri, InputStream inputStream) throws IOException {
        try {
            XmlPullParser xpp = this.xmlParserFactory.newPullParser();
            xpp.setInput(inputStream, null);
            if (xpp.next() == 2 && "MPD".equals(xpp.getName())) {
                return parseMediaPresentationDescription(xpp, uri.toString());
            }
            throw new ParserException("inputStream does not contain a valid media presentation description");
        } catch (Throwable e) {
            throw new ParserException(e);
        }
    }

    protected DashManifest parseMediaPresentationDescription(XmlPullParser xpp, String baseUrl) throws XmlPullParserException, IOException {
        long durationMs;
        XmlPullParser xmlPullParser = xpp;
        long availabilityStartTime = parseDateTime(xmlPullParser, "availabilityStartTime", C0555C.TIME_UNSET);
        long durationMs2 = parseDuration(xmlPullParser, "mediaPresentationDuration", C0555C.TIME_UNSET);
        long minBufferTimeMs = parseDuration(xmlPullParser, "minBufferTime", C0555C.TIME_UNSET);
        String typeString = xmlPullParser.getAttributeValue(null, "type");
        boolean z = typeString != null && "dynamic".equals(typeString);
        boolean dynamic = z;
        long minUpdateTimeMs = dynamic ? parseDuration(xmlPullParser, "minimumUpdatePeriod", C0555C.TIME_UNSET) : C0555C.TIME_UNSET;
        long timeShiftBufferDepthMs = dynamic ? parseDuration(xmlPullParser, "timeShiftBufferDepth", C0555C.TIME_UNSET) : C0555C.TIME_UNSET;
        long suggestedPresentationDelayMs = dynamic ? parseDuration(xmlPullParser, "suggestedPresentationDelay", C0555C.TIME_UNSET) : C0555C.TIME_UNSET;
        long publishTimeMs = parseDateTime(xmlPullParser, "publishTime", C0555C.TIME_UNSET);
        ProgramInformation programInformation = null;
        List<Period> periods = new ArrayList();
        boolean seenEarlyAccessPeriod = false;
        boolean seenFirstBaseUrl = false;
        long nextPeriodStartMs = dynamic ? C0555C.TIME_UNSET : 0;
        Uri location = null;
        UtcTimingElement utcTiming = null;
        String baseUrl2 = baseUrl;
        while (true) {
            String typeString2;
            long nextPeriodStartMs2;
            ProgramInformation programInformation2;
            String baseUrl3;
            UtcTimingElement utcTiming2;
            Uri location2;
            boolean seenEarlyAccessPeriod2;
            boolean seenFirstBaseUrl2;
            long nextPeriodStartMs3;
            ProgramInformation programInformation3;
            xpp.next();
            DashManifestParser dashManifestParser;
            if (!XmlPullParserUtil.isStartTag(xmlPullParser, "BaseURL")) {
                if (XmlPullParserUtil.isStartTag(xmlPullParser, "ProgramInformation")) {
                    dashManifestParser = this;
                    typeString2 = typeString;
                    nextPeriodStartMs2 = nextPeriodStartMs;
                    programInformation2 = parseProgramInformation(xpp);
                    baseUrl3 = baseUrl2;
                    utcTiming2 = utcTiming;
                    location2 = location;
                    seenEarlyAccessPeriod2 = seenEarlyAccessPeriod;
                    seenFirstBaseUrl2 = seenFirstBaseUrl;
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "UTCTiming")) {
                    dashManifestParser = this;
                    typeString2 = typeString;
                    nextPeriodStartMs2 = nextPeriodStartMs;
                    programInformation2 = programInformation;
                    baseUrl3 = baseUrl2;
                    utcTiming2 = parseUtcTiming(xpp);
                    location2 = location;
                    seenEarlyAccessPeriod2 = seenEarlyAccessPeriod;
                    seenFirstBaseUrl2 = seenFirstBaseUrl;
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "Location")) {
                    dashManifestParser = this;
                    typeString2 = typeString;
                    nextPeriodStartMs2 = nextPeriodStartMs;
                    programInformation2 = programInformation;
                    baseUrl3 = baseUrl2;
                    utcTiming2 = utcTiming;
                    location2 = Uri.parse(xpp.nextText());
                    seenEarlyAccessPeriod2 = seenEarlyAccessPeriod;
                    seenFirstBaseUrl2 = seenFirstBaseUrl;
                } else if (!XmlPullParserUtil.isStartTag(xmlPullParser, "Period") || seenEarlyAccessPeriod) {
                    dashManifestParser = this;
                    typeString2 = typeString;
                    nextPeriodStartMs3 = nextPeriodStartMs;
                    programInformation3 = programInformation;
                    baseUrl3 = baseUrl2;
                    utcTiming2 = utcTiming;
                    location2 = location;
                    maybeSkipTag(xpp);
                } else {
                    typeString2 = typeString;
                    Pair<Period, Long> periodWithDurationMs = parsePeriod(xmlPullParser, baseUrl2, nextPeriodStartMs);
                    nextPeriodStartMs3 = nextPeriodStartMs;
                    Period period = periodWithDurationMs.first;
                    programInformation3 = programInformation;
                    String baseUrl4 = baseUrl2;
                    if (period.startMs != C0555C.TIME_UNSET) {
                        baseUrl3 = baseUrl4;
                        programInformation = ((Long) periodWithDurationMs.second).longValue();
                        if (programInformation == C0555C.TIME_UNSET) {
                            utcTiming2 = utcTiming;
                            location2 = location;
                            utcTiming = 1;
                        } else {
                            utcTiming2 = utcTiming;
                            location2 = location;
                            utcTiming = period.startMs + programInformation;
                        }
                        periods.add(period);
                        nextPeriodStartMs3 = utcTiming;
                    } else if (dynamic) {
                        seenEarlyAccessPeriod = true;
                        baseUrl3 = baseUrl4;
                        utcTiming2 = utcTiming;
                        location2 = location;
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Unable to determine start of period ");
                        stringBuilder.append(periods.size());
                        throw new ParserException(stringBuilder.toString());
                    }
                    programInformation2 = programInformation3;
                    seenEarlyAccessPeriod2 = seenEarlyAccessPeriod;
                    seenFirstBaseUrl2 = seenFirstBaseUrl;
                    nextPeriodStartMs2 = nextPeriodStartMs3;
                }
                if (!XmlPullParserUtil.isEndTag(xmlPullParser, "MPD")) {
                    break;
                }
                programInformation = programInformation2;
                seenFirstBaseUrl = seenFirstBaseUrl2;
                utcTiming = utcTiming2;
                typeString = typeString2;
                baseUrl2 = baseUrl3;
                location = location2;
                nextPeriodStartMs = nextPeriodStartMs2;
                seenEarlyAccessPeriod = seenEarlyAccessPeriod2;
            } else if (seenFirstBaseUrl) {
                dashManifestParser = this;
                typeString2 = typeString;
                nextPeriodStartMs3 = nextPeriodStartMs;
                programInformation3 = programInformation;
                baseUrl3 = baseUrl2;
                utcTiming2 = utcTiming;
                location2 = location;
            } else {
                typeString2 = typeString;
                nextPeriodStartMs2 = nextPeriodStartMs;
                programInformation2 = programInformation;
                baseUrl3 = parseBaseUrl(xmlPullParser, baseUrl2);
                utcTiming2 = utcTiming;
                location2 = location;
                seenFirstBaseUrl2 = true;
                seenEarlyAccessPeriod2 = seenEarlyAccessPeriod;
                dashManifestParser = this;
                if (!XmlPullParserUtil.isEndTag(xmlPullParser, "MPD")) {
                    break;
                }
                programInformation = programInformation2;
                seenFirstBaseUrl = seenFirstBaseUrl2;
                utcTiming = utcTiming2;
                typeString = typeString2;
                baseUrl2 = baseUrl3;
                location = location2;
                nextPeriodStartMs = nextPeriodStartMs2;
                seenEarlyAccessPeriod = seenEarlyAccessPeriod2;
            }
            programInformation2 = programInformation3;
            seenEarlyAccessPeriod2 = seenEarlyAccessPeriod;
            seenFirstBaseUrl2 = seenFirstBaseUrl;
            nextPeriodStartMs2 = nextPeriodStartMs3;
            if (!XmlPullParserUtil.isEndTag(xmlPullParser, "MPD")) {
                break;
            }
            programInformation = programInformation2;
            seenFirstBaseUrl = seenFirstBaseUrl2;
            utcTiming = utcTiming2;
            typeString = typeString2;
            baseUrl2 = baseUrl3;
            location = location2;
            nextPeriodStartMs = nextPeriodStartMs2;
            seenEarlyAccessPeriod = seenEarlyAccessPeriod2;
        }
        if (durationMs2 == C0555C.TIME_UNSET) {
            if (nextPeriodStartMs2 != C0555C.TIME_UNSET) {
                durationMs = nextPeriodStartMs2;
                if (periods.isEmpty()) {
                    return buildMediaPresentationDescription(availabilityStartTime, durationMs, minBufferTimeMs, dynamic, minUpdateTimeMs, timeShiftBufferDepthMs, suggestedPresentationDelayMs, publishTimeMs, programInformation2, utcTiming2, location2, periods);
                }
                throw new ParserException("No periods found.");
            } else if (!dynamic) {
                throw new ParserException("Unable to determine duration of static manifest.");
            }
        }
        durationMs = durationMs2;
        if (periods.isEmpty()) {
            throw new ParserException("No periods found.");
        }
        return buildMediaPresentationDescription(availabilityStartTime, durationMs, minBufferTimeMs, dynamic, minUpdateTimeMs, timeShiftBufferDepthMs, suggestedPresentationDelayMs, publishTimeMs, programInformation2, utcTiming2, location2, periods);
    }

    protected DashManifest buildMediaPresentationDescription(long availabilityStartTime, long durationMs, long minBufferTimeMs, boolean dynamic, long minUpdateTimeMs, long timeShiftBufferDepthMs, long suggestedPresentationDelayMs, long publishTimeMs, ProgramInformation programInformation, UtcTimingElement utcTiming, Uri location, List<Period> periods) {
        return new DashManifest(availabilityStartTime, durationMs, minBufferTimeMs, dynamic, minUpdateTimeMs, timeShiftBufferDepthMs, suggestedPresentationDelayMs, publishTimeMs, programInformation, utcTiming, location, periods);
    }

    protected UtcTimingElement parseUtcTiming(XmlPullParser xpp) {
        return buildUtcTimingElement(xpp.getAttributeValue(null, "schemeIdUri"), xpp.getAttributeValue(null, "value"));
    }

    protected UtcTimingElement buildUtcTimingElement(String schemeIdUri, String value) {
        return new UtcTimingElement(schemeIdUri, value);
    }

    protected Pair<Period, Long> parsePeriod(XmlPullParser xpp, String baseUrl, long defaultStartMs) throws XmlPullParserException, IOException {
        DashManifestParser dashManifestParser = this;
        XmlPullParser xmlPullParser = xpp;
        String id = xmlPullParser.getAttributeValue(null, "id");
        long startMs = parseDuration(xmlPullParser, "start", defaultStartMs);
        long durationMs = parseDuration(xmlPullParser, "duration", C0555C.TIME_UNSET);
        List adaptationSets = new ArrayList();
        List<EventStream> eventStreams = new ArrayList();
        boolean seenFirstBaseUrl = false;
        SegmentBase segmentBase = null;
        String baseUrl2 = baseUrl;
        while (true) {
            String baseUrl3;
            boolean seenFirstBaseUrl2;
            SegmentBase segmentBase2;
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "BaseURL")) {
                if (!seenFirstBaseUrl) {
                    baseUrl3 = parseBaseUrl(xmlPullParser, baseUrl2);
                    seenFirstBaseUrl2 = true;
                    segmentBase2 = segmentBase;
                    if (XmlPullParserUtil.isEndTag(xmlPullParser, "Period")) {
                        return Pair.create(buildPeriod(id, startMs, adaptationSets, eventStreams), Long.valueOf(durationMs));
                    }
                    baseUrl2 = baseUrl3;
                    segmentBase = segmentBase2;
                    seenFirstBaseUrl = seenFirstBaseUrl2;
                }
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "AdaptationSet")) {
                adaptationSets.add(parseAdaptationSet(xmlPullParser, baseUrl2, segmentBase));
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "EventStream")) {
                eventStreams.add(parseEventStream(xpp));
            } else {
                if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentBase")) {
                    baseUrl3 = baseUrl2;
                    seenFirstBaseUrl2 = seenFirstBaseUrl;
                    segmentBase2 = parseSegmentBase(xmlPullParser, null);
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentList")) {
                    baseUrl3 = baseUrl2;
                    seenFirstBaseUrl2 = seenFirstBaseUrl;
                    segmentBase2 = parseSegmentList(xmlPullParser, null);
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentTemplate")) {
                    baseUrl3 = baseUrl2;
                    seenFirstBaseUrl2 = seenFirstBaseUrl;
                    segmentBase2 = parseSegmentTemplate(xmlPullParser, null);
                } else {
                    maybeSkipTag(xpp);
                }
                if (XmlPullParserUtil.isEndTag(xmlPullParser, "Period")) {
                    baseUrl2 = baseUrl3;
                    segmentBase = segmentBase2;
                    seenFirstBaseUrl = seenFirstBaseUrl2;
                } else {
                    return Pair.create(buildPeriod(id, startMs, adaptationSets, eventStreams), Long.valueOf(durationMs));
                }
            }
            baseUrl3 = baseUrl2;
            seenFirstBaseUrl2 = seenFirstBaseUrl;
            segmentBase2 = segmentBase;
            if (XmlPullParserUtil.isEndTag(xmlPullParser, "Period")) {
                return Pair.create(buildPeriod(id, startMs, adaptationSets, eventStreams), Long.valueOf(durationMs));
            }
            baseUrl2 = baseUrl3;
            segmentBase = segmentBase2;
            seenFirstBaseUrl = seenFirstBaseUrl2;
        }
    }

    protected Period buildPeriod(String id, long startMs, List<AdaptationSet> adaptationSets, List<EventStream> eventStreams) {
        return new Period(id, startMs, adaptationSets, eventStreams);
    }

    protected AdaptationSet parseAdaptationSet(XmlPullParser xpp, String baseUrl, SegmentBase segmentBase) throws XmlPullParserException, IOException {
        List<RepresentationInfo> representationInfos;
        ArrayList<Descriptor> supplementalProperties;
        ArrayList<Descriptor> accessibilityDescriptors;
        int contentType;
        DashManifestParser dashManifestParser = this;
        XmlPullParser xmlPullParser = xpp;
        int id = parseInt(xmlPullParser, "id", -1);
        int contentType2 = parseContentType(xpp);
        String str = null;
        String mimeType = xmlPullParser.getAttributeValue(null, "mimeType");
        String codecs = xmlPullParser.getAttributeValue(null, "codecs");
        int width = parseInt(xmlPullParser, "width", -1);
        int height = parseInt(xmlPullParser, "height", -1);
        float frameRate = parseFrameRate(xmlPullParser, -1.0f);
        int audioSamplingRate = parseInt(xmlPullParser, "audioSamplingRate", -1);
        String language = xmlPullParser.getAttributeValue(null, "lang");
        String label = xmlPullParser.getAttributeValue(null, "label");
        ArrayList<SchemeData> drmSchemeDatas = new ArrayList();
        ArrayList<Descriptor> inbandEventStreams = new ArrayList();
        ArrayList<Descriptor> accessibilityDescriptors2 = new ArrayList();
        ArrayList<Descriptor> supplementalProperties2 = new ArrayList();
        List<RepresentationInfo> representationInfos2 = new ArrayList();
        String baseUrl2 = baseUrl;
        SegmentBase segmentBase2 = segmentBase;
        String language2 = language;
        int audioChannels = -1;
        String drmSchemeType = null;
        int selectionFlags = 0;
        boolean seenFirstBaseUrl = false;
        int contentType3 = contentType2;
        while (true) {
            String language3;
            String baseUrl3;
            ArrayList<Descriptor> inbandEventStreams2;
            ArrayList<SchemeData> drmSchemeDatas2;
            String str2;
            XmlPullParser xmlPullParser2;
            int contentType4;
            xpp.next();
            if (!XmlPullParserUtil.isStartTag(xmlPullParser, "BaseURL")) {
                if (XmlPullParserUtil.isStartTag(xmlPullParser, "ContentProtection")) {
                    Pair<String, SchemeData> contentProtection = parseContentProtection(xpp);
                    if (contentProtection.first != null) {
                        drmSchemeType = contentProtection.first;
                    }
                    if (contentProtection.second != null) {
                        drmSchemeDatas.add(contentProtection.second);
                    }
                    language3 = language2;
                    baseUrl3 = baseUrl2;
                    representationInfos = representationInfos2;
                    supplementalProperties = supplementalProperties2;
                    accessibilityDescriptors = accessibilityDescriptors2;
                    inbandEventStreams2 = inbandEventStreams;
                    drmSchemeDatas2 = drmSchemeDatas;
                    str2 = str;
                    xmlPullParser2 = xmlPullParser;
                    contentType = contentType3;
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "ContentComponent")) {
                    language3 = checkLanguageConsistency(language2, xmlPullParser.getAttributeValue(str, "lang"));
                    baseUrl3 = baseUrl2;
                    representationInfos = representationInfos2;
                    supplementalProperties = supplementalProperties2;
                    accessibilityDescriptors = accessibilityDescriptors2;
                    inbandEventStreams2 = inbandEventStreams;
                    drmSchemeDatas2 = drmSchemeDatas;
                    str2 = str;
                    xmlPullParser2 = xmlPullParser;
                    contentType = checkContentTypeConsistency(contentType3, parseContentType(xpp));
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "Role")) {
                    selectionFlags |= parseRole(xpp);
                    language3 = language2;
                    baseUrl3 = baseUrl2;
                    representationInfos = representationInfos2;
                    supplementalProperties = supplementalProperties2;
                    accessibilityDescriptors = accessibilityDescriptors2;
                    inbandEventStreams2 = inbandEventStreams;
                    drmSchemeDatas2 = drmSchemeDatas;
                    str2 = str;
                    xmlPullParser2 = xmlPullParser;
                    contentType = contentType3;
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "AudioChannelConfiguration")) {
                    audioChannels = parseAudioChannelConfiguration(xpp);
                    language3 = language2;
                    baseUrl3 = baseUrl2;
                    representationInfos = representationInfos2;
                    supplementalProperties = supplementalProperties2;
                    accessibilityDescriptors = accessibilityDescriptors2;
                    inbandEventStreams2 = inbandEventStreams;
                    drmSchemeDatas2 = drmSchemeDatas;
                    str2 = str;
                    xmlPullParser2 = xmlPullParser;
                    contentType = contentType3;
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "Accessibility")) {
                    accessibilityDescriptors2.add(parseDescriptor(xmlPullParser, "Accessibility"));
                    contentType4 = contentType3;
                    language3 = language2;
                    baseUrl3 = baseUrl2;
                    representationInfos = representationInfos2;
                    supplementalProperties = supplementalProperties2;
                    accessibilityDescriptors = accessibilityDescriptors2;
                    inbandEventStreams2 = inbandEventStreams;
                    drmSchemeDatas2 = drmSchemeDatas;
                    str2 = str;
                    xmlPullParser2 = xmlPullParser;
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SupplementalProperty")) {
                    supplementalProperties2.add(parseDescriptor(xmlPullParser, "SupplementalProperty"));
                    contentType4 = contentType3;
                    language3 = language2;
                    baseUrl3 = baseUrl2;
                    representationInfos = representationInfos2;
                    supplementalProperties = supplementalProperties2;
                    accessibilityDescriptors = accessibilityDescriptors2;
                    inbandEventStreams2 = inbandEventStreams;
                    drmSchemeDatas2 = drmSchemeDatas;
                    str2 = str;
                    xmlPullParser2 = xmlPullParser;
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "Representation")) {
                    int contentType5 = contentType3;
                    language3 = language2;
                    baseUrl3 = baseUrl2;
                    List<RepresentationInfo> representationInfos3 = representationInfos2;
                    supplementalProperties = supplementalProperties2;
                    accessibilityDescriptors = accessibilityDescriptors2;
                    inbandEventStreams = inbandEventStreams;
                    drmSchemeDatas2 = drmSchemeDatas;
                    str2 = str;
                    RepresentationInfo representationInfo = parseRepresentation(xpp, baseUrl2, label, mimeType, codecs, width, height, frameRate, audioChannels, audioSamplingRate, language3, selectionFlags, accessibilityDescriptors, segmentBase2);
                    int contentType6 = checkContentTypeConsistency(contentType5, getContentType(representationInfo.format));
                    representationInfos = representationInfos3;
                    representationInfos.add(representationInfo);
                    contentType = contentType6;
                    inbandEventStreams2 = inbandEventStreams;
                    xmlPullParser2 = xpp;
                } else {
                    contentType4 = contentType3;
                    language3 = language2;
                    baseUrl3 = baseUrl2;
                    representationInfos = representationInfos2;
                    supplementalProperties = supplementalProperties2;
                    accessibilityDescriptors = accessibilityDescriptors2;
                    inbandEventStreams = inbandEventStreams;
                    drmSchemeDatas2 = drmSchemeDatas;
                    str2 = str;
                    xmlPullParser2 = xpp;
                    if (XmlPullParserUtil.isStartTag(xmlPullParser2, "SegmentBase")) {
                        segmentBase2 = parseSegmentBase(xmlPullParser2, (SingleSegmentBase) segmentBase2);
                        contentType = contentType4;
                        inbandEventStreams2 = inbandEventStreams;
                    } else if (XmlPullParserUtil.isStartTag(xmlPullParser2, "SegmentList")) {
                        segmentBase2 = parseSegmentList(xmlPullParser2, (SegmentList) segmentBase2);
                        contentType = contentType4;
                        inbandEventStreams2 = inbandEventStreams;
                    } else if (XmlPullParserUtil.isStartTag(xmlPullParser2, "SegmentTemplate")) {
                        segmentBase2 = parseSegmentTemplate(xmlPullParser2, (SegmentTemplate) segmentBase2);
                        contentType = contentType4;
                        inbandEventStreams2 = inbandEventStreams;
                    } else if (XmlPullParserUtil.isStartTag(xmlPullParser2, "InbandEventStream")) {
                        inbandEventStreams2 = inbandEventStreams;
                        inbandEventStreams2.add(parseDescriptor(xmlPullParser2, "InbandEventStream"));
                    } else {
                        inbandEventStreams2 = inbandEventStreams;
                        if (XmlPullParserUtil.isStartTag(xpp)) {
                            parseAdaptationSetChild(xpp);
                        }
                    }
                }
                if (!XmlPullParserUtil.isEndTag(xmlPullParser2, "AdaptationSet")) {
                    break;
                }
                xmlPullParser = xmlPullParser2;
                inbandEventStreams = inbandEventStreams2;
                contentType3 = contentType;
                baseUrl2 = baseUrl3;
                supplementalProperties2 = supplementalProperties;
                accessibilityDescriptors2 = accessibilityDescriptors;
                drmSchemeDatas = drmSchemeDatas2;
                str = str2;
                representationInfos2 = representationInfos;
                language2 = language3;
            } else if (seenFirstBaseUrl) {
                contentType4 = contentType3;
                language3 = language2;
                baseUrl3 = baseUrl2;
                representationInfos = representationInfos2;
                supplementalProperties = supplementalProperties2;
                accessibilityDescriptors = accessibilityDescriptors2;
                inbandEventStreams2 = inbandEventStreams;
                drmSchemeDatas2 = drmSchemeDatas;
                str2 = str;
                xmlPullParser2 = xmlPullParser;
            } else {
                baseUrl3 = parseBaseUrl(xmlPullParser, baseUrl2);
                seenFirstBaseUrl = true;
                language3 = language2;
                representationInfos = representationInfos2;
                supplementalProperties = supplementalProperties2;
                accessibilityDescriptors = accessibilityDescriptors2;
                inbandEventStreams2 = inbandEventStreams;
                drmSchemeDatas2 = drmSchemeDatas;
                str2 = str;
                xmlPullParser2 = xmlPullParser;
                contentType = contentType3;
                if (!XmlPullParserUtil.isEndTag(xmlPullParser2, "AdaptationSet")) {
                    break;
                }
                xmlPullParser = xmlPullParser2;
                inbandEventStreams = inbandEventStreams2;
                contentType3 = contentType;
                baseUrl2 = baseUrl3;
                supplementalProperties2 = supplementalProperties;
                accessibilityDescriptors2 = accessibilityDescriptors;
                drmSchemeDatas = drmSchemeDatas2;
                str = str2;
                representationInfos2 = representationInfos;
                language2 = language3;
            }
            contentType = contentType4;
            if (!XmlPullParserUtil.isEndTag(xmlPullParser2, "AdaptationSet")) {
                break;
            }
            xmlPullParser = xmlPullParser2;
            inbandEventStreams = inbandEventStreams2;
            contentType3 = contentType;
            baseUrl2 = baseUrl3;
            supplementalProperties2 = supplementalProperties;
            accessibilityDescriptors2 = accessibilityDescriptors;
            drmSchemeDatas = drmSchemeDatas2;
            str = str2;
            representationInfos2 = representationInfos;
            language2 = language3;
        }
        List representations = new ArrayList(representationInfos.size());
        for (int i = 0; i < representationInfos.size(); i++) {
            representations.add(buildRepresentation((RepresentationInfo) representationInfos.get(i), dashManifestParser.contentId, drmSchemeType, drmSchemeDatas2, inbandEventStreams2));
        }
        return buildAdaptationSet(id, contentType, representations, accessibilityDescriptors, supplementalProperties);
    }

    protected AdaptationSet buildAdaptationSet(int id, int contentType, List<Representation> representations, List<Descriptor> accessibilityDescriptors, List<Descriptor> supplementalProperties) {
        return new AdaptationSet(id, contentType, representations, accessibilityDescriptors, supplementalProperties);
    }

    protected int parseContentType(XmlPullParser xpp) {
        String contentType = xpp.getAttributeValue(null, "contentType");
        if (TextUtils.isEmpty(contentType)) {
            return -1;
        }
        if (MimeTypes.BASE_TYPE_AUDIO.equals(contentType)) {
            return 1;
        }
        if (MimeTypes.BASE_TYPE_VIDEO.equals(contentType)) {
            return 2;
        }
        if ("text".equals(contentType)) {
            return 3;
        }
        return -1;
    }

    protected int getContentType(Format format) {
        String sampleMimeType = format.sampleMimeType;
        if (TextUtils.isEmpty(sampleMimeType)) {
            return -1;
        }
        if (MimeTypes.isVideo(sampleMimeType)) {
            return 2;
        }
        if (MimeTypes.isAudio(sampleMimeType)) {
            return 1;
        }
        if (mimeTypeIsRawText(sampleMimeType)) {
            return 3;
        }
        return -1;
    }

    protected Pair<String, SchemeData> parseContentProtection(XmlPullParser xpp) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser = xpp;
        String schemeType = null;
        String licenseServerUrl = null;
        byte[] data = null;
        UUID uuid = null;
        boolean requiresSecureDecoder = false;
        SchemeData schemeData = null;
        String schemeIdUri = xmlPullParser.getAttributeValue(null, "schemeIdUri");
        String toLowerInvariant;
        if (schemeIdUri != null) {
            Object obj;
            String[] defaultKidStrings;
            UUID[] defaultKids;
            int i;
            toLowerInvariant = Util.toLowerInvariant(schemeIdUri);
            int hashCode = toLowerInvariant.hashCode();
            if (hashCode == 489446379) {
                if (toLowerInvariant.equals("urn:uuid:9a04f079-9840-4286-ab92-e65be0885f95")) {
                    obj = 1;
                    switch (obj) {
                        case null:
                            schemeType = xmlPullParser.getAttributeValue(null, "value");
                            toLowerInvariant = XmlPullParserUtil.getAttributeValueIgnorePrefix(xmlPullParser, "default_KID");
                            if (TextUtils.isEmpty(toLowerInvariant)) {
                                if ("00000000-0000-0000-0000-000000000000".equals(toLowerInvariant)) {
                                    break;
                                }
                                defaultKidStrings = toLowerInvariant.split("\\s+");
                                defaultKids = new UUID[defaultKidStrings.length];
                                for (i = 0; i < defaultKidStrings.length; i++) {
                                    defaultKids[i] = UUID.fromString(defaultKidStrings[i]);
                                }
                                data = PsshAtomUtil.buildPsshAtom(C0555C.COMMON_PSSH_UUID, defaultKids, null);
                                uuid = C0555C.COMMON_PSSH_UUID;
                                break;
                            }
                            break;
                        case 1:
                            uuid = C0555C.PLAYREADY_UUID;
                            break;
                        case 2:
                            uuid = C0555C.WIDEVINE_UUID;
                            break;
                        default:
                            break;
                    }
                }
            } else if (hashCode == 755418770) {
                if (toLowerInvariant.equals("urn:uuid:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed")) {
                    obj = 2;
                    switch (obj) {
                        case null:
                            schemeType = xmlPullParser.getAttributeValue(null, "value");
                            toLowerInvariant = XmlPullParserUtil.getAttributeValueIgnorePrefix(xmlPullParser, "default_KID");
                            if (TextUtils.isEmpty(toLowerInvariant)) {
                                if ("00000000-0000-0000-0000-000000000000".equals(toLowerInvariant)) {
                                    defaultKidStrings = toLowerInvariant.split("\\s+");
                                    defaultKids = new UUID[defaultKidStrings.length];
                                    for (i = 0; i < defaultKidStrings.length; i++) {
                                        defaultKids[i] = UUID.fromString(defaultKidStrings[i]);
                                    }
                                    data = PsshAtomUtil.buildPsshAtom(C0555C.COMMON_PSSH_UUID, defaultKids, null);
                                    uuid = C0555C.COMMON_PSSH_UUID;
                                    break;
                                }
                                break;
                            }
                            break;
                        case 1:
                            uuid = C0555C.PLAYREADY_UUID;
                            break;
                        case 2:
                            uuid = C0555C.WIDEVINE_UUID;
                            break;
                        default:
                            break;
                    }
                }
            } else if (hashCode == 1812765994 && toLowerInvariant.equals("urn:mpeg:dash:mp4protection:2011")) {
                obj = null;
                switch (obj) {
                    case null:
                        schemeType = xmlPullParser.getAttributeValue(null, "value");
                        toLowerInvariant = XmlPullParserUtil.getAttributeValueIgnorePrefix(xmlPullParser, "default_KID");
                        if (TextUtils.isEmpty(toLowerInvariant)) {
                            if ("00000000-0000-0000-0000-000000000000".equals(toLowerInvariant)) {
                                break;
                            }
                            defaultKidStrings = toLowerInvariant.split("\\s+");
                            defaultKids = new UUID[defaultKidStrings.length];
                            for (i = 0; i < defaultKidStrings.length; i++) {
                                defaultKids[i] = UUID.fromString(defaultKidStrings[i]);
                            }
                            data = PsshAtomUtil.buildPsshAtom(C0555C.COMMON_PSSH_UUID, defaultKids, null);
                            uuid = C0555C.COMMON_PSSH_UUID;
                            break;
                        }
                        break;
                    case 1:
                        uuid = C0555C.PLAYREADY_UUID;
                        break;
                    case 2:
                        uuid = C0555C.WIDEVINE_UUID;
                        break;
                    default:
                        break;
                }
            }
            obj = -1;
            switch (obj) {
                case null:
                    schemeType = xmlPullParser.getAttributeValue(null, "value");
                    toLowerInvariant = XmlPullParserUtil.getAttributeValueIgnorePrefix(xmlPullParser, "default_KID");
                    if (TextUtils.isEmpty(toLowerInvariant)) {
                        if ("00000000-0000-0000-0000-000000000000".equals(toLowerInvariant)) {
                            defaultKidStrings = toLowerInvariant.split("\\s+");
                            defaultKids = new UUID[defaultKidStrings.length];
                            for (i = 0; i < defaultKidStrings.length; i++) {
                                defaultKids[i] = UUID.fromString(defaultKidStrings[i]);
                            }
                            data = PsshAtomUtil.buildPsshAtom(C0555C.COMMON_PSSH_UUID, defaultKids, null);
                            uuid = C0555C.COMMON_PSSH_UUID;
                            break;
                        }
                        break;
                    }
                    break;
                case 1:
                    uuid = C0555C.PLAYREADY_UUID;
                    break;
                case 2:
                    uuid = C0555C.WIDEVINE_UUID;
                    break;
                default:
                    break;
            }
        }
        while (true) {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "ms:laurl")) {
                licenseServerUrl = xmlPullParser.getAttributeValue(null, "licenseUrl");
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "widevine:license")) {
                toLowerInvariant = xmlPullParser.getAttributeValue(null, "robustness_level");
                boolean z = toLowerInvariant != null && toLowerInvariant.startsWith("HW");
                requiresSecureDecoder = z;
            } else {
                if (data == null) {
                    if (XmlPullParserUtil.isStartTagIgnorePrefix(xmlPullParser, "pssh")) {
                        if (xpp.next() == 4) {
                            data = Base64.decode(xpp.getText(), 0);
                            uuid = PsshAtomUtil.parseUuid(data);
                            if (uuid == null) {
                                Log.m10w(TAG, "Skipping malformed cenc:pssh data");
                                data = null;
                            }
                        }
                    }
                }
                if (data == null) {
                    if (C0555C.PLAYREADY_UUID.equals(uuid)) {
                        if (XmlPullParserUtil.isStartTag(xmlPullParser, "mspr:pro")) {
                            if (xpp.next() == 4) {
                                data = PsshAtomUtil.buildPsshAtom(C0555C.PLAYREADY_UUID, Base64.decode(xpp.getText(), 0));
                            }
                        }
                    }
                }
                maybeSkipTag(xpp);
            }
            if (XmlPullParserUtil.isEndTag(xmlPullParser, "ContentProtection")) {
                if (uuid != null) {
                    SchemeData schemeData2 = new SchemeData(uuid, licenseServerUrl, MimeTypes.VIDEO_MP4, data, requiresSecureDecoder);
                }
                return Pair.create(schemeType, schemeData);
            }
        }
    }

    protected int parseRole(XmlPullParser xpp) throws XmlPullParserException, IOException {
        String schemeIdUri = parseString(xpp, "schemeIdUri", null);
        String value = parseString(xpp, "value", null);
        while (true) {
            xpp.next();
            if (XmlPullParserUtil.isEndTag(xpp, "Role")) {
                break;
            }
        }
        return ("urn:mpeg:dash:role:2011".equals(schemeIdUri) && "main".equals(value)) ? 1 : 0;
    }

    protected void parseAdaptationSetChild(XmlPullParser xpp) throws XmlPullParserException, IOException {
        maybeSkipTag(xpp);
    }

    protected RepresentationInfo parseRepresentation(XmlPullParser xpp, String baseUrl, String label, String adaptationSetMimeType, String adaptationSetCodecs, int adaptationSetWidth, int adaptationSetHeight, float adaptationSetFrameRate, int adaptationSetAudioChannels, int adaptationSetAudioSamplingRate, String adaptationSetLanguage, int adaptationSetSelectionFlags, List<Descriptor> adaptationSetAccessibilityDescriptors, SegmentBase segmentBase) throws XmlPullParserException, IOException {
        int audioChannels;
        String baseUrl2;
        SegmentBase segmentBase2;
        String drmSchemeType;
        DashManifestParser dashManifestParser = this;
        XmlPullParser xmlPullParser = xpp;
        String id = xmlPullParser.getAttributeValue(null, "id");
        int bandwidth = parseInt(xmlPullParser, "bandwidth", -1);
        String mimeType = parseString(xmlPullParser, "mimeType", adaptationSetMimeType);
        String codecs = parseString(xmlPullParser, "codecs", adaptationSetCodecs);
        int width = parseInt(xmlPullParser, "width", adaptationSetWidth);
        int height = parseInt(xmlPullParser, "height", adaptationSetHeight);
        float frameRate = parseFrameRate(xmlPullParser, adaptationSetFrameRate);
        int audioChannels2 = adaptationSetAudioChannels;
        int audioSamplingRate = parseInt(xmlPullParser, "audioSamplingRate", adaptationSetAudioSamplingRate);
        ArrayList<SchemeData> drmSchemeDatas = new ArrayList();
        ArrayList<Descriptor> inbandEventStreams = new ArrayList();
        ArrayList<Descriptor> supplementalProperties = new ArrayList();
        boolean seenFirstBaseUrl = false;
        SegmentBase segmentBase3 = segmentBase;
        String drmSchemeType2 = null;
        String baseUrl3 = baseUrl;
        while (true) {
            boolean seenFirstBaseUrl2;
            String baseUrl4;
            ArrayList<Descriptor> arrayList;
            ArrayList<SchemeData> arrayList2;
            String str;
            String str2;
            int i;
            int i2;
            float f;
            int i3;
            xpp.next();
            int audioChannels3 = audioChannels2;
            if (!XmlPullParserUtil.isStartTag(xmlPullParser, "BaseURL")) {
                if (XmlPullParserUtil.isStartTag(xmlPullParser, "AudioChannelConfiguration")) {
                    audioChannels = parseAudioChannelConfiguration(xpp);
                    baseUrl2 = baseUrl3;
                    seenFirstBaseUrl2 = seenFirstBaseUrl;
                    segmentBase2 = segmentBase3;
                    drmSchemeType = drmSchemeType2;
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentBase")) {
                    audioChannels = audioChannels3;
                    segmentBase2 = parseSegmentBase(xmlPullParser, (SingleSegmentBase) segmentBase3);
                    baseUrl2 = baseUrl3;
                    seenFirstBaseUrl2 = seenFirstBaseUrl;
                    drmSchemeType = drmSchemeType2;
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentList")) {
                    audioChannels = audioChannels3;
                    segmentBase2 = parseSegmentList(xmlPullParser, (SegmentList) segmentBase3);
                    baseUrl2 = baseUrl3;
                    seenFirstBaseUrl2 = seenFirstBaseUrl;
                    drmSchemeType = drmSchemeType2;
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentTemplate")) {
                    audioChannels = audioChannels3;
                    segmentBase2 = parseSegmentTemplate(xmlPullParser, (SegmentTemplate) segmentBase3);
                    baseUrl2 = baseUrl3;
                    seenFirstBaseUrl2 = seenFirstBaseUrl;
                    drmSchemeType = drmSchemeType2;
                } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "ContentProtection")) {
                    Pair<String, SchemeData> contentProtection = parseContentProtection(xpp);
                    baseUrl4 = baseUrl3;
                    if (contentProtection.first != null) {
                        drmSchemeType2 = contentProtection.first;
                    }
                    if (contentProtection.second != null) {
                        drmSchemeDatas.add(contentProtection.second);
                    }
                    audioChannels = audioChannels3;
                    baseUrl2 = baseUrl4;
                    seenFirstBaseUrl2 = seenFirstBaseUrl;
                    segmentBase2 = segmentBase3;
                    drmSchemeType = drmSchemeType2;
                } else {
                    baseUrl4 = baseUrl3;
                    if (XmlPullParserUtil.isStartTag(xmlPullParser, "InbandEventStream")) {
                        inbandEventStreams.add(parseDescriptor(xmlPullParser, "InbandEventStream"));
                    } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SupplementalProperty")) {
                        supplementalProperties.add(parseDescriptor(xmlPullParser, "SupplementalProperty"));
                    } else {
                        maybeSkipTag(xpp);
                    }
                }
                if (!XmlPullParserUtil.isEndTag(xmlPullParser, "Representation")) {
                    break;
                }
                arrayList = inbandEventStreams;
                arrayList2 = drmSchemeDatas;
                xmlPullParser = xpp;
                str = adaptationSetMimeType;
                str2 = adaptationSetCodecs;
                i = adaptationSetWidth;
                i2 = adaptationSetHeight;
                f = adaptationSetFrameRate;
                i3 = adaptationSetAudioSamplingRate;
                baseUrl3 = baseUrl2;
                audioChannels2 = audioChannels;
                seenFirstBaseUrl = seenFirstBaseUrl2;
                segmentBase3 = segmentBase2;
                drmSchemeType2 = drmSchemeType;
            } else if (seenFirstBaseUrl) {
                baseUrl4 = baseUrl3;
            } else {
                audioChannels = audioChannels3;
                baseUrl2 = parseBaseUrl(xmlPullParser, baseUrl3);
                seenFirstBaseUrl2 = true;
                segmentBase2 = segmentBase3;
                drmSchemeType = drmSchemeType2;
                if (!XmlPullParserUtil.isEndTag(xmlPullParser, "Representation")) {
                    break;
                }
                arrayList = inbandEventStreams;
                arrayList2 = drmSchemeDatas;
                xmlPullParser = xpp;
                str = adaptationSetMimeType;
                str2 = adaptationSetCodecs;
                i = adaptationSetWidth;
                i2 = adaptationSetHeight;
                f = adaptationSetFrameRate;
                i3 = adaptationSetAudioSamplingRate;
                baseUrl3 = baseUrl2;
                audioChannels2 = audioChannels;
                seenFirstBaseUrl = seenFirstBaseUrl2;
                segmentBase3 = segmentBase2;
                drmSchemeType2 = drmSchemeType;
            }
            audioChannels = audioChannels3;
            baseUrl2 = baseUrl4;
            seenFirstBaseUrl2 = seenFirstBaseUrl;
            segmentBase2 = segmentBase3;
            drmSchemeType = drmSchemeType2;
            if (!XmlPullParserUtil.isEndTag(xmlPullParser, "Representation")) {
                break;
            }
            arrayList = inbandEventStreams;
            arrayList2 = drmSchemeDatas;
            xmlPullParser = xpp;
            str = adaptationSetMimeType;
            str2 = adaptationSetCodecs;
            i = adaptationSetWidth;
            i2 = adaptationSetHeight;
            f = adaptationSetFrameRate;
            i3 = adaptationSetAudioSamplingRate;
            baseUrl3 = baseUrl2;
            audioChannels2 = audioChannels;
            seenFirstBaseUrl = seenFirstBaseUrl2;
            segmentBase3 = segmentBase2;
            drmSchemeType2 = drmSchemeType;
        }
        return new RepresentationInfo(buildFormat(id, label, mimeType, width, height, frameRate, audioChannels, audioSamplingRate, bandwidth, adaptationSetLanguage, adaptationSetSelectionFlags, adaptationSetAccessibilityDescriptors, codecs, supplementalProperties), baseUrl2, segmentBase2 != null ? segmentBase2 : new SingleSegmentBase(), drmSchemeType, drmSchemeDatas, inbandEventStreams, -1);
    }

    protected Format buildFormat(String id, String label, String containerMimeType, int width, int height, float frameRate, int audioChannels, int audioSamplingRate, int bitrate, String language, int selectionFlags, List<Descriptor> accessibilityDescriptors, String codecs, List<Descriptor> supplementalProperties) {
        String parseEac3SupplementalProperties;
        String sampleMimeType = getSampleMimeType(containerMimeType, codecs);
        if (sampleMimeType != null) {
            if (MimeTypes.AUDIO_E_AC3.equals(sampleMimeType)) {
                parseEac3SupplementalProperties = parseEac3SupplementalProperties(supplementalProperties);
            } else {
                parseEac3SupplementalProperties = sampleMimeType;
            }
            if (MimeTypes.isVideo(parseEac3SupplementalProperties)) {
                return Format.createVideoContainerFormat(id, label, containerMimeType, parseEac3SupplementalProperties, codecs, bitrate, width, height, frameRate, null, selectionFlags);
            }
            if (MimeTypes.isAudio(parseEac3SupplementalProperties)) {
                return Format.createAudioContainerFormat(id, label, containerMimeType, parseEac3SupplementalProperties, codecs, bitrate, audioChannels, audioSamplingRate, null, selectionFlags, language);
            }
            if (mimeTypeIsRawText(parseEac3SupplementalProperties)) {
                int accessibilityChannel;
                if (MimeTypes.APPLICATION_CEA608.equals(parseEac3SupplementalProperties)) {
                    accessibilityChannel = parseCea608AccessibilityChannel(accessibilityDescriptors);
                } else if (MimeTypes.APPLICATION_CEA708.equals(parseEac3SupplementalProperties)) {
                    accessibilityChannel = parseCea708AccessibilityChannel(accessibilityDescriptors);
                } else {
                    accessibilityChannel = -1;
                }
                return Format.createTextContainerFormat(id, label, containerMimeType, parseEac3SupplementalProperties, codecs, bitrate, selectionFlags, language, accessibilityChannel);
            }
        } else {
            parseEac3SupplementalProperties = sampleMimeType;
        }
        return Format.createContainerFormat(id, label, containerMimeType, parseEac3SupplementalProperties, codecs, bitrate, selectionFlags, language);
    }

    protected Representation buildRepresentation(RepresentationInfo representationInfo, String contentId, String extraDrmSchemeType, ArrayList<SchemeData> extraDrmSchemeDatas, ArrayList<Descriptor> extraInbandEventStreams) {
        RepresentationInfo representationInfo2 = representationInfo;
        Format format = representationInfo2.format;
        String drmSchemeType = representationInfo2.drmSchemeType != null ? representationInfo2.drmSchemeType : extraDrmSchemeType;
        List drmSchemeDatas = representationInfo2.drmSchemeDatas;
        drmSchemeDatas.addAll(extraDrmSchemeDatas);
        if (!drmSchemeDatas.isEmpty()) {
            filterRedundantIncompleteSchemeDatas(drmSchemeDatas);
            format = format.copyWithDrmInitData(new DrmInitData(drmSchemeType, drmSchemeDatas));
        }
        ArrayList<Descriptor> inbandEventStreams = representationInfo2.inbandEventStreams;
        inbandEventStreams.addAll(extraInbandEventStreams);
        return Representation.newInstance(contentId, representationInfo2.revisionId, format, representationInfo2.baseUrl, representationInfo2.segmentBase, inbandEventStreams);
    }

    protected SingleSegmentBase parseSegmentBase(XmlPullParser xpp, SingleSegmentBase parent) throws XmlPullParserException, IOException {
        long indexStart;
        long indexLength;
        XmlPullParser xmlPullParser = xpp;
        SingleSegmentBase singleSegmentBase = parent;
        long timescale = parseLong(xmlPullParser, "timescale", singleSegmentBase != null ? singleSegmentBase.timescale : 1);
        long indexLength2 = 0;
        long presentationTimeOffset = parseLong(xmlPullParser, "presentationTimeOffset", singleSegmentBase != null ? singleSegmentBase.presentationTimeOffset : 0);
        long indexStart2 = singleSegmentBase != null ? singleSegmentBase.indexStart : 0;
        if (singleSegmentBase != null) {
            indexLength2 = singleSegmentBase.indexLength;
        }
        String str = null;
        String indexRangeText = xmlPullParser.getAttributeValue(null, "indexRange");
        if (indexRangeText != null) {
            String[] indexRange = indexRangeText.split("-");
            indexStart2 = Long.parseLong(indexRange[0]);
            indexStart = indexStart2;
            indexLength = (Long.parseLong(indexRange[1]) - indexStart2) + 1;
        } else {
            indexLength = indexLength2;
            indexStart = indexStart2;
        }
        if (singleSegmentBase != null) {
            str = singleSegmentBase.initialization;
        }
        RangedUri initialization = str;
        while (true) {
            RangedUri initialization2;
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "Initialization")) {
                initialization2 = parseInitialization(xpp);
            } else {
                maybeSkipTag(xpp);
                initialization2 = initialization;
            }
            if (XmlPullParserUtil.isEndTag(xmlPullParser, "SegmentBase")) {
                return buildSingleSegmentBase(initialization2, timescale, presentationTimeOffset, indexStart, indexLength);
            }
            initialization = initialization2;
        }
    }

    protected SingleSegmentBase buildSingleSegmentBase(RangedUri initialization, long timescale, long presentationTimeOffset, long indexStart, long indexLength) {
        return new SingleSegmentBase(initialization, timescale, presentationTimeOffset, indexStart, indexLength);
    }

    protected SegmentList parseSegmentList(XmlPullParser xpp, SegmentList parent) throws XmlPullParserException, IOException {
        List<SegmentTimelineElement> timeline;
        List<RangedUri> segments;
        XmlPullParser xmlPullParser = xpp;
        SegmentList segmentList = parent;
        long j = 1;
        long timescale = parseLong(xmlPullParser, "timescale", segmentList != null ? segmentList.timescale : 1);
        long presentationTimeOffset = parseLong(xmlPullParser, "presentationTimeOffset", segmentList != null ? segmentList.presentationTimeOffset : 0);
        long duration = parseLong(xmlPullParser, "duration", segmentList != null ? segmentList.duration : C0555C.TIME_UNSET);
        long startNumber = "startNumber";
        if (segmentList != null) {
            j = segmentList.startNumber;
        }
        startNumber = parseLong(xmlPullParser, startNumber, j);
        RangedUri initialization = null;
        List<SegmentTimelineElement> timeline2 = null;
        List<RangedUri> segments2 = null;
        while (true) {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "Initialization")) {
                initialization = parseInitialization(xpp);
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentTimeline")) {
                timeline2 = parseSegmentTimeline(xpp);
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentURL")) {
                if (segments2 == null) {
                    segments2 = new ArrayList();
                }
                segments2.add(parseSegmentUrl(xpp));
            } else {
                maybeSkipTag(xpp);
            }
            if (XmlPullParserUtil.isEndTag(xmlPullParser, "SegmentList")) {
                break;
            }
        }
        if (segmentList != null) {
            initialization = initialization != null ? initialization : segmentList.initialization;
            timeline = timeline2 != null ? timeline2 : segmentList.segmentTimeline;
            segments = segments2 != null ? segments2 : segmentList.mediaSegments;
        } else {
            timeline = timeline2;
            segments = segments2;
        }
        return buildSegmentList(initialization, timescale, presentationTimeOffset, startNumber, duration, timeline, segments);
    }

    protected SegmentList buildSegmentList(RangedUri initialization, long timescale, long presentationTimeOffset, long startNumber, long duration, List<SegmentTimelineElement> timeline, List<RangedUri> segments) {
        return new SegmentList(initialization, timescale, presentationTimeOffset, startNumber, duration, timeline, segments);
    }

    protected SegmentTemplate parseSegmentTemplate(XmlPullParser xpp, SegmentTemplate parent) throws XmlPullParserException, IOException {
        RangedUri initialization;
        List<SegmentTimelineElement> timeline;
        DashManifestParser dashManifestParser = this;
        XmlPullParser xmlPullParser = xpp;
        SegmentTemplate segmentTemplate = parent;
        long j = 1;
        long timescale = parseLong(xmlPullParser, "timescale", segmentTemplate != null ? segmentTemplate.timescale : 1);
        long presentationTimeOffset = parseLong(xmlPullParser, "presentationTimeOffset", segmentTemplate != null ? segmentTemplate.presentationTimeOffset : 0);
        long duration = parseLong(xmlPullParser, "duration", segmentTemplate != null ? segmentTemplate.duration : C0555C.TIME_UNSET);
        String str = "startNumber";
        if (segmentTemplate != null) {
            j = segmentTemplate.startNumber;
        }
        long startNumber = parseLong(xmlPullParser, str, j);
        UrlTemplate urlTemplate = null;
        UrlTemplate mediaTemplate = parseUrlTemplate(xmlPullParser, "media", segmentTemplate != null ? segmentTemplate.mediaTemplate : null);
        str = "initialization";
        if (segmentTemplate != null) {
            urlTemplate = segmentTemplate.initializationTemplate;
        }
        UrlTemplate initializationTemplate = parseUrlTemplate(xmlPullParser, str, urlTemplate);
        RangedUri initialization2 = null;
        List<SegmentTimelineElement> timeline2 = null;
        while (true) {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "Initialization")) {
                initialization2 = parseInitialization(xpp);
            } else if (XmlPullParserUtil.isStartTag(xmlPullParser, "SegmentTimeline")) {
                timeline2 = parseSegmentTimeline(xpp);
            } else {
                maybeSkipTag(xpp);
            }
            if (XmlPullParserUtil.isEndTag(xmlPullParser, "SegmentTemplate")) {
                break;
            }
        }
        if (segmentTemplate != null) {
            initialization = initialization2 != null ? initialization2 : segmentTemplate.initialization;
            timeline = timeline2 != null ? timeline2 : segmentTemplate.segmentTimeline;
        } else {
            initialization = initialization2;
            timeline = timeline2;
        }
        return buildSegmentTemplate(initialization, timescale, presentationTimeOffset, startNumber, duration, timeline, initializationTemplate, mediaTemplate);
    }

    protected SegmentTemplate buildSegmentTemplate(RangedUri initialization, long timescale, long presentationTimeOffset, long startNumber, long duration, List<SegmentTimelineElement> timeline, UrlTemplate initializationTemplate, UrlTemplate mediaTemplate) {
        return new SegmentTemplate(initialization, timescale, presentationTimeOffset, startNumber, duration, timeline, initializationTemplate, mediaTemplate);
    }

    protected EventStream parseEventStream(XmlPullParser xpp) throws XmlPullParserException, IOException {
        XmlPullParser xmlPullParser = xpp;
        String schemeIdUri = parseString(xmlPullParser, "schemeIdUri", "");
        String value = parseString(xmlPullParser, "value", "");
        long timescale = parseLong(xmlPullParser, "timescale", 1);
        List<EventMessage> eventMessages = new ArrayList();
        ByteArrayOutputStream scratchOutputStream = new ByteArrayOutputStream(512);
        while (true) {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xmlPullParser, "Event")) {
                eventMessages.add(parseEvent(xpp, schemeIdUri, value, timescale, scratchOutputStream));
            } else {
                maybeSkipTag(xpp);
            }
            if (XmlPullParserUtil.isEndTag(xmlPullParser, "EventStream")) {
                break;
            }
        }
        long[] presentationTimesUs = new long[eventMessages.size()];
        EventMessage[] events = new EventMessage[eventMessages.size()];
        for (int i = 0; i < eventMessages.size(); i++) {
            EventMessage event = (EventMessage) eventMessages.get(i);
            presentationTimesUs[i] = event.presentationTimeUs;
            events[i] = event;
        }
        return buildEventStream(schemeIdUri, value, timescale, presentationTimesUs, events);
    }

    protected EventStream buildEventStream(String schemeIdUri, String value, long timescale, long[] presentationTimesUs, EventMessage[] events) {
        return new EventStream(schemeIdUri, value, timescale, presentationTimesUs, events);
    }

    protected EventMessage parseEvent(XmlPullParser xpp, String schemeIdUri, String value, long timescale, ByteArrayOutputStream scratchOutputStream) throws IOException, XmlPullParserException {
        byte[] bArr;
        XmlPullParser xmlPullParser = xpp;
        long id = parseLong(xmlPullParser, "id", 0);
        long duration = parseLong(xmlPullParser, "duration", C0555C.TIME_UNSET);
        long presentationTime = parseLong(xmlPullParser, "presentationTime", 0);
        long durationMs = Util.scaleLargeTimestamp(duration, 1000, timescale);
        long presentationTimesUs = Util.scaleLargeTimestamp(presentationTime, 1000000, timescale);
        String messageData = parseString(xmlPullParser, "messageData", null);
        byte[] eventObject = parseEventObject(xmlPullParser, scratchOutputStream);
        if (messageData == null) {
            bArr = eventObject;
        } else {
            bArr = Util.getUtf8Bytes(messageData);
        }
        return buildEvent(schemeIdUri, value, id, durationMs, bArr, presentationTimesUs);
    }

    protected byte[] parseEventObject(XmlPullParser xpp, ByteArrayOutputStream scratchOutputStream) throws XmlPullParserException, IOException {
        scratchOutputStream.reset();
        XmlSerializer xmlSerializer = Xml.newSerializer();
        xmlSerializer.setOutput(scratchOutputStream, "UTF-8");
        xpp.nextToken();
        while (!XmlPullParserUtil.isEndTag(xpp, "Event")) {
            switch (xpp.getEventType()) {
                case 0:
                    xmlSerializer.startDocument(null, Boolean.valueOf(false));
                    break;
                case 1:
                    xmlSerializer.endDocument();
                    break;
                case 2:
                    xmlSerializer.startTag(xpp.getNamespace(), xpp.getName());
                    for (int i = 0; i < xpp.getAttributeCount(); i++) {
                        xmlSerializer.attribute(xpp.getAttributeNamespace(i), xpp.getAttributeName(i), xpp.getAttributeValue(i));
                    }
                    break;
                case 3:
                    xmlSerializer.endTag(xpp.getNamespace(), xpp.getName());
                    break;
                case 4:
                    xmlSerializer.text(xpp.getText());
                    break;
                case 5:
                    xmlSerializer.cdsect(xpp.getText());
                    break;
                case 6:
                    xmlSerializer.entityRef(xpp.getText());
                    break;
                case 7:
                    xmlSerializer.ignorableWhitespace(xpp.getText());
                    break;
                case 8:
                    xmlSerializer.processingInstruction(xpp.getText());
                    break;
                case 9:
                    xmlSerializer.comment(xpp.getText());
                    break;
                case 10:
                    xmlSerializer.docdecl(xpp.getText());
                    break;
                default:
                    break;
            }
            xpp.nextToken();
        }
        xmlSerializer.flush();
        return scratchOutputStream.toByteArray();
    }

    protected EventMessage buildEvent(String schemeIdUri, String value, long id, long durationMs, byte[] messageData, long presentationTimeUs) {
        return new EventMessage(schemeIdUri, value, durationMs, id, messageData, presentationTimeUs);
    }

    protected List<SegmentTimelineElement> parseSegmentTimeline(XmlPullParser xpp) throws XmlPullParserException, IOException {
        List<SegmentTimelineElement> segmentTimeline = new ArrayList();
        long elapsedTime = 0;
        while (true) {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xpp, "S")) {
                elapsedTime = parseLong(xpp, "t", elapsedTime);
                long duration = parseLong(xpp, "d", C0555C.TIME_UNSET);
                int count = parseInt(xpp, "r", 0) + 1;
                for (int i = 0; i < count; i++) {
                    segmentTimeline.add(buildSegmentTimelineElement(elapsedTime, duration));
                    elapsedTime += duration;
                }
            } else {
                maybeSkipTag(xpp);
            }
            if (XmlPullParserUtil.isEndTag(xpp, "SegmentTimeline")) {
                return segmentTimeline;
            }
        }
    }

    protected SegmentTimelineElement buildSegmentTimelineElement(long elapsedTime, long duration) {
        return new SegmentTimelineElement(elapsedTime, duration);
    }

    protected UrlTemplate parseUrlTemplate(XmlPullParser xpp, String name, UrlTemplate defaultValue) {
        String valueString = xpp.getAttributeValue(null, name);
        if (valueString != null) {
            return UrlTemplate.compile(valueString);
        }
        return defaultValue;
    }

    protected RangedUri parseInitialization(XmlPullParser xpp) {
        return parseRangedUrl(xpp, "sourceURL", "range");
    }

    protected RangedUri parseSegmentUrl(XmlPullParser xpp) {
        return parseRangedUrl(xpp, "media", "mediaRange");
    }

    protected RangedUri parseRangedUrl(XmlPullParser xpp, String urlAttribute, String rangeAttribute) {
        long rangeLength;
        long rangeStart;
        String urlText = xpp.getAttributeValue(null, urlAttribute);
        String rangeText = xpp.getAttributeValue(null, rangeAttribute);
        if (rangeText != null) {
            String[] rangeTextArray = rangeText.split("-");
            long rangeStart2 = Long.parseLong(rangeTextArray[0]);
            if (rangeTextArray.length == 2) {
                rangeLength = (Long.parseLong(rangeTextArray[1]) - rangeStart2) + 1;
                rangeStart = rangeStart2;
            } else {
                rangeStart = rangeStart2;
                rangeLength = -1;
            }
        } else {
            rangeStart = 0;
            rangeLength = -1;
        }
        return buildRangedUri(urlText, rangeStart, rangeLength);
    }

    protected RangedUri buildRangedUri(String urlText, long rangeStart, long rangeLength) {
        return new RangedUri(urlText, rangeStart, rangeLength);
    }

    protected ProgramInformation parseProgramInformation(XmlPullParser xpp) throws IOException, XmlPullParserException {
        String title = null;
        String source = null;
        String copyright = null;
        String moreInformationURL = parseString(xpp, "moreInformationURL", null);
        String lang = parseString(xpp, "lang", null);
        while (true) {
            xpp.next();
            if (XmlPullParserUtil.isStartTag(xpp, "Title")) {
                title = xpp.nextText();
            } else if (XmlPullParserUtil.isStartTag(xpp, "Source")) {
                source = xpp.nextText();
            } else if (XmlPullParserUtil.isStartTag(xpp, "Copyright")) {
                copyright = xpp.nextText();
            } else {
                maybeSkipTag(xpp);
            }
            if (XmlPullParserUtil.isEndTag(xpp, "ProgramInformation")) {
                return new ProgramInformation(title, source, copyright, moreInformationURL, lang);
            }
        }
    }

    protected int parseAudioChannelConfiguration(XmlPullParser xpp) throws XmlPullParserException, IOException {
        String schemeIdUri = parseString(xpp, "schemeIdUri", null);
        int i = -1;
        if ("urn:mpeg:dash:23003:3:audio_channel_configuration:2011".equals(schemeIdUri)) {
            i = parseInt(xpp, "value", -1);
        } else if ("tag:dolby.com,2014:dash:audio_channel_configuration:2011".equals(schemeIdUri)) {
            i = parseDolbyChannelConfiguration(xpp);
        }
        int audioChannels = i;
        while (true) {
            xpp.next();
            if (XmlPullParserUtil.isEndTag(xpp, "AudioChannelConfiguration")) {
                return audioChannels;
            }
        }
    }

    public static void maybeSkipTag(XmlPullParser xpp) throws IOException, XmlPullParserException {
        if (XmlPullParserUtil.isStartTag(xpp)) {
            int depth = 1;
            while (depth != 0) {
                xpp.next();
                if (XmlPullParserUtil.isStartTag(xpp)) {
                    depth++;
                } else if (XmlPullParserUtil.isEndTag(xpp)) {
                    depth--;
                }
            }
        }
    }

    private static void filterRedundantIncompleteSchemeDatas(ArrayList<SchemeData> schemeDatas) {
        for (int i = schemeDatas.size() - 1; i >= 0; i--) {
            SchemeData schemeData = (SchemeData) schemeDatas.get(i);
            if (!schemeData.hasData()) {
                for (int j = 0; j < schemeDatas.size(); j++) {
                    if (((SchemeData) schemeDatas.get(j)).canReplace(schemeData)) {
                        schemeDatas.remove(i);
                        break;
                    }
                }
            }
        }
    }

    private static String getSampleMimeType(String containerMimeType, String codecs) {
        if (MimeTypes.isAudio(containerMimeType)) {
            return MimeTypes.getAudioMediaMimeType(codecs);
        }
        if (MimeTypes.isVideo(containerMimeType)) {
            return MimeTypes.getVideoMediaMimeType(codecs);
        }
        if (mimeTypeIsRawText(containerMimeType)) {
            return containerMimeType;
        }
        if (MimeTypes.APPLICATION_MP4.equals(containerMimeType)) {
            if (codecs != null) {
                if (codecs.startsWith("stpp")) {
                    return MimeTypes.APPLICATION_TTML;
                }
                if (codecs.startsWith("wvtt")) {
                    return MimeTypes.APPLICATION_MP4VTT;
                }
            }
        } else if (MimeTypes.APPLICATION_RAWCC.equals(containerMimeType)) {
            if (codecs != null) {
                if (codecs.contains("cea708")) {
                    return MimeTypes.APPLICATION_CEA708;
                }
                if (!codecs.contains("eia608")) {
                    if (codecs.contains("cea608")) {
                    }
                }
                return MimeTypes.APPLICATION_CEA608;
            }
            return null;
        }
        return null;
    }

    private static boolean mimeTypeIsRawText(String mimeType) {
        if (!MimeTypes.isText(mimeType)) {
            if (!MimeTypes.APPLICATION_TTML.equals(mimeType)) {
                if (!MimeTypes.APPLICATION_MP4VTT.equals(mimeType)) {
                    if (!MimeTypes.APPLICATION_CEA708.equals(mimeType)) {
                        if (!MimeTypes.APPLICATION_CEA608.equals(mimeType)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private static String checkLanguageConsistency(String firstLanguage, String secondLanguage) {
        if (firstLanguage == null) {
            return secondLanguage;
        }
        if (secondLanguage == null) {
            return firstLanguage;
        }
        Assertions.checkState(firstLanguage.equals(secondLanguage));
        return firstLanguage;
    }

    private static int checkContentTypeConsistency(int firstType, int secondType) {
        if (firstType == -1) {
            return secondType;
        }
        if (secondType == -1) {
            return firstType;
        }
        Assertions.checkState(firstType == secondType);
        return firstType;
    }

    protected static Descriptor parseDescriptor(XmlPullParser xpp, String tag) throws XmlPullParserException, IOException {
        String schemeIdUri = parseString(xpp, "schemeIdUri", "");
        String value = parseString(xpp, "value", null);
        String id = parseString(xpp, "id", null);
        while (true) {
            xpp.next();
            if (XmlPullParserUtil.isEndTag(xpp, tag)) {
                return new Descriptor(schemeIdUri, value, id);
            }
        }
    }

    protected static int parseCea608AccessibilityChannel(List<Descriptor> accessibilityDescriptors) {
        for (int i = 0; i < accessibilityDescriptors.size(); i++) {
            Descriptor descriptor = (Descriptor) accessibilityDescriptors.get(i);
            if ("urn:scte:dash:cc:cea-608:2015".equals(descriptor.schemeIdUri) && descriptor.value != null) {
                Matcher accessibilityValueMatcher = CEA_608_ACCESSIBILITY_PATTERN.matcher(descriptor.value);
                if (accessibilityValueMatcher.matches()) {
                    return Integer.parseInt(accessibilityValueMatcher.group(1));
                }
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unable to parse CEA-608 channel number from: ");
                stringBuilder.append(descriptor.value);
                Log.m10w(str, stringBuilder.toString());
            }
        }
        return -1;
    }

    protected static int parseCea708AccessibilityChannel(List<Descriptor> accessibilityDescriptors) {
        for (int i = 0; i < accessibilityDescriptors.size(); i++) {
            Descriptor descriptor = (Descriptor) accessibilityDescriptors.get(i);
            if ("urn:scte:dash:cc:cea-708:2015".equals(descriptor.schemeIdUri) && descriptor.value != null) {
                Matcher accessibilityValueMatcher = CEA_708_ACCESSIBILITY_PATTERN.matcher(descriptor.value);
                if (accessibilityValueMatcher.matches()) {
                    return Integer.parseInt(accessibilityValueMatcher.group(1));
                }
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unable to parse CEA-708 service block number from: ");
                stringBuilder.append(descriptor.value);
                Log.m10w(str, stringBuilder.toString());
            }
        }
        return -1;
    }

    protected static String parseEac3SupplementalProperties(List<Descriptor> supplementalProperties) {
        for (int i = 0; i < supplementalProperties.size(); i++) {
            Descriptor descriptor = (Descriptor) supplementalProperties.get(i);
            if ("tag:dolby.com,2014:dash:DolbyDigitalPlusExtensionType:2014".equals(descriptor.schemeIdUri)) {
                if ("ec+3".equals(descriptor.value)) {
                    return MimeTypes.AUDIO_E_AC3_JOC;
                }
            }
        }
        return MimeTypes.AUDIO_E_AC3;
    }

    protected static float parseFrameRate(XmlPullParser xpp, float defaultValue) {
        float frameRate = defaultValue;
        String frameRateAttribute = xpp.getAttributeValue(null, "frameRate");
        if (frameRateAttribute == null) {
            return frameRate;
        }
        Matcher frameRateMatcher = FRAME_RATE_PATTERN.matcher(frameRateAttribute);
        if (!frameRateMatcher.matches()) {
            return frameRate;
        }
        int numerator = Integer.parseInt(frameRateMatcher.group(1));
        String denominatorString = frameRateMatcher.group(2);
        if (TextUtils.isEmpty(denominatorString)) {
            return (float) numerator;
        }
        return ((float) numerator) / ((float) Integer.parseInt(denominatorString));
    }

    protected static long parseDuration(XmlPullParser xpp, String name, long defaultValue) {
        String value = xpp.getAttributeValue(null, name);
        if (value == null) {
            return defaultValue;
        }
        return Util.parseXsDuration(value);
    }

    protected static long parseDateTime(XmlPullParser xpp, String name, long defaultValue) throws ParserException {
        String value = xpp.getAttributeValue(null, name);
        if (value == null) {
            return defaultValue;
        }
        return Util.parseXsDateTime(value);
    }

    protected static String parseBaseUrl(XmlPullParser xpp, String parentBaseUrl) throws XmlPullParserException, IOException {
        xpp.next();
        return UriUtil.resolve(parentBaseUrl, xpp.getText());
    }

    protected static int parseInt(XmlPullParser xpp, String name, int defaultValue) {
        String value = xpp.getAttributeValue(null, name);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    protected static long parseLong(XmlPullParser xpp, String name, long defaultValue) {
        String value = xpp.getAttributeValue(null, name);
        return value == null ? defaultValue : Long.parseLong(value);
    }

    protected static String parseString(XmlPullParser xpp, String name, String defaultValue) {
        String value = xpp.getAttributeValue(null, name);
        return value == null ? defaultValue : value;
    }

    protected static int parseDolbyChannelConfiguration(XmlPullParser xpp) {
        String value = Util.toLowerInvariant(xpp.getAttributeValue(null, "value"));
        if (value == null) {
            return -1;
        }
        Object obj;
        int hashCode = value.hashCode();
        if (hashCode != 1596796) {
            if (hashCode != 2937391) {
                if (hashCode != 3094035) {
                    if (hashCode == 3133436 && value.equals("fa01")) {
                        obj = 3;
                        switch (obj) {
                            case null:
                                return 1;
                            case 1:
                                return 2;
                            case 2:
                                return 6;
                            case 3:
                                return 8;
                            default:
                                return -1;
                        }
                    }
                } else if (value.equals("f801")) {
                    obj = 2;
                    switch (obj) {
                        case null:
                            return 1;
                        case 1:
                            return 2;
                        case 2:
                            return 6;
                        case 3:
                            return 8;
                        default:
                            return -1;
                    }
                }
            } else if (value.equals("a000")) {
                obj = 1;
                switch (obj) {
                    case null:
                        return 1;
                    case 1:
                        return 2;
                    case 2:
                        return 6;
                    case 3:
                        return 8;
                    default:
                        return -1;
                }
            }
        } else if (value.equals("4000")) {
            obj = null;
            switch (obj) {
                case null:
                    return 1;
                case 1:
                    return 2;
                case 2:
                    return 6;
                case 3:
                    return 8;
                default:
                    return -1;
            }
        }
        obj = -1;
        switch (obj) {
            case null:
                return 1;
            case 1:
                return 2;
            case 2:
                return 6;
            case 3:
                return 8;
            default:
                return -1;
        }
    }
}
