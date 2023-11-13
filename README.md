# CS245 Elevator Simulation

# To run simulation:
    Without a properties file: 
        execute - "java Main"
    
    OR

    With a properties file: 
        execute - "java Main fileName.properties" (Where 'fileName' is the properties file you will provide)


# Properties accepted by simulation:
    structures: (Will accept either array or linked) Tells simulation which data structure style to use
        for each created structure during execution.
        
    floors: (Will accept any integer >= 2) Tells simulation how many floors to create in virtual building.
    
    passengers: (Will accept any value between 0.0f and 1.0f) The percentage of a passenger being generated
        on any floor at the beginning of each tick.

    elevators: (Will accept any integer >= 1) Tells the simulation how many elevators will be created for
        the virtual building.

    elevatorCapacity: (Will accept any integer >= 1) Tells the simulation the maximum occupency of each elevator.
        
    duration: (Will accept any integer >= 1) Tells the simulation how many ticks to run for.


# Flow of Simulation:    
    1)  Main executed in terminal.
        1a) If properties file included, instantiate "Simulation" object with file name.
        1b) If no properties file, instantiate "Simulation" object using default values.
                Default Values:
                    structures = linked
                    floors = 10
                    passengers = 0.03f
                    elevators = 1
                    elevatorCapacity = 10
                    duration = 500

    2)  "Simulation" object, reads properties file and sets properties.  
    
    3)  "Simulation" object creates necessary structures, using "structures" property specified.              

    4)  Start simulation using 'duration' property.
        4a) Does not allow elevator movement until after first passenger is generated.
        4b) Chance of passenger generation is evaluated once per floor per tick.

    5)  After execution of simulation, collect and prints data gathered during simulation.


# Classes:
     +  Simulation:
        -   structures : String
        -   elevators : int
        -   elevatorCapacity: int
        -   passengers : float
        -   duration : int
        -   floors : int
        -   timeToDest : List<Long>
        -   elevator : List<Elevator>
        -   floor : List<Floor>
    ---------------------------------
        +   readFile(String) : Properties                   "Reads file specified by user and returns a properties object of that file"
        +   startSim() : void                               "Starts the simulation"
        +   getAvgTimeToDest() : long                       "Gets the average time for a passenger to reach their destination"
        +   getShortestTimeToDest() : long                  "Gets the shortest time for a passenger to reach their destination"
        +   getLongestTimeToDest() : long                   "Gets the longest time for a passenger to reach their destination"
        -   getNearestPassenger(int, char) : int            "Returns the floor of the nearest passenger"
        -   getClosestFloor(int, char, List<Integer>) : int "Returns the closest floor to elevator from the list of floors with passengers"
        -   hasPickUps(int, char) : boolean                 "Determines if a floor has a passenger that is able to be picked up"
        -   pickUp(int, int, char) : void                   "Picks up all valid passengers from the current floor"
        -   elevatorMovement(int, int, char) : void         "Moves the elevator to the next best floor"

    -   Simulation.Floor
        -   up : Queue<Passenger> {final}
        -   down: Queue<Passenger> {final}
    ---------------------------------
        +   add(Passenger) : void       "Adds the passenger to the proper Queue for the direction they are going"

    -   Simuation.Elevator:
        - currentFloor : int
        - direction : char
        - passengers : List <Passenger> {final}
    ---------------------------------
        +   atDestination(int) : boolean        "Returns true if any passengers can be dropped off at the floor"
        +   dropOff(List<Passenger>) : void     "Drops off all passengers in the List and adds their total time to timeToDest list"
        +   add(Passenger) : void               "Adds the passenger to the 'passengers' list"
        +   arrived(int) : List<Passenger>      "Returns a list of all passengers who are at their destination floor"
        +   move(int) : void                    "Moves the elevator to the specified floor"
        -   moveUp(int) : void                  "Moves the elevator up and sets the direction"
        -   moveDown(int) : void                "Moves the elevator down and sets the direction"

    -   Simulation.Passenger:
        -   startFloor : int
        -   destFloor : int {final}
        -   arrivalTime : long {final}
        -   direction : char {final}
    --------------------------------
        +   getArrivalTime() : long     "Returns the time the passenger was created"
        +   direction(int) : char       "Returns the direction the passenger needs to go to get to their destination" 
