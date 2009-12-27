package mt.com.go.rule.engine.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;
import mt.com.go.rule.engine.RuleEngine;
import mt.com.go.rule.engine.RuleEngineRequest;
import mt.com.go.rule.engine.RuleEngineResponse;
import mt.com.go.rule.engine.exception.RuleEngineException;

@WebService(name = "DecisionEngineWS")
public class RuleEngineWS {

    @WebMethod(operationName = "doDecision")
    public RuleEngineResponse doDecision(RuleEngineRequest request) throws RuleEngineException {

        RuleEngine decisionEngine = new RuleEngine();
        return decisionEngine.doDecision(request);
        
    }
    

}
