package pikater;

import java.io.*;
import java.util.Vector;

import pikater.gui.java.MyWekaOption;
import pikater.ontology.messages.Computation;
import pikater.ontology.messages.Compute;
import pikater.ontology.messages.Evaluation;
import pikater.ontology.messages.Execute;
import pikater.ontology.messages.ExecuteParameters;
import pikater.ontology.messages.GetNextParameters;
import pikater.ontology.messages.GetOptions;
import pikater.ontology.messages.Interval;
import pikater.ontology.messages.Options;
import pikater.ontology.messages.MessagesOntology;
import pikater.ontology.messages.Option;
import pikater.ontology.messages.Results;
import pikater.ontology.messages.Solve;
import pikater.ontology.messages.Task;
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
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;
import jade.proto.SubscriptionResponder.Subscription;
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.List;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;
import jade.wrapper.StaleProxyException;

public abstract class Agent_Search extends Agent {	

	private static final long serialVersionUID = 8637677510056974015L;
	private Codec codec = new SLCodec();
	private Ontology ontology = MessagesOntology.getInstance();

	private List search_options = null;
	private List options = null;
	
	protected abstract String getAgentType();
	protected abstract List generateNewOptions(List options, List evaluations); //returns List of Options
	protected abstract boolean finished();
	protected abstract void updateFinished(List evaluations);
	protected abstract void loadSearchOptions(); // load the appropriate options before sending the first parameters
	
	protected ACLMessage getParameters(ACLMessage request) {
		ACLMessage reply = request.createReply();
		
		pikater.ontology.messages.Agent agent = null;

		String optPath = System.getProperty("user.dir") +
			System.getProperty("file.separator") + "options" + 
			System.getProperty("file.separator") + getAgentType() + ".opt";

		// read options from file
		try {
			/* Sets up a file reader to read the options file */
			FileReader input = new FileReader(optPath);
			/*
			 * Filter FileReader through a Buffered read to read a line at a
			 * time
			 */
			BufferedReader bufRead = new BufferedReader(input);

			String line; // String that holds current file line
			int count = 0; // Line number of count
			// Read first line
			line = bufRead.readLine();
			count++;

			// list of ontology.messages.Option
			List _options = new ArrayList();
			agent = new pikater.ontology.messages.Agent();
			agent.setName(getLocalName());
			agent.setType(getAgentType());
			
			// Read through file one line at time. Print line # and line
			while (line != null) {
				System.out.println("    " + count + ": " + line);

				// parse the line
				String delims = "[ ]+";
				String[] params = line.split(delims, 11);

				if (params[0].equals("$")) {
					
					String dt = null; 										
					if (params[2].equals("boolean")) {
						dt = "BOOLEAN";
					}
					if (params[2].equals("float")) {
						dt = "FLOAT";
					}
					if (params[2].equals("int")) {
						dt = "INT";
					}
					if (params[2].equals("mixed")) {
						dt = "MIXED";
					}					
					
					float numArgsMin;
					float numArgsMax;
					float rangeMin = 0;
					float rangeMax = 0;
					String range;
					List set = null;
					
					if (dt.equals("BOOLEAN")){
						numArgsMin = 1;
						numArgsMax = 1;
						range = null;						
					}
					else{
						numArgsMin = Float.parseFloat(params[3]);
						numArgsMax = Float.parseFloat(params[4]);
						range = params[5];

						if (range.equals("r")){
							rangeMin = Float.parseFloat(params[6]);
							rangeMax = Float.parseFloat(params[7]);
						}
						if (range.equals("s")){
							set = new ArrayList();
							String[] s = params[6].split("[ ]+");
							for (int i=0; i<s.length; i++){
								set.add(s[i]);
							}
						}
					}
					
					Option o = new Option(params[1], dt,
							numArgsMin, numArgsMax,
							range, rangeMin, rangeMax, set,
							params[params.length-3],
							params[params.length-2],
							params[params.length-1]);
					
					_options.add(o);
					
				}

				line = bufRead.readLine();

				count++;
			}
			agent.setOptions(_options);
			bufRead.close();

			reply.setPerformative(ACLMessage.INFORM);

			// Prepare the content
			ContentElement content = getContentManager()
					.extractContent(request); // TODO exception block?
			Result result = new Result((Action) content, agent);

			getContentManager().fillContent(reply, result);
			
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			reply.setPerformative(ACLMessage.FAILURE);
			reply.setContent(e.getMessage());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			reply.setPerformative(ACLMessage.FAILURE);
			reply.setContent(e.getMessage());
		} catch (CodecException e) {
			e.printStackTrace();
			reply.setPerformative(ACLMessage.FAILURE);
			reply.setContent(e.getMessage());
		} catch (OntologyException e) {
			e.printStackTrace();
			reply.setPerformative(ACLMessage.FAILURE);
			reply.setContent(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			reply.setPerformative(ACLMessage.FAILURE);
			reply.setContent(e.getMessage());
		}
		
		return reply;
	} // end getParameters
	
	protected List getOptions() {
		if(options != null){
			return options;
		}else{
			return new ArrayList();
		}
		
	}
	protected List getSearch_options() {
		if(search_options!=null){
			return search_options;
		}else{
			return new ArrayList();
		}
	}
	
	protected boolean registerWithDF() {
		// register with the DF

		DFAgentDescription description = new DFAgentDescription();
		// the description is the root description for each agent
		// and how we prefer to communicate.

		description.setName(getAID());
		// the service description describes a particular service we
		// provide.
		ServiceDescription servicedesc = new ServiceDescription();
		// the name of the service provided (we just re-use our agent name)
		servicedesc.setName(getLocalName());

		// The service type should be a unique string associated with
		// the service.s
		String typeDesc = getAgentType();

		servicedesc.setType(typeDesc);

		// the service has a list of supported languages, ontologies
		// and protocols for this service.
		// servicedesc.addLanguages(language.getName());
		// servicedesc.addOntologies(ontology.getName());
		// servicedesc.addProtocols(InteractionProtocol.FIPA_REQUEST);

		description.addServices(servicedesc);

		// add "OptionsManager agent service"
		ServiceDescription servicedesc_g = new ServiceDescription();

		servicedesc_g.setName(getLocalName());
		servicedesc_g.setType("Search");
		description.addServices(servicedesc_g);

		// register synchronously registers us with the DF, we may
		// prefer to do this asynchronously using a behaviour.
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
	
	protected class RequestServer extends CyclicBehaviour {
		/**
			 * 
			 */
		private static final long serialVersionUID = 1074564968341084444L;
		private MessageTemplate resMsgTemplate = MessageTemplate
				.and(
						MessageTemplate
								.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
						MessageTemplate.and(MessageTemplate
								.MatchPerformative(ACLMessage.REQUEST),
								MessageTemplate.and(MessageTemplate
										.MatchLanguage(codec.getName()),
										MessageTemplate.MatchOntology(ontology
												.getName()))));

		public RequestServer(Agent agent) {			
			super(agent);
		}

		@Override 
		public void action() {
			
			ACLMessage request = receive(resMsgTemplate);
			if (request != null) {
				try {
					ContentElement content = getContentManager().extractContent(request);
					if (((Action) content).getAction() instanceof GetOptions) {
						ACLMessage result_msg = getParameters(request);
						send(result_msg);
						return;
					}
					if (((Action) content).getAction() instanceof GetNextParameters) {											
						GetNextParameters gnp = (GetNextParameters) (((Action) content).getAction());
						search_options = gnp.getSearch_options();
						options = gnp.getOptions();														
						loadSearchOptions();
						
						List options_new = null;
						List evaluations = null;
						
						while (!finished()){
			                ExecuteParameters ep = new ExecuteParameters();
			                options_new = generateNewOptions(options_new, evaluations);
			                ep.setParameters(options_new); // List of Lists of Options
							
							Action a = new Action();
			                a.setAction(ep);
			                a.setActor(myAgent.getAID());

			                ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
			                req.addReceiver(request.getSender());
			                req.setLanguage(codec.getName());
			                req.setOntology(ontology.getName());
			                req.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

			                getContentManager().fillContent(req, a);
							
							ACLMessage get_next_parameters_results = FIPAService.doFipaRequestClient(myAgent, req);
							// extract List of Evaluations from response
							content = getContentManager().extractContent(get_next_parameters_results);
							if (content instanceof Result) {
								Result result = (Result) content;
								evaluations = (List)((List)result.getValue()).get(1);
								options_new = (List)((List)result.getValue()).get(0);
							}
							updateFinished(evaluations);							
						}
						
						ACLMessage reply = request.createReply();
						reply.setPerformative(ACLMessage.INFORM);
						reply.setContent("finished");
						
						send(reply);
						return;
					}
				} catch (CodecException ce) {
					ce.printStackTrace();
				} catch (OntologyException oe) {
					oe.printStackTrace();
				} catch (FIPAException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ACLMessage result_msg = request.createReply();
				result_msg.setPerformative(ACLMessage.NOT_UNDERSTOOD);
				send(result_msg);
				return;
			} else {
				block();
			}
		}
	}
		
	protected void setup() {

		System.out.println(getLocalName() + " is alive...");

		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);

		registerWithDF();
		
		addBehaviour(new RequestServer(this));
		
	} // end setup()
		
}
