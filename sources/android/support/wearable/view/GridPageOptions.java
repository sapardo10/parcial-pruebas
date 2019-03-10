package android.support.wearable.view;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;

@TargetApi(20)
@Deprecated
public interface GridPageOptions {

    public interface BackgroundListener {
        void notifyBackgroundChanged();
    }

    Drawable getBackground();

    void setBackgroundListener(BackgroundListener backgroundListener);
}
