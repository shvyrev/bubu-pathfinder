package mt.com.go.rule.engine.rules;

import java.util.Comparator;

public class RulePriorityComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        Rule r1 = (Rule) o1;
        Rule r2 = (Rule) o2;

        if (r1.getPriority() == 0) {
            return 1;
        }
        if (r2.getPriority() == 0) {
            return -1;
        }

        return r2.getPriority() - r1.getPriority();

    }
}
