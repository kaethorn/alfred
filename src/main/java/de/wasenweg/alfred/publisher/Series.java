package de.wasenweg.alfred.publisher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Series {

  private String id;

  private String series;
  private String publisher;
  private Short volumesCount;
}
