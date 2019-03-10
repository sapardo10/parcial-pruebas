package com.afollestad.materialdialogs;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build.VERSION;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.afollestad.materialdialogs.internal.MDAdapter;
import com.afollestad.materialdialogs.internal.MDButton;
import com.afollestad.materialdialogs.internal.MDRootLayout;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.util.DialogUtils;
import java.util.ArrayList;
import java.util.Arrays;
import me.zhanghai.android.materialprogressbar.HorizontalProgressDrawable;
import me.zhanghai.android.materialprogressbar.IndeterminateHorizontalProgressDrawable;
import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;

class DialogInit {
    DialogInit() {
    }

    @StyleRes
    public static int getTheme(@NonNull Builder builder) {
        boolean darkTheme = DialogUtils.resolveBoolean(builder.context, C0498R.attr.md_dark_theme, builder.theme == Theme.DARK);
        builder.theme = darkTheme ? Theme.DARK : Theme.LIGHT;
        return darkTheme ? C0498R.style.MD_Dark : C0498R.style.MD_Light;
    }

    @LayoutRes
    public static int getInflateLayout(Builder builder) {
        if (builder.customView != null) {
            return C0498R.layout.md_dialog_custom;
        }
        if ((builder.items == null || builder.items.size() <= 0) && builder.adapter == null) {
            if (builder.progress > -2) {
                return C0498R.layout.md_dialog_progress;
            }
            if (builder.indeterminateProgress) {
                if (builder.indeterminateIsHorizontalProgress) {
                    return C0498R.layout.md_dialog_progress_indeterminate_horizontal;
                }
                return C0498R.layout.md_dialog_progress_indeterminate;
            } else if (builder.inputCallback != null) {
                if (builder.checkBoxPrompt != null) {
                    return C0498R.layout.md_dialog_input_check;
                }
                return C0498R.layout.md_dialog_input;
            } else if (builder.checkBoxPrompt != null) {
                return C0498R.layout.md_dialog_basic_check;
            } else {
                return C0498R.layout.md_dialog_basic;
            }
        } else if (builder.checkBoxPrompt != null) {
            return C0498R.layout.md_dialog_list_check;
        } else {
            return C0498R.layout.md_dialog_list;
        }
    }

    @UiThread
    public static void init(MaterialDialog dialog) {
        boolean textAllCaps;
        MDButton positiveTextView;
        MDButton negativeTextView;
        MDButton neutralTextView;
        View innerView;
        int i;
        int framePadding;
        View sv;
        int paddingTop;
        int paddingBottom;
        MaterialDialog materialDialog = dialog;
        Builder builder = materialDialog.mBuilder;
        materialDialog.setCancelable(builder.cancelable);
        materialDialog.setCanceledOnTouchOutside(builder.canceledOnTouchOutside);
        if (builder.backgroundColor == 0) {
            builder.backgroundColor = DialogUtils.resolveColor(builder.context, C0498R.attr.md_background_color, DialogUtils.resolveColor(dialog.getContext(), C0498R.attr.colorBackgroundFloating));
        }
        if (builder.backgroundColor != 0) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(builder.context.getResources().getDimension(C0498R.dimen.md_bg_corner_radius));
            drawable.setColor(builder.backgroundColor);
            DialogUtils.setBackgroundCompat(materialDialog.view, drawable);
        }
        if (!builder.positiveColorSet) {
            builder.positiveColor = DialogUtils.resolveActionTextColorStateList(builder.context, C0498R.attr.md_positive_color, builder.positiveColor);
        }
        if (!builder.neutralColorSet) {
            builder.neutralColor = DialogUtils.resolveActionTextColorStateList(builder.context, C0498R.attr.md_neutral_color, builder.neutralColor);
        }
        if (!builder.negativeColorSet) {
            builder.negativeColor = DialogUtils.resolveActionTextColorStateList(builder.context, C0498R.attr.md_negative_color, builder.negativeColor);
        }
        if (!builder.widgetColorSet) {
            builder.widgetColor = DialogUtils.resolveColor(builder.context, C0498R.attr.md_widget_color, builder.widgetColor);
        }
        if (!builder.titleColorSet) {
            builder.titleColor = DialogUtils.resolveColor(builder.context, C0498R.attr.md_title_color, DialogUtils.resolveColor(dialog.getContext(), 16842806));
        }
        if (!builder.contentColorSet) {
            builder.contentColor = DialogUtils.resolveColor(builder.context, C0498R.attr.md_content_color, DialogUtils.resolveColor(dialog.getContext(), 16842808));
        }
        if (!builder.itemColorSet) {
            builder.itemColor = DialogUtils.resolveColor(builder.context, C0498R.attr.md_item_color, builder.contentColor);
        }
        materialDialog.title = (TextView) materialDialog.view.findViewById(C0498R.id.md_title);
        materialDialog.icon = (ImageView) materialDialog.view.findViewById(C0498R.id.md_icon);
        materialDialog.titleFrame = materialDialog.view.findViewById(C0498R.id.md_titleFrame);
        materialDialog.content = (TextView) materialDialog.view.findViewById(C0498R.id.md_content);
        materialDialog.recyclerView = (RecyclerView) materialDialog.view.findViewById(C0498R.id.md_contentRecyclerView);
        materialDialog.checkBoxPrompt = (CheckBox) materialDialog.view.findViewById(C0498R.id.md_promptCheckbox);
        materialDialog.positiveButton = (MDButton) materialDialog.view.findViewById(C0498R.id.md_buttonDefaultPositive);
        materialDialog.neutralButton = (MDButton) materialDialog.view.findViewById(C0498R.id.md_buttonDefaultNeutral);
        materialDialog.negativeButton = (MDButton) materialDialog.view.findViewById(C0498R.id.md_buttonDefaultNegative);
        if (builder.inputCallback != null && builder.positiveText == null) {
            builder.positiveText = builder.context.getText(17039370);
        }
        materialDialog.positiveButton.setVisibility(builder.positiveText != null ? 0 : 8);
        materialDialog.neutralButton.setVisibility(builder.neutralText != null ? 0 : 8);
        materialDialog.negativeButton.setVisibility(builder.negativeText != null ? 0 : 8);
        if (builder.icon != null) {
            materialDialog.icon.setVisibility(0);
            materialDialog.icon.setImageDrawable(builder.icon);
        } else {
            Drawable d = DialogUtils.resolveDrawable(builder.context, C0498R.attr.md_icon);
            if (d != null) {
                materialDialog.icon.setVisibility(0);
                materialDialog.icon.setImageDrawable(d);
            } else {
                materialDialog.icon.setVisibility(8);
            }
        }
        int maxIconSize = builder.maxIconSize;
        if (maxIconSize == -1) {
            maxIconSize = DialogUtils.resolveDimension(builder.context, C0498R.attr.md_icon_max_size);
        }
        if (!builder.limitIconToDefaultSize) {
            if (!DialogUtils.resolveBoolean(builder.context, C0498R.attr.md_icon_limit_icon_to_default_size)) {
                if (maxIconSize > -1) {
                    materialDialog.icon.setAdjustViewBounds(true);
                    materialDialog.icon.setMaxHeight(maxIconSize);
                    materialDialog.icon.setMaxWidth(maxIconSize);
                    materialDialog.icon.requestLayout();
                }
                if (!builder.dividerColorSet) {
                    builder.dividerColor = DialogUtils.resolveColor(builder.context, C0498R.attr.md_divider_color, DialogUtils.resolveColor(dialog.getContext(), C0498R.attr.md_divider));
                }
                materialDialog.view.setDividerColor(builder.dividerColor);
                if (materialDialog.title != null) {
                    materialDialog.setTypeface(materialDialog.title, builder.mediumFont);
                    materialDialog.title.setTextColor(builder.titleColor);
                    materialDialog.title.setGravity(builder.titleGravity.getGravityInt());
                    if (VERSION.SDK_INT >= 17) {
                        materialDialog.title.setTextAlignment(builder.titleGravity.getTextAlignment());
                    }
                    if (builder.title != null) {
                        materialDialog.titleFrame.setVisibility(8);
                    } else {
                        materialDialog.title.setText(builder.title);
                        materialDialog.titleFrame.setVisibility(0);
                    }
                }
                if (materialDialog.content != null) {
                    materialDialog.content.setMovementMethod(new LinkMovementMethod());
                    materialDialog.setTypeface(materialDialog.content, builder.regularFont);
                    materialDialog.content.setLineSpacing(0.0f, builder.contentLineSpacingMultiplier);
                    if (builder.linkColor != null) {
                        materialDialog.content.setLinkTextColor(DialogUtils.resolveColor(dialog.getContext(), 16842806));
                    } else {
                        materialDialog.content.setLinkTextColor(builder.linkColor);
                    }
                    materialDialog.content.setTextColor(builder.contentColor);
                    materialDialog.content.setGravity(builder.contentGravity.getGravityInt());
                    if (VERSION.SDK_INT >= 17) {
                        materialDialog.content.setTextAlignment(builder.contentGravity.getTextAlignment());
                    }
                    if (builder.content == null) {
                        materialDialog.content.setText(builder.content);
                        materialDialog.content.setVisibility(0);
                    } else {
                        materialDialog.content.setVisibility(8);
                    }
                }
                if (materialDialog.checkBoxPrompt != null) {
                    materialDialog.checkBoxPrompt.setText(builder.checkBoxPrompt);
                    materialDialog.checkBoxPrompt.setChecked(builder.checkBoxPromptInitiallyChecked);
                    materialDialog.checkBoxPrompt.setOnCheckedChangeListener(builder.checkBoxPromptListener);
                    materialDialog.setTypeface(materialDialog.checkBoxPrompt, builder.regularFont);
                    materialDialog.checkBoxPrompt.setTextColor(builder.contentColor);
                    MDTintHelper.setTint(materialDialog.checkBoxPrompt, builder.widgetColor);
                }
                materialDialog.view.setButtonGravity(builder.buttonsGravity);
                materialDialog.view.setButtonStackedGravity(builder.btnStackedGravity);
                materialDialog.view.setStackingBehavior(builder.stackingBehavior);
                if (VERSION.SDK_INT < 14) {
                    textAllCaps = DialogUtils.resolveBoolean(builder.context, 16843660, true);
                    if (textAllCaps) {
                        textAllCaps = DialogUtils.resolveBoolean(builder.context, C0498R.attr.textAllCaps, true);
                    }
                } else {
                    textAllCaps = DialogUtils.resolveBoolean(builder.context, C0498R.attr.textAllCaps, true);
                }
                positiveTextView = materialDialog.positiveButton;
                materialDialog.setTypeface(positiveTextView, builder.mediumFont);
                positiveTextView.setAllCapsCompat(textAllCaps);
                positiveTextView.setText(builder.positiveText);
                positiveTextView.setTextColor(builder.positiveColor);
                materialDialog.positiveButton.setStackedSelector(materialDialog.getButtonSelector(DialogAction.POSITIVE, true));
                materialDialog.positiveButton.setDefaultSelector(materialDialog.getButtonSelector(DialogAction.POSITIVE, false));
                materialDialog.positiveButton.setTag(DialogAction.POSITIVE);
                materialDialog.positiveButton.setOnClickListener(materialDialog);
                materialDialog.positiveButton.setVisibility(0);
                negativeTextView = materialDialog.negativeButton;
                materialDialog.setTypeface(negativeTextView, builder.mediumFont);
                negativeTextView.setAllCapsCompat(textAllCaps);
                negativeTextView.setText(builder.negativeText);
                negativeTextView.setTextColor(builder.negativeColor);
                materialDialog.negativeButton.setStackedSelector(materialDialog.getButtonSelector(DialogAction.NEGATIVE, true));
                materialDialog.negativeButton.setDefaultSelector(materialDialog.getButtonSelector(DialogAction.NEGATIVE, false));
                materialDialog.negativeButton.setTag(DialogAction.NEGATIVE);
                materialDialog.negativeButton.setOnClickListener(materialDialog);
                materialDialog.negativeButton.setVisibility(0);
                neutralTextView = materialDialog.neutralButton;
                materialDialog.setTypeface(neutralTextView, builder.mediumFont);
                neutralTextView.setAllCapsCompat(textAllCaps);
                neutralTextView.setText(builder.neutralText);
                neutralTextView.setTextColor(builder.neutralColor);
                materialDialog.neutralButton.setStackedSelector(materialDialog.getButtonSelector(DialogAction.NEUTRAL, true));
                materialDialog.neutralButton.setDefaultSelector(materialDialog.getButtonSelector(DialogAction.NEUTRAL, false));
                materialDialog.neutralButton.setTag(DialogAction.NEUTRAL);
                materialDialog.neutralButton.setOnClickListener(materialDialog);
                materialDialog.neutralButton.setVisibility(0);
                if (builder.listCallbackMultiChoice != null) {
                    materialDialog.selectedIndicesList = new ArrayList();
                }
                if (materialDialog.recyclerView != null) {
                    if (builder.adapter == null) {
                        if (builder.listCallbackSingleChoice != null) {
                            materialDialog.listType = ListType.SINGLE;
                        } else if (builder.listCallbackMultiChoice == null) {
                            materialDialog.listType = ListType.MULTI;
                            if (builder.selectedIndices != null) {
                                materialDialog.selectedIndicesList = new ArrayList(Arrays.asList(builder.selectedIndices));
                                builder.selectedIndices = null;
                            }
                        } else {
                            materialDialog.listType = ListType.REGULAR;
                        }
                        builder.adapter = new DefaultRvAdapter(materialDialog, ListType.getLayoutForType(materialDialog.listType));
                    } else if (builder.adapter instanceof MDAdapter) {
                        ((MDAdapter) builder.adapter).setDialog(materialDialog);
                    }
                }
                setupProgressDialog(dialog);
                setupInputDialog(dialog);
                if (builder.customView == null) {
                    ((MDRootLayout) materialDialog.view.findViewById(C0498R.id.md_root)).noTitleNoPadding();
                    FrameLayout frame = (FrameLayout) materialDialog.view.findViewById(C0498R.id.md_customViewFrame);
                    materialDialog.customViewFrame = frame;
                    innerView = builder.customView;
                    if (innerView.getParent() != null) {
                        ((ViewGroup) innerView.getParent()).removeView(innerView);
                    }
                    if (builder.wrapCustomViewInScroll) {
                        maxIconSize = -2;
                        i = -1;
                    } else {
                        Resources r = dialog.getContext().getResources();
                        framePadding = r.getDimensionPixelSize(C0498R.dimen.md_dialog_frame_margin);
                        sv = new ScrollView(dialog.getContext());
                        paddingTop = r.getDimensionPixelSize(C0498R.dimen.md_content_padding_top);
                        paddingBottom = r.getDimensionPixelSize(C0498R.dimen.md_content_padding_bottom);
                        sv.setClipToPadding(false);
                        if (innerView instanceof EditText) {
                            sv.setPadding(0, paddingTop, 0, paddingBottom);
                            innerView.setPadding(framePadding, 0, framePadding, 0);
                        } else {
                            sv.setPadding(framePadding, paddingTop, framePadding, paddingBottom);
                        }
                        maxIconSize = -2;
                        i = -1;
                        sv.addView(innerView, new LayoutParams(-1, -2));
                        innerView = sv;
                    }
                    frame.addView(innerView, new ViewGroup.LayoutParams(i, maxIconSize));
                }
                if (builder.showListener != null) {
                    materialDialog.setOnShowListener(builder.showListener);
                }
                if (builder.cancelListener != null) {
                    materialDialog.setOnCancelListener(builder.cancelListener);
                }
                if (builder.dismissListener != null) {
                    materialDialog.setOnDismissListener(builder.dismissListener);
                }
                if (builder.keyListener != null) {
                    materialDialog.setOnKeyListener(builder.keyListener);
                }
                dialog.setOnShowListenerInternal();
                dialog.invalidateList();
                materialDialog.setViewInternal(materialDialog.view);
                dialog.checkIfListInitScroll();
            }
        }
        maxIconSize = builder.context.getResources().getDimensionPixelSize(C0498R.dimen.md_icon_max_size);
        if (maxIconSize > -1) {
            materialDialog.icon.setAdjustViewBounds(true);
            materialDialog.icon.setMaxHeight(maxIconSize);
            materialDialog.icon.setMaxWidth(maxIconSize);
            materialDialog.icon.requestLayout();
        }
        if (!builder.dividerColorSet) {
            builder.dividerColor = DialogUtils.resolveColor(builder.context, C0498R.attr.md_divider_color, DialogUtils.resolveColor(dialog.getContext(), C0498R.attr.md_divider));
        }
        materialDialog.view.setDividerColor(builder.dividerColor);
        if (materialDialog.title != null) {
            materialDialog.setTypeface(materialDialog.title, builder.mediumFont);
            materialDialog.title.setTextColor(builder.titleColor);
            materialDialog.title.setGravity(builder.titleGravity.getGravityInt());
            if (VERSION.SDK_INT >= 17) {
                materialDialog.title.setTextAlignment(builder.titleGravity.getTextAlignment());
            }
            if (builder.title != null) {
                materialDialog.title.setText(builder.title);
                materialDialog.titleFrame.setVisibility(0);
            } else {
                materialDialog.titleFrame.setVisibility(8);
            }
        }
        if (materialDialog.content != null) {
            materialDialog.content.setMovementMethod(new LinkMovementMethod());
            materialDialog.setTypeface(materialDialog.content, builder.regularFont);
            materialDialog.content.setLineSpacing(0.0f, builder.contentLineSpacingMultiplier);
            if (builder.linkColor != null) {
                materialDialog.content.setLinkTextColor(builder.linkColor);
            } else {
                materialDialog.content.setLinkTextColor(DialogUtils.resolveColor(dialog.getContext(), 16842806));
            }
            materialDialog.content.setTextColor(builder.contentColor);
            materialDialog.content.setGravity(builder.contentGravity.getGravityInt());
            if (VERSION.SDK_INT >= 17) {
                materialDialog.content.setTextAlignment(builder.contentGravity.getTextAlignment());
            }
            if (builder.content == null) {
                materialDialog.content.setVisibility(8);
            } else {
                materialDialog.content.setText(builder.content);
                materialDialog.content.setVisibility(0);
            }
        }
        if (materialDialog.checkBoxPrompt != null) {
            materialDialog.checkBoxPrompt.setText(builder.checkBoxPrompt);
            materialDialog.checkBoxPrompt.setChecked(builder.checkBoxPromptInitiallyChecked);
            materialDialog.checkBoxPrompt.setOnCheckedChangeListener(builder.checkBoxPromptListener);
            materialDialog.setTypeface(materialDialog.checkBoxPrompt, builder.regularFont);
            materialDialog.checkBoxPrompt.setTextColor(builder.contentColor);
            MDTintHelper.setTint(materialDialog.checkBoxPrompt, builder.widgetColor);
        }
        materialDialog.view.setButtonGravity(builder.buttonsGravity);
        materialDialog.view.setButtonStackedGravity(builder.btnStackedGravity);
        materialDialog.view.setStackingBehavior(builder.stackingBehavior);
        if (VERSION.SDK_INT < 14) {
            textAllCaps = DialogUtils.resolveBoolean(builder.context, C0498R.attr.textAllCaps, true);
        } else {
            textAllCaps = DialogUtils.resolveBoolean(builder.context, 16843660, true);
            if (textAllCaps) {
                textAllCaps = DialogUtils.resolveBoolean(builder.context, C0498R.attr.textAllCaps, true);
            }
        }
        positiveTextView = materialDialog.positiveButton;
        materialDialog.setTypeface(positiveTextView, builder.mediumFont);
        positiveTextView.setAllCapsCompat(textAllCaps);
        positiveTextView.setText(builder.positiveText);
        positiveTextView.setTextColor(builder.positiveColor);
        materialDialog.positiveButton.setStackedSelector(materialDialog.getButtonSelector(DialogAction.POSITIVE, true));
        materialDialog.positiveButton.setDefaultSelector(materialDialog.getButtonSelector(DialogAction.POSITIVE, false));
        materialDialog.positiveButton.setTag(DialogAction.POSITIVE);
        materialDialog.positiveButton.setOnClickListener(materialDialog);
        materialDialog.positiveButton.setVisibility(0);
        negativeTextView = materialDialog.negativeButton;
        materialDialog.setTypeface(negativeTextView, builder.mediumFont);
        negativeTextView.setAllCapsCompat(textAllCaps);
        negativeTextView.setText(builder.negativeText);
        negativeTextView.setTextColor(builder.negativeColor);
        materialDialog.negativeButton.setStackedSelector(materialDialog.getButtonSelector(DialogAction.NEGATIVE, true));
        materialDialog.negativeButton.setDefaultSelector(materialDialog.getButtonSelector(DialogAction.NEGATIVE, false));
        materialDialog.negativeButton.setTag(DialogAction.NEGATIVE);
        materialDialog.negativeButton.setOnClickListener(materialDialog);
        materialDialog.negativeButton.setVisibility(0);
        neutralTextView = materialDialog.neutralButton;
        materialDialog.setTypeface(neutralTextView, builder.mediumFont);
        neutralTextView.setAllCapsCompat(textAllCaps);
        neutralTextView.setText(builder.neutralText);
        neutralTextView.setTextColor(builder.neutralColor);
        materialDialog.neutralButton.setStackedSelector(materialDialog.getButtonSelector(DialogAction.NEUTRAL, true));
        materialDialog.neutralButton.setDefaultSelector(materialDialog.getButtonSelector(DialogAction.NEUTRAL, false));
        materialDialog.neutralButton.setTag(DialogAction.NEUTRAL);
        materialDialog.neutralButton.setOnClickListener(materialDialog);
        materialDialog.neutralButton.setVisibility(0);
        if (builder.listCallbackMultiChoice != null) {
            materialDialog.selectedIndicesList = new ArrayList();
        }
        if (materialDialog.recyclerView != null) {
            if (builder.adapter == null) {
                if (builder.listCallbackSingleChoice != null) {
                    materialDialog.listType = ListType.SINGLE;
                } else if (builder.listCallbackMultiChoice == null) {
                    materialDialog.listType = ListType.REGULAR;
                } else {
                    materialDialog.listType = ListType.MULTI;
                    if (builder.selectedIndices != null) {
                        materialDialog.selectedIndicesList = new ArrayList(Arrays.asList(builder.selectedIndices));
                        builder.selectedIndices = null;
                    }
                }
                builder.adapter = new DefaultRvAdapter(materialDialog, ListType.getLayoutForType(materialDialog.listType));
            } else if (builder.adapter instanceof MDAdapter) {
                ((MDAdapter) builder.adapter).setDialog(materialDialog);
            }
        }
        setupProgressDialog(dialog);
        setupInputDialog(dialog);
        if (builder.customView == null) {
        } else {
            ((MDRootLayout) materialDialog.view.findViewById(C0498R.id.md_root)).noTitleNoPadding();
            FrameLayout frame2 = (FrameLayout) materialDialog.view.findViewById(C0498R.id.md_customViewFrame);
            materialDialog.customViewFrame = frame2;
            innerView = builder.customView;
            if (innerView.getParent() != null) {
                ((ViewGroup) innerView.getParent()).removeView(innerView);
            }
            if (builder.wrapCustomViewInScroll) {
                maxIconSize = -2;
                i = -1;
            } else {
                Resources r2 = dialog.getContext().getResources();
                framePadding = r2.getDimensionPixelSize(C0498R.dimen.md_dialog_frame_margin);
                sv = new ScrollView(dialog.getContext());
                paddingTop = r2.getDimensionPixelSize(C0498R.dimen.md_content_padding_top);
                paddingBottom = r2.getDimensionPixelSize(C0498R.dimen.md_content_padding_bottom);
                sv.setClipToPadding(false);
                if (innerView instanceof EditText) {
                    sv.setPadding(0, paddingTop, 0, paddingBottom);
                    innerView.setPadding(framePadding, 0, framePadding, 0);
                } else {
                    sv.setPadding(framePadding, paddingTop, framePadding, paddingBottom);
                }
                maxIconSize = -2;
                i = -1;
                sv.addView(innerView, new LayoutParams(-1, -2));
                innerView = sv;
            }
            frame2.addView(innerView, new ViewGroup.LayoutParams(i, maxIconSize));
        }
        if (builder.showListener != null) {
            materialDialog.setOnShowListener(builder.showListener);
        }
        if (builder.cancelListener != null) {
            materialDialog.setOnCancelListener(builder.cancelListener);
        }
        if (builder.dismissListener != null) {
            materialDialog.setOnDismissListener(builder.dismissListener);
        }
        if (builder.keyListener != null) {
            materialDialog.setOnKeyListener(builder.keyListener);
        }
        dialog.setOnShowListenerInternal();
        dialog.invalidateList();
        materialDialog.setViewInternal(materialDialog.view);
        dialog.checkIfListInitScroll();
    }

    private static void fixCanvasScalingWhenHardwareAccelerated(ProgressBar pb) {
        if (VERSION.SDK_INT >= 11 && VERSION.SDK_INT < 18) {
            if (pb.isHardwareAccelerated() && pb.getLayerType() != 1) {
                pb.setLayerType(1, null);
            }
        }
    }

    private static void setupProgressDialog(MaterialDialog dialog) {
        Builder builder = dialog.mBuilder;
        if (!builder.indeterminateProgress) {
            if (builder.progress <= -2) {
                if (dialog.mProgress == null) {
                    fixCanvasScalingWhenHardwareAccelerated(dialog.mProgress);
                }
            }
        }
        dialog.mProgress = (ProgressBar) dialog.view.findViewById(16908301);
        if (dialog.mProgress != null) {
            if (VERSION.SDK_INT < 14) {
                MDTintHelper.setTint(dialog.mProgress, builder.widgetColor);
            } else if (!builder.indeterminateProgress) {
                HorizontalProgressDrawable d = new HorizontalProgressDrawable(builder.getContext());
                d.setTint(builder.widgetColor);
                dialog.mProgress.setProgressDrawable(d);
                dialog.mProgress.setIndeterminateDrawable(d);
            } else if (builder.indeterminateIsHorizontalProgress) {
                IndeterminateHorizontalProgressDrawable d2 = new IndeterminateHorizontalProgressDrawable(builder.getContext());
                d2.setTint(builder.widgetColor);
                dialog.mProgress.setProgressDrawable(d2);
                dialog.mProgress.setIndeterminateDrawable(d2);
            } else {
                IndeterminateProgressDrawable d3 = new IndeterminateProgressDrawable(builder.getContext());
                d3.setTint(builder.widgetColor);
                dialog.mProgress.setProgressDrawable(d3);
                dialog.mProgress.setIndeterminateDrawable(d3);
            }
            if (builder.indeterminateProgress) {
                if (!builder.indeterminateIsHorizontalProgress) {
                    if (dialog.mProgress == null) {
                        fixCanvasScalingWhenHardwareAccelerated(dialog.mProgress);
                    }
                }
            }
            dialog.mProgress.setIndeterminate(builder.indeterminateIsHorizontalProgress);
            dialog.mProgress.setProgress(0);
            dialog.mProgress.setMax(builder.progressMax);
            dialog.mProgressLabel = (TextView) dialog.view.findViewById(C0498R.id.md_label);
            if (dialog.mProgressLabel != null) {
                dialog.mProgressLabel.setTextColor(builder.contentColor);
                dialog.setTypeface(dialog.mProgressLabel, builder.mediumFont);
                dialog.mProgressLabel.setText(builder.progressPercentFormat.format(0));
            }
            dialog.mProgressMinMax = (TextView) dialog.view.findViewById(C0498R.id.md_minMax);
            if (dialog.mProgressMinMax != null) {
                dialog.mProgressMinMax.setTextColor(builder.contentColor);
                dialog.setTypeface(dialog.mProgressMinMax, builder.regularFont);
                if (builder.showMinMax) {
                    dialog.mProgressMinMax.setVisibility(0);
                    dialog.mProgressMinMax.setText(String.format(builder.progressNumberFormat, new Object[]{Integer.valueOf(0), Integer.valueOf(builder.progressMax)}));
                    MarginLayoutParams lp = (MarginLayoutParams) dialog.mProgress.getLayoutParams();
                    lp.leftMargin = 0;
                    lp.rightMargin = 0;
                } else {
                    dialog.mProgressMinMax.setVisibility(8);
                }
            } else {
                builder.showMinMax = false;
            }
            if (dialog.mProgress == null) {
                fixCanvasScalingWhenHardwareAccelerated(dialog.mProgress);
            }
        }
    }

    private static void setupInputDialog(MaterialDialog dialog) {
        Builder builder = dialog.mBuilder;
        dialog.input = (EditText) dialog.view.findViewById(16908297);
        if (dialog.input != null) {
            dialog.setTypeface(dialog.input, builder.regularFont);
            if (builder.inputPrefill != null) {
                dialog.input.setText(builder.inputPrefill);
            }
            dialog.setInternalInputCallback();
            dialog.input.setHint(builder.inputHint);
            dialog.input.setSingleLine();
            dialog.input.setTextColor(builder.contentColor);
            dialog.input.setHintTextColor(DialogUtils.adjustAlpha(builder.contentColor, 0.3f));
            MDTintHelper.setTint(dialog.input, dialog.mBuilder.widgetColor);
            if (builder.inputType != -1) {
                dialog.input.setInputType(builder.inputType);
                if (builder.inputType != 144 && (builder.inputType & 128) == 128) {
                    dialog.input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
            dialog.inputMinMax = (TextView) dialog.view.findViewById(C0498R.id.md_minMax);
            if (builder.inputMinLength <= 0) {
                if (builder.inputMaxLength <= -1) {
                    dialog.inputMinMax.setVisibility(8);
                    dialog.inputMinMax = null;
                }
            }
            dialog.invalidateInputMinMaxIndicator(dialog.input.getText().toString().length(), builder.inputAllowEmpty ^ 1);
        }
    }
}
