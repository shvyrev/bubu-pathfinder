package mt.com.go.decision.engine.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;
import mt.com.go.decision.engine.DecisionEngine;
import mt.com.go.decision.engine.DecisionEngineRequest;
import mt.com.go.decision.engine.DecisionEngineResponse;
import mt.com.go.decision.engine.exception.DecisionEngineException;

@WebService(name = "DecisionEngineWS")
public class DecisionEngineWS {

    @WebMethod(operationName = "doDecision")
    public DecisionEngineResponse doDecision(DecisionEngineRequest request) throws DecisionEngineException {

        DecisionEngine decisionEngine = new DecisionEngine();
        return decisionEngine.doDecision(request);
        
    }
    

}
