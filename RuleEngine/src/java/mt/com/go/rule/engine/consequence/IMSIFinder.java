package mt.com.go.rule.engine.consequence;

import mt.com.go.rule.engine.consequence.iface.IConsequence;
import mt.com.go.rule.engine.RuleEngineRequest;
import mt.com.go.rule.engine.RuleEngineResponse;
import mt.com.go.rule.engine.logging.RuleEngineLogger;

public class IMSIFinder implements IConsequence {

    public void doConsequence(RuleEngineRequest request, RuleEngineResponse response) {

        if (response.getParameters() != null && !response.getParameters().containsKey("IMSI")) {

            String msisdn = request.getParameters().get("msisdn");

            if (msisdn != null && msisdn.trim().length() > 0) {

                String newImsi = "500" + msisdn.substring(2,3) + "00001";
                RuleEngineLogger.logDebug(this, "IMSI found for msisdn=" + msisdn + ", IMSI=" + newImsi);
                response.getParameters().put("imsi", newImsi);

            } 

        }

    }
    
}
