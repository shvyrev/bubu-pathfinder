package mt.com.go.rule.engine;

import java.util.ArrayList;
import java.util.Hashtable;

public class RuleEngineResponse {

    private Hashtable<String, String> parameters;
    private ArrayList<String> messages = new ArrayList<String>();
    private String path;

    public RuleEngineResponse() {
    }

    public Hashtable<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Hashtable<String, String> parameters) {
        this.parameters = parameters;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<String> messages) {
        this.messages = messages;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }



    @Override
    public String toString() {

        StringBuffer buffer = new StringBuffer();
        buffer.append(this.getClass().getSimpleName()).append("={");
        buffer.append(", messages=").append(messages);
        buffer.append(", parameters=").append(parameters);
        buffer.append(", path=").append(path);
        buffer.append("}");

        return buffer.toString();

    }
    
}

