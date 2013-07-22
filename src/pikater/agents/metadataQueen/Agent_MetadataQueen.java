package pikater.agents.metadataQueen;

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
import jade.core.Profile;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;
import jade.proto.ContractNetInitiator;
import jade.proto.SubscriptionResponder.Subscription;
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.List;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;
import jade.wrapper.StaleProxyException;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.LinkedList;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import pikater.DataManagerService;
import pikater.ontology.messages.DeleteTempFiles;

import pikater.ontology.messages.CreateAgent;
import pikater.ontology.messages.Data;
import pikater.ontology.messages.DataInstances;
import pikater.ontology.messages.Duration;
import pikater.ontology.messages.Eval;
import pikater.ontology.messages.Evaluation;
import pikater.ontology.messages.EvaluationMethod;
import pikater.ontology.messages.Execute;
import pikater.ontology.messages.GetAllMetadata;
import pikater.ontology.messages.GetData;
import pikater.ontology.messages.GetDuration;
import pikater.ontology.messages.GetFileInfo;
import pikater.ontology.messages.GetFiles;
import pikater.ontology.messages.GetMetadata;
import pikater.ontology.messages.GetTheBestAgent;
import pikater.ontology.messages.Id;
import pikater.ontology.messages.ImportFile;
import pikater.ontology.messages.Instance;
import pikater.ontology.messages.LoadResults;
import pikater.ontology.messages.MessagesOntology;
import pikater.ontology.messages.Metadata;
import pikater.ontology.messages.Option;
import pikater.ontology.messages.Results;
import pikater.ontology.messages.SaveMetadata;
import pikater.ontology.messages.SaveResults;
import pikater.ontology.messages.SavedResult;
import pikater.ontology.messages.Solve;
import pikater.ontology.messages.Task;
import pikater.ontology.messages.TranslateFilename;
import pikater.ontology.messages.UpdateMetadata;
import pikater.ontology.messages.Attribute;
import weka.core.AttributeStats;

public class Agent_MetadataQueen extends Agent {

	private static final long serialVersionUID = -1886699589066832983L;
	
	Codec codec = new SLCodec();
    Ontology ontology = MessagesOntology.getInstance();
    
    int id = 0;
    int metadata_list_id = 0;
    
    // 3 levels:
	// 0 no output
	// 1 minimal
	// 2 normal
	private int verbosity = 1;
	
	ArrayList metadata_list = new ArrayList(); 
	
    @Override
    protected void setup() {

    	println("Agent " + getLocalName() +  " (MetadataQueen) is alive...", 1, true);
    	
    	// get the agent's parameters
    	Object[] args = getArguments();
		if (args != null && args.length > 0) {
			int i = 0;
						
			while (i < args.length){
				if (args[i].equals("log_LR_durations")){
					// log_LR_durations = true;
				}
				i++;
			}
		}		    	

        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontology);               
        
        // receive request
        MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchOntology(ontology.getName()), MessageTemplate.MatchPerformative(ACLMessage.REQUEST));        
		addBehaviour(new receiveRequest(this, mt));

    }  // end setup()
    
      
	protected class receiveRequest extends AchieveREResponder {

		private static final long serialVersionUID = -1849883814703874922L;

		public receiveRequest(Agent a, MessageTemplate mt) {
			super(a, mt);
			// TODO Auto-generated constructor stub
		}

        @Override
        protected ACLMessage handleRequest(ACLMessage request)
        	throws NotUnderstoodException, RefuseException {
        	            
        	try {
                Action a = (Action) getContentManager().extractContent(request);

                if (a.getAction() instanceof GetMetadata) {
                        GetMetadata gm = (GetMetadata) a.getAction();

                        // request a reader agent to read data
                        ACLMessage response = FIPAService.doFipaRequestClient(myAgent, prepareGetDataReq(gm.getInternal_filename()));                        	
                        
                        DataInstances data = processGetData(response);       		
        				if (data != null) {        					
        					
        					Metadata m = computeMetadata(data);        					
        					m.setInternal_name(gm.getInternal_filename());
        					m.setExternal_name(gm.getExternal_filename());        					
        					MetadataListItem mli = new MetadataListItem(m, metadata_list_id);
        					metadata_list.add(mli);
        					metadata_list_id++;

        					computeExternalMetadata(data, gm.getInternal_filename(), request, mli);
        				}
        				else {
        					String msg = "No train data received from the reader agent: Wrong content.";
        		        	ACLMessage reply = request.createReply();
        		            reply.setPerformative(ACLMessage.FAILURE);
        		            reply.setContent(msg);                                                																				
        		            
        		            return reply;
        				}                        	                		                        
                }
            } catch (OntologyException e) {
                e.printStackTrace();
            } catch (CodecException e) {
                e.printStackTrace();
            } catch (FIPAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	return null;
        }
    }				        
                
    
    pikater.ontology.messages.DataInstances processGetData(ACLMessage inform) {
		ContentElement content;
		try {
			content = getContentManager().extractContent(inform);
			if (content instanceof Result) {
				Result result = (Result) content;
				if (result.getValue() instanceof pikater.ontology.messages.DataInstances) {
					return (pikater.ontology.messages.DataInstances) result.getValue();
				}
			}
		} catch (UngroundedException e) {
			e.printStackTrace();
		} catch (CodecException e) {
			e.printStackTrace();
		} catch (OntologyException e) {
			e.printStackTrace();
		}
		return null;
	}
	    	
    
	protected ACLMessage prepareGetDataReq(String fileName) {
		AID[] ARFFReaders;
		AID reader;
		ACLMessage msgOut = null;
		// Make the list of reader agents
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("ARFFReader");
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			// System.out.println(getLocalName() + ": Found the following ARFFReader agents:");
			ARFFReaders = new AID[result.length];
			for (int i = 0; i < result.length; ++i) {
				ARFFReaders[i] = result[i].getName();
				// System.out.println("    " + ARFFReaders[i].getName());
			}
			
			// randomly choose one of the readers
			Random randomGenerator = new Random();		    
		    int randomInt = randomGenerator.nextInt(result.length);
		    reader = ARFFReaders[randomInt];

		    println("Using " + reader + ", filename: " + fileName, 2, true);
			
			// request
			msgOut = new ACLMessage(ACLMessage.REQUEST);
			msgOut.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			// msgOut.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
			msgOut.setLanguage(codec.getName());
			msgOut.setOntology(ontology.getName());
			msgOut.addReceiver(reader);
			// content
			GetData get_data = new GetData();
			get_data.setFile_name(fileName);
			Action a = new Action();
			a.setAction(get_data);
			a.setActor(this.getAID());
			getContentManager().fillContent(msgOut, a);
		} catch (FIPAException fe) {
			fe.printStackTrace();
			return null;
		} catch (CodecException e) {
			e.printStackTrace();
			return null;
		} catch (OntologyException e) {
			e.printStackTrace();
			return null;
		}
		return msgOut;
	} // end prepareGetDataReq    
    
	
	private Metadata computeMetadata(DataInstances data){	

		MetadataReader reader=new MetadataReader();
                return reader.computeMetadata(data);
	}
		
	
	public AID getAgentByType(String agentType) {
		
		AID agent = null;
		
		// Make the list of agents of given type
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(agentType);
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			// System.out.println(getLocalName()+": Found the following " + agentType + " agents:");
			
			for (int i = 0; i < result.length; ++i) {
				agent = result[i].getName();
				// System.out.println(aid.getLocalName());
			}
			
			while (agent == null) {
				// create agent
				// doWait(300);
				
				// String agentName = generateName(agentType);
				// AID aid = createAgent(agentTypes.get(agentType), agentOptions.get(agentType));
				agent = createAgent(agentType, null, null);
			}
		} catch (FIPAException fe) {
			fe.printStackTrace();
			return null;
		}
		
		return agent;
		
	} // end getAgentByType

	public AID createAgent(String type, String name, List options) {
		
		ACLMessage msg_ca = new ACLMessage(ACLMessage.REQUEST);
		msg_ca.addReceiver(new AID("agentManager", false));
		msg_ca.setLanguage(codec.getName());
		msg_ca.setOntology(ontology.getName());
						
		CreateAgent ca = new CreateAgent();
		if (name != null){
			ca.setName(name);
		}
		if (options != null){
			ca.setArguments(options);
		}
		ca.setType(type);
		
		Action a = new Action();
		a.setAction(ca);
		a.setActor(this.getAID());
				
		AID aid = null; 
		try {
			getContentManager().fillContent(msg_ca, a);	
			ACLMessage msg_name = FIPAService.doFipaRequestClient(this, msg_ca);
			
			aid = new AID(msg_name.getContent(), AID.ISLOCALNAME);
		} catch (FIPAException e) {
			System.err.println(getLocalName() + ": Exception while adding agent "
					+ type + ": " + e);		
		} catch (CodecException e) {
			System.err.print(getLocalName() + ": ");
			e.printStackTrace();
		} catch (OntologyException e) {
			System.err.print(getLocalName() + ": ");
			e.printStackTrace();
		}
		
		return aid;		
	}
	
	private void computationDuration(String agent_type, String internal_filename, ACLMessage request, MetadataListItem mli){
        mli.to_compute.add(agent_type);
		System.out.println("adding: " + agent_type);
		// get / create linear regression agent
		AID aid = getAgentByType(agent_type);
		System.out.println("aid: " + aid);
		addBehaviour(new ExecuteTask(this, createCFPmessage(aid, agent_type, internal_filename), request, mli));
		// TODO first computation takes longer time ?	
	}
	
    protected ACLMessage createCFPmessage(AID aid, String agent_type, String filename) {

		// create CFP message for a Computing Agent							  		
		ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
		cfp.setLanguage(codec.getName());
		cfp.setOntology(ontology.getName());
		cfp.addReceiver(aid);
		cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);

		// We want to receive a reply in 10 secs
		cfp.setReplyByDate(new Date(System.currentTimeMillis() + 10000));

		pikater.ontology.messages.Agent ag = new pikater.ontology.messages.Agent();
		ag.setType(agent_type);
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
    

	protected class ExecuteTask extends ContractNetInitiator{

		private static final long serialVersionUID = -4895199062239049907L;
				
		ACLMessage cfp;
		ACLMessage request;
		MetadataListItem mli;		
		
		public ExecuteTask(jade.core.Agent a, ACLMessage cfp, ACLMessage request, MetadataListItem mli) {
			super(a, cfp);
			System.out.println("konstruktor "+cfp);
			this.cfp = cfp;
			this.mli = mli;
			this.request = request;
		}

		protected void handlePropose(ACLMessage propose, Vector v) {
			// System.out.println(myAgent.getLocalName()+": Agent "+propose.getSender().getName()+" proposed "+propose.getContent());
		}
		
		protected void handleRefuse(ACLMessage refuse) {
			println("Agent "+refuse.getSender().getName()+" refused.", 1, true);
		}
		
		protected void handleFailure(ACLMessage failure) {
			if (failure.getSender().equals(myAgent.getAMS())) {
				// FAILURE notification from the JADE runtime: the receiver
				// does not exist
				println("Responder " + failure.getSender().getName() + " does not exist", 1, true);
			}
			else {
				println("Agent "+failure.getSender().getName()+" failed", 1, true);
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
				// System.out.println(myAgent.getLocalName()+": Accepting proposal "+bestProposal+" from responder "+bestProposer.getName());
				
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
			println("Agent "+inform.getSender().getName()
					+ " successfully performed the requested action", 2, true);
																			
			ContentElement content;
			try {
				// get the original cfp message:
				content = getContentManager().extractContent(cfp);
				Execute execute = (Execute) (((Action) content).getAction());
				String internal_filename = execute.getTask().getData().getTrain_file_name();
				String agent_type = execute.getTask().getAgent().getType();
								
				content = getContentManager().extractContent(inform);
				if (content instanceof Result) {
					Result result = (Result) content;					
					List tasks = (List)result.getValue();
					Task t = (Task) tasks.get(0);
										
					// save the duration of the computation to the list
					Evaluation evaluation = (Evaluation)t.getResult();
					List ev = evaluation.getEvaluations();
					
					Iterator itr = ev.iterator();					
					while (itr.hasNext()) {
						Eval eval = (Eval) itr.next();						
						if(eval.getName().equals("duration")){
							// find the correct metadata's slot
							
							mli.to_compute.remove(agent_type);
							System.out.println("removing: " +agent_type);
							
							int duration = (int)eval.getValue();
							if (agent_type.equals("LinearRegression")){
								Metadata new_metadata = mli.getMetadata();
								new_metadata.setLinear_regression_duration(duration);
								mli.setMetadata(new_metadata);
							}
							
							if (isReadyToReply(mli)){
								// update metadata_list
								Metadata m = mli.getMetadata();
								metadata_list.remove(mli);
								
								reply(request, m);
							}
						}
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


	private boolean isReadyToReply(MetadataListItem mli){
		if (mli.to_compute.size() != 0){
			return false;
	    }        		            
		else{
			return true;
		}		
	}

	private void reply(ACLMessage request, Metadata m){
		ACLMessage reply = request.createReply();
		
		// order DataManager to write data to database
		// TODO presunout jinam
		DataManagerService.saveMetadata(this, m);        					        					
		
		// send inform to agent who requested this
		reply.setContent("OK");
		reply.setPerformative(ACLMessage.INFORM);

		send(reply);
	}
	
	
	private void computeExternalMetadata(DataInstances data, String internal_filename, ACLMessage request, MetadataListItem mli){		
        computationDuration("LinearRegression", internal_filename, request, mli);
	}
	
	
	private void print(String text, int level, boolean print_agent_name){
		if (verbosity >= level){
			if (print_agent_name){
				System.out.print(getLocalName() + ": ");
			}
			System.out.print(text);
		}
	}

	private void println(String text, int level, boolean print_agent_name){
		if (verbosity >= level){
			if (print_agent_name){
				System.out.print(getLocalName() + ": ");
			}
			System.out.println(text);
		}
	}    
}
