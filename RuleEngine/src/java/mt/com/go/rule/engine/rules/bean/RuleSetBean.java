package mt.com.go.rule.engine.rules.bean;

import java.util.ArrayList;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlPanelGroup;
import mt.com.go.rule.engine.rules.RuleLoader;
import mt.com.go.rule.engine.rules.RuleSet;

public class RuleSetBean {

    private ArrayList<RuleSet> ruleSets = null;
    private HtmlDataTable ruleSetTable = null;
    private HtmlDataTable ruleTable = null;
    private HtmlDataTable conditionTable = null;

    public RuleSetBean() {

        RuleLoader ruleLoader = new RuleLoader();
        ruleSets = ruleLoader.loadRules(false);

    }

    public ArrayList<RuleSet> getRuleSets() {
        return ruleSets;
    }

    public void setRuleSets(ArrayList<RuleSet> ruleSets) {
        this.ruleSets = ruleSets;
    }

    public HtmlDataTable getConditionTable() {
        return conditionTable;
    }

    public void setConditionTable(HtmlDataTable conditionTable) {
        this.conditionTable = conditionTable;
    }

    public HtmlDataTable getRuleSetTable() {
        return ruleSetTable;
    }

    public void setRuleSetTable(HtmlDataTable ruleSetTable) {
        this.ruleSetTable = ruleSetTable;
    }

    public HtmlDataTable getRuleTable() {
        return ruleTable;
    }

    public void setRuleTable(HtmlDataTable ruleTable) {
        this.ruleTable = ruleTable;
    }



}
