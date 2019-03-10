package org.apache.commons.text.diff;

public abstract class EditCommand<T> {
    private final T object;

    public abstract void accept(CommandVisitor<T> commandVisitor);

    protected EditCommand(T object) {
        this.object = object;
    }

    protected T getObject() {
        return this.object;
    }
}
