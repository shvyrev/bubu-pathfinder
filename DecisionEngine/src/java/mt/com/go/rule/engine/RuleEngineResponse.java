package mt.com.go.rule.engine;

import java.util.ArrayList;
import java.util.Hashtable;

public class RuleEngineResponse {

    private Hashtable<String, String> parameters;
    private boolean successful;
    private ArrayList<String> messages = new ArrayList<String>();

    public RuleEngineResponse() {
    }

    public Hashtable<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Hashtable<String, String> parameters) {
        this.parameters = parameters;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<String> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {

        StringBuffer buffer = new StringBuffer();
        buffer.append(this.getClass().getSimpleName()).append("={");
        buffer.append("successful=").append(successful);
        buffer.append(", messages=").append(messages);
        buffer.append(", parameters=").append(parameters);
        buffer.append("}");

        return buffer.toString();

    }
    
}

