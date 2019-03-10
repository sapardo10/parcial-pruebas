package org.awaitility.core;

import java.util.LinkedList;
import java.util.List;
import org.hamcrest.Matcher;

class HamcrestToStringFilter {
    private static final List<String> wordsToRemove = new LinkedList();

    HamcrestToStringFilter() {
    }

    static {
        wordsToRemove.add("not not ");
        wordsToRemove.add("is ");
    }

    static String filter(Matcher<?> matcher) {
        String matcherToString = matcher.toString();
        for (String wordToRemove : wordsToRemove) {
            matcherToString = matcherToString.replaceAll(wordToRemove, "");
        }
        return matcherToString;
    }
}
