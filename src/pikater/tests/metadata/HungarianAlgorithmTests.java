package pikater.tests.metadata;

/**
 * Created with IntelliJ IDEA.
 * User: Kuba
 * Date: 24.8.13
 * Time: 17:28
 * To change this template use File | Settings | File Templates.
 */
import org.junit.Assert;
import org.junit.Test;
import pikater.metadata.HungarianAlgorithm;

public class HungarianAlgorithmTests {
    @Test
    public void testHAComputation() {
        //sample from wiki - http://en.wikipedia.org/wiki/Hungarian_algorithm
        double[][] distanceMatrix = new double[3][3];
        distanceMatrix[0][0]=1;
        distanceMatrix[0][1]=2;
        distanceMatrix[0][2]=3;

        distanceMatrix[1][0]=3;
        distanceMatrix[1][1]=3;
        distanceMatrix[1][2]=3;

        distanceMatrix[2][0]=3;
        distanceMatrix[2][1]=3;
        distanceMatrix[2][2]=2;

        HungarianAlgorithm ha=new HungarianAlgorithm(distanceMatrix);
        int[] result=ha.execute();
        Assert.assertArrayEquals("message", new int[]{0,1,2}, result);

        distanceMatrix = new double[4][3];
        distanceMatrix[0][0]=1;
        distanceMatrix[0][1]=2;
        distanceMatrix[0][2]=3;

        distanceMatrix[1][0]=30;
        distanceMatrix[1][1]=30;
        distanceMatrix[1][2]=30;

        distanceMatrix[2][0]=3;
        distanceMatrix[2][1]=3;
        distanceMatrix[2][2]=2;

        distanceMatrix[3][0]=3;
        distanceMatrix[3][1]=3;
        distanceMatrix[3][2]=3;

        ha=new HungarianAlgorithm(distanceMatrix);
        result=ha.execute();
        Assert.assertArrayEquals("message", new int[]{0,-1,2,1}, result);

        distanceMatrix = new double[3][3];
        distanceMatrix[0][0]=10;
        distanceMatrix[0][1]=2;
        distanceMatrix[0][2]=3;

        distanceMatrix[1][0]=3;
        distanceMatrix[1][1]=3;
        distanceMatrix[1][2]=1;

        distanceMatrix[2][0]=3;
        distanceMatrix[2][1]=3;
        distanceMatrix[2][2]=2;

        ha=new HungarianAlgorithm(distanceMatrix);
        result=ha.execute();
        Assert.assertArrayEquals("message", new int[]{1,2,0}, result);
    }
}
