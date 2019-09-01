package de.wasenweg.alfred.scanner;

public class NoVolumeResultException extends Exception {

  private static final long serialVersionUID = -6399925963811538108L;

  public NoVolumeResultException() {
    super("No result in volume search");
  }
}
