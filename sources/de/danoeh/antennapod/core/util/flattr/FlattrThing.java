package de.danoeh.antennapod.core.util.flattr;

public interface FlattrThing {
    FlattrStatus getFlattrStatus();

    String getPaymentLink();

    String getTitle();
}
