package pikater.configuration;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kuba
 * Date: 9.8.13
 * Time: 9:47
 * To change this template use File | Settings | File Templates.
 */
public interface AgentConfiguration {
    String getAgentName();
    String getAgentType();
    List<Argument> getArguments();
}
