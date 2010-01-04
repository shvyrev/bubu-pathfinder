package mt.com.go.rule.engine.rules;

import java.util.ArrayList;
import mt.com.go.rule.engine.enums.RuleEngineLogicalOperator;

public class Rule {

    private int ruleId;
    private String name;
    private String description;
    private String consequence;
    private Integer priority;
    private RuleEngineLogicalOperator logicalOperator;
    private ArrayList<Condition> conditionList;

    public Rule() {
    }

    public Rule(int ruleId, String name, String description, String consequence, Integer priority, RuleEngineLogicalOperator logicalOperator, ArrayList<Condition> conditionList) {
        this.ruleId = ruleId;
        this.name = name;
        this.description = description;
        this.consequence = consequence;
        this.priority = priority;
        this.logicalOperator = logicalOperator;
        this.conditionList = conditionList;
    }

    public Rule getClone() {
        
        if (conditionList != null) {
            return new Rule(ruleId, name, description, consequence, priority, logicalOperator, (ArrayList<Condition>) conditionList.clone());
        } else {
            return new Rule(ruleId, name, description, consequence, priority, logicalOperator, conditionList);
        }
    }

    @Override
    public String toString() {

        StringBuffer buffer = new StringBuffer();
        buffer.append(this.getClass().getSimpleName()).append("={");
        buffer.append("ruleId=").append(ruleId);
        buffer.append(", name=").append(name);
        buffer.append(", description=").append(description);
        buffer.append(", consequence=").append(consequence);
        buffer.append(", priority=").append(priority);
        buffer.append(", logicalOperator=").append(logicalOperator);
        buffer.append(", conditionList=").append(conditionList);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }
    


}
