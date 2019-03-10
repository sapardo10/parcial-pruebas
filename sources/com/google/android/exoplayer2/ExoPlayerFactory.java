package com.google.android.exoplayer2;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.analytics.AnalyticsCollector$Factory;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter.Builder;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.Util;

public final class ExoPlayerFactory {
    @Nullable
    private static BandwidthMeter singletonBandwidthMeter;

    private ExoPlayerFactory() {
    }

    @Deprecated
    public static SimpleExoPlayer newSimpleInstance(Context context, TrackSelector trackSelector, LoadControl loadControl) {
        return newSimpleInstance(context, new DefaultRenderersFactory(context), trackSelector, loadControl);
    }

    @Deprecated
    public static SimpleExoPlayer newSimpleInstance(Context context, TrackSelector trackSelector, LoadControl loadControl, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager) {
        return newSimpleInstance(context, new DefaultRenderersFactory(context), trackSelector, loadControl, (DrmSessionManager) drmSessionManager);
    }

    @Deprecated
    public static SimpleExoPlayer newSimpleInstance(Context context, TrackSelector trackSelector, LoadControl loadControl, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, int extensionRendererMode) {
        return newSimpleInstance(context, new DefaultRenderersFactory(context, extensionRendererMode), trackSelector, loadControl, (DrmSessionManager) drmSessionManager);
    }

    @Deprecated
    public static SimpleExoPlayer newSimpleInstance(Context context, TrackSelector trackSelector, LoadControl loadControl, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, int extensionRendererMode, long allowedVideoJoiningTimeMs) {
        return newSimpleInstance(context, new DefaultRenderersFactory(context, extensionRendererMode, allowedVideoJoiningTimeMs), trackSelector, loadControl, (DrmSessionManager) drmSessionManager);
    }

    public static SimpleExoPlayer newSimpleInstance(Context context) {
        return newSimpleInstance(context, new DefaultTrackSelector());
    }

    public static SimpleExoPlayer newSimpleInstance(Context context, TrackSelector trackSelector) {
        return newSimpleInstance(context, new DefaultRenderersFactory(context), trackSelector);
    }

    @Deprecated
    public static SimpleExoPlayer newSimpleInstance(RenderersFactory renderersFactory, TrackSelector trackSelector) {
        return newSimpleInstance(null, renderersFactory, trackSelector, new DefaultLoadControl());
    }

    public static SimpleExoPlayer newSimpleInstance(Context context, RenderersFactory renderersFactory, TrackSelector trackSelector) {
        return newSimpleInstance(context, renderersFactory, trackSelector, new DefaultLoadControl());
    }

    public static SimpleExoPlayer newSimpleInstance(Context context, RenderersFactory renderersFactory, TrackSelector trackSelector, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager) {
        return newSimpleInstance(context, renderersFactory, trackSelector, new DefaultLoadControl(), (DrmSessionManager) drmSessionManager);
    }

    public static SimpleExoPlayer newSimpleInstance(Context context, RenderersFactory renderersFactory, TrackSelector trackSelector, LoadControl loadControl) {
        return newSimpleInstance(context, renderersFactory, trackSelector, loadControl, null, Util.getLooper());
    }

    public static SimpleExoPlayer newSimpleInstance(Context context, RenderersFactory renderersFactory, TrackSelector trackSelector, LoadControl loadControl, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager) {
        return newSimpleInstance(context, renderersFactory, trackSelector, loadControl, (DrmSessionManager) drmSessionManager, Util.getLooper());
    }

    public static SimpleExoPlayer newSimpleInstance(Context context, RenderersFactory renderersFactory, TrackSelector trackSelector, LoadControl loadControl, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, BandwidthMeter bandwidthMeter) {
        return newSimpleInstance(context, renderersFactory, trackSelector, loadControl, drmSessionManager, bandwidthMeter, new AnalyticsCollector$Factory(), Util.getLooper());
    }

    public static SimpleExoPlayer newSimpleInstance(Context context, RenderersFactory renderersFactory, TrackSelector trackSelector, LoadControl loadControl, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, AnalyticsCollector$Factory analyticsCollectorFactory) {
        return newSimpleInstance(context, renderersFactory, trackSelector, loadControl, drmSessionManager, analyticsCollectorFactory, Util.getLooper());
    }

    public static SimpleExoPlayer newSimpleInstance(Context context, RenderersFactory renderersFactory, TrackSelector trackSelector, LoadControl loadControl, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, Looper looper) {
        return newSimpleInstance(context, renderersFactory, trackSelector, loadControl, drmSessionManager, new AnalyticsCollector$Factory(), looper);
    }

    public static SimpleExoPlayer newSimpleInstance(Context context, RenderersFactory renderersFactory, TrackSelector trackSelector, LoadControl loadControl, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, AnalyticsCollector$Factory analyticsCollectorFactory, Looper looper) {
        return newSimpleInstance(context, renderersFactory, trackSelector, loadControl, drmSessionManager, getDefaultBandwidthMeter(), analyticsCollectorFactory, looper);
    }

    public static SimpleExoPlayer newSimpleInstance(Context context, RenderersFactory renderersFactory, TrackSelector trackSelector, LoadControl loadControl, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, BandwidthMeter bandwidthMeter, AnalyticsCollector$Factory analyticsCollectorFactory, Looper looper) {
        return new SimpleExoPlayer(context, renderersFactory, trackSelector, loadControl, drmSessionManager, bandwidthMeter, analyticsCollectorFactory, looper);
    }

    public static ExoPlayer newInstance(Renderer[] renderers, TrackSelector trackSelector) {
        return newInstance(renderers, trackSelector, new DefaultLoadControl());
    }

    public static ExoPlayer newInstance(Renderer[] renderers, TrackSelector trackSelector, LoadControl loadControl) {
        return newInstance(renderers, trackSelector, loadControl, Util.getLooper());
    }

    public static ExoPlayer newInstance(Renderer[] renderers, TrackSelector trackSelector, LoadControl loadControl, Looper looper) {
        return newInstance(renderers, trackSelector, loadControl, getDefaultBandwidthMeter(), looper);
    }

    public static ExoPlayer newInstance(Renderer[] renderers, TrackSelector trackSelector, LoadControl loadControl, BandwidthMeter bandwidthMeter, Looper looper) {
        return new ExoPlayerImpl(renderers, trackSelector, loadControl, bandwidthMeter, Clock.DEFAULT, looper);
    }

    private static synchronized BandwidthMeter getDefaultBandwidthMeter() {
        BandwidthMeter bandwidthMeter;
        synchronized (ExoPlayerFactory.class) {
            if (singletonBandwidthMeter == null) {
                singletonBandwidthMeter = new Builder().build();
            }
            bandwidthMeter = singletonBandwidthMeter;
        }
        return bandwidthMeter;
    }
}
