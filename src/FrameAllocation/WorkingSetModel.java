package FrameAllocation;

import Memory.PhysicalMemory.PhysicalMemory;
import Memory.VirtualMemory.Page;
import PageReplacement.PageReplacement;
import Process.Process;

import java.util.HashSet;

public class WorkingSetModel extends FrameAllocation{

    public static final int DELTA_T = PageReplacement.DELTA_T;

    private final FrameAllocation defaultAllocation;

    public WorkingSetModel(boolean print, boolean printDetails, Process[] processes, PhysicalMemory memory, Page[] globalReferenceString) {
        super(print, printDetails, processes, memory, globalReferenceString);

        defaultAllocation = new Equal(print, printDetails, processes, memory, globalReferenceString);
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
        defaultAllocation.staticAllocate();
    }

    @Override
    public void dynamicAllocate(Process process) {
         int numberOfRequiredFrames = 0;
         for(Process p : processes){
             numberOfRequiredFrames += getWorkingSetSize(p);
         }

         if(numberOfRequiredFrames <= memory.size()){
             
         }
    }

    public int getWorkingSetSize(Process process) {
        final int iter = process.getIter() - 1;
        final int shift = Math.min(iter, DELTA_T);

        Page[] refStr = process.getReferenceString();
        HashSet<String> hashSet = new HashSet<>();

        for(int i = 0; i <= shift; i++){
            hashSet.add(refStr[iter - i].idToString());
        }

        return hashSet.size();
    }
}
