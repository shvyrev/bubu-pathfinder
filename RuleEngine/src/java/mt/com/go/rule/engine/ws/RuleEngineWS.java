package mt.com.go.rule.engine.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;
import mt.com.go.rule.engine.RuleEngine;
import mt.com.go.rule.engine.RuleEngineRequest;
import mt.com.go.rule.engine.RuleEngineResponse;
import mt.com.go.rule.engine.exception.RuleEngineException;

@WebService(name = "RuleEngineWS")
public class RuleEngineWS {

    @WebMethod(operationName = "executeRule")
    public RuleEngineResponse executeRule(RuleEngineRequest request) throws RuleEngineException {

        RuleEngine ruleEngine = new RuleEngine();
        return ruleEngine.executeRule(request);
        
    }

    @WebMethod(operationName = "reloadRules")
    public void reloadRules() {

        RuleEngine ruleEngine = new RuleEngine();
        ruleEngine.reloadRules();
        
    }

}
