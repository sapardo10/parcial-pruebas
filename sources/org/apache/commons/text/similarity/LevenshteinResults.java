package org.apache.commons.text.similarity;

import java.util.Objects;

public class LevenshteinResults {
    private final Integer deleteCount;
    private final Integer distance;
    private final Integer insertCount;
    private final Integer substituteCount;

    public LevenshteinResults(Integer distance, Integer insertCount, Integer deleteCount, Integer substituteCount) {
        this.distance = distance;
        this.insertCount = insertCount;
        this.deleteCount = deleteCount;
        this.substituteCount = substituteCount;
    }

    public Integer getDistance() {
        return this.distance;
    }

    public Integer getInsertCount() {
        return this.insertCount;
    }

    public Integer getDeleteCount() {
        return this.deleteCount;
    }

    public Integer getSubstituteCount() {
        return this.substituteCount;
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (this == o) {
            return true;
        }
        if (o != null) {
            if (getClass() == o.getClass()) {
                LevenshteinResults result = (LevenshteinResults) o;
                if (Objects.equals(this.distance, result.distance) && Objects.equals(this.insertCount, result.insertCount)) {
                    if (Objects.equals(this.deleteCount, result.deleteCount)) {
                        if (Objects.equals(this.substituteCount, result.substituteCount)) {
                            return z;
                        }
                    }
                }
                z = false;
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.distance, this.insertCount, this.deleteCount, this.substituteCount});
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Distance: ");
        stringBuilder.append(this.distance);
        stringBuilder.append(", Insert: ");
        stringBuilder.append(this.insertCount);
        stringBuilder.append(", Delete: ");
        stringBuilder.append(this.deleteCount);
        stringBuilder.append(", Substitute: ");
        stringBuilder.append(this.substituteCount);
        return stringBuilder.toString();
    }
}
