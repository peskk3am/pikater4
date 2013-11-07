package pikater.agents.management;

/**
 * User: Kuba
 * Date: 7.11.13
 * Time: 14:16
 */
public class AgentTypeDefinition {
    String name;
    String typeName;
    Object[] options;

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

    public Object[] getOptions() {
        return options;
    }

    public void setOptions(Object[] options) {
        this.options = options;
    }
}
