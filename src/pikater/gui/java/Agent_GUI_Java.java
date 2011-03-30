package pikater.gui.java;

import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAService;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Iterator;
import jade.util.leap.LinkedList;
import jade.util.leap.List;
import java.util.ArrayList;
import java.util.Vector;

import java.util.regex.Pattern;
import java.util.regex.Pattern;

import pikater.Agent_GUI;
import pikater.DataManagerService;
import pikater.gui.java.improved.AgentOptionsDialog;
import pikater.gui.java.improved.FileBrowserFrame;
import pikater.gui.java.improved.FileDetailsFrame;
import pikater.gui.java.improved.FileGroup;
import pikater.gui.java.improved.GuiConstants;
import pikater.gui.java.improved.NewExperimentFrame;
import pikater.gui.java.improved.ResultsBrowserFrame;
import pikater.ontology.messages.Agent;
import pikater.ontology.messages.DataInstances;
import pikater.ontology.messages.GetData;
import pikater.ontology.messages.GetFileInfo;
import pikater.ontology.messages.LoadResults;
import pikater.ontology.messages.Metadata;
import pikater.ontology.messages.Option;
import pikater.ontology.messages.Problem;
import pikater.ontology.messages.Results;
import pikater.ontology.messages.Task;

public class Agent_GUI_Java extends Agent_GUI {

    /**
     *
     */
    private static final long serialVersionUID = -3678714827126048550L;
    transient protected pikater.gui.java.improved.MainWindow myGUI;

    public Agent_GUI_Java() {

        myGUI = new pikater.gui.java.improved.MainWindow(this);

        myGUI.setVisible(true);
    }

    @Override
    protected void DisplayWrongOption(int problemGuiId, String agentName,
            String optionName, String errorMessage) {
        myGUI.showError("Agent " + agentName + ": " + errorMessage);
    }

    @Override
    protected void allOptionsReceived(int problemId) {
        sendProblem(problemId);
        myGUI.showInfo("Starting experiment");
    }

    /*@Override
    protected void displayOptions(Problem problem, int performative) {
        // TODO Auto-generated method stub
    }*/

    @Override
    protected void displayPartialResult(ACLMessage inform) {
        if (inform.getPerformative() != ACLMessage.INFORM) {
            System.err.println("Received FAILURE");
            myGUI.showError(inform.getContent());
            return;
        }

        try {
            Result r = (Result) getContentManager().extractContent(inform);
            Results res = (Results) r.getValue();
            List tasks = res.getResults();

            myGUI.showInfo("Got results from: " + ((Task)tasks.get(0)).getAgent().getName());

            Iterator it = tasks.iterator();

            while (it.hasNext()) {
                Task t = (Task) it.next();

                String testInternalFilename = t.getData().getTest_file_name();
                String trainInternalFilename = t.getData().getTrain_file_name();

				String[] path = testInternalFilename.split(Pattern.quote(
						System.getProperty("file.separator")));
                testInternalFilename = path[path.length - 1];

				path = trainInternalFilename.split(Pattern.quote(
						System.getProperty("file.separator")));
                trainInternalFilename = path[path.length - 1];

                t.getData().setTest_file_name(
                        DataManagerService.translateFilename(this, 1, null,
                        testInternalFilename));
                t.getData().setTrain_file_name(
                        DataManagerService.translateFilename(this, 1, null,
                        trainInternalFilename));

                if (t.getResult().getStatus() == null)
                    myGUI.addResult(t);
                else {
                    myGUI.showError("Error: " + t.getResult().getStatus());
                }
            }
        } catch (UngroundedException e) {
            e.printStackTrace();
            myGUI.showError("Ungrounded exception: " + e.getLocalizedMessage());
        } catch (CodecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            myGUI.showError("Codec exception: " + e.getLocalizedMessage());
        } catch (OntologyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            myGUI.showError("Ontology exception: " + e.getLocalizedMessage());
        }

    }

    public List getAgentOptionsSynchronous(String agentType) {
        
        List options = null;
        try {
            options = getOptions(agentType);
        }
        catch (Exception e) {
            myGUI.showError("Problem while loading options: " + e.getLocalizedMessage());
        }

        if (options == null)
              options = new LinkedList();
        return options;
    }

    @Override
    protected void displayResult(ACLMessage inform) {
        displayPartialResult(inform);
        //myGUI.allResultsReceived();
    }

    @Override
    protected String getAgentType() {
        return "Java GUI Agent";
    }

    @Override
    protected void mySetup() {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onGuiEvent(GuiEvent ev) {
        switch (ev.getType()) {
            case GuiConstants.GET_FILES_INFO:

                if (ev.getSource() instanceof FileBrowserFrame) {
                    FileBrowserFrame fm = (FileBrowserFrame) ev.getSource();
                    GetFileInfo gfi = (GetFileInfo)ev.getParameter(0);
                    fm.setFiles(DataManagerService.getFilesInfo(this, gfi));
                }
                if (ev.getSource() instanceof NewExperimentFrame) {
                    NewExperimentFrame nef = (NewExperimentFrame)ev.getSource();
                    GetFileInfo gfi = (GetFileInfo)ev.getParameter(0);
                    nef.setFiles(DataManagerService.getFilesInfo(this, gfi));
                }
                break;

            case GuiConstants.UPDATE_METADATA:

                Metadata update = (Metadata) ev.getParameter(0);
                DataManagerService.updateMetadata(this, update);
                break;

            case GuiConstants.GET_DATA:

                String internalFilename = DataManagerService.translateFilename(this, 1, (String)ev.getParameter(0), null);
                internalFilename = "data/files/" + internalFilename;

                ServiceDescription sd = new ServiceDescription();
                sd.setType("ARFFReader");

                DFAgentDescription dfd = new DFAgentDescription();
                dfd.addServices(sd);

                try {
                    DFAgentDescription readers[] = DFService.search(this, dfd);

                    if (readers.length == 0) {
                        System.err.println("No readers found");
                        break;
                    }

                    AID reader = readers[0].getName();

                    GetData gd = new GetData();
                    gd.setFile_name(internalFilename);

                    Action a = new Action();
                    a.setAction(gd);
                    a.setActor(this.getAID());

                    ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
                    req.addReceiver(reader);
                    req.setLanguage(codec.getName());
                    req.setOntology(ontology.getName());
                    req.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

                    getContentManager().fillContent(req, a);

                    ACLMessage response = FIPAService.doFipaRequestClient(this, req);

                    if (response.getPerformative() != ACLMessage.INFORM) {
                        System.err.println("Error reading file");
                    }

                    Result res = (Result)getContentManager().extractContent(response);
                    DataInstances di = (DataInstances)res.getValue();

                    if (ev.getSource() instanceof FileDetailsFrame) {
                        FileDetailsFrame source = (FileDetailsFrame)ev.getSource();
                        source.setInstances(di);
                    }
                }
                catch (Exception e) {
                    myGUI.showError("Error: " + e.getLocalizedMessage());
                    e.printStackTrace();
                }
                break;
            case GuiConstants.LOAD_RESULTS:

                LoadResults lr = (LoadResults)ev.getParameter(0);
                Action a = new Action(this.getAID(), lr);

                System.err.println(lr.asText());
                System.err.println(lr.asSQLCondition());

                ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
                req.addReceiver(new AID("dataManager", false));
                req.setLanguage(codec.getName());
                req.setOntology(ontology.getName());
                req.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

                try {
                    getContentManager().fillContent(req, a);
                    ACLMessage response = FIPAService.doFipaRequestClient(this, req);

                    if (response.getPerformative() != ACLMessage.INFORM) {
                        System.err.println("Error getting results");
                    }

                    Result res = (Result)getContentManager().extractContent(response);
                    List l = (List)res.getValue();

                    if (ev.getSource() instanceof ResultsBrowserFrame) {
                        ResultsBrowserFrame source = (ResultsBrowserFrame)ev.getSource();
                        source.showResults(l);
                    }

                }
                catch (CodecException e) {
                    myGUI.showError("COdec error: " + e.getLocalizedMessage());
                    e.printStackTrace();
                }
                catch (OntologyException e) {
                    myGUI.showError("Ontlogy error: " + e.getLocalizedMessage());
                    e.printStackTrace();
                }
                catch (FIPAException e) {
                    myGUI.showError("FIPA error: " + e.getLocalizedMessage());
                    e.printStackTrace();
                }
                break;

            case GuiConstants.GET_AGENT_TYPES:

                NewExperimentFrame nef = (NewExperimentFrame) ev.getSource();

                Vector<String> types = offerAgentTypes();
                types.add(0, "?");
                String[] agentTypes = new String[types.size()];

                for (int i = 0; i < agentTypes.length; i++) {
                    agentTypes[i] = types.get(i);
                }

                nef.setAgentTypes(agentTypes);
                break;
            case GuiConstants.GET_AGENT_OPTIONS:

                AgentOptionsDialog aop = (AgentOptionsDialog)ev.getSource();

                String agentType = (String)ev.getParameter(0);

                try {
                    List options = getOptions(agentType);
                    if (options == null)
                        options = new LinkedList();

                    aop.setAgentOptions(options);

                    for (int i = 0; i < options.size(); i++) {
                        Option o = (Option)options.get(i);
                        System.err.println("-" + o.getName() + " : " + o.getSynopsis());
                    }
                }
                catch (CodecException ce) {
                    ce.printStackTrace();
                    myGUI.showError("Codec Error: " + ce.getLocalizedMessage());
                }
                catch (OntologyException oe) {
                    oe.printStackTrace();
                    myGUI.showError("Ontology Error: " + oe.getLocalizedMessage());
                }
                catch (FIPAException fe) {
                    fe.printStackTrace();
                    myGUI.showError("FIPA error: " + fe.getLocalizedMessage());
                }

                break;

            case GuiConstants.START_EXPERIMENT:

                LinkedList optionManager = (LinkedList) ev.getParameter(0);
                ArrayList<Agent> agents = (ArrayList<Agent>) ev.getParameter(1);
                ArrayList<FileGroup> files = (ArrayList<FileGroup>) ev.getParameter(2);
            
                int problemID = createNewProblem("10000");

                if (optionManager.get(0).equals("Random")) {
                    addMethodToProblem(problemID, optionManager.get(0).toString(),
                            optionManager.get(1).toString(), optionManager.get(2).toString());
                }

                if (optionManager.get(0).equals("ChooseXValues")) {
                    addMethodToProblem(problemID, optionManager.get(0).toString(), null, null);
                    setDefault_number_of_values_to_try((Integer)optionManager.get(1));
                }

                for (int i = 0; i < files.size(); i++) {
                    addDatasetToProblem(problemID, files.get(i).getTrainFile(), files.get(i).getTestFile(), null, "predictions", null);
                }

                try {
                    for (int i = 0; i < agents.size(); i++) {

                        if (agents.get(i).getType().equals("?")) {
                            System.err.println("? agent");
                            addAgentToProblem(problemID, null, agents.get(i).getType(), null);
                        } else {
                            int aid = addAgentToProblem(problemID, null, agents.get(i).getType(), null);

                            for (int j = 0; j < agents.get(i).getOptions().size(); j++) {
                                Option o = (Option)agents.get(i).getOptions().get(j);

                                String tries = o.getNumber_of_values_to_try() != 0 ? String.valueOf(o.getNumber_of_values_to_try()) : null;

                                if (o.getIs_a_set()) {
                                    String set = "";
                                    for (int k = 0; k < o.getSet().size(); k++) {
                                        set += o.getSet().get(k);
                                        if (k < o.getSet().size() - 1) {
                                            set += ",";
                                        }
                                    }
                                    System.err.println("SET PARAM: " + set);
                                    addOptionToAgent(problemID, aid, o.getName(), o.getValue(), null, null, String.valueOf(o.getSet().size()), set);
                                }
                                else
                                    addOptionToAgent(problemID, aid, o.getName(), o.getValue(), o.getRange().getMin().toString(), o.getRange().getMax().toString(), tries, null);
                            }

                        }
                    }
                } catch (FailureException e) {
                    e.printStackTrace();
                }

                
                break;

/*            case MainWindow.IMPORT_FILE:

                String fileName = (String) ev.getParameter(0);
                String fileContent = (String) ev.getParameter(1);

                DataManagerService.importFile(this, 1, fileName, fileContent);

                FileManagerPanel fmp = (FileManagerPanel) ev.getSource();
                fmp.reloadFileInfo();

                //myGUI.addFile(fileName);

                break;*/

        }
    }
}
