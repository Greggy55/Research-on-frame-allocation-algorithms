package Process;

import PageReplacement.*;
import Memory.PhysicalMemory.PhysicalMemory;
import Memory.VirtualMemory.VirtualMemory;

public class Process {
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

    public Process(
            int numberOfFrames,
            int totalNumberOfPages,
            int referenceStringLength,

            int localityLevel,
            double localityFactor
    ) {
        this.numberOfFrames = numberOfFrames;
        this.totalNumberOfPages = totalNumberOfPages;
        this.referenceStringLength = referenceStringLength;

        this.localityLevel = localityLevel;
        this.localityFactor = localityFactor;

        virtualMemory = new VirtualMemory(totalNumberOfPages);
        physicalMemory = new PhysicalMemory(numberOfFrames);

        lru = new LRU(false, false, physicalMemory);
    }

    public void runLRU(){
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
}
