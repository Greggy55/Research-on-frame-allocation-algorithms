package Process;

import Memory.VirtualMemory.Page;
import PageReplacement.*;
import Memory.PhysicalMemory.PhysicalMemory;
import Memory.VirtualMemory.VirtualMemory;

public class Process {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[38;5;228m";

    private int numberOfFrames;
    private final int totalNumberOfPages;
    private final int referenceStringLength;

    private final int localityLevel;
    private final double localityFactor;

    private VirtualMemory virtualMemory;
    //private PhysicalMemory physicalMemory;

    private int globalRefStringIndex = 0;
    private boolean completelyInGlobalRefStr = false;

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
        //physicalMemory = new PhysicalMemory(numberOfFrames);

        lru = new LRU(false, false, new PhysicalMemory(numberOfFrames));
    }

    public void updateNumberOfFrames(int numberOfFrames) {
        this.numberOfFrames = numberOfFrames;
        lru.updateNumberOfFrames(numberOfFrames);
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

    public Page[] getReferenceString(){
        return virtualMemory.getReferenceString();
    }

    public int getAndIncrementGlobalRefStringIndex(){
        return globalRefStringIndex++;
    }

    public boolean isCompletelyInGlobalRefStr(){
        return completelyInGlobalRefStr;
    }

    public void setCompletelyInGlobalRefStr(boolean completelyInGlobalRefStr) {
        this.completelyInGlobalRefStr = completelyInGlobalRefStr;
    }

    public String referenceStringToString() {
        return virtualMemory.referenceStringToString();
    }

    public String pagesToString() {
        return virtualMemory.pagesToString();
    }

    @Override
    public String toString() {
        return "Pages: " + pagesToString() + "\nRefStr: " + referenceStringToString();
    }
}
