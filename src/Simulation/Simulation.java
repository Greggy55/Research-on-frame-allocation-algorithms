package Simulation;

import PageReplacement.*;
import Memory.PhysicalMemory.PhysicalMemory;
import Memory.VirtualMemory.VirtualMemory;

public class Simulation {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[38;5;228m";

    private final int numberOfFrames;
    private final int totalNumberOfPages;
    private final int referenceStringLength;

    private final int localityLevel;
    private final double localityFactor;

    private VirtualMemory virtualMemory;
    private PhysicalMemory physicalMemory;

    private LRU lru;

    public Simulation(
            int numberOfFrames,
            int totalNumberOfPages,
            int referenceStringLength,

            int localityLevel,
            double localityFactor,
            
            boolean printLRU,

            boolean printDetails
    ) {
        this.numberOfFrames = numberOfFrames;
        this.totalNumberOfPages = totalNumberOfPages;
        this.referenceStringLength = referenceStringLength;

        this.localityLevel = localityLevel;
        this.localityFactor = localityFactor;

        virtualMemory = new VirtualMemory(totalNumberOfPages);
        physicalMemory = new PhysicalMemory(numberOfFrames);

        lru = new LRU(printLRU, printDetails, physicalMemory);
    }

    public void start(){
        generateReferenceString();
        //virtualMemory.generateExampleReferenceString();

        lru.run(virtualMemory.getReferenceString());
    }

    public void generateReferenceString(){
        if(localityLevel > 0){
            virtualMemory.generateReferenceStringWithLocality(referenceStringLength, localityLevel, localityFactor);
        }
        else{
            virtualMemory.generateRandomReferenceString(referenceStringLength);
        }
    }

    public void printParameters(){
        System.out.println("Pages: " + ANSI_YELLOW + virtualMemory.pagesToString() + ANSI_RESET);
        System.out.println("Reference string: " + ANSI_YELLOW + virtualMemory.referenceStringToString() + ANSI_RESET);
        System.out.println("Number of frames: " + ANSI_YELLOW + numberOfFrames + ANSI_RESET);
        System.out.println("Total number of pages: " + ANSI_YELLOW + totalNumberOfPages + ANSI_RESET);
        System.out.println("Reference string length: " + ANSI_YELLOW + referenceStringLength + ANSI_RESET);
    }

    public void printStatistics() {
        lru.printStatistics();
    }
}
