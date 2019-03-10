package org.apache.commons.text.similarity;

public class CosineDistance implements EditDistance<Double> {
    private final CosineSimilarity cosineSimilarity = new CosineSimilarity();
    private final Tokenizer<CharSequence> tokenizer = new RegexTokenizer();

    public Double apply(CharSequence left, CharSequence right) {
        CharSequence[] rightTokens = (CharSequence[]) this.tokenizer.tokenize(right);
        return Double.valueOf(1.0d - this.cosineSimilarity.cosineSimilarity(Counter.of((CharSequence[]) this.tokenizer.tokenize(left)), Counter.of(rightTokens)).doubleValue());
    }
}
