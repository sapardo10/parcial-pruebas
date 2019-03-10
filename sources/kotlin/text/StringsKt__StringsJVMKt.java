package kotlin.text;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.collections.ArraysKt;
import kotlin.collections.IntIterator;
import kotlin.internal.InlineOnly;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.sequences.SequencesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000x\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0019\n\u0000\n\u0002\u0010\u0015\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\r\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u0000\n\u0002\b\b\n\u0002\u0010\f\n\u0002\b\u0011\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000e\u001a\u0011\u0010\u0006\u001a\u00020\u00022\u0006\u0010\u0007\u001a\u00020\bH\b\u001a\u0011\u0010\u0006\u001a\u00020\u00022\u0006\u0010\t\u001a\u00020\nH\b\u001a\u0011\u0010\u0006\u001a\u00020\u00022\u0006\u0010\u000b\u001a\u00020\fH\b\u001a\u0019\u0010\u0006\u001a\u00020\u00022\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\b\u001a!\u0010\u0006\u001a\u00020\u00022\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0010H\b\u001a)\u0010\u0006\u001a\u00020\u00022\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\r\u001a\u00020\u000eH\b\u001a\u0011\u0010\u0006\u001a\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u0013H\b\u001a!\u0010\u0006\u001a\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0010H\b\u001a!\u0010\u0006\u001a\u00020\u00022\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0010H\b\u001a\n\u0010\u0016\u001a\u00020\u0002*\u00020\u0002\u001a\u0015\u0010\u0017\u001a\u00020\u0010*\u00020\u00022\u0006\u0010\u0018\u001a\u00020\u0010H\b\u001a\u0015\u0010\u0019\u001a\u00020\u0010*\u00020\u00022\u0006\u0010\u0018\u001a\u00020\u0010H\b\u001a\u001d\u0010\u001a\u001a\u00020\u0010*\u00020\u00022\u0006\u0010\u001b\u001a\u00020\u00102\u0006\u0010\u001c\u001a\u00020\u0010H\b\u001a\u001c\u0010\u001d\u001a\u00020\u0010*\u00020\u00022\u0006\u0010\u001e\u001a\u00020\u00022\b\b\u0002\u0010\u001f\u001a\u00020 \u001a\u0015\u0010!\u001a\u00020 *\u00020\u00022\u0006\u0010\t\u001a\u00020\bH\b\u001a\u0015\u0010!\u001a\u00020 *\u00020\u00022\u0006\u0010\"\u001a\u00020#H\b\u001a\n\u0010$\u001a\u00020\u0002*\u00020\u0002\u001a\u001c\u0010%\u001a\u00020 *\u00020\u00022\u0006\u0010&\u001a\u00020\u00022\b\b\u0002\u0010\u001f\u001a\u00020 \u001a \u0010'\u001a\u00020 *\u0004\u0018\u00010\u00022\b\u0010\u001e\u001a\u0004\u0018\u00010\u00022\b\b\u0002\u0010\u001f\u001a\u00020 \u001a2\u0010(\u001a\u00020\u0002*\u00020\u00022\u0006\u0010)\u001a\u00020*2\u0016\u0010+\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010-0,\"\u0004\u0018\u00010-H\b¢\u0006\u0002\u0010.\u001a*\u0010(\u001a\u00020\u0002*\u00020\u00022\u0016\u0010+\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010-0,\"\u0004\u0018\u00010-H\b¢\u0006\u0002\u0010/\u001a:\u0010(\u001a\u00020\u0002*\u00020\u00032\u0006\u0010)\u001a\u00020*2\u0006\u0010(\u001a\u00020\u00022\u0016\u0010+\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010-0,\"\u0004\u0018\u00010-H\b¢\u0006\u0002\u00100\u001a2\u0010(\u001a\u00020\u0002*\u00020\u00032\u0006\u0010(\u001a\u00020\u00022\u0016\u0010+\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010-0,\"\u0004\u0018\u00010-H\b¢\u0006\u0002\u00101\u001a\r\u00102\u001a\u00020\u0002*\u00020\u0002H\b\u001a\n\u00103\u001a\u00020 *\u00020#\u001a\u001d\u00104\u001a\u00020\u0010*\u00020\u00022\u0006\u00105\u001a\u0002062\u0006\u00107\u001a\u00020\u0010H\b\u001a\u001d\u00104\u001a\u00020\u0010*\u00020\u00022\u0006\u00108\u001a\u00020\u00022\u0006\u00107\u001a\u00020\u0010H\b\u001a\u001d\u00109\u001a\u00020\u0010*\u00020\u00022\u0006\u00105\u001a\u0002062\u0006\u00107\u001a\u00020\u0010H\b\u001a\u001d\u00109\u001a\u00020\u0010*\u00020\u00022\u0006\u00108\u001a\u00020\u00022\u0006\u00107\u001a\u00020\u0010H\b\u001a\u001d\u0010:\u001a\u00020\u0010*\u00020\u00022\u0006\u0010\u0018\u001a\u00020\u00102\u0006\u0010;\u001a\u00020\u0010H\b\u001a4\u0010<\u001a\u00020 *\u00020#2\u0006\u0010=\u001a\u00020\u00102\u0006\u0010\u001e\u001a\u00020#2\u0006\u0010>\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00102\b\b\u0002\u0010\u001f\u001a\u00020 \u001a4\u0010<\u001a\u00020 *\u00020\u00022\u0006\u0010=\u001a\u00020\u00102\u0006\u0010\u001e\u001a\u00020\u00022\u0006\u0010>\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00102\b\b\u0002\u0010\u001f\u001a\u00020 \u001a\u0012\u0010?\u001a\u00020\u0002*\u00020#2\u0006\u0010@\u001a\u00020\u0010\u001a$\u0010A\u001a\u00020\u0002*\u00020\u00022\u0006\u0010B\u001a\u0002062\u0006\u0010C\u001a\u0002062\b\b\u0002\u0010\u001f\u001a\u00020 \u001a$\u0010A\u001a\u00020\u0002*\u00020\u00022\u0006\u0010D\u001a\u00020\u00022\u0006\u0010E\u001a\u00020\u00022\b\b\u0002\u0010\u001f\u001a\u00020 \u001a$\u0010F\u001a\u00020\u0002*\u00020\u00022\u0006\u0010B\u001a\u0002062\u0006\u0010C\u001a\u0002062\b\b\u0002\u0010\u001f\u001a\u00020 \u001a$\u0010F\u001a\u00020\u0002*\u00020\u00022\u0006\u0010D\u001a\u00020\u00022\u0006\u0010E\u001a\u00020\u00022\b\b\u0002\u0010\u001f\u001a\u00020 \u001a\"\u0010G\u001a\b\u0012\u0004\u0012\u00020\u00020H*\u00020#2\u0006\u0010I\u001a\u00020J2\b\b\u0002\u0010K\u001a\u00020\u0010\u001a\u001c\u0010L\u001a\u00020 *\u00020\u00022\u0006\u0010M\u001a\u00020\u00022\b\b\u0002\u0010\u001f\u001a\u00020 \u001a$\u0010L\u001a\u00020 *\u00020\u00022\u0006\u0010M\u001a\u00020\u00022\u0006\u0010N\u001a\u00020\u00102\b\b\u0002\u0010\u001f\u001a\u00020 \u001a\u0015\u0010O\u001a\u00020\u0002*\u00020\u00022\u0006\u0010N\u001a\u00020\u0010H\b\u001a\u001d\u0010O\u001a\u00020\u0002*\u00020\u00022\u0006\u0010N\u001a\u00020\u00102\u0006\u0010\u001c\u001a\u00020\u0010H\b\u001a\u0017\u0010P\u001a\u00020\f*\u00020\u00022\b\b\u0002\u0010\r\u001a\u00020\u000eH\b\u001a\r\u0010Q\u001a\u00020\u0013*\u00020\u0002H\b\u001a3\u0010Q\u001a\u00020\u0013*\u00020\u00022\u0006\u0010R\u001a\u00020\u00132\b\b\u0002\u0010S\u001a\u00020\u00102\b\b\u0002\u0010N\u001a\u00020\u00102\b\b\u0002\u0010\u001c\u001a\u00020\u0010H\b\u001a\r\u0010T\u001a\u00020\u0002*\u00020\u0002H\b\u001a\u0015\u0010T\u001a\u00020\u0002*\u00020\u00022\u0006\u0010)\u001a\u00020*H\b\u001a\u0017\u0010U\u001a\u00020J*\u00020\u00022\b\b\u0002\u0010V\u001a\u00020\u0010H\b\u001a\r\u0010W\u001a\u00020\u0002*\u00020\u0002H\b\u001a\u0015\u0010W\u001a\u00020\u0002*\u00020\u00022\u0006\u0010)\u001a\u00020*H\b\"\u001b\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001*\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005¨\u0006X"}, d2 = {"CASE_INSENSITIVE_ORDER", "Ljava/util/Comparator;", "", "Lkotlin/String$Companion;", "getCASE_INSENSITIVE_ORDER", "(Lkotlin/jvm/internal/StringCompanionObject;)Ljava/util/Comparator;", "String", "stringBuffer", "Ljava/lang/StringBuffer;", "stringBuilder", "Ljava/lang/StringBuilder;", "bytes", "", "charset", "Ljava/nio/charset/Charset;", "offset", "", "length", "chars", "", "codePoints", "", "capitalize", "codePointAt", "index", "codePointBefore", "codePointCount", "beginIndex", "endIndex", "compareTo", "other", "ignoreCase", "", "contentEquals", "charSequence", "", "decapitalize", "endsWith", "suffix", "equals", "format", "locale", "Ljava/util/Locale;", "args", "", "", "(Ljava/lang/String;Ljava/util/Locale;[Ljava/lang/Object;)Ljava/lang/String;", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", "(Lkotlin/jvm/internal/StringCompanionObject;Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", "(Lkotlin/jvm/internal/StringCompanionObject;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", "intern", "isBlank", "nativeIndexOf", "ch", "", "fromIndex", "str", "nativeLastIndexOf", "offsetByCodePoints", "codePointOffset", "regionMatches", "thisOffset", "otherOffset", "repeat", "n", "replace", "oldChar", "newChar", "oldValue", "newValue", "replaceFirst", "split", "", "regex", "Ljava/util/regex/Pattern;", "limit", "startsWith", "prefix", "startIndex", "substring", "toByteArray", "toCharArray", "destination", "destinationOffset", "toLowerCase", "toPattern", "flags", "toUpperCase", "kotlin-stdlib"}, k = 5, mv = {1, 1, 10}, xi = 1, xs = "kotlin/text/StringsKt")
/* compiled from: StringsJVM.kt */
class StringsKt__StringsJVMKt extends StringsKt__StringNumberConversionsKt {
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String repeat(@org.jetbrains.annotations.NotNull java.lang.CharSequence r10, int r11) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:26:0x0081 in {2, 3, 11, 12, 15, 16, 17, 20, 21, 23, 25} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r0 = "$receiver";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r10, r0);
        r0 = 1;
        r1 = 0;
        if (r11 < 0) goto L_0x000b;
    L_0x0009:
        r2 = 1;
        goto L_0x000c;
    L_0x000b:
        r2 = 0;
    L_0x000c:
        if (r2 == 0) goto L_0x005e;
    L_0x000e:
        switch(r11) {
            case 0: goto L_0x002b;
            case 1: goto L_0x0026;
            default: goto L_0x0011;
        };
    L_0x0011:
        r2 = r10.length();
        switch(r2) {
            case 0: goto L_0x0048;
            case 1: goto L_0x002e;
            default: goto L_0x0018;
        };
    L_0x0018:
        r1 = new java.lang.StringBuilder;
        r2 = r10.length();
        r2 = r2 * r11;
        r1.<init>(r2);
        if (r0 > r11) goto L_0x0053;
    L_0x0025:
        goto L_0x004b;
    L_0x0026:
        r0 = r10.toString();
        goto L_0x005c;
    L_0x002b:
        r0 = "";
        goto L_0x005c;
    L_0x002e:
        r2 = r10.charAt(r1);
        r3 = r1;
        r4 = r11;
        r5 = r1;
        r6 = new char[r4];
        r7 = r6.length;
        r8 = 0;
    L_0x0039:
        if (r1 >= r7) goto L_0x0041;
    L_0x003b:
        r9 = r1;
        r6[r1] = r2;
        r1 = r1 + r0;
        goto L_0x0039;
        r0 = new java.lang.String;
        r0.<init>(r6);
        goto L_0x005c;
    L_0x0048:
        r0 = "";
        goto L_0x005c;
    L_0x004b:
        r1.append(r10);
        if (r0 == r11) goto L_0x0053;
    L_0x0050:
        r0 = r0 + 1;
        goto L_0x004b;
    L_0x0053:
        r0 = r1.toString();
        r2 = "sb.toString()";
        kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r0, r2);
        return r0;
    L_0x005e:
        r0 = r1;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Count 'n' must be non-negative, but was ";
        r1.append(r2);
        r1.append(r11);
        r2 = 46;
        r1.append(r2);
        r0 = r1.toString();
        r1 = new java.lang.IllegalArgumentException;
        r0 = r0.toString();
        r1.<init>(r0);
        r1 = (java.lang.Throwable) r1;
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.text.StringsKt__StringsJVMKt.repeat(java.lang.CharSequence, int):java.lang.String");
    }

    @InlineOnly
    private static final int nativeIndexOf(@NotNull String $receiver, char ch, int fromIndex) {
        if ($receiver != null) {
            return $receiver.indexOf(ch, fromIndex);
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    @InlineOnly
    private static final int nativeIndexOf(@NotNull String $receiver, String str, int fromIndex) {
        if ($receiver != null) {
            return $receiver.indexOf(str, fromIndex);
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    @InlineOnly
    private static final int nativeLastIndexOf(@NotNull String $receiver, char ch, int fromIndex) {
        if ($receiver != null) {
            return $receiver.lastIndexOf(ch, fromIndex);
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    @InlineOnly
    private static final int nativeLastIndexOf(@NotNull String $receiver, String str, int fromIndex) {
        if ($receiver != null) {
            return $receiver.lastIndexOf(str, fromIndex);
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    public static final boolean equals(@Nullable String $receiver, @Nullable String other, boolean ignoreCase) {
        if ($receiver == null) {
            return other == null;
        }
        boolean equalsIgnoreCase;
        if (ignoreCase) {
            equalsIgnoreCase = $receiver.equalsIgnoreCase(other);
        } else {
            equalsIgnoreCase = $receiver.equals(other);
        }
        return equalsIgnoreCase;
    }

    @NotNull
    public static final String replace(@NotNull String $receiver, char oldChar, char newChar, boolean ignoreCase) {
        String str = $receiver;
        Intrinsics.checkParameterIsNotNull(str, "$receiver");
        if (ignoreCase) {
            return SequencesKt.joinToString$default(StringsKt.splitToSequence$default(str, new char[]{oldChar}, ignoreCase, 0, 4, null), String.valueOf(newChar), null, null, 0, null, null, 62, null);
        }
        String replace = $receiver.replace(oldChar, newChar);
        Intrinsics.checkExpressionValueIsNotNull(replace, "(this as java.lang.Strin…replace(oldChar, newChar)");
        return replace;
    }

    @NotNull
    public static final String replace(@NotNull String $receiver, @NotNull String oldValue, @NotNull String newValue, boolean ignoreCase) {
        String str = $receiver;
        String str2 = oldValue;
        String str3 = newValue;
        Intrinsics.checkParameterIsNotNull(str, "$receiver");
        Intrinsics.checkParameterIsNotNull(str2, "oldValue");
        Intrinsics.checkParameterIsNotNull(str3, "newValue");
        return SequencesKt.joinToString$default(StringsKt.splitToSequence$default(str, new String[]{str2}, ignoreCase, 0, 4, null), str3, null, null, 0, null, null, 62, null);
    }

    @NotNull
    public static final String replaceFirst(@NotNull String $receiver, char oldChar, char newChar, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        int index = StringsKt.indexOf$default($receiver, oldChar, 0, ignoreCase, 2, null);
        if (index < 0) {
            return $receiver;
        }
        return StringsKt.replaceRange($receiver, index, index + 1, String.valueOf(newChar)).toString();
    }

    @NotNull
    public static final String replaceFirst(@NotNull String $receiver, @NotNull String oldValue, @NotNull String newValue, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(oldValue, "oldValue");
        Intrinsics.checkParameterIsNotNull(newValue, "newValue");
        int index = StringsKt.indexOf$default($receiver, oldValue, 0, ignoreCase, 2, null);
        if (index < 0) {
            return $receiver;
        }
        return StringsKt.replaceRange($receiver, index, oldValue.length() + index, newValue).toString();
    }

    @InlineOnly
    private static final String toUpperCase(@NotNull String $receiver) {
        if ($receiver != null) {
            String toUpperCase = $receiver.toUpperCase();
            Intrinsics.checkExpressionValueIsNotNull(toUpperCase, "(this as java.lang.String).toUpperCase()");
            return toUpperCase;
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    @InlineOnly
    private static final String toLowerCase(@NotNull String $receiver) {
        if ($receiver != null) {
            String toLowerCase = $receiver.toLowerCase();
            Intrinsics.checkExpressionValueIsNotNull(toLowerCase, "(this as java.lang.String).toLowerCase()");
            return toLowerCase;
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    @InlineOnly
    private static final char[] toCharArray(@NotNull String $receiver) {
        if ($receiver != null) {
            Object toCharArray = $receiver.toCharArray();
            Intrinsics.checkExpressionValueIsNotNull(toCharArray, "(this as java.lang.String).toCharArray()");
            return toCharArray;
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    @InlineOnly
    static /* bridge */ /* synthetic */ char[] toCharArray$default(String $receiver, char[] destination, int destinationOffset, int startIndex, int endIndex, int i, Object obj) {
        if ((i & 2) != null) {
            destinationOffset = 0;
        }
        if ((i & 4) != null) {
            startIndex = 0;
        }
        if ((i & 8) != null) {
            endIndex = $receiver.length();
        }
        if ($receiver != null) {
            $receiver.getChars(startIndex, endIndex, destination, destinationOffset);
            return destination;
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    @InlineOnly
    private static final char[] toCharArray(@NotNull String $receiver, char[] destination, int destinationOffset, int startIndex, int endIndex) {
        if ($receiver != null) {
            $receiver.getChars(startIndex, endIndex, destination, destinationOffset);
            return destination;
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    @InlineOnly
    private static final String format(@NotNull String $receiver, Object... args) {
        String format = String.format($receiver, Arrays.copyOf(args, args.length));
        Intrinsics.checkExpressionValueIsNotNull(format, "java.lang.String.format(this, *args)");
        return format;
    }

    @InlineOnly
    private static final String format(@NotNull StringCompanionObject $receiver, String format, Object... args) {
        String format2 = String.format(format, Arrays.copyOf(args, args.length));
        Intrinsics.checkExpressionValueIsNotNull(format2, "java.lang.String.format(format, *args)");
        return format2;
    }

    public static /* bridge */ /* synthetic */ boolean equals$default(String str, String str2, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        return StringsKt.equals(str, str2, z);
    }

    @InlineOnly
    private static final String format(@NotNull String $receiver, Locale locale, Object... args) {
        String format = String.format(locale, $receiver, Arrays.copyOf(args, args.length));
        Intrinsics.checkExpressionValueIsNotNull(format, "java.lang.String.format(locale, this, *args)");
        return format;
    }

    @InlineOnly
    private static final String format(@NotNull StringCompanionObject $receiver, Locale locale, String format, Object... args) {
        String format2 = String.format(locale, format, Arrays.copyOf(args, args.length));
        Intrinsics.checkExpressionValueIsNotNull(format2, "java.lang.String.format(locale, format, *args)");
        return format2;
    }

    @NotNull
    public static /* bridge */ /* synthetic */ List split$default(CharSequence charSequence, Pattern pattern, int i, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = 0;
        }
        return StringsKt.split(charSequence, pattern, i);
    }

    @NotNull
    public static final List<String> split(@NotNull CharSequence $receiver, @NotNull Pattern regex, int limit) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(regex, "regex");
        if ((limit >= 0 ? 1 : null) != null) {
            int $i$a$1$require = regex.split($receiver, limit == 0 ? -1 : limit);
            Intrinsics.checkExpressionValueIsNotNull($i$a$1$require, "regex.split(this, if (limit == 0) -1 else limit)");
            return ArraysKt.asList($i$a$1$require);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Limit must be non-negative, but was ");
        stringBuilder.append(limit);
        stringBuilder.append('.');
        throw new IllegalArgumentException(stringBuilder.toString().toString());
    }

    @InlineOnly
    private static final String substring(@NotNull String $receiver, int startIndex) {
        if ($receiver != null) {
            String substring = $receiver.substring(startIndex);
            Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.String).substring(startIndex)");
            return substring;
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    @InlineOnly
    private static final String substring(@NotNull String $receiver, int startIndex, int endIndex) {
        if ($receiver != null) {
            String substring = $receiver.substring(startIndex, endIndex);
            Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
            return substring;
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    public static /* bridge */ /* synthetic */ boolean startsWith$default(String str, String str2, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        return StringsKt.startsWith(str, str2, z);
    }

    public static final boolean startsWith(@NotNull String $receiver, @NotNull String prefix, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(prefix, "prefix");
        if (!ignoreCase) {
            return $receiver.startsWith(prefix);
        }
        return StringsKt.regionMatches($receiver, 0, prefix, 0, prefix.length(), ignoreCase);
    }

    public static /* bridge */ /* synthetic */ boolean startsWith$default(String str, String str2, int i, boolean z, int i2, Object obj) {
        if ((i2 & 4) != 0) {
            z = false;
        }
        return StringsKt.startsWith(str, str2, i, z);
    }

    public static final boolean startsWith(@NotNull String $receiver, @NotNull String prefix, int startIndex, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(prefix, "prefix");
        if (!ignoreCase) {
            return $receiver.startsWith(prefix, startIndex);
        }
        return StringsKt.regionMatches($receiver, startIndex, prefix, 0, prefix.length(), ignoreCase);
    }

    public static /* bridge */ /* synthetic */ boolean endsWith$default(String str, String str2, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        return StringsKt.endsWith(str, str2, z);
    }

    public static final boolean endsWith(@NotNull String $receiver, @NotNull String suffix, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(suffix, "suffix");
        if (!ignoreCase) {
            return $receiver.endsWith(suffix);
        }
        return StringsKt.regionMatches($receiver, $receiver.length() - suffix.length(), suffix, 0, suffix.length(), true);
    }

    @InlineOnly
    private static final String String(byte[] bytes, int offset, int length, Charset charset) {
        return new String(bytes, offset, length, charset);
    }

    @InlineOnly
    private static final String String(byte[] bytes, Charset charset) {
        return new String(bytes, charset);
    }

    @InlineOnly
    private static final String String(byte[] bytes, int offset, int length) {
        return new String(bytes, offset, length, Charsets.UTF_8);
    }

    @InlineOnly
    private static final String String(byte[] bytes) {
        return new String(bytes, Charsets.UTF_8);
    }

    @InlineOnly
    private static final String String(char[] chars) {
        return new String(chars);
    }

    @InlineOnly
    private static final String String(char[] chars, int offset, int length) {
        return new String(chars, offset, length);
    }

    @InlineOnly
    private static final String String(int[] codePoints, int offset, int length) {
        return new String(codePoints, offset, length);
    }

    @InlineOnly
    private static final String String(StringBuffer stringBuffer) {
        return new String(stringBuffer);
    }

    @InlineOnly
    private static final String String(StringBuilder stringBuilder) {
        return new String(stringBuilder);
    }

    @InlineOnly
    private static final int codePointAt(@NotNull String $receiver, int index) {
        if ($receiver != null) {
            return $receiver.codePointAt(index);
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    @InlineOnly
    private static final int codePointBefore(@NotNull String $receiver, int index) {
        if ($receiver != null) {
            return $receiver.codePointBefore(index);
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    @InlineOnly
    private static final int codePointCount(@NotNull String $receiver, int beginIndex, int endIndex) {
        if ($receiver != null) {
            return $receiver.codePointCount(beginIndex, endIndex);
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    public static /* bridge */ /* synthetic */ int compareTo$default(String str, String str2, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        return StringsKt.compareTo(str, str2, z);
    }

    public static final int compareTo(@NotNull String $receiver, @NotNull String other, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(other, "other");
        if (ignoreCase) {
            return $receiver.compareToIgnoreCase(other);
        }
        return $receiver.compareTo(other);
    }

    @InlineOnly
    private static final boolean contentEquals(@NotNull String $receiver, CharSequence charSequence) {
        if ($receiver != null) {
            return $receiver.contentEquals(charSequence);
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    @InlineOnly
    private static final boolean contentEquals(@NotNull String $receiver, StringBuffer stringBuilder) {
        if ($receiver != null) {
            return $receiver.contentEquals(stringBuilder);
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    @InlineOnly
    private static final String intern(@NotNull String $receiver) {
        if ($receiver != null) {
            String intern = $receiver.intern();
            Intrinsics.checkExpressionValueIsNotNull(intern, "(this as java.lang.String).intern()");
            return intern;
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    public static final boolean isBlank(@NotNull CharSequence $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        if ($receiver.length() != 0) {
            Iterable $receiver$iv = StringsKt.getIndices($receiver);
            int $i$f$all = 0;
            if (($receiver$iv instanceof Collection) && ((Collection) $receiver$iv).isEmpty()) {
                $receiver$iv = true;
            } else {
                Iterator it = $receiver$iv.iterator();
                while (it.hasNext()) {
                    if (CharsKt.isWhitespace($receiver.charAt(((IntIterator) it).nextInt())) == 0) {
                        $receiver$iv = null;
                        break;
                    }
                }
                $receiver$iv = true;
            }
            if ($receiver$iv == null) {
                return false;
            }
        }
        return true;
    }

    @InlineOnly
    private static final int offsetByCodePoints(@NotNull String $receiver, int index, int codePointOffset) {
        if ($receiver != null) {
            return $receiver.offsetByCodePoints(index, codePointOffset);
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    public static final boolean regionMatches(@NotNull CharSequence $receiver, int thisOffset, @NotNull CharSequence other, int otherOffset, int length, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(other, "other");
        if (!($receiver instanceof String) || !(other instanceof String)) {
            return StringsKt.regionMatchesImpl($receiver, thisOffset, other, otherOffset, length, ignoreCase);
        }
        return StringsKt.regionMatches((String) $receiver, thisOffset, (String) other, otherOffset, length, ignoreCase);
    }

    public static final boolean regionMatches(@NotNull String $receiver, int thisOffset, @NotNull String other, int otherOffset, int length, boolean ignoreCase) {
        boolean regionMatches;
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(other, "other");
        if (ignoreCase) {
            regionMatches = $receiver.regionMatches(ignoreCase, thisOffset, other, otherOffset, length);
        } else {
            regionMatches = $receiver.regionMatches(thisOffset, other, otherOffset, length);
        }
        return regionMatches;
    }

    @InlineOnly
    private static final String toLowerCase(@NotNull String $receiver, Locale locale) {
        if ($receiver != null) {
            String toLowerCase = $receiver.toLowerCase(locale);
            Intrinsics.checkExpressionValueIsNotNull(toLowerCase, "(this as java.lang.String).toLowerCase(locale)");
            return toLowerCase;
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    @InlineOnly
    private static final String toUpperCase(@NotNull String $receiver, Locale locale) {
        if ($receiver != null) {
            String toUpperCase = $receiver.toUpperCase(locale);
            Intrinsics.checkExpressionValueIsNotNull(toUpperCase, "(this as java.lang.String).toUpperCase(locale)");
            return toUpperCase;
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    @InlineOnly
    private static final byte[] toByteArray(@NotNull String $receiver, Charset charset) {
        if ($receiver != null) {
            Object bytes = $receiver.getBytes(charset);
            Intrinsics.checkExpressionValueIsNotNull(bytes, "(this as java.lang.String).getBytes(charset)");
            return bytes;
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    @InlineOnly
    static /* bridge */ /* synthetic */ byte[] toByteArray$default(String $receiver, Charset charset, int i, Object obj) {
        if ((i & 1) != null) {
            charset = Charsets.UTF_8;
        }
        if ($receiver != null) {
            obj = $receiver.getBytes(charset);
            Intrinsics.checkExpressionValueIsNotNull(obj, "(this as java.lang.String).getBytes(charset)");
            return obj;
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    @InlineOnly
    static /* bridge */ /* synthetic */ Pattern toPattern$default(String $receiver, int flags, int i, Object obj) {
        if ((i & 1) != null) {
            flags = 0;
        }
        obj = Pattern.compile($receiver, flags);
        Intrinsics.checkExpressionValueIsNotNull(obj, "java.util.regex.Pattern.compile(this, flags)");
        return obj;
    }

    @InlineOnly
    private static final Pattern toPattern(@NotNull String $receiver, int flags) {
        Pattern compile = Pattern.compile($receiver, flags);
        Intrinsics.checkExpressionValueIsNotNull(compile, "java.util.regex.Pattern.compile(this, flags)");
        return compile;
    }

    @NotNull
    public static final String capitalize(@NotNull String $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        if ((((CharSequence) $receiver).length() > 0 ? 1 : null) == null || !Character.isLowerCase($receiver.charAt(0))) {
            return $receiver;
        }
        StringBuilder stringBuilder = new StringBuilder();
        String substring = $receiver.substring(0, 1);
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
        if (substring != null) {
            substring = substring.toUpperCase();
            Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.String).toUpperCase()");
            stringBuilder.append(substring);
            String substring2 = $receiver.substring(1);
            Intrinsics.checkExpressionValueIsNotNull(substring2, "(this as java.lang.String).substring(startIndex)");
            stringBuilder.append(substring2);
            return stringBuilder.toString();
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    @NotNull
    public static final String decapitalize(@NotNull String $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        if ((((CharSequence) $receiver).length() > 0 ? 1 : null) == null || !Character.isUpperCase($receiver.charAt(0))) {
            return $receiver;
        }
        StringBuilder stringBuilder = new StringBuilder();
        String substring = $receiver.substring(0, 1);
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
        if (substring != null) {
            substring = substring.toLowerCase();
            Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.String).toLowerCase()");
            stringBuilder.append(substring);
            String substring2 = $receiver.substring(1);
            Intrinsics.checkExpressionValueIsNotNull(substring2, "(this as java.lang.String).substring(startIndex)");
            stringBuilder.append(substring2);
            return stringBuilder.toString();
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    @NotNull
    public static final Comparator<String> getCASE_INSENSITIVE_ORDER(@NotNull StringCompanionObject $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Comparator<String> comparator = String.CASE_INSENSITIVE_ORDER;
        Intrinsics.checkExpressionValueIsNotNull(comparator, "java.lang.String.CASE_INSENSITIVE_ORDER");
        return comparator;
    }
}
