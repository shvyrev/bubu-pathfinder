package mt.com.go.rule.engine.rules;

import java.util.ArrayList;

public class RuleSet {

    private String ruleSetName;
    private ArrayList<Rule> rules;

    public RuleSet() {
    }

    public RuleSet(String ruleSetName, ArrayList<Rule> rules) {
        this.ruleSetName = ruleSetName;
        this.rules = rules;
    }

    public String getRuleSetName() {
        return ruleSetName;
    }

    public void setRuleSetName(String ruleSetName) {
        this.ruleSetName = ruleSetName;
    }

    public ArrayList<Rule> getRules() {
        return rules;
    }

    public void setRules(ArrayList<Rule> rules) {
        this.rules = rules;
    }

}
