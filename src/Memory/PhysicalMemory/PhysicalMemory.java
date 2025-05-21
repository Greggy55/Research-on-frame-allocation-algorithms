package Memory.PhysicalMemory;

import Memory.VirtualMemory.Page;

public class PhysicalMemory {
    // size
    private final int numberOfFrames;

    private final Frame[] frameArray;

    public PhysicalMemory(int numberOfFrames) {
        this.numberOfFrames = numberOfFrames;
        frameArray = new Frame[numberOfFrames];
        for(int i = 0; i < numberOfFrames; i++){
            frameArray[i] = new Frame();
        }
    }

    public int indexOfPage(Page page){
        for(int i = 0; i < numberOfFrames; i++){
            if(frameArray[i].containsPage()
                    && page.sameIdAs(frameArray[i].getPage())){
                return i;
            }
        }
        return -1;
    }

    public void clear(){
        for(int i = 0; i < numberOfFrames; i++){
            frameArray[i].clear();
        }
    }

    public boolean isFull(){
        for(int i = 0; i < numberOfFrames; i++){
            if(!frameArray[i].containsPage()){
                return false;
            }
        }
        return true;
    }

    public int findEmptyFrame(){
        for(int i = 0; i < numberOfFrames; i++){
            if(!frameArray[i].containsPage()){
                return i;
            }
        }
        return -1;
    }

    public int size() {
        return numberOfFrames;
    }

    public Frame[] getFrameArray() {
        return frameArray;
    }

    public Frame getFrame(int index) {
        return frameArray[index];
    }

    public boolean containsPage(Page page) {
        return getFrame(page) != null;
    }

    public Frame getFrame(Page page) {
        for(int i = 0; i < numberOfFrames; i++){
            if(frameArray[i].containsPage()){
                if(page.sameIdAs(frameArray[i].getPage())){
                    return frameArray[i];
                }
            }
        }
        return null;
    }

    public void set(int index, Page page){
        frameArray[index].setPage(page);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PhysicalMemory:");
        int i = 28;
        for(Frame frame : frameArray){
            frame.setColorCode(i++);
            builder.append(" ").append(frame);
        }
        return builder.toString();
    }
}
