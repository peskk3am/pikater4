package pikater.ontology.messages;

import jade.content.AgentAction;
import jade.content.onto.basic.Action;

public class GetData implements AgentAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8760296402786723483L;
	private String file_name;
        private boolean saveMetadata = false;

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String fileName) {
		file_name = fileName;
	}

        public boolean getSaveMetadata() {
            return saveMetadata;
        }

        public void setSaveMetadata(boolean saveMetadata) {
            this.saveMetadata = saveMetadata;
        }
}