package pikater.configuration;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kuba
 * Date: 9.8.13
 * Time: 14:46
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationImpl implements Configuration {
    private List<AgentConfiguration> agentConfigurations;

    public ConfigurationImpl(List<AgentConfiguration> agentConfigurations)
    {
        this.agentConfigurations=agentConfigurations;
    }

    @Override
    public List<AgentConfiguration> getAgentConfigurations() {
        return agentConfigurations;
    }
}
