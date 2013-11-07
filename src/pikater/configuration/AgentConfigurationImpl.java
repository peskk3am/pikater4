package pikater.configuration;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Kuba
 * Date: 9.8.13
 * Time: 13:55
 * Container class that stores necessary information in order to initialize certain agent.
 */
public class AgentConfigurationImpl implements AgentConfiguration {
    private String agentName;
    private String agentType;
    private Map<String, String> arguments;

    public AgentConfigurationImpl(String agentName,String agentType,Map<String, String> arguments)
    {
        this.agentName=agentName;
        this.agentType=agentType;
        this.arguments=arguments;
    }

    @Override
    public String getAgentName() {
        return agentName;
    }

    @Override
    public String getAgentType() {
        return agentType;
    }

    @Override
    public Map<String, String> getArguments() {
        return arguments;
    }
}
