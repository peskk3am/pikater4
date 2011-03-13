package pikater.gui.java;

import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAService;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Iterator;
import jade.util.leap.List;

import java.util.regex.Pattern;
import java.util.regex.Pattern;

import pikater.Agent_GUI;
import pikater.DataManagerService;
import pikater.gui.java.improved.FileBrowserFrame;
import pikater.gui.java.improved.FileDetailsFrame;
import pikater.gui.java.improved.GuiConstants;
import pikater.gui.java.improved.ResultsBrowserFrame;
import pikater.ontology.messages.DataInstances;
import pikater.ontology.messages.GetData;
import pikater.ontology.messages.GetFileInfo;
import pikater.ontology.messages.LoadResults;
import pikater.ontology.messages.Metadata;
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
        // TODO Auto-generated method stub
    }

    @Override
    protected void allOptionsReceived(int problemId) {
        sendProblem(problemId);
    }

    @Override
    protected void displayOptions(Problem problem, int performative) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void displayPartialResult(ACLMessage inform) {
        if (inform.getPerformative() != ACLMessage.INFORM) {
            System.err.println("Received FAILURE");
            //myGUI.displayError(inform.getContent());
            return;
        }

        try {
            Result r = (Result) getContentManager().extractContent(inform);
            Results res = (Results) r.getValue();
            List tasks = res.getResults();

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

                //myGUI.addResult(t);
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

                FileBrowserFrame fm = (FileBrowserFrame) ev.getSource();
                GetFileInfo gfi = (GetFileInfo)ev.getParameter(0);
                fm.setFiles(DataManagerService.getFilesInfo(this, gfi));
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
                    e.printStackTrace();
                }
                break;
            case GuiConstants.LOAD_RESULTS:

                LoadResults lr = (LoadResults)ev.getParameter(0);
                Action a = new Action(this.getAID(), lr);

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
                    e.printStackTrace();
                }
                catch (OntologyException e) {
                    e.printStackTrace();
                }
                catch (FIPAException e) {
                    e.printStackTrace();
                }


            /*case MainWindow.ON_LOAD:

                NewExperimentPanel nep = (NewExperimentPanel) ev.getSource();

                Vector<String> types = offerAgentTypes();
                types.add(0, "?");
                String[] agentTypes = new String[types.size()];

                for (int i = 0; i < types.size(); i++) {
                    agentTypes[i] = types.get(i);
                }

                ArrayList files = DataManagerService.getFiles(this, 1);
                String[] filesList = new String[files.size()];

                for (int i = 0; i < files.size(); i++) {
                    filesList[i] = (String) files.get(i);
                }

                nep.setFilesList(filesList);
                nep.setAgentTypes(agentTypes);
                break;*/

//            case MainWindow.START_EXPERIMENT:
//
//                Vector<String> agents = (Vector<String>) ev.getParameter(0);
//                Vector<String> agentOptions = (Vector<String>) ev.getParameter(1);
//                Vector<String> trainFiles = (Vector<String>) ev.getParameter(2);
//                Vector<String> testFiles = (Vector<String>) ev.getParameter(3);
//                Vector<String> labelFiles = (Vector<String>) ev.getParameter(4);
//                Vector<String> optionsManager = (Vector<String>) ev.getParameter(5);
//
//                int problemID = createNewProblem("10000");
//
//                for (int i = 0; i < trainFiles.size(); i++) {
//                    addDatasetToProblem(problemID, trainFiles.get(i), testFiles.get(i), labelFiles.get(i), "predictions", null);
//                }
//
//                try {
//                    for (int i = 0; i < agents.size(); i++) {
//
//                        if (agents.get(i).contains("?")) {
//                            System.err.println("? agent");
//                            addAgentToProblem(problemID, null, agents.get(i), null);
//                        } else {
//                            addAgentToProblem(problemID, null, agents.get(i),
//                                    agentOptions.get(i));
//                        }
//                    }
//                } catch (FailureException e) {
//                    e.printStackTrace();
//                }
//
//                if (optionsManager.get(0).equals("Random")) {
//                    addMethodToProblem(problemID, optionsManager.get(0),
//                            optionsManager.get(1), optionsManager.get(2));
//                } else {
//                    setDefault_number_of_values_to_try(Integer.parseInt(optionsManager.get(1)));
//                    addMethodToProblem(problemID, "ChooseXValues", null, null);
//                }
//
//                break;

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
