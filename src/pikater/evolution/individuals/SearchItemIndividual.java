/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pikater.evolution.individuals;

import pikater.evolution.RandomNumberGenerator;
import pikater.ontology.messages.SearchItem;

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
    
}
