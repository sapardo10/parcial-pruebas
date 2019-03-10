package com.google.android.exoplayer2.trackselection;

import com.google.android.exoplayer2.RendererConfiguration;
import com.google.android.exoplayer2.util.Util;

public final class TrackSelectorResult {
    public final Object info;
    public final int length;
    public final RendererConfiguration[] rendererConfigurations;
    public final TrackSelectionArray selections;

    public TrackSelectorResult(RendererConfiguration[] rendererConfigurations, TrackSelection[] selections, Object info) {
        this.rendererConfigurations = rendererConfigurations;
        this.selections = new TrackSelectionArray(selections);
        this.info = info;
        this.length = rendererConfigurations.length;
    }

    public boolean isRendererEnabled(int index) {
        return this.rendererConfigurations[index] != null;
    }

    public boolean isEquivalent(TrackSelectorResult other) {
        if (other != null) {
            if (other.selections.length == this.selections.length) {
                for (int i = 0; i < this.selections.length; i++) {
                    if (!isEquivalent(other, i)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean isEquivalent(TrackSelectorResult other, int index) {
        boolean z = false;
        if (other == null) {
            return false;
        }
        if (Util.areEqual(this.rendererConfigurations[index], other.rendererConfigurations[index])) {
            if (Util.areEqual(this.selections.get(index), other.selections.get(index))) {
                z = true;
                return z;
            }
        }
        return z;
    }
}
