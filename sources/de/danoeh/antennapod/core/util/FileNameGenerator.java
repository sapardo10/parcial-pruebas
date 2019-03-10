package de.danoeh.antennapod.core.util;

import android.text.TextUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.text.CharacterPredicate;
import org.apache.commons.text.RandomStringGenerator.Builder;

public class FileNameGenerator {
    private static final char[] validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 _-".toCharArray();

    private FileNameGenerator() {
    }

    public static String generateFileName(String string) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (Character.isSpaceChar(c)) {
                if (buf.length() != 0) {
                    if (Character.isSpaceChar(buf.charAt(buf.length() - 1))) {
                    }
                }
            }
            if (ArrayUtils.contains(validChars, c)) {
                buf.append(c);
            }
        }
        String filename = buf.toString().trim();
        if (!TextUtils.isEmpty(filename)) {
            return filename;
        }
        return new Builder().withinRange(48, 122).filteredBy(new CharacterPredicate[]{-$$Lambda$oD74yg8sfjTBI7JstpEXgty_D_k.INSTANCE}).build().generate(8);
    }
}
