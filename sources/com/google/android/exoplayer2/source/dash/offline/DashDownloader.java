package com.google.android.exoplayer2.source.dash.offline;

import android.net.Uri;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.extractor.ChunkIndex;
import com.google.android.exoplayer2.offline.DownloadException;
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper;
import com.google.android.exoplayer2.offline.SegmentDownloader;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.source.dash.DashSegmentIndex;
import com.google.android.exoplayer2.source.dash.DashUtil;
import com.google.android.exoplayer2.source.dash.DashWrappingSegmentIndex;
import com.google.android.exoplayer2.source.dash.manifest.AdaptationSet;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.Period;
import com.google.android.exoplayer2.source.dash.manifest.RangedUri;
import com.google.android.exoplayer2.source.dash.manifest.Representation;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class DashDownloader extends SegmentDownloader<DashManifest> {
    public DashDownloader(Uri manifestUri, List<StreamKey> streamKeys, DownloaderConstructorHelper constructorHelper) {
        super(manifestUri, streamKeys, constructorHelper);
    }

    protected DashManifest getManifest(DataSource dataSource, Uri uri) throws IOException {
        return DashUtil.loadManifest(dataSource, uri);
    }

    protected List<Segment> getSegments(DataSource dataSource, DashManifest manifest, boolean allowIncompleteList) throws InterruptedException, IOException {
        DashManifest dashManifest = manifest;
        ArrayList<Segment> segments = new ArrayList();
        for (int i = 0; i < manifest.getPeriodCount(); i++) {
            List<AdaptationSet> adaptationSets;
            Period period = dashManifest.getPeriod(i);
            long periodStartUs = C0555C.msToUs(period.startMs);
            long periodDurationUs = dashManifest.getPeriodDurationUs(i);
            List<AdaptationSet> adaptationSets2 = period.adaptationSets;
            int j = 0;
            while (j < adaptationSets2.size()) {
                int j2 = j;
                adaptationSets = adaptationSets2;
                addSegmentsForAdaptationSet(dataSource, (AdaptationSet) adaptationSets2.get(j), periodStartUs, periodDurationUs, allowIncompleteList, segments);
                j = j2 + 1;
                adaptationSets2 = adaptationSets;
            }
            adaptationSets = adaptationSets2;
        }
        return segments;
    }

    private static void addSegmentsForAdaptationSet(DataSource dataSource, AdaptationSet adaptationSet, long periodStartUs, long periodDurationUs, boolean allowIncompleteList, ArrayList<Segment> out) throws IOException, InterruptedException {
        IOException e;
        AdaptationSet adaptationSet2 = adaptationSet;
        long j = periodStartUs;
        ArrayList<Segment> arrayList = out;
        int i = 0;
        while (i < adaptationSet2.representations.size()) {
            Representation representation = (Representation) adaptationSet2.representations.get(i);
            try {
                DashSegmentIndex index = getSegmentIndex(dataSource, adaptationSet2.type, representation);
                if (index != null) {
                    int segmentCount = index.getSegmentCount(periodDurationUs);
                    if (segmentCount != -1) {
                        long lastSegmentNum;
                        String baseUrl = representation.baseUrl;
                        RangedUri initializationUri = representation.getInitializationUri();
                        if (initializationUri != null) {
                            addSegment(j, baseUrl, initializationUri, arrayList);
                        }
                        RangedUri indexUri = representation.getIndexUri();
                        if (indexUri != null) {
                            addSegment(j, baseUrl, indexUri, arrayList);
                        }
                        long firstSegmentNum = index.getFirstSegmentNum();
                        representation = (((long) segmentCount) + firstSegmentNum) - 1;
                        long j2 = firstSegmentNum;
                        while (j2 <= representation) {
                            lastSegmentNum = representation;
                            addSegment(j + index.getTimeUs(j2), baseUrl, index.getSegmentUrl(j2), arrayList);
                            j2++;
                            representation = lastSegmentNum;
                            adaptationSet2 = adaptationSet;
                        }
                        lastSegmentNum = representation;
                        i++;
                        adaptationSet2 = adaptationSet;
                    } else {
                        throw new DownloadException("Unbounded segment index");
                    }
                }
                try {
                    throw new DownloadException("Missing segment index");
                } catch (IOException e2) {
                    e = e2;
                }
            } catch (IOException e3) {
                e = e3;
                Representation representation2 = representation;
                if (allowIncompleteList) {
                    i++;
                    adaptationSet2 = adaptationSet;
                } else {
                    throw e;
                }
            }
        }
    }

    private static void addSegment(long startTimeUs, String baseUrl, RangedUri rangedUri, ArrayList<Segment> out) {
        out.add(new Segment(startTimeUs, new DataSpec(rangedUri.resolveUri(baseUrl), rangedUri.start, rangedUri.length, null)));
    }

    @Nullable
    private static DashSegmentIndex getSegmentIndex(DataSource dataSource, int trackType, Representation representation) throws IOException, InterruptedException {
        DashSegmentIndex index = representation.getIndex();
        if (index != null) {
            return index;
        }
        ChunkIndex seekMap = DashUtil.loadChunkIndex(dataSource, trackType, representation);
        return seekMap == null ? null : new DashWrappingSegmentIndex(seekMap, representation.presentationTimeOffsetUs);
    }
}
