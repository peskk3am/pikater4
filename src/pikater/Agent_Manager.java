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
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetInitiator;
import jade.proto.SubscriptionResponder;
import jade.proto.SubscriptionResponder.Subscription;
import jade.proto.SubscriptionResponder.SubscriptionManager;
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.List;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;
import jade.wrapper.StaleProxyException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import pikater.agents.PikaterAgent;
import pikater.agents.management.ManagerAgentCommunicator;
import pikater.ontology.messages.Data;
import pikater.ontology.messages.Eval;
import pikater.ontology.messages.Evaluation;
import pikater.ontology.messages.Execute;
import pikater.ontology.messages.GetAgents;
import pikater.ontology.messages.Id;
import pikater.ontology.messages.MessagesOntology;
import pikater.ontology.messages.Option;
import pikater.ontology.messages.Problem;
import pikater.ontology.messages.Recommend;
import pikater.ontology.messages.Results;
import pikater.ontology.messages.Solve;
import pikater.ontology.messages.Task;

public class Agent_Manager extends PikaterAgent {


	public Agent_Manager() {
		
		// TODO tohle asi uz neni potreba
		// Sets up a file reader to read the agent_types file				
		FileReader input;
		try {
			input = new FileReader(path + "agent_types");
			// Filter FileReader through a Buffered read to read a line at a
			// time
			BufferedReader bufRead = new BufferedReader(input);
			String line = bufRead.readLine();

			// Read through file one line at time
			while (line != null) {
				String[] agentClass = line.trim().split(":");
				agentTypes.put(agentClass[0], agentClass[1]);
				if(agentClass.length>2){
					Object[] opts = new Object[agentClass.length-2];
					for(int i = 0; i < opts.length; i++)
						opts[i] = agentClass[i+2];
					this.agentOptions.put(agentClass[0], opts);
				}
				line = bufRead.readLine();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private final String NO_XML_OUTPUT ="no_xml_output";
	
	private HashMap<String, String> agentTypes = new HashMap<String, String>();
	private HashMap<String, Object[]> agentOptions = new HashMap<String, Object[]>();
	
	private static final long serialVersionUID = -5140758757320827589L;

	private String path = System.getProperty("user.dir")
			+ System.getProperty("file.separator");

	private int problem_i = 0;

	private long timeout = 10000;
	private boolean no_xml_output = false;
	
	private Codec codec = new SLCodec();
	private Ontology ontology = MessagesOntology.getInstance();

	private Set subscriptions = new HashSet();
	// private Subscription subscription;

	List busyAgents = new ArrayList(); // by this manager; list of vectors <AID, String task_id> 
	
	private int max_number_of_CAs = 10;
	
	Map<String, Integer> receivedProblemsID = new HashMap<String, Integer>();			
	// problem id, number of received replies
	
	private boolean print_distance_matrix = true;
	
	protected class ExecuteTask extends ContractNetInitiator{

		private static final long serialVersionUID = -2044738642107219180L;

		List responders;
		int nResponders = 0;
		int nTasks;
		ACLMessage request;
		String problemID;
		ACLMessage cfp;
		
		public ExecuteTask(jade.core.Agent a, ACLMessage cfp, ACLMessage _request,
				int _nTasks, String _problemID) {
			super(a, cfp);
			
			Iterator itr = cfp.getAllReceiver(); 
			while (itr.hasNext()) {
				AID aid = (AID) itr.next();
				nResponders++;
			}
			this.cfp = cfp;
			request = _request;
			nTasks = _nTasks;
			problemID = _problemID;			
		}

		protected void handlePropose(ACLMessage propose, Vector v) {
			System.out.println(myAgent.getLocalName()+": Agent "+propose.getSender().getName()+" proposed "+propose.getContent());
		}
		
		protected void handleRefuse(ACLMessage refuse) {
			System.out.println(myAgent.getLocalName()+": Agent "+refuse.getSender().getName()+" refused");
		}
		
		protected void handleFailure(ACLMessage failure) {
			if (failure.getSender().equals(myAgent.getAMS())) {
				// FAILURE notification from the JADE runtime: the receiver
				// does not exist
				System.out.println(getLocalName()+": Responder does not exist");
			}
			else {
				System.out.println(myAgent.getLocalName()+": Agent "+failure.getSender().getName()+" failed");
			}
			// Immediate failure --> we will not receive a response from this agent
			nResponders--;
		}
		
		protected void handleAllResponses(Vector responses, Vector acceptances) {
			if (responses.size() < nResponders) {
				// Some responder didn't reply within the specified timeout
				System.out.println(myAgent.getLocalName()+": Timeout expired: missing "+(nResponders - responses.size())+" responses");
			}
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
				System.out.println(myAgent.getLocalName()+": Accepting proposal "+bestProposal+" from responder "+bestProposer.getName());
				
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
		
		private boolean lastTask(){			
			if (receivedProblemsID.get(problemID) == nTasks){
				return true;
			}
			else{
				return false;
			}
		}
		
		protected void handleInform(ACLMessage inform) {
			System.out.println(myAgent.getLocalName()+": Agent "+inform.getSender().getName()+" successfully performed the requested action");
			int n = 0;
			if (receivedProblemsID.get(problemID) != null){
				n = receivedProblemsID.get(problemID);
			}
			receivedProblemsID.put(problemID, n+1);
			
			// send subscription to gui agent after each received task
			sendSubscription(inform);
			
			// when all tasks' results are sent, send reply-inform to gui agent
			if (lastTask()){
			
				System.out.println(myAgent.getLocalName()+": Agent: " + getLocalName()
							+ ": all results sent.");
				
				ACLMessage msgOut = request.createReply();
				msgOut.setPerformative(ACLMessage.INFORM);
				msgOut.setContent("Finished");

				send(msgOut);
			}
										
			// get task_id
			String task_id = null;
			ContentElement content;
			try {
				content = getContentManager().extractContent(inform);
				if (content instanceof Result) {
					Result result = (Result) content;					
					List tasks = (List)result.getValue();
					Task t = (Task) tasks.get(0);						
					// it would be enough to get id from one of the task
					task_id = t.getId().getIdentificator();
					
					// if sender was computational agent, write results into the database
					if (!inform.getSender().getLocalName().contains("OptionsManager")){
							t.setFinish(getDateTime());
							if (t.getSave_results()){						
								DataManagerService.saveResult(myAgent, t);
							}
					}
				}
				
				// remove dedicated agents from busyAgents list							
				Iterator ba_itr = busyAgents.iterator();
				while (ba_itr.hasNext()) {
					BusyAgent ba = (BusyAgent) ba_itr.next();
					if (ba.getTask_id().equals(task_id)){
						ba_itr.remove();
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

			// killAgent(inform.getSender().getName());	
		}		
		
		private void sendSubscription(ACLMessage result) {
			// System.out.println("Agent: "+getLocalName()+": result: "+result+" "+result.getPerformative());

			// Prepare the msgOut to the request originator
			ACLMessage msgOut = request.createReply();
			msgOut.setPerformative(result.getPerformative());

			String problemGuiId = null;

			// if (result.getPerformative() != ACLMessage.FAILURE){

			// fill its content
			Results results = prepareTaskResults(result, problemID);
			if (results != null) {

				// write results to the database
				/* 
				Iterator resIterator = results.getResults().iterator();
				while (resIterator.hasNext()) {
					Task t = (Task) resIterator.next();
					if (t.getFinish() == null){
						t.setFinish(getDateTime());
					}
					if (t.getSave_results()){
						DataManagerService.saveResult(myAgent, t);
					}					
				}
				*/

				if (!no_xml_output){
					writeXMLResults(results);
				}
				
				msgOut.setPerformative(ACLMessage.INFORM);
				ContentElement content;
				try {
					content = getContentManager().extractContent(
							request);
					if (((Action) content).getAction() instanceof Solve) {
						Solve solve = (Solve) ((Action) content).getAction();
						problemGuiId = solve.getProblem().getGui_id();
					}
					Result _result = new Result((Action) content, results);
					getContentManager().fillContent(msgOut, _result);

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
			} else {
				msgOut.setPerformative(ACLMessage.FAILURE);
				msgOut.setContent(result.getContent());
			}
			// } // end if

			// go through every subscription
			java.util.Iterator it = subscriptions.iterator();
			while (it.hasNext()) {
				Subscription subscription = (Subscription) it.next();

				if (subscription.getMessage().getConversationId().equals(
						"subscription" + request.getConversationId())) {
					subscription.notify(msgOut);
				}
			}
			try {
				String name = ((Task) results.getResults().iterator().next())
						.getAgent().getName();
				busyAgents.remove(new AID(name, AID.ISLOCALNAME));
			} catch (Exception e) {
				// do nothing (we don't need to remove an agent, if there wasn't
				// any)
			}

			// */
		} // end sendSubscription
		
		private void killAgent(String name) {
			System.out.println("Agent:" + getLocalName() + ": Agent " + name
					+ " is being killed.");

			PlatformController container = getContainerController();

			try {
				container.getAgent(name).kill();
			} catch (StaleProxyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ControllerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	} // end of call for proposal bahavior
	

	protected void setup() {
		
		initDefault();
		
		registerWithDF("UserInterface");
		
		if (containsArgument(NO_XML_OUTPUT)) {
			if (isArgumentValueTrue(NO_XML_OUTPUT)){
				no_xml_output = true;
			}
		}			
		

		SubscriptionManager subscriptionManager = new SubscriptionManager() {
			public boolean register(Subscription s) {
				subscriptions.add(s);
				return true;
			}

			public boolean deregister(Subscription s) {
				subscriptions.remove(s);
				return true;
			}
		};

		MessageTemplate mt = MessageTemplate.and(MessageTemplate
				.MatchOntology(ontology.getName()), // TODO MatchLanguage,
													// MatchProtocol...
				MessageTemplate.or(MessageTemplate
						.MatchPerformative(ACLMessage.SUBSCRIBE),
						MessageTemplate.MatchPerformative(ACLMessage.CANCEL)));

		SubscriptionResponder send_results = new SubscriptionResponder(this,
				mt, subscriptionManager) {
			// protected ACLMessage handleSubscription(ACLMessage
			// subscription_msg) {
			// createSubscription(subscription_msg);
			// return null;
			// }
		};
		addBehaviour(send_results);

		addBehaviour(new RequestServer(this));		

	} // end setup


	protected class RequestServer extends CyclicBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6257623790759885083L;

		private MessageTemplate requestMsgTemplate = MessageTemplate
				.and(MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
						MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
								MessageTemplate.and(MessageTemplate.MatchLanguage(codec.getName()),
										MessageTemplate.MatchOntology(ontology.getName()))));

		public RequestServer(Agent agent) {			
			super(agent);
		}

		@Override 
		public void action() {
			ACLMessage request = receive(requestMsgTemplate);
			
			if (request != null) {
				System.out.println(getLocalName()
						+ ": REQUEST received from "
						+ request.getSender().getName());

				try {
					ContentElement content = getContentManager().extractContent(request);
					if (((Action) content).getAction() instanceof Solve) {
	
						Solve solve = (Solve) (((Action) content).getAction());
	
						Problem problem = (Problem) solve.getProblem();
						
						ACLMessage agree = request.createReply();
						agree.setPerformative(ACLMessage.AGREE);
	
						String problemID = generateProblemID();
						
						agree.setContent(problemID);
						
						send(agree);
	
						List messages = prepareTaskMessages(request, problemID);

						Iterator itr = messages.iterator();
						while (itr.hasNext()) {
							ACLMessage msg = (ACLMessage) itr.next();							
							addBehaviour(new ExecuteTask(myAgent, msg, request, messages.size(), problemID));
						}
	
						return;
					}
					
					if (((Action) content).getAction() instanceof GetAgents) {
						// find and/or create required number of agents;
						// maximum being 10
						List agents = new ArrayList();

                        GetAgents ga = (GetAgents) (((Action) content).getAction());
											
						String agentType = ga.getAgent().getType();
						int n = ga.getNumber();
						
						System.out.println(getLocalName()+": agent " 
								+ request.getSender().getName() + " requested "
								+ n + " agents.");
						
						n = n <= max_number_of_CAs ? n : max_number_of_CAs;
						
						System.out.println(getLocalName() + ": " + n + " agents assigned."); 
								
						String task_id = ga.getTask_id().getIdentificator();
						
						
						agents = getAgentsByType(agentType, n, task_id, true);
						
						// printBusyAgents();
						
						ACLMessage reply = request.createReply();
						
						if (agents.size() == 0) {
							reply.setPerformative(ACLMessage.FAILURE);
							reply.setContent(agentType + " agent could not be created.");
							// TODO send message to GUI agent
							// behav.sendSubscription(msg);
						} else {
							// send agents to options manager
							reply.setPerformative(ACLMessage.INFORM);
							Result result = new Result((Action) content, agents);						
							try {
								getContentManager().fillContent(reply, result);
							} catch (CodecException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (OntologyException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}							
						}
						send(reply);
						
						return;
					}
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
			else {
				block();
			}

			/*
			ACLMessage result_msg = request.createReply();
			result_msg.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			send(result_msg);
			*/
			return;

		}
	}

	
	protected List prepareTaskMessages(ACLMessage request, String problemId) {		

		List msgList = new ArrayList();
		// System.out.println("Agent "+getLocalName()+" failure :"+failure);

		ContentElement content;
		try {
			content = getContentManager().extractContent(request);
			// System.out.println("Agent " + getLocalName() + ": " + content);

			if (((Action) content).getAction() instanceof Solve) {
				
				Action action = (Action) content;
				Solve solve = (Solve) action.getAction();
				Problem problem = (Problem) solve.getProblem();

				problem.setId(new Id(problemId));

				int task_i = 0;
				Iterator d_itr = problem.getData().iterator();
				while (d_itr.hasNext()) {
					Data next_data = (Data) d_itr.next();

					if (next_data.getMetadata() != null) {
						next_data.getMetadata().setInternal_name(
								next_data.getTrain_file_name());
					}

					Iterator a_itr = problem.getAgents().iterator();
					while (a_itr.hasNext()) {												
						pikater.ontology.messages.Agent a_next = (pikater.ontology.messages.Agent) a_itr
								.next();
													
						pikater.ontology.messages.Agent a_next_copy = new pikater.ontology.messages.Agent();
						a_next_copy.setGui_id(a_next.getGui_id());
						a_next_copy.setName(a_next.getName());
						a_next_copy.setOptions(a_next.getOptions());
						a_next_copy.setType(a_next.getType());
						
						
						String agentType = a_next.getType();
						
						if (agentType.contains("?")) {
							// create recommender agent
							AID Recommender = createAgent(problem.getRecommender().getType(), null, null);
							
							// send task to recommender:
							ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
							req.addReceiver(Recommender);

							req.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

							req.setLanguage(codec.getName());
							req.setOntology(ontology.getName());
							// request.setReplyByDate(new Date(System.currentTimeMillis() + 200));

							Recommend recommend = new Recommend();
							recommend.setData(next_data);
							recommend.setRecommender(problem.getRecommender());
							
							Action a = new Action();
							a.setAction(recommend);
							a.setActor(this.getAID());

							try {
								getContentManager().fillContent(req, a);

								ACLMessage inform = FIPAService.doFipaRequestClient(this, req);

								Result r = (Result) getContentManager().extractContent(inform);

								// recommended agent from recommender
								a_next = (pikater.ontology.messages.Agent) r.getItems().get(0);
								a_next_copy = a_next;
								
							} catch (CodecException ce) {
								ce.printStackTrace();
							} catch (OntologyException oe) {
								oe.printStackTrace();
							} catch (FIPAException fe) {
								fe.printStackTrace();
							}							
							
						}													
						
						if (a_next != null) {
							Task task = new Task();
							task.setAgent(a_next_copy);
							task.setData(next_data);
							task.setProblem_id(new Id(problemId));
							task.setId(new Id(Integer.toString(task_i)));							
							task.setStart(getDateTime()); // if sent to options manager, overwritten							
							task.setGet_results(problem.getGet_results());
							task.setSave_results(problem.getSave_results());
							task.setGui_agent(problem.getGui_agent());
							task.setProblem_name(problem.getName());
							task.setEvaluation_method(problem.getEvaluation_method());
							task_i++;

							Execute ex = new Execute();
							ex.setTask(task);
							ex.setMethod(problem.getMethod());
							
							
							msgList.add(createCFPmessage(ex, problemId, findCFPmessageReceivers(ex, problemId)));
						}
					} // end while (iteration over files)

					// enter metadata to the table
					if (next_data.getMetadata() != null) {
						DataManagerService.saveMetadata(this, next_data
								.getMetadata());
					}

				} // end while (iteration over agents List)

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

		return msgList;
	
	} // end prepareTaskMessages

	private boolean isBusy(AID aid) {		
		Iterator itr = busyAgents.iterator();
		while (itr.hasNext()) {
			BusyAgent ba = (BusyAgent) itr.next();
			if (ba.getAid().equals(aid)){				
				return true;
			}
		}
		return false;
	}
	
	private void printBusyAgents(){
		Iterator itr = busyAgents.iterator();
		System.out.println("Busy agents: ");
		while (itr.hasNext()){
			BusyAgent ba = (BusyAgent)itr.next();
			System.out.println("name: " + ba.getAid().getLocalName() + " task id: "+ ba.getTask_id());
		}		
	}
	
	public List getAgentsByType(String agentType, int n, String task_id, boolean assign) {
		// returns list of AIDs (n agents that are not busy)
		
		List Agents = new ArrayList(); // List of AIDs
		
		// Make the list of agents of given type
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(agentType);
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			System.out.println(getLocalName()+": Found the following " + agentType + " agents:");
			
			for (int i = 0; i < result.length; ++i) {
				AID aid = result[i].getName();
				System.out.println(aid.getLocalName());
				if (!isBusy(aid) && Agents.size() < n){
					Agents.add(aid);
					if (assign){
						busyAgents.add(new BusyAgent(aid, task_id));
					}
				}
			}
			
			while (Agents.size() < n) {
				// create agent
				// doWait(300)
				// String agentName = generateName(agentType);
				// AID aid = createAgent(agentTypes.get(agentType), agentOptions.get(agentType));
				AID aid = createAgent(agentType, null, null);
				Agents.add(aid);
				// if (assign){
				//	busyAgents.add(new BusyAgent(aid, task_id));
				// }
			}
		} catch (FIPAException fe) {
			fe.printStackTrace();
			return null;
		}
		
		return Agents;
		
	} // end getAgentsByType

	
	public Vector<String> offerAgentTypes() {
		return new Vector<String> (agentTypes.keySet());
	} // end offerAgentTypes


	public String generateName(String agentType) {
		// don't use this function, 
		// leave the generating of the name on 
		// agent Agent Manager
		
		int number = 0;
		String name = agentType + number;
		boolean success = false;
		while (!success) {
			// try to find an agent with "name"
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setName(name);
			template.addServices(sd);
			try {
				DFAgentDescription[] result = DFService.search(this, template);
				// if the agent with this name already exists, increase number
				if (result.length > 0) {
					number++;
					name = agentType + number;
				} else {
					success = true;
					return name;
				}
			} catch (FIPAException fe) {
				fe.printStackTrace();
			}
		}
		return null;
	}

	public boolean exists(String name) {
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setName(name);
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			if (result.length > 0) {
				return true;
			}
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		return false;
	}

	public AID createAgent(String type, String name, List options) {
        ManagerAgentCommunicator communicator=new ManagerAgentCommunicator("agentManager");
        AID aid=communicator.createAgent(this,type,name,options);
		return aid;		
	}


	
	protected List findCFPmessageReceivers(Execute ex, String problemID) {
		// creates an Option Manager or finds/creates a computing agent
		// and creates a cfp message
		// TODO !!! so far only ONE agent is selected
		List receivers = new ArrayList(); 
							
		List mutable = new ArrayList();
		Iterator itr = ex.getTask().getAgent().getOptions().iterator();
		while (itr.hasNext()) {
			Option o = (Option) itr.next();
			if (o.getMutable()){				
				mutable.add(o);
			}
		}
		
		if (mutable.size() == 0){
			// find or create an computing agent 
			String agentType = ex.getTask().getAgent().getType();
			List agents = getAgentsByType(agentType, 1, ex.getTask().getId().getIdentificator(), true);
			receivers.add( ((AID)agents.get(0)).getLocalName() );
			// write results into the database instead of optionsManager
			// TODO nemel by to delat vzdycky manager? aby to bylo pokazdy stejny?
		}
		else{
			// create an Option Manager agent
			AID aid = createAgent("OptionsManager", null, null);			
			receivers.add( aid.getLocalName() );
		}
		return receivers;
	}

	protected ACLMessage createCFPmessage(Execute ex, String problemID, List receivers) {

		// create CFP message for the Option Manager or Computing Agent							  		
		ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
		// cfp.setConversationId(problemID);
		cfp.setLanguage(codec.getName());
		cfp.setOntology(ontology.getName());

		for (int i = 0; i < receivers.size(); ++i) {
			cfp.addReceiver(new AID((String) receivers.get(i), AID.ISLOCALNAME));
		}
		cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
		// We want to receive a reply in 10 secs
		cfp.setReplyByDate(new Date(System.currentTimeMillis() + 60000));
											
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

	protected Results prepareTaskResults(ACLMessage resultmsg, String problemID) {
		Results results = new Results();

		ContentElement content;
		try {
			content = getContentManager().extractContent(resultmsg);
			if (content instanceof Result) {
				Result result = (Result) content;
				
				List listOfResults = result.getItems();
				results.setProblem_id(problemID);
				results.setResults(listOfResults);				
				
				float sumError_rate = 0;
				float sumKappa_statistic = 0;
				float sumMean_absolute_error = 0;
				float sumRoot_mean_squared_error = 0;
				float sumRelative_absolute_error = 0; // percent
				float sumRoot_relative_squared_error = 0; // percent

				if (listOfResults == null) {
					// there were no tasks computed
					// leave the default values
					return null;
				} else {
					Iterator itr = listOfResults.iterator();
					while (itr.hasNext()) {
						Task next = (Task) itr.next();
						Evaluation evaluation;
						evaluation = next.getResult();
						
						results.setTask_id(next.getId()); // one of the tasks will do							
						
						// if the value has not been set by the CA, the sum
						// will < 0
						// error rate is a manadatory slot

						Iterator ev_itr = evaluation.getEvaluations().iterator();							
						while (ev_itr.hasNext()) {
							Eval next_eval = (Eval) ev_itr.next();
							if (next_eval.getName().equals("error_rate")){ 
								sumError_rate += next_eval.getValue();
							}
							
							if (next_eval.getName().equals("kappa_statistic")){ 
								sumKappa_statistic += next_eval.getValue();
							}

							if (next_eval.getName().equals("mean_absolute_error")){ 
								sumMean_absolute_error += next_eval.getValue();
							}
							
							if (next_eval.getName().equals("root_mean_squared_error")){ 
								sumRoot_mean_squared_error += next_eval.getValue();
							}
							
							if (next_eval.getName().equals("relative_absolute_error")){ 
								sumRelative_absolute_error += next_eval.getValue();
							}
							
							if (next_eval.getName().equals("root_relative_squared_error")){ 
								sumRoot_relative_squared_error += next_eval.getValue();
							}
						}

					}
					
					if (sumError_rate > -1) {
						results.setAvg_error_rate(sumError_rate
								/ listOfResults.size());
					}
					if (sumKappa_statistic > -1) {
						results.setAvg_kappa_statistic(sumKappa_statistic
								/ listOfResults.size());
					}
					if (sumMean_absolute_error > -1) {
						results
								.setAvg_mean_absolute_error(sumMean_absolute_error
										/ listOfResults.size());
					}
					if (sumRoot_mean_squared_error > -1) {
						results
								.setAvg_root_mean_squared_error(sumRoot_mean_squared_error
										/ listOfResults.size());
					}
					if (sumRelative_absolute_error > -1) {
						results
								.setAvg_relative_absolute_error(sumRelative_absolute_error
										/ listOfResults.size());
					}
					if (sumRoot_relative_squared_error > -1) {
						results
								.setAvg_root_relative_squared_error(sumRoot_relative_squared_error
										/ listOfResults.size());
					}
				}
			}			
		} catch (UngroundedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace(); return null
		} catch (CodecException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace(); return null
		} catch (OntologyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return results;

	} // prepareTaskResult

	protected boolean writeXMLResults(Results results) {
		String file_name = "xml" + System.getProperty("file.separator")
				+ getDateTimeXML() + "_" + results.getTask_id().getIdentificator() + ".xml";

		// create the "xml" directory, if it doesn't exist
		boolean exists = (new File("xml")).exists();
		if (!exists) {
			boolean success = (new File("xml")).mkdir();
			if (!success) {
				System.err.println(getLocalName() + ": Directory: " + "xml"
						+ " could not be created"); // TODO exception
			}
		}

		/* Generate the ExpML document */
		Document doc = new Document(new Element("result"));
		Element root = doc.getRootElement();

		List _results = results.getResults();
		if (_results != null) {
			Iterator itr = _results.iterator();
			while (itr.hasNext()) {
				Task next_task = (Task) itr.next();

				pikater.ontology.messages.Agent agent = next_task.getAgent();

				Element newExperiment = new Element("experiment");
				Element newSetting = new Element("setting");
				Element newAlgorithm = new Element("algorithm");
				newAlgorithm.setAttribute("name", agent.getType());
				newAlgorithm.setAttribute("libname", "weka");

				List Options = agent.getOptions();
				if (Options != null) {
					Iterator itr_o = Options.iterator();
					while (itr_o.hasNext()) {
						pikater.ontology.messages.Option next_o = (pikater.ontology.messages.Option) itr_o
								.next();

						Element newParameter = new Element("parameter");
						newParameter.setAttribute("name", next_o.getName());

						String value = "";
						if (next_o.getValue() != null) {
							value = (String) next_o.getValue();
						}
						newParameter.setAttribute("value", value);

						newAlgorithm.addContent(newParameter);
					}
				}
				Element newDataSet = new Element("dataset");
				newDataSet.setAttribute("train", next_task.getData()
						.getExternal_train_file_name());
				newDataSet.setAttribute("test", next_task.getData()
						.getExternal_test_file_name());

				Element newEvaluation = new Element("evaluation");
								
				Element newMetric;
				Iterator ev_itr = next_task.getResult().getEvaluations().iterator();											
				while (ev_itr.hasNext()) {
					Eval next_eval = (Eval) ev_itr.next();

					newMetric = new Element("metric");					
					newMetric.setAttribute(next_eval.getName(), getXMLValue(next_eval.getValue()));
					
					newEvaluation.addContent(newMetric);
				}
								
				newExperiment.addContent(newSetting);
				newExperiment.addContent(newEvaluation);
				newSetting.addContent(newAlgorithm);
				newSetting.addContent(newDataSet);

				root.addContent(newExperiment);
			}
		}

		Element newStatistics = new Element("statistics");
		Element newMetric1 = new Element("metric");
		newMetric1.setAttribute("average_error_rate", getXMLValue(results
				.getAvg_error_rate()));
		Element newMetric2 = new Element("metric");
		newMetric2.setAttribute("average_kappa_statistic", getXMLValue(results
				.getAvg_kappa_statistic()));
		Element newMetric3 = new Element("metric");
		newMetric3.setAttribute("average_mean_absolute_error",
				getXMLValue(results.getAvg_mean_absolute_error()));
		Element newMetric4 = new Element("metric");
		newMetric4.setAttribute("average_root_mean_squared_error",
				getXMLValue(results.getAvg_root_mean_squared_error()));
		Element newMetric5 = new Element("metric");
		newMetric5.setAttribute("average_relative_absolute_error",
				getXMLValue(results.getAvg_relative_absolute_error()));
		Element newMetric6 = new Element("metric");
		newMetric6.setAttribute("average_root_relative_squared_error",
				getXMLValue(results.getAvg_root_relative_squared_error()));

		newStatistics.addContent(newMetric1);
		newStatistics.addContent(newMetric2);
		newStatistics.addContent(newMetric3);
		newStatistics.addContent(newMetric4);
		newStatistics.addContent(newMetric5);
		newStatistics.addContent(newMetric6);

		root.addContent(newStatistics);

		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		try {
			FileWriter fw = new FileWriter(file_name);
			BufferedWriter fout = new BufferedWriter(fw);

			out.output(root, fout);

			fout.close();

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	} // end writeXMLResults

	private String getXMLValue(float value) {
		if (value < 0) {
			return "NA";
		}
		return Double.toString(value);
	}

	protected String generateProblemID() {		
		// Date date = new Date();
		//String problem_id = Long.toString(date.getTime()) + "_" + problem_i;
		return Integer.toString(problem_i++);
	}
	
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getDateTimeXML() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
    
}