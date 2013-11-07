/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pikater.agents.metadataQueen;

import jade.util.leap.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pikater.ontology.messages.Metadata;
import pikater.ontology.messages.Attribute;
import pikater.ontology.messages.DataInstances;
import pikater.ontology.messages.Instance;
import pikater.ontology.messages.metadata.*;

/**
 * Class that can read metadata from the list of instances and attributes
 * @author Kuba
 */
public class MetadataReader {
    public Metadata computeMetadata(DataInstances data){
                Metadata metadata = new Metadata();		
							
                // number of instances
		metadata.setNumber_of_instances(data.getInstances().size());
		
		// number of attributes
                // we do not count targer attribute
		metadata.setNumber_of_attributes(data.getAttributes().size());

		// missing values
		boolean missing = false; 
		jade.util.leap.Iterator itr = data.getInstances().iterator();
		while(itr.hasNext()){
			Instance i = (Instance)itr.next();
			if (i.getMissing().size() != 0){
				missing = true;
			}			
		}		
		metadata.setMissing_values(missing);
		
		// data type
		String type = ""; 
		itr = data.getAttributes().iterator();
		while(itr.hasNext()){
			Attribute a = (Attribute)itr.next();
			if (type.isEmpty()){
				type = a.getType();
			}
			if (! a.getType().equals((type))){
				type = "Multivariate";
			}
					
		}		
		metadata.setAttribute_type(type);
		
		// default task 
                setTaskType(data, metadata);

                //Attributes
                readAttributesMetadata(data,metadata);  
                
                int c=metadata.getNumberOfCategorical();
                int r=metadata.getNumberOfReal();
                int i=metadata.getNumberOfInteger();
		return metadata;
    }
    
    private void setTaskType(DataInstances data, Metadata metadata)
    {
        if ( ((Attribute) data.getAttributes().get((data.getClass_index() >= 0 ? data.getClass_index() : data.getAttributes().size() - 1))).getType().equals("Numeric") ){
            metadata.setDefault_task("Regression");
        }
        else {
            metadata.setDefault_task("Classification");
        }
    }
    
    private void readAttributesMetadata(DataInstances data, Metadata metadata)
    {
        jade.util.leap.List attributeList=metadata.getAttribute_metadata_list();
        for (int i=0;i<metadata.getNumber_of_attributes();i++)
        {
            AttributeMetadata attMet=readAttributeMetadata(data, i);
            attributeList.add(attMet);
        }
    }
    
    private AttributeMetadata readAttributeMetadata(DataInstances data,int attributeNumber)
    {        
        AttributeType type=getAttributeType(data, attributeNumber);
        AttributeMetadata attributeMetadata=GetAttributeMetadataInstance(type);
        setBaseAttributeProperties(data, attributeMetadata, attributeNumber);
        if (type!=AttributeType.Categorical) setNumericalAttributeProperties(data, attributeMetadata, attributeNumber);
        else setCategoricalAttributeProperties(data, attributeMetadata, attributeNumber);
        if (type!=AttributeType.Real) countEntropies(data, attributeMetadata, attributeNumber);
        return attributeMetadata;
    }
    
    private void setBaseAttributeProperties(DataInstances data,AttributeMetadata metadata,int attributeNumber)
    {
        Attribute att=(Attribute)data.getAttributes().get(attributeNumber);
        metadata.setOrder(attributeNumber);  
        metadata.setName(att.getName());
        if (attributeNumber==data.getClass_index())metadata.setIsTarget(true);
        setRatioMissingValues(data, attributeNumber, metadata);
    }
    
    private void countEntropies(DataInstances data,AttributeMetadata metadata,int attributeNumber)
    {
        List<Object> values=new ArrayList<>();     
        List<Object> classValues=new ArrayList<>();        
        Iterator itr = data.getInstances().iterator();
        while(itr.hasNext()){                        
		Instance i = (Instance)itr.next();
		jade.util.leap.List missingList=i.getMissing();
                if ((boolean)missingList.get(attributeNumber))
                {
                    continue;
                }
                values.add(i.getValues().get(attributeNumber));
                classValues.add(i.getValues().get(data.getClass_index()));
        }
        double entropy=Entropy.CountEntropy(values);
        metadata.setEntropy(entropy);        
        double classEntropy=Entropy.CountEntropyClassAttribute(values, classValues);
        metadata.setAttributeClassEntropy(classEntropy);
    }
    
    private void setCategoricalAttributeProperties(DataInstances data,AttributeMetadata metadata,int attributeNumber)
    {
        Attribute att=(Attribute)data.getAttributes().get(attributeNumber);
        CategoricalAttributeMetadata met=(CategoricalAttributeMetadata)metadata;
        met.setNumberOfCategories(att.getValues().size());
    }
    
    private void setNumericalAttributeProperties(DataInstances data,AttributeMetadata metadata,int attributeNumber)
    {
        List<Double> values=new ArrayList<>();        
        NumericalAttributeMetadata met=(NumericalAttributeMetadata)metadata;
        Iterator itr = data.getInstances().iterator();
        while(itr.hasNext()){                        
		Instance i = (Instance)itr.next();
		jade.util.leap.List missingList=i.getMissing();
                if ((boolean)missingList.get(attributeNumber))
                {
                    continue;
                }
                values.add((Double)i.getValues().get(attributeNumber));
        }
        Collections.sort(values);
        met.setMin(values.get(0));
        met.setMax(values.get(values.size()-1));
        double average=0;
        double squareaverage=0;
        double n=values.size();
        for (int i=0;i<values.size();i++)
        {
            double currentvalue=values.get(i);
            average+=(currentvalue/n);
            squareaverage+=((currentvalue*currentvalue)/n);
        }
        double variation=squareaverage-(average*average);
        met.setAvg(average);
        met.setStandardDeviation(Math.sqrt(variation));
        int half=(int)Math.floor(values.size()/2);
        met.setMedian(values.get(half));
    }
    
    private void setRatioMissingValues(DataInstances data,int attributeNumber, AttributeMetadata attributeMetadata)
    {
        double numberOfValues=data.getInstances().size();
        double numberOfMissingValues=0;
        Iterator itr = data.getInstances().iterator();
		while(itr.hasNext()){                        
			Instance i = (Instance)itr.next();
			jade.util.leap.List missingList=i.getMissing();
                        if ((boolean)missingList.get(attributeNumber))
                        {
                            numberOfMissingValues++;
                        }
                }
          if (numberOfValues>0)
          {
              attributeMetadata.setRatioOfMissingValues(numberOfMissingValues/numberOfValues);
          }
    }  
    
    private AttributeMetadata GetAttributeMetadataInstance(AttributeType type)
    {
        switch (type){
            case Categorical: 
                return new CategoricalAttributeMetadata();
            case Integer: 
                return new IntegerAttributeMetadata();
            default: return new RealAttributeMetadata();
        }
    }
    
    private AttributeType getAttributeType(DataInstances data,int attributeNumber)
    {
        Attribute a = (Attribute)data.getAttributes().get(attributeNumber);
        if (a.getType().equals("NOMINAL"))
        {
            return AttributeType.Categorical;
        }
        boolean canBeInt=true;        
        Iterator itr = data.getInstances().iterator();
		while(itr.hasNext()){                        
			Instance i = (Instance)itr.next();
			jade.util.leap.List missingList=i.getMissing();
                        if ((boolean)missingList.get(attributeNumber))
                        {
                            continue;
                        }
                        else
                        {
                            jade.util.leap.List values=i.getValues();
                            Object value=values.get(attributeNumber);                            
                            if (value instanceof Double)
                            {
                                double doub=(double)value;
                                if (!((doub == Math.floor(doub)) && !Double.isInfinite(doub)))
                                {
                                    canBeInt= false;
                                }
                            }
                            else
                            {
                                return AttributeType.Categorical;
                            }
                        }
                }          
          if (canBeInt) return AttributeType.Integer;
          else return AttributeType.Real;
    }
}
