/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pikater.metadata;

/**
 *
 * @author Kuba
 */
public class NumericalAttributeMetadata extends AttributeMetadata  {
    private double _min;
    private double _max;
    private double _avg;
    private double _median;
    private double _standardDeviation;

    public double getStandardDeviation() {
        return _standardDeviation;
    }

    public void setStandardDeviation(double _standardDeviation) {
        this._standardDeviation = _standardDeviation;
    }

    public double getAvg() {
        return _avg;
    }

    public void setAvg(double _avg) {
        this._avg = _avg;
    }

    public double getMax() {
        return _max;
    }

    public void setMax(double _max) {
        this._max = _max;
    }

    public double getMedian() {
        return _median;
    }

    public void setMedian(double _median) {
        this._median = _median;
    }

    public double getMin() {
        return _min;
    }

    public void setMin(double _min) {
        this._min = _min;
    }
    
    @Override
    public  String getType()
    {
        return "Numerical";
    }
    
}
