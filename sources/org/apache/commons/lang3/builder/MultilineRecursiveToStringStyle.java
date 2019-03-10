package org.apache.commons.lang3.builder;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

public class MultilineRecursiveToStringStyle extends RecursiveToStringStyle {
    private static final int INDENT = 2;
    private static final long serialVersionUID = 1;
    private int spaces = 2;

    public MultilineRecursiveToStringStyle() {
        resetIndent();
    }

    private void resetIndent() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append(spacer(this.spaces));
        setArrayStart(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append(",");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append(spacer(this.spaces));
        setArraySeparator(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append(spacer(this.spaces - 2));
        stringBuilder.append("}");
        setArrayEnd(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append(spacer(this.spaces));
        setContentStart(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append(",");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append(spacer(this.spaces));
        setFieldSeparator(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append(spacer(this.spaces - 2));
        stringBuilder.append("]");
        setContentEnd(stringBuilder.toString());
    }

    private StringBuilder spacer(int spaces) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < spaces; i++) {
            sb.append(StringUtils.SPACE);
        }
        return sb;
    }

    public void appendDetail(StringBuffer buffer, String fieldName, Object value) {
        if (ClassUtils.isPrimitiveWrapper(value.getClass()) || String.class.equals(value.getClass()) || !accept(value.getClass())) {
            super.appendDetail(buffer, fieldName, value);
            return;
        }
        this.spaces += 2;
        resetIndent();
        buffer.append(ReflectionToStringBuilder.toString(value, this));
        this.spaces -= 2;
        resetIndent();
    }

    protected void appendDetail(StringBuffer buffer, String fieldName, Object[] array) {
        this.spaces += 2;
        resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        resetIndent();
    }

    protected void reflectionAppendArrayDetail(StringBuffer buffer, String fieldName, Object array) {
        this.spaces += 2;
        resetIndent();
        super.reflectionAppendArrayDetail(buffer, fieldName, array);
        this.spaces -= 2;
        resetIndent();
    }

    protected void appendDetail(StringBuffer buffer, String fieldName, long[] array) {
        this.spaces += 2;
        resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        resetIndent();
    }

    protected void appendDetail(StringBuffer buffer, String fieldName, int[] array) {
        this.spaces += 2;
        resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        resetIndent();
    }

    protected void appendDetail(StringBuffer buffer, String fieldName, short[] array) {
        this.spaces += 2;
        resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        resetIndent();
    }

    protected void appendDetail(StringBuffer buffer, String fieldName, byte[] array) {
        this.spaces += 2;
        resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        resetIndent();
    }

    protected void appendDetail(StringBuffer buffer, String fieldName, char[] array) {
        this.spaces += 2;
        resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        resetIndent();
    }

    protected void appendDetail(StringBuffer buffer, String fieldName, double[] array) {
        this.spaces += 2;
        resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        resetIndent();
    }

    protected void appendDetail(StringBuffer buffer, String fieldName, float[] array) {
        this.spaces += 2;
        resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        resetIndent();
    }

    protected void appendDetail(StringBuffer buffer, String fieldName, boolean[] array) {
        this.spaces += 2;
        resetIndent();
        super.appendDetail(buffer, fieldName, array);
        this.spaces -= 2;
        resetIndent();
    }
}
