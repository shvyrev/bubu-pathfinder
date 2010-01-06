package mt.com.go.rule.engine.rules;

import java.util.ArrayList;
import java.util.LinkedList;
import mt.com.go.rule.engine.enums.RuleEngineLogicalOperator;


public class Rule {

    private Integer ruleId;
    private Integer parentRuleId;
    private String name;
    private String code;
    private String consequence;
    private Integer priority;
    private RuleEngineLogicalOperator logicalOperator;
    private ArrayList<Condition> conditionList;
    private LinkedList<Rule> childRules;

    public Rule() {
    }

    public Rule(Integer ruleId, Integer parentRuleId, String name, String code, String consequence, Integer priority, RuleEngineLogicalOperator logicalOperator, ArrayList<Condition> conditionList, LinkedList<Rule> childRules) {
        this.ruleId = ruleId;
        this.parentRuleId = parentRuleId;
        this.name = name;
        this.code = code;
        this.consequence = consequence;
        this.priority = priority;
        this.logicalOperator = logicalOperator;
        this.conditionList = conditionList;
        this.childRules = childRules;
    }

  public String toString() {

        StringBuffer buffer = new StringBuffer();
        buffer.append(this.getClass().getSimpleName()).append("={");
        buffer.append("ruleId=").append(ruleId);
        buffer.append(", parentRuleId=").append(parentRuleId);
        buffer.append(", name=").append(name);
        buffer.append(", code=").append(code);
        buffer.append(", consequence=").append(consequence);
        buffer.append(", priority=").append(priority);
        buffer.append(", logicalOperator=").append(logicalOperator);
        buffer.append(", conditionList=").append(conditionList);
        buffer.append(", childRules=").append(childRules);
        buffer.append("}");

        return buffer.toString();

    }


    public ArrayList<Condition> getConditionList() {
        return conditionList;
    }

    public void setConditionList(ArrayList<Condition> conditionList) {
        this.conditionList = conditionList;
    }

    public String getConsequence() {
        return consequence;
    }

    public void setConsequence(String consequence) {
        this.consequence = consequence;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public RuleEngineLogicalOperator getLogicalOperator() {
        return logicalOperator;
    }

    public void setLogicalOperator(RuleEngineLogicalOperator logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public LinkedList<Rule> getChildRules() {
        return childRules;
    }

    public void setChildRules(LinkedList<Rule> childRules) {
        this.childRules = childRules;
    }

    public Integer getParentRuleId() {
        return parentRuleId;
    }

    public void setParentRuleId(Integer parentRuleId) {
        this.parentRuleId = parentRuleId;
    }

    

}
