package FrameAllocation;

import Memory.PhysicalMemory.PhysicalMemory;
import Memory.VirtualMemory.Page;
import Process.Process;

public class WorkingSetModel extends FrameAllocation{
    public WorkingSetModel(boolean print, boolean printDetails, Process[] processes, PhysicalMemory memory, Page[] globalReferenceString) {
        super(print, printDetails, processes, memory, globalReferenceString);
    }

    @Override
    public String getName() {
        return ANSI_GRAY + "Working-Set Model" + ANSI_RESET;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public void staticAllocate() {

    }

    @Override
    public void dynamicAllocate(Process process) {

    }
}
