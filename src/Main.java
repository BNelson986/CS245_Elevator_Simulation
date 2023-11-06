public class Main {
    /***
     *  Main driver for elevator simulation
     *  Instantiates Simulation class using
     *  args[0].
     * @param args name of property file
     */

    public static void main(String[] args) throws Exception {
        Simulation sim;

        //  Check for properties file argument
        if(args.length > 0){
            sim = new Simulation(args[0]);
        }
        else{
            sim = new Simulation();
        }

        sim.startSim();

        System.out.println("Success!");
    }
}
