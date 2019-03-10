package org.apache.commons.text.diff;

import java.util.ArrayList;
import java.util.List;

public class ReplacementsFinder<T> implements CommandVisitor<T> {
    private final ReplacementsHandler<T> handler;
    private final List<T> pendingDeletions = new ArrayList();
    private final List<T> pendingInsertions = new ArrayList();
    private int skipped = 0;

    public ReplacementsFinder(ReplacementsHandler<T> handler) {
        this.handler = handler;
    }

    public void visitInsertCommand(T object) {
        this.pendingInsertions.add(object);
    }

    public void visitKeepCommand(T t) {
        if (this.pendingDeletions.isEmpty() && this.pendingInsertions.isEmpty()) {
            this.skipped++;
            return;
        }
        this.handler.handleReplacement(this.skipped, this.pendingDeletions, this.pendingInsertions);
        this.pendingDeletions.clear();
        this.pendingInsertions.clear();
        this.skipped = 1;
    }

    public void visitDeleteCommand(T object) {
        this.pendingDeletions.add(object);
    }
}
