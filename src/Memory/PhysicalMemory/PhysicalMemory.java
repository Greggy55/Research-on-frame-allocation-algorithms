package Memory.PhysicalMemory;

import Memory.VirtualMemory.Page;

public class PhysicalMemory {
    // size
    private int numberOfFrames;

    private Frame[] frameArray;

    public PhysicalMemory(int numberOfFrames) {
        createFrameArray(numberOfFrames);
    }

    private void createFrameArray(int numberOfFrames) {
        this.numberOfFrames = numberOfFrames;
        frameArray = new Frame[numberOfFrames];
        for(int i = 0; i < numberOfFrames; i++){
            frameArray[i] = new Frame();
        }
    }

    public void updateNumberOfFrames(int numberOfFrames) {
        createFrameArray(numberOfFrames);
    }

    public int indexOfPage(Page page){
        if(numberOfFrames != frameArray.length){
            throw new IllegalStateException("Number of frames is not equal to the number of frames");
        }
        for(int i = 0; i < numberOfFrames; i++){
            if(frameArray[i].containsPage()
                    && page.sameIdAs(frameArray[i].getPage())){
                return i;
            }
        }
        return -1;
    }

    public void clear(){
        if(numberOfFrames != frameArray.length){
            throw new IllegalStateException("Number of frames is not equal to the number of frames");
        }
        for(int i = 0; i < numberOfFrames; i++){
            frameArray[i].clear();
        }
    }

    public boolean isFull(){
        if(numberOfFrames != frameArray.length){
            throw new IllegalStateException("Number of frames is not equal to the number of frames");
        }
        for(int i = 0; i < numberOfFrames; i++){
            if(!frameArray[i].containsPage()){
                return false;
            }
        }
        return true;
    }

    public int findEmptyFrame(){
        if(numberOfFrames != frameArray.length){
            throw new IllegalStateException("Number of frames is not equal to the number of frames");
        }
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
        if(numberOfFrames != frameArray.length){
            throw new IllegalStateException("Number of frames is not equal to the number of frames");
        }
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
        //int i = 28;
        for(Frame frame : frameArray){
            //frame.setColorCode(i++);
            builder.append(" ").append(frame);
        }
        return builder.toString();
    }

    public Frame removeLastFrame() {
        numberOfFrames--;
        Frame lastFrame = frameArray[numberOfFrames];
        Frame[] newFrameArray = new Frame[numberOfFrames];

        System.arraycopy(frameArray, 0, newFrameArray, 0, numberOfFrames);
        frameArray = newFrameArray;

        return lastFrame;
    }

    public void addFrame(Frame frame) {
        Frame[] newFrameArray = new Frame[numberOfFrames + 1];

        if (numberOfFrames >= 0) {
            System.arraycopy(frameArray, 0, newFrameArray, 0, numberOfFrames);
        }
        newFrameArray[numberOfFrames] = frame;
        numberOfFrames++;

        frameArray = newFrameArray;
    }
}
