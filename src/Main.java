import Process.Process;

public class Main {
    public static void main(String[] args) {
        int numberOfFrames = 10;
        int totalNumberOfPages = 20;
        int referenceStringLength = 100;

        int localityLevel = 12;
        double localityFactor = 0.8;

        Process process = new Process(
                numberOfFrames,
                totalNumberOfPages,
                referenceStringLength,

                localityLevel,
                localityFactor
        );

        process.generateReferenceString();
        process.runLRU();
    }
}
