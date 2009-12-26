package mt.com.go.decision.engine.consequence;

import mt.com.go.decision.engine.consequence.iface.IConsequence;
import mt.com.go.decision.engine.DecisionEngineRequest;
import mt.com.go.decision.engine.DecisionEngineResponse;

public class Mvno1IMSI implements IConsequence {

    public void doConsequence(DecisionEngineRequest request, DecisionEngineResponse response) {

        if (response.getParameters() != null && !response.getParameters().containsKey("operatorCode")) {
            response.getParameters().put("operatorCode", "Mvno1");
        }

    }

}
