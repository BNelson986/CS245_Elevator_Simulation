import java.io.*;
import java.util.*;

public class Simulation {
    /**
     *  Automatically begins the elevator simulation
     *  using values from properties file
     * @param fileName name of properties file to use
     */
    Simulation(String fileName) throws IOException {
        Properties prop = readFile(fileName);

        //  With presence of properties file, set all properties from file
        setStructures(prop.getProperty("structures"));
        setFloors(Integer.parseInt(prop.getProperty("floors")));
        setPassengers(Float.parseFloat(prop.getProperty("passengers")));
        setElevators(Integer.parseInt(prop.getProperty("elevators")));
        setElevatorCapacity(Integer.parseInt(prop.getProperty("elevatorCapacity")));
        setDuration(Integer.parseInt(prop.getProperty("duration")));
    }

    /**
     *  Automatically begins the elevator simulation
     *  using the default values
     */
    Simulation() {
        //  ONLY TEMPORARY REMOVE AFTER DEBUGGING
        //  With presence of properties file, set all properties from file
        setStructures("linked");
        setFloors(20);
        setPassengers(0.10f);
        setElevators(1);
        setElevatorCapacity(10);
        setDuration(500);
    }

    /**
     *  Gathers the properties file and returns it for the user
     * @param fileName name of properties file to read
     * @return properties file
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

    /**
     *  Starts simulation of elevator by first
     *  attempting to read a properties file,
     *  if there is not one will continue on
     *  with simulation using default values.
     */
    public void startSim() {
        //  Set pseudo-random seed for generator
        Random rand = new Random(System.currentTimeMillis());

        //  Set all structures to specified structure
        if(getStructures().matches("linked")){
            floor = new LinkedList<>();
            for(int i = 0; i < getFloors(); i++){
                List<Passenger> linkedList = new LinkedList<>();
                floor.add(linkedList);
            }

            elevator = new LinkedList<>();
            for(int i = 0; i < getElevators(); i++){
                elevator.add(new Elevator(getStructures()));
            }

            timeToDest = new LinkedList<>();
        }
        else if (getStructures().matches("array")){
            floor = new ArrayList<>();
            elevator = new ArrayList<>();
            timeToDest = new ArrayList<>();

            for(int i = 0; i < getFloors(); i++){
                List<Passenger> arrayList = new ArrayList<>();
                floor.add(arrayList);
            }
            for(int i = 0; i < getElevators(); i++){
                elevator.add(new Elevator(getStructures()));
            }
        }

        //  Set duration to variable to reduce function calls to duration
        int ticksLeft = getDuration();
        System.out.println(getDuration());
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
        //  Used to prevent excess looping before first arrival
        boolean firstPassenger = false;

        while(ticksLeft-- > 0){

            //  1) Passenger Generation
            for(int f = 0; f < getFloors(); f++){

                float chanceOfGeneration = rand.nextFloat(0.00f, 1.00f);

                if (chanceOfGeneration < getPassengers()) {

                    int destFloor = rand.nextInt(getFloors());

                    //  Generate new start/end until they are different
                    while(f == destFloor){
                        destFloor = rand.nextInt(getFloors());
                    }

                    floor.get(f).add(new Passenger(destFloor));

                    firstPassenger = true;
                }
            }

            // 2) Elevator movement
            for(int i = 0; i < getElevators() && firstPassenger; i++){

                //  Initialized local vars to reduce function calls
                int currFloor = elevator.get(i).getCurrentFloor();
                char elevDirection = elevator.get(i).getDirection();

                //  Elevator is empty, can change direction to reach nearest passenger
                if(elevator.get(i).isEmpty()){
                    currFloor = getNearestPassenger(currFloor, elevDirection);
                    elevator.get(i).move(currFloor);
                    elevDirection = elevator.get(i).getDirection();

                    pickUp(currFloor, i, elevDirection);
                }
                else{
                    elevatorMovement(i, currFloor, elevDirection);
                }

                //  UNCOMMENT THE FOLLOWING LINES TO TEST PROPER FUNCTIONALITY
                if(elevator.get(i).passengers.size() > getElevatorCapacity()){
                    System.out.println("Elevator " + i + " is over capacity");
                }
                for(Passenger passenger : elevator.get(i).passengers){
                    if(passenger.direction(elevator.get(i).getCurrentFloor()) != elevator.get(i).getDirection()){
                        System.out.println("Passenger on Elevator: " + i + " is going the wrong direction.");
                        System.out.println("Passenger: " + passenger +
                                " Destination Floor: " + passenger.destFloor +
                                " Current Floor: " + elevator.get(i).getCurrentFloor() +
                                " Elevator Direction: " + elevator.get(i).getDirection());
                    }
                }
                if(elevator.get(i).getCurrentFloor() < 0){
                    System.out.println("Elevator: " + i + " is out of bounds. (LOW)");
                }
                if(elevator.get(i).getCurrentFloor() >= getFloors()){
                    System.out.println("Elevator: " + i + " is out of bounds. (HIGH)");
                }
            }
        }

        //  Perform data analysis/collection after successful execution
        int totalPassengers = timeToDest.size();
        long avgTime = getAvgTimeToDest(timeToDest);
        long shortestTime = getShortestTimeToDest(timeToDest);
        long longestTime = getLongestTimeToDest(timeToDest);

        System.out.println("Total passengers serviced by " + elevators + " elevator(s): " + totalPassengers);
        System.out.println("Average travel time for passengers: " + avgTime + " ms.");
        System.out.println("Shortest travel time of any passenger: "+ shortestTime + " ms.");
        System.out.println("Longest travel time of any passenger: " + longestTime + " ms.");
    }

    /**
     *  Moves the elevator up and down the building
     *  to deliver passengers to the floors they need
     *  to go to. A passenger going down will not get
     *  on an elevator going up, and vice versa.
     * @param i elevator's number
     * @param currFloor floor that the elevator is on
     * @param elevDirection direction the elevator is moving
     */
    private void elevatorMovement(int i, int currFloor, char elevDirection){
        boolean moved = false;
        int elevMove = 0;

        if(elevDirection == 'U'){
            while(currFloor < getFloors() && elevMove++ < 5){
                //  Drop-Off Passengers
                if (elevator.get(i).atDestination(currFloor)) {
                    if(elevMove > 1) {
                        //  Move elevator before dropping off passengers
                        elevator.get(i).move(currFloor);
                        moved = true;
                    }

                    elevator.get(i).dropOff(elevator.get(i).arrived(currFloor));
                }

                //  Pick-Up Passengers
                if (hasPickUps(currFloor, elevDirection) && !elevator.get(i).isFull()) {
                    if (elevMove > 1 && !moved) {
                        elevator.get(i).move(currFloor);
                        moved = true;
                    }
                    pickUp(currFloor, i, elevDirection);
                }
                //  Nothing more to do during this tick if moved
                if(moved){
                    break;
                }
                currFloor++;
            }
            //  If 'moved' still false, no drop-offs within 5 floors move max distance (5 floors)
            if(!moved){
                elevator.get(i).move(currFloor);
            }
        }

        else if (elevDirection == 'D') {
            while(currFloor >= 0 && elevMove++ < 5) {
                //  Drop-Off Passengers
                if (elevator.get(i).atDestination(currFloor)) {
                    //  Move elevator before dropping off passengers
                    elevator.get(i).move(currFloor);
                    moved = true;

                    elevator.get(i).dropOff(elevator.get(i).arrived(currFloor));
                }

                //  Pick-Up Passengers
                if (hasPickUps(currFloor, elevDirection) && !elevator.get(i).isFull()) {
                    if (!moved) {
                        elevator.get(i).move(currFloor);
                        moved = true;
                    }
                    pickUp(currFloor, i, elevDirection);
                }
                if(moved){
                    break;
                }
                currFloor--;
            }

            //  If 'moved' still false, no drop-offs within 5 floors move max distance (5 floors)
            if(!moved){
                elevator.get(i).move(currFloor);
            }
        }
    }

    /**
     *  Determines if a floor has valid passengers
     *  to pick up
     * @param currFloor floor to check for passengers
     * @param elevDirection direction elevator is moving
     * @return true if at least one passenger
     * from the floor can get on
     */
    private boolean hasPickUps(int currFloor, char elevDirection){
        for(Passenger pass : floor.get(currFloor)){
            if(pass.direction(currFloor) == elevDirection){
                return true;
            }
        }
        return false;
    }

    /**
     *  Picks up all passengers possible and adds
     *  them to the elevator. Passenger will not board
     *  an elevator going in the wrong direction with passengers
     *  on board. If elevator empty, direction change is allowed
     *  once first passenger is picked up.
     * @param currFloor floor where elevator is at
     * @param i index of the elevator object
     * @param elevDirection direction elevator is moving
     */
    private void pickUp(int currFloor, int i, char elevDirection) {
        List<Passenger> toRemove;

        if(getStructures().matches("linked")){
            toRemove = new LinkedList<>();
        }
        else{
            toRemove = new ArrayList<>();
        }

        //  Pick up with empty elevator (Also change direction if needed)
        for(Passenger passenger : floor.get(currFloor)){
            if(elevator.get(i).isFull()) {
                break;
            }
            //  Verify elevator is going in the correct direction for each passenger
            if(passenger.direction(currFloor) == elevDirection){
                //  Add passenger to elevator
                elevator.get(i).add(passenger);

                //  Remove passenger from floor list
                toRemove.add(passenger);
            }
            //  Set direction to first passenger's on current floor
            else if(elevator.get(i).isEmpty()){
                //  Add passenger to elevator
                elevator.get(i).add(passenger);

                //  Change direction of elevator to match first passenger
                elevator.get(i).setDirection(passenger.direction(currFloor));
                //  Update local direction variable
                elevDirection = elevator.get(i).getDirection();

                //  Remove passenger from floor list
                toRemove.add(passenger);
            }
        }

        floor.get(currFloor).removeAll(toRemove);
    }

    /**
     *  Finds the shortest distance for the elevator
     *  to travel to find the next passenger.
     *  (ONLY USED FOR EMPTY ELEVATORS)
     * @param currFloor floor the elevator is currently on
     * @param elevDirection direction elevator is already moving
     * @return floor where the nearest passenger is waiting
     */
    private int getNearestPassenger(int currFloor, char elevDirection){
        List<Integer> floorsWithPassengers;

        if(getStructures().matches("linked")) {
            floorsWithPassengers = new LinkedList<>();
        }
        else {
            floorsWithPassengers = new ArrayList<>();
        }
        for(int j = 0; j < getFloors(); j++){
            if(!floor.get(j).isEmpty()){
                floorsWithPassengers.add(j);
            }
        }

        if(floorsWithPassengers.isEmpty()){
            return currFloor;
        }

        return getClosestFloor(currFloor, elevDirection, floorsWithPassengers);
    }

    /**
     *  Returns the closest floor in the list,
     *  with respect to currFloor
     * @param currFloor current floor elevator is at
     * @param elevDirection direction the elevator is moving
     * @param floorsWithPassengers List of all floor indexes with passengers waiting
     * @return index of closet floor to currFloor
     */
    private int getClosestFloor(int currFloor, char elevDirection, List<Integer> floorsWithPassengers) {
        int closestFloor = floorsWithPassengers.get(0);

        for(int i = 1; i < floorsWithPassengers.size(); i++){
            if(Math.abs(currFloor - closestFloor) > Math.abs(currFloor - floorsWithPassengers.get(i))){
                closestFloor = floorsWithPassengers.get(i);
            }
            //  Prioritize same direction if distance is the same
            else if(Math.abs(currFloor - closestFloor) == Math.abs(currFloor - floorsWithPassengers.get(i))){
                if(currFloor - floorsWithPassengers.get(i) > 0 && elevDirection == 'U'){
                    closestFloor = floorsWithPassengers.get(i);
                }
                else if(currFloor - floorsWithPassengers.get(i) < 0 && elevDirection == 'D'){
                    closestFloor = floorsWithPassengers.get(i);
                }
            }
        }
        //  Prevent over 5 floor movement
        if(Math.abs(currFloor - closestFloor) > 5){
            if(elevDirection == 'U' || currFloor == 0){
                if(currFloor + 5 >= getFloors()){
                    return currFloor - 5;
                }
                return currFloor + 5;
            }
            else if(elevDirection == 'D' || currFloor == getFloors() - 1){
                if(currFloor - 5 < 0){
                    return currFloor + 5;
                }
                return currFloor - 5;
            }
        }
        return closestFloor;
    }

    /**
     *  Get the average time taken for a passenger to
     *  reach their destination
     * @param times List of passenger travel times
     * @return average of 'times' list
     */
    public long getAvgTimeToDest(List<Long> times){
        if(times.isEmpty()){ return 0; }
        long sum = 0;

        for(Long time : times){
            sum += time;
        }
        return sum / times.size();
    }

    /**
     *  Given a list of times, return the smallest value
     * @param times list of travel times for passengers
     * @return shortest time of any passenger in simulation
     */
    public long getShortestTimeToDest(List<Long> times){
        if(times.isEmpty()){ return 0; }
        //  Start with max long value to find minimum
        long minTime = Long.MAX_VALUE;

        for(Long time : times){
            minTime = Math.min(minTime, time);
        }

        return minTime;
    }

    /**
     *  Given a list of times, return the largest value
     * @param times list of travel times for passengers
     * @return largest time of any passenger in simulation
     */
    public long getLongestTimeToDest(List<Long> times){
        if(times.isEmpty()){ return 0; }
        //  Start with min long value to find maximum
        long maxTime = Long.MIN_VALUE;

        for(Long time : times){
            maxTime = Math.max(maxTime, time);
        }
        return maxTime;

    }

    /*
     ****************************************
     *  Variables for simulation execution  *
     ****************************************
     */
    private List<List<Passenger>> floor;
    private List<Elevator> elevator;
    private List<Long> timeToDest;

    /*
     **************************************
     *  Default variables for properties  *
     **************************************
     */
    private String structures = "linked";
    private int floors = 10;
    private int elevators = 1;
    private int elevatorCapacity = 10;
    private int duration = 500;
    private float passengers = 0.03f;


    /*
     *****************************************************
     *  Utility functions to manipulate property values  *
     *****************************************************
     */

    public float getPassengers() { return passengers; }
    public int getDuration() { return duration; }
    public int getElevatorCapacity() { return elevatorCapacity; }
    public int getElevators() { return elevators; }
    public int getFloors() { return floors; }
    public String getStructures() { return structures; }

    public void setPassengers(float passengers) { this.passengers = passengers; }
    public void setDuration(int duration) { this.duration = duration; }
    public void setElevatorCapacity(int elevatorCapacity) { this.elevatorCapacity = elevatorCapacity; }
    public void setElevators(int elevators) { this.elevators = elevators; }
    public void setFloors(int floors) { this.floors = floors; }
    public void setStructures(String structures) { this.structures = structures; }

    /**
     *  Passenger class used to represent individual passengers
     *  created on each floor during each tick. Destination floor
     *  and Starting floor must be different.
     */
    private static class Passenger {
        public Passenger(int dest){
            destFloor = dest;
            arrivalTime = System.currentTimeMillis();
        }

        private final int destFloor;
        private final long arrivalTime;
        public long getArrivalTime() {
            return arrivalTime;
        }

        /**
         *  Given the current floor, return whether
         *  passenger is going up or down to determine
         *  if they are allowed to get on the elevator
         * @param currFloor floor the passenger is on 
         * @return 'U' for Up, 'D' for Down
         */
        public char direction(int currFloor) {
            if(this.destFloor > currFloor){
                return 'U';
            }
            return 'D';
        }
    }

    /**
     *  Elevator class used to represent a simulated elevator
     *  can move up and down between floors to pick up and drop off
     *  Passenger objects
     */
    private class Elevator {
        private int currentFloor;
        private final List<Passenger> passengers;
        private char direction;


        /***
         *  Constructs elevator object using structure defined in
         *  properties file and starts out on bottom floor
         */
        public Elevator( String structure){
            if(structure.matches("linked")){
                passengers = new LinkedList<>();
            }
            else{
                passengers = new ArrayList<>();
            }

            //  Start elevator on bottom floor
            currentFloor = 0;
            direction = 'U';
        }

        /**
         *  Moves the elevator up by the specified
         *  amount and set 'direction'
         * @param floor specified floor to move to
         */
        private void moveUp(int floor){
            setDirection('U');
            currentFloor = floor;
        }

        /**
         *  Moves the elevator down by the specified
         *  amount and set 'direction'
         * @param floor specified floor to move to
         */
        private void moveDown(int floor){
            setDirection('D');
            currentFloor = floor;
        }

        /**
         *  Removes all passenger in "arrivals" list
         *  from the "passengers" list and logs the time
         *  taken for each passenger to reach their destination
         * @param arrivals list of passengers to remove
         */
        public void dropOff(List<Passenger> arrivals){
            for(Passenger passenger : arrivals){
                //  Log total time for each passenger to reach destination
                timeToDest.add(System.currentTimeMillis() - passenger.getArrivalTime());
                passengers.remove(passenger);
            }
        }

        /**
         *  Moves the elevator to the specified floor.
         *  Calls either moveUp or moveDown depending on
         *  where the elevator is
         * @param floor the floor to move the elevator to
         */
        public void move(int floor){
            if(floor > getCurrentFloor()){
                moveUp(floor);
            }
            else if(floor < getCurrentFloor()){
                moveDown(floor);
            }
        }

        /**
         *  Returns a list of all current passengers in the
         *  elevator who have reached their destination
         * @param currentFloor Destination Floor to check against
         * @return list of all passengers who can be dropped off
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

        /**
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
            return passengers.isEmpty();
        }
        public boolean isFull(){
            return passengers.size() >= getElevatorCapacity();
        }
        public int getCurrentFloor(){
            return currentFloor;
        }
        public char getDirection() { return direction; }
        public void setDirection(char dir) { direction = dir; }
        public void add(Passenger p) { passengers.add(p); }
    }
}

