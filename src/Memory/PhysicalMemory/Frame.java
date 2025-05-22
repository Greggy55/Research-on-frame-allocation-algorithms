package Memory.PhysicalMemory;

import Memory.VirtualMemory.Page;
import Process.Process;

public class Frame {
    public static boolean COLOR = false;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[38;5;228m";

    // DO WYCZYSZCZENIA W CLEAR
    private int colorCode;
    private Process process;
    private Page page;
    private boolean containsPage;

    public Frame(){
        page = null;
        containsPage = false;
        colorCode = 0;
        process = null;
    }
    public Frame(Page page){
        this.page = page;
        containsPage = true;
        colorCode = 0;
        process = null;
    }

    public Page getPage() {
        return page;
    }

    public void clear(){
        page = null;
        containsPage = false;
        colorCode = 0;
        process = null;
    }

    public void setPage(Page page) {
        if(page != null){
            this.page = page;
            containsPage = true;
        }
        else{
            clear();
        }
    }

    public boolean containsPage() {
        return containsPage;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
        this.colorCode = process.getColorCode();
    }

    @Override
    public String toString() {
        return ansi256(colorCode) + "[" + (!containsPage ? " " : page.idToString()) + "]" + ANSI_RESET;
    }

    public int getColorCode() {
        return colorCode;
    }

//    public void setColorCode(int colorCode) {
//        this.colorCode = colorCode;
//    }

    public static String ansi256(int code) {
        return COLOR ? "\u001B[38;5;" + code + "m" : ANSI_YELLOW;
    }
}
