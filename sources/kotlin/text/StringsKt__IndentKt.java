package kotlin.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.SequencesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u001e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u000b\u001a!\u0010\u0000\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0002H\u0002¢\u0006\u0002\b\u0004\u001a\u0011\u0010\u0005\u001a\u00020\u0006*\u00020\u0002H\u0002¢\u0006\u0002\b\u0007\u001a\u0014\u0010\b\u001a\u00020\u0002*\u00020\u00022\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u001aJ\u0010\t\u001a\u00020\u0002*\b\u0012\u0004\u0012\u00020\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00062\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00020\u00012\u0014\u0010\r\u001a\u0010\u0012\u0004\u0012\u00020\u0002\u0012\u0006\u0012\u0004\u0018\u00010\u00020\u0001H\b¢\u0006\u0002\b\u000e\u001a\u0014\u0010\u000f\u001a\u00020\u0002*\u00020\u00022\b\b\u0002\u0010\u0010\u001a\u00020\u0002\u001a\u001e\u0010\u0011\u001a\u00020\u0002*\u00020\u00022\b\b\u0002\u0010\u0010\u001a\u00020\u00022\b\b\u0002\u0010\u0012\u001a\u00020\u0002\u001a\n\u0010\u0013\u001a\u00020\u0002*\u00020\u0002\u001a\u0014\u0010\u0014\u001a\u00020\u0002*\u00020\u00022\b\b\u0002\u0010\u0012\u001a\u00020\u0002¨\u0006\u0015"}, d2 = {"getIndentFunction", "Lkotlin/Function1;", "", "indent", "getIndentFunction$StringsKt__IndentKt", "indentWidth", "", "indentWidth$StringsKt__IndentKt", "prependIndent", "reindent", "", "resultSizeEstimate", "indentAddFunction", "indentCutFunction", "reindent$StringsKt__IndentKt", "replaceIndent", "newIndent", "replaceIndentByMargin", "marginPrefix", "trimIndent", "trimMargin", "kotlin-stdlib"}, k = 5, mv = {1, 1, 10}, xi = 1, xs = "kotlin/text/StringsKt")
/* compiled from: Indent.kt */
class StringsKt__IndentKt {
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String replaceIndentByMargin(@org.jetbrains.annotations.NotNull java.lang.String r39, @org.jetbrains.annotations.NotNull java.lang.String r40, @org.jetbrains.annotations.NotNull java.lang.String r41) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:45:0x0171 in {7, 10, 16, 17, 18, 21, 26, 28, 29, 34, 35, 38, 39, 40, 42, 44} preds:[]
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
        r0 = r39;
        r7 = r41;
        r1 = "$receiver";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r0, r1);
        r1 = "newIndent";
        r8 = r40;
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r8, r1);
        r1 = "marginPrefix";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r7, r1);
        r1 = r7;
        r1 = (java.lang.CharSequence) r1;
        r1 = kotlin.text.StringsKt.isBlank(r1);
        r1 = r1 ^ 1;
        r9 = 0;
        if (r1 == 0) goto L_0x0162;
    L_0x0021:
        r1 = r0;
        r1 = (java.lang.CharSequence) r1;
        r10 = kotlin.text.StringsKt.lines(r1);
        r1 = r39.length();
        r2 = r40.length();
        r3 = r10.size();
        r2 = r2 * r3;
        r11 = r1 + r2;
        r1 = getIndentFunction$StringsKt__IndentKt(r40);
        r12 = r10;
        r13 = r1;
        r14 = r9;
        r15 = kotlin.collections.CollectionsKt.getLastIndex(r12);
        r16 = r12;
        r16 = (java.lang.Iterable) r16;
        r17 = r9;
        r1 = new java.util.ArrayList;
        r1.<init>();
        r1 = (java.util.Collection) r1;
        r18 = r16;
        r6 = r1;
        r19 = r9;
        r20 = r18;
        r21 = r9;
        r1 = 0;
        r22 = r20.iterator();
        r2 = 0;
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r23 = 0;
        r24 = 0;
    L_0x006a:
        r25 = r22.hasNext();
        if (r25 == 0) goto L_0x012d;
    L_0x0070:
        r25 = r22.next();
        r26 = r1 + 1;
        r27 = r1;
        r28 = r25;
        r29 = r2;
        r30 = r28;
        r30 = (java.lang.String) r30;
        r2 = r27;
        r31 = r3;
        r32 = 0;
        if (r2 == 0) goto L_0x008a;
    L_0x0088:
        if (r2 != r15) goto L_0x0099;
    L_0x008a:
        r1 = r30;
        r1 = (java.lang.CharSequence) r1;
        r1 = kotlin.text.StringsKt.isBlank(r1);
        if (r1 == 0) goto L_0x0099;
    L_0x0094:
        r9 = r6;
        r30 = r32;
        goto L_0x0117;
        r3 = r30;
        r33 = r5;
        r1 = r3;
        r1 = (java.lang.CharSequence) r1;
        r34 = r4;
        r4 = r1.length();
        r5 = 0;
    L_0x00a8:
        r9 = -1;
        if (r5 >= r4) goto L_0x00bd;
    L_0x00ab:
        r35 = r1.charAt(r5);
        r36 = kotlin.text.CharsKt.isWhitespace(r35);
        r35 = r36 ^ 1;
        if (r35 == 0) goto L_0x00b8;
    L_0x00b7:
        goto L_0x00be;
        r5 = r5 + 1;
        r9 = 0;
        goto L_0x00a8;
    L_0x00bd:
        r5 = -1;
        if (r5 != r9) goto L_0x00c9;
    L_0x00c2:
        r36 = r2;
        r1 = r3;
        r9 = r6;
        r2 = r32;
        goto L_0x0101;
    L_0x00c9:
        r4 = 0;
        r9 = 4;
        r35 = 0;
        r1 = r3;
        r36 = r2;
        r2 = r41;
        r37 = r3;
        r3 = r5;
        r38 = r5;
        r5 = r9;
        r9 = r6;
        r6 = r35;
        r1 = kotlin.text.StringsKt.startsWith$default(r1, r2, r3, r4, r5, r6);
        if (r1 == 0) goto L_0x00fd;
    L_0x00e1:
        r1 = r41.length();
        r5 = r38 + r1;
        r1 = r37;
        if (r1 == 0) goto L_0x00f5;
    L_0x00eb:
        r2 = r1.substring(r5);
        r3 = "(this as java.lang.String).substring(startIndex)";
        kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r2, r3);
        goto L_0x0101;
    L_0x00f5:
        r2 = new kotlin.TypeCastException;
        r3 = "null cannot be cast to non-null type java.lang.String";
        r2.<init>(r3);
        throw r2;
    L_0x00fd:
        r1 = r37;
        r2 = r32;
        if (r2 == 0) goto L_0x0113;
    L_0x0104:
        r1 = r13.invoke(r2);
        r1 = (java.lang.String) r1;
        if (r1 == 0) goto L_0x0113;
    L_0x010c:
        r30 = r1;
        r5 = r33;
        r4 = r34;
        goto L_0x0117;
    L_0x0113:
        r5 = r33;
        r4 = r34;
        if (r30 == 0) goto L_0x0122;
    L_0x011a:
        r1 = r30;
        r2 = r24;
        r9.add(r1);
        goto L_0x0123;
    L_0x0123:
        r6 = r9;
        r1 = r26;
        r2 = r29;
        r3 = r31;
        r9 = 0;
        goto L_0x006a;
    L_0x012d:
        r9 = r6;
        r1 = r9;
        r1 = (java.util.List) r1;
        r16 = r1;
        r16 = (java.lang.Iterable) r16;
        r1 = new java.lang.StringBuilder;
        r1.<init>(r11);
        r17 = r1;
        r17 = (java.lang.Appendable) r17;
        r1 = "\n";
        r18 = r1;
        r18 = (java.lang.CharSequence) r18;
        r19 = 0;
        r20 = 0;
        r21 = 0;
        r22 = 0;
        r23 = 0;
        r24 = 124; // 0x7c float:1.74E-43 double:6.13E-322;
        r25 = 0;
        r1 = kotlin.collections.CollectionsKt.joinTo$default(r16, r17, r18, r19, r20, r21, r22, r23, r24, r25);
        r1 = (java.lang.StringBuilder) r1;
        r1 = r1.toString();
        r2 = "mapIndexedNotNull { inde…\"\\n\")\n        .toString()";
        kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r1, r2);
        return r1;
    L_0x0162:
        r1 = 0;
        r2 = new java.lang.IllegalArgumentException;
        r1 = "marginPrefix must be non-blank string.";
        r1 = r1.toString();
        r2.<init>(r1);
        r2 = (java.lang.Throwable) r2;
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.text.StringsKt__IndentKt.replaceIndentByMargin(java.lang.String, java.lang.String, java.lang.String):java.lang.String");
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String trimMargin$default(String str, String str2, int i, Object obj) {
        if ((i & 1) != 0) {
            str2 = "|";
        }
        return StringsKt.trimMargin(str, str2);
    }

    @NotNull
    public static final String trimMargin(@NotNull String $receiver, @NotNull String marginPrefix) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(marginPrefix, "marginPrefix");
        return StringsKt.replaceIndentByMargin($receiver, "", marginPrefix);
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String replaceIndentByMargin$default(String str, String str2, String str3, int i, Object obj) {
        if ((i & 1) != null) {
            str2 = "";
        }
        if ((i & 2) != 0) {
            str3 = "|";
        }
        return StringsKt.replaceIndentByMargin(str, str2, str3);
    }

    @NotNull
    public static final String trimIndent(@NotNull String $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return StringsKt.replaceIndent($receiver, "");
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String replaceIndent$default(String str, String str2, int i, Object obj) {
        if ((i & 1) != 0) {
            str2 = "";
        }
        return StringsKt.replaceIndent(str, str2);
    }

    @NotNull
    public static final String replaceIndent(@NotNull String $receiver, @NotNull String newIndent) {
        String str = $receiver;
        Intrinsics.checkParameterIsNotNull(str, "$receiver");
        Intrinsics.checkParameterIsNotNull(newIndent, "newIndent");
        List lines = StringsKt.lines(str);
        int $i$f$filter = 0;
        Collection destination$iv$iv = new ArrayList();
        int i = 0;
        for (String element$iv$iv : lines) {
            if ((StringsKt.isBlank(element$iv$iv) ^ 1) != 0) {
                destination$iv$iv.add(element$iv$iv);
            }
        }
        Iterable<String> $receiver$iv = (List) destination$iv$iv;
        $i$f$filter = i;
        destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10));
        i = 0;
        for (String p1 : $receiver$iv) {
            destination$iv$iv.add(Integer.valueOf(indentWidth$StringsKt__IndentKt(p1)));
        }
        Integer num = (Integer) CollectionsKt.min((List) destination$iv$iv);
        int minCommonIndent = num != null ? num.intValue() : 0;
        $i$f$filter = $receiver.length() + (newIndent.length() * lines.size());
        Function1 indentAddFunction$iv = getIndentFunction$StringsKt__IndentKt(newIndent);
        List<String> $receiver$iv2 = lines;
        i = 0;
        int lastIndex$iv = CollectionsKt.getLastIndex($receiver$iv2);
        int $i$f$mapIndexedNotNull = 0;
        Collection destination$iv$iv$iv = new ArrayList();
        int $i$f$mapIndexedNotNullTo = 0;
        int $i$f$forEachIndexed = 0;
        int index$iv$iv$iv = 0;
        for (String value$iv : $receiver$iv2) {
            Object it$iv$iv$iv;
            int index$iv$iv$iv$iv = index$iv$iv$iv + 1;
            int index$iv = index$iv$iv$iv;
            if ((index$iv == 0 || index$iv == lastIndex$iv) && StringsKt.isBlank(value$iv)) {
                it$iv$iv$iv = null;
            } else {
                str = StringsKt.drop(value$iv, minCommonIndent);
                if (str != null) {
                    it$iv$iv$iv = (String) indentAddFunction$iv.invoke(str);
                    if (it$iv$iv$iv != null) {
                    }
                }
                it$iv$iv$iv = value$iv;
            }
            if (it$iv$iv$iv != null) {
                destination$iv$iv$iv.add(it$iv$iv$iv);
            }
            index$iv$iv$iv = index$iv$iv$iv$iv;
            str = $receiver;
        }
        str = ((StringBuilder) CollectionsKt.joinTo$default((List) destination$iv$iv$iv, new StringBuilder($i$f$filter), "\n", null, null, 0, null, null, 124, null)).toString();
        Intrinsics.checkExpressionValueIsNotNull(str, "mapIndexedNotNull { inde…\"\\n\")\n        .toString()");
        return str;
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String prependIndent$default(String str, String str2, int i, Object obj) {
        if ((i & 1) != 0) {
            str2 = "    ";
        }
        return StringsKt.prependIndent(str, str2);
    }

    @NotNull
    public static final String prependIndent(@NotNull String $receiver, @NotNull String indent) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(indent, "indent");
        return SequencesKt.joinToString$default(SequencesKt.map(StringsKt.lineSequence($receiver), new StringsKt__IndentKt$prependIndent$1(indent)), "\n", null, null, 0, null, null, 62, null);
    }

    private static final int indentWidth$StringsKt__IndentKt(@NotNull String $receiver) {
        CharSequence $receiver$iv = $receiver;
        int i = 0;
        int $i$f$indexOfFirst = 0;
        int length = $receiver$iv.length();
        while (i < length) {
            if ((CharsKt.isWhitespace($receiver$iv.charAt(i)) ^ 1) != 0) {
                break;
            }
            i++;
        }
        i = -1;
        int it = i;
        i = length;
        return it == -1 ? $receiver.length() : it;
    }

    private static final Function1<String, String> getIndentFunction$StringsKt__IndentKt(String indent) {
        Function1<String, String> function1;
        if ((((CharSequence) indent).length() == 0 ? 1 : null) != null) {
            function1 = StringsKt__IndentKt$getIndentFunction$1.INSTANCE;
        } else {
            function1 = new StringsKt__IndentKt$getIndentFunction$2(indent);
        }
        return function1;
    }

    private static final String reindent$StringsKt__IndentKt(@NotNull List<String> $receiver, int resultSizeEstimate, Function1<? super String, String> indentAddFunction, Function1<? super String, String> indentCutFunction) {
        Iterable $receiver$iv;
        Iterable $receiver$iv2;
        int lastIndex;
        String $i$f$reindent$StringsKt__IndentKt;
        int $i$f$reindent$StringsKt__IndentKt2 = 0;
        int lastIndex2 = CollectionsKt.getLastIndex($receiver);
        Iterable<String> $receiver$iv3 = $receiver;
        int $i$f$mapIndexedNotNull = 0;
        Collection destination$iv$iv = new ArrayList();
        int $i$f$mapIndexedNotNullTo = 0;
        int $i$f$forEachIndexed = 0;
        int index$iv$iv = 0;
        for (String $i$f$reindent$StringsKt__IndentKt3 : $receiver$iv3) {
            int index$iv$iv$iv = index$iv$iv + 1;
            int $i$f$reindent$StringsKt__IndentKt4 = $i$f$reindent$StringsKt__IndentKt2;
            $receiver$iv = $receiver$iv2;
            int index = index$iv$iv;
            if ((index == 0 || index == lastIndex2) && StringsKt.isBlank($i$f$reindent$StringsKt__IndentKt3)) {
                $i$f$reindent$StringsKt__IndentKt2 = null;
                lastIndex = lastIndex2;
                lastIndex2 = indentAddFunction;
            } else {
                lastIndex = lastIndex2;
                String value = $i$f$reindent$StringsKt__IndentKt3;
                $i$f$reindent$StringsKt__IndentKt3 = (String) indentCutFunction.invoke($i$f$reindent$StringsKt__IndentKt3);
                if ($i$f$reindent$StringsKt__IndentKt3 != null) {
                    $i$f$reindent$StringsKt__IndentKt2 = (String) indentAddFunction.invoke($i$f$reindent$StringsKt__IndentKt3);
                    if ($i$f$reindent$StringsKt__IndentKt2 != null) {
                    }
                } else {
                    Function1<? super String, String> function1 = indentAddFunction;
                }
                $i$f$reindent$StringsKt__IndentKt2 = value;
            }
            if ($i$f$reindent$StringsKt__IndentKt2 != 0) {
                index = 0;
                destination$iv$iv.add($i$f$reindent$StringsKt__IndentKt2);
            }
            index$iv$iv = index$iv$iv$iv;
            $i$f$reindent$StringsKt__IndentKt2 = $i$f$reindent$StringsKt__IndentKt4;
            lastIndex2 = lastIndex;
            $receiver$iv2 = $receiver$iv;
        }
        lastIndex = lastIndex2;
        $receiver$iv = $receiver$iv2;
        lastIndex2 = indentAddFunction;
        $i$f$reindent$StringsKt__IndentKt3 = ((StringBuilder) CollectionsKt.joinTo$default((List) destination$iv$iv, new StringBuilder(resultSizeEstimate), "\n", null, null, 0, null, null, 124, null)).toString();
        Intrinsics.checkExpressionValueIsNotNull($i$f$reindent$StringsKt__IndentKt3, "mapIndexedNotNull { inde…\"\\n\")\n        .toString()");
        return $i$f$reindent$StringsKt__IndentKt3;
    }
}
