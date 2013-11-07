package pikater.configuration;

/**
 * User: Kuba
 * Date: 7.11.13
 * Time: 12:29
 */
public class Argument {
    public Boolean getSendOnlyValue() {
        return sendOnlyValue;
    }

    public void setSendOnlyValue(Boolean sendOnlyValue) {
        this.sendOnlyValue = sendOnlyValue;
    }

    private Boolean sendOnlyValue=false;
    public Argument( String name,String value) {
        this.value = value;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String value;
}
