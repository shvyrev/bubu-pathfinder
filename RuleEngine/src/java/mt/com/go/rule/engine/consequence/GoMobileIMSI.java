package mt.com.go.rule.engine.consequence;

import mt.com.go.rule.engine.consequence.iface.IConsequence;
import mt.com.go.rule.engine.RuleEngineRequest;
import mt.com.go.rule.engine.RuleEngineResponse;

public class GoMobileIMSI implements IConsequence {

    public void doConsequence(RuleEngineRequest request, RuleEngineResponse response) {

        if (response.getParameters() != null && !response.getParameters().containsKey("operatorCode")) {
            response.getParameters().put("operatorCode", "GO");
        }

    }

}
