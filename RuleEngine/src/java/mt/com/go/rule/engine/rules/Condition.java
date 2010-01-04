package mt.com.go.rule.engine.rules;

public class Condition {

    private int conditionId;
    private String description;
    private String parameterName;
    private String expression;
    private String conditionClass;


    public Condition() {
    }

    public Condition(int conditionId, String description, String attributeName, String expression, String conditionClass) {
        this.conditionId = conditionId;
        this.description = description;
        this.parameterName = attributeName;
        this.expression = expression;
        this.conditionClass = conditionClass;
    }

    public String toString() {

        StringBuffer buffer = new StringBuffer();
        buffer.append(this.getClass().getSimpleName()).append("={");
        buffer.append("conditionId=").append(conditionId);
        buffer.append(", description=").append(description);
        buffer.append(", parameterName=").append(parameterName);
        buffer.append(", expression=").append(expression);
        buffer.append(", conditionClass=").append(conditionClass);
        buffer.append("}");

        return buffer.toString();

    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }
    
    public String getConditionClass() {
        return conditionClass;
    }

    public void setConditionClass(String conditionClass) {
        this.conditionClass = conditionClass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public int getConditionId() {
        return conditionId;
    }

    public void setConditionId(int conditionId) {
        this.conditionId = conditionId;
    }



    

}
