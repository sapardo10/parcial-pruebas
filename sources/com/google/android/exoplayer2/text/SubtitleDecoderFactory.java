package com.google.android.exoplayer2.text;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.util.MimeTypes;

public interface SubtitleDecoderFactory {
    public static final SubtitleDecoderFactory DEFAULT = new C09851();

    /* renamed from: com.google.android.exoplayer2.text.SubtitleDecoderFactory$1 */
    static class C09851 implements SubtitleDecoderFactory {
        C09851() {
        }

        public boolean supportsFormat(Format format) {
            String mimeType = format.sampleMimeType;
            if (!MimeTypes.TEXT_VTT.equals(mimeType)) {
                if (!MimeTypes.TEXT_SSA.equals(mimeType)) {
                    if (!MimeTypes.APPLICATION_TTML.equals(mimeType)) {
                        if (!MimeTypes.APPLICATION_MP4VTT.equals(mimeType)) {
                            if (!MimeTypes.APPLICATION_SUBRIP.equals(mimeType)) {
                                if (!MimeTypes.APPLICATION_TX3G.equals(mimeType)) {
                                    if (!MimeTypes.APPLICATION_CEA608.equals(mimeType)) {
                                        if (!MimeTypes.APPLICATION_MP4CEA608.equals(mimeType)) {
                                            if (!MimeTypes.APPLICATION_CEA708.equals(mimeType)) {
                                                if (!MimeTypes.APPLICATION_DVBSUBS.equals(mimeType)) {
                                                    if (!MimeTypes.APPLICATION_PGS.equals(mimeType)) {
                                                        return false;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public com.google.android.exoplayer2.text.SubtitleDecoder createDecoder(com.google.android.exoplayer2.Format r4) {
            /*
            r3 = this;
            r0 = r4.sampleMimeType;
            r1 = r0.hashCode();
            switch(r1) {
                case -1351681404: goto L_0x0074;
                case -1248334819: goto L_0x0069;
                case -1026075066: goto L_0x005f;
                case -1004728940: goto L_0x0054;
                case 691401887: goto L_0x004a;
                case 822864842: goto L_0x003f;
                case 930165504: goto L_0x0035;
                case 1566015601: goto L_0x002b;
                case 1566016562: goto L_0x0020;
                case 1668750253: goto L_0x0016;
                case 1693976202: goto L_0x000b;
                default: goto L_0x0009;
            };
        L_0x0009:
            goto L_0x007f;
        L_0x000b:
            r1 = "application/ttml+xml";
            r0 = r0.equals(r1);
            if (r0 == 0) goto L_0x0009;
        L_0x0013:
            r0 = 3;
            goto L_0x0080;
        L_0x0016:
            r1 = "application/x-subrip";
            r0 = r0.equals(r1);
            if (r0 == 0) goto L_0x0009;
        L_0x001e:
            r0 = 4;
            goto L_0x0080;
        L_0x0020:
            r1 = "application/cea-708";
            r0 = r0.equals(r1);
            if (r0 == 0) goto L_0x0009;
        L_0x0028:
            r0 = 8;
            goto L_0x0080;
        L_0x002b:
            r1 = "application/cea-608";
            r0 = r0.equals(r1);
            if (r0 == 0) goto L_0x0009;
        L_0x0033:
            r0 = 6;
            goto L_0x0080;
        L_0x0035:
            r1 = "application/x-mp4-cea-608";
            r0 = r0.equals(r1);
            if (r0 == 0) goto L_0x0009;
        L_0x003d:
            r0 = 7;
            goto L_0x0080;
        L_0x003f:
            r1 = "text/x-ssa";
            r0 = r0.equals(r1);
            if (r0 == 0) goto L_0x0009;
        L_0x0048:
            r0 = 1;
            goto L_0x0080;
        L_0x004a:
            r1 = "application/x-quicktime-tx3g";
            r0 = r0.equals(r1);
            if (r0 == 0) goto L_0x0009;
        L_0x0052:
            r0 = 5;
            goto L_0x0080;
        L_0x0054:
            r1 = "text/vtt";
            r0 = r0.equals(r1);
            if (r0 == 0) goto L_0x0009;
        L_0x005d:
            r0 = 0;
            goto L_0x0080;
        L_0x005f:
            r1 = "application/x-mp4-vtt";
            r0 = r0.equals(r1);
            if (r0 == 0) goto L_0x0009;
        L_0x0067:
            r0 = 2;
            goto L_0x0080;
        L_0x0069:
            r1 = "application/pgs";
            r0 = r0.equals(r1);
            if (r0 == 0) goto L_0x0009;
        L_0x0071:
            r0 = 10;
            goto L_0x0080;
        L_0x0074:
            r1 = "application/dvbsubs";
            r0 = r0.equals(r1);
            if (r0 == 0) goto L_0x0009;
        L_0x007c:
            r0 = 9;
            goto L_0x0080;
        L_0x007f:
            r0 = -1;
        L_0x0080:
            switch(r0) {
                case 0: goto L_0x00cf;
                case 1: goto L_0x00c7;
                case 2: goto L_0x00c1;
                case 3: goto L_0x00bb;
                case 4: goto L_0x00b5;
                case 5: goto L_0x00ad;
                case 6: goto L_0x00a3;
                case 7: goto L_0x00a3;
                case 8: goto L_0x0099;
                case 9: goto L_0x0091;
                case 10: goto L_0x008b;
                default: goto L_0x0083;
            };
        L_0x0083:
            r0 = new java.lang.IllegalArgumentException;
            r1 = "Attempted to create decoder for unsupported format";
            r0.<init>(r1);
            throw r0;
        L_0x008b:
            r0 = new com.google.android.exoplayer2.text.pgs.PgsDecoder;
            r0.<init>();
            return r0;
        L_0x0091:
            r0 = new com.google.android.exoplayer2.text.dvb.DvbDecoder;
            r1 = r4.initializationData;
            r0.<init>(r1);
            return r0;
        L_0x0099:
            r0 = new com.google.android.exoplayer2.text.cea.Cea708Decoder;
            r1 = r4.accessibilityChannel;
            r2 = r4.initializationData;
            r0.<init>(r1, r2);
            return r0;
        L_0x00a3:
            r0 = new com.google.android.exoplayer2.text.cea.Cea608Decoder;
            r1 = r4.sampleMimeType;
            r2 = r4.accessibilityChannel;
            r0.<init>(r1, r2);
            return r0;
        L_0x00ad:
            r0 = new com.google.android.exoplayer2.text.tx3g.Tx3gDecoder;
            r1 = r4.initializationData;
            r0.<init>(r1);
            return r0;
        L_0x00b5:
            r0 = new com.google.android.exoplayer2.text.subrip.SubripDecoder;
            r0.<init>();
            return r0;
        L_0x00bb:
            r0 = new com.google.android.exoplayer2.text.ttml.TtmlDecoder;
            r0.<init>();
            return r0;
        L_0x00c1:
            r0 = new com.google.android.exoplayer2.text.webvtt.Mp4WebvttDecoder;
            r0.<init>();
            return r0;
        L_0x00c7:
            r0 = new com.google.android.exoplayer2.text.ssa.SsaDecoder;
            r1 = r4.initializationData;
            r0.<init>(r1);
            return r0;
        L_0x00cf:
            r0 = new com.google.android.exoplayer2.text.webvtt.WebvttDecoder;
            r0.<init>();
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.SubtitleDecoderFactory.1.createDecoder(com.google.android.exoplayer2.Format):com.google.android.exoplayer2.text.SubtitleDecoder");
        }
    }

    SubtitleDecoder createDecoder(Format format);

    boolean supportsFormat(Format format);
}
