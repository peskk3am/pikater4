package pikater;

import jade.content.ContentElement;
import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.behaviours.TickerBehaviour;
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
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;
import jade.proto.ContractNetInitiator;
import jade.proto.SubscriptionResponder.Subscription;
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.List;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;
import jade.wrapper.StaleProxyException;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.LinkedList;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import pikater.metadata.MetadataReader;
import pikater.ontology.messages.DeleteTempFiles;

import pikater.ontology.messages.CreateAgent;
import pikater.ontology.messages.Data;
import pikater.ontology.messages.DataInstances;
import pikater.ontology.messages.Duration;
import pikater.ontology.messages.Eval;
import pikater.ontology.messages.Evaluation;
import pikater.ontology.messages.EvaluationMethod;
import pikater.ontology.messages.Execute;
import pikater.ontology.messages.GetAllMetadata;
import pikater.ontology.messages.GetData;
import pikater.ontology.messages.GetDuration;
import pikater.ontology.messages.GetFileInfo;
import pikater.ontology.messages.GetFiles;
import pikater.ontology.messages.GetMetadata;
import pikater.ontology.messages.GetTheBestAgent;
import pikater.ontology.messages.Id;
import pikater.ontology.messages.ImportFile;
import pikater.ontology.messages.Instance;
import pikater.ontology.messages.LoadResults;
import pikater.ontology.messages.MessagesOntology;
import pikater.ontology.messages.Metadata;
import pikater.ontology.messages.Option;
import pikater.ontology.messages.Results;
import pikater.ontology.messages.SaveMetadata;
import pikater.ontology.messages.SaveResults;
import pikater.ontology.messages.SavedResult;
import pikater.ontology.messages.Solve;
import pikater.ontology.messages.Task;
import pikater.ontology.messages.TranslateFilename;
import pikater.ontology.messages.UpdateMetadata;
import pikater.ontology.messages.Attribute;
import weka.core.AttributeStats;

public class Agent_MetadataQueen extends Agent {

	private static final long serialVersionUID = -1886699589066832983L;
	
	Codec codec = new SLCodec();
    Ontology ontology = MessagesOntology.getInstance();
        
    // 3 levels:
	// 0 no output
	// 1 minimal
	// 2 normal
	private int verbosity = 1;    
    
    @Override
    protected void setup() {

    	println("Agent " + getLocalName() +  " (MetadataQueen) is alive...", 1, true);
    	
    	// get the agent's parameters
    	Object[] args = getArguments();
		if (args != null && args.length > 0) {
			int i = 0;
						
			while (i < args.length){
				if (args[i].equals("log_LR_durations")){
					// log_LR_durations = true;
				}
				i++;
			}
		}		    	

        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontology);               
        
        // receive request
        MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchOntology(ontology.getName()), MessageTemplate.MatchPerformative(ACLMessage.REQUEST));        
		addBehaviour(new receiveRequest(this, mt));
                
               

    }  // end setup()
    
      
	protected class receiveRequest extends AchieveREResponder {

		private static final long serialVersionUID = -1849883814703874922L;

		public receiveRequest(Agent a, MessageTemplate mt) {
			super(a, mt);
			// TODO Auto-generated constructor stub
		}

        @Override
        protected ACLMessage handleRequest(ACLMessage request)
        	throws NotUnderstoodException, RefuseException {
        	
        	String msg = "Failure";
            Integer performative = ACLMessage.FAILURE;
            
        	try {
                Action a = (Action) getContentManager().extractContent(request);

                if (a.getAction() instanceof GetMetadata) {
                        GetMetadata gm = (GetMetadata) a.getAction();

                        // request a reader agent to read data
                        ACLMessage response = FIPAService.doFipaRequestClient(myAgent, prepareGetDataReq(gm.getInternal_filename()));                        	
                        
                        DataInstances data = processGetData(response);       		
        				if (data != null) {
        					Metadata m = computeMetadata(data);
        					m.setInternal_name(gm.getInternal_filename());
        					m.setExternal_name(gm.getExternal_filename());
        					// order DataManager to write data to database
        					DataManagerService.saveMetadata(myAgent, m);        					        					
        					
        					// send inform to agent who requested this
        					msg = "OK";
        					performative = ACLMessage.INFORM;
        				}
        				else {
        					msg = "No train data received from the reader agent: Wrong content.";        					
        				}                        	                		                        
                }
            } catch (OntologyException e) {
                e.printStackTrace();
            } catch (CodecException e) {
                e.printStackTrace();
            } catch (FIPAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        	ACLMessage reply = request.createReply();
            reply.setPerformative(performative);
            reply.setContent(msg);                                                																				

            return reply;
        }
    }				        
                
    
    pikater.ontology.messages.DataInstances processGetData(ACLMessage inform) {
		ContentElement content;
		try {
                        ContentManager manager= getContentManager();
			content = manager.extractContent(inform);
			if (content instanceof Result) {
				Result result = (Result) content;
				if (result.getValue() instanceof pikater.ontology.messages.DataInstances) {
					return (pikater.ontology.messages.DataInstances) result.getValue();
				}
			}
		} catch (UngroundedException e) {
			e.printStackTrace();
		} catch (CodecException e) {
			e.printStackTrace();
		} catch (OntologyException e) {
			e.printStackTrace();
		}
		return null;
	}
	    	
    
	protected ACLMessage prepareGetDataReq(String fileName) {
		AID[] ARFFReaders;
		AID reader;
		ACLMessage msgOut = null;
		// Make the list of reader agents
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("ARFFReader");
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			// System.out.println(getLocalName() + ": Found the following ARFFReader agents:");
			ARFFReaders = new AID[result.length];
			for (int i = 0; i < result.length; ++i) {
				ARFFReaders[i] = result[i].getName();
				// System.out.println("    " + ARFFReaders[i].getName());
			}
			
			// randomly choose one of the readers
			Random randomGenerator = new Random();		    
		    int randomInt = randomGenerator.nextInt(result.length);
		    reader = ARFFReaders[randomInt];

		    println("Using " + reader + ", filename: " + fileName, 2, true);
			
			// request
			msgOut = new ACLMessage(ACLMessage.REQUEST);
			msgOut.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			// msgOut.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
			msgOut.setLanguage(codec.getName());
			msgOut.setOntology(ontology.getName());
			msgOut.addReceiver(reader);
			// content
			GetData get_data = new GetData();
			get_data.setFile_name(fileName);
			Action a = new Action();
			a.setAction(get_data);
			a.setActor(this.getAID());
			getContentManager().fillContent(msgOut, a);
		} catch (FIPAException fe) {
			fe.printStackTrace();
			return null;
		} catch (CodecException e) {
			e.printStackTrace();
			return null;
		} catch (OntologyException e) {
			e.printStackTrace();
			return null;
		}
		return msgOut;
	} // end prepareGetDataReq    
    
	
	private Metadata computeMetadata(DataInstances data){	

		MetadataReader reader=new MetadataReader();
                return reader.computeMetadata(data);
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
