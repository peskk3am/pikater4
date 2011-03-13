/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pikater.ontology.messages;

import jade.content.onto.basic.Action;

/**
 *
 * @author martin
 */
public class SaveAgent extends Action {

    int userID;
    Agent agent;
    byte[] object;

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public byte[] getObject() {
        return object;
    }

    public void setObject(byte[] object) {
        this.object = object;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    
}
