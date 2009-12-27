package mt.com.go.rule.engine.rules;

import java.util.ArrayList;
import java.util.HashMap;
import mt.com.go.rule.engine.enums.RuleEngineLogicalOperator;
import mt.com.go.rule.engine.logging.RuleEngineLogger;

public class RuleLoader {

    private static HashMap<String, ArrayList<Rule>> rules = null;

    public RuleLoader() {

    }

    public HashMap<String, ArrayList<Rule>> loadRules() {

        if (rules != null) {
            return rules;
        }

        rules = new HashMap<String, ArrayList<Rule>>();

        RuleEngineLogger.logDebug(this, "Loading Rules...");

        ArrayList<Rule> goMvnoDecision = new ArrayList<Rule>();

        Rule parameterChecksRule = new Rule (
                "Parameter Checkup",
                "Either IMSI or MSISDN have to be specified",
                "SET-UNSUCCESSFUL|ADD-MESSAGE Either IMSI or MSISDN have to be specified",
                1,
                RuleEngineLogicalOperator.AND,
                new ArrayList<Condition>());

        Rule imsiFinderRule = new Rule(
                "IMSI Finder",
                "Find IMSI from the MSISDN parameter",
                "RUN mt.com.go.decision.engine.consequence.IMSIFinder|SET-PARAM notes=Imsi found from msisdn|RESUBMIT 5",
                2,
                RuleEngineLogicalOperator.AND,
                new ArrayList<Condition>());

        Rule goMobileIMSIRule = new Rule(
                "IMSI",
                "Mark the request as GO",
                "RUN mt.com.go.decision.engine.consequence.GoMobileIMSI",
                3,
                RuleEngineLogicalOperator.AND,
                new ArrayList<Condition>());

        Rule mvno1IMSIRule = new Rule(
                "IMSI",
                "Mark the request as MVNO 1",
                "RUN mt.com.go.decision.engine.consequence.Mvno1IMSI",
                4,
                RuleEngineLogicalOperator.AND,
                new ArrayList<Condition>());

        Rule mvno2IMSIRule = new Rule(
                "IMSI",
                "Mark the request as MVNO 2",
                "RUN mt.com.go.decision.engine.consequence.Mvno2IMSI",
                5,
                RuleEngineLogicalOperator.AND,
                new ArrayList<Condition>());

        Rule mvno3IMSIRule = new Rule(
                "IMSI",
                "Warn that MVNO 3 is not yet available",
                "RUN mt.com.go.decision.engine.consequence.Mvno3IMSI",
                6,
                RuleEngineLogicalOperator.OR,
                new ArrayList<Condition>());

        Rule operatorCodeNotDerived = new Rule (
                "Operator N/A",
                "The operator code was not derived",
                "SET-UNSUCCESSFUL|ADD-MESSAGE The operator code was not derived",
                7,
                RuleEngineLogicalOperator.AND,
                new ArrayList<Condition>());

        Condition imsiNotInParamsCondition = new Condition("IMSI not in Parameter list", "imsi", "!exists",null);
        Condition msisdnNotInParamsCondition = new Condition("MSISDN not in Parameter list", "msisdn", "!exists",null);
        Condition resubmitNotInParamsCondition = new Condition("RESUBMIT not in Parameter list", "resubmit", "!exists", null);
        Condition goMobileIMSICondition = new Condition("GO Mobile IMSI Condition", "imsi", "500A\\d{5}", null);
        Condition mvno1IMSICondition = new Condition("MVNO 1 IMSI Condition", "imsi", "500B\\d{5}", null);
        Condition mvno2IMSICondition = new Condition("MVNO 2 IMSI Condition", "imsi", "500C\\d{5}", null);
        Condition mvno3IMSICondition = new Condition("MVNO 3 IMSI Condition", null, null, "mt.com.go.decision.engine.condition.Mvno3NotAvailableCondition");
        Condition alwaysTrue = new Condition("True", null, null,"mt.com.go.decision.engine.condition.TrueCondition");

        parameterChecksRule.getConditionList().add(imsiNotInParamsCondition);
        parameterChecksRule.getConditionList().add(msisdnNotInParamsCondition);
        imsiFinderRule.getConditionList().add(imsiNotInParamsCondition);
        imsiFinderRule.getConditionList().add(resubmitNotInParamsCondition);
        goMobileIMSIRule.getConditionList().add(goMobileIMSICondition);
        mvno1IMSIRule.getConditionList().add(mvno1IMSICondition);
        mvno2IMSIRule.getConditionList().add(mvno2IMSICondition);
        mvno3IMSIRule.getConditionList().add(mvno3IMSICondition);
        operatorCodeNotDerived.getConditionList().add(alwaysTrue);

        goMvnoDecision.add(parameterChecksRule);
        goMvnoDecision.add(imsiFinderRule);
        goMvnoDecision.add(goMobileIMSIRule);
        goMvnoDecision.add(mvno1IMSIRule);
        goMvnoDecision.add(mvno2IMSIRule);
        goMvnoDecision.add(mvno3IMSIRule);
        goMvnoDecision.add(operatorCodeNotDerived);
        
        rules.put("GO MVNO Decision", goMvnoDecision);

        Rule testRule1 = new Rule(
                "Test Rule 1",
                "Test !exists in a condition",
                "SET-PARAM notes=parameter does not exist|ADD-MESSAGE hello|RESUBMIT 10",
                1,
                RuleEngineLogicalOperator.AND,
                new ArrayList<Condition>());

        Condition condition1TestRule1 = new Condition("!exists in a condition", "testParameter", "!exists", null);
        testRule1.getConditionList().add(condition1TestRule1);
        ArrayList<Rule> notExistsTest = new ArrayList<Rule>();
        notExistsTest.add(testRule1);

        rules.put("Not exists test", notExistsTest);

        logRules(rules);

        RuleEngineLogger.logDebug(this, "Finished Loading Rules...");

        return rules;

    }

    private void logRules(HashMap <String, ArrayList<Rule>> rules) {

        for (Object currentKey : rules.keySet().toArray()) {

            ArrayList<Rule> temp = rules.get((String)currentKey);

            for (Rule currentRule : temp) {

                RuleEngineLogger.logDebug(this, "Loaded Rule : " + currentRule.toString());

            }

        }

    }

}
