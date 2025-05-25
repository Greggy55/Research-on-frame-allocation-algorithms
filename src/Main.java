import Memory.PhysicalMemory.Frame;
import Memory.VirtualMemory.Page;
import Memory.VirtualMemory.VirtualMemory;
import Simulation.Simulation;

public class Main {
    public static void main(String[] args) {

        Frame.COLOR = true;
        Page.COLOR = true;

        VirtualMemory.INT_TYPE = true;

        int totalNumberOfFrames = 10;

        int minReferenceStringLength = 100;
        int maxReferenceStringLength = 101;

        int numberOfProcesses = 2;

        int minNumberOfPages = 2;
        int maxNumberOfPages = 15;

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
