package pikater;

import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
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
import jade.proto.AchieveREInitiator;
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.List;
import pikater.agents.PikaterAgent;
import pikater.agents.management.ManagerAgentCommunicator;
import pikater.ontology.messages.BoolSItem;
import pikater.ontology.messages.Execute;
import pikater.ontology.messages.ExecuteParameters;
import pikater.ontology.messages.FloatSItem;
import pikater.ontology.messages.GetParameters;
import pikater.ontology.messages.Id;
import pikater.ontology.messages.IntSItem;
import pikater.ontology.messages.Options;
import pikater.ontology.messages.Option;
import pikater.ontology.messages.SearchSolution;
import pikater.ontology.messages.SetSItem;
import pikater.ontology.messages.Task;

import java.util.Random;

public class Agent_OptionsManager extends PikaterAgent {

	private static final long serialVersionUID = 7028866964341806289L;
	
	private List results = new ArrayList();
	
	protected pikater.ontology.messages.Evaluation evaluation;
	protected List Options;
	protected pikater.ontology.messages.Agent Agent;

	private int task_number = 0;
	private Task received_task;
	private ACLMessage received_request = null;
	
	protected String getAgentType() {
		return "OptionManager";
	}
	
	protected class ExecuteTask extends AchieveREInitiator{

        private ACLMessage query; // original query sent by search agent;
                                  // to be able to generate reply

		public ExecuteTask(jade.core.Agent a, ACLMessage msg, ACLMessage query) {
			super(a, msg);
            this.query = query;
		}

		protected void handleRefuse(ACLMessage refuse) {
            log("Agent "+refuse.getSender().getName()+" refused.", 1);
		}
		
		protected void handleFailure(ACLMessage failure) {
			if (failure.getSender().equals(myAgent.getAMS())) {
				// FAILURE notification from the JADE runtime: the receiver
				// does not exist
                log("Responder does not exist", 1);
			}
			else {
                log("Agent "+failure.getSender().getName()+" failed", 1);
			}
		}

		protected void handleInform(ACLMessage inform) {
            log("Agent "+inform.getSender().getName()+" successfully performed the requested action", 2);
			// send result to the search agent:
			
			// extract evaluation from the task in the inform message
            try{
                ContentElement content = getContentManager().extractContent(inform);
                ContentElement query_content = getContentManager().extractContent(query);
                if (content instanceof Result) {
                    Result result = (Result) content;
                    // get the original task from the query
                    List tasks = (List)result.getValue();
                    Task t = (Task) tasks.get(0);

					// save results to the database
					if (t.getSave_results()){						
						DataManagerService.saveResult(myAgent, t);
					}
					
					results.add(t);
					
					// send evaluation to search agent
					ACLMessage reply = query.createReply();
					reply.setPerformative(ACLMessage.INFORM);
					
					Result reply_result = new Result((Action) query_content, t.getResult());
					getContentManager().fillContent(reply, reply_result);
					
					send(reply);													
				}			
			} catch (UngroundedException e) {
				logError(getLocalName() + " ");
				e.printStackTrace();
			} catch (CodecException e) {
                logError(getLocalName() + " ");
				e.printStackTrace();
			} catch (OntologyException e) {
                logError(getLocalName() + " ");
				e.printStackTrace();
			}			
		}
	} // end of ExecuteTask ("send request to planner agent") bahavior


    private AID getPlannerAgent(){
        AID planner = null;

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Planner");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            AID[] Planners = new AID[result.length];
            for (int i = 0; i < result.length; ++i) {
                Planners[i] = result[i].getName();
            }
            if (result.length == 0){
                logError("No planner found!");
            }
            // randomly choose one of the planners (should be just one)
            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(result.length);
            planner = Planners[randomInt];
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        return planner;
    }


	protected ACLMessage createRequest(ACLMessage query) {
        ACLMessage request = null;

		try {
			ContentElement content = getContentManager().extractContent(query);
							
			ExecuteParameters ep = (ExecuteParameters) (((Action) content).getAction());
			
			// there is only one solution at the time
			Options opt = fillOptionsWithSolution(Options, (SearchSolution)(ep.getSolutions().get(0)));
		
			// create CFP message					  		
			request = new ACLMessage(ACLMessage.REQUEST);
            request.setLanguage(codec.getName());
            request.setOntology(ontology.getName());
            request.addReceiver(getPlannerAgent());

			request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

			Execute ex = new Execute();
			// add task id
			Id id = new Id(Integer.toString(task_number));
			Id rtid = received_task.getId();
			rtid.setSubid(id);
			received_task.setId(rtid);
			
			received_task.setNote(Integer.toString(task_number));
			task_number++;
			
			// add the new options to the task
			pikater.ontology.messages.Agent ag = received_task.getAgent();							
			ag.setOptions(opt.getList());							
			received_task.setAgent(ag);
			ex.setTask(received_task);		
										
			Action a = new Action();
			a.setAction(ex);
			a.setActor(this.getAID());
										
			getContentManager().fillContent(request, a);
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
		return request;
	}


	private void ProcessNextQuery(ACLMessage query){
        ACLMessage req = createRequest(query);
        addBehaviour(new ExecuteTask(this, req, query));
	} // end ProcessNextQuery


	protected class RequestServer extends CyclicBehaviour {

		private static final long serialVersionUID = 1902726126096385876L;
        private PikaterAgent agent;

        private MessageTemplate reqMsgTemplate = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                        MessageTemplate.and(MessageTemplate.MatchLanguage(codec.getName()),
                                MessageTemplate.MatchOntology(ontology.getName()))));

		private MessageTemplate queryMsgTemplate = MessageTemplate
				.and(MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_QUERY),
						MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF),
								MessageTemplate.and(MessageTemplate.MatchLanguage(codec.getName()),
										MessageTemplate.MatchOntology(ontology.getName()))));

        private MessageTemplate informMsgTemplate = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                        MessageTemplate.and(MessageTemplate.MatchLanguage(codec.getName()),
                                MessageTemplate.MatchOntology(ontology.getName()))));

		public RequestServer(PikaterAgent agent) {
			super(agent);
            this.agent=agent;
		}

		@Override 
		public void action() {
			
			ACLMessage req = receive(reqMsgTemplate); // Execute (from Manager)
			ACLMessage query = receive(queryMsgTemplate);
            ACLMessage inform = receive(informMsgTemplate); // results (no ?)

            boolean msg_received = false;

			ContentElement content;
			try {				
				if (req != null){
					msg_received = true;
					content = getContentManager().extractContent(req);
					if (((Action) content).getAction() instanceof Execute) {
						received_request = req;
												
						Execute execute = (Execute) (((Action) content).getAction());
						received_task = execute.getTask();

                        Options = received_task.getAgent().getOptions();
						
						List mutableOptions = getMutableOptions(Options);
						
						if (mutableOptions.size() > 0){							
							// create search agent
                            ManagerAgentCommunicator communicator=new ManagerAgentCommunicator("agentManager");
                            String type= execute.getMethod().getType();
                            AID aid=communicator.createAgent(agent,type,null,null);
                            String search_agent_name=aid.getLocalName();

							// send request to the search agent							
							ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
							msg.addReceiver(new AID(search_agent_name, false));
							msg.setLanguage(codec.getName());
							msg.setOntology(ontology.getName());
							msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
	
							GetParameters gp = new GetParameters();
							List schema = convertOptionsToSchema(received_task.getAgent().getOptions());
							gp.setSchema(schema);
							//gp.setOptions(getMutableOptions(computation.getAgent().getOptions()));						
							gp.setSearch_options(execute.getMethod().getOptions());

                            Action a = new Action();
							a.setAction(gp);
							a.setActor(myAgent.getAID());
									
							getContentManager().fillContent(msg, a);	
	
							addBehaviour(new StartGettingParameters(myAgent, msg));
						}
						else{
                            // create CFP message
                            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                            request.setLanguage(codec.getName());
                            request.setOntology(ontology.getName());
                            request.addReceiver(getPlannerAgent());

                            request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

                            Action a = new Action();
                            a.setAction(execute);
                            a.setActor(myAgent.getAID());

                            getContentManager().fillContent(request, a);
                            send(request);
						}
						return;
					}
				}

				if (query != null) {
					msg_received = true;
					// check whether the query is correct
					content = getContentManager().extractContent(query);					
					if (((Action) content).getAction() instanceof ExecuteParameters) {					
						// options manager received options to execute
						ProcessNextQuery(query);
				    }
                }

                if (inform != null){
                    msg_received = true;
                    content = getContentManager().extractContent(inform);
                    if (content instanceof Result) {
                        Result result = (Result) content;
                        // get the original task
                        List tasks = (List)result.getValue();
                        Task t = (Task) tasks.get(0);
                        results.add(t);

                        // save results to the database
                        if (t.getSave_results()){
                            DataManagerService.saveResult(myAgent, t);
                        }

                        sendResultsToManager();
                    }
                }
            } catch (CodecException ce) {
				ce.printStackTrace();
			} catch (OntologyException oe) {
				oe.printStackTrace();
			}
			
			if (! msg_received){
				block();
			}
			
		/* TODO:
		 	ACLMessage result_msg = request.createReply();
		 	result_msg.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			send(result_msg);
			return;
		 */

		}
	}
	

	private class StartGettingParameters extends AchieveREInitiator {

		public StartGettingParameters(Agent a, ACLMessage msg) {
			super(a, msg);
            log("StartGettingParameters behavior created.", 2);
		}

		
		protected void handleInform(ACLMessage inform) {
            log("Agent " + inform.getSender().getName()
					+ ": sending of Options have been finished.", 2);

			// sending of Options have been finished -> send message to Manager
			sendResultsToManager();			
		}
				
		protected void handleRefuse(ACLMessage refuse) {
            log("Agent " + refuse.getSender().getName()
					+ " refused to perform the requested action.", 1);
			// TODO preposlat zpravu managerovi
		}

		protected void handleFailure(ACLMessage failure) {
            log("Agent "+ failure.getSender().getName()
					+ ": failure while performing the requested action", 1);
			// TODO preposlat zpravu managerovi
		}

	};

	@Override
	protected void setup() {
		
		initDefault();
		
		registerWithDF();

		addBehaviour(new RequestServer(this));

	} // end setup
	
	
	private void sendResultsToManager(){

		ACLMessage msgOut = received_request.createReply();
		msgOut.setPerformative(ACLMessage.INFORM);
		
		// prepare the outgoing message content:
				
		ContentElement content;
			try {
				content = getContentManager().extractContent(received_request);
				Result result = new Result((Action) content, results);
				getContentManager().fillContent(msgOut, result);
				
				send(msgOut);
				
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

	
	private List getMutableOptions(List Options){
		List mutable = new ArrayList();
		Iterator itr = Options.iterator();
		while (itr.hasNext()) {
			Option o = (Option) itr.next();
			if (o.getMutable()){				
				mutable.add(o);
			}
		}
		return mutable;
	}
		
	//Create new options from solution with filled ? values (convert solution->options) 
	private Options fillOptionsWithSolution(List options, SearchSolution solution){
		Options res_options = new Options();
		List options_list = new ArrayList();
		if(options==null){
			return res_options;
		}
		//if no solution values to fill - return the option
		if(solution.getValues() == null){
			res_options.setList(options);
			return res_options;
		}
		Iterator sol_itr = solution.getValues().iterator();
		Iterator opt_itr = options.iterator();
		while (opt_itr.hasNext()) {
			Option opt = (Option) opt_itr.next();
			Option new_opt = opt.copyOption();
			if(opt.getMutable())
				new_opt.setValue(fillOptWithSolution(opt, sol_itr));
			options_list.add(new_opt);
		}
		res_options.setList(options_list);
		return res_options;
	}

	//Fill an option's ? with values in iterator
	private String fillOptWithSolution(Option opt, Iterator solution_itr){		
		String res_values = "";
		String[] values = ((String)opt.getUser_value()).split(",");
		int numArgs = values.length;
		for (int i = 0; i < numArgs; i++) {
			if (values[i].equals("?")) {
				res_values+=(String)solution_itr.next();
			}else{
				res_values+=values[i];
			}
			if (i < numArgs-1){
				res_values+=",";
			}
		}
		
		return res_values;
	}
	
	//Create schema of solutions from options (Convert options->schema)
	private List convertOptionsToSchema(List options){
		List new_schema = new ArrayList();
		if(options==null)
			return new_schema;
		Iterator itr = options.iterator();
		while (itr.hasNext()) {
			Option opt = (Option) itr.next();
			if(opt.getMutable())
				addOptionToSchema(opt, new_schema);
		}
		return new_schema;
	}
	
	private void addOptionToSchema(Option opt, List schema){
		String[] values = ((String)opt.getUser_value()).split(",");
		int numArgs = values.length;
		if (!opt.getIs_a_set()) {
			if (opt.getData_type().equals("INT") || opt.getData_type().equals("MIXED")) {
				for (int i = 0; i < numArgs; i++) {
					if (values[i].equals("?")) {
						IntSItem itm = new IntSItem();
						itm.setNumber_of_values_to_try(opt.getNumber_of_values_to_try());
						itm.setMin(opt.getRange().getMin().intValue());
						itm.setMax(opt.getRange().getMax().intValue());
						schema.add(itm);
					}
				}
			}else if (opt.getData_type().equals("FLOAT")) {
				for (int i = 0; i < numArgs; i++) {
					if (values[i].equals("?")) {
						FloatSItem itm = new FloatSItem();
						itm.setNumber_of_values_to_try(opt.getNumber_of_values_to_try());
						itm.setMin(opt.getRange().getMin());
						itm.setMax(opt.getRange().getMax());
						schema.add(itm);
					}
				}
			}else if (opt.getData_type().equals("BOOLEAN")) {
				BoolSItem itm = new BoolSItem();
				itm.setNumber_of_values_to_try(opt.getNumber_of_values_to_try());
				schema.add(itm);
			}
		}else{
			for (int i = 0; i < numArgs; i++) {
				if (values[i].equals("?")) {
					SetSItem itm = new SetSItem();
					itm.setNumber_of_values_to_try(opt.getNumber_of_values_to_try());
					itm.setSet(opt.getSet());
					schema.add(itm);
				}
			}
		}
	}
}