package org.apache.commons.text.diff;

public class KeepCommand<T> extends EditCommand<T> {
    public KeepCommand(T object) {
        super(object);
    }

    public void accept(CommandVisitor<T> visitor) {
        visitor.visitKeepCommand(getObject());
    }
}
