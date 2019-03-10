package com.google.android.exoplayer2.extractor.amr;

import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AmrExtractor$lVuGuaAcylUV-_XE4-hSR1hBylI implements ExtractorsFactory {
    public static final /* synthetic */ -$$Lambda$AmrExtractor$lVuGuaAcylUV-_XE4-hSR1hBylI INSTANCE = new -$$Lambda$AmrExtractor$lVuGuaAcylUV-_XE4-hSR1hBylI();

    private /* synthetic */ -$$Lambda$AmrExtractor$lVuGuaAcylUV-_XE4-hSR1hBylI() {
    }

    public final Extractor[] createExtractors() {
        return new Extractor[]{new AmrExtractor()};
    }
}
