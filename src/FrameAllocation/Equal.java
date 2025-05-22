package FrameAllocation;

import Memory.PhysicalMemory.Frame;
import Memory.PhysicalMemory.PhysicalMemory;
import Memory.VirtualMemory.Page;

import Process.Process;

public class Equal extends FrameAllocation{

    public Equal(boolean print, boolean printDetails, Process[] processes, PhysicalMemory memory, Page[] globalReferenceString) {
        super(print, printDetails, processes, memory, globalReferenceString);
        name = ANSI_GRAY + "Equal" + ANSI_RESET;
    }

    @Override
    public void allocate() {
        int frameIndex = 0;
        Frame[] globalFrames = memory.getFrameArray();
        int numberOfFrames = globalFrames.length / processes.length;

        for(Process process : processes) {
            process.updateNumberOfFrames(numberOfFrames);
            Frame[] processFrames = process.getFrameArray();

            for(int i = 0; i < numberOfFrames; i++) {
                processFrames[i] = globalFrames[i + frameIndex];
                processFrames[i].setColorCode(process.getColorCode());
            }

            frameIndex += numberOfFrames;
        }
    }
}
