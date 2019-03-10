package com.google.android.exoplayer2.source.hls;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.source.CompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSourceEventListener$EventDispatcher;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.SequenceableLoader;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper.Callback;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker.PlaylistEventListener;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;

public final class HlsMediaPeriod implements MediaPeriod, Callback, PlaylistEventListener {
    private final Allocator allocator;
    private final boolean allowChunklessPreparation;
    @Nullable
    private MediaPeriod.Callback callback;
    private SequenceableLoader compositeSequenceableLoader;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private final HlsDataSourceFactory dataSourceFactory;
    private HlsSampleStreamWrapper[] enabledSampleStreamWrappers = new HlsSampleStreamWrapper[0];
    private final MediaSourceEventListener$EventDispatcher eventDispatcher;
    private final HlsExtractorFactory extractorFactory;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    @Nullable
    private final TransferListener mediaTransferListener;
    private boolean notifiedReadingStarted;
    private int pendingPrepareCount;
    private final HlsPlaylistTracker playlistTracker;
    private HlsSampleStreamWrapper[] sampleStreamWrappers = new HlsSampleStreamWrapper[0];
    private final IdentityHashMap<SampleStream, Integer> streamWrapperIndices = new IdentityHashMap();
    private final TimestampAdjusterProvider timestampAdjusterProvider = new TimestampAdjusterProvider();
    private TrackGroupArray trackGroups;

    public HlsMediaPeriod(HlsExtractorFactory extractorFactory, HlsPlaylistTracker playlistTracker, HlsDataSourceFactory dataSourceFactory, @Nullable TransferListener mediaTransferListener, LoadErrorHandlingPolicy loadErrorHandlingPolicy, MediaSourceEventListener$EventDispatcher eventDispatcher, Allocator allocator, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, boolean allowChunklessPreparation) {
        this.extractorFactory = extractorFactory;
        this.playlistTracker = playlistTracker;
        this.dataSourceFactory = dataSourceFactory;
        this.mediaTransferListener = mediaTransferListener;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        this.eventDispatcher = eventDispatcher;
        this.allocator = allocator;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        this.allowChunklessPreparation = allowChunklessPreparation;
        this.compositeSequenceableLoader = compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(new SequenceableLoader[0]);
        eventDispatcher.mediaPeriodCreated();
    }

    public void release() {
        this.playlistTracker.removeListener(this);
        for (HlsSampleStreamWrapper sampleStreamWrapper : this.sampleStreamWrappers) {
            sampleStreamWrapper.release();
        }
        this.callback = null;
        this.eventDispatcher.mediaPeriodReleased();
    }

    public void prepare(MediaPeriod.Callback callback, long positionUs) {
        this.callback = callback;
        this.playlistTracker.addListener(this);
        buildAndPrepareSampleStreamWrappers(positionUs);
    }

    public void maybeThrowPrepareError() throws IOException {
        for (HlsSampleStreamWrapper sampleStreamWrapper : this.sampleStreamWrappers) {
            sampleStreamWrapper.maybeThrowPrepareError();
        }
    }

    public TrackGroupArray getTrackGroups() {
        return this.trackGroups;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long selectTracks(com.google.android.exoplayer2.trackselection.TrackSelection[] r21, boolean[] r22, com.google.android.exoplayer2.source.SampleStream[] r23, boolean[] r24, long r25) {
        /*
        r20 = this;
        r0 = r20;
        r1 = r21;
        r2 = r23;
        r3 = r1.length;
        r3 = new int[r3];
        r4 = r1.length;
        r4 = new int[r4];
        r5 = 0;
    L_0x000d:
        r6 = r1.length;
        if (r5 >= r6) goto L_0x0050;
    L_0x0010:
        r6 = r2[r5];
        r7 = -1;
        if (r6 != 0) goto L_0x0017;
    L_0x0015:
        r6 = -1;
        goto L_0x0025;
    L_0x0017:
        r6 = r0.streamWrapperIndices;
        r8 = r2[r5];
        r6 = r6.get(r8);
        r6 = (java.lang.Integer) r6;
        r6 = r6.intValue();
    L_0x0025:
        r3[r5] = r6;
        r4[r5] = r7;
        r6 = r1[r5];
        if (r6 == 0) goto L_0x004c;
    L_0x002d:
        r6 = r1[r5];
        r6 = r6.getTrackGroup();
        r8 = 0;
    L_0x0034:
        r9 = r0.sampleStreamWrappers;
        r10 = r9.length;
        if (r8 >= r10) goto L_0x004b;
    L_0x0039:
        r9 = r9[r8];
        r9 = r9.getTrackGroups();
        r9 = r9.indexOf(r6);
        if (r9 == r7) goto L_0x0048;
    L_0x0045:
        r4[r5] = r8;
        goto L_0x004d;
    L_0x0048:
        r8 = r8 + 1;
        goto L_0x0034;
    L_0x004b:
        goto L_0x004d;
    L_0x004d:
        r5 = r5 + 1;
        goto L_0x000d;
    L_0x0050:
        r5 = 0;
        r6 = r0.streamWrapperIndices;
        r6.clear();
        r6 = r1.length;
        r6 = new com.google.android.exoplayer2.source.SampleStream[r6];
        r7 = r1.length;
        r7 = new com.google.android.exoplayer2.source.SampleStream[r7];
        r8 = r1.length;
        r15 = new com.google.android.exoplayer2.trackselection.TrackSelection[r8];
        r8 = 0;
        r9 = r0.sampleStreamWrappers;
        r9 = r9.length;
        r13 = new com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper[r9];
        r9 = 0;
        r16 = r5;
        r14 = r8;
        r5 = r9;
    L_0x006a:
        r8 = r0.sampleStreamWrappers;
        r8 = r8.length;
        r12 = 0;
        if (r5 >= r8) goto L_0x0114;
    L_0x0070:
        r8 = 0;
    L_0x0071:
        r9 = r1.length;
        if (r8 >= r9) goto L_0x008b;
    L_0x0074:
        r9 = r3[r8];
        r10 = 0;
        if (r9 != r5) goto L_0x007c;
    L_0x0079:
        r9 = r2[r8];
        goto L_0x007d;
    L_0x007c:
        r9 = r10;
    L_0x007d:
        r7[r8] = r9;
        r9 = r4[r8];
        if (r9 != r5) goto L_0x0086;
    L_0x0083:
        r10 = r1[r8];
    L_0x0086:
        r15[r8] = r10;
        r8 = r8 + 1;
        goto L_0x0071;
    L_0x008b:
        r8 = r0.sampleStreamWrappers;
        r11 = r8[r5];
        r8 = r11;
        r9 = r15;
        r10 = r22;
        r2 = r11;
        r11 = r7;
        r12 = r24;
        r17 = r2;
        r18 = r13;
        r2 = r14;
        r13 = r25;
        r19 = r15;
        r15 = r16;
        r8 = r8.selectTracks(r9, r10, r11, r12, r13, r15);
        r9 = 0;
        r10 = 0;
    L_0x00a8:
        r11 = r1.length;
        r12 = 1;
        if (r10 >= r11) goto L_0x00dc;
    L_0x00ac:
        r11 = r4[r10];
        if (r11 != r5) goto L_0x00ca;
    L_0x00b0:
        r11 = r7[r10];
        if (r11 == 0) goto L_0x00b5;
    L_0x00b4:
        goto L_0x00b6;
    L_0x00b5:
        r12 = 0;
    L_0x00b6:
        com.google.android.exoplayer2.util.Assertions.checkState(r12);
        r11 = r7[r10];
        r6[r10] = r11;
        r9 = 1;
        r11 = r0.streamWrapperIndices;
        r12 = r7[r10];
        r13 = java.lang.Integer.valueOf(r5);
        r11.put(r12, r13);
        goto L_0x00d9;
    L_0x00ca:
        r11 = r3[r10];
        if (r11 != r5) goto L_0x00d8;
    L_0x00ce:
        r11 = r7[r10];
        if (r11 != 0) goto L_0x00d3;
    L_0x00d2:
        goto L_0x00d4;
    L_0x00d3:
        r12 = 0;
    L_0x00d4:
        com.google.android.exoplayer2.util.Assertions.checkState(r12);
        goto L_0x00d9;
    L_0x00d9:
        r10 = r10 + 1;
        goto L_0x00a8;
    L_0x00dc:
        if (r9 == 0) goto L_0x0107;
    L_0x00de:
        r18[r2] = r17;
        r14 = r2 + 1;
        if (r2 != 0) goto L_0x0100;
    L_0x00e4:
        r10 = r17;
        r10.setIsTimestampMaster(r12);
        if (r8 != 0) goto L_0x00f7;
    L_0x00eb:
        r2 = r0.enabledSampleStreamWrappers;
        r11 = r2.length;
        if (r11 == 0) goto L_0x00f7;
    L_0x00f0:
        r11 = 0;
        r2 = r2[r11];
        if (r10 == r2) goto L_0x00f6;
    L_0x00f5:
        goto L_0x00f7;
    L_0x00f6:
        goto L_0x010a;
    L_0x00f7:
        r2 = r0.timestampAdjusterProvider;
        r2.reset();
        r2 = 1;
        r16 = r2;
        goto L_0x010a;
    L_0x0100:
        r10 = r17;
        r11 = 0;
        r10.setIsTimestampMaster(r11);
        goto L_0x010a;
    L_0x0107:
        r10 = r17;
        r14 = r2;
    L_0x010a:
        r5 = r5 + 1;
        r13 = r18;
        r15 = r19;
        r2 = r23;
        goto L_0x006a;
    L_0x0114:
        r18 = r13;
        r2 = r14;
        r19 = r15;
        r11 = 0;
        r5 = r6.length;
        r8 = r23;
        java.lang.System.arraycopy(r6, r11, r8, r11, r5);
        r5 = r18;
        r9 = java.util.Arrays.copyOf(r5, r2);
        r9 = (com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper[]) r9;
        r0.enabledSampleStreamWrappers = r9;
        r9 = r0.compositeSequenceableLoaderFactory;
        r10 = r0.enabledSampleStreamWrappers;
        r9 = r9.createCompositeSequenceableLoader(r10);
        r0.compositeSequenceableLoader = r9;
        return r25;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.hls.HlsMediaPeriod.selectTracks(com.google.android.exoplayer2.trackselection.TrackSelection[], boolean[], com.google.android.exoplayer2.source.SampleStream[], boolean[], long):long");
    }

    public void discardBuffer(long positionUs, boolean toKeyframe) {
        for (HlsSampleStreamWrapper sampleStreamWrapper : this.enabledSampleStreamWrappers) {
            sampleStreamWrapper.discardBuffer(positionUs, toKeyframe);
        }
    }

    public void reevaluateBuffer(long positionUs) {
        this.compositeSequenceableLoader.reevaluateBuffer(positionUs);
    }

    public boolean continueLoading(long positionUs) {
        if (this.trackGroups != null) {
            return this.compositeSequenceableLoader.continueLoading(positionUs);
        }
        for (HlsSampleStreamWrapper wrapper : this.sampleStreamWrappers) {
            wrapper.continuePreparing();
        }
        return false;
    }

    public long getNextLoadPositionUs() {
        return this.compositeSequenceableLoader.getNextLoadPositionUs();
    }

    public long readDiscontinuity() {
        if (!this.notifiedReadingStarted) {
            this.eventDispatcher.readingStarted();
            this.notifiedReadingStarted = true;
        }
        return C0555C.TIME_UNSET;
    }

    public long getBufferedPositionUs() {
        return this.compositeSequenceableLoader.getBufferedPositionUs();
    }

    public long seekToUs(long positionUs) {
        HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr = this.enabledSampleStreamWrappers;
        if (hlsSampleStreamWrapperArr.length > 0) {
            boolean forceReset = hlsSampleStreamWrapperArr[0].seekToUs(positionUs, false);
            int i = 1;
            while (true) {
                HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr2 = this.enabledSampleStreamWrappers;
                if (i >= hlsSampleStreamWrapperArr2.length) {
                    break;
                }
                hlsSampleStreamWrapperArr2[i].seekToUs(positionUs, forceReset);
                i++;
            }
            if (forceReset) {
                this.timestampAdjusterProvider.reset();
            }
        }
        return positionUs;
    }

    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        return positionUs;
    }

    public void onPrepared() {
        int i = this.pendingPrepareCount - 1;
        this.pendingPrepareCount = i;
        if (i <= 0) {
            int totalTrackGroupCount = 0;
            for (HlsSampleStreamWrapper sampleStreamWrapper : this.sampleStreamWrappers) {
                totalTrackGroupCount += sampleStreamWrapper.getTrackGroups().length;
            }
            TrackGroup[] trackGroupArray = new TrackGroup[totalTrackGroupCount];
            int trackGroupIndex = 0;
            for (HlsSampleStreamWrapper sampleStreamWrapper2 : this.sampleStreamWrappers) {
                int wrapperTrackGroupCount = sampleStreamWrapper2.getTrackGroups().length;
                int j = 0;
                while (j < wrapperTrackGroupCount) {
                    int trackGroupIndex2 = trackGroupIndex + 1;
                    trackGroupArray[trackGroupIndex] = sampleStreamWrapper2.getTrackGroups().get(j);
                    j++;
                    trackGroupIndex = trackGroupIndex2;
                }
            }
            this.trackGroups = new TrackGroupArray(trackGroupArray);
            this.callback.onPrepared(this);
        }
    }

    public void onPlaylistRefreshRequired(HlsUrl url) {
        this.playlistTracker.refreshPlaylist(url);
    }

    public void onContinueLoadingRequested(HlsSampleStreamWrapper sampleStreamWrapper) {
        this.callback.onContinueLoadingRequested(this);
    }

    public void onPlaylistChanged() {
        this.callback.onContinueLoadingRequested(this);
    }

    public boolean onPlaylistError(HlsUrl url, long blacklistDurationMs) {
        boolean noBlacklistingFailure = true;
        for (HlsSampleStreamWrapper streamWrapper : this.sampleStreamWrappers) {
            noBlacklistingFailure &= streamWrapper.onPlaylistError(url, blacklistDurationMs);
        }
        this.callback.onContinueLoadingRequested(this);
        return noBlacklistingFailure;
    }

    private void buildAndPrepareSampleStreamWrappers(long positionUs) {
        HlsMasterPlaylist masterPlaylist = this.playlistTracker.getMasterPlaylist();
        List<HlsUrl> audioRenditions = masterPlaylist.audios;
        List<HlsUrl> subtitleRenditions = masterPlaylist.subtitles;
        int i = 1;
        int wrapperCount = (audioRenditions.size() + 1) + subtitleRenditions.size();
        this.sampleStreamWrappers = new HlsSampleStreamWrapper[wrapperCount];
        this.pendingPrepareCount = wrapperCount;
        buildAndPrepareMainSampleStreamWrapper(masterPlaylist, positionUs);
        int currentWrapperIndex = 1;
        int i2 = 0;
        while (i2 < audioRenditions.size()) {
            HlsUrl audioRendition = (HlsUrl) audioRenditions.get(i2);
            HlsUrl[] hlsUrlArr = new HlsUrl[i];
            hlsUrlArr[0] = audioRendition;
            HlsUrl audioRendition2 = audioRendition;
            HlsMasterPlaylist masterPlaylist2 = masterPlaylist;
            HlsSampleStreamWrapper sampleStreamWrapper = buildSampleStreamWrapper(1, hlsUrlArr, null, Collections.emptyList(), positionUs);
            int currentWrapperIndex2 = currentWrapperIndex + 1;
            r7.sampleStreamWrappers[currentWrapperIndex] = sampleStreamWrapper;
            Format renditionFormat = audioRendition2.format;
            if (!r7.allowChunklessPreparation || renditionFormat.codecs == null) {
                sampleStreamWrapper.continuePreparing();
            } else {
                TrackGroup[] trackGroupArr = new TrackGroup[1];
                trackGroupArr[0] = new TrackGroup(audioRendition2.format);
                sampleStreamWrapper.prepareWithMasterPlaylistInfo(new TrackGroupArray(trackGroupArr), 0, TrackGroupArray.EMPTY);
            }
            i2++;
            currentWrapperIndex = currentWrapperIndex2;
            masterPlaylist = masterPlaylist2;
            i = 1;
        }
        i = 0;
        while (i < subtitleRenditions.size()) {
            HlsUrl url = (HlsUrl) subtitleRenditions.get(i);
            sampleStreamWrapper = buildSampleStreamWrapper(3, new HlsUrl[]{url}, null, Collections.emptyList(), positionUs);
            currentWrapperIndex2 = currentWrapperIndex + 1;
            r7.sampleStreamWrappers[currentWrapperIndex] = sampleStreamWrapper;
            TrackGroup[] trackGroupArr2 = new TrackGroup[1];
            trackGroupArr2[0] = new TrackGroup(url.format);
            sampleStreamWrapper.prepareWithMasterPlaylistInfo(new TrackGroupArray(trackGroupArr2), 0, TrackGroupArray.EMPTY);
            i++;
            currentWrapperIndex = currentWrapperIndex2;
        }
        r7.enabledSampleStreamWrappers = r7.sampleStreamWrappers;
    }

    private void buildAndPrepareMainSampleStreamWrapper(HlsMasterPlaylist masterPlaylist, long positionUs) {
        List<HlsUrl> selectedVariants;
        HlsMediaPeriod hlsMediaPeriod = this;
        HlsMasterPlaylist hlsMasterPlaylist = masterPlaylist;
        List<HlsUrl> selectedVariants2 = new ArrayList(hlsMasterPlaylist.variants);
        ArrayList<HlsUrl> definiteVideoVariants = new ArrayList();
        ArrayList<HlsUrl> definiteAudioOnlyVariants = new ArrayList();
        for (int i = 0; i < selectedVariants2.size(); i++) {
            HlsUrl variant = (HlsUrl) selectedVariants2.get(i);
            Format format = variant.format;
            if (format.height <= 0) {
                if (Util.getCodecsOfType(format.codecs, 2) == null) {
                    if (Util.getCodecsOfType(format.codecs, 1) != null) {
                        definiteAudioOnlyVariants.add(variant);
                    }
                }
            }
            definiteVideoVariants.add(variant);
        }
        if (definiteVideoVariants.isEmpty()) {
            if (definiteAudioOnlyVariants.size() < selectedVariants2.size()) {
                selectedVariants2.removeAll(definiteAudioOnlyVariants);
            }
            selectedVariants = selectedVariants2;
        } else {
            selectedVariants = definiteVideoVariants;
        }
        Assertions.checkArgument(selectedVariants.isEmpty() ^ true);
        HlsUrl[] variants = (HlsUrl[]) selectedVariants.toArray(new HlsUrl[0]);
        String codecs = variants[0].format.codecs;
        HlsSampleStreamWrapper sampleStreamWrapper = buildSampleStreamWrapper(0, variants, hlsMasterPlaylist.muxedAudioFormat, hlsMasterPlaylist.muxedCaptionFormats, positionUs);
        hlsMediaPeriod.sampleStreamWrappers[0] = sampleStreamWrapper;
        if (!hlsMediaPeriod.allowChunklessPreparation || codecs == null) {
            sampleStreamWrapper.setIsTimestampMaster(true);
            sampleStreamWrapper.continuePreparing();
            return;
        }
        boolean variantsContainVideoCodecs = Util.getCodecsOfType(codecs, 2) != null;
        boolean variantsContainAudioCodecs = Util.getCodecsOfType(codecs, 1) != null;
        List<TrackGroup> muxedTrackGroups = new ArrayList();
        if (variantsContainVideoCodecs) {
            int i2;
            Format[] videoFormats = new Format[selectedVariants.size()];
            for (i2 = 0; i2 < videoFormats.length; i2++) {
                videoFormats[i2] = deriveVideoFormat(variants[i2].format);
            }
            muxedTrackGroups.add(new TrackGroup(videoFormats));
            if (variantsContainAudioCodecs) {
                if (hlsMasterPlaylist.muxedAudioFormat == null) {
                    if (!hlsMasterPlaylist.audios.isEmpty()) {
                        boolean z = variantsContainVideoCodecs;
                    }
                }
                Format[] formatArr = new Format[1];
                formatArr[0] = deriveAudioFormat(variants[0].format, hlsMasterPlaylist.muxedAudioFormat, false);
                muxedTrackGroups.add(new TrackGroup(formatArr));
            }
            variantsContainVideoCodecs = hlsMasterPlaylist.muxedCaptionFormats;
            if (variantsContainVideoCodecs) {
                for (i2 = 0; i2 < variantsContainVideoCodecs.size(); i2++) {
                    muxedTrackGroups.add(new TrackGroup((Format) variantsContainVideoCodecs.get(i2)));
                }
            }
        } else {
            if (variantsContainAudioCodecs) {
                Format[] audioFormats = new Format[selectedVariants.size()];
                for (int i3 = 0; i3 < audioFormats.length; i3++) {
                    audioFormats[i3] = deriveAudioFormat(variants[i3].format, hlsMasterPlaylist.muxedAudioFormat, true);
                }
                muxedTrackGroups.add(new TrackGroup(audioFormats));
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unexpected codecs attribute: ");
                stringBuilder.append(codecs);
                throw new IllegalArgumentException(stringBuilder.toString());
            }
        }
        muxedTrackGroups.add(new TrackGroup(Format.createSampleFormat("ID3", MimeTypes.APPLICATION_ID3, null, -1, null)));
        sampleStreamWrapper.prepareWithMasterPlaylistInfo(new TrackGroupArray((TrackGroup[]) muxedTrackGroups.toArray(new TrackGroup[0])), 0, new TrackGroupArray(id3TrackGroup));
    }

    private HlsSampleStreamWrapper buildSampleStreamWrapper(int trackType, HlsUrl[] variants, Format muxedAudioFormat, List<Format> muxedCaptionFormats, long positionUs) {
        return new HlsSampleStreamWrapper(trackType, this, new HlsChunkSource(this.extractorFactory, this.playlistTracker, variants, this.dataSourceFactory, this.mediaTransferListener, this.timestampAdjusterProvider, muxedCaptionFormats), this.allocator, positionUs, muxedAudioFormat, this.loadErrorHandlingPolicy, this.eventDispatcher);
    }

    private static Format deriveVideoFormat(Format variantFormat) {
        String codecs = Util.getCodecsOfType(variantFormat.codecs, 2);
        return Format.createVideoContainerFormat(variantFormat.id, variantFormat.label, variantFormat.containerMimeType, MimeTypes.getMediaMimeType(codecs), codecs, variantFormat.bitrate, variantFormat.width, variantFormat.height, variantFormat.frameRate, null, variantFormat.selectionFlags);
    }

    private static Format deriveAudioFormat(Format variantFormat, Format mediaTagFormat, boolean isPrimaryTrackInVariant) {
        String codecs;
        Format format = variantFormat;
        Format format2 = mediaTagFormat;
        int channelCount = -1;
        int selectionFlags = 0;
        String language = null;
        String label = null;
        if (format2 != null) {
            codecs = format2.codecs;
            channelCount = format2.channelCount;
            selectionFlags = format2.selectionFlags;
            language = format2.language;
            label = format2.label;
        } else {
            codecs = Util.getCodecsOfType(format.codecs, 1);
            if (isPrimaryTrackInVariant) {
                channelCount = format.channelCount;
                selectionFlags = format.selectionFlags;
                language = format.label;
                label = format.label;
            }
        }
        return Format.createAudioContainerFormat(format.id, label, format.containerMimeType, MimeTypes.getMediaMimeType(codecs), codecs, isPrimaryTrackInVariant ? format.bitrate : -1, channelCount, -1, null, selectionFlags, language);
    }
}
