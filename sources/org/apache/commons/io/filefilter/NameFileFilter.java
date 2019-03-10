package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import org.apache.commons.io.IOCase;

public class NameFileFilter extends AbstractFileFilter implements Serializable {
    private static final long serialVersionUID = 176844364689077340L;
    private final IOCase caseSensitivity;
    private final String[] names;

    public NameFileFilter(String name) {
        this(name, null);
    }

    public NameFileFilter(String name, IOCase caseSensitivity) {
        if (name != null) {
            this.names = new String[]{name};
            this.caseSensitivity = caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity;
            return;
        }
        throw new IllegalArgumentException("The wildcard must not be null");
    }

    public NameFileFilter(String[] names) {
        this(names, null);
    }

    public NameFileFilter(String[] names, IOCase caseSensitivity) {
        if (names != null) {
            this.names = new String[names.length];
            System.arraycopy(names, 0, this.names, 0, names.length);
            this.caseSensitivity = caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity;
            return;
        }
        throw new IllegalArgumentException("The array of names must not be null");
    }

    public NameFileFilter(List<String> names) {
        this((List) names, null);
    }

    public NameFileFilter(List<String> names, IOCase caseSensitivity) {
        if (names != null) {
            this.names = (String[]) names.toArray(new String[names.size()]);
            this.caseSensitivity = caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity;
            return;
        }
        throw new IllegalArgumentException("The list of names must not be null");
    }

    public boolean accept(File file) {
        String name = file.getName();
        for (String name2 : this.names) {
            if (this.caseSensitivity.checkEquals(name, name2)) {
                return true;
            }
        }
        return false;
    }

    public boolean accept(File dir, String name) {
        for (String name2 : this.names) {
            if (this.caseSensitivity.checkEquals(name, name2)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(super.toString());
        buffer.append("(");
        if (this.names != null) {
            for (int i = 0; i < this.names.length; i++) {
                if (i > 0) {
                    buffer.append(",");
                }
                buffer.append(this.names[i]);
            }
        }
        buffer.append(")");
        return buffer.toString();
    }
}
