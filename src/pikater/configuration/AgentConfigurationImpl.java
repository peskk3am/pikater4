package pikater.configuration;

import java.util.List;
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
    private List<Argument> arguments;

    public AgentConfigurationImpl(String agentName,String agentType,List<Argument> arguments)
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
    public List<Argument> getArguments() {
        return arguments;
    }
}
