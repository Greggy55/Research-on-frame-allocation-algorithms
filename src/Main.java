import Simulation.Simulation;

public class Main {
    public static void main(String[] args) {

        int totalNumberOfFrames = 30;
        int totalGlobalReferenceStringLength = 100;
        int numberOfProcesses = 5;
        int maxNumberOfPages = 5;

        Simulation simulation = new Simulation(
                totalNumberOfFrames,
                totalGlobalReferenceStringLength,
                numberOfProcesses,
                maxNumberOfPages
        );

        simulation.start();
    }
}
