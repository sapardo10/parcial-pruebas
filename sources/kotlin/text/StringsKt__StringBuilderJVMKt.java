package kotlin.text;

import kotlin.Metadata;
import kotlin.internal.InlineOnly;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000R\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\f\n\u0002\u0010\r\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000b\n\u0002\u0010\u0005\n\u0002\u0010\u0019\n\u0002\u0010\u0006\n\u0002\u0010\u0007\n\u0002\u0010\b\n\u0002\u0010\t\n\u0002\u0010\n\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\u001a\u0012\u0010\u0000\u001a\u00060\u0001j\u0002`\u0002*\u00060\u0001j\u0002`\u0002\u001a\u001d\u0010\u0000\u001a\u00060\u0001j\u0002`\u0002*\u00060\u0001j\u0002`\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\b\u001a\u001f\u0010\u0000\u001a\u00060\u0001j\u0002`\u0002*\u00060\u0001j\u0002`\u00022\b\u0010\u0003\u001a\u0004\u0018\u00010\u0005H\b\u001a\u0012\u0010\u0000\u001a\u00060\u0006j\u0002`\u0007*\u00060\u0006j\u0002`\u0007\u001a\u001f\u0010\u0000\u001a\u00060\u0006j\u0002`\u0007*\u00060\u0006j\u0002`\u00072\b\u0010\u0003\u001a\u0004\u0018\u00010\bH\b\u001a\u001f\u0010\u0000\u001a\u00060\u0006j\u0002`\u0007*\u00060\u0006j\u0002`\u00072\b\u0010\u0003\u001a\u0004\u0018\u00010\tH\b\u001a\u001d\u0010\u0000\u001a\u00060\u0006j\u0002`\u0007*\u00060\u0006j\u0002`\u00072\u0006\u0010\u0003\u001a\u00020\nH\b\u001a\u001d\u0010\u0000\u001a\u00060\u0006j\u0002`\u0007*\u00060\u0006j\u0002`\u00072\u0006\u0010\u0003\u001a\u00020\u000bH\b\u001a\u001d\u0010\u0000\u001a\u00060\u0006j\u0002`\u0007*\u00060\u0006j\u0002`\u00072\u0006\u0010\u0003\u001a\u00020\u0004H\b\u001a\u001d\u0010\u0000\u001a\u00060\u0006j\u0002`\u0007*\u00060\u0006j\u0002`\u00072\u0006\u0010\u0003\u001a\u00020\fH\b\u001a\u001f\u0010\u0000\u001a\u00060\u0006j\u0002`\u0007*\u00060\u0006j\u0002`\u00072\b\u0010\u0003\u001a\u0004\u0018\u00010\u0005H\b\u001a\u001d\u0010\u0000\u001a\u00060\u0006j\u0002`\u0007*\u00060\u0006j\u0002`\u00072\u0006\u0010\u0003\u001a\u00020\rH\b\u001a\u001d\u0010\u0000\u001a\u00060\u0006j\u0002`\u0007*\u00060\u0006j\u0002`\u00072\u0006\u0010\u0003\u001a\u00020\u000eH\b\u001a\u001d\u0010\u0000\u001a\u00060\u0006j\u0002`\u0007*\u00060\u0006j\u0002`\u00072\u0006\u0010\u0003\u001a\u00020\u000fH\b\u001a\u001d\u0010\u0000\u001a\u00060\u0006j\u0002`\u0007*\u00060\u0006j\u0002`\u00072\u0006\u0010\u0003\u001a\u00020\u0010H\b\u001a\u001d\u0010\u0000\u001a\u00060\u0006j\u0002`\u0007*\u00060\u0006j\u0002`\u00072\u0006\u0010\u0003\u001a\u00020\u0011H\b\u001a\u001f\u0010\u0000\u001a\u00060\u0006j\u0002`\u0007*\u00060\u0006j\u0002`\u00072\b\u0010\u0003\u001a\u0004\u0018\u00010\u0012H\b\u001a%\u0010\u0000\u001a\u00060\u0006j\u0002`\u0007*\u00060\u0006j\u0002`\u00072\u000e\u0010\u0003\u001a\n\u0018\u00010\u0006j\u0004\u0018\u0001`\u0007H\b\u001a!\u0010\u0013\u001a\u00020\u0014*\u00060\u0006j\u0002`\u00072\u0006\u0010\u0015\u001a\u00020\u000f2\u0006\u0010\u0003\u001a\u00020\u0004H\n¨\u0006\u0016"}, d2 = {"appendln", "Ljava/lang/Appendable;", "Lkotlin/text/Appendable;", "value", "", "", "Ljava/lang/StringBuilder;", "Lkotlin/text/StringBuilder;", "Ljava/lang/StringBuffer;", "", "", "", "", "", "", "", "", "", "", "set", "", "index", "kotlin-stdlib"}, k = 5, mv = {1, 1, 10}, xi = 1, xs = "kotlin/text/StringsKt")
/* compiled from: StringBuilderJVM.kt */
class StringsKt__StringBuilderJVMKt extends StringsKt__RegexExtensionsKt {
    @InlineOnly
    private static final void set(@NotNull StringBuilder $receiver, int index, char value) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        $receiver.setCharAt(index, value);
    }

    @NotNull
    public static final Appendable appendln(@NotNull Appendable $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Appendable append = $receiver.append(SystemProperties.LINE_SEPARATOR);
        Intrinsics.checkExpressionValueIsNotNull(append, "append(SystemProperties.LINE_SEPARATOR)");
        return append;
    }

    @InlineOnly
    private static final Appendable appendln(@NotNull Appendable $receiver, CharSequence value) {
        Appendable append = $receiver.append(value);
        Intrinsics.checkExpressionValueIsNotNull(append, "append(value)");
        return StringsKt.appendln(append);
    }

    @InlineOnly
    private static final Appendable appendln(@NotNull Appendable $receiver, char value) {
        Appendable append = $receiver.append(value);
        Intrinsics.checkExpressionValueIsNotNull(append, "append(value)");
        return StringsKt.appendln(append);
    }

    @NotNull
    public static final StringBuilder appendln(@NotNull StringBuilder $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        $receiver.append(SystemProperties.LINE_SEPARATOR);
        Intrinsics.checkExpressionValueIsNotNull($receiver, "append(SystemProperties.LINE_SEPARATOR)");
        return $receiver;
    }

    @InlineOnly
    private static final StringBuilder appendln(@NotNull StringBuilder $receiver, StringBuffer value) {
        $receiver.append(value);
        Intrinsics.checkExpressionValueIsNotNull($receiver, "append(value)");
        return StringsKt.appendln($receiver);
    }

    @InlineOnly
    private static final StringBuilder appendln(@NotNull StringBuilder $receiver, CharSequence value) {
        $receiver.append(value);
        Intrinsics.checkExpressionValueIsNotNull($receiver, "append(value)");
        return StringsKt.appendln($receiver);
    }

    @InlineOnly
    private static final StringBuilder appendln(@NotNull StringBuilder $receiver, String value) {
        $receiver.append(value);
        Intrinsics.checkExpressionValueIsNotNull($receiver, "append(value)");
        return StringsKt.appendln($receiver);
    }

    @InlineOnly
    private static final StringBuilder appendln(@NotNull StringBuilder $receiver, Object value) {
        $receiver.append(value);
        Intrinsics.checkExpressionValueIsNotNull($receiver, "append(value)");
        return StringsKt.appendln($receiver);
    }

    @InlineOnly
    private static final StringBuilder appendln(@NotNull StringBuilder $receiver, StringBuilder value) {
        $receiver.append(value);
        Intrinsics.checkExpressionValueIsNotNull($receiver, "append(value)");
        return StringsKt.appendln($receiver);
    }

    @InlineOnly
    private static final StringBuilder appendln(@NotNull StringBuilder $receiver, char[] value) {
        $receiver.append(value);
        Intrinsics.checkExpressionValueIsNotNull($receiver, "append(value)");
        return StringsKt.appendln($receiver);
    }

    @InlineOnly
    private static final StringBuilder appendln(@NotNull StringBuilder $receiver, char value) {
        $receiver.append(value);
        Intrinsics.checkExpressionValueIsNotNull($receiver, "append(value)");
        return StringsKt.appendln($receiver);
    }

    @InlineOnly
    private static final StringBuilder appendln(@NotNull StringBuilder $receiver, boolean value) {
        $receiver.append(value);
        Intrinsics.checkExpressionValueIsNotNull($receiver, "append(value)");
        return StringsKt.appendln($receiver);
    }

    @InlineOnly
    private static final StringBuilder appendln(@NotNull StringBuilder $receiver, int value) {
        $receiver.append(value);
        Intrinsics.checkExpressionValueIsNotNull($receiver, "append(value)");
        return StringsKt.appendln($receiver);
    }

    @InlineOnly
    private static final StringBuilder appendln(@NotNull StringBuilder $receiver, short value) {
        $receiver.append(value);
        Intrinsics.checkExpressionValueIsNotNull($receiver, "append(value.toInt())");
        return StringsKt.appendln($receiver);
    }

    @InlineOnly
    private static final StringBuilder appendln(@NotNull StringBuilder $receiver, byte value) {
        $receiver.append(value);
        Intrinsics.checkExpressionValueIsNotNull($receiver, "append(value.toInt())");
        return StringsKt.appendln($receiver);
    }

    @InlineOnly
    private static final StringBuilder appendln(@NotNull StringBuilder $receiver, long value) {
        $receiver.append(value);
        Intrinsics.checkExpressionValueIsNotNull($receiver, "append(value)");
        return StringsKt.appendln($receiver);
    }

    @InlineOnly
    private static final StringBuilder appendln(@NotNull StringBuilder $receiver, float value) {
        $receiver.append(value);
        Intrinsics.checkExpressionValueIsNotNull($receiver, "append(value)");
        return StringsKt.appendln($receiver);
    }

    @InlineOnly
    private static final StringBuilder appendln(@NotNull StringBuilder $receiver, double value) {
        $receiver.append(value);
        Intrinsics.checkExpressionValueIsNotNull($receiver, "append(value)");
        return StringsKt.appendln($receiver);
    }
}
