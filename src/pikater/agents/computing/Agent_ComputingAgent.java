package pikater.agents.computing;

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
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.util.leap.ArrayList;
import jade.util.leap.List;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

import pikater.agents.PikaterAgent;
import pikater.ontology.messages.Data;
import pikater.ontology.messages.DataInstances;
import pikater.ontology.messages.Eval;
import pikater.ontology.messages.Evaluation;
import pikater.ontology.messages.EvaluationMethod;
import pikater.ontology.messages.Execute;
import pikater.ontology.messages.GetData;
import pikater.ontology.messages.GetOptions;
import pikater.ontology.messages.MessagesOntology;
import pikater.ontology.messages.PartialResults;
import pikater.ontology.messages.Task;
import weka.core.Instances;

public abstract class Agent_ComputingAgent extends PikaterAgent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7927583436579620995L;
	protected Codec codec = new SLCodec();
	private Ontology ontology = MessagesOntology.getInstance();

	public enum states {
		NEW, TRAINED
	}

    private final String CLASS_NAME="className";

	/* common properties for all computing agents */
	public String trainFileName;
	public String testFileName;
	public String labelFileName = "";

	public states state = states.NEW;
	public boolean hasGotRightData = false;

	// protected Vector<MyWekaOption> Options;
	protected pikater.ontology.messages.Agent agent_options = null;

	protected Instances data; // data read from fileName file
	Instances train;
	DataInstances onto_train;
	Instances test;
	DataInstances onto_test;

	Instances label;
	DataInstances onto_label;
	int convId = 0;

	protected String[] OPTIONS;
	protected pikater.ontology.messages.Task current_task = null;
	// protected String[] OPTIONS_;
	protected String className;

	protected Object[] args;

	boolean working = false; // TODO -> state?

	LinkedList<ACLMessage> taskFIFO = new LinkedList<ACLMessage>();

	private Behaviour execution_behaviour = null;
	private Behaviour send_options_behaviour = null;

	private boolean newAgent = true;
	private boolean resurrected = false;
	
	private boolean engaged = false;

	protected abstract Date train(Evaluation evaluation) throws Exception;

	protected abstract void evaluateCA(EvaluationMethod evaluation_method,
			Evaluation evaluation) throws Exception;

	protected abstract DataInstances getPredictions(Instances test,
			DataInstances onto_test);

	public abstract String getAgentType();

	// public boolean loadAgent(String agentName);

	protected abstract void getParameters();

	protected boolean registerWithDF() {
		// register with the DF
		if (this.getAID().getLocalName().contains("Service")){
			return false;
		}
		DFAgentDescription description = new DFAgentDescription();
		// the description is the root description for each agent
		// and how we prefer to communicate.
		// description.addLanguages(language.getName());
		// description.addOntologies(ontology.getName());
		// description.addProtocols(InteractionProtocol.FIPA_REQUEST);
		description.setName(getAID());

		// the service description describes a particular service we
		// provide.
		ServiceDescription servicedesc = new ServiceDescription();
		// the name of the service provided (we just re-use our agent name)
		servicedesc.setName(getLocalName());

		// The service type should be a unique string associated with
		// the service.s
		String typeDesc;
		if (state == states.TRAINED) { // add fileName to service description
			typeDesc = getAgentType() + " trained on " + trainFileName;
		} else {
			typeDesc = getAgentType();
		}
		servicedesc.setType(typeDesc);

		// the service has a list of supported languages, ontologies
		// and protocols for this service.
		// servicedesc.addLanguages(language.getName());
		// servicedesc.addOntologies(ontology.getName());
		// servicedesc.addProtocols(InteractionProtocol.FIPA_REQUEST);

		description.addServices(servicedesc);

		// add "computing agent service"
		ServiceDescription servicedesc_g = new ServiceDescription();

		servicedesc_g.setName(getLocalName());
		servicedesc_g.setType("ComputingAgent");
		description.addServices(servicedesc_g);

		// register synchronously registers us with the DF, we may
		// prefer to do this asynchronously using a behaviour.
		// System.out.println("DF: "+DFService.);

		try {
			DFService.register(this, description);
			System.out.println(getLocalName()
					+ ": successfully registered with DF; service type: "
					+ typeDesc);
			return true;
		} catch (FIPAException e) {
			System.err.println(getLocalName()
					+ ": error registering with DF, exiting:" + e);
			// doDelete();
			return false;

		}
	} // end registerWithDF

	protected void deregisterWithDF() {
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			System.err.println(getLocalName()
					+ " failed to deregister with DF.");
			// doDelete();
		}
	} // end deregisterWithDF

	protected ACLMessage sendOptions(ACLMessage request) {
		ACLMessage msgOut = request.createReply();
		msgOut.setPerformative(ACLMessage.INFORM);
		try {
			// Prepare the content
			ContentElement content = getContentManager()
					.extractContent(request); // TODO exception block?
			Result result = new Result((Action) content, agent_options);
			// result.setValue(options);

			try {
				// Let JADE convert from Java objects to string
				getContentManager().fillContent(msgOut, result);
				// send(msgOut);
			} catch (CodecException ce) {
				ce.printStackTrace();
			} catch (OntologyException oe) {
				oe.printStackTrace();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return msgOut;

	} // end SendOptions

	@Override
	protected void setup() {

		initDefault();

        if (containsArgument(CLASS_NAME)) {
            className = getArgumentValue(CLASS_NAME);
        }
        if (isArgumentValueTrue("load")) {
            // TODO loadAgent(getLocalName());
            // args = new String[0]; // arguments are empty
        }
        // some important initializations before registering
        getParameters();

        java.util.ArrayList<String> typeDescList = new java.util.ArrayList< String >();
		typeDescList.add("ComputingAgent");

        String typeDesc;
		if (state == states.TRAINED) { // add fileName to service description
			typeDesc = getAgentType() + " trained on " + trainFileName;
		} else {
			typeDesc = getAgentType();
            typeDescList.add(typeDesc);
		}

		registerWithDF(typeDescList);
		
		
		if (!newAgent) {
			resurrected = true;
			System.out.println(getLocalName() + " resurrected.");
			taskFIFO = new LinkedList<ACLMessage>();
			execution_behaviour.reset();
			state = states.TRAINED;
			return;
		}
		newAgent = false;



		addBehaviour(send_options_behaviour = new RequestServer(this));
		addBehaviour(execution_behaviour = new ProcessAction(this));

	} // end setup

	public boolean setOptions(pikater.ontology.messages.Task task) {
		/*
		 * INPUT: task with weka options Fills the OPTIONS array and
		 * current_task.
		 */
		current_task = task;
		OPTIONS = task.getAgent().optionsToString().split("[ ]+");

		return true;
	} // end loadConfiguration

	public String getOptions() {
		// write out OPTIONS

		String strOPTIONS = "";
		strOPTIONS += "OPTIONS:";
		for (int i = 0; i < OPTIONS.length; i++) {
			strOPTIONS += " " + OPTIONS[i];
		}
		return strOPTIONS;
	}

	protected ACLMessage sendGetDataReq(String fileName) {
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

		    // System.out.println(getLocalName() + ": using " + reader + ", filename: " + fileName);
			
			// request
			msgOut = new ACLMessage(ACLMessage.REQUEST);
			msgOut.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			// msgOut.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
			msgOut.setLanguage(codec.getName());
			msgOut.setOntology(ontology.getName());
			msgOut.addReceiver(reader);
			msgOut.setConversationId("get-data_" + convId++);
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
	} // end sendGetDataReq

	public static byte[] toBytes(Object object) throws Exception {
		java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
		java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
		oos.writeObject(object);

		return baos.toByteArray();
	}


	/*
	 * Send partial results to the GUI Agent(s) call it after training or during
	 * training?
	 */
	protected void sendResultsToGUI(Boolean first_time, Task _task,
			List _evaluations) {
		ACLMessage msgOut = new ACLMessage(ACLMessage.INFORM);
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("GUIAgent");
		template.addServices(sd);
		try {
			DFAgentDescription[] gui_agents = DFService.search(this, template);
			for (int i = 0; i < gui_agents.length; ++i) {
				msgOut.addReceiver(gui_agents[i].getName());
			}
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		msgOut.setConversationId("partial-results");

		PartialResults content = new PartialResults();
		content.setResults(_evaluations);
		// content.setTask_id(_task.getId());
		if (first_time) {
			content.setTask(_task);
		}
		try {
			getContentManager().fillContent(msgOut, content);
		} catch (CodecException e) {
			e.printStackTrace();
		} catch (OntologyException e) {
			e.printStackTrace();
		}

		send(msgOut);
	}

	protected class RequestServer extends CyclicBehaviour {
		/**
			 *
			 */
		private static final long serialVersionUID = 1074564968341084444L;

		private MessageTemplate CFPproposalMsgTemplate = MessageTemplate.and(
			MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
			MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.CFP),
			MessageTemplate.and(MessageTemplate.MatchLanguage(codec.getName()),
			MessageTemplate.MatchOntology(ontology.getName()))));

		private MessageTemplate CFPreqMsgTemplate = MessageTemplate.and(
			MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
			MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
			MessageTemplate.and(MessageTemplate.MatchLanguage(codec.getName()),
			MessageTemplate.MatchOntology(ontology.getName()))));

		private MessageTemplate reqMsgTemplate = MessageTemplate.and(
			MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
			MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
			MessageTemplate.and(MessageTemplate.MatchLanguage(codec.getName()),
			MessageTemplate.MatchOntology(ontology.getName()))));
		
		public RequestServer(Agent agent) {
			super(agent);
		}

		// TODO: will we accept or refuse the request? (working, size of
		// taksFIFO, latency time...)
		boolean acceptTask() {
			return true/* taskFIFO.size()<=MAX_TASKS */;
		}

		ACLMessage processExecute(ACLMessage req) {
			ACLMessage result_msg = req.createReply();
			if (acceptTask()) {
				result_msg.setPerformative(ACLMessage.AGREE);
				taskFIFO.addLast(req);
				
			 	if (taskFIFO.size() == 1){
					if (!execution_behaviour.isRunnable()) {
						execution_behaviour.restart();
					}
			 	}
				/*
			 	if (!execution_behaviour.isRunnable()) {
					execution_behaviour.restart();
				}
				*/
			} else {
				result_msg.setPerformative(ACLMessage.REFUSE);
				result_msg.setContent("(Computing agent overloaded)");
			}
			return result_msg;
		}

		@Override
		public void action() {			
			
						
				ContentElement content;
				try {				
                    ACLMessage req = receive(reqMsgTemplate);
					if (req != null) {
						content = getContentManager().extractContent(req);					
						if (((Action) content).getAction() instanceof GetOptions) {						
							ACLMessage result_msg = sendOptions(req);
							send(result_msg);
							return;
						}
		
						ACLMessage result_msg = req.createReply();
						result_msg.setPerformative(ACLMessage.NOT_UNDERSTOOD);
						send(result_msg);
						return;
					}
                                
                    ACLMessage CFPproposal = receive(CFPproposalMsgTemplate);
					if (CFPproposal != null){
                        content = getContentManager().extractContent(CFPproposal);
						if (((Action) content).getAction() instanceof Execute) {						
							ACLMessage propose = CFPproposal.createReply();
							propose.setPerformative(ACLMessage.PROPOSE);
							int size = taskFIFO.size();
							if (engaged) {size++;}
							propose.setContent(Integer.toString(size));
							engaged = true;
							send(propose);
							return;
						}
					}
					
                    ACLMessage CFPreq = receive(CFPreqMsgTemplate);
					if (CFPreq != null){					
						engaged = false;
						content = getContentManager().extractContent(CFPreq);
						if (((Action) content).getAction() instanceof Execute) {												
							send(processExecute(CFPreq));
						}
						return;
					}
				} catch (CodecException ce) {
					ce.printStackTrace();
				} catch (OntologyException oe) {
					oe.printStackTrace();
				}
		}
	}

	private class ProcessAction extends FSMBehaviour {
		/**
			 * 
			 */
		private static final long serialVersionUID = 7417933314402310322L;
		private static final String INIT_STATE = "Init";
		private static final String GETTRAINDATA_STATE = "GetTrainingData";
		private static final String GETTESTDATA_STATE = "GetTestData";
		private static final String GETLABELDATA_STATE = "GetLabelData";
		private static final String TRAINTEST_STATE = "TrainTest";
		private static final String SENDRESULTS_STATE = "SendResults";
		private static final int NEXT_JMP = 0;
		private static final int LAST_JMP = 1;
		ACLMessage incoming_request;
		ACLMessage result_msg;
		Execute execute_action;
		boolean success;
		pikater.ontology.messages.Evaluation eval = new Evaluation();
		String train_fn;
		String test_fn;
		String label_fn;
		String output;
		String mode;

		/* Resulting message: FAILURE */

		void failureMsg(String desc) {
			List evaluations = new ArrayList();
			
			Eval er = new Eval();
			er.setName("error_rate");
			er.setValue(Float.MAX_VALUE);
			evaluations.add(er);
			
			// set duration to max_float
			Eval du = new Eval();
			du.setName("duration");
			du.setValue(Integer.MAX_VALUE);
			evaluations.add(du);			

			// set start to now
			Eval st = new Eval();
			st.setName("start");
			st.setValue(System.currentTimeMillis());
			evaluations.add(st);
			
			eval.setEvaluations(evaluations);
						
			eval.setStatus(desc);
		}

		/* Get a message from the FIFO of tasks */
		boolean getRequest() {			
			if (taskFIFO.size() > 0) {
				incoming_request = taskFIFO.removeFirst();
				try {
					ContentElement content = getContentManager()
							.extractContent(incoming_request);
					execute_action = (Execute) ((Action) content).getAction();
					return true;
				} catch (CodecException ce) {
					ce.printStackTrace();
				} catch (OntologyException oe) {
					oe.printStackTrace();
				}
			} else {
				block();
			}

			return false;
		}

		/* Extract data from INFORM message (ARFF reader) */
		pikater.ontology.messages.DataInstances processGetData(ACLMessage inform) {
			ContentElement content;
			try {
				content = getContentManager().extractContent(inform);
				if (content instanceof Result) {
					Result result = (Result) content;
					if (result.getValue() instanceof pikater.ontology.messages.DataInstances) {
						return (pikater.ontology.messages.DataInstances) result
								.getValue();
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

		ProcessAction(Agent a) {
			super(a);
			/* FSM: register states */
			// init state

			registerFirstState(new Behaviour(a) {
				int next;
				boolean cont;

				@Override
				public void action() {
					result_msg = null;
					execute_action = null;
					if (!getRequest()) {
						// no task to execute
						cont = true;
						// block();
						return;
					}
					cont = false;
					if (!resurrected) {
						state = Agent_ComputingAgent.states.NEW;
					}
					// Set options
					setOptions(execute_action.getTask());
					
					// set agent name in Task
					pikater.ontology.messages.Agent agent = current_task.getAgent();
					agent.setName(getLocalName());
					current_task.setAgent(agent);
					
					eval = new Evaluation();
					success = true;
					Data data = execute_action.getTask().getData();
					output = data.getOutput();
					mode = data.getMode();
					
					train_fn = data.getTrain_file_name();
					AchieveREInitiator get_train_behaviour = (AchieveREInitiator) ((ProcessAction) parent).getState(GETTRAINDATA_STATE);
					
					// get_train_behaviour.reset(sendGetDataReq(train_fn));
										
					if (!train_fn.equals(trainFileName)) {
						get_train_behaviour.reset(sendGetDataReq(train_fn));
					} else {
						// We have already the right data
						get_train_behaviour.reset(null);
					}
					

					if (!mode.equals("train_only")) {
						test_fn = data.getTest_file_name();
						AchieveREInitiator get_test_behaviour = (AchieveREInitiator) ((ProcessAction) parent)
								.getState(GETTESTDATA_STATE);
						if (!test_fn.equals(testFileName)) {
							get_test_behaviour.reset(sendGetDataReq(test_fn));
						} else {
							// We have already the right data
							get_test_behaviour.reset(null);
						}
					}
					
					if (data.getLabel_file_name() != null) {
						label_fn = data.getLabel_file_name();
						AchieveREInitiator get_label_behaviour = (AchieveREInitiator) ((ProcessAction) parent)
								.getState(GETLABELDATA_STATE);
						if (!label_fn.equals(labelFileName)) {
							get_label_behaviour.reset(sendGetDataReq(label_fn));
						} else {
							// We have already the right data
							get_label_behaviour.reset(null);
						}
					}
				}

				@Override
				public boolean done() {
					return !cont;
				}
			}, INIT_STATE);

			// get train data state
			registerState(new AchieveREInitiator(a, null) {
				public int next = NEXT_JMP;

				@Override
				protected void handleInform(ACLMessage inform) {
					pikater.ontology.messages.DataInstances _train = processGetData(inform);
					if (_train != null) {
						trainFileName = train_fn;
						onto_train = _train;
						train = onto_train.toWekaInstances();
						train.setClassIndex(train.numAttributes() - 1);
						next = NEXT_JMP;
						return;
					} else {
						next = LAST_JMP;
						failureMsg("No train data received from the reader agent: Wrong content.");
						return;
					}
				}

				@Override
				protected void handleFailure(ACLMessage failure) {
					failureMsg("No train data received from the reader agent: Reader Failed.");
					next = LAST_JMP;
				}

				@Override
				public int onEnd() {
					int next_val = next;
					next = NEXT_JMP;
					return next;
				}
			}, GETTRAINDATA_STATE);

			// get test data state
			registerState(new AchieveREInitiator(a, null) {
				public int next = NEXT_JMP;

				@Override
				protected void handleInform(ACLMessage inform) {
					pikater.ontology.messages.DataInstances _test = processGetData(inform);
					if (_test != null) {
						testFileName = test_fn;
						onto_test = _test;
						test = onto_test.toWekaInstances();
						test.setClassIndex(test.numAttributes() - 1);

						next = NEXT_JMP;
						return;
					} else {
						next = LAST_JMP;
						failureMsg("No test data received from the reader agent: Wrong content.");
						return;
					}

				}

				@Override
				protected void handleFailure(ACLMessage failure) {
					failureMsg("No test data received from the reader agent: Reader Failed.");
					next = LAST_JMP;
				}

				@Override
				public int onEnd() {
					int next_val = next;
					next = NEXT_JMP;
					return next;
				}
			}, GETTESTDATA_STATE);

			// get label data state
			registerState(new AchieveREInitiator(a, null) {
				public int next = NEXT_JMP;

				@Override
				protected void handleInform(ACLMessage inform) {
					pikater.ontology.messages.DataInstances _label = processGetData(inform);
					if (_label != null) {
						labelFileName = label_fn;
						onto_label = _label;
						label = onto_label.toWekaInstances();
						label.setClassIndex(label.numAttributes() - 1);
						next = NEXT_JMP;
						return;
					} else {
						next = LAST_JMP;
						failureMsg("No label data received from the reader agent: Wrong content.");
						return;
					}
				}

				@Override
				protected void handleFailure(ACLMessage failure) {
					failureMsg("No label data received from the reader agent: Reader Failed.");
					next = LAST_JMP;
				}

				@Override
				public int onEnd() {
					int next_val = next;
					next = NEXT_JMP;
					return next;
				}
			}, GETLABELDATA_STATE);

			// Train&test&label state
			registerState(new Behaviour(a) {

				@Override
				public void action() {
					try {

						List labeledData = new ArrayList();
						
						eval = new Evaluation();
						
						eval.setEvaluations(new ArrayList());
						// Date start = new Date();
						Date start = null;
						if (state != Agent_ComputingAgent.states.TRAINED) {
							start = train(eval);
						} else if (!resurrected) {
							if (!mode.equals("test_only")) {
								start = train(eval);
							}
						}
						eval.setStart(start);
						// Date end = new Date();
						// int duration = (int) (end.getTime() - start.getTime());
						
						
						List test_evals = new ArrayList();
						if (state == Agent_ComputingAgent.states.TRAINED) {
							EvaluationMethod evaluation_method = execute_action.getTask().getEvaluation_method();
							
							if (!mode.equals("train_only")) {
								evaluateCA(evaluation_method, eval);							
							
								if (output.equals("predictions")) {
									DataInstances di = new DataInstances();
									di.fillWekaInstances(test);
									labeledData.add(getPredictions(test, di));
									if (!labelFileName.equals("")) {
										di = new DataInstances();
										di.fillWekaInstances(label);
										labeledData.add(getPredictions(label, di));
									}
									eval.setLabeled_data(labeledData);
								}
							}														
						}

					} catch (Exception e) {
						success = false;
						working = false;
						failureMsg(e.getMessage());
						System.out.println(getLocalName() + ": Error: " + e.getMessage() + ".");
						// System.err.println(getLocalName() + ": Error: " + e.getMessage() + ".");
						// e.printStackTrace();
					}
				}

				@Override
				public boolean done() {
					return (state == Agent_ComputingAgent.states.TRAINED) || !success;
				}
			}, TRAINTEST_STATE);

			// send results state
			registerState(new OneShotBehaviour(a) {
				@Override
				public void action() {

					if (success && (result_msg == null)) {
						// save agent every time it executes a task
						/*
						 * String objectFilename = null; try { // resurrected =
						 * false; objectFilename = save(); } catch
						 * (CodecException e) { // TODO Auto-generated catch
						 * block e.printStackTrace(); } catch (OntologyException
						 * e) { // TODO Auto-generated catch block
						 * e.printStackTrace(); } catch (IOException e) { //
						 * TODO Auto-generated catch block e.printStackTrace();
						 * } catch (FIPAException e) { // TODO Auto-generated
						 * catch block e.printStackTrace(); }
						 */

						if ((current_task.getSave_mode() != null && current_task
								.getSave_mode().equals("file")) && !resurrected) {
							try {
								String objectFilename = saveAgentToFile();
								eval.setObject_filename(objectFilename);

							} catch (CodecException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (OntologyException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (FIPAException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						if ((current_task.getSave_mode() != null && current_task
								.getSave_mode().equals("message"))) {
							try {
								eval.setObject(getAgentObject());
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
					
					result_msg = incoming_request.createReply();
					result_msg.setPerformative(ACLMessage.INFORM);
										
					ContentElement content;
					try {
						content = getContentManager().extractContent(incoming_request);
						
						// Prepare the content: Result with current task & filled in evaluaton
						if (resurrected) { eval.setObject(null); };
						current_task.setResult(eval);
						
						List results = new ArrayList();
						results.add(current_task);
						Result result = new Result((Action) content, results);
						getContentManager().fillContent(result_msg, result);
						
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
			
					if (current_task.getGet_results().equals("after_each_task")) {								
						result_msg.addReceiver(new AID(current_task.getGui_agent(), false));
					}			
					current_task.setFinish(getDateTime());
					
					send(result_msg);					
					
					
					if (taskFIFO.size() > 0){
						execution_behaviour.restart();
					}
					
				}
			}, SENDRESULTS_STATE);

			/* FSM: register transitions */
			// init state transition
			registerDefaultTransition(INIT_STATE, GETTRAINDATA_STATE);

			// get train data transitions
			registerTransition(GETTRAINDATA_STATE, GETTESTDATA_STATE, NEXT_JMP);
			registerTransition(GETTRAINDATA_STATE, SENDRESULTS_STATE, LAST_JMP);

			// get test data transitions
			registerTransition(GETTESTDATA_STATE, GETLABELDATA_STATE, NEXT_JMP);
			registerTransition(GETTESTDATA_STATE, SENDRESULTS_STATE, LAST_JMP);

			// get label data transition
			registerTransition(GETLABELDATA_STATE, TRAINTEST_STATE, NEXT_JMP);
			registerTransition(GETLABELDATA_STATE, SENDRESULTS_STATE, LAST_JMP);

			// train&test state transition
			registerDefaultTransition(TRAINTEST_STATE, SENDRESULTS_STATE);

			// backward transition: reset all states
			registerDefaultTransition(SENDRESULTS_STATE, INIT_STATE,
					new String[] { INIT_STATE, GETTRAINDATA_STATE,
							GETTESTDATA_STATE, GETLABELDATA_STATE,
							TRAINTEST_STATE, SENDRESULTS_STATE });
		}
	}

	private byte[] getAgentObject() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(this);
		oos.flush();
		oos.close();

		byte[] data = bos.toByteArray();
		return data;
	}

	private pikater.ontology.messages.Agent getAgentWithFilledObject()
			throws IOException {

		pikater.ontology.messages.Agent savedAgent = current_task.getAgent();
		savedAgent.setObject(getAgentObject());

		return savedAgent;
	}

	private String saveAgentToFile() throws IOException, CodecException,
			OntologyException, FIPAException {

		pikater.ontology.messages.SaveAgent saveAgent = new pikater.ontology.messages.SaveAgent();

		saveAgent.setAgent(getAgentWithFilledObject());

		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.addReceiver(new AID("agentManager", false));
		request.setOntology(MessagesOntology.getInstance().getName());
		request.setLanguage(codec.getName());
		request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

		Action a = new Action();
		a.setActor(this.getAID());
		a.setAction(saveAgent);

		getContentManager().fillContent(request, a);
		ACLMessage reply = FIPAService.doFipaRequestClient(this, request);

		String objectFilename = reply.getContent();

		return objectFilename;
	}
	
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        Date date = new Date();
        return dateFormat.format(date);
    }

};