package com.google.android.exoplayer2.extractor.mp4;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.extractor.TrackOutput.CryptoData;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;

public final class TrackEncryptionBox {
    private static final String TAG = "TrackEncryptionBox";
    public final CryptoData cryptoData;
    public final byte[] defaultInitializationVector;
    public final boolean isEncrypted;
    public final int perSampleIvSize;
    @Nullable
    public final String schemeType;

    public TrackEncryptionBox(boolean isEncrypted, @Nullable String schemeType, int perSampleIvSize, byte[] keyId, int defaultEncryptedBlocks, int defaultClearBlocks, @Nullable byte[] defaultInitializationVector) {
        int i = 1;
        int i2 = perSampleIvSize == 0 ? 1 : 0;
        if (defaultInitializationVector != null) {
            i = 0;
        }
        Assertions.checkArgument(i ^ i2);
        this.isEncrypted = isEncrypted;
        this.schemeType = schemeType;
        this.perSampleIvSize = perSampleIvSize;
        this.defaultInitializationVector = defaultInitializationVector;
        this.cryptoData = new CryptoData(schemeToCryptoMode(schemeType), keyId, defaultEncryptedBlocks, defaultClearBlocks);
    }

    private static int schemeToCryptoMode(@Nullable String schemeType) {
        if (schemeType == null) {
            return 1;
        }
        String str;
        StringBuilder stringBuilder;
        Object obj = -1;
        int hashCode = schemeType.hashCode();
        if (hashCode != 3046605) {
            if (hashCode != 3046671) {
                if (hashCode != 3049879) {
                    if (hashCode == 3049895 && schemeType.equals(C0555C.CENC_TYPE_cens)) {
                        obj = 1;
                        switch (obj) {
                            case null:
                            case 1:
                                return 1;
                            case 2:
                            case 3:
                                return 2;
                            default:
                                str = TAG;
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("Unsupported protection scheme type '");
                                stringBuilder.append(schemeType);
                                stringBuilder.append("'. Assuming AES-CTR crypto mode.");
                                Log.m10w(str, stringBuilder.toString());
                                return 1;
                        }
                    }
                } else if (schemeType.equals(C0555C.CENC_TYPE_cenc)) {
                    obj = null;
                    switch (obj) {
                        case null:
                        case 1:
                            return 1;
                        case 2:
                        case 3:
                            return 2;
                        default:
                            str = TAG;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Unsupported protection scheme type '");
                            stringBuilder.append(schemeType);
                            stringBuilder.append("'. Assuming AES-CTR crypto mode.");
                            Log.m10w(str, stringBuilder.toString());
                            return 1;
                    }
                }
            } else if (schemeType.equals(C0555C.CENC_TYPE_cbcs)) {
                obj = 3;
                switch (obj) {
                    case null:
                    case 1:
                        return 1;
                    case 2:
                    case 3:
                        return 2;
                    default:
                        str = TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Unsupported protection scheme type '");
                        stringBuilder.append(schemeType);
                        stringBuilder.append("'. Assuming AES-CTR crypto mode.");
                        Log.m10w(str, stringBuilder.toString());
                        return 1;
                }
            }
        } else if (schemeType.equals(C0555C.CENC_TYPE_cbc1)) {
            obj = 2;
            switch (obj) {
                case null:
                case 1:
                    return 1;
                case 2:
                case 3:
                    return 2;
                default:
                    str = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Unsupported protection scheme type '");
                    stringBuilder.append(schemeType);
                    stringBuilder.append("'. Assuming AES-CTR crypto mode.");
                    Log.m10w(str, stringBuilder.toString());
                    return 1;
            }
        }
        switch (obj) {
            case null:
            case 1:
                return 1;
            case 2:
            case 3:
                return 2;
            default:
                str = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append("Unsupported protection scheme type '");
                stringBuilder.append(schemeType);
                stringBuilder.append("'. Assuming AES-CTR crypto mode.");
                Log.m10w(str, stringBuilder.toString());
                return 1;
        }
    }
}
