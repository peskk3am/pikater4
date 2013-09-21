/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pikater.agents.recommenders;

import jade.util.leap.Iterator;
import jade.util.leap.LinkedList;
import jade.util.leap.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import pikater.DataManagerService;
import pikater.ontology.messages.Agent;
import pikater.ontology.messages.Data;
import pikater.ontology.messages.GetAllMetadata;
import pikater.ontology.messages.Interval;
import pikater.ontology.messages.Metadata;
import pikater.ontology.messages.Option;

/**
 *
 * @author Martin Pilat
 */
public class Agent_NMTopRecommender extends Agent_Recommender {

    private int minAttributes;
    private int maxAttributes;
    private int minInstances;
    private int maxInstances;
    private int N = 100;
    private int M = 5;

    class MetadataDistancePair implements Comparable<MetadataDistancePair> {

        Metadata m;

        public Metadata getMetadata() {
            return m;
        }

        public void setMetadata(Metadata m) {
            this.m = m;
        }

        public double getDistance() {
            return d;
        }

        public void setDistance(double d) {
            this.d = d;
        }
        double d;

        public MetadataDistancePair(Metadata m, double d) {
            this.m = m;
            this.d = d;
        }

        @Override
        public int compareTo(MetadataDistancePair o) {
            if (o.getDistance() == this.getDistance()) {
                return 0;
            }
            return o.getDistance() < this.getDistance() ? 1 : -1;
        }
    }

    @Override
    protected Agent chooseBestAgent(Data data) {

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
            minAttributes = Math.min(minAttributes, na);
            maxAttributes = Math.max(maxAttributes, na);

            int ni = next_md.getNumber_of_instances();
            minInstances = Math.min(ni, minInstances);
            maxInstances = Math.max(ni, maxInstances);
        }

        ArrayList<MetadataDistancePair> distances = new ArrayList<MetadataDistancePair>();

        itr = allMetadata.iterator();
        while (itr.hasNext()) {
            Metadata next_md = (Metadata) itr.next();
            double dNew = distance(metadata, next_md);

            distances.add(new MetadataDistancePair(next_md, dNew));
        }

        Collections.sort(distances);

        List agents = new LinkedList();
        for (int i = 0; i < M; i++) {
            System.err.println("" + distances.get(i).m.getExternal_name() + ": " + distances.get(i).d);
            List ag = DataManagerService.getTheBestAgents(this, distances.get(i).m.getInternal_name(), N);
            Iterator it = ag.iterator();
            while (it.hasNext()) {
                agents.add(it.next());
            }
        }

        HashMap<String, Integer> counts = new HashMap<String, Integer>();

        Iterator it = agents.iterator();
        while (it.hasNext()) {
            Agent a = (Agent) it.next();

            if (counts.containsKey(a.getType())) {
                counts.put(a.getType(), counts.get(a.getType()) + 1);
            } else {
                counts.put(a.getType(), 1);
            }
        }

        int maxCount = 0;
        String bestAgentType = null;
        for (String s : counts.keySet()) {
            System.err.println(s + ": " + counts.get(s));
            if (counts.get(s) > maxCount) {
                maxCount = counts.get(s);
                bestAgentType = s;
            }
        }

        System.err.println("Best agent: " + bestAgentType);

        ArrayList<Agent> bestAgentOptions = new ArrayList<Agent>();

        it = agents.iterator();
        while (it.hasNext()) {
            Agent a = (Agent) it.next();

            if (a.getType().equals(bestAgentType)) {
                bestAgentOptions.add(a);
            }
        }

        List optionSamples = getAgentOptions(bestAgentType);
        List options = new LinkedList();
        it = optionSamples.iterator();
        while (it.hasNext()) {
            Option o = (Option) it.next();

            Option newOpt = o.copyOption();        	
            
            //ignore boolean and set options for now, set their value to the one of the best agent on closest file
            if (o.getData_type().equals("BOOLEAN") || o.getData_type().equals("MIXED")) {
                if (bestAgentOptions.get(0).getOptionByName(o.getName()) == null){
                	continue;
                }
                newOpt.setValue(bestAgentOptions.get(0).getOptionByName(o.getName()).getValue());                
            }
            else {
	            double sum = 0;
	            int count = 0;
	            String optionName = o.getName();
	            for (Agent a : bestAgentOptions) {
	            	if (a.getOptionByName(optionName) != null){
	            		sum += Double.parseDouble(a.getOptionByName(optionName).getValue());
	            	}
	                count++;
	            }
	            double avg = sum/count;
	            
	            double stdDev = 0;
	            for (Agent a : bestAgentOptions) {
	            	if (a.getOptionByName(optionName) != null){
	            		stdDev += Math.pow(Double.parseDouble(a.getOptionByName(optionName).getValue()) - avg, 2);
	            	}
	            }
	            
	            stdDev = Math.sqrt(stdDev/count);
	
	            if (stdDev > 0) {
	                newOpt.setValue("?");
	                newOpt.setUser_value("?");
	                newOpt.setMutable(true);
	                Interval range = new Interval();
	                range.setMin((float)Math.max(avg - 2*stdDev, o.getRange().getMin()));
	                range.setMax((float)Math.min(avg + 2*stdDev, o.getRange().getMax()));
	                newOpt.setRange(range);
	            }
	            else {
	                if (o.getData_type().equals("FLOAT")) {
	                    newOpt.setValue(Double.toString(avg));
	                }
	                if (o.getData_type().equals("INT")) {
	                    newOpt.setValue(Integer.toString((int)avg));
	                }
	            }
            }
            options.add(newOpt);
        }

        Agent agent = new Agent();
        agent.setName(null);
        agent.setType(bestAgentType);
        agent.setOptions(options);

        return agent;
    }

    @Override
    protected String getAgentType() {
        return "NMTopRecommender";
    }

    private double distance(Metadata m1, Metadata m2) {

        double wAttribute_type = 1;
        double wDefault_task = 1;
        double wMissing_values = 1;
        double wNumber_of_attributes = 1;
        double wNumber_of_instances = 1;

        // can be null
        double dAttribute_type = dCategory(m1.getAttribute_type(), m2.getAttribute_type());
        double dDefault_task = dCategory(m1.getDefault_task(), m2.getDefault_task());
        // default false - always set
        double dMissing_values = dBoolean(m1.getMissing_values(), m2.getMissing_values());
        // mandatory attributes - always set
        double dNumber_of_attributes = d(m1.getNumber_of_attributes(), m2.getNumber_of_attributes(), minAttributes, maxAttributes);
        double dNumber_of_instances = d(m1.getNumber_of_instances(), m2.getNumber_of_instances(), minInstances, maxInstances);

        double distance = wAttribute_type * dAttribute_type
                + wDefault_task * dDefault_task
                + wMissing_values * dMissing_values
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

    //remove this method when the bug with opts parsing is removed
    List readOptionsFromFile(String agentName) {
        String optPath = System.getProperty("user.dir")
                + System.getProperty("file.separator") + "options"
                + System.getProperty("file.separator") + agentName + ".opt";

        // read options from file
        try {
            /* Sets up a file reader to read the options file */
            FileReader input = new FileReader(optPath);
            /*
             * Filter FileReader through a Buffered read to read a line at a
             * time
             */
            BufferedReader bufRead = new BufferedReader(input);

            String line; // String that holds current file line
            int count = 0; // Line number of count
            // Read first line
            line = bufRead.readLine();
            count++;

            // list of ontology.messages.Option
            List _options = new jade.util.leap.ArrayList();

            // Read through file one line at time. Print line # and line
            while (line != null) {
                // parse the line
                String delims = "[ ]+";
                String[] params = line.split(delims, 11);

                for (int i = 0; i < params.length; i++) {
                    if (params[i].equals("MAXINT")) {
                        params[i] = Integer.toString(Integer.MAX_VALUE);
                    }
                }
                
                if (params[0].equals("$")) {

                    String dt = null;
                    if (params[2].equals("boolean")) {
                        dt = "BOOLEAN";
                    }
                    if (params[2].equals("float")) {
                        dt = "FLOAT";
                    }
                    if (params[2].equals("int")) {
                        dt = "INT";
                    }
                    if (params[2].equals("mixed")) {
                        dt = "MIXED";
                    }

                    float numArgsMin;
                    float numArgsMax;
                    float rangeMin = 0;
                    float rangeMax = 0;
                    String range;
                    List set = null;

                    if (dt.equals("BOOLEAN")) {
                        numArgsMin = 1;
                        numArgsMax = 1;
                        range = null;
                    } else {
                        numArgsMin = Float.parseFloat(params[3]);
                        numArgsMax = Float.parseFloat(params[4]);
                        range = params[5];

                        if (range.equals("r")) {
                            rangeMin = Float.parseFloat(params[6]);
                            rangeMax = Float.parseFloat(params[7]);
                        }
                        if (range.equals("s")) {
                            set = new jade.util.leap.ArrayList();
                            String[] s = params[6].split("[ ]+");
                            for (int i = 0; i < s.length; i++) {
                                set.add(s[i]);
                            }
                        }
                    }

                    Option o = new Option(params[1], dt,
                            numArgsMin, numArgsMax,
                            range, rangeMin, rangeMax, set,
                            params[params.length - 3],
                            params[params.length - 2],
                            params[params.length - 1]);

                    _options.add(o);

                }

                line = bufRead.readLine();
                count++;
            }
            bufRead.close();
            return _options;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }
}
