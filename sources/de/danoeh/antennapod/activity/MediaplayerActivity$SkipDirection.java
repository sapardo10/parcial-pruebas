package de.danoeh.antennapod.activity;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.widget.TextView;
import de.danoeh.antennapod.core.util.Consumer;
import de.danoeh.antennapod.core.util.Function;
import de.danoeh.antennapod.core.util.Supplier;
import de.danoeh.antennapod.debug.R;

public enum MediaplayerActivity$SkipDirection {
    SKIP_FORWARD(-$$Lambda$4cqA4kZ7HqFsP6DrP7kwbpB7jRM.INSTANCE, C1016x335a07bb.INSTANCE, -$$Lambda$Nc0h0MxyKEbaHyvYpXza_3KpGX0.INSTANCE, R.string.pref_fast_forward),
    SKIP_REWIND(-$$Lambda$Lk4nqNmbp8ZxvddbFxIiywsZgsw.INSTANCE, C1015x352fd72e.INSTANCE, -$$Lambda$PUDwvmQNaFubEt5TwXSlHHv6TUs.INSTANCE, R.string.pref_rewind);
    
    private final Supplier<Integer> getPrefSecsFn;
    private final Function<MediaplayerActivity, TextView> getTextViewFn;
    private final Consumer<Integer> setPrefSecsFn;
    private final int titleResourceID;

    private MediaplayerActivity$SkipDirection(Supplier<Integer> getPrefSecsFn, Function<MediaplayerActivity, TextView> getTextViewFn, Consumer<Integer> setPrefSecsFn, int titleResourceID) {
        this.getPrefSecsFn = getPrefSecsFn;
        this.getTextViewFn = getTextViewFn;
        this.setPrefSecsFn = setPrefSecsFn;
        this.titleResourceID = titleResourceID;
    }

    public int getPrefSkipSeconds() {
        return ((Integer) this.getPrefSecsFn.get()).intValue();
    }

    public void setPrefSkipSeconds(int seconds, @Nullable Activity activity) {
        this.setPrefSecsFn.accept(Integer.valueOf(seconds));
        if (activity != null && (activity instanceof MediaplayerActivity)) {
            TextView tv = (TextView) this.getTextViewFn.apply((MediaplayerActivity) activity);
            if (tv != null) {
                tv.setText(String.valueOf(seconds));
            }
        }
    }

    public int getTitleResourceID() {
        return this.titleResourceID;
    }
}
