package de.wasenweg.alfred.scanner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScannerIssue {

  public enum Severity {
    INFO,
    WARNING,
    ERROR
  }

  public enum Type {
    UNKNOWN,
    NOT_FLAT,
    NO_MONTH,
    NO_YEAR,
    NO_IMAGES,
    INVALID_FILE_FORMAT
  }

  private String message;
  private Severity severity;
  private Type type;

  private boolean fixable;

  @Builder.Default
  private Date date = new Date();
}
