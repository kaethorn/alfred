package de.wasenweg.alfred.scanner;

public class NoImagesException extends Exception {

  private static final long serialVersionUID = 7335176735056733527L;

  public NoImagesException() {
    super("No images found.");
  }
}