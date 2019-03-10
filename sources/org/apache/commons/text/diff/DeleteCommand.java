package org.apache.commons.text.diff;

public class DeleteCommand<T> extends EditCommand<T> {
    public DeleteCommand(T object) {
        super(object);
    }

    public void accept(CommandVisitor<T> visitor) {
        visitor.visitDeleteCommand(getObject());
    }
}
