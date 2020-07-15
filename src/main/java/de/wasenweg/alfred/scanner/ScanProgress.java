package de.wasenweg.alfred.scanner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class ScanProgress {

  public enum Status {
    START,
    CURRENT_FILE,
    TOTAL,
    CLEAN_UP,
    ASSOCIATION,
    DONE,
    SCAN_ISSUE
  }

  @Id
  private String id;

  private Status status;

  private int count;

  private Date started;

  private Date finished;
}
