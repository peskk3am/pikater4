package pikater.agents.management;

import jade.content.lang.sl.SLCodec;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;
import pikater.agents.PikaterAgent;
import pikater.configuration.AgentConfiguration;
import pikater.configuration.Configuration;
import pikater.configuration.ConfigurationProvider;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Agent_Initiator extends PikaterAgent {
	
	private static final long serialVersionUID = -3908734088006529947L;

	@Override
	protected void setup() {
		//Register the SL content language
		getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL);
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		// read agents from configuration
		try {
			/* Sets up a configuration provider via spring */
            ConfigurationProvider configProvider= (ConfigurationProvider) context.getBean("configuration");
            Configuration configuration=configProvider.getConfiguration();
            List<AgentConfiguration> agentConfigurations=configuration.getAgentConfigurations();
            for (AgentConfiguration agentConfiguration : agentConfigurations)
            {
                this.CreateAgent(agentConfiguration.getAgentType(),agentConfiguration.getAgentName(),new Object[0]);
            }

		} catch (ArrayIndexOutOfBoundsException e) {
			/*
			 * If no file was passed on the command line, this exception is
			 * generated. A message indicating how to the class should be called
			 * is displayed
			 */
			System.out.println(getLocalName() + ": no init file specified.");
			e.printStackTrace();

		} catch (Exception e) {
			// If another exception is generated, print a stack trace
			e.printStackTrace();
		}
		
		addBehaviour(new TickerBehaviour(this, 60000) {
		  Calendar cal;
		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			  
		  protected void onTick() {
			  cal = Calendar.getInstance();
			  System.out.println(myAgent.getLocalName()+": tick="+getTickCount()+" time="+sdf.format(cal.getTime()));
		  }
		});
	}

	public int CreateAgent(String type, String name, Object[] args) {
		// get a container controller for creating new agents
		PlatformController container = getContainerController();
		try {
			AgentController agent = container.createNewAgent(name, type, args);
			agent.start();
			// provide agent time to register with DF etc.
			doWait(300);
		} catch (ControllerException e) {
			System.err.println("Exception while adding agent: " + e);
			e.printStackTrace();
			return 0;
		}
		return 1;
	}
}