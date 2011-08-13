/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pikater.gui.java.improved;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;
import pikater.ontology.messages.Instance;

/**
 *
 * @author martin
 */
public class VisualisationFactory {
    
    public static ChartPanel getChartPanel(List<DataInstancesTableModel> dataList, int x, int y, float radius) {

        

        Shape[] shapes = {new Ellipse2D.Float(-radius/2, -radius/2, radius, radius),
        new Ellipse2D.Float(-radius/4, -radius/4, radius/2, radius/2),
        ShapeUtilities.createRegularCross(radius/2, radius/2), 
        ShapeUtilities.createDiagonalCross(radius/2, radius/2), ShapeUtilities.createDiamond(radius/2)};

        Paint[] paints = ChartColor.createDefaultPaintArray();

        DefaultXYItemRenderer renderer = new DefaultXYItemRenderer();
        renderer.setBaseLinesVisible(false);
        renderer.setBaseShapesFilled(false);
        renderer.setBaseShapesVisible(true);

        XYSeriesCollection dataset = new XYSeriesCollection();

        int series = 0;
        for (DataInstancesTableModel data : dataList) {

            HashMap<String, LinkedList<Instance>> classLists = new HashMap<String, LinkedList<Instance>>();

            String[] classes = data.getClasses();

            for (String s : classes) {
                classLists.put(s, new LinkedList<Instance>());
            }

            for (int i = 0; i < data.getRowCount(); i++) {
                String iClass = data.getValueAt(i, data.getClassIndex()).toString();
                classLists.get(iClass).add(data.getInstance(i));
            }

            for (String s : classes) {
                XYSeries dataSeries = new XYSeries(s);
                for (Instance i : classLists.get(s)) {
                    dataSeries.add(Float.parseFloat(i.getValues().get(x).toString()), Float.parseFloat(i.getValues().get(y).toString()));
                }
                dataset.addSeries(dataSeries);
            }

            for (int i = series*classes.length; i < series*classes.length + classes.length; i++) {
                renderer.setSeriesShape(i, shapes[series]);
                renderer.setSeriesPaint(i, paints[i - series*classes.length]);
            }
            
            series++;
        }

        final NumberAxis domainAxis = new NumberAxis(dataList.get(0).getColumnName(x));
        domainAxis.setAutoRangeIncludesZero(false);
        final NumberAxis rangeAxis = new NumberAxis(dataList.get(0).getColumnName(y));
        rangeAxis.setAutoRangeIncludesZero(false);

        final XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);

        final JFreeChart chart = new JFreeChart(plot);

        final ChartPanel panel = new ChartPanel(chart, true);
        panel.setPreferredSize(new java.awt.Dimension(500, 500));
        panel.setMinimumDrawHeight(10);
        panel.setMaximumDrawHeight(2000);
        panel.setMinimumDrawWidth(20);
        panel.setMaximumDrawWidth(2000);
        panel.setVisible(true);

        return panel;

    }

}
