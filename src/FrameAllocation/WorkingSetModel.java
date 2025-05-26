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

        updateWorkingSetSize(processes);

        if(numberOfRequiredFrames <= memory.size()){
            for(Process _ : processes){
                String memoryBeforeAllocate = memory.toString();
                allocateWSS();
                if(memoryBeforeAllocate.equals(memory.toString())){
                    break;
                }
            }

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
        int k = 0;
        while(process.getNumberOfFrames() > 0){
            k %= getActiveProcesses().length;
            Process p = findProcessWithKthLargestWSS(++k);
            process.giveFrameTo(p, true);
            if(print){
                System.out.print(process.getTransmittedFrameBefore() + " -> " + process.getTransmittedFrameAfter() + "\t");
            }
        }
        if(print){
            System.out.println();
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
        if(print){
            System.out.println(getName() + " Transmit frame: " + p.getTransmittedFrameBefore() + " -> " + p.getTransmittedFrameAfter());
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

    public Process findProcessWithKthLargestWSS(int k) {
        Process[] processes = getActiveProcesses();

        if(processes == null){
            throw new IllegalStateException("No active processes found");
        }
        if (processes.length < k || k <= 0) {
            throw new IllegalArgumentException("Invalid k: " + k + "; number of active processes: " + processes.length);
        }

        Arrays.sort(processes, (p1, p2) -> Integer.compare(
                workingSetSize.get(p2),
                workingSetSize.get(p1)
        ));

        return processes[k - 1];
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

    private void findDemandAndTransferFrame(Process process, Process[] processes, int processNumberOfFrames, int processWSS) {
        for(Process p : processes) {
            if(p.needsFrame()) {
                if(process.giveFrameTo(p)){
                    processNumberOfFrames--;
                    if(processNumberOfFrames == processWSS) {
                        process.setCanGiveFrame(false);
                        if(print){
                            System.out.println(getName() + " Transmit frame: " + process.getTransmittedFrameBefore() + " -> " + process.getTransmittedFrameAfter());
                        }
                        break;
                    }
                }
            }
        }
    }

    private void findAndTakeAvailableFrame(Process process, Process[] processes, int processNumberOfFrames, int processWSS) {
        for(Process p : processes) {
            if(p.canGiveFrame()) {
                if(p.giveFrameTo(process)){
                    processNumberOfFrames++;
                    if(processNumberOfFrames == processWSS) {
                        process.setNeedsFrame(false);
                        if(print){
                            System.out.println(getName() + " Transmit frame: " + p.getTransmittedFrameBefore() + " -> " + p.getTransmittedFrameAfter());
                        }
                        break;
                    }
                }
            }
        }
    }
}
