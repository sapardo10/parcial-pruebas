package com.google.android.exoplayer2.mediacodec;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException;
import java.util.Collections;
import java.util.List;

public interface MediaCodecSelector {
    public static final MediaCodecSelector DEFAULT = new C09791();
    public static final MediaCodecSelector DEFAULT_WITH_FALLBACK = new C09802();

    /* renamed from: com.google.android.exoplayer2.mediacodec.MediaCodecSelector$1 */
    static class C09791 implements MediaCodecSelector {
        C09791() {
        }

        public List<MediaCodecInfo> getDecoderInfos(String mimeType, boolean requiresSecureDecoder) throws DecoderQueryException {
            List<MediaCodecInfo> decoderInfos = MediaCodecUtil.getDecoderInfos(mimeType, requiresSecureDecoder);
            if (decoderInfos.isEmpty()) {
                return Collections.emptyList();
            }
            return Collections.singletonList(decoderInfos.get(0));
        }

        @Nullable
        public MediaCodecInfo getPassthroughDecoderInfo() throws DecoderQueryException {
            return MediaCodecUtil.getPassthroughDecoderInfo();
        }
    }

    /* renamed from: com.google.android.exoplayer2.mediacodec.MediaCodecSelector$2 */
    static class C09802 implements MediaCodecSelector {
        C09802() {
        }

        public List<MediaCodecInfo> getDecoderInfos(String mimeType, boolean requiresSecureDecoder) throws DecoderQueryException {
            return MediaCodecUtil.getDecoderInfos(mimeType, requiresSecureDecoder);
        }

        @Nullable
        public MediaCodecInfo getPassthroughDecoderInfo() throws DecoderQueryException {
            return MediaCodecUtil.getPassthroughDecoderInfo();
        }
    }

    List<MediaCodecInfo> getDecoderInfos(String str, boolean z) throws DecoderQueryException;

    @Nullable
    MediaCodecInfo getPassthroughDecoderInfo() throws DecoderQueryException;
}
