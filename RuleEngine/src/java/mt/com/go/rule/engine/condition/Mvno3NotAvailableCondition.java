package mt.com.go.rule.engine.condition;

import mt.com.go.rule.engine.RuleEngineRequest;
import mt.com.go.rule.engine.RuleEngineResponse;
import mt.com.go.rule.engine.condition.iface.ICondition;

public class Mvno3NotAvailableCondition implements ICondition {

    public boolean doCondition(RuleEngineRequest request, RuleEngineResponse response) {

        String imsi = "";
        imsi = response.getParameters().get("imsi");

        if (imsi != null) {
            return imsi.matches("500D\\d{5}");
        } else {
            return false;
        }
    }

}
