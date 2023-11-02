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

        //  Get potential properties file name
        String propFile = sim.getPropFileName();

        if(sim.isHasPropFile()){
            try {
                Properties properties = sim.readFile(propFile);

                //  Once open, set all properties as specified in file
                sim.setStructures(properties.getProperty("structures"));
                sim.setFloors(Integer.parseInt(properties.getProperty("floors")));
                sim.setPassengers(Float.parseFloat(properties.getProperty("passengers")));
                sim.setElevators(Integer.parseInt(properties.getProperty("elevators")));
                sim.setElevatorCapacity(Integer.parseInt(properties.getProperty("elevatorCapacity")));
                sim.setDuration(Integer.parseInt(properties.getProperty("duration")));
            }
            catch (IOException ioe){
                ioe.printStackTrace();
            }
        }

    }
}
