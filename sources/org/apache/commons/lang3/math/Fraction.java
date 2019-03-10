package org.apache.commons.lang3.math;

import java.math.BigInteger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public final class Fraction extends Number implements Comparable<Fraction> {
    public static final Fraction FOUR_FIFTHS = new Fraction(4, 5);
    public static final Fraction ONE = new Fraction(1, 1);
    public static final Fraction ONE_FIFTH = new Fraction(1, 5);
    public static final Fraction ONE_HALF = new Fraction(1, 2);
    public static final Fraction ONE_QUARTER = new Fraction(1, 4);
    public static final Fraction ONE_THIRD = new Fraction(1, 3);
    public static final Fraction THREE_FIFTHS = new Fraction(3, 5);
    public static final Fraction THREE_QUARTERS = new Fraction(3, 4);
    public static final Fraction TWO_FIFTHS = new Fraction(2, 5);
    public static final Fraction TWO_QUARTERS = new Fraction(2, 4);
    public static final Fraction TWO_THIRDS = new Fraction(2, 3);
    public static final Fraction ZERO = new Fraction(0, 1);
    private static final long serialVersionUID = 65382027393090L;
    private final int denominator;
    private transient int hashCode = 0;
    private final int numerator;
    private transient String toProperString = null;
    private transient String toString = null;

    public static org.apache.commons.lang3.math.Fraction getFraction(double r29) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:26:0x00be in {2, 3, 16, 17, 21, 23, 25} preds:[]
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
        r1 = 0;
        r3 = (r29 > r1 ? 1 : (r29 == r1 ? 0 : -1));
        if (r3 >= 0) goto L_0x0008;
    L_0x0006:
        r1 = -1;
        goto L_0x0009;
    L_0x0008:
        r1 = 1;
    L_0x0009:
        r2 = java.lang.Math.abs(r29);
        r4 = 4746794007244308480; // 0x41dfffffffc00000 float:NaN double:2.147483647E9;
        r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r6 > 0) goto L_0x00b4;
    L_0x0016:
        r4 = java.lang.Double.isNaN(r2);
        if (r4 != 0) goto L_0x00b4;
    L_0x001c:
        r4 = (int) r2;
        r5 = (double) r4;
        java.lang.Double.isNaN(r5);
        r2 = r2 - r5;
        r5 = 0;
        r6 = 1;
        r7 = 1;
        r8 = 0;
        r9 = 0;
        r10 = 0;
        r11 = (int) r2;
        r12 = 0;
        r13 = 4607182418800017408; // 0x3ff0000000000000 float:0.0 double:1.0;
        r15 = 0;
        r18 = r1;
        r0 = (double) r11;
        java.lang.Double.isNaN(r0);
        r0 = r2 - r0;
        r19 = 0;
        r21 = 9218868437227405311; // 0x7fefffffffffffff float:NaN double:1.7976931348623157E308;
        r23 = 1;
    L_0x003f:
        r24 = r21;
        r29 = r9;
        r30 = r10;
        r9 = r13 / r0;
        r12 = (int) r9;
        r15 = r0;
        r9 = (double) r12;
        java.lang.Double.isNaN(r9);
        r9 = r9 * r0;
        r19 = r13 - r9;
        r9 = r11 * r7;
        r9 = r9 + r5;
        r10 = r11 * r8;
        r10 = r10 + r6;
        r29 = r0;
        r0 = (double) r9;
        r26 = r5;
        r27 = r6;
        r5 = (double) r10;
        java.lang.Double.isNaN(r0);
        java.lang.Double.isNaN(r5);
        r0 = r0 / r5;
        r5 = r2 - r0;
        r21 = java.lang.Math.abs(r5);
        r11 = r12;
        r13 = r15;
        r5 = r19;
        r26 = r7;
        r29 = r8;
        r7 = r9;
        r8 = r10;
        r27 = r0;
        r17 = 1;
        r0 = r23 + 1;
        r23 = (r24 > r21 ? 1 : (r24 == r21 ? 0 : -1));
        if (r23 <= 0) goto L_0x0093;
    L_0x0080:
        r1 = 10000; // 0x2710 float:1.4013E-41 double:4.9407E-320;
        if (r10 > r1) goto L_0x0093;
    L_0x0084:
        if (r10 <= 0) goto L_0x0093;
    L_0x0086:
        r1 = 25;
        if (r0 < r1) goto L_0x008b;
    L_0x008a:
        goto L_0x0093;
    L_0x008b:
        r23 = r0;
        r0 = r5;
        r5 = r26;
        r6 = r29;
        goto L_0x003f;
    L_0x0093:
        r1 = 25;
        if (r0 == r1) goto L_0x00a6;
    L_0x0097:
        r1 = r29;
        r17 = r4 * r1;
        r17 = r26 + r17;
        r29 = r0;
        r0 = r17 * r18;
        r0 = getReducedFraction(r0, r1);
        return r0;
    L_0x00a6:
        r1 = r29;
        r29 = r0;
        r0 = new java.lang.ArithmeticException;
        r30 = r1;
        r1 = "Unable to convert double to fraction";
        r0.<init>(r1);
        throw r0;
    L_0x00b4:
        r18 = r1;
        r0 = new java.lang.ArithmeticException;
        r1 = "The value must not be greater than Integer.MAX_VALUE or NaN";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.math.Fraction.getFraction(double):org.apache.commons.lang3.math.Fraction");
    }

    private static int greatestCommonDivisor(int r4, int r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:49:0x0079 in {2, 7, 9, 10, 12, 13, 20, 25, 26, 29, 31, 32, 36, 37, 39, 41, 46, 48} preds:[]
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
        if (r4 == 0) goto L_0x005f;
    L_0x0002:
        if (r5 != 0) goto L_0x0005;
    L_0x0004:
        goto L_0x005f;
    L_0x0005:
        r0 = java.lang.Math.abs(r4);
        r1 = 1;
        if (r0 == r1) goto L_0x005d;
    L_0x000c:
        r0 = java.lang.Math.abs(r5);
        if (r0 != r1) goto L_0x0013;
    L_0x0012:
        goto L_0x005d;
    L_0x0013:
        if (r4 <= 0) goto L_0x0017;
    L_0x0015:
        r4 = -r4;
        goto L_0x0018;
    L_0x0018:
        if (r5 <= 0) goto L_0x001c;
    L_0x001a:
        r5 = -r5;
        goto L_0x001d;
    L_0x001d:
        r0 = 0;
    L_0x001e:
        r2 = r4 & 1;
        r3 = 31;
        if (r2 != 0) goto L_0x0031;
    L_0x0024:
        r2 = r5 & 1;
        if (r2 != 0) goto L_0x0031;
    L_0x0028:
        if (r0 >= r3) goto L_0x0031;
    L_0x002a:
        r4 = r4 / 2;
        r5 = r5 / 2;
        r0 = r0 + 1;
        goto L_0x001e;
        if (r0 == r3) goto L_0x0055;
    L_0x0034:
        r2 = r4 & 1;
        if (r2 != r1) goto L_0x003a;
    L_0x0038:
        r2 = r5;
        goto L_0x003d;
    L_0x003a:
        r2 = r4 / 2;
        r2 = -r2;
    L_0x003d:
        r3 = r2 & 1;
        if (r3 != 0) goto L_0x0044;
    L_0x0041:
        r2 = r2 / 2;
        goto L_0x003d;
    L_0x0044:
        if (r2 <= 0) goto L_0x0048;
    L_0x0046:
        r4 = -r2;
        goto L_0x0049;
    L_0x0048:
        r5 = r2;
    L_0x0049:
        r3 = r5 - r4;
        r2 = r3 / 2;
        if (r2 != 0) goto L_0x0054;
    L_0x004f:
        r3 = -r4;
        r1 = r1 << r0;
        r3 = r3 * r1;
        return r3;
    L_0x0054:
        goto L_0x003d;
    L_0x0055:
        r1 = new java.lang.ArithmeticException;
        r2 = "overflow: gcd is 2^31";
        r1.<init>(r2);
        throw r1;
        return r1;
        r0 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        if (r4 == r0) goto L_0x0070;
    L_0x0064:
        if (r5 == r0) goto L_0x0070;
    L_0x0066:
        r0 = java.lang.Math.abs(r4);
        r1 = java.lang.Math.abs(r5);
        r0 = r0 + r1;
        return r0;
        r0 = new java.lang.ArithmeticException;
        r1 = "overflow: gcd is 2^31";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.math.Fraction.greatestCommonDivisor(int, int):int");
    }

    private Fraction(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public static Fraction getFraction(int numerator, int denominator) {
        if (denominator != 0) {
            if (denominator < 0) {
                if (numerator == Integer.MIN_VALUE || denominator == Integer.MIN_VALUE) {
                    throw new ArithmeticException("overflow: can't negate");
                }
                numerator = -numerator;
                denominator = -denominator;
            }
            return new Fraction(numerator, denominator);
        }
        throw new ArithmeticException("The denominator must not be zero");
    }

    public static Fraction getFraction(int whole, int numerator, int denominator) {
        if (denominator == 0) {
            throw new ArithmeticException("The denominator must not be zero");
        } else if (denominator < 0) {
            throw new ArithmeticException("The denominator must not be negative");
        } else if (numerator >= 0) {
            long numeratorValue;
            if (whole < 0) {
                numeratorValue = (((long) whole) * ((long) denominator)) - ((long) numerator);
            } else {
                numeratorValue = (((long) whole) * ((long) denominator)) + ((long) numerator);
            }
            if (numeratorValue >= -2147483648L && numeratorValue <= 2147483647L) {
                return new Fraction((int) numeratorValue, denominator);
            }
            throw new ArithmeticException("Numerator too large to represent as an Integer.");
        } else {
            throw new ArithmeticException("The numerator must not be negative");
        }
    }

    public static Fraction getReducedFraction(int numerator, int denominator) {
        if (denominator == 0) {
            throw new ArithmeticException("The denominator must not be zero");
        } else if (numerator == 0) {
            return ZERO;
        } else {
            if (denominator == Integer.MIN_VALUE && (numerator & 1) == 0) {
                numerator /= 2;
                denominator /= 2;
            }
            if (denominator < 0) {
                if (numerator == Integer.MIN_VALUE || denominator == Integer.MIN_VALUE) {
                    throw new ArithmeticException("overflow: can't negate");
                }
                numerator = -numerator;
                denominator = -denominator;
            }
            int gcd = greatestCommonDivisor(numerator, denominator);
            return new Fraction(numerator / gcd, denominator / gcd);
        }
    }

    public static Fraction getFraction(String str) {
        Validate.isTrue(str != null, "The string must not be null", new Object[0]);
        if (str.indexOf(46) >= 0) {
            return getFraction(Double.parseDouble(str));
        }
        int pos = str.indexOf(32);
        if (pos > 0) {
            int whole = Integer.parseInt(str.substring(0, pos));
            str = str.substring(pos + 1);
            pos = str.indexOf(47);
            if (pos >= 0) {
                return getFraction(whole, Integer.parseInt(str.substring(0, pos)), Integer.parseInt(str.substring(pos + 1)));
            }
            throw new NumberFormatException("The fraction could not be parsed as the format X Y/Z");
        }
        pos = str.indexOf(47);
        if (pos < 0) {
            return getFraction(Integer.parseInt(str), 1);
        }
        return getFraction(Integer.parseInt(str.substring(0, pos)), Integer.parseInt(str.substring(pos + 1)));
    }

    public int getNumerator() {
        return this.numerator;
    }

    public int getDenominator() {
        return this.denominator;
    }

    public int getProperNumerator() {
        return Math.abs(this.numerator % this.denominator);
    }

    public int getProperWhole() {
        return this.numerator / this.denominator;
    }

    public int intValue() {
        return this.numerator / this.denominator;
    }

    public long longValue() {
        return ((long) this.numerator) / ((long) this.denominator);
    }

    public float floatValue() {
        return ((float) this.numerator) / ((float) this.denominator);
    }

    public double doubleValue() {
        double d = (double) this.numerator;
        double d2 = (double) this.denominator;
        Double.isNaN(d);
        Double.isNaN(d2);
        return d / d2;
    }

    public Fraction reduce() {
        int i = this.numerator;
        if (i == 0) {
            return equals(ZERO) ? this : ZERO;
        }
        i = greatestCommonDivisor(Math.abs(i), this.denominator);
        if (i == 1) {
            return this;
        }
        return getFraction(this.numerator / i, this.denominator / i);
    }

    public Fraction invert() {
        int i = this.numerator;
        if (i == 0) {
            throw new ArithmeticException("Unable to invert zero.");
        } else if (i == Integer.MIN_VALUE) {
            throw new ArithmeticException("overflow: can't negate numerator");
        } else if (i < 0) {
            return new Fraction(-this.denominator, -i);
        } else {
            return new Fraction(this.denominator, i);
        }
    }

    public Fraction negate() {
        int i = this.numerator;
        if (i != Integer.MIN_VALUE) {
            return new Fraction(-i, this.denominator);
        }
        throw new ArithmeticException("overflow: too large to negate");
    }

    public Fraction abs() {
        if (this.numerator >= 0) {
            return this;
        }
        return negate();
    }

    public Fraction pow(int power) {
        if (power == 1) {
            return this;
        }
        if (power == 0) {
            return ONE;
        }
        if (power >= 0) {
            Fraction f = multiplyBy(this);
            if (power % 2 == 0) {
                return f.pow(power / 2);
            }
            return f.pow(power / 2).multiplyBy(this);
        } else if (power == Integer.MIN_VALUE) {
            return invert().pow(2).pow(-(power / 2));
        } else {
            return invert().pow(-power);
        }
    }

    private static int mulAndCheck(int x, int y) {
        long m = ((long) x) * ((long) y);
        if (m >= -2147483648L && m <= 2147483647L) {
            return (int) m;
        }
        throw new ArithmeticException("overflow: mul");
    }

    private static int mulPosAndCheck(int x, int y) {
        long m = ((long) x) * ((long) y);
        if (m <= 2147483647L) {
            return (int) m;
        }
        throw new ArithmeticException("overflow: mulPos");
    }

    private static int addAndCheck(int x, int y) {
        long s = ((long) x) + ((long) y);
        if (s >= -2147483648L && s <= 2147483647L) {
            return (int) s;
        }
        throw new ArithmeticException("overflow: add");
    }

    private static int subAndCheck(int x, int y) {
        long s = ((long) x) - ((long) y);
        if (s >= -2147483648L && s <= 2147483647L) {
            return (int) s;
        }
        throw new ArithmeticException("overflow: add");
    }

    public Fraction add(Fraction fraction) {
        return addSub(fraction, true);
    }

    public Fraction subtract(Fraction fraction) {
        return addSub(fraction, false);
    }

    private Fraction addSub(Fraction fraction, boolean isAdd) {
        Validate.isTrue(fraction != null, "The fraction must not be null", new Object[0]);
        if (this.numerator == 0) {
            return isAdd ? fraction : fraction.negate();
        } else if (fraction.numerator == 0) {
            return this;
        } else {
            int d1 = greatestCommonDivisor(this.denominator, fraction.denominator);
            if (d1 == 1) {
                int uvp = mulAndCheck(this.numerator, fraction.denominator);
                int upv = mulAndCheck(fraction.numerator, this.denominator);
                return new Fraction(isAdd ? addAndCheck(uvp, upv) : subAndCheck(uvp, upv), mulPosAndCheck(this.denominator, fraction.denominator));
            }
            BigInteger uvp2 = BigInteger.valueOf((long) this.numerator).multiply(BigInteger.valueOf((long) (fraction.denominator / d1)));
            BigInteger upv2 = BigInteger.valueOf((long) fraction.numerator).multiply(BigInteger.valueOf((long) (this.denominator / d1)));
            BigInteger t = isAdd ? uvp2.add(upv2) : uvp2.subtract(upv2);
            int tmodd1 = t.mod(BigInteger.valueOf((long) d1)).intValue();
            int d2 = tmodd1 == 0 ? d1 : greatestCommonDivisor(tmodd1, d1);
            BigInteger w = t.divide(BigInteger.valueOf((long) d2));
            if (w.bitLength() <= 31) {
                return new Fraction(w.intValue(), mulPosAndCheck(this.denominator / d1, fraction.denominator / d2));
            }
            throw new ArithmeticException("overflow: numerator too large after multiply");
        }
    }

    public Fraction multiplyBy(Fraction fraction) {
        Validate.isTrue(fraction != null, "The fraction must not be null", new Object[0]);
        int d1 = this.numerator;
        if (d1 != 0) {
            if (fraction.numerator != 0) {
                d1 = greatestCommonDivisor(d1, fraction.denominator);
                int d2 = greatestCommonDivisor(fraction.numerator, this.denominator);
                return getReducedFraction(mulAndCheck(this.numerator / d1, fraction.numerator / d2), mulPosAndCheck(this.denominator / d2, fraction.denominator / d1));
            }
        }
        return ZERO;
    }

    public Fraction divideBy(Fraction fraction) {
        Validate.isTrue(fraction != null, "The fraction must not be null", new Object[0]);
        if (fraction.numerator != 0) {
            return multiplyBy(fraction.invert());
        }
        throw new ArithmeticException("The fraction to divide by must not be zero");
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Fraction)) {
            return false;
        }
        Fraction other = (Fraction) obj;
        if (getNumerator() != other.getNumerator() || getDenominator() != other.getDenominator()) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = ((getNumerator() + 629) * 37) + getDenominator();
        }
        return this.hashCode;
    }

    public int compareTo(Fraction other) {
        if (this == other) {
            return 0;
        }
        if (this.numerator == other.numerator && this.denominator == other.denominator) {
            return 0;
        }
        long first = ((long) this.numerator) * ((long) other.denominator);
        long second = ((long) other.numerator) * ((long) this.denominator);
        if (first == second) {
            return 0;
        }
        if (first < second) {
            return -1;
        }
        return 1;
    }

    public String toString() {
        if (this.toString == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getNumerator());
            stringBuilder.append("/");
            stringBuilder.append(getDenominator());
            this.toString = stringBuilder.toString();
        }
        return this.toString;
    }

    public String toProperString() {
        if (this.toProperString == null) {
            int i = this.numerator;
            if (i == 0) {
                this.toProperString = "0";
            } else {
                int i2 = this.denominator;
                if (i == i2) {
                    this.toProperString = "1";
                } else if (i == i2 * -1) {
                    this.toProperString = "-1";
                } else {
                    if (i > 0) {
                        i = -i;
                    }
                    if (i < (-this.denominator)) {
                        i = getProperNumerator();
                        if (i == 0) {
                            this.toProperString = Integer.toString(getProperWhole());
                        } else {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(getProperWhole());
                            stringBuilder.append(StringUtils.SPACE);
                            stringBuilder.append(i);
                            stringBuilder.append("/");
                            stringBuilder.append(getDenominator());
                            this.toProperString = stringBuilder.toString();
                        }
                    } else {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(getNumerator());
                        stringBuilder2.append("/");
                        stringBuilder2.append(getDenominator());
                        this.toProperString = stringBuilder2.toString();
                    }
                }
            }
        }
        return this.toProperString;
    }
}
