package pikater.agents.recommenders;

import pikater.DataManagerService;
import pikater.ontology.messages.Data;
import pikater.ontology.messages.GetAllMetadata;
import pikater.ontology.messages.Metadata;
import jade.util.leap.Iterator;
import jade.util.leap.List;

public class Agent_Basic extends Agent_Recommender {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1175580440950655620L;

	private double minAttributes = Integer.MAX_VALUE;
	private double maxAttributes = Integer.MIN_VALUE;
	private double minInstances = Integer.MAX_VALUE;
	private double maxInstances = Integer.MIN_VALUE;
		
	@Override
	protected String getAgentType(){
		return "BasicRecommender";
	}
	
	@Override
	protected pikater.ontology.messages.Agent chooseBestAgent(Data data){
		// in data there are already metadata filled in 
		// return agent with (partially/not at all) filled options
		
		println(distanceMatrix(), 2, true);

		Metadata metadata = data.getMetadata();
		
		GetAllMetadata gm = new GetAllMetadata();
		gm.setResults_required(true);

		// 1. choose the nearest training data
		List allMetadata = DataManagerService.getAllMetadata(this, gm);

		// set the min, max instances and attributes first
		Iterator itr = allMetadata.iterator();
		while (itr.hasNext()) {
			Metadata next_md = (Metadata) itr.next();

			int na = next_md.getNumber_of_attributes();
			if (na < minAttributes) {
				minAttributes = na;
			}
			if (na > maxAttributes) {
				maxAttributes = na;
			}

			int ni = next_md.getNumber_of_instances();
			if (ni < minInstances) {
				minInstances = ni;
			}
			if (ni > maxInstances) {
				maxInstances = ni;
			}
		}
		
		println("*********** files from the table: ", 2, true);

		double d_best = Integer.MAX_VALUE;
		Metadata m_best = null;

		double d_new;
		itr = allMetadata.iterator();
		while (itr.hasNext()) {
			Metadata next_md = (Metadata) itr.next();
			d_new = distance(metadata, next_md);
			if (!next_md.getInternal_name().equals(metadata.getInternal_name())) {
				if (d_new < d_best) {
					d_best = d_new;
					m_best = next_md;
				}
			}
			println("    " + next_md.getExternal_name() + " d: " + d_new, 2, false);
		}

		println("Nearest file: " + m_best.getExternal_name(), 1, true);
		String nearestInternalName = m_best.getInternal_name();

		// 2. find the agent with the lowest error_rate
		pikater.ontology.messages.Agent agent = DataManagerService
				.getTheBestAgent(this, nearestInternalName);
		
		if (agent != null){
			println("Best agent type: "+ agent.getType() +
					", options: " + agent.optionsToString() + 
					", error rate: " + agent.getGui_id(), 1, true);
		}
		else{
			println("No results in database for file " + m_best.getExternal_name(), 1, true);
			return null;
		}

		agent.setName(null); // we want only the type, since the particular
								// agent may not any longer exist				

		return agent;		
	}	         
	
	private String distanceMatrix() {
		String matrix = "";
		
		GetAllMetadata gm = new GetAllMetadata();
		gm.setResults_required(false);
	
		List allMetadata = DataManagerService.getAllMetadata(this, gm);
	
		Iterator itr_colls = allMetadata.iterator();
		while (itr_colls.hasNext()) {
			Metadata next_coll = (Metadata) itr_colls.next();			
	
			int na = next_coll.getNumber_of_attributes();
			if (na < minAttributes) {
				minAttributes = na;
			}
			if (na > maxAttributes) {
				maxAttributes = na;
			}
	
			int ni = next_coll.getNumber_of_instances();
			if (ni < minInstances) {
				minInstances = ni;
			}
			if (ni > maxInstances) {
				maxInstances = ni;
			}
		}
		
		itr_colls = allMetadata.iterator();
	
		double d;
		while (itr_colls.hasNext()) {
			Metadata next_coll = (Metadata) itr_colls.next();			
			matrix +=next_coll.getExternal_name() + ";";
			Iterator itr_rows = allMetadata.iterator();
			
			while (itr_rows.hasNext()) {
				Metadata next_row = (Metadata) itr_rows.next();
				d = distance(next_coll, next_row);
				matrix += String.format("%.10f", d);
				matrix += ";";				
			}
			matrix +="\n";
		}
		
		return matrix;
		
	} // end distanceMatrix

	/*
	 * Compute distance between two datasets (use metadata)
	 */
	private double distance(Metadata m1, Metadata m2) {

		double wAttribute_type = 1;
		double wDefault_task = 1;
		double wMissing_values = 1;
		double wNumber_of_attributes = 1;
		double wNumber_of_instances = 1;

		// can be null
		double dAttribute_type = dCategory(m1.getAttribute_type(), m2
				.getAttribute_type());
		double dDefault_task = dCategory(m1.getDefault_task(), m2
				.getDefault_task());
		// default false - always set
		double dMissing_values = dBoolean(m1.getMissing_values(), m2
				.getMissing_values());
		// mandatory attributes - always set
		double dNumber_of_attributes = d(m1.getNumber_of_attributes(), m2
				.getNumber_of_attributes(), minAttributes, maxAttributes);
		double dNumber_of_instances = d(m1.getNumber_of_instances(), m2
				.getNumber_of_instances(), minInstances, maxInstances);

		double distance = wAttribute_type * dAttribute_type + wDefault_task
				* dDefault_task + wMissing_values * dMissing_values
				+ wNumber_of_attributes * dNumber_of_attributes
				+ wNumber_of_instances * dNumber_of_instances;

		return distance;
	}

	private double d(double v1, double v2, double min, double max) {
		// map the value to the 0,1 interval; 0 - the same, 1 - the most
		// different

		return Math.abs(v1 - v2) / (max - min);
	}

	private int dCategory(String v1, String v2) {
		// null considered another value
		if (v1 == null) {
			v1 = "null";
		}
		if (v2 == null) {
			v2 = "null";
		}

		if (v1.equals(v2)) {
			return 0;
		}
		return 1;
	}

	private int dBoolean(Boolean v1, Boolean v2) {
		if (v1 == v2) {
			return 0;
		}
		return 1;
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
