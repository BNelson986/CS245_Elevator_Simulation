import java.io.IOException;
import java.util.Properties;

public class Main {
    /***
     *  Main driver for elevator simulation
     *  Basic functionality is as follows:
     *  a)  Checks for presence of properties file
     *      1)  Gets file name of properties file if present
     *      2)  Open properties file and set values found in file
     *  b)
     * @param args
     */
    public static void main(String[] args) throws IOException {
        Simulation sim = new Simulation();

        String fileName = sim.getPropFileName();
        if(sim.isHasPropFile()) {
            Properties prop = sim.readFile(fileName);

            sim.startSim(prop);
        }
        else {
            sim.startSim();
        }
        System.out.println("Success!");
    }
}
