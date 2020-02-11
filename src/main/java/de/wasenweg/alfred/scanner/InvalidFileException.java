package de.wasenweg.alfred.scanner;

public class InvalidFileException extends RuntimeException {

  private static final long serialVersionUID = -1310341819120533363L;

  public InvalidFileException(final Throwable err) {
    super("At least one file in the archive was unreadable.", err);
  }

  public InvalidFileException() {
    super("At least one file in the archive was unreadable.");
  }
}