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

        int totalNumberOfFrames = 30;

        int minReferenceStringLength = 500;
        int maxReferenceStringLength = 501;

        int numberOfProcesses = 5;

        int minNumberOfPages = 30;
        int maxNumberOfPages = 100;

        boolean printLRU = true;

        boolean printEqual = false;
        boolean printProportional = false;
        boolean printPFFControl = true;
        boolean printZoneModel = false;

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
