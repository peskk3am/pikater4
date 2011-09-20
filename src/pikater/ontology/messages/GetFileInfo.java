package pikater.ontology.messages;

import jade.content.AgentAction;
import jade.content.onto.basic.Action;

public class GetFileInfo implements AgentAction {

    private static final long serialVersionUID = -5936031580984331462L;
    private int userID;
    private Integer instLower;
    private Integer instUpper;
    private Integer attrLower;
    private Integer attrUpper;
    private String attributeType;
    private String defaultTask;
    private String filename;
    private Boolean missingValues;

    public Integer getAttrLower() {
        return attrLower;
    }

    public void setAttrLower(Integer attrLower) {
        this.attrLower = attrLower;
    }

    public Integer getAttrUpper() {
        return attrUpper;
    }

    public void setAttrUpper(Integer attrUpper) {
        this.attrUpper = attrUpper;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
    }

    public String getDefaultTask() {
        return defaultTask;
    }

    public void setDefaultTask(String defaultTask) {
        this.defaultTask = defaultTask;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getInstLower() {
        return instLower;
    }

    public void setInstLower(Integer instLower) {
        this.instLower = instLower;
    }

    public Integer getInstUpper() {
        return instUpper;
    }

    public void setInstUpper(Integer instUpper) {
        this.instUpper = instUpper;
    }

    public Boolean getMissingValues() {
        return missingValues;
    }

    public void setMissingValues(Boolean missingValues) {
        this.missingValues = missingValues;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String toSQLCondition() {

        String sql = "userID = " + userID;

        if (filename != null) {
            sql += " AND externalFilename LIKE \'%" + filename + "%\'";
        }

        if (missingValues != null) {
            sql += " AND missingValues = " + missingValues;
        }

        if (attrLower != null && attrUpper != null) {
            sql += " AND numberOfAttributes >= " + attrLower + " AND numberOfAttributes <= " + attrUpper;
        }

        if (instLower != null && instUpper != null) {
            sql += " AND numberOfInstances >= " + instLower + " AND numberOfInstances <= " + instUpper;
        }

        if (defaultTask != null) {
            sql += " AND defaultTask=\'" + defaultTask + "\'";
        }

        if (attributeType != null) {
            sql += " AND attributeType=\'" + attributeType + "\'";
        }

        return sql;
    }
}