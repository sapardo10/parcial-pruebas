package org.apache.commons.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.apache.commons.text.matcher.StringMatcher;
import org.apache.commons.text.matcher.StringMatcherFactory;

public class StringTokenizer implements ListIterator<String>, Cloneable {
    private static final StringTokenizer CSV_TOKENIZER_PROTOTYPE = new StringTokenizer();
    private static final StringTokenizer TSV_TOKENIZER_PROTOTYPE = new StringTokenizer();
    private char[] chars;
    private StringMatcher delimMatcher;
    private boolean emptyAsNull;
    private boolean ignoreEmptyTokens;
    private StringMatcher ignoredMatcher;
    private StringMatcher quoteMatcher;
    private int tokenPos;
    private String[] tokens;
    private StringMatcher trimmerMatcher;

    static {
        CSV_TOKENIZER_PROTOTYPE.setDelimiterMatcher(StringMatcherFactory.INSTANCE.commaMatcher());
        CSV_TOKENIZER_PROTOTYPE.setQuoteMatcher(StringMatcherFactory.INSTANCE.doubleQuoteMatcher());
        CSV_TOKENIZER_PROTOTYPE.setIgnoredMatcher(StringMatcherFactory.INSTANCE.noneMatcher());
        CSV_TOKENIZER_PROTOTYPE.setTrimmerMatcher(StringMatcherFactory.INSTANCE.trimMatcher());
        CSV_TOKENIZER_PROTOTYPE.setEmptyTokenAsNull(false);
        CSV_TOKENIZER_PROTOTYPE.setIgnoreEmptyTokens(false);
        TSV_TOKENIZER_PROTOTYPE.setDelimiterMatcher(StringMatcherFactory.INSTANCE.tabMatcher());
        TSV_TOKENIZER_PROTOTYPE.setQuoteMatcher(StringMatcherFactory.INSTANCE.doubleQuoteMatcher());
        TSV_TOKENIZER_PROTOTYPE.setIgnoredMatcher(StringMatcherFactory.INSTANCE.noneMatcher());
        TSV_TOKENIZER_PROTOTYPE.setTrimmerMatcher(StringMatcherFactory.INSTANCE.trimMatcher());
        TSV_TOKENIZER_PROTOTYPE.setEmptyTokenAsNull(false);
        TSV_TOKENIZER_PROTOTYPE.setIgnoreEmptyTokens(false);
    }

    private static StringTokenizer getCSVClone() {
        return (StringTokenizer) CSV_TOKENIZER_PROTOTYPE.clone();
    }

    public static StringTokenizer getCSVInstance() {
        return getCSVClone();
    }

    public static StringTokenizer getCSVInstance(String input) {
        StringTokenizer tok = getCSVClone();
        tok.reset(input);
        return tok;
    }

    public static StringTokenizer getCSVInstance(char[] input) {
        StringTokenizer tok = getCSVClone();
        tok.reset(input);
        return tok;
    }

    private static StringTokenizer getTSVClone() {
        return (StringTokenizer) TSV_TOKENIZER_PROTOTYPE.clone();
    }

    public static StringTokenizer getTSVInstance() {
        return getTSVClone();
    }

    public static StringTokenizer getTSVInstance(String input) {
        StringTokenizer tok = getTSVClone();
        tok.reset(input);
        return tok;
    }

    public static StringTokenizer getTSVInstance(char[] input) {
        StringTokenizer tok = getTSVClone();
        tok.reset(input);
        return tok;
    }

    public StringTokenizer() {
        this.delimMatcher = StringMatcherFactory.INSTANCE.splitMatcher();
        this.quoteMatcher = StringMatcherFactory.INSTANCE.noneMatcher();
        this.ignoredMatcher = StringMatcherFactory.INSTANCE.noneMatcher();
        this.trimmerMatcher = StringMatcherFactory.INSTANCE.noneMatcher();
        this.emptyAsNull = false;
        this.ignoreEmptyTokens = true;
        this.chars = null;
    }

    public StringTokenizer(String input) {
        this.delimMatcher = StringMatcherFactory.INSTANCE.splitMatcher();
        this.quoteMatcher = StringMatcherFactory.INSTANCE.noneMatcher();
        this.ignoredMatcher = StringMatcherFactory.INSTANCE.noneMatcher();
        this.trimmerMatcher = StringMatcherFactory.INSTANCE.noneMatcher();
        this.emptyAsNull = false;
        this.ignoreEmptyTokens = true;
        if (input != null) {
            this.chars = input.toCharArray();
        } else {
            this.chars = null;
        }
    }

    public StringTokenizer(String input, char delim) {
        this(input);
        setDelimiterChar(delim);
    }

    public StringTokenizer(String input, String delim) {
        this(input);
        setDelimiterString(delim);
    }

    public StringTokenizer(String input, StringMatcher delim) {
        this(input);
        setDelimiterMatcher(delim);
    }

    public StringTokenizer(String input, char delim, char quote) {
        this(input, delim);
        setQuoteChar(quote);
    }

    public StringTokenizer(String input, StringMatcher delim, StringMatcher quote) {
        this(input, delim);
        setQuoteMatcher(quote);
    }

    public StringTokenizer(char[] input) {
        this.delimMatcher = StringMatcherFactory.INSTANCE.splitMatcher();
        this.quoteMatcher = StringMatcherFactory.INSTANCE.noneMatcher();
        this.ignoredMatcher = StringMatcherFactory.INSTANCE.noneMatcher();
        this.trimmerMatcher = StringMatcherFactory.INSTANCE.noneMatcher();
        this.emptyAsNull = false;
        this.ignoreEmptyTokens = true;
        if (input == null) {
            this.chars = null;
        } else {
            this.chars = (char[]) input.clone();
        }
    }

    public StringTokenizer(char[] input, char delim) {
        this(input);
        setDelimiterChar(delim);
    }

    public StringTokenizer(char[] input, String delim) {
        this(input);
        setDelimiterString(delim);
    }

    public StringTokenizer(char[] input, StringMatcher delim) {
        this(input);
        setDelimiterMatcher(delim);
    }

    public StringTokenizer(char[] input, char delim, char quote) {
        this(input, delim);
        setQuoteChar(quote);
    }

    public StringTokenizer(char[] input, StringMatcher delim, StringMatcher quote) {
        this(input, delim);
        setQuoteMatcher(quote);
    }

    public int size() {
        checkTokenized();
        return this.tokens.length;
    }

    public String nextToken() {
        if (!hasNext()) {
            return null;
        }
        String[] strArr = this.tokens;
        int i = this.tokenPos;
        this.tokenPos = i + 1;
        return strArr[i];
    }

    public String previousToken() {
        if (!hasPrevious()) {
            return null;
        }
        String[] strArr = this.tokens;
        int i = this.tokenPos - 1;
        this.tokenPos = i;
        return strArr[i];
    }

    public String[] getTokenArray() {
        checkTokenized();
        return (String[]) this.tokens.clone();
    }

    public List<String> getTokenList() {
        checkTokenized();
        List<String> list = new ArrayList(this.tokens.length);
        Collections.addAll(list, this.tokens);
        return list;
    }

    public StringTokenizer reset() {
        this.tokenPos = 0;
        this.tokens = null;
        return this;
    }

    public StringTokenizer reset(String input) {
        reset();
        if (input != null) {
            this.chars = input.toCharArray();
        } else {
            this.chars = null;
        }
        return this;
    }

    public StringTokenizer reset(char[] input) {
        reset();
        if (input != null) {
            this.chars = (char[]) input.clone();
        } else {
            this.chars = null;
        }
        return this;
    }

    public boolean hasNext() {
        checkTokenized();
        return this.tokenPos < this.tokens.length;
    }

    public String next() {
        if (hasNext()) {
            String[] strArr = this.tokens;
            int i = this.tokenPos;
            this.tokenPos = i + 1;
            return strArr[i];
        }
        throw new NoSuchElementException();
    }

    public int nextIndex() {
        return this.tokenPos;
    }

    public boolean hasPrevious() {
        checkTokenized();
        return this.tokenPos > 0;
    }

    public String previous() {
        if (hasPrevious()) {
            String[] strArr = this.tokens;
            int i = this.tokenPos - 1;
            this.tokenPos = i;
            return strArr[i];
        }
        throw new NoSuchElementException();
    }

    public int previousIndex() {
        return this.tokenPos - 1;
    }

    public void remove() {
        throw new UnsupportedOperationException("remove() is unsupported");
    }

    public void set(String obj) {
        throw new UnsupportedOperationException("set() is unsupported");
    }

    public void add(String obj) {
        throw new UnsupportedOperationException("add() is unsupported");
    }

    private void checkTokenized() {
        if (this.tokens == null) {
            char[] cArr = this.chars;
            List<String> split;
            if (cArr == null) {
                split = tokenize(null, 0, 0);
                this.tokens = (String[]) split.toArray(new String[split.size()]);
                return;
            }
            split = tokenize(cArr, 0, cArr.length);
            this.tokens = (String[]) split.toArray(new String[split.size()]);
        }
    }

    protected List<String> tokenize(char[] srcChars, int offset, int count) {
        if (srcChars != null) {
            if (count != 0) {
                TextStringBuilder buf = new TextStringBuilder();
                List tokenList = new ArrayList();
                int pos = offset;
                while (pos >= 0 && pos < count) {
                    pos = readNextToken(srcChars, pos, count, buf, tokenList);
                    if (pos >= count) {
                        addToken(tokenList, "");
                    }
                }
                return tokenList;
            }
        }
        return Collections.emptyList();
    }

    private void addToken(List<String> list, String tok) {
        if (tok != null) {
            if (tok.length() != 0) {
                list.add(tok);
            }
        }
        if (!isIgnoreEmptyTokens()) {
            if (isEmptyTokenAsNull()) {
                tok = null;
            }
            list.add(tok);
        }
    }

    private int readNextToken(char[] srcChars, int start, int len, TextStringBuilder workArea, List<String> tokenList) {
        while (start < len) {
            int removeLen = Math.max(getIgnoredMatcher().isMatch(srcChars, start, start, len), getTrimmerMatcher().isMatch(srcChars, start, start, len));
            if (removeLen == 0 || getDelimiterMatcher().isMatch(srcChars, start, start, len) > 0) {
                break;
            } else if (getQuoteMatcher().isMatch(srcChars, start, start, len) > 0) {
                break;
            } else {
                start += removeLen;
            }
        }
        if (start >= len) {
            addToken(tokenList, "");
            return -1;
        }
        int delimLen = getDelimiterMatcher().isMatch(srcChars, start, start, len);
        if (delimLen > 0) {
            addToken(tokenList, "");
            return start + delimLen;
        }
        int quoteLen = getQuoteMatcher().isMatch(srcChars, start, start, len);
        if (quoteLen <= 0) {
            return readWithQuotes(srcChars, start, len, workArea, tokenList, 0, 0);
        }
        return readWithQuotes(srcChars, start + quoteLen, len, workArea, tokenList, start, quoteLen);
    }

    private int readWithQuotes(char[] srcChars, int start, int len, TextStringBuilder workArea, List<String> tokenList, int quoteStart, int quoteLen) {
        StringTokenizer stringTokenizer = this;
        char[] cArr = srcChars;
        int i = start;
        int i2 = len;
        TextStringBuilder textStringBuilder = workArea;
        List<String> list = tokenList;
        int i3 = quoteLen;
        workArea.clear();
        int pos = start;
        boolean quoting = i3 > 0;
        int trimStart = 0;
        while (pos < i2) {
            int ignoredLen;
            if (!quoting) {
                int delimLen = getDelimiterMatcher().isMatch(cArr, pos, i, i2);
                if (delimLen > 0) {
                    addToken(list, textStringBuilder.substring(0, trimStart));
                    return pos + delimLen;
                } else if (i3 <= 0 || !isQuote(srcChars, pos, len, quoteStart, quoteLen)) {
                    ignoredLen = getIgnoredMatcher().isMatch(cArr, pos, i, i2);
                    if (ignoredLen > 0) {
                        pos += ignoredLen;
                    } else {
                        int trimmedLen = getTrimmerMatcher().isMatch(cArr, pos, i, i2);
                        if (trimmedLen > 0) {
                            textStringBuilder.append(cArr, pos, trimmedLen);
                            pos += trimmedLen;
                        } else {
                            int pos2 = pos + 1;
                            textStringBuilder.append(cArr[pos]);
                            trimStart = workArea.size();
                            pos = pos2;
                        }
                    }
                } else {
                    quoting = true;
                    pos += i3;
                }
            } else if (isQuote(srcChars, pos, len, quoteStart, quoteLen)) {
                if (isQuote(srcChars, pos + i3, len, quoteStart, quoteLen)) {
                    textStringBuilder.append(cArr, pos, i3);
                    pos += i3 * 2;
                    trimStart = workArea.size();
                } else {
                    quoting = false;
                    pos += i3;
                }
            } else {
                ignoredLen = pos + 1;
                textStringBuilder.append(cArr[pos]);
                trimStart = workArea.size();
                pos = ignoredLen;
            }
        }
        addToken(list, textStringBuilder.substring(0, trimStart));
        return -1;
    }

    private boolean isQuote(char[] srcChars, int pos, int len, int quoteStart, int quoteLen) {
        int i = 0;
        while (i < quoteLen) {
            if (pos + i < len) {
                if (srcChars[pos + i] == srcChars[quoteStart + i]) {
                    i++;
                }
            }
            return false;
        }
        return true;
    }

    public StringMatcher getDelimiterMatcher() {
        return this.delimMatcher;
    }

    public StringTokenizer setDelimiterMatcher(StringMatcher delim) {
        if (delim == null) {
            this.delimMatcher = StringMatcherFactory.INSTANCE.noneMatcher();
        } else {
            this.delimMatcher = delim;
        }
        return this;
    }

    public StringTokenizer setDelimiterChar(char delim) {
        return setDelimiterMatcher(StringMatcherFactory.INSTANCE.charMatcher(delim));
    }

    public StringTokenizer setDelimiterString(String delim) {
        return setDelimiterMatcher(StringMatcherFactory.INSTANCE.stringMatcher(delim));
    }

    public StringMatcher getQuoteMatcher() {
        return this.quoteMatcher;
    }

    public StringTokenizer setQuoteMatcher(StringMatcher quote) {
        if (quote != null) {
            this.quoteMatcher = quote;
        }
        return this;
    }

    public StringTokenizer setQuoteChar(char quote) {
        return setQuoteMatcher(StringMatcherFactory.INSTANCE.charMatcher(quote));
    }

    public StringMatcher getIgnoredMatcher() {
        return this.ignoredMatcher;
    }

    public StringTokenizer setIgnoredMatcher(StringMatcher ignored) {
        if (ignored != null) {
            this.ignoredMatcher = ignored;
        }
        return this;
    }

    public StringTokenizer setIgnoredChar(char ignored) {
        return setIgnoredMatcher(StringMatcherFactory.INSTANCE.charMatcher(ignored));
    }

    public StringMatcher getTrimmerMatcher() {
        return this.trimmerMatcher;
    }

    public StringTokenizer setTrimmerMatcher(StringMatcher trimmer) {
        if (trimmer != null) {
            this.trimmerMatcher = trimmer;
        }
        return this;
    }

    public boolean isEmptyTokenAsNull() {
        return this.emptyAsNull;
    }

    public StringTokenizer setEmptyTokenAsNull(boolean emptyAsNull) {
        this.emptyAsNull = emptyAsNull;
        return this;
    }

    public boolean isIgnoreEmptyTokens() {
        return this.ignoreEmptyTokens;
    }

    public StringTokenizer setIgnoreEmptyTokens(boolean ignoreEmptyTokens) {
        this.ignoreEmptyTokens = ignoreEmptyTokens;
        return this;
    }

    public String getContent() {
        char[] cArr = this.chars;
        if (cArr == null) {
            return null;
        }
        return new String(cArr);
    }

    public Object clone() {
        try {
            return cloneReset();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    Object cloneReset() throws CloneNotSupportedException {
        StringTokenizer cloned = (StringTokenizer) super.clone();
        Object obj = cloned.chars;
        if (obj != null) {
            cloned.chars = (char[]) obj.clone();
        }
        cloned.reset();
        return cloned;
    }

    public String toString() {
        if (this.tokens == null) {
            return "StringTokenizer[not tokenized yet]";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("StringTokenizer");
        stringBuilder.append(getTokenList());
        return stringBuilder.toString();
    }
}
