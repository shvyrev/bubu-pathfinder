/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mt.com.go.rule.engine;

import java.util.ArrayList;
import java.util.StringTokenizer;
import mt.com.go.rule.engine.rules.Rule;
import mt.com.go.rule.engine.rules.RuleLoader;

/**
 *
 * @author Reuben
 */
public class Main {

    public static void main(String[] args) {

        RuleLoader loader = new RuleLoader();
        ArrayList<Rule> rules = loader.getRules();

        loader.logAllRulesLowDetail();



        
    }
}
