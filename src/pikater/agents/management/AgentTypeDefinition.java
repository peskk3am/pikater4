package pikater.agents.management;

import pikater.configuration.Argument;

/**
 * User: Kuba
 * Date: 7.11.13
 * Time: 14:16
 */
public class AgentTypeDefinition {
    String name;
    String typeName;
    Argument[] options;

    public AgentTypeDefinition(String name, String typeName) {
        this.name = name;
        this.typeName = typeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Argument[] getOptions() {
        return options;
    }

    public void setOptions(Argument[] options) {
        this.options = options;
    }
}
