package mt.com.go.decision.engine.consequence;

import mt.com.go.decision.engine.consequence.iface.IConsequence;
import mt.com.go.decision.engine.DecisionEngineRequest;
import mt.com.go.decision.engine.DecisionEngineResponse;
import mt.com.go.decision.engine.logging.DecisionEngineLogger;

public class IMSIFinder implements IConsequence {

    public void doConsequence(DecisionEngineRequest request, DecisionEngineResponse response) {

        if (response.getParameters() != null && !response.getParameters().containsKey("IMSI")) {

            String msisdn = request.getParameters().get("msisdn");

            if (msisdn != null && msisdn.trim().length() > 0) {

                String newImsi = "500" + msisdn.substring(2,3) + "00001";
                DecisionEngineLogger.logDebug(this, "IMSI found for msisdn=" + msisdn + ", IMSI=" + newImsi);
                response.getParameters().put("imsi", newImsi);

            } 

        }

    }
    
}
