package kotlin.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.ReplaceWith;
import kotlin.TypeCastException;
import kotlin.collections.ArraysKt;
import kotlin.collections.CharIterator;
import kotlin.collections.CollectionsKt;
import kotlin.internal.InlineOnly;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000t\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\r\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\f\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u001e\n\u0002\b\f\n\u0002\u0010\u0019\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\b\n\u0002\u0010\u0011\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u001b\u001a\u001c\u0010\t\u001a\u00020\n*\u00020\u00022\u0006\u0010\u000b\u001a\u00020\u00022\b\b\u0002\u0010\f\u001a\u00020\r\u001a\u001c\u0010\u000e\u001a\u00020\n*\u00020\u00022\u0006\u0010\u000b\u001a\u00020\u00022\b\b\u0002\u0010\f\u001a\u00020\r\u001a\u001f\u0010\u000f\u001a\u00020\r*\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\f\u001a\u00020\rH\u0002\u001a\u001f\u0010\u000f\u001a\u00020\r*\u00020\u00022\u0006\u0010\u000b\u001a\u00020\u00022\b\b\u0002\u0010\f\u001a\u00020\rH\u0002\u001a\u0015\u0010\u000f\u001a\u00020\r*\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u0013H\n\u001a\u001c\u0010\u0014\u001a\u00020\r*\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\f\u001a\u00020\r\u001a\u001c\u0010\u0014\u001a\u00020\r*\u00020\u00022\u0006\u0010\u0015\u001a\u00020\u00022\b\b\u0002\u0010\f\u001a\u00020\r\u001a:\u0010\u0016\u001a\u0010\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\n\u0018\u00010\u0017*\u00020\u00022\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\n0\u00192\b\b\u0002\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r\u001aE\u0010\u0016\u001a\u0010\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\n\u0018\u00010\u0017*\u00020\u00022\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\n0\u00192\u0006\u0010\u001a\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u001b\u001a\u00020\rH\u0002¢\u0006\u0002\b\u001c\u001a:\u0010\u001d\u001a\u0010\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\n\u0018\u00010\u0017*\u00020\u00022\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\n0\u00192\b\b\u0002\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r\u001a\u0012\u0010\u001e\u001a\u00020\r*\u00020\u00022\u0006\u0010\u001f\u001a\u00020\u0006\u001a&\u0010 \u001a\u00020\u0006*\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r\u001a;\u0010 \u001a\u00020\u0006*\u00020\u00022\u0006\u0010\u000b\u001a\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u00062\u0006\u0010!\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u001b\u001a\u00020\rH\u0002¢\u0006\u0002\b\"\u001a&\u0010 \u001a\u00020\u0006*\u00020\u00022\u0006\u0010#\u001a\u00020\n2\b\b\u0002\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r\u001a&\u0010$\u001a\u00020\u0006*\u00020\u00022\u0006\u0010%\u001a\u00020&2\b\b\u0002\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r\u001a,\u0010$\u001a\u00020\u0006*\u00020\u00022\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\n0\u00192\b\b\u0002\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r\u001a\r\u0010'\u001a\u00020\r*\u00020\u0002H\b\u001a\r\u0010(\u001a\u00020\r*\u00020\u0002H\b\u001a\r\u0010)\u001a\u00020\r*\u00020\u0002H\b\u001a \u0010*\u001a\u00020\r*\u0004\u0018\u00010\u0002H\b\u0002\u000e\n\f\b\u0000\u0012\u0002\u0018\u0001\u001a\u0004\b\u0003\u0010\u0000\u001a \u0010+\u001a\u00020\r*\u0004\u0018\u00010\u0002H\b\u0002\u000e\n\f\b\u0000\u0012\u0002\u0018\u0001\u001a\u0004\b\u0003\u0010\u0000\u001a\r\u0010,\u001a\u00020-*\u00020\u0002H\u0002\u001a&\u0010.\u001a\u00020\u0006*\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r\u001a&\u0010.\u001a\u00020\u0006*\u00020\u00022\u0006\u0010#\u001a\u00020\n2\b\b\u0002\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r\u001a&\u0010/\u001a\u00020\u0006*\u00020\u00022\u0006\u0010%\u001a\u00020&2\b\b\u0002\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r\u001a,\u0010/\u001a\u00020\u0006*\u00020\u00022\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\n0\u00192\b\b\u0002\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r\u001a\u0010\u00100\u001a\b\u0012\u0004\u0012\u00020\n01*\u00020\u0002\u001a\u0010\u00102\u001a\b\u0012\u0004\u0012\u00020\n03*\u00020\u0002\u001a\u0015\u00104\u001a\u00020\r*\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u0013H\f\u001a\u000f\u00105\u001a\u00020\n*\u0004\u0018\u00010\nH\b\u001a\u001c\u00106\u001a\u00020\u0002*\u00020\u00022\u0006\u00107\u001a\u00020\u00062\b\b\u0002\u00108\u001a\u00020\u0011\u001a\u001c\u00106\u001a\u00020\n*\u00020\n2\u0006\u00107\u001a\u00020\u00062\b\b\u0002\u00108\u001a\u00020\u0011\u001a\u001c\u00109\u001a\u00020\u0002*\u00020\u00022\u0006\u00107\u001a\u00020\u00062\b\b\u0002\u00108\u001a\u00020\u0011\u001a\u001c\u00109\u001a\u00020\n*\u00020\n2\u0006\u00107\u001a\u00020\u00062\b\b\u0002\u00108\u001a\u00020\u0011\u001aG\u0010:\u001a\b\u0012\u0004\u0012\u00020\u000101*\u00020\u00022\u000e\u0010;\u001a\n\u0012\u0006\b\u0001\u0012\u00020\n0<2\b\b\u0002\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010=\u001a\u00020\u0006H\u0002¢\u0006\u0004\b>\u0010?\u001a=\u0010:\u001a\b\u0012\u0004\u0012\u00020\u000101*\u00020\u00022\u0006\u0010;\u001a\u00020&2\b\b\u0002\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010=\u001a\u00020\u0006H\u0002¢\u0006\u0002\b>\u001a4\u0010@\u001a\u00020\r*\u00020\u00022\u0006\u0010A\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\u00022\u0006\u0010B\u001a\u00020\u00062\u0006\u00107\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\rH\u0000\u001a\u0012\u0010C\u001a\u00020\u0002*\u00020\u00022\u0006\u0010D\u001a\u00020\u0002\u001a\u0012\u0010C\u001a\u00020\n*\u00020\n2\u0006\u0010D\u001a\u00020\u0002\u001a\u001a\u0010E\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u00062\u0006\u0010!\u001a\u00020\u0006\u001a\u0012\u0010E\u001a\u00020\u0002*\u00020\u00022\u0006\u0010F\u001a\u00020\u0001\u001a\u001d\u0010E\u001a\u00020\n*\u00020\n2\u0006\u0010\u001a\u001a\u00020\u00062\u0006\u0010!\u001a\u00020\u0006H\b\u001a\u0015\u0010E\u001a\u00020\n*\u00020\n2\u0006\u0010F\u001a\u00020\u0001H\b\u001a\u0012\u0010G\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u0015\u001a\u00020\u0002\u001a\u0012\u0010G\u001a\u00020\n*\u00020\n2\u0006\u0010\u0015\u001a\u00020\u0002\u001a\u0012\u0010H\u001a\u00020\u0002*\u00020\u00022\u0006\u0010I\u001a\u00020\u0002\u001a\u001a\u0010H\u001a\u00020\u0002*\u00020\u00022\u0006\u0010D\u001a\u00020\u00022\u0006\u0010\u0015\u001a\u00020\u0002\u001a\u0012\u0010H\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\u0002\u001a\u001a\u0010H\u001a\u00020\n*\u00020\n2\u0006\u0010D\u001a\u00020\u00022\u0006\u0010\u0015\u001a\u00020\u0002\u001a+\u0010J\u001a\u00020\n*\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u00132\u0014\b\b\u0010K\u001a\u000e\u0012\u0004\u0012\u00020M\u0012\u0004\u0012\u00020\u00020LH\b\u001a\u001d\u0010J\u001a\u00020\n*\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010N\u001a\u00020\nH\b\u001a$\u0010O\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\u00112\u0006\u0010N\u001a\u00020\n2\b\b\u0002\u0010P\u001a\u00020\n\u001a$\u0010O\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\n2\u0006\u0010N\u001a\u00020\n2\b\b\u0002\u0010P\u001a\u00020\n\u001a$\u0010Q\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\u00112\u0006\u0010N\u001a\u00020\n2\b\b\u0002\u0010P\u001a\u00020\n\u001a$\u0010Q\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\n2\u0006\u0010N\u001a\u00020\n2\b\b\u0002\u0010P\u001a\u00020\n\u001a$\u0010R\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\u00112\u0006\u0010N\u001a\u00020\n2\b\b\u0002\u0010P\u001a\u00020\n\u001a$\u0010R\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\n2\u0006\u0010N\u001a\u00020\n2\b\b\u0002\u0010P\u001a\u00020\n\u001a$\u0010S\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\u00112\u0006\u0010N\u001a\u00020\n2\b\b\u0002\u0010P\u001a\u00020\n\u001a$\u0010S\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\n2\u0006\u0010N\u001a\u00020\n2\b\b\u0002\u0010P\u001a\u00020\n\u001a\u001d\u0010T\u001a\u00020\n*\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010N\u001a\u00020\nH\b\u001a\"\u0010U\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u00062\u0006\u0010!\u001a\u00020\u00062\u0006\u0010N\u001a\u00020\u0002\u001a\u001a\u0010U\u001a\u00020\u0002*\u00020\u00022\u0006\u0010F\u001a\u00020\u00012\u0006\u0010N\u001a\u00020\u0002\u001a%\u0010U\u001a\u00020\n*\u00020\n2\u0006\u0010\u001a\u001a\u00020\u00062\u0006\u0010!\u001a\u00020\u00062\u0006\u0010N\u001a\u00020\u0002H\b\u001a\u001d\u0010U\u001a\u00020\n*\u00020\n2\u0006\u0010F\u001a\u00020\u00012\u0006\u0010N\u001a\u00020\u0002H\b\u001a=\u0010V\u001a\b\u0012\u0004\u0012\u00020\n03*\u00020\u00022\u0012\u0010;\u001a\n\u0012\u0006\b\u0001\u0012\u00020\n0<\"\u00020\n2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010=\u001a\u00020\u0006¢\u0006\u0002\u0010W\u001a0\u0010V\u001a\b\u0012\u0004\u0012\u00020\n03*\u00020\u00022\n\u0010;\u001a\u00020&\"\u00020\u00112\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010=\u001a\u00020\u0006\u001a/\u0010V\u001a\b\u0012\u0004\u0012\u00020\n03*\u00020\u00022\u0006\u0010I\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010=\u001a\u00020\u0006H\u0002¢\u0006\u0002\bX\u001a%\u0010V\u001a\b\u0012\u0004\u0012\u00020\n03*\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u00132\b\b\u0002\u0010=\u001a\u00020\u0006H\b\u001a=\u0010Y\u001a\b\u0012\u0004\u0012\u00020\n01*\u00020\u00022\u0012\u0010;\u001a\n\u0012\u0006\b\u0001\u0012\u00020\n0<\"\u00020\n2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010=\u001a\u00020\u0006¢\u0006\u0002\u0010Z\u001a0\u0010Y\u001a\b\u0012\u0004\u0012\u00020\n01*\u00020\u00022\n\u0010;\u001a\u00020&\"\u00020\u00112\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010=\u001a\u00020\u0006\u001a\u001c\u0010[\u001a\u00020\r*\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\f\u001a\u00020\r\u001a\u001c\u0010[\u001a\u00020\r*\u00020\u00022\u0006\u0010D\u001a\u00020\u00022\b\b\u0002\u0010\f\u001a\u00020\r\u001a$\u0010[\u001a\u00020\r*\u00020\u00022\u0006\u0010D\u001a\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r\u001a\u0012\u0010\\\u001a\u00020\u0002*\u00020\u00022\u0006\u0010F\u001a\u00020\u0001\u001a\u001d\u0010\\\u001a\u00020\u0002*\u00020\n2\u0006\u0010]\u001a\u00020\u00062\u0006\u0010^\u001a\u00020\u0006H\b\u001a\u001f\u0010_\u001a\u00020\n*\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010!\u001a\u00020\u0006H\b\u001a\u0012\u0010_\u001a\u00020\n*\u00020\u00022\u0006\u0010F\u001a\u00020\u0001\u001a\u0012\u0010_\u001a\u00020\n*\u00020\n2\u0006\u0010F\u001a\u00020\u0001\u001a\u001c\u0010`\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\u00112\b\b\u0002\u0010P\u001a\u00020\n\u001a\u001c\u0010`\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\n2\b\b\u0002\u0010P\u001a\u00020\n\u001a\u001c\u0010a\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\u00112\b\b\u0002\u0010P\u001a\u00020\n\u001a\u001c\u0010a\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\n2\b\b\u0002\u0010P\u001a\u00020\n\u001a\u001c\u0010b\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\u00112\b\b\u0002\u0010P\u001a\u00020\n\u001a\u001c\u0010b\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\n2\b\b\u0002\u0010P\u001a\u00020\n\u001a\u001c\u0010c\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\u00112\b\b\u0002\u0010P\u001a\u00020\n\u001a\u001c\u0010c\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\n2\b\b\u0002\u0010P\u001a\u00020\n\u001a\n\u0010d\u001a\u00020\u0002*\u00020\u0002\u001a!\u0010d\u001a\u00020\u0002*\u00020\u00022\u0012\u0010e\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\r0LH\b\u001a\u0016\u0010d\u001a\u00020\u0002*\u00020\u00022\n\u0010%\u001a\u00020&\"\u00020\u0011\u001a\r\u0010d\u001a\u00020\n*\u00020\nH\b\u001a!\u0010d\u001a\u00020\n*\u00020\n2\u0012\u0010e\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\r0LH\b\u001a\u0016\u0010d\u001a\u00020\n*\u00020\n2\n\u0010%\u001a\u00020&\"\u00020\u0011\u001a\n\u0010f\u001a\u00020\u0002*\u00020\u0002\u001a!\u0010f\u001a\u00020\u0002*\u00020\u00022\u0012\u0010e\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\r0LH\b\u001a\u0016\u0010f\u001a\u00020\u0002*\u00020\u00022\n\u0010%\u001a\u00020&\"\u00020\u0011\u001a\r\u0010f\u001a\u00020\n*\u00020\nH\b\u001a!\u0010f\u001a\u00020\n*\u00020\n2\u0012\u0010e\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\r0LH\b\u001a\u0016\u0010f\u001a\u00020\n*\u00020\n2\n\u0010%\u001a\u00020&\"\u00020\u0011\u001a\n\u0010g\u001a\u00020\u0002*\u00020\u0002\u001a!\u0010g\u001a\u00020\u0002*\u00020\u00022\u0012\u0010e\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\r0LH\b\u001a\u0016\u0010g\u001a\u00020\u0002*\u00020\u00022\n\u0010%\u001a\u00020&\"\u00020\u0011\u001a\r\u0010g\u001a\u00020\n*\u00020\nH\b\u001a!\u0010g\u001a\u00020\n*\u00020\n2\u0012\u0010e\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\r0LH\b\u001a\u0016\u0010g\u001a\u00020\n*\u00020\n2\n\u0010%\u001a\u00020&\"\u00020\u0011\"\u0015\u0010\u0000\u001a\u00020\u0001*\u00020\u00028F¢\u0006\u0006\u001a\u0004\b\u0003\u0010\u0004\"\u0015\u0010\u0005\u001a\u00020\u0006*\u00020\u00028F¢\u0006\u0006\u001a\u0004\b\u0007\u0010\b¨\u0006h"}, d2 = {"indices", "Lkotlin/ranges/IntRange;", "", "getIndices", "(Ljava/lang/CharSequence;)Lkotlin/ranges/IntRange;", "lastIndex", "", "getLastIndex", "(Ljava/lang/CharSequence;)I", "commonPrefixWith", "", "other", "ignoreCase", "", "commonSuffixWith", "contains", "char", "", "regex", "Lkotlin/text/Regex;", "endsWith", "suffix", "findAnyOf", "Lkotlin/Pair;", "strings", "", "startIndex", "last", "findAnyOf$StringsKt__StringsKt", "findLastAnyOf", "hasSurrogatePairAt", "index", "indexOf", "endIndex", "indexOf$StringsKt__StringsKt", "string", "indexOfAny", "chars", "", "isEmpty", "isNotBlank", "isNotEmpty", "isNullOrBlank", "isNullOrEmpty", "iterator", "Lkotlin/collections/CharIterator;", "lastIndexOf", "lastIndexOfAny", "lineSequence", "Lkotlin/sequences/Sequence;", "lines", "", "matches", "orEmpty", "padEnd", "length", "padChar", "padStart", "rangesDelimitedBy", "delimiters", "", "limit", "rangesDelimitedBy$StringsKt__StringsKt", "(Ljava/lang/CharSequence;[Ljava/lang/String;IZI)Lkotlin/sequences/Sequence;", "regionMatchesImpl", "thisOffset", "otherOffset", "removePrefix", "prefix", "removeRange", "range", "removeSuffix", "removeSurrounding", "delimiter", "replace", "transform", "Lkotlin/Function1;", "Lkotlin/text/MatchResult;", "replacement", "replaceAfter", "missingDelimiterValue", "replaceAfterLast", "replaceBefore", "replaceBeforeLast", "replaceFirst", "replaceRange", "split", "(Ljava/lang/CharSequence;[Ljava/lang/String;ZI)Ljava/util/List;", "split$StringsKt__StringsKt", "splitToSequence", "(Ljava/lang/CharSequence;[Ljava/lang/String;ZI)Lkotlin/sequences/Sequence;", "startsWith", "subSequence", "start", "end", "substring", "substringAfter", "substringAfterLast", "substringBefore", "substringBeforeLast", "trim", "predicate", "trimEnd", "trimStart", "kotlin-stdlib"}, k = 5, mv = {1, 1, 10}, xi = 1, xs = "kotlin/text/StringsKt")
/* compiled from: Strings.kt */
class StringsKt__StringsKt extends StringsKt__StringsJVMKt {
    @org.jetbrains.annotations.NotNull
    public static final java.lang.CharSequence padEnd(@org.jetbrains.annotations.NotNull java.lang.CharSequence r3, int r4, char r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x0052 in {5, 10, 12, 14} preds:[]
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
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r3, r0);
        if (r4 < 0) goto L_0x0034;
    L_0x0007:
        r0 = r3.length();
        if (r4 > r0) goto L_0x0017;
    L_0x000d:
        r0 = 0;
        r1 = r3.length();
        r0 = r3.subSequence(r0, r1);
        return r0;
    L_0x0017:
        r0 = new java.lang.StringBuilder;
        r0.<init>(r4);
        r0.append(r3);
        r1 = r3.length();
        r1 = r4 - r1;
        r2 = 1;
        if (r2 > r1) goto L_0x0030;
    L_0x0028:
        r0.append(r5);
        if (r2 == r1) goto L_0x0030;
    L_0x002d:
        r2 = r2 + 1;
        goto L_0x0028;
    L_0x0030:
        r1 = r0;
        r1 = (java.lang.CharSequence) r1;
        return r1;
    L_0x0034:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Desired length ";
        r1.append(r2);
        r1.append(r4);
        r2 = " is less than zero.";
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        r0 = (java.lang.Throwable) r0;
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.text.StringsKt__StringsKt.padEnd(java.lang.CharSequence, int, char):java.lang.CharSequence");
    }

    @org.jetbrains.annotations.NotNull
    public static final java.lang.CharSequence padStart(@org.jetbrains.annotations.NotNull java.lang.CharSequence r3, int r4, char r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:15:0x0052 in {5, 10, 12, 14} preds:[]
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
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r3, r0);
        if (r4 < 0) goto L_0x0034;
    L_0x0007:
        r0 = r3.length();
        if (r4 > r0) goto L_0x0017;
    L_0x000d:
        r0 = 0;
        r1 = r3.length();
        r0 = r3.subSequence(r0, r1);
        return r0;
    L_0x0017:
        r0 = new java.lang.StringBuilder;
        r0.<init>(r4);
        r1 = r3.length();
        r1 = r4 - r1;
        r2 = 1;
        if (r2 > r1) goto L_0x002d;
    L_0x0025:
        r0.append(r5);
        if (r2 == r1) goto L_0x002d;
    L_0x002a:
        r2 = r2 + 1;
        goto L_0x0025;
    L_0x002d:
        r0.append(r3);
        r1 = r0;
        r1 = (java.lang.CharSequence) r1;
        return r1;
    L_0x0034:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Desired length ";
        r1.append(r2);
        r1.append(r4);
        r2 = " is less than zero.";
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        r0 = (java.lang.Throwable) r0;
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.text.StringsKt__StringsKt.padStart(java.lang.CharSequence, int, char):java.lang.CharSequence");
    }

    private static final java.util.List<java.lang.String> split$StringsKt__StringsKt(@org.jetbrains.annotations.NotNull java.lang.CharSequence r7, java.lang.String r8, boolean r9, int r10) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:29:0x008a in {2, 3, 8, 10, 13, 19, 23, 24, 26, 28} preds:[]
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
        r0 = 0;
        r1 = 1;
        if (r10 < 0) goto L_0x0006;
    L_0x0004:
        r2 = 1;
        goto L_0x0007;
    L_0x0006:
        r2 = 0;
    L_0x0007:
        if (r2 == 0) goto L_0x0067;
    L_0x0009:
        r2 = 0;
        r3 = kotlin.text.StringsKt.indexOf(r7, r8, r2, r9);
        r4 = -1;
        if (r3 == r4) goto L_0x005d;
    L_0x0011:
        if (r10 != r1) goto L_0x0014;
    L_0x0013:
        goto L_0x005d;
    L_0x0014:
        if (r10 <= 0) goto L_0x0018;
    L_0x0016:
        r0 = 1;
    L_0x0018:
        r1 = new java.util.ArrayList;
        r5 = 10;
        if (r0 == 0) goto L_0x0023;
    L_0x001e:
        r5 = kotlin.ranges.RangesKt.coerceAtMost(r10, r5);
    L_0x0023:
        r1.<init>(r5);
        r5 = r7.subSequence(r2, r3);
        r5 = r5.toString();
        r1.add(r5);
        r5 = r8.length();
        r2 = r3 + r5;
        if (r0 == 0) goto L_0x0043;
    L_0x003a:
        r5 = r1.size();
        r6 = r10 + -1;
        if (r5 != r6) goto L_0x0043;
    L_0x0042:
        goto L_0x0049;
    L_0x0043:
        r3 = kotlin.text.StringsKt.indexOf(r7, r8, r2, r9);
        if (r3 != r4) goto L_0x005c;
    L_0x0049:
        r4 = r7.length();
        r4 = r7.subSequence(r2, r4);
        r4 = r4.toString();
        r1.add(r4);
        r4 = r1;
        r4 = (java.util.List) r4;
        return r4;
    L_0x005c:
        goto L_0x0026;
        r0 = r7.toString();
        r0 = kotlin.collections.CollectionsKt.listOf(r0);
        return r0;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Limit must be non-negative, but was ";
        r1.append(r2);
        r1.append(r10);
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
        throw new UnsupportedOperationException("Method not decompiled: kotlin.text.StringsKt__StringsKt.split$StringsKt__StringsKt(java.lang.CharSequence, java.lang.String, boolean, int):java.util.List<java.lang.String>");
    }

    @NotNull
    public static final CharSequence trim(@NotNull CharSequence $receiver, @NotNull Function1<? super Character, Boolean> predicate) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        int startIndex = 0;
        int endIndex = $receiver.length() - 1;
        boolean startFound = false;
        while (startIndex <= endIndex) {
            boolean match = ((Boolean) predicate.invoke(Character.valueOf($receiver.charAt(!startFound ? startIndex : endIndex)))).booleanValue();
            if (startFound) {
                if (!match) {
                    break;
                }
                endIndex--;
            } else if (match) {
                startIndex++;
            } else {
                startFound = true;
            }
        }
        return $receiver.subSequence(startIndex, endIndex + 1);
    }

    @NotNull
    public static final String trim(@NotNull String $receiver, @NotNull Function1<? super Character, Boolean> predicate) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        CharSequence $receiver$iv = $receiver;
        int startIndex$iv = 0;
        int endIndex$iv = $receiver$iv.length() - 1;
        boolean startFound$iv = false;
        while (startIndex$iv <= endIndex$iv) {
            boolean match$iv = ((Boolean) predicate.invoke(Character.valueOf($receiver$iv.charAt(!startFound$iv ? startIndex$iv : endIndex$iv)))).booleanValue();
            if (startFound$iv) {
                if (!match$iv) {
                    break;
                }
                endIndex$iv--;
            } else if (match$iv) {
                startIndex$iv++;
            } else {
                startFound$iv = true;
            }
        }
        return $receiver$iv.subSequence(startIndex$iv, endIndex$iv + 1).toString();
    }

    @NotNull
    public static final CharSequence trimStart(@NotNull CharSequence $receiver, @NotNull Function1<? super Character, Boolean> predicate) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        int length = $receiver.length();
        for (int index = 0; index < length; index++) {
            if (!((Boolean) predicate.invoke(Character.valueOf($receiver.charAt(index)))).booleanValue()) {
                return $receiver.subSequence(index, $receiver.length());
            }
        }
        return "";
    }

    @NotNull
    public static final String trimStart(@NotNull String $receiver, @NotNull Function1<? super Character, Boolean> predicate) {
        Object subSequence;
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        CharSequence $receiver$iv = $receiver;
        int index$iv = 0;
        int $i$f$trimStart = 0;
        int length = $receiver$iv.length();
        while (index$iv < length) {
            if (!((Boolean) predicate.invoke(Character.valueOf($receiver$iv.charAt(index$iv)))).booleanValue()) {
                subSequence = $receiver$iv.subSequence(index$iv, $receiver$iv.length());
                break;
            }
            index$iv++;
        }
        subSequence = "";
        return subSequence.toString();
    }

    @NotNull
    public static final CharSequence trimEnd(@NotNull CharSequence $receiver, @NotNull Function1<? super Character, Boolean> predicate) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        int index = $receiver.length();
        while (true) {
            index--;
            if (index < 0) {
                return "";
            }
            if (!((Boolean) predicate.invoke(Character.valueOf($receiver.charAt(index)))).booleanValue()) {
                return $receiver.subSequence(0, index + 1);
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String trimEnd(@org.jetbrains.annotations.NotNull java.lang.String r6, @org.jetbrains.annotations.NotNull kotlin.jvm.functions.Function1<? super java.lang.Character, java.lang.Boolean> r7) {
        /*
        r0 = 0;
        r1 = "$receiver";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r6, r1);
        r1 = "predicate";
        kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r7, r1);
        r1 = r6;
        r1 = (java.lang.CharSequence) r1;
        r2 = 0;
        r3 = r2;
        r4 = r1.length();
    L_0x0014:
        r4 = r4 + -1;
        if (r4 < 0) goto L_0x0035;
    L_0x0018:
        r5 = r1.charAt(r4);
        r5 = java.lang.Character.valueOf(r5);
        r5 = r7.invoke(r5);
        r5 = (java.lang.Boolean) r5;
        r5 = r5.booleanValue();
        if (r5 != 0) goto L_0x0033;
    L_0x002c:
        r5 = r4 + 1;
        r2 = r1.subSequence(r2, r5);
        goto L_0x0039;
        goto L_0x0014;
    L_0x0035:
        r2 = "";
        r2 = (java.lang.CharSequence) r2;
    L_0x0039:
        r1 = r2.toString();
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.text.StringsKt__StringsKt.trimEnd(java.lang.String, kotlin.jvm.functions.Function1):java.lang.String");
    }

    @NotNull
    public static final CharSequence trim(@NotNull CharSequence $receiver, @NotNull char... chars) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(chars, "chars");
        CharSequence $receiver$iv = $receiver;
        int $i$f$trim = 0;
        int startIndex$iv = 0;
        int endIndex$iv = $receiver$iv.length() - 1;
        boolean startFound$iv = false;
        while (startIndex$iv <= endIndex$iv) {
            boolean match$iv = ArraysKt.contains(chars, $receiver$iv.charAt(!startFound$iv ? startIndex$iv : endIndex$iv));
            if (startFound$iv) {
                if (!match$iv) {
                    break;
                }
                endIndex$iv--;
            } else if (match$iv) {
                startIndex$iv++;
            } else {
                startFound$iv = true;
            }
        }
        return $receiver$iv.subSequence(startIndex$iv, endIndex$iv + 1);
    }

    @NotNull
    public static final String trim(@NotNull String $receiver, @NotNull char... chars) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(chars, "chars");
        int $i$f$trim = 0;
        CharSequence $receiver$iv$iv = $receiver;
        int $i$f$trim2 = 0;
        int startIndex$iv$iv = 0;
        int endIndex$iv$iv = $receiver$iv$iv.length() - 1;
        boolean startFound$iv$iv = false;
        while (startIndex$iv$iv <= endIndex$iv$iv) {
            boolean match$iv$iv = ArraysKt.contains(chars, $receiver$iv$iv.charAt(!startFound$iv$iv ? startIndex$iv$iv : endIndex$iv$iv));
            if (startFound$iv$iv) {
                if (!match$iv$iv) {
                    break;
                }
                endIndex$iv$iv--;
            } else if (match$iv$iv) {
                startIndex$iv$iv++;
            } else {
                startFound$iv$iv = true;
            }
        }
        return $receiver$iv$iv.subSequence(startIndex$iv$iv, endIndex$iv$iv + 1).toString();
    }

    @NotNull
    public static final CharSequence trimStart(@NotNull CharSequence $receiver, @NotNull char... chars) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(chars, "chars");
        CharSequence $receiver$iv = $receiver;
        int index$iv = 0;
        int $i$f$trimStart = 0;
        int length = $receiver$iv.length();
        while (index$iv < length) {
            if (ArraysKt.contains(chars, $receiver$iv.charAt(index$iv)) == '\u0000') {
                return $receiver$iv.subSequence(index$iv, $receiver$iv.length());
            }
            index$iv++;
        }
        return "";
    }

    @NotNull
    public static final String trimStart(@NotNull String $receiver, @NotNull char... chars) {
        Object subSequence;
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(chars, "chars");
        int index$iv$iv = 0;
        int $i$f$trimStart = 0;
        CharSequence $receiver$iv$iv = $receiver;
        int $i$f$trimStart2 = 0;
        int length = $receiver$iv$iv.length();
        while (index$iv$iv < length) {
            if (ArraysKt.contains(chars, $receiver$iv$iv.charAt(index$iv$iv)) == '\u0000') {
                subSequence = $receiver$iv$iv.subSequence(index$iv$iv, $receiver$iv$iv.length());
                break;
            }
            index$iv$iv++;
        }
        subSequence = "";
        return subSequence.toString();
    }

    @NotNull
    public static final CharSequence trimEnd(@NotNull CharSequence $receiver, @NotNull char... chars) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(chars, "chars");
        CharSequence $receiver$iv = $receiver;
        int $i$f$trimEnd = 0;
        for (int index$iv = $receiver$iv.length() - 1; index$iv >= 0; index$iv--) {
            if (ArraysKt.contains(chars, $receiver$iv.charAt(index$iv)) == '\u0000') {
                return $receiver$iv.subSequence(0, index$iv + 1);
            }
        }
        return "";
    }

    @NotNull
    public static final String trimEnd(@NotNull String $receiver, @NotNull char... chars) {
        Object subSequence;
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(chars, "chars");
        int $i$f$trimEnd = 0;
        CharSequence $receiver$iv$iv = $receiver;
        int $i$f$trimEnd2 = 0;
        for (int index$iv$iv = $receiver$iv$iv.length() - 1; index$iv$iv >= 0; index$iv$iv--) {
            if (ArraysKt.contains(chars, $receiver$iv$iv.charAt(index$iv$iv)) == '\u0000') {
                subSequence = $receiver$iv$iv.subSequence(0, index$iv$iv + 1);
                break;
            }
        }
        subSequence = "";
        return subSequence.toString();
    }

    @NotNull
    public static final CharSequence trim(@NotNull CharSequence $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        CharSequence $receiver$iv = $receiver;
        int $i$f$trim = 0;
        int startIndex$iv = 0;
        int endIndex$iv = $receiver$iv.length() - 1;
        boolean startFound$iv = false;
        while (startIndex$iv <= endIndex$iv) {
            boolean match$iv = CharsKt.isWhitespace($receiver$iv.charAt(!startFound$iv ? startIndex$iv : endIndex$iv));
            if (startFound$iv) {
                if (!match$iv) {
                    break;
                }
                endIndex$iv--;
            } else if (match$iv) {
                startIndex$iv++;
            } else {
                startFound$iv = true;
            }
        }
        return $receiver$iv.subSequence(startIndex$iv, endIndex$iv + 1);
    }

    @InlineOnly
    private static final String trim(@NotNull String $receiver) {
        if ($receiver != null) {
            return StringsKt.trim($receiver).toString();
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
    }

    @NotNull
    public static final CharSequence trimStart(@NotNull CharSequence $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        CharSequence $receiver$iv = $receiver;
        int index$iv = 0;
        int $i$f$trimStart = 0;
        int length = $receiver$iv.length();
        while (index$iv < length) {
            if (CharsKt.isWhitespace($receiver$iv.charAt(index$iv)) == '\u0000') {
                return $receiver$iv.subSequence(index$iv, $receiver$iv.length());
            }
            index$iv++;
        }
        return "";
    }

    @InlineOnly
    private static final String trimStart(@NotNull String $receiver) {
        if ($receiver != null) {
            return StringsKt.trimStart($receiver).toString();
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
    }

    @NotNull
    public static final CharSequence trimEnd(@NotNull CharSequence $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        CharSequence $receiver$iv = $receiver;
        int $i$f$trimEnd = 0;
        for (int index$iv = $receiver$iv.length() - 1; index$iv >= 0; index$iv--) {
            if (CharsKt.isWhitespace($receiver$iv.charAt(index$iv)) == '\u0000') {
                return $receiver$iv.subSequence(0, index$iv + 1);
            }
        }
        return "";
    }

    @InlineOnly
    private static final String trimEnd(@NotNull String $receiver) {
        if ($receiver != null) {
            return StringsKt.trimEnd($receiver).toString();
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
    }

    @NotNull
    public static final String padStart(@NotNull String $receiver, int length, char padChar) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return StringsKt.padStart($receiver, length, padChar).toString();
    }

    @NotNull
    public static final String padEnd(@NotNull String $receiver, int length, char padChar) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return StringsKt.padEnd($receiver, length, padChar).toString();
    }

    @InlineOnly
    private static final boolean isNullOrEmpty(@Nullable CharSequence $receiver) {
        if ($receiver != null) {
            if ($receiver.length() != 0) {
                return false;
            }
        }
        return true;
    }

    @InlineOnly
    private static final boolean isEmpty(@NotNull CharSequence $receiver) {
        return $receiver.length() == 0;
    }

    @InlineOnly
    private static final boolean isNotEmpty(@NotNull CharSequence $receiver) {
        return $receiver.length() > 0;
    }

    @InlineOnly
    private static final boolean isNotBlank(@NotNull CharSequence $receiver) {
        return StringsKt.isBlank($receiver) ^ 1;
    }

    @InlineOnly
    private static final boolean isNullOrBlank(@Nullable CharSequence $receiver) {
        if ($receiver != null) {
            if (!StringsKt.isBlank($receiver)) {
                return false;
            }
        }
        return true;
    }

    @NotNull
    public static final CharIterator iterator(@NotNull CharSequence $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return new StringsKt__StringsKt$iterator$1($receiver);
    }

    @InlineOnly
    private static final String orEmpty(@Nullable String $receiver) {
        return $receiver != null ? $receiver : "";
    }

    @NotNull
    public static final IntRange getIndices(@NotNull CharSequence $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return new IntRange(0, $receiver.length() - 1);
    }

    public static final int getLastIndex(@NotNull CharSequence $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return $receiver.length() - 1;
    }

    public static final boolean hasSurrogatePairAt(@NotNull CharSequence $receiver, int index) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        int length = $receiver.length() - 2;
        if (index >= 0) {
            if (length >= index) {
                if (Character.isHighSurrogate($receiver.charAt(index))) {
                    if (Character.isLowSurrogate($receiver.charAt(index + 1))) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    @NotNull
    public static final String substring(@NotNull String $receiver, @NotNull IntRange range) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(range, "range");
        String substring = $receiver.substring(range.getStart().intValue(), range.getEndInclusive().intValue() + 1);
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
        return substring;
    }

    @NotNull
    public static final CharSequence subSequence(@NotNull CharSequence $receiver, @NotNull IntRange range) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(range, "range");
        return $receiver.subSequence(range.getStart().intValue(), range.getEndInclusive().intValue() + 1);
    }

    @Deprecated(message = "Use parameters named startIndex and endIndex.", replaceWith = @ReplaceWith(expression = "subSequence(startIndex = start, endIndex = end)", imports = {}))
    @InlineOnly
    private static final CharSequence subSequence(@NotNull String $receiver, int start, int end) {
        return $receiver.subSequence(start, end);
    }

    @InlineOnly
    private static final String substring(@NotNull CharSequence $receiver, int startIndex, int endIndex) {
        return $receiver.subSequence(startIndex, endIndex).toString();
    }

    @InlineOnly
    static /* bridge */ /* synthetic */ String substring$default(CharSequence $receiver, int startIndex, int endIndex, int i, Object obj) {
        if ((i & 2) != null) {
            endIndex = $receiver.length();
        }
        return $receiver.subSequence(startIndex, endIndex).toString();
    }

    @NotNull
    public static final String substring(@NotNull CharSequence $receiver, @NotNull IntRange range) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(range, "range");
        return $receiver.subSequence(range.getStart().intValue(), range.getEndInclusive().intValue() + 1).toString();
    }

    @NotNull
    public static final String substringBefore(@NotNull String $receiver, char delimiter, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.indexOf$default($receiver, delimiter, 0, false, 6, null);
        if (index == -1) {
            return missingDelimiterValue;
        }
        String substring = $receiver.substring(0, index);
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
        return substring;
    }

    @NotNull
    public static final String substringBefore(@NotNull String $receiver, @NotNull String delimiter, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiter, "delimiter");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.indexOf$default($receiver, delimiter, 0, false, 6, null);
        if (index == -1) {
            return missingDelimiterValue;
        }
        String substring = $receiver.substring(0, index);
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
        return substring;
    }

    @NotNull
    public static final String substringAfter(@NotNull String $receiver, char delimiter, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.indexOf$default($receiver, delimiter, 0, false, 6, null);
        if (index == -1) {
            return missingDelimiterValue;
        }
        String substring = $receiver.substring(index + 1, $receiver.length());
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
        return substring;
    }

    @NotNull
    public static final String substringAfter(@NotNull String $receiver, @NotNull String delimiter, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiter, "delimiter");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.indexOf$default($receiver, delimiter, 0, false, 6, null);
        if (index == -1) {
            return missingDelimiterValue;
        }
        String substring = $receiver.substring(delimiter.length() + index, $receiver.length());
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
        return substring;
    }

    @NotNull
    public static final String substringBeforeLast(@NotNull String $receiver, char delimiter, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.lastIndexOf$default($receiver, delimiter, 0, false, 6, null);
        if (index == -1) {
            return missingDelimiterValue;
        }
        String substring = $receiver.substring(0, index);
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
        return substring;
    }

    @NotNull
    public static final String substringBeforeLast(@NotNull String $receiver, @NotNull String delimiter, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiter, "delimiter");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.lastIndexOf$default($receiver, delimiter, 0, false, 6, null);
        if (index == -1) {
            return missingDelimiterValue;
        }
        String substring = $receiver.substring(0, index);
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
        return substring;
    }

    @NotNull
    public static final String substringAfterLast(@NotNull String $receiver, char delimiter, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.lastIndexOf$default($receiver, delimiter, 0, false, 6, null);
        if (index == -1) {
            return missingDelimiterValue;
        }
        String substring = $receiver.substring(index + 1, $receiver.length());
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
        return substring;
    }

    @NotNull
    public static final String substringAfterLast(@NotNull String $receiver, @NotNull String delimiter, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiter, "delimiter");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.lastIndexOf$default($receiver, delimiter, 0, false, 6, null);
        if (index == -1) {
            return missingDelimiterValue;
        }
        String substring = $receiver.substring(delimiter.length() + index, $receiver.length());
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
        return substring;
    }

    @NotNull
    public static final CharSequence replaceRange(@NotNull CharSequence $receiver, int startIndex, int endIndex, @NotNull CharSequence replacement) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(replacement, "replacement");
        if (endIndex >= startIndex) {
            StringBuilder sb = new StringBuilder();
            sb.append($receiver, 0, startIndex);
            sb.append(replacement);
            sb.append($receiver, endIndex, $receiver.length());
            return sb;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("End index (");
        stringBuilder.append(endIndex);
        stringBuilder.append(") is less than start index (");
        stringBuilder.append(startIndex);
        stringBuilder.append(").");
        throw new IndexOutOfBoundsException(stringBuilder.toString());
    }

    @InlineOnly
    private static final String replaceRange(@NotNull String $receiver, int startIndex, int endIndex, CharSequence replacement) {
        if ($receiver != null) {
            return StringsKt.replaceRange($receiver, startIndex, endIndex, replacement).toString();
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
    }

    @NotNull
    public static final CharSequence replaceRange(@NotNull CharSequence $receiver, @NotNull IntRange range, @NotNull CharSequence replacement) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(range, "range");
        Intrinsics.checkParameterIsNotNull(replacement, "replacement");
        return StringsKt.replaceRange($receiver, range.getStart().intValue(), range.getEndInclusive().intValue() + 1, replacement);
    }

    @InlineOnly
    private static final String replaceRange(@NotNull String $receiver, IntRange range, CharSequence replacement) {
        if ($receiver != null) {
            return StringsKt.replaceRange($receiver, range, replacement).toString();
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
    }

    @NotNull
    public static final CharSequence removeRange(@NotNull CharSequence $receiver, int startIndex, int endIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        StringBuilder stringBuilder;
        if (endIndex < startIndex) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("End index (");
            stringBuilder.append(endIndex);
            stringBuilder.append(") is less than start index (");
            stringBuilder.append(startIndex);
            stringBuilder.append(").");
            throw new IndexOutOfBoundsException(stringBuilder.toString());
        } else if (endIndex == startIndex) {
            return $receiver.subSequence(0, $receiver.length());
        } else {
            stringBuilder = new StringBuilder($receiver.length() - (endIndex - startIndex));
            stringBuilder.append($receiver, 0, startIndex);
            stringBuilder.append($receiver, endIndex, $receiver.length());
            return stringBuilder;
        }
    }

    @InlineOnly
    private static final String removeRange(@NotNull String $receiver, int startIndex, int endIndex) {
        if ($receiver != null) {
            return StringsKt.removeRange($receiver, startIndex, endIndex).toString();
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
    }

    @NotNull
    public static final CharSequence removeRange(@NotNull CharSequence $receiver, @NotNull IntRange range) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(range, "range");
        return StringsKt.removeRange($receiver, range.getStart().intValue(), range.getEndInclusive().intValue() + 1);
    }

    @InlineOnly
    private static final String removeRange(@NotNull String $receiver, IntRange range) {
        if ($receiver != null) {
            return StringsKt.removeRange($receiver, range).toString();
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
    }

    @NotNull
    public static final CharSequence removePrefix(@NotNull CharSequence $receiver, @NotNull CharSequence prefix) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(prefix, "prefix");
        if (StringsKt.startsWith$default($receiver, prefix, false, 2, null)) {
            return $receiver.subSequence(prefix.length(), $receiver.length());
        }
        return $receiver.subSequence(0, $receiver.length());
    }

    @NotNull
    public static final String removePrefix(@NotNull String $receiver, @NotNull CharSequence prefix) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(prefix, "prefix");
        if (!StringsKt.startsWith$default($receiver, prefix, false, 2, null)) {
            return $receiver;
        }
        String substring = $receiver.substring(prefix.length());
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.String).substring(startIndex)");
        return substring;
    }

    @NotNull
    public static final CharSequence removeSuffix(@NotNull CharSequence $receiver, @NotNull CharSequence suffix) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(suffix, "suffix");
        if (StringsKt.endsWith$default($receiver, suffix, false, 2, null)) {
            return $receiver.subSequence(0, $receiver.length() - suffix.length());
        }
        return $receiver.subSequence(0, $receiver.length());
    }

    @NotNull
    public static final String removeSuffix(@NotNull String $receiver, @NotNull CharSequence suffix) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(suffix, "suffix");
        if (!StringsKt.endsWith$default($receiver, suffix, false, 2, null)) {
            return $receiver;
        }
        String substring = $receiver.substring(0, $receiver.length() - suffix.length());
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
        return substring;
    }

    @NotNull
    public static final CharSequence removeSurrounding(@NotNull CharSequence $receiver, @NotNull CharSequence prefix, @NotNull CharSequence suffix) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(prefix, "prefix");
        Intrinsics.checkParameterIsNotNull(suffix, "suffix");
        if ($receiver.length() >= prefix.length() + suffix.length() && StringsKt.startsWith$default($receiver, prefix, false, 2, null) && StringsKt.endsWith$default($receiver, suffix, false, 2, null)) {
            return $receiver.subSequence(prefix.length(), $receiver.length() - suffix.length());
        }
        return $receiver.subSequence(0, $receiver.length());
    }

    @NotNull
    public static final String removeSurrounding(@NotNull String $receiver, @NotNull CharSequence prefix, @NotNull CharSequence suffix) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(prefix, "prefix");
        Intrinsics.checkParameterIsNotNull(suffix, "suffix");
        if ($receiver.length() < prefix.length() + suffix.length() || !StringsKt.startsWith$default($receiver, prefix, false, 2, null) || !StringsKt.endsWith$default($receiver, suffix, false, 2, null)) {
            return $receiver;
        }
        String substring = $receiver.substring(prefix.length(), $receiver.length() - suffix.length());
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
        return substring;
    }

    @NotNull
    public static final CharSequence removeSurrounding(@NotNull CharSequence $receiver, @NotNull CharSequence delimiter) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiter, "delimiter");
        return StringsKt.removeSurrounding($receiver, delimiter, delimiter);
    }

    @NotNull
    public static final String removeSurrounding(@NotNull String $receiver, @NotNull CharSequence delimiter) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiter, "delimiter");
        return StringsKt.removeSurrounding($receiver, delimiter, delimiter);
    }

    @NotNull
    public static final String replaceBefore(@NotNull String $receiver, char delimiter, @NotNull String replacement, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(replacement, "replacement");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.indexOf$default($receiver, delimiter, 0, false, 6, null);
        return index == -1 ? missingDelimiterValue : StringsKt.replaceRange($receiver, 0, index, replacement).toString();
    }

    @NotNull
    public static final String replaceBefore(@NotNull String $receiver, @NotNull String delimiter, @NotNull String replacement, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiter, "delimiter");
        Intrinsics.checkParameterIsNotNull(replacement, "replacement");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.indexOf$default($receiver, delimiter, 0, false, 6, null);
        return index == -1 ? missingDelimiterValue : StringsKt.replaceRange($receiver, 0, index, replacement).toString();
    }

    @NotNull
    public static final String replaceAfter(@NotNull String $receiver, char delimiter, @NotNull String replacement, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(replacement, "replacement");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.indexOf$default($receiver, delimiter, 0, false, 6, null);
        if (index == -1) {
            return missingDelimiterValue;
        }
        return StringsKt.replaceRange($receiver, index + 1, $receiver.length(), replacement).toString();
    }

    @NotNull
    public static final String replaceAfter(@NotNull String $receiver, @NotNull String delimiter, @NotNull String replacement, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiter, "delimiter");
        Intrinsics.checkParameterIsNotNull(replacement, "replacement");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.indexOf$default($receiver, delimiter, 0, false, 6, null);
        if (index == -1) {
            return missingDelimiterValue;
        }
        return StringsKt.replaceRange($receiver, delimiter.length() + index, $receiver.length(), replacement).toString();
    }

    @NotNull
    public static final String replaceAfterLast(@NotNull String $receiver, @NotNull String delimiter, @NotNull String replacement, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiter, "delimiter");
        Intrinsics.checkParameterIsNotNull(replacement, "replacement");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.lastIndexOf$default($receiver, delimiter, 0, false, 6, null);
        if (index == -1) {
            return missingDelimiterValue;
        }
        return StringsKt.replaceRange($receiver, delimiter.length() + index, $receiver.length(), replacement).toString();
    }

    @NotNull
    public static final String replaceAfterLast(@NotNull String $receiver, char delimiter, @NotNull String replacement, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(replacement, "replacement");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.lastIndexOf$default($receiver, delimiter, 0, false, 6, null);
        if (index == -1) {
            return missingDelimiterValue;
        }
        return StringsKt.replaceRange($receiver, index + 1, $receiver.length(), replacement).toString();
    }

    @NotNull
    public static final String replaceBeforeLast(@NotNull String $receiver, char delimiter, @NotNull String replacement, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(replacement, "replacement");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.lastIndexOf$default($receiver, delimiter, 0, false, 6, null);
        return index == -1 ? missingDelimiterValue : StringsKt.replaceRange($receiver, 0, index, replacement).toString();
    }

    @NotNull
    public static final String replaceBeforeLast(@NotNull String $receiver, @NotNull String delimiter, @NotNull String replacement, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiter, "delimiter");
        Intrinsics.checkParameterIsNotNull(replacement, "replacement");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.lastIndexOf$default($receiver, delimiter, 0, false, 6, null);
        return index == -1 ? missingDelimiterValue : StringsKt.replaceRange($receiver, 0, index, replacement).toString();
    }

    @InlineOnly
    private static final String replace(@NotNull CharSequence $receiver, Regex regex, String replacement) {
        return regex.replace($receiver, replacement);
    }

    @InlineOnly
    private static final String replace(@NotNull CharSequence $receiver, Regex regex, Function1<? super MatchResult, ? extends CharSequence> transform) {
        return regex.replace($receiver, transform);
    }

    @InlineOnly
    private static final String replaceFirst(@NotNull CharSequence $receiver, Regex regex, String replacement) {
        return regex.replaceFirst($receiver, replacement);
    }

    @InlineOnly
    private static final boolean matches(@NotNull CharSequence $receiver, Regex regex) {
        return regex.matches($receiver);
    }

    public static final boolean regionMatchesImpl(@NotNull CharSequence $receiver, int thisOffset, @NotNull CharSequence other, int otherOffset, int length, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(other, "other");
        if (otherOffset >= 0 && thisOffset >= 0 && thisOffset <= $receiver.length() - length) {
            if (otherOffset <= other.length() - length) {
                for (int index = 0; index < length; index++) {
                    if (!CharsKt.equals($receiver.charAt(thisOffset + index), other.charAt(otherOffset + index), ignoreCase)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static final boolean startsWith(@NotNull CharSequence $receiver, char char_, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return $receiver.length() > 0 && CharsKt.equals($receiver.charAt(0), char_, ignoreCase);
    }

    public static final boolean endsWith(@NotNull CharSequence $receiver, char char_, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return $receiver.length() > 0 && CharsKt.equals($receiver.charAt(StringsKt.getLastIndex($receiver)), char_, ignoreCase);
    }

    public static final boolean startsWith(@NotNull CharSequence $receiver, @NotNull CharSequence prefix, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(prefix, "prefix");
        if (!ignoreCase && ($receiver instanceof String) && (prefix instanceof String)) {
            return StringsKt.startsWith$default((String) $receiver, (String) prefix, false, 2, null);
        }
        return StringsKt.regionMatchesImpl($receiver, 0, prefix, 0, prefix.length(), ignoreCase);
    }

    public static /* bridge */ /* synthetic */ boolean startsWith$default(CharSequence charSequence, CharSequence charSequence2, int i, boolean z, int i2, Object obj) {
        if ((i2 & 4) != 0) {
            z = false;
        }
        return StringsKt.startsWith(charSequence, charSequence2, i, z);
    }

    public static final boolean startsWith(@NotNull CharSequence $receiver, @NotNull CharSequence prefix, int startIndex, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(prefix, "prefix");
        if (!ignoreCase && ($receiver instanceof String) && (prefix instanceof String)) {
            return StringsKt.startsWith$default((String) $receiver, (String) prefix, startIndex, false, 4, null);
        }
        return StringsKt.regionMatchesImpl($receiver, startIndex, prefix, 0, prefix.length(), ignoreCase);
    }

    public static final boolean endsWith(@NotNull CharSequence $receiver, @NotNull CharSequence suffix, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(suffix, "suffix");
        if (!ignoreCase && ($receiver instanceof String) && (suffix instanceof String)) {
            return StringsKt.endsWith$default((String) $receiver, (String) suffix, false, 2, null);
        }
        return StringsKt.regionMatchesImpl($receiver, $receiver.length() - suffix.length(), suffix, 0, suffix.length(), ignoreCase);
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String commonPrefixWith$default(CharSequence charSequence, CharSequence charSequence2, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        return StringsKt.commonPrefixWith(charSequence, charSequence2, z);
    }

    @NotNull
    public static final String commonPrefixWith(@NotNull CharSequence $receiver, @NotNull CharSequence other, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(other, "other");
        int shortestLength = Math.min($receiver.length(), other.length());
        int i = 0;
        while (i < shortestLength && CharsKt.equals($receiver.charAt(i), other.charAt(i), ignoreCase)) {
            i++;
        }
        if (!StringsKt.hasSurrogatePairAt($receiver, i - 1)) {
            if (!StringsKt.hasSurrogatePairAt(other, i - 1)) {
                return $receiver.subSequence(0, i).toString();
            }
        }
        i--;
        return $receiver.subSequence(0, i).toString();
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String commonSuffixWith$default(CharSequence charSequence, CharSequence charSequence2, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        return StringsKt.commonSuffixWith(charSequence, charSequence2, z);
    }

    @NotNull
    public static final String commonSuffixWith(@NotNull CharSequence $receiver, @NotNull CharSequence other, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(other, "other");
        int thisLength = $receiver.length();
        int otherLength = other.length();
        int shortestLength = Math.min(thisLength, otherLength);
        int i = 0;
        while (i < shortestLength && CharsKt.equals($receiver.charAt((thisLength - i) - 1), other.charAt((otherLength - i) - 1), ignoreCase)) {
            i++;
        }
        if (!StringsKt.hasSurrogatePairAt($receiver, (thisLength - i) - 1)) {
            if (!StringsKt.hasSurrogatePairAt(other, (otherLength - i) - 1)) {
                return $receiver.subSequence(thisLength - i, thisLength).toString();
            }
        }
        i--;
        return $receiver.subSequence(thisLength - i, thisLength).toString();
    }

    public static final int indexOfAny(@NotNull CharSequence $receiver, @NotNull char[] chars, int startIndex, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(chars, "chars");
        if (!ignoreCase && chars.length == 1 && ($receiver instanceof String)) {
            return ((String) $receiver).indexOf(ArraysKt.single(chars), startIndex);
        }
        int index = RangesKt.coerceAtLeast(startIndex, 0);
        int lastIndex = StringsKt.getLastIndex($receiver);
        if (index <= lastIndex) {
            Object obj = null;
            while (true) {
                char charAtIndex = $receiver.charAt(index);
                Object obj2 = obj;
                for (char element$iv : chars) {
                    if (CharsKt.equals(element$iv, charAtIndex, ignoreCase) != '\u0000') {
                        obj = 1;
                        break;
                    }
                }
                obj = null;
                if (obj == null) {
                    if (index == lastIndex) {
                        break;
                    }
                    index++;
                    obj = obj2;
                } else {
                    return index;
                }
            }
        }
        return -1;
    }

    public static final int lastIndexOfAny(@NotNull CharSequence $receiver, @NotNull char[] chars, int startIndex, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(chars, "chars");
        if (!ignoreCase && chars.length == 1 && ($receiver instanceof String)) {
            return ((String) $receiver).lastIndexOf(ArraysKt.single(chars), startIndex);
        }
        int index = RangesKt.coerceAtMost(startIndex, StringsKt.getLastIndex($receiver));
        Object obj = null;
        while (index >= 0) {
            char charAtIndex = $receiver.charAt(index);
            Object obj2 = obj;
            for (char element$iv : chars) {
                if (CharsKt.equals(element$iv, charAtIndex, ignoreCase) != '\u0000') {
                    obj = 1;
                    break;
                }
            }
            obj = null;
            if (obj != null) {
                return index;
            }
            index--;
            obj = obj2;
        }
        return -1;
    }

    static /* bridge */ /* synthetic */ int indexOf$StringsKt__StringsKt$default(CharSequence charSequence, CharSequence charSequence2, int i, int i2, boolean z, boolean z2, int i3, Object obj) {
        return indexOf$StringsKt__StringsKt(charSequence, charSequence2, i, i2, z, (i3 & 16) != 0 ? false : z2);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static final int indexOf$StringsKt__StringsKt(@org.jetbrains.annotations.NotNull java.lang.CharSequence r10, java.lang.CharSequence r11, int r12, int r13, boolean r14, boolean r15) {
        /*
        r0 = 0;
        if (r15 != 0) goto L_0x0017;
    L_0x0003:
        r0 = kotlin.ranges.RangesKt.coerceAtLeast(r12, r0);
        r1 = new kotlin.ranges.IntRange;
        r2 = r10.length();
        r2 = kotlin.ranges.RangesKt.coerceAtMost(r13, r2);
        r1.<init>(r0, r2);
        r1 = (kotlin.ranges.IntProgression) r1;
        goto L_0x0027;
    L_0x0017:
        r1 = kotlin.text.StringsKt.getLastIndex(r10);
        r1 = kotlin.ranges.RangesKt.coerceAtMost(r12, r1);
        r0 = kotlin.ranges.RangesKt.coerceAtLeast(r13, r0);
        r1 = kotlin.ranges.RangesKt.downTo(r1, r0);
    L_0x0027:
        r0 = r1;
        r1 = r10 instanceof java.lang.String;
        if (r1 == 0) goto L_0x005c;
    L_0x002c:
        r1 = r11 instanceof java.lang.String;
        if (r1 == 0) goto L_0x005c;
    L_0x0030:
        r1 = r0.getFirst();
        r2 = r0.getLast();
        r3 = r0.getStep();
        if (r3 <= 0) goto L_0x0041;
    L_0x003e:
        if (r1 > r2) goto L_0x005b;
    L_0x0040:
        goto L_0x0043;
    L_0x0041:
        if (r1 < r2) goto L_0x005b;
    L_0x0043:
        r4 = r11;
        r4 = (java.lang.String) r4;
        r5 = 0;
        r6 = r10;
        r6 = (java.lang.String) r6;
        r8 = r11.length();
        r7 = r1;
        r9 = r14;
        r4 = kotlin.text.StringsKt.regionMatches(r4, r5, r6, r7, r8, r9);
        if (r4 == 0) goto L_0x0057;
    L_0x0056:
        return r1;
    L_0x0057:
        if (r1 == r2) goto L_0x005b;
    L_0x0059:
        r1 = r1 + r3;
        goto L_0x0043;
    L_0x005b:
        goto L_0x0084;
        r1 = r0.getFirst();
        r2 = r0.getLast();
        r3 = r0.getStep();
        if (r3 <= 0) goto L_0x006e;
    L_0x006b:
        if (r1 > r2) goto L_0x0084;
    L_0x006d:
        goto L_0x0070;
    L_0x006e:
        if (r1 < r2) goto L_0x0084;
    L_0x0070:
        r5 = 0;
        r8 = r11.length();
        r4 = r11;
        r6 = r10;
        r7 = r1;
        r9 = r14;
        r4 = kotlin.text.StringsKt.regionMatchesImpl(r4, r5, r6, r7, r8, r9);
        if (r4 == 0) goto L_0x0080;
    L_0x007f:
        return r1;
    L_0x0080:
        if (r1 == r2) goto L_0x0084;
    L_0x0082:
        r1 = r1 + r3;
        goto L_0x0070;
        r1 = -1;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.text.StringsKt__StringsKt.indexOf$StringsKt__StringsKt(java.lang.CharSequence, java.lang.CharSequence, int, int, boolean, boolean):int");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static final kotlin.Pair<java.lang.Integer, java.lang.String> findAnyOf$StringsKt__StringsKt(@org.jetbrains.annotations.NotNull java.lang.CharSequence r19, java.util.Collection<java.lang.String> r20, int r21, boolean r22, boolean r23) {
        /*
        r6 = r19;
        r7 = r21;
        r8 = 0;
        if (r22 != 0) goto L_0x0040;
    L_0x0007:
        r0 = r20.size();
        r1 = 1;
        if (r0 != r1) goto L_0x0040;
    L_0x000e:
        r0 = r20;
        r0 = (java.lang.Iterable) r0;
        r0 = kotlin.collections.CollectionsKt.single(r0);
        r9 = r0;
        r9 = (java.lang.String) r9;
        if (r23 != 0) goto L_0x0028;
    L_0x001b:
        r3 = 0;
        r4 = 4;
        r5 = 0;
        r0 = r19;
        r1 = r9;
        r2 = r21;
        r0 = kotlin.text.StringsKt.indexOf$default(r0, r1, r2, r3, r4, r5);
        goto L_0x0034;
    L_0x0028:
        r3 = 0;
        r4 = 4;
        r5 = 0;
        r0 = r19;
        r1 = r9;
        r2 = r21;
        r0 = kotlin.text.StringsKt.lastIndexOf$default(r0, r1, r2, r3, r4, r5);
    L_0x0034:
        if (r0 >= 0) goto L_0x0037;
    L_0x0036:
        goto L_0x003f;
    L_0x0037:
        r1 = java.lang.Integer.valueOf(r0);
        r8 = kotlin.TuplesKt.to(r1, r9);
    L_0x003f:
        return r8;
        r0 = 0;
        if (r23 != 0) goto L_0x0054;
    L_0x0044:
        r1 = kotlin.ranges.RangesKt.coerceAtLeast(r7, r0);
        r2 = new kotlin.ranges.IntRange;
        r3 = r19.length();
        r2.<init>(r1, r3);
        r2 = (kotlin.ranges.IntProgression) r2;
        goto L_0x0060;
    L_0x0054:
        r1 = kotlin.text.StringsKt.getLastIndex(r19);
        r1 = kotlin.ranges.RangesKt.coerceAtMost(r7, r1);
        r2 = kotlin.ranges.RangesKt.downTo(r1, r0);
    L_0x0060:
        r9 = r2;
        r1 = r6 instanceof java.lang.String;
        if (r1 == 0) goto L_0x00c4;
    L_0x0065:
        r1 = r9.getFirst();
        r10 = r9.getLast();
        r11 = r9.getStep();
        if (r11 <= 0) goto L_0x0076;
    L_0x0073:
        if (r1 > r10) goto L_0x00c3;
    L_0x0075:
        goto L_0x0078;
    L_0x0076:
        if (r1 < r10) goto L_0x00c3;
    L_0x0078:
        r12 = r1;
        r1 = 0;
    L_0x007a:
        r13 = r20;
        r13 = (java.lang.Iterable) r13;
        r14 = r0;
        r15 = r13.iterator();
    L_0x0083:
        r0 = r15.hasNext();
        if (r0 == 0) goto L_0x00ac;
    L_0x0089:
        r16 = r15.next();
        r17 = r16;
        r17 = (java.lang.String) r17;
        r18 = r1;
        r1 = 0;
        r2 = r6;
        r2 = (java.lang.String) r2;
        r4 = r17.length();
        r0 = r17;
        r3 = r12;
        r5 = r22;
        r0 = kotlin.text.StringsKt.regionMatches(r0, r1, r2, r3, r4, r5);
        if (r0 == 0) goto L_0x00a9;
    L_0x00a6:
        r1 = r18;
        goto L_0x00ae;
    L_0x00a9:
        r1 = r18;
        goto L_0x0083;
    L_0x00ac:
        r16 = r8;
    L_0x00ae:
        r0 = r16;
        r0 = (java.lang.String) r0;
        if (r0 == 0) goto L_0x00bd;
    L_0x00b4:
        r1 = java.lang.Integer.valueOf(r12);
        r1 = kotlin.TuplesKt.to(r1, r0);
        return r1;
        if (r12 == r10) goto L_0x00c3;
    L_0x00c0:
        r12 = r12 + r11;
        r0 = r14;
        goto L_0x007a;
    L_0x00c3:
        goto L_0x0123;
    L_0x00c4:
        r1 = r9.getFirst();
        r10 = r9.getLast();
        r11 = r9.getStep();
        if (r11 <= 0) goto L_0x00d5;
    L_0x00d2:
        if (r1 > r10) goto L_0x0123;
    L_0x00d4:
        goto L_0x00d7;
    L_0x00d5:
        if (r1 < r10) goto L_0x0123;
    L_0x00d7:
        r12 = r1;
        r1 = 0;
    L_0x00d9:
        r13 = r20;
        r13 = (java.lang.Iterable) r13;
        r14 = r0;
        r15 = r13.iterator();
    L_0x00e2:
        r0 = r15.hasNext();
        if (r0 == 0) goto L_0x010c;
    L_0x00e8:
        r16 = r15.next();
        r17 = r16;
        r17 = (java.lang.String) r17;
        r18 = r1;
        r0 = r17;
        r0 = (java.lang.CharSequence) r0;
        r1 = 0;
        r4 = r17.length();
        r2 = r19;
        r3 = r12;
        r5 = r22;
        r0 = kotlin.text.StringsKt.regionMatchesImpl(r0, r1, r2, r3, r4, r5);
        if (r0 == 0) goto L_0x0109;
    L_0x0106:
        r1 = r18;
        goto L_0x010e;
    L_0x0109:
        r1 = r18;
        goto L_0x00e2;
    L_0x010c:
        r16 = r8;
    L_0x010e:
        r0 = r16;
        r0 = (java.lang.String) r0;
        if (r0 == 0) goto L_0x011d;
    L_0x0114:
        r1 = java.lang.Integer.valueOf(r12);
        r1 = kotlin.TuplesKt.to(r1, r0);
        return r1;
        if (r12 == r10) goto L_0x0123;
    L_0x0120:
        r12 = r12 + r11;
        r0 = r14;
        goto L_0x00d9;
        return r8;
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.text.StringsKt__StringsKt.findAnyOf$StringsKt__StringsKt(java.lang.CharSequence, java.util.Collection, int, boolean, boolean):kotlin.Pair<java.lang.Integer, java.lang.String>");
    }

    @Nullable
    public static /* bridge */ /* synthetic */ Pair findAnyOf$default(CharSequence charSequence, Collection collection, int i, boolean z, int i2, Object obj) {
        if ((i2 & 2) != null) {
            i = 0;
        }
        if ((i2 & 4) != 0) {
            z = false;
        }
        return StringsKt.findAnyOf(charSequence, collection, i, z);
    }

    @Nullable
    public static final Pair<Integer, String> findAnyOf(@NotNull CharSequence $receiver, @NotNull Collection<String> strings, int startIndex, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(strings, "strings");
        return findAnyOf$StringsKt__StringsKt($receiver, strings, startIndex, ignoreCase, false);
    }

    @Nullable
    public static /* bridge */ /* synthetic */ Pair findLastAnyOf$default(CharSequence charSequence, Collection collection, int i, boolean z, int i2, Object obj) {
        if ((i2 & 2) != null) {
            i = StringsKt.getLastIndex(charSequence);
        }
        if ((i2 & 4) != 0) {
            z = false;
        }
        return StringsKt.findLastAnyOf(charSequence, collection, i, z);
    }

    @Nullable
    public static final Pair<Integer, String> findLastAnyOf(@NotNull CharSequence $receiver, @NotNull Collection<String> strings, int startIndex, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(strings, "strings");
        return findAnyOf$StringsKt__StringsKt($receiver, strings, startIndex, ignoreCase, true);
    }

    public static final int indexOfAny(@NotNull CharSequence $receiver, @NotNull Collection<String> strings, int startIndex, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(strings, "strings");
        Pair findAnyOf$StringsKt__StringsKt = findAnyOf$StringsKt__StringsKt($receiver, strings, startIndex, ignoreCase, false);
        if (findAnyOf$StringsKt__StringsKt != null) {
            Integer num = (Integer) findAnyOf$StringsKt__StringsKt.getFirst();
            if (num != null) {
                return num.intValue();
            }
        }
        return -1;
    }

    public static final int lastIndexOfAny(@NotNull CharSequence $receiver, @NotNull Collection<String> strings, int startIndex, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(strings, "strings");
        Pair findAnyOf$StringsKt__StringsKt = findAnyOf$StringsKt__StringsKt($receiver, strings, startIndex, ignoreCase, true);
        if (findAnyOf$StringsKt__StringsKt != null) {
            Integer num = (Integer) findAnyOf$StringsKt__StringsKt.getFirst();
            if (num != null) {
                return num.intValue();
            }
        }
        return -1;
    }

    public static final int indexOf(@NotNull CharSequence $receiver, char char_, int startIndex, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        if (!ignoreCase) {
            if ($receiver instanceof String) {
                return ((String) $receiver).indexOf(char_, startIndex);
            }
        }
        return StringsKt.indexOfAny($receiver, new char[]{char_}, startIndex, ignoreCase);
    }

    public static final int indexOf(@NotNull CharSequence $receiver, @NotNull String string, int startIndex, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(string, "string");
        if (!ignoreCase) {
            if ($receiver instanceof String) {
                return ((String) $receiver).indexOf(string, startIndex);
            }
        }
        return indexOf$StringsKt__StringsKt$default($receiver, string, startIndex, $receiver.length(), ignoreCase, false, 16, null);
    }

    public static final int lastIndexOf(@NotNull CharSequence $receiver, char char_, int startIndex, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        if (!ignoreCase) {
            if ($receiver instanceof String) {
                return ((String) $receiver).lastIndexOf(char_, startIndex);
            }
        }
        return StringsKt.lastIndexOfAny($receiver, new char[]{char_}, startIndex, ignoreCase);
    }

    public static final int lastIndexOf(@NotNull CharSequence $receiver, @NotNull String string, int startIndex, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(string, "string");
        if (!ignoreCase) {
            if ($receiver instanceof String) {
                return ((String) $receiver).lastIndexOf(string, startIndex);
            }
        }
        return indexOf$StringsKt__StringsKt($receiver, string, startIndex, 0, ignoreCase, true);
    }

    public static final boolean contains(@NotNull CharSequence $receiver, @NotNull CharSequence other, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(other, "other");
        boolean z = true;
        if (other instanceof String) {
            if (StringsKt.indexOf$default($receiver, (String) other, 0, ignoreCase, 2, null) < 0) {
                z = false;
            }
        } else {
            if (indexOf$StringsKt__StringsKt$default($receiver, other, 0, $receiver.length(), ignoreCase, false, 16, null) < 0) {
                z = false;
            }
        }
        return z;
    }

    public static final boolean contains(@NotNull CharSequence $receiver, char char_, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return StringsKt.indexOf$default($receiver, char_, 0, ignoreCase, 2, null) >= 0;
    }

    @InlineOnly
    private static final boolean contains(@NotNull CharSequence $receiver, Regex regex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return regex.containsMatchIn($receiver);
    }

    private static final Sequence<IntRange> rangesDelimitedBy$StringsKt__StringsKt(@NotNull CharSequence $receiver, char[] delimiters, int startIndex, boolean ignoreCase, int limit) {
        if ((limit >= 0 ? 1 : null) != null) {
            return (Sequence) new DelimitedRangesSequence($receiver, startIndex, limit, new StringsKt__StringsKt$rangesDelimitedBy$2(delimiters, ignoreCase));
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Limit must be non-negative, but was ");
        stringBuilder.append(limit);
        stringBuilder.append('.');
        throw new IllegalArgumentException(stringBuilder.toString().toString());
    }

    private static final Sequence<IntRange> rangesDelimitedBy$StringsKt__StringsKt(@NotNull CharSequence $receiver, String[] delimiters, int startIndex, boolean ignoreCase, int limit) {
        if ((limit >= 0 ? 1 : null) != null) {
            return new DelimitedRangesSequence($receiver, startIndex, limit, new StringsKt__StringsKt$rangesDelimitedBy$4(ArraysKt.asList(delimiters), ignoreCase));
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Limit must be non-negative, but was ");
        stringBuilder.append(limit);
        stringBuilder.append('.');
        throw new IllegalArgumentException(stringBuilder.toString().toString());
    }

    @NotNull
    public static final Sequence<String> splitToSequence(@NotNull CharSequence $receiver, @NotNull String[] delimiters, boolean ignoreCase, int limit) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiters, "delimiters");
        return SequencesKt.map(rangesDelimitedBy$StringsKt__StringsKt$default($receiver, delimiters, 0, ignoreCase, limit, 2, null), new StringsKt__StringsKt$splitToSequence$1($receiver));
    }

    @NotNull
    public static final List<String> split(@NotNull CharSequence $receiver, @NotNull String[] delimiters, boolean ignoreCase, int limit) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiters, "delimiters");
        Object obj = 1;
        if (delimiters.length == 1) {
            String delimiter = delimiters[0];
            if (delimiter.length() != 0) {
                obj = null;
            }
            if (obj == null) {
                return split$StringsKt__StringsKt($receiver, delimiter, ignoreCase, limit);
            }
        }
        Iterable<IntRange> $receiver$iv = SequencesKt.asIterable(rangesDelimitedBy$StringsKt__StringsKt$default($receiver, delimiters, 0, ignoreCase, limit, 2, null));
        int $i$f$map = 0;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10));
        int $i$f$mapTo = 0;
        for (IntRange it : $receiver$iv) {
            destination$iv$iv.add(StringsKt.substring($receiver, it));
        }
        return (List) destination$iv$iv;
    }

    @NotNull
    public static final Sequence<String> splitToSequence(@NotNull CharSequence $receiver, @NotNull char[] delimiters, boolean ignoreCase, int limit) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiters, "delimiters");
        return SequencesKt.map(rangesDelimitedBy$StringsKt__StringsKt$default($receiver, delimiters, 0, ignoreCase, limit, 2, null), new StringsKt__StringsKt$splitToSequence$2($receiver));
    }

    @NotNull
    public static final List<String> split(@NotNull CharSequence $receiver, @NotNull char[] delimiters, boolean ignoreCase, int limit) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiters, "delimiters");
        if (delimiters.length == 1) {
            return split$StringsKt__StringsKt($receiver, String.valueOf(delimiters[0]), ignoreCase, limit);
        }
        Iterable<IntRange> $receiver$iv = SequencesKt.asIterable(rangesDelimitedBy$StringsKt__StringsKt$default($receiver, delimiters, 0, ignoreCase, limit, 2, null));
        int $i$f$map = 0;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10));
        int $i$f$mapTo = 0;
        for (IntRange it : $receiver$iv) {
            destination$iv$iv.add(StringsKt.substring($receiver, it));
        }
        return (List) destination$iv$iv;
    }

    @InlineOnly
    private static final List<String> split(@NotNull CharSequence $receiver, Regex regex, int limit) {
        return regex.split($receiver, limit);
    }

    @InlineOnly
    static /* bridge */ /* synthetic */ List split$default(CharSequence $receiver, Regex regex, int limit, int i, Object obj) {
        if ((i & 2) != null) {
            limit = 0;
        }
        return regex.split($receiver, limit);
    }

    @NotNull
    public static final Sequence<String> lineSequence(@NotNull CharSequence $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return StringsKt.splitToSequence$default($receiver, new String[]{IOUtils.LINE_SEPARATOR_WINDOWS, "\n", StringUtils.CR}, false, 0, 6, null);
    }

    @NotNull
    public static final List<String> lines(@NotNull CharSequence $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return SequencesKt.toList(StringsKt.lineSequence($receiver));
    }
}
