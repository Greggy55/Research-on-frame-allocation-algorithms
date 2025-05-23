package PageReplacement;

import java.util.LinkedList;
import java.util.Queue;

public class PageFaultDetector {
    private final int deltaT;
    private Queue<Boolean> pageFaults;

    public PageFaultDetector(int deltaT) {
        this.deltaT = deltaT;
        pageFaults = new LinkedList<>();
    }

    public boolean isReady(){
        return pageFaults.size() == deltaT;
    }

    public void registerPageFault(boolean pageFault){
        if(isReady()){
            pageFaults.poll();
        }
        pageFaults.offer(pageFault);
    }

    public int getPageFaultFrequency(){
        if(!isReady()){
            throw new IllegalStateException("Can't get frequency of not ready PFD");
        }

        int count = 0;
        for(Boolean fault: pageFaults){
            if(fault){
                count++;
            }
        }
        return count;
    }

    public String getPageFaultsToString() {
        return pageFaults.toString();
    }
}
