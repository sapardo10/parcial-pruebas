package android.support.v7.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v7.preference.Preference.BaseSavedState;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SeekBarPreference extends Preference {
    private static final String TAG = "SeekBarPreference";
    private boolean mAdjustable;
    private int mMax;
    private int mMin;
    private SeekBar mSeekBar;
    private OnSeekBarChangeListener mSeekBarChangeListener;
    private int mSeekBarIncrement;
    private OnKeyListener mSeekBarKeyListener;
    private int mSeekBarValue;
    private TextView mSeekBarValueTextView;
    private boolean mShowSeekBarValue;
    private boolean mTrackingTouch;

    /* renamed from: android.support.v7.preference.SeekBarPreference$1 */
    class C03161 implements OnSeekBarChangeListener {
        C03161() {
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser && !SeekBarPreference.this.mTrackingTouch) {
                SeekBarPreference.this.syncValueInternal(seekBar);
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            SeekBarPreference.this.mTrackingTouch = true;
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            SeekBarPreference.this.mTrackingTouch = false;
            if (seekBar.getProgress() + SeekBarPreference.this.mMin != SeekBarPreference.this.mSeekBarValue) {
                SeekBarPreference.this.syncValueInternal(seekBar);
            }
        }
    }

    /* renamed from: android.support.v7.preference.SeekBarPreference$2 */
    class C03172 implements OnKeyListener {
        C03172() {
        }

        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() != 0) {
                return false;
            }
            if (!SeekBarPreference.this.mAdjustable && (keyCode == 21 || keyCode == 22)) {
                return false;
            }
            if (keyCode != 23) {
                if (keyCode != 66) {
                    if (SeekBarPreference.this.mSeekBar != null) {
                        return SeekBarPreference.this.mSeekBar.onKeyDown(keyCode, event);
                    }
                    Log.e(SeekBarPreference.TAG, "SeekBar view is null and hence cannot be adjusted.");
                    return false;
                }
            }
            return false;
        }
    }

    private static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new C03181();
        int max;
        int min;
        int seekBarValue;

        /* renamed from: android.support.v7.preference.SeekBarPreference$SavedState$1 */
        static class C03181 implements Creator<SavedState> {
            C03181() {
            }

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        }

        public SavedState(Parcel source) {
            super(source);
            this.seekBarValue = source.readInt();
            this.min = source.readInt();
            this.max = source.readInt();
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.seekBarValue);
            dest.writeInt(this.min);
            dest.writeInt(this.max);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }
    }

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mSeekBarChangeListener = new C03161();
        this.mSeekBarKeyListener = new C03172();
        TypedArray a = context.obtainStyledAttributes(attrs, C0315R.styleable.SeekBarPreference, defStyleAttr, defStyleRes);
        this.mMin = a.getInt(C0315R.styleable.SeekBarPreference_min, 0);
        setMax(a.getInt(C0315R.styleable.SeekBarPreference_android_max, 100));
        setSeekBarIncrement(a.getInt(C0315R.styleable.SeekBarPreference_seekBarIncrement, 0));
        this.mAdjustable = a.getBoolean(C0315R.styleable.SeekBarPreference_adjustable, true);
        this.mShowSeekBarValue = a.getBoolean(C0315R.styleable.SeekBarPreference_showSeekBarValue, true);
        a.recycle();
    }

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SeekBarPreference(Context context, AttributeSet attrs) {
        this(context, attrs, C0315R.attr.seekBarPreferenceStyle);
    }

    public SeekBarPreference(Context context) {
        this(context, null);
    }

    public void onBindViewHolder(PreferenceViewHolder view) {
        super.onBindViewHolder(view);
        view.itemView.setOnKeyListener(this.mSeekBarKeyListener);
        this.mSeekBar = (SeekBar) view.findViewById(C0315R.id.seekbar);
        this.mSeekBarValueTextView = (TextView) view.findViewById(C0315R.id.seekbar_value);
        if (this.mShowSeekBarValue) {
            this.mSeekBarValueTextView.setVisibility(0);
        } else {
            this.mSeekBarValueTextView.setVisibility(8);
            this.mSeekBarValueTextView = null;
        }
        SeekBar seekBar = this.mSeekBar;
        if (seekBar == null) {
            Log.e(TAG, "SeekBar view is null in onBindViewHolder.");
            return;
        }
        seekBar.setOnSeekBarChangeListener(this.mSeekBarChangeListener);
        this.mSeekBar.setMax(this.mMax - this.mMin);
        int i = this.mSeekBarIncrement;
        if (i != 0) {
            this.mSeekBar.setKeyProgressIncrement(i);
        } else {
            this.mSeekBarIncrement = this.mSeekBar.getKeyProgressIncrement();
        }
        this.mSeekBar.setProgress(this.mSeekBarValue - this.mMin);
        TextView textView = this.mSeekBarValueTextView;
        if (textView != null) {
            textView.setText(String.valueOf(this.mSeekBarValue));
        }
        this.mSeekBar.setEnabled(isEnabled());
    }

    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        int persistedInt;
        if (restoreValue) {
            persistedInt = getPersistedInt(this.mSeekBarValue);
        } else {
            persistedInt = ((Integer) defaultValue).intValue();
        }
        setValue(persistedInt);
    }

    protected Object onGetDefaultValue(TypedArray a, int index) {
        return Integer.valueOf(a.getInt(index, 0));
    }

    public void setMin(int min) {
        if (min > this.mMax) {
            min = this.mMax;
        }
        if (min != this.mMin) {
            this.mMin = min;
            notifyChanged();
        }
    }

    public int getMin() {
        return this.mMin;
    }

    public final void setMax(int max) {
        if (max < this.mMin) {
            max = this.mMin;
        }
        if (max != this.mMax) {
            this.mMax = max;
            notifyChanged();
        }
    }

    public final int getSeekBarIncrement() {
        return this.mSeekBarIncrement;
    }

    public final void setSeekBarIncrement(int seekBarIncrement) {
        if (seekBarIncrement != this.mSeekBarIncrement) {
            this.mSeekBarIncrement = Math.min(this.mMax - this.mMin, Math.abs(seekBarIncrement));
            notifyChanged();
        }
    }

    public int getMax() {
        return this.mMax;
    }

    public void setAdjustable(boolean adjustable) {
        this.mAdjustable = adjustable;
    }

    public boolean isAdjustable() {
        return this.mAdjustable;
    }

    public void setValue(int seekBarValue) {
        setValueInternal(seekBarValue, true);
    }

    private void setValueInternal(int seekBarValue, boolean notifyChanged) {
        if (seekBarValue < this.mMin) {
            seekBarValue = this.mMin;
        }
        if (seekBarValue > this.mMax) {
            seekBarValue = this.mMax;
        }
        if (seekBarValue != this.mSeekBarValue) {
            this.mSeekBarValue = seekBarValue;
            TextView textView = this.mSeekBarValueTextView;
            if (textView != null) {
                textView.setText(String.valueOf(this.mSeekBarValue));
            }
            persistInt(seekBarValue);
            if (notifyChanged) {
                notifyChanged();
            }
        }
    }

    public int getValue() {
        return this.mSeekBarValue;
    }

    private void syncValueInternal(SeekBar seekBar) {
        int seekBarValue = this.mMin + seekBar.getProgress();
        if (seekBarValue == this.mSeekBarValue) {
            return;
        }
        if (callChangeListener(Integer.valueOf(seekBarValue))) {
            setValueInternal(seekBarValue, false);
        } else {
            seekBar.setProgress(this.mSeekBarValue - this.mMin);
        }
    }

    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            return superState;
        }
        SavedState myState = new SavedState(superState);
        myState.seekBarValue = this.mSeekBarValue;
        myState.min = this.mMin;
        myState.max = this.mMax;
        return myState;
    }

    protected void onRestoreInstanceState(Parcelable state) {
        if (state.getClass().equals(SavedState.class)) {
            SavedState myState = (SavedState) state;
            super.onRestoreInstanceState(myState.getSuperState());
            this.mSeekBarValue = myState.seekBarValue;
            this.mMin = myState.min;
            this.mMax = myState.max;
            notifyChanged();
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
