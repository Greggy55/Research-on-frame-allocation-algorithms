import Memory.PhysicalMemory.Frame;
import Memory.VirtualMemory.Page;
import Memory.VirtualMemory.VirtualMemory;
import Simulation.Simulation;

public class Main {
    public static void main(String[] args) {

        Frame.COLOR = true;
        Page.COLOR = true;

        VirtualMemory.INT_TYPE = true;

//        int totalNumberOfFrames = 30;
//
//        int minReferenceStringLength = 100;
//        int maxReferenceStringLength = 101;
//
//        int numberOfProcesses = 5;
//
//        int minNumberOfPages = 5;
//        int maxNumberOfPages = 15;

        int totalNumberOfFrames = 14;

        int minReferenceStringLength = 100;
        int maxReferenceStringLength = 101;

        int numberOfProcesses = 3;

        int minNumberOfPages = 2;
        int maxNumberOfPages = 20;

        boolean printLRU = false;

        boolean printEqual = true;
        boolean printProportional = true;
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
