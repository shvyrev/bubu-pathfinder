
import junit.framework.*;
import mt.com.go.rule.engine.RuleEngine;
import mt.com.go.rule.engine.RuleEngineRequest;
import mt.com.go.rule.engine.RuleEngineResponse;

public class DecisionEngineTests extends TestCase {

    public DecisionEngineTests() {
    }

    public void testGoMvnoDecision() {

        RuleEngineResponse response = null;

        try {
            RuleEngine engine = new RuleEngine();
            RuleEngineRequest request = new RuleEngineRequest();
            request.setDecisionType("GO MVNO Decision");
            request.getParameters().put("msisdn", "500A00001");

            response = engine.executeRule(request);

            assertTrue("Engine returned successful", response.isSuccessful());
            assertTrue("Response contains imsi parameter", response.getParameters().containsKey("imsi"));
            assertTrue("Imsi value is populated", response.getParameters().get("imsi") != null && response.getParameters().get("imsi").trim().length() > 0);

        } catch (Exception ex) {

            assertTrue("Engine failure", false);
            ex.printStackTrace();

        }

    }
    
    public void test1() {

        RuleEngineResponse response = null;
        
        try {
            
            RuleEngine engine = new RuleEngine();
            RuleEngineRequest request = new RuleEngineRequest();
            request.setDecisionType("Not exists test");

            response = engine.executeRule(request);

        } catch (Exception ex) {

            assertTrue("Engine failure", false);
            ex.printStackTrace();

        }        
        
    }



}

