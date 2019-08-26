package de.wasenweg.alfred.scanner;

public class NoMatchException extends Exception {

  private static final long serialVersionUID = 277675303691669311L;

  public NoMatchException() {
    super("No matching issue found");
  }
}
