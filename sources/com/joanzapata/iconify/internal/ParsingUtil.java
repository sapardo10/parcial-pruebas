package com.joanzapata.iconify.internal;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.text.SpannableStringBuilder;
import android.util.TypedValue;
import android.widget.TextView;
import com.joanzapata.iconify.Icon;
import com.joanzapata.iconify.internal.HasOnViewAttachListener.OnViewAttachListener;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public final class ParsingUtil {
    private static final String ANDROID_PACKAGE_NAME = "android";

    private ParsingUtil() {
    }

    public static CharSequence parse(Context context, List<IconFontDescriptorWrapper> iconFontDescriptors, CharSequence text, final TextView target) {
        context = context.getApplicationContext();
        if (text == null) {
            return text;
        }
        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(text);
        recursivePrepareSpannableIndexes(context, text.toString(), spannableBuilder, iconFontDescriptors, 0);
        if (hasAnimatedSpans(spannableBuilder)) {
            if (target == null) {
                throw new IllegalArgumentException("You can't use \"spin\" without providing the target TextView.");
            } else if (target instanceof HasOnViewAttachListener) {
                ((HasOnViewAttachListener) target).setOnViewAttachListener(new OnViewAttachListener() {
                    boolean isAttached = null;

                    /* renamed from: com.joanzapata.iconify.internal.ParsingUtil$1$1 */
                    class C06691 implements Runnable {
                        C06691() {
                        }

                        public void run() {
                            if (C09861.this.isAttached) {
                                target.invalidate();
                                ViewCompat.postOnAnimation(target, this);
                            }
                        }
                    }

                    public void onAttach() {
                        this.isAttached = true;
                        ViewCompat.postOnAnimation(target, new C06691());
                    }

                    public void onDetach() {
                        this.isAttached = false;
                    }
                });
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(target.getClass().getSimpleName());
                stringBuilder.append(" does not implement ");
                stringBuilder.append("HasOnViewAttachListener. Please use IconTextView, IconButton or IconToggleButton.");
                throw new IllegalArgumentException(stringBuilder.toString());
            }
        } else if (target instanceof HasOnViewAttachListener) {
            ((HasOnViewAttachListener) target).setOnViewAttachListener(null);
        }
        return spannableBuilder;
    }

    private static boolean hasAnimatedSpans(SpannableStringBuilder spannableBuilder) {
        for (CustomTypefaceSpan span : (CustomTypefaceSpan[]) spannableBuilder.getSpans(0, spannableBuilder.length(), CustomTypefaceSpan.class)) {
            if (span.isAnimated()) {
                return true;
            }
        }
        return false;
    }

    private static void recursivePrepareSpannableIndexes(Context context, String fullText, SpannableStringBuilder text, List<IconFontDescriptorWrapper> iconFontDescriptors, int start) {
        Context context2 = context;
        String str = fullText;
        SpannableStringBuilder text2 = text;
        List<IconFontDescriptorWrapper> list = iconFontDescriptors;
        String stringText = text.toString();
        int startIndex = stringText.indexOf("{", start);
        if (startIndex != -1) {
            int endIndex = stringText.indexOf("}", startIndex) + 1;
            if (endIndex != -1) {
                int i;
                String[] strokes = stringText.substring(startIndex + 1, endIndex - 1).split(StringUtils.SPACE);
                String key = strokes[0];
                IconFontDescriptorWrapper iconFontDescriptor = null;
                Icon icon = null;
                for (i = 0; i < iconFontDescriptors.size(); i++) {
                    iconFontDescriptor = (IconFontDescriptorWrapper) list.get(i);
                    icon = iconFontDescriptor.getIcon(key);
                    if (icon != null) {
                        break;
                    }
                }
                if (icon == null) {
                    recursivePrepareSpannableIndexes(context2, str, text2, list, endIndex);
                    return;
                }
                float iconSizePx = -1.0f;
                int iconColor = Integer.MAX_VALUE;
                float iconSizeRatio = -1.0f;
                boolean spin = false;
                boolean baselineAligned = false;
                i = 1;
                while (i < strokes.length) {
                    String stringText2;
                    String stroke = strokes[i];
                    if (stroke.equalsIgnoreCase("spin")) {
                        spin = true;
                        stringText2 = stringText;
                    } else if (stroke.equalsIgnoreCase("baseline")) {
                        baselineAligned = true;
                        stringText2 = stringText;
                    } else if (stroke.matches("([0-9]*(\\.[0-9]*)?)dp")) {
                        stringText2 = stringText;
                        iconSizePx = dpToPx(context2, Float.valueOf(stroke.substring(null, stroke.length() - 2)).floatValue());
                    } else {
                        stringText2 = stringText;
                        if (stroke.matches("([0-9]*(\\.[0-9]*)?)sp") != null) {
                            iconSizePx = spToPx(context2, Float.valueOf(stroke.substring(0, stroke.length() - 2)).floatValue());
                        } else if (stroke.matches("([0-9]*)px") != null) {
                            iconSizePx = (float) Integer.valueOf(stroke.substring(0, stroke.length() - 2)).intValue();
                        } else if (stroke.matches("@dimen/(.*)") != null) {
                            iconSizePx = getPxFromDimen(context2, context.getPackageName(), stroke.substring(7));
                            if (iconSizePx < null) {
                                r10 = new StringBuilder();
                                r10.append("Unknown resource ");
                                r10.append(stroke);
                                r10.append(" in \"");
                                r10.append(str);
                                r10.append("\"");
                                throw new IllegalArgumentException(r10.toString());
                            }
                        } else if (stroke.matches("@android:dimen/(.*)") != null) {
                            iconSizePx = getPxFromDimen(context2, "android", stroke.substring(15));
                            if (iconSizePx < null) {
                                r6 = new StringBuilder();
                                r6.append("Unknown resource ");
                                r6.append(stroke);
                                r6.append(" in \"");
                                r6.append(str);
                                r6.append("\"");
                                throw new IllegalArgumentException(r6.toString());
                            }
                        } else if (stroke.matches("([0-9]*(\\.[0-9]*)?)%") != null) {
                            iconSizeRatio = Float.valueOf(stroke.substring(0, stroke.length() - 1)).floatValue() / 100.0f;
                        } else if (stroke.matches("#([0-9A-Fa-f]{6}|[0-9A-Fa-f]{8})") != null) {
                            iconColor = Color.parseColor(stroke);
                        } else if (stroke.matches("@color/(.*)") != null) {
                            stringText = getColorFromResource(context2, context.getPackageName(), stroke.substring(7));
                            if (stringText != 2147483647) {
                                iconColor = stringText;
                            } else {
                                r10 = new StringBuilder();
                                int iconColor2 = stringText;
                                r10.append("Unknown resource ");
                                r10.append(stroke);
                                r10.append(" in \"");
                                r10.append(str);
                                r10.append("\"");
                                throw new IllegalArgumentException(r10.toString());
                            }
                        } else if (stroke.matches("@android:color/(.*)") != null) {
                            stringText = getColorFromResource(context2, "android", stroke.substring(15));
                            if (stringText != 2147483647) {
                                iconColor = stringText;
                            } else {
                                r10 = new StringBuilder();
                                int iconColor3 = stringText;
                                r10.append("Unknown resource ");
                                r10.append(stroke);
                                r10.append(" in \"");
                                r10.append(str);
                                r10.append("\"");
                                throw new IllegalArgumentException(r10.toString());
                            }
                        } else {
                            r6 = new StringBuilder();
                            r6.append("Unknown expression ");
                            r6.append(stroke);
                            r6.append(" in \"");
                            r6.append(str);
                            r6.append("\"");
                            throw new IllegalArgumentException(r6.toString());
                        }
                    }
                    i++;
                    stringText = stringText2;
                    int i2 = start;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("");
                stringBuilder.append(icon.character());
                text2 = text2.replace(startIndex, endIndex, stringBuilder.toString());
                text2.setSpan(new CustomTypefaceSpan(icon, iconFontDescriptor.getTypeface(context2), iconSizePx, iconSizeRatio, iconColor, spin, baselineAligned), startIndex, startIndex + 1, 17);
                recursivePrepareSpannableIndexes(context2, str, text2, list, startIndex);
            }
        }
    }

    public static float getPxFromDimen(Context context, String packageName, String resName) {
        Resources resources = context.getResources();
        int resId = resources.getIdentifier(resName, "dimen", packageName);
        if (resId <= 0) {
            return -1.0f;
        }
        return resources.getDimension(resId);
    }

    public static int getColorFromResource(Context context, String packageName, String resName) {
        Resources resources = context.getResources();
        int resId = resources.getIdentifier(resName, TtmlNode.ATTR_TTS_COLOR, packageName);
        if (resId <= 0) {
            return Integer.MAX_VALUE;
        }
        return resources.getColor(resId);
    }

    public static float dpToPx(Context context, float dp) {
        return TypedValue.applyDimension(1, dp, context.getResources().getDisplayMetrics());
    }

    public static float spToPx(Context context, float sp) {
        return TypedValue.applyDimension(2, sp, context.getResources().getDisplayMetrics());
    }
}
