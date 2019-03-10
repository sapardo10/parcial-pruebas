package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;

public class SizeFileFilter extends AbstractFileFilter implements Serializable {
    private static final long serialVersionUID = 7388077430788600069L;
    private final boolean acceptLarger;
    private final long size;

    public SizeFileFilter(long size) {
        this(size, true);
    }

    public SizeFileFilter(long size, boolean acceptLarger) {
        if (size >= 0) {
            this.size = size;
            this.acceptLarger = acceptLarger;
            return;
        }
        throw new IllegalArgumentException("The size must be non-negative");
    }

    public boolean accept(File file) {
        boolean smaller = file.length() < this.size;
        if (!this.acceptLarger) {
            return smaller;
        }
        if (smaller) {
            return false;
        }
        return true;
    }

    public String toString() {
        String condition = this.acceptLarger ? ">=" : "<";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString());
        stringBuilder.append("(");
        stringBuilder.append(condition);
        stringBuilder.append(this.size);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
