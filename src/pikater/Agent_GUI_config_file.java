package pikater;

import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.domain.FIPAException;
import jade.util.leap.Iterator;
import jade.util.leap.List;

import java.io.IOException;

import org.jdom.JDOMException;

import pikater.ontology.messages.*;

public class Agent_GUI_config_file extends Agent_GUI {

	private static final long serialVersionUID = -709390383325209787L;

	private String configFileName;

    private final String CONFIG = "config";

    @Override
	protected void displayResult(List tasks, String type) {
        // possible types: result_after_task, all

        if (tasks != null) {
            Iterator itr = tasks.iterator();
            while (itr.hasNext()) {
                Task task = (Task) itr.next();

                Float error_rate = null;
                Iterator ev_itr = task.getResult().getEvaluations().iterator();
                while (ev_itr.hasNext()) {
                    Eval next_eval = (Eval) ev_itr.next();
                    if (next_eval.getName().equals("error_rate")){
                        error_rate = next_eval.getValue();
                    }
                }
                log("Options for agent "
                        + task.getAgent().getName() + " were "
                        + task.getAgent().optionsToString()
                        + ", dataset: "
                        + task.getData().getExternal_train_file_name()
                        + ", error_rate: "
                        + error_rate);
            }
        } else {
            log("There were no tasks in the computation.");
        }
	}

    @Override
    protected void displayFailure(AID agent, String message) {
        log(agent.getName() + ": " + message);
    }

	@Override
	protected void DisplayWrongOption(int problemGuiId, String agentName,
			String optionName, String errorMessage) {
		log(problemGuiId + " " + agentName + " " + optionName + " " + errorMessage);
	}

	@Override
	protected void allOptionsReceived(int problem_id) {
		try {
			sendProblem(problem_id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected String getAgentType() {
		return "GUI_config_file";
	}

    @Override
    protected void displayPartialResult() {
        log("Partial results");
    }

    @Override
    protected void displayFileImportProgress(int completed, int all) {
        // throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
	protected void mySetup() {
		setDefault_error_rate(0.01);

		doWait(10000);

		configFileName = getArgumentValue(CONFIG);
		try {
			 log("xml file: " + System.getProperty("user.dir") + System.getProperty("file.separator") + configFileName);
			 getProblemsFromXMLFile(configFileName);
		}
		// indicates a well-formedness error
		catch (JDOMException e) {
			logError(configFileName + " is not well-formed. " + e.getMessage());
		} catch (IOException e) {
			logError("Could not check " + configFileName + " because " + e.getMessage());
		}
		
	} // end mySetup


    private Boolean _test(){
        try {
            log("J48 options: " + getOptions("J48"));

            int newId = createNewProblem("1000", "after_each_computation", "yes", "test");
            addAgentToProblem(newId, null, "MultilayerPerceptron", "-L 0.4 -D -M ? -H ?,?");
            addAgentToProblem(newId, null, "RBFNetwork", "-B 4");
            addAgentToProblem(newId, null, "?", null);

            addDatasetToProblem(newId, "iris.arff", "iris.arff", null, "evaluation_only", "train_test");

            getAgentOptions("mp1");

            log("Agent types: " + offerAgentTypes());
        } catch (CodecException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return false;
        } catch (OntologyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return false;
        } catch (FIPAException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return false;
        }

        return true;
    }

    private Boolean _test_loading(){

        Agent a = new Agent();
        a.setName("1_RBFNetwork0_2011-05-17_09-01-32.263");
        a.setGui_id("pokusny oziveny agent");

        /*
        try {
            loadAgent("1_RBFNetwork0_2011-05-17_09-01-32.263", null);
        } catch (FIPAException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
            return false;
        }
        */
        return true;
    }


    private Boolean _test_loading2(){
        // test of loading a saved agent

        String agentName = "1_RBFNetwork1_2011-05-22_23-17-33.112";

        Agent a = new Agent();
        a.setName(agentName);
        a.setGui_id("pokusny oziveny agent");

        Data d = new Data();
        d.setMode("test_only");
        d.setTest_file_name("data/files/772c551b8486b932aed784a582b9c1b1");
        d.setTrain_file_name("data/files/772c551b8486b932aed784a582b9c1b1"); // weather

        // d.setTest_file_name("data/files/25d7d5d689042a3816aa1598d5fd56ef");
        // d.setTrain_file_name("data/files/25d7d5d689042a3816aa1598d5fd56ef"); // iris
        d.setExternal_test_file_name("weather.arff");
        d.setExternal_train_file_name("weather.arff");
        d.setOutput("predictions");

        Task t = new Task();
        t.setAgent(a);
        t.setData(d);

        // t.setId("pokusny task pro pokusneho oziveneho agenta");
        // t.setProblem_id("neni soucasti zadneho problemu");

        Execute ex = new Execute();
        ex.setTask(t);

        try {
            loadAgent(agentName, ex, null);
        } catch (FIPAException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
            return false;
        }
        return true;
    }

}
