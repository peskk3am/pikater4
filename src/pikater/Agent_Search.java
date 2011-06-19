package pikater;

import pikater.ontology.messages.Computation;
import pikater.ontology.messages.Compute;
import pikater.ontology.messages.Evaluation;
import pikater.ontology.messages.GetNextParameters;
import pikater.ontology.messages.MessagesOntology;
import pikater.ontology.messages.Option;
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
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import jade.util.leap.ArrayList;
import jade.util.leap.List;

public abstract class Agent_Search extends Agent {	

	private static final long serialVersionUID = 8637677510056974015L;
	private Codec codec = new SLCodec();
	private Ontology ontology = MessagesOntology.getInstance();

	private float error_rate = -1;
	private int maximum_tries = 1;
	private Evaluation evaluation = null;
	
	protected abstract String getAgentType();
	protected abstract void generateNewOptions(List options); // modifies options
	protected abstract boolean finished();

	protected float getError_rate() {
		return error_rate;
	}
	protected float getMaximum_tries() {
		return maximum_tries;
	}
	protected Evaluation getEvaluation() {
		return evaluation;
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

	protected void setup() {

		System.out.println(getLocalName() + " is alive...");

		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);

		registerWithDF();
		
		MessageTemplate mt = MessageTemplate.and(MessageTemplate
				.MatchOntology(ontology.getName()), MessageTemplate
				.MatchPerformative(ACLMessage.REQUEST));
		
		addBehaviour(new SendParameters(this, mt));
		
	} // end setup()
	
	private class SendParameters extends AchieveREResponder {

		public SendParameters(Agent a, MessageTemplate mt) {
			super(a, mt);
			// TODO Auto-generated constructor stub
		}
		
		protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException {
			return null;
		}
		
		protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
			List new_options = null;

			ACLMessage reply = request.createReply();
			reply.setPerformative(ACLMessage.INFORM);
			
			ContentElement content = null;
			try {
				content = getContentManager().extractContent(request);
				if (((Action) content).getAction() instanceof GetNextParameters) {					
					
					GetNextParameters gnp = (GetNextParameters) ((Action) content).getAction();
					evaluation = gnp.getEvaluation();
					List options = gnp.getOptions();
					error_rate = gnp.getError_rate();
					maximum_tries = gnp.getMaximum_tries();

					if (!finished()){
						generateNewOptions(options);
						new_options = options;
					}
					else{
						new_options = new ArrayList();
					}
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

			if (new_options != null && content != null){
				Result result = new Result((Action) content, new_options);
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
			else {
				reply.setPerformative(ACLMessage.FAILURE);
				reply.setContent("New options haven't been generated.");
			}

			return reply;
		}
	} // end SendParameters


	
}
