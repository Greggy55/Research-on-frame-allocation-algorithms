package Simulation;

import Memory.VirtualMemory.Page;
import Process.Process;

import java.util.Arrays;
import java.util.Random;

public class Simulation {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[38;5;228m";

    private final Random rand = new Random();

    private final int totalNumberOfFrames;
    private final int totalGlobalReferenceStringLength;
    private final int minNumberOfPages;
    private final int maxNumberOfPages;

    private final int numberOfProcesses;
    private final Process[] processes;

    private Page[] globalReferenceString;

    private final int localityLevel = 12;
    private final double localityFactor = 0.8;

    public Simulation(
            int totalNumberOfFrames,
            int totalGlobalReferenceStringLength,
            int numberOfProcesses,
            int minNumberOfPages,
            int maxNumberOfPages
    ) {
        this.totalNumberOfFrames = totalNumberOfFrames;
        this.totalGlobalReferenceStringLength = totalGlobalReferenceStringLength;
        this.numberOfProcesses = numberOfProcesses;
        this.minNumberOfPages = minNumberOfPages;
        this.maxNumberOfPages = maxNumberOfPages;

        this.processes = new Process[numberOfProcesses];
        this.globalReferenceString = new Page[totalGlobalReferenceStringLength];
    }

    public void start(){
        generateProcesses();
        createGlobalReferenceString();

        for(Process process : processes){
            System.out.println(process);
        }
        System.out.println();
        System.out.println("Global: " + Arrays.toString(globalReferenceString));
    }

    public void generateProcesses(){
        for(int i = 0; i < numberOfProcesses; i++) {
            int totalNumberOfPages = rand.nextInt(minNumberOfPages, maxNumberOfPages);
            int referenceStringLength = totalGlobalReferenceStringLength / numberOfProcesses;

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
        for(int i = 0; i < totalGlobalReferenceStringLength; i++){
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
