package org.apache.commons.lang3.text.translate;

import android.support.v4.internal.view.SupportMenu;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.EnumSet;
import kotlin.text.Typography;

@Deprecated
public class NumericEntityUnescaper extends CharSequenceTranslator {
    private final EnumSet<OPTION> options;

    public enum OPTION {
        semiColonRequired,
        semiColonOptional,
        errorIfNoSemiColon
    }

    public NumericEntityUnescaper(OPTION... options) {
        if (options.length > 0) {
            this.options = EnumSet.copyOf(Arrays.asList(options));
            return;
        }
        this.options = EnumSet.copyOf(Arrays.asList(new OPTION[]{OPTION.semiColonRequired}));
    }

    public boolean isSet(OPTION option) {
        EnumSet enumSet = this.options;
        return enumSet != null && enumSet.contains(option);
    }

    public int translate(CharSequence input, int index, Writer out) throws IOException {
        int seqEnd = input.length();
        int i = 0;
        if (input.charAt(index) != Typography.amp || index >= seqEnd - 2 || input.charAt(index + 1) != '#') {
            return 0;
        }
        int end;
        boolean semiNext;
        int entityValue;
        int start = index + 2;
        boolean isHex = false;
        char firstChar = input.charAt(start);
        if (firstChar != 'x') {
            if (firstChar != 'X') {
                end = start;
                while (end < seqEnd) {
                    if (input.charAt(end) >= '0') {
                        if (input.charAt(end) > '9') {
                        }
                        end++;
                    }
                    if (input.charAt(end) < 'a' || input.charAt(end) > 'f') {
                        if (input.charAt(end) < 'A' || input.charAt(end) > 'F') {
                            break;
                        }
                    }
                    end++;
                }
                semiNext = end == seqEnd && input.charAt(end) == ';';
                if (!semiNext) {
                    if (isSet(OPTION.semiColonRequired)) {
                        return 0;
                    }
                    if (!isSet(OPTION.errorIfNoSemiColon)) {
                        throw new IllegalArgumentException("Semi-colon required at end of numeric entity");
                    }
                }
                if (isHex) {
                    entityValue = Integer.parseInt(input.subSequence(start, end).toString(), 10);
                } else {
                    try {
                        entityValue = Integer.parseInt(input.subSequence(start, end).toString(), 16);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }
                if (entityValue <= SupportMenu.USER_MASK) {
                    char[] chars = Character.toChars(entityValue);
                    out.write(chars[0]);
                    out.write(chars[1]);
                } else {
                    out.write(entityValue);
                }
                int i2 = ((end + 2) - start) + (isHex ? 1 : 0);
                if (semiNext) {
                    i = 1;
                }
                return i2 + i;
            }
        }
        start++;
        isHex = true;
        if (start == seqEnd) {
            return 0;
        }
        end = start;
        while (end < seqEnd) {
            if (input.charAt(end) >= '0') {
                if (input.charAt(end) > '9') {
                }
                end++;
            }
            end++;
        }
        if (end == seqEnd) {
        }
        if (!semiNext) {
            if (isSet(OPTION.semiColonRequired)) {
                return 0;
            }
            if (!isSet(OPTION.errorIfNoSemiColon)) {
                throw new IllegalArgumentException("Semi-colon required at end of numeric entity");
            }
        }
        if (isHex) {
            entityValue = Integer.parseInt(input.subSequence(start, end).toString(), 10);
        } else {
            entityValue = Integer.parseInt(input.subSequence(start, end).toString(), 16);
        }
        if (entityValue <= SupportMenu.USER_MASK) {
            out.write(entityValue);
        } else {
            char[] chars2 = Character.toChars(entityValue);
            out.write(chars2[0]);
            out.write(chars2[1]);
        }
        if (isHex) {
        }
        int i22 = ((end + 2) - start) + (isHex ? 1 : 0);
        if (semiNext) {
            i = 1;
        }
        return i22 + i;
    }
}
