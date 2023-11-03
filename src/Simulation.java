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

    /***
     *  The function to use when there is no properties file
     *  present in the project folder
     */
    public void startSim(){

    }

    /***
     *  The function to use when there is a properties file
     *  present in the project folder.
     * @param prop
     */
    public void startSim(Properties prop) throws Exception {
        //  Set pseudo-random seed for generator
        Random rand = new Random(System.currentTimeMillis());

        //  With presence of properties file, set all properties from file
        setStructures(prop.getProperty("structures"));
        setFloors(Integer.parseInt(prop.getProperty("floors")));
        setPassengers(Float.parseFloat(prop.getProperty("passengers")));
        setElevators(Integer.parseInt(prop.getProperty("elevators")));
        setElevatorCapacity(Integer.parseInt(prop.getProperty("elevatorCapacity")));
        setDuration(Integer.parseInt(prop.getProperty("duration")));

        //  Set all structures to specified structure
        if(getStructures().matches("linked")){
            floor = new LinkedList<>();
            for(int i = 0; i < getFloors(); i++){
                QueueType<Passenger> linkedQueue = new LinkedQueue<>();
                floor.add(linkedQueue);
            }

            elevator = new LinkedList<>();
            for(int i = 0; i < getElevators(); i++){
                elevator.add(new Elevator(getStructures()));
            }

            timeToDest = new LinkedList<>();
        }
        else if (getStructures().matches("array")){
            floor = new ArrayList<>(getFloors());
            elevator = new ArrayList<>(getElevators());
            timeToDest = new ArrayList<>();
        }

        //  Set duration to variable to reduce function calls to duration
        int ticksLeft = getDuration();

        /*
         ***************************************
         *  Order of operations for each tick  *
         ***************************************
         *  1)  Passenger Generation
         *      a)  Start & Destination must be different
         *  2)  Elevator movement
         *      a)  No more than 5 floors at once
         *      b)  Prioritize the shortest travel distance
         *      c)  Cannot reverse direction unless 1 Passenger or less onboard
         *      d)  Will NOT go to floor with zero passengers waiting
         *          to be picked up or dropped off
         *  3)  Passenger Drop-Off
         *      a)  Only dropped off at the destination floor
         *      b)  Once dropped off set Passenger destTime
         *      c)  Add difference of end and start time for
         *          each passenger to timeToDest List
         *  4)  Passenger Pick-Up
         *      a)  Will NOT exceed elevator capacity
         *      b)  Passenger going down will NOT get on
         *          elevator going up and vice versa
         */
        while(ticksLeft-- > 0){
            //  Start with max of 25 passengers per tick FOR NOW
            //  UPDATE AFTER SUCCESSFUL COMPLETION
            double chanceOfGeneration = rand.nextDouble(0.00, 1.00);

            // 1) Generate Passengers
            if (chanceOfGeneration < getPassengers()) {
                int passengersGenerated = rand.nextInt(25);

                for(int i = 0; i < passengersGenerated; i++){
                    int startFloor = 0, destFloor = 0;

                    //  Generate new start/end until they are different
                    while(startFloor == destFloor){
                        startFloor = rand.nextInt(getFloors());
                        destFloor = rand.nextInt(getFloors());
                    }

                    floor.get(startFloor).enqueue(new Passenger(destFloor));
                }
            }

            // 2) Elevator movement
            for(int i = 0; i < getElevators(); i++){
                //  Track if elevator moved to allow only 1 per tick
                boolean moved = false;
                //  Moving Up
                if(elevator.get(i).getDirection() == 'U'){
                    for(int elevMove = 0; elevMove < 5 && !moved; elevMove++){
                        int currFloor = elevMove + elevator.get(i).getCurrentFloor();
                        //  3) Drop-Off Passengers
                        if(elevator.get(i).atDestination(currFloor)){
                            //  Move elevator before dropping off passengers
                            elevator.get(i).move(currFloor);
                            moved = true;
                            
                            elevator.get(i).dropOff(elevator.get(i).arrived(currFloor));
                        }
                        //  4) Pick-Up Passengers
                        if(!floor.get(currFloor).isEmpty() && !elevator.get(i).isFull()){
                            if(!moved){
                                elevator.get(i).move(currFloor);
                                moved = true;
                            }
                            //  Pick up as many as possible from floor
                            while(!floor.get(currFloor).isEmpty() && !elevator.get(i).isFull()){
                                elevator.get(i).pickUp(floor.get(currFloor).dequeue());
                            }
                        }
                    }
                }
            }
        }

    }
    /*
     ****************************************
     *  Variables for simulation execution  *
     ****************************************
     */
    private List<QueueType<Passenger>> floor;
    private List<Elevator> elevator;
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
     *  created on each floor during each tick. Instantiation
     *  MUST include starting floor and destination
     */
    private class Passenger {
        public Passenger(int dest){
            destFloor = dest;
            arrivalTime = System.currentTimeMillis();
        }

        private final int destFloor;
        private final long arrivalTime;

        public int getDestFloor() {
            return this.destFloor;
        }

        public long getArrivalTime() {
            return this.arrivalTime;
        }

        public int compareTo(int currFloor) {
            return this.destFloor - currFloor;
        }
    }

    /***
     *  Elevator class used to represent simulated elevator
     *  can move up and down between floors to pick up and drop off
     *  Passenger objects
     */
    private class Elevator {
        private int currentFloor;
        private List<Passenger> passengers;
        private char direction;


        /***
         *  Constructs elevator object using structure defined in
         *  properties file and starts out on bottom floor
         */
        public Elevator(String structure){
            if(structure.matches("linked")){
                passengers = new LinkedList<>();
            } else{
                passengers = new ArrayList<>();
            }

            //  Start elevator on bottom floor
            currentFloor = 0;
        }

        /*
            Separate private functions to move the elevator up or down
         */
        private void moveUp(int floor){ this.currentFloor = floor; }
        private void moveDown(int floor){ this.currentFloor = floor; }

        /***
         *  Removes all passenger in "arrivals" list
         *  from the "passengers" list
         * @param arrivals list of passengers to remove
         */
        public void dropOff(List<Passenger> arrivals){
            for(Passenger passenger : arrivals){
                //  Log total time for each passenger to reach destination
                timeToDest.add(System.currentTimeMillis() - passenger.getArrivalTime());
                passengers.remove(passenger);
            }
        }

        /***
         *  Add passenger to the elevator
         * @param pickup passengers to insert into elevator
         */
        public void pickUp(Passenger pickup){
            passengers.add(pickup);
        }
        public void move(int floor){
            if(this.direction == 'U'){ moveUp(floor); }
            else{ moveDown(floor); }
        }

        /***
         *  Returns a list of all current passengers in the
         *  elevator who have reached their destination
         * @param currentFloor Destination Floor to check against
         * @return arrivals "List of all who have arrived"
         */
        public List<Passenger> arrived(int currentFloor){
            List<Passenger> arrivals;
            if(getStructures().matches("linked")){
                arrivals = new LinkedList<>();
            }
            else{
                arrivals = new ArrayList<>();
            }

            //  Find all passengers who have arrived
            for (Passenger passenger : passengers) {
                //  Find passengers who have arrived to the list for drop-off
                if (passenger.destFloor == currentFloor) {
                    arrivals.add(passenger);
                }
            }
            return arrivals;
        }

        /***
         *  Called on each floor to check for arrivals
         * @param currentFloor floor to check for drop-offs
         * @return true if there is a passenger to drop at currentFloor
         */
        public boolean atDestination(int currentFloor){
            for(Passenger passenger : passengers){
                if(passenger.destFloor == currentFloor){ return true; }
            }
            return false;
        }

        public boolean isEmpty(){
            return this.passengers.isEmpty();
        }
        public boolean isFull(){
            return this.passengers.size() >= getElevatorCapacity();
        }
        public int getCurrentFloor(){
            return this.currentFloor;
        }

        public char getDirection() {
            return this.direction;
        }

        public void setCurrentFloor(int currentFloor) {
            this.currentFloor = currentFloor;
        }

        public void setDirection(char direction) {
            this.direction = direction;
        }
    }
}

