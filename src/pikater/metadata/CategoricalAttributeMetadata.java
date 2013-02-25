/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pikater.metadata;

/**
 *
 * @author Kuba
 */
public class CategoricalAttributeMetadata extends AttributeMetadata {
    private int _numberOfCategories;

    public int getNumberOfCategories() {
        return _numberOfCategories;
    }

    public void setNumberOfCategories(int _numberOfCategories) {
        this._numberOfCategories = _numberOfCategories;
    }
    
    @Override
    public  String getType()
    {
        return "Categorical";
    }
    
}
