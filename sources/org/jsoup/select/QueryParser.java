package org.jsoup.select;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kotlin.text.Typography;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;
import org.jsoup.helper.Validate;
import org.jsoup.internal.Normalizer;
import org.jsoup.parser.TokenQueue;
import org.jsoup.select.Evaluator.AllElements;
import org.jsoup.select.Evaluator.Attribute;
import org.jsoup.select.Evaluator.AttributeStarting;
import org.jsoup.select.Evaluator.AttributeWithValue;
import org.jsoup.select.Evaluator.AttributeWithValueContaining;
import org.jsoup.select.Evaluator.AttributeWithValueEnding;
import org.jsoup.select.Evaluator.AttributeWithValueMatching;
import org.jsoup.select.Evaluator.AttributeWithValueNot;
import org.jsoup.select.Evaluator.AttributeWithValueStarting;
import org.jsoup.select.Evaluator.Class;
import org.jsoup.select.Evaluator.ContainsData;
import org.jsoup.select.Evaluator.ContainsOwnText;
import org.jsoup.select.Evaluator.ContainsText;
import org.jsoup.select.Evaluator.Id;
import org.jsoup.select.Evaluator.IndexEquals;
import org.jsoup.select.Evaluator.IndexGreaterThan;
import org.jsoup.select.Evaluator.IndexLessThan;
import org.jsoup.select.Evaluator.IsEmpty;
import org.jsoup.select.Evaluator.IsFirstChild;
import org.jsoup.select.Evaluator.IsFirstOfType;
import org.jsoup.select.Evaluator.IsLastChild;
import org.jsoup.select.Evaluator.IsLastOfType;
import org.jsoup.select.Evaluator.IsNthChild;
import org.jsoup.select.Evaluator.IsNthLastChild;
import org.jsoup.select.Evaluator.IsNthLastOfType;
import org.jsoup.select.Evaluator.IsNthOfType;
import org.jsoup.select.Evaluator.IsOnlyChild;
import org.jsoup.select.Evaluator.IsOnlyOfType;
import org.jsoup.select.Evaluator.IsRoot;
import org.jsoup.select.Evaluator.MatchText;
import org.jsoup.select.Evaluator.Matches;
import org.jsoup.select.Evaluator.MatchesOwn;
import org.jsoup.select.Evaluator.Tag;
import org.jsoup.select.Evaluator.TagEndsWith;
import org.jsoup.select.Selector.SelectorParseException;

public class QueryParser {
    private static final String[] AttributeEvals = new String[]{"=", "!=", "^=", "$=", "*=", "~="};
    private static final Pattern NTH_AB = Pattern.compile("(([+-])?(\\d+)?)n(\\s*([+-])?\\s*\\d+)?", 2);
    private static final Pattern NTH_B = Pattern.compile("([+-])?(\\d+)");
    private static final String[] combinators = new String[]{",", ">", "+", "~", StringUtils.SPACE};
    private List<Evaluator> evals = new ArrayList();
    private String query;
    private TokenQueue tq;

    private QueryParser(String query) {
        this.query = query;
        this.tq = new TokenQueue(query);
    }

    public static Evaluator parse(String query) {
        try {
            return new QueryParser(query).parse();
        } catch (IllegalArgumentException e) {
            throw new SelectorParseException(e.getMessage(), new Object[0]);
        }
    }

    Evaluator parse() {
        this.tq.consumeWhitespace();
        if (this.tq.matchesAny(combinators)) {
            this.evals.add(new Root());
            combinator(this.tq.consume());
        } else {
            findElements();
        }
        while (!this.tq.isEmpty()) {
            boolean seenWhite = this.tq.consumeWhitespace();
            if (this.tq.matchesAny(combinators)) {
                combinator(this.tq.consume());
            } else if (seenWhite) {
                combinator(' ');
            } else {
                findElements();
            }
        }
        if (this.evals.size() == 1) {
            return (Evaluator) this.evals.get(0);
        }
        return new And(this.evals);
    }

    private void combinator(char combinator) {
        Evaluator rootEval;
        Evaluator currentEval;
        Evaluator currentEval2;
        this.tq.consumeWhitespace();
        Evaluator newEval = parse(consumeSubQuery());
        boolean replaceRightMost = false;
        if (this.evals.size() == 1) {
            rootEval = (Evaluator) this.evals.get(0);
            currentEval = rootEval;
            if ((rootEval instanceof Or) && combinator != ',') {
                currentEval = ((Or) currentEval).rightMostEvaluator();
                replaceRightMost = true;
            }
        } else {
            rootEval = new And(this.evals);
            currentEval = rootEval;
        }
        this.evals.clear();
        if (combinator == Typography.greater) {
            currentEval2 = new And(newEval, new ImmediateParent(currentEval));
        } else if (combinator == ' ') {
            currentEval2 = new And(newEval, new Parent(currentEval));
        } else if (combinator == '+') {
            currentEval2 = new And(newEval, new ImmediatePreviousSibling(currentEval));
        } else if (combinator == '~') {
            currentEval2 = new And(newEval, new PreviousSibling(currentEval));
        } else if (combinator != ',') {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unknown combinator: ");
            stringBuilder.append(combinator);
            throw new SelectorParseException(stringBuilder.toString(), new Object[0]);
        } else if (currentEval instanceof Or) {
            currentEval2 = (Or) currentEval;
            currentEval2.add(newEval);
        } else {
            currentEval2 = new Or();
            currentEval2.add(currentEval);
            currentEval2.add(newEval);
        }
        if (replaceRightMost) {
            ((Or) rootEval).replaceRightMostEvaluator(currentEval2);
        } else {
            rootEval = currentEval2;
        }
        this.evals.add(rootEval);
    }

    private String consumeSubQuery() {
        StringBuilder sq = new StringBuilder();
        while (!this.tq.isEmpty()) {
            if (this.tq.matches("(")) {
                sq.append("(");
                sq.append(this.tq.chompBalanced('(', ')'));
                sq.append(")");
            } else if (this.tq.matches("[")) {
                sq.append("[");
                sq.append(this.tq.chompBalanced('[', ']'));
                sq.append("]");
            } else if (this.tq.matchesAny(combinators)) {
                break;
            } else {
                sq.append(this.tq.consume());
            }
        }
        return sq.toString();
    }

    private void findElements() {
        if (this.tq.matchChomp("#")) {
            byId();
        } else if (this.tq.matchChomp(".")) {
            byClass();
        } else {
            if (!this.tq.matchesWord()) {
                if (!this.tq.matches("*|")) {
                    if (this.tq.matches("[")) {
                        byAttribute();
                        return;
                    } else if (this.tq.matchChomp("*")) {
                        allElements();
                        return;
                    } else if (this.tq.matchChomp(":lt(")) {
                        indexLessThan();
                        return;
                    } else if (this.tq.matchChomp(":gt(")) {
                        indexGreaterThan();
                        return;
                    } else if (this.tq.matchChomp(":eq(")) {
                        indexEquals();
                        return;
                    } else if (this.tq.matches(":has(")) {
                        has();
                        return;
                    } else if (this.tq.matches(":contains(")) {
                        contains(false);
                        return;
                    } else if (this.tq.matches(":containsOwn(")) {
                        contains(true);
                        return;
                    } else if (this.tq.matches(":containsData(")) {
                        containsData();
                        return;
                    } else if (this.tq.matches(":matches(")) {
                        matches(false);
                        return;
                    } else if (this.tq.matches(":matchesOwn(")) {
                        matches(true);
                        return;
                    } else if (this.tq.matches(":not(")) {
                        not();
                        return;
                    } else if (this.tq.matchChomp(":nth-child(")) {
                        cssNthChild(false, false);
                        return;
                    } else if (this.tq.matchChomp(":nth-last-child(")) {
                        cssNthChild(true, false);
                        return;
                    } else if (this.tq.matchChomp(":nth-of-type(")) {
                        cssNthChild(false, true);
                        return;
                    } else if (this.tq.matchChomp(":nth-last-of-type(")) {
                        cssNthChild(true, true);
                        return;
                    } else if (this.tq.matchChomp(":first-child")) {
                        this.evals.add(new IsFirstChild());
                        return;
                    } else if (this.tq.matchChomp(":last-child")) {
                        this.evals.add(new IsLastChild());
                        return;
                    } else if (this.tq.matchChomp(":first-of-type")) {
                        this.evals.add(new IsFirstOfType());
                        return;
                    } else if (this.tq.matchChomp(":last-of-type")) {
                        this.evals.add(new IsLastOfType());
                        return;
                    } else if (this.tq.matchChomp(":only-child")) {
                        this.evals.add(new IsOnlyChild());
                        return;
                    } else if (this.tq.matchChomp(":only-of-type")) {
                        this.evals.add(new IsOnlyOfType());
                        return;
                    } else if (this.tq.matchChomp(":empty")) {
                        this.evals.add(new IsEmpty());
                        return;
                    } else if (this.tq.matchChomp(":root")) {
                        this.evals.add(new IsRoot());
                        return;
                    } else if (this.tq.matchChomp(":matchText")) {
                        this.evals.add(new MatchText());
                        return;
                    } else {
                        throw new SelectorParseException("Could not parse query '%s': unexpected token at '%s'", this.query, this.tq.remainder());
                    }
                }
            }
            byTag();
        }
    }

    private void byId() {
        String id = this.tq.consumeCssIdentifier();
        Validate.notEmpty(id);
        this.evals.add(new Id(id));
    }

    private void byClass() {
        String className = this.tq.consumeCssIdentifier();
        Validate.notEmpty(className);
        this.evals.add(new Class(className.trim()));
    }

    private void byTag() {
        String tagName = this.tq.consumeElementSelector();
        Validate.notEmpty(tagName);
        if (tagName.startsWith("*|")) {
            this.evals.add(new Or(new Tag(Normalizer.normalize(tagName)), new TagEndsWith(Normalizer.normalize(tagName.replace("*|", ":")))));
            return;
        }
        if (tagName.contains("|")) {
            tagName = tagName.replace("|", ":");
        }
        this.evals.add(new Tag(tagName.trim()));
    }

    private void byAttribute() {
        TokenQueue cq = new TokenQueue(this.tq.chompBalanced('[', ']'));
        String key = cq.consumeToAny(AttributeEvals);
        Validate.notEmpty(key);
        cq.consumeWhitespace();
        if (cq.isEmpty()) {
            if (key.startsWith("^")) {
                this.evals.add(new AttributeStarting(key.substring(1)));
            } else {
                this.evals.add(new Attribute(key));
            }
        } else if (cq.matchChomp("=")) {
            this.evals.add(new AttributeWithValue(key, cq.remainder()));
        } else if (cq.matchChomp("!=")) {
            this.evals.add(new AttributeWithValueNot(key, cq.remainder()));
        } else if (cq.matchChomp("^=")) {
            this.evals.add(new AttributeWithValueStarting(key, cq.remainder()));
        } else if (cq.matchChomp("$=")) {
            this.evals.add(new AttributeWithValueEnding(key, cq.remainder()));
        } else if (cq.matchChomp("*=")) {
            this.evals.add(new AttributeWithValueContaining(key, cq.remainder()));
        } else if (cq.matchChomp("~=")) {
            this.evals.add(new AttributeWithValueMatching(key, Pattern.compile(cq.remainder())));
        } else {
            throw new SelectorParseException("Could not parse attribute query '%s': unexpected token at '%s'", this.query, cq.remainder());
        }
    }

    private void allElements() {
        this.evals.add(new AllElements());
    }

    private void indexLessThan() {
        this.evals.add(new IndexLessThan(consumeIndex()));
    }

    private void indexGreaterThan() {
        this.evals.add(new IndexGreaterThan(consumeIndex()));
    }

    private void indexEquals() {
        this.evals.add(new IndexEquals(consumeIndex()));
    }

    private void cssNthChild(boolean backwards, boolean ofType) {
        int a;
        int b;
        String argS = Normalizer.normalize(this.tq.chompTo(")"));
        Matcher mAB = NTH_AB.matcher(argS);
        Matcher mB = NTH_B.matcher(argS);
        if ("odd".equals(argS)) {
            a = 2;
            b = 1;
        } else if ("even".equals(argS)) {
            a = 2;
            b = 0;
        } else {
            b = 0;
            int i = 1;
            if (mAB.matches()) {
                if (mAB.group(3) != null) {
                    i = Integer.parseInt(mAB.group(1).replaceFirst("^\\+", ""));
                }
                a = i;
                if (mAB.group(4) != null) {
                    b = Integer.parseInt(mAB.group(4).replaceFirst("^\\+", ""));
                }
            } else if (mB.matches()) {
                a = 0;
                b = Integer.parseInt(mB.group().replaceFirst("^\\+", ""));
            } else {
                throw new SelectorParseException("Could not parse nth-index '%s': unexpected format", argS);
            }
        }
        if (ofType) {
            if (backwards) {
                this.evals.add(new IsNthLastOfType(a, b));
            } else {
                this.evals.add(new IsNthOfType(a, b));
            }
        } else if (backwards) {
            this.evals.add(new IsNthLastChild(a, b));
        } else {
            this.evals.add(new IsNthChild(a, b));
        }
    }

    private int consumeIndex() {
        String indexS = this.tq.chompTo(")").trim();
        Validate.isTrue(StringUtil.isNumeric(indexS), "Index must be numeric");
        return Integer.parseInt(indexS);
    }

    private void has() {
        this.tq.consume(":has");
        String subQuery = this.tq.chompBalanced('(', ')');
        Validate.notEmpty(subQuery, ":has(el) subselect must not be empty");
        this.evals.add(new Has(parse(subQuery)));
    }

    private void contains(boolean own) {
        this.tq.consume(own ? ":containsOwn" : ":contains");
        String searchText = TokenQueue.unescape(this.tq.chompBalanced('(', ')'));
        Validate.notEmpty(searchText, ":contains(text) query must not be empty");
        if (own) {
            this.evals.add(new ContainsOwnText(searchText));
        } else {
            this.evals.add(new ContainsText(searchText));
        }
    }

    private void containsData() {
        this.tq.consume(":containsData");
        String searchText = TokenQueue.unescape(this.tq.chompBalanced('(', ')'));
        Validate.notEmpty(searchText, ":containsData(text) query must not be empty");
        this.evals.add(new ContainsData(searchText));
    }

    private void matches(boolean own) {
        this.tq.consume(own ? ":matchesOwn" : ":matches");
        String regex = this.tq.chompBalanced('(', ')');
        Validate.notEmpty(regex, ":matches(regex) query must not be empty");
        if (own) {
            this.evals.add(new MatchesOwn(Pattern.compile(regex)));
        } else {
            this.evals.add(new Matches(Pattern.compile(regex)));
        }
    }

    private void not() {
        this.tq.consume(":not");
        String subQuery = this.tq.chompBalanced('(', ')');
        Validate.notEmpty(subQuery, ":not(selector) subselect must not be empty");
        this.evals.add(new Not(parse(subQuery)));
    }
}
