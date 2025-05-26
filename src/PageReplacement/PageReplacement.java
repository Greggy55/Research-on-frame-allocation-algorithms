package PageReplacement;

import FrameAllocation.PFFControl;
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

    public static final int DELTA_T = 32;
    public static final int CHECK = DELTA_T / 4;
    public static final int UPPER_TRASHING_LIMIT = PFFControl.UPPER_PFF_LIMIT;

    public static boolean PRINT_SHORT = false;

    private final PageFaultDetector pageFaultDetector = new PageFaultDetector(DELTA_T);
    
    private int totalPageFaultCount = 0;
    private int totalThrashingCount = 0;

    protected boolean print;
    protected boolean printDetails;

    protected Page[] referenceString;
    protected PhysicalMemory memory;

    protected Page currentPage;

    protected HashMap<Page, Integer> lastReference = new HashMap<>();

    protected int iter = 0;

    public PageReplacement(boolean print, boolean printDetails, PhysicalMemory memory){
        this.print = print;
        this.printDetails = printDetails;
        this.memory = memory;
    }

    public abstract String getName();

    public void updateNumberOfFrames(int numberOfFrames){
        memory.updateNumberOfFrames(numberOfFrames);
    }

    public abstract void replacePage();

    public Page getCurrentPage(){
        return currentPage;
    }

    public void runSingleIteration(Page[] refStr){
        //memory.clear();
        referenceString = refStr;

        if(print && !PRINT_SHORT){
            System.out.printf("%s Run\n", getName());
        }

        if(check()){
            if(pageFaultDetector.getPageFaultFrequency() >= UPPER_TRASHING_LIMIT){
                totalThrashingCount++;
                //System.out.println(ANSI_RED+"TRASHING HAPPENED"+ANSI_RESET);
                //System.out.println(pageFaultDetector.getPageFaultsToString());
            }
        }

        currentPage = referenceString[iter];
        if(this instanceof LRU){
            ((LRU) this).updateLastReference();
        }

        if(print){
            if(!PRINT_SHORT){
                System.out.println();
                System.out.printf("%s Iteration: " + ANSI_YELLOW + iter + ANSI_RESET + "\n", getName());
            }
            System.out.printf("%s " + memory + "\n", getName());
            System.out.printf("%s Reference: " + ANSI_YELLOW + currentPage.toString() + ANSI_RESET + "\n", getName());
        }

        if(pageFault()){
            if(print){
                System.out.printf("%s Page " + ANSI_RED + "fault\n"+ANSI_RESET, getName());
            }
            pageFaultDetector.registerPageFault(true);
            totalPageFaultCount++;

            replacePage();
        }
        else{
            if(print){
                System.out.printf("%s Page " + ANSI_GREEN + "OK\n" + ANSI_RESET, getName());
            }

            pageFaultDetector.registerPageFault(false);
        }

        if(print && !PRINT_SHORT){
            System.out.println();
            System.out.printf("%s End\n", getName());
            System.out.printf("%s " + memory + "\n", getName());

            System.out.println();
            System.out.println("-".repeat(100));
            System.out.println();
        }

        iter++;
    }

    public boolean check() {
        return iter % CHECK == 0 && pageFaultDetector.isReady();
    }

    public void run(Page[] refStr){
        memory.clear();
        referenceString = refStr;

        if(print){
            System.out.printf("%s Run\n", getName());
        }

        for(iter = 0; iter < referenceString.length; iter++){
            currentPage = referenceString[iter];
            if(this instanceof LRU){
                ((LRU) this).updateLastReference();
            }

            if(print){
                System.out.println();
                System.out.printf("%s Iteration: " + ANSI_YELLOW + iter + ANSI_RESET + "\n", getName());
                System.out.printf("%s " + memory + "\n", getName());
                System.out.printf("%s Reference: " + ANSI_YELLOW + currentPage.toString() + ANSI_RESET + "\n", getName());
            }

            if(pageFault()){
                if(print){
                    System.out.printf("%s Page " + ANSI_RED + "fault\n"+ANSI_RESET, getName());
                }
                totalPageFaultCount++;

                replacePage();
            }
            else{
                if(print){
                    System.out.printf("%s Page " + ANSI_GREEN + "OK\n" + ANSI_RESET, getName());
                }
            }

        }

        if(print){
            System.out.println();
            System.out.printf("%s End\n", getName());
            System.out.printf("%s " + memory + "\n", getName());

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

    public boolean pageFault() {
        return memory.indexOfPage(currentPage) == -1;
    }

    public String getStatistics() {
        //final int dashes = 15;
        //System.out.println();
        //System.out.printf("%s %s %s\n", "-".repeat(dashes), getName(), "-".repeat(dashes - getName().length() + ANSI_GRAY.length() + ANSI_RESET.length() + dashes/3));
//        System.out.printf("Page fault count: " + ANSI_YELLOW + "%d\n" + ANSI_RESET, totalPageFaultCount);
//        System.out.printf("Total trashing count: " + ANSI_YELLOW + "%d\n" + ANSI_RESET, totalThrashingCount);

        return "Page fault count: " + ANSI_YELLOW + "%d\n".formatted(totalPageFaultCount) + ANSI_RESET +
                "Total trashing count: " + ANSI_YELLOW + "%d\n".formatted(totalThrashingCount) + ANSI_RESET;
    }

    public String getStatistics(boolean squeeze){
        if(!squeeze){
            return getStatistics();
        }
        return "["+ANSI_YELLOW + totalPageFaultCount + ANSI_RESET + ", " + ANSI_YELLOW + totalThrashingCount + ANSI_RESET + "]";
    }

    public void printReplacementFrame(Frame replacementFrame) {
        if (print) {
            String msg = (replacementFrame != null) ? replacementFrame.toString() : ANSI_YELLOW + "Free frame" + ANSI_RESET;
            System.out.printf("%s Replacement frame: %s\n", getName(), msg);
        }
    }

    public int getPFF() {
        return pageFaultDetector.getPageFaultFrequency();
    }

    public boolean canGetPFF(){
        return pageFaultDetector.isReady();
    }

    public int getIter() {
        return iter;
    }

    public void resetStatistics(){
        totalPageFaultCount = 0;
        totalThrashingCount = 0;
    }

    public int getTotalThrashingCount() {
        return totalThrashingCount;
    }

    public int getTotalPageFaultCount() {
        return totalPageFaultCount;
    }
}
