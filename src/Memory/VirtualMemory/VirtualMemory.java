package Memory.VirtualMemory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import Process.Process;

public class VirtualMemory {
    public static boolean INT_TYPE = true;
    public static int colorCode = 15;

    private Process process;

    private static int pageId = 1;

    private static final ArrayList<Character> used = new ArrayList<>();
    private static final char emptyChar = ' ';

    private final int totalNumberOfPages;
    private final Page[] pageArray;

    private Page[] referenceString;

    private final Random rnd = new Random();

    public VirtualMemory(int totalNumberOfPages) {
        this.totalNumberOfPages = totalNumberOfPages;
        pageArray = new Page[totalNumberOfPages];
        used.add(emptyChar);

        colorCode += 5;
        if(INT_TYPE){
            generateIntPages();
        }
        else{
            generateCharPages();
        }
    }

    public VirtualMemory(int totalNumberOfPages, Process process) {
        this.totalNumberOfPages = totalNumberOfPages;
        pageArray = new Page[totalNumberOfPages];
        used.add(emptyChar);

        setProcess(process);

        colorCode += 5;
        if(INT_TYPE){
            generateIntPages();
        }
        else{
            generateCharPages();
        }
    }

    public static int getColorCode() {
        return colorCode;
    }

    private void generateIntPages() {
        for(int i = 0; i < totalNumberOfPages; i++){
            pageArray[i] = new Page(i + pageId);
            pageArray[i].setColorCode(colorCode);
            if(process == null){
                throw new RuntimeException("Process is null");
            }
            pageArray[i].setProcess(process);
        }
        pageId += totalNumberOfPages;
    }

    private void generateCharPages() {
        char ch = (char) rnd.nextInt(33, Character.MAX_VALUE);
        for(int i = 0; i < totalNumberOfPages; i++){
            do{
                //ch = (char)(rnd.nextInt(33,127));
                //ch = (char)(rnd.nextInt(Character.MAX_VALUE));
                //ch = (char)(rnd.nextInt(totalNumberOfPages) + '0');
                ch++;
                ch %= Character.MAX_VALUE;
            } while(used.contains(ch) || isInvalid(ch));

            pageArray[i] = new Page(ch);
            pageArray[i].setColorCode(colorCode);
            pageArray[i].setProcess(process);
            used.add(ch);
        }
    }

    private static boolean isInvalid(char ch) {
        return !Character.isDefined(ch) || Character.isISOControl(ch) || Character.isSurrogate(ch) || ch == '⯔';
    }

    public void generateExampleReferenceString() {
        referenceString = new Page[]{new Page('1'), new Page('2'), new Page('3'), new Page('4'), new Page('1'), new Page('2'), new Page('5'), new Page('1'), new Page('2'), new Page('3'), new Page('4'), new Page('5')};
        //referenceString = new Page[]{new Page('1'), new Page('1'), new Page('2'), new Page('2'), new Page('3'), new Page('3'), new Page('4'), new Page('4'), new Page('5'), new Page('5'), new Page('1'), new Page('2')};
    }

    public void generateRandomReferenceString(int stringLength){
        referenceString = new Page[stringLength];

        for(int i = 0; i < stringLength; i++){
            referenceString[i] = pageArray[rnd.nextInt(pageArray.length)];
        }
    }

    public void generateReferenceStringWithLocality(int stringLength, int approxNumberOfLocalities, double localityFactor){
        referenceString = new Page[stringLength];

        int nextLocalitySwitch = stringLength / approxNumberOfLocalities;
        int radius = Math.min(pageArray.length, 2);
        int mid = rnd.nextInt(Math.max(1, pageArray.length - radius));

        int origin = Math.max(0, mid-radius);
        int bound = Math.min(mid+radius+1, pageArray.length);

        for(int i = 0; i < stringLength; i++){
            if(localityFactor > rnd.nextDouble()){
                if(i >= nextLocalitySwitch){
                    mid = rnd.nextInt(Math.max(1, pageArray.length - radius));
                    origin = Math.max(0, mid-radius);
                    bound = mid+radius+1; // +1
                    nextLocalitySwitch += rnd.nextInt(Math.max(1, stringLength / approxNumberOfLocalities));
                }
                referenceString[i] = pageArray[rnd.nextInt(origin, bound)];
            }
            else{
                referenceString[i] = pageArray[rnd.nextInt(pageArray.length)];
            }
        }
    }

    public int size() {
        return totalNumberOfPages;
    }

    public Page[] getPageArray() {
        return pageArray;
    }

    public Page[] getReferenceString() {
        return referenceString;
    }

    public String pagesToString(){
        return Arrays.toString(pageArray);
    }

    public String referenceStringToString(){
        return Arrays.toString(referenceString);
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }
}
