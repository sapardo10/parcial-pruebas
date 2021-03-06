package org.apache.commons.lang3.mutable;

import org.apache.commons.lang3.math.NumberUtils;

public class MutableByte extends Number implements Comparable<MutableByte>, Mutable<Number> {
    private static final long serialVersionUID = -1585823265;
    private byte value;

    public MutableByte(byte value) {
        this.value = value;
    }

    public MutableByte(Number value) {
        this.value = value.byteValue();
    }

    public MutableByte(String value) throws NumberFormatException {
        this.value = Byte.parseByte(value);
    }

    public Byte getValue() {
        return Byte.valueOf(this.value);
    }

    public void setValue(byte value) {
        this.value = value;
    }

    public void setValue(Number value) {
        this.value = value.byteValue();
    }

    public void increment() {
        this.value = (byte) (this.value + 1);
    }

    public byte getAndIncrement() {
        byte last = this.value;
        this.value = (byte) (this.value + 1);
        return last;
    }

    public byte incrementAndGet() {
        this.value = (byte) (this.value + 1);
        return this.value;
    }

    public void decrement() {
        this.value = (byte) (this.value - 1);
    }

    public byte getAndDecrement() {
        byte last = this.value;
        this.value = (byte) (this.value - 1);
        return last;
    }

    public byte decrementAndGet() {
        this.value = (byte) (this.value - 1);
        return this.value;
    }

    public void add(byte operand) {
        this.value = (byte) (this.value + operand);
    }

    public void add(Number operand) {
        this.value = (byte) (this.value + operand.byteValue());
    }

    public void subtract(byte operand) {
        this.value = (byte) (this.value - operand);
    }

    public void subtract(Number operand) {
        this.value = (byte) (this.value - operand.byteValue());
    }

    public byte addAndGet(byte operand) {
        this.value = (byte) (this.value + operand);
        return this.value;
    }

    public byte addAndGet(Number operand) {
        this.value = (byte) (this.value + operand.byteValue());
        return this.value;
    }

    public byte getAndAdd(byte operand) {
        byte last = this.value;
        this.value = (byte) (this.value + operand);
        return last;
    }

    public byte getAndAdd(Number operand) {
        byte last = this.value;
        this.value = (byte) (this.value + operand.byteValue());
        return last;
    }

    public byte byteValue() {
        return this.value;
    }

    public int intValue() {
        return this.value;
    }

    public long longValue() {
        return (long) this.value;
    }

    public float floatValue() {
        return (float) this.value;
    }

    public double doubleValue() {
        return (double) this.value;
    }

    public Byte toByte() {
        return Byte.valueOf(byteValue());
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (!(obj instanceof MutableByte)) {
            return false;
        }
        if (this.value == ((MutableByte) obj).byteValue()) {
            z = true;
        }
        return z;
    }

    public int hashCode() {
        return this.value;
    }

    public int compareTo(MutableByte other) {
        return NumberUtils.compare(this.value, other.value);
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}
