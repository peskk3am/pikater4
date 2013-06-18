package pikater;

import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAService;
import jade.lang.acl.ACLMessage;
import jade.util.leap.ArrayList;
import jade.util.leap.List;
import pikater.ontology.messages.DeleteTempFiles;
import pikater.ontology.messages.Duration;
import pikater.ontology.messages.ExecuteParameters;
import pikater.ontology.messages.GetAllMetadata;
import pikater.ontology.messages.GetDuration;
import pikater.ontology.messages.GetFileInfo;
import pikater.ontology.messages.GetFiles;
import pikater.ontology.messages.GetTheBestAgent;
import pikater.ontology.messages.ImportFile;
import pikater.ontology.messages.MessagesOntology;
import pikater.ontology.messages.Metadata;
import pikater.ontology.messages.SaveMetadata;
import pikater.ontology.messages.SaveResults;
import pikater.ontology.messages.Task;
import pikater.ontology.messages.TranslateFilename;
import pikater.ontology.messages.UpdateMetadata;

public class DurationService extends FIPAService {

	static final Codec codec = new SLCodec();

	public static Duration getDuration(Agent agent, GetDuration gd) {            

		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.addReceiver(new AID("duration", false));
		request.setOntology(MessagesOntology.getInstance().getName());
		request.setLanguage(codec.getName());
		request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

		Action a = new Action();
		a.setActor(agent.getAID());
		a.setAction(gd);

		try {
			agent.getContentManager().fillContent(request, a);
		} catch (CodecException e1) {
			e1.printStackTrace();
		} catch (OntologyException e1) {
			e1.printStackTrace();
		}

		Duration duration = gd.getDuration();
		duration.setLR_duration(-1);
		try {						
			ACLMessage reply = FIPAService.doFipaRequestClient(agent, request);
			
			// get Duration from the received message			
			ContentElement content = agent.getContentManager().extractContent(reply);
							
			duration = (Duration)(((Result) content).getValue());									
			
		} catch (FIPAException e) {
			e.printStackTrace();
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

		return duration;
        }
}