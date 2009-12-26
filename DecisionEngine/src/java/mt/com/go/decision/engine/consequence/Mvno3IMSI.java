package mt.com.go.decision.engine.consequence;

import mt.com.go.decision.engine.DecisionEngineRequest;
import mt.com.go.decision.engine.DecisionEngineResponse;
import mt.com.go.decision.engine.consequence.iface.IConsequence;

public class Mvno3IMSI implements IConsequence {

    public void doConsequence(DecisionEngineRequest request, DecisionEngineResponse response) {

        response.setSuccessful(false);
        response.getMessages().add("MVNO 3 not yet available");

    }

}
