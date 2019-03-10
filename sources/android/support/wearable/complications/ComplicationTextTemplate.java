package android.support.wearable.complications;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

@TargetApi(24)
public class ComplicationTextTemplate implements Parcelable, TimeDependentText {
    public static final Creator<ComplicationTextTemplate> CREATOR = new C04071();
    private static final String KEY_SURROUNDING_STRING = "KEY_SURROUNDING_STRING";
    private static final String KEY_TIME_DEPENDENT_TEXTS = "KEY_TIME_DEPENDENT_TEXTS";
    private final ComplicationText[] mComplicationTexts;
    private final CharSequence mSurroundingText;

    /* renamed from: android.support.wearable.complications.ComplicationTextTemplate$1 */
    class C04071 implements Creator<ComplicationTextTemplate> {
        C04071() {
        }

        public ComplicationTextTemplate createFromParcel(Parcel in) {
            return new ComplicationTextTemplate(in);
        }

        public ComplicationTextTemplate[] newArray(int size) {
            return new ComplicationTextTemplate[size];
        }
    }

    public static final class Builder {
        private CharSequence mSurroundingText;
        private final List<ComplicationText> mTexts = new ArrayList(2);

        public Builder addComplicationText(@NonNull ComplicationText text) {
            this.mTexts.add(text);
            return this;
        }

        public Builder setSurroundingText(CharSequence surroundingText) {
            this.mSurroundingText = surroundingText;
            return this;
        }

        public ComplicationTextTemplate build() {
            if (this.mTexts.isEmpty()) {
                throw new IllegalStateException("At least one text must be specified.");
            }
            CharSequence charSequence = this.mSurroundingText;
            List list = this.mTexts;
            return new ComplicationTextTemplate(charSequence, (ComplicationText[]) list.toArray(new ComplicationText[list.size()]));
        }
    }

    private ComplicationTextTemplate(CharSequence surroundingText, ComplicationText[] complicationTexts) {
        this.mSurroundingText = surroundingText;
        this.mComplicationTexts = complicationTexts;
        checkFields();
    }

    private ComplicationTextTemplate(Parcel in) {
        this(in.readBundle(ComplicationTextTemplate.class.getClassLoader()));
    }

    private ComplicationTextTemplate(Bundle rootBundle) {
        this.mSurroundingText = rootBundle.getCharSequence(KEY_SURROUNDING_STRING);
        Parcelable[] texts = rootBundle.getParcelableArray(KEY_TIME_DEPENDENT_TEXTS);
        this.mComplicationTexts = new ComplicationText[texts.length];
        for (int i = 0; i < texts.length; i++) {
            this.mComplicationTexts[i] = (ComplicationText) texts[i];
        }
        checkFields();
    }

    private void checkFields() {
        if (this.mSurroundingText == null) {
            if (this.mComplicationTexts.length == 0) {
                throw new IllegalStateException("One of mSurroundingText and mTimeDependentText must be non-null");
            }
        }
    }

    public void writeToParcel(Parcel out, int flags) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence(KEY_SURROUNDING_STRING, this.mSurroundingText);
        bundle.putParcelableArray(KEY_TIME_DEPENDENT_TEXTS, this.mComplicationTexts);
        out.writeBundle(bundle);
    }

    public CharSequence getText(Context context, long dateTimeMillis) {
        int len = this.mComplicationTexts.length;
        if (len == 0) {
            return this.mSurroundingText;
        }
        CharSequence[] timeDependentParts = new CharSequence[len];
        for (int i = 0; i < len; i++) {
            timeDependentParts[i] = this.mComplicationTexts[i].getText(context, dateTimeMillis);
        }
        CharSequence charSequence = this.mSurroundingText;
        if (charSequence == null) {
            return TextUtils.join(StringUtils.SPACE, timeDependentParts);
        }
        return TextUtils.expandTemplate(charSequence, timeDependentParts);
    }

    public boolean returnsSameText(long firstDateTimeMillis, long secondDateTimeMillis) {
        for (TimeDependentText text : this.mComplicationTexts) {
            if (!text.returnsSameText(firstDateTimeMillis, secondDateTimeMillis)) {
                return false;
            }
        }
        return true;
    }

    public int describeContents() {
        return 0;
    }
}
