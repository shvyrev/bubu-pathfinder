package mt.com.go.rule.engine.rules;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import mt.com.go.rule.engine.enums.RuleEngineLogicalOperator;
import mt.com.go.rule.engine.logging.RuleEngineLogger;

public class RuleLoader {

    private static ArrayList<Rule> rules = null;
    private static Connection dbConnection = null;

    public RuleLoader() {
        loadRules(false);
    }

    private Connection getConnection() throws SQLException {

        if (dbConnection != null) {
            return dbConnection;
        }

        String DB_CONN_STRING = "jdbc:derby://localhost:1527/RULE_ENGINE";
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

    public ArrayList<Rule> loadRules(boolean forceReload) {

        if (!forceReload) {
            if (rules != null) {
                return rules;
            }
        }

        rules = new ArrayList<Rule>();

        RuleEngineLogger.logDebug(this, "Loading Rules...");
        Hashtable<Integer, Rule> rulesTable = loadRulesFromDB();
        RuleEngineLogger.logDebug(this, "Loaded Rules : " + rulesTable.size());
        RuleEngineLogger.logDebug(this, "Loading Conditions...");
        Hashtable<Integer, Condition> conditionsTable = loadConditionsFromDB();
        RuleEngineLogger.logDebug(this, "Loaded Conditions : " + conditionsTable.size());

        // propagate conditions in rules
        for (Object currentRulesTableKey : rulesTable.keySet().toArray()) {

            Rule rule = (Rule) rulesTable.get((Integer) currentRulesTableKey);
            ArrayList<Integer> ruleConditions = loadRuleConditionMapFromDB(rule.getRuleId());
            rule.setConditionList(new ArrayList<Condition>());

            for (Integer currentConditionId : ruleConditions) {
                rule.getConditionList().add(conditionsTable.get(currentConditionId));
            }

        }

        // move rules inside parent rules
        for (Object currentRulesTableKey : rulesTable.keySet().toArray()) {

            Rule rule = (Rule) rulesTable.get((Integer) currentRulesTableKey);

            boolean x = rule.getParentRuleId() != null && rule.getParentRuleId() > 0;

            if (rule.getParentRuleId() != null && rule.getParentRuleId() > 0) {
                Rule parentRule = rulesTable.get(rule.getParentRuleId());
                if (parentRule != null) {
                    if (parentRule.getChildRules() == null) {
                        parentRule.setChildRules(new LinkedList<Rule>());
                    }
                    parentRule.getChildRules().add(rule);
                }
            } else {
                rules.add(rule);
            }

        }

        prioritiseRules();
        return rules;
    }

    private void prioritiseRules() {

        Collections.sort(rules, new RulePriorityComparator());

        for (Rule currentRule : rules) {
            if (currentRule.getChildRules() != null && currentRule.getChildRules().size() > 0) {
                prioritiseRules(currentRule.getChildRules());
            }
        }
    }

    private void prioritiseRules(LinkedList<Rule> childRules) {
        Collections.sort(childRules, new RulePriorityComparator());

        for (Rule currentRule : childRules) {
            if (currentRule.getChildRules() != null && currentRule.getChildRules().size() > 0) {
                prioritiseRules(currentRule.getChildRules());
            }
        }
    }

//    public ArrayList<Rule> loadRulesOld(boolean forceReload) {
//
//        if (!forceReload) {
//            if (rules != null) {
//                return rules;
//            }
//        }
//
//        rules = new ArrayList<Rule>();
//
//
//
//        for (Object currentRuleSet : ruleSetsTable.keySet().toArray()) {
//
//            Integer key = (Integer) currentRuleSet;
//            RuleSet ruleSet = ruleSetsTable.get(key);
//            ruleSet.setRuleSetId(key.intValue());
//
//            ArrayList<Rule> ruleList = loadRuleSetsFromDB(key, rulesTable, conditionsTable);
//            ruleSet.setRules(ruleList);
//
//            rules.add(ruleSet);
//        }
//
//        RuleEngineLogger.logDebug(this, "Finished Loading Rules...");
//
//        RuleEngineLogger.logDebug(this, "Rules summary...");
//
//        logAllRulesLowDetail();
//
//        return rules;
//    }
//    private ArrayList<Rule> loadRuleSetsFromDB(Integer ruleSetId, Hashtable<Integer, Rule> rulesTable, Hashtable<Integer, Condition> conditionsTable) {
//
//        ArrayList<Rule> ruleSet = new ArrayList<Rule>();
//
//        try {
//            Connection con = getConnection();
//            PreparedStatement pstmt = con.prepareStatement("select rule_id, priority from rule_set_conf where rule_set_id = ? order by priority");
//            pstmt.setInt(1, ruleSetId.intValue());
//            ResultSet rs = pstmt.executeQuery();
//
//
//            ArrayList<RuleSetConfig> ruleSetConfig = new ArrayList<RuleSetConfig>();
//
//            while (rs.next()) {
//                ruleSetConfig.add(new RuleSetConfig(new Integer(rs.getInt("rule_id")), new Integer(rs.getInt("priority"))));
//            }
//
//            int priorityLevel = 0;
//
//            for (RuleSetConfig currentRuleSetConfig : ruleSetConfig) {
//                Rule tempRule = rulesTable.get(currentRuleSetConfig.getRuleId());
//
//                Rule rule = tempRule.getClone();
//                priorityLevel++;
//                rule.setPriority(new Integer(priorityLevel));
//
//
//
//                RuleEngineLogger.logDebug(this, rule.toString());
//
//                for (Integer currentConditionId : conditionIdList) {
//                    if (rule.getConditionList() == null) {
//                        rule.setConditionList(new ArrayList<Condition>());
//                    }
//
//                    Condition condition = conditionsTable.get(currentConditionId);
//                    rule.getConditionList().add(condition);
//                    RuleEngineLogger.logDebug(this, "Adding > " + condition);
//                }
//                ruleSet.add(rule);
//                //RuleEngineLogger.logDebug(this, rule.toString());
//            }
//
//        } catch (Exception ex) {
//            RuleEngineLogger.logDebug(this, "Error while loading rule set", ex);
//        }
//
//        return ruleSet;
//
//    }
    private ArrayList<Integer> loadRuleConditionMapFromDB(Integer ruleId) {

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

    private Hashtable<Integer, Condition> loadConditionsFromDB() {

        Hashtable<Integer, Condition> conditionsTable = new Hashtable<Integer, Condition>();

        try {
            Connection con = getConnection();
            PreparedStatement pstmt = con.prepareStatement("select condition_id, description, parameter_name, expression, condition_class from condition");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                Integer conditionId = new Integer(rs.getInt("condition_id"));
                Condition condition = new Condition();
                condition.setConditionId(conditionId);
                condition.setDescription(rs.getString("description"));
                condition.setParameterName(rs.getString("parameter_name"));
                condition.setExpression(rs.getString("expression"));
                condition.setConditionClass(rs.getString("condition_class"));
                conditionsTable.put(conditionId, condition);

                RuleEngineLogger.logDebug(this, "Loading condition : " + condition);
            }

        } catch (Exception ex) {
            RuleEngineLogger.logDebug(this, "Error while loading conditions", ex);
        }

        return conditionsTable;

    }

    private Hashtable<Integer, Rule> loadRulesFromDB() {

        Hashtable<Integer, Rule> rulesTable = new Hashtable<Integer, Rule>();

        try {
            Connection con = getConnection();
            PreparedStatement pstmt = con.prepareStatement("select rule_id, parent_rule_id, priority, name, code, consequence, logical_operator from rule order by priority desc");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                Integer ruleId = new Integer(rs.getInt("rule_id"));
                Rule rule = new Rule();
                rule.setRuleId(ruleId);
                rule.setParentRuleId(new Integer(rs.getInt("parent_rule_id")));
                rule.setName(rs.getString("name"));
                rule.setCode(rs.getString("code"));
                rule.setConsequence(rs.getString("consequence"));
                rule.setPriority(new Integer(rs.getInt("priority")));
                String logicalOperator = rs.getString("logical_operator");
                if (logicalOperator.trim().equalsIgnoreCase("OR")) {
                    rule.setLogicalOperator(RuleEngineLogicalOperator.OR);
                } else if (logicalOperator.trim().equalsIgnoreCase("AND")) {
                    rule.setLogicalOperator(RuleEngineLogicalOperator.AND);
                }

                rulesTable.put(ruleId, rule);

                RuleEngineLogger.logDebug(this, "Loading rule : " + rule);
            }

        } catch (Exception ex) {
            RuleEngineLogger.logDebug(this, "Error while loading rules", ex);
        }

        return rulesTable;

    }

//    public void logRulesFullDetail() {
//
//        rules = loadRules(false);
//
//        for (RuleSet currentRuleSet : rules) {
//            for (Rule currentRule : currentRuleSet.getRules()) {
//                RuleEngineLogger.logDebug(this, currentRule.toString());
//            }
//        }
//
//    }
//
    public void logAllRulesLowDetail() {

        for (Rule currentRule : rules) {

            RuleEngineLogger.logDebug(this, currentRule.getName() + " (" + currentRule.getCode() + ") (" + currentRule.getPriority() + ")");

            if (currentRule.getChildRules() != null && currentRule.getChildRules().size() > 0) {
                logAllRulesLowDetail(currentRule.getChildRules(), 1);
            }
        }

    }

    private void logAllRulesLowDetail(LinkedList<Rule> childRules, int level) {

        for (Rule currentRule : childRules) {

            StringBuffer levelString = new StringBuffer("");
            for (int i = 0; i < level; i++) {
                levelString.append("> ");
            }

            RuleEngineLogger.logDebug(this, levelString + currentRule.getName() + " (" + currentRule.getCode() + ") (" + currentRule.getPriority() + ")");

            if (currentRule.getChildRules() != null && currentRule.getChildRules().size() > 0) {
                level++;
                logAllRulesLowDetail(currentRule.getChildRules(), level);
            }
        }

    }

    public static ArrayList<Rule> getRules() {
        if (rules == null) {
            RuleLoader loader = new RuleLoader();
        }

        return rules;
    }

    public static void setRules(ArrayList<Rule> rules) {
        RuleLoader.rules = rules;
    }
}
