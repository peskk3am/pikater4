package pikater;

import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import jade.proto.ContractNetInitiator;
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.List;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import pikater.agents.PikaterAgent;
import pikater.agents.management.ManagerAgentCommunicator;
import pikater.ontology.messages.Data;
import pikater.ontology.messages.Duration;
import pikater.ontology.messages.Eval;
import pikater.ontology.messages.Evaluation;
import pikater.ontology.messages.EvaluationMethod;
import pikater.ontology.messages.Execute;
import pikater.ontology.messages.GetDuration;
import pikater.ontology.messages.Id;
import pikater.ontology.messages.MessagesOntology;
import pikater.ontology.messages.Task;

public class Agent_Duration extends PikaterAgent {

	private static final long serialVersionUID = -5555820420884978956L;
    
    private final String LOG_LR_DURATIONS_NAME="log_LR_durations";
	
	List durations = new ArrayList();  // list of Durations
    
    int t = 10000; //ms
    AID aid = null;
    int id = 0;
    
    boolean log_LR_durations = false;
    
    @Override
    protected String getAgentType(){
    	return "Duration";
    }
    
    @Override
    protected void setup() {

		initDefault();
		
		registerWithDF();
    	
		if (ContainsArgument(LOG_LR_DURATIONS_NAME)) {
			if (isArgumentValueTrue(LOG_LR_DURATIONS_NAME)){
				log_LR_durations = true;
			}
		}		    	
    	        
        // create linear regression agent
        // send message to AgentManager to create an agent
        ManagerAgentCommunicator communicator=new ManagerAgentCommunicator("agentManager");
        aid=communicator.createAgent(this,"LinearRegression","DurationServiceRegression",null);
        		
		// compute one LR (as the first one is usually longer) 
		addBehaviour(new ExecuteTask(this, createCFPmessage(aid, "dc7ce6dea5a75110486760cfac1051a5")));
		doWait(2000);
		
        addBehaviour(new Test(this, t));			  
        
        MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchOntology(ontology.getName()), MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

        addBehaviour(new AchieveREResponder(this, mt) {

            private static final long serialVersionUID = 1L;

            @Override
            protected ACLMessage handleRequest(ACLMessage request)
                    throws NotUnderstoodException, RefuseException {

                try {
                    Action a = (Action) getContentManager().extractContent(request);

                    if (a.getAction() instanceof GetDuration) {
                        GetDuration gd = (GetDuration) a.getAction();
                        Duration duration = gd.getDuration();
                        
                        ACLMessage reply = request.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                                                
                        duration.setLR_duration(
                        		countDuration(duration.getStart(), duration.getDuration()));
                        		
                        Result r = new Result(gd, duration);                        
						getContentManager().fillContent(reply, r);												

                        return reply;
                    }
                } catch (OntologyException e) {
                    e.printStackTrace();
                } catch (CodecException e) {
                    e.printStackTrace();
                }

                ACLMessage failure = request.createReply();
                failure.setPerformative(ACLMessage.FAILURE);
                return failure;
            }
        });
    }    
    
    private float countDuration(Date _start, int duration){    	
    	if (duration >= Integer.MAX_VALUE){
    		return Integer.MAX_VALUE;
    	}    	
    	
    	float number_of_LRs = 0; 
    	long start = _start.getTime();
    	
    	// find the duration right before the start
    	int i_d = durations.size()-1;
    	while (start < ((Duration)durations.get(i_d)).getStart().getTime()){    		
    		i_d--;
    	}
    	
    	int i = 0;
		
    	long t1 = -1;
		long t2 = -1;
		long d = -1; 
		
    	while (duration > 0){   		    	
    		try {
	    		t1 = ((Duration)durations.get(i_d + i)).getStart().getTime();	    		
	    		if (i_d + i + 1 > durations.size()-1){ 
	    			// after last LR
	        		t2 = t1 + t; // expected time    		
	    		}
	    		else {
	    			t2 = ((Duration)durations.get(i_d + i + 1)).getStart().getTime();
	    		}
	    		long time_between_LRs = t2 - t1;
	    		
	    		// if (duration < t){
	    		if (duration < time_between_LRs){
	    			d = duration;
	    		}
	    		else {
	    		// 	d = t;
	    			d = Math.min(t2 - start, time_between_LRs); // osetreni prvniho useku        		
	    		}
	    		
	    		// System.out.println("d: " + d + " LR dur: " + ((Duration)durations.get(i_d + i)).getDuration());
	    		number_of_LRs += (float)d / (float)((Duration)durations.get(i_d + i)).getDuration();
	    		duration = duration - (int)Math.ceil(d);
	    		
	    		i++;
    		
	    	}
	    	catch (Exception e){
	    		e.printStackTrace();
	    	}

    	}
    	    	
    	return number_of_LRs;
    }
  
    protected class Test extends TickerBehaviour {

		private static final long serialVersionUID = -2200601967185243650L;

		public Test(Agent a, long period) {
			super(a, period);
			
		}

		protected void onTick() {
			  // compute linear regression on random (but the same) dataset
			  addBehaviour(new ExecuteTask(myAgent, createCFPmessage(aid, "dc7ce6dea5a75110486760cfac1051a5")));			  
		} 
    }
    
	protected class ExecuteTask extends ContractNetInitiator{

		private static final long serialVersionUID = -4895199062239049907L;
				
		ACLMessage cfp; 
		
		public ExecuteTask(jade.core.Agent a, ACLMessage cfp) {
			super(a, cfp);
			this.cfp = cfp;
		}

		protected void handlePropose(ACLMessage propose, Vector v) {
			// System.out.println(myAgent.getLocalName()+": Agent "+propose.getSender().getName()+" proposed "+propose.getContent());
		}
		
		protected void handleRefuse(ACLMessage refuse) {
			log("Agent "+refuse.getSender().getName()+" refused.", 1);
		}
		
		protected void handleFailure(ACLMessage failure) {
			if (failure.getSender().equals(myAgent.getAMS())) {
				// FAILURE notification from the JADE runtime: the receiver
				// does not exist
                log("Responder " + failure.getSender().getName() + " does not exist", 1);
			}
			else {
                log("Agent "+failure.getSender().getName()+" failed", 1);
			}
		}
		
		protected void handleAllResponses(Vector responses, Vector acceptances) {
			// Evaluate proposals.
			int bestProposal = Integer.MAX_VALUE;
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
					if (proposal < bestProposal) {
						bestProposal = proposal;
						bestProposer = msg.getSender();
						accept = reply;
					}
				}
			}
			// Accept the proposal of the best proposer
			if (accept != null) {				
				try {
					ContentElement content = getContentManager().extractContent(cfp);
					Execute execute = (Execute) (((Action) content).getAction());
					
					Action a = new Action();
					a.setAction(execute);
					a.setActor(myAgent.getAID());
												
					getContentManager().fillContent(accept, a);

				} catch (CodecException exception) {
					// TODO Auto-generated catch block
					exception.printStackTrace();
				} catch (OntologyException exception) {
					// TODO Auto-generated catch block
					exception.printStackTrace();
				}
				
				accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);				
			}						
			// TODO - if there is no proposer...
		}
				
		protected void handleInform(ACLMessage inform) {
            log("Agent "+inform.getSender().getName() + " successfully performed the requested action", 2);
																			
			ContentElement content;
			try {
				content = getContentManager().extractContent(inform);
				if (content instanceof Result) {
					Result result = (Result) content;					
					List tasks = (List)result.getValue();
					Task t = (Task) tasks.get(0);
					
					if (durations.size() > 1000000) { // over 270 hours
						durations.remove(0);
					}
					
					// save the duration of the computation to the list
					Evaluation evaluation = (Evaluation)t.getResult();
					List ev = evaluation.getEvaluations();
					
					Duration d = new Duration();
					Iterator itr = ev.iterator();					
					while (itr.hasNext()) {
						Eval eval = (Eval) itr.next();						
						if(eval.getName().equals("duration")){
							d.setDuration((int)eval.getValue());
						}
					}
					d.setStart(evaluation.getStart());
					durations.add(d);
					
					if (log_LR_durations){
						// write duration into a file:
						log(d.getStart() + " - " + d.getDuration());
					}											
					
				}				
			} catch (UngroundedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CodecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OntologyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}			
	} // end of call for proposal bahavior

        
    protected ACLMessage createCFPmessage(AID aid, String filename) {

		// create CFP message for Linear Regression Computing Agent							  		
		ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
		cfp.setLanguage(codec.getName());
		cfp.setOntology(ontology.getName());
		cfp.addReceiver(aid);
		cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);

		// We want to receive a reply in 10 secs
		cfp.setReplyByDate(new Date(System.currentTimeMillis() + 10000));

		pikater.ontology.messages.Agent ag = new pikater.ontology.messages.Agent();
		ag.setType("LinearRegression");
		ag.setOptions(new ArrayList());

		Data d = new Data();
		d.setTest_file_name("data/files/xxx");
		d.setTrain_file_name("data/files/"+filename);
		d.setExternal_test_file_name("xxx");
		d.setExternal_train_file_name("xxx");
		d.setMode("train_only");
		
		Task t = new Task();
		Id _id = new Id();
		_id.setIdentificator(Integer.toString(id));
		t.setId(_id);
		id++;
		
		t.setAgent(ag);
		t.setData(d);
		
		EvaluationMethod em = new EvaluationMethod();
		em.setName("Standard"); // TODO don't evaluate at all
		
		t.setEvaluation_method(em);
		
		t.setGet_results("after_each_computation");
		t.setSave_results(false);

		Execute ex = new Execute();
		ex.setTask(t);
		
		try {
			Action a = new Action();
			a.setAction(ex);
			a.setActor(this.getAID());
										
			getContentManager().fillContent(cfp, a);

		} catch (CodecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OntologyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return cfp;

	} // end createCFPmessage()
}
