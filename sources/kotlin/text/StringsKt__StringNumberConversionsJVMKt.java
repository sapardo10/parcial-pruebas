package kotlin.text;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import kotlin.Metadata;
import kotlin.SinceKotlin;
import kotlin.internal.InlineOnly;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000X\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0005\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0010\t\n\u0000\n\u0002\u0010\n\n\u0002\b\u0002\u001a4\u0010\u0000\u001a\u0004\u0018\u0001H\u0001\"\u0004\b\u0000\u0010\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u0002H\u00010\u0005H\b¢\u0006\u0004\b\u0006\u0010\u0007\u001a\r\u0010\b\u001a\u00020\t*\u00020\u0003H\b\u001a\u0015\u0010\b\u001a\u00020\t*\u00020\u00032\u0006\u0010\n\u001a\u00020\u000bH\b\u001a\u000e\u0010\f\u001a\u0004\u0018\u00010\t*\u00020\u0003H\u0007\u001a\u0016\u0010\f\u001a\u0004\u0018\u00010\t*\u00020\u00032\u0006\u0010\n\u001a\u00020\u000bH\u0007\u001a\r\u0010\r\u001a\u00020\u000e*\u00020\u0003H\b\u001a\u0015\u0010\r\u001a\u00020\u000e*\u00020\u00032\u0006\u0010\u000f\u001a\u00020\u0010H\b\u001a\u000e\u0010\u0011\u001a\u0004\u0018\u00010\u000e*\u00020\u0003H\u0007\u001a\u0016\u0010\u0011\u001a\u0004\u0018\u00010\u000e*\u00020\u00032\u0006\u0010\u000f\u001a\u00020\u0010H\u0007\u001a\r\u0010\u0012\u001a\u00020\u0013*\u00020\u0003H\b\u001a\r\u0010\u0014\u001a\u00020\u0015*\u00020\u0003H\b\u001a\u0015\u0010\u0014\u001a\u00020\u0015*\u00020\u00032\u0006\u0010\u000f\u001a\u00020\u0010H\b\u001a\r\u0010\u0016\u001a\u00020\u0017*\u00020\u0003H\b\u001a\u0013\u0010\u0018\u001a\u0004\u0018\u00010\u0017*\u00020\u0003H\u0007¢\u0006\u0002\u0010\u0019\u001a\r\u0010\u001a\u001a\u00020\u001b*\u00020\u0003H\b\u001a\u0013\u0010\u001c\u001a\u0004\u0018\u00010\u001b*\u00020\u0003H\u0007¢\u0006\u0002\u0010\u001d\u001a\r\u0010\u001e\u001a\u00020\u0010*\u00020\u0003H\b\u001a\u0015\u0010\u001e\u001a\u00020\u0010*\u00020\u00032\u0006\u0010\u000f\u001a\u00020\u0010H\b\u001a\r\u0010\u001f\u001a\u00020 *\u00020\u0003H\b\u001a\u0015\u0010\u001f\u001a\u00020 *\u00020\u00032\u0006\u0010\u000f\u001a\u00020\u0010H\b\u001a\r\u0010!\u001a\u00020\"*\u00020\u0003H\b\u001a\u0015\u0010!\u001a\u00020\"*\u00020\u00032\u0006\u0010\u000f\u001a\u00020\u0010H\b\u001a\u0015\u0010#\u001a\u00020\u0003*\u00020\u00152\u0006\u0010\u000f\u001a\u00020\u0010H\b\u001a\u0015\u0010#\u001a\u00020\u0003*\u00020\u00102\u0006\u0010\u000f\u001a\u00020\u0010H\b\u001a\u0015\u0010#\u001a\u00020\u0003*\u00020 2\u0006\u0010\u000f\u001a\u00020\u0010H\b\u001a\u0015\u0010#\u001a\u00020\u0003*\u00020\"2\u0006\u0010\u000f\u001a\u00020\u0010H\b¨\u0006$"}, d2 = {"screenFloatValue", "T", "str", "", "parse", "Lkotlin/Function1;", "screenFloatValue$StringsKt__StringNumberConversionsJVMKt", "(Ljava/lang/String;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "toBigDecimal", "Ljava/math/BigDecimal;", "mathContext", "Ljava/math/MathContext;", "toBigDecimalOrNull", "toBigInteger", "Ljava/math/BigInteger;", "radix", "", "toBigIntegerOrNull", "toBoolean", "", "toByte", "", "toDouble", "", "toDoubleOrNull", "(Ljava/lang/String;)Ljava/lang/Double;", "toFloat", "", "toFloatOrNull", "(Ljava/lang/String;)Ljava/lang/Float;", "toInt", "toLong", "", "toShort", "", "toString", "kotlin-stdlib"}, k = 5, mv = {1, 1, 10}, xi = 1, xs = "kotlin/text/StringsKt")
/* compiled from: StringNumberConversionsJVM.kt */
class StringsKt__StringNumberConversionsJVMKt extends StringsKt__StringBuilderKt {
    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final String toString(byte $receiver, int radix) {
        String num = Integer.toString($receiver, CharsKt.checkRadix(CharsKt.checkRadix(radix)));
        Intrinsics.checkExpressionValueIsNotNull(num, "java.lang.Integer.toStri…(this, checkRadix(radix))");
        return num;
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final String toString(short $receiver, int radix) {
        String num = Integer.toString($receiver, CharsKt.checkRadix(CharsKt.checkRadix(radix)));
        Intrinsics.checkExpressionValueIsNotNull(num, "java.lang.Integer.toStri…(this, checkRadix(radix))");
        return num;
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final String toString(int $receiver, int radix) {
        String num = Integer.toString($receiver, CharsKt.checkRadix(radix));
        Intrinsics.checkExpressionValueIsNotNull(num, "java.lang.Integer.toStri…(this, checkRadix(radix))");
        return num;
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final String toString(long $receiver, int radix) {
        String l = Long.toString($receiver, CharsKt.checkRadix(radix));
        Intrinsics.checkExpressionValueIsNotNull(l, "java.lang.Long.toString(this, checkRadix(radix))");
        return l;
    }

    @InlineOnly
    private static final boolean toBoolean(@NotNull String $receiver) {
        return Boolean.parseBoolean($receiver);
    }

    @InlineOnly
    private static final byte toByte(@NotNull String $receiver) {
        return Byte.parseByte($receiver);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final byte toByte(@NotNull String $receiver, int radix) {
        return Byte.parseByte($receiver, CharsKt.checkRadix(radix));
    }

    @InlineOnly
    private static final short toShort(@NotNull String $receiver) {
        return Short.parseShort($receiver);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final short toShort(@NotNull String $receiver, int radix) {
        return Short.parseShort($receiver, CharsKt.checkRadix(radix));
    }

    @InlineOnly
    private static final int toInt(@NotNull String $receiver) {
        return Integer.parseInt($receiver);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final int toInt(@NotNull String $receiver, int radix) {
        return Integer.parseInt($receiver, CharsKt.checkRadix(radix));
    }

    @InlineOnly
    private static final long toLong(@NotNull String $receiver) {
        return Long.parseLong($receiver);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final long toLong(@NotNull String $receiver, int radix) {
        return Long.parseLong($receiver, CharsKt.checkRadix(radix));
    }

    @InlineOnly
    private static final float toFloat(@NotNull String $receiver) {
        return Float.parseFloat($receiver);
    }

    @InlineOnly
    private static final double toDouble(@NotNull String $receiver) {
        return Double.parseDouble($receiver);
    }

    @SinceKotlin(version = "1.1")
    @Nullable
    public static final Float toFloatOrNull(@NotNull String $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        int $i$f$screenFloatValue$StringsKt__StringNumberConversionsJVMKt = 0;
        Float f = null;
        try {
            if (ScreenFloatValueRegEx.value.matches($receiver)) {
                f = Float.valueOf(Float.parseFloat($receiver));
            }
        } catch (NumberFormatException e) {
        }
        return f;
    }

    @SinceKotlin(version = "1.1")
    @Nullable
    public static final Double toDoubleOrNull(@NotNull String $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        int $i$f$screenFloatValue$StringsKt__StringNumberConversionsJVMKt = 0;
        Double d = null;
        try {
            if (ScreenFloatValueRegEx.value.matches($receiver)) {
                d = Double.valueOf(Double.parseDouble($receiver));
            }
        } catch (NumberFormatException e) {
        }
        return d;
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final BigInteger toBigInteger(@NotNull String $receiver) {
        return new BigInteger($receiver);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final BigInteger toBigInteger(@NotNull String $receiver, int radix) {
        return new BigInteger($receiver, CharsKt.checkRadix(radix));
    }

    @SinceKotlin(version = "1.2")
    @Nullable
    public static final BigInteger toBigIntegerOrNull(@NotNull String $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return StringsKt.toBigIntegerOrNull($receiver, 10);
    }

    @SinceKotlin(version = "1.2")
    @Nullable
    public static final BigInteger toBigIntegerOrNull(@NotNull String $receiver, int radix) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        CharsKt.checkRadix(radix);
        int length = $receiver.length();
        int start = 0;
        switch (length) {
            case 0:
                return null;
            case 1:
                if (CharsKt.digitOf($receiver.charAt(0), radix) >= 0) {
                    break;
                }
                return null;
            default:
                if ($receiver.charAt(0) == '-') {
                    start = 1;
                }
                for (int index = start; index < length; index++) {
                    if (CharsKt.digitOf($receiver.charAt(index), radix) < 0) {
                        return null;
                    }
                }
                break;
        }
        return new BigInteger($receiver, CharsKt.checkRadix(radix));
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final BigDecimal toBigDecimal(@NotNull String $receiver) {
        return new BigDecimal($receiver);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final BigDecimal toBigDecimal(@NotNull String $receiver, MathContext mathContext) {
        return new BigDecimal($receiver, mathContext);
    }

    @SinceKotlin(version = "1.2")
    @Nullable
    public static final BigDecimal toBigDecimalOrNull(@NotNull String $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        int $i$f$screenFloatValue$StringsKt__StringNumberConversionsJVMKt = 0;
        BigDecimal bigDecimal = null;
        try {
            if (ScreenFloatValueRegEx.value.matches($receiver)) {
                bigDecimal = new BigDecimal($receiver);
            }
        } catch (NumberFormatException e) {
        }
        return bigDecimal;
    }

    @SinceKotlin(version = "1.2")
    @Nullable
    public static final BigDecimal toBigDecimalOrNull(@NotNull String $receiver, @NotNull MathContext mathContext) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(mathContext, "mathContext");
        int $i$f$screenFloatValue$StringsKt__StringNumberConversionsJVMKt = 0;
        BigDecimal bigDecimal = null;
        try {
            if (ScreenFloatValueRegEx.value.matches($receiver)) {
                bigDecimal = new BigDecimal($receiver, mathContext);
            }
        } catch (NumberFormatException e) {
        }
        return bigDecimal;
    }

    private static final <T> T screenFloatValue$StringsKt__StringNumberConversionsJVMKt(String str, Function1<? super String, ? extends T> parse) {
        T t = null;
        try {
            if (ScreenFloatValueRegEx.value.matches(str)) {
                t = parse.invoke(str);
            }
        } catch (NumberFormatException e) {
        }
        return t;
    }
}
