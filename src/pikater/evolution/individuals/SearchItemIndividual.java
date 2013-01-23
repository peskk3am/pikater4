package pikater.evolution.individuals;

import jade.util.leap.Iterator;
import java.util.Arrays;
import pikater.evolution.RandomNumberGenerator;
import pikater.ontology.messages.BoolSItem;
import pikater.ontology.messages.SearchItem;
import pikater.ontology.messages.SetSItem;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author Martin Pilat
 */
public class SearchItemIndividual extends ArrayIndividual {

    SearchItem[] schema;
    String[] items;

    public SearchItemIndividual(int n) {
        schema = new SearchItem[n];
        items = new String[n];
    }
    
    public void setSchema(int n, SearchItem s) {
        schema[n] = s;
    }
    
    public SearchItem getSchema(int n) {
        return schema[n];
    }
    
    @Override
    public String get(int n) {
        return items[n];
    }

    @Override
    public void set(int n, Object o) {
        items[n] = (String)o;
    }

    @Override
    public int length() {
        return items.length;
    }

    @Override
    public void randomInitialization() {
        for (int i = 0; i < length(); i++) {
            items[i] = schema[i].randomValue(RandomNumberGenerator.getInstance().getRandom());
        }   
    }
    
    public String toString() {
        return Arrays.toString(items);
    }
    
    public Object clone() {
        
        SearchItemIndividual newSI = (SearchItemIndividual)super.clone();
        
        newSI.schema = schema;
        newSI.items = new String[items.length];
        
        for (int i = 0; i < items.length; i++) {
            newSI.items[i] = new String(items[i]);
        }
        
        newSI.fitnessValue = fitnessValue;
        newSI.objectiveValue = objectiveValue;
        
        return newSI;
        
    }
    
    /**
     * Creates an empty dataset from the schema of the individual. Assigns the
     * attributes types according the schema specified in the individual. 
     * 
     * @return The empty dataset representing the schema of the individual.
     */
    
    public Instances emptyDatasetFromSchema() {        
        
        FastVector attributes = new FastVector();
        
        for (int i = 0; i < length(); i++) {
            if (schema[i] instanceof SetSItem) {
                FastVector values = new FastVector();
                Iterator it = schema[i].possibleValues().iterator();
                while (it.hasNext()) {
                    values.addElement(it.next());
                }
                attributes.addElement(new Attribute("a" + i, values));
                continue;
            }
            attributes.addElement(new Attribute("a" + i));
        }
        
        attributes.addElement(new Attribute("class"));
        
        Instances inst = new Instances("train", attributes, 0);
        inst.setClassIndex(attributes.size() - 1);
        
        return inst;
        
    }
    
    /**
     * Transforms the individual into a Weka instance which can be used for
     * surrogate model training.
     * 
     * The instance does not have any dataset assigned. Use the dataset returned
     * by {@link #emptyDatasetFromSchema() emptyDatasetFromSchema} method.
     * 
     * @return the instance representing this individual WITHOUT the class value
     * set.
     */
    public Instance toWekaInstance() {

        Instance inst = new Instance(items.length + 1);
        inst.setDataset(emptyDatasetFromSchema());
        
        for (int i = 0; i < items.length; i++) {
            if (schema[i] instanceof SetSItem) {
                inst.setValue(i, items[i]);
                continue;
            }
            if (schema[i] instanceof BoolSItem) {
                inst.setValue(i, items[i].equals("False") ? 0.0 : 1.0);
                continue;
            }
            inst.setValue(i, Double.parseDouble(items[i]));
        }
        
        return inst;
    }
    
}
