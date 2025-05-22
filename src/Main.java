import Memory.PhysicalMemory.Frame;
import Memory.VirtualMemory.Page;
import Memory.VirtualMemory.VirtualMemory;
import Simulation.Simulation;

public class Main {
    public static void main(String[] args) {

        Frame.COLOR = true;
        Page.COLOR = true;

        VirtualMemory.INT_TYPE = true;

        int totalNumberOfFrames = 30;
        int maxReferenceStringLength = 150;
        int numberOfProcesses = 5;
        int minNumberOfPages = 3;
        int maxNumberOfPages = 10;

        boolean printLRU = false;

        boolean printEqual = false;
        boolean printProportional = true;
        boolean printPFFControl = false;
        boolean printZoneModel = true;

        Simulation simulation = new Simulation(
                totalNumberOfFrames,
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
