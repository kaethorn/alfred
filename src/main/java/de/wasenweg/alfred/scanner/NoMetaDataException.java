package de.wasenweg.alfred.scanner;

public class NoMetaDataException extends Exception {

  private static final long serialVersionUID = 8750400589574572222L;

  public NoMetaDataException(final Throwable err) {
    super("No meta data file 'ComicInfo.xml' found.", err);
  }

  public NoMetaDataException() {
    super("No meta data file 'ComicInfo.xml' found.");
  }
}