package FrameAllocation;

import Memory.PhysicalMemory.Frame;
import Memory.PhysicalMemory.PhysicalMemory;
import Memory.VirtualMemory.Page;

import Process.Process;

public class Equal extends FrameAllocation{

    public Equal(boolean print, boolean printDetails, Process[] processes, PhysicalMemory memory, Page[] globalReferenceString) {
        super(print, printDetails, processes, memory, globalReferenceString);
    }

    @Override
    public String getName() {
        return ANSI_GRAY + "Equal" + ANSI_RESET;
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public void staticAllocate() {
        int frameIndex = 0;
        Frame[] globalFrames = memory.getFrameArray();
        int numberOfFrames = globalFrames.length / processes.length;

        for(Process process : processes) {
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
}
