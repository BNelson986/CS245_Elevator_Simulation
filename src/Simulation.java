import java.io.*;
import java.util.*;

public class Simulation {

    /***
     *  Locates the properties file in the current directory
     * @return name of properties files
     */
    public String getPropFileName(){
        //  Scan current folder
        File folder = new File(".");
        File[] filesInFolder = folder.listFiles();

        assert filesInFolder != null;

        for(File current : filesInFolder){
            if(current.isFile() && current.getName().endsWith(".properties")){
                hasPropFile = true;
                return current.getName();
            }
        }
        return "";
    }

    /***
     *  Gathers the properties file and returns it for the user
     * @param fileName
     * @return properties file
     * @throws IOException
     */
    public Properties readFile(String fileName) throws IOException {
        FileInputStream inFile;
        Properties properties;

        inFile = new FileInputStream(fileName);
        properties = new Properties();
        try {
            properties.load(inFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        inFile.close();

        return properties;
    }

    /*
     ****************************************
     *  Variables for simulation execution  *
     ****************************************
     */
    private List<Queue<Passenger>> floor;
    private Elevator elevator;
    private List<Long> timeToDest;

    /*
     **************************************
     *  Default variables for properties  *
     **************************************
     */
    private String structures = "linked";
    private int floors = 10;
    private float passengers = 0.03f;
    private int elevators = 1;
    private int elevatorCapacity = 10;
    private int duration = 500;
    private boolean hasPropFile = false;

    /*
     *****************************************************
     *  Utility functions to manipulate property values  *
     *****************************************************
     */

    //  Getter functions
    public boolean isHasPropFile() {
        return hasPropFile;
    }
    public float getPassengers() {
        return passengers;
    }
    public int getDuration() {
        return duration;
    }
    public int getElevatorCapacity() {
        return elevatorCapacity;
    }
    public int getElevators() {
        return elevators;
    }
    public int getFloors() {
        return floors;
    }
    public String getStructures() {
        return structures;
    }

    //  Setter functions
    public void setHasPropFile(boolean hasPropFile) {
        this.hasPropFile = hasPropFile;
    }
    public void setPassengers(float passengers) {
        this.passengers = passengers;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public void setElevatorCapacity(int elevatorCapacity) {
        this.elevatorCapacity = elevatorCapacity;
    }
    public void setElevators(int elevators) {
        this.elevators = elevators;
    }
    public void setFloors(int floors) {
        this.floors = floors;
    }
    public void setStructures(String structures) {
        this.structures = structures;
    }

    /***
     *  Passenger class used to represent individual passengers
     *  created on each floor during each tick.
     */
    private class Passenger {
    public Passenger(int start, int dest){
            startFloor = start;
            destFloor = dest;
            arrivalTime = System.currentTimeMillis();
        }
        private final int startFloor;
        private final int destFloor;
        private final long arrivalTime;
        private long destTime;
    }

    /***
     *  Elevator class used to represent simulated elevator
     *  can move up and down between floors to pick up and drop off
     *  Passenger objects
     */
    private class Elevator {
        private int currentFloor;
        private List<Passenger> passengers;

        public int moveUp(){

        }
        public int moveDown(){

        }
        public void dropOff(Passenger ){

        }
        public void pickUp(Passenger){

        }
    }
}

