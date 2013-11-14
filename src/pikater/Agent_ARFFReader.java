package pikater;

import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import pikater.agents.PikaterAgent;
import pikater.ontology.messages.DataInstances;
import pikater.ontology.messages.GetData;
import weka.core.Attribute;
import weka.core.AttributeStats;
import weka.core.Instances;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Agent_ARFFReader extends PikaterAgent {
	private static final long serialVersionUID = 7116837600070711675L;
	// data read from file
	protected Instances data;
	boolean ReadFromFile(String fileName) {
		if (fileName == null || fileName.length() == 0) {
			return false;
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			data = new Instances(reader);
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
			logError("Reading of data from file " + fileName + " failed.");
			return false;
		}
		log("Reading of data from file " + fileName + " succesful.");
		return true;
	}

	@Override
	protected String getAgentType(){
		return "ARFFReader";
	}
	
	@Override
	protected void setup() {
		initDefault();
		registerWithDF();

        MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        addBehaviour(new GetDataResponder(this, template));
	} // end Setup

	protected ACLMessage sendData(ACLMessage request) {
		// responding message
		ACLMessage msgOut = request.createReply();
		msgOut.setPerformative(ACLMessage.INFORM);
		try {
			ContentElement content = getContentManager()
					.extractContent(request);
                        GetData gd = (GetData) ((Action) content).getAction();
			String file_name = gd.getFile_name();
			DataInstances instances = new DataInstances();
			// Read the file
			boolean file_read = ReadFromFile(file_name);
			if (!file_read) {
				throw new FailureException("File haven't been read. Wrong file-name?");
			}

			instances.fillWekaInstances(data);
                        ArrayList<Integer> types = new ArrayList<Integer>();

                        for (int i = 0; i < data.numAttributes(); i++) {
                            Attribute a = data.attribute(i);
                            AttributeStats as = data.attributeStats(i);

                            if (i != (data.classIndex() >= 0 ? data.classIndex() : data.numAttributes() - 1)) {
                                if (!types.contains(a.type())) {
                                    types.add(a.type());
                                }
                            }
                        }

			// Prepare the content
			Result result = new Result((Action) content, instances);
			try {
				getContentManager().fillContent(msgOut, result);
			} catch (CodecException ce) {
				ce.printStackTrace();
			} catch (OntologyException oe) {
				oe.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msgOut;
	} // end SendData

	private class GetDataResponder extends AchieveREResponder {
		private static final long serialVersionUID = 4340928332826216394L;

		public GetDataResponder(Agent a, MessageTemplate mt) {
			super(a, mt);
		}

		@Override
		protected ACLMessage prepareResponse(ACLMessage request) {
			ACLMessage agree = request.createReply();
			agree.setPerformative(ACLMessage.AGREE);
			return agree;
		} // end prepareResponse

		@Override
		protected ACLMessage prepareResultNotification(ACLMessage request,
				ACLMessage response) {
			try {
				ContentElement content = getContentManager().extractContent(
						request);
				// GetData
				if (((Action) content).getAction() instanceof GetData) {
					return sendData(request);
				}
			} catch (CodecException ce) {
				ce.printStackTrace();
			} catch (OntologyException oe) {
				oe.printStackTrace();
			}

			ACLMessage notUnderstood = request.createReply();
			notUnderstood.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			return notUnderstood;
		} // end prepareResultNotification
	}
}