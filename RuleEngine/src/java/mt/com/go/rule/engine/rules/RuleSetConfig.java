package mt.com.go.rule.engine.rules;

public class RuleSetConfig {

    private Integer ruleId;
    private Integer priority;

    public RuleSetConfig() {
    }

    public RuleSetConfig(Integer ruleId, Integer priority) {
        this.ruleId = ruleId;
        this.priority = priority;
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


}
