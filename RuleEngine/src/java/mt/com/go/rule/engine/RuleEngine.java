package mt.com.go.rule.engine;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import mt.com.go.rule.engine.condition.iface.ICondition;
import mt.com.go.rule.engine.consequence.iface.IConsequence;
import mt.com.go.rule.engine.enums.RuleEngineLogicalOperator;
import mt.com.go.rule.engine.exception.RuleEngineException;
import mt.com.go.rule.engine.logging.RuleEngineLogger;
import mt.com.go.rule.engine.rules.Condition;
import mt.com.go.rule.engine.rules.Rule;
import mt.com.go.rule.engine.rules.RuleLoader;

/**
 * Consequence scripting, each rule can have multiple consequenced delimited by | (pipe)
 *
 * SET-PARAM X=Y - adds a parameter to the parameter list with the name X and value Y
 * REMOVE-PARAM X - removes the parameter with the name X from the parameter list
 * SET-UNSUCCESSFUL - marks the response as unsuccessful
 * ADD-MESSAGE X - adds a message to the message list in the response
 * SLEEP X - waits for X seconds before continuing
 * RUN X - executes the doConsequence method of class X that implements IConsequence
 *
 */
public class RuleEngine {

    public RuleEngine() {
    }

    public void reloadRules() {

        RuleLoader ruleLoader = new RuleLoader();
        ruleLoader.loadRules(true);

    }

    public RuleEngineResponse executeRule(RuleEngineRequest request) {

        RuleEngineLogger.logDebug(this, "---");
        RuleEngineLogger.logDebug(this, "Received request : " + request.toString());

        RuleEngineResponse response = new RuleEngineResponse();
        response.setParameters(request.getParameters());

        ArrayList<Rule> rules = RuleLoader.getRules();

        for (Rule currentRule : rules) {

            if (currentRule.getName().equalsIgnoreCase(request.getDecisionType())) {
                try {
                    processRule(request, currentRule, response);
                } catch (Exception ex) {
                    RuleEngineLogger.logDebug(this, "Failed to process rule", ex);
                    response.getMessages().add("Internal Error : " + ex.getMessage());
                }

            }

        }

        RuleEngineLogger.logDebug(this, "Response sent : " + response.toString());

        return response;

    }

    private void processRule(RuleEngineRequest request, Rule rule, RuleEngineResponse response) throws Exception {


        boolean ruleSatisfied = false;

        int matchedConditions = 0;

        for (Condition currentCondition : rule.getConditionList()) {
            // loop conditions

            boolean conditionMatched = false;

            if (currentCondition.getParameterName() != null && currentCondition.getParameterName().trim().length() > 0) {
                // if condition parameter name is set

                String conditionExpressionBackup = currentCondition.getExpression();
                currentCondition.setExpression(updateExpressionVariables(currentCondition.getExpression(), request.getParameters()));

                String parameter = null;

                if (request.getParameters() != null && request.getParameters().size() > 0) {
                    // Get the parameter
                    parameter = request.getParameters().get(currentCondition.getParameterName());
                }

                if (currentCondition.getExpression().equalsIgnoreCase("exists")) {
                    // Check if parameter exists
                    if (parameter != null) {
                        conditionMatched = true;
                    }

                } else if (currentCondition.getExpression().equalsIgnoreCase("!exists")) {
                    // Check if parameter does not exist
                    if (parameter == null) {
                        conditionMatched = true;
                    }

                } else if (currentCondition.getExpression().trim().startsWith(">") || currentCondition.getExpression().trim().startsWith("<")) {

                    // identify comparator
                    String comparator = null;
                    if (currentCondition.getExpression().trim().startsWith(">=")) {
                        comparator = ">=";
                    } else if (currentCondition.getExpression().trim().startsWith("<=")) {
                        comparator = "<=";
                    } else if (currentCondition.getExpression().trim().startsWith(">")) {
                        comparator = ">";
                    } else if (currentCondition.getExpression().trim().startsWith("<")) {
                        comparator = "<";
                    }

                    // the expression of the current condition
                    String expressionValue = currentCondition.getExpression().trim();
                    // remove comparator from the expression
                    expressionValue = expressionValue.substring(expressionValue.indexOf(comparator) + comparator.length(), expressionValue.length()).trim();
                    // the value of the parameter to match with the expression
                    String parameterValue = request.getParameters().get(currentCondition.getParameterName()).trim();

                    if (expressionValue.startsWith("@date(")) {
                        // compare dates

                        try {

                            conditionMatched = compareDates(expressionValue, parameterValue, comparator);

                        } catch (Exception e) {

                            response.getMessages().add("'" + expressionValue + "' " + comparator + " '" + request.getParameters().get(currentCondition.getParameterName()) + "' could not be evaluated");
                            RuleEngineLogger.logDebug(this, "'" + expressionValue + "' " + comparator + " '" + request.getParameters().get(currentCondition.getParameterName()) + "' could not be evaluated", e);

                        }

                    } else {
                        // compare numbers

                        try {

                            conditionMatched = compareNumbers(expressionValue, parameterValue, comparator);

                        } catch (Exception e) {

                            response.getMessages().add("'" + expressionValue + "' " + comparator + " '" + request.getParameters().get(currentCondition.getParameterName()) + "' could not be evaluated");
                            RuleEngineLogger.logDebug(this, "'" + expressionValue + "' " + comparator + " '" + request.getParameters().get(currentCondition.getParameterName()) + "' could not be evaluated", e);

                        }

                    }

                } else if (currentCondition.getExpression().trim().startsWith("=") || currentCondition.getExpression().trim().startsWith("!=")) {

                    String comparator = null;
                    if (currentCondition.getExpression().trim().startsWith("=")) {
                        comparator = "=";
                    } else if (currentCondition.getExpression().trim().startsWith("!=")) {
                        comparator = "!=";
                    }

                    String expressionValue = currentCondition.getExpression().trim();
                    expressionValue = expressionValue.substring(expressionValue.indexOf(comparator) + comparator.length(), expressionValue.length()).trim();

                    String parameterValue = request.getParameters().get(currentCondition.getParameterName()).trim();

                    if (expressionValue.startsWith("@date(")) {
                        // compare dates

                        conditionMatched = compareDates(expressionValue, parameterValue, comparator);

                    } else {

                        if (isNumber(expressionValue) && isNumber(parameterValue)) {
                            // compare numbers

                            conditionMatched = compareNumbers(expressionValue, parameterValue, comparator);

                        } else {
                            // compare strings

                            if (expressionValue.length() > 0) {

                                if (comparator.equalsIgnoreCase("=") && parameterValue.equalsIgnoreCase(expressionValue)) {
                                    conditionMatched = true;
                                } else if (comparator.equalsIgnoreCase("!=") && !parameterValue.equalsIgnoreCase(expressionValue)) {
                                    conditionMatched = true;
                                }
                            }

                        }
                    }

                } else if (parameter != null && parameter.matches(currentCondition.getExpression())) {
                    // Check if the parameter matches the expression
                    conditionMatched = true;
                }

                currentCondition.setExpression(conditionExpressionBackup);

            } else if (currentCondition.getConditionClass() != null && currentCondition.getConditionClass().trim().length() > 0) {
                // Execute condition class

                try {
                    Class cls = Class.forName(currentCondition.getConditionClass());
                    ICondition condition = (ICondition) cls.newInstance();
                    boolean conditionSuccessful = condition.doCondition(request, response);

                    if (conditionSuccessful) {
                        conditionMatched = true;
                    }
                } catch (Exception ex) {
                    throw new RuleEngineException("> Error running condition", ex);
                }

            }

            if (conditionMatched) {
                matchedConditions++;
                RuleEngineLogger.logDebug(this, "> Condition matched, " + currentCondition.toString());
            } else {
                RuleEngineLogger.logDebug(this, "> Condition not matched, " + currentCondition.toString());
            }

            if (matchedConditions > 0 && rule.getLogicalOperator() == RuleEngineLogicalOperator.OR) {
                // if DecisionEngineLogicalOperator is set to OR only 1 match is required
                ruleSatisfied = true;
                RuleEngineLogger.logDebug(this, "> Rule Satisfied");
                break;
            }

        }

        if (rule.getLogicalOperator() == RuleEngineLogicalOperator.AND && matchedConditions == rule.getConditionList().size()) {
            // if DecisionEngineLogicalOperator is set to AND all conditions need to be matched
            ruleSatisfied = true;
            RuleEngineLogger.logDebug(this, "> Rule Satisfied");
        }



        if (ruleSatisfied) {
            // rule has been satisfied, proceed to consequences

            if (response.getPath() == null) {
                response.setPath("");
            }

            if (response.getPath().length() > 0) {
                response.setPath(response.getPath() + "." + rule.getCode());
            } else {
                response.setPath(rule.getCode());
            }


            try {

                StringTokenizer tokenizer = new StringTokenizer(rule.getConsequence(), "|");

                while (tokenizer.hasMoreTokens()) {

                    String consequenceElement = tokenizer.nextToken().trim();

                    RuleEngineLogger.logDebug(this, "> > Running consequence : " + consequenceElement);

                    if (consequenceElement.startsWith("SET-PARAM ")) {
                        // add/update a parameter in the parameter list

                        try {

                            String parameterName = consequenceElement.substring(consequenceElement.indexOf(" ") + 1, consequenceElement.indexOf("=")).trim();
                            String parameterValue = consequenceElement.substring(consequenceElement.indexOf("=") + 1, consequenceElement.length()).trim();

                            if (response.getParameters() != null) {
                                response.getParameters().put(parameterName, parameterValue);
                            }

                        } catch (Exception e) {
                            throw new RuleEngineException("Failed to set parameter", e);
                        }


                    } else if (consequenceElement.startsWith("REMOVE-PARAM ")) {
                        // remove a parameter from the parameter list

                        try {

                            String parameterName = consequenceElement.substring(consequenceElement.indexOf(" ") + 1, consequenceElement.length()).trim();

                            if (response.getParameters() != null) {
                                response.getParameters().remove(parameterName);
                            }

                        } catch (Exception e) {
                            throw new RuleEngineException("Failed to remove parameter", e);
                        }


                    } else if (consequenceElement.startsWith("ADD-MESSAGE ")) {
                        // add a message to the message list

                        String message = consequenceElement.substring(consequenceElement.indexOf(" ") + 1, consequenceElement.length()).trim();
                        response.getMessages().add(message);

                    } else if (consequenceElement.startsWith("SLEEP ")) {
                        // wait for a number of seconds

                        try {

                            String sleepSeconds = consequenceElement.substring(consequenceElement.indexOf(" ") + 1, consequenceElement.length()).trim();

                            if (sleepSeconds == null || sleepSeconds.trim().length() == 0) {
                                throw new RuleEngineException("Sleep not setup properly");
                            }

                            long sleepMillis = (long) Double.parseDouble(sleepSeconds) * 1000;
                            Thread.sleep(sleepMillis);

                        } catch (Exception e) {
                            throw new RuleEngineException("Failed to sleep", e);
                        }

                    } else if (consequenceElement.startsWith("RUN")) {
                        // execute a custom consequence

                        String className = consequenceElement.substring(consequenceElement.indexOf(" ") + 1, consequenceElement.length()).trim();

                        Class cls = Class.forName(className);
                        IConsequence consequence = (IConsequence) cls.newInstance();
                        consequence.doConsequence(request, response);

                    }

                }


            } catch (Exception ex) {

                RuleEngineLogger.logDebug(this, "Error running consequence", ex);
                throw new RuleEngineException("Error loading consequence", ex);

            }

        }

        if (rule.getChildRules() != null && rule.getChildRules().size() > 0) {
            for (Rule currentChildRule : rule.getChildRules()) {
                processRule(request, currentChildRule, response);
            }
        }

    }

    private boolean isNumber(String param) {

        boolean isNumberValue = false;

        try {
            BigDecimal temp = new BigDecimal(param);
            isNumberValue = true;
        } catch (Exception e) {
            //ignore
        }

        return isNumberValue;

    }

    private String updateExpressionVariables(String expression, Hashtable<String, String> parameters) {

        String ret = expression;
        String delim = "{";

        StringTokenizer tokens = new StringTokenizer(expression, delim, false);

        while (tokens.hasMoreTokens()) {

            String token = tokens.nextToken();

            if (token.startsWith("?")) {
                token = token.substring(1, token.indexOf("}"));
                ret = ret.replace("{?" + token + "}", parameters.get(token));
            }

        }

        return ret;
    }

    private boolean compareDates(String dateExpression, String parameterValue, String comparator) throws ParseException {

        boolean conditionMatched = false;

        String tempDateExpression = dateExpression.substring(6, dateExpression.length()).replace(")", "");

        if (tempDateExpression.indexOf(",") > 0) {

            String dateValueString = tempDateExpression.substring(0, tempDateExpression.indexOf(",")).trim();
            String datePattern = tempDateExpression.substring(tempDateExpression.indexOf(",") + 1, tempDateExpression.length()).trim();

            SimpleDateFormat sdf = new SimpleDateFormat(datePattern);

            Date dateValue = null;

            if (dateValueString.equalsIgnoreCase("now")) {

                dateValue = new Date();

            } else if (dateValueString.equalsIgnoreCase("today")) {

                GregorianCalendar gcal = (GregorianCalendar) GregorianCalendar.getInstance();
                gcal.set(Calendar.HOUR_OF_DAY, 0);
                gcal.set(Calendar.MINUTE, 0);
                gcal.set(Calendar.SECOND, 0);
                gcal.set(Calendar.MILLISECOND, 0);

                dateValue = gcal.getTime();

            } else if (dateValueString.equalsIgnoreCase("tomorrow")) {

                GregorianCalendar gcal = (GregorianCalendar) GregorianCalendar.getInstance();
                gcal.set(Calendar.HOUR_OF_DAY, 0);
                gcal.set(Calendar.MINUTE, 0);
                gcal.set(Calendar.SECOND, 0);
                gcal.set(Calendar.MILLISECOND, 0);
                gcal.add(Calendar.DATE, 1);

                dateValue = gcal.getTime();

            } else if (dateValueString.equalsIgnoreCase("yesterday")) {

                GregorianCalendar gcal = (GregorianCalendar) GregorianCalendar.getInstance();
                gcal.set(Calendar.HOUR_OF_DAY, 0);
                gcal.set(Calendar.MINUTE, 0);
                gcal.set(Calendar.SECOND, 0);
                gcal.set(Calendar.MILLISECOND, 0);
                gcal.add(Calendar.DATE, -1);

                dateValue = gcal.getTime();

            } else {

                dateValue = sdf.parse(dateValueString);

            }

            Date parameterDate = sdf.parse(parameterValue);

            if (comparator.equalsIgnoreCase(">=") && parameterDate.getTime() >= dateValue.getTime()) {
                conditionMatched = true;
            } else if (comparator.equalsIgnoreCase("<=") && parameterDate.getTime() <= dateValue.getTime()) {
                conditionMatched = true;
            } else if (comparator.equalsIgnoreCase(">") && parameterDate.getTime() > dateValue.getTime()) {
                conditionMatched = true;
            } else if (comparator.equalsIgnoreCase("<") && parameterDate.getTime() < dateValue.getTime()) {
                conditionMatched = true;
            } else if (comparator.equalsIgnoreCase("=") && parameterDate.getTime() == dateValue.getTime()) {
                conditionMatched = true;
            } else if (comparator.equalsIgnoreCase("!=") && parameterDate.getTime() != dateValue.getTime()) {
                conditionMatched = true;
            }

        }

        return conditionMatched;
    }

    private boolean compareNumbers(String expressionValue, String parameterValue, String comparator) throws Exception {

        boolean conditionMatched = false;

        if (expressionValue.length() >= 0) {

            BigDecimal expressionValueBD = null;
            BigDecimal parameterValueBD = null;


            expressionValueBD = new BigDecimal(expressionValue);
            parameterValueBD = new BigDecimal(parameterValue);

            if (comparator.equalsIgnoreCase(">=") && parameterValueBD.compareTo(expressionValueBD) >= 0) {
                conditionMatched = true;
            } else if (comparator.equalsIgnoreCase("<=") && parameterValueBD.compareTo(expressionValueBD) <= 0) {
                conditionMatched = true;
            } else if (comparator.equalsIgnoreCase(">") && parameterValueBD.compareTo(expressionValueBD) == 1) {
                conditionMatched = true;
            } else if (comparator.equalsIgnoreCase("<") && parameterValueBD.compareTo(expressionValueBD) == -1) {
                conditionMatched = true;
            } else if (comparator.equalsIgnoreCase("=") && parameterValueBD.compareTo(expressionValueBD) == 0) {
                conditionMatched = true;
            } else if (comparator.equalsIgnoreCase("!=") && parameterValueBD.compareTo(expressionValueBD) != 0) {
                conditionMatched = true;
            }

        }

        return conditionMatched;

    }
}
