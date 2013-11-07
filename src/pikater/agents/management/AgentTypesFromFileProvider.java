package pikater.agents.management;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Kuba
 * Date: 7.11.13
 * Time: 14:19
 */
public class AgentTypesFromFileProvider implements AgentTypesProvider {
    private String path = System.getProperty("user.dir") + System.getProperty("file.separator");

    public Map<String, AgentTypeDefinition> GetAgentTypes() {
        Map<String, AgentTypeDefinition> toReturn=new HashMap<>();
        FileReader input;
        try {
            input = new FileReader(path + "agent_types");
            // Filter FileReader through a Buffered read to read a line at a
            // time
            BufferedReader bufRead = new BufferedReader(input);
            String line = bufRead.readLine();

            // Read through file one line at time
            while (line != null) {
                String[] agentClass = line.split(":");
                AgentTypeDefinition typeDefinition=new AgentTypeDefinition(agentClass[0], agentClass[1]);
                if(agentClass.length>2){
                    Object[] opts = new Object[agentClass.length-2];
                    System.arraycopy(agentClass, 2, opts, 0, opts.length);
                    typeDefinition.setOptions(opts);
                }
                toReturn.put(typeDefinition.getName(),typeDefinition);
                line = bufRead.readLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return toReturn;
    }
}
