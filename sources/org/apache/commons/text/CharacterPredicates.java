package org.apache.commons.text;

public enum CharacterPredicates implements CharacterPredicate {
    LETTERS {
        public boolean test(int codePoint) {
            return Character.isLetter(codePoint);
        }
    },
    DIGITS {
        public boolean test(int codePoint) {
            return Character.isDigit(codePoint);
        }
    },
    ARABIC_NUMERALS {
        public boolean test(int codePoint) {
            return codePoint >= 48 && codePoint <= 57;
        }
    },
    ASCII_LOWERCASE_LETTERS {
        public boolean test(int codePoint) {
            return codePoint >= 97 && codePoint <= 122;
        }
    },
    ASCII_UPPERCASE_LETTERS {
        public boolean test(int codePoint) {
            return codePoint >= 65 && codePoint <= 90;
        }
    },
    ASCII_LETTERS {
        public boolean test(int codePoint) {
            if (!ASCII_LOWERCASE_LETTERS.test(codePoint)) {
                if (!ASCII_UPPERCASE_LETTERS.test(codePoint)) {
                    return false;
                }
            }
            return true;
        }
    },
    ASCII_ALPHA_NUMERALS {
        public boolean test(int codePoint) {
            if (!ASCII_LOWERCASE_LETTERS.test(codePoint) && !ASCII_UPPERCASE_LETTERS.test(codePoint)) {
                if (!ARABIC_NUMERALS.test(codePoint)) {
                    return false;
                }
            }
            return true;
        }
    }
}
