package org.jsoup.parser;

import kotlin.text.Typography;
import org.jsoup.helper.StringUtil;
import org.jsoup.helper.Validate;

public class TokenQueue {
    private static final char ESC = '\\';
    private int pos = 0;
    private String queue;

    public TokenQueue(String data) {
        Validate.notNull(data);
        this.queue = data;
    }

    public boolean isEmpty() {
        return remainingLength() == 0;
    }

    private int remainingLength() {
        return this.queue.length() - this.pos;
    }

    public char peek() {
        return isEmpty() ? '\u0000' : this.queue.charAt(this.pos);
    }

    public void addFirst(Character c) {
        addFirst(c.toString());
    }

    public void addFirst(String seq) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(seq);
        stringBuilder.append(this.queue.substring(this.pos));
        this.queue = stringBuilder.toString();
        this.pos = 0;
    }

    public boolean matches(String seq) {
        return this.queue.regionMatches(true, this.pos, seq, 0, seq.length());
    }

    public boolean matchesCS(String seq) {
        return this.queue.startsWith(seq, this.pos);
    }

    public boolean matchesAny(String... seq) {
        for (String s : seq) {
            if (matches(s)) {
                return true;
            }
        }
        return false;
    }

    public boolean matchesAny(char... seq) {
        if (isEmpty()) {
            return false;
        }
        for (char c : seq) {
            if (this.queue.charAt(this.pos) == c) {
                return true;
            }
        }
        return false;
    }

    public boolean matchesStartTag() {
        return remainingLength() >= 2 && this.queue.charAt(this.pos) == Typography.less && Character.isLetter(this.queue.charAt(this.pos + 1));
    }

    public boolean matchChomp(String seq) {
        if (!matches(seq)) {
            return false;
        }
        this.pos += seq.length();
        return true;
    }

    public boolean matchesWhitespace() {
        return !isEmpty() && StringUtil.isWhitespace(this.queue.charAt(this.pos));
    }

    public boolean matchesWord() {
        return !isEmpty() && Character.isLetterOrDigit(this.queue.charAt(this.pos));
    }

    public void advance() {
        if (!isEmpty()) {
            this.pos++;
        }
    }

    public char consume() {
        String str = this.queue;
        int i = this.pos;
        this.pos = i + 1;
        return str.charAt(i);
    }

    public void consume(String seq) {
        if (matches(seq)) {
            int len = seq.length();
            if (len <= remainingLength()) {
                this.pos += len;
                return;
            }
            throw new IllegalStateException("Queue not long enough to consume sequence");
        }
        throw new IllegalStateException("Queue did not match expected sequence");
    }

    public String consumeTo(String seq) {
        int offset = this.queue.indexOf(seq, this.pos);
        if (offset == -1) {
            return remainder();
        }
        String consumed = this.queue.substring(this.pos, offset);
        this.pos += consumed.length();
        return consumed;
    }

    public String consumeToIgnoreCase(String seq) {
        int start = this.pos;
        String first = seq.substring(null, 1);
        boolean canScan = first.toLowerCase().equals(first.toUpperCase());
        while (!isEmpty()) {
            if (matches(seq)) {
                break;
            } else if (canScan) {
                int skip = this.queue.indexOf(first, this.pos);
                int i = this.pos;
                skip -= i;
                if (skip == 0) {
                    this.pos = i + 1;
                } else if (skip < 0) {
                    this.pos = this.queue.length();
                } else {
                    this.pos = i + skip;
                }
            } else {
                this.pos++;
            }
        }
        return this.queue.substring(start, this.pos);
    }

    public String consumeToAny(String... seq) {
        int start = this.pos;
        while (!isEmpty() && !matchesAny(seq)) {
            this.pos++;
        }
        return this.queue.substring(start, this.pos);
    }

    public String chompTo(String seq) {
        String data = consumeTo(seq);
        matchChomp(seq);
        return data;
    }

    public String chompToIgnoreCase(String seq) {
        String data = consumeToIgnoreCase(seq);
        matchChomp(seq);
        return data;
    }

    public String chompBalanced(char open, char close) {
        int start = -1;
        int end = -1;
        int depth = 0;
        char last = '\u0000';
        boolean inQuote = false;
        while (!isEmpty()) {
            Character c = Character.valueOf(consume());
            if (last != '\u0000') {
                if (last == '\\') {
                    if (depth <= 0 && last != '\u0000') {
                        end = this.pos;
                    }
                    last = c.charValue();
                    if (depth > 0) {
                        break;
                    }
                }
            }
            if ((c.equals(Character.valueOf('\'')) || c.equals(Character.valueOf(Typography.quote))) && c.charValue() != open) {
                inQuote = !inQuote;
            }
            if (inQuote) {
                if (depth > 0) {
                    break;
                }
            } else {
                if (c.equals(Character.valueOf(open))) {
                    depth++;
                    if (start == -1) {
                        start = this.pos;
                    }
                } else if (c.equals(Character.valueOf(close))) {
                    depth--;
                }
                if (depth <= 0) {
                }
                last = c.charValue();
                if (depth > 0) {
                    break;
                }
            }
        }
        String out = end >= 0 ? this.queue.substring(start, end) : "";
        if (depth > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Did not find balanced marker at '");
            stringBuilder.append(out);
            stringBuilder.append("'");
            Validate.fail(stringBuilder.toString());
        }
        return out;
    }

    public static String unescape(String in) {
        StringBuilder out = StringUtil.stringBuilder();
        char last = '\u0000';
        for (char c : in.toCharArray()) {
            if (c != '\\') {
                out.append(c);
            } else if (last != '\u0000' && last == '\\') {
                out.append(c);
            }
            last = c;
        }
        return out.toString();
    }

    public boolean consumeWhitespace() {
        boolean seen = false;
        while (matchesWhitespace()) {
            this.pos++;
            seen = true;
        }
        return seen;
    }

    public String consumeWord() {
        int start = this.pos;
        while (matchesWord()) {
            this.pos++;
        }
        return this.queue.substring(start, this.pos);
    }

    public String consumeTagName() {
        int start = this.pos;
        while (!isEmpty() && (matchesWord() || matchesAny(':', '_', '-'))) {
            this.pos++;
        }
        return this.queue.substring(start, this.pos);
    }

    public String consumeElementSelector() {
        int start = this.pos;
        while (!isEmpty()) {
            if (!matchesWord()) {
                if (!matchesAny("*|", "|", "_", "-")) {
                    break;
                }
            }
            this.pos++;
        }
        return this.queue.substring(start, this.pos);
    }

    public String consumeCssIdentifier() {
        int start = this.pos;
        while (!isEmpty() && (matchesWord() || matchesAny('-', '_'))) {
            this.pos++;
        }
        return this.queue.substring(start, this.pos);
    }

    public String consumeAttributeKey() {
        int start = this.pos;
        while (!isEmpty() && (matchesWord() || matchesAny('-', '_', ':'))) {
            this.pos++;
        }
        return this.queue.substring(start, this.pos);
    }

    public String remainder() {
        String remainder = this.queue;
        remainder = remainder.substring(this.pos, remainder.length());
        this.pos = this.queue.length();
        return remainder;
    }

    public String toString() {
        return this.queue.substring(this.pos);
    }
}
