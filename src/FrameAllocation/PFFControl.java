package FrameAllocation;

import Memory.PhysicalMemory.PhysicalMemory;
import Memory.VirtualMemory.Page;

import Process.Process;

public class PFFControl extends FrameAllocation{

    public static final int LOWER_PFF_LIMIT = 3;
    public static final int UPPER_PFF_LIMIT = 7;

    private final FrameAllocation defaultAllocation;

    public PFFControl(boolean print, boolean printDetails, Process[] processes, PhysicalMemory memory, Page[] globalReferenceString) {
        super(print, printDetails, processes, memory, globalReferenceString);
        name = ANSI_GRAY + "PFF Control" + ANSI_RESET;
        isDynamic = true;

        defaultAllocation = new Equal(print, printDetails, processes, memory, globalReferenceString);
    }

    @Override
    public void staticAllocate() {
        defaultAllocation.staticAllocate();
    }

    @Override
    public void dynamicAllocate() {

    }


}
