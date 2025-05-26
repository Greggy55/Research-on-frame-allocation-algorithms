package FrameAllocation;

import Memory.PhysicalMemory.Frame;
import Memory.PhysicalMemory.PhysicalMemory;
import Memory.VirtualMemory.Page;

import Process.Process;

import java.util.ArrayList;

public abstract class FrameAllocation {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[38;5;228m";
    public static final String ANSI_GREEN = "\u001B[38;5;120m";
    public static final String ANSI_GRAY = "\u001B[38;5;244m";

    protected boolean print;
    protected boolean printDetails;

    protected final Process[] processes;
    protected final Page[] globalReferenceString;
    protected final PhysicalMemory memory;

    public FrameAllocation(boolean print, boolean printDetails, Process[] processes, PhysicalMemory memory, Page[] globalReferenceString){
        this.print = print;
        this.printDetails = printDetails;
        this.processes = processes;
        this.memory = memory;
        this.globalReferenceString = globalReferenceString;
    }

    public abstract String getName();
    public abstract boolean isDynamic();

    public abstract void staticAllocate();
    public abstract void dynamicAllocate(Process process);

    public void suspendProcess(Process process){
        if(print){
            System.out.printf("%s " + "Suspend " + process.colored() + " ", getName());
        }
    }

    public void unsuspendProcess(Process process){
        if(print){
            System.out.printf("%s " + "Unsuspend " + process.colored() + "\n", getName());
        }
    }

    public void run(){
        memory.clear();

        if(print){
            System.out.println();
            System.out.println("-".repeat(100));
            System.out.println();
            System.out.printf("%s Run\n", getName());
        }

        staticAllocate();

        if(print){
            System.out.println();
            System.out.printf("%s " + memory + "\n", getName());
        }
    }

    public PhysicalMemory getMemory() {
        return memory;
    }

    public void printMemory(){
        System.out.println(getName() + " " + memory);
    }

    public void allocateFreeFrames(Frame[] globalFrames){
        Process[] processes = getActiveProcesses();

        for(int i = globalFrames.length - 1; globalFrames[i].getProcess() == null; --i){
            globalFrames[i].setProcess(processes[processes.length - 1]);
        }
    }

    public Process[] getActiveProcesses() {
        ArrayList<Process> activeProcesses = new ArrayList<Process>();
        for(Process process : processes){
            if(!process.isSuspended()){
                activeProcesses.add(process);
            }
        }

        return activeProcesses.toArray(new Process[0]);
    }

    public Process[] getSuspendedProcesses() {
        ArrayList<Process> suspendedProcesses = new ArrayList<Process>();
        for(Process process : processes){
            if(process.isSuspended()){
                suspendedProcesses.add(process);
            }
        }

        return suspendedProcesses.toArray(new Process[0]);
    }

    public String getStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("--------------- ").append(getName()).append(" ---------------").append("\n");
        int totalPageFaultCount = 0;
        int totalThrashingCount = 0;
        int totalNumberOfSuspensions = 0;
        int totalNumberOfFramesTaken = 0;
        int totalNumberOfFramesReceived = 0;
        for(Process process : processes){
            totalPageFaultCount += process.getTotalPageFaultCount();
            totalThrashingCount += process.getTotalThrashingCount();
            totalNumberOfSuspensions += process.getNumberOfSuspensions();
            totalNumberOfFramesTaken += process.getNumberOfFramesTaken();
            totalNumberOfFramesReceived += process.getNumberOfFramesReceived();
            sb.append(process.colored());
            sb.append(process.getStatistics(true));
            sb.append("\n");
        }
        sb.append("Total page fault count: ").append(ANSI_YELLOW).append(totalPageFaultCount).append(ANSI_RESET).append("\n");
        sb.append("Total thrashing count: ").append(ANSI_YELLOW).append(totalThrashingCount).append(ANSI_RESET).append("\n");
        if(isDynamic()){
            sb.append("Total number of suspensions: ").append(ANSI_YELLOW).append(totalNumberOfSuspensions).append(ANSI_RESET).append("\n");
//            sb.append("Total number of frames taken: ").append(ANSI_YELLOW).append(totalNumberOfFramesTaken).append(ANSI_RESET).append("\n");
//            sb.append("Total number of frames received: ").append(ANSI_YELLOW).append(totalNumberOfFramesReceived).append(ANSI_RESET).append("\n");
            if(totalNumberOfFramesTaken != totalNumberOfFramesReceived){
                throw new RuntimeException("Total number of frames taken: " + totalNumberOfFramesTaken + "\nTotal number of frames received: " + totalNumberOfFramesReceived + "\nShould be equal");
            }
            sb.append("Total number of frames transmitted: ").append(ANSI_YELLOW).append(totalNumberOfFramesTaken).append(ANSI_RESET).append("\n");
        }
        return sb.toString();
    }
}
