package mt.com.go.rule.engine.condition;

import mt.com.go.rule.engine.RuleEngineRequest;
import mt.com.go.rule.engine.RuleEngineResponse;
import mt.com.go.rule.engine.condition.iface.ICondition;

public class TrueCondition implements ICondition {

    public boolean doCondition(RuleEngineRequest request, RuleEngineResponse response) {
        return true;
    }

}
