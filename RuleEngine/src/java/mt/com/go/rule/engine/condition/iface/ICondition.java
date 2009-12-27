package mt.com.go.rule.engine.condition.iface;

import mt.com.go.rule.engine.RuleEngineRequest;
import mt.com.go.rule.engine.RuleEngineResponse;

public interface ICondition {

    public boolean doCondition(RuleEngineRequest request, RuleEngineResponse response);

}
