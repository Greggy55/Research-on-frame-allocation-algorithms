package Process;

import Memory.PhysicalMemory.Frame;
import Memory.VirtualMemory.Page;
import PageReplacement.*;
import Memory.PhysicalMemory.PhysicalMemory;
import Memory.VirtualMemory.VirtualMemory;

import java.util.HashSet;

public class Process {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[38;5;228m";

    private final int colorCode;

    private boolean canGiveFrame = false;
    private boolean needsFrame = false;
    private boolean isSuspended = false;

    private int numberOfFrames;
    private final int totalNumberOfPages;
    private final int referenceStringLength;

    private final int localityLevel;
    private final double localityFactor;

    private final VirtualMemory virtualMemory;
    private final PhysicalMemory physicalMemory;

    private int globalRefStringIndex = 0;
    private boolean completelyInGlobalRefStr = false;

    private String transmittedFrameBefore;
    private String transmittedFrameAfter;

    private LRU lru;

    private int numberOfSuspensions = 0;
    private int numberOfFramesTaken = 0;
    private int numberOfFramesReceived = 0;

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

        virtualMemory = new VirtualMemory(totalNumberOfPages, this);
        physicalMemory = new PhysicalMemory(numberOfFrames);

        colorCode = VirtualMemory.getColorCode();

        lru = new LRU(true, false, physicalMemory);
    }

    public int getColorCode() {
        return colorCode;
    }

    public void updateNumberOfFrames(int numberOfFrames) {
        this.numberOfFrames = numberOfFrames;
        lru.updateNumberOfFrames(numberOfFrames);
    }

    public int getNumberOfFrames() {
        return numberOfFrames;
    }

    public void runLRU(){
        lru.run(virtualMemory.getReferenceString());
    }

    public void runSingleIterationLRU(){
        lru.runSingleIteration(virtualMemory.getReferenceString());
    }

    public Page getCurrentPage(){
        return lru.getCurrentPage();
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

    public int getTotalNumberOfPages() {
        return totalNumberOfPages;
    }

    public Frame[] getFrameArray(){
        return physicalMemory.getFrameArray();
    }

    @Override
    public String toString() {
        return "\n\tPages: " + pagesToString() + "\n\tRefStr: " + referenceStringToString();
    }

    public String colored(){
        return ansi256(colorCode) + "process" + ANSI_RESET;
    }

    public static String ansi256(int code) {
        return "\u001B[38;5;" + code + "m";
    }

    public void resetLRU(boolean printLRU){
        lru = new LRU(printLRU, false, physicalMemory);
    }

    public int getPFF(){
        return lru.getPFF();
    }

    public boolean canGetPFF(){
        return lru.canGetPFF();
    }

    public boolean check(){
        return lru.check();
    }

    public int getWorkingSetSize(int deltaT) {
        final int iter = this.getIter() - 1;
        final int shift = Math.min(iter, deltaT);

        Page[] refStr = this.getReferenceString();
        HashSet<String> hashSet = new HashSet<>();

        for(int i = 0; i <= shift; i++){
            hashSet.add(refStr[iter - i].idToString());
        }

        return hashSet.size();
    }

    public boolean canGiveFrame() {
        return canGiveFrame;
    }

    public void setCanGiveFrame(boolean canGiveFrame) {
        this.canGiveFrame = canGiveFrame;
    }

    public boolean needsFrame() {
        return needsFrame;
    }

    public void setNeedsFrame(boolean needsFrame) {
        this.needsFrame = needsFrame;
    }

    public boolean isSuspended() {
        return isSuspended;
    }

    public void setSuspended(boolean suspended) {
        isSuspended = suspended;
        if(suspended){
            numberOfSuspensions++;
        }
    }

    public boolean giveFrameTo(Process process){
        return giveFrameTo(process, false);
    }

    public boolean giveFrameTo(Process process, boolean force){
        if(this.numberOfFrames <= 1 && !force){
            return false;
        }

        this.numberOfFrames--;
        this.numberOfFramesTaken++;
        process.numberOfFrames++;
        process.numberOfFramesReceived++;

        Frame transmittedFrame = this.physicalMemory.removeLastFrame();
        transmittedFrameBefore = transmittedFrame.toString();

        transmittedFrame.clear();
        transmittedFrame.setProcess(process);
        process.physicalMemory.addFrame(transmittedFrame);

        transmittedFrameAfter = transmittedFrame.toString();

        return true;
    }

    public String  getTransmittedFrameBefore() {
        return transmittedFrameBefore;
    }

    public String getTransmittedFrameAfter() {
        return transmittedFrameAfter;
    }

    public int getIter(){
        return lru.getIter();
    }

    public PhysicalMemory getPhysicalMemory() {
        return physicalMemory;
    }

    public String getStatistics(boolean squeeze){
        return lru.getStatistics(squeeze)
                + "["
                + ANSI_YELLOW + numberOfSuspensions + ANSI_RESET + ", "
                + ANSI_YELLOW +numberOfFramesTaken + ANSI_RESET + ", "
                + ANSI_YELLOW +numberOfFramesReceived + ANSI_RESET
                + "]";
    }

    public void resetStatistics() {
        lru.resetStatistics();
        numberOfFramesReceived = 0;
        numberOfFramesTaken = 0;
        numberOfSuspensions = 0;
    }

    public int getTotalPageFaultCount(){
        return lru.getTotalPageFaultCount();
    }

    public int getTotalThrashingCount(){
        return lru.getTotalThrashingCount();
    }

    public int getNumberOfSuspensions() {
        return numberOfSuspensions;
    }

    public int getNumberOfFramesTaken() {
        return numberOfFramesTaken;
    }

    public int getNumberOfFramesReceived() {
        return numberOfFramesReceived;
    }
}
