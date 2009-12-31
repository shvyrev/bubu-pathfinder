package mt.com.go.rule.engine.rules.bean;

import java.util.ArrayList;
import mt.com.go.rule.engine.rules.RuleLoader;
import mt.com.go.rule.engine.rules.RuleSet;

public class RulesBean {

    private ArrayList<RuleSet> rules = null;

    public RulesBean() {

        RuleLoader ruleLoader = new RuleLoader();
        rules = ruleLoader.loadRules(false);

    }

    public ArrayList<RuleSet> getRules() {
        return rules;
    }

    public void setRules(ArrayList<RuleSet> rules) {
        this.rules = rules;
    }


}
