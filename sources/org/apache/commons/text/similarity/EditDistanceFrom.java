package org.apache.commons.text.similarity;

import org.apache.commons.lang3.Validate;

public class EditDistanceFrom<R> {
    private final EditDistance<R> editDistance;
    private final CharSequence left;

    public EditDistanceFrom(EditDistance<R> editDistance, CharSequence left) {
        Validate.isTrue(editDistance != null, "The edit distance may not be null.", new Object[0]);
        this.editDistance = editDistance;
        this.left = left;
    }

    public R apply(CharSequence right) {
        return this.editDistance.apply(this.left, right);
    }

    public CharSequence getLeft() {
        return this.left;
    }

    public EditDistance<R> getEditDistance() {
        return this.editDistance;
    }
}
