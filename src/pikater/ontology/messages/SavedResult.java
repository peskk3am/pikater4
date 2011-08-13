/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pikater.ontology.messages;

/**
 *
 * @author martin
 */
public class SavedResult {

    String agentType;
    String agentOptions;
    String trainFile;
    String testFile;
    String date;
    double errorRate;
    double kappaStatistic;
    double meanAbsError;
    double RMSE;
    double relativeAbsoluteError;
    double rootRelativeSquaredError;
    int userID;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getRMSE() {
        return RMSE;
    }

    public void setRMSE(double RMSE) {
        this.RMSE = RMSE;
    }

    public String getAgentOptions() {
        return agentOptions;
    }

    public void setAgentOptions(String agentOptions) {
        this.agentOptions = agentOptions;
    }

    public String getAgentType() {
        return agentType;
    }

    public void setAgentType(String agentType) {
        this.agentType = agentType;
    }

    public double getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(double errorRate) {
        this.errorRate = errorRate;
    }

    public double getKappaStatistic() {
        return kappaStatistic;
    }

    public void setKappaStatistic(double kappaStatistic) {
        this.kappaStatistic = kappaStatistic;
    }

    public double getMeanAbsError() {
        return meanAbsError;
    }

    public void setMeanAbsError(double meanAbsError) {
        this.meanAbsError = meanAbsError;
    }

    public double getRelativeAbsoluteError() {
        return relativeAbsoluteError;
    }

    public void setRelativeAbsoluteError(double relativeAbsoluteError) {
        this.relativeAbsoluteError = relativeAbsoluteError;
    }

    public double getRootRelativeSquaredError() {
        return rootRelativeSquaredError;
    }

    public void setRootRelativeSquaredError(double rootRelativeSquaredError) {
        this.rootRelativeSquaredError = rootRelativeSquaredError;
    }

    public String getTestFile() {
        return testFile;
    }

    public void setTestFile(String testFile) {
        this.testFile = testFile;
    }

    public String getTrainFile() {
        return trainFile;
    }

    public void setTrainFile(String trainFile) {
        this.trainFile = trainFile;
    }

}
