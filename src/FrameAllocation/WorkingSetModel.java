package FrameAllocation;

import Memory.PhysicalMemory.PhysicalMemory;
import Memory.VirtualMemory.Page;
import PageReplacement.PageReplacement;
import Process.Process;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class WorkingSetModel extends FrameAllocation{

    public static final int DELTA_T = PageReplacement.DELTA_T;

    private final FrameAllocation defaultAllocation;

    private int numberOfRequiredFrames = 0;
    private HashMap<Process, Integer> workingSetSize = new HashMap<Process, Integer>();

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
        if(!process.check()){
            return;
        }
        Process[] processes = getActiveProcesses();

        //System.out.println("Start dynamic allocate");
        updateWorkingSetSize(processes);

        //System.out.println("Number of required frames: " + numberOfRequiredFrames);
        if(numberOfRequiredFrames <= memory.size()){
            //System.out.println("UPDATE NOF");
            for(Process p : processes){
                //System.out.println("Update: " + p.getNumberOfFrames() + " -> " + workingSetSize.get(p));
                allocateWSS(workingSetSize);
                //System.out.println(memory);
            }

            Process[] suspendedProcesses = getSuspendedProcesses();
            if(suspendedProcesses.length > 0){
                //System.out.println("-- Unsuspended process: " + suspendedProcesses[0]);
                unsuspendProcess(suspendedProcesses[0], workingSetSize);
            }
        }
        else{
            //System.out.println("SUSPEND PROCESS");
            Process processToSuspend = findProcessWithTheLargestWSS(workingSetSize);
            //System.out.println("Process to suspend: " + processToSuspend);
            suspendProcess(processToSuspend, workingSetSize);
        }
    }

    private void updateWorkingSetSize(Process[] processes) {
        numberOfRequiredFrames = 0;

        //System.out.println("Get WSSs");
        for(Process p : processes){
            int WSS = getWorkingSetSize(p);
            //System.out.println("WSS: " + WSS);
            workingSetSize.put(p, WSS);
            numberOfRequiredFrames += WSS;
        }
    }

    public void suspendProcess(Process process, HashMap<Process, Integer> workingSetSize){
        process.setSuspended(true);
        //System.out.println("Suspending process:\nactive processes: " + Arrays.toString(getActiveProcesses()) + "\nSuspended memory: " + process.getPhysicalMemory());
        while(process.getNumberOfFrames() > 0){
            //System.out.println(process.getNumberOfFrames());
            Process p = findProcessWithTheLargestWSS(workingSetSize);
            process.giveFrameTo(p, true);
        }
    }

    public void unsuspendProcess(Process process, HashMap<Process, Integer> workingSetSize){
        Process p = findProcessWithTheLargestWSS(workingSetSize);
        while(!p.giveFrameTo(process)){
            updateWorkingSetSize(getActiveProcesses());
            p = findProcessWithTheLargestWSS(workingSetSize);
            //System.out.println("oui");
        }
        process.setSuspended(false);
        updateWorkingSetSize(getActiveProcesses());
        allocateWSS(workingSetSize);
    }

    public Process findProcessWithTheLargestWSS(HashMap<Process, Integer> workingSetSize){
        Process[] processes = getActiveProcesses();

        int maxWSS = 0;
        Process returnProcess = null;

        for(Process p : processes){
            int WSS = workingSetSize.get(p);
            if(WSS > maxWSS){
                maxWSS = WSS;
                returnProcess = p;
            }
        }

        return returnProcess;
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

    public void allocateWSS(HashMap<Process, Integer> workingSetSize){
        Process[] processes = getActiveProcesses();

        for(Process process : processes){
            int processNumberOfFrames = process.getNumberOfFrames();
            int processWSS = workingSetSize.get(process);

            if(processNumberOfFrames > processWSS) {
                process.setCanGiveFrame(true);
                process.setNeedsFrame(false);

                for(Process p : processes) {
                    if(p.needsFrame()) {
                        if(process.giveFrameTo(p)){
                            processNumberOfFrames--;
                            if(processNumberOfFrames == processWSS) {
                                process.setCanGiveFrame(false);
                                break;
                            }
                        }
                    }
                }
            }
            else if(processNumberOfFrames < processWSS) {
                process.setCanGiveFrame(false);
                process.setNeedsFrame(true);

                for(Process p : processes) {
                    if(p.canGiveFrame()) {
                        if(p.giveFrameTo(process)){
                            processNumberOfFrames++;
                            if(processNumberOfFrames == processWSS) {
                                process.setNeedsFrame(false);
                                break;
                            }
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
}
