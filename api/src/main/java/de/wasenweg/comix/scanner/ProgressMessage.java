package de.wasenweg.comix.scanner;

public class ProgressMessage {

    private String file;
    private int total;
    private Boolean done;

	public ProgressMessage() {
    	this.done = false;
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

    public Boolean getDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}
}