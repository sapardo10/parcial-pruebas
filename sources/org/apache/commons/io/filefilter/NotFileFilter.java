package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;

public class NotFileFilter extends AbstractFileFilter implements Serializable {
    private static final long serialVersionUID = 6131563330944994230L;
    private final IOFileFilter filter;

    public NotFileFilter(IOFileFilter filter) {
        if (filter != null) {
            this.filter = filter;
            return;
        }
        throw new IllegalArgumentException("The filter must not be null");
    }

    public boolean accept(File file) {
        return this.filter.accept(file) ^ 1;
    }

    public boolean accept(File file, String name) {
        return this.filter.accept(file, name) ^ 1;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString());
        stringBuilder.append("(");
        stringBuilder.append(this.filter.toString());
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
