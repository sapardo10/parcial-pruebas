package kotlin.text;

import com.google.android.exoplayer2.C0555C;
import kotlin.Metadata;
import kotlin.SinceKotlin;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000&\n\u0000\n\u0002\u0010\u0005\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\n\n\u0002\b\u0003\u001a\u0013\u0010\u0000\u001a\u0004\u0018\u00010\u0001*\u00020\u0002H\u0007¢\u0006\u0002\u0010\u0003\u001a\u001b\u0010\u0000\u001a\u0004\u0018\u00010\u0001*\u00020\u00022\u0006\u0010\u0004\u001a\u00020\u0005H\u0007¢\u0006\u0002\u0010\u0006\u001a\u0013\u0010\u0007\u001a\u0004\u0018\u00010\u0005*\u00020\u0002H\u0007¢\u0006\u0002\u0010\b\u001a\u001b\u0010\u0007\u001a\u0004\u0018\u00010\u0005*\u00020\u00022\u0006\u0010\u0004\u001a\u00020\u0005H\u0007¢\u0006\u0002\u0010\t\u001a\u0013\u0010\n\u001a\u0004\u0018\u00010\u000b*\u00020\u0002H\u0007¢\u0006\u0002\u0010\f\u001a\u001b\u0010\n\u001a\u0004\u0018\u00010\u000b*\u00020\u00022\u0006\u0010\u0004\u001a\u00020\u0005H\u0007¢\u0006\u0002\u0010\r\u001a\u0013\u0010\u000e\u001a\u0004\u0018\u00010\u000f*\u00020\u0002H\u0007¢\u0006\u0002\u0010\u0010\u001a\u001b\u0010\u000e\u001a\u0004\u0018\u00010\u000f*\u00020\u00022\u0006\u0010\u0004\u001a\u00020\u0005H\u0007¢\u0006\u0002\u0010\u0011¨\u0006\u0012"}, d2 = {"toByteOrNull", "", "", "(Ljava/lang/String;)Ljava/lang/Byte;", "radix", "", "(Ljava/lang/String;I)Ljava/lang/Byte;", "toIntOrNull", "(Ljava/lang/String;)Ljava/lang/Integer;", "(Ljava/lang/String;I)Ljava/lang/Integer;", "toLongOrNull", "", "(Ljava/lang/String;)Ljava/lang/Long;", "(Ljava/lang/String;I)Ljava/lang/Long;", "toShortOrNull", "", "(Ljava/lang/String;)Ljava/lang/Short;", "(Ljava/lang/String;I)Ljava/lang/Short;", "kotlin-stdlib"}, k = 5, mv = {1, 1, 10}, xi = 1, xs = "kotlin/text/StringsKt")
/* compiled from: StringNumberConversions.kt */
class StringsKt__StringNumberConversionsKt extends StringsKt__StringNumberConversionsJVMKt {
    @SinceKotlin(version = "1.1")
    @Nullable
    public static final Byte toByteOrNull(@NotNull String $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return StringsKt.toByteOrNull($receiver, 10);
    }

    @SinceKotlin(version = "1.1")
    @Nullable
    public static final Byte toByteOrNull(@NotNull String $receiver, int radix) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        int intR = StringsKt.toIntOrNull($receiver, radix);
        if (intR == 0) {
            return null;
        }
        intR = intR.intValue();
        if (intR >= -128) {
            if (intR <= 127) {
                return Byte.valueOf((byte) intR);
            }
        }
        return null;
    }

    @SinceKotlin(version = "1.1")
    @Nullable
    public static final Short toShortOrNull(@NotNull String $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return StringsKt.toShortOrNull($receiver, 10);
    }

    @SinceKotlin(version = "1.1")
    @Nullable
    public static final Short toShortOrNull(@NotNull String $receiver, int radix) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        int intR = StringsKt.toIntOrNull($receiver, radix);
        if (intR == 0) {
            return null;
        }
        intR = intR.intValue();
        if (intR >= -32768) {
            if (intR <= 32767) {
                return Short.valueOf((short) intR);
            }
        }
        return null;
    }

    @SinceKotlin(version = "1.1")
    @Nullable
    public static final Integer toIntOrNull(@NotNull String $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return StringsKt.toIntOrNull($receiver, 10);
    }

    @SinceKotlin(version = "1.1")
    @Nullable
    public static final Integer toIntOrNull(@NotNull String $receiver, int radix) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        CharsKt.checkRadix(radix);
        int length = $receiver.length();
        if (length == 0) {
            return null;
        }
        int limit = 0;
        boolean isNegative = false;
        int start = 0;
        char firstChar = $receiver.charAt(0);
        if (firstChar >= '0') {
            start = 0;
            isNegative = false;
            limit = -2147483647;
        } else if (length == 1) {
            return null;
        } else {
            start = 1;
            if (firstChar == '-') {
                isNegative = true;
                limit = Integer.MIN_VALUE;
            } else if (firstChar != '+') {
                return null;
            } else {
                isNegative = false;
                limit = -2147483647;
            }
        }
        int limitBeforeMul = limit / radix;
        int result = 0;
        int i = length - 1;
        if (start <= i) {
            int i2 = start;
            while (true) {
                int digit = CharsKt.digitOf($receiver.charAt(i2), radix);
                if (digit >= 0 && result >= limitBeforeMul) {
                    result *= radix;
                    if (result >= limit + digit) {
                        result -= digit;
                        if (i2 == i) {
                            break;
                        }
                        i2++;
                    } else {
                        return null;
                    }
                }
                return null;
            }
        }
        return isNegative ? Integer.valueOf(result) : Integer.valueOf(-result);
    }

    @SinceKotlin(version = "1.1")
    @Nullable
    public static final Long toLongOrNull(@NotNull String $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return StringsKt.toLongOrNull($receiver, 10);
    }

    @SinceKotlin(version = "1.1")
    @Nullable
    public static final Long toLongOrNull(@NotNull String $receiver, int radix) {
        String str = $receiver;
        int i = radix;
        Intrinsics.checkParameterIsNotNull(str, "$receiver");
        CharsKt.checkRadix(radix);
        int length = $receiver.length();
        Long l = null;
        if (length == 0) {
            return null;
        }
        long limit;
        boolean isNegative = false;
        int start = 0;
        char firstChar = str.charAt(0);
        if (firstChar >= '0') {
            start = 0;
            isNegative = false;
            limit = C0555C.TIME_UNSET;
        } else if (length == 1) {
            return null;
        } else {
            start = 1;
            if (firstChar == '-') {
                isNegative = true;
                limit = Long.MIN_VALUE;
            } else if (firstChar != '+') {
                return null;
            } else {
                isNegative = false;
                limit = C0555C.TIME_UNSET;
            }
        }
        long limitBeforeMul = limit / ((long) i);
        long result = 0;
        int i2 = length - 1;
        if (start <= i2) {
            int i3 = start;
            while (true) {
                int digit = CharsKt.digitOf(str.charAt(i3), i);
                if (digit >= 0 && result >= limitBeforeMul) {
                    long limit2 = limit;
                    result *= (long) i;
                    if (result >= limit2 + ((long) digit)) {
                        l = null;
                        result -= (long) digit;
                        if (i3 == i2) {
                            break;
                        }
                        i3++;
                        limit = limit2;
                    } else {
                        return null;
                    }
                }
                return l;
            }
        }
        return isNegative ? Long.valueOf(result) : Long.valueOf(-result);
    }
}
