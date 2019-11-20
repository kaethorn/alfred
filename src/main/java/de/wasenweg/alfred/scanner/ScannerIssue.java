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

  public enum Type {
    INFO,
    WARNING,
    ERROR
  }

  private String message;
  private Type type;
  private String path;

  @Builder.Default
  private Date date = new Date();
}
