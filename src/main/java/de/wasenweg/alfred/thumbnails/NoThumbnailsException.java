package de.wasenweg.alfred.thumbnails;

public class NoThumbnailsException extends RuntimeException {

  private static final long serialVersionUID = 137629900102L;

  public NoThumbnailsException(final Throwable err) {
    super("No thumbnails could be extracted.", err);
  }
}
