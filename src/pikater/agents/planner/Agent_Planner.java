package pikater.agents.planner;

import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetInitiator;
import pikater.agents.PikaterAgent;
import pikater.ontology.messages.Execute;

import java.util.*;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: Klara
 * Date: 3/16/14
 * Time: 1:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class Agent_Planner extends PikaterAgent {

    protected LinkedList<ACLMessage> requestsFIFO = new LinkedList<ACLMessage>();
    protected int nResponders;

    private AID[] getAllComputingAgents(){
        AID[] computingAgents = null;

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("ComputingAgent");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            computingAgents = new AID[result.length];
            for (int i = 0; i < result.length; ++i) {
                computingAgents[i] = result[i].getName();
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        return computingAgents;
    }


    protected void setup() {
            initDefault();

            registerWithDF("Planner");

            addBehaviour(new RequestServer(this));
    }


    protected class RequestServer extends CyclicBehaviour {

        private MessageTemplate reqMsgTemplate = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                        MessageTemplate.and(MessageTemplate.MatchLanguage(codec.getName()),
                                MessageTemplate.MatchOntology(ontology.getName()))));

        public RequestServer(PikaterAgent agent) {
            super(agent);
        }

        @Override
        public void action() {

            try {
                ACLMessage req = receive(reqMsgTemplate);
                if (req != null) {
                    ContentElement content = getContentManager().extractContent(req);
                    if (((Action) content).getAction() instanceof Execute) {
                        // find computing agents
                        AID[] receivers = getAllComputingAgents();
                        // Fill the CFP message
                        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                        for (int i = 0; i < receivers.length; ++i) {
                            if (!receivers[i].getLocalName().equals("DurationServiceRegression")){
                                cfp.addReceiver(receivers[i]);
                            }
                        }
                        cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
                        cfp.setLanguage(codec.getName());
                        cfp.setOntology(ontology.getName());

                        // We want to receive a reply in 10 secs
                        cfp.setReplyByDate(new Date(System.currentTimeMillis() + 10000));

                        // add content
                        Execute ex = (Execute)((Action) content).getAction();
                        Action a = new Action();
                        a.setAction(ex);
                        a.setActor(myAgent.getAID());

                        getContentManager().fillContent(cfp, a);
                        addBehaviour(new askComputingAgents(myAgent, cfp, req));
                        return;
                    }

                    ACLMessage result_msg = req.createReply();
                    result_msg.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                    send(result_msg);
                    return;
                }
            } catch (Codec.CodecException ce) {
                ce.printStackTrace();
            } catch (OntologyException oe) {
                oe.printStackTrace();
            }
        }
    }

    protected class askComputingAgents extends ContractNetInitiator {
        ACLMessage req;
        ACLMessage cfp;

        public askComputingAgents(Agent agent, ACLMessage _cfp, ACLMessage _req) {
            super(agent, _cfp);
            cfp = _cfp;
            req = _req;
        }

        protected void handlePropose(ACLMessage propose, Vector v) {
            log("Agent " + propose.getSender().getName() + " proposed " + propose.getContent());
        }

        protected void handleRefuse(ACLMessage refuse) {
            log("Agent " + refuse.getSender().getName() + " refused");
        }

        protected void handleFailure(ACLMessage failure) {
            if (failure.getSender().equals(myAgent.getAMS())) {
                // FAILURE notification from the JADE runtime: the receiver
                // does not exist
                log("Responder does not exist");
            }
            else {
                log("Agent " + failure.getSender().getName() + " failed");
            }
            // Immediate failure --> we will not receive a response from this agent
            nResponders--;
        }

        protected void handleAllResponses(Vector responses, Vector acceptances) {
            if (responses.size() < nResponders) {
                // Some responder didn't reply within the specified timeout
                log("Timeout expired: missing " + (nResponders - responses.size()) + " responses.");
            }
            // Evaluate proposals.
            int bestProposal = -1;
            AID bestProposer = null;
            ACLMessage accept = null;
            Enumeration e = responses.elements();
            while (e.hasMoreElements()) {
                ACLMessage msg = (ACLMessage) e.nextElement();
                if (msg.getPerformative() == ACLMessage.PROPOSE) {
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    acceptances.addElement(reply);
                    int proposal = Integer.parseInt(msg.getContent());
                    if (proposal > bestProposal) {
                        bestProposal = proposal;
                        bestProposer = msg.getSender();
                        accept = reply;
                    }
                }
            }
            // Accept the proposal of the best proposer
            if (accept != null) {
                log("Accepting proposal " + bestProposal + " from responder " + bestProposer.getName());
                accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                try {
                    ContentElement content = getContentManager().extractContent(cfp);

                    Execute execute = (Execute) (((Action) content).getAction());

                    Action a = new Action();
                    a.setAction(execute);
                    a.setActor(myAgent.getAID());

                    getContentManager().fillContent(accept, a);
                } catch (UngroundedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (CodecException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (OntologyException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }

        protected void handleInform(ACLMessage _inform) {
            log("Agent "+_inform.getSender().getName()+" successfully performed the requested action");

            ACLMessage inform = req.createReply();
            inform.setPerformative(ACLMessage.INFORM);

            try {
                ContentElement content = getContentManager().extractContent(_inform);
                getContentManager().fillContent(inform, content);
            } catch (CodecException e) {
                e.printStackTrace();
            } catch (OntologyException e) {
                e.printStackTrace();
            }

            send(inform);
        }
    }
}
