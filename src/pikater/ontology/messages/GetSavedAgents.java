package pikater.ontology.messages;

import jade.content.AgentAction;
import jade.content.onto.basic.Action;

public class GetSavedAgents implements AgentAction {
	private int userID;

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getUserID() {
		return userID;
	}

}
