import Memory.PhysicalMemory.Frame;
import Memory.VirtualMemory.Page;
import Memory.VirtualMemory.VirtualMemory;
import Simulation.Simulation;

public class Main {
    public static void main(String[] args) {

        Frame.COLOR = false;
        Page.COLOR = true;

        VirtualMemory.INT_TYPE = true;

        int totalNumberOfFrames = 30;
        int maxReferenceStringLength = 100;
        int numberOfProcesses = 5;
        int minNumberOfPages = 3;
        int maxNumberOfPages = 10;

        Simulation simulation = new Simulation(
                totalNumberOfFrames,
                maxReferenceStringLength,
                numberOfProcesses,
                minNumberOfPages,
                maxNumberOfPages
        );

        simulation.start();
    }
}
