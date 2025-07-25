package FrameAllocation;

import Memory.PhysicalMemory.Frame;
import Memory.PhysicalMemory.PhysicalMemory;
import Memory.VirtualMemory.Page;

import Process.Process;

public class Proportional extends FrameAllocation{
    public Proportional(boolean print, boolean printDetails, Process[] processes, PhysicalMemory memory, Page[] globalReferenceString) {
        super(print, printDetails, processes, memory, globalReferenceString);
    }

    @Override
    public String getName() {
        return ANSI_GRAY + "Proportional" + ANSI_RESET;
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public void staticAllocate() {
        int frameIndex = 0;
        final int totalNumberOfPages = getTotalNumberOfPages();
        Frame[] globalFrames = memory.getFrameArray();

        for(Process process : processes) {
            int numberOfFrames = getRatio(process, globalFrames, totalNumberOfPages);
            //System.out.println(numberOfFrames);

            if(numberOfFrames + frameIndex >= globalFrames.length) {
                numberOfFrames = globalFrames.length - frameIndex;
            }

            process.updateNumberOfFrames(numberOfFrames);
            Frame[] processFrames = process.getFrameArray();

            for(int i = 0; i < numberOfFrames; i++) {
                processFrames[i] = globalFrames[i + frameIndex];
                processFrames[i].setProcess(process);
            }

            frameIndex += numberOfFrames;
        }

        allocateFreeFrames(globalFrames);
    }

    @Override
    public void dynamicAllocate(Process p) {
        throw new UnsupportedOperationException("Not supported for static frame allocation algorithms.");
    }

    @Override
    public void unsuspendProcess(Process process) {
        throw new UnsupportedOperationException("Not supported for static frame allocation algorithms.");
    }

    @Override
    public void suspendProcess(Process process) {
        throw new UnsupportedOperationException("Not supported for static frame allocation algorithms.");
    }

    private static int getRatio(Process process, Frame[] globalFrames, int totalNumberOfPages) {
        return (int) Math.round((double) globalFrames.length * process.getTotalNumberOfPages() / totalNumberOfPages);
    }

    public int getTotalNumberOfPages(){
        int result = 0;
        for(Process process : processes) {
            result += process.getTotalNumberOfPages();
        }
        return result;
    }
}
