package pikater.agents.recommenders;

import pikater.DataManagerService;
import pikater.ontology.messages.CreateAgent;
import pikater.ontology.messages.Data;
import pikater.ontology.messages.GetMetadata;
import pikater.ontology.messages.MessagesOntology;
import pikater.ontology.messages.Metadata;
import pikater.ontology.messages.Option;
import pikater.ontology.messages.Recommend;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.Agent;
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
import jade.proto.AchieveREResponder;
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.List;

public abstract class Agent_Recommender extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4413578066473667553L;
	
	protected abstract pikater.ontology.messages.Agent chooseBestAgent(Data data);
	protected abstract String getAgentType();

	Codec codec = new SLCodec();
    Ontology ontology = MessagesOntology.getInstance();
        
    // 3 levels:
	// 0 no output
	// 1 minimal
	// 2 normal
	protected int verbosity = 1;    
    
	
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

		// add "Search agent service"
		ServiceDescription servicedesc_g = new ServiceDescription();

		servicedesc_g.setName(getLocalName());
		servicedesc_g.setType("Recommender");
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

	
    @Override
    protected void setup() {

    	println("Agent " + getLocalName() +  " (Agent_Recommender) is alive...", 1, true);
    	
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontology);               
        
        // receive request
        MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchOntology(ontology.getName()), MessageTemplate.MatchPerformative(ACLMessage.REQUEST));        
		addBehaviour(new receiveRequest(this, mt));

    }  // end setup()
         
	
	protected class receiveRequest extends AchieveREResponder {

		/**
		 * 
		 */
		private static final long serialVersionUID = -8353926385111974474L;		
		
		public receiveRequest(Agent a, MessageTemplate mt) {
			super(a, mt);
			// TODO Auto-generated constructor stub
		}

        @Override
        protected ACLMessage handleRequest(ACLMessage request)
        	throws NotUnderstoodException, RefuseException {
        	
        	ACLMessage reply = request.createReply();
            Integer performative = ACLMessage.FAILURE;
            
        	try {        		
        		Action a = (Action) getContentManager().extractContent(request);

                if (a.getAction() instanceof Recommend) {
                    Recommend rec = (Recommend) a.getAction();                        
    				
                    // TODO decide which recommendation method to use acording
                    // to the Recommend options
                    Data data = rec.getData();
                    
                    // Get metadata:
					Metadata metadata = null;    
					
					// either metatada are not yet in ontology		
					if (rec.getData().getMetadata() == null) {
						// or fetch them from database:
						GetMetadata gm = new GetMetadata();
						gm.setInternal_filename(rec.getData().getTest_file_name());
						metadata = DataManagerService.getMetadata(myAgent, gm);
						data.setMetadata(metadata);
					}                            					
					// else TODO - overit, jestli jsou metadata OK, pripadne vygenerovat
					/* boolean hasMetadata = false;
					if (metadata.getNumber_of_attributes() > -1
							&& metadata.getNumber_of_instances() > -1) {
						hasMetadata = true;
						    					
					} */    				
					
                    pikater.ontology.messages.Agent recommended_agent = chooseBestAgent(rec.getData());
                    
                    // fill options
                	recommended_agent.setOptions(mergeOptions(getAgentOptions(recommended_agent.getType()), recommended_agent.getOptions()));

        			println("********** Agent "
        					+ recommended_agent.getType()
        					+ " recommended. Options: "
        					+ recommended_agent.optionsToString()
        					+ "**********", 1, true);            			

                		// Prepare the content of inform message                       
        				Result result = new Result(a, recommended_agent);
        				try {
        					getContentManager().fillContent(reply, result);
        				} catch (CodecException ce) {
        					ce.printStackTrace();
        				} catch (OntologyException oe) {
        					oe.printStackTrace();
        				}

        				performative = ACLMessage.INFORM;
        		}
            } catch (OntologyException e) {
                e.printStackTrace();
            } catch (CodecException e) {
                e.printStackTrace();
			}

            reply.setPerformative(performative);

            return reply;
        }
    }				        
    
	private List mergeOptions(List o1_CA, List o2) {
		List new_options = new ArrayList();
		if (o1_CA != null) {

			// if this type of agent has got some options
			// update the options (merge them)

			// go through the CA options
			// replace the value and add it to the new options
			Iterator o2itr = o2.iterator();
			while (o2itr.hasNext()) {
				Option next_option = (Option) o2itr.next();

				Iterator o1CAitr = o1_CA.iterator();
				while (o1CAitr.hasNext()) {
					Option next_CA_option = (Option) o1CAitr.next();

					if (next_option.getName().equals(next_CA_option.getName())) {
						// copy the value
						next_CA_option.setValue(next_option.getValue());
						if (next_option.getValue().contains("?")){
							// just in case the someone forgot to set opt to mutable
							next_CA_option.setMutable(true);
						}
						else {
							next_CA_option.setMutable(next_option.getMutable());
						}

						new_options.add(next_CA_option);
					}
				}
			}
		}
		return new_options;
	}
	
	private List getAgentOptions(String agentType) {

		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		// find an agent according to type
		List agents = getAgentsByType(agentType);
		request.addReceiver((AID)agents.get(0));

		request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

		request.setLanguage(codec.getName());
		request.setOntology(ontology.getName());

		pikater.ontology.messages.GetOptions get = new pikater.ontology.messages.GetOptions();
		Action a = new Action();
		a.setAction(get);
		a.setActor(this.getAID());

		try {
			// Let JADE convert from Java objects to string
			getContentManager().fillContent(request, a);

			ACLMessage inform = FIPAService.doFipaRequestClient(this, request);

			if (inform == null) {
				return null;
			}

			Result r = (Result) getContentManager().extractContent(inform);

			return ((pikater.ontology.messages.Agent) r.getItems().get(0)).getOptions();

		} catch (CodecException ce) {
			ce.printStackTrace();
		} catch (OntologyException oe) {
			oe.printStackTrace();
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		return null;
	}

	public List getAgentsByType(String agentType) {				
		
		List Agents = new ArrayList(); // List of AIDs
		
		// Make the list of agents of given type
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(agentType);
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			// System.out.println(getLocalName()+": Found the following " + agentType + " agents:");
			
			for (int i = 0; i < result.length; ++i) {
				AID aid = result[i].getName();
				Agents.add(aid);
			}
			
			while (Agents.size() < 1) {
				AID aid = createAgent(agentType, null, null);
				Agents.add(aid);
			}
		} catch (FIPAException fe) {
			fe.printStackTrace();
			return null;
		}
		
		return Agents;
		
	} // end getAgentsByType

	
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
