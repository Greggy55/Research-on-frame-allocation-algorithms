package FrameAllocation;

import Memory.PhysicalMemory.PhysicalMemory;
import Memory.VirtualMemory.Page;
import PageReplacement.PageReplacement;
import Process.Process;

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

        updateWorkingSetSize(processes);

        if(numberOfRequiredFrames <= memory.size()){
            //for(Process _ : processes){
            allocateWSS();
            //}

            Process[] suspendedProcesses = getSuspendedProcesses();
            if(suspendedProcesses.length > 0){
                unsuspendProcess(suspendedProcesses[0]);
            }
        }
        else{
            Process processToSuspend = findProcessWithTheLargestWSS();
            suspendProcess(processToSuspend);
        }
    }

    private void updateWorkingSetSize(Process[] processes) {
        numberOfRequiredFrames = 0;

        for(Process p : processes){
            int WSS = getWorkingSetSize(p);
            workingSetSize.put(p, WSS);
            numberOfRequiredFrames += WSS;
        }
    }

    @Override
    public void suspendProcess(Process process){
        super.suspendProcess(process);
        process.setSuspended(true);
        while(process.getNumberOfFrames() > 0){
            Process p = findProcessWithTheLargestWSS();
            process.giveFrameTo(p, true);
        }
    }

    @Override
    public void unsuspendProcess(Process process){
        super.unsuspendProcess(process);
        Process p = findProcessWithTheLargestWSS();
        while(!p.giveFrameTo(process)){
            updateWorkingSetSize(getActiveProcesses());
            p = findProcessWithTheLargestWSS();
        }
        process.setSuspended(false);
        updateWorkingSetSize(getActiveProcesses());
        allocateWSS();
    }

    public Process findProcessWithTheLargestWSS(){
        // assert workingSetSize is updated
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

    public void allocateWSS(){
        Process[] processes = getActiveProcesses();

        for(Process process : processes){
            int processNumberOfFrames = process.getNumberOfFrames();
            int processWSS = workingSetSize.get(process);

            if(processNumberOfFrames > processWSS) {
                process.setCanGiveFrame(true);
                process.setNeedsFrame(false);

                findDemandAndTransferFrame(process, processes, processNumberOfFrames, processWSS);
            }
            else if(processNumberOfFrames < processWSS) {
                process.setCanGiveFrame(false);
                process.setNeedsFrame(true);

                findAndTakeAvailableFrame(process, processes, processNumberOfFrames, processWSS);
            }
            else{
                process.setCanGiveFrame(false);
                process.setNeedsFrame(false);
            }
        }
    }

    private static void findDemandAndTransferFrame(Process process, Process[] processes, int processNumberOfFrames, int processWSS) {
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

    private static void findAndTakeAvailableFrame(Process process, Process[] processes, int processNumberOfFrames, int processWSS) {
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
}
