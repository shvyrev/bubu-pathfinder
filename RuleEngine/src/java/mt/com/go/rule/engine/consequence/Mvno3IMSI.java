package mt.com.go.rule.engine.consequence;

import mt.com.go.rule.engine.RuleEngineRequest;
import mt.com.go.rule.engine.RuleEngineResponse;
import mt.com.go.rule.engine.consequence.iface.IConsequence;

public class Mvno3IMSI implements IConsequence {

    public void doConsequence(RuleEngineRequest request, RuleEngineResponse response) {

        response.getMessages().add("MVNO 3 not yet available");

    }

}
