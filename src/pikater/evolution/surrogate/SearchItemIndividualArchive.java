package pikater.evolution.surrogate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import pikater.evolution.individuals.SearchItemIndividual;
import weka.core.Instance;
import weka.core.Instances;

public class SearchItemIndividualArchive {

    HashMap<String, SearchItemIndividual> archive = new HashMap<String, SearchItemIndividual>();
    
    public void add(SearchItemIndividual si) {
       if (!archive.containsKey(si.toString())) {
           archive.put(si.toString(), (SearchItemIndividual)si.clone());
       }
    }
    
    public boolean contains(SearchItemIndividual si) {
        return archive.containsKey(si.toString());
    }
    
    public double getFitness(SearchItemIndividual si) {
        return archive.get(si.toString()).getFitnessValue();
    }
    
    public int size() {
        return archive.size();
    }
    
    public ArrayList<SearchItemIndividual> getSavedIndividuals() {
        return new ArrayList<SearchItemIndividual>(archive.values());
    }
    
    public Instances getWekaDataSet(ModelValueProvider mvp, ModelInputNormalizer norm) {
        
        mvp.reset();
        
        Collection<SearchItemIndividual> inds = archive.values();
        
        if (inds.isEmpty()) {
            return null;
        }
        
        java.util.Iterator<SearchItemIndividual> it = inds.iterator();
        SearchItemIndividual first = it.next();
        
        Instances inst = first.emptyDatasetFromSchema();
        Instance firstInstance = first.toWekaInstance(norm);
        firstInstance.setClassValue(mvp.getModelValue(first, this, norm));
        firstInstance.setDataset(inst);
        inst.add(firstInstance);
        
        while (it.hasNext()) {
            SearchItemIndividual ind = it.next();
            Instance in = ind.toWekaInstance(norm);
            in.setClassValue(mvp.getModelValue(ind, this, norm));
            in.setDataset(inst);
            inst.add(in);
        }
        
        return inst;
    }
}