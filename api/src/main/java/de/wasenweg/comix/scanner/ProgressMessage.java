package de.wasenweg.comix.scanner;

public class ProgressMessage {

    private String file;
    private int total;

    public ProgressMessage() {
    }

    public String getFile() {
        return file;
    }
    
    public void setFile(String file) {
    	this.file = file;
    }
    
    public int getTotal() {
    	return total;
    }
    
    public void setTotal(int total) {
    	this.total = total;
    }
}