package FrameAllocation;

import Memory.PhysicalMemory.PhysicalMemory;
import Memory.VirtualMemory.Page;

import PageReplacement.PageReplacement;
import Process.Process;

public class PFFControl extends FrameAllocation{

    public static final int LOWER_PFF_LIMIT = PageReplacement.DELTA_T / 3;
    public static final int UPPER_PFF_LIMIT = PageReplacement.DELTA_T * 2 / 3;

    private final FrameAllocation defaultAllocation;

    public PFFControl(boolean print, boolean printDetails, Process[] processes, PhysicalMemory memory, Page[] globalReferenceString) {
        super(print, printDetails, processes, memory, globalReferenceString);

        defaultAllocation = new Equal(print, printDetails, processes, memory, globalReferenceString);
    }

    @Override
    public String getName() {
        return ANSI_GRAY + "PFF Control" + ANSI_RESET;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public void staticAllocate() {
        defaultAllocation.staticAllocate();
    }

    @Override
    public void dynamicAllocate(Process process) {
        if(!process.canGetPFF() || !process.checkPFF()){
            return;
        }
        int PFF = process.getPFF();

        if(PFF < LOWER_PFF_LIMIT) {
            process.setCanGiveFrame(true);
            process.setNeedsFrame(false);
        }
        else if(PFF > UPPER_PFF_LIMIT) {
            process.setCanGiveFrame(false);
            process.setNeedsFrame(true);

            for(Process p : processes) {
                if(p.canGiveFrame()) {
                    if(p.giveFrameTo(process)){
                        break;
                    }
                }
            }
        }
        else{
            process.setCanGiveFrame(false);
            process.setNeedsFrame(false);
        }
    }
}
