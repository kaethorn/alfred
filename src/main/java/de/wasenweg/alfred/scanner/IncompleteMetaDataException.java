package de.wasenweg.alfred.scanner;

import java.util.List;

public class IncompleteMetaDataException extends RuntimeException {

  private static final long serialVersionUID = 1631924574114202823L;

  public IncompleteMetaDataException(final List<String> missingAttributes) {
    super("Missing meta data: " + String.join(", ", missingAttributes));
  }
}