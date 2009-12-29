package mt.com.go.rule.engine.rules;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import mt.com.go.rule.engine.enums.RuleEngineLogicalOperator;
import mt.com.go.rule.engine.logging.RuleEngineLogger;
//import org.apache.derby.jdbc.e

public class RuleLoader {

    private static HashMap<String, ArrayList<Rule>> rules = null;
    private static Connection dbConnection = null;

    public RuleLoader() {
    }

    private Connection getConnection() throws SQLException {

        if (dbConnection != null) {
            return dbConnection;
        }

        String DB_CONN_STRING = "jdbc:derby://localhost:1527/RuleEngineDB";
        String DRIVER_CLASS_NAME = "org.apache.derby.jdbc.ClientDriver";
        String USER_NAME = "rule_engine";
        String PASSWORD = "rule_engine";

        Connection con = null;
        try {
            Class.forName(DRIVER_CLASS_NAME).newInstance();
        } catch (Exception ex) {
            RuleEngineLogger.logDebug(this, "Cannot load db driver: " + DRIVER_CLASS_NAME, ex);
        }

        if (USER_NAME.trim().length() > 0) {
            con = DriverManager.getConnection(DB_CONN_STRING, USER_NAME, PASSWORD);
        } else {
            con = DriverManager.getConnection(DB_CONN_STRING);
        }

        if (con != null) {
            dbConnection = con;
        }

        return con;

    }

    public HashMap<String, ArrayList<Rule>> loadRules2() {

        if (rules != null) {
            return rules;
        }

        rules = new HashMap<String, ArrayList<Rule>>();

        RuleEngineLogger.logDebug(this, "Loading Rules...");

        Hashtable<Integer, String> ruleSetsTable = getRuleSets();
        Hashtable<Integer, Rule> rulesTable = getRules();
        Hashtable<Integer, Condition> conditionsTable = getConditions();

        for (Object currentRuleSet : ruleSetsTable.keySet().toArray()) {

            Integer key = (Integer) currentRuleSet;
            rules.put(ruleSetsTable.get(currentRuleSet), loadRuleSet(key, rulesTable, conditionsTable));

        }



        RuleEngineLogger.logDebug(this, "Finished Loading Rules...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(RuleLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

        return rules;
    }

    private ArrayList<Rule> loadRuleSet(Integer ruleSetId, Hashtable<Integer, Rule> rulesTable, Hashtable<Integer, Condition> conditionsTable) {

        ArrayList<Rule> ruleSet = new ArrayList<Rule>();

        try {
            Connection con = getConnection();
            PreparedStatement pstmt = con.prepareStatement("select rule_id, priority from rule_set_conf where rule_set_id = ? order by priority");
            pstmt.setInt(1, ruleSetId.intValue());
            ResultSet rs = pstmt.executeQuery();


            ArrayList<RuleSetConfig> ruleSetConfig = new ArrayList<RuleSetConfig>();

            while (rs.next()) {
                ruleSetConfig.add(new RuleSetConfig(new Integer(rs.getInt("rule_id")), new Integer(rs.getInt("priority"))));
            }

            int priorityLevel = 0;

            for (RuleSetConfig currentRuleSetConfig : ruleSetConfig) {
                Rule tempRule = rulesTable.get(currentRuleSetConfig.getRuleId());

                Rule rule = tempRule.getClone();
                priorityLevel++;
                rule.setPriority(new Integer(priorityLevel));

                ArrayList<Integer> conditionIdList = getConditionsForRule(currentRuleSetConfig.getRuleId());
                for (Integer currentConditionId : conditionIdList) {
                    if (rule.getConditionList() == null) {
                        rule.setConditionList(new ArrayList<Condition>());
                    }
                    rule.getConditionList().add(conditionsTable.get(currentConditionId));
                }
                ruleSet.add(rule);
                RuleEngineLogger.logDebug(this, rule.toString());
            }

        } catch (Exception ex) {
            RuleEngineLogger.logDebug(this, "Error while loading rule sets", ex);
        }

        return ruleSet;

    }

    private ArrayList<Integer> getConditionsForRule(Integer ruleId) {

        ArrayList<Integer> conditionIdList = new ArrayList<Integer>();

        try {

            Connection con = getConnection();
            PreparedStatement pstmt = con.prepareStatement("select condition_id from rule_condition_map where rule_id = ?");
            pstmt.setInt(1, ruleId.intValue());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                conditionIdList.add(new Integer(rs.getInt("condition_id")));
            }

        } catch (Exception ex) {
            RuleEngineLogger.logDebug(this, "Error while loading condions for rule " + ruleId, ex);
        }

        return conditionIdList;

    }

    private Hashtable<Integer, Condition> getConditions() {

        Hashtable<Integer, Condition> conditionsTable = new Hashtable<Integer, Condition>();

        try {
            Connection con = getConnection();
            PreparedStatement pstmt = con.prepareStatement("select condition_id, description, parameter_name, expression, condition_class from condition");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                Integer ruleId = new Integer(rs.getInt("condition_id"));
                Condition condition = new Condition();
                condition.setDescription(rs.getString("description"));
                condition.setParameterName(rs.getString("parameter_name"));
                condition.setExpression(rs.getString("expression"));
                condition.setConditionClass(rs.getString("condition_class"));
                conditionsTable.put(ruleId, condition);

                RuleEngineLogger.logDebug(this, "Loading conditions : " + condition);
            }

        } catch (Exception ex) {
            RuleEngineLogger.logDebug(this, "Error while loading conditions", ex);
        }

        return conditionsTable;

    }

    private Hashtable<Integer, Rule> getRules() {

        Hashtable<Integer, Rule> rulesTable = new Hashtable<Integer, Rule>();

        try {
            Connection con = getConnection();
            PreparedStatement pstmt = con.prepareStatement("select rule_id, name, description, consequence, logical_operator from rule");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                Integer ruleId = new Integer(rs.getInt("rule_id"));
                Rule rule = new Rule();
                rule.setName(rs.getString("name"));
                rule.setDescription(rs.getString("description"));
                rule.setConsequence(rs.getString("consequence"));
                String logicalOperator = rs.getString("logical_operator");
                if (logicalOperator.trim().equalsIgnoreCase("OR")) {
                    rule.setLogicalOperator(RuleEngineLogicalOperator.OR);
                } else if (logicalOperator.trim().equalsIgnoreCase("AND")) {
                    rule.setLogicalOperator(RuleEngineLogicalOperator.AND);
                }

                rulesTable.put(ruleId, rule);

                RuleEngineLogger.logDebug(this, "Loading rules : " + rule);
            }

        } catch (Exception ex) {
            RuleEngineLogger.logDebug(this, "Error while loading rules", ex);
        }

        return rulesTable;

    }

    private Hashtable<Integer, String> getRuleSets() {

        Hashtable<Integer, String> ruleSetsTable = new Hashtable<Integer, String>();
        try {
            Connection con = getConnection();
            PreparedStatement pstmt = con.prepareStatement("select rule_set_id, name from rule_set");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Integer ruleSetId = new Integer(rs.getInt("rule_set_id"));
                String ruleSetName = rs.getString("name");

                ruleSetsTable.put(ruleSetId, ruleSetName);
                RuleEngineLogger.logDebug(this, "Loading rule sets : " + ruleSetName);
            }

        } catch (Exception ex) {
            RuleEngineLogger.logDebug(this, "Error while loading rule sets", ex);
        }

        return ruleSetsTable;
    }

    public HashMap<String, ArrayList<Rule>> loadRules() {

        getRuleSets();


        if (rules != null) {
            return rules;
        }

        rules = new HashMap<String, ArrayList<Rule>>();

        RuleEngineLogger.logDebug(this, "Loading Rules...");

        ArrayList<Rule> goMvnoDecision = new ArrayList<Rule>();

        Rule parameterChecksRule = new Rule(
                "Parameter Checkup",
                "Either IMSI or MSISDN have to be specified",
                "SET-UNSUCCESSFUL|ADD-MESSAGE Either IMSI or MSISDN have to be specified",
                1,
                RuleEngineLogicalOperator.AND,
                new ArrayList<Condition>());

        Rule imsiFinderRule = new Rule(
                "IMSI Finder",
                "Find IMSI from the MSISDN parameter",
                "RUN mt.com.go.rule.engine.consequence.IMSIFinder|SET-PARAM notes=Imsi found from msisdn|RESUBMIT 5",
                2,
                RuleEngineLogicalOperator.AND,
                new ArrayList<Condition>());

        Rule goMobileIMSIRule = new Rule(
                "IMSI",
                "Mark the request as GO",
                "RUN mt.com.go.rule.engine.consequence.GoMobileIMSI",
                3,
                RuleEngineLogicalOperator.AND,
                new ArrayList<Condition>());

        Rule mvno1IMSIRule = new Rule(
                "IMSI",
                "Mark the request as MVNO 1",
                "RUN mt.com.go.rule.engine.consequence.Mvno1IMSI",
                4,
                RuleEngineLogicalOperator.AND,
                new ArrayList<Condition>());

        Rule mvno2IMSIRule = new Rule(
                "IMSI",
                "Mark the request as MVNO 2",
                "RUN mt.com.go.rule.engine.consequence.Mvno2IMSI",
                5,
                RuleEngineLogicalOperator.AND,
                new ArrayList<Condition>());

        Rule mvno3IMSIRule = new Rule(
                "IMSI",
                "Warn that MVNO 3 is not yet available",
                "RUN mt.com.go.rule.engine.consequence.Mvno3IMSI",
                6,
                RuleEngineLogicalOperator.OR,
                new ArrayList<Condition>());

        Rule operatorCodeNotDerived = new Rule(
                "Operator N/A",
                "The operator code was not derived",
                "SET-UNSUCCESSFUL|ADD-MESSAGE The operator code was not derived",
                7,
                RuleEngineLogicalOperator.AND,
                new ArrayList<Condition>());

        Condition imsiNotInParamsCondition = new Condition("IMSI not in Parameter list", "imsi", "!exists", null);
        Condition msisdnNotInParamsCondition = new Condition("MSISDN not in Parameter list", "msisdn", "!exists", null);
        Condition resubmitNotInParamsCondition = new Condition("RESUBMIT not in Parameter list", "resubmit", "!exists", null);
        Condition goMobileIMSICondition = new Condition("GO Mobile IMSI Condition", "imsi", "500A\\d{5}", null);
        Condition mvno1IMSICondition = new Condition("MVNO 1 IMSI Condition", "imsi", "500B\\d{5}", null);
        Condition mvno2IMSICondition = new Condition("MVNO 2 IMSI Condition", "imsi", "500C\\d{5}", null);
        Condition mvno3IMSICondition = new Condition("MVNO 3 IMSI Condition", null, null, "mt.com.go.rule.engine.condition.Mvno3NotAvailableCondition");
        Condition alwaysTrue = new Condition("True", null, null, "mt.com.go.rule.engine.condition.TrueCondition");

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

    private void logRules(HashMap<String, ArrayList<Rule>> rules) {

        for (Object currentKey : rules.keySet().toArray()) {

            ArrayList<Rule> temp = rules.get((String) currentKey);

            for (Rule currentRule : temp) {

                RuleEngineLogger.logDebug(this, "Loaded Rule : " + currentRule.toString());

            }

        }

    }
}
