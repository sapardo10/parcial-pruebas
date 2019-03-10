package com.google.android.exoplayer2.extractor.mkv;

import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MatroskaExtractor$jNXW0tyYIOPE6N2jicocV6rRvBs implements ExtractorsFactory {
    public static final /* synthetic */ -$$Lambda$MatroskaExtractor$jNXW0tyYIOPE6N2jicocV6rRvBs INSTANCE = new -$$Lambda$MatroskaExtractor$jNXW0tyYIOPE6N2jicocV6rRvBs();

    private /* synthetic */ -$$Lambda$MatroskaExtractor$jNXW0tyYIOPE6N2jicocV6rRvBs() {
    }

    public final Extractor[] createExtractors() {
        return new Extractor[]{new MatroskaExtractor()};
    }
}
