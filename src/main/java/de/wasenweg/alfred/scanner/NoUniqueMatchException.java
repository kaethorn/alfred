package de.wasenweg.alfred.scanner;

public class NoUniqueMatchException extends Exception {

  private static final long serialVersionUID = 3915284974398953043L;

  public NoUniqueMatchException() {
    super("No unique issue found");
  }
}
