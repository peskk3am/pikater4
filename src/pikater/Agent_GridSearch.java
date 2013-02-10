/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pikater;

import jade.util.leap.Iterator;
import jade.util.leap.LinkedList;
import jade.util.leap.List;
import java.util.ArrayList;
import pikater.ontology.messages.BoolSItem;
import pikater.ontology.messages.FloatSItem;
import pikater.ontology.messages.IntSItem;
import pikater.ontology.messages.Option;
import pikater.ontology.messages.SearchItem;
import pikater.ontology.messages.SearchSolution;
import pikater.ontology.messages.SetSItem;

/**
 *
 * @author Martin Pilat
 */
public class Agent_GridSearch extends Agent_Search {

    int defaultTries = 10;
    List schema;
    boolean linearSteps = true;
    boolean logSteps = true;
    double logZero = 1.0E-8;
    ArrayList<String> values = null;

    @Override
    protected String getAgentType() {
        return "GridSearch";
    }

    @Override
    protected List generateNewSolutions(List solutions, float[][] evaluations) {

        System.err.println("GENERATING");

        if (values == null) {
            values = generateValues();
        }
        
        System.err.println("VALUES: " + values.size());
        
        List ret = new LinkedList();
        
        for (int i = 0; i < values.size(); i++) {
            SearchSolution ss = new SearchSolution();
            LinkedList v = new LinkedList();
            for (String s: values.get(i).split(",")) {
                v.add(s);
            }
            ss.setValues(v);
            ret.add(ss);
        }
        
        System.err.println("ret: " + ret.size());
        
        return ret;
    }

    private ArrayList<String> generateValues() {
        
        ArrayList<String> vals = new ArrayList<String>();
        
        ArrayList<ArrayList<String>> valsForOpts = new ArrayList<ArrayList<String>>();

        Iterator it = schema.iterator();
        while (it.hasNext()) {
            SearchItem si = (SearchItem) it.next();
            Integer tries = si.getNumber_of_values_to_try();
            if (tries == 0) {
                tries = defaultTries;
            }
            ArrayList<String> valsForItem = new ArrayList<String>();
            if (si instanceof IntSItem) {
                IntSItem isi = (IntSItem)si;
                if (isi.getMax() - isi.getMin() < tries) {
                    for (int i = isi.getMin(); i <= isi.getMax(); i++) {
                        valsForItem.add(Integer.toString(i));
                    }
                } else {
                    if (linearSteps) {
                        double stepSize = 1.0 * (isi.getMax() - isi.getMin()) / (tries - 1);
                        for (int i = 0; i < tries; i++) {
                            String add = Integer.toString((int) Math.round(isi.getMin() + i * stepSize));
                            if (!valsForItem.contains(add)) {
                                valsForItem.add(add);
                            }
                        }
                    }
                    if (logSteps) {
                        double normalization = isi.getMin() < logZero ? isi.getMin() - logZero : 0.0;
                        double start = Math.log(isi.getMin() - normalization);
                        double range = Math.log(isi.getMax() - normalization) - Math.log(isi.getMin() - normalization);
                        double stepSize = range / (tries - 1);
                        for (int i = 0; i < tries; i++) {
                            String add = Integer.toString((int) Math.round(Math.exp(start + i * stepSize) + normalization));
                            if (!valsForItem.contains(add)) {
                                valsForItem.add(add);
                            }
                        }
                    }
                }
            }

            if (si instanceof FloatSItem) {
                FloatSItem isi = (FloatSItem)si;
                if (linearSteps) {
                    double stepSize = 1.0 * (isi.getMax() - isi.getMin()) / (tries - 1);
                    for (int i = 0; i < tries; i++) {
                        String add = Double.toString(isi.getMin() + i * stepSize);
                        if (!valsForItem.contains(add)) {
                            valsForItem.add(add);
                        }
                    }
                }
                if (logSteps) {
                    double normalization = isi.getMin() < logZero ? isi.getMin() - logZero : 0.0;
                    double start = Math.log(isi.getMin() - normalization);
                    double range = Math.log(isi.getMax() - normalization) - Math.log(isi.getMin() - normalization);
                    double stepSize = range / (tries - 1);
                    for (int i = 0; i < tries; i++) {
                        String add = Double.toString(Math.exp(start + i * stepSize) + normalization);
                        if (!valsForItem.contains(add)) {
                            valsForItem.add(add);
                        }
                    }
                }
            }

            if (si instanceof BoolSItem) {
                valsForItem.add("True");
                valsForItem.add("False");
            }

            if (si instanceof SetSItem) {
                SetSItem ssi = (SetSItem) si;
                for (int i = 0; i < tries && i < ssi.getSet().size(); i++) {
                    valsForItem.add(ssi.getSet().get(i).toString());
                }
            }
            valsForOpts.add(valsForItem);
        }
        
        System.err.println("valsForOpts.size(): " + valsForOpts.size());
        
        vals.addAll(valsForOpts.get(0));
        
        for (int i = 1; i < valsForOpts.size(); i++) {
            ArrayList<String> newVals = new ArrayList<String>();
            for (int j = 0; j < vals.size(); j++) {
                for (int k = 0; k < valsForOpts.get(i).size(); k++) {
                    newVals.add(vals.get(j) + "," + valsForOpts.get(i).get(k));
                    System.err.println("VALUES: " + vals.get(j) + "," + valsForOpts.get(i).get(k));
                }
            }
            vals = newVals;
        }
        
        for (String v : vals) {
            System.err.println("VALUES: " + v);
        }
        
        return vals;
    }

    @Override
    protected boolean finished() {
        return values != null;
    }

    @Override
    protected void updateFinished(float[][] evaluations) {}

    @Override
    protected void loadSearchOptions() { 
        List search_options = getSearch_options();
        Iterator itr = search_options.iterator();
        while (itr.hasNext()) {
            Option next = (Option) itr.next();

            if (next.getName().equals("N")) {
                defaultTries = Integer.parseInt(next.getValue());
            }
            if (next.getName().equals("B")) {
                query_block_size = Integer.parseInt(next.getValue());
            }
            //if (next.getName().equals("L")) {
            //   linearSteps = Boolean.parseBoolean(next.getValue());
            //}
            //if (next.getName().equals("G")) {
            //    logSteps = Boolean.parseBoolean(next.getValue());
            //}
            if (next.getName().equals("Z")) {
                logZero = Double.parseDouble(next.getValue());
            }
        }
        schema = getSchema();
    }
}
