/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pikater.agents.metadataQueen;

import java.util.*;

/**
 *
 * @author Kuba
 */
public class Entropy {
    public static double CountEntropy(List<Object> values)
    {
        double n=values.size();
        double result=0;
        Map<Object,Integer> hash=new HashMap<>();
        for (Object o:values)
        {
            if (hash.containsKey(o))
            {
                int current=hash.get(o);
                hash.put(o, current+1);
            }
            else
            {
                hash.put(o, 1);
            }            
        }
        for (Object o: hash.keySet())
        {
            int count=hash.get(o);
            double pt=count/n;
            double logpt=Math.log(pt)/Math.log(2);
            result+=pt*logpt;
        }
        result=result*-1;
        return result;
    }
    
    public static double CountEntropyClassAttribute(List<Object> attributeValues,List<Object> classValues)
    {   
        double result=0;
        Set<Object> targetValues=new HashSet();
        Set<Object> sourceValues=new HashSet();
        for (Object o:classValues)
        {
            if (!targetValues.contains(o))
            {
                targetValues.add(o);
            }            
        }
        for (Object o:attributeValues)
        {
            if (!sourceValues.contains(o))
            {
                sourceValues.add(o);
            }            
        }
        //count H(A(v))
        
        double n=attributeValues.size();
        for (Object o:sourceValues)
        {
            double nav=getNumberOfInstancesWithSpecifiedAttributeValue(attributeValues, o);
            double hav=0;
            for (Object target:targetValues)
            {
                double ntav=getNumberOfInstancesWithSpecifiedAttributeClassValue(attributeValues, classValues, o, target);
                double ratio=ntav/nav;
                if (ratio==0) continue;
                hav+=(ratio)*Math.log(ratio)/Math.log(2);
            }
            hav=hav*-1;         
            result+=(nav*hav)/n;            
        }        
        return result;
    }
    
    private static double getNumberOfInstancesWithSpecifiedAttributeValue(List<Object> attributeValues,Object fixedValue)
    {
        double result=0;
        for (Object o:attributeValues)
        {
            if (o.equals(fixedValue)) result ++;
        }
        return result;
    }
    
    private static double getNumberOfInstancesWithSpecifiedAttributeClassValue(List<Object> attributeValues,List<Object> classValues,Object fixedAttributeValue,Object fixedClassValue)
    {
        double result=0;
        for (int i=0;i<attributeValues.size();i++)
        {
            if (attributeValues.get(i).equals(fixedAttributeValue))
            {
                if (classValues.get(i).equals(fixedClassValue)) result++;
            }
        }
        return result;
    }
    
}
