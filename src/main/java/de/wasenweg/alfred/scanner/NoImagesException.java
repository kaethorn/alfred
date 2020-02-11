package de.wasenweg.alfred.scanner;

public class NoImagesException extends RuntimeException {

  private static final long serialVersionUID = 7335176735056733527L;

  public NoImagesException(final Throwable err) {
    super("No images found.", err);
  }

  public NoImagesException() {
    super("No images found.");
  }
}