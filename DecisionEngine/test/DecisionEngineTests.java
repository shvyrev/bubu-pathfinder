
import junit.framework.*;
import mt.com.go.decision.engine.DecisionEngine;
import mt.com.go.decision.engine.DecisionEngineRequest;
import mt.com.go.decision.engine.DecisionEngineResponse;

public class DecisionEngineTests extends TestCase {

    public DecisionEngineTests() {
    }

    public void testGoMvnoDecision() {

        DecisionEngineResponse response = null;

        try {
            DecisionEngine engine = new DecisionEngine();
            DecisionEngineRequest request = new DecisionEngineRequest();
            request.setDecisionType("GO MVNO Decision");
            request.getParameters().put("msisdn", "500A00001");

            response = engine.doDecision(request);

            assertTrue("Engine returned successful", response.isSuccessful());
            assertTrue("Response contains imsi parameter", response.getParameters().containsKey("imsi"));
            assertTrue("Imsi value is populated", response.getParameters().get("imsi") != null && response.getParameters().get("imsi").trim().length() > 0);

        } catch (Exception ex) {

            assertTrue("Engine failure", false);
            ex.printStackTrace();

        }

    }
    
    public void test1() {

        DecisionEngineResponse response = null;
        
        try {
            
            DecisionEngine engine = new DecisionEngine();
            DecisionEngineRequest request = new DecisionEngineRequest();
            request.setDecisionType("Not exists test");

            response = engine.doDecision(request);

        } catch (Exception ex) {

            assertTrue("Engine failure", false);
            ex.printStackTrace();

        }        
        
    }



}

