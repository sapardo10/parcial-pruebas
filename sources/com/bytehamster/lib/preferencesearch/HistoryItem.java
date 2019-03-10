package com.bytehamster.lib.preferencesearch;

class HistoryItem extends ListItem {
    static final int TYPE = 1;
    private String term;

    HistoryItem(String term) {
        this.term = term;
    }

    public int getType() {
        return 1;
    }

    String getTerm() {
        return this.term;
    }

    public boolean equals(Object obj) {
        if (obj instanceof HistoryItem) {
            return ((HistoryItem) obj).term.equals(this.term);
        }
        return false;
    }
}
