package Simulation;

import FrameAllocation.*;
import Memory.PhysicalMemory.PhysicalMemory;
import Memory.VirtualMemory.Page;
import Process.Process;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Simulation {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[38;5;228m";

    private final Random rand = new Random();

    private final boolean printLRU;

    private final boolean printEqual;
    private final boolean printProportional;
    private final boolean printPFFControl;
    private final boolean printWorkingSetModel;

    private int globalReferenceStringLength = 0;
    private final int totalNumberOfFrames;
    private final int minReferenceStringLength;
    private final int maxReferenceStringLength;
    private final int minNumberOfPages;
    private final int maxNumberOfPages;

    private final int numberOfProcesses;
    private final Process[] processes;

    private Page[] globalReferenceString;

    private FrameAllocation frameAllocation;
    private PhysicalMemory memory;

    private final int localityLevel = 12;
    private final double localityFactor = 0.8;

    private final Queue<Page> suspendedPages = new LinkedList<>();

    public Simulation(
            int totalNumberOfFrames,
            int minReferenceStringLength,
            int maxReferenceStringLength,
            int numberOfProcesses,
            int minNumberOfPages,
            int maxNumberOfPages,

            boolean printLRU,

            boolean printEqual,
            boolean printProportional,
            boolean printPFFControl,
            boolean printWorkingSetModel
    ) {
        this.totalNumberOfFrames = totalNumberOfFrames;
        this.minReferenceStringLength = minReferenceStringLength;
        this.maxReferenceStringLength = Math.max(maxReferenceStringLength, maxNumberOfPages+1);
        this.numberOfProcesses = numberOfProcesses;
        this.minNumberOfPages = minNumberOfPages;
        this.maxNumberOfPages = maxNumberOfPages;

        this.printLRU = printLRU;

        this.printEqual = printEqual;
        this.printProportional = printProportional;
        this.printPFFControl = printPFFControl;
        this.printWorkingSetModel = printWorkingSetModel;

        this.processes = new Process[numberOfProcesses];
        this.memory = new PhysicalMemory(totalNumberOfFrames);
    }

    public void start(){
        generateProcesses();
        createGlobalReferenceString();

        for(Process process : processes){
            System.out.println(process);
        }
        System.out.println();
        System.out.println("Global: " + Arrays.toString(globalReferenceString));
        System.out.println();

        // ---------- Equal ----------
        reset(printLRU && printEqual);
        frameAllocation = new Equal(printEqual, printEqual, processes, memory, globalReferenceString);
        frameAllocation.run();
        runLRU(printEqual);

        // ---------- Proportional ----------
        reset(printLRU && printProportional);
        frameAllocation = new Proportional(printProportional, printProportional, processes, memory, globalReferenceString);
        frameAllocation.run();
        runLRU(printProportional);

        // ---------- PFF Control ----------
        reset(printLRU && printPFFControl);
        frameAllocation = new PFFControl(printPFFControl, printPFFControl, processes, memory, globalReferenceString);
        frameAllocation.run();
        runLRU(printPFFControl);

        // ---------- Working Set Model ----------
        reset(printLRU && printWorkingSetModel);
        frameAllocation = new WorkingSetModel(printWorkingSetModel, printWorkingSetModel, processes, memory, globalReferenceString);
        frameAllocation.run();
        runLRU(printWorkingSetModel);
    }

    private void runLRU(boolean printAllocation) {
        for (Page page : globalReferenceString) {
            Process process = page.getProcess();
            if(process.isSuspended()){
                suspendedPages.offer(page);
                continue;
            }

            runLRUForSingleProcess(printAllocation, process);

            while(!process.isSuspended() && !page.equals(process.getCurrentPage())){
                suspendedPages.poll();

                runLRUForSingleProcess(printAllocation, process);
            }
        }
    }

    private void runLRUForSingleProcess(boolean printAllocation, Process process) {
        if(frameAllocation instanceof WorkingSetModel){
            //System.out.println("{");
            //System.out.println(process);
            //System.out.println(suspendedPages);
            //System.out.println("sus: " + process.isSuspended() + "\n}");
        }
        process.runSingleIterationLRU();

        if(frameAllocation.isDynamic()){
            frameAllocation.dynamicAllocate(process);
        }

        if (printAllocation) {
            frameAllocation.printMemory();
        }
    }

    private void reset(boolean printLRU) {
        memory.clear();
        for (Page page : globalReferenceString) {
            Process process = page.getProcess();
            process.resetLRU(printLRU);
        }
    }

    public void generateProcesses(){
        for(int i = 0; i < numberOfProcesses; i++) {
            int totalNumberOfPages = rand.nextInt(minNumberOfPages, maxNumberOfPages);
            int referenceStringLength = rand.nextInt(minReferenceStringLength, maxReferenceStringLength);
            globalReferenceStringLength += referenceStringLength;

            Process process = new Process(
                    1,
                    totalNumberOfPages,
                    referenceStringLength,
                    localityLevel,
                    localityFactor
            );

            process.generateReferenceString();
            processes[i] = process;
        }
    }

    public void createGlobalReferenceString(){
        globalReferenceString = new Page[globalReferenceStringLength];
        for(int i = 0; i < globalReferenceStringLength; i++){
            // wybór procesu (losowanie)
            int processIndex = rand.nextInt(numberOfProcesses);
            Process process = processes[processIndex];
            if(process.isCompletelyInGlobalRefStr()){
                i--;
                continue;
            }

            // wybór kolejnej strony z lokalnego ciągu odwołań
            Page[] referenceString = process.getReferenceString();
            int globalRefStrIndex = process.getAndIncrementGlobalRefStringIndex();

            // przydzielnie wybranej strony do globalnego ciągu odwołań
            if(globalRefStrIndex >= referenceString.length){
                process.setCompletelyInGlobalRefStr(true);
                i--;
                continue;
            }
            globalReferenceString[i] = referenceString[globalRefStrIndex];
        }
    }
}
