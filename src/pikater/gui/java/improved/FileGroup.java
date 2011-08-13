/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pikater.gui.java.improved;

import java.util.LinkedList;

/**
 *
 * @author martin
 */
public class FileGroup {

    private String trainFile;
    private String testFile;
    private LinkedList<String> labelFiles;

    public LinkedList<String> getLabelFiles() {
        return labelFiles;
    }

    public void setLabelFiles(LinkedList<String> labelFiles) {
        this.labelFiles = labelFiles;
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
    
    public String toString() {
        return trainFile + ":" + testFile;
    }
}
