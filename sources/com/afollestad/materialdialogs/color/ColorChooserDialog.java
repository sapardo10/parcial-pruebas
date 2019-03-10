package com.afollestad.materialdialogs.color;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.commons.C0502R;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.util.DialogUtils;
import java.io.Serializable;

public class ColorChooserDialog extends DialogFragment implements OnClickListener, OnLongClickListener {
    public static final String TAG_ACCENT = "[MD_COLOR_CHOOSER]";
    public static final String TAG_CUSTOM = "[MD_COLOR_CHOOSER]";
    public static final String TAG_PRIMARY = "[MD_COLOR_CHOOSER]";
    private ColorCallback mCallback;
    private int mCircleSize;
    private View mColorChooserCustomFrame;
    @Nullable
    private int[][] mColorsSub;
    @NonNull
    private int[] mColorsTop;
    private EditText mCustomColorHex;
    private View mCustomColorIndicator;
    private OnSeekBarChangeListener mCustomColorRgbListener;
    private TextWatcher mCustomColorTextWatcher;
    private SeekBar mCustomSeekA;
    private TextView mCustomSeekAValue;
    private SeekBar mCustomSeekB;
    private TextView mCustomSeekBValue;
    private SeekBar mCustomSeekG;
    private TextView mCustomSeekGValue;
    private SeekBar mCustomSeekR;
    private TextView mCustomSeekRValue;
    private GridView mGrid;
    private int mSelectedCustomColor;

    /* renamed from: com.afollestad.materialdialogs.color.ColorChooserDialog$1 */
    class C04991 implements OnShowListener {
        C04991() {
        }

        public void onShow(DialogInterface dialog) {
            ColorChooserDialog.this.invalidateDynamicButtonColors();
        }
    }

    /* renamed from: com.afollestad.materialdialogs.color.ColorChooserDialog$5 */
    class C05005 implements TextWatcher {
        C05005() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                ColorChooserDialog colorChooserDialog = ColorChooserDialog.this;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("#");
                stringBuilder.append(s.toString());
                colorChooserDialog.mSelectedCustomColor = Color.parseColor(stringBuilder.toString());
            } catch (IllegalArgumentException e) {
                ColorChooserDialog.this.mSelectedCustomColor = ViewCompat.MEASURED_STATE_MASK;
            }
            ColorChooserDialog.this.mCustomColorIndicator.setBackgroundColor(ColorChooserDialog.this.mSelectedCustomColor);
            if (ColorChooserDialog.this.mCustomSeekA.getVisibility() == 0) {
                ColorChooserDialog.this.mCustomSeekA.setProgress(Color.alpha(ColorChooserDialog.this.mSelectedCustomColor));
                ColorChooserDialog.this.mCustomSeekAValue.setText(String.format("%d", new Object[]{Integer.valueOf(alpha)}));
            }
            if (ColorChooserDialog.this.mCustomSeekA.getVisibility() == 0) {
                ColorChooserDialog.this.mCustomSeekA.setProgress(Color.alpha(ColorChooserDialog.this.mSelectedCustomColor));
            }
            ColorChooserDialog.this.mCustomSeekR.setProgress(Color.red(ColorChooserDialog.this.mSelectedCustomColor));
            ColorChooserDialog.this.mCustomSeekG.setProgress(Color.green(ColorChooserDialog.this.mSelectedCustomColor));
            ColorChooserDialog.this.mCustomSeekB.setProgress(Color.blue(ColorChooserDialog.this.mSelectedCustomColor));
            ColorChooserDialog.this.isInSub(false);
            ColorChooserDialog.this.topIndex(-1);
            ColorChooserDialog.this.subIndex(-1);
            ColorChooserDialog.this.invalidateDynamicButtonColors();
        }

        public void afterTextChanged(Editable s) {
        }
    }

    /* renamed from: com.afollestad.materialdialogs.color.ColorChooserDialog$6 */
    class C05016 implements OnSeekBarChangeListener {
        C05016() {
        }

        @SuppressLint({"DefaultLocale"})
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                int color;
                if (ColorChooserDialog.this.getBuilder().mAllowUserCustomAlpha) {
                    color = Color.argb(ColorChooserDialog.this.mCustomSeekA.getProgress(), ColorChooserDialog.this.mCustomSeekR.getProgress(), ColorChooserDialog.this.mCustomSeekG.getProgress(), ColorChooserDialog.this.mCustomSeekB.getProgress());
                    ColorChooserDialog.this.mCustomColorHex.setText(String.format("%08X", new Object[]{Integer.valueOf(color)}));
                } else {
                    color = Color.rgb(ColorChooserDialog.this.mCustomSeekR.getProgress(), ColorChooserDialog.this.mCustomSeekG.getProgress(), ColorChooserDialog.this.mCustomSeekB.getProgress());
                    ColorChooserDialog.this.mCustomColorHex.setText(String.format("%06X", new Object[]{Integer.valueOf(ViewCompat.MEASURED_SIZE_MASK & color)}));
                }
            }
            ColorChooserDialog.this.mCustomSeekAValue.setText(String.format("%d", new Object[]{Integer.valueOf(ColorChooserDialog.this.mCustomSeekA.getProgress())}));
            ColorChooserDialog.this.mCustomSeekRValue.setText(String.format("%d", new Object[]{Integer.valueOf(ColorChooserDialog.this.mCustomSeekR.getProgress())}));
            ColorChooserDialog.this.mCustomSeekGValue.setText(String.format("%d", new Object[]{Integer.valueOf(ColorChooserDialog.this.mCustomSeekG.getProgress())}));
            ColorChooserDialog.this.mCustomSeekBValue.setText(String.format("%d", new Object[]{Integer.valueOf(ColorChooserDialog.this.mCustomSeekB.getProgress())}));
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    public static class Builder implements Serializable {
        protected boolean mAccentMode = false;
        protected boolean mAllowUserCustom = true;
        protected boolean mAllowUserCustomAlpha = true;
        @StringRes
        protected int mBackBtn = C0502R.string.md_back_label;
        @StringRes
        protected int mCancelBtn = C0502R.string.md_cancel_label;
        @Nullable
        protected int[][] mColorsSub;
        @Nullable
        protected int[] mColorsTop;
        @NonNull
        protected final transient AppCompatActivity mContext;
        @StringRes
        protected int mCustomBtn = C0502R.string.md_custom_label;
        @StringRes
        protected int mDoneBtn = C0502R.string.md_done_label;
        protected boolean mDynamicButtonColor = true;
        @ColorInt
        protected int mPreselect;
        @StringRes
        protected int mPresetsBtn = C0502R.string.md_presets_label;
        protected boolean mSetPreselectionColor = false;
        @Nullable
        protected String mTag;
        @Nullable
        protected Theme mTheme;
        @StringRes
        protected final int mTitle;
        @StringRes
        protected int mTitleSub;

        public <ActivityType extends AppCompatActivity & ColorCallback> Builder(@NonNull ActivityType context, @StringRes int title) {
            this.mContext = context;
            this.mTitle = title;
        }

        @NonNull
        public Builder titleSub(@StringRes int titleSub) {
            this.mTitleSub = titleSub;
            return this;
        }

        @NonNull
        public Builder tag(@Nullable String tag) {
            this.mTag = tag;
            return this;
        }

        @NonNull
        public Builder theme(@NonNull Theme theme) {
            this.mTheme = theme;
            return this;
        }

        @NonNull
        public Builder preselect(@ColorInt int preselect) {
            this.mPreselect = preselect;
            this.mSetPreselectionColor = true;
            return this;
        }

        @NonNull
        public Builder accentMode(boolean accentMode) {
            this.mAccentMode = accentMode;
            return this;
        }

        @NonNull
        public Builder doneButton(@StringRes int text) {
            this.mDoneBtn = text;
            return this;
        }

        @NonNull
        public Builder backButton(@StringRes int text) {
            this.mBackBtn = text;
            return this;
        }

        @NonNull
        public Builder cancelButton(@StringRes int text) {
            this.mCancelBtn = text;
            return this;
        }

        @NonNull
        public Builder customButton(@StringRes int text) {
            this.mCustomBtn = text;
            return this;
        }

        @NonNull
        public Builder presetsButton(@StringRes int text) {
            this.mPresetsBtn = text;
            return this;
        }

        @NonNull
        public Builder dynamicButtonColor(boolean enabled) {
            this.mDynamicButtonColor = enabled;
            return this;
        }

        @NonNull
        public Builder customColors(@NonNull int[] topLevel, @Nullable int[][] subLevel) {
            this.mColorsTop = topLevel;
            this.mColorsSub = subLevel;
            return this;
        }

        @NonNull
        public Builder customColors(@ArrayRes int topLevel, @Nullable int[][] subLevel) {
            this.mColorsTop = DialogUtils.getColorArray(this.mContext, topLevel);
            this.mColorsSub = subLevel;
            return this;
        }

        @NonNull
        public Builder allowUserColorInput(boolean allow) {
            this.mAllowUserCustom = allow;
            return this;
        }

        @NonNull
        public Builder allowUserColorInputAlpha(boolean allow) {
            this.mAllowUserCustomAlpha = allow;
            return this;
        }

        @NonNull
        public ColorChooserDialog build() {
            ColorChooserDialog dialog = new ColorChooserDialog();
            Bundle args = new Bundle();
            args.putSerializable("builder", this);
            dialog.setArguments(args);
            return dialog;
        }

        @NonNull
        public ColorChooserDialog show() {
            ColorChooserDialog dialog = build();
            dialog.show(this.mContext);
            return dialog;
        }
    }

    public interface ColorCallback {
        void onColorSelection(@NonNull ColorChooserDialog colorChooserDialog, @ColorInt int i);
    }

    private class ColorGridAdapter extends BaseAdapter {
        public int getCount() {
            if (ColorChooserDialog.this.isInSub()) {
                return ColorChooserDialog.this.mColorsSub[ColorChooserDialog.this.topIndex()].length;
            }
            return ColorChooserDialog.this.mColorsTop.length;
        }

        public Object getItem(int position) {
            if (ColorChooserDialog.this.isInSub()) {
                return Integer.valueOf(ColorChooserDialog.this.mColorsSub[ColorChooserDialog.this.topIndex()][position]);
            }
            return Integer.valueOf(ColorChooserDialog.this.mColorsTop[position]);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        @SuppressLint({"DefaultLocale"})
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new CircleView(ColorChooserDialog.this.getContext());
                convertView.setLayoutParams(new LayoutParams(ColorChooserDialog.this.mCircleSize, ColorChooserDialog.this.mCircleSize));
            }
            CircleView child = (CircleView) convertView;
            child.setBackgroundColor(ColorChooserDialog.this.isInSub() ? ColorChooserDialog.this.mColorsSub[ColorChooserDialog.this.topIndex()][position] : ColorChooserDialog.this.mColorsTop[position]);
            if (ColorChooserDialog.this.isInSub()) {
                child.setSelected(ColorChooserDialog.this.subIndex() == position);
            } else {
                child.setSelected(ColorChooserDialog.this.topIndex() == position);
            }
            child.setTag(String.format("%d:%d", new Object[]{Integer.valueOf(position), Integer.valueOf(color)}));
            child.setOnClickListener(ColorChooserDialog.this);
            child.setOnLongClickListener(ColorChooserDialog.this);
            return convertView;
        }
    }

    /* renamed from: com.afollestad.materialdialogs.color.ColorChooserDialog$2 */
    class C09252 implements SingleButtonCallback {
        C09252() {
        }

        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            ColorChooserDialog.this.toggleCustom(dialog);
        }
    }

    /* renamed from: com.afollestad.materialdialogs.color.ColorChooserDialog$3 */
    class C09263 implements SingleButtonCallback {
        C09263() {
        }

        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            if (ColorChooserDialog.this.isInSub()) {
                dialog.setActionButton(DialogAction.NEGATIVE, ColorChooserDialog.this.getBuilder().mCancelBtn);
                ColorChooserDialog.this.isInSub(false);
                ColorChooserDialog.this.subIndex(-1);
                ColorChooserDialog.this.invalidate();
                return;
            }
            dialog.cancel();
        }
    }

    /* renamed from: com.afollestad.materialdialogs.color.ColorChooserDialog$4 */
    class C09274 implements SingleButtonCallback {
        C09274() {
        }

        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            ColorCallback access$800 = ColorChooserDialog.this.mCallback;
            ColorChooserDialog colorChooserDialog = ColorChooserDialog.this;
            access$800.onColorSelection(colorChooserDialog, colorChooserDialog.getSelectedColor());
            ColorChooserDialog.this.dismiss();
        }
    }

    @android.support.annotation.NonNull
    public android.app.Dialog onCreateDialog(android.os.Bundle r12) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:62:0x01d8 in {6, 18, 21, 22, 30, 31, 33, 34, 35, 36, 37, 38, 39, 42, 43, 46, 47, 52, 53, 55, 56, 57, 59, 61} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r11 = this;
        r0 = r11.getArguments();
        if (r0 == 0) goto L_0x01cf;
    L_0x0006:
        r0 = r11.getArguments();
        r1 = "builder";
        r0 = r0.containsKey(r1);
        if (r0 == 0) goto L_0x01cf;
    L_0x0012:
        r11.generateColors();
        r0 = 0;
        r1 = 1;
        r2 = 0;
        if (r12 == 0) goto L_0x0028;
    L_0x001a:
        r3 = "in_custom";
        r3 = r12.getBoolean(r3, r2);
        r3 = r3 ^ r1;
        r0 = r3;
        r3 = r11.getSelectedColor();
        goto L_0x008a;
    L_0x0028:
        r3 = r11.getBuilder();
        r3 = r3.mSetPreselectionColor;
        if (r3 == 0) goto L_0x0087;
    L_0x0030:
        r3 = r11.getBuilder();
        r3 = r3.mPreselect;
        if (r3 == 0) goto L_0x0086;
    L_0x0038:
        r4 = 0;
    L_0x0039:
        r5 = r11.mColorsTop;
        r6 = r5.length;
        if (r4 >= r6) goto L_0x0085;
    L_0x003e:
        r5 = r5[r4];
        if (r5 != r3) goto L_0x0060;
    L_0x0042:
        r0 = 1;
        r11.topIndex(r4);
        r5 = r11.getBuilder();
        r5 = r5.mAccentMode;
        if (r5 == 0) goto L_0x0053;
    L_0x004e:
        r5 = 2;
        r11.subIndex(r5);
        goto L_0x0085;
    L_0x0053:
        r5 = r11.mColorsSub;
        if (r5 == 0) goto L_0x005b;
    L_0x0057:
        r11.findSubIndexForColor(r4, r3);
        goto L_0x0085;
    L_0x005b:
        r5 = 5;
        r11.subIndex(r5);
        goto L_0x0085;
    L_0x0060:
        r5 = r11.mColorsSub;
        if (r5 == 0) goto L_0x0081;
    L_0x0064:
        r5 = 0;
    L_0x0065:
        r6 = r11.mColorsSub;
        r7 = r6[r4];
        r7 = r7.length;
        if (r5 >= r7) goto L_0x007d;
    L_0x006c:
        r6 = r6[r4];
        r6 = r6[r5];
        if (r6 != r3) goto L_0x007a;
    L_0x0072:
        r0 = 1;
        r11.topIndex(r4);
        r11.subIndex(r5);
        goto L_0x007d;
    L_0x007a:
        r5 = r5 + 1;
        goto L_0x0065;
    L_0x007d:
        if (r0 == 0) goto L_0x0080;
    L_0x007f:
        goto L_0x0085;
    L_0x0080:
        goto L_0x0082;
    L_0x0082:
        r4 = r4 + 1;
        goto L_0x0039;
    L_0x0085:
        goto L_0x008a;
    L_0x0086:
        goto L_0x008a;
    L_0x0087:
        r3 = -16777216; // 0xffffffffff000000 float:-1.7014118E38 double:NaN;
        r0 = 1;
    L_0x008a:
        r4 = r11.getResources();
        r5 = com.afollestad.materialdialogs.commons.C0502R.dimen.md_colorchooser_circlesize;
        r4 = r4.getDimensionPixelSize(r5);
        r11.mCircleSize = r4;
        r4 = r11.getBuilder();
        r5 = new com.afollestad.materialdialogs.MaterialDialog$Builder;
        r6 = r11.getActivity();
        r5.<init>(r6);
        r6 = r11.getTitle();
        r5 = r5.title(r6);
        r5 = r5.autoDismiss(r2);
        r6 = com.afollestad.materialdialogs.commons.C0502R.layout.md_dialog_colorchooser;
        r5 = r5.customView(r6, r2);
        r6 = r4.mCancelBtn;
        r5 = r5.negativeText(r6);
        r6 = r4.mDoneBtn;
        r5 = r5.positiveText(r6);
        r6 = r4.mAllowUserCustom;
        if (r6 == 0) goto L_0x00c8;
    L_0x00c5:
        r6 = r4.mCustomBtn;
        goto L_0x00c9;
    L_0x00c8:
        r6 = 0;
    L_0x00c9:
        r5 = r5.neutralText(r6);
        r6 = new com.afollestad.materialdialogs.color.ColorChooserDialog$4;
        r6.<init>();
        r5 = r5.onPositive(r6);
        r6 = new com.afollestad.materialdialogs.color.ColorChooserDialog$3;
        r6.<init>();
        r5 = r5.onNegative(r6);
        r6 = new com.afollestad.materialdialogs.color.ColorChooserDialog$2;
        r6.<init>();
        r5 = r5.onNeutral(r6);
        r6 = new com.afollestad.materialdialogs.color.ColorChooserDialog$1;
        r6.<init>();
        r5 = r5.showListener(r6);
        r6 = r4.mTheme;
        if (r6 == 0) goto L_0x00fb;
    L_0x00f5:
        r6 = r4.mTheme;
        r5.theme(r6);
        goto L_0x00fc;
    L_0x00fc:
        r6 = r5.build();
        r7 = r6.getCustomView();
        r8 = com.afollestad.materialdialogs.commons.C0502R.id.md_grid;
        r8 = r7.findViewById(r8);
        r8 = (android.widget.GridView) r8;
        r11.mGrid = r8;
        r8 = r4.mAllowUserCustom;
        if (r8 == 0) goto L_0x01ca;
    L_0x0112:
        r11.mSelectedCustomColor = r3;
        r8 = com.afollestad.materialdialogs.commons.C0502R.id.md_colorChooserCustomFrame;
        r8 = r7.findViewById(r8);
        r11.mColorChooserCustomFrame = r8;
        r8 = com.afollestad.materialdialogs.commons.C0502R.id.md_hexInput;
        r8 = r7.findViewById(r8);
        r8 = (android.widget.EditText) r8;
        r11.mCustomColorHex = r8;
        r8 = com.afollestad.materialdialogs.commons.C0502R.id.md_colorIndicator;
        r8 = r7.findViewById(r8);
        r11.mCustomColorIndicator = r8;
        r8 = com.afollestad.materialdialogs.commons.C0502R.id.md_colorA;
        r8 = r7.findViewById(r8);
        r8 = (android.widget.SeekBar) r8;
        r11.mCustomSeekA = r8;
        r8 = com.afollestad.materialdialogs.commons.C0502R.id.md_colorAValue;
        r8 = r7.findViewById(r8);
        r8 = (android.widget.TextView) r8;
        r11.mCustomSeekAValue = r8;
        r8 = com.afollestad.materialdialogs.commons.C0502R.id.md_colorR;
        r8 = r7.findViewById(r8);
        r8 = (android.widget.SeekBar) r8;
        r11.mCustomSeekR = r8;
        r8 = com.afollestad.materialdialogs.commons.C0502R.id.md_colorRValue;
        r8 = r7.findViewById(r8);
        r8 = (android.widget.TextView) r8;
        r11.mCustomSeekRValue = r8;
        r8 = com.afollestad.materialdialogs.commons.C0502R.id.md_colorG;
        r8 = r7.findViewById(r8);
        r8 = (android.widget.SeekBar) r8;
        r11.mCustomSeekG = r8;
        r8 = com.afollestad.materialdialogs.commons.C0502R.id.md_colorGValue;
        r8 = r7.findViewById(r8);
        r8 = (android.widget.TextView) r8;
        r11.mCustomSeekGValue = r8;
        r8 = com.afollestad.materialdialogs.commons.C0502R.id.md_colorB;
        r8 = r7.findViewById(r8);
        r8 = (android.widget.SeekBar) r8;
        r11.mCustomSeekB = r8;
        r8 = com.afollestad.materialdialogs.commons.C0502R.id.md_colorBValue;
        r8 = r7.findViewById(r8);
        r8 = (android.widget.TextView) r8;
        r11.mCustomSeekBValue = r8;
        r8 = r4.mAllowUserCustomAlpha;
        r9 = 8;
        if (r8 != 0) goto L_0x01ae;
    L_0x0184:
        r8 = com.afollestad.materialdialogs.commons.C0502R.id.md_colorALabel;
        r8 = r7.findViewById(r8);
        r8.setVisibility(r9);
        r8 = r11.mCustomSeekA;
        r8.setVisibility(r9);
        r8 = r11.mCustomSeekAValue;
        r8.setVisibility(r9);
        r8 = r11.mCustomColorHex;
        r9 = "2196F3";
        r8.setHint(r9);
        r8 = r11.mCustomColorHex;
        r1 = new android.text.InputFilter[r1];
        r9 = new android.text.InputFilter$LengthFilter;
        r10 = 6;
        r9.<init>(r10);
        r1[r2] = r9;
        r8.setFilters(r1);
        goto L_0x01c3;
    L_0x01ae:
        r8 = r11.mCustomColorHex;
        r10 = "FF2196F3";
        r8.setHint(r10);
        r8 = r11.mCustomColorHex;
        r1 = new android.text.InputFilter[r1];
        r10 = new android.text.InputFilter$LengthFilter;
        r10.<init>(r9);
        r1[r2] = r10;
        r8.setFilters(r1);
    L_0x01c3:
        if (r0 != 0) goto L_0x01c9;
    L_0x01c5:
        r11.toggleCustom(r6);
        goto L_0x01cb;
    L_0x01c9:
        goto L_0x01cb;
    L_0x01cb:
        r11.invalidate();
        return r6;
        r0 = new java.lang.IllegalStateException;
        r1 = "ColorChooserDialog should be created using its Builder interface.";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.afollestad.materialdialogs.color.ColorChooserDialog.onCreateDialog(android.os.Bundle):android.app.Dialog");
    }

    private void generateColors() {
        Builder builder = getBuilder();
        if (builder.mColorsTop != null) {
            this.mColorsTop = builder.mColorsTop;
            this.mColorsSub = builder.mColorsSub;
            return;
        }
        if (builder.mAccentMode) {
            this.mColorsTop = ColorPalette.ACCENT_COLORS;
            this.mColorsSub = ColorPalette.ACCENT_COLORS_SUB;
        } else {
            this.mColorsTop = ColorPalette.PRIMARY_COLORS;
            this.mColorsSub = ColorPalette.PRIMARY_COLORS_SUB;
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        boolean z;
        super.onSaveInstanceState(outState);
        outState.putInt("top_index", topIndex());
        outState.putBoolean("in_sub", isInSub());
        outState.putInt("sub_index", subIndex());
        String str = "in_custom";
        View view = this.mColorChooserCustomFrame;
        if (view != null) {
            if (view.getVisibility() == 0) {
                z = true;
                outState.putBoolean(str, z);
            }
        }
        z = false;
        outState.putBoolean(str, z);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ColorCallback) {
            this.mCallback = (ColorCallback) activity;
            return;
        }
        throw new IllegalStateException("ColorChooserDialog needs to be shown from an Activity implementing ColorCallback.");
    }

    private boolean isInSub() {
        return getArguments().getBoolean("in_sub", false);
    }

    private void isInSub(boolean value) {
        getArguments().putBoolean("in_sub", value);
    }

    private int topIndex() {
        return getArguments().getInt("top_index", -1);
    }

    private void topIndex(int value) {
        if (value > -1) {
            findSubIndexForColor(value, this.mColorsTop[value]);
        }
        getArguments().putInt("top_index", value);
    }

    private int subIndex() {
        if (this.mColorsSub == null) {
            return -1;
        }
        return getArguments().getInt("sub_index", -1);
    }

    private void subIndex(int value) {
        if (this.mColorsSub != null) {
            getArguments().putInt("sub_index", value);
        }
    }

    @StringRes
    public int getTitle() {
        int title;
        Builder builder = getBuilder();
        if (isInSub()) {
            title = builder.mTitleSub;
        } else {
            title = builder.mTitle;
        }
        if (title == 0) {
            return builder.mTitle;
        }
        return title;
    }

    public String tag() {
        Builder builder = getBuilder();
        if (builder.mTag != null) {
            return builder.mTag;
        }
        return super.getTag();
    }

    public boolean isAccentMode() {
        return getBuilder().mAccentMode;
    }

    public void onClick(View v) {
        if (v.getTag() != null) {
            int index = Integer.parseInt(((String) v.getTag()).split(":")[0]);
            MaterialDialog dialog = (MaterialDialog) getDialog();
            Builder builder = getBuilder();
            if (isInSub()) {
                subIndex(index);
            } else {
                topIndex(index);
                int[][] iArr = this.mColorsSub;
                if (iArr != null && index < iArr.length) {
                    dialog.setActionButton(DialogAction.NEGATIVE, builder.mBackBtn);
                    isInSub(true);
                }
            }
            if (builder.mAllowUserCustom) {
                this.mSelectedCustomColor = getSelectedColor();
            }
            invalidateDynamicButtonColors();
            invalidate();
        }
    }

    public boolean onLongClick(View v) {
        if (v.getTag() == null) {
            return false;
        }
        ((CircleView) v).showHint(Integer.parseInt(((String) v.getTag()).split(":")[1]));
        return true;
    }

    private void invalidateDynamicButtonColors() {
        MaterialDialog dialog = (MaterialDialog) getDialog();
        if (dialog != null) {
            if (getBuilder().mDynamicButtonColor) {
                int selectedColor = getSelectedColor();
                if (Color.alpha(selectedColor) >= 64) {
                    if (Color.red(selectedColor) > 247) {
                        if (Color.green(selectedColor) > 247) {
                            if (Color.blue(selectedColor) > 247) {
                            }
                        }
                    }
                    if (getBuilder().mDynamicButtonColor) {
                        dialog.getActionButton(DialogAction.POSITIVE).setTextColor(selectedColor);
                        dialog.getActionButton(DialogAction.NEGATIVE).setTextColor(selectedColor);
                        dialog.getActionButton(DialogAction.NEUTRAL).setTextColor(selectedColor);
                    }
                    if (this.mCustomSeekR != null) {
                        if (this.mCustomSeekA.getVisibility() == 0) {
                            MDTintHelper.setTint(this.mCustomSeekA, selectedColor);
                        }
                        MDTintHelper.setTint(this.mCustomSeekR, selectedColor);
                        MDTintHelper.setTint(this.mCustomSeekG, selectedColor);
                        MDTintHelper.setTint(this.mCustomSeekB, selectedColor);
                    }
                }
                selectedColor = Color.parseColor("#DEDEDE");
                if (getBuilder().mDynamicButtonColor) {
                    dialog.getActionButton(DialogAction.POSITIVE).setTextColor(selectedColor);
                    dialog.getActionButton(DialogAction.NEGATIVE).setTextColor(selectedColor);
                    dialog.getActionButton(DialogAction.NEUTRAL).setTextColor(selectedColor);
                }
                if (this.mCustomSeekR != null) {
                    if (this.mCustomSeekA.getVisibility() == 0) {
                        MDTintHelper.setTint(this.mCustomSeekA, selectedColor);
                    }
                    MDTintHelper.setTint(this.mCustomSeekR, selectedColor);
                    MDTintHelper.setTint(this.mCustomSeekG, selectedColor);
                    MDTintHelper.setTint(this.mCustomSeekB, selectedColor);
                }
            }
        }
    }

    @ColorInt
    private int getSelectedColor() {
        View view = this.mColorChooserCustomFrame;
        if (view != null && view.getVisibility() == 0) {
            return this.mSelectedCustomColor;
        }
        int color = 0;
        if (subIndex() > -1) {
            color = this.mColorsSub[topIndex()][subIndex()];
        } else if (topIndex() > -1) {
            color = this.mColorsTop[topIndex()];
        }
        if (color == 0) {
            int fallback = 0;
            if (VERSION.SDK_INT >= 21) {
                fallback = DialogUtils.resolveColor(getActivity(), 16843829);
            }
            color = DialogUtils.resolveColor(getActivity(), C0502R.attr.colorAccent, fallback);
        }
        return color;
    }

    private void findSubIndexForColor(int topIndex, int color) {
        int[] subColors = this.mColorsSub;
        if (subColors != null) {
            if (subColors.length - 1 >= topIndex) {
                subColors = subColors[topIndex];
                for (int subIndex = 0; subIndex < subColors.length; subIndex++) {
                    if (subColors[subIndex] == color) {
                        subIndex(subIndex);
                        break;
                    }
                }
            }
        }
    }

    private void toggleCustom(MaterialDialog dialog) {
        if (dialog == null) {
            dialog = (MaterialDialog) getDialog();
        }
        if (this.mGrid.getVisibility() == 0) {
            dialog.setTitle(getBuilder().mCustomBtn);
            dialog.setActionButton(DialogAction.NEUTRAL, getBuilder().mPresetsBtn);
            dialog.setActionButton(DialogAction.NEGATIVE, getBuilder().mCancelBtn);
            this.mGrid.setVisibility(4);
            this.mColorChooserCustomFrame.setVisibility(0);
            this.mCustomColorTextWatcher = new C05005();
            this.mCustomColorHex.addTextChangedListener(this.mCustomColorTextWatcher);
            this.mCustomColorRgbListener = new C05016();
            this.mCustomSeekR.setOnSeekBarChangeListener(this.mCustomColorRgbListener);
            this.mCustomSeekG.setOnSeekBarChangeListener(this.mCustomColorRgbListener);
            this.mCustomSeekB.setOnSeekBarChangeListener(this.mCustomColorRgbListener);
            if (this.mCustomSeekA.getVisibility() == 0) {
                this.mCustomSeekA.setOnSeekBarChangeListener(this.mCustomColorRgbListener);
                this.mCustomColorHex.setText(String.format("%08X", new Object[]{Integer.valueOf(this.mSelectedCustomColor)}));
                return;
            }
            this.mCustomColorHex.setText(String.format("%06X", new Object[]{Integer.valueOf(ViewCompat.MEASURED_SIZE_MASK & this.mSelectedCustomColor)}));
            return;
        }
        dialog.setTitle(getBuilder().mTitle);
        dialog.setActionButton(DialogAction.NEUTRAL, getBuilder().mCustomBtn);
        if (isInSub()) {
            dialog.setActionButton(DialogAction.NEGATIVE, getBuilder().mBackBtn);
        } else {
            dialog.setActionButton(DialogAction.NEGATIVE, getBuilder().mCancelBtn);
        }
        this.mGrid.setVisibility(0);
        this.mColorChooserCustomFrame.setVisibility(8);
        this.mCustomColorHex.removeTextChangedListener(this.mCustomColorTextWatcher);
        this.mCustomColorTextWatcher = null;
        this.mCustomSeekR.setOnSeekBarChangeListener(null);
        this.mCustomSeekG.setOnSeekBarChangeListener(null);
        this.mCustomSeekB.setOnSeekBarChangeListener(null);
        this.mCustomColorRgbListener = null;
    }

    private void invalidate() {
        if (this.mGrid.getAdapter() == null) {
            this.mGrid.setAdapter(new ColorGridAdapter());
            this.mGrid.setSelector(ResourcesCompat.getDrawable(getResources(), C0502R.drawable.md_transparent, null));
        } else {
            ((BaseAdapter) this.mGrid.getAdapter()).notifyDataSetChanged();
        }
        if (getDialog() != null) {
            getDialog().setTitle(getTitle());
        }
    }

    private Builder getBuilder() {
        if (getArguments() != null) {
            if (getArguments().containsKey("builder")) {
                return (Builder) getArguments().getSerializable("builder");
            }
        }
        return null;
    }

    private void dismissIfNecessary(AppCompatActivity context, String tag) {
        Fragment frag = context.getSupportFragmentManager().findFragmentByTag(tag);
        if (frag != null) {
            ((DialogFragment) frag).dismiss();
            context.getSupportFragmentManager().beginTransaction().remove(frag).commit();
        }
    }

    @Nullable
    public static ColorChooserDialog findVisible(@NonNull AppCompatActivity context, String tag) {
        Fragment frag = context.getSupportFragmentManager().findFragmentByTag(tag);
        if (frag == null || !(frag instanceof ColorChooserDialog)) {
            return null;
        }
        return (ColorChooserDialog) frag;
    }

    @NonNull
    public ColorChooserDialog show(AppCompatActivity context) {
        String tag;
        Builder builder = getBuilder();
        if (builder.mColorsTop != null) {
            tag = "[MD_COLOR_CHOOSER]";
        } else if (builder.mAccentMode) {
            tag = "[MD_COLOR_CHOOSER]";
        } else {
            tag = "[MD_COLOR_CHOOSER]";
        }
        dismissIfNecessary(context, tag);
        show(context.getSupportFragmentManager(), tag);
        return this;
    }
}
