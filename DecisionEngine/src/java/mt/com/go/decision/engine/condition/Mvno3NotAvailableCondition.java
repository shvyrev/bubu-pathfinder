package mt.com.go.decision.engine.condition;

import mt.com.go.decision.engine.DecisionEngineRequest;
import mt.com.go.decision.engine.DecisionEngineResponse;
import mt.com.go.decision.engine.condition.iface.ICondition;

public class Mvno3NotAvailableCondition implements ICondition {

    public boolean doCondition(DecisionEngineRequest request, DecisionEngineResponse response) {

        String imsi = "";
        imsi = response.getParameters().get("imsi");

        if (imsi != null) {
            return imsi.matches("500D\\d{5}");
        } else {
            return false;
        }
    }

}
