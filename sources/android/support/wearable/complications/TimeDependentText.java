package android.support.wearable.complications;

import android.content.Context;
import android.os.Parcelable;

public interface TimeDependentText extends Parcelable {
    CharSequence getText(Context context, long j);

    boolean returnsSameText(long j, long j2);
}
