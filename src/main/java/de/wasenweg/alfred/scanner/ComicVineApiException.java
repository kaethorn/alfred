package de.wasenweg.alfred.scanner;

public class ComicVineApiException extends Exception {

  private static final long serialVersionUID = -7856456187077378359L;

  public ComicVineApiException() {
    super("Error during Comic Vine API meta data retrieval");
  }
}
