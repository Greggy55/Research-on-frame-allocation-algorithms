package Simulation;

import FrameAllocation.*;
import Memory.PhysicalMemory.PhysicalMemory;
import Memory.VirtualMemory.Page;
import Process.Process;

import java.util.Arrays;
import java.util.Random;

public class Simulation {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[38;5;228m";

    private final Random rand = new Random();

    private int globalReferenceStringLength = 0;
    private final int totalNumberOfFrames;
    private final int maxReferenceStringLength;
    private final int minNumberOfPages;
    private final int maxNumberOfPages;

    private final int numberOfProcesses;
    private final Process[] processes;

    private Page[] globalReferenceString;

    private FrameAllocation frameAllocation;

    private final int localityLevel = 12;
    private final double localityFactor = 0.8;

    public Simulation(
            int totalNumberOfFrames,
            int maxReferenceStringLength,
            int numberOfProcesses,
            int minNumberOfPages,
            int maxNumberOfPages
    ) {
        this.totalNumberOfFrames = totalNumberOfFrames;
        this.maxReferenceStringLength = Math.max(maxReferenceStringLength, maxNumberOfPages+1);
        this.numberOfProcesses = numberOfProcesses;
        this.minNumberOfPages = minNumberOfPages;
        this.maxNumberOfPages = maxNumberOfPages;

        this.processes = new Process[numberOfProcesses];
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

        PhysicalMemory memory = new PhysicalMemory(totalNumberOfFrames);

        frameAllocation = new Equal(true, true, processes, memory, globalReferenceString);
        frameAllocation.run();
    }

    public void generateProcesses(){
        for(int i = 0; i < numberOfProcesses; i++) {
            int totalNumberOfPages = rand.nextInt(minNumberOfPages, maxNumberOfPages);
            int referenceStringLength = rand.nextInt(totalNumberOfPages+1, maxReferenceStringLength);
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
