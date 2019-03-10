package org.jsoup.select;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Element;

abstract class CombiningEvaluator extends Evaluator {
    final ArrayList<Evaluator> evaluators;
    int num;

    static final class And extends CombiningEvaluator {
        And(Collection<Evaluator> evaluators) {
            super(evaluators);
        }

        And(Evaluator... evaluators) {
            this(Arrays.asList(evaluators));
        }

        public boolean matches(Element root, Element node) {
            for (int i = 0; i < this.num; i++) {
                if (!((Evaluator) this.evaluators.get(i)).matches(root, node)) {
                    return false;
                }
            }
            return true;
        }

        public String toString() {
            return StringUtil.join(this.evaluators, StringUtils.SPACE);
        }
    }

    static final class Or extends CombiningEvaluator {
        Or(Collection<Evaluator> evaluators) {
            if (this.num > 1) {
                this.evaluators.add(new And((Collection) evaluators));
            } else {
                this.evaluators.addAll(evaluators);
            }
            updateNumEvaluators();
        }

        Or(Evaluator... evaluators) {
            this(Arrays.asList(evaluators));
        }

        Or() {
        }

        public void add(Evaluator e) {
            this.evaluators.add(e);
            updateNumEvaluators();
        }

        public boolean matches(Element root, Element node) {
            for (int i = 0; i < this.num; i++) {
                if (((Evaluator) this.evaluators.get(i)).matches(root, node)) {
                    return true;
                }
            }
            return false;
        }

        public String toString() {
            return StringUtil.join(this.evaluators, ", ");
        }
    }

    CombiningEvaluator() {
        this.num = 0;
        this.evaluators = new ArrayList();
    }

    CombiningEvaluator(Collection<Evaluator> evaluators) {
        this();
        this.evaluators.addAll(evaluators);
        updateNumEvaluators();
    }

    Evaluator rightMostEvaluator() {
        int i = this.num;
        return i > 0 ? (Evaluator) this.evaluators.get(i - 1) : null;
    }

    void replaceRightMostEvaluator(Evaluator replacement) {
        this.evaluators.set(this.num - 1, replacement);
    }

    void updateNumEvaluators() {
        this.num = this.evaluators.size();
    }
}
