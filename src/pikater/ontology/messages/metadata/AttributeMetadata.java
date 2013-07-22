/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pikater.ontology.messages.metadata;
import jade.content.Concept;

/**
 * Metadata of one attribute
 * @author Kuba
 */
public class AttributeMetadata implements Concept {
    private double _ratioOfMissingValues=0;
    private int _order;
    private double _entropy=0;
    private double _attributeClassEntropy=0;

    public double getAttributeClassEntropy() {
        return _attributeClassEntropy;
    }

    public void setAttributeClassEntropy(double _attributeClassEntropy) {
        this._attributeClassEntropy = _attributeClassEntropy;
    }

    public double getEntropy() {
        return _entropy;
    }

    public void setEntropy(double _entropy) {
        this._entropy = _entropy;
    }
    private String _name;    
    private boolean _isTarget;

    public boolean isIsTarget() {
        return _isTarget;
    }

    public void setIsTarget(boolean _isTarget) {
        this._isTarget = _isTarget;
    }

    public int getOrder() {
        return _order;
    }

    public void setOrder(int _order) {
        this._order = _order;
    }

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public boolean getHasMissingValues() {
        return getRatioOfMissingValues()==0;
    }
    
    public double getRatioOfMissingValues() {
        return _ratioOfMissingValues;
    }

    public void setRatioOfMissingValues(double _ratioOfMissingValues) {
        this._ratioOfMissingValues = _ratioOfMissingValues;
    }
    
    public String getType()
    {
        return "Base";
    }
}
