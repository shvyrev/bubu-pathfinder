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
import java.util.StringTokenizer;
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
 * RESUBMIT - resubmits the request until the rule keeps being satisfied
 * RESUBMIT X - resubmits the request until the rule keeps being satisfied up to X number of times
 * SET-PARAM X=Y - adds a parameter to the parameter list with the name X and value Y
 * REMOVE-PARAM X - removes the parameter with the name X from the parameter list
 * SET-UNSUCCESSFUL - marks the response as unsuccessful
 * ADD-MESSAGE X - adds a message to the message list in the response
 * SET-DECISION X - sets the decision to X, only useful in conjunction with RESUBMIT
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
        ruleLoader.logAllRulesLowDetail();

    }

    public RuleEngineResponse executeRule(RuleEngineRequest request) {

        RuleEngineLogger.logDebug(this, "---");
        RuleEngineLogger.logDebug(this, "Received request : " + request.toString());

        RuleEngineResponse response = new RuleEngineResponse();
        response.setParameters(request.getParameters());
        response.setSuccessful(true);

        try {

            long submitCounter = 0; // the counter where resubmissions are counted
            long resubmitAmount = 0; // the max number of resubmissions
            long resubmitPriority = 0; // the priority level at which the RESUBMIT consequence was set
            boolean resubmitForever = false; // when true the request will be resubmitted until the rule is not satisfied anymore

            boolean resubmit = true;

            while (resubmit) {

                submitCounter++;

                // log resubmission
                if (submitCounter > 1) {
                    RuleEngineLogger.logDebug(this, "Resubmitted #" + (submitCounter - 1));
                }

                if (resubmitAmount == 0 && resubmitForever == false) {
                    resubmit = false;
                }

                // load all rules
                RuleLoader ruleLoader = new RuleLoader();
                HashMap<String, ArrayList<Rule>> globalRules = ruleLoader.loadRules(false);

                // get the rules that match the requested decision type
                ArrayList<Rule> requestRuleSet = globalRules.get(request.getDecisionType());

                if (requestRuleSet != null) {

                    // loop rules
                    for (Rule currentRule : requestRuleSet) {

                        // if during a resubmission and the priority level is bigger than resubmitPriority, stop resubmitting
                        if (submitCounter > 1 && currentRule.getPriority().longValue() > resubmitPriority) {
                            RuleEngineLogger.logDebug(this, "Abandoning resubmissions, priority skipped.");
                            resubmitAmount = 0;
                            submitCounter = 1;
                            resubmitForever = false;
                            resubmitPriority = 0;
                            resubmit = false;
                        }


                        RuleEngineLogger.logDebug(this, "Running rule : " + currentRule.getName() + " - " + currentRule.getDescription() + " (" + currentRule.getLogicalOperator() + ")");

                        boolean ruleSatisfied = false;

                        int matchedConditions = 0;

                        for (Condition currentCondition : currentRule.getConditionList()) {
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
                                    String comparison = null;
                                    if (currentCondition.getExpression().trim().startsWith(">=")) {
                                        comparison = ">=";
                                    } else if (currentCondition.getExpression().trim().startsWith("<=")) {
                                        comparison = "<=";
                                    } else if (currentCondition.getExpression().trim().startsWith(">")) {
                                        comparison = ">";
                                    } else if (currentCondition.getExpression().trim().startsWith("<")) {
                                        comparison = "<";
                                    }

                                    // the expression of the current condition
                                    String expressionValue = currentCondition.getExpression().trim();
                                    // remove comparator from the expression
                                    expressionValue = expressionValue.substring(expressionValue.indexOf(comparison) + comparison.length(), expressionValue.length()).trim();
                                    // the value of the parameter to match with the expression
                                    String parameterValue = request.getParameters().get(currentCondition.getParameterName()).trim();

                                    if (expressionValue.startsWith("@date(")) {

                                        conditionMatched = compareDates(expressionValue, parameterValue, comparison);

                                    } else {

                                        if (expressionValue.length() >= 0) {

                                            BigDecimal expressionValueBD = null;
                                            BigDecimal parameterValueBD = null;

                                            try {

                                                expressionValueBD = new BigDecimal(expressionValue);
                                                parameterValueBD = new BigDecimal(parameterValue);

                                                if (comparison.equalsIgnoreCase(">=") && parameterValueBD.compareTo(expressionValueBD) >= 0) {
                                                    conditionMatched = true;
                                                } else if (comparison.equalsIgnoreCase("<=") && parameterValueBD.compareTo(expressionValueBD) <= 0) {
                                                    conditionMatched = true;
                                                } else if (comparison.equalsIgnoreCase(">") && parameterValueBD.compareTo(expressionValueBD) == 1) {
                                                    conditionMatched = true;
                                                } else if (comparison.equalsIgnoreCase("<") && parameterValueBD.compareTo(expressionValueBD) == -1) {
                                                    conditionMatched = true;
                                                }

                                            } catch (Exception e) {
                                                response.setSuccessful(false);
                                                response.getMessages().add("'" + expressionValue + "' " + comparison + " '" + request.getParameters().get(currentCondition.getParameterName()) + "' could not be evaluated");
                                                RuleEngineLogger.logDebug(this, "'" + expressionValue + "' " + comparison + " '" + request.getParameters().get(currentCondition.getParameterName()) + "' could not be evaluated", e);
                                            }

                                        }
                                    }

                                } else if (currentCondition.getExpression().trim().startsWith("=") || currentCondition.getExpression().trim().startsWith("!=")) {

                                    String comparison = null;
                                    if (currentCondition.getExpression().trim().startsWith("=")) {
                                        comparison = "=";
                                    } else if (currentCondition.getExpression().trim().startsWith("!=")) {
                                        comparison = "!=";
                                    }

                                    String expressionValue = currentCondition.getExpression().trim();
                                    expressionValue = expressionValue.substring(expressionValue.indexOf(comparison) + comparison.length(), expressionValue.length()).trim();

                                    String parameterValue = request.getParameters().get(currentCondition.getParameterName()).trim();

                                    if (expressionValue.startsWith("@date(")) {

                                        conditionMatched = compareDates(expressionValue, parameterValue, comparison);
                                        
                                    } else {

                                        if (isNumber(expressionValue) && isNumber(parameterValue)) {

                                            BigDecimal expressionBD = new BigDecimal(expressionValue);
                                            BigDecimal parameterBD = new BigDecimal(parameterValue);

                                            if (comparison.equalsIgnoreCase("=") && expressionBD.compareTo(parameterBD) == 0) {
                                                conditionMatched = true;
                                            } else if (comparison.equalsIgnoreCase("!=") && expressionBD.compareTo(parameterBD) != 0) {
                                                conditionMatched = true;
                                            }

                                        } else {

                                            if (expressionValue.length() > 0) {

                                                if (comparison.equalsIgnoreCase("=") && parameterValue.equalsIgnoreCase(expressionValue)) {
                                                    conditionMatched = true;
                                                } else if (comparison.equalsIgnoreCase("!=") && !parameterValue.equalsIgnoreCase(expressionValue)) {
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

                            if (matchedConditions > 0 && currentRule.getLogicalOperator() == RuleEngineLogicalOperator.OR) {
                                // if DecisionEngineLogicalOperator is set to OR only 1 match is required
                                ruleSatisfied = true;
                                RuleEngineLogger.logDebug(this, "> Rule Satisfied");
                                break;
                            }

                        }

                        if (currentRule.getLogicalOperator() == RuleEngineLogicalOperator.AND && matchedConditions == currentRule.getConditionList().size()) {
                            // if DecisionEngineLogicalOperator is set to AND all conditions need to be matched
                            ruleSatisfied = true;
                            RuleEngineLogger.logDebug(this, "> Rule Satisfied");
                        }

                        if (ruleSatisfied) {
                            // rule has been satisfied, proceed to consequences

                            try {

                                StringTokenizer tokenizer = new StringTokenizer(currentRule.getConsequence(), "|");

                                while (tokenizer.hasMoreTokens()) {

                                    String consequenceElement = tokenizer.nextToken().trim();

                                    RuleEngineLogger.logDebug(this, "> > Running consequence : " + consequenceElement);

                                    if (consequenceElement.startsWith("RESUBMIT") && submitCounter == 1) {
                                        // initialise a resubmission

                                        RuleEngineRequest resubmitRequest = new RuleEngineRequest();
                                        resubmitRequest.setDecisionType(request.getDecisionType());
                                        resubmitRequest.setParameters(response.getParameters());
                                        resubmitPriority = currentRule.getPriority().longValue();
                                        resubmit = true;

                                        if (!consequenceElement.endsWith("RESUBMIT")) {
                                            // setup limited number of resubmissions

                                            try {
                                                String resubmitAmountParameter = consequenceElement.substring(consequenceElement.indexOf(" ") + 1, consequenceElement.length()).trim();
                                                resubmitAmount = Integer.parseInt(resubmitAmountParameter) + 1;
                                                submitCounter = 1;
                                                resubmitForever = false;

                                            } catch (Exception e) {
                                                throw new RuleEngineException("Resubmit amount not properly set", e);
                                            }

                                        } else {
                                            // setup unlimited number of resubmissions

                                            resubmitAmount = 0;
                                            submitCounter = 1;
                                            resubmitForever = true;
                                        }

                                    } else if (consequenceElement.startsWith("RESUBMIT") && submitCounter > 1) {

                                        if (!resubmitForever && submitCounter >= resubmitAmount) {
                                            // if limited number of resubmissions has been reached stop resubmitting

                                            resubmitAmount = 0;
                                            submitCounter = 1;
                                            resubmit = false;

                                        }

                                    } else if (consequenceElement.startsWith("SET-PARAM ")) {
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


                                    } else if (consequenceElement.startsWith("SET-UNSUCCESSFUL")) {
                                        // set the response as unsuccessful

                                        response.setSuccessful(false);

                                    } else if (consequenceElement.startsWith("ADD-MESSAGE ")) {
                                        // add a message to the message list

                                        String message = consequenceElement.substring(consequenceElement.indexOf(" ") + 1, consequenceElement.length()).trim();
                                        response.getMessages().add(message);

                                    } else if (consequenceElement.startsWith("SET-DECISION ")) {
                                        // set the new decision type

                                        String decision = null;

                                        try {

                                            decision = consequenceElement.substring(consequenceElement.indexOf(" ") + 1, consequenceElement.length()).trim();
                                            request.setDecisionType(decision);

                                        } catch (Exception e) {
                                            throw new RuleEngineException("Failed to set decision", e);
                                        }

                                        if (decision == null || decision.trim().length() == 0) {
                                            throw new RuleEngineException("Decision type cannot be empty");
                                        }

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

                                break;

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                throw new RuleEngineException("Error loading consequence", ex);

                            }

                        }

                    }

                }
            }

        } catch (Exception e) {

            response.setSuccessful(false);
            response.getMessages().add("Internal error : " + e.getMessage());
            RuleEngineLogger.logDebug(this, "Internal error.", e);

        }

        RuleEngineLogger.logDebug(this, "Response sent : " + response.toString());

        return response;

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

    private boolean compareDates(String dateExpression, String parameterValue, String comparison) throws ParseException {

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

            if (comparison.equalsIgnoreCase(">=") && parameterDate.getTime() >= dateValue.getTime()) {
                conditionMatched = true;
            } else if (comparison.equalsIgnoreCase("<=") && parameterDate.getTime() <= dateValue.getTime()) {
                conditionMatched = true;
            } else if (comparison.equalsIgnoreCase(">") && parameterDate.getTime() > dateValue.getTime()) {
                conditionMatched = true;
            } else if (comparison.equalsIgnoreCase("<") && parameterDate.getTime() < dateValue.getTime()) {
                conditionMatched = true;
            } else if (comparison.equalsIgnoreCase("=") && parameterDate.getTime() == dateValue.getTime()) {
                conditionMatched = true;
            } else if (comparison.equalsIgnoreCase("!=") && parameterDate.getTime() != dateValue.getTime()) {
                conditionMatched = true;
            }

        }

        return conditionMatched;
    }
}
