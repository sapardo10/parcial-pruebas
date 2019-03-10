package com.google.android.exoplayer2.upstream.cache;

import com.google.android.exoplayer2.upstream.DataSpec;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$u97poD-IIwob7OPYcVJkh9jokx0 implements CacheKeyFactory {
    public static final /* synthetic */ -$$Lambda$u97poD-IIwob7OPYcVJkh9jokx0 INSTANCE = new -$$Lambda$u97poD-IIwob7OPYcVJkh9jokx0();

    private /* synthetic */ -$$Lambda$u97poD-IIwob7OPYcVJkh9jokx0() {
    }

    public final String buildCacheKey(DataSpec dataSpec) {
        return CacheUtil.getKey(dataSpec);
    }
}
