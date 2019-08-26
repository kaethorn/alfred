package de.wasenweg.alfred.scanner;

public class EmptyVolumeException extends Exception {

  private static final long serialVersionUID = 959897810299898922L;

  public EmptyVolumeException() {
    super("Empty volume");
  }
}
