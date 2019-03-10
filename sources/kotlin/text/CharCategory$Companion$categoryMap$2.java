package kotlin.text;

import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;
import kotlin.ranges.RangesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010$\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0000\u0010\u0000\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0001H\n¢\u0006\u0002\b\u0004"}, d2 = {"<anonymous>", "", "", "Lkotlin/text/CharCategory;", "invoke"}, k = 3, mv = {1, 1, 10})
/* compiled from: CharCategory.kt */
final class CharCategory$Companion$categoryMap$2 extends Lambda implements Function0<Map<Integer, ? extends CharCategory>> {
    public static final CharCategory$Companion$categoryMap$2 INSTANCE = new CharCategory$Companion$categoryMap$2();

    CharCategory$Companion$categoryMap$2() {
        super(0);
    }

    @NotNull
    public final Map<Integer, CharCategory> invoke() {
        Object[] $receiver$iv = CharCategory.values();
        int i = 0;
        int $i$f$associateBy = 0;
        Map destination$iv$iv = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity($receiver$iv.length), 16));
        Object[] $receiver$iv$iv = $receiver$iv;
        int $i$f$associateByTo = 0;
        int length = $receiver$iv$iv.length;
        while (i < length) {
            CharCategory element$iv$iv = $receiver$iv$iv[i];
            destination$iv$iv.put(Integer.valueOf(element$iv$iv.getValue()), element$iv$iv);
            i++;
        }
        return destination$iv$iv;
    }
}
