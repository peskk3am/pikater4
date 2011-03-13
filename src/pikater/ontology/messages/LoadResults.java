/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pikater.ontology.messages;

import com.jgoodies.forms.layout.FormLayout.Measure;
import jade.content.onto.basic.Action;

/**
 * Ontology class which represents the request to load the results
 * stored in DB.
 *
 * The variables store the search conditions
 *
 * @author Martin Pilat
 */
public class LoadResults extends Action {


    int userID;

    Double mseLower;
    Double mseUpper;
    Double kappaLower;
    Double kappaUpper;
    Double errorLower;
    Double errorUpper;
    Double maeLower;
    Double maeUpper;
    Double raeLower;
    Double raeUpper;
    Double rrseLower;
    Double rrseUpper;
    String dataFile;
    String testFile;
    String agentType;
    String startDate;
    String endDate;

    public String getAgentType() {
        return agentType;
    }

    public void setAgentType(String agentType) {
        this.agentType = agentType;
    }

    public String getDataFile() {
        return dataFile;
    }

    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Double getErrorLower() {
        return errorLower;
    }

    public void setErrorLower(Double errorLower) {
        this.errorLower = errorLower;
    }

    public Double getErrorUpper() {
        return errorUpper;
    }

    public void setErrorUpper(Double errorUpper) {
        this.errorUpper = errorUpper;
    }

    public Double getKappaLower() {
        return kappaLower;
    }

    public void setKappaLower(Double kappaLower) {
        this.kappaLower = kappaLower;
    }

    public Double getKappaUpper() {
        return kappaUpper;
    }

    public void setKappaUpper(Double kappaUpper) {
        this.kappaUpper = kappaUpper;
    }

    public Double getMaeLower() {
        return maeLower;
    }

    public void setMaeLower(Double maeLower) {
        this.maeLower = maeLower;
    }

    public Double getMaeUpper() {
        return maeUpper;
    }

    public void setMaeUpper(Double maeUpper) {
        this.maeUpper = maeUpper;
    }

    public Double getMseLower() {
        return mseLower;
    }

    public void setMseLower(Double mseLower) {
        this.mseLower = mseLower;
    }

    public Double getMseUpper() {
        return mseUpper;
    }

    public void setMseUpper(Double mseUpper) {
        this.mseUpper = mseUpper;
    }

    public Double getRaeLower() {
        return raeLower;
    }

    public void setRaeLower(Double raeLower) {
        this.raeLower = raeLower;
    }

    public Double getRaeUpper() {
        return raeUpper;
    }

    public void setRaeUpper(Double raeUpper) {
        this.raeUpper = raeUpper;
    }

    public Double getRrseLower() {
        return rrseLower;
    }

    public void setRrseLower(Double rrseLower) {
        this.rrseLower = rrseLower;
    }

    public Double getRrseUpper() {
        return rrseUpper;
    }

    public void setRrseUpper(Double rrseUpper) {
        this.rrseUpper = rrseUpper;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getTestFile() {
        return testFile;
    }

    public void setTestFile(String testFile) {
        this.testFile = testFile;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }


    private String appendAnd(String to, String what) {
        if (to.isEmpty()) {
            to = what;
        }
        else to += " AND " + what;
        return to;
    }

    public String asText() {

        String txt = "";

        if (agentType != null)
            txt = appendAnd(txt, "Agent type = " + agentType);
        if (dataFile != null)
            txt = appendAnd(txt, "trainig file = " + dataFile);
        if (testFile != null)
            txt = appendAnd(txt, "testing file = " + testFile);
        if (startDate != null && endDate != null)
            txt = appendAnd(txt, "created between " + startDate + " and " + endDate);
        if (errorLower != null && errorUpper != null)
            txt = appendAnd(txt,errorLower + " <= error <= " + errorUpper);
        if (kappaLower != null && kappaUpper != null)
            txt = appendAnd(txt, kappaLower + " <= kappa <= " + kappaUpper);
        if (mseLower != null && mseUpper != null)
            txt = appendAnd(txt, mseLower + " <= RMSE <= " + mseUpper);
        if (maeLower != null && maeUpper != null)
            txt = appendAnd(txt, maeLower + " <= MAE <= " + maeUpper);
        if (raeLower != null && raeUpper != null)
            txt = appendAnd(txt, raeLower + " <= RAE <= " + raeUpper);
        if (rrseLower != null && rrseUpper != null)
            txt = appendAnd(txt, rrseLower + " <= RRSE <= " + rrseUpper);

        if (txt.equals(""))
            return "No filter";

        return txt;
    }

    public String asSQLCondition() {

        String txt = "";

        // "CREATE TABLE results (" + "agentName VARCHAR (256), " + "agentType VARCHAR (256), 
        //" + "options VARCHAR (256), " + "dataFile VARCHAR (50), " + "testFile VARCHAR (50),
        //" + "errorRate DOUBLE, " + "kappaStatistic DOUBLE, " + "meanAbsoluteError DOUBLE, "
        //+ "rootMeanSquaredError DOUBLE, " + "relativeAbsoluteError DOUBLE, " +
        //"rootRelativeSquaredError DOUBLE)");


        if (agentType != null)
            txt = appendAnd(txt, "agentType LIKE \'%" + agentType + "%\'");
        if (dataFile != null)
            txt = appendAnd(txt, "dataFile LIKE \'%" + dataFile + "%\'");
        if (testFile != null)
            txt = appendAnd(txt, "testFile LIKE \'%" + testFile + "\'%");
        //if (startDate != null && endDate != null)
        //    txt = appendAnd(txt, "created between " + startDate + " and " + endDate);
        if (errorLower != null && errorUpper != null)
            txt = appendAnd(txt,errorLower + " <= errorRate AND errorRate <= " + errorUpper);
        if (kappaLower != null && kappaUpper != null)
            txt = appendAnd(txt, kappaLower + " <= kappaStatistic AND kappaStatistic <= " + kappaUpper);
        if (mseLower != null && mseUpper != null)
            txt = appendAnd(txt, mseLower + " <= rootMeanSquaredError AND rootMeanSquaredError <= " + mseUpper);
        if (maeLower != null && maeUpper != null)
            txt = appendAnd(txt, maeLower + " <= meanAbsoluteError AND meanAbsoluteError <= " + maeUpper);
        if (raeLower != null && raeUpper != null)
            txt = appendAnd(txt, raeLower + " <= relativeAbsoluteError AND relativeAbsoluteError <= " + raeUpper);
        if (rrseLower != null && rrseUpper != null)
            txt = appendAnd(txt, rrseLower + " <= rootRelativeSquaredError AND rootRelativeSquaredError <= " + rrseUpper);

        if (txt.equals(""))
            return "";


        return "WHERE " + txt;
        

    }


}
