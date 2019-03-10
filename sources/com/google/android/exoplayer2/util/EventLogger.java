package com.google.android.exoplayer2.util;

import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.Surface;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.analytics.AnalyticsListener.-CC;
import com.google.android.exoplayer2.analytics.AnalyticsListener.EventTime;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.MediaSourceEventListener$LoadEventInfo;
import com.google.android.exoplayer2.source.MediaSourceEventListener$MediaLoadData;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import io.reactivex.annotations.SchedulerSupport;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class EventLogger implements AnalyticsListener {
    private static final String DEFAULT_TAG = "EventLogger";
    private static final int MAX_TIMELINE_ITEM_LINES = 3;
    private static final NumberFormat TIME_FORMAT = NumberFormat.getInstance(Locale.US);
    private final Period period;
    private final long startTimeMs;
    private final String tag;
    @Nullable
    private final MappingTrackSelector trackSelector;
    private final Window window;

    public /* synthetic */ void onAudioAttributesChanged(EventTime eventTime, AudioAttributes audioAttributes) {
        -CC.$default$onAudioAttributesChanged(this, eventTime, audioAttributes);
    }

    public /* synthetic */ void onVolumeChanged(EventTime eventTime, float f) {
        -CC.$default$onVolumeChanged(this, eventTime, f);
    }

    static {
        TIME_FORMAT.setMinimumFractionDigits(2);
        TIME_FORMAT.setMaximumFractionDigits(2);
        TIME_FORMAT.setGroupingUsed(false);
    }

    public EventLogger(@Nullable MappingTrackSelector trackSelector) {
        this(trackSelector, DEFAULT_TAG);
    }

    public EventLogger(@Nullable MappingTrackSelector trackSelector, String tag) {
        this.trackSelector = trackSelector;
        this.tag = tag;
        this.window = new Window();
        this.period = new Period();
        this.startTimeMs = SystemClock.elapsedRealtime();
    }

    public void onLoadingChanged(EventTime eventTime, boolean isLoading) {
        logd(eventTime, "loading", Boolean.toString(isLoading));
    }

    public void onPlayerStateChanged(EventTime eventTime, boolean playWhenReady, int state) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(playWhenReady);
        stringBuilder.append(", ");
        stringBuilder.append(getStateString(state));
        logd(eventTime, "state", stringBuilder.toString());
    }

    public void onRepeatModeChanged(EventTime eventTime, int repeatMode) {
        logd(eventTime, "repeatMode", getRepeatModeString(repeatMode));
    }

    public void onShuffleModeChanged(EventTime eventTime, boolean shuffleModeEnabled) {
        logd(eventTime, "shuffleModeEnabled", Boolean.toString(shuffleModeEnabled));
    }

    public void onPositionDiscontinuity(EventTime eventTime, int reason) {
        logd(eventTime, "positionDiscontinuity", getDiscontinuityReasonString(reason));
    }

    public void onSeekStarted(EventTime eventTime) {
        logd(eventTime, "seekStarted");
    }

    public void onPlaybackParametersChanged(EventTime eventTime, PlaybackParameters playbackParameters) {
        logd(eventTime, "playbackParameters", Util.formatInvariant("speed=%.2f, pitch=%.2f, skipSilence=%s", new Object[]{Float.valueOf(playbackParameters.speed), Float.valueOf(playbackParameters.pitch), Boolean.valueOf(playbackParameters.skipSilence)}));
    }

    public void onTimelineChanged(EventTime eventTime, int reason) {
        int i;
        int periodCount = eventTime.timeline.getPeriodCount();
        int windowCount = eventTime.timeline.getWindowCount();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("timelineChanged [");
        stringBuilder.append(getEventTimeString(eventTime));
        stringBuilder.append(", periodCount=");
        stringBuilder.append(periodCount);
        stringBuilder.append(", windowCount=");
        stringBuilder.append(windowCount);
        stringBuilder.append(", reason=");
        stringBuilder.append(getTimelineChangeReasonString(reason));
        logd(stringBuilder.toString());
        for (i = 0; i < Math.min(periodCount, 3); i++) {
            eventTime.timeline.getPeriod(i, this.period);
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("  period [");
            stringBuilder2.append(getTimeString(this.period.getDurationMs()));
            stringBuilder2.append("]");
            logd(stringBuilder2.toString());
        }
        if (periodCount > 3) {
            logd("  ...");
        }
        for (i = 0; i < Math.min(windowCount, 3); i++) {
            eventTime.timeline.getWindow(i, this.window);
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("  window [");
            stringBuilder3.append(getTimeString(this.window.getDurationMs()));
            stringBuilder3.append(", ");
            stringBuilder3.append(this.window.isSeekable);
            stringBuilder3.append(", ");
            stringBuilder3.append(this.window.isDynamic);
            stringBuilder3.append("]");
            logd(stringBuilder3.toString());
        }
        if (windowCount > 3) {
            logd("  ...");
        }
        logd("]");
    }

    public void onPlayerError(EventTime eventTime, ExoPlaybackException e) {
        loge(eventTime, "playerFailed", e);
    }

    public void onTracksChanged(EventTime eventTime, TrackGroupArray ignored, TrackSelectionArray trackSelections) {
        MappingTrackSelector mappingTrackSelector = this.trackSelector;
        MappedTrackInfo mappedTrackInfo = mappingTrackSelector != null ? mappingTrackSelector.getCurrentMappedTrackInfo() : null;
        if (mappedTrackInfo == null) {
            logd(eventTime, "tracksChanged", "[]");
            return;
        }
        EventTime eventTime2 = eventTime;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("tracksChanged [");
        stringBuilder.append(getEventTimeString(eventTime));
        stringBuilder.append(", ");
        logd(stringBuilder.toString());
        int rendererCount = mappedTrackInfo.getRendererCount();
        int rendererIndex = 0;
        while (true) {
            boolean z = false;
            if (rendererIndex >= rendererCount) {
                break;
            }
            int selectionIndex;
            TrackGroupArray rendererTrackGroups = mappedTrackInfo.getTrackGroups(rendererIndex);
            TrackSelection trackSelection = trackSelections.get(rendererIndex);
            if (rendererTrackGroups.length > 0) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("  Renderer:");
                stringBuilder2.append(rendererIndex);
                stringBuilder2.append(" [");
                logd(stringBuilder2.toString());
                int groupIndex = 0;
                while (groupIndex < rendererTrackGroups.length) {
                    TrackGroup trackGroup = rendererTrackGroups.get(groupIndex);
                    String adaptiveSupport = getAdaptiveSupportString(trackGroup.length, mappedTrackInfo.getAdaptiveSupport(rendererIndex, groupIndex, z));
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("    Group:");
                    stringBuilder3.append(groupIndex);
                    stringBuilder3.append(", adaptive_supported=");
                    stringBuilder3.append(adaptiveSupport);
                    stringBuilder3.append(" [");
                    logd(stringBuilder3.toString());
                    for (int trackIndex = 0; trackIndex < trackGroup.length; trackIndex++) {
                        String status = getTrackStatusString(trackSelection, trackGroup, trackIndex);
                        String formatSupport = getFormatSupportString(mappedTrackInfo.getTrackSupport(rendererIndex, groupIndex, trackIndex));
                        StringBuilder stringBuilder4 = new StringBuilder();
                        stringBuilder4.append("      ");
                        stringBuilder4.append(status);
                        stringBuilder4.append(" Track:");
                        stringBuilder4.append(trackIndex);
                        stringBuilder4.append(", ");
                        stringBuilder4.append(Format.toLogString(trackGroup.getFormat(trackIndex)));
                        stringBuilder4.append(", supported=");
                        stringBuilder4.append(formatSupport);
                        logd(stringBuilder4.toString());
                    }
                    logd("    ]");
                    groupIndex++;
                    z = false;
                }
                if (trackSelection != null) {
                    for (selectionIndex = 0; selectionIndex < trackSelection.length(); selectionIndex++) {
                        Metadata metadata = trackSelection.getFormat(selectionIndex).metadata;
                        if (metadata != null) {
                            logd("    Metadata [");
                            printMetadata(metadata, "      ");
                            logd("    ]");
                            break;
                        }
                    }
                }
                logd("  ]");
            }
            rendererIndex++;
        }
        TrackSelectionArray trackSelectionArray = trackSelections;
        TrackGroupArray unassociatedTrackGroups = mappedTrackInfo.getUnmappedTrackGroups();
        if (unassociatedTrackGroups.length > 0) {
            logd("  Renderer:None [");
            for (selectionIndex = 0; selectionIndex < unassociatedTrackGroups.length; selectionIndex++) {
                StringBuilder stringBuilder5 = new StringBuilder();
                stringBuilder5.append("    Group:");
                stringBuilder5.append(selectionIndex);
                stringBuilder5.append(" [");
                logd(stringBuilder5.toString());
                TrackGroup trackGroup2 = unassociatedTrackGroups.get(selectionIndex);
                for (int trackIndex2 = 0; trackIndex2 < trackGroup2.length; trackIndex2++) {
                    String status2 = getTrackStatusString(false);
                    adaptiveSupport = getFormatSupportString(0);
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("      ");
                    stringBuilder3.append(status2);
                    stringBuilder3.append(" Track:");
                    stringBuilder3.append(trackIndex2);
                    stringBuilder3.append(", ");
                    stringBuilder3.append(Format.toLogString(trackGroup2.getFormat(trackIndex2)));
                    stringBuilder3.append(", supported=");
                    stringBuilder3.append(adaptiveSupport);
                    logd(stringBuilder3.toString());
                }
                logd("    ]");
            }
            logd("  ]");
        }
        logd("]");
    }

    public void onSeekProcessed(EventTime eventTime) {
        logd(eventTime, "seekProcessed");
    }

    public void onMetadata(EventTime eventTime, Metadata metadata) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("metadata [");
        stringBuilder.append(getEventTimeString(eventTime));
        stringBuilder.append(", ");
        logd(stringBuilder.toString());
        printMetadata(metadata, "  ");
        logd("]");
    }

    public void onDecoderEnabled(EventTime eventTime, int trackType, DecoderCounters counters) {
        logd(eventTime, "decoderEnabled", getTrackTypeString(trackType));
    }

    public void onAudioSessionId(EventTime eventTime, int audioSessionId) {
        logd(eventTime, "audioSessionId", Integer.toString(audioSessionId));
    }

    public void onDecoderInitialized(EventTime eventTime, int trackType, String decoderName, long initializationDurationMs) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getTrackTypeString(trackType));
        stringBuilder.append(", ");
        stringBuilder.append(decoderName);
        logd(eventTime, "decoderInitialized", stringBuilder.toString());
    }

    public void onDecoderInputFormatChanged(EventTime eventTime, int trackType, Format format) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getTrackTypeString(trackType));
        stringBuilder.append(", ");
        stringBuilder.append(Format.toLogString(format));
        logd(eventTime, "decoderInputFormatChanged", stringBuilder.toString());
    }

    public void onDecoderDisabled(EventTime eventTime, int trackType, DecoderCounters counters) {
        logd(eventTime, "decoderDisabled", getTrackTypeString(trackType));
    }

    public void onAudioUnderrun(EventTime eventTime, int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(bufferSize);
        stringBuilder.append(", ");
        stringBuilder.append(bufferSizeMs);
        stringBuilder.append(", ");
        stringBuilder.append(elapsedSinceLastFeedMs);
        stringBuilder.append("]");
        loge(eventTime, "audioTrackUnderrun", stringBuilder.toString(), null);
    }

    public void onDroppedVideoFrames(EventTime eventTime, int count, long elapsedMs) {
        logd(eventTime, "droppedFrames", Integer.toString(count));
    }

    public void onVideoSizeChanged(EventTime eventTime, int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(width);
        stringBuilder.append(", ");
        stringBuilder.append(height);
        logd(eventTime, "videoSizeChanged", stringBuilder.toString());
    }

    public void onRenderedFirstFrame(EventTime eventTime, @Nullable Surface surface) {
        logd(eventTime, "renderedFirstFrame", String.valueOf(surface));
    }

    public void onMediaPeriodCreated(EventTime eventTime) {
        logd(eventTime, "mediaPeriodCreated");
    }

    public void onMediaPeriodReleased(EventTime eventTime) {
        logd(eventTime, "mediaPeriodReleased");
    }

    public void onLoadStarted(EventTime eventTime, MediaSourceEventListener$LoadEventInfo loadEventInfo, MediaSourceEventListener$MediaLoadData mediaLoadData) {
    }

    public void onLoadError(EventTime eventTime, MediaSourceEventListener$LoadEventInfo loadEventInfo, MediaSourceEventListener$MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
        printInternalError(eventTime, "loadError", error);
    }

    public void onLoadCanceled(EventTime eventTime, MediaSourceEventListener$LoadEventInfo loadEventInfo, MediaSourceEventListener$MediaLoadData mediaLoadData) {
    }

    public void onLoadCompleted(EventTime eventTime, MediaSourceEventListener$LoadEventInfo loadEventInfo, MediaSourceEventListener$MediaLoadData mediaLoadData) {
    }

    public void onReadingStarted(EventTime eventTime) {
        logd(eventTime, "mediaPeriodReadingStarted");
    }

    public void onBandwidthEstimate(EventTime eventTime, int totalLoadTimeMs, long totalBytesLoaded, long bitrateEstimate) {
    }

    public void onSurfaceSizeChanged(EventTime eventTime, int width, int height) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(width);
        stringBuilder.append(", ");
        stringBuilder.append(height);
        logd(eventTime, "surfaceSizeChanged", stringBuilder.toString());
    }

    public void onUpstreamDiscarded(EventTime eventTime, MediaSourceEventListener$MediaLoadData mediaLoadData) {
        logd(eventTime, "upstreamDiscarded", Format.toLogString(mediaLoadData.trackFormat));
    }

    public void onDownstreamFormatChanged(EventTime eventTime, MediaSourceEventListener$MediaLoadData mediaLoadData) {
        logd(eventTime, "downstreamFormatChanged", Format.toLogString(mediaLoadData.trackFormat));
    }

    public void onDrmSessionAcquired(EventTime eventTime) {
        logd(eventTime, "drmSessionAcquired");
    }

    public void onDrmSessionManagerError(EventTime eventTime, Exception e) {
        printInternalError(eventTime, "drmSessionManagerError", e);
    }

    public void onDrmKeysRestored(EventTime eventTime) {
        logd(eventTime, "drmKeysRestored");
    }

    public void onDrmKeysRemoved(EventTime eventTime) {
        logd(eventTime, "drmKeysRemoved");
    }

    public void onDrmKeysLoaded(EventTime eventTime) {
        logd(eventTime, "drmKeysLoaded");
    }

    public void onDrmSessionReleased(EventTime eventTime) {
        logd(eventTime, "drmSessionReleased");
    }

    protected void logd(String msg) {
        Log.m4d(this.tag, msg);
    }

    protected void loge(String msg, @Nullable Throwable tr) {
        Log.m7e(this.tag, msg, tr);
    }

    private void logd(EventTime eventTime, String eventName) {
        logd(getEventString(eventTime, eventName));
    }

    private void logd(EventTime eventTime, String eventName, String eventDescription) {
        logd(getEventString(eventTime, eventName, eventDescription));
    }

    private void loge(EventTime eventTime, String eventName, @Nullable Throwable throwable) {
        loge(getEventString(eventTime, eventName), throwable);
    }

    private void loge(EventTime eventTime, String eventName, String eventDescription, @Nullable Throwable throwable) {
        loge(getEventString(eventTime, eventName, eventDescription), throwable);
    }

    private void printInternalError(EventTime eventTime, String type, Exception e) {
        loge(eventTime, "internalError", type, e);
    }

    private void printMetadata(Metadata metadata, String prefix) {
        for (int i = 0; i < metadata.length(); i++) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(prefix);
            stringBuilder.append(metadata.get(i));
            logd(stringBuilder.toString());
        }
    }

    private String getEventString(EventTime eventTime, String eventName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(eventName);
        stringBuilder.append(" [");
        stringBuilder.append(getEventTimeString(eventTime));
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    private String getEventString(EventTime eventTime, String eventName, String eventDescription) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(eventName);
        stringBuilder.append(" [");
        stringBuilder.append(getEventTimeString(eventTime));
        stringBuilder.append(", ");
        stringBuilder.append(eventDescription);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    private String getEventTimeString(EventTime eventTime) {
        StringBuilder stringBuilder;
        String windowPeriodString = new StringBuilder();
        windowPeriodString.append("window=");
        windowPeriodString.append(eventTime.windowIndex);
        windowPeriodString = windowPeriodString.toString();
        if (eventTime.mediaPeriodId != null) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(windowPeriodString);
            stringBuilder.append(", period=");
            stringBuilder.append(eventTime.timeline.getIndexOfPeriod(eventTime.mediaPeriodId.periodUid));
            windowPeriodString = stringBuilder.toString();
            if (eventTime.mediaPeriodId.isAd()) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(windowPeriodString);
                stringBuilder.append(", adGroup=");
                stringBuilder.append(eventTime.mediaPeriodId.adGroupIndex);
                windowPeriodString = stringBuilder.toString();
                stringBuilder = new StringBuilder();
                stringBuilder.append(windowPeriodString);
                stringBuilder.append(", ad=");
                stringBuilder.append(eventTime.mediaPeriodId.adIndexInAdGroup);
                windowPeriodString = stringBuilder.toString();
            }
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append(getTimeString(eventTime.realtimeMs - this.startTimeMs));
        stringBuilder.append(", ");
        stringBuilder.append(getTimeString(eventTime.currentPlaybackPositionMs));
        stringBuilder.append(", ");
        stringBuilder.append(windowPeriodString);
        return stringBuilder.toString();
    }

    private static String getTimeString(long timeMs) {
        return timeMs == C0555C.TIME_UNSET ? "?" : TIME_FORMAT.format((double) (((float) timeMs) / 1000.0f));
    }

    private static String getStateString(int state) {
        switch (state) {
            case 1:
                return "IDLE";
            case 2:
                return "BUFFERING";
            case 3:
                return "READY";
            case 4:
                return "ENDED";
            default:
                return "?";
        }
    }

    private static String getFormatSupportString(int formatSupport) {
        switch (formatSupport) {
            case 0:
                return "NO";
            case 1:
                return "NO_UNSUPPORTED_TYPE";
            case 2:
                return "NO_UNSUPPORTED_DRM";
            case 3:
                return "NO_EXCEEDS_CAPABILITIES";
            case 4:
                return "YES";
            default:
                return "?";
        }
    }

    private static String getAdaptiveSupportString(int trackCount, int adaptiveSupport) {
        if (trackCount < 2) {
            return "N/A";
        }
        if (adaptiveSupport == 0) {
            return "NO";
        }
        if (adaptiveSupport == 8) {
            return "YES_NOT_SEAMLESS";
        }
        if (adaptiveSupport != 16) {
            return "?";
        }
        return "YES";
    }

    private static String getTrackStatusString(@Nullable TrackSelection selection, TrackGroup group, int trackIndex) {
        boolean z;
        if (selection != null && selection.getTrackGroup() == group) {
            if (selection.indexOf(trackIndex) != -1) {
                z = true;
                return getTrackStatusString(z);
            }
        }
        z = false;
        return getTrackStatusString(z);
    }

    private static String getTrackStatusString(boolean enabled) {
        return enabled ? "[X]" : "[ ]";
    }

    private static String getRepeatModeString(int repeatMode) {
        switch (repeatMode) {
            case 0:
                return "OFF";
            case 1:
                return "ONE";
            case 2:
                return "ALL";
            default:
                return "?";
        }
    }

    private static String getDiscontinuityReasonString(int reason) {
        switch (reason) {
            case 0:
                return "PERIOD_TRANSITION";
            case 1:
                return "SEEK";
            case 2:
                return "SEEK_ADJUSTMENT";
            case 3:
                return "AD_INSERTION";
            case 4:
                return "INTERNAL";
            default:
                return "?";
        }
    }

    private static String getTimelineChangeReasonString(int reason) {
        switch (reason) {
            case 0:
                return "PREPARED";
            case 1:
                return "RESET";
            case 2:
                return "DYNAMIC";
            default:
                return "?";
        }
    }

    private static String getTrackTypeString(int trackType) {
        switch (trackType) {
            case 0:
                return "default";
            case 1:
                return MimeTypes.BASE_TYPE_AUDIO;
            case 2:
                return MimeTypes.BASE_TYPE_VIDEO;
            case 3:
                return "text";
            case 4:
                return TtmlNode.TAG_METADATA;
            case 5:
                return "camera motion";
            case 6:
                return SchedulerSupport.NONE;
            default:
                String stringBuilder;
                if (trackType >= 10000) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("custom (");
                    stringBuilder2.append(trackType);
                    stringBuilder2.append(")");
                    stringBuilder = stringBuilder2.toString();
                } else {
                    stringBuilder = "?";
                }
                return stringBuilder;
        }
    }
}
