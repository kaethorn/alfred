package de.wasenweg.comix.scanner;

public class ProgressMessage {

    private String file;

    public ProgressMessage() {
    }

    public ProgressMessage(String file) {
        this.file = file;
    }

    public String getFile() {
        return file;
    }
}