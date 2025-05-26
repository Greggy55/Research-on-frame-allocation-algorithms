package FrameAllocation;

import Memory.PhysicalMemory.PhysicalMemory;
import Memory.VirtualMemory.Page;

import PageReplacement.PageReplacement;
import Process.Process;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class PFFControl extends FrameAllocation{

    public static final int DELTA_T = PageReplacement.DELTA_T;

    public static final int LOWER_PFF_LIMIT = DELTA_T / 3;
    public static final int UPPER_PFF_LIMIT = DELTA_T * 2 / 3;
    //public static final int ABSOLUTE_PFF_LIMIT = DELTA_T * 5 / 6;

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
        if(!process.check()){
            return;
        }
        int PFF = process.getPFF();

        if(PFF < LOWER_PFF_LIMIT) {
            process.setCanGiveFrame(true);
            process.setNeedsFrame(false);

            Process[] suspendedProcesses = getSuspendedProcesses();
            if(suspendedProcesses.length > 0){
                unsuspendProcess(suspendedProcesses[0]);
            }
        }
        else if(PFF > UPPER_PFF_LIMIT) {
            process.setCanGiveFrame(false);
            process.setNeedsFrame(true);

            boolean found = findAndTakeAvailableFrame(process);
            if(!found && processes.length > 1){
                suspendProcess(process);
            }
        }
        else{
            process.setCanGiveFrame(false);
            process.setNeedsFrame(false);
        }
    }

    @Override
    public void unsuspendProcess(Process process) {
        super.unsuspendProcess(process);
        Process p = findProcessWithTheLargestPFF();
        int k = 0;
        while(!p.giveFrameTo(process)){
            p = findProcessWithKthLargestPFF(++k);
        }
        process.setSuspended(false);
    }

    @Override
    public void suspendProcess(Process process) {
        super.suspendProcess(process);
        process.setSuspended(true);
        int k = 0;
        while(process.getNumberOfFrames() > 0){
            k %= getActiveProcesses().length;
            Process p = findProcessWithKthLargestPFF(++k);
            process.giveFrameTo(p, true);
        }
    }

    public Process findProcessWithTheLargestPFF(){
        Process[] processes = getActiveProcesses();

        int maxPFF = 0;
        Process returnProcess = null;

        for(Process p : processes){
            int PFF = p.getPFF();
            if(PFF > maxPFF){
                maxPFF = PFF;
                returnProcess = p;
            }
        }

        return returnProcess;
    }

    public Process findProcessWithKthLargestPFF(int k) {
        Process[] processes = getActiveProcesses();

        if(processes == null){
            throw new IllegalStateException("No active processes found");
        }
        if (processes.length < k || k <= 0) {
            throw new IllegalArgumentException("Invalid k: " + k + "; number of active processes: " + processes.length);
        }

        Arrays.sort(processes, (p1, p2) -> Integer.compare(p2.getPFF(), p1.getPFF()));

        return processes[k - 1];
    }

    private boolean findAndTakeAvailableFrame(Process process) {
        Process[] processes = getActiveProcesses();
        for(Process p : processes) {
            if(p.canGiveFrame()) {
                if(p.giveFrameTo(process)){
                    process.setNeedsFrame(false);
                    if(print){
                        System.out.println(getName() + " Transmit frame: " + p.getTransmittedFrameBefore() + " -> " + p.getTransmittedFrameAfter());
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
