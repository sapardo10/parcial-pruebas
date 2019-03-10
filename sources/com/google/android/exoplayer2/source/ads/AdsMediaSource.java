package com.google.android.exoplayer2.source.ads;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.source.CompositeMediaSource;
import com.google.android.exoplayer2.source.DeferredMediaPeriod;
import com.google.android.exoplayer2.source.DeferredMediaPeriod.PrepareErrorListener;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AdsMediaSource extends CompositeMediaSource<MediaPeriodId> {
    private static final MediaPeriodId DUMMY_CONTENT_MEDIA_PERIOD_ID = new MediaPeriodId(new Object());
    private MediaSource[][] adGroupMediaSources;
    private Timeline[][] adGroupTimelines;
    private final MediaSourceFactory adMediaSourceFactory;
    private AdPlaybackState adPlaybackState;
    private final ViewGroup adUiViewGroup;
    private final AdsLoader adsLoader;
    private ComponentListener componentListener;
    private Object contentManifest;
    private final MediaSource contentMediaSource;
    private Timeline contentTimeline;
    private final Map<MediaSource, List<DeferredMediaPeriod>> deferredMediaPeriodByAdMediaSource;
    @Nullable
    private final Handler eventHandler;
    @Nullable
    private final EventListener eventListener;
    private final Handler mainHandler;
    private final Period period;

    public static final class AdLoadException extends IOException {
        public static final int TYPE_AD = 0;
        public static final int TYPE_AD_GROUP = 1;
        public static final int TYPE_ALL_ADS = 2;
        public static final int TYPE_UNEXPECTED = 3;
        public final int type;

        public static AdLoadException createForAd(Exception error) {
            return new AdLoadException(0, error);
        }

        public static AdLoadException createForAdGroup(Exception error, int adGroupIndex) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to load ad group ");
            stringBuilder.append(adGroupIndex);
            return new AdLoadException(1, new IOException(stringBuilder.toString(), error));
        }

        public static AdLoadException createForAllAds(Exception error) {
            return new AdLoadException(2, error);
        }

        public static AdLoadException createForUnexpected(RuntimeException error) {
            return new AdLoadException(3, error);
        }

        private AdLoadException(int type, Exception cause) {
            super(cause);
            this.type = type;
        }

        public RuntimeException getRuntimeExceptionForUnexpected() {
            Assertions.checkState(this.type == 3);
            return (RuntimeException) getCause();
        }
    }

    @Deprecated
    public interface EventListener {
        void onAdClicked();

        void onAdLoadError(IOException iOException);

        void onAdTapped();

        void onInternalAdLoadError(RuntimeException runtimeException);
    }

    public interface MediaSourceFactory {
        MediaSource createMediaSource(Uri uri);

        int[] getSupportedTypes();
    }

    private final class AdPrepareErrorListener implements PrepareErrorListener {
        private final int adGroupIndex;
        private final int adIndexInAdGroup;
        private final Uri adUri;

        public AdPrepareErrorListener(Uri adUri, int adGroupIndex, int adIndexInAdGroup) {
            this.adUri = adUri;
            this.adGroupIndex = adGroupIndex;
            this.adIndexInAdGroup = adIndexInAdGroup;
        }

        public void onPrepareError(MediaPeriodId mediaPeriodId, IOException exception) {
            AdsMediaSource.this.createEventDispatcher(mediaPeriodId).loadError(new DataSpec(this.adUri), this.adUri, Collections.emptyMap(), 6, -1, 0, 0, AdLoadException.createForAd(exception), true);
            AdsMediaSource.this.mainHandler.post(new C0626xf756bb9(this, exception));
        }
    }

    private final class ComponentListener implements com.google.android.exoplayer2.source.ads.AdsLoader.EventListener {
        private final Handler playerHandler = new Handler();
        private volatile boolean released;

        public void release() {
            this.released = true;
            this.playerHandler.removeCallbacksAndMessages(null);
        }

        public void onAdPlaybackState(AdPlaybackState adPlaybackState) {
            if (!this.released) {
                this.playerHandler.post(new C0627xc868a828(this, adPlaybackState));
            }
        }

        public static /* synthetic */ void lambda$onAdPlaybackState$0(ComponentListener componentListener, AdPlaybackState adPlaybackState) {
            if (!componentListener.released) {
                AdsMediaSource.this.onAdPlaybackState(adPlaybackState);
            }
        }

        public void onAdClicked() {
            if (!this.released) {
                if (AdsMediaSource.this.eventHandler != null && AdsMediaSource.this.eventListener != null) {
                    AdsMediaSource.this.eventHandler.post(new C0630xc0c60fab());
                }
            }
        }

        public static /* synthetic */ void lambda$onAdClicked$1(ComponentListener componentListener) {
            if (!componentListener.released) {
                AdsMediaSource.this.eventListener.onAdClicked();
            }
        }

        public void onAdTapped() {
            if (!this.released) {
                if (AdsMediaSource.this.eventHandler != null && AdsMediaSource.this.eventListener != null) {
                    AdsMediaSource.this.eventHandler.post(new C0629xbc495fec());
                }
            }
        }

        public static /* synthetic */ void lambda$onAdTapped$2(ComponentListener componentListener) {
            if (!componentListener.released) {
                AdsMediaSource.this.eventListener.onAdTapped();
            }
        }

        public void onAdLoadError(AdLoadException error, DataSpec dataSpec) {
            if (!this.released) {
                AdsMediaSource.this.createEventDispatcher(null).loadError(dataSpec, dataSpec.uri, Collections.emptyMap(), 6, -1, 0, 0, error, true);
                if (AdsMediaSource.this.eventHandler == null || AdsMediaSource.this.eventListener == null) {
                    AdLoadException adLoadException = error;
                } else {
                    AdsMediaSource.this.eventHandler.post(new C0628xb24b6d47(r0, error));
                }
            }
        }

        public static /* synthetic */ void lambda$onAdLoadError$3(ComponentListener componentListener, AdLoadException error) {
            if (!componentListener.released) {
                if (error.type == 3) {
                    AdsMediaSource.this.eventListener.onInternalAdLoadError(error.getRuntimeExceptionForUnexpected());
                } else {
                    AdsMediaSource.this.eventListener.onAdLoadError(error);
                }
            }
        }
    }

    public AdsMediaSource(MediaSource contentMediaSource, Factory dataSourceFactory, AdsLoader adsLoader, ViewGroup adUiViewGroup) {
        this(contentMediaSource, new ExtractorMediaSource.Factory(dataSourceFactory), adsLoader, adUiViewGroup, null, null);
    }

    public AdsMediaSource(MediaSource contentMediaSource, MediaSourceFactory adMediaSourceFactory, AdsLoader adsLoader, ViewGroup adUiViewGroup) {
        this(contentMediaSource, adMediaSourceFactory, adsLoader, adUiViewGroup, null, null);
    }

    @Deprecated
    public AdsMediaSource(MediaSource contentMediaSource, Factory dataSourceFactory, AdsLoader adsLoader, ViewGroup adUiViewGroup, @Nullable Handler eventHandler, @Nullable EventListener eventListener) {
        this(contentMediaSource, new ExtractorMediaSource.Factory(dataSourceFactory), adsLoader, adUiViewGroup, eventHandler, eventListener);
    }

    @Deprecated
    public AdsMediaSource(MediaSource contentMediaSource, MediaSourceFactory adMediaSourceFactory, AdsLoader adsLoader, ViewGroup adUiViewGroup, @Nullable Handler eventHandler, @Nullable EventListener eventListener) {
        this.contentMediaSource = contentMediaSource;
        this.adMediaSourceFactory = adMediaSourceFactory;
        this.adsLoader = adsLoader;
        this.adUiViewGroup = adUiViewGroup;
        this.eventHandler = eventHandler;
        this.eventListener = eventListener;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.deferredMediaPeriodByAdMediaSource = new HashMap();
        this.period = new Period();
        this.adGroupMediaSources = new MediaSource[0][];
        this.adGroupTimelines = new Timeline[0][];
        adsLoader.setSupportedContentTypes(adMediaSourceFactory.getSupportedTypes());
    }

    @Nullable
    public Object getTag() {
        return this.contentMediaSource.getTag();
    }

    public void prepareSourceInternal(ExoPlayer player, boolean isTopLevelSource, @Nullable TransferListener mediaTransferListener) {
        super.prepareSourceInternal(player, isTopLevelSource, mediaTransferListener);
        Assertions.checkArgument(isTopLevelSource, "AdsMediaSource must be the top-level source used to prepare the player.");
        ComponentListener componentListener = new ComponentListener();
        this.componentListener = componentListener;
        prepareChildSource(DUMMY_CONTENT_MEDIA_PERIOD_ID, this.contentMediaSource);
        this.mainHandler.post(new -$$Lambda$AdsMediaSource$I9x4oOVLBLy3lKTApMQfS3WwRWU(this, player, componentListener));
    }

    public MediaPeriod createPeriod(MediaPeriodId id, Allocator allocator) {
        if (this.adPlaybackState.adGroupCount <= 0 || !id.isAd()) {
            DeferredMediaPeriod mediaPeriod = new DeferredMediaPeriod(this.contentMediaSource, id, allocator);
            mediaPeriod.createPeriod(id);
            return mediaPeriod;
        }
        MediaSource adMediaSource;
        int adGroupIndex = id.adGroupIndex;
        int adIndexInAdGroup = id.adIndexInAdGroup;
        Uri adUri = this.adPlaybackState.adGroups[adGroupIndex].uris[adIndexInAdGroup];
        if (this.adGroupMediaSources[adGroupIndex].length <= adIndexInAdGroup) {
            adMediaSource = this.adMediaSourceFactory.createMediaSource(adUri);
            MediaSource[][] mediaSourceArr = this.adGroupMediaSources;
            if (adIndexInAdGroup >= mediaSourceArr[adGroupIndex].length) {
                int adCount = adIndexInAdGroup + 1;
                mediaSourceArr[adGroupIndex] = (MediaSource[]) Arrays.copyOf(mediaSourceArr[adGroupIndex], adCount);
                Timeline[][] timelineArr = this.adGroupTimelines;
                timelineArr[adGroupIndex] = (Timeline[]) Arrays.copyOf(timelineArr[adGroupIndex], adCount);
            }
            this.adGroupMediaSources[adGroupIndex][adIndexInAdGroup] = adMediaSource;
            this.deferredMediaPeriodByAdMediaSource.put(adMediaSource, new ArrayList());
            prepareChildSource(id, adMediaSource);
        }
        adMediaSource = this.adGroupMediaSources[adGroupIndex][adIndexInAdGroup];
        DeferredMediaPeriod deferredMediaPeriod = new DeferredMediaPeriod(adMediaSource, id, allocator);
        deferredMediaPeriod.setPrepareErrorListener(new AdPrepareErrorListener(adUri, adGroupIndex, adIndexInAdGroup));
        List<DeferredMediaPeriod> mediaPeriods = (List) this.deferredMediaPeriodByAdMediaSource.get(adMediaSource);
        if (mediaPeriods == null) {
            deferredMediaPeriod.createPeriod(new MediaPeriodId(this.adGroupTimelines[adGroupIndex][adIndexInAdGroup].getUidOfPeriod(0), id.windowSequenceNumber));
        } else {
            mediaPeriods.add(deferredMediaPeriod);
        }
        return deferredMediaPeriod;
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        DeferredMediaPeriod deferredMediaPeriod = (DeferredMediaPeriod) mediaPeriod;
        List<DeferredMediaPeriod> mediaPeriods = (List) this.deferredMediaPeriodByAdMediaSource.get(deferredMediaPeriod.mediaSource);
        if (mediaPeriods != null) {
            mediaPeriods.remove(deferredMediaPeriod);
        }
        deferredMediaPeriod.releasePeriod();
    }

    public void releaseSourceInternal() {
        super.releaseSourceInternal();
        this.componentListener.release();
        this.componentListener = null;
        this.deferredMediaPeriodByAdMediaSource.clear();
        this.contentTimeline = null;
        this.contentManifest = null;
        this.adPlaybackState = null;
        this.adGroupMediaSources = new MediaSource[0][];
        this.adGroupTimelines = new Timeline[0][];
        Handler handler = this.mainHandler;
        AdsLoader adsLoader = this.adsLoader;
        adsLoader.getClass();
        handler.post(new -$$Lambda$2Zac3B-Whc_7swHmcnO1d7h-1Gc(adsLoader));
    }

    protected void onChildSourceInfoRefreshed(MediaPeriodId mediaPeriodId, MediaSource mediaSource, Timeline timeline, @Nullable Object manifest) {
        if (mediaPeriodId.isAd()) {
            onAdSourceInfoRefreshed(mediaSource, mediaPeriodId.adGroupIndex, mediaPeriodId.adIndexInAdGroup, timeline);
        } else {
            onContentSourceInfoRefreshed(timeline, manifest);
        }
    }

    @Nullable
    protected MediaPeriodId getMediaPeriodIdForChildMediaPeriodId(MediaPeriodId childId, MediaPeriodId mediaPeriodId) {
        return childId.isAd() ? childId : mediaPeriodId;
    }

    private void onAdPlaybackState(AdPlaybackState adPlaybackState) {
        if (this.adPlaybackState == null) {
            this.adGroupMediaSources = new MediaSource[adPlaybackState.adGroupCount][];
            Arrays.fill(this.adGroupMediaSources, new MediaSource[0]);
            this.adGroupTimelines = new Timeline[adPlaybackState.adGroupCount][];
            Arrays.fill(this.adGroupTimelines, new Timeline[0]);
        }
        this.adPlaybackState = adPlaybackState;
        maybeUpdateSourceInfo();
    }

    private void onContentSourceInfoRefreshed(Timeline timeline, Object manifest) {
        this.contentTimeline = timeline;
        this.contentManifest = manifest;
        maybeUpdateSourceInfo();
    }

    private void onAdSourceInfoRefreshed(MediaSource mediaSource, int adGroupIndex, int adIndexInAdGroup, Timeline timeline) {
        boolean z = true;
        if (timeline.getPeriodCount() != 1) {
            z = false;
        }
        Assertions.checkArgument(z);
        this.adGroupTimelines[adGroupIndex][adIndexInAdGroup] = timeline;
        List<DeferredMediaPeriod> mediaPeriods = (List) this.deferredMediaPeriodByAdMediaSource.remove(mediaSource);
        if (mediaPeriods != null) {
            Object periodUid = timeline.getUidOfPeriod(null);
            for (int i = 0; i < mediaPeriods.size(); i++) {
                DeferredMediaPeriod mediaPeriod = (DeferredMediaPeriod) mediaPeriods.get(i);
                mediaPeriod.createPeriod(new MediaPeriodId(periodUid, mediaPeriod.id.windowSequenceNumber));
            }
        }
        maybeUpdateSourceInfo();
    }

    private void maybeUpdateSourceInfo() {
        AdPlaybackState adPlaybackState = this.adPlaybackState;
        if (adPlaybackState != null && this.contentTimeline != null) {
            this.adPlaybackState = adPlaybackState.withAdDurationsUs(getAdDurations(this.adGroupTimelines, this.period));
            refreshSourceInfo(this.adPlaybackState.adGroupCount == 0 ? this.contentTimeline : new SinglePeriodAdTimeline(this.contentTimeline, this.adPlaybackState), this.contentManifest);
        }
    }

    private static long[][] getAdDurations(Timeline[][] adTimelines, Period period) {
        long[][] adDurations = new long[adTimelines.length][];
        for (int i = 0; i < adTimelines.length; i++) {
            adDurations[i] = new long[adTimelines[i].length];
            for (int j = 0; j < adTimelines[i].length; j++) {
                long j2;
                long[] jArr = adDurations[i];
                if (adTimelines[i][j] == null) {
                    j2 = C0555C.TIME_UNSET;
                } else {
                    j2 = adTimelines[i][j].getPeriod(0, period).getDurationUs();
                }
                jArr[j] = j2;
            }
        }
        return adDurations;
    }
}
