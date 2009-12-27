package mt.com.go.rule.engine;

import java.util.ArrayList;
import java.util.HashMap;
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

                // load all rules
                RuleLoader ruleLoader = new RuleLoader();
                HashMap<String, ArrayList<Rule>> globalRules = ruleLoader.loadRules();

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

                                } else if (parameter != null && parameter.matches(currentCondition.getExpression())) {
                                    // Check if the parameter matches the expression
                                    conditionMatched = true;
                                }

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

                                    } else if (consequenceElement.startsWith("RUN"))  {
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
}