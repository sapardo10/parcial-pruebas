package org.apache.commons.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.lang3.Validate;

public final class RandomStringGenerator {
    private final List<Character> characterList;
    private final Set<CharacterPredicate> inclusivePredicates;
    private final int maximumCodePoint;
    private final int minimumCodePoint;
    private final TextRandomProvider random;

    public static class Builder implements Builder<RandomStringGenerator> {
        public static final int DEFAULT_LENGTH = 0;
        public static final int DEFAULT_MAXIMUM_CODE_POINT = 1114111;
        public static final int DEFAULT_MINIMUM_CODE_POINT = 0;
        private List<Character> characterList;
        private Set<CharacterPredicate> inclusivePredicates;
        private int maximumCodePoint = DEFAULT_MAXIMUM_CODE_POINT;
        private int minimumCodePoint = 0;
        private TextRandomProvider random;

        public Builder withinRange(int minimumCodePoint, int maximumCodePoint) {
            boolean z = true;
            Validate.isTrue(minimumCodePoint <= maximumCodePoint, "Minimum code point %d is larger than maximum code point %d", Integer.valueOf(minimumCodePoint), Integer.valueOf(maximumCodePoint));
            Validate.isTrue(minimumCodePoint >= 0, "Minimum code point %d is negative", (long) minimumCodePoint);
            if (maximumCodePoint > DEFAULT_MAXIMUM_CODE_POINT) {
                z = false;
            }
            Validate.isTrue(z, "Value %d is larger than Character.MAX_CODE_POINT.", (long) maximumCodePoint);
            this.minimumCodePoint = minimumCodePoint;
            this.maximumCodePoint = maximumCodePoint;
            return this;
        }

        public Builder withinRange(char[]... pairs) {
            this.characterList = new ArrayList();
            for (char[] pair : pairs) {
                Validate.isTrue(pair.length == 2, "Each pair must contain minimum and maximum code point", new Object[0]);
                int minimumCodePoint = pair[0];
                int maximumCodePoint = pair[1];
                Validate.isTrue(minimumCodePoint <= maximumCodePoint, "Minimum code point %d is larger than maximum code point %d", Integer.valueOf(minimumCodePoint), Integer.valueOf(maximumCodePoint));
                for (int index = minimumCodePoint; index <= maximumCodePoint; index++) {
                    this.characterList.add(Character.valueOf((char) index));
                }
            }
            return this;
        }

        public Builder filteredBy(CharacterPredicate... predicates) {
            if (predicates != null) {
                if (predicates.length != 0) {
                    Set set = this.inclusivePredicates;
                    if (set == null) {
                        this.inclusivePredicates = new HashSet();
                    } else {
                        set.clear();
                    }
                    Collections.addAll(this.inclusivePredicates, predicates);
                    return this;
                }
            }
            this.inclusivePredicates = null;
            return this;
        }

        public Builder usingRandom(TextRandomProvider random) {
            this.random = random;
            return this;
        }

        public Builder selectFrom(char... chars) {
            this.characterList = new ArrayList();
            for (char c : chars) {
                this.characterList.add(Character.valueOf(c));
            }
            return this;
        }

        public RandomStringGenerator build() {
            return new RandomStringGenerator(this.minimumCodePoint, this.maximumCodePoint, this.inclusivePredicates, this.random, this.characterList);
        }
    }

    private RandomStringGenerator(int minimumCodePoint, int maximumCodePoint, Set<CharacterPredicate> inclusivePredicates, TextRandomProvider random, List<Character> characterList) {
        this.minimumCodePoint = minimumCodePoint;
        this.maximumCodePoint = maximumCodePoint;
        this.inclusivePredicates = inclusivePredicates;
        this.random = random;
        this.characterList = characterList;
    }

    private int generateRandomNumber(int minInclusive, int maxInclusive) {
        TextRandomProvider textRandomProvider = this.random;
        if (textRandomProvider != null) {
            return textRandomProvider.nextInt((maxInclusive - minInclusive) + 1) + minInclusive;
        }
        return ThreadLocalRandom.current().nextInt(minInclusive, maxInclusive + 1);
    }

    private int generateRandomNumber(List<Character> characterList) {
        int listSize = characterList.size();
        TextRandomProvider textRandomProvider = this.random;
        if (textRandomProvider != null) {
            return String.valueOf(characterList.get(textRandomProvider.nextInt(listSize))).codePointAt(0);
        }
        return String.valueOf(characterList.get(ThreadLocalRandom.current().nextInt(0, listSize))).codePointAt(0);
    }

    public String generate(int length) {
        if (length == 0) {
            return "";
        }
        Validate.isTrue(length > 0, "Length %d is smaller than zero.", (long) length);
        StringBuilder builder = new StringBuilder(length);
        long remaining = (long) length;
        while (true) {
            int codePoint;
            List list = this.characterList;
            if (list == null || list.isEmpty()) {
                codePoint = generateRandomNumber(this.minimumCodePoint, this.maximumCodePoint);
            } else {
                codePoint = generateRandomNumber(this.characterList);
            }
            int type = Character.getType(codePoint);
            if (type != 0) {
                switch (type) {
                    case 18:
                    case 19:
                        break;
                    default:
                        Set<CharacterPredicate> set = this.inclusivePredicates;
                        if (set != null) {
                            boolean matchedFilter = false;
                            for (CharacterPredicate predicate : set) {
                                if (predicate.test(codePoint)) {
                                    matchedFilter = true;
                                    if (matchedFilter) {
                                        break;
                                    }
                                }
                            }
                            if (matchedFilter) {
                            }
                        }
                        builder.appendCodePoint(codePoint);
                        remaining--;
                        break;
                }
            }
            if (remaining == 0) {
                return builder.toString();
            }
        }
    }

    public String generate(int minLengthInclusive, int maxLengthInclusive) {
        Validate.isTrue(minLengthInclusive >= 0, "Minimum length %d is smaller than zero.", (long) minLengthInclusive);
        Validate.isTrue(minLengthInclusive <= maxLengthInclusive, "Maximum length %d is smaller than minimum length %d.", Integer.valueOf(maxLengthInclusive), Integer.valueOf(minLengthInclusive));
        return generate(generateRandomNumber(minLengthInclusive, maxLengthInclusive));
    }
}
