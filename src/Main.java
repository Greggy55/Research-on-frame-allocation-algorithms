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

        PageReplacement.PRINT_REFERENCE = true;

        int totalNumberOfFrames = 20;

        int minReferenceStringLength = 100;
        int maxReferenceStringLength = 101;

        int numberOfProcesses = 5;

        int minNumberOfPages = 10;
        int maxNumberOfPages = 35;

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
    }
}
