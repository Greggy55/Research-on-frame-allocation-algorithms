import Memory.PhysicalMemory.Frame;
import Memory.VirtualMemory.Page;
import Memory.VirtualMemory.VirtualMemory;
import PageReplacement.PageReplacement;
import Simulation.Simulation;

public class Main {
    public static void main(String[] args) {

        Frame.COLOR = true;
        Page.COLOR = true;

        VirtualMemory.INT_TYPE = true;

        PageReplacement.PRINT_SHORT = true;

        int totalNumberOfFrames = 40; // 20 - 40

        int minReferenceStringLength = 500; // 300-500
        int maxReferenceStringLength = 600; // 501-1000

        int numberOfProcesses = 10; // 6 - 16

        int minNumberOfPages = 30; // 30
        int maxNumberOfPages = 100; // 100

        boolean printLRU = false;

        boolean printEqual = false;
        boolean printProportional = false;
        boolean printPFFControl = false;
        boolean printZoneModel = true;

        Simulation simulation = new Simulation(
                totalNumberOfFrames,

                minReferenceStringLength,
                maxReferenceStringLength,

                numberOfProcesses,

                minNumberOfPages,
                maxNumberOfPages,

                printLRU,

                printEqual,
                printProportional,
                printPFFControl,
                printZoneModel
        );

        simulation.start();
        simulation.printParameters();
        simulation.printStatistics();
    }
}
