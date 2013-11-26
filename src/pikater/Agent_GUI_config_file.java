package pikater;

import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAException;
import jade.util.leap.Iterator;
import jade.util.leap.List;

import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import org.jdom.input.SAXBuilder;
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

    protected void getProblemsFromXMLFile(String fileName) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        log("GetProblemsFromXMLFile: " + path + fileName);
        Document doc = builder.build("file:" + path + fileName);
        Element root_element = doc.getRootElement();

        java.util.List _end_pikater_when_finished = root_element.getChildren("hasta_la_vista_baby");
        if (_end_pikater_when_finished.size() > 0){
            end_pikater_when_finished = true;
            // take into consideration only the first one
            if ( ((Element)_end_pikater_when_finished.get(0)).getAttributeValue("shutdown_database").equals("true")){
                shutdown_database = true;
            }
        }

        // return all children by name
        java.util.List _problems = root_element.getChildren("experiment");
        java.util.Iterator p_itr = _problems.iterator();
        while (p_itr.hasNext()) {
            Element next_problem = (Element) p_itr.next();

            int p_id = createNewProblem(next_problem.getAttributeValue("timeout"),
                    next_problem.getAttributeValue("get_results"),
                    next_problem.getAttributeValue("save_results"),
                    next_problem.getAttributeValue("name"));

            java.util.List evaluation_method = next_problem.getChildren("evaluation");
            if (evaluation_method.size() == 0){
                throw new JDOMException("evaluation tag missing.");
            }
            if (evaluation_method.size() > 1) {
                throw new JDOMException("more than one evaluation tags found.");
            }

            java.util.Iterator em_itr = evaluation_method.iterator();
            Element next_evaluation_method = (Element) em_itr.next();
            addEvaluationMethodToProblem(p_id, next_evaluation_method.getAttributeValue("name"));

            java.util.List _evaluation_method_options = next_evaluation_method.getChildren("parameter");
            java.util.Iterator emo_itr = _evaluation_method_options.iterator();
            while (emo_itr.hasNext()) {
                Element next_option = (Element) emo_itr.next();
                addEvaluationMethodOption(p_id, next_option.getAttributeValue("name"),
                        next_option.getAttributeValue("value"));
            }

            java.util.List method = next_problem.getChildren("method");
            java.util.Iterator m_itr = method.iterator();
            if (method.size() == 0) {
                throw new JDOMException("method tag missing.");
            }
            if (method.size() > 1) {
                throw new JDOMException("more than one method tags found.");
            }
            Element next_method = (Element) m_itr.next();
            addMethodToProblem(p_id, next_method.getAttributeValue("name"));

            java.util.List _search_options = next_method.getChildren("parameter");
            java.util.Iterator so_itr = _search_options.iterator();
            while (so_itr.hasNext()) {
                Element next_option = (Element) so_itr.next();
                addSearchOption(p_id, next_option.getAttributeValue("name"),
                        next_option.getAttributeValue("value"));
            }

            java.util.List dataset = next_problem.getChildren("dataset");
            java.util.Iterator ds_itr = dataset.iterator();
            while (ds_itr.hasNext()) {
                Element next_dataset = (Element) ds_itr.next();
                int d_id = addDatasetToProblem(p_id,
                        next_dataset.getAttributeValue("train"),
                        next_dataset.getAttributeValue("test"),
                        next_dataset.getAttributeValue("label"),
                        next_dataset.getAttributeValue("output"),
                        next_dataset.getAttributeValue("mode"));

                java.util.List metadata = next_dataset.getChildren("metadata");
                if (metadata.size() > 0) {
                    java.util.Iterator md_itr = metadata.iterator();
                    Element next_metadata = (Element) md_itr.next();

                    addMetadataToDataset(d_id,
                            next_dataset.getAttributeValue("train"),
                            next_metadata.getAttributeValue("missing_values"),
                            next_metadata.getAttributeValue("number_of_attributes"),
                            next_metadata.getAttributeValue("number_of_instances"),
                            next_metadata.getAttributeValue("attribute_type"),
                            next_metadata.getAttributeValue("default_task"));
                }
            }

            java.util.List recommender = next_problem.getChildren("recommender");
            java.util.Iterator r_itr = recommender.iterator();

            if (recommender.size() != 0) {
                while (r_itr.hasNext()) {
                    Element next_recommender = (Element) r_itr.next();
                    addRecommenderToProblem(p_id, next_recommender.getAttributeValue("name"));
                }
            }

            java.util.List _agents = next_problem.getChildren("agent");
            java.util.Iterator a_itr = _agents.iterator();
            while (a_itr.hasNext()) {
                Element next_agent = (Element) a_itr.next();

                String agent_name = next_agent.getAttributeValue("name");
                String agent_type = next_agent.getAttributeValue("type");
                int a_id = -1;
                try {
                    a_id = addAgentToProblem(p_id, agent_name, agent_type, null);
                } catch (FailureException e) {
                    logError(e.getLocalizedMessage());
                }

                java.util.List _options = next_agent.getChildren("parameter");
                java.util.Iterator o_itr = _options.iterator();
                while (o_itr.hasNext()) {
                    Element next_option = (Element) o_itr.next();
                    addOptionToAgent(p_id, a_id,
                            next_option.getAttributeValue("name"),
                            next_option.getAttributeValue("value"),
                            next_option.getAttributeValue("lower"),
                            next_option.getAttributeValue("upper"),
                            next_option.getAttributeValue("number_of_values_to_try"),
                            next_option.getAttributeValue("set"));
                }
            }
        }
    } // end getProblemsFromXMLFile
}
