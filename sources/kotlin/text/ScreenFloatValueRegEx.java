package kotlin.text;

import kotlin.Metadata;
import kotlin.jvm.JvmField;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bÂ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u0010\u0010\u0003\u001a\u00020\u00048\u0006X\u0004¢\u0006\u0002\n\u0000¨\u0006\u0005"}, d2 = {"Lkotlin/text/ScreenFloatValueRegEx;", "", "()V", "value", "Lkotlin/text/Regex;", "kotlin-stdlib"}, k = 1, mv = {1, 1, 10})
/* compiled from: StringNumberConversionsJVM.kt */
final class ScreenFloatValueRegEx {
    public static final ScreenFloatValueRegEx INSTANCE = new ScreenFloatValueRegEx();
    @NotNull
    @JvmField
    public static final Regex value;

    static {
        String Digits = "(\\p{Digit}+)";
        String HexDigits = "(\\p{XDigit}+)";
        String Exp = new StringBuilder();
        Exp.append("[eE][+-]?");
        Exp.append(Digits);
        Exp = Exp.toString();
        String HexString = new StringBuilder();
        HexString.append("(0[xX]");
        HexString.append(HexDigits);
        HexString.append("(\\.)?)|");
        HexString.append("(0[xX]");
        HexString.append(HexDigits);
        HexString.append("?(\\.)");
        HexString.append(HexDigits);
        HexString.append(')');
        HexString = HexString.toString();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');
        stringBuilder.append(Digits);
        stringBuilder.append("(\\.)?(");
        stringBuilder.append(Digits);
        stringBuilder.append("?)(");
        stringBuilder.append(Exp);
        stringBuilder.append(")?)|");
        stringBuilder.append("(\\.(");
        stringBuilder.append(Digits);
        stringBuilder.append(")(");
        stringBuilder.append(Exp);
        stringBuilder.append(")?)|");
        stringBuilder.append("((");
        stringBuilder.append(HexString);
        stringBuilder.append(")[pP][+-]?");
        stringBuilder.append(Digits);
        stringBuilder.append(')');
        String Number = stringBuilder.toString();
        String fpRegex = new StringBuilder();
        fpRegex.append("[\\x00-\\x20]*[+-]?(NaN|Infinity|((");
        fpRegex.append(Number);
        fpRegex.append(")[fFdD]?))[\\x00-\\x20]*");
        value = new Regex(fpRegex.toString());
    }

    private ScreenFloatValueRegEx() {
    }
}
