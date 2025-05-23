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
    public void dynamicAllocate(Process process) {
        if(!process.canGetPFF()){
            return;
        }
        int PFF = process.getPFF();

        if(PFF < LOWER_PFF_LIMIT) {
            process.setCanGiveFrame(true);
            process.setNeedsFrame(false);

//            for(Process p : processes) {
//                if(p.needsFrame()) {
//                    if(process.giveFrameTo(p)){
//                        break;
//                    }
//                }
//            }
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
