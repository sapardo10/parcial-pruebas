package de.danoeh.antennapod.core.util.gui;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import de.danoeh.antennapod.core.C0734R;

public class MoreContentListFooterUtil {
    private Listener listener;
    private boolean loading;
    private final View root;

    public interface Listener {
        void onClick();
    }

    public MoreContentListFooterUtil(View root) {
        this.root = root;
        root.setOnClickListener(new -$$Lambda$MoreContentListFooterUtil$4gQm2_WXOq1kCewwRkT8XEsJhAk());
    }

    public static /* synthetic */ void lambda$new$0(MoreContentListFooterUtil moreContentListFooterUtil, View v) {
        Listener listener = moreContentListFooterUtil.listener;
        if (listener != null && !moreContentListFooterUtil.loading) {
            listener.onClick();
        }
    }

    public void setLoadingState(boolean newState) {
        ImageView imageView = (ImageView) this.root.findViewById(C0734R.id.imgExpand);
        ProgressBar progressBar = (ProgressBar) this.root.findViewById(C0734R.id.progBar);
        if (newState) {
            imageView.setVisibility(8);
            progressBar.setVisibility(0);
        } else {
            imageView.setVisibility(0);
            progressBar.setVisibility(8);
        }
        this.loading = newState;
    }

    public void setClickListener(Listener l) {
        this.listener = l;
    }

    public View getRoot() {
        return this.root;
    }
}
