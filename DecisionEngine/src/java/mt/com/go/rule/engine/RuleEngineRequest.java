package mt.com.go.rule.engine;

import java.util.Hashtable;

public class RuleEngineRequest {

    private Hashtable<String, String> parameters = new Hashtable<String, String>();
    private String decisionType;

    public RuleEngineRequest() {
    }

    public Hashtable<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Hashtable<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getDecisionType() {
        return decisionType;
    }

    public void setDecisionType(String decisionType) {
        this.decisionType = decisionType;
    }

    @Override
    public String toString() {

        StringBuffer buffer = new StringBuffer();
        buffer.append(this.getClass().getSimpleName()).append("={");
        buffer.append("decisionType=").append(decisionType);
        buffer.append(", parameters=").append(parameters);
        buffer.append("}");

        return buffer.toString();

    }

}
