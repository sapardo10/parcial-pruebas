package kotlin.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import kotlin.Metadata;
import kotlin.SinceKotlin;
import kotlin.TypeCastException;
import kotlin.internal.InlineOnly;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u0001\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010\u000b\n\u0002\u0010\u0018\n\u0002\u0010\u0005\n\u0002\u0010\u0012\n\u0002\u0010\f\n\u0002\u0010\u0019\n\u0002\u0010\u0006\n\u0002\u0010\u0013\n\u0002\u0010\u0007\n\u0002\u0010\u0014\n\u0002\u0010\b\n\u0002\u0010\u0015\n\u0002\u0010\t\n\u0002\u0010\u0016\n\u0002\u0010\n\n\u0002\u0010\u0017\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u001f\n\u0002\b\u0006\n\u0002\u0010\u001e\n\u0002\b\u0004\n\u0002\u0010\u000f\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\f\u001a#\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u0003¢\u0006\u0002\u0010\u0004\u001a\u0010\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00050\u0001*\u00020\u0006\u001a\u0010\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00070\u0001*\u00020\b\u001a\u0010\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\t0\u0001*\u00020\n\u001a\u0010\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0001*\u00020\f\u001a\u0010\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\r0\u0001*\u00020\u000e\u001a\u0010\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0001*\u00020\u0010\u001a\u0010\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00110\u0001*\u00020\u0012\u001a\u0010\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00130\u0001*\u00020\u0014\u001aU\u0010\u0015\u001a\u00020\u000f\"\u0004\b\u0000\u0010\u0002*\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u00032\u0006\u0010\u0016\u001a\u0002H\u00022\u001a\u0010\u0017\u001a\u0016\u0012\u0006\b\u0000\u0012\u0002H\u00020\u0018j\n\u0012\u0006\b\u0000\u0012\u0002H\u0002`\u00192\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f¢\u0006\u0002\u0010\u001c\u001a9\u0010\u0015\u001a\u00020\u000f\"\u0004\b\u0000\u0010\u0002*\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u00032\u0006\u0010\u0016\u001a\u0002H\u00022\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f¢\u0006\u0002\u0010\u001d\u001a&\u0010\u0015\u001a\u00020\u000f*\u00020\b2\u0006\u0010\u0016\u001a\u00020\u00072\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f\u001a&\u0010\u0015\u001a\u00020\u000f*\u00020\n2\u0006\u0010\u0016\u001a\u00020\t2\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f\u001a&\u0010\u0015\u001a\u00020\u000f*\u00020\f2\u0006\u0010\u0016\u001a\u00020\u000b2\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f\u001a&\u0010\u0015\u001a\u00020\u000f*\u00020\u000e2\u0006\u0010\u0016\u001a\u00020\r2\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f\u001a&\u0010\u0015\u001a\u00020\u000f*\u00020\u00102\u0006\u0010\u0016\u001a\u00020\u000f2\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f\u001a&\u0010\u0015\u001a\u00020\u000f*\u00020\u00122\u0006\u0010\u0016\u001a\u00020\u00112\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f\u001a&\u0010\u0015\u001a\u00020\u000f*\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u00132\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f\u001a0\u0010\u001e\u001a\u00020\u0005\"\u0004\b\u0000\u0010\u0002*\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u00032\u000e\u0010\u001f\u001a\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u0003H\f¢\u0006\u0002\u0010 \u001a \u0010!\u001a\u00020\u000f\"\u0004\b\u0000\u0010\u0002*\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u0003H\b¢\u0006\u0002\u0010\"\u001a \u0010#\u001a\u00020$\"\u0004\b\u0000\u0010\u0002*\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u0003H\b¢\u0006\u0002\u0010%\u001a0\u0010&\u001a\u00020\u0005\"\u0004\b\u0000\u0010\u0002*\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u00032\u000e\u0010\u001f\u001a\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u0003H\f¢\u0006\u0002\u0010 \u001a\u0015\u0010&\u001a\u00020\u0005*\u00020\u00062\u0006\u0010\u001f\u001a\u00020\u0006H\f\u001a\u0015\u0010&\u001a\u00020\u0005*\u00020\b2\u0006\u0010\u001f\u001a\u00020\bH\f\u001a\u0015\u0010&\u001a\u00020\u0005*\u00020\n2\u0006\u0010\u001f\u001a\u00020\nH\f\u001a\u0015\u0010&\u001a\u00020\u0005*\u00020\f2\u0006\u0010\u001f\u001a\u00020\fH\f\u001a\u0015\u0010&\u001a\u00020\u0005*\u00020\u000e2\u0006\u0010\u001f\u001a\u00020\u000eH\f\u001a\u0015\u0010&\u001a\u00020\u0005*\u00020\u00102\u0006\u0010\u001f\u001a\u00020\u0010H\f\u001a\u0015\u0010&\u001a\u00020\u0005*\u00020\u00122\u0006\u0010\u001f\u001a\u00020\u0012H\f\u001a\u0015\u0010&\u001a\u00020\u0005*\u00020\u00142\u0006\u0010\u001f\u001a\u00020\u0014H\f\u001a \u0010'\u001a\u00020\u000f\"\u0004\b\u0000\u0010\u0002*\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u0003H\b¢\u0006\u0002\u0010\"\u001a\r\u0010'\u001a\u00020\u000f*\u00020\u0006H\b\u001a\r\u0010'\u001a\u00020\u000f*\u00020\bH\b\u001a\r\u0010'\u001a\u00020\u000f*\u00020\nH\b\u001a\r\u0010'\u001a\u00020\u000f*\u00020\fH\b\u001a\r\u0010'\u001a\u00020\u000f*\u00020\u000eH\b\u001a\r\u0010'\u001a\u00020\u000f*\u00020\u0010H\b\u001a\r\u0010'\u001a\u00020\u000f*\u00020\u0012H\b\u001a\r\u0010'\u001a\u00020\u000f*\u00020\u0014H\b\u001a \u0010(\u001a\u00020$\"\u0004\b\u0000\u0010\u0002*\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u0003H\b¢\u0006\u0002\u0010%\u001a\r\u0010(\u001a\u00020$*\u00020\u0006H\b\u001a\r\u0010(\u001a\u00020$*\u00020\bH\b\u001a\r\u0010(\u001a\u00020$*\u00020\nH\b\u001a\r\u0010(\u001a\u00020$*\u00020\fH\b\u001a\r\u0010(\u001a\u00020$*\u00020\u000eH\b\u001a\r\u0010(\u001a\u00020$*\u00020\u0010H\b\u001a\r\u0010(\u001a\u00020$*\u00020\u0012H\b\u001a\r\u0010(\u001a\u00020$*\u00020\u0014H\b\u001a$\u0010)\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0003\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u0003H\b¢\u0006\u0002\u0010*\u001a.\u0010)\u001a\n\u0012\u0006\u0012\u0004\u0018\u0001H\u00020\u0003\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00032\u0006\u0010+\u001a\u00020\u000fH\b¢\u0006\u0002\u0010,\u001a\r\u0010)\u001a\u00020\u0006*\u00020\u0006H\b\u001a\u0015\u0010)\u001a\u00020\u0006*\u00020\u00062\u0006\u0010+\u001a\u00020\u000fH\b\u001a\r\u0010)\u001a\u00020\b*\u00020\bH\b\u001a\u0015\u0010)\u001a\u00020\b*\u00020\b2\u0006\u0010+\u001a\u00020\u000fH\b\u001a\r\u0010)\u001a\u00020\n*\u00020\nH\b\u001a\u0015\u0010)\u001a\u00020\n*\u00020\n2\u0006\u0010+\u001a\u00020\u000fH\b\u001a\r\u0010)\u001a\u00020\f*\u00020\fH\b\u001a\u0015\u0010)\u001a\u00020\f*\u00020\f2\u0006\u0010+\u001a\u00020\u000fH\b\u001a\r\u0010)\u001a\u00020\u000e*\u00020\u000eH\b\u001a\u0015\u0010)\u001a\u00020\u000e*\u00020\u000e2\u0006\u0010+\u001a\u00020\u000fH\b\u001a\r\u0010)\u001a\u00020\u0010*\u00020\u0010H\b\u001a\u0015\u0010)\u001a\u00020\u0010*\u00020\u00102\u0006\u0010+\u001a\u00020\u000fH\b\u001a\r\u0010)\u001a\u00020\u0012*\u00020\u0012H\b\u001a\u0015\u0010)\u001a\u00020\u0012*\u00020\u00122\u0006\u0010+\u001a\u00020\u000fH\b\u001a\r\u0010)\u001a\u00020\u0014*\u00020\u0014H\b\u001a\u0015\u0010)\u001a\u00020\u0014*\u00020\u00142\u0006\u0010+\u001a\u00020\u000fH\b\u001a4\u0010-\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0003\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00032\u0006\u0010\u001a\u001a\u00020\u000f2\u0006\u0010\u001b\u001a\u00020\u000fH\b¢\u0006\u0002\u0010.\u001a\u001d\u0010-\u001a\u00020\u0006*\u00020\u00062\u0006\u0010\u001a\u001a\u00020\u000f2\u0006\u0010\u001b\u001a\u00020\u000fH\b\u001a\u001d\u0010-\u001a\u00020\b*\u00020\b2\u0006\u0010\u001a\u001a\u00020\u000f2\u0006\u0010\u001b\u001a\u00020\u000fH\b\u001a\u001d\u0010-\u001a\u00020\n*\u00020\n2\u0006\u0010\u001a\u001a\u00020\u000f2\u0006\u0010\u001b\u001a\u00020\u000fH\b\u001a\u001d\u0010-\u001a\u00020\f*\u00020\f2\u0006\u0010\u001a\u001a\u00020\u000f2\u0006\u0010\u001b\u001a\u00020\u000fH\b\u001a\u001d\u0010-\u001a\u00020\u000e*\u00020\u000e2\u0006\u0010\u001a\u001a\u00020\u000f2\u0006\u0010\u001b\u001a\u00020\u000fH\b\u001a\u001d\u0010-\u001a\u00020\u0010*\u00020\u00102\u0006\u0010\u001a\u001a\u00020\u000f2\u0006\u0010\u001b\u001a\u00020\u000fH\b\u001a\u001d\u0010-\u001a\u00020\u0012*\u00020\u00122\u0006\u0010\u001a\u001a\u00020\u000f2\u0006\u0010\u001b\u001a\u00020\u000fH\b\u001a\u001d\u0010-\u001a\u00020\u0014*\u00020\u00142\u0006\u0010\u001a\u001a\u00020\u000f2\u0006\u0010\u001b\u001a\u00020\u000fH\b\u001a7\u0010/\u001a\u000200\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00032\u0006\u0010\u0016\u001a\u0002H\u00022\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f¢\u0006\u0002\u00101\u001a&\u0010/\u001a\u000200*\u00020\u00062\u0006\u0010\u0016\u001a\u00020\u00052\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f\u001a&\u0010/\u001a\u000200*\u00020\b2\u0006\u0010\u0016\u001a\u00020\u00072\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f\u001a&\u0010/\u001a\u000200*\u00020\n2\u0006\u0010\u0016\u001a\u00020\t2\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f\u001a&\u0010/\u001a\u000200*\u00020\f2\u0006\u0010\u0016\u001a\u00020\u000b2\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f\u001a&\u0010/\u001a\u000200*\u00020\u000e2\u0006\u0010\u0016\u001a\u00020\r2\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f\u001a&\u0010/\u001a\u000200*\u00020\u00102\u0006\u0010\u0016\u001a\u00020\u000f2\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f\u001a&\u0010/\u001a\u000200*\u00020\u00122\u0006\u0010\u0016\u001a\u00020\u00112\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f\u001a&\u0010/\u001a\u000200*\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u00132\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f\u001a-\u00102\u001a\b\u0012\u0004\u0012\u0002H30\u0001\"\u0004\b\u0000\u00103*\u0006\u0012\u0002\b\u00030\u00032\f\u00104\u001a\b\u0012\u0004\u0012\u0002H305¢\u0006\u0002\u00106\u001aA\u00107\u001a\u0002H8\"\u0010\b\u0000\u00108*\n\u0012\u0006\b\u0000\u0012\u0002H309\"\u0004\b\u0001\u00103*\u0006\u0012\u0002\b\u00030\u00032\u0006\u0010:\u001a\u0002H82\f\u00104\u001a\b\u0012\u0004\u0012\u0002H305¢\u0006\u0002\u0010;\u001a,\u0010<\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0003\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00032\u0006\u0010\u0016\u001a\u0002H\u0002H\u0002¢\u0006\u0002\u0010=\u001a4\u0010<\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0003\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00032\u000e\u0010>\u001a\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u0003H\u0002¢\u0006\u0002\u0010?\u001a2\u0010<\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0003\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00032\f\u0010>\u001a\b\u0012\u0004\u0012\u0002H\u00020@H\u0002¢\u0006\u0002\u0010A\u001a\u0015\u0010<\u001a\u00020\u0006*\u00020\u00062\u0006\u0010\u0016\u001a\u00020\u0005H\u0002\u001a\u0015\u0010<\u001a\u00020\u0006*\u00020\u00062\u0006\u0010>\u001a\u00020\u0006H\u0002\u001a\u001b\u0010<\u001a\u00020\u0006*\u00020\u00062\f\u0010>\u001a\b\u0012\u0004\u0012\u00020\u00050@H\u0002\u001a\u0015\u0010<\u001a\u00020\b*\u00020\b2\u0006\u0010\u0016\u001a\u00020\u0007H\u0002\u001a\u0015\u0010<\u001a\u00020\b*\u00020\b2\u0006\u0010>\u001a\u00020\bH\u0002\u001a\u001b\u0010<\u001a\u00020\b*\u00020\b2\f\u0010>\u001a\b\u0012\u0004\u0012\u00020\u00070@H\u0002\u001a\u0015\u0010<\u001a\u00020\n*\u00020\n2\u0006\u0010\u0016\u001a\u00020\tH\u0002\u001a\u0015\u0010<\u001a\u00020\n*\u00020\n2\u0006\u0010>\u001a\u00020\nH\u0002\u001a\u001b\u0010<\u001a\u00020\n*\u00020\n2\f\u0010>\u001a\b\u0012\u0004\u0012\u00020\t0@H\u0002\u001a\u0015\u0010<\u001a\u00020\f*\u00020\f2\u0006\u0010\u0016\u001a\u00020\u000bH\u0002\u001a\u0015\u0010<\u001a\u00020\f*\u00020\f2\u0006\u0010>\u001a\u00020\fH\u0002\u001a\u001b\u0010<\u001a\u00020\f*\u00020\f2\f\u0010>\u001a\b\u0012\u0004\u0012\u00020\u000b0@H\u0002\u001a\u0015\u0010<\u001a\u00020\u000e*\u00020\u000e2\u0006\u0010\u0016\u001a\u00020\rH\u0002\u001a\u0015\u0010<\u001a\u00020\u000e*\u00020\u000e2\u0006\u0010>\u001a\u00020\u000eH\u0002\u001a\u001b\u0010<\u001a\u00020\u000e*\u00020\u000e2\f\u0010>\u001a\b\u0012\u0004\u0012\u00020\r0@H\u0002\u001a\u0015\u0010<\u001a\u00020\u0010*\u00020\u00102\u0006\u0010\u0016\u001a\u00020\u000fH\u0002\u001a\u0015\u0010<\u001a\u00020\u0010*\u00020\u00102\u0006\u0010>\u001a\u00020\u0010H\u0002\u001a\u001b\u0010<\u001a\u00020\u0010*\u00020\u00102\f\u0010>\u001a\b\u0012\u0004\u0012\u00020\u000f0@H\u0002\u001a\u0015\u0010<\u001a\u00020\u0012*\u00020\u00122\u0006\u0010\u0016\u001a\u00020\u0011H\u0002\u001a\u0015\u0010<\u001a\u00020\u0012*\u00020\u00122\u0006\u0010>\u001a\u00020\u0012H\u0002\u001a\u001b\u0010<\u001a\u00020\u0012*\u00020\u00122\f\u0010>\u001a\b\u0012\u0004\u0012\u00020\u00110@H\u0002\u001a\u0015\u0010<\u001a\u00020\u0014*\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u0013H\u0002\u001a\u0015\u0010<\u001a\u00020\u0014*\u00020\u00142\u0006\u0010>\u001a\u00020\u0014H\u0002\u001a\u001b\u0010<\u001a\u00020\u0014*\u00020\u00142\f\u0010>\u001a\b\u0012\u0004\u0012\u00020\u00130@H\u0002\u001a,\u0010B\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0003\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00032\u0006\u0010\u0016\u001a\u0002H\u0002H\b¢\u0006\u0002\u0010=\u001a\u001d\u0010C\u001a\u000200\"\u0004\b\u0000\u0010\u0002*\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u0003¢\u0006\u0002\u0010D\u001a*\u0010C\u001a\u000200\"\u000e\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020E*\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u0003H\b¢\u0006\u0002\u0010F\u001a1\u0010C\u001a\u000200\"\u0004\b\u0000\u0010\u0002*\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u00032\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f¢\u0006\u0002\u0010G\u001a\n\u0010C\u001a\u000200*\u00020\b\u001a\u001e\u0010C\u001a\u000200*\u00020\b2\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f\u001a\n\u0010C\u001a\u000200*\u00020\n\u001a\u001e\u0010C\u001a\u000200*\u00020\n2\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f\u001a\n\u0010C\u001a\u000200*\u00020\f\u001a\u001e\u0010C\u001a\u000200*\u00020\f2\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f\u001a\n\u0010C\u001a\u000200*\u00020\u000e\u001a\u001e\u0010C\u001a\u000200*\u00020\u000e2\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f\u001a\n\u0010C\u001a\u000200*\u00020\u0010\u001a\u001e\u0010C\u001a\u000200*\u00020\u00102\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f\u001a\n\u0010C\u001a\u000200*\u00020\u0012\u001a\u001e\u0010C\u001a\u000200*\u00020\u00122\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f\u001a\n\u0010C\u001a\u000200*\u00020\u0014\u001a\u001e\u0010C\u001a\u000200*\u00020\u00142\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f\u001a9\u0010H\u001a\u000200\"\u0004\b\u0000\u0010\u0002*\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u00032\u001a\u0010\u0017\u001a\u0016\u0012\u0006\b\u0000\u0012\u0002H\u00020\u0018j\n\u0012\u0006\b\u0000\u0012\u0002H\u0002`\u0019¢\u0006\u0002\u0010I\u001aM\u0010H\u001a\u000200\"\u0004\b\u0000\u0010\u0002*\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u00032\u001a\u0010\u0017\u001a\u0016\u0012\u0006\b\u0000\u0012\u0002H\u00020\u0018j\n\u0012\u0006\b\u0000\u0012\u0002H\u0002`\u00192\b\b\u0002\u0010\u001a\u001a\u00020\u000f2\b\b\u0002\u0010\u001b\u001a\u00020\u000f¢\u0006\u0002\u0010J\u001a-\u0010K\u001a\b\u0012\u0004\u0012\u0002H\u00020L\"\u000e\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020E*\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u0003¢\u0006\u0002\u0010M\u001a?\u0010K\u001a\b\u0012\u0004\u0012\u0002H\u00020L\"\u0004\b\u0000\u0010\u0002*\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u00032\u001a\u0010\u0017\u001a\u0016\u0012\u0006\b\u0000\u0012\u0002H\u00020\u0018j\n\u0012\u0006\b\u0000\u0012\u0002H\u0002`\u0019¢\u0006\u0002\u0010N\u001a\u0010\u0010K\u001a\b\u0012\u0004\u0012\u00020\u00050L*\u00020\u0006\u001a\u0010\u0010K\u001a\b\u0012\u0004\u0012\u00020\u00070L*\u00020\b\u001a\u0010\u0010K\u001a\b\u0012\u0004\u0012\u00020\t0L*\u00020\n\u001a\u0010\u0010K\u001a\b\u0012\u0004\u0012\u00020\u000b0L*\u00020\f\u001a\u0010\u0010K\u001a\b\u0012\u0004\u0012\u00020\r0L*\u00020\u000e\u001a\u0010\u0010K\u001a\b\u0012\u0004\u0012\u00020\u000f0L*\u00020\u0010\u001a\u0010\u0010K\u001a\b\u0012\u0004\u0012\u00020\u00110L*\u00020\u0012\u001a\u0010\u0010K\u001a\b\u0012\u0004\u0012\u00020\u00130L*\u00020\u0014\u001a\u0015\u0010O\u001a\b\u0012\u0004\u0012\u00020\u00050\u0003*\u00020\u0006¢\u0006\u0002\u0010P\u001a\u0015\u0010O\u001a\b\u0012\u0004\u0012\u00020\u00070\u0003*\u00020\b¢\u0006\u0002\u0010Q\u001a\u0015\u0010O\u001a\b\u0012\u0004\u0012\u00020\t0\u0003*\u00020\n¢\u0006\u0002\u0010R\u001a\u0015\u0010O\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0003*\u00020\f¢\u0006\u0002\u0010S\u001a\u0015\u0010O\u001a\b\u0012\u0004\u0012\u00020\r0\u0003*\u00020\u000e¢\u0006\u0002\u0010T\u001a\u0015\u0010O\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0003*\u00020\u0010¢\u0006\u0002\u0010U\u001a\u0015\u0010O\u001a\b\u0012\u0004\u0012\u00020\u00110\u0003*\u00020\u0012¢\u0006\u0002\u0010V\u001a\u0015\u0010O\u001a\b\u0012\u0004\u0012\u00020\u00130\u0003*\u00020\u0014¢\u0006\u0002\u0010W¨\u0006X"}, d2 = {"asList", "", "T", "", "([Ljava/lang/Object;)Ljava/util/List;", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "binarySearch", "element", "comparator", "Ljava/util/Comparator;", "Lkotlin/Comparator;", "fromIndex", "toIndex", "([Ljava/lang/Object;Ljava/lang/Object;Ljava/util/Comparator;II)I", "([Ljava/lang/Object;Ljava/lang/Object;II)I", "contentDeepEquals", "other", "([Ljava/lang/Object;[Ljava/lang/Object;)Z", "contentDeepHashCode", "([Ljava/lang/Object;)I", "contentDeepToString", "", "([Ljava/lang/Object;)Ljava/lang/String;", "contentEquals", "contentHashCode", "contentToString", "copyOf", "([Ljava/lang/Object;)[Ljava/lang/Object;", "newSize", "([Ljava/lang/Object;I)[Ljava/lang/Object;", "copyOfRange", "([Ljava/lang/Object;II)[Ljava/lang/Object;", "fill", "", "([Ljava/lang/Object;Ljava/lang/Object;II)V", "filterIsInstance", "R", "klass", "Ljava/lang/Class;", "([Ljava/lang/Object;Ljava/lang/Class;)Ljava/util/List;", "filterIsInstanceTo", "C", "", "destination", "([Ljava/lang/Object;Ljava/util/Collection;Ljava/lang/Class;)Ljava/util/Collection;", "plus", "([Ljava/lang/Object;Ljava/lang/Object;)[Ljava/lang/Object;", "elements", "([Ljava/lang/Object;[Ljava/lang/Object;)[Ljava/lang/Object;", "", "([Ljava/lang/Object;Ljava/util/Collection;)[Ljava/lang/Object;", "plusElement", "sort", "([Ljava/lang/Object;)V", "", "([Ljava/lang/Comparable;)V", "([Ljava/lang/Object;II)V", "sortWith", "([Ljava/lang/Object;Ljava/util/Comparator;)V", "([Ljava/lang/Object;Ljava/util/Comparator;II)V", "toSortedSet", "Ljava/util/SortedSet;", "([Ljava/lang/Comparable;)Ljava/util/SortedSet;", "([Ljava/lang/Object;Ljava/util/Comparator;)Ljava/util/SortedSet;", "toTypedArray", "([Z)[Ljava/lang/Boolean;", "([B)[Ljava/lang/Byte;", "([C)[Ljava/lang/Character;", "([D)[Ljava/lang/Double;", "([F)[Ljava/lang/Float;", "([I)[Ljava/lang/Integer;", "([J)[Ljava/lang/Long;", "([S)[Ljava/lang/Short;", "kotlin-stdlib"}, k = 5, mv = {1, 1, 10}, xi = 1, xs = "kotlin/collections/ArraysKt")
/* compiled from: _ArraysJvm.kt */
class ArraysKt___ArraysJvmKt extends ArraysKt__ArraysKt {
    @NotNull
    public static final <R> List<R> filterIsInstance(@NotNull Object[] $receiver, @NotNull Class<R> klass) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(klass, "klass");
        return (List) filterIsInstanceTo($receiver, new ArrayList(), klass);
    }

    @NotNull
    public static final <C extends Collection<? super R>, R> C filterIsInstanceTo(@NotNull Object[] $receiver, @NotNull C destination, @NotNull Class<R> klass) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(destination, "destination");
        Intrinsics.checkParameterIsNotNull(klass, "klass");
        for (Object element : $receiver) {
            if (klass.isInstance(element)) {
                destination.add(element);
            }
        }
        return destination;
    }

    @NotNull
    public static final <T> List<T> asList(@NotNull T[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        List<T> asList = ArraysUtilJVM.asList($receiver);
        Intrinsics.checkExpressionValueIsNotNull(asList, "ArraysUtilJVM.asList(this)");
        return asList;
    }

    @NotNull
    public static final List<Byte> asList(@NotNull byte[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return new ArraysKt___ArraysJvmKt$asList$1($receiver);
    }

    @NotNull
    public static final List<Short> asList(@NotNull short[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return new ArraysKt___ArraysJvmKt$asList$2($receiver);
    }

    @NotNull
    public static final List<Integer> asList(@NotNull int[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return new ArraysKt___ArraysJvmKt$asList$3($receiver);
    }

    @NotNull
    public static final List<Long> asList(@NotNull long[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return new ArraysKt___ArraysJvmKt$asList$4($receiver);
    }

    @NotNull
    public static final List<Float> asList(@NotNull float[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return new ArraysKt___ArraysJvmKt$asList$5($receiver);
    }

    @NotNull
    public static final List<Double> asList(@NotNull double[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return new ArraysKt___ArraysJvmKt$asList$6($receiver);
    }

    @NotNull
    public static final List<Boolean> asList(@NotNull boolean[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return new ArraysKt___ArraysJvmKt$asList$7($receiver);
    }

    @NotNull
    public static final List<Character> asList(@NotNull char[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return new ArraysKt___ArraysJvmKt$asList$8($receiver);
    }

    public static /* bridge */ /* synthetic */ int binarySearch$default(Object[] objArr, Object obj, Comparator comparator, int i, int i2, int i3, Object obj2) {
        if ((i3 & 4) != null) {
            i = 0;
        }
        if ((i3 & 8) != 0) {
            i2 = objArr.length;
        }
        return binarySearch(objArr, obj, comparator, i, i2);
    }

    public static final <T> int binarySearch(@NotNull T[] $receiver, T element, @NotNull Comparator<? super T> comparator, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(comparator, "comparator");
        return Arrays.binarySearch($receiver, fromIndex, toIndex, element, comparator);
    }

    public static final <T> int binarySearch(@NotNull T[] $receiver, T element, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return Arrays.binarySearch($receiver, fromIndex, toIndex, element);
    }

    public static final int binarySearch(@NotNull byte[] $receiver, byte element, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return Arrays.binarySearch($receiver, fromIndex, toIndex, element);
    }

    public static final int binarySearch(@NotNull short[] $receiver, short element, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return Arrays.binarySearch($receiver, fromIndex, toIndex, element);
    }

    public static final int binarySearch(@NotNull int[] $receiver, int element, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return Arrays.binarySearch($receiver, fromIndex, toIndex, element);
    }

    public static final int binarySearch(@NotNull long[] $receiver, long element, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return Arrays.binarySearch($receiver, fromIndex, toIndex, element);
    }

    public static final int binarySearch(@NotNull float[] $receiver, float element, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return Arrays.binarySearch($receiver, fromIndex, toIndex, element);
    }

    public static final int binarySearch(@NotNull double[] $receiver, double element, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return Arrays.binarySearch($receiver, fromIndex, toIndex, element);
    }

    public static final int binarySearch(@NotNull char[] $receiver, char element, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return Arrays.binarySearch($receiver, fromIndex, toIndex, element);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final <T> boolean contentDeepEquals(@NotNull T[] $receiver, T[] other) {
        return Arrays.deepEquals($receiver, other);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final <T> int contentDeepHashCode(@NotNull T[] $receiver) {
        return Arrays.deepHashCode($receiver);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final <T> String contentDeepToString(@NotNull T[] $receiver) {
        String deepToString = Arrays.deepToString($receiver);
        Intrinsics.checkExpressionValueIsNotNull(deepToString, "java.util.Arrays.deepToString(this)");
        return deepToString;
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final <T> boolean contentEquals(@NotNull T[] $receiver, T[] other) {
        return Arrays.equals($receiver, other);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final boolean contentEquals(@NotNull byte[] $receiver, byte[] other) {
        return Arrays.equals($receiver, other);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final boolean contentEquals(@NotNull short[] $receiver, short[] other) {
        return Arrays.equals($receiver, other);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final boolean contentEquals(@NotNull int[] $receiver, int[] other) {
        return Arrays.equals($receiver, other);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final boolean contentEquals(@NotNull long[] $receiver, long[] other) {
        return Arrays.equals($receiver, other);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final boolean contentEquals(@NotNull float[] $receiver, float[] other) {
        return Arrays.equals($receiver, other);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final boolean contentEquals(@NotNull double[] $receiver, double[] other) {
        return Arrays.equals($receiver, other);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final boolean contentEquals(@NotNull boolean[] $receiver, boolean[] other) {
        return Arrays.equals($receiver, other);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final boolean contentEquals(@NotNull char[] $receiver, char[] other) {
        return Arrays.equals($receiver, other);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final <T> int contentHashCode(@NotNull T[] $receiver) {
        return Arrays.hashCode($receiver);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final int contentHashCode(@NotNull byte[] $receiver) {
        return Arrays.hashCode($receiver);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final int contentHashCode(@NotNull short[] $receiver) {
        return Arrays.hashCode($receiver);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final int contentHashCode(@NotNull int[] $receiver) {
        return Arrays.hashCode($receiver);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final int contentHashCode(@NotNull long[] $receiver) {
        return Arrays.hashCode($receiver);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final int contentHashCode(@NotNull float[] $receiver) {
        return Arrays.hashCode($receiver);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final int contentHashCode(@NotNull double[] $receiver) {
        return Arrays.hashCode($receiver);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final int contentHashCode(@NotNull boolean[] $receiver) {
        return Arrays.hashCode($receiver);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final int contentHashCode(@NotNull char[] $receiver) {
        return Arrays.hashCode($receiver);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final <T> String contentToString(@NotNull T[] $receiver) {
        String arrays = Arrays.toString($receiver);
        Intrinsics.checkExpressionValueIsNotNull(arrays, "java.util.Arrays.toString(this)");
        return arrays;
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final String contentToString(@NotNull byte[] $receiver) {
        String arrays = Arrays.toString($receiver);
        Intrinsics.checkExpressionValueIsNotNull(arrays, "java.util.Arrays.toString(this)");
        return arrays;
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final String contentToString(@NotNull short[] $receiver) {
        String arrays = Arrays.toString($receiver);
        Intrinsics.checkExpressionValueIsNotNull(arrays, "java.util.Arrays.toString(this)");
        return arrays;
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final String contentToString(@NotNull int[] $receiver) {
        String arrays = Arrays.toString($receiver);
        Intrinsics.checkExpressionValueIsNotNull(arrays, "java.util.Arrays.toString(this)");
        return arrays;
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final String contentToString(@NotNull long[] $receiver) {
        String arrays = Arrays.toString($receiver);
        Intrinsics.checkExpressionValueIsNotNull(arrays, "java.util.Arrays.toString(this)");
        return arrays;
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final String contentToString(@NotNull float[] $receiver) {
        String arrays = Arrays.toString($receiver);
        Intrinsics.checkExpressionValueIsNotNull(arrays, "java.util.Arrays.toString(this)");
        return arrays;
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final String contentToString(@NotNull double[] $receiver) {
        String arrays = Arrays.toString($receiver);
        Intrinsics.checkExpressionValueIsNotNull(arrays, "java.util.Arrays.toString(this)");
        return arrays;
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final String contentToString(@NotNull boolean[] $receiver) {
        String arrays = Arrays.toString($receiver);
        Intrinsics.checkExpressionValueIsNotNull(arrays, "java.util.Arrays.toString(this)");
        return arrays;
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final String contentToString(@NotNull char[] $receiver) {
        String arrays = Arrays.toString($receiver);
        Intrinsics.checkExpressionValueIsNotNull(arrays, "java.util.Arrays.toString(this)");
        return arrays;
    }

    @InlineOnly
    private static final <T> T[] copyOf(@NotNull T[] $receiver) {
        Object copyOf = Arrays.copyOf($receiver, $receiver.length);
        Intrinsics.checkExpressionValueIsNotNull(copyOf, "java.util.Arrays.copyOf(this, size)");
        return copyOf;
    }

    @InlineOnly
    private static final byte[] copyOf(@NotNull byte[] $receiver) {
        Object copyOf = Arrays.copyOf($receiver, $receiver.length);
        Intrinsics.checkExpressionValueIsNotNull(copyOf, "java.util.Arrays.copyOf(this, size)");
        return copyOf;
    }

    @InlineOnly
    private static final short[] copyOf(@NotNull short[] $receiver) {
        Object copyOf = Arrays.copyOf($receiver, $receiver.length);
        Intrinsics.checkExpressionValueIsNotNull(copyOf, "java.util.Arrays.copyOf(this, size)");
        return copyOf;
    }

    @InlineOnly
    private static final int[] copyOf(@NotNull int[] $receiver) {
        Object copyOf = Arrays.copyOf($receiver, $receiver.length);
        Intrinsics.checkExpressionValueIsNotNull(copyOf, "java.util.Arrays.copyOf(this, size)");
        return copyOf;
    }

    @InlineOnly
    private static final long[] copyOf(@NotNull long[] $receiver) {
        Object copyOf = Arrays.copyOf($receiver, $receiver.length);
        Intrinsics.checkExpressionValueIsNotNull(copyOf, "java.util.Arrays.copyOf(this, size)");
        return copyOf;
    }

    @InlineOnly
    private static final float[] copyOf(@NotNull float[] $receiver) {
        Object copyOf = Arrays.copyOf($receiver, $receiver.length);
        Intrinsics.checkExpressionValueIsNotNull(copyOf, "java.util.Arrays.copyOf(this, size)");
        return copyOf;
    }

    @InlineOnly
    private static final double[] copyOf(@NotNull double[] $receiver) {
        Object copyOf = Arrays.copyOf($receiver, $receiver.length);
        Intrinsics.checkExpressionValueIsNotNull(copyOf, "java.util.Arrays.copyOf(this, size)");
        return copyOf;
    }

    @InlineOnly
    private static final boolean[] copyOf(@NotNull boolean[] $receiver) {
        Object copyOf = Arrays.copyOf($receiver, $receiver.length);
        Intrinsics.checkExpressionValueIsNotNull(copyOf, "java.util.Arrays.copyOf(this, size)");
        return copyOf;
    }

    @InlineOnly
    private static final char[] copyOf(@NotNull char[] $receiver) {
        Object copyOf = Arrays.copyOf($receiver, $receiver.length);
        Intrinsics.checkExpressionValueIsNotNull(copyOf, "java.util.Arrays.copyOf(this, size)");
        return copyOf;
    }

    @InlineOnly
    private static final byte[] copyOf(@NotNull byte[] $receiver, int newSize) {
        Object copyOf = Arrays.copyOf($receiver, newSize);
        Intrinsics.checkExpressionValueIsNotNull(copyOf, "java.util.Arrays.copyOf(this, newSize)");
        return copyOf;
    }

    @InlineOnly
    private static final short[] copyOf(@NotNull short[] $receiver, int newSize) {
        Object copyOf = Arrays.copyOf($receiver, newSize);
        Intrinsics.checkExpressionValueIsNotNull(copyOf, "java.util.Arrays.copyOf(this, newSize)");
        return copyOf;
    }

    @InlineOnly
    private static final int[] copyOf(@NotNull int[] $receiver, int newSize) {
        Object copyOf = Arrays.copyOf($receiver, newSize);
        Intrinsics.checkExpressionValueIsNotNull(copyOf, "java.util.Arrays.copyOf(this, newSize)");
        return copyOf;
    }

    @InlineOnly
    private static final long[] copyOf(@NotNull long[] $receiver, int newSize) {
        Object copyOf = Arrays.copyOf($receiver, newSize);
        Intrinsics.checkExpressionValueIsNotNull(copyOf, "java.util.Arrays.copyOf(this, newSize)");
        return copyOf;
    }

    @InlineOnly
    private static final float[] copyOf(@NotNull float[] $receiver, int newSize) {
        Object copyOf = Arrays.copyOf($receiver, newSize);
        Intrinsics.checkExpressionValueIsNotNull(copyOf, "java.util.Arrays.copyOf(this, newSize)");
        return copyOf;
    }

    @InlineOnly
    private static final double[] copyOf(@NotNull double[] $receiver, int newSize) {
        Object copyOf = Arrays.copyOf($receiver, newSize);
        Intrinsics.checkExpressionValueIsNotNull(copyOf, "java.util.Arrays.copyOf(this, newSize)");
        return copyOf;
    }

    @InlineOnly
    private static final boolean[] copyOf(@NotNull boolean[] $receiver, int newSize) {
        Object copyOf = Arrays.copyOf($receiver, newSize);
        Intrinsics.checkExpressionValueIsNotNull(copyOf, "java.util.Arrays.copyOf(this, newSize)");
        return copyOf;
    }

    @InlineOnly
    private static final char[] copyOf(@NotNull char[] $receiver, int newSize) {
        Object copyOf = Arrays.copyOf($receiver, newSize);
        Intrinsics.checkExpressionValueIsNotNull(copyOf, "java.util.Arrays.copyOf(this, newSize)");
        return copyOf;
    }

    @InlineOnly
    private static final <T> T[] copyOf(@NotNull T[] $receiver, int newSize) {
        Object copyOf = Arrays.copyOf($receiver, newSize);
        Intrinsics.checkExpressionValueIsNotNull(copyOf, "java.util.Arrays.copyOf(this, newSize)");
        return copyOf;
    }

    @InlineOnly
    private static final <T> T[] copyOfRange(@NotNull T[] $receiver, int fromIndex, int toIndex) {
        Object copyOfRange = Arrays.copyOfRange($receiver, fromIndex, toIndex);
        Intrinsics.checkExpressionValueIsNotNull(copyOfRange, "java.util.Arrays.copyOfR…this, fromIndex, toIndex)");
        return copyOfRange;
    }

    @InlineOnly
    private static final byte[] copyOfRange(@NotNull byte[] $receiver, int fromIndex, int toIndex) {
        Object copyOfRange = Arrays.copyOfRange($receiver, fromIndex, toIndex);
        Intrinsics.checkExpressionValueIsNotNull(copyOfRange, "java.util.Arrays.copyOfR…this, fromIndex, toIndex)");
        return copyOfRange;
    }

    @InlineOnly
    private static final short[] copyOfRange(@NotNull short[] $receiver, int fromIndex, int toIndex) {
        Object copyOfRange = Arrays.copyOfRange($receiver, fromIndex, toIndex);
        Intrinsics.checkExpressionValueIsNotNull(copyOfRange, "java.util.Arrays.copyOfR…this, fromIndex, toIndex)");
        return copyOfRange;
    }

    @InlineOnly
    private static final int[] copyOfRange(@NotNull int[] $receiver, int fromIndex, int toIndex) {
        Object copyOfRange = Arrays.copyOfRange($receiver, fromIndex, toIndex);
        Intrinsics.checkExpressionValueIsNotNull(copyOfRange, "java.util.Arrays.copyOfR…this, fromIndex, toIndex)");
        return copyOfRange;
    }

    @InlineOnly
    private static final long[] copyOfRange(@NotNull long[] $receiver, int fromIndex, int toIndex) {
        Object copyOfRange = Arrays.copyOfRange($receiver, fromIndex, toIndex);
        Intrinsics.checkExpressionValueIsNotNull(copyOfRange, "java.util.Arrays.copyOfR…this, fromIndex, toIndex)");
        return copyOfRange;
    }

    @InlineOnly
    private static final float[] copyOfRange(@NotNull float[] $receiver, int fromIndex, int toIndex) {
        Object copyOfRange = Arrays.copyOfRange($receiver, fromIndex, toIndex);
        Intrinsics.checkExpressionValueIsNotNull(copyOfRange, "java.util.Arrays.copyOfR…this, fromIndex, toIndex)");
        return copyOfRange;
    }

    @InlineOnly
    private static final double[] copyOfRange(@NotNull double[] $receiver, int fromIndex, int toIndex) {
        Object copyOfRange = Arrays.copyOfRange($receiver, fromIndex, toIndex);
        Intrinsics.checkExpressionValueIsNotNull(copyOfRange, "java.util.Arrays.copyOfR…this, fromIndex, toIndex)");
        return copyOfRange;
    }

    @InlineOnly
    private static final boolean[] copyOfRange(@NotNull boolean[] $receiver, int fromIndex, int toIndex) {
        Object copyOfRange = Arrays.copyOfRange($receiver, fromIndex, toIndex);
        Intrinsics.checkExpressionValueIsNotNull(copyOfRange, "java.util.Arrays.copyOfR…this, fromIndex, toIndex)");
        return copyOfRange;
    }

    @InlineOnly
    private static final char[] copyOfRange(@NotNull char[] $receiver, int fromIndex, int toIndex) {
        Object copyOfRange = Arrays.copyOfRange($receiver, fromIndex, toIndex);
        Intrinsics.checkExpressionValueIsNotNull(copyOfRange, "java.util.Arrays.copyOfR…this, fromIndex, toIndex)");
        return copyOfRange;
    }

    public static final <T> void fill(@NotNull T[] $receiver, T element, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Arrays.fill($receiver, fromIndex, toIndex, element);
    }

    public static final void fill(@NotNull byte[] $receiver, byte element, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Arrays.fill($receiver, fromIndex, toIndex, element);
    }

    public static final void fill(@NotNull short[] $receiver, short element, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Arrays.fill($receiver, fromIndex, toIndex, element);
    }

    public static final void fill(@NotNull int[] $receiver, int element, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Arrays.fill($receiver, fromIndex, toIndex, element);
    }

    public static final void fill(@NotNull long[] $receiver, long element, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Arrays.fill($receiver, fromIndex, toIndex, element);
    }

    public static final void fill(@NotNull float[] $receiver, float element, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Arrays.fill($receiver, fromIndex, toIndex, element);
    }

    public static final void fill(@NotNull double[] $receiver, double element, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Arrays.fill($receiver, fromIndex, toIndex, element);
    }

    public static final void fill(@NotNull boolean[] $receiver, boolean element, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Arrays.fill($receiver, fromIndex, toIndex, element);
    }

    public static final void fill(@NotNull char[] $receiver, char element, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Arrays.fill($receiver, fromIndex, toIndex, element);
    }

    @NotNull
    public static final <T> T[] plus(@NotNull T[] $receiver, T element) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        int index = $receiver.length;
        Object[] result = Arrays.copyOf($receiver, index + 1);
        result[index] = element;
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final byte[] plus(@NotNull byte[] $receiver, byte element) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        int index = $receiver.length;
        byte[] result = Arrays.copyOf($receiver, index + 1);
        result[index] = element;
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final short[] plus(@NotNull short[] $receiver, short element) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        int index = $receiver.length;
        short[] result = Arrays.copyOf($receiver, index + 1);
        result[index] = element;
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final int[] plus(@NotNull int[] $receiver, int element) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        int index = $receiver.length;
        int[] result = Arrays.copyOf($receiver, index + 1);
        result[index] = element;
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final long[] plus(@NotNull long[] $receiver, long element) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        int index = $receiver.length;
        long[] result = Arrays.copyOf($receiver, index + 1);
        result[index] = element;
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final float[] plus(@NotNull float[] $receiver, float element) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        int index = $receiver.length;
        float[] result = Arrays.copyOf($receiver, index + 1);
        result[index] = element;
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final double[] plus(@NotNull double[] $receiver, double element) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        int index = $receiver.length;
        double[] result = Arrays.copyOf($receiver, index + 1);
        result[index] = element;
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final boolean[] plus(@NotNull boolean[] $receiver, boolean element) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        int index = $receiver.length;
        boolean[] result = Arrays.copyOf($receiver, index + 1);
        result[index] = element;
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final char[] plus(@NotNull char[] $receiver, char element) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        int index = $receiver.length;
        char[] result = Arrays.copyOf($receiver, index + 1);
        result[index] = element;
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final <T> T[] plus(@NotNull T[] $receiver, @NotNull Collection<? extends T> elements) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(elements, "elements");
        int index = $receiver.length;
        Object[] result = Arrays.copyOf($receiver, elements.size() + index);
        for (Object element : elements) {
            int index2 = index + 1;
            result[index] = element;
            index = index2;
        }
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final byte[] plus(@NotNull byte[] $receiver, @NotNull Collection<Byte> elements) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(elements, "elements");
        int index = $receiver.length;
        byte[] result = Arrays.copyOf($receiver, elements.size() + index);
        for (Number element : elements) {
            int index2 = index + 1;
            result[index] = element.byteValue();
            index = index2;
        }
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final short[] plus(@NotNull short[] $receiver, @NotNull Collection<Short> elements) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(elements, "elements");
        int index = $receiver.length;
        short[] result = Arrays.copyOf($receiver, elements.size() + index);
        for (Number element : elements) {
            int index2 = index + 1;
            result[index] = element.shortValue();
            index = index2;
        }
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final int[] plus(@NotNull int[] $receiver, @NotNull Collection<Integer> elements) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(elements, "elements");
        int index = $receiver.length;
        int[] result = Arrays.copyOf($receiver, elements.size() + index);
        for (Number element : elements) {
            int index2 = index + 1;
            result[index] = element.intValue();
            index = index2;
        }
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final long[] plus(@NotNull long[] $receiver, @NotNull Collection<Long> elements) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(elements, "elements");
        int index = $receiver.length;
        long[] result = Arrays.copyOf($receiver, elements.size() + index);
        for (Number element : elements) {
            int index2 = index + 1;
            result[index] = element.longValue();
            index = index2;
        }
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final float[] plus(@NotNull float[] $receiver, @NotNull Collection<Float> elements) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(elements, "elements");
        int index = $receiver.length;
        float[] result = Arrays.copyOf($receiver, elements.size() + index);
        for (Number element : elements) {
            int index2 = index + 1;
            result[index] = element.floatValue();
            index = index2;
        }
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final double[] plus(@NotNull double[] $receiver, @NotNull Collection<Double> elements) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(elements, "elements");
        int index = $receiver.length;
        double[] result = Arrays.copyOf($receiver, elements.size() + index);
        for (Number element : elements) {
            int index2 = index + 1;
            result[index] = element.doubleValue();
            index = index2;
        }
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final boolean[] plus(@NotNull boolean[] $receiver, @NotNull Collection<Boolean> elements) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(elements, "elements");
        int index = $receiver.length;
        boolean[] result = Arrays.copyOf($receiver, elements.size() + index);
        for (Boolean element : elements) {
            int index2 = index + 1;
            result[index] = element.booleanValue();
            index = index2;
        }
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final char[] plus(@NotNull char[] $receiver, @NotNull Collection<Character> elements) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(elements, "elements");
        int index = $receiver.length;
        char[] result = Arrays.copyOf($receiver, elements.size() + index);
        for (Character element : elements) {
            int index2 = index + 1;
            result[index] = element.charValue();
            index = index2;
        }
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final <T> T[] plus(@NotNull T[] $receiver, @NotNull T[] elements) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(elements, "elements");
        int thisSize = $receiver.length;
        int arraySize = elements.length;
        Object[] result = Arrays.copyOf($receiver, thisSize + arraySize);
        System.arraycopy(elements, 0, result, thisSize, arraySize);
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final byte[] plus(@NotNull byte[] $receiver, @NotNull byte[] elements) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(elements, "elements");
        int thisSize = $receiver.length;
        int arraySize = elements.length;
        byte[] result = Arrays.copyOf($receiver, thisSize + arraySize);
        System.arraycopy(elements, 0, result, thisSize, arraySize);
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final short[] plus(@NotNull short[] $receiver, @NotNull short[] elements) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(elements, "elements");
        int thisSize = $receiver.length;
        int arraySize = elements.length;
        short[] result = Arrays.copyOf($receiver, thisSize + arraySize);
        System.arraycopy(elements, 0, result, thisSize, arraySize);
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final int[] plus(@NotNull int[] $receiver, @NotNull int[] elements) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(elements, "elements");
        int thisSize = $receiver.length;
        int arraySize = elements.length;
        int[] result = Arrays.copyOf($receiver, thisSize + arraySize);
        System.arraycopy(elements, 0, result, thisSize, arraySize);
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final long[] plus(@NotNull long[] $receiver, @NotNull long[] elements) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(elements, "elements");
        int thisSize = $receiver.length;
        int arraySize = elements.length;
        long[] result = Arrays.copyOf($receiver, thisSize + arraySize);
        System.arraycopy(elements, 0, result, thisSize, arraySize);
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final float[] plus(@NotNull float[] $receiver, @NotNull float[] elements) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(elements, "elements");
        int thisSize = $receiver.length;
        int arraySize = elements.length;
        float[] result = Arrays.copyOf($receiver, thisSize + arraySize);
        System.arraycopy(elements, 0, result, thisSize, arraySize);
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final double[] plus(@NotNull double[] $receiver, @NotNull double[] elements) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(elements, "elements");
        int thisSize = $receiver.length;
        int arraySize = elements.length;
        double[] result = Arrays.copyOf($receiver, thisSize + arraySize);
        System.arraycopy(elements, 0, result, thisSize, arraySize);
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final boolean[] plus(@NotNull boolean[] $receiver, @NotNull boolean[] elements) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(elements, "elements");
        int thisSize = $receiver.length;
        int arraySize = elements.length;
        boolean[] result = Arrays.copyOf($receiver, thisSize + arraySize);
        System.arraycopy(elements, 0, result, thisSize, arraySize);
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @NotNull
    public static final char[] plus(@NotNull char[] $receiver, @NotNull char[] elements) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(elements, "elements");
        int thisSize = $receiver.length;
        int arraySize = elements.length;
        char[] result = Arrays.copyOf($receiver, thisSize + arraySize);
        System.arraycopy(elements, 0, result, thisSize, arraySize);
        Intrinsics.checkExpressionValueIsNotNull(result, "result");
        return result;
    }

    @InlineOnly
    private static final <T> T[] plusElement(@NotNull T[] $receiver, T element) {
        return plus((Object[]) $receiver, (Object) element);
    }

    public static final void sort(@NotNull int[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        if ($receiver.length > 1) {
            Arrays.sort($receiver);
        }
    }

    public static final void sort(@NotNull long[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        if ($receiver.length > 1) {
            Arrays.sort($receiver);
        }
    }

    public static final void sort(@NotNull byte[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        if ($receiver.length > 1) {
            Arrays.sort($receiver);
        }
    }

    public static final void sort(@NotNull short[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        if ($receiver.length > 1) {
            Arrays.sort($receiver);
        }
    }

    public static final void sort(@NotNull double[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        if ($receiver.length > 1) {
            Arrays.sort($receiver);
        }
    }

    public static final void sort(@NotNull float[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        if ($receiver.length > 1) {
            Arrays.sort($receiver);
        }
    }

    public static final void sort(@NotNull char[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        if ($receiver.length > 1) {
            Arrays.sort($receiver);
        }
    }

    @InlineOnly
    private static final <T extends Comparable<? super T>> void sort(@NotNull T[] $receiver) {
        if ($receiver != null) {
            sort((Object[]) $receiver);
            return;
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<kotlin.Any?>");
    }

    public static final <T> void sort(@NotNull T[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        if ($receiver.length > 1) {
            Arrays.sort($receiver);
        }
    }

    public static final <T> void sort(@NotNull T[] $receiver, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Arrays.sort($receiver, fromIndex, toIndex);
    }

    public static final void sort(@NotNull byte[] $receiver, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Arrays.sort($receiver, fromIndex, toIndex);
    }

    public static final void sort(@NotNull short[] $receiver, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Arrays.sort($receiver, fromIndex, toIndex);
    }

    public static final void sort(@NotNull int[] $receiver, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Arrays.sort($receiver, fromIndex, toIndex);
    }

    public static final void sort(@NotNull long[] $receiver, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Arrays.sort($receiver, fromIndex, toIndex);
    }

    public static final void sort(@NotNull float[] $receiver, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Arrays.sort($receiver, fromIndex, toIndex);
    }

    public static final void sort(@NotNull double[] $receiver, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Arrays.sort($receiver, fromIndex, toIndex);
    }

    public static final void sort(@NotNull char[] $receiver, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Arrays.sort($receiver, fromIndex, toIndex);
    }

    public static final <T> void sortWith(@NotNull T[] $receiver, @NotNull Comparator<? super T> comparator) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(comparator, "comparator");
        if ($receiver.length > 1) {
            Arrays.sort($receiver, comparator);
        }
    }

    public static /* bridge */ /* synthetic */ void sortWith$default(Object[] objArr, Comparator comparator, int i, int i2, int i3, Object obj) {
        if ((i3 & 2) != null) {
            i = 0;
        }
        if ((i3 & 4) != 0) {
            i2 = objArr.length;
        }
        sortWith(objArr, comparator, i, i2);
    }

    public static final <T> void sortWith(@NotNull T[] $receiver, @NotNull Comparator<? super T> comparator, int fromIndex, int toIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(comparator, "comparator");
        Arrays.sort($receiver, fromIndex, toIndex, comparator);
    }

    @NotNull
    public static final Byte[] toTypedArray(@NotNull byte[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Byte[] result = new Byte[$receiver.length];
        int length = $receiver.length;
        for (int index = 0; index < length; index++) {
            result[index] = Byte.valueOf($receiver[index]);
        }
        return result;
    }

    @NotNull
    public static final Short[] toTypedArray(@NotNull short[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Short[] result = new Short[$receiver.length];
        int length = $receiver.length;
        for (int index = 0; index < length; index++) {
            result[index] = Short.valueOf($receiver[index]);
        }
        return result;
    }

    @NotNull
    public static final Integer[] toTypedArray(@NotNull int[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Integer[] result = new Integer[$receiver.length];
        int length = $receiver.length;
        for (int index = 0; index < length; index++) {
            result[index] = Integer.valueOf($receiver[index]);
        }
        return result;
    }

    @NotNull
    public static final Long[] toTypedArray(@NotNull long[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Long[] result = new Long[$receiver.length];
        int length = $receiver.length;
        for (int index = 0; index < length; index++) {
            result[index] = Long.valueOf($receiver[index]);
        }
        return result;
    }

    @NotNull
    public static final Float[] toTypedArray(@NotNull float[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Float[] result = new Float[$receiver.length];
        int length = $receiver.length;
        for (int index = 0; index < length; index++) {
            result[index] = Float.valueOf($receiver[index]);
        }
        return result;
    }

    @NotNull
    public static final Double[] toTypedArray(@NotNull double[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Double[] result = new Double[$receiver.length];
        int length = $receiver.length;
        for (int index = 0; index < length; index++) {
            result[index] = Double.valueOf($receiver[index]);
        }
        return result;
    }

    @NotNull
    public static final Boolean[] toTypedArray(@NotNull boolean[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Boolean[] result = new Boolean[$receiver.length];
        int length = $receiver.length;
        for (int index = 0; index < length; index++) {
            result[index] = Boolean.valueOf($receiver[index]);
        }
        return result;
    }

    @NotNull
    public static final Character[] toTypedArray(@NotNull char[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Character[] result = new Character[$receiver.length];
        int length = $receiver.length;
        for (int index = 0; index < length; index++) {
            result[index] = Character.valueOf($receiver[index]);
        }
        return result;
    }

    @NotNull
    public static final <T extends Comparable<? super T>> SortedSet<T> toSortedSet(@NotNull T[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return (SortedSet) ArraysKt___ArraysKt.toCollection((Object[]) $receiver, (Collection) new TreeSet());
    }

    @NotNull
    public static final SortedSet<Byte> toSortedSet(@NotNull byte[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return (SortedSet) ArraysKt___ArraysKt.toCollection($receiver, (Collection) new TreeSet());
    }

    @NotNull
    public static final SortedSet<Short> toSortedSet(@NotNull short[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return (SortedSet) ArraysKt___ArraysKt.toCollection($receiver, (Collection) new TreeSet());
    }

    @NotNull
    public static final SortedSet<Integer> toSortedSet(@NotNull int[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return (SortedSet) ArraysKt___ArraysKt.toCollection($receiver, (Collection) new TreeSet());
    }

    @NotNull
    public static final SortedSet<Long> toSortedSet(@NotNull long[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return (SortedSet) ArraysKt___ArraysKt.toCollection($receiver, (Collection) new TreeSet());
    }

    @NotNull
    public static final SortedSet<Float> toSortedSet(@NotNull float[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return (SortedSet) ArraysKt___ArraysKt.toCollection($receiver, (Collection) new TreeSet());
    }

    @NotNull
    public static final SortedSet<Double> toSortedSet(@NotNull double[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return (SortedSet) ArraysKt___ArraysKt.toCollection($receiver, (Collection) new TreeSet());
    }

    @NotNull
    public static final SortedSet<Boolean> toSortedSet(@NotNull boolean[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return (SortedSet) ArraysKt___ArraysKt.toCollection($receiver, (Collection) new TreeSet());
    }

    @NotNull
    public static final SortedSet<Character> toSortedSet(@NotNull char[] $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return (SortedSet) ArraysKt___ArraysKt.toCollection($receiver, (Collection) new TreeSet());
    }

    @NotNull
    public static final <T> SortedSet<T> toSortedSet(@NotNull T[] $receiver, @NotNull Comparator<? super T> comparator) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(comparator, "comparator");
        return (SortedSet) ArraysKt___ArraysKt.toCollection((Object[]) $receiver, (Collection) new TreeSet(comparator));
    }
}
