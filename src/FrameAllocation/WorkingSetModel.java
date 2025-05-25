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
        //System.out.println("Start dynamic allocate");
        HashMap<Process, Integer> workingSetSize = new HashMap<Process, Integer>();
        int numberOfRequiredFrames = 0;

        //System.out.println("Get WSSs");
        for(Process p : processes){
            int WSS = getWorkingSetSize(p);
            //System.out.println("WSS: " + WSS);
            workingSetSize.put(p, WSS);
            numberOfRequiredFrames += WSS;
        }

        //System.out.println("Number of required frames: " + numberOfRequiredFrames);
        if(numberOfRequiredFrames <= memory.size()){
            //System.out.println("UPDATE NOF");
            for(Process p : processes){
                //System.out.println("Update: " + p.getNumberOfFrames() + " -> " + workingSetSize.get(p));
                allocateWSS(workingSetSize);
                //System.out.println(memory);
            }
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

    public void allocateWSS(HashMap<Process, Integer> workingSetSize){

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
