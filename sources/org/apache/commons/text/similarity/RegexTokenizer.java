package org.apache.commons.text.similarity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

class RegexTokenizer implements Tokenizer<CharSequence> {
    RegexTokenizer() {
    }

    public CharSequence[] tokenize(CharSequence text) {
        Validate.isTrue(StringUtils.isNotBlank(text), "Invalid text", new Object[0]);
        Matcher matcher = Pattern.compile("(\\w)+").matcher(text.toString());
        List<String> tokens = new ArrayList();
        while (matcher.find()) {
            tokens.add(matcher.group(0));
        }
        return (CharSequence[]) tokens.toArray(new String[0]);
    }
}
