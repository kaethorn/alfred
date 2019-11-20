package de.wasenweg.alfred.scanner;

public class InvalidIssueNumberException extends RuntimeException {

  private static final long serialVersionUID = -1026247018579804966L;

  public InvalidIssueNumberException(final String number) {
    super("Couldn't read number '" + number + "'. Falling back to '0'");
  }
}