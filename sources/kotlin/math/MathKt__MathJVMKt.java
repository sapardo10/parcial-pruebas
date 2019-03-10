package kotlin.math;

import kotlin.Metadata;
import kotlin.SinceKotlin;
import kotlin.internal.InlineOnly;
import kotlin.jvm.internal.DoubleCompanionObject;
import kotlin.jvm.internal.FloatCompanionObject;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\"\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b7\u001a\u0011\u0010\u0016\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\b\u001a\u0011\u0010\u0016\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0011\u0010\u0016\u001a\u00020\t2\u0006\u0010\u0018\u001a\u00020\tH\b\u001a\u0011\u0010\u0016\u001a\u00020\f2\u0006\u0010\u0018\u001a\u00020\fH\b\u001a\u0011\u0010\u0019\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\b\u001a\u0011\u0010\u0019\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0010\u0010\u001a\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\u0007\u001a\u0011\u0010\u001a\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0011\u0010\u001b\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\b\u001a\u0011\u0010\u001b\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0010\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\u0007\u001a\u0011\u0010\u001c\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0011\u0010\u001d\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\b\u001a\u0011\u0010\u001d\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0019\u0010\u001e\u001a\u00020\u00012\u0006\u0010\u001f\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\b\u001a\u0019\u0010\u001e\u001a\u00020\u00062\u0006\u0010\u001f\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0010\u0010 \u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\u0007\u001a\u0011\u0010 \u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0011\u0010!\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\b\u001a\u0011\u0010!\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0011\u0010\"\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\b\u001a\u0011\u0010\"\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0011\u0010#\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\b\u001a\u0011\u0010#\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0011\u0010$\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\b\u001a\u0011\u0010$\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0011\u0010%\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\b\u001a\u0011\u0010%\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0011\u0010&\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\b\u001a\u0011\u0010&\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0019\u0010'\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u00012\u0006\u0010\u001f\u001a\u00020\u0001H\b\u001a\u0019\u0010'\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u00062\u0006\u0010\u001f\u001a\u00020\u0006H\b\u001a\u0011\u0010(\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\b\u001a\u0011\u0010(\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0011\u0010)\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\b\u001a\u0011\u0010)\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0018\u0010*\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u00012\u0006\u0010+\u001a\u00020\u0001H\u0007\u001a\u0018\u0010*\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u00062\u0006\u0010+\u001a\u00020\u0006H\u0007\u001a\u0011\u0010,\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\b\u001a\u0011\u0010,\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0010\u0010-\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\u0007\u001a\u0010\u0010-\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\u0007\u001a\u0019\u0010.\u001a\u00020\u00012\u0006\u0010/\u001a\u00020\u00012\u0006\u00100\u001a\u00020\u0001H\b\u001a\u0019\u0010.\u001a\u00020\u00062\u0006\u0010/\u001a\u00020\u00062\u0006\u00100\u001a\u00020\u0006H\b\u001a\u0019\u0010.\u001a\u00020\t2\u0006\u0010/\u001a\u00020\t2\u0006\u00100\u001a\u00020\tH\b\u001a\u0019\u0010.\u001a\u00020\f2\u0006\u0010/\u001a\u00020\f2\u0006\u00100\u001a\u00020\fH\b\u001a\u0019\u00101\u001a\u00020\u00012\u0006\u0010/\u001a\u00020\u00012\u0006\u00100\u001a\u00020\u0001H\b\u001a\u0019\u00101\u001a\u00020\u00062\u0006\u0010/\u001a\u00020\u00062\u0006\u00100\u001a\u00020\u0006H\b\u001a\u0019\u00101\u001a\u00020\t2\u0006\u0010/\u001a\u00020\t2\u0006\u00100\u001a\u00020\tH\b\u001a\u0019\u00101\u001a\u00020\f2\u0006\u0010/\u001a\u00020\f2\u0006\u00100\u001a\u00020\fH\b\u001a\u0011\u00102\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\b\u001a\u0011\u00102\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0011\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\b\u001a\u0011\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0011\u00103\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\b\u001a\u0011\u00103\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0011\u00104\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\b\u001a\u0011\u00104\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0011\u00105\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\b\u001a\u0011\u00105\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0011\u00106\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\b\u001a\u0011\u00106\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0011\u00107\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\b\u001a\u0011\u00107\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0010\u00108\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\u0007\u001a\u0010\u00108\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\u0007\u001a\u0015\u00109\u001a\u00020\u0001*\u00020\u00012\u0006\u0010:\u001a\u00020\u0001H\b\u001a\u0015\u00109\u001a\u00020\u0006*\u00020\u00062\u0006\u0010:\u001a\u00020\u0006H\b\u001a\r\u0010;\u001a\u00020\u0001*\u00020\u0001H\b\u001a\r\u0010;\u001a\u00020\u0006*\u00020\u0006H\b\u001a\u0015\u0010<\u001a\u00020\u0001*\u00020\u00012\u0006\u0010=\u001a\u00020\u0001H\b\u001a\u0015\u0010<\u001a\u00020\u0006*\u00020\u00062\u0006\u0010=\u001a\u00020\u0006H\b\u001a\r\u0010>\u001a\u00020\u0001*\u00020\u0001H\b\u001a\r\u0010>\u001a\u00020\u0006*\u00020\u0006H\b\u001a\u0015\u0010?\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0001H\b\u001a\u0015\u0010?\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0018\u001a\u00020\tH\b\u001a\u0015\u0010?\u001a\u00020\u0006*\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\b\u001a\u0015\u0010?\u001a\u00020\u0006*\u00020\u00062\u0006\u0010\u0018\u001a\u00020\tH\b\u001a\f\u0010@\u001a\u00020\t*\u00020\u0001H\u0007\u001a\f\u0010@\u001a\u00020\t*\u00020\u0006H\u0007\u001a\f\u0010A\u001a\u00020\f*\u00020\u0001H\u0007\u001a\f\u0010A\u001a\u00020\f*\u00020\u0006H\u0007\u001a\u0015\u0010B\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u000f\u001a\u00020\u0001H\b\u001a\u0015\u0010B\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u000f\u001a\u00020\tH\b\u001a\u0015\u0010B\u001a\u00020\u0006*\u00020\u00062\u0006\u0010\u000f\u001a\u00020\u0006H\b\u001a\u0015\u0010B\u001a\u00020\u0006*\u00020\u00062\u0006\u0010\u000f\u001a\u00020\tH\b\"\u001f\u0010\u0000\u001a\u00020\u0001*\u00020\u00018Æ\u0002X\u0004¢\u0006\f\u0012\u0004\b\u0002\u0010\u0003\u001a\u0004\b\u0004\u0010\u0005\"\u001f\u0010\u0000\u001a\u00020\u0006*\u00020\u00068Æ\u0002X\u0004¢\u0006\f\u0012\u0004\b\u0002\u0010\u0007\u001a\u0004\b\u0004\u0010\b\"\u001f\u0010\u0000\u001a\u00020\t*\u00020\t8Æ\u0002X\u0004¢\u0006\f\u0012\u0004\b\u0002\u0010\n\u001a\u0004\b\u0004\u0010\u000b\"\u001f\u0010\u0000\u001a\u00020\f*\u00020\f8Æ\u0002X\u0004¢\u0006\f\u0012\u0004\b\u0002\u0010\r\u001a\u0004\b\u0004\u0010\u000e\"\u001f\u0010\u000f\u001a\u00020\u0001*\u00020\u00018Æ\u0002X\u0004¢\u0006\f\u0012\u0004\b\u0010\u0010\u0003\u001a\u0004\b\u0011\u0010\u0005\"\u001f\u0010\u000f\u001a\u00020\u0006*\u00020\u00068Æ\u0002X\u0004¢\u0006\f\u0012\u0004\b\u0010\u0010\u0007\u001a\u0004\b\u0011\u0010\b\"\u001e\u0010\u000f\u001a\u00020\t*\u00020\t8FX\u0004¢\u0006\f\u0012\u0004\b\u0010\u0010\n\u001a\u0004\b\u0011\u0010\u000b\"\u001e\u0010\u000f\u001a\u00020\t*\u00020\f8FX\u0004¢\u0006\f\u0012\u0004\b\u0010\u0010\r\u001a\u0004\b\u0011\u0010\u0012\"\u001f\u0010\u0013\u001a\u00020\u0001*\u00020\u00018Æ\u0002X\u0004¢\u0006\f\u0012\u0004\b\u0014\u0010\u0003\u001a\u0004\b\u0015\u0010\u0005\"\u001f\u0010\u0013\u001a\u00020\u0006*\u00020\u00068Æ\u0002X\u0004¢\u0006\f\u0012\u0004\b\u0014\u0010\u0007\u001a\u0004\b\u0015\u0010\b¨\u0006C"}, d2 = {"absoluteValue", "", "absoluteValue$annotations", "(D)V", "getAbsoluteValue", "(D)D", "", "(F)V", "(F)F", "", "(I)V", "(I)I", "", "(J)V", "(J)J", "sign", "sign$annotations", "getSign", "(J)I", "ulp", "ulp$annotations", "getUlp", "abs", "x", "n", "acos", "acosh", "asin", "asinh", "atan", "atan2", "y", "atanh", "ceil", "cos", "cosh", "exp", "expm1", "floor", "hypot", "ln", "ln1p", "log", "base", "log10", "log2", "max", "a", "b", "min", "round", "sin", "sinh", "sqrt", "tan", "tanh", "truncate", "IEEErem", "divisor", "nextDown", "nextTowards", "to", "nextUp", "pow", "roundToInt", "roundToLong", "withSign", "kotlin-stdlib"}, k = 5, mv = {1, 1, 10}, xi = 1, xs = "kotlin/math/MathKt")
/* compiled from: MathJVM.kt */
class MathKt__MathJVMKt extends MathKt__MathHKt {
    @SinceKotlin(version = "1.2")
    @InlineOnly
    public static /* synthetic */ void absoluteValue$annotations(double d) {
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    public static /* synthetic */ void absoluteValue$annotations(float f) {
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    public static /* synthetic */ void absoluteValue$annotations(int i) {
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    public static /* synthetic */ void absoluteValue$annotations(long j) {
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    public static /* synthetic */ void sign$annotations(double d) {
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    public static /* synthetic */ void sign$annotations(float f) {
    }

    @SinceKotlin(version = "1.2")
    public static /* synthetic */ void sign$annotations(int i) {
    }

    @SinceKotlin(version = "1.2")
    public static /* synthetic */ void sign$annotations(long j) {
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    public static /* synthetic */ void ulp$annotations(double d) {
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    public static /* synthetic */ void ulp$annotations(float f) {
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double sin(double x) {
        return Math.sin(x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double cos(double x) {
        return Math.cos(x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double tan(double x) {
        return Math.tan(x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double asin(double x) {
        return Math.asin(x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double acos(double x) {
        return Math.acos(x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double atan(double x) {
        return Math.atan(x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double atan2(double y, double x) {
        return Math.atan2(y, x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double sinh(double x) {
        return Math.sinh(x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double cosh(double x) {
        return Math.cosh(x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double tanh(double x) {
        return Math.tanh(x);
    }

    @SinceKotlin(version = "1.2")
    public static final double asinh(double x) {
        double d;
        double d2;
        if (x >= Constants.taylor_n_bound) {
            if (x <= Constants.upper_taylor_n_bound) {
                d = x * x;
                d2 = (double) 1;
                Double.isNaN(d2);
                d = Math.log(Math.sqrt(d + d2) + x);
            } else if (x > Constants.upper_taylor_2_bound) {
                d = Math.log(x) + Constants.LN2;
            } else {
                d = (double) 2;
                Double.isNaN(d);
                double d3 = x * d;
                double d4 = (double) 1;
                Double.isNaN(d);
                d *= x;
                Double.isNaN(d4);
                d = Math.log(d3 + (d4 / d));
            }
        } else if (x <= (-Constants.taylor_n_bound)) {
            d = -asinh(-x);
        } else {
            d = x;
            if (Math.abs(x) >= Constants.taylor_2_bound) {
                d2 = (x * x) * x;
                double d5 = (double) 6;
                Double.isNaN(d5);
                d -= d2 / d5;
            }
        }
        return d;
    }

    @SinceKotlin(version = "1.2")
    public static final double acosh(double x) {
        double d = (double) 1;
        if (x < d) {
            d = DoubleCompanionObject.INSTANCE.getNaN();
        } else if (x > Constants.upper_taylor_2_bound) {
            d = Math.log(x) + Constants.LN2;
        } else {
            Double.isNaN(d);
            double d2;
            if (x - d >= Constants.taylor_n_bound) {
                d2 = x * x;
                Double.isNaN(d);
                d = Math.log(Math.sqrt(d2 - d) + x);
            } else {
                Double.isNaN(d);
                d = Math.sqrt(x - d);
                d2 = d;
                if (d >= Constants.taylor_2_bound) {
                    double d3 = (d * d) * d;
                    double d4 = (double) 12;
                    Double.isNaN(d4);
                    d2 -= d3 / d4;
                }
                d = Math.sqrt(2.0d) * d2;
            }
        }
        return d;
    }

    @SinceKotlin(version = "1.2")
    public static final double atanh(double x) {
        double result;
        if (Math.abs(x) < Constants.taylor_n_bound) {
            result = x;
            if (Math.abs(x) > Constants.taylor_2_bound) {
                double d = (x * x) * x;
                double d2 = (double) 3;
                Double.isNaN(d2);
                result += d / d2;
            }
            return result;
        }
        result = (double) 1;
        Double.isNaN(result);
        d = result + x;
        Double.isNaN(result);
        result = Math.log(d / (result - x));
        d = (double) 2;
        Double.isNaN(d);
        return result / d;
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double hypot(double x, double y) {
        return Math.hypot(x, y);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double sqrt(double x) {
        return Math.sqrt(x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double exp(double x) {
        return Math.exp(x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double expm1(double x) {
        return Math.expm1(x);
    }

    @SinceKotlin(version = "1.2")
    public static final double log(double x, double base) {
        if (base > 0.0d) {
            if (base != 1.0d) {
                return Math.log(x) / Math.log(base);
            }
        }
        return DoubleCompanionObject.INSTANCE.getNaN();
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double ln(double x) {
        return Math.log(x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double log10(double x) {
        return Math.log10(x);
    }

    @SinceKotlin(version = "1.2")
    public static final double log2(double x) {
        return Math.log(x) / Constants.LN2;
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double ln1p(double x) {
        return Math.log1p(x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double ceil(double x) {
        return Math.ceil(x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double floor(double x) {
        return Math.floor(x);
    }

    @SinceKotlin(version = "1.2")
    public static final double truncate(double x) {
        double floor;
        if (!Double.isNaN(x)) {
            if (!Double.isInfinite(x)) {
                if (x > ((double) 0)) {
                    floor = Math.floor(x);
                } else {
                    floor = Math.ceil(x);
                }
                return floor;
            }
        }
        floor = x;
        return floor;
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double round(double x) {
        return Math.rint(x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double abs(double x) {
        return Math.abs(x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double sign(double x) {
        return Math.signum(x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double min(double a, double b) {
        return Math.min(a, b);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double max(double a, double b) {
        return Math.max(a, b);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double pow(double $receiver, double x) {
        return Math.pow($receiver, x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double pow(double $receiver, int n) {
        return Math.pow($receiver, (double) n);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double IEEErem(double $receiver, double divisor) {
        return Math.IEEEremainder($receiver, divisor);
    }

    private static final double getAbsoluteValue(double $receiver) {
        return Math.abs($receiver);
    }

    private static final double getSign(double $receiver) {
        return Math.signum($receiver);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double withSign(double $receiver, double sign) {
        return Math.copySign($receiver, sign);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double withSign(double $receiver, int sign) {
        return Math.copySign($receiver, (double) sign);
    }

    private static final double getUlp(double $receiver) {
        return Math.ulp($receiver);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double nextUp(double $receiver) {
        return Math.nextUp($receiver);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double nextDown(double $receiver) {
        return Math.nextAfter($receiver, DoubleCompanionObject.INSTANCE.getNEGATIVE_INFINITY());
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final double nextTowards(double $receiver, double to) {
        return Math.nextAfter($receiver, to);
    }

    @SinceKotlin(version = "1.2")
    public static final int roundToInt(double $receiver) {
        if (Double.isNaN($receiver)) {
            throw new IllegalArgumentException("Cannot round NaN value.");
        }
        int i = Integer.MAX_VALUE;
        if ($receiver <= ((double) 2147483647)) {
            if ($receiver < ((double) Integer.MIN_VALUE)) {
                i = Integer.MIN_VALUE;
            } else {
                i = (int) Math.round($receiver);
            }
        }
        return i;
    }

    @SinceKotlin(version = "1.2")
    public static final long roundToLong(double $receiver) {
        if (!Double.isNaN($receiver)) {
            return Math.round($receiver);
        }
        throw new IllegalArgumentException("Cannot round NaN value.");
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float sin(float x) {
        return (float) Math.sin((double) x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float cos(float x) {
        return (float) Math.cos((double) x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float tan(float x) {
        return (float) Math.tan((double) x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float asin(float x) {
        return (float) Math.asin((double) x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float acos(float x) {
        return (float) Math.acos((double) x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float atan(float x) {
        return (float) Math.atan((double) x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float atan2(float y, float x) {
        return (float) Math.atan2((double) y, (double) x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float sinh(float x) {
        return (float) Math.sinh((double) x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float cosh(float x) {
        return (float) Math.cosh((double) x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float tanh(float x) {
        return (float) Math.tanh((double) x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float asinh(float x) {
        return (float) asinh((double) x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float acosh(float x) {
        return (float) acosh((double) x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float atanh(float x) {
        return (float) atanh((double) x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float hypot(float x, float y) {
        return (float) Math.hypot((double) x, (double) y);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float sqrt(float x) {
        return (float) Math.sqrt((double) x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float exp(float x) {
        return (float) Math.exp((double) x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float expm1(float x) {
        return (float) Math.expm1((double) x);
    }

    @SinceKotlin(version = "1.2")
    public static final float log(float x, float base) {
        if (base > 0.0f) {
            if (base != 1.0f) {
                return (float) (Math.log((double) x) / Math.log((double) base));
            }
        }
        return FloatCompanionObject.INSTANCE.getNaN();
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float ln(float x) {
        return (float) Math.log((double) x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float log10(float x) {
        return (float) Math.log10((double) x);
    }

    @SinceKotlin(version = "1.2")
    public static final float log2(float x) {
        return (float) (Math.log((double) x) / Constants.LN2);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float ln1p(float x) {
        return (float) Math.log1p((double) x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float ceil(float x) {
        return (float) Math.ceil((double) x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float floor(float x) {
        return (float) Math.floor((double) x);
    }

    @SinceKotlin(version = "1.2")
    public static final float truncate(float x) {
        float floor;
        if (!Float.isNaN(x)) {
            if (!Float.isInfinite(x)) {
                if (x > ((float) 0)) {
                    floor = (float) Math.floor((double) x);
                } else {
                    floor = (float) Math.ceil((double) x);
                }
                return floor;
            }
        }
        floor = x;
        return floor;
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float round(float x) {
        return (float) Math.rint((double) x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float abs(float x) {
        return Math.abs(x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float sign(float x) {
        return Math.signum(x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float min(float a, float b) {
        return Math.min(a, b);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float max(float a, float b) {
        return Math.max(a, b);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float pow(float $receiver, float x) {
        return (float) Math.pow((double) $receiver, (double) x);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float pow(float $receiver, int n) {
        return (float) Math.pow((double) $receiver, (double) n);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float IEEErem(float $receiver, float divisor) {
        return (float) Math.IEEEremainder((double) $receiver, (double) divisor);
    }

    private static final float getAbsoluteValue(float $receiver) {
        return Math.abs($receiver);
    }

    private static final float getSign(float $receiver) {
        return Math.signum($receiver);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float withSign(float $receiver, float sign) {
        return Math.copySign($receiver, sign);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float withSign(float $receiver, int sign) {
        return Math.copySign($receiver, (float) sign);
    }

    private static final float getUlp(float $receiver) {
        return Math.ulp($receiver);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float nextUp(float $receiver) {
        return Math.nextUp($receiver);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float nextDown(float $receiver) {
        return Math.nextAfter($receiver, DoubleCompanionObject.INSTANCE.getNEGATIVE_INFINITY());
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final float nextTowards(float $receiver, float to) {
        return Math.nextAfter($receiver, (double) to);
    }

    @SinceKotlin(version = "1.2")
    public static final int roundToInt(float $receiver) {
        if (!Float.isNaN($receiver)) {
            return Math.round($receiver);
        }
        throw new IllegalArgumentException("Cannot round NaN value.");
    }

    @SinceKotlin(version = "1.2")
    public static final long roundToLong(float $receiver) {
        return roundToLong((double) $receiver);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final int abs(int n) {
        return Math.abs(n);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final int min(int a, int b) {
        return Math.min(a, b);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final int max(int a, int b) {
        return Math.max(a, b);
    }

    private static final int getAbsoluteValue(int $receiver) {
        return Math.abs($receiver);
    }

    public static final int getSign(int $receiver) {
        int i;
        if ($receiver < 0) {
            i = -1;
        } else if ($receiver > 0) {
            i = 1;
        } else {
            i = 0;
        }
        return i;
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final long abs(long n) {
        return Math.abs(n);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final long min(long a, long b) {
        return Math.min(a, b);
    }

    @SinceKotlin(version = "1.2")
    @InlineOnly
    private static final long max(long a, long b) {
        return Math.max(a, b);
    }

    private static final long getAbsoluteValue(long $receiver) {
        return Math.abs($receiver);
    }

    public static final int getSign(long $receiver) {
        int i;
        if ($receiver < 0) {
            i = -1;
        } else if ($receiver > 0) {
            i = 1;
        } else {
            i = 0;
        }
        return i;
    }
}
