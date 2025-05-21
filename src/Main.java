import Simulation.Simulation;

public class Main {
    public static void main(String[] args) {

        int totalNumberOfFrames = 30;
        int totalGlobalReferenceStringLength = 100;
        int numberOfProcesses = 5;
        int minNumberOfPages = 3;
        int maxNumberOfPages = 10;

        Simulation simulation = new Simulation(
                totalNumberOfFrames,
                totalGlobalReferenceStringLength,
                numberOfProcesses,
                minNumberOfPages,
                maxNumberOfPages
        );

        simulation.start();
    }
}
