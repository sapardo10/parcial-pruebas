package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import org.apache.commons.io.FilenameUtils;

@Deprecated
public class WildcardFilter extends AbstractFileFilter implements Serializable {
    private static final long serialVersionUID = -5037645902506953517L;
    private final String[] wildcards;

    public WildcardFilter(String wildcard) {
        if (wildcard != null) {
            this.wildcards = new String[]{wildcard};
            return;
        }
        throw new IllegalArgumentException("The wildcard must not be null");
    }

    public WildcardFilter(String[] wildcards) {
        if (wildcards != null) {
            this.wildcards = new String[wildcards.length];
            System.arraycopy(wildcards, 0, this.wildcards, 0, wildcards.length);
            return;
        }
        throw new IllegalArgumentException("The wildcard array must not be null");
    }

    public WildcardFilter(List<String> wildcards) {
        if (wildcards != null) {
            this.wildcards = (String[]) wildcards.toArray(new String[wildcards.size()]);
            return;
        }
        throw new IllegalArgumentException("The wildcard list must not be null");
    }

    public boolean accept(File dir, String name) {
        if (dir != null && new File(dir, name).isDirectory()) {
            return false;
        }
        for (String wildcard : this.wildcards) {
            if (FilenameUtils.wildcardMatch(name, wildcard)) {
                return true;
            }
        }
        return false;
    }

    public boolean accept(File file) {
        if (file.isDirectory()) {
            return false;
        }
        for (String wildcard : this.wildcards) {
            if (FilenameUtils.wildcardMatch(file.getName(), wildcard)) {
                return true;
            }
        }
        return false;
    }
}
