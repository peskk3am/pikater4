package pikater.evolution.surrogate;

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
    
    public Instances getWekaDataSet() {
        
        Collection<SearchItemIndividual> inds = archive.values();
        
        if (inds.isEmpty()) {
            return null;
        }
        
        java.util.Iterator<SearchItemIndividual> it = inds.iterator();
        SearchItemIndividual first = it.next();
        
        Instances inst = first.emptyDatasetFromSchema();
        Instance firstInstance = first.toWekaInstance();
        firstInstance.setClassValue(this.getFitness(first));
        firstInstance.setDataset(inst);
        inst.add(firstInstance);
        
        while (it.hasNext()) {
            SearchItemIndividual ind = it.next();
            Instance in = ind.toWekaInstance();
            in.setClassValue(-this.getFitness(ind));
            in.setDataset(inst);
            inst.add(in);
        }
        
        return inst;
    }
}