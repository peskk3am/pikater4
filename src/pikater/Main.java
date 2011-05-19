/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pikater;

public class Main {

    public static void main(String args[]) {

        String[] arg = {"pikater.properties"};

        if (args.length == 0)
            args = arg;

        jade.Boot.main(arg);

    }

}
