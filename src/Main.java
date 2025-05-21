import Memory.PhysicalMemory.Frame;
import Memory.VirtualMemory.Page;
import Memory.VirtualMemory.VirtualMemory;
import Simulation.Simulation;

public class Main {
    public static void main(String[] args) {

        Frame.COLOR = false;
        Page.COLOR = false;

        VirtualMemory.INT_TYPE = false;

        int numberOfFrames = 10;
        int totalNumberOfPages = 20;
        int referenceStringLength = 100;
        // 4 5 12

        int localityLevel = 12;
        double localityFactor = 0.8;
        // 12 0.8

        boolean printFIFO = true;
        boolean printRAND = true;
        boolean printOPT = true;
        boolean printLRU = true;
        boolean printALRU = true;

        boolean printDetails = false;

        Simulation simulation = new Simulation(
                numberOfFrames,
                totalNumberOfPages,
                referenceStringLength,

                localityLevel,
                localityFactor,

                printLRU,

                printDetails
        );

        simulation.start();
        simulation.printParameters();
        simulation.printStatistics();
    }
}
