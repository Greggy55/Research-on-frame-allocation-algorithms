package FrameAllocation;

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
        System.out.println("allocate");
    }
}
