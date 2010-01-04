package mt.com.go.rule.engine.rules;

import java.util.ArrayList;

public class RuleSet {

    private int ruleSetId;
    private String ruleSetName;
    private ArrayList<Rule> rules;

    public RuleSet() {
    }

    public RuleSet(int ruleSetId, String ruleSetName, ArrayList<Rule> rules) {
        this.ruleSetId = ruleSetId;
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

    public int getRuleSetId() {
        return ruleSetId;
    }

    public void setRuleSetId(int ruleSetId) {
        this.ruleSetId = ruleSetId;
    }

    

}
