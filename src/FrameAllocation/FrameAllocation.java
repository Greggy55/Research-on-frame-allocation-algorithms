package FrameAllocation;

import Memory.PhysicalMemory.PhysicalMemory;
import Memory.VirtualMemory.Page;

import Process.Process;

public abstract class FrameAllocation {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[38;5;228m";
    public static final String ANSI_GREEN = "\u001B[38;5;120m";
    public static final String ANSI_GRAY = "\u001B[38;5;244m";

    protected String name;
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

    public abstract void allocate();

    public void run(){
        memory.clear();

        if(print){
            System.out.printf("%s Run\n", name);
        }

        allocate();

        if(print){
            System.out.println();
            System.out.printf("%s End\n", name);
            System.out.printf("%s " + memory + "\n", name);

            System.out.println();
            System.out.println("-".repeat(100));
            System.out.println();
        }
    }

    public PhysicalMemory getMemory() {
        return memory;
    }
}
