package PageReplacement;

import Memory.PhysicalMemory.Frame;
import Memory.PhysicalMemory.PhysicalMemory;
import Memory.VirtualMemory.Page;

import java.util.HashMap;

public abstract class PageReplacement {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[38;5;228m";
    public static final String ANSI_GREEN = "\u001B[38;5;120m";
    public static final String ANSI_GRAY = "\u001B[38;5;244m";

    protected int pageFaultCount = 0;
    protected int totalThrashingTime = 0;
    protected boolean pageFaultInPreviousPage = false;

    protected String name;
    protected boolean print;
    protected boolean printDetails;

    protected Page[] referenceString;
    protected PhysicalMemory memory;

    protected Page currentPage;

    protected HashMap<Page, Integer> lastReference = new HashMap<>();

    protected int iter;

    public PageReplacement(boolean print, boolean printDetails, PhysicalMemory memory){
        this.print = print;
        this.printDetails = printDetails;
        this.memory = memory;
    }

    public void updateNumberOfFrames(int numberOfFrames){
        memory.updateNumberOfFrames(numberOfFrames);
    }

    public abstract void replacePage();

    public void run(Page[] refStr){
        memory.clear();
        referenceString = refStr;

        if(print){
            System.out.printf("%s Run\n", name);
        }

        for(iter = 0; iter < referenceString.length; iter++){
            currentPage = referenceString[iter];
            if(this instanceof LRU){
                ((LRU) this).updateLastReference();
            }

            if(print){
                System.out.println();
                System.out.printf("%s Iteration: " + ANSI_YELLOW + iter + ANSI_RESET + "\n", name);
                System.out.printf("%s " + memory + "\n", name);
                System.out.printf("%s Reference: " + ANSI_YELLOW + currentPage.idToString() + ANSI_RESET + "\n", name);
            }

            if(pageFault()){
                if(print){
                    System.out.printf("%s Page " + ANSI_RED + "fault\n"+ANSI_RESET, name);
                }
                pageFaultCount++;

                checkIfTrashingHappened();

                replacePage();
            }
            else{
                if(print){
                    System.out.printf("%s Page " + ANSI_GREEN + "OK\n" + ANSI_RESET, name);
                }

                pageFaultInPreviousPage = false;
            }

        }

        if(print){
            System.out.println();
            System.out.printf("%s End\n", name);
            System.out.printf("%s " + memory + "\n", name);

            System.out.println();
            System.out.println("-".repeat(100));
            System.out.println();
        }
    }

    private void refresh(){
        Frame[] frames = memory.getFrameArray();
        for(Frame frame : frames){
            if(frame.containsPage()){
                if(!refStrHasReference(frame.getPage())){
                    frame.clear();
                }
            }
        }
    }

    private boolean refStrHasReference(Page page){
        for(Page refPage : referenceString){
            if(refPage.sameIdAs(page)){
                return true;
            }
        }
        return false;
    }

    private void checkIfTrashingHappened() {
        if(pageFaultInPreviousPage){
            totalThrashingTime++;
        }
        else{
            pageFaultInPreviousPage = true;
        }
    }

    public boolean pageFault() {
        return memory.indexOfPage(currentPage) == -1;
    }

    public void printStatistics() {
        final int dashes = 15;
        System.out.println();
        System.out.printf("%s %s %s\n", "-".repeat(dashes), name, "-".repeat(dashes - name.length() + ANSI_GRAY.length() + ANSI_RESET.length() + dashes/3));
        System.out.printf("Page fault count: " + ANSI_YELLOW + "%d\n" + ANSI_RESET, pageFaultCount);
        System.out.printf("Total trashing time: " + ANSI_YELLOW + "%d\n" + ANSI_RESET, totalThrashingTime);
    }

    public void printReplacementFrame(Frame replacementFrame) {
        if (print) {
            String msg = (replacementFrame != null) ? replacementFrame.toString() : ANSI_YELLOW + "Empty frame" + ANSI_RESET;
            System.out.printf("%s Replacement frame: %s\n", name, msg);
        }
    }
}
