package de.danoeh.antennapod.core.util;

import java.util.concurrent.Callable;

public interface ShownotesProvider {
    Callable<String> loadShownotes();
}
